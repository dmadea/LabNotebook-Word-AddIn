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
package com.chemistry.enotebook.person;

import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;

import java.math.BigInteger;

/**
 * Interface for external Person Service
 */
public interface IPersonService {

	/**
	 * Get information about people with given first and last name
	 * 
	 * @param first
	 *            first name
	 * @param last
	 *            last name
	 * @param cnt
	 *            results count
	 * @return Information array
	 * @throws PersonServiceException
	 */
	public IPerson[] getPeople(String first, String last, BigInteger cnt)
			throws PersonServiceException;

	/**
	 * Get information about people with given User ID
	 * 
	 * @param ntName
	 *            User ID
	 * @return Information array
	 * @throws PersonServiceException
	 */
	public IPerson[] getUsersByUserName(String ntName)
			throws PersonServiceException;

	/**
	 * Get user's location
	 * 
	 * @param name
	 *            User ID
	 * @return Location
	 * @throws PersonServiceException
	 */
	public String getLocationBy(String name) throws PersonServiceException;

	/**
	 * Get information about user with given ID
	 * 
	 * @param userId
	 *            User ID
	 * @return Information about user
	 * @throws PersonServiceException
	 */
	public IPerson userIDtoPerson(String userId) throws PersonServiceException;
}
