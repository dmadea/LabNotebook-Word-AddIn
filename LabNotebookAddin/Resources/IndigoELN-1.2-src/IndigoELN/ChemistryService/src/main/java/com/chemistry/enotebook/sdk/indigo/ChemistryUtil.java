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
package com.chemistry.enotebook.sdk.indigo;

import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemistryUtilInterface;
import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;

public class ChemistryUtil implements ChemistryUtilInterface {
	//TODO remove
	public byte[] convertChemistry(byte[] inBuff, String inBuffType, String outBuffType) throws ChemUtilAccessException {
		return inBuff;
	}

	public void dispose() {
	}

	public boolean isChemistryEmpty(byte[] chemistry) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		indigo.setOption("ignore-stereochemistry-errors", "true");
		IndigoObject indigoObject = null;
		try {
			indigoObject = indigo.loadReaction(chemistry);
		} catch (Exception e) {
			indigoObject = indigo.loadMolecule(chemistry);
		}
		return indigoObject.countAtoms() <= 0;
	}

	public boolean isChemistryEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException {
		Indigo indigo = new Indigo();
		indigo.setOption("ignore-stereochemistry-errors", "true");
		String str1 = new String(chemistry1);
		String str2 = new String(chemistry2);
		IndigoObject handle1 = null;
		IndigoObject handle2 = null;
		try {
			handle1 = indigo.loadReaction(str1);
			handle2 = indigo.loadReaction(str2);
		} catch (Exception e) {
			handle1 = indigo.loadMolecule(str1);
			handle2 = indigo.loadMolecule(str2);
		}
		return indigo.exactMatch(handle1, handle2) != null;
	}
}
