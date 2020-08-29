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
package com.global.dri.common.exception;

/**
 * 
 */
public class CorbaException extends BaseException {

	private static final long serialVersionUID = -1846889812935495100L;

	public CorbaException() {
		super();
	}

	public CorbaException(String msg) {
		super(msg);
	}

	public CorbaException(Throwable throwable) {
		super.setPreviousThrowable(throwable);
	}

	public CorbaException(String msg, Throwable throwable) {
		super(msg);
		super.setPreviousThrowable(throwable);
	}
}
