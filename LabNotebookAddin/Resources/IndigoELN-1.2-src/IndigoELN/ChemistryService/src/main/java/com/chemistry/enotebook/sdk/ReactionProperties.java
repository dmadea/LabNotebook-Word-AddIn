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
import java.util.List;
import java.util.Vector;

/**
 * 
 *
 * TODO Add Class Information
 */
public class ReactionProperties
	implements Serializable
{
	static final long serialVersionUID = 5815696046358854655L;
	
	public static String CHEM_DRAW_SKETCH = ChemistryProperties.CHEM_DRAW_SKETCH;
	public static String ISIS_DRAW_SKETCH = ChemistryProperties.ISIS_DRAW_SKETCH;

	public boolean isolateFragments = false;

	public byte[] Reaction = null;
	
	public String ReturnedSketchFormat = null;
	
	public List<byte[]> Reactants = new Vector<byte[]>();
	public List<byte[]> Products = new Vector<byte[]>();
	
//	public List aboveArrowComponents = new Vector();
//	public List belowArrowComponents = new Vector();
}
