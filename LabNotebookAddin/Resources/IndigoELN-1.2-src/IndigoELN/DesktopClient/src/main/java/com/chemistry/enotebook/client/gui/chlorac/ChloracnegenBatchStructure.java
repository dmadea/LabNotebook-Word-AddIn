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
package com.chemistry.enotebook.client.gui.chlorac;

import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;

/**
 * Bean class to hold structure and other info for testing and displaying
 * Chloracnegen tests.
 * 
 * 
 * 
 */
public class ChloracnegenBatchStructure implements java.io.Serializable {

	private static final long serialVersionUID = -6130290212328433371L;

	// Indentifies type ( REACTANT or INTENDED PRODUCT so forth )
	private BatchType type;
	// isis format of the structure
	private byte[] nativeSketch;
	// mol representation fo the compound structure
	private byte[] molString;
	// different identifications in order of display preference
	private String compoundName; // p01,p02 etc
	private String compoundID;
	private String casNumber; //
	private String NBKBatchNumber; //
	private String molecularFormula; // C6H6
	private boolean testedForChloracnegen = false; // Flag to check if
	// batch has been tested
	// for Chloracnegen
	private boolean chloracnegenFlag = false; // Flag to identify if this
	// batch is a chloracnegen hit
	// after running structure
	// through CCT
	private String chloracnegenType = ""; // Type info like Class 1 ,Class
	// 2 etc
	private String batchKey = ""; // key of the batch

	public ChloracnegenBatchStructure() {
	}

	public ChloracnegenBatchStructure(byte[] molString, BatchType type) {
		this.molString = molString;
		this.type = type;
	}

	public ChloracnegenBatchStructure(byte[] nativeSketch, byte[] molString,
			BatchType type) {
		this.molString = molString;
		this.nativeSketch = nativeSketch;
		this.type = type;
	}

	public ChloracnegenBatchStructure(byte[] nativeSketch) {
		this.nativeSketch = nativeSketch;
	}

	public String getCasNumber() {
		return casNumber;
	}

	public void setCasNumber(String casNumber) {
		this.casNumber = casNumber;
	}

	public boolean isChloracnegenFlag() {
		return chloracnegenFlag;
	}

	public void setChloracnegenFlag(boolean chloracnegenFlag) {
		this.chloracnegenFlag = chloracnegenFlag;
	}

	public String getChloracnegenType() {
		return chloracnegenType;
	}

	public void setChloracnegenType(String chloracnegenType) {
		this.chloracnegenType = chloracnegenType;
	}

	public String getCompoundID() {
		return compoundID;
	}

	public void setCompoundID(String compoundID) {
		this.compoundID = compoundID;
	}

	public String getCompoundName() {
		return compoundName;
	}

	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}

	public String getMolecularFormula() {
		return molecularFormula;
	}

	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula;
	}

	public byte[] getMolString() {
		return molString;
	}

	public void setMolString(byte[] molString) {
		this.molString = molString;
	}

	public byte[] getNativeSketch() {
		return nativeSketch;
	}

	public void setNativeSketch(byte[] nativeSketch) {
		this.nativeSketch = nativeSketch;
	}

	public String getNBKBatchNumber() {
		return NBKBatchNumber;
	}

	public void setNBKBatchNumber(String batchNumber) {
		NBKBatchNumber = batchNumber;
	}

	public boolean isTestedForChloracnegen() {
		return testedForChloracnegen;
	}

	public void setTestedForChloracnegen(boolean testedForChloracnegen) {
		this.testedForChloracnegen = testedForChloracnegen;
	}

	public BatchType getType() {
		return type;
	}

	public void setType(BatchType type) {
		this.type = type;
	}

	public String getDispayID() {
		// //System.out.println("ChloracnegenBatchStructure.getDisplayID()"+type.getOrdinal());
		// if intended product display compoud name first and others
		if (type.getOrdinal() == BatchType.INTENDED_PRODUCT_ORDINAL
				|| type.getOrdinal() == BatchType.ACTUAL_PRODUCT_ORDINAL) {
			// //System.out.println("ChloracnegenBatchStructure.getDisplayID()"+this.compoundName);
			if (this.compoundName != null
					&& this.compoundName.compareTo("") != 0)
				return this.compoundName;
			else if (this.casNumber != null
					&& this.casNumber.compareTo("") != 0)
				return this.casNumber;
			else if (this.NBKBatchNumber != null
					&& this.NBKBatchNumber.compareTo("") != 0)
				return this.NBKBatchNumber;
			else if (this.molecularFormula != null
					&& this.molecularFormula.compareTo("") != 0)
				return this.molecularFormula;
			else
				return "Intended Product";
		} else {
			if (this.compoundID != null && this.compoundID.compareTo("") != 0)
				return this.compoundID;
			else if (this.casNumber != null
					&& this.casNumber.compareTo("") != 0)
				return this.casNumber;
			else if (this.NBKBatchNumber != null
					&& this.NBKBatchNumber.compareTo("") != 0)
				return this.NBKBatchNumber;
			else if (this.molecularFormula != null
					&& this.molecularFormula.compareTo("") != 0)
				return this.molecularFormula;
			else
				return "Stoic Batch";
		}
		// if any other display compound name and then others
	}

	public String getBatchKey() {
		return batchKey;
	}

	public void setBatchKey(String batchKey) {
		this.batchKey = batchKey;
	}
}
