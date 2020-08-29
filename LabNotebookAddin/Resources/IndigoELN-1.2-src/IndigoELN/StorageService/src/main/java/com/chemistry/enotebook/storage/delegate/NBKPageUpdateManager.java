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
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * Utility class that acts as single point for providing update services to Notebook Page(s) This class introspects the model level
 * flags and call the storage delegate methods appropriately. In other words it does smart update i.e persists data for the only
 * models that are changed.
 * 
 * This class functionality will be used on the CeN client.
 */
public class NBKPageUpdateManager {

	private static final Log log = LogFactory.getLog(NBKPageUpdateManager.class);
	private StorageDelegate storageObj = null;
	private static NBKPageUpdateManager updateManager = null;
	private static boolean reinit = false;

	private SessionIdentifier sessionID;
	
	// right now this will be a singleton instance
	public static NBKPageUpdateManager getNBKPageUpdateManagerInstance(SessionIdentifier sessionID) throws StorageInitException {
		if (updateManager == null || reinit) {
			updateManager = new NBKPageUpdateManager(sessionID);
		}
		return updateManager;
	}

	private NBKPageUpdateManager(SessionIdentifier sessionID) throws StorageInitException {
		try {
			this.sessionID = sessionID;
			storageObj = new StorageDelegate();
			log.info("Initialized storage delegate");
		} catch (Exception e) {
			// Remote service retry is already done in delegate. So no need to re try to initalize here
			log.error("Exception initializing Storage delegate: " + CommonUtils.getStackTrace(e));
			throw new StorageInitException("Exception initializing Storage delegate" + e.getMessage());
		}
	}

