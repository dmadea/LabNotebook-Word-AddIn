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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class JobInsert extends SqlUpdate {
	public static final Log log = LogFactory.getLog(JobInsert.class);

	public JobInsert(DataSource ds) {
		super(ds, getInsertJobQuery());
		log.debug("JobInsert(DataSource) start");
		this.declareParameter(new SqlParameter(Types.VARCHAR));//PLATE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_REGISTRATION_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_REG_STATUS_MESSAGE
		//this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_REG_SUBMISSION_DATE
		//this.declareParameter(new SqlParameter(Types.VARCHAR));//MODIFIED_DATE
		this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_MANAGEMENT_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_MGMT_STATUS_MESSAGE
		this.declareParameter(new SqlParameter(Types.VARCHAR));//PURIFICATION_SERVICE_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR));//PUR_SERVICE_STATUS_MSG
		this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_AGGREGATION_STATUS
		this.declareParameter(new SqlParameter(Types.VARCHAR));//CMPD_AGGREGATION_STATUS_MSG
		this.declareParameter(new SqlParameter(Types.VARCHAR));//CALLBACK_URL
		this.declareParameter(new SqlParameter(Types.VARCHAR));//PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR));//COMPOUND_REGISTRATION_JOB_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR));//BATCH_KEYS
		this.declareParameter(new SqlParameter(Types.VARCHAR));//PLATE_KEYS
		this.declareParameter(new SqlParameter(Types.VARCHAR));//WORKFLOW
		this.declareParameter(new SqlParameter(Types.VARCHAR));//STATUS
		log.debug("JobInsert(DataSource) end");
	}

	public static String getInsertJobQuery() {
		log.debug("getInsertJobQuery() start");
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_REG_JOBS(");
		sqlQuery.append("PLATE_KEY, ");
		sqlQuery.append("COMPOUND_REGISTRATION_STATUS, ");
		sqlQuery.append("COMPOUND_REG_STATUS_MESSAGE, ");
		sqlQuery.append("COMPOUND_REG_SUBMISSION_DATE, ");
		sqlQuery.append("MODIFIED_DATE, ");
		sqlQuery.append("COMPOUND_MANAGEMENT_STATUS, ");
		sqlQuery.append("COMPOUND_MGMT_STATUS_MESSAGE, ");
		sqlQuery.append("PURIFICATION_SERVICE_STATUS, ");
		sqlQuery.append("PUR_SERVICE_STATUS_MSG, ");
		sqlQuery.append("COMPOUND_AGGREGATION_STATUS, ");
		sqlQuery.append("CMPD_AGGREGATION_STATUS_MSG, ");
		sqlQuery.append("CALLBACK_URL, ");
		sqlQuery.append("PAGE_KEY, ");
		sqlQuery.append("COMPOUND_REGISTRATION_JOB_ID, ");
		sqlQuery.append("BATCH_KEYS, ");
		sqlQuery.append("PLATE_KEYS, ");
		sqlQuery.append("WORKFLOW, ");
		sqlQuery.append("STATUS) ");
		sqlQuery.append("VALUES (?,?,?, CURRENT_DATE, CURRENT_DATE,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		if (log.isDebugEnabled()) {
			log.debug(sqlQuery.toString());
		}
		
		log.debug("getInsertJobQuery() end");
		return sqlQuery.toString();
	}
}
