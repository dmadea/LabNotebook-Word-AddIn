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


import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.dao.NotebookLoadDAO;
import com.chemistry.enotebook.storage.dao.NotebookUpdateDAO;
import com.chemistry.enotebook.storage.service.StorageServiceImpl;
import com.chemistry.enotebook.utils.CommonUtils;

import java.util.ArrayList;

public class TestNotebookUpdateDAO {

	public static void testUpadateNotebookPageHeader(NotebookRef nbref)throws Exception {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookLoadDAO dao =  factory.getNotebookLoadDAO();
		StorageServiceImpl ss = new StorageServiceImpl();
		NotebookPageModel page =  ss.getNotebookPageExperimentInfo(nbref, null);
		//NotebookPageModel page = dao.loadNotebookPage(nbref);
		//dao.getNotebookPageHeaderInfo(nbref);
		//List rxnSteps = dao.getAllReactionSteps(page.getKey());
		page.setLiteratureRef(" This is a literature testing");
		page.setProtocolID("code007");
		
		NotebookUpdateDAO updateDao = factory.getNotebookUpdateDAO();
		
		long startTime = System.currentTimeMillis();
		updateDao.updateNotebookPageHeader(page.getPageHeader());
		updateDao.updateReactionSteps(page.getKey(),page.getReactionSteps());
		System.out.println(System.currentTimeMillis() - startTime +
                    " ms elapsed for [ Update Notebook Page "+page.getNbRef()+ "]");;
	}

	public static void testUpadateNotebookPageStatus(String pageKey)throws Exception {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookUpdateDAO updateDao =  factory.getNotebookUpdateDAO();
		updateDao.updateNotebookPageStatus(pageKey, 
				CeNConstants.PAGE_STATUS_DELETED,CommonUtils.getCurrentTimestamp());
		
	}
	
	public static void testUpdateMonomerBatch(MonomerBatchModel monModel)
	{
		try
		{
			DAOFactory factory = DAOFactoryManager.getDAOFactory();
			NotebookUpdateDAO updateDao =  factory.getNotebookUpdateDAO();
		}catch(Exception e)
		{
			
		}
	}
	
	
	public static void testInsertProductBatchesThroughStoredProc()
	{
		NotebookUpdateDAO updateDao = null;
		DAOFactory factory = null;
		try
		{
			factory = DAOFactoryManager.getDAOFactory();
			updateDao =  factory.getNotebookUpdateDAO();
			String stepKey = "bfccfb52ac1eb85981b4ad4588399920e9054cf7";
			String pageKey="bfce6233ac1ef60b31fe4d0bb3718f01579febe6";
			String listKey="bfccf6b4ac1eb8597278565f4350975073d2634c";
			ArrayList pbList = new ArrayList();
			ProductBatchModel pb1 = new ProductBatchModel();
			pbList.add(pb1);
			updateDao.insertNewProductBatchesForPageUpdate(stepKey, pageKey, pbList, listKey);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			updateDao.releaseDaoFactory(factory);
		}
	}
	
	public static void testUpdateProductBatch()
	{
		DAOFactory factory = null;
		try
		{
			//prepare Pord model
			ParentCompoundModel parent = new ParentCompoundModel("fb503501ac1ebb8211d0f745b3718f01ea08a938");
			parent.setMolFormula("H2O2");
			parent.setMolWgt(23.45);
			parent.setModified(true);
			ProductBatchModel pbModel = new ProductBatchModel("fb503501ac1ebb823130e44cb3718f0171f4486e");
			pbModel.setCompound(parent);
			pbModel.setBatchType(BatchType.ACTUAL_PRODUCT);
			SaltFormModel sfm = new SaltFormModel("07");
			pbModel.setSaltForm(sfm);
			pbModel.setSaltEquivs(2.0);
			pbModel.setBatchNumber("66666669-0157-0011A1");
			System.out.println("Salt equiv:"+pbModel.getSaltEquivs());
			
			//call dao
			factory = DAOFactoryManager.getDAOFactory();
			NotebookUpdateDAO updateDao =  factory.getNotebookUpdateDAO();
			updateDao.updateProductBatch(pbModel);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void testUpdateMonomerBatch()
	{
		DAOFactory factory = null;
		try
		{
			//prepare Pord model
			ParentCompoundModel parent = new ParentCompoundModel("fb5034a3ac1ebb822197833eb3718f012cd7271a");
			parent.setMolFormula("H2O2");
			parent.setMolWgt(23.45);
			parent.setModified(true);
			MonomerBatchModel monModel = new MonomerBatchModel("fb5034a3ac1ebb8238eba9b0b3718f01dc62a5c1");
			monModel.setCompound(parent);
			monModel.setBatchType(BatchType.REAGENT);
			SaltFormModel sfm = new SaltFormModel("07");
			monModel.setSaltForm(sfm);
			monModel.setSaltEquivs(2.0);
			monModel.setTestedForChloracnegen(true);
			System.out.println("Salt equiv:"+monModel.getSaltEquivs());
			
			//call dao
			factory = DAOFactoryManager.getDAOFactory();
			NotebookUpdateDAO updateDao =  factory.getNotebookUpdateDAO();
			updateDao.updateMonomerBatch(monModel);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//NotebookRef nbref = new NotebookRef();
		//nbref.setNbNumber("87654321");
		//nbref.setNbPage("0182");
		//nbref.setVersion(1);

		//TestNotebookUpdateDAO.testUpadateNotebookPageHeader(nbref);
		
     	//	TestNotebookUpdateDAO.testUpadateNotebookPageStatus("f604fad4ac1ebba6914781c888d1a9e52a5bb0ae");

		
		TestNotebookUpdateDAO.testUpdateProductBatch();
		//TestNotebookUpdateDAO.testUpdateMonomerBatch();
		System.out.println("Test call done");
	}

}
