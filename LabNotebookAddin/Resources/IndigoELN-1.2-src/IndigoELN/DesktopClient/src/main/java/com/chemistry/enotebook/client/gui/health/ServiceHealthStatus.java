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
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.health;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceHealthStatus {
	public static final String SESSION_SERVICE = "Session Service";
	public static final String CODETABLE_SERVICE = "Codetable Service";
	public static final String CHEMISTRY_SERVICE = "Chemistry Service";
	public static final String STORAGE_SERVICE = "Storage Service";
	public static final String ANALYTICAL_SERVICE = "Analytical Service";
	public static final String REAGENT_SERVICE = "Reagent Lookup Service";
	public static final String TIES_SERVICE = "MSDS Service";
	public static final String CALCULATION_SERVICE = "Calculation Service";
	public static final String DSP_SERVICE = "Design Service";
	public static final String GRC_SERVICE = "Global Reaction Center Service (GRC)";
	public static final String COMPOUND_SERVICE = "Compound Management Service";
	public static final String 		SSS_SERVICE = "     * Structure Search Service";
	public static final String 		CLS_SERVICE = "     * Compound Lookup Service";
	public static final String 		COMPOUND_MANAGEMENT_CONT_SERVICE = "     * CompoundManagement Container Service";
	public static final String 		COMPOUND_MANAGEMENT_ORD_SERVICE = "     * CompoundManagement Order Service";
	public static final String REGISTRATION_SERVICE = "Compound Registration Service";
	public static final String 		COMPOUND_REGISTRATION_SERVICE = "     * CompoundRegistration VnV / Registration Service";
	public static final String 		UNIQUENESS_SERVICE = "     * Uniqueness Check Service";
	public static final String COMPOUND_AGGREGATION_SERVICE = "Electronic Compound Aggregation Service";
	public static final String PURIFICATION_SERVICE_SERVICE = "Purification Service";
	public static final String ESIGNATURE_SERVICE = "eSignature Service";
	public static final String 		SAFESIGN_SERVICE = 		"     * Signature Service Submissions";
	public static final String 		ARCHIVE_SERVICE = 		"     * Signature Status & eArchive";
	public static final String EXTERNAL_COLLABORATOR_SERVICE = "External Collaborator Service";

	public static final String PROGRESS_COMPOUND_MANAGEMENT_ORDER = "CompoundManagement Order Service";
	public static final String PROGRESS_COMPOUND_MANAGEMENT_CONT = "CompoundManagement Container Service";
	public static final String PROGRESS_SSS_SERVICE = "Structure Search Service";
	public static final String PROGRESS_CLS_SERVICE = "Compound Lookup Service";
	public static final String PROGRESS_COMPOUND_REGISTRATION_SERVICE = "Compound Registration VnV / Registration Service";
	public static final String PROGRESS_UNIQUENESS_SERVICE = "Uniqueness Check Service";
	public static final String PROGRESS_SAFESIGN_SERVICE = "Signature Service Service";
	public static final String PROGRESS_ARCHIVE_SERVICE = "eArchive Service";
	
	public static final String INIT_STATUS = "INIT";
	public static final String GOOD_STATUS = "GOOD";
	public static final String BAD_STATUS = "BAD";
	public static final String MINIMAL_STATUS = "MINIMAL";
	
	private String serviceName;
	private String serviceDesc;
	private String progressDesc;
	private String healthStatus;

	public ServiceHealthStatus() {
		serviceName = "";
		serviceDesc = "";
		healthStatus = INIT_STATUS;
	}

	/**
	 * @return Returns the progressDesc.
	 */
	public String getProgressDesc() {
		return progressDesc;
	}

	/**
	 * @param progressDesc
	 *            The progressDesc to set.
	 */
	public void setProgressDesc(String progressDesc) {
		this.progressDesc = progressDesc;
	}

	/**
	 * @return Returns the serviceDesc.
	 */
	public String getServiceDesc() {
		return serviceDesc;
	}

	/**
	 * @param serviceDesc
	 *            The serviceDesc to set.
	 */
	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	/**
	 * @return Returns the serviceName.
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName
	 *            The serviceName to set.
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return Returns the healthStatus.
	 */
	public String getHealthStatus() {
		return healthStatus;
	}

	/**
	 * @param healthStatus
	 *            The healthStatus to set.
	 */
	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}
}
