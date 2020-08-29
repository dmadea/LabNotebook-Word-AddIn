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
package com.chemistry.enotebook.test;

import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.LoginException;
import com.chemistry.enotebook.handler.CompoundManagementHandler;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.delegate.SessionTokenInitException;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;

import java.util.List;
import java.util.Map;

public class TestRegistrationManagerDelegate {
	static SessionIdentifier sidentifier = null;

	static RegistrationManagerDelegate regManager = null;

	public void testSubmitPlatesForRegistration(ProductPlate[] plates, NotebookPageModel pageModel) throws Exception {
		Map<String, String> map = regManager.submitPlatesForRegistration(plates, "G00001273", pageModel);
		System.out.println("Result of PLATE[] CompoundRegistration Submission :" + map);
	}

	public void testSubmitPlatesForCompoundManagementRegistration(ProductPlate[] plates) throws Exception {

		CompoundManagementBarcodePrefixInfo[] prefixes = regManager.getCompoundManagementBarcodePrefixes("PLATE", "SITE1");
		System.out.println("Prefixes :" + prefixes.length);
		if (prefixes.length > 0) {
			String[] barcodes = regManager.getNewGBLPlateBarCodesFromCompoundManagement("GALT", 2, "SITE1");// prefixes[0].getPrefix(), 2, "SITE1");
			System.out.println("Bar Codes :" + barcodes.length + " barcode:" + barcodes[0]);
			System.out.println(plates[0].getPlateBarCode());
			plates[0].setPlateBarCode(barcodes[0]);

			Map<String, JobModel> map = regManager.submitPlatesToCompoundManagement(plates, "SITE1");
			System.out.println("Result of CompoundManagementRegistration :" + map);
		}

	}

	public void testSubmitPlatesForCompoundManagementRegistrationWithCompoundManagementHandler(ProductPlate[] plates) throws Exception {

		CompoundManagementHandler compoundManagement = new CompoundManagementHandler();
		CompoundManagementBarcodePrefixInfo[] prefixes = compoundManagement.getCompoundManagementBarcodePrefixes("PLATE", "SITE1");
		System.out.println("Prefixes :" + prefixes.length);
		if (prefixes.length > 0) {
			String[] barcodes = compoundManagement.getNewGBLPlateBarCodesFromCompoundManagement("GALT", 2, "SITE1");// prefixes[0].getPrefix(),
																										// 2, "SITE1");
			System.out.println("Bar Codes :" + barcodes.length + " barcode:" + barcodes[0]);
			System.out.println(plates[0].getPlateBarCode());
			plates[0].setPlateBarCode(barcodes[0]);

			Map<String, JobModel> map = compoundManagement.submitPlatesForCompoundManagementRegistration(plates, "SITE1");
			System.out.println("Result of CompoundManagementRegistration :" + map);
		}

	}

	public void testSubmitPlatesForPurification(ProductPlate[] plates, SessionIdentifier sidentifier) throws Exception {
//		PurificationServiceHandler handler = new PurificationServiceHandler();
		// Map map = handler.submitPlatesForPurification("SITE1", plates, sidentifier);
		Map<String, JobModel> map = regManager.submitPlatesForPurification(sidentifier.getUserID(), "SITE1", plates);

		System.out.println("Result of PLATE PurificationService Purification :" + map);
	}

	public void testSubmitBatchesForRegistration(ProductBatchModel[] pBatches, NotebookPageModel pageModel) throws Exception {
		Map<String, String> map = regManager.submitBatchesForRegistration(pBatches, "G00001273", pageModel);
		System.out.println("Result of BATCH[] CompoundManagementRegistration :" + map);
	}

	public void testSubmitTubesForPurification(PlateWell<ProductBatchModel>[] tubes) throws Exception {
		Map<String, JobModel> map = regManager.submitTubesForPurification(sidentifier.getUserID(), "SITE1", "pageKeyTest", tubes);
		System.out.println("Result of PLATE-WELL Purification :" + map);
	}

