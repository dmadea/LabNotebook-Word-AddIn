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
import java.util.List;
import java.util.Map;

/**
 * Basic Value Object for serializing the AnalyticalService Service available information across the network.
 *
 */
public class AnalyticalServiceMetaData implements Serializable {
	
	private static final long serialVersionUID = 8994508515508376321L;

	private Map<String, String> domains = null;
	private List<String> instrumentTypes = null;
	private List<String> fileTypes = null;
	
	public AnalyticalServiceMetaData(Map<String, String> domains, List<String> supportedInstrumentTypes, List<String> supportedFileTypes) {
		this.domains = domains;
		this.instrumentTypes = supportedInstrumentTypes;
		this.fileTypes = supportedFileTypes;
	}

	public Map<String, String> getSiteDescriptionsToSiteCodeMap() { return domains; }
	/**
	 * Holds a list of available instrument types
	 * @return
	 */
	public List<String> getInstrumentTypesSupported() { return instrumentTypes; }
	/** 
	 * Holds a list of supported file types
	 * @return
	 */
	public List<String> getFileTypesSupported() { return fileTypes; }	
	
}
