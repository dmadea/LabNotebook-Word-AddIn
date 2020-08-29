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
package com.chemistry.enotebook.analyticalservice.classes;

import java.io.Serializable;
import java.util.Date;

public class UploadFileAttributes implements Serializable
{
	public UploadFileAttributes(String fileName, long fileLength, String sourceComputer, String sourcePath, String sampleReference, String batchID, 
            String site, Date experimentDate, Date fileDate, String userId, String domain) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.fileDate = fileDate;        
        this.sourceComputer = sourceComputer;
        this.sourcePath = sourcePath;
        this.sampleReference = sampleReference;
        this.batchID = batchID;
        this.experimentDate = experimentDate;
        this.userId = userId;
        this.domain = domain;
        this.site = site;
    }

    public Date getExperimentDate()
    {
        return experimentDate;
    }

    public String getExperimentName()
    {
        return experimentName;
    }

    public void setExperimentName(String experimentName)
    {
        this.experimentName = experimentName;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getInstrumentType()
    {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType)
    {
        this.instrumentType = instrumentType;
    }

    public String getDataFileType()
    {
        return "PDF";
    }

    public String getInstrument()
    {
        return "Manual Data Upload";
    }

    public String getSampleReference()
    {
        return sampleReference;
    }

    public String getBatchID()
    {
        return batchID;
    }

	public String getDomain() {
		return domain;
	}

	public long getFileLength() {
		return fileLength;
	}

	public String getFileName() {
		return fileName;
	}

	public Date getFileDate() {
		return fileDate;
	}
	
	public String getSite() {
		return site;
	}

	public String getSourceComputer() {
		return sourceComputer;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getUserId() {
		return userId;
	}
    
	private final String sampleReference;
    private final String batchID;
    private final Date experimentDate;
    private String groupId;
    private String instrumentType;
    private String experimentName;
    private String fileName;
	private long fileLength;
	private Date fileDate;
	private String sourceComputer;
	private String sourcePath;
	private String userId;
	private String domain;
	private String site;
	private static final long serialVersionUID = 7346472295622776147L;
}
