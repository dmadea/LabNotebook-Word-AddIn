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
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.stoichiometry;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class StructureSearchResultData {
	private String compoundId;
	private String compoundParent;
	private String nbRef;
	private double molWt;
	private String molFormula;
	private String conversationalBatchNumber;
	private String saltCode;

	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

	public String getCompoundParent() {
		return compoundParent;
	}

	public void setCompoundParent(String compoundParent) {
		this.compoundParent = compoundParent;
	}

	public String getConversationalBatchNumber() {
		return conversationalBatchNumber;
	}

	public void setConversationalBatchNumber(String conversationalBatchNumber) {
		this.conversationalBatchNumber = conversationalBatchNumber;
	}

	public String getMolFormula() {
		return molFormula;
	}

	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	public double getMolWt() {
		return molWt;
	}

	public void setMolWt(double molWt) {
		this.molWt = molWt;
	}

	public String getNbRef() {
		return nbRef;
	}

	public void setNbRef(String nbRef) {
		this.nbRef = nbRef;
	}

	public String getSaltCode() {
		return saltCode;
	}

	public void setSaltCode(String saltCode) {
		this.saltCode = saltCode;
	}
}
