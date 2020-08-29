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
package com.chemistry.enotebook.experiment.datamodel.compound;

import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;

import java.awt.*;

public class Compound extends PersistableObject implements ICompound, DeepCopy, DeepClone, Comparable {
	
	private static final long serialVersionUID = 6727308964957508807L;
	
	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;

	private String molfile;

	// Note: sdFiles and molFiles should be passed to the setNativeSketch() which will
	// fit out the rest of the information
	private byte[] viewSketch; // Displayed version for speedbar - JPG
	private byte[] searchSketch; // To be passed to and from component - Cartridge format
	private byte[] nativeSketch; // To be passed to and from component - ISIS/Draw or ChemDraw
	private String nativeSketchFormat; // ISISDraw or ChemDraw or SDFile

	private String dspStructure; // compressed structure from Design Service

	// The following flag indicates which structures need to be
	// persisted in the CeN database. The danger is curation at a
	// later date. This data store may not be updated when the structure
	// is corrected further down the discovery and development line.
	// Set this flag to true if structure info should be stored.
	// All product batch structures should be stored automatically.
	private boolean createdByNotebook = false;

	// MetaData for compound needs to be persisted for all types of compound
	private String compoundName; // Name the user might give the compound not chemically associated like P1 for Product 1
	private String regNumber;
	private String compoundParent; // Reg number equivalent to stripping the salt code in modern cases but might be different in
	// legacy systems.
	private String chemicalName; // Name given to compound ex: 'THF' or 'Tetrahydrafuran.
	private String casNumber;
	private String stereoisomerCode;
	private String molFormula;
	private double molWgt; // Equivalent to parent molecular weight
	private double exactMass; // Mass calculation from mol formula or structure.
	private final Amount meltingPt = new Amount(UnitType.TEMP); // Stores melting point in temp units
	private final Amount boilingPt = new Amount(UnitType.TEMP); // Stores compound
	private Color color;
	private String comments;
	private String hazardComments;
	private String structureComments;

	public Compound() {
		super();
		init();
	}

	public Compound(String key, byte[] nativeStruct, byte[] jpegStruct, String nativeSketchFormat) {
		super(key);
		this.viewSketch = jpegStruct;
		this.nativeSketch = nativeStruct;
		this.nativeSketchFormat = nativeSketchFormat;
		init();
	}

	public Compound(String key, byte[] nativeStruct, byte[] jpegStruct, String nativeSketchFormat, double molWgt,
			double parentMolWgt, String molFormula) {
		this(key, nativeStruct, jpegStruct, nativeSketchFormat);
		this.molWgt = molWgt;
		this.molFormula = molFormula;
	}

	private void init() {
		if (meltingPt != null)
			meltingPt.addObserver(this);
		if (boilingPt != null)
			boilingPt.addObserver(this);
	}

	/**
	 * Resets names, id numbers, color, comments and melting and boiling points.
	 * 
	 */
	public void resetIDs() {
		regNumber = null;
		compoundName = null;
		chemicalName = null;
		casNumber = null;
		compoundParent = null;
		hazardComments = null;
		stereoisomerCode = null;
		color = null;
		comments = null;
		structureComments = null;
		meltingPt.reset();
		boilingPt.reset();
	}

	//
	// Structure MetaData
	//

	/**
	 * @return Returns whether this Compound is registered or not.
	 */
	public boolean isRegistered() {
		return (regNumber != null && !regNumber.equals(""));
	}

	public String getMolfile() {
		return molfile;
	}

	public void setMolfile(String molfile) {
		this.molfile = molfile;
	}

	/**
	 * Name of the compound is not necessarily chemically significant. Can be "A", "B", "C" if the chemist wants. If the name is
	 * chemically significant as in a IUPAC chemical name use getChemicalName() instead.
	 * 
	 * @return Returns the name attached to this compound.
	 */
	public String getCompoundName() {
		return (compoundName == null) ? "" : compoundName;
	}

	/**
	 * Name of the compound is not necessarily chemically significant. Can be "A", "B", "C" if the chemist wants. If the name is
	 * chemically significant as in a IUPAC chemical name use setChemicalName(String) instead.
	 * 
	 * @param newName
	 *            name to attach to this compound - can be any string
	 */
	public void setCompoundName(String newName) {
		if (areStringsDifferent(this.compoundName, newName)) {
			this.compoundName = newName;
			setModified(true);
		}
	}

