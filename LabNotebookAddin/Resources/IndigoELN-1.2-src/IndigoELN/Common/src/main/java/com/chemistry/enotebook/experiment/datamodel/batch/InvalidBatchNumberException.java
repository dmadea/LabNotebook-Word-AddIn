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
package com.chemistry.enotebook.experiment.datamodel.batch;

/**
 * 
 * @date Aug 9, 2004
 */
public class InvalidBatchNumberException extends Exception {
	
	private static final long serialVersionUID = 6919948095716586241L;

	public InvalidBatchNumberException() {
		super();
	}

	public InvalidBatchNumberException(String msg) {
		super(msg);
	}

	public InvalidBatchNumberException(String msg, Exception e) {
		super(msg, e);
	}

}
