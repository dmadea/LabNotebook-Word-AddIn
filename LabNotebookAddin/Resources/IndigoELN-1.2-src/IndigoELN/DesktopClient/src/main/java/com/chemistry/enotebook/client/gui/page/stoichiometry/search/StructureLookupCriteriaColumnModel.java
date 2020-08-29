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
package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

import com.chemistry.enotebook.client.gui.page.stoichiometry.AbstractBatchColumnModel;
import com.chemistry.enotebook.client.gui.page.stoichiometry.ColumnProperties;
import com.chemistry.enotebook.domain.MonomerBatchModel;

/**
 * 
 * 
 *
 */
public class StructureLookupCriteriaColumnModel extends AbstractBatchColumnModel {
	public static final int FORMULA = 0;
	public static final int MOLECULAR_WEIGHT = 1;
	public static final int INFO = 2;
	public static final int MAX_COLS = 3;
	private static StructureLookupCriteriaColumnModel instance = null;

	/**
	 * Constructs the displayable model for ProductBatches
	 */
	private StructureLookupCriteriaColumnModel() {
		init();
	}

	public static StructureLookupCriteriaColumnModel getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new StructureLookupCriteriaColumnModel();
			instance.init();
		}
	}

	/**
	 * Fill the Map with the column properties to be used in the order they should appear.
	 * 
	 */
	private void init() {
		// Initializes only the visible columns
		ColumnProperties colProps = new ColumnProperties(FORMULA, "Mol. Formula", String.class, 110, 170, 100);
		colPropMap.put(new Integer(FORMULA), colProps);
		// colPropMap.put(new Integer(FORMULA), new ColumnProperties(FORMULA,
		// "Formula", String.class, 110, 200, 50));
		colProps = new ColumnProperties(MOLECULAR_WEIGHT, "Mol.Wt.", Double.class, 60, 75, 50);
		colPropMap.put(new Integer(MOLECULAR_WEIGHT), colProps);
		// colPropMap.put(new Integer(MOLECULAR_WEIGHT), new
		// ColumnProperties(MOLECULAR_WEIGHT, "Mol.Wt.", Double.class, 50, 100,
		// 40));
		colPropMap.put(new Integer(INFO), new ColumnProperties(INFO, "Status", String.class, 110, 200, 50));
	}

	public int getColumnPreferredWidth(int colModelIndex) {
		return ((ColumnProperties) colPropMap.get(new Integer(colModelIndex))).prefWidth;
	}

	public int getColumnMaxWidth(int colModelIndex) {
		return ((ColumnProperties) colPropMap.get(new Integer(colModelIndex))).maxWidth;
	}

	public int getColumnMinWidth(int colModelIndex) {
		return ((ColumnProperties) colPropMap.get(new Integer(colModelIndex))).minWidth;
	}

	/**
	 * Used to extract value from batch to display in TableModel
	 * 
	 * @param batch =
	 *            to be displayed
	 * @param column =
	 *            associated with constants here to indicate which to display
	 * @return Object = value to be displayed.
	 */
	public Object getValueFromBatch(MonomerBatchModel rb, int column) {
		Object result = null;
		if (rb != null) {
			switch (column) {
				case StructureLookupCriteriaColumnModel.FORMULA:
					result = rb.getMolecularFormula();
					break;
				case StructureLookupCriteriaColumnModel.MOLECULAR_WEIGHT:
					result = new Double(rb.getMolWgt());
					break;
				default:
					break;
			}
		}
		return result;
	}
}
