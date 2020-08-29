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

import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.storage.DAOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

public class DSPDAO {

	private String JDBC_DSP_DBURI = "";
	private String dspDBAccountUserID = "";
	private String dspDBAccountPsswd = "";

	private static final Log log = LogFactory.getLog(DSPDAO.class);
	
	public DSPDAO() throws Exception
	{
		loadProperties();
	}
	
	/**
	 * 
	 * @param spid
	 */
	public void lockDSPSPID(String spid) throws Exception
	{
		String methodName = "lockDSPSPID()";
		String sqlStmt = "UPDATE DSP_SYNTH_PLAN "
			+ "SET LOCKED = 'Y' "
			+ " where SPID = ?";

		log.info(sqlStmt);
		
		//
		// Operation
		//
		Connection dbConn = null;
		PreparedStatement cs = null;

		try {
			dbConn = getDSPDBConnection();
			if(dbConn == null) return;
			// prepare and execute the db call
			cs = dbConn.prepareStatement(sqlStmt);			
			cs.setString(1, spid);
    		cs.executeUpdate();
    		log.info(methodName + " executed update for spid."+spid);
		} catch (Exception e) {
			log.error(methodName +" failed for SPID: "+ spid, e);
			throw new DAOException(methodName +" failed for SPID: "+ spid, e);
		}
		finally {
			try {
				if(dbConn != null) {
					dbConn.close();
				}
			} catch (Exception e) {
				log.error(methodName + e.getMessage());
			} 
			log.info(methodName + " is done.");
		}
	}
	
	
	private Connection getDSPDBConnection()
	{
		try {
			Connection conn = DriverManager.getConnection(this.JDBC_DSP_DBURI, this.dspDBAccountUserID, this.dspDBAccountPsswd);
			return conn;
		} catch (Exception e) {
			log.error("Failed to connect with DSP uri: " + JDBC_DSP_DBURI + " as user: " + dspDBAccountUserID, e);
			return null;
		}
	}

	private void loadProperties() throws Exception {
		Connection con;
		Statement st;
		ResultSet rs;
		String queryString;

		con = null;
		st = null;
		rs = null;
		queryString = "SELECT x.xml_metadata FROM cen_properties x WHERE x.site_code = 'GBL'";
		try {
			log.debug(queryString);
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();
			st = con.createStatement();
			rs = st.executeQuery(queryString);
			if (rs.next()) {
				String xmlMetadata = rs.getString("xml_metadata");
				
				this.JDBC_DSP_DBURI = CeNXMLParser.getXmlProperty(xmlMetadata, "/Notebook_Properties/Services/DSP/JDBC_DBURI");
				this.dspDBAccountUserID = CeNXMLParser.getXmlProperty(xmlMetadata, "/Notebook_Properties/Services/DSP/DBUserID");
				this.dspDBAccountPsswd = CeNXMLParser.getXmlProperty(xmlMetadata, "/Notebook_Properties/Services/DSP/DBPassword");
			}
		} catch (Exception e) {
			log.error("Failed while loading Design Service properties from CEN_PROPERTIES table.", e);	

			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e1) {
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e2) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e3) {
			}
			throw new PropertyException("Failed to load the Design Service Properties from CeN_Properties table", e);
		}
	}
}
