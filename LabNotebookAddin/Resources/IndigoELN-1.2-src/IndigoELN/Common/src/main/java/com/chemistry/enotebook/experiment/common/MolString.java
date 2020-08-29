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
/**
 * 
 */
package com.chemistry.enotebook.experiment.common;

import java.io.Serializable;

public class MolString implements Serializable {
	
	
	private static final long serialVersionUID = -3239522704094107986L;

	String molString;
	int index;

	public MolString() {
		molString = "";
		index = 0;
	}

	public MolString(String s, int a) {
		molString = s;
		index = a;
	}

	public String toString() {
		return molString;
	}

	public void setMolString(String molString) {
		this.molString = molString;
	}

	public String getMolString() {
		return molString;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}