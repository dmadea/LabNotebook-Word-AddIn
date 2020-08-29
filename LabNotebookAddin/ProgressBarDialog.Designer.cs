namespace LabNotebookAddin
{
	partial class ProgressBarDialog
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
			this.pb = new System.Windows.Forms.ProgressBar();
			this.SuspendLayout();
			// 
			// pb
			// 
			this.pb.Location = new System.Drawing.Point(12, 12);
			this.pb.MarqueeAnimationSpeed = 1000;
			this.pb.Maximum = 10000;
			this.pb.Name = "pb";
			this.pb.Size = new System.Drawing.Size(235, 34);
			this.pb.Step = 1;
			this.pb.TabIndex = 0;
			this.pb.Value = 1;
			this.pb.MouseDown += new System.Windows.Forms.MouseEventHandler(this.pb_MouseDown);
			// 
			// ProgressBarDialog
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(261, 61);
			this.Controls.Add(this.pb);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
			this.Name = "ProgressBarDialog";
			this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
			this.Text = "ProgressBarDialog";
			this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.ProgressBarDialog_MouseDown);
			this.ResumeLayout(false);

		}

		#endregion

		private System.Windows.Forms.ProgressBar pb;
	}
}