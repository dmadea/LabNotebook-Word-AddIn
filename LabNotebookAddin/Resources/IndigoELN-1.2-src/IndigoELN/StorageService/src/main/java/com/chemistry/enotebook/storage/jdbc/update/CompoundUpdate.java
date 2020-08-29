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

public class CompoundUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(CompoundUpdate.class);

	public CompoundUpdate(DataSource ds) {
		super(ds, getUpdateSql());

		this.declareParameter(new SqlParameter(Types.BINARY)); // STRUCT_SKETCH
		this.declareParameter(new SqlParameter(Types.BINARY)); // NATIVE_STRUCT_SKETCH
		this.declareParameter(new SqlParameter(Types.BINARY)); // STRUCT_IMAGE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // CHEMICAL_NAME
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // MOLECULAR_FORMULA
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // MOLECULAR_WEIGHT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VIRTUAL_COMPOUND_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // REGISTRATION_NUMBER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //STRUCT_SKTH_FRMT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //NATIVE_STRUCT_SKTH_FRMT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //STRUCT_IMAGE_FRMT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //USER_HAZARD_COMMENTS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //STRUCT_COMMENTS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //STEREOISOMER_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //COMPOUND_NAME
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //BOILING_PT_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BOILING_PT_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //MELTING_PT_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //MELTING_PT_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //CREATED_BY_NOTEBOOK
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //EXACT_MASS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //COMPOUND_PARENT_ID
		
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // STRUCT_KEY
	}

	static protected String getUpdateSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE CEN_STRUCTURES SET ");
		sql.append(" STRUCT_SKETCH = ?, ");
		sql.append("NATIVE_STRUCT_SKETCH = ?, ");
		sql.append("STRUCT_IMAGE = ?, ");
		sql.append("XML_METADATA = ?, ");
		sql.append("CHEMICAL_NAME = ?, ");
		sql.append("MOLECULAR_FORMULA = ?, ");
		sql.append("MOLECULAR_WEIGHT = ?, ");
		sql.append("VIRTUAL_COMPOUND_ID = ?, ");
		sql.append("REGISTRATION_NUMBER = ?, ");
		sql.append("STRUCT_SKTH_FRMT = ?, ");
		sql.append("NATIVE_STRUCT_SKTH_FRMT = ?, ");
		sql.append("STRUCT_IMAGE_FRMT = ?, ");
		sql.append("USER_HAZARD_COMMENTS = ?, ");
		sql.append("STRUCT_COMMENTS = ?, ");
		sql.append("STEREOISOMER_CODE = ?, ");
		sql.append("COMPOUND_NAME = ?, ");
		sql.append("BOILING_PT_VALUE = ?, ");
		sql.append("BOILING_PT_UNIT_CODE = ?, ");
		sql.append("MELTING_PT_VALUE = ?, ");
		sql.append("MELTING_PT_UNIT_CODE = ?, ");
		sql.append("CREATED_BY_NOTEBOOK = ?, ");
		sql.append("EXACT_MASS = ?, ");
		sql.append("COMPOUND_PARENT_ID = ? ");
		sql.append("WHERE STRUCT_KEY = ?");
		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}
		return sql.toString();
	}


}