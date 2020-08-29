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
package com.chemistry.enotebook.experiment.datamodel.reaction;

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
 * 
 * Intended is for the page (Over arching reaction) Actual and Generics are being ignored. Legacy combi-chem info Step is used to
 * identify the steps in Intended Reaction.
 */
public class ReactionType implements Serializable {
	static final long serialVersionUID = -729664046877461066L;

	private static final int HASH_PRIME = 18749;
	public static final ReactionType INTENDED = new ReactionType("INTENDED");
	public static final ReactionType STEP = new ReactionType("STEP");
	public static final ReactionType CONCEPTION = new ReactionType("CONCEPTION");
	public static final ReactionType ACTUAL = new ReactionType("ACTUAL");
	public static final ReactionType GENERIC = new ReactionType("GENERIC");

	// Currently listed alphabetically
	private static final ReactionType[] PRIVATE_VALUES = { INTENDED, STEP, CONCEPTION, ACTUAL, GENERIC };

	private final String type;

	// Private Constructor
	protected ReactionType(String type) {
		this.type = type;
	}

	// Ordinal of next suit to be created
	private static int nextOrdinal = 0;
	// Assign an ordinal to this suit
	private final int ordinal = nextOrdinal++;

	// Using the ordinal ranking we can adjust which reaction type is
	// most to least important to us.
	public int compareTo(Object o) {
		return ordinal - ((ReactionType) o).ordinal;
	}

	public String toString() {
		return type;
	}

	// Override-prevention methods
	public final boolean equals(Object that) {
		return (this.compareTo(that) == 0);
	}

	public final int hashCode() {
		return (HASH_PRIME + (this.ordinal * HASH_PRIME));
	}

	/**
	 * Calling this will initialize the values in this order if the type hasn't been called previously
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

	Object readResolve() throws ObjectStreamException {
		return PRIVATE_VALUES[ordinal];
	} // Canonicalize
}
