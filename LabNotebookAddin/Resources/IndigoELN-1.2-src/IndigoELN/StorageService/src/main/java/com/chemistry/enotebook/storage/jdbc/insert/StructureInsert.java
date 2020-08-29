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

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class StructureInsert extends BatchSqlUpdate {

	public StructureInsert(DataSource ds) {
		super(ds, getUpdateSql());
		
		declareParameter(new SqlParameter(Types.VARCHAR)); // STRUCT_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		declareParameter(new SqlParameter(Types.BINARY));  // STRUCT_SKETCH
		declareParameter(new SqlParameter(Types.BINARY));  // NATIVE_STRUCT_SKETCH
		declareParameter(new SqlParameter(Types.BINARY));  // STRUCT_IMAGE
		declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		declareParameter(new SqlParameter(Types.VARCHAR)); // CHEMICAL_NAME
		declareParameter(new SqlParameter(Types.VARCHAR)); // MOLECULAR_FORMULA
		declareParameter(new SqlParameter(Types.NUMERIC)); // MOLECULAR_WEIGHT
		declareParameter(new SqlParameter(Types.VARCHAR)); // VIRTUAL_COMPOUND_ID
		declareParameter(new SqlParameter(Types.VARCHAR)); // REGISTRATION_NUMBER
		declareParameter(new SqlParameter(Types.VARCHAR)); // CAS_NUMBER
		declareParameter(new SqlParameter(Types.VARCHAR)); // STRUCT_SKTH_FRMT
		declareParameter(new SqlParameter(Types.VARCHAR)); // NATIVE_STRUCT_SKTH_FRMT
		declareParameter(new SqlParameter(Types.VARCHAR)); // STRUCT_IMAGE_FRMT
		declareParameter(new SqlParameter(Types.VARCHAR)); // USER_HAZARD_COMMENTS
		declareParameter(new SqlParameter(Types.VARCHAR)); // STRUCT_COMMENTS
		declareParameter(new SqlParameter(Types.VARCHAR)); // STEREOISOMER_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_NAME
		declareParameter(new SqlParameter(Types.NUMERIC)); // BOILING_PT_VALUE
		declareParameter(new SqlParameter(Types.VARCHAR)); // BOILING_PT_UNIT_CODE
		declareParameter(new SqlParameter(Types.NUMERIC)); // MELTING_PT_VALUE
		declareParameter(new SqlParameter(Types.VARCHAR)); // MELTING_PT_UNIT_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // CREATED_BY_NOTEBOOK
		declareParameter(new SqlParameter(Types.NUMERIC)); // EXACT_MASS
		declareParameter(new SqlParameter(Types.VARCHAR)); // COMPOUND_PARENT_ID
	}
	
	private static String getUpdateSql() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO CEN_STRUCTURES (");
		
		sb.append("STRUCT_KEY,");
		sb.append("PAGE_KEY,");
		sb.append("STRUCT_SKETCH,");
		sb.append("NATIVE_STRUCT_SKETCH,");
		sb.append("STRUCT_IMAGE,");
		sb.append("XML_METADATA,");
		sb.append("CHEMICAL_NAME,");
		sb.append("MOLECULAR_FORMULA,");
		sb.append("MOLECULAR_WEIGHT,");
		sb.append("VIRTUAL_COMPOUND_ID,");
		sb.append("REGISTRATION_NUMBER,");
		sb.append("CAS_NUMBER,");
		sb.append("STRUCT_SKTH_FRMT,");
		sb.append("NATIVE_STRUCT_SKTH_FRMT,");
		sb.append("STRUCT_IMAGE_FRMT,");
		sb.append("USER_HAZARD_COMMENTS,");
		sb.append("STRUCT_COMMENTS,");
		sb.append("STEREOISOMER_CODE,");
		sb.append("COMPOUND_NAME,");
		sb.append("BOILING_PT_VALUE,");
		sb.append("BOILING_PT_UNIT_CODE,");
		sb.append("MELTING_PT_VALUE,");
		sb.append("MELTING_PT_UNIT_CODE,");
		sb.append("CREATED_BY_NOTEBOOK,");
		sb.append("EXACT_MASS,");
		sb.append("COMPOUND_PARENT_ID");
		
		sb.append(") VALUES (");
		
		sb.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
		
		sb.append(")");
		
		return sb.toString();
	}
}
