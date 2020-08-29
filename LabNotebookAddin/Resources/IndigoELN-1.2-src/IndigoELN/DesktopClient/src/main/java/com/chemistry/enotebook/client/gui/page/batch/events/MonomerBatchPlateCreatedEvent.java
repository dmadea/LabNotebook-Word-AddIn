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
package com.chemistry.enotebook.client.gui.page.batch.events;

import com.chemistry.enotebook.domain.MonomerPlate;
import com.chemistry.enotebook.domain.ReactionStepModel;

import java.util.EventObject;
import java.util.List;

public class MonomerBatchPlateCreatedEvent extends EventObject {
	
	private static final long serialVersionUID = 7776663890106542480L;
	
	// ProductPlate plate;
	List<MonomerPlate> plates;
	
	// int stepIndex;
	private ReactionStepModel rxnStep;

	public MonomerBatchPlateCreatedEvent(Object source, List<MonomerPlate> plate, ReactionStepModel step) {
		super(source);
		plates = plate;
		rxnStep = step;
	}

	/**
	 * @return the plate
	 */
	public List<MonomerPlate> getPlates() {
		return plates;
	}

	/**
	 * @param plate the plate to set
	 */
	public void setPlates(List<MonomerPlate> plate) {
		this.plates = plate;
	}

	public ReactionStepModel ReactionStepModel() {
		return rxnStep;
	}
}

