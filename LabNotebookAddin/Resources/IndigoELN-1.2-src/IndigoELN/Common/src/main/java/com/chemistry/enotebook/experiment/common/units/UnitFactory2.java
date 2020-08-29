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

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.common.Amount2;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class UnitFactory2 {

	public static final int STD_DISPLAY_FIGS = 3;
	// Codes found here were derived from the then single resource COMPOUND_MANAGEMENT_UNIT_CDT in Groton - Aug. 31, 2004
	// public static final String [] massCodes = { "NG", "PG", "UG", "MG", "GM", "KG" };
	public static final String[] massCodes = { "MG", "GM", "KG" };
	// public static final String [] moleCodes = { "PMOL", "UMOL", "MMOL", "MOL" };
	public static final String[] moleCodes = { "MMOL" };
	// public static final String [] molarCodes = { "FM", "NM", "UM", "MM", "M" };
	public static final String[] molarCodes = { "MM", "M" };
	// public static final String [] volumeCodes = { "PL", "UL", "ML", "L", "DL", "MM3" };
	public static final String[] volumeCodes = { "UL", "ML", "L" };
	// public static final String [] densityCodes = { "PGUL", "UGML", "MGML", "GML", "UGL", "MGL", "GL" };
	public static final String[] densityCodes = { "GML" };
	public static final String[] percentCodes = { "SCAL" };
	public static final String[] loadingCodes = { "MMOLG" };
	// public static final String [] weightPercentCodes = { "NGKG", "PGKG", "UGKG", "MGKG" }; // Resins
	// public static final String [] volumePercentCodes = { "NLML", "PLML", "ULML", "MLML" };
	// public static final String [] weightPerVolumeCodes = { "NGML", "PGML", "UGML", "MGML",
	// "UGDL", "MGDL" };

	// public static final String [] areaCodes = { "NM2", "PM2", "MM2", "CM2", "M2", "KM2" };
	// public static final String [] timeCodes = { "MSEC", "SECS", "MINS", "HOURS", "DAYS" };
	// public static final String [] lengthCodes = { "NMETR", "UMETR", "MMETR", "CMETR", "METR", "KMETR" };
	// public static final String [] temperatureCodes = { "C", "F", "K", "R" };
	public static final String[] temperatureCodes = { "C" };
	public static final String[] scalarCodes = {};


	public static Unit2 createUnitOfType(UnitType ut) {
		Unit2 result = null;
		// UnitCache cache = UnitCache.getInstance();
		// String unitCode = null;
		switch (ut.getOrdinal()) {
			case UnitType.MASS_ORDINAL:
				// unitCode = getPreferredUnitCode("MG");
				// result = cache.getUnit(unitCode);
				result = new Unit2("MG", ut, "mg", "Milligrams", "MG", 1, 1);
				break;
			case UnitType.MOLES_ORDINAL:
				result = new Unit2("MMOL", ut, "mmol", "Micromole", "MMOL", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.VOLUME_ORDINAL:
				// unitCode = getPreferredUnitCode("ML");
				// result = cache.getUnit(unitCode);
				result = new Unit2("ML", ut, "mL", "Milliliter", "ML", 1, STD_DISPLAY_FIGS); // Times ten to the -3rd
				break;
			case UnitType.MOLAR_ORDINAL:
				// unitCode = getPreferredUnitCode("M");
				// result = cache.getUnit(unitCode);
				result = new Unit2("M", ut, "M", "Molar", "M", 1, STD_DISPLAY_FIGS); // Times ten to the 0th power
				break;
			case UnitType.DENSITY_ORDINAL:
				result = new Unit2("GML", ut, "g/mL", "Grams/Milliliter", "GML", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.SCALAR_ORDINAL:
				result = new Unit2("SCAL", ut, "", "Scalar - no units appropriate", "SCAL", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.LOADING_ORDINAL:
				result = new Unit2("MMOLG", ut, "mmol/g", "Millimole/Gram", "MMOLG", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.TEMP_ORDINAL:
				result = new Unit2("C", ut, "C", "Celcius", "C", 1, STD_DISPLAY_FIGS);
				break;
			default:
				break;
		}
		return result;
	}

	public static String[] getUnitCodesOfType(UnitType ut) {
		switch (ut.getOrdinal()) {
			case UnitType.MASS_ORDINAL:
				return massCodes;
			case UnitType.MOLES_ORDINAL:
				return moleCodes;
			case UnitType.VOLUME_ORDINAL:
				return volumeCodes;
			case UnitType.MOLAR_ORDINAL:
				return molarCodes;
			case UnitType.DENSITY_ORDINAL:
				return densityCodes;
			case UnitType.SCALAR_ORDINAL:
				return scalarCodes;
			case UnitType.LOADING_ORDINAL:
				return loadingCodes;
			case UnitType.TEMP_ORDINAL:
				return temperatureCodes;
			default:
				return null;
		}
	}

	public static Map<String, Unit2> getUnitsOfType(UnitType ut) {
		TreeMap<String, Unit2> result = new TreeMap<String, Unit2>();
		switch (ut.getOrdinal()) {
			case UnitType.MASS_ORDINAL:
				// result.put("NG", new Unit2("NG", ut, "ng", "Nanograms", "MG", 0.000000001));
				// result.put("PG", new Unit2("PG", ut, "pg", "Picograms", "MG", 0.000001));
				// result.put("UG", new Unit2("UG", ut, "ug", "Micrograms", "MG", 0.001));
				result.put("MG", new Unit2("MG", ut, "mg", "Milligrams", "MG", 1, 1));
				result.put("GM", new Unit2("GM", ut, "g", "Grams", "MG", 1000, STD_DISPLAY_FIGS));
				result.put("KG", new Unit2("KG", ut, "kg", "Kilograms", "MG", 1000000, STD_DISPLAY_FIGS));
				break;
			case UnitType.MOLES_ORDINAL:
				// result.put("PMOL", new Unit2("PMOL", ut, "pMol", "Picomole", "MMOL", 0.000001));
				result.put("UMOL", new Unit2("UMOL", ut, "umol", "Micromole", "MMOL", 0.001, 1));
				result.put("MMOL", new Unit2("MMOL", ut, "mmol", "Millimole", "MMOL", 1, STD_DISPLAY_FIGS));
				result.put("MOL", new Unit2("MOL", ut, "mol", "Mole", "MMOL", 1000, STD_DISPLAY_FIGS));
				break;
			case UnitType.VOLUME_ORDINAL:
				// result.put("PL", new Unit2("PL", ut, "pL", "Picoliter", "ML", 0.000001)); // Times ten to the -9th
				result.put("UL", new Unit2("UL", ut, "uL", "Microliter", "ML", 0.001, 1)); // Times ten to the -6th
				result.put("ML", new Unit2("ML", ut, "mL", "Milliliter", "ML", 1, STD_DISPLAY_FIGS)); // Times ten to the -3rd
				result.put("L", new Unit2("L", ut, "L", "Liter", "ML", 1000, STD_DISPLAY_FIGS)); // 1 L
				// result.put("DL", new Unit2("DL", ut, "DL", "Decaliter", "ML", 10000)); // 10 L
				break;
			case UnitType.MOLAR_ORDINAL:
				// result.put("FM", new Unit2("FM", ut, "fM", "Femto-Molar", "M", 0.000000000000001)); // Times ten to the -15th
				// result.put("NM", new Unit2("NM", ut, "nM", "Nano-Molar", "M", 0.000000000001)); // Times ten to the -12th
				// result.put("PM", new Unit2("PM", ut, "pM", "Pico-Molar", "M", 0.000000001)); // Times ten to the -9th
				// result.put("UM", new Unit2("UM", ut, "uM", "Micro-Molar", "M", 0.000001)); // Times ten to the -6th
				result.put("MM", new Unit2("MM", ut, "mM", "Milli-Molar", "M", 0.001, STD_DISPLAY_FIGS)); // Times ten to the -3rd
				result.put("M", new Unit2("M", ut, "M", "Molar", "M", 1, STD_DISPLAY_FIGS)); // Times ten to the 0th.
				break;
			case UnitType.DENSITY_ORDINAL:
				// result.put("PGUL", new Unit2("PGUL", ut, "pg/uL", "Picograms/Microliter", "GML", 0.000001)); // Same ratio
				// result.put("UGML", new Unit2("UGML", ut, "ug/mL", "Micrograms/Milliliter", "GML", 0.000001)); // Same ratio
				// result.put("MGML", new Unit2("MGML", ut, "mg/mL", "Milligrams/Milliliter", "GML", 0.001));
				result.put("GML", new Unit2("GML", ut, "g/mL", "Grams/Milliliter", "GML", 1, STD_DISPLAY_FIGS));
				// result.put("UGL", new Unit2("UGL", ut, "ug/L", "Micrograms/Liter", "GML", 0.000000001));
				// result.put("MGL", new Unit2("MGL", ut, "mg/L", "Milligrams/Liter", "GML", 0.000001));
				// result.put("GL", new Unit2( "GL", ut, "g/L", "Grams/Liter", "GML", 0.001));
				break;
			case UnitType.SCALAR_ORDINAL:
				result.put("SCAL", new Unit2("SCAL", ut, "", "Scalar - no units appropriate", "SCAL", 1, STD_DISPLAY_FIGS));
				break;
			case UnitType.LOADING_ORDINAL:
				result.put("MMOLG", new Unit2("MMOLG", ut, "mmol/g", "Millimole/Gram", "MMOLG", 1, STD_DISPLAY_FIGS));
				break;
			case UnitType.TEMP_ORDINAL:
				result.put("C", new Unit2("C", ut, "C", "Celcius", "C", 1, STD_DISPLAY_FIGS));
				break;
			default:
				result.put("SCAL", new Unit2("SCAL", ut, "", "", "Scalar - no units appropriate", 1, STD_DISPLAY_FIGS));
				break;
		}
		return result;
	}

	public static String[] getComboArrayForType(UnitType ut) {
		Map<String, Unit2> units = UnitFactory2.getUnitsOfType(ut);
		StringBuffer results = new StringBuffer();
		Unit2 u = null;
		for (Iterator<String> it = units.keySet().iterator(); it.hasNext();) {
			u = units.get(it.next());
			if (results.length() > 0)
				results.append(' ');
			results.append(u.getDisplayValue());
			u = null;
		}
		return results.toString().split(" ");
	}

	public static String getPreferredUnitForAmount(Amount2 amt, NotebookPageModel page, NotebookUser user) throws UserPreferenceException {
		StringBuffer temp = new StringBuffer();
		String result = null;
		String unitName = null;
		UnitType type = amt.getUnitType();

		// check which amount we have and retrieve the unit type from it
		if ((type.toString()).equals("MASS")) {
			unitName = "<Mass_Amount_Unit_Code>";
		} else if ((type.toString()).equals("MOLAR")) {
			unitName = "<Molar_Amount_Unit_Code>";
		} else if ((type.toString()).equals("MOLES")) {
			unitName = "<Moles_Amount_Unit_Code>";
		} else if ((type.toString()).equals("VOLUME")) {
			unitName = "<Volume_Amount_Unit_Code>";
		} // else unitName == null;

		String pageProperties = page.getTableProperties();
		if (pageProperties == null)
			throw new UserPreferenceException("Failed to Retrieve NotebookProperties in UnitFactory");
		if (StringUtils.isNotBlank(pageProperties)) {
			if (unitName != null) {
				int startIndex = pageProperties.indexOf(unitName);
				String endUnitElement = unitName.replaceFirst("<", "</");
				if (startIndex != -1) {
					int endIndex = pageProperties.indexOf(endUnitElement);
					temp = new StringBuffer(pageProperties);
					result = temp.substring(startIndex, endIndex);
					temp.setLength(0);
					temp.append(result.replaceAll(unitName, ""));

				}// end if
			} // end if
		} else {
			if( user != null && type != null) {
				String preferredUnit = user.getPreferredUnitAsString(type);
				if(StringUtils.isNotBlank(preferredUnit)) {
					temp.append(preferredUnit);
				}
			}
		}
		if (temp != null && temp.length() > 0) {
			return temp.toString();
		} else {
			return null;
		}
	}
	
	
}
