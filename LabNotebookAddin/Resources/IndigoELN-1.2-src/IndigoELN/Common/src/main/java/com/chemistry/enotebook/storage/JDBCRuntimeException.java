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
package com.chemistry.enotebook.storage;

/*
 * This Class defines a Runtime exception class for the insert/update database
 * calls to throw back to the caller. This runtime exception is mandatory for
 * Spring Transaction Manager to determine if rollback is necessary. On seeing 
 * this Exception Transaction would be ROLLED BACK.
 */
public class JDBCRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 261884301923790322L;

	/*
	 * Constructs a new JDBC runtime exception with null as its detail message.
	 */
	public JDBCRuntimeException() {
		super();
	}

	/*
	 * Constructs a new runtime exception with the specified detail message.
	 */
	public JDBCRuntimeException(String message) {
		super(message);
	}

	/*
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.
	 */
	public JDBCRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/*
	 * Constructs a new runtime exception with the specified cause and a detail
	 * message of (cause==null ? null : cause.toString()) (which typically
	 * contains the class and detail message of cause).
	 */
	public JDBCRuntimeException(Throwable cause) {
		super(cause);
	}
}
