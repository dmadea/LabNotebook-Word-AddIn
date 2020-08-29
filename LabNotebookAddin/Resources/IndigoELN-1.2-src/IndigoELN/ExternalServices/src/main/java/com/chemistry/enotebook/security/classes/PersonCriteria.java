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

public class PersonCriteria implements Serializable {

	private static final long serialVersionUID = -3815588207888654411L;
	
	private String userName;
	private String firstName;
	private String lastName;
	private String phone;
	private String mail;

	public PersonCriteria(String userName, String firstName, String lastName, String phone, String mail) {
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.mail = mail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String s) {
		userName = s;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String s) {
		firstName = s;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String s) {
		lastName = s;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String s) {
		phone = s;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String s) {
		mail = s;
	}

	public String toString() {
		return "PersonCriteria: userName = " + userName + " firstName = "
				+ firstName + " lastName = " + lastName + " phone = " + phone
				+ " mail = " + mail;
	}
}
