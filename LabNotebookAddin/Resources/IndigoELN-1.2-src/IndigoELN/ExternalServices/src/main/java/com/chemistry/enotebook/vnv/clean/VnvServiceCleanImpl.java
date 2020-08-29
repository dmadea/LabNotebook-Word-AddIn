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
package com.chemistry.enotebook.vnv.clean;

import com.chemistry.enotebook.vnv.VnvService;
import com.chemistry.enotebook.vnv.classes.IVnvResult;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;
import com.chemistry.enotebook.vnv.classes.UniquenessCheckVO;
import com.chemistry.enotebook.vnv.classes.VnvResultImpl;
import com.chemistry.enotebook.vnv.exceptions.VnvRuntimeException;

public class VnvServiceCleanImpl implements VnvService {

	@Override
	public boolean checkHealth() {
		return true;
	}
	
	@Override
	public String executeVnV(String molfile, String stereoisomerCode) throws VnvRuntimeException {
		return null;
	}

	@Override
	public IVnvResult validateStructureAssignStereoIsomerCode(String molStructure) throws VnvRuntimeException {
		return new VnvResultImpl(true, molStructure, false, null, "", 0, true, "NOSTC", new String[] { "NOSTC" });
	}

	@Override
	public IVnvResult validateStructureWithStereoIsomerCode(String molStructure, String inputSic) throws VnvRuntimeException {
		return new VnvResultImpl(true, molStructure, false, null, "", 0, true, "NOSTC", new String[] { "NOSTC" }); 
	}

	@Override
	public UniquenessCheckVO checkUniqueness(String molfile, String stereoisomerCode, boolean includeLegacy) throws VnvRuntimeException {
		UniquenessCheckVO vo = new UniquenessCheckVO();

		String keyString = "";

		UcCompoundInfo ucRec = new UcCompoundInfo();
		
		ucRec.setIsExact(true);
		ucRec.setIsIsomer(true);
		ucRec.setIsLegacy(false);
		ucRec.setIsNewParent(true);
		ucRec.setRegNumber(keyString);

		if (!molfile.startsWith("\r\n")) {
			molfile = "\r\n" + molfile;
		}
		
		ucRec.setMolStruct(molfile);

		// Fill in some Data
		ucRec.setIsomerCode(stereoisomerCode);
		ucRec.setMolFormula(keyString);
		ucRec.setMolWgt("0");
		ucRec.setComments("");

		vo.addResult(ucRec);

		return vo;
	}
}
