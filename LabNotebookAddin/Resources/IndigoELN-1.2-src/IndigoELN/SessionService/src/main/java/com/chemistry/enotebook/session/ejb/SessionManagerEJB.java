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

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.errorlogger.delegate.LoggerProducer;
import com.chemistry.enotebook.person.PersonServiceFactory;
import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;
import com.chemistry.enotebook.security.SecurityServiceFactory;
import com.chemistry.enotebook.security.classes.PersonTO;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.DBHealthStatusVO;
import com.chemistry.enotebook.session.SystemHealth;
import com.chemistry.enotebook.session.SystemProperties;
import com.chemistry.enotebook.session.delegate.AuthenticationException;
import com.chemistry.enotebook.session.delegate.SessionTokenAccessException;
import com.chemistry.enotebook.session.delegate.SessionTokenInitException;
import com.chemistry.enotebook.session.interfaces.SessionManager;
import com.chemistry.enotebook.session.security.*;
import com.chemistry.enotebook.sessiontoken.delegate.SessionTokenLocalDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.util.*;

public class SessionManagerEJB implements SessionManager {
	
	private static final Log log = LogFactory.getLog(SessionManagerEJB.class);
	
	private static final Map<String, SessionIdentifier> userTokens = new HashMap<String, SessionIdentifier>();

	@Override
	public SessionToken login(String userName, String password) throws SessionTokenInitException, AuthenticationException {
		//boolean authed = false;
		SessionToken st = null;

		try {

			if (SecurityServiceFactory.getService().authenticate(userName, password)) {
				// authenticated fine if you get here
				st = getSessionToken(userName);
			} else {
				throw new AuthenticationException("Invalid Credentials");
			}		
		} catch (Exception e) {
			log.error("Failed authentication.", e);
			throw new SessionTokenInitException(e.getMessage(), e);
		}

		// log metrics to the db
		sendMetricsLogToServer("LOGIN", userName, st.getUserData().getSiteCode());

		// call SessionToken entity bean and create user
		// this step might be done asynchronously by using jms/MDB
		// this step might be combined with the metrics logging
		SessionTokenLocalDelegate sessionTokenLocalDelegate = new SessionTokenLocalDelegate();
		try {
			sessionTokenLocalDelegate.createSessionInfo(st.getTokenString(), userName);
		} catch (com.chemistry.enotebook.sessiontoken.delegate.SessionTokenAccessException e) {
			log.error("", e);
		}

		return st;
	}

	@Override
	public SessionToken impersonate(String loginUser, String loginPassword, String userName) throws SessionTokenInitException, AuthenticationException {
		boolean authed = false;
		SessionToken st = null;

		try {
			authed = SecurityServiceFactory.getService().authenticate(loginUser, loginPassword);
			if (authed) {
				// Ensure that login user is a superuser before impersonation
				st = getSessionToken(loginUser);
				if (st.getSessionIdentifier().isSuperUser()) {
					String tmpEmail = st.getSmtpEmail();

					SessionTokenLocalDelegate sessionTokenLocalDelegate = new SessionTokenLocalDelegate();
					sessionTokenLocalDelegate.deleteSessionInfo(st.getSessionIdentifier().getTokenString());

					// authenticated fine if you get here
					st = getSessionToken(userName);

					// Some users who have left do not have an email so use the email of the impersonator
					if (st.getSmtpEmail() == null || st.getSmtpEmail().length() == 0)
						st.setSmtpEmail(tmpEmail);
				} else
					throw new SessionTokenInitException("Login user can not impersonate, only a Superuser can do this");
			}
		} catch (Exception e) {
			throw new SessionTokenInitException(e.getMessage(), e);
		}
		if (!authed) {
			throw new AuthenticationException("Invalid Credentials");
		}

		// log metrics to the db
		sendMetricsLogToServer("LOGIN", userName, st.getUserData().getSiteCode());

		// call SessionToken entity bean and create user
		// this step might be done asynchronously by using jms/MDB
		// this step might be combined with the metrics logging
		SessionTokenLocalDelegate sessionTokenLocalDelegate = new SessionTokenLocalDelegate();
		try {
			sessionTokenLocalDelegate.createSessionInfo(st.getTokenString(), userName);
		} catch (com.chemistry.enotebook.sessiontoken.delegate.SessionTokenAccessException e) {
			log.error("", e);
		}

		return st;
	}

	@Override
	@Deprecated
	public void logout(String sToken) throws SessionTokenAccessException {
		try {
			// call SessionToken entity bean and delete session token from db
			SessionTokenLocalDelegate sessionTokenLocalDelegate = new SessionTokenLocalDelegate();
			sessionTokenLocalDelegate.deleteSessionInfo(sToken);
		} catch (Exception e) {
			throw new SessionTokenAccessException(e.getMessage(), e);
		}
	}

