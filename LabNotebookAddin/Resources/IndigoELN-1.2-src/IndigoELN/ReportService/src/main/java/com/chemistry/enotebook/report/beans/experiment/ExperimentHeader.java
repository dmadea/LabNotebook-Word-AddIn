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
package com.chemistry.enotebook.report.beans.experiment;

import com.chemistry.enotebook.report.utils.TextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExperimentHeader {
	private static final Log log = LogFactory.getLog(ExperimentHeader.class);
	
	private String author = "";
	private String site = "";
	private String subject = "";
	private String notebookExperiment = "";
	private String status = "";
	private String notebookRef = "";
	
	
	public ExperimentHeader() {
	}

	public ExperimentHeader(String author, String site, String subject,
			String notebookExperiment, String status, String notebookRef) {
		super();
		this.author = author;
		this.site = site;
		this.subject = subject;
		this.notebookExperiment = notebookExperiment;
		this.status = status;
		this.notebookRef = notebookRef;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getNotebookExperiment() {
		return notebookExperiment;
	}
	public void setNotebookExperiment(String notebookExperiment) {
		this.notebookExperiment = notebookExperiment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNotebookRef() {
		return notebookRef;
	}
	public void setNotebookRef(String notebookRef) {
		this.notebookRef = notebookRef;
	}

	public String toXml() {
		StringBuffer buff = new StringBuffer("<experimentHeader>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</experimentHeader>");
		log.debug(buff.toString());
		return buff.toString();
	}

}
