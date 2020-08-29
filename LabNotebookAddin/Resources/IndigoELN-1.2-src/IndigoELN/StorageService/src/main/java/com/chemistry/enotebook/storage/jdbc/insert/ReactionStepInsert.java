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

/**
 * 
 *         table. Each parameter defined will be used by the NotebookInsertDAO to pass the value at the runtime.
 */
public class ReactionStepInsert extends SqlUpdate {
	private static final Log log = LogFactory.getLog(ReactionStepInsert.class);

	public ReactionStepInsert(DataSource ds) {
		super(ds, getInsertReactionStepQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // STEP_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // SEQ_NUM
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // RXN_SCHEME_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA

	}

	static protected String getInsertReactionStepQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_REACTION_STEPS(");
		sqlQuery.append("STEP_KEY, ");
		sqlQuery.append("PAGE_KEY, ");
		sqlQuery.append("SEQ_NUM, ");
		sqlQuery.append("RXN_SCHEME_KEY, ");
		sqlQuery.append("XML_METADATA");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?)");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

}
