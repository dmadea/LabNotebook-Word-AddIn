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

import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

import java.math.BigDecimal;

/**
 * 
 * 
 *
 * 
 * Transaction Step Add holds batch additions to anything. For now it only refers to batch additions to stoichiometry and no
 * container information exists.
 * 
 * Transaction Steps can be referenced by Reaction Steps, but they don't necessarily need to be part of a reaction. An example would
 * be the creation of a container and the addition of batch material to that container.
 * 
 * If the amount added is null then the batch was simply added to the scheme and not in any quantity. amtBeforeAdd indicates any
 * quantities of the batch prior to any addition. You can add a batch with amounts and all as a step, but the amount added will not
 * be recorded. If the amount to add is to be recorded so that it may be rolled back, simply add the batch as is and the amount with
 * the proper add_type. The batch will have the information added to it while recording it's previous value incase the user wants to
 * roll back that step.
 */
public class AdditionTransactionStep extends TransactionStep implements ITransactionStep {

	private static final long serialVersionUID = -5043551621779629343L;
	
	public static final int ADD_BATCH = 0;
	public static final int ADD_BY_MOLES = 1;
	public static final int ADD_BY_WEIGHT = 2;
	public static final int ADD_BY_VOLUME = 3;
	public static final int ADD_BY_EQUIVS = 4;
	public static final int ADD_BY_MOLARITY = 5;

	// private Container sourceContainer;
	private AbstractBatch source;
	private Amount amtBeforeAdd;
	private Amount amtAdded;
	private int add_type = -1; // Invalid type = nothing gets done.

	// 
	// Constructors
	//
	private AdditionTransactionStep() {
		super(TransactionStepType.ADD);
	}

	public AdditionTransactionStep(AbstractBatch source) {
		this(source, null, ADD_BATCH);
	}

	public AdditionTransactionStep(AbstractBatch source, Amount amtToAdd) {
		this(source, amtToAdd, ADD_BATCH);
	}

	public AdditionTransactionStep(AbstractBatch source, Amount amtToAdd, int add_type) {
		this();
		setLoading(true);
		addAmount(source, amtToAdd, add_type);
		setLoading(false);
	}

	// 
	// Accessors
	//
	public AbstractBatch getSource() {
		return source;
	}

	public Amount getAmountAdded() {
		return this.amtAdded;
	}

	public int getAdditionType() {
		return add_type;
	}

	//
	// Methods
	//
	public void addAmount(AbstractBatch source, Amount amtToAdd, int add_type) {
		this.source = source;
		addAmount(amtAdded, add_type);
	}

	public void addAmount(Amount amtToAdd, int add_type) {
		this.amtAdded = amtToAdd;
		this.add_type = add_type;
		setBeforeAddedAmount();
		performAddition();
		setModified(true);
	}

	private void setBeforeAddedAmount() {
		amtBeforeAdd = getRelaventAmountFromSource();
	}

	private void performAddition() {
		Amount targetAmt = getRelaventAmountFromSource();
		if (targetAmt != null)
			addAmounts(targetAmt);
	}

	private Amount getRelaventAmountFromSource() {
		Amount result = null;
		if (source != null) {
			switch (add_type) {
				case ADD_BY_MOLES:
					result = source.getMoleAmount();
					break;
				case ADD_BY_WEIGHT:
					result = source.getWeightAmount();
					break;
				case ADD_BY_VOLUME:
					result = source.getVolumeAmount();
					break;
				case ADD_BY_EQUIVS:
					result = source.getRxnEquivsAmount();
					break;
				case ADD_BY_MOLARITY:
					result = source.getMolarAmount();
					break;
				default:
					break;
			}
		}
		return result;
	}

	private void addAmounts(Amount target) {
		// Ensure target and amtAdded have compatible units.
		if (amtAdded != null && target.getUnitType().equals(amtAdded.getUnitType())) {
			BigDecimal bd = new BigDecimal(target.GetValueInStdUnits());
			BigDecimal tmp = new BigDecimal(amtAdded.GetValueInStdUnits());
			target.SetValueInStdUnits(tmp.add(bd).doubleValue());
		}
	}
}