	@Override
	public void logout(SessionIdentifier sessionID) throws SessionTokenAccessException {
		try {
			// log metrics to the db
			sendMetricsLogToServer("LOGOUT", sessionID.getUserID(), sessionID.getSiteCode());

			// call SessionToken entity bean and delete session token from db
			SessionTokenLocalDelegate sessionTokenLocalDelegate = new SessionTokenLocalDelegate();
			sessionTokenLocalDelegate.deleteSessionInfo(sessionID.getTokenString());
		} catch (Exception e) {
			throw new SessionTokenAccessException(e.getMessage(), e);
		}
	}

	private void sendMetricsLogToServer(String funcName, String user, String SiteCode) {
		StringBuffer sb = new StringBuffer();
		sb.append("<CeN_Log>");
		sb.append("<Log_Type>Metrics</Log_Type>");
		sb.append("<Log_Body>");
		sb.append("<Application_Name>CeN</Application_Name>");
		sb.append("<Sub_Function_Name>" + funcName + "</Sub_Function_Name>");
		sb.append("<Account_Name>" + user + "</Account_Name>");
		sb.append("<Site_Code>" + SiteCode + "</Site_Code>");
		sb.append("</Log_Body>");
		sb.append("</CeN_Log>");
		LoggerProducer.logMessage(new String(sb));
	}

	private SessionToken getSessionToken(String userName) throws SessionTokenAccessException {
		SessionToken st = null;

		try {
			// Generate a key. In practice, you would save this key.
			long l = Calendar.getInstance().getTimeInMillis();

			// Encrypt
			String encrypted = TokenEncrypter.encrypt(userName + l);
			PersonTO userDetails = SecurityServiceFactory.getService().getPerson(userName);

			String userDesc = userDetails.getDescription();
			if (userDesc == null)
				userDesc = "";
			if (userDesc.indexOf(", ") < 0)
				userDesc = userDesc.replaceFirst(",", ", ");

			// Get user Preferences, create user with site defaults if necessary
			UserData ud = null;
			UserDAO uDao = new UserDAO();
			boolean existingCeNUser = uDao.doesExistInCeNUsers(userName);
			if (existingCeNUser) {
				ud = uDao.getData(userName);
			} else {
				CompoundManagementEmployee compoundManagementEmp = uDao.getCompoundManagementEmployee(userName);
				ud = uDao.createUser(userName, compoundManagementEmp.getSiteCode(), userDesc, "VALID", compoundManagementEmp.getEmployeeId());

				sendMetricsLogToServer("NEW USER", userName, compoundManagementEmp.getSiteCode()); // log metrics to the db
			}

			st = new SessionToken(userName, encrypted, ud);
			if (st.getUserData().getXmlMetaData() != null){
				int xml_size =  st.getUserData().getXmlMetaData().length();
				int sub_size = xml_size > 100 ? 100 : xml_size;
				log.debug("Is super user:" + st.getUserData().getXmlMetaData().substring(0, sub_size));
			}
			st.setLoginTime(l);
			st.setFirstName(userDetails.getFirstName());
			st.setLastName(userDetails.getLastName());
			st.setDisplayName(userDesc);
			st.setSmtpEmail(userDetails.getSmtpAddress());
			st.setNtDomain(userDetails.getNtDomain());

			// Store the Token to validate future requests for tokens, Could be stored in database or Server Cache.
			userTokens.put(userName, st.getSessionIdentifier());
		} catch (Exception e) {
			throw new SessionTokenAccessException(e.getMessage(), e);
		}

		return st;
	}

	@Override
	public void updateUser(UserData userData) throws Exception {
		UserDAO uDao = new UserDAO();
		uDao.setData(userData);
	}

	@Override
	public UserData getUser(String ntUser) throws Exception {
		UserDAO uDao = new UserDAO();
		UserData userData = uDao.getData(ntUser);
		return userData;
	}

	public String getNtUserNameByCompoundManagementEmployeeId(String compoundManagementEmployeeId) throws Exception {
		UserDAO uDao = new UserDAO();
		return uDao.getNtUserNameByCompoundManagementEmployeeId(compoundManagementEmployeeId);
	}

	@Override
	public CompoundManagementEmployee getCompoundManagementEmployeeID(String userName) throws Exception {
		CompoundManagementEmployee result = null;
		// no reason to have a try catch block if you are simply rethrowing the same error.
		UserDAO uDao = new UserDAO();
		result = uDao.getCompoundManagementEmployee(userName);

		return result;
	}

	@Override
	public UserData createUser(String ntUser, String siteCode, String fullName, String status, String empID) throws Exception {
		UserDAO uDao = new UserDAO();
		UserData uData = uDao.createUser(ntUser, siteCode, fullName, status, empID);

		// log metrics to the db
		sendMetricsLogToServer("NEW USER", ntUser, siteCode);

		// //Refresh the CEN tables in the CodeTableCache via local ejb call
		// ArrayList sourceList = new ArrayList();
		// sourceList.add(CodeTableSource.CEN);
		// CodeTableLocalDelegate ctd = new CodeTableLocalDelegate();
		// ctd.refreshCodeTables(sourceList);
		return uData;
	}

