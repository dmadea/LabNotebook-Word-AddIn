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
package com.chemistry.enotebook.analyticalservice.clean;

import com.chemistry.enotebook.analyticalservice.classes.SearchResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class SearchResultImpl implements SearchResult
{

    public SearchResultImpl(String server, String cyberlabUserID, String cyberlabDomainID, String cyberlabFileID, String cyberlabFolderID, String cyberlabLCDFID, String sampleReference[], 
            String batchID[], String groupID, String userID, String domain, String experiment, Date experimentTime, String instrument, 
            String instrumentType, String site, String fileType, String fileName, Long filesize, Double version)
    {
        this.server = server;
        this.cyberlabUserID = cyberlabUserID;
        this.cyberlabDomainID = cyberlabDomainID;
        this.cyberlabFileID = cyberlabFileID;
        this.cyberlabFolderID = cyberlabFolderID;
        this.cyberlabLCDFID = cyberlabLCDFID;
        this.sampleReference = sampleReference;
        this.batchID = batchID;
        this.groupID = groupID;
        this.userID = userID;
        this.domain = domain;
        this.experiment = experiment;
        this.experimentTime = experimentTime;
        this.instrument = instrument;
        this.instrumentType = instrumentType;
        this.site = site;
        this.fileType = fileType;
        
        URL temp;
        try {
			temp = new URL("data/test.pdf");
		} catch (MalformedURLException e) {
			temp = null;
		}
		file = temp;
        filename = fileName;
        this.filesize = filesize;
        this.version = version;
    }

    public SearchResultImpl(String cyberlabDomainID, String cyberlabFileID, String cyberlabFolderID, String sampleReference[], String batchID[], String groupID, String userID, 
            String domain, String experiment, Date experimentTime, String instrument, String instrumentType, String site, String fileType, 
            String fileName, Long filesize)
    {
        this(null, null, cyberlabDomainID, cyberlabFileID, cyberlabFolderID, null, sampleReference, batchID, groupID, userID, domain, experiment, experimentTime, instrument, instrumentType, site, fileType, fileName, filesize, null);
    }

    public SearchResultImpl(String cyberlabFileID, String cyberlabFolderID, String sampleReference[], String batchID[], String groupID, String userID, String domain, 
            String experiment, Date experimentTime, String instrument, String instrumentType, String site, String fileType, String fileName, 
            Long filesize)
    {
        this(null, null, null, cyberlabFileID, cyberlabFolderID, null, sampleReference, batchID, groupID, userID, domain, experiment, experimentTime, instrument, instrumentType, site, fileType, fileName, filesize, null);
    }

    public SearchResultImpl(String cyberlabDomainID, String cyberlabFileID, String site, String fileName)
    {
        this(null, null, cyberlabDomainID, cyberlabFileID, null, null, null, null, null, null, null, null, null, null, null, site, null, fileName, null, null);
    }

    public SearchResultImpl(String cyberlabDomainID, String cyberlabFileID, String site)
    {
        this(null, null, cyberlabDomainID, cyberlabFileID, null, null, null, null, null, null, null, null, null, null, null, site, null, null, null, null);
    }

    public final String getServer()
    {
        return server;
    }

    public final String getCyberlabDomainID()
    {
        return cyberlabDomainID;
    }

    public final String getCyberlabUserID()
    {
        return cyberlabUserID;
    }

    public final String getCyberlabID()
    {
        return cyberlabFileID;
    }

    public String getCyberlabFolderID()
    {
        return cyberlabFolderID;
    }

    public String getCyberlabLCDFID()
    {
        return cyberlabLCDFID;
    }

    public final String getDomain()
    {
        return domain;
    }

    public final String getExperiment()
    {
        return experiment;
    }

    public final Date getExperimentTime()
    {
        return experimentTime;
    }

    public final URL getFile()
    {
        return file;
    }

    public final String getFileType()
    {
        return fileType;
    }

    public final String getGroupID()
    {
        return groupID;
    }

    public final String getInstrument()
    {
        return instrument;
    }

    public final String getInstrumentType()
    {
        return instrumentType;
    }

    public final String[] getSampleReference()
    {
        return sampleReference;
    }

    public final String[] getBatchID()
    {
        return batchID;
    }

    public final String getSite()
    {
        return site;
    }

    public final String getUserID()
    {
        return userID;
    }

    public final String getFilename()
    {
        return filename;
    }

    public String getShortFilename()
    {
        String returnValue = getFilename();
        if(returnValue != null)
        {
            int index = returnValue.lastIndexOf('/');
            if(index > -1)
                returnValue = returnValue.substring(index + 1);
        }
        return returnValue;
    }

    public final Long getFilesize()
    {
        return filesize;
    }

    public Double getVersion()
    {
        return version;
    }

    static final long serialVersionUID = 0xc3335824568d0aeL;
    private final String server;
    private final String cyberlabUserID;
    private final String cyberlabDomainID;
    private final String cyberlabFileID;
    private final String cyberlabFolderID;
    private final String cyberlabLCDFID;
    private final String sampleReference[];
    private final String batchID[];
    private final String groupID;
    private final String userID;
    private final String domain;
    private final String experiment;
    private final Date experimentTime;
    private final String instrument;
    private final String instrumentType;
    private final String site;
    private final String fileType;
    private final URL file;
    private final String filename;
    private final Long filesize;
    private final Double version;
}
