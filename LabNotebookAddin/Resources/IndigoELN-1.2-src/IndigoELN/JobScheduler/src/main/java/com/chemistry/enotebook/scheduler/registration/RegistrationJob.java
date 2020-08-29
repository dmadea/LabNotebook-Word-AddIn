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
package com.chemistry.enotebook.scheduler.registration;

import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationJobStatus;
import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationStatus;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.session.security.HttpUserMessage;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.RegistrationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class RegistrationJob {

	private static final Logger log = Logger.getLogger(RegistrationJob.class);

	private StorageDelegate storageDelegate = null;
	private RegistrationManagerDelegate registrationManager = null;
	private RegistrationServiceDelegate registrationDelegate = null;
	
    @Scheduled(fixedRate = 1L * 60L * 1000L)
	public void execute() {
		log.debug("execute(JobExecutionContext) start");
	
		try {
			init();
		} catch (Exception e) {
			log.error("Couldn't init service delegates:", e);
			return;
		}
		
		List<JobModel> jobsList = null;
		
		try {
			jobsList  = storageDelegate.getAllRegistrationJobsAndUpdateStatus(CeNConstants.JOB_PROGRESS);
		} catch (Throwable e) {
			log.error("Error getting all registration jobs:", e);
			return;
		}
		
		if (jobsList != null) {
			log.debug("Jobs count: " + jobsList.size());
			
			for (final JobModel model : jobsList) {
				if (model != null) {
					Thread thread = new Thread(new Runnable() {
						public void run() {
							executeJob(model);
						}							
					}, "Executing registration job: " + model.getJobID());
					thread.start();
				}
			}
		}
		log.debug("execute(JobExecutionContext) end");
	}
	
	private void init() throws Exception {
		log.info("init() start");

		storageDelegate = new StorageDelegate();
		registrationManager = new RegistrationManagerDelegate();
		registrationDelegate = new RegistrationServiceDelegate();
		
		log.info("init() end");
	}
	
	private void executeJob(JobModel model) {
		log.info("executeJob(JobModel) start -- jobID = " + model.getJobID());
		
		NotebookPageModel pageModel = null;
		List<ProductBatchModel> batchesForWait = null;
		List<String> jobIDs = null;
		List<ProductBatchModel> allBatches = null;
		List<String> workflowList = null;	
		
		try {
			NotebookRef nbRef = new NotebookRef(model.getUserMessage().getNotebook(), model.getUserMessage().getPage());
			nbRef.setVersion(model.getUserMessage().getVersion());
			
			log.info("Notebook reference: " + nbRef.getNotebookRef());
			
			pageModel = getNotebookPageExperimentInfo(nbRef);
				
			log.info("Got PageModel for page " + pageModel.getNbRef().getNotebookRef());
			
			String username = pageModel.getUserName();
			
			List<ProductBatchModel> batches = getBatchModels(model.getBatchKeysString(), pageModel);			
			List<ProductPlate> plates = getPlateModels(model.getPlateKeysString(), pageModel);

			log.info("Batches: " + batches.toString());
			log.info("Plates:  " + plates.toString());
			
			List<PlateWell<ProductBatchModel>> wellsAsTubes = new Vector<PlateWell<ProductBatchModel>>(); // Wells from plates and non-plated batches - contain no well form plate having the tube
			allBatches = new Vector<ProductBatchModel>();	//This array contain as single batches as batches from plates (all existing batches)
				
			workflowList = getKeys(model.getWorkflowsString());

			// Get plate wells from plates and free batches - for register in CompoundManagement and PurificationService as tubes
			if (!batches.isEmpty()) {
				for (ProductBatchModel batchModel : batches) {
					if (batchModel != null && isBatchInSelectedList(model.getBatchKeysString(), batchModel)) {
						PlateWell<ProductBatchModel> batchWell = new PlateWell<ProductBatchModel>(batchModel);
						wellsAsTubes.add(batchWell);
					}
				}
			}
			
			allBatches.addAll(batches);
			
			//Also add plates witch has tubes inside some wells
			if (!plates.isEmpty()) {
				for (ProductPlate plate : plates) {
					if (plate != null) {
						PlateWell<ProductBatchModel>[] wells = plate.getWells();
						if (wells != null) {
							for (int i = 0; i < wells.length; ++i) {
								PlateWell<ProductBatchModel> well = wells[i];
								if (well != null && well.getBatch() instanceof ProductBatchModel) {
									ProductBatchModel batch = (ProductBatchModel) well.getBatch();
									// also fill allBatches list
									if (!allBatches.contains(batch)) {
										allBatches.add(batch);
									}
									if (batch.getRegInfo() != null && batch.getRegInfo().getSubmitContainerListSize() > 0){
										// add into list wells with tubes inside
										boolean hasBatch = false;
										for (PlateWell<ProductBatchModel> hasWell : wellsAsTubes) {
											if (hasWell.getBatch().equals(well.getBatch())) {
												hasBatch = true;
											}
										}
										if (!hasBatch) {
											wellsAsTubes.add(well);
										}
									}
								}
							}
						}
					}
				}
			}
																
			/* Asynchronous registration */
			if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
				log.info("CompoundRegistration registration start");					
					
				//This list contain batches for compoundBatch and Plate for CompoundRegistration registration
				List<ProductBatchModel> batchToReg = new Vector<ProductBatchModel>();
				//This list contain batches for CompoundRegistration status update 
				List<ProductBatchModel> batchToUpdate = new Vector<ProductBatchModel>();
				for (ProductBatchModel batch : allBatches) {
					if (batch != null) {
						BatchRegInfoModel regInfo = batch.getRegInfo();
						if (regInfo != null){
							//Here it is CompoundRegistration registration status
							String status = regInfo.getCompoundRegistrationStatus();
							int jobId = new Integer(regInfo.getJobId()).intValue();
							if (CeNConstants.REGINFO_NOT_SUBMITTED.equals(status)){
								//Insert new batches into CompoundRegistration 
								batchToReg.add(batch);
							} else if (CeNConstants.REGINFO_SUBMISION_FAIL.equals(status) || CeNConstants.REGINFO_SUBMISION_PENDING.equals(status)) {
								if (jobId > 0){
									//We have jobId - just update status form CompoundRegistration
									batchToUpdate.add(batch);
								} else {
									//We need CompoundRegistration registration
									batchToReg.add(batch);
								}
							}
						}
					}
				}
					
				setCompoundRegistrationPendingStatusForBatches(batchToReg);
				setCompoundRegistrationPendingStatusForBatches(batchToUpdate);
				
				batchesForWait = new Vector<ProductBatchModel>();
				jobIDs = new Vector<String>();
						
				batchesForWait.addAll(batchToReg);
				batchesForWait.addAll(batchToUpdate);
				
				// Set 'Waiting' status for batches
								
				for (ProductBatchModel batch : allBatches) {
					JobModel batchModel = new JobModel();
					
					batchModel.setCallbackUrl(model.getCallbackUrl());
					batchModel.setUserMessage(model.getClonedUserMessage());		
					batchModel.setPlateKey(batch.getKey());
					
					if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
						batchModel.setCompoundManagementStatus(CeNConstants.REGINFO_SUBMISION_WAITING);
						batchModel.setCompoundManagementStatusMessage("");
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT, CeNConstants.REGINFO_SUBMISION_WAITING, "");
						sendMessageToClient(batchModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
					}
					
					if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
						batchModel.setPurificationServiceStatus(CeNConstants.REGINFO_SUBMISION_WAITING);
						batchModel.setPurificationServiceStatusMessage("");
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_PURIFICATION_SERVICE, CeNConstants.REGINFO_SUBMISION_WAITING, "");
						sendMessageToClient(batchModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
					}
					
					if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
						batchModel.setCompoundAggregationStatus(CeNConstants.REGINFO_SUBMISION_WAITING);
						batchModel.setCompoundAggregationStatusMessage("");
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION, CeNConstants.REGINFO_SUBMISION_WAITING, "");
						sendMessageToClient(batchModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
					}
				}
				
				// Submit to CompoundRegistration
				
				if (!batchToReg.isEmpty()) {
					Map<String, String> batchesMap = submitBatchesForRegistration(batchToReg, username, pageModel);
					List<String> IDs = extractIDs(batchesMap);
					jobIDs.addAll(IDs);
				}				
			
				jobIDs = removeRepeatedItems(jobIDs);
				
				// Update 'Pending' statuses on client
				for (String jobId : jobIDs) {					
					JobModel compoundRegistrationModel = new JobModel();
					compoundRegistrationModel.setCallbackUrl(model.getCallbackUrl());
					compoundRegistrationModel.setUserMessage(model.getClonedUserMessage());					
					sendMessageToClient(compoundRegistrationModel, "", HttpUserMessage.BATCH, jobId);
				}
								
				String compoundRegistrationJobIDs = jobIDs.toString().replace('[', ' ').replace(']', ' ').trim();
				model.setCompoundRegistrationJobId(compoundRegistrationJobIDs);
				updateJobModel(model);
																		
				waitForCompoundRegistration(batchesForWait, getKeys(model.getCompoundRegistrationJobId()), model);
				
				log.info("CompoundRegistration registration end");
			}

			List<PlateWell<ProductBatchModel>> passCompoundRegistrationTubes = (List<PlateWell<ProductBatchModel>>) clearObjects(wellsAsTubes, CeNConstants.REGINFO_PASSED, BatchRegInfoModel.class.getMethod("getStatus", new  Class[0]));
			List<ProductBatchModel> passCompoundRegistrationBatches = (List<ProductBatchModel>) clearObjects(allBatches, CeNConstants.REGINFO_PASSED, BatchRegInfoModel.class.getMethod("getStatus", new  Class[0]));

			// Send 'Not Submitted' to not pass compoundRegistration wells -- CompoundManagement
			if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
				for (PlateWell<ProductBatchModel> well : wellsAsTubes) {
					if (!passCompoundRegistrationTubes.contains(well) && well != null && well.getBatch() != null) {
						ProductBatchModel batch = well.getBatch();
					
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT, CeNConstants.REGINFO_NOT_SUBMITTED, "");
					
						JobModel compoundManagementModel = new JobModel();
						compoundManagementModel.setCallbackUrl(model.getCallbackUrl());
						compoundManagementModel.setUserMessage(model.getClonedUserMessage());
						compoundManagementModel.setCompoundManagementStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						compoundManagementModel.setCompoundManagementStatusMessage("");
						compoundManagementModel.setPlateKey(batch.getKey());
				
						sendMessageToClient(compoundManagementModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
					}
				}
			}
			
			// Send 'Not submitted' to batches which not pass CompoundRegistration -- PurificationService
			if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
				for (PlateWell<ProductBatchModel> well : wellsAsTubes) {
					if (!passCompoundRegistrationTubes.contains(well) && well != null && well.getBatch() != null) {
						ProductBatchModel batch = well.getBatch();
					
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_PURIFICATION_SERVICE, CeNConstants.REGINFO_NOT_SUBMITTED, "");
					
						JobModel purificationServiceModel = new JobModel();
						purificationServiceModel.setCallbackUrl(model.getCallbackUrl());
						purificationServiceModel.setUserMessage(model.getClonedUserMessage());
						purificationServiceModel.setPurificationServiceStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						purificationServiceModel.setPurificationServiceStatusMessage("");
						purificationServiceModel.setPlateKey(batch.getKey());
				
						sendMessageToClient(purificationServiceModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
					}
				}
			}
			
			// Send 'NotSubmitted' to not pass compoundRegistration batches -- CompoundAggregation
			if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
				for (ProductBatchModel batch : allBatches) {
					if (!passCompoundRegistrationBatches.contains(batch) && batch != null) {					
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION, CeNConstants.REGINFO_NOT_SUBMITTED, "");
					
						JobModel compoundAggregationModel = new JobModel();
						compoundAggregationModel.setCallbackUrl(model.getCallbackUrl());
						compoundAggregationModel.setUserMessage(model.getClonedUserMessage());
						compoundAggregationModel.setCompoundAggregationStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						compoundAggregationModel.setCompoundAggregationStatusMessage("");
						compoundAggregationModel.setPlateKey(batch.getKey());
				
						sendMessageToClient(compoundAggregationModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
					}
				}
			}
			
			plates = clrCompoundRegistrationPlates(plates, CeNConstants.REGINFO_PASSED);

			// Set Project Tracking Code for plates if missed
			for (ProductPlate plate : plates) {
				if (plate != null && StringUtils.isEmpty(plate.getProjectTrackingCode())) {
					plate.setProjectTrackingCode(pageModel.getProjectCode());
				}
			}
				
			workflowList.remove(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION);
			
			/* Synchronous registration */
			if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
				log.info("CompoundManagement registration start");
				
				if (!passCompoundRegistrationTubes.isEmpty()) {
					// Set 'Pending' status
					for (PlateWell<ProductBatchModel> well : passCompoundRegistrationTubes) {
						if (well != null && well.getBatch() != null) {
							ProductBatchModel batch = well.getBatch();
							JobModel batchModel = new JobModel();
							batchModel.setCallbackUrl(model.getCallbackUrl());
							batchModel.setUserMessage(model.getClonedUserMessage());
							batchModel.setCompoundManagementStatus(CeNConstants.REGINFO_SUBMISION_PENDING);
							batchModel.setCompoundManagementStatusMessage("");
							batchModel.setPlateKey(batch.getKey());
							updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT, CeNConstants.REGINFO_SUBMISION_PENDING, "");
							sendMessageToClient(batchModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
						}
					}
					
					Map<String, String> result = submitTubesForCompoundManagementRegistration(passCompoundRegistrationTubes, pageModel);
					if (result != null) {
						for (PlateWell<ProductBatchModel> well : passCompoundRegistrationTubes) {							
							if (well != null && well.getBatch() instanceof ProductBatchModel) {
								String status = (String) result.get(well.getKey());	
								ProductBatchModel batch =  (ProductBatchModel) well.getBatch();
								BatchRegInfoModel regInfo = batch.getRegInfo();
								if (regInfo != null){
									log.info("CompoundManagement Tube status: " + status);
									
									regInfo.setCompoundManagementStatus(status);
									
									JobModel compoundManagementModel = new JobModel();
									compoundManagementModel.setCallbackUrl(model.getCallbackUrl());
									compoundManagementModel.setUserMessage(model.getClonedUserMessage());
									compoundManagementModel.setCompoundManagementStatus(status);
									compoundManagementModel.setCompoundManagementStatusMessage("");
									//compoundManagementModel.setPlateKey(well.getKey());
									compoundManagementModel.setPlateKey(batch.getKey());
									
									updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT, status, "");
									sendMessageToClient(compoundManagementModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
								}
							}	
						}		
					}
				}
					
				if (!plates.isEmpty()) {
					Map<String, JobModel> result = submitPlatesForCompoundManagementRegistration(plates, pageModel);
					if (result != null) {
						for (ProductPlate plate : plates) {
							if (plate != null) {
								JobModel plateModel = (JobModel) result.get(plate.getKey());
								if (plateModel != null) {
									log.info("CompoundManagement Plate status: " + plateModel.getCompoundManagementStatus());
									
									plate.setCompoundManagementRegistrationSubmissionStatus(plateModel.getCompoundManagementStatus());
									plate.setCompoundManagementRegistrationSubmissionMessage(plateModel.getCompoundManagementStatusMessage());
									
									JobModel compoundManagementModel = new JobModel();
									compoundManagementModel.setCallbackUrl(model.getCallbackUrl());
									compoundManagementModel.setUserMessage(model.getClonedUserMessage());
									compoundManagementModel.setCompoundManagementStatus(plateModel.getCompoundManagementStatus());
									compoundManagementModel.setCompoundManagementStatusMessage(plateModel.getCompoundManagementStatusMessage());
									compoundManagementModel.setPlateKey(plate.getKey());
									
									sendMessageToClient(compoundManagementModel, "", HttpUserMessage.PLATE, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
								}
							}
						}
					}
				}
				log.info("CompoundManagement registration end");
			}
			
			List<PlateWell<ProductBatchModel>> passCompoundManagementTubes = (List<PlateWell<ProductBatchModel>>) clearObjects(passCompoundRegistrationTubes, CeNConstants.REGINFO_SUBMISION_PASS, BatchRegInfoModel.class.getMethod("getCompoundManagementStatus", new  Class[0]));
			List<ProductPlate> passCompoundManagementPlates = (List<ProductPlate>) clearObjects(plates, CeNConstants.REGINFO_SUBMISION_PASS, ProductPlate.class.getMethod("getCompoundManagementRegistrationSubmissionStatus", new Class[0]));			
			
			// Send 'Not submitted' to batches which not pass CompoundManagement -- PurificationService
			if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
				for (PlateWell<ProductBatchModel> well : passCompoundRegistrationTubes) {
					if (!passCompoundManagementTubes.contains(well) && well != null && well.getBatch() != null) {
						ProductBatchModel batch = well.getBatch();
					
						updateBatchRegInfo(batch, CeNConstants.WORKFLOW_PURIFICATION_SERVICE, CeNConstants.REGINFO_NOT_SUBMITTED, "");
					
						JobModel purificationServiceModel = new JobModel();
						purificationServiceModel.setCallbackUrl(model.getCallbackUrl());
						purificationServiceModel.setUserMessage(model.getClonedUserMessage());
						purificationServiceModel.setPurificationServiceStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						purificationServiceModel.setPurificationServiceStatusMessage("");
						purificationServiceModel.setPlateKey(batch.getKey());
				
						sendMessageToClient(purificationServiceModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
					}
				}
			}
			
			updateTubesRegInfoWithTubeGuid(passCompoundManagementTubes, pageModel.getSiteCode());
			updateTubesPurificationServiceParametersWithRxnScale(passCompoundManagementTubes, pageModel);
			
			workflowList.remove(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
			
			/* Synchronous registration */
			if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
				log.info("PurificationService registration start");
				if (!passCompoundManagementTubes.isEmpty()) {
					
					// Set 'Pending' status
					for (PlateWell<ProductBatchModel> well : passCompoundManagementTubes) {
						if (well != null && well.getBatch() != null) {
							ProductBatchModel batch = well.getBatch();
							JobModel batchModel = new JobModel();
							batchModel.setCallbackUrl(model.getCallbackUrl());
							batchModel.setUserMessage(model.getClonedUserMessage());
							batchModel.setPurificationServiceStatus(CeNConstants.REGINFO_SUBMISION_PENDING);
							batchModel.setPurificationServiceStatusMessage("");
							batchModel.setPlateKey(batch.getKey());	
							updateBatchRegInfo(batch, CeNConstants.WORKFLOW_PURIFICATION_SERVICE, CeNConstants.REGINFO_SUBMISION_PENDING, "");
							sendMessageToClient(batchModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
						}
					}
					
					Map<String, JobModel> result = submitTubesForPurification(passCompoundManagementTubes, username, pageModel);
					if (result != null) {
						for (PlateWell<ProductBatchModel> well : passCompoundManagementTubes) {
							if (well != null) {
								JobModel wellModel = result.get(pageModel.getKey());
								if (wellModel != null) {
									log.info("PurificationService Tube status: " + wellModel.getPurificationServiceStatus());
									
									BatchModel batch = well.getBatch();
									if (batch instanceof ProductBatchModel) {
										ProductBatchModel wellBatch = (ProductBatchModel) batch;
										BatchRegInfoModel regInfo = wellBatch.getRegInfo();
										
										if (regInfo != null) {
											regInfo.setPurificationServiceStatus(wellModel.getPurificationServiceStatus());
											regInfo.setPurificationServiceStatusMessage(wellModel.getPurificationServiceStatusMessage());
										}
									
										JobModel purificationServiceModel = new JobModel();
										purificationServiceModel.setCallbackUrl(model.getCallbackUrl());
										purificationServiceModel.setUserMessage(model.getClonedUserMessage());
										purificationServiceModel.setPurificationServiceStatus(wellModel.getPurificationServiceStatus());
										purificationServiceModel.setPurificationServiceStatusMessage(wellModel.getPurificationServiceStatusMessage());
										purificationServiceModel.setPlateKey(wellBatch.getKey());
									
										updateBatchRegInfo(wellBatch, CeNConstants.WORKFLOW_PURIFICATION_SERVICE, wellModel.getPurificationServiceStatus(), wellModel.getPurificationServiceStatusMessage());
										sendMessageToClient(purificationServiceModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
									}
								}
							}
						}
					}
				}
				
				if (!passCompoundManagementPlates.isEmpty()) {
					Map<String, JobModel> result = submitPlatesForPurification(passCompoundManagementPlates, username, pageModel);
					if (result != null) {
						for (ProductPlate plate : passCompoundManagementPlates) {
							if (plate != null) {
								JobModel plateModel = (JobModel) result.get(plate.getKey());
								if (plateModel != null) {
									log.info("PurificationService Plate status: " + plateModel.getPurificationServiceStatus());
									
									plate.setPurificationSubmissionStatus(plateModel.getPurificationServiceStatus());
									plate.setPurificationSubmissionMessage(plateModel.getPurificationServiceStatusMessage());
									
									JobModel purificationServiceModel = new JobModel();
									purificationServiceModel.setCallbackUrl(model.getCallbackUrl());
									purificationServiceModel.setUserMessage(model.getClonedUserMessage());
									purificationServiceModel.setPurificationServiceStatus(plateModel.getPurificationServiceStatus());
									purificationServiceModel.setPurificationServiceStatusMessage(plateModel.getPurificationServiceStatusMessage());
									purificationServiceModel.setPlateKey(plate.getKey());
									
									sendMessageToClient(purificationServiceModel, "", HttpUserMessage.PLATE, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
								}
							}
						}
					}	
				}
				log.info("PurificationService registration end");
			}
		
			workflowList.remove(CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
			
			/* Synchronous registration */
			if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
				log.info("CompoundAggregation registration start");
				if (!passCompoundRegistrationBatches.isEmpty()) {
					
					// Set 'Pending' status
					for (ProductBatchModel batch : passCompoundRegistrationBatches) {
						if (batch != null) {
							JobModel batchModel = new JobModel();
							batchModel.setCallbackUrl(model.getCallbackUrl());
							batchModel.setUserMessage(model.getClonedUserMessage());
							batchModel.setCompoundAggregationStatus(CeNConstants.REGINFO_SUBMISION_PENDING);
							batchModel.setCompoundAggregationStatusMessage("");
							batchModel.setPlateKey(batch.getKey());			
							updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION, CeNConstants.REGINFO_SUBMISION_PENDING, "");
							sendMessageToClient(batchModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
						}
					}
					
					Map<String, JobModel> result = submitBatchesToCompoundAggregation(passCompoundRegistrationBatches, username, pageModel);
					if (result != null) {
						for (ProductBatchModel batch : passCompoundRegistrationBatches) {
							if (batch != null && batch.getRegInfo() != null) {
								JobModel statusModel = result.get(batch.getKey());
								log.info("CompoundAggregation Batch status: " + statusModel.getCompoundAggregationStatus());
								
								batch.getRegInfo().setCompoundAggregationStatus(statusModel.getCompoundAggregationStatus());
								batch.getRegInfo().setCompoundAggregationStatusMessage(statusModel.getCompoundAggregationStatusMessage());
								
								JobModel compoundAggregationModel = new JobModel();
								compoundAggregationModel.setCallbackUrl(model.getCallbackUrl());
								compoundAggregationModel.setUserMessage(model.getClonedUserMessage());
								compoundAggregationModel.setCompoundAggregationStatus(statusModel.getCompoundAggregationStatus());
								compoundAggregationModel.setCompoundAggregationStatusMessage(statusModel.getCompoundAggregationStatusMessage());
								compoundAggregationModel.setPlateKey(batch.getKey());
								
								updateBatchRegInfo(batch, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION, statusModel.getCompoundAggregationStatus(), statusModel.getCompoundAggregationStatusMessage());
								sendMessageToClient(compoundAggregationModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
							}
						}
					}
				}
				if (!plates.isEmpty()) {
					Map<String, JobModel> result = submitPlatesToCompoundAggregation(plates, username, pageModel);
					if (result != null) {
						for (ProductPlate plate : plates) {
							if (plate != null) {
								JobModel plateModel = (JobModel) result.get(plate.getKey());
								if (plateModel != null) {
									log.info("CompoundAggregation Plate status: " + plateModel.getCompoundAggregationStatus());
									
									plate.setScreenPanelsSubmissionStatus(plateModel.getCompoundAggregationStatus());
									plate.setScreenPanelSubmissionMessage(plateModel.getCompoundAggregationStatusMessage());
									
									JobModel compoundAggregationModel = new JobModel();
									compoundAggregationModel.setCallbackUrl(model.getCallbackUrl());
									compoundAggregationModel.setUserMessage(model.getClonedUserMessage());
									compoundAggregationModel.setCompoundAggregationStatus(plateModel.getCompoundAggregationStatus());
									compoundAggregationModel.setCompoundAggregationStatusMessage(plateModel.getCompoundAggregationStatusMessage());
									compoundAggregationModel.setPlateKey(plate.getKey());
									
									sendMessageToClient(compoundAggregationModel, "", HttpUserMessage.PLATE, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
								}
							}
						}
					}
				}
				log.info("CompoundAggregation registration end");
			}
		} catch (Throwable e) {
			log.error("Error sending plates and batches to registration:", e);
			handleException(jobIDs, allBatches, workflowList, model, e);
		} finally {
			try {				
				// Update batch statuses in DB
				List<BatchRegInfoModel> regInfos = new Vector<BatchRegInfoModel>();
				if (allBatches != null) {
					for (ProductBatchModel batch : allBatches) {
						if (batch != null && batch.getRegInfo() != null) {
							regInfos.add(batch.getRegInfo());
						}
					}
					updateBatchJobs(regInfos);				
				}
				// Update job model in DB				
				updateJobModelStatus(model, CeNConstants.JOB_FINISHED);
			} catch (Exception e) {
				log.error("updateJobModelStatus() exception: ", e);
			}
		}
		log.info("executeJob(JobModel) end");
	}

	private void updateBatchRegInfo(ProductBatchModel batch, String workflow, String status, String statusMessage) throws Exception {
		if (batch != null && batch.getRegInfo() != null) {
			List<BatchRegInfoModel> regInfoList = new Vector<BatchRegInfoModel>();
			BatchRegInfoModel regInfo = batch.getRegInfo();
			
			if (StringUtils.equals(workflow, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
				regInfo.setCompoundManagementStatus(status);
				regInfo.setCompoundManagementStatusMessage(statusMessage);
			}
			
			if (StringUtils.equals(workflow, CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
				regInfo.setPurificationServiceStatus(status);
				regInfo.setPurificationServiceStatusMessage(statusMessage);
			}
			
			if (StringUtils.equals(workflow, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
				regInfo.setCompoundAggregationStatus(status);
				regInfo.setCompoundAggregationStatusMessage(statusMessage);
			}
			
			regInfoList.add(regInfo);
			updateBatchJobs(regInfoList);
		}
	}

	private void updateTubesPurificationServiceParametersWithRxnScale(List<PlateWell<ProductBatchModel>> wellsAsTubes, NotebookPageModel pageModel) {
		for (PlateWell<ProductBatchModel> tube : wellsAsTubes) {
			if (tube != null && tube.getBatch() != null && tube.getBatch().getRegInfo() != null) {
				BatchRegInfoModel regInfo = tube.getBatch().getRegInfo();
				List<BatchSubmissionContainerInfoModel> contList = regInfo.getSubmitContainerList();
				if (contList != null) {
					for (BatchSubmissionContainerInfoModel infoModel : contList) {
						if (infoModel != null) {
							PurificationServiceSubmisionParameters parameter = infoModel.getPurificationServiceParameters();
							if (parameter != null) {
								AmountModel reactionScale = null;
								
								if(pageModel != null && pageModel.getPageHeader() != null && pageModel.isParallelExperiment()) {
									reactionScale = pageModel.getPageHeader().getScale();
								} else {
									List<StoicModelInterface> limitingReagents = pageModel.getLimitingReagentsInAllSteps();
									
									if (limitingReagents != null && !limitingReagents.isEmpty() && limitingReagents.get(0) != null) {
										StoicModelInterface limitingReagent = limitingReagents.get(0);
										reactionScale = limitingReagent.getStoicMoleAmount();
									} else {
										reactionScale = new AmountModel(UnitType.MOLES, -1);
									}
								}
								
								parameter.setRecationScaleAmount(reactionScale);
							}
						}
					}
				}
			}
		}
	}

	private void updateTubesRegInfoWithTubeGuid(List<PlateWell<ProductBatchModel>> wellsAsTubes, String siteCode) {
		if (wellsAsTubes != null) {
			for (PlateWell<ProductBatchModel> well : wellsAsTubes) {
				if (well != null && well.getBatch() != null && well.getBatch().getRegInfo() != null) {
					BatchRegInfoModel model = well.getBatch().getRegInfo();
					if (model.getSubmitContainerList() != null) {
						for (BatchSubmissionContainerInfoModel infoModel : model.getSubmitContainerList()) {
							if (infoModel != null && StringUtils.isNotBlank(infoModel.getBarCode())) {
								try {
									String tubeGuid = registrationManager.getCompoundManagementTubeGuidByBarCodeAndSiteCode(infoModel.getBarCode(), siteCode);
									infoModel.setTubeGuid(tubeGuid);
								} catch (Exception e) {
									log.error("Error getting Tube GUID: ", e);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isBatchInSelectedList(String batchKeysString, ProductBatchModel batch) {
		if (batchKeysString != null && batch != null) {
			List<String> keys = getKeys(batchKeysString);
			for (String key : keys) {
				if (StringUtils.equals(key, batch.getKey())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes repeated items from list. Compares items by equals() method.
	 * @param items Keys list contains repeated items
	 * @return New list with unique items
	 */
	private List<String> removeRepeatedItems(List<String> items) {
		log.info("removeRepeatedItems(List) start");
		List<String> newItems = new Vector<String>();
		if (items != null) {
			for (String item : items) {
				if (!newItems.contains(item)) {
					newItems.add(item);
				}
			}
		}
		log.info("removeRepeatedItems(List) end");
		return newItems;
	}

	/**
	 *	Remove plates with mismatches CompoundRegistration status from array
	 */
	private List<ProductPlate> clrCompoundRegistrationPlates(List<ProductPlate> plates, String validStatus) {
		List<ProductPlate> platesArray = (plates == null ? new Vector<ProductPlate>() : new Vector<ProductPlate>(plates));
		Iterator<ProductPlate> pIt = platesArray.iterator();
		while (pIt.hasNext()) {
			ProductPlate plate = pIt.next();
			ProductBatchModel[] platebatches = plate.getAllBatchesInThePlate();
			for (int i = 0; platebatches != null && i < platebatches.length; i++){
				BatchRegInfoModel regInfo = platebatches[i].getRegInfo();
				if (regInfo != null) {
					if (!StringUtils.equals(validStatus, regInfo.getStatus())){
						pIt.remove();
						break;
					}
				}
			}					
		}
		return platesArray;
	}

	/**
	 * 	Remove objects with mismatching status from array.
	 *  @param method - method of BatchRegInfoModel used to get status to compare
	 */
	private List<?> clearObjects(List<?> objects, String validStatus, Method method) throws Exception {
		List<?> validArray = null;
		if (objects != null) {
			validArray = new Vector<Object>(objects);
		} else {
			validArray =  new Vector<Object>();
		}
		/*Delete invalid objects from list*/
		Iterator<?> bIt = validArray.iterator();
		while (bIt.hasNext()) {
			Object checked = bIt.next();
			if (checked instanceof ProductBatchModel) {
				ProductBatchModel batchModel = (ProductBatchModel) checked;
				clearObject(batchModel.getRegInfo(), bIt, validStatus, method);
			} else if (checked instanceof PlateWell<?>) {
				PlateWell<ProductBatchModel> well = (PlateWell<ProductBatchModel>) checked;
				ProductBatchModel batchModel = (ProductBatchModel) well.getBatch();
				if (batchModel != null) {
					clearObject(batchModel.getRegInfo(), bIt, validStatus, method);
				}
			} else if (checked instanceof BatchRegInfoModel) {
				clearObject((BatchRegInfoModel) checked, bIt, validStatus, method);
			}
		}
		return validArray;
	}

	private void clearObject(BatchRegInfoModel regInfo, Iterator<?> it, String validStatus, Method method) throws Exception {
		String status = (String) method.invoke(regInfo, new Object[0]);
		if (!StringUtils.equals(validStatus, status)){
			it.remove();
		}
	}
	
	private void setCompoundRegistrationPendingStatusForBatches(List<ProductBatchModel> batches) {
		if (!batches.isEmpty()) {
			for (ProductBatchModel batchModel : batches) {
				if (batchModel != null && batchModel.getRegInfo() != null) {
					BatchRegInfoModel regInfo = batchModel.getRegInfo();
					regInfo.setSubmissionStatus(CeNConstants.REGINFO_SUBMITTED);
					regInfo.setStatus(CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
					regInfo.setCompoundRegistrationStatusMessage("");
				}
			}
		}
	}

	private void handleException(List<String> jobIDs, List<ProductBatchModel> batches, List<String> workflowList, JobModel model, Throwable e) {
		log.info("handleException(List, List, JobModel, Exception) start");
		
		List<BatchRegInfoModel> regInfos = new Vector<BatchRegInfoModel>();
		
		for (ProductBatchModel batch : batches) {
			if (batch != null && batch.getRegInfo() != null) {
				JobModel batchJobModel = new JobModel();
				BatchRegInfoModel regInfo = batch.getRegInfo();
				
				batchJobModel.setCallbackUrl(model.getCallbackUrl());
				batchJobModel.setUserMessage(model.getClonedUserMessage());
				batchJobModel.setPlateKey(batch.getKey());
				
				if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
					if (!isPassOrFail(regInfo.getCompoundRegistrationStatus())) {
						regInfo.setStatus(null);
						regInfo.setSubmissionStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						regInfo.setCompoundRegistrationStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						regInfo.setCompoundRegistrationStatusMessage(e.getMessage());
						batchJobModel.setCompoundRegistrationStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						batchJobModel.setCompoundRegistrationStatusMessage(e.getMessage());
						sendMessageToClient(batchJobModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_REGISTRATION);
					}
				}
				
				if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
					if (!isPassOrFail(regInfo.getCompoundManagementStatus())) {
						regInfo.setCompoundManagementStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						regInfo.setCompoundManagementStatusMessage(e.getMessage());
						batchJobModel.setCompoundManagementStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						batchJobModel.setCompoundManagementStatusMessage(e.getMessage());
						sendMessageToClient(batchJobModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
					}
				}
				
				if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
					if (!isPassOrFail(regInfo.getPurificationServiceStatus())) {
						regInfo.setPurificationServiceStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						regInfo.setPurificationServiceStatusMessage(e.getMessage());
						batchJobModel.setPurificationServiceStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						batchJobModel.setPurificationServiceStatusMessage(e.getMessage());
						sendMessageToClient(batchJobModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
					}
				}
				
				if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
					if (!isPassOrFail(regInfo.getCompoundAggregationStatus())) {
						regInfo.setCompoundAggregationStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						regInfo.setCompoundAggregationStatusMessage(e.getMessage());
						batchJobModel.setCompoundAggregationStatus(CeNConstants.REGINFO_NOT_SUBMITTED);
						batchJobModel.setCompoundAggregationStatusMessage(e.getMessage());
						sendMessageToClient(batchJobModel, "", HttpUserMessage.BATCH, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
					}
				}
				
				regInfos.add(regInfo);
			}
		}
		
		try {
			updateBatchJobs(regInfos);
		} catch (Exception e1) {
			log.error("Error updating batch statuses in DB: ", e1);
		}
		
		log.info("handleException(List, List, JobModel, Exception) end");
	}

	private boolean isPassOrFail(String status) {
		if (StringUtils.equals(status, CeNConstants.REGINFO_SUBMISION_PASS)) {
			return true;
		}
		if (StringUtils.equals(status, CeNConstants.REGINFO_SUBMISION_FAIL)) {
			return true;
		}
		return false;
	}

	private void updateJobModelStatus(JobModel model, String jobStatus) throws Exception {
		log.info("setJobModelStatus(JobModel, String) start -- jobId = " + model.getJobID() + "; jobStatus = " + jobStatus);
		model.setJobStatus(jobStatus);
		updateJobModel(model);
		log.info("setJobModelStatus(JobModel, String) end");
	}
	
	/**
	 * THIS METHOD IS DEPRECATED -- Now status updates from client
	 * <br>
	 * Send HttpUserMessage to client
	 * @param model JobModel
	 * @param message Message to show
	 * @param type Type - batch or plate
	 * @param jobId Job ID
	 */
	@Deprecated
	private void sendMessageToClient(JobModel model, String message, String type, String jobId) {
/*		log.info("sendMessageToClient(JobModel, String) start");
		
		HttpUserMessage httpMessage = model.getClonedUserMessage();
		
		httpMessage.setAction(CeNConstants.JETTY_MESSAGE_REGISTRATION_ACTION);
		httpMessage.setType(type);
		httpMessage.setMessage(message);
		httpMessage.setJobid(jobId);
		httpMessage.setKey(model.getPlateKey());
		
		if (StringUtils.equals(jobId, CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
			httpMessage.setStatus(model.getCompoundRegistrationStatus());
			httpMessage.setStatusMessage(model.getCompoundRegistrationStatusMessage());
		}
		if (StringUtils.equals(jobId, CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
			httpMessage.setStatus(model.getCompoundManagementStatus());
			httpMessage.setStatusMessage(model.getCompoundManagementStatusMessage());
		}
		if (StringUtils.equals(jobId, CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
			httpMessage.setStatus(model.getPurificationServiceStatus());
			httpMessage.setStatusMessage(model.getPurificationServiceStatusMessage());
		}
		if (StringUtils.equals(jobId, CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
			httpMessage.setStatus(model.getCompoundAggregationStatus());
			httpMessage.setStatusMessage(model.getCompoundAggregationStatusMessage());
		}
				
		CommonUtils.sendMessageToClient(httpMessage, model.getCallbackUrl());
		
		log.info("sendMessageToClient(JobModel, String) end");*/
	}
	
	private void waitForCompoundRegistration(List<ProductBatchModel> batches, List<String> jobIDs, JobModel model) throws Throwable {
		log.info("waitForCompoundRegistration(List, JobModel) start for jobIDs: " + jobIDs.toString());
		
		if (jobIDs != null && jobIDs.size() > 0) {
			RegistrationUtils regutil = new RegistrationUtils();
			List<CompoundRegistrationJobStatus> completedJobs = new Vector<CompoundRegistrationJobStatus>();
			boolean allJobsCompleted = false;
			while (!allJobsCompleted) {
				allJobsCompleted = true;
				completedJobs.clear();
				List<CompoundRegistrationJobStatus> status = getRegisterJobStatus(jobIDs);
				if(status.isEmpty()) {
					log.error("CompoundRegistration API is not returning the job statuses.");
					throw new Exception("CompoundRegistration API is not returning the job statuses.");
				}
				for (CompoundRegistrationJobStatus s : status) {
					if (s != null && (StringUtils.equals(s.getStatus(), CeNConstants.REGINFO_PASSED) || StringUtils.equals(s.getStatus(), CeNConstants.REGINFO_FAILED))) {
						if (s.getStopTime() == null) {
							s.setStopTime(new Timestamp(new Date().getTime()));
						}
						
						completedJobs.add(s);
					} else {
						allJobsCompleted = false;
					}
				}
				log.info("JobIDs size: " + jobIDs.size());
				log.info("Completed jobs size: " + completedJobs.size());
				for (CompoundRegistrationJobStatus regStatus : completedJobs) {
					String jobId = regStatus.getJobId();
					List<BatchRegInfoModel> comRegModels =	getAllRegisteredBatchesForJobid(jobId);
					if (comRegModels != null) {
						log.info("Registered batches: " + comRegModels.size());
					}
										
					CompoundRegistrationStatus[] compStatus = getRegisterCompoundStatus(jobId, "", new int[0], false);
					regutil.setRegistrationDataToCeNCompoundModel(comRegModels, compStatus, regStatus);
					
					// Log batch statuses and set regInfo to related batches
					for (BatchRegInfoModel regInfo : comRegModels) {
						if (regInfo != null) {
							// Log batch status
							log.info("CompoundRegistration Batch status: " + regInfo.getConversationalBatchNumber() + ": " + regInfo.getStatus());
							// Set regInfo to related batches
							for (ProductBatchModel batchModel : batches) {
								if (batchModel != null && StringUtils.equals(batchModel.getKey(), regInfo.getBatchKey())) {
									batchModel.setRegInfo(regInfo);
								}
							}
						}
					}
										
					updateBatchJobs(comRegModels);
					
					JobModel compoundRegistrationModel = new JobModel();
					compoundRegistrationModel.setCallbackUrl(model.getCallbackUrl());
					compoundRegistrationModel.setUserMessage(model.getClonedUserMessage());
					compoundRegistrationModel.setCompoundRegistrationStatus(regStatus.getStatus());
					compoundRegistrationModel.setCompoundRegistrationStatusMessage(regStatus.getDetails());
					
					sendMessageToClient(compoundRegistrationModel, "", HttpUserMessage.BATCH, jobId);
					
					// set jobID=-1 to prevent double-submission
					for (BatchRegInfoModel regInfo : comRegModels) {
						if (regInfo != null) {
							regInfo.setJobId("-1");
						}
					}
					
					updateBatchJobs(comRegModels);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("Error sleeping thread:", e);
				}
			}
		}
		log.info("waitForCompoundRegistration(List, JobModel) end");
	}
	
	/**
	 * Updates batches registration information in CeN DB
	 * @param batchRegInfoModels registration information models
	 * @throws Exception
	 */
	private void updateBatchJobs(List<BatchRegInfoModel> batchRegInfoModels) throws Exception {
		try {
			storageDelegate.updateBatchJobs((BatchRegInfoModel[]) batchRegInfoModels.toArray(new BatchRegInfoModel[0]), null);
		} catch (Throwable e) {
			try {
				init();
				storageDelegate.updateBatchJobs((BatchRegInfoModel[]) batchRegInfoModels.toArray(new BatchRegInfoModel[0]), null);
			} catch (Exception e1) {
				log.error("Error getting compound status:", e1);
				throw e1;
			}
		}
	}
	
	private CompoundRegistrationStatus[] getRegisterCompoundStatus(String jobId, String plateId, int[] offsets, boolean includeStruct) throws Exception {
		log.info("getRegisterCompoundStatus(String, String, int[], boolean) start");
		CompoundRegistrationStatus[] result = null;
		try {
			result = registrationDelegate.getRegisterCompoundStatus(jobId, plateId, offsets, includeStruct);
		} catch (Exception e) {
			try {
				init();
				result = registrationDelegate.getRegisterCompoundStatus(jobId, plateId, offsets, includeStruct);
			} catch (Exception e1) {
				log.error("Error getting compound status:", e1);
				throw e1;
			}
		}
		log.info("getRegisterCompoundStatus(String, String, int[], boolean) end");
		return result;
	}
	
	private NotebookPageModel getNotebookPageExperimentInfo(NotebookRef nbRef) throws Throwable {
		log.info("getNotebookPageExperimentInfo(NotebookRef, String) start");
		NotebookPageModel result = null;
		try {
			result = storageDelegate.getNotebookPageExperimentInfo(nbRef, null, null);
		} catch (Throwable e) {
			try {
				init();
				result = storageDelegate.getNotebookPageExperimentInfo(nbRef, null, null);
			} catch (Throwable e1) {
				log.error("Error getting notebook page:", e1);
				throw e1;
			}
		}
		log.info("getNotebookPageExperimentInfo(NotebookRef, String) end");
		return result;
	}
	
	private List<BatchRegInfoModel> getAllRegisteredBatchesForJobid(String jobId) throws Throwable {
		log.info("getAllRegisteredBatchesForJobid(String) start");
		List<BatchRegInfoModel> result = null;
		try {
			result = storageDelegate.getAllRegisteredBatchesForJobid(jobId, null);
		} catch (Throwable e) {
			try {
				init();
				result = storageDelegate.getAllRegisteredBatchesForJobid(jobId, null);
			} catch (Throwable e1) {
				log.error("Error getting registered batches:", e1);
				throw e1;
			}
		}
		log.info("getAllRegisteredBatchesForJobid(String) end");
		return result;
	}
		
	private List<CompoundRegistrationJobStatus> getRegisterJobStatus(List<String> jobIDs) throws Exception {
		CompoundRegistrationJobStatus[] result = null;
		log.info("getRegisterJobStatus(String[]) start");
		try {
			result = registrationDelegate.getRegisterJobStatus((String[]) jobIDs.toArray(new String[0]));
		} catch (Exception e) {
			try {
				init();
				result = registrationDelegate.getRegisterJobStatus((String[]) jobIDs.toArray(new String[0]));
			} catch (Exception e1) {
				log.error("Error getting register job status:", e1);
				throw e1;
			}
		}
		log.info("getRegisterJobStatus(String[]) end");
		return (result == null ? new Vector<CompoundRegistrationJobStatus>() : new Vector<CompoundRegistrationJobStatus>(Arrays.asList(result)));
	}
	
	private void updateRegistrationJobs(JobModel[] jobs) throws Exception {
		log.info("updateRegistrationJobs(JobModel[]) start");
		try {
			storageDelegate.updateRegistrationJobs(jobs, null);
		} catch (Throwable e) {
			try {
				init();
				storageDelegate.updateRegistrationJobs(jobs, null);
			} catch (Exception e1) {
				log.error("Error updating registration jobs:", e1);
				throw e1;
			}
		}
		log.info("updateRegistrationJobs(JobModel[]) end");
	}
	
	/**
	 * Submit product batches to CompoundRegistration
	 */
	private Map<String, String> submitBatchesForRegistration(List<ProductBatchModel> batches, String userName, NotebookPageModel pageModel) throws Exception {
		log.info("submitBatchesForRegistration(List<ProductBatchModel>, String, NotebookPageModel) start");
		Map<String, String> result = null;
		try {
			result = registrationManager.submitBatchesForRegistration((ProductBatchModel[]) batches.toArray(new ProductBatchModel[0]), userName, pageModel);
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitBatchesForRegistration((ProductBatchModel[]) batches.toArray(new ProductBatchModel[0]), userName, pageModel);
			} catch (Exception e1) {
				log.error("Error submitting batches to CompoundRegistration:", e1);
				throw e1;
			}
		}
		log.info("submitBatchesForRegistration(List<ProductBatchModel>, String, NotebookPageModel) end");
		return (result == null ? new HashMap<String, String>() : result);
	}
	
	/**
	 * Submit tubes to CompoundManagement
	 */
	private Map<String, String> submitTubesForCompoundManagementRegistration(List<PlateWell<ProductBatchModel>> wells, NotebookPageModel pageModel) throws Exception {
		log.info("submitTubesForCompoundManagementRegistration(List<PlateWell>, NotebookPageModel) start");
		Map<String, String> result = null;
		try {
			result = registrationManager.submitTubesToCompoundManagement((PlateWell<ProductBatchModel>[]) wells.toArray(), pageModel.getSiteCode());
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitTubesToCompoundManagement((PlateWell<ProductBatchModel>[]) wells.toArray(), pageModel.getSiteCode());
			} catch (Exception e1) {
				log.error("Error submitting tubes to CompoundManagement:", e1);
				throw e1;
			}
		}
		log.info("submitTubesForCompoundManagementRegistration(List<PlateWell>, NotebookPageModel) end");
		return (result == null ? new HashMap<String, String>() : result);
	}
	
	/**
	 * Submit product plates to CompoundManagement
	 */
	private Map<String, JobModel> submitPlatesForCompoundManagementRegistration(List<ProductPlate> plates, NotebookPageModel pageModel) throws Exception {
		log.info("submitPlatesForCompoundManagementRegistration(List<ProductPlate>, NotebookPageModel) start");
		Map<String, JobModel> result = null;
		try {
			result = registrationManager.submitPlatesToCompoundManagement((ProductPlate[])plates.toArray(new ProductPlate[0]), pageModel.getSiteCode());
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitPlatesToCompoundManagement((ProductPlate[])plates.toArray(new ProductPlate[0]), pageModel.getSiteCode());
			} catch (Exception e1) {
				log.error("Error submitting plates to CompoundManagement:", e1);
				throw e1;
			}
		}
		log.info("submitPlatesForCompoundManagementRegistration(List<ProductPlate>, NotebookPageModel) end");
		return (result == null ? new HashMap<String, JobModel>() : result);
	}
		
	/**
	 * Submit tubes to PurificationService
	 */
	private Map<String, JobModel> submitTubesForPurification(List<PlateWell<ProductBatchModel>> wells, String userName, NotebookPageModel pageModel) throws Exception {
		log.info("submitTubesForPurification(List<PlateWell>, String, NotebookPageModel) start");
		Map<String, JobModel> result = null;
		try {
			result = registrationManager.submitTubesForPurification(userName, pageModel.getSiteCode(), pageModel.getKey(), wells.toArray(new PlateWell[0]));
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitTubesForPurification(userName, pageModel.getSiteCode(), pageModel.getKey(), wells.toArray(new PlateWell[0]));
			} catch (Exception e1) {
				log.error("Error submitting tubes to PurificationService:", e1);
				throw e1;
			}
		}
		log.info("submitTubesForPurification(List<PlateWell>, String, NotebookPageModel) end");
		return (result == null ? new HashMap<String, JobModel>() : result);
	}
		
	/**
	 * Submit product plates to PurificationService
	 */
	private Map<String, JobModel> submitPlatesForPurification(List<ProductPlate> plates, String userName, NotebookPageModel pageModel) throws Exception {
		log.info("submitPlatesForPurification(List<ProductPlate>, String, NotebookPageModel) start");
		Map<String, JobModel> result = null;
		try {
			result = registrationManager.submitPlatesForPurification(userName, pageModel.getSiteCode(), plates.toArray(new ProductPlate[0]));
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitPlatesForPurification(userName, pageModel.getSiteCode(), plates.toArray(new ProductPlate[0]));
			} catch (Exception e1) {
				log.error("Error submitting plates to PurificationService:", e1);
				throw e1;
			}
		}
		log.info("submitPlatesForPurification(List<ProductPlate>, String, NotebookPageModel) end");
		return (result == null ? new HashMap<String, JobModel>() : result);
	}
	
	/**
	 * Submit product batches to CompoundAggregation
	 */
	private Map<String, JobModel> submitBatchesToCompoundAggregation(List<ProductBatchModel> batches, String userName, NotebookPageModel pageModel) throws Exception {
		log.info("submitBatchesToCompoundAggregation(List<ProductBatchModel>, String, NotebookPageModel) start");
		Map<String, JobModel> result = null;
		try {
			result = registrationManager.submitBatchesToCompoundAggregation(userName, (ProductBatchModel[]) batches.toArray(new ProductBatchModel[0]), pageModel.getPageHeader(), pageModel.getSiteCode());
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitBatchesToCompoundAggregation(userName, (ProductBatchModel[]) batches.toArray(new ProductBatchModel[0]), pageModel.getPageHeader(), pageModel.getSiteCode());
			} catch (Exception e1) {
				log.error("Error submitting batches to CompoundAggregation:", e1);
				throw e1;
			}
		}
		log.info("submitBatchesToCompoundAggregation(List<ProductBatchModel>, String, NotebookPageModel) end");
		return (result == null ? new HashMap<String, JobModel>() : result);
	}
	
	/**
	 * Submit product plates to CompoundAggregation
	 */
	private Map<String, JobModel> submitPlatesToCompoundAggregation(List<ProductPlate> plates, String userName, NotebookPageModel pageModel) throws Exception {
		log.info("submitPlatesToCompoundAggregation(List<ProductPlate>, String, NotebookPageModel) start");
		Map<String, JobModel> result = null;
		try {
			result = registrationManager.submitPlatesToCompoundAggregation(userName, (ProductPlate[]) plates.toArray(new ProductPlate[0]), pageModel.getPageHeader(), pageModel.getSiteCode());
		} catch (Throwable e) {
			try {
				init();
				result = registrationManager.submitPlatesToCompoundAggregation(userName, (ProductPlate[]) plates.toArray(new ProductPlate[0]), pageModel.getPageHeader(), pageModel.getSiteCode());
			} catch (Exception e1) {
				log.error("Error submitting plates to CompoundAggregation:", e1);
				throw e1;
			}
		}
		log.info("submitPlatesToCompoundAggregation(List<ProductPlate>, String, NotebookPageModel) end");
		return (result == null ? new HashMap<String, JobModel>() : result);
	}
	
	private List<String> extractIDs(Map<String, String> batchesOrPlatesMap) {
		log.info("extractIDs(Map) start");
		List<String> result = new Vector<String>();
		for (Entry<String, String> entry : batchesOrPlatesMap.entrySet()) {
			String id = entry.getValue();
			if (StringUtils.isNotEmpty(id)) {
				result.add(id);
			}
		}
		log.info("extractIDs(Map) end");
		return result;
	}

	private void updateJobModel(JobModel model) throws Exception{
		log.info("updateJobModel(JobModel) start");
		updateRegistrationJobs(new JobModel[] { model });
		log.info("updateJobModel(JobModel) end");
	}

	private List<ProductPlate> getPlateModels(String plateKeysString, NotebookPageModel pageModel) {
		log.info("getPlates(String) start");
		List<ProductPlate> result = new Vector<ProductPlate>();
		if (!StringUtils.isEmpty(plateKeysString)) {
			List<String> plateKeys = getKeys(plateKeysString);
			if (!plateKeys.isEmpty()) {
				List<ProductPlate> productPlates = pageModel.getAllProductPlates();
				if (productPlates != null) {
					for (String key : plateKeys) {
						for (ProductPlate plate : productPlates) {
							if (plate != null && key != null && StringUtils.equals(plate.getKey(), key)) {
								result.add(plate);
								break;
							}
						}
					}
				}
			}
		}
		log.info("getPlates(String) end");
		return result;
	}

	private List<ProductBatchModel> getBatchModels(String batchKeysString, NotebookPageModel pageModel) {
		log.info("getBatchModels(String) start");
		List<ProductBatchModel> result = new Vector<ProductBatchModel>();
		if (!StringUtils.isEmpty(batchKeysString)) {
			List<String> batchKeys = getKeys(batchKeysString);
			if (!batchKeys.isEmpty()) {
				List<ProductBatchModel> productBatches = pageModel.getAllProductBatchModelsInThisPage();
				if (productBatches != null) {
					for (String key : batchKeys) {
						for (ProductBatchModel batch : productBatches) {
							if (batch != null && key != null && StringUtils.equals(batch.getKey(), key)) {
								result.add(batch);
								break;
							}
						}
					}
				}
			}
		}		
		log.info("getBatchModels(String) end");
		return result;
	}

	private List<String> getKeys(String keysString) {
		log.info("getKeys(String) start");
		List<String> result = new Vector<String>(); 
		if (!StringUtils.isEmpty(keysString)) {
			Scanner scanner = new Scanner(keysString);
			scanner.useDelimiter(",");
			while (scanner.hasNext()) {
				result.add(scanner.next().trim());
			}
		}
		log.info("getKeys(String) end");
		return result;
	}
}
