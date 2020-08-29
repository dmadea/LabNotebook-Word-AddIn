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
package com.chemistry.enotebook.delegate;

import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanel;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanelSelector;
import com.chemistry.enotebook.compoundaggregation.exceptions.CompoundAggregationServiceException;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.interfaces.RegistrationManagerRemote;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.vnv.classes.IVnvResult;

import java.util.List;
import java.util.Map;

public class RegistrationManagerDelegate implements RegistrationManagerRemote {

	private RegistrationManagerRemote registrationManagerRemote;

	public RegistrationManagerDelegate() {
		registrationManagerRemote = ServiceLocator.getInstance().locateService("RegistrationManager", RegistrationManagerRemote.class);
	}

	@Override
	public Map<String, String> submitPlatesForRegistration(ProductPlate[] pPlates, String compoundManagementEmployeeID, NotebookPageModel pageModel) throws Exception {
		return registrationManagerRemote.submitPlatesForRegistration(pPlates, compoundManagementEmployeeID, pageModel);
	}

	@Override
	public Map<String, String> submitBatchesForRegistration(ProductBatchModel[] pBatches, String userID, NotebookPageModel pageModel) throws Exception {
		return registrationManagerRemote.submitBatchesForRegistration(pBatches, userID, pageModel);
	}

	@Override
	public ProductBatchModel[] getRegistrationInformation(ProductBatchModel[] batches) throws Exception {
		return registrationManagerRemote.getRegistrationInformation(batches);
	}

	@Override
	public Map<String, JobModel> submitPlatesToCompoundAggregation(String userID, ProductPlate[] pPlates, NotebookPageHeaderModel pageHeader, String siteCode) throws Exception {
		return registrationManagerRemote.submitPlatesToCompoundAggregation(userID, pPlates, pageHeader, siteCode);
	}

	@Override
	public String[] getScreenPanelsNames(long[] screenPanelIDs) throws Exception {
		return registrationManagerRemote.getScreenPanelsNames(screenPanelIDs);
	}

	@Override
	public Map<String, JobModel> submitBatchesToCompoundAggregation(String userID, ProductBatchModel[] batches, NotebookPageHeaderModel pageHeader, String siteCode) throws Exception {
		return registrationManagerRemote.submitBatchesToCompoundAggregation(userID, batches, pageHeader, siteCode);
	}

	@Override
	public Map<String, JobModel> submitPlatesToCompoundManagement(ProductPlate[] plates, String siteCode) throws Exception {
		return registrationManagerRemote.submitPlatesToCompoundManagement(plates, siteCode);
	}

	@Override
	public Map<String, String> submitTubesToCompoundManagement(PlateWell<ProductBatchModel>[] compContainers, String siteCode) throws Exception {
		return registrationManagerRemote.submitTubesToCompoundManagement(compContainers, siteCode);
	}

	@Override
	public CompoundManagementBarcodePrefixInfo[] getCompoundManagementBarcodePrefixes(String containerType, String siteCode) throws Exception {
		return registrationManagerRemote.getCompoundManagementBarcodePrefixes(containerType, siteCode);
	}

	@Override
	public String[] getNewGBLPlateBarCodesFromCompoundManagement(String barcodePrefix, int noOfBarcodes, String siteCode) throws Exception {
		return registrationManagerRemote.getNewGBLPlateBarCodesFromCompoundManagement(barcodePrefix, noOfBarcodes, siteCode);
	}

	@Override
	public Map<String, JobModel> submitPlatesForPurification(String userID, String siteCode, ProductPlate[] plates) throws Exception {
		return registrationManagerRemote.submitPlatesForPurification(userID, siteCode, plates);
	}

	@Override
	public Map<String, JobModel> submitTubesForPurification(String userName, String siteCode, String pageKey, PlateWell<ProductBatchModel>[] tubes) throws Exception {
		return registrationManagerRemote.submitTubesForPurification(userName, siteCode, pageKey, tubes);
	}

	@Override
	public boolean isCompoundAggregationApiAvailable(String siteCode) throws Exception {
		return registrationManagerRemote.isCompoundAggregationApiAvailable(siteCode);
	}

	@Override
	public boolean isPurificationServiceApiAvailable() throws Exception {
		return registrationManagerRemote.isPurificationServiceApiAvailable();
	}

	@Override
	public boolean isCompoundManagementContainerApiAvailable(String siteCode) throws Exception {
		return registrationManagerRemote.isCompoundManagementContainerApiAvailable(siteCode);
	}

	@Override
	public String submitBatchesAndPlatesForRegistration(String[] batchKeys, String[] plateKeys, String[] workflows, NotebookPageModel pageModel) throws Exception {
		return registrationManagerRemote.submitBatchesAndPlatesForRegistration(batchKeys, plateKeys, workflows, pageModel);
	}

	@Override
	public String getCompoundManagementTubeGuidByBarCodeAndSiteCode(String barCode, String siteCode) throws Exception {
		return registrationManagerRemote.getCompoundManagementTubeGuidByBarCodeAndSiteCode(barCode, siteCode);
	}

	@Override
	public IVnvResult validateStructureAssignStereoIsomerCode(String molStructure) throws Exception {
		return registrationManagerRemote.validateStructureAssignStereoIsomerCode(molStructure);
	}

	@Override
	public IVnvResult validateStructureWithStereoIsomerCode(String molStructure, String inputSic) throws Exception {
		return registrationManagerRemote.validateStructureWithStereoIsomerCode(molStructure, inputSic);
	}

	@Override
	public List<String> getPurificationArchivePlateChoice() throws Exception {
		return registrationManagerRemote.getPurificationArchivePlateChoice();
	}

	@Override
	public List<String> getLabsForPurification(String siteCode) throws Exception {
		return registrationManagerRemote.getLabsForPurification(siteCode);
	}

	@Override
	public List<String> getPurificationSampleWorkup() throws Exception {
		return registrationManagerRemote.getPurificationSampleWorkup();
	}

	@Override
	public ScreenPanelSelector getScreenPanelDialog() throws CompoundAggregationServiceException {
		return registrationManagerRemote.getScreenPanelDialog();
	}

	@Override
	public ScreenPanel getScreenPanel(long screenKey) throws CompoundAggregationServiceException {
		return registrationManagerRemote.getScreenPanel(screenKey);
	}
}
