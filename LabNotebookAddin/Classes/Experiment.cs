using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.epam.indigo;
using System.Runtime.Serialization;
using System.Diagnostics;
using System.Drawing;
using Lyx;

namespace LabNotebookAddin
{
	[Serializable]
	public class Experiment : IDisposable
	{
		private bool disposed;

		public string ExpCode { get; set; }

		public string Procedure { get; set; }

		public DateTime Date { get; set; }

		public string Caption { get; set; }

		public string ShortDescripton { get; set; }

		public string Literature { get; set; }

		public int PageNumberInWordNotebook { get; set; }

		public LabNotebook Owner { get; set; }

		public CompressibleImage ReactionScheme { get; set; }

		[NonSerialized]
		private IndigoObject substructureMatch;
		
		public IndigoObject SubstructureMatch
		{
			get { return substructureMatch; }
			set { substructureMatch = value; }
		}

		//[NonSerialized]
		//private List<string> listOfMolecules;

		//private List<string> _listOfMoleculesSerialized;

		public List<string> ListOfMolecules { get; set; }

		//public List<string> ListOfMolecules
		//{
		//	get { return listOfMolecules; }
		//	set { listOfMolecules = value; }
		//}

		public int MolCount { get { return ListOfMolecules.Count; } }


		// from https://msdn.microsoft.com/en-us/library/system.runtime.serialization.onserializingattribute(v=vs.110).aspx and https://stackoverflow.com/questions/35087829/how-can-i-serialize-a-class-with-non-serializable-membersa and http://lifescience.opensource.epam.com/indigo/api/index.html#exact-match - Serialization

		[OnSerializing()]
		internal void OnSerializingMethod(StreamingContext context)
		{
			//_listOfMoleculesSerialized = new List<string>();
			//foreach (var molecule in ListOfMolecules)
			//{
			//	_listOfMoleculesSerialized.Add(molecule.smiles());
			//}
		}

		[OnSerialized()]
		internal void OnSerializedMethod(StreamingContext context)
		{
			//_listOfMoleculesSerialized = null;
		}

		[OnDeserializing()]
		internal void OnDeserializingMethod(StreamingContext context)
		{
			
		}

		[OnDeserialized()]
		internal void OnDeserializedMethod(StreamingContext context)
		{
			//using (Indigo ind = new Indigo())
			//{
			//	ListOfMolecules = new List<IndigoObject>();
			//	foreach (string molSmiles in _listOfMoleculesSerialized)
			//	{
			//		IndigoObject obj = ind.loadMolecule(molSmiles);
			//		ListOfMolecules.Add(obj);
			//	}
			//}
		}


		public Experiment()
		{
			PageNumberInWordNotebook = -1;
			ListOfMolecules = new List<string>(6);
		}

		public Experiment(LabNotebook owner)
		{
			PageNumberInWordNotebook = -1;
			ListOfMolecules = new List<string>(6);
			Owner = owner;
		}


		public override string ToString()
		{
			string molecules = string.Empty ;

			try
			{
				foreach (var moleculeSmiles in ListOfMolecules)
				{
					using (Indigo ind = new Indigo())
					{
						using (var molecule = ind.loadMolecule(moleculeSmiles))
						{
							molecules += $"{molecule.grossFormula()}  Mr = {molecule.molecularWeight()}\r\n";
						}
					}
				}
			}
			catch (IndigoException ex)
			{
				Debug.WriteLine(ex.Message);
				//throw;
			}

			string ret = $"Experiment code: {ExpCode}  Date: {Date.ToShortDateString()}\r\nLitrature: {Literature}\r\nNumber of molecules in reaction: {MolCount}\r\n{molecules}\r\nProcedure: {Procedure}";

			return ret;
		}

		~Experiment()
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

					if (ReactionScheme != null)
						ReactionScheme = null;
					if (substructureMatch != null)
						substructureMatch.Dispose();

					//ReactionScheme = null;
					//substructureMatch = null;
				}

				// Dispose unmanaged resources here.
				//if (ListOfMolecules != null)
				//{
				//	for (int i = 0; i < MolCount; i++)
				//	{
				//		ListOfMolecules[i].Dispose();
				//		//listOfMolecules[i] = null;
				//	}
				//}
				//ListOfMolecules = null;
			}

			disposed = true;
		}



	}
	
}
