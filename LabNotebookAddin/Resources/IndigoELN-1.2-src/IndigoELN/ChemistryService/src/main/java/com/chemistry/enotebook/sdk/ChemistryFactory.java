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
package com.chemistry.enotebook.sdk;

import com.chemistry.enotebook.sdk.indigo.ChemistryReaction;
import com.chemistry.enotebook.sdk.indigo.ChemistryStructure;
import com.chemistry.enotebook.sdk.indigo.ChemistryUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChemistryFactory
{
	public static final Log log = LogFactory.getLog(ChemistryFactory.class);
	
	public static synchronized ChemistryUtilInterface getNewUtilityObj() throws ChemUtilInitException {
		if (ourChemistryUtilInterface == null) {
			ourChemistryUtilInterface = new ChemistryUtil();
		}
		return ourChemistryUtilInterface;
	}

	public static synchronized ChemistryStructureInterface getNewStructureObj() throws ChemUtilInitException {
		if (ourChemistryStructureInterface == null) {
			ourChemistryStructureInterface = new ChemistryStructure();
		}
		return ourChemistryStructureInterface;
	}

	public static synchronized ChemistryReactionInterface getNewReactionObj() throws ChemUtilInitException {
		if (ourChemistryReactionInterface == null) {
			ourChemistryReactionInterface = new ChemistryReaction();
		}
		return ourChemistryReactionInterface;
	}

	private static ChemistryUtilInterface ourChemistryUtilInterface;
	private static ChemistryStructureInterface ourChemistryStructureInterface;
	private static ChemistryReactionInterface ourChemistryReactionInterface;
}
