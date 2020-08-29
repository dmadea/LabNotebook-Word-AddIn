/*
* Copyright (c) 2016, Dominik Madea
* All rights reserved.
*
* Permission to use, copy, modify, and/or distribute this software for any
* purpose with or without fee is hereby granted, provided that the above
* copyright notice and this permission notice appear in all copies.
*
* THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
* WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS.IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
* ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
* WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
* ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
* OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

//using AxChemDrawControl12;
using com.epam.indigo;
using FTP;
using Microsoft.Office.Tools.Ribbon;
using nucs.JsonSettings;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Globalization;

//using Interop.ChemDraw;
using System.IO;
using System.Linq;
using System.Net;
using System.Reflection;

//using com.epam.indigo;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Text;
using System.Windows.Forms;
using Word = Microsoft.Office.Interop.Word;

namespace LabNotebookAddin
{
	public partial class MainRibbon
	{
		public const int dESize = 7;

		public const int dSize = 9;

		public const int sSize = 9;

		public const Word.WdLineWidth thickLineWidth = Word.WdLineWidth.wdLineWidth150pt;

		public const Word.WdLineWidth THICKLineWidth = Word.WdLineWidth.wdLineWidth225pt;

		public const int tSize = 9; //number of columns in data table

		public const int waitTime = 50;
		public const int maxTries = 5;

		public Word.Application app = null;

		public Dictionary<int, float> dataTableColWidths;

		//application
		public Word.Document doc = null;

		public string fullNameOfDoc = null;

		public NumberFormatInfo nFormat;

		private bool justSavingInPublish = false;

		public delegate void ActiveDocumentChangedEventHandler(Word.Document doc);

		public event ActiveDocumentChangedEventHandler ActiveDocumentChanged = delegate { };

		//columns of calculating table
		//KeyboardListener kListener;// = new KeyboardListener();
		//active document
		//private Properties.Settings sett;

		public MySettings sett; // declare hardcoded settings

		public enum Variables
		{ w, cm, mr, eq, n, ro, m, V }

		public static string decryptPassword(byte[] entropy, byte[] encryptedData)
		{
			byte[] data = System.Security.Cryptography.ProtectedData.Unprotect(encryptedData, entropy, DataProtectionScope.CurrentUser);

			for (int i = 0; i < data.Length; i++)
			{
				data[i] -= (byte)(entropy[0] * entropy[19]);
				data[i] = (byte)~data[i];
			}

			return Encoding.UTF8.GetString(data);
		}

		public static void encryptPassword(string pwd, out byte[] entropy, out byte[] encryptedData)
		{
			//entropy = encryptedData = null;
			//if (string.IsNullOrEmpty(pwd)) return false;

			// Data to protect. Convert a string to a byte[] using Encoding.UTF8.GetBytes().
			byte[] plaintext = Encoding.UTF8.GetBytes(pwd);

			// Generate additional entropy (will be used as the Initialization vector)
			entropy = new byte[20];
			using (RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider())
			{
				rng.GetBytes(entropy);
			}

			for (int i = 0; i < plaintext.Length; i++)
			{
				plaintext[i] = (byte)~plaintext[i];
				plaintext[i] += (byte)(entropy[0] * entropy[19]);
			}

			encryptedData = System.Security.Cryptography.ProtectedData.Protect(plaintext, entropy, DataProtectionScope.CurrentUser);

			//return true;
		}

		public void optimizePerformance(bool done)
		{
			//app.Options.Pagination = app.ScreenUpdating = done; //pagination doesn't work on win XP
			app.ScreenUpdating = done;
		}

		private static void uploadFtpFile(string hostName, string user, string pwd, string folderName, string filename, string filePath)
		{
			FtpWebRequest request;
			string ftpPath;

			//ftpPath = string.Format(@"ftp://{0}/{1}{2}{3}", hostName, folderName, (string.IsNullOrEmpty(folderName)) ? string.Empty : "/", filename);

			ftpPath = string.Format(@"ftp://{0}{1}{2}", hostName, folderName, filename);

			try
			{
				request = WebRequest.Create(new Uri(ftpPath)) as FtpWebRequest;
				request.Method = WebRequestMethods.Ftp.UploadFile; //STOR
				request.UseBinary = true;
				request.UsePassive = true;
				request.KeepAlive = true;
				request.Credentials = new NetworkCredential(user, pwd);
				//request.ConnectionGroupName = "group";

				using (FileStream fs = File.OpenRead(filePath))
				{
					byte[] buffer = new byte[fs.Length];
					fs.Read(buffer, 0, buffer.Length);
					fs.Close();
					using (Stream requestStream = request.GetRequestStream())
					{
						requestStream.Write(buffer, 0, buffer.Length);
						requestStream.Flush();
						requestStream.Close();
					}
				}
			}
			catch (WebException ex)
			{
				bool catched = false;
				FtpWebResponse response = (FtpWebResponse)ex.Response;
				switch (ex.Status)
				{
					case WebExceptionStatus.ProtocolError: // error in username or password
						switch (response.StatusCode)
						{
							case FtpStatusCode.ActionNotTakenFilenameNotAllowed:
								MessageBox.Show("Directory doesn't exist. Please check the settings.\n" + ex.ToString(), "Protocol error", MessageBoxButtons.OK, MessageBoxIcon.Error);
								catched = true;
								break;

							case FtpStatusCode.NotLoggedIn:
								MessageBox.Show("Access denied, wrong username or/and password.\n" + ex.ToString(), "Protocol error", MessageBoxButtons.OK, MessageBoxIcon.Error);
								catched = true;
								break;
						}
						break;

					case WebExceptionStatus.ConnectFailure:
						MessageBox.Show("Connection failed, check internet connection.\n" + ex.ToString(), "Connection failure", MessageBoxButtons.OK, MessageBoxIcon.Error);
						catched = true;
						break;

					case WebExceptionStatus.Timeout:
						MessageBox.Show("Timeout has expired.\n" + ex.ToString(), "Timeout", MessageBoxButtons.OK, MessageBoxIcon.Error);
						catched = true;
						break;

					case WebExceptionStatus.NameResolutionFailure:
						MessageBox.Show("Error in host name.\n" + ex.ToString(), "Name resolution failure", MessageBoxButtons.OK, MessageBoxIcon.Error);
						catched = true;
						break;
				}

				if (!catched) MessageBox.Show(ex.ToString(), "Error");
			}
			catch (UriFormatException ex)
			{
				MessageBox.Show(ex.ToString(), "Unmanaged exception", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Unmanaged exception", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}
		}

		private void btnCalculateMasses_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			string clpString = null;

			dynamic cdDoc = null;

			Word.WdOLEVerb verb = Word.WdOLEVerb.wdOLEVerbHide;

			try
			{
				Word.Table tNested;

				if (app.Selection.Tables[1].Columns.Count != tSize) //selected main table instead of nested table
					tNested = app.Selection.Tables[1].Cell(3, 1).Tables[1];
				else
					tNested = app.Selection.Tables[1];

				int tRows = tNested.Rows.Count;

				if (tRows < 3) goto ExitVoid;

				clpString = Clipboard.GetText();

				for (int i = 2; i < tRows; i++) //<= tRows - 1
				{
					try
					{
						if (tNested.Cell(i, 1).Range.InlineShapes.Count < 1) //no chemdraw document
						{ //create document with caption containing the typed text and extract molecular weight
							string text = tNested.Cell(i, 1).Range.Text.Trim('\r', '\a');
							if (string.IsNullOrEmpty(text)) continue;

							//var doc = new CambridgeSoft.ChemOffice.Interop.ChemDraw.Document();

							//doc.Groups.Item(1).Objects.
							//
							//var at1 = doc.MakeAtom();

							Type t = Type.GetTypeFromProgID("ChemDraw.Document.6.0");
							cdDoc = Activator.CreateInstance(t);

							//cdDoc.Application.Visible = false;

							//this is the only way it works xD
							Clipboard.SetText(text);
							cdDoc.Paste();

							double mr = cdDoc.Objects.MolecularWeight;

							if (mr > 0)
							{
								tNested.Cell(i, sett.CImr).Range.Text = formatNumber(mr, sett.dmr, sett.smr);
								formula(tNested.Cell(i, 1).Range);
							}

							cdDoc.Close();
						}
						else
						{
							Word.OLEFormat scheme = tNested.Cell(i, 1).Range.InlineShapes[1].OLEFormat;
							scheme.DoVerb(verb); //open chemdraw
												 //cdDoc = scheme.Object as Document; //ChemDraw document

							cdDoc = scheme.Object;
							//cdDoc.Application.Visible = false;

							double mr = cdDoc.Objects.MolecularWeight;

							if (mr <= 0) continue;

							tNested.Cell(i, sett.CImr).Range.Text = formatNumber(mr, sett.dmr, sett.smr);
							cdDoc.Close();
						}
					}
					catch (Exception ex)
					{
						if (MessageBox.Show("Error occured. Do you want to continue with next compound?\n\n" + ex.ToString(), "Error", MessageBoxButtons.YesNo, MessageBoxIcon.Error, MessageBoxDefaultButton.Button1) == DialogResult.Yes)
							continue;
						else
							break;
					}
				}
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}
			try
			{
				if (!sett.DontCloseChemDraw) cdDoc.Application.Quit();
			}
			catch { }

			if (!string.IsNullOrEmpty(clpString))
			{
				Clipboard.Clear();
				Clipboard.SetText(clpString);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnFormula_Click(object sender, RibbonControlEventArgs e)
		{
			try
			{
				//var range = app.Selection.Range;

				formula(app.Selection.Range);
			}
			catch
			{
			}
		}

		private void btnTransfer_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			if (app.Selection.Tables[1].Columns.Count == tSize) //selected nested table instead of main table
			{
				app.Selection.Tables[1].Select();
				app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
			}

			Word.WdOLEVerb verb = Word.WdOLEVerb.wdOLEVerbHide;
			dynamic cdDoc = null; //ChemDraw Document IChemDrawDocument

			//IDictionary<string, object> clpData = null;

			string clpString = null;

			try
			{
				if (app.Selection.Tables[1].Cell(2, 1).Range.InlineShapes.Count < 1) goto ExitVoid;

				Word.OLEFormat scheme = app.Selection.Tables[1].Cell(2, 1).Range.InlineShapes[1].OLEFormat;

				scheme.DoVerb(verb); //open chemdraw

				cdDoc = scheme.Object;

				int count = cdDoc.Groups.Count;

				if (count < 1) goto ExitVoid;

				if (app.Selection.Tables[1].Cell(3, 1).Tables.Count < 1) goto ExitVoid;

				Word.Table tNested = app.Selection.Tables[1].Cell(3, 1).Tables[1];

				int tRows = tNested.Rows.Count;

				if (tRows < 3) goto ExitVoid;

				if (tRows - 2 < count) //add missing rows to data table
				{
					var beforeRow = tNested.Rows[2]; //first row
					for (int i = 0; i < count - tRows + 2; i++)
					{
						tNested.Rows.Add(beforeRow);
					}
					setupDataTableBorders(ref tNested);
				}

				//backup clipboard data
				//clpData = GetClipboardData();

				clpString = Clipboard.GetText();

				int idx = 2;//, deleted = 0;

				for (int i = 1, deleted = 0; i <= count; i++)
				{
					var compound = cdDoc.Groups.Item(i);
					double mr = compound.Objects.MolecularWeight;

					if (mr <= 0)
					{
						tNested.Rows[i + 1 - deleted].Delete();
						deleted++;
						continue;
					}

					compound.Objects.Copy(); //copy compound to cliboard

					tNested.Cell(idx, 1).Select();
					app.Selection.Paste();
					tNested.Cell(idx, 1).Range.ParagraphFormat.Alignment = Word.WdParagraphAlignment.wdAlignParagraphCenter;

					tNested.Cell(idx, sett.CImr).Range.Text = formatNumber(mr, sett.dmr, sett.smr);

					idx++;
				}

				cdDoc.Close(); //close opened document
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}
			try
			{
				if (!sett.DontCloseChemDraw) cdDoc.Application.Quit();
			}
			catch { }

			//restore clipboard data

			if (!string.IsNullOrEmpty(clpString))
			{
				Clipboard.Clear();
				Clipboard.SetText(clpString);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void loadSettings()
		{
			sett = JsonSettings.Load<MySettings>();

			cbEq.Checked = sett.includeEq;
			cbExportUnits.Checked = sett.cbExportUnits;

			if (sett.WorkingDir == string.Empty)
			{
				string docDir = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
				sett.WorkingDir = Path.Combine(docDir, "LabNotebookAddIn");
			}

			if (sett.UsersDirFtp == string.Empty)
			{
				sett.UsersDirFtp = "/Users";
			}

			if (sett.LabNotebookDirFtp == string.Empty)
			{
				sett.LabNotebookDirFtp = "Laboratory notebooks";
			}

			//set default spacing in data table
			dataTableColWidths = new Dictionary<int, float>();

			dataTableColWidths.Add(1, 22.8F); //compound column
			dataTableColWidths.Add(sett.CIw, 8.5F);
			dataTableColWidths.Add(sett.CIcm, 11.4F);
			dataTableColWidths.Add(sett.CImr, 11.4F);
			dataTableColWidths.Add(sett.CIeq, 8.6F);
			dataTableColWidths.Add(sett.CIn, 10F);
			dataTableColWidths.Add(sett.CIro, 10F);
			dataTableColWidths.Add(sett.CIm, 8.5F);
			dataTableColWidths.Add(sett.CIV, 8.4F);
		}

		private void Ribbon_Load(object sender, RibbonUIEventArgs e)
		{
			app = Globals.ThisAddIn.Application;

			//MessageBox.Show("Ribbon_Loaded");
			//app = (Word.Application) Marshal.GetActiveObject("Word.Application");

			app.DocumentOpen += (Word.Document Doc) =>
			{
				doc = Doc;
				fullNameOfDoc = doc.FullName;
				ActiveDocumentChanged(doc);
			};

			//var wordApp = Marshal.GetActiveObject("Word.Application");

			bool docBeforeClose = false;
			app.DocumentBeforeClose += (Word.Document Doc, ref bool Cancel) =>
			{
				docBeforeClose = true;
				//kListener.Dispose();
			};

			app.WindowActivate += (Word.Document Doc, Word.Window Wn) =>
			{
				doc = Doc; //update active document when working with multiple documents
				fullNameOfDoc = doc.FullName;
				ActiveDocumentChanged(doc);
			};

			app.DocumentChange += () =>
			{
				if (!docBeforeClose)
				{
					try
					{
						doc = app.ActiveDocument; //update active document when working with multiple documents
						fullNameOfDoc = doc.FullName;
						ActiveDocumentChanged(doc);
					}
					catch
					{
					}

					//MessageBox.Show("Document changed\n\n" + fullNameOfDoc);
				}
			};

			app.DocumentBeforeSave += (Word.Document Doc, ref bool SaveAsUI, ref bool Cancel) =>
			{
				if (!justSavingInPublish && sett.ExportWhenSaved && (!string.IsNullOrEmpty(Doc.Path) || SaveAsUI))
				{
					btnPublish_Click(null, null);
					Cancel = true;
				}
			};

			//kListener = new KeyboardListener();
			//kListener.KeyDown += KListener_KeyDown;

			//sett = Properties.Settings.Default;

			loadSettings();

			nFormat = new NumberFormatInfo();
			nFormat.NumberGroupSeparator = " ";
			nFormat.NumberDecimalSeparator = ".";

			setDescriptions();

			//Test();
			//button2.Visible = false;
		}

		private void setDescriptions()
		{
			btnM1.ScreenTip = btnM1.Label;
			btnM1.SuperTip = "Recalculates entire table according to the specific cell. This cell must be selected (cursor can be placed in it).";
			btnM2.ScreenTip = btnM2.Label;
			btnM2.SuperTip = "Recalculates row of the table according to the specific cell. This cell must be selected (cursor can be placed in it). Equivalent in row is changed accordingly to the rest of the data table.";
			btnNewExp.ScreenTip = btnNewExp.Label;
			btnNewExp.SuperTip = "Copies last experiment (table) in document, inserts page break and copied table with today's date and selects reaction code cell. Date format can be changed.";
			btnLastOne.ScreenTip = btnLastOne.Label;
			btnLastOne.SuperTip = btnNewExp.SuperTip;
			btnNewExpSelected.ScreenTip = btnNewExpSelected.Label;
			btnNewExpSelected.SuperTip = "Copies selected experiment (table) in document (cursor can be placed in main or data table, doesn't matter), inserts page break after last experiment (table) in document and copied table with today's date and selects reaction code cell. Date format can be changed in settings.";
			btnFitToPage.ScreenTip = btnFitToPage.Label;
			btnFitToPage.SuperTip = "Sets the procedure cell height to fit the entire experiment table to the page. Empirical sum of heights can be adjusted in settings.";
			//btnWholeDocument.ScreenTip = btnWholeDocument.Label;
			//btnWholeDocument.SuperTip = "Performs the Fit to page command for all tables in document.";
			btnCopyData.ScreenTip = btnCopyData.Label;
			btnCopyData.SuperTip = "Copies row data from the data table to clipboard in the following format: ([concentration], [equivalent - optional], [mass/volume], [amount of substance]). Row must be selected (cursor can be placed in any cell of that row). Handy when writing supporting info for the article.";
			cbEq.ScreenTip = cbEq.Label;
			cbEq.SuperTip = "Includes equivalent when exporting from data table.";
			btnChangeUnits.ScreenTip = btnChangeUnits.Label;
			btnChangeUnits.SuperTip = "Changes mmol->µmol, g->mg and mL->µL and vice versa in data table heading.";
			cbExportUnits.ScreenTip = cbExportUnits.Label;
			cbExportUnits.SuperTip = "Appropriately changes unit when exporting from data table. Values < 0.1 and >= 1000 will be appropriately changed (eg: 0.025 g -> 25 mg, 1253 mmol -> 1.253 mol). This applies only for the amount of substance, mass and volume.";
			btnTransfer.ScreenTip = btnTransfer.Label;
			btnTransfer.SuperTip = "Transfers compounds from reaction scheme (ChemDraw object) to data table and fills molecular weights. The data table must have at least 3 rows. If number of transfered compounds exceeds the number of compound rows in data table, the rest is added. Compounds are transfered in the order they were added in ChemDraw.";
			btnCalculateMasses.ScreenTip = btnCalculateMasses.Label;
			btnCalculateMasses.SuperTip = "Calculates molecular weights of compounds in the first column of data table (written as text or included as ChemDraw objects) and puts them in molecular weight column. Formula command is applied to text.";
			btnFormula.ScreenTip = btnFormula.Label;
			btnFormula.SuperTip = "Formats the selection to look like chemical formula. Formats appropriate numbers as subscript.";
			btnRemoveRows.ScreenTip = btnRemoveRows.Label;
			btnRemoveRows.SuperTip = "Deletes selected rows in table. Does the same job as the command in table tools - layout.";
			btnHeading.ScreenTip = btnHeading.Label;
			btnHeading.SuperTip = "Formats heading in data table according to correct order defined in settings and resets the borders.";
			btnPublish.ScreenTip = btnPublish.Label;
			btnPublish.SuperTip = "Saves current document to disk or ftp server as docx and/or pdf (defined in settings).";
			btnInsertTemplate.ScreenTip = btnInsertTemplate.Label;
			btnInsertTemplate.SuperTip = "Inserts laboratory notebook template table.";
			btnSwapCells.ScreenTip = btnSwapCells.Label;
			btnSwapCells.SuperTip = "Smartly swaps selected cells/rows/columns. Only even number of cells can be swapped. This command works only for tables without vertically merged cells.";
			btnChemDrawInfo.ScreenTip = btnChemDrawInfo.Label;
			btnChemDrawInfo.SuperTip = "This method gets the information about the ChemDraw class IChemDrawDocument. ChemDraw object must be selected. Extracted data are copied to clipboard. This takes some time.";
			//btnTestFormat.ScreenTip = btnTestFormat.Label;
			//btnTestFormat.SuperTip = "Inserts page breaks among all tables (experiments) in document, deletes unnecessary characters and performs fit to page comannd for each table.";
			grpFormat.DialogLauncher.ScreenTip = "Formatting for entire document";
			grpFormat.DialogLauncher.SuperTip = "Opens Format Document Dialog, which enables formatting for entire document (fit to page, spaces, data table).";

			btnOpenNMR.ScreenTip = btnOpenNMR.Label;
			btnOpenNMR.SuperTip = @"Opens selected text as NMR in MestReNova. NMR needs to be stored in ./NMR/[expCode]{inserts the selected text}. * stands for the reaction code.";
		}

		private void Test()
		{
			//Indigo indigo = new Indigo();

			//string ver = indigo.version();

			//foreach (IndigoObject mol in indigo.iterateCDXFile(@"C:\Users\Dominik\Desktop\untitled.cdx"))
			//{
			//	string form = mol.grossFormula();
			//	float molW = mol.molecularWeight();
			//}

			//var shape = app.Selection.InlineShapes[1];

			//Word.OLEFormat ole = shape.OLEFormat;

			//object verb = Word.WdOLEVerb.wdOLEVerbShow;

			//try
			//{
			//	ole.DoVerb(ref verb);

			//	//object obj = shape.OLEFormat.Object;

			//	Document cdDoc = shape.OLEFormat.Object as Document; //ChemDraw document

			//	double mr = cdDoc.Objects.MolecularWeight;

			//	int gCount = cdDoc.Groups.Count;

			//	var item = cdDoc.Groups.Item(2);

			//	item.Objects.Copy();

			//	double mww = cdDoc.Groups.Item(1).Objects.MolecularWeight;

			//	//cdDoc.Close(false);

			//	cdDoc.Application.Quit();

			//	//Type dispatchType = DispatchUtility.GetType(obj, true);

			//	//string name = dispatchType.FullName;

			//	//MemberInfo[] members = dispatchType.GetMembers();

			//	//var propertyNames = dispatchType.GetProperties().Select(p => p.Name).ToArray();
			//	//foreach (var prop in propertyNames)
			//	//{
			//	//	object propValue = dispatchType.GetProperty(prop).GetValue(obj, null);
			//	//}

			//}
			//catch (Exception ex)
			//{
			//	throw;
			//}
		}

		#region Formatting

		//presumed format: xxxxxNN?xx?NNN?xxxx where x is letter and N is digit
		//Example CFDM01_05 -> CFDM01_06 ; DMb078A -> DMb079A

		public static string getIncrementedCode(string oldCode)
		{
			if (string.IsNullOrEmpty(oldCode)) return string.Empty;

			oldCode = oldCode.Trim('\r', '\a'); //remove shit characters

			string strCode = string.Empty;

			int firstDigIdx = -1, lastDigIdx = 0;

			for (int i = 0; i < oldCode.Length; i++)
			{
				if (char.IsDigit(oldCode[i]))
				{
					if (firstDigIdx == -1) firstDigIdx = i;
					//strCode += oldCode[i];
					lastDigIdx = i;
				}
			}

			if (firstDigIdx == -1) return oldCode; //doesn't contain digit, return the same string

			for (firstDigIdx = lastDigIdx; firstDigIdx >= 0; firstDigIdx--)
			{
				if (!char.IsDigit(oldCode[firstDigIdx]))
				{ firstDigIdx++; break; }
			}

			if (firstDigIdx < 0) firstDigIdx = 0;

			//if (strCode.Length < 1) return oldCode; //doesn't contain digit, return the same string
			long code;
			strCode = oldCode.Substring(firstDigIdx, lastDigIdx - firstDigIdx + 1); // last number part in reaction code
			if (!long.TryParse(strCode, out code)) return oldCode;

			if (code == long.MaxValue) return oldCode;

			code++; //increment itself
			int exp = (int)Math.Floor(Math.Log10(code)) + 1; //exponent + 1

			strCode = code.ToString(new string('0', (exp > strCode.Length) ? exp : strCode.Length));

			return oldCode.Substring(0, firstDigIdx) + strCode + oldCode.Substring(lastDigIdx + 1, oldCode.Length - lastDigIdx - 1);
		}

		public static string getSmilesFromClipboard(int maxTries, int waitTime)
		{
			byte[] data = null;

			for (int i = 0; i < maxTries; i++)
			{
				data = getDataFromClipboard("SMILES");
				if (data != null)
				{
					return Encoding.UTF8.GetString(data).Substring(0, data.Length - 1);
				}
				LogListener.WriteLine($"Error loading data from clipboard, waiting {waitTime} ms. {i + 1}. try.");
				System.Threading.Thread.Sleep(waitTime);
			}
			return null;
		}

		public static IEnumerable<IndigoObject> IterateMolecules(dynamic chemDrawObj, Indigo indigo)
		{
			int components = chemDrawObj.Groups.Count;

			if (components < 1) { yield break; }

			IndigoObject rxn = LoadReaction(chemDrawObj, indigo);

			if (rxn == null) // Indigo reaction reading exception, manualy load molecules
			{
				for (int i = 1; i <= components; i++) // for each component
				{
					dynamic compound;

					try
					{
						compound = chemDrawObj.Groups.Item(i);
					}
					catch (Exception ex)
					{
						LogListener.WriteLine($"Rare error: " + ex.Message);
						continue;
					}

					double mr = compound.Objects.MolecularWeight;

					if (mr <= 0)
						continue;

					compound.Objects.Copy(); //copy compound to cliboard

					string smiles = getSmilesFromClipboard(maxTries, waitTime);

					IndigoObject molecule;

					try
					{
						molecule = indigo.loadMolecule(smiles);
					}
					catch (IndigoException ex)
					{
						//Debug.WriteLine("IndigoException - loading molecule:\n" + ex.Message);
						LogListener.WriteLine($"Error loading molecule>{smiles}<: " + ex.Message);
						continue;
					}

					yield return molecule;
				}
				//obj.Close();
				yield break;
			}

			foreach (IndigoObject molecule in rxn.iterateMolecules())
			{
				yield return molecule;
			}

			//renderer.renderToFile(rxn, Path.Combine(path, string.Format("{0}.png", reactionCode)));

			//Debug.WriteLine(100 * i / count);

			//Application.DoEvents();
			//obj.Close();
		}

		//winapi method for getting data from clipboard, .NET methods doesn't work in multithreading applications - some STA thread attribute
		//help from https://stackoverflow.com/questions/33003958/getting-byte-from-getclipboarddata-native-method
		public static byte[] getDataFromClipboard(string format)
		{
			uint formatType = DllImports.RegisterClipboardFormat(format);
			if (formatType == 0) return null;

			if (!DllImports.IsClipboardFormatAvailable(formatType))
				return null;

			try
			{
				if (!DllImports.OpenClipboard(IntPtr.Zero))
					return null;

				IntPtr handle = DllImports.GetClipboardData(formatType);
				if (handle == IntPtr.Zero)
					return null;

				IntPtr pointer = IntPtr.Zero;

				try
				{
					pointer = DllImports.GlobalLock(handle);
					if (pointer == IntPtr.Zero)
						return null;

					int size = DllImports.GlobalSize(handle);
					byte[] buff = new byte[size];

					Marshal.Copy(pointer, buff, 0, size);

					return buff;
				}
				finally
				{
					if (pointer != IntPtr.Zero)
						DllImports.GlobalUnlock(handle);
				}
			}
			finally
			{
				DllImports.CloseClipboard();
			}
		}

		public static IndigoObject LoadReaction(dynamic ChemDrawDocument, Indigo indigo)
		{
			try
			{
				//int count = ChemDrawDocument.Groups.Count;

				//if (count < 2) return null;

				ChemDrawDocument.Objects.Copy();

				string smiles = getSmilesFromClipboard(maxTries, waitTime);

				IndigoObject rxn = indigo.loadReaction(smiles);

				//int reactants = rxn.countReactants();
				//int products = rxn.countProducts();
				//int catalysts = rxn.countCatalysts();

				return rxn;
			}
			catch (COMException ex)
			{
				throw;
			}
			catch (IndigoException ex)
			{
				LogListener.WriteLine("Error loading reaction: " + ex.Message);
				return null;
			}
			catch (Exception ex)
			{
				throw;
				//return null;
			}
		}

		public void fitTableToPage(ref Word.Table t)
		{
			t.Cell(3, 1).HeightRule = Word.WdRowHeightRule.wdRowHeightAuto;
			t.Cell(2, 1).HeightRule = Word.WdRowHeightRule.wdRowHeightAuto;

			float reactHeight = t.Cell(3, 1).Range.Information[Word.WdInformation.wdVerticalPositionRelativeToPage]
				- t.Cell(2, 1).Range.Information[Word.WdInformation.wdVerticalPositionRelativeToPage];

			float dataTHeight = t.Cell(4, 1).Range.Information[Word.WdInformation.wdVerticalPositionRelativeToPage]
					- t.Cell(3, 1).Range.Information[Word.WdInformation.wdVerticalPositionRelativeToPage];

			t.Cell(6, 1).HeightRule = Word.WdRowHeightRule.wdRowHeightExactly;
			float set = sett.sumSpace - dataTHeight - reactHeight;

			if (set >= 0) t.Cell(6, 1).Height = set;// set procedure cell height

			t.AllowAutoFit = false;
		}

		/// <summary>
		/// Changes the text in range to formula, example: 2Na2SO4*2H2O will be changed to 2Na_{2}SO_{4}*2H_{2}O / from Latex notation
		/// </summary>
		/// <param name="text"></param>
		/// <returns></returns>
		public void formula(Word.Range rng)
		{
			if (rng.Start == rng.End || string.IsNullOrEmpty(rng.Text)) return;

			string text = rng.Text.Replace("\a", string.Empty).Replace("\n", string.Empty).Replace("\r", string.Empty);

			if (text == string.Empty) return;

			rng.Text = text;

			int start;// = rng.Start;
			int end;// = rng.End;

			start = rng.Start;
			end = rng.End;

			text += 'A';

			bool wasLetter = false;

			int startIdx = -1, endIdx = -1;

			for (int i = 0; i < text.Length; i++)
			{
				bool isDigit = char.IsDigit(text[i]);

				//wasLetter = (!wasLetter && !isDigit) ? true : wasLetter;

				wasLetter = (wasLetter || isDigit) ? wasLetter : true;

				//wasLetter = char.IsLetter(text[i]) || isBracket(text[i]);

				//if (isDigit && startIdx == -1) startIdx = i;
				//if (!isDigit) endIdx = i;

				if (isDigit && wasLetter && startIdx == -1) startIdx = i;
				if (!isDigit && wasLetter) endIdx = i;

				wasLetter = char.IsLetter(text[i]) || isBracket(text[i]);

				if (endIdx > startIdx && startIdx != -1 && endIdx != -1)
				{
					rng.Start = start + startIdx;
					rng.End = start + endIdx;
					rng.Font.Subscript = 1; //this line makes the selected text subscripted
					endIdx = startIdx = -1;
				}
			}
		}

		//trick with font size
		public Point[] GetSelectedCells()
		{
			//We will use this to hold column and row coordinates for cells
			List<Point> value = new List<System.Drawing.Point>();
			try
			{
				Word.Range range = app.Selection.Range;

				Word.Table tempTable = range.Tables[1];
				float[,] backupTable = new float[range.Tables[1].Rows.Count + 1, range.Tables[1].Columns.Count + 1];

				for (int i = 1; i <= tempTable.Rows.Count; i++)
				{
					for (int j = 1; j <= tempTable.Rows[i].Cells.Count; j++)
						backupTable[i, j] = tempTable.Rows[i].Cells[j].Range.Font.Size;
				}

				app.Selection.Font.Size = 1;

				foreach (Word.Row row in range.Tables[1].Rows)
				{
					foreach (Word.Cell cell in row.Cells)
					{
						if (cell.Range.Font.Size == 1)
						{
							Point point = new Point();
							point.X = cell.RowIndex;
							point.Y = cell.ColumnIndex;
							value.Add(point);
						}
					}
				}

				for (int i = 1; i <= tempTable.Rows.Count; i++)
				{
					for (int j = 1; j <= tempTable.Rows[i].Cells.Count; j++)
						tempTable.Rows[i].Cells[j].Range.Font.Size = backupTable[i, j];
				}
				Marshal.ReleaseComObject(tempTable);
			}
			catch (Exception ex)
			{
				throw;
			}
			return value.ToArray();
		}

		public string insertDate()
		{
			if (sett.dateType == 0)
			{
				return DateTime.Today.ToString("yyyy-MM-dd");
			}
			else if (sett.dateType == 1)
			{
				return DateTime.Today.ToString("dd.MM.yyyy");
			}
			return DateTime.Today.ToString();
		}

		public void setupDataTableBorders(ref Word.Table t)
		{
			int rows = t.Rows.Count;

			//setup borders

			t.Borders.Enable = 0;

			t.Borders.InsideLineStyle = Word.WdLineStyle.wdLineStyleSingle; //single line is 50 pt width
			t.Borders.OutsideLineStyle = Word.WdLineStyle.wdLineStyleSingle;

			for (int r = 1; r <= rows; r++) //for each row
			{
				if (r == 1)
				{
					t.Rows[1].Borders[Word.WdBorderType.wdBorderTop].LineWidth = THICKLineWidth;
					t.Rows[1].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = thickLineWidth;
				}
				if (r == rows)
				{
					t.Rows[rows].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = THICKLineWidth;
					t.Rows[rows].Borders[Word.WdBorderType.wdBorderTop].LineWidth = thickLineWidth;
					t.Rows[rows].Range.Shading.BackgroundPatternColor = Word.WdColor.wdColorGray05;
					t.Cell(rows, 2).Borders[Word.WdBorderType.wdBorderLeft].LineWidth = thickLineWidth; //Yield thick lines
					t.Cell(rows, 2).Borders[Word.WdBorderType.wdBorderRight].LineWidth = thickLineWidth;
					t.Cell(r, 4).Borders[Word.WdBorderType.wdBorderRight].LineWidth = THICKLineWidth;
				}
				t.Cell(r, 1).Borders[Word.WdBorderType.wdBorderLeft].LineWidth = THICKLineWidth;
				t.Cell(r, 1).Borders[Word.WdBorderType.wdBorderRight].LineWidth = thickLineWidth;
				if (r < rows) t.Cell(r, tSize).Borders[Word.WdBorderType.wdBorderRight].LineWidth = THICKLineWidth;
				t.Rows[r].HeightRule = Word.WdRowHeightRule.wdRowHeightAtLeast;
			}
		}

		public void updateHeading(ref Word.Table t, bool micro)
		{
			t.Cell(1, 1).Range.Font.Superscript = 0;
			t.Cell(1, 1).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIw).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIw).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIcm).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIcm).Range.Font.Subscript = 0;
			t.Cell(1, sett.CImr).Range.Font.Superscript = 0;
			t.Cell(1, sett.CImr).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIeq).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIeq).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIro).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIro).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIn).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIn).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIm).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIm).Range.Font.Subscript = 0;
			t.Cell(1, sett.CIV).Range.Font.Superscript = 0;
			t.Cell(1, sett.CIV).Range.Font.Subscript = 0;

			t.Cell(1, 1).Range.Text = "Compound";
			t.Cell(1, 1).Range.Font.Bold = 1;
			t.Cell(1, sett.CIw).Range.Text = "w/%";

			t.Cell(1, sett.CIcm).Range.Text = "c/mol L-1";
			doc.Range(t.Cell(1, sett.CIcm).Range.End - 3, t.Cell(1, sett.CIcm).Range.End).Font.Superscript = 1;

			//app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
			//app.Selection.MoveLeft(Word.WdUnits.wdCharacter, 1);
			//app.Selection.MoveLeft(Word.WdUnits.wdCharacter, 2, Word.WdMovementType.wdExtend);
			t.Cell(1, sett.CImr).Range.Text = "M/g mol-1";
			doc.Range(t.Cell(1, sett.CImr).Range.End - 3, t.Cell(1, sett.CImr).Range.End).Font.Superscript = 1;

			//app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
			//app.Selection.MoveLeft(Word.WdUnits.wdCharacter, 1);
			//app.Selection.MoveLeft(Word.WdUnits.wdCharacter, 2, Word.WdMovementType.wdExtend);

			t.Cell(1, sett.CIeq).Range.Text = "eq";

			t.Cell(1, sett.CIro).Range.Text = "\u03C1/g mL-1"; //micro is unicode character 03C1 in hex
			doc.Range(t.Cell(1, sett.CIro).Range.End - 3, t.Cell(1, sett.CIro).Range.End).Font.Superscript = 1;

			//t.Cell(1, CIro).Range.Select();
			//app.Selection.Collapse();
			//app.Selection.TypeText("\u03C1/g mL-1");

			//doc.Range(t.Cell(1, CIro).Range.End - 3, t.Cell(1, CIro).Range.End).Font.Superscript = 1;

			//app.Selection.MoveLeft(Word.WdUnits.wdCharacter, 2, Word.WdMovementType.wdExtend);
			//app.Selection.Font.Superscript = 1;
			app.Selection.Collapse();

			string nHead, mHead, VHead;

			if (micro)
			{
				nHead = "n/µmol";
				mHead = "m/mg";
				VHead = "V/µL";
			}
			else
			{
				nHead = "n/mmol";
				mHead = "m/g";
				VHead = "V/mL";
			}

			t.Cell(1, sett.CIn).Range.Text = nHead;
			t.Cell(1, sett.CIm).Range.Text = mHead;
			t.Cell(1, sett.CIV).Range.Text = VHead;
		}

		//private static int PadLeftSequence(byte[] bytes, byte[] seqBytes)
		//{
		//	int i = 1;
		//	while (i < bytes.Length)
		//	{
		//		int n = bytes.Length - i;
		//		byte[] aux1 = new byte[n];
		//		byte[] aux2 = new byte[n];
		//		Array.Copy(bytes, i, aux1, 0, n);
		//		Array.Copy(seqBytes, aux2, n);
		//		if (aux1.SequenceEqual(aux2))
		//			return i;
		//		i++;
		//	}
		//	return i;
		//}

		private void btnFitToPage_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			try
			{
				Word.Table t, tNested;

				if (app.Selection.Tables[1].Columns.Count == tSize) //user selected nested table
				{
					tNested = app.Selection.Tables[1];
					int c = app.Selection.Cells[1].ColumnIndex;
					int r = app.Selection.Cells[1].RowIndex;

					app.Selection.Tables[1].Select();
					app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
					t = app.Selection.Tables[1];
					tNested.Cell(r, c).Select(); //select original cell in nested data table
					app.Selection.Collapse();
				}
				else
				{
					t = app.Selection.Tables[1];
				}

				fitTableToPage(ref t);
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				goto ExitVoid;
			}

			ExitVoid:
			optimizePerformance(true);
		}

		public void formatDataTable(ref Word.Table mainTable)
		{
			Word.Table tNested = mainTable.Cell(3, 1).Tables[1];

			string[,] strData = getTableStringArray(ref tNested);
			if (strData == null) return;
			tNested = mainTable.Cell(3, 1).Tables[1]; //table has been deleted, so it has to be asigned again

			bool micro = false;

			string[] mU = strData[0, sett.CIm - 1].Split('/');
			if (mU.Length == 2 && mU[1] == "mg")
				micro = true;

			tNested.AllowAutoFit = false;
			tNested.Rows.Alignment = Word.WdRowAlignment.wdAlignRowCenter;
			tNested.Range.Font.Name = "Consolas";
			tNested.Range.Font.Size = 9F;
			tNested.Range.ParagraphFormat.SpaceAfter = 0;

			tNested.Select();

			tNested.Range.ParagraphFormat.Alignment = Word.WdParagraphAlignment.wdAlignParagraphCenter;
			app.Selection.Cells.VerticalAlignment = Word.WdCellVerticalAlignment.wdCellAlignVerticalCenter;

			app.Selection.Collapse();

			setupDataTableBorders(ref tNested);

			updateHeading(ref tNested, micro);
		}

		public void formatMainTable(ref Word.Table t)
		{
			int rows = 9, columns = 7;

			t.Rows.Alignment = Word.WdRowAlignment.wdAlignRowCenter;
			t.Range.ParagraphFormat.SpaceAfter = 0;

			//setup borders
			t.Borders.InsideLineStyle = Word.WdLineStyle.wdLineStyleSingle; //single line is 50 pt width
			t.Borders.OutsideLineStyle = Word.WdLineStyle.wdLineStyleSingle;

			for (int r = 1; r <= rows; r++) //for each row
			{
				//if (r < 7)
				//	t.Rows[r].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = thickLineWidth;

				t.Cell(r, 1).Borders[Word.WdBorderType.wdBorderLeft].LineWidth = THICKLineWidth;
				t.Cell(r, 1).Borders[Word.WdBorderType.wdBorderRight].LineWidth = thickLineWidth;
				t.Cell(r, columns).Borders[Word.WdBorderType.wdBorderRight].LineWidth = THICKLineWidth;

				if (r == 1)
				{
					t.Rows[1].Borders[Word.WdBorderType.wdBorderTop].LineWidth = THICKLineWidth;
					t.Cell(1, 6).Borders[Word.WdBorderType.wdBorderLeft].LineWidth = thickLineWidth; //Date border
				}
				if (r == rows)
				{
					t.Rows[rows].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = THICKLineWidth;
				}
				if (r == 5)
				{
					t.Rows[r].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = Word.WdLineWidth.wdLineWidth050pt;
					t.Rows[r].Borders[Word.WdBorderType.wdBorderBottom].LineStyle = Word.WdLineStyle.wdLineStyleDashLargeGap;
				}
				t.Rows[r].HeightRule = Word.WdRowHeightRule.wdRowHeightExactly;
				t.Rows[r].Height = 14.2F; //1cm = 28.35 pt
			}

			t.Rows[rows].Height *= 3;
		}

		private void btnFormatDataTable_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			try
			{
				Word.Table t, tNested;

				if (app.Selection.Tables[1].Columns.Count == tSize) //user selected nested table
				{
					tNested = app.Selection.Tables[1];
					int c = app.Selection.Cells[1].ColumnIndex;
					int r = app.Selection.Cells[1].RowIndex;

					app.Selection.Tables[1].Select();
					app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
					t = app.Selection.Tables[1];
					tNested.Cell(r, c).Select(); //select original cell in nested data table
					app.Selection.Collapse();
				}
				else
				{
					t = app.Selection.Tables[1];
				}

				formatDataTable(ref t);
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnChangeUnits_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			try
			{
				Word.Table t;

				if (app.Selection.Tables[1].Columns.Count != tSize) //selected main table instead of nested table
					t = app.Selection.Tables[1].Cell(3, 1).Tables[1];
				else
					t = app.Selection.Tables[1];

				string[,] strData = getTableStringArray(ref t);
				if (strData == null) goto ExitVoid;
				t = app.Selection.Tables[1]; //table has been deleted, so it has to be asigned again

				string nHead, mHead, VHead;

				string[] mU = strData[0, sett.CIm - 1].Split('/');
				if (mU.Length != 2) { updateHeading(ref t, false); goto ExitVoid; }

				if (mU[1] == "g")
				{
					nHead = "n/µmol";
					mHead = "m/mg";
					VHead = "V/µL";
				}
				else if (mU[1] == "mg")
				{
					nHead = "n/mmol";
					mHead = "m/g";
					VHead = "V/mL";
				}
				else
				{
					updateHeading(ref t, false);
					goto ExitVoid;
				}

				t.Cell(1, sett.CIn).Range.Text = nHead;
				t.Cell(1, sett.CIm).Range.Text = mHead;
				t.Cell(1, sett.CIV).Range.Text = VHead;
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnChemDrawInfo_Click(object sender, RibbonControlEventArgs e)
		{
			Indigo indigo = new Indigo();
			IndigoRenderer renderer = new IndigoRenderer(indigo);

			indigo.setOption("render-output-format", "png");
			indigo.setOption("render-margins", 10, 10);
			indigo.setOption("render-coloring", true);
			indigo.setOption("render-image-width", 1500);

			string picPath = @"C:\Users\Dominik\Desktop\test\";

			//RenderReactionsInDocument(renderer, indigo, picPath);

			Type t = Type.GetTypeFromProgID("ChemDraw.Document.6.0");
			dynamic cdDoc = Activator.CreateInstance(t);

			//CambridgeSoft.ChemOffice.Interop.ChemDrawControl.Document cdDoc = (CambridgeSoft.ChemOffice.Interop.ChemDrawControl.Document)Activator.CreateInstance(t);

			cdDoc.Activate();

			//cdDoc.Application.Visible = true;

			MessageBox.Show("Draw molecule and then click OK! Don't close the document.", "LabNotebook AddIn");

			int count = cdDoc.Application.ActiveDocument.Groups.Count;
			if (count < 1) { cdDoc.Close(); return; };

			cdDoc.Application.ActiveDocument.Objects.Copy();

			cdDoc.Application.ActiveDocument.Close();

			MemoryStream data = (MemoryStream)Clipboard.GetData("SMILES");
			byte[] bytes = data.ToArray();
			string smiles = Encoding.UTF8.GetString(bytes).Substring(0, bytes.Length - 1);

			IndigoObject queryMolecule = null;

			try
			{
				queryMolecule = indigo.loadMolecule(smiles);

				renderer.renderToFile(queryMolecule, Path.Combine(picPath, "moleculeFromChemDraw.png"));
			}
			catch (IndigoException ex)
			{
				Debug.WriteLine("IndigoException - loading molecule:\n" + ex.Message);
				return;
			}

			int tcount = doc.Tables.Count;

			if (tcount < 1) return; //no table in document

			string queryMoleculeFormula = queryMolecule.grossFormula();

			string matches = string.Format("Query molecule formula: {0}\n", queryMoleculeFormula);

			for (int i = 1; i <= tcount; i++) // for each table in document
			{
				var shape = doc.Tables[i].Cell(2, 1).Range.InlineShapes[1];

				string reactionCode = doc.Tables[i].Cell(1, 2).Range.Text.Trim('\r', '\a', ' ', '\t');

				Word.OLEFormat ole = shape.OLEFormat;

				ole.DoVerb(Word.WdOLEVerb.wdOLEVerbHide); //open chemdraw

				CambridgeSoft.ChemOffice.Interop.ChemDrawControl.Document obj = ole.Object;

				foreach (IndigoObject molecule in IterateMolecules(obj, indigo))
				{
					IndigoObject match = indigo.exactMatch(molecule, queryMolecule, "NONE");

					string moleculeFormula = molecule.grossFormula();

					if (match != null)
					{
						matches += string.Format("Exact match: Reaction code {0}, page {1} in document.\n", reactionCode, "Not Impelemented");
					}
				}

				obj.Close();
				Debug.WriteLine(i * 100 / tcount);
			}

			Debug.WriteLine(matches);

			File.WriteAllText(Path.Combine(picPath, "matchResults.txt"), matches);

			//if (app.Selection.InlineShapes.Count < 1)
			//{
			//	MessageBox.Show("Select ChemDraw object!");
			//	return;
			//}
			//var shape = app.Selection.InlineShapes[1];
			//string text = string.Empty;

			////try
			////{
			//Word.OLEFormat ole = shape.OLEFormat;

			//if (ole.ProgID != "ChemDraw.Document.6.0") return;

			//ole.DoVerb(Word.WdOLEVerb.wdOLEVerbHide); //open chemdraw

			//CambridgeSoft.ChemOffice.Interop.ChemDrawControl.Document obj = ole.Object;

			//int count = obj.Groups.Count;

			//if (count < 1) return;

			//int molCount = 0;

			//IndigoObject rxn = LoadReaction(ref obj, indigo);

			//string smiles = "";

			//foreach (string file in Directory.EnumerateFiles(picPath))
			//{
			//	File.Delete(file);
			//}

			//for (int j = 1; j <= count; j++)
			//{
			//	var compound = obj.Groups.Item(j);
			//	double mr = compound.Objects.MolecularWeight;

			//	if (mr <= 0)
			//	{
			//		continue;
			//	}

			//	compound.Objects.Copy(); //copy compound to cliboard

			//	bool contSmiles = Clipboard.ContainsData("SMILES");

			//	molCount++;

			//	try
			//	{
			//		IndigoObject molecule = indigo.loadMolecule(smiles);

			//		molecule.layout();

			//		renderer.renderToFile(molecule, picPath + molCount.ToString() + ".png");

			//		int atoms = molecule.countAtoms();
			//		int pseudoAtoms = molecule.countPseudoatoms();
			//		int bonds = molecule.countBonds();
			//		string check = string.Format("Bad valence: {0}, Ambiguous hydrogens: {1}", molecule.checkBadValence(), molecule.checkAmbiguousH());
			//		double mass = molecule.molecularWeight();
			//		string formula = molecule.grossFormula();

			//		//renderer.renderToFile(item, picPath + i.ToString() + ".png");

			//		Debug.WriteLine(string.Format("\n\nAtoms {0}, Pseudoatoms {1}, Bonds {2}\n {3}\n Mass {4}, Formula {5}", atoms, pseudoAtoms, bonds, check, mass, formula));
			//	}
			//	catch (IndigoException ex)
			//	{
			//		Debug.WriteLine(ex.ToString());
			//		//Debug.WriteLine(ex.);
			//	}

			//}

			//obj.Close(); //close opened document

			//app.Selection.Copy();

			//MemoryStream data;
			//byte[] buffer = null;

			//if (Clipboard.ContainsData("ChemDraw Interchange Format"))
			//{
			//	data = (MemoryStream)Clipboard.GetData("ChemDraw Interchange Format");
			//	buffer = data.ToArray();
			//}
			//else if (Clipboard.ContainsData("Native"))
			//{
			//	data = (MemoryStream)Clipboard.GetData("Native");
			//	byte[] buf = data.ToArray();

			//	byte[] sequence = { 0x56, 0x6a, 0x43, 0x44, 0x30, 0x31, 0x30, 0x30 };

			//	long[] pos = FindPositions(data, sequence);

			//	long idx = pos[pos.Length - 1];

			//	buffer = new byte[buf.LongLength - idx];
			//	//buf.CopyTo(buffer, idx);

			//	Array.Copy(buf, idx, buffer, 0, buf.LongLength - idx);

			//	File.WriteAllBytes(@"C:\Users\Dominik\Desktop\file.cdx", buffer);

			//	//OpenClipboard(IntPtr.Zero);
			//	//IntPtr hemf = GetClipboardData(CF_ENHMETAFILE);
			//	//CloseClipboard();
			//	//Metafile mf = new Metafile(hemf, true);
			//	//mf.Save(@"C:\Users\Dominik\Desktop\ehmf");

			//	// bool res = EnumEnhMetaFile(IntPtr.Zero, mf.GetHenhmetafile(), null, IntPtr.Zero, new Rectangle(0, 0, 9999, 9999));
			//}
			//else
			//	return;

			//int i = 1;
			//try
			//{
			//	foreach (IndigoObject item in indigo.iterateCDX(cdxFile))
			//	{
			//		try
			//		{
			//			renderer.renderToFile(item, picPath + i.ToString() + ".png");

			//			int atoms = item.countAtoms();
			//			int pseudoAtoms = item.countPseudoatoms();
			//			int bonds = item.countBonds();
			//			string check = string.Format("Bad valence: {0}, Ambiguous hydrogens: {1}", item.checkBadValence(), item.checkAmbiguousH());
			//			double mass = item.molecularWeight();
			//			string formula = item.grossFormula();

			//			item.layout();

			//			//renderer.renderToFile(item, picPath + i.ToString() + ".png");

			//			Debug.WriteLine(string.Format("\n\nAtoms {0}, Pseudoatoms {1}, Bonds {2}\n {3}\n Mass {4}, Formula {5}", atoms, pseudoAtoms, bonds, check, mass, formula));
			//		}
			//		catch (IndigoException ex)
			//		{
			//			Debug.WriteLine(ex.ToString());
			//			//Debug.WriteLine(ex.);
			//		}
			//		i++;
			//	}
			//}
			//catch (IndigoException ex)
			//{
			//	Debug.WriteLine(ex.ToString());
			//}
			//Debug.WriteLine("Total number of object in cdx: " + i.ToString());

			//CambridgeSoft.ChemOffice.Interop.ChemDrawControl.Document obj = ole.Object;

			//object verb = Word.WdOLEVerb.wdOLEVerbShow;

			//ole.DoVerb(verb);

			/*Type dispatchType = DispatchUtility.GetType(obj, true);

			text = typeToString(dispatchType);

			obj.Close();*/
			//}
			//catch (Exception ex)
			//{
			//	MessageBox.Show("Exception occured" + ex.ToString());
			//	text += "\nException occured:\n\n" + ex.ToString();
			//}

			//MessageBox.Show("Data has been copied to clipboard.");
			//Clipboard.SetText(text);
		}

		private void btnInsertTemplate_Click(object sender, RibbonControlEventArgs e)
		{
			insertTemplate();

			//insertDataTableTemplate(app.Selection.Range, 5, Word.WdLineWidth.wdLineWidth225pt, Word.WdLineWidth.wdLineWidth150pt, "Consolas");
		}

		private void btnLastOne_Click(object sender, RibbonControlEventArgs e)
		{
			btnNewExp_Click(null, null);
		}

		private void btnNewExp_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			int count = doc.Tables.Count;

			//no table in document
			if (count < 1)
			{
				if (MessageBox.Show("No table found in document, do you want to insert template?",
						"Template", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
					insertTemplate();

				goto ExitVoid;
			}

			try
			{
				doc.Tables[count].Select(); //select last table
				app.Selection.Copy();
				app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
				app.Selection.InsertBreak(7); //insert page break
				app.Selection.Paste();
				app.Selection.Tables[1].Cell(1, 4).Range.Text = insertDate();

				if (sett.cbIncrement) app.Selection.Tables[1].Cell(1, 2).Range.Text =
						getIncrementedCode(app.Selection.Tables[1].Cell(1, 2).Range.Text);

				app.Selection.Tables[1].Cell(1, 2).Select();
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnNewExpSelected_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			if (app.Selection.Tables[1].Columns.Count == tSize) //selected nested table instead of main table
			{
				app.Selection.Tables[1].Select();
				app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
			}

			try
			{
				int count = doc.Tables.Count;

				app.Selection.Tables[1].Select();
				app.Selection.Copy();
				doc.Tables[count].Select(); //select last table

				string lastReactionCode = app.Selection.Tables[1].Cell(1, 2).Range.Text;

				app.Selection.MoveRight(Word.WdUnits.wdCharacter, 1);
				app.Selection.InsertBreak(7); //insert page break
				app.Selection.Paste();
				app.Selection.Tables[1].Cell(1, 4).Range.Text = insertDate();

				if (sett.cbIncrement) app.Selection.Tables[1].Cell(1, 2).Range.Text =
						getIncrementedCode(lastReactionCode);

				app.Selection.Tables[1].Cell(1, 2).Select();
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnQuery_Click(object sender, RibbonControlEventArgs e)
		{
			if (QueryDialog.IsAlreadyOpened)
			{
				Application.OpenForms["QueryDialog"].Activate();
				return;
			}

			QueryDialog qDialog = new QueryDialog(this);

			qDialog.Show();
		}

		private void btnRemoveRows_Click(object sender, RibbonControlEventArgs e)
		{
			try
			{
				app.Selection.Rows.Delete();
			}
			catch
			{
			}
		}

		private void btnOpenNMR_Click(object sender, RibbonControlEventArgs e)
		{
			if (string.IsNullOrEmpty(sett.MNovaPath))
			{
				MessageBox.Show("Please, first, setup MestReNova path in settings.", "LabNotebookAddIn", MessageBoxButtons.OK, MessageBoxIcon.Information);
				return;
			}

			try
			{
				string sText = app.Selection.Text.Trim('\r', '\a');
				if (string.IsNullOrEmpty(sText)) return;
				if (app.Selection.Tables.Count < 1) return;

				string code = app.Selection.Tables[1].Cell(1, 2).Range.Text.Trim('\r', '\a'); //reaction code
				if (code.Length < 2) return;

				if (sText.StartsWith("*")) sText = sText.Substring(1);
				if (!(sText.Contains('\\') || sText.Contains('/'))) sText = sText.Insert(0, "\\");

				sText = code + sText.Trim().Replace('/', '\\');

				string dir = Path.GetDirectoryName(fullNameOfDoc);

				string pathToMNova = Path.Combine(dir, "NMR", sText);

				if (!(Directory.Exists(pathToMNova)))
				{
					MessageBox.Show($"Invalid directory path: {pathToMNova}", "LabNotebookAddIn", MessageBoxButtons.OK, MessageBoxIcon.Error);
					return;
				}

				using (Process p = new Process())
				{
					p.StartInfo.FileName = sett.MNovaPath;
					p.StartInfo.Arguments = $"\"{pathToMNova}\"";
					p.Start();
				}
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.Message, "LabNotebookAddIn", MessageBoxButtons.OK, MessageBoxIcon.Error);

				//throw;
			}
		}

		private void btnSwapCells_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			try
			{
				if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
				{
					MessageBox.Show("Select cells to swap.", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
					goto ExitVoid;
				}

				Point[] selCells = GetSelectedCells();
				//x - row index, y - column index

				int cellsCount = selCells.Length;

				if (cellsCount % 2 == 1)
				{
					MessageBox.Show("Select even number of cells!", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
					goto ExitVoid;
				}

				Word.Table t = app.Selection.Tables[1];

				if (cellsCount == 2) //switch these two
				{
					swapCells(selCells[0], selCells[1], ref t);
					Marshal.ReleaseComObject(t);
					goto ExitVoid;
				}

				int r = selCells[0].X, c = selCells[0].Y;

				Point[] sameRowCells = selCells.Where(p => p.X == r).ToArray();
				Point[] sameColCells = selCells.Where(p => p.Y == c).ToArray();

				if ((sameRowCells.Length < 2 && sameColCells.Length < 2) || sameRowCells.Length == cellsCount || sameColCells.Length == cellsCount)
				{
					MessageBox.Show("Wrong selection, can't swap cells.", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
					Marshal.ReleaseComObject(t);
					goto ExitVoid;
				}

				bool swapRows;

				if (sameRowCells.Length == sameColCells.Length)
				{
					swapRows = (MessageBox.Show("Ambiguous selection. Swap rows? Otherwise columns will be swapped.", "Information",
						MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes) ? true : false;

					//MessageBox.Show("Ambiguous selection.", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
					//Marshal.ReleaseComObject(t);
					//goto ExitVoid;
				}
				else
				{
					swapRows = (sameRowCells.Length > sameColCells.Length) ? true : false;
				}

				Point[] fistHalf, secondHalf;

				if (swapRows)
				{
					fistHalf = sameRowCells.OrderBy(p => p.Y).ToArray();
					secondHalf = selCells.Except(sameRowCells).OrderBy(p => p.Y).ToArray();
				}
				else
				{
					fistHalf = sameColCells.OrderBy(p => p.X).ToArray();
					secondHalf = selCells.Except(sameColCells).OrderBy(p => p.X).ToArray();
				}

				if (fistHalf.Length != secondHalf.Length)
				{
					MessageBox.Show("Wrong selection, can't swap cells.", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
					Marshal.ReleaseComObject(t);
					goto ExitVoid;
				}

				for (int i = 0; i < fistHalf.Length; i++)
				{
					swapCells(fistHalf[i], secondHalf[i], ref t);
				}

				Marshal.ReleaseComObject(t);
			}
			catch (Exception ex)
			{
				MessageBox.Show("Error occured while swapping cells:\n\n" + ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void getFiles(FTPclient client, string directory, string fileFilter, ref StringBuilder buffer)
		{
			var dirs = client.ListDirectoryDetail(directory);

			foreach (var item in dirs)
			{
				if (item.FileType == FTPfileInfo.DirectoryEntryTypes.Directory)
				{
					//buffer.AppendLine($"Directory  {item.FullName}  Date  {item.FileDateTime} ");
					getFiles(client, fSettings.formateFtpPath(item.FullName), fileFilter, ref buffer);
				}
				else
				{
					string extension = Path.GetExtension(item.FullName);
					if (string.Equals(Path.GetExtension(item.FullName), "." + fileFilter, StringComparison.CurrentCultureIgnoreCase))
					{
						buffer.AppendLine(item.FullName);
					}
					//Debug.WriteLine($"File  {item.FullName}  Date  {item.FileDateTime} ");
				}
			}
		}

		private void catchWebException(WebException ex)
		{
			bool catched = false;
			FtpWebResponse response = (FtpWebResponse)ex.Response;
			switch (ex.Status)
			{
				case WebExceptionStatus.ProtocolError: // error in username or password
					switch (response.StatusCode)
					{
						case FtpStatusCode.ActionNotTakenFilenameNotAllowed:
							MessageBox.Show("Directory doesn't exist. Please check the settings.\n" + ex.ToString(), "Protocol error", MessageBoxButtons.OK, MessageBoxIcon.Error);
							catched = true;
							break;

						case FtpStatusCode.NotLoggedIn:
							MessageBox.Show("Access denied, wrong username or/and password.\n" + ex.ToString(), "Protocol error", MessageBoxButtons.OK, MessageBoxIcon.Error);
							catched = true;
							break;
					}
					break;

				case WebExceptionStatus.ConnectFailure:
					MessageBox.Show("Connection failed, check internet connection.\n" + ex.ToString(), "Connection failure", MessageBoxButtons.OK, MessageBoxIcon.Error);
					catched = true;
					break;

				case WebExceptionStatus.Timeout:
					MessageBox.Show("Timeout has expired.\n" + ex.ToString(), "Timeout", MessageBoxButtons.OK, MessageBoxIcon.Error);
					catched = true;
					break;

				case WebExceptionStatus.NameResolutionFailure:
					MessageBox.Show("Error in host name.\n" + ex.ToString(), "Name resolution failure", MessageBoxButtons.OK, MessageBoxIcon.Error);
					catched = true;
					break;
			}

			if (!catched) MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
		}

		private void btnTest_Click(object sender, RibbonControlEventArgs e)
		{
			string hostName = "147.251.29.206";

			string user = "nick";
			string pwd = ">>";

			//FTPclient client = new FTPclient("147.251.29.206", "asds", decryptPassword(sett.entropy, sett.pwd));
			FTPclient client = new FTPclient(hostName, user, pwd);
			client.KeepAlive = true;
			//client.EnableSSL = true;
			client.UsePassive = true;

			try
			{
				FTPdirectory userDirs = client.ListDirectoryDetail(sett.UsersDirFtp);
				if (userDirs.Count < 1) return;

				foreach (FTPfileInfo userDir in userDirs) //for each user
				{
					string labNotebookDir = fSettings.formateFtpPath(userDir.FullName + "/" + sett.LabNotebookDirFtp);
					//labNotebookDir = labNotebookDir.Substring(0, labNotebookDir.Length - 1);
					if (userDir.FileType == FTPfileInfo.DirectoryEntryTypes.Directory && client.FtpDirectoryExists(labNotebookDir))
					{
						StringBuilder sb = new StringBuilder();

						//if (!labNotebookDir.StartsWith("/Users/Madea")) continue;

						getFiles(client, labNotebookDir, "ln", ref sb);

						Debug.Write(sb.ToString());
					}
				}
			}
			catch (WebException ex)
			{
				catchWebException(ex);
				//throw;
			}

			//var dirs = client.ListDirectoryDetail();

			//var dirsl = client.ListDirectoryDetail("/Group Meetings/");
			//var dirtsd  = client.ListDirectoryDetail("/Group Meetings/2017 Spring GMs/12-05-2017");

			//StringBuilder sb = new StringBuilder();

			//recursiveListing(client, "/", ref sb);

			//File.WriteAllText($@"C:\Users\Dominik\Desktop\test\{sett.HostName}.txt", sb.ToString());

			//Word.Table t = app.Selection.Tables[1];

			////t.PreferredWidthType = Word.WdPreferredWidthType.wdPreferredWidthPercent;
			////t.PreferredWidth = 100;

			//Word.Table tNested = t.Tables[1];

			//float w = t.Cell(3, 1).Width;

			//tNested.PreferredWidth = 0.97f*w;

			////t.Cell(3, 1).SetWidth(w - 100, Word.WdRulerStyle.wdAdjustNone);

			//Indigo indigo = new Indigo();
			//IndigoRenderer renderer = new IndigoRenderer(indigo);

			//indigo.setOption("render -output-format", "png");
			//indigo.setOption("render-margins", 10, 10);
			//indigo.setOption("render-coloring", true);
			//indigo.setOption("render-image-width", 1500);

			//string picPath = @"C:\Users\Dominik\Desktop\test\";

			//RenderReactionsInDocument(renderer, indigo, picPath);
		}

		private void grpFormat_DialogLauncherClick(object sender, RibbonControlEventArgs e)
		{
			FormatDocumentDialog fdd = new FormatDocumentDialog(this);

			fdd.ShowDialog();
		}

		private void insertDataTableTemplate(Word.Range where, int rows, string fontName)
		{
			Word.Table t = doc.Tables.Add(where, rows, tSize);

			t.AllowAutoFit = false;
			t.Rows.Alignment = Word.WdRowAlignment.wdAlignRowCenter;
			t.Range.Font.Name = fontName;
			t.Range.Font.Size = 9F;
			t.Range.ParagraphFormat.SpaceAfter = 0;

			for (int c = 1; c <= tSize; c++)
			{
				t.Columns[c].PreferredWidthType = Word.WdPreferredWidthType.wdPreferredWidthPercent;
				t.Columns[c].PreferredWidth = dataTableColWidths[c];
			}

			doc.Range(t.Cell(rows, 1).Range.Start, t.Cell(rows, 5).Range.End).Cells.Merge();
			doc.Range(t.Cell(rows, 2).Range.Start, t.Cell(rows, 3).Range.End).Cells.Merge();

			t.Cell(rows, 2).Range.Text = "Yield:";

			setupDataTableBorders(ref t);

			updateHeading(ref t, false); //fill heading

			t.Select();

			t.Range.ParagraphFormat.Alignment = Word.WdParagraphAlignment.wdAlignParagraphCenter;
			app.Selection.Cells.VerticalAlignment = Word.WdCellVerticalAlignment.wdCellAlignVerticalCenter;
		}

		private void insertTemplate()
		{
			optimizePerformance(false);

			string fontName = "Consolas";

			int rows = 9, columns = 7;
			float margin = 36; //narrow margin = 1.27 cm

			try
			{
				if (app.Selection.Tables.Count > 0)
				{
					MessageBox.Show("Please choose another location to insert, template table would merge with the selected table.", "Information",
						MessageBoxButtons.OK, MessageBoxIcon.Information);
					goto ExitVoid;
				}

				if (doc.PageSetup.LeftMargin != 36)
					if (MessageBox.Show("Do you want to set up narrow margins (1.27 cm for left, right, top and bottom margins)? It looks better, adds more space!", "Margins", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
						doc.PageSetup.LeftMargin = doc.PageSetup.RightMargin = doc.PageSetup.TopMargin = doc.PageSetup.BottomMargin = margin;

				Word.Table t = doc.Tables.Add(app.Selection.Range, rows, columns);
				//t.AllowAutoFit = false;
				t.Rows.Alignment = Word.WdRowAlignment.wdAlignRowCenter;
				t.Range.Font.Name = fontName;
				t.Range.Font.Size = 11F;
				t.Range.ParagraphFormat.SpaceAfter = 0;

				t.Select();
				app.Selection.Cells.VerticalAlignment = Word.WdCellVerticalAlignment.wdCellAlignVerticalCenter;
				app.Selection.ParagraphFormat.Alignment = Word.WdParagraphAlignment.wdAlignParagraphLeft;

				//setup borders
				t.Borders.InsideLineStyle = Word.WdLineStyle.wdLineStyleSingle; //single line is 50 pt width
				t.Borders.OutsideLineStyle = Word.WdLineStyle.wdLineStyleSingle;

				for (int r = 1; r <= rows; r++) //for each row
				{
					if (r < 7)
						t.Rows[r].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = thickLineWidth;

					t.Cell(r, 1).Borders[Word.WdBorderType.wdBorderLeft].LineWidth = THICKLineWidth;
					t.Cell(r, 1).Borders[Word.WdBorderType.wdBorderRight].LineWidth = thickLineWidth;
					t.Cell(r, columns).Borders[Word.WdBorderType.wdBorderRight].LineWidth = THICKLineWidth;

					if (r == 1)
					{
						t.Rows[1].Borders[Word.WdBorderType.wdBorderTop].LineWidth = THICKLineWidth;
						t.Cell(1, 6).Borders[Word.WdBorderType.wdBorderLeft].LineWidth = thickLineWidth; //Date border
					}
					if (r == rows)
					{
						t.Rows[rows].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = THICKLineWidth;
					}
					if (r == 5)
					{
						t.Rows[r].Borders[Word.WdBorderType.wdBorderBottom].LineWidth = Word.WdLineWidth.wdLineWidth050pt;
						t.Rows[r].Borders[Word.WdBorderType.wdBorderBottom].LineStyle = Word.WdLineStyle.wdLineStyleDashLargeGap;
					}
					t.Rows[r].HeightRule = Word.WdRowHeightRule.wdRowHeightExactly;
					t.Rows[r].Height = 14.2F; //1cm = 28.35 pt
				}

				t.Rows[rows].Height *= 3;

				//filling and merging

				//first row
				doc.Range(t.Cell(1, 1).Range.Start, t.Cell(1, 2).Range.End).Cells.Merge();
				doc.Range(t.Cell(1, 2).Range.Start, t.Cell(1, 4).Range.End).Cells.Merge();
				t.Cell(1, 1).Range.Text = "Reaction code";
				t.Cell(1, 1).Range.Bold = 1;
				t.Cell(1, 3).Range.Text = "Date";
				t.Cell(1, 3).Range.Bold = 1;
				t.Cell(1, 4).Range.Text = insertDate();
				t.Cell(1, 4).Range.Bold = 1;

				//second row
				t.Rows[2].Range.Cells.Merge();
				t.Cell(2, 1).Range.Text = "Reaction scheme\n";
				t.Cell(2, 1).Range.Bold = 1;
				doc.Range(t.Cell(2, 1).Range.End - 1, t.Cell(2, 1).Range.End).ParagraphFormat.Alignment = Word.WdParagraphAlignment.wdAlignParagraphCenter;
				//t.Cell(2, 1).HeightRule = Word.WdRowHeightRule.wdRowHeightAuto;

				//third row
				t.Rows[3].Range.Cells.Merge();
				//t.Cell(3, 1).HeightRule = Word.WdRowHeightRule.wdRowHeightAuto;
				//int startRng = t.Cell(3, 1).Range.Start;
				//insertDataTableTemplate(doc.Range(startRng, startRng), 5, THICKLineWidth, thickLineWidth, fontName); //insert data table

				doc.Range(doc.Range().End - 1, doc.Range().End).Select();
				app.Selection.Collapse(Word.WdCollapseDirection.wdCollapseEnd);
				app.Selection.TypeText("\n");

				var rng = app.Selection.Range;

				insertDataTableTemplate(rng, 5, fontName); //insert data table

				app.Selection.Cut();

				t.Cell(3, 1).Range.PasteSpecial();

				rng.Start -= 1;
				rng.Delete();

				//fourth row
				t.Cell(4, 1).Range.Text = "Literature";
				t.Cell(4, 1).Range.Bold = 1;
				doc.Range(t.Cell(4, 2).Range.Start, t.Cell(4, 7).Range.End).Cells.Merge();

				//fifth and sixth row
				t.Rows[5].Range.Cells.Merge();
				t.Cell(5, 1).Range.Text = "Procedure";
				t.Cell(5, 1).Range.Bold = 1;

				t.Rows[6].Range.Cells.Merge(); //Procedure window
				t.Cell(6, 1).Select();
				app.Selection.Cells.VerticalAlignment = Word.WdCellVerticalAlignment.wdCellAlignVerticalTop;

				//last rows
				doc.Range(t.Cell(7, 1).Range.Start, t.Cell(9, 1).Range.End).Select();
				app.Selection.Cells.Merge();
				doc.Range(t.Cell(7, 2).Range.Start, t.Cell(7, 3).Range.End).Select();
				app.Selection.Cells.Merge();

				doc.Range(t.Cell(7, 3).Range.Start, t.Cell(8, 4).Range.End).Select();
				app.Selection.Cells.Merge();
				doc.Range(t.Cell(7, 4).Range.Start, t.Cell(8, 5).Range.End).Select();
				app.Selection.Cells.Merge();
				doc.Range(t.Cell(7, 5).Range.Start, t.Cell(8, 6).Range.End).Select();
				app.Selection.Cells.Merge();
				doc.Range(t.Cell(7, 6).Range.Start, t.Cell(8, 7).Range.End).Select();
				app.Selection.Cells.Merge();

				doc.Range(t.Cell(7, 2).Range.Start, t.Cell(9, 7).Range.End).Select();
				//app.Selection.Cells.VerticalAlignment = Word.WdCellVerticalAlignment.wdCellAlignVerticalCenter; //center alignment in cells
				app.Selection.ParagraphFormat.Alignment = Word.WdParagraphAlignment.wdAlignParagraphCenter;

				t.Cell(7, 1).Range.Text = "Chemical analysis";
				t.Cell(7, 1).Range.Bold = 1;
				t.Cell(7, 2).Range.Text = "NMR";
				t.Cell(7, 3).Range.Text = "HRMS";
				t.Cell(7, 4).Range.Text = "GC/HPLC";
				t.Cell(7, 5).Range.Text = "Melting point";
				t.Cell(7, 6).Range.Text = "Other";

				t.Cell(8, 2).Range.Text = "1H";
				doc.Range(t.Cell(8, 2).Range.Start, t.Cell(8, 2).Range.Start + 1).Font.Superscript = 1;

				t.Cell(8, 3).Range.Text = "13C";
				doc.Range(t.Cell(8, 3).Range.Start, t.Cell(8, 3).Range.Start + 2).Font.Superscript = 1;

				fitTableToPage(ref t);
			}
			catch (Exception ex)
			{
				MessageBox.Show("Error occured while inserting lab notebook template:\n\n" + ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private bool isBracket(char symbol)
		{
			return symbol == ')' || symbol == '(' || symbol == ']' || symbol == '[' || symbol == '}' || symbol == '{';
		}

		private void RenderReactionsInDocument(IndigoRenderer renderer, Indigo indigo, string path)
		{
			foreach (string file in Directory.EnumerateFiles(path))
			{
				File.Delete(file);
			}

			int count = doc.Tables.Count;

			if (count < 1) return; //no table in document

			for (int i = 1; i <= count; i++) // for each table in document
			{
				var shape = doc.Tables[i].Cell(2, 1).Range.InlineShapes[1];

				string reactionCode = doc.Tables[i].Cell(1, 2).Range.Text.Trim('\r', '\a', ' ', '\t');

				Word.OLEFormat ole = shape.OLEFormat;

				if (ole.ProgID != "ChemDraw.Document.6.0") continue;

				ole.DoVerb(Word.WdOLEVerb.wdOLEVerbHide); //open chemdraw

				CambridgeSoft.ChemOffice.Interop.ChemDrawControl.Document obj = ole.Object;

				//obj.

				int components = obj.Groups.Count;

				if (components < 1) continue;

				IndigoObject rxn = LoadReaction(obj, indigo);

				if (rxn == null) // Indigo reaction reading exception, manualy load molecules
				{
					for (int j = 1; j <= components; j++) // for each component
					{
						var compound = obj.Groups.Item(j);
						double mr = compound.Objects.MolecularWeight;

						if (mr <= 0)
							continue;

						compound.Objects.Copy(); //copy compound to cliboard

						MemoryStream data = (MemoryStream)Clipboard.GetData("SMILES");
						byte[] bytes = data.ToArray();
						string smiles = Encoding.UTF8.GetString(bytes).Substring(0, bytes.Length - 1);

						try
						{
							IndigoObject molecule = indigo.loadMolecule(smiles);

							renderer.renderToFile(molecule, Path.Combine(path, string.Format("{0}-mol{1:00}.png", reactionCode, j)));
						}
						catch (IndigoException ex)
						{
							Debug.WriteLine("IndigoException - loading molecule:\n" + ex.Message);
							continue;
						}
					}
					continue;
				}

				renderer.renderToFile(rxn, Path.Combine(path, string.Format("{0}.png", reactionCode)));

				Debug.WriteLine(100 * i / count);

				Application.DoEvents();
				obj.Close();
			}
		}

		private void selectTableMsg()
		{
			MessageBox.Show("Select table of interest (cursor can be placed in it).", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
		}

		//x - row index, y - columns index
		private void swapCells(Point cell1, Point cell2, ref Word.Table t)
		{
			Word.Range range1 = t.Cell(cell1.X, cell1.Y).Range;
			range1.End -= 1;
			Word.Range range2 = t.Cell(cell2.X, cell2.Y).Range;
			range2.End -= 1;

			if (range1.Start == range1.End && range2.Start == range2.End) //both cells empty
				return;

			if (range1.Start == range1.End) //range1 is empty
			{
				range2.Cut();
				range1.Paste();
				return;
			}
			else if (range2.Start == range2.End)
			{
				range1.Cut();
				range2.Paste();
				return;
			}

			range1.Cut();
			doc.Range(range2.End, range2.End).Paste();
			range2.Cut();
			range1.Paste();
		}

		private string typeToString(Type dispatchType)
		{
			StringBuilder buffer = new StringBuilder();

			buffer.AppendLine("AssemblyName: " + dispatchType.AssemblyQualifiedName);

			buffer.AppendLine("Module: " + dispatchType.Module.ScopeName + "  Module version ID: " +
				dispatchType.Module.ModuleVersionId);

			buffer.AppendLine("Guid: " + dispatchType.GUID.ToString());

			buffer.AppendLine("\n\n Methods:");

			foreach (var method in dispatchType.GetMethods())
			{
				buffer.AppendLine(method.ToString());
			}

			buffer.AppendLine("\n\n Fields:");

			foreach (var item in dispatchType.GetFields())
			{
				buffer.AppendLine(item.ToString());
			}

			buffer.AppendLine("\n\n Properties:");

			foreach (var item in dispatchType.GetProperties())
			{
				buffer.AppendLine(item.ToString());
			}

			buffer.AppendLine("\n\n Events:");

			foreach (var item in dispatchType.GetEvents())
			{
				buffer.AppendLine(item.ToString());
			}

			buffer.AppendLine("\n\n All members:");

			foreach (var item in dispatchType.GetMembers())
			{
				buffer.AppendLine(item.ToString());
			}

			buffer.AppendLine("\n\nDefined types: " + dispatchType.Assembly.GetTypes());

			foreach (var item in dispatchType.Assembly.GetTypes())
			{
				buffer.AppendLine(item.ToString());
			}

			string text = buffer.ToString();

			return text;
		}

		#endregion Formatting

		#region Export

		private void btnCopyData_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			//doc = app.ActiveDocument;

			if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
			{
				selectTableMsg();
				goto ExitVoid;
			}

			try
			{
				Word.Table t = app.Selection.Tables[1];

				if (t.Columns.Count != tSize) goto ExitVoid;

				int r, c, tr;
				r = app.Selection.Cells[1].RowIndex;
				c = app.Selection.Cells[1].ColumnIndex;
				tr = t.Rows.Count; //total rows in table

				if (r == 1) goto ExitVoid;

				string[,] strData = getTableStringArray(ref t);
				if (strData == null) goto ExitVoid;
				t = app.Selection.Tables[1]; //table has been deleted, so it has to be asigned again

				t.Cell(r, c).Select();
				app.Selection.Collapse();

				if (r == tr) //yield row
				{
					string y = strData[tr - 1, 3];
					if (!string.IsNullOrEmpty(y))
					{
						string[] split = y.Split(' ');

						if (split.Length == 1) goto ExitVoid;
						double Y = parse(split[0]);
						if (Y == -1) goto ExitVoid;

						Clipboard.SetText(formatNumber(Y, sett.dYE, sett.sY) + " %");
					}
					goto ExitVoid;
				}

				double[,] tData = getTableDataArray(strData);

				double w, cm, eq, n, m, V;

				//fill variables
				w = tData[r, sett.CIw]; cm = tData[r, sett.CIcm];
				eq = tData[r, sett.CIeq]; n = tData[r, sett.CIn];
				m = tData[r, sett.CIm]; V = tData[r, sett.CIV];

				if (n == -1) goto ExitVoid; //no n in that row, exit

				bool micro = true;
				string[] mCell = strData[0, sett.CIm - 1].Split('/');
				if (mCell[1] == "g")
					micro = false;

				string nU, mU, VU; //n, m and V units
				nU = ((micro) ? " µ" : " m") + "mol";
				mU = ((micro) ? " m" : " ") + "g";
				VU = ((micro) ? " µ" : " m") + "L";

				//define internal format number function for n, m and V formatting
				Func<double, int, bool, Variables, string> _formatNumber = (num, decPlc, sigFig, var) =>
				{
					string curEnding = string.Empty;
					switch (var)
					{
						case Variables.n:
							curEnding = nU;
							break;

						case Variables.m:
							curEnding = mU;
							break;

						case Variables.V:
							curEnding = VU;
							break;
					}

					if (sett.cbExportUnits)
					{
						if (micro)
						{
							if (num < 0.1)
							{
								switch (var)
								{
									case Variables.n:
										curEnding = " nmol";
										break;

									case Variables.m:
										curEnding = " µg";
										break;

									case Variables.V:
										curEnding = " nL";
										break;
								}
								num *= 1000;
							}
							else if (num >= 1000)
							{
								switch (var)
								{
									case Variables.n:
										curEnding = " mmol";
										break;

									case Variables.m:
										curEnding = " g";
										break;

									case Variables.V:
										curEnding = " mL";
										break;
								}
								num /= 1000;
							}
						}
						else
						{
							if (num < 0.1)
							{
								switch (var)
								{
									case Variables.n:
										curEnding = " µmol";
										break;

									case Variables.m:
										curEnding = " mg";
										break;

									case Variables.V:
										curEnding = " µL";
										break;
								}
								num *= 1000;
							}
							else if (num >= 1000)
							{
								switch (var)
								{
									case Variables.n:
										curEnding = " mol";
										break;

									case Variables.m:
										curEnding = " kg";
										break;

									case Variables.V:
										curEnding = " L";
										break;
								}
								num /= 1000;
							}
						}
					}
					return formatNumber(num, decPlc, sigFig) + curEnding;
				};

				if (sett.includeEq && eq == -1) goto ExitVoid;
				string eqIncld = (sett.includeEq) ? formatNumber(eq, sett.deqE, sett.seq) + " eq, " : string.Empty;

				string text, temp;

				if (V > 0)
				{
					temp = eqIncld + _formatNumber(V, sett.dVE, sett.sV, Variables.V) + ", " + _formatNumber(n, sett.dnE, sett.sn, Variables.n) + ")";
					if (w > 0)
						text = "(" + formatNumber(w, sett.dwE, sett.sw) + " %, " + temp;
					else if (cm > 0)
						text = "(" + formatNumber(cm, sett.dcmE, sett.scm) + " M, " + temp;
					else
						text = "(" + temp;
					Clipboard.SetText(text);
				}
				else if (m > 0)
				{
					temp = eqIncld + _formatNumber(m, sett.dmE, sett.sm, Variables.m) + ", " + _formatNumber(n, sett.dnE, sett.sn, Variables.n) + ")";
					if (w > 0)
						text = "(" + formatNumber(w, sett.dwE, sett.sw) + " %, " + temp;
					else
						text = "(" + temp;
					Clipboard.SetText(text);
				}
			}
			catch
			{
			}

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnPublish_Click(object sender, RibbonControlEventArgs e)
		{
			if (string.IsNullOrEmpty(doc.Path))
			{
				try
				{
					doc.Save();
				}
				catch
				{
				}
				if (string.IsNullOrEmpty(doc.Path)) return;
			}

			string name = doc.Name;
			//string filename = Path.GetFileNameWithoutExtension(name);
			string fullPath = doc.FullName;

			string docxFileName = Path.GetFileNameWithoutExtension(name) +
				((sett.PublishDate) ? "_" + DateTime.Today.ToString("yyyy-MM-dd") : string.Empty) + Path.GetExtension(name);

			string pdfFileName = Path.GetFileNameWithoutExtension(name) +
				((sett.PublishDate) ? "_" + DateTime.Today.ToString("yyyy-MM-dd") : string.Empty) + ".pdf";

			//string path = doc.Path;

			justSavingInPublish = true; //doc.SaveAs2 method executes DocumentBeforeSave event

			try
			{
				if (sett.PublishFtp)
				{
					if (string.IsNullOrEmpty(sett.HostName) || string.IsNullOrEmpty(sett.UserName) || sett.pwd == null || sett.entropy == null)
					{
						MessageBox.Show("Set ftp server settings.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
						justSavingInPublish = false;
						return;
					}

					if (sett.PublishPdf)
					{
						string tempFilePath = Path.GetTempFileName();
						doc.SaveAs2(tempFilePath, Word.WdSaveFormat.wdFormatPDF); //save temp pdf file
																				  //upload temp file to ftp server and delete it
						uploadFtpFile(sett.HostName, sett.UserName, decryptPassword(sett.entropy, sett.pwd), sett.FtpPath, pdfFileName, tempFilePath);
						File.Delete(tempFilePath); //delete temp file
					}

					if (sett.PublishDocx)
					{
						if (!doc.Saved) doc.Save();
						doc.Close();
						uploadFtpFile(sett.HostName, sett.UserName, decryptPassword(sett.entropy, sett.pwd), sett.FtpPath, docxFileName, fullPath);
						app.Documents.Open(fullPath);
					}
				}
				else //export to disk
				{
					if (string.IsNullOrEmpty(sett.DiskPath) || !Directory.Exists(sett.DiskPath))
					{
						MessageBox.Show("Set correctly export path in settings.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
						justSavingInPublish = false;
						return;
					}

					if (sett.PublishPdf) doc.SaveAs2(sett.DiskPath + "\\" + pdfFileName, Word.WdSaveFormat.wdFormatPDF);

					if (sett.PublishDocx)
					{
						if (!doc.Saved) doc.Save();
						doc.Close();
						File.Copy(fullPath, sett.DiskPath + "\\" + docxFileName, true);
						app.Documents.Open(fullPath);

						//doc.SaveAs2(sett.DiskPath + "\\" + name, Word.WdSaveFormat.wdFormatDocument);
					}
				}
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			justSavingInPublish = false;

			////using (WebClient client = new WebClient())
			////{
			////	client.Credentials = new NetworkCredential(user, pwd);
			////	client.UploadFile(hostFilePath, "STOR", filename);
			////}
		}

		private void cbEq_Click(object sender, RibbonControlEventArgs e)
		{
			sett.includeEq = cbEq.Checked;
			sett.Save();
		}

		private void cbExportUnits_Click(object sender, RibbonControlEventArgs e)
		{
			sett.cbExportUnits = cbExportUnits.Checked;
			sett.Save();
		}

		#endregion Export

		#region Calculation

		public string[,] getTableStringArray(ref Word.Table t)
		{
			string rawText = t.ConvertToText("\t", false).Text;

			try
			{
				doc.Undo(); //need to undo one step in document, because word actually converts the data table to text
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error undo method");
				return null;
			}

			rawText = rawText.Substring(0, rawText.Length - 1); //get rid of last '\r' character

			string[] tRows = rawText.Split('\r');
			int rows = tRows.Length;

			string[,] data = new string[rows, tSize];

			string[] tRow = new string[tSize];

			try
			{
				for (int r = 0; r < rows - 1; r++) //for each row
				{
					tRow = tRows[r].Split('\t');
					for (int c = 0; c < tSize; c++) //for each column
					{
						data[r, c] = tRow[c];
					}
				}

				tRow = tRows[rows - 1].Split('\t'); //parse last row - yield row
				data[rows - 1, 2] = tRow[2]; // setup pract yield to be in 3rd column, last row
				data[rows - 1, 3] = tRow[3]; // setup % yield to be in 4rd column, last row
			}
			catch (IndexOutOfRangeException)
			{
				MessageBox.Show("Don't use cartridge return (\\r) or new line (\\n) characters in cells! Please remove them.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);

				//TODO -- remove \r characters
				return null;
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error in getTableStringArray method.", MessageBoxButtons.OK, MessageBoxIcon.Error);
				return null;
			}

			return data;
		}

		private void btnM1_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			Word.Table t;
			int r, c, tr;
			double[,] tData;

			if (!init(out t, out r, out c, out tr, out tData)) goto ExitVoid;

			double w, cm, mr, eq, n, ro, m, V;

			//fill variables
			w = tData[r, sett.CIw]; cm = tData[r, sett.CIcm];
			mr = tData[r, sett.CImr]; eq = tData[r, sett.CIeq];
			n = tData[r, sett.CIn]; ro = tData[r, sett.CIro];
			m = tData[r, sett.CIm]; V = tData[r, sett.CIV];

			if (c == sett.CIw) //switch syntax doesn't work here, complier doesn't like it, because CIx variables are not constants
			{
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }
				if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }

				calculateTable(tr, r, eq, n, ref t, ref tData, false);
			}
			else if (c == sett.CIcm || c == sett.CImr)
			{
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }

				calculateTable(tr, r, eq, n, ref t, ref tData, false);
			}
			else if (c == sett.CIeq)
			{
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }

				calculateTable(tr, r, eq, n, ref t, ref tData, false);
			}
			else if (c == sett.CIn)
			{
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }

				calculateTable(tr, r, eq, n, ref t, ref tData, false);
			}
			else if (c == sett.CIro)
			{
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }
				if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }

				calculateTable(tr, r, eq, n, ref t, ref tData, false);
			}
			else if (c == sett.CIm)
			{
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }
				if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }

				n = ((w == -1) ? 100 : w) * m * 10 / mr;
				V = (ro == -1) ? -1 : m / ro;

				formatVariables(w, -1, mr, eq, n, ro, m, V, ref t, r);

				calculateTable(tr, r, eq, n, ref t, ref tData, true);
			}
			else if (c == sett.CIV)
			{
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }

				if (cm > 0)  //calculating with molar concentration
				{
					n = V * cm;
					m = (mr > 0) ? n * mr / 1000 : -1;

					formatVariables(-1, cm, mr, eq, n, -1, m, V, ref t, r);
				}
				else
				{
					if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }
					if (ro == -1) { t.Cell(r, sett.CIro).Select(); goto ExitVoid; }

					m = ro * V;
					n = ((w > 0) ? w : 100) * 10 * m / mr;

					formatVariables(w, -1, mr, eq, n, ro, m, V, ref t, r);
				}

				calculateTable(tr, r, eq, n, ref t, ref tData, true);
			}
			else
				goto ExitVoid;

			ExitVoid:
			optimizePerformance(true);
		}

		private void btnM2_Click(object sender, RibbonControlEventArgs e)
		{
			optimizePerformance(false);

			Word.Table t;
			int r, c, tr;
			double[,] tData;

			if (!init(out t, out r, out c, out tr, out tData)) goto ExitVoid;

			double w, cm, mr, eq, n, ro, m, V;

			double eq2, n2;
			eq2 = n2 = -1;

			bool changeEq = false;

			for (int ri = 2; ri < tr; ri++) //for each row except last row
			{
				if (ri == r) continue;
				n2 = tData[ri, sett.CIn];
				eq2 = tData[ri, sett.CIeq];
				if (n2 > 0 && eq2 > 0) { changeEq = true; break; }
			}

			//fill variables
			w = tData[r, sett.CIw]; cm = tData[r, sett.CIcm];
			mr = tData[r, sett.CImr]; eq = tData[r, sett.CIeq];
			n = tData[r, sett.CIn]; ro = tData[r, sett.CIro];
			m = tData[r, sett.CIm]; V = tData[r, sett.CIV];

			if (c == sett.CIw) //switch syntax doesn't work, complier doesn't like it, because CIx variables are not constants
			{
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }
				eq = (changeEq) ? n * eq2 / n2 : eq;
				calculateRow(r, w, cm, mr, eq, n, ro, out m, out V, ref t);
			}
			else if (c == sett.CIcm)
			{
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }
				eq = (changeEq) ? n * eq2 / n2 : eq;
				calculateRow(r, w, cm, mr, eq, n, ro, out m, out V, ref t);
			}
			else if (c == sett.CImr)
			{
				if (m == -1) { t.Cell(r, sett.CIm).Select(); goto ExitVoid; }
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }
				eq = (changeEq) ? n * eq2 / n2 : eq;
				calculateRow(r, w, cm, mr, eq, n, ro, out m, out V, ref t);
			}
			else if (c == sett.CIeq) //changes all row according to eq when changeEq == true, there is at least one record (eq and n) in data table
			{
				if (!changeEq) goto ExitVoid;
				n = n2 * eq / eq2;
				calculateRow(r, w, cm, mr, eq, n, ro, out m, out V, ref t);
			}
			else if (c == sett.CIn)
			{
				eq = (changeEq) ? n * eq2 / n2 : eq;
				calculateRow(r, w, cm, mr, eq, n, ro, out m, out V, ref t);
			}
			else if (c == sett.CIro)
			{
				//if (eq == -1) { t.Cell(r, CIeq).Select(); goto ExitVoid; }
				if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }
				if (n == -1) { t.Cell(r, sett.CIn).Select(); goto ExitVoid; }

				V = (ro == -1) ? -1 : m / ro;
				eq = (changeEq) ? n * eq2 / n2 : eq;

				formatVariables(w, -1, mr, eq, n, ro, m, V, ref t, r);
			}
			else if (c == sett.CIm)
			{
				if (eq == -1) { t.Cell(r, sett.CIeq).Select(); goto ExitVoid; }
				if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }

				n = ((w == -1) ? 100 : w) * m * 10 / mr;
				V = (ro == -1) ? -1 : m / ro;

				eq = (changeEq) ? n * eq2 / n2 : eq;

				formatVariables(w, -1, mr, eq, n, ro, m, V, ref t, r);
			}
			else if (c == sett.CIV)
			{
				if (cm > 0)  //calculating with molar concentration
				{
					n = V * cm;
					m = (mr > 0) ? n * mr / 1000 : -1;
					eq = (changeEq) ? n * eq2 / n2 : eq;

					formatVariables(-1, cm, mr, eq, n, -1, m, V, ref t, r);
				}
				else
				{
					if (mr == -1) { t.Cell(r, sett.CImr).Select(); goto ExitVoid; }
					if (ro == -1) { t.Cell(r, sett.CIro).Select(); goto ExitVoid; }

					m = ro * V;
					n = ((w > 0) ? w : 100) * 10 * m / mr;
					eq = (changeEq) ? n * eq2 / n2 : eq;

					formatVariables(w, -1, mr, eq, n, ro, m, V, ref t, r);
				}
			}
			else
				goto ExitVoid;

			ExitVoid:
			optimizePerformance(true);
		}

		private void calculateRow(int r, double w, double cm, double mr, double eq, double n, double ro, out double m, out double V, ref Word.Table t)
		{
			if (mr > 0)
			{
				if (w > 0)
				{
					m = n * mr / (w * 10);
					V = (ro == -1) ? -1 : m / ro;
					formatVariables(w, -1, mr, eq, n, ro, m, V, ref t, r);
				}
				else if (cm > 0)
				{
					m = n * mr / 1000;
					V = n / cm;
					formatVariables(-1, cm, mr, eq, n, -1, m, V, ref t, r);
				}
				else //no w and cm in that row ri
				{
					m = n * mr / 1000;
					V = (ro == -1) ? -1 : m / ro;
					formatVariables(-1, -1, mr, eq, n, ro, m, V, ref t, r);
				}
			}
			else
			{
				m = -1;
				V = (cm == -1) ? -1 : n / cm;
				formatVariables(-1, cm, -1, eq, n, -1, -1, V, ref t, r);
			}
		}

		private void calculateTable(int tr, int r, double eqOrig, double nOrig, ref Word.Table t, ref double[,] tData, bool skipRow = true)
		{
			double practYield = -1;

			for (int ri = 2; ri < tr; ri++) //for each row except last row
			{
				if (ri == r && skipRow) continue;

				double w, cm, mr, eq, n, ro, m, V;

				//fill variables
				w = tData[ri, sett.CIw];
				cm = tData[ri, sett.CIcm];
				mr = tData[ri, sett.CImr];
				eq = tData[ri, sett.CIeq];
				ro = tData[ri, sett.CIro];

				if (eq == -1) //no equivalent or eq <= 0 -> skip row, nonsense
					continue;

				n = nOrig * eq / eqOrig;

				calculateRow(ri, w, cm, mr, eq, n, ro, out m, out V, ref t);

				if (ri == tr - 1) practYield = m;
			}

			if (r == tr - 1 && skipRow)
			{
				string text = t.Cell(r, sett.CIm).Range.Text.Trim('\r', '\a');
				practYield = parse(text);
			}
			calculateYield(ref t, practYield, tData[tr, 3], tr);
		}

		private void calculateYield(ref Word.Table t, double theorY, double practY, int tr)
		{
			if (practY > 0 && theorY > 0) //calculate yield if table contains theoretical and practical yield
			{
				double y = practY * 100 / theorY;
				t.Cell(tr, 3).Range.Text = formatNumber(practY, sett.dm, sett.sm);
				t.Cell(tr, 4).Range.Text = formatNumber(y, sett.dY, sett.sY) + " %";
			}
			else
				t.Cell(tr, 4).Range.Text = string.Empty;
		}

		//not trivial to round number to significant figures; for positive numbers
		private string formatNumber(double number, int places, bool sigFig)
		{
			//Math.Ceiling(Math.Log10(Math.Abs(value)) - 1); //true exponenent
			//Math.Floor(Math.Log10(Math.Abs(value))); //true exponenent, the same result as above

			if (sigFig)
			{
				double scale = Math.Pow(10, Math.Floor(Math.Log10(Math.Abs(number))) + 1); //10^(true exponent + 1)
				double sclDig = Math.Pow(10, places); //round
				double x = number * sclDig / scale;
				x = x - Math.Truncate(x) < 0.5 ? Math.Truncate(x) : Math.Truncate(x) + 1; // round itself, round value / scale

				double rounded = scale * x / sclDig; //rounded number to sigFigures significant figures

				//rescale again, exponent might changed after rounding, for example, for number 0.9999 (3 sig dig), correct number is 1.00, but only above code would produce 1.000
				scale = Math.Pow(10, Math.Floor(Math.Log10(Math.Abs(rounded))) + 1);
				x = rounded * sclDig / scale;
				x = x - Math.Truncate(x) < 0.5 ? Math.Truncate(x) : Math.Truncate(x) + 1; // round itself, round value / scale

				rounded = scale * x / sclDig;

				//number of final decimal places to round: dp = sigFigures - 1 - trueExponent

				int dp = places - (int)Math.Log10(scale); //final decimal places

				// if processing number is larger than 1e+5 or less than 1e-3, use exponential output or final dp is > 5
				if (rounded > 1e+5 || rounded < 1e-3 || dp > 5)
					return rounded.ToString(string.Format("0.{0}E+0", new string('0', places - 1)), CultureInfo.InvariantCulture);

				return rounded.ToString("#,0." + new string('0', Math.Max(0, dp)), nFormat);
			}
			if ((number > 1e+5 || number < 1e-3 || places > 5) && number != 0) // if processing number is larger than 1e+5 or less than 1e-3, use exponential output
				return number.ToString(string.Format("0.{0}E+0", new string('0', places)), CultureInfo.InvariantCulture);

			return number.ToString("#,0." + new string('0', places), nFormat);
		}

		private void formatVariables(double w, double cm, double mr, double eq, double n, double ro, double m, double V, ref Word.Table t, int r)
		{
			t.Cell(r, sett.CIw).Range.Text = (w > 0) ? formatNumber(w, sett.dw, sett.sw) : "-";
			t.Cell(r, sett.CIcm).Range.Text = (cm > 0) ? formatNumber(cm, sett.dcm, sett.scm) : "-";
			t.Cell(r, sett.CImr).Range.Text = (mr > 0) ? formatNumber(mr, sett.dmr, sett.smr) : string.Empty;
			t.Cell(r, sett.CIeq).Range.Text = (eq > 0) ? formatNumber(eq, sett.deq, sett.seq) : string.Empty;
			t.Cell(r, sett.CIro).Range.Text = (ro > 0) ? formatNumber(ro, sett.dro, sett.sro) : "-";
			t.Cell(r, sett.CIn).Range.Text = (n > 0) ? formatNumber(n, sett.dn, sett.sn) : string.Empty;
			t.Cell(r, sett.CIm).Range.Text = (m > 0) ? formatNumber(m, sett.dm, sett.sm) : string.Empty;
			t.Cell(r, sett.CIV).Range.Text = (V > 0) ? formatNumber(V, sett.dV, sett.sV) : string.Empty;
		}

		private double[,] getTableDataArray(string[,] strData)
		{ //vba has array counting from 1 to size, so in order to return compatible array, a one column and one row is added
			int rows = strData.GetUpperBound(0) + 1;
			double[,] output = new double[rows + 1, tSize + 1];
			for (int r = 1; r <= rows; r++)
			{
				for (int c = 1; c <= tSize; c++)
					output[r, c] = parse(strData[r - 1, c - 1]);
			}
			return output;
		}

		private bool init(out Word.Table t, out int r, out int c, out int tr, out double[,] tData)
		{
			t = null; r = c = tr = 0; tData = null;

			try
			{
				//doc = app.ActiveDocument;

				if (!app.Selection.Information[Word.WdInformation.wdWithInTable]) //if cursor is not in table, exit void
					return false;

				t = app.Selection.Tables[1];

				if (t.Columns.Count != tSize) return false;

				if (app.Selection.Cells.Count > 1) return false; ///TODO formate text

				r = app.Selection.Cells[1].RowIndex;
				c = app.Selection.Cells[1].ColumnIndex;
				tr = t.Rows.Count; //total rows in table

				if (r == 1) return false;  //selected first row

				string[,] strData = getTableStringArray(ref t);

				t = app.Selection.Tables[1]; //table has been deleted, so it has to be asigned again

				if (strData == null) { t.Cell(r, c).Select(); app.Selection.Collapse(); return false; }
				tData = getTableDataArray(strData);

				t.Cell(r, c).Select();

				if (tData[r, c] == -1)
					return false;

				app.Selection.Collapse();

				if (r == tr)
				{
					calculateYield(ref t, tData[tr - 1, sett.CIm], tData[tr, 3], tr); //calculate yield and return false -> goto ExitVoid
					return false;
				}
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error occured in init method");
			}
			return true;
		}

		private double parse(string str) //if str is positive non-zero number, function returns it as double, otherwise returns -1
		{
			if (string.IsNullOrEmpty(str))
				return -1;
			string temp = str.Replace(',', '.').Replace(" ", string.Empty);
			double output;

			//added culture settings for problems with parsing numbers
			if (!double.TryParse(temp, NumberStyles.Number | NumberStyles.AllowExponent, CultureInfo.InvariantCulture, out output))
				return -1;
			if (output <= 0)
				return -1;
			return output;
		}

		#endregion Calculation

		#region Misc

		private void btnAbout_Click(object sender, RibbonControlEventArgs e)
		{
			if (AboutBox.IsAlreadyOpened)
			{
				Application.OpenForms["AboutBox"].Activate();
				return;
			}

			AboutBox aBox = new AboutBox();
			aBox.StartPosition = FormStartPosition.CenterScreen;
			aBox.Show();
		}

		private void btnSettings_Click(object sender, RibbonControlEventArgs e)
		{
			if (fSettings.IsAlreadyOpened)
			{
				Application.OpenForms["fSettings"].Activate();
				return;
			}

			fSettings fSett = new fSettings(ref sett);
			fSett.StartPosition = FormStartPosition.CenterScreen;

			fSett.Show();
			fSett.FormClosed += (send, ev) =>
			{
				if (fSett.DialogResult == DialogResult.OK)
					loadSettings();
			};
		}

		private void btn_format_clicked(object sender, RibbonControlEventArgs e)
		{
			Word.Range rng = app.Selection.Range;

			if (rng.Start == rng.End || string.IsNullOrEmpty(rng.Text)) return;
			string text = rng.Text.Replace("\a", string.Empty).Replace("\n", string.Empty).Replace("\r", string.Empty);

			string[] numbers = text.Split(new char[] { ',' });

			string[] fNumbers = new string[numbers.Length];

			for (int i = 0; i < numbers.Length; i++)
			{
				double num;
				if (!double.TryParse(numbers[i].Trim(), NumberStyles.AllowDecimalPoint, CultureInfo.InvariantCulture,  out num))
				{
					fNumbers[i] = numbers[i];
					continue;
				}

				int places = 0;

				fNumbers[i] = formatNumber(num, places, false).Replace(" ", "");  // remove spaces

			}

			rng.Text = string.Join(", ", fNumbers);


			//if (rng.Start == rng.End || string.IsNullOrEmpty(rng.Text)) return;

			//string text = rng.Text.Replace("\a", string.Empty).Replace("\n", string.Empty).Replace("\r", string.Empty);

			//if (text == string.Empty) return;

			//rng.Text = text;

			//int start;// = rng.Start;
			//int end;// = rng.End;

			//start = rng.Start;
			//end = rng.End;

			//text += 'A';

			//bool wasLetter = false;

			//int startIdx = -1, endIdx = -1;

			//for (int i = 0; i < text.Length; i++)
			//{
			//	bool isDigit = char.IsDigit(text[i]);

			//	//wasLetter = (!wasLetter && !isDigit) ? true : wasLetter;

			//	wasLetter = (wasLetter || isDigit) ? wasLetter : true;

			//	//wasLetter = char.IsLetter(text[i]) || isBracket(text[i]);

			//	//if (isDigit && startIdx == -1) startIdx = i;
			//	//if (!isDigit) endIdx = i;

			//	if (isDigit && wasLetter && startIdx == -1) startIdx = i;
			//	if (!isDigit && wasLetter) endIdx = i;

			//	wasLetter = char.IsLetter(text[i]) || isBracket(text[i]);

			//	if (endIdx > startIdx && startIdx != -1 && endIdx != -1)
			//	{
			//		rng.Start = start + startIdx;
			//		rng.End = start + endIdx;
			//		rng.Font.Subscript = 1; //this line makes the selected text subscripted
			//		endIdx = startIdx = -1;
			//	}
			//}





		}

		#endregion Misc

		//private double Round(double value, int digits)
		//{
		//	double scl = Math.Pow(10, digits);
		//	double x = value * scl;
		//	x = x - Math.Truncate(x) < 0.5 ? Math.Truncate(x) : Math.Truncate(x) + 1;
		//	return x / scl;
		//}
	}

	public class MySettings : JsonSettings
	{
		public override string FileName { get; set; }

		#region Settings

		//setup default values
		//Column indexes of variables

		public int CIw { get; set; } = 2;
		public int CIcm { get; set; } = 3;
		public int CImr { get; set; } = 4;
		public int CIeq { get; set; } = 5;
		public int CIn { get; set; } = 6;
		public int CIro { get; set; } = 7;
		public int CIm { get; set; } = 8;
		public int CIV { get; set; } = 9;

		//decPlaces default values

		public int dw { get; set; } = 2;
		public int dcm { get; set; } = 2;
		public int dmr { get; set; } = 3;
		public int deq { get; set; } = 3;
		public int dn { get; set; } = 4;
		public int dro { get; set; } = 3;
		public int dm { get; set; } = 3;
		public int dV { get; set; } = 3;
		public int dY { get; set; } = 1;

		//decPalcesE decPlaces to export default values

		public int dwE { get; set; } = 1;
		public int dcmE { get; set; } = 1;
		public int deqE { get; set; } = 2;
		public int dnE { get; set; } = 3;
		public int dmE { get; set; } = 3;
		public int dVE { get; set; } = 2;
		public int dYE { get; set; } = 1;

		//sigFigures default values

		public bool sw { get; set; } = false;
		public bool scm { get; set; } = false;
		public bool smr { get; set; } = false;
		public bool seq { get; set; } = true;
		public bool sn { get; set; } = true;
		public bool sro { get; set; } = false;
		public bool sm { get; set; } = true;
		public bool sV { get; set; } = true;
		public bool sY { get; set; } = false;

		public int dateType { get; set; } = 0;
		public int sumSpace { get; set; } = 610;
		public bool includeEq { get; set; } = false;

		public Byte[] entropy { get; set; } = null;
		public Byte[] pwd { get; set; } = null;

		public string HostName { get; set; } = "147.251.29.206";
		public string UserName { get; set; } = string.Empty;
		public string FtpPath { get; set; } = string.Empty;
		public string DiskPath { get; set; } = string.Empty;

		public bool PublishFtp { get; set; } = false;
		public bool ExportWhenSaved { get; set; } = false;

		public int SelectedTab { get; set; } = 0;

		public bool cbExportUnits { get; set; } = true;
		public bool cbIncrement { get; set; } = true;

		public bool DontCloseChemDraw { get; set; } = true;
		public bool PublishDocx { get; set; } = true;
		public bool PublishPdf { get; set; } = false;
		public bool PublishDate { get; set; } = true;

		public string WorkingDir { get; set; } = string.Empty;
		public string UsersDirFtp { get; set; } = string.Empty;
		public string LabNotebookDirFtp { get; set; } = string.Empty;
		public string MNovaPath { get; set; } = string.Empty;

		#endregion Settings

		//Step 3: Override parent's constructors
		public MySettings()
		{
			Assembly assemblyInfo = Assembly.GetExecutingAssembly();
			string assemblyLocation = assemblyInfo.Location;

			FileName = Path.Combine(Path.GetDirectoryName(assemblyLocation), "config.json");
		}

		public MySettings(string fileName) : base(fileName)
		{
		}
	}

	public static class DllImports
	{
		//public struct RECT
		//{
		//	public int left; public int top; public int right; public int bottom;
		//}

		//[DllImport("gdi32.dll")]
		//internal static extern IntPtr
		//CreateEnhMetaFile(IntPtr hdcRef, IntPtr zero, ref RECT rc, IntPtr zero2);

		[DllImport("gdi32.dll")]
		internal static extern IntPtr
		CloseEnhMetaFile(IntPtr hdc);

		[DllImport("user32.dll", CharSet = CharSet.Auto, ExactSpelling = true)]
		public static extern bool OpenClipboard(IntPtr hWndNewOwner);

		[DllImport("user32.dll", SetLastError = true)]
		public static extern uint RegisterClipboardFormat(string lpszFormat);

		[DllImport("user32.dll", CharSet = CharSet.Auto, ExactSpelling = true)]
		public static extern bool CloseClipboard();

		[DllImport("user32.dll", CharSet = CharSet.Auto, ExactSpelling = true)]
		public static extern IntPtr GetClipboardData(uint format);

		[DllImport("user32.dll", CharSet = CharSet.Auto, ExactSpelling = true)]
		public static extern bool IsClipboardFormatAvailable(uint format);

		[DllImport("user32.dll")]
		[return: MarshalAs(UnmanagedType.Bool)]
		internal static extern bool EmptyClipboard();

		[DllImport("user32.dll")]
		internal static extern IntPtr
		SetClipboardData(uint format, IntPtr h);

		[DllImport("Kernel32.dll", SetLastError = true)]
		public static extern IntPtr GlobalLock(IntPtr hMem);

		[DllImport("Kernel32.dll", SetLastError = true)]
		[return: MarshalAs(UnmanagedType.Bool)]
		public static extern bool GlobalUnlock(IntPtr hMem);

		[DllImport("Kernel32.dll", SetLastError = true)]
		public static extern int GlobalSize(IntPtr hMem);

		[DllImport("user32.dll")]
		internal static extern IntPtr GetDC(IntPtr hwnd);

		[DllImport("user32.dll")]
		internal static extern Int32
		ReleaseDC(IntPtr hwnd, IntPtr hdc);

		[DllImport("gdi32.dll")]
		internal static extern bool
		DeleteObject(IntPtr hObject);

		[DllImport("gdi32.dll")]
		internal static extern IntPtr
		CreateCompatibleDC(IntPtr hdc);

		[DllImport("gdi32.dll")]
		internal static extern int DeleteDC(IntPtr hdc);

		[DllImport("gdi32.dll")]
		internal static extern IntPtr
		SelectObject(IntPtr hdc, IntPtr hgdiobj);

		[DllImport("gdi32.dll")]
		internal static extern int BitBlt(IntPtr
		hdcDst, int xDst, int yDst, int w, int h, IntPtr hdcSrc, int xSrc, int ySrc, int rop);

		//[DllImport("gdi32.dll")]
		//public static extern bool EnumEnhMetaFile(IntPtr hdc, IntPtr HenhMetafile, EnhMetaFileCallBack lpEnhMetaFunc,
		//IntPtr lpData, RECT lpRect);

		public static uint CF_ENHMETAFILE = 14;

		//public delegate void EnhMetaFileCallBack(string message);
	}
}