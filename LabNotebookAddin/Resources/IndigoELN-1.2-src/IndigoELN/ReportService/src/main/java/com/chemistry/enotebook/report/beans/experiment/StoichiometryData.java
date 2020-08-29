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


public class StoichiometryData {

	private String name = "";
	private String stepName = "";
	private String reagent = "";
	private String molecularWeight = "";
	private String weight = "";
	private String mMoles = "";
    private String molarity = "";
	private String volume = "";
	private String eq = "";
	private String compoundId = "";
	private String chemicalName = "";
	private String notebookBatchNumber = "";
	private String reactionRole = "";
	private String purity = "";
	private String loadingFactor = "";
	private String hazards = "";
	private String density = "";
	private String saltCode = "";
	private String saltEq = "";
	private String casNumber = "";
	private String imageUri = "";
	private String attributes = "";
	private String molecularFormula = "";
    private String comments = "";
	private boolean isList = true;

	public void dispose() {
		// no lists to clear
	}

	public String getMolecularFormula() {
		return molecularFormula;
	}

	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula;
	}
	public String getMolecularWeight() {
		return molecularWeight;
	}
	public void setMolecularWeight(String molecularWeight) {
		this.molecularWeight = molecularWeight;
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

    public String getMolarity() {
        return molarity;
    }

    public void setMolarity(String molarity) {
        this.molarity = molarity;
    }

    public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getEq() {
		return eq;
	}
	public void setEq(String eq) {
		this.eq = eq;
	}
	public String getReactionRole() {
		return reactionRole;
	}
	public void setReactionRole(String reactionRole) {
		this.reactionRole = reactionRole;
	}
	public String getPurity() {
		return purity;
	}
	public void setPurity(String purity) {
		this.purity = purity;
	}
	public String getLoadingFactor() {
		return loadingFactor;
	}
	public void setLoadingFactor(String loadingFactor) {
		this.loadingFactor = loadingFactor;
	}
	public String getHazards() {
		return hazards;
	}
	public void setHazards(String hazards) {
		this.hazards = hazards;
	}

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReagent() {
		return reagent;
	}
	public void setReagent(String reagent) {
		this.reagent = reagent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public String getCompoundId() {
		return compoundId;
	}
	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}
	public String getNotebookBatchNumber() {
		return notebookBatchNumber;
	}
	public void setNotebookBatchNumber(String notebookBatchNumber) {
		this.notebookBatchNumber = notebookBatchNumber;
	}
	public String getDensity() {
		return density;
	}
	public void setDensity(String density) {
		this.density = density;
	}
	public String getSaltCode() {
		return saltCode;
	}
	public void setSaltCode(String saltCode) {
		this.saltCode = saltCode;
	}
	public String getSaltEq() {
		return saltEq;
	}
	public void setSaltEq(String saltEq) {
		this.saltEq = saltEq;
	}
	public String getChemicalName() {
		return chemicalName;
	}
	public void setChemicalName(String chemicalName) {
		this.chemicalName = chemicalName;
	}
	public boolean isList() {
		return isList;
	}
	public void setIsList(boolean isList) {
		this.isList = isList;
	}
	public String getCasNumber() {
		return casNumber;
	}
	public void setCasNumber(String casNumber) {
		this.casNumber = casNumber;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public String getAttributes() {
		StringBuffer buff = new StringBuffer();
		if (this.casNumber != null && this.casNumber.length() > 0)
			buff.append("CAS Number: ").append(this.casNumber).append("\n");
		if (this.chemicalName != null && this.chemicalName.length() > 0)
			buff.append("Chemical Name: ").append(this.chemicalName).append("\n");
		if (this.molecularFormula != null && this.molecularFormula.length() > 0)
			buff.append("Molecular Formula: ").append(this.molecularFormula).append("\n");
		if (this.density != null && this.density.length() > 0)
			buff.append("Density: ").append(this.density).append("\n");
		if (this.hazards != null && this.hazards.length() > 0)
			buff.append("Hazards: ").append(this.hazards).append("\n");
		if (this.loadingFactor != null && this.loadingFactor.length() > 0 && (!this.loadingFactor.equals("0")))
			buff.append("Loading Factor: ").append(this.loadingFactor).append("\n");
		if (this.name != null && this.name.length() > 0)
			buff.append("Name: ").append(this.name).append("\n");
		if (this.notebookBatchNumber != null && this.notebookBatchNumber.length() > 0)
			buff.append("Nbk Batch No: ").append(this.notebookBatchNumber).append("\n");
		if (this.purity != null && this.purity.length() > 0)
			buff.append("Purity: ").append(this.purity).append("\n");
		if (this.reactionRole != null && this.reactionRole.length() > 0)
			buff.append("Reaction Role: ").append(this.reactionRole).append("\n");
		if (this.reagent != null && this.reagent.length() > 0)
			buff.append("Reagent: ").append(this.reagent).append("\n");
		if (this.saltCode != null && this.saltCode.length() > 0)
			if (!this.saltCode.equals("00")) {
				buff.append("Salt Code: ").append(this.saltCode).append("\n");
				if (this.saltEq != null && this.saltEq.length() > 0)
					buff.append("Salt Eq: ").append(this.saltEq).append("\n");
			}
		return buff.toString();
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<stoichiometry>");
		buff.append("<name>").append(this.getName()).append("</name>");
		buff.append("<stepName>").append(this.getStepName()).append("</stepName>");
		if (! this.isList()) {
			buff.append("<notebookBatchNumber>").append(this.getNotebookBatchNumber()).append("</notebookBatchNumber>");
			buff.append("<molecularFormula>").append(this.getMolecularFormula()).append("</molecularFormula>");
			buff.append("<molecularWeight>").append(this.getMolecularWeight()).append("</molecularWeight>");
			buff.append("<chemicalName>").append(this.getChemicalName()).append("</chemicalName>");
			buff.append("<compoundId>").append(this.getChemicalName()).append("</compoundId>");
			buff.append("<casNumber>").append(this.getCasNumber()).append("</casNumber>");
			//buff.append("<imageUri>").append(this.getImageUri()).append("</imageUri>");
		}
		buff.append("<imageUri>").append(this.getImageUri()).append("</imageUri>");
		buff.append("<weight>").append(this.getWeight()).append("</weight>");
		buff.append("<volume>").append(this.getVolume()).append("</volume>");
		buff.append("<eq>").append(this.getEq()).append("</eq>");
		buff.append("<hazards>").append(this.getHazards()).append("</hazards>");
		buff.append("<mMoles>").append(this.getMMoles()).append("</mMoles>");
		buff.append("<purity>").append(this.getPurity()).append("</purity>");
		buff.append("<density>").append(removeZero(this.getDensity())).append("</density>");
		buff.append("<saltCode>").append(removeZero(this.getSaltCode())).append("</saltCode>");
		buff.append("<saltEq>").append(removeZero(this.getSaltEq())).append("</saltEq>");
        buff.append("<loadingFactor>").append(this.getLoadingFactor()).append("</loadingFactor>");
        buff.append("<molarity>").append(this.getMolarity()).append("</molarity>");
        buff.append("<comments>").append(this.getComments()).append("</comments>");
		buff.append("<reactionRole>").append(this.getReactionRole()).append("</reactionRole>");
		buff.append("<attributes>").append(this.getAttributes()).append("</attributes>");
		buff.append("</stoichiometry>");
		return buff.toString();
	}


    public String  removeZero(String s) {
        if (s == null)
            return "";
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch != '0' && ch != '.')
                return s;
        }
        return "";

    }


}
