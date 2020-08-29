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
 * Created on May 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.sessiontoken.ejb;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SessionInfo implements Serializable{
	
	static final long serialVersionUID = 7192025638634071282L;	
	
	private String sessionToken;
	private String userName;
	private Date timeCreated;
	
	public SessionInfo(){
		sessionToken = "";
		userName = "";
		timeCreated = new Date();
	}

	/**
	 * @return Returns the sessionToken.
	 */
	public String getSessionToken() {
		return sessionToken;
	}
	/**
	 * @param sessionToken The sessionToken to set.
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	/**
	 * @return Returns the timeCreated.
	 */
	public Date getTimeCreated() {
		return timeCreated;
	}
	/**
	 * @param timeCreated The timeCreated to set.
	 */
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
