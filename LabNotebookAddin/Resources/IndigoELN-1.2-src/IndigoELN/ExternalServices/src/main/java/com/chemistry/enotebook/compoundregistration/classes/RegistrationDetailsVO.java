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
package com.chemistry.enotebook.compoundregistration.classes;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDetailsVO implements Serializable {

	private static final long serialVersionUID = 89086276721001497L;

	private String globalNumber;
	private String globalCompoundNumber;
	private String batchNumber;
	private String batchTrackingNo;
	private String batchOwnerId;
	private Timestamp loadDate;
	private String notebokRef;
	private String structureComment;
	private String saltCode;
	private double saltMole;
	private String batchComment;
	private double molecularWeight;
	private String molecularFormula;
	private String stereoisomerCode;
	private double amountMade;
	private String amountMadeUnit;
	private String source;
	private String sourceDetail;
	private String supplierCode;
	private String supplierRef;

	private List<RegistrationVialDetailsVO> vials = new ArrayList<RegistrationVialDetailsVO>();

	private List<String> precursors = new ArrayList<String>();
	private List<List<String>> residualSolvents = new ArrayList<List<String>>();
	private List<List<String>> solubilitySolvents = new ArrayList<List<String>>();

	/**
	 * @return Returns the batchComment.
	 */
	public String getBatchComment() {
		return batchComment;
	}

	/**
	 * @param batchComment
	 *            The batchComment to set.
	 */
	public void setBatchComment(String batchComment) {
		this.batchComment = batchComment;
	}

	/**
	 * @return Returns the batchNumber.
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * @param batchNumber
	 *            The batchNumber to set.
	 */
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * @return Returns the batchTrackingNo.
	 */
	public String getBatchTrackingNo() {
		return batchTrackingNo;
	}

	/**
	 * @param batchTrackingNo
	 *            The batchTrackingNo to set.
	 */
	public void setBatchTrackingNo(String batchTrackingNo) {
		this.batchTrackingNo = batchTrackingNo;
	}

	/**
	 * @return Returns the globalCompoundNumber.
	 */
	public String getGlobalCompoundNumber() {
		return globalCompoundNumber;
	}

	/**
	 * @param globalCompoundNumber
	 *            The globalCompoundNumber to set.
	 */
	public void setGlobalCompoundNumber(String globalCompoundNumber) {
		this.globalCompoundNumber = globalCompoundNumber;
	}

	public String getGlobalNumber() {
		return globalNumber;
	}

	public void setGlobalNumber(String globalNumber) {
		this.globalNumber = globalNumber;
	}

	/**
	 * @return Returns the loadDate.
	 */
	public Timestamp getLoadDate() {
		return loadDate;
	}

	/**
	 * @param loadDate
	 *            The loadDate to set.
	 */
	public void setLoadDate(Timestamp loadDate) {
		this.loadDate = loadDate;
	}

	/**
	 * @return Returns the molecularFormula.
	 */
	public String getMolecularFormula() {
		return molecularFormula;
	}

	/**
	 * @param molecularFormula
	 *            The molecularFormula to set.
	 */
	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula;
	}

	/**
	 * @return Returns the molecularWeight.
	 */
	public double getMolecularWeight() {
		return molecularWeight;
	}

	/**
	 * @param molecularWeight
	 *            The molecularWeight to set.
	 */
	public void setMolecularWeight(double molecularWeight) {
		this.molecularWeight = molecularWeight;
	}

	/**
	 * @return Returns the notebokRef.
	 */
	public String getNotebokRef() {
		return notebokRef;
	}

	/**
	 * @param notebokRef
	 *            The notebokRef to set.
	 */
	public void setNotebokRef(String notebokRef) {
		this.notebokRef = notebokRef;
	}

	/**
	 * @return Returns the saltCode.
	 */
	public String getSaltCode() {
		return saltCode;
	}

	/**
	 * @param saltCode
	 *            The saltCode to set.
	 */
	public void setSaltCode(String saltCode) {
		this.saltCode = saltCode;
	}

	/**
	 * @return Returns the saltMole.
	 */
	public double getSaltMole() {
		return saltMole;
	}

	/**
	 * @param saltMole
	 *            The saltMole to set.
	 */
	public void setSaltMole(double saltMole) {
		this.saltMole = saltMole;
	}

	/**
	 * @return Returns the stereoisomerCode.
	 */
	public String getStereoisomerCode() {
		return stereoisomerCode;
	}

	/**
	 * @param stereoisomerCode
	 *            The stereoisomerCode to set.
	 */
	public void setStereoisomerCode(String stereoisomerCode) {
		this.stereoisomerCode = stereoisomerCode;
	}

	/**
	 * @return Returns the structureComment.
	 */
	public String getStructureComment() {
		return structureComment;
	}

	/**
	 * @param structureComment
	 *            The structureComment to set.
	 */
	public void setStructureComment(String structureComment) {
		this.structureComment = structureComment;
	}

	/**
	 * @return Returns the vials.
	 */
	public List<RegistrationVialDetailsVO> getVials() {
		return vials;
	}

	/**
	 * @param vials
	 *            The vials to set.
	 */
	public void setVials(List<RegistrationVialDetailsVO> vials) {
		if (vials == null) {
			vials = new ArrayList<RegistrationVialDetailsVO>();
		}
		this.vials = vials;
	}

	public void addVial(RegistrationVialDetailsVO vial) {
		this.vials.add(vial);
	}

	/**
	 * @return Returns the precursors.
	 */
	public List<String> getPrecursors() {
		return precursors;
	}

	/**
	 * @param vials
	 *            The precursors to set.
	 */
	public void setPrecursors(List<String> precursors) {
		if (precursors == null) {
			precursors = new ArrayList<String>();
		}
		this.precursors = precursors;
	}

	public void addPrecursor(String precursor) {
		this.precursors.add(precursor);
	}

	/**
	 * @return Returns the Residual Solvent.
	 */
	public List<List<String>> getResidualSolvents() {
		return residualSolvents;
	}

	/**
	 * @param vials
	 *            The Residual Solvents to set.
	 */
	public void setResidualSolvents(List<List<String>> solvents) {
		if (solvents == null) {
			solvents = new ArrayList<List<String>>();
		}
		this.residualSolvents = solvents;
	}

	public void addResidualSolvent(List<String> solvent) {
		this.residualSolvents.add(solvent);
	}

	/**
	 * @return Returns the Solubility Solvent.
	 */
	public List<List<String>> getSolubilitySolvents() {
		return solubilitySolvents;
	}

	/**
	 * @param vials
	 *            The Solubility Solvents to set.
	 */
	public void setSolubilitySolvents(List<List<String>> solvents) {
		if (solvents == null) {
			solvents = new ArrayList<List<String>>();
		}
		this.solubilitySolvents = solvents;
	}

	public void addSolubilitySolvent(List<String> solvent) {
		this.solubilitySolvents.add(solvent);
	}

	/**
	 * @return Returns the amountMade.
	 */
	public double getAmountMade() {
		return amountMade;
	}

	/**
	 * @param amountMade
	 *            The amountMade to set.
	 */
	public void setAmountMade(double amountMade) {
		this.amountMade = amountMade;
	}

	/**
	 * @return Returns the amountMadeUnit.
	 */
	public String getAmountMadeUnit() {
		return amountMadeUnit;
	}

	/**
	 * @param amountMadeUnit
	 *            The amountMadeUnit to set.
	 */
	public void setAmountMadeUnit(String amountMadeUnit) {
		this.amountMadeUnit = amountMadeUnit;
	}

	/**
	 * @return Returns the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            The source to set.
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return Returns the sourceDetail.
	 */
	public String getSourceDetail() {
		return sourceDetail;
	}

	/**
	 * @param sourceDetail
	 *            The sourceDetail to set.
	 */
	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	/**
	 * @param batchOwnerId
	 *            The batchOwnerId to set
	 */
	public void setBatchOwnerId(String batchOwnerId) {
		this.batchOwnerId = batchOwnerId;
	}

	/**
	 * @return Returns the batchOwnerId
	 */
	public String getBatchOwnerId() {
		return batchOwnerId;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierRef(String supplierRef) {
		this.supplierRef = supplierRef;
	}

	public String getSupplierRef() {
		return supplierRef;
	}

}
