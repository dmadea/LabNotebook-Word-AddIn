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
 * Created on May 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage.ejb;

import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.ValidationInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class ValidationDAO {
	private static final Log log = LogFactory.getLog(ValidationDAO.class);

	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment) throws DAOException {
		return validateNotebook(siteCode, notebook, experiment, null);
	}
	
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment, String pageVersion) throws DAOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String tableClause = null;
		StringBuffer whereClause = new StringBuffer();
		StringBuffer selectClause = new StringBuffer();
		String queryString = "";
		ValidationInfo result = null;

		tableClause = "cen_notebooks n";

		// Validate inputs
		if ((siteCode == null) || (siteCode.length() == 0)) {
			siteCode = null;
		}
		if ((notebook == null) || (notebook.length() == 0)) {
			notebook = null;
		}
		if ((experiment == null) || (experiment.length() == 0)) {
			experiment = null;
		}
		if ((siteCode != null) && (notebook == null)) {
			throw new DAOException("Invalid Parameters to validateNotebook");
		}
		if ((notebook == null) && (experiment != null)) {
			throw new DAOException("Invalid Parameters to validateNotebook");
		}

		// Build the Where/Select Clause
		selectClause.append("site_code, notebook, username");
		//if (siteCode != null) {
		//	whereClause.append("site_code = '" + siteCode + "'");
		//}

		if (notebook != null) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("notebook = '" + notebook + "'");
		}

		if (experiment == null) {
			selectClause.append(", status");

			// Create query for database
			queryString = "SELECT " + selectClause + " FROM " + tableClause + " WHERE " + whereClause;
            log.debug(queryString);
			try {
				// Retrieve the Connection to the database from the Database pool
				conn = this.getConnection();

				stmt = conn.createStatement();

				// Execute the query
				rs = stmt.executeQuery(queryString);
				if (rs != null) {
					if (rs.next()) {
						result = new ValidationInfo();
						if (rs.getString(1) != null) {
							result.siteCode = rs.getString(1);
						}
						if (rs.getString(2) != null) {
							result.notebook = rs.getString(2);
						}
						if (rs.getString(3) != null) {
							result.creator = rs.getString(3);
						}
						if (rs.getString(4) != null) result.status = rs.getString(4);
						if (rs.next()) {
							result.multipleResultsFlag = true;
						}
					}
				}
			} catch (SQLException se) {
				// System.out.println("SQL Exception Occurred in validateNotebook.\nQuery = " + queryString);
				throw new DAOException("ValidationDAO:validateNotebook:: SQL Exception Occured - Failed to Retrieve SpeedBar Data",
						se);
			} catch (Exception e) {
				throw new DAOException("ValidationDAO:validateNotebook:: Failed to Retrieve SpeedBar Data", e);
			} finally {
				this.cleanUp(conn, stmt, rs);
			}
		} else {
			tableClause = "cen_pages p";

			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("experiment = '" + experiment + "'");

			selectClause.append(", experiment, page_status, page_version, latest_version ");
			
			if (pageVersion != null) {				
				whereClause.append(" AND page_version = '" + pageVersion + "'");
			}

			// Create query for database
			queryString = "SELECT " + selectClause + " FROM " + tableClause + " WHERE " + whereClause + " order by page_version desc ";
			log.debug(queryString);
			try {
				// Retrieve the Connection to the database from the Database pool
				conn = this.getConnection();

				stmt = conn.createStatement();

				// Execute the query
				rs = stmt.executeQuery(queryString);
				if (rs != null) {
					if (rs.next()) {
						result = new ValidationInfo();
						if (rs.getString(1) != null) {
							result.siteCode = rs.getString(1);
						}
						if (rs.getString(2) != null) {
							result.notebook = rs.getString(2);
						}
						if (rs.getString(3) != null) {
							result.creator = rs.getString(3);
						}

						if (rs.getString(4) != null) {
							result.experiment = rs.getString(4);
						}
						if (rs.getString(5) != null) {
							result.status = rs.getString(5);
						}
						
						if (rs.getString(6) != null) {
							result.pageVersion = rs.getString(6);
						}
						
						if (rs.getString(7) != null) {
							result.latestVersion = "Y".equalsIgnoreCase(rs.getString(7));
						}

						if (rs.next()) {
							result.multipleResultsFlag = true;
						}
					}
				}
			} catch (SQLException se) {
				// System.out.println("SQL Exception Occured in validateNotebook.\nQuery = " + queryString);
				throw new DAOException("ValidationDAO:validateNotebook:: SQL Exception Occured - Failed to Retrieve SpeedBar Data",
						se);
			} catch (Exception e) {
				throw new DAOException("ValidationDAO:validateNotebook:: Failed to Retrieve SpeedBar Data", e);
			} finally {
				this.cleanUp(conn, stmt, rs);
			}
		}

		return result;
	}

	public Connection getConnection() throws NamingException, SQLException, PropertyException {
		DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI));
		return (ds != null) ? ds.getConnection() : null;
	}

	private void cleanUp(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
		}
	}
}
