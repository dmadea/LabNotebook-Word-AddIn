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
import com.chemistry.enotebook.domain.BatchModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 *
 */
public class ReagentLookupTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1500445025357005539L;
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	// private ArrayList rows = new ArrayList();
	protected List<BatchModel> reagents = new ArrayList<BatchModel>();
	private ReagentLookupColumnModel rcm = ReagentLookupColumnModel.getInstance();

	private ReagentLookupTableModel() {
		init();
	}

	/**
	 * @param nbPage
	 */
	public ReagentLookupTableModel(List<BatchModel> reagents) {
		super();
		if (reagents == null)
			reagents = new ArrayList<BatchModel>();
		this.reagents = reagents;
		init();
	}

	private void init() {
		// rows.clear();
		// ReagentLookupColumnModel rlcm =
		// ReagentLookupColumnModel.getInstance();
		// for (Iterator it = reagents.iterator(); it.hasNext();) {
		// Object result = it.next();
		// if (result instanceof StructureSearchResultData)
		// rows.add(rlcm.getRowFromBatch((StructureSearchResultData) result));
		// else
		// rows.add(rlcm.getRowFromBatch((AbstractBatch) result));
		// }
		// fireTableStructureChanged();
	}

	//
	// Overrides/Implements TableModel
	//
	public Class getColumnClass(int columnIndex) {
		return rcm.getColumnClass(columnIndex);
	}

	public String getColumnName(int col) {
		return rcm.getColumnName(col);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public int getColumnCount() {
		return rcm.getColumnCount();
	}

	public int getRowCount() {
		return reagents.size();
	}

	public BatchModel getBatchAt(int r) {
		if (r < reagents.size()) {
			return reagents.get(r);
		}
		return null;
	}

	public Object getValueAt(int r, int c) {
		if (r < reagents.size()) {
			return rcm.getValueFromBatch(reagents.get(r), c);
		}
		return null;
	}

	// public Object getValueAt(int r, int c)
	// {
	// if (r < rows.size()) {
	// ArrayList row = ((ArrayList) rows.get(r));
	//
	// if (c <= (row.size() - 1)) {
	// return row.get(c);
	// }
	// }
	//
	// return null;
	// }
	//    
	// public AbstractBatch getBatchAtRow(int r) {
	// if (r <= getRowCount() - 1 && r >= 0) {
	// return (AbstractBatch)
	// ((ArrayList)rows.get(r)).get(ReagentLookupColumnModel.HIDDEN_DATA);
	// }
	// return null;
	// }
	public void refresh() {
		fireTableStructureChanged();
	}
}
