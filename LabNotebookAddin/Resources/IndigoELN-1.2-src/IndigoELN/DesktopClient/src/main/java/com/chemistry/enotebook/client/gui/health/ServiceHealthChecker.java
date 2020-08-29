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
/*
 * Created on Apr 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.health;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.DBHealthStatusVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceHealthChecker implements Runnable {
	private static final Log log = LogFactory.getLog(ServiceHealthChecker.class);
	
	private ServiceHealthStatus service;
	private boolean isAvailable = false;
	// private HealthCheckHandler healthCheckHandler;
	private boolean doingServiceCheck = false;
	private boolean doingDBCheck = false;

	public void run() {
		try {
			if (doingServiceCheck)
				serviceHealthCheck();
		} catch (Exception e) { log.debug("Service check failure.", e); }

		try {
			if (doingDBCheck)
				dbHealthCheck();
		} catch (Exception e) { /* Ignored */ }
	}

	private void serviceHealthCheck() {
		try {
			// get status of the specified service
			isAvailable = HealthCheckHandler.checkServiceHealth(service.getServiceName());
			if (isAvailable)
				service.setHealthStatus(ServiceHealthStatus.GOOD_STATUS);
			else
				service.setHealthStatus(ServiceHealthStatus.BAD_STATUS);
			
			// update the status of the service in the service list
			HealthCheckHandler.updateServiceStatus(service);
			
			// update the MasterController service availability flag
			if (MasterController.getHealthFlag().equals(ServiceHealthStatus.BAD_STATUS)) {
				return;
			} else if (service.getHealthStatus().equals(ServiceHealthStatus.BAD_STATUS)) {
				if (HealthCheckHandler.isMinimalService(service.getServiceName())) {
					if (!MasterController.getHealthFlag().equals(ServiceHealthStatus.BAD_STATUS)) {
						MasterController.setHealthFlag(ServiceHealthStatus.MINIMAL_STATUS);
					}
				} else {
					if(!"COMPOUND".equals(service.getServiceName())) {
						MasterController.setHealthFlag(ServiceHealthStatus.BAD_STATUS);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); /* Ignored */
		}
	}

	private void dbHealthCheck() {
		try {
			List<DBHealthStatusVO> dbvoList = HealthCheckHandler.checkDSHealth();
			HealthCheckHandler.setDbStatusList(dbvoList);
			
			// update the MasterController service availability flag
			for (int i = 0; dbvoList != null && i < dbvoList.size(); i++) {
				if (ServiceLocator.CEN_DS_JNDI.equals(dbvoList.get(i).getDbJNDI()) && dbvoList.get(i).getHealthStatus().equals(ServiceHealthStatus.BAD_STATUS)) {//added ServiceLocator.CEN_DS_JNDI.equals(...) for cen open source project
					MasterController.setHealthFlag(ServiceHealthStatus.BAD_STATUS);
					break;
				} else if (dbvoList.get(i).getHealthStatus().equals(ServiceHealthStatus.MINIMAL_STATUS)) {
					if (!MasterController.getHealthFlag().equals(ServiceHealthStatus.BAD_STATUS)) {
						MasterController.setHealthFlag(ServiceHealthStatus.MINIMAL_STATUS);
					}
				}
				if (MasterController.getHealthFlag().equals(ServiceHealthStatus.BAD_STATUS)) {
					return;
				}
			}
		} catch (Exception e) {
			log.warn("HealthCheck Status failure: ", e); /* Ignored */
		}
		if (MasterController.getHealthFlag().equals(ServiceHealthStatus.INIT_STATUS)) {
			MasterController.setHealthFlag(ServiceHealthStatus.GOOD_STATUS);
		}
	}

	/**
	 * @return Returns the service.
	 */
	public ServiceHealthStatus getService() {
		return service;
	}

	/**
	 * @param service
	 *            The service to set.
	 */
	public void setService(ServiceHealthStatus service) {
		this.service = service;
	}

	/**
	 * @return Returns the doingDBCheck.
	 */
	public boolean isDoingDBCheck() {
		return doingDBCheck;
	}

	/**
	 * @param doingDBCheck
	 *            The doingDBCheck to set.
	 */
	public void setDoingDBCheck(boolean doingDBCheck) {
		this.doingDBCheck = doingDBCheck;
	}

	/**
	 * @return Returns the doingServiceCheck.
	 */
	public boolean isDoingServiceCheck() {
		return doingServiceCheck;
	}

	/**
	 * @param doingServiceCheck
	 *            The doingServiceCheck to set.
	 */
	public void setDoingServiceCheck(boolean doingServiceCheck) {
		this.doingServiceCheck = doingServiceCheck;
	}
}