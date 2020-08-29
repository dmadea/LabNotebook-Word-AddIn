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

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatchesList<E extends BatchModel> extends CeNAbstractModel implements Comparable<BatchesList<E>>, StoicModelInterface {

	private static final long serialVersionUID = -229096776631434914L;
	
	// ArrayList has ProductBatch or MonomerBatch models in the list
	private List<E> batchModels = new ArrayList<E>();
	// Position this list of batchMode
	// A,B,C for monomers and P1,P2 for final products,intermediate products
	private String position = "";

	// Flag to describe if the list already exists from summary or previous step
	// isDedupedList = true means do not persist this batchesList again
	// also list key matches to the batchesList key that is previously persisted
	private boolean isDedupedList = false;

	// Required for StoicModel
	private String rxnRole = CeNConstants.BATCH_TYPE_REACTANT;

	// Required for StoicModel
	private String stoicLabel = null;

	// These attributes are used for List type ( Models > 1) in Stoich
	private final AmountModel listMolecularWeightAmount = new AmountModel(UnitType.SCALAR); // Holds batch molecular weight
	private final AmountModel listMoleAmount = new AmountModel(UnitType.MOLES); // Unitless amount indicating how much of an Avagadro's
	private final AmountModel listWeightAmount = new AmountModel(UnitType.MASS); // AmountModel will contain unit conversions original amount
	private final AmountModel listLoadingAmount = new AmountModel(UnitType.LOADING); // Loading is generally mmol/gram - tackles resins
	private final AmountModel listVolumeAmount = new AmountModel(UnitType.VOLUME); // AmountModel in volume
	private final AmountModel listDensityAmount = new AmountModel(UnitType.DENSITY); // Density of compound in g/mL
	private final AmountModel listMolarAmount = new AmountModel(UnitType.MOLAR); // Describes concentration of batch
	private final AmountModel listPurityAmount = new AmountModel(UnitType.SCALAR, 100); // % Purity info 100 - 0
	private final AmountModel listRxnEquivsAmount = new AmountModel(UnitType.SCALAR, 0, 0); // 
	private final SaltFormModel listSaltForm = new SaltFormModel();
	private final AmountModel listPreviousMolarAmount = new AmountModel(UnitType.MOLAR);
	//these are the flags to handle if all the BatchModels in the list has same value or  user has entered a specific value to any single BatchModel
	private boolean isAllBatchModelsMoleAmountIsSame = true;
	private boolean isAllBatchModelsWeightAmountIsSame = true;
	private boolean isAllBatchModelsLoadingAmountISSame = true;
	private boolean isAllBatchModelsVolumeAmountIsSame = true;
	private boolean isAllBatchModelsDensityAmountIsSame = true;
	private boolean isAllBatchModelsMolarAmountIsSame = true;
	private boolean isAllBatchModelsPurityAmountIsSame = true;
	private boolean isAllBatchModelsRxnEquivsIsSame = true;
	private boolean isAllBatchPreviousMolarAmountIsSame = true;
	private boolean isAllBatchModelsLimitingReagentIsSame = true;
	private boolean isAllBatchModelsStoichCommentAreSame = true;
	private boolean isAllBatchModelsStoicLabelAreSame = true;

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		return xmlbuff.toString();
	}

	public void addBatch(E batch) {
		if(batchModels.contains(batch) == false)
			batchModels.add(batch);
		setModelChanged(true);
	}

	public void addAllBatches(List<E> batchesList) {
		for(E batch : batchesList) {
			if(batchModels.contains(batch) == false) {
				batchModels.add(batch);
				setModelChanged(true);
			}
		}
	}

	public List<E> getBatchModels() {
		return batchModels;
	}

	public void setBatchModels(List<E> batchModels) {
		if(batchModels == null) {
			this.batchModels = new ArrayList<E>();
		} else {
			this.batchModels = batchModels;
		}
		this.modelChanged = true;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		if (!this.position.equals(position)) {
			this.position = position;
			this.modelChanged = true;
		}
	}

	public BatchesList() {
		// Create Key for this object ( eventually List_Key )
		this.key = GUIDUtil.generateGUID(this);
	}

	public BatchesList(String key) {
		this.key = key;
	}

	public BatchesList(String position, List<E> batchModels, String rxnRole) {
		this();
		this.position = position;
		this.batchModels = batchModels;
		this.rxnRole = rxnRole;
	}

	public BatchesList(String key, String position, List<E> batchModels) {
		this(key);
		this.position = position;
		this.batchModels = batchModels;
	}

	public BatchesList(String key, String position, List<E> batchModels, boolean isDeduped) {
		this(key);
		this.position = position;
		this.batchModels = batchModels;
		this.isDedupedList = isDeduped;
	}

	public String getListKey() {
		return this.key;
	}

	public boolean isDedupedList() {
		return isDedupedList;
	}

	public void setDedupedList(boolean isDedupedList) {
		if (this.isDedupedList != isDedupedList) {
			this.isDedupedList = isDedupedList;
			this.modelChanged = true;
		}
	}

	// This will give array list of monomer ids for all monomerBatches if this
	// list is monomer batches
	public ArrayList<String> getMonomerIDList() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < this.batchModels.size() && batchModels.get(i) instanceof MonomerBatchModel; i++) {
			list.add(((MonomerBatchModel) batchModels.get(i)).getMonomerId());
		}
		return list;
	}

	// This will give array list of prod ids for all productBatches if this
	// list is product batches
	public ArrayList<String> getProductIDList() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < this.batchModels.size() && batchModels.get(i) instanceof ProductBatchModel; i++) {
			list.add(((ProductBatchModel) batchModels.get(i)).getProductId());
		}
		return list;
	}

	public int compareTo(BatchesList<E> anotherBatchesList) throws ClassCastException {
//		if (!(anotherBatchesList instanceof BatchesList))
//			throw new ClassCastException("A BatchesList object expected.");
		int thisPos = 0;
		int otherPos = 0;
		try {
			String anotherListPos = anotherBatchesList.getPosition();
			char cother = anotherListPos.charAt(0);
			otherPos = Character.getNumericValue(cother);
			char cthis = this.position.charAt(0);
			thisPos = Character.getNumericValue(cthis);

		} catch (Exception e) {
			// Any exception just ignore and return 0
		}
		// Compares this object with the specified object for order.
		// Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
		// specified object.
		return thisPos - otherPos;
	}

	public E getMatchingBatch(String id) {
		for (E model : batchModels) {
			if (BatchType.REACTANT_VALUES.contains(model.getBatchType()) && model.getBatchId().compareToIgnoreCase(id) == 0) {
				return model;
			}
		}
		return null;
	}

