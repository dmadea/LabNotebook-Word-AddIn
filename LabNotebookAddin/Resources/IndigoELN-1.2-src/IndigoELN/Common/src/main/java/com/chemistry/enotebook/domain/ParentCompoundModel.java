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
package com.chemistry.enotebook.domain;


import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import org.apache.commons.lang.StringUtils;



public class ParentCompoundModel extends CeNAbstractModel {

	public static final long serialVersionUID = 7526472295622776147L;

	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;

	private String molfile="";

	// Note: sdFiles and molFiles should be passed to the setNativeSketch() which will
	// fit out the rest of the information
	private byte[] viewSketch; // (STRUCT_IMAGE) Displayed version for speedbar - JPG
	private byte[] stringSketch; // (STRUCT_SKETCH)To be passed to and from component - Cartridge format
	private byte[] nativeSketch; // (NATIVE_STRUCT_SKETCH) To be passed to and from component - ISIS/Draw or ChemDraw
	private String nativeSketchFormat=""; // ISISDraw or ChemDraw or SDFile

	private String stringSketchFormat=""; // compressed structure from Design Service

	// The following flag indicates which structures need to be
	// persisted in the CeN database. The danger is curation at a
	// later date. This data store may not be updated when the structure
	// is corrected further down the discovery and development line.
	// Set this flag to true if structure info should be stored.
	// All product batch structures should be stored automatically.
	private boolean createdByNotebook = false;

	// MetaData for compound needs to be persisted for all types of compound
	private String compoundName=""; // Name the user might give the compound not chemically associated like P1 for Product 1
	private String regNumber=""; // Same as CompoundId - used during registration to indicate this is another batch of PF-XYZ
	private String compoundParent=""; // Reg number equivalent to stripping the salt code in modern cases but might be different in
	// legacy systems.
	private String chemicalName=""; // Name given to compound ex: 'THF' or 'Tetrahydrafuran.
	private String casNumber="";
	private String stereoisomerCode="";
	private String molFormula="";
	private double molWgt; // Equivalent to parent molecular weight
	private double exactMass; // Mass calculation from mol formula or structure.
	private final AmountModel meltingPt = new AmountModel(UnitType.TEMP); // Stores melting point in temp units
	private final AmountModel boilingPt = new AmountModel(UnitType.TEMP); // Stores compound
	private String comments="";
	private String hazardComments="";
	private String structureComments="";
	private String virtualCompoundId="";

	public ParentCompoundModel() {
		this.key = GUIDUtil.generateGUID(this);

	}

	public ParentCompoundModel(String key) {
		this.key = key;

	}

	public ParentCompoundModel(String key, byte[] nativeStruct, byte[] jpegStruct, String nativeSketchFormat) {
		this(key);
		this.viewSketch = jpegStruct;
		this.nativeSketch = nativeStruct;
		this.nativeSketchFormat = nativeSketchFormat;

	}

	public ParentCompoundModel(String key, byte[] nativeStruct, byte[] jpegStruct, String nativeSketchFormat, double molWgt,
			double parentMolWgt, String molFormula) {
		this(key, nativeStruct, jpegStruct, nativeSketchFormat);
		this.molWgt = molWgt;
		this.molFormula = molFormula;
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
		comments = null;
		structureComments = null;
		virtualCompoundId = null;
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
		this.modelChanged = true;
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
		this.compoundName = newName;
		this.modelChanged = true;
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

		compoundParent = newParent;
		this.modelChanged = true;
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

		this.regNumber = regNumber;
		this.modelChanged = true;
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

		this.chemicalName = chemicalName;
		this.modelChanged = true;
	}

	/**
	 * @return Returns the molFormula.
	 */
	public String getMolFormula() {
		return (molFormula == null) ? "" : molFormula.trim();
	}

	/**
	 * @param molFormula The molFormula to set.
	 */
	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula == null ? "" : molFormula.trim();
		this.modelChanged = true;
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
			this.modelChanged = true;
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
			this.modelChanged = true;
		}
	}

	/**
	 * @return Returns the Boiling Point.
	 */
	public AmountModel getBoilingPt() {
		return boilingPt;
	}

	/**
	 * @param boilingPt
	 *            The Boiling Point to set.
	 */
	public void setBoilingPt(AmountModel boilingPt) {

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
		if (casNumber != null) {
			// && casNumber.indexOf("CAS") >= 0) { // NOT SURE Why this line was here

			this.casNumber = casNumber;
			this.modelChanged = true;
		}
	}

	/**
	 * @return Returns the Melting Point.
	 */
	public AmountModel getMeltingPt() {
		return meltingPt;
	}

	/**
	 * @param meltingPt
	 *            The Melting Point to set.
	 */
	public void setMeltingPt(AmountModel meltingPt) {
		this.meltingPt.setValue(meltingPt.getValue());
		this.modelChanged = true;

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
//		stereoisomerCode = (isomerCode == null || isomerCode.length() == 0) ? "" : isomerCode.length() > 5 ? isomerCode.substring(0, 5) : isomerCode;
		if(StringUtils.isBlank(isomerCode)) {
			stereoisomerCode = "";
		} else  if (isomerCode.startsWith("HSREG")) {
            return;
        } else if(isomerCode.length() > 5) {
			stereoisomerCode = isomerCode.substring(0, 5);
		} else {
			stereoisomerCode = isomerCode;
		}
		this.modelChanged = true;
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

		this.comments = comments;
		this.modelChanged = true;

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

		this.hazardComments = hazardComments;
		this.modelChanged = true;

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
			this.modelChanged = true;
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
		if (structureComments == null)
			this.structureComments = "";
		else
			this.structureComments = structureComments;
		this.modelChanged = true;

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
		this.modelChanged = true;

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
		this.modelChanged = true;

	}

	/**
	 * Cartridge image whatever format that is.... I.E. indexed sketch for searching the database.
	 * 
	 * @return
	 */
	public byte[] getStringSketch() {
		return stringSketch;
	}

	/**
	 * Cartridge image whatever format that is.... I.E. indexed sketch for searching the database.
	 * 
	 * @param stringSketch
	 */
	public void setStringSketch(byte[] searchSketch) {
		this.stringSketch = searchSketch;
		this.modelChanged = true;

	}

	/**
	 * @return Returns the nativeSketchFormat.
	 */
	public String getNativeSketchFormat() {
		return (nativeSketchFormat == null) ? "" : nativeSketchFormat;
	}

	public void setNativeSketchFormat(String sketchFormat) {
		nativeSketchFormat = sketchFormat;
		this.modelChanged = true;
	}

	/**
	 * @return Returns the format this Compounds Sketch (ISISDRAW or CHEMDRAW).
	 */
	public int getFormat() {
		if (getNativeSketchFormat().equals("ISIS Sketch"))
			return ISISDRAW;
		else if (getNativeSketchFormat().equals("ChemDraw"))
			return CHEMDRAW;
		else
			return 0;
	}

	public String getStringSketchFormat() {
		return stringSketchFormat;
	}

	public void setStringSketchFormat(String dspStructure) {
		this.stringSketchFormat = dspStructure;
		this.modelChanged = true;
	}
