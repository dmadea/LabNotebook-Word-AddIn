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
package com.chemistry.enotebook.client.gui.page.experiment.stoich;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.businessmodel.stoichiometry.ComparatorStoicAdditionOrder;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//this class plays the role of StoichiometeryModel in 1.1
//Also look at ReactionStepModelUtils.java 
public class PCeNStoichCalculator {

	private static final Log log = LogFactory.getLog(PCeNStoichCalculator.class);
	private ReactionStepModel rxnStepModel;
	private boolean autoCalcOn = true;
	private boolean isEditable = true;

	private String pageType; // Parallel/Med_chem etc

	public PCeNStoichCalculator(ReactionStepModel mrxnStepModel, String vpageType) {
		this.rxnStepModel = mrxnStepModel;
		pageType = vpageType;
	}

	/**
	 * Use to have the ReactionStep's Stoichiometry reevaluated Resets limiting reagent if one can't be found.
	 * 
	 */
	public void recalculateStoich() {
		log.debug("PCeNStoichCalculator.recalculateStoich().enter");
		if (isEditablePage()) {
			StoicModelInterface rb = null;
			// Determine the limiting reagent
			if (autoCalcOn)
				rb = setLimitingReagent();
			// Make sure there is at least one Intended Product if there is a limiting Reagent.
			if (rb != null) {
				// Now make sure everyone is up to date
				recalculateMolesBasedOnLimitingReagent(rb);
				recalculateRxnEquivsBasedOnLimitingReagentMoles(rb);
			} else {
				// remove any intended products.
				if (getBatches(BatchType.REACTANT_ORDINAL).size() == 0) {
					rxnStepModel.deleteBatches(BatchType.INTENDED_PRODUCT);
				}
			}
			recalculateProductsBasedOnStoich();
			updateMonomerAndProductListLevelFlags();
			log.debug("PCeNStoichCalculator.recalculateStoich().exit");
		}
	}

	public void recalculateStoichBasedOnBatch(StoicModelInterface rb, boolean calcMolesOnly) {
		log.debug("PCeNStoichCalculator.recalculateStoichBasedOnBatch().enter");
		if (rb != null) {
			// see if this batch(List) is qualified as LimitReag
			if (rb.isStoicLimiting() && !canBeLimiting(rb)) {
				rb.setStoicLimiting(false);
			}
			// find limiting reagent
			StoicModelInterface limitingReag = findLimitingReagent();
			if (limitingReag == null && autoCalcOn) {
				limitingReag = setLimitingReagent();
			}
            if (rb.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				recalculateSolventAmounts(rb, limitingReag);
				// to recalculate Molarity or Volume
            }
            if (limitingReag != null) {// vb 5/8 limitingBatch is null if the user hasn't entered stoic info and is in
				if (CeNNumberUtils.doubleEquals(limitingReag.getStoicRxnEquivsAmount().GetValueInStdUnitsAsDouble(), 0.0,
						0.0001)) {
					resetRxnEquivs(limitingReag); // Puts back to 1.0
				}
                // Catalyst to get calcs going in case limiting reag isn't filled in.
                // Make sure the limiting reagent is set and has a value.
                if (limitingReag.getStoicMoleAmount().isValueDefault()) {
                    recalculateAmountsForBatch(rb, limitingReag, calcMolesOnly);
                }

                // Update Molarity for all solvents, if moles of limiting reagent is modified
                recalculateMolarAmountForSolvent(limitingReag.getStoicMoleAmount());
                findAndRecalculateVolumeForSolvents(rxnStepModel.getStoicElementListInTransactionOrder(), limitingReag.getStoicMoleAmount(), null);
            }
			
            // unset any other limiting reagent flag
            if(rb.isStoicLimiting()) {
                clearLimitingReagentFlags(rb);
            }
            // re calculate the moles of all the other batches in stoic based on limit reag moles
            recalculateMolesBasedOnLimitingReagent(rb);

			recalculateProductsBasedOnStoich();
			
			// if rb data is meaningfull then display EQ value otherwise - don't display  
//			rb.getStoicRxnEquivsAmount().setCanBeDisplayed(!isReactionEquivMean	ingless(rb));
		}
		updateMonomerAndProductListLevelFlags();
		log.debug("PCeNStoichCalculator.recalculateStoichBasedOnBatch().exit");
	}

	public void recalculateProductsBasedOnStoich() {
		log.debug("PCeNStoichCalculator.recalculateProductsBasedOnStoich().enter");
		if (isEditablePage()) {
			StoicModelInterface limitingReag = findLimitingReagent();

			if (limitingReag != null) {
				// addProductIfNeeded(); // For MED-CHEM
				// used to keep a place marker for stoich products if there isn't one
				// allows calcs to be setup such that the user can see the effect of
				// their changes.
				for (StoicModelInterface pb : getProductBatches()) {
					// updateIntendedProductPrecursors(); // For MED_CHEM
					recalculateProductAmounts(limitingReag, pb);
				}
			}
		}
		log.debug("PCeNStoichCalculator.recalculateProductsBasedOnStoich().exit");
	}

	/**
	 * If only Volume is set for the batch then don't display reaction eq value
	 * 
	 * @param rb
	 * @return
	 */
	private boolean isReactionEquivMeaningless(StoicModelInterface rb) {
		// if only Volume is set for it then don't display eq value
		return !rb.getStoicVolumeAmount().isCalculated() && rb.getStoicMoleAmount().isCalculated() &&
			rb.getStoicRxnEquivsAmount().isCalculated() && rb.getStoicDensityAmount().isCalculated() &&
			rb.getStoicMolarAmount().isCalculated();
	}
	
	private boolean canBeLimiting(StoicModelInterface rb) {
		if (rb.getStoicReactionRole().equals(BatchType.SOLVENT.toString()) || isReactionEquivMeaningless(rb))
			return false;
		else
			return true;
	}

	// Find currently already marked limiting reagent in the list.
	public StoicModelInterface findLimitingReagent() {
		StoicModelInterface result = null;

		if (rxnStepModel != null) {
			for(StoicModelInterface reagent : rxnStepModel.getStoicElementListInTransactionOrder()) {
				if (reagent.isStoicLimiting() && canBeLimiting(reagent)) {
					result = reagent;
					break;
				}
			}
		}

		return result;
	}

	// Find current limiting reagent.Identify new limiting reagent if there is none currently.
	// lowest mole value batch(List) is selected as Limiting Reagent and set equivs to 1.0 if current value is zero
	private StoicModelInterface setLimitingReagent() {
		StoicModelInterface result = null;
		if (isEditablePage()) {
			result = findLimitingReagent();
			if (result == null) {
				for (StoicModelInterface test : getReagentBatches()) {
					test.setStoicLimiting(false);
					if (canBeLimiting(test) && (result == null ||
					    test.getStoicMoleAmount().doubleValue() < result.getStoicMoleAmount().doubleValue())) 
					{
						result = test;
					}
				}
				if (result != null) {
					result.setStoicLimiting(true);
					if (result.getStoicRxnEquivsAmount().isValueDefault())
						resetRxnEquivs(result);
				}
			}
		}
		return result;
	}

