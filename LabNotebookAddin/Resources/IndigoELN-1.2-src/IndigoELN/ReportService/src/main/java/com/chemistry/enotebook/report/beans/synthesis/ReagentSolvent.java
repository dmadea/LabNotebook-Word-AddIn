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
package com.chemistry.enotebook.report.beans.synthesis;

import com.chemistry.enotebook.report.utils.TextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReagentSolvent {
	private static final Log log = LogFactory.getLog(ReagentSolvent.class);
	
	private String step = "";
	private String name = "";
	private String weight = "";
	private String volume = "";
	private String moles = "";
	private String molarity = "";
	private String solvent = "";
	private String hazards = "";
	private String weightInWell = "";
	private String volumeInWell = "";
	private String deadVolume = "";
	
	public void dispose() {
		
	}
	
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getMoles() {
		return moles;
	}
	public void setMoles(String moles) {
		this.moles = moles;
	}
	public String getMolarity() {
		return molarity;
	}
	public void setMolarity(String molarity) {
		this.molarity = molarity;
	}
	public String getSolvent() {
		return solvent;
	}
	public void setSolvent(String solvent) {
		this.solvent = solvent;
	}
	public String getHazards() {
		return hazards;
	}
	public void setHazards(String hazards) {
		this.hazards = hazards;
	}
	
	public String getWeightInWell() {
		return weightInWell;
	}

	public void setWeightInWell(String weightInWell) {
		this.weightInWell = weightInWell;
	}

	public String getVolumeInWell() {
		return volumeInWell;
	}

	public void setVolumeInWell(String volumeInWell) {
		this.volumeInWell = volumeInWell;
	}

	public String getDeadVolume() {
		return deadVolume;
	}

	public void setDeadVolume(String deadVolume) {
		this.deadVolume = deadVolume;
	}

	public String toXml() {
		StringBuffer buff = new StringBuffer("<reagentSolvent>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</reagentSolvent>");
		log.debug(buff.toString());
		return buff.toString();
	}


}
