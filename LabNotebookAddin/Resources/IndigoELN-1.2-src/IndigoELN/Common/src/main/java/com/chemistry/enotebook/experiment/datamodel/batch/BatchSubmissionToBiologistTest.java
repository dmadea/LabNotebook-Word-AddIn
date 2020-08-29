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
 * Created on Dec 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchSubmissionToBiologistTest extends ObservableObject implements DeepClone, DeepCopy {
	
	private static final long serialVersionUID = -6738215937730464380L;
	
	public static String NOT_SUBMITTED = "Not Submitted";
	public static String SUBMITTED = "Submitted";
	public static String SUBMITTING = "Submitting";

	private String submittedByMm;
	private String screenProtocolId;
	private String screenCode;
	private String screenProtocolTitle;
	private String siteCode;
	private String scientistName;
	private String scientistCode;
	private String minAmountUnit;
	private double minAmountValue;
	private String containerType;
	private String submissionStatus;
	private String amountUnit;
	private double amountValue;

	public BatchSubmissionToBiologistTest() {
		submittedByMm = "false";
		screenProtocolId = "";
		screenCode = "";
		screenProtocolTitle = "";
		siteCode = "";
		scientistCode = "";
		scientistName = "";
		minAmountValue = 0.0;
		minAmountUnit = "";
		containerType = "";
		submissionStatus = BatchSubmissionToBiologistTest.NOT_SUBMITTED;
		amountValue = 0.0;
		amountUnit = "";
	}

	/**
	 * @return Returns the SubmittedByMm.
	 */
	public String getSubmittedByMm() {
		return submittedByMm;
	}

	/**
	 * @param value
	 *            Flag indicating Screen submitted by MM vs. self
	 */
	public void setSubmittedByMm(String value) {
		if (value == null)
			value = "false";
		if (!submittedByMm.equals(value)) {
			submittedByMm = value;
			this.setModified(true);
		}
	}

	public boolean isTestSubmittedByMm() {
		return submittedByMm.equals("true");
	}

	/**
	 * @return Returns the ScreenProtocolID.
	 */
	public String getScreenProtocolId() {
		return screenProtocolId;
	}

	/**
	 * @param code
	 *            The Biology Screen Protocol ID
	 */
	public void setScreenProtocolId(String code) {
		if (!screenProtocolId.equals(code)) {
			screenProtocolId = code;
			setModified(true);
		}
	}

	/**
	 * @return Returns the ScientistCode.
	 */
	public String getScientistCode() {
		return scientistCode;
	}

	/**
	 * @param code
	 *            The Scientist ID associated with the Biology Screen Protocol
	 */
	public void setScientistCode(String code) {
		if (!scientistCode.equals(code)) {
			scientistCode = code;
			setModified(true);
		}
	}

	/**
	 * @return Returns the ActualAmount.
	 */
	public String getMinAmountUnit() {
		return minAmountUnit;
	}

	/**
	 * @param unit
	 *            The unit String to set.
	 */
	public void setMinAmountUnit(String unit) {
		if (!this.minAmountUnit.equals(unit)) {
			this.minAmountUnit = unit;
			this.setModified(true);
		}
	}

	/**
	 * @return Returns the ActualAmountValue.
	 */
	public double getMinAmountValue() {
		return minAmountValue;
	}

	/**
	 * @param value
	 *            The Actual value to set.
	 */
	public void setMinAmountValue(double value) {
		if (this.minAmountValue != value) {
			this.minAmountValue = value;
			this.setModified(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepCopy#deepCopy(java.lang.Object)
	 */
	public void deepCopy(Object source) {
		if (source != null) {
			BatchSubmissionToBiologistTest sourceInstance = (BatchSubmissionToBiologistTest) source;

			submittedByMm = sourceInstance.submittedByMm;
			screenProtocolId = sourceInstance.screenProtocolId;
			scientistCode = sourceInstance.scientistCode;
			minAmountValue = sourceInstance.minAmountValue;
			minAmountUnit = sourceInstance.minAmountUnit;
			scientistName = sourceInstance.scientistName;
			screenProtocolTitle = sourceInstance.screenProtocolTitle;
			siteCode = sourceInstance.siteCode;
			screenCode = sourceInstance.screenCode;
			containerType = sourceInstance.containerType;
			submissionStatus = sourceInstance.submissionStatus;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchSubmissionToBiologistTest target = new BatchSubmissionToBiologistTest();

		target.submittedByMm = this.submittedByMm;
		target.screenProtocolId = this.screenProtocolId;
		target.scientistCode = this.scientistCode;
		target.minAmountValue = this.minAmountValue;
		target.minAmountUnit = this.minAmountUnit;
		target.scientistName = this.scientistName;
		target.screenProtocolTitle = this.screenProtocolTitle;
		target.siteCode = this.siteCode;
		target.screenCode = this.screenCode;
		target.containerType = this.containerType;
		target.submissionStatus = this.submissionStatus;

		return target;
	}

	/**
	 * @return Returns the scientistName.
	 */
	public String getScientistName() {
		return scientistName;
	}

	/**
	 * @param scientistName
	 *            The scientistName to set.
	 */
	public void setScientistName(String scientistName) {
		if (!this.scientistName.equals(scientistName)) {
			this.scientistName = scientistName;
			setModified(true);
		}
	}

	/**
	 * @return Returns the screenCode.
	 */
	public String getScreenCode() {
		return screenCode;
	}

	/**
	 * @param screenCode
	 *            The screenCode to set.
	 */
	public void setScreenCode(String screenCode) {
		if (!this.screenCode.equals(screenCode)) {
			this.screenCode = screenCode;
			setModified(true);
		}
	}

	/**
	 * @return Returns the screenProtocolTitle.
	 */
	public String getScreenProtocolTitle() {
		return screenProtocolTitle;
	}

	/**
	 * @param screenProtocolTitle
	 *            The screenProtocolTitle to set.
	 */
	public void setScreenProtocolTitle(String screenProtocolTitle) {
		if (!this.screenProtocolTitle.equals(screenProtocolTitle)) {
			this.screenProtocolTitle = screenProtocolTitle;
			setModified(true);
		}
	}

	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode
	 *            The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		if (!this.siteCode.equals(siteCode)) {
			this.siteCode = siteCode;
			setModified(true);
		}
	}

	/**
	 * @return Returns the containerType.
	 */
	public String getContainerType() {
		return containerType;
	}

	/**
	 * @param containerType
	 *            The containerType to set.
	 */
	public void setContainerType(String containerType) {
		if (!this.containerType.equals(containerType)) {
			this.containerType = containerType;
			setModified(true);
		}
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
	 * @return Returns the AmountUnit.
	 */
	public String getAmountUnit() {
		return amountUnit;
	}

	/**
	 * @param unit
	 *            The unit String to set.
	 */
	public void setAmountUnit(String unit) {
		if (!this.amountUnit.equals(unit)) {
			this.amountUnit = unit;
			this.setModified(true);
		}
	}

	/**
	 * @return Returns the ActualAmountValue.
	 */
	public double getAmountValue() {
		return amountValue;
	}

	/**
	 * @param value
	 *            The Actual value to set.
	 */
	public void setAmountValue(double value) {
		if (this.amountValue != value) {
			this.amountValue = value;
			this.setModified(true);
		}
	}
}
