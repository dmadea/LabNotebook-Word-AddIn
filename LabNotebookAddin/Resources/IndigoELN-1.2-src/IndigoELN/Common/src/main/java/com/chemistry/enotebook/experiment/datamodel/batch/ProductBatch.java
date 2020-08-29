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
/* 
 * IBatch.java
 * 
 * Created on Aug 17, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.experiment.common.Purity;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @date Aug 17, 2004
 * 
 * 
 */
public class ProductBatch extends AbstractBatch {
	
	private static final long serialVersionUID = -6909094729421135222L;
	
	public static final int PASS = 0;
	public static final int FAIL = 1;
	public static final int SUSPECT = 2;

	private BatchType type = BatchType.INTENDED_PRODUCT;
	// theo amounts were added because they are valid search criteria.
	// therefore the data must exist in the database for indexing.
	// The values will be as below unless intendedBatch != null
	private double theoreticalYieldPercent = -1.0; // -1 means not set.
	private Amount theoreticalYieldPercentAmount = null;// = new Amount(UnitType.SCALAR, "-1.0"); // -1 means not set.
	private Amount theoreticalWeightAmount = null;// = new Amount(UnitType.MASS);
	private Amount theoreticalMoleAmount = null;// = new Amount(UnitType.MOLES);
	private boolean productFlag = true; // Use to indicate if this was a targeted product (was what user wants to identify as a
	// final product)

	private ArrayList analyticalPurityList = new ArrayList(); // Holds all purity object which indicate the techniques used to get
	// purity designation for this batch.
	private final BatchRegistrationInfo regInfo = new BatchRegistrationInfo();
	private int intendedBatchAdditionOrder = 0;

	private String parentKey; // GUID of its parent batch (for ActualProd it will be key to coresspoinding IntProd)

	// Note: Use RegNumber in Compound object instead
	// private String compoundId;
	private String productId;
	private String virtualCompoundId;
	private String synthesisPlanProductId;
	private int enumerationSequence;
	private boolean selected;
	private String annotation;
	private boolean registered;
	private int priority;
	private int occuredInStepNumber;

	private int selectivityStatus = PASS;
	private ProductBatchModel batchModel;
	private List analysisList = new ArrayList();

	/**
	 * @return the occuredInStepNumber
	 */
	public int getOccuredInStepNumber() {
		return occuredInStepNumber;
	}

	/**
	 * @param occuredInStepNumber
	 *            the occuredInStepNumber to set
	 */
	public void setOccuredInStepNumber(int occuredInStepNumber) {
		this.occuredInStepNumber = occuredInStepNumber;
	}

	public ProductBatch() {
		populatePBControllerFromPOJO(this, new ProductBatchModel());
		this.setProtectionCode("NONE");
		regInfo.addObserver(this);
	}

	public ProductBatch(ProductBatchModel batchModel) {
		populatePBControllerFromPOJO(this, batchModel);
		this.setProtectionCode("NONE");
		regInfo.addObserver(this);
	}

	public void dispose() throws Throwable {
		finalize();
	}

	protected void finalize() throws Throwable {
		type = null;
		analyticalPurityList.clear();
		analyticalPurityList = null;
		regInfo.dispose();
		super.finalize();
	}

	/**
	 * Type is the indication of INTENDED, ACTUAL, REAGENT, etc.
	 * 
	 */
	public BatchType getType() {
		return type;
	}

	public void setType(BatchType newType) {
		type = newType;
		setModified(true);
	}

	public int getIntendedBatchAdditionOrder() {
		return intendedBatchAdditionOrder;
	}

	public void setIntendedBatchAdditionOrder(int additionOrder) {
		intendedBatchAdditionOrder = additionOrder;
		setModified(true);
	}

	public boolean getProductFlag() {
		return productFlag;
	}

	public void setProductFlag(boolean newValue) {
		productFlag = newValue;
		setModified(true);
	}

	public List getAnalyticalPurityList() {
		return analyticalPurityList;
	}

	public void setAnalyticalPurityList(List list) {
		analyticalPurityList.clear();
		analyticalPurityList.addAll(list);
		setModified(true);
	}

	public boolean hasPurity(Purity tstPurity) {
		return analyticalPurityList.contains(tstPurity);
	}

	/**
	 * Should be a percentage between 0.0 and 1.0
	 * 
	 * @param purity
	 *            The purity to set.
	 */
	public void addPurity(Purity purity) {
		analyticalPurityList.add(purity);
		setModified(true);
	}

