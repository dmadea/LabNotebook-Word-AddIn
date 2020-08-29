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
package com.common.chemistry.codetable.delegate;

public class CodeTableAccessException extends java.lang.Exception {

	private static final long serialVersionUID = 8813654561453251055L;

	public CodeTableAccessException() {
		super();
	}

	public CodeTableAccessException(Exception e) {
		super(e);
	}

	public CodeTableAccessException(String msg) {
		super(msg);
	}

	public CodeTableAccessException(String msg, Exception e) {
		super(msg, e);
	}
}
