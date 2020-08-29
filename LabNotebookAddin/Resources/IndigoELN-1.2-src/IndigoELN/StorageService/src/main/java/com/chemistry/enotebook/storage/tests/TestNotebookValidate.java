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
import com.chemistry.enotebook.storage.ValidationInfo;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import junit.framework.TestCase;

/**
 * 
 * @date December 20, 2004
 */
public class TestNotebookValidate extends TestCase {
	static SessionTokenDelegate sessionObj = null;
	static StorageDelegate storageObj = null;

	static String userID = "USER"; // Put username / password here TEMPORARILY FOR TEST
	static String password = "pass";

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

		if (success) {
			System.out.println("Successfully established contact...");
		} else {
			System.err.println("Failed to initialize connection.\n" + "Is your webserver running?\n  Configured?");
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestNotebookValidate.class);
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

	public void testValidateNotebook() {
		try {
			assertTrue(storageObj.validateNotebook("SITE1", "99123456", "0931") != null); // Exists
			assertFalse(storageObj.validateNotebook("SITE1", "99123456", "9999") != null); // Does not Exist

			assertTrue(storageObj.validateNotebook(null, "99123456", null) != null); // Exists
			ValidationInfo vi = storageObj.validateNotebook(null, "99123456", "0931");
			assertTrue(vi != null); // Exists
			assertTrue(vi.siteCode.equals("SITE1"));
			assertTrue(vi.notebook.equals("99123456"));
			assertTrue(vi.creator.equals("USER"));
			assertTrue(vi.experiment.equals("0931"));
			assertTrue(vi.status.equals("OPEN"));
			assertFalse(vi.multipleResultsFlag);

			assertFalse(storageObj.validateNotebook(null, "99193956", null) != null); // Does not Exist
			assertFalse(storageObj.validateNotebook(null, "99193956", "9494") != null); // Does not Exist
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDeleteExperiment() {
		try {
			storageObj.deleteExperiment(null, "99123456", "0004", 1, null);
			assertFalse(true);
		} catch (Exception e) {
			assertFalse(false);
			e.printStackTrace();
		}

		try {
			storageObj.deleteExperiment("SITE1", null, "0004", 1,null);
			assertFalse(true);
		} catch (Exception e) {
			assertFalse(false);
			e.printStackTrace();
		}

		try {
			storageObj.deleteExperiment("SITE1", "99123456", null, 1,null);
			assertFalse(true);
		} catch (Exception e) {
			assertFalse(false);
			e.printStackTrace();
		}

		try {
			storageObj.deleteExperiment("SITE1", "99123456", "0004", 0,null);
			assertFalse(true);
		} catch (Exception e) {
			assertFalse(false);
			e.printStackTrace();
		}

		try {
			assertTrue(storageObj.deleteExperiment("SITE1", "99123456", "0005", 1,null));
		} catch (Exception e) {
			assertFalse(false);
			e.printStackTrace();
		}
	}
}
