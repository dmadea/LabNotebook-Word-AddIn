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
package com.chemistry.enotebook.experiment.datamodel.common;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.common.units.Unit;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.common.units.UnitFactory;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;

import java.math.BigDecimal;

/**
 * 
 * Created on: Aug 18, 2004
 * 
 *
 * 
 * Uses String type to store the value, so you should be able to store some pretty big digits.
 * Uses BigDecimal when converting values.  Hence it doesn't lose its original sig figs.
 * displayedFigs is set only when std unit display values need to be overridden.
 *
 * Note on Significant Figures:
 * 
 * Online: http://chemed.chem.purdue.edu/genchem/topicreview/bp/ch1/sigfigs.html#determ
 * 1) All nonzero digits are significant--457 cm (three significant figures); 0.25 g (two significant figures).
 * 2) Zeros between nonzero digits are significant--1005 kg (four significant figures); 1.03 cm (three significant figures).
 * 3) Zeros within a number are always significant. Both 4308 and 40.05 contain four significant figures
 * 4) Zeros to the left of the first nonzero digits in a number are not significant; they merely indicate the position of the decimal point--0.02 g (one significant figure); 0.0026 cm (two significant figures).
 * 5) When a number ends in zeros that are to the right of the decimal point, they are significant--0.0200 g (three significant figures); 3.0 cm (two significant figures).
 * 
 */
public class Amount extends ObservableObject implements DeepClone, DeepCopy
{
	
	private static final long serialVersionUID = 6472279391378307360L;
	
	// Holds the amount in a double value and units in a separate value
	private static final int HASH_PRIME = 12433;
	private transient final int MAX_FIGS = 10;
	private StringBuffer value = new StringBuffer("0");
	//holds the default value 
	private BigDecimal defaultValue = new BigDecimal("0");
	
	private Unit unit = UnitFactory.createUnitOfType(UnitType.MASS);
	private boolean calculated = true;
    // need sense of maximum figs possible.
	private int userPrefFigs =  -1;     // Set by user entry.  Overrides std unit figs to display
    private int displayedFigs = -1;     // Amount of decimal places to show - overrides unit.getStdDisplayFigs()
    private transient int roundingPreference = BigDecimal.ROUND_HALF_UP;  // TODO: Part of preference in future
	private transient static final double DELTA_FIGS = 0.0000001;
	private transient boolean debug = false; 
	
//	 Specifies if SignificantFigures count is set or not. If not set, the user prefs and such take over.
	private boolean sigDigitsSet = true;
    private int sigDigits = CeNNumberUtils.DEFAULT_SIG_DIGITS;          // int holding the value's significant digits.
	
	/** 
	 * Do not use this constructor, unless you only want default setup: UnitType.MASS
	 *
	 */
	public Amount() { }
	public Amount(UnitType unitType)
	{
		this();
		unit = UnitFactory.createUnitOfType(unitType);
	}
	public Amount(UnitType unitType, String defaultVal)
	{
		this();
	    setDefaultValue(defaultVal);
	    unit = UnitFactory.createUnitOfType(unitType);
	}
	public Amount(String val, Unit toUnit)
	{
		this();
	    setValue(val);
		unit.deepCopy(toUnit);
	}
	public Amount(String val, Unit toUnit, String defaultVal)
	{
		this(val, toUnit);
	    setDefaultValue(defaultVal);
	}
	public Amount(double val, Unit toUnit)
	{
		this();
		setValue(val);
		unit.deepCopy(toUnit);
	}
	public Amount(double val, Unit toUnit, String defaultVal)
	{
		this(val, toUnit);
	    setDefaultValue(defaultVal);
	}

	public Amount(double val, Unit toUnit, String defaultVal, int sigDigs)
	{
		this(val, toUnit);
		setSigDigits(sigDigits);
	    setDefaultValue(defaultVal);
	}

	public Amount(Amount amt) { 
		deepCopy(amt); 
	}
	
	public void finalize() throws Throwable {
		super.finalize();
		unit = null;
		value = null;
	}
	
	// 
	// Public methods
	//
	
