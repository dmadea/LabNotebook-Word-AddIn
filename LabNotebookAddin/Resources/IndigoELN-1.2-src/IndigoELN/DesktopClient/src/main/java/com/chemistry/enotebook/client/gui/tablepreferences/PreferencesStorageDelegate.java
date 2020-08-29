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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PreferencesStorageDelegate {
	public static final int FIRST_COLUMN = 0;
	public static final int SECOND_COLUMN = 1;
	public static final int THIRD_COLUMN = 2;
	public static final int FORTH_COLUMN = 3;
	String x = "<ColumnInfo>";
	private Map userPreference = null;
	private boolean userTablePreferenceModified = false;
	private ColumnFurniture myColumn = null;

	public PreferencesStorageDelegate() {
		userPreference = new HashMap();
		myColumn = new ColumnFurniture();
		myColumn.setModelIndex(FIRST_COLUMN);
		myColumn.setColumnIndex(SECOND_COLUMN);
		myColumn.setVisible(true);
		myColumn.setWidth(50);
		userPreference.put(new Integer(FIRST_COLUMN), myColumn);
		myColumn = new ColumnFurniture();
		myColumn.setModelIndex(SECOND_COLUMN);
		myColumn.setColumnIndex(FIRST_COLUMN);
		myColumn.setVisible(true);
		myColumn.setWidth(70);
		userPreference.put(new Integer(SECOND_COLUMN), myColumn);
		myColumn = new ColumnFurniture();
		myColumn.setModelIndex(THIRD_COLUMN);
		myColumn.setColumnIndex(FORTH_COLUMN);
		myColumn.setVisible(true);
		myColumn.setWidth(20);
		userPreference.put(new Integer(THIRD_COLUMN), myColumn);
		myColumn = new ColumnFurniture();
		myColumn.setModelIndex(FORTH_COLUMN);
		myColumn.setColumnIndex(THIRD_COLUMN);
		myColumn.setVisible(false);
		myColumn.setWidth(150);
		userPreference.put(new Integer(FORTH_COLUMN), myColumn);
	}

	public Map getUserPreferences() {
		return userPreference;
	}

	public void setUserPreferences(Map userPreference) {
		this.userPreference = userPreference;
	}

	public ColumnFurniture getColumnInfoFromModelIndex(int index) {
		ColumnFurniture result = null;
		for (Iterator it = userPreference.values().iterator(); it.hasNext() && result == null;) {
			ColumnFurniture cf = (ColumnFurniture) it.next();
			if (cf.getModelIndex() == index)
				result = cf;
		}
		return result;
	}

	public void setUserTablePreferences(boolean modified) {
		userTablePreferenceModified = modified;
	}

	public boolean isUserTablePreferencesModified() {
		return userTablePreferenceModified;
	}
}// end class
