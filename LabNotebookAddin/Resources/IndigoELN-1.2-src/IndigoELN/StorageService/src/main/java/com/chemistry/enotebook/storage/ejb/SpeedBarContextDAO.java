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
import com.chemistry.enotebook.storage.SpeedBarContext;
import com.chemistry.enotebook.storage.StorageVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarContextDAO {
	private static final Log log = LogFactory.getLog(SpeedBarContextDAO.class);
	
	public boolean executeQuery(SpeedBarContext ctx) throws DAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean status = false;
		StorageVO RowSet = null;
		String ColumnsOfInterest = null;
		String tableClause = null;
		String whereClause = null;
		StringBuffer groupClause = new StringBuffer("");
		StringBuffer orderClause = new StringBuffer("");
		String DefaultGroupBy = null;
		String DefaultOrderBy = null;
		String queryString = "";

		try {
			// Determine criterea for page selection as well as which columns and default sort
			if ((ctx.getSiteCode() != null) && (ctx.getUserName() == null) && (ctx.getNotebook() == null)
					&& (ctx.getExperiment() == null)) { // GET USER INFO
				RowSet = new StorageVO();

				ColumnsOfInterest = "distinct u.username chemist_userid, " +
									"u.fullname chemist_name";

				if (ctx.includeAllUsers())
					tableClause = "cen_users u LEFT OUTER JOIN cen_notebooks n ON u.site_code = n.site_code and u.username = n.username";
				else
					tableClause = "cen_users u, cen_notebooks n";
				
				if (ctx.includeAllUsers())
					whereClause = "u.site_code = ? ";
				else
					whereClause = "u.site_code = ? and u.site_code = n.site_code and u.username = n.username";
					

				DefaultOrderBy = "chemist_name";
			} else if ((ctx.getSiteCode() != null) && (ctx.getUserName() != null) && (ctx.getNotebook() == null)
					&& (ctx.getExperiment() == null)) { // GET NOTEBOOK INFO
				RowSet = new StorageVO();
				tableClause = "cen_notebooks n LEFT OUTER JOIN cen_pages p ON n.site_code = p.site_code and n.notebook = p.notebook ";

				ColumnsOfInterest = "n.notebook, n.status, MIN(p.experiment), MAX(p.experiment)";

				whereClause = "n.site_code = ? AND n.username = ? AND "
						+ "NOT n.status IN ('ALLOCATED', 'OFFLINE')";

				DefaultGroupBy = "n.notebook, n.status";
				DefaultOrderBy = "n.notebook DESC";
			} else if ((ctx.getSiteCode() != null) && (ctx.getUserName() != null) && (ctx.getNotebook() != null)) { // GET
																													// EXPERIMENT
																													// INFO
				RowSet = new StorageVO();
				tableClause = "cen_pages p";
				
				ColumnsOfInterest = "p.experiment, " + "p.page_version, p.latest_version, " + "p.look_n_feel, " + "p.page_status, "
						+ "to_char(p.creation_date, 'Mon DD, YYYY HH24:MI:SS TZH') creation_date, "
						+ "p.subject, "
						+ "p.project_code, "
						+ "(SELECT coalesce(r.native_rxn_sketch, r.rxn_sketch) " + "  FROM cen_reaction_schemes r " + "   WHERE p.page_key = r.page_key AND "
						+ "         r.reaction_type  = 'INTENDED') sketch_image, "
						+ "to_char(p.modified_date, 'Mon DD, YYYY HH24:MI:SS TZH') modified_date ";

				whereClause = "p.site_code = ? AND p.notebook = ? "
						+ ((ctx.getExperiment() != null) ? " AND p.experiment = ? " : "")
						+ ((ctx.getExperimentRangeStart() != null) ? " AND p.experiment >= ? " : "")
						+ ((ctx.getExperimentRangeEnd() != null) ? " AND p.experiment <= ? " : "");

				if ((ctx.getExperiment() != null) && (ctx.getVersion() > 0)) {
					whereClause += " AND p.page_version = ? ";
				} else if (!ctx.includeOlderVersions()) {
					whereClause += " AND p.latest_version = 'Y' ";
				}

				DefaultOrderBy = "p.experiment DESC, p.page_version DESC";
			}

			// Sort based on user request or default sort order
			// int[] so = ctx.getSortOrder();
			// if (so != null)
			// {
			// for (int i=0; i < so.length; i++)
			// {
			// if (so[i] == ctx.SITECODE) {
			// if (orderClause.length() != 0) orderClause.append(", ");
			// orderClause.append("site_code");
			// } else if (so[i] == ctx.USERNAME) {
			// if (orderClause.length() != 0) orderClause.append(", ");
			// orderClause.append("chemist_userid");
			// } else if (so[i] == ctx.NOTEBOOK) {
			// if (orderClause.length() != 0) orderClause.append(", ");
			// orderClause.append("notebook");
			// } else if (so[i] == ctx.PAGE) {
			// if (orderClause.length() != 0) orderClause.append(", ");
			// orderClause.append("page");
			// }
			// }
			// }
			// else
			if (DefaultGroupBy != null) {
				groupClause.append(DefaultGroupBy);
			}
			if (DefaultOrderBy != null) {
				orderClause.append(DefaultOrderBy);
			}

			// Retrieve the Connection to the database from the Database pool
			conn = this.getConnection();

			// Create query for database
			queryString = "SELECT " + ColumnsOfInterest + " FROM " + tableClause
					+ ((whereClause.length() != 0) ? " WHERE " + whereClause.toString() : "")
					+ ((groupClause.length() != 0) ? " GROUP BY " + groupClause.toString() : "")
					+ ((orderClause.length() != 0) ? " ORDER BY " + orderClause.toString() : "");

			// System.out.println("Speedbar query: " + queryString);
			stmt = conn.prepareStatement(queryString);
			log.debug(queryString);
			// Fill in parameters
			int varPos = 1;
			if (ctx.getSiteCode() != null) {
				stmt.setString(varPos++, ctx.getSiteCode());
			}
			if ((ctx.getUserName() != null) && (ctx.getNotebook() == null)) {
				stmt.setString(varPos++, ctx.getUserName());
			}
			if (ctx.getNotebook() != null) {
				stmt.setString(varPos++, ctx.getNotebook());
				if (ctx.getExperiment() != null) {
					stmt.setString(varPos++, ctx.getExperiment());
				}
				if (ctx.getExperimentRangeStart() != null) {
					stmt.setString(varPos++, ctx.getExperimentRangeStart());
				}
				if (ctx.getExperimentRangeEnd() != null) {
					stmt.setString(varPos++, ctx.getExperimentRangeEnd());
				}
				if ((ctx.getExperiment() != null) && (ctx.getVersion() > 0)) {
					stmt.setInt(varPos++, ctx.getVersion());
				}
			}

			// Execute the query
			rs = stmt.executeQuery();
			if (rs != null) {
				RowSet.populate(rs);
			}

			ctx.setResults(RowSet);
			status = true;
		} catch (SQLException se) {
			throw new DAOException("SpeedBarContextDAO:executeQuery:: SQL Exception Occured - Failed to Retrieve SpeedBar Data", se);
		} catch (Exception e) {
			throw new DAOException("SpeedBarContextDAO:executeQuery:: Failed to Retrieve SpeedBar Data", e);
		} finally {
			this.cleanUp(conn, stmt, rs);
		}

		return status;
	}

	public Connection getConnection() throws NamingException, SQLException, PropertyException {
		DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI));
		return (ds != null) ? ds.getConnection() : null;
	}

	public void cleanUp(Connection conn, Statement stmt, ResultSet rs) {
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

	// private static void dumpDataVO(StorageVO vo)
	// {
	// try
	// {
	// vo.afterLast();
	// System.out.println(" Successfully queried Data, " + vo.getRow() + " record(s).");
	// vo.beforeFirst();
	//			
	// ResultSetMetaData md = vo.getMetaData();
	// int ColCount = md.getColumnCount();
	//
	// System.out.print("Column Names: ");
	// for (int i=1; i <= ColCount; i++)
	// System.out.print(md.getColumnName(i) + "(" + md.getColumnTypeName(i) + "), ");
	//				
	// while (vo.next()) {
	// System.out.print(" ");
	// for (int i=1; i <= ColCount; i++) {
	// Object DataObj = vo.getObject(i);
	// if (DataObj != null && DataObj.toString() != null)
	// System.out.print("'" + DataObj.toString() + "' (" + DataObj.toString().length() + ", " + DataObj.getClass().getName() + "),
	// ");
	// else
	// System.out.print("null, ");
	// }
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
