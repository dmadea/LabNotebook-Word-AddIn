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
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StructureSelect extends AbstractSelect {

	private static final Log log = LogFactory.getLog(StructureSelect.class);

	public StructureSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		// declareParameter(new SqlParameter(Types.VARCHAR));
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		long starttime = System.currentTimeMillis();
		ParentCompoundModel compound = new ParentCompoundModel(rs.getString("STRUCT_KEY"));

		compound.setChemicalName(rs.getString("CHEMICAL_NAME"));

		compound.setStringSketch(rs.getBytes("STRUCT_SKETCH"));
		compound.setNativeSketch(rs.getBytes("NATIVE_STRUCT_SKETCH"));
		compound.setViewSketch(rs.getBytes("STRUCT_IMAGE"));
		
		compound.setMolFormula(rs.getString("MOLECULAR_FORMULA"));
		compound.setMolWgt(CommonUtils.toDouble(rs.getString("MOLECULAR_WEIGHT")));
		compound.setVirtualCompoundId(rs.getString("VIRTUAL_COMPOUND_ID"));
		compound.setRegNumber(rs.getString("REGISTRATION_NUMBER"));
		compound.setNativeSketchFormat(rs.getString("NATIVE_STRUCT_SKTH_FRMT"));
		compound.setStringSketchFormat(rs.getString("STRUCT_SKTH_FRMT"));
		compound.setCompoundName(rs.getString("COMPOUND_NAME"));
		String createdByNotebookStr = rs.getString("CREATED_BY_NOTEBOOK");
		boolean createdByNotebookFlag = false;
		if (createdByNotebookStr.equalsIgnoreCase("true")) {
			createdByNotebookFlag = true;
		}
		compound.setCreatedByNotebook(createdByNotebookFlag);
		compound.setExactMass(CommonUtils.toDouble(rs.getString("EXACT_MASS")));
		compound.setStereoisomerCode(rs.getString("STEREOISOMER_CODE"));
		this.getBoilingPointAmount(compound.getBoilingPt(),rs);
		this.getMeltingPointAmount(compound.getMeltingPt(),rs);
		compound.setCASNumber(rs.getString("CAS_NUMBER"));
		compound.setHazardComments(rs.getString("USER_HAZARD_COMMENTS"));
		compound.setCompoundParent(rs.getString("COMPOUND_PARENT_ID"));
		compound.setStructureComments(rs.getString("STRUCT_COMMENTS"));
	
		compound.setLoadedFromDB(true);
		compound.setModelChanged(false);
		return compound;
	}

	public void getBoilingPointAmount(AmountModel amt ,ResultSet rs) throws SQLException {
		String unitCode = rs.getString("BOILING_PT_UNIT_CODE");
		if(unitCode == null) return;
		Unit2 unit = new Unit2(UnitType.TEMP, unitCode);
		amt.setUnit(unit);
		amt.setValue(CommonUtils.toDouble(rs.getString("BOILING_PT_VALUE")));

	}

	public void getMeltingPointAmount(AmountModel amt,ResultSet rs) throws SQLException {
		String unitCode = rs.getString("MELTING_PT_UNIT_CODE");
		if(unitCode == null) return;
		Unit2 unit = new Unit2(UnitType.TEMP, unitCode);
		amt.setUnit(unit);
		amt.setValue(CommonUtils.toDouble(rs.getString("MELTING_PT_VALUE")));

	}
}
