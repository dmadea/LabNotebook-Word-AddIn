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
package com.chemistry.enotebook.domain.purificationservice;

import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.purificationservice.classes.PurificationServicePlateInfoExternal;

public class PurificationServicePlateInfo extends CeNAbstractModel{
	
	public static final long serialVersionUID = 7526472295622776147L;
	
	String gblPlateBarCode ="";
	//All compounds on the plate(wells)
	PurificationServiceCompoundInfo[] compounds;
	
	
	public PurificationServiceCompoundInfo[] getCompounds() {
		return compounds;
	}


	public void setCompounds(PurificationServiceCompoundInfo[] compounds) {
		this.compounds = compounds;
	}


	public String getGblPlateBarCode() {
		return gblPlateBarCode;
	}


	public void setGblPlateBarCode(String gblPlateBarCode) {
		this.gblPlateBarCode = gblPlateBarCode;
	}


	public String toXML()
	{
		return "";
	}
	
	public PurificationServicePlateInfoExternal convertToPurificationServicePlateInfoExternal() {
		PurificationServicePlateInfoExternal purificationServicePlateInfoExternal = new PurificationServicePlateInfoExternal();

		purificationServicePlateInfoExternal.setGblPlateBarCode(this.getGblPlateBarCode());
		purificationServicePlateInfoExternal.setCompounds(PurificationServiceCompoundInfo.convertToArrayPurificationServiceCompoundInfoExternal(this.getCompounds()));
		
		return purificationServicePlateInfoExternal;
	}
	
	public static PurificationServicePlateInfoExternal[] convertToArrayPurificationServiceTubeInfoExternal(PurificationServicePlateInfo[] purificationServicePlateInfos) {
		PurificationServicePlateInfoExternal[] purificationServicePlateInfoExternals = new PurificationServicePlateInfoExternal[purificationServicePlateInfos.length];
		
		for(int i = 0; i < purificationServicePlateInfos.length; i++) {
			purificationServicePlateInfoExternals[i] = purificationServicePlateInfos[i].convertToPurificationServicePlateInfoExternal();
		}

		return purificationServicePlateInfoExternals;
	}
}
