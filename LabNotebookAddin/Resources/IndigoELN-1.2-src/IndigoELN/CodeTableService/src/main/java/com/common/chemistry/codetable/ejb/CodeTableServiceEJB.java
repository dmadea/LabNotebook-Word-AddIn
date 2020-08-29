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
package com.common.chemistry.codetable.ejb;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.codetable.CodeTableServiceFactory;
import com.chemistry.enotebook.codetable.exceptions.CodeTableServiceException;
import com.common.chemistry.codetable.interfaces.CodeTableServiceRemote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CodeTableServiceEJB implements CodeTableServiceRemote {

//	private static final String COMPOUND_MANAGEMENT_PREFIX = "COMPOUND_MANAGEMENT";
//	private static final String COMPOUND_REGISTRATION_PREFIX = "COMPOUND_REGISTRATION";
		
	@Override
    public List<Properties> getCodeTableValues(String tableName) throws LoadServiceException, CodeTableServiceException {    	
    	return new ArrayList<Properties>(CodeTableServiceFactory.getService().getCodeTable(tableName));
    }
    
	@Override
    public Map<String, List<Properties>> getFullCache() throws LoadServiceException, CodeTableServiceException {
    	return CodeTableServiceFactory.getService().getFullCache();
    }
}
