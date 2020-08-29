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
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.*;

public class RegistrationValidator {
	public static final String BATCH_ALREADY_GCR_REGISTERED = "This batch is already registered";
	public static final String BATCH_BEING_GCR_REGISTERED = "This batch is being Registered. Please wait.";
	private static final String BATCH_NOT_GCR_REGISTERED = "This batch is not registered in CompoundRegistration";
	private static final String PLATE_COMPOUND_MANAGEMENT_REGISTERED = "This plate is already registered in CompoundManagement";
	//private static final String PLATE_COMPOUND_MANAGEMENT_BEING_REGISTERED = "This plate is being registered. Please wait.";
	private static final String PLATE_PURIFICATION_SERVICE_PURIFICATION = "This batch is already purified in PurificationService";
	private static final String PLATE_PURIFICATION_SERVICE_BEING_PURIFICATION = "This batch is being purified. Please wait.";
	private static final String TUBE_PURIFICATION_SERVICE_PARAMETERS_NOT_SET = "PurificationService parameters is not set for tubes in this batch.";
	private static final String PLATE_COMPOUND_AGGREGATION_SCREENED = "This batch is already submitted to CompoundAggregation";
	private static final String PLATE_COMPOUND_AGGREGATION_BEING_SCREENED = "This batch is being submitted to CompoundAggregation. Please wait.";
	private static final String PLATE_VNV_NOT_CHECKED = "This batch is never VNV checked";
	private static final String PLATE_COMPOUND_MANAGEMENT_NOT_REGISTERED = "This batch is not CompoundManagement registered";
	private static final String TUBE_GECM_NOT_BARCODED = "The barcode of this Tube/Vial is invalid";
	private static final String PLATE_PURIFICATION_SERVICEPARAMETERS_NOT_ASSIGNED = "Plate does not have required purifications parameters assigned.";
	private static final String BATCH_NO_SUBMITTAL_SETS_ASSIGNED = "This batch does not have any submittal sets assigned to it.";
	private static final String PLATE_WELL_MOLARITY_ZERO = "Molarity of plate well containing this batch is 0.";
	private static final String PLATE_WELL_SOLVENT_EMPTY = "Plate well does not contain solvent in it.";
	private static final String INVALID_BATCH_STATUS = "Batch status is not valid to Register.";
	private static final String TUBE_COMPOUND_MANAGEMENT_REGISTERED = "This tube is already registered in CompoundManagement";
	private static final String TUBE_WELL_MOLARITY_ZERO = "Molarity of tube containing this batch is 0.";
	private static final String TUBE_WELL_SOLVENT_EMPTY = "Tube does not contain solvent in it.";
	
	private NotebookPageModel pageModel = null;
		
	private List<String> workflowList = new Vector<String>();
	
	public RegistrationValidator(NotebookPageModel pageModel, List<String> workflowList) {
		this.pageModel = pageModel;
		this.workflowList = workflowList;
	}

	public void validatePlatesForRegistration(ProductPlate[] plates, Map<ProductBatchModel, String> errorMap) {
		if (plates != null) {
			for (ProductPlate plate : plates) {
				if (plate != null) {
					ProductBatchModel[] batches = plate.getAllBatchesInThePlate();
					validateBatchesForRegistration(batches, errorMap);
				}
			}
		}
	}
	
	public void validateBatchesForRegistration(ProductBatchModel[] batches, Map<ProductBatchModel, String> errorMap) {
		if (batches != null) {
			for (ProductBatchModel batch : batches) {
				if (batch != null && batch.getRegInfo() != null) {
					BatchRegInfoModel regInfo = batch.getRegInfo();
					if (regInfo.isCompoundRegistrationSubmittedSuccessfully() || StringUtils.equals(regInfo.getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMISION_PASS)) { // isCompoundRegistrationSubmittedSuccessfully() does not work
						errorMap.put(batch, BATCH_ALREADY_GCR_REGISTERED);
					} else if (regInfo.isCompoundRegistrationSubmitPending()){ //TODO || regInfo.isCompoundRegistrationSubmitted()) {
						errorMap.put(batch, BATCH_BEING_GCR_REGISTERED);
					} else if (StringUtils.equals(ProductBatchStatusMapper.getInstance().getStatusString(batch.getSelectivityStatus(), batch.getContinueStatus()), CeNConstants.NOT_MADE_DISCONTINUE)) {
						errorMap.put(batch, INVALID_BATCH_STATUS);
					} else if (MasterController.isVnvEnabled() && batch.isUserAdded() && isVnVCheckPassed(batch) == false) {
						errorMap.put(batch, PLATE_VNV_NOT_CHECKED);
					}
				}
			}
		}
	}

