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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.stoichiometry.AbstractBatchColumnModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 *
 */
public class StructureLookupCriteriaTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1293257800274250716L;
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private ArrayList<String> labels = new ArrayList<String>();
	private ArrayList<String> errors = new ArrayList<String>();
	private List<MonomerBatchModel> reagents = new ArrayList<MonomerBatchModel>();
	private StructureLookupCriteriaColumnModel slccm = StructureLookupCriteriaColumnModel.getInstance();

	private StructureLookupCriteriaTableModel() {
	}

	/**
	 * @param nbPage
	 */
	public StructureLookupCriteriaTableModel(List<MonomerBatchModel> reagentsToShow) {
		super();
		init(reagentsToShow);
	}

	private void init(List<MonomerBatchModel> reagList) {
		reagents = reagList;
		labels.clear();
		errors.clear();
		if (reagents != null) {
			for (int i = 0; i < reagents.size(); i++) {
				labels.add(i, "Searching...");
			}
		}
		refresh();
	}

	//
	// Overrides/Implements TableModel
	//
	public Class getColumnClass(int columnIndex) {
		return slccm.getColumnClass(columnIndex);
	}

	public String getColumnName(int col) {
		return slccm.getColumnName(col);
	}

	public int getColumnCount() {
		return slccm.getColumnCount();
	}

	public int getRowCount() {
		return reagents.size();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int r, int c) {
		Object result = null;
		if (r < reagents.size()) {
			MonomerBatchModel row =  reagents.get(r);
			if (c == StructureLookupCriteriaColumnModel.INFO) {
				result = labels.get(r);
			} else if (c <= (slccm.getColumnCount() - 1)) {
				result = slccm.getValueFromBatch(row, c);
			}
		}
		return result;
	}

	public MonomerBatchModel getBatchAt(int r) {
		if (r <= getRowCount() - 1 && r >= 0) {
			return reagents.get(r);
		}
		return null;
	}

	public void refresh() {
		fireTableStructureChanged();
	}

	public AbstractBatchColumnModel getAbstractBatchColumnModel() {
		return slccm;
	}

	/**
	 * Only INFO field is settable.
	 */
	public void setValueAt(Object obj, int r, int c) {
		if (r >= 0 && r < reagents.size() && c == StructureLookupCriteriaColumnModel.INFO) {
			if (r >= labels.size()) {
				labels.add(r, (String) obj);
			} else {
				labels.set(r, (String) obj);
			}
		}
	}

	public String getToolTipText(int row) {
		String result = null;
		if (row >= 0 && row < errors.size() && errors.get(row) != null)
			result = (String) errors.get(row);
		return result;
	}

	public void setErrorMsg(int row, String msg) {
		if (row >= 0 && row < reagents.size()) {
			if (row == errors.size() || row >= errors.size())
				errors.add(row, msg);
			else
				errors.set(row, msg);
		}
	}
}
