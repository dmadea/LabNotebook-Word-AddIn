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
package com.chemistry.enotebook.storage.jdbc.select;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.CeNBatchAmountsModel;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AmountSelect extends AbstractSelect {

	private static final Log log = LogFactory.getLog(AmountSelect.class);

	private Map batchAmountsMap = new HashMap(100);
	
	public AmountSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		CeNBatchAmountsModel batchAmounts = new CeNBatchAmountsModel();
		batchAmounts.setBatchKey(rs.getString("BATCH_KEY"));
		batchAmounts.setPageKey(rs.getString("PAGE_KEY"));
		//load weight AMT
		getAmount(batchAmounts.getWeightAmount(),"WEIGHT",rs);
		//load volume AMT
		getAmount(batchAmounts.getVolumeAmount(),"VOLUME",rs);
		//load molarity AMT
		getAmount(batchAmounts.getMolarAmount(),"MOLARITY",rs);
		//load density AMT
		getAmount(batchAmounts.getDensityAmount(),"DENSITY",rs);
		//load mole AMT
		getAmount(batchAmounts.getMoleAmount(),"MOLE",rs);
//		load Purity AMT
		getAmount(batchAmounts.getPurityAmount(),"PURITY",rs);
		//load Loading AMT
		getAmount(batchAmounts.getLoadingAmount(),"LOADING",rs);
		//load RxnEquivs AMT
		getAmount(batchAmounts.getRxnEquivsAmount(),"RXNEQUIVS",rs);
		//load theo wt AMT
		getAmount(batchAmounts.getTheoreticalWeightAmount(),"THEO_WT",rs);
		//load theo mole AMT
		getAmount(batchAmounts.getTheoreticalMoleAmount(),"THEO_MOLE",rs);
//		load total weight AMT
		getAmount(batchAmounts.getTotalWeight(),"TOTAL_WT",rs);
		//load total volume AMT
		getAmount(batchAmounts.getTotalVolume(),"TOTAL_VOL",rs);
		//load deliv wt AMT
		getAmount(batchAmounts.getDeliveredWeight(),"DELIV_WT",rs);
		//load deliv vol AMT
		getAmount(batchAmounts.getDeliveredVolume(),"DELIV_VOL",rs);
		//load needed mole AMT
		getAmount(batchAmounts.getAmountNeeded(),"NEEDED_MOLE",rs);
//		load extra needed wt AMT
		getAmount(batchAmounts.getExtraNeeded(),"EX_NEDED_MOLE",rs);
		//load SOLUTE WT AMT
		getAmount(batchAmounts.getSoluteAmount(),"SOLUTE_WT",rs);
		//load prev molarity AMT
		getAmount(batchAmounts.getPreviousMolarAmount(),"PREV_MOLAR",rs);
		//load theo yld percent AMT
		getAmount(batchAmounts.getTheoreticalYieldPercentAmount(),"THEO_YLD_PCNT",rs);
    	batchAmountsMap.put(batchAmounts.getBatchKey(),batchAmounts);
		
		return batchAmounts;
	}

	/**
	 * @return the batchAmountsMap
	 */
	public Map getBatchAmountsMap() {
		return batchAmountsMap;
	}

	/**
	 * @param batchAmountsMap the batchAmountsMap to set
	 */
	public void setBatchAmountsMap(Map batchAmountsMap) {
		this.batchAmountsMap = batchAmountsMap;
	}

	//
	private void getAmount(AmountModel amount,String amtPrefix,ResultSet rs) throws SQLException
	{
		//flag to stop calculations
		amount.setLoadingFromDB(true);
		String unitCode = rs.getString(amtPrefix+"_UNIT_CODE");
		if(unitCode == null ) return;
		Unit2 unit = new Unit2(unitCode);
		amount.setUnit(unit);
		amount.setValue(rs.getDouble(amtPrefix+"_VALUE"));
		String calculated = rs.getString(amtPrefix+"_IS_CALC");
		//default column is 'Y' and model attrib val is true
		if(calculated.equals("N"))
		{
			amount.setCalculated(false);
		}
	
		amount.setSigDigits(rs.getInt(amtPrefix+"_SIG_DIGITS"));
		String isSigSet = rs.getString(amtPrefix+"_SIG_DIGITS_SET");
		if(isSigSet.equals("N"))
		{
			amount.setSigDigitsSet(false);
		}
		
		amount.setUserPrefFigs(rs.getInt(amtPrefix+"_USER_PREF_FIGS"));
		amount.setLoadingFromDB(false);
	}
	
	

}
