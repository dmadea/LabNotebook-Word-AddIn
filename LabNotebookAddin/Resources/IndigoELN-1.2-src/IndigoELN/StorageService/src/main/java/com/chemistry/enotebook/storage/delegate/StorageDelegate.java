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
package com.chemistry.enotebook.storage.delegate;


import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.searchquery.SearchQueryParams;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.storage.*;
import com.chemistry.enotebook.storage.exceptions.SearchTooMuchDataException;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import com.chemistry.enotebook.storage.exceptions.StorageTokenInvalidException;
import com.chemistry.enotebook.storage.interfaces.StorageRemote;
import com.chemistry.enotebook.utils.CompositeCompressedBytes;
import com.chemistry.enotebook.utils.ParallelExpModelUtils;
import com.chemistry.enotebook.utils.SerializeObjectCompression;

import java.util.Date;
import java.util.List;

public class StorageDelegate implements StorageRemote {

	private StorageRemote service;

	public StorageDelegate() throws StorageInitException {
		service = ServiceLocator.getInstance().locateService("StorageService", StorageRemote.class);
	}

	@Override
	public StorageContextInterface retrieveData(StorageContextInterface context, SessionIdentifier sessionID) throws StorageException, StorageTokenInvalidException {
		return service.retrieveData(context, sessionID);
	}

