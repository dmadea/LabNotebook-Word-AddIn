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
package com.chemistry.enotebook.experiment.datamodel.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CeNBigDecimal extends BigDecimal {

	private static final long serialVersionUID = 3159333203589090040L;

	public CeNBigDecimal(double val) {
		super(val);
	}

	public CeNBigDecimal(String val) {
		super(val);
	}

	public CeNBigDecimal(BigInteger val) {
		super(val);
	}

	public CeNBigDecimal(BigInteger unscaledVal, int scale) {
		super(unscaledVal, scale);
	}

	public BigDecimal divide(BigDecimal b, int rounding) {
		int myscale = scale();
		String t = b.unscaledValue().toString();
		myscale = myscale + (t.length() - 1);
		return super.divide(b, myscale, rounding);
	}

	public BigDecimal multiply(BigDecimal a) {
		int sigfigs = this.unscaledValue().toString().length();
		BigDecimal d1 = super.multiply(a);
		String prod = "" + d1.intValue();
		int dsigfigs = prod.length();
		if (sigfigs > dsigfigs) {
			int diff = sigfigs - dsigfigs;
			d1 = d1.setScale(diff, BigDecimal.ROUND_UP);
		} else {
			d1 = d1.setScale(0, BigDecimal.ROUND_UP);
		}
		return d1;
	}

}
