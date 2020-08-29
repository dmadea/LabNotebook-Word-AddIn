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
package com.virtuan.plateVisualizer;

/**
 * Title: NumberSystemMapper Description: This class handles alpha numeric well position label conversions.
 */
public class NumberSystemMapper {
	private final char[] charList;

	public NumberSystemMapper() {
		charList = new char[26];
		for (int index = 'A'; index <= 'Z'; index++) {
			charList[index - 'A'] = (char) index;
		}
	}

	public NumberSystemMapper(char[] charSet) {
		this.charList = charSet;
	}

	public String mapIntegerToSystem(int value) {
		String result = "";
		int tempValue = value;
		for (int index = 0; tempValue > 0; index++) {
			int offset = (tempValue - 1) % charList.length;
			tempValue = (tempValue - 1) / charList.length;
			result = Character.toString(charList[offset]) + result;
		}
		return result;
	}

	public int mapSystemToInteger(String strValue) {
		int result = 0;
		for (int index = 0; index < strValue.length(); index++) {
			result = result * charList.length + charIndex(strValue.charAt(index)) + 1;
		}
		return result;
	}

	private int charIndex(char testChar) {
		int index = 0;
		for (; index < charList.length; index++) {
			if (charList[index] == testChar) {
				break;
			}
			;
		}
		return index;
	}

	public static void main(String[] args) {
		// System.out.println("System with only digits 1 and 2");
		char[] testChars = new char[2];
		testChars[0] = '1';
		testChars[1] = '2';
		NumberSystemMapper mapper = new NumberSystemMapper(testChars);
		int intValue = 2;
		String strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 4;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 6;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 14;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		// System.out.println("System with only digits 1 - 9");
		testChars = new char[9];
		testChars[0] = '1';
		testChars[1] = '2';
		testChars[2] = '3';
		testChars[3] = '4';
		testChars[4] = '5';
		testChars[5] = '6';
		testChars[6] = '7';
		testChars[7] = '8';
		testChars[8] = '9';
		mapper = new NumberSystemMapper(testChars);
		intValue = 9;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 25;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 90;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 819;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		// System.out.println("System with digits A - Z");
		mapper = new NumberSystemMapper();
		intValue = 26;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 100;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 702;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		intValue = 18278;
		strValue = mapper.mapIntegerToSystem(intValue);
		// System.out.println(intValue + " -> " + strValue);
		intValue = mapper.mapSystemToInteger(strValue);
		// System.out.println(strValue + " -> " + intValue);
		for (int index = 0; index < 500; index++) {
			strValue = mapper.mapIntegerToSystem(index);
			// System.out.println(intValue + " -> " + strValue);
		}
	}
}
