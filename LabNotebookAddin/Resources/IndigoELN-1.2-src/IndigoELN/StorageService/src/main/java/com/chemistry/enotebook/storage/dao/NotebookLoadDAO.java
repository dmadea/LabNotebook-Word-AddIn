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
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.SignaturePageVO;
import com.chemistry.enotebook.storage.jdbc.select.*;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ParallelExpDuperDeDuper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotebookLoadDAO extends StorageDAO {

	private AbstractSelect selector = null;
	
	Stopwatch watch = new Stopwatch();
	private static final Log log = LogFactory.getLog(NotebookLoadDAO.class);
	

	public NotebookPageModel loadNotebookPage( NotebookRef nbref ) throws DAOException{
		
		try {
			log.debug("STARTED Loading Notebook: "+nbref);
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("NotebookLoadDAO.loadNotebookPage()");
			long startTime = System.currentTimeMillis();
			NotebookPageModel pageModel = loadNotebookPageWithHeader(nbref);

			List procedureImages = loadProcedureImages(pageModel.getKey());
			if(procedureImages != null){
				pageModel.getPageHeader().setProcedureImages(procedureImages);
			}

//			System.out.println(System.currentTimeMillis() - startTime +
//            " ms elapsed for [ Load Page Header ]");
			
			//Load all reaction steps and BatchesList without BatchModels
			List<ReactionStepModel> reactionSteps = loadAllReactionSteps(pageModel.getKey());
			
//			System.out.println(System.currentTimeMillis() - startTime +
//            " ms elapsed for [ Load Summary Reaction ]");
			
			//load Registreded paltes
			DAOFactory factory = getDaoFactory();
			PlateDAO plateDao = factory.getPlateDAO();
			ArrayList<ProductPlate> list = plateDao.loadRegisteredPlates(pageModel.getKey());
			log.debug("Loaded Registred Plates for notebook: "+list.size());
			pageModel.setRegisteredPlates(list);
			
			if(reactionSteps.size() > 0){
				pageModel.setReactionSteps(reactionSteps);
				loadAllReactionStepsWithBatches(pageModel);
			}
			
			AnalysisCacheModel analysisCache = loadAnalysisCache(pageModel.getKey());
			if(analysisCache != null){
				pageModel.setAnalysisCache(analysisCache);
			}
			
			AttachmentCacheModel attachmentCache = loadAttachmentCache(pageModel.getKey());
			if(attachmentCache != null){
				pageModel.setAttachmentCache(attachmentCache);
			}
		
			stopwatch.stop();
			log.debug("FINISHED Loading NotebookPage"+nbref);
			return pageModel;
		} catch (DAOException error) {
			// TODO Auto-generated catch block
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
	}
	
	private NotebookPageModel loadNotebookPageWithHeader(NotebookRef nbref) throws DAOException{

		try {
			NotebookPageModel page = null;
			log.debug("STARTED Loading NotebookPageHeader: "+nbref);
			String sqlQuery = SelectQueryGenerator.getNotebookHeaderQuery(nbref);
			this.selector = new NotebookPageSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for  NotebookPageHeaderInfo.");
			List pageList = this.selector.execute();
			if(pageList.size() > 0){
				Object pageObj = pageList.get(0);
				log.debug(" page class :"+pageObj.getClass());
				if(pageObj instanceof NotebookPageModel ){
					page = (NotebookPageModel) pageObj;
					if(page.getPageType().equals("PARALLEL")){
						DesignSynthesisPlan planObj = loadDesignSynthesisPlan(page.getKey());
						//DSP object userName overrides the DesignSubmiiter set in the NotebookPageSelect
						page.getPageHeader().setDSPData(planObj);
					}
					if(page.getPageType().equals("CONCEPTION")){
						DesignSynthesisPlan planObj = loadDesignSynthesisPlan(page.getKey());
						page.getPageHeader().setSpid(planObj.getSpid());
					}
				}
			} else {
				log.error("NO PAGES returned for nbref = "+nbref);
				throw new DAOException("Notebook Not Found for NbkRef: "+nbref);
			}
			log.debug("FINISHED Loading NotebookPageHeader: "+page.getPageHeader());
			return page;
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException("Failed loading notebook page for ref: " + nbref.getCompleteNbkNumber(), error);
		}
	}

	public List getNotebookPagesForUser(String user)  throws DAOException {
		List nbkPages = null;
		try {
			log.debug("STARTED Loading NBKPages for User: "+user);
			String sqlQuery = SelectQueryGenerator.getNbkQueryForUser(user);
			this.selector = new NotebookPageSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for  NotebookPagesForUser .");
			nbkPages = this.selector.execute();
			log.debug("FINISHED Loading NBKPages for User: "+nbkPages.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return nbkPages;
	}

	private DesignSynthesisPlan loadDesignSynthesisPlan(
			String pageKey)	 throws DAOException{
		DesignSynthesisPlan dsp = null;
		watch.start("LoadDesignSynthesisPlan");
		try {
			log.debug("STARTED Loading DesignSynthesisPlan for pageKey: "+ pageKey);
			String sqlQuery = SelectQueryGenerator.getQueryForDSP(pageKey);
			this.selector = new DSPSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for DesignSynthesisPlan.");
			List dspList = this.selector.execute();
			if ((dspList != null) && (dspList.size() > 0)) {
				dsp = (DesignSynthesisPlan) dspList.get(0);
			}
			log.debug("FINISHED Loading DSP :"+dsp);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		watch.stop();
		return dsp;
	}




	private void loadAllReactionStepsWithBatches(NotebookPageModel pageModel) throws DAOException{
		ArrayList reactionSteps = new ArrayList(pageModel.getReactionSteps().size());
		//loads all Amounts for the page, and those amounts will be later assigned to appropriate batches
		Map batchAmountsMap = getAllAmountsForPage(pageModel.getKey());
		//Load summary Step first.(All BatchModel models with amounts ).
		reactionSteps.add(loadReactionStepDetails(pageModel.getSummaryReactionStep(),batchAmountsMap, pageModel.isConceptionExperiment()));
		DAOFactory factory = getDaoFactory();
		PlateDAO plateDao = factory.getPlateDAO();
		
		//Checking for deduping logic
		if(pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)){
			ParallelExpDuperDeDuper deduper = new ParallelExpDuperDeDuper();
			deduper.dedupeParallelExpAfterPartialLoadFromDB(pageModel);
			List isteps = pageModel.getIntermediateReactionSteps();
			log.debug("Loading Intermediate steps batches...size = "+isteps.size());
			/**
			 * This is to handle ONE STEP reaction in PARALLEL, loads plates to 
			 * Step 0.
			 */
			if(isteps.size() ==0){
				ReactionStepModel oneStep = (ReactionStepModel)reactionSteps.get(0);
				oneStep.setMonomerPlates(plateDao.loadMonomerPlates(pageModel.getKey(), oneStep.getKey(),oneStep.getStepNumber()));
				oneStep.setProductPlates(plateDao.loadProductPlates(pageModel.getKey(), oneStep.getKey(),oneStep.getStepNumber()));
			}
			/**
			 * For MULTI STEP, following code will load all the intermediate steps
			 */
			for(int i=0;i<isteps.size();i++){
				ReactionStepModel istep = (ReactionStepModel)isteps.get(i);
				List monomerList = istep.getMonomers();
				ArrayList newMonomerList = new ArrayList(monomerList.size());
				log.debug("Loading MONOMER BATCHES ...monomerBatchesList size = "+monomerList.size()+" step no: "+istep.getStepNumber());
				for(int j=0;j<monomerList.size();j++){
					BatchesList blist = (BatchesList)monomerList.get(j);
					//There will always be One Stoic BatchesListObject
					if(!blist.getPosition().equals(CeNConstants.STOIC_POSITION_CONSTANT))
					{
						log.debug("Loading MonomerBatches List..Position = "+blist.getPosition()+"  listKey: "+blist.getKey());
						newMonomerList.add(loadMonomerBatchList(blist,batchAmountsMap));
					}
				}
				istep.setMonomers(newMonomerList);
				
				BatchesList stoicBatchesList = istep.getStoicBatchesList();
				if(stoicBatchesList == null){
					stoicBatchesList = new BatchesList();
					stoicBatchesList.setPosition(CeNConstants.STOIC_POSITION_CONSTANT);
				} else if(stoicBatchesList.isLoadedFromDB()){
					//load stoic batches only when we have this List record in table
					log.debug("Loading Stoic batches for the step");
					stoicBatchesList = loadMonomerBatchList(stoicBatchesList,batchAmountsMap);
				}
				istep.setStoicBatchesList(stoicBatchesList);
				List productList = istep.getProducts();
				ArrayList newProductsList = new ArrayList(productList.size());
				log.debug("Loading Product Batches ...productBatchesList size = "+productList.size());
				for(int k=0;k<productList.size();k++){
					BatchesList plist = (BatchesList)productList.get(k);
					log.debug("Loading ProductBatches List..Position = "+plist.getPosition()+"  listKey: "+plist.getKey());
					plist = loadProductBatchList(plist,batchAmountsMap);
					newProductsList.add(plist);
				}
				istep.setProducts(newProductsList);
				istep.setMonomerPlates(plateDao.loadMonomerPlates(pageModel.getKey(), istep.getKey(),istep.getStepNumber()));
				istep.setProductPlates(plateDao.loadProductPlates(pageModel.getKey(), istep.getKey(),istep.getStepNumber()));
				reactionSteps.add(istep);
			}
		}

		//LOAD the PSEUDO PLate
		List pseudoPltList = plateDao.loadPseudoProductPlate(pageModel.getKey());
		if(pseudoPltList != null && pseudoPltList.size() == 1) {
			pageModel.setPseudoProductPlate((PseudoProductPlate)pseudoPltList.get(0));
			log.debug("PseudoPlate has been loaded..");
		}
		
		pageModel.setReactionSteps(reactionSteps);
		releaseDaoFactory(factory);
	}
	
	
	public BatchesList loadMonomerBatchList(BatchesList mlist, Map allAmountsMap)throws DAOException{
		//* If this list is deduped and already loaded in SummaryStep , this list will not be loaded again
		if(!mlist.isDedupedList()){
			String sqlQuery = 
				SelectQueryGenerator.getIntermediateMonomerBatchesQuery(mlist.getKey());
			long startTime = System.currentTimeMillis();
			this.selector = new MonomerBatchSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for MonomerBatchSelect.");
			List monomerList = this.selector.execute();
			ArrayList monomerArrayList = new ArrayList(monomerList);
			loadAmountsIntoBatches(monomerArrayList, allAmountsMap);
			
			mlist.setBatchModels(monomerArrayList);
			log.debug("Monomers loaded : "+monomerList.size()+ " for position: "+mlist.getPosition());
			log.debug(System.currentTimeMillis() - startTime +
            " ms elapsed for [ Load Monomer Batch ]");
		}
		return mlist;
	}

	public BatchesList loadProductBatchList(BatchesList plist, Map allAmountsMap) throws DAOException{
		if(!plist.isDedupedList()){
			String sqlQuery = 
				SelectQueryGenerator.getIntermediateProductBatchesQuery(plist.getListKey());
			long startTime = System.currentTimeMillis();
			this.selector = new ProductBatchSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for ProductBatchSelect.");
			List productList = this.selector.execute();
			ArrayList productArrayList = new ArrayList(productList);
			loadAmountsIntoBatches(productArrayList, allAmountsMap);
			plist.setBatchModels(productArrayList);
			log.debug("Product selected : "+productList.size());
			log.debug(System.currentTimeMillis() - startTime +
            " ms elapsed for [ Load Product Batch ]");
		}
		return plist;
	}
	
	public List loadAllReactionSteps(String pageKey)  throws DAOException {
		List allSteps = null;
		try {
			log.debug("STARTED Loading all reaction steps for pageKey : "+pageKey);
			String sqlQuery = SelectQueryGenerator.getAllReactionStepsQuery(pageKey);
			this.selector = new ReactionStepSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for AllReactionSteps without details.");
			allSteps = this.selector.execute();
			
			log.debug("FINISHED Loading all steps "+allSteps);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return allSteps;
	}
	
	public List loadProcedureImages(String pageKey) throws DAOException{
		ProcedureImageDAO procedureImageDAO = this.getDaoFactory().getProcedureImageDAO();
		List list = procedureImageDAO.loadNotebookProcedureImages(pageKey);
		return list;
	}
	
	public AnalysisCacheModel loadAnalysisCache(String pageKey) throws DAOException{
		AnalysisDAO analysisDAO = this.getDaoFactory().getAnalysisDAO();
		List  list = analysisDAO.loadAnalysis(pageKey);
		AnalysisCacheModel cache =  new AnalysisCacheModel(list);
		return cache;
	}
	
	public AttachmentCacheModel loadAttachmentCache(String pageKey) throws DAOException{
		AttachmentDAO attachmentDAO = this.getDaoFactory().getAttachmentDAO();
		List  list = attachmentDAO.loadAttachment(pageKey);
		AttachmentCacheModel cache =  new AttachmentCacheModel(list);
		return cache;
	}
	/*
	 * @ deprecated 
	 */ 
//	private List getAllReactionStepsWithDetails(String pageKey) throws DAOException {
//		try {
//			log.debug("STARTED Loading all reaction step details for nbkPageKey "+pageKey);
//			List steps = loadAllReactionSteps(pageKey);
//			List detailSteps = new ArrayList(steps.size());
//			ReactionStepModel step = null;
//			log.debug(" Extracting details for each step of "+steps.size()+" steps");
//			for(int i=0; i< steps.size(); i++){
//				step = (ReactionStepModel)steps.get(i);
//				log.debug("Fetching step details for step # "+step.getStepNumber());
//				if(step != null)
//					detailSteps.add(loadReactionStepDetails(step));
//			}
//			log.debug("FINISHED Loading all reaction steps with details :"+steps.size());
//			return detailSteps;
//		} catch (DAOException error) {
//			log.error(CommonUtils.getStackTrace(error));
//			throw error;
//		}
//	}
	/*
	 * @ deprecated 
	 */ 
//	private ReactionStepModel getReactionStepSummary(String pageKey)  throws DAOException {
//		ReactionStepModel step = null;
//		try {
//			log.debug("STARTED Loading reaction step summary :"+pageKey);
//			String sqlQuery = SelectQueryGenerator.getReactionStepQueryBySeqNumber(pageKey, 0);
//			this.selector = new ReactionStepSelect(super.getDataSource(), sqlQuery);
//
//			log.debug("Executing Select for ReactionStepSummary.");
//			// fill the exact string
//			List reactionStepList = this.selector.execute();
//			if ((reactionStepList != null) && (reactionStepList.size() > 0)) {
//				step = (ReactionStepModel) reactionStepList.get(0);
//			}
//			log.debug("FINISHED Loading reaction step summary "+step);
//		} catch (Exception error) {
//			log.error(CommonUtils.getStackTrace(error));
//			throw new DAOException(error);
//		}
//		return step;
//	}
	/*
	 * @ deprecated 
	 */ 
//	private ReactionStepModel getIntermediateReactionStep(String pageKey, int seqNo) throws DAOException {
//		ReactionStepModel step = null;
//		try {
//			log.debug("STARTED Loading Intermediate Reaction Step :"+pageKey);
//			String sqlQuery = SelectQueryGenerator.getReactionStepQueryBySeqNumber(pageKey, seqNo);
//			this.selector = new ReactionStepSelect(super.getDataSource(), sqlQuery);
//			log.debug("Executing Select for IntermediateReactionStep.");
//
//			// fill the exact string
//			List reactionStepList = this.selector.execute();
//			if ((reactionStepList != null) && (reactionStepList.size() > 0)) {
//				step = (ReactionStepModel) reactionStepList.get(0);
//			}
//			log.debug("FINISHED Loading Intermediate Reaction Step :"+step);
//		} catch (Exception error) {
//			log.error(CommonUtils.getStackTrace(error));
//			throw new DAOException(error);
//		}
//		return step;
//	}

	private ReactionStepModel loadReactionStepDetails(ReactionStepModel step, Map amountsMap, boolean isConceptionExperiment)  throws DAOException{
		try {
			log.debug("STARTED Loading ReactionStep Details for Summary Step -> "+step.isSummaryStep());
			String sqlQueryForProductBatches = SelectQueryGenerator.getSummaryProductBatchesQuery(step.getKey());
			
			long startTime = System.currentTimeMillis();
			this.selector = new ProductBatchSelect(super.getDataSource(), sqlQueryForProductBatches);
			log.debug("Executing Select for ProductBatchSelect.");
			List productList = this.selector.execute();
			log.debug("Total Products selected : "+productList.size()+ " for step: "+step.getStepNumber());
			if(productList.size() > 0){
				//load Amounts into these batches
				loadAmountsIntoBatches(productList,amountsMap);
				//Now wrap them to appropriate BatchesList
				wrapProductBatches(step, productList);
			}
			log.debug(System.currentTimeMillis() - startTime + " ms elapsed for [ Load Product Batches ]");
			startTime = System.currentTimeMillis();
			
			if (!isConceptionExperiment) {
				String sqlQueryForMonomerBatches = SelectQueryGenerator.getSummaryMonomerBatchesQuery(step.getKey());			
				this.selector = new MonomerBatchSelect(super.getDataSource(), sqlQueryForMonomerBatches);
				log.debug("Executing Select for MonomerBatchSelect.");
				List<MonomerBatchModel> monomerList = this.selector.execute();
				log.debug("Total Monomers selected : "+monomerList.size()+ " for step: "+step.getStepNumber());
				if(monomerList.size() > 0){
					//load Amounts into these batches
					loadAmountsIntoBatches(monomerList,amountsMap);
					//Now wrap them to appropriate BatchesList
					wrapMonomerBatches(step, monomerList);
				}
				log.debug(System.currentTimeMillis() - startTime + " ms elapsed for [ Load Monomer Batches ]");
			}
			
			log.debug("FINISHED Loading ReactionStep Details for step: "+ step.getStepNumber());
			return step;
		} catch (Exception e) {
			log.error(e);
			throw new DAOException(e);
		}
	}
	
	
	private List loadAmountsIntoBatches(List batchList, Map amountsMap){
		ProductBatchModel productBatch = null;
		MonomerBatchModel monomerBatch = null;
		BatchModel batch = null;
		Object batchObject = null;
		Stopwatch watch =new Stopwatch();
		//watch.start("Adding Amount into Batches");
		//long starttime = System.currentTimeMillis();
		for(int i=0;i<batchList.size();i++){
			batchObject = batchList.get(i);
			batch =(BatchModel)batchObject;
			CeNBatchAmountsModel batchAmounts=  (CeNBatchAmountsModel)amountsMap.get(batch.getKey());
			batch.setLoadingFromDB(true);
			if(batchAmounts == null || batch== null) continue;
			
			batch.setMoleAmount(batchAmounts.getMoleAmount());
			batch.setWeightAmount(batchAmounts.getWeightAmount());
			batch.setLoadingAmount(batchAmounts.getLoadingAmount());
			batch.setVolumeAmount(batchAmounts.getVolumeAmount());
			batch.setDensityAmount(batchAmounts.getDensityAmount());
			batch.setMolarAmount(batchAmounts.getMolarAmount());
			batch.setPurityAmount(batchAmounts.getPurityAmount());
			batch.setRxnEquivsAmount(batchAmounts.getRxnEquivsAmount());
			batch.setTotalWeight(batchAmounts.getTotalWeight());
			batch.setTotalVolume(batchAmounts.getTotalVolume());
		    batch.setTotalMolarity(batchAmounts.getTotalMolarity());
		    batch.setPreviousMolarAmount(batchAmounts.getPreviousMolarAmount());
			
			if( batchObject instanceof MonomerBatchModel){
				monomerBatch = (MonomerBatchModel)batchObject;
				monomerBatch.setAmountNeeded(batchAmounts.getAmountNeeded());
				monomerBatch.setExtraNeeded(batchAmounts.getExtraNeeded());
				monomerBatch.setSoluteAmount(batchAmounts.getSoluteAmount());
				monomerBatch.setDeliveredWeight(batchAmounts.getDeliveredWeight());
				monomerBatch.setDeliveredVolume(batchAmounts.getDeliveredVolume());
			}
			else{
				productBatch = (ProductBatchModel)batchObject;
				productBatch.setTheoreticalWeightAmount(batchAmounts.getTheoreticalWeightAmount());
				productBatch.setTheoreticalMoleAmount(batchAmounts.getTheoreticalMoleAmount());
				productBatch.setTheoreticalYieldPercentAmount(batchAmounts.getTheoreticalYieldPercentAmount());
				productBatch.setTotalWeightAmount(batchAmounts.getTotalWeight());
				productBatch.setTotalVolumeAmount(batchAmounts.getTotalVolume());
				//productBatch.setTotalTubeWeightAmount(batchAmounts.g);
				//productBatch.setTotalWellWeightAmount(batchAmounts.g);
				//productBatch.setTotalTubeVolumeAmount(batchAmounts.g);
				//productBatch.setTotalWellVolumeAmount(batchAmounts.g);
			}
			batch.setLoadingFromDB(false);
		}
		//log.debug("Amounts Mapped to "+batchList.size()+" batches in :"+(System.currentTimeMillis()-starttime)+"ms");
		//watch.stop();
		return batchList;
	}
	
	private Map getAllAmountsForPage(String pageKey) throws DAOException {
		List amounts = null;
		Map batchAmountsMap = new HashMap();
		try {
			long starttime = System.currentTimeMillis();
			log.debug("Loading all AMOUNTS for NBK with pageKey : "+pageKey);
			String sqlQuery = SelectQueryGenerator.getAmountsForPageQuery(pageKey);
			AmountSelect selector = new AmountSelect(getDataSource(), sqlQuery);
			amounts = selector.execute();
			batchAmountsMap = selector.getBatchAmountsMap();
			long endtime = System.currentTimeMillis()-starttime;
			log.debug("Time to load "+amounts.size()+" Amounts "+endtime+"ms");
			log.debug("FINISHED Loading Batch Amounts "+batchAmountsMap.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new JDBCRuntimeException(error);
		}
		return batchAmountsMap;
	}

	/*
	 * With the DEDUPING changes while inserting for Parallel batches, a new BatchList was introduced. Step contains ArrayList of
	 * BatchesList and BatchesList will contain ArrayList of ProductBatchModel Objects. Following method separates the product
	 * batches into different BatchesList based on the "listKey". For Singleton Batches each productbatch is put in one BatchesList
	 * object for consistancy sake.
	 */

	private void wrapProductBatches(ReactionStepModel step, List productBatches)  throws DAOException{
		HashMap pBatchMap = new HashMap();
		BatchesList batchesList = null;
		ProductBatchModel pbatch = null;
		log.debug("STARTED wrapProductBatches");
		for (int i = 0; i < productBatches.size(); i++) {
			// step.addProductBatch((ProductBatchModel)productList.get(i));
			pbatch = (ProductBatchModel) productBatches.get(i);
			String listKey = pbatch.getListKey();
			if (listKey != null) {
				if (pBatchMap.containsKey(listKey)) {
					batchesList = (BatchesList) pBatchMap.get(listKey);
					batchesList.addBatch(pbatch);
				} else {
					log.debug("Products -> New List Key : "+listKey);
					batchesList = new BatchesList(listKey);
					batchesList.setPosition(pbatch.getPosition());
					batchesList.addBatch(pbatch);
					batchesList.setLoadedFromDB(pbatch.isLoadedFromDB());
					pBatchMap.put(listKey, batchesList);
				}
				batchesList.setModelChanged(false);
			} else {
				// Handles Singleton Batches
				batchesList = new BatchesList();
				batchesList.addBatch(pbatch);
				step.addProductBatchesList(batchesList);
			}
		}
		if (pBatchMap.size() > 0) {
			log.debug("Size of Product Batches Map: "+pBatchMap.size());
				// adding list of BatchList to Step
			step.setProducts(new ArrayList(pBatchMap.values()));
		}
		log.debug("FINISHED wrapProductBatches: "+step.getProducts().size());
	}

	/*
	 * With the DEDUPING changes while inserting for Parallel batches, a new BatchList was introduced. Step contains ArrayList of
	 * BatchesList and BatchesList will contain ArrayList of MonomerBatchModel Objects. Following method separates the monomer
	 * batches into different BatchesList based on the "listKey". For Singleton Batches each monomerbatch is put in one BatchesList
	 * object for consistancy sake.
	 */
	private void wrapMonomerBatches(ReactionStepModel step, List<MonomerBatchModel> monomerBatches)  throws DAOException{
		HashMap<String, BatchesList<MonomerBatchModel>> mBatchMap = new HashMap<String, BatchesList<MonomerBatchModel>>();
		HashMap<String, BatchesList<MonomerBatchModel>> stoicBatchMap = new HashMap<String, BatchesList<MonomerBatchModel>>(1);
		BatchesList<MonomerBatchModel> batchesList = null;
		
		log.debug("STARTED wrapMonomerBatches ");
		for (MonomerBatchModel mbatch : monomerBatches) {
			String listKey = mbatch.getListKey();
			if (listKey != null) {
				if (mBatchMap.containsKey(listKey)) {
					// retreive the BatchesList from Map
					batchesList = mBatchMap.get(listKey);
					batchesList.addBatch(mbatch);
				} else if (stoicBatchMap.containsKey(listKey)) {
					// retreive the stoich BatchesList from Map
					batchesList = stoicBatchMap.get(listKey);
					batchesList.addBatch(mbatch);
				} else {
					log.debug("Monomers -> New List Key: "+listKey);
					// create and add First BatchesList to Map
					batchesList = new BatchesList<MonomerBatchModel>(listKey);
					batchesList.setPosition(mbatch.getPosition());
					log.debug("POSITION -- "+batchesList.getPosition());
					batchesList.setLoadedFromDB(mbatch.isLoadedFromDB());
					batchesList.addBatch(mbatch);
					if(batchesList.getPosition().equals(CeNConstants.STOIC_POSITION_CONSTANT)){
						stoicBatchMap.put(listKey, batchesList);
					} else {
						mBatchMap.put(listKey, batchesList);
					}
				}
			} else {
				//Need to handle for Singleton Monomer Batches
				
				// Handles Singleton Batches
//				batchesList = new BatchesList();
//				batchesList.addBatch(mbatch);
//				step.addMonomerBatchesList(batchesList);
			}
		}
		
		if (mBatchMap.size() > 0) {
			log.debug("Size of Monomer Batches Map: "+new ArrayList(mBatchMap.values()).size());
			// adding list of BatchList to Step
			step.setMonomers(new ArrayList(mBatchMap.values()));
		}
		if (stoicBatchMap.size() > 0) {
			log.debug("Size of Stoic Batches--: "+stoicBatchMap.size());
			Object[] batches = stoicBatchMap.values().toArray();
			batchesList = (BatchesList)batches[0];
			// adding stoicBatchList object to Step
			step.setStoicBatchesList(batchesList);
		}
		
		log.debug("FINISHED wrapping Monomer Batches : "+step.getMonomers().size());
	}
	
	
	public List<BatchRegInfoModel> loadAllRegisteredBatchesForPage(String pageKey) throws DAOException{
		List<BatchRegInfoModel> allRegBatches = null;
		try {
			log.debug("STARTED Loading all RegisteredBatches for pageKey : "+pageKey);
			String sqlQuery = SelectQueryGenerator.getAllRegisteredBatchesForPageQuery(pageKey);
			this.selector = new RegisteredBatchSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for AllRegisteredBatch.");
			allRegBatches = this.selector.execute();
			
			log.debug("FINISHED Loading all registeredBatches : "+allRegBatches.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return allRegBatches;
	}
	
	public List getAllRegisteredBatchesWithJobID(String jobid) throws DAOException{
		List allRegBatches = null;
		try {
			log.debug("STARTED Loading all RegisteredBatches for jobid : "+jobid);
			String sqlQuery = SelectQueryGenerator.getAllRegisteredBatchesForJobidQuery(jobid);
			this.selector = new RegisteredBatchRegInfoSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for AllRegisteredBatch.");
			allRegBatches = this.selector.execute();
			
			log.debug("FINISHED Loading all registeredBatches : "+allRegBatches.size());
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return allRegBatches;
	}
	
	public ProductBatchModel loadProductBatchModel(String batchNumber) throws DAOException
	{
		try {
			ProductBatchModel prodModel = null;
			String sqlQuery = SelectQueryGenerator.getStoicProductBatchQuery(batchNumber);
			this.selector = new StoicProductBatchSearchSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for  ProductBatchModel.");
			List selList = this.selector.execute();
			if ((selList != null) && (selList.size() > 0)) {
				prodModel = (ProductBatchModel) selList.get(0);
			}
			return prodModel;
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
	}
	
	public ArrayList<SignaturePageVO> getExperimentsBeingSigned(String ntUserID) throws DAOException {
		String whereUsername = StringUtils.isBlank(ntUserID) ? " " : " username = '" + ntUserID + "' AND ";
		
		String s_Query = "SELECT site_code, notebook, experiment, page_version, page_status, xml_metadata FROM cen_pages x WHERE "
				+ whereUsername
				+ " page_status in ('SUBMITTED', 'SIGNING', 'ARCHIVING')";// 'SIGNED',
		
		try {
			AbstractSelect selector = new ExperimentsBeingSignedSelect(getDataSource(), s_Query);
			List result = selector.execute();
			if (result != null) {
				return new ArrayList<SignaturePageVO>(result);
			} else {
				return new ArrayList<SignaturePageVO>();
			}
		} catch (Exception e) {
			log.error("Error getting experiments being signed: ", e);
			throw new DAOException(e);
		}
	}
	
	public List<SignaturePageVO> getAllSubmittedExperiments() throws DAOException {

		String s_Query = "SELECT site_code, notebook, experiment, page_version, page_status, username, page_key,"
				+ "  xml_metadata " + "  FROM cen_pages x "
				+ "  WHERE page_status in ('SUBMITTED', 'SIGNED', 'ARCHIVING')";

		
		List result = new ArrayList();
		List<SignaturePageVO> aList = new ArrayList<SignaturePageVO>();
		try {
			this.selector = new AbstractSelect(super.getDataSource(), s_Query) {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					String xmlMetadata = rs.getString("XML_METADATA");
					String key = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Ussi_Key");
					
					SignaturePageVO vo = new SignaturePageVO();
					
					vo.setSiteCode(rs.getString("site_code"));
					vo.setNotebook(rs.getString("notebook"));
					vo.setExperiment(rs.getString("experiment"));
					vo.setVersion(rs.getInt("page_version"));
					vo.setStatus(rs.getString("page_status"));
					vo.setUssiKey(StringUtils.isBlank(key) ? "" : key);
					vo.setUserName(rs.getString("username"));
					vo.setPageKey(rs.getString("page_key"));
					
					return vo;
				}
			};
			log.debug("Executing Select for getAllSubmittedExperiments().");
			result = this.selector.execute();
			if(result!= null && result.size() > 0)
			{
				int size = result.size();
				
				for(int i =0; i< size ; i ++)
				{
					aList.add((SignaturePageVO)result.get(i));
				}
				
			}
		
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} 

		return aList;
	}

	public SignaturePageVO getNotebookPageStatus(String siteCode, String nbRefStr, int version) throws DAOException,
			InvalidNotebookRefException {
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return null;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return null;
		}
		if (version <= 0) {
			return null;
		}

		NotebookRef nbRef = new NotebookRef(nbRefStr);

		String s_Query = "SELECT site_code, notebook, experiment, page_version,page_status, " + " xml_metadata "
				+ "  FROM cen_pages " + "  WHERE site_code = '" + siteCode + "' AND " + "  notebook = '"
				+ nbRef.getNbNumber() + "' AND" + " experiment = '" + nbRef.getNbPage() + "' AND" + " page_version = "
				+ version;

		try {
			this.selector = new ExperimentsBeingSignedSelect(super.getDataSource(), s_Query);
			log.debug("Executing Select for getExperimentsBeingSigned().");
			List result = this.selector.execute();
			if(result != null && result.size() > 0)
			{
				SignaturePageVO vo = (SignaturePageVO)result.get(0);
				return vo;
			}else
			{
				return null;
			}
			
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} 
	}

}
