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

package com.chemistry.enotebook.session.ejb;

import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;
import com.chemistry.enotebook.session.security.UserData;
import com.common.chemistry.codetable.delegate.CodeTableDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class UserDAO {
	public static StringBuffer s_SelectSql = new StringBuffer(
			"select u.username ntuser, u.fullname fullname, u.site_code sitecode, u.status status, u.xml_metadata xmlmetadata ")
			.append(" from cen_users u  where u.username = ?");

	public static StringBuffer s_UpdateSql = new StringBuffer(
			"update cen_users set xml_metadata = ?, ").append(
			"audit_log = ?  where ").append("username = ?");

	public static StringBuffer s_InsertSql = new StringBuffer(
			"insert into cen_users(username, xml_metadata, audit_log, fullname, ")
			.append("site_code, status)  values(?, ?, ?, ?, ?, ?) ");
	public static StringBuffer s_SelectAuditLogSql = new StringBuffer(
			"select u.audit_log  from cen_users u  where u.username = ?");

	public static StringBuffer s_SelectExtSql = new StringBuffer(
			"select u.vendor_password from cen_users u where upper(u.vendor_email) = ?");

	public static String s_DataSourceName = ServiceLocator.CEN_DS_JNDI;
	public static String s_CompoundManagementDataSourceName = "COMPOUND_MANAGEMENT_DS_JNDI";

	private static final Log log = LogFactory.getLog(UserDAO.class);

	public UserData getData(String username) throws UserDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		UserData ud = null;

		try {
			conn = getCeNConnection();

			stmt = conn.prepareStatement(s_SelectSql.toString());
			stmt.setString(1, username.toUpperCase());

			rs = stmt.executeQuery();
			log.debug(s_SelectSql.toString());

			while (rs.next()) {
				boolean validUser = rs.getString("status").equalsIgnoreCase("VALID");
				String s_xmlmetadata = rs.getString("xmlmetadata");
				ud = new UserData(rs.getString("ntuser"), rs.getString("fullname"), rs.getString("sitecode"), validUser, s_xmlmetadata, "", "");
			}
		} catch (Exception e) {
			throw new UserDAOException(
					"UserDAO:setData:: Failed to Retrieve User Data for "
							+ username, e);
		} finally {
			cleanUp(conn, stmt, rs);
		}

		return ud;
	}

	/**
	 * @return Connection
	 * @throws NamingException
	 * @throws SQLException
	 */
	public Connection getCeNConnection() throws NamingException, SQLException,
			PropertyException {
		DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(s_DataSourceName));
		return ds.getConnection();
	}

	/**
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public void cleanUp(Connection conn, Statement stmt, ResultSet rs) {
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

	/**
	 * @param ud
	 * @throws UserDAOException
	 */
	public void setData(UserData ud) throws UserDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getCeNConnection();
			stmt = conn.prepareStatement(s_UpdateSql.toString());

			stmt.setString(1, ud.getXmlMetaData());
			// String reasons = ud.getAuditLogReason();
			String currentAudit = getAuditLog(conn, ud.getUserName());
			stmt.setObject(2, currentAudit);
			stmt.setString(3, ud.getUserName().toUpperCase());
			stmt.execute();
		} catch (Exception e) {
			throw new UserDAOException(
					"UserDAO:setData:: Failed to Update User Data", e);
		} finally {
			cleanUp(conn, stmt, null);
		}
	}

	public UserData createUser(String ntUser, String siteCode, String fullName,
			String status, String compoundManagementEmployeeId)
			throws UserDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		UserData ud = null;

		String s_xmlMetaData = null;
		String s_AuditLog = null;

		try {
			conn = getCeNConnection();
			// met2580481
			s_xmlMetaData = getDefaultUserProperties(conn, siteCode);
			if (s_xmlMetaData == null || s_xmlMetaData.trim().length() == 0)
				s_xmlMetaData = getDefaultUserProperties(conn, "GBL");

			if (s_xmlMetaData == null || s_xmlMetaData.trim().length() == 0)
				s_xmlMetaData = "<User_Properties><EmployeeID>"
						+ compoundManagementEmployeeId
						+ "</EmployeeID></User_Properties>";
			else
				s_xmlMetaData = s_xmlMetaData.replaceFirst(
						"</User_Properties>", "<EmployeeID>"
								+ compoundManagementEmployeeId
								+ "</EmployeeID></User_Properties>");

			s_AuditLog = "<Audit_Log><Log><Entry><Reason>Record Created</Reason><Timestamp>"
					+ (new SimpleDateFormat("MMM d, yyyy HH:mm:ss z"))
							.format(new Date())
					+ "</Timestamp><Username>"
					+ ntUser + "</Username></Entry></Log></Audit_Log>";

			stmt = conn.prepareStatement(s_InsertSql.toString());

			stmt.setString(1, ntUser.toUpperCase());
			stmt.setString(2, s_xmlMetaData);
			stmt.setString(3, s_AuditLog);
			// stmt.setString(4, s_MyReagents);
			stmt.setString(4, fullName);
			stmt.setString(5, siteCode);
			stmt.setString(6, status);

			stmt.execute();

			ud = new UserData(ntUser, fullName, siteCode,
					status.equalsIgnoreCase("VALID"), s_xmlMetaData, "", "");// s_MyReagents);
			return ud;
		} catch (Exception e) {
			throw new UserDAOException(
					"UserDAO:createUser:: Failed to Create the User "
							+ e.getMessage(), e);
		} finally {
			cleanUp(conn, stmt, rs);
		}
	}

	private String getDefaultUserProperties(Connection conn, String siteCode)
			throws SQLException {
		String result = null;
		String selectPropertySql = "select p.xml_metadata xmlmetadatastr"
				+ "  from cen_properties p"
				+ "  where p.site_code='"
				+ siteCode + "'";
		Statement st = null;
		ResultSet rs = null;

		try {
			st = conn.createStatement();
			rs = st.executeQuery(selectPropertySql);
			
			if (rs.next()) {
				result = rs.getString("xmlmetadatastr");
				if (result != null) {
					int startPos = result.indexOf("<User_Properties");
					if (startPos >= 0)
						result = result.substring(startPos);
					else
						result = "";
					int endPos = result.indexOf("</User_Properties>");
					if (endPos >= 0)
						result = result.substring(0, endPos + "</User_Properties>".length());
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			cleanUp(null, st, rs);
		}

		return result;
	}

	public boolean doesExistInCeNUsers(String ntUser) throws UserDAOException {
		String userStatus = "";

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			String s_Sql = "select status from cen_users where username = '"
					+ ntUser.toUpperCase() + "'";

			conn = getCeNConnection();
			st = conn.createStatement();
			rs = st.executeQuery(s_Sql);

			// If there is a record then get the user Status
			if (rs.next())
				userStatus = rs.getString(1).toUpperCase();
		} catch (Exception e) {
			throw new UserDAOException(
					"UserDAO:doesExistInCeNUsers:: Failed to retrieve User Data",
					e);
		} finally {
			cleanUp(conn, st, rs);
		}

		if (userStatus.equals("")) // If we are here there is no exception, if
									// "" then record not found
			return false;
		// else if (!userStatus.equals("VALID")) // Record found, but user
		// marked invalid
		// throw new
		// UserDAOException("UserDAO:doesExistInCeNUsers:: User exists but has been denied access, Status = '"
		// + userStatus + "'.");
		else
			// Record exists and status is VALID
			return true;
	}

	public String getNtUserNameByCompoundManagementEmployeeId(String compoundManagementEmployeeId) throws UserDAOException {
		List<Properties> empl;
		
		try {
			empl = new CodeTableDelegate().getCodeTableValues("COMPOUND_MANAGEMENT_EMPLOYEE");
		} catch (Exception e) {
			throw new UserDAOException(e);
		}
		
		for (Properties employeeObj : empl) {
			Properties employee = employeeObj;
			if (employee.getProperty("EMPLOYEE_ID").equals(compoundManagementEmployeeId)) {
				return employee.getProperty("NT_NAME");
			}
		}
		return null;
		/*
		 * String result = null;
		 * 
		 * Connection conn = null; Statement st = null; ResultSet rs = null;
		 * 
		 * try { String sql =
		 * "select e.nt_name from compound_management_employee e where e.employee_id = '"
		 * + compoundManagementEmployeeId.toUpperCase() + "'";
		 * 
		 * conn = getCompoundManagementConnection(); st =
		 * conn.createStatement(); rs = st.executeQuery(sql);
		 * 
		 * rs.next();
		 * 
		 * result = rs.getString("NT_NAME"); } catch (Exception e) { throw new
		 * UserDAOException(e); } finally { cleanUp(conn, st, rs); }
		 * 
		 * return result;
		 */}

	public CompoundManagementEmployee getCompoundManagementEmployee(String ntUser) throws UserDAOException {
		UserData data = getData(ntUser);
		String xmlMetadata = data.getXmlMetaData();
		
		String employeeIdStart = "<EmployeeID>";
		String employeeIdEnd = "</EmployeeID>";
		
		String employeeId = "";
		
		int idx = xmlMetadata.indexOf(employeeIdStart);
		if (idx > 0)
			employeeId = xmlMetadata.substring(idx + employeeIdStart.length(), xmlMetadata.indexOf(employeeIdEnd));
		
		CompoundManagementEmployee employee = new CompoundManagementEmployee();
		employee.setEmployeeId(employeeId);
		employee.setFullName(data.getFullUserName());
		employee.setSiteCode(data.getSiteCode());
		employee.setStatus(data.isValidUser() ? "VALID" : "INVALID");
		
		return employee;
	}

	/*
	 * Builds the audit Log, an <Entry> node is inserted . more than one reason
	 * is separated by a "~" refer to setAuditLog of UserData
	 */
	String buildAuditLog(String reasons, String username, String currentAuditLog)
			throws SQLException {
		if (reasons == null)
			reasons = "Not Specified";
		StringBuffer sbNew = new StringBuffer();
		String currentTimestamp = (new SimpleDateFormat(
				"MMM d, yyyy HH:mm:ss z")).format(new Date());

		sbNew.append("<Entry>");
		if (reasons != null && !reasons.equals(""))
			sbNew.append("<Reason>").append(reasons).append("</Reason>");
		sbNew.append("<Timestamp>").append(currentTimestamp)
				.append("</Timestamp>").append("<Username>").append(username)
				.append("</Username></Entry>");
		if (currentAuditLog == null || currentAuditLog.indexOf("<Entry>") < 0) {
			return "<Audit_Log><Log>" + sbNew.toString() + "</Log></Audit_Log>";
		}
		int locateInsertInd = currentAuditLog.indexOf("<Entry>");
		String temp1 = currentAuditLog.substring(0, locateInsertInd); // new log
																		// insert
																		// before
		String temp2 = sbNew.toString(); // new log
		String temp3 = currentAuditLog.substring(locateInsertInd);// new log
																	// insert
																	// after

		return temp1 + temp2 + temp3;
	}

	/**
	 * This method returns the existing audit log as a String
	 * 
	 * @param con
	 * @param username
	 * @return
	 * @throws UserDAOException
	 */
	public String getAuditLog(Connection con, String username)
			throws UserDAOException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String s_auditLog = null;

		try {
			stmt = con.prepareStatement(s_SelectAuditLogSql.toString());
			stmt.setString(1, username.toUpperCase());

			rs = stmt.executeQuery();

			if (rs.next()) {
				s_auditLog = rs.getString("audit_log");
			}
		} catch (Exception e) {
			throw new UserDAOException(
					"UserDAO:setData:: Failed to Retrieve User Audit Data", e);
		} finally {
			cleanUp(null, stmt, rs);
		}

		return s_auditLog;
	}
}
