using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LabNotebookAddin
{
	public static class LogListener
	{
		//public static string Trace;

		public static StringBuilder Trace { get; set; }

		static LogListener()  //static ctor
		{
			Trace = new StringBuilder();
		}

		public static void WriteLine()
		{
			Trace.AppendLine();
		}

		public static void WriteLine(string msg)
		{
			Trace.AppendLine(msg);
		}

		public static void WriteLine(object obj)
		{
			Trace.AppendLine(obj.ToString());
		}

		public static void Write(string msg)
		{
			Trace.Append(msg);
		}

		public static void Write(object obj)
		{
			Trace.Append(obj.ToString());
		}

		public static void ResetTrace()
		{
			Trace.Clear();
		}

		

	}
}
