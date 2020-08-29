using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace LabNotebookAddin
{
	public partial class fInfoDialog : Form
	{
		private static bool _opened = false;

		public static bool IsAlreadyOpened { get { return _opened; } }

		//private Experiment exp;

		public void loadNewData (Experiment exp)
		{
			lblBuild.Text = exp.Owner.Builded.ToShortDateString();
			lblName.Text = exp.Owner.Name;
			lblUser.Text = exp.Owner.User;
			lblExperiments.Text = (exp.Owner.OriginalCount == null) ? exp.Owner.Count.ToString() : exp.Owner.OriginalCount.ToString();
			lblExpCode.Text = exp.ExpCode;
			lblDate.Text = exp.Date.ToShortDateString();
			lblPageNumber.Text = exp.PageNumberInWordNotebook.ToString();
			lblFullPath.Text = exp.Owner.FullPath;

			tbProcedure.Text = exp.Procedure;
			tbLiterature.Text = exp.Literature;
		}

		public fInfoDialog()
		{
			InitializeComponent();
			_opened = true;
			//this.exp = exp;
			this.ShowIcon = false;

			this.Text = Assembly.GetExecutingAssembly().GetName().Name + " - Experiment Info Dialog";

			//loadNewData(exp);
		}

		private void fInfoDialog_Load(object sender, EventArgs e)
		{

		}

		private void fInfoDialog_FormClosed(object sender, FormClosedEventArgs e)
		{
			_opened = false;
		}
	}
}
