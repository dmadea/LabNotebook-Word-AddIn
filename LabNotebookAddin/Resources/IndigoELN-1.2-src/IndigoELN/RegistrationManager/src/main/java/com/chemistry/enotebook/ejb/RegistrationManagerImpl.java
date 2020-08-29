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
package com.chemistry.enotebook.ejb;

import com.chemistry.enotebook.compoundaggregation.CompoundAggregationServiceFactory;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.exceptions.CompoundManagementSubmissionException;
import com.chemistry.enotebook.handler.*;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 *        Provides the actual implementation for the EJB Methods
 */
public class RegistrationManagerImpl 
{
	private static final Log log = LogFactory.getLog(RegistrationManagerImpl.class);

	/**
	 * Following method submits batches in plates to CompoundRegistration for registration and returns back the job Id
	 * 
	 * @param List
	 *            of PlateModel Objects
	 * @return Job Id
	 * @throws Exception
	 */
	public HashMap<String, String> submitPlatesForRegistration(ProductPlate[] pPlates, String compoundManagementEmployeeID, 
												NotebookPageModel pageModel, SessionIdentifier sessionID) 
		throws Exception 
	{
		log.debug("Calling Plate Registration ");
		CompoundRegistrationHandler compoundRegistration = new CompoundRegistrationHandler();
		return compoundRegistration.submitPlatesForRegistration(pPlates, sessionID, compoundManagementEmployeeID, pageModel);
	}

	/**
	 * Following method submits batches in plates to CompoundRegistration for registration and returns back the job Id
	 * 
	 * @param ArrayList
	 *            of BatchModel Objects
	 * @return Job Id
	 * @throws Exception
	 */
	public HashMap<String, String> submitBatchesForRegistration(ProductBatchModel[] batches, String userID,
									NotebookPageModel pageModel, SessionIdentifier sessionID) 
		throws Exception 
	{
		log.debug("Calling Batch Registration ");
		CompoundRegistrationHandler compoundRegistration = new CompoundRegistrationHandler();
		return compoundRegistration.submitBatchesForRegistration(batches, userID, userID, pageModel, sessionID);
	}

	/**
	 * Following method submits batches in plates to CompoundAggregation for submission and returns back the status
	 * 
	 * @param List
	 *            of PlateModel Objects
	 * @return Status
	 * @throws Exception
	 */
	public Map<String, JobModel> submitPlatesToCompoundAggregation(String userID, ProductPlate[] plates, NotebookPageHeaderModel pageHeader, String siteCode)
		throws Exception 
	{
		log.debug("Calling CompoundAggregation Submission ");
		Map<String, JobModel> jobStatus = new CompoundAggregationHandler(siteCode).submitPlatesToCompoundAggregation(userID, plates, pageHeader);
		return jobStatus;
	}

	/**
	 * Following method submits batches in plates to CompoundAggregation for submission and returns back the Status
	 * 
	 * @param ArrayList
	 *            of BatchModel Objects
	 * @return Submission Status
	 * @throws Exception
	 */
	public Map<String, JobModel> submitBatchesToCompoundAggregation(String userID, ProductBatchModel[] batches, NotebookPageHeaderModel pageHeader, String siteCode)
		throws Exception 
	{
		return new CompoundAggregationHandler(siteCode).submitBatchesToCompoundAggregation(userID, batches, pageHeader);
	}
	
	public String[] getScreenPanelsNames(SessionIdentifier sessionID, long[] screenPanelIDs) throws Exception {
		return CompoundAggregationServiceFactory.getService().getScreenPanelsNames(screenPanelIDs);
	}

//	/**
//	 * Following method submits batches in plates to CompoundManagement for Container Registration and returns back the status
//	 * 
//	 * @param List
//	 *            of PlateModel Objects
//	 * @return Status
//	 * @throws Exception
//	 */
//	public String submitPlatesToPurificationService(ArrayList<ProductBatchModel> plates, SessionIdentifier sessionID) 
//		throws Exception 
//	{
//		return "";
//	}
//
//	/**
//	 * Following method submits batches in plates to PurificationService for purification and returns back the Status
//	 * PurificationService requires tubes for submission.  This method is not currently implemented
//	 * 
//	 * @param ArrayList
//	 *            of BatchModel Objects
//	 * @return Submission Status
//	 * @throws Exception
//	 */
//	public String submitBatchesToPurificationService(ArrayList<ProductBatchModel> batches, SessionIdentifier sessionID) 
//		throws Exception 
//	{
//		return "";
//	}

	/**
	 * Following method submits batches in plates to CompoundManagement and returns back the status
	 * 
	 * @param List
	 *            of PlateModel Objects
	 * @return Status
	 * @throws Exception
	 */
	public Map<String, JobModel> submitPlatesToCompoundManagement(ProductPlate[] plates, String site) 
		throws CompoundManagementSubmissionException 
	{
		try {
			CompoundManagementHandler handler = new CompoundManagementHandler();
			return handler.submitPlatesForCompoundManagementRegistration(plates, site);
		} catch (Exception err) {
			throw new CompoundManagementSubmissionException("", err);
		}
	}

