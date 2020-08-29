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
package com.chemistry.enotebook.sdk;

public class MoleculePropertyInfo {
	
	private String molStructure;
	private String MF;
	private double MW;
	private double exactMass;
	
	public double getExactMass() {
		return exactMass;
	}
	public void setExactMass(double exactMass) {
		this.exactMass = exactMass;
	}
	public String getMF() {
		return MF;
	}
	public void setMF(String mf) {
		MF = mf;
	}
	public String getMolStructure() {
		return molStructure;
	}
	public void setMolStructure(String molStructure) {
		this.molStructure = molStructure;
	}
	public double getMW() {
		return MW;
	}
	public void setMW(double mw) {
		MW = mw;
	}
}
