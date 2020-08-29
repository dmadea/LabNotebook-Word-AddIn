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
 * Created on 10-Feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.chloracnegen.classes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Structure implements Serializable {

	private static final long serialVersionUID = 7526472295622776147L;

	private String mol = "";
	private Map<String, String> results = new HashMap<String, String>();
	private boolean chloracnegenicStructure = false;
	private String resultString = "";

	public Structure(String mol) {
		setMol(mol);
	}

	public void addResult(String pName, String result) {
		results.put(pName, result);
	}

	public boolean isChloracnegenicStructure() {
		return chloracnegenicStructure;
	}

	public void setChloracnegenicStructure(boolean chloracnegenicStructure) {
		this.chloracnegenicStructure = chloracnegenicStructure;
	}

	public String getResults() {
		return resultString;
	}

	public void setResults(String resultString) {
		this.resultString = resultString;
	}

	public void setMol(String mol) {
		this.mol = mol;
	}

	public String getMol() {
		return this.mol;
	}
}
