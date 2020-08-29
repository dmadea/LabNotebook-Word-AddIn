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

public interface IVnvResult extends Serializable {
	public boolean isPassed();

	public String getResultMol();

	public boolean isMolWasModified();

	public String[] getVnvErrors();

	public String getMolFormula();

	public double getMolWeight();

	public boolean isStereoIsomerAssigned();

	public String getAssignedCode();

	public String[] getValidCodes();
}