	public void recalculateSolventAmounts(StoicModelInterface rb, StoicModelInterface limitingReagent) {
		log.debug("PCeNStoichCalculator.recalculateSolventAmounts().enter");
		if (limitingReagent == null)
			return;
		AmountModel molarAmount = rb.getStoicMolarAmount();
		AmountModel volumeAmount = rb.getStoicVolumeAmount();
		ArrayList<AmountModel> volumeAmountList = getVolumeAmountsForAllSolvents();
		if (molarAmount.GetValueInStdUnitsAsDouble() == 0.0 && volumeAmount.GetValueInStdUnitsAsDouble() == 0.0)
			return;

		List<StoicModelInterface> reagents = getReagentBatches();
		for (int i = 0; i < reagents.size(); i++) {
			StoicModelInterface b = (StoicModelInterface) reagents.get(i);
			// if solvant go ahead
			if (b.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				// if this batch is the editing batch
				if (b.equals(rb)) {
					if (volumeAmount.isCalculated()) {
						// Case for user entry for Molar amount
						if (!molarAmount.isCalculated() && molarAmount.GetValueInStdUnitsAsDouble() != 0) {
							recalculateVolumeForSolvent(limitingReagent.getStoicMoleAmount(), rb, volumeAmountList);
							molarAmount = rb.getStoicMolarAmount();
							updateMolarAmountsForSolvent(molarAmount.GetValueInStdUnitsAsDouble(), molarAmount.getSigDigits());
							molarAmount.setCalculated(false);
							rb.setPreviousMolarAmount(molarAmount);
						}
					} else if (molarAmount.isCalculated()) {
						if (molarAmount.GetValueInStdUnitsAsDouble() == 0 || areAllVolumesUserEnteredForSovents())
							recalculateMolarAmountForSolvent(limitingReagent.getStoicMoleAmount());
						// User entered Volume amount
						// once Molar amounts are updated for the volume entered, it is required
						// to find and update the volume that was calculated.
						findAndRecalculateVolumeForSolvents(reagents, limitingReagent.getStoicMoleAmount(), volumeAmount);
						rb.getStoicVolumeAmount().setCalculated(false);
					} else {
						// Volume and Molarity are both user entered, check if Molar Amount
						// is different than other solvents, if different then user have entered
						// for Molarity
						AmountModel tempMolarAmt = rb.getPreviousMolarAmount();
						if (tempMolarAmt.GetValueInStdUnitsAsDouble() != molarAmount.GetValueInStdUnitsAsDouble()) {
							// Molarity entered by the user
							updateMolarAmountsForSolvent(rb.getStoicMolarAmount().GetValueInStdUnitsAsDouble(), rb
									.getStoicMolarAmount().getSigDigits());
							applySigDigitsToAllSolventMolarites(rb.getStoicMolarAmount());
							rb.getStoicMolarAmount().setCalculated(false);
							// Calculate Volume for this solvent
							recalculateVolumeForSolvent(limitingReagent.getStoicMoleAmount(), rb, volumeAmountList);
							rb.getStoicVolumeAmount().setCalculated(true);
							rb.setPreviousMolarAmount(rb.getStoicMolarAmount());
						} else {
							if (areAllVolumesUserEnteredForSovents()) {
								// recalculate Molarity with the updated total volume
								recalculateMolarAmountForSolvent(limitingReagent.getStoicMoleAmount());
								rb.getStoicVolumeAmount().setCalculated(false);
								rb.getStoicMolarAmount().setCalculated(true);
							} else {
								// once Molar amounts are updated for the volume entered, it is required
								// to find and update the volume that was calculated.
								findAndRecalculateVolumeForSolvents(reagents, limitingReagent.getStoicMoleAmount(), volumeAmount);
							}
						} // end of if-else started with if(isCurrentCalcForMolarityChange)
					} // end of if-else started with if (volumeAmount.isCalculated())
				}// end of if(b.equals(rb))
			}// end of if (b.getType().equals(BatchType.SOLVENT))
		}// end of for loop
		log.debug("PCeNStoichCalculator.recalculateSolventAmounts().exit");
	}

	private void recalculateAmountsForBatch(StoicModelInterface limtReag, StoicModelInterface targetReag, boolean calcMolesOnly) {
		log.debug("PCeNStoichCalculator.recalculateAmountsForBatch().enter");
		if (isEditablePage() && limtReag != null && targetReag != null && limtReag != targetReag && targetReag.isAutoCalcOn()) {
			// Need to decide if we are updating RxnEquivs or a weight amount
			if (!targetReag.getStoicRxnEquivsAmount().isCalculated() || 
			    (targetReag.getStoicMoleAmount().isCalculated() && 
				 targetReag.getStoicWeightAmount().isCalculated() && 
				 targetReag.getStoicVolumeAmount().isCalculated())) 
			{
				recalculateMoleAmountForBatch(limtReag, targetReag);
			}

			if (!calcMolesOnly && limtReag != null && limtReag.getStoicMoleAmount().doubleValue() > 0.0
					&& targetReag.getStoicRxnEquivsAmount().isCalculated()) {
				// don'e bother if sourceReag's mole amount isn't > 0
				// Update the rxnEquivs for the batch
				recalculateRxnEquivsForBatch(limtReag, targetReag);
			}
		}
		log.debug("PCeNStoichCalculator.recalculateAmountsForBatch().exit");
	}

	private void resetRxnEquivs(StoicModelInterface ab) {
		if (ab.getStoicRxnEquivsAmount().doubleValue() == 0 && ab.getStoicRxnEquivsAmount().isCalculated()) {
			AmountModel rxnEquiv = ab.getStoicRxnEquivsAmount();
			rxnEquiv.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
			rxnEquiv.setValue("1.00", true);
			// ab can be Monomer list or a simple batch. So call the setter so that all the models in list will targeted
			ab.setStoicRxnEquivsAmount(rxnEquiv);
		}
	}

	/**
	 * Removes isLimiting() flag from any reagent in the stoich model. set rb to null if all reagents are to have their limiting
	 * flag unset. Otherwise pass in the limiting reagent after setting its flag and all other stoich batches will have their flags
	 * unset.
	 */
	public void clearLimitingReagentFlags(StoicModelInterface exceptForThisReagent) {
		StoicModelInterface limiting = exceptForThisReagent;
		for (StoicModelInterface b : rxnStepModel.getStoicElementListInTransactionOrder()) {
			if (!b.getGUIDKey().equals(limiting.getGUIDKey())) {
				b.setStoicLimiting(false);
			}
		}
	}