	/**
	 * Following method submits batches in tubes to CompoundManagement and returns back the status
	 * 
	 * @param List
	 *            of Tube Objects
	 * @return Status
	 * @throws Exception
	 */
	public Map<String, String> submitTubesToCompoundManagement(PlateWell<ProductBatchModel>[] compContainers, String site, SessionIdentifier sessionID)
		throws CompoundManagementSubmissionException 
	{
		log.debug("submitTubesToCompoundManagement.enter()");
		try {
			CompoundManagementHandler handler = new CompoundManagementHandler();
			Map<String, String> map = handler.submitTubesForCompoundManagementRegistration(compContainers, site);
			log.debug("submitTubesToCompoundManagement.exit()");
			return map;
		} catch (Exception err) {
			throw new CompoundManagementSubmissionException("", err);
		}
	}

	/**
	 * @param containerType
	 * @param siteCode
	 * @param session
	 * @return
	 * @throws CompoundManagementSubmissionException
	 */
	public CompoundManagementBarcodePrefixInfo[] getCompoundManagementBarcodePrefixes(String containerType, String siteCode, SessionIdentifier session)
		throws Exception 
	{
		CompoundManagementHandler compoundManagementHandler = new CompoundManagementHandler();
		return compoundManagementHandler.getCompoundManagementBarcodePrefixes(containerType, siteCode);
	}

	/**
	 * @param barcodePrefix
	 * @param noOfBarcodes
	 * @param session
	 * @param siteCode
	 * @return
	 * @throws Exception
	 */
	public String[] getNewGBLPlateBarCodesFromCompoundManagement(String barcodePrefix, int noOfBarcodes, 
													SessionIdentifier session, String siteCode)
		throws Exception 
	{
		try {
			CompoundManagementHandler compoundManagementHandler = new CompoundManagementHandler();
			return compoundManagementHandler.getNewGBLPlateBarCodesFromCompoundManagement(barcodePrefix, noOfBarcodes, siteCode);
		} catch (Exception err) {
			throw new Exception(err);
		}
	}

	/**
	 * @param siteCode
	 * @param plates
	 * @param session
	 * @return Map<plateKey, JobModel>
	 * @throws Exception
	 */
	public Map<String, JobModel> submitPlatesForPurification(String userID, String siteCode, ProductPlate[] plates, SessionIdentifier session) 
		throws Exception 
	{
		try {
			PurificationServiceHandler handler = new PurificationServiceHandler();
			return handler.submitPlatesForPurification(userID, siteCode, plates, session);
		} catch (Exception err) {
			throw new Exception(err);
		}
	}

	/**
	 * @param siteCode
	 * @param tubes
	 * @param session
	 * @return pageKey, jobModel submitted
	 * @throws Exception
	 */
	public Map<String, JobModel> submitTubesForPurification(String userName, String siteCode, String pageKey, List<PlateWell<ProductBatchModel>> tubes) 
		throws Exception 
	{
		try {
			PurificationServiceHandler handler = new PurificationServiceHandler();
			return handler.submitTubesForPurification(userName, siteCode, pageKey, tubes);
		} catch (Exception err) {
			throw new Exception(err);
		}
	}

	public ProductBatchModel[] getRegistrationInformation(ProductBatchModel[] batches, SessionIdentifier sessionID) 
	{
		try {
			CompoundRegistrationHandler handler = new CompoundRegistrationHandler();
			return handler.getRegistrationInformation(batches, sessionID);
		} catch (Exception err) {
			throw new RuntimeException(err);
		}
	}

	public String submitBatchesAndPlatesForRegistration(String[] batchKeys, String[] plateKeys, String[] workflows, NotebookPageModel pageModel, SessionIdentifier sessionID) throws StorageInitException {
		log.debug("submitBatchesAndPlatesForRegistration(String[], String[], String[], NotebookPageModel, SessionIdentifier) called");
		String batchKeysString = Arrays.asList(batchKeys).toString().replace('[', ' ').replace(']', ' ').trim();
		String plateKeysString = Arrays.asList(plateKeys).toString().replace('[', ' ').replace(']', ' ').trim();
		String workflowsString = Arrays.asList(workflows).toString().replace('[', ' ').replace(']', ' ').trim();
		return new AbstractHandler().createAndSubmitNewJob(batchKeysString, plateKeysString, workflowsString, pageModel, sessionID);
	}

	public String getCompoundManagementTubeGuidByBarCodeAndSiteCode(String barCode, String siteCode) {
		try {
			CompoundManagementHandler handler = new CompoundManagementHandler();
			return handler.getCompoundManagementTubeGuidByBarCodeAndSiteCode(barCode, siteCode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getPurificationArchivePlateChoice() {
		try {
			PurificationServiceHandler handler = new PurificationServiceHandler();
			return handler.getPurificationArchivePlateChoice();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getLabsForPurification(String siteCode) {
		try {
			PurificationServiceHandler handler = new PurificationServiceHandler();
			return handler.getLabsForPurification(siteCode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getPurificationSampleWorkup() {
		try {
			PurificationServiceHandler handler = new PurificationServiceHandler();
			return handler.getPurificationSampleWorkup();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
