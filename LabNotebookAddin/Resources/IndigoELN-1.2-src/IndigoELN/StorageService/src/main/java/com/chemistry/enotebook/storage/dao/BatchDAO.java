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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BatchDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(BatchDAO.class);

	protected Object[] getOraCompound(ParentCompoundModel compound, String pageKey) {
		try {
			return new Object[] {
					compound.getKey(), // STRUCT_KEY
					pageKey, // PAGE_KEY
					compound.getStringSketch(), //STRUCT_SKETCH
					compound.getNativeSketch(), // NATIVE_STRUCT_SKETCH
					compound.getViewSketch(), // STRUCT_IMAGE
					null, // XML_METADATA
					compound.getChemicalName(), // CHEMICAL_NAME
					compound.getMolFormula(), // MOLECULAR_FORMULA
					new Double(compound.getMolWgt()),// MOLECULAR_WEIGHT
					compound.getVirtualCompoundId(), // VIRTUAL_COMPOUND_ID
					compound.getRegNumber(), // REGISTRATION_NUMBER
					compound.getCASNumber(),
					compound.getStringSketchFormat(),
                    compound.getNativeSketchFormat(),
                    null,
                    compound.getHazardComments(),
                    compound.getStructureComments(),
                    compound.getStereoisomerCode(),
                    compound.getCompoundName(),
                    new Double(compound.getBoilingPt().doubleValue()),
                    compound.getBoilingPt().getUnit().getCode(),
                    new Double(compound.getMeltingPt().doubleValue()),
                    compound.getMeltingPt().getUnit().getCode(),
                    new Character(compound.isCreatedByNotebook()== true?'Y':'N'),
                    new Double(compound.getExactMass()),
                    compound.getCompoundParent()
				};
		} catch (Exception error) {
			log.error("Failed to create 'COMPOUND' object  : "+error.getMessage());
    		log.error(CommonUtils.getStackTrace(error));
    		throw new JDBCRuntimeException(error);
    	}
	}

	public void updateBatchWithCompound(BatchModel batch, Connection con) throws SQLException {
		String sql1 = "update cen_batches set xml_metadata = ? where batch_key = ?";
		String sql2 = "update cen_structures set xml_metadata = ? where struct_key = ?";
		
		PreparedStatement st = con.prepareStatement(sql1);

		st.setString(1, batch.toXML());
		st.setString(2, batch.getKey());
		st.executeUpdate();
		st.close();

		st = con.prepareStatement(sql2);

		st.setString(1, batch.getCompound().toXML());
		st.setString(2, batch.getCompound().getKey());
		st.executeUpdate();
		st.close();
	}
}
