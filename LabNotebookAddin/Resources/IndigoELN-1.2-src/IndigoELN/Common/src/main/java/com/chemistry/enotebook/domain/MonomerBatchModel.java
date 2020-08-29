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
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class MonomerBatchModel extends BatchModel {

	public static final long serialVersionUID = 7526472295622776147L;
	// Note: Use RegNumber in the Compound Object instead.
	// private String monomerId; //identifier for this batch . like MN-234324

	private String reactantPosition = "";
	private int priority=-1;
	private String annotation=""; 
	private String orderId = "";
	private boolean selected = false;
	private AmountModel amountNeeded = new AmountModel(UnitType.MOLES);
	private AmountModel extraNeeded = new AmountModel(UnitType.MOLES);
	//This is no longer used. We use total delivered instead
	//private AmountModel totalOrdered = new AmountModel(UnitType.VOLUME);
	private int noOfTimesUsed = 0;
	//Added for stoich purpose
	private AmountModel deliveredWeight = new AmountModel(UnitType.MASS);
	//private boolean autocalcaltedWeight = false;
	private AmountModel deliveredVolume = new AmountModel(UnitType.VOLUME);  // VB 8/1
	
	private String deliveredMonomerID = "";
	private String solute = "";
	//List of keys for the Products generated involving this monomer
	private List<String> genratedProductBatchKeys = new ArrayList<String>();
	//Monomers and Products have diffrent set of status values
	private String status ;
	
	//This is parent compound ID this concorded compound matched to.This will help to link
	// the original compound to this concorded compound.Compound ID can be CP-100.
	// concordanceCompoundID can be PF-10.
    private String concordedCompoundID="";

	private AmountModel totalVolumeDelivered = new AmountModel(UnitType.VOLUME);
    

	//
	// Constructors
	//
	public MonomerBatchModel() {
		super();
		super.setBatchType(BatchType.REAGENT);
	}
	
	//Use this constructor to create SOLVENT,REAGENT,MIXTURE so on
	public MonomerBatchModel(BatchType type) {
		super();
		super.setBatchType(type);
	}
	
	
	public MonomerBatchModel(BatchModel batch) {
		super(batch);
		if (batch instanceof MonomerBatchModel) {
			setTotalVolumeDelivered(((MonomerBatchModel)batch).getTotalVolumeDelivered());
		}
	}

	public MonomerBatchModel(String key) {
		super(key);
		this.key = key;
	}

	// 
	// Setters/Getters
	//
	public List<String> getGenratedProductBatchKeys() {
		return genratedProductBatchKeys;
	}

	public void setGenratedProductBatchKeys(List<String> genratedProductBatchKeys) {
		this.genratedProductBatchKeys = genratedProductBatchKeys;
		this.modelChanged = true;
	}

	public int getNoOfTimesUsed() {
		return noOfTimesUsed;
	}

	public void setNoOfTimesUsed(int noOfTimesUsed) {
		this.noOfTimesUsed = noOfTimesUsed;
		this.modelChanged = true;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		if(annotation != null){
			if(!this.annotation.equals(annotation)){
				this.annotation = annotation;
				this.modelChanged = true;
			}
		}
	}

	public AmountModel getExtraNeeded() {
		extraNeeded.setUnit(this.getMoleAmount().getUnit());
		return this.extraNeeded;
	}

	public void setExtraNeeded(AmountModel extraNeeded) {
		if(this.extraNeeded != null ){
			if(!this.extraNeeded.equals(extraNeeded)){
				this.extraNeeded = extraNeeded;
				getMoleAmount().setUnit(extraNeeded.getUnit());
				this.modelChanged = true;
			}
		}
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		if(orderId != null){
			if(!this.orderId.equals(orderId)){
				this.orderId = orderId;
				this.modelChanged = true;
			}
		}
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		if(this.priority != priority){
			this.priority = priority;
			this.modelChanged = true;
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if(this.selected != selected) {
			this.selected = selected;
			this.modelChanged = true;
		}
	}
	/*
	 * Total Ordered Amount or Reaction Amount,  = (AmtNeeded*TimesUsed*EQ)+ExtraNeeded
	 *    
	 */
	public AmountModel getTotalWeightNeeded() {
		AmountModel totalWeightNeeded = new AmountModel(UnitType.MASS);
		double amtNeeded = this.getAmountNeeded().GetValueInStdUnitsAsDouble();
		double extraNeeded = this.getExtraNeeded().GetValueInStdUnitsAsDouble();
		//double rxnEq = this.getRxnEquivsAmount().GetValueInStdUnitsAsDouble();
		
		double reactionWeightAmount = ((amtNeeded*getNoOfTimesUsed())+extraNeeded) * this.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
		totalWeightNeeded.setValue(reactionWeightAmount);
		
		return totalWeightNeeded;
	}
	
	/*
	 * Total Ordered Amount or Reaction Amount,  = (AmtNeeded*TimesUsed*EQ)+ExtraNeeded
	 *    
	 */
	public AmountModel getTotalMolesNeeded() {
		AmountModel totalMolesNeedeed = new AmountModel(UnitType.MOLES);
		double amtNeeded = this.getAmountNeeded().GetValueInStdUnitsAsDouble();
		double extraNeeded = this.getExtraNeeded().GetValueInStdUnitsAsDouble();
		//double rxnEq = this.getRxnEquivsAmount().GetValueInStdUnitsAsDouble();
		
		double reactionMolesAmount =(amtNeeded*getNoOfTimesUsed())+extraNeeded;
		totalMolesNeedeed.setValue(reactionMolesAmount);
		totalMolesNeedeed.setUnit(getMoleAmount().getUnit());
		return totalMolesNeedeed;
	}

	public AmountModel getTotalVolumeNeeded() {
		AmountModel totalVolumeNeeded = new AmountModel(UnitType.VOLUME);
		double amtNeeded = this.getAmountNeeded().GetValueInStdUnitsAsDouble();
		double extraNeeded = this.getExtraNeeded().GetValueInStdUnitsAsDouble();
		double reactionVolumeAmount = ((amtNeeded*getNoOfTimesUsed())+extraNeeded) * this.getVolumeAmount().GetValueInStdUnitsAsDouble();
		totalVolumeNeeded.setValue(reactionVolumeAmount);
		return totalVolumeNeeded;
	}
	
//	public void setTotalOrdered(AmountModel totalOrdered) {
//		this.totalOrdered = totalOrdered;
//		this.modelChanged = true;
//	}

	public AmountModel getTotalWeight() {
//		double totalOrderedAmt = getTotalOrdered().GetValueInStdUnitsAsDouble();
//		double molWeight = getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
//		double weight = totalOrderedAmt*molWeight;
//		AmountModel totalWeight = super.getTotalWeight();
//		totalWeight.setValue(weight);
//		return totalWeight;
		return super.getTotalWeight();
	}

	public void setMonomerId(String monomerId) {
		this.getCompound().setRegNumber(monomerId);
		this.modelChanged = true;
	}

	public void setBatchId(String batchId) {
		setMonomerId(batchId);
	}
	
	public String getMonomerId() {
		return getCompound().getRegNumber();
	}
	
	public String getBatchId() {
		return getMonomerId();
	}
	/**
	 * Delivered Amount = derived from TotalOrderedAmount
	 * @return the deliveredVolume
	 */
	public AmountModel getDeliveredVolume() {
		/*
		double deliveredWgt = getDeliveredWeight().GetValueInStdUnitsAsDouble();
		double molWeight = getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
		double deliveredAmt = deliveredWgt/molWeight;
		deliveredVolume.setValue(deliveredAmt);
		*/
		return deliveredVolume;
	}

	/**
	 * Delivered Weight = Sum of well amounts
	 * @return the deliveredWeight
	 */
	public AmountModel getDeliveredWeight() {
		//if (autocalcaltedWeight) {
		if (deliveredWeight.isCalculated()) {
			double molarAmount = getMolarAmount().GetValueInStdUnitsAsDouble();
			double deliveredVol = getDeliveredVolume().GetValueInStdUnitsAsDouble();			
			if (molarAmount != 0) {
				double molWeight = getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
				deliveredWeight.setValue(deliveredVol * molWeight * molarAmount);
			} else {
				double densityAmount = getDensityAmount().GetValueInStdUnitsAsDouble();
				deliveredWeight.setValue(deliveredVol * densityAmount);
			}
			return deliveredWeight;
		} else {
//			double wellAmt = getTotalWellAmount().getValue();
//			double tubeAmt = getTotalTubeAmount().getValue();
//			deliveredWeight.setValue(wellAmt+tubeAmt);
			return deliveredWeight;
		}
	}

	/**
	 * @return the amountNeeded
	 */
	public AmountModel getAmountNeeded() {
		return amountNeeded;
	}

	/**
	 * @param amountNeeded the amountNeeded to set
	 */
	public void setAmountNeeded(AmountModel amountNeeded) {
		if(amountNeeded != null){
			if(!this.amountNeeded.equals(amountNeeded)){
				this.modelChanged = true;
				this.amountNeeded.deepCopy(amountNeeded);
			}
		}
	}

	

	/**
	 * @return the reactantPosition
	 */
	public String getReactantPosition() {
		return reactantPosition;
	}

	/**
	 * @param reactantPosition the reactantPosition to set
	 */
	public void setReactantPosition(String reactantPosition) {
		this.reactantPosition = reactantPosition;
		this.modelChanged = true;
	}
	
	
	public void setDeliveredVolume(AmountModel vdeliveredVolume) {
		if (vdeliveredVolume != null) {
			if (vdeliveredVolume.getUnitType().getOrdinal() == UnitType.VOLUME.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!deliveredVolume.equals(vdeliveredVolume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(deliveredVolume, vdeliveredVolume);
					deliveredVolume.deepCopy(vdeliveredVolume);
					deliveredWeight.setCalculated(true);
					//autocalcaltedWeight = true;					
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			deliveredVolume.setValue("0");
		}
	}

	public void setDeliveredWeight(AmountModel vdeliveredWeight) {
		if (vdeliveredWeight != null) {
			if (vdeliveredWeight.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!deliveredWeight.equals(vdeliveredWeight)) {
					unitChange = BatchUtils.isUnitOnlyChanged(deliveredWeight, vdeliveredWeight);
					deliveredWeight.deepCopy(vdeliveredWeight);
					deliveredWeight.setCalculated(false);
					//autocalcaltedWeight = false;
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			deliveredWeight.setValue("0");
		}
	}

	//public void setTotalOrdered(AmountModel totalOrdered) {
	//	this.totalOrdered = totalOrdered;
	//}

	public void setTotalWeight(AmountModel totalWeight) {
		super.setTotalWeight(totalWeight);
	}
	

	
	//For MolecularFormula
	public String getParentMolecularFormula(){
		return this.getCompound().getMolFormula();
	}
	public void setParentMolecularFormula(String molFormula){
		this.getCompound().setMolFormula(molFormula);
		this.modelChanged = true;
	}
	
	
	
	//For NBKBatchNo
	public BatchNumber getBatchNumber(){
		return super.getBatchNumber();
	}
	public void setBatchNumber(BatchNumber nbkBatchNo){
		super.setBatchNumber(nbkBatchNo);
	}
	
	//For MolecularWeight
	public AmountModel getMolecularWeightAmount(){
		return super.getMolecularWeightAmount();
	}
	
	public void setMolecularWeightAmount(AmountModel molWeight){
		super.setMolecularWeightAmount(molWeight);
	}
	
	//For Weight
	public AmountModel getWeightAmount(){
		return super.getWeightAmount();
	}
	public void setWeightAmount(AmountModel weight){
		super.setWeightAmount(weight);
	}
	
	//For Density
	public AmountModel getDensityAmount(){
		return super.getDensityAmount();
	}
	public void setDensityAmount(AmountModel densityAmount ){
		super.setDensityAmount(densityAmount);
	}
	
	//For Volume
	public AmountModel getVolumeAmount(){
		return super.getVolumeAmount();
	}
	public void setVolumeAmount(AmountModel volumeAmt ){
		super.setVolumeAmount(volumeAmt);
	}

	public AmountModel getTotalVolumeDelivered() {
		return totalVolumeDelivered ;
	}
	
	public void setTotalVolumeDelivered(AmountModel totalVolumeDelivered) {
		if (totalVolumeDelivered != null) {
			if (getTotalVolumeDelivered().GetValueInStdUnitsAsDouble() != totalVolumeDelivered.GetValueInStdUnitsAsDouble()) {
				this.totalVolumeDelivered = totalVolumeDelivered;
				setModified(true);
			}
		}
	}
	
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(super.toXML());
		xmlbuff.append("<Total_Volume_Delivered>");
		xmlbuff.append(this.getTotalVolumeDelivered().GetValueInStdUnitsAsDouble());
		xmlbuff.append("</Total_Volume_Delivered>");
		xmlbuff.append("<Delivered_Monomer>");
		xmlbuff.append(this.deliveredMonomerID);
		xmlbuff.append("</Delivered_Monomer>");
		xmlbuff.append("<Generated_Products>"+ 
				//CommonUtils.convertArrayListToXML(this.genratedProductBatchKeys) +
				"</Generated_Products>");
		xmlbuff.append("</Meta_Data>");
		xmlbuff.append("</Batch_Properties>");
		return xmlbuff.toString();
	}
	
	public MonomerBatchModel deepClone()
	{
		//MonomerBatchModel  key should be the same
		MonomerBatchModel monModel = new MonomerBatchModel(this.getKey());
		monModel.setBeingCloned(true);
		monModel.deepCopy(this);
		monModel.setBeingCloned(false);
	    return monModel;	
	}

	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		this.modelChanged = true;
	}
	
	public void clearData()
	{
		super.clearData();
	}
	
	
	public boolean equals(Object o) {
		boolean result = false;
		if (o != null && o instanceof MonomerBatchModel) {
			MonomerBatchModel ab = (MonomerBatchModel) o;
			if (this.getKey().equals(ab.getKey()))
				result = true;
		}
		return result;
	}

	public String getDeliveredMonomerID() {
		if (CommonUtils.isNotNull(deliveredMonomerID))
			return deliveredMonomerID;
		else
			return "";
	}

	public void setDeliveredMonomerID(String deliveredMonomerID) {
		this.deliveredMonomerID = deliveredMonomerID;
		this.modelChanged = true;
	}

	public void deepCopy(BatchModel srcBM) {
		MonomerBatchModel src = (MonomerBatchModel)srcBM;
		if (this.key.equals(src.key))// As part of deepClone() this method is called.
		{
			setCompound((ParentCompoundModel) src.getCompound().deepClone());
		} else {
			ParentCompoundModel compModel = new ParentCompoundModel();
			compModel.deepCopy(src.getCompound());
			setCompound(compModel);
		}
		setBatchNumber(src.getBatchNumber());
		setBatchType(src.getBatchType());
		setChloracnegenFlag(src.isChloracnegenFlag());
		setChloracnegenType(src.getChloracnegenType());
		setConversationalBatchNumber(src.getConversationalBatchNumber());
		setDensityAmount(src.getDensityAmount());
		setIntermediate(src.isIntermediate());
		setLimiting(src.isLimiting());
		setListKey(src.getListKey());
		setLoadingAmount(src.getLoadingAmount());
		setMolarAmount(src.getMolarAmount());
		setMoleAmount(src.getMoleAmount());
		setMolecularFormula(src.getMolecularFormula());
		setMolecularWeightAmount(src.getMolecularWeightAmount());
		setParentBatchNumber(src.getParentBatchNumber());
		setPosition(src.getPosition());
		setProjectTrackingCode(src.getProjectTrackingCode());
		setPurityAmount(src.getPurityAmount());
		setRxnEquivsAmount(src.getRxnEquivsAmount());
		setSaltForm(src.getSaltForm());
		setSaltEquivs(src.getSaltEquivs());
		setStepNumber(src.getStepNumber());
		setStoichComments(src.getStoichComments());
		setStoicLabel(src.getStoicLabel());
		setSynthesizedBy(src.getSynthesizedBy());
		setTestedForChloracnegen(src.isTestedForChloracnegen());
		setTransactionOrder(src.getTransactionOrder());
		//src.setVendorInfo(vendorInfo);
		setVolumeAmount(src.getVolumeAmount());
		setWeightAmount(src.getWeightAmount());
		setSoluteAmount(src.getSoluteAmount());
		setTotalVolumeDelivered(src.getTotalVolumeDelivered());
		//MonomerBatch Specific properties
		setReactantPosition(src.reactantPosition);
		setPriority(src.priority);
		setAnnotation(src.annotation); 
		setOrderId(src.orderId);
		setSelected(src.selected);
		setAmountNeeded(src.amountNeeded);
		setExtraNeeded(src.extraNeeded);
		setNoOfTimesUsed(src.noOfTimesUsed);
		setDeliveredWeight(src.deliveredWeight);
		setDeliveredVolume(src.deliveredVolume);
		setDeliveredMonomerID(src.deliveredMonomerID);
		setSolute(src.solute);
		setGenratedProductBatchKeys(src.genratedProductBatchKeys);
		setModelChanged(src.isModelChanged());
		setToDelete(src.isSetToDelete());
		setTotalWeight(src.getTotalWeight());
		setTotalVolume(src.getTotalVolume());
		setTotalMolarity(src.getTotalMolarity());
		setPreviousMolarAmount(src.getPreviousMolarAmount());
		setSolventsAdded(src.getSolventsAdded());
		setLoadedFromDB(src.isLoadedFromDB());
	}

	public String getConcordedCompoundID() {
		return concordedCompoundID;
	}

	public void setConcordedCompoundID(String concordedCompoundID) {
		this.concordedCompoundID = concordedCompoundID;
	}
	
	
}