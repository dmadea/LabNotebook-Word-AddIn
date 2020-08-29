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
 * Created on Aug 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.valueobject;

import java.io.Serializable;

/**
 * 
 *
 */
public class UserInfoVO 
	implements Serializable
{
	static final long serialVersionUID = 6399006905142390423L;	
	
	private String userName;
	private String fullName;
	private String siteCode;
	private String status;

	
	public UserInfoVO() { }
	
	public UserInfoVO(String userID){
		this.userName = userID;
	}
	
	
	public String getUserName(){
		return this.userName;
	}
	
	public String getFullName(){
		return this.fullName;
	}
	
	public String getSiteCode(){
		return this.siteCode;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public void setUserName(String userId){
		this.userName = userId;
	}
	
	public void setFullName(String fName){
		this.fullName = fName;
	}
	
	public void setSiteCode(String sCode){
		this.siteCode = sCode;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

}