	/**
	 * Concatinate the list of purity objects to the current list of purities.
	 * 
	 * @param purities -
	 *            a list of purity objects
	 */
	public void addPurity(List purities) {
		analyticalPurityList.addAll(purities);
		setModified(true);
	}

	public void setAnalyticalPurityList(ArrayList aPurityList) {
		analyticalPurityList = aPurityList;
		setModified(true);
	}

	public String getRegStatus() {
		return (regInfo == null) ? BatchRegistrationInfo.NOT_REGISTERED : regInfo.getRegistrationStatus();
	}

	public BatchRegistrationInfo getRegInfo() {
		return regInfo;
	}

	public void setRegInfo(BatchRegistrationInfo batchRegInfo) {
		regInfo.deepCopy(batchRegInfo);
		setModified(true);
	}

	/**
	 * If molecular weight amount isCalculated() or the batch molWgt is not set, the value will be the compound molWt and the salt
	 * code's molWt * the number of Salt Equivs. If isCalculated() is false, then the value stored with the batch will be the value
	 * returned by this method.
	 * 
	 * ShortCut for getMolecularWeightAmount().getValue();
	 * 
	 * @return the calculated molWgt if MolecularWeightAmount.isCalculated() == true.
	 */
	public double getMolWgt() {
		if (getMolecularWeightAmount().isCalculated())
			setMolWgtCalculated(getMolWgtCalculated());
		return getMolecularWeightAmount().doubleValue();
	}

	/**
	 * 
	 * @return null if no IntendedProduct is there to match up. Otherwise it returns weightAmount of IntendedProduct
	 */
	public Amount getTheoreticalWeightAmount() {
		Amount result = null;
		if (type == BatchType.INTENDED_PRODUCT && theoreticalWeightAmount.isCalculated())
			result = getWeightAmount();
		else
			result = theoreticalWeightAmount;
		return result;
	}

	/**
	 * This should only be called when loading the batch. Otherwise, the yieldPercent is calculated to keep it up-to-date with the
	 * intendedBatch.
	 * 
	 * @param yieldPercent
	 */
	public void setTheoreticalWeightAmount(Amount weightAmount) {
		if (!theoreticalWeightAmount.equals(weightAmount)) {
	    	boolean oldCalcFlag = theoreticalMoleAmount.isCalculated();
			theoreticalWeightAmount.deepCopy(weightAmount);
	        theoreticalMoleAmount.setCalculated(oldCalcFlag);
			setModified(true);
		}
	}

	/**
	 * 
	 * @return null if no IntendedProduct is there to match up. Otherwise it returns moleAmount of IntendedProduct
	 */
	public Amount getTheoreticalMoleAmount() {
		Amount result = null;
		if (type == BatchType.INTENDED_PRODUCT && theoreticalMoleAmount.isCalculated())
			result = getMoleAmount();
		else
			result = theoreticalMoleAmount;
		return result;
	}

	/**
	 * This should only be called when loading the batch. Otherwise, the yieldPercent is calculated to keep it up-to-date with the
	 * intendedBatch.
	 * 
	 * @param yieldPercent
	 */
	public void setTheoreticalMoleAmount(Amount moleAmount) {
		if (!theoreticalMoleAmount.equals(moleAmount)) {
	    	boolean oldCalcFlag = theoreticalMoleAmount.isCalculated();
			theoreticalMoleAmount.deepCopy(moleAmount);
	        theoreticalMoleAmount.setCalculated(oldCalcFlag);
			updateTheoreticalWeightAmount();
			setModified(true);
		}
	}

	/**
	 * Uses weights to find yeild. Is always calculated.
	 * 
	 * @return -1.0 if no IntendedProduct is associated with this batch. -1.0 if IntendedProduct's moleAmount.GetValueInStdUnits() ==
	 *         0.0. 100.0 - 0.0 value if there is an IntendedProduct to match.
	 */
	public Amount getTheoreticalYieldPercentAmount() {
		return theoreticalYieldPercentAmount;
	}

	/**
	 * This should only be called when loading the batch. Otherwise, the yieldPercent is calculated to keep it up-to-date with the
	 * intendedBatch.
	 * 
	 * @param yieldPercent
	 */
	public void setTheoreticalYieldPercentAmount(Amount yieldPercent) {
		if (!theoreticalYieldPercentAmount.equals(yieldPercent)) {
			theoreticalYieldPercentAmount.deepCopy(yieldPercent);
			setModified(true);
		}
	}

