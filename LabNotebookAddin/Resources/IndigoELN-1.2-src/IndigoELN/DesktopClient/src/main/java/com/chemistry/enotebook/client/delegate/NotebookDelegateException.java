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
 * NotebookDelegateException.java
 * 
 * Created on Aug 10, 2004
 *
 * 
 */
package com.chemistry.enotebook.client.delegate;

/**
 * 
 * @date Aug 10, 2004
 */
public class NotebookDelegateException extends Exception {

	private static final long serialVersionUID = -7645857013763839718L;

	public NotebookDelegateException() {
		super();
	}

	public NotebookDelegateException(String msg) {
		super(msg);
	}

	public NotebookDelegateException(String msg, Exception e) {
		super(msg, e);
	}
}
