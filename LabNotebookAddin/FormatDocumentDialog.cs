using System;
using System.ComponentModel;
using System.Reflection;
using System.Windows.Forms;
using Word = Microsoft.Office.Interop.Word;

namespace LabNotebookAddin
{
	public partial class FormatDocumentDialog : Form
	{
		public const int max = 100000;
		public const int constValue = 100;
		public const int k = constValue - max;
		private BackgroundWorker bw;
		private MainRibbon mr;
		//private delegate void EnableCheckBoxes(bool done);

		private void SetCheckBoxes (bool done)
		{
			cbFitToPage.Enabled = cbDataTable.Enabled = cbSpaces.Enabled = cbPreferredWidth.Enabled = cbFormatExperimentTable.Enabled = done;
		}

		public FormatDocumentDialog(MainRibbon _mr)
		{
			InitializeComponent();

			mr = _mr;

			this.Text = Assembly.GetExecutingAssembly().GetName().Name + " - Format Document Dialog";
			this.FormBorderStyle = FormBorderStyle.FixedDialog;
			this.StartPosition = FormStartPosition.CenterScreen;

			cbFitToPage.Checked = true;
			cbDataTable.Checked = false;
			cbFormatExperimentTable.Checked = false;
			cbSpaces.Checked = true;
			cbPreferredWidth.Checked = true;

			progressBarCtor(pBar);

			bw = new BackgroundWorker();
			bw.WorkerReportsProgress = true;
			bw.WorkerSupportsCancellation = true;
		}

		public static void progressBarCtor(ProgressBar pBar)
		{
			pBar.MarqueeAnimationSpeed = 10000;
			pBar.Value = FormatDocumentDialog.constValue;
			pBar.Maximum = FormatDocumentDialog.max;
		}

		public void FormatDocument()
		{
			if (!cbFitToPage.Checked && !cbDataTable.Checked && !cbSpaces.Checked && !cbPreferredWidth.Checked && !cbFormatExperimentTable.Checked) { Close(); return; }

			int count = mr.doc.Tables.Count;

			if (count < 1) { Close(); return; } //no table in document

			//EnableCheckBoxes delCheckBoxes = (bool done) => { cbFitToPage.Enabled = cbDataTable.Enabled = cbSpaces.Enabled = done; };

			bw.ProgressChanged += (send, eArgs) =>
			{
				changeProgress(eArgs.ProgressPercentage);
			};

			bw.RunWorkerCompleted += (send, eArgs) =>
			{
				mr.optimizePerformance(true);

				Invoke(new Action(() =>
				{
					SetCheckBoxes(false);
				}));
				Close();
			};

			bw.DoWork += (send, eArgs) =>
			{
				mr.optimizePerformance(false);
				Invoke(new Action(() =>
				{
					SetCheckBoxes(false);
				}));
				try
				{
					Word.Table t = null;

					Action<int> changeWidths = new Action<int>((int i) =>
					{
						Word.Table tNested = mr.doc.Tables[i].Cell(3, 1).Tables[1];

						t.PreferredWidthType = Word.WdPreferredWidthType.wdPreferredWidthPercent;
						t.PreferredWidth = 100f;

						float tWidth = t.Cell(3, 1).Width; //tNested.preferredWidthType percent doesn't work if width > 100 % -> caused shrinking of the table because tNested.preferredWidthType remained wdPreferredWidthPoints
						tNested.PreferredWidthType = Word.WdPreferredWidthType.wdPreferredWidthPoints;
						tNested.PreferredWidth = 0.975f * tWidth;
					});

					for (int i = 1; i <= count; i++)
					{ 
						if (bw.CancellationPending) return;

						t = mr.doc.Tables[i];

						if (cbSpaces.Checked && i < count) // for all tables except last table
						{
							int start = t.Range.End;
							Word.Range range = mr.doc.Range(start, start);

							range.InsertBreak(Word.WdBreakType.wdPageBreak);

							Word.Range deleteRange = mr.doc.Range(range.End - 1, mr.doc.Tables[i + 1].Range.Start);

							deleteRange.Delete();
						}

						if (cbFormatExperimentTable.Checked)
						{
							mr.formatMainTable(ref t);
						}

						if (cbDataTable.Checked)
						{
							mr.formatDataTable(ref t);
						}

						if (cbPreferredWidth.Checked)
						{
							changeWidths(i);
						}

						if (cbFitToPage.Checked) mr.fitTableToPage(ref t);

						int progressValue = (100 * i) / (((cbPreferredWidth.Checked) ? 2 : 1) * count);

						bw.ReportProgress(progressValue);
					}

					if (cbPreferredWidth.Checked) // obviously it doesn't word after one pass, some data table widths aren't changed, so the changeWidth action has to be repeated
					{
						for (int i = 1; i <= count; i++)
						{
							if (bw.CancellationPending) return;

							t = mr.doc.Tables[i];

							changeWidths(i);
							bw.ReportProgress((100 * (i + count)) / (2 * count));
						}
					}
				}
				catch (Exception ex)
				{
					MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				}
			};

			bw.RunWorkerAsync();
		}

		public void changeProgress(int value)
		{
			pBar.Maximum = (int)(constValue / (value / 100.0 + 0.0001));
			//pBar.Maximum = (int) ((double) k * value / 100D + max);
		}

		private void btnCancel_Click(object sender, EventArgs e)
		{
			Close();
		}

		private void btnFormat_Click(object sender, EventArgs e)
		{
			if (!bw.IsBusy) FormatDocument();
		}

		private void FormatDocumentDialog_FormClosing(object sender, FormClosingEventArgs e)
		{
			if (bw.IsBusy)
			{
				e.Cancel = true;
				bw.CancelAsync();
			}
		}

		private void FormatDocumentDialog_Load(object sender, EventArgs e)
		{ }
	}
}