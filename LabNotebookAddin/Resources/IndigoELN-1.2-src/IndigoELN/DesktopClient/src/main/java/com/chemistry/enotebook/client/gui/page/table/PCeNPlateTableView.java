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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;

import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PCeNPlateTableView extends PCeNTableView implements ListSelectionListener {  // vb 11/29

	private static final long serialVersionUID = -1471379506051425509L;
	
	private BatchSelectionListener[] batchSelectionListeners = null;
	private int seletedRow = -1;

	public PCeNPlateTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connector) {
		super(model, rowHeight, connector, null);
		getSelectionModel().addListSelectionListener(this); // vbchange
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					valueChanged();
				}
			} // end mouseClicked

		});
	}

	public void setProductBatchDetailsContainerListeners(BatchSelectionListener[] batchSelectionListeners) {
		this.batchSelectionListeners = batchSelectionListeners;

	}

	public void valueChanged() {
		if (batchSelectionListeners != null) {
			int a = this.getSelectedRow();

			if (a != seletedRow) {
				seletedRow = a;

				if (getModel() instanceof PCeNTableModel) {

					PCeNTableModel model = (PCeNTableModel) getModel();
					PCeNTableModelConnector connector = model.getConnector();
					if (connector instanceof PCeNPlateTableRowSelectionChangeHandler) {
						PCeNPlateTableRowSelectionChangeHandler utility = (PCeNPlateTableRowSelectionChangeHandler) connector;
						BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, utility.getPlateWell(a));
						for (int i=0; i<batchSelectionListeners.length; i++) {
							batchSelectionListeners[i].batchSelectionChanged(batchSelectionEvent);
						}

					}
				}
			}
		}
	}
}
