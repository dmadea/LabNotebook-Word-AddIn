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
package com.chemistry.enotebook.experiment.common.units;

import com.chemistry.enotebook.experiment.common.GenericCode;

/**
 * 
 * 
 *
 */
public class Unit extends GenericCode implements Comparable {
	
	private static final long serialVersionUID = -1688969122894961308L;
	
	private static final int HASH_PRIME = 19471;
	private UnitType type;
	private String displayValue = "";
	private String stdCode = "--";
	private double stdConversionFactor = -1;
	private int stdDisplayFigs = 3;
	private static int nextOrdinal = 0;
	private final int ordinal;

	/**
	 * Returns SCALAR version of unit.
	 * 
	 */
	public Unit() {
		this("");
	}

	public Unit(String code) {
		super();
		setCode(code);
		this.ordinal = nextOrdinal++;
	}

	/**
	 * @param code
	 * @param type
	 * @param displayValue
	 * @param description
	 * @param stdCode
	 * @param stdConversionFactor
	 */
	public Unit(String code, UnitType type, String displayValue, String description, String stdCode, double stdConversionFactor,
			int stdDisplayFigs) {
		super();
		this.code = code;
		this.type = type;
		this.displayValue = displayValue;
		this.description = description;
		this.stdCode = stdCode;
		this.stdConversionFactor = stdConversionFactor;
		this.stdDisplayFigs = stdDisplayFigs;
		this.ordinal = nextOrdinal++;
	}

	public void finalize() {
		type = null;
	}

	// Removal of this function seems to reinstate integrity of unit being saved.
	// 1.4.2_06 doesn't guarantee order of update of fields in an introspected object.
	// Prior to this release things worked fine with this override in place.
	// /**
	// * @return Returns the code.
	// */
	// public String getCode() {
	// return code;
	// }
	/**
	 * This is a kludge because of the necessity to set a single value upon load from storage. This needs to be revisited when
	 * load/save happen in an easier manner.
	 * 
	 * @param code
	 *            The code to set - entire unit is rebuilt from UnitCache.getUnit(code)
	 */
	public void setCode(String code) {
		deepCopy(UnitCache.getInstance().getUnit(code));
	}

	/**
	 * @return Returns the displayValue.
	 */
	public String getDisplayValue() {
		return displayValue;
	}

	/**
	 * @param displayValue
	 *            The displayValue to set.
	 */
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	/**
	 * @return Returns the stdCode.
	 */
	public String getStdCode() {
		return stdCode;
	}

	/**
	 * @param stdCode
	 *            The stdCode to set.
	 */
	public void setStdCode(String stdCode) {
		this.stdCode = stdCode;
	}

	/**
	 * @return Returns the stdConversionFactor.
	 */
	public double getStdConversionFactor() {
		return stdConversionFactor;
	}

	/**
	 * @param stdConversionFactor
	 *            The stdConversionFactor to set.
	 */
	public void setStdConversionFactor(double stdConversionFactor) {
		this.stdConversionFactor = stdConversionFactor;
	}

	/**
	 * @return Returns the type.
	 */
	public UnitType getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(UnitType type) {
		this.type = type;
	}

	/**
	 * Used to indicate what the default value for displaying figures after the decimal point should be for this unit type and code.
	 * 
	 * @return
	 */
	public int getStdDisplayFigs() {
		return stdDisplayFigs;
	}

	/**
	 * Used to set the default value for displaying figures after the decimal point should be for this unit type and code.
	 * 
	 * @param displayFigs
	 */
	public void setStdDisplayFigs(int displayFigs) {
		stdDisplayFigs = displayFigs;
	}

	// 
	// Implements
	//
	public int compareTo(Object o) {
		int comp = this.hashCode() - o.hashCode();
		if (o instanceof Unit) {
			comp = this.type.compareTo(((Unit) o).getType());
			if (comp == 0)
				comp = this.ordinal - ((Unit) o).ordinal;
		}
		return comp;
	}

	//
	// Overrides
	//
	public String toString() {
		return this.displayValue;
	}

	public int hashCode() {
		return (HASH_PRIME + ordinal * HASH_PRIME) + this.type.hashCode();
	}

	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Unit) {
			Unit test = (Unit) obj;
			result = (type.equals(test.type) && code.equals(test.code));
		}
		return result;
	}

	public void deepCopy(Object source) {
		if (source instanceof Unit) {
			Unit src = (Unit) source;
			super.deepCopy(source);
			type = src.type;
			displayValue = src.displayValue;
			stdCode = src.stdCode;
			stdConversionFactor = src.stdConversionFactor;
			stdDisplayFigs = src.stdDisplayFigs;
		}
	}

	public Object deepClone() {
		return new Unit(getCode());
	}
	
	public String toXML(){
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<Unit>");
		xmlbuff.append("<Code>"+this.code+"</Code>");
		xmlbuff.append("<Description>"+ this.description +"</Description>");
		xmlbuff.append("</Unit>");
		return xmlbuff.toString();
	}
}