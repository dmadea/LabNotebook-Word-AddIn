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
package com.chemistry.enotebook.vnv.exceptions;

public class VnvRuntimeException extends Exception {

	private static final long serialVersionUID = 6892186195207019140L;

	public VnvRuntimeException() {
		super();
	}

	public VnvRuntimeException(String message) {
		super(message);
	}

	public VnvRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public VnvRuntimeException(Throwable cause) {
		super(cause);
	}
}
