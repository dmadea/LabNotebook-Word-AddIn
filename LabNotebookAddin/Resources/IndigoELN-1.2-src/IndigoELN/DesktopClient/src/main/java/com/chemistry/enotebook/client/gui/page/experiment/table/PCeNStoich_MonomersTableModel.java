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

public class PCeNStoich_MonomersTableModel extends PCeNTableModel {
	
	private static final long serialVersionUID = 228180950007470216L;
	
	private PCeNStoich_MonomersTableColumnModel pcentm = PCeNStoich_MonomersTableColumnModel.getInstance();
	
	public PCeNStoich_MonomersTableModel() {
	}

	public PCeNStoich_MonomersTableModel(PCeNTableModelConnector mvconnector) {
		if (mvconnector == null || mvconnector.getHeaderNames() == null) {
			throw new IllegalArgumentException("ParallelCeNTableModel received NULL arguments");
		}
		this.connector = mvconnector;
	}
	
	//addtional impl/overriding
	public Class<?> getColumnClass(int columnIndex) {
		return pcentm.getColumnClass(columnIndex);
	}

	//override the method in PcENTableModel and JTableModel to call the refresh() after setting the value
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		connector.setValue(aValue, rowIndex, columnIndex);
	    //Call refresh the table which ever the connector is handling
		this.refresh();
	}
}
