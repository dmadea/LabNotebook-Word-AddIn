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
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.dao.*;
import com.chemistry.enotebook.storage.service.StorageServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class TestPlateDAO {
	
	static String pageKey = "b8d294fa94a842cc11deee2e82717010af5b1e15";
	static String stepKey = "b8d2c6ec94a842cc5049859a40227880ca7e43a0";
	
	static HttpUserProfile userProfile = new HttpUserProfile("COMPNAME",65432,"USER");
	
	public static void testInsertMonomerPlates() {
		try { 
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			String nbK = "87654321";
			String page = "0209"; //CONCEPTION
			//String page = "0191"; //MED-CHEM
			String pageKey = "b8d294fa94a842cc11deee2e82717010af5b1e15";
			String stepKey = "b8d2c6ec94a842cc5049859a40227880ca7e43a0";
			int version =1; 
			NotebookRef nbRef = new NotebookRef(nbK,page,pageKey);
			nbRef.setVersion(version);

			MonomerPlate mPlate = new MonomerPlate();
			mPlate.setPlateType("MONOMER");
			mPlate.setPlateNumber("NUK87654321-0209-PL01");
			mPlate.setContainer(new Container("f0758f50ac1eb906122d189c163045406fe21cf7"));
			
			PlateWell plateWells[] = new PlateWell[]{getTestWell()};
			mPlate.setWells(plateWells);
			DAOFactory factory = DAOFactoryManager.getDAOFactory();
			PlateDAO dao = factory.getPlateDAO();
			List plates = new ArrayList();
			plates.add(mPlate);
			dao.insertMonomerPlates(pageKey, stepKey, plates);
			System.out.println("Monomer Plate inserted");
		}catch(Exception e)
		{ 
			e.printStackTrace(); 
		}
	}
	
	public static void testInsertProductPlates() {
		try { 
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			String nbK = "87654321";
			String page = "0209"; //CONCEPTION
			//String page = "0191"; //MED-CHEM
			String pageKey = "b8d294fa94a842cc11deee2e82717010af5b1e15";
			String stepKey = "b8d2c6ec94a842cc5049859a40227880ca7e43a0";
			int version =1; 
			NotebookRef nbRef = new NotebookRef(nbK,page,pageKey);
			nbRef.setVersion(version);

			ProductPlate pPlate = new ProductPlate();
			pPlate.setPlateType("PRODUCT");
			pPlate.setPlateNumber("NUK87654321-0209-PL01");
			pPlate.setContainer(new Container("f0758f50ac1eb906122d189c163045406fe21cf7"));
			
			PlateWell plateWells[] = new PlateWell[]{getTestWell()};
			pPlate.setWells(plateWells);
			DAOFactory factory = DAOFactoryManager.getDAOFactory();
			PlateDAO dao = factory.getPlateDAO();
			List plates = new ArrayList();
			plates.add(pPlate);
			dao.insertProductPlates(pageKey, stepKey, plates);

			System.out.println("Product Plate inserted");
		}catch(Exception e)
		{ 
			e.printStackTrace(); 
		}
	}
	public static void testInsertRegisteredPlates() {
		try { 
			int version =1; 
			SessionIdentifier sidentifier = new SessionIdentifier("SITE1","USER2","kicL/o26dciVm/9w+dvhXIiYBzP0VOoH",true);
			String nbK = "87654321";
			String page = "0209"; //CONCEPTION
			//String page = "0191"; //MED-CHEM
			NotebookRef nbRef = new NotebookRef(nbK,page,pageKey);
			nbRef.setVersion(version);

			ProductPlate rPlate = new ProductPlate();
			System.out.println("Product Plate inserted");
			rPlate.setPlateType("REGISTERED");
			rPlate.setPlateNumber("NUK87654321-0209-PL01");
			rPlate.setContainer(new Container("f0758f50ac1eb906122d189c163045406fe21cf7"));
			
			PlateWell plateWells[] = new PlateWell[]{getTestWell()};
			rPlate.setWells(plateWells);
			StorageServiceImpl ss = new StorageServiceImpl();
			DAOFactory factory = DAOFactoryManager.getDAOFactory();
			PlateDAO dao = factory.getPlateDAO();
			List plates = new ArrayList();
			plates.add(rPlate);
			dao.insertRegisteredPlates(pageKey,  plates);

			System.out.println("Registered Plate inserted");
		}catch(Exception e)
		{ 
			e.printStackTrace(); 
		}
	}
	private static PlateWell getTestWell(){
		PlateWell well = new PlateWell(new MonomerBatchModel("a42cce60ac1eb85911c1b26c3384231005e14c3f"));
		well.setWellNumber("1");
		AmountModel contAmt = new AmountModel();
		contAmt.setValue(12.21);
		contAmt.setUnit(new Unit2(UnitType.MASS,"MG"));
		well.setContainedWeightAmount(contAmt);
		AmountModel molarityAmt = new AmountModel(UnitType.MOLAR);
		contAmt.setValue(6.2);
		well.setMolarity(molarityAmt);
		return well;
	}

	public static void testLoadMonomerPlates() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		PlateDAO dao = factory.getPlateDAO();
		NotebookLoadDAO nblDao = factory.getNotebookLoadDAO();
		List list = dao.loadMonomerPlates(pageKey,stepKey,0);
		System.out.println(" Loaded Monomer Plates count : "+list.size());
	}
	public static void testLoadProductPlates() throws Exception{

		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		PlateDAO dao = factory.getPlateDAO();
		NotebookLoadDAO nblDao = factory.getNotebookLoadDAO();
		List list = dao.loadProductPlates(pageKey,stepKey,0);
		System.out.println(" Loaded Product Plates count : "+list.size());
	}
	/*
	public static void testLoadRegisteredPlates()throws Exception{

		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		PlateDAO dao = factory.getPlateDAO();
		List list = dao.loadRegisteredPlates(pageKey);
		System.out.println(" Loaded Registered Plates count : "+list.size());
	}
	*/
	public static void testLoadNotebookPageWithPlates() throws Exception{
		
		String nbK = "87654321";
		String page = "0209"; //CONCEPTION
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		NotebookDAO dao = factory.getNotebookDAO();
		NotebookRef ref = new NotebookRef(nbK,page);
		NotebookPageModel exp = dao.loadNotebookPage(ref);
		System.out.println("RPlates :"+exp.getRegisteredPlates().size());
		List list = exp.getReactionSteps();
		for(int i=0;i<list.size();i++){
			ReactionStepModel step = (ReactionStepModel)list.get(i);
			System.out.println("PPlates :"+step.getProductPlates().size());
			System.out.println("MPlates :"+step.getMonomerPlates().size());
		}
	}
	
	public static void testDeletePlates() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		PlateDAO dao = factory.getPlateDAO();
		dao.removePlate("c8082ed01a80803e597bd9f2904274402184e56a");
		System.out.println("Succefully removed PLATE");
	}
	
	public static void testUpdatePlates() throws Exception{
		
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		PlateDAO dao= factory.getPlateDAO();
    	dao.displayPlateCount();
	
		try { 
			NotebookDAO notebookDAO = factory.getNotebookDAO();
			
			//String nbK = "87654321";
			String nbK = "27071975";
			String page = "0229"; //CONCEPTION
			//String page = "0191"; //MED-CHEM
			final String pageKey = "e6e4672bac1eb85903d12df700677260981cb240";
			final String stepKey = "e6e4b2acac1eb859617032da07640390c9577343";
			int version =1; 
			NotebookRef nbRef = new NotebookRef(nbK,page,pageKey);
			nbRef.setVersion(version);
			CROPageInfo cro = new CROPageInfo(pageKey);
			cro.setCroID("cro_id");
			cro.setLoadedFromDB(false);
			
			final MonomerPlate mPlate = new MonomerPlate();
			mPlate.setPlateType("MONOMER");
			mPlate.setPlateNumber("NUK87654321-0209-PL01");
			mPlate.setContainer(new Container("f0758f50ac1eb906122d189c163045406fe21cf7"));
			PlateWell well = new PlateWell(new BatchModel("e6e49464ac1eb859662be88608313430f096e70a"));
			//well.setBatch();
			well.setWellNumber("1");
			PlateWell plateWells[] = new PlateWell[]{well};
			mPlate.setWells(plateWells);
			
			final ProductPlate pPlate = new ProductPlate();
			pPlate.setPlateType("PRODUCT");
			pPlate.setPlateNumber("NUK87654321-0209-PL02");
			pPlate.setPlateBarCode("NUK87654321-0209-PL02");
			pPlate.setContainer(new Container("f0758f50ac1eb906122d189c163045406fe21cf7"));
			PlateWell well2 = new PlateWell(new BatchModel("e6e49464ac1eb859662be88608313430f096e70a"));
			well2.setWellNumber("1");
			PlateWell plateWells2[] = new PlateWell[]{well2};
			pPlate.setWells(plateWells2);
			
			final ProductPlate rPlate = new ProductPlate();
			rPlate.setPlateType("REGISTERED");
			rPlate.setPlateNumber("NUK87654321-0209-PL03");
			rPlate.setContainer(new Container("f0758f50ac1eb906122d189c163045406fe21cf7"));
			PlateWell well3 = new PlateWell(new BatchModel("e6e49464ac1eb859662be88608313430f096e70a"));
			well3.setWellNumber("1");
			PlateWell plateWells3[] = new PlateWell[]{well3};
			rPlate.setWells(plateWells3);
			
			//StorageServiceImpl ss = new StorageServiceImpl();
			//MonomerPlate plates[] = new MonomerPlate[]{mPlate};
//			ss.addMonomerPlates(nbRef, stepKey, plates, sidentifier);
			//NotebookPageModel pageModel = new NotebookPageModel(nbRef,pageKey);
			NotebookPageModel pageModel = notebookDAO.loadNotebookPage(nbRef);
			pageModel.setCenVersion("V3.3");
			pageModel.setProjectCode("BBBBBB");
			ReactionStepModel stepModel = new ReactionStepModel(stepKey);
			pageModel.addReactionStep(stepModel);
			stepModel.addMonomerPlate(mPlate);
			stepModel.addProductPlate(pPlate);
			pageModel.addRegisteredPlate(rPlate);
			pageModel.setCroInfo(cro);
			pageModel.setPageStatus("OPEN");
			
//			NotebookManager nbkMgr = factory.getNotebookManager();
//			nbkMgr.updateNotebookPage(pageModel);
//			TransactionTemplate tt = new TransactionTemplate(factory.getTransactionManager());
			//tt.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
			//tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	        //tt.setTimeout(30); // 30 seconds
//	        tt.execute(new TransactionCallbackWithoutResult() {
//			    protected void doInTransactionWithoutResult(TransactionStatus status) {
//			    	try
//					{
//			    		PlateDAO plateDAO = factory.getPlateDAO();
//			    	
//			    		plateDAO.insertRegisteredPlate(pageKey, rPlate);
//			    		plateDAO.insertMonomerPlate(pageKey, stepKey, mPlate);
//			    		plateDAO.insertProductPlate(pageKey, stepKey, pPlate);
//					} catch (Exception daoe) {
//			    			daoe.printStackTrace();
//			    	}
//			    }
//			});
		
	    }catch(Exception e)
		{ 
			e.printStackTrace(); 
		}
	    dao.displayPlateCount();
		
		System.out.println("Updated the List successfully..");
	}
	
	public static void main(String[] args) throws Exception {
		
//		TestPlateDAO.testInsertMonomerPlates();
//		TestPlateDAO.testInsertProductPlates();
//		TestPlateDAO.testInsertRegisteredPlates();
//		
//		TestPlateDAO.testLoadMonomerPlates();
//		TestPlateDAO.testLoadProductPlates();
//		TestPlateDAO.testLoadRegisteredPlates();
		
//		TestPlateDAO.testDeletePlates();
		
		TestPlateDAO.testUpdatePlates();
		//TestPlateDAO.testLoadNotebookPageWithPlates();
		
	}
	
}
