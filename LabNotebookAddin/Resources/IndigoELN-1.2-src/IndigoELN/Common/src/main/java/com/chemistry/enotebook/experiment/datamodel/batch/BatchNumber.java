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
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 *
 */
public class BatchNumber extends CeNAbstractModel implements Comparable, DeepClone, DeepCopy {
	public static final long serialVersionUID = 7526472295622776147L;
	// Need to override equals and hashCode to get these to be
	// consistent
	private static final int HASH_PRIME = 100363;

	// Class used to create disect and validaste BatchNumbers
	private NotebookRef nbRef; // Used to store and validate the notebook reference part of the batch number
	private String lotNumber; // Length must currently not exceed 6 characters

	public BatchNumber() {
		nbRef = new NotebookRef();
		lotNumber = "";
	}

	public BatchNumber(String newNumber) throws InvalidBatchNumberException {
		setBatchNumber(newNumber);
	}

	public NotebookRef getNbRef() {
		return nbRef;
	}

	public void setNbRef(NotebookRef newRef) {
		nbRef = newRef;
	}

	public void setNbRef(String newRef) throws InvalidBatchNumberException {
		try {
			nbRef = new NotebookRef(newRef);
		} catch (InvalidNotebookRefException e) {
			throw new InvalidBatchNumberException("Need to set a valid notebook reference.", e);
		}
	}

	public String getBatchNumber() {
		if (StringUtils.isBlank(lotNumber) || nbRef == null || StringUtils.isBlank(nbRef.toString()))
			return "";
		else
			return nbRef.toString() + "-" + lotNumber;
	}

	//This will be used in Stoic grid when user want to search on NB batch# and it shoudlnot be padded
	//as search should be doen with exact match to what user typed in.
	public void setBatchNumberWithoutLotNumberPadding(String newNumber) throws InvalidBatchNumberException {
		if (newNumber != null) {
			if (StringUtils.isNotBlank(newNumber) && NotebookPageUtil.isValidCeNBatchNumber(newNumber)) {
				//This new padded # generated is not used in 1.1.So commenting out here as well.
				//String newBatchNumberPadded = NotebookPageUtil.formatBatchNumber(newNumber);
				try {
					nbRef = new NotebookRef(NotebookPageUtil.getNotebookRefFromBatchNumber(newNumber));
				} catch (InvalidNotebookRefException e) {
					throw new InvalidBatchNumberException("Notebook reference in the entered batch number is invalid: '"
							+ newNumber + "'", e);
				}
				try {
					this.setLotNumber(NotebookPageUtil.getLotNumberFromBatchNumber(newNumber));
				} catch (RuntimeException e) {
					throw new InvalidBatchNumberException("Lot number entered is invalid: '"
							+ NotebookPageUtil.getLotNumberFromBatchNumber(newNumber) + "'", e);
				}
			} else if (newNumber.equals("")) {
				this.lotNumber = "";
			} else {
				throw new InvalidBatchNumberException("BatchNumber entered is invalid: '" + newNumber + "'");
			}
		}
	}
	
	// vb 6/12 We need the lot number to be padded
	// vb 6/24 We don't want the lot number to be padded
	public void setBatchNumber(String newNumber) throws InvalidBatchNumberException {
		if (newNumber != null) {
			if (!newNumber.equals("") && NotebookPageUtil.isValidCeNBatchNumber(newNumber)) {
				//String newBatchNumberPadded = NotebookPageUtil.formatBatchNumber(newNumber);
				try {
					nbRef = new NotebookRef(NotebookPageUtil.getNotebookRefFromBatchNumber(newNumber));
				} catch (InvalidNotebookRefException e) {
					throw new InvalidBatchNumberException("Notebook reference in the entered batch number is invalid: '"
							+ newNumber + "'", e);
				}
				try {
					this.setLotNumber(NotebookPageUtil.getLotNumberFromBatchNumber(newNumber));
				} catch (RuntimeException e) {
					throw new InvalidBatchNumberException("Lot number entered is invalid: '"
							+ NotebookPageUtil.getLotNumberFromBatchNumber(newNumber) + "'", e);
				}
			} else if (newNumber.equals("")) {
				this.lotNumber = "";
			} else {
				throw new InvalidBatchNumberException("BatchNumber entered is invalid: '" + newNumber + "'");
			}
		}
	}

	public String getLotNumber() {
		if (lotNumber != null && !lotNumber.equals(""))
			return lotNumber;
		else
			return "";
	}

	public void setLotNumber(String newNumber) throws InvalidBatchNumberException {
		if (newNumber.length() <= NotebookPageUtil.NB_BATCH_LOT_MAX_LENGTH)
			lotNumber = newNumber;
		else
			throw new InvalidBatchNumberException(newNumber + " is too long for a lot number.  Should be a maximum of "
					+ NotebookPageUtil.NB_BATCH_LOT_MAX_LENGTH + " characters.");
	}

	//
	// Overrides
	//
	public String toString() {
		return getBatchNumber();
	}

	public boolean equals(Object rhs) {
		boolean result = false;
		if (rhs.getClass() == this.getClass()) {
			result = (rhs.toString().equals(this.toString()));
		}
		return result;
	}

	public int hashCode() {
		return (HASH_PRIME * (nbRef.hashCode() + "-".hashCode() + lotNumber.hashCode()));
	}

	public int compareTo(Object o) {
		int result = 0;
		if (o instanceof BatchNumber) {
			BatchNumber bn = (BatchNumber) o;
			result = toString().compareToIgnoreCase(bn.toString());
			if (result == 0) // if equal then re-compare w/case
				result = toString().compareTo(bn.toString());
		}
		return result;
	}

	public void deepCopy(Object resource) {
		if (resource instanceof BatchNumber) {
			BatchNumber src = (BatchNumber) resource;
			lotNumber = src.lotNumber;
			if (src.nbRef != null)
				nbRef.deepCopy(src.nbRef);
		}
	}

	public Object deepClone() {
		BatchNumber bn = new BatchNumber();
		bn.deepCopy(this);
		return bn;
	}

	public String toXML() {
		return "";
	}

}
