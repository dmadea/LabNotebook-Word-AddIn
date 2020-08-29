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

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.ReagentBatch;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

/**
 * 
 * 
 *
 * 
 * I have decided to use this technique to build the rows of the table as it is the localized instance that knows what to display
 * and what not to.
 */
public class ReactantColumnModel extends AbstractBatchColumnModel {
	public static final int COMPOUND_ID = 0;
	public static final int CHEMICAL_NAME = 1;
	public static final int BATCH_LOT_NUMBER = 2;
	public static final int CAS_NUMBER = 3;
	public static final int MOLECULAR_WEIGHT = 4;
	public static final int WEIGHT = 5;
	public static final int VOLUME = 6;
	public static final int MOLES = 7;
	public static final int RXN_EQUIVS = 8;
	public static final int LIMITING = 9;
	public static final int RXN_ROLE = 10;
	public static final int DENSITY = 11;
	public static final int MOLARITY = 12;
	public static final int PURITY = 13;
	public static final int FORMULA = 14;
	public static final int SALT_CODE = 15;
	public static final int SALT_EQUIVS = 16;
	public static final int LOADING = 17;
	public static final int HAZARD_COMMENTS = 18;
	public static final int HIDDEN_DATA = 19;
	public static final int MAX_COLS = 19;
	public static final String COMP_ID = "Compound ID";
	public static final String CHEM_NAME = "Chemical Name";
	public static final String BATCH_NUM = "Nbk Batch #";
	public static final String CAS_NUM = "CAS Number";
	public static final String FORMUL = "Formula";
    public static final String HAZARD = "Hazard Comments";
	private static ReactantColumnModel instance = null;

	/**
	 * Constructs the displayable model for ProductBatches
	 */
	private ReactantColumnModel() {
		init();
	}

