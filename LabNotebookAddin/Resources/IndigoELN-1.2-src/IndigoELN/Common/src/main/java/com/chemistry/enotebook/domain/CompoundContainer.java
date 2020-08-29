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

import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class CompoundContainer<E extends BatchModel> extends CeNAbstractModel {

	
	private static final long serialVersionUID = -5163272218516314588L;
	protected long batchTrackingId =-1;
	protected E batch = null;
	protected AbstractPlate plate;
	protected AmountModel containedWeightAmount = new AmountModel(UnitType.MASS);
	protected AmountModel containedVolumeAmount = new AmountModel(UnitType.VOLUME);
	protected AmountModel containedMolarity = new AmountModel(UnitType.MOLAR);
	protected AmountModel containedMoleAmount = new AmountModel(UnitType.MOLES);
	protected int position; // 1, 2, 3, 4, ....
	protected String number; // A01, A02, A03, ...., B01, B02, .....
	protected String solventCode = null;//"DMSO";//Test code assigned, need to be replaced
	protected String containerType = "";//WELL, TUBE or VIAL
	
	private static final int UPDATE_TYPE_AMOUNT_WEIGHT = 1;  // vb 2/2
	private static final int UPDATE_TYPE_AMOUNT_VOLUME = 2;
	private static final int UPDATE_TYPE_AMOUNT_MOLARITY = 3;	
	private int lastUpdatedProductType = 0;
	protected boolean inCalculation = false; // Do not disturb. Batch is in process of calculating values
	
	protected String compoundManagementContainerGUID;  // GUID from CompoundManagement
	protected String barCode; //2D barcode of well or barcode of Vial or barcode of Tube
	
	protected String containerTypeCode="2DT"; // Vial or Tube type code from COMPOUND_MANAGEMENT_VIAL_CDT
	protected String locationCode="GREMP"; //location from COMPOUND_MANAGEMENT_LOCATION_CDT
	
	private PurificationServiceSubmisionParameters purificationServiceParameter;

	public AbstractPlate getPlate() {
		return plate;
	}

	public void setPlate(AbstractPlate plate) {
		this.plate = plate;
	}
	
	public E getBatch() {
		return batch;
	}

	public void setBatch(E batch) {
		this.batch = batch;
		this.modelChanged = true;
	}

	public long getBatchTrackingId() {
		if (batchTrackingId == -1) {
			E model = getBatch();
			if (model instanceof ProductBatchModel) {
				BatchRegInfoModel regInfo = ((ProductBatchModel)model).getRegInfo();
				if (regInfo != null) {
					batchTrackingId = regInfo.getBatchTrackingId();
				}
			}
		}
		return batchTrackingId;
	}

	public void setBatchTrackingId(long batchTrackingId) {
		this.batchTrackingId = batchTrackingId;
		this.modelChanged = true;
	}

	/**
	 * @return the molarity
	 */
	public AmountModel getContainedMolarity() {
		return containedMolarity;
	}

	/**
	 * @param molarity
	 *            the molarity to set
	 */
	public void setContainedMolarity(AmountModel molarity) {
		if (molarity != null /*&& molarity.GetValueInStdUnitsAsDouble() != 0.0*/) {
			if (molarity.getUnitType().getOrdinal() == UnitType.MOLAR.getOrdinal()) {
				// Check to see if it is a unit change
				if (!containedMolarity.equals(molarity)) {
					boolean unitChange = BatchUtils.isUnitOnlyChanged(containedMolarity, molarity);
					containedMolarity.deepCopy(molarity);
					if (!unitChange) {
						lastUpdatedProductType = UPDATE_TYPE_AMOUNT_MOLARITY;
						recalcContainedAmounts();
						setModified(true);
					}
				}
			}
		} else {
			getContainedMolarity().setValue("0");
		}
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int wellPosition) {
		this.position = wellPosition;
		this.modelChanged = true;
	}


	public PurificationServiceSubmisionParameters getPurificationServiceParameter() {
		return purificationServiceParameter;
	}

	public void setPurificationServiceParameter(PurificationServiceSubmisionParameters purificationServiceParameter) {
		this.purificationServiceParameter = purificationServiceParameter;
		this.modelChanged = true;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String wellNumber) {
		this.number = wellNumber;
		this.modelChanged = true;
	}
	
	/**
	 * @return the solventCode
	 */
	public String getSolventCode() {
		return solventCode;
	}

	/**
	 * @param solventCode
	 *            the solventCode to set
	 */
	public void setSolventCode(String vsolventCode) {
		if(vsolventCode== null)
		{
			if(this.solventCode != null)
			{
			this.modelChanged = true;
			}
			this.solventCode = vsolventCode;	
			
		}
		else if(!vsolventCode.equals(this.solventCode)){
			this.solventCode = vsolventCode;
			this.modelChanged = true;
		}
	}

	public void setContainedWeightAmount(AmountModel containedAmountWeight) {
		if (containedAmountWeight != null && containedAmountWeight.GetValueInStdUnitsAsDouble() != 0.0) {
			if (containedAmountWeight.getUnitType().getOrdinal() == UnitType.MASS.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (! containedAmountWeight.equals(this.containedWeightAmount)) {
					unitChange = BatchUtils.isUnitOnlyChanged(containedAmountWeight, this.containedWeightAmount);
					this.containedWeightAmount.deepCopy(containedAmountWeight);
					if (!unitChange) {
						lastUpdatedProductType = UPDATE_TYPE_AMOUNT_WEIGHT;
						//updateCalcFlags(weightAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcContainedAmounts();
			}
		} else {
			this.containedWeightAmount.setValue("0");
		}
	}
	
	public void recalcContainedAmounts() {
		// Make sure we don't get into a loop!
		// And that we don't lose information on load.
		if ( inCalculation || isLoadingFromDB())
			return;

		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		inCalculation = true;
		// Check which value was set by hand: solid or liquid
		// Molar type as last updated not considered here because moles is considered driver
		// when there is a tie in flags.
		if (lastUpdatedProductType == UPDATE_TYPE_AMOUNT_VOLUME) { //&& ! this.containedVolumeAmount.isCalculated()) {
			// We need to update moles and weight from volume
			// Molarity takes precedence over density
			if (this.getContainedMolarity().doubleValue() > 0) {
				amts.add(this.getContainedMolarity());
				amts.add(this.containedVolumeAmount);
				this.getBatch().applySigFigRules(getContainedMoleAmount(), amts);
				amts.clear(); // important to clear the amts list
				// Update mole amount
				// Std unit for molar is mMolar
				//
				// mMoles = (mole/L) * mL
/*				this.getBatch().getMoleAmount().SetValueInStdUnits(this.getContainedMolarity().GetValueInStdUnitsAsDouble() * 
						this.containedVolumeAmount.GetValueInStdUnitsAsDouble(),	true);
*/				//updateContainedWeightFromMoles();
				this.containedWeightAmount.setCalculated(true);
			} 
			updateContainedMolarity();
		} else if (lastUpdatedProductType == UPDATE_TYPE_AMOUNT_WEIGHT) { // && ! this.containedVolumeAmount.isCalculated()) {
//				if ((lastUpdatedType != UPDATE_TYPE_TOTAL_WEIGHT && ! this.getMoleAmount().isCalculated()) || ! this.getRxnEquivsAmount().isCalculated()
//				|| this.getMoleAmount().doubleValue() > 0 && lastUpdatedType != UPDATE_TYPE_TOTAL_WEIGHT) {
//				updateTotalWeightFromMoles();
//				} else {
//				updateMolesFromTotalWeight();
//				}
				updateMolesFromContainedWeight();
				// Now that the solids are straightened out, we can calc the liquid
				if (this.getContainedMolarity().doubleValue() > 0) {
					// update volume
					amts.add(this.getContainedMoleAmount());
					amts.add(this.getContainedMolarity());
					if (this.containedVolumeAmount.isCalculated() && this.containedWeightAmount.isCalculated())
						this.containedVolumeAmount.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
					else
						applySigFigRules(this.containedVolumeAmount, amts);
					amts.clear();// important to clear the amts list
					this.containedVolumeAmount.SetValueInStdUnits(this.getContainedMoleAmount().GetValueInStdUnitsAsDouble() / this.getContainedMolarity().GetValueInStdUnitsAsDouble(),
							true);
					this.containedVolumeAmount.setCalculated(true);
				} 
			}
		else if (lastUpdatedProductType == UPDATE_TYPE_AMOUNT_MOLARITY) { 
			updateMolesFromContainedWeight();
			// Now that the solids are straightened out, we can calc the liquid
			if (this.getContainedMolarity().doubleValue() > 0) {
				// update volume
				amts.add(getContainedMoleAmount());
				amts.add(this.getContainedMolarity());
				if (this.getContainedVolumeAmount().isCalculated() && this.getContainedWeightAmount().isCalculated())
					this.getContainedVolumeAmount().setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
				else
					applySigFigRules(this.getContainedVolumeAmount(), amts);
				amts.clear();// important to clear the amts list
				this.getContainedVolumeAmount().SetValueInStdUnits(this.getContainedMoleAmount().GetValueInStdUnitsAsDouble() / this.getContainedMolarity().GetValueInStdUnitsAsDouble(),
						true);
				this.getContainedVolumeAmount().setCalculated(true);
			} 
		}
		if (lastUpdatedProductType != UPDATE_TYPE_AMOUNT_MOLARITY)
			updateContainedMolarity();
		inCalculation = false;
	}

	private void updateMolesFromContainedWeight() {
		double result = 0d;
		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		// weight std unit = mg; density std unit = mg/mmol
		// Weight = 9.82m/s^2 * mass
		if (batch.getMolWgt() > 0.0) {
			// TODO: Warning. Using getMolWgt() in the future may not return
			// standard units. Make sure this changes if the
			// users ever get the opportunity to change units of
			// MolWt.
			result = this.getContainedWeightAmount().GetValueInStdUnitsAsDouble() / batch.getMolWgt();
			amts.add(this.getContainedWeightAmount());
			amts.add(batch.getMolecularWeightAmount());
			// we just got the result for mg/mmole,
			// now we need to make sure the answer makes sense for the units of
			// weight currently being used.
			if (batch.getPurityAmount().doubleValue() < 100d && batch.getPurityAmount().doubleValue() > 0.0) {
				result = result * (batch.getPurityAmount().doubleValue() / 100);
				amts.add(batch.getPurityAmount());
			}
		}
		applySigFigRules(getContainedMoleAmount(), amts);
		amts.clear();
		containedMoleAmount.SetValueInStdUnits(result, true);
		//batch.getMoleAmount().SetValueInStdUnits(result, true);
	}
	
	private void updateContainedMolarity() {
		if (this.getContainedMoleAmount().doubleValue() > 0.0 && containedVolumeAmount.doubleValue() > 0.0)
		{
			double result = this.getContainedMoleAmount().GetValueInStdUnitsAsDouble() / containedVolumeAmount.GetValueInStdUnitsAsDouble();
			this.getContainedMolarity().SetValueInStdUnits(result, true);
			this.getContainedMolarity().setCalculated(true);
		}
	}

	/**
	 * Do we apply sig fig rules to calculations?
	 * 
	 */
	public boolean shouldApplySigFigRules() {
		return (!containedWeightAmount.isCalculated() || !containedVolumeAmount.isCalculated());
	}
	
	/**
	 * 
	 */
	public boolean shouldApplyDefaultSigFigs() {
		return (!this.getBatch().getRxnEquivsAmount().isCalculated() || !this.getContainedMoleAmount().isCalculated() || !this.getContainedMolarity().isCalculated());
	}
	
	public void applySigFigRules(AmountModel amt, List<AmountModel> amts) {
		if (shouldApplySigFigRules()) {
			amt.setSigDigits(CeNNumberUtils.getSmallestSigFigsFromAmountModelList(amts));
		} else {
			if (shouldApplyDefaultSigFigs())
				amt.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		}
	}

	public void setContainedVolumeAmount(AmountModel volume) {
		if (volume != null && volume.GetValueInStdUnitsAsDouble() != 0.0 ) {
			if (volume.getUnitType().getOrdinal() == UnitType.VOLUME.getOrdinal()) {
				boolean unitChange = false;
				if (! this.containedVolumeAmount.equals(volume)) {
					unitChange = BatchUtils.isUnitOnlyChanged(this.containedVolumeAmount, volume);
					this.containedVolumeAmount.deepCopy(volume);
					if (!unitChange) {
						lastUpdatedProductType = UPDATE_TYPE_AMOUNT_VOLUME;
						//updateCalcFlags(volumeAmount);
						setModified(true);
					}
				}
				if (!unitChange)
					recalcContainedAmounts();
			}
		} else {
			this.containedVolumeAmount.setValue("0");
		}
	}

	public AmountModel getContainedWeightAmount() {
		return containedWeightAmount;
	}

	public AmountModel getContainedVolumeAmount() {
		return containedVolumeAmount;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
		this.modelChanged = true;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
		this.modelChanged = true;
	}

	public boolean isInCalculation() {
		return inCalculation;
	}

	public void setInCalculation(boolean inCalculation) {
		this.inCalculation = inCalculation;
	}

	public String getCompoundManagementContainerGUID() {
		return compoundManagementContainerGUID;
	}

	public void setCompoundManagementContainerGUID(String tubeGUID) {
		this.compoundManagementContainerGUID = tubeGUID;
		this.modelChanged = true;
	}

	public AmountModel getContainedMoleAmount() {
		return containedMoleAmount;
	}

	public void setContainedMoleAmount(AmountModel containedMoleAmount) {
		this.containedMoleAmount = containedMoleAmount;
		this.modelChanged = true;
	}

	public void deepCopy(PlateWell<E> src) {
		if (src.key.equals(this.key))//This is called from deepClone().
		{
			setPlate(src.getPlate());
			setCompoundManagementContainerGUID(src.getCompoundManagementContainerGUID());
			setBarCode(src.getBarCode());
		}
		else
		{
			//setPlate(src.getPlate());
			//setBarCode(src.getBarCode()); Not required for deepCopy.
		}
		setContainedWeightAmount(src.getContainedWeightAmount());
		setContainedVolumeAmount(src.getContainedVolumeAmount());
		setContainedMolarity(src.getContainedMolarity());
		setContainedMoleAmount(src.getContainedMoleAmount());
		setPosition(src.getPosition()); // 1, 2, 3, 4, ....
		setNumber(src.getNumber()); // A01, A02, A03, ...., B01, B02, .....
		setSolventCode(src.getSolventCode());//"DMSO";//Test code assigned, need to be replaced
		setContainerType(src.getContainerType());//WELL, TUBE or VIAL
		setLocationCode(src.getLocationCode());
		setContainerTypeCode(src.getContainerTypeCode());
	}

	public String getContainerTypeCode() {
		return containerTypeCode;
	}

	public void setContainerTypeCode(String containerTypeCode) {
		this.containerTypeCode = containerTypeCode;
		this.modelChanged = true;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
		this.modelChanged = true;
	}
	
	
}
