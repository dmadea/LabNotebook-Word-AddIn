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
 * Created on Aug 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.sdk;

import java.io.Serializable;

/**
 * 
 *
 * TODO Add Class Information
 */
public class ChemistryProperties
	implements Serializable
{
	static final long serialVersionUID = 2113897175159243027L;

	public static String CHEM_DRAW_SKETCH = "ChemDraw";
	public static String ISIS_DRAW_SKETCH = "ISIS Sketch";
	
	public String Name;
	public double MolecularWeight = 0;
	public String MolecularFormula;
	public double ExactMolecularWeight = 0;
}
