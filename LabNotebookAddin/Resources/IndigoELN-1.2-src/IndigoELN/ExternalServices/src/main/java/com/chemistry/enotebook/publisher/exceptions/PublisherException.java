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
package com.chemistry.enotebook.publisher.exceptions;

public class PublisherException extends Exception {

	private static final long serialVersionUID = 8788464793461237733L;

	public PublisherException() {
		super();
	}

	public PublisherException(String message, Throwable cause) {
		super(message, cause);
	}

	public PublisherException(String message) {
		super(message);
	}

	public PublisherException(Throwable cause) {
		super(cause);
	}
}