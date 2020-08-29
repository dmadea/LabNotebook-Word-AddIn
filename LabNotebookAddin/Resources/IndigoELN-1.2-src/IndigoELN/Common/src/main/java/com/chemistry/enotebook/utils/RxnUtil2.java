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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ReactionProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;

import java.io.FileOutputStream;
import java.util.List;

public class RxnUtil2 {
	static ChemistryDelegate chemObj = null;

	static {
			chemObj = new ChemistryDelegate();
	}

	private void WriteISISSketch(String filename, byte[] sketch) {
		try {
			sketch = chemObj.convertChemistry(sketch, null, "ISIS Sketch");
			FileOutputStream skcOutStrm = new FileOutputStream(filename);
			skcOutStrm.write(sketch);
			skcOutStrm.flush();
			skcOutStrm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getMDLRxnString(List monomers, List productes) {

		ReactionProperties rp = null;
		try {
			rp = new ReactionProperties();
			int numreac = monomers.size();
			int numprod = productes.size();

			for (int i = 0; i < numreac; i++) {

				rp.Reactants.add(((String) monomers.get(i)).getBytes());

			}
			for (int i = 0; i < numprod; i++) {
				rp.Products.add(((String) productes.get(i)).getBytes());
			}

			rp = chemObj.combineReactionComponents(rp);

			return new String(rp.Reaction);
			// chemObj.convertChemistry(arg0, arg1, arg2)
			//WriteISISSketch("C:\\testcen.skc", rp.Reaction);
			//assertFalse("combineReactionComponents(rp):reaction!=rxn", chemObj.isChemistryEqual(rp.Reaction, rxn.getBytes()));

		} catch (ChemUtilAccessException e) {
			e.printStackTrace();
		}
		return "";
	}
}
