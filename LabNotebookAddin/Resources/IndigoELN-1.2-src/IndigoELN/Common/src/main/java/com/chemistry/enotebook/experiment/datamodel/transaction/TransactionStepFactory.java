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

/**
 * 
 * 
 *
 */
public class TransactionStepFactory {
	private TransactionStepFactory() {
	}

	public static TransactionStep getTransactionStep(String type) {
		TransactionStepType tst = null;
		if (TransactionStepType.ADD.toString().equalsIgnoreCase(type))
			tst = TransactionStepType.ADD;
		// if (TransactionStepType.REMOVE.toString().equalsIgnoreCase(type)) tst = TransactionStepType.REMOVE;
		// if (TransactionStepType.MOVE.toString().equalsIgnoreCase(type)) tst = TransactionStepType.MOVE;
		// if (TransactionStepType.MIX.toString().equalsIgnoreCase(type)) tst = TransactionStepType.MIX;
		// if (TransactionStepType.HEAT.toString().equalsIgnoreCase(type)) tst = TransactionStepType.HEAT;
		// if (TransactionStepType.COOL.toString().equalsIgnoreCase(type)) tst = TransactionStepType.COOL;
		return new TransactionStep(tst);
	}

	public static TransactionStep getTransactionStep(TransactionStepType type) {
		TransactionStep tst = null;
		return getTransactionStep(type.toString());
	}
}
