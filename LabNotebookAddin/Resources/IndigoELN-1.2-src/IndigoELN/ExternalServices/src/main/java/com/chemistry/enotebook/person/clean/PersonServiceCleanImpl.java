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

import com.chemistry.enotebook.person.IPersonService;
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;

import java.math.BigInteger;

public class PersonServiceCleanImpl implements IPersonService {
	
	public IPerson[] getPeople(String first, String last, BigInteger cnt) throws PersonServiceException {
		return new Person[0];
	}

	public IPerson[] getUsersByUserName(String ntName) throws PersonServiceException {
		return new Person[0];
	}

	public String getLocationBy(String name) throws PersonServiceException {
		return null;
	}

	public IPerson userIDtoPerson(String userId) throws PersonServiceException {
		return null;
	}
}