	/**
	 * Recalculates Moles for all batches in this ReactionStep given the limiting Reagent. Use when limiting reagent is set, or the
	 * amount is changed in some way.
	 * 
	 * @param ab -
	 *            Batch to alter based on limiting reagent.
	 */
	public void recalculateMolesBasedOnLimitingReagent(StoicModelInterface limitingAB) {
		log.debug("PCeNStoichCalculator.recalculateMolesBasedOnLimitingReagent().enter");
		if (isEditablePage() && limitingAB != null && canBeLimiting(limitingAB)) {
			for (StoicModelInterface b : getReagentBatches()) {
				if (!b.equals(limitingAB) && b.isAutoCalcOn()) {
					// So in effect you are saying that if the volume of the limiting reagent is zero you don't 
					// want to permit any reagent from being updated by values in the limiting reagent.
					// That is an incorrect assumption as limiting reagent can be dry or neat.
					// I believe this check-in will break our calculations when volume of limiting reagent is zero.
					// Try again.
					if (CeNNumberUtils.doubleEquals(limitingAB.getStoicVolumeAmount().doubleValue(), 0.0) && CeNNumberUtils.doubleEquals(limitingAB.getStoicWeightAmount().doubleValue(), 0.0) ) {
						//nothing
						if (b.isStoicLimiting()) {
							AmountModel am = b.getStoicMoleAmount();
							AmountModel tmpAmt = new AmountModel(am.getUnitType());
							tmpAmt.deepCopy(am);
							limitingAB.setStoicMoleAmount(tmpAmt);
						}
					} else if (b.getStoicRxnEquivsAmount().GetValueInStdUnitsAsDouble() > 0.0 &&
							(b.getStoicMoleAmount().doubleValue() > 0.0 || 
									CeNNumberUtils.doubleEquals(b.getStoicMoleAmount().doubleValue(), 0.0) && 
									b.getStoicVolumeAmount().doubleValue() > 0.0) || limitingAB.isStoicLimiting()) {
						// Need to not calculate Amounts for SOLVENTS
						if (!b.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
							// if the batch 'limitingAB' is a limited reagent then all the batches should be recalculated.
							if (limitingAB.isStoicLimiting()) {
								recalculateAmountsForBatch(limitingAB, b, false);								
							
							// if the batch 'limitingAB' is NOT a limited reagent then only this batch should be recalculated basing on the limited one
							} else if (b.isStoicLimiting()) {
								recalculateAmountsForBatch(b, limitingAB, false);
							}
						}
					} else if (b.getStoicMoleAmount().doubleValue() > 0.0) {
						b.resetAmount(CeNConstants.MOLE_AMOUNT);
					}
				}
			}
			updateActualProductTheoAmounts(limitingAB);
		}
		log.debug("PCeNStoichCalculator.recalculateMolesBasedOnLimitingReagent().exit");
	}

	public void recalculateMolarAmountForSolvent(AmountModel limitingMole) {
		log.debug("PCeNStoichCalculator.recalculateMolarAmountForSolvent().enter");
		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		ArrayList<AmountModel> amountsList = getVolumeAmountsForAllSolvents();
		AmountModel tempAmount = null;
		AmountModel limitingMoleAmt = limitingMole;
		double totalVolume = 0;
		// adding up the total volume
		for (int j = 0; j < amountsList.size(); j++) {
			tempAmount = (AmountModel) amountsList.get(j);
			totalVolume += tempAmount.GetValueInStdUnitsAsDouble();
		}
		amts.addAll(amountsList);
		amts.add(limitingMoleAmt);
		// Molarity calculation
		double molarity = limitingMoleAmt.GetValueInStdUnitsAsDouble() / totalVolume;
		updateMolarAmountsForSolvent(molarity, CeNNumberUtils.getSmallestSigFigsFromAmountModelList(amts));
		amts.clear();
		log.debug("PCeNStoichCalculator.recalculateMolarAmountForSolvent().exit");
	}

//	private boolean isMolarityOfRxnUserEntered() {
//		ArrayList<AmountModel> amountsList = getMolarAmountsForAllSolvents();
//		for (int j = 0; j < amountsList.size(); j++) {
//			// Checks if Amount is user entered
//			if (!((AmountModel) amountsList.get(j)).isCalculated())
//				return true;
//		}
//		return false;
//	}

	private void findAndRecalculateVolumeForSolvents(List<StoicModelInterface> reagents, AmountModel limitingMoleAmount, AmountModel currentVolumeAmount) {

		// update the calculated volume to current value.
		for (int k = 0; k < reagents.size(); k++) {
			StoicModelInterface batch = (StoicModelInterface) reagents.get(k);
			if (batch.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				AmountModel tempVolumeAmt = batch.getStoicVolumeAmount();
				if (!tempVolumeAmt.equals(currentVolumeAmount)) {
					if (tempVolumeAmt.isCalculated()) {
						recalculateVolumeForSolvent(limitingMoleAmount, batch, getVolumeAmountsForAllSolvents());
					}// end of if(volumeAmount.isCalculated())
				} // end of if(!reagents.get(k).equals(volumeAmount))
			}// end of if(b.getType().equals(BatchType.SOLVENT)
		} // end of for loop
	}

	boolean isEditablePage() {
		return this.isEditable;
	}

	// private List getReagentBatches() {
	// return rxnStepModel.getStoicElementListInTransactionOrder();
	// }

	public void updateActualProductTheoAmounts(StoicModelInterface limitingReag) {
		log.debug("PCeNStoichCalculator.updateActualProductTheoAmounts().enter");
		if (isEditablePage() && limitingReag != null && canBeLimiting(limitingReag)) {
			for (StoicModelInterface pb : getActualProductBatches()) {
				// Because the amount might indicate user edits we need to make sure the new version
				// doesn't. When set..Amount() is called the modified flag can cause other events to
				// trigger before we are ready. Make sure the calc'd flag is set properly first.
				AmountModel tmpAmt = new AmountModel(UnitType.MOLES);
				tmpAmt.deepCopy(limitingReag.getStoicMoleAmount());
				tmpAmt.setCalculated(true);
				pb.setTheoreticalMoleAmount(tmpAmt);
				pb.recalcAmounts(); // Updates the theo weight and yield
			}
		}
		log.debug("PCeNStoichCalculator.updateActualProductTheoAmounts().exit");
	}

