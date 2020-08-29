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
 * LoginException.java
 * 
 * Created on Aug 5, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.user;

/**
 * 
 * @date Aug 5, 2004
 */
public class LoginException extends Exception {
	static final long serialVersionUID = -8298128367558304005L;

	public LoginException() {
		super();
	}

	public LoginException(String msg) {
		super(msg);
	}

	public LoginException(String msg, Exception e) {
		super(msg, e);
	}
}
