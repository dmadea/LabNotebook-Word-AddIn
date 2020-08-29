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
 * Created on Dec 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionContainerInfo;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SamplesInContainerTableModel extends AbstractTableModel {
/**
	 * 
	 */
	private static final long serialVersionUID = 7481196109484750976L;
	//	public static final int MM_SITE_IDX = 0;
	public static final int CONTAINER_BAR_CODE_IDX = 0;
	public static final int AMOUNT_IN_CONTAINER_IDX = 1;
	public static final int MAX_COLS = 2;
//	private static final String MM_SITE = "MM Site";
	private static final String CONTAINER_BAR_CODE = "SampleContainer Barcode";
	private static final String AMOUNT_IN_CONTAINER = "Amount in SampleContainer";
	private ArrayList containerData = new ArrayList();
	protected boolean readOnly = false;

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean flag) {
		readOnly = flag;
	}

	public int getRowCount() {
		return containerData.size();
	}

	public int getColumnCount() {
		return MAX_COLS;
	}

	public String getColumnName(int column) {
		switch (column) {
//			case MM_SITE_IDX:
//				return MM_SITE;
			case CONTAINER_BAR_CODE_IDX:
				return CONTAINER_BAR_CODE;
			case AMOUNT_IN_CONTAINER_IDX:
				return AMOUNT_IN_CONTAINER;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		switch (nCol) {
//			case MM_SITE_IDX:
//				return false;
			case CONTAINER_BAR_CODE_IDX:
				return false;
			case AMOUNT_IN_CONTAINER_IDX:
				return !readOnly;
		}
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		BatchSubmissionContainerInfo ci = ((BatchSubmissionContainerInfo) containerData.get(nRow));
		switch (nCol) {
//			case MM_SITE_IDX:
//				return getSiteDecrFromCode(ci.getSiteCode());
			case CONTAINER_BAR_CODE_IDX:
				return ci.getBarCode();
			case AMOUNT_IN_CONTAINER_IDX:
				Amount val = new Amount();
				val.setUnit(UnitCache.getInstance().getUnit(ci.getAmountUnit()));
				val.setValue(ci.getAmountValue());
				return val;
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		BatchSubmissionContainerInfo ci = ((BatchSubmissionContainerInfo) containerData.get(rowIndex));
		switch (columnIndex) {
//			case MM_SITE_IDX:
//				ci.setSiteCode((String) aValue);
//				break;
			case CONTAINER_BAR_CODE_IDX:
				ci.setBarCode((String) aValue);
				break;
			case AMOUNT_IN_CONTAINER_IDX:
				Amount val = (Amount) aValue;
				ci.setAmountValue(val.doubleValue());
				ci.setAmountUnit(val.getUnit().getCode());
				break;
		}
		fireTableDataChanged();
	}

//	private String getSiteDecrFromCode(String code) {
//		String decrString = "";
//		try {
//			decrString = CodeTableCache.getCache().getSiteDescription(code);
//		} catch (CodeTableCacheException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(null, "Look up description failed.", e);
//		}
//		return decrString;
//	}

	public void resetModelData(ArrayList containerInfoList) {
		readOnly = false;
		for (int i = 0; i < containerInfoList.size(); i++)
			containerData.add((BatchSubmissionContainerInfo) containerInfoList.get(i));
		fireTableDataChanged();
	}

	public void addModelData(BatchSubmissionContainerInfo containerInfo) {
		try {
			containerData.add(containerInfo);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void cutModelData(int rowIndex) {
		try {
			containerData.remove(rowIndex);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void clearModelData() {
		try {
			containerData.clear();
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
}