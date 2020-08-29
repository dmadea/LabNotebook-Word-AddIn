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

import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import javax.swing.*;

/**
 * Remove this class
 * 
 *
 */
public class TablePreferenceController {
	private ManageTableColumns columnManager = null;
	private TablePreferenceDelegate prefsDelegate = null;
	private JTable myTable = null;
	private int whichTable = -1;
	private NotebookPage page = null;

	public TablePreferenceController(JTable tableReference, NotebookPage page, int whichTable) {
		myTable = tableReference;
		this.whichTable = whichTable;
		this.page = page;
	}

	public void setPreferences() throws UserPreferenceException, Exception {
//		/** TODO add a condition for batch table info */
//		if (whichTable == TablePreferenceDelegate.TABLE_PROPS_REACTANTS
//				|| whichTable == TablePreferenceDelegate.TABLE_PROPS_INTENDED_PRODUCTS) {
//			try {
//				prefsDelegate = new TablePreferenceDelegate(whichTable, page, myTable);
//				columnManager = new ManageTableColumns(myTable, prefsDelegate, page);
//				columnManager.applyTablePreferences();
//				myTable.getColumnModel().addColumnModelListener(prefsDelegate);
//			} catch (UserPreferenceException usExp) {
//				throw new UserPreferenceException(usExp.getMessage(), usExp);
//			} catch (Exception ex) {
//				throw new Exception(ex.getMessage(), ex.getCause());
//			}
//		}
	}
}