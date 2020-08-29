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

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.SignaturePageVO;
import com.chemistry.enotebook.storage.dao.AnalysisDAO;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.dao.NotebookLoadDAO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.storage.service.StorageServiceImpl;
import com.chemistry.enotebook.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class TestNotebookLoadDAO {

	public static SessionIdentifier sidentifier = null;
	static
	{
	 sidentifier = new SessionIdentifier("SITE1", "USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);	
	}
	public static void testNotebookPageLoadFromStorageEJB(NotebookRef nbref) throws Exception {
		
		StorageDelegate ssi = new StorageDelegate();
		long startTime = System.currentTimeMillis();
		NotebookPageModel page = ssi.getNotebookPageExperimentInfo(nbref, sidentifier.getSiteCode(),null);//getNotebookPageExperimentInfo(nbref, sidentifier);
		System.out.println(System.currentTimeMillis() - startTime +
        " ms elapsed for [ NotebookPage Load "+nbref+"]");
		System.out.println("Loaded Page "+page.getUserName()+" : "+page.getNbRef().getNbRef());
		
		CommonUtils.printNBPageData(page);
   	 }

	public static void testNotebookPagesForUser(String user) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao = factory.getNotebookLoadDAO();
		List list = dao.getNotebookPagesForUser(user);
		// System.out.println("Notebook List Size---:"+list.size());
		for (int i = 0; i < list.size(); i++) {
			NotebookPageModel page = (NotebookPageModel) list.get(i);
			// System.out.println(page.getUserNTID()+" : "+page.getNbRef().getNbRef());
		}
	}

	public static void testGetDesignSynthesisPlan(String notebookPageId, int version) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		//DesignSynthesisPlan plan = dao.getDesignSynthesisPlan(notebookPageId, version);
		// System.out.println("DesignSynthesisPlan Size---:"+plan);
	}

	public static void testGetAttachments(String notebookPageId, int version) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		
	}

	public static void testGetAnalysis(String pageKey) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AnalysisDAO dao =  factory.getAnalysisDAO();
		List list = dao.loadAnalysis(pageKey);
		// System.out.println("Analysis Size---:"+list.size());
		// for (int i=0;i<list.size();i++ )
		// {
		// NotebookPageModel page = (NotebookPageModel)list.get(i);
		// //System.out.println(page.getUserNTID()+" : "+page.getNbRef().getNbRef());
		// }
	}

	public static void testGetAllReactionSteps(String notebookPageId, int version) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		//List list = dao.getAllReactionSteps(notebookPageId, version);
		// System.out.println("Attachments Size---:"+list.size());
		// for (int i=0;i<list.size();i++ )
		// {
		// NotebookPageModel page = (NotebookPageModel)list.get(i);
		// //System.out.println(page.getUserNTID()+" : "+page.getNbRef().getNbRef());
		// }
	}

	public static void testloadNBKPageOnServer(NotebookRef nbref) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		NotebookPageModel page = dao.loadNotebookPage(nbref);
		//ReactionStepModel stepObj = dao.getReactionStepSummary(notebookPageId, version);
		System.out.println("ReactionStepModel ---:");
	}
	
	public static void testGetReactionStepSummary(String notebookPageId, int version) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		//ReactionStepModel stepObj = dao.getReactionStepSummary(notebookPageId, version);
		// System.out.println("ReactionStepModel ---:"+stepObj);
	}

	public static void testGetIntermediateReactionStep(String notebookPageId, int version, int seqNo)throws Exception {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		//ReactionStepModel model = dao.getIntermediateReactionStep(notebookPageId, version, seqNo);
		// System.out.println("ReactionStepModel---:"+model);
	}

	public static void testGetReactionStepDetails(String notebookPageId, int version) throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		//ReactionStepModel stepObj = dao.getReactionStepSummary(notebookPageId, version);
		// System.out.println("ReactionStepModel ---:"+stepObj);
		//ReactionStepModel detail = dao.getReactionStepDetails( stepObj);
		// System.out.println("ReactionStepDetail ---:"+stepObj);

	}

	public static void testCreateConceptionExperiment() throws Exception{
		StorageServiceImpl ss = new StorageServiceImpl();
		//NotebookPageModel page = ss.createConceptionRecord("27071975", sidentifier);
	}

	public static void testLoadRegisteredBatches() throws Exception{
		String pageKey = "3cce561bac1eb8594171072024167040a03f7129";
		SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
		HttpUserProfile user = new HttpUserProfile("http://localhost:65432");
		sidentifier.setUserProfile(user);
		StorageDelegate delegate =  new StorageDelegate();
		List list = delegate.getAllRegisteredBatchesForPage(pageKey,null);
		System.out.println("loaded reg batches :"+list.size());
	}
	
    public static void testNotebookPageLoadFromStorageImpl(NotebookRef nbref) throws Exception {
		
    	StorageServiceImpl storageImpl = new StorageServiceImpl();
		long startTime = System.currentTimeMillis();
		NotebookPageModel page = storageImpl.getNotebookPageExperimentInfo(nbref, sidentifier);//getNotebookPageExperimentInfo(nbref, sidentifier);
		System.out.println(System.currentTimeMillis() - startTime +
        " ms elapsed for [ NotebookPage Load "+nbref+"]");
		System.out.println("Loaded Page "+page.getUserName()+" : "+page.getNbRef().getNbRef());
		
		CommonUtils.printNBPageData(page);
   	 }
	
    public static void testGetStoicSearchProductBatch(String batchNum)throws Exception {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		ProductBatchModel prodModel = dao.loadProductBatchModel(batchNum);
		System.out.println("Load complete. Is value returnred---:"+prodModel != null?true:false);
	}
    
    public static void testgetAllRegisteredBatchesForJobidQuery() throws Exception
    {
    	//500893
    	DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		dao.getAllRegisteredBatchesWithJobID("500893");
		System.out.println("Load complete.");
		
    	
    }
    
    public static void testGetExperimentsBeignSigned() throws Exception
    {
    	DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		ArrayList list = dao.getExperimentsBeingSigned("USER2");
		System.out.println("Load complete."+list != null?list.size():0);
    }
    
    public static void testGetNotebookPageStatus() throws Exception
    {
    	
    	DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		SignaturePageVO vo =  dao.getNotebookPageStatus("SITE1", "87654321-0001", 1);
		System.out.println("testGetNotebookPageStatus.Load complete.");
    }
	public static void main(String[] args) throws Exception {
		
	//	TestNotebookLoadDAO.testLoadRegisteredBatches();
	//	testLoadCROPageSubmissionInfo();
		
		NotebookRef nbref = new NotebookRef();
		nbref.setNbNumber("87654321");
		nbref.setNbPage("41");
		//nbref.setNbNumber("87654321");
		//nbref.setNbPage("0004");//0931");
		testloadNBKPageOnServer(nbref);
		//testNotebookPageLoadFromStorageImpl(nbref);
		
		//testNotebookPageLoad(nbref);
		//testCreateConceptionExperiment();
		// System.out.println("-----Loading Notebook Page----");
		//testNotebookPageLoad(nbref);
		// //System.out.println("-----Loading Notebook Pages for User ---");
		// testNotebookPagesForUser("USER");
		// //System.out.println("-----Loading Design Synthesis Plan for Notebook Page ---");
		// testGetDesignSynthesisPlan("27071975-0102",1);
		// //System.out.println("-----Loading AllReactionSteps for NBRef---");
		// testGetAllReactionSteps("27071975-0102",1);
		// //System.out.println("-----Loading ReactionStepSummary for NBRef ---");
		// testGetReactionStepSummary("27071975-0102",1);
		// //System.out.println("-----Loading IntermediateReaction for User ---");
		// testGetIntermediateReactionStep("27071975-0102",1,0);
		// //System.out.println("-----Loading ReactionStepDetails for User ---");
		// testGetReactionStepDetails("27071975-0102",1);
		
		//testGetStoicSearchProductBatch("05092005-0001-10A1");
		
		//testgetAllRegisteredBatchesForJobidQuery();
		
		//testGetExperimentsBeignSigned();
		
		//testGetNotebookPageStatus();
	}
}