	public boolean updateNotebookPageData(NotebookPageModel pageModel) throws StorageAccessException {
		boolean success = false;

		// The high level
		if (pageModel != null) {
			//String level = getModelUpdateLevel(pageModel);
			//if (level.compareTo(CeNConstants.UPDATE_NOTEBOOKPAGEHEADER) == 0) {
			//}

			// Prepare PageModel for update. Include the objects that are changed.
			NotebookPageModel model = getPageModelForUpdate(pageModel);
			//CommonUtils.printNBPageData(pageModel);

			// update procedure images
			ProcedureImagesUpdateManager.updateProcedureOnSave(model);

			// Call storage delegate
			try {
				storageObj.updateNotebookPageModel(model, sessionID);
			} catch (StorageException e) {
				throw new StorageAccessException(e);
			}

			//If successful set the modelChanged flag to false on original NotebookPage model
			pageModel.onLoadonAfterSaveSetModelChanged(false,true);
		}
		return success;
	}

	
	private NotebookPageModel getPageModelForUpdate(NotebookPageModel actualPageModel) {
		NotebookPageModel clonedPageModel = (NotebookPageModel) actualPageModel.deepClone();
		clonedPageModel.setNbRef((NotebookRef) actualPageModel.getNbRef().deepClone());
		if (!actualPageModel.isChanging()) {
			//Header will be updated everytime since, modification time need be set
			//while every update.
			clonedPageModel.setPageHeader((NotebookPageHeaderModel) actualPageModel.getPageHeader().deepClone());
			
			//Check for Analytical Model changes
			clonedPageModel.setAnalysisCache(actualPageModel.getAnalysisCache().getClonedAnalyticalModelsForInsertAndUpdate());
			
			//Check for Attachment Model changes
			clonedPageModel.setAttachmentCache(actualPageModel.getAttachmentCache().getClonedAttachmentModelsForInsertAndUpdate());
			
			// check for scheme changes. ( Step need to be included by default all the time)
			List<ReactionStepModel> stepList = actualPageModel.getReactionSteps();
			int size = stepList.size();
			for (int stepNo = 0; stepNo < size; stepNo++) {
				
				ReactionStepModel actualStepModel = actualPageModel.getReactionStep(stepNo);
				ReactionStepModel clonedStepModel = (ReactionStepModel) actualStepModel.deepClone();
				if (actualStepModel.getRxnScheme().isModelChanged()) {
					clonedStepModel.setRxnScheme((ReactionSchemeModel) actualStepModel.getRxnScheme().deepClone());
				}
				
				//Check for MonomerBatches List and BatchModel changes
				List<BatchesList<MonomerBatchModel>>  actualMonBatchesList = actualStepModel.getMonomers();
				ArrayList<BatchesList<MonomerBatchModel>> clonedMonBatchesList = new ArrayList<BatchesList<MonomerBatchModel>>();
				int sizeMonBList = actualMonBatchesList.size();
				for(int i= 0; i < sizeMonBList ; i ++)
				{
					BatchesList<MonomerBatchModel> origList = actualMonBatchesList.get(i);
					BatchesList<MonomerBatchModel> clonedList = origList.deepClone();
					if(clonedList.isModelChanged())
					{
						clonedMonBatchesList.add(clonedList);	
					}
					
				}
				//add these to cloned step
				clonedStepModel.setMonomers(clonedMonBatchesList);
				
				//Check for ProductBatches List and BatchModel changes
				List<BatchesList<ProductBatchModel>> actualProdBatchesList = actualStepModel.getProducts();
				ArrayList<BatchesList<ProductBatchModel>> clonedProdBatchesList = new ArrayList<BatchesList<ProductBatchModel>>();
				int sizeProdBList = actualProdBatchesList.size();
				for(int i = 0; i < sizeProdBList ; i ++)
				{
					BatchesList<ProductBatchModel> origList = actualProdBatchesList.get(i);
					BatchesList<ProductBatchModel> clonedList = origList.deepClone();
					if(clonedList.isModelChanged())
					{
						clonedProdBatchesList.add(clonedList);	
					}
				}
				// add deleted batches list
				clonedProdBatchesList.add(actualStepModel.getIntendedProductBatchesToRemoveList());
				//add these to cloned step
				clonedStepModel.setProducts(clonedProdBatchesList);
				
				//Check for StoichElement batches changes
				ArrayList<MonomerBatchModel> clonedStoichElementList = new ArrayList<MonomerBatchModel>();
				BatchesList<MonomerBatchModel> actualStoicBatchesList = actualStepModel.getStoicBatchesList();
				List<MonomerBatchModel> actualStoichElementList =  actualStoicBatchesList.getBatchModels();
				int sizeSE =actualStoichElementList.size();
				for(int i = 0; i<sizeSE; i ++)
				{
					MonomerBatchModel actualModel = (MonomerBatchModel)actualStoichElementList.get(i);
					if (actualModel.isModelChanged()) {
						MonomerBatchModel cloneModel = (MonomerBatchModel) actualModel.deepClone();
						// In deepCopy() of MonomerBatchModel we set 'Extra needed' and set its Unit to MoleAmount
						// In Singleton, we don't have 'Extra needed', so we need to save Unit of MoleAmount
						if (actualPageModel.isSingletonExperiment()) {
							cloneModel.getMoleAmount().setUnit(actualModel.getMoleAmount().getUnit().deepClone());
						}
						clonedStoichElementList.add(cloneModel);
					}
				}
				// add batches to remove
				clonedStoichElementList.addAll(actualStepModel.getStoicBatchesToRemoveList().getBatchModels());
				
				BatchesList<MonomerBatchModel> clonedStoicBatchesList = new BatchesList<MonomerBatchModel>(actualStoicBatchesList.getKey());
				clonedStoicBatchesList.setPosition(actualStoicBatchesList.getPosition());
				clonedStoicBatchesList.addAllBatches(clonedStoichElementList);
				//add these Stoichelement to cloned step
				clonedStepModel.setStoicBatchesList(clonedStoicBatchesList);
				
				
				// Check for Monomer Plate/Plate well changes in each Step
				List<MonomerPlate> actualMonPlatesList = actualStepModel.getMonomerPlates();
				List<MonomerPlate> clonedMonPlatesList = new ArrayList<MonomerPlate>();
				if (actualMonPlatesList != null && actualMonPlatesList.size() > 0) {
					int sizeMon = actualMonPlatesList.size();
					for (int i = 0; i < sizeMon; i++) {
						MonomerPlate plate = actualMonPlatesList.get(i);
						PlateWell<MonomerBatchModel> wells[] = plate.getAllModifedWellsClone();
						if (plate.isModelChanged() || (wells != null && wells.length > 0)) {
							clonedMonPlatesList.add((MonomerPlate) plate.deepClone());
						}
					}
					clonedStepModel.addMonomerPlates(clonedMonPlatesList);
				}
				// Check for Product Plate/Plate well changes in each Step
				List<ProductPlate> actualProdPlatesList = actualStepModel.getProductPlates();
				ArrayList<ProductPlate> clonedProdPlatesList = new ArrayList<ProductPlate>();
				if (actualProdPlatesList != null && actualProdPlatesList.size() > 0) {
					int sizeProd = actualProdPlatesList.size();
					for (int i = 0; i < sizeProd; i++) {
						ProductPlate plate = actualProdPlatesList.get(i);
						PlateWell<ProductBatchModel> wells[] = plate.getAllModifedWellsClone();
						if (plate.isModelChanged() || (wells != null && wells.length > 0)) {
							clonedProdPlatesList.add((ProductPlate) plate.deepClone());
						}
					}
					clonedStepModel.addProductPlates(clonedProdPlatesList);
				}
             
				clonedPageModel.addReactionStep(clonedStepModel);	
			}// for each step iterate

			// Check for Registered Plate/PlateWell
			List<ProductPlate> actualRegisterPlatesList = actualPageModel.getRegisteredPlates();
			ArrayList<ProductPlate> clonedRegisterPlatesList = new ArrayList<ProductPlate>();
			if (actualRegisterPlatesList != null && actualRegisterPlatesList.size() > 0) {
				int sizeReg = actualRegisterPlatesList.size();
				for (int i = 0; i < sizeReg; i++) {
					ProductPlate plate = actualRegisterPlatesList.get(i);
					PlateWell<ProductBatchModel> wells[] = plate.getAllModifedWellsClone();
					if (plate.isModelChanged() || (wells != null && wells.length > 0)) {
						clonedRegisterPlatesList.add((ProductPlate) plate.deepClone());
					}
				}
				clonedPageModel.setRegisteredPlates(clonedRegisterPlatesList);
			}
			
			//Clone PseudoPlate
			PseudoProductPlate plate = actualPageModel.getPseudoProductPlate(false);
			if(plate != null )
			{
			 clonedPageModel.setPseudoProductPlate((PseudoProductPlate)plate.deepClone());
			}

		}
		return clonedPageModel;
	}
}