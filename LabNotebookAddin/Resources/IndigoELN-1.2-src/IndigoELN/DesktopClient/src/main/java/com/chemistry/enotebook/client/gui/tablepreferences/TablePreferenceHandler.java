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
package com.chemistry.enotebook.client.gui.tablepreferences;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;

import javax.swing.*;

public class TablePreferenceHandler {
	private JTable table;
	TablePreferenceManager preferencesDelegate;
	

//	public void applyTablePreferences(JTable myTable, TablePreferenceInterface preferences) throws UserPreferenceException {
//		this.table = myTable;
//		this.preferencesDelegate = (TablePreferenceManager) preferences;
//		TableColumnModel tcm = table.getColumnModel();
//		Vector v = new Vector();
//		v.setSize(tcm.getColumnCount());
//		for (int i = 0; i < tcm.getColumnCount(); i++) {
//			TableColumn col = tcm.getColumn(i);
//			TableColumnInfo tci = preferencesDelegate.getColumnInfoFromModelIndex(col.getModelIndex());
//			if (tci != null) {
//				ColumnFurniture cf = new ColumnFurniture(col, tci.isVisible(), tci.getColumnIndex(), tci.getPreferredWidth());
//				updateColumn(cf, table);
//				v.setElementAt(cf, tci.getColumnIndex());
//			} else {
//				/** TODO: Should this throw an exception? */
//				// CeNErrorHandler.getInstance().logErrorMsg(null,
//				// sysEx.getMessage(), "Table Preference Missing",
//				// JOptionPane.ERROR_MESSAGE) ;
//				throw new UserPreferenceException(
//						"Empty Table Column Info: Probable Cause is - Failure to populate columns array with Table Column Info Objects - \nOrigin method 'ParsePreferences of TablePreferenceDelegate");
//			}
//		}
//		for (int i = tcm.getColumnCount() - 1; i >= 0; i--) {
//			TableColumn col = tcm.getColumn(i);
//			tcm.removeColumn(col);
//		}
//		for (Iterator it = v.iterator(); it.hasNext();)
//			tcm.addColumn((ColumnFurniture) it.next());
//		table.getTableHeader().setResizingAllowed(true);
//	}
//
	/**
	 * Because the structure column can be changed in two places, we need to check to make sure we are not 
	 * setting an invisible structure column to invisible.  This will set the saved width to zero and it will
	 * never appear again.
	 * @param table
	 * @param modelIndex
	 * @param visible
	 */
	public static void changeColumnVisibility(JTable table, int modelIndex, boolean visible) { // update
		int columnIndex = table.convertColumnIndexToView(modelIndex);
		ColumnFurniture cf = ((ColumnFurniture) table.getColumnModel().getColumn(columnIndex));
		if (cf.isVisible() && visible) {
			return;
		} else if (!cf.isVisible() && !visible) {
			return;
		}
		cf.setVisible(visible);
		columnIndex = cf.getColumnIndex();
		if (visible) {
			cf.setMaxWidth(cf.getSavedMaxWidth());
			cf.setMinWidth(cf.getSavedMinWidth());
			cf.setPreferredWidth(cf.getSavedPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setPreferredWidth(cf.getSavedPreferredWidth());
		} else {
			cf.setSavedMinWidth(cf.getMinWidth());
			cf.setSavedMaxWidth(cf.getMaxWidth());
			cf.setSavedPreferredWidth(cf.getPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setPreferredWidth(cf.getSavedPreferredWidth());
			cf.setMinWidth(0);
			cf.setMaxWidth(0);
			cf.setPreferredWidth(0);
		}
	}

	public static void initColumnVisibility(PCeNTableView table) {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			ColumnFurniture column = (ColumnFurniture) table.getColumnModel().getColumn(i);
			if (! column.isVisible()) {
				//changeColumnVisibility(table, i, false);
				updateColumn(column, table);
			}
		}
	}
	
	/**
	 * Used to initialize the settings of the preference or default invisible columns.
	 * @param cf
	 * @param table
	 */
	public static void updateColumn(ColumnFurniture cf, JTable table) {
		int columnIndex = cf.getColumnIndex();
		boolean visible = cf.isVisible();
		if (visible) {
			cf.setMaxWidth(cf.getSavedMaxWidth());
			cf.setMinWidth(cf.getSavedMinWidth());
			cf.setPreferredWidth(cf.getSavedPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setPreferredWidth(cf.getSavedPreferredWidth());
		} else {
			cf.setSavedMinWidth(cf.getMinWidth());
			cf.setSavedMaxWidth(cf.getMaxWidth());
			cf.setSavedPreferredWidth(cf.getPreferredWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(cf.getSavedMaxWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(cf.getSavedMinWidth());
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setPreferredWidth(cf.getSavedPreferredWidth());
			cf.setMinWidth(0);
			cf.setMaxWidth(0);
			cf.setPreferredWidth(0);
		}
	}

	public void tableChanged(ColumnFurniture tableColumn, String xPath) {
	}
}
