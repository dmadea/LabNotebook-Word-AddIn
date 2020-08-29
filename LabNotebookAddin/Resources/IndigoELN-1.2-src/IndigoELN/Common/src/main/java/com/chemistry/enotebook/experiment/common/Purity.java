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
 * Purity.java
 * 
 * Created on Oct 14, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.common;

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @date Oct 14, 2004
 */
public class Purity extends GenericCode {
	// method = code = Ex: HPLC, LCMS, NMR, etc.
	// descr = What these methods mean.

	private static final long serialVersionUID = -7465923512936938651L;
	
	private String operator; // =, >, <, ~
	private Amount purityValue = new Amount(UnitType.SCALAR);
	private Date date = new Date(System.currentTimeMillis());
	private String comments;
	private String sourceFile;
	private boolean isRepresentativePurity;

	public Purity() {
		super();
		purityValue.addObserver(this);
	}

	/**
	 * @param method
	 * @param operator
	 * @param value
	 */
	public Purity(String method, String description, String operator, Amount value, Date dateMeasured, String comments,boolean isRepresentative,String sourceFile) {
		super();
		code = method;
		this.description = description;
		this.operator = operator;
		purityValue.deleteObserver(this);
		purityValue = value;
		purityValue.addObserver(this);
		date = dateMeasured;
		this.comments = comments;
		this.isRepresentativePurity = isRepresentative;
		this.sourceFile = sourceFile;
	}

	//
	// Getters/Setters
	//
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
		setModified(true);
	}

	public Amount getPurityValue() {
		return purityValue;
	}

	public void setPurityValue(Amount value) {
		purityValue.deleteObserver(this);
		purityValue = value;
		purityValue.addObserver(this);
		setModified(true);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date dateMeasured) {
		this.date = dateMeasured;
		setModified(true);
	}

	public String getComments() {
		if (comments == null)
			return "";
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
		setModified(true);
	}
	
	

	public boolean isRepresentativePurity() {
		return isRepresentativePurity;
	}

	public void setRepresentativePurity(boolean isRepresentativePurity) {
		this.isRepresentativePurity = isRepresentativePurity;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	// 
	// Overrides / Implementations
	//
	/**
	 * Utility function to help string together responses for purity types in a list Probably better done where needed.
	 */
	public static String toString(List purities) {
		StringBuffer result = new StringBuffer("");
		if (purities != null || purities.size() > 0) {
			String returnStr = "";
			for (Iterator i = purities.iterator(); i.hasNext();) {
				Purity p =(Purity) i.next();
				if(p.isRepresentativePurity)
				{
				result.append(p.toString() + "\n");
				}
			}
		}
		return result.toString();
	}

	/**
	 * Uses HTML rendering for the new Lines
	 * 
	 * @param purities
	 * @return
	 */
	public static String toToolTipString(List purities) {
		StringBuffer result = new StringBuffer("<html>");
		if (purities != null || purities.size() > 0) {
			String returnStr = "";
			for (Iterator i = purities.iterator(); i.hasNext();) {
				result.append(((Purity) i.next()).toString() + "<br>");
			}
		}
		result.append("</html>");
		return result.toString();
	}

	public String toString() {
		return getCode() + " purity " + getOperator() + " " + getPurityValue().toString() + "% " + getComments() + "  RepresentativePurity:"+ this.isRepresentativePurity;
	}

	public void deepCopy(Object source) {
		if (source instanceof Purity) {
			Purity inst = (Purity) source;
			super.deepCopy(inst);
			date = (Date) inst.date.clone();
			comments = inst.comments;
			operator = inst.operator;
			purityValue.deepCopy(inst.purityValue);
		}
	}

	public Object deepClone() {
		Purity target = new Purity();
		target.deepCopy(this);
		return target;
	}
}
