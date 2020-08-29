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
package com.chemistry.enotebook.design.delegate;

public class DesignServiceException extends Exception {

	private static final long serialVersionUID = -8449960397828676239L;

	public DesignServiceException() {
		super();
	}

	public DesignServiceException(String string) {
		super(string);
	}

	public DesignServiceException(Throwable throwable) {
		super(throwable);
	}

	public DesignServiceException(String string, Throwable throwable) {
		super(string, throwable);
	}
}
