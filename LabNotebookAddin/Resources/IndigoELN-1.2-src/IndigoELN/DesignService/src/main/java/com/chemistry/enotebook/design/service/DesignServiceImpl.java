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
package com.chemistry.enotebook.design.service;

import com.chemistry.enotebook.design.delegate.DesignServiceException;
import com.chemistry.enotebook.design.interfaces.DesignServiceRemote;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;

public class DesignServiceImpl implements DesignServiceRemote {

	@Override
	public String[] getSites() throws DesignServiceException {
		return new String[0];
	}

	@Override
	public boolean isDesignServiceAvailable() {
		return true;
	}

	@Override
	public NotebookPageModel getExperimentFromDesignService(String spid, boolean summaryPlanCompleteDetails) throws DesignServiceException {
		return new NotebookPageModel();
	}

	@Override
	public ReactionStepModel getReactionStepCompleteDetails(String pid) throws DesignServiceException {
		return new ReactionStepModel();
	}
}
