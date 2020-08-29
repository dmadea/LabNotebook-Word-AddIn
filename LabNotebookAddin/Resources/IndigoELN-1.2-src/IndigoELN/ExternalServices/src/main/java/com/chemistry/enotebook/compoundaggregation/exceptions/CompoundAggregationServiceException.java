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
package com.chemistry.enotebook.compoundaggregation.exceptions;

public class CompoundAggregationServiceException extends Exception {

	private static final long serialVersionUID = -3466760154244290712L;

	public CompoundAggregationServiceException() {
		super();
	}

	public CompoundAggregationServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CompoundAggregationServiceException(String arg0) {
		super(arg0);
	}

	public CompoundAggregationServiceException(Throwable arg0) {
		super(arg0);
	}
}
