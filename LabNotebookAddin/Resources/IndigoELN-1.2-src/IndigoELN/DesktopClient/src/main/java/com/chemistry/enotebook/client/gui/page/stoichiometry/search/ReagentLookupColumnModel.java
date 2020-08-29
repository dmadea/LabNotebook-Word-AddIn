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
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.CeNConstants;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 *
 */
public class ReagentLookupColumnModel extends AbstractBatchColumnModel {
	public static final int COMPOUND_ID = 0;
	public static final int BATCH_LOT_NUMBER = 1;
	public static final int MOLECULAR_WEIGHT = 2;
	// public static final int SALT_CODE = 3;
	// public static final int SALT_EQUIVS = 4;
	public static final int FORMULA = 3;
	public static final int MAX_COLS = 4;
	private static ReagentLookupColumnModel instance = null;

	/**
	 * Constructs the displayable model for ProductBatches
	 */
	private ReagentLookupColumnModel() {
		init();
	}

	public static ReagentLookupColumnModel getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new ReagentLookupColumnModel();
			instance.init();
		}
	}

	/**
	 * Fill the Map with the column properties to be used in the order they should appear.
	 * 
	 */
	private void init() {
		// Initializes only the visible columns
		ColumnProperties colProps = new ColumnProperties(FORMULA, "Mol. Formula", String.class, 150, 200, 140);
		colPropMap.put(new Integer(FORMULA), colProps);
		colProps = new ColumnProperties(COMPOUND_ID, "Compound ID", String.class, 140, 200, 130);
		colPropMap.put(new Integer(COMPOUND_ID), colProps);
		colProps = new ColumnProperties(BATCH_LOT_NUMBER, "Nbk Batch #", String.class, 160, 150, 100);
		colPropMap.put(new Integer(BATCH_LOT_NUMBER), colProps);
		colProps = new ColumnProperties(MOLECULAR_WEIGHT, "Mol.Wt.", Double.class, 75, 100, 50);
		colPropMap.put(new Integer(MOLECULAR_WEIGHT), colProps);
		// colProps = new ColumnProperties(SALT_CODE, "Salt Code",
		// String.class);
		// colPropMap.put(new Integer(SALT_CODE), colProps);
		// colProps = new ColumnProperties(SALT_EQUIVS, "Salt EQ",
		// Double.class);
		// colPropMap.put(new Integer(SALT_EQUIVS), colProps);
	}

	public int getColumnPreferredWidth(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).prefWidth;
	}

	public int getColumnMaxWidth(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).maxWidth;
	}

	public int getColumnMinWidth(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).minWidth;
	}

	// public List getRowFromBatch(AbstractBatch batch) {
	// ArrayList row = new ArrayList();
	// row.add(ReagentLookupColumnModel.COMPOUND_ID, batch.getRegNumber());
	// row.add(ReagentLookupColumnModel.BATCH_LOT_NUMBER,
	// batch.getBatchNumberAsString());
	// row.add(ReagentLookupColumnModel.MOLECULAR_WEIGHT, new
	// Double(batch.getMolWgt()));
	// row.add(ReagentLookupColumnModel.FORMULA, batch.getMolFormula());
	// row.add(ReagentLookupColumnModel.HIDDEN_DATA, batch);
	// return row;
	// }
	//    
	// /* (non-Javadoc)
	// * @see
	// com.chemistry.enotebook.client.gui.page.stoichiometry.AbstractBatchColumnModel#getRowFromBatch(com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch)
	// */
	// public List getRowFromBatch(StructureSearchResultData batch) {
	// ArrayList row = new ArrayList();
	// row.add(ReagentLookupColumnModel.COMPOUND_ID, batch.getCompoundId());
	// if (batch.getNbRef() != null && batch.getNbRef().length() > 0)
	// row.add(ReagentLookupColumnModel.BATCH_LOT_NUMBER, batch.getNbRef());
	// else
	// row.add(ReagentLookupColumnModel.BATCH_LOT_NUMBER,
	// batch.getConversationalBatchNumber());
	// row.add(ReagentLookupColumnModel.MOLECULAR_WEIGHT, new
	// Double(batch.getMolWt()));
	// row.add(ReagentLookupColumnModel.FORMULA, batch.getMolFormula());
	// row.add(ReagentLookupColumnModel.HIDDEN_DATA, batch);
	// return row;
	// }
	//	
	/**
	 * Used to extract value from batch to display in TableModel
	 * 
	 * @param batch =
	 *            to be displayed
	 * @param column =
	 *            associated with constants here to indicate which to display
	 * @return Object = value to be displayed.
	 */
	public Object getValueFromBatch(BatchModel rb, int column) {
		Object result = null;
		if (rb != null) {
			switch (column) {
				case ReagentLookupColumnModel.FORMULA:
					result = rb.getMolecularFormula();
					break;
				case ReagentLookupColumnModel.COMPOUND_ID:
					result = "";
					if (StringUtils.isNotBlank(rb.getCompoundId())) {
						result = rb.getCompoundId();
						//TODO workaround for CENSTR 
						if (((String)result).startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
							result = "";
						}
					}					 
					break;
				case ReagentLookupColumnModel.BATCH_LOT_NUMBER:
					if (StringUtils.isNotBlank(rb.getBatchNumberAsString()))
						result = rb.getBatchNumberAsString();
					else
						result = rb.getOriginalBatchNumber();
					break;
				case ReagentLookupColumnModel.MOLECULAR_WEIGHT:
					result = new Double(rb.getMolWgt());
					break;
				// case ReagentLookupColumnModel.SALT_CODE:
				// if (rb.getSaltForm() != null) result =
				// rb.getSaltForm().getCode();
				// break;
				// case ReagentLookupColumnModel.SALT_EQUIVS:
				// result = new Double(rb.getSaltEquivs());
				// break;
				// case ReagentLookupColumnModel.CHEMICAL_NAME:
				// result = rb.getCompound().getChemicalName();
				// break;
				default:
					break;
			}
		}
		return result;
	}
}
