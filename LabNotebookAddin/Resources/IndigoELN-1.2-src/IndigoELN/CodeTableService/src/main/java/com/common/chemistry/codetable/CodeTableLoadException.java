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

public class CodeTableLoadException extends java.lang.Exception {
	
	private static final long serialVersionUID = 8177091860250327040L;

	public CodeTableLoadException() {
		super();
	}

	public CodeTableLoadException(String msg) {
		super(msg);
	}

	public CodeTableLoadException(String msg, Exception e) {
		super(msg, e);
	}

	public CodeTableLoadException(String msg, Throwable t) {
		super(msg, t);
	}
}
