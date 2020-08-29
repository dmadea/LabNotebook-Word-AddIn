using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Windows.Forms;

namespace LabNotebookAddin
{
	public partial class ProgressBarDialog : Form
	{

		public const int max = 100000;


		public const int WM_NCLBUTTONDOWN = 0xA1;
		public const int HT_CAPTION = 0x2;

		[DllImport("user32.dll")]
		public static extern int SendMessage(IntPtr hWnd, int Msg, int wParam, int lParam);
		[DllImport("user32.dll")]
		public static extern bool ReleaseCapture();

		private void ProgressBarDialog_MouseDown(object sender, MouseEventArgs e)
		{
			if (e.Button == MouseButtons.Left)
			{
				ReleaseCapture();
				SendMessage(Handle, WM_NCLBUTTONDOWN, HT_CAPTION, 0);
			}
		}

		private void pb_MouseDown(object sender, MouseEventArgs e)
		{
			if (e.Button == MouseButtons.Left)
			{
				ReleaseCapture();
				SendMessage(Handle, WM_NCLBUTTONDOWN, HT_CAPTION, 0);
			}
		}


		public ProgressBarDialog()
		{
			InitializeComponent();

			pb.Value = 100;
			pb.Maximum = max;
		}
		
		public void changeProgress(int value)
		{
			pb.Maximum = (int)((double) pb.Value / (value / 100.0));
		}

		



		//public void setMax(int value)
		//{
		//	pb.Maximum = value;
		//}
	}
}
