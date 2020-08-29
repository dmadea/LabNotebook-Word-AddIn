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
 * Created on Jul 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.session.delegate;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AuthenticationException extends java.lang.Exception {
	static final long serialVersionUID = 6876411176586490601L;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String msg) {
		super(msg);
	}

	public AuthenticationException(String msg, Exception e) {
		super(msg, e);
	}
}