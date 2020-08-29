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

import java.awt.geom.Rectangle2D;

/**
 * Title: PlateGeneralMethods Description: This class contains general methods for use by the plateVisualizerGUI
 */
public class PlateGeneralMethods {
	private static int CHARACTER_A_NUMERIC_VALUE = 65;
	public static String X_TEXT = "x";
	public static String Y_TEXT = "y";

	/**
	 * method to return the well diameter
	 * 
	 * @param dataArea
	 *            Rectangle 2D of the plate area
	 * @param numberOfWellsOnLargeSide
	 * @param scalingFactor
	 *            generally 0 if number of wells on large side <= 40 generally 2 if number of wells on large side > 40 if wells are
	 *            running together, increase the scaling factor (by increments of 1)
	 * @return double value of the well diameter in 2D user space
	 */
	public static double getGeneralWellDiameter(Rectangle2D dataArea, int numberOfWellsOnLargeSide, int scalingFactor) {
		// //System.out.println("numberOfWellsOnLargeSide:
		// "+numberOfWellsOnLargeSide);
		// //System.out.println("scalingFactor: "+scalingFactor);
		int hd;
		int wd;
		int diameter;
		int height = dataArea.getBounds().height;
		int width = dataArea.getBounds().width;
		if (height > width) {
			hd = height;
			wd = width;
		} else {
			hd = width;
			wd = height;
		}
		/*
		 * if(scalingFactor <= 0) diameter = wd/(numberOfWellsOnLargeSide); else diameter = wd/(numberOfWellsOnLargeSide +
		 * (numberOfWellsOnLargeSide/scalingFactor));
		 */
		int gap = 30;
		if (numberOfWellsOnLargeSide != 1)
			gap = wd / (numberOfWellsOnLargeSide - 1);
		if (gap > 30)
			diameter = 30;
		else
			diameter = gap;
		// //System.out.println("diameter"+diameter);
		// //System.out.println("Double(diameter).doubleValue()"+new
		// Double(diameter).doubleValue());
		return new Double(diameter).doubleValue();
	}

	/**
	 * method for converting a alphabetic string string into a numeric value. i.e input 1, return A input 26, return Z input 27,
	 * return AA Will not work for input larger that 2 characters
	 * 
	 * @param numericString
	 *            String value of the letter string to be converted
	 * @return int value of the well position for the passed index
	 */
	public static String convertNumericStringToALetter(String numericString) {
		NumberSystemMapper nsm = new NumberSystemMapper();
		return nsm.mapIntegerToSystem(Integer.parseInt(numericString));
		/**
		 * int number; int currentChar; String returnString; char c ='{'; char c1 = '}'; char[] cArray;
		 * 
		 * number = Integer.parseInt(numericString) - 1;
		 * 
		 * int capA = CHARACTER_A_NUMERIC_VALUE;
		 * 
		 * int lastLetter = capA + (number % 26); c = (char) lastLetter; int firstLetters = number / 26;
		 * 
		 * if (firstLetters > 0) { int firstLetter = capA - 1 + (firstLetters%26); c1 = (char)firstLetter; }
		 * 
		 * if (c1 != '}') cArray = new char[]{c1, c}; else cArray = new char[]{c};
		 * 
		 * return new String(cArray);
		 */
	}

	/**
	 * method for converting an alpha numeric text well position into a numeric (per well index)
	 * 
	 * @param wellPosition
	 *            String value of the well position in letter number format (A1, A01)
	 * @param xORy
	 *            String value of "x" or "y" index
	 * @return the numeric value of the well position for the passed index
	 */
	public static int convertWellPositionToNumericValue(String wellPosition, String xORy) {
		if (xORy.equalsIgnoreCase(X_TEXT))
			return Integer.parseInt(getXorYStringValue(wellPosition, xORy));
		else if (xORy.equalsIgnoreCase(Y_TEXT))
			return convertLetterStringToANumber(getXorYStringValue(wellPosition, xORy));
		else
			return 0;
	}

	/**
	 * method for converting a alphabetic string string into a numeric value. i.e input A, return 1 input Z, return 26 input AA,
	 * return 27 Will not work for input larger that 2 characters
	 * 
	 * @param letterString
	 *            String value of the letter string to be converted
	 * @return int value of the well position for the passed index
	 */
	public static int convertLetterStringToANumber(String letterString) {
		NumberSystemMapper nsm = new NumberSystemMapper();
		return nsm.mapSystemToInteger(letterString);
		/**
		 * int number; int currentChar;
		 * 
		 * StringBuffer s = new StringBuffer(letterString.toUpperCase()); s.reverse();
		 * 
		 * int capA = Character.getNumericValue(new Character('A').charValue());
		 * 
		 * number = Character.getNumericValue(s.charAt(0)) - (capA - 1);
		 * 
		 * for(int x=1;x<s.length(); x++){ currentChar = Character.getNumericValue(s.charAt(x)) + 1; while(currentChar > capA){
		 * number = number + 26; currentChar--; } } return number;
		 */
	}

	/**
	 * method to return the Y value of an alpha numeric position string (Y value is the letter portion
	 * 
	 * @param wellPosition
	 *            String value of the well position
	 * @param xORy
	 *            String value "X" if numberic return wanted, "Y" if alphabetic return wanted, null if neither X or Y is passed
	 * @return String Value of the alphabetic portion of the well position
	 */
	public static String getXorYStringValue(String wellPosition, String xORy) {
		String yValue = "";
		String xValue = "";
		for (int x = 0; x < wellPosition.length(); x++) {
			if (Character.isLetter(wellPosition.charAt(x)))
				yValue = yValue + wellPosition.charAt(x);
			else {
				if (!(xValue.length() == 0 && Integer.parseInt(String.valueOf(wellPosition.charAt(x))) == 0))
					xValue = xValue + wellPosition.charAt(x);
			}
		}
		if (xORy.equalsIgnoreCase(X_TEXT))
			return xValue;
		else if (xORy.equalsIgnoreCase(Y_TEXT))
			return yValue;
		else
			return null;
	}

	/**
	 * method to get the alpha numeric position for an item
	 * 
	 * @param item
	 * @return String value of the alpha numeric position
	 */
	public static String getPositionForItem(int item, int numberOfRows) {
		int column = (item / numberOfRows) + 1;
		int row = item % numberOfRows + 1;
		return PlateGeneralMethods.convertNumericStringToALetter(String.valueOf(row)) + String.valueOf(column); //12.03 fix empty well positions
		//return PlateGeneralMethods.convertNumericStringToALetter(String.valueOf(numberOfRows - row + 1)) + String.valueOf(column);
	}
}
