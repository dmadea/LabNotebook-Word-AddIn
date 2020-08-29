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
package com.chemistry.enotebook.domain.purificationservice;

import com.chemistry.enotebook.domain.CeNAbstractModel;

public class PurificationServiceDefaults extends CeNAbstractModel{

	public static final long serialVersionUID = 7526472295622776147L;
	
	private String compoundRegistrationCompoundSourceCode = "INPRODINT";
	private String compoundRegistrationCompoundSourceDetailCode = "INPRDARRAY";
	private double compoundRegistrationTotalAmountMade = 50;
	private String compoundRegistrationTotalAmountMadeUnitCode = "MG";
	private double compoundRegistrationPlateWellAmountValue =1000;
	private String compoundRegistrationPlateWellAmountUnitValue = "ul";
	private double compoundRegistrationPlateWellInitialAmountValue =1000;
	private String compoundRegistrationPlateWellInitialAmountUnitValue = "ul";
	private double compoundRegistrationPlateWellMolarityValue =200;
	private String compoundRegistrationPlateWellMolairtyUnitCode = "MM";

	
	public String getCompoundRegistrationCompoundSourceCode() {
		return compoundRegistrationCompoundSourceCode;
	}



	public void setCompoundRegistrationCompoundSourceCode(String compoundRegistrationCompoundSourceCode) {
		this.compoundRegistrationCompoundSourceCode = compoundRegistrationCompoundSourceCode;
	}



	public String getCompoundRegistrationCompoundSourceDetailCode() {
		return compoundRegistrationCompoundSourceDetailCode;
	}



	public void setCompoundRegistrationCompoundSourceDetailCode(String compoundRegistrationCompoundSourceDetailCode) {
		this.compoundRegistrationCompoundSourceDetailCode = compoundRegistrationCompoundSourceDetailCode;
	}



	public String getCompoundRegistrationPlateWellAmountUnitValue() {
		return compoundRegistrationPlateWellAmountUnitValue;
	}



	public void setCompoundRegistrationPlateWellAmountUnitValue(String compoundRegistrationPlateWellAmountUnitValue) {
		this.compoundRegistrationPlateWellAmountUnitValue = compoundRegistrationPlateWellAmountUnitValue;
	}



	public double getCompoundRegistrationPlateWellAmountValue() {
		return compoundRegistrationPlateWellAmountValue;
	}



	public void setCompoundRegistrationPlateWellAmountValue(double compoundRegistrationPlateWellAmountValue) {
		this.compoundRegistrationPlateWellAmountValue = compoundRegistrationPlateWellAmountValue;
	}



	public String getCompoundRegistrationPlateWellInitialAmountUnitValue() {
		return compoundRegistrationPlateWellInitialAmountUnitValue;
	}



	public void setCompoundRegistrationPlateWellInitialAmountUnitValue(String compoundRegistrationPlateWellInitialAmountUnitValue) {
		this.compoundRegistrationPlateWellInitialAmountUnitValue = compoundRegistrationPlateWellInitialAmountUnitValue;
	}



	public double getCompoundRegistrationPlateWellInitialAmountValue() {
		return compoundRegistrationPlateWellInitialAmountValue;
	}



	public void setCompoundRegistrationPlateWellInitialAmountValue(double compoundRegistrationPlateWellInitialAmountValue) {
		this.compoundRegistrationPlateWellInitialAmountValue = compoundRegistrationPlateWellInitialAmountValue;
	}



	public String getCompoundRegistrationPlateWellMolairtyUnitCode() {
		return compoundRegistrationPlateWellMolairtyUnitCode;
	}



	public void setCompoundRegistrationPlateWellMolairtyUnitCode(String compoundRegistrationPlateWellMolairtyUnitCode) {
		this.compoundRegistrationPlateWellMolairtyUnitCode = compoundRegistrationPlateWellMolairtyUnitCode;
	}



	public double getCompoundRegistrationPlateWellMolarityValue() {
		return compoundRegistrationPlateWellMolarityValue;
	}



	public void setCompoundRegistrationPlateWellMolarityValue(double compoundRegistrationPlateWellMolarityValue) {
		this.compoundRegistrationPlateWellMolarityValue = compoundRegistrationPlateWellMolarityValue;
	}



	public double getCompoundRegistrationTotalAmountMade() {
		return compoundRegistrationTotalAmountMade;
	}



	public void setCompoundRegistrationTotalAmountMade(double compoundRegistrationTotalAmountMade) {
		this.compoundRegistrationTotalAmountMade = compoundRegistrationTotalAmountMade;
	}



	public String getCompoundRegistrationTotalAmountMadeUnitCode() {
		return compoundRegistrationTotalAmountMadeUnitCode;
	}



	public void setCompoundRegistrationTotalAmountMadeUnitCode(String compoundRegistrationTotalAmountMadeUnitCode) {
		this.compoundRegistrationTotalAmountMadeUnitCode = compoundRegistrationTotalAmountMadeUnitCode;
	}



	public String toXML() {
		return "";
	}
}