	private boolean isVnVCheckPassed(ProductBatchModel batch) {
		if (batch != null && batch.getRegInfo() != null && batch.getRegInfo().getBatchVnVInfo() != null) {
			return BatchVnVInfoModel.VNV_PASS.equalsIgnoreCase(batch.getRegInfo().getBatchVnVInfo().getStatus());
			//return batch.getRegInfo().getBatchVnVInfo().isPassed();
		}
		return false;
	}

	public void validatePlatesForPlateRegistration(ProductPlate[] productPlates, Map<ProductBatchModel, String> errorMap) {
		int initialSize = errorMap.size();
		
		if (productPlates != null) {
			for (ProductPlate plate : productPlates) {
				if (plate != null) {
					ProductBatchModel[] batches = plate.getAllBatchesInThePlate();
					if (batches != null) {
						for (ProductBatchModel batch : batches) {
							if (batch != null) {
								if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION) == false && 
								    batch.getRegInfo().isCompoundRegistrationSubmittedSuccessfully() == false) 
								{
									errorMap.put(batch, "This batch is not CompoundRegistration registered");
								}
								if (plate.getCompoundManagementRegistrationSubmissionStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS)) {
									errorMap.put(batch, PLATE_COMPOUND_MANAGEMENT_REGISTERED);
								}
							}
						}
					}
				}
			}
		}
		
		if (errorMap.size() == initialSize) {
			checkForCompoundRegistrationNotRegistered(productPlates, errorMap);
		}
		
		if (errorMap.size() == initialSize) {
			checkForMolarityValue(productPlates, errorMap);
		}
	}

	private void checkForMolarityValue(ProductPlate[] productPlates, Map<ProductBatchModel, String> errorMap) {
		if (productPlates != null) {
			for (ProductPlate plate : productPlates) {
				if (plate != null) {
					PlateWell<ProductBatchModel>[] wells = plate.getWells();
					if (wells != null) {
						for (PlateWell<ProductBatchModel> well : wells) {
							if (well != null) {
								ProductBatchModel batch = well.getBatch();
								String solventCode = well.getSolventCode();
								AmountModel molarity = well.getMolarity();
								if (batch != null && !StringUtils.isEmpty(solventCode) && molarity != null && molarity.GetValueInStdUnitsAsDouble() < 0) {
									errorMap.put(batch, PLATE_WELL_MOLARITY_ZERO);
								}
							}
						}
					}
				}
			}
		}
	}

	private void checkForWellSolventValue(ProductPlate[] productPlates, Map<ProductBatchModel, String> errorMap) {
		for (ProductPlate plate : productPlates)
		{
			for (PlateWell<ProductBatchModel> plateWell : plate.getWells())
			{
				if (plateWell.getBatch() != null && 
				    (StringUtils.isNotBlank(plateWell.getSolventCode())))//This is not applicable for vials. Need to check once vials are enabled.
				{
					errorMap.put(plateWell.getBatch(), PLATE_WELL_SOLVENT_EMPTY);
				}
			}
		}
	}
	
	//Check if it is already CompoundRegistration registered. Must be Registered.
	private void checkForCompoundRegistrationNotRegistered(ProductPlate[] productPlates, Map<ProductBatchModel, String> errorMap) {
		if (productPlates != null) {
			for (ProductPlate plate : productPlates) {
				if (plate != null) {
					checkForCompoundRegistrationNotRegistered(plate.getAllBatchesInThePlate(), errorMap);
				}
			}
		}
	}

	//Check if it is already CompoundManagement registered. Must be Registered.
	private void checkForCompoundManagementNotRegistered(ProductPlate[] productPlates, Map<ProductBatchModel, String> errorMap) {
		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT) == false) {
			for (int i=0; i<productPlates.length; i++) {
				if (productPlates[i].isCompoundManagementRegistered() == false) {
					ProductBatchModel[] productBatchModel = productPlates[i].getAllBatchesInThePlate();
					checkForCompoundManagementNotRegistered(productBatchModel, errorMap);
				}
			}
		}
	}
	
	private void checkForCompoundManagementNotRegistered(ProductBatchModel[] nonPlatedBatchesAndOtherBatches,
	                                      Map<ProductBatchModel, String> errorMap) {
		if(workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT) == false) {
			for (int j=0; j<nonPlatedBatchesAndOtherBatches.length; j++) {
				if (!nonPlatedBatchesAndOtherBatches[j].getRegInfo().isCompoundManagementSubmittedSuccessfully()) {
					errorMap.put(nonPlatedBatchesAndOtherBatches[j], PLATE_COMPOUND_MANAGEMENT_NOT_REGISTERED);
				}
			}
		}
	}

	private void checkForCompoundManagementRegistered(ProductBatchModel[] nonPlatedBatchesAndOtherBatches, Map<ProductBatchModel, String> errorMap) {
		if (!workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
			for (int j = 0; j < nonPlatedBatchesAndOtherBatches.length; j++) {
				if (!nonPlatedBatchesAndOtherBatches[j].getRegInfo().isCompoundManagementSubmittedSuccessfully()) {
					errorMap.put(nonPlatedBatchesAndOtherBatches[j], PLATE_COMPOUND_MANAGEMENT_NOT_REGISTERED);
				}
			}
		}
	}
	
	public void validatePlatesForPurification(ProductPlate[] productPlates, Map<ProductBatchModel, String> errorMap) {
		int initialSize = errorMap.size();
		//Check if it is already PurificationService registered. Must not be registered.
		if (productPlates != null) {
			for (int i = 0; i < productPlates.length; ++i)
			{
				ProductBatchModel[] productBatchModel = productPlates[i].getAllBatchesInThePlate();
				for (int j = 0; j < productBatchModel.length; ++j) {
					if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION) == false) {
						if (!productBatchModel[j].getRegInfo().isCompoundRegistrationSubmittedSuccessfully()) {
							errorMap.put(productBatchModel[j], "This batch is not CompoundRegistration registered");
						}
					}
					else if (productPlates[i].getPurificationSubmissionStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS))
						errorMap.put(productBatchModel[j], PLATE_PURIFICATION_SERVICE_PURIFICATION);
/*				else if (productPlates[i].getPurificationSubmissionStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING))
					errorMap.put(productBatchModel[j], PLATE_PURIFICATION_SERVICE_BEING_PURIFICATION);
*/				}
			}
			if (errorMap.size() == initialSize)
				checkForCompoundManagementNotRegistered(productPlates, errorMap);
		
			if (errorMap.size() == initialSize)
				checkPlatesForPurificationParameters(productPlates, errorMap);
		}
	}

	private void checkPlatesForPurificationParameters(ProductPlate[] productPlates, 
	                                                  Map<ProductBatchModel, String> errorMap) {
		for (int i=0; i<productPlates.length; i++)
		{
			PlateWell[] plateWell = productPlates[i].getWells();
			if (plateWell[0].getPurificationServiceParameter() == null || (!isPopulated(plateWell[0].getPurificationServiceParameter())))
			{
				ProductBatchModel[] productBatchModels = productPlates[i].getAllBatchesInThePlate();
				for (int j=0; j<productBatchModels.length; j++)
				{
					errorMap.put(productBatchModels[j], PLATE_PURIFICATION_SERVICEPARAMETERS_NOT_ASSIGNED);
				}
			}
		}
	}

	private boolean isPopulated(PurificationServiceSubmisionParameters purificationServiceParameter) {
		if (purificationServiceParameter == null ||  
			StringUtils.isBlank(purificationServiceParameter.getArchivePlate()) || 
			StringUtils.isBlank(purificationServiceParameter.getSampleWorkUp()))
			return false;
		return true;
	}

	/**
	 * 
	 * @param nonPlatedBatchesAndOtherBatches
	 * @param errorMap
	 */
	public void validateBatchesForPurification(ProductBatchModel[] nonPlatedBatches,
	                                           Map<ProductBatchModel, String> errorMap)
	{
		int initialSize = errorMap.size();
		for (ProductBatchModel batch: nonPlatedBatches) {
			if (batch.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS))
				errorMap.put(batch, PLATE_PURIFICATION_SERVICE_PURIFICATION);
			else if (batch.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING))
				errorMap.put(batch, PLATE_PURIFICATION_SERVICE_BEING_PURIFICATION);
		}
		if (errorMap.size() == initialSize)
			checkForCompoundManagementNotRegistered(nonPlatedBatches, errorMap);
	}

	/**
	 * 
	 * @param nonPlatedTubes
	 * @param errorMap
	 */
	public void validateTubesForPurification(List<PlateWell<ProductBatchModel>> nonPlatedTubes,
	                                         Map<ProductBatchModel, String> errorMap)
	{
		int initialSize = errorMap.size();
		List<ProductBatchModel> batches = new ArrayList<ProductBatchModel>();
		for (PlateWell<ProductBatchModel> tube : nonPlatedTubes) {
			ProductBatchModel batch = tube.getBatch();
			batches.add(batch);
			if (batch.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS)) {
				errorMap.put(batch, PLATE_PURIFICATION_SERVICE_PURIFICATION);
				continue;
			} else if (batch.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
				errorMap.put(batch, PLATE_PURIFICATION_SERVICE_BEING_PURIFICATION);
				continue;
			}
			for (BatchSubmissionContainerInfoModel infoModel : batch.getRegInfo().getSubmitContainerList()) {
				if (infoModel != null && !infoModel.isPurificationServiceParamatersSetByUser()) {
					errorMap.put(batch, TUBE_PURIFICATION_SERVICE_PARAMETERS_NOT_SET);
					break;
				}
			}
		}
		if (errorMap.size() == initialSize) {
			checkForCompoundManagementRegistered(batches.toArray(new ProductBatchModel[]{}), errorMap);
		}
	}
	
	/**
	 * 
	 * @param nonPlatedTubes
	 * @param errorMap
	 */
	public void validateTubeForPurification(PlateWell<ProductBatchModel> nonPlatedTube,
	                                        Map<ProductBatchModel, String> errorMap)
	{
		int initialSize = errorMap.size();
		List<ProductBatchModel> batches = new ArrayList<ProductBatchModel>();
		ProductBatchModel batch = nonPlatedTube.getBatch();
		batches.add(batch);
		if (batch.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS)) {
			errorMap.put(batch, PLATE_PURIFICATION_SERVICE_PURIFICATION);
		} else if (batch.getRegInfo().getPurificationServiceStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
			errorMap.put(batch, PLATE_PURIFICATION_SERVICE_BEING_PURIFICATION);
		}
		if (errorMap.size() == initialSize) {
			checkForCompoundManagementNotRegistered(batches.toArray(new ProductBatchModel[]{}), errorMap);
		}
	}
	
	private void checkForCompoundRegistrationNotRegistered(ProductBatchModel[] batches,	Map<ProductBatchModel, String> errorMap) {
		if (batches != null) {
			for (ProductBatchModel batch : batches) {
				if (batch != null) {
					BatchRegInfoModel regInfo = batch.getRegInfo();
					if (regInfo != null) {
						if (!workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
							if (!(regInfo.isCompoundRegistrationSubmittedSuccessfully() || StringUtils.equals(regInfo.getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMISION_PASS))) { // isCompoundRegistrationSubmittedSuccessfully() does not work
								errorMap.put(batch, BATCH_NOT_GCR_REGISTERED);
							}
						}
					}
				}
			}
		}
	}

	public void validatePlatesForScreening(ProductPlate[] productPlates,
			LinkedHashMap errorMap) {
		int initialSize = errorMap.size();
		//Check if it is already CompoundAggregation registered. Must not be registered.
		for (int i=0; i<productPlates.length; i++)
		{
			ProductBatchModel[] productBatchModel = productPlates[i].getAllBatchesInThePlate();
			for (int j=0; j<productBatchModel.length; j++) {
				if (productBatchModel[j].getRegInfo().getCompoundAggregationStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS))
					errorMap.put(productBatchModel[j], PLATE_COMPOUND_AGGREGATION_SCREENED);
				else if (productBatchModel[j].getRegInfo().getCompoundAggregationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING))
					errorMap.put(productBatchModel[j], PLATE_COMPOUND_AGGREGATION_BEING_SCREENED);
			}
		}
		if (errorMap.size() == initialSize)
			checkForCompoundRegistrationNotRegistered(productPlates, errorMap);
		
		if (errorMap.size() == initialSize)
			checkForSubmittalSets(productPlates, errorMap);
	}

	private void checkForSubmittalSets(ProductPlate[] productPlates, LinkedHashMap errorMap) {
		ProductBatchModel[] productBatchModels = null;
		for (int i=0; i< productPlates.length; i++)
		{
			productBatchModels = productPlates[i].getAllBatchesInThePlate();
			checkForSubmittalSets(productBatchModels, errorMap);
		}
	}

	private void checkForSubmittalSets(	ProductBatchModel[] nonPlatedBatchesAndOtherBatches, Map<ProductBatchModel, String> errorMap) {
		for (int i=0; i<nonPlatedBatchesAndOtherBatches.length; i++)
		{
			ProductBatchModel batch = nonPlatedBatchesAndOtherBatches[i];
			if (batch.getRegInfo().getNewSubmitToBiologistTestList().size() == 0)
				errorMap.put(batch, BATCH_NO_SUBMITTAL_SETS_ASSIGNED);
		}
	}
	
	public void validateBatchesForScreening(ProductBatchModel[] nonPlatedBatchesAndOtherBatches,
	                                        Map<ProductBatchModel, String> errorMap) {
		int initialSize = errorMap.size();
		for (int j=0; j<nonPlatedBatchesAndOtherBatches.length; j++) {
			if (nonPlatedBatchesAndOtherBatches[j].getRegInfo().getCompoundAggregationStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS))
				errorMap.put(nonPlatedBatchesAndOtherBatches[j], PLATE_COMPOUND_AGGREGATION_SCREENED);
			else if (nonPlatedBatchesAndOtherBatches[j].getRegInfo().getCompoundAggregationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING))
				errorMap.put(nonPlatedBatchesAndOtherBatches[j], PLATE_COMPOUND_AGGREGATION_BEING_SCREENED);
		}
		if (errorMap.size() == initialSize)
			checkForCompoundRegistrationNotRegistered(nonPlatedBatchesAndOtherBatches, errorMap);
		
		if (errorMap.size() == initialSize)
			checkForSubmittalSets(nonPlatedBatchesAndOtherBatches, errorMap);
	}

	public void validateBatchesForCompoundManagementSubmission(ProductBatchModel[] nonPlatedBatchesAndOtherBatches, Tube[] tubesList, Map<ProductBatchModel, String> errorMap) {
		int initialSize = errorMap.size();
		for (int i=0; i<nonPlatedBatchesAndOtherBatches.length;i++)
		{
			//Get the Tube object and then barcode to validate it.
			if (tubesList[i] != null && tubesList[i].getBarcode().length() !=12)
				errorMap.put(nonPlatedBatchesAndOtherBatches[i], TUBE_GECM_NOT_BARCODED);
		}
		if (errorMap.size() == initialSize)
			checkForCompoundRegistrationNotRegistered(nonPlatedBatchesAndOtherBatches, errorMap);		
	}
	
	public boolean validateBatchRegInfo(ProductBatchModel batch, Map<ProductBatchModel, String> errorMap) {
        BatchRegInfoModel regInfo = batch.getRegInfo();
        if (regInfo.isCompoundRegistrationSubmittedSuccessfully() ||
				(regInfo.getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)))
			return true;
		boolean isValid = true;
		NotebookPageModel page = pageModel;
		StringBuffer errorMessage = new StringBuffer();
		// Structure Molfile
		if ((batch.getCompound().getNativeSketch() == null || batch.getCompound().getNativeSketch().equals(""))//for singletin user created batches
				//&& (batch.getCompound().getMolfile() == null || batch.getCompound().getMolfile().equals(""))) {//For Parallel Molfile need to check.
				// vb 1/22 fixes incorrect field for product batches
				&& (batch.getCompound().getStringSketchAsString() == null || batch.getCompound().getStringSketchAsString().equals(""))) {//For Parallel Molfile need to check.
			errorMessage.append("Compound structure is required. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE
		//if (batch.getWeightAmount().doubleValue() <= 0.0) { vb 1/22
		//batch.getTotalVolume().GetValueInStdUnitsAsDouble() <= 0.0 &&
		if (batch.getTotalWeight().GetValueInStdUnitsAsDouble() <= 0.0) {
			errorMessage.append("Total Amount Made (Weight) must be greater than zero. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_NOTEBOOK_REFERENCE
		if (batch.getBatchNumberAsString() == null || batch.getBatchNumberAsString().trim().length() <= 0) {
			errorMessage.append("Notebook Reference is required. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_TA_CODE
		if (page.getTaCode() == null || page.getTaCode().trim().length() <= 0) {
			errorMessage.append("Therapeutic Area Code is required. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_PROJECT_CODE
		if (page.getProjectCode() == null || page.getProjectCode().trim().length() <= 0) {
			errorMessage.append("Project Code is required. \n");
			isValid = false;
		}
		// //COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE
		// if (batch.getWeightAmount().getValue() <= 0) {
		// errorMessage.append("Total Amount must be greater than zero. \n");
		// isValid = false;
		// }
		if (batch.getCompound().getStereoisomerCode() != null && batch.getCompound().getStereoisomerCode().trim().length() > 0) {
			// //System.out.println("the SIC is: " +
			// batch.getCompound().getStereoisomerCode());
			// COMPOUND_REGISTRATION_STRUCTURE_COMMENT is mandatory if
			// COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE in
			// (('SNENU','LRCMX','ENENU','DSTRU','UNKWN') and
			// ((COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is not defined) or
			// (COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is 'NEW')))
			if (batch.getCompound().getStereoisomerCode().equals("SNENU")
					|| batch.getCompound().getStereoisomerCode().equals("LRCMX") /* HARDCODE */
					|| batch.getCompound().getStereoisomerCode().equals("ENENU")
					|| batch.getCompound().getStereoisomerCode().equals("DSTRU")
					|| batch.getCompound().getStereoisomerCode().equals("UNKWN")) {
				if (!(batch.getCompound().getStructureComments() != null && batch.getCompound().getStructureComments().trim()
						.length() > 0)) {
					errorMessage.append("Structure comment is required. \n");
					isValid = false;
				}
			}
		} else {
			errorMessage.append("Stereoisomer Code is required. \n");
			isValid = false;
		}
		// COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE and COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE
		if (regInfo.getCompoundSource() != null && regInfo.getCompoundSource().trim().length() > 0) {
			if (!(regInfo.getCompoundSourceDetail() != null && regInfo.getCompoundSourceDetail().trim()
					.length() > 0)) {
				errorMessage.append("Compound Source Detail is required. \n");
				isValid = false;
			}
		}
		if (!(regInfo.getCompoundSource() != null && regInfo.getCompoundSource().trim().length() > 0)) {
			errorMessage.append("Compound Source is required. \n");
			isValid = false;
		}
		// If COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE='EXTERNL' then COMPOUND_REGISTRATION_SUPPLIER_CODE and
		// COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER are required
		if (regInfo.getCompoundSource() != null && regInfo.getCompoundSource().trim().length() > 0) {
			if (regInfo.getCompoundSource().equals("EXTERNL")) { /* HARDCODE */
				if (!(batch.getVendorInfo().getCode() != null && batch.getVendorInfo().getCode().trim().length() > 0)) {
					errorMessage.append("Supplier Code is required. \n");
					isValid = false;
				}
				if (!(batch.getVendorInfo().getSupplierCatalogRef() != null && batch.getVendorInfo().getSupplierCatalogRef().trim()
						.length() > 0)) {
					errorMessage.append("Supplier Registry Number is required. \n");
					isValid = false;
				}
			}
		}

        String intermediateOrTest = regInfo.getIntermediateOrTest();
        if (!StringUtils.equals("U", intermediateOrTest) && !StringUtils.equals("N", intermediateOrTest)) {
            errorMessage.append("Test Compound/Intermediate is not selected. \n");
            isValid = false;
        }
		// If COMPOUND_REGISTRATION_GLOBAL_SALT_CODE not like '00', then COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE is required
		if (batch.getSaltForm().getCode() != null) {
			if (!batch.getSaltForm().getCode().equals("00")) {
				if (batch.getSaltEquivs() <= 0) {
					errorMessage.append("Salt Equivalents is required and must be greater than 0. \n");
					isValid = false;
				}
			}
		}
		if (ProductBatchStatusMapper.getInstance().getStatusString(batch.getSelectivityStatus(), batch.getContinueStatus()).equals(CeNConstants.NOT_MADE_DISCONTINUE)) {
			errorMessage.append(INVALID_BATCH_STATUS +  "\n");
			isValid = false;
		}
		// cannot be reached
		// if
		// (selectedBatch.getRegInfo().getSubmissionStatus().equals("Submitting")
		// ) {
		// if (selectedBatch.getRegInfo().getSubmitContainerList()!= null &&
		// selectedBatch.getRegInfo().getSubmitContainerList().size()>0 ) {
		// if (selectedBatch.getProtectionCode() != null &&
		// selectedBatch.getProtectionCode().trim().length() <= 0 )
		// errorMessage.append("Protection Code is required. \n");
		// isValid = false;
		// }
		// }
		// //System.out.println("The isValid is; " + isValid);
		
    // check to ensure vial barcodes are valid and not in use
        regInfo.clearEmptyElementsFromSubmitContainerList();
		Iterator iter = regInfo.getSubmitContainerList().iterator();
		RegSubHandler regSubHandler = new RegSubHandler();
		
    while (iter.hasNext()) {
      BatchSubmissionContainerInfoModel bsciModel = (BatchSubmissionContainerInfoModel) iter.next();

      if (bsciModel.getAmountValue() == 0 && bsciModel.getVolumeValue() == 0) {
        errorMessage.append("Amount and Volume are both zero (Vial/Tube). \n");
        isValid = false;
        continue;
      }
      if (StringUtils.isBlank(bsciModel.getContainerType())) {
        errorMessage.append("Container Type must be specified (Vial/Tube). \n");
        isValid = false;
        continue;
      }
      String barCode = bsciModel.getBarCode();

      if (StringUtils.isBlank(barCode)) {
        continue;
      }
      
      // Do not need to check whether the compound is registered in CompoundRegistration prior to tube submission 
      // to CompoundManagement, since those two registration actions will be combined in v1.2 by GGA.
      if (!bsciModel.getContainerType().equalsIgnoreCase(CeNConstants.CONTAINER_TYPE_VIAL)) {  // tube
        if (barCode.trim().length() != 12) {
          errorMessage.append("Tube barcode must be 12 characters (" + barCode.trim() + "). \n");
          isValid = false;
        }
        continue;
      }
      
      // It's a vial ...
      BarcodeValidationVO bcVO = new BarcodeValidationVO();
      
      bcVO.setBarcode(barCode.trim());
      bcVO = regSubHandler.validateBarcode(bcVO);
    
      if (bcVO.getBarcodeStatus().equals(BarcodeValidationVO.NOT_VALID_STRING)) {
          errorMessage.append("Vial barcode is not valid (" + bcVO.getBarcode() + "). \n");
          isValid = false;
      }
      else if (bcVO.getBarcodeStatus().equals(BarcodeValidationVO.VALID_BUT_IN_USE_STRING)) {
        errorMessage.append("Vial barcode is valid but currently in use (" + bcVO.getBarcode() + "). \n");
        isValid = false;
      }
    }
		
		if (errorMap == null)// Singleton Request, So, VnV check need to be done here.
		{
			if (MasterController.isVnvEnabled() && batch.isUserAdded() && (!isVnVCheckPassed(batch)))
			{
				errorMessage.append("VnV and Uniquness check has to be performed. \n");
				isValid = false;
			}
		}
		if (!isValid)
			if (errorMap == null)// Singleton Request
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
				                              errorMessage.toString(), 
				                              "Errors for " + pageModel.getNbRef(), 
				                              JOptionPane.INFORMATION_MESSAGE);
			else
				errorMap.put(batch, errorMessage.toString());
		return isValid;
	}

	public boolean validateBatchRegInfo(ProductBatchModel batchModel) {
		return validateBatchRegInfo(batchModel, null);
	}
	
	public void validateTubesForRegistration (List<PlateWell<ProductBatchModel>> wells, 
	                                          LinkedHashMap<ProductBatchModel, String> errorMap) {
		//Check if it is already CompoundRegistration registered. Must not be CompoundManagement registered.
		for (PlateWell<ProductBatchModel> well : wells)
		{
			BatchRegInfoModel regInfo = well.getBatch().getRegInfo();
			if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
				if ((regInfo.isCompoundRegistrationSubmittedSuccessfully() || StringUtils.equals(regInfo.getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMISION_PASS))) {
					errorMap.put(well.getBatch(), "This batch is CompoundRegistration registered");
				}
			} else {
				if (!StringUtils.equals(regInfo.getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMISION_PASS)) { //regInfo.isCompoundRegistrationSubmittedSuccessfully() - does not work
					errorMap.put(well.getBatch(), "This batch is not CompoundRegistration registered");
				}				
			}
			if (regInfo.isCompoundManagementSubmittedSuccessfully()) {
				errorMap.put(well.getBatch(), TUBE_COMPOUND_MANAGEMENT_REGISTERED);
			}
		}
	}

	private void checkForTubeMolarityValue(PlateWell<ProductBatchModel>[] wells,
	                                       LinkedHashMap<ProductBatchModel, String> errorMap) {
	
			for (int j=0; j<wells.length; j++)
			{
				if (wells[j].getBatch() != null && ((wells[j].getSolventCode() != null && !wells[j].getSolventCode().equals("")) && wells[j].getMolarity().GetValueInStdUnitsAsDouble() <= 0d))//This is not applicable for vials. Need to check once vials are enabled.
				{
					errorMap.put(wells[j].getBatch(), TUBE_WELL_MOLARITY_ZERO);
				}
			}
		
	}

	private void checkForWellSolventValue(PlateWell[] plateWell, LinkedHashMap errorMap) {
		
			for (int j=0; j<plateWell.length; j++)
			{
				if (plateWell[j].getBatch() != null && (plateWell[j].getSolventCode() == null || plateWell[j].getSolventCode().equals("")))//This is not applicable for vials. Need to check once vials are enabled.
				{
					errorMap.put(plateWell[j].getBatch(), PLATE_WELL_SOLVENT_EMPTY);
				}
			}
		
	}
	
	
		
	private void checkForTubeCompoundManagementNotRegistered(
			ProductBatchModel[] nonPlatedBatchesAndOtherBatches,
			LinkedHashMap errorMap) {
		for (int j=0; j<nonPlatedBatchesAndOtherBatches.length; j++)
			if (!nonPlatedBatchesAndOtherBatches[j].getRegInfo().isCompoundManagementSubmittedSuccessfully())
				errorMap.put(nonPlatedBatchesAndOtherBatches[j], PLATE_COMPOUND_MANAGEMENT_NOT_REGISTERED);
	}
	
}
