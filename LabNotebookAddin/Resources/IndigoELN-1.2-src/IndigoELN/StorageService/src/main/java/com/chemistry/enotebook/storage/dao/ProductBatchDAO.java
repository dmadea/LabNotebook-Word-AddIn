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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.ExternalSupplierModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.BatchAmountInsert;
import com.chemistry.enotebook.storage.jdbc.insert.BatchInsert;
import com.chemistry.enotebook.storage.jdbc.insert.RegisteredBatchInsert;
import com.chemistry.enotebook.storage.jdbc.insert.StructureInsert;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductBatchDAO extends BatchDAO {

	private static final Log log = LogFactory.getLog(ProductBatchDAO.class);
	
	public void insertProductBatchesThruStoredProcedure(String stepKey, String pageKey, List<ProductBatchModel> batches, String listKey)  {
		log.debug(" INSIDE insertProductBatchesThruStoredProcedure");
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookInsertDAO.insertProductBatches()  STORED PROCEDURE APPROACH");
		
		Connection dbCon = null;
		
		try {
			log.debug(" CALLING for Oracle Connection");
			
			dbCon = getConnection();

			String batchNumber = "";
			String batchType = "";
			String saltCode = "";
			
			StructureInsert si = new StructureInsert(getDataSource());
			BatchInsert bi = new BatchInsert(getDataSource());
			BatchAmountInsert bai = new BatchAmountInsert(getDataSource());
			RegisteredBatchInsert rbi = new RegisteredBatchInsert(getDataSource());
			
			for (ProductBatchModel batch : batches) {
				if (productBatchAlreadyExists(batch.getKey(), dbCon))
					continue;
				
				if (CommonUtils.isNotNull(batch.getBatchNumber()))
					batchNumber = batch.getBatchNumber().getBatchNumber();
				else
					batchNumber = "";

				if (CommonUtils.isNotNull(batch.getBatchType()))
					batchType = batch.getBatchType().toString();
				else
					batchType = "";

				if (CommonUtils.isNotNull(batch.getSaltForm()))
					saltCode = batch.getSaltForm().getCode();
				else
					saltCode = "";
				
				Double theoYld = null;
				if (!batch.getTheoreticalYieldPercentAmount().isValueDefault()) {
					theoYld = new Double(batch.getTheoreticalYieldPercentAmount().doubleValue());
				}

				Object[] batchObjValues = new Object[] {
						batch.getKey(), // BATCH_KEY
						pageKey, // PAGE_KEY
						batchNumber, // BATCH_NUMBER
						batch.getCompound().getKey(), // STRUCT_KEY
						null, // XML_METADATA
						stepKey, // STEP_KEY
						batchType, // BATCH_TYPE
						batch.getMolecularFormula(), // MOLECULAR_FORMULA
						theoYld,// THEORITICAL_YIELD_PERCENT
						saltCode, // SALT_CODE
						new Double(batch.getSaltEquivs()), // SALT_EQUIVS
						listKey, // LIST_KEY
						new Double(batch.getMolecularWeightAmount().doubleValue()), // VALUE
						batch.getMolecularWeightAmount().getUnit().getCode(), // UNIT_CODE
						new Character(batch.getMolecularWeightAmount().isCalculated()== true?'Y':'N'), // CALCULATED
						new Integer(batch.getMolecularWeightAmount().getSigDigits()), // SIG_DIGITS
						new Character(batch.getMolecularWeightAmount().getSigDigitsSet()== true?'Y':'N'), // SIG_DIGITS_SET
						new Integer(batch.getMolecularWeightAmount().getUserPrefFigs()), // USER_PREF_FIGS
						new String(batch.isLimiting()== true?"Y":"N") ,
						new String(batch.isAutoCalcOn()== true?"Y":"N"),
						batch.getSynthesizedBy(),
						batch.getSolventsAdded(),
						null,
						new Integer(batch.getTransactionOrder()),
					    CommonUtils.convertChloracnegenType(batch.getChloracnegenType(), true),
					    new String(batch.isChloracnegen()== true?"Y":"N") ,
					    new String(batch.isTestedForChloracnegen()== true?"Y":"N")
					};

				si.update(getOraCompound(batch.getCompound(), pageKey));
				bi.update(batchObjValues);
				bai.update(getOraProductBatchAmount(batch, pageKey));
				rbi.update(getOraRegisteredBatch(batch, pageKey));
			}
				
			si.flush();
			bi.flush();
			bai.flush();
			rbi.flush();
			
			String sql = "update cen_registered_batches set xml_metadata = ? where reg_batch_key = ?";
			
			for (ProductBatchModel b : batches) {
				updateBatchWithCompound(b, dbCon);
				
				PreparedStatement st = dbCon.prepareStatement(sql);
				
				st.setString(1, b.getRegInfo().toXML());
				st.setString(2, b.getRegInfo().getKey());
				
				st.executeUpdate();
				st.close();
			}
			
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(ExceptionUtils.getRootCause(insertError));
		} finally {
			stopwatch.stop();
			log.debug("Close ORACLE Connection in finally block: ");
			closeConnection(dbCon);
		}
	}
	
	private Object[] getOraRegisteredBatch(ProductBatchModel pBatch, String pageKey) {
		try {
			String  submissionStatus = "";
			String  status = "";
			long jobId = -1;
			long batchTrackingId = 0;
			
			if (CommonUtils.isNotNull(pBatch.getRegInfo())) {
				try {
					jobId = Long.parseLong(pBatch.getRegInfo().getJobId());
				} catch (Exception e) {
					log.info("RegInfo for batch " + pBatch.getBatchNumberAsString() + " doesn't contain correct JobId: ", e);
				}
				
				batchTrackingId = pBatch.getRegInfo().getBatchTrackingId();
				status = pBatch.getRegInfo().getStatus();
				submissionStatus = pBatch.getRegInfo().getSubmissionStatus();
			} else {
				jobId = -1;
				batchTrackingId = 0;
				status = "";
				submissionStatus = "";
			}
			
			Long btid = null;
			
			if (batchTrackingId == -1 || batchTrackingId == 0) {
				btid = null;
			} else {
				btid = new Long(batchTrackingId);
			}
			
            String vendorCode = null;
            String vedorRefID = null;
            
			ExternalSupplierModel vendorInfo = pBatch.getRegInfo().getVendorInfo();
			if (vendorInfo != null) {
				vendorCode = vendorInfo.getCode();
				vedorRefID = vendorInfo.getSupplierCatalogRef();
			}
			
			return new Object[] {
				    pBatch.getRegInfo().getKey(), 
					pageKey, // PAGE_KEY
					pBatch.getKey(), // BATCH_KEY
					pBatch.getRegInfo().getConversationalBatchNumber(), // CONVERSATIONAL_BATCH_NUMBER
					pBatch.getParentBatchNumber(), // PARENT_BATCH_NUMBER
					btid, // BATCH_TRACKING_ID
					new Long(jobId), // JOB_ID
					pBatch.getRegInfo().getRegistrationStatus(), // REGISTRATION_STATUS
					submissionStatus, // SUBMISSION_STATUS
					status, // STATUS
					null, // XML_METADATA
					new Double(pBatch.getRegInfo().getMeltPointRange().getLower()),
					new Double(pBatch.getRegInfo().getMeltPointRange().getUpper()),
					pBatch.getRegInfo().getMeltPointRange().getComment(),
					vendorCode,
					vedorRefID,
					pBatch.getRegInfo().getCompoundSource(),
					pBatch.getRegInfo().getCompoundSourceDetail(),
					pBatch.getComments(),
					pBatch.getRegInfo().getCompoundState(),
					pBatch.getRegInfo().getHazardComments(),
					pBatch.getRegInfo().getHandlingComments(),
					pBatch.getRegInfo().getStorageComments(),
					pBatch.getRegInfo().getProtectionCode(),
					new String(pBatch.getContinueStatus()+""), 
					new String(pBatch.getSelectivityStatus()+""),
					pBatch.getRegInfo().getHitId(),
					pBatch.getRegInfo().getRegistrationDate(),
					pBatch.getRegInfo().getBatchVnVInfo().getStatus(),
					pBatch.getOwner(),
					pBatch.isProductFlag()==true?"Y":"N"
				};
		} catch (Exception error) {
    		throw new JDBCRuntimeException("Failed to create 'REGISTERED_BATCH' object for pageKey: " + pageKey + " product batch: " + (pBatch != null ? pBatch.getBatchNumberAsString() : "Null product batch"), error);
    	}
	}

	private boolean productBatchAlreadyExists(String batchKey, Connection connection) {
		ResultSet rs = null;
		
		try {
			String sql = "SELECT count(*) FROM CEN_BATCHES WHERE BATCH_KEY = ?";  
			
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, batchKey);
			
			rs = ps.executeQuery();
			rs.next();
			
			return (rs.getInt(1) > 0);
		} catch (SQLException e) {
			return false;
		} finally {
			if(rs != null) { try { rs.close(); } catch (SQLException ignore) { } }
		}
	}
}
