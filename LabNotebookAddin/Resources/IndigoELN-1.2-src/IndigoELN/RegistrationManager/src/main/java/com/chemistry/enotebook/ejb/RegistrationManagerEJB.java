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
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanel;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanelSelector;
import com.chemistry.enotebook.compoundaggregation.exceptions.CompoundAggregationServiceException;
import com.chemistry.enotebook.compoundmanagement.CompoundManagementServiceFactory;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.exceptions.CompoundManagementSubmissionException;
import com.chemistry.enotebook.handler.PurificationServiceHandler;
import com.chemistry.enotebook.interfaces.RegistrationManagerRemote;
import com.chemistry.enotebook.vnv.VnvServiceFactory;
import com.chemistry.enotebook.vnv.classes.IVnvResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RegistrationManagerEJB implements RegistrationManagerRemote {
	
	private static final Log log = LogFactory.getLog(RegistrationManagerEJB.class);

	/**
	 * interface-method view-type = "remote"
	 */
	@Override
	public String submitBatchesAndPlatesForRegistration(String batchKeys[], String plateKeys[], String workflows[], NotebookPageModel pageModel) throws Exception {
		try {
			RegistrationManagerImpl impl = new RegistrationManagerImpl();
			return impl.submitBatchesAndPlatesForRegistration(batchKeys, plateKeys, workflows, pageModel, null);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * Following method submits batches in plates to CompoundRegistration for registration and returns back the job Id
	 * 
	 * @param List
	 *            of PlateModel Objects
	 * @return Job Id
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, String> submitPlatesForRegistration(ProductPlate[] pPlates, String compoundManagementEmployeeID, 
							NotebookPageModel pageModel) 
		throws Exception 
	{
		Map<String, String> jobIdMap = null;
		log.debug("Submitting Plates for Registration");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			jobIdMap = regImpl.submitPlatesForRegistration(pPlates, compoundManagementEmployeeID, pageModel, null);
		} catch (Exception err) {
			throw new Exception(err);
		}
		return jobIdMap;
	}

	/**
	 * Following method submits batches in plates to CompoundRegistration for registration and returns back the job Id
	 * 
	 * @param ArrayList
	 *            of BatchModel Objects
	 * @return Job Id
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, String> submitBatchesForRegistration(ProductBatchModel[] batches, String userID,
							NotebookPageModel pageModel) 
		throws Exception 
	{
		Map<String, String> jobIdMap = null;
		log.debug("Submitting Batches for Registration");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			jobIdMap = regImpl.submitBatchesForRegistration(batches, userID, pageModel, null);
		} catch (Exception err) {
			throw new Exception("Error submitting batches for registration", err);
		}
		return jobIdMap;
	}

	/**
	 * Following method retrieves updated batches registration info from CompoundRegistration.
	 * 
	 * @param ArrayList
	 *            of BatchModel Objects
	 * @param nullID
	 *            of BatchModel Objects
	 * @return ArrayList of BatchModel Objects
	 * @throws Exception
	 */
	@Override
	public ProductBatchModel[] getRegistrationInformation(ProductBatchModel[] batches)
		throws Exception 
	{
		log.debug("Retrieving Batches form CompoundRegistration Registration");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			batches = regImpl.getRegistrationInformation(batches, null);
		} catch (Exception err) {
			throw new Exception(err);
		}
		return batches;
	}

	/**
	 * Following method submits batches in plates to CompoundAggregation for submission and returns back the status
	 * 
	 * @param List
	 *            of PlateModel Objects
	 * @return Status
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, JobModel> submitPlatesToCompoundAggregation(String userID, ProductPlate[] pPlates, NotebookPageHeaderModel pageHeader, String siteCode)
		throws Exception 
	{
		Map<String, JobModel> resultMap = null;
		log.debug("Submitting plates for CompoundAggregation");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			resultMap = regImpl.submitPlatesToCompoundAggregation(userID, pPlates, pageHeader, siteCode);
		} catch (Exception err) {
			throw new Exception(err);
		}
		return resultMap;
	}
	
	@Override
	public String[] getScreenPanelsNames(long[] screenPanelIDs) throws Exception{
		log.debug("Screen Panels for CompoundAggregation");
		String[] result = null;
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			result = regImpl.getScreenPanelsNames(null, screenPanelIDs);
		} catch (Exception err) {
			throw new Exception(err);
		}
		return result;
	}

	/**
	 * Following method submits batches in plates to CompoundAggregation for submission and returns back the Status
	 * 
	 * @param ArrayList
	 *            of BatchModel Objects
	 * @return Submission Status
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, JobModel> submitBatchesToCompoundAggregation(String userID, ProductBatchModel[] batches, NotebookPageHeaderModel pageHeader, String siteCode)
		throws Exception 
	{
		Map<String, JobModel> resultMap = null;
		log.debug("Submitting plates for CompoundAggregation");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			resultMap = regImpl.submitBatchesToCompoundAggregation(userID, batches, pageHeader, siteCode);
		} catch (Exception err) {
			throw new Exception(err);
		}
		return resultMap;
	}

	/**
	 * Following method submits batches in plates to CompoundManagement submission and returns back the status
	 * 
	 * @param plates
	 * @param pageHeader
	 * @param nullID
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, JobModel> submitPlatesToCompoundManagement(ProductPlate[] plates, String siteCode) 
		throws Exception 
	{
		log.debug("Submitting plates for CompoundManagement");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.submitPlatesToCompoundManagement(plates, siteCode);
		} catch (CompoundManagementSubmissionException err) {
			throw new Exception(err);
		}
	}

	/**
	 * Following method submits tubes for CompoundManagement Submission and returns back the status
	 * 
	 * @param CompoundContainers
	 *            array
	 * @param pageHeader
	 * @param nullID
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, String> submitTubesToCompoundManagement(PlateWell<ProductBatchModel>[] compContainers, String siteCode)
		throws Exception 
	{
		log.debug("Submitting tubes for CompoundManagement");
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			Map<String, String> map = regImpl.submitTubesToCompoundManagement(compContainers, siteCode, null);
			log.debug("Submitted tubes for CompoundManagement");
			return map;
		} catch (CompoundManagementSubmissionException err) {
			log.error("CompoundManagementSubmissionException:"+err.getMessage());
			throw new Exception(err);
		}catch (Exception err2) {
			log.error("Exception:"+err2.getMessage());
			throw new Exception(err2);
		}
	}

	/**
	 * @param containerType
	 * @param siteCode
	 * @param null
	 * @return
	 * @throws CompoundManagementSubmissionException
	 * 
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public CompoundManagementBarcodePrefixInfo[] getCompoundManagementBarcodePrefixes(String containerType, String siteCode)
		throws Exception 
	{
		log.debug("Received Request for getCompoundManagementBarcodePrefixes for " + containerType);
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.getCompoundManagementBarcodePrefixes(containerType, siteCode, null);
		} catch (Exception err) {
			throw new Exception(err);
		}
	}

	/**
	 * @param barcodePrefix
	 * @param noOfBarcodes
	 * @param null
	 * @param siteCode
	 * @return
	 * @throws Exception 
	 */
	@Override
	public String[] getNewGBLPlateBarCodesFromCompoundManagement(String barcodePrefix, int noOfBarcodes,  String siteCode) throws Exception 
	{
		log.debug("Received Request for getNewGBLPlateBarCodesFromCompoundManagement for barcode prefix :" + barcodePrefix);
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.getNewGBLPlateBarCodesFromCompoundManagement(barcodePrefix, noOfBarcodes, null, siteCode);
		} catch (Exception err) {
			throw new Exception(err);
		}
	}

	/**
	 * @param siteCode
	 * @param plates
	 * @param null
	 * @return
	 * @throws Exception
	 * 
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, JobModel> submitPlatesForPurification(String userID, String siteCode, ProductPlate[] plates) 
		throws Exception 
	{
		log.debug("sending request to PurificationServiceHandler for PlatesPurification :" + plates.length);
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.submitPlatesForPurification(userID, siteCode, plates, null);
		} catch (Exception err) {
			throw new Exception(err);
		}
	}

	/**
	 * @param siteCode
	 * @param pageKey
	 * @param tubes
	 * @param null
	 * @return 
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public Map<String, JobModel> submitTubesForPurification(String userName, String siteCode, String pageKey, PlateWell<ProductBatchModel>[] tubes) 
		throws Exception 
	{
		log.debug("sending request to PurificationServiceHandler for WellPurification :" + (tubes != null ? tubes.length : "null array receied!"));
		if (tubes != null) {
			try {
				RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
				List<PlateWell<ProductBatchModel>> tubeList = Arrays.asList(tubes);
				return regImpl.submitTubesForPurification(userName, siteCode, pageKey, tubeList);
			} catch (Exception err) {
				throw new Exception(err);
			}
		}
		return null;
	}

	/**
	 * @param siteCode
	 * @return
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public boolean isCompoundAggregationApiAvailable(String siteCode) throws Exception 
	{
		return CompoundAggregationServiceFactory.getService().isAvailable(siteCode);
	}

	/**
	 * @return
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public boolean isPurificationServiceApiAvailable() 
		throws Exception 
	{
		return (new PurificationServiceHandler()).isAvailable();
	}

	/**
	 * @param siteCode
	 * @return
	 * @throws Exception
	 * interface-method view-type = "remote"
	 * transaction type = "NotSupported"
	 */
	@Override
	public boolean isCompoundManagementContainerApiAvailable(String siteCode) throws Exception {
		return CompoundManagementServiceFactory.getService().isAvailable(siteCode);
	}
	
	@Override
	public List<String> getPurificationArchivePlateChoice() throws java.lang.Exception {
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.getPurificationArchivePlateChoice();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<String> getLabsForPurification(String siteCode) throws java.lang.Exception {
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.getLabsForPurification(siteCode);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	@Override
	public List<String> getPurificationSampleWorkup() throws java.lang.Exception {
		try {
			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
			return regImpl.getPurificationSampleWorkup();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public String getCompoundManagementTubeGuidByBarCodeAndSiteCode(String barCode, String siteCode) throws Exception {
		try {
			return "";
			//TODO: add mock. Commented due to cen open source issue 
//			RegistrationManagerImpl regImpl = new RegistrationManagerImpl();
//			return regImpl.getCompoundManagementTubeGuidByBarCodeAndSiteCode(barCode, siteCode);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	@Override
	public IVnvResult validateStructureAssignStereoIsomerCode(String molStructure) throws Exception {
		return VnvServiceFactory.getService().validateStructureAssignStereoIsomerCode(molStructure);	
	}
	
	@Override
	public IVnvResult validateStructureWithStereoIsomerCode(String molStructure, String inputSic) throws Exception {
		return VnvServiceFactory.getService().validateStructureWithStereoIsomerCode(molStructure, inputSic);
	}
	
	@Override
	public ScreenPanelSelector getScreenPanelDialog() throws CompoundAggregationServiceException {
		try {
			return CompoundAggregationServiceFactory.getService().getScreenPanelDialog();
		} catch (Exception e) {
			throw new CompoundAggregationServiceException(e);
		}
	}
	
	@Override
	public ScreenPanel getScreenPanel(long screenKey) throws CompoundAggregationServiceException {
		try {
			return CompoundAggregationServiceFactory.getService().getScreenPanel(screenKey);
		} catch (Exception e) {
			throw new CompoundAggregationServiceException(e);
		}
	}
}
