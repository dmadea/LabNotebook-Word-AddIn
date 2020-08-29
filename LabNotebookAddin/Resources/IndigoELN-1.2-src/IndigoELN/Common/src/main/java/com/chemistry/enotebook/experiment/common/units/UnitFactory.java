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

import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * 
 *
 * 
 * Molar Concentrations
 * 
 * 1 Molar = 1 M = 1 mole/L = 1 mmole/mL 1 millimolar = 1 mM = 1 mmole/L = 1 umole/mL 1 micromolar = 1 uM = 1 umole/L = 1 nmole/mL 1
 * nanomolar = 1 nM = 1 nmole/L = 1 pmole/mL
 * 
 * Weight
 * 
 * 1 kilogram = 1 kg = 1000 grams 1 gram = 1 g = 1 gram = 1000 milligrams 1 milligram = 1 mg = 1000 micrograms 1 microgram = 1 ug =
 * 1000 nanograms 1 nanogram = 1 ng = 1000 picograms
 * 
 * Volume
 * 
 * 1 Liter = 1 L = 1000 milliliters 1 milliliter = 1 mL = 1000 microliters 1 microliter = 1 uL = 1000 nanoliters
 */
public class UnitFactory {
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

//	public static String getPreferredUnitCode(String amountUnitXPath) {
//		String result = null;
//		String preferredUnitCodeXML = NotebookUser.getInstance().getTablePropertiesDescendant(amountUnitXPath);
//		if (!preferredUnitCodeXML.equals(""))
//			result = preferredUnitCodeXML;
//
//		return result;
//	}

