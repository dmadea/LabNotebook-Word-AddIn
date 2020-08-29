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
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.domain.BatchSubmissionContainerInfoModel;
import com.chemistry.enotebook.domain.BatchSubmissionToScreenModel;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchRegistrationInfo extends ObservableObject implements DeepClone, DeepCopy {
	// private BatchRegistrationCompoundColor compoundColor = new BatchRegistrationCompoundColor();

	private static final long serialVersionUID = 6589548532231611804L;
	
	private String compoundSource;
	private String compoundSourceDetail;
	private Date registrationDate; // Should initially be null
	private String registrationStatus;
	private String submissionStatus;
	private String status;
	private String jobId;
	private String batchTrackingId;
	private String errorMsg;
	private String hitId;
	private ArrayList<BatchResidualSolventModel> residualSolventList = new ArrayList<BatchResidualSolventModel>();
	private ArrayList<BatchSolubilitySolventModel> solubilitySolventList = new ArrayList<BatchSolubilitySolventModel>();

	private ArrayList<BatchSubmissionToScreenModel> submitToBiologistTestList = new ArrayList<BatchSubmissionToScreenModel>();
	private ArrayList<BatchSubmissionContainerInfoModel> submitContainerList = new ArrayList<BatchSubmissionContainerInfoModel>();
	private ArrayList<BatchRegistrationListener> regListeners = new ArrayList<BatchRegistrationListener>(); // Objects interested in registration changes

	private BatchVnVInfo batchVnVInfo = new BatchVnVInfo();

	// The following codes must be of GenericCode type
	private ArrayList<String> compoundRegistrationHazardCodes = new ArrayList<String>(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_HAZARD_CDT table
	private ArrayList<String> compoundRegistrationHandlingCodes = new ArrayList<String>(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_HANDLING_CDT table
	private ArrayList<String> compoundRegistrationStorageCodes = new ArrayList<String>(); // List of two digit codes that correspond to a COMPOUND_REGISTRATION_STORAGE_CDT table

	private String intermediate_or_test;

	public static final String NOT_SUBMITTED = "Not Submitted";
	public static final String SUBMITTED = "Submitted";
	public static final String SUBMITTING = "Submitting";

	public static final String POST_REGISTERING = "Post Registering";
	public static final String POST_SUBMITTING = "Post Submitting";

	public static final String NOT_REGISTERED = "Not Registered";
	public static final String REGISTERED = "Registered";
	public static final String REGISTERING = "Registering";

	public static final String PASSED = "PASSED";
	public static final String FAILED = "FAILED";
	public static final String PROCESSING = "PROCESSING";

	public BatchRegistrationInfo() {
		init();
		batchVnVInfo.addObserver(this);
	}

	private void init() {
		compoundSource = "";
		compoundSourceDetail = "";
		registrationDate = null; // Should initially be null
		registrationStatus = BatchRegistrationInfo.NOT_REGISTERED;
		submissionStatus = BatchRegistrationInfo.NOT_SUBMITTED;
		status = "";
		jobId = "";
		batchTrackingId = "";
		errorMsg = "";
		hitId = "";
		intermediate_or_test = "";

		if (residualSolventList != null)
			residualSolventList.clear();
		if (solubilitySolventList != null)
			solubilitySolventList.clear();
		if (submitToBiologistTestList != null)
			submitToBiologistTestList.clear();
		if (submitContainerList != null)
			submitContainerList.clear();
		if (compoundRegistrationHazardCodes != null)
			compoundRegistrationHazardCodes.clear();
		if (compoundRegistrationHandlingCodes != null)
			compoundRegistrationHandlingCodes.clear();
		if (compoundRegistrationStorageCodes != null)
			compoundRegistrationStorageCodes.clear();
	}

	public void dispose() throws Throwable {
		finalize();
	}

	protected void finalize() throws Throwable {
		registrationDate = null;
		if (residualSolventList != null)
			residualSolventList.clear();
		residualSolventList = null;
		if (solubilitySolventList != null)
			solubilitySolventList.clear();
		solubilitySolventList = null;
		if (submitToBiologistTestList != null)
			submitToBiologistTestList.clear();
		submitToBiologistTestList = null;
		if (submitContainerList != null)
			submitContainerList.clear();
		submitContainerList = null;
		if (compoundRegistrationHazardCodes != null)
			compoundRegistrationHazardCodes.clear();
		compoundRegistrationHazardCodes = null;
		if (compoundRegistrationHandlingCodes != null)
			compoundRegistrationHandlingCodes.clear();
		compoundRegistrationHandlingCodes = null;
		if (compoundRegistrationStorageCodes != null)
			compoundRegistrationStorageCodes.clear();
		compoundRegistrationStorageCodes = null;
		if (regListeners != null)
			regListeners.clear();
		regListeners = null;
		super.finalize();
	}

	/**
	 * Allows one to get back to default settings for BatchInfo
	 * 
	 */
	public void reset() {
		init();
	}

	/**
	 * @return Returns the compoundSource.
	 */
	public String getCompoundSource() {
		return compoundSource;
	}

	/**
	 * @param compoundSource
	 *            The compoundSource to set.
	 */
	public void setCompoundSource(String compoundSource) {
		if (this.compoundSource == null || !this.compoundSource.equals(compoundSource)){
			this.compoundSource = compoundSource;
			setModified(true);
		}
	}

	/**
	 * @return Returns the sourceDetail.
	 */
	public String getCompoundSourceDetail() {
		return compoundSourceDetail;
	}

	/**
	 * @param sourceDetail
	 *            The sourceDetail to set.
	 */
	public void setCompoundSourceDetail(String sourceDetail) {
		if (compoundSourceDetail == null || !compoundSourceDetail.equals(sourceDetail)){
			compoundSourceDetail = sourceDetail;
			setModified(true);
		}
	}

	/**
	 * @return Returns the hitID.
	 */
	public String getHitId() {
		return hitId;
	}

	/**
	 * @param hitId
	 *            The hitID to set.
	 */
	public void setHitId(String hitId) {
		if (!this.hitId.equals(hitId)) {
			this.hitId = hitId;
			setModified(true);
		}
	}

	/**
	 * @return Returns the intermediate_or_test.
	 */
	public String getIntermediateOrTest() { return intermediate_or_test; }
	
	/**
	 * @param intermediate The intermediate to set.
	 */
	public void setIntermediateOrTest(String intermediate_or_test) {
		if (!this.intermediate_or_test.equals(intermediate_or_test)){
			this.intermediate_or_test = intermediate_or_test;
			setModified(true);
		}
	}

	/**
	 * @return Returns the registrationDate.
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate
	 *            The registrationDate to set.
	 */
	public void setRegistrationDate(Date registrationDate) {
		if (this.registrationDate == null || !this.registrationDate.equals(registrationDate)) {
			this.registrationDate = registrationDate;
			setModified(true);
		}
	}

	/**
	 * @return Returns the regStatus.
	 */
	public String getRegistrationStatus() {
		return (registrationStatus == null || registrationStatus.length() == 0) ? NOT_REGISTERED : registrationStatus;
	}

	/**
	 * @param regStatus
	 *            The regStatus to set.
	 */
	public void setRegistrationStatus(String regStatus) {
		if (!registrationStatus.equals(regStatus)) {
			registrationStatus = regStatus;
			fireRegStatusChangedEvent();
			setModified(true);
		}
	}

	/**
	 * Adds object that implements BatchRegistrationListener interface to the list of Objects to be notified when registrationStatus
	 * changes.
	 * 
	 * @param o -
	 *            object implementing BatchRegistrationListener interface
	 */
	public void addRegistrationListener(BatchRegistrationListener o) {
		if (!regListeners.contains(o))
			regListeners.add(o);
	}

	/**
	 * Removes object if it exists in the list of listeners.
	 * 
	 * @param o
	 */
	public void deleteRegistrationListener(BatchRegistrationListener o) {
		regListeners.remove(o);
	}

	/**
	 * @return Returns the residualSolventList.
	 */
	public ArrayList<BatchResidualSolventModel> getResidualSolventList() {
		return residualSolventList;
	}

	/**
	 * @param container
	 *            The MM Container to add to the list.
	 */
	public void addResidualSolvent(BatchResidualSolventModel residualSolvent) {
		residualSolventList.add(residualSolvent);
		// Moving to newer non-observer model - fingers crossed
//		addObserver(residualSolvent);
		setModified(true);
	}

	/**
	 * @param residualSolventList
	 *            The residualSolventList to set.
	 */
	public void setResidualSolventList(ArrayList<BatchResidualSolventModel> residualSolventList) {
		removeAllObservablesFromList(this.residualSolventList);
		// Moving to newer non-observer model - fingers crossed
//		addAllObservablesToList(residualSolventList, this.residualSolventList);
		setModified(true);
	}

	/**
	 * @return Returns the solubilitySolventList.
	 */
	public ArrayList<BatchSolubilitySolventModel> getSolubilitySolventList() {
		return solubilitySolventList;
	}

	/**
	 * @param container
	 *            The MM Container to add to the list.
	 */
	public void addSolubilitySolvent(BatchSolubilitySolventModel solubilitySolvent) {
		solubilitySolventList.add(solubilitySolvent);
		// Moving to newer non-observer model - fingers crossed
//		addObserver(solubilitySolvent);
		setModified(true);
	}

	/**
	 * @param solubilitySolventList
	 *            The solubilitySolventList to set.
	 */
	public void setSolubilitySolventList(ArrayList<BatchSolubilitySolventModel> solubilitySolventList) {
//		removeAllObservablesFromList(this.solubilitySolventList);
//		addAllObservablesToList(solubilitySolventList, this.solubilitySolventList);
		setModified(true);
	}

	/**
	 * 
	 * @return ArrayList of GenericCode types
	 */
	public List<String> getCompoundRegistrationHandlingCodes() {
		return compoundRegistrationHandlingCodes;
	}

	/**
	 * 
	 * Use CodeTableCache to get appropriate codes and descriptions
	 * 
	 * @param ArrayList
	 *            of GenericCode types
	 */
	public void setCompoundRegistrationHandlingCodes(List<String> handlingCodes) {
//		removeAllObservablesFromList(compoundRegistrationHandlingCodes);
//		addAllObservablesToList(handlingCodes, compoundRegistrationHandlingCodes);
		setModified(true);
	}

	/**
	 * 
	 * @return ArrayList of GenericCode types
	 */
	public List<String> getCompoundRegistrationHazardCodes() {
		return compoundRegistrationHazardCodes;
	}

	/**
	 * 
	 * Use CodeTableCache to get appropriate codes and descriptions
	 * 
	 * @param ArrayList
	 *            of GenericCode types
	 */
	public void setCompoundRegistrationHazardCodes(List<String> hazardCodes) {
//		removeAllObservablesFromList(compoundRegistrationHazardCodes);
//		addAllObservablesToList(hazardCodes, compoundRegistrationHazardCodes);
		setModified(true);
	}

	/**
	 * 
	 * @return ArrayList of GenericCode types
	 */
	public List<String> getCompoundRegistrationStorageCodes() {
		return compoundRegistrationStorageCodes;
	}

	/**
	 * 
	 * Use CodeTableCache to get appropriate codes and descriptions
	 * 
	 * @param ArrayList
	 *            of GenericCode types
	 */
	public void setCompoundRegistrationStorageCodes(List<String> storageCodes) {
//		removeAllObservablesFromList(compoundRegistrationStorageCodes);
//		addAllObservablesToList(storageCodes, compoundRegistrationStorageCodes);
		setModified(true);
	}

	// /**
	// * @return Returns the compoundColor.
	// */
	// public BatchRegistrationCompoundColor getCompoundColor() {
	// return compoundColor;
	// }
	// /**
	// * @param compoundColor The compoundColor to set.
	// */
	// public void setCompoundColor(BatchRegistrationCompoundColor compoundColor) {
	// this.compoundColor = compoundColor;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public void deepCopy(Object source) {
		if (source != null) {
			BatchRegistrationInfo sourceInstance = (BatchRegistrationInfo) source;
			// compoundColor = sourceInstance.compoundColor;
			compoundSource = sourceInstance.compoundSource;
			compoundSourceDetail = sourceInstance.compoundSourceDetail;
			hitId = sourceInstance.hitId;
			if (sourceInstance.registrationDate != null)
				registrationDate = (Date) sourceInstance.registrationDate.clone();
			else
				registrationDate = null;
			registrationStatus = sourceInstance.registrationStatus;
			submissionStatus = sourceInstance.submissionStatus;
			status = sourceInstance.status;
			jobId = sourceInstance.jobId;
			batchTrackingId = sourceInstance.batchTrackingId;
			errorMsg = sourceInstance.errorMsg;
			removeAllObservablesFromList(solubilitySolventList);
//			addAllObservablesToList(sourceInstance.solubilitySolventList, solubilitySolventList);
			removeAllObservablesFromList(residualSolventList);
//			addAllObservablesToList(sourceInstance.residualSolventList, residualSolventList);
			removeAllObservablesFromList(submitContainerList);
//			addAllObservablesToList(sourceInstance.submitContainerList, submitContainerList);
			removeAllObservablesFromList(submitToBiologistTestList);
//			addAllObservablesToList(sourceInstance.submitToBiologistTestList, submitToBiologistTestList);

			removeAllObservablesFromList(compoundRegistrationHazardCodes);
//			addAllObservablesToList(sourceInstance.compoundRegistrationHazardCodes, compoundRegistrationHazardCodes);
			removeAllObservablesFromList(compoundRegistrationHandlingCodes);
//			addAllObservablesToList(sourceInstance.compoundRegistrationHandlingCodes, compoundRegistrationHandlingCodes);
			removeAllObservablesFromList(compoundRegistrationStorageCodes);
//			addAllObservablesToList(sourceInstance.compoundRegistrationStorageCodes, compoundRegistrationStorageCodes);
			batchVnVInfo.deepCopy(sourceInstance.batchVnVInfo);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchRegistrationInfo target = new BatchRegistrationInfo();
		target.deepCopy(this);
		return target;
	}

	/**
	 * @return Returns the submitContainerList.
	 */
	public ArrayList<BatchSubmissionContainerInfoModel> getSubmitContainerList() {
		return submitContainerList;
	}

	/**
	 * @param container
	 *            The MM Container to add to the list.
	 */
	public void addSubmitContainer(BatchSubmissionContainerInfoModel container) {
		submitContainerList.add(container);
//		addObserver(container);
		setModified(true);
	}

	/**
	 * @param submitContainerList
	 *            The submitContainerList to set.
	 */
	public void setSubmitContainerList(ArrayList<BatchSubmissionContainerInfoModel> sContainerList) {
		removeAllObservablesFromList(submitContainerList);
//		addAllObservablesToList(sContainerList, submitContainerList);
		setModified(true);
	}

	/**
	 * @param test
	 *            The Biologist test to add to the list.
	 */
	public void removeSubmitContainer(int index) {
		if (index >= 0 && index < submitContainerList.size()) {
//			ObservableObject o = (ObservableObject) submitContainerList.get(index);
//			deleteObserver(o);
			submitContainerList.remove(index);
			setModified(true);
		}
	}

	/**
	 * @return Returns the submiToBiologistTestList.
	 */
	public ArrayList<BatchSubmissionToScreenModel> getSubmitToBiologistTestList() {
		return submitToBiologistTestList;
	}

	/**
	 * @param test
	 *            The Biologist test to add to the list.
	 */
	public void addSubmitToBiologistTest(BatchSubmissionToScreenModel test) {
		submitToBiologistTestList.add(test);
//		addObserver(test);
		setModified(true);
	}

	/**
	 * @param test
	 *            The Biologist test to add to the list.
	 */
	public void removeSubmitToBiologistTest(int index) {
		if (index >= 0 && index < submitToBiologistTestList.size()) {
//			ObservableObject o = (ObservableObject) submitToBiologistTestList.get(index);
//			deleteObserver(o);
			submitToBiologistTestList.remove(index);
			setModified(true);
		}
	}

	/**
	 * @param submiToBiologistTestList
	 *            The submiToBiologistTestList to set.
	 */
	public void setSubmitToBiologistTestList(ArrayList<BatchSubmissionToScreenModel> sToBiologistTestList) {
//		removeAllObservablesFromList(submitToBiologistTestList);
//		addAllObservablesToList(sToBiologistTestList, submitToBiologistTestList);
		setModified(true);
	}

	/**
	 * @return Returns the submissionStatus.
	 */
	public String getSubmissionStatus() {
		return submissionStatus;
	}

	/**
	 * @param submissionStatus
	 *            The submissionStatus to set.
	 */
	public void setSubmissionStatus(String submissionStatus) {
		if (!this.submissionStatus.equals(submissionStatus)) {
			this.submissionStatus = submissionStatus;
			setModified(true);
		}

	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(String status) {
		if (!this.status.equals(status)) {
			this.status = status;
			setModified(true);
		}
	}

	/**
	 * @return Returns the batchTrackingId.
	 */
	public String getBatchTrackingId() {
		return batchTrackingId;
	}

	/**
	 * @param batchTrackingId
	 *            The batchTrackingId to set.
	 */
	public void setBatchTrackingId(String batchTrackingId) {
		if (!this.batchTrackingId.equals(batchTrackingId)) {
			this.batchTrackingId = batchTrackingId;
			setModified(true);
		}

	}

	/**
	 * @return Returns the jobId.
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @param jobId
	 *            The jobId to set.
	 */
	public void setJobId(String jobId) {
		if (!this.jobId.equals(jobId)) {
			this.jobId = jobId;
			setModified(true);
		}
	}

	/**
	 * @return Returns the errorMsg.
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            The errorMsg to set.
	 */
	public void setErrorMsg(String errorMsg) {
		if (!this.errorMsg.equals(errorMsg)) {
			this.errorMsg = errorMsg;
			setModified(true);
		}
	}

	/**
	 * @return Returns the batchVnVInfo.
	 */
	public BatchVnVInfo getBatchVnVInfo() {
		return batchVnVInfo;
	}

	/**
	 * @param bVnVInfo
	 *            The batchVnVInfo to set.
	 */
	public void setBatchVnVInfo(BatchVnVInfo bVnVInfo) {
		batchVnVInfo.deepCopy(bVnVInfo);
		setModified(true);
	}

	// Events

	private void fireRegStatusChangedEvent() {
		ChangeEvent ce = new ChangeEvent(this);
		for (int i = regListeners.size() - 1; i >= 0; i--)
			((BatchRegistrationListener) regListeners.get(i)).batchRegistrationChanged(ce);
	}

	/**
	 * Information here indicates that the batch is in a registered state or is in the process of being registerred.
	 * 
	 * @return false if registerred or in process.
	 */
	public boolean allowBatchEdits() {
		return (getRegistrationStatus().equals(NOT_REGISTERED) || !(getStatus().equals(PROCESSING) || getStatus().equals(PASSED)));
	}
}
