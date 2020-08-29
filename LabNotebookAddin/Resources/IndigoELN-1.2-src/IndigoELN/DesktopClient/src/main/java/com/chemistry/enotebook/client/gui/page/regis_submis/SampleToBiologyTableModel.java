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
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionToBiologistTest;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SampleToBiologyTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9015680089391458907L;
	public static final int SCREEN_NAME_IDX = 0;
	public static final int SCREEN_CODE_IDX = 1;
	public static final int SITE_AND_RECIPIENT_NAME_IDX = 2;
	public static final int MIN_AMOUNT_NEEDED_IDX = 3;
	public static final int AMOUNT_SENT_IDX = 4;
	public static final int DELIVERY_CONTAINER_TYPE_IDX = 5;
	private static final int MAX_COLS = 6;
	private static final String SCREEN_NAME = "Screen Name";
	private static final String SCREEN_CODE = "Screen Code";
	private static final String SITE_AND_RECIPIENT_NAME = "(Site) & Recipient Name";
	private static final String MIN_AMOUNT_NEEDED = "Min Amount Needed";
	private static final String AMOUNT_SENT = "Amount Sent";
	private static final String DELIVERY_CONTAINER_TYPE = "Delivery SampleContainer Type";
	protected ArrayList screenData = new ArrayList();
	protected boolean readOnly = false;

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean flag) {
		readOnly = flag;
	}

	public int getRowCount() {
		return screenData.size();
	}

	public int getColumnCount() {
		return MAX_COLS;
	}

	public String getColumnName(int column) {
		switch (column) {
			case SCREEN_NAME_IDX:
				return SCREEN_NAME;
			case SCREEN_CODE_IDX:
				return SCREEN_CODE;
			case SITE_AND_RECIPIENT_NAME_IDX:
				return SITE_AND_RECIPIENT_NAME;
			case MIN_AMOUNT_NEEDED_IDX:
				return MIN_AMOUNT_NEEDED;
			case AMOUNT_SENT_IDX:
				return AMOUNT_SENT;
			case DELIVERY_CONTAINER_TYPE_IDX:
				return DELIVERY_CONTAINER_TYPE;
		}
		return "";
	}

	public Class getColumnClass(int column) {
		Class dataType = super.getColumnClass(column);
		if (column == AMOUNT_SENT_IDX)
			dataType = Amount.class;
		return dataType;
	}

	public boolean isCellEditable(int nRow, int nCol) {
		switch (nCol) {
			case SCREEN_NAME_IDX:
				return false;
			case SCREEN_CODE_IDX:
				return false;
			case SITE_AND_RECIPIENT_NAME_IDX:
				return false;
			case MIN_AMOUNT_NEEDED_IDX:
				return false;
			case AMOUNT_SENT_IDX:
				return !readOnly;
			case DELIVERY_CONTAINER_TYPE_IDX:
				return false;
		}
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		BatchSubmissionToBiologistTest si = ((BatchSubmissionToBiologistTest) screenData.get(nRow));
		switch (nCol) {
			case SCREEN_NAME_IDX:
				return si.getScreenProtocolTitle();
			case SCREEN_CODE_IDX:
				return si.getScreenCode();
			case SITE_AND_RECIPIENT_NAME_IDX:
				return "(" + si.getSiteCode() + ") " + si.getScientistName();
			case MIN_AMOUNT_NEEDED_IDX:
				return si.getMinAmountValue() + " " + si.getMinAmountUnit();
			case AMOUNT_SENT_IDX:
				Amount val = new Amount();
				val.setUnit(UnitCache.getInstance().getUnit(si.getAmountUnit()));
				val.setValue(si.getAmountValue());
				return val;
			case DELIVERY_CONTAINER_TYPE_IDX:
				return si.getContainerType();
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		BatchSubmissionToBiologistTest si = ((BatchSubmissionToBiologistTest) screenData.get(rowIndex));
		switch (columnIndex) {
			case SCREEN_NAME_IDX:
				si.setScreenProtocolTitle((String) aValue);
				break;
			case SCREEN_CODE_IDX:
				si.setScreenCode((String) aValue);
				break;
			case SITE_AND_RECIPIENT_NAME_IDX:
				// read only, todo if want to do updates
				break;
			case MIN_AMOUNT_NEEDED_IDX:
				// read only, todo if want to do updates
				break;
			case AMOUNT_SENT_IDX:
				Amount val = (Amount) aValue;
				si.setAmountValue(val.doubleValue());
				si.setAmountUnit(val.getUnit().getCode());
				break;
			case DELIVERY_CONTAINER_TYPE_IDX:
				si.setContainerType((String) aValue);
				break;
		}
		fireTableDataChanged();
	}

	public void resetModelData(List mmList) {
		readOnly = false;
		for (int i = 0; i < mmList.size(); i++) {
			BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) mmList.get(i);
			if (batchSubmissionToBiologistTest.isTestSubmittedByMm())
				screenData.add(batchSubmissionToBiologistTest);
		}
		fireTableDataChanged();
	}

	public void addModelData() {
		try {
			BatchSubmissionToBiologistTest sd = new BatchSubmissionToBiologistTest();
			sd.setSubmittedByMm("TRUE");
			screenData.add(sd);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void cutModelData(int rowIndex) {
		try {
			screenData.remove(rowIndex);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void clearModelData() {
		try {
			screenData.clear();
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
}