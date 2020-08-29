namespace LabNotebookAddin
{
	partial class FormatDocumentDialog
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
			this.btnFormat = new System.Windows.Forms.Button();
			this.btnCancel = new System.Windows.Forms.Button();
			this.cbFitToPage = new System.Windows.Forms.CheckBox();
			this.cbDataTable = new System.Windows.Forms.CheckBox();
			this.cbSpaces = new System.Windows.Forms.CheckBox();
			this.label1 = new System.Windows.Forms.Label();
			this.pBar = new System.Windows.Forms.ProgressBar();
			this.cbPreferredWidth = new System.Windows.Forms.CheckBox();
			this.cbFormatExperimentTable = new System.Windows.Forms.CheckBox();
			this.SuspendLayout();
			// 
			// btnFormat
			// 
			this.btnFormat.Location = new System.Drawing.Point(281, 176);
			this.btnFormat.Name = "btnFormat";
			this.btnFormat.Size = new System.Drawing.Size(139, 29);
			this.btnFormat.TabIndex = 0;
			this.btnFormat.Text = "Format document";
			this.btnFormat.UseVisualStyleBackColor = true;
			this.btnFormat.Click += new System.EventHandler(this.btnFormat_Click);
			// 
			// btnCancel
			// 
			this.btnCancel.Location = new System.Drawing.Point(193, 176);
			this.btnCancel.Name = "btnCancel";
			this.btnCancel.Size = new System.Drawing.Size(82, 29);
			this.btnCancel.TabIndex = 1;
			this.btnCancel.Text = "Cancel";
			this.btnCancel.UseVisualStyleBackColor = true;
			this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
			// 
			// cbFitToPage
			// 
			this.cbFitToPage.AutoSize = true;
			this.cbFitToPage.Location = new System.Drawing.Point(12, 41);
			this.cbFitToPage.Name = "cbFitToPage";
			this.cbFitToPage.Size = new System.Drawing.Size(97, 21);
			this.cbFitToPage.TabIndex = 2;
			this.cbFitToPage.Text = "Fit to page";
			this.cbFitToPage.UseVisualStyleBackColor = true;
			// 
			// cbDataTable
			// 
			this.cbDataTable.AutoSize = true;
			this.cbDataTable.Location = new System.Drawing.Point(12, 68);
			this.cbDataTable.Name = "cbDataTable";
			this.cbDataTable.Size = new System.Drawing.Size(141, 21);
			this.cbDataTable.TabIndex = 3;
			this.cbDataTable.Text = "Format data table";
			this.cbDataTable.UseVisualStyleBackColor = true;
			// 
			// cbSpaces
			// 
			this.cbSpaces.AutoSize = true;
			this.cbSpaces.Location = new System.Drawing.Point(12, 122);
			this.cbSpaces.Name = "cbSpaces";
			this.cbSpaces.Size = new System.Drawing.Size(234, 21);
			this.cbSpaces.TabIndex = 4;
			this.cbSpaces.Text = "Reformate spaces among tables";
			this.cbSpaces.UseVisualStyleBackColor = true;
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Location = new System.Drawing.Point(12, 9);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(298, 17);
			this.label1.TabIndex = 5;
			this.label1.Text = "Performs selected actions for entire document";
			// 
			// pBar
			// 
			this.pBar.Location = new System.Drawing.Point(10, 217);
			this.pBar.MarqueeAnimationSpeed = 1;
			this.pBar.Name = "pBar";
			this.pBar.Size = new System.Drawing.Size(410, 12);
			this.pBar.TabIndex = 6;
			// 
			// cbPreferredWidth
			// 
			this.cbPreferredWidth.AutoSize = true;
			this.cbPreferredWidth.Location = new System.Drawing.Point(12, 149);
			this.cbPreferredWidth.Name = "cbPreferredWidth";
			this.cbPreferredWidth.Size = new System.Drawing.Size(394, 21);
			this.cbPreferredWidth.TabIndex = 7;
			this.cbPreferredWidth.Text = "Set preferred width of experiment and data table to 100 %";
			this.cbPreferredWidth.UseVisualStyleBackColor = true;
			// 
			// cbFormatExperimentTable
			// 
			this.cbFormatExperimentTable.AutoSize = true;
			this.cbFormatExperimentTable.Location = new System.Drawing.Point(12, 95);
			this.cbFormatExperimentTable.Name = "cbFormatExperimentTable";
			this.cbFormatExperimentTable.Size = new System.Drawing.Size(182, 21);
			this.cbFormatExperimentTable.TabIndex = 8;
			this.cbFormatExperimentTable.Text = "Format experiment table";
			this.cbFormatExperimentTable.UseVisualStyleBackColor = true;
			// 
			// FormatDocumentDialog
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(429, 244);
			this.Controls.Add(this.cbFormatExperimentTable);
			this.Controls.Add(this.cbPreferredWidth);
			this.Controls.Add(this.pBar);
			this.Controls.Add(this.label1);
			this.Controls.Add(this.cbSpaces);
			this.Controls.Add(this.cbDataTable);
			this.Controls.Add(this.cbFitToPage);
			this.Controls.Add(this.btnCancel);
			this.Controls.Add(this.btnFormat);
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "FormatDocumentDialog";
			this.Text = "FormatDocumentDialog";
			this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FormatDocumentDialog_FormClosing);
			this.Load += new System.EventHandler(this.FormatDocumentDialog_Load);
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.Button btnFormat;
		private System.Windows.Forms.Button btnCancel;
		private System.Windows.Forms.CheckBox cbFitToPage;
		private System.Windows.Forms.CheckBox cbDataTable;
		private System.Windows.Forms.CheckBox cbSpaces;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.ProgressBar pBar;
		private System.Windows.Forms.CheckBox cbPreferredWidth;
		private System.Windows.Forms.CheckBox cbFormatExperimentTable;
	}
}