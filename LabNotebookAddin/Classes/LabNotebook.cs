using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Windows.Forms;

namespace LabNotebookAddin
{
	[Serializable]
	public class LabNotebook : IDisposable
	{
		private bool disposed;

		public List<Experiment> Experiments { get; set; }

		public string Name { get; set; }

		public int Count { get { return Experiments.Count; } }

		public int? OriginalCount { get; set; } // for search methods

		public string FullPath { get; set; } //location of the word notebook on disk or web including the name of the document

		public DateTime Builded { get; set; }

		public string User { get; set; }

		public string Log { get; set; }

		//public List MyProperty { get; set; }

		public void AddExperiment(Experiment exp)
		{
			Experiments.Add(exp);
			exp.Owner = this;
		}

		public LabNotebook()
		{
			Experiments = new List<Experiment>();
		}

		public LabNotebook(List<Experiment> exps)
		{
			Experiments = exps;
		}

		public LabNotebook getInstanceWithoutExps()
		{
			LabNotebook ln = new LabNotebook();
			ln.Name = this.Name;
			ln.Builded = this.Builded;
			ln.User = this.User;
			ln.OriginalCount = this.Count;
			ln.FullPath = this.FullPath;
			ln.Log = this.Log;
			return ln;
		}

		public override bool Equals(object Obj)
		{
			LabNotebook ln = (LabNotebook)Obj;
			return (this.Builded == ln.Builded && this.Count == ln.Count && this.FullPath == ln.FullPath &&
				this.Name == ln.Name && this.OriginalCount == ln.OriginalCount && this.User == ln.User);
		}

		public static bool operator ==(LabNotebook ln1, LabNotebook ln2)
		{
			if (object.ReferenceEquals(ln1, null))
			{
				return object.ReferenceEquals(ln2, null);
			}
			else if (object.ReferenceEquals(ln2, null))
			{
				return object.ReferenceEquals(ln1, null);
			}

			return ln1.Equals(ln2);
		}

		public static bool operator !=(LabNotebook ln1, LabNotebook ln2)
		{
			return !(ln1 == ln2);
		}


		public override string ToString()
		{
			string toString = Name + "\n";

			foreach (Experiment exp in Experiments)
			{
				toString += exp.ToString() + "\r\n\r\n\r\n";
			}
			return toString;
		}

		public static void SerializeAndSave(string path, LabNotebook exps)
		{
			try
			{
				using (FileStream file = File.Create(path))
				{
					IFormatter formatter = new BinaryFormatter();

					formatter.Serialize(file, exps);
				}
			}
			catch (UnauthorizedAccessException)
			{
				MessageBox.Show("Error occured when attempt to save file: Unauthorized access.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				return;
			}
			catch (PathTooLongException)
			{
				MessageBox.Show("Error occured when attempt to save file: Name of file is too long.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				return;
			}
			catch (DirectoryNotFoundException)
			{
				MessageBox.Show("Error occured when attempt to save file: Directory doesn't exist.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				return;
			}
			catch (Exception ex)
			{
				MessageBox.Show("Error occured when attempt to save file:\n\n" + ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				return;
			}
		}


		public static LabNotebook Deserialize(string path)
		{
			try
			{
				using (FileStream file = File.OpenRead(path))
				{
					IFormatter formatter = new BinaryFormatter();

					return (LabNotebook)formatter.Deserialize(file);
				}
			}
			catch (Exception ex)
			{
				MessageBox.Show("Error occured when loading file. Error message:\n\n" + ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
				return null;
			}

		}

		~LabNotebook()
		{
			this.Dispose(false);
		}

		/// <summary>
		/// The dispose method that implements IDisposable.
		/// </summary>
		public void Dispose()
		{
			this.Dispose(true);
			GC.SuppressFinalize(this);
		}

		/// <summary>
		/// The virtual dispose method that allows
		/// classes inherithed from this one to dispose their resources.
		/// </summary>
		/// <param name="disposing"></param>
		protected virtual void Dispose(bool disposing)
		{
			if (!disposed)
			{
				if (disposing)
				{
					// Dispose managed resources here.

					if (Experiments != null)
					{
						for (int i = 0; i < Count; i++)
						{
							Experiments[i].Dispose();
							Experiments[i] = null;
						}
					}
					Experiments = null;
				}
			}

			disposed = true;
		}



	}
}
