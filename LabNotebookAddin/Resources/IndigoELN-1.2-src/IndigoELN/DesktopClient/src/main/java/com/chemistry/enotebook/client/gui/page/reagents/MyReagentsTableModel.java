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
import com.chemistry.enotebook.utils.jtable.DefaultSortTableModel;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class MyReagentsTableModel extends DefaultSortTableModel {
	
	private static final long serialVersionUID = -4260048536016698576L;
	
	public static final String REAGENT_NAME = "Reagent Name";
	public static final String MOLECULAR_FORMULA = "Molecular Formula";
	public static final String DISPLAY_MOLECULAR_WEIGHT = "Mol. Wt.";
	public static final String MOLECULAR_WEIGHT = "Molecular Weight";
	public static final String REAGENT_TYPE = "Reagent Type";
	private String myReagentsList = null; // Original String from Database
	// containing unsorted reagent list
	private Vector<ResultTableVO> resultTableVOList = new Vector<ResultTableVO>(); // List of reagents

	public int getRowCount() {
		return (resultTableVOList == null) ? 0 : resultTableVOList.size();
	}

	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return MyReagentsTableModel.REAGENT_NAME;
			case 1:
				return MyReagentsTableModel.DISPLAY_MOLECULAR_WEIGHT;
			case 2:
				return MyReagentsTableModel.MOLECULAR_FORMULA;
			case 3:
				return MyReagentsTableModel.REAGENT_TYPE;
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
				return row.getReagentType();
		}
		return "";
	}

	public String getMyReagentsList() {
		return myReagentsList;
	}

	public void setMyReagentsList(String rList) {
		myReagentsList = rList;
		resetModelData();
	}

	private synchronized void resetModelData() {
		resultTableVOList.clear();
		try {
			if (myReagentsList != null) {
				StringReader reader = new StringReader(myReagentsList);
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(reader);
				Element root = doc.getRootElement();
				List<Element> reagentsList = XPath.selectNodes(root, "/Reagents/Reagent/Fields");
				for (int i = 0; i < reagentsList.size(); i++) {
					Element fieldsElement = reagentsList.get(i);
					ResultTableVO row = new ResultTableVO();
					row.setPropertyElements(fieldsElement);
					updateResultTableSummary(row);
					// Add to tree map so that it gets sorted
					resultTableVOList.add(row);
				}
				// Sort initially by Reagent name
				sortColumn(0, true);
			}
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void updateModelData(int rowIndex, Element updateFieldsElement) {
		ResultTableVO row = (ResultTableVO) resultTableVOList.get(rowIndex);
		try {
			row.setPropertyElements(updateFieldsElement);
			updateResultTableSummary(row);
			fireTableDataChanged();
		} catch (JDOMException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void addModelData(Element newReagentFieldsElement) {
		try {
			ResultTableVO row = new ResultTableVO();
			row.setPropertyElements(newReagentFieldsElement);
			updateResultTableSummary(row);
			resultTableVOList.add(0, row);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public boolean checkIfReagentExist(String rgtName) {
		boolean isExist = false;
		if (rgtName != null && rgtName.length() > 0) {
			for (int j = 0; j < resultTableVOList.size() && !isExist; j++) {
				ResultTableVO row = (ResultTableVO) resultTableVOList.get(j);
				if (rgtName.trim().equalsIgnoreCase(row.getReagentName().trim()))
					isExist = true;
			}
		}
		return isExist;
	}

	private void updateResultTableSummary(ResultTableVO row) throws JDOMException {
		Element fields = row.getPropertyElements();
		Element formulaElement = (Element) XPath
				.selectSingleNode(fields, "child::Field[@Display_Name='" + MOLECULAR_FORMULA + "']");
		row.setMolFormula((formulaElement == null) ? "" : formulaElement.getText());
		Element weightElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='" + MOLECULAR_WEIGHT + "']");
		row.setMolWeight((weightElement == null) ? "" : weightElement.getText());
		Element reagentNameElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='" + REAGENT_NAME + "']");
		row.setReagentName((reagentNameElement == null) ? "" : reagentNameElement.getTextTrim());
		Element reagentTypeElement = (Element) XPath.selectSingleNode(fields, "child::Field[@Display_Name='" + REAGENT_TYPE + "']");
		String reagentType = (reagentTypeElement == null) ? "" : reagentTypeElement.getTextTrim();
		if (StringUtils.equals(reagentType, BatchType.ACTUAL_PRODUCT.toString()))
			reagentType = BatchType.REACTANT.toString();
		row.setReagentType(reagentType);
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

	public String buildMyReagents() {
		try {
			Element rootElemnt = new Element("Reagents");
			for (int i = 0; i < resultTableVOList.size(); i++) {
				ResultTableVO row = (ResultTableVO) resultTableVOList.get(i);
				Element fieldsElement = row.getPropertyElements();
				fieldsElement.detach();
				Element reagentElemnt = new Element("Reagent");
				reagentElemnt.addContent(fieldsElement);
				rootElemnt.addContent(reagentElemnt);
			}
			Document doc = new Document(rootElemnt);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLOutputter outputter = new XMLOutputter();
			outputter.output(doc, out);
			out.flush();
			return out.toString();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		return null;
	}

	public Element getSelectedReagentInfo(int selectedIndex) {
		return resultTableVOList.get(selectedIndex).getPropertyElements();
	}

	public void deleteSelectedReagent(int selectedIndex) {
		resultTableVOList.remove(selectedIndex);
		fireTableDataChanged();
	}

	// Required for supporting Sortable Table Models
	public Vector<ResultTableVO> getDataVector() {
		return resultTableVOList;//new Vector(resultTableVOList);
	}
}