	public static ReactantColumnModel getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new ReactantColumnModel();
			instance.init();
		}
	}

	/**
	 * Fill the Map with the column properties to be used in the order they should appear.
	 * 
	 */
	private void init() {
		// Initializes only the visible columns
		ColumnProperties colProps = new ColumnProperties(COMPOUND_ID, "Compound ID", String.class, 120, 200, 120);
		colPropMap.put(new Integer(COMPOUND_ID), colProps);
		colProps = new ColumnProperties(BATCH_LOT_NUMBER, "Nbk Batch #", String.class, 110, 150, 70);
		colPropMap.put(new Integer(BATCH_LOT_NUMBER), colProps);
		colProps = new ColumnProperties(CAS_NUMBER, "CAS Number", String.class, 90, 100, 80);
		colPropMap.put(new Integer(CAS_NUMBER), colProps);
		colProps = new ColumnProperties(MOLECULAR_WEIGHT, "Mol.Wt.", Amount.class, 60, 100, 30);
		colPropMap.put(new Integer(MOLECULAR_WEIGHT), colProps);
		colProps = new ColumnProperties(WEIGHT, "Weight", Amount.class, 90, 160, 70, UnitType.MASS);
		colPropMap.put(new Integer(WEIGHT), colProps);
		colProps = new ColumnProperties(VOLUME, "Volume", Amount.class, 90, 160, 70, UnitType.VOLUME);
		colPropMap.put(new Integer(VOLUME), colProps);
		colProps = new ColumnProperties(MOLES, "mMoles", Amount.class, 60, 120, 60, UnitType.MOLES);
		colPropMap.put(new Integer(MOLES), colProps);
		colProps = new ColumnProperties(RXN_EQUIVS, "EQ", Amount.class, 40, 70, 30);
		colPropMap.put(new Integer(RXN_EQUIVS), colProps);
		colProps = new ColumnProperties(LIMITING, "Limiting", Boolean.class);
		colPropMap.put(new Integer(LIMITING), colProps);
		colProps = new ColumnProperties(RXN_ROLE, "Rxn Role", BatchType.class, 75, 130, 70);
		colPropMap.put(new Integer(RXN_ROLE), colProps);
		colProps = new ColumnProperties(DENSITY, "Density", Amount.class, 80, 150, 70);
		colPropMap.put(new Integer(DENSITY), colProps);
		colProps = new ColumnProperties(MOLARITY, "Molarity", Amount.class, 90, 180, 50, UnitType.MOLAR);
		colPropMap.put(new Integer(MOLARITY), colProps);
		colProps = new ColumnProperties(PURITY, "Purity", Amount.class, 50, 80, 30);
		colPropMap.put(new Integer(PURITY), colProps);
		colProps = new ColumnProperties(FORMULA, "Formula", String.class, 100, 200, 70);
		colPropMap.put(new Integer(FORMULA), colProps);
		colProps = new ColumnProperties(SALT_CODE, "Salt Code", String.class);
		colPropMap.put(new Integer(SALT_CODE), colProps);
		colProps = new ColumnProperties(SALT_EQUIVS, "Salt EQ", Double.class, 50, 80, 30);
		colPropMap.put(new Integer(SALT_EQUIVS), colProps);
		colProps = new ColumnProperties(CHEMICAL_NAME, "Chemical Name", String.class, 150, 200, 150);
		colPropMap.put(new Integer(CHEMICAL_NAME), colProps);
		colProps = new ColumnProperties(LOADING, "Load Factor", Amount.class, 90, 150, 70);
		colPropMap.put(new Integer(LOADING), colProps);
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
	public Object getValueFromBatch(ReagentBatch rb, int column) {
		Object result = null;
		if (rb != null) {
			switch (column) {
				case ReactantColumnModel.FORMULA:
					result = rb.getMolFormula();
					break;
				case ReactantColumnModel.COMPOUND_ID:
					result = (rb.getRegNumber() != null) ? rb.getRegNumber() : "";
					break;
				case ReactantColumnModel.BATCH_LOT_NUMBER:
					result = rb.getBatchNumberAsString();
					break;
				case ReactantColumnModel.MOLECULAR_WEIGHT:
					// Amount amt = new Amount(UnitType.SCALAR, 0.0);
					// amt.setValue(rb.getMolWgt());
					// amt.setCalculated(rb.getMolecularWeightAmount().isCalculated());
					// result = amt;
					result = rb.getMolecularWeightAmount();
					break;
				case ReactantColumnModel.WEIGHT:
					result = rb.getWeightAmount();
					break;
				case ReactantColumnModel.MOLES:
					result = rb.getMoleAmount();
					break;
				case ReactantColumnModel.DENSITY:
					result = rb.getDensityAmount();
					break;
				case ReactantColumnModel.VOLUME:
					result = rb.getVolumeAmount();
					break;
				case ReactantColumnModel.MOLARITY:
					result = rb.getMolarAmount();
					break;
				case ReactantColumnModel.RXN_EQUIVS:
					result = rb.getRxnEquivsAmount();
					break;
				case ReactantColumnModel.LIMITING:
					result = new Boolean(rb.isLimiting());
					break;
				case ReactantColumnModel.RXN_ROLE:
					result = rb.getType();
					break;
				case ReactantColumnModel.PURITY:
					// Used to catch older versions of Purity
					// TODO: Remove when database is purged.
					// if (rb.getPurityAmount().isCalculated() &&
					// CeNNumberUtils.doubleEquals(rb.getPurityAmount().doubleValue(),
					// 0.0, 0.001))
					// rb.getPurityAmount().setValue("100");
					// result = rb.getPurityAmount();
					result = rb.getPurityAmount();
					break;
				case ReactantColumnModel.SALT_CODE:
					if (rb.getSaltForm() != null)
						result = rb.getSaltForm().getCode();
					break;
				case ReactantColumnModel.SALT_EQUIVS:
		        	if (rb.getSaltEquivsSet() && rb.getSaltForm() != null && !rb.getSaltForm().isParentForm())
		        		result = new Double(rb.getSaltEquivs());
		        	else
		        		result = null;
					break;
				case ReactantColumnModel.CHEMICAL_NAME:
					result = rb.getCompound().getChemicalName();
					break;
				case ReactantColumnModel.LOADING:
					result = rb.getLoadingAmount();
					break;
				case ReactantColumnModel.CAS_NUMBER:
					result = rb.getCompound().getCASNumber();
					break;
				case ReactantColumnModel.HAZARD_COMMENTS:
					result = rb.getHazardComments();
					break;
				default:
					break;
			}
		}
		return result;
	}
}
