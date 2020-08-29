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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;


public class BatchVnVInfoModel extends CeNAbstractModel {

	public static final long serialVersionUID = 7526472295622776147L;

	public static final String VNV_PASS = "Pass";
	public static final String VNV_FAIL = "Fail";
	public static final String SIC_HSREG = "HSREG";
	public static final String SIC_ERROR = "ERROR";
	private String status="";
	private String molData=""; //Vnv result struc
	private String errorMsg="";
	private String assignedStereoIsomerCode="";
	private ArrayList<String> suggestedSICList = new ArrayList<String>();
	private String molFormula = new String();
	private double molWeight = 0.0;
	public String getMolFormula() {
		return molFormula;
	}

	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	public double getMolWeight() {
		return molWeight;
	}

	public void setMolWeight(double molWeight) {
		this.molWeight = molWeight;
	}

	public BatchVnVInfoModel() {
		status = "";
		molData = "";
		errorMsg = "";
		assignedStereoIsomerCode = "";
		suggestedSICList = new ArrayList<String>();
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the molData
	 */
	public String getMolData() {
		return molData;
	}

	/**
	 * @param molData
	 *            the molData to set
	 */
	public void setMolData(String molData) {
		this.molData = molData;
	}

	/**
	 * Checks to see if VnV was run (getStatus() is not blank) and that we passed VnV
	 * Also will indicate false if the value of the assigned Stereoisomer Code = "HSREG"
	 * 
	 * @return
	 */
	public boolean isPassed() {
		return StringUtils.equalsIgnoreCase(getStatus(), VNV_PASS) &&
		       StringUtils.isNotBlank(getAssignedStereoIsomerCode()) && 
		       StringUtils.equals(getAssignedStereoIsomerCode(), SIC_HSREG) == false;
	}
	
	/** 
	 * Check to see if we ran VnV and got a "Fail" as a return value or if the assigned Stereoisomer Code contains the word "ERROR"
	 * @return
	 */
	public boolean isFailed() {
		return StringUtils.isNotBlank(getStatus()) && // indicates we had a run
		      ( StringUtils.equalsIgnoreCase(getStatus(), VNV_FAIL) || 
		        StringUtils.contains(getAssignedStereoIsomerCode(), SIC_ERROR));
	}
	
	/**
	 * @return the status - should only be set if there was a VnV result for this object
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * suggested SIC List shows VnV response for alternates. 
	 * One would have been chosen as the most likely candidate.
	 * 
	 * @return the suggestedSICList
	 */
	public ArrayList<String> getSuggestedSICList() {
		return suggestedSICList;
	}

	/**
	 * @param suggestedSICList
	 *            the suggestedSICList to set
	 */
	public void setSuggestedSICList(ArrayList<String> suggestedSICList) {
		this.suggestedSICList = suggestedSICList;
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();

		return xmlbuff.toString();
	}

	/**
	 * @return the assignedSICode
	 */
	public String getAssignedStereoIsomerCode() {
		return assignedStereoIsomerCode;
	}

	/**
	 * @param assignedSICode the assignedSICode to set
	 */
	public void setAssignedStereoIsomerCode(String assignedStereoIsomerCode) {
		this.assignedStereoIsomerCode = assignedStereoIsomerCode;
	}

}
