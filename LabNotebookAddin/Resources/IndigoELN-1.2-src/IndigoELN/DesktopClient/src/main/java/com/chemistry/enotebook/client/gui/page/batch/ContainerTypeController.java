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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.experiment.datamodel.batch.ContainerType;

import java.util.ArrayList;
import java.util.List;

public class ContainerTypeController {
	private List allContainerTypeList;
	private List myContainerTypeList;

	public ContainerTypeController(String userId) {
		// 1 Get All Container Type List
		// 2 Get My Container List based on user Id
		ContainerType myContainerType1 = new ContainerType(11, 5, ContainerType.HORIZONTAL_DIRECTION, "My Favorite Container 1");
		ContainerType myContainerType2 = new ContainerType(12, 5, ContainerType.VERTICAL_DIRECTION, "My Favorite Container 2");
		ContainerType myContainerType3 = new ContainerType(13, 5, ContainerType.HORIZONTAL_DIRECTION, "My Favorite Container 3");
		myContainerTypeList = new ArrayList();
		myContainerTypeList.add(myContainerType1);
		myContainerTypeList.add(myContainerType2);
		myContainerTypeList.add(myContainerType3);
		ContainerType compoundManagementContainerType1 = new ContainerType(4, 5, ContainerType.HORIZONTAL_DIRECTION, "Compound Management Container 1");
		ContainerType compoundManagementContainerType2 = new ContainerType(5, 5, ContainerType.VERTICAL_DIRECTION, "Compound Management Container 2");
		ContainerType compoundManagementContainerType3 = new ContainerType(6, 5, ContainerType.HORIZONTAL_DIRECTION, "Compound Management Container 3");
		allContainerTypeList = new ArrayList();
		allContainerTypeList.add(compoundManagementContainerType1);
		allContainerTypeList.add(compoundManagementContainerType2);
		allContainerTypeList.add(compoundManagementContainerType3);
	}

	public List getAllContainerTypeList() {
		return allContainerTypeList;
	}

	public List getMyContainerTypeList() {
		return myContainerTypeList;
	}

	public boolean removeContainerTypeFromMyList(ContainerType ct) {
		// 1. Remove from my list on server side
		// todo
		// 2. Remove from my local Client
		myContainerTypeList.remove(ct);
		// update GUI in tree and all other occurance
		return true;
	}

	public boolean addContainerTypetoMyList(ContainerType ct) {
		// 1. add to my list on server side
		// todo
		// 2. add my local Client
		myContainerTypeList.add(ct);
		// update GUI in tree and all other occurance
		return true;
	}

	public List searchCompoundManagementCTs(String key) {
		ContainerType resultContainerType1 = new ContainerType(11, 5, ContainerType.HORIZONTAL_DIRECTION, "Result 1");
		ContainerType resultContainerType2 = new ContainerType(12, 5, ContainerType.VERTICAL_DIRECTION, "Result 2");
		ContainerType resultContainerType3 = new ContainerType(13, 5, ContainerType.HORIZONTAL_DIRECTION, "Result 3");
		List resultList = new ArrayList();
		resultList.add(resultContainerType1);
		resultList.add(resultContainerType2);
		resultList.add(resultContainerType3);
		return resultList;
	}
}
