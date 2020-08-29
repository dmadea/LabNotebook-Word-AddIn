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
package com.chemistry.enotebook.storage.service;

import com.chemistry.enotebook.design.delegate.DesignServiceDelegate;
import com.chemistry.enotebook.design.delegate.DesignServiceException;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.publisher.classes.PublishEntityInfo;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.NotebookInvalidException;
import com.chemistry.enotebook.storage.dao.*;
import com.chemistry.enotebook.storage.utils.JMSUtils;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ParallelExpDuperDeDuper;
import com.chemistry.enotebook.utils.ParallelExpModelUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Top level implementation class of StorageService. <p/> To whoever implementing: free to further delegate to smaller
 * implementations.
 */
public class StorageServiceImpl // implements StorageService
{
	private static final Log log = LogFactory.getLog(StorageServiceImpl.class);
	
	public static SessionIdentifier ssid = null;
	
	public NotebookPageModel createParallelExperiment(String spid, String notebook, SessionIdentifier identifier) throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		String action = "Create Experiment("+notebook+")";
		identifier.getUserProfile().getHttpMessage().setAction(action);
		identifier.getUserProfile().getHttpMessage().setNotebook(notebook);
		HttpUserProfile userProfile = identifier.getUserProfile();
		
		stopwatch.start("StorageServiceImpl.createParallelExperiment()");
		NotebookPageModel pageModel = null;
		DAOFactory daoFactory = null;
		long starttime = System.currentTimeMillis();
		try {
			log.debug(identifier.getThreadId()+"(" +new Time(System.currentTimeMillis())+")-> Before Calling DSP.");
			DesignServiceDelegate desingLocalref = this.getDesignServiceEJBLocal();
			userProfile.getHttpMessage().setMessage("Retrieving experiment from Design Service");
			
			if (desingLocalref != null) {
				pageModel = desingLocalref.getExperimentFromDesignService(spid, true);
			} else {
				log.info("Unable to get Design Service service reference");
				userProfile.getHttpMessage().setMessage("Failed to retrieve experiment info from Design Service");
			
				return null;
			}
			long halttime1 = System.currentTimeMillis() - starttime;
			log.debug(identifier.getThreadId()+"(" +new Time(System.currentTimeMillis())+")-> getExperimentFromDesignService :"+halttime1/1000+"secs");
			starttime = System.currentTimeMillis();
			log.info("Retrieved data from DSP through Design service");
			// Get next experiment for the notebook
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			int exp = notebookDAO.getNextExperimentForNotebook(notebook);
			long halttime2 = System.currentTimeMillis() - starttime;
			log.debug(identifier.getThreadId()+"(" +new Time(System.currentTimeMillis())+")-> getNextExperimentForNotebook :"+halttime2/1000+"secs");
			starttime = System.currentTimeMillis();
			
			NotebookRef nbRef = new NotebookRef(notebook + "-" + exp);
			if (pageModel != null) {
				pageModel.setNbRef(nbRef);
			}

			// Also set the userid and site code for the page
			pageModel.setUserName(identifier.getUserID());
			pageModel.setBatchOwner(identifier.getUserID());
			pageModel.setBatchCreator(identifier.getUserID());
			pageModel.setSiteCode(identifier.getSiteCode());
			Timestamp ts = CommonUtils.getCurrentTimestamp();
			pageModel.setCreationDateAsTimestamp(ts);
			pageModel.setModificationDateAsTimestamp(ts);
			pageModel.setPageType(CeNConstants.PAGE_TYPE_PARALLEL);
			// Assign notebook batch # for the products
			this.setNotebookBatchNumbers(pageModel, nbRef);
            // Create a PseudoPlate 
			List<ProductBatchModel> list = pageModel.getAllProductBatchModelsInThisPage();
			PseudoProductPlate psPlate = new PseudoProductPlate((ArrayList<ProductBatchModel>)list);
			pageModel.setPseudoProductPlate(psPlate);
			// Sort the MonomerBatch List
			ParallelExpDuperDeDuper deduper = new ParallelExpDuperDeDuper();
			deduper.sortMonomerBatchesInSteps(pageModel);
			userProfile.getHttpMessage().setPage(""+exp);
			userProfile.getHttpMessage().setMessage("Inserting into CeN");
			// Now persist the data to CeN database.
			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			pageModel.setThreadId(identifier.getThreadId());
			// Following method call Participates in Transaction.
			notebookMgr.insertNotebookPage(pageModel,userProfile);
			long halttime3 = System.currentTimeMillis() - starttime;
			log.debug(identifier.getThreadId()+"(" +new Time(System.currentTimeMillis())+")-> insertNotebookPage :"+(halttime3)/1000+"secs");
			userProfile.getHttpMessage().setMessage("Completed Experiment "+exp+" Creation.");
			
			log.info("Completed data persistance using DAOs");
			// Write to file for testing purpose.
			// new CommonUtils().serializeObject(pageModel, pageModel.getNbRef().getNotebookRef());

			// Post CUS message to CUS Q
			JMSUtils jmsUtil = new JMSUtils();
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			jmsUtil.postMessageToCUSQueue(utils.getCUSInfo(pageModel));
			log.info("Completed sending message to CUS Q");
			
		} catch (DAOException daoe) {
			log.error("DAO exception", daoe);
			throw daoe;
		} catch (NotebookInvalidException ne) {
			log.error("Invalid notebook passes", ne);
			throw ne;
		} catch (InvalidNotebookRefException nre) {
			log.error("Invalid notebook ref", nre);
			throw nre;
		} catch (DesignServiceException de) {
			log.error("Error calling DesignServiceLocal", de);
			throw de;
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while insert.", jdbcError);
			throw new DAOException(jdbcError);
		} catch (Exception e) {
			log.error("General Error in storage service.", e);
			throw e;
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}
		return pageModel;
	}

	private DesignServiceDelegate getDesignServiceEJBLocal() {
		return new DesignServiceDelegate();
	}

	/**
	 * @param nbRef
	 * @param PID
	 * @param identifier
	 * @return
	 * @throws Exception
	 * @deprecated need to remove at the earliest to clear confusion as an alternate the method need to be routed thru NotebookDAO
	 * 
	 */
	public boolean addDSPPlanToCeNReactionStep(NotebookRef nbRef, String PID, SessionIdentifier identifier) throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.addDSPPlanToCeNReactionStep()");
		ReactionStepModel stepModel = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			DesignServiceDelegate desingLocalref = this.getDesignServiceEJBLocal();
			if (desingLocalref != null) {
				stepModel = desingLocalref.getReactionStepCompleteDetails(PID);
			} else {
				log.info("Unable to get Design Service reference");
				return false;
			}

			if (stepModel == null)
				return false;

			// Now persist the data to CeN database.
