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
package com.chemistry.enotebook.domain.reg;

import com.chemistry.enotebook.domain.CeNAbstractModel;

public class RegistrationCompoundStatus extends CeNAbstractModel{

	private static final long serialVersionUID = 5969945990432354931L;
	
	private String batchNumber;
	private String batchTrackingNumber;
	private String detailElement;
	private String dryRun;
	private String globalNumber;
	private String jobid;
	private String offset;
	private String plateId;
	private String status;
	private String structureData;
	private String transactionState;
	private String wellId;
	
	public RegistrationCompoundStatus()
	{
		
	}
	
	public String getBatchNumber() {
		return batchNumber;
	}


	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}


	public String getBatchTrackingNumber() {
		return batchTrackingNumber;
	}


	public void setBatchTrackingNumber(String batchTrackingNumber) {
		this.batchTrackingNumber = batchTrackingNumber;
	}


	public String getDetailElement() {
		return detailElement;
	}


	public void setDetailElement(String detailElement) {
		this.detailElement = detailElement;
	}


	public String getDryRun() {
		return dryRun;
	}


	public void setDryRun(String dryRun) {
		this.dryRun = dryRun;
	}


	public String getGlobalNumber() {
		return globalNumber;
	}


	public void setGlobalNumber(String globalNumber) {
		this.globalNumber = globalNumber;
	}


	public String getJobid() {
		return jobid;
	}


	public void setJobid(String jobid) {
		this.jobid = jobid;
	}


	public String getOffset() {
		return offset;
	}


	public void setOffset(String offset) {
		this.offset = offset;
	}


	public String getPlateId() {
		return plateId;
	}


	public void setPlateId(String plateId) {
		this.plateId = plateId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getStructureData() {
		return structureData;
	}


	public void setStructureData(String structureData) {
		this.structureData = structureData;
	}


	public String getTransactionState() {
		return transactionState;
	}


	public void setTransactionState(String transactionState) {
		this.transactionState = transactionState;
	}


	public String getWellId() {
		return wellId;
	}


	public void setWellId(String wellId) {
		this.wellId = wellId;
	}


	public String toXML()
	{
		return "";
	}
}
