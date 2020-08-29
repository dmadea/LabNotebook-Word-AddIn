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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SynthesisPlate {
	
	private String key = "";
	private String step = "";
	private String name = "";
	private String barcode = "";
	//private List wells = new ArrayList();
	private List rows = new ArrayList();
	private SynthesisPlateTable plateTable = new SynthesisPlateTable();
	
	public void dispose() {
		if (rows != null) {
			for (Iterator rit = rows.iterator(); rit.hasNext();) {
				PlateRow row = (PlateRow) rit.next();
				row.dispose();
			}
			rows.clear();
		}
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
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
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
//	public void addWell(SynthesisPlateWell well) {
//		this.wells.add(well);
//	}
	public void addRow(PlateRow row) {
		rows.add(row);
	}
	public SynthesisPlateTable getPlateTable() {
		return plateTable;
	}
	public void setPlateTable(SynthesisPlateTable plateTable) {
		this.plateTable = plateTable;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<synthesisPlate>");
		buff.append("<key>").append(this.getKey()).append("</key>");
		buff.append("<step>").append(this.getStep()).append("</step>");
		buff.append("<name>").append(this.getName()).append("</name>");
		buff.append("<barcode>").append(this.getBarcode()).append("</barcode>");
		buff.append(this.getPlateTable().toXml());
		buff.append("</synthesisPlate>");
		return buff.toString();
	}	
}
