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
package com.chemistry.enotebook.client.utils;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitFactory2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;

import java.util.*;
import java.util.regex.Pattern;

//This class will have most of the stuff StoichiometryModel1.1 class does.
//Also look at PCeNStoichCalculator.java 
public class ReactionStepModelUtils {

	public void insertBatchIntoAdditionOrder(ReactionStepModel step, BatchType type, int position )
	{
		StoicModelInterface model = new MonomerBatchModel(type);
		List<StoicModelInterface> stoicList = step.getStoicElementListInTransactionOrder();
		if (position >= 0 && position < stoicList.size() - 1) {
			model.setStoicTransactionOrder(position);
			stoicList.add(position, model);
		} else if (position >= stoicList.size() - 1) {
			// append batch to list.
			model.setStoicTransactionOrder(stoicList.size());
			stoicList.add(model);
		}
		step.addStoicModelInterface(model);
		updateAdditionOrder(stoicList);
	}
	
	
	private static void updateAdditionOrder(List<StoicModelInterface> batches)
	{
		for(int i = 0 ; i < batches.size() ; i ++)
		{
			StoicModelInterface model = batches.get(i);
			model.setStoicTransactionOrder(i);
		}
	}
	
	public static StoicModelInterface findLimitingReagent(ReactionStepModel rxnStepModel) {
		StoicModelInterface result = null;

		if (rxnStepModel != null) {
			List<StoicModelInterface> reagents = rxnStepModel.getStoicElementListInTransactionOrder();
			for (StoicModelInterface b : reagents) {
				if (b instanceof BatchesList<?>) {
					if (((BatchesList<?>) b).getBatchModels().size() > 0) {
						if (b.isStoicLimiting() && canBeLimiting(b)) {
							result = b;
							break;
						}
					}
				} else {
					if (b.isStoicLimiting() && canBeLimiting(b)) {
						result = b;
						break;
					}
				}
			}
		}

		return result;
	}
	