	public void recalculateProductAmounts(StoicModelInterface limitingReag, StoicModelInterface prodBatch) {
		log.debug("PCeNStoichCalculator.recalculateProductAmounts().enter");
		if (isEditablePage() && limitingReag != null && prodBatch.isAutoCalcOn()) {
			// if moleAmount are not = limiting moleAmount and not set by the user, set it.
			// send to recalc based on moleAmount. Need to be sure that weightAmount isn't
			// edited by the user either.
			if (!prodBatch.getStoicRxnEquivsAmount().isCalculated() || 
					(prodBatch.getStoicWeightAmount().isCalculated() && prodBatch.getStoicMoleAmount().isCalculated()
					&& !limitingReag.getStoicMoleAmount().equals(prodBatch.getStoicMoleAmount()))) {
				// Need to set calc flag regardless of limitingReag state.
				AmountModel amtTemp = (AmountModel) limitingReag.getStoicMoleAmount().deepClone();
				double targetMoles = amtTemp.doubleValue() * prodBatch.getStoicRxnEquivsAmount().GetValueInStdUnitsAsDouble();
				amtTemp.setValue(targetMoles);
				amtTemp.setCalculated(true);
				// Causes batch to recalculate amounts based on Mole change.
				prodBatch.setStoicMoleAmount(amtTemp);
				if (limitingReag.shouldApplySigFigRules()) {
					prodBatch.applyLatestSigDigits(amtTemp.getSigDigits());
				} else {
					prodBatch.applyLatestSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
				}
			}

			//--equivalents of Int. Prod should not be recalculated. It can be set only by user
			//			//calculate the equivalents of Int. Prod after the Limiting
			//			//reagent moles have changed
			//            //calc new Equiv amount
			//			AmountModel amtTempEquiv = (AmountModel) prodBatch.getStoicRxnEquivsAmount().deepClone();
			//			if(limitingReag.getStoicRxnEquivsAmount().equals(prodBatch.getStoicRxnEquivsAmount()) == false) {
			//				amtTempEquiv.setValue(limitingReag.getStoicRxnEquivsAmount().doubleValue());
			//			} else {
			//				double newEquivValue = BatchUtils.calcEquivalentsWithMoles(prodBatch.getStoicMoleAmount(), limitingReag.getStoicMoleAmount());
			//				amtTempEquiv.setValue(newEquivValue);
			//			}
			//			amtTempEquiv.setCalculated(true);
			//			// Causes batch to recalculate amounts based on Equivs change.
			//			prodBatch.setStoicRxnEquivsAmount(amtTempEquiv);
			
			//			if (limitingReag.shouldApplySigFigRules())
			//				prodBatch.applyLatestSigDigits(amtTempEquiv.getSigDigits());
			//			else
			//				prodBatch.applyLatestSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
			
			if (this.pageType.equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
				//Now loop through the matching ACTUAL prod batches and update theo values
				updateActualProductTheoAmounts(limitingReag);
			}
		}
		log.debug("PCeNStoichCalculator.recalculateProductAmounts().exit");
	}

	private ArrayList<AmountModel> getVolumeAmountsForAllSolvents() {
		List<StoicModelInterface> reagents = getReagentBatches();
		ArrayList<AmountModel> list = new ArrayList<AmountModel>(reagents.size());
		for (int i = 0; i < reagents.size(); i++) {
			StoicModelInterface b = (StoicModelInterface) reagents.get(i);
			// if solvent go ahead
			if (b.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				list.add(b.getStoicVolumeAmount());
			}
		}
		return list;
	}// end of method

	private void updateMolarAmountsForSolvent(double value, int sigDigits) {
		List<StoicModelInterface> reagents = getReagentBatches();
		// Loop for reagents
		for (int i = 0; i < reagents.size(); i++) {
			StoicModelInterface b = (StoicModelInterface) reagents.get(i);
			// if solvent then set the Molar amount
			if (b.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				AmountModel actualMolarModel = b.getStoicMolarAmount();
				if (actualMolarModel.isCalculated()) {				
					AmountModel molarModel = (AmountModel)actualMolarModel.deepClone();
					molarModel.SetValueInStdUnits(value);
					molarModel.setSigDigits(sigDigits);
					b.setStoicMolarAmount(molarModel);
					b.setPreviousMolarAmount(b.getStoicMolarAmount());
				}
			}
		}
	}

	public boolean areAllVolumesUserEnteredForSovents() {
		List<StoicModelInterface> reagents = getReagentBatches();
		for (int i = 0; i < reagents.size(); i++) {
			StoicModelInterface b = (StoicModelInterface) reagents.get(i);
			// if solvent go ahead
			if (b.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				if (b.getStoicVolumeAmount().isCalculated())
					return false;
			}
		}
		return true;
	}

	private void recalculateVolumeForSolvent(AmountModel limitingMole, StoicModelInterface rb, ArrayList<AmountModel> volumeAmounts) {
		ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
		AmountModel tempVolumeAmt = null;
		double tempMolarAmtValue = rb.getStoicMolarAmount().GetValueInStdUnitsAsDouble();
		double calcVolume = 0;
		amts.add(limitingMole);
		amts.add(rb.getStoicMolarAmount());

		if (tempMolarAmtValue > 0) {
			calcVolume = limitingMole.GetValueInStdUnitsAsDouble() / tempMolarAmtValue;
		} else
			return;
		// CalcVolume need to be subtracted with volumes of other solvents
		for (int k = 0; k < volumeAmounts.size(); k++) {
			tempVolumeAmt = (AmountModel) volumeAmounts.get(k);
			if (!tempVolumeAmt.equals(rb.getStoicVolumeAmount())) {
				calcVolume -= tempVolumeAmt.GetValueInStdUnitsAsDouble();
				amts.add(tempVolumeAmt);
				if (tempVolumeAmt.GetValueInStdUnitsAsDouble() != 0)
					tempVolumeAmt.setCalculated(false);
			}
		}
		AmountModel actaulVolumeAmt = rb.getStoicVolumeAmount();
		AmountModel volumeAmt = (AmountModel)actaulVolumeAmt.deepClone();
		volumeAmt.SetValueInStdUnits(calcVolume, true);
		volumeAmt.setSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
		rb.setStoicVolumeAmount(volumeAmt);
		amts.clear();
	}

	private void applySigDigitsToAllSolventMolarites(AmountModel molarAmt) {
		ArrayList<AmountModel> amountsList = getMolarAmountsForAllSolvents();
		for (int j = 0; j < amountsList.size(); j++) {
			AmountModel tempAmt = (AmountModel) amountsList.get(j);
			if (!tempAmt.equals(molarAmt)) {
				tempAmt.setSigDigits(molarAmt.getSigDigits());
			}
		}
	}

