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
package com.chemistry.enotebook.compoundmanagement.classes;

import java.io.Serializable;

public class CompoundManagementOrderDetail implements Serializable {
	
	private static final long serialVersionUID = 1955110435009660793L;

	public CompoundManagementOrderDetail(String plateBarCode, String barCodeNo,
			String compoundNumber, double molWgt, String molFormula,
			String unitCode, double amountDelivered, String batchTrackingId) {
		this.plateBarCode = plateBarCode;
		this.barCodeNo = barCodeNo;
		this.compoundNumber = compoundNumber;
		this.molWgt = molWgt;
		this.molFormula = molFormula;
		this.unitCode = unitCode;
		this.amountDelivered = amountDelivered;
		this.batchTrackingId = batchTrackingId;
	}

	private String plateBarCode;
	private String barCodeNo;
	private String compoundNumber;
	private double molWgt;
	private String molFormula;
	private String unitCode;
	private double amountDelivered;
	private String batchTrackingId;

	public String getBatchTrackingId() {
		return batchTrackingId;
	}

	public void setBatchTrackingId(String batchTrackingId) {
		this.batchTrackingId = batchTrackingId;
	}

	public String getCompoundNumber() {
		return compoundNumber;
	}

	public void setCompoundNumber(String compoundNumber) {
		this.compoundNumber = compoundNumber;
	}

	public double getMolWgt() {
		return molWgt;
	}

	public void setMolWgt(double molWgt) {
		this.molWgt = molWgt;
	}

	public String getMolFormula() {
		return molFormula;
	}

	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public double getAmountDelivered() {
		return amountDelivered;
	}

	public void setAmountDelivered(double amountDelivered) {
		this.amountDelivered = amountDelivered;
	}

	public String getPlateBarCode() {
		return plateBarCode;
	}

	public void setPlateBarCode(String plateBarCode) {
		this.plateBarCode = plateBarCode;
	}

	public String getBarCodeNo() {
		return barCodeNo;
	}

	public void setBarCodeNo(String barCodeNo) {
		this.barCodeNo = barCodeNo;
	}
}
