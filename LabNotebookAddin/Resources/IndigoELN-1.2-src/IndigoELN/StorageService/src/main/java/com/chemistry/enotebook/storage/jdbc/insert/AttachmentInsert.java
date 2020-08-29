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

public class AttachmentInsert extends SqlUpdate {
	private static final Log log = LogFactory.getLog(AttachmentInsert.class);

	public AttachmentInsert(DataSource dsource) {
		super(dsource, getInsertAttachmentQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // ATTACHEMENT_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		this.declareParameter(new SqlParameter(Types.BINARY)); // BLOB_DATA
		
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // 
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // 
		
		
	}

	static protected String getInsertAttachmentQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_ATTACHEMENTS(");
		sqlQuery.append("ATTACHEMENT_KEY, PAGE_KEY, XML_METADATA, BLOB_DATA,");
		sqlQuery.append("DATE_MODIFIED ,DOCUMENT_DESCRIPTION ,DOCUMENT_NAME ,IP_RELATED ,");
		sqlQuery.append("ORIGINAL_FILE_NAME ,DOCUMENT_SIZE ,DOCUMENT_TYPE"); 
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

}
