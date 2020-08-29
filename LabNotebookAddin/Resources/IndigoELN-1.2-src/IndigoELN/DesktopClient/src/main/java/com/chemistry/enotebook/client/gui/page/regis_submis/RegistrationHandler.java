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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationVialDetailsVO;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.exceptions.RegistrationManagerInitException;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.common.units.UnitCache;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.utils.SDFileGeneratorUtil;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistrationHandler {
	
	public static final Log log = LogFactory.getLog(RegistrationHandler.class);
	
	private NotebookPageModel pageModel;

	private List<String> workflowList = new ArrayList<String>();
	
	public RegistrationHandler(NotebookPageModel pageModel, List<String> workflowList) {
		 this(pageModel);
		 this.workflowList  = workflowList;
	}
	
	public RegistrationHandler(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}

	/**
	 * Validate batches for CompoundRegistration registration.
	 * @param batchArray
	 * @return error map.  Empty if no errors.
	 */
	public LinkedHashMap<ProductBatchModel, String> validateBatchesForRegistration(ProductBatchModel[] batchArray, LinkedHashMap<ProductBatchModel, String> errorMap) {
		//LinkedHashMap errorMap = new LinkedHashMap();
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validateBatchesForRegistration(batchArray, errorMap);
		return errorMap;
	}
	
	/**
	 * Validate batches for CompoundRegistration registration.
	 * @param batchArray
	 * @return error map.  Empty if no errors.
	 */
	public LinkedHashMap<ProductBatchModel, String> validatePlatesForRegistration(ProductPlate[] plateArray, 
	                                                                              LinkedHashMap<ProductBatchModel, String> errorMap) {
		//LinkedHashMap errorMap = new LinkedHashMap();
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validatePlatesForRegistration(plateArray, errorMap);
		return errorMap;
	}
	
	/**
	 * Submit batches for registration to CompoundRegistration.  
	 * @param batchArray
	 * @param employeeID
	 * @return
	 */
	public Map<String, String> submitBatchesForRegistration(ProductBatchModel[] batchArray, String employeeID) throws Exception {
		Map<String, String> batchesMap  = null;
		batchesMap = getRegistrationManagerDelegate().submitBatchesForRegistration(batchArray, employeeID, pageModel);
		// Batches registered with out any exceptions.
		setBatchStatus(batchArray, "REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
		//pageModel.setEditable(false);//The project code and all other project specific details can not be changed once single batch is Registered. 
		return batchesMap;
	}

	/**
	 * Submit plates for registration to CompoundRegistration.  
	 * @param batchArray
	 * @param employeeID
	 * @return
	 */
	public Map<String, String> submitPlatesForRegistration(ProductPlate[] plateArray, String employeeID) throws Exception {
		Map<String, String> batchesMap  = null;
		batchesMap = getRegistrationManagerDelegate().submitPlatesForRegistration(plateArray, employeeID, pageModel);
		// Batches registered with out any exceptions.
		setBatchStatus(plateArray, "REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
		//pageModel.setEditable(false);//The project code and all other project specific details can not be changed once single batch is Registered. 
		return batchesMap;
	}
	
	
	/**
	 * Invoke the validator for plate CompoundManagement registration.
	 * @param plateArray
	 * @param errorMap 
	 * @return error map, empty if no errors.
	 */
	public LinkedHashMap<ProductBatchModel, String> validatePlatesForCompoundManagementRegistration(ProductPlate[] plateArray, 
	                                                                                 LinkedHashMap<ProductBatchModel, String> errorMap) {
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validatePlatesForPlateRegistration(plateArray, errorMap);
		return errorMap;
	}
	
	/**
	 * Prepare the wells for CompoundManagement registration and then submit the plates.
	 * @param productPlates
	 * @return
	 */
	public String submitPlatesForCompoundManagementRegistration(ProductPlate[] productPlates, String projectCode) {
		String serverErrorMessage = "";
		try {
			// Before submitting plate for registration, we need to transfer the batch tracking id from
			// the batch reg info and set a solvent code.  The solvent code is not used, but if it is null
			// the plate registration will fail.
			for (int i=0; i<productPlates.length; i++) {
				ProductPlate plate = productPlates[i];
				plate.setProjectTrackingCode(projectCode);
				PlateWell<ProductBatchModel>[] wells =  plate.getWells();
				for (int j=0; j<wells.length; j++) {
					PlateWell<ProductBatchModel> well = wells[j];
					ProductBatchModel batch = well.getBatch();
					if (batch == null)
						continue;
					long batchTrackingId = batch.getRegInfo().getBatchTrackingId();
					well.setBatchTrackingId(batchTrackingId);
				}
			}
			
			Map<String, JobModel> productPlatesMap = getRegistrationManagerDelegate().submitPlatesToCompoundManagement(productPlates,
			                                                                                                         pageModel.getSiteCode());
			
			for (int i=0; i<productPlates.length; i++) {
				ProductPlate plate = productPlates[i];
				String plateKey = plate.getKey();
				JobModel resultJobModel = productPlatesMap.get(plateKey);
				String status = resultJobModel.getCompoundManagementStatus();
				plate.setCompoundManagementRegistrationSubmissionStatus(status);
				String errorMsg = resultJobModel.getCompoundManagementStatusMessage();
				plate.setCompoundManagementRegistrationSubmissionMessage(errorMsg);
				//updateBatchesWithStatus(1, plate, status, errorMsg); We may need it later. Do not remove.
			}
			
		} catch (Exception e) {
			serverErrorMessage = "Submit to registration of product plates failed due to : \n";
			serverErrorMessage += e.getMessage() + "\n";
			log.error(serverErrorMessage, e);
		}
		return serverErrorMessage;
	}
	
	private void updateBatchesWithStatus(int workFlow, ProductPlate plate, String status, String errorMsg) {
		final int compoundManagement = 1;
		final int PurificationService = 2;
		final int compoundAggregation = 3;
		ProductBatchModel[] productBatchModels = plate.getAllBatchesInThePlate();
		for (int j=0; j<productBatchModels.length; j++)
		{
			switch (workFlow)
			{
			case compoundManagement:
				productBatchModels[j].getRegInfo().setCompoundManagementStatus(status);
				productBatchModels[j].getRegInfo().setCompoundManagementStatusMessage(errorMsg);
				break;
			case PurificationService:
				productBatchModels[j].getRegInfo().setPurificationServiceStatus(status);
				productBatchModels[j].getRegInfo().setPurificationServiceStatusMessage(errorMsg);
				break;
			case compoundAggregation:
				productBatchModels[j].getRegInfo().setCompoundAggregationStatus(status);
				productBatchModels[j].getRegInfo().setCompoundAggregationStatusMessage(errorMsg);
				break;
			}
			
		}
	}

	/**
	 * Validate batches for CompoundRegistration registration.
	 * @param errorMap 
	 * @param batchArray
	 * @return error map.  Empty if no errors.
	 */
	public LinkedHashMap<ProductBatchModel, String> validatePlatesForPurification(ProductPlate[] productPlates, LinkedHashMap<ProductBatchModel, String> errorMap) {
		//LinkedHashMap errorMap = new LinkedHashMap();
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validatePlatesForPurification(productPlates, errorMap);
		return errorMap;
	}
	

	/**
	 * Submit plates to PurificationService purification
	 * @param productPlates
	 * @return
	 */
	public String submitPlatesForPurification(ProductPlate[] productPlates) {
		String serverErrorMessage = "";
		try {
			Map<String, JobModel> productPlatesMap = (Map<String, JobModel>) getRegistrationManagerDelegate().submitPlatesForPurification(pageModel.getUserName(),
			                                                                                                                              pageModel.getSiteCode(),
			                                                                                                                              productPlates);
			for (int i=0; i<productPlates.length; i++) {
				ProductPlate plate = productPlates[i];
				String plateKey = plate.getKey();
				JobModel resultJobModel = (JobModel)productPlatesMap.get(plateKey);
				String status = resultJobModel.getPurificationServiceStatus();
				plate.setPurificationSubmissionStatus(status);
				String errorMsg = resultJobModel.getPurificationServiceStatusMessage();
				plate.setPurificationSubmissionMessage(errorMsg);
				//updateBatchesWithStatus(2, plate, status, errorMsg); We may need it later.
			}
		} catch (Exception e) {
			serverErrorMessage = "Submit of product plates to purification failed due to : \n";
			serverErrorMessage += e.getMessage() + "\n";
			log.error(serverErrorMessage, e);
		}
		return serverErrorMessage;
	}
	
	
	/**
	 * Validate batches for PurificationService.  TODO this probably needs a check for bar code.  
	 * @param batchArray
	 * @param errorMap 
	 * @return error map.  Empty if no errors.
	 */
	public LinkedHashMap<ProductBatchModel, String> validateBatchesForPurification(ProductBatchModel[] batchArray, LinkedHashMap<ProductBatchModel, String> errorMap) {
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validateBatchesForPurification(batchArray, errorMap);
		return errorMap;
	}
	
	/**
	 * 
	 * @param tubes
	 * @param errorMap
	 * @return
	 */
	public LinkedHashMap<ProductBatchModel, String> validateTubesForPurification(List<PlateWell<ProductBatchModel>> tubes, 
	                                                                             LinkedHashMap<ProductBatchModel, String> errorMap) {
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validateTubesForPurification(tubes, errorMap);
		return errorMap;
	}
	
	/**
	 * 
	 * @param tube
	 * @param errorMap
	 * @return
	 */
	public LinkedHashMap<ProductBatchModel, String> validateTubeForPurification(PlateWell<ProductBatchModel> tube, 
	                                                                            LinkedHashMap<ProductBatchModel, String> errorMap) {
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validateTubeForPurification(tube, errorMap);
		return errorMap;
	}
	
	/**
	 * Submit individual batches in wells/tubes or vials for PurificationService purification.
	 * @param wells
	 * @return
	 */
	public String submitTubesForPurification(PlateWell<ProductBatchModel>[] wells) {
		String serverErrorMessage = "";
		try {
			Map<String, JobModel> map = getRegistrationManagerDelegate().submitTubesForPurification(pageModel.getUserName(), pageModel.getSiteCode(), pageModel.getKey(), wells);
			for(String key : map.keySet()) {
				JobModel result = map.get(key);
				String purificationServiceStatus = result.getPurificationServiceStatus();
				if (purificationServiceStatus.equalsIgnoreCase(CeNConstants.REGINFO_PASSED) || purificationServiceStatus.equalsIgnoreCase("Submitted - PASSED"))
					setBatchStatus(wells, "BATCH PURIFICATION",	CeNConstants.REGINFO_SUBMITTED,	CeNConstants.REGINFO_PASSED);
				else // There is no "pending" status for plate registration
					setBatchStatus(wells, "BATCH PURIFICATION",	CeNConstants.REGINFO_SUBMITTED,	CeNConstants.REGINFO_FAILED);
			}
		} catch (Exception e) {
			serverErrorMessage = "Submit to purification of batches failed due to : \n" + e.getMessage() + "\n";
			log.error(serverErrorMessage, e);
		}

		return serverErrorMessage;
	}
	
	/**
	 * 
	 * @param productPlates
	 * @param errorMap 
	 * @return
	 */
	public LinkedHashMap<ProductBatchModel, String> validatePlatesForScreening(ProductPlate[] productPlates, LinkedHashMap<ProductBatchModel, String> errorMap) {
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validatePlatesForScreening(productPlates, errorMap);
		return errorMap;		
	}
	
	public String submitPlatesForScreening(ProductPlate[] productPlates) {
		String serverErrorMessage = "";
		try {
			Map<String, JobModel> productPlatesMap = (Map<String, JobModel>) getRegistrationManagerDelegate().submitPlatesToCompoundAggregation(pageModel.getUserName(),
			                                                                                                                     productPlates, 
			                                                                                                                     pageModel.getPageHeader(),
			                                                                                                                     pageModel.getSiteCode());
			try {
				for (int i=0; i<productPlates.length; i++) {
					ProductPlate plate = productPlates[i];
					JobModel resultJobModel = productPlatesMap.get(plate.getKey());
					String status = resultJobModel.getCompoundAggregationStatus();
					plate.setScreenPanelsSubmissionStatus(status);
					ProductBatchModel[] batchModels = plate.getAllBatchesInThePlate();
					for (int j=0; j<batchModels.length; j++)
					{
						batchModels[j].getRegInfo().setCompoundAggregationStatus(status);
						String errorMsg = resultJobModel.getCompoundAggregationStatusMessage();
						batchModels[j].getRegInfo().setCompoundAggregationStatusMessage(errorMsg); //Reset the previous error status messages.
					}
					if (status.equalsIgnoreCase(CeNConstants.REGINFO_PASSED) || status.equalsIgnoreCase("Submitted - PASSED"))
						setBatchStatus(productPlates, "SCREENING",	CeNConstants.REGINFO_SUBMITTED,	CeNConstants.REGINFO_PASSED);
					else // There is no "pending" status for plate registration
						setBatchStatus(productPlates, "SCREENING",	CeNConstants.REGINFO_SUBMITTED,	CeNConstants.REGINFO_FAILED);
				}
			}
			catch(Exception e)
			{
				serverErrorMessage = "Could not get the return status of submission. Please try again later.";
			}

		} catch (Exception e) {
			serverErrorMessage = "Submit of product plates to screening failed due to : \n";
			serverErrorMessage += e.getMessage() + "\n";
			log.error(serverErrorMessage, e);
		}	
		return serverErrorMessage;
	}
	
	/**
	 * 
	 * @param batches
	 * @param errorMap 
	 * @return
	 */
	public LinkedHashMap<ProductBatchModel, String> validateBatchesForScreening(ProductBatchModel[] batches, LinkedHashMap<ProductBatchModel, String> errorMap) {
		//LinkedHashMap errorMap = new LinkedHashMap();
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validateBatchesForScreening(batches, errorMap);
		return errorMap;		
	}


	/**
	 * Submit batches to CompoundAggregation
	 * @param batches
	 * @return
	 */
	public String submitBatchesForScreening(ProductBatchModel[] batches) {
		String serverErrorMessage = "";
		try {
			Map<String, JobModel> map = (Map<String, JobModel>) getRegistrationManagerDelegate().submitBatchesToCompoundAggregation(pageModel.getUserName(),
			                                                                                                                 batches, 
			                                                                                                                 pageModel.getPageHeader(),
																															 pageModel.getSiteCode());
			try {
				for (int j=0; j<batches.length; j++)
				{
					String batchKey = batches[j].getKey();
					JobModel resultJobModel = (JobModel)map.get(batchKey);
					String status = resultJobModel.getCompoundAggregationStatus();
					batches[j].getRegInfo().setCompoundAggregationStatus(status);
					String errorMsg = resultJobModel.getCompoundAggregationStatusMessage();
					batches[j].getRegInfo().setCompoundAggregationStatusMessage(errorMsg); //Reset the previous error status messages.
				}
			}
			catch(Exception e)
			{
				serverErrorMessage = "Could not get the return status of submission. Please try again later.";
			}
/*			while (pit.hasNext()) {
				String key = (String) pit.next();
				String result = (String) map.get(key);
				if (result.equalsIgnoreCase(CeNConstants.REGINFO_PASSED) || result.equalsIgnoreCase("Submitted - PASSED"))
					setBatchStatus(batches, "BATCH SCREENING",	CeNConstants.REGINFO_SUBMITTED,	CeNConstants.REGINFO_PASSED);
				else // There is no "pending" status for plate registration
					setBatchStatus(batches, "BATCH SCREENING",	CeNConstants.REGINFO_SUBMITTED,	CeNConstants.REGINFO_FAILED);
			}
*/		} catch (Exception e) {
			serverErrorMessage += "Submit to purification of batches failed due to : \n";
			serverErrorMessage += e.getMessage() + "\n";
			log.error(serverErrorMessage, e);
		}

		return serverErrorMessage;
	}
	
	/**
	 * Updates CompoundRegistration registration status given batches
	 * @param batches
	 * @return
	 */
	public String getRegistrationStatusForBatches(ProductBatchModel[] batches) {
		log.info("getRegistrationStatusForBatches(ProductBatchModel[]) start");
		
		int count = 0;
		
		if (batches != null) {
			List<String> batchNos = new ArrayList<String>();
		
			for (ProductBatchModel batch : batches) {
				if (batch != null && StringUtils.isNotBlank(batch.getBatchNumberAsString())) {
					batchNos.add(batch.getBatchNumberAsString());
				}
			}
		
			try {
				SessionTokenDelegate smObj = new SessionTokenDelegate();
				RegistrationServiceDelegate registrationServiceDelegate = new RegistrationServiceDelegate();		
				
				Map<String, RegistrationDetailsVO> resultMap = registrationServiceDelegate.getRegistrationInformation(batchNos.toArray(new String[0]));
				
				for (ProductBatchModel iBatch : batches) {
					if (iBatch != null) {
						RegistrationDetailsVO result = resultMap.get(iBatch.getBatchNumberAsString());
     					if (result != null) {
     						iBatch.setParentBatchNumber(result.getGlobalNumber());
//     						iBatch.getCompound().setRegNumber(result.getGlobalCompoundNumber());
     						iBatch.setConversationalBatchNumber(result.getGlobalCompoundNumber());
     						iBatch.getRegInfo().setConversationalBatchNumber(result.getGlobalCompoundNumber());
     						
     						String batchOwner = smObj.getNtUserNameByCompoundManagementEmployeeId(result.getBatchOwnerId());
     						iBatch.getRegInfo().setOwner(batchOwner);
     						
     						iBatch.getRegInfo().setBatchTrackingId(Long.parseLong(result.getBatchTrackingNo()));
         					iBatch.setComments(result.getBatchComment());
         					iBatch.getRegInfo().setCompoundSource(result.getSource());
         					iBatch.getRegInfo().setCompoundSourceDetail(result.getSourceDetail());
         					iBatch.getRegInfo().setRegistrationDate(result.getLoadDate());
     						iBatch.getRegInfo().setStatus(CeNConstants.REGINFO_PASSED);
     						iBatch.getRegInfo().setErrorMsg("");
     						iBatch.getRegInfo().setRegistrationStatus(CeNConstants.REGINFO_REGISTERED);
     						
     						String unit = result.getAmountMadeUnit();
     						if (unit == null || unit.length() == 0) unit = "MG";
     						AmountModel newAmount = new AmountModel(UnitCache.getInstance().getUnit(unit).getType(), result.getAmountMade());
     						newAmount.setUnit(iBatch.getWeightAmount().getUnit());
     						newAmount.setSigDigits(iBatch.getWeightAmount().getSigDigits());
    						iBatch.setWeightAmount(newAmount);
     						
    						BatchAttributeComponentUtility.setTotalAmountMadeWeight(iBatch, newAmount, null);
    						
     						// Check Structure, isomer code, Structure Comment
     						try {
	   							CompoundMgmtServiceDelegate cmpdMgmtServiceDelegate = new CompoundMgmtServiceDelegate();
	   							String structure = cmpdMgmtServiceDelegate.getStructureByCompoundNo(result.getGlobalNumber()).get(0);
	   							
	   							char[] charStruct = structure.toCharArray();
	   							byte[] byteStruct = new byte[charStruct.length];
	   							
	   							for (int i = 0; i < charStruct.length; ++i) {
	   								byteStruct[i] = (byte) charStruct[i];
	   							}
	   								   							
	   							StructureLoadAndConversionUtil.loadSketch(byteStruct, StructureLoadAndConversionUtil.SDFILE, true, iBatch.getCompound());
	   							iBatch.getCompound().setStringSketch(byteStruct);
     						} catch (Exception e) { 
     							log.error("Error getting structure: ", e);
     						}
     						     						
     						if (!iBatch.getCompound().getStereoisomerCode().equals(result.getStereoisomerCode()))
     							iBatch.getCompound().setStereoisomerCode(result.getStereoisomerCode());
     						if (!iBatch.getCompound().getStructureComments().equals(result.getStructureComment()))
     							iBatch.getCompound().setStructureComments(result.getStructureComment());

     						// Check Salt Code & Equiv
     						try {
	     						if (!iBatch.getSaltForm().getCode().equals(result.getSaltCode())) {
	     							SaltCodeCache scc = SaltCodeCache.getCache();
	     							iBatch.setSaltForm(new SaltFormModel(result.getSaltCode(), scc.getDescriptionGivenCode(result.getSaltCode()), scc.getMolFormulaGivenCode(result.getSaltCode()), scc.getMolWtGivenCode(result.getSaltCode())));
	     						}
     						} catch (Exception e) { }
         					if (iBatch.getSaltEquivs() != result.getSaltMole()) {
         						iBatch.setSaltEquivs(result.getSaltMole());
         						iBatch.setSaltEquivsSet(true);
         					}

         					// Process all Submitted Containers to check for changes or additions
         					List<BatchSubmissionContainerInfoModel> vials = iBatch.getRegInfo().getSubmitContainerList();
         					for (RegistrationVialDetailsVO nC : result.getVials()) {
         						boolean newFlag = true;
         						for (BatchSubmissionContainerInfoModel c : vials) {
         							if (c.getBarCode().equals(nC.getLocalBarCode())) {
         								if (c.getAmountValue() != nC.getAmountAvailable()) {
         									c.setAmountValue(nC.getAmountAvailable());
         								}
         								if (!c.getAmountUnit().equals(nC.getAmountUnitCode())) {
             								c.setAmountUnit(nC.getAmountUnitCode());
         								}
             							newFlag = false;
             							break;
         							}
         						}
         						
         						if (newFlag) {
         							BatchSubmissionContainerInfoModel containerInfo = new BatchSubmissionContainerInfoModel();
         							containerInfo.setSiteCode(nC.getSiteCode());
         							containerInfo.setBarCode(nC.getLocalBarCode());
         							containerInfo.setAmountValue(nC.getAmountAvailable());
         							containerInfo.setAmountUnit(nC.getAmountUnitCode());
         							containerInfo.setSubmissionStatus(CeNConstants.REGINFO_SUBMITTED);
         							
         							iBatch.getRegInfo().addSubmitContainer(containerInfo);
         						}
         					}
     						
     						iBatch.setPrecursors(result.getPrecursors());
     						
     						List<List<String>> list = result.getResidualSolvents();
     						if (list.size() > 0) {
     							ArrayList<BatchResidualSolventModel> newList = new ArrayList<BatchResidualSolventModel>();
     							
     							for (List<String> entry : list) {
     								String code = (String)entry.get(0);
     								
             						boolean newFlag = true;
             						for (BatchResidualSolventModel s : iBatch.getRegInfo().getResidualSolventList()) {
             							if (s.getCodeAndName().equals(code)) {
             								s.setEqOfSolvent(Double.parseDouble((String)entry.get(1)));
             								newFlag = false;
             								newList.add(s);
             								break;
             							}
             						}
             						
             						if (newFlag) {
             							BatchResidualSolventModel s = new BatchResidualSolventModel();
	     								s.setCodeAndName(code);
	     								s.setEqOfSolvent(Double.parseDouble((String)entry.get(1)));
	     								newList.add(s);
             						}
     							}
     							
     							iBatch.getRegInfo().setResidualSolventList(newList);
     						}
     						
     						list = result.getSolubilitySolvents();
     						if (list.size() > 0) {
     							ArrayList<BatchSolubilitySolventModel> newList = new ArrayList<BatchSolubilitySolventModel>();
     							
     							for (List<String> entry : list) {
     								String code = (String)entry.get(0);
             						boolean newFlag = true;
             						for (BatchSolubilitySolventModel s :iBatch.getRegInfo().getSolubilitySolventList()) {
             							if (s.getCodeAndName().equals(code)) {
             								if (entry.get(4) != null) {
             									s.setQualitative(true);
             									s.setQualiString((String)entry.get(4));
              								} else {
             									s.setQuantitative(true);
                 								s.setOperator((String)entry.get(1));
                 								s.setSolubilityValue(Double.parseDouble((String)entry.get(2)));
                 								s.setSolubilityUnit((String)entry.get(5));
             								}
             								newFlag = false;
             								newList.add(s);
             								break;
             							}
             						}
             						
             						if (newFlag) {
             							BatchSolubilitySolventModel s = new BatchSolubilitySolventModel();
	     								s.setCodeAndName(code);
         								if (entry.get(4) != null) {
         									s.setQualitative(true);
         									s.setQualiString((String)entry.get(4));
          								} else {
         									s.setQuantitative(true);
             								s.setOperator((String)entry.get(1));
             								s.setSolubilityValue(Double.parseDouble((String)entry.get(2)));
             								s.setSolubilityUnit((String)entry.get(5));
         								}
	     								newList.add(s);
             						}
             						
         							iBatch.getRegInfo().setSolubilitySolventList(newList);
    							}
     						}
     						
         					// Check MW and MF
     						if (iBatch.getMolecularWeightAmount().GetValueInStdUnitsAsDouble() != result.getMolecularWeight()) {
     							iBatch.getMolecularWeightAmount().setCalculated(false);
     							iBatch.getMolecularWeightAmount().setValue(result.getMolecularWeight());
     						}
     						
     						if (!iBatch.getMolecularFormula().equals(result.getMolecularFormula())) {
     							iBatch.setMolFormula(result.getMolecularFormula());
     							iBatch.setMolecularFormula(result.getMolecularFormula());
     						}

     						iBatch.getMolecularWeightAmount().setCalculated(true);
     						
 							String oldStatus = iBatch.getRegInfo().getStatus();
 							String oldRegStatus = iBatch.getRegInfo().getRegistrationStatus();
 							iBatch.getRegInfo().setStatus("CURATION");
 							iBatch.getRegInfo().setRegistrationStatus("CURATION");
 							boolean bCalcFlg = iBatch.getTheoreticalWeightAmount().isCalculated();
 							iBatch.getTheoreticalWeightAmount().setCalculated(true);
 							iBatch.recalcAmounts();
							iBatch.getTheoreticalWeightAmount().setCalculated(bCalcFlg);
 							iBatch.getRegInfo().setStatus(oldStatus);
 							iBatch.getRegInfo().setRegistrationStatus(oldRegStatus);
 							
 							if (StringUtils.equals(iBatch.getRegInfo().getCompoundRegistrationStatus(), CeNConstants.REGINFO_NOT_SUBMITTED)) {
 								iBatch.getRegInfo().setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMISION_PASS);
 								iBatch.getRegInfo().setCompoundRegistrationStatusMessage("");
 								iBatch.getRegInfo().setStatus(CeNConstants.REGINFO_PASSED);
 								iBatch.getRegInfo().setSubmissionStatus(CeNConstants.REGINFO_SUBMITTED);
 							}
 							
 							iBatch.getVendorInfo().setCode(result.getSupplierCode());
 							iBatch.getVendorInfo().setSupplierCatalogRef(result.getSupplierRef());
 							
 							BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(iBatch, pageModel, true);
 							
     						count++;
     					}
					}
				}
			} catch (Exception e) {
				log.error("Error while getting registration information:", e);
			}
		}
		
		String message = count + " record(s) updated.";
		
		if (count > 0) {
			message += " Don't forget to save the changes.";
		}
		
		log.info("getRegistrationStatusForBatches(ProductBatchModel[]) end");
		
		return message;
	}
	
	/**
	 * 
	 * @param plates
	 * @param purpose
	 * @param reginfoSubmitted
	 * @param compoundRegistrationJobStatusPending
	 */
	private void setBatchStatus(ProductPlate[] plates, String purpose, String reginfoSubmitted, String compoundRegistrationJobStatusPending) {
		for (int i = 0; i < plates.length; i++) {
			ProductBatchModel[] batches = plates[i].getAllBatchesInThePlate();
			setBatchStatus(batches, purpose, reginfoSubmitted, compoundRegistrationJobStatusPending);
		}
	}

	/**
	 * 
	 * @param wells
	 * @param purpose
	 * @param reginfoSubmitted
	 * @param compoundRegistrationJobStatusPending
	 */
	private void setBatchStatus(PlateWell[] wells, String purpose, String reginfoSubmitted, String compoundRegistrationJobStatusPending) {
		ProductBatchModel[] batches = new ProductBatchModel[wells.length];
		for (int i = 0; i < wells.length; i++) 
			batches[i] = (ProductBatchModel) wells[i].getBatch();
		setBatchStatus(batches, purpose, reginfoSubmitted, compoundRegistrationJobStatusPending);
	}
	
	/**
	 * 
	 * @param batches
	 * @param purpose
	 * @param submissionStatus
	 * @param status
	 */
	private void setBatchStatus(ProductBatchModel[] batches, String purpose, String submissionStatus, String status) {
		for (int i = 0; i < batches.length; i++) {
			batches[i].setSelected(false);
			if (purpose.equals("REGISTRATION")) {
				batches[i].getRegInfo().setSubmissionStatus(submissionStatus);
				batches[i].getRegInfo().setStatus(status);
				batches[i].setSelectivityStatus(CeNConstants.BATCH_STATUS_PASSED_REGISTERED);
				batches[i].getRegInfo().setCompoundRegistrationStatusMessage(""); //Reset the previous error status messages.
				
			} else if (purpose.equals("PLATE REGISTRATION"))
			{
				batches[i].getRegInfo().setCompoundManagementStatus(submissionStatus + " - " + status);
				batches[i].getRegInfo().setCompoundManagementStatusMessage(""); //Reset the previous error status messages.
			}
			else if (purpose.equals("PLATE PURIFICATION"))
			{
				batches[i].getRegInfo().setPurificationServiceStatus(submissionStatus + " - " + status);
				batches[i].getRegInfo().setPurificationServiceStatusMessage(""); //Reset the previous error status messages.
			}
			else if (purpose.equals("SCREENING"))// not used.
			{
				batches[i].getRegInfo().setCompoundAggregationStatus(submissionStatus + " - " + status);
				batches[i].getRegInfo().setCompoundAggregationStatusMessage(status); //Reset the previous error status messages.
			}
		}
	}
	
	/**
	 * 
	 * @param productPlates
	 * @return
	 */
 	public boolean isAllProductPlatesBarCoded(ProductPlate[] productPlates) {
		for (int i = 0; i < productPlates.length; i++) {
			if (productPlates[i].getPlateBarCode() == null)
				return false;
			if (productPlates[i].getPlateBarCode().length() == 12)
				continue;
			else
				return false;
		}
		return true;
	}
 	
 	/**
 	 * 
 	 * @param productPlates
 	 * @return
 	 */
	public boolean isAllCompoundManagementContainers(ProductPlate[] productPlates) {
		boolean result = true;
		for (int i = 0; i < productPlates.length; i++) {
			if (productPlates[i].getContainer().isUserDefined()) //"CompoundManagementMonomerContainer"
/*			if (!productPlates[i].getContainer().getContainerType().equals(  // vbtodo fix!!!
					"Rack")) //"CompoundManagementMonomerContainer"))
*/
			result = false;
		}
		return result;
	}

	public String buildSDFile(ProductBatchModel selectedBatch, boolean forCompoundRegistrationReg) {
		String result = "";
		//USER2:purposefully commented out and will be refactored later.
		//use SDFileGeneratorUtil instead
		/*if (selectedBatch != null && pageModel != null && selectedBatch.getCompound() != null) {
			ParentCompoundModel selectedCompound = selectedBatch.getCompound();
			boolean submitUsingMGs = false;
			try {
				String prop = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SUBMIT_MG);
				if (prop != null && prop.equals("true"))
					submitUsingMGs = true;
			} catch (Exception e) {
			}
			try {
				ChemistryDelegate chemDel = new ChemistryDelegate();
				String stringSketch = selectedCompound.getStringSketchAsString();
				stringSketch = Decoder.decodeString(stringSketch);
				byte[] molFile = chemDel.convertChemistry(stringSketch.getBytes(), "", "MDL Molfile");
				sdUnit sDunit = new sdUnit(new String(molFile), true);
				// 11 fields required for all types of SDFiles
				sDunit.setValue("STEREOISOMER_CODE", selectedBatch.getCompound().getStereoisomerCode());
				sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE", selectedBatch.getCompound().getStereoisomerCode());
				sDunit.setValue("COMPOUND_REGISTRATION_SITE_CODE", pageModel.getSiteCode());
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_REFERENCE", selectedBatch.getBatchNumberAsString());
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE", selectedBatch.getRegInfo().getCompoundSource());
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE", selectedBatch.getRegInfo().getCompoundSourceDetail());
				sDunit.setValue("COMPOUND_REGISTRATION_TA_CODE", pageModel.getTaCode());
				sDunit.setValue("COMPOUND_REGISTRATION_PROJECT_CODE", pageModel.getProjectCode());
				if (submitUsingMGs) {
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE", (new Double(selectedBatch.getWeightAmount()
							.GetValueInStdUnitsAsDouble())).toString());
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE", "MG");
				} else {
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE", (new Double(selectedBatch.getWeightAmount().getValue()))
							.toString());
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE", selectedBatch.getWeightAmount().getUnit().getCode());
				}
				// Check if owner is the same as chemist, if so use employeeid
				// of chemist
				if (selectedBatch.getOwner().equals(selectedBatch.getSynthesizedBy()))
					sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID", NotebookUser.getInstance().getPreference(NotebookUser.PREF_EmployeeID));
				else {
					String empId = getOwnerEmpId(selectedBatch.getOwner());
					if (empId != null)
						sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID", empId);
					else {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Owner NT UserId not found in Employee Database.");
						return "";
					}
				}
				// 51 fields optional for ALL types of SDFiles (NTVL, NTPL,
				// SOLPL)
				// this comment should be structure comment for compound
				sDunit.setValue("COMPOUND_REGISTRATION_STRUCTURE_COMMENT", handleGTLT(selectedBatch.getCompound().getStructureComments()).replaceAll("\n", " "));
				if (NotebookPageUtil.isValidRegNumber(selectedBatch.getCompound().getRegNumber()))
					sDunit.setValue("COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH", selectedBatch.getCompound().getRegNumber()); // PF-00000000-00
				// SCT commented out at request of KevinH. fields are not
				// required and will prevent us from having to strip leading 0
				// on page #
				// AJK uncommented as these are required fields for CompoundRegistration
				// Interactive registration.
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_NUMBER", NotebookPageUtil.getNotebookNumberFromBatchNumber(selectedBatch
						.getBatchNumberAsString()));
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_PAGE_NUMBER", validateNoteBookPageNumber(NotebookPageUtil
						.getNotebookPageFromBatchNumber(selectedBatch.getBatchNumberAsString())));
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_SPOT_NUMBER", NotebookPageUtil.getLotNumberFromBatchNumber(selectedBatch
						.getBatchNumberAsString()));
				sDunit.setValue("COMPOUND_REGISTRATION_SUPPLIER_CODE", selectedBatch.getVendorInfo().getCode());
				sDunit.setValue("COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER", selectedBatch.getVendorInfo().getSupplierCatalogRef());
				if (!(pageModel.getLiteratureRef() == null || pageModel.getLiteratureRef().trim().length() == 0)) {
					sDunit.setValue("COMPOUND_REGISTRATION_LITERATURE_REFERENCE_CODE", "LIT");
					sDunit.setValue("COMPOUND_REGISTRATION_LITERATURE_REFERENCE_COMMENT", handleGTLT(pageModel.getLiteratureRef()).replaceAll("\n",
							", "));
				}
				if (!selectedBatch.getCompound().getRegNumber().equals("")) {
					sDunit.setValue("COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH", selectedBatch.getCompound().getRegNumber());
				}
				if (selectedBatch.getSaltEquivs() > 0 && selectedBatch.getSaltEquivsSet()) {
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE", selectedBatch.getSaltForm().getCode());
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE", new Double(selectedBatch.getSaltEquivs()).toString());
				} else if (selectedBatch.getSaltForm().getCode().equals("00")) {
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE", selectedBatch.getSaltForm().getCode());
				}
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_STATE", selectedBatch.getCompoundState());
				// Alias_Batch_Number is an externally supplied batch number -
				// we currently use external Supplier
				// sDunit.setValue("COMPOUND_REGISTRATION_ALIAS_BATCH_NUMBER", );G00000912
				if (selectedBatch.getVendorInfo().getCode() == null || selectedBatch.getVendorInfo().getCode().length() == 0)
					sDunit.setValue("COMPOUND_REGISTRATION_BATCH_CREATOR_ID", NotebookUser.getInstance().getPreference(NotebookUser.PREF_EmployeeID));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_COMMENT", handleGTLT(selectedBatch.getComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_MF", selectedBatch.getMolecularFormula());
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_MW", Double.toString(selectedBatch.getMolWgt()));
				sDunit.setValue("COMPOUND_REGISTRATION_PARENT_MF", selectedBatch.getCompound().getMolFormula());
				if (!forCompoundRegistrationReg) {
					sDunit.setValue("CAL_PARENT_MW", Double.toString(selectedBatch.getCompound().getMolWgt()));
					sDunit.setValue("CAL_PARENT_MF", selectedBatch.getCompound().getMolFormula());
				}
				String preCursors = null;
				if (selectedBatch.getPrecursors() != null && selectedBatch.getPrecursors().size() > 0) {
					// Updated AbstractBatch to ensure no duplicate
					// Pre-Cursors, but let's make sure
					ArrayList pc = new ArrayList();
					for (Iterator it = selectedBatch.getPrecursors().iterator(); it.hasNext();) {
						String pcs = (String) it.next();
						if (pc.indexOf(pcs) < 0)
							pc.add(pcs);
					}
					preCursors = buildTagValue(pc);
					sDunit.setValue("COMPOUND_REGISTRATION_PRECURSOR_COMPOUND", preCursors);
				}
				sDunit.setValue("COMPOUND_REGISTRATION_PROTOCOL_ID", pageModel.getProtocolID());
				// If Hit Id exists enter in as COMPOUND_REGISTRATION_HIT_ID
				// HitID is a means for med chemists to identify a plate and a
				// well from a screening
				// process that they wish to process.
				String hitID = selectedBatch.getRegInfo().getHitId();
				if (hitID != null && hitID.length() > 0)
					sDunit.setValue("COMPOUND_REGISTRATION_HIT_ID", hitID);
				String tVal = selectedBatch.getRegInfo().getIntermediateOrTest();
				if (tVal == null || tVal.length() == 0) tVal = "U";
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", tVal);
//				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", "U");
				// sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", (selectedBatch.isIntermediate()) ? "Y": "N");
				// Reaction Components:
				// monomer series: A + B + C indicates specific order of
				// precursors
				// Literally a letter A, B, C, or D
				// if (preCursors != null) {
				// String rxnComponentLabels = new String();
				// List preCursorList = selectedBatch.getPrecursors();
				// for (int i=0; i < preCursorList.size(); i++) {
				// if (i > 0) rxnComponentLabels += "\n";
				// char data[] = { (char)('A' + i) };
				// rxnComponentLabels += new String(data);
				// }
				//					
				// sDunit.setValue("COMPOUND_REGISTRATION_REACTION_COMPONENT", preCursors);
				// sDunit.setValue("COMPOUND_REGISTRATION_REACTION_COMPONENT_LABEL",
				// rxnComponentLabels);
				// }
				if (selectedBatch.getAnalyticalPurityList() != null && selectedBatch.getAnalyticalPurityList().size() > 0) {
					// Purity method is not used any more - ajk 2004/12/02
					String operatorString = "";
					String valueString = "";
					String codeString = "";
					for (int i = 0; i < selectedBatch.getAnalyticalPurityList().size(); i++) {
						PurityModel pur = (PurityModel) selectedBatch.getAnalyticalPurityList().get(i);
						if (i != 0) {
							// dateString += "\n" +pur.getDate();
							operatorString += "\n" + convertPurityOperatorCode(pur.getOperator());
							valueString += "\n" + pur.getPurityValue();
							// flagString += "\n" +pur.ge;
							codeString += "\n" + pur.getCode();
						} else {
							// dateString += pur.getDate();
							operatorString += convertPurityOperatorCode(pur.getOperator());
							valueString += pur.getPurityValue();
							// flagString += pur.getFlag();
							codeString += pur.getCode();
						}
					}
					// sDunit.setValue("COMPOUND_REGISTRATION_PURIFICATION_METHOD",
					// methodString);
					// TODO: Andy needs to update Purity
					sDunit.setValue("COMPOUND_REGISTRATION_ANALYTICAL_PURITY_CODE", codeString);
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_DATE", dateString);
					// TODO: F = failed, P = pass, S = suspect(?)
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_FLAG", );
					sDunit.setValue("COMPOUND_REGISTRATION_PURITY_OPERATOR", operatorString);
					sDunit.setValue("COMPOUND_REGISTRATION_PURITY_VALUE", valueString);
					// Do not allow ranges. Make upper value equal to lower
					// value - 2004/12/02 Li Su.
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_UPPER_VALUE",
					// valueString);
				}
				if (selectedBatch.getRegInfo().getResidualSolventList() != null
						&& selectedBatch.getRegInfo().getResidualSolventList().size() > 0) {
					String codeString = new String();
					String moleString = new String();
					for (int i = 0; i < selectedBatch.getRegInfo().getResidualSolventList().size(); i++) {
						BatchRegistrationResidualSolvent brResidualSolvent = (BatchRegistrationResidualSolvent) (selectedBatch
								.getRegInfo().getResidualSolventList().get(i));
						if (i > 0) {
							codeString += "\n" + brResidualSolvent.getCodeAndName();
							moleString += "\n" + Double.toString(brResidualSolvent.getEqOfSolvent());
						} else {
							codeString += brResidualSolvent.getCodeAndName();
							moleString += Double.toString(brResidualSolvent.getEqOfSolvent());
						}
					}
					sDunit.setValue("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CODE", codeString);
					sDunit.setValue("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_MOLE", moleString);
				}
				if (selectedBatch.getRegInfo().getSolubilitySolventList() != null
						&& selectedBatch.getRegInfo().getSolubilitySolventList().size() > 0) {
					String codeString = "";
					String operatorString = "";
					String valueString = "";
					String upperValueString = "";
					String unitString = "";
					String qualitativeString = "";
					for (int i = 0; i < selectedBatch.getRegInfo().getSolubilitySolventList().size(); i++) {
						BatchRegistrationSolubilitySolvent brSolubilitySolvent = (BatchRegistrationSolubilitySolvent) (selectedBatch
								.getRegInfo().getSolubilitySolventList().get(i));
						if (brSolubilitySolvent.isQualitative()) {
							if (i != 0) {
								codeString += "\n" + brSolubilitySolvent.getCodeAndName();
								qualitativeString += "\n" + brSolubilitySolvent.getQualiString();
								operatorString += "\n" + "N/A";
								valueString += "\n" + "N/A";
								upperValueString += "\n" + "N/A";
								unitString += "\n" + "N/A";
							} else {
								codeString += brSolubilitySolvent.getCodeAndName();
								qualitativeString += brSolubilitySolvent.getQualiString();
								operatorString += "N/A";
								valueString += "N/A";
								upperValueString += "N/A";
								unitString += "N/A";
							}
						} else if (brSolubilitySolvent.isQuantitative()) {
							if (i != 0) {
								codeString += "\n" + brSolubilitySolvent.getCodeAndName();
								qualitativeString += "\n" + "N/A";
								operatorString += "\n" + brSolubilitySolvent.getOperator();
								valueString += "\n" + brSolubilitySolvent.getSolubilityValue();
								upperValueString += "\n"
										+ new Double(new Double(brSolubilitySolvent.getSolubilityValue()).doubleValue() + 1.0)
												.toString();
								unitString += "\n" + brSolubilitySolvent.getSolubilityUnit();
							} else {
								codeString += brSolubilitySolvent.getCodeAndName();
								qualitativeString += "N/A";
								operatorString += brSolubilitySolvent.getOperator();
								valueString += brSolubilitySolvent.getSolubilityValue();
								upperValueString += new Double(
										new Double(brSolubilitySolvent.getSolubilityValue()).doubleValue() + 1.0).toString();
								unitString += brSolubilitySolvent.getSolubilityUnit();
							}
						}
					}
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CODE", codeString);
					sDunit.setValue("COMPOUND_REGISTRATION_QUALITATIVE_SOLUBILITY", qualitativeString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_OPERATOR", operatorString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_VALUE", valueString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UNIT_CODE", unitString);
					sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UPPER_VALUE", upperValueString);
					// Do not allow ranges. Make upper value equal to lower
					// value - 2004/12/02 Li Su.
					// sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UPPER_VALUE",
					// valueString);
				}
				sDunit.setValue("COMPOUND_REGISTRATION_HANDLING_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationHandlingCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_HANDLING_COMMENT", handleGTLT(selectedBatch.getHandlingComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_STORAGE_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationStorageCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_STORAGE_COMMENT", handleGTLT(selectedBatch.getStorageComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_HAZARD_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_HAZARD_COMMENT", selectedBatch.getHazardComments());
				sDunit.setValue("COMPOUND_REGISTRATION_PARENT_HAZARD_COMMENT", handleGTLT(selectedBatch.getCompound().getHazardComments()).replaceAll("\n", " "));
				// Sample Submission
				UnitCache uc = UnitCache.getInstance();
				// if
				// (selectedBatch.getRegInfo().getSubmissionStatus().equals("Submitting")
				// ) {
				// Submission for container info
				if (selectedBatch.getRegInfo().getSubmitContainerList() != null
						&& selectedBatch.getRegInfo().getSubmitContainerList().size() > 0) {
					String codeString = "";
					String amountValueString = "";
					String amountUnitString = "";
					int actualCount = 0;
					for (int i = 0; i < selectedBatch.getRegInfo().getSubmitContainerList().size(); i++) {
						BatchSubmissionContainerInfo brBatchSubmissionContainerInfo = (BatchSubmissionContainerInfo) (selectedBatch
								.getRegInfo().getSubmitContainerList().get(i));
						if (!brBatchSubmissionContainerInfo.getSubmissionStatus().equals(BatchSubmissionContainerInfo.SUBMITTED)) {
							if (actualCount > 0) {
								codeString += "\n";
								amountUnitString += "\n";
								amountValueString += "\n";
							}
							codeString += brBatchSubmissionContainerInfo.getBarCode();
							if (submitUsingMGs) {
								Amount amount = new Amount(brBatchSubmissionContainerInfo.getAmountValue(), uc
										.getUnit(brBatchSubmissionContainerInfo.getAmountUnit()));
								amountUnitString += "MG";
								amountValueString += amount.GetValueInStdUnitsAsDouble();
							} else {
								amountUnitString += brBatchSubmissionContainerInfo.getAmountUnit();
								amountValueString += Double.toString(brBatchSubmissionContainerInfo.getAmountValue());
							}
							actualCount++;
						}
					}
					sDunit.setValue("COMPOUND_REGISTRATION_BAR_CODE", codeString);
					sDunit.setValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT_UNIT_CODE", amountUnitString);
					sDunit.setValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT", amountValueString);
					sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", selectedBatch.getProtectionCode());
				}
				// Submission to Biologist for test
				ArrayList biologyList = selectedBatch.getRegInfo().getSubmitToBiologistTestList();
				if (biologyList != null && biologyList.size() > 0) {
					String scientistCode = "";
					String screenProtocolID = "";
					String screenDefaultAmount = "";
					String unitString = "MG";
					double total = 0.0;
					int byMMcount = 0;
					for (int i = 0; i < biologyList.size(); i++) {
						BatchSubmissionToBiologistTest brSubmissionToBiologistTest = (BatchSubmissionToBiologistTest) biologyList
								.get(i);
						if (brSubmissionToBiologistTest.isTestSubmittedByMm()) {
							if (!brSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
								if (byMMcount > 0) {
									scientistCode += "\n" + brSubmissionToBiologistTest.getScientistCode();
									screenProtocolID += "\n" + brSubmissionToBiologistTest.getScreenProtocolId();
									screenDefaultAmount += "\n" + Double.toString(brSubmissionToBiologistTest.getAmountValue());
								} else {
									scientistCode += brSubmissionToBiologistTest.getScientistCode();
									screenProtocolID += brSubmissionToBiologistTest.getScreenProtocolId();
									screenDefaultAmount += Double.toString(brSubmissionToBiologistTest.getAmountValue());
								}
								++byMMcount;
							}
						} else {
							if (!brSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToBiologistTest.SUBMITTED)) {
								Amount amount = new Amount(brSubmissionToBiologistTest.getAmountValue(), uc
										.getUnit(brSubmissionToBiologistTest.getAmountUnit()));
								total += amount.GetValueInStdUnitsAsDouble();
							}
						}
					}
					// sent by MM
					sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_SCIENTIST_CODE", scientistCode);
					sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_PROTOCOL_ID", screenProtocolID);
					sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_DEFAULT_AMOUNT", screenDefaultAmount);
					// sent by self
					sDunit.setValue("COMPOUND_REGISTRATION_SENT_TO_BIOLOGISTS_AMOUNT_UNIT_CODE", unitString);
					sDunit.setValue("COMPOUND_REGISTRATION_SENT_TO_BIOLOGISTS_AMOUNT_VALUE", (new Double(total)).toString());
					// sDunit.setValue("COMPOUND_REGISTRATION_SCREEN_NUMBER", screenNumber);
				}
				// }
				// else {
				// //Registration without submission requires Protection Code as
				// well.
				// sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", "NONE");
				// //System.out.println("the protection code is: ");
				// sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE");
				// }
				if (sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE") == null || sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE").length() <= 0)
					sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", "NONE");
				// //System.out.println("Final: The built sd file is: " +
				// sDunit.toString());
				result = sDunit.toString();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
			}
		}
		*/
		return result;
	}
	

	/**
	 * 
	 * @param nonPlatedBatchesAndOtherBatches
	 * @param pseudoProductPlate
	 * @return
	 */
/*	private Tube[] getTubesForBatches(ProductBatchModel[] nonPlatedBatchesAndOtherBatches, PseudoProductPlate pseudoProductPlate) {
		Tube[] tubesList = new Tube[nonPlatedBatchesAndOtherBatches.length];
		for (int i = 0; i < nonPlatedBatchesAndOtherBatches.length; i++) {
			tubesList[i] = pseudoProductPlate.getTubeForBatch(nonPlatedBatchesAndOtherBatches[i]);
		}
		return tubesList;
	}*/
	
	private String convertPurityOperatorCode(String purityOperator) {
		if (purityOperator.equals("=")) {
			return "EQ";
		} else if (purityOperator.equals("<")) {
			return "LT";
		} else if (purityOperator.equals(">")) {
			return "GT";
		} else if (purityOperator.equals("~")) {
			return "AP";
		}
		return purityOperator;
	}
	

	private String processCodes(List valueList) {
		String tagValueString = new String();
		for (int i = 0; i < valueList.size(); i++) {
			if (i > 0) {
				tagValueString += "\n" + valueList.get(i);
			} else {
				tagValueString += valueList.get(i);
			}
		}
		return tagValueString;
	}
	
	//Use SDFileGenerator Util for all SDFile generation needs and also one point of logic for SDFile creation.
	//Helps in fixing bugs related to compound registration and code maintence.
	public String buildSDFile(List<ProductBatchModel> batchesList,String registrarNTID,String compoundManagementEmployeeID) throws Exception
	{
		SDFileGeneratorUtil sdfUtil = new SDFileGeneratorUtil();
		SDFileInfo sdinfo = sdfUtil.buildSDUnitsForListofBatches(batchesList.toArray(new ProductBatchModel[]{}), registrarNTID, compoundManagementEmployeeID, pageModel, true);
		return sdinfo.getSdfileStr();
		
	}
	
	public String submitTubesForCompoundManagementRegistration(PlateWell[] wellsArray, String siteCode)  {
		Map<String, String> batchesMap  = null;
		String apiMessage = "";
		try {
			batchesMap = getRegistrationManagerDelegate().submitTubesToCompoundManagement(wellsArray, siteCode);
			//Tubes submitted to Container API with out any login/connectivity exceptions.Each Tube might still pass/fail
			//For now there is one tube for batch.So update BAtch CompoundManagement Status message
			int wellSize = wellsArray.length;
			for(int i =0 ; i < wellSize ; i ++)
			{
				ProductBatchModel batch = (ProductBatchModel) wellsArray[i].getBatch();
				String status = batchesMap.get(wellsArray[i].getKey());
				batch.getRegInfo().setCompoundManagementStatus(CeNConstants.REGINFO_SUBMITTED + " - " + status);
				batch.getRegInfo().setCompoundManagementStatusMessage(""); // Reset the previous error status messages.
			}
			//pageModel.setEditable(false);//The project code and all other project specific details can not be changed once single batch is Registered. 
		} catch (Exception e) {
			log.error("Failure while submitting tubes for CompoundManagement Registration.", e); 
			apiMessage = e.getMessage();
		}
		return apiMessage;
	}
	
  /**
   * rlc
   * @param tubes
   * @return
   */
  public boolean isAllTubesBarCodedForCompoundManagementSubmission(PlateWell[] tubes) {
    for (int i = 0; i < tubes.length; i++) {
      ProductBatchModel prodBatch = (ProductBatchModel) tubes[i].getBatch();
      BatchRegInfoModel regInfo = prodBatch.getRegInfo();
      List<BatchSubmissionContainerInfoModel> submitContainerList = regInfo.getSubmitContainerList();
      for(BatchSubmissionContainerInfoModel bsci : submitContainerList) {
      
        if (bsci.getBarCode() == null) {
          return false;
        }
        if (bsci.getBarCode().length() == 12) {
          continue;
        }
        else {
          return false;
        }
      }
    }
      
    return true;
  }
	 
	public boolean isAllTubesBarCoded(PlateWell[] tubes) {
		for (int i = 0; i < tubes.length; i++) {
			if (tubes[i].getBarCode() == null)
				return false;
			if (tubes[i].getBarCode().length() == 12)
				continue;
			else
				return false;
		}
		return true;
	}

  /**
   * rlc
   * @param tubes
   * @return
   */
  public boolean isAllCompoundManagementTubeContainersForCompoundManagementSubmission(PlateWell[] tubes) {
    for (int i = 0; i < tubes.length; i++) {
      ProductBatchModel prodBatch = (ProductBatchModel) tubes[i].getBatch();
      BatchRegInfoModel regInfo = prodBatch.getRegInfo();
      List<BatchSubmissionContainerInfoModel> submitContainerList = regInfo.getSubmitContainerList();
      for(BatchSubmissionContainerInfoModel bsci : submitContainerList) {
        if (StringUtils.isBlank(bsci.getContainerType())) {
          return false;
        }
      }
    }
      
    return true;
  }
  
 	/**
 	 * 
 	 * @param productPlates
 	 * @return
 	 */
	public boolean isAllCompoundManagementTubeContainers(PlateWell[] tubes) {
		boolean result = true;
		for (int i = 0; i < tubes.length && result; i++) {
			result = StringUtils.isNotBlank(tubes[i].getContainerTypeCode()); 
		}
		return result;
	}
	
	/**
	 * Invoke the validator for plate CompoundManagement registration.
	 * @param plateArray
	 * @param errorMap 
	 * @return error map, empty if no errors.
	 */
	public LinkedHashMap<ProductBatchModel, String> validateTubesForCompoundManagementRegistration(List<PlateWell<ProductBatchModel>> wellArray, 
	                                                                                LinkedHashMap<ProductBatchModel, String> errorMap) {
		RegistrationValidator validator = new RegistrationValidator(pageModel, workflowList);
		validator.validateTubesForRegistration(wellArray, errorMap);
		return errorMap;
	}

	public String submitBatchesAndPlatesForRegistration(String[] batchKeys,
	                                                  String[] plateKeys,
	                                                  String[] workflows,
	                                                  NotebookPageModel pageModel, 
	                                                  SessionIdentifier sessionId) throws Exception {
		return getRegistrationManagerDelegate().submitBatchesAndPlatesForRegistration(batchKeys, plateKeys, workflows, pageModel);
	}
	
	public RegistrationManagerDelegate getRegistrationManagerDelegate() throws RegistrationManagerInitException {
		NotebookUser user = MasterController.getUser();
		return ServiceController.getRegistrationManagerDelegate(user.getSessionIdentifier(), 
		                                                        user.getCompoundManagementEmployeeId());	
	}
}