	/**
	 * Uses weight to calculate the yeilds
	 * 
	 * @deprecated - v 1.0.0 Because of sig fig update. Used only for backward compatibility
	 * 
	 * @return -1.0 if no IntendedProduct is associated with this batch. -1.0 if IntendedProduct's moleAmount.GetValueInStdUnits() ==
	 *         0.0. 100.0 - 0.0 value if there is an IntendedProduct to match.
	 */
	public double getTheoreticalYieldPercent() {
		return CeNNumberUtils.round(theoreticalYieldPercent, 2);
	}

	/**
	 * Uses weight to calculate the yeilds
	 * 
	 * @deprecated - v 1.0.0 Because of sig fig update. Used only for backward compatibility
	 * 
	 * This should only be called when loading the batch. Otherwise, the yieldPercent is calculated to keep it up-to-date with the
	 * intendedBatch.
	 * 
	 * @param yieldPercent
	 */
	public void setTheoreticalYieldPercent(double yieldPercent) {
		if (theoreticalYieldPercent != yieldPercent) {
			theoreticalYieldPercent = yieldPercent;
			setModified(true);
		}
	}

	private void updateTheoreticalWeightAmount() {
		if (theoreticalWeightAmount.isCalculated() && getMolWgt() > 0.0) {
			theoreticalWeightAmount.setUnit(getWeightAmount().getUnit());
			ArrayList amts = new ArrayList();
			amts.add(getTheoreticalMoleAmount());
			amts.add(getMolecularWeightAmount());
            theoreticalWeightAmount.setSigDigits(CeNNumberUtils.getSmallestSigFigsFromList(amts));
//			applySigFigRules(getTheoreticalWeightAmount(), amts);
			amts.clear();

			theoreticalWeightAmount.SetValueInStdUnits(theoreticalMoleAmount.GetValueInStdUnitsAsDouble() * getMolWgt());
		}
	}

	public double getMolWgtCalculated() {
		double result = super.getMolWgtCalculated();
		// Open question of whether or not to run sig figs here as volume would be a limiting measured quantity.
		// limiting means sig fig limiting as volume can only be measured to 3 or 4 places at most when using mL amounts.
		// most commonly the volume will be to 2 sig figs causing problems with calcs of other areas because sig fig rules
		// would always apply.
		if (regInfo != null && regInfo.getResidualSolventList() != null && regInfo.getResidualSolventList().size() > 0) {
			Iterator<BatchResidualSolventModel> it = regInfo.getResidualSolventList().iterator();
			while (it.hasNext()) {
				BatchResidualSolventModel solv = (BatchResidualSolventModel) it.next();
				result += solv.getResidualMolWgt();
			}
		}

		return result;
	}

	public String getMolFormula() {
		String result = "" + super.getMolFormula();

		if (regInfo != null && regInfo.getResidualSolventList() != null && regInfo.getResidualSolventList().size() > 0) {
			Iterator<BatchResidualSolventModel> it = regInfo.getResidualSolventList().iterator();
			while (it.hasNext()) {
				BatchResidualSolventModel solv = (BatchResidualSolventModel) it.next();
				String val = solv.getResidualMolFormula();
				if (val != null && val.length() > 0)
					result += "; " + val;
			}
		}

		return result;
	}

	/**
	 * Policy based on registration status of the Batch. Please use the StoiciometryModel to determine if intended batches are
	 * editable or not. This method is intended only to be used by Actual Batch types.
	 * 
	 * DataModel will only inform of the policy. Not enforce. It is up to the implementor to decide what is best.
	 * 
	 * @return true if batch can be altered. false otherwise
	 */
	public boolean isEditable() {
		// Current policy is that after registration no values in AbstractBatch
		// should be editable.
		boolean result = (getType() != BatchType.ACTUAL_PRODUCT);
		if (!result)
			result = (regInfo != null && regInfo.allowBatchEdits());
		return result;
	}

	/**
	 * Triggers a recalculation of amounts based on last valid entry.
	 * 
	 * For product batches this also recalculates TheoreticalYieldPercent
	 */
	public void recalcAmounts() {
		// TODO: needs to be tested: Added isEditable() to prevent updates unnecessarily.
		// The test needs to be such that there were items that used to be calculated
		// on the fly.
		if (isEditable() && autoCalcOn && !inCalculation && !isLoading()) {
			super.recalcAmounts();
			// Need to reestablish Yield if things changed
			inCalculation = true;
			updateTheoreticalWeightAmount();
			if (theoreticalWeightAmount.doubleValue() > 0.0) {
				double yield = 100 * (getWeightAmount().GetValueInStdUnitsAsDouble() / theoreticalWeightAmount.GetValueInStdUnitsAsDouble());
				ArrayList amts = new ArrayList();
				amts.add(getWeightAmount());
				amts.add(getTheoreticalWeightAmount());
                getTheoreticalYieldPercentAmount().setSigDigits(CeNNumberUtils.getSmallestSigFigsFromList(amts));
//				applySigFigRules(getTheoreticalYieldPercentAmount(), amts);
				amts.clear();
				theoreticalYieldPercentAmount.setValue(yield);
			} else if (theoreticalWeightAmount.doubleValue() == 0.0) {
				// If the theoreticalWeightAmount is 0 then yeild % is 0
				theoreticalYieldPercentAmount.reset();
			}
			inCalculation = false;
		}
	}

