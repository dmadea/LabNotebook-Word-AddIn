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

public class UnitCache2 {

private static UnitCache2 instance;
private TreeMap<UnitType, Map<String, Unit2>> unitsByType = null;
private TreeMap<String, Unit2> units = null; // Cached by code
private TreeMap<String, Unit2> unitsByDisplayName = null;
private boolean debug = false;

// Constructor
private UnitCache2() {
	units = new TreeMap<String, Unit2>();
	unitsByType = new TreeMap<UnitType, Map<String, Unit2>>();
	unitsByDisplayName = new TreeMap<String, Unit2>();
	init();
}

public static UnitCache2 getInstance() {
	if (instance == null)
		createInstance();
	return instance;
}

// Double-Checked locking
private static void createInstance() {
	if (instance == null) {
		instance = new UnitCache2();
	}
}

public Map<String, Unit2> getUnitsOfType(UnitType ut) {
	return unitsByType.get(ut);
}

public List<Unit2> getUnitsOfTypeAsList(UnitType type) {
	return deepCloneList(new ArrayList(getUnitsOfType(type).values()));
}

public List<Unit2> getUnitsOfTypeSorted(UnitType type) {
	ArrayList<Unit2> result = new ArrayList(getUnitsOfType(type).values());
	// if (debug) //System.out.println("UnitCache.getUnitsOfTypeSorted( " + type + " ): presorted list = " + result);
	Collections.sort(result);
	// if (debug) //System.out.println("UnitCache.getUnitsOfTypeSorted( " + type + " ): sorted list = " + result);
	return deepCloneList(result);
}

public Unit2 getUnit(String unitCode) {
	Unit2 result = null;
	if (unitCode.equals("G"))
		unitCode = "GM";
	if (unitCode.equals("SCLR"))
		unitCode = "SCAL";
	if (units.containsKey(unitCode))
		result = (Unit2) units.get(unitCode);
	if (units.containsKey(unitCode.toUpperCase()))
		result = (Unit2) units.get(unitCode.toUpperCase());
	if (unitsByDisplayName.containsKey(unitCode))
		result = (Unit2) unitsByDisplayName.get(unitCode);
	return result;
}

private void init() {
	for (Iterator<UnitType> it = UnitType.VALUES.iterator(); it.hasNext();) {
		UnitType ut = it.next();
		addMap(ut, UnitFactory2.getUnitsOfType(ut));
	}
}

private void addMap(UnitType type, Map<String, Unit2> mp) {
	if (!unitsByType.containsKey(type))
		unitsByType.put(type, mp);
	Unit2 tempUnit = null;
	for (Iterator<String> it = mp.keySet().iterator(); it.hasNext();) {
		String key = it.next();
		if (!units.containsKey(key)) {
			tempUnit = (Unit2) mp.get(key);
			unitsByDisplayName.put(tempUnit.getDisplayValue(), tempUnit);
			units.put(key, tempUnit);
		}
	}
}

private List<Unit2> deepCloneList(List<Unit2> src) {
	ArrayList<Unit2> result = new ArrayList<Unit2>();
	for (Iterator<Unit2> i = src.iterator(); i.hasNext();) {
		result.add(i.next().deepClone());
	}
	return result;
}

}

