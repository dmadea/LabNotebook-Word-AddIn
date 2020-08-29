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
/*
 * Created on Aug 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.sdk;

/**
 * 
 *
 * TODO Add Class Information
 */
public interface ChemistryStructureInterface
{
    public boolean areMoleculesEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException;

    public ChemistryProperties getMolecularInformation(byte[] chemistry) throws ChemUtilAccessException;
    
    public MoleculePropertyInfo getMoleculefromChimeString(String chimeString) throws ChemUtilAccessException;

    public MoleculePropertyInfo getMoleculeInfofromMDLString(String mdlMolString) throws ChemUtilAccessException;
	
	public boolean isMoleculeChiral(byte[] chemistry) throws ChemUtilAccessException;
	
	public byte[] setChiralFlag(byte[] chemistry, boolean flag) throws ChemUtilAccessException;

	public byte[] convertToPicture(byte[] chemistry, int format, int height, int width, double loss, double pixelToMM) throws ChemUtilAccessException;
	
	public void dispose();
}
