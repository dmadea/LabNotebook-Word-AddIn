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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewHeaderRenderer;

import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableModel;
//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableView;
//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableViewHeaderRenderer;
//import com.chemistry.enotebook.client.gui.page.experiment.table.TableViewMVConnector;

public class PCeNRegistration_BatchDetailsTableView extends	PCeNTableView {

	/**
	 * 
	 */
	private static final long serialVersionUID = -507878700272636634L;
	private PCeNRegistration_SummaryTableViewCellEditor mPcenRegistrationSummaryTableViewCellEditor;
	private PCeNRegistration_SummaryTableViewCellRender mPcenRegistrationSummaryTableViewCellRender = new PCeNRegistration_SummaryTableViewCellRender();

	/**
	 * Constructor
	 * @param model the table model
	 * @param rowHeight initial height of the rows
	 * @param connector the controller link between the model and the table view
	 */
	public PCeNRegistration_BatchDetailsTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connector) {
		super(model, rowHeight, connector, null);
		mPcenRegistrationSummaryTableViewCellEditor = new PCeNRegistration_SummaryTableViewCellEditor((null));
		setTableViewCellRenderers();

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					checkboxclicked(evt);
				}
			} // end mouseClicked

		});
	}

	/**
	 * Handle the event when a row is selected by clicking on the checkbox.
	 * @param evt
	 */
	private void checkboxclicked(MouseEvent evt) {
		String columnName = getColumnName(this.getSelectedColumn());
		if (columnName.equalsIgnoreCase("Select")) {
			connector.setSelectValue(this.getSelectedRow());
			this.repaint();
		}
	}

	/**
	 * Set non-default table cell renderer to the ParallelCeNTableViewHeaderRenderer.
	 */
	protected void setTableViewCellRenderers() {
		PCeNTableViewHeaderRenderer headerRenderer = new PCeNTableViewHeaderRenderer();
		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++) {
			TableColumn col = getColumn(getColumnName(i));
			col.setCellRenderer(mPcenRegistrationSummaryTableViewCellRender);
			col.setHeaderRenderer(headerRenderer);
			col.setCellEditor(mPcenRegistrationSummaryTableViewCellEditor);
		}// end for
	}
}
