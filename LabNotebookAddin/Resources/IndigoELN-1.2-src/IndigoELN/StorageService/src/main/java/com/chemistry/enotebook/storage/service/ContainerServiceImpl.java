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
package com.chemistry.enotebook.storage.service;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.dao.ContainerDAO;
import com.chemistry.enotebook.storage.dao.DAOFactory;
import com.chemistry.enotebook.storage.dao.DAOFactoryManager;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class ContainerServiceImpl  {// implements ContainerService{
	
	private static final Log log = LogFactory.getLog(ContainerServiceImpl.class);
	//private DAOFactory daoFactory = new DAOFactory();
	
	public ArrayList getAllContainers() throws StorageException 
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory(); 
			ContainerDAO dao = daoFactory.getContainerDAO();	
			return dao.getAllContainers();
		}catch(DAOException de)
		{
			log.error("DAO exception:"+CommonUtils.getStackTrace(de));
			throw new StorageException("Error getting all containers",de);
		} finally{
			DAOFactoryManager.release(daoFactory);
		}
	}

	public ArrayList getContainers(boolean isUserDefined)throws StorageException
	{
		DAOFactory daoFactory = null;
		
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			return dao.getContainers(isUserDefined);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error getting containers",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}

	public ArrayList getUserContainers(String userId)throws StorageException 
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			return dao.getUserContainers(userId);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error getting user containers",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public Container getContainer(String key)throws StorageException
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			return dao.getContainer(key);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error getting container for key"+key ,de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public void createContainer(Container container)throws StorageException
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			dao.createContainer(container);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error creating Container",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public void createContainers(List containerList) throws StorageException
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			dao.createContainers((ArrayList) containerList);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error creating Containers",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public void updateContainer(Container container)throws StorageException 
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			dao.updateContainer(container);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error updating container",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public void updateContainers(List containerList)throws StorageException
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();	
			ContainerDAO dao = daoFactory.getContainerDAO();	
			dao.updateContainers((ArrayList) containerList);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error updating containers",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public void removeContainer(String containerKey)throws StorageException 
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			dao.removeContainer(containerKey);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error removing container "+containerKey,de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	} 	 	

	public void removeContainers(List containerCodeList)throws StorageException
	{
		DAOFactory daoFactory = null;
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			dao.removeContainers((ArrayList<String>) containerCodeList);
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error removing containers",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}

	public ArrayList<Container> searchForCompoundManagementContainers(Container container) throws StorageException, LoadServiceException, CompoundManagementServiceException 
	{
		DAOFactory daoFactory = null;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ContainerServiceImpl.searchForCompoundManagementContainers()");
		try
		{
			daoFactory=DAOFactoryManager.getDAOFactory();
			ContainerDAO dao = daoFactory.getContainerDAO();	
			ArrayList<Container> list =  dao.searchForCompoundManagementContainers(container);
			stopwatch.stop();
		return list;
		}catch(DAOException de)
		{
		log.error("DAO exception:"+CommonUtils.getStackTrace(de));
		throw new StorageException("Error searching CompoundManagement containers",de);
		}finally{
			DAOFactoryManager.release(daoFactory);
		}
	}

}
