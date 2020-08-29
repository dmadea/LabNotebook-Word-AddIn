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
import java.util.List;

public class DispensingPlateWell extends PlateWell {

	private String compoundId = "";
	private List<DispensingDirections> dispensingList = new ArrayList<DispensingDirections>();
	private String rowId = "";
	private String totalWeight = "";
	private String totalVolume = "";

	public void addDispensingDirections(DispensingDirections directions) {
		this.dispensingList.add(directions);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<dispensingPlateWell>");
		buff.append("<compoundId>").append(this.getCompoundId()).append("</compoundId>");
		for (DispensingDirections d : dispensingList) {
			buff.append(d.toXml());
		}
		buff.append("</dispensingPlateWell>");
		return buff.toString();
	}
	
	public void dispose() {
		if (dispensingList != null) {
			for (DispensingDirections d : dispensingList) {
				d.dispose();
			}
		}
	}
	
	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

	public List<DispensingDirections> getDispensingList() {
		return dispensingList;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(String totalWeight) {
		this.totalWeight = totalWeight;
	}

	public String getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(String totalVolume) {
		this.totalVolume = totalVolume;
	}
	
	
}
