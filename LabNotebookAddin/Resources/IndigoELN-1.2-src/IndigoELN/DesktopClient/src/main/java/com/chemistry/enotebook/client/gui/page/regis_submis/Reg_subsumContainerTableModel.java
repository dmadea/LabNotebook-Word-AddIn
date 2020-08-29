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
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.BatchSubmissionContainerInfoModel;
import com.chemistry.enotebook.domain.BatchSubmissionToScreenModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchRegistrationInfo;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Reg_subsumContainerTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1837963617468606489L;
	private ArrayList<ProductBatchModel> batchList = new ArrayList<ProductBatchModel>();
	private boolean isEditable = false;
	public static final String BATCH_ID = "  Nbk Batch #  ";
	public static final String REGISTERY_NUMBER = "Conv. Batch #";
	public static final String AMOUNT_MADE = "Amount Made";
	public static final String AMOUNT_SUBMITTED = "Amount Submitted";
	public static final String AMOUNT_REMAINING = "Amount Remaining";
	public static final String REGISTERED_DATE = "Registered Date";
	public static final String SELECT_TO_REGISTER = "Select to Register";
	// public static final String SELECT_TO_SUBMIT = "Select to Submit";
	public static final String STATUS = "Status";
	public static final int BATCH_ID_IDX = 0;
	public static final int REGISTERY_NUMBER_IDX = 1;
	public static final int AMOUNT_MADE_IDX = 2;
	public static final int AMOUNT_SUBMITTED_IDX = 3;
	public static final int AMOUNT_REMAINING_IDX = 4;
	public static final int REGISTERED_DATE_IDX = 5;
	public static final int SELECT_TO_REGISTER_IDX = 6;
	// public static final int SELECT_TO_SUBMIT_IDX = 6;
	public static final int STATUS_IDX = 7;
	private static final int COLUMN_COUNT = 8;

	public int getRowCount() {
		return batchList.size();
	}

	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	public String getColumnName(int column) {
		switch (column) {
			case BATCH_ID_IDX:
				return BATCH_ID;
			case REGISTERY_NUMBER_IDX:
				return REGISTERY_NUMBER;
			case AMOUNT_MADE_IDX:
				return AMOUNT_MADE;
			case AMOUNT_SUBMITTED_IDX:
				return AMOUNT_SUBMITTED;
			case AMOUNT_REMAINING_IDX:
				return AMOUNT_REMAINING;
			case REGISTERED_DATE_IDX:
				return REGISTERED_DATE;
			case SELECT_TO_REGISTER_IDX:
				return SELECT_TO_REGISTER;
				// case SELECT_TO_SUBMIT_IDX:
				// return SELECT_TO_SUBMIT;
			case STATUS_IDX:
				return STATUS;
		}
		return "";
	}

	public Class getColumnClass(int column) {
		Class dataType = super.getColumnClass(column);
		if (column == SELECT_TO_REGISTER_IDX) // || column ==
			// SELECT_TO_SUBMIT_IDX)
			dataType = java.lang.Boolean.class;
		return dataType;
	}

	public boolean isCellEditable(int nRow, int nCol) {
		ProductBatchModel row = this.batchList.get(nRow);
		switch (nCol) {
			case BATCH_ID_IDX:
				return false;
			case REGISTERY_NUMBER_IDX:
				return false;
			case AMOUNT_MADE_IDX:
				return false;
			case AMOUNT_SUBMITTED_IDX:
				return false;
			case AMOUNT_REMAINING_IDX:
				return false;
			case REGISTERED_DATE_IDX:
				return false;
			case SELECT_TO_REGISTER_IDX:
				// if (isEditable && row.getRegInfo().getRegistrationStatus() !=
				// null && !row.getRegInfo().getRegistrationStatus().equals(""))
				// {
				// if
				// (row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)
				// ||
				// row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING))
				// result = false;
				return (isEditable && row.isEditable());
				// }
				// return isEditable;
				// case SELECT_TO_SUBMIT_IDX:
				// if (isEditable && row.getRegInfo().getSubmissionStatus()!=
				// null && !row.getRegInfo().getSubmissionStatus().equals("")) {
				// if
				// (row.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTED))
				// return false;
				// }
				// return isEditable;
			case STATUS_IDX:
				return false;
		}
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		ProductBatchModel row = this.batchList.get(nRow);
		NumberFormat formatter = NumberFormat.getInstance();
		AmountModel amount = row.getWeightAmount();
		formatter.setMaximumFractionDigits(amount.getFixedFigs());
		switch (nCol) {
			case BATCH_ID_IDX:
				return row.getBatchNumber();
			case REGISTERY_NUMBER_IDX:
				return row.getConversationalBatchNumber();
			case AMOUNT_MADE_IDX:
				return row.getWeightAmount().GetValueForDisplay() + " " + row.getWeightAmount().getUnit();
			case AMOUNT_SUBMITTED_IDX:
				// if (row.getRegInfo().getSubmissionStatus() != null &&
				// !row.getRegInfo().getSubmissionStatus().equals("")) {
				// if
				// (row.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTED))
				// {
				AmountModel sampleTotal = getSubmittedTotal(row, null);// BatchRegistrationInfo.SUBMITTED);
				sampleTotal.setUnit(amount.getUnit());
				return sampleTotal.GetValueForDisplay() + " " + sampleTotal.getUnit();
				// }
				// }
				// return "";
			case AMOUNT_REMAINING_IDX:
				// if (row.getRegInfo().getStatus().equals("PASSED")) {
				AmountModel amountSubmitted = getSubmittedTotal(row, null);
				amountSubmitted.setUnit(amount.getUnit());
				double val = new Double(amount.GetValueForDisplay()).doubleValue();
				val = Math.round((val - amountSubmitted.doubleValue()) * 100000.0) / 100000.0;
				return formatter.format(val) + " " + amount.getUnit();
				// } else
				// return formatter.format(amount.getValue()) + " " +
				// amount.getUnit();
			case REGISTERED_DATE_IDX:
				if (row.getRegInfo().getRegistrationDate() != null) {
					SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
					return df.format(row.getRegInfo().getRegistrationDate());
				} else
					return "";
			case SELECT_TO_REGISTER_IDX:
				if (row.getRegInfo().getStatus() != null && !row.getRegInfo().getStatus().equals("")) {
					if (row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)
							|| row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PASSED)
							|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERING)
							|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)
							|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.POST_REGISTERING))
						return Boolean.TRUE;
					else
						return Boolean.FALSE;
				} else {
					if (row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERING)
							|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)
							|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.POST_REGISTERING)) {
						return Boolean.TRUE;
					} else
						return Boolean.FALSE;
				}
				// case SELECT_TO_SUBMIT_IDX:
				// ////System.out.println("the sub status is: " +
				// row.getRegInfo().getSubmissionStatus());
				// //if (this.calculateSubmittedTotal(row).getValue() ==
				// 0)return Boolean.FALSE;
				// if (row.getRegInfo().getSubmissionStatus()!= null &&
				// !row.getRegInfo().getSubmissionStatus().equals("")) {
				// if
				// (row.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTING)
				// ||
				// row.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.POST_SUBMITTING)
				// )
				// return Boolean.TRUE;
				// else
				// if(row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PASSED)
				// &&
				// row.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTED)
				// ){
				// return Boolean.TRUE;
				// }else
				// return Boolean.FALSE;
				// } else
				// return Boolean.FALSE;
			case STATUS_IDX:
				if (row.getRegInfo().getStatus().equals(BatchRegistrationInfo.FAILED))
					return row.getRegInfo().getStatus() + ". Click to view details.";
				return row.getRegInfo().getStatus();
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		ProductBatchModel row = this.batchList.get(rowIndex);
		switch (columnIndex) {
			case SELECT_TO_REGISTER_IDX:
				if (row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)
						|| row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PASSED))
					break;
				if (row.getRegInfo().getRegistrationStatus() != null && !row.getRegInfo().getRegistrationStatus().equals("")) {
					if (row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.REGISTERED)
							|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.POST_REGISTERING))
						break;
				}
				if (aValue instanceof Boolean) {
					if (((Boolean) aValue).booleanValue())
						row.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.REGISTERING);
					else {
						row.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
						if (row.getRegInfo().getSubmissionStatus().equals(BatchRegistrationInfo.SUBMITTING)) {
							// setValueAt(new Boolean(false), rowIndex,
							// SELECT_TO_SUBMIT_IDX);
							// fireTableCellUpdated(rowIndex, SELECT_TO_SUBMIT_IDX);
							if (row.getRegInfo().getRegistrationStatus() != null
									&& !row.getRegInfo().getRegistrationStatus().equals("")) {
								if (row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)
										|| row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.POST_SUBMITTING))
									break;
								if (row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.NOT_REGISTERED)) {
									row.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
									break;
								}
							}
							row.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
							// //System.out.println("The submission status is: " +
							// row.getRegInfo().getSubmissionStatus());
						}
					}
				}
				break;
			// case SELECT_TO_SUBMIT_IDX:
			// if (row.getRegInfo().getRegistrationStatus() != null &&
			// !row.getRegInfo().getRegistrationStatus().equals("") ) {
			// if
			// (row.getRegInfo().getStatus().equals(BatchRegistrationInfo.PROCESSING)
			// ||
			// row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.POST_SUBMITTING))
			// break;
			//
			// if
			// (row.getRegInfo().getRegistrationStatus().equals(BatchRegistrationInfo.NOT_REGISTERED)
			// ) {
			// row.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
			// break;
			// }
			// }
			// if (aValue instanceof Boolean) {
			// if (((Boolean)aValue).booleanValue())
			// row.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.SUBMITTING);
			// else
			// row.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
			// }
			// ////System.out.println("The submission status is: " +
			// row.getRegInfo().getSubmissionStatus());
			// break;
		}
	}

	public void resetRegStatus(int rowIndex) {
		ProductBatchModel row = this.batchList.get(rowIndex);
		row.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
	}

	public void resetSubStatus(int rowIndex) {
		ProductBatchModel row = this.batchList.get(rowIndex);
		row.getRegInfo().setSubmissionStatus(BatchRegistrationInfo.NOT_SUBMITTED);
	}

	public void resetModelData() {
	}

	public AmountModel calculateSubmittedScreenTotal(ProductBatchModel row, String status) {
		double total = 0.0;
		UnitCache uc = UnitCache.getInstance();
		List<BatchSubmissionToScreenModel> list = row.getRegInfo().getNewSubmitToBiologistTestList();
		for (int i = 0; i < list.size(); i++) {
			BatchSubmissionToScreenModel batchSubmissionToBiologistTest = list.get(i);
			if (!batchSubmissionToBiologistTest.isTestSubmittedByMm()
					&& (status == null || (status != null && batchSubmissionToBiologistTest.getSubmissionStatus().equals(status)))) {
				Amount amount = new Amount(batchSubmissionToBiologistTest.getAmountValue(), uc
						.getUnit(batchSubmissionToBiologistTest.getAmountUnit()));
				total += amount.GetValueInStdUnitsAsDouble();
			}
		}
		return new AmountModel(UnitType.MASS, total);
	}

	public AmountModel calculateSubmittedContainerTotal(ProductBatchModel row, String status) {
		double total = 0.0;
		UnitCache uc = UnitCache.getInstance();
		ArrayList<BatchSubmissionContainerInfoModel> list = row.getRegInfo().getSubmitContainerList();
		for (int j = 0; j < list.size(); j++) {
			BatchSubmissionContainerInfoModel containerInfo = list.get(j);
			if (status == null || (status != null && containerInfo.getSubmissionStatus().equals(status))) {
				Amount amount = new Amount(containerInfo.getAmountValue(), uc.getUnit(containerInfo.getAmountUnit()));
				total += amount.GetValueInStdUnitsAsDouble();
			}
		}
		return new AmountModel(UnitType.MASS, total);
	}

	private AmountModel getSubmittedTotal(ProductBatchModel row, String status) {
		AmountModel containerAmountSum = calculateSubmittedContainerTotal(row, status);
		AmountModel screenAmountSum = calculateSubmittedScreenTotal(row, status);
		return new AmountModel(UnitType.MASS, containerAmountSum.doubleValue() + screenAmountSum.doubleValue());
	}

	public int getBatchesToRegisterCount() {
		int result = 0;
		for (Iterator<ProductBatchModel> i = batchList.iterator(); i.hasNext();) {
			ProductBatchModel pb = i.next();
			if (pb.getRegInfo() != null) {
				String status = pb.getRegInfo().getStatus();
				String regStatus = pb.getRegInfo().getRegistrationStatus();
				if (regStatus != null && !regStatus.equals(BatchRegistrationInfo.NOT_REGISTERED)
						&& (status == null || status.length() == 0 || status.equals(BatchRegistrationInfo.FAILED)))
					result++;
			}
		}
		return result;
	}

	/**
	 * @return Returns the batchList.
	 */
	public ArrayList<ProductBatchModel> getBatchList() {
		return batchList;
	}

	/**
	 * @param batchList
	 *            The batchList to set.
	 */
	public void setBatchList(ArrayList<ProductBatchModel> batchList) {
		this.batchList = batchList;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean flag) {
		isEditable = flag;
	}
}