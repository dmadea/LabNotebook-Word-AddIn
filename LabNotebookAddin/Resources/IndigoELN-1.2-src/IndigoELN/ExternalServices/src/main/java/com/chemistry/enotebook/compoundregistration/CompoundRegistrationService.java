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
package com.chemistry.enotebook.compoundregistration;

import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.search.Compound;

import java.util.List;

/**
 * Interface for external Compound Registration service
 */
public interface CompoundRegistrationService {

	/**
	 * Check service health
	 * 
	 * @return true if service available, false if service unavailable
	 */
	public boolean checkHealth();

//	/**
//	 * Get token for user from Registration Service
//	 * 
//	 * @param username
//	 *            username
//	 * @param password
//	 *            password
//	 * @return user's token
//	 * @throws RegistrationRuntimeException
//	 */
//	public String getTokenHash(String username, String password)
//			throws RegistrationRuntimeException;

	/**
	 * Get token for user from Registration Service.
	 * 
	 * Service implementation should use own username & password 
	 * 
	 * @return user's token
	 * @throws RegistrationRuntimeException
	 */
	public String getTokenHash()
			throws RegistrationRuntimeException;
	
	/**
	 * Submit compounds for registration
	 * 
	 * @param tokenHash
	 *            user's token from registration service
	 * @param compound
	 *            list of compounds to send to registration
	 * @return Registration Job ID
	 * @throws RegistrationRuntimeException
	 */
	public long submitForRegistration(String tokenHash, List<Compound> compound)
			throws RegistrationRuntimeException;

	/**
	 * Retrieve registration job status
	 * 
	 * @param tokenHash
	 *            user's token from registration service
	 * @param jobId
	 *            Registration Job ID
	 * @return Job status. Can be PASSED or FAILED or something other (in this
	 *         case, Indigo ELN will wait for PASSED or FAILED)
	 * @throws RegistrationRuntimeException
	 */
	public String getRegisterJobStatus(String tokenHash, long jobId)
			throws RegistrationRuntimeException;

	/**
	 * Retrieve compounds from job with given ID
	 * 
	 * @param tokenHash
	 *            user's token from registration service
	 * @param jobId
	 *            Registration Job ID
	 * @return Compounds from job with given ID
	 * @throws RegistrationRuntimeException
	 */
	public List<Compound> getRegisteredCompounds(String tokenHash, long jobId)
			throws RegistrationRuntimeException;
}
