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


import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.dao.CroPageDAO;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestCroPageDAO {
	
	
	public static CROPageInfo getCroPage(String pageKey, String reqId){
		CROPageInfo croPage = new CROPageInfo(pageKey);
		croPage.setCroID("cro_id");
		croPage.setCroDisplayName("disp_name");
		croPage.getCroChemistInfo().setCroChemistID("chem_id");
		croPage.getCroChemistInfo().setCroChemistDisplayName("chem_name");
		croPage.setCroAplicationSourceName("Test Application Source");
		croPage.setRequestId(reqId);
		
		return croPage;
	}

	public static void testInsertCROPageInfo() throws DAOException {

		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		CroPageDAO croDAO = factory.getCroPageDAO();
		croDAO.insertCROPageInfo(getCroPage("4dd99910ac1eb859516b964138382680e137afb7","REQ_001"));
		System.out.println("CRO One Inserted !!");
		croDAO.insertCROPageInfo(getCroPage("4da1fd81ac1eb859885aa94a942789401b92a742","REQ_001"));
		System.out.println("CRO Two Inserted !!");
		croDAO.insertCROPageInfo(getCroPage("4d9eaf81ac1eb85932f5f736252561808e2fdb1c","REQ_002"));
		System.out.println("CRO Three Inserted !!");
		croDAO.insertCROPageInfo(getCroPage("4d9ab8aaac1eb85931cb087780992060881625f6","REQ_002"));
		System.out.println("CRO Four Inserted !!");
		DAOFactoryManager.release(factory);
	}
	
	/**
	 * @return List containing vendorId and vendorDisplayName
	 */
	public static void testGetAllCros() throws DAOException{
		try{
//			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
//			StorageDelegate storageObj = new StorageDelegate(sidentifier);
			DAOFactory factory = DAOFactoryManager.getDAOFactory();
			CroPageDAO croDAO = factory.getCroPageDAO();
			String user = croDAO.getDataSource().getConnection().getMetaData().getUserName();
			System.out.println("User -"+user);
			String owner = user.replaceFirst("USER", "OWNER");
			System.out.println("Owner -"+owner);
			List map = croDAO.getAllCros();
//			ArrayList map = storageObj.getAllCROs();
			System.out.println("CRO List  "+map);
			DAOFactoryManager.release(factory);
		}catch(Exception error){
			error.printStackTrace();
		}
	}
	/**
	 * @param croId 
	 * @return List containing ChemistId and ChemistDisplayName
	 */
	public static void testGetAllChemistsForCro() throws DAOException{
//		DAOFactory factory = DAOFactory.getInstance();
//		CroPageDAO croDAO = factory.getCroPageDAO();
//		Map map = croDAO.getAllChemistsForCro("SITE1");
//		System.out.println(" CRO chemist List "+map);
		try{
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			StorageDelegate storageObj = new StorageDelegate();
			ArrayList map = (ArrayList) storageObj.getAllChemistsUnderCRO("SITE1", null);
			System.out.println("CRO Chemist List  "+map);
		}catch(Exception error){
			error.printStackTrace();
		}
	}
	
	/**
	 * @param chemistId 
	 * @return List containing Objects with nbkRef+CROSubmissionPage info.
	 */
	public static void testGetAllPagesForChemist() throws DAOException{
//		DAOFactory factory = DAOFactory.getInstance();
//		CroPageDAO croDAO = factory.getCroPageDAO();
//		List list = croDAO.getAllPagesForChemist("USER");
		
		try{
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			StorageDelegate storageObj = new StorageDelegate();
			List list = storageObj.getAllPagesWithSummaryUnderChemist("USER", null);
			System.out.println("Pages for CHEMIST  List size "+list.size());
			for(int i=0;i<list.size();i++){
				System.out.println(list.get(i));
			}
		}catch(Exception error){
			error.printStackTrace();
		}
	}
	
		
	/**
	 * @param cro
	 * @return List of CROSubmissionPageInfo objects
	 */
	public static void testSearchForCROPages() throws DAOException{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		CroPageDAO croDAO = factory.getCroPageDAO();
		List list = croDAO.searchForCROPages(getCroPage("4dd99910ac1eb859516b964138382680e137afb7","REQ_001"));
		System.out.println("SEARCH Pages List size "+list.size());
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
		DAOFactoryManager.release(factory);
	}
	
	/**
	 * @param requestId
	 * @return boolean status of requestid availability
	 */
	public static void testIsCroRequestIdAvailable()
	throws DAOException{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		CroPageDAO croDAO = factory.getCroPageDAO();
		System.out.println("Search Result :"+croDAO.isCroRequestIdAvailable("REQ_004"));
		DAOFactoryManager.release(factory);
	}
	
	public static void testDeletePages() throws DAOException{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		CroPageDAO croDAO = factory.getCroPageDAO();
		croDAO.deletePages("REQ_001");
		System.out.println("CRO ONE DELETED!!");
		croDAO.deletePages("REQ_002");
		System.out.println("CRO TWO DELETED!!");
		DAOFactoryManager.release(factory);
	}

	public static void testGetAllRequestIdsForChemist() throws DAOException{
//		DAOFactory factory = DAOFactory.getInstance();
//		CroPageDAO croDAO = factory.getCroPageDAO();
//		Map map = croDAO.getAllChemistsForCro("SITE1");
//		System.out.println(" CRO chemist List "+map);
		try{
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			StorageDelegate storageObj = new StorageDelegate();
			ArrayList map = (ArrayList)storageObj.getAllRequestIdsForChemist("USER", null);
			System.out.println("CRO Chemist List  "+map);
		}catch(Exception error){
			error.printStackTrace();
		}
	}
	
	public static void testGetAllNotebooksForChemistId( SessionIdentifier identifier) {
		try{
			StorageDelegate storageObj = new StorageDelegate();
			List result = storageObj.getAllNotebooksForChemistId("UNKNOWN",null);
			System.out.println("Notebooks Result  -:"+result);
		}catch(Exception err){
			err.printStackTrace();
		}
	}
	
	/**
	 * @param requestId
	 * @param identifier
	 * @return modifiedDate
	 * @throws RemoteException
	 */
	public static void testGetCROModifiedDate(SessionIdentifier identifier) {
		try{
			StorageDelegate storageObj = new StorageDelegate();
			Date result = storageObj.getCROModifiedDate("37", null);
			System.out.println("Modfied Date Result -:"+result);
		}catch(Exception err){
			err.printStackTrace();
		}
	}
	
	/**
	 * @param nbkRef
	 * @param identifier
	 * @return CROPageInfo Object corresponding to NotebookRef 
	 * @throws RemoteException
	 */
	public static void testGetCroPageForNBK(SessionIdentifier identifier) throws Exception{
		try{
			StorageDelegate storageObj = new StorageDelegate();
			CROPageInfo result = storageObj.getCroPageForNBK(new NotebookRef("00111638","115"), null);
			System.out.println("CROPageInfo Result -:"+result);
		}catch(Exception err){
			err.printStackTrace();
		}
	}
	
	public static void testGetAllPagesForNotebook( SessionIdentifier identifier) {
		try{
			StorageDelegate storageObj = new StorageDelegate();
			List result = storageObj.getAllPagesForNotebook("00111638", null);
			System.out.println("CROPageInfo Result -:"+result);
		}catch(Exception err){
			err.printStackTrace();
		}
	}
	
	
	public static void main(String[] str) throws Exception{
		SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
//		TestCroPageDAO.testDeletePages();
//		TestCroPageDAO.testInsertCROPageInfo();
//		TestCroPageDAO.testGetAllCros();
//		TestCroPageDAO.testGetAllChemistsForCro();
//		TestCroPageDAO.testGetAllPagesForChemist();
//		TestCroPageDAO.testGetAllRequestIdsForChemist();
//		TestCroPageDAO.testSearchForCROPages();
//		testIsCroRequestIdAvailable();
		TestCroPageDAO.testGetCroPageForNBK(sidentifier);
		TestCroPageDAO.testGetCROModifiedDate(sidentifier);
		TestCroPageDAO.testGetAllNotebooksForChemistId(sidentifier);
		TestCroPageDAO.testGetAllPagesForNotebook(sidentifier);
	}
	
}
