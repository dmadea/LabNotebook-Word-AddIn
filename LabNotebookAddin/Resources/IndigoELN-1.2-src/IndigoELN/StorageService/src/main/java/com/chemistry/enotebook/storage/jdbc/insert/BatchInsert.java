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

public class BatchInsert extends BatchSqlUpdate {

	public BatchInsert(DataSource ds) {
		super(ds, getUpdateSql());
		
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_NUMBER
		declareParameter(new SqlParameter(Types.VARCHAR)); // STRUCT_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		declareParameter(new SqlParameter(Types.VARCHAR)); // STEP_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_TYPE
		declareParameter(new SqlParameter(Types.VARCHAR)); // MOLECULAR_FORMULA
		declareParameter(new SqlParameter(Types.NUMERIC)); // THEORITICAL_YIELD_PERCENT
		declareParameter(new SqlParameter(Types.VARCHAR)); // SALT_CODE
		declareParameter(new SqlParameter(Types.NUMERIC)); // SALT_EQUIVS
		declareParameter(new SqlParameter(Types.VARCHAR)); // LIST_KEY
		declareParameter(new SqlParameter(Types.NUMERIC)); // BATCH_MW_VALUE
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_MW_UNIT_CODE
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_MW_IS_CALC
		declareParameter(new SqlParameter(Types.NUMERIC)); // BATCH_MW_SIG_DIGITS
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_MW_SIG_DIGITS_SET
		declareParameter(new SqlParameter(Types.NUMERIC)); // BATCH_MW_USER_PREF_FIGS
		declareParameter(new SqlParameter(Types.VARCHAR)); // IS_LIMITING
		declareParameter(new SqlParameter(Types.VARCHAR)); // AUTO_CALC
		declareParameter(new SqlParameter(Types.VARCHAR)); // SYNTHSZD_BY
		declareParameter(new SqlParameter(Types.VARCHAR)); // ADDED_SOLV_BATCH_KEY
		declareParameter(new SqlParameter(Types.NUMERIC)); // NO_OF_TIMES_USED
		declareParameter(new SqlParameter(Types.NUMERIC)); // INTD_ADDITION_ORDER
		declareParameter(new SqlParameter(Types.VARCHAR)); // CHLORACNEGEN_TYPE
		declareParameter(new SqlParameter(Types.VARCHAR)); // IS_CHLORACNEGEN
		declareParameter(new SqlParameter(Types.VARCHAR)); // TESTED_FOR_CHLORACNEGEN
	}
	
	private static String getUpdateSql() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO CEN_BATCHES (");
		
		sb.append("BATCH_KEY,");
		sb.append("PAGE_KEY,");
		sb.append("BATCH_NUMBER,");
		sb.append("STRUCT_KEY,");
		sb.append("XML_METADATA,");
		sb.append("STEP_KEY,");
		sb.append("BATCH_TYPE,");
		sb.append("MOLECULAR_FORMULA,");
		sb.append("THEORITICAL_YIELD_PERCENT,");
		sb.append("SALT_CODE,");
		sb.append("SALT_EQUIVS,");
		sb.append("LIST_KEY,");
		sb.append("BATCH_MW_VALUE,");
		sb.append("BATCH_MW_UNIT_CODE,");
		sb.append("BATCH_MW_IS_CALC,");
		sb.append("BATCH_MW_SIG_DIGITS,");
		sb.append("BATCH_MW_SIG_DIGITS_SET,");
		sb.append("BATCH_MW_USER_PREF_FIGS,");
		sb.append("IS_LIMITING,");
		sb.append("AUTO_CALC,");
		sb.append("SYNTHSZD_BY,");
		sb.append("ADDED_SOLV_BATCH_KEY,");
		sb.append("NO_OF_TIMES_USED,");
		sb.append("INTD_ADDITION_ORDER,");
		sb.append("CHLORACNEGEN_TYPE,");
		sb.append("IS_CHLORACNEGEN,");
		sb.append("TESTED_FOR_CHLORACNEGEN");
		
		sb.append(") VALUES (");
		
		sb.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
		
		sb.append(")");
		
		return sb.toString();
	}
}
