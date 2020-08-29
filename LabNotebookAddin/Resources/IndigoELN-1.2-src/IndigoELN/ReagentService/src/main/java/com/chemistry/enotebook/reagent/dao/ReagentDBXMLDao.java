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
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.common.DAO;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReagentDBXMLDao implements DAO, Serializable {
	
	static final long serialVersionUID = -3556425260900782571L;

	public ReagentDBXMLDao() {
	}

	public String getReagentDBXML(String siteCode) throws ReagentMgmtException {
		String reagentDBXML = "";
		
		Connection con = null;
		ResultSet rset = null;

		try {
			String queryString = "SELECT P.REAGENT_DB_PROPERTIES AS REAGENT_DB_PROPERTIES FROM CEN_PROPERTIES P WHERE P.SITE_CODE = '" + siteCode + "'";
			
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();
			rset = con.createStatement().executeQuery(queryString);

			if(rset.next())
				reagentDBXML = rset.getString("REAGENT_DB_PROPERTIES");

		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (SQLException e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}

		return reagentDBXML;
	}

	public void updateReagentDBXML(String SiteCode, String reagentDBXML) throws ReagentMgmtException {
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			String queryString = "UPDATE CEN_PROPERTIES SET REAGENT_DB_PROPERTIES = ? WHERE SITE_CODE = ?";
			
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();
			pst = con.prepareStatement(queryString);
			
			pst.setString(1, reagentDBXML);
			pst.setString(2, SiteCode);
			
			pst.executeUpdate();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		} finally {
			try {
				if (pst != null)
					pst.close();
			} catch (SQLException e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
	}
}
