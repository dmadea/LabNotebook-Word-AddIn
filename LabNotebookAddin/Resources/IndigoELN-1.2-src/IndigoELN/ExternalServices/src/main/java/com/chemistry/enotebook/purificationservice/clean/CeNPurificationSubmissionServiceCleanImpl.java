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
package com.chemistry.enotebook.purificationservice.clean;

import com.chemistry.enotebook.purificationservice.CeNPurificationServiceSubmissionService;
import com.chemistry.enotebook.purificationservice.classes.PurificationServicePlateInfoExternal;
import com.chemistry.enotebook.purificationservice.classes.PurificationServiceTubeInfoExternal;
import com.chemistry.enotebook.purificationservice.exceptions.CeNtoPurificationServiceSubmissionException;

import java.util.ArrayList;
import java.util.List;

public class CeNPurificationSubmissionServiceCleanImpl implements CeNPurificationServiceSubmissionService {
	
	public boolean submitPlatesToPurificationServiceForPurification(String submitterNTID, String sitecode, PurificationServicePlateInfoExternal[] plates)
			throws CeNtoPurificationServiceSubmissionException {
		return true;
	}
	
	public boolean submitTubesToPurificationServiceForPurification(String submitterNTID, String sitecode, PurificationServiceTubeInfoExternal[] tubes)
			throws CeNtoPurificationServiceSubmissionException {
		return true;
	}

	public List<String> getLabsForPurification(String siteCode)
			throws CeNtoPurificationServiceSubmissionException {
		List<String> list = new ArrayList<String>();
		return list;
	}

	public List<String> getPurificationArchivePlateChoice()
			throws CeNtoPurificationServiceSubmissionException {
		List<String> list = new ArrayList<String>();
		return list;
	}

	public List<String> getPurificationSampleWorkup()
			throws CeNtoPurificationServiceSubmissionException {
		List<String> list = new ArrayList<String>();
		return list;
	}
	
}
