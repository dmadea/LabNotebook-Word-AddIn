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
 * Created on Dec 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage;

import java.io.Serializable;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class ValidationInfo implements Serializable {
	static final long serialVersionUID = 468016960898766498L;

	public String siteCode = null;
	public String notebook = null;
	public String experiment = null;
	public String status = null;
	public String creator = null;
	public String owner = null;
	public String pageVersion = null;
	public boolean latestVersion = false;

	public boolean multipleResultsFlag = false;
}
