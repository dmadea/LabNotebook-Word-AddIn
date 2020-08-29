package com.chemistry.enotebook.design.delegate;

import com.chemistry.enotebook.design.interfaces.DesignServiceRemote;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

public class DesignServiceDelegate implements DesignServiceRemote {

	private DesignServiceRemote service;
	
	public DesignServiceDelegate() {
		service = ServiceLocator.getInstance().locateService("DesignService", DesignServiceRemote.class);
	}
	
	@Override
	public String[] getSites() throws DesignServiceException {
		return service.getSites();
	}

	@Override
	public boolean isDesignServiceAvailable() {
		return service.isDesignServiceAvailable();
	}

	@Override
	public NotebookPageModel getExperimentFromDesignService(String spid, boolean summaryPlanCompleteDetails) throws DesignServiceException {
		return service.getExperimentFromDesignService(spid, summaryPlanCompleteDetails);
	}

	@Override
	public ReactionStepModel getReactionStepCompleteDetails(String pid) throws DesignServiceException {
		return service.getReactionStepCompleteDetails(pid);
	}
}
