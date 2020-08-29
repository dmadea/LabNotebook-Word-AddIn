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

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class CeNBatchAmountsUpdate extends SqlUpdate {
	
	
	/*
	  Order of Amounts
	  
	   weight
	   volume
	   molarity
	   mole
	   density
	   purity
	   loading
	   rxnequivs
	   theo_wt
	   theo_mol
	   tot_wt
	   tot_vol
	   del_wt
	   deliv_vol
	   need_mol
	   extra_need_mol
	   solvent_mol
	   prev_mol
	   theoYld
	  
	*/
	public CeNBatchAmountsUpdate(DataSource dsource) {
		super(dsource, AmountUpdateSql);
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //WEIGHT_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //VOLUME_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //MOLARIY_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //MOLE_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //DENSITY_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //PURITY_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //LOADING_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //RXNEQUIVS_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //THEO_WT_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //THEO_MOL_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //TOT_WT_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //TOT_VOL_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //DEL_WT_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //DEL_VOL_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //NEEDED_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //EXTRA_NEED_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //SOLUTE_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //PREV_MOLAR_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS
		
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //THEO_YIELD_VALUE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_UNIT_CODE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_IS_CALC
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_SIG_DIGITS
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //_SIG_DIGITS_SET
		this.declareParameter(new SqlParameter(Types.NUMERIC)); //_USER_PREF_FIGS

		this.declareParameter(new SqlParameter(Types.VARCHAR)); // BATCH_KEY
		
	}
	
	static protected String AmountUpdateSql = "UPDATE CEN_BATCH_AMOUNTS SET "+
	"WEIGHT_VALUE=?,"+
"WEIGHT_UNIT_CODE=?,"+    
"WEIGHT_IS_CALC=?,"+   
"WEIGHT_SIG_DIGITS=?,"+    
"WEIGHT_SIG_DIGITS_SET=?,"+    
"WEIGHT_USER_PREF_FIGS=?,"+
"VOLUME_VALUE=?,"+
"VOLUME_UNIT_CODE=?,"+    
"VOLUME_IS_CALC=?,"+
"VOLUME_SIG_DIGITS=?,"+   
"VOLUME_SIG_DIGITS_SET=?,"+    
"VOLUME_USER_PREF_FIGS=?,"+
"MOLARITY_VALUE=?,"+
"MOLARITY_UNIT_CODE=?,"+    
"MOLARITY_IS_CALC=?,"+
"MOLARITY_SIG_DIGITS=?,"+   
"MOLARITY_SIG_DIGITS_SET=?,"+    
"MOLARITY_USER_PREF_FIGS=?,"+
"MOLE_VALUE=?,"+
"MOLE_UNIT_CODE=?,"+    
"MOLE_IS_CALC=?,"+
"MOLE_SIG_DIGITS=?,"+   
"MOLE_SIG_DIGITS_SET=?,"+    
"MOLE_USER_PREF_FIGS=?,"+
"DENSITY_VALUE=?,"+
"DENSITY_UNIT_CODE=?,"+    
"DENSITY_IS_CALC=?,"+
"DENSITY_SIG_DIGITS=?,"+   
"DENSITY_SIG_DIGITS_SET=?,"+    
"DENSITY_USER_PREF_FIGS=?,"+
"PURITY_VALUE=?,"+
"PURITY_UNIT_CODE=?,"+    
"PURITY_IS_CALC=?,"+
"PURITY_SIG_DIGITS=?,"+   
"PURITY_SIG_DIGITS_SET=?,"+    
"PURITY_USER_PREF_FIGS=?,"+
"LOADING_VALUE=?,"+
"LOADING_UNIT_CODE=?,"+    
"LOADING_IS_CALC=?,"+
"LOADING_SIG_DIGITS=?,"+   
"LOADING_SIG_DIGITS_SET=?,"+    
"LOADING_USER_PREF_FIGS=?,"+
"RXNEQUIVS_VALUE=?,"+
"RXNEQUIVS_UNIT_CODE=?,"+    
"RXNEQUIVS_IS_CALC=?,"+
"RXNEQUIVS_SIG_DIGITS=?,"+   
"RXNEQUIVS_SIG_DIGITS_SET=?,"+
"RXNEQUIVS_USER_PREF_FIGS=?,"+
"THEO_WT_VALUE=?,"+
"THEO_WT_UNIT_CODE=?,"+    
"THEO_WT_IS_CALC=?,"+
"THEO_WT_SIG_DIGITS=?,"+   
"THEO_WT_SIG_DIGITS_SET=?,"+    
"THEO_WT_USER_PREF_FIGS=?,"+
"THEO_MOLE_VALUE=?,"+
"THEO_MOLE_UNIT_CODE=?,"+    
"THEO_MOLE_IS_CALC=?,"+
"THEO_MOLE_SIG_DIGITS=?,"+   
"THEO_MOLE_SIG_DIGITS_SET=?,"+
"THEO_MOLE_USER_PREF_FIGS=?,"+
"TOTAL_WT_VALUE=?,"+
"TOTAL_WT_UNIT_CODE=?,"+
"TOTAL_WT_IS_CALC=?,"+
"TOTAL_WT_SIG_DIGITS=?,"+
"TOTAL_WT_SIG_DIGITS_SET=?,"+    
"TOTAL_WT_USER_PREF_FIGS=?,"+
"TOTAL_VOL_VALUE=?,"+
"TOTAL_VOL_UNIT_CODE=?,"+    
"TOTAL_VOL_IS_CALC=?,"+
"TOTAL_VOL_SIG_DIGITS=?,"+
"TOTAL_VOL_SIG_DIGITS_SET=?,"+
"TOTAL_VOL_USER_PREF_FIGS=?,"+
"DELIV_WT_VALUE=?,"+
"DELIV_WT_UNIT_CODE=?,"+    
"DELIV_WT_IS_CALC=?,"+
"DELIV_WT_SIG_DIGITS=?,"+   
"DELIV_WT_SIG_DIGITS_SET=?,"+    
"DELIV_WT_USER_PREF_FIGS=?,"+
"DELIV_VOL_VALUE=?,"+
"DELIV_VOL_UNIT_CODE=?,"+    
"DELIV_VOL_IS_CALC=?,"+
"DELIV_VOL_SIG_DIGITS=?,"+   
"DELIV_VOL_SIG_DIGITS_SET=?,"+
"DELIV_VOL_USER_PREF_FIGS=?,"+
"NEEDED_MOLE_VALUE=?,"+
"NEEDED_MOLE_UNIT_CODE=?,"+    
"NEEDED_MOLE_IS_CALC=?,"+
"NEEDED_MOLE_SIG_DIGITS=?,"+    
"NEEDED_MOLE_SIG_DIGITS_SET=?,"+
"NEEDED_MOLE_USER_PREF_FIGS=?,"+
"EX_NEDED_MOLE_VALUE=?,"+
"EX_NEDED_MOLE_UNIT_CODE=?,"+    
"EX_NEDED_MOLE_IS_CALC=?,"+
"EX_NEDED_MOLE_SIG_DIGITS=?,"+   
"EX_NEDED_MOLE_SIG_DIGITS_SET=?,"+    
"EX_NEDED_MOLE_USER_PREF_FIGS=?,"+
"SOLUTE_WT_VALUE=?,"+
"SOLUTE_WT_UNIT_CODE=?,"+    
"SOLUTE_WT_IS_CALC=?,"+
"SOLUTE_WT_SIG_DIGITS=?,"+   
"SOLUTE_WT_SIG_DIGITS_SET=?,"+    
"SOLUTE_WT_USER_PREF_FIGS=?,"+
"PREV_MOLAR_VALUE=?,"+
"PREV_MOLAR_UNIT_CODE=?,"+    
"PREV_MOLAR_IS_CALC=?,"+
"PREV_MOLAR_SIG_DIGITS=?,"+   
"PREV_MOLAR_SIG_DIGITS_SET=?,"+    
"PREV_MOLAR_USER_PREF_FIGS=?,"+
"THEO_YLD_PCNT_VALUE=?,"+
"THEO_YLD_PCNT_UNIT_CODE=?,"+    
"THEO_YLD_PCNT_IS_CALC=?,"+
"THEO_YLD_PCNT_SIG_DIGITS=?,"+   
"THEO_YLD_PCNT_SIG_DIGITS_SET=?,"+    
"THEO_YLD_PCNT_USER_PREF_FIGS=?"+
" WHERE BATCH_KEY = ?"	;
	
}
