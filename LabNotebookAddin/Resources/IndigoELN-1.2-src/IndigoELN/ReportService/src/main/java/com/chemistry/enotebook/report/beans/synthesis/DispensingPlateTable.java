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

public class DispensingPlateTable {

	private String plateKey = "";
	private Vector rows = new Vector();
	private int numCols = 0;
	private String monomerListName = "";
	
	public void dispose() {
		if (rows != null) {
			for (Iterator rit = rows.iterator(); rit.hasNext();) {
				Vector cols = (Vector) rit.next();
				cols.clear(); // this is a vector of strings
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

	public String getMonomerListName() {
		return monomerListName;
	}

	public void setMonomerListName(String monomerListName) {
		this.monomerListName = monomerListName;
	}

	public void addRow(PlateRow row) {
		Vector cols = new Vector();
		cols.add(row.getRowId());
		for (Iterator it = row.getWells().iterator(); it.hasNext();) {
			DispensingPlateWell well = (DispensingPlateWell) it.next();
			StringBuffer buff = new StringBuffer();
			buff.append(well.getCompoundId()).append("\n");
			String totalWeight = well.getTotalWeight();
			if (totalWeight != null && totalWeight.length() > 0)
				buff.append("Total Wgt: ").append(totalWeight).append("\n");
			String totalVolume = well.getTotalVolume();
			if (totalVolume != null && totalVolume.length() > 0)
				buff.append("Total Vol: ").append(totalVolume).append("\n");
			if (well.getDispensingList().size() > 0)
				buff.append("To:").append("\n");
			for (Iterator dit = well.getDispensingList().iterator(); dit.hasNext();) {
				DispensingDirections dispDirections = (DispensingDirections) dit.next();
				buff.append(dispDirections.getPlateName()).append(", ").append(dispDirections.getPlateWell()).append("\n");
				buff.append("\t").append(dispDirections.getMoleAmount()).append(", ");
				if (dispDirections.getWeightAmount().length() > 0)
					buff.append(dispDirections.getWeightAmount());
				if (dispDirections.getVolumeAmount().length() > 0) {
					if (dispDirections.getWeightAmount().length() > 0) {
						buff.append(",").append(dispDirections.getVolumeAmount());
					} else 
						buff.append(dispDirections.getVolumeAmount());
				}

				buff.append(dispDirections.getVolumeAmount()).append(" \n");
			}
			cols.add(buff.toString());
		}
		rows.add(cols);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer();
		buff.append("<DispensingTable>");
		buff.append("<plateKey>").append(this.getPlateKey()).append("</plateKey>");
		buff.append("<monomerListName>").append(this.getMonomerListName()).append("</monomerListName>");
		for (Iterator rit = rows.iterator(); rit.hasNext();) {
			Vector row = (Vector) rit.next();
			buff.append("<row>");
			buff.append("<rowid>").append(row.get(0).toString()).append("</rowid>");
			for (int i=0; i<row.size(); i++) {
				buff.append("<col" + i + ">");
				Object obj = row.elementAt(i);
				if (obj instanceof String)
					buff.append(obj.toString());
				else if (obj instanceof DispensingPlateWell) {
					DispensingPlateWell well = (DispensingPlateWell) obj;
					buff.append(well.toXml());
				}
				//buff.append(row.elementAt(i).toString());
				buff.append("</col" + i + ">");
			}
			buff.append("</row>");
		}
		buff.append("</DispensingTable>");
		return buff.toString();
	}
}
