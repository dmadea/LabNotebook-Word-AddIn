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

import java.util.Iterator;
import java.util.Vector;

public class SynthesisPlateTable {

	private String plateKey = "";
	private Vector rows = new Vector();
	private int numCols = 0;
	
	public String getPlateKey() {
		return plateKey;
	}

	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}

	public void addRow(PlateRow row) {
		Vector cols = new Vector();
		cols.add(row.getRowId());
		for (Iterator it = row.getWells().iterator(); it.hasNext();) {
			SynthesisPlateWell well = (SynthesisPlateWell) it.next();
			StringBuffer buff = new StringBuffer();
			buff.append(well.getBatchNumber()).append("\n");
			buff.append(well.getVcNumber()).append("\n");
			buff.append(well.getMolecularWeight()).append("\n\n");
			for (Iterator pit = well.getPrecursors().iterator(); pit.hasNext();) {
				SynthesisPrecursor precursor = (SynthesisPrecursor) pit.next();
				buff.append(precursor.getBatchId()).append("\n");
				buff.append("\t").append(precursor.getMoleAmount()).append(", ");
				buff.append(precursor.getWeightAmount()).append("\n");
			}
			cols.add(buff.toString());
		}
		rows.add(cols);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer();
		buff.append("<SynthesisTable>");
		buff.append("<plateKey>").append(this.getPlateKey()).append("</plateKey>");
		for (Iterator rit = rows.iterator(); rit.hasNext();) {
			Vector row = (Vector) rit.next();
			buff.append("<row>");
			buff.append("<rowid>").append(row.get(0).toString()).append("</rowid>");
			for (int i=0; i<row.size(); i++) {
				buff.append("<col" + i + ">");
				buff.append(row.elementAt(i).toString());
				buff.append("</col" + i + ">");
			}
			buff.append("</row>");
		}
		buff.append("</SynthesisTable>");
		return buff.toString();
	}
}
