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

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ResourceKit {
	public static final String ABCD[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
	public static final String RGroups[] = { "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8" };
	public static final List MONOMER_LIST = Arrays.asList(ABCD);
	public static final List R_LIST = Arrays.asList(RGroups);

	public static String getABCD(int a) {
		return (a > -1 && a < ABCD.length) ? ABCD[a] : "";
	}

	public static String getNumber(int a) {
		return (a < 10) ? "0" + a : "" + a;
	}
	
	public static boolean isMonomerListRootName(String aName) {
		return MONOMER_LIST.contains(aName);
	}

	public static boolean isRGroupRootName(String aName) {
		return R_LIST.contains(aName);
	}

	public static boolean isListRoot(String aName) {
		return isMonomerListRootName(aName) || isRGroupRootName(aName);
	}

	public static String getRgroupRoot(int index) {
		return (index > -1 && index < RGroups.length) ? RGroups[index] : "";
	}

	public static String getABCDRoot(int index) {
		return (index > -1 && index < ABCD.length) ? ABCD[index] : "";
	}

	public static boolean withinLimits(int pos) {
		return pos < Math.min(ABCD.length, RGroups.length);
	}

	/*
	 * public static String getABCD(int a){ String r=""; switch (a) { case 0: r="A"; break; case 1: r="B"; break; case 2: r="C";
	 * break; case 3: r="D"; break; } return r; }
	 */
	public static int getIndex(String groupName) {
		int r = -1;
		if (groupName != null) {
			r = MONOMER_LIST.indexOf(groupName.toUpperCase());
			if (r == -1) {
				r = R_LIST.indexOf(groupName.toUpperCase());
			}
		}
		return r;
	}

	public static String getabcd(int a) {
		String r = "";
		switch (a) {
			case 0:
				r = "a";
				break;
			case 1:
				r = "b";
				break;
			case 2:
				r = "c";
				break;
			case 3:
				r = "d";
				break;
		}
		return r;
	}

	public static String get1234(int a) {
		String r = "";
		switch (a) {
			case 0:
				r = "1";
				break;
			case 1:
				r = "2";
				break;
			case 2:
				r = "3";
				break;
			case 3:
				r = "4";
				break;
		}
		return r;
	}

	public static String getString1234(String s, int a) {
		String r = "";
		switch (a) {
			case 0:
				r = s + "1";
				break;
			case 1:
				r = s + "2";
				break;
			case 2:
				r = s + "3";
				break;
			case 3:
				r = s + "4";
				break;
		}
		return r;
	}

	public static Dimension getComponentPreferredSize() {
		return new Dimension(240, 240);
	}

	public static Dimension getTabPreferredSize() {
		return new Dimension(220, 220);
	}

	public static Color getComponentBackgroudColor() {
		return new Color(252, 252, 200);
	}

	public static Color getCoreBackgroudColor() {
		return new Color(252, 252, 200);
	}
	
	// vb 6/24 Any set of 6 or less alphanumeric characters is OK.
	public static boolean iaValidBatchNumber(String batchNumber) {
		String []batchNumberArr = batchNumber.split("-");
		if (batchNumberArr.length < 3)
			return false;
		String lotNumber = batchNumberArr[2];
		if (lotNumber.length() > 6)
			return false;
		if (lotNumber.matches("\\p{Alnum}+"))
			return true;
		return false;
//		Pattern p = Pattern.compile("[A-Za-Z0-9]");
//		//Pattern p = Pattern.compile("[0-9][0-9][0-9][0-9][A-Z][0-9]");
//		Matcher m = p.matcher(batchNumberArr[2]);
//		return m.matches();
	}
}
