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
package com.chemistry.enotebook.domain.purificationservice;

import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.purificationservice.classes.PurificationServiceCompoundInfoExternal;

public class PurificationServiceCompoundInfo extends CeNAbstractModel{
	
	public static final long serialVersionUID = 7526472295622776147L;
	//submission paramters per compound
	PurificationServiceSubmisionParameters compoundSubmissionParam;	
	
	long batchTrackingID; //16554648
    String compoundNumber; //PF-01386891-00
    String batchNumber; //PF-01386891-00-0007
    String notebookBatchNumber; //00310002-0027-000013
    String saltCode; // 01
	
    
	public String getBatchNumber() {
		return batchNumber;
	}


	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}


	public long getBatchTrackingID() {
		return batchTrackingID;
	}


	public void setBatchTrackingID(long batchTrackingID) {
		this.batchTrackingID = batchTrackingID;
	}


	public String getCompoundNumber() {
		return compoundNumber;
	}


	public void setCompoundNumber(String compoundNumber) {
		this.compoundNumber = compoundNumber;
	}


	public PurificationServiceSubmisionParameters getCompoundSubmissionParam() {
		return compoundSubmissionParam;
	}


	public void setCompoundSubmissionParam(PurificationServiceSubmisionParameters compoundSubmissionParam) {
		this.compoundSubmissionParam = compoundSubmissionParam;
	}


	public String getNotebookBatchNumber() {
		return notebookBatchNumber;
	}


	public void setNotebookBatchNumber(String notebookBatchNumber) {
		this.notebookBatchNumber = notebookBatchNumber;
	}


	public String getSaltCode() {
		return saltCode;
	}


	public void setSaltCode(String saltCode) {
		this.saltCode = saltCode;
	}


	public String toXML()
	{
		return "";
	}
	
	public PurificationServiceCompoundInfoExternal convertToPurificationServiceCompoundInfoExternal() {
		PurificationServiceCompoundInfoExternal purificationServiceCompoundInfoExternal = new PurificationServiceCompoundInfoExternal();
		
		purificationServiceCompoundInfoExternal.setBatchTrackingID(this.getBatchTrackingID());
		purificationServiceCompoundInfoExternal.setCompoundNumber(this.getCompoundNumber());
		purificationServiceCompoundInfoExternal.setBatchNumber(this.getBatchNumber());
		purificationServiceCompoundInfoExternal.setNotebookBatchNumber(this.getNotebookBatchNumber());
		purificationServiceCompoundInfoExternal.setSaltCode(this.getSaltCode());
		
		return purificationServiceCompoundInfoExternal;
	}
	
	public static PurificationServiceCompoundInfoExternal[] convertToArrayPurificationServiceCompoundInfoExternal(PurificationServiceCompoundInfo[] purificationServiceCompoundInfos) {
		PurificationServiceCompoundInfoExternal[] purificationServiceCompoundInfoExternals = new PurificationServiceCompoundInfoExternal[purificationServiceCompoundInfos.length];
		
		for(int i = 0; i < purificationServiceCompoundInfos.length; i++) {
			purificationServiceCompoundInfoExternals[i] = purificationServiceCompoundInfos[i].convertToPurificationServiceCompoundInfoExternal();
		}

		return purificationServiceCompoundInfoExternals;
	}
}
