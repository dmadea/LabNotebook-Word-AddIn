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

import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.storage.dao.AnalysisDAO;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;

import java.util.List;

public class TestAnalysisDAO {
	
	
	public static void loadAnalysis() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AnalysisDAO dao =  factory.getAnalysisDAO();
		//load all with page_key
		List list = dao.loadAnalysis("055809c67a82a95f3101f4176b367877c1fae67d");
		System.out.println("AnalysisList loaded:"+list.size());
	}
	
	
	public static void insertAnalysis() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AnalysisDAO dao =  factory.getAnalysisDAO();
		//load all from this page_key
		List list = dao.loadAnalysis("753059d5ac1ebb8244c8aeb3b3718f013113c4b9");
		System.out.println("AnalysisList loaded:"+list.size());
		for(int i=0; i<list.size();i++){
			AnalysisModel analysis = (AnalysisModel)list.get(i);
			analysis.setTempKey(GUIDUtil.generateGUID(analysis));
			
		}
		//insert into this page
		dao.insertAnalysisList(list, "749c924eac1ebb822981e252b3718f01f3087af7");
		System.out.println("Insert Analysis success");
	}
	
	public static void deleteAnalysis() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AnalysisDAO dao =  factory.getAnalysisDAO();
//		load all with page_key
		List list = dao.loadAnalysis("753059d5ac1ebb8244c8aeb3b3718f013113c4b9");
		System.out.println("AnalysisList loaded:"+list.size());
		dao.deleteAnalysisWithBatching(list);
		System.out.println("AnalysisList deleted:"+list.size());
	}
	
	

	public static void updateAnalysis() throws Exception{
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		AnalysisDAO dao =  factory.getAnalysisDAO();
		List list = dao.loadAnalysis("749c924eac1ebb822981e252b3718f01f3087af7");
		System.out.println("AnalysisList loaded:"+list.size());
		
		//Insert the new model
		AnalysisModel model =(AnalysisModel)list.get(0);
		model.setLoadedFromDB(false);
		model.setTempKey(GUIDUtil.generateGUID(model));
		//update rest of the models
		for(int i=1; i<list.size();i++){
			AnalysisModel analysis = (AnalysisModel)list.get(i);
			analysis.setSiteCode("SITE3");
			analysis.setUserId("USER2");
			
		}
		dao.updateAnalysisList(list,"749c924eac1ebb822981e252b3718f01f3087af7");
		
		System.out.println("Update Analysis success");
	}
	
	public static void main(String[] args) throws Exception{
		//loadAnalysis();
		//insertAnalysis();
		updateAnalysis();
		//deleteAnalysis();
	}

}
