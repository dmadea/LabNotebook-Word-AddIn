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
package com.chemistry.enotebook.experiment.businessmodel.stoichiometry;

import com.chemistry.enotebook.domain.StoicModelInterface;

import java.util.Comparator;

public class ComparatorStoicAdditionOrder implements Comparator<StoicModelInterface> {

	public ComparatorStoicAdditionOrder() {
		super();
	}

	public int compare(StoicModelInterface o1, StoicModelInterface o2) {
		int result = 0;
		if (o1 != null) {
			result = 1;
			if (o2 != null) {
				result = o1.getStoicTransactionOrder() - o2.getStoicTransactionOrder();
			}
		} else if (o2 != null)
			result = -1;

		return result;
	}
}
