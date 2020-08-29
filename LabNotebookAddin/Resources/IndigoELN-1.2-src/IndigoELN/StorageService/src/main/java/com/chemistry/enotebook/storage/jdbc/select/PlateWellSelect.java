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

import com.chemistry.enotebook.domain.AbstractPlate;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlateWellSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(PlateWellSelect.class);
	private AbstractPlate plate = null;


	public PlateWellSelect(DataSource dataSource, AbstractPlate plate) {
		super(dataSource, getQuery(plate.getKey()));
		this.plate = plate;
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		PlateWell well = new PlateWell(rs.getString("WELL_KEY"),plate);
		well.setLoadingFromDB(true);
		well.setWellPosition(rs.getInt("WELL_POSITION"));
		well.setWellType(rs.getString("WELL_TYPE"));
		well.setSolventCode(rs.getString("SOLVENT_CODE"));
		String batchIntheWellKey = rs.getString("BATCH_KEY");
		//unfilled/skipped well
		if(batchIntheWellKey != null )
		{
		well.setBatch(new BatchModel(batchIntheWellKey));
		}
		well.setBarCode(rs.getString("BARCODE"));
		well.setWellNumber(rs.getString("WELL_NUMBER"));
		//load weight amount
		getAmount(well.getContainedWeightAmount(),"WEIGHT",rs);
		//load volume AMT
		getAmount(well.getContainedVolumeAmount(),"VOLUME",rs);
		//load molarity AMT
		getAmount(well.getContainedMolarity(),"MOLARITY",rs);
		
		//load the PurificationService Params
		String purificationServiceKey = rs.getString("PURIFICATION_SERVICE_KEY");
		if(purificationServiceKey != null && !purificationServiceKey.equals(""))
		{
			PurificationServiceSubmisionParameters purificationServiceData = new PurificationServiceSubmisionParameters(purificationServiceKey,well.getKey());
			purificationServiceData.setModifiers(CommonUtils.getStringArray(rs.getString("MODIFIERS")));
			purificationServiceData.setArchivePlate(rs.getString("ARCHIVE_PLATE"));
			purificationServiceData.setArchiveVolume(rs.getDouble("ARCHIVE_VOLUME"));
			purificationServiceData.setDestinationLab(rs.getString("DESTINATION_LAB"));
			purificationServiceData.setInorganicByProductSaltPresent(rs.getString("INORGANIC_BYPRODUCT_SALT").equals("Y")?true:false);
			purificationServiceData.setSampleWorkUp(rs.getString("SAMPLE_WORKUP"));
			purificationServiceData.setSeperateTheIsomers(rs.getString("SEPERATE_ISOMERS").equals("Y")?true:false);
			purificationServiceData.setLoadedFromDB(true);
			well.setPurificationServiceParameter(purificationServiceData);
			log.debug("Loaded PurificationService data for the well with PurificationServiceKey:"+purificationServiceKey);
		}
		well.setLoadedFromDB(true);
		well.setModelChanged(false);
		well.setLoadingFromDB(false);
		return well;
	}
	
	private static String getQuery(String plateKey) {
		String selectQry = "SELECT pw.*, ca.* FROM CEN_PLATE_WELL pw LEFT OUTER JOIN CEN_PURIFICATION_SERVICE ca ON pw.WELL_KEY = ca.WELL_KEY WHERE pw.PLATE_KEY = '" + plateKey + "' order by well_position";
		log.debug(selectQry);
		return selectQry;
	}
	
	
	private void getAmount(AmountModel amount,String amtPrefix,ResultSet rs) throws SQLException
	{
		amount.setLoadingFromDB(true);
		Unit2 unit = new Unit2(rs.getString(amtPrefix+"_UNIT_CODE"));
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
