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
package com.chemistry.enotebook.security.classes;

import java.io.Serializable;

public class PersonTO implements Serializable {

	private static final long serialVersionUID = 1696502238451634461L;
	
	private String userName;
	private String commonName;
	private String guid;
	private String firstName;
	private String middleInitial;
	private String lastName;
	private String description;
	private String ntDomain;
	private String smtpAddress;
	private String phone;
	private String title;
	private String department;
	private String location;
	private String employeeStatus;
	private String employeeType;

	public PersonTO() {
		userName = null;
		commonName = null;
		guid = null;
		firstName = null;
		middleInitial = null;
		lastName = null;
		description = null;
		ntDomain = null;
		smtpAddress = null;
		phone = null;
		title = null;
		department = null;
		location = null;
		employeeStatus = null;
		employeeType = null;
	}

	public PersonTO(String userName, String commonName, String guid,
			String firstName, String middleInitial, String lastName,
			String description, String ntDomain, String smtpAddress,
			String phone, String title, String department, String location,
			String employeeType, String employeeStatus) {
		this();
		this.userName = userName;
		this.commonName = commonName;
		this.guid = guid;
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;
		this.description = description;
		this.ntDomain = ntDomain;
		this.smtpAddress = smtpAddress;
		this.phone = phone;
		this.title = title;
		this.department = department;
		this.location = location;
		this.employeeType = employeeType;
		this.employeeStatus = employeeStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String s) {
		userName = s;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String s) {
		commonName = s;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String s) {
		guid = s;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String s) {
		firstName = s;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String s) {
		middleInitial = s;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String s) {
		lastName = s;
	}

	public String getNtDomain() {
		return ntDomain;
	}

	public void setNtDomain(String s) {
		ntDomain = s;
	}

	public String getSmtpAddress() {
		return smtpAddress;
	}

	public void setSmtpAddress(String s) {
		smtpAddress = s;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String s) {
		phone = s;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String s) {
		description = s;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String s) {
		title = s;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String s) {
		department = s;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String s) {
		location = s;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String s) {
		employeeType = s;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String s) {
		employeeStatus = s;
	}

	public String toString() {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("userName: " + userName + "\n");
		stringbuffer.append("commonName: " + commonName + "\n");
		stringbuffer.append("GUID: " + guid + "\n");
		stringbuffer.append("firstName: " + firstName + "\n");
		stringbuffer.append("middleInitial: " + middleInitial + "\n");
		stringbuffer.append("lastName: " + lastName + "\n");
		stringbuffer.append("description: " + description + "\n");
		stringbuffer.append("ntDomain: " + ntDomain + "\n");
		stringbuffer.append("smtpAddress: " + smtpAddress + "\n");
		stringbuffer.append("phone: " + phone + "\n");
		stringbuffer.append("title: " + title + "\n");
		stringbuffer.append("department: " + department + "\n");
		stringbuffer.append("location: " + location + "\n");
		stringbuffer.append("employeeType: " + employeeType + "\n");
		stringbuffer.append("employeeStatus: " + employeeStatus + "\n");
		return stringbuffer.toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PersonTO)) {
			return false;
		}
		PersonTO personto = (PersonTO) obj;
		int i = hashCode();
		int j = personto.hashCode();
		if (i == 0 && j == 0) {
			return false;
		}
		return i == j;
	}

	public int hashCode() {
		if (userName == null) {
			return 0;
		} else {
			return userName.toLowerCase().hashCode();
		}
	}
}