	private ArrayList<AmountModel> getMolarAmountsForAllSolvents() {
		List<StoicModelInterface> reagents = getReagentBatches();
		ArrayList<AmountModel> list = new ArrayList<AmountModel>(reagents.size());
		for (int i = 0; i < reagents.size(); i++) {
			StoicModelInterface b = (StoicModelInterface) reagents.get(i);
			// if solvent go ahead
			if (b.getStoicReactionRole().equals(BatchType.SOLVENT.toString())) {
				list.add(b.getStoicMolarAmount());
			}
		}
		return list;
	}// end of method

	public void recalculateMoleAmountForBatch(StoicModelInterface limtReag, StoicModelInterface targetReag) {
		// The last check here is literal: sourceReag != the same object as targetReag
		if (isEditablePage() && limtReag != null && targetReag != null && limtReag != targetReag && targetReag.isAutoCalcOn()) {
			// Equiv amounts are always scalar so StdUnits are the same as the value itself
			double limitingRxnEquivs = limtReag.getStoicRxnEquivsAmount().doubleValue();
			// ??? should we get value in std unit or the unit user selected ? molAmt.GetValueInStdUnitsAsDouble()
			double limitingMoles = limtReag.getStoicMoleAmount().GetValueInStdUnitsAsDouble();
			if (targetReag instanceof BatchesList<?>) {
				BatchesList<MonomerBatchModel> blist = (BatchesList<MonomerBatchModel>) targetReag;
				if (blist.getBatchModels() == null)
					return;
				for (MonomerBatchModel batchModel : blist.getBatchModels()) {
					ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
					// RxnEquivsAmount is scalar, hence no need for std units.
					if (CeNNumberUtils.doubleEquals(batchModel.getStoicRxnEquivsAmount().doubleValue(), 0.0, 0.00001)) {
						// somehow the targetReag's equivs weren't set.
						// default equivs = 1
						resetRxnEquivs(batchModel); // targetReag.getRxnEquivsAmount().setValue("1.00", true);
					}
					// (targetMoles * ab.getRxnEquivs()) = (limitingMoles * limitingRxnEquivs())
					double targetMoles = limitingMoles * (batchModel.getStoicRxnEquivsAmount().doubleValue() / limitingRxnEquivs);
					amts.add(limtReag.getStoicMoleAmount());
					amts.add(batchModel.getStoicRxnEquivsAmount());
					amts.add(limtReag.getStoicRxnEquivsAmount());
					// Do not do this as this calc below is wrong by 1/x!
					// double targetMoles = limitingMoles * limitingRxnEquivs / targetReag.getRxnEquivsAmount().doubleValue();

					// to trigger calc of weight ( recalcAmount() method )
					AmountModel molModelActual = batchModel.getStoicMoleAmount();
					AmountModel molModel = (AmountModel)molModelActual.deepClone();
					// molModel.setValue(targetMoles);
					// set same unit as Limiting Reagent's
					// molModel.setUnit(limtReag.getStoicMoleAmount().getUnit());
					// molModel.setCalculated(true);
					molModel.SetValueInStdUnits(targetMoles, true);
					molModel.setUnit(limtReag.getStoicMoleAmount().getUnit());
					batchModel.setMoleAmount(molModel);
					batchModel.applySigFigRules(batchModel.getStoicMoleAmount(), amts);
				}
			} else {

				ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
				// RxnEquivsAmount is scalar, hence no need for std units.
				if (CeNNumberUtils.doubleEquals(targetReag.getStoicRxnEquivsAmount().doubleValue(), 0.0, 0.00001)) {
					// somehow the targetReag's equivs weren't set.
					// default equivs = 1
					resetRxnEquivs(targetReag); // targetReag.getRxnEquivsAmount().setValue("1.00", true);
				}
				// (targetMoles * ab.getRxnEquivs()) = (limitingMoles * limitingRxnEquivs())
				double targetMoles = limitingMoles * (targetReag.getStoicRxnEquivsAmount().doubleValue() / limitingRxnEquivs);
				amts.add(limtReag.getStoicMoleAmount());
				amts.add(targetReag.getStoicRxnEquivsAmount());
				amts.add(limtReag.getStoicRxnEquivsAmount());
				// Do not do this as this calc below is wrong by 1/x!
				// double targetMoles = limitingMoles * limitingRxnEquivs / targetReag.getRxnEquivsAmount().doubleValue();

//				AmountModel molModelActual = targetReag.getStoicMoleAmount();
				AmountModel molModel = (AmountModel) targetReag.getStoicMoleAmount().deepClone();
				// molModel.setValue(targetMoles);
				// set same unit as Limiting Reagent's
				// molModel.setUnit(limtReag.getStoicMoleAmount().getUnit());
				// molModel.setCalculated(true);
				// ??Issue is whether to set in std units or in units that of limReag's
				molModel.SetValueInStdUnits(targetMoles, true);
				molModel.setUnit(limtReag.getStoicMoleAmount().getUnit());
				targetReag.setStoicMoleAmount(molModel);
				targetReag.applySigFigRules(targetReag.getStoicMoleAmount(), amts);
			}
		}
	}

