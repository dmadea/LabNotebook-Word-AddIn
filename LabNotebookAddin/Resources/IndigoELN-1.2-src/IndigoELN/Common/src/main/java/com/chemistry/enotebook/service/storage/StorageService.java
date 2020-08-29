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
package com.chemistry.enotebook.service.storage;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.utils.CompositeCompressedBytes;

import java.util.List;

public interface StorageService {
	/**
	 * This method returns List of NotebookPageModel objects with Page header(Design Service Header in case of parallel exp ) and Reaction Step
	 * information filled. Reaction Step will be Summary Step(Step 0 ) in case of multi-step reaction experiment. Only Reaction
	 * Scheme is included in the step to provide preview of reaction drawing.
	 * 
	 * @param userid
	 * @param identifier
	 * @return List of NotebookPageModel objects
	 */
	public List<NotebookPageModel> getNotebookPagesForUser(String userid, SessionIdentifier identifier) throws StorageException;

	/**
	 * This method returns NotebookPageModel with page header info.(will have DesignSynthesisHeader info) if it is parallel
	 * experiment.This will fetch version of notebook specified. This will not have any ReactionStep info.
	 * 
	 * @param nbRef
	 * @param version
	 * @param identifier
	 * @return NotebookPageModel
	 */
	public NotebookPageModel getNotebookPageHeaderInfo(NotebookRef nbRef, int version, SessionIdentifier identifier)
			throws StorageException;

