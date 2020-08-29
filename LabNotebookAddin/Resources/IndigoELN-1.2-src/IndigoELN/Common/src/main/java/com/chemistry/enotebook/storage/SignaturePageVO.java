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
/**
 * 
 */
package com.chemistry.enotebook.storage;

import java.io.Serializable;

/**
 * 
 * 
 * Value Object used to pass information on experiments that are in the state of Signing or Archiving.
 */
public class SignaturePageVO implements Serializable {
	static final long serialVersionUID = 9132003017802454578L;

	private String siteCode;
	private String notebook;
	private String experiment;
	private int version;
	private String status;
	private String ussiKey;
	private String userName;
	private String pageKey;

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 * @return Returns the notebook.
	 */
	public String getNotebook() {
		return notebook;
	}

	/**
	 * @param notebook
	 *            The notebook to set.
	 */
	public void setNotebook(String notebook) {
		this.notebook = notebook;
	}

	/**
	 * @return Returns the Experiment.
	 */
	public String getExperiment() {
		return experiment;
	}

	/**
	 * @param page
	 *            The Experiment to set.
	 */
	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Returns the version.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public String getUssiKey() {
		return ussiKey;
	}

	public void setUssiKey(String ussiKey) {
		this.ussiKey = ussiKey;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPageKey() {
		return pageKey;
	}

	public void setPageKey(String pageKey) {
		this.pageKey = pageKey;
	}
}
