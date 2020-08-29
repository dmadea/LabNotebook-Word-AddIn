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

public class NotebookPageInsert extends SqlUpdate {
	private static final Log log = LogFactory.getLog(NotebookPageInsert.class);

	public NotebookPageInsert(DataSource dsource) {
		super(dsource, getInsertNotebookPageQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SITE_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // NOTEBOOK
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // EXPERIMENT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // USERNAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // OWNER_USERNAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // LOOK_N_FEEL
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_STATUS
		this.declareParameter(new SqlParameter(Types.TIMESTAMP)); // CREATION_DATE
		this.declareParameter(new SqlParameter(Types.TIMESTAMP)); // MODIFIED_DATE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA(XML_TYPE)
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // VERSION
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // LATEST_VERSION
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SPID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // TA_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROJECT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // LITERATURE_REF
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SUBJECT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SERIES_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROTOCOL_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_OWNER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_CREATOR
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // DESIGN_SUBMITTER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROCEDURE
	}

	static private String getInsertNotebookPageQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PAGES(");
		sqlQuery.append("PAGE_KEY, SITE_CODE, NOTEBOOK, EXPERIMENT, USERNAME,");
		sqlQuery.append("OWNER_USERNAME, LOOK_N_FEEL, PAGE_STATUS, CREATION_DATE,");
		sqlQuery.append("MODIFIED_DATE,XML_METADATA, PAGE_VERSION,");
		sqlQuery.append("LATEST_VERSION, SPID, TA_CODE, PROJECT_CODE,");
		sqlQuery.append("LITERATURE_REF, SUBJECT, SERIES_ID, PROTOCOL_ID," );
		sqlQuery.append("BATCH_OWNER,BATCH_CREATOR,DESIGN_SUBMITTER,PROCEDURE");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?," + "?,?,?,?," +"?,?,?," + "?,?,?,?," + "?,?,?,?," + "?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

}
