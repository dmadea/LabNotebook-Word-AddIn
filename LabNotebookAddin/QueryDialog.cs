using com.epam.indigo;

//using System.Windows.Controls;
using ComponentOwl.BetterListView;
using CustomExtensions;
using Lyx;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Imaging;

//using Interop.ChemDraw;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using Word = Microsoft.Office.Interop.Word;

namespace LabNotebookAddin
{
	public partial class QueryDialog : Form
	{
		public static object lockObj = new object();
		public LabNotebook[] loadedLNs;
		public LabNotebook[] matchedLNs;
		//public BackgroundWorker bw;
		public LabNotebook[] visibleLNs;

		private static bool _opened = false;
		private Bitmap bitmap;
		private Graphics grp;
		private bool checkingInTreeView = false;
		private Indigo indigo;
		private MainRibbon mr; //reference to MainRibbon instance
		private IndigoObject queryMolecule = null;
		private IndigoRenderer renderer;
		private Properties.Settings sett = Properties.Settings.Default;
		private IndigoObject substructureQueryMol = null;

		public bool search = false;
		private bool substructureSearch = false;

		public QueryDialog(MainRibbon _mr) // ctor
		{
			_opened = true;

			InitializeComponent();

			this.Text = Assembly.GetExecutingAssembly().GetName().Name + " - Structure Search Dialog";
			this.StartPosition = FormStartPosition.CenterScreen;

			mr = _mr;
			this.ShowIcon = false;

			bLView.ShowGroups = true;

			bLView.Columns.AddRange(
				new[]
					{
						new BetterListViewColumnHeader("Reaction code", 150)
							{
								Style = BetterListViewColumnHeaderStyle.Sortable,
								MinimumWidth = 60
							},
						new BetterListViewColumnHeader("Date", 120)
							{
								Style = BetterListViewColumnHeaderStyle.Sortable,
								MinimumWidth = 60
							},
						new BetterListViewColumnHeader("Reaction scheme", 350)
							{
								Style = BetterListViewColumnHeaderStyle.Nonclickable,
								MinimumWidth = 60
							},
						new BetterListViewColumnHeader("Substructure match", 200)
							{
								Style = BetterListViewColumnHeaderStyle.Sortable,
								MinimumWidth = 60
							}
					});
			bLView.GridLines = BetterListViewGridLines.Grid;
			bLView.MouseClick += BLView_MouseClick;

			tvLns.HideSelection = true;
			tvLns.CheckBoxes = true;

			tvLns.DrawMode = TreeViewDrawMode.OwnerDrawAll;
			tvLns.DrawNode += new DrawTreeNodeEventHandler(this.treeView_DrawNode);

			cbLabNotebookStyle.Items.AddRange(new string[] { "Madea", "Bacon", "Eduardo Palao" });
			cbLabNotebookStyle.DropDownStyle = ComboBoxStyle.DropDownList;
			cbLabNotebookStyle.SelectedIndex = 0;

			lblShowedExps.Text = string.Empty;
			tssLabel.Text = string.Empty;
			tssSpace.Text = string.Empty;

			FormatDocumentDialog.progressBarCtor(pBar);

			tssActiveDoc.Text = $"Active doc.: {mr.doc.Name}";
			mr.ActiveDocumentChanged += (Word.Document doc) =>
			{
				tssActiveDoc.Text = $"Active doc.: {doc.Name}";
			};
		}

		public enum LabNotebookStyle
		{
			Madea, Bacon, Eduardo_Palao
		}
		public static bool IsAlreadyOpened { get { return _opened; } }

		public static Image ResizeImage(Image imgToResize, double scale, int maxWidth)
		{
			maxWidth = (int)(maxWidth * 0.96); //add margins
			if (imgToResize.Width * scale > maxWidth) scale = (double)maxWidth / imgToResize.Width;
			return (new Bitmap(imgToResize, new Size((int)(imgToResize.Width * scale), (int)(imgToResize.Height * scale))));
		}

		public static Image ResizeImage(Image imgToResize, int width, int height, bool keepRatio)
		{
			width = (int)(width * 0.96); //add margins
			height = (int)(height * 0.96); //add margins
			if (keepRatio)
			{
				double ratioX = (double)width / (double)imgToResize.Width;
				double ratioY = (double)height / (double)imgToResize.Height;

				double ratio = ratioX < ratioY ? ratioX : ratioY;

				height = (int)(imgToResize.Height * ratio);
				width = (int)(imgToResize.Width * ratio);
			}

			return (new Bitmap(imgToResize, new Size(width, height)));
		}

