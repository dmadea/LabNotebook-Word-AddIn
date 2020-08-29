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
import com.chemistry.enotebook.storage.BatchNumberSearchContext;
import com.chemistry.enotebook.storage.StorageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import junit.framework.TestCase;

/**
 * 
 * @date May 25, 2004
 */
public class TestBatchNumberSearchContext extends TestCase {
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
		junit.textui.TestRunner.run(TestBatchNumberSearchContext.class);
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

	public void testRetrieveBatchNumberData() {
		BatchNumberSearchContext bnsCtx = new BatchNumberSearchContext();
		bnsCtx.setBatchNumber("12345678-0015-42");

		try {
			// System.out.println("Processing batchNumber = 12345678-0015-42");
			bnsCtx = (BatchNumberSearchContext) storageObj.retrieveData(bnsCtx, null);

			dumpData(((BatchNumberSearchContext) bnsCtx).getResults());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// System.out.println("Processing batchNumber = 00000006-0003-1");
			bnsCtx = (BatchNumberSearchContext) storageObj.retrieveData(bnsCtx, null);

			dumpData(((BatchNumberSearchContext) bnsCtx).getResults());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// System.out.println("Processing batchNumber = 00000006-0003-1");
			bnsCtx = (BatchNumberSearchContext) storageObj.retrieveData(bnsCtx, null);

			dumpData(((BatchNumberSearchContext) bnsCtx).getResults());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dumpData(StorageVO vo) {

	}
}
