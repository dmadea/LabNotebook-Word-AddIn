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

import java.io.Serializable;

/**
 * 
 * 
 * Purpose: Used to pull back table of contents information for a given notebook or notebook experiment grouping
 * 
 * Use: Fill in at a minimum siteCode and Notebook for Notebook table of contents. Additionally start/stop experiment can be filled
 * in to narrow table of contents to a notebook experiment grouping.
 */
public class ContentsContext implements StorageContextInterface, Serializable {
	
	private static final long serialVersionUID = 7110527608126995109L;

	public ContentsContext() {
	}

	public ContentsContext(String xml) {
		this.setXml(xml);
	}

	private String siteCode_ = null;
	private String notebook_ = null;
	private int startExperiment_ = -1;
	private int stopExperiment_ = -1;

	private StorageVO[] results = null;

	public String getXml() {
		StringBuffer xml = new StringBuffer();

		xml.append("<ContentsContext>\n" + "\t<Site>" + getSiteCode() + "</Site>\n" + "\t<Notebook>" + getNotebook()
				+ "</Notebook>\n");

		if (getStartExperiment() != -1)
			xml.append("\t<StartExperiment>" + getStartExperiment() + "</StartExperiment>\n" + "\t<StopExperiment>"
					+ getStopExperiment() + "</StopExperiment>\n");

		xml.append("</ContentsContext>\n");

		return xml.toString();
	}

	public void setXml(String xml) {

		// System.out.println("ContentsContext::setXml: Not supported yet.");
	}

	public StorageVO[] getResults() {
		return results;
	}

	public void setResults(StorageVO[] vo) {
		this.results = vo;
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
	 * @return Returns the notebook_.
	 */
	public String getNotebook() {
		return notebook_;
	}

	/**
	 * @param notebook_
	 *            The notebook_ to set.
	 */
	public void setNotebook(String notebook) {
		this.notebook_ = notebook;
	}

	/**
	 * @return Returns the startExperiment_.
	 */
	public int getStartExperiment() {
		return startExperiment_;
	}

	/**
	 * @param startExperiment_
	 *            The startExperiment_ to set.
	 */
	public void setStartExperiment(int startExperiment) {
		this.startExperiment_ = startExperiment;
	}

	/**
	 * @return Returns the stopExperiment_.
	 */
	public int getStopExperiment() {
		return stopExperiment_;
	}

	/**
	 * @param stopExperiment
	 *            The stopExperiment_ to set.
	 */
	public void setStopExperiment(int stopExperiment) {
		this.stopExperiment_ = stopExperiment;
	}
}
