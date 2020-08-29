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
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.registration.ScreenSearchVO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class LookUpScreensTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6653306968022050611L;
	public static final int ID_IDX = 0;
	public static final int SCREEN_CODE_IDX = 1;
	public static final int SCREEN_PROTOCOL_TITLE_IDX = 2;
	public static final int SCIENTIST_NAME_IDX = 3;
	public static final int SITE_IDX = 4;
	public static final int MIN_AMOUNT_REQUIRED_IDX = 5;
	public static final int SENT_BY_MM_IDX = 6;
	public static final int SENT_BY_MYSELF_IDX = 7;
	private static final int MAX_COLS = 8;
	private static final String ID = "ID";
	private static final String SCREEN_CODE = "Screen Code";
	private static final String SCREEN_PROTOCOL_TITLE = "Screen Protocol Title";
	private static final String SCIENTIST_NAME = "Scientist Name";
	private static final String SITE = "Site";
	private static final String MIN_AMOUNT_REQUIRED = "Min. Amount Required";
	private static final String SENT_BY_MM = "Sent By MM";
	private static final String SENT_BY_MYSELF = "Sent By Myself";
	private ArrayList idList = new ArrayList();
	private ArrayList screenCodeList = new ArrayList();
	private ArrayList siteList = new ArrayList();
	private ArrayList minAmountList = new ArrayList();
	private ArrayList screenProtocolList = new ArrayList();
	private ArrayList scientistNameList = new ArrayList();
	private ArrayList sentByMMList = new ArrayList();
	private ArrayList sentByMyselfList = new ArrayList();
	private ArrayList scientistCodeList = new ArrayList();
	private int sampleCount = 0;

	public int getRowCount() {
		return sampleCount;
	}

	public int getColumnCount() {
		return MAX_COLS;
	}

	public String getColumnName(int column) {
		switch (column) {
			case ID_IDX:
				return ID;
			case SCREEN_CODE_IDX:
				return SCREEN_CODE;
			case SCREEN_PROTOCOL_TITLE_IDX:
				return SCREEN_PROTOCOL_TITLE;
			case SCIENTIST_NAME_IDX:
				return SCIENTIST_NAME;
			case SITE_IDX:
				return SITE;
			case MIN_AMOUNT_REQUIRED_IDX:
				return MIN_AMOUNT_REQUIRED;
			case SENT_BY_MM_IDX:
				return SENT_BY_MM;
			case SENT_BY_MYSELF_IDX:
				return SENT_BY_MYSELF;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		switch (nCol) {
			case ID_IDX:
				return false;
			case SCREEN_CODE_IDX:
				return false;
			case SCREEN_PROTOCOL_TITLE_IDX:
				return false;
			case SCIENTIST_NAME_IDX:
				return false;
			case SITE_IDX:
				return false;
			case MIN_AMOUNT_REQUIRED_IDX:
				return false;
			case SENT_BY_MM_IDX:
				return true;
			case SENT_BY_MYSELF_IDX:
				return true;
		}
		return false;
	}

	public Class getColumnClass(int column) {
		Class dataType = super.getColumnClass(column);
		if (column == SENT_BY_MM_IDX || column == SENT_BY_MYSELF_IDX)
			dataType = java.lang.Boolean.class;
		return dataType;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		switch (nCol) {
			case ID_IDX:
				return getIdList().get(nRow);
			case SCREEN_CODE_IDX:
				return getScreenCodeList().get(nRow);
			case SCREEN_PROTOCOL_TITLE_IDX:
				return getScreenProtocolList().get(nRow);
			case SCIENTIST_NAME_IDX:
				return getScientistNameList().get(nRow);
			case SITE_IDX:
				return getSiteList().get(nRow);
			case MIN_AMOUNT_REQUIRED_IDX:
				return getMinAmountList().get(nRow);
			case SENT_BY_MM_IDX:
				if (getSentByMMList().get(nRow).equals("TRUE"))
					return Boolean.TRUE;
				else
					return Boolean.FALSE;
			case SENT_BY_MYSELF_IDX:
				if (getSentByMyselfList().get(nRow).equals("TRUE"))
					return Boolean.TRUE;
				else
					return Boolean.FALSE;
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case ID_IDX:
				getIdList().set(rowIndex, aValue);
				break;
			case SCREEN_CODE_IDX:
				getScreenCodeList().set(rowIndex, aValue);
				break;
			case SCREEN_PROTOCOL_TITLE_IDX:
				getScreenProtocolList().set(rowIndex, aValue);
				break;
			case SCIENTIST_NAME_IDX:
				getScientistNameList().set(rowIndex, aValue);
				break;
			case SITE_IDX:
				getSiteList().set(rowIndex, aValue);
				break;
			case MIN_AMOUNT_REQUIRED_IDX:
				getMinAmountList().set(rowIndex, aValue);
				break;
			case SENT_BY_MM_IDX:
				if (getSentByMMList().get(rowIndex).equals("TRUE"))
					getSentByMMList().set(rowIndex, "FALSE");
				else
					getSentByMMList().set(rowIndex, "TRUE");
				break;
			case SENT_BY_MYSELF_IDX:
				if (getSentByMyselfList().get(rowIndex).equals("TRUE"))
					getSentByMyselfList().set(rowIndex, "FALSE");
				else
					getSentByMyselfList().set(rowIndex, "TRUE");
				break;
		}
	}

	public void resetModelData(List<ScreenSearchVO> screensList) {
		for (int i = 0; i < screensList.size(); i++) {
			ScreenSearchVO screenInfo = (ScreenSearchVO) screensList.get(i);
			getIdList().add(screenInfo.getScreenProtocolID());
			getScreenCodeList().add(screenInfo.getScreenCode());
			getScreenProtocolList().add(screenInfo.getScreenProtocolTitle());
			getScientistNameList().add(screenInfo.getRecipientName());
			getSiteList().add(screenInfo.getRecipientSiteCode());
			getMinAmountList().add(screenInfo.getMinAmountValue() + " " + screenInfo.getMinAmountUnit());
			getSentByMMList().add("FALSE");
			getSentByMyselfList().add("FALSE");
			getScientistCodeList().add(screenInfo.getScientistCode());
		}
		setSampleCount(getIdList().size());
	}

	public boolean isSentByMM(int rownum) {
		if (((String) sentByMMList.get(rownum)).equals("TRUE"))
			return true;
		if (((String) sentByMyselfList.get(rownum)).equals("TRUE"))
			return false;
		return true;
	}

	public void clearModelData() {
		try {
			getIdList().clear();
			getScreenCodeList().clear();
			getScreenProtocolList().clear();
			getScientistNameList().clear();
			getSiteList().clear();
			getMinAmountList().clear();
			getSentByMMList().clear();
			getSentByMyselfList().clear();
			setSampleCount(getIdList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * @return Returns the amountList.
	 */
	public ArrayList getSiteList() {
		return siteList;
	}

	/**
	 * @param amountList
	 *            The amountList to set.
	 */
	public void setSiteList(ArrayList amountList) {
		this.siteList = amountList;
	}

	/**
	 * @return Returns the barcodeList.
	 */
	public ArrayList getScreenCodeList() {
		return screenCodeList;
	}

	/**
	 * @param barcodeList
	 *            The barcodeList to set.
	 */
	public void setScreenCodeList(ArrayList barcodeList) {
		this.screenCodeList = barcodeList;
	}

	/**
	 * @return Returns the containerCount.
	 */
	public int getSampleCount() {
		return sampleCount;
	}

	/**
	 * @param containerCount
	 *            The containerCount to set.
	 */
	public void setSampleCount(int containerCount) {
		this.sampleCount = containerCount;
	}

	/**
	 * @return Returns the containerList.
	 */
	public ArrayList getIdList() {
		return idList;
	}

	/**
	 * @param containerList
	 *            The containerList to set.
	 */
	public void setIdList(ArrayList containerList) {
		this.idList = containerList;
	}

	/**
	 * @return Returns the deliveryContainerTypeList.
	 */
	public ArrayList getSentByMMList() {
		return sentByMMList;
	}

	/**
	 * @param deliveryContainerTypeList
	 *            The deliveryContainerTypeList to set.
	 */
	public void setSentByMMList(ArrayList deliveryContainerTypeList) {
		this.sentByMMList = deliveryContainerTypeList;
	}

	/**
	 * @return Returns the minAmountList.
	 */
	public ArrayList getMinAmountList() {
		return minAmountList;
	}

	/**
	 * @param minAmountList
	 *            The minAmountList to set.
	 */
	public void setMinAmountList(ArrayList minAmountList) {
		this.minAmountList = minAmountList;
	}

	// /**
	// * @return Returns the iD.
	// */
	// public static String getID() {
	// return ID;
	// }
	// /**
	// * @param id The iD to set.
	// */
	// public static void setID(String id) {
	// ID = id;
	// }
	/**
	 * @return Returns the scientistNameList.
	 */
	public ArrayList getScientistNameList() {
		return scientistNameList;
	}

	/**
	 * @param scientistNameList
	 *            The scientistNameList to set.
	 */
	public void setScientistNameList(ArrayList scientistNameList) {
		this.scientistNameList = scientistNameList;
	}

	/**
	 * @return Returns the screenProtocolList.
	 */
	public ArrayList getScreenProtocolList() {
		return screenProtocolList;
	}

	/**
	 * @param screenProtocolList
	 *            The screenProtocolList to set.
	 */
	public void setScreenProtocolList(ArrayList screenProtocolList) {
		this.screenProtocolList = screenProtocolList;
	}

	/**
	 * @return Returns the sentByMyselfList.
	 */
	public ArrayList getSentByMyselfList() {
		return sentByMyselfList;
	}

	/**
	 * @param sentByMyselfList
	 *            The sentByMyselfList to set.
	 */
	public void setSentByMyselfList(ArrayList sentByMyselfList) {
		this.sentByMyselfList = sentByMyselfList;
	}

	/**
	 * @return Returns the scientistCodeList.
	 */
	public ArrayList getScientistCodeList() {
		return scientistCodeList;
	}

	/**
	 * @param scientistCodeList
	 *            The scientistCodeList to set.
	 */
	public void setScientistCodeList(ArrayList scientistCodeList) {
		this.scientistCodeList = scientistCodeList;
	}
}
