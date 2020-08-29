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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PCeNMonomer_TableView extends PCeNTableView {
	
	private static final long serialVersionUID = 7307774191199976313L;
	private PCeNTableModel model;

	public PCeNMonomer_TableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connector)
	{
		super(model, rowHeight, connector, null);
		this.model = model;
		this.connector = connector;
		//mPcenRegistrationSummaryTableViewCellEditor = new PCeNRegistration_SummaryTableViewCellEditor(this);
		//setTableViewCellRenderers();

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					checkboxclicked(evt);
				}
			} // end mouseClicked

		});
	}
	
	private void checkboxclicked(MouseEvent evt) {
		String columnName = getColumnName(this.getSelectedColumn());
		if (columnName.equalsIgnoreCase(LIMITING)) {
			connector.setSelectValue(this.getSelectedRow());
			this.repaint();
		}
		else
			super.mouseClicked(evt);
	}
}
