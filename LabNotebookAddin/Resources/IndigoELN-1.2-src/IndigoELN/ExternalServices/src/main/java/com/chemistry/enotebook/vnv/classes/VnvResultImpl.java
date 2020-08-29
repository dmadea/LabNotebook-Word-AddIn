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
package com.chemistry.enotebook.vnv.classes;


public class VnvResultImpl implements IVnvResult {
	private static final long serialVersionUID = -9051412085097044358L;

	private boolean passed;
	private String resultMol;
	private boolean molWasModified;
	private String[] vnvErrors;
	private String molFormula;
	private double molWeight;
	private boolean stereoIsomerAssigned;
	private String assignedCode;
	private String[] validCodes;

	public VnvResultImpl(boolean passed, String resultMol,
			boolean molWasModified, String[] vnvErrors, String molFormula,
			double molWeight, boolean stereoIsomerAssigned,
			String assignedCode, String[] validCodes) {
		this.passed = passed;
		this.resultMol = resultMol;
		this.molWasModified = molWasModified;
		this.vnvErrors = vnvErrors;
		this.molFormula = molFormula;
		this.molWeight = molWeight;
		this.stereoIsomerAssigned = stereoIsomerAssigned;
		this.assignedCode = assignedCode;
		this.validCodes = validCodes;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getResultMol() {
		return resultMol;
	}

	public void setResultMol(String resultMol) {
		this.resultMol = resultMol;
	}

	public boolean isMolWasModified() {
		return molWasModified;
	}

	public void setMolWasModified(boolean molWasModified) {
		this.molWasModified = molWasModified;
	}

	public String[] getVnvErrors() {
		return vnvErrors;
	}

	public void setVnvErrors(String[] vnvErrors) {
		this.vnvErrors = vnvErrors;
	}

	public String getMolFormula() {
		return molFormula;
	}

	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	public double getMolWeight() {
		return molWeight;
	}

	public void setMolWeight(double molWeight) {
		this.molWeight = molWeight;
	}

	public boolean isStereoIsomerAssigned() {
		return stereoIsomerAssigned;
	}

	public void setStereoIsomerAssigned(boolean stereoIsomerAssigned) {
		this.stereoIsomerAssigned = stereoIsomerAssigned;
	}

	public String getAssignedCode() {
		return assignedCode;
	}

	public void setAssignedCode(String assignedCode) {
		this.assignedCode = assignedCode;
	}

	public String[] getValidCodes() {
		return validCodes;
	}

	public void setValidCodes(String[] validCodes) {
		this.validCodes = validCodes;
	}
}
