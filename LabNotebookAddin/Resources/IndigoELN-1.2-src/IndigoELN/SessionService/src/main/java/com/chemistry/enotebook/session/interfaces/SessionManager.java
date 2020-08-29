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
 * Generated by XDoclet - Do not edit!
 */
package com.chemistry.enotebook.session.interfaces;

import com.chemistry.enotebook.person.classes.IPerson;
import com.chemistry.enotebook.person.exceptions.PersonServiceException;
import com.chemistry.enotebook.session.DBHealthStatusVO;
import com.chemistry.enotebook.session.SystemProperties;
import com.chemistry.enotebook.session.delegate.AuthenticationException;
import com.chemistry.enotebook.session.delegate.SessionTokenAccessException;
import com.chemistry.enotebook.session.delegate.SessionTokenInitException;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.session.security.SessionToken;
import com.chemistry.enotebook.session.security.UserData;

import java.math.BigInteger;
import java.util.List;

public interface SessionManager {

   public SessionToken login(String userName, String password) throws SessionTokenInitException, AuthenticationException;

   public SessionToken impersonate(String loginUser, String loginPassword, String userName) throws Exception, SessionTokenInitException, AuthenticationException;

   public void logout(String sToken) throws Exception, SessionTokenAccessException;

   public void logout(SessionIdentifier sessionID) throws Exception, SessionTokenAccessException;

   public void updateUser(UserData userData) throws Exception;

   public UserData getUser(String ntUser) throws Exception;

   public CompoundManagementEmployee getCompoundManagementEmployeeID(String userName) throws Exception;

   public String getNtUserNameByCompoundManagementEmployeeId(String compoundManagementEmployeeId) throws Exception;
   
   public UserData createUser(String ntUser, String siteCode, String fullName, String status, String empID) throws Exception;

   public List<DBHealthStatusVO> checkSystemDBHealth() throws Exception;

   public SystemProperties getSystemProperties(String siteCode) throws Exception;

   public String getSystemMemoryStatistics() throws Exception;
   
   public IPerson userIDtoPerson(String userId) throws PersonServiceException;
   
   public String getLocationBy(String name) throws PersonServiceException;
   
   public IPerson[] getPeople(String first, String last, BigInteger cnt) throws PersonServiceException;
}