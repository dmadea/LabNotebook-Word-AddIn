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
 * Created on Aug 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.session.ejb;

import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.SystemProperties;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class CeNPropertiesDAO {
	
	private static final String s_SelectSql = "SELECT P.ENABLED_FLAG, P.XML_METADATA AS XML_METADATA FROM CEN_PROPERTIES P WHERE P.SITE_CODE = ?";
	private static final String s_DataSourceName = ServiceLocator.CEN_DS_JNDI;
	
	public SystemProperties getData(String siteCode) throws Exception {
		SystemProperties props = null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try	{
			conn = getCeNConnection();
			stmt = conn.prepareStatement(s_SelectSql);
			
			if (siteCode == null || siteCode.length() == 0)
				siteCode = "GBL";
			
			stmt.setString(1, siteCode.toUpperCase());
			
			rs = stmt.executeQuery();
			
			while (rs.next())	{
				props = new SystemProperties();
				
				props.setSiteCode(siteCode);
				props.setEnabled(rs.getString("ENABLED_FLAG").equalsIgnoreCase("YES"));
				props.setProperties(rs.getString("XML_METADATA"));
			}
		} catch (Exception e) {
			throw new UserDAOException("CeNPropertiesDAO:getData:: Failed to Retrieve System Properties", e);
		} finally {
			cleanUp(conn, stmt, rs);
		}
		
		return props;
	}
	
	private Connection getCeNConnection() throws NamingException, SQLException, PropertyException {
		DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(s_DataSourceName));
		return ds.getConnection();
	}
	
	private void cleanUp(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
		}
		
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
		}
		
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}
}
