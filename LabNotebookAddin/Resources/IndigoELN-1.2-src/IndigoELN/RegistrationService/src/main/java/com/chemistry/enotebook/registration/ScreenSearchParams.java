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
public class ScreenSearchParams 
	implements Serializable
{
	static final long serialVersionUID = -1328382316325674365L;	
	
	private String siteCode;
	private String tACode;
	private String projectCode;
	private String screenCode;
	private String screenProtocol;
	private String scientistName;
	
	
	public ScreenSearchParams(){
		siteCode = "";
		tACode = "";
		projectCode = "";
		screenCode = "";
		screenProtocol = "";
		scientistName = "";
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
	 * @return Returns the scientistName.
	 */
	public String getScientistName() {
		return scientistName;
	}
	/**
	 * @param scientistName The scientistName to set.
	 */
	public void setScientistName(String scientistName) {
		this.scientistName = scientistName;
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
	 * @return Returns the screenProtocol.
	 */
	public String getScreenProtocol() {
		return screenProtocol;
	}
	/**
	 * @param screenProtocol The screenProtocol to set.
	 */
	public void setScreenProtocol(String screenProtocol) {
		this.screenProtocol = screenProtocol;
	}
	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return siteCode;
	}
	/**
	 * @param siteCode The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	/**
	 * @return Returns the tACode.
	 */
	public String getTACode() {
		return tACode;
	}
	/**
	 * @param code The tACode to set.
	 */
	public void setTACode(String code) {
		tACode = code;
	}
}
