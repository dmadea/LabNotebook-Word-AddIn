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

import com.chemistry.enotebook.storage.NotebookContext;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import junit.framework.TestCase;

/**
 * 
 * @date May 25, 2004
 */
public class TestNotebookContext extends TestCase {
	static final String URL = "localhost:7001";
	static StorageDelegate storeObj = null;

	String[] validNBRef = { "12345678-123", "00000001-0001" };
	String[] invalidNBRef = { "AA123456-AB", "A145132-1515" };
	String ntUserID = "user10";
	String ntOwnerID = "user11";
	String nbNumber = "1";
	String nbAltNumber = "32";
	String paddedNBNumber = "00000001";
	String paddedNBAltNumber = "00000032";
	String nbPage = "9";
	String nbAltPage = "45";
	String paddedNBPage = "0009";
	String paddedNBAltPage = "0045";
	String siteCode = "SITE1";
	String siteName = "Groton";

	static {
		boolean success = true;
		// Setup initial context for test

		// Get a Storage Delegate Object
		try {
			storeObj = new StorageDelegate();
		} catch (Exception e) {
			System.err.println("Failed to create the object!");
			e.printStackTrace();
			success = false;
		}
		if (success) {
			System.err.println("Successfully established contact...");
		} else {
			System.err.println("Failed to initialize connection.\n" + "Is your webserver running?\n  Configured?");
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestNotebookContext.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		assertTrue("Context Object missing!", storeObj != null);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNotebookContextObjGettersAndSetters() {
		NotebookContext nc = new NotebookContext();

		// test getters and setters:
		nc.setChemistUserID(this.ntUserID);
		assertTrue(nc.getChemistUserID().equals(this.ntUserID));

		nc.setNotebookNumber(this.nbNumber);
		assertTrue(nc.getNotebookNumber().equals(this.paddedNBNumber));
		nc.setExperiment(this.nbPage);
		assertTrue(nc.getExperiment().equals(this.paddedNBPage));

		nc.setNotebookRef(this.nbAltNumber + "-" + this.nbAltPage);
		assertTrue(nc.getNotebookNumber().equals(this.paddedNBAltNumber));
		assertTrue(nc.getExperiment().equals(this.paddedNBAltPage));
		assertTrue(nc.getNotebookRef().equals(this.paddedNBAltNumber + "-" + this.paddedNBAltPage));

		nc.setSiteCode(this.siteCode);
		assertTrue(nc.getSiteCode().equals(this.siteCode));

		nc.setOwnerUserID(this.ntOwnerID);
		assertTrue(nc.getOwnerUserID().equals(this.ntOwnerID));
	}
}
