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

import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;

public class DAOFactoryManager {
	
	private static final Log log = LogFactory.getLog(DAOFactoryManager.class);
	
	private static ApplicationContext context = null;
	private final static int POOL_SIZE = 10;
	
	// Holds the created DAOFactory Object which is not in use
	private static ArrayList<DAOFactory> freePool = new ArrayList<DAOFactory>(POOL_SIZE);
	// Holds the created DAOFactory Object which is being actively used
	private static ArrayList<DAOFactory> busyPool = new ArrayList<DAOFactory>(POOL_SIZE);
	
	private static final Object sync = new Object();
	
	/**
	 * Tries to return DAOFactory object from a free Pool if not available
	 * it create a new instance and returns it back.
	 * @return DAOFactory object 
	 * @throws DAOException
	 */
	public static DAOFactory getDAOFactory() throws DAOException{
		synchronized (sync) {
			DAOFactory factory = null;
			if(freePool.size() > 0){
				factory = freePool.remove(0);
				log.debug("DAOFactory Object taken from FREE POOL");
			} else {
				factory = lookUpDAOFactory();
				log.debug("DAOFactory Object created from Context LOOK-UP");
			}
			log.debug("DAOFactory Object moved to BUSY POOL");
			busyPool.add(factory);
			return factory;			
		}
	}
	
	/**
	 * Moves the object from busyPool to freePool, so as the be reused
	 * @param factoryObject
	 */
	public static void release(DAOFactory factoryObject){
		synchronized (sync) {
			if( busyPool.size() > 0 && factoryObject != null){
				int index = busyPool.indexOf(factoryObject);
				if(index >= 0){
					DAOFactory factory = busyPool.remove(index);
					freePool.add(factory);
					log.debug("DAOFactory Object released to FREE pool");
				}
			}			
		}
	}
	
	/**
	 * Looks up for DAOFactory object using the ApplicationContext from
	 * storage-xml.dao config file
	 * 
	 * @return DAOFactory Object
	 * @throws DAOException
	 */
	private static DAOFactory lookUpDAOFactory() throws DAOException{
		DAOFactory factory = null;
		try {
			factory = (DAOFactory) getContext().getBean("factoryDAO");
			log.debug("DAOFactory created!");
		} catch (Throwable error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}// end of catch
		return factory;
	}
	
	
	/**
	 * @return the context
	 */
	private static ApplicationContext getContext(){
		if (context == null) {
			log.debug("Creating Application Context from 'storage-dao.xml'");
			context = new ClassPathXmlApplicationContext("storage-dao.xml");
		}
		return context;
	}

}
