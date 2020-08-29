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
package com.chemistry.enotebook.client.gui.preferences.stoic;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * 
 *
 */
public class StoicColumnPrefTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7366326639737131887L;
	public static ArrayList data;
	public ArrayList columnNames;
	private static String[] s_columnNames = { "<html>Display<br>Order</html>", "Column Display Name", "Data Type",
			"Unit List & Default", "Significant Number", "Show?" };
	private static Object[][] sampleData = { { "1", "Chemical Name", "Text", "na", new Integer(1), new Boolean(false) },
			{ "2", "Mol. Wt.", "Number", "na", new Integer(1), new Boolean(false) },
			{ "3", "Purity (%Wt.)", "Number", "na", new Integer(1), new Boolean(false) },
			{ "4", "Weight (unit)", "Number", "na", new Integer(1), new Boolean(false) },
			{ "5", "mMoles", "Number", "na", new Integer(1), new Boolean(false) } };
	public static ArrayList columnClasses = new ArrayList(Arrays.asList(new Class[] { String.class, String.class, String.class,
			String.class, Integer.class, Boolean.class, }));

	/**
	 * @param nbPage
	 */
	public StoicColumnPrefTableModel() {
		super();
		columnNames = new ArrayList();
		columnNames.add("Display Order");
		columnNames.add("Column Display Name");
		columnNames.add("Data Type");
		columnNames.add("Unit List & Default");
		columnNames.add("Significant Number");
		columnNames.add("Show?");
		init();
	}

	private void init() {
		setData();
		fireTableDataChanged();
	}

	public Class getColumnClass(int columnIndex) {
		return (Class) columnClasses.get(columnIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public String getColumnName(int col) {
		return (String) columnNames.get(col);
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public int getRowCount() {
		int result = 0;
		if (data != null)
			result = data.size();
		return result;
	}

	/**
	 * 
	 * 
	 */
	public void refresh() {
		fireTableDataChanged();
	}

	/**
	 * @param batchModel
	 *            The batchModel to set.
	 */
	// Table is not editable
	public void setValueAt(Object value, int row, int col) {
		if (col == 5) {
			super.setValueAt(value, row, col);
			fireTableCellUpdated(row, col);
			fireTableDataChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		if (data != null) {
			if (row < data.size()) {
				ArrayList al = (ArrayList) data.get(row);
				return al.get(col);
			}
		}
		return null;
	}

	public void setData() {
		data = new ArrayList();
		for (int i = 0; i < sampleData.length; i++) {
			Object[] s = (Object[]) sampleData[i];
			ArrayList a = new ArrayList(Arrays.asList(s));
			data.add(a);
		}
	}

	public ArrayList getData() {
		return data;
	}
}
