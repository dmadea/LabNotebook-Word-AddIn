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
package com.chemistry.enotebook.experiment.common.units;

import java.util.*;

/**
 * 
 * 
 *
 * 
 * Unit types are based on (T)ime, (M)ass, (L)ength, (W)eight, (D)ensity
 */
public class UnitCache {
	private static UnitCache instance;
	private TreeMap unitsByType = null;
	private TreeMap units = null; // Cached by code
	private TreeMap unitsByDisplayName = null;
	private boolean debug = false;

	// Constructor
	private UnitCache() {
		units = new TreeMap();
		unitsByType = new TreeMap();
		unitsByDisplayName = new TreeMap();
		init();
	}

	public static UnitCache getInstance() {
		if (instance == null)
			createInstance();
		return instance;
	}

	// Double-Checked locking
	private static void createInstance() {
		if (instance == null) {
			instance = new UnitCache();
		}
	}

	public Map getUnitsOfType(UnitType ut) {
		return (Map) unitsByType.get(ut);
	}

	public List getUnitsOfTypeAsList(UnitType type) {
		return deepCloneList(new ArrayList(getUnitsOfType(type).values()));
	}

	public List getUnitsOfTypeSorted(UnitType type) {
		ArrayList result = new ArrayList(getUnitsOfType(type).values());
		// if (debug) //System.out.println("UnitCache.getUnitsOfTypeSorted( " + type + " ): presorted list = " + result);
		Collections.sort(result);
		// if (debug) //System.out.println("UnitCache.getUnitsOfTypeSorted( " + type + " ): sorted list = " + result);
		return deepCloneList(result);
	}

	public Unit getUnit(String unitCode) {
		Unit result = null;
		if (unitCode.equals("G"))
			unitCode = "GM";
		if (unitCode.equals("SCLR"))
			unitCode = "SCAL";
		if (units.containsKey(unitCode))
			result = (Unit) units.get(unitCode);
		if (units.containsKey(unitCode.toUpperCase()))
			result = (Unit) units.get(unitCode.toUpperCase());
		if (unitsByDisplayName.containsKey(unitCode))
			result = (Unit) unitsByDisplayName.get(unitCode);
		return result;
	}

	private void init() {
		for (Iterator it = UnitType.VALUES.iterator(); it.hasNext();) {
			UnitType ut = (UnitType) it.next();
			addMap(ut, UnitFactory.getUnitsOfType(ut));
		}
	}

	private void addMap(UnitType type, Map mp) {
		if (!unitsByType.containsKey(type))
			unitsByType.put(type, mp);
		Unit tempUnit = null;
		for (Iterator it = mp.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			if (!units.containsKey(key)) {
				tempUnit = (Unit) mp.get(key);
				unitsByDisplayName.put(tempUnit.getDisplayValue(), tempUnit);
				units.put(key, tempUnit);
			}
		}
	}

	private List deepCloneList(List src) {
		ArrayList result = new ArrayList();
		for (Iterator i = src.iterator(); i.hasNext();) {
			result.add(((Unit) i.next()).deepClone());
		}
		return result;
	}

}
