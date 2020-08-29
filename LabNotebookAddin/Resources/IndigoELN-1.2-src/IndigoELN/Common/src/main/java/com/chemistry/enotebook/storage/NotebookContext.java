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
 * Created on May 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage;

import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * 
 * Purpose: Used to pull back information based on a single notebook page. Use SearchNotebooksContext to bring back multiple
 * notebook responses to handle search criteria.
 * 
 * Use: Fill in pageKey_ or notebookRef_ to fill notebookContext with appropriate notebook. Where clause of pageKey_ or notebookRef_
 * will isolate return to a single notebook object. pageKey_ is used to bring back previous versions of the notebook, and using
 * notebookRef_ will bring back current/most recent version of the page.
 */
public class NotebookContext implements StorageContextInterface, Serializable {
	static final long serialVersionUID = 7571474003546499759L;
	private String pageKey_ = null;
	private String notebookRef_ = null;
	private int version_ = 0;

	private String chemistUserID_ = null;
	private String ownerUserID_ = null;

	private String siteCode_ = null;

	private String status_ = null;
	// List of tables to fetch like CEN_PAGES,CEN_REACTION_STEPS,CEN_BATCHEs,CEN_ANALYSIS
	private LinkedHashMap tableList = new LinkedHashMap();
	private HashMap queryConstraints = new HashMap();
	private HashMap fieldExclusions = new HashMap();

	// Result data from each table requested as Oracle rowset
	private HashMap notebookMap_ = null;

	public NotebookContext() {
	}

	public NotebookContext(String xml) {
		this.setXml(xml);
	}

	public String getXml() {
		StringBuffer xml = new StringBuffer();

		xml.append("<StorageContext>\n" + "\t<NotebookPage>\n" + "\t\t<PageKey>" + getPageKey() + "</PageKey>\n" + "\t\t<Site>"
				+ getSiteCode() + "</Site>\n" + "\t\t<UserName>" + getChemistUserID() + "</UserName>\n" + "\t\t<Notebook>"
				+ getNotebookNumber() + "</Notebook>\n" + "\t\t<Experiment>" + getExperiment() + "</Experiment>\n"
				+ "\t\t<Version>" + getVersion() + "</Version>\n");
		xml.append("\t</NotebookPage>\n" + "</StorageContext>\n");

		return xml.toString();
	}

	public void setXml(String xml) {
		// System.out.println("NotebookContext::setXml: Not supported yet.");
	}

	public String getNotebookNumber() {
		return NotebookPageUtil.getNotebookNumberFromNotebookRef(notebookRef_);
	}

	public void setNotebookNumber(String notebookNumber) {
		String NotebookPage = NotebookPageUtil.getNotebookPageFromNotebookRef(notebookRef_);
		if (!NotebookPageUtil.isValidNotebookPage(NotebookPage))
			notebookRef_ = notebookNumber;
		else {
			String testNotebookRef = NotebookPageUtil.formatNotebookRef(notebookNumber + "-" + NotebookPage);
			if (NotebookPageUtil.isValidNotebookRef(testNotebookRef))
				notebookRef_ = testNotebookRef;
		}
	}

	public String getExperiment() {
		return NotebookPageUtil.getNotebookPageFromNotebookRef(notebookRef_);
	}

	public void setExperiment(String experiment) {
		// TODO: Divide Current Notebook Ref and reassemble with new page.
		// If the notebook Page is a number then set its value.
		String NotebookNum = NotebookPageUtil.getNotebookNumberFromNotebookRef(notebookRef_);
		if (NotebookPageUtil.isValidNotebookNumber(NotebookNum)) {
			String testNotebookRef = NotebookPageUtil.formatNotebookRef(NotebookNum + "-" + experiment);
			if (NotebookPageUtil.isValidNotebookRef(testNotebookRef))
				notebookRef_ = testNotebookRef;
		}
	}

	/**
	 * @return Returns the notebookRef.
	 */
	public String getNotebookRef() {
		return notebookRef_;
	}

	/**
	 * @param notebookRef
	 *            The notebookRef to set.
	 */
	public void setNotebookRef(String notebookRef) {
		String testNotebookRef = NotebookPageUtil.formatNotebookRef(notebookRef);
		if (NotebookPageUtil.isValidNotebookRef(testNotebookRef))
			notebookRef_ = testNotebookRef;
	}

	/**
	 * @return Returns the chemistUserID.
	 */
	public String getChemistUserID() {
		return chemistUserID_;
	}

	/**
	 * @param chemistUserID
	 *            The chemistUserID to set.
	 */
	public void setChemistUserID(String chemistUserID) {
		chemistUserID_ = chemistUserID;
	}

	/**
	 * @return Returns the ownerUserID.
	 */
	public String getOwnerUserID() {
		return ownerUserID_;
	}

	/**
	 * @param ownerUserID
	 *            The ownerUserID to set.
	 */
	public void setOwnerUserID(String ownerUserID) {
		ownerUserID_ = ownerUserID;
	}

	/**
	 * @return Returns the pageKey.
	 */
	public String getPageKey() {
		return pageKey_;
	}

	/**
	 * @param pageKey
	 *            The pageKey to set.
	 */
	public void setPageKey(String pageKey) {
		pageKey_ = pageKey;
	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status_;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(String status) {
		status_ = status;
	}

	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return siteCode_;
	}

	/**
	 * @param siteCode
	 *            The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		siteCode_ = siteCode;
	}

	/**
	 * @return Returns the version #.
	 */
	public int getVersion() {
		return version_;
	}

	/**
	 * @param ver
	 *            The version # to set.
	 */
	public void setVersion(int ver) {
		version_ = ver;
	}

	public void setNotebookMap(HashMap crsetMap) {
		this.notebookMap_ = crsetMap;
	}

	public HashMap getNotebookMap() {
		return this.notebookMap_;
	}

	public void addTableName(CeNTableName table) {
		// the table list is a hashmap of a table alias key and the
		// real table name - table alias key is used as the key in notebookMap
		addTableName(table.toString(), table);
	}

	public void addTableName(String tableAlias, CeNTableName table) {
		// the table list is a hashmap of a table alias key and the
		// real table name - table alias key is used as the key in notebookMap
		tableList.put(tableAlias, table.toString());
	}

	public LinkedHashMap getTableNames() {
		if (tableList.size() == 0) {
			List tables = CeNTableName.VALUES;
			for (int i = 0; i < tables.size(); i++) {
				CeNTableName table = (CeNTableName) tables.get(i);
				addTableName(table.toString(), table);
			}
		}
		return tableList;
	}

	public void addTableFieldExclusions(CeNTableName tableName, ArrayList fieldNames) {

		fieldExclusions.put(tableName.toString(), fieldNames);
	}

	public ArrayList getTableFieldExclusions(String table) {
		return (ArrayList) fieldExclusions.get(table);
	}

	public void addTableKeyConstraint(CeNTableName table, String keyValue) {
		queryConstraints.put(table.toString(), keyValue);
	}

	public String getTableKeyConstraint(String table) {
		return (String) queryConstraints.get(table);
	}

	public void setTableKeyConstraints(HashMap constraints) {
		this.queryConstraints = constraints;
	}
}
