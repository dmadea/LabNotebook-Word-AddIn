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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.publisher.classes.PublishEntityInfo;
import com.chemistry.enotebook.util.Stopwatch;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

//Utility functions that are applicable for ParallelExp notebook page
//For a batch - identifying  number of times it is used in that step and set that property to the batch
//For a batch - identifying all the precursors used to make this batch and set those to the batch
//For a batch -  Trigger marking the status (not continue etc) based on the status of its precursors set.
//Link PlateWell  batch to  its corresponding batch object ( in Monomers/Products list ) by object reference
//For a batch in CeN  match it to batch in CompoundManagement Orders and set back the ordered amount etc to the CeN batch
//BatchesList function to identify is a Amount value is set by user at List Level or at batch level

public class ParallelExpModelUtils {

	private static final Log log = LogFactory.getLog(ParallelExpModelUtils.class);

	NotebookPageModel pageModel;

	public ParallelExpModelUtils(NotebookPageModel pageModel) {

		// Check if model has data
		if (pageModel == null)
			return;
		else
			this.pageModel = pageModel;

	}

	public void sortProductBatchesList() {
		if (this.pageModel.getReactionSteps() != null) {
			int stepSize = pageModel.getReactionSteps().size();
			// for each step do the calc
			for (int stepNo = 0; stepNo < stepSize; stepNo++) {
				ReactionStepModel stepModel = pageModel.getReactionStep(stepNo);
				List<BatchesList<ProductBatchModel>>  prodBatchesList = stepModel.getProducts();
				if (prodBatchesList != null && prodBatchesList.size() > 1) {
					BatchesList<ProductBatchModel> prodBList = prodBatchesList.get(0);
					// There will always be only two elements, one for Products (P1) and other one for user added batches (PUA).
					if (prodBList.getPosition() != null) { // vb 6/25 this is null for singletons
						if (!prodBList.getPosition().equals(CeNConstants.PRODUCTS_DESIGN_DSP)) {
							ArrayList<BatchesList<ProductBatchModel>> newBatchesList = new ArrayList<BatchesList<ProductBatchModel>>();
							newBatchesList.add(prodBatchesList.get(1));
							newBatchesList.add(prodBatchesList.get(0));
							stepModel.setProducts(newBatchesList);
						}
					}
				}
			}
		}
	}

	public void sortProductBatchListsByBatchNumber() {
		Comparator c = new NbkBatchNumberComparator();
		sortProductBatchListsByBatchNumber(c);
	}
	
	public void sortProductBatchListsByBatchNumber(Comparator c) {
		for (ReactionStepModel step : pageModel.getReactionSteps()) {
			List<BatchesList<ProductBatchModel>> batchesLists = step.getProductBatchLists();
			for (BatchesList<ProductBatchModel> batchesList : batchesLists) {
				List<ProductBatchModel> batches = batchesList.getBatchModels();
				Collections.sort(batches, c);
			}
			
			Collections.sort(batchesLists, new Comparator<BatchesList<ProductBatchModel>>() {
				public int compare(final BatchesList<ProductBatchModel> o1, final BatchesList<ProductBatchModel> o2) {
					if (o1.getBatchModels().size() == 0) {
						return -1;
					}
					if (o2.getBatchModels().size() == 0) {
						return 1;
					}
					final Integer i1 = Integer.valueOf(o1.getBatchModels().get(0).getIntendedBatchAdditionOrder());
					final Integer i2 = Integer.valueOf(o2.getBatchModels().get(0).getIntendedBatchAdditionOrder());
					return i1.compareTo(i2);
				}
			});
		}
	}

	public void linkPlateWellBatchToCorrespondingBatch() {
		// Go through each step and link plates in that step to corresponding batches
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.linkPlateWellBatchToCorrespondingBatch()");
		// Check if model has data
		if (pageModel == null)
			return;
		if (this.pageModel.getReactionSteps() != null) {
			int stepSize = this.pageModel.getReactionSteps().size();
			// for each step do the recursion
			for (int stepNo = 0; stepNo < stepSize; stepNo++) {
				ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
				// link monomer plates
				List<MonomerPlate> monPlateList = stepModel.getMonomerPlates();
				List<BatchesList<MonomerBatchModel>>  monList = stepModel.getMonomers();
				if (monPlateList != null && monList != null) {
					int platesSize = monPlateList.size();
					// each plate recursive
					for (int i = 0; i < platesSize; i++) {
						MonomerPlate mPlate = (MonomerPlate) monPlateList.get(i);
						PlateWell wells[] = mPlate.getWells();
						if (wells != null && wells.length > 0) {
							int wellSize = wells.length;
							for (int k = 0; k < wellSize; k++) {
								PlateWell well = wells[k];
								// Handling empty wells on a plate
								if (well == null) {
									log.info("PlateWell is null");
									continue;
								}
								// sHandling skipped wells on a plate
								else if (well.getBatch() == null || well.getBatch().getKey() == null) {
									log.info("PlateWell has no MonomerBatchModel or its key is null:" + well.getPosition());
									// //set with Well with empty MonomerBatchModel for client side processing.
									// //Key should be null otherwise there will be FK error wile saving
									// well.setBatch(new MonomerBatchModel(well.getBatch()));
								} else {
									MonomerBatchModel model = getMatchingMonomerBatch(well.getBatch().getKey(), monList);
									well.setBatch(model);
								}

							}// for each well
						}
					}// for each plate
				}// if has monomer plates

				// link product plates
				List<ProductPlate>  prodPlateList = stepModel.getProductPlates();
				List<BatchesList<ProductBatchModel>>  prodList = stepModel.getProducts();
				if (prodPlateList != null && prodList != null) {
					int platesSize = prodPlateList.size();
					// each plate recursive
					for (int i = 0; i < platesSize; i++) {
						ProductPlate mPlate = (ProductPlate) prodPlateList.get(i);
						PlateWell wells[] = mPlate.getWells();
						if (wells != null && wells.length > 0) {
							int wellSize = wells.length;
							for (int k = 0; k < wellSize; k++) {
								PlateWell well = wells[k];
								// Handling empty wells on a plate
								if (well == null) {
									log.info("PlateWell is null");
									continue;
								}
								// sHandling skipped wells on a plate
								else if (well.getBatch() == null || well.getBatch().getKey() == null) {
									log.info("PlateWell has no ProductBatchModel or its key is null:" + well.getPosition());
									// set with Well with empty ProductBatchModel for client side processing.
									// Key should be null otherwise there will be FK error wile saving
									// well.setBatch(new ProductBatchModel(well.getBatch()));
								} else {
									ProductBatchModel model = getMatchingProductBatch(well.getBatch().getKey(), prodList);
									well.setBatch(model);
								}

							}// for each well
						}
					}// for each plate
				}// if has monomer plates
			}// for each step
		}// if model has steps

		// Link the ProductBatches to Pseudpplate Wells
		PseudoProductPlate psedoPlate = pageModel.getPseudoProductPlate(false);
		if (psedoPlate != null && psedoPlate.getWells() != null) {
			PlateWell wells[] = psedoPlate.getWells();
			List<ProductBatchModel>  allProdList = pageModel.getAllProductBatchModelsInThisPage();
			if (wells != null && wells.length > 0) {
				int wellSize = wells.length;
				for (int k = 0; k < wellSize; k++) {
					PlateWell well = wells[k];
					if (well == null || well.getBatch() == null) {
						continue;
					}
					ProductBatchModel model = getMatchingProductBatchFromProductBatchModelList(well.getBatch().getKey(),
							allProdList);
					if (model != null) {
						well.setBatch(model);
					}
				}// for each well
			}
		}

		// Link the ProductBatches to Register plate Wells
		List<ProductPlate> registeredPlatesList = pageModel.getRegisteredPlates();
		for (int i = 0; i < registeredPlatesList.size(); i++) {
			ProductPlate registeredPlate = (ProductPlate) registeredPlatesList.get(i);
			if (registeredPlate != null && registeredPlate.getWells() != null) {
				PlateWell wells[] = registeredPlate.getWells();
				List<ProductBatchModel> allProdList = pageModel.getAllProductBatchModelsInThisPage();
				if (wells != null && wells.length > 0) {
					int wellSize = wells.length;
					for (int k = 0; k < wellSize; k++) {
						PlateWell well = wells[k];
						if (well == null || well.getBatch() == null) {
							continue;
						}
						ProductBatchModel model = getMatchingProductBatchFromProductBatchModelList(well.getBatch().getKey(),
								allProdList);
						well.setBatch(model);
					}// for each well
				}
			}
		}
		log.info("Linking PlateWell Batch to Batch object complete");
		stopwatch.stop();
	}

