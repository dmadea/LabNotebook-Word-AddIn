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
package com.chemistry.enotebook.vnv.classes;

/*
 * Created on Mar 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class UniquenessCheckVO implements Serializable {
	static final long serialVersionUID = 3501216174011546822L;

	private ArrayList<UcCompoundInfo> resultSet = null; // list of
														// UcCompoundInfo
	private String ucMessage = null;

	public UniquenessCheckVO() {
		resultSet = new ArrayList<UcCompoundInfo>();
		ucMessage = "";
	}

	public ArrayList<UcCompoundInfo> getResults() {
		return resultSet;
	}

	public void setResults(ArrayList<UcCompoundInfo> value) {
		resultSet = value;
	}

	public void addResult(UcCompoundInfo value) {
		resultSet.add(value);
	}

	public void clearResults() {
		resultSet.clear();
	}

	public String getMessage() {
		return ucMessage;
	}

	public void setMessage(String msg) {
		ucMessage = msg;
	}
}
