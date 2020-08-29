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
 * TestNotebookContext.java
 *
 * Created on May 25, 2004
 *
 *
 */
package com.chemistry.enotebook.storage.tests;

import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.session.security.SessionToken;
import com.chemistry.enotebook.storage.SpeedBarContext;
import com.chemistry.enotebook.storage.StorageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import junit.framework.TestCase;

/**
 * 
 * @date May 25, 2004
 */
public class TestSpeedBarContext extends TestCase {
	static SessionTokenDelegate sessionObj = null;
	static StorageDelegate storageObj = null;

	static String userID = "USER"; // Put username / password here TEMPORARILY FOR TEST
	static String password = "password";

	static SessionIdentifier sessionID = null;

	static {
		boolean success = true;

		// Setup initial context for test
		try {
			sessionObj = new SessionTokenDelegate();
			SessionToken st = sessionObj.login(userID, password);
			sessionID = st.getSessionIdentifier();

			storageObj = new StorageDelegate();
		} catch (Exception e) {
			System.err.println("Exception during EJB startup");
			e.printStackTrace();
			success = false;
		}

		if (success)
			System.out.println("Successfully established contact...");
		else
			System.err.println("Failed to initialize connection.\n" + "Is your webserver running?\n  Configured?");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestSpeedBarContext.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		assertTrue("Storage Object missing!", storageObj != null);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRetrieveSpeedBarGTNData() {
		SpeedBarContext sbCtx = new SpeedBarContext();
		sbCtx.setSiteCode("SITE1");

		try {
			// System.out.println("Processing users for GTN:");
			sbCtx = (SpeedBarContext) storageObj.retrieveData(sbCtx,null);

			dumpData(((SpeedBarContext) sbCtx).getResults());
		} catch (Exception e) {
			e.printStackTrace();
		}

		sbCtx.setSiteCode("SITE1");
		sbCtx.setUserName("user13");

		try {
			// System.out.println("Processing Notebooks for user13:");
			sbCtx = (SpeedBarContext) storageObj.retrieveData(sbCtx,null);

			dumpData(((SpeedBarContext) sbCtx).getResults());
		} catch (Exception e) {
			e.printStackTrace();
		}

		sbCtx.setSiteCode("SITE1");
		sbCtx.setUserName("user13");
		sbCtx.setNotebook("00055581");

		try {
			// System.out.println("Processing Notebook Pages for user13 / 00055581:");
			sbCtx = (SpeedBarContext) storageObj.retrieveData(sbCtx,null);

			dumpData(((SpeedBarContext) sbCtx).getResults());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dumpData(StorageVO vo) {

	}
}
