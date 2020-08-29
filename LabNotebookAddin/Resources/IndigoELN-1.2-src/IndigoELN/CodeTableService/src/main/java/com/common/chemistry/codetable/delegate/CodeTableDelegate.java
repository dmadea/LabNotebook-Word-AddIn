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
 * Created on Jul 22, 2004
 *
 * Business delegate can decouple business components from the code that uses them.
 * The Business Delegate pattern manages the complexity of distributed component
 * lookup and exception handling, and may adapt the business component interface
 * to a simpler interface for use by views.
 * */
package com.common.chemistry.codetable.delegate;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.codetable.exceptions.CodeTableServiceException;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.common.chemistry.codetable.interfaces.CodeTableServiceRemote;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CodeTableDelegate implements CodeTableServiceRemote {
	
	private CodeTableServiceRemote service;

	public CodeTableDelegate() throws CodeTableInitException {
		service = ServiceLocator.getInstance().locateService("CodeTableService", CodeTableServiceRemote.class);
	}

	@Override
	public List<Properties> getCodeTableValues(String tableName) throws LoadServiceException, CodeTableServiceException {
		return service.getCodeTableValues(tableName);
	}

	@Override
	public Map<String, List<Properties>> getFullCache() throws LoadServiceException, CodeTableServiceException {
		return service.getFullCache();
	}
}