/*
	private void clearDependentSketches() {
		stringSketch = null;
		viewSketch = null;
		setSketchDependentVars(null);
	}

	private void setSketchDependentVars(ChemistryProperties cp) {
		if (cp != null) {
			molWgt = cp.MolecularWeight;
			molFormula = cp.MolecularFormula;
			exactMass = cp.ExactMolecularWeight;
			if (cp.Name != null && cp.Name.length() > 0)
				chemicalName = cp.Name;
		} else {
			molWgt = 0.0;
			molFormula = "";
			exactMass = 0.0;
			if (!(chemicalName != null && chemicalName.length() > 0))
				chemicalName = "";
		}
	}
*/
	/**
	 * @return the virtualCompoundId
	 */
	public String getVirtualCompoundId() {
		return virtualCompoundId;
	}

	/**
	 * @param virtualCompoundId
	 *            the virtualCompoundId to set
	 */
	public void setVirtualCompoundId(String virtualCompoundId) {
		this.virtualCompoundId = virtualCompoundId;
		this.modelChanged = true;
	}

	public String getStringSketchAsString() {
		if(stringSketch != null)
		{
			return new String(stringSketch);	
		}else
		{
			return "";	
		}
		
	}
	// 
	// Overrides / Implements methods
	//
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append(CeNConstants.XML_VERSION_TAG);
		xmlbuff.append("<Structure_Properties>");
		xmlbuff.append("   <Meta_Data>");
		xmlbuff.append("   </Meta_Data>");
		xmlbuff.append("</Structure_Properties>");
		return xmlbuff.toString();
	}
	
	public String toString(){
		return "Key :"+this.getKey()+"\n"
		 +"ChemicalName :"+this.getChemicalName()+"\n"
		 +"MolFormula :"+this.getMolFormula()+"\n"
		 +"stereoisomerCode :"+this.stereoisomerCode+"\n"
		 +"VirtualCompoundId :"+this.getVirtualCompoundId()+"\n"
		 +"RegNumber :"+this.getRegNumber()+"\n"
		 +"MOLECULAR_WEIGHT :"+this.getMolWgt() ;
	}
	
	
	public Object deepClone()
	{
		//ParentCompoundModel key should be the same
		ParentCompoundModel compModel = new ParentCompoundModel(this.getKey());
		compModel.deepCopy(this);
		return compModel;	
	}

	
	public int compareTo(Object o) {
		int result = 0;
		if (o != null && o instanceof ParentCompoundModel) {
			ParentCompoundModel cp = (ParentCompoundModel) o;
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

	public void deepCopy(ParentCompoundModel src) {
		if (this.key.equals(src.key))
		{
			setBoilingPt((AmountModel)boilingPt.deepClone());
		}
		else
		{
			AmountModel amtModel = new AmountModel(src.boilingPt.getUnit().getType());
			amtModel.deepCopy(src.boilingPt);
			setBoilingPt(amtModel);
		}
		setCASNumber(src.casNumber);
		setChemicalName(src.getChemicalName());
		setComments(src.getComments());
		setCompoundName(src.getCompoundName());
		setCompoundParent(src.getCompoundParent());
		setCreatedByNotebook(src.isCreatedByNotebook());
		setExactMass(src.exactMass);
		setHazardComments(src.hazardComments);
		setMeltingPt(src.meltingPt);
		setMolfile(src.molfile);
		setMolFormula(src.molFormula);
		setMolWgt(src.molWgt);
		setNativeSketch(src.nativeSketch);
		setNativeSketchFormat(src.nativeSketchFormat);
		setRegNumber(src.regNumber);
		setStereoisomerCode(src.stereoisomerCode);
		setStringSketch(src.stringSketch);
		setStringSketchFormat(src.stringSketchFormat);
		setStructureComments(src.structureComments);
		setViewSketch(src.viewSketch);
		setVirtualCompoundId(src.virtualCompoundId);
		setModelChanged(false);
		setModelChanged(src.modelChanged);
	}
}
