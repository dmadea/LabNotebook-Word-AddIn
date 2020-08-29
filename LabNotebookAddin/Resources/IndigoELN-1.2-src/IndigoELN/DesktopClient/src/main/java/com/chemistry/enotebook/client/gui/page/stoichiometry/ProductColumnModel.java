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
package com.chemistry.enotebook.client.gui.page.stoichiometry;

import com.chemistry.enotebook.experiment.datamodel.batch.ProductBatch;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

/**
 * 
 * 
 *
 * 
 * I have decided to use this technique to build the rows of the table as it is the localized instance that knows what to display
 * and what not to.
 */
public class ProductColumnModel extends AbstractBatchColumnModel {
	public static final int NAME = 0;
	public static final int FORMULA = 1;
	public static final int MOLECULAR_WEIGHT = 2;
	public static final int EXACT_MASS = 3;
	public static final int THEO_WEIGHT = 4;
	public static final int THEO_MOLES = 5;
	public static final int SALT_CODE = 6;
	public static final int SALT_EQUIVS = 7;
	public static final int HAZARD_COMMENTS = 8;
	public static final int NUM_EQUIV = 9;
	public static final int HIDDEN_DATA = 10;
	public static final int MAX_COLS = 9;
	public static final String CHEMICAL_NAME = "Chemical Name";
	public static final String FORMULA_STR = "Formula";
	private static ProductColumnModel instance = null;

	/**
	 * Constructs the displayable model for ProductBatches
	 */
	private ProductColumnModel() {
		init();
	}

	public static ProductColumnModel getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new ProductColumnModel();
			instance.init();
		}
	}

	/**
	 * Fill the Map with the column properties to be used in the order they should appear.
	 * 
	 */
	private void init() {
		// Initializes only the visible columns
		ColumnProperties colProps = new ColumnProperties(NAME, "Chemical Name", String.class, 130, 200, 100);
		colPropMap.put(new Integer(NAME), colProps);
		colProps = new ColumnProperties(FORMULA, "Formula", String.class, 130, 200, 130);
		colPropMap.put(new Integer(FORMULA), colProps);
		colProps = new ColumnProperties(MOLECULAR_WEIGHT, "Mol.Wt.", Amount.class, 60, 100, 30);
		colPropMap.put(new Integer(MOLECULAR_WEIGHT), colProps);
		colProps = new ColumnProperties(EXACT_MASS, "Exact Mass", Double.class, 70, 100, 30);
		colPropMap.put(new Integer(EXACT_MASS), colProps);
		colProps = new ColumnProperties(THEO_WEIGHT, "Theo. Wgt.", Amount.class, 90, 160, 70);
		colPropMap.put(new Integer(THEO_WEIGHT), colProps);
		colProps = new ColumnProperties(THEO_MOLES, "mMoles", Amount.class, 60, 120, 60);
		colPropMap.put(new Integer(THEO_MOLES), colProps);
		colProps = new ColumnProperties(NUM_EQUIV, "EQ", Amount.class, 40, 70, 30);
		colPropMap.put(new Integer(NUM_EQUIV), colProps);
		colProps = new ColumnProperties(SALT_CODE, "Salt Code", String.class);
		colPropMap.put(new Integer(SALT_CODE), colProps);
		colProps = new ColumnProperties(SALT_EQUIVS, "Salt EQ", Double.class, 60, 60, 60);
		colPropMap.put(new Integer(SALT_EQUIVS), colProps);
		colProps = new ColumnProperties(HAZARD_COMMENTS, "Hazard Comments", String.class, 170, 230, 150);
		colPropMap.put(new Integer(HAZARD_COMMENTS), colProps);
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
	public Object getValueFromBatch(ProductBatch pb, int column) {
		Object result = null;
		if (pb != null) {
			switch (column) {
				case ProductColumnModel.NAME:
					result = pb.getCompound().getCompoundName();
					if (result == null || ((String) result).length() == 0)
						result = "P" + pb.getIntendedBatchAdditionOrder();
					break;
				case ProductColumnModel.FORMULA:
					result = pb.getMolFormula();
					break;
				case ProductColumnModel.MOLECULAR_WEIGHT:
					result = pb.getMolecularWeightAmount();
					break;
				case ProductColumnModel.EXACT_MASS:
					result = new Double(pb.getCompound().getExactMass());
					break;
				case ProductColumnModel.THEO_WEIGHT:
					result = pb.getWeightAmount();
					break;
				case ProductColumnModel.THEO_MOLES:
					result = pb.getMoleAmount();
					break;
				case ProductColumnModel.NUM_EQUIV:
					result = pb.getRxnEquivsAmount();
					break;
				case ProductColumnModel.SALT_CODE:
					if (pb.getSaltForm() != null)
						result = pb.getSaltForm().getCode();
					break;
				case ProductColumnModel.SALT_EQUIVS:
		        	if (pb.getSaltEquivsSet() && pb.getSaltForm() != null && !pb.getSaltForm().isParentForm())
		        		result = new Double(pb.getSaltEquivs());
		        	else
		        		result = null;
					result = new Double(pb.getSaltEquivs());
					break;
				case ProductColumnModel.HAZARD_COMMENTS:
					result = pb.getHazardComments();
					break;
				default:
					break;
			}
		}
		return result;
	}
}
