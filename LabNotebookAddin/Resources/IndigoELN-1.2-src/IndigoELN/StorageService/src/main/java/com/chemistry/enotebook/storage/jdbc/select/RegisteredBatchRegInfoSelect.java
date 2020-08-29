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
package com.chemistry.enotebook.storage.jdbc.select;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegisteredBatchRegInfoSelect extends AbstractSelect {
	
	private static final Log log = LogFactory.getLog(RegisteredBatchRegInfoSelect.class);

	public RegisteredBatchRegInfoSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		String xmlMetadata = rs.getString("REG_XML_METADATA");

		String registration_error_msg = CeNXMLParser.getXmlProperty(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Error_Msg");
		String residual_solvent_list = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Residual_Solvent_List");
		String solubility_solvent_list = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Solubility_Solvent_List");
		String submit_to_biologist_test_list = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Submit_To_Biologist_Test_List");
		String submit_container_list = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Submit_Container_List");
		String compound_reg_hazard_codes = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/CompoundRegistration_Hazard_Codes");
		String compound_reg_handling_codes = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/CompoundRegistration_Handling_Codes");
		String compound_reg_storage_codes = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/CompoundRegistration_Storage_Codes");
		
		BatchRegInfoModel regBatchInfo = new BatchRegInfoModel(rs.getString("REG_BATCH_KEY"), true);
		regBatchInfo.setBatchKey(rs.getString("BATCH_KEY"));
		regBatchInfo.setInternalBatchNumber(rs.getString("BATCH_NUMBER"));
		regBatchInfo.setConversationalBatchNumber(rs.getString("R_CONVERSATIONAL_BATCH_NUMBER"));
		regBatchInfo.setParentBatchNumber(rs.getString("R_PARENT_BATCH_NUMBER"));
		regBatchInfo.setCompoundSource(rs.getString("SOURCE_CODE"));
		regBatchInfo.setCompoundSourceDetail(rs.getString("SOURCE_DETAIL_CODE"));
		regBatchInfo.setRegistrationDate(rs.getTimestamp("REGISTRATION_DATE"));
//		regBatchInfo.setRegistrationDate(rs.getString("REGISTRATION_DATE"));
		regBatchInfo.setRegistrationStatus(rs.getString("REGISTRATION_STATUS"));
		regBatchInfo.setSubmissionStatus(rs.getString("SUBMISSION_STATUS"));
		regBatchInfo.setStatus(rs.getString("STATUS"));
		regBatchInfo.setJobId(rs.getString("JOB_ID"));
		regBatchInfo.setBatchTrackingId(CommonUtils.toInteger(rs.getString("BATCH_TRACKING_ID")));
		regBatchInfo.setErrorMsg(registration_error_msg);
		regBatchInfo.setHitId(rs.getString("HIT_ID"));
		String residualSolventList = residual_solvent_list;
		regBatchInfo.setResidualSolventList(new ArrayList<BatchResidualSolventModel>(getBatchResidualSolventModelList(CommonUtils.convertXMLToArrayList(residualSolventList))));
		String solubilitySolventList = solubility_solvent_list;
		regBatchInfo.setSolubilitySolventList(new ArrayList<BatchSolubilitySolventModel>(getBatchSolubilitySolventModelList(CommonUtils.convertXMLToArrayList(solubilitySolventList))));
		String biologistTestList = submit_to_biologist_test_list; 
		regBatchInfo.setSubmitToBiologistTestList(new ArrayList<BatchSubmissionToScreenModel>(getScreenModelList(CommonUtils.convertXMLToArrayList(biologistTestList))));
		String containerList = submit_container_list;
		regBatchInfo.setSubmitContainerList(new ArrayList<BatchSubmissionContainerInfoModel>(getContainerModelList(CommonUtils.convertXMLToArrayList(containerList))));
		regBatchInfo.setBatchVnVInfo(this.getBatchVnVInfoModel(rs));
		String compoundRegistrationHazardCodes = compound_reg_hazard_codes;
		regBatchInfo.setCompoundRegistrationHazardCodes(CommonUtils.convertXMLToArrayList(compoundRegistrationHazardCodes));
		String compoundRegistrationHandlingCodes = compound_reg_handling_codes;
		regBatchInfo.setCompoundRegistrationHandlingCodes(CommonUtils.convertXMLToArrayList(compoundRegistrationHandlingCodes));
		String compoundRegistrationStorageCodes = compound_reg_storage_codes;
		regBatchInfo.setCompoundRegistrationStorageCodes(CommonUtils.convertXMLToArrayList(compoundRegistrationStorageCodes));
		regBatchInfo.setCompoundAggregationStatus(rs.getString("COMPOUND_AGGREGATION_STATUS"));
		regBatchInfo.setCompoundAggregationStatusMessage(rs.getString("CMPD_AGGREGATION_STATUS_MSG"));
		regBatchInfo.setPurificationServiceStatus(rs.getString("PURIFICATION_SERVICE_STATUS"));
		regBatchInfo.setPurificationServiceStatusMessage(rs.getString("PUR_SERVICE_STATUS_MSG"));
		regBatchInfo.setCompoundManagementStatus(rs.getString("COMPOUND_MANAGEMENT_STATUS"));
		regBatchInfo.setCompoundManagementStatusMessage(rs.getString("COMPOUND_MGMT_STATUS_MESSAGE"));
		
		if (StringUtils.equals(rs.getString("SUBMISSION_STATUS"), CeNConstants.REGINFO_SUBMITTED)) {
			regBatchInfo.setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMITTED + " - " + rs.getString("STATUS"));
		}
		
		regBatchInfo.setCompoundRegistrationStatusMessage(rs.getString("COMPOUND_REG_STATUS_MESSAGE"));
		regBatchInfo.setOffset(rs.getInt("COMPOUND_REGISTRATION_OFFSET"));
		regBatchInfo.setVendorInfo(this.getExternalSupplier(rs));
		regBatchInfo.setCompoundState(rs.getString("COMPOUND_STATE"));
		regBatchInfo.setProtectionCode(rs.getString("PROTECTION_CODE"));
		regBatchInfo.setRegStatus(rs.getString("REGISTRATION_STATUS"));
		regBatchInfo.setStorageComments(rs.getString("BATCH_STORAGE_COMMENT"));
		regBatchInfo.setHazardComments(rs.getString("BATCH_HAZARD_COMMENT"));
		regBatchInfo.setHandlingComments(rs.getString("BATCH_HANDLING_COMMENT"));
		regBatchInfo.getMeltPointRange().setLower(CommonUtils.toDouble(rs.getString("MELT_POINT_VAL_LOWER")));
		regBatchInfo.getMeltPointRange().setUpper(CommonUtils.toDouble(rs.getString("MELT_POINT_VAL_UPPER")));
		regBatchInfo.getMeltPointRange().setComment(rs.getString("MELT_POINT_COMMENTS"));
		String flag = rs.getString("PRODUCT_FLAG");
		if (flag != null)
			regBatchInfo.setProductFlag(flag.equals("Y") ? true : false);
		else
			regBatchInfo.setProductFlag(false);
		regBatchInfo.setOwner(rs.getString("BATCH_OWNER"));
		regBatchInfo.setComments(rs.getString("BATCH_COMMENTS"));
		// regBatchInfo.setProjectTrackingCode(rs.getString("PROJECT_TRACKING_CODE"));
		regBatchInfo.setLoadedFromDB(true);
		regBatchInfo.setModelChanged(false);
		return regBatchInfo;
	}

	/*
	 * returns BatchVnVInfoModel
	 * 
	 */
	private BatchVnVInfoModel getBatchVnVInfoModel(ResultSet rs) throws SQLException {
		String xmlMetadata = rs.getString("REG_XML_METADATA");

		String vnv_mol_data = CeNXMLParser.getXmlProperty(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Batch_Vn_VInfo/Mol_Data");
		String vnv_suggested_sic_list = CeNXMLParser.getXmlPropertyAsXml(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Batch_Vn_VInfo/Suggested_SICList");
		String vnv_error_msg = CeNXMLParser.getXmlProperty(xmlMetadata, "/Registered_Batch_Properties/Meta_Data/Reg_Info/Batch_Vn_VInfo/Error_Msg");
		
		BatchVnVInfoModel regBatchVnVInfo = new BatchVnVInfoModel();
		regBatchVnVInfo.setStatus(rs.getString("VNV_STATUS"));
		regBatchVnVInfo.setMolData(vnv_mol_data);
		String sicList = vnv_suggested_sic_list;
		regBatchVnVInfo.setSuggestedSICList(CommonUtils.convertXMLToArrayList(sicList));
		regBatchVnVInfo.setErrorMsg(vnv_error_msg);

		return regBatchVnVInfo;
	}
	
	private BatchSubmissionToScreenModel getScreenModel(String xmlString)
	{
		BatchSubmissionToScreenModel model = null;
		xmlString = xmlString.trim();
		if(StringUtils.isBlank(xmlString))
		{
			return model;
		}
		model = new BatchSubmissionToScreenModel();
		model.setAdditionOrder(Integer.parseInt(getXMLElementValueFromString(xmlString, "Addition_Order")));
		model.setCompoundAggregationScreenPanelKey(Long.parseLong(getXMLElementValueFromString(xmlString, "CompoundAggregation_Screenpanel_Key")));
		if(xmlString.indexOf("<Screen_Protocol_Title>") > 0)
		{
		model.setScreenProtocolTitle(getXMLElementValueFromString(xmlString, "Screen_Protocol_Title"));
		}
		
		//IF CompoundAggregation key is zero that means this legacy screen data 
		//if(model.getCompoundAggregationScreenPanelKey() == 0) {
		String screenProtocolId = getXMLElementValueFromString(xmlString, "Screen_Protocol_Id");
		if(screenProtocolId != null) {
		
			model.setAmountUnit(getXMLElementValueFromString(xmlString, "Amount_Unit"));
			String val = getXMLElementValueFromString(xmlString, "Amount_Value");
			if(val != null && val.trim().length() > 0)
			{
				model.setAmountValue(Double.parseDouble(val));
			}
			model.setContainerType(getXMLElementValueFromString(xmlString, "Container_Type"));
			model.setMinAmountUnit(getXMLElementValueFromString(xmlString, "Min_Amount_Unit"));
			String val2 = getXMLElementValueFromString(xmlString, "Min_Amount_Value");
			if(val2 != null && val2.trim().length() > 0)
			{
				model.setMinAmountValue(Double.parseDouble(val2));
			}
			model.setScientistCode(getXMLElementValueFromString(xmlString, "Scientist_Code"));
			model.setScientistName(getXMLElementValueFromString(xmlString, "Scientist_Name"));
			model.setScreenCode(getXMLElementValueFromString(xmlString, "Screen_Code"));
			model.setScreenProtocolId(getXMLElementValueFromString(xmlString, "Screen_Protocol_Id"));
			model.setSiteCode(getXMLElementValueFromString(xmlString, "Site_Code"));
			model.setSubmissionStatus(getXMLElementValueFromString(xmlString, "Submission_Status"));
			model.setSubmittedByMm(getXMLElementValueFromString(xmlString, "Submitted_By_Mm"));
		}
		return model;
	}

	private List<BatchSubmissionToScreenModel> getScreenModelList(List<String> xmlStringsList) {
		List<BatchSubmissionToScreenModel> list = new ArrayList<BatchSubmissionToScreenModel>();

		if (xmlStringsList == null || xmlStringsList.size() == 0) {
			return list;
		}

		log.debug("Total Screen panels loaded for PB." + xmlStringsList.size());

		for (String str : xmlStringsList) {
			list.add(getScreenModel(str));
		}

		return list;
	}

	private BatchResidualSolventModel getBatchResidualSolventModel(String xmlString) {
		BatchResidualSolventModel model = new BatchResidualSolventModel();
		
		model.setAdditionOrder(Integer.parseInt(xmlString.substring(xmlString.indexOf("<Addition_Order>") + 16, xmlString.indexOf("</Addition_Order>"))));
		model.setCodeAndName(xmlString.substring(xmlString.indexOf("<Code_And_Name>") + 15, xmlString.indexOf("</Code_And_Name>")));
		
		if (xmlString.indexOf("<Comments>") > 0) {
			model.setComments(xmlString.substring(xmlString.indexOf("<Comments>") + 10, xmlString.indexOf("</Comments>")));
		}
		
		model.setEqOfSolvent(Double.parseDouble(xmlString.substring(xmlString.indexOf("<Eq_Of_Solvent>") + 15, xmlString.indexOf("</Eq_Of_Solvent>"))));
		
		return model;
	}

	private List<BatchResidualSolventModel> getBatchResidualSolventModelList(List<String> xmlStringsList) {
		List<BatchResidualSolventModel> list = new ArrayList<BatchResidualSolventModel>();

		if (xmlStringsList == null || xmlStringsList.size() == 0) {
			return list;
		}

		log.debug("Total Residual Solvents loaded for PB." + xmlStringsList.size());

		for (String str : xmlStringsList) {
			list.add(getBatchResidualSolventModel(str));
		}

		return list;
	}

	private BatchSolubilitySolventModel getBatchSolubilitySolventModel(String xmlString) {
		BatchSolubilitySolventModel model = new BatchSolubilitySolventModel();
		
		model.setAdditionOrder(Integer.parseInt(xmlString.substring(xmlString.indexOf("<Addition_Order>") + 16, xmlString.indexOf("</Addition_Order>"))));
		model.setCodeAndName(xmlString.substring(xmlString.indexOf("<Code_And_Name>") + 15, xmlString.indexOf("</Code_And_Name>")));
		
		if (xmlString.indexOf("<Comments>") > 0) {
			model.setComments(xmlString.substring(xmlString.indexOf("<Comments>") + 10, xmlString.indexOf("</Comments>")));
		}
		
		model.setQualitative(Boolean.valueOf(xmlString.substring(xmlString.indexOf("<Qualitative>") + 13, xmlString.indexOf("</Qualitative>"))).booleanValue());
		model.setQuantitative(Boolean.valueOf(xmlString.substring(xmlString.indexOf("<Quantitative>") + 14, xmlString.indexOf("</Quantitative>"))).booleanValue());
		
		if (model.getQualitative()) {
			model.setQualiString(xmlString.substring(xmlString.indexOf("<Quali_String>") + 14, xmlString.indexOf("</Quali_String>")));
		} else if (model.getQuantitative()) {
			String oper = xmlString.substring(xmlString.indexOf("<Operator>") + 10, xmlString.indexOf("</Operator>"));
			model.setOperator(oper);
			model.setSolubilityUnit(xmlString.substring(xmlString.indexOf("<Solubility_Unit>") + 17, xmlString.indexOf("</Solubility_Unit>")));
			// upper value needs to be set
			model.setSolubilityValue(Double.parseDouble(xmlString.substring(xmlString.indexOf("<Solubility_Value>") + 18, xmlString.indexOf("</Solubility_Value>"))));
		}
		
		return model;
	}

	private List<BatchSolubilitySolventModel> getBatchSolubilitySolventModelList(List<String> xmlStringsList) {
		List<BatchSolubilitySolventModel> list = new ArrayList<BatchSolubilitySolventModel>();

		if (xmlStringsList == null || xmlStringsList.size() == 0) {
			return list;
		}

		log.debug("Total solubility Solvents loaded for PB." + xmlStringsList.size());

		for (String str : xmlStringsList) {
			list.add(getBatchSolubilitySolventModel(str));
		}

		return list;
	}

	/*
	 * returns Vendor Info
	 */
	private ExternalSupplierModel getExternalSupplier(ResultSet rs) throws SQLException {
		ExternalSupplierModel vendor = new ExternalSupplierModel();
		vendor.setCode(rs.getString("SUPPLIER_CODE"));
		vendor.setSupplierCatalogRef(rs.getString("SUPPLIER_REGISTRY_NUMBER"));

		return vendor;
	}

	private List<BatchSubmissionContainerInfoModel> getContainerModelList(List<String> xmlStringsList) {
		List<BatchSubmissionContainerInfoModel> list = new ArrayList<BatchSubmissionContainerInfoModel>();

		if (xmlStringsList == null || xmlStringsList.size() == 0) {
			return list;
		}
		
		log.debug("Total Containers loaded for PB." + xmlStringsList.size());
		
		for (String str : xmlStringsList) {
			list.add(getContainerSubmissionModel(str));
		}

		return list;
	}

	/**
	 * Question: What is the purpose of this model? Existence? Called by
	 * getContainerModelList will have null entries
	 * 
	 * @param xmlString
	 * @return
	 */
	private BatchSubmissionContainerInfoModel getContainerSubmissionModel(String xmlString) {
		log.debug("In RegisteredBatchRegInfoSelect.getContainerSubmissionModel(): xmlString is " + xmlString);

		BatchSubmissionContainerInfoModel model = null;
		PurificationServiceSubmisionParameters purificationServiceparameters = null;

		xmlString = xmlString.trim();

		if (StringUtils.isEmpty(xmlString)) {
			return model;
		}

		model = new BatchSubmissionContainerInfoModel();
		purificationServiceparameters = new PurificationServiceSubmisionParameters();

		model.setBarCode(getXMLElementValueFromString(xmlString, "BarCode"));
		model.setTubeGuid(getXMLElementValueFromString(xmlString, "Tube_Guid"));
		model.setAmountUnit(getXMLElementValueFromString(xmlString, "Amount_Unit"));

		String val = getXMLElementValueFromString(xmlString, "Amount_Value");
		if (StringUtils.isNotBlank(val)) { // isNumeric() checks only for digits, so "1.2" would return false
			model.setAmountValue(Double.parseDouble(val));
		}

		model.setMoleUnit(getXMLElementValueFromString(xmlString, "Mole_Unit"));

		String val2 = getXMLElementValueFromString(xmlString, "Mole_Value");
		if (StringUtils.isNotBlank(val2)) {
			model.setMoleValue(Double.parseDouble(val2));
		}

		model.setVolumeUnit(getXMLElementValueFromString(xmlString, "Volume_Unit"));

		if (StringUtils.contains(xmlString, "<Volume_Value>")) {
			String val3 = getXMLElementValueFromString(xmlString, "Volume_Value");
			if (StringUtils.isNotBlank(val3)) {
				model.setVolumeValue(Double.parseDouble(val3));
			}
		}

		model.setSolvent(getXMLElementValueFromString(xmlString, "Solvent"));
		model.setContainerType(getXMLElementValueFromString(xmlString, "Container_Type"));
		model.setContainerTypeCode(getXMLElementValueFromString(xmlString, "Container_Type_Code"));
		model.setContainerLocation(getXMLElementValueFromString(xmlString, "Location_Code"));

		if (StringUtils.contains(xmlString, "<Site_Code>")) {
			model.setSiteCode(getXMLElementValueFromString(xmlString, "Site_Code"));
		}

		model.setSubmissionStatus(getXMLElementValueFromString(xmlString, "Submission_Status"));

		model.setPurificationServiceParamatersSetByUser(StringUtils.equals(getXMLElementValueFromString(xmlString, "purificationServiceParameters_Set"), "YES") ? true : false);

		// Load PurificationService parameters
		String rxnScaleAmount = getXMLElementValueFromString(xmlString, "purification_service_reaction_scale_amount");
		if (StringUtils.isNotBlank(rxnScaleAmount)) {
			purificationServiceparameters.setRecationScaleAmount(new AmountModel(UnitType.MOLES, Double.parseDouble(rxnScaleAmount)));
		}
		
		purificationServiceparameters.setArchivePlate(getXMLElementValueFromString(xmlString, "purification_service_archive_plate"));

		String archiveVolume = getXMLElementValueFromString(xmlString, "purification_service_archive_volume");
		if (StringUtils.isNotBlank(archiveVolume)) {
			purificationServiceparameters.setArchiveVolume(Double.parseDouble(archiveVolume));
		}

		purificationServiceparameters.setSampleWorkUp(getXMLElementValueFromString(xmlString, "purification_service_sample_workup"));

		String analyticalPlateConc = getXMLElementValueFromString(xmlString, "purification_service_analytical_plate_conc");
		if (StringUtils.isNotBlank(analyticalPlateConc)) {
			purificationServiceparameters.setAnalyticalPlateConcVolume(new AmountModel(UnitType.SCALAR, Double.parseDouble(analyticalPlateConc)));
		}

		purificationServiceparameters.setDestinationLab(getXMLElementValueFromString(xmlString, "purification_service_destination_lab"));
		purificationServiceparameters.setSaltType(getXMLElementValueFromString(xmlString, "purification_service_salt_type"));

		String isInorganic = getXMLElementValueFromString(xmlString, "purification_service_inorganic");
		if (StringUtils.isNotBlank(isInorganic)) {
			purificationServiceparameters.setInorganicByProductSaltPresent(Boolean.parseBoolean(isInorganic));
		}

		String separateIsomers = getXMLElementValueFromString(xmlString, "purification_service_separate_the_isomers");
		if (StringUtils.isNotBlank(separateIsomers)) {
			purificationServiceparameters.setSeperateTheIsomers(Boolean.parseBoolean(separateIsomers));
		}
		purificationServiceparameters.setComment(getXMLElementValueFromString(xmlString, "purification_service_comment"));

		purificationServiceparameters.setModifiers(StringUtils.split(getXMLElementValueFromString(xmlString, "purification_service_modifiers"), '|'));
		purificationServiceparameters.setLoadedFromDB(true);

		model.setPurificationServiceParameters(purificationServiceparameters);

		return model;
	}
}
