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
 * Created by IntelliJ IDEA.
 * User: ITO01
 * Date: Jul 30, 2002
 * Time: 7:51:06 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.global.dri.compoundregistration.services.structure;

import java.io.PrintStream;
import java.io.PrintWriter;

public class StructureUCException extends Exception {

	private static final long serialVersionUID = -1727746592953698086L;
	
	private Throwable previousThrowable = null;

	public StructureUCException() {
		super();
	}

	public StructureUCException(String msg) {
		super(msg);
	}

	public StructureUCException(Throwable throwable) {
		setPreviousThrowable(throwable);
	}

	public StructureUCException(String msg, Throwable throwable) {
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

}