		public LabNotebook getDocLabNotebook(Word.Document doc, LabNotebookStyle style)
		{
			mr.optimizePerformance(false);
			//changeProgress(0);
			int tcount = doc.Tables.Count;

			if (tcount < 1) return null; //no table in document

			Indigo ind = new Indigo();

			int procedureRow, procedureCol, litRow, litCol, dateRow, dateCol, rCodeRow, rCodeCol, rSchemeRow, rSchemeCol;

			getStyleIndexes(style, out procedureRow, out procedureCol, out litRow, out litCol, out dateRow, out dateCol,
				out rCodeRow, out rCodeCol, out rSchemeRow, out rSchemeCol);

			LabNotebook ln = new LabNotebook();

			LogListener.ResetTrace();

			ln.Name = Path.GetFileNameWithoutExtension(doc.Name);
			ln.FullPath = doc.FullName;

			for (int i = 1; i <= tcount; i++) // for each table in document
			{
				try
				{
					Experiment exp = new Experiment();

					Application.DoEvents();

					if (style == LabNotebookStyle.Eduardo_Palao)
					{
						string firstPart = doc.Tables[i].Cell(rCodeRow, rCodeCol).Range.Text.TrimWord();
						string secondPart = doc.Tables[i].Cell(rCodeRow, rCodeCol + 1).Range.Text.TrimWord();
						exp.ExpCode = firstPart + secondPart;
					}
					else
					{
						exp.ExpCode = doc.Tables[i].Cell(rCodeRow, rCodeCol).Range.Text.TrimWord();
					}

					if (doc.Tables[i].Cell(rSchemeRow, rSchemeCol).Range.InlineShapes.Count < 1)
					{
						LogListener.WriteLine($"Exp. code {exp.ExpCode}: No reaction scheme.");
						continue;
					}

					var shape = doc.Tables[i].Cell(rSchemeRow, rSchemeCol).Range.InlineShapes[1];

					shape.Select();
					doc.Application.Selection.Copy(); //copy scheme to clipboard

					int pageNumber = shape.Range.Information[Word.WdInformation.wdActiveEndPageNumber];
					exp.PageNumberInWordNotebook = pageNumber;

					Word.OLEFormat ole = shape.OLEFormat;
					ole.DoVerb(Word.WdOLEVerb.wdOLEVerbHide); //open chemdraw
					dynamic chemDrawObj = ole.Object;

					string expDate = doc.Tables[i].Cell(dateRow, dateCol).Range.Text.TrimWord();
					DateTime date;
					if (DateTime.TryParse(expDate, out date)) { exp.Date = date; }
					else LogListener.WriteLine($"Date {expDate} could not be parsed.");

					exp.Literature = doc.Tables[i].Cell(litRow, litCol).Range.Text.TrimWord();
					exp.Procedure = doc.Tables[i].Cell(procedureRow, procedureCol).Range.Text.TrimWord();

					// extract reaction scheme from clipboard first as enhanced metafile and then convert it to bitmap
					Metafile emf = getMetafileFromClipboard(MainRibbon.maxTries, MainRibbon.waitTime);

					if (emf != null)
					{
						exp.ReactionScheme = new CompressibleImage(new Bitmap(emf));
						exp.ReactionScheme.CompressImage();
						emf.Dispose();
					}
					else { LogListener.WriteLine("Error formatting reaction scheme to Enhanced Metafile."); }

					foreach (IndigoObject molecule in MainRibbon.IterateMolecules(chemDrawObj, ind))
					{
						string smiles = molecule.smiles();

						bool @continue = false;
						foreach (string mol in exp.ListOfMolecules) // remove duplicate molecules
						{
							if (smiles == mol) @continue = true;
						}

						if (@continue) continue;

						exp.ListOfMolecules.Add(smiles);
					}

					chemDrawObj.Close();

					ln.AddExperiment(exp);

					if (exp.MolCount < 1) LogListener.WriteLine($"Exp. code {exp.ExpCode}: Error loading molecules in reaction scheme, no molecules loaded.");

					//Debug.WriteLine(i * 100 / tcount);
					changeProgress(i * 100 / tcount);
				}
				catch (COMException ex)
				{
					if (ex.ErrorCode == -2147417846)
					{
						Application.DoEvents();
						int time = 600;
						LogListener.WriteLine("COM Exception: " + ex.Message);
						LogListener.WriteLine($"Waiting for Word to properly load for {time} ms.");
						Thread.Sleep(time); // wait until word loads the document
						i--;
						continue; // repeat the failed experiment
					}
					else throw;
				}
				catch (Exception ex)
				{
					Debug.WriteLine(ex.Message);
					LogListener.WriteLine("Error: " + ex.Message);
					continue;
				}
			}

			ln.Builded = DateTime.Today;

			//changeProgress(0);
			//mr.optimizePerformance(true);

			ind.Dispose();

			LogListener.WriteLine();
			LogListener.WriteLine(ln.Name);
			foreach (Experiment exp in ln.Experiments)
			{
				LogListener.WriteLine($"    Exp. code {exp.ExpCode}, Molecules {exp.MolCount}:");
				foreach (string mol in exp.ListOfMolecules)
				{
					LogListener.WriteLine($"    {mol}");
				}
				LogListener.WriteLine();
			}

			ln.Log = LogListener.Trace.ToString();

			if (ln.Count < 1) return null;
			return ln;
		}

		public BetterListViewItem getItemByKey(string key)
		{
			foreach (var item in bLView.Items)
			{
				if (item.Key.ToString() == key) return item;
			}
			return null;
		}

		public void changeProgress(int value) // from 0 to 100
		{
			if (pBar.InvokeRequired)
			{
				pBar.Invoke(new Action(() => { pBar.Maximum = (int)(FormatDocumentDialog.constValue / (value / 100.0 + 0.0001)); }));
			}
			else
				pBar.Maximum = (int)(FormatDocumentDialog.constValue / (value / 100.0 + 0.0001));
		}

		public static int getDigits(string code)
		{
			StringBuilder digits = new StringBuilder("");

			foreach (char c in code)
			{
				if (char.IsDigit(c)) digits.Append(c);
			}
			if (digits.Length < 1) return -1;

			return int.Parse(digits.ToString());
		}

