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
package com.chemistry.enotebook.service.container;

import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.storage.StorageException;

import java.util.List;


/**
 * Interface for all container related services.
 */
public interface ContainerService {

	/**
	 * Method returns all the containers user defined and saved from CompoundManagement by all users
	 */
	public  List getAllContainers() throws StorageException;

	/**
	 * Method returns all containers of a either user defined or not.
	 * All Plates or Vials or Racks etc
	 * 
	 * @param typeFilter
	 * 
	 */

	public List getContainers(boolean isUserDefined) throws StorageException;

	public List getUserContainers(String userId) throws StorageException;
	/**
	 * 
	 * @param matchingDescription
	 * @return
	 */
//	public Container[] searchContainerMatchingDescription(String matchingDescription);

//	public Container[] searchContainerMatchingXYPositions(int x, int y);

	public Container getContainer(String key) throws StorageException;
	
	public void createContainer(Container container) throws StorageException;
	
	public void createContainers(List containerList) throws StorageException;
	
	public void updateContainer(Container container) throws StorageException;
	
	public void updateContainers(List containerList) throws StorageException;
	
	public void removeContainer(String containerCode) throws StorageException; 	 	

	public void removeContainers(List containerCodeList) throws StorageException;

	public List searchForCompoundManagementContainers(Container container) throws StorageException;
}
