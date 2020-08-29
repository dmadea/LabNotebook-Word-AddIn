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

public class AnalysisUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(AnalysisUpdate.class);

	public AnalysisUpdate(DataSource ds) {
		super(ds, getUpdateSql());

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
		
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // ANALYSIS_KEY
		
	}

	static protected String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_ANALYSIS SET ");
		sql.append(" XML_METADATA = ?, ");
		sql.append("BLOB_DATA = ?, ");
		sql.append("CEN_SAMPLE_REF = ?, ");
		sql.append("ANALYTICAL_SERVICE_SAMPLE_REF = ?, ");
		sql.append("ANNOTATION = ? ,");
		sql.append("COMMENTS = ? ,");
		sql.append("SITE_CODE = ?, ");
		sql.append("CYBER_LAB_DOMAIN_ID = ?, ");
		sql.append("CYBER_LAB_FILE_ID = ?, ");
		sql.append("CYBER_LAB_FOLDER_ID = ?, ");
		sql.append("CYBER_LAB_LCDF_ID = ?, ");
		sql.append("CYBER_LAB_USER_ID = ?, ");
		sql.append("DOMAIN = ?, ");
		sql.append("SERVER = ?, ");
		sql.append("URL = ?, ");
		sql.append("USER_ID = ?, ");
		sql.append("ANALYTICAL_VERSION = ? ,");
		sql.append("INSTRUMENT = ?, ");
		sql.append("INSTRUMENT_TYPE = ?, ");
		sql.append("FILE_NAME = ?, ");
		sql.append("FILE_SIZE = ?, ");
		sql.append("FILE_TYPE = ?, ");
		sql.append("EXPERIMENT_TIME = ?, ");
		sql.append("EXPERIMENT = ?, ");
		sql.append("GROUP_ID = ?, ");
		sql.append("IP_RELATED = ?, ");
		sql.append("IS_LINKED = ? ");
		sql.append(" WHERE ANALYSIS_KEY = ? ");
		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}
		return sql.toString();
	}

}
