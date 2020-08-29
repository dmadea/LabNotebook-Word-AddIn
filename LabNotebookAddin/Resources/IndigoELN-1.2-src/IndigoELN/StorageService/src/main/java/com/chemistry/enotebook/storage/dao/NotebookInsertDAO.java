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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.NotebookInsert;
import com.chemistry.enotebook.storage.jdbc.insert.NotebookPageInsert;
import com.chemistry.enotebook.storage.jdbc.insert.ReactionSchemeInsert;
import com.chemistry.enotebook.storage.jdbc.insert.ReactionStepInsert;
import com.chemistry.enotebook.storage.query.InsertQueryGenerator;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotebookInsertDAO extends StorageDAO {
	private static final Log log = LogFactory.getLog(NotebookInsertDAO.class);
	
	private HttpUserProfile userProfile = null;

	/*
	 * Inserts NotebookPage in CEN_Pages table and returns boolean indicating the success or failure of the insert operation.
	 * 
	 * @return boolean true if insert was success.
	 */
	public void insertNotebookPage(NotebookPageModel page) throws DAOException {

		log.debug("Setting AUTOCOMMIT to false");

		NotebookPageInsert insert = new NotebookPageInsert(this.getDataSource());

		Stopwatch stopwatch = new Stopwatch();
		try {

			log.debug(" Before Inserting NotebookPage ");
			stopwatch.start("NotebookInsertDAO.insertNotebookPage ");
			
			String pageKey = page.getKey();
			String nbkNumber = "";
			String expNumber = "";
			long starttime = System.currentTimeMillis();
			if (CommonUtils.isNotNull(page.getNbRef())) {
				nbkNumber = page.getNbRef().getNbNumber();
				expNumber = page.getNbRef().getNbPage();
			}

			String userName = page.getUserName();
			if (userName != null) {
				userName = userName.toUpperCase();
			}
			// userProfile.getHttpMessage().setMessage("Creating Header");
			// CommonUtils.sendMessageToClient(userProfile.getHttpMessage(), userProfile.getUrl());
			log.debug("Creation date timestamp:" + page.getCreationDate());
			log.debug("PageFK1 --> NBK:" + nbkNumber + " Site:" + page.getSiteCode() + " Username:" + userName);
			
			insert.compile();
			insert.update(new Object[] { pageKey, page.getSiteCode(), nbkNumber, expNumber, userName, userName, page.getPageType(),
					page.getStatus(), page.getCreationDateAsTimestamp(), page.getModificationDateAsTimestamp(),
					CommonUtils.replaceSpecialCharsinXML(page.toXML()),
					new Double(page.getVersion()), page.getLatestVersion(), page.getPageHeader().getSpid(), page.getTaCode(),
					page.getProjectCode(), CommonUtils.replaceSpecialCharsInText(page.getLiteratureRef()), page.getSubject(),
					page.getSeriesID(), page.getProtocolID(), page.getBatchOwner(), page.getBatchCreator(),
					page.getDesignSubmitter(), page.getProcedure()});
			
			long halttime1 = System.currentTimeMillis() - starttime;
			log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> InsertNBKPageHeader :" + halttime1
					+ "msecs");

			starttime = System.currentTimeMillis();
			CROPageInfo cro = page.getCroInfo();
			if (cro != null) {
				String requestId = cro.getRequestId();
				if (CommonUtils.isNotNull(requestId) && !requestId.equals("0")) {
					CroPageDAO croDao = this.getDaoFactory().getCroPageDAO();
					croDao.insertCROPageInfo(cro);
				}
				long halttimecro = System.currentTimeMillis() - starttime;
				log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> Insert CRO :" + halttimecro
						+ "msecs");
			}
			starttime = System.currentTimeMillis();
			if (page.getReactionSteps().size() > 0) {
				log.debug("Inserting " + page.getReactionSteps().size() + " Reaction Steps ");
				List summarySteps = new ArrayList();
				summarySteps.add(page.getSummaryReactionStep());
				// First do Summary
				createReactionSteps(pageKey, summarySteps, page.getThreadId());
				long halttime2 = System.currentTimeMillis() - starttime;
				log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> InsertSummaryStep :" + halttime2
						+ "msecs");
				// Then do all Intermediate steps
				starttime = System.currentTimeMillis();
				createReactionSteps(pageKey, page.getIntermediateReactionSteps(), page.getThreadId());
				long halttime3 = System.currentTimeMillis() - starttime;
				log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> InsertIntermediateSteps :"
						+ halttime3 + "msecs");
			}
			AnalysisCacheModel analysisCache = page.getAnalysisCache();
			AnalysisDAO analysisDAO = this.getDaoFactory().getAnalysisDAO();
			if (analysisCache != null) {
				analysisDAO.insertAnalysisList(analysisCache.getAnalyticalList(), pageKey);
			}

			long halttime4 = System.currentTimeMillis() - starttime;
			log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> InsertAnalysis :" + halttime4 / 1000
					+ "secs");
			starttime = System.currentTimeMillis();
			AttachmentCacheModel attachmentCache = page.getAttachmentCache();
			AttachmentDAO attachmentDAO = this.getDaoFactory().getAttachmentDAO();
			if (attachmentCache != null) {
				attachmentDAO.insertAttachmentList(attachmentCache.getAttachmentList(), pageKey);
			}
			long halttime5 = System.currentTimeMillis() - starttime;
			log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> InsertAttachment :" + halttime5 / 1000
					+ "secs");

			ProcedureImageDAO procedureImageDAO = getDaoFactory().getProcedureImageDAO();
			if (procedureImageDAO != null) {
				procedureImageDAO.updateNotebookProcedureImages(page.getPageHeader());
			}
			
			// If parallel exp insert the Pseudo plate once first time exp is created
			if (page.isParallelExperiment()) {
				starttime = System.currentTimeMillis();
				int wellSize = 0;
				PseudoProductPlate psPlate = page.getPseudoProductPlate(false);
				if (psPlate == null) {
					log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis())
							+ ")-> PSEUDOPLATE is null for this page");
					return;
				}
				PlateWell[] list = psPlate.getWells();
				// Insert the Pseudo plate for the NBK page
				PlateDAO dao = getDaoFactory().getPlateDAO();
				dao.insertProductPlate(pageKey, "", psPlate);
				if (list != null && list.length > 0) {
					wellSize = list.length;
					// Only wells.There will not be PurificationService data at this point
					dao.insertPlateWellsUsingSpringBatching(psPlate.getKey(), psPlate.getWells());
				}
				long halttime6 = System.currentTimeMillis() - starttime;
				log.info(page.getThreadId() + "(" + new Time(System.currentTimeMillis()) + ")-> InsertPSEUDOPLATE with " + wellSize
						+ " Wells took:" + halttime6 + " msecs");

			}

		} catch (Exception updateError) {
			throw new JDBCRuntimeException(updateError);
		} finally {
			stopwatch.stop();
		}
	}

	/*
	 * Inserts Reaction Steps in CEN_Reaction_Steps table and returns List of keys corresponding to the Reaction Steps which were
	 * successfully inserted.
	 * 
	 * @return List of Step_keys inserted successfully.
	 */
	public void createReactionSteps(String pageKey, List steps, String threadId) throws DAOException {
		ReactionStepModel step = null;
		ArrayList rxnSchemes = new ArrayList();

		for (int i = 0; i < steps.size(); i++) {
			log.debug("Inloop adding ReactionStep Count " + i);
			step = (ReactionStepModel) steps.get(i);
			rxnSchemes.add(step.getRxnScheme());
		}
		long starttime = System.currentTimeMillis();
		this.insertReactionScheme(pageKey, rxnSchemes);
		long halttime3 = System.currentTimeMillis() - starttime;
		log.info(threadId + "(" + new Time(System.currentTimeMillis()) + ")-> insertReactionScheme :" + halttime3 + "msecs");

		this.insertReactionSteps(pageKey, steps, threadId);

	}

	/*
	 * Inserts Reaction Step in CEN_Reaction_Steps table and returns boolean indicating the success or failure of the insert
	 * operation.
	 * 
	 * @return boolean true if insert was success.
	 */
	private void insertReactionSteps(String pageKey, List steps, String threadId) throws DAOException {

		log.debug("Before inserting ReactionStep---:");
		ReactionStepInsert insert = new ReactionStepInsert(this.getDataSource());
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookInsertDAO.insertReactionSteps() ");

		ReactionStepModel step = null;
		try {
			for (int i = 0; i < steps.size(); i++) {
				step = (ReactionStepModel) steps.get(i);
				long starttime = System.currentTimeMillis();
				String stepKey = step.getKey();
				insert.update(new Object[] { stepKey, // STEP_KEY
						pageKey, // PAGE_KEY
						new Integer(step.getStepNumber()), // SEQ_NUM
						step.getRxnScheme().getKey(), // RXN_SCHEME_KEY
						CommonUtils.replaceSpecialCharsinXML(step.toXML()), // XML_METADATA
				});

				log.debug(" ReactionStep with rxn_step_key" + step.getKey() + " Inserted SUCESSFULLY");
				long halttime1 = System.currentTimeMillis() - starttime;
				log.debug(threadId + "(" + new Time(System.currentTimeMillis()) + ")-> insertReactionSTEP :" + halttime1 / 1000
						+ "secs");
				starttime = System.currentTimeMillis();
				if (step.getMonomers().size() > 0) {
					ArrayList monomerList = step.getMonomers();
					createParallelMonomerBatches(stepKey, pageKey, monomerList, step.isSummaryStep());

					log.debug("Inserted " + monomerList.size() + " Monomer Batches successfully.");
				} else {
					log.debug(" Monomer List empty -- No Insert");
				}
				long halttime2 = System.currentTimeMillis() - starttime;
				log.debug(threadId + "(" + new Time(System.currentTimeMillis()) + ")-> insertMONOMERBatches :" + halttime2 / 1000
						+ "secs");
				starttime = System.currentTimeMillis();
				if (step.getProducts().size() > 0) {
					ArrayList productList = step.getProducts();
					this.createParallelProductBatches(stepKey, pageKey, productList, step.isSummaryStep());
					log.debug("Inserted " + step.getProducts().size() + " Product Batches successfully.");
				} else {
					log.debug(" Product List empty -- No Insert");
				}
				long halttime3 = System.currentTimeMillis() - starttime;
				log.debug(threadId + "(" + new Time(System.currentTimeMillis()) + ")-> insertPRODUCTBatches :" + halttime3 / 1000
						+ "secs");

				starttime = System.currentTimeMillis();

				if (step.getStoicBatchesList().getListSize() > 0) {
					this.createStoicMonomerBatches(stepKey, pageKey, step.getStoicBatchesList());
					log.debug("Inserted " + step.getProducts().size() + " Product Batches successfully.");
				} else {
					log.debug(" Product List empty -- No Insert");
				}
				long halttime4 = System.currentTimeMillis() - starttime;
				log.debug(threadId + "(" + new Time(System.currentTimeMillis()) + ")-> insertSTOICMONOMERBatches :" + halttime4 / 1000
						+ "secs");
			}

		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		} finally {
			stopwatch.stop();
		}
	}

