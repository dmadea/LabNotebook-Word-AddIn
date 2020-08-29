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
 * Created on Dec 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.session;

import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * 
 *
 * TODO Add Class Information
 */
public class SystemHealth
{
	public boolean testDBConnection(String DSName) {
		boolean connected = false;
		Connection con = null;
		Statement st = null;
		String sqlQuery = "SELECT 1 FROM CEN_PROPERTIES";
		ResultSet rs = null;
		
		try {
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(DSName))).getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			connected = true;
		} catch (Exception e) {
			connected = false;
		} finally {
			try { if (rs != null)  rs.close(); }  catch (Exception e) { }
			try { if (st != null)  st.close(); }  catch (Exception e) { }
			try { if (con != null) con.close(); } catch (Exception e) { }
		}
		
		return connected;
	}
}
