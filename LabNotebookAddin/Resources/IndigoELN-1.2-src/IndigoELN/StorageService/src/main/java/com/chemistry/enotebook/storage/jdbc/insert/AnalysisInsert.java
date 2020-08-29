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
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class AnalysisInsert extends SqlUpdate {
	public AnalysisInsert(DataSource dsource) {
		super(dsource, getInsertAnalysisQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // ANALYSIS_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		this.declareParameter(new SqlParameter(Types.BINARY)); // BLOB_DATA
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CEN_SAMPLE_REF
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // ANALYTICAL_SERVICE_SAMPLE_REF
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
	}
	static private  String getInsertAnalysisQuery() {
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("INSERT INTO CEN_ANALYSIS(");
		sqlQuery.append("ANALYSIS_KEY, PAGE_KEY, XML_METADATA, BLOB_DATA, CEN_SAMPLE_REF, ANALYTICAL_SERVICE_SAMPLE_REF,");
		sqlQuery.append("ANNOTATION ,COMMENTS ,SITE_CODE ,CYBER_LAB_DOMAIN_ID ,CYBER_LAB_FILE_ID ,");
		sqlQuery.append("CYBER_LAB_FOLDER_ID ,CYBER_LAB_LCDF_ID ,CYBER_LAB_USER_ID ,DOMAIN ,SERVER ,URL ,");
		sqlQuery.append("USER_ID,ANALYTICAL_VERSION,INSTRUMENT,INSTRUMENT_TYPE,FILE_NAME ,");
		sqlQuery.append("FILE_SIZE,FILE_TYPE,EXPERIMENT_TIME,EXPERIMENT,GROUP_ID,IP_RELATED,IS_LINKED ");

		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,?,?");
		sqlQuery.append(")");
		//log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

}