//			NotebookDAO nbkDAO = daoFactory.getNotebookDAO();
			// nbkDAO.createReactionStep(nbRef.getKey(), stepModel);
			log.info("Reaction step(" + stepModel.getStepNumber() + ")add DAO called succesfully for PID:" + PID);
		} catch (Exception dataAccessError) {
			log.error("Failed adding reaction step.", dataAccessError);
			throw dataAccessError;
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return true;
	}

	public NotebookPageModel getNotebookPageExperimentInfo(NotebookRef nbRef, SessionIdentifier identifier) throws Exception {
		NotebookPageModel pageModel = null;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getNotebookPageExperimentInfo()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			HttpUserProfile profile =null;
			String requestedUserID = "SERVICE";
			if(identifier != null)
			{
				profile =identifier.getUserProfile();
				requestedUserID = identifier.getUserID();
			}
			pageModel = notebookDAO.loadNotebookPage(nbRef);
			//Do this in StorageDelegate
			//if (pageModel != null && pageModel.getPageType().compareToIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL) == 0) {
			//	ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			//	utils.linkPlateWellBatchToCorrespondingBatch();
			//}
			/*
			 * Setting the editable property of page to false if the Creator and requester are not same.
			 */
			if (!requestedUserID.equals(pageModel.getUserName()))
			{
				pageModel.setEditable(false);
			} else {
				if(CeNConstants.PAGE_TYPE_PARALLEL.equalsIgnoreCase(pageModel.getPageType()) && !identifier.isSuperUser()) {
					pageModel.setEditable(false);
				}
			}
		} catch (DAOException daoe) {
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
		if (pageModel == null) {
			log.info("After Retrieval notebook page info for " + nbRef.getNotebookRef() + " is null");
		}
		log.info("Retrieved call complete notebook page info for " + nbRef.getNotebookRef());

		stopwatch.stop();
		return pageModel;

	}

	// This is where the lotNumber is created
	private void setNotebookBatchNumbers(NotebookPageModel pageModel, NotebookRef nbRef) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.setNotebookBatchNumbers() - " + nbRef.getNotebookRef());
		if (pageModel != null && nbRef != null) {
			ArrayList<ReactionStepModel> steps = pageModel.getIntermediateReactionSteps();
			ReactionStepModel sumStep = pageModel.getSummaryReactionStep();
			// If only summary step is present
			if (steps != null && steps.size() < 2 && sumStep.getMonomers() != null) {
				ArrayList<BatchesList<ProductBatchModel>> productBatchesList = sumStep.getProducts();
				if (productBatchesList != null) {
					int size = productBatchesList.size();
					int lot = 1;
					for (int i = 0; i < size; i++) {
						BatchesList<ProductBatchModel> prodList = productBatchesList.get(i);
						for (int k = 0; k < prodList.getBatchModels().size(); k++) {
							ProductBatchModel model = prodList.getBatchModels().get(k);
							try {
								model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
							} catch (Exception e) {
								log.error("Error creating and assigning Notebook Batch number " + e.getMessage());
							}
							lot = lot + 1;
						}
					}
					log.info("Final lot number after notebook batch assignment: " + lot);
				}

			}// end if
			// if has intermediate steps
			// Intermediate steps products should be assigned Notebook Batch#
			else {
				// Assign NB batch# first for step1 to last but one of final step.
				int lot = 1;
				int totalInterimSteps = steps.size();
				int finalStepNo = totalInterimSteps;
				// start numbering from last step
//				for (int i = 1; i < totalInterimSteps; i++) {
//
//					ReactionStepModel step = (ReactionStepModel) pageModel.getReactionStep(i);
//					ArrayList productBatchesList = step.getProducts();
//					if (productBatchesList != null) {
//						int size = productBatchesList.size();
//						for (int m = 0; m < size; m++) {
//							BatchesList prodList = (BatchesList) productBatchesList.get(m);
//							for (int k = 0; k < prodList.getBatchModels().size(); k++) {
//								ProductBatchModel model = (ProductBatchModel) prodList.getBatchModels().get(k);
//								try {
//									model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
//								} catch (Exception e) {
//									log.error("Error creating and assigning Note book Batch number" + e.getMessage());
//								}
//								lot = lot + 1;
//							}
//						}
//					}
//
//				}
				// Assign same NB batch# for summary products and final step products
				ReactionStepModel finalstep = pageModel.getReactionStep(finalStepNo);
				ArrayList<BatchesList<ProductBatchModel>>  productBatchesList = finalstep.getProducts();
				//ArrayList finalproductBatchesList = sumStep.getProducts();
				//
				for (int n = 0; n < productBatchesList.size(); n++) {
					BatchesList<ProductBatchModel> prodList = productBatchesList.get(n);
					for (int k = 0; k < prodList.getBatchModels().size(); k++) {
						ProductBatchModel model = prodList.getBatchModels().get(k);
						try {
							model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
						} catch (Exception e) {
							log.error("Error creating and assigning Note book Batch number " + e.getMessage());
						}
						lot = lot + 1;
					}
				}
				
				for (int i = 1; i < totalInterimSteps; i++) {

					ReactionStepModel step = pageModel.getReactionStep(i);
					productBatchesList = step.getProducts();
					if (productBatchesList != null) {
						int size = productBatchesList.size();
						for (int m = 0; m < size; m++) {
							BatchesList<ProductBatchModel> prodList = productBatchesList.get(m);
							for (int k = 0; k < prodList.getBatchModels().size(); k++) {
								ProductBatchModel model = prodList.getBatchModels().get(k);
								try {
									model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
								} catch (Exception e) {
									log.error("Error creating and assigning Note book Batch number " + e.getMessage());
								}
								lot = lot + 1;
							}
						}
					}

				}
				
				log.info("Final lot number after notebook batch assignment: " + lot);
			}// else
		}// if has data

		stopwatch.stop();
	}

	public void removeExperiment(String pageKey, Timestamp modifiedDate) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.removeExperiment()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			notebookDAO.deleteExperiment(pageKey, modifiedDate);
		} catch (DAOException daoe) {
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		log.info("Removed Experiment with pageKey: " + pageKey);

	}

	/*
	 * Updates NotebookPage Header in Database
	 */
	public void updateNotebookPage(NotebookPageModel pageModel,SessionIdentifier identifier) throws DAOException,Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.updateNotebookPageHeader()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			notebookMgr.updateNotebookPage(pageModel,identifier == null ? null : identifier.getUserProfile());
			
			//Send the struct updates to CUS
			sendTheStructUpdatesToCUS(pageModel);
			
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while update.", jdbcError);
			throw new DAOException(jdbcError);
		} catch (Exception de) {
			log.error("Unexpected Exception", de);
			throw de;
		}finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		log.info("Updated Experiment header for :" + pageModel.getNbRef());
	}
	
	private void sendTheStructUpdatesToCUS(NotebookPageModel pageModel) throws Exception {
		ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
		ArrayList<PublishEntityInfo>  cusUpdatesList = utils.getCUSInfo(pageModel);
		if (cusUpdatesList != null && cusUpdatesList.size() > 0) {
			JMSUtils jmsUtil = new JMSUtils();
			jmsUtil.postMessageToCUSQueue(cusUpdatesList);
		} else			{
			log.debug("No updates to CUS");	
		}
	}

	public NotebookPageModel createConceptionRecord(String notebook, SessionIdentifier identifier) throws DAOException {

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.createConceptionRecord()");
		NotebookPageModel pageModel = new NotebookPageModel();
		ReactionStepModel emptyStepModel = new ReactionStepModel();
		emptyStepModel.setRxnScheme(new ReactionSchemeModel());
		pageModel.addReactionStep(emptyStepModel);
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			// Get next experiment for the notebook
			NotebookDAO nbkDAO = daoFactory.getNotebookDAO();
			int exp = nbkDAO.getNextExperimentForNotebook(notebook);

			NotebookRef nbRef = new NotebookRef(notebook + "-" + exp);
			pageModel.setNbRef(nbRef);

			// Also set the userid and site code for the page
			pageModel.setUserName(identifier.getUserID());
			pageModel.setBatchOwner(identifier.getUserID());
			pageModel.setBatchCreator(identifier.getUserID());
			pageModel.setSiteCode(identifier.getSiteCode());

			pageModel.setPageType(CeNConstants.PAGE_TYPE_CONCEPTION);
			pageModel.setPageStatus(CeNConstants.PAGE_STATUS_OPEN);
			pageModel.setCenVersion("1");
			pageModel.setLatestVersion(CeNConstants.PAGE_LATEST_VERSION_YES);
			// Both should have same timestamp first time
			Timestamp ts = CommonUtils.getCurrentTimestamp();
			pageModel.setCreationDateAsTimestamp(ts);
			pageModel.setModificationDateAsTimestamp(ts);

			// Now persist the data to CeN database.
			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			notebookMgr.insertNotebookPage(pageModel,identifier.getUserProfile());
			log.info("Completed data persistance using DAOs");
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while insert.", jdbcError);
			throw new DAOException(jdbcError);
		} catch (InvalidNotebookRefException e) {
			log.error("InvalidNotebookRefException exception", e);
			throw new DAOException("InvalidNotebookRefException", e);
		} catch (NotebookInvalidException ne) {
			log.error("NotebookInvalidException exception", ne);
			throw new DAOException("NotebookInvalidException", ne);
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}
		return pageModel;
	}

	public NotebookPageModel createSingletonExperiment(String notebook, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.createSingletonExperiment()");
		NotebookPageModel pageModel = new NotebookPageModel();
		ReactionStepModel emptyStepModel = new ReactionStepModel();
		emptyStepModel.setRxnScheme(new ReactionSchemeModel());
		pageModel.addReactionStep(emptyStepModel);
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			// Get next experiment for the notebook
			NotebookDAO nbkDAO = daoFactory.getNotebookDAO();
			int exp = nbkDAO.getNextExperimentForNotebook(notebook);

			NotebookRef nbRef = new NotebookRef(notebook + "-" + exp);
			pageModel.setNbRef(nbRef);

			// Also set the userid and site code for the page
			pageModel.setUserName(identifier.getUserID());
			pageModel.setBatchOwner(identifier.getUserID());
			pageModel.setBatchCreator(identifier.getUserID());
			pageModel.setSiteCode(identifier.getSiteCode());

			pageModel.setPageType(CeNConstants.PAGE_TYPE_MED_CHEM);
			pageModel.setPageStatus(CeNConstants.PAGE_STATUS_OPEN);
			pageModel.setCenVersion("1");
			pageModel.setLatestVersion(CeNConstants.PAGE_LATEST_VERSION_YES);
			// Both should have same timestamp first time
			Timestamp ts = CommonUtils.getCurrentTimestamp();
			pageModel.setCreationDateAsTimestamp(ts);
			pageModel.setModificationDateAsTimestamp(ts);

			// Now persist the data to CeN database.
			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			notebookMgr.insertNotebookPage(pageModel,identifier.getUserProfile());
			log.info("Completed data persistance using DAOs");
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while insert", jdbcError);
			throw new DAOException("Database error while performing insert", jdbcError);
		} catch (InvalidNotebookRefException e) {
			log.error("InvalidNotebookRefException exception", e);
			throw new DAOException("InvalidNotebookRefException", e);
		} catch (NotebookInvalidException ne) {
			log.error("NotebookInvalidException exception", ne);
			throw new DAOException("NotebookInvalidException", ne);
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}
		return pageModel;
	}

	public void deletePlateWells(String[] plateWellKeys, SessionIdentifier identifier) throws DAOException {
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookManager notebookMgr = daoFactory.getNotebookManager();// getNotebookDAO();
			notebookMgr.deletePlateWells(plateWellKeys, identifier.getUserProfile());
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while delete Plate Wells.", jdbcError);
			throw new DAOException("Database error while delete Plate Wells.", jdbcError);
		} catch (DAOException e) {
			log.error("General Error in storage service.", e);
			throw new DAOException("General Error in storage service: " + e.getMessage(), e);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public void deletePlates(String[] plateKeys, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.deletePlates()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookManager notebookMgr = daoFactory.getNotebookManager();// getNotebookDAO();
			notebookMgr.deletePlates(plateKeys,identifier.getUserProfile());
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while delete Plates.", jdbcError);
			throw new DAOException("Database error while delete Plates.", jdbcError);
		} catch (DAOException e) {
			log.error("General Error in storage service:" + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service: " + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}
	}

	/*
	 * @return Map containing cro id as key and value as cro display name
	 */
	public ArrayList getAllCROs(SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getAllCROs()");
		ArrayList<CROPageInfo> croList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			croList = croPageDAO.getAllCros();
		} catch (DAOException e) {
			log.error("General Error in storage service.", e);
			throw new DAOException("General Error in storage service: " + e.getMessage(), e);
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return croList;
	}

	/*
	 * @ return Map containing key as chemistId and value containing displayName
	 */
	public ArrayList getAllChemistsUnderCRO(String croId, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getChemistsForCRO()");
		ArrayList chemistList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			chemistList = croPageDAO.getAllChemistsForCro(croId);
		} catch (DAOException e) {
			log.error("General Error in storage service: " + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service: " + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return chemistList;
	}

	/*
	 * @ return List containing the NotebookPageModel with summary information which includes NotebookPageHeader and ReactionScheme
	 * information
	 */
	public ArrayList getAllPagesWithSummaryUnderChemist(String chemistId, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getAllPagesWithSummaryUnderChemist()");
		ArrayList pagesList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			pagesList = croPageDAO.getAllPagesForChemist(chemistId);
		} catch (DAOException e) {
			log.error("General Error in storage service: " + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service: " + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return pagesList;
	}

	/*
	 * @ return List containing the NotebookPageModel with summary information which includes NotebookPageHeader and ReactionScheme
	 * information
	 */
	public ArrayList getAllPagesWithSummaryForRequestId(String requestId, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getAllPagesWithSummaryForRequestId()");
		ArrayList pagesList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			pagesList = croPageDAO.getAllPagesForRequestId(requestId);
		} catch (DAOException e) {
			log.error("General Error in storage service: " + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service: " + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return pagesList;
	}

	
	/*
	 * @ return List containing the requestId's under the chemist
	 * 
	 */
	public ArrayList getAllRequestIdsForChemist(String chemistId, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getAllPagesWithSummaryForRequestId()");
		ArrayList pagesList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			pagesList = croPageDAO.getAllRequestIdsForChemist(chemistId);
		} catch (DAOException e) {
			log.error("General Error in storage service.", e);
			throw new DAOException("General Error in storage service: " + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return pagesList;
	}

	
		
	/**
	 * @return List containing the notebook numbers under given croId
	 * 
	 */
	public List getAllNotebooksForChemistId(String chemistId, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getAllNotebooksForChemistId()");
		List notebookList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			notebookList = croPageDAO.getAllNotebooksForChemistId(chemistId);
		} catch (DAOException e) {
			log.error("General Error in storage service.", e);
			throw new DAOException("General Error in storage service:" + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return notebookList;
	}
	
	public List getAllPagesForNotebook(String nbkNo, SessionIdentifier identifier) throws DAOException{
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getAllNotebooksForCroId()");
		List pageList = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			pageList = croPageDAO.getAllPagesForNotebook(nbkNo);
		} catch (DAOException e) {
			log.error("General Error in storage service:" + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service:" + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return pageList;
	}
	/**
	 * @return Date, modified date corresponding to the requestId
	 * 
	 */
	public java.util.Date getCROModifiedDate(String requestId, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getCROModifiedDate()");
		java.util.Date modifiedDate = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			modifiedDate = croPageDAO.getCROModifiedDate(requestId);
		} catch (DAOException e) {
			log.error("General Error in storage service:" + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service:" + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return modifiedDate;
	}
	
	/**
	 * @return CROPageInfo corresponding to the notebook ref 
	 *  
	 */
	public CROPageInfo getCroPageForNBK(NotebookRef nbkRef, SessionIdentifier identifier) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getCroPageForNBK()");
		CROPageInfo croInfo = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			croInfo = croPageDAO.getCroPageForNBK(nbkRef);
		} catch (DAOException e) {
			log.error("General Error in storage service:" + CommonUtils.getStackTrace(e));
			throw new DAOException("General Error in storage service:" + e.getMessage());
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return croInfo;
	}
	
		
	public void createCeNExperiment(NotebookPageModel pageModel) throws DAOException {
		DAOFactory daoFactory = null;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.createCeNExperiment()");
		try {
        	 //Both should have same timestamp first time
			Timestamp ts = CommonUtils.getCurrentTimestamp();
			pageModel.setCreationDateAsTimestamp(ts);
			pageModel.setModificationDateAsTimestamp(ts);
			
			daoFactory = DAOFactoryManager.getDAOFactory();
			// Now persist the data to CeN database.
			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			HttpUserProfile dummyProfile = new HttpUserProfile("NONE",0,"NONE");
			notebookMgr.insertNotebookPage(pageModel,dummyProfile);
			// Update batchRegInfos
			List<ProductBatchModel> batchModels = pageModel.getAllProductBatchModelsInThisPage();
			List<BatchRegInfoModel> regInfos = new ArrayList<BatchRegInfoModel>();
			for (ProductBatchModel batch : batchModels) {
				if (batch != null) {
					regInfos.add(batch.getRegInfo());
				}
			}
			updateBatchJobs(regInfos.toArray(new BatchRegInfoModel[0]));
			log.info("Completed data persistance using DAOs");
			
			sendTheStructUpdatesToCUS(pageModel);
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Database error while insert.");
			log.error(CommonUtils.getStackTrace(jdbcError));
			throw new DAOException(jdbcError);
		} catch (Exception de) {
			log.error("Unexpected Exception", de);
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

	}
	
	public void removeCRORequestId(String requestId) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.removeCRORequestId()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croPageDAO = daoFactory.getCroPageDAO();
			croPageDAO.deletePages(requestId);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		log.info("Removed CRO Pages with requestId :" + requestId);

	}
	
	public String[] insertRegistrationJobs(JobModel[] jobModels) throws DAOException{
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling insertRegJob on jobDAO");
			log.info("Inserted Registration Job!");
			return jobDAO.insertRegistrationJobs(jobModels);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}

	/**
	 * @param jobModels
	 * @throws RemoteException
	 */
	public void updateRegistrationJobs(JobModel[] jobModels) throws DAOException{
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling updatePlateJobs on jobDAO");
			jobDAO.updateRegistrationJobs(jobModels);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("Updated Plate Job!");
	}

	public JobModel getRegistrationJob(String jobId) throws DAOException {
		DAOFactory daoFactory = null;
		JobModel job = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getRegistrationJob on jobDAO");
			job = jobDAO.getRegistrationJob(jobId);
		} catch (DAOException e) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(e));
			log.error("DAO exception", e);
			throw e;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		return job;
	}
	
	public List<JobModel> getAllRegistrationJobs(String userId, String status) throws DAOException {
		DAOFactory daoFactory = null;
		List<JobModel> jobs = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getAllRegistrationJobs on jobDAO");
			jobs = jobDAO.getAllRegistrationJobs(userId, status);
		} catch (DAOException e) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(e));
			log.error("DAO exception", e);
			throw e;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		return jobs;
	}
	
	/**
	 * @throws RemoteException
	 */
	public List getAllRegistrationJobs() throws DAOException{
		DAOFactory daoFactory = null;
		List jobs = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getAllRegistrationJobs on jobDAO");
			jobs = jobDAO.getAllRegistrationJobs();
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all plate Jobs!");
		return jobs;
	}

	/**
	 * @throws RemoteException
	 */
	public List getAllRegistrationJobsAndUpdateStatus(String status) throws DAOException{
		DAOFactory daoFactory = null;
		List jobs = null;
		try {
			//TODO is it really executing in the same transaction???
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getAllRegistrationJobs on jobDAO");
			jobs = jobDAO.getAllRegistrationJobs(true);
			JobModel[] jobsModels = new JobModel[jobs.size()]; 
			jobs.toArray(jobsModels);
			for (int i = 0; i < jobsModels.length; i++) {
				jobsModels[i].setJobStatus(status);
			}
			jobDAO.updateRegistrationJobs(jobsModels);			
		} catch (DAOException daoe) {
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all plate Jobs!");
		return jobs;
	}
	

	/**
	 * @param jobModels
	 * @throws RemoteException
	 */
	public void updateBatchJobs(BatchRegInfoModel[] batchModels) throws DAOException{
		DAOFactory daoFactory = null;
		log.debug("updateBatchJobs.enter");
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling updateBatchJob on jobDAO");
			jobDAO.updateBatchJobs(batchModels);
		} catch (DAOException daoe) {
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.debug("updateBatchJobs.exit");
	}

	/**
	 * @throws RemoteException
	 */
	public List getAllBatchJobs(String jobId) throws DAOException{
		DAOFactory daoFactory = null;
		List batchJobs = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getAllBatchJobs on jobDAO");
			batchJobs = jobDAO.getAllBatchJobs(jobId);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all batch Jobs!");
		return batchJobs;
	}
	
	/**
	 * @return
	 * @throws RemoteException
	 */
	public List getAllPendingCompoundRegistrationJobs() throws DAOException	{
		DAOFactory daoFactory = null;
		List jobIds = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getAllPendingCompoundRegistrationJobs on jobDAO");
			jobIds = jobDAO.getAllPendingCompoundRegistrationJobs();
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all pendindg CompoundRegistration Jobs!");
		return jobIds;

	}
	
	/**
	 * @param jobId
	 * @return
	 * @throws RemoteException
	 */
	public List getAllCompoundRegistrationJobOffsets(String jobId) throws DAOException{
		DAOFactory daoFactory = null;
		List jobOffsets = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getAllOffsets on jobDAO");
			jobOffsets = jobDAO.getAllCompoundRegistrationJobOffsets(jobId);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all job offsets!");
		return jobOffsets;

	}
	
	
	/**
	 * @param pageKey
	 * @param sessionID
	 * @return
	 * @throws DAOException
	 */
	public List<BatchRegInfoModel> getAllRegisteredBatchesForPage(String pageKey, SessionIdentifier sessionID) throws DAOException{
		DAOFactory daoFactory = null;
		List<BatchRegInfoModel> registeredBatches = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookLoadDAO loadDAO = daoFactory.getNotebookLoadDAO();
			log.info("Calling loadAllRegisteredBatchesForPage on jobDAO");
			registeredBatches = loadDAO.loadAllRegisteredBatchesForPage(pageKey);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all registeredBatches!");
		return registeredBatches;
	}
	
	
	public List getAllRegisteredBatchesWithJobID(String jobid, SessionIdentifier sessionID) throws DAOException{
		DAOFactory daoFactory = null;
		List registeredBatches = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookLoadDAO loadDAO = daoFactory.getNotebookLoadDAO();
			log.info("Calling getAllRegisteredBatchesWithJobID on NotebookLoadDAO");
			registeredBatches = loadDAO.getAllRegisteredBatchesWithJobID(jobid);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("returning all registeredBatches!");
		return registeredBatches;
	}
	
	public JobModel getCeNPlateJob(String plateKey) throws DAOException
	{
		log.info("getCeNPlateJob().enter");
		DAOFactory daoFactory = null;
		JobModel jobmodel = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			JobDAO jobDAO = daoFactory.getJobDAO();
			log.info("Calling getCeNPlateJob on jobDAO");
			jobmodel = jobDAO.getCeNPlateJob(plateKey);
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}

		log.info("getCeNPlateJob().exit");
		return jobmodel;
	}
	
	public void addParallelExperimentPage(NotebookPageModel pageModel, SessionIdentifier identifier) throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		String action = "addParallelExperimentPage("+pageModel.getNbRef().getNbNumber()+")";
		identifier.getUserProfile().getHttpMessage().setAction(action);
		identifier.getUserProfile().getHttpMessage().setNotebook(pageModel.getNbRef().getNbNumber());
		HttpUserProfile userProfile = identifier.getUserProfile();
		
		stopwatch.start("StorageServiceImpl.addParallelExperimentPage()");
		DAOFactory daoFactory = null;
		long starttime = System.currentTimeMillis();
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			// Now persist the data to CeN database.
			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			pageModel.setThreadId(identifier.getThreadId());
			// Following method call Participates in Transaction.
			notebookMgr.insertNotebookPage(pageModel,userProfile);
			long halttime3 = System.currentTimeMillis() - starttime;
			log.debug(identifier.getThreadId()+"(" +new Time(System.currentTimeMillis())+")-> insertNotebookPage :"+(halttime3)/1000+"secs");
			userProfile.getHttpMessage().setMessage("Completed Experiment "+pageModel.getNbRef().getNbPage()+" Creation.");
			log.info("Completed data persistance using DAOs");

			// Post CUS message to CUS Q
			JMSUtils jmsUtil = new JMSUtils();
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			jmsUtil.postMessageToCUSQueue(utils.getCUSInfo(pageModel));
			log.info("Completed sending message to CUS Q");
			
		} catch (DAOException daoe) {
			daoe.printStackTrace();
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			log.error(CommonUtils.getStackTrace(daoe));
			throw daoe;
		} catch (JDBCRuntimeException jdbcError) {
			log.error("Data base error while insert.");
			log.error(CommonUtils.getStackTrace(jdbcError));
			throw new DAOException(jdbcError);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("General Error in storage service:" + CommonUtils.getStackTrace(e));
			throw e;
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}
		
	}
	
	public ProductBatchModel getProductBatchForBatchNumber(String batchNumber,SessionIdentifier identifier) throws Exception
	{
		ProductBatchModel prodModel = null;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.getProductBatchForBatchNumber()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			prodModel = notebookDAO.loadProductBatchModel(batchNumber);
		
		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			log.error("DAO exception", daoe);
			throw daoe;
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
		if (prodModel == null) {
			log.info("After Retrieval ProductBatchModel for" + batchNumber + " is null");
		}
		log.info("Retrieved call complete for" + batchNumber);

		stopwatch.stop();
		return prodModel;
		
	}

}