	/**
	 * The parent compound number for this structure if one exists.
	 * 
	 * @return String or null if not set.
	 */
	public String getCompoundParent() {
		return (compoundParent == null) ? "" : compoundParent;
	}

	/**
	 * Set the compound parent id. for this structure.
	 * 
	 * @param newParent -
	 *            String.
	 */
	public void setCompoundParent(String newParent) {
		if (areStringsDifferent(this.compoundName, newParent)) {
			compoundParent = newParent;
			setModified(true);
		}
	}

	/**
	 *
	 * @return Returns the regNumber.
	 */
	public String getRegNumber() {
		return (regNumber == null) ? "" : regNumber;
	}

	/**
	 * @param regNumber
	 *            The regNumber to set.
	 */
	public void setRegNumber(String regNumber) {
		if (areStringsDifferent(this.regNumber, regNumber)) {
			this.regNumber = regNumber;
			setModified(!isLoading());
		}
	}

	/**
	 * 
	 * @return String representation of chemical name
	 */
	public String getChemicalName() {
		return (chemicalName == null) ? "" : chemicalName;
	}

	/**
	 * 
	 * @param chemicalName -
	 *            string representing the readable chemical name. ex: 'THF' or 'DMSO'
	 */
	public void setChemicalName(String chemicalName) {
		if (areStringsDifferent(this.chemicalName, chemicalName)) {
			this.chemicalName = chemicalName;
			setModified(true);
		}
	}

	/**
	 * @return Returns the molFormula.
	 */
	public String getMolFormula() {
		return (molFormula == null) ? "" : molFormula;
	}

	/**
	 * @param molFormula
	 *            The molFormula to set.
	 */
	public void setMolFormula(String molFormula) {
		if (areStringsDifferent(this.molFormula, molFormula)) {
			this.molFormula = molFormula;
			setModified(true);
		}
	}

	/**
	 * @return Returns the molWgt.
	 */
	public double getMolWgt() {
		return molWgt;
	}

	/**
	 * @param molWgt
	 *            The molWgt to set.
	 */
	public void setMolWgt(double molWgt) {
		if (molWgt >= 0.0 && this.molWgt != molWgt) {
			this.molWgt = molWgt;
			setModified(true);
		}
	}

	/**
	 * @return Returns the Exact Mass.
	 */
	public double getExactMass() {
		return exactMass;
	}

	/**
	 * @param mass
	 *            The Exact Mass to set.
	 */
	public void setExactMass(double mass) {
		if (mass >= 0.0 && this.exactMass != mass) {
			this.exactMass = mass;
			setModified(true);
		}
	}

	/**
	 * @return Returns the Boiling Point.
	 */
	public Amount getBoilingPt() {
		return boilingPt;
	}

	/**
	 * @param boilingPt
	 *            The Boiling Point to set.
	 */
	public void setBoilingPt(Amount boilingPt) {
		if (!this.boilingPt.equals(boilingPt)) {
			this.boilingPt.deepCopy(boilingPt);
			setModified(true);
		}
	}

	/**
	 * @return Returns the casNumber.
	 */
	public String getCASNumber() {
		return (this.casNumber == null) ? "" : casNumber;
		// Not sure why the regNumber was used instead of casNumber
		// return (regNumber == null) ? "" : regNumber;
	}

	/**
	 * @param CASNumber
	 *            The CASNumber to set.
	 */
	public void setCASNumber(String casNumber) {
		// Need to validate CAS number string
		if (areStringsDifferent(this.casNumber, casNumber) && casNumber != null) {
			// && casNumber.indexOf("CAS") >= 0) { // NOT SURE Why this line was here

			this.casNumber = casNumber;
			setModified(true);
		}
	}

	/**
	 * @return Returns the Melting Point.
	 */
	public Amount getMeltingPt() {
		return meltingPt;
	}

	/**
	 * @param meltingPt
	 *            The Melting Point to set.
	 */
	public void setMeltingPt(Amount meltingPt) {
		if (!this.meltingPt.equals(meltingPt)) {
			this.meltingPt.deepCopy(meltingPt);
			setModified(true);
		}
	}

