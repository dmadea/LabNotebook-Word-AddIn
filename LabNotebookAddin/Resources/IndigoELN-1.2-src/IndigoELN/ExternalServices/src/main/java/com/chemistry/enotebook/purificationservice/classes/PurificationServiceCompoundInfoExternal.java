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
package com.chemistry.enotebook.purificationservice.classes;

import java.io.Serializable;

public class PurificationServiceCompoundInfoExternal implements Serializable {

	private static final long serialVersionUID = 5605465077764328985L;

	// submission paramters per compound
	PurificationServiceSubmisionParametersLight compoundSubmissionParam;

	long batchTrackingID;
	String compoundNumber;
	String batchNumber;
	String notebookBatchNumber;
	String saltCode;

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

	public PurificationServiceSubmisionParametersLight getCompoundSubmissionParam() {
		return compoundSubmissionParam;
	}

	public void setCompoundSubmissionParam(
			PurificationServiceSubmisionParametersLight compoundSubmissionParam) {
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

	public String toXML() {
		return "";
	}

}
