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
package com.chemistry.enotebook.vnv.classes;

import java.io.Serializable;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class UcCompoundInfo implements Serializable {
	static final long serialVersionUID = -9046247089983948771L;

	private String molStruct = "";
	private String isomerCode = "";
	private String isomerDescr = "";

	private String regNumber = "";
	private String molWgt = "";
	private String molFormula = "";
	private String comments = "";

	private boolean isNewParent = false;
	private boolean isExact = false;
	private boolean isLegacy = false;
	private boolean isIsomer = false;
	private boolean isCuration = false;

	private boolean isSelected = false;

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return Returns the isomerCode.
	 */
	public String getIsomerCode() {
		return isomerCode;
	}

	/**
	 * @param isomerDescr
	 *            The isomerCode to set.
	 */
	public void setIsomerCode(String isomerCode) {
		this.isomerCode = isomerCode;
	}

	/**
	 * @return Returns the isomerDescr.
	 */
	public String getIsomerDescr() {
		return isomerDescr;
	}

	/**
	 * @param isomerDescr
	 *            The isomerDescr to set.
	 */
	public void setIsomerDescr(String isomerDescr) {
		this.isomerDescr = isomerDescr;
	}

	/**
	 * @return Returns the molFormula.
	 */
	public String getMolFormula() {
		return molFormula;
	}

	/**
	 * @param molFormula
	 *            The molFormula to set.
	 */
	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	/**
	 * @return Returns the molStruct.
	 */
	public String getMolStruct() {
		return molStruct;
	}

	/**
	 * @param molStruct
	 *            The molStruct to set.
	 */
	public void setMolStruct(String molStruct) {
		this.molStruct = molStruct;
	}

	/**
	 * @return Returns the molWgt.
	 */
	public String getMolWgt() {
		return molWgt;
	}

	/**
	 * @param molWgt
	 *            The molWgt to set.
	 */
	public void setMolWgt(String molWgt) {
		this.molWgt = molWgt;
	}

	/**
	 * @return Returns the regNumber.
	 */
	public String getRegNumber() {
		return regNumber;
	}

	/**
	 * @param regNumber
	 *            The regNumber to set.
	 */
	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	/**
	 * @return Returns the isExact.
	 */
	public boolean isExact() {
		return isExact;
	}

	/**
	 * @param flag
	 *            The isExact to set.
	 */
	public void setIsExact(boolean flag) {
		this.isExact = flag;
	}

	/**
	 * @return Returns the isNewParent.
	 */
	public boolean isNewParent() {
		return isNewParent;
	}

	/**
	 * @param flag
	 *            The isNewParent to set.
	 */
	public void setIsNewParent(boolean flag) {
		this.isNewParent = flag;
	}

	/**
	 * @return Returns the isLegacy.
	 */
	public boolean isLegacy() {
		return isLegacy;
	}

	/**
	 * @param flag
	 *            The isLegacy to set.
	 */
	public void setIsLegacy(boolean flag) {
		this.isLegacy = flag;
	}

	/**
	 * @return Returns the isIsomer.
	 */
	public boolean isIsomer() {
		return isIsomer;
	}

	/**
	 * @param flag
	 *            The isIsomer to set.
	 */
	public void setIsIsomer(boolean flag) {
		this.isIsomer = flag;
	}

	/**
	 * @return Returns the isCuration.
	 */
	public boolean isCuration() {
		return isCuration;
	}

	/**
	 * @param flag
	 *            The isCuration to set.
	 */
	public void setIsCuration(boolean flag) {
		this.isCuration = flag;
	}

	/**
	 * @return Returns the isSelected.
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param flag
	 *            The isSelected to set.
	 */
	public void setIsSelected(boolean flag) {
		this.isSelected = flag;
	}
}