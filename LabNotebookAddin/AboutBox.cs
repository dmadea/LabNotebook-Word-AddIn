/*
* Copyright (c) 2017, Dominik Madea
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


using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace LabNotebookAddin
{
	partial class AboutBox : Form
	{
		private Bitmap bitmap;
		private Graphics grp;

		public const int WM_NCLBUTTONDOWN = 0xA1;
		public const int HT_CAPTION = 0x2;

		private static bool _opened = false;

		public static bool IsAlreadyOpened { get { return _opened; } }

		[DllImport("user32.dll")]
		public static extern int SendMessage(IntPtr hWnd, int Msg, int wParam, int lParam);
		[DllImport("user32.dll")]
		public static extern bool ReleaseCapture();

		private void AboutBox_MouseDown(object sender, MouseEventArgs e)
		{
			if (e.Button == MouseButtons.Left)
			{
				ReleaseCapture();
				SendMessage(Handle, WM_NCLBUTTONDOWN, HT_CAPTION, 0);
			}
		}

		private void picBox_MouseDown(object sender, MouseEventArgs e)
		{
			if (e.Button == MouseButtons.Left)
			{
				ReleaseCapture();
				SendMessage(Handle, WM_NCLBUTTONDOWN, HT_CAPTION, 0);
			}
		}

		public AboutBox()
		{
			_opened = true;
			InitializeComponent();

			picBox.Location = new Point(0, 0);
			picBox.Size = this.Size;

			int width = picBox.Size.Width, height = picBox.Size.Height;
			float lineSize = 1.7f;
			Color lineCol = Color.DarkGray;

			Font fontHead = new Font("Times New Roman", 11f, FontStyle.Bold);
			Font fontNorm = new Font("Times New Roman", 10f, FontStyle.Regular);

			string buildTime = "October 14 2018";
			string copyright = "Copyright (c) 2018, Dominik Madea\r\nAll rights reserved.\r\n\r\nPermission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.\r\n\r\nTHE SOFTWARE IS PROVIDED \"AS IS\" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.";

			//lblAuthor.Text = "Dominik Madea";
			//this.lblVersion.Text = AssemblyVersion;
			//this.labelCopyright.Text = AssemblyCopyright;
			//this.labelCompanyName.Text = AssemblyCompany;
			//this.textBoxDescription.Text = AssemblyDescription;

			bitmap = new Bitmap(width, height);
			grp = Graphics.FromImage(bitmap);
			grp.Clear(Color.White);
			grp.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

			width--;
			height--;
			//draw borders
			grp.DrawLine(new Pen(lineCol, lineSize), 0, 0, 0, height);
			grp.DrawLine(new Pen(lineCol, lineSize), 0, 0, width, 0);
			grp.DrawLine(new Pen(lineCol, lineSize), width, 0, width, height);
			grp.DrawLine(new Pen(lineCol, lineSize), 0, height, width, height);

			int spacing = 150;
			int space = 20;

			grp.DrawString("Laboratory Notebook Word AddIn", fontHead, Brushes.DarkRed, new PointF(30, 10));

			grp.DrawString("Author:", fontNorm, Brushes.Black, new PointF(space, 50));
			grp.DrawString("Dominik Madea", fontNorm, Brushes.Black, new PointF(spacing, 50));

			grp.DrawString("Version:", fontNorm, Brushes.Black, new PointF(space, 80));
			grp.DrawString(AssemblyVersion, fontNorm, Brushes.Black, new PointF(spacing, 80));

			grp.DrawString("Build time:", fontNorm, Brushes.Black, new PointF(space, 110));
			grp.DrawString(buildTime, fontNorm, Brushes.Black, new PointF(spacing, 110));

			//grp.DrawString("Copyright", fontNorm, Brushes.Black, new PointF(space, 150));

			tbCopyright.Font = fontNorm;

			tbCopyright.Text = copyright;

			grp.Save();
		}

		#region Assembly Attribute Accessors

		public string AssemblyTitle
		{
			get
			{
				object[] attributes = Assembly.GetExecutingAssembly().GetCustomAttributes(typeof(AssemblyTitleAttribute), false);
				if (attributes.Length > 0)
				{
					AssemblyTitleAttribute titleAttribute = (AssemblyTitleAttribute)attributes[0];
					if (titleAttribute.Title != "")
					{
						return titleAttribute.Title;
					}
				}
				return System.IO.Path.GetFileNameWithoutExtension(Assembly.GetExecutingAssembly().CodeBase);
			}
		}

		public string AssemblyVersion
		{
			get
			{
				return Assembly.GetExecutingAssembly().GetName().Version.ToString();
			}
		}

		public string AssemblyDescription
		{
			get
			{
				object[] attributes = Assembly.GetExecutingAssembly().GetCustomAttributes(typeof(AssemblyDescriptionAttribute), false);
				if (attributes.Length == 0)
				{
					return "";
				}
				return ((AssemblyDescriptionAttribute)attributes[0]).Description;
			}
		}

		public string AssemblyProduct
		{
			get
			{
				object[] attributes = Assembly.GetExecutingAssembly().GetCustomAttributes(typeof(AssemblyProductAttribute), false);
				if (attributes.Length == 0)
				{
					return "";
				}
				return ((AssemblyProductAttribute)attributes[0]).Product;
			}
		}

		public string AssemblyCopyright
		{
			get
			{
				object[] attributes = Assembly.GetExecutingAssembly().GetCustomAttributes(typeof(AssemblyCopyrightAttribute), false);
				if (attributes.Length == 0)
				{
					return "";
				}
				return ((AssemblyCopyrightAttribute)attributes[0]).Copyright;
			}
		}

		public string AssemblyCompany
		{
			get
			{
				object[] attributes = Assembly.GetExecutingAssembly().GetCustomAttributes(typeof(AssemblyCompanyAttribute), false);
				if (attributes.Length == 0)
				{
					return "";
				}
				return ((AssemblyCompanyAttribute)attributes[0]).Company;
			}
		}
		#endregion

		private void okButton_Click(object sender, EventArgs e)
		{
			Close();
		}

		private void AboutBox_FormClosing(object sender, FormClosingEventArgs e)
		{
			_opened = false;
		}

		private void AboutBox_Activated(object sender, EventArgs e)
		{
			WindowState = FormWindowState.Normal;
		}

		private void picBox_Paint(object sender, PaintEventArgs e)
		{
			e.Graphics.DrawImage(bitmap, 0, 0);
		}

		private void okButton_Click_1(object sender, EventArgs e)
		{
			Close();
		}
	}
}
