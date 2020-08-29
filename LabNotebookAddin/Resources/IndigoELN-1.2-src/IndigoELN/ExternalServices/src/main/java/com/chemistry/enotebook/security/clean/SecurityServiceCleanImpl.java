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
package com.chemistry.enotebook.security.clean;

import com.chemistry.enotebook.security.SecurityService;
import com.chemistry.enotebook.security.classes.PersonTO;
import com.chemistry.enotebook.security.exceptions.SecurityServiceException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecurityServiceCleanImpl implements SecurityService {
	
	private static final Log LOG = LogFactory.getLog(SecurityServiceCleanImpl.class);

	private static final String TABLE_NAME = "cen_users";
	
	public boolean authenticate(String username, String password) throws SecurityServiceException {
		ResultSet rs = null;
		PreparedStatement st = null;
		Connection conn = getConnection();
		
		String sql = "select u.password from " + TABLE_NAME + " u where u.username='" + username + "'";
		
		try {
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			if (rs.next())
				if (StringUtils.equals(getMd5Hash(password), rs.getString("PASSWORD")))
					return true;
		} catch (SQLException e) {
			LOG.error("Error getting user from DB: ", e);
			throw new SecurityServiceException(e);
		} finally {
			try { rs.close(); } catch (Exception e) { }
			try { st.close(); } catch (Exception e) { }
			try { conn.close(); } catch (Exception e) { }
		}
		
		return false;
	}

	public PersonTO getPerson(String username) throws SecurityServiceException {
		PersonTO user = null;
		ResultSet rs = null;
		PreparedStatement st = null;
		Connection conn = getConnection();
		
		String sql = "select * from " + TABLE_NAME + " u where u.username='" + username + "'";
		
		try {
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			if (rs.next()) {
				user = new PersonTO();
				user.setUserName(rs.getString("USERNAME"));
				user.setNtDomain(rs.getString("SITE_CODE"));
			
				String fullName = rs.getString("FULLNAME");
				int delimIndex = fullName.indexOf(',');
				
				if (delimIndex == -1) {
					user.setFirstName("");
					user.setLastName(fullName);
				} else {				
					user.setFirstName(fullName.substring(delimIndex + 1, fullName.length()).trim());
					user.setLastName(fullName.substring(0, delimIndex).trim());
				}
				
				user.setEmployeeStatus(rs.getString("STATUS"));
				user.setSmtpAddress(rs.getString("EMAIL"));
			}
		} catch (SQLException e) {
			LOG.error("Error getting user from DB: ", e);
			throw new SecurityServiceException(e);
		} finally {
			try { rs.close(); } catch (Exception e) { }
			try { st.close(); } catch (Exception e) { }
			try { conn.close(); } catch (Exception e) { }
		}
		
		return user;
	}
	
	private Connection getConnection() throws SecurityServiceException {
		Connection conn;
		
		try {			
			DataSource ds = (DataSource) new ClassPathXmlApplicationContext("database/xml/eln-datasource-context.xml").getBean("dataSource");
			if (ds != null)
				conn = ds.getConnection();
			else
				throw new Exception("Error getting connection to DB.");
		} catch (Exception e) {
			LOG.error("Error getting connection to DB: ", e);
			throw new SecurityServiceException(e);
		}
		
		return conn;
	}
	
	private String getMd5Hash(String s) {
		return DigestUtils.md5Hex(s);
	}
}
