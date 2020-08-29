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

import com.chemistry.enotebook.domain.JobModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.storage.dao.JobDAO;

import java.util.List;

public class TestJobDAO {

	
	public static void testInsertPlateJob(JobModel[] jobs) throws DAOException {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		JobDAO dao = factory.getJobDAO();
		dao.insertRegistrationJobs(jobs);
	}
	
	public static void testUpdateJob(JobModel[] jobs) throws DAOException {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		JobDAO dao = factory.getJobDAO();
		dao.insertRegistrationJobs(jobs);
	}
	
	public static void testGetAllPlateJobs() throws DAOException {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		JobDAO dao = factory.getJobDAO();
		List jobs = dao.getAllRegistrationJobs();
		System.out.println("Plate Jobs Loaded :"+jobs.size());
		if(jobs.size() > 0){
			JobModel job = (JobModel)jobs.get(0);
			System.out.println("HttpMessage "+job.getUserMessage());	
		}
		
	}
	
	public static void testGetAllBatchJobs() throws DAOException {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		JobDAO dao = factory.getJobDAO();
		List jobs = dao.getAllBatchJobs("271734");
		System.out.println("Batch Jobs Loaded :"+jobs);
	}
	
	
	public static void testGetAllPendingCompoundRegistrationJobs() throws DAOException {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		JobDAO dao = factory.getJobDAO();
		dao.getAllPendingCompoundRegistrationJobs();
	}
	
	public static void testGetAllJobOffsets() throws DAOException {
		DAOFactory factory = DAOFactoryManager.getDAOFactory();
		JobDAO dao = factory.getJobDAO();
		dao.getAllCompoundRegistrationJobOffsets("168712");
	}
	
	
	public static void main(String[] str) throws Exception{
		
//		JobModel[] jobs = new JobModel[1];
//		jobs[0] = new JobModel("1234","platekey");
//		TestJobDAO.testInsertRegistrationJob(jobs);
		
		//TestJobDAO.testGetAllPendingCompoundRegistrationJobs();
		//TestJobDAO.testGetAllJobOffsets();
		//TestJobDAO.testGetAllPlateJobs();
		
		TestJobDAO.testGetAllBatchJobs();
		//TestJobDAO.testGetAllPendingCompoundRegistrationJobs();
	}
	
}
