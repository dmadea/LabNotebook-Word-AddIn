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
package com.chemistry.enotebook.vcr.clean;

import com.chemistry.enotebook.vcr.VCRService;
import com.chemistry.enotebook.vcr.auxiliary.CompoundAttribute;
import com.chemistry.enotebook.vcr.auxiliary.StatusInfo;
import com.chemistry.enotebook.vcr.auxiliary.StatusInfoImpl;

public class VCRServiceCleanImpl implements VCRService {

	public StatusInfo[] getRegistrationResult(String jobId) {
		return createStatusInfoArray();
	}

	public boolean isRegisteredUser(String ntName) {
		return true;
	}

	public boolean isRegistrationComplete(String jobId) {
		return true;
	}

	public boolean isValidProject(String projectCode) {
		return true;
	}

	public String registerBatchAsync(String sdfile, boolean isDryrun, String loaderId) {
		return "";
	}

	public StatusInfo registerCompound(String molfile, boolean isDryrun, String loaderId) {
		return createStatusInfo("","");
	}

	public StatusInfo[] registerCompounds(String sdfile, boolean isDryrun, String loaderId) {
		return createStatusInfoArray();
	}

	public String registerCompoundsAsync(String[] molfiles, boolean isDryrun, String loaderId) {
		return "";
	}

	public StatusInfo updateRegistrationInfo(CompoundAttribute[] attributes) {
		return createStatusInfo("","");
	}

	private StatusInfo[] createStatusInfoArray() {
		StatusInfo info[] = new StatusInfo[0];
		return info;
	}
	
	private StatusInfo createStatusInfo(String compoundId, String molFormula) {
		StatusInfoImpl statusInfo = new StatusInfoImpl();
		return statusInfo;
	}
}
