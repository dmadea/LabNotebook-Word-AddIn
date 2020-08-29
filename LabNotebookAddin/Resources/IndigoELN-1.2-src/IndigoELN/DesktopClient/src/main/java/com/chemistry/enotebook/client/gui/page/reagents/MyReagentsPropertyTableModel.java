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
 * Created on Sep 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import org.apache.commons.lang.StringUtils;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class MyReagentsPropertyTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -6596836105676623357L;
	
	public static final String PROPERTY_NAME = "Property Name";
	public static final String PROPERTY_VALUE = "Property Value";
	private ArrayList<String> resultNameList = new ArrayList<String>();
	private ArrayList<String> resultValueList = new ArrayList<String>();
	private Element fieldsElement = null;
	private int selectedRowOfMyReagent = -1;
	private MyReagentsTableModel myReagentsTableModel = new MyReagentsTableModel();
	private ArrayList<String> mandatoryFields = new ArrayList<String>();

	public int getRowCount() {
		return resultNameList.size();
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return MyReagentsPropertyTableModel.PROPERTY_NAME;
			case 1:
				return MyReagentsPropertyTableModel.PROPERTY_VALUE;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		switch (nCol) {
			case 0:
				return false;
			case 1:
				return true;
		}
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
			case 1:
				resultValueList.set(rowIndex, (String) aValue);
		}
		// update my reagents
		myReagentsTableModel.updateModelData(selectedRowOfMyReagent, updateFieldsElement());
	}

	public Element getFieldsElement() {
		return fieldsElement;
	}

	public void setFieldsElement(Element fElement) {
		fieldsElement = fElement;
		resetModelData();
	}

	public void clearModelData() {
		resultNameList.clear();
		resultValueList.clear();
		fieldsElement = null;
		selectedRowOfMyReagent = -1;
		fireTableDataChanged();
	}

	private void resetModelData() {
		try {
			resultNameList.clear();
			resultValueList.clear();
			HashMap<String, String> tList = new HashMap<String, String>();
			// Determine properties already stored
			List<Element> fieldList = XPath.selectNodes(fieldsElement, "child::Field");
			for (int j = 0; j < fieldList.size(); j++) {
				Element fieldElement = fieldList.get(j);
				if (fieldElement.getText().length() > 0) {
					String name = fieldElement.getAttributeValue("Display_Name");
					String value = fieldElement.getText();

					if (StringUtils.equals(name, "Reagent Type")
							&& StringUtils.equals(value, BatchType.ACTUAL_PRODUCT.toString())) {
						value = BatchType.REACTANT.toString();
					}
					
					if (!StringUtils.equals(name, "Structure")) {
						resultNameList.add(name);
						resultValueList.add(value);
						tList.put(name, value);
					}
				}
			}
			// Now bring in properties not stored and keep the desired order
			for(String name : mandatoryFields) {
				if (!tList.containsKey(name)) {
					// resultNameList.add(name);
					// resultValueList.add(tList.get(name));
					//					
					// tList.remove(name);
					// } else {
					resultNameList.add(name);
					resultValueList.add("");
				}
			}
			// // Add any additional ones to end
			// Iterator tListIterator = tList.keySet().iterator();
			// while (tListIterator.hasNext()) {
			// String name = (String)tListIterator.next();
			//				
			// resultNameList.add(name);
			// resultValueList.add(tList.get(name));
			// }
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	// setters and getters
	public ArrayList<String> getResultNameList() {
		return resultNameList;
	}

	public ArrayList<String> getResultValueList() {
		return resultValueList;
	}

	public int getSelectedRowOfMyReagent() { return selectedRowOfMyReagent; }
	public void setSelectedRowOfMyReagent(int selectedRow) {
		selectedRowOfMyReagent = selectedRow;
	}

	public void setMyReagentsTableModel(MyReagentsTableModel model) {
		myReagentsTableModel = model;
	}

	/**
	 * @param missingfieldsDBMap
	 *            The missingfieldsDBMap to set.
	 */
	public void setMandatoryFields(ArrayList<String> fields) {
		if (fields == null)
			fields = new ArrayList<String>();
		mandatoryFields = fields;
	}

	private Element updateFieldsElement() {
		Element fieldsElement = new Element("Fields");
		try {
			for (int i = 0; i < resultNameList.size(); i++) {
				Element fieldElemnt = new Element("Field");
				fieldElemnt.setAttribute("Display_Name", resultNameList.get(i));
				fieldElemnt.addContent(new CDATA(resultValueList.get(i)));
				fieldsElement.addContent(fieldElemnt);
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return fieldsElement;
	}
}