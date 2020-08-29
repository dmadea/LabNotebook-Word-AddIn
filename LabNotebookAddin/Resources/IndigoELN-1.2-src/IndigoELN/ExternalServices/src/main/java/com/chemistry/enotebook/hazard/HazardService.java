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
package com.chemistry.enotebook.hazard;

import com.chemistry.enotebook.hazard.exceptions.HazardException;

/**
 * Interface for Hazard info service
 */
public interface HazardService {

	/**
	 * Get hazard information with given id
	 * 
	 * @param id
	 *            information ID
	 * @param idtype
	 *            type of information ID
	 * @param lang
	 *            language code
	 * @return hazard information
	 * @throws HazardException
	 */
	public String getHazardInfo(String id, String idtype, String lang)
			throws HazardException;

	/**
	 * Check service health
	 * 
	 * @return true if service available, false if service unavailable
	 * @throws HazardException
	 */
	public boolean getHazardInfoHealth() throws HazardException;
}