		public void populateLstv(LabNotebook[] lns)
		{
			//bLView.Items.Clear();

			if (lns == null) return;

			int maxHeight = (int)nudMaxHeight.Value;
			Font boldFont = new Font(bLView.Font.Name, 10f, FontStyle.Bold);

			if (visibleLNs == null) visibleLNs = new LabNotebook[loadedLNs.Length];

			bLView.BeginUpdate();
			int lnIdx = -1;
			foreach (LabNotebook ln in lns) //for each lab notebook
			{
				lnIdx++;
				if (ln == null) continue;
				if (visibleLNs[lnIdx] == lns[lnIdx]) continue;
				visibleLNs[lnIdx] = lns[lnIdx];
				int count = ln.Count;
				if (count < 1) continue;

				string s = (count == 1) ? string.Empty : "s";

				//TODO add user

				BetterListViewGroup blvg = new BetterListViewGroup($"{ln.Name} - {count} record{s}, build time: {ln.Builded.ToShortDateString()}")
				{
					HeaderAlignmentHorizontal = TextAlignmentHorizontal.Left,
					Font = boldFont,
					ForeColor = System.Drawing.Color.DarkBlue
				};

				bLView.Groups.Add(blvg);

				Task loadItems = new Task(new Action(() =>
				{
					BetterListViewItem[] items = new BetterListViewItem[count];

					for (int i = 0; i < count; i++) //for each experiment in lab notebook
					{
						items[i] = new BetterListViewItem(blvg);

						string expCode = ln.Experiments[i].ExpCode;
						items[i].Tag = ln.Experiments[i];
						//items[i].Key = getDigits(expCode);
						//reaction code subitem
						items[i].SubItems[0] = new BetterListViewSubItem() { Text = expCode, Key = expCode };
						//date subitem
						items[i].SubItems.Add(new BetterListViewSubItem()
						{
							Key = ln.Experiments[i].Date,
							Text = ln.Experiments[i].Date.ToShortDateString(),
							AlignHorizontal = TextAlignmentHorizontal.Center
						});

						//reaction scheme and substructure match subite
						items[i].SubItems.Add(new BetterListViewSubItem() { AlignHorizontalImage = BetterListViewImageAlignmentHorizontal.OverlayCenter });
						items[i].SubItems.Add(new BetterListViewSubItem() { AlignHorizontalImage = BetterListViewImageAlignmentHorizontal.OverlayCenter });

						if (ln.Experiments[i].ReactionScheme != null)
						{
							CompressibleImage cImage = ln.Experiments[i].ReactionScheme; // reference to image in memory
							items[i].SubItems[2].Tag = cImage; // save compressed image in Tag
							items[i].SubItems[2].Image = ResizeImage(cImage.GetDecompressedImage(), bLView.Columns[2].Width, (int) maxHeight, true );

							cImage.CompressImage(); // compress original image and keep only resized image in memory

							if (ln.Experiments[i].SubstructureMatch != null)
							{
								IndigoObject mol = ln.Experiments[i].SubstructureMatch;

								items[i].SubItems[3].Tag = mol;
								int height = items[i].SubItems[2].Image.Height;
								if (height < 20) height = 20;
								setRenderOptions(height);

								items[i].SubItems[3].Key = mol.canonicalSmiles(); // to be able to sort substructure matches
								items[i].SubItems[3].Image = renderer.renderToBitmap(mol);
							}
						}

						items[i].Key = lnIdx.ToString() + i.ToString();
					}

					bLView.Items.AddRange(items);
					//bLView.EndUpdate();
				}));

				loadItems.RunSynchronously();
			}
			bLView.EndUpdate();

			lblShowedExps.Text = bLView.Items.Count.ToString();
		}

		public void populateTreeView(LabNotebook[] lns)
		{
			tvLns.Nodes.Clear();
			bLView.Items.Clear();

			if (lns == null) return;
			TreeNode all = tvLns.Nodes.Add("All notebooks");

			tvLns.BeginUpdate();
			int countTotal = 0;
			for (int i = 0; i < lns.Length; i++)
			{
				if (lns[i] == null) continue;
				int count = lns[i].Count;
				if (count < 1) continue;

				countTotal += count;

				TreeNode lnNode = all.Nodes.Add(lns[i].Name);
				lnNode.Tag = i; //save notebook index

				TreeNode[] childs = new TreeNode[count];
				for (int j = 0; j < count; j++) //for each experiment in lab notebook
				{
					string expCode = lns[i].Experiments[j].ExpCode;
					expCode = (string.IsNullOrEmpty(expCode)) ? "[no code]" : expCode;
					childs[j] = new TreeNode(expCode);
					childs[j].Tag = j; //save exp. index
				}

				lnNode.Nodes.AddRange(childs);
			}

			lblExpsFound.Text = (search) ? countTotal.ToString() : string.Empty;

			tvLns.EndUpdate();
			tvLns.Nodes[0].Expand();
		}

		//second level
		private static IEnumerable<TreeNode> IterateCheckedNodes(TreeView tv)
		{
			if (tv.Nodes.Count < 1) yield break;
			foreach (TreeNode node in tv.Nodes[0].Nodes)
			{
				if (node.Checked) yield return node;
			}
		}

