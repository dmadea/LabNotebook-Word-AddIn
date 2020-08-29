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
import java.net.URL;
import java.util.Date;

public interface SearchResult extends Serializable {
    public String getExperiment();

    public Date getExperimentTime();

    public String getGroupID();

    public String getInstrument();

    public String getInstrumentType();

    public String[] getSampleReference();

    public String[] getBatchID();

    public String getSite();

    public Long getFilesize();
    
    public Double getVersion();
    
    public String getUserID();
    
    public String getServer();
    
    public String getFilename();
    
    public String getDomain();
    
    public String getFileType();
    
    public URL getFile();
    
    public String getCyberlabDomainID();
    
    public String getCyberlabFolderID();
    
    public String getCyberlabID();
    
    public String getCyberlabLCDFID();
    
    public String getCyberlabUserID();
}
