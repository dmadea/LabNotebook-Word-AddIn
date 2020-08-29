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
package com.chemistry.enotebook.handler;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.JobModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractHandler {
	private static final Log log = LogFactory.getLog(AbstractHandler.class);
	
	public static final String COMPOUND_MANAGEMENT_APP = "CompoundManagement";
	public static final String PURIFICATION_SERVICE_APP = "PurificationService";
	public static final String COMPOUND_AGGREGATION_APP = "CompoundAggregation";
	
	/**
	 * @return StorageLocal
	 */
	protected StorageDelegate getStorageEJBLocal() {
		try {
			return new StorageDelegate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * When workflow is enabled this method is no more required.CompoundRegistration jobid is used to handle the rest of CompoundManagement,PurificationService,CompoundAggregation submissions
	 * for that record. One record is inserted in jobs table and all these submissions are handled. This is required only when user
	 * from UI selects CompoundManagement or PurificationService or CompoundAggregation submission only.
	 * 
	 * @param plate
	 * @param pageKey
	 * @param jobId
	 */
	public JobModel perpareAndSubmitJob(String plateKey, String pageKey, SessionIdentifier session, String app) {
		log.info("perpareAndSubmitJob(String, String, SessionIdentifier, String) start");
		try{
			JobModel model = getStorageEJBLocal().getCeNPlateJob(plateKey);
			if(model != null) {
				log.info("Found existing Job record for plate key.");		
				return model;
			}
			log.info("Could not find existing Job record for plate key.Proceeding to create new Job");
		} catch (Exception e){
			log.error("Error getting existing job: ", e);
		}
				
		JobModel plateJob = new JobModel();
		
		plateJob.setPlateKey(plateKey);
		plateJob.setPageKey(pageKey);
		
		if (session != null && session.getUserProfile() != null) {
			plateJob.setCallbackUrl(session.getUserProfile().getUrl());
		}
		
		if (app.equals(COMPOUND_MANAGEMENT_APP))	{
			plateJob.setCompoundManagementStatus(CeNConstants.REGINFO_SUBMITTING);
		} else if (app.equals(PURIFICATION_SERVICE_APP)) {
			plateJob.setPurificationServiceStatus(CeNConstants.REGINFO_SUBMITTING);
		} else if (app.equals(COMPOUND_AGGREGATION_APP)) {
			plateJob.setCompoundAggregationStatus(CeNConstants.REGINFO_SUBMITTING);
		}
		
		try {
			getStorageEJBLocal().insertRegistrationJobs(new JobModel[] { plateJob } );
		} catch (Exception e) {
			log.error("Error inserting registration job: ", e);
		}
		
		log.info("perpareAndSubmitJob(String, String, SessionIdentifier, String) end");
		return plateJob;
	}
	
	public String createAndSubmitNewJob(String batchKeysString, String plateKeysString, String workflowsString, NotebookPageModel pageModel, SessionIdentifier sessionID) throws StorageInitException {
		JobModel model = new JobModel();
		
//		model.setCallbackUrl(sessionID.getUserProfile().getUrl());
		model.setBatchKeysString(batchKeysString);
		model.setPlateKeysString(plateKeysString);
		model.setWorkflowsString(workflowsString);
		model.setPageKey(pageModel.getKey());
		model.getUserMessage().setNotebook(pageModel.getNbRef().getNbNumber());
		model.getUserMessage().setPage(pageModel.getNbRef().getNbPage());
		model.getUserMessage().setVersion(pageModel.getVersion());
		model.setJobStatus(CeNConstants.JOB_OPEN);
		
		try {
			return getStorageEJBLocal().insertRegistrationJobs(new JobModel[] { model })[0];
		} catch (StorageException e) {
			throw new StorageInitException("", e);
		}
	}
}