	/**
	 * @return Returns the stereoisomerCode.
	 */
	public String getStereoisomerCode() {
		return (stereoisomerCode == null) ? "" : stereoisomerCode;
	}

	/**
	 * @param stereoisomerCode
	 *            The stereoisomerCode to set.
	 */
	public void setStereoisomerCode(String isomerCode) {
		if (areStringsDifferent(stereoisomerCode, isomerCode)) {
			stereoisomerCode = isomerCode;
			setModified(true);
		}
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return (comments == null) ? "" : comments;
	}

	/**
	 * @param comment
	 *            The comments to set.
	 */
	public void setComments(String comments) {
		if (areStringsDifferent(this.comments, comments)) {
			this.comments = comments;
			setModified(true);
		}
	}

	/**
	 * @return Returns the Hazard comment.
	 */
	public String getHazardComments() {
		return (hazardComments == null) ? "" : hazardComments;
	}

	/**
	 * @param comment
	 *            The Hazard comment to set.
	 */
	public void setHazardComments(String hazardComments) {
		if (areStringsDifferent(this.hazardComments, hazardComments)) {
			this.hazardComments = hazardComments;
			setModified(true);
		}
	}

	/**
	 * @return Returns the createdInNotebook.
	 */
	public boolean isCreatedByNotebook() {
		return createdByNotebook;
	}

	/**
	 * @param createdInNotebook
	 *            The createdInNotebook to set.
	 */
	public void setCreatedByNotebook(boolean createdByNotebook) {
		if (this.createdByNotebook != createdByNotebook) {
			this.createdByNotebook = createdByNotebook;
			setModified(true);
		}
	}

	/**
	 * @return Returns the structureComments.
	 */
	public String getStructureComments() {
		return (structureComments == null) ? "" : structureComments;
	}

	/**
	 * @param structureComments
	 *            The structureComments to set.
	 */
	public void setStructureComments(String structureComments) {
		if (areStringsDifferent(this.structureComments, structureComments)) {
			this.structureComments = structureComments;
			setModified(true);
		}
	}

	/**
	 * @return Returns the viewSketch.
	 */
	public byte[] getViewSketch() {
		return viewSketch;
	}

	/**
	 * @param viewSketch
	 *            The viewSketch to set.
	 */
	public void setViewSketch(byte[] viewSketch) {
		this.viewSketch = viewSketch;
		setModified(true);
	}

	/**
	 * Sketch from drawing application - look at sketch format to figure that out.
	 * 
	 * @return Returns the nativeSketch.
	 */
	public byte[] getNativeSketch() {
		return nativeSketch;
	}

	/**
	 * Sketch from drawing application - look at sketch format to figure that out.
	 * 
	 * @param nativeSketch
	 *            The nativeSketch to set.
	 */
	public void setNativeSketch(byte[] nativeSketch) {
		this.nativeSketch = nativeSketch;
		setModified(true);
	}

	/**
	 * Cartridge image whatever format that is.... I.E. indexed sketch for searching the database.
	 * 
	 * @return
	 */
	public byte[] getSearchSketch() {
		return searchSketch;
	}

	/**
	 * Cartridge image whatever format that is.... I.E. indexed sketch for searching the database.
	 * 
	 * @param searchSketch
	 */
	public void setSearchSketch(byte[] searchSketch) {
		this.searchSketch = searchSketch;
		setModified(true);
	}

	/**
	 * @return Returns the nativeSketchFormat.
	 */
	public String getNativeSketchFormat() {
		return (nativeSketchFormat == null) ? "" : nativeSketchFormat;
	}

	/**
	 * @param nativeSketchFormat
	 *            The nativeSketchFormat to set.
	 */
	public void setNativeSketchFormat(String nativeSketchFormat) {
		this.nativeSketchFormat = nativeSketchFormat;
		setModified(true);
	}

//	/**
//	 * @return Returns the format this Compounds Sketch (ISISDRAW or CHEMDRAW).
//	 */
//	public int getFormat() {
//		if (getNativeSketchFormat().equals("ISIS Sketch"))
//			return ISISDRAW;
//		else if (getNativeSketchFormat().equals("ChemDraw"))
//			return CHEMDRAW;
//		else
//			return 0;
//	}


//


	public String getDspStructure() {
		return dspStructure;
	}