	/**
	 * Amounts that are possibly being loaded from external sources.
	 * 
	 * @return
	 */
	protected List getLoadedAmounts() {
		ArrayList result = new ArrayList();
		result.add(getMolecularWeightAmount()); // calc'd. Don't play with sig digits.
		result.add(getLoadingAmount()); // treat as user set sig digits
		result.add(getDensityAmount()); // treat as user set sig digits
		result.add(getMolarAmount()); // treat as user set sig digits
		return result;
	}

	/**
	 * Amounts that are all interrelated regarding batch calculations. SigDigits apply.
	 * 
	 * @return
	 */
	protected List getCalculatedAmounts() {
		ArrayList result = new ArrayList();
		result.add(getTheoreticalMoleAmount()); // Set by limiting reagent or by user.
		result.add(getTheoreticalWeightAmount()); // Set by limiting reagent.
		result.add(getTheoreticalYieldPercentAmount()); // Calulated from weight amounts Theo and Actual
		result.add(getMoleAmount()); // Set by limiting if intended type or if actual is calculated from weight.
		result.add(getWeightAmount()); // Set by user or calc'd from moles.
		result.add(getVolumeAmount()); // Calculated in registration/submission based on solvent used to dilute sample.
		result.add(getPurityAmount()); // no single purity amount available as of 1/17/2006 for intended or actual products
		result.add(getRxnEquivsAmount()); // Use this to set initial sigdigits = 3 by setting to 1.00 as default.
		return result;
	}

	/**
	 * Adds object that implements BatchRegistrationListener interface to the list of Objects to be notified when registrationStatus
	 * changes.
	 * 
	 * @param o -
	 *            object implementing BatchRegistrationListener interface
	 */
	public void addRegistrationListener(BatchRegistrationListener o) {
		if (regInfo != null)
			regInfo.addRegistrationListener(o);
	}

	/**
	 * Removes object if it exists in the list of listeners.
	 * 
	 * @param o
	 */
	public void deleteRegistrationListener(BatchRegistrationListener o) {
		if (regInfo != null)
			regInfo.deleteRegistrationListener(o);
	}

	public int compareTo(Object o) {
		int result = 0;
		if (o != null) {
			result = hashCode() - o.hashCode();
			if (o instanceof ProductBatch) {
				ProductBatch pb = (ProductBatch) o;
				if (type.equals(BatchType.INTENDED_PRODUCT)) {
					result = intendedBatchAdditionOrder - pb.getIntendedBatchAdditionOrder();
					// Precedence should be batchNumber if Product otherwise Compound Number
				} else {
					if (getBatchNumber() != null && pb.getBatchNumber() != null)
						result = (getBatchNumber().compareTo(pb.getBatchNumber()));
					else if (batchModel.getCompound().getRegNumber() != null && pb.batchModel.getCompound().getRegNumber() != null)
						result = (batchModel.getCompound().getRegNumber().compareTo(pb.batchModel.getCompound().getRegNumber()));
				}
			}
		}
		return result;
	}

	/**
	 * Copies target batch's information over to the this batch. Compound information is referenced and not copied. Convenience
	 * method for copy(AbstractBatch, boolean).
	 * 
	 * @param target -
	 *            batch to copy informtion to.
	 */
	public void deepCopy(Object source) {
		if (source != null && source instanceof ProductBatch)
			deepCopy(source, false);
	}

