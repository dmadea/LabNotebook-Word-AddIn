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

import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount2;
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class ProductBatchModel extends BatchModel {

	private static final long serialVersionUID = 213824001208174426L;
	
//	private BatchType type = BatchType.INTENDED_PRODUCT;
	
	// theo amounts were added because they are valid search criteria.
	// therefore the data must exist in the database for indexing.
	// The values will be as below unless intendedBatch != null
	// Also the IntProd's weightAmount is ActualProd's theoWeightAmount
	// IntProd's moleAmount is ActualProd's theoMoleAmount
	private final AmountModel theoreticalYieldPercentAmount = new AmountModel(UnitType.SCALAR, "-1.0"); // -1 means not set.
	private AmountModel theoreticalWeightAmount = new AmountModel(UnitType.MASS);
	private AmountModel theoreticalMoleAmount = new AmountModel(UnitType.MOLES);
	
	// analyticalPurityList Holds all purity object which indicate the techniques used to get
	private ArrayList<PurityModel> analyticalPurityList = new ArrayList<PurityModel>(); 
	// purity designation for this batch.
	private BatchRegInfoModel regInfo = new BatchRegInfoModel(this.key); 
//	private int intendedBatchAdditionOrder = 0;

	private String parentKey; // GUID of its parent batch (for ActualProd it
								// will be key to corresponding IntProd)

	// Note: Use RegNumber in Compound object instead
	// private String compoundId;
//	private String productId = "";
	private String synthesisPlanProductId = "";
	private int enumerationSequence;
	private boolean selected;
	private boolean selectedForRegistration = false;
	private String annotation = "";
	private boolean registered;
	private int priority;
	private int occuredInStepNumber;

	private int selectivityStatus = CeNConstants.BATCH_STATUS_PASS;
	private int continueStatus = CeNConstants.BATCH_STATUS_CONTINUE;

	// List of keys of the Monomers involved in generating this product ( A,B,C )
	private List<String> precursorBatchKeys = new ArrayList<String>();

	// List of reagents in the step to make this product. ( ex: P',C )
	private List<String> reactantBatchKeys = new ArrayList<String>();

	// This batch's Analytical Records.These are pointers to
	// AnalysisCacheModel in NotebookPageModel
	private transient List<AnalysisModel> analysisModelList = new ArrayList<AnalysisModel>();
	// Batch level Analytical comment
	private String analyticalComment = "";

	private int lastUpdatedProductType = UPDATE_TYPE_MOLES;

	private AmountModel totalWellWeightAmount = new AmountModel(UnitType.MASS);
	private AmountModel totalTubeWeightAmount = new AmountModel(UnitType.MASS);
	private AmountModel totalWellVolumeAmount = new AmountModel(UnitType.VOLUME);
	private AmountModel totalTubeVolumeAmount = new AmountModel(UnitType.VOLUME);
	private boolean isUserAdded = false;
	
	private String customSingletonPrecursorsString = "";

	public ProductBatchModel() {
		super.setBatchType(BatchType.INTENDED_PRODUCT);
		this.key = GUIDUtil.generateGUID(this);
		regInfo = new BatchRegInfoModel(this.key);
	}

	public ProductBatchModel(String key) {
		this.key = key;
		regInfo = new BatchRegInfoModel(this.key);
	}

	// BatchModel object required by Storage Service as a generic way of loading batches
	public ProductBatchModel(BatchModel batchModel) {
		super(batchModel);
		// regInfo is instantiated after the key is set.
		// this causes an error on the setComment() call in the super class' init.
//		regInfo = new BatchRegInfoModel(this.key);
		
	}

	public List<String> getReactantBatchKeys() {
		return reactantBatchKeys;
	}

	public void setReactantBatchKeys(List<String> reactantBatchKeys) {
		this.reactantBatchKeys = reactantBatchKeys;
	}

	public List<String> getPrecursorBatchKeys() {
		return precursorBatchKeys;
	}

	public void setPrecursorBatchKeys(List<String> precursorBatchKeys) {
		this.precursorBatchKeys = precursorBatchKeys;
	}

	/**
	 * @return the analyticalPurityList
	 */
	public ArrayList<PurityModel> getAnalyticalPurityList() {
		return analyticalPurityList;
	}

	/**
	 * @param analyticalPurityList
	 *            the analyticalPurityList to set
	 */
	public void setAnalyticalPurityList(ArrayList<PurityModel> analyticalPurityList) {
		this.analyticalPurityList = analyticalPurityList;
		// Looping in the list to set the PurityAmount, with AmountValue
		// corresponding to the representativePurity flag set to "true"
		if (analyticalPurityList != null)
		{
			for (int i = 0; i < analyticalPurityList.size(); i++) {
				PurityModel purity = analyticalPurityList.get(i);
				if (purity.isRepresentativePurity()) {
					super.setPurityAmount(purity.getPurityValue());
				}
			}
		}
		this.modelChanged = true;
	}

	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation
	 *            the annotation to set
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
		this.modelChanged = true;
	}

	/**
	 * @return the enumerationSequence
	 */
	public int getEnumerationSequence() {
		return enumerationSequence;
	}

	/**
	 * @param enumerationSequence
	 *            the enumerationSequence to set
	 */
	public void setEnumerationSequence(int enumerationSequence) {
		this.enumerationSequence = enumerationSequence;
		this.modelChanged = true;
	}

	/**
	 * 
	 * @return TransactionOrder maintained by the super class
	 */
	public int getIntendedBatchAdditionOrder() {
		return super.getTransactionOrder();
	}

	/**
	 * @param intendedBatchAdditionOrder
	 *            the intendedBatchAdditionOrder to set
	 */
	public void setIntendedBatchAdditionOrder(int intendedBatchAdditionOrder) {
		super.setTransactionOrder(intendedBatchAdditionOrder);
		this.modelChanged = true;
	}

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
		this.modelChanged = true;
	}

	/**
	 * @return the parentKey
	 */
	public String getParentKey() {
		return parentKey;
	}

	/**
	 * @param parentKey
	 *            the parentKey to set
	 */
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
		this.modelChanged = true;
	}

	/**
	 * @return the synthesisPlanProductId
	 */
	public String getSynthesisPlanProductId() {
		return synthesisPlanProductId;
	}

	/**
	 * @param synthesisPlanProductId
	 *            the synthesisPlanProductId to set
	 */
	public void setSynthesisPlanProductId(String synthesisPlanProductId) {
		this.synthesisPlanProductId = synthesisPlanProductId;
		this.modelChanged = true;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
		this.modelChanged = true;
	}

	/**
	 * @return the productFlag
	 */
	public boolean isProductFlag() {
		return this.regInfo.isProductFlag();
	}

	/**
	 * @param productFlag
	 *            the productFlag to set
	 */
	public void setProductFlag(boolean productFlag) {
		this.regInfo.setProductFlag(productFlag);
		updateTheoreticalWeightAmount();
		this.modelChanged = true;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
//		return productId;
		return getBatchId();
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		setBatchId(productId);
	}

	/**
	 * @return the regInfo
	 */
	public BatchRegInfoModel getRegInfo() {
		return regInfo;
	}

	/**
	 * @param regInfo
	 *            the regInfo to set
	 */
	public void setRegInfo(BatchRegInfoModel regInfo) {
		this.regInfo = regInfo;
		this.modelChanged = true;
	}

	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * @param registered
	 *            the registered to set
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
		this.modelChanged = true;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		this.modelChanged = true;
	}

	/**
	 * Method for GUI "Registration" tab
	 * @param selectedForRegistration
	 */
	public void setSelectedForRegistration(boolean selectedForRegistration) {
		this.selectedForRegistration = selectedForRegistration;
	}
	
	/**
	 * Method for GUI "Registration" tab
	 * @return
	 */
	public boolean isSelectedForRegistration() {
		return selectedForRegistration;
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
		this.modelChanged = true;
	}

	/**
	 * @return the theoreticalMoleAmount
	 */
	public AmountModel getTheoreticalMoleAmount() {
		return theoreticalMoleAmount;
	}

	/**
	 * @param theoreticalMoleAmount
	 *            the theoreticalMoleAmount to set
	 */
	public void setTheoreticalMoleAmount(AmountModel mtheoreticalMoleAmount) {
		if (!theoreticalMoleAmount.equals(mtheoreticalMoleAmount)) {
			boolean oldCalcFlag = theoreticalMoleAmount.isCalculated();
			theoreticalMoleAmount.deepCopy(mtheoreticalMoleAmount);
			theoreticalMoleAmount.setCalculated(oldCalcFlag);
			updateTheoreticalWeightAmount();
			setModified(true);
		}
	}

	/**
	 * @return the theoreticalWeightAmount
	 */
	public AmountModel getTheoreticalWeightAmount() {
		return theoreticalWeightAmount;
	}

	/**
	 * @param theoreticalWeightAmount
	 *            the theoreticalWeightAmount to set
	 */
	public void setTheoreticalWeightAmount(AmountModel theoreticalWeightAmount) {
		this.theoreticalWeightAmount = theoreticalWeightAmount;
		this.modelChanged = true;
	}

	public int getContinueStatus() {
		return continueStatus;
	}

	public void setContinueStatus(int continueStatus) {
		this.continueStatus = continueStatus;
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(super.toXML());
		xmlbuff.append("<Reactants_For_Product>");
		xmlbuff.append(CommonUtils
				.convertArrayListToXML(this.reactantBatchKeys));
		xmlbuff.append("</Reactants_For_Product>");

		xmlbuff.append("<Analytical_Purity_List>");

		/*
		 * Need to change the nesting according to 1.1 XML
		 * 
		 * <Analytical_Purity_List> <Entry> <Code/> <Description/><PurityValue><Value/></PurityValue><Operator/><Comments/>
		 * </Entry> </Analytical_Purity_List>
		 * 
		 */
		for (int i = 0; i < this.analyticalPurityList.size(); i++) {
			xmlbuff.append("<Entry>");
			PurityModel model = (PurityModel) this.analyticalPurityList.get(i);
			xmlbuff.append(model.toXML());
			xmlbuff.append("</Entry>");
		}
		xmlbuff.append("</Analytical_Purity_List>");
		xmlbuff.append("<Precursors>");
		xmlbuff.append(CommonUtils
				.convertArrayListToXML(this.precursorBatchKeys));
		xmlbuff.append("</Precursors>");
		xmlbuff.append("<Analytical_Comment>" + this.analyticalComment
				+ "</Analytical_Comment>");
		xmlbuff.append("<Screen_Panels>");
		xmlbuff.append(CommonUtils.convertLongArrayToXML(this
				.getScreenPanelIds()));
		xmlbuff.append("</Screen_Panels>");
		xmlbuff.append("<Enumeration_Sequence>");
		xmlbuff.append(this.enumerationSequence);
		xmlbuff.append("</Enumeration_Sequence>");
		xmlbuff.append("</Meta_Data>");
		xmlbuff.append("</Batch_Properties>");
		return xmlbuff.toString();
	}

	public ProductBatchModel deepClone() {
		// ProductBatchModel key should be the same
		ProductBatchModel prodModel = new ProductBatchModel(this.getKey());
		prodModel.deepCopy(this);

		return prodModel;
	}

	public int getAnalyticalRecordCountForInstrument(String instrument) {
		if (this.analysisModelList != null && this.analysisModelList.size() > 0) {
			int count = 0;
			Iterator<AnalysisModel> i_analyticals = this.analysisModelList.iterator();
			while (i_analyticals.hasNext()) {
				AnalysisModel a = (AnalysisModel) i_analyticals.next();
				if (a.getInstrumentType().equalsIgnoreCase(instrument))
					count++;
			}
			return count;
		} else {
			return 0;
		}
	}

	public boolean isAnalysisEmptyForBatch() {
		boolean isEmpty = true;
		if (this.analysisModelList != null && this.analysisModelList.size() > 0) {
			isEmpty = false;
			return isEmpty;
		} else {
			return isEmpty;
		}
	}

	public String[] getDistinctAnalyticalInstrumentList() {
		if (this.analysisModelList != null && this.analysisModelList.size() > 0) {
			Set<String> instrument = new HashSet<String>();
			Iterator<AnalysisModel> i_analyticals = this.analysisModelList.iterator();
			while (i_analyticals.hasNext()) {
				AnalysisModel a = i_analyticals.next();
				instrument.add(a.getInstrumentType());

			}
			return (String[]) instrument.toArray(new String[] {});
		} else {
			return new String[0];
		}
	}

	public List<AnalysisModel> getAnalysisModelList() {
		return analysisModelList;
	}

	public void setAnalysisModelList(List<AnalysisModel> analysisModelList) {
		this.analysisModelList = analysisModelList;
	}

	public String getAnalyticalComment() {
		return analyticalComment;
	}

	public void setAnalyticalComment(String analyticalComment) {
		this.analyticalComment = analyticalComment;
	}

	/**
	 * @return the screenPanelIds
	 */
	public long[] getScreenPanelIds() {

		long compoundAggregationScreenPanelKeys[] = null;
		List<BatchSubmissionToScreenModel> list = this.regInfo.getNewSubmitToBiologistTestList();
		if (list != null && list.size() > 0) {
			int listsize = list.size();
			compoundAggregationScreenPanelKeys = new long[listsize];
			for (int i = 0; i < listsize; i++) {
				BatchSubmissionToScreenModel screenModel = list.get(i);
				if (screenModel != null) {
					compoundAggregationScreenPanelKeys[i] = screenModel
							.getCompoundAggregationScreenPanelKey();
				}
			}
		}
		return compoundAggregationScreenPanelKeys;
	}

	/**
	 * @param screenPanelIds
	 *            the screenPanelIds to set
	 */
	public void addCompoundAggregationScreenPanelKey(long screenPanelId) {

	}

	public void addCompoundAggregationScreenPanelKeys(long[] screenPanelIds) {

	}

	/*
	 * public boolean isUserAddedBatch() { return isUserAdded; BatchType
	 * userAddedBatchType = getUserAddedBatchTye(); BatchType batchType =
	 * getBatchType(); if (batchType.compareTo(userAddedBatchType) == 0) return
	 * true; else return false; }
	 */
	/*
	 * public BatchType getUserAddedBatchTye() { BatchType userAddedBatchType =
	 * null; try { userAddedBatchType =
	 * BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL); } catch
	 * (InvalidBatchTypeException e) { e.printStackTrace(); } return
	 * userAddedBatchType; }
	 */

	public void addPurity(List<PurityModel> originalPurityList) {
		analyticalPurityList.addAll(originalPurityList);
		setModified(true);
	}

	/**
	 * Triggers a recalculation of amounts based on last valid entry.
	 * 
	 * For product batches this also recalculates TheoreticalYieldPercent
	 */
	public void recalcAmounts() {
		// TODO: needs to be tested: Added isEditable() to prevent updates
		// unnecessarily.
		// The test needs to be such that there were items that used to be
		// calculated
		// on the fly.
		if (isEditable() && autoCalcOn && !inCalculation && !isLoadingFromDB()) {
			//As per 1.1 calc and also trigger weight,volume amounts calc
			super.recalcAmounts();
			//Trigger totalXXXAmounts calc. this can calc few values twice.
			recalcTotalAmounts();
			// Need to reestablish Yield if things changed
			inCalculation = true;
			updateTheoreticalWeightAmount();
			if (theoreticalWeightAmount.doubleValue() > 0.0) { // vb 5/9
																// substituted
																// total weight
																// for weight
																// amount
				double yield = 100 * (this.getTotalWeight()
						.GetValueInStdUnitsAsDouble() / theoreticalWeightAmount
						.GetValueInStdUnitsAsDouble());
				ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
				amts.add(getWeightAmount());
				amts.add(getTheoreticalWeightAmount());
				getTheoreticalYieldPercentAmount().setSigDigits(
						CeNNumberUtils
								.getSmallestSigFigsFromAmountModelList(amts));
				// applySigFigRules(getTheoreticalYieldPercentAmount(), amts);
				amts.clear();
				theoreticalYieldPercentAmount.setValue(yield);
			} else if (theoreticalWeightAmount.doubleValue() == 0.0) {
				// If the theoreticalWeightAmount is 0 then yeild % is 0
				theoreticalYieldPercentAmount.reset();
			}
			inCalculation = false;
		}
	}

	private void updateTheoreticalWeightAmount() {
		if (theoreticalWeightAmount.isCalculated() && getMolWgt() > 0.0) {
			theoreticalWeightAmount.setUnit(getWeightAmount().getUnit());
			getTotalWeight().setUnit(getWeightAmount().getUnit());
			
			// if(!getTheoreticalMoleAmount().isCalculated()){
			// //setting the default sigfigs when mmoles for limiting batch is
			// user entered
			// //Fix as a part of artifact [artf26633]
			// theoreticalWeightAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
			// } else {
			ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
			amts.add(getTheoreticalMoleAmount());
			amts.add(getMolecularWeightAmount());
			theoreticalWeightAmount.setSigDigits(CeNNumberUtils
					.getSmallestSigFigsFromAmountModelList(amts));
			// applySigFigRules(getTheoreticalWeightAmount(), amts);
			amts.clear();
			// }
			theoreticalWeightAmount.SetValueInStdUnits(theoreticalMoleAmount
					.GetValueInStdUnitsAsDouble()
					* getMolWgt());
		}
	}

	/**
	 * Policy based on registration status of the Batch. Please use the
	 * StoiciometryModel to determine if intended batches are editable or not.
	 * This method is intended only to be used by Actual Batch types.
	 * 
	 * DataModel will only inform of the policy. Not enforce. It is up to the
	 * implementor to decide what is best.
	 * 
	 * @return true if batch can be altered. false otherwise
	 */
	public boolean isEditable() {
		// Current policy is that after registration no values in AbstractBatch
		// should be editable.
		// boolean result = (getBatchType() != BatchType.ACTUAL_PRODUCT);
		/*
		 * boolean result = (getBatchType() != BatchType.); if (result)
		 */boolean result = (regInfo != null && regInfo.allowBatchEdits());
		return result;
	}

	/**
	 * Amounts that are possibly being loaded from external sources.
	 * 
	 * @return
	 */
	protected List<AmountModel> getLoadedAmounts() {
		ArrayList<AmountModel> result = new ArrayList<AmountModel>();
		result.add(getMolecularWeightAmount()); // calc'd. Don't play with sig
												// digits.
		result.add(getLoadingAmount()); // treat as user set sig digits
		result.add(getDensityAmount()); // treat as user set sig digits
		result.add(getMolarAmount()); // treat as user set sig digits
		return result;
	}

	/**
	 * Amounts that are all interrelated regarding batch calculations. SigDigits
	 * apply.
	 * 
	 * @return
	 */
	protected List<AmountModel> getCalculatedAmounts() {
		ArrayList<AmountModel> result = new ArrayList<AmountModel>();
		result.add(getTheoreticalMoleAmount()); // Set by limiting reagent or by
												// user.
		result.add(getTheoreticalWeightAmount()); // Set by limiting reagent.
		result.add(getTheoreticalYieldPercentAmount()); // Calulated from weight
														// amounts Theo and
														// Actual
		result.add(getMoleAmount()); // Set by limiting if intended type or
										// if actual is calculated from weight.
		result.add(getWeightAmount()); // Set by user or calc'd from moles.
		result.add(getVolumeAmount()); // Calculated in registration/submission
										// based on solvent used to dilute
										// sample.
		result.add(getPurityAmount()); // no single purity amount available as
										// of 1/17/2006 for intended or actual
										// products
		result.add(getRxnEquivsAmount()); // Use this to set initial sigdigits
											// = 3 by setting to 1.00 as
											// default.
		return result;
	}

	/**
	 * Uses weights to find yeild. Is always calculated.
	 * 
	 * @return -1.0 if no IntendedProduct is associated with this batch. -1.0 if
	 *         IntendedProduct's moleAmount.GetValueInStdUnits() == 0.0. 100.0 -
	 *         0.0 value if there is an IntendedProduct to match.
	 */
	public AmountModel getTheoreticalYieldPercentAmount() {
		return theoreticalYieldPercentAmount;
	}

	/**
	 * This should only be called when loading the batch. Otherwise, the
	 * yieldPercent is calculated to keep it up-to-date with the intendedBatch.
	 * 
	 * @param yieldPercent
	 */
	public void setTheoreticalYieldPercentAmount(AmountModel yieldPercent) {
		if (!theoreticalYieldPercentAmount.equals(yieldPercent)) {
			theoreticalYieldPercentAmount.deepCopy(yieldPercent);
			setModified(true);
		}
	}

	/**
	 * @param totalVolumeAmount
	 *            the totalVolumeAmount to set
	 */
	public void setTotalVolumeAmount(AmountModel volume) {
		if (volume != null /*&& volume.GetValueInStdUnitsAsDouble() != 0.0*/) {
			if (volume.getUnitType().getOrdinal() == UnitType.VOLUME
					.getOrdinal()) {
				boolean unitChange = false;
				if (!this.getTotalVolume().equals(volume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(this
							.getTotalVolume(), volume);
					this.getTotalVolume().deepCopy(volume);
					if (!unitChange) {
						lastUpdatedProductType = UPDATE_TYPE_TOTAL_VOLUME;
						// updateCalcFlags(volumeAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					//This will trigger recalcTotalAmounts() as well
					recalcAmounts();
			}
		} else {
			this.getTotalVolume().setValue("0");
		}
	}

	/**
	 * @param totalWeightAmount
	 *            the totalWeightAmount to set
	 */
	public void setTotalWeightAmount(AmountModel weight) {
		if (weight != null /*&& weight.GetValueInStdUnitsAsDouble() != 0.0*/) {
			if (weight.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!weight.equals(this.getTotalWeight())) {
					unitChange = BatchUtils.isUnitOnlyChanged(weight, this
							.getTotalWeight());
					this.getTotalWeight().deepCopy(weight);
					if (!unitChange) {
						lastUpdatedProductType = BatchModel.UPDATE_TYPE_TOTAL_WEIGHT;
						// updateCalcFlags(weightAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					//this includes recalcTotalAmounts();
					recalcAmounts();
			}
		} else {
			this.getTotalWeight().setValue("0");
		}
	}

	public AmountModel getTotalMolarAmount() {
		return super.getTotalMolarity();
	}

	public void setTotalMolarAmount(AmountModel totalMolarAmount) {
		if (totalMolarAmount != null
				&& totalMolarAmount.GetValueInStdUnitsAsDouble() != 0.0) {
			if (totalMolarAmount.getUnitType().getOrdinal() == UnitType.MOLAR
					.getOrdinal()) {
				// Check to see if it is a unit change
				if (getTotalMolarity() != null
						&& !getTotalMolarity().equals(totalMolarAmount)) {
					boolean unitChange = BatchUtils.isUnitOnlyChanged(
							getTotalMolarity(), totalMolarAmount);
					getTotalMolarity().deepCopy(totalMolarAmount);
					if (!unitChange) {
						lastUpdatedProductType = BatchModel.UPDATE_TYPE_TOTAL_MOLARITY;
						//This includes recalcTotalAmounts() as well
						recalcAmounts();
						setModified(true);
					}
				}
			}
		} else {
			getTotalMolarity().setValue("0");
		}
	}

	// vb 2/2
	public void recalcTotalAmounts() {
		// Make sure we don't get into a loop!
		// And that we don't lose information on load.
		// if (!autoCalcOn || inCalculation || isLoadingFromDB())
		// return;

		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		inCalculation = true;
		// Check which value was set by hand: solid or liquid
		// Molar type as last updated not considered here because moles is
		// considered driver
		// when there is a tie in flags.
		if (lastUpdatedProductType == UPDATE_TYPE_TOTAL_VOLUME) { // && !
																	// this.getTotalVolume().isCalculated())
																	// {
			// We need to update moles and weight from volume
			// Molarity takes precedence over density
			if (this.getMolarAmount().doubleValue() > 0) {
				amts.add(this.getMolarAmount());
				amts.add(this.getTotalVolume());
				applySigFigRules(this.getMoleAmount(), amts);
				amts.clear(); // important to clear the amts list
				// Update mole amount
				// Std unit for molar is mMolar
				//
				// mMoles = (mole/L) * mL
				this.getMoleAmount().SetValueInStdUnits(
						this.getMolarAmount().GetValueInStdUnitsAsDouble()
								* this.getTotalVolume()
										.GetValueInStdUnitsAsDouble(), true);
				getMoleAmount().setCalculated(true);
				updateTotalWeightFromMoles();
			}
			 else if (this.getDensityAmount().doubleValue() > 0) {
					// find governing sig figs
					amts.add(this.getTotalVolume());
					amts.add(this.getDensityAmount());
					applySigFigRules(this.getTotalWeight(), amts);
					amts.clear();// important to clear the amts list
					// mg = (mL * g/mL)/ (1000 mg/g)
					this.getTotalWeight().SetValueInStdUnits(1000 * this.getTotalVolume().GetValueInStdUnitsAsDouble()
							* this.getDensityAmount().GetValueInStdUnitsAsDouble(), true);
					updateMolesFromTotalWeight();
				}
			updateTotalMolarity();
		} else if (lastUpdatedProductType == UPDATE_TYPE_TOTAL_WEIGHT) { // && !
																			// this.getTotalVolume().isCalculated())
																			// {
		// if ((lastUpdatedType != UPDATE_TYPE_TOTAL_WEIGHT && !
		// this.getMoleAmount().isCalculated()) || !
		// this.getRxnEquivsAmount().isCalculated()
		// || this.getMoleAmount().doubleValue() > 0 && lastUpdatedType !=
		// UPDATE_TYPE_TOTAL_WEIGHT) {
		// updateTotalWeightFromMoles();
		// } else {
		// updateMolesFromTotalWeight();
		// }
			updateMolesFromTotalWeight();
			// Now that the solids are straightened out, we can calc the liquid
			if (this.getMolarAmount().doubleValue() > 0) {
				// update volume
				amts.add(this.getMoleAmount());
				amts.add(this.getMolarAmount());
				if (this.getTotalVolume().isCalculated()
						&& this.getTotalWeight().isCalculated())
					this.getTotalVolume().setSigDigits(
							CeNNumberUtils.DEFAULT_SIG_DIGITS);
				else
					applySigFigRules(this.getTotalVolume(), amts);
				amts.clear();// important to clear the amts list
				this.getTotalVolume().SetValueInStdUnits(
						this.getMoleAmount().GetValueInStdUnitsAsDouble()
								/ this.getMolarAmount()
										.GetValueInStdUnitsAsDouble(), true);
				this.getTotalVolume().setCalculated(true);
			}
			// Calculate total Molarity
			updateTotalMolarity();
		} else if (lastUpdatedProductType == UPDATE_TYPE_TOTAL_MOLARITY) {
			updateMolesFromTotalWeight();
			// Now that the solids are straightened out, we can calc the liquid
			if (this.getTotalMolarAmount().doubleValue() > 0) {
				// update volume
				amts.add(this.getMoleAmount());
				amts.add(this.getTotalMolarAmount());
				if (this.getTotalVolume().isCalculated()
						&& this.getTotalWeight().isCalculated())
					this.getTotalVolume().setSigDigits(
							CeNNumberUtils.DEFAULT_SIG_DIGITS);
				else
					applySigFigRules(this.getTotalVolume(), amts);
				amts.clear();// important to clear the amts list
				this.getTotalVolume().SetValueInStdUnits(
						this.getMoleAmount().GetValueInStdUnitsAsDouble()
								/ this.getTotalMolarAmount()
										.GetValueInStdUnitsAsDouble(), true);
				this.getTotalVolume().setCalculated(true);
			}
			 else if (this.getDensityAmount().doubleValue() > 0) {
					amts.add(this.getTotalWeight());
					amts.add(this.getDensityAmount());
					applySigFigRules(getTotalVolume(), amts);
					amts.clear();// important to clear the amts list

					// update volume from weight value: mg /(1000mg/g)* (g/mL) = mL
					getTotalVolume().SetValueInStdUnits(this.getTotalWeight().GetValueInStdUnitsAsDouble()
							/ (1000 * this.getDensityAmount().GetValueInStdUnitsAsDouble()), true);
				}
		}
		inCalculation = false;
	}

	private void updateTotalMolarity() {
		if (getMolarAmount().doubleValue() == 0.0
				&& getMoleAmount().doubleValue() > 0.0
				&& getTotalVolume().doubleValue() > 0.0) {
			double result = getMoleAmount().GetValueInStdUnitsAsDouble()
					/ getTotalVolume().GetValueInStdUnitsAsDouble();
			this.getTotalMolarAmount().SetValueInStdUnits(result, true);
			this.getTotalMolarAmount().setCalculated(true);
		}
	}

	/**
	 * Applies SigFigs for the Amount Obeject. Checks if the standard sigfig
	 * rules to be applied, which checks for any user edits on Measured Amounts
	 * (Weight,Volume). else any other edit would lead to Default SigFig
	 * application on the Amount.
	 * 
	 * @param amt
	 * @param amts
	 */
	public void applySigFigRules(Amount2 amt, List<AmountModel> amts) {
		if (shouldApplySigFigRules()) {
			amt.setSigDigits(CeNNumberUtils
					.getSmallestSigFigsFromAmountModelList(amts));
		} else {
			if (shouldApplyDefaultSigFigs())
				amt.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		}
	}

	/**
	 * Do we apply sig fig rules to calculations?
	 * 
	 */
	public boolean shouldApplySigFigRules() {
		return (!getTotalWeight().isCalculated() || !getTotalVolume()
				.isCalculated());
	}

	/**
	 * 
	 */
	public boolean shouldApplyDefaultSigFigs() {
		return (!this.getRxnEquivsAmount().isCalculated()
				|| !this.getMoleAmount().isCalculated() || !this
				.getTotalMolarAmount().isCalculated());
	}

	private void updateTotalWeightFromMoles() {
		this.getTotalWeight().setCalculated(true);
		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		// weight = weight/mole * moles
		// In this case moles are always mMoles and the default return of mole
		// amount is in mmoles
		// Hence when we setValue for weight we are doing so in mg and no
		// conversion is necessary.
		double result = getMolWgt()
				* getMoleAmount().GetValueInStdUnitsAsDouble();
		if (getPurityAmount().doubleValue() < 100d
				&& getPurityAmount().doubleValue() > 0.0) {
			result = result / (getPurityAmount().doubleValue() / 100);
			amts.add(this.getMoleAmount());
			amts.add(this.getPurityAmount());
			amts.add(this.getMolecularWeightAmount());
		} else {
			amts.add(this.getMoleAmount());
			amts.add(this.getMolecularWeightAmount());
		}
		// Applies SignificantFigures to weightAmount
		applySigFigRules(this.getTotalWeight(), amts);
		amts.clear();

		// We just got the result for mg * purity),
		// Now we need to make sure the answer makes sense for the units of
		// weight currently being used.
		this.getTotalWeight().SetValueInStdUnits(result, true);
	}

	private void updateMolesFromTotalWeight() {
		double result = 0d;
		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		// weight std unit = mg; density std unit = mg/mmol
		// Weight = 9.82m/s^2 * mass
		if (getMolWgt() > 0.0) {
			// TODO: Warning. Using getMolWgt() in the future may not return
			// standard units. Make sure this changes if the
			// users ever get the opportunity to change units of
			// MolWt.
			result = this.getTotalWeight().GetValueInStdUnitsAsDouble()
					/ getMolWgt();
			amts.add(this.getTotalWeight());
			amts.add(this.getMolecularWeightAmount());
			// we just got the result for mg/mmole,
			// now we need to make sure the answer makes sense for the units of
			// weight currently being used.
			if (getPurityAmount().doubleValue() < 100d
					&& this.getPurityAmount().doubleValue() > 0.0) {
				result = result * (this.getPurityAmount().doubleValue() / 100);
				amts.add(this.getPurityAmount());
			}
		}
		applySigFigRules(this.getMoleAmount(), amts);
		Unit2 unit = this.getMoleAmount().getUnit();
		amts.clear();
		this.getMoleAmount().SetValueInStdUnits(result, true);
		this.getMoleAmount().setUnit(unit);
		this.getMoleAmount().setCalculated(true);
	}

	/**
	 * @return the totalPlateAmount
	 */
	public AmountModel getTotalWellWeightAmount() {
		return totalWellWeightAmount;
	}

	/**
	 * @param totalPlateAmount
	 *            the totalPlateAmount to set
	 */
	public void setTotalWellWeightAmount(AmountModel vtotalWellWeight) {
		if (vtotalWellWeight != null) {
			if (vtotalWellWeight.getUnitType().getOrdinal() == UnitType.MASS
					.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalWellWeightAmount.equals(vtotalWellWeight)) {
					unitChange = BatchUtils.isUnitOnlyChanged(
							totalWellWeightAmount, vtotalWellWeight);
					totalWellWeightAmount.deepCopy(vtotalWellWeight);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			totalWellWeightAmount.setValue("0");
		}
	}

	/**
	 * @return the totalTubeWeightAmount
	 */
	public AmountModel getTotalTubeWeightAmount() {
		return totalTubeWeightAmount;
	}

	/**
	 * @param totalTubeWeightAmount
	 *            the totalTubeWeightAmount to set
	 */
	public void setTotalTubeWeightAmount(AmountModel vtotalTubeWeight) {
		if (vtotalTubeWeight != null) {
			if (vtotalTubeWeight.getUnitType().getOrdinal() == UnitType.MASS
					.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalTubeWeightAmount.equals(vtotalTubeWeight)) {
					unitChange = BatchUtils.isUnitOnlyChanged(
							totalTubeWeightAmount, vtotalTubeWeight);
					totalTubeWeightAmount.deepCopy(vtotalTubeWeight);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			totalTubeWeightAmount.setValue("0");
		}
	}

	public AmountModel getTotalTubeVolumeAmount() {
		return totalTubeVolumeAmount;
	}

	public void setTotalTubeVolumeAmount(AmountModel vtotalTubeVolume) {
		if (vtotalTubeVolume != null) {
			if (vtotalTubeVolume.getUnitType().getOrdinal() == UnitType.VOLUME
					.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalTubeVolumeAmount.equals(vtotalTubeVolume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(
							totalTubeVolumeAmount, vtotalTubeVolume);
					totalTubeVolumeAmount.deepCopy(vtotalTubeVolume);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			totalTubeVolumeAmount.setValue("0");
		}
	}

	public AmountModel getTotalWellVolumeAmount() {
		return totalWellVolumeAmount;
	}

	public void setTotalWellVolumeAmount(AmountModel vtotalWellVolume) {
		if (vtotalWellVolume != null) {
			if (vtotalWellVolume.getUnitType().getOrdinal() == UnitType.VOLUME
					.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!totalWellVolumeAmount.equals(vtotalWellVolume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(
							totalWellVolumeAmount, vtotalWellVolume);
					totalWellVolumeAmount.deepCopy(vtotalWellVolume);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			totalWellVolumeAmount.setValue("0");
		}
	}

	// This method will not copy the source amount key to the target amount.
	public void setTheoreticalMoleAmountFromStoicAmount(
			AmountModel mtheoreticalMoleAmount) {
		if (!theoreticalMoleAmount.equals(mtheoreticalMoleAmount)) {
			boolean oldCalcFlag = theoreticalMoleAmount.isCalculated();
			theoreticalMoleAmount.deepCopyWithoutKeys(mtheoreticalMoleAmount);
			theoreticalMoleAmount.setCalculated(oldCalcFlag);
			updateTheoreticalWeightAmount();
			setModified(true);
		}

	}

	// This method will not copy the source amount key to the target amount.
	public void setTheoreticalWeightAmountFromStoicAmount(
			AmountModel mtheoreticalWeightAmount) {
		if (!theoreticalWeightAmount.equals(mtheoreticalWeightAmount)) {
			this.theoreticalWeightAmount
					.deepCopyWithoutKeys(mtheoreticalWeightAmount);
			this.modelChanged = true;
		}

	}

	public void setUserAdded(boolean isUserAdded) {
		this.isUserAdded = isUserAdded;
	}

	public boolean isUserAdded() {
		return isUserAdded;
	}
	
	public boolean equals(Object o) {
		boolean result = false;
		if (o != null && o instanceof ProductBatchModel) {
			ProductBatchModel ab = (ProductBatchModel) o;
			if (this.getKey().equals(ab.getKey()))
				result = true;
		}
		return result;
	}

	public String getHandlingComments() {
		return regInfo.getHandlingComments();
	}

	public void setHandlingComments(String handlingComments) {
		if (handlingComments == null)
			return;
		regInfo.setHandlingComments(handlingComments);
		this.modelChanged = true;
	}

	public String getHazardComments() {
		return regInfo == null ? "" : regInfo.getHazardComments();
	}

	public void setHazardComments(String hazardComments) {
		if (hazardComments == null || regInfo == null)  // allow hazard comments to be cleared
			return;
		regInfo.setHazardComments(hazardComments);
		this.modelChanged = true;
	}
	
	public String getStorageComments() {
		return regInfo == null ? "" : regInfo.getStorageComments();
	}

	public void setStorageComments(String storageComments) {
		if (storageComments == null || regInfo == null)
			return;
		this.regInfo.setStorageComments(storageComments);
		this.modelChanged = true;
	}
	
	/**
	 * @param meltPointRange
	 *            the meltPointRange to set
	 */
	public void setMeltPointRange(TemperatureRangeModel meltPointRange) {
		regInfo.setMeltPointRange(meltPointRange);
		this.modelChanged = true;
	}
	
	public TemperatureRangeModel getMeltPointRange() {
		return regInfo.getMeltPointRange();
	}
	
	public String getCompoundState() {
		return regInfo.getCompoundState();
	}

	public void setCompoundState(String compoundState) {
		if (compoundState == null)
			return;
		regInfo.setCompoundState(compoundState);
		this.modelChanged = true;
	}
	
	public String getProtectionCode() {
		return regInfo.getProtectionCode();
	}

	public void setProtectionCode(String protectionCode) {
		if (protectionCode == null)
			return;
		regInfo.setProtectionCode(protectionCode);
		this.modelChanged = true;
	}
	
	public String getRegStatus() {
		return regInfo.getRegStatus();
	}
	
	public String getStatus(){
		return regInfo.getStatus();
	}

	public void setRegStatus(String regStatus) {
		if (regStatus == null)
			return;
		regInfo.setRegStatus( regStatus);
		this.modelChanged = true;
	}
	
	public ExternalSupplierModel getVendorInfo() {
		return regInfo.getVendorInfo();
	}

	public void setVendorInfo(ExternalSupplierModel vendorInfo) {
		if (vendorInfo == null)
			vendorInfo = new ExternalSupplierModel();
		regInfo.setVendorInfo(vendorInfo);
		this.modelChanged = true;
	}
	
	
	public String getOwner() {
		return this.regInfo.getOwner();
	}

	public void setOwner(String owner) {
		if (owner == null)
			return;
		this.regInfo.setOwner(owner);
		
	}
	
	/**
	 * Tricky!  If initialized with ProductBatchModel(batchModel) this may be set
	 * to the super.comments attribute, but we will never be able to get at these comments directly
	 * because the regInfo object will be created just after the initialization of the batch.
	 */
	public String getComments() {
		return (regInfo == null ? super.getComments() : regInfo.getComments());
	}

	/**
	 * @param String comments = comments to be put into the RegInfo Object
	 */
	public void setComments(String comments) {
		if (comments != null) { 
			if (regInfo == null) {
				super.setComments(comments);
			} else {
				this.regInfo.setComments(comments);
			}
		}
	}

	/**
	 * Keep signature the same to override the BatchModel version of deepCopy
	 */
	public void deepCopy(BatchModel srcBM) {
		try {
			this.setBeingCloned(true);
			ProductBatchModel src = (ProductBatchModel) srcBM;
			if (this.key == src.getKey())//This is called as part of deepClone().
			{
				setCompound((ParentCompoundModel) src.getCompound().deepClone());
				setListKey(src.getListKey());
				setRegInfo(src.getRegInfo()); // vb 6/29
			}
			else
			{
				ParentCompoundModel compModel = new ParentCompoundModel();
				compModel.deepCopy(src.getCompound());
				setCompound(compModel);
				BatchRegInfoModel batchRegInfoModel = new BatchRegInfoModel(this.getKey());
				batchRegInfoModel.deepCopy(src.getRegInfo());
				setRegInfo(batchRegInfoModel);
			}
			setBatchNumber(src.getBatchNumber());
			setBatchType(src.getBatchType());
			setChloracnegenFlag(src.isChloracnegenFlag());
			setChloracnegenType(src.getChloracnegenType());
			setComments(src.getComments());
			setConversationalBatchNumber(src
					.getConversationalBatchNumber());
			setDensityAmount(src.getDensityAmount());
			setIntermediate(src.isIntermediate());
			setLimiting(src.isLimiting());
			
			setLoadingAmount(src.getLoadingAmount());
			setMolarAmount(src.getMolarAmount());
			setMoleAmount(src.getMoleAmount());
			setMolecularFormula(src.getMolecularFormula());
			setMolecularWeightAmount(src.getMolecularWeightAmount());
			setOwner(src.getOwner());
			setParentBatchNumber(src.getParentBatchNumber());
			setPosition(src.getPosition());
			setProjectTrackingCode(src.getProjectTrackingCode());
			setPurityAmount(src.getPurityAmount());
			setRxnEquivsAmount(src.getRxnEquivsAmount());
			setPrecursorBatchKeys(src.precursorBatchKeys);
			setReactantBatchKeys(src.reactantBatchKeys);
			setSaltForm(src.getSaltForm());
			setSaltEquivs(src.getSaltEquivs());
			setStepNumber(src.getStepNumber());
			setStoichComments(src.getStoichComments());
			setStoicLabel(src.getStoicLabel());
			setSynthesizedBy(src.getSynthesizedBy());
			setTestedForChloracnegen(src.isTestedForChloracnegen());
			setTransactionOrder(src.getTransactionOrder());
			setVolumeAmount(src.getVolumeAmount());
			setWeightAmount(src.getWeightAmount());
	
			// ProductBatchModel specific props
			setTheoreticalWeightAmount(src.theoreticalWeightAmount);
			setTheoreticalMoleAmount(src.theoreticalMoleAmount);
			setTheoreticalYieldPercentAmount(src.theoreticalYieldPercentAmount);
			setAnalyticalPurityList(src.analyticalPurityList);
			
			setIntendedBatchAdditionOrder(src.getIntendedBatchAdditionOrder());
			setParentKey(src.parentKey);
			//setProductId(src.productId);
			setSynthesisPlanProductId(src.synthesisPlanProductId);
			setEnumerationSequence(src.enumerationSequence);
			setSelected(src.selected);
			setAnnotation(src.annotation);
			setRegistered(src.registered);
			setPriority(src.priority);
			setOccuredInStepNumber(src.occuredInStepNumber);
			setSelectivityStatus(src.selectivityStatus);
			setModelChanged(src.isModelChanged());
			setToDelete(src.isSetToDelete());
			setAnalyticalComment(src.analyticalComment);
			setTotalWellWeightAmount(src.totalWellWeightAmount);
			setTotalTubeWeightAmount(src.totalTubeWeightAmount);
			setTotalWellVolumeAmount(src.totalWellVolumeAmount);
			setTotalTubeVolumeAmount(src.totalTubeVolumeAmount);
			setTotalWeight(src.getTotalWeight());
			setTotalVolume(src.getTotalVolume());
			setTotalMolarity(src.getTotalMolarity());
			setPreviousMolarAmount(src.getPreviousMolarAmount());
			setLoadedFromDB(src.isLoadedFromDB());
			setCustomSingletonPrecursorsString(src.customSingletonPrecursorsString);
		} finally {
			this.setBeingCloned(false);
		}
	}

	public int compareTo(ProductBatchModel o) {
		return super.compareTo(o);
	}
	
	public boolean isActualBatch() {
		return BatchType.ACTUAL_PRODUCT.equals(getBatchType());
	}
	public boolean isIntendedBatch() {
		return BatchType.INTENDED_PRODUCT.equals(getBatchType());
	}

	/**
	 * <p>
	 * Checks the compound stereoisomer code for 'HSREG' and whether or not the batchVnVInfoModel has a VNV_PASS status
	 * This is quite different from checking whether the assigned stereoisomer code (SIC) is proper and the status 
	 * of a VnV / UC check is successful. Use the regInfo object's batchVnVInfoModel to check for isPassed() to get 
	 * a validation of the assigned SIC
	 * </p>
	 * @return
	 */
	public boolean isVnVPassedAndStereoisomerCodeValid() {
		boolean comopundSICIsNotHSREG = getCompound() != null && 
										StringUtils.equalsIgnoreCase(BatchVnVInfoModel.SIC_HSREG, 
										                             getCompound().getStereoisomerCode());
		// only looking for VnV Status = BatchVnVInfoModel.VNV_PASS
		boolean vnvStatusIsPassed = regInfo != null && regInfo.isVnVValid();
		return comopundSICIsNotHSREG && vnvStatusIsPassed;
	}

	/**
	 * use NotebookPageModel::getSingletonPrecursorsString() for getting precursors from this batch
	 */
	String getCustomSingletonPrecursorsString() {
		return this.customSingletonPrecursorsString;
	}
	
	/**
	 * use NotebookPageModel::processSingletonPrecursorsUpdate() for setting precursors to this batch
	 */	
	void setCustomSingletonPrecursorsString(String customSingletonPrecursorsString) {
		this.customSingletonPrecursorsString = customSingletonPrecursorsString;
		this.setModelChanged(true);
	}
}