		private void bLView_ColumnWidthChanged(object sender, BetterListViewColumnWidthChangedEventArgs eventArgs)
		{
			if (eventArgs.ColumnHeader.Index < 2) return; //deal only with reaction scheme (2) and substructure match (3) columns
			if (bLView.Items.Count < 1) return;

			int maxHeight = (int)nudMaxHeight.Value;
			int reactionSchemeColWidth = bLView.Columns[2].Width;
			int subMatchColWidth = bLView.Columns[3].Width;

			int topIdx = bLView.TopItemIndex;
			int bottomIdx = bLView.BottomItemIndex;

			if (topIdx < 0 || bottomIdx < 0) return;

			bLView.BeginUpdate();
			for (int i = topIdx; i <= bottomIdx; i++)
			{
				CompressibleImage tempImg = bLView.Items[i].SubItems[2].Tag as CompressibleImage;
				if (tempImg != null)
				{
					bLView.Items[i].SubItems[2].Image = ResizeImage(tempImg.GetDecompressedImage(), reactionSchemeColWidth, maxHeight, true );
					//tempImg.CompressImage();
				}

				IndigoObject tempMol = bLView.Items[i].SubItems[3].Tag as IndigoObject;
				if (tempMol != null)
				{
					int height = bLView.Items[i].SubItems[2].Image.Height;
					if (height < 20) height = 20;
					setRenderOptions(height);
					bLView.Items[i].SubItems[3].Image = renderer.renderToBitmap(tempMol);
				}
			}
			bLView.EndUpdate();

			Task resizeImages = new Task(new Action(() =>
			{
				for (int i = 0; i < bLView.Items.Count; i++)
				{
					CompressibleImage tempImg = bLView.Items[i].SubItems[2].Tag as CompressibleImage;
					if (tempImg != null)
					{
						if (i >= topIdx && i <= bottomIdx)
						{
							tempImg.CompressImage();
						}
						else
						{
							bLView.Items[i].SubItems[2].Image = ResizeImage(tempImg.GetDecompressedImage(), reactionSchemeColWidth, (int)maxHeight, true);
							tempImg.CompressImage();

							IndigoObject tempMol = bLView.Items[i].SubItems[3].Tag as IndigoObject;
							if (tempMol != null)
							{
								int height = bLView.Items[i].SubItems[2].Image.Height;
								if (height < 20) height = 20;
								setRenderOptions(height);
								bLView.Items[i].SubItems[3].Image = renderer.renderToBitmap(tempMol);
							}
						}
					}

					//bLView.Invalidate();
					Application.DoEvents();
					bLView.Invoke(new Action(() => { bLView.Update(); }));
					//Thread.Sleep(20);
				}
			}));

			resizeImages.Start();
		}

		//when double clicked on item
		private void bLView_ItemActivate(object sender, BetterListViewItemActivateEventArgs eventArgs)
		{
			//if (eventArgs.ActivationSource != BetterListViewItemActivationSource.Mouse) return;

			openfInfoDialog(eventArgs.Item);
		}

		private void BLView_MouseClick(object sender, MouseEventArgs e)
		{
			if (bLView.Items.Count < 1) return;
			if (e.Button == MouseButtons.Right)
			{
				//handle some shits
				BetterListViewItem item = bLView.FocusedItem;
				if (item == null) return;

				if (!item.GetBounds(BetterListViewItemBoundsPortion.Entire).Contains(e.Location)) return;

				//show menu strip
				cmsBLView.Show(bLView, e.Location);

				experimentInfoToolStripMenuItem.Click += (send, args) =>
				{
					openfInfoDialog(item);
				};

				openExperimentInWordToolStripMenuItem.Click += (send, args) =>
				{
					OpenExperimentInWord(item, mr.app);
				};

				//Debug.WriteLine(item.ToString());
			}
		}

		private void bLView_VScrollPropertiesChanged(object sender, BetterListViewScrollPropertiesChangedEventArgs eventArgs)
		{
			//Debug.WriteLine($"Scroll bar value: {eventArgs.ScrollProperties.Value}, top item index {bLView.TopItemIndex}," +
			//	$" bottom item index {bLView.BottomItemIndex}");

			//int number = 10;
			//int topIdx = bLView.TopItemIndex - number;
			//int bottomIdx = bLView.BottomItemIndex + number;
			//topIdx = (topIdx < 0) ? 0 : topIdx;
			//bottomIdx = (bottomIdx >= bLView.Items.Count) ? bLView.Items.Count - 1 : bottomIdx;
		}

		private void btnDrawMolecule_Click(object sender, EventArgs e)
		{
			Type t = Type.GetTypeFromProgID("ChemDraw.Document.6.0");

			dynamic obj = Activator.CreateInstance(t);

			obj.Activate();
		}

		private void btnCheck_Click(object sender, EventArgs e)
		{
			if (loadedLNs == null) loadAvailableNotebooks();
			if (loadedLNs == null) return;

			string result = checkNotebooks(loadedLNs);

			string path = Path.Combine(sett.WorkingDir, "checkResult.txt");

			File.WriteAllText(path, result);
		}

		private void btnLoadQueryMol_Click(object sender, EventArgs e)
		{
			try
			{
				string smiles = Clipboard.GetText();

				substructureQueryMol = indigo.loadQueryMolecule(smiles);
				substructureSearch = true;

				rerenderMolecule();
				picBoxMoleculeView.Invalidate();
			}
			catch (Exception)
			{

				//throw;
			}

		}

		private void btnLoadMolecule_Click(object sender, EventArgs e)
		{
			substructureSearch = false;
			try
			{
				if (Clipboard.ContainsData("SMILES"))
				{
					MemoryStream data = (MemoryStream)Clipboard.GetData("SMILES");
					byte[] bytes = data.ToArray();
					string smiles = Encoding.UTF8.GetString(bytes).Substring(0, bytes.Length - 1);

					queryMolecule = indigo.loadMolecule(smiles);
				}
				else if (Clipboard.ContainsText(TextDataFormat.Text))
				{
					queryMolecule = indigo.loadMolecule(Clipboard.GetText(TextDataFormat.Text));
				}
				else if (Clipboard.ContainsData("Native") && Clipboard.ContainsData("Embedded Object")) // probably copied directly from office
				{
					Type t = Type.GetTypeFromProgID("ChemDraw.Document.6.0");
					dynamic cdDoc = Activator.CreateInstance(t);

					cdDoc.Application.Visible = false;
					cdDoc.Paste();
					cdDoc.Objects.Copy(); // recopy again from ChemDraw

					if (Clipboard.ContainsData("SMILES"))
					{
						MemoryStream data = (MemoryStream)Clipboard.GetData("SMILES");
						byte[] bytes = data.ToArray();
						string smiles = Encoding.UTF8.GetString(bytes).Substring(0, bytes.Length - 1);

						queryMolecule = indigo.loadMolecule(smiles);
					}
				}
				else
				{
					return;
				}
			}
			catch (IndigoException ex)
			{
				Debug.WriteLine("IndigoException - loading molecule:\n" + ex.Message);

				////TODO >---- >>>> Load query, smarts

				string smiles = Clipboard.GetText();

				substructureQueryMol = indigo.loadQueryMolecule(smiles);
				substructureSearch = true;

				//return;
			}
			//catch (Exception)
			//{
			//	throw;
			//}


			rerenderMolecule();
			picBoxMoleculeView.Invalidate();
		}

