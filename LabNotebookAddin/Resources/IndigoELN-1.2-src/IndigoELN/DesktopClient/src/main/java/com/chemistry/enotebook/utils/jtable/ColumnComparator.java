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
/**
 * 
 */
package com.chemistry.enotebook.utils.jtable;

import com.chemistry.enotebook.experiment.common.MolString;

import java.util.Comparator;

public class ColumnComparator implements Comparator {
	protected int index;
	protected boolean ascending;

	public ColumnComparator(int index, boolean ascending) {
		this.index = index;
		this.ascending = ascending;
	}

	public int compare(Object one, Object two) {
		if (one instanceof SortableVO && two instanceof SortableVO) {
			SortableVO vOne = (SortableVO) one;
			SortableVO vTwo = (SortableVO) two;
			Object oOne = vOne.getValueAt(index);
			Object oTwo = vTwo.getValueAt(index);
			// Treat empty strings like nulls
			if (oOne instanceof String && ((String) oOne).length() == 0)
				oOne = null;
			if (oTwo instanceof String && ((String) oTwo).length() == 0)
				oTwo = null;
			// Sort nulls so they appear last, regardless of sort order
			if (oOne == null && oTwo == null)
				return 0;
			else if (oOne == null)
				return 1;
			else if (oTwo == null)
				return -1;
			else if (oOne instanceof String && oTwo instanceof String) { // Try
				// Strings
				// First
				// (Ignore
				// Case)
				String valueOne = (String) oOne;
				String valueTwo = (String) oTwo;
				if (ascending) {
					if (isDouble(valueOne) && isDouble(valueTwo)) { // value
						// is
						// double
						return compareDoubles(valueOne, valueTwo, ascending);
					} else if (isInteger(valueOne) && isInteger(valueTwo)) { // value
						// is
						// integer
						return compareIntegers(valueOne, valueTwo, ascending);
					} else { // return string if it contains characters
						return (valueOne).compareToIgnoreCase(valueTwo);
					}
				} else {
					if (isDouble(valueOne) && isDouble(valueTwo)) { // value
						// is
						// double
						return compareDoubles(valueOne, valueTwo, ascending);
					} else if (isInteger(valueOne) && isInteger(valueTwo)) { // value
						// is
						// integer
						return compareIntegers(valueOne, valueTwo, ascending);
					} else { // return string if it contains characters
						return (valueTwo).compareToIgnoreCase(valueOne);
					}
				}
			} else if (oOne instanceof MolString && oTwo instanceof MolString) {
				return compareMolStrings(oOne, oTwo, ascending);
			} else if (oOne instanceof Comparable && oTwo instanceof Comparable) { // Now
				// Check
				// for
				// Comparable
				Comparable cOne = (Comparable) oOne;
				Comparable cTwo = (Comparable) oTwo;
				if (ascending) {
					return cOne.compareTo(cTwo);
				} else {
					return cTwo.compareTo(cOne);
				}
			} else { // Try String again
				if (ascending) {
					return oOne.toString().compareTo(oTwo.toString());
				} else {
					return oTwo.toString().compareTo(oOne.toString());
				}
			}
		}
		return 1;
	}

	private boolean isDouble(String value) {
		try {
			double a = Double.parseDouble(value);
			return true;
		} catch (NumberFormatException nEx) {
			return false;
		}
	}

	private boolean isInteger(String value) {
		try {
			int a = Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nEx) {
			return false;
		}
	}

	private int compareDoubles(String value1, String value2, boolean ascMode) {
		Double doubleValue1 = new Double(value1);
		Double doubleValue2 = new Double(value2);
		if (ascMode)
			return doubleValue1.compareTo(doubleValue2);
		else
			return doubleValue2.compareTo(doubleValue1);
	}

	private int compareIntegers(String value1, String value2, boolean ascMode) {
		Integer intValue1 = new Integer(value1);
		Integer intValue2 = new Integer(value2);
		if (ascMode)
			return intValue1.compareTo(intValue2);
		else
			return intValue2.compareTo(intValue1);
	}

	private int compareMolStrings(Object value1, Object value2, boolean ascMode) {
		MolString mol1 = (MolString) value1;
		MolString mol2 = (MolString) value2;
		if (ascMode)
			return mol1.getIndex() - mol2.getIndex();
		else
			return mol2.getIndex() - mol1.getIndex();
	}
}
