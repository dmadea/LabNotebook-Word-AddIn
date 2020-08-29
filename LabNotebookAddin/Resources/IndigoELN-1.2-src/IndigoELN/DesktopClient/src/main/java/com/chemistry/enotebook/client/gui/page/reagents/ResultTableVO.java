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

import com.chemistry.enotebook.utils.jtable.SortableVO;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ResultTableVO implements SortableVO {
	private String dbName = "";
	private String reagentName = "";
	private String molWeight = "";
	private String molFormula = "";
	private String reagentType = "";
	private Element propertyElements = null;

	public ResultTableVO() {
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dName) {
		dbName = dName;
	}

	public String getReagentName() {
		return reagentName;
	}

	public void setReagentName(String cName) {
		reagentName = cName;
	}

	public String getMolWeight() {
		return molWeight;
	}

	public void setMolWeight(String cdName) {
		molWeight = cdName;
	}

	public String getMolFormula() {
		return molFormula;
	}

	public void setMolFormula(String sCriteria) {
		molFormula = sCriteria;
	}

	public Element getPropertyElements() {
		return propertyElements;
	}

	public void setPropertyElements(Element props) {
		propertyElements = props;
	}

	public String getReagentType() {
		return reagentType;
	}

	public void setReagentType(String reagentType) {
		this.reagentType = reagentType;
	}

	// Methods required to implement Sortable VO
	public int getNumColumns() {
		return 5;
	}

	public Object getValueAt(int index) {
		switch (index) {
			case 0:
				return reagentName;
			case 1:
				return StringUtils.isBlank(molWeight) ? null : new Double(molWeight);
			case 2:
				return molFormula;
			case 3:
				return reagentType;
			case 4:
				return dbName;
		}
		return "";
	}
}
