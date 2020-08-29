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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.util.StringTokenizer;

public class PersonDelegate {
	private SessionTokenDelegate servicePort = null;
	private static Log log = LogFactory.getLog(PersonDelegate.class.getName());
	private static final BigInteger SearchResultCnt = new BigInteger("10");

	public PersonDelegate() throws LoadServiceException {
		try {
			servicePort = new SessionTokenDelegate();
		} catch (Exception e) {
			throw new LoadServiceException("", e);
		}
	}

	public IPerson[] getUsersByName(String first, String last) throws PersonServiceException {
		return getUsersByName(first, last, SearchResultCnt);
	}

	public String getLocationBy(String name) throws PersonServiceException {
		return servicePort.getLocationBy(name);
	}
	
	public IPerson[] getUsersByName(String first, String last, BigInteger cnt) throws PersonServiceException {
		if (first.equals("") && last.equals("")) {
		}
		return servicePort.getPeople(first, last, cnt);
	}

	public IPerson getPersonByFullName(String fullname) {
		if (fullname == null)
			return null;
		if (fullname.indexOf(',') < 0)
			return null;
		StringTokenizer st = new StringTokenizer(fullname, ", ");
		String last = st.nextToken();
		String first = st.nextToken();
		try {
			IPerson[] p = getUsersByName(first.trim(), last.trim());
			return p[0];
		} catch (Exception ex) {
			log.error(ex);
			return null;
		}
	}

	public IPerson userIDtoPerson(String userId) throws PersonServiceException {
		return servicePort.userIDtoPerson(userId);
	}
}
