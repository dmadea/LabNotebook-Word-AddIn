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

import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;

import java.util.List;

public interface StoicModelInterface {
	
	//Following method will help to display LIST field in stoic
	public boolean isList();
	
	//Following method will help to display LABEL field in stoic
	public String getStoicLabel();
	public void setStoicLabel(String label);
	
	//Following 2 methods will help to display mMOLES field in stoic
	public void setStoicMoleAmount(AmountModel molAmt);
	
	public AmountModel getStoicMoleAmount();
	
	//Following 2 methods will help to display EQ field in stoic
	public void setStoicRxnEquivsAmount(AmountModel rxnEquivAmt);
	
	public AmountModel getStoicRxnEquivsAmount();
	
	//Following 2 methods will help to display LIMITING field in stoic
	public void setStoicLimiting(boolean isLimiting);
	
	public boolean isStoicLimiting();
	
	//Following 2 methods will help to display RXN_ROLE field in stoic
	public void setStoicReactionRole(String rxnRole);
	
	public String getStoicReactionRole();
	
	//Following 2 methods will help to display MOLARITY field in stoic
	public void setStoicMolarAmount(AmountModel molarAmt);
	
	public AmountModel getStoicMolarAmount();

	//Following 2 methods will help to display SOLUTE AMT field in stoic
	public void setStoicSoluteAmount(AmountModel soluteAmt);
	
	public AmountModel getStoicSoluteAmount();
	
	//Following 2 methods will help to display SOLUTE field in stoic
	public void setStoicSolute(String solute);
	
	public String getStoicSolute();
	
	//Following are additional method more specific to MonomerBatchModel.
	//BatchesList returns empty string for these get methods
	
	//For ChemicalName
	public String getStoicChemicalName();
	public void setStoicChemicalName(String chemName);
	
	//For MolecularFormula
	public String getStoicMolecularFormula();
	public void setStoicMolecularFormula(String molFormula);
	
	//For CompoundId
	public String getStoicCompoundId();
	public void setStoicCompoundId(String compoundId);
	
	//For NBKBatchNo
	public BatchNumber getStoicBatchNumber();
	public void setStoicBatchNumber(BatchNumber nbkBatchNo);
	
	//For MolecularWeight
	public AmountModel getStoicMolecularWeightAmount();
	public void setStoicMolecularWeightAmount(AmountModel molWeight);
	
	//For Weight
	public AmountModel getStoicWeightAmount();
	public void setStoicWeightAmount(AmountModel weight);
	
	//For Density
	public AmountModel getStoicDensityAmount();
	public void setStoicDensityAmount(AmountModel density );
	
	//For Volume
	public AmountModel getStoicVolumeAmount();
	public void setStoicVolumeAmount(AmountModel volume );
	
	//For BarCode
	public String getBarCode();
	public void setBarCode(String barCode );
	
	//For Hazards
	public String getStoicHazardsComments();
	public void setStoicHazardsComments(String hazards );
	
	//For Stoich Comments
	public String getStoichComments();
	public void setStoichComments(String comments);
	
	//For TotalWeight
	public AmountModel getTotalWeight();
	public void setTotalWeight(AmountModel totalWeight );
	
	//For TotalVolume
	public AmountModel getTotalVolume();
	public void setTotalVolume(AmountModel totalVolume );
	
		
	/**
	 * @return the transactionOrder
	 */
	public int getStoicTransactionOrder() ;

	/**
	 * @param transactionOrder
	 *            the transactionOrder to set
	 */
	public void setStoicTransactionOrder(int transactionOrder) ;
	
	
	public void markToBeDeleted(boolean deleted);
	
	public void clearStoicData();
	
	public String getStoicBatchCASNumber();
	public void setStoicBatchCASNumber(String casNumber);
	
	public Double getStoicBatchSaltEquivs();
	public void setStoicBatchSaltEquivs(double salteq);
	
	public SaltFormModel getStoicBatchSaltForm();
	public void setStoicBatchSaltform(SaltFormModel salt);
	
	public AmountModel getStoicPurityAmount();
	public void setStoicPurityAmount(AmountModel purityModel);
	
		
	public AmountModel getStoicLoadingAmount();
	public void setStoicLoadingAmount(AmountModel loadingModel);
	
	
	public String getGUIDKey();
	
	public boolean isAutoCalcOn();
	
	public boolean shouldApplySigFigRules();
	
	public boolean shouldApplyDefaultSigFigs();
	
	public void applyLatestSigDigits(int defaultSigs);
	
	public void setPreviousMolarAmount(AmountModel molarAmount);
	
	public AmountModel getPreviousMolarAmount();
	
	public void applySigFigRules(AmountModel amt, List<AmountModel> amts);
	
	public BatchType getBatchType();
	
	public void recalcAmounts();
	
	//ProductBatch specific methods for Stoich
	public AmountModel getTheoreticalWeightAmount();
	public void setTheoreticalWeightAmount(AmountModel theoreticalWeightAmount);
	
	public AmountModel getTheoreticalMoleAmount();
	public void setTheoreticalMoleAmount(AmountModel theoreticalMoleAmount);
	
	public String getStoicSolventsAdded() ;

	public void setStoicSolventsAdded(String solvBatchKey) ;
	
//	For NBKBatchNo
	public String getStoicBatchNumberAsString();
	public void setStoicBatchNumber(String nbkBatchNo) throws InvalidBatchNumberException;
	
	//interface to get list size.
	public int getStoicListSize();
	
	public void resetAmount(String amountName);

	public ParentCompoundModel getStoichCompoundModel();
}