	public void recalculateRxnEquivsForBatch(StoicModelInterface limtReag, StoicModelInterface targetReagent) {
		if (isEditablePage() && 
		    limtReag != null && 
		    targetReagent != null && 
		    targetReagent.isAutoCalcOn() && 
		    targetReagent.getStoicRxnEquivsAmount().isCalculated()) 
		{
			// Need to account for interesting ratios like a limiting reagent that has
			// a rxnEquiv of 2 while the target only has a 1.
			// Invariant limitingRxnEquivs != 0.0
			double result = 1.0; // default rxnEquiv amount.
			// TODO: when is 1.0 going to fall through and make this calc wrong?
			
			double sourceMoles = limtReag.getStoicMoleAmount().GetValueInStdUnitsAsDouble();
			double sourceRxnEquivs = limtReag.getStoicRxnEquivsAmount().doubleValue();
			if (targetReagent instanceof BatchesList<?>) 
			{
				BatchesList<MonomerBatchModel> blist = (BatchesList<MonomerBatchModel>) targetReagent;
				if (blist.getBatchModels() == null)
					return;
				
				for (MonomerBatchModel batchModel : blist.getBatchModels()) 
				{
					double targetReagMoles = batchModel.getStoicMoleAmount().GetValueInStdUnitsAsDouble();	
					
					ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
					if (targetReagMoles > 0.0)
					{
						amts.add(batchModel.getStoicMoleAmount());
					}
					// Make sure targetReagMoles > 0
					if (targetReagMoles == 0.0 && batchModel.getStoicMolecularWeightAmount().doubleValue() > 0.0) {
						targetReagMoles = batchModel.getStoicWeightAmount().GetValueInStdUnitsAsDouble()/ batchModel.getStoicMolecularWeightAmount().doubleValue();
						amts.add(batchModel.getStoicMolecularWeightAmount());
						amts.add(batchModel.getStoicWeightAmount());
						if (batchModel.getStoicPurityAmount().doubleValue() < 100d && batchModel.getStoicPurityAmount().doubleValue() > 0.0) {
							result = result * (batchModel.getStoicPurityAmount().doubleValue() / 100);
							amts.add(batchModel.getStoicPurityAmount());
						}
					}
					if (batchModel.getStoicRxnEquivsAmount().isCalculated() && targetReagMoles > 0.0) {
// need to use version from Singleton in else clause below.  This version creates multiples when limiting rxn equivs < 0.
// (targetReagMoles * targetReag.getRxnEquivs()) = (sourceMoles * sourceReag.getRxnEquivs())
						// (targetReagMoles / sourceMoles) = (targetReag.getRxnEquivs() / sourceReag.getRxnEquivs())
						// RxnEquivsAmount is scalar, hence no need for std units.
						double testResult = (targetReagMoles / sourceMoles) * sourceRxnEquivs;
						// Doing this is wrong!
						// double testResult = (sourceMoles * sourceRxnEquivs) / targetReagMoles;
						if (testResult >= 0.0)
							result = testResult;
						// RxnEquivs is a scalar Unit, though Mole amounts are currently only mMole amounts.
						// Hence we can simply set the result, but it is safer tu use InStdUnits() method
						// as there is noise of adding mole amounts in the future.
						amts.add(limtReag.getStoicMoleAmount());
						amts.add(limtReag.getStoicRxnEquivsAmount());
						batchModel.getStoicRxnEquivsAmount().SetValueInStdUnits(result, true);
						batchModel.applySigFigRules(batchModel.getStoicRxnEquivsAmount(), amts);
					}
				}//for each batch
			} else {
				MonomerBatchModel batchModel = (MonomerBatchModel) targetReagent;
				double targetReagMoles = batchModel.getStoicMoleAmount().GetValueInStdUnitsAsDouble();	
				
				ArrayList<AmountModel> amts = new ArrayList<AmountModel>();
				if (targetReagMoles > 0.0)
				{
					amts.add(batchModel.getStoicMoleAmount());
				}
				// Make sure targetReagMoles > 0
				if (targetReagMoles == 0.0 && batchModel.getStoicMolecularWeightAmount().doubleValue() > 0.0) {
					targetReagMoles = batchModel.getStoicWeightAmount().GetValueInStdUnitsAsDouble() / batchModel.getStoicMolecularWeightAmount().doubleValue();
					amts.add(batchModel.getStoicMolecularWeightAmount());
					amts.add(batchModel.getStoicWeightAmount());
					if (batchModel.getStoicPurityAmount().doubleValue() < 100d && batchModel.getStoicPurityAmount().doubleValue() > 0.0) {
						result = result * (batchModel.getStoicPurityAmount().doubleValue() / 100);
						amts.add(batchModel.getStoicPurityAmount());
					}
				}
				if (targetReagMoles > 0.0) {
					// (targetReagMoles / sourceMoles) = (targetReag.getRxnEquivs() / sourceReag.getRxnEquivs())
					// RxnEquivsAmount is scalar, hence no need for std units.
					double testResult = (targetReagMoles / sourceMoles) * sourceRxnEquivs;
					// Doing this is wrong!
//					double testResult = (sourceMoles * sourceRxnEquivs) / targetReagMoles;
					if (testResult >= 0.0)
						result = testResult;
					// RxnEquivs is a scalar Unit, though Mole amounts are currently only mMole amounts.
					// Hence we can simply set the result, but it is safer tu use InStdUnits() method
					// as there is noise of adding mole amounts in the future.
					amts.add(limtReag.getStoicMoleAmount());
					amts.add(limtReag.getStoicRxnEquivsAmount());
					batchModel.getStoicRxnEquivsAmount().SetValueInStdUnits(result, true);
					batchModel.applySigFigRules(batchModel.getStoicRxnEquivsAmount(), amts);
				}	
			}
		}
	}

	public void insertBatchIntoAdditionOrder(StoicModelInterface model, int position) {
		List<StoicModelInterface> stoicList = rxnStepModel.getStoicElementListInTransactionOrder();
		if (position >= 0 && position < stoicList.size()) {
			model.setStoicTransactionOrder(position);
			stoicList.add(position, model);
		} else if (position >= stoicList.size()) {
			// append batch to list.
			model.setStoicTransactionOrder(stoicList.size());
			stoicList.add(model);
		}
		rxnStepModel.addStoicModelInterface(model);
		updateAdditionOrder(stoicList);
		recalculateStoich();
	}

	private void updateAdditionOrder(List<StoicModelInterface> batches) {
		for (int i = 0; i < batches.size(); i++) {
			StoicModelInterface model = (StoicModelInterface) batches.get(i);
			model.setStoicTransactionOrder(i);
		}
	}

	public void removeBatchFromStep(StoicModelInterface stoicModel) {
		// stoicList.remove(stoicModel);
		rxnStepModel.deleteStoicElement(stoicModel);
		// The above method also handles the update of AddtionOrder after delete.
		recalculateStoich();

	}

	/**
	 * This method adds a ProductBatchModel to products list if there are monomer batches and no INTENDED products in a ReactionStepModel
	 * This is applicable for non-Parallel exps only
	 */
	public void addProductIfNeeded() {
		if(this.pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) return;
			
		if (getProductBatches().size() == 0 && 
        		getReactantBatches().size() >= 1) {
    	    createIntendedProduct( getProductBatches().size() );  // Ignoring return value.
        }
	}
	
	public ProductBatchModel createIntendedProduct(int additionOrder)
	{
		ProductBatchModel result = null;
	    try {
                result = new ProductBatchModel();
                result.setBatchType(BatchType.INTENDED_PRODUCT);
                result.getCompound().setCreatedByNotebook( true );
                resetRxnEquivs( result );
                result.setPrecursors( getPreCursorsForReaction() );
                result.setIntendedBatchAdditionOrder(additionOrder);
   
                // Set the preferred units for this new intended product
                setPreferredAmountUnits(result);
            } catch (Exception e) {
                // should never throw this as we are in control of the batch type.
                // development only error.
                log.error("Failed to create Intended Product.\n" + e.toString(), e);
            }

	    return result;
	}
	
	
	public void setPreferredAmountUnits(StoicModelInterface batchModel)
	{
		//USER2 1.1 code ?
	}
	