//	public ProductBatchModel getMatchingProduct(String productID) {
//		int size = this.batchModels.size();
//		for (int i = 0; i < size && batchModels.get(i) instanceof ProductBatchModel; i++) {
//			ProductBatchModel model = (ProductBatchModel) batchModels.get(i);
//			if (model.getProductId().compareToIgnoreCase(productID) == 0) {
//				return model;
//			}
//		}
//
//		return null;
//	}

	/*
	 * As Mole Amount is same for the all the Batches in the list, Returns Molar amount for the first Batch in the list.
	 * 
	 * This is a StoicModelInterface method
	 */
	public AmountModel getStoicMoleAmount() {
		if (getListSize() == 1 || (this.isAllBatchModelsMoleAmountIsSame)) {
			BatchModel batchModel = (BatchModel) batchModels.get(0);
			return batchModel.getStoicMoleAmount();
		} else {
			return listMoleAmount;
		}
	}

	/*
	 * Returns ReactionRole
	 * 
	 * This is a StoicModelInterface method
	 */
	public String getStoicReactionRole() {
		return this.rxnRole;
	}

	/*
	 * As Molar Amount is same for the all the Batches in the list, method returns Molar amount for the first
	 * 
	 * This is a StoicModelInterface method
	 */
	public AmountModel getStoicRxnEquivsAmount() {
		if(getListSize() == 1 || (this.isAllBatchModelsRxnEquivsIsSame))
 		{
			BatchModel batchModel = (BatchModel) batchModels.get(0);
			return batchModel.getRxnEquivsAmount();
 		}else
 		{
 			this.listRxnEquivsAmount.setValue("0");
 			this.listRxnEquivsAmount.setDefaultValue("0");
 			return listRxnEquivsAmount ;	
		}
		
	}

	/*
	 * As solute is same for the all the Batches in the list, method returns Solute for the first Batch in the laits
	 * 
	 * This is a StoicModelInterface method
	 */
	public String getStoicSolute() {
		BatchModel batchModel = (BatchModel) batchModels.get(0);
		return batchModel.getStoicSolute();
	}

	/*
	 * As Solute Amount is same for the all the Batches in the list, method returns Solute amount for the first
	 * 
	 * This is a StoicModelInterface method
	 */
	public AmountModel getStoicSoluteAmount() {
		BatchModel batchModel = (BatchModel) batchModels.get(0);
		return batchModel.getStoicSoluteAmount();
	}

	/*
	 * As Limiting property is same for the all the Batches in the list, method returns Limiting property for the first
	 * 
	 * This is a StoicModelInterface method
	 */
	public boolean isStoicLimiting() {
		BatchModel batchModel = (BatchModel) batchModels.get(0);
		return batchModel.isLimiting();
	}

	/*
	 * 
	 * This is a StoicModelInterface method
	 */
	public boolean isList() {

		if (getListSize() > 1) {
			return true;
		} else
			return false;
	}

	/*
	 * Sets limiting property to all the Batches in the list
	 * 
	 * This is a StoicModelInterface method
	 */
	public void setStoicLimiting(boolean limiting) {
		if (this.isStoicLimiting() != limiting) {
			for (int i = 0; i < batchModels.size(); i++) {
				BatchModel batchModel = (BatchModel) batchModels.get(i);
				batchModel.setLimiting(limiting);
			}
			this.modelChanged = true;
		}
	}

	
	/*
	 * Sets Mole amount to all the Batches in the list
	 * 
	 * This is a StoicModelInterface method
	 */
	public void setStoicMoleAmount(AmountModel molAmt) {
		if (!this.getStoicMoleAmount().equals(molAmt)) {
			for (int i = 0; i < batchModels.size(); i++) {
				BatchModel batchModel = (BatchModel) batchModels.get(i);
				batchModel.setMoleAmount(molAmt);
			}
			this.modelChanged = true;
			this.updateAllListLevelFlags();
		}
	}

	/*
	 * Sets reactionRole
	 * 
	 * This is a StoicModelInterface method
	 */
	public void setStoicReactionRole(String rxnRole) {
		if (!this.rxnRole.equals(rxnRole)) {
			for (int i = 0; i < batchModels.size(); i++) {
				BatchModel batchModel = (BatchModel) batchModels.get(i);
				batchModel.setStoicReactionRole(rxnRole);
			}
			this.rxnRole = rxnRole;
			this.modelChanged = true;
		}
	}

	/*
	 * Sets rxnEquivAmt to all the batches in the list. This is a StoicModelInterface method
	 */
	public void setStoicRxnEquivsAmount(AmountModel rxnEquivAmt) {
		if (!this.getStoicRxnEquivsAmount().equals(rxnEquivAmt)) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
			batchModel.setRxnEquivsAmount(rxnEquivAmt);
		}
		setModelChanged(true);
		this.updateAllListLevelFlags();
		}
	}

	/*
	 * Sets solute to all the Batches in the list
	 * 
	 * This is a StoicModelInterface method
	 */
	public void setStoicSolute(String solute) {
		if (!this.getStoicSolute().equals(solute)) {
			for (int i = 0; i < batchModels.size(); i++) {
				BatchModel batchModel = (BatchModel) batchModels.get(i);
				batchModel.setStoicSolute(solute);
			}
			this.modelChanged = true;
		}
	}

	/*
	 * Sets solute amount to all the Batches in the list This is a StoicModelInterface method
	 */
	public void setStoicSoluteAmount(AmountModel soluteAmt) {
		if (!this.getStoicSoluteAmount().equals(soluteAmt)) {
			for (int i = 0; i < batchModels.size(); i++) {
				BatchModel batchModel = (BatchModel) batchModels.get(i);
				batchModel.setSoluteAmount(soluteAmt);
			}
			this.modelChanged = true;
		}
	}

	public BatchesList<E> deepClone() {
		// BatchesList key should be the same
		BatchesList<E> batchesListModel = new BatchesList<E>(this.getKey());
		batchesListModel.setPosition(position);
		batchesListModel.setDedupedList(isDedupedList);
		batchesListModel.setStoicReactionRole(rxnRole);
		batchesListModel.setLoadedFromDB(this.isLoadedFromDB());
		// The above setters will make modelChanged on this cloned object to true.
		batchesListModel.setModelChanged(false);
		// Get the actual flag in the object
		batchesListModel.setModelChanged(this.modelChanged);
		ArrayList<E> changedModels = getClonedModifiedBatchModelsList();
		if (changedModels.size() > 0) {
			batchesListModel.setBatchModels(changedModels);
			batchesListModel.setModelChanged(true);
		}

		return batchesListModel;
	}
	
	public ArrayList<E> getClonedModifiedBatchModelsList() {
		ArrayList<E> changedBatchesModel = new ArrayList<E>();
		List<E> models = this.getBatchModels();
		if (models != null && models.size() > 0) {
			for (E model : models) {
				if (model.isModelChanged() && changedBatchesModel.contains(model) == false) {
					changedBatchesModel.add((E) model.deepClone());
				}
			}
//			} else if (models.get(0) instanceof ProductBatchModel) {
//				for (int i = 0; i < size; i++) {
//					ProductBatchModel model = (ProductBatchModel) models.get(i);
//					if (model.isModelChanged()) {
//						changedBatchesModel.add((E)model.deepClone());
//					}
//				}
//			}
		}

		return changedBatchesModel;
	}

	// For ChemicalName
	public String getStoicChemicalName() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicChemicalName();
  		}else
  		{
  			return "";	
		}
	}

	public void setStoicChemicalName(String chemName) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setStoicChemicalName(chemName);
  		}else
  		{
  		    //Do nothing on List size > 1	
		}
	}

	// For MolecularFormula
	public String getStoicMolecularFormula() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicMolecularFormula();
  		}else
  		{
  			return "";	
		}
	}

	public void setStoicMolecularFormula(String molFormula) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setStoicMolecularFormula(molFormula);
  		}else
  		{
  		    //Do nothing on List size > 1	
		}	
	}

	// For CompoundId
	public String getStoicCompoundId() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getCompoundId();
  		}else
  		{
  			return "";	
		}
	}

	public void setStoicCompoundId(String compoundId) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setStoicCompoundId(compoundId);
  		}else
  		{
  		    //Do nothing on List size > 1	
		}	
	}

	// For NBKBatchNo
	public BatchNumber getStoicBatchNumber() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicBatchNumber();
  		}else
  		{
  			return null;	
		}
	}

	public void setStoicBatchNumber(BatchNumber nbkBatchNo) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.getStoicBatchNumber().deepCopy(nbkBatchNo);
  		}else
  		{
  		    //Do nothing on List size > 1	
		}	
	}

	// For MolecularWeight
	public AmountModel getStoicMolecularWeightAmount() {
		if(getListSize() == 1)
 		{
			BatchModel batchModel = (BatchModel) batchModels.get(0);
			return batchModel.getStoicMolecularWeightAmount ();
 		}else
 		{
 			return listMolecularWeightAmount  ;	
		}
	}

	public void setStoicMolecularWeightAmount(AmountModel molWeight) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setMolecularWeightAmount(molWeight);
  		}
	}

	// For Weight
	public AmountModel getStoicWeightAmount() {
		if(getListSize() == 1 || (this.isAllBatchModelsWeightAmountIsSame) )
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicWeightAmount ();
  		}else
  		{
  			return listWeightAmount  ;	
		}
	}

	public void setStoicWeightAmount(AmountModel weight) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
			batchModel.setWeightAmount(weight);
			
  		}
		setModelChanged(true);	
		//Set the same value at list level. ( value and isCalc )
		this.updateAllListLevelFlags();
	}

	// For Density
	public AmountModel getStoicDensityAmount() {
		 if(getListSize() == 1 || (this.isAllBatchModelsDensityAmountIsSame))
	  		{
	 			BatchModel batchModel = (BatchModel) batchModels.get(0);
	 			return batchModel.getDensityAmount();
	  		}else
	  		{
	  			return listDensityAmount ;	
			}
	 
	}

	public void setStoicDensityAmount(AmountModel density) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
			batchModel.setDensityAmount(density);
  		}
		setModelChanged(true);
		//Set the same value at list level. ( value and isCalc )
		this.updateAllListLevelFlags();
	}

	// For Volume
	public AmountModel getStoicVolumeAmount() {
		 if(getListSize() == 1 || (this.isAllBatchModelsVolumeAmountIsSame))
	  		{
	 			BatchModel batchModel = (BatchModel) batchModels.get(0);
	 			return batchModel.getStoicVolumeAmount ();
	  		}else
	  		{
	  			return listVolumeAmount  ;	
			}
	 
	}

	public void setStoicVolumeAmount(AmountModel volume) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
 			batchModel.setVolumeAmount(volume);
  		}
		setModelChanged(true);
		//Set the same value at list level. ( value and isCalc )
		this.updateAllListLevelFlags();
	}

	// For BarCode
	public String getBarCode() {
		return "";
	}

	public void setBarCode(String barCode) {
		// Do nothing
	}

	// For Hazards
	public String getStoicHazardsComments() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicHazardsComments();
  		}else
  		{
  			return "";	
		}
	}

	public void setStoicHazardsComments(String hazards) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setStoicHazardsComments(hazards);
 			modelChanged = true;
  		}
	}

	public String getStoicLabel() {
		if (getListSize() == 1 || isAllBatchModelsStoicLabelAreSame) {
			BatchModel model = (BatchModel) batchModels.get(0);
			if (model.getStoicLabel() == null) {
				setStoicLabel(getPosition());
			}
			return model.getStoicLabel();
		} else {
			if (stoicLabel == null) {
				setStoicLabel(getPosition());
			}
			return stoicLabel;
		}
	}

	public void setStoicLabel(String label) {
		if (isAllBatchModelsStoicLabelAreSame) {
			for (int i = 0; i < batchModels.size(); ++i) {
				BatchModel model = (BatchModel) batchModels.get(i);
				model.setStoicLabel(label);
			}
			setModelChanged(true);
			updateAllListLevelFlags();
		}
		stoicLabel = label;
	}
	
	//For Stoich Comments
	public String getStoichComments() {
		if (getListSize() == 1 || (this.isAllBatchModelsStoichCommentAreSame)) {
			BatchModel batchModel = (BatchModel) batchModels.get(0);
			return batchModel.getStoichComments();
		} else {
			return "";
		}
	}
	
	public void setStoichComments(String comments) {
		//to ensure data is not edited if different for batch models
		if (!isAllBatchModelsStoichCommentAreSame)
			return;
		
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
			batchModel.setStoichComments(comments);
  		}
		setModelChanged(true);
		//Set the same value at list level. ( value and isCalc )
		this.updateAllListLevelFlags();
	}
		
	// For TotalWeight
	public AmountModel getTotalWeight() {
		if(getListSize() == 1)
 		{
 			return null;
 		}else
 		{
 			return null;	
		}
	}

	public void setTotalWeight(AmountModel totalWeight) {
		// Do nothing
	}

	// For TotalVolume
	public AmountModel getTotalVolume() {
		if(getListSize() == 1)
 		{
 			return null;
 		}else
 		{
 			return null;	
		}
	}

	public void setTotalVolume(AmountModel totalVolume) {
		// Do nothing
	}

	/**
	 * @return the transactionOrder
	 */
	public int getStoicTransactionOrder() {
		int size = this.batchModels.size();
		if (size > 0) {
			BatchModel model = (BatchModel) this.batchModels.get(0);
			return model.getTransactionOrder();
		} else
			return 0;
	}

	/**
	 * @param transactionOrder
	 *            the transactionOrder to set
	 */
	public void setStoicTransactionOrder(int transactionOrder) {
		int size = this.batchModels.size();
		for (int i = 0; i < size; i++) {
			BatchModel model = (BatchModel) this.batchModels.get(i);
			model.setTransactionOrder(transactionOrder);
		}
		this.modelChanged = true;
	}

	public void markToBeDeleted(boolean deleted) {
		// Do nothing, Method from StoicModelInterface
	}

	/**
	 * Will not tell you if the batch didn't exist. Just returns true to indicate batch no longer exists in list
	 * 
	 * @param batch
	 * @return
	 */
	public boolean removeBatch(Object batch) {
		boolean result = true;
		if(batchModels.contains(batch)) {
			result = batchModels.remove(batch);
		}
		return result;
	}

	public void clearStoicData() {
		// Clear fields that are shown in stoic

	}

	public String getStoicBatchCASNumber() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicBatchCASNumber();
  		}else
  		{
  			return "";	
		}
	}

	public void setStoicBatchCASNumber(String casNumber) {

	}

	public Double getStoicBatchSaltEquivs() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicBatchSaltEquivs();
  		}else
  		{
  			return null;	
		}
	}

	public void setStoicBatchSaltEquivs(double saltEq) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setStoicBatchSaltEquivs(saltEq);
  		}
	}

	public SaltFormModel getStoicBatchSaltForm() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicBatchSaltForm();
  		}else
  		{
  			return listSaltForm ;	
		}
	}

	public void setStoicBatchSaltform(SaltFormModel salt) {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setStoicBatchSaltform(salt);
  		}else
  		{
  		  // do nothing on a list > 1;	
		}
	}
    // For Purity amount
	public AmountModel getStoicPurityAmount() {
		if(getListSize() == 1 || (this.isAllBatchModelsPurityAmountIsSame))
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicPurityAmount ();
  		}else
  		{
  			return listPurityAmount  ;	
		}
	}

	public void setStoicPurityAmount(AmountModel purityModel) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
 			batchModel.setPurityAmount(purityModel);
  		}
		setModelChanged(true);	
		this.updateAllListLevelFlags();
	}

	public AmountModel getStoicMolarAmount() {
		if(getListSize() == 1 || (this.isAllBatchModelsMolarAmountIsSame))
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicMolarAmount ();
  		}else
  		{
  			return listMolarAmount ;	
		}
	}

	public void setStoicMolarAmount(AmountModel molarityModel) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
			batchModel.setMolarAmount(molarityModel);
  		}
		setModelChanged(true);	
		this.updateAllListLevelFlags();
		
	}

	public AmountModel getStoicLoadingAmount()
	{
		if(getListSize() == 1 || (this.isAllBatchModelsLoadingAmountISSame))
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getStoicLoadingAmount ();
  		}else
  		{
  			return listLoadingAmount  ;	
		}
 
 	}

	public void setStoicLoadingAmount(AmountModel loadingModel) {
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
			batchModel.setLoadingAmount(loadingModel);
  		}
		setModelChanged(true);	
		this.updateAllListLevelFlags();
	}

	public String getGUIDKey() {
		return this.key;
	}

	public int getListSize() {
		if (batchModels == null) {
			return 0;
		} else
			return batchModels.size();
	}
	
	public boolean isAutoCalcOn()
	{
		if(getListSize() >= 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.isAutoCalcOn();
  		}else
  			return true;
	}
	
    public boolean shouldApplySigFigRules()
    {
    	
 	  return (!getStoicWeightAmount().isCalculated() || !getStoicVolumeAmount().isCalculated());
  	
    }
	
	public boolean shouldApplyDefaultSigFigs()
	{
		return (!getStoicRxnEquivsAmount().isCalculated() || !getStoicMoleAmount().isCalculated() || !getStoicMolarAmount().isCalculated());
		
	}
	
	public void applyLatestSigDigits(int defaultSigs)
	{
		List<AmountModel> amts = getCalculatedAmounts();
		for (Iterator<AmountModel> i = amts.iterator(); i.hasNext();) {
			AmountModel amt = i.next();
			if (amt.isCalculated()) {
				amt.setSigDigits(defaultSigs);
			}
		}
	}
	
	/**
	 * Amounts that are all interrelated regarding batch calculations. SigDigits apply.
	 * 
	 * @return
	 */
	protected List<AmountModel> getCalculatedAmounts() {
		ArrayList<AmountModel> result = new ArrayList<AmountModel>();
		result.add(getStoicMoleAmount());
		result.add(getStoicWeightAmount());
		result.add(getStoicVolumeAmount());
		result.add(getStoicRxnEquivsAmount()); // Use this to set initial sigdigits = 3 by setting to 1.00 as default.
		return result;
	}
	
	public void setPreviousMolarAmount(AmountModel molarAmount)
	{
		if(getListSize() >= 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setPreviousMolarAmount(molarAmount);
  		}
	}
	
	public AmountModel getPreviousMolarAmount()
	{
		if(getListSize() >= 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getPreviousMolarAmount();
  		}else
  		{
  			return listPreviousMolarAmount;
  		}
	}
	
	public void applySigFigRules(AmountModel amt, List<AmountModel> amts) {
		if (shouldApplySigFigRules()) {
			amt.setSigDigits(CeNNumberUtils.getSmallestSigFigsFromAmountModelList(amts));
		} else {
			if (shouldApplyDefaultSigFigs())
				amt.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		}
	}
	
	public BatchType getBatchType()
	{
		if(getListSize() >= 1)
		{
		 //this is not true in case of stoicBatchesList Since this list may contain SOLV.REAG,MIXTURES etc	
			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getBatchType();	
		}else
		{
			return null;
		}
	}
	
