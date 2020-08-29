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

public class MonomerPlateTable {

	private String plateKey = "";
	private List<ArrayList<MonomerPlateWell>> rows = new ArrayList<ArrayList<MonomerPlateWell>>();
	
	public void dispose() {
		if (rows != null) {
			for (ArrayList<MonomerPlateWell> row : rows) {
				row.clear();
			}
			rows.clear();
		}
	}
	
	public String getPlateKey() {
		return plateKey;
	}

	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}

	public void addRow(MonomerPlateRow row) {
		ArrayList<MonomerPlateWell> cols = new ArrayList<MonomerPlateWell>();
		//cols.add(row.getRowId());
		for (MonomerPlateWell well : row.getWells()) {
//			StringBuffer buff = new StringBuffer();
//			buff.append(well.getCompoundId()).append("\n");
//			buff.append(well.getMMoles()).append("\n");
//			buff.append(well.getMolecularWeight()).append("\n");
//			cols.add(buff.toString());
			cols.add(well);
		}
		rows.add(cols);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer();
		buff.append("<MonomerPlateTable>");
		buff.append("<plateKey>").append(this.getPlateKey()).append("</plateKey>");
		int count = 1;
		for (ArrayList<MonomerPlateWell> row : rows) {
			buff.append("<row>");
			buff.append("<rowid>");
			buff.append("" + count++);
			buff.append("</rowid>");
			//buff.append("<rowid>").append(row.get(0).toString()).append("</rowid>");
			for (int i=0; i<row.size(); i++) {
				buff.append("<col" + i + ">");
				MonomerPlateWell well = row.get(i);
				buff.append(well.toXml());
				buff.append("</col" + i + ">");
			}
			buff.append("</row>");
		}
		buff.append("</MonomerPlateTable>");
		return buff.toString();
	}
}
