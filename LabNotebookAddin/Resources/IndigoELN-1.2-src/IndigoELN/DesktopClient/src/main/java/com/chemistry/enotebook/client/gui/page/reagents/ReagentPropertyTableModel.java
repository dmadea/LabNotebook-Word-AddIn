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
 * Created on Sep 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ReagentPropertyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4219044644695097412L;
	
	public static final String PROPERTY_NAME = "Property Name";
	public static final String PROPERTY_VALUE = "Property Value";
	
	private List<String> resultNameList = new ArrayList<String>();
	private List<String> resultValueList = new ArrayList<String>();
	
	private Element fieldsElement = null;
	
	public int getRowCount() {
		return resultNameList.size();
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return ReagentPropertyTableModel.PROPERTY_NAME;
			case 1:
				return ReagentPropertyTableModel.PROPERTY_VALUE;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		switch (nCol) {
			case 0:
				return resultNameList.get(nRow);
			case 1:
				return resultValueList.get(nRow);
		}
		return "";
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				resultNameList.add(rowIndex, (String) aValue);
			case 1:
				resultValueList.add(rowIndex, (String) aValue);
		}
	}

	public Element getFieldsElement() {
		return fieldsElement;
	}

	public void setFieldsElement(Element fElement) {
		fieldsElement = fElement;
		resetModelData();
	}

	public void clearModel() {
		resultNameList.clear();
		resultValueList.clear();
		fireTableDataChanged();
	}

	private void resetModelData() {
		try {
			resultNameList.clear();
			resultValueList.clear();
			
			List<Element> fieldList = XPath.selectNodes(fieldsElement, "child::Field");
			
			for (Element fieldElement : fieldList) {
				String displayName = fieldElement.getAttributeValue("Display_Name");
				
				if (!displayName.equals("Structure")) {
					resultNameList.add(displayName);
					resultValueList.add(fieldElement.getText());
				}
			}
			
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public String getDBName() {
		String value = fieldsElement.getAttributeValue("Database_Name");
		return (value == null) ? "" : value;
	}

	public List<String> getResultNameList() {
		return resultNameList;
	}

	public List<String> getResultValueList() {
		return resultValueList;
	}
}
