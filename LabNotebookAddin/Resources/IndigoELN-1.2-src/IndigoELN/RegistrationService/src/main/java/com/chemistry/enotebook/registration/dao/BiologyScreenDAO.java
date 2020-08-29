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
 * Created on Dec 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.registration.dao;

import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.registration.ScreenInfoRequest;
import com.chemistry.enotebook.registration.ScreenSearchParams;
import com.chemistry.enotebook.registration.ScreenSearchVO;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BiologyScreenDAO 
{
	private static final Log log = LogFactory.getLog(BiologyScreenDAO.class);
	
	private String interstedColumns = "SELECT DISTINCT COMPOUND_REGISTRATION_SCREEN_PROTOCOL.SCREEN_PROTOCOL_ID, COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME SCREEN_CODE, "
			+" COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE SCREEN_PROTOCOL_TITLE,  COMPOUND_REGISTRATION_SCREEN_PROTOCOL.CONTAINER_TYPE_CODE, "
			+" COMPOUND_REGISTRATION_SCREEN_PROTOCOL.MIN_COMPOUND_REQUIRED, COMPOUND_REGISTRATION_SCREEN_PROTOCOL.AMOUNT_UNIT_CODE,  COMPOUND_REGISTRATION_SCREEN_PROTOCOL.SCIENTIST_CODE, "
			+" COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME, COMPOUND_REGISTRATION_EMPLOYEE.MIDDLE_INIT, COMPOUND_REGISTRATION_EMPLOYEE.FIRST_NAME, COMPOUND_REGISTRATION_EMPLOYEE.SITE_CODE ";
	
	private String fromString = " FROM COMPOUND_REGISTRATION_SCREEN_PROTOCOL, COMPOUND_REGISTRATION_EMPLOYEE ";
	
	private String conditionString = " WHERE COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NT_DOMAIN = COMPOUND_REGISTRATION_EMPLOYEE.NT_DOMAIN AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NT_NAME = COMPOUND_REGISTRATION_EMPLOYEE.NT_NAME ";
	
	
	public ArrayList<ScreenSearchVO> performScreenSearch(ScreenSearchParams params)
		throws RegistrationRuntimeException 
	{
		String sqlQuery = "";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean searchingSiteCode = false;
		
		if( params.getSiteCode() == null || params.getSiteCode().trim().length() == 0 || params.getSiteCode().trim().equals("GBL")){
			searchingSiteCode = false;
		}else{
			conditionString += " AND COMPOUND_REGISTRATION_EMPLOYEE.SITE_CODE = ? ";
			searchingSiteCode = true;
		}
		
		ArrayList<ScreenSearchVO> screenInfoVOList = new ArrayList<ScreenSearchVO>();	
		
		int caseNum = -1;
		if( params.getScreenCode().trim().length() > 0 && params.getScreenProtocol().trim().length() > 0 && params.getScientistName().trim().length() > 0){
			if( params.getScreenCode().trim().indexOf("%") >= 0 && params.getScreenProtocol().trim().indexOf("%") >= 0 && params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE LIKE '" + params.getScreenProtocol().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 0;
			}else if(params.getScreenCode().trim().indexOf("%") >= 0 && params.getScreenProtocol().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE LIKE '" + params.getScreenProtocol().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 1;
			}else if(params.getScreenCode().trim().indexOf("%") >= 0  && params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 2;
			}else if( params.getScreenProtocol().trim().indexOf("%") >= 0 && params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE LIKE '" + params.getScreenProtocol().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 3;
			}else if( params.getScreenCode().trim().indexOf("%") >= 0 ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE =? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 4;
			}else if( params.getScreenProtocol().trim().indexOf("%") >= 0 ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE LIKE '" + params.getScreenProtocol().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 5;
			}else if( params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 6;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 7;
			}
		}else if(params.getScreenCode().trim().length() > 0 && params.getScreenProtocol().trim().length() > 0){
			if( params.getScreenCode().trim().indexOf("%") >= 0 && params.getScreenProtocol().trim().indexOf("%") >= 0 ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE LIKE '" + params.getScreenProtocol().trim()+ "' ";
				caseNum = 0;
			}else if( params.getScreenCode().trim().indexOf("%") >= 0  ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? ";
				caseNum = 2;
			}else if( params.getScreenProtocol().trim().indexOf("%") >= 0 ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE LIKE '" + params.getScreenProtocol().trim()+ "' ";
				caseNum = 3;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? ";
				caseNum = 6;
			}			
		}else if(params.getScreenCode().trim().length() > 0  && params.getScientistName().trim().length() > 0){
			if( params.getScreenCode().trim().indexOf("%") >= 0  && params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 0;
			}else if( params.getScreenCode().trim().indexOf("%") >= 0 ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 1;
			}else if( params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 3;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ?  AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 5;
			}			
		}else if(params.getScreenProtocol().trim().length() > 0 && params.getScientistName().trim().length() > 0){
			if( params.getScreenProtocol().trim().indexOf("%") >= 0  && params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE like '" + params.getScreenProtocol().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 0;
			}else if( params.getScreenProtocol().trim().indexOf("%") >= 0  ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE like '" + params.getScreenProtocol().trim()+ "' AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 1;
			}else if( params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 2;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 4;
			}			
		}else if(params.getScreenCode().trim().length() > 0){
			if( params.getScreenCode().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME LIKE '" + params.getScreenCode().trim()+ "' ";
				caseNum = 0;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.NAME = ? ";
				caseNum = 3;
			}			
		}else if(params.getScreenProtocol().trim().length() > 0){
			if(params.getScreenProtocol().trim().indexOf("%") >= 0 ){
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE like '" + params.getScreenProtocol().trim()+ "' ";
				caseNum = 0;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_SCREEN_PROTOCOL.TITLE = ? ";
				caseNum = 2;
			}			
		}else if(params.getScientistName().trim().length() > 0){
			if( params.getScientistName().trim().indexOf("%") >= 0){
				conditionString += " AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME LIKE '" + params.getScientistName().trim()+ "' ";
				caseNum = 0;
			}else{
				conditionString += " AND COMPOUND_REGISTRATION_EMPLOYEE.LAST_NAME = ? ";
				caseNum = 1;
			}			
		}
		
		sqlQuery = this.interstedColumns + this.fromString + this.conditionString;
	
		try {
			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI("COMPOUND_REGISTRATION_DS_JNDI"))).getConnection();
			pst = con.prepareStatement(sqlQuery);
			
			if(searchingSiteCode){
				pst.setString(1, params.getSiteCode().trim());
			}
		
			switch (caseNum) {
				case 0:
					break;
				case 1:
					if(searchingSiteCode){
						pst.setString(2, params.getScientistName().trim());
					}else{
						pst.setString(1, params.getScientistName().trim());
					}
					break;
				case 2:
					if(searchingSiteCode){
						pst.setString(2, params.getScreenProtocol().trim());
					}else{
						pst.setString(1, params.getScreenProtocol().trim());
					}
					break;
				case 3:
					if(searchingSiteCode){
						pst.setString(2, params.getScreenCode().trim());
					}else{
						pst.setString(1, params.getScreenCode().trim());
					}
					break;
				case 4:
					if(searchingSiteCode){
						pst.setString(2, params.getScreenProtocol().trim());
						pst.setString(3, params.getScientistName().trim());
					}else{
						pst.setString(1, params.getScreenProtocol().trim());
						pst.setString(2, params.getScientistName().trim());
					}
					break;
				case 5:
					if(searchingSiteCode){
						pst.setString(2, params.getScreenCode().trim());
						pst.setString(3, params.getScientistName().trim());
					}else{
						pst.setString(1, params.getScreenCode().trim());
						pst.setString(2, params.getScientistName().trim());
					}
					break;
				case 6:
					if(searchingSiteCode){
						pst.setString(2, params.getScreenCode().trim());
						pst.setString(3, params.getScreenProtocol().trim());
					}else{
						pst.setString(1, params.getScreenCode().trim());
						pst.setString(2, params.getScreenProtocol().trim());
					}
					break;
				case 7:
					if(searchingSiteCode){
						pst.setString(2, params.getScreenCode().trim());
						pst.setString(3, params.getScreenProtocol().trim());
						pst.setString(4, params.getScientistName().trim());
					}else{
						pst.setString(1, params.getScreenCode().trim());
						pst.setString(2, params.getScreenProtocol().trim());
						pst.setString(3, params.getScientistName().trim());
					}
					break;
			}
			
			log.debug("The final query is: " + sqlQuery);
			log.debug("The case number is: " + caseNum);
			
			rs = pst.executeQuery();

			while (rs.next()) {
				ScreenSearchVO screenInfoVO = new ScreenSearchVO("", "", "");
				screenInfoVO.setScreenProtocolID(rs.getString("SCREEN_PROTOCOL_ID"));
				screenInfoVO.setScreenProtocolTitle(rs.getString("SCREEN_PROTOCOL_TITLE"));
				screenInfoVO.setScreenCode(rs.getString("SCREEN_CODE"));
				screenInfoVO.setScientistCode(rs.getString("SCIENTIST_CODE"));
				screenInfoVO.setRecipientSiteCode(rs.getString("SITE_CODE"));
				screenInfoVO.setRecipientName(rs.getString("LAST_NAME") + ", " + rs.getString("FIRST_NAME"));
				screenInfoVO.setContainerTypeCode(rs.getString("CONTAINER_TYPE_CODE"));
				screenInfoVO.setMinAmountValue(rs.getDouble("MIN_COMPOUND_REQUIRED"));
				screenInfoVO.setMinAmountUnit(rs.getString("AMOUNT_UNIT_CODE"));
				
				screenInfoVOList.add(screenInfoVO);
			}
			return screenInfoVOList;
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		} finally {
			cleanUp(con, pst, null, rs);
		}
	}
	
	public ArrayList<ScreenInfoRequest> processScreenInfoRequests(ArrayList<ScreenInfoRequest> screenInfoRequests)
		throws Exception
	{
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;;
		ResultSet rs = null;
		String sqlQuery1 = "SELECT name, title, nt_domain, nt_name, container_type_code, " +
        				   "       min_compound_required, amount_unit_code, status_code " +
						   "  FROM compound_registration_screen_protocol " +
						   "  WHERE screen_protocol_id = ? AND scientist_code = ?";
		String sqlQuery2 = "SELECT name, title, nt_domain, nt_name, container_type_code, " +
		   				   "       min_compound_required, amount_unit_code, status_code " +
						   "  FROM compound_registration_screen_protocol " +
						   "  WHERE screen_protocol_id = ? AND nt_name = ?";

		ArrayList<ScreenInfoRequest> results = null;
		if (screenInfoRequests != null && screenInfoRequests.size() > 0) {
			try {
				// Retrieve the Connection to the database from the Database pool
				conn = getConnection();

				stmt1 = conn.prepareStatement(sqlQuery1);
				stmt2 = conn.prepareStatement(sqlQuery2);

				Iterator<ScreenInfoRequest> it = screenInfoRequests.iterator();
				while (it.hasNext()) {
					ScreenInfoRequest req = (ScreenInfoRequest)it.next();
					
					if (req.getScientistCode() != null) {
						stmt1.setString(1, req.getScreenProtocolID());
						stmt1.setString(2, req.getScientistCode());

						rs = stmt1.executeQuery();
					} else {
						stmt2.setString(1, req.getScreenProtocolID());
						stmt2.setString(2, req.getRecipientUserID());
						rs = stmt2.executeQuery();
					}
					
					if (rs.next()) {
						if (results == null) results = new ArrayList<ScreenInfoRequest>();
						
						req.setMinAmountUnit(rs.getString("amount_unit_code"));
						req.setMinAmountValue(new Double(rs.getString("min_compound_required")).doubleValue());
						req.setRecipientSiteCode(rs.getString("nt_domain"));
						req.setRecipientUserID(rs.getString("nt_name"));
						req.setContainerTypeCode(rs.getString("container_type_code"));
						
						results.add(req);
					}
					try { if (rs != null) rs.close(); } catch (Exception e) { }   rs = null;
				}
			} catch (SQLException se) {
				throw new Exception("BiologyScreenDAO:processScreenInfoRequests:: SQL Exception Occured - Failed to Retrieve Screen Data", se);
			} catch (Exception e) {
				throw new Exception("BiologyScreenDAO:processScreenInfoRequests:: Failed to Retrieve Screen Data", e);
			} finally {
				cleanUp(conn, stmt1, stmt2, rs);
			}
		}
		
		return results;
	}


	public Connection getConnection()
		throws NamingException, SQLException, PropertyException
	{
		DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI("COMPOUND_REGISTRATION_DS_JNDI"));
		return (ds != null) ? ds.getConnection() : null;
	}
	
	public void cleanUp(Connection conn, Statement stmt1, Statement stmt2, ResultSet rs)
	{
		try { if (rs != null)    rs.close(); }    catch (Exception e) { }
		try { if (stmt1 != null) stmt1.close(); } catch (Exception e) { }
		try { if (stmt2 != null) stmt2.close(); } catch (Exception e) { }
		try { if (conn != null)  conn.close(); }  catch (Exception e) { }
	}
}
