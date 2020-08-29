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

import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;

//import com.cen.TestDelegate;

public class TestStorageService extends TestCase {

	static StorageDelegate storageObj = null;
	NotebookRef nbRef = null;
	SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER2", "kicL/o26dciVm/9w+dvhXIiYBzP0VOoH", true);

	static void init() {
		boolean success = true;
		// Create Seesion identifier as we need to pass SessionIdentifier to StorageDlegate
		try {
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER2", "kicL/o26dciVm/9w+dvhXIiYBzP0VOoH", true);
			// SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
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
		junit.textui.TestRunner.run(TestStorageService.class);
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

	public void testLoadParallelExp() {
		try {
			String nbK = "87654321";
			String page = "0325";
			String site = "SITE1";
			int version = 1;
			nbRef = new NotebookRef(nbK, page);
			nbRef.setVersion(version);
			NotebookPageModel pageModel = storageObj.getNotebookPageExperimentInfo(nbRef, site,null);
			System.out.println("Loaded parallel exp:" + pageModel.getNbRef().toString());
			nbRef = new NotebookRef(pageModel.getNbRef().getNbNumber(), pageModel.getNbRef().getNbPage(), pageModel.getKey());
			assertTrue("Notebook forexp", nbK.equals(pageModel.getNbRef().getNbNumber()));
			//TestDelegate delgate = new TestDelegate();
			//delgate.updateNBKPageModel(pageModel);
			System.out.println("NBK pagemodel updated");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void atestGetAllRegisteredProductBatches() {
		try {
			//List pbModels = storageObj.getAllRegisteredBatchesForPage("2e728b17ac1eb8592142bb9282067380bd70a2e1");
			List pbModels = storageObj.getAllRegisteredBatchesForJobid("495215",null);
			System.out.println("Size of PB is"+pbModels.size());
			for(Iterator iter = pbModels.iterator();iter.hasNext();)
			{
				BatchRegInfoModel model = (BatchRegInfoModel) iter.next();
				if(model.getBatchKey().equals("2e72f60bac1eb8599173e7d06650674068a84ece"))
				{
					System.out.println("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void atestUpdateBatchesWithRegistration()
	{
		try {
			//List pbModels = storageObj.getAllRegisteredBatchesForPage("2e728b17ac1eb8592142bb9282067380bd70a2e1");
			List comRegModels = storageObj.getAllRegisteredBatchesForJobid("495215",null);
			System.out.println("Size of PB is"+comRegModels.size());
			storageObj.updateBatchJobs((BatchRegInfoModel[])comRegModels.toArray(new BatchRegInfoModel[]{}),null);
			System.out.println("update RegBatches Reg Info completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}