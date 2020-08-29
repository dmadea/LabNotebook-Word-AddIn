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

import com.chemistry.enotebook.session.DBHealthStatusVO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class HealthCheckDBTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 7444076223416453541L;
	
	public static final String DB_NAME_TITLE = "Database Name";
	public static final String AVAILABILITY = "Availability";
	public static final String IMPACT = "Impact";
	public static final String OK_STRING = "OK";
	public static final String FAILURE_STRING = "Failure";
	public static final String MINIMAL_STRING = "Minimal";
	
	private List<DBHealthStatusVO> dbHealthVOList = new ArrayList<DBHealthStatusVO>();

	public int getRowCount() {
		if (dbHealthVOList == null)
			return 0;
		else
			return dbHealthVOList.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return HealthCheckDBTableModel.DB_NAME_TITLE;
			case 1:
				return HealthCheckDBTableModel.AVAILABILITY;
			case 2:
				return HealthCheckDBTableModel.IMPACT;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		DBHealthStatusVO row = (DBHealthStatusVO) this.dbHealthVOList.get(nRow);
		switch (nCol) {
			case 0:
				return row.getDbDesc();
			case 1:
				return row;
			case 2:
				if (row.getHealthStatus().equals(ServiceHealthStatus.GOOD_STATUS)) {
					return OK_STRING;
				} else if (row.getHealthStatus().equals(ServiceHealthStatus.BAD_STATUS)) {
					return FAILURE_STRING;
				} else if (row.getHealthStatus().equals(ServiceHealthStatus.MINIMAL_STATUS)) {
					return MINIMAL_STRING;
				} else
					return "";
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	public void resetModelData(List<DBHealthStatusVO> voList) {
		dbHealthVOList.clear();
		dbHealthVOList = voList;
	}
}
