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

public class PurificationServiceTubeInfoExternal implements Serializable {

	private static final long serialVersionUID = -7922407834133594621L;

	private String tubeGUID;
	private PurificationServiceCompoundInfoExternal compound;

	public PurificationServiceCompoundInfoExternal getCompound() {
		return compound;
	}

	public void setCompound(PurificationServiceCompoundInfoExternal compound) {
		this.compound = compound;
	}

	public String getTubeGUID() {
		return tubeGUID;
	}

	public void setTubeGUID(String tubeGUID) {
		this.tubeGUID = tubeGUID;
	}

	public String toXML() {
		return "";
	}

}