		private void btnLoadNotebooks_Click(object sender, EventArgs e)
		{
			loadAvailableNotebooks();

			search = false;

			populateTreeView(loadedLNs);
			
		}

		private async void btnSaveDocuments_Click(object sender, EventArgs e)
		{
			OpenFileDialog ofd = new OpenFileDialog();

			ofd.Multiselect = true;
			ofd.Title = "Choose laboratory notebooks you want to serialize into ln files";
			ofd.Filter = "Word files (*.doc, *.docx) | *.doc; *.docx|All Files (*.*) | *.*";

			if (ofd.ShowDialog() != DialogResult.OK) return;

			string[] filenames = ofd.FileNames;
			LabNotebookStyle style = getStyle();
			string path = sett.WorkingDir;

			for (int i = 0; i < filenames.Length; i++)
			{
				tssLabel.Text = $"Parsing {Path.GetFileName(filenames[i])}, {i + 1} of {filenames.Length}";

				object filepath = filenames[i];

				object nullobj = System.Reflection.Missing.Value;
				object readOnly = false;

				try
				{
					Word.Document doc = mr.app.Documents.Open(
							  ref filepath, ref nullobj, ref readOnly,
							  ref nullobj, ref nullobj, ref nullobj,
							  ref nullobj, ref nullobj, ref nullobj,
							  ref nullobj, ref nullobj, ref nullobj,
							  ref nullobj, ref nullobj, ref nullobj);

					LabNotebook ln = null;

					await Task.Factory.StartNew(() =>
					{
						mr.optimizePerformance(false);
						changeProgress(0);

						ln = getDocLabNotebook(doc, style);

						doc.Close();

						if (ln != null) LabNotebook.SerializeAndSave(Path.Combine(path, ln.Name + ".ln"), ln);
						File.WriteAllText(Path.Combine(path, ln.Name + ".log"), LogListener.Trace.ToString()); // save log file
						mr.optimizePerformance(true);
						changeProgress(0);
					});
				}
				catch (Exception ex)
				{
					Debug.WriteLine(ex.Message);
					MessageBox.Show(ex.Message, "LabNotebookAddIn Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
					continue;
				}
			}
			tssLabel.Text = "Done";
		}

		private async void btnSerializeDoc_Click(object sender, EventArgs e)
		{
			//string picPath = @"C:\Users\Dominik\Desktop\test";

			LabNotebookStyle style = getStyle();

			LabNotebook ln = null;
			string path = sett.WorkingDir;

			if (mr.doc == null) return;

			try
			{
				tssLabel.Text = $"Parsing {mr.doc.Name}";
			}
			catch (COMException) // not active document
			{
				return;
			}

			await Task.Factory.StartNew(() =>
			{
				mr.optimizePerformance(false);
				changeProgress(0);

				ln = getDocLabNotebook(mr.doc, style);

				if (ln != null) LabNotebook.SerializeAndSave(Path.Combine(path, ln.Name + ".ln"), ln);
				File.WriteAllText(Path.Combine(path, ln.Name + ".log"), LogListener.Trace.ToString()); // save log file
				mr.optimizePerformance(true);
				changeProgress(0);
			});

			tssLabel.Text = "Done";

			//if (ln != null) LabNotebook.SerializeAndSave(Path.Combine(path, ln.Name + ".ln"), ln);
			//File.WriteAllText(Path.Combine(path, ln.Name + ".log"), LogListener.Trace.ToString()); // save log file
			//changeProgress(0);
		}

		private void deleteFilesInDir(string path)
		{
			if (!Directory.Exists(path)) return;
			foreach (string file in Directory.EnumerateFiles(path))
			{
				File.Delete(file);
			}
		}

		private string getFlags()
		{
			if (!cbELE.Checked && !cbFRA.Checked && !cbMAS.Checked && !cbSTE.Checked) return "NONE";
			if (cbELE.Checked && cbFRA.Checked && cbMAS.Checked && cbSTE.Checked) return "ALL";

			string ele = (cbELE.Checked) ? "ELE" : string.Empty;
			string fra = (cbFRA.Checked) ? "FRA" : string.Empty;
			string mas = (cbMAS.Checked) ? "MAS" : string.Empty;
			string ste = (cbSTE.Checked) ? "STE" : string.Empty;

			return $"{ele} {fra} {mas} {ste}";
		}

		private LabNotebookStyle getStyle()
		{
			LabNotebookStyle style = LabNotebookStyle.Madea;

			switch (cbLabNotebookStyle.Text)
			{
				case "Madea":
					style = LabNotebookStyle.Madea;
					break;

				case "Bacon":
					style = LabNotebookStyle.Bacon;
					break;

				case "Eduardo Palao":
					style = LabNotebookStyle.Eduardo_Palao;
					break;
			}

			return style;
		}

		private void loadAvailableNotebooks()
		{
			string path = sett.WorkingDir;

			if (!Directory.Exists(path)) Directory.CreateDirectory(path);

			string[] filePaths = Directory.GetFiles(path, "*.ln"); //available files

			if (filePaths.Length < 1) return;

			loadedLNs = new LabNotebook[filePaths.Length];
			//visibleLNs = new LabNotebook[filePaths.Length];

			for (int i = 0; i < filePaths.Length; i++)
			{
				loadedLNs[i] = LabNotebook.Deserialize(filePaths[i]);
			}

			//Parallel.For(0, filePaths.Length - 1, new Action<int>((int i) =>
			//{
			//	loadedLNs[i] = LabNotebook.Deserialize(filePaths[i]);
			//}));
		}

		private void openfInfoDialog(BetterListViewItem item)
		{
			Experiment exp = item.Tag as Experiment;

			if (exp == null) return;

			if (fInfoDialog.IsAlreadyOpened)
			{
				fInfoDialog openedDialog = (fInfoDialog)Application.OpenForms["fInfoDialog"];
				openedDialog.loadNewData(exp);
				openedDialog.Activate();
				return;
			}

			fInfoDialog expInfoDialog = new fInfoDialog();
			expInfoDialog.StartPosition = FormStartPosition.CenterParent;
			expInfoDialog.loadNewData(exp);
			expInfoDialog.Show();
		}

		private void picBoxMoleculeView_Paint(object sender, PaintEventArgs e)
		{
			e.Graphics.DrawImage(bitmap, 0, 0);
		}

		private void picBoxMoleculeView_Resize(object sender, EventArgs e)
		{
			setGraphics();
			if (queryMolecule != null || substructureQueryMol != null)
				rerenderMolecule();
			picBoxMoleculeView.Invalidate();
		}

		private void QueryDialog_FormClosed(object sender, FormClosedEventArgs e)
		{
			_opened = false;
		}

		private void QueryDialog_Load(object sender, EventArgs e)
		{
			Task initAsync = new Task(new Action(() =>
			{ //takes long time to load indigo, initialize them asynchronously
				indigo = new Indigo();
				renderer = new IndigoRenderer(indigo);

				//docExperiments = new List<Experiment>();

				indigo.setOption("render-output-format", "png");
				indigo.setOption("render-margins", 10, 10);
				indigo.setOption("render-coloring", true);
				indigo.setOption("render-image-width", picBoxMoleculeView.Width);
				indigo.setOption("render-image-height", picBoxMoleculeView.Height);
				indigo.setOption("standardize-stereo", true);
				indigo.setOption("standardize-charges", true);
			}));

			initAsync.Start();

			cbMAS.Checked = true;

			//lView.Columns

			setGraphics();
			picBoxMoleculeView.Invalidate();
		}

		private void rerenderMolecule()
		{
			grp.Clear(this.BackColor);
			//indigo.setOption("render-margins", 10, 10);
			indigo.setOption("render-image-width", picBoxMoleculeView.Width);
			indigo.setOption("render-image-height", picBoxMoleculeView.Height);
			if (substructureSearch) grp.DrawImage(renderer.renderToBitmap(substructureQueryMol), 0, 0);
			else grp.DrawImage(renderer.renderToBitmap(queryMolecule), 0, 0);
		}

		private void setGraphics()
		{
			if (this.WindowState != FormWindowState.Minimized)
			{
				bitmap = new Bitmap(picBoxMoleculeView.Width, picBoxMoleculeView.Height);
				grp = Graphics.FromImage(bitmap);
				grp.Clear(this.BackColor);
				grp.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
			}
		}

		private void setRenderOptions(int height)
		{
			float scaleMargin = 0.1f;
			indigo.setOption("render-margins", 8, 8);
			indigo.setOption("render-coloring", false);
			indigo.setOption("render-image-width", bLView.Columns[3].Width);
			indigo.setOption("render-image-height", height);
		}

		private void treeView_DrawNode(object sender, DrawTreeNodeEventArgs e)
		{
			if (IsThirdLevel(e.Node))
			{
				System.Drawing.Color backColor, foreColor;

				if ((e.State & TreeNodeStates.Selected) == TreeNodeStates.Selected)
				{
					backColor = SystemColors.Highlight;
					foreColor = SystemColors.HighlightText;
				}
				else if ((e.State & TreeNodeStates.Hot) == TreeNodeStates.Hot)
				{
					backColor = SystemColors.HotTrack;
					foreColor = SystemColors.HighlightText;
				}
				else
				{
					backColor = e.Node.BackColor;
					foreColor = e.Node.ForeColor;
				}

				using (SolidBrush brush = new SolidBrush(backColor))
				{
					e.Graphics.FillRectangle(brush, e.Node.Bounds);
				}

				TextRenderer.DrawText(e.Graphics, e.Node.Text, this.tvLns.Font, e.Node.Bounds, foreColor, backColor);

				if ((e.State & TreeNodeStates.Focused) == TreeNodeStates.Focused)
				{
					ControlPaint.DrawFocusRectangle(e.Graphics, e.Node.Bounds, foreColor, backColor);
				}

				e.DrawDefault = false;
			}
			else
			{
				e.DrawDefault = true;
			}
		}

		private void twLns_AfterCheck(object sender, TreeViewEventArgs e)
		{
			if (checkingInTreeView) return;
			string nodeText = e.Node.Text;

			TreeNode node = e.Node;

			LabNotebook[] tempLNs = new LabNotebook[loadedLNs.Length];

			if (IsFirstLevel(node))
			{
				//visibleLNs = new LabNotebook[loadedLNs.Length];
				checkingInTreeView = true;
				foreach (TreeNode child in node.Nodes)
				{
					child.Checked = node.Checked;
				}
				checkingInTreeView = false;

				if (loadedLNs != null && node.Checked) //load all notebooks to listview
					tempLNs = (search) ? matchedLNs : loadedLNs;
			}
			else if (IsSecondLevel(node))
			{
				//visibleLNs = new LabNotebook[loadedLNs.Length];
				foreach (TreeNode checkedNode in IterateCheckedNodes(tvLns))
				{
					int idx = (int)checkedNode.Tag;
					tempLNs[idx] = (search) ? matchedLNs[idx] : loadedLNs[idx];
				}
			}

			populateLstv(tempLNs);
		}

		private void twLns_AfterSelect(object sender, TreeViewEventArgs e)
		{
			//e.Node.Checked = !e.Node.Checked;

			if (IsThirdLevel(e.Node))
			{
				int lnIdx = (int)e.Node.Parent.Tag;
				if (visibleLNs[lnIdx] != null)
				{
					string key = lnIdx.ToString() + e.Node.Tag.ToString();
					var item = getItemByKey(key);
					bLView.EnsureVisible(item);
				}
			}
		}



		#region Search Functions

		private void btnExactMatch_Click(object sender, EventArgs e)
		{
			substructureSearch = false;
			search = true;
			if (queryMolecule == null) return;

			if (loadedLNs == null) loadAvailableNotebooks();
			if (loadedLNs == null) return;

			string flags = getFlags();

			matchedLNs = new LabNotebook[loadedLNs.Length];

			//StringBuilder sb = new StringBuilder();

			for (int i = 0; i < loadedLNs.Length; i++)
			{
				if (loadedLNs[i] == null) continue;
				matchedLNs[i] = loadedLNs[i].getInstanceWithoutExps();

				//sb.AppendLine("\n" + loadedLNs[i].Name);

				foreach (Experiment exp in loadedLNs[i].Experiments)
				{
					//sb.AppendLine("   " + exp.ExpCode + "   Molecules: ");
					foreach (string molSmiles in exp.ListOfMolecules)
					{
						//sb.AppendLine("   " + molSmiles);
						IndigoObject match = null;

						using (IndigoObject molecule = indigo.loadMolecule(molSmiles))
						{
							try
							{
								match = indigo.exactMatch(molecule, queryMolecule, flags);
							}
							catch (IndigoException ex)
							{
								Debug.WriteLine(ex.Message);
								//match = null;
								continue;
							}
						}

						if (match != null)
						{
							matchedLNs[i].AddExperiment(exp);
							break;
						}
					}
					//sb.AppendLine();
				}
			}

			//File.WriteAllText(@"C:\Users\Dominik\Desktop\test\test.txt", sb.ToString());

			populateTreeView(matchedLNs);
		}

		private void btnSubstructureSearch_Click(object sender, EventArgs e)
		{
			substructureSearch = true;
			search = true;

			if (loadedLNs == null) loadAvailableNotebooks();
			if (loadedLNs == null) return;

			if (queryMolecule == null && substructureQueryMol == null) return;

			if (substructureQueryMol == null)
			{
				substructureQueryMol = queryMolecule.clone();
				//IndigoObject unAromatizedQueryMol = indigo.loadQueryMolecule(substructureQueryMol.canonicalSmiles());
				substructureQueryMol.aromatize();

				string smls = substructureQueryMol.smiles();
				substructureQueryMol = indigo.loadQueryMolecule(smls);
			}else
			{
				substructureQueryMol.aromatize();
			}

			rerenderMolecule();
			picBoxMoleculeView.Invalidate();

			matchedLNs = new LabNotebook[loadedLNs.Length];

			for (int i = 0; i < loadedLNs.Length; i++)
			{
				matchedLNs[i] = loadedLNs[i].getInstanceWithoutExps();

				foreach (Experiment exp in loadedLNs[i].Experiments)
				{
					foreach (string moleculeSmiles in exp.ListOfMolecules)
					{
						IndigoObject match = null;

						//string querySmiles = "C1~C~C~C~C~C~1"; // any 6 C ring, substitued cyclohexane, benzene etc.
						//querySmiles = "C1NC=C(C)C=1";

						using (IndigoObject molecule = indigo.loadMolecule(moleculeSmiles))
						{
							try
							{
								molecule.aromatize();
								match = indigo.substructureMatcher(molecule).match(substructureQueryMol);

								//if (match == null)
								//{
								//	match = indigo.substructureMatcher(molecule).match(unAromatizedQueryMol);
								//}
								//if (match == null)
								//{
								//	match = indigo.substructureMatcher(molecule).match(substructureQueryMol);
								//}
								//if (match == null)
								//{
								//	match = indigo.substructureMatcher(molecule).match(unAromatizedQueryMol);
								//}
							}
							catch (IndigoException ex)
							{
								Debug.WriteLine(ex.Message);
								//match = null;
								continue;
							}

							if (match != null)
							{
								matchedLNs[i].AddExperiment(exp);

								IndigoObject matchedMol = match.highlightedTarget();

								matchedMol.dearomatize();
								matchedLNs[i].Experiments[matchedLNs[i].Count - 1].SubstructureMatch = matchedMol;
								break;
							}
						}
					}
				}
			}
			populateTreeView(matchedLNs);
		}

		#endregion Search Functions

		
		#region Utiliy Functions

		public static void getStyleIndexes(LabNotebookStyle style, out int procedureRow, out int procedureCol, out int litRow, out int litCol, out int dateRow,
			out int dateCol, out int rCodeRow, out int rCodeCol, out int rSchemeRow, out int rSchemeCol)
		{
			rCodeRow = 1; rCodeCol = 2;
			dateRow = 1; dateCol = 4;
			rSchemeRow = 2; rSchemeCol = 1;
			litRow = 4; litCol = 2;
			procedureRow = 6; procedureCol = 1;

			switch (style)
			{
				case LabNotebookStyle.Bacon:
					rCodeRow = 1; rCodeCol = 2;
					dateRow = 1; dateCol = 4;
					rSchemeRow = 2; rSchemeCol = 1;
					litRow = 4; litCol = 2;
					procedureRow = 5; procedureCol = 2;
					break;

				case LabNotebookStyle.Madea:
					rCodeRow = 1; rCodeCol = 2;
					dateRow = 1; dateCol = 4;
					rSchemeRow = 2; rSchemeCol = 1;
					litRow = 4; litCol = 2;
					procedureRow = 6; procedureCol = 1;
					break;

				case LabNotebookStyle.Eduardo_Palao:
					rCodeRow = 1; rCodeCol = 2;
					dateRow = 1; dateCol = 6;
					rSchemeRow = 2; rSchemeCol = 1;
					litRow = 4; litCol = 2;
					procedureRow = 5; procedureCol = 2;
					break;
			}
		}

		public static Metafile getMetafileFromClipboard(int maxTries, int waitTime)
		{
			Metafile emf = null;

			for (int i = 0; i < maxTries; i++)
			{
				emf = internalGetMetafileFromClipboard();
				if (emf != null)
				{
					return emf;
				}
				LogListener.WriteLine($"Error loading metafile from clipboard, waiting {waitTime} ms. {i + 1}. try.");
				System.Threading.Thread.Sleep(waitTime);
			}
			return emf;
		}

		public static string checkNotebooks(params LabNotebook[] notebooks)
		{
			if (notebooks == null) return null;
			if (notebooks.Length < 1) return null;

			StringBuilder sb = new StringBuilder();

			foreach (LabNotebook ln in notebooks)
			{
				sb.AppendLine($"\r\n\r\nName of laboratory notebook: {ln.Name}, Full path: {ln.FullPath}, Build time: {ln.Builded.ToShortDateString()}, " +
					$"User: {ln.User}");

				if (ln.Builded == null) sb.AppendLine("Build time is missing.");
				if (string.IsNullOrEmpty(ln.Name)) sb.AppendLine("Name is missing.");
				if (string.IsNullOrEmpty(ln.FullPath)) sb.AppendLine("FullPath is missing.");
				if (string.IsNullOrEmpty(ln.Log)) sb.AppendLine("Log is missing.");
				//if (string.IsNullOrEmpty(ln.User)) sb.AppendLine("User is missing.");

				if (ln.Count < 1)
				{
					sb.AppendLine("No experiment found in this laboratory notebook.");
					continue;
				}

				foreach (Experiment exp in ln.Experiments)
				{
					//if (string.IsNullOrEmpty(exp.Caption)) sb.AppendLine($"Caption in {exp.ExpCode} is missing.");
					if (string.IsNullOrEmpty(exp.ExpCode)) sb.AppendLine($"Experiment code is missing.");
					if (exp.Date == null) sb.AppendLine($"Date in {exp.ExpCode} is missing.");
					//if (string.IsNullOrEmpty(exp.Literature)) sb.AppendLine($"Literature in {exp.ExpCode} is missing.");
					if (string.IsNullOrEmpty(exp.Procedure)) sb.AppendLine($"Procedure of {exp.ExpCode} is missing.");
					//if (string.IsNullOrEmpty(exp.ShortDescripton)) sb.AppendLine($"ShortDescripton of {exp.ExpCode} is missing.");
					if (exp.PageNumberInWordNotebook == -1) sb.AppendLine($"PageNumber of {exp.ExpCode} is missing.");
					if (exp.ReactionScheme == null) sb.AppendLine($"Reaction scheme of {exp.ExpCode} is missing.");
					if (exp.MolCount < 1) sb.AppendLine($"No saved molecules in {exp.ExpCode}.\r\n");
				}
			}
			return sb.ToString();
		}

		public static void OpenExperimentInWord(BetterListViewItem item, Word.Application app)
		{
			Experiment exp = item.Tag as Experiment;
			LabNotebook owner = exp.Owner;

			if (string.IsNullOrEmpty((owner.FullPath))) return;

			object filepath = owner.FullPath;

			object nullobj = System.Reflection.Missing.Value;
			object readOnly = false;

			try
			{
				Word.Document doc = app.Documents.Open(
					  ref filepath, ref nullobj, ref readOnly,
					  ref nullobj, ref nullobj, ref nullobj,
					  ref nullobj, ref nullobj, ref nullobj,
					  ref nullobj, ref nullobj, ref nullobj,
					  ref nullobj, ref nullobj, ref nullobj);

				object what = Word.WdGoToItem.wdGoToPage;
				object which = Word.WdGoToDirection.wdGoToFirst;
				object count = exp.PageNumberInWordNotebook;

				doc.Activate();
				app.Selection.GoTo(ref what, ref which, ref count, ref nullobj);

				app.Activate();
			}
			catch (COMException ex)
			{
				if (ex.HResult == -2146823114) // file not found
					MessageBox.Show(ex.Message, "LabNotebookAddIn Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				else
				{
					throw;
				}
			}
		}

		private static Metafile internalGetMetafileFromClipboard()
		{
			Metafile emf = null;
			if (DllImports.OpenClipboard(IntPtr.Zero))
			{
				if (DllImports.IsClipboardFormatAvailable(DllImports.CF_ENHMETAFILE))
				{
					var ptr = DllImports.GetClipboardData(DllImports.CF_ENHMETAFILE);
					if (!ptr.Equals(IntPtr.Zero))
						emf = new Metafile(ptr, true);
				}
				// You must close ir, or it will be locked
				DllImports.CloseClipboard();
			}
			return emf;
		}

		private static bool IsFirstLevel(TreeNode node)
		{
			return node.Parent == null;
		}

		private static bool IsSecondLevel(TreeNode node)
		{
			return node.Parent != null && node.Parent.Parent == null;
		}

		private static bool IsThirdLevel(TreeNode node)
		{
			return node.Parent != null && node.Parent.Parent != null;
		}
		#endregion Utiliy Functions

		
	}
}