	public void testSubmitPlatesToCompoundAggregation(ProductPlate[] pPlates, NotebookPageHeaderModel pageHeader, SessionIdentifier sidentifier)
			throws Exception {
		// CompoundAggregationHandler handler = new CompoundAggregationHandler(sidentifier);
		// Map map = handler.submitPlatesToCompoundAggregation(pPlates, pageHeader);
		Map<String, JobModel> map = regManager.submitPlatesToCompoundAggregation(sidentifier.getUserID(), pPlates, pageHeader, pageHeader.getSiteCode());
		System.out.println("Result of PLATE CompoundAggregation Submission :" + map.size());
	}

	public void testSubmitBatchesToCompoundAggregation(ProductBatchModel[] pBatches, NotebookPageHeaderModel pageHeader) throws Exception {
		Map<String, JobModel> map = regManager.submitBatchesToCompoundAggregation(sidentifier.getUserID(),pBatches, pageHeader, pageHeader.getSiteCode());
		System.out.println("Result of BATCH CompoundAggregation Submission :" + map);
	}

	public static void main(String[] str) throws Exception {
		TestRegistrationManagerDelegate delegate = new TestRegistrationManagerDelegate();

		NotebookRef nbref = new NotebookRef();
		nbref.setNbNumber("87654321");
		nbref.setNbPage("0135");
		sidentifier = new SessionIdentifier("SITE1", cenApplicationUser, "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
		HttpUserProfile user = new HttpUserProfile("http://com:65432");
		sidentifier.setUserProfile(user);
		sidentifier.setPassword(cenApplicationPassword);
		regManager = new RegistrationManagerDelegate();

		// CompoundAggregationHandler compoundAggregation = new CompoundAggregationHandler(sidentifier);

		try {
			new SessionTokenDelegate();
		} catch (SessionTokenInitException e) {
			throw new LoginException(e.getMessage(), e);
		}

		// NotebookPageModel page = loadNotebookPageThruDelegate(nbref);//loadNBKPageOnServer(nbref);

		// ProductPlate[] plates = delegate.getPlates(page);
		// delegate.testSubmitPlatesForRegistration(delegate.getPlates(page),page.getPageHeader());//97863526-0068
		// delegate.testSubmitBatchesForRegistration(delegate.getBatches(page),page.getPageHeader());//97863526-0068
		// delegate.testSubmitPlatesForCompoundManagementRegistration(plates);
		// delegate.testSubmitPlatesForPurification(plates,sidentifier);
		// delegate.testSubmitPlateWellsForPurification(plates);
		// delegate.testSubmitPlatesToCompoundAggregation(plates, page.getPageHeader(),sidentifier);
		// delegate.testSubmitBatchesToCompoundAggregation(delegate.getBatches(page), page.getPageHeader());

		// Test for CompoundAggregation services initialization. No need for real data.
		// delegate.testSubmitPlatesToCompoundAggregation(new ProductPlate[]{}, null,sidentifier);

		// Test Tube submissions
		//delegate.testSubmitPlatesForCompoundManagementRegistrationWithCompoundManagementHandler(nbref);
		delegate.testSubmitPlatesForCompoundManagementRegWithRegManager(nbref);
	}

	public ProductPlate[] getPlates(NotebookPageModel page) throws Exception {
		ProductPlate[] plates = null;
		for (int i = 0; i < page.getReactionSteps().size(); i++) {
			ReactionStepModel step = page.getReactionStep(i);
			List<ProductPlate> list = step.getProductPlates();
			System.out.println("Plates count :" + list.size());
			if (list.size() == 0)
				continue;
			plates = new ProductPlate[1];
			plates[0] = (ProductPlate) list.get(0);
			PlateWell<ProductBatchModel>[] wells = plates[0].getWells();
			for (int j = 0; j < wells.length; j++) {
				if (wells[j].getSolventCode() == null)
					wells[j].setSolventCode("DMSO");
			}
			System.out.println("Batches in plate :" + plates[0].getAllBatchesInThePlate().length);
			break;
		}
		return plates;
	}

	public ProductBatchModel[] getBatches(NotebookPageModel page) throws Exception {
		ProductBatchModel[] batches = null;
		for (int i = 0; i < page.getReactionSteps().size(); i++) {
			ReactionStepModel step = page.getReactionStep(i);
			List<ProductPlate> list = step.getProductPlates();
			System.out.println("Plates count :" + list.size());
			if (list.size() == 0) {
				List<ProductBatchModel> batcheslist = step.getAllProductBatchModelsInThisStep();
				batches = new ProductBatchModel[batcheslist.size()];
				for (int j = 0; j < batcheslist.size(); j++) {
					batches[j] = (ProductBatchModel) batcheslist.get(j);
				}
				if (batches.length != 0)
					break;
			}
		}
		return batches;
	}

//	public static NotebookPageModel loadNBKPageOnServer(NotebookRef nbref) throws Exception {
//		DAOFactory factory = DAOFactoryManager.getDAOFactory();
//		NotebookLoadDAO dao = factory.getNotebookLoadDAO();
//		NotebookPageModel page = dao.loadNotebookPage(nbref);
//		return page;
//	}

	public static NotebookPageModel loadNotebookPageThruDelegate(NotebookRef nbref) throws Exception {
		SessionIdentifier sidentifier = new SessionIdentifier("SITE1", cenApplicationUser, "token_string",
				true);

		HttpUserProfile userProfile = new HttpUserProfile("http://localhost:65432");
		sidentifier.setUserProfile(userProfile);

		StorageDelegate ssi = new StorageDelegate();
		long startTime = System.currentTimeMillis();
		NotebookPageModel page = ssi.getNotebookPageExperimentInfo(nbref, sidentifier.getSiteCode(),null);// getNotebookPageExperimentInfo(nbref,
																										// sidentifier);
		System.out.println(System.currentTimeMillis() - startTime + " ms elapsed for [ NotebookPage Load " + nbref + "]");
		System.out.println("Loaded Page " + page.getUserName() + " : " + page.getNbRef().getNbRef());

		// CommonUtils.printNBPageData(page);
		return page;
	}

	public PlateWell<ProductBatchModel>[] getRackTubePlateWells(NotebookPageModel page) throws Exception {
		ProductPlate plate = page.getGuiPseudoProductPlate();
		PlateWell<ProductBatchModel>[] wells = plate.getWells();
		for (int j = 0; j < wells.length; j++) {
			//for solubilized tube ( also need volume info
			if (wells[j].getSolventCode() == null)
			{
			wells[j].setSolventCode("DMSO");
			wells[j].getContainedVolumeAmount().setValue(1.23);
			}
			//for Dry tube
			else
			{
				wells[j].getContainedWeightAmount().setValue(3.3);	
			}
			wells[j].setBarCode("TM0001107225");
			wells[j].setContainerTypeCode("2DT");
			wells[j].setLocationCode("GCOE");
			

		}

		return wells;
	}

	public void testSubmitPlatesForCompoundManagementRegistrationWithCompoundManagementHandler(NotebookRef nbref) {

		CompoundManagementHandler compoundManagement = new CompoundManagementHandler();
		try {
			NotebookPageModel model = TestRegistrationManagerDelegate.loadNotebookPageThruDelegate(nbref);
			PlateWell<ProductBatchModel> wells[] = this.getRackTubePlateWells(model);
			compoundManagement.submitTubesForCompoundManagementRegistration(wells, "SITE1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSubmitPlatesForCompoundManagementRegWithRegManager(NotebookRef nbref) {

		
		try {
			NotebookPageModel model = TestRegistrationManagerDelegate.loadNotebookPageThruDelegate(nbref);
			PlateWell<ProductBatchModel> wells[] = this.getRackTubePlateWells(model);
			regManager.submitTubesToCompoundManagement(wells, "SITE1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static String cenApplicationUser = "USER";
	static String cenApplicationPassword = "#";

}
