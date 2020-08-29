using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
//using System.Configuration;

namespace LabNotebookAddin
{
	public partial class fSettings : Form
	{
		private static bool _opened = false;

		public static bool IsAlreadyOpened { get { return _opened; } }

		private MySettings sett;

		public fSettings( ref MySettings sett)
		{
			_opened = true;

			InitializeComponent();

			this.Text = Assembly.GetExecutingAssembly().GetName().Name + " - Settings";

			tbPwd.MaxLength = 30;
			tbPwd.PasswordChar = '\u25CF'; //black circle

			this.sett = sett;

			tabCrl.SelectedIndex = sett.SelectedTab;

			//Columns order tab

			nudw.Value = sett.CIw;
			nudcm.Value = sett.CIcm;
			nudmr.Value = sett.CImr;
			nudeq.Value = sett.CIeq;
			nudn.Value = sett.CIn;
			nudro.Value = sett.CIro;
			nudm.Value = sett.CIm;
			nudV.Value = sett.CIV;

			//Rounding tab

			nuddw.Value = sett.dw;
			nuddcm.Value = sett.dcm;
			nuddmr.Value = sett.dmr;
			nuddeq.Value = sett.deq;
			nuddn.Value = sett.dn;
			nuddro.Value = sett.dro;
			nuddm.Value = sett.dm;
			nuddV.Value = sett.dV;
			nuddY.Value = sett.dY;

			cbw.Checked = sett.sw;
			cbcm.Checked = sett.scm;
			cbmr.Checked = sett.smr;
			cbeq.Checked = sett.seq;
			cbn.Checked = sett.sn;
			cbro.Checked = sett.sro;
			cbm.Checked = sett.sm;
			cbV.Checked = sett.sV;
			cbY.Checked = sett.sY;

			//perform checked changed voids to correctly setup minimums of numeric up downs
			cbw_CheckedChanged(cbw, null);
			cbcm_CheckedChanged(cbcm, null);
			cbmr_CheckedChanged(cbmr, null);
			cbeq_CheckedChanged(cbeq, null);
			cbn_CheckedChanged(cbn, null);
			cbro_CheckedChanged(cbro, null);
			cbm_CheckedChanged(cbm, null);
			cbV_CheckedChanged(cbV, null);
			cbY_CheckedChanged(cbY, null);

			nuddwE.Value = sett.dwE;
			nuddcmE.Value = sett.dcmE;
			nuddeqE.Value = sett.deqE;
			nuddnE.Value = sett.dnE;
			nuddmE.Value = sett.dmE;
			nuddVE.Value = sett.dVE;
			nuddYE.Value = sett.dYE;

			//Other tab

			cbDateFormat.SelectedIndex = sett.dateType;

			nudSumSpace.Value = sett.sumSpace;

			tbMNova.Text = sett.MNovaPath;

			//Publish tab

			rbFtp.Checked = sett.PublishFtp;
			rbFtp_CheckedChanged(null, null);

			tbDiskPath.Text = sett.DiskPath;

			tbHost.Text = sett.HostName;
			tbUser.Text = sett.UserName;
			tbFtpPath.Text = sett.FtpPath;

			if (sett.pwd != null && sett.entropy != null) tbPwd.Text = MainRibbon.decryptPassword(sett.entropy, sett.pwd);

			cbPublishPdf.Checked = sett.PublishPdf;
			cbPublishDocx.Checked = sett.PublishDocx;
			cbPublishDate.Checked = sett.PublishDate;
			cbPublishSave.Checked = sett.ExportWhenSaved;
			cbIncrement.Checked = sett.cbIncrement;
			cbCloseChemDraw.Checked = sett.DontCloseChemDraw;


			//Search tab
			tbWorkingDir.Text = sett.WorkingDir;
			tbUsersDirFtp.Text = sett.UsersDirFtp;
			tbNotebooksFolderFtp.Text = sett.LabNotebookDirFtp;


		}

		private bool correctCI()
		{
			int product = 362880; // = 9!
			int sum = 44; // = 2+3+4+5+6+7+8+9

			int _sum = (int)(nudw.Value + nudcm.Value + nudmr.Value + nudeq.Value + nudn.Value + nudro.Value + nudm.Value + nudV.Value);
			int _product = (int)(nudw.Value * nudcm.Value * nudmr.Value * nudeq.Value * nudn.Value * nudro.Value * nudm.Value * nudV.Value);

			if (sum == _sum && product == _product)
				return true;

			return false;
		}