	/**
	 * Copies target batch's information over to the this batch. If the Compound object's information is to be copied and not just
	 * referenced, pass a true to deepCopy.
	 * 
	 * Also used as the default implementation of deepClone where the target is the object to be cloned.
	 * 
	 * @param target -
	 *            batch to copy informtion to.
	 * @param deepCopy -
	 *            true if the Compound object is to be cloned or false if Compound is to be referenced
	 */
	public void deepCopy(Object source, boolean deepCopy) {
		if (source != null && source instanceof ProductBatch && !((ProductBatch) source).isDeleted()) {
			super.deepCopy(source, deepCopy);
			ProductBatch sourceInstance = (ProductBatch) source;
			type = sourceInstance.type;
			regInfo.deepCopy(sourceInstance.regInfo);
			theoreticalMoleAmount.deepCopy(sourceInstance.theoreticalMoleAmount);
			theoreticalWeightAmount.deepCopy(sourceInstance.theoreticalWeightAmount);
			theoreticalYieldPercent = sourceInstance.theoreticalYieldPercent;
		    theoreticalYieldPercentAmount.deepCopy(sourceInstance.theoreticalYieldPercentAmount);
			intendedBatchAdditionOrder = sourceInstance.intendedBatchAdditionOrder;
		    if (type == BatchType.ACTUAL_PRODUCT) { 
		    	getWeightAmount().setCalculated(false);
		    	setLastUpdatedType(UPDATE_TYPE_WEIGHT);
		    }
		    productFlag = sourceInstance.productFlag;
		    parentKey = sourceInstance.parentKey;

			// copy analytical list
			removeAllObservablesFromList(analyticalPurityList);
			for (Iterator i = analyticalPurityList.iterator(); i.hasNext();) {
				analyticalPurityList.add(((Purity) i.next()).deepClone());
			}
			addAllObservablesToList(sourceInstance.analyticalPurityList, analyticalPurityList);
		}
	}

	/**
	 * Produces an object just like this one with a reference to the Compound object
	 */
	public Object deepClone() {
		return cloneThis(true);
	}

	/**
	 * Produces an object just like this one with no ties to the original object. All values are independent.
	 */
	public Object clone() {
		return cloneThis(false);
	}

	private ProductBatch cloneThis(boolean deepCloneCompound) {
		ProductBatch target = null;
		if (!isDeleted()) {
			target = new ProductBatch();
			target.deepCopy(this, deepCloneCompound);
		}
		return target;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	/**
	 * @return the selectivityStatus
	 */
	public int getSelectivityStatus() {
		return selectivityStatus;
	}

	/**
	 * @param selectivityStatus
	 *            the selectivityStatus to set
	 */
	public void setSelectivityStatus(int selectivityStatus) {
		this.selectivityStatus = selectivityStatus;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public int getEnumerationSequence() {
		return enumerationSequence;
	}

	public void setEnumerationSequence(int enumerationSequence) {
		this.enumerationSequence = enumerationSequence;
	}

	public String getSynthesisPlanProductId() {
		return synthesisPlanProductId;
	}

	public void setSynthesisPlanProductId(String synthesisPlanProductId) {
		this.synthesisPlanProductId = synthesisPlanProductId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getVirtualCompoundId() {
		return virtualCompoundId;
	}

	public void setVirtualCompoundId(String virtualCompoundId) {
		this.virtualCompoundId = virtualCompoundId;
	}

	/** trying to safe singleton with new POJO model */
	private void populatePBControllerFromPOJO(ProductBatch batchController, ProductBatchModel batchModel) {
		batchController.setBatchModel(batchModel);
		AmountModel amtMod = new AmountModel(UnitType.SCALAR);
		amtMod.setValue(amtMod.getValue());// -1 means not set.
		/*theoreticalYieldPercentAmount = new Amount(amtMod);
		amtMod = new AmountModel(UnitType.MASS);
		theoreticalWeightAmount = new Amount(amtMod);
		amtMod = new AmountModel(UnitType.MOLES);
		theoreticalMoleAmount = new Amount(amtMod);*/
	}

	/**
	 * @return the batchModel
	 */
	public ProductBatchModel getBatchModel() {
		return batchModel;
	}

	/**
	 * @param batchModel
	 *            the batchModel to set
	 */
	public void setBatchModel(ProductBatchModel batchModel) {
		this.batchModel = batchModel;
	}

	public ParentCompoundModel getParentCompound() {
		return batchModel.getCompound();
	}

	/**
	 * Returns count of instrumentTypes in the analysis list
	 * @param instrumentType
	 * @return
	 */
	public int getInstrumentTypeCount(String instrumentType){
		int count = 0;
		for(int i=0;i<analysisList.size();i++){
			AnalysisModel analysis = (AnalysisModel)analysisList.get(i);
			if(analysis.getInstrumentType().equals(instrumentType)){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @return the analysisList
	 */
	public List getAnalysisList() {
		return analysisList;
	}

	/**
	 * @param analysisList the analysisList to set
	 */
	public void setAnalysisList(List analysisList) {
		this.analysisList = analysisList;
	}

}

