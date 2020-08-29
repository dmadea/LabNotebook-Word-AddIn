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

public class CROPageInsert extends SqlUpdate {
	private static final Log log = LogFactory.getLog(CROPageInsert.class);

	public CROPageInsert(DataSource dsource) {
		super(dsource, getInsertCROPageInfoQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VENDOR_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VENDOR_DISPLAY_NAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VENDOR_CHEMIST_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VENDOR_CHEMIST_DISPLAY_NAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VENDOR_APPLICATION_SOURCE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // REQUEST_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
	}

	static protected String getInsertCROPageInfoQuery() {

		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_CRO_PAGEINFO(");
		sqlQuery.append("PAGE_KEY, ");
		sqlQuery.append("VENDOR_ID,");
		sqlQuery.append("VENDOR_DISPLAY_NAME, ");
		sqlQuery.append("VENDOR_CHEMIST_ID,");
		sqlQuery.append("VENDOR_CHEMIST_DISPLAY_NAME, ");
		sqlQuery.append("VENDOR_APPLICATION_SOURCE,");
		sqlQuery.append("REQUEST_ID, ");
		sqlQuery.append("XML_METADATA)");
		sqlQuery.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();

	}
}
