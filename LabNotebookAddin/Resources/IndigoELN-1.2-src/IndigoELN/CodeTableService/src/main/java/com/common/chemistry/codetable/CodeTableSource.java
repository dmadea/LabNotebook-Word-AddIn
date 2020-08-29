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
package com.common.chemistry.codetable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CodeTableSource implements Serializable {

	private static final long serialVersionUID = -5189567668925928380L;
	
	// The typesafe enum pattern
	private final String sourceName;

	private CodeTableSource(String name) {
		this.sourceName = name;
	}

	public String toString() {
		return sourceName;
	}

	public static final CodeTableSource CompoundManagement = new CodeTableSource("CompoundManagement");
	public static final CodeTableSource GCD = new CodeTableSource("GCD");
	public static final CodeTableSource CEN = new CodeTableSource("CEN");

	private static final CodeTableSource[] VALS = { CompoundManagement, GCD, CEN };

	public static final List<CodeTableSource> VALUES = Arrays.asList(VALS);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (arg0 instanceof String) {
			return sourceName.equals((String) arg0);
		} else if (arg0 instanceof CodeTableSource) {
			return sourceName.equals(((CodeTableSource) arg0).toString());
		} else {
			return super.equals(arg0);
		}
	}
}
