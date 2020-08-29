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
package com.chemistry.enotebook.analyticalservice.ejb;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.analyticalservice.AnalyticalService;
import com.chemistry.enotebook.analyticalservice.AnalyticalServiceFactory;
import com.chemistry.enotebook.analyticalservice.classes.AnalyticalServiceMetaData;
import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.dao.AnalyticalServiceDao;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceException;
import com.chemistry.enotebook.analyticalservice.interfaces.AnalyticalUIService;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class AnalyticalUIServiceEJB implements AnalyticalUIService {
	
	private static final Log log = LogFactory.getLog(AnalyticalUIServiceEJB.class);
	
	/**
	 * A business method if instruction flag is true resultList would be marked as linked in AnalyticalService for the sampleRef
	 *
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public void markFileAsLinked(String sampleRef, List<SearchResult> resultList, boolean isLinked )
		throws AnalyticalServiceException 
	{
		try {
			AnalyticalService analyticalServiceService = AnalyticalServiceFactory.getService();
			analyticalServiceService.markFilesAsLinked(	sampleRef, resultList, isLinked);
		} catch (Throwable e) {
			log.error(e.toString(), e);
			throw new AnalyticalServiceException(e.getMessage(), e);
		}
	}

	/**
	 * A business method
	 *
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public byte[] retrieveFileContents(String domainID, String fileID, String site ) 
		throws AnalyticalServiceException 
	{
		try {
			AnalyticalService analyticalServiceService = AnalyticalServiceFactory.getService();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			analyticalServiceService.retrieveFile(domainID, fileID, site, bos);
			byte[] b = new byte[bos.size()];
			b = bos.toByteArray();
			bos.close();
			return b;
		} catch (Throwable e) {
			log.error(e.toString(), e);
			throw new AnalyticalServiceException(e.getMessage(), e);
		}
	}

	/**
	 * @returns AnalyticalServiceMetaData which contains a domainCache - from CeN, <br>
	 *          AnalyticalService Files Types, AnalyticalService Instrument Types come from the analyticalService client.
	 *          The analyticalService client is connected to via the CeN Properties in the CeN DB. 
	 *          Note the instrument types may differ from those setup in the CeN db accessed 
	 *          by getAllInstrumentTypesFromDB(SessionIdentifier).
	 *
	 * @throws AnalyticalServiceException
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public AnalyticalServiceMetaData retrieveAnalyticalServiceMetaData() 
		throws AnalyticalServiceException 
	{
		try{
			AnalyticalService clientInterface = AnalyticalServiceFactory.getService();
			return clientInterface.retrieveAnalyticalServiceMetaData();
		} catch (LoadServiceException e) {
			throw new AnalyticalServiceException(e);
		}
	}
	
	/**
	 * A business method,
	 *
	 * @throws PropertyException
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public SearchResult doFileUpload(final UploadFileAttributes fileAttributes, 
	                                               final byte[] fileContents
												   ) 
	    throws AnalyticalServiceException
	{
		try{
			AnalyticalService clientInterface = AnalyticalServiceFactory.getService();
			InputStream is = new ByteArrayInputStream(fileContents);
			return clientInterface.sendFileAndGetFileInfo(fileAttributes, is);
		} catch (LoadServiceException e) {
			throw new AnalyticalServiceException(e);
		}
	}

	/**
	 * A business method
	 *
	 */
	public boolean canMarkFileAsLinked() {
		return new AnalyticalServiceDao().canMarkAsLinked();
	}

	/**
	 * A business method
	 * @param List<String> of compoundManagement site codes to search.  
	 * @throws PropertyException
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public SearchResultsSummary retrieveAnalyticalServiceData(String query, List<String> compoundManagementSiteCodes )
		throws AnalyticalServiceException
	{
		try {
			int maxNumFilesToBeReturned = new AnalyticalServiceDao().getMaxNumberOfResults();
			AnalyticalService clientInterface = AnalyticalServiceFactory.getService();
			SearchResultsSummary result = clientInterface.findFiles(compoundManagementSiteCodes,
																	query,
																	maxNumFilesToBeReturned);
			return result;
		} catch (Throwable e) {
			String msg = "Problem retrieving query: " + query + " for AnalyticalService Site Codes: " + CommonUtils.getConcatenatedString(compoundManagementSiteCodes);
			log.error(msg, e);
			throw new AnalyticalServiceException(msg, e);
		}
	}

	

	/**
	 * A method to set the Notebook Batch ID for a given file in AnalyticalService. This is used when the sample reference entered at the
	 * instrument does not match exactly with the Notebook Batch #.
	 *
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public void setFileBatchID(List<AnalysisModel> updateList ) 
		throws AnalyticalServiceException
	{
		String batchID = null;
		String sampleRef = null;
		StringBuffer errorMsg = new StringBuffer();
		try {
			AnalyticalService clientInterface = AnalyticalServiceFactory.getService();

			for (Iterator<AnalysisModel> it = updateList.iterator(); it.hasNext();) {
				AnalysisModel val = it.next();

				batchID = val.getCenSampleRef();
				sampleRef = val.getCenSampleRef();
				if (StringUtils.isNotBlank(batchID) && StringUtils.isNotBlank(sampleRef)) {
					clientInterface.setFileBatchID(	val.getSite(),
													val.getCyberLabFileId(),
													sampleRef,
													batchID);
				} else {
					// what do we do when batchID or sampleRef are null?  Capture the issue and move to the next.
					if(errorMsg.length() > 0) {
						errorMsg.append("\n");
					} else {
						errorMsg.append("Cannot bind blank ids!\n");
					}
					errorMsg.append("Given a batchID of ["+batchID+"] with a sampleRef = " + sampleRef);
				}
			}
		} catch (Throwable e) {
			log.error("Problem setting file Batch Id for sample ref: " + sampleRef + " and batch id: " + batchID, e);
			throw new AnalyticalServiceException(e.getMessage(),e);
		}
		if(errorMsg.length() > 0) {
			// I want this wrapped for transport. Yes I know it will be 
			throw new AnalyticalServiceException("Problem setting some or all bindings: " + errorMsg.toString());
		}

	}

	/**
	 * A business method
	 * @throws AnalyticalServiceException
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public boolean checkHealth() throws AnalyticalServiceException 
	{
		AnalyticalService clientInterface;
		try {
			clientInterface = AnalyticalServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new AnalyticalServiceException(e);
		}
		return clientInterface.checkHealth();
	}
	
	/**
	 * A method used to get CeN Property information directly.  Unfortunately this incurs same 
	 * overhead as getting the results from retrieveAnalyticalServiceMetaData.
	 * 
	 * @throws PropertyException
	 * 
	 *             Thrown if the instance could not perform the function requested by the container because of an system-level
	 *             error.
	 */
	public List<String> getAllInstrumentTypesFromDB() 
		throws PropertyException 
	{
		return new AnalyticalServiceDao().getInstrumentTypesSupported();
	}
}
