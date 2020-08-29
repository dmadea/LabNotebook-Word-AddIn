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

import com.chemistry.enotebook.design.delegate.DesignServiceDelegate;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.dao.NotebookDAO;
import com.chemistry.enotebook.storage.dao.NotebookManager;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.ParallelExpDuperDeDuper;
import junit.framework.TestCase;

public class TestCreateParallelNotebook extends TestCase {

	static StorageDelegate storageObj = null;
	static NotebookRef nbRef = null;

	static void init() {
		boolean success = true;
		// Create Session identifier as we need to pass SessionIdentifier to
		// StorageDlegate
		try {
			// SessionIdentifier sidentifier = new
			// SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1",
					"USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
			storageObj = new StorageDelegate();
		} catch (Exception e) {
			success = false;
			System.err.println("Exception during EJB startup");
			e.printStackTrace();
		}
		if (success)
			System.out
					.println("Successfully established contact with StorageEJB...");
		else
			System.err.println("Failed to initialize connection.\n"
					+ "Is your webserver running?\n  Configured?");
	}

	public static void main(String[] args) {
		init();
		// junit.textui.TestRunner.run(TestCreateParallelNotebook.class);
		// testCreateParallelExpStepFromDSP();
		// testCreateParallelExpOnServer();
		testCreateParallelExpThruDelegate();
		// testCreateParallelExpFromDSP();
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		assertTrue("Storage Object missing!", storageObj != null);
	}

	public static void testCreateParallelExpThruDelegate() {
		try {
			// String nbK = "87654321";
			String notebook = "27071975";
			String spid = "SP0000000350v00";
			Stopwatch watch = new Stopwatch();
			watch.start("Create parallel Exp");
			long startTime = System.currentTimeMillis();
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1",
					"USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
			HttpUserProfile profile = new HttpUserProfile(
					"http://localhost:8080");
			sidentifier.setUserProfile(profile);
			sidentifier.setThreadId("Worker-0");
			storageObj = new StorageDelegate();
			NotebookPageModel pageModel = storageObj.createParallelExperiment(
					spid, "27071975", null);

			System.out.println("Created parallel exp:"
					+ pageModel.getNbRef().toString());
			nbRef = new NotebookRef(pageModel.getNbRef().getNbNumber(),
					pageModel.getNbRef().getNbPage(), pageModel.getKey());
			watch.stop();
			System.out.println(System.currentTimeMillis() - startTime
					+ " ms elapsed ");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void testCreateParallelExpOnServer() {
		try {
			// String nbK = "87654321";
			String notebook = "27071975";
			String spid = "SP0000000350v00";
			Stopwatch watch = new Stopwatch();
			watch.start("Create parallel Exp");
			long startTime = System.currentTimeMillis();
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1",
					"USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
			sidentifier.setThreadId("Worker-0");
			// StorageServiceImpl storageImpl = new StorageServiceImpl();
			// NotebookPageModel pageModel =
			// storageImpl.createParallelExperiment(spid, nbK, sidentifier);

			DesignServiceDelegate designService = new DesignServiceDelegate();
			long start = System.currentTimeMillis();
			NotebookPageModel pageModel = designService
					.getExperimentFromDesignService(spid, true);
			DAOFactory daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			int exp = notebookDAO.getNextExperimentForNotebook(notebook);

			pageModel.setNbRef(new NotebookRef("27071975", exp + ""));
			ParallelExpDuperDeDuper deduper = new ParallelExpDuperDeDuper();
			deduper.sortMonomerBatchesInSteps(pageModel);

			NotebookManager notebookMgr = daoFactory.getNotebookManager();
			pageModel.setThreadId(sidentifier.getThreadId());
			// Following method call Participates in Transaction.
			// notebookMgr.insertNotebookPage(pageModel);

			System.out.println("Created parallel exp:"
					+ pageModel.getNbRef().toString());
			nbRef = new NotebookRef(pageModel.getNbRef().getNbNumber(),
					pageModel.getNbRef().getNbPage(), pageModel.getKey());
			watch.stop();
			System.out.println(System.currentTimeMillis() - startTime
					+ " ms elapsed ");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public static void testCreateParallelExpFromDSP() {
		try {
			// String nbK = "87654321";
			String nbK = "27071975";
			String spid = "SP0000000365v00";
			Stopwatch watch = new Stopwatch();
			watch.start("Create parallel Exp");
			long startTime = System.currentTimeMillis();
			NotebookPageModel pageModel = storageObj.createParallelExperiment(
					spid, nbK,null);
			System.out.println("Created parallel exp:"
					+ pageModel.getNbRef().toString());
			nbRef = new NotebookRef(pageModel.getNbRef().getNbNumber(),
					pageModel.getNbRef().getNbPage(), pageModel.getKey());
			watch.stop();
			System.out.println(System.currentTimeMillis() - startTime
					+ " ms elapsed ");
			assertTrue("Notebook for exp",
					nbK.equals(pageModel.getNbRef().getNbNumber()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
