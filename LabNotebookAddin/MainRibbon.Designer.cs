namespace LabNotebookAddin
{
	partial class MainRibbon : Microsoft.Office.Tools.Ribbon.RibbonBase
	{
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		public MainRibbon()
			: base(Globals.Factory.GetRibbonFactory())
		{
			InitializeComponent();
		}

		/// <summary> 
		/// Clean up any resources being used.
		/// </summary>
		/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Component Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			Microsoft.Office.Tools.Ribbon.RibbonDialogLauncher ribbonDialogLauncherImpl1 = this.Factory.CreateRibbonDialogLauncher();
			this.tab1 = this.Factory.CreateRibbonTab();
			this.group1 = this.Factory.CreateRibbonGroup();
			this.btnM1 = this.Factory.CreateRibbonButton();
			this.btnM2 = this.Factory.CreateRibbonButton();
			this.grpFormat = this.Factory.CreateRibbonGroup();
			this.btnNewExp = this.Factory.CreateRibbonSplitButton();
			this.btnLastOne = this.Factory.CreateRibbonButton();
			this.btnNewExpSelected = this.Factory.CreateRibbonButton();
			this.btnInsertTemplate = this.Factory.CreateRibbonButton();
			this.btnFitToPage = this.Factory.CreateRibbonButton();
			this.btnChangeUnits = this.Factory.CreateRibbonButton();
			this.btnHeading = this.Factory.CreateRibbonButton();
			this.btnTransfer = this.Factory.CreateRibbonButton();
			this.btnCalculateMasses = this.Factory.CreateRibbonButton();
			this.btnFormula = this.Factory.CreateRibbonButton();
			this.btnRemoveRows = this.Factory.CreateRibbonButton();
			this.btnSwapCells = this.Factory.CreateRibbonButton();
			this.group3 = this.Factory.CreateRibbonGroup();
			this.btnPublish = this.Factory.CreateRibbonButton();
			this.btnCopyData = this.Factory.CreateRibbonButton();
			this.cbEq = this.Factory.CreateRibbonCheckBox();
			this.cbExportUnits = this.Factory.CreateRibbonCheckBox();
			this.group4 = this.Factory.CreateRibbonGroup();
			this.btnSettings = this.Factory.CreateRibbonButton();
			this.btnAbout = this.Factory.CreateRibbonButton();
			this.btnOpenNMR = this.Factory.CreateRibbonButton();
			this.group5 = this.Factory.CreateRibbonGroup();
			this.btnChemDrawInfo = this.Factory.CreateRibbonButton();
			this.btnTest = this.Factory.CreateRibbonButton();
			this.btnQuery = this.Factory.CreateRibbonButton();
			this.button1 = this.Factory.CreateRibbonButton();
			this.tab1.SuspendLayout();
			this.group1.SuspendLayout();
			this.grpFormat.SuspendLayout();
			this.group3.SuspendLayout();
			this.group4.SuspendLayout();
			this.group5.SuspendLayout();
			this.SuspendLayout();
			// 
			// tab1
			// 
			this.tab1.ControlId.ControlIdType = Microsoft.Office.Tools.Ribbon.RibbonControlIdType.Office;
			this.tab1.Groups.Add(this.group1);
			this.tab1.Groups.Add(this.grpFormat);
			this.tab1.Groups.Add(this.group3);
			this.tab1.Groups.Add(this.group4);
			this.tab1.Groups.Add(this.group5);
			this.tab1.Label = "Lab Notebook";
			this.tab1.Name = "tab1";
			// 
			// group1
			// 
			this.group1.Items.Add(this.btnM1);
			this.group1.Items.Add(this.btnM2);
			this.group1.Label = "Calculation";
			this.group1.Name = "group1";
			// 
			// btnM1
			// 
			this.btnM1.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnM1.Label = "Recalculate table";
			this.btnM1.Name = "btnM1";
			this.btnM1.OfficeImageId = "TableSelect";
			this.btnM1.ShowImage = true;
			this.btnM1.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnM1_Click);
			// 
			// btnM2
			// 
			this.btnM2.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnM2.Label = "Recalculate row";
			this.btnM2.Name = "btnM2";
			this.btnM2.OfficeImageId = "TableRowSelect";
			this.btnM2.ShowImage = true;
			this.btnM2.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnM2_Click);
			// 
			// grpFormat
			// 
			this.grpFormat.DialogLauncher = ribbonDialogLauncherImpl1;
			this.grpFormat.Items.Add(this.btnNewExp);
			this.grpFormat.Items.Add(this.btnFitToPage);
			this.grpFormat.Items.Add(this.btnChangeUnits);
			this.grpFormat.Items.Add(this.btnHeading);
			this.grpFormat.Items.Add(this.btnTransfer);
			this.grpFormat.Items.Add(this.btnCalculateMasses);
			this.grpFormat.Items.Add(this.btnFormula);
			this.grpFormat.Items.Add(this.btnRemoveRows);
			this.grpFormat.Items.Add(this.btnSwapCells);
			this.grpFormat.Label = "Formatting";
			this.grpFormat.Name = "grpFormat";
			this.grpFormat.DialogLauncherClick += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.grpFormat_DialogLauncherClick);
			// 
			// btnNewExp
			// 
			this.btnNewExp.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnNewExp.Items.Add(this.btnLastOne);
			this.btnNewExp.Items.Add(this.btnNewExpSelected);
			this.btnNewExp.Items.Add(this.btnInsertTemplate);
			this.btnNewExp.Label = "New experiment";
			this.btnNewExp.Name = "btnNewExp";
			this.btnNewExp.OfficeImageId = "SmartArtAddShape";
			this.btnNewExp.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnNewExp_Click);
			// 
			// btnLastOne
			// 
			this.btnLastOne.Label = "From last";
			this.btnLastOne.Name = "btnLastOne";
			this.btnLastOne.OfficeImageId = "SmartArtAddShape";
			this.btnLastOne.ShowImage = true;
			this.btnLastOne.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnLastOne_Click);
			// 
			// btnNewExpSelected
			// 
			this.btnNewExpSelected.Label = "From selected";
			this.btnNewExpSelected.Name = "btnNewExpSelected";
			this.btnNewExpSelected.OfficeImageId = "TableOfFiguresInsert";
			this.btnNewExpSelected.ShowImage = true;
			this.btnNewExpSelected.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnNewExpSelected_Click);
			// 
			// btnInsertTemplate
			// 
			this.btnInsertTemplate.Label = "Insert template";
			this.btnInsertTemplate.Name = "btnInsertTemplate";
			this.btnInsertTemplate.OfficeImageId = "AdpDiagramNewTable";
			this.btnInsertTemplate.ShowImage = true;
			this.btnInsertTemplate.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnInsertTemplate_Click);
			// 
			// btnFitToPage
			// 
			this.btnFitToPage.Label = "Fit to page";
			this.btnFitToPage.Name = "btnFitToPage";
			this.btnFitToPage.OfficeImageId = "TableHeight";
			this.btnFitToPage.ShowImage = true;
			this.btnFitToPage.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnFitToPage_Click);
			// 
			// btnChangeUnits
			// 
			this.btnChangeUnits.Label = "Change units";
			this.btnChangeUnits.Name = "btnChangeUnits";
			this.btnChangeUnits.OfficeImageId = "TableStyleRowHeaders";
			this.btnChangeUnits.ShowImage = true;
			this.btnChangeUnits.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnChangeUnits_Click);
			// 
			// btnHeading
			// 
			this.btnHeading.Label = "Format data table";
			this.btnHeading.Name = "btnHeading";
			this.btnHeading.OfficeImageId = "CreateTableUsingSharePointListsGallery";
			this.btnHeading.ShowImage = true;
			this.btnHeading.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnFormatDataTable_Click);
			// 
			// btnTransfer
			// 
			this.btnTransfer.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnTransfer.Label = "Transfer compounds";
			this.btnTransfer.Name = "btnTransfer";
			this.btnTransfer.OfficeImageId = "ImportMoreMenu";
			this.btnTransfer.ShowImage = true;
			this.btnTransfer.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnTransfer_Click);
			// 
			// btnCalculateMasses
			// 
			this.btnCalculateMasses.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnCalculateMasses.Label = "Calculate weights";
			this.btnCalculateMasses.Name = "btnCalculateMasses";
			this.btnCalculateMasses.OfficeImageId = "AutoSum";
			this.btnCalculateMasses.ShowImage = true;
			this.btnCalculateMasses.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnCalculateMasses_Click);
			// 
			// btnFormula
			// 
			this.btnFormula.Label = "Formula";
			this.btnFormula.Name = "btnFormula";
			this.btnFormula.OfficeImageId = "FunctionWizard";
			this.btnFormula.ShowImage = true;
			this.btnFormula.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnFormula_Click);
			// 
			// btnRemoveRows
			// 
			this.btnRemoveRows.Label = "Delete rows";
			this.btnRemoveRows.Name = "btnRemoveRows";
			this.btnRemoveRows.OfficeImageId = "TableRowsDelete";
			this.btnRemoveRows.ShowImage = true;
			this.btnRemoveRows.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnRemoveRows_Click);
			// 
			// btnSwapCells
			// 
			this.btnSwapCells.Label = "Swap cells";
			this.btnSwapCells.Name = "btnSwapCells";
			this.btnSwapCells.OfficeImageId = "DiagramReverseClassic";
			this.btnSwapCells.ShowImage = true;
			this.btnSwapCells.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnSwapCells_Click);
			// 
			// group3
			// 
			this.group3.Items.Add(this.btnPublish);
			this.group3.Items.Add(this.btnCopyData);
			this.group3.Items.Add(this.cbEq);
			this.group3.Items.Add(this.cbExportUnits);
			this.group3.Label = "Export";
			this.group3.Name = "group3";
			// 
			// btnPublish
			// 
			this.btnPublish.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnPublish.Label = "Publish";
			this.btnPublish.Name = "btnPublish";
			this.btnPublish.OfficeImageId = "MenuPublish";
			this.btnPublish.ShowImage = true;
			this.btnPublish.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnPublish_Click);
			// 
			// btnCopyData
			// 
			this.btnCopyData.ControlSize = Microsoft.Office.Core.RibbonControlSize.RibbonControlSizeLarge;
			this.btnCopyData.Label = "Copy row data";
			this.btnCopyData.Name = "btnCopyData";
			this.btnCopyData.OfficeImageId = "TableExportTableToSharePointList";
			this.btnCopyData.ShowImage = true;
			this.btnCopyData.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnCopyData_Click);
			// 
			// cbEq
			// 
			this.cbEq.Label = "Include equivalents";
			this.cbEq.Name = "cbEq";
			this.cbEq.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.cbEq_Click);
			// 
			// cbExportUnits
			// 
			this.cbExportUnits.Label = "Appropriately change units";
			this.cbExportUnits.Name = "cbExportUnits";
			this.cbExportUnits.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.cbExportUnits_Click);
			// 
			// group4
			// 
			this.group4.Items.Add(this.btnSettings);
			this.group4.Items.Add(this.btnAbout);
			this.group4.Items.Add(this.btnOpenNMR);
			this.group4.Label = "Misc";
			this.group4.Name = "group4";
			// 
			// btnSettings
			// 
			this.btnSettings.Label = "Settings";
			this.btnSettings.Name = "btnSettings";
			this.btnSettings.OfficeImageId = "FileExcelServicesOptions";
			this.btnSettings.ShowImage = true;
			this.btnSettings.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnSettings_Click);
			// 
			// btnAbout
			// 
			this.btnAbout.Label = "About";
			this.btnAbout.Name = "btnAbout";
			this.btnAbout.OfficeImageId = "Info";
			this.btnAbout.ShowImage = true;
			this.btnAbout.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnAbout_Click);
			// 
			// btnOpenNMR
			// 
			this.btnOpenNMR.Image = global::LabNotebookAddin.Properties.Resources._256x256bb;
			this.btnOpenNMR.Label = "Open NMR";
			this.btnOpenNMR.Name = "btnOpenNMR";
			this.btnOpenNMR.OfficeImageId = "Info";
			this.btnOpenNMR.ShowImage = true;
			this.btnOpenNMR.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnOpenNMR_Click);
			// 
			// group5
			// 
			this.group5.Items.Add(this.btnChemDrawInfo);
			this.group5.Items.Add(this.btnTest);
			this.group5.Items.Add(this.btnQuery);
			this.group5.Items.Add(this.button1);
			this.group5.Label = "Test";
			this.group5.Name = "group5";
			// 
			// btnChemDrawInfo
			// 
			this.btnChemDrawInfo.Label = "ChemDraw Obj Info";
			this.btnChemDrawInfo.Name = "btnChemDrawInfo";
			this.btnChemDrawInfo.ShowImage = true;
			this.btnChemDrawInfo.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnChemDrawInfo_Click);
			// 
			// btnTest
			// 
			this.btnTest.Label = "Test-reactions";
			this.btnTest.Name = "btnTest";
			this.btnTest.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnTest_Click);
			// 
			// btnQuery
			// 
			this.btnQuery.Label = "Structure search";
			this.btnQuery.Name = "btnQuery";
			this.btnQuery.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btnQuery_Click);
			// 
			// button1
			// 
			this.button1.Label = "test";
			this.button1.Name = "button1";
			this.button1.ShowImage = true;
			this.button1.Click += new Microsoft.Office.Tools.Ribbon.RibbonControlEventHandler(this.btn_format_clicked);
			// 
			// MainRibbon
			// 
			this.Name = "MainRibbon";
			this.RibbonType = "Microsoft.Word.Document";
			this.Tabs.Add(this.tab1);
			this.Load += new Microsoft.Office.Tools.Ribbon.RibbonUIEventHandler(this.Ribbon_Load);
			this.tab1.ResumeLayout(false);
			this.tab1.PerformLayout();
			this.group1.ResumeLayout(false);
			this.group1.PerformLayout();
			this.grpFormat.ResumeLayout(false);
			this.grpFormat.PerformLayout();
			this.group3.ResumeLayout(false);
			this.group3.PerformLayout();
			this.group4.ResumeLayout(false);
			this.group4.PerformLayout();
			this.group5.ResumeLayout(false);
			this.group5.PerformLayout();
			this.ResumeLayout(false);

		}

		#endregion

		internal Microsoft.Office.Tools.Ribbon.RibbonTab tab1;
		internal Microsoft.Office.Tools.Ribbon.RibbonGroup group1;
		internal Microsoft.Office.Tools.Ribbon.RibbonGroup grpFormat;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnChangeUnits;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnSettings;
		internal Microsoft.Office.Tools.Ribbon.RibbonGroup group3;
		internal Microsoft.Office.Tools.Ribbon.RibbonGroup group4;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnM1;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnM2;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnCopyData;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnAbout;
		internal Microsoft.Office.Tools.Ribbon.RibbonSplitButton btnNewExp;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnNewExpSelected;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnLastOne;
		internal Microsoft.Office.Tools.Ribbon.RibbonCheckBox cbEq;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnPublish;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnHeading;
		internal Microsoft.Office.Tools.Ribbon.RibbonCheckBox cbExportUnits;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnCalculateMasses;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnTransfer;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnFormula;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnChemDrawInfo;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnRemoveRows;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnInsertTemplate;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnSwapCells;
		internal Microsoft.Office.Tools.Ribbon.RibbonGroup group5;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnTest;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnQuery;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnFitToPage;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton btnOpenNMR;
		internal Microsoft.Office.Tools.Ribbon.RibbonButton button1;
	}

	partial class ThisRibbonCollection
	{
		internal MainRibbon Ribbon
		{
			get { return this.GetRibbon<MainRibbon>(); }
		}
	}
}
