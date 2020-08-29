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
package com.chemistry.enotebook.formatter;

import java.util.Arrays;

public class ENotebookFormatter implements Formatter {
	
	public static final int MIN_NUMBER_LENGTH = 6;
	
	/**
	 * Adds leading zeros to the number (between letter prefix and number)
	 * to make sure the length of the number is not less than MIN_NUMBER_LENGTH digits.
	 * @param s string to format
	 * @return formatted string
	 */
	public String formatCompoundNumber(String s) {
		return formatCompoundNumber(s, true);
	}

	/**
	 * Adds leading zeros to the number (between letter prefix and number)
	 * to make sure the length of the number is not less than MIN_NUMBER_LENGTH digits.
	 * @param s string to format
	 * @param withSalt if true, leave salt, if false, return result without salt
	 * @return formatted string
	 */
	public String formatCompoundNumber(String s, boolean withSalt) {
		if ((s == null) || (s.length() == 0)) {
			return s;
		}
		
		int length = s.length();
		char[] symbols = new char[length];
		s.getChars(0, s.length(), symbols, 0);
		
		int first = 0;
		while ((symbols[first] < '0') || (symbols[first] > '9')) {
			first++;
			if (first >= length) {
				return s;
			}
		}
		
		int last = first;
		while ((last < length) &&(symbols[last] >= '0') && (symbols[last] <= '9')) {
			last++;
		}
		
		if (last - first >= MIN_NUMBER_LENGTH) {
			return s;
		}
		
		int zerosNumber = MIN_NUMBER_LENGTH - (last - first);
		char[] zeros = new char[zerosNumber];
		Arrays.fill(zeros, '0');
		
		char[] resultArray = new char[length + zerosNumber];
		
		System.arraycopy(symbols, 0, resultArray, 0, first);
		System.arraycopy(zeros, 0, resultArray, first, zerosNumber);
		
		if (withSalt) {
			System.arraycopy(symbols, first, resultArray, first + zerosNumber, length - first);
		} else {
			System.arraycopy(symbols, first, resultArray, first + zerosNumber, last - first);			
		}
		
		return new String(resultArray);
	}

	/**
	 * Returns string without all symbols after first digits group
	 * @param s string to format
	 * @return formatted string 
	 */
	public String removeSaltCode(String s) {
		if ((s == null) || (s.length() == 0)) {
			return s;
		}
		
		int length = s.length();
		char[] symbols = new char[length];
		s.getChars(0, s.length(), symbols, 0);

		boolean digitsGroup = false;
		for (int i = 0; i < length; i++) {
			if ((symbols[i] >= '0') && (symbols[i] <= '9')) {
				digitsGroup = true;
			} else {
				if (digitsGroup) {
					return new String(symbols, 0, i);
				}
			}
		}
		
		return s;
	}
	
	/**
	 * Returns symbols after first digits group in string
	 * @param s string
	 * @return symbols after first digits group in string, first symbol after digits is skipped
	 */
	public String getSaltCode(String s) {
		if ((s == null) || (s.length() == 0)) {
			return s;
		}
		
		int length = s.length();
		char[] symbols = new char[length];
		s.getChars(0, s.length(), symbols, 0);
		
		boolean digitsGroup = false;
		for (int i = 0; i < length - 1; i++) {
			if ((symbols[i] >= '0') && (symbols[i] <= '9')) {
				digitsGroup = true;
			} else {
				if (digitsGroup) {
					return new String(symbols, i + 1, length - i - 1);
				}
			}
		}
		return "";
	}
	
}
