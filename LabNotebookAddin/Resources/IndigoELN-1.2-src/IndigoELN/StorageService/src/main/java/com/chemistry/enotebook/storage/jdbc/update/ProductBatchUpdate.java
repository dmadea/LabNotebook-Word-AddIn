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

public class ProductBatchUpdate extends SqlUpdate {

	private static final Log log = LogFactory.getLog(ProductBatchUpdate.class);

	public ProductBatchUpdate(DataSource ds) {
		super(ds, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_NUMBER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_TYPE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //MOLECULAR_FORMULA
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // THEORITICAL_YIELD_PERCENT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // SALT_CODE
		this.declareParameter(new SqlParameter(Types.NUMERIC)); // SALT_EQUIVS
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //BATCH_MW_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_MW_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_MW_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //BATCH_MW_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //BATCH_MW_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //BATCH_MW_USER_PREF_FIGS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //IS_LIMITING
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //AUTO_CALC
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SYNTHSZD_BY
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //ADDED_SOLV_BATCH_KEY
		this.declareParameter(new SqlParameter(Types.INTEGER)); //NO_OF_TIMES_USED
		this.declareParameter(new SqlParameter(Types.INTEGER)); //INTD_ADDITION_ORDER
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //CHLORACNEGEN_TYPE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //IS_CHLORACNEGEN
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //TESTED_FOR_CHLORACNEGEN */
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_KEY
		
	}

	static protected String getUpdateSql() {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE CEN_BATCHES SET ");
		sql.append("BATCH_NUMBER = ?, ");
		sql.append("XML_METADATA = ?, ");
		sql.append("BATCH_TYPE = ?, ");
		sql.append("MOLECULAR_FORMULA = ?, ");
		sql.append("THEORITICAL_YIELD_PERCENT = ?, ");
		sql.append("SALT_CODE = ?, ");
		sql.append("SALT_EQUIVS = ? ");
		sql.append(",BATCH_MW_VALUE = ? ");
		sql.append(",BATCH_MW_UNIT_CODE = ? ");
		sql.append(",BATCH_MW_IS_CALC = ? ");
		sql.append(",BATCH_MW_SIG_DIGITS = ? ");
		sql.append(",BATCH_MW_SIG_DIGITS_SET = ? ");
		sql.append(",BATCH_MW_USER_PREF_FIGS = ? ");
		sql.append(",IS_LIMITING = ? ");
		sql.append(",AUTO_CALC = ? ");
		sql.append(",SYNTHSZD_BY = ? ");
		sql.append(",ADDED_SOLV_BATCH_KEY = ? ");
		sql.append(",NO_OF_TIMES_USED = ? ");
		sql.append(",INTD_ADDITION_ORDER = ? ");
		sql.append(",CHLORACNEGEN_TYPE = ? ");
		sql.append(",IS_CHLORACNEGEN = ? ");
		sql.append(",TESTED_FOR_CHLORACNEGEN = ? "); 
		sql.append(" WHERE BATCH_KEY = ? ");
		if (log.isDebugEnabled()) {
			log.debug(sql.toString());
		}
		return sql.toString();
	}

}
