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

import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.storage.ContentsContext;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.StorageVO;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class ContentsContextDAO {
	public boolean executeQuery(ContentsContext ctx) throws DAOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean status = false;
		StorageVO RowSet = null;

		String ColumnsOfInterest = "p.notebook, p.experiment, p.page_version, p.site_code, p.username, p.page_status, "
				+ " p.project_code, "
				+ " p.ta_code, "
				+ " TO_CHAR(p.creation_date, 'Mon DD, YYYY HH24:MI:SS TZD') creation_date, "
				+ " p.subject, "
				+ " p.literature_ref, "
				+ " p.xml_metadata, "
				+ " r.sketch_image, coalesce(r.rxn_sketch, r.native_rxn_sketch) as rxn_sketch, "
				+ " look_n_feel, u.fullname, r.rxn_scheme_key ";

		String TablesOfInterest = "cen_reaction_schemes r RIGHT OUTER JOIN cen_pages p ON r.page_key = p.page_key, cen_users u ";
		StringBuffer WhereClause = new StringBuffer();

		if ((ctx.getSiteCode() != null) && (ctx.getSiteCode().length() > 0)) {
			if (WhereClause.length() > 0) {
				WhereClause.append(" AND ");
			}
			WhereClause.append("p.site_code = '" + ctx.getSiteCode() + "'");
		}

		if ((ctx.getNotebook() != null) && (ctx.getNotebook().length() > 0)) {
			if (WhereClause.length() > 0) {
				WhereClause.append(" AND ");
			}
			WhereClause.append("p.notebook = '" + ctx.getNotebook() + "'");
		}

		if ((ctx.getStartExperiment() != -1) && (ctx.getStopExperiment() != -1)) {
			if (WhereClause.length() > 0) {
				WhereClause.append(" AND ");
			}
			
			try {
				NotebookRef startRef = new NotebookRef(ctx.getNotebook(), Integer.toString(ctx.getStartExperiment()));
				NotebookRef stopRef = new NotebookRef(ctx.getNotebook(), Integer.toString(ctx.getStopExperiment()));
				WhereClause.append("p.experiment >= '" + startRef.getNbPage() + "' AND ");
				WhereClause.append("p.experiment <= '" + stopRef.getNbPage() + "'");
			} catch (InvalidNotebookRefException e) {
				WhereClause.append("p.experiment >= " + ctx.getStartExperiment() + " AND ");
				WhereClause.append("p.experiment <= " + ctx.getStopExperiment());
			}
		}

		if (WhereClause.length() > 0) {
			WhereClause.append(" AND ");
		}
		WhereClause.append("p.latest_version = 'Y'");

		if (WhereClause.length() > 0) {
			WhereClause.append(" AND ");
		}
		WhereClause.append(" coalesce(r.reaction_type, 'INTENDED') = 'INTENDED' and u.username = p.username ");

		try {
			// Retrieve the Connection to the database from the Database pool
			conn = this.getConnection();

			// Create query for database
			String queryString = "SELECT " + ColumnsOfInterest + " FROM " + TablesOfInterest + " WHERE " + WhereClause.toString()
					+ " ORDER BY p.notebook, p.experiment";

			// Execute the query
			stmt = conn.createStatement();
			rs = stmt.executeQuery(queryString);
			
			List<StorageVO> list = new ArrayList<StorageVO>();
			
			while (rs.next()) {
				RowSet = new StorageVO();
				
				RowSet.setString("XML_METADATA", rs.getString("XML_METADATA"));
				
				RowSet.setString("NOTEBOOK", rs.getString("NOTEBOOK"));
				RowSet.setString("EXPERIMENT", rs.getString("EXPERIMENT"));
				RowSet.setString("PAGE_VERSION", rs.getString("PAGE_VERSION"));
				
				RowSet.setString("PAGE_STATUS", rs.getString("PAGE_STATUS"));
				RowSet.setString("LOOK_N_FEEL", rs.getString("LOOK_N_FEEL"));
				
				RowSet.setString("SITE_CODE", rs.getString("SITE_CODE"));
				RowSet.setString("USERNAME", rs.getString("USERNAME"));
				RowSet.setString("FULLNAME", rs.getString("FULLNAME"));
				
				RowSet.setString("PROJECT_CODE", rs.getString("PROJECT_CODE"));
				RowSet.setString("TA_CODE", rs.getString("TA_CODE"));
				
				RowSet.setString("CREATION_DATE", rs.getString("CREATION_DATE"));
				
				RowSet.setString("SUBJECT", rs.getString("SUBJECT"));
				RowSet.setString("LITERATURE_REF", rs.getString("LITERATURE_REF"));
				
				RowSet.setString("RXN_SCHEME_KEY", rs.getString("RXN_SCHEME_KEY"));
				
				RowSet.setBytes("RXN_SKETCH", rs.getBytes("RXN_SKETCH"));
				RowSet.setBytes("SKETCH_IMAGE", rs.getBytes("SKETCH_IMAGE"));
				
				list.add(RowSet);
			}
			
			ctx.setResults(list.toArray(new StorageVO[0]));

			status = true;
		} catch (SQLException se) {
			throw new DAOException(
					"ContentsContextDAO:executeQuery:: SQL Exception Occured - Failed to Retrieve Notebook Contents Data", se);
		} catch (Throwable e) {
			throw new DAOException("ContentsContextDAO:executeQuery:: Failed to Retrieve Notebook Contents Data", e);
		} finally {
			this.cleanUp(conn, stmt, rs);
		}

		return status;
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