		public static string formateFtpPath(string path)
		{
			string retCharacter = "/";

			if (string.IsNullOrEmpty(path)) return retCharacter;

			string temp = path.Replace('\\', '/'); // replace any \ to /

			string[] split = temp.Split(new char[] { '/' }, StringSplitOptions.RemoveEmptyEntries);

			if (split.Length < 1) return retCharacter;

			temp = retCharacter;

			for (int i = 0; i < split.Length; i++)
			{
				temp += split[i] + "/";
			}

			return temp;
		}

		public static bool checkUri(string hostName, string folderName)
		{
			string ftpPath;
			ftpPath = string.Format(@"ftp://{0}{1}{2}", hostName, folderName, "file.ext");

			try
			{
				Uri uri = new Uri(ftpPath);
			}
			catch (UriFormatException)
			{
				return false;
			}
			return true;
		}

		private void btnSave_Click(object sender, EventArgs e)
		{
			if (!correctCI())
			{
				MessageBox.Show("Error in columns order, please correct it.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				tabCrl.SelectedIndex = 0; //select first columns order tab
				return;
			}

			//check publish docx and pdf checkboxes
			if (!(cbPublishPdf.Checked | cbPublishDocx.Checked))
			{
				MessageBox.Show("At least one file must be selected (docx or pdf).", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				tabCrl.SelectedIndex = 2; //select third tab
				return;
			}

			//Columns order tab

			sett.CIw = (int)nudw.Value;
			sett.CIcm = (int)nudcm.Value;
			sett.CImr = (int)nudmr.Value;
			sett.CIeq = (int)nudeq.Value;
			sett.CIn = (int)nudn.Value;
			sett.CIro = (int)nudro.Value;
			sett.CIm = (int)nudm.Value;
			sett.CIV = (int)nudV.Value;

			//Rounding tab

			sett.dw = (int)nuddw.Value;
			sett.dcm = (int)nuddcm.Value;
			sett.dmr = (int)nuddmr.Value;
			sett.deq = (int)nuddeq.Value;
			sett.dn = (int)nuddn.Value;
			sett.dro = (int)nuddro.Value;
			sett.dm = (int)nuddm.Value;
			sett.dV = (int)nuddV.Value;
			sett.dY = (int)nuddY.Value;

			sett.dwE = (int)nuddwE.Value;
			sett.dcmE = (int)nuddcmE.Value;
			sett.deqE = (int)nuddeqE.Value;
			sett.dnE = (int)nuddnE.Value;
			sett.dmE = (int)nuddmE.Value;
			sett.dVE = (int)nuddVE.Value;
			sett.dYE = (int)nuddYE.Value;

			sett.sw = cbw.Checked;
			sett.scm = cbcm.Checked;
			sett.smr = cbmr.Checked;
			sett.seq = cbeq.Checked;
			sett.sn = cbn.Checked;
			sett.sro = cbro.Checked;
			sett.sm = cbm.Checked;
			sett.sV = cbV.Checked;
			sett.sY = cbY.Checked;

			//Other tab

			sett.dateType = cbDateFormat.SelectedIndex;

			sett.sumSpace = (int)nudSumSpace.Value;

			sett.MNovaPath = tbMNova.Text;

			//Publish tab

			sett.PublishFtp = rbFtp.Checked;

			string formattedFtpPath = fSettings.formateFtpPath(tbFtpPath.Text);
			sett.FtpPath = formattedFtpPath;

			if (rbFtp.Checked && !checkUri(tbHost.Text, formattedFtpPath))
			{
				MessageBox.Show("A mistake has been made in host name, please correct it.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				tabCrl.SelectedIndex = 2; //select publish tab
				tbHost.Select();
				return;
			}

			sett.DiskPath = tbDiskPath.Text;

			sett.HostName = tbHost.Text;
			sett.UserName = tbUser.Text;

			byte[] pwd = null;
			byte[] entropy = null;

			if (!string.IsNullOrEmpty(tbPwd.Text)) MainRibbon.encryptPassword(tbPwd.Text, out entropy, out pwd);

			sett.entropy = entropy;
			sett.pwd = pwd;

			sett.PublishPdf = cbPublishPdf.Checked;
			sett.PublishDocx = cbPublishDocx.Checked;
			sett.PublishDate = cbPublishDate.Checked;
			sett.ExportWhenSaved = cbPublishSave.Checked;
			sett.cbIncrement = cbIncrement.Checked;
			sett.DontCloseChemDraw = cbCloseChemDraw.Checked;

			//Search tab
			sett.WorkingDir = tbWorkingDir.Text;
			sett.UsersDirFtp = fSettings.formateFtpPath(tbUsersDirFtp.Text);
			sett.LabNotebookDirFtp = tbNotebooksFolderFtp.Text;

			sett.Save();

			DialogResult = DialogResult.OK;
			Close();
		}


		private void btnClose_Click(object sender, EventArgs e)
		{
			Close();
		}

		private void fSettings_FormClosing(object sender, FormClosingEventArgs e)
		{
			sett.SelectedTab = tabCrl.SelectedIndex; //save selected tab
			sett.Save();
			_opened = false;
		}

		private void cbw_CheckedChanged(object sender, EventArgs e)
		{
			nuddwE.Minimum = nuddw.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbcm_CheckedChanged(object sender, EventArgs e)
		{
			nuddcmE.Minimum = nuddcm.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbmr_CheckedChanged(object sender, EventArgs e)
		{
			nuddmr.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbeq_CheckedChanged(object sender, EventArgs e)
		{
			nuddeqE.Minimum = nuddeq.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbn_CheckedChanged(object sender, EventArgs e)
		{
			nuddnE.Minimum = nuddn.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbro_CheckedChanged(object sender, EventArgs e)
		{
			nuddro.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbm_CheckedChanged(object sender, EventArgs e)
		{
			nuddmE.Minimum = nuddm.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbV_CheckedChanged(object sender, EventArgs e)
		{
			nuddVE.Minimum = nuddV.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void cbY_CheckedChanged(object sender, EventArgs e)
		{
			nuddYE.Minimum = nuddY.Minimum = (((CheckBox)sender).Checked) ? 1 : 0;
		}

		private void rbFtp_CheckedChanged(object sender, EventArgs e)
		{
			rbDisk.Checked = !rbFtp.Checked;
			tbFtpPath.Enabled = tbPwd.Enabled = tbUser.Enabled = tbHost.Enabled = rbFtp.Checked;
		}

		private void btnSetFolder_Click(object sender, EventArgs e)
		{
			FolderBrowserDialog fbd = new FolderBrowserDialog();

			fbd.SelectedPath = tbDiskPath.Text;
			fbd.Description = "Select folder, where your laboratory notebook will be exported as pdf.";

			fbd.ShowNewFolderButton = true;

			if (fbd.ShowDialog() == DialogResult.OK)
			{
				tbDiskPath.Text = fbd.SelectedPath;
			}

		}

		private void btnAssemblyInfo_Click(object sender, EventArgs e)
		{
			//Get the assembly information
			Assembly assemblyInfo = Assembly.GetExecutingAssembly();

			//Location is where the assembly is run from 
			string assemblyLocation = assemblyInfo.Location;

			//CodeBase is the location of the ClickOnce deployment files
			Uri uriCodeBase = new Uri(assemblyInfo.CodeBase);
			string ClickOnceLocation = Path.GetDirectoryName(uriCodeBase.LocalPath.ToString());

			MessageBox.Show("Assembly location:\n" + assemblyLocation + "\n\nClickOnceLocation:\n" + ClickOnceLocation);
		}

		private void btnChemDrawObj_Click(object sender, EventArgs e)
		{
			try
			{
				Type t = Type.GetTypeFromProgID("ChemDraw.Document.6.0");

				dynamic obj = Activator.CreateInstance(t);
                

				/*obj.Application.Visible = true;

				//int b = 2;

				//var menu = obj.Application.MenuBars.Item(1);

				//short menusCount = menu.Menus.Count;
				
				
				//dynamic[] menus = new dynamic[menusCount];
				//string[] captions = new string[menusCount];
				//dynamic[][] MenuItems = new dynamic[menusCount][];
				//int[] menuItemsCount = new int[menusCount];

				//for (int i = 0; i < menusCount; i++)
				//{
				//	menus[i] = menu.Menus.Item(i+1);
				//	captions[i] = menus[i].caption;
				//	menuItemsCount[i] = menus[i].MenuItems.Count;
				//	MenuItems[i] = new dynamic[menuItemsCount[i]];
				//	for (int j = 0; j < menuItemsCount[i]; j++)
				//	{
				//		MenuItems[i][j] = menus[i].MenuItems.Item(j+1);
				//	}
				//}

				//MenuItems[0][0].caption = "asdasd";*/



				//CambridgeSoft.ChemOffice.Interop.ChemDraw.Document doc;



				//doc.Application.Visible = false;





				//var ctrl = new CambridgeSoft.ChemOffice.Interop.AxChemDrawControl.AxChemDrawCtl();


				//doc.BeginInit();

				//

				//doc.Show();

				//doc.ShowAboutBox();

				//doc.DoVerb(-1);


				//obj.Activate();
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
			}

			//tNested.Rows.Add()
			//var obj = Clipboard.GetDataObject();

			//string[] objects = obj.GetFormats();

			//int i = 0;

			//dynamic obj = Activator.CreateInstance(Type.GetTypeFromCLSID(new Guid("41BA6D21-A02E-11CE-8FD9-0020AFD1F20C")));
			//dynamic obj = Activator.CreateInstance(Type.GetTypeFromCLSID(new Guid("4A6F3C59-D184-49FA-9189-AF42BEDFE5E4")));
			//dynamic obj = Activator.CreateInstance(Type.GetTypeFromCLSID(new Guid("9e3a4685-0a8f-420d-a73e-4a37b83830db")));

			//Type fromCLSID = Type.GetTypeFromCLSID(new Guid("4A6F3C59-D184-49FA-9189-AF42BEDFE5E4"));



			//var shape = app.Selection.InlineShapes[1];

			//Word.OLEFormat ole = shape.OLEFormat;

			//object verb = Word.WdOLEVerb.wdOLEVerbShow;

			//ole.DoVerb(verb);

			//object obj = ole.Object;

			////Type tt = obj.GetType();

			////object oo = Activator.CreateInstance(tt);



			//Type dispatchType = DispatchUtility.GetType(obj, true);

			//Guid tmpGuid = dispatchType.GUID;

			////string sProgID;
			////ProgIDFromCLSID(ref tmpGuid, out sProgID);

			////dispatchType.

			////Type tt = Type.GetTypeFromProgID(sProgID, true);
			//try
			//{


			//	dynamic novel = (dynamic)Activator.CreateInstance(dispatchType);
			//}
			//catch (Exception ex)
			//{

			//	throw;
			//}

			////Activator.CreateInstance()

			////Activator.CreateInstance(dispatchType, BindingFlags.InvokeMethod, ,,)



			//// Create a COM object using the GUID passed...
			//System.ComponentModel.GuidConverter gc = new System.ComponentModel.GuidConverter();
			//System.Guid guid = (Guid)gc.ConvertFromString("4A6F3C59-D184-49FA-9189-AF42BEDFE5E4");
			//Type consoleType = Type.GetTypeFromCLSID(guid);
			//object objConsole = Activator.CreateInstance(consoleType);



			////Type dispatchType = DispatchUtility.GetType(obj, true);

			////string text = typeToString(dispatchType);


		}

		private void fSettings_Activated(object sender, EventArgs e)
		{
			WindowState = FormWindowState.Normal;
		}

		private void btnWorkingDir_Click(object sender, EventArgs e)
		{
			FolderBrowserDialog fbd = new FolderBrowserDialog();

			fbd.SelectedPath = tbWorkingDir.Text;
			fbd.Description = "Select the application working directory used for saving other laboratory notebooks.";

			fbd.ShowNewFolderButton = true;

			if (fbd.ShowDialog() == DialogResult.OK)
			{
				tbWorkingDir.Text = fbd.SelectedPath;
			}
		}

		private void fSettings_Load(object sender, EventArgs e)
		{

		}

		private void btnMNovaFInd_Click(object sender, EventArgs e)
		{
			// Create an instance of the open file dialog box.
			OpenFileDialog ofd = new OpenFileDialog();

			// Set filter options and filter index.
			ofd.Filter = "MestReNova|MestReNova.exe|Executable files (*.exe)|*.exe";
			ofd.FilterIndex = 1;
			ofd.Multiselect = false;

			//ofd.InitialDirectory = gplPATH == string.Empty ? string.Empty : Path.GetDirectoryName(gplPATH);

			if (ofd.ShowDialog() == DialogResult.OK)
			{
				tbMNova.Text = ofd.FileName;
			}
		}
	}
}