	/**
	 * This method returns List of ReactionsStepModel objects with only Reaction scheme data but not any batches or plates in that
	 * step.( Summary + Intermediate steps )
	 * 
	 * @param nbRef
	 * @param identifier
	 * @return List of ReactionsStepModel objects
	 */
	public List<ReactionStepModel> getAllReactionsSteps(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException;

	/**
	 * This method returns List of ReactionsStepModel objects with Reaction scheme data and also batches and plates involved in that
	 * step.( Summary + Intermediate steps )
	 * 
	 * @param nbRef
	 * @param identifier
	 * @return List of ReactionsStepModel objects with complete data
	 */
	public List<ReactionStepModel> getAllReactionStepsWithDetails(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException;

	public ReactionStepModel getAllReactionStepDetails(NotebookRef nbRef, int version, ReactionStepModel step,
			SessionIdentifier identifier) throws StorageException;

	/**
	 * This method returns ReactionStep object with reaction scheme data , monomer batches with out their structures ,product
	 * batches with structures and monomer , product plate data if any This method gets default step i.e step no 0
	 * 
	 * @param nbRef
	 * @param identifier
	 * @return ReactionStepModel complete data
	 */
	public ReactionStepModel getSummaryReaction(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException;

	/**
	 * This method returns ReactionStep object with reaction scheme data , monomer batches with out their structures ,product
	 * batches with structures and monomer , product plate data if any.
	 * 
	 * @param nbRef
	 * @param stepno
	 * @param identifier
	 * @return ReactionStepModel complete data
	 */
	public ReactionStepModel getIntermediateReactionStep(NotebookRef nbRef, int stepno, SessionIdentifier identifier)
			throws StorageException;

	/**
	 * This method returns NotebookPageModel with all data that needed for experiment tab. If multistep experiment then summary
	 * reaction step is only step included.
	 * 
	 * @param nbRef
	 * @param identifier
	 * @return NotebookPageModel object
	 */
	public CompositeCompressedBytes getNotebookPageExperimentInfo(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException;
  
  /**
   * Get an attachment (including contents) given the key.
   * @param attachmentKey
   * @return
   * @throws StorageAccessException
   */
  //CompositeCompressedBytes
  public CompositeCompressedBytes getNotebookPageExperimentAttachment(String attachmentKey, boolean bool) throws StorageException;
 

	/**
	 * Creates a parallel experiment and returns NotebookPageModel object with Notebook header,Design Service header, Summary reaction step
	 * with monomer(with out structures) and product batches
	 * 
	 * @param spid
	 * @param userid
	 * @return NotebookPageModel object
	 */
	public NotebookPageModel createParallelExperiment(String spid, String notebook, SessionIdentifier identifier)
			throws StorageException;

	public NotebookPageModel createConceptionRecord( String notebook, SessionIdentifier identifier) throws StorageException;

	public NotebookPageModel createSingletonExperiment( String notebook, SessionIdentifier identifier) throws StorageException;


	public NotebookPageModel getExperiment(String nbkNumber, String pageNumber) throws StorageException; // Consider NotebookRefModel

	// model in storage

	/**
	 * Returns Design Service object with spid level data, summary plan id,intermediate plan Ids Plan objects will be null in this DSP object
	 * 
	 * @param spid
	 * @return
	 */
	public DesignSynthesisPlan getDesignSynthesisPlanHeader(String spid) throws StorageException;

	/**
	 * Returns complete Design Service object with spid level data and summary plan,intermediate plan objs Plan objects will be null in this
	 * Design Service object
	 * 
	 * @param spid
	 * @return
	 */
	public DesignSynthesisPlan getDesignSynthesisPlanAll(String spid, boolean includeStructures) throws StorageException;

	/**
	 * Returns Design Service object with spid level data, summary plan data and intermediate plan Ids
	 * 
	 * @param spid
	 * @return DesignSynthesisPlan
	 */
	public NotebookPageModel getDesignSynthesisPlanWithSummaryReaction(String spid, boolean includeStructures)
			throws StorageException;

	/**
	 * Returns a ReactionStepModel Information from a Design Service matching the planId
	 * 
	 * @param spid
	 * @param planId
	 * @return ReactionStepModel
	 */
	public ReactionStepModel getDesignSynthesisPlanReactionStep(String spid, String planId, boolean includeStructures)
			throws StorageException;

	// Add all Reaction step,Reaction scheme and bacthes and structures
	public boolean addDSPPlanToCeNReactionStep(NotebookRef nbRef, String PID, SessionIdentifier identifier) throws StorageException;
	
	
	public void removeNotebookPage(String pageKey,SessionIdentifier identifier) throws StorageException;
	
	
	/**
	 * Updates the object graph in NotebookPage Model.
	 */
	public void updateNotebookPageModel(CompositeCompressedBytes pageModel, SessionIdentifier identifier) throws StorageException;
	
	 /**
   * Updates compressed NotebookPage Model.
   */
	//public void updateNotebookPageModel(CompositeCompressedBytes pageModel,SessionIdentifier identifier) throws StorageException;

	public void deletePlateWells(String[] plateWellKeys,SessionIdentifier identifier) throws StorageException;
	
	/**
	 * Method deletes list of plates.
	 * @param plateKeys
	 * @param identifier
	 * @throws java.rmi.StorageException
	 */
	public void deletePlates(String[] plateKeys,SessionIdentifier identifier) throws StorageException;
	
	/**
	 * @param identifier
	 * @return List of CROPageInfo  objects
	 * @throws StorageException
	 */
	public List<CROPageInfo> getAllCROs(SessionIdentifier identifier) throws StorageException;

	/**
	 * @param croId
	 * @param identifier
	 * @return List of CROPage  objects
	 * @throws StorageException
	 */
	public List<CROPageInfo> getAllChemistsUnderCRO(String croId,SessionIdentifier identifier) throws StorageException;

	/**
	 * @param chemistId
	 * @param identifier
	 * @return List containing the NotebookPageModel with summary information
	 * 			which includes NotebookPageHeader and ReactionScheme information
	 * @throws StorageException
	 */
	public List<NotebookPageModel> getAllPagesWithSummaryUnderChemist(String chemistId,SessionIdentifier identifier) throws StorageException;
	
	
	/**
	 * @param requestId
	 * @param identifier
	 * @return List containing the NotebookPageModel with summary information
	 * 			which includes NotebookPageHeader and ReactionScheme information
	 * @throws DAOException
	 */
	public List<NotebookPageModel> getAllPagesWithSummaryForRequestId(String requestId, SessionIdentifier identifier) throws StorageException ;
	
	/**
	 * @param chemistId
	 * @param identifier
	 * @return List of request ids in string format
	 * @throws DAOException
	 */
	public List<String> getAllRequestIdsForChemist(String chemistId, SessionIdentifier identifier) throws StorageException ;

	/**
	 * @param chemistId vendor_id
	 * @param identifier
	 * @return List with Notebook numbers
	 * @throws StorageException
	 */
	public List getAllNotebooksForChemistId(String chemistId, SessionIdentifier identifier) throws StorageException ;
	
	
	/**
	 * @param nbkNo Notebook number
	 * @param identifier
	 * @return List of pages with summary corresponding to notebookpage number
	 * @throws StorageException
	 */
	public List getAllPagesForNotebook(String nbkNo, SessionIdentifier identifier) throws StorageException;
		
	/**
	 * @param requestId
	 * @param identifier
	 * @return modifiedDate
	 * @throws StorageException
	 */
	public java.util.Date getCROModifiedDate(String requestId, SessionIdentifier identifier) throws StorageException;
	
	/**
	 * @param nbkRef
	 * @param identifier
	 * @return CROPageInfo Object corresponding to NotebookRef 
	 * @throws StorageException
	 */
	public CROPageInfo getCroPageForNBK(NotebookRef nbkRef, SessionIdentifier identifier) throws StorageException; 
		
		
	/**
	 * @param jobModels
	 * @throws StorageException
	 */
	public String[] insertRegistrationJobs(JobModel[] jobModels) throws StorageException;
			
	/**
	 * @param jobModels
	 * @throws StorageException
	 */
	public void updateRegistrationJobs(JobModel[] jobModels,SessionIdentifier session) throws StorageException;
	
	public List<JobModel> getAllRegistrationJobs(String userId, String status) throws StorageException;
	
	/**
	 * @throws StorageException
	 */
	public List getAllRegistrationJobs() throws StorageException;

	/**
	 * @throws StorageException
	 */
	public List getAllRegistrationJobsAndUpdateStatus(String status) throws StorageException;

	/**
	 * @param jobModels
	 * @throws StorageException
	 */
	public void updateBatchJobs(BatchRegInfoModel[] batchModels,SessionIdentifier session) throws StorageException;
	
	/**
	 * @throws StorageException
	 */
	public List getAllBatchJobs(String jobId,SessionIdentifier session) throws StorageException;
	
	
	/**
	 * @return
	 * @throws StorageException
	 */
	public List getAllPendingCompoundRegistrationJobs() throws StorageException	;
	
	/**
	 * @param jobId
	 * @return
	 * @throws StorageException
	 */
	public List getAllCompoundRegistrationJobOffsets(String jobId) throws StorageException;
		
	
	/**
	 * @param pageKey
	 * @param sessionID
	 * @return
	 * @throws StorageException
	 */
	public List<BatchRegInfoModel> getAllRegisteredBatchesForPage(String pageKey, SessionIdentifier sessionID) throws StorageException;
	
	/**
	 * 
	 * @param jobid
	 * @param sessionID
	 * @return
	 * @throws StorageException
	 */
	public List getAllRegisteredBatchesWithJobID(String jobid, SessionIdentifier sessionID) throws StorageException;
	
	
	public JobModel getCeNPlateJob(String plateKey) throws StorageException;
	
	public void addNotebookPage(NotebookPageModel pageModel,SessionIdentifier identifier) throws StorageException;
	
	/**
	 * Used for "Repeat this Experiment," etc. Send a compressed notebook page.
	 * @param nbRef
	 * @param identifier
	 * @throws StorageException
	 */
	//public void addNotebookPage(CompositeCompressedBytes nbRef, SessionIdentifier identifier) throws StorageException;
	
	public ProductBatchModel getProductBatchForBatchNumber(String batchNumber,SessionIdentifier identifier) throws StorageException;
	
}
