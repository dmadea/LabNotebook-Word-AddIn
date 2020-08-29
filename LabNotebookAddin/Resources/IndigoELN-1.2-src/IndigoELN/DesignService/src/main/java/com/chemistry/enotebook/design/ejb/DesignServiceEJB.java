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
package com.chemistry.enotebook.design.ejb;

import com.chemistry.enotebook.design.delegate.DesignServiceException;
import com.chemistry.enotebook.design.interfaces.DesignServiceRemote;
import com.chemistry.enotebook.design.service.DesignServiceImpl;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.util.Stopwatch;

public class DesignServiceEJB implements DesignServiceRemote {

	@Override
	public String[] getSites() throws DesignServiceException {
		DesignServiceImpl designService = new DesignServiceImpl();
		return designService.getSites();
	}

	@Override
	public boolean isDesignServiceAvailable() {
		try {
			DesignServiceImpl designService = new DesignServiceImpl();
			return designService.isDesignServiceAvailable();
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public NotebookPageModel getExperimentFromDesignService(String spid, boolean summaryPlanCompleteDetails) throws DesignServiceException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("DesignServiceEJB.getExperimentFromDesignService() - " + spid);

		DesignServiceImpl designService = new DesignServiceImpl();
		NotebookPageModel pageModel = designService.getExperimentFromDesignService(spid, summaryPlanCompleteDetails);
				
		stopwatch.stop();
		return pageModel;

	}

	@Override
	public ReactionStepModel getReactionStepCompleteDetails(String pid) throws DesignServiceException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("DesignServiceEJB.getReactionStepCompleteDetails() - " + pid);

		DesignServiceImpl designService = new DesignServiceImpl();
		ReactionStepModel stepModel = designService.getReactionStepCompleteDetails(pid);
		stopwatch.stop();
		return stepModel;
	}
}
