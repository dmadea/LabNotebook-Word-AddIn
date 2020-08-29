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

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.SaltFormModel;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchSelect extends StructureSelect {
	private static final Log log = LogFactory.getLog(BatchSelect.class);
	
	public BatchSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		try{			
			String xmlMetadata = rs.getString("BATCH_XML_METADATA");
			
			String stoich_comments = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Stoich_Comments");
			String container_barcode = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Container_Barcode");
			String stoic_label = CeNXMLParser.getXmlProperty(xmlMetadata, "/Batch_Properties/Meta_Data/Stoic_Label");
			
			BatchModel batchModel = new BatchModel(rs.getString("BATCH_KEY"));
			batchModel.setLoadingFromDB(true);
			ParentCompoundModel parentCompound = (ParentCompoundModel) super.mapRow(rs, rowNum);
			batchModel.setCompound(parentCompound);

			BatchSelect.setSaltCode(batchModel, rs.getString("SALT_CODE"));
		
			batchModel.setBatchNumber(BatchSelect.getBatchNumber(rs));

			// Molecular weight
			double molWeightValue = rs.getDouble("BATCH_MW_VALUE");
			String molWeightUnitCode = rs.getString("BATCH_MW_UNIT_CODE");
			boolean molWeightCalculated = StringUtils.equals(rs.getString("BATCH_MW_IS_CALC"), "Y") ? true : false;
			int molWeightSigDigits = rs.getInt("BATCH_MW_SIG_DIGITS");
			boolean molWeightSigDigitsSet = StringUtils.equals(rs.getString("BATCH_MW_SIG_DIGITS_SET"), "Y") ? true : false;
			int molWeightUserPrefFigs = Integer.parseInt(rs.getString("BATCH_MW_USER_PREF_FIGS"));
		
			AmountModel molWeightAmount = new AmountModel(UnitType.MASS, molWeightValue);
			molWeightAmount.setUnit(new Unit2(molWeightUnitCode));
			molWeightAmount.setCalculated(molWeightCalculated);
			molWeightAmount.setSigDigits(molWeightSigDigits);
			molWeightAmount.setSigDigitsSet(molWeightSigDigitsSet);
			molWeightAmount.setUserPrefFigs(molWeightUserPrefFigs);
		
			batchModel.setMolecularWeightAmount(molWeightAmount);
		
			//USER2: MF and MW for Batch will be calc from PArentCompound MF and MW etc
			
			batchModel.setMolecularFormula(rs.getString("MOLECULAR_FORMULA"));
			batchModel.setSaltEquivs(CommonUtils.toDouble(rs.getString("SALT_EQUIVS")));
			batchModel.setLimiting(CommonUtils.toBooleanFromChar(rs.getString("IS_LIMITING")));
			batchModel.setAutoCalcOn(CommonUtils.toBooleanFromChar(rs.getString("AUTO_CALC")));
			batchModel.setSynthesizedBy(rs.getString("SYNTHSZD_BY"));
			batchModel.setTransactionOrder(rs.getInt("INTD_ADDITION_ORDER"));
			batchModel.setBatchType(BatchType.getBatchType(rs.getString("BATCH_TYPE")));
			batchModel.setListKey(rs.getString("LIST_KEY"));
			batchModel.setPosition(rs.getString("POSITION"));
			batchModel.setSolventsAdded(rs.getString("ADDED_SOLV_BATCH_KEY"));
			batchModel.setChloracnegenType(CommonUtils.convertChloracnegenType(rs.getString("CHLORACNEGEN_TYPE"), false));		
			batchModel.setChloracnegenFlag(rs.getString("IS_CHLORACNEGEN").equalsIgnoreCase("Y")?true:false);
			batchModel.setTestedForChloracnegen(rs.getString("TESTED_FOR_CHLORACNEGEN").equalsIgnoreCase("Y")?true:false);
			batchModel.setBarCode(container_barcode);
			batchModel.setStoichComments(stoich_comments);
			batchModel.setStoicLabel(stoic_label);
		
			batchModel.setLoadedFromDB(true);
			
			return batchModel;
		} catch (Exception e){
			log.error("Failed loading batch:", e);
			throw new SQLException("Failed loading Batch: " + e.toString()); // JRE 1.5 doesn't allow for embedding cause.
		}
	}

	/**
	 * @return BATCH_NUMBER from given ResultSet 
	 */
	protected static BatchNumber getBatchNumber(ResultSet rs) throws SQLException {
		BatchNumber batchNo = new BatchNumber();
		
		try {
			batchNo.setBatchNumber(rs.getString("BATCH_NUMBER"));
		} catch (InvalidBatchNumberException e) {
			log.error("Exception with preparing BatchNumber from DB value:", e);
		}

		return batchNo;
	}

	protected static void setSaltCode(BatchModel batch, String saltCode) throws CodeTableCacheException {
		if (batch != null) {
			String[] values = StringUtils.split(saltCode, " ");
			if (!ArrayUtils.isEmpty(values) && !StringUtils.equals(values[0], batch.getSaltForm().getCode())) {
				SaltCodeCache scc = SaltCodeCache.getCache();
				batch.setSaltForm(new SaltFormModel(values[0],
				                                    scc.getDescriptionGivenCode(values[0]),
				                                    scc.getMolFormulaGivenCode(values[0]),
				                                    scc.getMolWtGivenCode(values[0])));
				if (SaltFormModel.isParentCode(values[0])) {
					batch.setSaltEquivs(0.0);
				}
			}
		}
	}
}