/**
 * Checks of the batches are for update or for insert and performs the tasks.
 * 
 * @param pageKey
 * @param stepKey
 * @param batchesListObj
 * @throws DAOException
 */
private void insertStoicBatch(String pageKey, String stepKey, BatchesList batchesListObj) throws DAOException {
	DAOFactory factory = null;
	try {
		factory = DAOFactoryManager.getDAOFactory();
		List batches = batchesListObj.getBatchModels();
		ArrayList insertableBatches = new ArrayList(batches.size());
		for (int i = 0; i < batches.size(); i++) {
			MonomerBatchModel batch = (MonomerBatchModel) batches.get(i);
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());

			insertableBatches.add(batch);

		}
		if (insertableBatches.size() > 0) {
			NotebookInsertDAO insert = factory.getNotebookInsertDAO();
			batchesListObj.setBatchModels(insertableBatches);
			insert.createStoicMonomerBatches(stepKey, pageKey, batchesListObj);
		}
	} catch (Exception updateError) {
		throw new JDBCRuntimeException(updateError);
	} finally {
		DAOFactoryManager.release(factory);
	}

}


	/*
	 * Inserts Reaction Scheme in CEN_Reaction_Schemes table and returns boolean indicating the success or failure of the insert
	 * operation.
	 * 
	 * @return boolean true if insert was success.
	 */

	private void insertReactionScheme(String pageKey, List rxnSchemes) throws DAOException {
		log.debug("----------Inserting Reaction Scheme-------");

		ReactionSchemeInsert insert = new ReactionSchemeInsert(this.getDataSource());

		ReactionSchemeModel rxnScheme = null;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookInsertDAO.insertReactionScheme() " + rxnScheme);
		try {
			for (int i = 0; i < rxnSchemes.size(); i++) {
				rxnScheme = (ReactionSchemeModel) rxnSchemes.get(i);
				// log.debug("Inserted Reaction Scheme XML:\n"+rxnScheme.toXML());
				String key = rxnScheme.getKey();

				insert.update(new Object[] {
						key, // RXN_SCHEME_KEY
						pageKey, // PAGE_KEY
						rxnScheme.getReactionType(), // REACTION_TYPE
						rxnScheme.getStringSketch(),
						rxnScheme.getNativeSketch(), 
						rxnScheme.getViewSketch(),
						CommonUtils.replaceSpecialCharsinXML(rxnScheme.toXML()), // XML_METADATA
						rxnScheme.getVrxId(), // VRXN_ID
						rxnScheme.getProtocolId() // PROTOCOL_ID
						});
				log.debug("Inserted Reaction Schemes SUCCESS!! with key=" + rxnScheme.getKey());
			}
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		} finally {
			stopwatch.stop();
		}

	}

	/*
	 * Inserts Product Batches in CEN_Batches table and returns List of keys corresponding to the Monomer Batches which were
	 * successfully inserted.
	 * 
	 */
	// add a set of product batches ( same method call for RegisteredBatches )
	public void createParallelProductBatches(String stepKey, String pageKey, List productBatchList, boolean summaryStep)
			throws DAOException {
		for (int j = 0; j < productBatchList.size(); j++) {
			Object batchObject = productBatchList.get(j);
			if (batchObject instanceof BatchesList) {
				BatchesList tempBatchesList = (BatchesList) batchObject;
				// ProductBatches are inserted into DB only when its a part of SummaryStep
				// or if Deduping is set to false in BatchesList,
				if (summaryStep || (!tempBatchesList.isDedupedList())) {
					insertList(tempBatchesList.getKey(), CeNConstants.FOR_PRODUCTS_CONSTANT);
					this.isolateProductBatchesForInsert(stepKey, pageKey, tempBatchesList.getBatchModels(), tempBatchesList
							.getKey());
					log.debug("Inserted " + productBatchList.size() + " ProductBatches successfully.");
				}else if(tempBatchesList.getPosition().equals(CeNConstants.PRODUCTS_USER_ADDED))
				{
					insertList(tempBatchesList.getKey(), CeNConstants.FOR_PRODUCTS_CONSTANT);
					this.isolateProductBatchesForInsert(stepKey, pageKey, tempBatchesList.getBatchModels(), tempBatchesList
							.getKey());
					log.debug("Inserted " + productBatchList.size() + " ProductBatches successfully with position:"+tempBatchesList.getPosition());
				}
				else {
					log.debug("Dedupe logic applied ProductBatches NOT Inserted. for batchesList -" + tempBatchesList.getListKey());
				}
				this.insertStepBatchesList(pageKey, stepKey, tempBatchesList.getKey(), tempBatchesList.getPosition());

			}
		}

	}

	public void createSingletonProductBatches(String stepKey, String pageKey, List productList) throws DAOException {

		this.isolateProductBatchesForInsert(stepKey, pageKey, productList, "SingletonProductList");
		log.debug("Inserted " + productList.size() + " Singleton ProductBatches successfully.");

	}

	/*
	 * Seperates out the compounds from ProductBatchList to perform Batch inserts
	 */
	private void isolateProductBatchesForInsert(String stepKey, String pageKey, List productList, String listKey)
			throws DAOException {
		/*
		 * List parentCompounds = new ArrayList(productList.size()); log.debug("Isolating Product Batches Size:" +
		 * productList.size());
		 * 
		 * for (int i = 0; i < productList.size(); i++) { Object batch = productList.get(i); log.debug("Product Batch List INDEX -" +
		 * i); parentCompounds.add(((ProductBatchModel) batch).getCompound());
		 *  } log.debug("Before Inserting Comounds " + parentCompounds.size()); this.insertCompoundStructure(pageKey,
		 * parentCompounds);
		 */
		log.debug("Before Inserting ProductBatches " + productList.size());
		DAOFactory factory = this.getDaoFactory();
		try {
			ProductBatchDAO pBatchDao = factory.getProductBatchDAO();
			pBatchDao.insertProductBatchesThruStoredProcedure(stepKey, pageKey, productList, listKey);
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(insertError);
		} finally {
			this.releaseDaoFactory(factory);
		}
	}

	/*
	 * Seperates out the compounds from ProductBatchList to perform Batch inserts
	 */
	private void isolateMonomerBatchesForInsert(String stepKey, String pageKey, List monomerList) throws DAOException {
		/*
		 * List parentCompounds = new ArrayList(monomerList.size()); log.debug("Inside Create Monomer Batches Size:" +
		 * monomerList.size());
		 * 
		 * for (int i = 0; i < monomerList.size(); i++) { Object batch = monomerList.get(i); log.debug("Monomer Batch List INDEX -" +
		 * i); ParentCompoundModel comp = ((MonomerBatchModel) batch).getCompound(); if (comp != null) parentCompounds.add(comp);
		 * log.debug("Compound Structure -: " + comp); } log.debug("Before Inserting Compounds " + parentCompounds.size());
		 * this.insertCompoundStructure(pageKey, parentCompounds); log.debug("\\n\\n\\n\\n\\n***********************************");
		 */
		log.debug("Before Inserting MonomerBatches " + monomerList.size());

		DAOFactory factory = this.getDaoFactory();
		try {
			MonomerBatchDAO mBatchDao = factory.getMonomerBatchDAO();
			mBatchDao.insertMonomerBatchesThruStoredProcedure(stepKey, pageKey, monomerList);
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException();
		} finally {
			this.releaseDaoFactory(factory);
		}
		log.debug("***********************************\\n\\n\\n\\n\\n");

	}

	/*
	 * Inserts Monomer Batches in CEN_Batches table and returns List of keys corresponding to the Monomer Batches which were
	 * successfully inserted.
	 * 
	 * @return List of Batch_keys inserted successfully.
	 */
	public void createParallelMonomerBatches(String stepKey, String pageKey, List monomerList, boolean summaryStep)
			throws DAOException {
		// For handling batches from Parallel Chemistry
		ArrayList monBatchList = new ArrayList();
		for (int j = 0; j < monomerList.size(); j++) {
			Object batchObject = monomerList.get(j);
			if (batchObject instanceof BatchesList) {
				BatchesList tempBatchesList = (BatchesList) batchObject;
				// ProductBatches are inserted into DB only when its a part of SummaryStep
				// or if Deduping is set to false in BatchesList,
				if (summaryStep || (!tempBatchesList.isDedupedList())) {
					if (tempBatchesList.getPosition().equals(CeNConstants.STOIC_POSITION_CONSTANT)) {
						insertList(tempBatchesList.getKey(), CeNConstants.FOR_STOIC_MONOMERS_CONSTANT);
					} else {
						insertList(tempBatchesList.getKey(), CeNConstants.FOR_MONOMERS_CONSTANT);
					}
					// set ListKey to the batch models
					int size = tempBatchesList.getBatchModels().size();
					List list = tempBatchesList.getBatchModels();
					for (int i = 0; i < size; i++) {
						((MonomerBatchModel) list.get(i)).setListKey(tempBatchesList.getKey());
					}
					// Append all the MonomerBatches to one list.
					monBatchList.addAll(list);
					log.debug("Appended " + list.size() + " MonomerBatches to grand list for insert.Grand list size:"+monBatchList);
				} else {
					log.debug("Dedupe logic applied!! MonomerBatches NOT Inserted. for batchesList -"
							+ tempBatchesList.getListKey());
				}
				this.insertStepBatchesList(pageKey, stepKey, tempBatchesList.getKey(), tempBatchesList.getPosition());
			}
		}

		// Insert all the monomers in Lists A ,B ,C etc in one go for optimization.
		this.isolateMonomerBatchesForInsert(stepKey, pageKey, monBatchList);
		log.debug("Inserted " + monBatchList.size() + " MonomerBatches successfully.");
	}

	/**
	 * @param stepKey
	 * @param pageKey
	 * @param monomerList
	 * @throws DAOException
	 */
	public void createStoicMonomerBatches(String stepKey, String pageKey, BatchesList stoicList) throws DAOException {
		JdbcTemplate jt = new JdbcTemplate();
		jt.setDataSource(this.getDataSource());
		String query = "SELECT COUNT(*) FROM CEN_LISTS WHERE LIST_KEY='" + stoicList.getKey() + "'";
		log.debug(query);
		int result = jt.queryForInt(query);
		/*
		 * This check is required to avoid Unique Key Contraints generated for LIST_KEY.
		 */
		if (result > 0) {
//			 set ListKey to the batch models
			int size = stoicList.getBatchModels().size();
			List list = stoicList.getBatchModels();
			for (int i = 0; i < size; i++) {
				((MonomerBatchModel) list.get(i)).setListKey(stoicList.getKey());
			}
			this.isolateMonomerBatchesForInsert(stepKey, pageKey, stoicList.getBatchModels());
			log.debug("Inserted STE MonomerBatch Array with count : " + stoicList.getBatchModels().size() + " for stoic successfully.");
		} else {
			ArrayList monomerList = new ArrayList(1);
			monomerList.add(stoicList);
			this.createParallelMonomerBatches(stepKey, pageKey, monomerList, false);
			log.debug("Inserted BatchList for stoic successfully.");
		}
		log.debug("Inserted  " + stoicList.getBatchModels().size() + " StoichBatches successfully.");

	}

	public void createNewNotebook(String siteCode, String userID, String notebook, String creator) throws DAOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookInsertDAO.createNewNotebook()");
		try {
			String insertSql = InsertQueryGenerator.getInsertNewNotebookQuery();
			NotebookInsert insert = new NotebookInsert(this.getDataSource(), insertSql);

			String s_AuditLog = CeNConstants.XML_VERSION_TAG + "<Audit_Log><Log><Entry><Reason>Record Created</Reason><Timestamp>"
					+ (new SimpleDateFormat("MMM d, yyyy HH:mm:ss z")).format(new Date()) + "</Timestamp><Username>" + creator
					+ "</Username></Entry></Log></Audit_Log>";

			String xmlMetadata = CeNConstants.XML_VERSION_TAG + "<Notebook_Properties/>";

			insert.update(new Object[] { siteCode, // SITE_CODE
					userID,// USERNAME
					notebook, // NOTEBOOK,
					"OPEN", // STATUS
					xmlMetadata // XML_METADATA
					});

		} catch (Exception updateError) {
			throw new JDBCRuntimeException(updateError);
		} finally {
			stopwatch.stop();
		}
	}

	/**
	 * @return the userProfile
	 */
	public HttpUserProfile getUserProfile() {
		return userProfile;
	}

	/**
	 * @param userProfile
	 *            the userProfile to set
	 */
	public void setUserProfile(HttpUserProfile userProfile) {
		this.userProfile = userProfile;
	}

	

}