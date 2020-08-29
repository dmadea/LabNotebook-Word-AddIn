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
 * InvalidNotebookRefException.java
 * 
 * Created on Aug 9, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.page;

/**
 * 
 * @date Aug 9, 2004
 */
public class InvalidNotebookRefException extends Exception {
	
	private static final long serialVersionUID = 7381801111062220561L;

	public InvalidNotebookRefException() {
		super();
	}

	public InvalidNotebookRefException(String msg) {
		super(msg);
	}

	public InvalidNotebookRefException(String msg, Exception e) {
		super(msg, e);
	}

}
