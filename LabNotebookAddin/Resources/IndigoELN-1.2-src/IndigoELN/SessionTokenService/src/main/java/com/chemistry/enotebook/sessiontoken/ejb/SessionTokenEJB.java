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
package com.chemistry.enotebook.sessiontoken.ejb;

import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.sessiontoken.delegate.SessionTokenAccessException;
import com.chemistry.enotebook.sessiontoken.interfaces.SessionTokenRemote;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class SessionTokenEJB implements SessionTokenRemote {

	private static final Log log = LogFactory.getLog(SessionTokenEJB.class);

    private static final String INSERT_SQL = "insert into CEN_USER_SESSIONS (SESSION_TOKEN, USERNAME, LEASE_START_TIME) values (?, ?, ?)";
    private static final String DELETE_SQL = "delete from CEN_USER_SESSIONS where SESSION_TOKEN = ?";
    private static final String SELECT_BY_SESSION_TOKEN_SQL = "select SESSION_TOKEN, USERNAME, LEASE_START_TIME from CEN_USER_SESSIONS where SESSION_TOKEN = ?";

    /*static field*/
    public static String s_DataSourceName = ServiceLocator.CEN_DS_JNDI;

	@Override
	public void createSessionInfo(String sessionToken, String uid) throws SessionTokenAccessException {
        log.debug("createSessionInfo() called for (" + sessionToken + ") " + uid.toUpperCase());
    	
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = getCeNConnection();
            
            pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setString(1, sessionToken);
            pstmt.setString(2, uid);
            pstmt.setDate(3, new Date(System.currentTimeMillis()));
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("", e);
            throw new SessionTokenAccessException("", e);
        } finally {
            cleanUp(conn, pstmt, null);
            log.debug("createSessionInfo() end");
        }
	}

	@Override
	public boolean findSessionInfo(String sessionToken) throws SessionTokenAccessException {
		log.debug("findSessionInfo(" + sessionToken + ") called on " + hashCode());
		
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rs = null;
        
        try {
            conn = getCeNConnection();
            
            pstmt = conn.prepareStatement(SELECT_BY_SESSION_TOKEN_SQL);
            pstmt.setString(1, sessionToken);
            
            rs = pstmt.executeQuery();
            rs.next();
            
            sessionToken = rs.getString("SESSION_TOKEN");
            if (sessionToken != null && sessionToken.trim().length() > 0) {
            	return true;
            } else {
            	return false;
            }
        } catch (Exception e) {
            log.error("findSessionInfo(" + sessionToken + ") failed", e);
            throw new SessionTokenAccessException("", e);
        } finally {
            cleanUp(conn, pstmt, rs);
            log.debug("findSessionInfo() end");
        }
	}

	@Override
	public void deleteSessionInfo(String sessionToken) throws SessionTokenAccessException {    	
        log.debug("deleteSessionInfo(" + sessionToken + ") called on " + hashCode());
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        
        try {
            conn = getCeNConnection();
            pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setString(1, sessionToken);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("", e);
            throw new SessionTokenAccessException("", e);
        } finally {
            cleanUp(conn, pstmt, null);
            log.debug("deleteSessionInfo() end");
        }
	}
	
	private Connection getCeNConnection() throws NamingException, SQLException, PropertyException {
        DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(s_DataSourceName));
        return ds.getConnection();
    }

    private void cleanUp(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception e) { }
        try { if (stmt != null) stmt.close(); } catch (Exception e) { }
        try { if (conn != null) conn.close(); } catch (Exception e) { }
    }
}
