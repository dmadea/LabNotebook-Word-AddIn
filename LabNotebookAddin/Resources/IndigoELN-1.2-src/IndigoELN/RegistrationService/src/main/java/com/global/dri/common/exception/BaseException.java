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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2331975698919317671L;

	/**
	 * By giving <code>BaseException</code> a reference to a Throwable object,
	 * exception chaining can be enforced easily.
	 */
	private Throwable previousThrowable = null;

	/**
	 * By giving <code> key </code> we can generate specific language massages
	 */
	private String key = null;

	public BaseException() {
		super();
	}

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(Throwable throwable) {
		setPreviousThrowable(throwable);
	}

	public BaseException(String msg, Throwable throwable) {
		super(msg);
		setPreviousThrowable(throwable);
	}

	/**
	 * @return Throwable - original cause of exception
	 */
	public Throwable getPreviousThrowable() {
		return this.previousThrowable;
	}

	protected void setPreviousThrowable(Throwable throwable) {

		if (throwable == null) {
			this.previousThrowable = new Throwable();
		} else {
			this.previousThrowable = throwable;
		}
	}

	/**
	 * Prints stack trace for itself and root-cause of exception.
	 */
	public void printStackTrace() {
		super.printStackTrace();

		if (this.previousThrowable != null) {
			this.previousThrowable.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);

		if (this.previousThrowable != null) {
			this.previousThrowable.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);

		if (this.previousThrowable != null) {
			this.previousThrowable.printStackTrace(s);
		}
	}

	public String getPrintStackTrace() {
		if (this.previousThrowable != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			this.previousThrowable.printStackTrace(new PrintStream(
					byteArrayOutputStream));
			printStackTrace(new PrintStream(byteArrayOutputStream));

			return byteArrayOutputStream.toString();
		}

		return null;
	}

	/**
	 * Sets message key to be used in internationalization.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}