	public static boolean canBeLimiting(StoicModelInterface rb) {
		if (rb.getStoicReactionRole().equals(BatchType.SOLVENT.toString()))
			return false;
		else
			return true;
	}
	
	
	/**
	 * 
	 * @param reactantStructures -
	 *            structures on which to base reactant batches.
	 * @return list of batches containing the structures passed to the method.
	 * @throws ChemUtilInitException -
	 *             could not get a hold of the chemistry service
	 * @throws ChemUtilAccessException -
	 *             could not translate the structure properly.
	 */
	public static List<MonomerBatchModel> createReactantBatchesFromStructures(List<byte[]> reactantStructures, NotebookPageModel nbPage) throws ChemUtilInitException, ChemUtilAccessException {
		ArrayList<MonomerBatchModel> reactantBatches = new ArrayList<MonomerBatchModel>();
		BitSet addressedRxnPositions = new BitSet(reactantStructures.size());
		// Create reactants for all left over rxnComponents
		int preferredDrawingTool = MasterController.getUser().getPreferredDrawingTool();
		for (int position = 0; position < reactantStructures.size()
				&& addressedRxnPositions.cardinality() <= addressedRxnPositions.size(); position++) {
			if (!addressedRxnPositions.get(position)) {
				MonomerBatchModel rBatch = new MonomerBatchModel();
				// Set Preferred Amount Units for Amounts that have more than one unit code
				setPreferredAmountUnits(rBatch,nbPage);
				rBatch.setBatchType(BatchType.REACTANT);
				byte[] b1 = reactantStructures.get(position);
				// CreateByNotebook flag for load sketch should be false since we do not search on reactants
				StructureLoadAndConversionUtil.loadSketch(b1, preferredDrawingTool, false,rBatch.getCompound());
				reactantBatches.add(rBatch);
			}
		}
		return reactantBatches;
	}
	
	
	public static void setPreferredAmountUnits(BatchModel batch,NotebookPageModel nbPage) {
		try {
			AmountModel amount = null;
			StringBuffer preferredUnit = null;
			String unit = null;
			int unitTypeOrdinal = UnitType.MASS_ORDINAL;
			NotebookUser user = MasterController.getUser();
			
			switch (unitTypeOrdinal) {
				case UnitType.MASS_ORDINAL:
					// get the Amount of UnitType MASS
					amount = batch.getWeightAmount();
					unit = UnitFactory2.getPreferredUnitForAmount(amount, nbPage, user);
					if (unit != null)
						preferredUnit = new StringBuffer(unit);
					if (preferredUnit != null && preferredUnit.length() > 0) {
						// creates a new unit from UnitCache and assigns it to the Amount in Batch
						Unit2 un = new Unit2(preferredUnit.toString());
						amount.setUnit(un);
						// reuse those references for the next amount to be set
						preferredUnit.setLength(0);
						amount = null;
						unit = null;
					} // end if
					break;

				case UnitType.MOLAR_ORDINAL:
					// get the Amount of UnitType MOLAR
					amount = batch.getMolarAmount();
					unit = UnitFactory2.getPreferredUnitForAmount(amount, nbPage, user);
					if (unit != null)
						preferredUnit = new StringBuffer(unit);
					if (preferredUnit != null && preferredUnit.length() > 0) {
						// creates a new unit from UnitCache and assigns it to the Amount in Batch
						Unit2 un = new Unit2(preferredUnit.toString());
						amount.setUnit(un);
						// reuse those references for the next amount to be set
						preferredUnit.setLength(0);
						amount = null;
						unit = null;
					} // end if
					break;

				case UnitType.MOLES_ORDINAL:
					// get the Amount of UnitType MOLES
					amount = batch.getMoleAmount();
					unit = UnitFactory2.getPreferredUnitForAmount(amount, nbPage, user);
					if (unit != null)
						preferredUnit = new StringBuffer(unit);
					if (preferredUnit != null && preferredUnit.length() > 0) {
						// creates a new unit from UnitCache and assigns it to the Amount in Batch
						Unit2 un = new Unit2(preferredUnit.toString());
						amount.setUnit(un);
						// reuse those references for the next amount to be set
						preferredUnit.setLength(0);
						amount = null;
						unit = null;
					} // end if
					break;

				case UnitType.VOLUME_ORDINAL:
					// get the Amount of UnitType VOLUME
					amount = batch.getVolumeAmount();
					unit = UnitFactory2.getPreferredUnitForAmount(amount, nbPage, user);
					if (unit != null)
						preferredUnit = new StringBuffer(unit);
					if (preferredUnit != null && preferredUnit.length() > 0) {
						// creates a new unit from UnitCache and assigns it to the Amount in Batch
						Unit2 un = new Unit2(preferredUnit.toString());
						amount.setUnit(un);
						// reuse those references for the next amount to be set
						preferredUnit.setLength(0);
						amount = null;
						unit = null;
					} // end if
					break;

			} // end switch

		} catch (UserPreferenceException usExp) {
			// could not make CeNErrorHandler to compile ...
			usExp.printStackTrace();
		}
	}
	
	
	/**
	 * @param allBatches -
	 *            primary list of all reagents
	 * @param reactantBatches -
	 *            list of only Reactant Type Reagents used to compare to the primary.
	 * @return List of batches in allBatches whose structure <b>is not<b> found in reactantBatches. Empty if all are matched.
	 *         Allows for multiple instances of a structure occurring in each. Matches on first come first served basis
	 */
	public static List<MonomerBatchModel> getUnmatchedReactantBatches(List<MonomerBatchModel> allBatches, 
	                                                                  List<MonomerBatchModel> reactantBatches,
	                                                                  NotebookPageModel nbPage) 
		throws ChemUtilInitException, ChemUtilAccessException 
	{
		List<MonomerBatchModel> unmatchedBatches = new ArrayList<MonomerBatchModel>();
		ChemistryDelegate chemDelegate = new ChemistryDelegate();
		for (int i = 0; i < allBatches.size(); i++) {
			MonomerBatchModel comp = allBatches.get(i);
			if (comp.getBatchType().equals(BatchType.REACTANT)) {
				byte[] b1 = comp.getCompound().getNativeSketch();
				boolean matched = false;
				for (int j = 0; j < reactantBatches.size() && !matched; j++) {
					MonomerBatchModel rb = reactantBatches.get(j);
					if (rb.getBatchType().equals(BatchType.REACTANT) && !unmatchedBatches.contains(rb)) {
						matched = chemDelegate.areMoleculesEqual(b1, rb.getCompound().getNativeSketch());
					}
				}
				if (!matched) {
					// Set Preferred Amount Units for Amounts that have more than one unit code
					setPreferredAmountUnits(comp,nbPage);
					// reset chlorac info if struc doesn't match ( i.e new struc and needs to be tested again)
					comp.setTestedForChloracnegen(false);
					comp.setChloracnegenFlag(false);
					unmatchedBatches.add(comp);
				}// end if
			}
		}
		return unmatchedBatches;
	}
	
	
	/**
	 * 
	 * @param batchesInNewOrder -
	 *            must include matches for all reactants.
	 */
	public static void updateReactantOrderBasedOnList(List<MonomerBatchModel> batchesInNewOrder,
	                                                  ReactionStepModel stepModel) throws ChemUtilInitException, ChemUtilAccessException {
		ChemistryDelegate chemDelegate = new ChemistryDelegate();
		if (chemDelegate != null) {
			StoicModelInterface rb = null;
			List<StoicModelInterface> reactants = stepModel.getStoicModelList();
			List<StoicModelInterface> matches = new ArrayList<StoicModelInterface>();
			int[] reactantPositionsList = new int[reactants.size()];
			for (int i = 0; i < reactants.size(); i++) {
				reactantPositionsList[i] = reactants.get(i).getStoicTransactionOrder();
			}
			boolean matched = false;
			for (MonomerBatchModel mbm : batchesInNewOrder) {
				matched = false;
				byte[] sketch = mbm.getCompound().getNativeSketch();
				for (Iterator<StoicModelInterface> count = reactants.iterator(); count.hasNext() && !matched;) {
					StoicModelInterface comp = count.next();
					byte[] b1 = comp.getStoichCompoundModel().getNativeSketch();
					if (!matches.contains(comp)) {
						matched = chemDelegate.areMoleculesEqual(b1, sketch);
						if (matched)
							matches.add(comp);
					}
				}
			}
			// Now that we know all the matches we can swap positions.
			if (matches.size() == reactantPositionsList.length) {
				boolean found = false;
				int desiredPos = -1;
				for (int i = 0; i < matches.size(); i++) {
					rb = matches.get(i);
					desiredPos = reactantPositionsList[i];
					if (rb.getStoicTransactionOrder() != desiredPos) {
						// get reactant with desired position number
						found = false; 
						// logical error found was not used in loop below. Since no two reactants can be added at the
						// same time, I figured the exit condition would hold and put it back. - ajk 6/1/2010 
						for (Iterator<StoicModelInterface> count = reactants.iterator(); count.hasNext() && !found;) {
							StoicModelInterface comp = count.next();
							if (comp.getStoicTransactionOrder() == desiredPos) {
								stepModel.swapBatchPositionOrder(rb, comp);
								found = true;
							}
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Run through supplied list of rxnComponents and either match to a current stoich reactant, create a new one or remove/replace
	 * excess/existing reactants. Modifies stoichiometry to match the added reactants. Places all reactants at the start of the
	 * stoich and appends in order of occurance any reagents and solvents.
	 * 
	 * @param rxnReactants -
	 *            ordered list of reactants matching left to right progression of scheme through the reaction components.
	 * @return List of batches that represent the proposed reactants in stoichModel. The stoich model has not been altered.
	 */
	public static void syncReactants(List<MonomerBatchModel> reactionReactants, ReactionStepModel stepModel) throws ChemUtilInitException, ChemUtilAccessException {
		// Make all RxnReactants as REACTANT
		for (MonomerBatchModel rb : reactionReactants)
			rb.setBatchType(BatchType.REACTANT);
		
		ChemistryDelegate chemDelegate = new ChemistryDelegate();
		// Use an array to track the matched batches using rxnPosition
		ArrayList<MonomerBatchModel> matchedRxnPositions = new ArrayList<MonomerBatchModel>();
		List<MonomerBatchModel> curReagents = stepModel.getReagentBatches();
		
		//Convert the ReagetnBatches to MonomerBatchModels
		//List rxnReactants = CeN11To12ConversionUtils.getReagentsListConverted(reactionReactants);
//		List<MonomerBatchModel> rxnReactants = reactionReactants;
		// Run through reaction components matching where we can.
		// Position is either moving a current or inserting the unmatched into
		// When no match is found put the components in a list to vet later.
		for (int position = 0; position < reactionReactants.size(); position++) {
			boolean matched = false;
			MonomerBatchModel rb = reactionReactants.get(position);
			for (int i = 0; i < curReagents.size() && !matched; i++) {
				MonomerBatchModel comp = curReagents.get(i);
				if (comp.getBatchType().equals(BatchType.REACTANT) && !matchedRxnPositions.contains(comp)) {
					matched = chemDelegate.areMoleculesEqual(rb.getCompound().getNativeSketch(), 
					                                         comp.getCompound().getNativeSketch());
					if (matched) {
						matchedRxnPositions.add(position, comp);
					}
				}
			}
			if (!matched)
				matchedRxnPositions.add(position, null);

		}

		// Remove any Reactant batches that do not match
		ArrayList<MonomerBatchModel> modelsForRemove = new ArrayList<MonomerBatchModel>();
		for (MonomerBatchModel curReagentModel : curReagents) {
			if (!matchedRxnPositions.contains(curReagentModel) && curReagentModel.getBatchType().equals(BatchType.REACTANT)) {
				modelsForRemove.add(curReagentModel);
			}
		}
		for (MonomerBatchModel model : modelsForRemove) {
			stepModel.deleteStoicElement(model);
		}
		
		// Add RxnReactants that have no match and update order of those that do
		for (int position = 0; position < reactionReactants.size(); position++) {
			MonomerBatchModel rb1 = reactionReactants.get(position);
			if (matchedRxnPositions.get(position) != null) {
				rb1 = matchedRxnPositions.get(position);
				insertBatchIntoAdditionOrder(rb1, curReagents, position);
			} else {
				rb1.setTransactionOrder(position);
				addReagentBatchAt(stepModel, rb1, position);
				curReagents = stepModel.getReagentBatches();
			}
		}
	}

	public static void addReagentBatchAt(ReactionStepModel step, StoicModelInterface model, int position )
	{
		List<StoicModelInterface> stoicList = step.getStoicElementListInTransactionOrder();
		if (position >= 0 && position < stoicList.size() - 1) {
			model.setStoicTransactionOrder(position);
			stoicList.add(position, model);
		} else if (position >= stoicList.size() - 1) {
			// append batch to list.
			model.setStoicTransactionOrder(stoicList.size());
			stoicList.add(model);
		}
		step.addStoicModelInterface(model);
		updateAdditionOrder(stoicList);
	}
	
	
	protected static void insertBatchIntoAdditionOrder(MonomerBatchModel ab, List<MonomerBatchModel> batches, int additionOrder) {
		if (batches.contains(ab))
			batches.remove(ab);
		if (additionOrder >= 0 && additionOrder < batches.size() - 1) {
			ab.setTransactionOrder(additionOrder);
			batches.add(additionOrder, ab);
		} else if (additionOrder >= batches.size() - 1) {
			// append batch to list.
			ab.setTransactionOrder(batches.size());
			batches.add(ab);
		}
		// updateAdditionOrder is the only part that seems to deal with BatchesList and BatchModels
		List<StoicModelInterface> result = new ArrayList<StoicModelInterface>(); 
		Collections.addAll(result, batches.toArray(new StoicModelInterface[0]));
		updateAdditionOrder(result);
	}
	
	
	public static boolean isReactionSchemeDrawingEmpty(byte[] sketch)
	{
		try {
			ChemistryDelegate chemDelegate = new ChemistryDelegate();
			return !chemDelegate.isReaction(sketch);
		} catch (Exception e) {
//			log.debug("Empty reaction sketch", e);
			return true;
		}
	}
	
	public static ProductBatchModel createIntendedProduct(int addOrder) 
	{
	    ProductBatchModel result = null;
	     try {
                result = new ProductBatchModel();
                result.setBatchType(BatchType.INTENDED_PRODUCT);
                result.getCompound().setCreatedByNotebook(true);
                //USER2resetRxnEquivs(result);
                //USER2result.setPrecursors( getPreCursorsForReaction() );
//              result.setModified(true);
                result.setIntendedBatchAdditionOrder(addOrder);
                
                // Set the preferred units for this new intended product
                //USER2setPreferredAmountUnits(result);
            } catch (Exception e) {
                // should never throw this as we are in control of the batch type.
                // development only error.
                e.printStackTrace();
            }
	    
	    return result;
	}
	
	 public static boolean validateCASNo(String casNo){
	    	boolean validationResult = true;
//			 Create a pattern to match breaks
	        Pattern p = Pattern.compile("[-\\s]+");
//	      Split input with the pattern into three parts
	        String[] result = 
	                 p.split(casNo);
	        //Invalid cas number if splits are not equal to 3
	        if(result.length!=3)
	        	return false;
	        try{
		        int checksum = 0;
		        int caslen = result[0].length()+result[1].length();
		        System.out.println("caslen---:"+caslen);
		        char[] firstPart = result[0].toCharArray();
		        for(int i=0;i<firstPart.length;i++,caslen--){
		        	int temp = Integer.parseInt(firstPart[i]+"");
		        	checksum +=  temp*caslen;
		        }
		        char[] secondPart = result[1].toCharArray();
		        for(int i=0;i<secondPart.length;i++,caslen--){
		        	int temp = Integer.parseInt(secondPart[i]+"");
		        	checksum += temp*caslen;
		        }
		    	//System.out.println("Total----:"+checksum);
		        int moduloTen = checksum%10;
		        int thirdPart = Integer.parseInt(result[2]);
		        //matching the modulo10 result with third part
		        if (moduloTen == thirdPart) 
		        	validationResult= true;
		        else
		        	validationResult=false;
	        } catch(NumberFormatException numbererror){
	        	numbererror.printStackTrace();
	        	validationResult=false;
	        }
	    	return validationResult;
	    }
}//end of class
