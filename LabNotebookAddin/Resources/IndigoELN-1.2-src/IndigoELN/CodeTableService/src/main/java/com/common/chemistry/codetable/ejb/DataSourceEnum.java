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
/**
 * @Title:	DataSourceEnum.java
 *
 * @Date:	Dec 11, 2002
 *
 * 
 *
 * Usage:	Thought it would be good to use when accessing preferred contact
 * 			method information.  It was.
 *
 * Purpose: To represent the club information.
 *
 * Requires:
 *
 */
package com.common.chemistry.codetable.ejb;

import com.chemistry.enotebook.servicelocator.ServiceLocator;

public class DataSourceEnum {

	private final String value;
//	private static final String STR_COMPOUND_MANAGEMENT_DS = "COMPOUND_MANAGEMENT_DS_JNDI";
	private static final String STR_CEN_DS = ServiceLocator.CEN_DS_JNDI;
//	private static final String STR_COMPOUND_REGISTRATION_DS = "COMPOUND_REGISTRATION_DS_JNDI";
	
	private DataSourceEnum(String newValue) { this.value = newValue; }
	
//	public static final DataSourceEnum CompoundManagement = new DataSourceEnum(STR_COMPOUND_MANAGEMENT_DS);
	public static final DataSourceEnum CEN = new DataSourceEnum(STR_CEN_DS);
//	public static final DataSourceEnum CompoundRegistration = new DataSourceEnum(STR_COMPOUND_REGISTRATION_DS);

	// need to override equals, hashCode, and 
	public String toString() { return value; }

	public static DataSourceEnum convertStringToEnum(String param)
	{
		DataSourceEnum retVal = null;

//		if (param.equals(STR_COMPOUND_MANAGEMENT_DS)) retVal = CompoundManagement;
		if (param.equals(STR_CEN_DS)) retVal = CEN;
//		if (param.equals(STR_COMPOUND_REGISTRATION_DS)) retVal = CompoundRegistration;
		
		return retVal; 
	} // convertStringToEnum

	public static String convertEnumToString(DataSourceEnum param)
	{
		String retVal = "";
		
//		if (STR_COMPOUND_MANAGEMENT_DS.equals(param.toString())) retVal = STR_COMPOUND_MANAGEMENT_DS;
		if (STR_CEN_DS.equals(param.toString())) retVal = STR_CEN_DS;
//		if (STR_COMPOUND_REGISTRATION_DS.equals(param.toString())) retVal = STR_COMPOUND_REGISTRATION_DS;
		
		return retVal; 
	} // convertEnumToString
}
