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
package com.chemistry.enotebook.vcr;

import com.chemistry.enotebook.vcr.auxiliary.CompoundAttribute;
import com.chemistry.enotebook.vcr.auxiliary.StatusInfo;

/**
 * Interface for external Virtual Compound Registration service
 */
public interface VCRService {

	/**
	 * Register virtual compound
	 * 
	 * @param molfile
	 *            structure
	 * @param isDryrun
	 *            true if compound is dry
	 * @param loaderId
	 *            username
	 * @return Registration status information
	 */
	StatusInfo registerCompound(String molfile, boolean isDryrun,
			String loaderId);

	/**
	 * Register multiple virtual compounds using SD File
	 * 
	 * @param sdfile
	 *            SD File with structures
	 * @param isDryrun
	 *            true if compounds is dry
	 * @param loaderId
	 *            username
	 * @return Registration result for each compound
	 */
	StatusInfo[] registerCompounds(String sdfile, boolean isDryrun,
			String loaderId);

	/**
	 * Register virtual compound asynchronous
	 * 
	 * @param sdfile
	 *            structure
	 * @param isDryrun
	 *            true if compound is dry
	 * @param loaderId
	 *            username
	 * @return Job ID
	 */
	String registerBatchAsync(String sdfile, boolean isDryrun, String loaderId);

	/**
	 * Register multiple virtual compounds asynchronous
	 * 
	 * @param molfiles
	 *            structures
	 * @param isDryrun
	 *            true if compounds is dry
	 * @param loaderId
	 *            username
	 * @return Job ID
	 */
	String registerCompoundsAsync(String[] molfiles, boolean isDryrun,
			String loaderId);

	/**
	 * Check if registration complete for asynchronous registration
	 * 
	 * @param jobId
	 * @return true if registration is complete, false if not
	 */
	boolean isRegistrationComplete(String jobId);

	/**
	 * Retrieve registration results for given Job ID
	 * 
	 * @param jobId
	 *            Job ID
	 * @return Registration results
	 */
	StatusInfo[] getRegistrationResult(String jobId);

	/**
	 * Update information in service
	 * 
	 * @param attributes
	 *            compound attributes to update
	 * @return Update status
	 */
	StatusInfo updateRegistrationInfo(CompoundAttribute[] attributes);

	/**
	 * Check if user can register virtual compounds
	 * 
	 * @param ntName
	 *            username
	 * @return true if user can register virtual compounds, false if not
	 */
	boolean isRegisteredUser(String ntName);

	/**
	 * Check if given project code is valid for registration
	 * 
	 * @param projectCode
	 *            project code
	 * @return true if project code is valid, false if not
	 */
	boolean isValidProject(String projectCode);
}
