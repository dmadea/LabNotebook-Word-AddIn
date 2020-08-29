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

//this class is a place holder for cen_batch_amounts table load
public class CeNBatchAmountsModel  extends CeNAbstractModel{

	private static final long serialVersionUID = -2107991968193231770L;
	
	String batchKey;
	String pageKey;
	private final AmountModel moleAmount = new AmountModel(UnitType.MOLES); // Unitless amount indicating how much of an Avagadro's
	private final AmountModel weightAmount = new AmountModel(UnitType.MASS); // AmountModel will contain unit conversions original amount
	private final AmountModel loadingAmount = new AmountModel(UnitType.LOADING); // Loading is generally mmol/gram - tackles resins
	private final AmountModel volumeAmount = new AmountModel(UnitType.VOLUME); // AmountModel in volume
	private final AmountModel densityAmount = new AmountModel(UnitType.DENSITY); // Density of compound in g/mL
	private final AmountModel molarAmount = new AmountModel(UnitType.MOLAR); // Describes concentration of batch
	private final AmountModel purityAmount = new AmountModel(UnitType.SCALAR, 100); // % Purity info 100 - 0
	private final AmountModel rxnEquivsAmount = new AmountModel(UnitType.SCALAR, 1.0, 1.0); // Represents equivalants of compound to a
	private final AmountModel soluteAmount = new AmountModel(UnitType.MASS);
	private final AmountModel totalVolume = new AmountModel(UnitType.VOLUME);
	private final AmountModel totalWeight = new AmountModel(UnitType.MASS);
	private final AmountModel totalMolarity = new AmountModel(UnitType.MOLAR); // Total Amount made molarity
	private final AmountModel previousMolarAmount = new AmountModel(UnitType.MOLAR); // Describes concentration of batch before
	private final AmountModel amountNeeded = new AmountModel(UnitType.MOLES);
	private final AmountModel extraNeeded = new AmountModel(UnitType.MOLES);
	private final AmountModel deliveredWeight = new AmountModel(UnitType.MASS);
	private final AmountModel deliveredVolume = new AmountModel(UnitType.VOLUME);
	private final AmountModel theoreticalWeightAmount = new AmountModel(UnitType.MASS);
	private final AmountModel theoreticalMoleAmount = new AmountModel(UnitType.MOLES);
	private final AmountModel theoreticalYieldPercentAmount = new AmountModel(UnitType.SCALAR, "-1.0");
	
	public CeNBatchAmountsModel()
	{
		
	}
	public AmountModel getAmountNeeded() {
		return amountNeeded;
	}


	public String getBatchKey() {
		return batchKey;
	}


	public void setBatchKey(String batchKey) {
		this.batchKey = batchKey;
	}


	public AmountModel getDeliveredVolume() {
		return deliveredVolume;
	}


	public AmountModel getDeliveredWeight() {
		return deliveredWeight;
	}


	public AmountModel getDensityAmount() {
		return densityAmount;
	}


	public AmountModel getExtraNeeded() {
		return extraNeeded;
	}


	public AmountModel getLoadingAmount() {
		return loadingAmount;
	}


	public AmountModel getMolarAmount() {
		return molarAmount;
	}


	public AmountModel getMoleAmount() {
		return moleAmount;
	}


	public String getPageKey() {
		return pageKey;
	}


	public void setPageKey(String pageKey) {
		this.pageKey = pageKey;
	}


	public AmountModel getPreviousMolarAmount() {
		return previousMolarAmount;
	}


	public AmountModel getPurityAmount() {
		return purityAmount;
	}


	public AmountModel getRxnEquivsAmount() {
		return rxnEquivsAmount;
	}


	public AmountModel getSoluteAmount() {
		return soluteAmount;
	}


	public AmountModel getTheoreticalMoleAmount() {
		return theoreticalMoleAmount;
	}


	public AmountModel getTheoreticalWeightAmount() {
		return theoreticalWeightAmount;
	}


	public AmountModel getTheoreticalYieldPercentAmount() {
		return theoreticalYieldPercentAmount;
	}


	public AmountModel getTotalMolarity() {
		return totalMolarity;
	}


	public AmountModel getTotalVolume() {
		return totalVolume;
	}


	public AmountModel getTotalWeight() {
		return totalWeight;
	}


	public AmountModel getVolumeAmount() {
		return volumeAmount;
	}


	public AmountModel getWeightAmount() {
		return weightAmount;
	}


	public String toXML()
	{
		return "";
	}
}