	@Override
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment, String pageVersion) throws StorageException {
		return service.validateNotebook(siteCode, notebook, experiment, pageVersion);
	}

	@Override
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment) throws StorageException {
		return validateNotebook(siteCode, notebook, experiment, null);
	}

	@Override
	public void createNotebook(String notebook, SessionIdentifier sessionID) throws StorageException, NotebookInvalidException, NotebookExistsException {
		service.createNotebook(notebook, sessionID);
	}

	@Override
	public void createNotebook(String siteCode, String userId, String notebook, SessionIdentifier sessionID) throws StorageException, NotebookInvalidException {
		service.createNotebook(siteCode, userId, notebook, sessionID);
	}

	@Override
	public boolean deleteExperiment(String siteCode, String notebook, String experiment, int version, SessionIdentifier sessionID) throws StorageException, InvalidNotebookRefException {
		return service.deleteExperiment(siteCode, notebook, experiment, version, sessionID);
	}

	@Override
	public int getNextExperimentForNotebook(String notebook) throws StorageException, NotebookInvalidException {
		return service.getNextExperimentForNotebook(notebook);
	}

	@Override
	public String[] getUsersFullName(String[] userIDs) throws StorageException {
		return service.getUsersFullName(userIDs);
	}

	@Override
	public List<SignaturePageVO> getExperimentsBeingSigned(String ntUserID) throws StorageException {
		return service.getExperimentsBeingSigned(ntUserID);
	}

	@Override
	public SignaturePageVO getNotebookPageStatus(String siteCode, String nbRefStr, int version) throws StorageException, InvalidNotebookRefException {
		return service.getNotebookPageStatus(siteCode, nbRefStr, version);
	}

	@Override
	public void updateNotebookPageStatus(String siteCode, String nbRefStr, int version, String status) throws StorageException, InvalidNotebookRefException {
		service.updateNotebookPageStatus(siteCode, nbRefStr, version, status);
	}

	@Override
	public void updateNotebookPageStatus(String siteCode, String nbRefStr, int version, String status, int ussiKey) throws StorageException, InvalidNotebookRefException {
		service.updateNotebookPageStatus(siteCode, nbRefStr, version, status, ussiKey);
	}

	@Override
	public void storeExperimentPDF(String siteCode, String nbRefStr, int version, byte[] pdf) throws StorageException, InvalidNotebookRefException {
		service.storeExperimentPDF(siteCode, nbRefStr, version, pdf);
	}

	@Override
	public List getNotebookPagesForUser(String userid, SessionIdentifier identifier) {
		return null;
	}

	@Override
	public NotebookPageModel getNotebookPageHeaderInfo(NotebookRef nbRef, int version, SessionIdentifier identifier) throws StorageException {
		return null;
	}

	@Override
	public List getAllReactionsSteps(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public List getAllReactionsStepsDetails(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	@Override
	public ReactionStepModel getSummaryReaction(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public ReactionStepModel getReactionStep(NotebookRef nbRef, int stepno) {
		return null;
	}

	@Override
	public NotebookPageModel createParallelExperiment(String spid, String notebook, SessionIdentifier sessionID) throws StorageException {
		NotebookPageModel pageModel = service.createParallelExperiment(spid, notebook, sessionID);
		if(pageModel != null) {
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			utils.setOrRefreshGuiPseudoProductPlate();
			utils.populateMonomerAndProductHashMaps();
			utils.linkProductBatchesAnalyticalModelInAnalysisCache();
			utils.linkBatchesWithPlateWells();
			pageModel.onLoadonAfterSaveSetModelChanged(false,true);
		}
		return pageModel;
	}

	@Override
	public boolean addDSPPlanToCeNReactionStep(NotebookRef nbRefWithPageKey, String PID, SessionIdentifier sessionID) throws StorageException {
		return service.addDSPPlanToCeNReactionStep(nbRefWithPageKey, PID, sessionID);
	}

	@Override
	public List getAllContainers() throws StorageException {
		return service.getAllContainers();
	}

	@Override
	public List getContainers(boolean isUserDefined) throws StorageException {
		return service.getContainers(isUserDefined);
	}

	@Override
	public List getUserContainers(String userId) throws StorageException {
		return service.getUserContainers(userId);
	}

	@Override
	public Container getContainer(String key) throws StorageException {
		return service.getContainer(key);
	}

	@Override
	public void createContainer(Container container) throws StorageException {
		service.createContainer(container);
	}

	@Override
	public void createContainers(List containerList) throws StorageException {
		service.createContainers(containerList);
	}

	@Override
	public void updateContainer(Container container) throws StorageException {
		service.updateContainer(container);
	}

	@Override
	public void updateContainers(List containerList) throws StorageException {
		service.updateContainers(containerList);
	}

	@Override
	public void removeContainer(String containerKey) throws StorageException {
		service.removeContainer(containerKey);
	}

	@Override
	public void removeContainers(List containerKeyList) throws StorageException {
		service.removeContainers(containerKeyList);
	}

	@Override
	public List searchForCompoundManagementContainers(Container container) throws StorageException {
		return service.searchForCompoundManagementContainers(container);
	}

	@Override
	public void removeNotebookPage(String pageKey, SessionIdentifier sessionID) throws StorageException {
		service.removeNotebookPage(pageKey, sessionID);
	}

	public NotebookPageModel createConceptionRecord(String userid, String notebook, SessionIdentifier sessionID) throws StorageException {
		NotebookPageModel pageModel = service.createConceptionRecord(notebook, sessionID);
		return pageModel;
	}

	public NotebookPageModel createSingletonExperiment(String userid, String notebook, SessionIdentifier sessionID) throws StorageException {
		NotebookPageModel pageModel = service.createSingletonExperiment(notebook, sessionID);
		if(pageModel != null) {
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			utils.populateMonomerAndProductHashMaps();
			utils.linkProductBatchesAnalyticalModelInAnalysisCache();
		}
		return pageModel;
	}

	@Override
	public void deletePlateWells(String[] plateWellKeys, SessionIdentifier sessionID) throws StorageException {
		service.deletePlateWells(plateWellKeys, sessionID);
	}

	@Override
	public void deletePlates(String[] plateKeys, SessionIdentifier sessionID) throws StorageException {
		service.deletePlates(plateKeys, sessionID);
	}

	@Override
	public List getAllCROs(SessionIdentifier sessionID) throws StorageException {
		return service.getAllCROs(sessionID);
	}

	@Override
	public List getAllChemistsUnderCRO(String croId, SessionIdentifier sessionID) throws StorageException {
		return service.getAllChemistsUnderCRO(croId, sessionID);
	}
	
	@Override
	public List getAllPagesWithSummaryUnderChemist(String chemistId, SessionIdentifier sessionID) throws StorageException {
		return service.getAllPagesWithSummaryUnderChemist(chemistId, sessionID);
	}
	
	
	public List getAllPagesWithSummaryForRequestId(String requestId, SessionIdentifier sessionID) throws StorageException {
		return service.getAllPagesWithSummaryForRequestId(requestId, sessionID);
	}
	
	public List getAllRequestIdsForChemist(String chemistId, SessionIdentifier sessionID) throws StorageException {
		return service.getAllRequestIdsForChemist(chemistId, sessionID);
	}

	@Override
	public Date getCROModifiedDate(String requestId, SessionIdentifier sessionID) throws StorageException {
		return service.getCROModifiedDate(requestId, sessionID);
	}

	@Override
	public CROPageInfo getCroPageForNBK(NotebookRef nbkRef, SessionIdentifier sessionID) throws StorageException {
		return service.getCroPageForNBK(nbkRef, sessionID);
	}

	@Override
	public List getAllNotebooksForChemistId(String chemistId, SessionIdentifier sessionID) throws StorageException {
		return service.getAllNotebooksForChemistId(chemistId, sessionID);
	}

	@Override
	public List getAllPagesForNotebook(String nbkNo, SessionIdentifier sessionID) throws StorageException {
		return service.getAllPagesForNotebook(nbkNo, sessionID);
	}

	@Override
	public JobModel getRegistrationJob(String jobId) throws StorageException {
		return service.getRegistrationJob(jobId);
	}

	@Override
	public List<JobModel> getAllRegistrationJobs(String userId, String status) throws StorageException {
		return service.getAllRegistrationJobs(userId, status);
	}

	@Override
	public List<JobModel> getAllRegistrationJobs() throws  StorageException	{
		return service.getAllRegistrationJobs();
	}

	@Override
	public List<JobModel> getAllRegistrationJobsAndUpdateStatus(String status) throws  StorageException	{
		return service.getAllRegistrationJobsAndUpdateStatus(status);
	}

	@Override
	public void updateBatchJobs(BatchRegInfoModel[] batchModels, SessionIdentifier sessionID) throws  StorageException {
		service.updateBatchJobs(batchModels, sessionID);
	}

	@Override
	public List getAllPendingCompoundRegistrationJobs() throws  StorageException {
		return service.getAllPendingCompoundRegistrationJobs();
	}

	@Override
	public List<BatchRegInfoModel> getAllRegisteredBatchesForPage(String pageKey, SessionIdentifier sessionID) throws StorageException {
		return service.getAllRegisteredBatchesForPage(pageKey, sessionID);
	}

	@Override
	public void updateRegistrationJobs(JobModel[] jobs, SessionIdentifier sessionID) throws  StorageException	{
		service.updateRegistrationJobs(jobs, sessionID);
	}

	public List getAllRegisteredBatchesForJobid(String jobid, SessionIdentifier sessionID) throws StorageException {
		return service.getAllRegisteredBatchesWithJobID(jobid, sessionID);
	}

	@Override
	public void addNotebookPage(NotebookPageModel pageModel, SessionIdentifier sessionID) throws StorageException {
		// assert non null page model
		if (pageModel == null)
			throw new StorageException("addNotebook page failed.  pageModel was null.");
		ProcedureImagesUpdateManager.updateProcedureOnSave(pageModel);
		service.addNotebookPage(pageModel, sessionID);
		if(pageModel != null) {
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			//utils.setOrRefreshGuiPseudoProductPlate();
			//utils.populateMonomerAndProductHashMaps();
			//utils.linkProductBatchesAnalyticalModelInAnalysisCache();
			//utils.linkBatchesWithPlateWells();
			pageModel.onLoadonAfterSaveSetModelChanged(false, true);
		}
	}

	@Override
	public ProductBatchModel getProductBatchForBatchNumber(String batchNumber, SessionIdentifier sessionID) throws StorageException {
		return service.getProductBatchForBatchNumber(batchNumber, sessionID);
	}

	@Override
	public int checkCompliance(String username, int numComplianceDays) throws StorageException {
		return service.checkCompliance(username, numComplianceDays);
	}

	@Override
	public void updateNotebookPage(String siteCode, String nbRefStr, int version, String status, int ussiKey,byte[] pdfContent,String completionDateTSStr) throws StorageException, InvalidNotebookRefException {
       	service.updateNotebookPage(siteCode, nbRefStr, version, status, ussiKey,pdfContent,completionDateTSStr);
	}

	@Override
	public void updateNotebookPage(String siteCode, String nbRefStr, int version, String status, byte[] pdfContent, String pageXMLMetadata) throws StorageException, InvalidNotebookRefException {
		service.updateNotebookPage(siteCode, nbRefStr, version, status, pdfContent, pageXMLMetadata);
	}

	@Override
	public boolean completeExperiment(String siteCode, String nbRefStr, int version, TemplateVO template, NotebookUser user, String timeZoneId) throws StorageException, InvalidNotebookRefException {
		return service.completeExperiment(siteCode, nbRefStr, version, template, user, timeZoneId);
	}

	@Override
	public String getNotebookPageCompleteStatus(String siteCode, String nbRefStr, int version) throws InvalidNotebookRefException, StorageException {
		return service.getNotebookPageCompleteStatus(siteCode, nbRefStr, version);
	}

	@Override
	public List<ReactionStepModel> getAllReactionStepsWithDetails(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException {
		return service.getAllReactionStepsWithDetails(nbRef, identifier);
	}

	@Override
	public ReactionStepModel getIntermediateReactionStep(NotebookRef nbRef, int stepno, SessionIdentifier identifier) throws StorageException {
		return service.getIntermediateReactionStep(nbRef, stepno, identifier);
	}

	@Override
	public NotebookPageModel createConceptionRecord(String notebook, SessionIdentifier identifier) throws StorageException {
		return service.createConceptionRecord(notebook, identifier);
	}

	@Override
	public NotebookPageModel createSingletonExperiment(String notebook, SessionIdentifier identifier) throws StorageException {
		return service.createSingletonExperiment(notebook, identifier);
	}

	@Override
	public NotebookPageModel getExperiment(String nbkNumber, String pageNumber) throws StorageException {
		return service.getExperiment(nbkNumber, pageNumber);
	}

	@Override
	public DesignSynthesisPlan getDesignSynthesisPlanHeader(String spid) throws StorageException {
		return service.getDesignSynthesisPlanHeader(spid);
	}

	@Override
	public DesignSynthesisPlan getDesignSynthesisPlanAll(String spid, boolean includeStructures) throws StorageException {
		return service.getDesignSynthesisPlanAll(spid, includeStructures);
	}

	@Override
	public NotebookPageModel getDesignSynthesisPlanWithSummaryReaction(String spid, boolean includeStructures) throws StorageException {
		return service.getDesignSynthesisPlanWithSummaryReaction(spid, includeStructures);
	}

	@Override
	public ReactionStepModel getDesignSynthesisPlanReactionStep(String spid, String planId, boolean includeStructures) throws StorageException {
		return service.getDesignSynthesisPlanReactionStep(spid, planId, includeStructures);		
	}

	@Override
	public String[] insertRegistrationJobs(JobModel[] jobModels) throws StorageException {
		return service.insertRegistrationJobs(jobModels);
	}

	@Override
	public List getAllBatchJobs(String jobId, SessionIdentifier session) throws StorageException {
		return service.getAllBatchJobs(jobId, session);		
	}

	@Override
	public List getAllCompoundRegistrationJobOffsets(String jobId) throws StorageException {
		return service.getAllCompoundRegistrationJobOffsets(jobId);		
	}

	@Override
	public JobModel getCeNPlateJob(String plateKey) throws StorageException {
		return service.getCeNPlateJob(plateKey);		
	}

	@Override
	public ReactionStepModel getAllReactionStepDetails(NotebookRef nbRef, int version, ReactionStepModel step, SessionIdentifier identifier) throws StorageException {
		return service.getAllReactionStepDetails(nbRef, version, step, identifier);
	}

	@Override
	public List getAllRegisteredBatchesWithJobID(String jobid, SessionIdentifier sessionID) throws StorageException {
		return service.getAllRegisteredBatchesWithJobID(jobid, sessionID);
	}

	@Override
	public void createCeNExperiment(NotebookPageModel pageModel) throws StorageException {
		service.createCeNExperiment(pageModel);
	}

	@Override
	public void removeCRORequestId(String requestId) throws StorageException {
		service.removeCRORequestId(requestId);
	}

	@Override
	public ValidationInfo getNotebookInfo(String siteCode, String notebook) throws StorageException {
		return service.getNotebookInfo(siteCode, notebook);
	}
	
	// ----------------------------------------------------------------------
	// Methods with compression
	// ----------------------------------------------------------------------
	
	public List searchReactionInfo(final SearchQueryParams params) throws StorageException, SearchTooMuchDataException {
		try {
			return (List)SerializeObjectCompression.convertCompressedBytesToObject(searchReactionInfo(params, true));
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}
	
	@Override
	public CompositeCompressedBytes searchReactionInfo(final SearchQueryParams params, boolean bool) throws StorageException, SearchTooMuchDataException {
		return service.searchReactionInfo(params, bool);
	}
	
	public void updateNotebookPageModel(NotebookPageModel pageModel, SessionIdentifier sessionID) throws StorageException {
		ParallelExpModelUtils expUtils = new ParallelExpModelUtils(pageModel);
		expUtils.linkPlateWellBatchToCorrespondingBatchBeforeSAVE();
		updateNotebookPageModel(SerializeObjectCompression.serializeToCompressedBytes(pageModel), sessionID);
	}
	
	@Override
	public void updateNotebookPageModel(CompositeCompressedBytes pageModel, SessionIdentifier identifier) throws StorageException {
		service.updateNotebookPageModel(pageModel, identifier);
	}
	
	public NotebookPageModel getNotebookPageExperimentInfo(NotebookRef nbRef, String siteCode, SessionIdentifier sessionID) throws StorageException, InvalidNotebookRefException {
		NotebookPageModel pageModel = null;
		
		try {
			pageModel = (NotebookPageModel) SerializeObjectCompression.convertCompressedBytesToObject(getNotebookPageExperimentInfo(nbRef, sessionID));
		} catch (Exception e) {
			throw new StorageException(e);
		}
		
		if (pageModel != null) {
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			
			utils.sortProductBatchListsByBatchNumber();
			utils.linkPlateWellBatchToCorrespondingBatch();
			utils.setOrRefreshGuiPseudoProductPlate();
			utils.populateMonomerAndProductHashMaps();
			utils.linkBatchesWithPlateWells();
			
			if(pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
				utils.updateBatchesListAmountFlags();
			}
			
			utils.linkProductBatchesAnalyticalModelInAnalysisCache();
			pageModel.onLoadonAfterSaveSetModelChanged(false,true);
		}
		
		return pageModel;
	}	
	
	@Override
	public CompositeCompressedBytes getNotebookPageExperimentInfo(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException {
		return service.getNotebookPageExperimentInfo(nbRef, identifier);
	}
	
	public byte[] generateExperimentPDF(String siteCode, String nbRefStr, int version, String timeZoneId) throws StorageException, InvalidNotebookRefException {
		try {
			return (byte[]) SerializeObjectCompression.convertCompressedBytesToObject(generateExperimentPDF(siteCode, nbRefStr, version, timeZoneId, true));
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}
	
	@Override
	public CompositeCompressedBytes generateExperimentPDF(String siteCode, String nbRefStr, int version, String timeZoneId, boolean bool) throws StorageException, InvalidNotebookRefException {
		return service.generateExperimentPDF(siteCode, nbRefStr, version, timeZoneId, bool);				
	}

	public byte[] getExperimentPDF(String siteCode, String nbRefStr, int version) throws StorageException, InvalidNotebookRefException {
		try {
			return (byte[]) SerializeObjectCompression.convertCompressedBytesToObject(getExperimentPDF(siteCode, nbRefStr, version, true));
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public CompositeCompressedBytes getExperimentPDF(String siteCode, String nbRefStr, int version, boolean bool) throws StorageException, InvalidNotebookRefException {
		return service.getExperimentPDF(siteCode, nbRefStr, version, bool);
	}
	
	public AttachmentModel getNotebookPageExperimentAttachment(String attachmentKey) throws StorageException {
		try {
			return (AttachmentModel) SerializeObjectCompression.convertCompressedBytesToObject(getNotebookPageExperimentAttachment(attachmentKey, true));
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}
	
	@Override
	public CompositeCompressedBytes getNotebookPageExperimentAttachment(String attachmentKey, boolean bool) throws StorageException {
		return service.getNotebookPageExperimentAttachment(attachmentKey, bool);
	}
}
