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
package com.chemistry.enotebook.person.clean;

import com.chemistry.enotebook.person.classes.IPerson;

public class Person implements IPerson {

	private static final long serialVersionUID = -764956776924308964L;

	private String firstName;
	private String lastName;
	private String locationDescr;
	private String middleName;
	private String logonId;
	private String phoneBusinessOffice;

	public Person(String firstName, String lastName, String middleName,
			String locationDescr, String logonId, String phoneBusinessOffice) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.locationDescr = locationDescr;
		this.middleName = middleName;
		this.logonId = logonId;
		this.phoneBusinessOffice = phoneBusinessOffice;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getLocationDescr() {
		return locationDescr;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLogonId() {
		return logonId;
	}

	public String getPhoneBusinessOffice() {
		return phoneBusinessOffice;
	}
}
