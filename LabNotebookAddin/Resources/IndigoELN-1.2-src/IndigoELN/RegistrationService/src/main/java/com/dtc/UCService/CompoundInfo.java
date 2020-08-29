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
package com.dtc.UCService;

import org.omg.CORBA.portable.IDLEntity;

public final class CompoundInfo implements IDLEntity {

	private static final long serialVersionUID = -3200924560306792619L;
	
	public CompoundInfo() {
		compNumber = "";
		isomerCode = "";
	}

	public CompoundInfo(String compNumber, String isomerCode) {
		this.compNumber = "";
		this.isomerCode = "";
		this.compNumber = compNumber;
		this.isomerCode = isomerCode;
	}

	public String compNumber;
	public String isomerCode;
}
