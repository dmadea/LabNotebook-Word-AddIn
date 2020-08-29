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
package com.chemistry.enotebook.experiment.common.units;

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
public class UnitType implements Serializable, Comparable<UnitType> {
	static final long serialVersionUID = 2577491617825018196L;

	private static final int HASH_PRIME = 10093;

	public static final int SCALAR_ORDINAL = 0;
	public static final int MASS_ORDINAL = 1;
	public static final int MOLES_ORDINAL = 2;
	public static final int VOLUME_ORDINAL = 3;
	public static final int MOLAR_ORDINAL = 4;
	public static final int DENSITY_ORDINAL = 5;
	public static final int LOADING_ORDINAL = 6;
	public static final int TIME_ORDINAL = 7;
	public static final int TEMP_ORDINAL = 8;
	public static final int MASS_PERCENT_ORDINAL = 9;
	public static final int WEIGHT_PER_VOLUME_ORDINAL = 10;
	public static final int AREA_ORDINAL = 11;

	public static final UnitType SCALAR = new UnitType("SCALAR", SCALAR_ORDINAL);
	public static final UnitType MASS = new UnitType("MASS", MASS_ORDINAL);
	public static final UnitType MOLES = new UnitType("MOLES", MOLES_ORDINAL);
	public static final UnitType LOADING = new UnitType("LOADING", LOADING_ORDINAL);
	public static final UnitType VOLUME = new UnitType("VOLUME", VOLUME_ORDINAL);
	public static final UnitType MOLAR = new UnitType("MOLAR", MOLAR_ORDINAL);
	public static final UnitType DENSITY = new UnitType("DENSITY", DENSITY_ORDINAL);
	public static final UnitType TIME = new UnitType("TIME", TIME_ORDINAL);
	public static final UnitType TEMP = new UnitType("TEMPERATURE", TEMP_ORDINAL);
	public static final UnitType MASS_PERCENT = new UnitType("MASS_PERCENT", MASS_PERCENT_ORDINAL);
	public static final UnitType WEIGHT_PER_VOLUME = new UnitType("WEIGHT_PER_VOLUME", WEIGHT_PER_VOLUME_ORDINAL);
	public static final UnitType AREA = new UnitType("AREA", AREA_ORDINAL);

	// Currently listed alphabetically
	private static final UnitType[] PRIVATE_VALUES = { SCALAR, MASS, MOLES, LOADING, VOLUME, MOLAR, DENSITY, TIME, TEMP,
			MASS_PERCENT, WEIGHT_PER_VOLUME, AREA };

	private final String type;

	// Private Constructor
	protected UnitType(String type, int ordinal) {
		this.type = type;
		this.ordinal = ordinal;
	}

	// Assign an ordinal to this suit
	private final int ordinal;

	// Using the ordinal ranking we can adjust which reaction type is
	// most to least important to us.
	public int compareTo(UnitType o) {
		return ordinal - o.ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	} // Can be used in switch statements

	public String toString() {
		return type;
	}

	// Override-prevention methods  
	// changed from reference compare vb 11/15
	public final boolean equals(Object that) {
		if (ordinal == ((UnitType)that).ordinal)
			return true;
		return false;
	}

	public final int hashCode() {
		return type.hashCode() * HASH_PRIME;
	}

	/**
	 * Calling this will initialize the values in this order if the type hasn't been called previously
	 */
	public static final List<UnitType> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

	//** this impl has bug. Returns wrong untitype after serialization as the array is defined in alphabetical order
	//but not in the order of Ordinals.
	//Object readResolve() throws ObjectStreamException {
	//	return PRIVATE_VALUES[ordinal];
	//} // Canonicalize
}
