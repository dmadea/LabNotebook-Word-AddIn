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
package com.chemistry.enotebook.storage.jdbc.update;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class RegisteredBatchUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(RegisteredBatchUpdate.class);

	public RegisteredBatchUpdate(DataSource dsource) {
		super(dsource, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // BATCH_TRACKING_ID
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // JOB_ID
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // COMPOUND_REGISTRATION_OFFSET
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PARENT_BATCH_NUMBER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CONVERSATIONAL_BATCH_NUMBER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // REGISTRATION_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SUBMISSION_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_MANAGEMENT_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_MGMT_STATUS_MESSAGE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_AGGREGATION_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CMPD_AGGREGATION_STATUS_MSG
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PURIFICATION_SERVICE_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PUR_SERVICE_STATUS_MSG
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_REG_STATUS_MESSAGE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //MELT_POINT_VAL_LOWER
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //MELT_POINT_VAL_UPPER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //MELT_POINT_COMMENTS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SUPPLIER_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SUPPLIER_REGISTRY_NUMBER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SOURCE_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SOURCE_DETAIL_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_COMMENTS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //COMPOUND_STATE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_HAZARD_COMMENT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_HANDLING_COMMENT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_STORAGE_COMMENT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //PROTECTION_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //CONTINUE_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SELECTIVITY_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //HIT_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //VNV_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_OWNER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //PRODUCT_FLAG
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //INTERMEDIATE_OR_TEST
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // REG_BATCH_KEY


	}

	static protected String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_REGISTERED_BATCHES SET ");
		sql.append("BATCH_TRACKING_ID = ? ");
		sql.append(",JOB_ID = ? ");
		sql.append(",COMPOUND_REGISTRATION_OFFSET = ? ");
		sql.append(",PARENT_BATCH_NUMBER = ? ");
		sql.append(",CONVERSATIONAL_BATCH_NUMBER = ? ");
		sql.append(",REGISTRATION_STATUS = ? ");
		sql.append(",SUBMISSION_STATUS = ? ");
		sql.append(",STATUS = ? ");
		sql.append(",COMPOUND_MANAGEMENT_STATUS = ? ");
		sql.append(",COMPOUND_MGMT_STATUS_MESSAGE = ? ");
		sql.append(",COMPOUND_AGGREGATION_STATUS = ? ");
		sql.append(",CMPD_AGGREGATION_STATUS_MSG = ? ");
		sql.append(",PURIFICATION_SERVICE_STATUS = ? ");
		sql.append(",PUR_SERVICE_STATUS_MSG = ? ");
		sql.append(",COMPOUND_REG_STATUS_MESSAGE = ? ");
		sql.append(",XML_METADATA = ? ");
		sql.append(",MELT_POINT_VAL_LOWER = ? ");
		sql.append(",MELT_POINT_VAL_UPPER = ? ");
		sql.append(",MELT_POINT_COMMENTS = ? ");
		sql.append(",SUPPLIER_CODE = ? ");
		sql.append(",SUPPLIER_REGISTRY_NUMBER = ? ");
		sql.append(",SOURCE_CODE = ? ");
		sql.append(",SOURCE_DETAIL_CODE = ? ");
		sql.append(",BATCH_COMMENTS = ? ");
		sql.append(",COMPOUND_STATE = ? ");
		sql.append(",BATCH_HAZARD_COMMENT = ? ");
		sql.append(",BATCH_HANDLING_COMMENT = ? ");
		sql.append(",BATCH_STORAGE_COMMENT = ? ");
		sql.append(",PROTECTION_CODE = ? ");
		sql.append(",CONTINUE_STATUS = ? ");
		sql.append(",SELECTIVITY_STATUS = ? ");
		sql.append(",HIT_ID = ? ");
		sql.append(",VNV_STATUS = ? ");
		sql.append(",BATCH_OWNER = ? ");
		sql.append(",PRODUCT_FLAG = ? ");
		sql.append(",INTERMEDIATE_OR_TEST = ? ");
		sql.append(" WHERE REG_BATCH_KEY = ? ");

		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}
		return sql.toString();
	}

}
