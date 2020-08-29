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
package com.chemistry.enotebook.experiment.datamodel.transaction;

import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;

/**
 * 
 * 
 *
 */
public class TransactionStep extends PersistableObject implements Comparable, DeepCopy, DeepClone {
	
	private static final long serialVersionUID = 5279192018340201825L;
	
	private static final int HASH_PRIME = 11617;
	private TransactionStepType tst = null;
	private int step;
	private String stepKey;

	// Constructor
	public TransactionStep(TransactionStepType tst) {
		this.tst = tst;
		this.stepKey = GUIDUtil.generateGUID(this);
	}

	public void dispose() {
		tst = null;
	}

	public TransactionStepType getType() {
		return tst;
	}

	public void setType(TransactionStepType tst) {
		if (!this.tst.equals(tst)) {
			this.tst = tst;
			setModified(true);
		}
	}

	public int getStepNumber() {
		return step;
	}

	public void setStepNumber(int nextStep) {
		if (this.step != nextStep) {
			this.step = nextStep;
			setModified(true);
		}
	}

	// public String getStepKey() { return stepKey; }
	// public void setStepKey(String newStepKey) // setStepKey should only be used on load
	// {
	// this.stepKey = newStepKey;
	// }

	public int compareTo(Object o) {
		int result = 0;
		if (o instanceof TransactionStep) {
			result = (this.getStepNumber() - ((TransactionStep) o).getStepNumber());
		}
		return result;
	}

	public int hashCode() {
		return HASH_PRIME + this.step + (this.tst.hashCode() * HASH_PRIME);
	}

	public boolean equals(Object o) {
		boolean result = false;

		return result;
	}

	// 
	// DeepCopy/DeepClone
	//
	public void deepCopy(Object resource) {
		if (resource instanceof TransactionStep) {
			TransactionStep srcStep = (TransactionStep) resource;
			tst = srcStep.tst;
			step = srcStep.step;
		}
	}

	public Object deepClone() {
		TransactionStep txnStep = new TransactionStep(TransactionStepType.ADD);
		txnStep.deepCopy(this);
		return txnStep;
	}
}
