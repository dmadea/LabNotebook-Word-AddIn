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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.utils.BatchesPlatesLookupUtil;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ReactionStepModel extends CeNAbstractModel {

	private static final long serialVersionUID = -1155646095455922138L;

	public static final Log log = LogFactory.getLog(ReactionStepModel.class);

	private int stepNumber = 0; // 0 is default and step no for summary plan
	private ReactionSchemeModel rxnScheme = null; // Represents the reaction at this step
	private String rxnProperties=""; // Free Text containing properties of the reaction

	// Procedure section data for each step in case of parallel
	//private String procedure = "";
	//private int procedureWidth = 0;
	// List of products BatchesList associated with this reaction step
	// This is ArrayList of BatchesList objects(Products BatchesList for P1, etc)
	private final ArrayList<BatchesList<ProductBatchModel>> productBatchesList = new ArrayList<BatchesList<ProductBatchModel>>();
	// This is ArrayList of BatchesList objects( Monomers BatchesList for A ,
	// Monomers BatchesList for B so on)
	private final ArrayList<BatchesList<MonomerBatchModel>> monomerBatchesLists = new ArrayList<BatchesList<MonomerBatchModel>>();
	// List of product (ProductPlate model) plates/racks
	private List<ProductPlate> productPlates = new ArrayList<ProductPlate>();
	// List of monomer (MonomerPlate model) plates/racks
	private List<MonomerPlate> monomerPlates = new ArrayList<MonomerPlate>();
	// List holds reagents and solvents Batches added from the Stoic
	private BatchesList<MonomerBatchModel> stoicBatchesList = null;
	// List holds reagents and solvents Batches which have been deleted from the Stoic but haven't been saved yet
	private BatchesList<MonomerBatchModel> stoicBatchesToRemoveList = null;
	// List holds intended product batches which have been deleted from the intended reaction products table but haven't been saved yet
	private BatchesList<ProductBatchModel> intendedProductBatchesToRemoveList = null;

//	private HashMap batchPlateMap;

	public ReactionStepModel() {
		// Create Key for this object ( eventually step_key )
		this.key = GUIDUtil.generateGUID(this);
		// initialize the scheme model as well
		rxnScheme = new ReactionSchemeModel();
		stoicBatchesList = new BatchesList<MonomerBatchModel>();
		productBatchesList.add(new BatchesList<ProductBatchModel>());
		stoicBatchesList.setPosition(CeNConstants.STOIC_POSITION_CONSTANT);
	}

	public ReactionStepModel(String key) {
		this.key = key;
		if (log.isInfoEnabled()) {
			log.info("Incomplete constructor called.  Lists are not initialized.");
		}
	}

	/**
	 * @return the monomerPlates
	 */
	public List<MonomerPlate> getMonomerPlates() {
		return monomerPlates;
	}

	/**
	 * @param monomerPlates
	 *            the monomerPlates to set
	 */
	public void setMonomerPlates(final List<MonomerPlate> monomerPlates) {
		if(monomerPlates == null) {
			this.monomerPlates = new ArrayList<MonomerPlate>();
		} else {
			this.monomerPlates = monomerPlates;
		}
		setModified(true);
	}

	/**
	 * @return the monomers
	 */
	public ArrayList<BatchesList<MonomerBatchModel>> getMonomers() {
		return monomerBatchesLists;
	}

	/**
	 * @param monomers
	 *            the monomers to set
	 */
	public void setMonomers(final ArrayList<BatchesList<MonomerBatchModel>> monomers) {
		this.monomerBatchesLists.clear();
		if(monomers != null && monomers.size() > 0) {
			for(BatchesList<MonomerBatchModel> batchesList : monomers) {
				if(monomerBatchesLists.contains(batchesList) == false) {
					this.monomerBatchesLists.add(batchesList);
				}
			}
		}
		setModified(true);
	}

	/**
	 * @return the productPlates
	 */
	public List<ProductPlate> getProductPlates() {
		return productPlates;
	}

	/**
	 * @param productPlates
	 *            the productPlates to set
	 */
	public void setProductPlates(final List<ProductPlate> productPlates) {
		if(productPlates == null) {
			this.productPlates = new ArrayList<ProductPlate>();
		} else {
			this.productPlates = productPlates;
		}
		setModified(true);
	}

	/**
	 * @return the products
	 */
	public ArrayList<BatchesList<ProductBatchModel>> getProducts() {
		return productBatchesList;
	}

	/**
	 * @param products
	 *            the products to set
	 */
	public void setProducts(final ArrayList<BatchesList<ProductBatchModel>> products) {
		this.productBatchesList.clear();
		if(products != null && products.size() > 0) {
			for(BatchesList<ProductBatchModel> batchesList : products) {
				if(productBatchesList.contains(batchesList) == false) {
					this.productBatchesList.add(batchesList);
				}
			}
		}		
		setModified(true);
	}

	/**
	 * @return the rxnProperties
	 */
	public String getRxnProperties() {
		return rxnProperties;
	}

	/**
	 * @param rxnProperties
	 *            the rxnProperties to set
	 */
	public void setRxnProperties(String rxnProperties) {
		this.rxnProperties = rxnProperties;
		setModified(true);
	}

	/**
	 * @return the rxnScheme
	 */
	public ReactionSchemeModel getRxnScheme() {
		return rxnScheme;
	}

	/**
	 * @param rxnScheme
	 *            the rxnScheme to set
	 */
	public void setRxnScheme(ReactionSchemeModel rxnScheme) {
		this.rxnScheme = rxnScheme;
		setModified(true);
	}

	/**
	 * @return the stepNumber
	 */
	public int getStepNumber() {
		return stepNumber;
	}

	/**
	 * @param stepNumber
	 *            the stepNumber to set
	 */
	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
		setModified(true);
	}

	public void addMonomerPlate(MonomerPlate plate) {
		if(this.monomerPlates.contains(plate) == false) {
			this.monomerPlates.add(plate);
			setModified(true);
		}
	}

	public void addProductPlate(ProductPlate plate) {
		if(this.productPlates.contains(plate) == false) {
			this.productPlates.add(plate);
			setModified(true);
		}
	}

	public void addMonomerPlates(List<MonomerPlate> plates) {
		for(MonomerPlate plate : plates) {
			if(this.monomerPlates.contains(plate) == false) {
				this.monomerPlates.add(plate);
				setModified(true);
			}
		}
	}

	public void addProductPlates(List<ProductPlate> plates) {
		for(ProductPlate plate : plates) {
			if(this.productPlates.contains(plate) == false) {
				this.productPlates.add(plate);
				setModified(true);
			}
		}
	}

	public void addMonomerBatch(BatchesList<MonomerBatchModel> mBatchList) {
		if(this.monomerBatchesLists.contains(mBatchList) == false) {
			this.monomerBatchesLists.add(mBatchList);
			setModified(true);
		}
	}

	public String toString() {
		return "Step " + stepNumber;
	}

	// For Parallel experiment we will have BatchesList of A , B ,C
	// monomers added to monomers list.
	public void addMonomerBatches(BatchesList<MonomerBatchModel> mBatches) {
		if(this.monomerBatchesLists.contains(mBatches) == false) {
			this.monomerBatchesLists.add(mBatches);
			setModified(true);
		}
	}

	// This method should be called only for parallel experiment
	public BatchesList<MonomerBatchModel> getMonomersList(int index) {
		return monomerBatchesLists.get(index);
	}

	public void addProductBatches(List<BatchesList<ProductBatchModel>> pBatches) {
		for(BatchesList<ProductBatchModel> batchList : pBatches) {
			if(this.productBatchesList.contains(batchList) == false) {
				setModified(true);
			}
		}
	}

	public void addMonomerBatchesList(BatchesList<MonomerBatchModel> batchesList) {
		this.monomerBatchesLists.add(batchesList);
		setModified(true);
	}

	public void addProductBatchesList(BatchesList<ProductBatchModel> batchesList) {
		// see if this is the empty batcheslist added in the constructor
		if (this.productBatchesList.size() > 0) {
			BatchesList<ProductBatchModel> list = this.productBatchesList.get(0);
			if (list.getBatchModels().size() == 0) {
				// remove the emptyBatches
				this.productBatchesList.remove(0);

			}
			// push that empty batcheslist to next slot
			this.productBatchesList.add(batchesList);
		} else {
			this.productBatchesList.add(batchesList);
		}

		setModified(true);
	}

	public Object deepClone() {
		// Step key should be the same
		ReactionStepModel stepModel = new ReactionStepModel(this.getKey());
		stepModel.deepCopy(this);
		return stepModel;
	}
	
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		String defaultType = "INTENDED";
		if (this.rxnScheme != null) {
			defaultType = this.rxnScheme.getReactionType();
		}
		xmlbuff.append("<Step_Properties Type=\"" + defaultType + "\">\n");
		xmlbuff.append("<Meta_Data>\n");
		xmlbuff.append("<Rxn_Properties>" + this.rxnProperties + "</Rxn_Properties>\n");
		xmlbuff.append("</Meta_Data>\n");
		xmlbuff.append("</Step_Properties>\n");
		return xmlbuff.toString();
	}
	
	public boolean isSummaryStep() {
		if (this.stepNumber == 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getAllProductIDs() {
		List<String> allProdIDList = new ArrayList<String>();
		for (BatchesList<ProductBatchModel> batchesList : productBatchesList) {
			List<ProductBatchModel> productModels = batchesList.getBatchModels();
			for(ProductBatchModel productBatch : productModels) {
				if(StringUtils.isNotBlank(productBatch.getProductId()) && allProdIDList.contains(productBatch.getProductId()) == false) {
					allProdIDList.add(productBatch.getProductId());
				}
			}
		}
		return allProdIDList;
	}

	public List<String> getAllMonomerIDs() {
		List<String> allMonIDList = new ArrayList<String>();
		for (BatchesList<MonomerBatchModel> batchesList : monomerBatchesLists) {
			List<MonomerBatchModel> monomerModels = batchesList.getBatchModels();
			for(MonomerBatchModel monomerBatch : monomerModels) {
				if(StringUtils.isNotBlank(monomerBatch.getMonomerId()) && allMonIDList.contains(monomerBatch.getMonomerId()) == false) {
					allMonIDList.add(monomerBatch.getMonomerId());
				}
			}
		}
		return allMonIDList;
	}

	//In case of parallel this will be List A ,List B etc. IT will not include additional solvents/reagents added to stoich
	public List<MonomerBatchModel> getAllMonomerBatchModelsInThisStep() {
		List<MonomerBatchModel> allMonList = new ArrayList<MonomerBatchModel>();
		for (BatchesList<MonomerBatchModel> bathcesList : monomerBatchesLists) {
			List<MonomerBatchModel> monomerModels = bathcesList.getBatchModels();
			if (monomerModels != null && monomerModels.size() > 0) {
				for(MonomerBatchModel batch : monomerModels) {
					if (batch != null && allMonList.contains(batch) == false) {
						allMonList.add(batch);
					}
				}
			}
		}
		return allMonList;
	}

	public List<ProductBatchModel> getAllProductBatchModelsInThisStep() {
		List<ProductBatchModel> allProdList = new ArrayList<ProductBatchModel>();
		for (BatchesList<ProductBatchModel> batchesList : productBatchesList) {
			for(ProductBatchModel batch : batchesList.getBatchModels()) {
				if (batch != null && allProdList.contains(batch) == false) {
					allProdList.add(batch);
				}
				else {
					log.error("Dumping batch: " + batch.getBatchNumberAsString() + 
					          " as not a ProductBatchModel!  It was: " + batch.getBatchType());
				}
			}
		}
		return allProdList;
	}

	public List<ProductBatchModel> getAllIntendedProductBatchModelsInThisStep() {
		List<ProductBatchModel> intProdList = new ArrayList<ProductBatchModel>();
		for (BatchesList<ProductBatchModel> batchesList : productBatchesList) {
			for(ProductBatchModel batch : batchesList.getBatchModels()) {
				if (batch.getBatchType().equals(BatchType.INTENDED_PRODUCT) && intProdList.contains(batch) == false)
				{
					intProdList.add(batch);
				}
			}
		}		
		return intProdList;
	}
	
	/**
	 * 
	 * @return List sorted by ProductBatchModel compareTo function.
	 */
	public List<ProductBatchModel> getAllActualProductBatchModelsInThisStep() {
		List<ProductBatchModel> prodList = new ArrayList<ProductBatchModel>();
		for (BatchesList<ProductBatchModel> batchesList : productBatchesList) {
			for(ProductBatchModel batch : batchesList.getBatchModels()) {
				if (batch.getBatchType().equals(BatchType.ACTUAL_PRODUCT) && prodList.contains(batch) == false)
				{
					prodList.add((ProductBatchModel) batch);
				}
			}
		}
		return prodList;
	}
	
/*	public HashMap getAllPlatesAndProductBatchModelsInThisStep(PseudoProductPlate pseudoPlate) {
		if (batchPlateMap != null)
			return batchPlateMap;
		batchPlateMap = new HashMap();
		int prodListSize = productBatchesList.size();
		ProductBatchModel productBatchModel = null;
		for (int i = 0; i < prodListSize; i++) {
			BatchesList bathcesList = (BatchesList) productBatchesList.get(i);
			List productModels = bathcesList.getBatchModels();
			if (productModels != null && productModels.size() > 0) {
				{
					for (int k=0; k< productModels.size(); k++)
					{
						productBatchModel = (ProductBatchModel)productModels.get(k);
						ProductPlate productPlate = getPlate(productBatchModel);
						if (productPlate != null)
							batchPlateMap.put(productBatchModel, productPlate);
						else
							batchPlateMap.put(productBatchModel, pseudoPlate);
					}
				}
				
			}
		}
		return batchPlateMap;
	}*/
	
//	private ProductPlate getPlate(ProductBatchModel productBatchModel) {
//		List<ProductPlate> productPlatesList  = this.getProductPlates();
//		ProductPlate productPlate  = null;
//		for (int i =0; i<productPlatesList.size(); i++)
//		{
//			productPlate = (ProductPlate) productPlatesList.get(i);
//			ProductBatchModel[] productBatchModels = productPlate.getAllBatchesInThePlate();
//			for (int j = 0; j <productBatchModels.length; j++)
//			{
//				if (productBatchModels[j] == productBatchModel)
//					return productPlate; 
//			}
//		}
//		return null;
//	}

	public MonomerBatchModel[] getReactantsForAProduct(ProductBatchModel productBatchModel) {
		if (productBatchModel == null)
			return null;
		List<String> batchKeyList = productBatchModel.getReactantBatchKeys();
		int size = batchKeyList.size();
		MonomerBatchModel[] monomers = new MonomerBatchModel[size];
		List<MonomerBatchModel> allMonomersInThisStep = this.getAllMonomerBatchModelsInThisStep();
		for (int i = 0; i < size; i++) {
			String monomerKey = (String) batchKeyList.get(i);
			monomers[i] = BatchesPlatesLookupUtil.getMatchingMonomerBatch(monomerKey, allMonomersInThisStep);
		}
		return monomers;
	}

	public List<PlateWell<MonomerBatchModel>> getAllMonomerPlateWellsInThisStep() {
		int monPltListSize = monomerPlates.size();
		List<PlateWell<MonomerBatchModel>> allPLtWellList = new ArrayList<PlateWell<MonomerBatchModel>>();
		for (int i = 0; i < monPltListSize; i++) {
			MonomerPlate plate = monomerPlates.get(i);
			PlateWell<MonomerBatchModel> wells[] = plate.getWells();
			if (wells != null && wells.length > 0) {
				int wellSize = wells.length;
				for (int k = 0; k < wellSize; k++) {
					allPLtWellList.add(wells[k]);
				}
			}
		}
		return allPLtWellList;
	}

	public List<PlateWell<ProductBatchModel>> getAllProductPlateWellsInThisStep() {
		int prodPltListSize = productPlates.size();
		List<PlateWell<ProductBatchModel>> allPLtWellList = new ArrayList<PlateWell<ProductBatchModel>>();
		for (int i = 0; i < prodPltListSize; i++) {
			ProductPlate plate = productPlates.get(i);
			PlateWell<ProductBatchModel> wells[] = plate.getWells();
			if (wells != null && wells.length > 0) {
				int wellSize = wells.length;
				for (int k = 0; k < wellSize; k++) {
					allPLtWellList.add(wells[k]);
				}
			}
		}
		return allPLtWellList;
	}

	/*
	 * adds MonomerBatchModel to the list
	 */
	public void addMonomerBatchForStoic(MonomerBatchModel batchObject) {
		if (batchObject != null) {
			this.stoicBatchesList.addBatch(batchObject);
			setModified(true);
		}

	}

	public void setStoicBatchesList(BatchesList<MonomerBatchModel> listofBatches) {
		if (listofBatches != null) {
			this.stoicBatchesList = listofBatches;
			setModified(true);
		}

	}

	/*
	 * @return List of MonomerBatchModels (reagents and solvents)
	 */
	public BatchesList<MonomerBatchModel> getStoicBatchesList() {
		if (stoicBatchesList == null) {
			stoicBatchesList = new BatchesList<MonomerBatchModel>();
			stoicBatchesList.setPosition("STE");
		}
		return this.stoicBatchesList;
	}

	public BatchesList<MonomerBatchModel> getStoicBatchesToRemoveList() {
		if (stoicBatchesToRemoveList == null) {
			stoicBatchesToRemoveList = new BatchesList<MonomerBatchModel>();
		}
		return this.stoicBatchesToRemoveList;
	}
	
	public BatchesList<ProductBatchModel> getIntendedProductBatchesToRemoveList() {
		if (intendedProductBatchesToRemoveList == null) {
			intendedProductBatchesToRemoveList = new BatchesList<ProductBatchModel>();
			// to be able to delete removed batches from db
			intendedProductBatchesToRemoveList.setLoadedFromDB(true);
		}
		return this.intendedProductBatchesToRemoveList;
	}

	public void clearDeletedBatches() {
		if (stoicBatchesToRemoveList != null) {
			List<MonomerBatchModel> monomerBatchesToRemove = this.stoicBatchesToRemoveList.getBatchModels();
			monomerBatchesToRemove.clear();
		}
		
		// clear deleted product batches 
		if (intendedProductBatchesToRemoveList != null) {
			List<ProductBatchModel> productBatchesToRemove = this.intendedProductBatchesToRemoveList.getBatchModels();
			productBatchesToRemove.clear();
		}
	}
	
	/**
	 * @return List containing Reagents and Solvents from StoicBatchesList
	 */
	public List<MonomerBatchModel> getBatchesFromStoicBatchesList() {
		return this.getStoicBatchesList().getBatchModels();
	}

	/*
	 * @return ArrayList of StoicModelInterface Objects( Monomers and Reagents,Solvents etc )
	 */
	public ArrayList<StoicModelInterface> getStoicModelList() {
		ArrayList<StoicModelInterface> stoicList = new ArrayList<StoicModelInterface>();
		// adding MonomerList Objects
		stoicList.addAll(this.monomerBatchesLists);
		// adding MonomerBatchModel objects
		//Filter solvent batches associated with a Reagent/Reactant directly
		List<MonomerBatchModel> allStoicElementBatches = this.getBatchesFromStoicBatchesList();
		int size = allStoicElementBatches.size();
		if (size > 0) {
			List<StoicModelInterface> newList = new ArrayList<StoicModelInterface>();
			for (int i = 0; i < size; i++) {
				StoicModelInterface stoicModel = (StoicModelInterface) allStoicElementBatches.get(i);
				if (stoicModel.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
					String key = stoicModel.getGUIDKey();
					if (!isSolventForASpecificReacant(key)) {
						newList.add(stoicModel);
					}

				} else {
					newList.add(stoicModel);
				}
			}
			stoicList.addAll(newList);
		}
		return stoicList;
	}

	public List<StoicModelInterface> getStoicElementListInTransactionOrder() {
		List<StoicModelInterface> list = this.getStoicModelList();
		ExperimentPageUtils.sortStoicElementsInListOnTransactionOrder(list);
		return list;
	}

	/*
	 * Only add possible is for reagents and solvents batches Method call addStoicMonomerBatch to update the list
	 */
	public void addStoicModelInterface(StoicModelInterface stoicModel) {
		if (stoicModel != null) {
			// TO DO Handling for Singleton.
			if (stoicModel instanceof MonomerBatchModel) {
				this.addMonomerBatchForStoic((MonomerBatchModel) stoicModel);
			} else if (stoicModel instanceof BatchesList<?>) {
				this.monomerBatchesLists.add((BatchesList<MonomerBatchModel>) stoicModel);
			}
		}
	}

	/*
	 * adds MonomerBatchModel List to the list
	 */
	public void addMonomerBatchListForStoic(List<MonomerBatchModel> batchObjects) {
		if (batchObjects != null) {
			int curentmaxTran = getCurrentMaxTransactionOrder();
			for (int i = 0; i < batchObjects.size(); i++) {
				MonomerBatchModel model = batchObjects.get(i);
				int curTran = model.getTransactionOrder();
				if (curTran == -1 || (curTran == 0 && curentmaxTran != 0)) {
					model.setTransactionOrder(curentmaxTran + (i + 1));
				}
				this.stoicBatchesList.addBatch(model);
			}
			setModified(true);
		}

	}

	public void deleteMonomerPlateFromList(MonomerPlate monomerPlate) {
		this.monomerPlates.remove(monomerPlate);

	}

	public void deleteProductPlateFromList(ProductPlate productPlate) {
		this.productPlates.remove(productPlate);
	}

	public void swapBatchPositionOrder(StoicModelInterface source, StoicModelInterface target) {
		int tmpPositionHolder = source.getStoicTransactionOrder();
		source.setStoicTransactionOrder(target.getStoicTransactionOrder());
		target.setStoicTransactionOrder(tmpPositionHolder);
	}

	public int getCurrentMaxTransactionOrder() {
		int max = 0;
		List<BatchesList<MonomerBatchModel>> monBList = this.getMonomers();
		// find max among BachesList of monomers
		for (StoicModelInterface monBlistI : monBList) {
			int tran = monBlistI.getStoicTransactionOrder();
			if (tran > max) {
				max = tran;
			}
		}
		// find max among MonomerBatchModel of other StoicElements
		List<MonomerBatchModel> models = this.getStoicBatchesList().getBatchModels();
		for (MonomerBatchModel model : models) {
			int tran = model.getTransactionOrder();
			if (tran > max) {
				max = tran;
			}
		}

		return max;
	}

	public void deleteStoicElement(StoicModelInterface stoicModel) {
		if (stoicModel != null) {
			// Fixed for Handling Singleton.
			if (stoicModel instanceof MonomerBatchModel) {
				((MonomerBatchModel) stoicModel).markToBeDeleted(true);
				BatchesList<MonomerBatchModel> stoichRemovalList = getStoicBatchesToRemoveList();
				stoichRemovalList.addBatch((MonomerBatchModel) stoicModel);
				BatchesList<MonomerBatchModel> stoichBatchesList = getStoicBatchesList();
				stoichBatchesList.removeBatch((MonomerBatchModel) stoicModel);
			} else if (stoicModel instanceof BatchesList<?>) {
				if (monomerBatchesLists != null) {
					this.monomerBatchesLists.remove((BatchesList<MonomerBatchModel>) stoicModel);
				}
			}
			
			//update the transaction order.
			List<StoicModelInterface> stoicList = this.getStoicElementListInTransactionOrder();
			updateAdditionOrder(stoicList);
		}
	}

	//This method needs to be modified as the logic of getting the Non-Plated batches is moved to Notebook Level.
	
/*	public PseudoProductPlate getNonPlatedProductBatchesInThisStep() {
		PseudoProductPlate pseudoPlate = null;
		ArrayList nonPLTPB = new ArrayList();
		ArrayList pbModels = (ArrayList) this.getAllProductBatchModelsInThisStep();
		List pbWells = this.getAllProductPlateWellsInThisStep();
		if (pbWells == null || pbWells.size() == 0 && (pbModels != null && pbModels.size() > 0)) {
			pseudoPlate = new PseudoProductPlate(pbModels);
		} else if (pbModels == null || pbModels.size() == 0) {
			pseudoPlate = new PseudoProductPlate(nonPLTPB);
		} else {
			nonPLTPB.addAll(pbModels);
			for (Iterator pbit = pbModels.iterator(); pbit.hasNext();) {
				ProductBatchModel model = (ProductBatchModel) pbit.next();
				for (Iterator pwit = pbWells.iterator(); pwit.hasNext();) {
					PlateWell well = (PlateWell) pwit.next();
					// If the model matches with the batch in the Platewell remove from list
					if (well.getBatch().getKey().equals(model.getKey())) {
						nonPLTPB.remove(model);
					}

				}
			}
			
			pseudoPlate = new PseudoProductPlate(nonPLTPB);
		}// else

		return pseudoPlate;
	}*/

	/**
	 * 
	 * @return results from calling getBatchesFromStoicBatchesList
	 */
	public List<MonomerBatchModel> getReagentBatches() {
		return getBatchesFromStoicBatchesList();
	}

	/**
	 * 
	 * @return null if no batches are returned from getReagentBatches or a MonomerBatchModel that is first encountered with isLimiting() == true
	 */
	public MonomerBatchModel getLimitingReagent() {
		MonomerBatchModel limitingReagent = null;
		for(Iterator<MonomerBatchModel> monomerItr = getReagentBatches().iterator(); limitingReagent == null && monomerItr.hasNext();) {
			MonomerBatchModel testBatch = monomerItr.next();
			if(testBatch.isLimiting()) {
				limitingReagent = testBatch;
			}
		}
		return limitingReagent;
	}
	/**
	 * 
	 * @return empty list or list with MonomerBatchModels that have a batch type that contains the value of BatchType.REACTANT_VALUES
	 */
	public List<MonomerBatchModel> getReactantBatches() {
		// filter returned list for reactant batches
		List<MonomerBatchModel> result = new ArrayList<MonomerBatchModel>();
		for(MonomerBatchModel batch : getBatchesFromStoicBatchesList()) {
			if(BatchType.REACTANT_VALUES.contains(batch.getBatchType()) && result.contains(batch) == false) {
				result.add(batch);
			}
		}
		return result;
	}

	//This is a redundant method to getAllProductBatchModelsInThisStep()
	public List<ProductBatchModel> getProductBatches() {
		List<ProductBatchModel> list = new ArrayList<ProductBatchModel>();
		if (this.productBatchesList != null && productBatchesList.size() > 0) {
			for (BatchesList<ProductBatchModel> bl : productBatchesList) {
				for(ProductBatchModel batch : bl.getBatchModels()) {
					list.add(batch);
				}
			}
		}
		return list;
	}
	
	// vb 3/13 To sort the product batches
	public List<BatchesList<ProductBatchModel>> getProductBatchLists() {
		return this.productBatchesList;
	}

	public void addProductBatch(ProductBatchModel pb) {
		pb.setIntendedBatchAdditionOrder(this.getAllIntendedProductBatchModelsInThisStep().size());
		BatchesList<ProductBatchModel> pbList = new BatchesList<ProductBatchModel>();
		pbList.addBatch(pb);
		productBatchesList.add(pb.getIntendedBatchAdditionOrder(), pbList);
		setModified(true);
	}

	public void removeProductBatch(ProductBatchModel pb) {
		if (productBatchesList != null) {
			for (Iterator<BatchesList<ProductBatchModel>> bIt = productBatchesList.iterator(); bIt.hasNext(); ) {
				BatchesList<ProductBatchModel> batchesList = bIt.next();
				if (batchesList != null && batchesList.getBatchModels() != null) {
					List<ProductBatchModel> pbModels = batchesList.getBatchModels();
					if (pbModels != null) {
						if (pbModels.size() == 1) {
							if (pbModels.contains(pb)) {
								if (pb.isLoadedFromDB()) {
									pb.setToDelete(true);
									this.getIntendedProductBatchesToRemoveList().addBatch(pb);
								} 

								batchesList.removeBatch(pb);
								bIt.remove();									
								reindexProductsList();
								setModified(true);
							}
						} else {
							for (Iterator<ProductBatchModel> pIt = pbModels.iterator(); pIt.hasNext(); ) {
								ProductBatchModel batch = pIt.next();  // only way to modify a list while iterating through it.
								if (batch != null && StringUtils.equals(batch.getKey(), pb.getKey())) {
									pIt.remove();
								}
							}
						}
					}
				}
			}
		}
	}

	public void removeProductBatch(int leftToRightOrdinalPosition) {
		
	}
	
	private void reindexProductsList() {
		if (this.productBatchesList != null && productBatchesList.size() > 0) {
			int addOrder = 0;
			for (BatchesList<ProductBatchModel> batchesList : this.productBatchesList) {
				if(batchesList.getBatchModels().size() == 1)
				{
					ProductBatchModel batch = batchesList.getBatchModels().get(0);
					if (batch.getBatchType().equals(BatchType.INTENDED_PRODUCT)) {
						batch.setIntendedBatchAdditionOrder(addOrder++);					 
					}
				}
			}
		}
	}
		
		
	public BatchesList<ProductBatchModel> getUserAddedProductBatchesList() {
		BatchesList<ProductBatchModel> batchesList = new BatchesList<ProductBatchModel>();
		for (BatchesList<ProductBatchModel> tempBatchesList : productBatchesList) {  
			if (tempBatchesList.getPosition().equals(CeNConstants.PRODUCTS_USER_ADDED))// User Added Batches. PUA
				return tempBatchesList;
		}
		batchesList.setPosition(CeNConstants.PRODUCTS_USER_ADDED);
		return batchesList;
	}
	
	public void deleteBatches(BatchType type)
	{
		log.error("No op. ReactionStepModel.deleteBatches was not implemented!");
	}
	
	/**
	 * 
	 * @return a list with either Batches<MonomerBatchModel> or MonomerBatchModel objects
	 */
	public ArrayList<StoicModelInterface> getAllStoicModelList() {
		ArrayList<StoicModelInterface>  stoicList = new ArrayList<StoicModelInterface> ();
		// adding MonomerList Objects
		stoicList.addAll(this.monomerBatchesLists);
		// adding MonomerBatchModel objects
		stoicList.addAll(this.getBatchesFromStoicBatchesList());
	
		return stoicList;
	}
	
//	This method returns if the key is a SolventBatch that was associated with Reactant/Reagent
	private boolean isSolventForASpecificReacant(String solventBatchKey)
	{
		List<StoicModelInterface> list = this.getAllStoicModelList();
		for (int i = 0; i < list.size(); i++) {
			StoicModelInterface stoicM = (StoicModelInterface) list.get(i);
			if (stoicM.getStoicSolventsAdded() == null)
				continue;
			if (stoicM.getStoicSolventsAdded().equals(solventBatchKey)) {
				return true;
			}
		}

		return false;
	}
	
//	This method returns the Solvent StoicModelInterfaces for the matching key list.
	public StoicModelInterface getSolventBatch(String solventBatchKey)
	{
		StoicModelInterface stoicM = null;
		List<StoicModelInterface> list = this.getAllStoicModelList();
		for (int i = 0; i < list.size(); i++) {
			stoicM = (StoicModelInterface) list.get(i);
			if (stoicM.getGUIDKey().equals(solventBatchKey)) {
				return stoicM;
			}
		}

		return stoicM;
	}
	
	private void updateAdditionOrder(List<StoicModelInterface> batches)
	{		
		for(int i = 0 ; i < batches.size() ; ++i) {
			StoicModelInterface model = batches.get(i);
			if (model != null) {
				model.setStoicTransactionOrder(i);
			}
		}
	}
	

//	This method returns the MonomerBatch's StoicModelInterfaces for the matching key list.
	public StoicModelInterface getStoicMonomerBatch(String monBatchKey)
	{
		StoicModelInterface stoicM = null;
		List<StoicModelInterface> list = this.getAllStoicModelList();
		for (int i = 0; i < list.size(); i++) {
			stoicM = (StoicModelInterface) list.get(i);
			if (stoicM.getGUIDKey().equals(monBatchKey)) {
				return stoicM;
			}
		}

		return stoicM;

	}

	public void deepCopy(ReactionStepModel src) {
		setStepNumber(src.stepNumber);
		// stepModel.setRxnScheme((ReactionSchemeModel)this.rxnScheme.deepClone());
		setModelChanged(src.modelChanged);
		setRxnProperties(src.rxnProperties);
		setLoadedFromDB(src.isLoadedFromDB());
	}
	
	public MonomerBatchModel getMatchingMonomerBatch(String batchKey)
	{
		MonomerBatchModel result = null;
		List<MonomerBatchModel> monomersList = this.getBatchesFromStoicBatchesList();
		// ensure we don't look through the list at the same object more than once.
		for(MonomerBatchModel batch : this.getAllMonomerBatchModelsInThisStep()) {
			if(monomersList.contains(batch) == false) {
				monomersList.add(batch);
			}
		}
		if(monomersList == null ) 
			return null;
		for(MonomerBatchModel model : monomersList) {
			if(model.getKey().compareToIgnoreCase(batchKey) == 0)
			{
			  result = model;
			  break;
			}
		}
		
		return result;
	}
	
	public ProductBatchModel getMatchingProductBatch(String batchKey)
	{
		List<ProductBatchModel> productsList = this.getAllProductBatchModelsInThisStep();
	
		if(productsList == null ) 
			return null;
		for(ProductBatchModel model : productsList) {
			if(model.getKey().compareToIgnoreCase(batchKey) == 0)
			{
			  return model;
			}
		}
		
		return null;
	}
	
	public void updateIntendedProductsWithChloracnegenInfo(HashMap<String, String> testedRxnProductsMap)
    {
        if(testedRxnProductsMap != null)
            try
            {
                Set<String> keySet = testedRxnProductsMap.keySet();
                //List currentPBList = this.getProductBatches();
                Iterator<String> iter = keySet.iterator();
                do
                {
                    if(!iter.hasNext())
                        break;
                    //String nativeStruc = (String)iter.next();
                    //String result = (String)testedRxnProductsMap.get(nativeStruc);
                    //ProductBatchModel pb = isStructureInBatchList(nativeStruc.getBytes(), currentPBList);
                    String pbKey = (String)iter.next();
                    String result = (String)testedRxnProductsMap.get(pbKey);
                    ProductBatchModel pb = this.getMatchingProductBatch(pbKey);
					if (pb != null) {
						log.debug("updateIntendedProductsWithChloracnegenInfo()." +
									result);
						if (result != null &&
							result.indexOf("Chlor") > 0) {
							pb.setChloracnegenFlag(true);
							pb.setTestedForChloracnegen(true);
							String type = result.substring(0, result.indexOf("Chloracnegen"));
							pb.setChloracnegenType(type.trim());
						}
                   }
                } while(true);
            }
            catch(Exception e) {
            	log.error("Failed to update product with chloracnegen information.", e);
            }
    }
	
	
	public void updateStoicBatchesWithChloracnegenInfo(HashMap<String, String> testedStoicBatchesMap)
    {
        if(testedStoicBatchesMap != null)
            try
            {
                Set<String> keySet = testedStoicBatchesMap.keySet();
                Iterator<String> iter = keySet.iterator();
                do
                {
                    if(!iter.hasNext())
                        break;
                    String batchKey = (String)iter.next();
                    String result = (String)testedStoicBatchesMap.get(batchKey);
                    MonomerBatchModel pb = this.getMatchingMonomerBatch(batchKey);
                    if(pb != null)
                    {
                        
                        if(result != null && result.indexOf("Chlor") >= 0)
                        {
                            pb.setChloracnegenFlag(true);
                            pb.setTestedForChloracnegen(true);
                            //add only the type. Like 'Class 1'
                            String type = result.substring(0,result.indexOf("Chloracnegen"));
                            pb.setChloracnegenType(type.trim());
                        } else
                        if(result != null && result.indexOf("NO_HITS") >= 0)
                        {
                            pb.setChloracnegenFlag(false);
                            pb.setTestedForChloracnegen(true);
                            pb.setChloracnegenType(result);
                        } else
                        if(result != null && result.indexOf("JOB_FAILED") >= 0)
                        {
                            pb.setTestedForChloracnegen(false);
                        }
                        log.debug("updateStoicBatchesWithChloracnegenInfo()." + result);
                    }
                } while(true);
            }
            catch(Exception e)
            {
                log.error("Failed to update batches with Chloracnegen Information.", e);
            }
    }
	
//	public ProductBatchModel isStructureInBatchList(byte testStructure[], List<ProductBatchModel> batches)	throws ChemUtilInitException,
//																						ChemUtilAccessException {
//		ProductBatchModel result = null;
//		Iterator<ProductBatchModel> i = batches.iterator();
//		do {
//			if (!i.hasNext() ||
//				result != null)
//				break;
//			ProductBatchModel tstBatch = (ProductBatchModel) i.next();
//			if ((new ChemistryDelegate()).areMoleculesEqual(testStructure, tstBatch.getCompound().getNativeSketch()))
//				result = tstBatch;
//		} while (true);
//		return result;
//	}
}