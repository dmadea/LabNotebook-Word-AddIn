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
package com.chemistry.enotebook.handler;

import com.chemistry.enotebook.compoundmanagement.CompoundManagementServiceFactory;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.exceptions.CompoundManagementSubmissionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class CompoundManagementHandler extends AbstractHandler {
	private static final Log log = LogFactory.getLog(CompoundManagementHandler.class);
	private CompoundManagementContainerHandler containerHandler = new CompoundManagementContainerHandler();
	
	/**
	 * @param plates
	 * @param siteCode
	 * @param session
	 * @return
	 * @throws CompoundManagementSubmissionException
	 */
	public Map<String, JobModel> submitPlatesForCompoundManagementRegistration(ProductPlate[] plates, String siteCode) throws CompoundManagementSubmissionException {
		String status = "";
		String statusMessage = "";
		HashMap<String, JobModel> statusMap = new HashMap<String, JobModel>();
		JobModel[] jobs = new JobModel[plates.length];
		try {
			for (int i = 0; i < plates.length; ++i) {
				try {
					// Take this out when workflow is enabled and plate is selected from UI for CompoundRegistration & CompoundManagement same time.
					jobs[i] = perpareAndSubmitJob(plates[i].getKey(), "", null, COMPOUND_MANAGEMENT_APP);
					boolean regStatus = containerHandler.registerNewPlate(plates[i], siteCode);
					log.debug("Container Registration Status :" + regStatus);
					if (regStatus) {
						status = CeNConstants.REGINFO_SUBMISION_PASS;
					} else {
						status = CeNConstants.REGINFO_SUBMISION_FAIL;
					}
				} catch (Exception error) {
					log.error("Plate Submission Error.", error);
					status = CeNConstants.REGINFO_SUBMISION_FAIL;
					statusMessage = error.getMessage();
				}
				jobs[i].setCompoundManagementStatus(status);
				jobs[i].setCompoundManagementStatusMessage(statusMessage);
				statusMap.put(plates[i].getKey(), jobs[i]);
			}
			try {
				// This needs to be revisited when Workflow is enabled. When Plate submmited alone doesn't have job record already.
				getStorageEJBLocal().updateRegistrationJobs(jobs, null);
			} catch (Exception error) {
				log.error("Unable to insert JOBS using Storage Service", error);
			}
		} catch (Exception error) {
			throw new CompoundManagementSubmissionException(error.getMessage(), error);
		}

		return statusMap;
	}

  /**
   * @param tubes
   * @param siteCode
   * @param session
   * @return
   * @throws CompoundManagementSubmissionException. This will never be thrown actually. This exception is caught and appropriate error
   * message is logged to the DB as CompoundManagementStatusMessage.A status Map is sent back to the callee
   */
  public Map<String, String> submitTubesForCompoundManagementRegistration(PlateWell<ProductBatchModel>[] compContainers, String siteCode) throws CompoundManagementSubmissionException {
    log.info("submitTubesForCompoundManagementRegistration(PlateWell[], String) start");
    String status = "";
    HashMap<String, String> statusMap = new HashMap<String, String>();
    JobModel[] jobs = new JobModel[compContainers.length];
    
    try {
    	if (compContainers != null) {
    		for (int i = 0; i < compContainers.length; ++i) {
    			if (compContainers[i] != null) {
    				jobs[i] = new JobModel();
    				jobs[i].setPlateKey(compContainers[i].getKey());
    				try {
    					containerHandler.registerNewTubes(compContainers[i], siteCode);
        				status = CeNConstants.REGINFO_SUBMISION_PASS;
    				} catch (Exception e) {
    					log.error("CompoundManagement Tube submission error: ", e);
						status = CeNConstants.REGINFO_SUBMISION_FAIL;
					}
    				statusMap.put(compContainers[i].getKey(), status);
    				jobs[i].setCompoundManagementStatus(status);
    			}
    		}
    	}
    } catch (Exception error) {  // TODO make error handling back to the client more robust
    	log.error("CompoundManagement API Exception:", error);
    	throw new CompoundManagementSubmissionException("CompoundManagement API Exception:" + error.getMessage(), error);
    }
    
    log.info("submitTubesForCompoundManagementRegistration(PlateWell[], String) end");
    return statusMap;
  }

	/**
	 * @param containerType
	 * @param siteCode
	 * @param session
	 * @return
	 * @throws CompoundManagementSubmissionException
	 */
	public CompoundManagementBarcodePrefixInfo[] getCompoundManagementBarcodePrefixes(String containerType, String siteCode) throws Exception {
		try {
			log.info("getCompoundManagementBarcodePrefixes(String, String) start");
			log.info("Container Type " + containerType);
			CompoundManagementBarcodePrefixInfo[] prefixInfo = containerHandler.getCompoundManagementBarcodePrefixes(containerType.trim(), siteCode.trim());
			log.info("Returning CompoundManagementBarCodePrefixes count " + prefixInfo.length);
			log.info("getCompoundManagementBarcodePrefixes(String, String) start");
			return prefixInfo;
		} catch (Exception error) {
			log.error("Error getting Barcode prefixes:", error);
			throw error;
		}
	}

	/**
	 * @param barcodePrefix
	 * @param noOfBarcodes
	 * @param session
	 * @param siteCode
	 * @return
	 * @throws Exception
	 */
	public String[] getNewGBLPlateBarCodesFromCompoundManagement(String barcodePrefix, int noOfBarcodes, String siteCode) throws Exception {
		try {
			log.info("getNewGBLPlateBarCodesFromCompoundManagement(String, int, String) start");
			String[] barCodes = containerHandler.getNewGBLPlateBarCodesFromCompoundManagement(barcodePrefix, noOfBarcodes, siteCode);
			log.info("Returning BarCodes count " + barCodes.length);
			log.info("getNewGBLPlateBarCodesFromCompoundManagement(String, int, String) end");
			return barCodes;
		} catch (Exception error) {
			log.error("Error getting Barcodes:", error);
			throw new CompoundManagementSubmissionException(error.getMessage(), error);
		}
	}

	/**
	 * @param siteCode
	 * @return
	 * @throws Exception
	 */
	public boolean isAvailable(String siteCode) throws Exception {
		log.info("isAvailable(String) called");
		return CompoundManagementServiceFactory.getService().isAvailable(siteCode);
	}

	public String getCompoundManagementTubeGuidByBarCodeAndSiteCode(String barCode, String siteCode) throws Exception {
		try {
			String tubeGuid =  CompoundManagementServiceFactory.getService().getTubesBySysTubeBarCodesAndSiteCode(barCode, siteCode);
			return tubeGuid;
		} catch (Exception error) {
			log.error("Error getting Tube GUID:", error);
			throw new CompoundManagementSubmissionException(error.getMessage(), error);
		}
	}
}
