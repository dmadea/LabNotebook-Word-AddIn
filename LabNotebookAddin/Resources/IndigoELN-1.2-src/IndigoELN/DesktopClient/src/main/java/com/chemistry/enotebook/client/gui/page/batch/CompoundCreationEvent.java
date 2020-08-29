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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ReactionStepModel;

import java.util.EventObject;

public class CompoundCreationEvent extends EventObject {

	private static final long serialVersionUID = -3194549375350698172L;
	
	// ProductPlate plate;
	private BatchModel batch;
	//int stepIndex;
	private ReactionStepModel rxnStep;
	private Object selectedViewObject = null; //Used in Plate Summary table to select the Non-plated batches. 
	
	public CompoundCreationEvent(Object source, BatchModel batch, ReactionStepModel step, Object selectedViewObject) {
		super(source);
		this.batch = batch;
		rxnStep = step;
		this.selectedViewObject = selectedViewObject;
	}

	public CompoundCreationEvent(Object source, BatchModel batch, ReactionStepModel step) {
		super(source);
		this.batch = batch;
		rxnStep = step;
	}
	
	/**
	 * @return the plate
	 */
	public BatchModel getBatch() {
		return batch;
	}

	/**
	 * @param plate
	 *            the plate to set
	 */
	public void setBatch(BatchModel batch) {
		this.batch = batch;
	}
	public ReactionStepModel ReactionStepModel() {
		return rxnStep;
	}
	/**
	 * @return the String(Object) of selected view.
	 */
	public String getSelectedViewItem() {
		if (selectedViewObject != null) {
			return selectedViewObject.toString();
		} else {
			return null;
		}
	}
}
