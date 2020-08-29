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

package com.chemistry.enotebook.vcr.auxiliary;

public class StatusInfoImpl implements StatusInfo {

	private static final long serialVersionUID = 4849411014607790727L;

	public String getCompoundId() {
		return compoundId;
	}

	public String[] getMatched() {
		return matched;
	}

	public String[] getMessage() {
		return message;
	}

	public String getMolFormula() {
		return molFormula;
	}

	public String getMolweight() {
		return molweight;
	}

	public VcrStatus getVcrStatus() {
		return vcrStatus;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

	public void setMatched(String[] matched) {
		this.matched = matched;
	}

	public void setMessage(String[] message) {
		this.message = message;
	}

	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	public void setMolweight(String molweight) {
		this.molweight = molweight;
	}

	public void setVcrStatus(VcrStatus vcrStatus) {
		this.vcrStatus = vcrStatus;
	}

	private VcrStatus vcrStatus;
	private String molFormula;
	private String[] message;
	private String[] matched;
	private String compoundId;
	private String molweight;
}
