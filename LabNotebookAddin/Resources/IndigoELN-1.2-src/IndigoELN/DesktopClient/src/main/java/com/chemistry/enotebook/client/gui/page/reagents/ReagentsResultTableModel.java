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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.utils.jtable.DefaultSortTableModel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.StringReader;
import java.util.List;
import java.util.Vector;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ReagentsResultTableModel extends DefaultSortTableModel {//AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3973445484505015298L;
	public static final String REAGENT_NAME = "Reagent Name";
	public static final String MOLECULAR_FORMULA = "Molecular Formula";
	public static final String FOUND_IN_DATABASE = "Found in Database";
	public static final String DISPLAY_MOLECULAR_WEIGHT = "Mol. Wt.";
	public static final String MOLECULAR_WEIGHT = "Molecular Weight";
	public static final String DATABASE_NAME = "Database_Name";
	private String reagentsList = null;
	private Vector resultTableVOList = new Vector();

	public int getRowCount() {
		return resultTableVOList != null ? resultTableVOList.size() : 0;
	}

	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return ReagentsResultTableModel.REAGENT_NAME;
			case 1:
				return ReagentsResultTableModel.DISPLAY_MOLECULAR_WEIGHT;
			case 2:
				return ReagentsResultTableModel.MOLECULAR_FORMULA;
			case 3:
				return ReagentsResultTableModel.FOUND_IN_DATABASE;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount())
			return "";
		ResultTableVO row = (ResultTableVO) resultTableVOList.get(nRow);
		switch (nCol) {
			case 0:
				return row.getReagentName();
			case 1:
				return row.getMolWeight();
			case 2:
				return row.getMolFormula();
			case 3:
				return row.getDbName();
		}
		return "";
	}

	public String getReagentsList() {
		return reagentsList;
	}

	public void setReagentsList(String rList) {
		reagentsList = rList;
	}

	public synchronized void resetModelData(boolean isNewSearch) {
		if (isNewSearch)
			resultTableVOList.clear();
		try {
			StringReader reader = new StringReader(reagentsList.replaceAll("\1", "").replaceAll("\0", ""));
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(reader);
			Element root = doc.getRootElement();
			List reagentsList = XPath.selectNodes(root, "/Reagents/Reagent/Fields");
			for (int i = 0; i < reagentsList.size(); i++) {
				Element fieldsElement = (Element) reagentsList.get(i);
				ResultTableVO row = new ResultTableVO();
				row.setPropertyElements(fieldsElement);
				updateResultTableSummary(row);
				resultTableVOList.add(row);
			}
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	private void updateResultTableSummary(ResultTableVO row) throws JDOMException {
		Element fields = row.getPropertyElements();
		row.setDbName(fields.getAttributeValue("Database_Name"));
		Element formulaElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='"
				+ ReagentsResultTableModel.MOLECULAR_FORMULA + "']");
		row.setMolFormula((formulaElement == null) ? "" : formulaElement.getText());
		Element weightElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='"
				+ ReagentsResultTableModel.MOLECULAR_WEIGHT + "']");
		row.setMolWeight((weightElement == null) ? "" : weightElement.getText());
		Element reagentNameElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='"
				+ ReagentsResultTableModel.REAGENT_NAME + "']");
		row.setReagentName((reagentNameElement == null) ? "" : reagentNameElement.getTextTrim());
		// If there is no reagent name then display the registry #
		if (reagentNameElement == null || row.getReagentName().length() == 0) {
			Element regNumberElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='Internal Registry #']");
			if (regNumberElement != null)
				row.setReagentName(regNumberElement.getText());
			else {
				Element mfcdNumberElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='External #']");
				if (mfcdNumberElement != null)
					row.setReagentName(mfcdNumberElement.getText());
				else {
					Element casNumberElement = (Element) XPath.selectSingleNode(fields,
							"child::Field[@Display_Name='CAS Registry#']");
					if (casNumberElement != null)
						row.setReagentName(casNumberElement.getText());
					else {
						Element casNumberElement2 = (Element) XPath.selectSingleNode(fields,
								"child::Field[@Display_Name='CAS Number']");
						if (casNumberElement2 != null)
							row.setReagentName(casNumberElement2.getText());
					}
				}
			}
		}
	}

	// Required for supporting Sortable Table Models
	public Vector getDataVector() {
		return resultTableVOList;//new Vector(resultTableVOList);
	}

	public String getSelectedReagentName(int selectedIndex) {
		return ((ResultTableVO) resultTableVOList.get(selectedIndex)).getReagentName();
	}

	public Element getSelectedReagentInfo(int selectedIndex) {
		return ((ResultTableVO) resultTableVOList.get(selectedIndex)).getPropertyElements();
	}
}
