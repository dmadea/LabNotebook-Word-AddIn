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

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.storage.DAOException;

public interface NotebookManager {
	/**
	 * Inserts NotebookPageModel using NotebookDAO, 
	 * This method all Database operations are in one transaction
	 * defined in storage-dao.xml as
	 * <prop key="insertNotebookPage*">
	 *	PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED
	 * </prop>
	 * 
	 * @param pageModel
	 * @throws DAOException
	 */
	public void insertNotebookPage(NotebookPageModel pageModel,HttpUserProfile userProfile) throws DAOException;
	
	/**
	 * Update NotebookPageModel using NotebookDAO, 
	 * This method all Database operations are in one transaction
	 * defined in storage-dao.xml as
	 * <prop key="updateNotebookPage*">
	 *	PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED
	 * </prop>
	 * 
	 * @param pageModel
	 * @throws DAOException
	 */
	public void updateNotebookPage(NotebookPageModel pageModel,HttpUserProfile userProfile) throws DAOException;
	
	public void deletePlateWells(String[] plateWellKeys, HttpUserProfile userProfile) throws DAOException; 
	
	/**
	 * Delete Plates using NotebookDAO, 
	 * This method all Database operations are in one transaction
	 * defined in storage-dao.xml as
	 * <prop key="deletePlates*">
	 *	PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED
	 * </prop>
	 * 
	 * @param plateKeys
	 * @throws DAOException
	 */
	public void deletePlates(String[] plateKeys,HttpUserProfile userProfile) throws DAOException; 
	
	
	//public void setUserProfile(HttpUserProfile userProfile);
}
