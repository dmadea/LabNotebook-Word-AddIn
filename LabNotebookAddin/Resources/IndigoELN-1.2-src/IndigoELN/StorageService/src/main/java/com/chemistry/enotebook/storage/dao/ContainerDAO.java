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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.compoundmanagement.CompoundManagementService;
import com.chemistry.enotebook.compoundmanagement.CompoundManagementServiceFactory;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.jdbc.insert.ContainerInsert;
import com.chemistry.enotebook.storage.jdbc.select.ContainerSelect;
import com.chemistry.enotebook.storage.jdbc.update.ContainerUpdate;
import com.chemistry.enotebook.storage.query.InsertQueryGenerator;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ContainerDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(ContainerDAO.class);
	
//	private DataSource compoundManagementDataSource = null;

	public ArrayList getAllContainers() throws DAOException {
		log.debug("Inside getAllContainers()");
		String selectSql = SelectQueryGenerator.getQueryForAllContainers();
		ArrayList resultList = null;
		try {
			ContainerSelect select = new ContainerSelect(this.getDataSource(), selectSql);
			List result = select.execute();
			resultList = new ArrayList(result);
			// resultList= ;
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return resultList;
	}

	public ArrayList getContainers(boolean isUserDefined) throws DAOException {
		log.debug("Inside getContainers(" + isUserDefined + ")");
		String selectSql = SelectQueryGenerator.getQueryForContainers(isUserDefined);
		ArrayList resultList = null;
		try {
			ContainerSelect select = new ContainerSelect(this.getDataSource(), selectSql);
			List result = select.execute();
			resultList = new ArrayList(result);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return resultList;
	}

	public ArrayList getUserContainers(String userId) throws DAOException {
		log.debug("Inside getUserContainers(" + userId + ")");
		String selectSql = SelectQueryGenerator.getQueryForUserContainers(userId);
		ArrayList resultList = null;
		try {
			ContainerSelect select = new ContainerSelect(this.getDataSource(), selectSql);
			List result = select.execute();
			resultList = new ArrayList(result);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return resultList;
	}

	public Container getContainer(String containerCode) throws DAOException {
		log.debug("Inside getContainer(" + containerCode + ")");
		String selectSql = SelectQueryGenerator.getQueryForOneContainer(containerCode);
		ArrayList resultList = null;
		try {
			ContainerSelect select = new ContainerSelect(this.getDataSource(), selectSql);
			List result = select.execute();
			resultList = new ArrayList(result);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}

		if (resultList.size() > 0) {
			if (resultList.size() == 1)
				return (Container) resultList.get(0);
			else
				throw new DAOException("More than one container found with containercode=" + containerCode);
		} else
			throw new DAOException("No container found with containercode=" + containerCode);

	}

	public Container getContainerForKey(String containerKey) throws DAOException {
		log.debug("Inside getContainer(" + containerKey + ")");
		String selectSql = SelectQueryGenerator.getQueryForOneContainerWithKey(containerKey);
		ArrayList resultList = null;
		try {
			ContainerSelect select = new ContainerSelect(this.getDataSource(), selectSql);
			List result = select.execute();
			resultList = new ArrayList(result);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}

		if (resultList.size() > 0) {
			if (resultList.size() == 1)
				return (Container) resultList.get(0);
			else
				throw new DAOException("More than one container found with containercode=" + containerKey);
		} else
			throw new DAOException("No container found with containercode=" + containerKey);

	}

	public void createContainer(Container container) throws DAOException {
		String insertSql = InsertQueryGenerator.getInsertContainerQuery();
		ContainerInsert insert = new ContainerInsert(this.getDataSource(), insertSql);
		try {

			insert.update(new Object[] { container.getKey(),// CONTAINER_KEY
					container.getContainerCode(),// CONTAINER_CODE
					container.getCreatorId(),// CREATOR_ID
					container.getContainerName(),// CONTAINER_NAME
					CommonUtils.toCharFromBoolean(container.isUserDefined()) + "", // IS_USER_DEFINED
					new Integer(container.getXPositions()),// X_POSITIONS
					new Integer(container.getYPositions()),// Y_POSITIONS
					container.getMajorAxis(), // MAJOR_AXIS
					container.getContainerType(), // CONTAINER_TYPE
					CommonUtils.getAsPipeSeperateValues(container.getSkippedWellPositions())
					});
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error.getMessage());
		}

	}

	public void createContainers(ArrayList containerList) throws DAOException {
		try {
			for (int i = 0; i < containerList.size(); i++) {
				createContainer((Container) containerList.get(i));
			}
		} catch (DAOException error) {
			throw error;
		}
	}

	public void updateContainer(Container container) throws DAOException {
		try {

			SqlUpdate su = new ContainerUpdate(this.getDataSource());

			su.update(new Object[] { container.getCreatorId(), // CREATOR_ID
					container.getContainerName(), // CONTAINER_NAME
					String.valueOf(CommonUtils.toCharFromBoolean(container.isUserDefined())), // IS_USER_DEFINED
					new Integer(container.getXPositions()), // X_POSITIONS
					new Integer(container.getYPositions()), // Y_POSITIONS
					container.getMajorAxis(), // MAJOR_AXIS
					container.getContainerType(), // CONTAINER_TYPE
					CommonUtils.getAsPipeSeperateValues(container.getSkippedWellPositions()),
					container.getContainerCode() // CONTAINER_CODE
					});
		} catch (Exception e) {
			log.error(e);
			throw new DAOException(e);
		}
	}

	/*
	 * Loops thru the list of containers and calls updateContainer
	 */
	public void updateContainers(ArrayList containerList) throws DAOException {
		try {
			for (int i = 0; i < containerList.size(); i++) {
				updateContainer((Container) containerList.get(i));
			}
		} catch (DAOException error) {
			throw error;
		}
	}

	public void removeContainer(String containerKey) throws DAOException {

		try {
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			SqlUpdate su = new SqlUpdate();
			su.setJdbcTemplate(jt);
			String removeSql = RemoveQueryGenerator.getRemoveContainerQuery(containerKey);
			su.setSql(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove Container with ContainerKey :" + containerKey);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
	}

	public void removeContainers(ArrayList<String> containerCodeList) throws DAOException {
		try {
			for (int i = 0; i < containerCodeList.size(); i++) {
				removeContainer(containerCodeList.get(i));
			}
		} catch (DAOException error) {
			throw error;
		}
	}

	public ArrayList<Container> searchForCompoundManagementContainers(Container c) throws DAOException, LoadServiceException, CompoundManagementServiceException {
		CompoundManagementService compoundManagementService = CompoundManagementServiceFactory.getService();
		ArrayList<Properties> propertyList = compoundManagementService.searchForCompoundManagementContainers(c.getContainerName());
		
		ArrayList<Container> containersList = new ArrayList<Container>(propertyList.size()); 
		for (Properties row : propertyList) {			

			Container container = new Container();
			container.setContainerCode(row.getProperty("CONTAINER_TYPE_CODE"));
			container.setContainerName(row.getProperty("CONTAINER_TYPE_DESCR"));
			container.setContainerType(row.getProperty("CONTAINER_TYPE"));
			container.setMajorAxis(row.getProperty("MAJOR_AXIS"));

			container.setXPositions("".equals(row.getProperty("X_POSITIONS")) ? Integer.valueOf(0) : Integer.valueOf(row.getProperty("X_POSITIONS")));
			container.setYPositions("".equals(row.getProperty("Y_POSITIONS")) ? Integer.valueOf(0) : Integer.valueOf(row.getProperty("Y_POSITIONS")));
			container.setSiteCode(row.getProperty("SITE_CODE"));
			if(container.getXPositions() == 0 || container.getYPositions() == 0)
			{
				container.setXPositions("".equals(row.getProperty("RACK_X_POSITIONS")) ? Integer.valueOf(0) : Integer.valueOf(row.getProperty("RACK_X_POSITIONS")));
				container.setYPositions("".equals(row.getProperty("RACK_Y_POSITIONS")) ? Integer.valueOf(0) : Integer.valueOf(row.getProperty("RACK_Y_POSITIONS")));	
			}
			container.setUserDefined(false);
			container.setLoadedFromDB(true);
			container.setModelChanged(false);
			containersList.add(container);
		}
		
		return containersList;
	}	
	private ArrayList sortTheContainers(List resultList ,Map map)
	{
		
		int size = resultList.size();
		ArrayList newlist = new ArrayList();
		String prevContcode = "";
		for(int i=0;i<size ; i ++)
		{
			Container cont = (Container)resultList.get(i);
			
			if(!prevContcode.equalsIgnoreCase(cont.getContainerCode()))
			{
			ArrayList list = (ArrayList)map.get(cont.getContainerCode());	
			if(list != null)
			{
				cont.setSkippedWellPositions(list);
			}
			newlist.add(cont);
			prevContcode = cont.getContainerCode();
			}
		}
		
		
		return newlist;
	}

}
