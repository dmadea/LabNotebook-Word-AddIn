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

import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoicProductBatchSearchSelect extends AbstractSelect{
	private static final Log log = LogFactory.getLog(StructureSelect.class);

	public StoicProductBatchSearchSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		String xmlMetadata = rs.getString("BATCH_XML_METADATA");
		
		String stoich_comments = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Stoich_Comments");
		
		ParentCompoundModel compound = new ParentCompoundModel(rs.getString("STRUCT_KEY"));

		compound.setChemicalName(rs.getString("CHEMICAL_NAME"));

		compound.setStringSketch(rs.getBytes("STRUCT_SKETCH"));
		compound.setNativeSketch(rs.getBytes("NATIVE_STRUCT_SKETCH"));
		compound.setViewSketch(rs.getBytes("STRUCT_IMAGE"));
		
		compound.setMolFormula(rs.getString("MOLECULAR_FORMULA"));
		compound.setMolWgt(CommonUtils.toDouble(rs.getString("MOLECULAR_WEIGHT")));
		compound.setVirtualCompoundId(rs.getString("VIRTUAL_COMPOUND_ID"));
		
		String registrationNumber = rs.getString("REGISTRATION_NUMBER");
		if (StringUtils.isBlank(registrationNumber)) {
			registrationNumber = rs.getString("R_PARENT_BATCH_NUMBER");
		}
		if (StringUtils.isNotBlank(registrationNumber)) {
			compound.setRegNumber(registrationNumber);
		} else {
			compound.setRegNumber("");
		}
		
		compound.setComments(stoich_comments);
		compound.setNativeSketchFormat(rs.getString("NATIVE_STRUCT_SKTH_FRMT"));
		compound.setStringSketchFormat(rs.getString("STRUCT_SKTH_FRMT"));
		compound.setCompoundName(rs.getString("COMPOUND_NAME"));
		String createdByNotebookStr = rs.getString("CREATED_BY_NOTEBOOK");
		
		boolean createdByNotebookFlag = false;
		if (createdByNotebookStr.equalsIgnoreCase("true")) {
			createdByNotebookFlag = true;
		}
		
		compound.setCreatedByNotebook(createdByNotebookFlag);
		compound.setExactMass(CommonUtils.toDouble(rs.getString("EXACT_MASS")));
		compound.setStereoisomerCode(rs.getString("STEREOISOMER_CODE"));
		compound.setCASNumber(rs.getString("CAS_NUMBER"));
		compound.setHazardComments(rs.getString("USER_HAZARD_COMMENTS"));
		compound.setCompoundParent(rs.getString("COMPOUND_PARENT_ID"));
		compound.setStructureComments(rs.getString("STRUCT_COMMENTS"));
		compound.setLoadedFromDB(true);
		compound.setModelChanged(false);
		
		ProductBatchModel batchModel = new ProductBatchModel(rs.getString("BATCH_KEY"));
		batchModel.setLoadingFromDB(true);
		batchModel.setCompound(compound);
		
		try {
			BatchSelect.setSaltCode(batchModel, rs.getString("SALT_CODE"));
		} catch (CodeTableCacheException e) {
			log.error("Failed to get SaltCode:", e);
		}
		
		batchModel.setBatchNumber(BatchSelect.getBatchNumber(rs));
		batchModel.setMolecularFormula(rs.getString("MOLECULAR_FORMULA"));
		batchModel.setSaltEquivs(CommonUtils.toDouble(rs.getString("SALT_EQUIVS")));
		batchModel.setAutoCalcOn(CommonUtils.toBooleanFromChar(rs.getString("AUTO_CALC")));
		batchModel.setSynthesizedBy(rs.getString("SYNTHSZD_BY"));
		batchModel.setBatchType(BatchType.getBatchType(rs.getString("BATCH_TYPE")));
		batchModel.setSolventsAdded(rs.getString("ADDED_SOLV_BATCH_KEY"));
		batchModel.setChloracnegenType(CommonUtils.convertChloracnegenType(rs.getString("CHLORACNEGEN_TYPE"), false));
		batchModel.setChloracnegenFlag(rs.getString("IS_CHLORACNEGEN").equalsIgnoreCase("Y")?true:false);
		batchModel.setTestedForChloracnegen(rs.getString("TESTED_FOR_CHLORACNEGEN").equalsIgnoreCase("Y")?true:false);
		batchModel.setLoadedFromDB(true);
		
		ProductBatchModel productBatch = new ProductBatchModel(batchModel);

		productBatch.setSelectivityStatus(CommonUtils.toInteger(rs.getString("SELECTIVITY_STATUS")));
		productBatch.setContinueStatus(CommonUtils.toInteger(rs.getString("CONTINUE_STATUS")));
		
		BatchRegInfoModel regBatchInfo = new BatchRegInfoModel(rs.getString("REG_BATCH_KEY"),true);
		regBatchInfo.setBatchKey(rs.getString("BATCH_KEY"));
		regBatchInfo.setConversationalBatchNumber(rs.getString("R_CONVERSATIONAL_BATCH_NUMBER"));
		regBatchInfo.setParentBatchNumber(rs.getString("R_PARENT_BATCH_NUMBER"));
		regBatchInfo.setCompoundSource(rs.getString("SOURCE_CODE"));
		regBatchInfo.setCompoundSourceDetail(rs.getString("SOURCE_DETAIL_CODE"));
		regBatchInfo.setBatchTrackingId(CommonUtils.toInteger(rs.getString("BATCH_TRACKING_ID")));
		regBatchInfo.setHitId(rs.getString("HIT_ID"));
		regBatchInfo.setCompoundState(rs.getString("COMPOUND_STATE"));
		regBatchInfo.setProtectionCode(rs.getString("PROTECTION_CODE"));
		regBatchInfo.setStorageComments(rs.getString("BATCH_STORAGE_COMMENT"));
		regBatchInfo.setHazardComments(rs.getString("BATCH_HAZARD_COMMENT"));
		regBatchInfo.setHandlingComments(rs.getString("BATCH_HANDLING_COMMENT"));
		
		String flag = rs.getString("PRODUCT_FLAG");
		if( flag != null) {
			regBatchInfo.setProductFlag(flag.equals("Y")?true:false);
		} else {
			regBatchInfo.setProductFlag(false);
		}
		
		regBatchInfo.setOwner(rs.getString("BATCH_OWNER"));
		regBatchInfo.setComments(rs.getString("BATCH_COMMENTS"));
		regBatchInfo.setLoadedFromDB(true);
		regBatchInfo.setModelChanged(false);
		productBatch.setRegInfo(regBatchInfo);
		productBatch.setLoadedFromDB(true);
		productBatch.setLoadingFromDB(false);
		productBatch.setModelChanged(false);
		
		return productBatch;
	}
}
