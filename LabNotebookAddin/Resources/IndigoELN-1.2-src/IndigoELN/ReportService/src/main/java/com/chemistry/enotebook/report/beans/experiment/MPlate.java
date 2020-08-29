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
package com.chemistry.enotebook.report.beans.experiment;

import java.util.ArrayList;
import java.util.List;

public class MPlate {

	private String key = "";
	private String name = "";
	private String type = "";
	private String stepName = "";
	List<MonomerPlateWell> wells = new ArrayList<MonomerPlateWell>();
	
	public void dispose() {
		if (wells != null)
			wells.clear();
	}
	
	private MonomerPlateTable plateTable = new MonomerPlateTable();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<MonomerPlateWell> getWells() {
		return wells;
	}
	public void setWells(List<MonomerPlateWell> wells) {
		this.wells = wells;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public MonomerPlateTable getPlateTable() {
		return plateTable;
	}
	public void setPlateTable(MonomerPlateTable plateTable) {
		this.plateTable = plateTable;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<monomerPlate>");
		buff.append("<key>").append(this.getKey()).append("</key>");
		buff.append("<name>").append(this.getName()).append("</name>");
		buff.append("<stepName>").append(this.getStepName()).append("</stepName>");
		buff.append("<type>").append(this.getType()).append("</type>");
//		buff.append("<wells>");
//		for (Iterator it = wells.iterator(); it.hasNext();) {
//			buff.append(((MonomerPlateWell) it.next()).toXml());
//		}
//		buff.append("</wells>");
		buff.append(this.getPlateTable().toXml());
		buff.append("</monomerPlate>");
		return buff.toString();
	}
			
}
