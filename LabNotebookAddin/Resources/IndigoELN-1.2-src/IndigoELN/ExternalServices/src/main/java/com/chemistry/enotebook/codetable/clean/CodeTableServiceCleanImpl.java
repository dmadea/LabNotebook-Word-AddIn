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

package com.chemistry.enotebook.codetable.clean;

import com.chemistry.enotebook.codetable.CodeTableService;
import com.chemistry.enotebook.codetable.exceptions.CodeTableServiceException;
import com.chemistry.enotebook.compoundregistration.clean.CompoundRegistrationServiceCleanImpl;
import com.csvreader.CsvReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CodeTableServiceCleanImpl implements CodeTableService {

	// For Compound Registration
	private static final String COMPOUND_REGISTRATION_TA_CDT = "COMPOUND_REGISTRATION_TA_CDT";
	private static final String COMPOUND_REGISTRATION_PROJECT_CDT = "COMPOUND_REGISTRATION_PROJECT_CDT";
	private static final String COMPOUND_REGISTRATION_PROTECTION_CDT = "COMPOUND_REGISTRATION_PROTECTION_CDT";
	private static final String COMPOUND_REGISTRATION_SOURCE_CDT = "COMPOUND_REGISTRATION_SOURCE_CDT";
	private static final String COMPOUND_REGISTRATION_SOURCE_DETAIL_CDT = "COMPOUND_REGISTRATION_SOURCE_DETAIL_CDT";
	private static final String COMPOUND_REGISTRATION_STATE_CDT = "COMPOUND_REGISTRATION_STATE_CDT";
	private static final String COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT = "COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT";
	private static final String COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT = "COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT";
//	private static final String COMPOUND_REGISTRATION_HANDLING_CDT = "COMPOUND_REGISTRATION_HANDLING_CDT";
//	private static final String COMPOUND_REGISTRATION_HAZARD_CDT = "COMPOUND_REGISTRATION_HAZARD_CDT";
//	private static final String COMPOUND_REGISTRATION_STORAGE_CDT = "COMPOUND_REGISTRATION_STORAGE_CDT";
	private static final String COMPOUND_REGISTRATION_ANALYTIC_PURITY_CDT = "COMPOUND_REGISTRATION_ANALYTIC_PURITY_CDT";
	private static final String COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT = "COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT";
	
	// For Compound Management
	private static final String COMPOUND_MANAGEMENT_SITE_CDT = "COMPOUND_MANAGEMENT_SITE_CDT";
	private static final String COMPOUND_MANAGEMENT_SALT_CDT = "COMPOUND_MANAGEMENT_SALT_CDT";
	private static final String COMPOUND_MANAGEMENT_STEREOISOMER_CDT = "COMPOUND_MANAGEMENT_STEREOISOMER_CDT";
	private static final String COMPOUND_MANAGEMENT_SOLVENT_CDT = "COMPOUND_MANAGEMENT_SOLVENT_CDT";
	private static final String COMPOUND_MANAGEMENT_SUPPLIER_CDT = "COMPOUND_MANAGEMENT_SUPPLIER_CDT";
	private static final String COMPOUND_MANAGEMENT_EMPLOYEE = "COMPOUND_MANAGEMENT_EMPLOYEE";	
	private static final String COMPOUND_MANAGEMENT_UNIT_CDT = "COMPOUND_MANAGEMENT_UNIT_CDT";
//	private static final String COMPOUND_MANAGEMENT_CONTAINERS_SELECT = "COMPOUND_MANAGEMENT_CONTAINERS_SELECT";
		
	// Maps
	private Map<String, List<Properties>> codeTable;
	
	public CodeTableServiceCleanImpl() throws CodeTableServiceException {
		codeTable = new HashMap<String, List<Properties>>();
		
		codeTable.put(COMPOUND_REGISTRATION_TA_CDT, 				getTableValues("data/" + COMPOUND_REGISTRATION_TA_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_PROJECT_CDT, 			getTableValues("data/" + COMPOUND_REGISTRATION_PROJECT_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_SOURCE_CDT,				getTableValues("data/" + COMPOUND_REGISTRATION_SOURCE_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_SOURCE_DETAIL_CDT,		getTableValues("data/" + COMPOUND_REGISTRATION_SOURCE_DETAIL_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_STATE_CDT,				getTableValues("data/" + COMPOUND_REGISTRATION_STATE_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT, 	getTableValues("data/" + COMPOUND_REGISTRATION_RESIDUAL_SOLVENT_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT, 	getTableValues("data/" + COMPOUND_REGISTRATION_QUAL_SOLUBILITY_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT,	getTableValues("data/" + COMPOUND_REGISTRATION_SOLUBILITY_SOLVENT_CDT + ".csv"));
//		codeTable.put(COMPOUND_REGISTRATION_HANDLING_CDT, 			getTableValues("data/" + COMPOUND_REGISTRATION_HANDLING_CDT + ".csv"));
//		codeTable.put(COMPOUND_REGISTRATION_HAZARD_CDT, 			getTableValues("data/" + COMPOUND_REGISTRATION_HAZARD_CDT + ".csv"));
//		codeTable.put(COMPOUND_REGISTRATION_STORAGE_CDT,			getTableValues("data/" + COMPOUND_REGISTRATION_STORAGE_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_ANALYTIC_PURITY_CDT,	getTableValues("data/" + COMPOUND_REGISTRATION_ANALYTIC_PURITY_CDT + ".csv"));
		codeTable.put(COMPOUND_REGISTRATION_PROTECTION_CDT,			getTableValues("data/" + COMPOUND_REGISTRATION_PROTECTION_CDT + ".csv"));
				
		codeTable.put(COMPOUND_MANAGEMENT_SITE_CDT, 				getTableValues("data/" + COMPOUND_MANAGEMENT_SITE_CDT + ".csv"));
		codeTable.put(COMPOUND_MANAGEMENT_SALT_CDT, 				getTableValues("data/" + COMPOUND_MANAGEMENT_SALT_CDT + ".csv"));
		codeTable.put(COMPOUND_MANAGEMENT_STEREOISOMER_CDT, 		getTableValues("data/" + COMPOUND_MANAGEMENT_STEREOISOMER_CDT + ".csv"));
		codeTable.put(COMPOUND_MANAGEMENT_SOLVENT_CDT, 				getTableValues("data/" + COMPOUND_MANAGEMENT_SOLVENT_CDT + ".csv"));
		codeTable.put(COMPOUND_MANAGEMENT_SUPPLIER_CDT, 			getTableValues("data/" + COMPOUND_MANAGEMENT_SUPPLIER_CDT + ".csv"));
		codeTable.put(COMPOUND_MANAGEMENT_EMPLOYEE,	 				getTableValues("data/" + COMPOUND_MANAGEMENT_EMPLOYEE + ".csv"));		
//		codeTable.put(COMPOUND_MANAGEMENT_CONTAINERS_SELECT,		getTableValues("data/" + COMPOUND_MANAGEMENT_CONTAINERS_SELECT + ".csv"));
		codeTable.put(COMPOUND_MANAGEMENT_UNIT_CDT,					getTableValues("data/" + COMPOUND_MANAGEMENT_UNIT_CDT + ".csv"));
	}
	
	@Override
	public List<Properties> getCodeTable(String tableName) throws CodeTableServiceException {
		if (!codeTable.containsKey(tableName))
			throw new CodeTableServiceException("Code table for table '" + tableName + "' doesn't exists!");
		
		return codeTable.get(tableName);
	}

	private static List<Properties> getTableValues(String fileName) throws CodeTableServiceException {
		CsvReader reader = null;
		List<Properties> rowsSet = null;
		
		try {
			InputStream inputStream = CompoundRegistrationServiceCleanImpl.class.getClassLoader().getResourceAsStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream); 
			reader = new CsvReader(inputStreamReader);

			String[] headers = null;
			
			if (reader.readHeaders())
				headers = reader.getHeaders();
			 			
			rowsSet = new ArrayList<Properties>(); 
			
			while (reader.readRecord())	{
				Properties row = new Properties();
				
				for (int i = 0; i < headers.length; i++)
					row.put(headers[i], reader.get(headers[i]));
				
				rowsSet.add(row);
			}

			reader.close();
		} catch (Exception e) {
			throw new CodeTableServiceException(e);
		}
		
		return rowsSet; 
	}

	@Override
	public Map<String, List<Properties>> getFullCache() throws CodeTableServiceException {
		return codeTable;
	}
}
