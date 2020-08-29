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

package com.chemistry.enotebook.codetable;

import com.chemistry.enotebook.codetable.exceptions.CodeTableServiceException;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Interface to work with Code Tables
 */
public interface CodeTableService {

	/**
	 * Get Full Cache
	 * 
	 * @return Full Cache
	 * @throws CodeTableServiceException
	 */
	public Map<String, List<Properties>> getFullCache()
			throws CodeTableServiceException;

	/**
	 * Get Code Table
	 * 
	 * @param tableName
	 *            Code Table name
	 * @return Code Table with given name
	 * @throws CodeTableServiceException
	 */
	public List<Properties> getCodeTable(String tableName)
			throws CodeTableServiceException;
}
