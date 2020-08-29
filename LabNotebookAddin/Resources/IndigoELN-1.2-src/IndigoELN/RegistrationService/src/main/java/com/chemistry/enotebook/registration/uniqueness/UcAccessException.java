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
package com.chemistry.enotebook.registration.uniqueness;

/*
 * Created on May 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
/**
 * 
 * 
 *         TODO Add Class Information
 */
public class UcAccessException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3264512345698381336L;

	public UcAccessException(String msg) {
		super(msg);
	}

	public UcAccessException(Exception e) {
		super(e);
	}

	public UcAccessException(String msg, Exception e) {
		super(msg, e);
	}
}