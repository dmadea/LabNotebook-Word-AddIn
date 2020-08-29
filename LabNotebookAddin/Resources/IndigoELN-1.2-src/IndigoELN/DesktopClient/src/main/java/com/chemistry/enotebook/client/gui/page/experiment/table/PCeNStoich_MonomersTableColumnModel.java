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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.page.stoichiometry.AbstractBatchColumnModel;
import com.chemistry.enotebook.client.gui.page.stoichiometry.ColumnProperties;
import com.chemistry.enotebook.domain.StoicModelInterface;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

public class PCeNStoich_MonomersTableColumnModel extends AbstractBatchColumnModel{
	
	public static final int IS_LIST = 0;
	public static final int LABEL = 1;
	public static final int COMPOUND_ID = 2;
	public static final int CHEMICAL_NAME = 3 ;
	public static final int BATCH_LOT_NUMBER = 4;
	public static final int CAS_NUMBER = 5;
	public static final int MOLECULAR_WEIGHT = 6;
	public static final int WEIGHT = 7;
	public static final int VOLUME = 8;
	public static final int MOLES = 9;
	public static final int RXN_EQUIVS = 10;
	public static final int LIMITING = 11;
	public static final int RXN_ROLE = 12;
	public static final int DENSITY = 13;
	public static final int MOLARITY = 14;
	public static final int SOLVENT = 15;
	public static final int PURITY = 16;
	public static final int FORMULA = 17;
	public static final int SALT_CODE = 18;
	public static final int SALT_EQUIVS = 19;
	public static final int LOADING = 20;
	public static final int HAZARD_COMMENTS = 21;
	public static final int COMMENTS = 22;
	public static final int HIDDEN_DATA = 23;
	public static final int MAX_COLS = 23;
	
	private static PCeNStoich_MonomersTableColumnModel instance = null;

	/**
	 * Constructs the displayable model for ProductBatches
	 */
	private PCeNStoich_MonomersTableColumnModel() {
		init();
	}

	public static PCeNStoich_MonomersTableColumnModel getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new PCeNStoich_MonomersTableColumnModel();
			instance.init();
		}
	}

	/**
	 * Fill the Map with the column properties to be used in the order they should appear.
	 * 
	 */
	private void init() {
		// Initializes only the visible columns
		ColumnProperties colProps = new ColumnProperties(IS_LIST, "LIST", String.class, 60, 100, 60);
		colPropMap.put(new Integer(COMPOUND_ID), colProps);
		colProps = new ColumnProperties(LABEL, "LABEL", String.class, 100, 160, 100);
		colPropMap.put(new Integer(COMPOUND_ID), colProps);
		colProps = new ColumnProperties(COMPOUND_ID, "Compound ID", String.class, 120, 200, 120);
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
		colProps = new ColumnProperties(SOLVENT, "Solvent", String.class, 150, 200, 150);
		colPropMap.put(new Integer(SOLVENT), colProps);
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
		colProps = new ColumnProperties(COMMENTS, "Comments", String.class, 170, 230, 150);
		colPropMap.put(new Integer(COMMENTS), colProps);
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
	public Object getValueFromBatch(StoicModelInterface stoicModel, int column) {
		Object result = null;
		if (stoicModel != null) {
			switch (column) {
				case PCeNStoich_MonomersTableColumnModel.IS_LIST:
					boolean isList= stoicModel.isList();
					if(isList){
						result = "YES";
					}
					else{
						result = "NO";
					}
					break;
				case PCeNStoich_MonomersTableColumnModel.LABEL:
					result = stoicModel.getStoicLabel();
					break;	
				case PCeNStoich_MonomersTableColumnModel.FORMULA:
					result = stoicModel.getStoicMolecularFormula();
					break;
				case PCeNStoich_MonomersTableColumnModel.COMPOUND_ID:
					result = (stoicModel.getStoicCompoundId() != null) ? stoicModel.getStoicCompoundId() : "";
					break;
				case PCeNStoich_MonomersTableColumnModel.BATCH_LOT_NUMBER:
					BatchNumber bn = stoicModel.getStoicBatchNumber();
					result = (bn != null) ? bn.getBatchNumber() : "";
					break;
				case PCeNStoich_MonomersTableColumnModel.MOLECULAR_WEIGHT:
					// Amount amt = new Amount(UnitType.SCALAR, 0.0);
					// amt.setValue(rb.getMolWgt());
					// amt.setCalculated(rb.getMolecularWeightAmount().isCalculated());
					// result = amt;
					result = stoicModel.getStoicMolecularWeightAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.WEIGHT:
					result = stoicModel.getStoicWeightAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.MOLES:
					result = stoicModel.getStoicMoleAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.DENSITY:
					result = stoicModel.getStoicDensityAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.VOLUME:
					result = stoicModel.getStoicVolumeAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.MOLARITY:
					result = stoicModel.getStoicMolarAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.RXN_EQUIVS:
					result = stoicModel.getStoicRxnEquivsAmount();
					break;
				case PCeNStoich_MonomersTableColumnModel.LIMITING:
					result = new Boolean(stoicModel.isStoicLimiting());
					break;
				case PCeNStoich_MonomersTableColumnModel.RXN_ROLE:
					result = stoicModel.getStoicReactionRole();
					break;
				case PCeNStoich_MonomersTableColumnModel.PURITY:
					
					//result = stoicModel.getPurityAmount();
					
					break;
				case PCeNStoich_MonomersTableColumnModel.SALT_CODE:
					if (stoicModel.getStoicBatchSaltForm()!= null)
						result = stoicModel.getStoicBatchSaltForm().getCode();
					break;
				case PCeNStoich_MonomersTableColumnModel.SALT_EQUIVS:
					if(stoicModel.getStoicBatchSaltEquivs() != null)
					{
					result = stoicModel.getStoicBatchSaltEquivs();
					}
					break;
				case PCeNStoich_MonomersTableColumnModel.CHEMICAL_NAME:
					result = stoicModel.getStoicChemicalName();
					break;
				case PCeNStoich_MonomersTableColumnModel.LOADING:
					//result = stoicModel.getLoadingAmount();
					
					break;
				case PCeNStoich_MonomersTableColumnModel.CAS_NUMBER:
					result = stoicModel.getStoicBatchCASNumber();
					break;
				case PCeNStoich_MonomersTableColumnModel.HAZARD_COMMENTS:
					result = stoicModel.getStoicHazardsComments();
					break;
				default:
					break;
			}
		}
		return result;
	}
}
