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
 * Created on Jul 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.session.security;

import java.io.Serializable;



/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SessionToken 
	implements Serializable 
{
    static final long serialVersionUID = -2020403286661708749L;

	private long loginTime;
	private int noOfAttempts;
	private long lastLoginTime;
	private String firstName;
	private String lastName;
	private String displayName;
	private String smtpEmail;
	private UserData userData;
	private String ntDomain;
	
	private SessionIdentifier sessionID = null;
	
	
	public SessionIdentifier getSessionIdentifier() { return sessionID; }
	
	public SessionToken(String userName, String tokenString, UserData ud)
	{
		// TODO:  This should be changed to access this as a document to ensure
		// that SuperUser is not found somewhere else
		// Actual Location is /User_Properties/Preferences@SuperUser
        boolean superUser = isSuperUser(ud);
    	sessionID = new SessionIdentifier(ud.getSiteCode(), userName, tokenString, superUser);

		userData = ud;
	}
	
	private boolean isSuperUser(UserData ud) {
		if (ud.getXmlMetaData().indexOf("<SuperUser/>") != -1)
			return true;
		
		if (ud.getXmlMetaData().indexOf("<SuperUser />") != -1)
			return true;
		
		return false;
	}
	
	/**
	 * @return Returns the ntUser.
	 */
	public String getNtUser() {
		return sessionID.getUserID();
	}	
	
	/**
	 * @return Returns the tokenString.
	 */
	public String getTokenString() {
		return sessionID.getTokenString();
	}
	
	/**
	 * @return Returns the userData.
	 */
	public UserData getUserData() {
		return userData;
	}
	
	/**
	 * @return Returns the lastLoginTime.
	 */
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	/**
	 * @param lastLoginTime The lastLoginTime to set.
	 */
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	/**
	 * @return Returns the loginTime.
	 */
	public long getLoginTime() {
		return loginTime;
	}
	/**
	 * @param loginTime The loginTime to set.
	 */
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
	
	/**
	 * @return Returns the noOfAttempts.
	 */
	public int getNoOfAttempts() {
		return noOfAttempts;
	}
	/**
	 * @param noOfAttempts The noOfAttempts to set.
	 */
	public void setNoOfAttempts(int noOfAttempts) {
		this.noOfAttempts = noOfAttempts;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * @return Returns the smtpEmail.
	 */
	public String getSmtpEmail() {
		return smtpEmail;
	}
	/**
	 * @param smtpEmail The smtpEmail to set.
	 */
	public void setSmtpEmail(String smtpEmail) {
		this.smtpEmail = smtpEmail;
	}

	public String getNtDomain() {
		return ntDomain;
	}
	public void setNtDomain(String ntDomain) {
		this.ntDomain = ntDomain;
	}
}
