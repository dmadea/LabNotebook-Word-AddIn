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
package com.chemistry.enotebook.session.security;



/**
 *
 * This class identifies a user within the CeN system.
 * It provides the userId, a flag if this user is a super user
 * and an identifier within the system that provides access to
 * CeN mid-tier services
 */
public class SessionIdentifier
	implements java.io.Serializable
{
    static final long serialVersionUID = 5820926433310414407L;	

	private String siteCode = "";
	private String userID = "";
	private String password = "";
	private String tokenString = "";
	private boolean superUser = false;
	//Below is defined for the purpose of Performance Testing, Need be removed later.
	private HttpUserProfile userProfile = null;
	private String threadId = "";
	private String compoundManagementEmployeeID = "";
	
	//public SessionIdentifier() { }
	public SessionIdentifier(String siteCode, String userID, 
			String tokenString, boolean superUser) {
		this.siteCode = siteCode;
		this.userID = userID;
		this.tokenString = tokenString;
		this.superUser = superUser;
	}
	
	public String getSiteCode() { return siteCode; }
	
	public String getUserID() { return userID; }

	public String getTokenString() { return tokenString; }
	
    public boolean isSuperUser() { return superUser; }
    
    public String getThreadId() { return threadId; }
    
    public void setThreadId(String id) { this.threadId=id; }

	/**
	 * @return the userProfile
	 */
	public HttpUserProfile getUserProfile() {
		return userProfile;
	}

	/**
	 * @param userProfile the userProfile to set
	 */
	public void setUserProfile(HttpUserProfile userProfile) {
		this.userProfile = userProfile;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompoundManagementEmployeeID() {
		return compoundManagementEmployeeID;
	}

	public void setCompoundManagementEmployeeID(String compoundManagementEmployeeID) {
		this.compoundManagementEmployeeID = compoundManagementEmployeeID;
	}
	
	

}
