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
 * Created on Jul 12, 2004
 *
 */
package com.chemistry.enotebook.reagent.common;


public interface DAO {

	public final String CONTAINS_OPERAND = "contains".toLowerCase();
	public final String EQUALS_OPERAND = "equals".toLowerCase();
	public final String STARTS_WITH_OPERAND = "starts with".toLowerCase();
	public final String ENDS_WITH_OPERAND = "ends with".toLowerCase();
	public final String GREATER_THAN_OPERAND = "greater than".toLowerCase();
	public final String GREATER_THAN_OR_EQUALS_OPERAND = "greater than or equals".toLowerCase();
	public final String LESS_THAN_OPERAND = "less than".toLowerCase();
	public final String LESS_THAN_OR_EQUALS_OPERAND = "less than or equals".toLowerCase();
	public final String NULL_STRING = "null".toLowerCase();
//	public final String CEN_DATASOURCE_JNDI = "com.enotebook.pool1_cen";
//	public final String COMPOUND_MANAGEMENT_DATASOURCE_JNDI = "com.enotebook.pool2_compoundmanagement";
//	public final String CPI_DATASOURCE_JNDI = "com.enotebook.pool4_cpi";
	
	
	
	//public abstract String buildQuery(Element textDB); 
	
	//public abstract void parsingSearchParams(String paramsXML); 

		
	
}

