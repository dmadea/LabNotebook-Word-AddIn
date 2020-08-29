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
package com.chemistry.enotebook.client.utils;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.experiment.common.units.UnitType;

public class AmountModelCalculator {

	/**
	 * Create a new amountModel that is the product of the two arguments, and set
	 * the sig figs to the smaller of the two amountModel's sig figs.  NOTE:  this assumes
	 * that the units are multipliable, that is, that the units are the same, or one is a 
	 * scalar, and that the unit scales are the same.  In other words, if one is in mg, the
	 * other is in mg.
	 * @param am1
	 * @param am2
	 * @return
	 */
	public static AmountModel multiplyWithSigFigs(AmountModel am1, AmountModel am2, UnitType unitType) {
		int sigfigs1 = am1.getSigDigits();
		int sigfigs2 = am2.getSigDigits();
		int sigfigs = sigfigs1 > sigfigs2 ? sigfigs2 : sigfigs1;
		double d1 = am1.GetValueInStdUnitsAsDouble();
		double d2 = am2.GetValueInStdUnitsAsDouble();
		AmountModel newam = new AmountModel(unitType); 
		newam.setValue(d1 * d2);
		newam.setSigDigits(sigfigs);
		return newam;
	}
	
	/** do the same without creating a new AmountModel  vb 7/10
	 * 
	 * @param am1
	 * @param am2
	 * @param unitType
	 * @return
	 */
	public static void multiplyWithSigFigs(AmountModel product,	AmountModel am1, AmountModel am2, UnitType unitType) {
		int sigfigs1 = am1.getSigDigits();
		int sigfigs2 = am2.getSigDigits();
		int sigfigs = sigfigs1 > sigfigs2 ? sigfigs2 : sigfigs1;
		double d1 = am1.GetValueInStdUnitsAsDouble();
		double d2 = am2.GetValueInStdUnitsAsDouble();
		//AmountModel newam = new AmountModel(unitType);  vb 7/10
		product.setValue(d1 * d2);
		product.setSigDigits(sigfigs);
	}
	
	public static void setSigFigsFromDouble(AmountModel am, double d) {
		String strDouble = "" + d;
		String cleanedStr = removeZerosAfterDecimal(strDouble);
		am.setValue(cleanedStr);
		cleanedStr = cleanedStr.replaceAll("\\.", "");
		am.setSigDigits(cleanedStr.length());
	}
	
	/**
	 * Hack to remove zeros to right of decimal.  This is for numbers that
	 * are stored as decimal types and not AmountModels.
	 * @param number
	 * @return
	 */
	private static String removeZerosAfterDecimal(String number) {
		StringBuffer buff = new StringBuffer();
		if (number.indexOf(".") < 0)
			buff.append(number);
		else {
			buff.append(number.substring(0, number.indexOf(".")));
			String fractionalPart = number.substring(number.indexOf(".") + 1);
			StringBuffer fracbuff = new StringBuffer();
			for (int i=0; i<fractionalPart.length(); i++) {
				char c = fractionalPart.charAt(i);
				if (c == '0')
					continue;
				else
					fracbuff.append(c);
			}
			if (fracbuff.length() > 0) {
				buff.append(".");
				buff.append(fracbuff);
			}
		}
		return buff.toString();
	}
	
	private boolean isInteger(double d) {
		int i = (int) d;
		if (d - i < 0.00000001)
			return true;
		else
			return false;
	}


}
