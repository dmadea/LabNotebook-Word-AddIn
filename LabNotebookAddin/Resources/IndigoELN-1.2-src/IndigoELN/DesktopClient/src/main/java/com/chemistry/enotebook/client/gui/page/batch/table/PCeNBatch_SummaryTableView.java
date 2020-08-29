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
package com.chemistry.enotebook.client.gui.page.batch.table;

import com.chemistry.enotebook.client.gui.page.batch.PCeNBatch_SummaryViewControllerUtility;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * 
 *
 */
public class PCeNBatch_SummaryTableView extends PCeNTableView {

	private static final long serialVersionUID = -8257827546207473464L;

	public PCeNBatch_SummaryTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connector) {
		super(model, rowHeight, connector, null);

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
		if (columnName.equalsIgnoreCase("View")) {
			PCeNBatch_SummaryViewControllerUtility controller = (PCeNBatch_SummaryViewControllerUtility) connector;
			controller.setSelectValue(this.getSelectedRow());
			this.repaint();
		}
	}
}
