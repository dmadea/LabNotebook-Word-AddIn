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

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.dao.ContainerDAO;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;

import java.util.ArrayList;
import java.util.List;

public class TestContainerDAO {

	DAOFactory factory = null;
	{
		try{
			factory = DAOFactoryManager.getDAOFactory();
		}catch(Exception erre){
			factory = null;
		}
	}
	public void testGetAllContainers() throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testGetAllContainers-----");
		ArrayList list = dao.getAllContainers();
		System.out.println("ContainerList :"+list);
	}

	public void testGetContainers()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testGetContainers-----");
		ArrayList list = dao.getContainers(true);
		System.out.println("ContainerList (UserDefined) :"+list);
		list = dao.getContainers(false);
		System.out.println("ContainerList (GSM) :"+list);
	}

	public void testGetUserContainers()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testGetUserContainers-----");
		ArrayList list = dao.getUserContainers("USER2");
		System.out.println("ContainerList (USER2) :"+list);
		list = dao.getUserContainers("USER");
		System.out.println("ContainerList (USER) :"+list);
		
	}

	public void testGetContainer()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testGetContainer-----");
		Container c =dao.getContainer("PS96");
		System.out.println("Container :"+c);
		Container c1 =dao.getContainer("PD96");
		System.out.println("Container :"+c1);
		Container c2 =dao.getContainer("2MLTITR");
		System.out.println("Container :"+c2);
	}
	
	public void testCreateContainer()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testCreateContainer-----");
		
		Container c1 = new Container("123456asdf", "PS96", "Shallow 96-well plate", CeNConstants.CONTAINER_TYPE_PLATE,"USER2",12, 8, CeNConstants.CONTAINER_MAJOR_AXIS_X, 
				false);
		Container c2 = new Container("123456asdg","PD96", "Deep 96-well plate",CeNConstants.CONTAINER_TYPE_PLATE,"USER2", 12, 8, CeNConstants.CONTAINER_MAJOR_AXIS_X, false);
		Container c3 = new Container("123456asdh","2MLTITR", "Shallow 96-well plate", CeNConstants.CONTAINER_TYPE_PLATE,"USER",12, 8, CeNConstants.CONTAINER_MAJOR_AXIS_X,true);
		dao.createContainer(c1);
		dao.createContainer(c2);
		dao.createContainer(c3);
	}
	
	public void testCreateContainers()throws DAOException{
		System.out.println("----testCreateContainers-----");
		ContainerDAO dao = factory.getContainerDAO();
		ArrayList containerList = new ArrayList(3);
		Container c33 = new Container("123456asdv","ASDI-60", "16 mL = 21 x 70 mm",CeNConstants.CONTAINER_TYPE_VIAL,"USER", 0, 0, CeNConstants.CONTAINER_MAJOR_AXIS_NONE,true);
		Container c34 = new Container("123456asdw","8ST", "1-DRAM (4 ML) SEPTA SCREW CAP VIAL",CeNConstants.CONTAINER_TYPE_VIAL,"USER1", 0, 0, CeNConstants.CONTAINER_MAJOR_AXIS_NONE, false);
		Container c35 = new Container("123456asdx","1ST", "30 ML SEPTA SCREW CAP VIAL", CeNConstants.CONTAINER_TYPE_VIAL,"USER",0, 0, CeNConstants.CONTAINER_MAJOR_AXIS_NONE, false);
		containerList.add(c33);
		containerList.add(c34);
		containerList.add(c35);
		dao.createContainers(containerList);
		List list = dao.getAllContainers();
		System.out.println("ContainerList size :"+list.size());
	}
	
	public void testUpdateContainer()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testUpdateContainer-----");
		Container c35 = new Container("123456asdx","1ST", "30 ML SEPTA SCREW CAP VIAL", CeNConstants.CONTAINER_TYPE_VIAL,"USER",20, 10, CeNConstants.CONTAINER_MAJOR_AXIS_NONE, false);
		dao.updateContainer(c35);
		Container c = dao.getContainer("1ST");
		System.out.println("Updated Container  :"+c);
	}
	
	
	public void testUpdateContainers()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testUpdateContainers-----");
		Container c33 = new Container("123456asdv","ASDI-60", "16 mL = 21 x 70 mm",CeNConstants.CONTAINER_TYPE_VIAL,"USER1", 33, 44, CeNConstants.CONTAINER_MAJOR_AXIS_NONE,true);
		Container c34 = new Container("123456asdw","8ST", "1-DRAM (4 ML) SEPTA SCREW CAP VIAL",CeNConstants.CONTAINER_TYPE_VIAL,"USER", 11, 22, CeNConstants.CONTAINER_MAJOR_AXIS_NONE, false);
		ArrayList containerList = new ArrayList(3);
		containerList.add(c33);
		containerList.add(c34);
		dao.updateContainers(containerList);
		Container c = dao.getContainer("ASDI-60");
		System.out.println("Updated Container  :"+c);
		c = dao.getContainer("8ST");
		System.out.println("Updated Container  :"+c);
		
	}
	
	public void testRemoveContainer()throws DAOException{
		System.out.println("----testRemoveContainer-----");
		ContainerDAO dao = factory.getContainerDAO();
		List list = dao.getAllContainers();
		System.out.println("List --:"+list.size());
		dao.removeContainer("8ST");
		list = dao.getAllContainers();
		System.out.println("List --:"+list.size());
	}

	public void testRemoveContainers()throws DAOException{
		ContainerDAO dao = factory.getContainerDAO();
		System.out.println("----testRemoveContainers-----");
		ArrayList list = dao.getAllContainers();
		System.out.println("List --:"+list.size());
		list = new ArrayList(2);
		list.add("123456asdw");
		list.add("123456asdv");
		dao.removeContainers(list);
		list = dao.getAllContainers();
		System.out.println("List --:"+list.size());
		
	}

	public void testSearchForCompoundManagementContainers()throws DAOException, LoadServiceException, CompoundManagementServiceException{
		ContainerDAO dao = factory.getContainerDAO();
		
		System.out.println("----testSearchForCompoundManagementContainers-----");
		Container c = new Container();
		c.setContainerCode("2HR");
		ArrayList list = dao.searchForCompoundManagementContainers(c);
		System.out.println(list);
	}
	
	public static void main(String[] str){
		try{
			TestContainerDAO testDao= new TestContainerDAO();
//			testDao.testCreateContainer();
//			testDao.testGetContainer();
//			testDao.testGetAllContainers();
//			testDao.testGetContainers();
//			testDao.testGetUserContainers();
//			testDao.testCreateContainers();
//			testDao.testUpdateContainer();
//			testDao.testUpdateContainers();
//			testDao.testRemoveContainers();
			testDao.testSearchForCompoundManagementContainers();
		}catch(Exception err){
			err.printStackTrace();
		}
	}
}
