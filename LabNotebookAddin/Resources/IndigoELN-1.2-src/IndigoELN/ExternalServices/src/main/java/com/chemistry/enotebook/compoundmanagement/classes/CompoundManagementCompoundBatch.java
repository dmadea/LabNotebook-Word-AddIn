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
package com.chemistry.enotebook.compoundmanagement.classes;

import java.io.Serializable;

public class CompoundManagementCompoundBatch implements Serializable {
	
	private static final long serialVersionUID = 2993742640879105917L;
	
	private String CompoundNumber;
	private double MolecularWeight;
	private String MolecularFormula;
	private String CompoundHazardComment;
	private String CompoundParent;
	private String AmountMadeUnitCode;
	private double AmountMade;
	private String BatchComment;
	private String SaltCode;

	public CompoundManagementCompoundBatch(String compoundNumber,
			double molecularWeight, String molecularFormula,
			String compoundHazardComment, String compoundParent,
			String amountMadeUnitCode, double amountMade, String batchComment,
			String saltCode) {
		CompoundNumber = compoundNumber;
		MolecularWeight = molecularWeight;
		MolecularFormula = molecularFormula;
		CompoundHazardComment = compoundHazardComment;
		CompoundParent = compoundParent;
		AmountMadeUnitCode = amountMadeUnitCode;
		AmountMade = amountMade;
		BatchComment = batchComment;
		SaltCode = saltCode;
	}

	public String getCompoundNumber() {
		return CompoundNumber;
	}

	public void setCompoundNumber(String compoundNumber) {
		CompoundNumber = compoundNumber;
	}

	public double getMolecularWeight() {
		return MolecularWeight;
	}

	public void setMolecularWeight(double molecularWeight) {
		MolecularWeight = molecularWeight;
	}

	public String getMolecularFormula() {
		return MolecularFormula;
	}

	public void setMolecularFormula(String molecularFormula) {
		MolecularFormula = molecularFormula;
	}

	public String getCompoundHazardComment() {
		return CompoundHazardComment;
	}

	public void setCompoundHazardComment(String compoundHazardComment) {
		CompoundHazardComment = compoundHazardComment;
	}

	public String getCompoundParent() {
		return CompoundParent;
	}

	public void setCompoundParent(String compoundParent) {
		CompoundParent = compoundParent;
	}

	public String getAmountMadeUnitCode() {
		return AmountMadeUnitCode;
	}

	public void setAmountMadeUnitCode(String amountMadeUnitCode) {
		AmountMadeUnitCode = amountMadeUnitCode;
	}

	public double getAmountMade() {
		return AmountMade;
	}

	public void setAmountMade(double amountMade) {
		AmountMade = amountMade;
	}

	public String getBatchComment() {
		return BatchComment;
	}

	public void setBatchComment(String batchComment) {
		BatchComment = batchComment;
	}

	public String getSaltCode() {
		return SaltCode;
	}

	public void setSaltCode(String saltCode) {
		SaltCode = saltCode;
	}
}
