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
package com.chemistry.enotebook.compoundmanagement.clean;

import com.chemistry.enotebook.compoundmanagement.CompoundManagementService;
import com.chemistry.enotebook.compoundmanagement.classes.*;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate.CompoundManagementPlateWell;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class CompoundManagementServiceCleanImpl implements CompoundManagementService {

	public CompoundManagementServiceCleanImpl() {
	}
	
	public boolean isAvailable(String siteCode) {
		return true;
	}

	public ArrayList<Properties> searchForCompoundManagementContainers(String containerName) throws CompoundManagementServiceException {
		return new ArrayList<Properties>();
	}

	public String getRegistrationNoFromBatch(String batchNo) throws CompoundManagementServiceException {
		return null;
	}

	
	//***** Methods for CompoundManagementContainerRegHandler *******// 
	
	public String getGlobalPlateBarCode(String barcodePrefix) throws CompoundManagementServiceException {
		return "";
	}

	public CompoundManagementBarCodeReg[] getPlateBarCodeRegList() throws CompoundManagementServiceException {
		CompoundManagementBarCodeReg barcode1 = new CompoundManagementBarCodeReg();
		return new CompoundManagementBarCodeReg[] {barcode1};
	}

	public List<String> getTubeBarcodesByDateAndSiteCode(Date startDate, Date endDate) throws CompoundManagementServiceException {
		return new ArrayList<String>();
	}

	public String getTubesBySysTubeBarCodesAndSiteCode(String barCode, String siteCode) throws CompoundManagementServiceException {
		return "";
	}

	public void registerNewPlate(CompoundManagementPlateWell[] compoundManagementPlateWells) throws CompoundManagementServiceException {
	}

	public void registerNewTubes(CompoundManagementPlateWell plateWell, String site) throws CompoundManagementServiceException {
	}

	public CompoundManagementContainer getContainer(String containerTypeCode) throws CompoundManagementServiceException {
		return new CompoundManagementContainer("", 1, 1, "X");
	}
	
	public List<CompoundManagementOrderDetail> findOrders(String orderId) throws CompoundManagementServiceException {
		List<CompoundManagementOrderDetail> details = new ArrayList<CompoundManagementOrderDetail>();
		return details;
	}

	public CompoundManagementCompoundBatch getCompoundBatch(String barcode) throws CompoundManagementServiceException {
		return new CompoundManagementCompoundBatch("",  50.d, "", "", "", "", 30.d, "", "");
	}

	public CompoundManagementPlate getPlate(String plateBarcode) throws CompoundManagementServiceException {
		CompoundManagementPlate compoundManagementPlate = new CompoundManagementPlate("", "", "", "");
		return compoundManagementPlate;
	}

	public BarcodeValidationVO validateBarcode(BarcodeValidationVO barcodeValidationVO) {
		return barcodeValidationVO;
	}
}
