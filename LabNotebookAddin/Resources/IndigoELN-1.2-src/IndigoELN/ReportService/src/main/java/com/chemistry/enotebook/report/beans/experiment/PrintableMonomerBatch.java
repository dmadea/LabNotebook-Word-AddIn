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
package com.chemistry.enotebook.report.beans.experiment;

public class PrintableMonomerBatch {

	String listKey = "";
	String listName = "";
	String stepName = "";
	String compoundId = "";
	String weight = "";
	String mMoles = "";
	String molecularWeight = "";
	String molecularFormula = "";
	String imageUri = "";
	
	
	public PrintableMonomerBatch() {
	}

	public PrintableMonomerBatch(String compoundId, String weight, String moles,
			String molecularWeight, String molecularFormula, String imageUri) {
		super();
		this.compoundId = compoundId;
		this.weight = weight;
		mMoles = moles;
		this.molecularWeight = molecularWeight;
		this.molecularFormula = molecularFormula;
		this.imageUri = imageUri;
	}

	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getMMoles() {
		return mMoles;
	}

	public void setMMoles(String moles) {
		mMoles = moles;
	}

	public String getMolecularWeight() {
		return molecularWeight;
	}

	public void setMolecularWeight(String molecularWeight) {
		this.molecularWeight = molecularWeight;
	}

	public String getMolecularFormula() {
		return molecularFormula;
	}

	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula;
	}

	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getListKey() {
		return listKey;
	}

	public void setListKey(String listKey) {
		this.listKey = listKey;
	}

	public String toXml() {
		StringBuffer buff = new StringBuffer("<monomerBatch>");
		buff.append("<listKey>").append(this.getListKey()).append("</listKey>");
		buff.append("<stepName>").append(this.getStepName()).append("</stepName>");
		buff.append("<listName>").append(this.getListName()).append("</listName>");
		buff.append("<compoundId>").append(this.getCompoundId()).append("</compoundId>");
		buff.append("<mMoles>").append(this.getMMoles()).append("</mMoles>");
		buff.append("<molecularWeight>").append(this.getMolecularWeight()).append("</molecularWeight>");
		buff.append("<molecularFormula>").append(this.getMolecularFormula()).append("</molecularFormula>");
		buff.append("<weight>").append(this.getWeight()).append("</weight>");
		buff.append("<imageUri>").append(this.getImageUri()).append("</imageUri>");
		buff.append("</monomerBatch>");
		return buff.toString();
	}	
	
}