	/**
	 * 
	 * @return List of strings representing the precursors of each reactant and 
	 * 		   each reactant in that structurally contribute to the final product.
	 */
	public ArrayList<String> getPreCursorsForReaction() {
	    ArrayList<String> tmpList = new ArrayList<String>();
	    List<String> smPrecursors = null;
	    // Process all the Reagents to ensure the order is correct
	    for (Iterator<StoicModelInterface> i = getReactantBatches().iterator(); i.hasNext();) {
	        // Does this reactant have precursors?  If so put them into 
	        // the precursor list instead of the reactant.
	    	MonomerBatchModel rb = (MonomerBatchModel) i.next();
	        smPrecursors = rb.getPrecursors();
	        if (smPrecursors != null && smPrecursors.size() > 0) {
                for (Iterator<String> j = smPrecursors.iterator(); j.hasNext();) {
                    String testStr = (String) j.next();
                    if (tmpList.indexOf(testStr) < 0)
                    	tmpList.add( testStr );
                }
	        } else {
	            // No precursors to add so we will try to add the reactant designator instead. 
	            // If the regNumber nor batch designator exist we can't add anything.
	            if (rb.getCompound().getRegNumber() != null && rb.getCompound().getRegNumber().length() > 0) {
					if (tmpList.indexOf(rb.getCompound().getRegNumber()) < 0) {
						tmpList.add(rb.getCompound().getRegNumber());
					}
// Notified that batch numbers shouldn't appear in precursor list.                    
//	            } else if (rb.getBatchNumberAsString() != null && rb.getBatchNumberAsString().length() > 0) {
//                    if (tmpList.indexOf(rb.getBatchNumberAsString()) < 0) tmpList.add( rb.getBatchNumberAsString() );
	            }
	        }
	        // clean up.
	        smPrecursors.clear();
	        smPrecursors = null;
	        rb = null;
	    }

	    return tmpList;
	}

	/**
	 * This method updates the precursor info for all Products.
	 * This is only required for MedChem/Conception. Parallel experiment precurosrs are computed in different way.
	 * 
	 * @throws IllegalArgumentException if it encounters anything but ProductBatchModel when calling
	 *         getIntendedProductBatches()
	 * 
	 */
	public void updateIntendedProductPrecursors() {
		ArrayList<String> precursors = getPreCursorsForReaction();
		for (StoicModelInterface stoichItem : getIntendedProductBatches()) {
			if(stoichItem instanceof ProductBatchModel) {
				((ProductBatchModel) stoichItem).setPrecursors(precursors);
			} else if( stoichItem instanceof BatchesList<?>){
				throw new IllegalArgumentException("Parallel Experiments cannot use updateIntendedProductPrecursors.");
			} else {
				throw new IllegalArgumentException("Non-ProductBatchModel found when calling getIntendedProductBatches().");
			}
		}

	}

