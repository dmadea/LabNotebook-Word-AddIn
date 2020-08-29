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
package com.chemistry.enotebook.client.gui.common.utils;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RepeatExpHelper {
	private static final Log log = LogFactory.getLog(RepeatExpHelper.class);
	
	public static void copyDesign(NotebookPageModel actualPageModel, NotebookPageModel pageModelNew) {
		
		// check for scheme changes. ( Step need to be included by default all the time)
		List<ReactionStepModel> stepList = actualPageModel.getReactionSteps();
		int size = stepList.size();
		for (int stepNo = 0; stepNo < size; stepNo++) {
			
			ReactionStepModel actualStepModel = actualPageModel.getReactionStep(stepNo);
			ReactionStepModel copiedStepModel = new ReactionStepModel();
			copiedStepModel.deepCopy(actualStepModel);
			ReactionSchemeModel schemeModel = new ReactionSchemeModel(actualStepModel.getRxnScheme().getReactionType());
			schemeModel.deepCopy(actualStepModel.getRxnScheme());
			copiedStepModel.setRxnScheme(schemeModel);
			
			//Check for MonomerBatches List and BatchModel 
			List<BatchesList<MonomerBatchModel>> actualMonBatchesList = actualStepModel.getMonomers();
			ArrayList<BatchesList<MonomerBatchModel>> copiedMonBatchesList = new ArrayList<BatchesList<MonomerBatchModel>>();
			if (actualMonBatchesList != null) {
				for (BatchesList<MonomerBatchModel> origList : actualMonBatchesList) {
					if (origList != null) {
						BatchesList<MonomerBatchModel> copiedList = new BatchesList<MonomerBatchModel>();
						copiedList.deepCopy(origList);
						copiedMonBatchesList.add(copiedList);
					}
				}
			}
			resetMonomerBatchAttributes(copiedMonBatchesList);
			copiedStepModel.setMonomers(copiedMonBatchesList);

			//Check for ProductBatches List and BatchModel changes
			ArrayList<BatchesList<ProductBatchModel>> actualProdBatchesList = actualStepModel.getProducts();
			ArrayList<BatchesList<ProductBatchModel>> copiedProdBatchesList = new ArrayList<BatchesList<ProductBatchModel>>();
			if (actualProdBatchesList != null) {
				for (BatchesList<ProductBatchModel> origList : actualProdBatchesList) {
					BatchesList<ProductBatchModel> copiedList = new BatchesList<ProductBatchModel>();
					copiedList.deepCopy(origList);
					// Last step && User Added Batch
					String position = copiedList.getPosition() == null ? "" : copiedList.getPosition(); // to avoid null-pointer if getPosition returns null
					if ((stepNo + 1) == size && position.equals(CeNConstants.PRODUCTS_USER_ADDED)) {
						continue;
					}
					copiedProdBatchesList.add(copiedList);
				}
			}
			
			// Remove product batches from lists if singleton experiment
			if (actualPageModel.isSingletonExperiment() || actualPageModel.isConceptionExperiment()) {
				copiedProdBatchesList = removeProductBatchesFromLists(copiedProdBatchesList);
			}
			
			//add these to cloned step
			resetCopiedProductBatchAttributes(copiedProdBatchesList);
			copiedStepModel.setProducts(copiedProdBatchesList);
			
			//Check for StoichElement batches changes
			ArrayList<MonomerBatchModel> copiedStoichElementList = new ArrayList<MonomerBatchModel>();
			BatchesList<MonomerBatchModel> actualStoicBatchesList = actualStepModel.getStoicBatchesList();
			if (actualStoicBatchesList != null) {
				List<MonomerBatchModel> actualStoichElementList = actualStoicBatchesList.getBatchModels();
				if (actualStoichElementList != null) {
					for (BatchModel actualModel : actualStoichElementList) {
						MonomerBatchModel copiedMonModel = new MonomerBatchModel();
						copiedMonModel.deepCopy((MonomerBatchModel) actualModel);
						copiedStoichElementList.add(copiedMonModel);
					}
				}
			}
			BatchesList<MonomerBatchModel> copiedStoicBatchesList = new BatchesList<MonomerBatchModel>();
			if (actualStoicBatchesList != null) {
				copiedStoicBatchesList.setPosition(actualStoicBatchesList.getPosition());
			}
			copiedStoicBatchesList.addAllBatches(copiedStoichElementList);
			copiedStoicBatchesList.setModelChanged(true); // should be handled by the addAllBatches call.
			//add these StoichElement to cloned step
			copiedStepModel.setStoicBatchesList(copiedStoicBatchesList);
			
			// Check for Monomer Plate/Plate well changes in each Step
/*			List actualMonPlatesList = actualStepModel.getMonomerPlates();
			List copiedMonPlatesList = new ArrayList();
			if (actualMonPlatesList != null && actualMonPlatesList.size() > 0) {
				int sizeMon = actualMonPlatesList.size();
				for (int i = 0; i < sizeMon; i++) {
					MonomerPlate plate = (MonomerPlate) actualMonPlatesList.get(i);
					MonomerPlate  copiedPlate = new MonomerPlate();
					copiedPlate.deepCopy(plate);
					copiedMonPlatesList.add(copiedPlate);
				}
				copiedStepModel.addMonomerPlates(copiedMonPlatesList);
			}
			// Check for Product Plate/Plate well changes in each Step
			List actualProdPlatesList = actualStepModel.getProductPlates();
			ArrayList copiedProdPlatesList = new ArrayList();
			if (actualProdPlatesList != null && actualProdPlatesList.size() > 0) {
				int sizeProd = actualProdPlatesList.size();
				for (int i = 0; i < sizeProd; i++) {
					ProductPlate plate = (ProductPlate) actualProdPlatesList.get(i);
					ProductPlate copiedPlate = new ProductPlate();
					copiedPlate.deepCopy(plate);
					copiedProdPlatesList.add(copiedPlate);
				}
				copiedStepModel.addProductPlates(copiedProdPlatesList);
			}
*/         
			pageModelNew.addReactionStep(copiedStepModel);	
		}// for each step iterate

		// Check for Registered Plate/PlateWell
/*		List actualRegisterPlatesList = actualPageModel.getRegisteredPlates();
		ArrayList copiedRegisterPlatesList = new ArrayList();
		if (actualRegisterPlatesList != null && actualRegisterPlatesList.size() > 0) {
			int sizeReg = actualRegisterPlatesList.size();
			for (int i = 0; i < sizeReg; i++) {
				ProductPlate plate = (ProductPlate) actualRegisterPlatesList.get(i);
				ProductPlate copiedPlate = new ProductPlate();
				copiedPlate.deepCopy(plate);
				copiedRegisterPlatesList.add(copiedPlate);
			}
			
			
			setRegisteredPlates(copiedRegisterPlatesList);
		}
*/		
		copyPageHeaderDesign(actualPageModel, pageModelNew);
		
		//Clone PseudoPlate
		PseudoProductPlate plate = actualPageModel.getPseudoProductPlate(false);//Refresh and reload.
		if(plate != null )
		{
			PseudoProductPlate copiedPlate = new PseudoProductPlate();
			copiedPlate.deepCopy(plate);
			copiedPlate.setBatches(pageModelNew.getNonPlatedBatches());
			setPlatesIntoWells(copiedPlate);
			pageModelNew.setPseudoProductPlate(copiedPlate);
			pageModelNew.getPseudoProductPlate(true);
		}
		
	}

	private static ArrayList<BatchesList<ProductBatchModel>> removeProductBatchesFromLists(List<BatchesList<ProductBatchModel>> copiedProdBatchesList) {
		ArrayList<BatchesList<ProductBatchModel>> newProdBatchesList = new ArrayList<BatchesList<ProductBatchModel>>();
		if (copiedProdBatchesList != null) {
			for (BatchesList<ProductBatchModel> list : copiedProdBatchesList) {
				if (list != null) {
					List<ProductBatchModel> batchesList =  list.getBatchModels();
					if (batchesList != null) {
						ArrayList<ProductBatchModel> newBatchesList = new ArrayList<ProductBatchModel>();
						for (ProductBatchModel batch : batchesList) {
							if (batch != null && batch.getBatchType() != null && batch.getBatchType().equals(BatchType.INTENDED_PRODUCT)) {
								newBatchesList.add(batch);
							}
						}
						if (newBatchesList != null && newBatchesList.size() > 0) {
							list.setBatchModels(newBatchesList);
							newProdBatchesList.add(list);
						}
					}
				}				
			}
		}
		return newProdBatchesList;
	}

	private static void resetCopiedProductBatchAttributes(ArrayList<BatchesList<ProductBatchModel>> copiedProdBatchesList) {
		for (int i=0; i<copiedProdBatchesList.size(); i++)
		{
			BatchesList<ProductBatchModel> batchesList = copiedProdBatchesList.get(i);
			List<ProductBatchModel> productBatchModelsList = batchesList.getBatchModels();
			for (int j=0; j<productBatchModelsList.size(); j++ ) {
				ProductBatchModel productBatchModel = (ProductBatchModel)productBatchModelsList.get(j);
				BatchRegInfoModel batchRegInfoModel = new BatchRegInfoModel(productBatchModel.getKey());
				productBatchModel.setRegInfo(batchRegInfoModel);
				productBatchModel.setConversationalBatchNumber("");
				//productBatchModel.setPurityAmount(new AmountModel(UnitType.SCALAR, 100));
				//productBatchModel.setVolumeAmount(new AmountModel(UnitType.VOLUME));
				//productBatchModel.setWeightAmount(new AmountModel(UnitType.MASS));

				// ProductBatchModel specific props
				productBatchModel.setTheoreticalWeightAmount(new AmountModel(UnitType.MASS));
				productBatchModel.setTheoreticalMoleAmount(new AmountModel(UnitType.MOLES));
				productBatchModel.setTheoreticalYieldPercentAmount(new AmountModel(UnitType.SCALAR, "-1.0"));
				productBatchModel.setAnalyticalPurityList(new ArrayList<PurityModel>());
				
				productBatchModel.setSelected(false);
				productBatchModel.setRegistered(false);
				productBatchModel.setToDelete(false);
				productBatchModel.setTotalWellWeightAmount(new AmountModel(UnitType.MASS));
				productBatchModel.setTotalTubeWeightAmount(new AmountModel(UnitType.MASS));
				productBatchModel.setTotalWellVolumeAmount(new AmountModel(UnitType.VOLUME));
				productBatchModel.setTotalTubeVolumeAmount(new AmountModel(UnitType.VOLUME));
				productBatchModel.setTotalWeight(new AmountModel(UnitType.MASS));
				productBatchModel.setTotalVolume(new AmountModel(UnitType.VOLUME));
				productBatchModel.setTotalMolarity(new AmountModel(UnitType.MOLAR));
				productBatchModel.setPreviousMolarAmount(new AmountModel(UnitType.MOLAR));
				productBatchModel.setLoadedFromDB(false);
			}
		}
	}


	private static void resetMonomerBatchAttributes(ArrayList<BatchesList<MonomerBatchModel>> copiedMonBatchesList) {
		for (int i=0; i< copiedMonBatchesList.size(); i++)
		{
			BatchesList<MonomerBatchModel> batchesList = copiedMonBatchesList.get(i);
			 List<MonomerBatchModel> mononerBatchModelsList = batchesList.getBatchModels();
			 for (int j=0; j<mononerBatchModelsList.size(); j++)
			 {
				 MonomerBatchModel monomerBatchModel = (MonomerBatchModel)mononerBatchModelsList.get(j);
				 //monomerBatchModel.setConversationalBatchNumber("");
				 //monomerBatchModel.setPurityAmount(new AmountModel(UnitType.SCALAR, 100));
				 //monomerBatchModel.setSaltForm(new SaltFormModel("00"));
				 //monomerBatchModel.setSoluteAmount(new AmountModel(UnitType.MASS));
					
					//MonomerBatch Specific properties
				 monomerBatchModel.setSelected(false);
				 monomerBatchModel.setDeliveredWeight(new AmountModel(UnitType.MASS));
				 monomerBatchModel.setDeliveredVolume(new AmountModel(UnitType.VOLUME));
				 monomerBatchModel.setDeliveredMonomerID("");
				 //monomerBatchModel.setSolute("");
				 monomerBatchModel.setModelChanged(false);
				 monomerBatchModel.setToDelete(false);
				 monomerBatchModel.setTotalWeight(new AmountModel(UnitType.MASS));
				 monomerBatchModel.setTotalVolume(new AmountModel(UnitType.VOLUME));
				 monomerBatchModel.setTotalMolarity(new AmountModel(UnitType.MOLAR));
				 monomerBatchModel.setPreviousMolarAmount(new AmountModel(UnitType.MOLAR));
				 //monomerBatchModel.setSolventsAdded("");
				 monomerBatchModel.setLoadedFromDB(false);
			 }
		}
	}

	private static void copyPageHeaderDesign(NotebookPageModel src, NotebookPageModel pageModelNew) {
		// Also set the userid and site code for the page
		//NTID,SITE code should be same as that of the User who is repeating the exp(it can be 
		// his/her or some other user's experiment.It should not be the source exp's)
		//ageModelNew.setUserName(src.getUserName());
		//pageModelNew.setBatchOwner(src.getBatchOwner());
		//pageModelNew.setBatchCreator(src.getBatchCreator());
		//pageModelNew.setSiteCode(src.getSiteCode());
		Timestamp ts = CommonUtils.getCurrentTimestamp();
		pageModelNew.setCreationDateAsTimestamp(ts);
		pageModelNew.setModificationDateAsTimestamp(ts);
		pageModelNew.setPageType(src.getPageType());
		pageModelNew.setDesignSite(src.getDesignSite()) ;
		pageModelNew.setCenVersion("1");
		pageModelNew.setVersion(1);
				
		NotebookPageHeaderModel pageHeaderModelNew = pageModelNew.getPageHeader();
		NotebookPageHeaderModel pageHeaderModelSrc = src.getPageHeader();
		
		pageHeaderModelNew.setTotalReactionSteps(pageHeaderModelSrc.getTotalReactionSteps());
		pageModelNew.setPageStatus(CeNConstants.PAGE_STATUS_OPEN);
		pageModelNew.setSubject(src.getSubject());
		
		pageModelNew.setLiteratureRef(src.getLiteratureRef()) ;
		pageModelNew.setTaCode(src.getTaCode()) ;
		pageModelNew.setProjectCode(src.getProjectCode()) ;

		pageModelNew.setProjectAlias(src.getProjectAlias()) ;
		pageModelNew.setProtocolID(src.getProtocolID()) ;
		pageModelNew.setSeriesID(src.getSeriesID()) ;
		pageModelNew.setConceptorNames(src.getConceptorNames()) ;
		pageModelNew.setConceptionKeyWords(src.getConceptionKeyWords()) ;
		pageHeaderModelNew.setAutoCalcOn(pageHeaderModelSrc.isAutoCalcOn()) ;
		pageHeaderModelNew.setTableProperties(src.getTableProperties()) ; 
		pageHeaderModelNew.setSpid(src.getSPID()); 
		pageHeaderModelNew.setComments(pageHeaderModelSrc.getComments());
		pageHeaderModelNew.setDescription(pageHeaderModelSrc.getDescription());
		pageModelNew.setDesignSite(src.getDesignSite());
		//setVrxnId(src.vrxnId);
		pageHeaderModelNew.setDesignUsers(pageHeaderModelSrc.getDesignUsers());
		pageModelNew.setSummaryPlanId(src.getSummaryPlanId()); 
		pageHeaderModelNew.setIntermediatePlanIds(pageHeaderModelSrc.getIntermediatePlanIds()); 
		//setScreenPanels(src.screenPanels); 
		pageHeaderModelNew.setScale(pageHeaderModelSrc.getScale()); 
		pageHeaderModelNew.setPrototypeLeadIds(pageHeaderModelSrc.getPrototypeLeadIds()); 
		pageHeaderModelNew.setDesignSubmitter(pageHeaderModelSrc.getDesignSubmitter());
		pageHeaderModelNew.setNotifyList(pageHeaderModelSrc.getNotifyList());
				
		pageModelNew.setProcedure(src.getProcedure());
		pageModelNew.setProcedureWidth(src.getProcedureWidth());
		pageModelNew.setLoadedFromDB(false);
	}

//	 This is where the lotNumber is created
	public static void setNotebookBatchNumbers(NotebookPageModel pageModel) {
		NotebookRef nbRef = pageModel.getNbRef();
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.setNotebookBatchNumbers() - " + nbRef.getNotebookRef());
		if (pageModel != null && nbRef != null) {
			ArrayList<ReactionStepModel> steps = pageModel.getIntermediateReactionSteps();
			ReactionStepModel sumStep = pageModel.getSummaryReactionStep();
			// If only summary step is present
			if (steps != null && steps.size() < 2 && sumStep.getMonomers() != null) {
				ArrayList<BatchesList<ProductBatchModel>> productBatchesList = sumStep.getProducts();
				if (productBatchesList != null) {
					int lot = 1;
					for (BatchesList<ProductBatchModel> prodList : productBatchesList) {
						for (ProductBatchModel model : prodList.getBatchModels()) {
							try {
								if (pageModel.isParallelExperiment()) {
									model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
								} else {
									if (!CeNConstants.PRODUCTS_SYNC_INTENDED.equals(model.getPosition())) {
										continue;
									}
									String lotstr = "";
									if (lot < 10)
										lotstr = "00" + lot;
									else if (lot < 100)
										lotstr = "0" + lot;
									else 
										lotstr = "" + lot;
									model.setBatchNumber(nbRef.getNotebookRef() + "-" + lotstr);
									

								}
							} catch (Exception e) {
								log.error("Error creating and assigning Note book Batch number" + e.getMessage());
							}
							lot = lot + 1;
						}
					}
					log.info("Final lot number after notebook batch assignment :" + lot);
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
				ReactionStepModel finalstep = (ReactionStepModel) pageModel.getReactionStep(finalStepNo);
				//ArrayList finalproductBatchesList = sumStep.getProducts();
				//
				for (BatchesList<ProductBatchModel> prodList : finalstep.getProducts()) {
					for (ProductBatchModel model : prodList.getBatchModels()) {
						try {
							if (pageModel.isParallelExperiment()) {
								model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
							} else {
								if (!CeNConstants.PRODUCTS_SYNC_INTENDED.equals(model.getPosition())) {
									continue;
								}
								String lotstr = "";
								if (lot < 10)
									lotstr = "00" + lot;
								else if (lot < 100)
									lotstr = "0" + lot;
								else 
									lotstr = "" + lot;
								model.setBatchNumber(nbRef.getNotebookRef() + "-" + lotstr);

							}
						} catch (Exception e) {
							log.error("Error creating and assigning Note book Batch number" + e.getMessage());
						}
						lot = lot + 1;
					}
				}
				
				for (ReactionStepModel step : pageModel.getReactionSteps()) {
					for (BatchesList<ProductBatchModel> prodList : step.getProducts()) {
						for (ProductBatchModel model : prodList.getBatchModels()) {
							try {
								if (pageModel.isParallelExperiment()) {
									model.setBatchNumber(new BatchNumber(nbRef.getNotebookRef() + "-" + lot + "A1"));
								} else {
									if (!CeNConstants.PRODUCTS_SYNC_INTENDED.equals(model.getPosition())) {
										continue;
									}
									String lotstr = "";
									if (lot < 10)
										lotstr = "00" + lot;
									else if (lot < 100)
										lotstr = "0" + lot;
									else 
										lotstr = "" + lot;
									model.setBatchNumber(nbRef.getNotebookRef() + "-" + lotstr);
								}
							} catch (Exception e) {
								log.error("Error creating and assigning Note book Batch number" + e.getMessage());
							}
							lot = lot + 1;
						}
					}
				}
				
				log.info("Final lot number after notebook batch assignment :" + lot);
			}// else
		}// if has data
		
		

		stopwatch.stop();
	}

	private static void setPlatesIntoWells(AbstractPlate<?> copiedPlate) {
		if (copiedPlate != null) {
			PlateWell<?>[] wells = copiedPlate.getWells();
			if (wells != null) {
				for (PlateWell<?> plateWell : wells) {
					plateWell.setPlate(copiedPlate);
				}
			}
		}
	}
}
