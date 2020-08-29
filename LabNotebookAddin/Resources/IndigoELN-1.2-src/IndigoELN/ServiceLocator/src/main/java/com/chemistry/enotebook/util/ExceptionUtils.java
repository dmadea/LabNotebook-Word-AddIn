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
 * Created on Jul 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ExceptionUtils {

	public static Throwable getRootCause(Throwable e) {
		Throwable lastError = e;

		if (lastError != null)
			while (lastError.getCause() != null)
				lastError = lastError.getCause();

		return lastError;
	}

	public static void throwRootCause(Throwable t) throws Exception {
		Throwable rootCause = getRootCause(t);

		if (rootCause != null)
			throw new Exception(rootCause);

		throw new Exception("Unknown exception!");
	}

	public static String getStackTrace(Throwable e) {
		String stackTrace = null;

		try {
			OutputStream out = new ByteArrayOutputStream();
			e.printStackTrace(new PrintWriter(out, true));
			stackTrace = out.toString();
		} catch (Exception f) {
			// Do nothing. We're really broken now!
		}

		return stackTrace;
	}
}
