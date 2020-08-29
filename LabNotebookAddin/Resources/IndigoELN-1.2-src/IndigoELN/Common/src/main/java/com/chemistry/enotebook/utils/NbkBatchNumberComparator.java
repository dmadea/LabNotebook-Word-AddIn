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

import com.chemistry.enotebook.domain.BatchModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Comparator;

/**
 * Compare notebook batch numbers.  Assumes that these numbers
 * are in the format NNNNNNNN-NNNN-NNNNNN.  The comparason is 
 * performed on the last six numbers -- the lot numbers.  NOTE:
 * it is possible that the lot numbers contain alphabetic characters.
 * If this is the case the comparator returns a zero.
 */

public class NbkBatchNumberComparator<E extends BatchModel> implements Comparator<E> {
	
	public static final Log log = LogFactory.getLog(NbkBatchNumberComparator.class);

	public int compare(E batch1, E batch2) {
		if (batch1 == null && batch2 == null)
			return 0;
		else if (batch1 == null)
			return -1;
		else if (batch2 == null)
			return 1;
//		if (! (o1 instanceof BatchModel && o2 instanceof BatchModel)) {
//			log.error(this.getClass().getName() + " comparing " + o1.getClass() + " and " + o2.getClass());
//			throw new IllegalArgumentException();
//		}
//		BatchModel batch1 = (BatchModel) batch1;
//		BatchModel batch2 = (BatchModel) batch2;
		if (batch1.getBatchNumber() == null || batch2.getBatchNumber() == null)
			return 0;
		String batchNumberStr1 = batch1.getBatchNumberAsString();
		String batchNumberStr2 = batch2.getBatchNumberAsString();
		if (batchNumberStr1.length() == 0 || batchNumberStr2.length() == 0) {
			if (batchNumberStr2.length() == 0 && batchNumberStr1.length() > 0) {
				return 1;
			}
			if (batchNumberStr1.length() == 0 && batchNumberStr2.length() > 0) {
				return -1;
			}
			if (batchNumberStr1.length() == 0 && batchNumberStr2.length() == 0) {
				return 0;
			}
		}
		int lotNumber1 = -1;
		int lotNumber2 = -1;
		try {
			try {			
				lotNumber1 = Integer.parseInt(getIntegerPart(batch1.getBatchNumber().getLotNumber()));
			} catch (NumberFormatException e) {
				// not number
			}
			try {			
				lotNumber2 = Integer.parseInt(getIntegerPart(batch2.getBatchNumber().getLotNumber()));
			} catch (NumberFormatException e) {
				
			}
			if (lotNumber1 > 0 && lotNumber2 > 0) { 
				if (lotNumber1 > lotNumber2) 
					return 1;
				else if (lotNumber2 > lotNumber1)
					return -1;
			} else {
				if (lotNumber1 > 0) {
					return -1;
				} else if (lotNumber2 > 0) {
					return 1;
				} else {
					String s1 = batch1.getBatchNumber().getLotNumber();
					String s2 = batch2.getBatchNumber().getLotNumber();
					return s1.compareTo(s2);
				}
			}
		} catch (Exception e) {
			log.error("Batch number formatting failed: " + e);
		}
		return 0;
	}

	protected String getIntegerPart(String s) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9')
				buff.append(c);
			else 
				break;
		}
		return buff.toString();
	}
}