	@Override
	public List<DBHealthStatusVO> checkSystemDBHealth() throws Exception {
		ArrayList<DBHealthStatusVO> dbStatusList = new ArrayList<DBHealthStatusVO>();
		SystemHealth systemHealth = new SystemHealth();

		ArrayList<String> dsList = buildDSList();
		boolean isAvailable = false;
		for (int i = 0; i < dsList.size(); i++) {
			DBHealthStatusVO dBHealthStatusVO = new DBHealthStatusVO();
			dBHealthStatusVO.setDbJNDI(dsList.get(i));
			if (dsList.get(i).equals(ServiceLocator.CEN_DS_JNDI)) {
				dBHealthStatusVO.setDbDesc(DBHealthStatusVO.CEN_DB);
			} else if (dsList.get(i).equals("COMPOUND_MANAGEMENT_DS_JNDI")) {
				dBHealthStatusVO.setDbDesc(DBHealthStatusVO.COMPOUND_MANAGEMENT_DB);
			} else if (dsList.get(i).equals("COMPOUND_REGISTRATION_DS_JNDI")) {
				dBHealthStatusVO.setDbDesc(DBHealthStatusVO.COMPOUND_REGISTRATION_DB);
			} else if (dsList.get(i).equals("CPI_DS_JNDI")) {
				dBHealthStatusVO.setDbDesc(DBHealthStatusVO.ACD_DB);
			}
			isAvailable = systemHealth.testDBConnection((String) dsList.get(i));
			if (isAvailable) {
				dBHealthStatusVO.setHealthStatus("GOOD");
			} else {
				dBHealthStatusVO.setHealthStatus("BAD");

				// For CompoundManagement and CompoundRegistration if the Code Tables are loaded then it
				// is a minimal impact
				/*if (dsList.get(i).equals("COMPOUND_MANAGEMENT_DS_JNDI")) {
					ArrayList<String> list = new ArrayList<String>();
					list.add("CompoundManagement");
					try {
						CodeTableLocalDelegate ctd = new CodeTableLocalDelegate();
						if (ctd.areCodeTablesCached(list))
							dBHealthStatusVO.setHealthStatus("MINIMAL");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (dsList.get(i).equals("COMPOUND_REGISTRATION_DS_JNDI")) {
					ArrayList<String> list = new ArrayList<String>();
					list.add("GCD");
					try {
						CodeTableDelegate ctd = new CodeTableDelegate();
						if (ctd.areCodeTablesCached(list))
							dBHealthStatusVO.setHealthStatus("MINIMAL");
					} catch (Exception e) {
						log.warn("Failed to determine whether code table delegate cached code tables", e);
					}
				} else if (dsList.get(i).equals("CPI_DS_JNDI"))
					dBHealthStatusVO.setHealthStatus("MINIMAL");
				else if (dsList.get(i).equals("CUS_DS_JNDI"))
					dBHealthStatusVO.setHealthStatus("MINIMAL");
				*/
			}

			dbStatusList.add(dBHealthStatusVO);
		}
		return dbStatusList;
	}

	private ArrayList<String> buildDSList() {
		ArrayList<String> dsStatusList = new ArrayList<String>();
		dsStatusList.add(ServiceLocator.CEN_DS_JNDI);
		return dsStatusList;
	}

	@Override
	public SystemProperties getSystemProperties(String siteCode) throws Exception {
		return new CeNPropertiesDAO().getData(siteCode);
	}

	@Override
	public String getSystemMemoryStatistics() throws Exception {
		Runtime stats = Runtime.getRuntime();

		double freeMemory = stats.freeMemory() / 1024.0 / 1024.0;
		double totalMemory = stats.totalMemory() / 1024.0 / 1024.0;
		double maxMemory = stats.maxMemory() / 1024.0 / 1024.0;
		double percentFreeAllocated = Math.round((freeMemory / totalMemory) * 100.0);
		double percentAllocated = Math.round((totalMemory / maxMemory) * 100.0);

		StringBuffer result = new StringBuffer();
		result.append("Max Memory Available to JVM: " + Math.round(maxMemory) + " mb\n");
		result.append("Total Memory Allocated: " + Math.round(totalMemory) + " mb\n");
		result.append("Free Allocated Memory: " + Math.round(freeMemory) + " mb\n");
		result.append(percentFreeAllocated + " % of Allocated Memory is free\n");
		result.append(percentAllocated + " % of Available Memory is Allocated\n");

		return result.toString();
	}

	@Override
	public IPerson userIDtoPerson(String userId) throws PersonServiceException {
		try {
			return PersonServiceFactory.getService().userIDtoPerson(userId);
		} catch (LoadServiceException e) {
			throw new PersonServiceException("", e);
		}
	}

	@Override
	public String getLocationBy(String name) throws PersonServiceException {
		try {
			return PersonServiceFactory.getService().getLocationBy(name);
		} catch (LoadServiceException e) {
			throw new PersonServiceException("", e);
		}
	}

	@Override
	public IPerson[] getPeople(String first, String last, BigInteger cnt) throws PersonServiceException {
		try {
			return PersonServiceFactory.getService().getPeople(first, last, cnt);
		} catch (LoadServiceException e) {
			throw new PersonServiceException("", e);
		}
	}
}
