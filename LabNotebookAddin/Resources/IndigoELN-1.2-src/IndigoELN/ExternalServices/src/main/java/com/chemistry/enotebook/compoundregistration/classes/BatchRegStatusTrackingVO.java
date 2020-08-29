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
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.compoundregistration.classes;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BatchRegStatusTrackingVO implements Serializable {
	static final long serialVersionUID = 4581201020985718489L;

	private String jobID = new String();
	private String jobStatus = new String();
	private String compoundStatus = new String();
	private String batchNumber = new String();

	private String globalPfID = new String();
	private String detailString = new String();

	private String notebookRef = new String();
	private String compoundParent = new String();
	private String compoundNumber = new String();

	private String batchTrackingID = new String();
	private Date regDate = new Date();

	/**
	 * @return Returns the batchNumber.
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * @param batchNumber
	 *            The batchNumber to set.
	 */
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * @return Returns the batchTrackingID.
	 */
	public String getBatchTrackingID() {
		return batchTrackingID;
	}

	/**
	 * @param batchTrackingID
	 *            The batchTrackingID to set.
	 */
	public void setBatchTrackingID(String batchTrackingID) {
		this.batchTrackingID = batchTrackingID;
	}

	/**
	 * @return Returns the compoundNumber.
	 */
	public String getCompoundNumber() {
		return compoundNumber;
	}

	/**
	 * @param compoundNumber
	 *            The compoundNumber to set.
	 */
	public void setCompoundNumber(String compoundNumber) {
		this.compoundNumber = compoundNumber;
	}

	/**
	 * @return Returns the compoundParent.
	 */
	public String getCompoundParent() {
		return compoundParent;
	}

	/**
	 * @param compoundParent
	 *            The compoundParent to set.
	 */
	public void setCompoundParent(String compoundParent) {
		this.compoundParent = compoundParent;
	}

	/**
	 * @return Returns the compoundStatus.
	 */
	public String getCompoundStatus() {
		return compoundStatus;
	}

	/**
	 * @param compoundStatus
	 *            The compoundStatus to set.
	 */
	public void setCompoundStatus(String compoundStatus) {
		this.compoundStatus = compoundStatus;
	}

	/**
	 * @return Returns the detailString.
	 */
	public String getDetailString() {
		return detailString;
	}

	/**
	 * @param detailString
	 *            The detailString to set.
	 */
	public void setDetailString(String detailString) {
		this.detailString = detailString;
	}

	/**
	 * @return Returns the globalPfID.
	 */
	public String getGlobalPfID() {
		return globalPfID;
	}

	/**
	 * @param globalPfID
	 *            The globalPfID to set.
	 */
	public void setGlobalPfID(String globalPfID) {
		this.globalPfID = globalPfID;
	}

	/**
	 * @return Returns the jobID.
	 */
	public String getJobID() {
		return jobID;
	}

	/**
	 * @param jobID
	 *            The jobID to set.
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * @return Returns the jobStatus.
	 */
	public String getJobStatus() {
		return jobStatus;
	}

	/**
	 * @param jobStatus
	 *            The jobStatus to set.
	 */
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	/**
	 * @return Returns the notebookRef.
	 */
	public String getNotebookRef() {
		return notebookRef;
	}

	/**
	 * @param notebookRef
	 *            The notebookRef to set.
	 */
	public void setNotebookRef(String notebookRef) {
		this.notebookRef = notebookRef;
	}

	/**
	 * @return Returns the regDate.
	 */
	public Date getRegDate() {
		return regDate;
	}

	/**
	 * @param regDate
	 *            The regDate to set.
	 */
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
}
