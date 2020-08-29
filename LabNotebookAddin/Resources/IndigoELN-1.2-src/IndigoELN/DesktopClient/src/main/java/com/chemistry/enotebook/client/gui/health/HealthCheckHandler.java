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
 * Created on Mar 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.health;

import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.chloracnegen.delegate.ChloracnegenServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.delegate.DesignServiceDelegate;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundMgmtServiceException;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.esig.delegate.SignatureDelegate;
import com.chemistry.enotebook.reagent.delegate.ReagentMgmtServiceDelegate;
import com.chemistry.enotebook.registration.delegate.RegistrationDelegateException;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.session.DBHealthStatusVO;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.common.chemistry.codetable.delegate.CodeTableDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class HealthCheckHandler {
	public static final Log log = LogFactory.getLog(HealthCheckHandler.class);
	public static final String SESSION_SERVICE = "SESSION";
	public static final String CODETABLE_SERVICE = "CODETABLE";
	public static final String CHEMISTRY_SERVICE = "CHEMISTRY";
	public static final String STORAGE_SERVICE = "STORAGE";
	public static final String ANALYTICAL_SERVICE = "AnalyticalService";
	public static final String REAGENT_SERVICE = "REAGENT";
	public static final String TIES_SERVICE = "TIES";
	public static final String CALCULATION_SERVICE = "CALCULATION";
	public static final String DSP_SERVICE = "SYNTHESIS_PLAN";
	public static final String COMPOUND_SERVICE = "COMPOUND";
	public static final String 		CLS_SERVICE = "CLS";
	public static final String 		SSS_SERVICE = "SSS";
	public static final String 		COMPOUND_MANAGEMENT_CONT_SERVICE = "COMPOUND_MANAGEMENT_CONT";
	public static final String 		COMPOUND_MANAGEMENT_ORD_SERVICE = "COMPOUND_MANAGEMENT_ORDER";
	public static final String REGISTRATION_SERVICE = "REGISTRATION";
	public static final String 		COMPOUND_REGISTRATION_SERVICE = "CompoundRegistration";
	public static final String 		UNIQUENESS_SERVICE = "UNIQUENESS";
	public static final String COMPOUND_AGGREGATION_SERVICE = "CompoundAggregation";
	public static final String PURIFICATION_SERVICE_SERVICE = "PurificationService";
	public static final String ESIGNATURE_SERVICE = "ESIGNATURE";
	public static final String 		SAFESIGN_SERVICE = "Signature Service";
	public static final String 		ARCHIVE_SERVICE = "ARCHIVE";
	public static final String EXTERNAL_COLLABORATOR_SERVICE = "ExternalCollaborator";

	public static List<String> serviceList = new ArrayList<String>();
	public static List<ServiceHealthStatus> serviceStatusList = new ArrayList<ServiceHealthStatus>();
	public static List<DBHealthStatusVO> dbStatusList = new ArrayList<DBHealthStatusVO>();
	
	private static List<SystemHealthStatusChangeListener> systemHealthStatusChangeListeners = new ArrayList<SystemHealthStatusChangeListener>();
	private static List<String> minimalImpactServices = new Vector<String>();
		
	public HealthCheckHandler() { }
	
	private static String getHealthSiteCode() {
		String result = "SITE1";
		try {
			// Retrieve the last Site code used and use that for health checks,  Assume GTN first time
			Properties properties = new Properties();
			properties.load(new FileInputStream(MasterController.getCeNPropertiesFile()));
			String siteCode = properties.getProperty("siteCode");
			if (siteCode != null && siteCode.length() > 0) result = siteCode;
		} catch (Exception e) { /* Ignored */ }
		return result;
	}

	public static boolean isMinimalService(String service) {
		return minimalImpactServices.contains(service);
	}

	public static List<DBHealthStatusVO> checkDSHealth() {
		SessionTokenDelegate sessionTokenDelegate;
		try {
			sessionTokenDelegate = new SessionTokenDelegate();
			return sessionTokenDelegate.checkSystemDBHealth();
		} catch (Exception e) {
			log.error("Check Session Token Delegate Health Failed.", e);
			return null;
		}
	}

	public static boolean checkServiceHealth(String serviceName) {
		boolean serviceAvailable = false;
		try {
			if (serviceName.equals(SESSION_SERVICE)) {
				new SessionTokenDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(CODETABLE_SERVICE)) {
				new CodeTableDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(CHEMISTRY_SERVICE)) {
				// TODO: ChemistryDelegate chemistryDelegate = new ChemistryDelegate();
				// serviceAvailable = chemistryDelegate.checkHealth();
				// chemistryDelegate.dispose();
				serviceAvailable = true;
			} else if (serviceName.equals(STORAGE_SERVICE)) {
				new StorageDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(ANALYTICAL_SERVICE)) {
				AnalyticalServiceDelegate AnalyticalServiceDelegate = new AnalyticalServiceDelegate();
				serviceAvailable = AnalyticalServiceDelegate.checkHealth();
			} if (serviceName.equals(REAGENT_SERVICE)) {
				ReagentMgmtServiceDelegate rgtDel = new ReagentMgmtServiceDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(TIES_SERVICE)) {
				ReagentMgmtServiceDelegate rgtDel = new ReagentMgmtServiceDelegate();
				serviceAvailable = rgtDel.getHazardInfoHealth();
			} else if (serviceName.equals(CALCULATION_SERVICE)) {
				new ChloracnegenServiceDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(DSP_SERVICE)) {
				DesignServiceDelegate dsp = new DesignServiceDelegate();
				serviceAvailable = dsp.isAvailable();
			} else if (serviceName.equals(COMPOUND_SERVICE)) {
				CompoundMgmtServiceDelegate compoundMgmtServiceDelegate = new CompoundMgmtServiceDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(CLS_SERVICE)) {
				CompoundMgmtServiceDelegate compoundMgmtServiceDelegate;
				try {
					compoundMgmtServiceDelegate = new CompoundMgmtServiceDelegate();
					serviceAvailable = compoundMgmtServiceDelegate.checkHealth();
				} catch (Exception e1) {
					serviceAvailable = false;
				}
			} else if (serviceName.equals(SSS_SERVICE)) {
				CompoundMgmtServiceDelegate compoundMgmtServiceDelegate;
				try {
					compoundMgmtServiceDelegate = new CompoundMgmtServiceDelegate();
					serviceAvailable = compoundMgmtServiceDelegate.checkHealth();
				} catch (CompoundMgmtServiceException e1) {
					serviceAvailable = false;
				}
			} else if (serviceName.equals(COMPOUND_MANAGEMENT_CONT_SERVICE)) {
				RegistrationManagerDelegate regManager = new RegistrationManagerDelegate();
				serviceAvailable = regManager.isCompoundManagementContainerApiAvailable(getHealthSiteCode());
			} else if (serviceName.equals(COMPOUND_MANAGEMENT_ORD_SERVICE)) {
				RegistrationManagerDelegate regManager = new RegistrationManagerDelegate();
				serviceAvailable = regManager.isCompoundManagementContainerApiAvailable(getHealthSiteCode());
			} else if (serviceName.equals(REGISTRATION_SERVICE)) {
				new RegistrationServiceDelegate();
				serviceAvailable = true;
			} else if (serviceName.equals(COMPOUND_REGISTRATION_SERVICE)) {
				RegistrationServiceDelegate registrationServiceDelegate;
				try {
					registrationServiceDelegate = new RegistrationServiceDelegate();
					serviceAvailable = registrationServiceDelegate.checkCompoundRegistrationHealth();
				} catch (RegistrationDelegateException e1) {
					serviceAvailable = false;
				}
			} else if (serviceName.equals(UNIQUENESS_SERVICE)) {
				RegistrationServiceDelegate registrationServiceDelegate;
				try {
					registrationServiceDelegate = new RegistrationServiceDelegate();
					serviceAvailable = registrationServiceDelegate.checkUniquenessHealth();
				} catch (RegistrationDelegateException e1) {
					serviceAvailable = false;
				}
			} else if (serviceName.equals(COMPOUND_AGGREGATION_SERVICE)) {
				RegistrationManagerDelegate regManager = new RegistrationManagerDelegate();
				serviceAvailable = regManager.isCompoundAggregationApiAvailable(getHealthSiteCode());
			} else if (serviceName.equals(PURIFICATION_SERVICE_SERVICE)) {
				RegistrationManagerDelegate regManager = new RegistrationManagerDelegate();			
				serviceAvailable = regManager.isPurificationServiceApiAvailable();
			} else if (serviceName.equals(ESIGNATURE_SERVICE)) {
				SignatureDelegate sigDelegate = new SignatureDelegate();
				serviceAvailable = sigDelegate.isServiceAvailable();
			} else if (serviceName.equals(SAFESIGN_SERVICE)) {
				SignatureDelegate sigDelegate = new SignatureDelegate();
				serviceAvailable = sigDelegate.areSubmissionsAllowed();
// We are not doing anything different for archive service as we do for esig service.  Don't waste time.
//			} else if (serviceName.equals(ARCHIVE_SERVICE)) {
//				SignatureDelegate sigDelegate = new SignatureDelegate(null);
//				serviceAvailable = sigDelegate.checkHealth();
			} else if (serviceName.equals(EXTERNAL_COLLABORATOR_SERVICE)) {
				// Not used at this time since ExternalCollaborator service does not impact day-to-day usage.
				serviceAvailable = false;
			}
		} catch (Exception e) {
			log.error("Check Service Health Failure", e);
			serviceAvailable = false;
		}
		return serviceAvailable;
	}

	public static void buildServiceList() {
		serviceList.clear();
		serviceList.add(SESSION_SERVICE);
		serviceList.add(CODETABLE_SERVICE);
		serviceList.add(CHEMISTRY_SERVICE);
		serviceList.add(STORAGE_SERVICE);
		serviceList.add(ANALYTICAL_SERVICE);
		serviceList.add(REAGENT_SERVICE);
		serviceList.add(TIES_SERVICE);
		serviceList.add(CALCULATION_SERVICE);
		serviceList.add(DSP_SERVICE);
		serviceList.add(COMPOUND_SERVICE);
		serviceList.add(CLS_SERVICE);
		serviceList.add(SSS_SERVICE);
		serviceList.add(COMPOUND_MANAGEMENT_CONT_SERVICE);
		serviceList.add(COMPOUND_MANAGEMENT_ORD_SERVICE);
		serviceList.add(REGISTRATION_SERVICE);
		serviceList.add(COMPOUND_REGISTRATION_SERVICE);
		serviceList.add(UNIQUENESS_SERVICE);
		serviceList.add(COMPOUND_AGGREGATION_SERVICE);
		serviceList.add(PURIFICATION_SERVICE_SERVICE);
		serviceList.add(ESIGNATURE_SERVICE);
		serviceList.add(SAFESIGN_SERVICE);
//		serviceList.add(ARCHIVE_SERVICE);
//		serviceList.add(EXTERNAL_COLLABORATOR_SERVICE);
		
		// Detail service that have minimal impact on system, i.e. we can
		// run even though these are down though at a reduced capacity.
		minimalImpactServices.clear();
		minimalImpactServices.add(ANALYTICAL_SERVICE);
		minimalImpactServices.add(REAGENT_SERVICE);
		minimalImpactServices.add(TIES_SERVICE);
		minimalImpactServices.add(DSP_SERVICE);
		minimalImpactServices.add(CALCULATION_SERVICE);
		minimalImpactServices.add(CLS_SERVICE);
		minimalImpactServices.add(SSS_SERVICE);
		minimalImpactServices.add(COMPOUND_MANAGEMENT_CONT_SERVICE);
		minimalImpactServices.add(COMPOUND_MANAGEMENT_ORD_SERVICE);
		minimalImpactServices.add(COMPOUND_REGISTRATION_SERVICE);
		minimalImpactServices.add(UNIQUENESS_SERVICE);
		minimalImpactServices.add(COMPOUND_AGGREGATION_SERVICE);
		minimalImpactServices.add(PURIFICATION_SERVICE_SERVICE);
		minimalImpactServices.add(ESIGNATURE_SERVICE);
		minimalImpactServices.add(SAFESIGN_SERVICE);
		minimalImpactServices.add(ARCHIVE_SERVICE);
//		minimalImpactServices.add(EXTERNAL_COLLABORATOR_SERVICE);
	}

	public static List<ServiceHealthStatus> buildServiceStatusList() {
		serviceStatusList.clear();
		String serviceName = "";
		for (int i = 0; i < serviceList.size(); i++) {
			ServiceHealthStatus serviceHealthStatus = new ServiceHealthStatus();
			serviceName = serviceList.get(i);
			serviceHealthStatus.setServiceName(serviceName);
			if (serviceName.equals(SESSION_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.SESSION_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.SESSION_SERVICE);
			} else if (serviceName.equals(CODETABLE_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.CODETABLE_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.CODETABLE_SERVICE);
			} else if (serviceName.equals(CHEMISTRY_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.CHEMISTRY_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.CHEMISTRY_SERVICE);
			} else if (serviceName.equals(STORAGE_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.STORAGE_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.STORAGE_SERVICE);
			} else if (serviceName.equals(ANALYTICAL_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.ANALYTICAL_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.ANALYTICAL_SERVICE);
			} else if (serviceName.equals(REAGENT_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.REAGENT_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.REAGENT_SERVICE);
			} else if (serviceName.equals(TIES_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.TIES_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.TIES_SERVICE);
			} else if (serviceName.equals(CALCULATION_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.CALCULATION_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.CALCULATION_SERVICE);
			} else if (serviceName.equals(DSP_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.DSP_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.DSP_SERVICE);
			} else if (serviceName.equals(COMPOUND_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.COMPOUND_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.COMPOUND_SERVICE);
			} else if (serviceName.equals(CLS_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.CLS_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_CLS_SERVICE);
			} else if (serviceName.equals(SSS_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.SSS_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_SSS_SERVICE);
			} else if (serviceName.equals(COMPOUND_MANAGEMENT_CONT_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.COMPOUND_MANAGEMENT_CONT_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_COMPOUND_MANAGEMENT_CONT);
			} else if (serviceName.equals(COMPOUND_MANAGEMENT_ORD_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.COMPOUND_MANAGEMENT_ORD_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_COMPOUND_MANAGEMENT_ORDER);
			} else if (serviceName.equals(REGISTRATION_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.REGISTRATION_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.REGISTRATION_SERVICE);
			} else if (serviceName.equals(COMPOUND_REGISTRATION_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.COMPOUND_REGISTRATION_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_COMPOUND_REGISTRATION_SERVICE);
			} else if (serviceName.equals(UNIQUENESS_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.UNIQUENESS_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_UNIQUENESS_SERVICE);
			} else if (serviceName.equals(COMPOUND_AGGREGATION_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.COMPOUND_AGGREGATION_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.COMPOUND_AGGREGATION_SERVICE);
			} else if (serviceName.equals(PURIFICATION_SERVICE_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.PURIFICATION_SERVICE_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PURIFICATION_SERVICE_SERVICE);
			} else if (serviceName.equals(ESIGNATURE_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.ESIGNATURE_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.ESIGNATURE_SERVICE);
			} else if (serviceName.equals(SAFESIGN_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.SAFESIGN_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_SAFESIGN_SERVICE);
			} else if (serviceName.equals(ARCHIVE_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.ARCHIVE_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.PROGRESS_ARCHIVE_SERVICE);
			} else if (serviceName.equals(EXTERNAL_COLLABORATOR_SERVICE)) {
				serviceHealthStatus.setServiceDesc(ServiceHealthStatus.EXTERNAL_COLLABORATOR_SERVICE);
				serviceHealthStatus.setProgressDesc(ServiceHealthStatus.EXTERNAL_COLLABORATOR_SERVICE);
			}
			serviceStatusList.add(serviceHealthStatus);
		}
		return serviceStatusList;
	}

	public static void updateServiceStatus(ServiceHealthStatus serviceHealthStatus) {
		ServiceHealthStatus service = new ServiceHealthStatus();
		for (int i = 0; i < serviceStatusList.size(); i++) {
			service = serviceStatusList.get(i);
			if (service.getServiceName().equals(serviceHealthStatus.getServiceName())) {
				serviceStatusList.set(i, serviceHealthStatus);
				break;
			}
		}
	}

	public static void checkServiceHealth() {
		try {
			buildServiceList();
			serviceStatusList = buildServiceStatusList();
			ServiceHealthStatus service = null;
			ServiceHealthChecker serviceHealthChecker = null;
			Thread[] threads = new Thread[serviceStatusList.size()];
			for (int i = 0; i < serviceStatusList.size(); i++) {
				service = serviceStatusList.get(i);
				serviceHealthChecker = new ServiceHealthChecker();
				serviceHealthChecker.setService(service);
				serviceHealthChecker.setDoingServiceCheck(true);
				Thread th = new Thread(serviceHealthChecker);
				threads[i] = th;
				th.start();
			}

			for (int i = 0; i < serviceStatusList.size(); i++) {
				//LoginDialog.setProgressValue(i, serviceStatusList.get(i).getProgressDesc());
				threads[i].join(30000); // wait 30 seconds before quitting.
			}
			//LoginDialog.setProgressValue(serviceStatusList.size(), service.getProgressDesc());
		} catch (Exception e) {

			log.error("Failure occurred while checking system health", e); /* Ignore */
		}
	}

	public static void checkDBHealth() {
		try {
			ServiceHealthChecker serviceHealthChecker = new ServiceHealthChecker();
			serviceHealthChecker.setDoingDBCheck(true);
			Thread th = new Thread(serviceHealthChecker);
			th.start();
			th.join(30000); // wait 30 seconds before quiting
		} catch (Exception e) {
			log.error("Failure occurred while checking DB health", e);
		}
	}

	/**
	 * @return Returns the serviceList.
	 */
	public List<String> getServiceList() {
		return serviceList;
	}

	/**
	 * @param serviceList
	 *            The serviceList to set.
	 */
	public static void setServiceList(List<String> serviceList) {
		HealthCheckHandler.serviceList = serviceList;
	}

	/**
	 * @return Returns the serviceStatusList.
	 */
	public List<ServiceHealthStatus> getServiceStatusList() {
		return serviceStatusList;
	}

	/**
	 * @param serviceStatusList
	 *            The serviceStatusList to set.
	 */
	public static void setServiceStatusList(List<ServiceHealthStatus> serviceStatusList) {
		HealthCheckHandler.serviceStatusList = serviceStatusList;
	}

	/**
	 * @return Returns the dbStatusList.
	 */
	public List<DBHealthStatusVO> getDbStatusList() {
		return dbStatusList;
	}

	/**
	 * @param dbStatusList
	 *            The dbStatusList to set.
	 */
	public static void setDbStatusList(List<DBHealthStatusVO> dbStatusList) {
		HealthCheckHandler.dbStatusList = dbStatusList;
	}

	public static void addSystemHealthStatusChangeListener(SystemHealthStatusChangeListener newListener) {
		systemHealthStatusChangeListeners.add(newListener);
	}

	public static void removeSystemHealthStatusChangeListener(SystemHealthStatusChangeListener target) {
		systemHealthStatusChangeListeners.remove(target);
	}

	public static void notifySystemHealthStatusChangeListeners() {
		for (int i = 0; i < systemHealthStatusChangeListeners.size(); i++) {
			systemHealthStatusChangeListeners.get(i).updateHealthStatus();
		}
	}
}
