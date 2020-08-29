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

import com.chemistry.enotebook.analyticalservice.AnalyticalService;
import com.chemistry.enotebook.analyticalservice.classes.AnalyticalServiceMetaData;
import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticalServiceCleanImpl implements AnalyticalService {
	


	public void retrieveFile(String domainID, String fileID, String site, OutputStream os) throws AnalyticalServiceException {
//		try {
//			Document document = new Document();
//
//			PdfWriter.getInstance(document, os);
//			
//			document.open();
//			
//			document.add(new Paragraph("----------------------------"));
//			document.add(new Paragraph(" This is the test PDF file"));
//			document.add(new Paragraph("----------------------------"));
//			
//			document.close();
//		} catch (DocumentException e) {
//			throw new AnalyticalServiceException(e);
//		}
	}

	public boolean checkHealth() {
		return true;
	}

	public SearchResult sendFileAndGetFileInfo(UploadFileAttributes arg2, InputStream is) throws AnalyticalServiceException {
		return new SearchResultImpl("", "", "");
	}

	public SearchResultsSummary findFiles(List<String> compoundManagementSiteCodes, String query, int maxNumFilesToBeReturned) throws AnalyticalServiceException {
		ArrayList<SearchResult> arrayList = new ArrayList<SearchResult>();
		arrayList.add(new SearchResultImpl("", "", ""));
		return new SearchResultsSummary(arrayList);
	}



	public void markFilesAsLinked(String sampleRef, List<SearchResult> resultList, boolean isLinked) throws AnalyticalServiceException {
        System.out.println("");	
	}

	public AnalyticalServiceMetaData retrieveAnalyticalServiceMetaData() throws AnalyticalServiceException {
		Map<String, String> map = new HashMap<String, String>();
		ArrayList<String> fileTypes = new ArrayList<String>();
		ArrayList<String> instrumentTypes = new ArrayList<String>();
		
		return new AnalyticalServiceMetaData(map, instrumentTypes, fileTypes);
	}

	public void setFileBatchID(String site, String cyberlabID, String sampleRef, String batchID) {
        System.out.println("");
	}
}
