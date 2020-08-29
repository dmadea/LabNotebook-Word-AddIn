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

import com.chemistry.enotebook.domain.BatchModel;

import java.util.Comparator;

/**
 * Note this only works for compound ids like MN-001234.  It does not yet 
 * sort cases where there are multiple compounds.
 */

public class CompoundNumberComparator 
	implements Comparator 
{
	public int compare(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		if (! ((o1 instanceof BatchModel && o2 instanceof BatchModel) ||
			  (o1 instanceof String && o2 instanceof String)))
			throw new IllegalArgumentException();
		
		String s1 = "";
		String s2 = "";
		if (o1 instanceof BatchModel) {
			s1 = ((BatchModel) o1).getCompoundId();   
			s2 = ((BatchModel) o2).getCompoundId();
		} else {
			s1 = (String) o1;
			s2 = (String) o2;
		}
		return s1.compareTo(s2);
	}
}