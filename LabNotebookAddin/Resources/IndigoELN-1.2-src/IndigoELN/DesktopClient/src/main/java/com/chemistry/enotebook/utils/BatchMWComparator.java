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

public class BatchMWComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		if (! (o1 instanceof BatchModel && o2 instanceof BatchModel))
			throw new IllegalArgumentException();
		BatchModel batch1 = (BatchModel) o1;
		BatchModel batch2 = (BatchModel) o2;
		try {
			double mw1 = batch1.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
			double mw2 = batch2.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
			if (mw1 > mw2) 
				return 1;
			else if (mw2 > mw1)
				return -1;
		} catch (NumberFormatException e) {
			return 0;
		}
		return 0;
	}
}
