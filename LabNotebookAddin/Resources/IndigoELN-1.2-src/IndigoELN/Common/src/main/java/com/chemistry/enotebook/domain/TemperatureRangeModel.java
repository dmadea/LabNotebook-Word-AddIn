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


public class TemperatureRangeModel extends CeNAbstractModel {
	public static final long serialVersionUID = 7526472295622776147L;

	private double lower;
	private double upper;
	private String comment="";

	public void reset() {
		lower = 0.0;
		upper = 0.0;
		comment = "";
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		if (comment != null)
			this.comment = comment;
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

	public String toXML() {
		return "";
	}
	
	public String toString()
	{
		if (lower == 0 && upper == 0) {
			return "";
		} else {
			return "Range " + lower + " ~ " + upper + ((getComment() == null) ? "" : ", " + this.getComment());
		}
	}
}
