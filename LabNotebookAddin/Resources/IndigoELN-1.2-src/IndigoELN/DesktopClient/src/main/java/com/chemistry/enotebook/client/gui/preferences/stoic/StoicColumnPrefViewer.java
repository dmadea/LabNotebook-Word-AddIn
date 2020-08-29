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
/*
 * Created on Oct 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.preferences.stoic;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class StoicColumnPrefViewer extends JTable {
	
	private static final long serialVersionUID = -7121853366928635911L;

	// private StoicColumnPrefTableModel summaryTableModel;
	public StoicColumnPrefViewer() {
		super();
		init();
	}

	/**
	 * 
	 */
	private void init() {
		StoicColumnPrefTableModel tableModel = new StoicColumnPrefTableModel();
		setModel(tableModel);
		DefaultTableCellRenderer strRendr = (DefaultTableCellRenderer) getDefaultRenderer(String.class);
		strRendr.setHorizontalAlignment(SwingConstants.CENTER);
		initColWidths();
	}

	public void initColWidths() {
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel tcm = getColumnModel();
		JTableHeader th = getTableHeader();
		if (th == null)
			return;
		// Need to bold the headers
		th.setFont(new java.awt.Font("sansserif", Font.BOLD, 11));
		for (int i = 0; i < getColumnCount(); i++) {
			TableColumn tCol = tcm.getColumn(i);
			tCol.setMinWidth(70);
			tCol.setPreferredWidth(70);
		}
	}

	public void refresh(StoicColumnPrefTableModel summaryTableModel) {
		setModel(summaryTableModel);
		initColWidths();
	}
}
