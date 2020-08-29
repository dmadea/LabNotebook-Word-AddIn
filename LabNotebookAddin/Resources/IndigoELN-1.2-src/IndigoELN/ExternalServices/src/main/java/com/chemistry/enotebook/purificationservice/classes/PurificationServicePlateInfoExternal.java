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
package com.chemistry.enotebook.purificationservice.classes;

import java.io.Serializable;

public class PurificationServicePlateInfoExternal implements Serializable {

	private static final long serialVersionUID = 4301272258703763546L;

	String gblPlateBarCode = "";
	// All compounds on the plate(wells)
	PurificationServiceCompoundInfoExternal[] compounds;

	public PurificationServiceCompoundInfoExternal[] getCompounds() {
		return compounds;
	}

	public void setCompounds(PurificationServiceCompoundInfoExternal[] compounds) {
		this.compounds = compounds;
	}

	public String getGblPlateBarCode() {
		return gblPlateBarCode;
	}

	public void setGblPlateBarCode(String gblPlateBarCode) {
		this.gblPlateBarCode = gblPlateBarCode;
	}

	public String toXML() {
		return "";
	}

}
