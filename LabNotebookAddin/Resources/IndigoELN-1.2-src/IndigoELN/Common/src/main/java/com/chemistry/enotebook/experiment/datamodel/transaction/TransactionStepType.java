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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 
 *
 * 
 * From Josh Bloch's implementation of a type-safe Enum class.
 * 
 * The idea here is to classify reactions by type and to allow them to be comparable so they can be sorted on type.
 * 
 * Warning: because there is an ordinal variable here the compareTo function can differ depending on which version of the enum was
 * called first.
 */
public class TransactionStepType implements Serializable {
	static final long serialVersionUID = -346646491236117500L;

	private static final int HASH_PRIME = 18701;

	public static final TransactionStepType ADD = new TransactionStepType("ADD");
	public static final TransactionStepType REMOVE = new TransactionStepType("REMOVE");
	public static final TransactionStepType MOVE = new TransactionStepType("MOVE");
	public static final TransactionStepType MIX = new TransactionStepType("MIX");
	public static final TransactionStepType HEAT = new TransactionStepType("HEAT");
	public static final TransactionStepType COOL = new TransactionStepType("COOL");
	public static final TransactionStepType WAIT = new TransactionStepType("WAIT");
	public static final TransactionStepType PURIFY = new TransactionStepType("PURIFY");

	// Currently listed alphabetically
	private static final TransactionStepType[] PRIVATE_VALUES = { ADD, REMOVE, MOVE, MIX, HEAT, COOL, WAIT };

	private final String type;

	// Private Constructor
	protected TransactionStepType(String type) {
		this.type = type;
	}

	// Ordinal of next suit to be created
	private static int nextOrdinal = 0;
	// Assign an ordinal to this suit
	private final int ordinal = nextOrdinal++;

	// Using the ordinal ranking we can adjust which transaction type is
	// most to least important to us.
	public int compareTo(Object o) {
		return ordinal - ((TransactionStepType) o).ordinal;
	}

	public String toString() {
		return type;
	}

	// Override-prevention methods
	public final boolean equals(Object that) {
		return super.equals(that);
	}

	public final int hashCode() {
		return type.hashCode() * HASH_PRIME;
	}

	/**
	 * Calling this will initialize the values in this order if the type hasn't been called previously
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

	Object readResolve() throws ObjectStreamException {
		return PRIVATE_VALUES[ordinal];
	} // Canonicalize

}
