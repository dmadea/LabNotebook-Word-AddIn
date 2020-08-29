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
/*
 * Created on Jul 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.registration.dao;

import com.chemistry.enotebook.registration.CompoundRegistrationDaoException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CompoundRegistrationDao 
{
    private static final String TABLE_NAME = "COMPOUND_REGISTRATION_COMPOUND_PARENT";
    private static final String GLOBAL_SEQ_NUMBER = "GLOBAL_SEQ_NUMBER";
    private static final String GLOBAL_NUMBER = "GLOBAL_NUMBER";
    private static final String STEREOISOMER_CODE = "STEREOISOMER_CODE";
    private static final String CAL_PARENT_MF = "CAL_PARENT_MF";
    private static final String CAL_PARENT_MW = "CAL_PARENT_MW";
    private static final String STRUCTURE_COMMENT = "STRUCTURE_COMMENT";
    private static final String ORIGIN_SITE_CODE = "ORIGIN_SITE_CODE";
    private static final String PARENT_HAZARD_COMMENT = "PARENT_HAZARD_COMMENT";
    private static final String LOADER_ID = "LOADER_ID";
    private static final String LOAD_DATE = "LOAD_DATE";
    private static final String AUDIT_ID = "AUDIT_ID";
    private static final String AUDIT_DATE = "AUDIT_DATE";

    private final String JOIN_TABLE_NAME_STEREOISOMER_CDT = "COMPOUND_REGISTRATION_STEREOISOMER_CDT";
    private final String JOIN_STEREOISOMER_CODE = "STEREOISOMER_CODE";
    private final String JOIN_STEREOISOMER_DETAIL_DESC = "STEREOISOMER_DETAIL_DESC";

    
    public String getCompoundNoFromGlobalSeqNumber(String seqNumber) 
		throws CompoundRegistrationDaoException
	{
		String compoundNumber = null;
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		String sql = "SELECT " + GLOBAL_NUMBER + " " +
			         "FROM " + TABLE_NAME + " p " +
			         "WHERE " + GLOBAL_SEQ_NUMBER + " = ? ";
		
		try {
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI("COMPOUND_REGISTRATION_DS_JNDI"))).getConnection();
			
			pst = con.prepareStatement(sql);
			pst.setString(1, seqNumber);
			rs = pst.executeQuery();
	
			if (rs.next()) compoundNumber = rs.getString(1);
		} catch (Exception e) {
			throw new CompoundRegistrationDaoException("Error processing request", e);
		} finally {
			if (rs != null)  try { rs.close(); }  catch (SQLException e) { /* ignored */ }
			if (pst != null) try { pst.close(); } catch (SQLException e) { /* ignored */ }
			if (con != null) try { con.close(); } catch (SQLException e) { /* ignored */ }
		}
		
		return compoundNumber;
	}
}