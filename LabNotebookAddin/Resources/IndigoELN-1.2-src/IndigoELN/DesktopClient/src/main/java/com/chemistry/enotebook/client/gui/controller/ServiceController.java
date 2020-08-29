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
package com.chemistry.enotebook.client.gui.controller;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.analyticalservice.exceptions.AnalyticalServiceAccessException;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.exceptions.RegistrationManagerInitException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.service.container.ContainerService;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import org.apache.commons.lang.StringUtils;

public class ServiceController {
	
	private static ContainerService containerService = null;
	private static StorageDelegate storageDelegate = null;
	private static RegistrationManagerDelegate registrationManagerDelegate = null;
	private static AnalyticalServiceDelegate analyticalServiceDelegate = null;
	private static ChemistryDelegate chemDelegate = null;	
	
	public static synchronized AnalyticalServiceDelegate getAnalyticalServiceDelegate(SessionIdentifier sessionId) throws AnalyticalServiceAccessException {
		if (analyticalServiceDelegate == null) {
			analyticalServiceDelegate = new AnalyticalServiceDelegate();
		}
		return analyticalServiceDelegate;
	}
	
	public static synchronized ContainerService getContainerService(SessionIdentifier sessionId) throws StorageInitException {
		if (storageDelegate == null) {
			storageDelegate = new StorageDelegate();
			containerService = (ContainerService) storageDelegate;
			return containerService;
		} else if (containerService == null) {
			containerService = (ContainerService) storageDelegate;
		}
		return containerService;
	}

	public static synchronized StorageDelegate getStorageDelegate(SessionIdentifier sessionId) throws StorageInitException {
		if (storageDelegate == null) {
			storageDelegate = new StorageDelegate();
		}
		return storageDelegate;
	}
	
	public static synchronized StorageDelegate getStorageDelegate() throws StorageInitException {
		if (storageDelegate == null) {
			storageDelegate = new StorageDelegate();
		}
		return storageDelegate;
	}

	public static synchronized RegistrationManagerDelegate getRegistrationManagerDelegate(SessionIdentifier sessionId, String employeeId) throws RegistrationManagerInitException {
		if (registrationManagerDelegate == null) {
			if (StringUtils.isBlank(employeeId)) {
				throw new RegistrationManagerInitException("CompoundManagement employee ID is not assigned. This may cause some issues while Registering components with CompoundManagement.");
			} else {
				sessionId.setCompoundManagementEmployeeID(employeeId);
			}
			registrationManagerDelegate = new RegistrationManagerDelegate();				
			return registrationManagerDelegate;
		} else
			return registrationManagerDelegate;
	}

	public static synchronized ChemistryDelegate getChemistryDelegate() throws ChemUtilInitException {
		if (chemDelegate == null) {
			chemDelegate = new ChemistryDelegate();
		}
		return chemDelegate;
	}
}
