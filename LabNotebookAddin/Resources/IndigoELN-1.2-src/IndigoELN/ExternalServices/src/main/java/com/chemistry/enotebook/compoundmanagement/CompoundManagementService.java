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
package com.chemistry.enotebook.compoundmanagement;

import com.chemistry.enotebook.compoundmanagement.classes.*;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate.CompoundManagementPlateWell;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Interface for external Compound Management service
 */
public interface CompoundManagementService {

	/**
	 * Check service health for given site code
	 * 
	 * @param siteCode
	 *            site code
	 * @return true if service available, false if service unavailable
	 */
	public boolean isAvailable(String siteCode);

	/**
	 * Search compound management containers list for given container name
	 * 
	 * @param containerName
	 *            container name
	 * @return Containers list: "CONTAINER_TYPE_CODE", "CONTAINER_TYPE_DESCR",
	 *         "MAJOR_AXIS", "SITE_CODE" and optional "X_POSITIONS",
	 *         "Y_POSITIONS", "RACK_X_POSITIONS", "RACK_Y_POSITIONS"
	 * @throws CompoundManagementServiceException
	 */
	public ArrayList<Properties> searchForCompoundManagementContainers(
			String containerName) throws CompoundManagementServiceException;

	/**
	 * Retrieve registration number for given batch number
	 * 
	 * @param batchNo
	 *            batch number
	 * @return Registration number
	 * @throws CompoundManagementServiceException
	 */
	public String getRegistrationNoFromBatch(String batchNo)
			throws CompoundManagementServiceException;

	/**
	 * Retrieve tube barcodes for given time interval
	 * 
	 * @param startDate
	 *            start of interval
	 * @param endDate
	 *            end of interval
	 * @return List of barcodes
	 * @throws CompoundManagementServiceException
	 */
	public List<String> getTubeBarcodesByDateAndSiteCode(Date startDate,
			Date endDate) throws CompoundManagementServiceException;

	/**
	 * Retrieve plate barcodes info
	 * 
	 * @return Plate barcodes info list
	 * @throws CompoundManagementServiceException
	 */
	public CompoundManagementBarCodeReg[] getPlateBarCodeRegList()
			throws CompoundManagementServiceException;

	/**
	 * Get global barcode for given prefix
	 * 
	 * @param barcodePrefix
	 *            prefix
	 * @return Global barcode
	 * @throws CompoundManagementServiceException
	 */
	public String getGlobalPlateBarCode(String barcodePrefix)
			throws CompoundManagementServiceException;

	/**
	 * Create new plate in service from given plate wells
	 * 
	 * @param compoundManagementPlateWells
	 *            plate wells to register
	 * @throws CompoundManagementServiceException
	 */
	public void registerNewPlate(
			CompoundManagementPlateWell[] compoundManagementPlateWells)
			throws CompoundManagementServiceException;

	/**
	 * Create new tubes from given plate well
	 * 
	 * @param plateWell
	 *            plate well
	 * @param site
	 *            site code
	 * @throws CompoundManagementServiceException
	 */
	public void registerNewTubes(CompoundManagementPlateWell plateWell,
			String site) throws CompoundManagementServiceException;

	/**
	 * Retrieve Tube GUID using given barcode and site code
	 * 
	 * @param barCode
	 *            barcode
	 * @param siteCode
	 *            site code
	 * @return Tube GUID
	 * @throws CompoundManagementServiceException
	 */
	public String getTubesBySysTubeBarCodesAndSiteCode(String barCode,
			String siteCode) throws CompoundManagementServiceException;

	/**
	 * Retrieve container for given type code
	 * 
	 * @param containerTypeCode
	 *            type code
	 * @return Containser
	 * @throws CompoundManagementServiceException
	 */
	public CompoundManagementContainer getContainer(String containerTypeCode)
			throws CompoundManagementServiceException;

	/**
	 * Search for orders using given order ID
	 * 
	 * @param orderId
	 *            order ID
	 * @return Orders
	 * @throws CompoundManagementServiceException
	 */
	public List<CompoundManagementOrderDetail> findOrders(String orderId)
			throws CompoundManagementServiceException;

	/**
	 * Retrieve plate for given barcode
	 * 
	 * @param plateBarcode
	 *            barcode
	 * @return Plate
	 * @throws CompoundManagementServiceException
	 */
	public CompoundManagementPlate getPlate(String plateBarcode)
			throws CompoundManagementServiceException;

	/**
	 * Retrieve batch for given barcode
	 * 
	 * @param barcode
	 *            barcode
	 * @return Batch
	 * @throws CompoundManagementServiceException
	 */
	public CompoundManagementCompoundBatch getCompoundBatch(String barcode)
			throws CompoundManagementServiceException;

	/**
	 * Check if barcode is valid
	 * 
	 * @param barcodeValidationVO
	 *            barcode with parameters
	 * @return Barcode validation status
	 */
	public BarcodeValidationVO validateBarcode(
			BarcodeValidationVO barcodeValidationVO);
}