	public static Unit createUnitOfType(UnitType ut) {
		Unit result = null;
		// UnitCache cache = UnitCache.getInstance();
		// String unitCode = null;
		switch (ut.getOrdinal()) {
			case UnitType.MASS_ORDINAL:
				// unitCode = getPreferredUnitCode("MG");
				// result = cache.getUnit(unitCode);
				result = new Unit("MG", ut, "mg", "Milligrams", "MG", 1, 1);
				break;
			case UnitType.MOLES_ORDINAL:
				result = new Unit("MMOL", ut, "mmol", "Millimole", "MMOL", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.VOLUME_ORDINAL:
				// unitCode = getPreferredUnitCode("ML");
				// result = cache.getUnit(unitCode);
				result = new Unit("ML", ut, "mL", "Milliliter", "ML", 1, STD_DISPLAY_FIGS); // Times ten to the -3rd
				break;
			case UnitType.MOLAR_ORDINAL:
				// unitCode = getPreferredUnitCode("M");
				// result = cache.getUnit(unitCode);
				result = new Unit("M", ut, "M", "Molar", "M", 1, STD_DISPLAY_FIGS); // Times ten to the 0th power
				break;
			case UnitType.DENSITY_ORDINAL:
				result = new Unit("GML", ut, "g/mL", "Grams/Milliliter", "GML", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.SCALAR_ORDINAL:
				result = new Unit("SCAL", ut, "", "Scalar - no units appropriate", "SCAL", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.LOADING_ORDINAL:
				result = new Unit("MMOLG", ut, "mmol/g", "Millimole/Gram", "MMOLG", 1, STD_DISPLAY_FIGS);
				break;
			case UnitType.TEMP_ORDINAL:
				result = new Unit("C", ut, "C", "Celcius", "C", 1, STD_DISPLAY_FIGS);
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

	public static Map getUnitsOfType(UnitType ut) {
		TreeMap result = new TreeMap();
		switch (ut.getOrdinal()) {
			case UnitType.MASS_ORDINAL:
				// result.put("NG", new Unit("NG", ut, "ng", "Nanograms", "MG", 0.000000001));
				// result.put("PG", new Unit("PG", ut, "pg", "Picograms", "MG", 0.000001));
				// result.put("UG", new Unit("UG", ut, "ug", "Micrograms", "MG", 0.001));
				result.put("MG", new Unit("MG", ut, "mg", "Milligrams", "MG", 1, 1));
				result.put("GM", new Unit("GM", ut, "g", "Grams", "MG", 1000, STD_DISPLAY_FIGS));
				result.put("KG", new Unit("KG", ut, "kg", "Kilograms", "MG", 1000000, 6));
				break;
			case UnitType.MOLES_ORDINAL:
				// result.put("PMOL", new Unit("PMOL", ut, "pMol", "Picomole", "MMOL", 0.000001));
				result.put("UMOL", new Unit("UMOL", ut, "umol", "Micromole", "MMOL", 0.001, 1));
				result.put("MMOL", new Unit("MMOL", ut, "mmol", "Millimole", "MMOL", 1, STD_DISPLAY_FIGS));
				result.put("MOL", new Unit("MOL", ut, "mol", "Mole", "MMOL", 1000, STD_DISPLAY_FIGS));
				break;
			case UnitType.VOLUME_ORDINAL:
				// result.put("PL", new Unit("PL", ut, "pL", "Picoliter", "ML", 0.000001)); // Times ten to the -9th
				result.put("UL", new Unit("UL", ut, "uL", "Microliter", "ML", 0.001, 1)); // Times ten to the -6th
				result.put("ML", new Unit("ML", ut, "mL", "Milliliter", "ML", 1, STD_DISPLAY_FIGS)); // Times ten to the -3rd
				result.put("L", new Unit("L", ut, "L", "Liter", "ML", 1000, STD_DISPLAY_FIGS)); // 1 L
				// result.put("DL", new Unit("DL", ut, "DL", "Decaliter", "ML", 10000)); // 10 L
				break;
			case UnitType.MOLAR_ORDINAL:
				// result.put("FM", new Unit("FM", ut, "fM", "Femto-Molar", "M", 0.000000000000001)); // Times ten to the -15th
				// result.put("NM", new Unit("NM", ut, "nM", "Nano-Molar", "M", 0.000000000001)); // Times ten to the -12th
				// result.put("PM", new Unit("PM", ut, "pM", "Pico-Molar", "M", 0.000000001)); // Times ten to the -9th
				// result.put("UM", new Unit("UM", ut, "uM", "Micro-Molar", "M", 0.000001)); // Times ten to the -6th
				result.put("MM", new Unit("MM", ut, "mM", "Milli-Molar", "M", 0.001, STD_DISPLAY_FIGS)); // Times ten to the -3rd
				result.put("M", new Unit("M", ut, "M", "Molar", "M", 1, STD_DISPLAY_FIGS)); // Times ten to the 0th.
				break;
			case UnitType.DENSITY_ORDINAL:
				// result.put("PGUL", new Unit("PGUL", ut, "pg/uL", "Picograms/Microliter", "GML", 0.000001)); // Same ratio
				// result.put("UGML", new Unit("UGML", ut, "ug/mL", "Micrograms/Milliliter", "GML", 0.000001)); // Same ratio
				// result.put("MGML", new Unit("MGML", ut, "mg/mL", "Milligrams/Milliliter", "GML", 0.001));
				result.put("GML", new Unit("GML", ut, "g/mL", "Grams/Milliliter", "GML", 1, STD_DISPLAY_FIGS));
				// result.put("UGL", new Unit("UGL", ut, "ug/L", "Micrograms/Liter", "GML", 0.000000001));
				// result.put("MGL", new Unit("MGL", ut, "mg/L", "Milligrams/Liter", "GML", 0.000001));
				// result.put("GL", new Unit( "GL", ut, "g/L", "Grams/Liter", "GML", 0.001));
				break;
			case UnitType.SCALAR_ORDINAL:
				result.put("SCAL", new Unit("SCAL", ut, "", "Scalar - no units appropriate", "SCAL", 1, STD_DISPLAY_FIGS));
				break;
			case UnitType.LOADING_ORDINAL:
				result.put("MMOLG", new Unit("MMOLG", ut, "mmol/g", "Millimole/Gram", "MMOLG", 1, STD_DISPLAY_FIGS));
				break;
			case UnitType.TEMP_ORDINAL:
				result.put("C", new Unit("C", ut, "C", "Celcius", "C", 1, STD_DISPLAY_FIGS));
				break;
			default:
				result.put("SCAL", new Unit("SCAL", ut, "", "", "Scalar - no units appropriate", 1, STD_DISPLAY_FIGS));
				break;
		}
		return result;
	}

	public static String[] getComboArrayForType(UnitType ut) {
		Map units = UnitFactory.getUnitsOfType(ut);
		StringBuffer results = new StringBuffer();
		Unit u = null;
		for (Iterator it = units.keySet().iterator(); it.hasNext();) {
			u = (Unit) units.get(it.next());
			if (results.length() > 0)
				results.append(' ');
			results.append(u.getDisplayValue());
			u = null;
		}
		return results.toString().split(" ");
	}

	public static String getPreferredUnitForAmount(Amount amt, NotebookPage page, NotebookUser user) throws UserPreferenceException {
		StringBuffer temp = null;
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
		}

		String pageProperties = page.getTableProperties();
		if (pageProperties == null)
			throw new UserPreferenceException("Failed to Retrieve NotebookProperties in UnitFactory");
		if (!pageProperties.equals("")) {
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
			if(user != null) {
				String preferredUnit = user.getPreferredUnitAsString(type);
				if (StringUtils.isNotBlank(preferredUnit)) {
					temp = new StringBuffer(preferredUnit);
				}
			}
		}
		if (temp != null && StringUtils.isNotBlank(temp.toString())) {
			return temp.toString();
		} else {
			return null;
		}
	}
}
