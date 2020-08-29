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
 * Created on Jul 12, 2004
 *
 */
package com.chemistry.enotebook;

/**
 * 
 * 
 */
public class PropertyException extends java.lang.Exception {
	
	private static final long serialVersionUID = -5065921539979326854L;

	public PropertyException() {
		super();
	}

	public PropertyException(String msg) {
		super(msg);
	}

	public PropertyException(String msg, Throwable e) {
		super(msg, e);
	}
}
