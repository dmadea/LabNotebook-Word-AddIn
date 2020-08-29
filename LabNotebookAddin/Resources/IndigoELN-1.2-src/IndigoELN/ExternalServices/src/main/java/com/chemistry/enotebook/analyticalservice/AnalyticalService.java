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
package com.chemistry.enotebook.analyticalservice;

import com.chemistry.enotebook.analyticalservice.classes.AnalyticalServiceMetaData;
import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface to external Analytical Service to store analytical data files
 */
public interface AnalyticalService {

	/**
	 * Check external service health
	 * 
	 * @return true if service available, false if service unavailable
	 * @throws AnalyticalServiceException
	 */
	public boolean checkHealth() throws AnalyticalServiceException;

	/**
	 * Search files using query and site codes
	 * 
	 * @param compoundManagementSiteCodes
	 *            site codes where to search files
	 * @param query
	 *            search query
	 * @param maxNumFilesToBeReturned
	 *            maximum count of files to return
	 * @return object with search results
	 * @throws AnalyticalServiceException
	 */
	public SearchResultsSummary findFiles(
			List<String> compoundManagementSiteCodes, String query,
			int maxNumFilesToBeReturned) throws AnalyticalServiceException;

	/**
	 * Mark files as linked to the batch
	 * 
	 * @param sampleRef
	 *            reference to file
	 * @param resultList
	 *            information of files that need to be linked
	 * @param isLinked
	 *            true to link, false to unlink
	 * @throws AnalyticalServiceException
	 */
	public void markFilesAsLinked(String sampleRef,
			List<SearchResult> resultList, boolean isLinked)
			throws AnalyticalServiceException;

	/**
	 * Get file using fileID, site and domainID
	 * 
	 * @param domainID
	 *            domain code where file is stored
	 * @param fileID
	 *            file ID
	 * @param site
	 *            site code where file is stored
	 * @param os
	 *            output stream to write file contents
	 * @throws AnalyticalServiceException
	 */
	public void retrieveFile(String domainID, String fileID, String site,
			OutputStream os) throws AnalyticalServiceException;

	/**
	 * Upload file
	 * 
	 * @param fileAttributes
	 *            file attributes like name, length etc
	 * @param is
	 *            input stream to read file contents
	 * @return file information from service
	 * @throws AnalyticalServiceException
	 */
	public SearchResult sendFileAndGetFileInfo(
			UploadFileAttributes fileAttributes, InputStream is)
			throws AnalyticalServiceException;

	/**
	 * Retrieve analytical metadata
	 * 
	 * @return analytical metadata
	 * @throws AnalyticalServiceException
	 */
	public AnalyticalServiceMetaData retrieveAnalyticalServiceMetaData()
			throws AnalyticalServiceException;

	/**
	 * Set analytical references to the specified batch
	 * 
	 * @param site
	 *            site code where file is stored
	 * @param cyberlabID
	 *            file ID
	 * @param sampleRef
	 *            reference to file
	 * @param batchID
	 *            batch number
	 */
	public void setFileBatchID(String site, String cyberlabID,
			String sampleRef, String batchID);
}
