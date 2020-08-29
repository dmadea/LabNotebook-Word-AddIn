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
package com.chemistry.enotebook.client.gui.health;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class HealthcheckServiceTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -2816316618069236350L;
	
	public static final String SERVICE_NAME_TITLE = "Service & Component Name";
	public static final String AVAILABILITY = "Availability";
	public static final String IMPACT = "Impact";
	public static final String OK_STRING = "OK";
	public static final String FAILURE_STRING = "Failure";
	public static final String MINIMAL_STRING = "Minimal";
	
	private List<ServiceHealthStatus> serviceHealthList = new ArrayList<ServiceHealthStatus>();

	public int getRowCount() {
		return serviceHealthList.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return HealthcheckServiceTableModel.SERVICE_NAME_TITLE;
			case 1:
				return HealthcheckServiceTableModel.AVAILABILITY;
			case 2:
				return HealthcheckServiceTableModel.IMPACT;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		ServiceHealthStatus row = (ServiceHealthStatus) this.serviceHealthList.get(nRow);
		switch (nCol) {
			case 0:
				return row.getServiceDesc();
			case 1:
				return row;
			case 2:
				if (row.getHealthStatus().equals(ServiceHealthStatus.GOOD_STATUS)) {
					return OK_STRING;
				} else if (row.getHealthStatus().equals(ServiceHealthStatus.BAD_STATUS)) {
					if (HealthCheckHandler.isMinimalService(row.getServiceName()))
						return MINIMAL_STRING;
					else
						return FAILURE_STRING;
				} else
					return "Checking";
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	public void resetModelData(List<ServiceHealthStatus> voList) {
		serviceHealthList.clear();
		serviceHealthList = voList;
	}

	public synchronized void addModelData(ServiceHealthStatus servicehealth) {
		serviceHealthList.add(servicehealth);
	}

	public synchronized void updateModelData(ServiceHealthStatus servicehealth) {
		for (int i = 0; i < serviceHealthList.size(); i++) {
			ServiceHealthStatus service = (ServiceHealthStatus) serviceHealthList.get(i);
			if (service.getServiceName().equals(servicehealth.getServiceName())) {
				service.setHealthStatus(servicehealth.getHealthStatus());
				break;
			}
		}
	}
}
