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
package com.chemistry.enotebook.analyticalservice.delegate;

import com.chemistry.enotebook.analyticalservice.classes.AnalyticalServiceMetaData;
import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceAccessException;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceException;
import com.chemistry.enotebook.analyticalservice.interfaces.AnalyticalUIService;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnalyticalServiceDelegate implements AnalyticalUIService
{

    private AnalyticalUIService service;

    public AnalyticalServiceDelegate()
        throws AnalyticalServiceAccessException
    {
    	service = ServiceLocator.getInstance().locateService("AnalyticalService", AnalyticalUIService.class);
    }

    /**
     * Business method,retrieves the File
     * @param r
     * @param fos
     * @throws RemoteException
     * @throws PropertyException
     * @throws IOException
     */
    @Override
    public byte[] retrieveFileContents(String domainID, String fileID, String site) throws PropertyException, IOException, AnalyticalServiceException {
       	return service.retrieveFileContents(domainID, fileID, site);
    }

    /**
	 * @returns AnalyticalServiceMetaData which contains a domainCache - from CeN, AnalyticalService Files Types, AnalyticalService Instrument Types
	 *          from the analyticalService client we connected to via the CeN Properties in the DB. Note the instrument types may differ
	 *          from those setup in the CeN db.
	 * @throws CeNAnalyticalServiceAccessException 
	 */
    @Override
	public AnalyticalServiceMetaData retrieveAnalyticalServiceMetaData() throws PropertyException, AnalyticalServiceException {
		return service.retrieveAnalyticalServiceMetaData();
	}

    /**A  business method
     * if instruction flag is true resultList would be marked as linked in AnalyticalService for the sampleRef
     *
     * @param sampleRef
     * @param resultList
     * @param instruction
     * @throws RemoteException
     * @throws PropertyException
     */
    @Override
    public void markFileAsLinked(String sampleRef, List<SearchResult> resultList, boolean linked)
        throws PropertyException, AnalyticalServiceException, AnalyticalServiceAccessException
    {
        service.markFileAsLinked(sampleRef, resultList, linked);
    }

    /**
     *
     * @param fileAttributes
     * @param istream
     * @throws RemoteException
     * @throws PropertyException
     */
    @Override
    public SearchResult doFileUpload(final UploadFileAttributes fileAttributes, final byte[] fileContents) throws AnalyticalServiceException {
    	return service.doFileUpload(fileAttributes, fileContents);
    }

    /**
     *
     * @return
     * @throws RemoteException
     * @throws PropertyException
     */
    @Override
    public List<String> getAllInstrumentTypesFromDB()
        throws PropertyException
    {
        return service.getAllInstrumentTypesFromDB();
    }

    /**
     * A  business method
     * @throws RemoteException
     * @throws AnalyticalServiceException
     *
     * @ejb.interface-method view-type = "remote"
     *
     * the function requested by the container because of an system-level error.
     */
    @Override
    public boolean canMarkFileAsLinked()
    {
        return service.canMarkFileAsLinked();
    }

    /** Business method, retrieves the analyticalService data
     *
     * @param query
     * @param al_sites
     * @return
     * @throws RemoteException
     * @throws PropertyException
     */
    @Override
    public SearchResultsSummary retrieveAnalyticalServiceData(String query, List<String> al_sites)
        throws AnalyticalServiceException
    {
    	return service.retrieveAnalyticalServiceData(query, al_sites);
    }

    /** Business method: Sets Batch ID for a AnalyticalService Sample
     *
     * @param updateList
     * @return
     * @throws RemoteException
     * @throws PropertyException
     */
    @Override
    public void setFileBatchID(List<AnalysisModel> updateList)
        throws PropertyException, AnalyticalServiceException, AnalyticalServiceAccessException
    {
        service.setFileBatchID(updateList);
    }

    /**
     *
     * @return
     * @throws AnalyticalServiceException
     */
    @Override
	public boolean checkHealth()
		throws AnalyticalServiceException 
	{
		return service.checkHealth();
	}

	public static String windowsValidFileName(String filename){
		return normalizeFileName(fileNameOnly(filename));
	}
	
	protected static String fileNameOnly(String filepath){
		// return filename without path
		File f = new File(filepath);
		return f.getName();
	}
	
	protected static String normalizeFileName(String filename){
		// normalize for windows file names by stripping any of the following characters
		//		\ is the filename separator in DOS/Windows and the escape character in Unix
		//		/ is the filename separator in Unix and the command option tag in DOS
		//		: is the filename separator in MacOS and the drive indicator in DOS
		//		* is a DOS wildcard character
		//		? is a DOS wildcard character
		//		" is used by DOS to delimit file names with spaces
		//		< is a DOS redirection character
		//		> is a DOS redirection character
		//		| is a DOS redirection character
		return filename.replaceAll("[\\\\:/*<>|]", "");
	}
}
