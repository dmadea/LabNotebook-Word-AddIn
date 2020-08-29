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
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;

public class PrintRequest {

	private final String siteCode;
	private final NotebookRef nbRef;
	private final Integer modelVersionNumber;
	private final String pageType;
	
	public PrintRequest(String siteCode, 
						NotebookRef notebookReference,
						Integer modelVersion) {
		this(siteCode, notebookReference, modelVersion, null);
	}

	public PrintRequest(String siteCode, 
	                    NotebookRef notebookReference,
	                    Integer modelVersion,
	                    String pageType ) {
		this.siteCode = siteCode;
		this.nbRef = notebookReference;
		this.modelVersionNumber = modelVersion;
		this.pageType = pageType;
	}
	
	public String getSiteCode() {
		return siteCode;
	}

	public NotebookRef getNbRef() {
		return nbRef;
	}

	public Integer getModelVersionNumber() {
		return modelVersionNumber == null ? new Integer(1) : modelVersionNumber;
	}

	public String getPageType() {
		return pageType;
	}

	public String getNotebookNumber() { return nbRef == null ? null : nbRef.getNbNumber(); }
	public String getNotebookPage() { return nbRef == null ? null : nbRef.getNbPage(); }

}
