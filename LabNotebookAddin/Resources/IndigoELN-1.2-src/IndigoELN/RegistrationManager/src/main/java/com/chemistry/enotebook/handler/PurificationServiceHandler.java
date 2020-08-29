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

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServicePlateInfo;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceTubeInfo;
import com.chemistry.enotebook.purificationservice.CENPurificationSubmissionServiceFactory;
import com.chemistry.enotebook.purificationservice.CeNPurificationServiceSubmissionService;
import com.chemistry.enotebook.purificationservice.exceptions.CeNtoPurificationServiceSubmissionException;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.utils.RegistrationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurificationServiceHandler 
	extends AbstractHandler 
{
	private static final Log log = LogFactory.getLog(PurificationServiceHandler.class);

	/**
	 * @param siteCode
	 * @param plates
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Map<String, JobModel> submitPlatesForPurification(String userID, String siteCode, ProductPlate[] plates, SessionIdentifier session) 
		throws Exception 
	{
		log.debug("submitPlatesForPurification.enter");
		HashMap<String, JobModel> map = new HashMap<String, JobModel>();
		String status = "";
		String statusMessage = "";
		JobModel[] jobs = new JobModel[plates.length];
		
		try {
			CeNPurificationServiceSubmissionService purificationServiceService = CENPurificationSubmissionServiceFactory.getPurificationServiceSubmissionService();
			for (int i = 0; i < plates.length; i++) {
				try {
					jobs[i] = perpareAndSubmitJob(plates[i].getKey(), "", session, PURIFICATION_SERVICE_APP);

					ProductPlate[] plateArray = new ProductPlate[1];
					plateArray[0] = plates[i];
					PurificationServicePlateInfo[] purificationServicePlates = RegistrationUtils.convertToPurificationServicePlateInfoArray(plateArray);

					boolean result = purificationServiceService.submitPlatesToPurificationServiceForPurification(userID, siteCode, PurificationServicePlateInfo.convertToArrayPurificationServiceTubeInfoExternal(purificationServicePlates));

					if (result) {
						status = CeNConstants.REGINFO_SUBMISION_PASS;
					} else {
						status = CeNConstants.REGINFO_SUBMISION_FAIL;
					}

					log.debug("PurificationService Plate Submission result :" + result);
				} catch (CeNtoPurificationServiceSubmissionException cenpurificationServicee) {
					log.error("PurificationService Plate Submission failed for platekey :" + plates[i].getKey(), cenpurificationServicee);
					status = CeNConstants.REGINFO_SUBMISION_FAIL;
					if (ExceptionUtils.getRootCause(cenpurificationServicee) != null) {
						statusMessage = ExceptionUtils.getRootCause(cenpurificationServicee).getMessage();
					} else {
						statusMessage = cenpurificationServicee.getMessage();
					}
				} catch (Exception error) {
					log.error("PurificationService Plate Submission failed for platekey :" + plates[i].getKey(), error);
					status = CeNConstants.REGINFO_SUBMISION_FAIL;
					if (ExceptionUtils.getRootCause(error) != null) {
						statusMessage = ExceptionUtils.getRootCause(error).getMessage();
					} else {
						statusMessage = error.getMessage();
					}
					// throw error;
				}
				jobs[i].setPurificationServiceStatus(status);
				jobs[i].setPurificationServiceStatusMessage(statusMessage);
				log.debug("status Message is :" + statusMessage);
				map.put(plates[i].getKey(), jobs[i]);
			}
			try {
				getStorageEJBLocal().updateRegistrationJobs(jobs, null);
				log.error("updated PurificationService JOBS using Storage Service");
			} catch (Exception error) {
				log.error("Unable to update PurificationService JOBS using Storage Service", error);
			}
			log.debug("submitPlatesForPurification.exit");
		} catch (Exception e) {
			log.error("Error with PurificationService service..", e);
		}
		return map;
	}

	/**
	 * Submits PlateWell objects as if they were tubes to PurificationService for purification
	 * 
	 * @param siteCode
	 * @param tubes
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Map<String, JobModel> submitTubesForPurification(String userName, String siteCode, String pageKey, List<PlateWell<ProductBatchModel>> tubes) throws Exception {
		log.debug("Calling submitTubesForPurification.");
		Map<String, JobModel> map = new HashMap<String, JobModel>();
		String status = "";
		String statusMessage = "";
		
		JobModel job = new JobModel();
		try {
			CeNPurificationServiceSubmissionService purificationServiceService = CENPurificationSubmissionServiceFactory.getPurificationServiceSubmissionService();
			job = perpareAndSubmitJob("", pageKey, null, PURIFICATION_SERVICE_APP);

			PurificationServiceTubeInfo[] purificationServiceTubes = RegistrationUtils.convertToPurificationServiceTubeInfoArray(tubes);
			       	
			boolean result = purificationServiceService.submitTubesToPurificationServiceForPurification(userName, siteCode, PurificationServiceTubeInfo.convertToArrayPurificationServiceTubeInfoExternal(purificationServiceTubes));

			if (result) {
				status = CeNConstants.REGINFO_SUBMISION_PASS;
			} else {
				status = CeNConstants.REGINFO_SUBMISION_FAIL;
			}

			log.debug("PurificationService Tube Submission result :" + result);
		} catch (Exception error) {
			log.error("PurificationService Tube Submission failed:", error);
			status = CeNConstants.REGINFO_SUBMISION_FAIL;
			if (ExceptionUtils.getRootCause(error) != null) {
				statusMessage = ExceptionUtils.getRootCause(error).getMessage();
			} else {
				statusMessage = error.getMessage();
			}
			// throw error;
		}
		job.setPurificationServiceStatus(status);
		job.setPurificationServiceStatusMessage(statusMessage);
		log.debug("status Message is :" + statusMessage);
		map.put(pageKey, job);
		try {
			JobModel[] jobs = new JobModel[1];
			jobs[0] = job;
			getStorageEJBLocal().updateRegistrationJobs(jobs, null);
			log.error("updated PurificationService JOBS using Storage Service");
		} catch (Exception error) {
			log.error("Unable to update PurificationService JOBS using Storage Service", error);
		}
		log.debug("submitTubesForPurification.exit");
	
		return map;
	}

	/**
	 * @param siteCode
	 * @return
	 * @throws Exception
	 */
	public boolean isAvailable() 
		throws Exception 
	{
		boolean status = false;
		try {
			CENPurificationSubmissionServiceFactory.getPurificationServiceSubmissionService();
			status = true;
		} catch (Exception error) { /* Ignored */ }
		return status;
	}
	
	public List<String> getPurificationArchivePlateChoice() throws Exception {
		CeNPurificationServiceSubmissionService purificationServiceService = CENPurificationSubmissionServiceFactory.getPurificationServiceSubmissionService();
		return purificationServiceService.getPurificationArchivePlateChoice();
	}

	public List<String> getLabsForPurification(String siteCode) throws Exception {
		CeNPurificationServiceSubmissionService purificationServiceService = CENPurificationSubmissionServiceFactory.getPurificationServiceSubmissionService();
		return purificationServiceService.getLabsForPurification(siteCode);
	}

	public List<String> getPurificationSampleWorkup() throws Exception {
		CeNPurificationServiceSubmissionService purificationServiceService = CENPurificationSubmissionServiceFactory.getPurificationServiceSubmissionService();
		return purificationServiceService.getPurificationSampleWorkup();
	}
}
