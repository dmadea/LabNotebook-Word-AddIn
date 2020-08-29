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

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarContext implements StorageContextInterface, Serializable {
    static final long serialVersionUID = 2719462597395972418L;

	public final int SITECODE = 1;
	public final int USERNAME = 2;
	public final int NOTEBOOK = 3;
	public final int EXPERIMENT = 4;
	public final int VERSION = 5;

	private String SiteCode = null;
	private String UserName = null;
	private String Notebook = null;
	private String Experiment = null;

	private String ExpRangeStart = null;
	private String ExpRangeEnd = null;

	private int Version = 0;
	private boolean includeOlderVersions = false;
	private boolean includeAllUsers = false;

	private int[] SortOrder = null;

	private StorageVO results = null;

	public SpeedBarContext() {
	}

	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return SiteCode;
	}

	/**
	 * @param siteCode
	 *            The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		SiteCode = siteCode;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * @param userName
	 *            The userName to set.
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}

	/**
	 * @return Returns the notebook.
	 */
	public String getNotebook() {
		return Notebook;
	}

	/**
	 * @param notebook
	 *            The notebook to set.
	 */
	public void setNotebook(String notebook) {
		Notebook = notebook;
	}

	/**
	 * @return Returns the Experiment.
	 */
	public String getExperiment() {
		return Experiment;
	}

	/**
	 * @param exp
	 *            The notebook Experiment to set.
	 */
	public void setExperiment(String exp) {
		Experiment = exp;
	}

	public String getExperimentRangeStart() {
		return ExpRangeStart;
	}

	public String getExperimentRangeEnd() {
		return ExpRangeEnd;
	}

	public void setExperimentRange(String start, String end) {
		if (start != null)
			ExpRangeStart = NotebookPageUtil.formatNotebookPage(start);
		else
			ExpRangeStart = null;

		if (end != null)
			ExpRangeEnd = NotebookPageUtil.formatNotebookPage(end);
		else
			ExpRangeEnd = null;
	}

	public void setExperimentRange(int start, int end) {
		setExperimentRange((start == 0) ? "" : "" + start, (end == 0) ? "" : "" + end);
	}

	/**
	 * @return Returns the Version of the Experiment.
	 */
	public int getVersion() {
		return Version;
	}

	/**
	 * @param ver
	 *            The Notebook Experiment Version to set.
	 */
	public void setVersion(int ver) {
		Version = ver;
	}

	/**
	 * @return Returns whether older versions will be included in results.
	 */
	public boolean includeAllUsers() { return includeAllUsers; }
	
	/**
	 * @param flag The value to set for IncludeOlderVersions.
	 */
	public void setIncludeAllUsers(boolean flag) {
		includeAllUsers = flag;
	}

	/**
	 * @return Returns whether older versions will be included in results.
	 */
	public boolean includeOlderVersions() {
		return includeOlderVersions;
	}

	/**
	 * @param flag
	 *            The value to set for IncludeOlderVersions.
	 */
	public void setIncludeOlderVersions(boolean flag) {
		includeOlderVersions = flag;
	}

	/**
	 * @return Returns the sortOrder.
	 */
	public int[] getSortOrder() {
		return SortOrder;
	}

	/**
	 * @param sortOrder
	 *            The sortOrder to set.
	 */
	public void setSortOrder(int[] sortOrder) {
		SortOrder = sortOrder;
	}

	public String getXml() {
		StringBuffer xml = new StringBuffer();

		xml.append("<StorageContext>\n" + "\t<SpeedBar>\n" + "\t\t<Site>" + getSiteCode() + "</Site>\n" + "\t\t<UserName>"
				+ getUserName() + "</UserName>\n" + "\t\t<Notebook>" + getNotebook() + "</Notebook>\n" + "\t\t<Experiment>"
				+ getExperiment() + "</Experiment>\n" + "\t\t<Version>" + getExperiment() + "</Version>\n");

		if (SortOrder!=null && SortOrder.length > 0) {
			xml.append("\t\t<SortOrder>\n");
			for (int i = 0; i < SortOrder.length; i++) {
				xml.append("\t\t\t<Field>");
				switch (SortOrder[i]) {
					case SITECODE:
						xml.append("SiteCode");
						break;
					case USERNAME:
						xml.append("UserName");
						break;
					case NOTEBOOK:
						xml.append("Notebook");
						break;
					case EXPERIMENT:
						xml.append("Experiment");
						break;
					case VERSION:
						xml.append("Version");
						break;
				}
				xml.append("</Field>");
			}
			xml.append("\t\t</SortOrder>\n");
		}

		xml.append("\t</SpeedBar>\n" + "</StorageContext>\n");

		return xml.toString();
	}

	public void setXml(String xml) {
		// System.out.println("SpeedBarContext::setXml: Not supported yet.");
	}

	public StorageVO getResults() {
		return results;
	}

	public void setResults(StorageVO vo) {
		this.results = vo;
	}
}
