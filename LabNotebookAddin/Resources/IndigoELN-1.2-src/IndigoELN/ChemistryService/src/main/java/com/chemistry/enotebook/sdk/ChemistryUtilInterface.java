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
 * Created on Aug 4, 2004
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
public interface ChemistryUtilInterface
{
	public boolean isChemistryEmpty(byte[] chemistry) throws ChemUtilAccessException;
	
    public boolean isChemistryEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException;
    
	public byte[] convertChemistry(byte[] inBuff, String inBuffType, String outBuffType) throws ChemUtilAccessException;
	
	public void dispose();
}
