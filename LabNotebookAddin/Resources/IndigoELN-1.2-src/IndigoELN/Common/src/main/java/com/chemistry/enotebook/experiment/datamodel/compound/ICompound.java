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

import com.chemistry.enotebook.sdk.ChemUtilAccessException;

/**
 * 
 * 
 *
 */
public interface ICompound {
	/**
	 * NativeSketch can be anything: An SDFile, ISIS/Draw or ChemDraw sketches
	 */
	public String getNativeSketchFormat();

	/**
	 * Format corresponds to ChemistryViewer Static variables.
	 * 
	 * @param format
	 */
	public void setNativeSketchFormat(String format);

	/**
	 * This is a JPG representation of the structure so that it might be able to appear in a Tool-Tip.
	 * 
	 * @return
	 */
	public byte[] getViewSketch();

	public void setViewSketch(byte[] sketch);


	public byte[] getSearchSketch();

	public void setSearchSketch(byte[] sketch);

	/**
	 * Structure in byte array form, unaltered from its original format.
	 * 
	 * @return
	 */
	public byte[] getNativeSketch();

	public void setNativeSketch(byte[] sketch) throws ChemUtilAccessException;

	// Compound MetaData
	public boolean isRegistered();

	public String getRegNumber();

	public void setRegNumber(String RegNumber);

	public String getMolFormula();

	public void setMolFormula(String molFormula);

	public double getMolWgt();

	public void setMolWgt(double molWgt);

	public String getStereoisomerCode();

	public void setStereoisomerCode(String stereoisomerCode);

	public String getComments();

	public void setComments(String comment);

	public String getHazardComments();

	public void setHazardComments(String hazardComments);

	public boolean isCreatedByNotebook();

	public void setCreatedByNotebook(boolean createdByNotebook);

	public String getCASNumber();

	public void setCASNumber(String casNumber);
}