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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.domain.PseudoProductPlate;

import java.util.Collections;
import java.util.Comparator;

//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableModel;
//import com.chemistry.enotebook.client.gui.page.experiment.table.TableViewMVConnector;

public class PCeNRegistration_ErrorTableModel extends PCeNTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3685100636580887096L;

	/**
	 * 
	 */
	public PCeNRegistration_ErrorTableModel() {
		super();
	}

	/**
	 * @param connector
	 */
	public PCeNRegistration_ErrorTableModel(PCeNTableModelConnector connector) {
		super(connector);
	}
	
	public void sortColumn(int col, boolean ascending) {
		sortedColumnIndex = col;
		sortedColumnAscending = ascending;
		if (this.getColumnName(col).toLowerCase().indexOf("originated") >= 0) {
			// This is really really confusing...batches are actually plates
			Collections.sort(connector.getAbstractBatches(), new Comparator() {
				public int compare(Object o1, Object o2) {
					if (o1 instanceof ProductPlate && o2 instanceof ProductPlate) {
						try {
							int p1StepNumber = 0;
							int p2StepNumber = 0;
							if (o1 instanceof PseudoProductPlate)
								if (sortedColumnAscending)
									p1StepNumber = 9999;
								else 
									p1StepNumber = 0;
							else 
								p1StepNumber = Integer.parseInt(((ProductPlate) o1).getStepNumber());
							if (o2 instanceof PseudoProductPlate )
								if (sortedColumnAscending)
									p2StepNumber = 9999;
								else 
									p2StepNumber = 0;
							else
								p2StepNumber = Integer.parseInt(((ProductPlate) o2).getStepNumber());
							if (p1StepNumber == p2StepNumber)
								return 0;
							else if (p1StepNumber > p2StepNumber)
								if (sortedColumnAscending)
									return 1;
								else 
									return -1;
							else 
								if (sortedColumnAscending)
									return -1;
								else
									return 1;
						} catch (NullPointerException e) {
							// This should go away when the step number is FINALLY set in the product plate model!!!
							return 0;
						} catch (NumberFormatException e) { // should not happen
							//e.printStackTrace();
						}
					}
					return 0;
				}
			});
		}
	}

}
