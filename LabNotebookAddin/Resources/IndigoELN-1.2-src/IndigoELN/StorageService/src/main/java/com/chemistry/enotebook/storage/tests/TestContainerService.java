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
package com.chemistry.enotebook.storage.tests;

import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import junit.framework.TestCase;

import java.util.List;

public class TestContainerService extends TestCase {

	static StorageDelegate storageObj = null;
	NotebookRef nbRef = null;

	static void init() {
		boolean success = true;
		// Create Seesion identifier as we need to pass SessionIdentifier to StorageDlegate
		try {
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
			storageObj = new StorageDelegate();
		} catch (Exception e) {
			success = false;
			System.err.println("Exception during EJB startup");
			e.printStackTrace();
		}
		if (success)
			System.out.println("Successfully established contact with StorageEJB...");
		else
			System.err.println("Failed to initialize connection.\n" + "Is your webserver running?\n  Configured?");
	}

	public static void main(String[] args) {
		init();
		junit.textui.TestRunner.run(TestContainerService.class);
		// testCreateParallelExpStepFromDSP();
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
	
	public void testGetUserContainer() {
		 try {
			 List usrList = storageObj.getUserContainers("USER");
			 System.out.println("USR container Results size:"+usrList.size());
		 }catch(Exception e)
		 {
			e.printStackTrace(); 
		 }
		 }
}