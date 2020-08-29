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

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.storage.StorageException;

import java.util.*;

/**
 * Mock service object for container services interface
 * 
 * 
 * 
 */
public class ContainerServiceMock implements ContainerService {

	private static HashMap compoundManagementContainersMap = new HashMap(10);
	private static HashMap userPrefContainerMap = new HashMap(10);

	public ArrayList getAllContainers() {
		ArrayList list = new ArrayList();
		list.addAll(compoundManagementContainersMap.values());
		return list;

	}

	
	private List getAllUserContainer()
	{
		ArrayList list = new ArrayList();
		list.addAll(userPrefContainerMap.values());
		return list;
	}
	public Container[] getAllContainers(String typeFilter) {
		ArrayList arrayList = new ArrayList();
		Set keyset = compoundManagementContainersMap.keySet();
		Iterator iter = keyset.iterator();
		while (iter.hasNext()) {
			Container c = (Container) compoundManagementContainersMap.get(iter.next());
			if (c.getContainerType() == typeFilter) {
				arrayList.add(c);
			}
		}
		return (Container[]) arrayList.toArray();
	}

	public Container[] searchContainerMatchingDescription(String matchingDescription) {
		/*
		 * ArrayList arrayList = new ArrayList(); Set keyset = compoundManagementContainersMap.keySet(); Iterator iter = keyset.iterator(); while
		 * (iter.hasNext()) { Container c = (Container)compoundManagementContainersMap.get(iter.next());
		 * if(c.getDescription().indexOf(matchingDescription)!= -1) { arrayList.add(c); } } return (Container[])arrayList.toArray();
		 */

		return (Container[]) compoundManagementContainersMap.values().toArray(new Container[] {});
	}

	public Container[] searchContainerMatchingXYPositions(int x, int y) {
		ArrayList arrayList = new ArrayList();
		Set keyset = compoundManagementContainersMap.keySet();
		Iterator iter = keyset.iterator();
		while (iter.hasNext()) {
			Container c = (Container) compoundManagementContainersMap.get(iter.next());
			if (c.getXPositions() == x && c.getYPositions() == y) {
				arrayList.add(c);
			}
		}
		return (Container[]) arrayList.toArray();
	}

	public Container getContainer(String containerCode) {
		// first check in CompoundManagement MAP
		Container allCont = (Container) compoundManagementContainersMap.get(containerCode);
		if (allCont == null) {
			allCont = (Container) userPrefContainerMap.get(containerCode);
		}
		return allCont;
	}

	public Container[] getMyPreferedContainers(String userid) {

		HashMap myMap = (HashMap) userPrefContainerMap.get("user");

		/*
		 * Set keyset = myMap.keySet(); Iterator iter = keyset.iterator(); while (iter.hasNext()) { Container c =
		 * (Container)compoundManagementContainersMap.get(iter.next()); if(c.getContainerType().equals(typeFilter)) { arrayList.add(c); } }
		 */
		// return (Container[]) myMap.values().toArray(new Container[]{});
		if (myMap != null)
			return (Container[]) myMap.values().toArray(new Container[] {});
		else
			return null;
	}

	public String addContainerToMyPreferenceList(Container container, String userid) {
		HashMap myMap = (HashMap) userPrefContainerMap.get(userid);
		myMap.put(container.getContainerCode(), container);
		userPrefContainerMap.put(userid, myMap);
		return "SUCCESS";
	}

	public String removeContainerFromMyPreferenceList(String code, String userid) {
		HashMap myMap = (HashMap) userPrefContainerMap.get(userid);
		myMap.remove(code);
		userPrefContainerMap.put(userid, myMap);
		return "SUCCESS";
	}

	static {
		Container c1 = new Container("123456asdf", "PS96", "96WELL", CeNConstants.CONTAINER_TYPE_PLATE,"user",12, 8, CeNConstants.CONTAINER_MAJOR_AXIS_X, false);

		compoundManagementContainersMap.put(c1.getContainerCode(), c1);

		/*
		 * compoundManagementContainersMap.put(c21.getContainerCode(),c21); compoundManagementContainersMap.put(c22.getContainerCode(),c22);
		 * compoundManagementContainersMap.put(c23.getContainerCode(),c23); compoundManagementContainersMap.put(c24.getContainerCode(),c24);
		 * compoundManagementContainersMap.put(c25.getContainerCode(),c25);
		 */

		HashMap myMap = new HashMap();

		userPrefContainerMap.put("user", myMap);

	}
	
	public void removeContainers(List containerCodeList)throws StorageException
	{
		
	}
	
	public List getContainers(boolean isUserDefined)throws StorageException
	{
		if(isUserDefined)
		{
		return getAllUserContainer();
		}else
		{
			ArrayList list =  getAllContainers();	
		 list.addAll(getAllUserContainer());
		 return list;
		}
	}
	

	public void createContainer(Container container)throws StorageException {
		compoundManagementContainersMap.put(container.getContainerCode(),container);
	}
		
	public void createContainers(List containerList)throws StorageException {
		int size = containerList.size();
		for(int i=0;i<size ; i ++)
		{
			Container container = (Container)containerList.get(i);
			compoundManagementContainersMap.put(container.getContainerCode(),container);
		}
	}
		
	public void updateContainer(Container container)throws StorageException {
		
	}
		
	public void updateContainers(List containerList)throws StorageException {
		
	}
		
	public void removeContainer(String containerCode)throws StorageException {
		
		compoundManagementContainersMap.remove(containerCode);
	}

	public List getUserContainers(String userId)throws StorageException
	{
		return getAllUserContainer();
	}
	
	public List searchForCompoundManagementContainers(Container container) throws StorageException
	{
		return getAllContainers();
	}	
}
