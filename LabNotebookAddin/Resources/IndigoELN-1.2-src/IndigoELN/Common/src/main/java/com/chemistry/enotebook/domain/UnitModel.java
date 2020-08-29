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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.common.units.UnitType;

public class UnitModel extends CeNAbstractModel {

	public static final long serialVersionUID = 7526472295622776147L;

	private UnitType unitType;
	private transient String displayValue = "";
	private String stdCode = "--";
	private String description = null;
	private transient double stdConversionFactor = -1;

	public UnitModel() {

	}

	public UnitModel(UnitType unitType) {
		this.unitType = unitType;
	}
	
	

	public UnitModel(UnitType unitType, String stdCode) {
		this.unitType = unitType;
		this.stdCode = stdCode;
	}

	public UnitModel(UnitType unitType, String displayValue, String stdCode) {
		this.unitType = unitType;
		this.displayValue = displayValue;
		this.stdCode = stdCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
		this.modelChanged = true;
	}

	/**
	 * @return the stdCode
	 */
	public String getStdCode() {
		return stdCode;
	}

	/**
	 * @param stdCode
	 *            the stdCode to set
	 */
	public void setStdCode(String stdCode) {
		this.stdCode = stdCode;
		this.modelChanged = true;
	}

	/**
	 * @return the unitType
	 */
	public UnitType getUnitType() {
		return unitType;
	}

	/**
	 * @param unitType
	 *            the unitType to set
	 */
	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
		this.modelChanged = true;
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<Unit>");
		xmlbuff.append("<Code>"+this.stdCode+"</Code>");
		xmlbuff.append("<Description>"+this.description+"</Description>");
		xmlbuff.append("</Unit>");

		return xmlbuff.toString();
	}
	
	
	public static UnitModel getUnitFromStdCode(String code){
		if(code == null)
			return new UnitModel(UnitType.SCALAR, "","SCAL");
		
		code = code.toUpperCase();
		if(code.equals("MG"))
			return new UnitModel( UnitType.MASS, "mg", "MG");
		else if(code.equals("GM"))
			return  new UnitModel( UnitType.MASS, "g", "GM");
		else if(code.equals("KG"))
			return new UnitModel( UnitType.MASS, "kg", "KG");
		else if(code.equals("UMOL"))
			return new UnitModel(UnitType.MOLES, "umol","UMOL");
		else if(code.equals("MMOL"))
			return new UnitModel(UnitType.MOLES, "mmol","MMOL");
		else if(code.equals("MOL"))
			return new UnitModel(UnitType.MOLES, "mol","MOL");
		else if(code.equals("UL"))
			return new UnitModel( UnitType.VOLUME, "uL","UL"); 
		else if(code.equals("ML"))
			return new UnitModel(UnitType.VOLUME, "mL", "ML"); 
		else if(code.equals("L"))
			return new UnitModel( UnitType.VOLUME, "L", "L"); 
		else if(code.equals("MM"))
			return new UnitModel( UnitType.MOLAR, "mM", "MM");
		else if(code.equals("M"))
			return new UnitModel( UnitType.MOLAR, "M", "M"); 
		else if(code.equals("GML"))
			return new UnitModel( UnitType.DENSITY, "g/mL", "GML");
		else if(code.equals("SCAL"))
			return new UnitModel(UnitType.SCALAR, "", "SCAL");
		else if(code.equals("MMOLG"))
			return new UnitModel( UnitType.LOADING, "mmol/g", "MMOLG");
		else if(code.equals("C"))
			return new UnitModel( UnitType.TEMP, "C", "C");
		
		
		return new UnitModel( UnitType.SCALAR, "", "SCAL");
		
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public double getStdConversionFactor() {
		return stdConversionFactor;
	}

	public void setStdConversionFactor(double stdConversionFactor) {
		this.stdConversionFactor = stdConversionFactor;
	}
	

}
