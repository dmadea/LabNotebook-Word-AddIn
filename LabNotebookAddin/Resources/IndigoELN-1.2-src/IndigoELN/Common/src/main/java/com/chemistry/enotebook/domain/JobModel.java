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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.session.security.HttpUserMessage;

import java.sql.Timestamp;

public class JobModel extends CeNAbstractModel {
	
	private static final long serialVersionUID = 4105509258539258832L;
	
	private String jobID = null;
	private String plateKey = null;
	private String pageKey = null;
	private String compoundRegistrationStatus = null;
	private String compoundRegistrationStatusMessage = null;
	private String compoundManagementStatus = null;
	private String compoundManagementStatusMessage = null;
	private String purificationServiceStatus = null;
	private String purificationServiceStatusMessage = null;
	private String compoundAggregationStatus = null;
	private String compoundAggregationStatusMessage = null;
	
	private Timestamp modificationDate =null;
	private String callbackUrl = null;
	
	private HttpUserMessage userMessage = null;
	
	private String compoundRegistrationJobId = null;
	private String batchKeysString = null;
	private String plateKeysString = null;
	private String workflowsString = null;
	private String jobStatus = null;
	
	public JobModel(){
		userMessage = new HttpUserMessage();
	}
	
	/**
	 * @param jobId
	 * @param plateKey
	 * @param callbackUrl
	 */
	public JobModel(String jobId,String plateKey,String pageKey,String callbackUrl){
		this();
		this.jobID = jobId;
		this.plateKey = plateKey;
		this.pageKey = pageKey;
		this.callbackUrl = callbackUrl;
	}
		
	public String getCompoundRegistrationJobId() {
		return compoundRegistrationJobId;
	}
	
	public void setCompoundRegistrationJobId(String compoundRegistrationJobId) {
		this.compoundRegistrationJobId = compoundRegistrationJobId;
	}
	
	public String getBatchKeysString() {
		return batchKeysString;
	}
	
	public void setBatchKeysString(String batchKeysString) {
		this.batchKeysString = batchKeysString;
	}
	
	public String getPlateKeysString() {
		return plateKeysString;
	}
	
	public void setPlateKeysString(String plateKeysString) {
		this.plateKeysString = plateKeysString;
	}
	
	public String getWorkflowsString() {
		return workflowsString;
	}
	
	public void setWorkflowsString(String workflowsString) {
		this.workflowsString = workflowsString;
	}
	
	public String getJobStatus() {
		return jobStatus;
	}
	
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	/**
	 * @return the jobID
	 */
	public String getJobID() {
		return jobID;
	}

	/**
	 * @param jobID the jobID to set
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * @return the plateKey
	 */
	public String getPlateKey() {
		return plateKey;
	}

