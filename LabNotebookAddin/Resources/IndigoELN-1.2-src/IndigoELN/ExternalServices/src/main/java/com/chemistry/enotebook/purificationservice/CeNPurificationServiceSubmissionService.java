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
package com.chemistry.enotebook.purificationservice;

import com.chemistry.enotebook.purificationservice.classes.PurificationServicePlateInfoExternal;
import com.chemistry.enotebook.purificationservice.classes.PurificationServiceTubeInfoExternal;
import com.chemistry.enotebook.purificationservice.exceptions.CeNtoPurificationServiceSubmissionException;

import java.util.List;

/**
 * Interface for external Purification Service
 */
public interface CeNPurificationServiceSubmissionService {

	/**
	 * Submit plates to purification
	 * 
	 * @param submitterNTID
	 *            username
	 * @param sitecode
	 *            site code
	 * @param plates
	 *            plates to purify
	 * @return true if submit successful, false if not
	 * @throws CeNtoPurificationServiceSubmissionException
	 */
	public boolean submitPlatesToPurificationServiceForPurification(
			String submitterNTID, String sitecode,
			PurificationServicePlateInfoExternal[] plates)
			throws CeNtoPurificationServiceSubmissionException;

	/**
	 * Submit tubes to purification
	 * 
	 * @param submitterNTID
	 *            username
	 * @param sitecode
	 *            site code
	 * @param tubes
	 *            tubes to purify
	 * @return true if submit successful, false if not
	 * @throws CeNtoPurificationServiceSubmissionException
	 */
	public boolean submitTubesToPurificationServiceForPurification(
			String submitterNTID, String sitecode,
			PurificationServiceTubeInfoExternal[] tubes)
			throws CeNtoPurificationServiceSubmissionException;

	/**
	 * Retrieve purification Archive plates
	 * 
	 * @return Archive plates
	 * @throws CeNtoPurificationServiceSubmissionException
	 */
	public List<String> getPurificationArchivePlateChoice()
			throws CeNtoPurificationServiceSubmissionException;

	/**
	 * Retrieve purification Labs for given site code
	 * 
	 * @param siteCode
	 *            site code
	 * @return Labs
	 * @throws CeNtoPurificationServiceSubmissionException
	 */
	public List<String> getLabsForPurification(String siteCode)
			throws CeNtoPurificationServiceSubmissionException;

	/**
	 * Retrieve purification Sample Workups
	 * 
	 * @return Sample Workups
	 * @throws CeNtoPurificationServiceSubmissionException
	 */
	public List<String> getPurificationSampleWorkup()
			throws CeNtoPurificationServiceSubmissionException;
}
