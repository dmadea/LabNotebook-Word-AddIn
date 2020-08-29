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
 * Created on Nov 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.reagents;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ReagentTransferObj {
	public ReagentTransferObj() {
		ChemicalName = new String();
		BatchNumber = new String();
		RegistrationNumber = new String();
		MolecularFormula = new String();
		ChiralityCode = new String();
		GeneralComment = new String();
		StorageComment = new String();
		HazardComment = new String();
		SDFile = new String();
		CompoundParent = new String();
		MolecularWeight = 0.0;
	}

	private String ChemicalName;
	private String BatchNumber;
	private String RegistrationNumber; // will be the CAS Number, MFCD Number
	// MDL number, etc. Will also be PF
	// numbers.
	private String MolecularFormula;
	private String ChiralityCode;
	private String GeneralComment;
	private String StorageComment;
	private String HazardComment;
	private String VendorName;
	private String SDFile;
	private String SaltCode;
	private String CompoundParent;
	private double MolecularWeight;

	/**
	 * @return Returns the batchNumber.
	 */
	public String getBatchNumber() {
		return BatchNumber;
	}

	/**
	 * @param batchNumber
	 *            The batchNumber to set.
	 */
	public void setBatchNumber(String batchNumber) {
		BatchNumber = batchNumber;
	}

	/**
	 * @return Returns the chemicalName.
	 */
	public String getChemicalName() {
		return ChemicalName;
	}

	/**
	 * @param chemicalName
	 *            The chemicalName to set.
	 */
	public void setChemicalName(String chemicalName) {
		ChemicalName = chemicalName;
	}

	/**
	 * @return Returns the chiralityCode.
	 */
	public String getChiralityCode() {
		return ChiralityCode;
	}

	/**
	 * @param chiralityCode
	 *            The chiralityCode to set.
	 */
	public void setChiralityCode(String chiralityCode) {
		ChiralityCode = chiralityCode;
	}

	/**
	 * @return Returns the generalComment.
	 */
	public String getGeneralComment() {
		return GeneralComment;
	}

	/**
	 * @param generalComment
	 *            The generalComment to set.
	 */
	public void setGeneralComment(String generalComment) {
		GeneralComment = generalComment;
	}

	/**
	 * @return Returns the hazardComment.
	 */
	public String getHazardComment() {
		return HazardComment;
	}

	/**
	 * @param hazardComment
	 *            The hazardComment to set.
	 */
	public void setHazardComment(String hazardComment) {
		HazardComment = hazardComment;
	}

	/**
	 * @return Returns the molecularFormula.
	 */
	public String getMolecularFormula() {
		return MolecularFormula;
	}

	/**
	 * @param molecularFormula
	 *            The molecularFormula to set.
	 */
	public void setMolecularFormula(String molecularFormula) {
		MolecularFormula = molecularFormula;
	}

	/**
	 * @return Returns the registrationNumber.
	 */
	public String getRegistrationNumber() {
		return RegistrationNumber;
	}

	/**
	 * @param registrationNumber
	 *            The registrationNumber to set.
	 */
	public void setRegistrationNumber(String registrationNumber) {
		RegistrationNumber = registrationNumber;
	}

	/**
	 * @return Returns the sDFile.
	 */
	public String getSDFile() {
		return SDFile;
	}

	/**
	 * @param file
	 *            The sDFile to set.
	 */
	public void setSDFile(String file) {
		SDFile = file;
	}

	/**
	 * @return Returns the storageComment.
	 */
	public String getStorageComment() {
		return StorageComment;
	}

	/**
	 * @param storageComment
	 *            The storageComment to set.
	 */
	public void setStorageComment(String storageComment) {
		StorageComment = storageComment;
	}

	/**
	 * @return Returns the vendorName.
	 */
	public String getVendorName() {
		return VendorName;
	}

	/**
	 * @param vendorName
	 *            The vendorName to set.
	 */
	public void setVendorName(String vendorName) {
		VendorName = vendorName;
	}

	/**
	 * @return Returns the molecularWeight.
	 */
	public double getMolecularWeight() {
		return MolecularWeight;
	}

	/**
	 * @param molecularWeight
	 *            The molecularWeight to set.
	 */
	public void setMolecularWeight(double molecularWeight) {
		MolecularWeight = molecularWeight;
	}

	/**
	 * @return Returns the compoundParent.
	 */
	public String getCompoundParent() {
		return CompoundParent;
	}

	/**
	 * @param compoundParent
	 *            The compoundParent to set.
	 */
	public void setCompoundParent(String compoundParent) {
		this.CompoundParent = compoundParent;
	}

	/**
	 * @return Returns the saltCode.
	 */
	public String getSaltFormCode() {
		return SaltCode;
	}

	/**
	 * @param saltCode
	 *            The saltCode to set.
	 */
	public void setSaltFormCode(String saltCode) {
		SaltCode = saltCode;
	}
}