	public void calcNoOfTimesMonomerUsed() {

		// Go through each step monomers and calc of no of times each monomer used in a reaction
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.calcNoOfTimesMonomerUsed()");
		int stepSize = this.pageModel.getReactionSteps().size();

		// for each step do the recursion
		for (int stepNo = 0; stepNo < stepSize; stepNo++) {
			// Don't calc this for SumamryStep in a multistep reaction.
			// Interim Steps calc will reflect in summaryStep because of Linking in Deduping Logic
			if (stepSize > 1 && stepNo == 0) {
				continue;
			}
			ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
			List<String> prodIDList = stepModel.getAllProductIDs();
			HashMap<String,Integer> map = this.getMonomerINProductsNoTimes(prodIDList);
			List<MonomerBatchModel> monList = stepModel.getAllMonomerBatchModelsInThisStep();
			if (map != null && map.size() > 0 && monList != null && monList.size() > 0) {
				for (MonomerBatchModel model : monList) {
					String monID = model.getMonomerId();
					if (map.containsKey(monID)) {
						Integer count = (Integer) map.get(monID);
						if (count != null) {
							model.setNoOfTimesUsed(count.intValue());
						}

					} else {
						// There are no products from this monomer.like from Library shaping etc
						model.setStatus(CeNConstants.MONOMER_BATCH_STATUS_NOT_AVAILABLE);
						log.debug("Step no:" + stepNo + " Monomer is not used in a reaction:" + model.getMonomerId());
					}

				}
			}
		}// each step

		log.info("Calc of no of times each monomer used in a reaction complete");
		stopwatch.stop();

	}

	public void setPrecursorsForProductsAndMonomerBatches() {
		// Go through each step monomers,products and find precursors
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.setPrecursorsForBatches()");
		int stepSize = this.pageModel.getReactionSteps().size();
		// for each step do the recursion
		for (int stepNo = 0; stepNo < stepSize; stepNo++) {
			// Don't calc this for SumamryStep in a multistep reaction.
			// Interim Steps calc will reflect in summaryStep because of Linking in Deduping Logic
			if (stepSize > 1 && stepNo == 0) {
				continue;
			}
			ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);

			// Do precursors for Products
			List<String> prodIDList = stepModel.getAllProductIDs();
			List<MonomerBatchModel> monomerList = stepModel.getAllMonomerBatchModelsInThisStep();
			HashMap<String, List<String>> map = getReactantsForProducts(prodIDList, monomerList);
			List<ProductBatchModel> prodList = stepModel.getAllProductBatchModelsInThisStep();
			if (map != null && map.isEmpty() == false && prodList != null && prodList.isEmpty() == false) {
				for (ProductBatchModel model : prodList) {
					String prodID = model.getProductId();
					if (StringUtils.isNotBlank(prodID) && map.containsKey(prodID)) {
						List<String> precurList = map.get(prodID);
						if (precurList != null && precurList.isEmpty() == false) {
							model.setReactantBatchKeys(precurList);
						}
					}
				}
			}

			// Do generated Products for Monomers
			List<String> monIDList = stepModel.getAllMonomerIDs();
			List<ProductBatchModel> productsList = stepModel.getAllProductBatchModelsInThisStep();
			HashMap<String, List<String>>  map2 = this.getGeneratedProductsForMonomers(monIDList, productsList);
			List<MonomerBatchModel> monList = stepModel.getAllMonomerBatchModelsInThisStep();
			if (map2 != null && map2.size() > 0 && monList != null && monList.isEmpty() == false) {
				for (MonomerBatchModel model : monList) {
					String monID = model.getMonomerId();
					if (StringUtils.isNotBlank(monID) && map2.containsKey(monID)) {
						List<String> precurList = map2.get(monID);
						if (precurList != null && precurList.isEmpty() == false) {
							model.setGenratedProductBatchKeys(precurList);
						}
					}
				}
			}

		}// each step
		log.info("Setting reagents,products generated for each batch complete");
		// Do the precurosrs now
		setPrecurosrsForProducts();
		setReactantCompoundIDsAsPrecursors();
		stopwatch.stop();

	}

	public void setPrecurosrsForProducts() {

		// Go through each step monomers,products and find precursors
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.findPrecurosrsForProducts()");
		int stepSize = this.pageModel.getReactionSteps().size();
		// for each step do the recursion
		for (int stepNo = 0; stepNo < stepSize; stepNo++) {
			// Don't calc this for final intermediate Step in a multistep reaction.
			// summaryStep calc will reflect in final intermediate step product due Linking in Deduping Logic
			if (stepSize > 1 && stepNo == (stepSize - 1)) {
				continue;
			}
			ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);

			// Do precursors for Products
			List<String> prodIDList = stepModel.getAllProductIDs();
			List<MonomerBatchModel> monomerList = stepModel.getAllMonomerBatchModelsInThisStep();
			HashMap<String, List<String>>  map = this.getPrecurosrsForProductsMap(prodIDList, monomerList);
			List<ProductBatchModel> prodList = stepModel.getAllProductBatchModelsInThisStep();
			if (map != null && map.size() > 0 && prodList != null && prodList.size() > 0) {
				for (ProductBatchModel model : prodList) {
					String prodID = model.getProductId();
					if (map.containsKey(prodID)) {
						List<String> precurList = map.get(prodID);
						if (precurList != null) {
							model.setPrecursorBatchKeys(precurList);
						}

					}

				}
			}
		}// each step
		log.info("Setting precursors for each batch complete");
		stopwatch.stop();
	}