	/*
	 * This method triggers calc of Moles based on user entered nEquivs and also calc of nEquiv based on user entered Moles and
	 * other calcs like yield and theoretical Wt for a particular product batch.(in Products Table ) nMoles = nEquiv *
	 * limitingReagentMoles
	 */
	public void recalculateProductMolesandEquivs(StoicModelInterface prodBatch) {
		if (isEditablePage()) {
			StoicModelInterface limitingReag = findLimitingReagent();

			if (limitingReag != null) {
				addProductIfNeeded(); // used to keep a place marker for stoich products if there isn't one
				// allows calcs to be setup such that the user can see the effect of
				// their changes.

				updateIntendedProductPrecursors();

				// if intd prod nMoles is set zero then set its value back to limiting
				// reagent nMoles and equivs as 1.0
				if (limitingReag != null && prodBatch.getStoicWeightAmount().GetValueInStdUnitsAsDouble() == 0.0) {
					// set nMoles
					AmountModel amtTemp = (AmountModel) limitingReag.getStoicMoleAmount().deepClone();
					amtTemp.setCalculated(true);
					// Causes batch to recalculate amounts based on Mole change.
					prodBatch.setStoicMoleAmount(amtTemp);

					if (limitingReag.shouldApplySigFigRules())
						prodBatch.applyLatestSigDigits(amtTemp.getSigDigits());
					else
						prodBatch.applyLatestSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);

					// set nEquivs
					AmountModel amtTemp2 = (AmountModel) limitingReag.getStoicRxnEquivsAmount().deepClone();
					amtTemp2.setCalculated(true);
					prodBatch.setStoicRxnEquivsAmount(amtTemp2);

					prodBatch.recalcAmounts();

				} else if (limitingReag != null && prodBatch.isAutoCalcOn()) {
					AmountModel amtTemp = (AmountModel) limitingReag.getStoicMoleAmount().deepClone();
					// prodBatch.setTheoreticalMoleAmount(amtTemp);

					// if nEquiv set by the user.recalc moleAmount based on nEquiv and limiting reagent nMoles.
					if (prodBatch.getStoicMoleAmount().isCalculated() && !prodBatch.getStoicRxnEquivsAmount().isCalculated()) {
						// calc new mole amount
						double newValue = BatchUtils.calcMolesWithEquivalents(amtTemp, prodBatch.getStoicRxnEquivsAmount());
						amtTemp.setValue(newValue);
						amtTemp.setCalculated(true);

						// Causes batch to recalculate amounts based on Mole change.
						prodBatch.setStoicMoleAmount(amtTemp);
						if (limitingReag.shouldApplySigFigRules())
							prodBatch.applyLatestSigDigits(amtTemp.getSigDigits());
						else
							prodBatch.applyLatestSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
					}
					// if moleAmount set by the user.
					// recalc nEquiv based on moleAmount and limiting reagent nMoles.
					else if (!prodBatch.getStoicMoleAmount().isCalculated() && prodBatch.getStoicRxnEquivsAmount().isCalculated()) {
						// calc new Equiv amount
						double newValue = BatchUtils.calcEquivalentsWithMoles(prodBatch.getStoicMoleAmount(), amtTemp);
						amtTemp.setValue(newValue);
						amtTemp.setCalculated(true);

						// Causes batch to recalculate amounts based on Equivs change.
						prodBatch.setStoicRxnEquivsAmount(amtTemp);
						if (limitingReag.shouldApplySigFigRules())
							prodBatch.applyLatestSigDigits(amtTemp.getSigDigits());
						else
							prodBatch.applyLatestSigDigits(CeNNumberUtils.DEFAULT_SIG_DIGITS);
					}

					prodBatch.recalcAmounts(); // Updates the theo weight and yield
				}
			}
		}
	}

	// Method to calc rxnEquivs for Reactants batches
	protected void recalculateRxnEquivsBasedOnLimitingReagentMoles(StoicModelInterface limitingAB) {
		if (isEditablePage() && limitingAB != null && limitingAB.isStoicLimiting()) {
			for (StoicModelInterface b : getReagentBatches()) {
				if (!b.equals(limitingAB) && b.isAutoCalcOn()) {
					if (limitingAB.getStoicRxnEquivsAmount().doubleValue() > 0.0
							&& limitingAB.getStoicMoleAmount().doubleValue() > 0.0) {
						// Need nto calculated Amoutns for SOLVENTS
						if (!b.getStoicReactionRole().equals(BatchType.SOLVENT.toString()))
							recalculateRxnEquivsForBatch(limitingAB, b);
					} else if (b.getStoicMoleAmount().doubleValue() > 0.0) {
						b.getStoicMoleAmount().reset();
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return a List of Reagent batches for the displayed reaction step. 
	 *         The list includes reactants, reagents, solvents and starting
	 *         materials where starting materials are a discontinued type from the Chemistry Workbook.
	 */
	public List<StoicModelInterface> getReagentBatches() {
		return getBatches(BatchType.REACTANT_ORDINAL | 
		                  BatchType.REAGENT_ORDINAL | 
		                  BatchType.SOLVENT_ORDINAL | 
		                  BatchType.START_MTRL_ORDINAL);
	}

	/**
	 * 
	 * @return a List of Reactant batches for the displayed reaction step. 
	 *         Only Reactant batches are returned. Use getReagents() if
	 *         you need all reagent types.
	 */
	public List<StoicModelInterface> getReactantBatches() {
		return getBatches(BatchType.REACTANT_ORDINAL);
	}

	/**
	 * 
	 * @return a list of StoicModelInterface object: BatchesList and BatchModel 
	 *         that are of a product type: ACTUAL or INTENDED for this reaction step
	 */
	public List<StoicModelInterface> getProductBatches() {
		List<StoicModelInterface> result = null;
		if (this.pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
			result = getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);
		} else {
			result = getBatches(BatchType.INTENDED_PRODUCT_ORDINAL);
		}
		Collections.sort(result, new ComparatorStoicAdditionOrder());
		return result;
	}
	
	/**
	 * 
	 * @return list of StoicModelInterface object: BatchesList and BatchModel
	 *         that are of product type: INTENDED only for this reaction step
	 */
	public List<StoicModelInterface> getIntendedProductBatches() {
		List<StoicModelInterface> result = new ArrayList<StoicModelInterface>();
		if (this.pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
			return result;
		} else {
			result = getBatches(BatchType.INTENDED_PRODUCT_ORDINAL);
		}
		
		Collections.sort(result, new ComparatorStoicAdditionOrder());
		return result;
	}

	/**
	 * 
	 * @return a list of StoicModelInterface object: BatchesList and BatchModel 
	 *         that are of a product type: ACTUAL or INTENDED only for this reaction step
	 */
	public List<StoicModelInterface> getProductBatchesSortedByAdditionOrder() {
		List<StoicModelInterface> result = null;
		if (this.pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
			result = getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);
		} else {
			result = getBatches(BatchType.INTENDED_PRODUCT_ORDINAL);
		}
		Collections.sort(result, new ComparatorStoicAdditionOrder());
		return result;
	}

	/**
	 * 
	 * @return all batches in stoich model
	 */
	public List<StoicModelInterface> getBatches() {
		List<StoicModelInterface> result = null;
		if (this.pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
			result = getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);
		} else {
			result = getBatches(BatchType.REACTANT_ORDINAL |
								BatchType.REAGENT_ORDINAL | 
								BatchType.SOLVENT_ORDINAL | 
								BatchType.START_MTRL_ORDINAL |
								BatchType.INTENDED_PRODUCT_ORDINAL);
		}
		return result;
	}

	/**
	 * Use the BatchType class to feed ordinal values to this method. You can OR them together to get multiple types returned. Or
	 * use -1 to get all batches returned. Batches are returned in order of addition.
	 * 
	 * Note: what is returned can be a BatchesList or BatchModel.  Hence the use of the StoicModelInterface
	 * 
	 * @param batchTypes
	 *            OR'ed bitmap of ORDINAL values in BatchType
	 * @return List of requested batches or an empty list if no batches matched the criteria
	 */
	public List<StoicModelInterface> getBatches(int batchTypes) {
		log.debug("PCeNStoichCalculator.getBatches().enter");
		ArrayList<StoicModelInterface> stoicList = new ArrayList<StoicModelInterface>();
		if (rxnStepModel != null) {
			// adding MonomerList Objects
			stoicList.addAll(rxnStepModel.getMonomers());
			//System.out.println("Monomer Lists size:" + rxnStepModel.getMonomers().size());
			// adding MonomerBatchModel objects
			stoicList.addAll(rxnStepModel.getBatchesFromStoicBatchesList());
			//System.out.println("Stoic element batches size:" + rxnStepModel.getBatchesFromStoicBatchesList().size());
			// adding Intended and Actual ProductBatchModel objects
			stoicList.addAll(rxnStepModel.getProductBatches());
			//System.out.println("Product batches size:" + rxnStepModel.getProductBatches().size());

			// Collections.sort(result, new ComparatorStoicAdditionOrder());
		}

		ArrayList<StoicModelInterface> bList = new ArrayList<StoicModelInterface>();
		//System.out.println("Complete Stoiclist size:" + stoicList.size());
		//System.out.println("BatchType filter:" + batchTypes);
		for (StoicModelInterface ab : stoicList) {
			if (ab == null || ab.getBatchType() == null) {
				continue;
			}
			if ((ab.getBatchType().getOrdinal() & batchTypes) != 0) {
				// System.out.println("Element ->BatchType:"+ab.getBatchType().toString()+" ordinal
				// is:"+ab.getBatchType().getOrdinal()+" -- matched");
				bList.add(ab);
			} else {
				// System.out.println("Element ->BatchType:"+ab.getBatchType().toString()+" ordinal
				// is:"+ab.getBatchType().getOrdinal());
			}
		}
		//System.out.println("getBatches(int batchTypes).Total filtered batches returned:" + bList.size());
		log.debug("PCeNStoichCalculator.getBatches().exit");
		return bList;
	}

	public List<StoicModelInterface> getActualProductBatches() {
		return getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);
	}

	
	private void updateMonomerAndProductListLevelFlags()
	{
		//There are no lists in other type of experiments
		if(!pageType.equals(CeNConstants.PAGE_TYPE_PARALLEL)) 
			return;
		log.debug("updateMonomerAndProductListLevelFlags().enter");
		//sync monomer lists
		for(BatchesList<MonomerBatchModel> list : this.rxnStepModel.getMonomers()) {
			list.updateAllListLevelFlags();
		}
		//sync product lists
		for(BatchesList<ProductBatchModel> list : this.rxnStepModel.getProducts()) {
			list.updateAllListLevelFlags();
		}
		log.debug("updateMonomerAndProductListLevelFlags().exit");
	}

}
