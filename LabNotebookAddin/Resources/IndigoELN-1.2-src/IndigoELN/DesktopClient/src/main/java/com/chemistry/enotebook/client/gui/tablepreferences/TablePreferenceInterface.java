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

import javax.swing.event.TableColumnModelListener;
import java.util.List;

public interface TablePreferenceInterface extends TableColumnModelListener {
	public String getUserPreferences(String root);

	public void setUserPreferences(String prefs) throws UserPreferenceException;

	public void updateUserPreferences(List<TableColumnInfo> columns);

	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex);
}
