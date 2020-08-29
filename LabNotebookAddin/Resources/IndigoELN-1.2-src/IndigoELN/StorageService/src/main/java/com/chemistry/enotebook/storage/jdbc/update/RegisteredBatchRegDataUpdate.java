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

public class RegisteredBatchRegDataUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(RegisteredBatchUpdate.class);

	public RegisteredBatchRegDataUpdate(DataSource dsource) {
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
		this.declareParameter(new SqlParameter(Types.TIMESTAMP)); //REGISTRATION_DATE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //VNV_STATUS
		
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // REG_BATCH_KEY


	}

	static protected String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_REGISTERED_BATCHES SET ");
		sql.append("BATCH_TRACKING_ID = ?, ");
		sql.append("JOB_ID = ?, ");
		sql.append("COMPOUND_REGISTRATION_OFFSET = ?, ");
		sql.append("PARENT_BATCH_NUMBER = ?, ");
		sql.append("CONVERSATIONAL_BATCH_NUMBER = ?, ");
		sql.append("REGISTRATION_STATUS = ?, ");
		sql.append("SUBMISSION_STATUS = ?, ");
		sql.append("STATUS = ?, ");
		sql.append("COMPOUND_MANAGEMENT_STATUS = ?, ");
		sql.append("COMPOUND_MGMT_STATUS_MESSAGE = ?, ");
		sql.append("COMPOUND_AGGREGATION_STATUS = ?, ");
		sql.append("CMPD_AGGREGATION_STATUS_MSG = ?, ");
		sql.append("PURIFICATION_SERVICE_STATUS = ?, ");
		sql.append("PUR_SERVICE_STATUS_MSG = ?, ");
		sql.append("COMPOUND_REG_STATUS_MESSAGE = ?, ");
		sql.append("XML_METADATA = ?, ");
		sql.append("REGISTRATION_DATE = ? ,");
		sql.append("VNV_STATUS = ? ");
		sql.append("WHERE REG_BATCH_KEY = ? ");

		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}
		return sql.toString();
	}

}
