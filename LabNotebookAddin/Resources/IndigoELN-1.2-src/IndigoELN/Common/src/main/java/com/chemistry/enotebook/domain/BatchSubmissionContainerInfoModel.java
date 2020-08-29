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

import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import org.apache.commons.lang.StringUtils;

public class BatchSubmissionContainerInfoModel extends CeNAbstractModel{
	
	private static final long serialVersionUID = 1997166373050575424L;
	
	private String barCode = "";
	private String tubeGuid = "";
	private String amountUnit = "MG";
	private double amountValue;
	private String siteCode = "";
	private String submissionStatus = BatchSubmissionContainerInfoModel.NOT_SUBMITTED;
	public static String NOT_SUBMITTED = "Not Submitted";
	public static String SUBMITTED = "Submitted";
	public static String SUBMITTING = "Submitting";
	private String moleUnit = "MMOL"; //default val to enable mol amount cell
	private double moleValue;
	private String volumeUnit = "ML";
	private double volumeValue;
	private String solvent = "";
	private String containerTypeCode = "";
	private String containerLocation = "";
	private String containerType = ""; //vial or tube. We need to know this since the workflow/validations are different.

	private PurificationServiceSubmisionParameters purificationServiceParameters = new PurificationServiceSubmisionParameters();
	private boolean isPurificationServiceParamatersSetByUser = false;
	
	public BatchSubmissionContainerInfoModel() {
		
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
		if (solubilityUnit != null && solubilityUnit.trim().length() > 0 && solubilityUnit.equals("G"))
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
			BatchSubmissionContainerInfoModel sourceInstance = (BatchSubmissionContainerInfoModel) source;
			barCode = sourceInstance.barCode;
			tubeGuid = sourceInstance.tubeGuid;
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
		BatchSubmissionContainerInfoModel target = new BatchSubmissionContainerInfoModel();
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
	
	private String formatXMLTag(String tagName, String value) {
		StringBuffer tagStr = new StringBuffer("");
	  
		tagStr.append("<" + tagName + ">");

		if (StringUtils.isNotBlank(value)) {
			tagStr.append(value);  // ensure not to include "null" as the value
		}
		
		tagStr.append("</" + tagName + ">");
	  
		return tagStr.toString();
	}

	private String formatXMLTag(String tagName, double value) {
		return formatXMLTag(tagName, "" + value);
	}
	
	public String toXML() {
		StringBuffer xmlStr = new StringBuffer();;
		
		xmlStr.append(formatXMLTag("BarCode", barCode));
		xmlStr.append(formatXMLTag("Tube_Guid", tubeGuid));
		xmlStr.append(formatXMLTag("Amount_Unit", amountUnit));
		xmlStr.append(formatXMLTag("Amount_Value", amountValue));
		xmlStr.append(formatXMLTag("Mole_Unit", moleUnit));
		xmlStr.append(formatXMLTag("Mole_Value", moleValue));
		xmlStr.append(formatXMLTag("Volume_Unit", volumeUnit));
		xmlStr.append(formatXMLTag("Volume_Value", volumeValue));
		xmlStr.append(formatXMLTag("Container_Type", containerType));
		xmlStr.append(formatXMLTag("Container_Type_Code", containerTypeCode));
		xmlStr.append(formatXMLTag("Solvent", solvent));
		xmlStr.append(formatXMLTag("Location_Code", containerLocation));
		xmlStr.append(formatXMLTag("Site_Code", siteCode));
		xmlStr.append(formatXMLTag("Submission_Status", submissionStatus));
		    
		xmlStr.append(purificationServiceParameters != null ? purificationServiceParameters.toXML() : "");
		
		return xmlStr.toString();
	}

	public String getContainerLocation() {
		return containerLocation;
	}

	public void setContainerLocation(String containerLocation) {
		this.containerLocation = containerLocation;
		this.setModified(true);
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
		this.setModified(true);
	}

	public String getContainerTypeCode() {
		return containerTypeCode;
	}

	public void setContainerTypeCode(String containerTypeCode) {
		this.containerTypeCode = containerTypeCode;
		this.setModified(true);
	}

	public String getMoleUnit() {
		return moleUnit;
	}

	public void setMoleUnit(String moleUnit) {
		this.moleUnit = moleUnit;
		this.setModified(true);
	}

	public double getMoleValue() {
		return moleValue;
	}

	public void setMoleValue(double moleValue) {
		this.moleValue = moleValue;
		this.setModified(true);
	}

	public String getSolvent() {
		return solvent;
	}

	public void setSolvent(String solvent) {
		this.solvent = solvent;
		this.setModified(true);
	}

	public String getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(String volumeUnit) {
		this.volumeUnit = volumeUnit;
		this.setModified(true);
	}

	public double getVolumeValue() {
		return volumeValue;
	}

	public void setVolumeValue(double volumeValue) {
		this.volumeValue = volumeValue;
		this.setModified(true);
	}

	public void setTubeGuid(String tubeGuid) {
		this.tubeGuid = tubeGuid;
		setModified(true);
	}

	public String getTubeGuid() {
		return tubeGuid;
	}

	public void setPurificationServiceParameters(PurificationServiceSubmisionParameters purificationServiceParameters) {
		this.purificationServiceParameters = purificationServiceParameters;
		setModified(true);
	}

	public PurificationServiceSubmisionParameters getPurificationServiceParameters() {
		return purificationServiceParameters;
	}
	
	public void setPurificationServiceParamatersSetByUser(boolean isPurificationServiceParamatersSetByUser) {
		this.isPurificationServiceParamatersSetByUser  = isPurificationServiceParamatersSetByUser;
	}

	public boolean isPurificationServiceParamatersSetByUser() {
		return isPurificationServiceParamatersSetByUser;
	}
}
