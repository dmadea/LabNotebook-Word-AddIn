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
public class BatchSubmissionContainerInfo extends ObservableObject implements DeepClone, DeepCopy {

	private static final long serialVersionUID = 4396199933783409233L;
	
	private String barCode;
	private String amountUnit;
	private double amountValue;
	private String siteCode;
	private String submissionStatus;
	public static String NOT_SUBMITTED = "Not Submitted";
	public static String SUBMITTED = "Submitted";
	public static String SUBMITTING = "Submitting";

	public BatchSubmissionContainerInfo() {
		barCode = new String();
		amountValue = 0.0;
		amountUnit = new String();
		siteCode = "";
		submissionStatus = BatchSubmissionContainerInfo.NOT_SUBMITTED;
	}

	/**
	 * @return Returns the solubilityUnit.
	 */
	public String getAmountUnit() {
		return amountUnit;
	}

	/**
	 * @param solubilityUnit
	 *            The solubilityUnit to set.
	 */
	public void setAmountUnit(String solubilityUnit) {
		if (solubilityUnit != null && solubilityUnit.equals("G"))
			solubilityUnit = "GM";

			this.amountUnit = solubilityUnit;
			this.setModified(true);
	}

	/**
	 * @return Returns the codeAndName.
	 */
	public String getBarCode() {
		return barCode;
	}

	/**
	 * @param codeAndName
	 *            The codeAndName to set.
	 */
	public void setBarCode(String codeAndName) {
			this.barCode = codeAndName;
			this.setModified(true);
	}

	/**
	 * @return Returns the solubilityValue.
	 */
	public double getAmountValue() {
		return amountValue;
	}

	/**
	 * @param solubilityValue
	 *            The solubilityValue to set.
	 */
	public void setAmountValue(double solubilityValue) {
		if (this.amountValue != solubilityValue) {
			this.amountValue = solubilityValue;
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
			BatchSubmissionContainerInfo sourceInstance = (BatchSubmissionContainerInfo) source;
			barCode = sourceInstance.barCode;
			amountValue = sourceInstance.amountValue;
			amountUnit = sourceInstance.amountUnit;
			siteCode = sourceInstance.siteCode;
			submissionStatus = sourceInstance.submissionStatus;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchSubmissionContainerInfo target = new BatchSubmissionContainerInfo();
		target.deepCopy(this);
		return target;
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
			this.siteCode = siteCode;
			this.setModified(true);
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
			this.submissionStatus = submissionStatus;
			this.setModified(true);
	}
}
