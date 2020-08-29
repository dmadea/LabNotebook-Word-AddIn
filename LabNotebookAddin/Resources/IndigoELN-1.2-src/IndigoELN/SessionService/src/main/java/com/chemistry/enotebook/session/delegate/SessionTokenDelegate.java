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
package com.chemistry.enotebook.session.delegate;

import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.DBHealthStatusVO;
import com.chemistry.enotebook.session.SystemProperties;
import com.chemistry.enotebook.session.interfaces.SessionManager;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.session.security.SessionToken;
import com.chemistry.enotebook.session.security.UserData;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class SessionTokenDelegate implements SessionManager, Serializable {

	private static final long serialVersionUID = -1227465304979422958L;
	
	private SessionManager service;

	public SessionTokenDelegate() throws SessionTokenInitException {
		service = ServiceLocator.getInstance().locateService("SessionService", SessionManager.class);
	}

	@Override
	public SessionToken login(String ntUser, String password) throws SessionTokenInitException, AuthenticationException {
		return service.login(ntUser, password);
	}

	@Override
    public SessionToken impersonate(String loginUser, String loginPassword, String userName) throws Exception, SessionTokenAccessException, AuthenticationException	{
        return service.impersonate(loginUser, loginPassword, userName);
    }

	@Override
    @Deprecated
    public void logout(String sToken) throws Exception {
    	service.logout(sToken);
    }

	@Override
    public void logout(SessionIdentifier sessionID) throws Exception {
    	service.logout(sessionID);
    }

	@Override
	public void updateUser(UserData userData) throws Exception {
		service.updateUser(userData);
	}

	@Override
	public UserData getUser(String ntUser) throws Exception {
		return service.getUser(ntUser);
	}

	@Override
	public CompoundManagementEmployee getCompoundManagementEmployeeID(String userName) throws Exception {
		return service.getCompoundManagementEmployeeID(userName);
	}

	@Override
	public String getNtUserNameByCompoundManagementEmployeeId(String compoundManagementEmployeeId) throws Exception {
		return service.getNtUserNameByCompoundManagementEmployeeId(compoundManagementEmployeeId);
	}

	@Override
	public UserData createUser(String ntUser, String siteCode, String fullName, String status, String empId) throws Exception {
		return service.createUser(ntUser, siteCode, fullName, status, empId);
	}

	@Override
	public List<DBHealthStatusVO> checkSystemDBHealth() throws Exception {
		return service.checkSystemDBHealth();
	}

	@Override
	public SystemProperties getSystemProperties(String siteCode) throws Exception {
		return service.getSystemProperties(siteCode);
	}

	@Override
	public String getSystemMemoryStatistics() throws Exception {
		return service.getSystemMemoryStatistics();
	}

    public SystemProperties getSystemPropertiesOnServer(String SiteCode) throws Exception {
        return service.getSystemProperties(SiteCode);
    }

	@Override
	public IPerson userIDtoPerson(String userId) throws PersonServiceException {
		return service.userIDtoPerson(userId);
	}

	@Override
	public String getLocationBy(String name) throws PersonServiceException {
		return service.getLocationBy(name);
	}

	@Override
	public IPerson[] getPeople(String first, String last, BigInteger cnt) throws PersonServiceException {
		return service.getPeople(first, last, cnt);
	}
}
