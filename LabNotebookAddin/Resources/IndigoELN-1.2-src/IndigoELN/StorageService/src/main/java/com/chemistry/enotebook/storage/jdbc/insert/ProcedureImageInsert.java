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

public class ProcedureImageInsert extends SqlUpdate {
	private static final Log log = LogFactory.getLog(ProcedureImageInsert.class);

	public ProcedureImageInsert(DataSource dsource) {
		super(dsource, getProcedureImageQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // IMAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // IMAGE_KEY
		this.declareParameter(new SqlParameter(Types.BINARY)); // IMAGE_DATA
	}

	static protected String getProcedureImageQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PROCEDURE_IMAGES(");
		sqlQuery.append("IMAGE_KEY, PAGE_KEY, IMAGE_TYPE, IMAGE_DATA");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}
}
