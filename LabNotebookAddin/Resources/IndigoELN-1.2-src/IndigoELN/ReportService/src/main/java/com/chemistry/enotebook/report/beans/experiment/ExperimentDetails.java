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

public class ExperimentDetails {
	private static final Log log = LogFactory.getLog(ExperimentDetails.class);
	
	private String batchCreator = "";
	private String batchOwner = "";
	private String designSite = "";
	private String therapeuticArea = "";
	private String projectCode = "";
	private String projectAlias = "";
	private String continuedFrom = "";
	private String continuedTo = "";
	private String compoundOwner = "";
	private String literatureReference = "";
	private String creationDate = "";
	private String spId = "";
	private String vrxId = "";
	private String seriesId = "";
	private String protocolId = "";
	private String designSumbitter = "";
	private String conceptKeywords = "";
	private String inventors = "";
	
	public String getTherapeuticArea() {
		return therapeuticArea;
	}
	public void setTherapeuticArea(String therapeuticArea) {
		this.therapeuticArea = therapeuticArea;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getProjectAlias() {
		return projectAlias;
	}
	public void setProjectAlias(String projectAlias) {
		this.projectAlias = projectAlias;
	}
	public String getContinuedFrom() {
		return continuedFrom;
	}
	public void setContinuedFrom(String continuedFrom) {
		this.continuedFrom = continuedFrom;
	}
	public String getContinuedTo() {
		return continuedTo;
	}
	public void setContinuedTo(String continuedTo) {
		this.continuedTo = continuedTo;
	}
	public String getCompoundOwner() {
		return compoundOwner;
	}
	public void setCompoundOwner(String compoundOwner) {
		this.compoundOwner = compoundOwner;
	}
	public String getLiteratureReference() {
		return literatureReference;
	}
	public void setLiteratureReference(String literatureReference) {
		this.literatureReference = literatureReference;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getVrxId() {
		return vrxId;
	}
	public void setVrxId(String vrxId) {
		this.vrxId = vrxId;
	}
	public String getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
	public String getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}
	public String getDesignSumbitter() {
		return designSumbitter;
	}
	public void setDesignSumbitter(String designSumbitter) {
		this.designSumbitter = designSumbitter;
	}
	
	public String getBatchCreator() {
		return batchCreator;
	}
	public void setBatchCreator(String batchCreator) {
		this.batchCreator = batchCreator;
	}
	public String getBatchOwner() {
		return batchOwner;
	}
	public void setBatchOwner(String batchOwner) {
		this.batchOwner = batchOwner;
	}
	public String getDesignSite() {
		return designSite;
	}
	public void setDesignSite(String designSite) {
		this.designSite = designSite;
	}
	public String getSpId() {
		return spId;
	}
	public void setSpId(String spId) {
		this.spId = spId;
	}
	public String getConceptKeywords() {
		return conceptKeywords;
	}
	public void setConceptKeywords(String conceptKeywords) {
		this.conceptKeywords = conceptKeywords;
	}
	public String getInventors() {
		return inventors;
	}
	public void setInventors(String inventors) {
		this.inventors = inventors;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<experimentDetails>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</experimentDetails>");
		log.debug(buff.toString());
		return buff.toString();
	}

}
