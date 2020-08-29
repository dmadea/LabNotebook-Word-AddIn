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
import com.chemistry.enotebook.experiment.datamodel.common.Amount2;

import java.text.DecimalFormat;

public class AmountModel extends Amount2 {

	public static final long serialVersionUID = 7526472295622776147L;

	//This is a wrong constructor to use. Initialize with Unit Type.
	public AmountModel()
	{
		
	}
	
	public AmountModel(UnitType unitType) {
		super(unitType);
	}

	public AmountModel(UnitType unitType, double defaultVal, double value) {
		super(unitType,value,defaultVal+"");
	}

	public AmountModel(UnitType unitType, double value) {
		super(unitType,value);
	}

	
	public void deepCopy(Object source) {
		if (source instanceof AmountModel) {
			AmountModel src = (AmountModel)source;
			//setValue("");
			setValue(src.getValue().toString());
			//setDefaultValue(src.getDefaultValue());
			setDisplayedFigs(src.getDisplayedFigs());
			setSigDigitsSet(src.getSigDigitsSet());
			setSigDigits(src.getSigDigits());
			setUserPrefFigs(src.getUserPrefFigs());
			setCalculated(src.isCalculated());
			getUnit().deepCopy(src.getUnit());
			setCanBeDisplayed(src.isCanBeDisplayed());
		}
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<Calculated>"+ this.isCalculated() +"</Calculated>");
		xmlbuff.append("<Default_Value>"+ this.getDefaultValue() +"</Default_Value>");
		xmlbuff.append(this.getUnit().toXML());
		xmlbuff.append("<Value>" + this.getValue() +"</Value>");
		return xmlbuff.toString();
	}
	
	public String toXMLWithStartingTag(String tagElementName) {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<"+tagElementName+">");
		xmlbuff.append(this.toXML());
		xmlbuff.append("</"+tagElementName+">");
		return xmlbuff.toString();
	}
	
	public boolean equals(Object amtObj){
		if(amtObj == null) return false;
		AmountModel amtModel = (AmountModel)amtObj;
		// vb 7/15 value was == so always returned false
		// isCalculated and sigDigits were not included
		// Leave as separate lines for debugging if necessary
		
		//NS 7/23. Fix to load empty value Amount Record from DB to model.
		if(amtModel.isLoadingFromDB())
		{
			return false;
		}
		if(! (this.getValue().equals(amtModel.getValue()) ) ) {
			return false;	
		}
		if(! (this.isCalculated() == amtModel.isCalculated()) ) {
			return false;
		}
		if(! (this.getSigDigits() == amtModel.getSigDigits()) ) {
			return false;
		}
		if(! (this.getUnit().getCode().equals(amtModel.getUnit().getCode()) )  ) {
			return false;
		}
		return true;	
	}
	
	public Object deepClone()
	{
		
		AmountModel amtModel = new AmountModel(getUnit().getType());
		amtModel.setUnit(getUnit());
		amtModel.setCalculated(isCalculated());
		amtModel.setValue(getValue());
		amtModel.setDefaultValue(getDefaultValue());
		// vb 10/20/08 to set the sig figs when user enters a measurement
		amtModel.setSigDigits(this.getSigDigits());
		amtModel.setModelChanged(this.modelChanged);
	    return amtModel;	
	}
	
	  private double getTwoDigitValue(double longValue){
		  try{
			  String s = new DecimalFormat(".00").format(longValue);
			  return Double.parseDouble(s);
		  }catch(Exception error){
			  return .00;
		  }
	  }

	  private double getFourDigitValue(double longValue){
		try {
			String s = new DecimalFormat(".0000").format(longValue);
			return Double.parseDouble(s);
		} catch (Exception e) {
			return .0000;
		}
	  }

	//We need this transformation since this column in Amount table is defined
	// as Number(1)
	public int getSigDigitsSetForStorageAPI()
	{
		if(this.getSigDigitsSet())
		{
			return 1;
		}else
		{
		 return  0;	
		}
	}
	
	public void setSigDigitsSetFromStorageAPI(int val)
	{
		if(val == 0)
		{
			this.setSigDigitsSet(false);
		}else
		{
			this.setSigDigitsSet(true);
		}
	}
	
	public AmountModel(UnitType unitType, String defaultVal)
	{
		super(unitType,defaultVal);
	}

		
	//This is defined to be used for parallelStoic handling with BAtchesList.
	//If you use it elsewehre it can cuase some serious troubles,
	public void deepCopyWithoutKeys(Object source) {
		if (source instanceof AmountModel) {
			AmountModel src = (AmountModel)source;
			setValue("");
			setValue(src.getValue().toString());
			setDefaultValue(src.getDefaultValue());
			setDisplayedFigs(src.getDisplayedFigs());
			setSigDigitsSet(src.getSigDigitsSet());
			setSigDigits(src.getSigDigits());
			setUserPrefFigs(src.getUserPrefFigs());
			setCalculated(src.isCalculated());
			getUnit().deepCopy(src.getUnit());
			
		}
	}
	
	public boolean equalsByValueAndUnits(Object amtObj){
		if(amtObj == null) return false;
		AmountModel amtModel = (AmountModel)amtObj;
		// to avoid .10 not equal to .100 when comparened by getValue()
		if(! (this.GetValueForDisplay().equals(amtModel.GetValueForDisplay()) ) ) {
			return false;	
		}
		if(! (this.getUnit().getStdCode().equals(amtModel.getUnit().getStdCode()) )  ) {
			return false;
		}
		return true;	
	}
	
	}