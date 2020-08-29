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
 * Created on Mar 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.session;

import java.io.Serializable;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DBHealthStatusVO 
	implements Serializable
{
	static final long serialVersionUID = 7192025638634071282L;	
	
	public static final String COMPOUND_MANAGEMENT_DB = "Compound Management database";
	public static final String CEN_DB = "Main database";
	public static final String ACD_DB = "ACD database (MDL Available Chemicals Directory)";
	public static final String COMPOUND_REGISTRATION_DB = "Compound Registration database";

	private String dbDesc;
	private String dbJNDI;
	private String healthStatus;
	
	
	public DBHealthStatusVO(){
		dbDesc = "";
		dbJNDI = "";
		healthStatus = "INIT";
	}
	

	/**
	 * @return Returns the dbDesc.
	 */
	public String getDbDesc() {
		return dbDesc;
	}
	/**
	 * @param dbDesc The dbDesc to set.
	 */
	public void setDbDesc(String dbDesc) {
		this.dbDesc = dbDesc;
	}
	
	
	/**
	 * @return Returns the dbJNDI.
	 */
	public String getDbJNDI() {
		return dbJNDI;
	}
	/**
	 * @param dbJNDI The dbJNDI to set.
	 */
	public void setDbJNDI(String dbJNDI) {
		this.dbJNDI = dbJNDI;
	}
	/**
	 * @return Returns the healthStatus.
	 */
	public String getHealthStatus() {
		return healthStatus;
	}
	/**
	 * @param healthStatus The healthStatus to set.
	 */
	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}
}
