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
package com.chemistry.enotebook.client.integration.compoundmanagementorderingapi;

import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.MonomerPlate;
import com.chemistry.enotebook.domain.PlateWell;

import java.util.ArrayList;
import java.util.List;

public class OrderDeliverable extends CeNAbstractModel {
	
	private static final long serialVersionUID = 420845727358247228L;
	
	private MonomerPlate[] monomerPlates = new MonomerPlate[0];
	private ArrayList<PlateWell<MonomerBatchModel>> monomerTubes = new ArrayList<PlateWell<MonomerBatchModel>>();

	public OrderDeliverable() { }

	public boolean hasPlate() {
		return monomerPlates.length > 0;
	}

	public MonomerPlate[] getMonomerPlates() {
		return monomerPlates;
	}

	public void setMonomerPlates(MonomerPlate[] monomerPlates) {
		this.monomerPlates = monomerPlates;
	}

	public boolean hasTubes() {
		return (monomerTubes != null && monomerTubes.size() > 0);
	}

	public ArrayList<PlateWell<MonomerBatchModel>> getMonomerTubes() {
		return monomerTubes;
	}

	public List<MonomerBatchModel> getMonomerBatches() {
		ArrayList<MonomerBatchModel> list = new ArrayList<MonomerBatchModel>();
		for (PlateWell<MonomerBatchModel> tube : this.monomerTubes) {
			list.add((MonomerBatchModel)tube.getBatch());
		}
		return list;
	}

	public void setMonomerTubes(ArrayList<PlateWell<MonomerBatchModel>> monomerTubes) {
		this.monomerTubes = monomerTubes;
		if (this.monomerTubes == null) this.monomerTubes = new ArrayList<PlateWell<MonomerBatchModel>>();
	}


	/*
	 * public MonomerPlate[] getMonomerRackPlates() { return monomerRackPlates; }
	 * 
	 * public void setMonomerRackPlates(MonomerPlate[] monomerRackPlates) { this.monomerRackPlates = monomerRackPlates; }
	 */
	
	public String toXML() {
		return "";
	}
}