//	empty Impl. ProdBAtchModel will ovveride with correct impl
	public AmountModel getTheoreticalWeightAmount()
	{
		if(getBatchType().getOrdinal() == BatchType.ACTUAL_PRODUCT_ORDINAL)
		{
		if(getListSize() >= 1)
		{
		 	ProductBatchModel batchModel = (ProductBatchModel) batchModels.get(0);
 			return batchModel.getTheoreticalWeightAmount();	
		}
		}
		return null;
	}
	public void setTheoreticalWeightAmount(AmountModel theoreticalWeightAmount)
	{
		if(getBatchType().getOrdinal() == BatchType.ACTUAL_PRODUCT_ORDINAL)
		{
			for (int i = 0; i < batchModels.size(); i++) {
				ProductBatchModel batchModel = (ProductBatchModel) batchModels.get(i);
				batchModel.setTheoreticalWeightAmount(theoreticalWeightAmount);
	  		}
		}
	}
	
	public void recalcAmounts()
	{
		for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
 			batchModel.recalcAmounts();
  		}
	}
	
	public AmountModel getTheoreticalMoleAmount()
	{
		if(getBatchType().getOrdinal() == BatchType.ACTUAL_PRODUCT_ORDINAL)
		{
		if(getListSize() >= 1)
		{
		 	
			ProductBatchModel batchModel = (ProductBatchModel) batchModels.get(0);
 			return batchModel.getTheoreticalMoleAmount();	
		}
		}
		return null;
	}
	public void setTheoreticalMoleAmount(AmountModel theoreticalMoleAmount)
	{
		if(getBatchType().getOrdinal() == BatchType.ACTUAL_PRODUCT_ORDINAL)
		{
			for (int i = 0; i < batchModels.size(); i++) {
				ProductBatchModel batchModel = (ProductBatchModel) batchModels.get(i);
				batchModel.setTheoreticalMoleAmount(theoreticalMoleAmount);
	  		}
		}
	}
	
	public String getStoicSolventsAdded() {
		if(getListSize() >= 1)
		{
		 //this is not true in case of stoicBatchesList Since this list may contain SOLV.REAG,MIXTURES etc	
			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getSolventsAdded();	
		}else
		{
			return null;
		}
		
	}

	public void setStoicSolventsAdded(String vsolventsAdded) {
		if(getListSize() >= 1)
		{
			for (int i = 0; i < batchModels.size(); i++) {
			BatchModel batchModel = (BatchModel) batchModels.get(i);
 			batchModel.setSolventsAdded(vsolventsAdded);
			}
		}
	}
	
	public String getStoicBatchNumberAsString()
	{
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getBatchNumberAsString();
  		}else
  		{
  			return "";	
		}
		
	}
	
	public void setStoicBatchNumber(String nbkBatchNo) throws InvalidBatchNumberException
	{
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			batchModel.setBatchNumber(nbkBatchNo);
  		}else
  		{
  		    //Do nothing on List size > 1	
		}	
	}

	public boolean isAllBatchModelsDensityAmountIsSame() {
		return isAllBatchModelsDensityAmountIsSame;
	}

	public void setAllBatchModelsDensityAmountIsSame(boolean isAllBatchModelsDensityAmountIsSame) {
		this.isAllBatchModelsDensityAmountIsSame = isAllBatchModelsDensityAmountIsSame;
	}

	public boolean isAllBatchModelsLoadingAmountISSame() {
		return isAllBatchModelsLoadingAmountISSame;
	}

	public void setAllBatchModelsLoadingAmountISSame(boolean isAllBatchModelsLoadingAmountISSame) {
		this.isAllBatchModelsLoadingAmountISSame = isAllBatchModelsLoadingAmountISSame;
	}

	public boolean isAllBatchModelsMolarAmountIsSame() {
		return isAllBatchModelsMolarAmountIsSame;
	}

	public void setAllBatchModelsMolarAmountIsSame(boolean isAllBatchModelsMolarAmounIsSame) {
		this.isAllBatchModelsMolarAmountIsSame = isAllBatchModelsMolarAmounIsSame;
	}

	public boolean isAllBatchModelsMoleAmountIsSame() {
		return isAllBatchModelsMoleAmountIsSame;
	}

	public void setAllBatchModelsMoleAmountIsSame(boolean isAllBatchModelsMoleAmountIsSame) {
		this.isAllBatchModelsMoleAmountIsSame = isAllBatchModelsMoleAmountIsSame;
	}

	public boolean isAllBatchModelsPurityAmountIsSame() {
		return isAllBatchModelsPurityAmountIsSame;
	}

	public void setAllBatchModelsPurityAmountIsSame(boolean isAllBatchModelsPurityAmountIsSame) {
		this.isAllBatchModelsPurityAmountIsSame = isAllBatchModelsPurityAmountIsSame;
	}

	public boolean isAllBatchModelsRxnEquivsIsSame() {
		return isAllBatchModelsRxnEquivsIsSame;
	}

	public void setAllBatchModelsRxnEquivsIsSame(boolean isAllBatchModelsRxnEquivsIsSame) {
		this.isAllBatchModelsRxnEquivsIsSame = isAllBatchModelsRxnEquivsIsSame;
	}

	public boolean isAllBatchModelsVolumeAmountIsSame() {
		return isAllBatchModelsVolumeAmountIsSame;
	}

	public void setAllBatchModelsVolumeAmountIsSame(boolean isAllBatchModelsVolumeAmountIsSame) {
		this.isAllBatchModelsVolumeAmountIsSame = isAllBatchModelsVolumeAmountIsSame;
	}

	public boolean isAllBatchModelsWeightAmountIsSame() {
		return isAllBatchModelsWeightAmountIsSame;
	}

	public void setAllBatchModelsWeightAmountIsSame(boolean isAllBatchModelsWeightAmountIsSame) {
		this.isAllBatchModelsWeightAmountIsSame = isAllBatchModelsWeightAmountIsSame;
	}

	public boolean isAllBatchPreviousMolarAmountIsSame() {
		return isAllBatchPreviousMolarAmountIsSame;
	}

	public void setAllBatchPreviousMolarAmountIsSame(boolean isAllBatchPreviousMolarAmountIsSame) {
		this.isAllBatchPreviousMolarAmountIsSame = isAllBatchPreviousMolarAmountIsSame;
	}

	public AmountModel getListDensityAmount() {
		return listDensityAmount;
	}

	public void setListDensityAmount(AmountModel listDensityAmount) {
		this.listDensityAmount.deepCopyWithoutKeys(listDensityAmount);
	}

	public AmountModel getListLoadingAmount() {
		return listLoadingAmount;
	}

	public void setListLoadingAmount(AmountModel listLoadingAmount) {
		this.listLoadingAmount.deepCopyWithoutKeys(listLoadingAmount);
	}

	public AmountModel getListMolarAmount() {
		return listMolarAmount;
	}

	public void setListMolarAmount(AmountModel listMolarAmount) {
		this.listMolarAmount.deepCopyWithoutKeys(listMolarAmount);
	}

	public AmountModel getListMoleAmount() {
		return listMoleAmount;
	}

	public void setListMoleAmount(AmountModel listMoleAmount) {
		this.listMoleAmount.deepCopyWithoutKeys(listMoleAmount);
		this.isAllBatchModelsMoleAmountIsSame = true;
	}

	
	public AmountModel getListPreviousMolarAmount() {
		return listPreviousMolarAmount;
	}

	public void setListPreviousMolarAmount(AmountModel listPreviousMolarAmount) {
		this.listPreviousMolarAmount.deepCopyWithoutKeys(listPreviousMolarAmount);
		this.isAllBatchPreviousMolarAmountIsSame = true;
	}

	public AmountModel getListPurityAmount() {
		return listPurityAmount;
	}

	public void setListPurityAmount(AmountModel listPurityAmount) {
		this.listPurityAmount.deepCopyWithoutKeys(listPurityAmount);
		this.isAllBatchModelsPurityAmountIsSame = true;
	}

	public AmountModel getListRxnEquivsAmount() {
		return listRxnEquivsAmount;
	}

	public void setListRxnEquivsAmount(AmountModel listRxnEquivsAmount) {
		this.listRxnEquivsAmount.deepCopyWithoutKeys(listRxnEquivsAmount);
		this.isAllBatchModelsRxnEquivsIsSame = true;
	}

	public SaltFormModel getListSaltForm() {
		return listSaltForm;
	}

	public void setListSaltForm(SaltFormModel listSaltForm) {
		this.listSaltForm.deepCopy(listSaltForm);
	}

	public AmountModel getListVolumeAmount() {
		return listVolumeAmount;
	}

	public void setListVolumeAmount(AmountModel listVolumeAmount) {
		this.listVolumeAmount.deepCopyWithoutKeys(listVolumeAmount);
		this.isAllBatchModelsVolumeAmountIsSame = true;
	}

	public AmountModel getListWeightAmount() {
		return listWeightAmount;
	}

	public void setListWeightAmount(AmountModel listWeightAmount) {
		this.listWeightAmount.deepCopyWithoutKeys(listWeightAmount);
		this.isAllBatchModelsWeightAmountIsSame = true;
	}
	
	public int getStoicListSize() {
		return getListSize();
	}
	
	public boolean findIfAllMoleAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = this.batchModels.get(i);
				if(!refBatchModel.getMoleAmount().equalsByValueAndUnits(batchModel.getMoleAmount()))
				{
					//System.out.println("Mole Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllMolarAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.getMolarAmount().equalsByValueAndUnits(batchModel.getMolarAmount()))
				{
					//System.out.println("Molar Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllRxnEquivsAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.getRxnEquivsAmount().equalsByValueAndUnits(batchModel.getRxnEquivsAmount()))
				{
					//System.out.println("RxnEquiv Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllWeightAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.getWeightAmount().equalsByValueAndUnits(batchModel.getWeightAmount()))
				{
					//System.out.println("Weight Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllVolumeAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.getVolumeAmount().equalsByValueAndUnits(batchModel.getVolumeAmount()))
				{
					//System.out.println("Volume Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllDensityAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.getDensityAmount().equalsByValueAndUnits(batchModel.getDensityAmount()))
				{
					//System.out.println("Density Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllPurityAmountsAreSameInTheList()
	{
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.getPurityAmount().equalsByValueAndUnits(batchModel.getPurityAmount()))
				{
					//System.out.println("Purity Amount in Batch didn't match at position:"+i+ " value is:"+batchModel.getMoleAmount().doubleValue());
					return false;
				}
			}
		}
		return isSameVal;
	}
	
	public boolean findIfAllLoadingAmountsAreSameInTheList()
	{
		return true;
	}
	
	public boolean findIfAllSaltCodeandAmountsAreSameInTheList()
	{
		return true;
	}
	
	public void updateAllListLevelFlags()
	{
		this.isAllBatchModelsMoleAmountIsSame = this.findIfAllMoleAmountsAreSameInTheList();
		this.isAllBatchModelsWeightAmountIsSame = this.findIfAllWeightAmountsAreSameInTheList();
		this.isAllBatchModelsLoadingAmountISSame = this.findIfAllLoadingAmountsAreSameInTheList();
		this.isAllBatchModelsVolumeAmountIsSame = this.findIfAllVolumeAmountsAreSameInTheList();
		this.isAllBatchModelsDensityAmountIsSame = this.findIfAllDensityAmountsAreSameInTheList();
		this.isAllBatchModelsMolarAmountIsSame = this.findIfAllMolarAmountsAreSameInTheList();
		this.isAllBatchModelsPurityAmountIsSame = this.findIfAllPurityAmountsAreSameInTheList();
		this.isAllBatchModelsRxnEquivsIsSame = this.findIfAllRxnEquivsAmountsAreSameInTheList();
		this.isAllBatchModelsLimitingReagentIsSame = this.findIfAllLimitingReagentsAreSameInTheList();
		this.isAllBatchModelsStoichCommentAreSame = this.findIfAllStoichCommentAreSameInTheList();
		this.isAllBatchModelsStoicLabelAreSame = this.findIfAllBatchModelsStoicLabelAreSame();
	}
	
	private boolean findIfAllBatchModelsStoicLabelAreSame() {
		boolean isSameVal = true;
		if (batchModels == null || batchModels.size() < 2) {
			return isSameVal;
		} else {
			BatchModel refModel = (BatchModel) batchModels.get(0);
			for (int i = 1; i < batchModels.size(); ++i) {
				BatchModel model = (BatchModel) batchModels.get(i);
				String refLabel = refModel.getStoicLabel();
				String label = model.getStoicLabel();
				if (refLabel != null) {
					if (!refLabel.equals(label)) {
						return false;
					}
				} else {
					if (label != null) {
						return false;
					}
				}
			}
		}
		return isSameVal;
	}

	private boolean findIfAllStoichCommentAreSameInTheList() {
		boolean isSameVal = true;
		if (this.batchModels == null || this.batchModels.size() < 2) {
			return isSameVal;
		} else {
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel) this.batchModels.get(0);
			for (int i = 1; i < size; i++) {
				BatchModel batchModel = (BatchModel) this.batchModels.get(i);
				String refComments = refBatchModel.getStoichComments();
				String comments = batchModel.getStoichComments();
				if (refComments != null) {
					if (!refComments.equals(comments)) {
						return false;
					}
				} else {
					if (comments != null) {
						return false;
					}
				}
			}
		}
		return isSameVal;
	}
	
	private boolean findIfAllLimitingReagentsAreSameInTheList() {
		boolean isSameVal = true;
		if(this.batchModels == null || this.batchModels.size() < 2 )
		{
			return isSameVal;
		}else
		{
			int size = this.batchModels.size();
			BatchModel refBatchModel = (BatchModel)this.batchModels.get(0);
			for(int i = 1 ; i < size ; i ++)
			{
				BatchModel batchModel = (BatchModel)this.batchModels.get(i);
				if(!refBatchModel.isLimiting() == batchModel.isLimiting())
				{
					return false;
				}
			}
		}
		return isSameVal;
	}

		
	public boolean equals(Object o) {
		boolean result = false;
		if (o != null && o instanceof BatchesList) {
			BatchesList ab = (BatchesList) o;
			if (this.getKey().equals(ab.getKey()))
				result = true;
		}
		return result;
	}
	
	public void resetAmount(String amountName)
	{
		if(batchModels == null) return;
		for (int i = 0; i < batchModels.size(); i++) {
		BatchModel batchModel = (BatchModel) batchModels.get(i);
		  		
		if(amountName.equals(CeNConstants.MOLE_AMOUNT))
		{
		  batchModel.getMoleAmount().reset();
			
		}else if (amountName.equals(CeNConstants.RXN_EQUIVS_AMOUNT))
		{
			
		}
		}
	}

	public void deepCopy(BatchesList<E> src) {
		setPosition(src.position);
		setDedupedList(src.isDedupedList);
		setStoicReactionRole(src.rxnRole);
		setLoadedFromDB(src.isLoadedFromDB());
		// The above setters will make modelChanged on this cloned object to true.
		setModelChanged(false);
		// Get the actual flag in the object
		setModelChanged(src.modelChanged);
		ArrayList<E> changedModels = src.getCopiedBatchModelsList(this.getKey());
		setBatchModels(changedModels);
	}

	private ArrayList<E> getCopiedBatchModelsList(String listKey) {
		ArrayList<E> copiedBatchesModel = new ArrayList<E>();
		List<E> models = this.getBatchModels();
		if (models != null && models.size() > 0) {
			int size = models.size();
			E copiedBatchModel = null;
			for (int i = 0; i < size; i++) {
					E model = (E) models.get(i);
					if (models.get(0) instanceof MonomerBatchModel) {
						copiedBatchModel = (E) new MonomerBatchModel();
					} else {
						copiedBatchModel = (E) new ProductBatchModel();
					}
					copiedBatchModel.setListKey(listKey);
					copiedBatchModel.deepCopy(model);
					copiedBatchModel.setListKey(getKey());
					copiedBatchesModel.add(copiedBatchModel);
//			} else if (models.get(0) instanceof ProductBatchModel) {
//				for (int i = 0; i < size; i++) {
//					ProductBatchModel model = (ProductBatchModel) models.get(i);
//					ProductBatchModel copiedBatchModel = new ProductBatchModel();
//					copiedBatchModel.setListKey(listKey);
//					copiedBatchModel.deepCopy(model);
//					copiedBatchModel.setListKey(getKey());
//					copiedBatchesModel.add(copiedBatchModel);
//				}
			}
		}

		return copiedBatchesModel;
	}

	public ParentCompoundModel getStoichCompoundModel() {
		if(getListSize() == 1)
  		{
 			BatchModel batchModel = (BatchModel) batchModels.get(0);
 			return batchModel.getCompound();
  		}else
  		{
  			return null;	
		}
	}
}