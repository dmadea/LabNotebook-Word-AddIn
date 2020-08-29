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
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.analytical;

import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 * 
 * 
 */
public abstract class AbstractAnalysis extends PersistableObject implements
		Observer, DeepCopy, DeepClone, Comparable {
	
	private static final long serialVersionUID = -6781452256946815625L;
	
	private String annotation;
	private String cyberLabFileId;
	private String server;
	private String cyberLabUserId;
	private String cyberLabDomainId;
	private String cyberLabFolderId;
	private String cyberLabLCDFId;
	private String cenSampleRef;
	private String analyticalServiceSampleRef;
	private String groupId;
	private String userId;
	private String domain;
	private String experiment;
	private String experimentTime;
	private String site;
	private String instrument;
	private String instrumentType;
	private String fileType;
	private String fileName;
	private long fileSize;
	private long version;
	private String url;
	private String comments;
	boolean isLinked;
	boolean isIPRelated = false;

	/*
	 * Constructors
	 */
	public AbstractAnalysis() {
		this("", null);
	}

	public AbstractAnalysis(String analysisKey, String cyberLabId) {
		this.cyberLabFileId = cyberLabId;
	}

	public static String windowsValidFileName(String filename) {
		return normalizeFileName(fileNameOnly(filename));
	}

	protected static String fileNameOnly(String filepath) {
		// return filename without path
		File f = new File(filepath);
		return f.getName();
	}

	protected static String normalizeFileName(String filename) {
		// normalize for windows file names by stripping any of the following
		// characters
		// \ is the filename separator in DOS/Windows and the escape character
		// in Unix
		// / is the filename separator in Unix and the command option tag in DOS
		// : is the filename separator in MacOS and the drive indicator in DOS
		// * is a DOS wildcard character
		// ? is a DOS wildcard character
		// " is used by DOS to delimit file names with spaces
		// < is a DOS redirection character
		// > is a DOS redirection character
		// | is a DOS redirection character
		return filename.replaceAll("[\\\\:/*<>|]", "");
	}

	public void dispose() {
	} // No objects are allocated here.

	public String getCyberLabDomainId() {
		return cyberLabDomainId;
	}

	public void setCyberLabDomainId(String cyberLabDomainId) {
		this.cyberLabDomainId = cyberLabDomainId;
	}

	public String getCyberLabFileId() {
		return cyberLabFileId;
	}

	public void setCyberLabFileId(String cyberLabFileId) {
		this.cyberLabFileId = cyberLabFileId;
	}

	public String getCyberLabFolderId() {
		return cyberLabFolderId;
	}

	public void setCyberLabFolderId(String cyberLabFolderId) {
		this.cyberLabFolderId = cyberLabFolderId;
	}

	public String getCyberLabLCDFId() {
		return cyberLabLCDFId;
	}

	public void setCyberLabLCDFId(String cyberLabLCDFId) {
		this.cyberLabLCDFId = cyberLabLCDFId;
	}

	public String getCyberLabUserId() {
		return cyberLabUserId;
	}

	public void setCyberLabUserId(String cyberLabUserId) {
		this.cyberLabUserId = cyberLabUserId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getExperiment() {
		if (experiment == null)
			return "";
		return experiment;
	}

	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}

	public String getExperimentTime() {
		return experimentTime;
	}

	public void setExperimentTime(String experimentTime) {
		this.experimentTime = experimentTime;
	}

	public String getFileName() {
		return windowsValidFileName(fileName);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getGroupId() {
		if (groupId == null)
			return "";
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAnnotation() {
		if (annotation == null)
			return "";
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getCenSampleRef() {
		if (cenSampleRef == null)
			return "";
		return cenSampleRef;
	}

	public void setCenSampleRef(String cenSampleRef) {
		this.cenSampleRef = cenSampleRef;
	}

	public String getAnalyticalServiceSampleRef() {
		if (analyticalServiceSampleRef == null)
			return "";
		return analyticalServiceSampleRef;
	}

	public void setAnalyticalServiceSampleRef(String analyticalServiceSampleRef) {
		this.analyticalServiceSampleRef = analyticalServiceSampleRef;
	}

	public boolean isLinked() {
		return isLinked;
	}

	public void setLinked(boolean isLinked) {
		this.isLinked = isLinked;
	}

	/**
	 * This is an override of the ObservableObject. Don't use this to update the
	 * values in the object as it is meant only to indicate that the
	 * isModified() flag was set on either this object or one it contains.
	 */
	public void update(Observable observed) {

		super.update(observed);
	}

	//
	// DeepClone/Copy Interface
	//

	public void deepCopy(Object resource) {
		if (resource instanceof AbstractAnalysis) {
			AbstractAnalysis src = (AbstractAnalysis) resource;

			setCenSampleRef(src.getCenSampleRef());
			setAnalyticalServiceSampleRef(src.getAnalyticalServiceSampleRef());
			setVersion(src.getVersion());
			setAnnotation(src.getAnnotation());
			setComments(src.getComments());
			setCyberLabDomainId(src.getCyberLabDomainId());
			setCyberLabFileId(src.getCyberLabFileId());
			setCyberLabFolderId(src.getCyberLabFolderId());
			setCyberLabLCDFId(src.getCyberLabLCDFId());
			setCyberLabUserId(src.getCyberLabUserId());
			setDomain(src.getDomain());
			setExperiment(src.getExperiment());
			setExperimentTime(src.getExperimentTime());
			setFileName(src.getFileName());
			setFileSize(src.getFileSize());
			setFileType(src.getFileType());
			setGroupId(src.getGroupId());
			setInstrument(src.getInstrument());
			setInstrumentType(src.getInstrumentType());
			setServer(src.getServer());
			setSite(src.getSite());
			setUrl(src.getUrl());
			setUserId(src.getUserId());
			setLinked(src.isLinked());
		}
	}

	public abstract Object deepClone();

	public boolean isIPRelated() {
		return isIPRelated;
	}

	public void setIPRelated(boolean isIPRelated) {
		this.isIPRelated = isIPRelated;
	}

	// For Sorting
	public int compareTo(Object o) {
		int result = 0;
		if (o != null && o instanceof AbstractAnalysis) {
			AbstractAnalysis a = (AbstractAnalysis) o;
			if (cenSampleRef != null && cenSampleRef.length() > 0
					&& a.getCenSampleRef() != null
					&& a.getCenSampleRef().length() > 0)
				result = (cenSampleRef.compareTo(a.getCenSampleRef()));
			else if (analyticalServiceSampleRef != null && analyticalServiceSampleRef.length() > 0
					&& a.getAnalyticalServiceSampleRef() != null
					&& a.getAnalyticalServiceSampleRef().length() > 0)
				result = (analyticalServiceSampleRef.compareTo(a.getAnalyticalServiceSampleRef()));
		}
		return result;
	}
}
