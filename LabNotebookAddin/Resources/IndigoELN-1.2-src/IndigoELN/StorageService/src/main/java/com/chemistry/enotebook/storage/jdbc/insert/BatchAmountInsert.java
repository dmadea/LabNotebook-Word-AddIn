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

public class BatchAmountInsert extends BatchSqlUpdate {

	public BatchAmountInsert(DataSource ds) {
		super(ds, getUpdateSql());
		
		declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_KEY
		declareParameter(new SqlParameter(Types.VARCHAR)); // PAGE_KEY
		
		for (int i = 0; i < 19; ++i) {
			declareParameter(new SqlParameter(Types.NUMERIC)); // VALUE
			declareParameter(new SqlParameter(Types.VARCHAR)); // UNIT_CODE
			declareParameter(new SqlParameter(Types.VARCHAR)); // IS_CALC
			declareParameter(new SqlParameter(Types.NUMERIC)); // SIG_DIGITS
			declareParameter(new SqlParameter(Types.VARCHAR)); // SIG_DIGITS_SET
			declareParameter(new SqlParameter(Types.NUMERIC)); // USER_PREFS_FIGS
		}
	}
	
	private static String getUpdateSql() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO CEN_BATCH_AMOUNTS (");
		
		sb.append("BATCH_KEY,");
		sb.append("PAGE_KEY,");
		
		sb.append("WEIGHT_VALUE,");
		sb.append("WEIGHT_UNIT_CODE,");
		sb.append("WEIGHT_IS_CALC,");
		sb.append("WEIGHT_SIG_DIGITS,");
		sb.append("WEIGHT_SIG_DIGITS_SET,");
		sb.append("WEIGHT_USER_PREF_FIGS,");
		
		sb.append("VOLUME_VALUE,");
		sb.append("VOLUME_UNIT_CODE,");
		sb.append("VOLUME_IS_CALC,");
		sb.append("VOLUME_SIG_DIGITS,");
		sb.append("VOLUME_SIG_DIGITS_SET,");
		sb.append("VOLUME_USER_PREF_FIGS,");
		
		sb.append("MOLARITY_VALUE,");
		sb.append("MOLARITY_UNIT_CODE,");
		sb.append("MOLARITY_IS_CALC,");
		sb.append("MOLARITY_SIG_DIGITS,");
		sb.append("MOLARITY_SIG_DIGITS_SET,");
		sb.append("MOLARITY_USER_PREF_FIGS,");
		
		sb.append("MOLE_VALUE,");
		sb.append("MOLE_UNIT_CODE,");
		sb.append("MOLE_IS_CALC,");
		sb.append("MOLE_SIG_DIGITS,");
		sb.append("MOLE_SIG_DIGITS_SET,");
		sb.append("MOLE_USER_PREF_FIGS,");
		
		sb.append("DENSITY_VALUE,");
		sb.append("DENSITY_UNIT_CODE,");
		sb.append("DENSITY_IS_CALC,");
		sb.append("DENSITY_SIG_DIGITS,");
		sb.append("DENSITY_SIG_DIGITS_SET,");
		sb.append("DENSITY_USER_PREF_FIGS,");
		
		sb.append("PURITY_VALUE,");
		sb.append("PURITY_UNIT_CODE,");
		sb.append("PURITY_IS_CALC,");
		sb.append("PURITY_SIG_DIGITS,");
		sb.append("PURITY_SIG_DIGITS_SET,");
		sb.append("PURITY_USER_PREF_FIGS,");
		
		sb.append("LOADING_VALUE,");
		sb.append("LOADING_UNIT_CODE,");
		sb.append("LOADING_IS_CALC,");
		sb.append("LOADING_SIG_DIGITS,");
		sb.append("LOADING_SIG_DIGITS_SET,");
		sb.append("LOADING_USER_PREF_FIGS,");
		
		sb.append("RXNEQUIVS_VALUE,");
		sb.append("RXNEQUIVS_UNIT_CODE,");
		sb.append("RXNEQUIVS_IS_CALC,");
		sb.append("RXNEQUIVS_SIG_DIGITS,");
		sb.append("RXNEQUIVS_SIG_DIGITS_SET,");
		sb.append("RXNEQUIVS_USER_PREF_FIGS,");
		
		sb.append("THEO_WT_VALUE,");
		sb.append("THEO_WT_UNIT_CODE,");
		sb.append("THEO_WT_IS_CALC,");
		sb.append("THEO_WT_SIG_DIGITS,");
		sb.append("THEO_WT_SIG_DIGITS_SET,");
		sb.append("THEO_WT_USER_PREF_FIGS,");
		
		sb.append("THEO_MOLE_VALUE,");
		sb.append("THEO_MOLE_UNIT_CODE,");
		sb.append("THEO_MOLE_IS_CALC,");
		sb.append("THEO_MOLE_SIG_DIGITS,");
		sb.append("THEO_MOLE_SIG_DIGITS_SET,");
		sb.append("THEO_MOLE_USER_PREF_FIGS,");
		
		sb.append("TOTAL_WT_VALUE,");
		sb.append("TOTAL_WT_UNIT_CODE,");
		sb.append("TOTAL_WT_IS_CALC,");
		sb.append("TOTAL_WT_SIG_DIGITS,");
		sb.append("TOTAL_WT_SIG_DIGITS_SET,");
		sb.append("TOTAL_WT_USER_PREF_FIGS,");
		
		sb.append("TOTAL_VOL_VALUE,");
		sb.append("TOTAL_VOL_UNIT_CODE,");
		sb.append("TOTAL_VOL_IS_CALC,");
		sb.append("TOTAL_VOL_SIG_DIGITS,");
		sb.append("TOTAL_VOL_SIG_DIGITS_SET,");
		sb.append("TOTAL_VOL_USER_PREF_FIGS,");
		
		sb.append("DELIV_WT_VALUE,");
		sb.append("DELIV_WT_UNIT_CODE,");
		sb.append("DELIV_WT_IS_CALC,");
		sb.append("DELIV_WT_SIG_DIGITS,");
		sb.append("DELIV_WT_SIG_DIGITS_SET,");
		sb.append("DELIV_WT_USER_PREF_FIGS,");
		
		sb.append("DELIV_VOL_VALUE,");
		sb.append("DELIV_VOL_UNIT_CODE,");
		sb.append("DELIV_VOL_IS_CALC,");
		sb.append("DELIV_VOL_SIG_DIGITS,");
		sb.append("DELIV_VOL_SIG_DIGITS_SET,");
		sb.append("DELIV_VOL_USER_PREF_FIGS,");
		
		sb.append("NEEDED_MOLE_VALUE,");
		sb.append("NEEDED_MOLE_UNIT_CODE,");
		sb.append("NEEDED_MOLE_IS_CALC,");
		sb.append("NEEDED_MOLE_SIG_DIGITS,");
		sb.append("NEEDED_MOLE_SIG_DIGITS_SET,");
		sb.append("NEEDED_MOLE_USER_PREF_FIGS,");
		
		sb.append("EX_NEDED_MOLE_VALUE,");
		sb.append("EX_NEDED_MOLE_UNIT_CODE,");
		sb.append("EX_NEDED_MOLE_IS_CALC,");
		sb.append("EX_NEDED_MOLE_SIG_DIGITS,");
		sb.append("EX_NEDED_MOLE_SIG_DIGITS_SET,");
		sb.append("EX_NEDED_MOLE_USER_PREF_FIGS,");
		
		sb.append("SOLUTE_WT_VALUE,");
		sb.append("SOLUTE_WT_UNIT_CODE,");
		sb.append("SOLUTE_WT_IS_CALC,");
		sb.append("SOLUTE_WT_SIG_DIGITS,");
		sb.append("SOLUTE_WT_SIG_DIGITS_SET,");
		sb.append("SOLUTE_WT_USER_PREF_FIGS,");
		
		sb.append("PREV_MOLAR_VALUE,");
		sb.append("PREV_MOLAR_UNIT_CODE,");
		sb.append("PREV_MOLAR_IS_CALC,");
		sb.append("PREV_MOLAR_SIG_DIGITS,");
		sb.append("PREV_MOLAR_SIG_DIGITS_SET,");
		sb.append("PREV_MOLAR_USER_PREF_FIGS,");
		
		sb.append("THEO_YLD_PCNT_VALUE,");
		sb.append("THEO_YLD_PCNT_UNIT_CODE,");
		sb.append("THEO_YLD_PCNT_IS_CALC,");
		sb.append("THEO_YLD_PCNT_SIG_DIGITS,");
		sb.append("THEO_YLD_PCNT_SIG_DIGITS_SET,");
		sb.append("THEO_YLD_PCNT_USER_PREF_FIGS");
		
		sb.append(") VALUES (");
		
		for (int i = 0; i < 116; ++i) {
			sb.append("?");
			if (i != 115)
				sb.append(",");
		}
		
		sb.append(")");
		
		return sb.toString();
	}
}
