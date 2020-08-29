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
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.registration;

import java.io.Serializable;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScreenSearchVO 
	implements Serializable
{
	static final long serialVersionUID = 284994650944414624L;

	private String screenProtocolID;
	private String screenCode;
	private String screenProtocolTitle;
	
	private String projectCode;
	private String projectName;
	
	
	private String minAmountUnit;
	private double minAmountValue;

	private String containerTypeCode;
	
	private String scientistCode;
	private String recipientSiteCode;
	private String recipientUserID;
	private String recipientName;
	
	
	
	// Must have screenProtocolID and one of these (scientistCode or scientistUserID) 
	public ScreenSearchVO(String screenProtocolID, String scientistCode, String scientistUserID)
	{
		this.screenProtocolID = screenProtocolID;
		if (scientistCode != null) this.scientistCode = scientistCode;
		if (scientistUserID != null) this.recipientUserID = scientistUserID;
		screenCode = "";
		screenProtocolTitle = "";
		
		projectCode = "";
		projectName = "";
		
		minAmountUnit = "";
		minAmountValue = 0.0;

		containerTypeCode = "";
	
		recipientSiteCode = "";
		recipientUserID = "";
		recipientName = "";
	}
	
	
	/**
	 * @return Returns the ScreenProtocolID.
	 */
	public String getScreenProtocolID() 
	{
		return screenProtocolID;
	}
	
	/**
	 * @param code The Biology Screen Protocol ID
	 */
	public void setScreenProtocolID(String code) 
	{
		screenProtocolID = code;
	}

	
	/**
	 * @return Returns the ScientistCode.
	 */
	public String getScientistCode() 
	{
		return scientistCode;
	}
	
	/**
	 * @param code The Scientist ID associated with the Biology Screen Protocol
	 */
	public void setScientistCode(String code) 
	{
		scientistCode = code;
	}


	public String getMinAmountUnit() { return minAmountUnit; }
	public void setMinAmountUnit(String unit) {
		this.minAmountUnit = unit;
	}
	
	public double getMinAmountValue() {	return minAmountValue; }
	public void setMinAmountValue(double value) {
		this.minAmountValue = value;
	}


	public String getContainerTypeCode() { return containerTypeCode; }
	public void setContainerTypeCode(String code) {
		containerTypeCode = code;
	}

	
	public String getRecipientSiteCode() { return recipientSiteCode; }
	public void setRecipientSiteCode(String code) {
		recipientSiteCode = code;
	}

	public String getRecipientUserID() { return recipientUserID; }
	public void setRecipientUserID(String id) {
		recipientUserID = id;
	}
	
	/**
	 * @return Returns the projectCode.
	 */
	public String getProjectCode() {
		return projectCode;
	}
	/**
	 * @param projectCode The projectCode to set.
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName The projectName to set.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return Returns the recipientName.
	 */
	public String getRecipientName() {
		return recipientName;
	}
	/**
	 * @param recipientName The recipientName to set.
	 */
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	/**
	 * @return Returns the screenCode.
	 */
	public String getScreenCode() {
		return screenCode;
	}
	/**
	 * @param screenCode The screenCode to set.
	 */
	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}
	/**
	 * @return Returns the screenID.
	 */
	public String getScreenProtocolTitle() {
		return screenProtocolTitle;
	}
	/**
	 * @param screenID The screenID to set.
	 */
	public void setScreenProtocolTitle(String screenID) {
		this.screenProtocolTitle = screenID;
	}
}

