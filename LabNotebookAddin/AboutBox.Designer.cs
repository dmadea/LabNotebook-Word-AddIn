namespace LabNotebookAddin
{
	partial class AboutBox
	{
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
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
			this.picBox = new System.Windows.Forms.PictureBox();
			this.okButton = new System.Windows.Forms.Button();
			this.tbCopyright = new System.Windows.Forms.TextBox();
			((System.ComponentModel.ISupportInitialize)(this.picBox)).BeginInit();
			this.SuspendLayout();
			// 
			// picBox
			// 
			this.picBox.BackColor = System.Drawing.Color.White;
			this.picBox.Location = new System.Drawing.Point(15, 7);
			this.picBox.Name = "picBox";
			this.picBox.Size = new System.Drawing.Size(322, 374);
			this.picBox.TabIndex = 26;
			this.picBox.TabStop = false;
			this.picBox.Paint += new System.Windows.Forms.PaintEventHandler(this.picBox_Paint);
			this.picBox.MouseDown += new System.Windows.Forms.MouseEventHandler(this.picBox_MouseDown);
			// 
			// okButton
			// 
			this.okButton.BackColor = System.Drawing.Color.Transparent;
			this.okButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
			this.okButton.Location = new System.Drawing.Point(265, 353);
			this.okButton.Margin = new System.Windows.Forms.Padding(4);
			this.okButton.Name = "okButton";
			this.okButton.Size = new System.Drawing.Size(76, 28);
			this.okButton.TabIndex = 27;
			this.okButton.Text = "&OK";
			this.okButton.UseVisualStyleBackColor = false;
			this.okButton.Click += new System.EventHandler(this.okButton_Click_1);
			// 
			// tbCopyright
			// 
			this.tbCopyright.BackColor = System.Drawing.Color.White;
			this.tbCopyright.BorderStyle = System.Windows.Forms.BorderStyle.None;
			this.tbCopyright.Font = new System.Drawing.Font("Times New Roman", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
			this.tbCopyright.Location = new System.Drawing.Point(20, 162);
			this.tbCopyright.Multiline = true;
			this.tbCopyright.Name = "tbCopyright";
			this.tbCopyright.ReadOnly = true;
			this.tbCopyright.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
			this.tbCopyright.Size = new System.Drawing.Size(320, 184);
			this.tbCopyright.TabIndex = 28;
			this.tbCopyright.Text = "Copyrights\r\nasdasd";
			// 
			// AboutBox
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.BackColor = System.Drawing.Color.White;
			this.ClientSize = new System.Drawing.Size(356, 393);
			this.Controls.Add(this.tbCopyright);
			this.Controls.Add(this.okButton);
			this.Controls.Add(this.picBox);
			this.ForeColor = System.Drawing.SystemColors.ControlText;
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
			this.Margin = new System.Windows.Forms.Padding(4);
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "AboutBox";
			this.Padding = new System.Windows.Forms.Padding(12, 11, 12, 11);
			this.ShowIcon = false;
			this.ShowInTaskbar = false;
			this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
			this.Text = "About";
			this.Activated += new System.EventHandler(this.AboutBox_Activated);
			this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.AboutBox_FormClosing);
			this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.AboutBox_MouseDown);
			((System.ComponentModel.ISupportInitialize)(this.picBox)).EndInit();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion
		private System.Windows.Forms.PictureBox picBox;
		private System.Windows.Forms.Button okButton;
		private System.Windows.Forms.TextBox tbCopyright;
	}
}
