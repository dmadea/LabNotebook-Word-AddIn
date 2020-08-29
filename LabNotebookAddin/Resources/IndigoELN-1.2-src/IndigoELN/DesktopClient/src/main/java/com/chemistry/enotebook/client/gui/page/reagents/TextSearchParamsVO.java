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
 * Created on Sep 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.reagents;

import java.io.Serializable;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TextSearchParamsVO implements Serializable {
	static final long serialVersionUID = -9146895753016901645L;
	private String dbName = null;
	private String colName = null;
	private String colDisplayName = null;
	private String searchCriteria = null;
	private String searchValue = null;

	public TextSearchParamsVO() {
	}

	public String getDbName() {
		return this.dbName;
	}

	public String getColName() {
		return this.colName;
	}

	public String getColDisplayName() {
		return this.colDisplayName;
	}

	public String getSearchValue() {
		return this.searchValue;
	}

	public String getSearchCriteria() {
		return this.searchCriteria;
	}

	public void setSearchCriteria(String sCriteria) {
		this.searchCriteria = sCriteria;
	}

	public void setSearchValue(String sValue) {
		this.searchValue = sValue;
	}

	public void setDbName(String dName) {
		this.dbName = dName;
	}

	public void setColName(String cName) {
		this.colName = cName;
	}

	public void setColDisplayName(String cdName) {
		this.colDisplayName = cdName;
	}
}
