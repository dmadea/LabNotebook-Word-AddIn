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

public class PurificationServiceParameterInsert extends SqlUpdate{
	private static final Log log = LogFactory.getLog(PurificationServiceParameterInsert.class);
	
	public PurificationServiceParameterInsert(DataSource dsource) {
		super(dsource, getQuery());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PURIFICATION_SERVICE_KEY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // MODIFIERS
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // PH_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // ARCHIVE_PLATE
		this.declareParameter(new SqlParameter(Types.NUMERIC));  //ARCHIVE_VOLUME                                         
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SAMPLE_WORKUP
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // INORGANIC_BYPRODUCT_SALT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SEPERATE_ISOMERS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // DESTINATION_LAB
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // WELL_KEY
	}
	
	private static String getQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PURIFICATION_SERVICE(");
		sqlQuery.append("PURIFICATION_SERVICE_KEY, MODIFIERS, PH_VALUE, ARCHIVE_PLATE, ARCHIVE_VOLUME,  "
				+ " SAMPLE_WORKUP,INORGANIC_BYPRODUCT_SALT,SEPERATE_ISOMERS,DESTINATION_LAB,WELL_KEY");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}
}
