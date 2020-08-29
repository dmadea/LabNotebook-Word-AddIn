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
import com.chemistry.enotebook.purificationservice.classes.PurificationServiceTubeInfoExternal;

public class PurificationServiceTubeInfo extends CeNAbstractModel{
	
	public static final long serialVersionUID = 7526472295622776147L;
	
	private String tubeGUID;
	private PurificationServiceCompoundInfo compound;
	
	
	public PurificationServiceCompoundInfo getCompound() {
		return compound;
	}


	public void setCompound(PurificationServiceCompoundInfo compound) {
		this.compound = compound;
	}


	public String getTubeGUID() {
		return tubeGUID;
	}


	public void setTubeGUID(String tubeGUID) {
		this.tubeGUID = tubeGUID;
	}


	public String toXML()
	{
		return "";
	}
	
	public PurificationServiceTubeInfoExternal convertToPurificationServiceTubeInfoExternal() {
		PurificationServiceTubeInfoExternal purificationServiceTubeInfoExternal = new PurificationServiceTubeInfoExternal();

		purificationServiceTubeInfoExternal.setTubeGUID(this.getTubeGUID());
		purificationServiceTubeInfoExternal.setCompound(this.getCompound().convertToPurificationServiceCompoundInfoExternal());
		
		return purificationServiceTubeInfoExternal;
	}
	
	public static PurificationServiceTubeInfoExternal[] convertToArrayPurificationServiceTubeInfoExternal(PurificationServiceTubeInfo[] purificationServiceTubeInfos) {
		PurificationServiceTubeInfoExternal[] purificationServiceTubeInfoExternals = new PurificationServiceTubeInfoExternal[purificationServiceTubeInfos.length];
		
		for(int i = 0; i < purificationServiceTubeInfos.length; i++) {
			purificationServiceTubeInfoExternals[i] = purificationServiceTubeInfos[i].convertToPurificationServiceTubeInfoExternal();
		}

		return purificationServiceTubeInfoExternals;
	}
}
