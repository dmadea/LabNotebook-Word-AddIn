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
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionToBiologistTest;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class SampleToMMForBioTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6246114984678058914L;
	private static String SCREEN_NAME = "Screen Name";
	private static String SCREEN_CODE = "Screen Code";
	private static String SITE_AND_RECIPIENT_NAME = "(Site) & Recipient Name";
	private static String MIN_AMOUNT_NEEDED = "Min Amount Needed";
	private static String DELIVERY_CONTAINER_TYPE = "Delivery SampleContainer Type";
	private ArrayList screenNameList = new ArrayList();
	private ArrayList screenCodeList = new ArrayList();
	private ArrayList siteRecipientNametList = new ArrayList();
	private ArrayList minAmountList = new ArrayList();
	private ArrayList deliveryContainerTypeList = new ArrayList();
	private int sampleCount = 0;

	public int getRowCount() {
		return sampleCount;
	}

	public int getColumnCount() {
		return 5;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return SampleToMMForBioTableModel.SCREEN_NAME;
			case 1:
				return SampleToMMForBioTableModel.SCREEN_CODE;
			case 2:
				return SampleToMMForBioTableModel.SITE_AND_RECIPIENT_NAME;
			case 3:
				return SampleToMMForBioTableModel.MIN_AMOUNT_NEEDED;
			case 4:
				return SampleToMMForBioTableModel.DELIVERY_CONTAINER_TYPE;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		// switch (nCol) {
		// case 0: return true;
		// case 1: return true;
		// case 2: return true;
		// }
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		switch (nCol) {
			case 0:
				return this.getScreenNameList().get(nRow);
			case 1:
				return this.getScreenCodeList().get(nRow);
			case 2:
				return this.getSiteRecipientNametList().get(nRow);
			case 3:
				return this.getMinAmountList().get(nRow);
			case 4:
				return this.getDeliveryContainerTypeList().get(nRow);
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				this.getScreenNameList().set(rowIndex, aValue);
				break;
			case 1:
				this.getScreenCodeList().set(rowIndex, aValue);
				break;
			case 2:
				this.getSiteRecipientNametList().set(rowIndex, aValue);
				break;
			case 3:
				this.getMinAmountList().set(rowIndex, aValue);
				break;
			case 4:
				this.getDeliveryContainerTypeList().set(rowIndex, aValue);
				break;
		}
	}

	public void resetModelData(ArrayList mmList) {
		for (int i = 0; i < mmList.size(); i++) {
			BatchSubmissionToBiologistTest batchSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) mmList.get(i);
			if (batchSubmissionToBiologistTest.isTestSubmittedByMm()) {
				this.getScreenNameList().add(batchSubmissionToBiologistTest.getScreenProtocolTitle());
				this.getScreenCodeList().add(batchSubmissionToBiologistTest.getScreenCode());
				this.getSiteRecipientNametList().add(
						"(" + batchSubmissionToBiologistTest.getSiteCode() + ") "
								+ batchSubmissionToBiologistTest.getScientistName());
				this.getMinAmountList().add(
						batchSubmissionToBiologistTest.getMinAmountValue() + " "
								+ batchSubmissionToBiologistTest.getMinAmountUnit());
				this.getDeliveryContainerTypeList().add(batchSubmissionToBiologistTest.getContainerType());
			}
		}
		this.setSampleCount(this.getScreenNameList().size());
	}

	public void addModelData() {
		try {
			this.getScreenNameList().add("");
			this.getScreenCodeList().add("");
			this.getSiteRecipientNametList().add("");
			this.getMinAmountList().add("");
			this.getDeliveryContainerTypeList().add("");
			this.setSampleCount(this.getScreenNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void cutModelData(int rowIndex) {
		try {
			this.getScreenNameList().remove(rowIndex);
			this.getScreenCodeList().remove(rowIndex);
			this.getSiteRecipientNametList().remove(rowIndex);
			this.getMinAmountList().remove(rowIndex);
			this.getDeliveryContainerTypeList().remove(rowIndex);
			this.setSampleCount(this.getScreenNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void clearModelData() {
		try {
			this.getScreenNameList().clear();
			this.getScreenCodeList().clear();
			this.getSiteRecipientNametList().clear();
			this.getMinAmountList().clear();
			this.getDeliveryContainerTypeList().clear();
			this.setSampleCount(this.getScreenNameList().size());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/**
	 * @return Returns the amountList.
	 */
	public ArrayList getSiteRecipientNametList() {
		return siteRecipientNametList;
	}

	/**
	 * @param amountList
	 *            The amountList to set.
	 */
	public void setSiteRecipientNametList(ArrayList amountList) {
		this.siteRecipientNametList = amountList;
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
	public ArrayList getScreenNameList() {
		return screenNameList;
	}

	/**
	 * @param containerList
	 *            The containerList to set.
	 */
	public void setScreenNameList(ArrayList containerList) {
		this.screenNameList = containerList;
	}

	/**
	 * @return Returns the deliveryContainerTypeList.
	 */
	public ArrayList getDeliveryContainerTypeList() {
		return deliveryContainerTypeList;
	}

	/**
	 * @param deliveryContainerTypeList
	 *            The deliveryContainerTypeList to set.
	 */
	public void setDeliveryContainerTypeList(ArrayList deliveryContainerTypeList) {
		this.deliveryContainerTypeList = deliveryContainerTypeList;
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
}