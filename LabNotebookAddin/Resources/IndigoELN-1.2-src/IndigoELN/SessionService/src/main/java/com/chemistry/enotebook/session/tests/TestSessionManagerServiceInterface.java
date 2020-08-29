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
package com.chemistry.enotebook.session.tests;

import com.chemistry.enotebook.security.SecurityServiceFactory;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.delegate.SessionTokenInitException;
import com.chemistry.enotebook.session.security.SessionToken;
import junit.framework.TestCase;

public class TestSessionManagerServiceInterface extends TestCase {
	// static final String JNDI_FACTORY =
	// "weblogic.jndi.WLInitialContextFactory";

	// static final String URL = "t3://localhost:7001";
	static SessionTokenDelegate stObj = null;

	static {
//		boolean success = true;
		// Setup initial context for test
		try {
			stObj = new SessionTokenDelegate();
		} catch (SessionTokenInitException nnfe) {
			System.err.println("Name Not found exception!");
			nnfe.printStackTrace();
//			success = false;
		} catch (Exception nnfe) {
			System.err.println("Name Not found exception!");
			nnfe.printStackTrace();
//			success = false;
		}

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestSessionManagerServiceInterface.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		assertTrue("Context Object missing!", stObj != null);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLogin() {
		SessionToken myToken = null;
//		java.util.ArrayList retVal = null;
		try {
			myToken = stObj.login("username", "password");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.err.println("Login Success  " + myToken.getLoginTime() + ", "
				+ myToken.getTokenString());
	}

	public void testCslLogin() {
		try {
			SecurityServiceFactory.getService().authenticate(
					"user", "password");
			assertTrue("LDAP login check!", true);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("An error Occured", 1 == 0);
		}
	}
}
