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
package com.chemistry.enotebook.domain.container;

public class ContainerType {

	public static final String PLATE = "U_D_PLATE";
	public static final String RACK = "U_D_RACK";
	public static final String VIAL = "U_D_VIAL";
	public static final String TOTE = "U_D_TOTE";
	public static final String[] types = { PLATE, RACK, VIAL, TOTE };

	public static int getType(String typw) {

		for (int i = 0; i < types.length; i++) {
			if (types[i].equalsIgnoreCase(typw)) {
				return i;
			}

		}
		return -1;
	}
}