	public void setDspStructure(String dspStructure) {
		this.dspStructure = dspStructure;
	}

//	private void clearDependentSketches() {
//		searchSketch = null;
//		viewSketch = null;
//		setSketchDependentVars(null);
//	}
//
//	private void setSketchDependentVars(ChemistryProperties cp) {
//		if (cp != null) {
//			molWgt = cp.MolecularWeight;
//			molFormula = cp.MolecularFormula;
//			exactMass = cp.ExactMolecularWeight;
//			if (cp.Name != null && cp.Name.length() > 0)
//				chemicalName = cp.Name;
//		} else {
//			molWgt = 0.0;
//			molFormula = "";
//			exactMass = 0.0;
//			if (!(chemicalName != null && chemicalName.length() > 0))
//				chemicalName = "";
//		}
//	}

	// 
	// Overrides / Implements methods
	//

	/**
	 * Compares this to the object entered. Works off of available data priority is: reg number first then cas number
	 * 
	 * @param o =
	 *            object which to compare
	 * @return int 1 if greater, 0 if no comparable or they are equal, -1 if less
	 */
	public int compareTo(Object o) {
		int result = 0;
		if (o != null && o instanceof Compound) {
			Compound cp = (Compound) o;
			if (getRegNumber() != null && getRegNumber().length() > 0 && cp.getRegNumber() != null
					&& cp.getRegNumber().length() > 0)
				result = (getRegNumber().compareTo(cp.getRegNumber()));
			else if (getCASNumber() != null && getCASNumber().length() > 0 && cp.getCASNumber() != null
					&& cp.getCASNumber().length() > 0)
				result = (getCASNumber().compareTo(cp.getCASNumber()));
			else if (getCompoundName() != null && getCompoundName().length() > 0 && cp.getCompoundName() != null
					&& cp.getCompoundName().length() > 0)
				result = (getCompoundName().compareTo(cp.getCompoundName()));
			else if (getChemicalName() != null && getChemicalName().length() > 0 && cp.getChemicalName() != null
					&& cp.getChemicalName().length() > 0)
				result = (getChemicalName().compareTo(cp.getChemicalName()));
			else {// Use molwgt
				if (CeNNumberUtils.doubleEquals(getMolWgt(), cp.getMolWgt(), 0.00001))
					result = 0;
				else if (getMolWgt() - cp.getMolWgt() > 0)
					result = 1;
				else
					result = -1;
			}
		}
		return result;
	}

	public void deepCopy(Object source) {
		if (source != null && source instanceof Compound) {
			Compound sourceInstance = (Compound) source;

			viewSketch = sourceInstance.viewSketch;
			searchSketch = sourceInstance.searchSketch;
			nativeSketch = sourceInstance.nativeSketch;
			nativeSketchFormat = sourceInstance.nativeSketchFormat;
			regNumber = sourceInstance.regNumber;
			chemicalName = sourceInstance.chemicalName;
			compoundName = sourceInstance.compoundName;
			casNumber = sourceInstance.casNumber;
			stereoisomerCode = sourceInstance.stereoisomerCode;
			molFormula = sourceInstance.molFormula;
			molWgt = sourceInstance.molWgt;
			exactMass = sourceInstance.exactMass;
			meltingPt.deepCopy(sourceInstance.meltingPt);
			boilingPt.deepCopy(sourceInstance.boilingPt);
			comments = sourceInstance.comments;
			hazardComments = sourceInstance.hazardComments;
			createdByNotebook = sourceInstance.createdByNotebook;
			structureComments = sourceInstance.structureComments;
		}
	}

	public Object deepClone() {
		Compound target = new Compound();
		target.deepCopy(this);
		return target;
	}

//	// method to check if the structure is empty
//	public boolean isCompoundStructureEmpty() {
//		if (nativeSketch != null && nativeSketch.length > 0) {
//			return false;
//		} else {
//			ChemistryDelegate chemDel = null;
//			try {
//				chemDel = new ChemistryDelegate();
//			} catch (Exception e) {
//			}
//			if (chemDel != null)
//				try {
//					return chemDel.isChemistryEmpty(nativeSketch);
//				} catch (Exception e) {
//					e.printStackTrace();
//					return true;
//				}
//			else
//				return true;
//		}
//	}
}
