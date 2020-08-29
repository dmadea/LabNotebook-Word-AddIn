/****************************************************************************
 * Copyright (C) 2009-2012 EPAM Systems
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

package com.epam.indigo.eln.tools.users.core;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 5074790003315999165L;

	private static final String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private static final String SUPERUSER = "<SuperUser/>";
	private static final String SUPERUSER2 = "<SuperUser />";

	private static final String USER_PROPERTIES_START = "<User_Properties>";
	private static final String USER_PROPERTIES_END = "</User_Properties>";
	
	private static final String PREFERENCES_START = "<Preferences>";
	private static final String PREFERENCES_END = "</Preferences>";

	private static final String EMPLOYEE_ID_START = "<EmployeeID>";
	private static final String EMPLOYEE_ID_END = "</EmployeeID>";
	
	public static final String DEFAULT_XML_METADATA = XML_PREFIX + USER_PROPERTIES_START + PREFERENCES_START + PREFERENCES_END + USER_PROPERTIES_END;
	public static final String DEFAULT_MY_REAGENTS = XML_PREFIX + "<Reagents></Reagents>";
	public static final String DEFAULT_AUDIT_LOG = XML_PREFIX + "<Audit_Log><Log></Log></Audit_Log>";

	private String username;

	private String password;

	private String sitecode;

	private String firstname;

	private String lastname;
	
	private String xmlMetadata;

	private String myReagentList;

	private String auditLog;

	private String email;

	public User() {
		setXmlMetadata(DEFAULT_XML_METADATA);
		setMyReagentList(DEFAULT_MY_REAGENTS);
		setAuditLog(DEFAULT_AUDIT_LOG);
	}

	public String getUsername() {
		return username.toUpperCase();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSitecode() {
		return sitecode;
	}

	public void setSitecode(String sitecode) {
		this.sitecode = sitecode;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmployeeId() {
		String employeeId = "";
		
		int idx = xmlMetadata.indexOf(EMPLOYEE_ID_START);
		
		if (idx > 0)
			employeeId = xmlMetadata.substring(idx + EMPLOYEE_ID_START.length(), xmlMetadata.indexOf(EMPLOYEE_ID_END));
		
		return employeeId.toUpperCase();
	}

	public void setEmployeeId(String employeeId) {
		int idx = xmlMetadata.indexOf(EMPLOYEE_ID_START);
		
		if (idx > 0)
			xmlMetadata = xmlMetadata.substring(0, idx + EMPLOYEE_ID_START.length()) + employeeId.toUpperCase() + xmlMetadata.substring(xmlMetadata.indexOf(EMPLOYEE_ID_END));
		else {
			int place = xmlMetadata.indexOf(USER_PROPERTIES_START) + USER_PROPERTIES_START.length();
			xmlMetadata = xmlMetadata.substring(0, place) + EMPLOYEE_ID_START + employeeId.toUpperCase() + EMPLOYEE_ID_END + xmlMetadata.substring(place);
		}
	}
	
	public String getXmlMetadata() {
		return xmlMetadata;
	}

	public void setXmlMetadata(String xmlMetadata) {
		this.xmlMetadata = xmlMetadata;
	}

	public String getMyReagentList() {
		return myReagentList;
	}

	public void setMyReagentList(String myReagentList) {
		this.myReagentList = myReagentList;
	}

	public String getFullName() {
		return lastname + ", " + firstname;
	}

	public String getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(String auditLog) {
		this.auditLog = auditLog;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isSuperUser() {
		boolean b = xmlMetadata.indexOf(SUPERUSER) != -1;

		if (!b) {
			b = xmlMetadata.indexOf(SUPERUSER2) != -1;
			if (b) {
				xmlMetadata = xmlMetadata.replaceAll(SUPERUSER2, SUPERUSER);
			}
		}

		return b;
	}

	public void setSuperUser(boolean superuser) {
		isSuperUser(); // TODO do this job with java-xml
		
		if (superuser && isSuperUser())
			return;

		if (!superuser && !isSuperUser())
			return;

		int idx = xmlMetadata.indexOf(PREFERENCES_START) + PREFERENCES_START.length();

		if (superuser) {
			xmlMetadata = xmlMetadata.substring(0, idx) + SUPERUSER + xmlMetadata.substring(idx);
		} else {
			xmlMetadata = xmlMetadata.substring(0, idx) + xmlMetadata.substring(idx + SUPERUSER.length());
		}
	}
}
