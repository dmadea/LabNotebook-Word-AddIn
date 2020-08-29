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
 * Created on Nov 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.common;

import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

/**
 * 
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class TemperatureRange extends ObservableObject implements DeepClone,
		DeepCopy {

	private static final long serialVersionUID = -8734677680972149788L;

	private double lower;
	private double upper;
	private String comment;

	public void reset() {
		lower = 0.0;
		upper = 0.0;
		comment = "";
		setModified(true);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		if (this.comment == null || !this.comment.equals(comment)) {
			this.comment = comment;
			setModified(true);
		}
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		if (!(this.lower == lower)) {
			this.lower = lower;
			setModified(true);
		}
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		if (!(this.upper == upper)) {
			this.upper = upper;
			setModified(true);
		}
	}

	public String toString() {
		if (lower == 0 && upper == 0)
			return "";
		return "Range " + getLower() + " ~ " + getUpper();
	}

	public void deepCopy(Object srcTemp) {
		if (srcTemp instanceof TemperatureRange) {
			TemperatureRange srcRange = (TemperatureRange) srcTemp;
			lower = srcRange.lower;
			upper = srcRange.upper;
			comment = srcRange.comment;
			setModified(true);
		}
	}

	public Object deepClone() {
		TemperatureRange target = new TemperatureRange();
		target.deepCopy(this);
		return target;
	}
}
