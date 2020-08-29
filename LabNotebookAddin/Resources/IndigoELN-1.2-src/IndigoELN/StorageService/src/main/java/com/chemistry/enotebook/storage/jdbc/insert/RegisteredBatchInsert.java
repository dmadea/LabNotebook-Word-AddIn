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
package com.chemistry.enotebook.storage.jdbc.insert;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class RegisteredBatchInsert extends BatchSqlUpdate {

	public RegisteredBatchInsert(DataSource ds) {
		super(ds, getUpdateSql());
		
		declareParameter(new SqlParameter(Types.VARCHAR)); // REG_BATCH_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // CONVERSATIONAL_BATCH_NUMBER
		declareParameter(new SqlParameter(Types.VARCHAR)); // PARENT_BATCH_NUMBER
		declareParameter(new SqlParameter(Types.NUMERIC)); // BATCH_TRACKING_ID
		declareParameter(new SqlParameter(Types.NUMERIC)); // JOB_ID
		declareParameter(new SqlParameter(Types.VARCHAR)); // REGISTRATION_STATUS
		declareParameter(new SqlParameter(Types.VARCHAR)); // SUBMISSION_STATUS
		declareParameter(new SqlParameter(Types.VARCHAR)); // STATUS
		declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		declareParameter(new SqlParameter(Types.NUMERIC)); // MELT_POINT_VAL_LOWER
		declareParameter(new SqlParameter(Types.NUMERIC)); // MELT_POINT_VAL_UPPER
		declareParameter(new SqlParameter(Types.VARCHAR)); // MELT_POINT_COMMENTS
		declareParameter(new SqlParameter(Types.VARCHAR)); // SUPPLIER_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // SUPPLIER_REGISTRY_NUMBER
		declareParameter(new SqlParameter(Types.VARCHAR)); // SOURCE_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // SOURCE_DETAIL_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_COMMENTS
		declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_STATE
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_HAZARD_COMMENT
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_HANDLING_COMMENT
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_STORAGE_COMMENT
		declareParameter(new SqlParameter(Types.VARCHAR)); // PROTECTION_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // CONTINUE_STATUS
		declareParameter(new SqlParameter(Types.VARCHAR)); // SELECTIVITY_STATUS
		declareParameter(new SqlParameter(Types.VARCHAR)); // HIT_ID
		declareParameter(new SqlParameter(Types.TIMESTAMP)); // REGISTRATION_DATE
		declareParameter(new SqlParameter(Types.VARCHAR)); // VNV_STATUS
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_OWNER
		declareParameter(new SqlParameter(Types.VARCHAR)); // PRODUCT_FLAG
	}
	
	private static String getUpdateSql() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO CEN_REGISTERED_BATCHES (");
		
		sb.append("REG_BATCH_KEY,");
		sb.append("PAGE_KEY,");
		sb.append("BATCH_KEY,");
		sb.append("CONVERSATIONAL_BATCH_NUMBER,");
		sb.append("PARENT_BATCH_NUMBER,");
		sb.append("BATCH_TRACKING_ID,");
		sb.append("JOB_ID,");
		sb.append("REGISTRATION_STATUS,");
		sb.append("SUBMISSION_STATUS,");
		sb.append("STATUS,");
		sb.append("XML_METADATA,");
		sb.append("MELT_POINT_VAL_LOWER,");
		sb.append("MELT_POINT_VAL_UPPER,");
		sb.append("MELT_POINT_COMMENTS,");
		sb.append("SUPPLIER_CODE,");
		sb.append("SUPPLIER_REGISTRY_NUMBER,");
		sb.append("SOURCE_CODE,");
		sb.append("SOURCE_DETAIL_CODE,");
		sb.append("BATCH_COMMENTS,");
		sb.append("COMPOUND_STATE,");
		sb.append("BATCH_HAZARD_COMMENT,");
		sb.append("BATCH_HANDLING_COMMENT,");
		sb.append("BATCH_STORAGE_COMMENT,");
		sb.append("PROTECTION_CODE,");
		sb.append("CONTINUE_STATUS,");
		sb.append("SELECTIVITY_STATUS,");
		sb.append("HIT_ID,");
		sb.append("REGISTRATION_DATE,");
		sb.append("VNV_STATUS,");
		sb.append("BATCH_OWNER,");
		sb.append("PRODUCT_FLAG");
				
		sb.append(") VALUES (");
		
		sb.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
		
		sb.append(")");
		
		return sb.toString();
	}
}
