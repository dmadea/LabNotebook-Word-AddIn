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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.domain.ProductPlate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Comparator;

/**
 * Compare notebook plate numbers.  Assumes that these numbers
 * are in the format PL<NN>.  The comparason is 
 * performed on the last two numbers -- the plate lot numbers.  
 */

public class PlateNumberComparator implements Comparator<ProductPlate> {
	
	public static final Log log = LogFactory.getLog(PlateNumberComparator.class);

	public int compare(ProductPlate plate1, ProductPlate plate2) {
		if (plate1 == null && plate2 == null)
			return 0;
		else if (plate1 == null)
			return -1;
		else if (plate2 == null)
			return 1;
		if (plate1.getLotNo() == null || plate2.getLotNo() == null)
			return 0;
		String plateNumberStr1 = plate1.getLotNo();
		String plateNumberStr2 = plate2.getLotNo();
		if (plateNumberStr1.length() == 0 || plateNumberStr2.length() == 0) {
			if (plateNumberStr2.length() == 0 && plateNumberStr1.length() > 0) {
				return 1;
			}
			if (plateNumberStr1.length() == 0 && plateNumberStr2.length() > 0) {
				return -1;
			}
			if (plateNumberStr1.length() == 0 && plateNumberStr2.length() == 0) {
				return 0;
			}
		}
		int lotNumber1;
		int lotNumber2;
		try {
			lotNumber1 = Integer.valueOf(plate1.getLotNo().substring(2)).intValue();
			lotNumber2 = Integer.valueOf(plate2.getLotNo().substring(2)).intValue();
			if (lotNumber1 > lotNumber2) 
				return 1;
			else if (lotNumber2 > lotNumber1)
				return -1;

		} catch (Exception e) {
			log.error("Batch number formatting failed: " + e);
		}
		return 0;
	}

}