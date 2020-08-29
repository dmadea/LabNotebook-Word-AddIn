using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CustomExtensions
{
	public static class ExtensionMethods
	{


		public static string TrimWord(this string str)
		{
			return str.Trim('\r', '\a', ' ', '\t');
		}

	}
}
