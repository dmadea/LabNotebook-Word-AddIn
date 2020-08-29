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

import com.chemistry.enotebook.compoundaggregation.CompoundAggregationServiceFactory;
import com.chemistry.enotebook.compoundaggregation.classes.CompoundAggregationBatchInfo;
import com.chemistry.enotebook.compoundaggregation.classes.CompoundAggregationResult;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.SDFileGeneratorUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundAggregationHandler extends AbstractHandler 
{
	private static final Log log = LogFactory.getLog(CompoundAggregationHandler.class);

	public CompoundAggregationHandler(String siteCode) throws Exception {
	}

	/**
	 *Looks thru the product plates and extracts the batches and calls
	 * 
	 * @param batches
	 *            list of Product Batches
	 * @param pageHeader
	 *            contains projectCode and siteCode info corresponding to the User
	 */
	public Map<String, JobModel> submitPlatesToCompoundAggregation(String userID, ProductPlate[] pPlates, NotebookPageHeaderModel pageHeader) 
	throws Exception 
	{
		log.debug("submitPlatesToCompoundAggregation.enter");
		Map<String, JobModel> resultMap = new HashMap<String, JobModel>();
		ProductPlate pPlate = null;
		JobModel[] jobs = new JobModel[pPlates.length];
		String status = "";
		String statusMessage = "";
		for (int i = 0; i < pPlates.length; i++) {
			pPlate = pPlates[i];
			//jobs[i] = perpareAndSubmitJob(pPlate.getKey(), "", this.sessionObj, COMPOUND_AGGREGATION_APP);
			jobs[i] = perpareAndSubmitJob(pPlate.getKey(), "", null, COMPOUND_AGGREGATION_APP);
			Map<String, JobModel> result = submitBatchesToCompoundAggregation(userID, pPlate.getAllBatchesInThePlate(), pageHeader);
			log.debug("submission to CompoundAggregation compelte...updating the status for plate:" + pPlate.getKey());
			if (result != null) {
				status = CeNConstants.REGINFO_SUBMISION_PASS;
				for (JobModel model : result.values()) {
					if (StringUtils.equals(model.getCompoundAggregationStatus(), CeNConstants.REGINFO_SUBMISION_FAIL)) {
						status = CeNConstants.REGINFO_SUBMISION_FAIL;
						statusMessage = model.getCompoundAggregationStatusMessage();
						break;
					}
				}
				log.debug("Updated the status from CompoundAggregation as: " + statusMessage);
			} else {
				log.debug("Status from CompoundAggregation is null");
			}
			jobs[i].setCompoundAggregationStatus(status);
			jobs[i].setCompoundAggregationStatusMessage(statusMessage);
			resultMap.put(pPlate.getKey(), jobs[i]);
		}

		try {
			getStorageEJBLocal().updateRegistrationJobs(jobs, null);
			log.error("updated CompoundAggregation JOBS using Storage Service");
		} catch (Exception error) {
			log.error("Unable to insert CompoundAggregation JOBS using Storage Service", error);
		}
		log.debug("submitPlatesToCompoundAggregation.exit");
		return resultMap;
	}

	/**
	 *Looks thru the product batches and creates array of RequestItem objects and performs bulk submit to CompoundAggregation.
	 * 
	 * @param batches
	 *            list of Product Batches
	 * @param pageHeader
	 *            contains projectCode and siteCode info corresponding to the User
	 */
	public Map<String, JobModel> submitBatchesToCompoundAggregation(String userID, ProductBatchModel[] pBatches, NotebookPageHeaderModel pageHeader) throws Exception {
		Map<String, JobModel> resultMap = new HashMap<String, JobModel>();

		try {
			String compoundManagementEmployeeID = new SDFileGeneratorUtil().getOwnerEmpId(userID);

			CompoundAggregationBatchInfo[] batchInfos = new CompoundAggregationBatchInfo[pBatches.length];
			for (int i = 0; i < pBatches.length; i++) {
				batchInfos[i] = new CompoundAggregationBatchInfo(pBatches[i].getScreenPanelIds(), pBatches[i].getRegInfo().getBatchTrackingId());
			}
			CompoundAggregationResult result = CompoundAggregationServiceFactory.getService().submitBatchesToCompoundAggregation(batchInfos, compoundManagementEmployeeID, pageHeader.getSiteCode(), pageHeader.getProjectCode());

			String status = "";
			String errorMessage = "";

			if (result.getStatus()) {
				status = CeNConstants.REGINFO_SUBMISION_PASS;
			} else {
				status = CeNConstants.REGINFO_SUBMISION_FAIL;
			}
			errorMessage = result.getMessage();


			// updating the RegBatchInfo object with status

			List<BatchRegInfoModel> batchJobs = new ArrayList<BatchRegInfoModel>();

			for (ProductBatchModel pBatch : pBatches) {
				JobModel model = new JobModel();

				BatchRegInfoModel batchJob = pBatch.getRegInfo();

				batchJob.setCompoundAggregationStatus(status);
				batchJob.setCompoundAggregationStatusMessage(errorMessage);

				batchJobs.add(batchJob);

				model.setCompoundAggregationStatus(status);
				model.setCompoundAggregationStatusMessage(errorMessage);

				resultMap.put(pBatch.getKey(), model);
			}

			log.debug("Status of CompoundAggregation submission: " + status);
			log.debug("Error Message from CompoundAggregation Submision: " + errorMessage);

			try {
				getStorageEJBLocal().updateBatchJobs(batchJobs.toArray(new BatchRegInfoModel[0]), null);
			} catch (Exception error) {
				log.error("Unable to insert JOBS using Storage Service", error);
			}
		} catch (Exception error) {
			log.error("Failed submitting batches to CompoundAggregation:", error);
			throw error;
		}

		return resultMap;
	}
}