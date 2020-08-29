namespace LabNotebookAddin
{
	partial class QueryDialog
	{
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

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

		#region Windows Form Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.components = new System.ComponentModel.Container();
			this.btnDrawMolecule = new System.Windows.Forms.Button();
			this.picBoxMoleculeView = new System.Windows.Forms.PictureBox();
			this.btnLoadMolecule = new System.Windows.Forms.Button();
			this.btnExactMatch = new System.Windows.Forms.Button();
			this.btnSubstructureSearch = new System.Windows.Forms.Button();
			this.btnSerializeDoc = new System.Windows.Forms.Button();
			this.cbELE = new System.Windows.Forms.CheckBox();
			this.cbMAS = new System.Windows.Forms.CheckBox();
			this.cbSTE = new System.Windows.Forms.CheckBox();
			this.cbFRA = new System.Windows.Forms.CheckBox();
			this.btnLoadQueryMol = new System.Windows.Forms.Button();
			this.btnLoadNotebooks = new System.Windows.Forms.Button();
			this.tvLns = new System.Windows.Forms.TreeView();
			this.pBar = new System.Windows.Forms.ProgressBar();
			this.bLView = new ComponentOwl.BetterListView.BetterListView();
			this.nudMaxHeight = new System.Windows.Forms.NumericUpDown();
			this.cbLabNotebookStyle = new System.Windows.Forms.ComboBox();
			this.groupBox1 = new System.Windows.Forms.GroupBox();
			this.label1 = new System.Windows.Forms.Label();
			this.label2 = new System.Windows.Forms.Label();
			this.lblShowedExps = new System.Windows.Forms.Label();
			this.label3 = new System.Windows.Forms.Label();
			this.lblExpsFound = new System.Windows.Forms.Label();
			this.label4 = new System.Windows.Forms.Label();
			this.cmsBLView = new System.Windows.Forms.ContextMenuStrip(this.components);
			this.experimentInfoToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.openExperimentInWordToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
			this.btnSaveDocuments = new System.Windows.Forms.Button();
			this.statusStrip1 = new System.Windows.Forms.StatusStrip();
			this.tssLabel = new System.Windows.Forms.ToolStripStatusLabel();
			this.tssSpace = new System.Windows.Forms.ToolStripStatusLabel();
			this.tssActiveDoc = new System.Windows.Forms.ToolStripStatusLabel();
			this.btnCheck = new System.Windows.Forms.Button();
			this.splitContainer1 = new System.Windows.Forms.SplitContainer();
			((System.ComponentModel.ISupportInitialize)(this.picBoxMoleculeView)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.bLView)).BeginInit();
			((System.ComponentModel.ISupportInitialize)(this.nudMaxHeight)).BeginInit();
			this.groupBox1.SuspendLayout();
			this.cmsBLView.SuspendLayout();
			this.statusStrip1.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
			this.splitContainer1.Panel1.SuspendLayout();
			this.splitContainer1.Panel2.SuspendLayout();
			this.splitContainer1.SuspendLayout();
			this.SuspendLayout();
			// 
			// btnDrawMolecule
			// 
			this.btnDrawMolecule.Location = new System.Drawing.Point(12, 12);
			this.btnDrawMolecule.Name = "btnDrawMolecule";
			this.btnDrawMolecule.Size = new System.Drawing.Size(152, 32);
			this.btnDrawMolecule.TabIndex = 0;
			this.btnDrawMolecule.Text = "Draw molecule";
			this.btnDrawMolecule.UseVisualStyleBackColor = true;
			this.btnDrawMolecule.Click += new System.EventHandler(this.btnDrawMolecule_Click);
			// 
			// picBoxMoleculeView
			// 
			this.picBoxMoleculeView.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
			this.picBoxMoleculeView.Location = new System.Drawing.Point(583, 12);
			this.picBoxMoleculeView.Name = "picBoxMoleculeView";
			this.picBoxMoleculeView.Size = new System.Drawing.Size(516, 239);
			this.picBoxMoleculeView.TabIndex = 1;
			this.picBoxMoleculeView.TabStop = false;
			this.picBoxMoleculeView.Paint += new System.Windows.Forms.PaintEventHandler(this.picBoxMoleculeView_Paint);
			this.picBoxMoleculeView.Resize += new System.EventHandler(this.picBoxMoleculeView_Resize);
			// 
			// btnLoadMolecule
			// 
			this.btnLoadMolecule.Location = new System.Drawing.Point(12, 50);
			this.btnLoadMolecule.Name = "btnLoadMolecule";
			this.btnLoadMolecule.Size = new System.Drawing.Size(152, 32);
			this.btnLoadMolecule.TabIndex = 2;
			this.btnLoadMolecule.Text = "Load molecule";
			this.btnLoadMolecule.UseVisualStyleBackColor = true;
			this.btnLoadMolecule.Click += new System.EventHandler(this.btnLoadMolecule_Click);
			// 
			// btnExactMatch
			// 
			this.btnExactMatch.Location = new System.Drawing.Point(170, 51);
			this.btnExactMatch.Name = "btnExactMatch";
			this.btnExactMatch.Size = new System.Drawing.Size(152, 31);
			this.btnExactMatch.TabIndex = 4;
			this.btnExactMatch.Text = "Exact match";
			this.btnExactMatch.UseVisualStyleBackColor = true;
			this.btnExactMatch.Click += new System.EventHandler(this.btnExactMatch_Click);
			// 
			// btnSubstructureSearch
			// 
			this.btnSubstructureSearch.Location = new System.Drawing.Point(170, 12);
			this.btnSubstructureSearch.Name = "btnSubstructureSearch";
			this.btnSubstructureSearch.Size = new System.Drawing.Size(152, 32);
			this.btnSubstructureSearch.TabIndex = 5;
			this.btnSubstructureSearch.Text = "Substructure search";
			this.btnSubstructureSearch.UseVisualStyleBackColor = true;
			this.btnSubstructureSearch.Click += new System.EventHandler(this.btnSubstructureSearch_Click);
			// 
			// btnSerializeDoc
			// 
			this.btnSerializeDoc.Location = new System.Drawing.Point(12, 126);
			this.btnSerializeDoc.Name = "btnSerializeDoc";
			this.btnSerializeDoc.Size = new System.Drawing.Size(152, 31);
			this.btnSerializeDoc.TabIndex = 6;
			this.btnSerializeDoc.Text = "Save this doc to ln";
			this.btnSerializeDoc.UseVisualStyleBackColor = true;
			this.btnSerializeDoc.Click += new System.EventHandler(this.btnSerializeDoc_Click);
			// 
			// cbELE
			// 
			this.cbELE.AutoSize = true;
			this.cbELE.Location = new System.Drawing.Point(6, 21);
			this.cbELE.Name = "cbELE";
			this.cbELE.Size = new System.Drawing.Size(221, 38);
			this.cbELE.TabIndex = 9;
			this.cbELE.Text = "Bond types, charges, radicals,\r\nvalences";
			this.cbELE.UseVisualStyleBackColor = true;
			// 
			// cbMAS
			// 
			this.cbMAS.AutoSize = true;
			this.cbMAS.Location = new System.Drawing.Point(6, 63);
			this.cbMAS.Name = "cbMAS";
			this.cbMAS.Size = new System.Drawing.Size(119, 21);
			this.cbMAS.TabIndex = 10;
			this.cbMAS.Text = "Atom isotopes";
			this.cbMAS.UseVisualStyleBackColor = true;
			// 
			// cbSTE
			// 
			this.cbSTE.AutoSize = true;
			this.cbSTE.Location = new System.Drawing.Point(6, 92);
			this.cbSTE.Name = "cbSTE";
			this.cbSTE.Size = new System.Drawing.Size(213, 38);
			this.cbSTE.TabIndex = 11;
			this.cbSTE.Text = "Chiral centers, stereogroups,\r\ncis-trans isomerization";
			this.cbSTE.UseVisualStyleBackColor = true;
			// 
			// cbFRA
			// 
			this.cbFRA.AutoSize = true;
			this.cbFRA.Location = new System.Drawing.Point(6, 136);
			this.cbFRA.Name = "cbFRA";
			this.cbFRA.Size = new System.Drawing.Size(229, 38);
			this.cbFRA.TabIndex = 12;
			this.cbFRA.Text = "Disallow match of separate ions\r\nin salts";
			this.cbFRA.UseVisualStyleBackColor = true;
			// 
			// btnLoadQueryMol
			// 
			this.btnLoadQueryMol.Location = new System.Drawing.Point(12, 88);
			this.btnLoadQueryMol.Name = "btnLoadQueryMol";
			this.btnLoadQueryMol.Size = new System.Drawing.Size(152, 32);
			this.btnLoadQueryMol.TabIndex = 14;
			this.btnLoadQueryMol.Text = "Load query molecule";
			this.btnLoadQueryMol.UseVisualStyleBackColor = true;
			this.btnLoadQueryMol.Click += new System.EventHandler(this.btnLoadQueryMol_Click);
			// 
			// btnLoadNotebooks
			// 
			this.btnLoadNotebooks.Location = new System.Drawing.Point(12, 201);
			this.btnLoadNotebooks.Name = "btnLoadNotebooks";
			this.btnLoadNotebooks.Size = new System.Drawing.Size(152, 43);
			this.btnLoadNotebooks.TabIndex = 17;
			this.btnLoadNotebooks.Text = "Load all available notebooks";
			this.btnLoadNotebooks.UseVisualStyleBackColor = true;
			this.btnLoadNotebooks.Click += new System.EventHandler(this.btnLoadNotebooks_Click);
			// 
			// tvLns
			// 
			this.tvLns.Dock = System.Windows.Forms.DockStyle.Fill;
			this.tvLns.Location = new System.Drawing.Point(0, 0);
			this.tvLns.Name = "tvLns";
			this.tvLns.Size = new System.Drawing.Size(182, 297);
			this.tvLns.TabIndex = 18;
			this.tvLns.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.twLns_AfterCheck);
			this.tvLns.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.twLns_AfterSelect);
			// 
			// pBar
			// 
			this.pBar.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
			this.pBar.Location = new System.Drawing.Point(10, 560);
			this.pBar.MarqueeAnimationSpeed = 1;
			this.pBar.Name = "pBar";
			this.pBar.Size = new System.Drawing.Size(1089, 10);
			this.pBar.TabIndex = 20;
			// 
			// bLView
			// 
			this.bLView.Dock = System.Windows.Forms.DockStyle.Fill;
			this.bLView.Location = new System.Drawing.Point(0, 0);
			this.bLView.Name = "bLView";
			this.bLView.Size = new System.Drawing.Size(901, 297);
			this.bLView.TabIndex = 23;
			this.bLView.ItemActivate += new ComponentOwl.BetterListView.BetterListViewItemActivateEventHandler(this.bLView_ItemActivate);
			this.bLView.ColumnWidthChanged += new ComponentOwl.BetterListView.BetterListViewColumnWidthChangedEventHandler(this.bLView_ColumnWidthChanged);
			this.bLView.VScrollPropertiesChanged += new ComponentOwl.BetterListView.BetterListViewScrollPropertiesChangedEventHandler(this.bLView_VScrollPropertiesChanged);
			// 
			// nudMaxHeight
			// 
			this.nudMaxHeight.Increment = new decimal(new int[] {
            5,
            0,
            0,
            196608});
			this.nudMaxHeight.Location = new System.Drawing.Point(257, 201);
			this.nudMaxHeight.Maximum = new decimal(new int[] {
            800,
            0,
            0,
            0});
			this.nudMaxHeight.Minimum = new decimal(new int[] {
            30,
            0,
            0,
            0});
			this.nudMaxHeight.Name = "nudMaxHeight";
			this.nudMaxHeight.Size = new System.Drawing.Size(65, 22);
			this.nudMaxHeight.TabIndex = 24;
			this.nudMaxHeight.Value = new decimal(new int[] {
            160,
            0,
            0,
            0});
			// 
			// cbLabNotebookStyle
			// 
			this.cbLabNotebookStyle.Location = new System.Drawing.Point(170, 130);
			this.cbLabNotebookStyle.Name = "cbLabNotebookStyle";
			this.cbLabNotebookStyle.Size = new System.Drawing.Size(129, 24);
			this.cbLabNotebookStyle.TabIndex = 25;
			// 
			// groupBox1
			// 
			this.groupBox1.Controls.Add(this.cbELE);
			this.groupBox1.Controls.Add(this.cbMAS);
			this.groupBox1.Controls.Add(this.cbSTE);
			this.groupBox1.Controls.Add(this.cbFRA);
			this.groupBox1.Location = new System.Drawing.Point(328, 12);
			this.groupBox1.Name = "groupBox1";
			this.groupBox1.Size = new System.Drawing.Size(249, 183);
			this.groupBox1.TabIndex = 26;
			this.groupBox1.TabStop = false;
			this.groupBox1.Text = "Restrictions for Exact match";
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Location = new System.Drawing.Point(176, 202);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(76, 17);
			this.label1.TabIndex = 27;
			this.label1.Text = "max height";
			// 
			// label2
			// 
			this.label2.AutoSize = true;
			this.label2.Location = new System.Drawing.Point(419, 227);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(95, 17);
			this.label2.TabIndex = 28;
			this.label2.Text = "Showed exps:";
			// 
			// lblShowedExps
			// 
			this.lblShowedExps.AutoSize = true;
			this.lblShowedExps.Location = new System.Drawing.Point(520, 227);
			this.lblShowedExps.Name = "lblShowedExps";
			this.lblShowedExps.Size = new System.Drawing.Size(51, 17);
			this.lblShowedExps.TabIndex = 29;
			this.lblShowedExps.Text = "[count]";
			// 
			// label3
			// 
			this.label3.AutoSize = true;
			this.label3.Location = new System.Drawing.Point(176, 227);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(129, 17);
			this.label3.TabIndex = 28;
			this.label3.Text = "Experiments found:";
			// 
			// lblExpsFound
			// 
			this.lblExpsFound.AutoSize = true;
			this.lblExpsFound.Location = new System.Drawing.Point(311, 227);
			this.lblExpsFound.Name = "lblExpsFound";
			this.lblExpsFound.Size = new System.Drawing.Size(51, 17);
			this.lblExpsFound.TabIndex = 29;
			this.lblExpsFound.Text = "[count]";
			// 
			// label4
			// 
			this.label4.AutoSize = true;
			this.label4.Location = new System.Drawing.Point(167, 110);
			this.label4.Name = "label4";
			this.label4.Size = new System.Drawing.Size(157, 17);
			this.label4.TabIndex = 27;
			this.label4.Text = "Lab notebook template:";
			// 
			// cmsBLView
			// 
			this.cmsBLView.ImageScalingSize = new System.Drawing.Size(20, 20);
			this.cmsBLView.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.experimentInfoToolStripMenuItem,
            this.openExperimentInWordToolStripMenuItem});
			this.cmsBLView.Name = "cmsBLView";
			this.cmsBLView.ShowImageMargin = false;
			this.cmsBLView.Size = new System.Drawing.Size(225, 52);
			// 
			// experimentInfoToolStripMenuItem
			// 
			this.experimentInfoToolStripMenuItem.Name = "experimentInfoToolStripMenuItem";
			this.experimentInfoToolStripMenuItem.Size = new System.Drawing.Size(224, 24);
			this.experimentInfoToolStripMenuItem.Text = "Experiment info";
			// 
			// openExperimentInWordToolStripMenuItem
			// 
			this.openExperimentInWordToolStripMenuItem.Name = "openExperimentInWordToolStripMenuItem";
			this.openExperimentInWordToolStripMenuItem.Size = new System.Drawing.Size(224, 24);
			this.openExperimentInWordToolStripMenuItem.Text = "Open experiment in Word";
			// 
			// btnSaveDocuments
			// 
			this.btnSaveDocuments.Location = new System.Drawing.Point(12, 163);
			this.btnSaveDocuments.Name = "btnSaveDocuments";
			this.btnSaveDocuments.Size = new System.Drawing.Size(159, 31);
			this.btnSaveDocuments.TabIndex = 6;
			this.btnSaveDocuments.Text = "Save documents to lns";
			this.btnSaveDocuments.UseVisualStyleBackColor = true;
			this.btnSaveDocuments.Click += new System.EventHandler(this.btnSaveDocuments_Click);
			// 
			// statusStrip1
			// 
			this.statusStrip1.ImageScalingSize = new System.Drawing.Size(20, 20);
			this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tssLabel,
            this.tssSpace,
            this.tssActiveDoc});
			this.statusStrip1.Location = new System.Drawing.Point(0, 576);
			this.statusStrip1.Name = "statusStrip1";
			this.statusStrip1.Size = new System.Drawing.Size(1111, 25);
			this.statusStrip1.TabIndex = 30;
			this.statusStrip1.Text = "statusStrip1";
			// 
			// tssLabel
			// 
			this.tssLabel.Name = "tssLabel";
			this.tssLabel.Size = new System.Drawing.Size(151, 20);
			this.tssLabel.Text = "toolStripStatusLabel1";
			// 
			// tssSpace
			// 
			this.tssSpace.Name = "tssSpace";
			this.tssSpace.Size = new System.Drawing.Size(794, 20);
			this.tssSpace.Spring = true;
			this.tssSpace.Text = "toolStripStatusLabel1";
			// 
			// tssActiveDoc
			// 
			this.tssActiveDoc.Name = "tssActiveDoc";
			this.tssActiveDoc.Size = new System.Drawing.Size(151, 20);
			this.tssActiveDoc.Text = "toolStripStatusLabel1";
			// 
			// btnCheck
			// 
			this.btnCheck.Location = new System.Drawing.Point(408, 199);
			this.btnCheck.Name = "btnCheck";
			this.btnCheck.Size = new System.Drawing.Size(147, 25);
			this.btnCheck.TabIndex = 31;
			this.btnCheck.Text = "Check notebooks";
			this.btnCheck.UseVisualStyleBackColor = true;
			this.btnCheck.Click += new System.EventHandler(this.btnCheck_Click);
			// 
			// splitContainer1
			// 
			this.splitContainer1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
			this.splitContainer1.Location = new System.Drawing.Point(12, 257);
			this.splitContainer1.Name = "splitContainer1";
			// 
			// splitContainer1.Panel1
			// 
			this.splitContainer1.Panel1.Controls.Add(this.tvLns);
			// 
			// splitContainer1.Panel2
			// 
			this.splitContainer1.Panel2.Controls.Add(this.bLView);
			this.splitContainer1.Size = new System.Drawing.Size(1087, 297);
			this.splitContainer1.SplitterDistance = 182;
			this.splitContainer1.TabIndex = 32;
			// 
			// QueryDialog
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(1111, 601);
			this.Controls.Add(this.splitContainer1);
			this.Controls.Add(this.btnCheck);
			this.Controls.Add(this.statusStrip1);
			this.Controls.Add(this.lblExpsFound);
			this.Controls.Add(this.lblShowedExps);
			this.Controls.Add(this.label3);
			this.Controls.Add(this.label2);
			this.Controls.Add(this.label4);
			this.Controls.Add(this.label1);
			this.Controls.Add(this.groupBox1);
			this.Controls.Add(this.cbLabNotebookStyle);
			this.Controls.Add(this.nudMaxHeight);
			this.Controls.Add(this.pBar);
			this.Controls.Add(this.btnLoadNotebooks);
			this.Controls.Add(this.btnLoadQueryMol);
			this.Controls.Add(this.btnSaveDocuments);
			this.Controls.Add(this.btnSerializeDoc);
			this.Controls.Add(this.btnSubstructureSearch);
			this.Controls.Add(this.btnExactMatch);
			this.Controls.Add(this.btnLoadMolecule);
			this.Controls.Add(this.picBoxMoleculeView);
			this.Controls.Add(this.btnDrawMolecule);
			this.MinimumSize = new System.Drawing.Size(722, 362);
			this.Name = "QueryDialog";
			this.Text = "Structure search";
			this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.QueryDialog_FormClosed);
			this.Load += new System.EventHandler(this.QueryDialog_Load);
			((System.ComponentModel.ISupportInitialize)(this.picBoxMoleculeView)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.bLView)).EndInit();
			((System.ComponentModel.ISupportInitialize)(this.nudMaxHeight)).EndInit();
			this.groupBox1.ResumeLayout(false);
			this.groupBox1.PerformLayout();
			this.cmsBLView.ResumeLayout(false);
			this.statusStrip1.ResumeLayout(false);
			this.statusStrip1.PerformLayout();
			this.splitContainer1.Panel1.ResumeLayout(false);
			this.splitContainer1.Panel2.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
			this.splitContainer1.ResumeLayout(false);
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.Button btnDrawMolecule;
		private System.Windows.Forms.PictureBox picBoxMoleculeView;
		private System.Windows.Forms.Button btnLoadMolecule;
		private System.Windows.Forms.Button btnExactMatch;
		private System.Windows.Forms.Button btnSubstructureSearch;
		private System.Windows.Forms.Button btnSerializeDoc;
		private System.Windows.Forms.CheckBox cbELE;
		private System.Windows.Forms.CheckBox cbMAS;
		private System.Windows.Forms.CheckBox cbSTE;
		private System.Windows.Forms.CheckBox cbFRA;
		private System.Windows.Forms.Button btnLoadQueryMol;
		private System.Windows.Forms.Button btnLoadNotebooks;
		private System.Windows.Forms.TreeView tvLns;
		private System.Windows.Forms.ProgressBar pBar;
		private ComponentOwl.BetterListView.BetterListView bLView;
		private System.Windows.Forms.NumericUpDown nudMaxHeight;
		private System.Windows.Forms.ComboBox cbLabNotebookStyle;
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label lblShowedExps;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.Label lblExpsFound;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.ContextMenuStrip cmsBLView;
		private System.Windows.Forms.ToolStripMenuItem experimentInfoToolStripMenuItem;
		private System.Windows.Forms.ToolStripMenuItem openExperimentInWordToolStripMenuItem;
		private System.Windows.Forms.Button btnSaveDocuments;
		private System.Windows.Forms.StatusStrip statusStrip1;
		private System.Windows.Forms.ToolStripStatusLabel tssLabel;
		private System.Windows.Forms.ToolStripStatusLabel tssSpace;
		private System.Windows.Forms.ToolStripStatusLabel tssActiveDoc;
		private System.Windows.Forms.Button btnCheck;
		private System.Windows.Forms.SplitContainer splitContainer1;
	}
}