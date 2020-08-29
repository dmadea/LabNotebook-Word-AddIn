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
 * Created on Jul 22, 2004
 *
 * Business delegate can decouple business components from the code that uses them. 
 * The Business Delegate pattern manages the complexity of distributed component 
 * lookup and exception handling, and may adapt the business component interface 
 * to a simpler interface for use by views. 
 * */

package com.chemistry.enotebook.sessiontoken.delegate;

import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.sessiontoken.interfaces.SessionTokenRemote;

public class SessionTokenLocalDelegate implements SessionTokenRemote {

	private SessionTokenRemote service;
	
	public SessionTokenLocalDelegate() {
		service = ServiceLocator.getInstance().locateService("SessionTokenService", SessionTokenRemote.class);
	}
	
	@Override
	public void createSessionInfo(String sessionToken, String uid) throws SessionTokenAccessException {
		service.createSessionInfo(sessionToken, uid);
	}

	@Override
	public boolean findSessionInfo(String sessionToken) throws SessionTokenAccessException {
		return service.findSessionInfo(sessionToken);
	}

	@Override
	public void deleteSessionInfo(String sessionToken) throws SessionTokenAccessException {
		service.deleteSessionInfo(sessionToken);
	}
}
