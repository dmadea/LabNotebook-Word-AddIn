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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.utils.GUIDUtil;

public class AnalysisModel extends CeNAbstractModel {

	private static final long serialVersionUID = -239304940875763246L;
	
	private String annotation = "";
	private String cyberLabFileId = "";
	private String server = "";
	private String cyberLabUserId = "";
	private String cyberLabDomainId = "";
	private String cyberLabFolderId = "";
	private String cyberLabLCDFId = "";
	private String cenSampleRef = "";
	private String analyticalServiceSampleRef = "";
	private String groupId = "";
	private String userId = "";
	private String domain = "";
	private String experiment = "";
	private String experimentTime = "";
	private String site = "";
	private String siteCode = "";
	private String instrument = "";
	private String instrumentType = "";
	private String fileType = "";
	private String fileName = "";
	private long fileSize = 0;
	private long version = 0;
	private String url = "";
	private String comments = "";
	boolean isLinked = false;
	boolean isIPRelated = false;

	private byte[] file = null;

	public AnalysisModel(String key) {
		this.key = key;
	}

	public AnalysisModel() {
		this.key = GUIDUtil.generateGUID(this);
	}

	/**
	 * @return the file
	 */
	public byte[] getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(byte[] file) {
		this.file = file;
		this.modelChanged = true;
	}

	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation
	 *            the annotation to set
	 */
	public void setAnnotation(String annotation) {
		if (annotation == null) {
			annotation = "";
		}
		if (!this.annotation.equals(annotation)) {
			this.annotation = annotation;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the cenSampleRef
	 */
	public String getCenSampleRef() {
		return cenSampleRef;
	}

	/**
	 * @param cenSampleRef
	 *            the cenSampleRef to set
	 */
	public void setCenSampleRef(String cenSampleRef) {

		if (cenSampleRef == null) {
			cenSampleRef = "";
		}
		if (!this.cenSampleRef.equals(cenSampleRef)) {
			this.cenSampleRef = cenSampleRef;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {

		if (comments == null) {
			comments = "";
		}
		if (!this.comments.equals(comments)) {
			this.comments = comments;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the cyberLabDomainId
	 */
	public String getCyberLabDomainId() {
		return cyberLabDomainId;
	}

	/**
	 * @param cyberLabDomainId
	 *            the cyberLabDomainId to set
	 */
	public void setCyberLabDomainId(String cyberLabDomainId) {
		if (cyberLabDomainId == null) {
			cyberLabDomainId = "";
		}
		if (!this.cyberLabDomainId.equals(cyberLabDomainId)) {
			this.cyberLabDomainId = cyberLabDomainId;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the cyberLabFileId
	 */
	public String getCyberLabFileId() {
		return cyberLabFileId;
	}

	/**
	 * @param cyberLabFileId
	 *            the cyberLabFileId to set
	 */
	public void setCyberLabFileId(String cyberLabFileId) {
		if (cyberLabFileId == null) {
			cyberLabFileId = "";
		}
		if (!this.cyberLabFileId.equals(cyberLabFileId)) {
			this.cyberLabFileId = cyberLabFileId;
			this.modelChanged = true;
		}

	}

	/**
	 * @return the cyberLabFolderId
	 */
	public String getCyberLabFolderId() {
		return cyberLabFolderId;
	}

	/**
	 * @param cyberLabFolderId
	 *            the cyberLabFolderId to set
	 */
	public void setCyberLabFolderId(String cyberLabFolderId) {
		if (cyberLabFolderId == null) {
			cyberLabFolderId = "";
		}
		if (!this.cyberLabFolderId.equals(cyberLabFolderId)) {
			this.cyberLabFolderId = cyberLabFolderId;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the cyberLabLCDFId
	 */
	public String getCyberLabLCDFId() {
		return cyberLabLCDFId;
	}

	/**
	 * @param cyberLabLCDFId
	 *            the cyberLabLCDFId to set
	 */
	public void setCyberLabLCDFId(String cyberLabLCDFId) {
		if (cyberLabLCDFId == null) {
			cyberLabLCDFId = "";
		}
		if (!this.cyberLabLCDFId.equals(cyberLabLCDFId)) {
			this.cyberLabLCDFId = cyberLabLCDFId;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the cyberLabUserId
	 */
	public String getCyberLabUserId() {
		return cyberLabUserId;
	}

	/**
	 * @param cyberLabUserId
	 *            the cyberLabUserId to set
	 */
	public void setCyberLabUserId(String cyberLabUserId) {
		if (cyberLabUserId == null) {
			cyberLabUserId = "";
		}
		if (!this.cyberLabUserId.equals(cyberLabUserId)) {
			this.cyberLabUserId = cyberLabUserId;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain
	 *            the domain to set
	 */
	public void setDomain(String domain) {
		if (domain == null) {
			domain = "";
		}
		if (!this.domain.equals(domain)) {
			this.domain = domain;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the experiment
	 */
	public String getExperiment() {
		return experiment;
	}

	/**
	 * @param experiment
	 *            the experiment to set
	 */
	public void setExperiment(String experiment) {
		if (experiment == null) {
			experiment = "";
		}
		if (!this.experiment.equals(experiment)) {
			this.experiment = experiment;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the experimentTime
	 */
	public String getExperimentTime() {
		return experimentTime;
	}

	/**
	 * @param experimentTime
	 *            the experimentTime to set
	 */
	public void setExperimentTime(String experimentTime) {
		if (experimentTime == null) {
			experimentTime = "";
		}
		if (!this.experimentTime.equals(experimentTime)) {
			this.experimentTime = experimentTime;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		if (fileName == null) {
			fileName = "";
		}
		if (!this.fileName.equals(fileName)) {
			this.fileName = fileName;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize) {

		if (this.fileSize != fileSize) {
			this.fileSize = fileSize;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		if (fileType == null) {
			fileType = "";
		}
		if (!this.fileType.equals(fileType)) {
			this.fileType = fileType;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the analyticalServiceSampleRef
	 */
	public String getAnalyticalServiceSampleRef() {
		return analyticalServiceSampleRef;
	}

	/**
	 * @param analyticalServiceSampleRef
	 *            the analyticalServiceSampleRef to set
	 */
	public void setAnalyticalServiceSampleRef(String analyticalServiceSampleRef) {
		if (analyticalServiceSampleRef == null) {
			analyticalServiceSampleRef = "";
		}
		if (!this.analyticalServiceSampleRef.equals(analyticalServiceSampleRef)) {
			this.analyticalServiceSampleRef = analyticalServiceSampleRef;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		if (groupId == null) {
			groupId = "";
		}
		if (!this.groupId.equals(groupId)) {
			this.groupId = groupId;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the instrument
	 */
	public String getInstrument() {
		return instrument;
	}

	/**
	 * @param instrument
	 *            the instrument to set
	 */
	public void setInstrument(String instrument) {
		if (instrument == null) {
			instrument = "";
		}
		if (!this.instrument.equals(instrument)) {
			this.instrument = instrument;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the instrumentType
	 */
	public String getInstrumentType() {
		return instrumentType;
	}

	/**
	 * @param instrumentType
	 *            the instrumentType to set
	 */
	public void setInstrumentType(String instrumentType) {
		if (instrumentType == null) {
			instrumentType = "";
		}
		if (!this.instrumentType.equals(instrumentType)) {
			this.instrumentType = instrumentType;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the isLinked
	 */
	public boolean isLinked() {
		return isLinked;
	}

	/**
	 * @param isLinked
	 *            the isLinked to set
	 */
	public void setLinked(boolean isLinked) {
		if (this.isLinked != isLinked) {
			this.isLinked = isLinked;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(String server) {
		if (server == null) {
			server = "";
		}
		if (!this.server.equals(server)) {
			this.server = server;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(String site) {
		if (site == null) {
			site = "";
		}
		if (!this.site.equals(site)) {
			this.site = site;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		if (url == null) {
			url = "";
		}
		if (!this.url.equals(url)) {
			this.url = url;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		if (userId == null) {
			userId = "";
		}
		if (!this.userId.equals(userId)) {
			this.userId = userId;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(long version) {
		if (this.version != version) {
			this.version = version;
			this.modelChanged = true;
		}
	}

	public String toXML() {
		StringBuffer xmlbuffer = new StringBuffer();
		xmlbuffer.append(CeNConstants.XML_VERSION_TAG);
		xmlbuffer.append("<Analysis_Properties Type=\"" + this.getInstrumentType() + "\">");
		xmlbuffer.append("<Meta_Data>");
		xmlbuffer.append("</Meta_Data>");
		xmlbuffer.append("</Analysis_Properties>");
		return xmlbuffer.toString();
	}

	/**
	 * @return the siteCode
	 */
	public String getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode
	 *            the siteCode to set
	 */
	public void setSiteCode(String siteCode) {
		if (siteCode == null) {
			siteCode = "";
		}
		this.siteCode = siteCode;
	}

	/**
	 * This method is purely for testing purpose
	 * 
	 * @param key
	 */
	public void setTempKey(String key) {
		this.key = key;
	}

	public AnalysisModel deepClone() {
		// key should be the same
		AnalysisModel clone = new AnalysisModel(this.key);
		clone.deepCopy(this);
		return clone;
	}

	public boolean isIPRelated() {
		return isIPRelated;
	}

	public void setIPRelated(boolean isIPRelated) {
		if (isIPRelated() != isIPRelated) {
			this.isIPRelated = isIPRelated;
			this.modelChanged = true;
		}
	}

	public void deepCopy(AnalysisModel src) {
		setAnnotation(src.annotation);
		setCyberLabFileId(src.cyberLabFileId);
		setServer(src.server);
		setCyberLabUserId(src.cyberLabUserId);
		setCyberLabDomainId(src.cyberLabDomainId);
		setCyberLabFolderId(src.cyberLabFolderId);
		setCyberLabLCDFId(src.cyberLabLCDFId);
		setCenSampleRef(src.cenSampleRef);
		setAnalyticalServiceSampleRef(src.analyticalServiceSampleRef);
		setUserId(src.userId);
		setGroupId(src.groupId);
		setDomain(src.domain);
		setExperiment(src.experiment);
		setExperimentTime(src.experimentTime);
		setSite(src.site);
		setSiteCode(src.siteCode);
		setInstrument(src.instrument);
		setInstrumentType(src.instrumentType);
		setFileType(src.fileType);
		setFileName(src.fileName);
		setFileSize(src.fileSize);
		setVersion(src.version);
		setUrl(src.url);
		setComments(src.comments);
		setLinked(src.isLinked);
		setFile(src.file);
		setIPRelated(src.isIPRelated);
		setLoadedFromDB(src.isLoadedFromDB());
		setToDelete(src.isSetToDelete());
	}
}
