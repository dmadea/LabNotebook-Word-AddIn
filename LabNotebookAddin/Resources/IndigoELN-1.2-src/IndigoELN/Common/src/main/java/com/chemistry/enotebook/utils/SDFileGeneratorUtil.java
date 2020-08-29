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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionContainerInfo;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;
import com.chemistry.enotebook.utils.sdf.SdUnit;
import com.chemistry.enotebook.utils.sdf.SdfileIterator;
import com.chemistry.enotebook.utils.sdf.SdfileIteratorFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//Sole purpose of this class is to generate a SDFile for CompoundRegistration submission etc
public class SDFileGeneratorUtil 
{
//	private static final long serialVersionUID = 1L; 
	
	private static Log log = LogFactory.getLog(SDFileGeneratorUtil.class);
	private final String NULL_STRING = "null";

	public SDFileGeneratorUtil() {
	}

	public SDFileInfo buildSDUnitsForBatchesInPlate(ProductPlate plate, String registrarNTID, String compoundManagementEmployeeID,
			NotebookPageModel pageModel, boolean forCompoundRegistrationReg) throws Exception 
	{
		log.debug("started to build SDFileInfo");
		ProductBatchModel models[] = plate.getAllBatchesInThePlate();

		log.debug("Product BatchModels in plate:" + models.length);
		SDFileInfo info = new SDFileInfo();

		if (models != null && models.length > 0) {
			// Get this property one time from cen_properties xml .No need to run it for every batch.
			boolean submitUsingMGs = false;
			try {
				String prop = CeNSystemProperties.getCeNServerSideSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SUBMIT_MG);
				if (prop != null && prop.equals("true"))
					submitUsingMGs = true;

				StringBuffer sdBuffer = new StringBuffer();
				int size = models.length;
				int[] fileOffsets = new int[size];
				log.debug("Building SDF Unit for a Batch " + size);
				for (int i = 0; i < size; i++) {
					//String sdStr = buildSDUnitForABatch(models[i], compoundManagementEmployeeID, pageModel, forCompoundRegistrationReg, submitUsingMGs);
					String sdStr = buildSDUnitForABatch(models[i], registrarNTID, pageModel, forCompoundRegistrationReg, submitUsingMGs);
					// CompoundRegistration uses offsets starting from 1
					fileOffsets[i] = (i + 1);
					models[i].getRegInfo().setOffset(fileOffsets[i]);
					log.debug("Offset added to reg info :" + fileOffsets[i]);
					sdBuffer.append(sdStr);
				}
				info.setSdfileStr(sdBuffer.toString());
				info.setSdunitOffsets(fileOffsets);
				log.debug("SDFile prepared is:\n" + info.getSdfileStr());
			} catch (Exception e) {
				log.error(e);
			}
			return info;
		} else {
			throw new Exception("Plate doesn't have any models to prepare SDfile");
		}
	}

	public SDFileInfo buildSDUnitsForListofBatches(ProductBatchModel productBatches[], String registrarNTID, String compoundManagementEmployeeID,
			NotebookPageModel pageModel, boolean forCompoundRegistrationReg) throws Exception {
		SDFileInfo info = new SDFileInfo();
		if (productBatches != null && productBatches.length > 0) {
			// Get this property one time from cen_properties xml .No need to run it for every batch.
			boolean submitUsingMGs = false;
			/*
			 * try { String prop = CeNSystemProperties.getCeNServerSideSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SUBMIT_MG);
			 * if (prop != null && prop.equals("true")) submitUsingMGs = true; } catch (Exception e) {
			 * log.debug("Error retrieving CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SUBMIT_MG property "); }
			 */
      String prop = CeNSystemProperties.getCeNServerSideSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SUBMIT_MG);
      if (prop != null && prop.equals("true"))
        submitUsingMGs = true;

			StringBuffer sdBuffer = new StringBuffer();
			int size = productBatches.length;
			int[] fileOffsets = new int[size];
			for (int i = 0; i < size; i++) {
				String sdStr = buildSDUnitForABatch(productBatches[i], compoundManagementEmployeeID, pageModel, forCompoundRegistrationReg, submitUsingMGs);
				fileOffsets[i] = (i + 1);
				productBatches[i].getRegInfo().setOffset(fileOffsets[i]);
				log.debug("Offset added to reg info :" + productBatches[i].getRegInfo().getOffset());
				sdBuffer.append(sdStr);
			}
			info.setSdfileStr(sdBuffer.toString());
			info.setSdunitOffsets(fileOffsets);
			log.debug("SDFile prepared is:\n" + info.getSdfileStr());
			return info;
		} else
			return info;
	}

	public String buildSDUnitForABatch(ProductBatchModel selectedBatch, String compoundManagementEmployeeID, NotebookPageModel pageModel, boolean forCompoundRegistrationReg, boolean submitUsingMGs) 
		throws Exception 
	{
		String result = "";
		SdUnit sDunit = null;
		
		if (selectedBatch != null && selectedBatch.getCompound() != null) {
			ParentCompoundModel selectedCompound = selectedBatch.getCompound();
//			String compoundManagementEmpId = getOwnerEmpId(compoundManagementEmployeeID);

			try {
				byte[] stringStrucBytes = selectedCompound.getStringSketch();
				if (stringStrucBytes != null) {
					String stringStruc = new String(stringStrucBytes);
					// Check if it is not mol format already
					if (stringStruc.indexOf("M END") <= 0) {
						String molformat = Decoder.decodeString(stringStruc);
						sDunit = new SdUnit(molformat, true);
					} else {
						sDunit = new SdUnit(stringStruc, true);
					}
				} else {
					throw new Exception("Compound structure is emtpy.Cannot prepare sdfile.Compound NB batch is: " + selectedBatch.getBatchNumberAsString());
				}

				// 11 fields required for all types of SDFiles
				String sic = selectedBatch.getCompound().getStereoisomerCode();
				if (stringIsNull(sic)) {
					sic = "HSREG";
				}
				sDunit.setValue("STEREOISOMER_CODE", sic);
				sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_STEREOISOMER_CODE", sic);
				sDunit.setValue("COMPOUND_REGISTRATION_SITE_CODE", pageModel.getSiteCode());
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_REFERENCE", selectedBatch.getBatchNumber().getBatchNumber());
				String sourceCode = selectedBatch.getRegInfo().getCompoundSource();
				if (stringIsNull(sourceCode)) {
					sourceCode = "INPRODINT";
				}
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE", sourceCode);
				String sourceDetCode = selectedBatch.getRegInfo().getCompoundSourceDetail();
				if (stringIsNull(sourceDetCode)) {
					sourceDetCode = "INPRDSINGL";
				}
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE", sourceDetCode);
				sDunit.setValue("COMPOUND_REGISTRATION_TA_CODE", pageModel.getTaCode());
				sDunit.setValue("COMPOUND_REGISTRATION_PROJECT_CODE", pageModel.getProjectCode());
				if (submitUsingMGs) {  // when checking amounts (total amount made >= Sum(vial amounts)), CompoundRegistration does not make conversions for units, so convert to standard unit code (MG)
					double amts = selectedBatch.getTotalWeight().GetValueInStdUnitsAsDouble();
					if (amts == 0)
						amts = 0.01;
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE", new Double(amts).toString());
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE", "MG");
				} else {
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE", (new Double(selectedBatch.getTotalWeight().getValue())).toString());
					sDunit.setValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE", selectedBatch.getTotalWeight().getUnit().getCode());
				}

//				String batchOwnerId = getOwnerEmpId(selectedBatch.getOwner());
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID", compoundManagementEmployeeID);
				// Check if owner is the same as chemist, if so use employeeid of chemist
				// if (selectedBatch.getOwner().equals(selectedBatch.getSynthesizedBy()))
				// 		sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID",compoundManagementEmployeeID);
				// else {
//				 		String empId = getOwnerEmpId(selectedBatch.getOwner());
				// 		if (empId != null)
				//			 sDunit.setValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID", empId);
				// 		else {
				// 			 new Exception("Owner NT UserId not found in Employee Database.");
				// 		}
				// }

				// 51 fields optional for ALL types of SDFiles (NTVL, NTPL, SOLPL)
				// this comment should be structure comment for compound
				sDunit.setValue("COMPOUND_REGISTRATION_STRUCTURE_COMMENT", handleGTLT(selectedBatch.getCompound().getStructureComments()).replaceAll("\n", " "));

//				Terms:
//					 Stereoisomer Code = SIC
//					 Global Number = GN
//
//				SDFile
//				COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH is used to create a new batch of ABC-00000000 (GN or Parent)
//				Notes:
//					1. Compound RegNumber doesn't usually carry a salt code so we will end up with only ABC-00000000 this is what we submit 
//					2. If we submit an id that points to a structure that doesn't match SIC and GN registration will fail.
//					3. If we submit an id that includes salt code this field is ignored and a new PF is assigned
//					4. If the SIC is labeled as the code for unknown the current business rule is to assign a new GN - not true in CompoundRegistration.  
//					    If the SIC and PF match it simply becomes a new batch of the GN.
//					5. Currently we lookup the SIC values and don't know what they are ahead of time, hence we used to allow the 
//				        user to decide when they wanted to declare that this was a new batch of the GPN and when they wanted a new GN.
				String regNumber = selectedBatch.getCompound().getRegNumber();
				if (StringUtils.isNotBlank(regNumber)) {
				    if(NotebookPageUtil.isValidParentRegNumber(regNumber)) 
					{
						sDunit.setValue("COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH", selectedBatch.getCompound().getRegNumber());
					} else {
						log.warn("Batch: " + selectedBatch.getBatchNumberAsString() + " with Compound Registration Number = " + regNumber + ": Failed validation of compound reg number as a Global Number format = ABC-00000000.  Consequence is that a new GN will be assigned on registration.");
					}
				}

				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_NUMBER", NotebookPageUtil.getNotebookNumberFromBatchNumber(selectedBatch.getBatchNumberAsString()));
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_PAGE_NUMBER", NotebookPageUtil.getNotebookPageFromBatchNumber(selectedBatch.getBatchNumberAsString()));
				sDunit.setValue("COMPOUND_REGISTRATION_NOTEBOOK_SPOT_NUMBER", NotebookPageUtil.getLotNumberFromBatchNumber(selectedBatch.getBatchNumberAsString()));
				if (selectedBatch.getRegInfo().getVendorInfo() == null
						|| StringUtils.isNotBlank(selectedBatch.getRegInfo().getVendorInfo().getCode())) {
					sDunit.setValue("COMPOUND_REGISTRATION_SUPPLIER_CODE", selectedBatch.getRegInfo().getVendorInfo().getCode());
					sDunit.setValue("COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER", selectedBatch.getRegInfo().getVendorInfo().getSupplierCatalogRef());
				}
				if (StringUtils.isNotBlank(pageModel.getLiteratureRef())) {
					sDunit.setValue("COMPOUND_REGISTRATION_LITERATURE_REFERENCE_CODE", "LIT");
					sDunit.setValue("COMPOUND_REGISTRATION_LITERATURE_REFERENCE_COMMENT", handleGTLT(pageModel.getLiteratureRef()).replaceAll("\n", " "));
				}

				if (selectedBatch.getSaltEquivs() > 0 && selectedBatch.getSaltEquivsSet()) {
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE", selectedBatch.getSaltForm().getCode());
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE", new Double(selectedBatch.getSaltEquivs()).toString());
				} else if (selectedBatch.getSaltForm().getCode().equals("00")) {
					sDunit.setValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE", selectedBatch.getSaltForm().getCode());
				}
				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_STATE", selectedBatch.getRegInfo().getCompoundState());

				// Alias_Batch_Number is an externally supplied batch number - we currently use external Supplier

				// sDunit.setValue("COMPOUND_REGISTRATION_ALIAS_BATCH_NUMBER", );G00000912
//				if (selectedBatch.getVendorInfo().getCode() == null || selectedBatch.getVendorInfo().getCode().length() == 0)
					
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_CREATOR_ID", compoundManagementEmployeeID);
				
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_COMMENT", handleGTLT(" "));
				
				// sDunit.setValue("COMPOUND_REGISTRATION_BATCH_COMMENT", handleGTLT(selectedBatch.getComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_MF", selectedBatch.getMolecularFormula());
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_MW", selectedBatch.getMolecularWeightAmount().getValue() + "");
				sDunit.setValue("COMPOUND_REGISTRATION_PARENT_MF", selectedBatch.getCompound().getMolFormula());

				if (!forCompoundRegistrationReg) {
					sDunit.setValue("CAL_PARENT_MW", Double.toString(selectedBatch.getCompound().getMolWgt()));
					sDunit.setValue("CAL_PARENT_MF", selectedBatch.getCompound().getMolFormula());
				}

				sDunit.setValue("COMPOUND_REGISTRATION_PROTOCOL_ID", pageModel.getProtocolID());

				// If Hit Id exists enter in as COMPOUND_REGISTRATION_HIT_ID
				// HitID is a means for med chemists to identify a plate and a well from a screening process that they wish to process.
				String hitID = selectedBatch.getRegInfo().getHitId();
				if (StringUtils.isNotBlank(hitID))
					sDunit.setValue("COMPOUND_REGISTRATION_HIT_ID", hitID);

				String tVal = selectedBatch.getRegInfo().getIntermediateOrTest();
//				if (tVal == null || tVal.length() == 0) tVal = "U";
				if (tVal != null && tVal.length() > 0)
					sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", tVal);
//				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", "U");
//				sDunit.setValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE", (selectedBatch.getIntermediateOrTest()) ? "Y": "N");


				// Reaction Components:
				// monomer series: A + B + C indicates specific order of precursors
				// Literally a letter A, B, C, or D
				List preCursorList = selectedBatch.getReactantBatchKeys();
				if (preCursorList != null && preCursorList.size() > 0) {
					ArrayList pc = new ArrayList();
					ExperimentPageUtils pageUtils = new ExperimentPageUtils();
					if (pageModel.getPageType().equalsIgnoreCase(CeNConstants.PAGE_TYPE_PARALLEL)) {
						String rxnComponentLabels = new String();
//						for (int i = 0; i < preCursorList.size(); i++) {
//							if (i > 0) rxnComponentLabels += "\n";
//							char data[] = { (char) ('A' + i) };
//							rxnComponentLabels += new String(data);
//							
//							String monomerKey = (String) preCursorList.get(i);
//							MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
//							if (monomerBatch != null) {
//								pc.add(monomerBatch.getCompound().getRegNumber());
//							}
//						}
//						String preCursors = buildTagValue(pc);
						
						int cnt = 0; 
						
            for (Iterator it = preCursorList.iterator(); it.hasNext();) {
              if (cnt > 0) rxnComponentLabels += "\n";
              
              char data[] = { (char) ('A' + cnt++) };
              
              rxnComponentLabels += new String(data);
              
              String monomerKey = (String) it.next();
              MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
              
              // check for duplicates
              if (pc.indexOf(monomerBatch.getCompound().getRegNumber()) < 0) {
                pc.add(monomerBatch.getCompound().getRegNumber());
					    }
            }
            String preCursors = buildTagValue(pc);						
						
  					sDunit.setValue("COMPOUND_REGISTRATION_REACTION_COMPONENT", preCursors);
						sDunit.setValue("COMPOUND_REGISTRATION_REACTION_COMPONENT_LABEL", rxnComponentLabels);
					} else { // Med-Chem or Conception treat it as Precursors
						for (Iterator it = preCursorList.iterator(); it.hasNext();) {
							String monomerKey = (String) it.next();
							MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
							
							// check for duplicates
							if (pc.indexOf(monomerBatch.getCompound().getRegNumber()) < 0)
								pc.add(monomerBatch.getCompound().getRegNumber());
						}
						String preCursors = buildTagValue(pc);
						sDunit.setValue("COMPOUND_REGISTRATION_PRECURSOR_COMPOUND", preCursors);
					}
				}

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

					// sDunit.setValue("COMPOUND_REGISTRATION_PURIFICATION_METHOD", methodString);
					// TODO: Andy needs to update Purity
					sDunit.setValue("COMPOUND_REGISTRATION_ANALYTICAL_PURITY_CODE", codeString);
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_DATE", dateString);
					// TODO: F = failed, P = pass, S = suspect(?)
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_FLAG", );
					sDunit.setValue("COMPOUND_REGISTRATION_PURITY_OPERATOR", operatorString);
					sDunit.setValue("COMPOUND_REGISTRATION_PURITY_VALUE", valueString);
					// Do not allow ranges. Make upper value equal to lower value - 2004/12/02 Li Su.
					// sDunit.setValue("COMPOUND_REGISTRATION_PURITY_UPPER_VALUE", valueString);
				}

				if (selectedBatch.getRegInfo().getResidualSolventList() != null && selectedBatch.getRegInfo().getResidualSolventList().size() > 0) {
					String codeString = new String();
					String moleString = new String();

					for (int i = 0; i < selectedBatch.getRegInfo().getResidualSolventList().size(); i++) {
						BatchResidualSolventModel brResidualSolvent = (BatchResidualSolventModel) (selectedBatch.getRegInfo().getResidualSolventList().get(i));
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

				if (selectedBatch.getRegInfo().getSolubilitySolventList() != null && selectedBatch.getRegInfo().getSolubilitySolventList().size() > 0) {
					String codeString = "";
					String operatorString = "";
					String valueString = "";
					String upperValueString = "";
					String unitString = "";
					String qualitativeString = "";

					for (int i = 0; i < selectedBatch.getRegInfo().getSolubilitySolventList().size(); i++) {
						BatchSolubilitySolventModel brSolubilitySolvent = (BatchSolubilitySolventModel) (selectedBatch.getRegInfo().getSolubilitySolventList().get(i));

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
								upperValueString += "\n" + new Double(new Double(brSolubilitySolvent.getSolubilityValue()).doubleValue() + 1.0).toString();
								unitString += "\n" + brSolubilitySolvent.getSolubilityUnit();
							} else {
								codeString += brSolubilitySolvent.getCodeAndName();
								qualitativeString += "N/A";
								operatorString += brSolubilitySolvent.getOperator();
								valueString += brSolubilitySolvent.getSolubilityValue();
								upperValueString += new Double(new Double(brSolubilitySolvent.getSolubilityValue()).doubleValue() + 1.0).toString();
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
					// Do not allow ranges. Make upper value equal to lower value - 2004/12/02 Li Su.
					// sDunit.setValue("COMPOUND_REGISTRATION_SOLUBILITY_UPPER_VALUE", valueString);
				}

				sDunit.setValue("COMPOUND_REGISTRATION_HANDLING_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationHandlingCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_HANDLING_COMMENT", handleGTLT(selectedBatch.getRegInfo().getHandlingComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_STORAGE_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationStorageCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_STORAGE_COMMENT", handleGTLT(selectedBatch.getRegInfo().getStorageComments()).replaceAll("\n", " "));
				sDunit.setValue("COMPOUND_REGISTRATION_HAZARD_CODE", processCodes(selectedBatch.getRegInfo().getCompoundRegistrationHazardCodes()));
				sDunit.setValue("COMPOUND_REGISTRATION_BATCH_HAZARD_COMMENT", selectedBatch.getRegInfo().getHazardComments());
				sDunit.setValue("COMPOUND_REGISTRATION_PARENT_HAZARD_COMMENT", handleGTLT(selectedBatch.getCompound().getHazardComments()).replaceAll("\n", " "));

				// Sample Submission
				// Need to verify if this condition is required in 1.2
				// if (selectedBatch.getRegInfo() != null && selectedBatch.getRegInfo().getSubmissionStatus() != null
				// 		&& selectedBatch.getRegInfo().getSubmissionStatus().equals("Submitting")) {
				// Submission for container info
				if (selectedBatch.getRegInfo().getSubmitContainerList() != null && selectedBatch.getRegInfo().getSubmitContainerListSize() > 0) {
					String codeString = "";
					String amountValueString = "";
					String amountUnitString = "";
					int actualCount = 0;

					for (BatchSubmissionContainerInfoModel brBatchSubmissionContainerInfo : selectedBatch.getRegInfo().getSubmitContainerList()) {						
						if (brBatchSubmissionContainerInfo.getContainerType() == null ||
						    !brBatchSubmissionContainerInfo.getContainerType().equalsIgnoreCase(CeNConstants.CONTAINER_TYPE_VIAL)) {
						  continue;
						}
						if (!brBatchSubmissionContainerInfo.getSubmissionStatus().equals(BatchSubmissionContainerInfo.SUBMITTED) &&
							!(brBatchSubmissionContainerInfo.getBarCode() == null || brBatchSubmissionContainerInfo.getBarCode().equalsIgnoreCase("null"))) {
							if (actualCount > 0) {
								codeString += "\n";
								amountUnitString += "\n";
								amountValueString += "\n";
							}

							codeString += brBatchSubmissionContainerInfo.getBarCode();
							if (submitUsingMGs) {
								AmountModel amount = new AmountModel(UnitType.MASS);
								amount.setValue(brBatchSubmissionContainerInfo.getAmountValue());
								amount.getUnit().setCode(brBatchSubmissionContainerInfo.getAmountUnit());
								amountUnitString += "MG"; 
								amountValueString += amount.GetValueInStdUnitsAsDouble();
							} else {
								amountUnitString += brBatchSubmissionContainerInfo.getAmountUnit();
								amountValueString += Double.toString(brBatchSubmissionContainerInfo.getAmountValue());
							}

							actualCount++;
						}
					}

					if (actualCount > 0) {
						sDunit.setValue("COMPOUND_REGISTRATION_BAR_CODE", codeString);
						sDunit.setValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT_UNIT_CODE", amountUnitString);
						sDunit.setValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT", amountValueString);
					}
				}

				// Submission to Biologist for test
				List biologyList = selectedBatch.getRegInfo().getNewSubmitToBiologistTestList();
				if (biologyList != null && biologyList.size() > 0) {
					String scientistCode = "";
					String screenProtocolID = "";
					String screenDefaultAmount = "";

					String unitString = "MG";
					double total = 0.0;

					int byMMcount = 0;
					for (int i = 0; i < biologyList.size(); i++) {
						BatchSubmissionToScreenModel brSubmissionToBiologistTest = (BatchSubmissionToScreenModel) biologyList
								.get(i);
						if (brSubmissionToBiologistTest.isTestSubmittedByMm()) {
							if (!brSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToScreenModel.SUBMITTED)) {
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
							if (!brSubmissionToBiologistTest.getSubmissionStatus().equals(BatchSubmissionToScreenModel.SUBMITTED)) {
								AmountModel amount = new AmountModel(UnitType.MASS);
								amount.setValue(brSubmissionToBiologistTest.getAmountValue());
								amount.getUnit().setCode(brSubmissionToBiologistTest.getAmountUnit());
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

				if (sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE") == null || sDunit.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE").length() <= 0)
					sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", "NONE");
				else
					sDunit.setValue("COMPOUND_REGISTRATION_PROTECTION_CODE", selectedBatch.getRegInfo().getProtectionCode());

				// Adding SPID information
				if (pageModel.getPageHeader().getSpid() != null && !pageModel.getPageHeader().getSpid().equals("")) {
					sDunit.setValue("COMPOUND_REGISTRATION_SYNTHESIS_PLAN_ID", pageModel.getPageHeader().getSpid());
				}

				result = sDunit.toString();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e);
			}
		}

		return result;
	}

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

	public String getOwnerEmpId(String ntName) throws Exception {
		String empId = null;

		if (StringUtils.isNotBlank(ntName)) {
			try {
				SessionTokenDelegate smObj = new SessionTokenDelegate();
				CompoundManagementEmployee emp = smObj.getCompoundManagementEmployeeID(ntName);
				if (emp != null && stringIsNull(emp.getEmployeeId()) == false) {
					empId = emp.getEmployeeId();
				}
			} catch (Exception e) {
				if (e.getLocalizedMessage().indexOf("User not in CEN_USERS and not in COMPOUND_MANAGEMENT_EMPLOYEE") < 0)
					throw new Exception("Employee ID look up failed.", e);
				// hides any other errors !!!
			}
		}

		return empId;
	}

	public String getNtName(String ownerId) {
		if (StringUtils.isNotBlank(ownerId)) {
			try {
				SessionTokenDelegate sessionTokenDelegate = new SessionTokenDelegate();
				return sessionTokenDelegate.getNtUserNameByCompoundManagementEmployeeId(ownerId);
			} catch (Exception e) {
				log.error("Error getting NT Name: ", e);
			}
		}
		return "";
	}
	
	public String handleGTLT(String value) {
		if (stringIsNull(value)) {
			return "";
		}
		return value;
	}

	private String buildTagValue(List list) {
		String tagValueString = new String();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				tagValueString += "\n" + list.get(i);
			} else {
				tagValueString += list.get(i);
			}
		}
		return tagValueString;
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

	private List<String> processCodes(String codes) {
		if (StringUtils.isNotBlank(codes)) {
			String[] codesArray = codes.trim().split("\n");
			return new ArrayList<String>(Arrays.asList(codesArray));
		}
		return new ArrayList<String>();
	}
	
	private String validateNoteBookPageNumber(String npNumber) {
		String newString = npNumber;
		String prop = null;

		try {
			prop = CeNSystemProperties.getCeNServerSideSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_PAGE_TRUNCATE);
		} catch (Exception e) {
		}

		if (prop != null && prop.equalsIgnoreCase("true")) {
			if (npNumber.length() > 3)
				newString = npNumber.substring(npNumber.length() - 3, npNumber.length());
		}

		return newString;
	}

	public ProductBatchModel[] getProductBatchModelsFromSDFile(InputStream is) throws Exception {
		List<ProductBatchModel> pbModelList = new ArrayList<ProductBatchModel>();
		try {
			SdUnit sdu = null;
			SdfileIterator it = SdfileIteratorFactory.getIterator(is);
			int offset = 0;
			while ((sdu = it.getNext()) != null) {
				offset++;
				
				if (!sdu.isValidMol()) {
					String msg = "Invalid sd file\noffset:" + offset + "\n" + sdu.getInvalidDescription();
					log.info(msg);
				}
								
				ProductBatchModel batch = new ProductBatchModel();
				ParentCompoundModel compound = new ParentCompoundModel();
				
				// Sketch
				compound.setStringSketch(sdu.getMol().getBytes());
				compound.setStringSketchFormat(CeNConstants.STRING_SKETCH_FORMAT_MOL);
				
				// Compound MF
				
				String parentMF = sdu.getValue("COMPOUND_REGISTRATION_PARENT_MF");
				
				if (parentMF == null) {
					 parentMF = sdu.getValue("CAL_PARENT_MF");
				}
				
				compound.setMolFormula(parentMF);
				
				// Compound MW
				String stringAmountValue = sdu.getValue("CAL_PARENT_MW");
				compound.setMolWgt(Double.parseDouble(stringAmountValue != null ? stringAmountValue : "0"));
				
				// Stereoisomer code
				compound.setStereoisomerCode(sdu.getValue("STEREOISOMER_CODE"));
				
				batch.setCompound(compound);
				
				// Vnv info
				batch.getRegInfo().getBatchVnVInfo().setStatus(sdu.getValue("VnV_Result"));
				batch.getRegInfo().getBatchVnVInfo().setAssignedStereoIsomerCode(sdu.getValue("VnV_SIC"));
				batch.getRegInfo().getBatchVnVInfo().setModified(true);
				
				// Compound source and details
				batch.getRegInfo().setCompoundSource(sdu.getValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_CODE"));
				batch.getRegInfo().setCompoundSourceDetail(sdu.getValue("COMPOUND_REGISTRATION_COMPOUND_SOURCE_DETAIL_CODE"));
				
				// Total amount made
				String unitValue = sdu.getValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_UNIT_CODE");
				UnitType type = null;
				if (StringUtils.isNotBlank(unitValue)) {
					Unit2 unit = new Unit2(unitValue);
					type = unit.getType();
				}
				stringAmountValue = sdu.getValue("COMPOUND_REGISTRATION_TOTAL_AMOUNT_MADE_VALUE");
				AmountModel amountModel = new AmountModel(type != null ? type : UnitType.MASS, Double.parseDouble(stringAmountValue != null ? stringAmountValue : "0"));
				batch.setTotalWeightAmount(amountModel);
				
				// Batch owner
				batch.setOwner(getNtName(sdu.getValue("COMPOUND_REGISTRATION_BATCH_OWNER_ID")));
				
				// Batch creator
				batch.setSynthesizedBy(getNtName(sdu.getValue("COMPOUND_REGISTRATION_BATCH_CREATOR_ID")));
				
				// Structure comment
				compound.setStructureComments(sdu.getValue("COMPOUND_REGISTRATION_STRUCTURE_COMMENT"));
				
				// Compound reg number
				compound.setRegNumber(sdu.getValue("COMPOUND_REGISTRATION_GPN_TO_ASSIGN_NEW_BATCH"));
				
				// Supplier code and reg number
				batch.getRegInfo().getVendorInfo().setCode(sdu.getValue("COMPOUND_REGISTRATION_SUPPLIER_CODE"));
				batch.getRegInfo().getVendorInfo().setSupplierCatalogRef(sdu.getValue("COMPOUND_REGISTRATION_SUPPLIER_REGISTRY_NUMBER"));
				
				// Salt code and equivs
				batch.setSaltForm(new SaltFormModel(sdu.getValue("COMPOUND_REGISTRATION_GLOBAL_SALT_CODE")));
				stringAmountValue = sdu.getValue("COMPOUND_REGISTRATION_GLOBAL_SALT_MOLE");
				batch.setSaltEquivs(Double.parseDouble(stringAmountValue != null ? stringAmountValue : "0"));
				
				// Compound state
				batch.getRegInfo().setCompoundState(sdu.getValue("COMPOUND_REGISTRATION_COMPOUND_STATE"));
				
				// Batch comment
				batch.setComments(sdu.getValue("COMPOUND_REGISTRATION_BATCH_COMMENT"));
				
				// Batch MF
				batch.setMolecularFormula(sdu.getValue("COMPOUND_REGISTRATION_BATCH_MF"));
				
				// Batch MW
				stringAmountValue = sdu.getValue("COMPOUND_REGISTRATION_BATCH_MW");
				AmountModel batchMW = new AmountModel();
				batchMW.setValue(Double.parseDouble(stringAmountValue != null ? stringAmountValue : "0"));
				batch.setMolecularWeightAmount(batchMW);
				
				// Batch Hit ID
				batch.getRegInfo().setHitId(sdu.getValue("COMPOUND_REGISTRATION_HIT_ID"));
				
				// Batch Intermediate or Test
				batch.getRegInfo().setIntermediateOrTest(sdu.getValue("COMPOUND_REGISTRATION_COMPOUND_IS_INTERMEDIATE"));
				
				// Precursors
				// TODO: Make precursors part (COMPOUND_REGISTRATION_REACTION_COMPONENT, COMPOUND_REGISTRATION_REACTION_COMPONENT_LABEL) for parallel
				
				batch.setPrecursors(processCodes(sdu.getValue("COMPOUND_REGISTRATION_PRECURSOR_COMPOUND"))); // This is only for singleton and conception
				
				// Analytical purity list

				List<String> analyticalPurityCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_ANALYTICAL_PURITY_CODE"));
				List<String> purityOperatorList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_PURITY_OPERATOR"));
				List<String> purityValueList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_PURITY_VALUE"));
				
				List<PurityModel> analyticalPurityList = new ArrayList<PurityModel>();
				
				for (int i = 0; i < analyticalPurityCodeList.size(); ++i) {
					PurityModel purityModel = new PurityModel();
					AmountModel purityValueAmount = new AmountModel();
					
					String purityCode = analyticalPurityCodeList.get(i);
					String purityOperator = purityOperatorList.get(i);
					String purityValue = purityValueList.get(i);
					
					purityValueAmount.setValue(Double.parseDouble(purityValue != null ? purityValue : "0"));
					
					if (purityOperator.equals("EQ")) {
						purityOperator = "=";
					} else if (purityOperator.equals("LT")) {
						purityOperator = "<";
					} else if (purityOperator.equals("GT")) {
						purityOperator = ">";
					} else if (purityOperator.equals("AP")) {
						purityOperator = "~";
					}
					
					purityModel.setCode(purityCode);
					purityModel.setOperator(purityOperator);
					purityModel.setPurityValue(purityValueAmount);
					
					analyticalPurityList.add(purityModel);
				}
				
				batch.setAnalyticalPurityList((ArrayList<PurityModel>) analyticalPurityList);
				
				// Residual solvent list	
				
				List<String> residualSolventCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CODE"));
				List<String> residualSolventMoleList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_MOLE"));
				
				List<BatchResidualSolventModel> residualSolventList = new ArrayList<BatchResidualSolventModel>();
				
				for (int i = 0; i < residualSolventCodeList.size(); ++i) {
					BatchResidualSolventModel residualSolventModel = new BatchResidualSolventModel();
					
					String code = residualSolventCodeList.get(i);
					String mole = residualSolventMoleList.get(i);
					
					residualSolventModel.setCodeAndName(code);
					residualSolventModel.setEqOfSolvent(Double.parseDouble(mole != null ? mole : "0"));
					
					residualSolventList.add(residualSolventModel);
				}
				
				batch.getRegInfo().setResidualSolventList((ArrayList<BatchResidualSolventModel>) residualSolventList);
				
				// Solubility solvent list
				
				List<String> solubilitySolventCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CODE"));
				List<String> qualitativeSolubilityList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_QUALITATIVE_SOLUBILITY"));
				List<String> solubilityOperatorList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SOLUBILITY_OPERATOR"));
				List<String> solubilityValueList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SOLUBILITY_VALUE"));
				List<String> solubilityUnitCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SOLUBILITY_UNIT_CODE"));
				
				List<BatchSolubilitySolventModel> solubilitySolventModelList = new ArrayList<BatchSolubilitySolventModel>();
				
				for (int i = 0; i < solubilitySolventCodeList.size(); ++i) {
					BatchSolubilitySolventModel solubilitySolventModel = new BatchSolubilitySolventModel();
					
					String solubilitySolventCode = solubilitySolventCodeList.get(i);
					String qualitativeSolubility = qualitativeSolubilityList.get(i);
					String solubilityOperator = solubilityOperatorList.get(i);
					String solubilityValue = solubilityValueList.get(i);
					String solubilityUnitCode = solubilityUnitCodeList.get(i);
					
					solubilitySolventModel.setCodeAndName(solubilitySolventCode);
					solubilitySolventModel.setQualiString(qualitativeSolubility);
					solubilitySolventModel.setOperator(solubilityOperator);
					solubilitySolventModel.setSolubilityValue(Double.parseDouble((solubilityValue == null || solubilityValue.equals("N/A")) ? "0" : solubilityValue));
					solubilitySolventModel.setSolubilityUnit(solubilityUnitCode);
					
					solubilitySolventModelList.add(solubilitySolventModel);
				}
				
				batch.getRegInfo().setSolubilitySolventList((ArrayList<BatchSolubilitySolventModel>) solubilitySolventModelList);
				
				// CompoundRegistration handling codes
				batch.getRegInfo().setCompoundRegistrationHandlingCodes((ArrayList<String>) processCodes(sdu.getValue("COMPOUND_REGISTRATION_HANDLING_CODE")));
				
				// Batch handling comments
				batch.getRegInfo().setHandlingComments(sdu.getValue("COMPOUND_REGISTRATION_BATCH_HANDLING_COMMENT"));
				
				// CompoundRegistration storage codes
				batch.getRegInfo().setCompoundRegistrationStorageCodes((ArrayList<String>) processCodes(sdu.getValue("COMPOUND_REGISTRATION_STORAGE_CODE")));
				
				// Batch storage comments
				batch.getRegInfo().setStorageComments(sdu.getValue("COMPOUND_REGISTRATION_BATCH_STORAGE_COMMENT"));
				
				// CompoundRegistration hazard codes
				batch.getRegInfo().setCompoundRegistrationHazardCodes((ArrayList<String>) processCodes(sdu.getValue("COMPOUND_REGISTRATION_HAZARD_CODE")));
				
				// Batch hazard comments
				batch.getRegInfo().setHazardComments(sdu.getValue("COMPOUND_REGISTRATION_BATCH_HAZARD_COMMENT"));
				
				// Compound hazard comments
				compound.setHazardComments(sdu.getValue("COMPOUND_REGISTRATION_PARENT_HAZARD_COMMENT"));
				
				// Submit container list

				List<String> submitContainerBarCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_BAR_CODE"));
				List<String> submitContainerUnitCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT_UNIT_CODE"));
				List<String> submitContainerContainerAmountList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_CONTAINER_AMOUNT"));
				
				List<BatchSubmissionContainerInfoModel> submitContainerList = new ArrayList<BatchSubmissionContainerInfoModel>();
				
				for (int i = 0; i < submitContainerBarCodeList.size(); ++i) {
					BatchSubmissionContainerInfoModel submitContainerModel = new BatchSubmissionContainerInfoModel();
					
					String barCode = submitContainerBarCodeList.get(i);
					String unitCode = submitContainerUnitCodeList.get(i);
					String amount = submitContainerContainerAmountList.get(i);
					
					submitContainerModel.setBarCode(barCode);
					submitContainerModel.setAmountUnit(unitCode);
					submitContainerModel.setAmountValue(Double.parseDouble(amount != null ? amount : "0"));
					
					submitContainerList.add(submitContainerModel);
				}
				
				batch.getRegInfo().setSubmitContainerList((ArrayList<BatchSubmissionContainerInfoModel>) submitContainerList);
				
				// Submit to biologist test list

				List<String> scientistsCodeList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SCREEN_SCIENTIST_CODE"));
				List<String> screenProtocolIdList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SCREEN_PROTOCOL_ID"));
				List<String> screenDefaultAmountList = processCodes(sdu.getValue("COMPOUND_REGISTRATION_SCREEN_DEFAULT_AMOUNT"));
				
				List<BatchSubmissionToScreenModel> submitToBiologistTestList = new ArrayList<BatchSubmissionToScreenModel>(); 
				
				for (int i = 0; i < scientistsCodeList.size(); ++i) {
					BatchSubmissionToScreenModel subToScreenModel = new BatchSubmissionToScreenModel();
					
					String scientistsCode = scientistsCodeList.get(i);
					String screenProtocolId = screenProtocolIdList.get(i);
					String screenDefaultAmount = screenDefaultAmountList.get(i);
					
					subToScreenModel.setScientistCode(scientistsCode);
					subToScreenModel.setScreenProtocolId(screenProtocolId);
					subToScreenModel.setAmountValue(Double.parseDouble(screenDefaultAmount != null ? screenDefaultAmount : "0"));
					subToScreenModel.setAmountUnit("MG");
					
					submitToBiologistTestList.add(subToScreenModel);		
				}
				
				batch.getRegInfo().setSubmitToBiologistTestList((ArrayList<BatchSubmissionToScreenModel>) submitToBiologistTestList);
				
				// Batch protection code
				batch.getRegInfo().setProtectionCode(sdu.getValue("COMPOUND_REGISTRATION_PROTECTION_CODE"));
				
				batch.recalcAmounts();
				
				batch.setUserAdded(true);
				batch.getCompound().setModelChanged(true);
				batch.getRegInfo().setModelChanged(true);
				batch.setModelChanged(true);
				
				// add to list
				pbModelList.add(batch);
			}
		} catch (Exception e) {
			log.error("Error while extracting ProductBatchModel from SDF: " + e);
			throw e;
		}
		return pbModelList.toArray(new ProductBatchModel[0]);
	}
	
	public MonomerBatchModel[] getMonomerBatchModelsFromSDFile(String sdfile) {
		return null;
	}
	
	/**
	 * Created because objects representing null will return "null" when the toString is called
	 * 
	 * @param test
	 * @return
	 */
	private boolean stringIsNull(String test) {
		return StringUtils.isEmpty(test) || NULL_STRING.equalsIgnoreCase(test);
	}
}