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
/* 
 * CeNNumberUtils.java
 * 
 * Created on Nov 18, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.utils;

import com.chemistry.enotebook.domain.AmountModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @date Nov 18, 2004
 */
public class CeNNumberUtils {
	private static final double LN10_VALUE = Math.log(10);

	public static final int DEFAULT_SIG_DIGITS = 3;

	public static int defaultFixedFigs = 3;
	public static double defaultDoubleDelta = 0.000000001;

	public static boolean doubleEquals(double x, double y, double delta) {
		boolean result = false;
		if (Math.abs(x) < Math.abs(y))
			result = (Math.abs(Math.abs(x) - Math.abs(y)) <= delta);
		else
			result = (Math.abs(Math.abs(y) - Math.abs(x)) <= delta);
		return result;
	}

	public static boolean doubleEquals(double x, double y) {
		return doubleEquals(x, y, defaultDoubleDelta);
	}

	public static BigDecimal getBigDecimal(double val) {
		return getBigDecimal(val, defaultFixedFigs);
	}

	public static BigDecimal getBigDecimal(double val, int fixedFigs) {
		BigDecimal result = CeNNumberUtils.getDefaultBigDecimal();
		if (!Double.isInfinite(val) && !Double.isNaN(val)) {
			result = new BigDecimal(val);
			result = result.setScale(fixedFigs, BigDecimal.ROUND_HALF_UP);
		}
		return result;
	}

	public static BigDecimal getBigDecimal(String val) {
		return CeNNumberUtils.getBigDecimal(val, defaultFixedFigs);
	}

	/**
	 * Will return 0.0 to fixed figs place if val is alpha-numeric and not just numeric Otherwise the return is a rounded up
	 * BigDecimal with the set scale.
	 * 
	 * @param val -
	 *            string value to change to a BigDecimal
	 * @param fixedFigs -
	 *            scale of the BigDecimal
	 * @return - BigDecimal set to the proper scale. 0.0 to proper scale if val somehow fails to be represented as an BigInteger
	 */
	public static BigDecimal getBigDecimal(String val, int fixedFigs) {
		BigDecimal result = CeNNumberUtils.getDefaultBigDecimal();
		if (val.length() > 0) {
			try {
				result = new BigDecimal(val);
			} catch (NumberFormatException e) {
				// not a number should use defaultBigDecimal();
			}
			if (!Double.isInfinite(result.doubleValue()) && !Double.isNaN(result.doubleValue())) {
				result = result.setScale(fixedFigs, BigDecimal.ROUND_HALF_UP);
			}
		}
		return result;
	}

	public static BigDecimal getDefaultBigDecimal() {
		return CeNNumberUtils.getDefaultBigDecimal(defaultFixedFigs);
	}

	public static BigDecimal getDefaultBigDecimal(int fixedFigs) {
		BigDecimal result = new BigDecimal(0.0);
		return result.setScale(fixedFigs, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * input must be a number
	 * 
	 * @param input
	 * @param fractionDigits
	 * @return double - formatted with the fractionDigits
	 */
	public static double round(double input, int fractionDigits) {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		fmt.setGroupingUsed(false);
		fmt.setMaximumFractionDigits(fractionDigits);
		fmt.setMinimumFractionDigits(fractionDigits);
		return Double.parseDouble(fmt.format(input));
	}

	/**
	 * Don't use for obscenely large resulting integers. Calculates to the nearest integer power of 10 that a number represents.
	 * 
	 * @param value
	 * @return integer version of the power of 10 of the submitted value
	 */
	public static int powerOfTen(double value) {
		int result = 0;
		if (value != 1.0 && value != 0) {
			// technically we should be using a long.
			BigDecimal bd = new BigDecimal(Math.log(Math.abs(value)) / LN10_VALUE);
			BigDecimal bdFinal = bd.setScale(15, BigDecimal.ROUND_HALF_UP);
			// ////System.out.println("PowerOfTen of " + value + " = " + Math.floor(bdFinal.doubleValue()));
			result = (int) Math.floor(bdFinal.doubleValue());
		}
		return result;
	}

	/**
	 * Note on Significant Figures:
	 * 
	 * Online: http://chemed.chem.purdue.edu/genchem/topicreview/bp/ch1/sigfigs.html#determ 1) All nonzero digits are
	 * significant--457 cm (three significant figures); 0.25 g (two significant figures). 2) Zeros between nonzero digits are
	 * significant--1005 kg (four significant figures); 1.03 cm (three significant figures). 3) Zeros within a number are always
	 * significant. Both 4308 and 40.05 contain four significant figures 4) Zeros to the left of the first nonzero digits in a
	 * number are not significant; they merely indicate the position of the decimal point--0.02 g (one significant figure); 0.0026
	 * cm (two significant figures). 5) When a number ends in zeros that are to the right of the decimal point, they are
	 * significant--0.0200 g (three significant figures); 3.0 cm (two significant figures).
	 * 
	 * Scientist override on standard rules above. 1) Scientists do not want to deal with the "." when there are trailing zeros in a
	 * number. Instead they wish to have the decimal point understood. Making "10" two sig figs and "1100" four sig figs.
	 * 
	 * Does not deal with negative values.
	 * 
	 * @param val -
	 *            number represented as a string from which we will extract s
	 * @return String = representing the significant figures from the input string. No decimal is returned.
	 */
	public static String getSigFigsFromNumberString(String val) {
		StringBuffer result = new StringBuffer();
		int indexOfDecimal = val.indexOf(".");
		// March through string to find nonzero and sig zero characters.
		boolean finished = false;
		for (int i = 0; !finished && i < val.length(); i++) {
			if (i != indexOfDecimal) {
				int number = Character.getNumericValue(val.charAt(i));
				// handle numbers like 123: item 1
				if (number > 0) {
					result.append(number);
				} else if (result.length() > 0 && number == 0) {
					// handle numbers like 1100
					result.append(number);
				} else if (indexOfDecimal >= 0 && i > indexOfDecimal) {
					// handle numbers with values after decimal point
					if (result.length() > 0 && number == 0) {
						result.append(number);
					}
				}
			}
		}
		return result.toString();
	}

	/**
	 * Same as getSmallestSigFigsFromAmountModelList now that we have retired the Amount object
	 * @param amounts
	 * @return
	 */
	public static int getSmallestSigFigsFromList(List<AmountModel> amounts) {
		int smallestSigFig = 9999;
		for (Iterator<AmountModel> i = amounts.iterator(); i.hasNext();) {
			AmountModel amt = i.next();
			if (amt.getSigDigitsSet() && amt.getSigDigits() < smallestSigFig)
				smallestSigFig = amt.getSigDigits();
		}
		return (smallestSigFig == 9999 ? DEFAULT_SIG_DIGITS : smallestSigFig);
	}
	
	/**
	 * 
	 * @param amounts
	 * @return
	 */
	public static int getSmallestSigFigsFromAmountModelList(List<AmountModel> amounts) {
		int smallestSigFig = 9999;
		for (Iterator<AmountModel> i = amounts.iterator(); i.hasNext();) {
			AmountModel amt = i.next();
			if (amt.getSigDigitsSet() && amt.getSigDigits() < smallestSigFig)
				smallestSigFig = amt.getSigDigits();
		}
		return (smallestSigFig == 9999 ? DEFAULT_SIG_DIGITS : smallestSigFig);
	}
}
