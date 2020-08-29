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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.domain.StoicModelInterface;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * 
 * 
 */
// vb 11/5 public class ParallelCeNTableModel extends AbstractTableModel implements SortableTableModel {
public class PCeNTableModel extends AbstractTableModel { //implements SortableTableModel {
	
	private static final long serialVersionUID = 3496951833973133836L;
	
	protected int sortedColumnIndex = -1;
	protected boolean sortedColumnAscending = true;
	protected PCeNTableModelConnector connector;

	public PCeNTableModel() {
	}

	public PCeNTableModel(PCeNTableModelConnector connector) {
		if (connector == null || connector.getHeaderNames() == null)
			throw new IllegalArgumentException("ParallelCeNTableModel received NULL arguments");
		this.connector = connector;
	}

	public boolean isSortable(int col) {
		return true;
	}

	public void sortColumn(int col, boolean ascending) {
		connector.sortBatches(col, ascending);
		//this.fireTableStructureChanged();
//		sortedColumnIndex = col;
//		sortedColumnAscending = ascending;
//		if (!connector.isStructureHidden())
//			Collections.sort(connector.getListOfBatchesWithMolStrings(), new ColumnComparator(col, ascending));
//		else
//			Collections.sort(connector.getAbstractBatches(), new ColumnComparator(col, ascending));
	}

	public String getColumnName(int col) {
		String[] headerNames = connector.getHeaderNames();
		if (headerNames != null && col > -1 && col < headerNames.length) {
			return headerNames[col];
		}
		return "";
	}

	public int getColumnCount() {
		return connector.getHeaderNames().length;
	}

	public Object getValueAt(int row, int col) {
		return connector.getValue(row, col);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (connector.getPageModel().isEditable())
			return connector.isCellEditable(rowIndex, columnIndex);
		else
			return false;
	}
	public boolean isColumnEditable(int columnIndex) {
		return connector.isCellEditable(0, columnIndex);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		connector.setValue(aValue, rowIndex, columnIndex);
	
	}

	public int getRowCount() {
		
		return connector.getRowCount();
	}

	public int getSortedColumnIndex() {
		return sortedColumnIndex;
	}

	public void setSortedColumnIndex(int sortedColumnIndex) {
		this.sortedColumnIndex = sortedColumnIndex;
	}

	public boolean isSortedColumnAscending() {
		return sortedColumnAscending;
	}

	public void setSortedColumnAscending(boolean sortedColumnAscending) {
		this.sortedColumnAscending = sortedColumnAscending;
	}

	public int getModelIndexFromHeaderName(String name) {
		for (int i = 0; i < connector.getHeaderNames().length; i++) {
			String temp = connector.getHeaderNames()[i];
			if (name.equalsIgnoreCase(temp)) {
				return i;
			}
		}
		return -1;
	}
//	public void updateColumn(String cloumnname, String newValue){
//		connector.updateColumn(cloumnname, newValue);
//	}

	// vb 11/26
	public void updateColumn(String cloumnname, Object newValue) {
		connector.updateColumn(cloumnname, newValue);
	}
	/**
	 * @return the connector
	 */
	public PCeNTableModelConnector getConnector() {
		return connector;
	}
	
	public StoicModelInterface getStoicElementAt(int r) {
		List<StoicModelInterface> rows = connector.getStoicElementListInTransactionOrder();
		if (rows != null && r >= 0 && r < rows.size()) {
			return rows.get(r);
		}
		return null;
	}
	

	public void refresh() {
		fireTableDataChanged();
	}
	
	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		return this.getConnector().getColumnInfoFromModelIndex(modelIndex);
	}

}
