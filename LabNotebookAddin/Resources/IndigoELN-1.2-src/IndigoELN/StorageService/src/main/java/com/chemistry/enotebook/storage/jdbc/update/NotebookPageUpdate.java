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

public class NotebookPageUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(NotebookPageUpdate.class);

	public NotebookPageUpdate(DataSource dsource) {
		super(dsource, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_STATUS
		this.declareParameter(new SqlParameter(Types.TIMESTAMP)); // MODIFIED_DATE
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // VERSION
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA(XML_TYPE)
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // LATEST_VERSION
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // TA_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROJECT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // LITERATURE_REF
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SUBJECT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SERIES_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROTOCOL_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SPID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_OWNER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_CREATOR
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // DESIGN_SUBMITTER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROCEDURE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
	}

	static private String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_PAGES SET ");
		sql.append("PAGE_STATUS = ?, ");
		sql.append("MODIFIED_DATE = ?, ");
		sql.append("PAGE_VERSION = ?, ");
		sql.append("XML_METADATA = ?, ");
		sql.append("LATEST_VERSION = ?, ");
		sql.append("TA_CODE = ?, ");
		sql.append("PROJECT_CODE = ?, ");
		sql.append("LITERATURE_REF = ?, ");
		sql.append("SUBJECT = ?, ");
		sql.append("SERIES_ID = ?, ");
		sql.append("PROTOCOL_ID = ?, ");
		sql.append("SPID = ?, ");
		sql.append("BATCH_OWNER = ?, ");
		sql.append("BATCH_CREATOR = ?, ");
		sql.append("DESIGN_SUBMITTER = ?, ");
		sql.append("PROCEDURE = ? ");
		sql.append(" WHERE PAGE_KEY = ?");
		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}

		return sql.toString();
	}




}
