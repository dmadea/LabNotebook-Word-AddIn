/****************************************************************************
 * Copyright (C) 2009-2015 EPAM Systems
 * 
 * This file is part of Indigo ELN.
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/
package com.chemistry.enotebook.utils;

import java.text.DecimalFormat;

public class NumberUtils 
{
	public NumberUtils(){
		super();
	}
	
	// utilities -- called from the ...Handler classes that set keywords to data

	// new value methods
	public static String nvl(String inVal) { return (nvl(inVal, "")); }

	public static String nvl(String inVal, String defaultVal)
	{
		return (inVal == null || inVal.length() == 0) ? defaultVal : inVal; 
	}

	// decimal number formatter
    public static String formatNumber(double value, String pattern)
    {
    	DecimalFormat myFormatter = new DecimalFormat(pattern);
     	return myFormatter.format(value);
    }
}