//	public void calcMolecularPropsForAllBatches() {
//		Stopwatch stopwatch = new Stopwatch();
//		stopwatch.start("ParallelExpModelUtils.calcMolecularPropsForAllBatches()");
//		if (pageModel == null || pageModel.getReactionSteps() == null || pageModel.getReactionSteps().size() == 0) {
//			return;
//		}
//		try {
//			SearchService cusAdaptor = null;
//			boolean isCUSAvailable = false;
//			try {
//				cusAdaptor = SearchServiceFactory.getService();
//				isCUSAvailable = true;
//			} catch (Exception e) {
//				log.error("Error while initializing CSS .calc of MF and MW for monomers will not happen.");
//				e.printStackTrace();
//			}
//			ChemistryPropCalc propCalc = new ChemistryPropCalc();
//			int stepSize = pageModel.getReactionSteps().size();
//			// for each step do the calc
//			for (int stepNo = 0; stepNo < stepSize; stepNo++) {
//				ReactionStepModel stepModel = pageModel.getReactionStep(stepNo);
//				// Calc Props for monomers
//				for (BatchesList<MonomerBatchModel> monBList : stepModel.getMonomers()) {
//					List<String> ids = monBList.getMonomerIDList();
//					if (ids != null && ids != null) {
//						// Need to check if the ID is of type A:B(interm prod as monomer). Then CLS will not
//						// be able to lookup the structure.
//						if (CommonUtils.isThisMonomerAIntermediateProduct((String) ids.get(0))) {
//							// This monomer will get structure from I.P in previous step using
//							// copy over Structures util method.
//							for (MonomerBatchModel model : monBList.getBatchModels()) {
//								byte[] struc = model.getCompound().getStringSketch();
//								if (struc != null) {
//									MoleculePropertyInfo propInfo = propCalc.getMoleculefromChimeString(new String(struc));
//									model.setMolecularFormula(propInfo.getMF());
//									model.getMolecularWeightAmount().setValue(propInfo.getMW());
//									// Also set same values for parent compound
//									model.getCompound().setMolFormula(propInfo.getMF());
//									model.getCompound().setMolWgt(propInfo.getMW());
//									model.getCompound().setExactMass(propInfo.getExactMass());
//								}
//							}
//						} else {
//							if (!isCUSAvailable)
//								continue;
//							List<String> molStrings = new ArrayList<String>();
//							for (String id : ids)
//								molStrings.add(cusAdaptor.getStructureByCompoundNo(id).get(0));
//							if (molStrings != null) {
//								int idSize = ids.size();
//								int clsStrSize = molStrings.size();
//								if (clsStrSize != idSize) {
//									continue;
//								}
//								for (int k = 0; k < idSize; k++) {
//									String ID = (String) ids.get(k);
//									MoleculePropertyInfo propInfo = propCalc.getMoleculefromMDLString(molStrings.get(k));
//									MonomerBatchModel model = monBList.getMatchingBatch(ID);
//									// model.setMolecularFormula(propInfo.getMF());
//									// model.getMolecularWeightAmount().setValue(propInfo.getMW());
//									// Also set same values for parent compound
//									model.getCompound().setMolFormula(propInfo.getMF());
//									model.getCompound().setMolWgt(propInfo.getMW());
//									model.getCompound().setExactMass(propInfo.getExactMass());
//
//								}
//							}// if CLS has returned
//						}// else use CLS to get structures
//					}
//				}
//
//				// Calc Props for products
//				for (BatchesList<ProductBatchModel> prodList : stepModel.getProducts()) {
//					for (ProductBatchModel model : prodList.getBatchModels()) {
//						byte[] struc = model.getCompound().getStringSketch();
//						if (struc != null) {
//							MoleculePropertyInfo propInfo = propCalc.getMoleculefromChimeString(new String(struc));
//							// These values are at Compound level. BAtch values are calc using salt code etc
//							// model.setMolecularFormula(propInfo.getMF());
//							// model.getMolecularWeightAmount().setValue(propInfo.getMW());
//							// Also set same values for parent compound
//							model.getCompound().setMolFormula(propInfo.getMF());
//							model.getCompound().setMolWgt(propInfo.getMW());
//							model.getCompound().setExactMass(propInfo.getExactMass());
//						}
//					}
//				}
//
//			}// for each step
//			log.info("Calc for all Batches complete");
//			stopwatch.stop();
//		} catch (Exception e) {
//			log.error("Error while calculating MF and MW for monomers and products. These properties might have not been calced to all batches", e);
//		}
//
//	}

	// This method should be used only after loading the NotebookPageModel on the client end
	// HashMaps are marked Transient
	public void populateMonomerAndProductHashMaps() {
		// Go through each step monomers,products and find precursors
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.populateMonomerAndProductHashMaps()");
		try {
			int stepSize = this.pageModel.getReactionSteps().size();
			HashMap<String, ProductBatchModel> prodMap = new HashMap<String, ProductBatchModel>();
			HashMap<String, MonomerBatchModel> monMap = new HashMap<String, MonomerBatchModel>();
			// for each step do the recursion
			for (int stepNo = 0; stepNo < stepSize; stepNo++) {
				// Don't calc this for SumamryStep in a multistep reaction.
				// Interim Steps calc will reflect in summaryStep because of Linking in Deduping Logic
				if (stepSize > 1 && stepNo == 0) {
					continue;
				}
				ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
				List<MonomerBatchModel> monModels = stepModel.getAllMonomerBatchModelsInThisStep();
				if (monModels != null && monModels.size() > 0) {
					int size = monModels.size();
					for (int i = 0; i < size; i++) {
						MonomerBatchModel model = (MonomerBatchModel) monModels.get(i);
						monMap.put(model.getKey(), model);
					}
				}
				List<ProductBatchModel> prodModels = stepModel.getAllProductBatchModelsInThisStep();
				if (prodModels != null && prodModels.size() > 0) {
					int size = prodModels.size();
					for (int i = 0; i < size; i++) {
						ProductBatchModel model = (ProductBatchModel) prodModels.get(i);
						prodMap.put(model.getKey(), model);
					}
				}

			}// each step
			this.pageModel.setMonomerBatchModelMap(monMap);
			this.pageModel.setProductBatchModelMap(prodMap);
			log.info("Setting populateMonomerAndProductHashMaps");
		} catch (Exception e) {
			log.error("Failed populating Monomer and Product Hash Maps.", e);
		}
		stopwatch.stop();
	}

	public void linkBatchesWithPlateWells() {
		HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> batchPlateWellMap = new HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>>();
		List<PlateWell<ProductBatchModel>> pseudoWellList = null;
		try {
			// Go through each step and link plates in that step to corresponding batches
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("ParallelExpModelUtils.linkBatchesWithPlateWells()");
			// Check if model has data
			if (pageModel == null)
				return;
			// Link the BAtches to PseduWell PlateWells as well
			PseudoProductPlate psedoPlate = pageModel.getGuiPseudoProductPlate();
			if (psedoPlate != null && psedoPlate.getWells() != null) {
				PlateWell<ProductBatchModel> wells[] = psedoPlate.getWells();
				pseudoWellList = Arrays.asList(wells);

			}
			if (this.pageModel.getReactionSteps() != null) {
				int stepSize = this.pageModel.getReactionSteps().size();
				// for each step do the recursion
				for (int stepNo = 0; stepNo < stepSize; stepNo++) {
					ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
					// link monomer plates
					List<PlateWell<MonomerBatchModel>> monPlateWellList = stepModel.getAllMonomerPlateWellsInThisStep();
					List<MonomerBatchModel> allMonList = stepModel.getAllMonomerBatchModelsInThisStep();
					if (monPlateWellList != null && allMonList != null && monPlateWellList.size() > 0) {
						for (MonomerBatchModel model : allMonList) {
							ArrayList<PlateWell<? extends BatchModel>> list = new ArrayList<PlateWell<? extends BatchModel>>();
							list.addAll(getPlateWellsHavingBatch(model.getKey(), monPlateWellList));
							batchPlateWellMap.put(model, list);
						}// for each monomer
					}

					// link product plates
					List<PlateWell<ProductBatchModel>> prodPlateWellList = stepModel.getAllProductPlateWellsInThisStep();
					List<ProductBatchModel> allProdList = stepModel.getAllProductBatchModelsInThisStep();
					if (prodPlateWellList != null && allProdList != null && prodPlateWellList.size() > 0) {
						// Add the PSeudo plate wells to this list since a product batch can be in Pseudoplate and
						// normal product plate at the same time
						prodPlateWellList.addAll(pseudoWellList);
						for (ProductBatchModel model : allProdList) {
							List<PlateWell<ProductBatchModel>> list = getPlateWellsHavingBatch(model.getKey(), prodPlateWellList);

							// This is added to facilitate versioning. The above list will be empty when the new version is created.
							// We have to change the batch in the well that scenario.
							// Make this as a separate method/call from GUIController.java if this part creates any problem while
							// creating new notebook from Design Service.
							if (list == null || list.size() == 0) {
								list = getPlateWellsHavingBatchByBatchNum(model.getBatchNumberAsString(), prodPlateWellList);
								for (int i = 0; i < list.size(); i++) {
									PlateWell<ProductBatchModel> well = list.get(i);
									well.setBatch(model);
								}
							}
							long batchTrackingId = model.getRegInfo().getBatchTrackingId();
							if (batchTrackingId != -1) {
								for (int i = 0; i < list.size(); i++) {
									PlateWell<ProductBatchModel> well = list.get(i);
									well.setBatchTrackingId(batchTrackingId);
								}
							}
							ArrayList<PlateWell<? extends BatchModel>> tmpList = new ArrayList<PlateWell<? extends BatchModel>>();
							tmpList.addAll(list);
							batchPlateWellMap.put(model, tmpList);
						}// for each product
					}
				}// for each step
			}// if model has steps
			// If Page doesn't have Product Plates yet
			if (pageModel.getAllProductPlatesAndRegPlates() == null || pageModel.getAllProductPlatesAndRegPlates().size() == 0) {
				List<ProductBatchModel> allPBModels = pageModel.getAllProductBatchModelsInThisPage();
				int size = allPBModels.size();
				if (size > 0) {
					for (int k = 0; k < size; k++) {
						ProductBatchModel model = allPBModels.get(k);
						ArrayList<PlateWell<ProductBatchModel>> list = getPlateWellsHavingBatch(model.getKey(), pseudoWellList);
						long batchTrackingId = model.getRegInfo().getBatchTrackingId();
						if (batchTrackingId != -1) {
							for (int i = 0; i < list.size(); i++) {
								PlateWell<ProductBatchModel> well = list.get(i);
								well.setBatchTrackingId(batchTrackingId);
							}
						}
						ArrayList<PlateWell<? extends BatchModel>> tmpList = new ArrayList<PlateWell<? extends BatchModel>>();
						tmpList.addAll(list);
						batchPlateWellMap.put(model, tmpList);
						// System.out.println("map size:"+batchPlateWellMap.size());
					}// for each product
				}

			}
			// set the map back to pageModel
			pageModel.setBatchPlateWellsMap(batchPlateWellMap);
			log.info("Linking Batch to PlateWells complete");
			stopwatch.stop();
		} catch (Exception e) {
			log.error("Failed linking batches with plate wells.", e);
		}

	}

	private MonomerBatchModel getMatchingMonomerBatch(String batchKey, List<BatchesList<MonomerBatchModel>> monomerList) {
		if (StringUtils.isNotBlank(batchKey)) {
			for (BatchesList<MonomerBatchModel> list : monomerList) {
				List<MonomerBatchModel> monomers = list.getBatchModels();
				if (monomers != null && monomers.size() > 0) {
					for (MonomerBatchModel model : monomers) {
						if (model == null || model.getKey() == null) {
							log.info("MonomerBatchModel or its key shouldn't be null");
						}
						// if key matches return that model
						if (model != null && model.getKey().compareTo(batchKey) == 0) {
							return model;
						}
					}
				}
			}
	}
		return null;
	}

	private ProductBatchModel getMatchingProductBatch(String batchKey, List<BatchesList<ProductBatchModel>> productList) {
		// in case of skipped well
		if (StringUtils.isNotBlank(batchKey)) {
			for (BatchesList<ProductBatchModel> list : productList) {
				List<ProductBatchModel> products = list.getBatchModels();
				if (products != null && products.size() > 0) {
					for (ProductBatchModel model : products) {
						// if key matches return that model
						if (model.getKey().compareTo(batchKey) == 0) {
							return model;
						}
					}
				}
			}
		}
		return null;
	}

	public void releaseNotebookPageLocalReference() {
		this.pageModel = null;
	}

	// A + B + C --> A:B:C
	// A + B --> A:B
	// [A:B] + C --> [A:B]:C
	// Transformation notation: A:B --> [A:B]
	private HashMap<String, Integer> getMonomerINProductsNoTimes(List<String> productIDList) {
		// HashMap will have
		// Key - MonomerID and Value - Integer ( count)
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try {
			if (productIDList == null || productIDList.size() == 0) {
				return map;
			}

			int size = productIDList.size();
			for (int i = 0; i < size; i++) {
				String IDStr = (String) productIDList.get(i);
				int indx = IDStr.lastIndexOf(":");
				// This is one compound ID string like PF-34557
				if (indx <= 0) {
					if (map.containsKey(IDStr)) {
						Integer times = (Integer) map.get(IDStr);
						int count = times.intValue();
						count = count + 1;
						Integer times2 = new Integer(count);
						map.put(IDStr, times2);

					} else {
						Integer times = new Integer(1);
						map.put(IDStr, times);
					}
					continue;
				}
				// Check if there are more than two compound IDs used like A1:B1:C1 ( PF-324234:PF-45677:PF-12321 )
				else if (IDStr.indexOf(":") != indx) {
					// use String tokenizer. This logic might fail if the string contains other chars like { or ] etc
					StringTokenizer tokenizer = new StringTokenizer(IDStr, ":");
					while (tokenizer.hasMoreTokens()) {
						String token = (String) tokenizer.nextToken();
						if (map.containsKey(token)) {
							Integer times = (Integer) map.get(token);
							int count = times.intValue();
							count = count + 1;
							Integer times2 = new Integer(count);
							map.put(token, times2);

						} else {
							Integer times = new Integer(1);
							map.put(token, times);
						}
					}
				}
				// This logic when there are tow compounds seperated by : A1:B1 (PF-324234:PF-45677)
				else {
					String ID1 = IDStr.substring(0, indx);
					String ID2 = IDStr.substring(indx + 1);
					if (map.containsKey(ID1)) {
						Integer times = (Integer) map.get(ID1);
						int count = times.intValue();
						count = count + 1;
						Integer times2 = new Integer(count);
						map.put(ID1, times2);

					} else {
						Integer times = new Integer(1);
						map.put(ID1, times);
					}
					if (map.containsKey(ID2)) {
						Integer times = (Integer) map.get(ID2);
						int count = times.intValue();
						count = count + 1;
						Integer times2 = new Integer(count);
						map.put(ID2, times2);

					} else {
						Integer times = new Integer(1);
						map.put(ID2, times);
					}

				}
			}
		} catch (Exception e) {
			log.info("Failed to find monomer component count for products. Returning empty map.", e);
		}
		return map;
	}

	private HashMap<String, List<String>> getReactantsForProducts(List<String> productIDList, List<MonomerBatchModel> monomerList) {
		// HashMap will have
		// Key - productID and Value - ArraList of monomerBatchKeys
		// A:B key(ProductID) will have Value as List of A batch's Key and B batch's key
		// P':C key(ProductID) will have reactants List as P' and C
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			if (productIDList == null || productIDList.size() == 0) {
				return map;
			}

			int size = productIDList.size();
			// scan through productIDList for id strings for which we can find batch keys.
			for (int i = 0; i < size && StringUtils.isNotBlank(productIDList.get(i)); i++) {
				String IDStrOrginal = productIDList.get(i);
				String IDStr = IDStrOrginal;
				IDStr = IDStr.replaceAll("\\[", "");
				IDStr = IDStr.replaceAll("\\]", "");

				int indx = IDStr.lastIndexOf(":");
				if (indx <= 0) {
					if (map.containsKey(IDStrOrginal)) {
						List<String> cursors = map.get(IDStrOrginal);
						MonomerBatchModel model = this.getMatchingMonomerBatchWithMonomerID(IDStr, monomerList);
						if (model != null && StringUtils.isNotBlank(model.getKey())) {
							String monBatchKey = model.getKey();
							cursors.add(monBatchKey);
							map.put(IDStrOrginal, cursors);
						}

					} else {
						map.put(IDStrOrginal, new ArrayList<String>());
					}

				} else {

					// To handle A:B:C situation and A:B situation use string tokenizer

					if (map.containsKey(IDStrOrginal)) {

						StringTokenizer tokenizer = new StringTokenizer(IDStr, ":");
						List<String> cursors = map.get(IDStrOrginal);
						int pos = 0;
						while (tokenizer.hasMoreTokens()) {
							String precursorID = tokenizer.nextToken();
							MonomerBatchModel model = this.getMatchingMonomerBatchWithMonomerID(precursorID, monomerList);

							if (model != null && StringUtils.isNotBlank(model.getKey())) {
								cursors.add(pos, model.getKey());
								pos++;
							}
						}
						map.put(IDStrOrginal, cursors);

					} else {

						List<String> cursors = new ArrayList<String>();
						StringTokenizer tokenizer = new StringTokenizer(IDStr, ":");
						int pos = 0;
						while (tokenizer.hasMoreTokens()) {
							String precursorID = tokenizer.nextToken();
							MonomerBatchModel model = this.getMatchingMonomerBatchWithMonomerID(precursorID, monomerList);

							if (model != null && StringUtils.isNotBlank(model.getKey())) {
								cursors.add(pos, model.getKey());
								pos++;
							}
						}
						map.put(IDStrOrginal, cursors);
					}

				}
			}
		} catch (Exception e) {
			log.error("Failed to find reactant keys for product batches.", e);
		}
		return map;
	}

	private HashMap<String, List<String>> getGeneratedProductsForMonomers(List<String> monomerIDList, List<ProductBatchModel> productList) {
		// HashMap will have
		// Key - monomerID and Value - ArraList of productBatchKeys
		// A:B key(ProductID) will have Value as List of A batch's Key and B batch's key
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			if (monomerIDList == null || productList.size() == 0) {
				return map;
			}

			int size = monomerIDList.size();
			for (int i = 0; i < size; i++) {
				String IDStr = (String) monomerIDList.get(i);
				List<String> keys = this.getMatchingProductBatchesKeysWithMonomerIDINProductID(IDStr, productList);
				map.put(IDStr, keys);

			}
		} catch (Exception e) {
			log.error("Failed to get Product Batches for Monomer Ids", e);
		}
		return map;
	}

	// Need to find A1 model in List of all A,B
	private MonomerBatchModel getMatchingMonomerBatchWithMonomerID(String monomerID, List<MonomerBatchModel> monomerList) {
		for (MonomerBatchModel model : monomerList) {
			// if ID matches return that model
			String comparetoID = model.getMonomerId();
			comparetoID = comparetoID.replaceAll("\\[", "");
			comparetoID = comparetoID.replaceAll("\\]", "");
			if (comparetoID.compareTo(monomerID) == 0) {
				return model;
			}
		}

		return null;
	}

	// Need to find A1 occurrence in all of A:B
	private List<String> getMatchingProductBatchesKeysWithMonomerIDINProductID(String monomerID, List<ProductBatchModel> productList) {
		List<String> matchingProductKeys = new ArrayList<String>();
		for (ProductBatchModel model : productList) {
			// if key matches return that model
			if (model.getProductId().indexOf(monomerID) >= 0) {
				matchingProductKeys.add(model.getKey());
			}
		}

		return matchingProductKeys;
	}

	private HashMap<String, List<String>> getPrecurosrsForProductsMap(List<String> productIDList, List<MonomerBatchModel> monomerList) {
		// HashMap will have
		// Key - productID and Value - ArraList of monomerBatchKeys
		// A:B:C key(productID) will have Value as List of A,B,C
		// A:B key(ProductID) will have Value as List of A batch's Key and B batch's key
		// P':C key(ProductID as P) will have precursor List as A ,B and C ( From summary step)
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			if (productIDList == null || productIDList.size() == 0) {
				return map;
			}

			int size = productIDList.size();
			for (int i = 0; i < size; i++) {
				String IDStrOrginal = (String) productIDList.get(i);
				String IDStr = IDStrOrginal;
				int indx = IDStr.lastIndexOf(":");
				if (indx <= 0) {
					if (map.containsKey(IDStr)) {
						List<String> cursors = (List<String>) map.get(IDStr);
						MonomerBatchModel model = this.getMatchingMonomerBatchWithMonomerID(IDStr, monomerList);
						if (model != null && StringUtils.isNotBlank(model.getKey())) {
							String monBatchKey = model.getKey();
							cursors.add(monBatchKey);
							map.put(IDStr, cursors);
						}

					} else {
						map.put(IDStr, new ArrayList<String>());
					}

				} else {
					// Tokenize based on ":" separator

					IDStr = IDStr.replaceAll("\\[", "");
					IDStr = IDStr.replaceAll("\\]", "");
					StringTokenizer tokenizer = new StringTokenizer(IDStr, ":");
					if (map.containsKey(IDStrOrginal)) {
						List<String> cursors = map.get(IDStr);
						while (tokenizer.hasMoreTokens()) {
							String ID = tokenizer.nextToken();
							MonomerBatchModel model = this.getMatchingMonomerBatchWithMonomerID(ID, monomerList);
							if (model != null && StringUtils.isNotBlank(model.getKey())) {
								cursors.add(model.getKey());
							}
						}// has tokens
						map.put(IDStrOrginal, cursors);

					} else {
						List<String> cursors = new ArrayList<String>();
						while (tokenizer.hasMoreTokens()) {
							String ID = tokenizer.nextToken();
							MonomerBatchModel model = this.getMatchingMonomerBatchWithMonomerID(ID, monomerList);
							if (model != null && StringUtils.isNotBlank(model.getKey())) {
								cursors.add(model.getKey());
							}
						}// has tokens
						map.put(IDStrOrginal, cursors);
					}

				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return map;
	}

	private <T extends BatchModel> ArrayList<PlateWell<T>> getPlateWellsHavingBatch(String batchKey, List<PlateWell<T>> plateWells) {
		ArrayList<PlateWell<T>> list = new ArrayList<PlateWell<T>>();
		for (PlateWell<T> well : plateWells) {
			// if key matches return that model.Also check for skipped/empty well
			if (well == null || well.getBatch() == null || well.getBatch().getKey() == null) {
				log.info("PlateWell is null or has no BatchModel or the BatchModel's key is null:"
						+ (well == null ? "null well" : well.getPosition() + ""));
				continue;
			}
			if (well.getBatch().getKey().compareTo(batchKey) == 0) {
				list.add(well);
			}
		}
		return list;
	}

	private <T extends BatchModel> ArrayList<PlateWell<T>> getPlateWellsHavingBatchByBatchNum(String batchNo, List<PlateWell<T>> plateWells) {
		ArrayList<PlateWell<T>> list = new ArrayList<PlateWell<T>>();
		for (PlateWell<T> well : plateWells) {
			// if key matches return that model
			if (well.getBatch() != null && well.getBatch().getBatchNumberAsString().compareTo(batchNo) == 0) {
				list.add(well);
			}
		}
		return list;
	}

	public void assignTransactionOrderToLists() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.assignTransactionOrderToLists()");
		int stepSize = this.pageModel.getReactionSteps().size();

		// for each step do the recursion
		for (int stepNo = 0; stepNo < stepSize; stepNo++) {
			// Don't calc this for SumamryStep in a multistep reaction.
			// Interim Steps calc will reflect in summaryStep because of Linking in Deduping Logic
// Step size == 1 will mean we have only one step which is step 0.
// Am I missing something or is this code a simple wait state?
			if (stepSize > 1 && stepNo == 0) {
				continue;   
			}
			ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
			List<BatchesList<MonomerBatchModel>>  monBatchesList = stepModel.getMonomers();
			// Sort this List based on Position before assigning transaction order
			java.util.Collections.sort(monBatchesList);
			int size = monBatchesList.size();
			for (int i = 0; i < size; i++) {
				BatchesList<MonomerBatchModel> list = (BatchesList<MonomerBatchModel>) monBatchesList.get(i);
				list.setStoicTransactionOrder(i);
			}
		}
		stopwatch.stop();
	}

	// This method will be called one time after pageLoad and in cen client
	public void linkProductBatchesAnalyticalModelInAnalysisCache() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.linkProductBatchesAnalyticalModelInAnalysisCache()");
		List<ProductBatchModel> pbModelList = this.pageModel.getAllProductBatchModelsInThisPage();
		if (pbModelList != null && pbModelList.size() > 0) {
			for (ProductBatchModel pbmodel : pbModelList) {
				String batchNumber = pbmodel.getBatchNumber().getBatchNumber();
				if (StringUtils.isNotBlank(batchNumber)) {
					List<AnalysisModel> analList = this.pageModel.getAnalysisListForBatch(batchNumber);
					pbmodel.setAnalysisModelList(analList);
				}
			}
		}

		stopwatch.stop();
	}

	public void setDefaultLimitingReagentInIntermediateStep() {
		log.info("setDefaultLimitingReagentInIntermediateStep().enter");
		try {
			// Check if model has data
			if (pageModel == null)
				return;
			if (this.pageModel.getReactionSteps() != null) {
				int stepSize = this.pageModel.getReactionSteps().size();
				if (stepSize <= 0)
					return;
				// Set the Limiting flag at the summary step since the reference is same in inter steps.
				if (stepSize == 1) {
					ReactionStepModel stepModel = this.pageModel.getReactionStep(0);
					List<StoicModelInterface> monList = stepModel.getStoicElementListInTransactionOrder();
					if (monList != null && monList.size() > 0) {
						StoicModelInterface stoicM = (StoicModelInterface) monList.get(0);
						stoicM.setStoicLimiting(true);
						log.info("setDefaultLimitingReagent to List label:" + stoicM.getStoicLabel());
					}

				}// If Multi step
				else {
					// for each step do the recursion
					for (int stepNo = 1; stepNo < stepSize; stepNo++) {
						ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
						// in A+B step A is limiting, P1+C step P1 is limiting.
						List<StoicModelInterface> monList = stepModel.getStoicElementListInTransactionOrder();
						if (monList != null && monList.size() > 0) {
							StoicModelInterface stoicM = (StoicModelInterface) monList.get(0);
							stoicM.setStoicLimiting(true);
						}
					}
				}
			}
			log.info("setDefaultLimitingReagentInIntermediateStep().exit");
		} catch (Exception e) {
			log.error("Handled Error in setDefaultLimitingReagentInIntermediateStep()" + e.getMessage(), e);
		}
	}

	private ProductBatchModel getMatchingProductBatchFromProductBatchModelList(String batchKey, List<ProductBatchModel> productList) {
		ProductBatchModel batchModel = null;
		// in case of skipped well or unfilled well
		if (StringUtils.isNotBlank(batchKey)) {
			for (ProductBatchModel model : productList) {
				// if key matches return that model
				if (model.getKey().compareTo(batchKey) == 0) {
					batchModel = model;
					break;
				}
			}
		}

		return batchModel;
	}

	public void setOrRefreshGuiPseudoProductPlate() {
		PseudoProductPlate actualPseuPlate = this.pageModel.getPseudoProductPlate(false);
		PseudoProductPlate guiPseuPlate = this.pageModel.getGuiPseudoProductPlate();

		if (actualPseuPlate != null) {
			if (guiPseuPlate == null) {
				guiPseuPlate = new PseudoProductPlate(actualPseuPlate.getKey());
				guiPseuPlate.setContainer(actualPseuPlate.getContainer());
				guiPseuPlate.setPlateNumber(actualPseuPlate.getPlateNumber());
				guiPseuPlate.setRegisteredDate(actualPseuPlate.getRegisteredDate());
				guiPseuPlate.setPlateType(actualPseuPlate.getPlateType());
				guiPseuPlate.setPlateComments(actualPseuPlate.getPlateComments());
				guiPseuPlate.setPlateBarCode(actualPseuPlate.getPlateBarCode());
				guiPseuPlate.setLoadedFromDB(actualPseuPlate.isLoadedFromDB());
				guiPseuPlate.setModelChanged(actualPseuPlate.isModelChanged());
				guiPseuPlate.setCompoundManagementRegistrationSubmissionStatus(actualPseuPlate.getCompoundManagementRegistrationSubmissionStatus());
				guiPseuPlate.setBatches((ArrayList<ProductBatchModel>) this.pageModel.getNonPlatedBatches());
				PlateWell pseudoWells[] = this.pageModel.getMatchingPlateWellsFromPseudoPlate(guiPseuPlate.getBatches());
				guiPseuPlate.setWells(pseudoWells);
				this.pageModel.setGuiPseudoProductPlate(guiPseuPlate);
			} else {
				guiPseuPlate.setBatches((ArrayList<ProductBatchModel>) this.pageModel.getNonPlatedBatches());
				PlateWell pseudoWells[] = this.pageModel.getMatchingPlateWellsFromPseudoPlate(guiPseuPlate.getBatches());
				guiPseuPlate.setWells(pseudoWells);

			}
		}
	}

	// This method updates all the Amount flags at list level based on underlying batchmodels
	public void updateBatchesListAmountFlags() {
		// Check if model has data
		if (pageModel == null)
			return;
		if (this.pageModel.getReactionSteps() != null) {
			int stepSize = this.pageModel.getReactionSteps().size();
			int stepNo = 0;
			if (stepSize > 1) {
				// If we have intermediate steps sync Intermediate steps that will indirectly sync Summary step lists
				stepNo = 1;
			}
			// for each step do the recursion
			for (; stepNo < stepSize; stepNo++) {
				ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
				// sync monomer lists
				List<BatchesList<MonomerBatchModel>>  monBL = stepModel.getMonomers();
				for (BatchesList<MonomerBatchModel> list : monBL) {
					list.updateAllListLevelFlags();
				}
				// sync product lists
				List<BatchesList<ProductBatchModel>>  prodBL = stepModel.getProducts();
				for (BatchesList<ProductBatchModel> list : prodBL) {
					list.updateAllListLevelFlags();
				}
			}
		}

	}

	// This method returns ArrayList of CUSInfo objetcs for CUS service
	public ArrayList<PublishEntityInfo> getCUSInfo(NotebookPageModel pageModel) {
		log.debug("getCUSInfo()");
		Set<PublishEntityInfo> info = new HashSet<PublishEntityInfo>();
		String nbKey = "";
		if (pageModel.getNbRef() != null) {
			nbKey = pageModel.getNbRef().getCompleteNbkNumber();
			log.debug("for NBRef:" + nbKey);
		}
		// If there is no notebook ref value we cannot prepare CUSInfo
		else {
			return new ArrayList<PublishEntityInfo>();
		}
		// now loop through all the steps and prepare CUSInfo objects

		for (ReactionStepModel reacModel : pageModel.getReactionSteps()) {
//			if (reacModel == null)
//				continue;
			// Get the Reaction scheme
			ReactionSchemeModel scheme = reacModel.getRxnScheme();
			try {
                if (scheme != null) {
                	String decodedString = null;
                	String decodedStringSketch = null;
                	String decodedNativeSketch = null;
                	String stringSketch = scheme.getStringSketchAsString();
                	String nativeSketch = new String(scheme.getNativeSketch() == null ? new byte[0] : scheme.getNativeSketch());
                	
                	if(CommonUtils.isNotNull(stringSketch)) {
                		decodedStringSketch = ChimeUtils.toMoleFileFormatFromChime(stringSketch);
                		if(CommonUtils.isNotNull(decodedStringSketch)) {
                			decodedString = decodedStringSketch;
                		} else {
                			decodedString = stringSketch;
                		}
                	}
                	if(CommonUtils.isNull(decodedStringSketch) && CommonUtils.isNotNull(nativeSketch)) { 
                		decodedNativeSketch = ChimeUtils.toMoleFileFormatFromChime(new String(nativeSketch));
                		if(CommonUtils.isNotNull(decodedNativeSketch)) {
                			decodedString = decodedNativeSketch;
                		} else {
                			decodedString = nativeSketch;
                		}
                	}
                    
                    log.debug("RxN Struc Def for step:" + reacModel.getStepNumber() + "\n" + decodedString);
                    
                    if (CommonUtils.isNotNull(decodedString)) {
                        PublishEntityInfo cusObj = new PublishEntityInfo(nbKey + CeNConstants.PUBLISHER_KEY_SPERATOR + scheme.getKey(), scheme.getNativeSketch(),
                                CeNConstants.PUBLISHER_STRUCTURE_TYPE_REACTION, decodedString, scheme.getVrxId());
                        if (scheme.isLoadedFromDB() && scheme.isModelChanged()) {
                            cusObj.setInsert(false);
                            info.add(cusObj);
                        } else if (!scheme.isLoadedFromDB()) {
                            cusObj.setInsert(true);
                            info.add(cusObj);
                        }
                        
                        if(scheme.isToInsertToCus()) {
                        	cusObj.setInsert(true);
                        	info.add(cusObj);
                        }
                    }
                }
            } catch (Exception e) {
				log.error("Issue converting the reaction structure to Rxn for step:" + reacModel.getStepNumber(), e);
			}
			// No need to publish Starting Materials,Solvents,Reagents
			/*
			 * int size = reacModel.getMonomers().size(); //For each batchesList in monomers for a step for (int i = 0; i < size;
			 * i++) { BatchesList bList = (BatchesList) reacModel.getMonomers().get(i); //Get batch models in the BatchesList int
			 * batchModelSize = bList.getBatchModels().size(); //If BatchesList is Deduped no need to create CUSInfo objects
			 * if(bList.isDedupedList()) { continue; }else { for(int m= 0 ;m<batchModelSize ; m++) { MonomerBatchModel mnModel =
			 * (MonomerBatchModel)bList.getBatchModels().get(m);
			 * 
			 * CUSInfo cusObj1 = new CUSInfo(nbKey+CeNConstants.CUS_KEY_SPERATOR+mnModel.getKey(),
			 * mnModel.getCompound().getNativeSketch(),CeNConstants.CUS_STRUCTURE_TYPE_BATCH,mnModel.getCompound().getStringSketchAsString(),mnModel.getMonomerId());
			 * if(mnModel.isLoadedFromDB() && mnModel.isModelChanged()) { cusObj1.setInsert(false); list.add(cusObj1); } else
			 * if(!mnModel.isLoadedFromDB()) { cusObj1.setInsert(true); list.add(cusObj1); } } }
			 *  }
			 */
			// For each batchesList in products for a step
            for (ProductBatchModel prodModel : reacModel.getAllActualProductBatchModelsInThisStep()) {
            	try {
        			if (prodModel.isLoadedFromDB() && prodModel.isSetToDelete()) {
        				//TODO why nbKey?
            			//CUSInfo cusObj2 = new CUSInfo(nbKey + CeNConstants.CUS_KEY_SPERATOR + prodModel.getKey(), new byte[0], CeNConstants.CUS_STRUCTURE_TYPE_BATCH);
            			//CUSInfo cusObj2 = new CUSInfo(prodModel.getBatchNumberAsString() + CeNConstants.CUS_KEY_SPERATOR + prodModel.getKey(), new byte[0], CeNConstants.CUS_STRUCTURE_TYPE_BATCH);            			
        				PublishEntityInfo cusObj2 = new PublishEntityInfo(nbKey + CeNConstants.PUBLISHER_KEY_SPERATOR + prodModel.getBatchNumberAsString(), new byte[0], CeNConstants.PUBLISHER_STRUCTURE_TYPE_BATCH);
        				cusObj2.setDelete(true);
        				info.add(cusObj2);
        				continue;
        			}

            		String molStrucProduct = ChimeUtils.toMoleFileFormatFromChime(prodModel.getCompound().getStringSketchAsString());
            		// log.debug("Product ID:"+prodModel.getProductId()+"\n"+molStrucProduct);
            		if (molStrucProduct != null && molStrucProduct.length() > 0) {
            			//TODO why nbKey?
            			PublishEntityInfo cusObj2 = new PublishEntityInfo(nbKey + CeNConstants.PUBLISHER_KEY_SPERATOR + prodModel.getBatchNumberAsString(), prodModel
            			//CUSInfo cusObj2 = new CUSInfo(prodModel.getBatchNumberAsString() + CeNConstants.CUS_KEY_SPERATOR + prodModel.getKey(), prodModel
            			//CUSInfo cusObj2 = new CUSInfo(nbKey + CeNConstants.CUS_KEY_SPERATOR + prodModel.getKey(), prodModel
            					.getCompound().getNativeSketch(), CeNConstants.PUBLISHER_STRUCTURE_TYPE_BATCH, molStrucProduct, prodModel.getProductId());
            			cusObj2.setSicCode(prodModel.getCompound().getStereoisomerCode());
            			if (prodModel.isLoadedFromDB() && prodModel.isModelChanged()) {
            				cusObj2.setInsert(false);
            			} else if (!prodModel.isLoadedFromDB()) {
            				cusObj2.setInsert(true);
            			}
        				info.add(cusObj2);
            		}
            	} catch (Exception e) {
            		log.error("Issue converting the product structure to mol:" + prodModel.getProductId(), e);
            	}
            }
		}
		return new ArrayList<PublishEntityInfo>(info);
	}

	public void assignSynthesisPlanCompoundAggregationScreensToProductBatches() {
		String[] screenPanel = this.pageModel.getPageHeader().getScreenPanels();
		ArrayList<BatchSubmissionToScreenModel>  biologicalTestList = convertCompoundAggregationScreenPanelToCeNScreenModel(screenPanel);
		for (ProductBatchModel productBatch : this.pageModel.getSummaryReactionStep().getAllProductBatchModelsInThisStep()) {
			// biologicalTestList = ((ProductBatchModel)submittalBatches.get(i)).getRegInfo().getSubmitToBiologistTestList();
			productBatch.getRegInfo().setSubmitToBiologistTestList(biologicalTestList);
			productBatch.setModelChanged(true);
		}

		/*
		 * for (int i=0; i< submittalBatches.size(); i++) { ProjectTrackingData ptd = null;
		 * 
		 * if (spSelector.getSelectedPanels().length > 0){ try { // get the project code from CompoundAggregation ProjectTrackingSession pts =
		 * compoundAggregationManager.createProjectTrackingSession(); ptd = pts.getProjectTrackingData(this.projectCode); } catch
		 * (ProjectTrackingException e) { e.printStackTrace(); }
		 * 
		 * try { // create the session for submitting requests to CompoundAggregation SubmittalRequestSession srs =
		 * compoundAggregationManager.createSubmittalRequestSession();
		 *  // create a new RequestItem RequestItem batchSubmittal = srs.createBatchItem();
		 *  // fill the request item with data batchSubmittal.addScreenPanels(spSelector.getSelectedPanels()); // get the selected
		 * screen panels from the control
		 * batchSubmittal.setBatchTrackingID(((ProductBatchModel)submittalBatches.get(i)).getRegInfo().getBatchTrackingId());// this
		 * is the batch tracking ID of the compound-batch batchSubmittal.setBarCode("SOMEPLATE"); // include the tube or plate
		 * barcode for reference (not required by good practice) batchSubmittal.setProjectTrackingData(ptd); // this is the project
		 * submitting the compound which is not necessarily the same project // that was selected in the screen panel selection
		 * dialog since a user may want to submit // compounds to some other projects screen panels
		 * batchSubmittal.setCompoundManagementEmployeeID(CommonUtils.getCompoundManagementEmployeeId()); // we currently use CompoundManagement Employee ID
		 * batchSubmittal.setSiteCode("SITE1"); // we currently use CompoundManagement Employee ID RequestItem batchSubmittalArr[] = new
		 * RequestItem[]{ batchSubmittal};
		 *  // submit the request to CompoundAggregation Result result = srs.insertRequests(batchSubmittalArr); } catch (SubmittalRequestException
		 * e) { e.printStackTrace(); } } }
		 */
	}

	private ArrayList<BatchSubmissionToScreenModel> convertCompoundAggregationScreenPanelToCeNScreenModel(String[] screenPanel) {
		ArrayList<BatchSubmissionToScreenModel> biologicalTestList = new ArrayList<BatchSubmissionToScreenModel>();
		BatchSubmissionToScreenModel screenModel = null;
		for (int i = 0; i < screenPanel.length; i++) {
			screenModel = new BatchSubmissionToScreenModel();
			// screenModel.setCompoundAggregationScreenPanelKey(screenPanel[i]);
			// screenModel.setAmountUnit(unit);
			// screenModel.setAmountValue(value);
			// screenModel.setContainerType();
			// screenModel.setLoadedFromDB(isLoadedFromDB);
			// screenModel.setLoadingFromDB(isLoadingFromDB);
			// screenModel.setMinAmountUnit(unit);
			// screenModel.setMinAmountValue(value);
			// screenModel.setScientistCode(screenPanel[i].get);
			// screenModel.setScientistName(scientistName);
			// screenModel.setScreenProtocolId(code);
			// screenModel.setScreenProtocolTitle(screenPanel[i].getName());
			/*
			 * screenModel.setSiteCode(siteCode); screenModel.setSubmissionStatus(submissionStatus);
			 * screenModel.setSubmittedByMm(value);
			 */
			screenModel.setModelChanged(true);
			biologicalTestList.add(screenModel);
		}
		return biologicalTestList;
	}

	public void linkPlateWellBatchToCorrespondingBatchBeforeSAVE() {
		// Go through each step and link plates in that step to corresponding batches
		// for setting null to skipped/unfilled wells on plate to avoid FK issues on plate_well save
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.linkPlateWellBatchToCorrespondingBatch()");
		// Check if model has data
		if (this.pageModel.isParallelExperiment()) {

			if (this.pageModel.getReactionSteps() != null) {
				int stepSize = this.pageModel.getReactionSteps().size();
				// for each step do the recursion
				for (int stepNo = 0; stepNo < stepSize; stepNo++) {
					ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
					// link monomer plates
					List<MonomerPlate> monPlateList = stepModel.getMonomerPlates();
					ArrayList<BatchesList<MonomerBatchModel>> monList = stepModel.getMonomers();
					if (monPlateList != null && monList != null) {
						int platesSize = monPlateList.size();
						// each plate recursive
						for (int i = 0; i < platesSize; i++) {
							MonomerPlate mPlate = monPlateList.get(i);
							PlateWell<MonomerBatchModel> wells[] = mPlate.getWells();
							if (wells != null && wells.length > 0) {
								int wellSize = wells.length;
								for (int k = 0; k < wellSize; k++) {
									PlateWell<MonomerBatchModel> well = wells[k];
									// Handling empty wells on a plate
									if (well == null) {
										log.info("PlateWell is null");
										continue;
									}
									// sHandling skipped wells on a plate
									else if (well.getBatch() == null || well.getBatch().getKey() == null) {
										log.info("PlateWell has no MonomerBatchModel or its key is null:" + well.getPosition());
										// set with Well with empty MonomerBatchModel for client side processing.
										// Key should be null otherwise there will be FK error wile saving
										well.setBatch(null);
									} else {
										// MonomerBatchModel model = getMatchingMonomerBatch(well.getBatch().getKey(), monList);
										// well.setBatch(model);
									}

								}// for each well
							}
						}// for each plate
					}// if has monomer plates

					// link product plates
					List<ProductPlate> prodPlateList = stepModel.getProductPlates();
					ArrayList<BatchesList<ProductBatchModel>> prodList = stepModel.getProducts();
					if (prodPlateList != null && prodList != null) {
						int platesSize = prodPlateList.size();
						// each plate recursive
						for (int i = 0; i < platesSize; i++) {
							ProductPlate mPlate = prodPlateList.get(i);
							PlateWell<ProductBatchModel> wells[] = mPlate.getWells();
							if (wells != null && wells.length > 0) {
								int wellSize = wells.length;
								for (int k = 0; k < wellSize; k++) {
									PlateWell<ProductBatchModel> well = wells[k];
									// Handling empty wells on a plate
									if (well == null) {
										log.info("PlateWell at position " + k + " is null");
										continue;
									}
									// sHandling skipped wells on a plate
									else if (well.getBatch() == null || well.getBatch().getKey() == null) {
										log.info("PlateWell at position " + well.getPosition() + " has no ProductBatchModel or its key is null.");
										// set with Well with empty ProductBatchModel for client side processing.
										// Key should be null otherwise there will be FK error wile saving
										well.setBatch(null);
									} else {
										// ProductBatchModel model = getMatchingProductBatch(well.getBatch().getKey(), prodList);
										// well.setBatch(model);
									}

								}// for each well
							}
						}// for each plate
					}// if has monomer plates
				}// for each step
			}// if model has steps

			// Link the ProductBatches to Pseudoplate Wells
			PseudoProductPlate psedoPlate = pageModel.getPseudoProductPlate(false);
			if (psedoPlate != null && psedoPlate.getWells() != null) {
				PlateWell<ProductBatchModel> wells[] = psedoPlate.getWells();
				List<ProductBatchModel> allProdList = pageModel.getAllProductBatchModelsInThisPage();
				if (wells != null && wells.length > 0) {
					int wellSize = wells.length;
					for (int k = 0; k < wellSize && wells[k] != null; k++) {
						PlateWell<ProductBatchModel> well = wells[k];
						ProductBatchModel model = getMatchingProductBatchFromProductBatchModelList(well.getBatch().getKey(),
						                                                                           allProdList);
						if (model != null) {
							well.setBatch(model);
						}
					}// for each well
				}
			}

			// Link the ProductBatches to Register plate Wells
			List<ProductPlate> registeredPlatesList = pageModel.getRegisteredPlates();
			for (int i = 0; i < registeredPlatesList.size(); i++) {
				ProductPlate registeredPlate = registeredPlatesList.get(i);
				if (registeredPlate != null && registeredPlate.getWells() != null) {
					PlateWell<ProductBatchModel> wells[] = registeredPlate.getWells();
					List<ProductBatchModel> allProdList = pageModel.getAllProductBatchModelsInThisPage();
					if (wells != null && wells.length > 0) {
						int wellSize = wells.length;
						for (int k = 0; k < wellSize; k++) {
							PlateWell<ProductBatchModel> well = wells[k];
							if (well == null) {
								continue;
							}
							ProductBatchModel model = getMatchingProductBatchFromProductBatchModelList(well.getBatch().getKey(),
							                                                                           allProdList);
							well.setBatch(model);
						}// for each well
					}
				}
			}
		}
		log.info("Linking PlateWell Batch to Batch object complete");
		stopwatch.stop();
	}

	public void setReactantCompoundIDsAsPrecursors() {

		int stepSize = this.pageModel.getReactionSteps().size();
		for (int stepNo = 0; stepNo < stepSize; stepNo++) {
			// Don't calc this for SumamryStep in a multistep reaction.
			// Interim Steps calc will reflect in summaryStep because of Linking in Deduping Logic
			if (stepSize > 1 && stepNo == 0) {
				continue;
			}
			ReactionStepModel stepModel = this.pageModel.getReactionStep(stepNo);
			for (ProductBatchModel batch : stepModel.getAllProductBatchModelsInThisStep()) {
				ArrayList<String> precursors = new ArrayList<String>();
				// it should be precursors rather than getReactantBatchKeys() or getPrecursorBatchKeys(). Note both are different
				ExperimentPageUtils pageUtils = new ExperimentPageUtils();
				for (String monomerKey : batch.getReactantBatchKeys()) {
					MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
					if (monomerBatch != null) {
						ParentCompoundModel compoundModel = monomerBatch.getCompound();
						if (compoundModel != null) {
							precursors.add(compoundModel.getRegNumber());
						}
					}
				}
				batch.setPrecursors(precursors);

			}
		}
	}                                                         
}