	/**
	 * Returns the value set to defaultValue and sets unit to default value
	 * Calls Modified
	 */
	public void reset() 
	{
        unit.deepCopy(UnitCache.getInstance().getUnit(unit.getStdCode()));
	    softReset();
	}
	
	/**
	 * Returns the value set to defaultValue sets calc'd to true and resets 
	 *         displayFigs but leaves the unit alone
	 * Calls Modified
	 */
	public void softReset() 
	{
        value.setLength(0);
        value.append(defaultValue.toString());
		calculated = true;
		sigDigits = CeNNumberUtils.DEFAULT_SIG_DIGITS;
		sigDigitsSet =  true;
		displayedFigs = -1;
		setModified(true);
	}
	
	/**
	 * Returns the value as if the amount object were set to that unit.
	 * Does not change the value or unit in the amount object.
     * Does not change the sig figs of the value.
     * 
     * Side-effect: value is set to new value and unit is updated.
	 */
	private String convertValue(Unit toUnit)
	{
//        BigDecimal result = null;
        String result = null;
		// need to check unit is of specific type.
		if (getUnitType().equals(toUnit.getType()) && !unit.equals(toUnit)) {
			// bring unit to standard units
			// System.out.println("Amount.convertValue() being used");
			if (debug) {
				System.out.println("Amount.convertValue() initial unit = " + getUnit());
				System.out.println("Amount.convertValue() convert toUnit = " + toUnit);
				System.out.println("Amount.convertValue() conversion factor = " + toUnit.getStdConversionFactor());
			}
			String baseUnit = unit.getStdCode();
			double convFactor = unit.getStdConversionFactor();
			double fullConvValue =  Double.parseDouble(getValue()) * convFactor;
			if (toUnit.getCode().equals(baseUnit)){
				value.setLength(0);
//				result = new BigDecimal(trimRightJunk(fullConvValue + ""));
                result = trimRightJunk(fullConvValue + "");
				value.append(result.toString());
			} else {
				double toConvFactor = toUnit.getStdConversionFactor();
				fullConvValue = fullConvValue / toConvFactor;
				
				value.setLength(0);
//				result = new BigDecimal(trimRightJunk(fullConvValue+""));
                result = trimRightJunk(fullConvValue+"");
				value.append(result.toString());
			}
       } else {
//		    result = new BigDecimal(getValue());
            result = getValue();
        }
		return result.toString();
	}
   /*
    * Trims '*.0' values generated by the double division or multiplication
    * on 2 double or int values. else it would return the same val
    */
	public String trimRightJunk(String val){
		StringBuffer nonZeroValue = new StringBuffer();
		if (val.endsWith(".0")) {
			int dotIndex = val.indexOf('.');
			nonZeroValue.append(val.substring(0, dotIndex));
		} else {
            //Trims the additional decimal places greater than MAX_FIGS
			int index = val.indexOf('.');
            String fraction = val.substring(index, val.length());
            if (fraction.length() > 10) {
                nonZeroValue.append(SignificantFigures.format(val, MAX_FIGS));
            } else {
                nonZeroValue.append(val);
            }
            
        }
		return nonZeroValue.toString();
	}
	
