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

public class ReactionSchemeInsert extends SqlUpdate {
	private static final Log log = LogFactory.getLog(ReactionSchemeInsert.class);

	public ReactionSchemeInsert(DataSource ds) {
		super(ds, getInsertReactionSchemesQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // RXN_SCHEME_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // REACTION_TYPE
		this.declareParameter(new SqlParameter(Types.BINARY)); // RXN_SKETCH
		this.declareParameter(new SqlParameter(Types.BINARY)); // NATIVE_RXN_SKETCH
		this.declareParameter(new SqlParameter(Types.BINARY)); // SKETCH_IMAGE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VRXN_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROTOCOL_ID
	}

	static protected String getInsertReactionSchemesQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_REACTION_SCHEMES(");
		sqlQuery.append("RXN_SCHEME_KEY, ");
		sqlQuery.append("PAGE_KEY, ");
		sqlQuery.append("REACTION_TYPE, ");
		sqlQuery.append("RXN_SKETCH, ");
		sqlQuery.append("NATIVE_RXN_SKETCH, ");
		sqlQuery.append("SKETCH_IMAGE, ");
		sqlQuery.append("XML_METADATA, ");
		sqlQuery.append("VRXN_ID, ");
		sqlQuery.append("PROTOCOL_ID");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?,?,?");

		sqlQuery.append(")");
		log.debug(sqlQuery);
		return sqlQuery.toString();
	}
}
