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

package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.common.DAO;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.reagent.util.ZIPUtil;
import com.chemistry.enotebook.reagent.valueobject.UserInfoVO;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

import java.io.Serializable;
import java.sql.*;

public class MyReagentsDao implements DAO, Serializable {

	private static final long serialVersionUID = 1367616985247159787L;

	public MyReagentsDao() {
	}

	public String getMyReagentList(String UserID) throws ReagentMgmtException {
		Connection con = null;
		Statement stmt = null;
		String myReagentList = MyReagentsDao.NULL_STRING;
		ResultSet rset = null;

		try {
			String queryString = "SELECT MY_REAGENTS FROM CEN_USERS WHERE USERNAME = '" + UserID + "'";

			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();

			stmt = con.createStatement();
			rset = stmt.executeQuery(queryString);
			
			if (rset.next()) {
				myReagentList = rset.getString("MY_REAGENTS");
			}
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (SQLException e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}

		if (myReagentList.equals(MyReagentsDao.NULL_STRING)) {
			StringBuffer sb = new StringBuffer();
			sb.append("<Reagents/>");
			myReagentList = sb.toString();
		}

		return myReagentList;
	}

	/**
	 * 
	 * @param UserInfo
	 * @param MyReagentList
	 * 
	 */
	public void UpdateMyReagentList(UserInfoVO myInfo, String MyReagentList)
			throws ReagentMgmtException {
		Connection con = null;
		PreparedStatement pst = null;
		Statement st = null;
		boolean isUserExist = false;
		ResultSet rs = null;

		try {
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();

			String queryString = "SELECT USERNAME FROM CEN_USERS WHERE USERNAME = '"
					+ myInfo.getUserName() + "'";
			st = con.createStatement();
			rs = st.executeQuery(queryString);
			if (!rs.next()) { // the specified user does not exist, do insert
				System.out.println("...Inserting my reagents...");
				queryString = "INSERT INTO CEN_USERS (USERNAME,FULLNAME,SITE_CODE,STATUS,MY_REAGENTS) VALUES(?,?,?,?,XMLType(?))";
				isUserExist = false;
			} else {// do update
				queryString = "UPDATE CEN_USERS SET MY_REAGENTS = XMLType(?) WHERE USERNAME = ?";
				isUserExist = true;
			}

			// xmlData is the string that contains the XML Data.
			// Get the CLOB object using the getCLOB method.

			pst = con.prepareStatement(queryString);
			if (isUserExist) {
				// Bind this CLOB with the prepared Statement
				pst.setString(1, MyReagentList);
				pst.setString(2, myInfo.getUserName());
			} else {
				// Bind this CLOB with the prepared Statement
				pst.setString(1, myInfo.getUserName());
				pst.setString(2, myInfo.getFullName());
				pst.setString(3, myInfo.getSiteCode());
				pst.setString(4, myInfo.getStatus());
				pst.setString(5, MyReagentList);
			}

			pst.executeUpdate();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if (pst != null)
					pst.close();
			} catch (SQLException e) {
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * 
	 * @param String
	 * @param MyReagentList
	 * 
	 */
	public void UpdateMyReagentList(String userName, byte[] myReagentList)
			throws ReagentMgmtException {
		Connection con = null;
		PreparedStatement pst = null;
		Statement st = null;
		boolean isUserExist = false;
		ResultSet rs = null;

		try {
			String MyReagentList = new String(ZIPUtil.unZip(myReagentList));
			// System.out.println("The passed in my reagent list xml is: " +
			// MyReagentList);

			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();

			String queryString = "";
			// first check if the user exist
			queryString = "SELECT USERNAME FROM CEN_USERS WHERE USERNAME = '"
					+ userName + "'";
			st = con.createStatement();
			rs = st.executeQuery(queryString);
			if (!rs.next()) {// the specified user does not exist, do insert
				queryString = "INSERT INTO CEN_USERS (USERNAME, MY_REAGENTS) VALUES(?,?)";
				isUserExist = false;
			} else {// do update
				queryString = "UPDATE CEN_USERS SET MY_REAGENTS = ? WHERE USERNAME = ?";
				isUserExist = true;
			}

			// xmlData is the string that contains the XML Data.
			// Get the CLOB object using the getCLOB method.
			// myReagentListClob = getCLOB(MyReagentList, vendorConn);

			pst = con.prepareStatement(queryString);
			if (isUserExist) {
				// Bind this CLOB with the prepared Statement
				pst.setString(1, MyReagentList);
				pst.setString(2, userName);
			} else {
				// Bind this CLOB with the prepared Statement
				pst.setString(1, userName);
				pst.setString(2, MyReagentList);
			}

			pst.executeUpdate();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
			}
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
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
