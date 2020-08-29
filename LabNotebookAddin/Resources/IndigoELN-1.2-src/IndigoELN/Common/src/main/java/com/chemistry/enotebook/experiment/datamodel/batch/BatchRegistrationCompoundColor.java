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
 * Created on Nov 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */

// this is not going to be used until the CompoundRegistration is ready to take color it
public class BatchRegistrationCompoundColor extends ObservableObject implements DeepClone, DeepCopy {

	private static final long serialVersionUID = 4998400318637301253L;

	private float RValue = 0.0f;

	private float GValue = 0.0f;

	private float BValue = 0.0f;

	private String description = new String();

	public BatchRegistrationCompoundColor() {

	}

	/**
	 * @return Returns the bValue.
	 */
	public float getBValue() {
		return BValue;
	}

	/**
	 * @param value
	 *            The bValue to set.
	 */
	public void setBValue(float value) {
		BValue = value;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the gValue.
	 */
	public float getGValue() {
		return GValue;
	}

	/**
	 * @param value
	 *            The gValue to set.
	 */
	public void setGValue(float value) {
		GValue = value;
	}

	/**
	 * @return Returns the rValue.
	 */
	public float getRValue() {
		return RValue;
	}

	/**
	 * @param value
	 *            The rValue to set.
	 */
	public void setRValue(float value) {
		RValue = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepCopy#deepCopy(java.lang.Object)
	 */
	public void deepCopy(Object source) {
		if (source != null) {
			BatchRegistrationCompoundColor colorInstance = (BatchRegistrationCompoundColor) source;
			RValue = colorInstance.RValue;
			GValue = colorInstance.GValue;
			BValue = colorInstance.BValue;
			description = colorInstance.description;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchRegistrationCompoundColor target = new BatchRegistrationCompoundColor();
		target.RValue = this.RValue;
		target.GValue = this.GValue;
		target.BValue = this.BValue;
		target.description = this.description;
		return target;
	}

}