    public String trimLeftZeros(String val) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < val.length(); i++)
        {
            if(val.charAt(i) != '0')
                result.append(val.charAt(i));
        }
        return result.toString();
    }
    
	public double getConversionFactor(Unit toUnit)
	{
		// This may not work all the time just yet.
		double conversionFactor = 1.0;
		int result = unit.compareTo(toUnit);
		if (result != 0) {
			if (result > 0)
				conversionFactor = unit.getStdConversionFactor() * toUnit.getStdConversionFactor();
			else if (toUnit.getStdConversionFactor() != 0.0)
				conversionFactor = unit.getStdConversionFactor() / toUnit.getStdConversionFactor();
		}
		return conversionFactor;
	}
    
	/**
	 * Only valid after editing is finished.
	 * 
	 * @return
	 */
	public String getValue() {
		return value.toString();
	    /*String result;
        if (sigDigitsSet && sigDigits > 0) {
            // Use this to allow for enteries like "60." to allow a setting of 2 sig figs.
            result = SignificantFigures.format(value.toString(), sigDigits);
            // Use this to keep scientific notiation out of the display. 
            if (result.indexOf("E") >= 0) 
                result = new BigDecimal(SignificantFigures.format(value.toString(), sigDigits)).toString();
        } else { // value is old school: pre sig figs.
            int decimalPlace = value.indexOf(".");
            int fixedFigs = getFixedFigs();
            if (isCalculated() && decimalPlace >= 0 && 
                    value.substring(decimalPlace + 1, value.length()).length() > fixedFigs)
                result = new BigDecimal(value.toString()).setScale(fixedFigs, roundingPreference).toString();
            else // user set.
                result = value.toString();
        }
        return result;*/
    }
    
	public String GetValueForDisplay() {
		String result = ""; // May need to use this = value.toString();
		try
		{
        if (sigDigitsSet && sigDigits > 0) {
            // Use this to allow for enteries like "60." to allow a setting of 2 sig figs.
            result = SignificantFigures.format(value.toString(), sigDigits);
            // Use this to keep scientific notiation out of the display. 
            if (result.indexOf("E") >= 0) 
                result = new BigDecimal(SignificantFigures.format(value.toString(), sigDigits)).toString();
        } else { // value is old school: pre sig figs.
            int decimalPlace = value.indexOf(".");
            int fixedFigs = getFixedFigs();
            if (isCalculated() && decimalPlace >= 0 && 
                    value.substring(decimalPlace + 1, value.length()).length() > fixedFigs)
                result = new BigDecimal(value.toString()).setScale(fixedFigs, roundingPreference).toString();
            else // user set.
                result = value.toString();
        }
//        System.out.println("Value :"+ getValue());
//        System.out.println("DisplayValue :"+ result);
		}catch(java.lang.NumberFormatException fe)
		{
			//No action required.When there is NAN resulting division this exception is thrown 
			//and GUI doesn't initialize. But print stack trace to let know issue is there.
			fe.printStackTrace();
		}
        return result;
//	    String result = value.toString();
//	    int decimalLocation = result.indexOf("."); 
//	    int figsToDisplay = getFixedFigs();
//	    // checking for user preference before setting the FixedFigures
//	    if (decimalLocation >= 0){
//	    	if (isCalculated()) { 
//	    	   	if(result.substring(decimalLocation + 1).length() > figsToDisplay){
//	                // we know we need to round to see if a value will be displayable with given digits
//	                // rounding code here.
//	                BigDecimal bd = getBigDecimal(result, figsToDisplay);
//	                result = bd.toString();
//    	   		}
//	    	} else {
//    	   		String formattedNumber = null;
//            	//Format only if the sig digits are set
//            	if (sigDigitsSet && sigDigits > 0)
//                    formattedNumber = SignificantFigures.format(Double.parseDouble(value.toString()), sigDigits);
//            	else
//            		formattedNumber = value.toString();
//            	
//	            	BigDecimal bd = new BigDecimal(formattedNumber);
//	                result = bd.toString();
//	    	}
//	    }
//	    return result;
        //return getValue();
	}
	public double doubleValue() { return Double.parseDouble(getValue()); }
    
    public String GetValueInStdUnits() {
        BigDecimal bd = new BigDecimal(getValue()).multiply(new BigDecimal(unit.getStdConversionFactor()));
        String result = bd.toString();
        if (bd.scale() > 0)
            result = result.substring(0, result.length() - (result.length() - bd.scale()));
        return result; 
    }
	public double GetValueInStdUnitsAsDouble() { 
		System.out.println("");
        BigDecimal bd = new BigDecimal(getValue()).multiply(new BigDecimal(unit.getStdConversionFactor()));
	    return bd.doubleValue(); 
	}
	
	/**
	 * Sets the value of the Amount object to the value in numbers the string represents
	 * If the string is "" the default value is used.  
	 * The default value is initialized as 0.000;
	 * 
	 * @param val - String representation of a number or "" or null;
	 */
	public void setValue(String val) {
		setValue(val, isCalculated());
    }
	/**
	 * Sets the value of the Amount object to the value in numbers the string represents
	 * If the string is "" the default value is used.  
	 * The default value is initialized as 0.000;
	 * isCalc indicates the status of the amount object whether it is a calculated quantity
	 * or is a user set quantity.
	 * 
	 * @param val - String representation of a number or "" or null;
	 */
	public void setValue(String val, boolean isCalc) {
		if (val != null && val.length() > 0) {
	        Double tstVal = new Double(val);
	        if (!(tstVal.isInfinite() && tstVal.isNaN())) {
                value.setLength(0);
                if (tstVal.doubleValue() != 0.0) {
                	//The value should be un-adultrated, so directly appending the val 
                	//to value string buffer.
                	value.append(val);
                	
                	// used to show historical values: values prior to application of sig digits update.
                    /*if (!sigDigitsSet || sigDigits <= 0) {
                        if (val.indexOf(".") == 0) {
                            value.append("0");
                        }
                        System.out.println("Value 1--:"+value);
                        value.append(val);
                        System.out.println("Value 2--:"+value);
                    } else {
                    	
                        System.out.println("Value 3--:"+value);
                        value.append(SignificantFigures.format(val, sigDigits)).toString();
                        System.out.println("Value 4--:"+value);
//                        value.append(new BigDecimal(SignificantFigures.format(val, sigDigits)).toString());
                    }*/
                } else {
                    value.append("0");
                }
                setCalculated(isCalc);
                setModified(true);
            }
	    } else { 
            setValue(defaultValue, isCalc);
        }
	} 
	
	public void setValue(double val) { 
		setValue(Double.toString(val)); 
	}
	
	public void setValue(double val, boolean isCalc) {
		setValue(Double.toString(val), isCalc); 
	}
	
	/**
	 * WARNING: using this will set the scale visible to users.
	 * 
	 * @param val
	 */
	public void setValue(Double val) { 
		if (!(val.isInfinite() && val.isNaN())) {
			BigDecimal bg = new BigDecimal(val.doubleValue());
			if (!isCalculated()) setFixedFigs(bg.scale());
			setValue(bg);
		} else {
		    setValue(defaultValue);
		}
	}
	public void setValue(Double val, boolean isCalculated) {
		if (!(val.isInfinite() && val.isNaN())) {
			BigDecimal bg = new BigDecimal(val.doubleValue());
			setValue(bg, isCalculated);
		} else {
		    setValue(defaultValue, true);
		}
	}

    public void setValue(BigDecimal val) { 
//      value = new BigDecimal(val.unscaledValue(), val.scale());  // this is an effective copy of BigDecimal
        setValue(val.doubleValue());  // this is an effective copy of BigDecimal
        if (!isCalculated()) setFixedFigs(val.scale());
        setModified(true);
    }
    public void setValue(BigDecimal val, boolean isCalc) {
        // Make sure there is no tie to the original value so that this one
        // will not change when the other is changed.
        setCalculated(isCalc);
        setValue(val);
    }
    
	public void SetValueInStdUnits(double val) {
	    if (!(Double.isNaN(val) || Double.isInfinite(val))) {
	        BigDecimal bd = new BigDecimal(val);
	    	bd = bd.setScale(getFixedFigs(), roundingPreference);
	        setValue(bd.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    } else {
	        setValue(defaultValue.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    }
	}
	public void SetValueInStdUnits(double val, boolean isCalc) {
	    if (!(Double.isNaN(val) || Double.isInfinite(val))) {
	    	setCalculated(isCalc);
	    	// avoid going through the check for valid value twice 
	    	BigDecimal bd = new BigDecimal(val  / unit.getStdConversionFactor());
	    	// Get a grip on the proper number of sig figs to display.
	    	// If scale is 1/1000 then we will get a value of 0.001 
	    	bd = bd.setScale(getFixedFigs(), roundingPreference);
	        setValue(bd);
	    } else {
	        setCalculated(true);
	        setValue(defaultValue.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    }
	}
	
	public void SetValueInStdUnits(Double val) {
	    if (!(val.isNaN() || val.isInfinite())) {
	        BigDecimal bd = new BigDecimal(val.doubleValue());
	        bd = bd.setScale(getFixedFigs(), roundingPreference);
	        setValue(bd.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    } else {
	        setValue(defaultValue.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    }
	}
	public void SetValueInStdUnits(Double val, boolean isCalc) {
	    if (!(val.isNaN() || val.isInfinite())) {
	    	setCalculated(isCalc);
	    	// avoid going through the check for valid value twice 
	    	BigDecimal bd = new BigDecimal(val.doubleValue());
	    	bd = bd.setScale(getFixedFigs(), roundingPreference);
	        setValue(bd.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    } else {
            setCalculated(true);
	        setValue(defaultValue.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
	    }
	}
    public void SetValueInStdUnits(String val, boolean isCalc) {
        if (val != null && val.length() > 0) {
            Double tstVal = new Double(val);
            if (!(tstVal.isInfinite() && tstVal.isNaN())) {
                SignificantFigures sigs = new SignificantFigures(val);
                setCalculated(isCalc);
                // avoid going through the check for valid value twice
                // Get a grip on the proper number of sig figs to display.
                // If scale is 1/1000 then we will get a value of 0.001
                String result = val;
                if (sigs.getNumberSignificantFigures() > 0) 
                    result = SignificantFigures.format(sigs.doubleValue() / unit.getStdConversionFactor(), sigs.getNumberSignificantFigures());
                setValue(result);

//                BigDecimal bd = new BigDecimal(sigs.doubleValue() / unit.getStdConversionFactor());
//                bd.setScale(getFixedFigs(), roundingPreference);
//                setValue(bd);
            } else {
                setCalculated(true);
                setValue(defaultValue.divide(new BigDecimal(unit.getStdConversionFactor()), roundingPreference));
            }
        }
    }

	public Unit getUnit() { return unit; }
	public void setUnit(Unit toUnit) {
		// Invariant: Unit != null
		if (toUnit != null && !unit.equals(toUnit)) {
			// convert value to new unit.
            if(!getValue().trim().equals("") && 
            		Double.parseDouble(getValue().trim())!= 0.0)
            {
            	String convVal = convertValue(toUnit);
                if (!sigDigitsSet)
                    setFixedFigs(getFixedFigsBasedOnString(convVal));
            }
			unit.deepCopy(toUnit);
		}
	}

	public UnitType getUnitType() { return unit.getType(); }
	
	/** 
	 * Value the amount object will use when the value is set to "" or null.
	 * 
	 * @return double 
	 */
	public double getDefaultValue() { return defaultValue.doubleValue(); }
	/**
	 * Sets value that the amount object will use when created without a value, and used
	 * when a null string is entered in setValue as the null string indicates the defaults are
	 * to be imposed.
	 * 
	 * @param val - value to be used as default.  Must be a number.
	 */
	public void setDefaultValue(String val) 
	{
	    defaultValue = new BigDecimal(val);
	    setModified(true);
	}
	/**
	 * Sets value that the amount object will use when created without a value, and used
	 * when a null string is entered in setValue as the null string indicates the defaults are
	 * to be imposed.
	 * 
	 * @param val - value to be used as default.
	 */
	public void setDefaultValue(double val) 
	{
	    defaultValue = new BigDecimal(val);
	    setModified(true);
	}

	/**
	 * Standard figures for display come from the unit itself.
     * This can be overwritten by adding a user preference for figures to be displayed
     * Ultimately both are overridden should the user enter a value by hand.  Then
     * the Sig figs of that value are what are important to display.
     * 
	 * @return int = figures after the decimal point to display
	 */
	public int getFixedFigs() { 
	    int figsToDisplay = unit.getStdDisplayFigs();
	    if (isCalculated()) {
            if (userPrefFigs >= 0) figsToDisplay = userPrefFigs;
//            else if (displayedFigs >= 0) figsToDisplay = displayedFigs;
        } else if (displayedFigs >= 0) figsToDisplay = displayedFigs;
        return figsToDisplay; 
	}
    
	/**
	 * Setting this value will cause it to override any default values.
	 * @param fixedFigs - number of places after the decimal to display
	 */
	public void setFixedFigs(int fixedFigs) { 
	    if (displayedFigs != fixedFigs) { 
	    	displayedFigs = fixedFigs; 
	    	setModified(true);
	    }
	}
    
    public int getUserPrefFigs() { return userPrefFigs; }
    /**
     * 
     * @param fixedFigs - fixed number of places after decimal point to display figures.
     */
    public void setUserPrefFigs(int fixedFigs) {
    	if (userPrefFigs != fixedFigs) {
    		userPrefFigs = fixedFigs; 
    		setModified(true);
    	}
    }

    /**
     * 
     * @param val the string representing the number.
     * @return number of digits behind the decimal point or 0.
     */
	private int getFixedFigsBasedOnString(String val) {
        int result = 0;
        if (val.indexOf(".") >= 0) 
        	result = val.length() - (val.indexOf(".") + 1);
        return result;
    }
	/**
	 * Indicates whether or not the user entered this information is calculated ( = true) or entered by hand ( = false)
	 * 
	 * @return false if user entered true if calculated
	 */
	public boolean isCalculated() { return calculated; }
	
	/** 
	 * Does trigger modified events
	 * 
	 * @param calc = true if this amount is calculated or false if it was set by a user.
	 */
	public void setCalculated(boolean calc) { 
        if (calc != calculated) {
            calculated = calc;
            displayedFigs = -1;
            if (calc) {
                if (userPrefFigs >= 0) displayedFigs = userPrefFigs;
            }
            setModified(true);
        }
    }

	/**
	 * Use to determine if this amount value is set to its default value.
	 * 
	 * @return boolean true for equals and false otherwise.
	 */
	public boolean isValueDefault() { return CeNNumberUtils.doubleEquals(doubleValue(), getDefaultValue(), DELTA_FIGS); }
	
//    private BigDecimal getBigDecimal(String value, int scale) {
//        BigDecimal result = new BigDecimal(value);
//        result = result.setScale(scale, roundingPreference);
//        return result;
//    }
    
//
// Method overrides
//
	public String toString() { 
	    String result = getValue();
	    if (isCalculated()) result = GetValueForDisplay();
	    return result;
	}
	
	public boolean equals(Object obj)
	{
		boolean result = false;
		if (obj instanceof Amount)
		{
			Amount tmpAmt = (Amount) obj;
            
			// Used to be in the if statement below.
            // Fixed Figs doesn't mean anything anymore after sig figs.
            //          getFixedFigs() == tmpAmt.getFixedFigs() &&
			if (getValue().equals(tmpAmt.getValue()) &&
			    isCalculated() == tmpAmt.isCalculated() &&
                sigDigits == tmpAmt.sigDigits &&
				getUnit().equals(tmpAmt.getUnit())) result = true;
		}
		return result;
	}
    
	public int hashCode()
	{
		String[] result = Double.toString(this.doubleValue() * HASH_PRIME).split(".");
		int iTmp = 0;
		if (result.length > 0) iTmp = Integer.parseInt(result[0]);
		return (HASH_PRIME + iTmp + this.getUnit().hashCode());
	}

// 
// Implemented Interface methods
//
	public void deepCopy(Object source) {
	    if (source instanceof Amount) {
	        Amount src = (Amount) source;
	        value.setLength(0);
	        value.append(src.value.toString());
	        defaultValue = new BigDecimal(src.defaultValue.toString());
	        displayedFigs = src.displayedFigs;
            sigDigitsSet = src.sigDigitsSet;
            sigDigits = src.sigDigits;
            userPrefFigs = src.userPrefFigs;
	        calculated = src.calculated;
	        unit.deepCopy(src.unit);
	    }
	}
	
	public Object deepClone() {
	    Amount target = new Amount(unit.getType());
	    target.deepCopy(this);
	    return target;
	}
   
	/**
	 * @return Returns the sigDigits.
	 */
	public int getSigDigits() {
		return sigDigits;
	}
	
	/**
	 * @param sigDigits The sigDigits to set.
	 */
	public void setSigDigits(int sigDigits) {
		if (this.sigDigits != sigDigits) {
			this.sigDigits = sigDigits;
			sigDigitsSet = true;
    		setModified(true);
		}
	}
    
    public boolean getSigDigitsSet() {
        return sigDigitsSet;
    }
    
    public void setSigDigitsSet(boolean isSigDigitsSet) {
    	if (sigDigitsSet != isSigDigitsSet) {
    		sigDigitsSet = isSigDigitsSet;
    		setModified(true);
    	}
    }
}