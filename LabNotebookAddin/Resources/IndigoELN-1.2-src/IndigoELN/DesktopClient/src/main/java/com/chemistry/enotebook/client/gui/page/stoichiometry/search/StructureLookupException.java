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
 * StructureLookupException.java
 * 
 * Created on May 16, 2005
 *
 * 
 */
package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

/**
 * 
 * @date May 16, 2005
 */
public class StructureLookupException extends Exception {
	
	private static final long serialVersionUID = 6291904740325745899L;

	/**
	 * 
	 */
	public StructureLookupException() {
		super();
	}

	/**
	 * @param message
	 */
	public StructureLookupException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public StructureLookupException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StructureLookupException(String message, Throwable cause) {
		super(message, cause);
	}
}
