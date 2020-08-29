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
package com.chemistry.enotebook.vnv;

import com.chemistry.enotebook.vnv.classes.IVnvResult;
import com.chemistry.enotebook.vnv.classes.UniquenessCheckVO;
import com.chemistry.enotebook.vnv.exceptions.VnvRuntimeException;

/**
 * Interface to external Validate and Verify service
 */
public interface VnvService {

	/**
	 * Check service health
	 * 
	 * @return true if service available, false if service unavailable
	 */
	public boolean checkHealth();

	/**
	 * Execute Validate and Verify process
	 * 
	 * @param molfile
	 *            structure to validate
	 * @param stereoisomerCode
	 *            stereoisomer code to validate with structure
	 * @return XML result of VnV process
	 * @throws VnvRuntimeException
	 */
	public abstract String executeVnV(String molfile, String stereoisomerCode)
			throws VnvRuntimeException;

	/**
	 * Validate structure and automatically assign stereoisomer code
	 * 
	 * @param molStructure
	 *            structure
	 * @return Result of validation process
	 * @throws VnvRuntimeException
	 */
	public IVnvResult validateStructureAssignStereoIsomerCode(
			String molStructure) throws VnvRuntimeException;

	/**
	 * Validate structure with assigned stereoisomer code
	 * 
	 * @param molStructure
	 *            stricture
	 * @param inputSic
	 *            stereoisomer code
	 * @return Result of validation process
	 * @throws VnvRuntimeException
	 */
	public IVnvResult validateStructureWithStereoIsomerCode(
			String molStructure, String inputSic) throws VnvRuntimeException;

	/**
	 * Check uniqueness of structure
	 * 
	 * @param molfile
	 *            structure
	 * @param stereoisomerCode
	 *            stereoisomer code assigned to structure
	 * @param includeLegacy
	 *            include old compounds
	 * @return Result of UC process
	 * @throws VnvRuntimeException
	 */
	public UniquenessCheckVO checkUniqueness(String molfile,
			String stereoisomerCode, boolean includeLegacy)
			throws VnvRuntimeException;
}
