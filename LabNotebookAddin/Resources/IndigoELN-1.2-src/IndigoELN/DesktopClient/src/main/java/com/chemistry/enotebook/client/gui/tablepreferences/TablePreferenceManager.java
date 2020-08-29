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

import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import java.util.List;

public class TablePreferenceManager implements TablePreferenceInterface {
	private List<TableColumnInfo> columns = null;
	
	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		TableColumnInfo result = null;
		if (columns == null)
			return null;
		for (int colIndex = 0; colIndex < columns.size() && result == null; colIndex++) {
			TableColumnInfo ci = (TableColumnInfo) columns.get(colIndex);
			if (ci.getModelIndex() == modelIndex)
				result = ci;
		}
		return result;
	}

	public String getUserPreferences(String root) {
		return null;
	}

	public void setUserPreferences(String prefs) throws UserPreferenceException {
		// TODO Auto-generated method stub

	}

	public void updateUserPreferences(List<TableColumnInfo> columns) {
		this.columns = columns;
	}

	public void columnAdded(TableColumnModelEvent e) {
		// TODO Auto-generated method stub

	}

	public void columnMarginChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

	public void columnMoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub

	}

	public void columnRemoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub

	}

	public void columnSelectionChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	
}
