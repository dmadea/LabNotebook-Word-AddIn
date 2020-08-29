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


public class CROPageInfo extends CeNAbstractModel{

	public static final long serialVersionUID = 7526472222322776147L;
	//key should be same as Page_KEY
	private String croID="";
	private String croDisplayName="";
	private CROChemistInfo croChemistInfo;
	private String croAplicationSourceName="";
	private String requestId="";
	private String additionalXMLInfo="";
	
	public CROPageInfo (String key)
	{
		this.key = key;	
	}
	
	
	public String getCroAplicationSourceName() {
		return croAplicationSourceName;
	}

	public void setCroAplicationSourceName(String croAplicationSourceName) {
		this.croAplicationSourceName = croAplicationSourceName;
	}


	public CROChemistInfo getCroChemistInfo() {
		return this.croChemistInfo;
	}

	public void setCroChemistInfo(CROChemistInfo croChemistInfo) {
		this.croChemistInfo = croChemistInfo;
	}

	public String getCroDisplayName() {
		return croDisplayName;
	}

	public void setCroDisplayName(String croDisplayName) {
		this.croDisplayName = croDisplayName;
	}

	public String getCroID() {
		return croID;
	}

	public void setCroID(String croID) {
		this.croID = croID;
	}

	public String toString()
	{
		return croDisplayName;
	}
	/**
	 * @return the additionalXMLInfo
	 */
	public String getAdditionalXMLInfo() {
		return additionalXMLInfo;
	}
	/**
	 * @param additionalXMLInfo the additionalXMLInfo to set
	 */
	public void setAdditionalXMLInfo(String additionalXMLInfo) {
		this.additionalXMLInfo = additionalXMLInfo;
	}
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String toXML(){
		return "[CRO REQUEST ID="+this.getRequestId()+
		"; CRO ID ="+this.getCroID()+
		"; CRO DISPLAY NAME="+this.getCroDisplayName()+
		this.getCroChemistInfo()+
		"; CRO REQUEST ID="+this.getRequestId()+"]";
	}
}