	/**
	 * @param plateKey the plateKey to set
	 */
	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}


	/**
	 * @return the modificationDate
	 */
	public Timestamp getModificationDate() {
		return modificationDate;
	}

	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String toXML(){
		return "";
	}

	/**
	 * @return the compoundRegistrationStatus
	 */
	public String getCompoundRegistrationStatus() {
		if(compoundRegistrationStatus == null)
			return "";
		return compoundRegistrationStatus;
	}

	/**
	 * @param compoundRegistrationStatus the compoundRegistrationStatus to set
	 */
	public void setCompoundRegistrationStatus(String compoundRegistrationStatus) {
		this.compoundRegistrationStatus = compoundRegistrationStatus;
	}

	/**
	 * @return the compoundRegistrationStatusMessage
	 */
	public String getCompoundRegistrationStatusMessage() {
		if(compoundRegistrationStatusMessage == null)
			return "";
		return compoundRegistrationStatusMessage;
	}

	/**
	 * @param compoundRegistrationStatusMessage the compoundRegistrationStatusMessage to set
	 */
	public void setCompoundRegistrationStatusMessage(String compoundRegistrationStatusMessage) {
		this.compoundRegistrationStatusMessage = compoundRegistrationStatusMessage;
	}

	/**
	 * @return the compoundManagementStatus
	 */
	public String getCompoundManagementStatus() {
		if(compoundManagementStatus == null)
			return "";
		return compoundManagementStatus;
	}

	/**
	 * @param compoundManagementStatus the compoundManagementStatus to set
	 */
	public void setCompoundManagementStatus(String compoundManagementStatus) {
		this.compoundManagementStatus = compoundManagementStatus;
	}

	/**
	 * @return the compoundManagementStatusMessage
	 */
	public String getCompoundManagementStatusMessage() {
		if(compoundManagementStatusMessage == null)
			return "";
		return compoundManagementStatusMessage;
	}

	/**
	 * @param compoundManagementStatusMessage the compoundManagementStatusMessage to set
	 */
	public void setCompoundManagementStatusMessage(String compoundManagementStatusMessage) {
		this.compoundManagementStatusMessage = compoundManagementStatusMessage;
	}

	/**
	 * @return the purificationServiceStatus
	 */
	public String getPurificationServiceStatus() {
		if(purificationServiceStatus == null)
			return "";
		return purificationServiceStatus;
	}

	/**
	 * @param purificationServiceStatus the purificationServiceStatus to set
	 */
	public void setPurificationServiceStatus(String purificationServiceStatus) {
		this.purificationServiceStatus = purificationServiceStatus;
	}

	/**
	 * @return the purificationServiceStatusMessage
	 */
	public String getPurificationServiceStatusMessage() {
		if(purificationServiceStatusMessage == null)
			return "";
		return purificationServiceStatusMessage;
	}

	/**
	 * @param purificationServiceStatusMessage the purificationServiceStatusMessage to set
	 */
	public void setPurificationServiceStatusMessage(String purificationServiceStatusMessage) {
		this.purificationServiceStatusMessage = purificationServiceStatusMessage;
	}

	/**
	 * @return the compoundAggregationStatus
	 */
	public String getCompoundAggregationStatus() {
		if(compoundAggregationStatus == null)
			return "";
		return compoundAggregationStatus;
	}

	/**
	 * @param compoundAggregationStatus the compoundAggregationStatus to set
	 */
	public void setCompoundAggregationStatus(String compoundAggregationStatus) {
		this.compoundAggregationStatus = compoundAggregationStatus;
	}

	/**
	 * @return the compoundAggregationStatusMessage
	 */
	public String getCompoundAggregationStatusMessage() {
		if(compoundAggregationStatusMessage == null)
			return "";
		return compoundAggregationStatusMessage;
	}

	/**
	 * @param compoundAggregationStatusMessage the compoundAggregationStatusMessage to set
	 */
	public void setCompoundAggregationStatusMessage(String compoundAggregationStatusMessage) {
		this.compoundAggregationStatusMessage = compoundAggregationStatusMessage;
	}

	/**
	 * @return the callbackUrl
	 */
	public String getCallbackUrl() {
		if(callbackUrl == null)
			return "";
		return callbackUrl;
	}

	/**
	 * @param callbackUrl the callbackUrl to set
	 */
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	/**
	 * @return the userMessage
	 */
	public HttpUserMessage getUserMessage() {
		return userMessage;
	}

	/**
	 * @return Full copy of userMessage
	 */
	public HttpUserMessage getClonedUserMessage() {
		return userMessage.clone();
	}
	
	/**
	 * @param userMessage the userMessage to set
	 */
	public void setUserMessage(HttpUserMessage userMessage) {
		this.userMessage = userMessage;
	}

	/**
	 * @return the pageKey
	 */
	public String getPageKey() {
		return pageKey;
	}

	/**
	 * @param pageKey the pageKey to set
	 */
	public void setPageKey(String pageKey) {
		this.pageKey = pageKey;
	}

}
