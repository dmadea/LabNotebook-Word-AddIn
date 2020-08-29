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
 * Created on May 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class StorageException extends Exception {

	private static final long serialVersionUID = -4100802719712701787L;

	public StorageException(String msg) {
		super(msg);
	}

	public StorageException(Throwable e) {
		super(e);
	}

	public StorageException(String msg, Throwable e) {
		super(msg, e);
	}
}