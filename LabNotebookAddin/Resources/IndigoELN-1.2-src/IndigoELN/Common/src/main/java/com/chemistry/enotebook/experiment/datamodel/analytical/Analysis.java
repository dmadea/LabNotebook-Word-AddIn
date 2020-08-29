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
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.analytical;

/**
 * 
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Analysis extends AbstractAnalysis {

	private static final long serialVersionUID = 7343417601427583360L;

	//
	// DeepClone/Copy Interface
	//

	public void deepCopy(Object resource) {
		super.deepCopy(resource);
	}

	public Object deepClone() {
		AbstractAnalysis newAnalysis = new Analysis();
		newAnalysis.deepCopy(this);
		return newAnalysis;
	}

}
