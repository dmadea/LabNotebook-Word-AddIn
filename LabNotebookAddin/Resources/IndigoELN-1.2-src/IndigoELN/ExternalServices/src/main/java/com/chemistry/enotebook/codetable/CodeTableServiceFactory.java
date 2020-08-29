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

import com.chemistry.enotebook.AbstractServiceFactory;
import com.chemistry.enotebook.LoadServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CodeTableServiceFactory extends AbstractServiceFactory {

	private static final Log log = LogFactory.getLog(CodeTableServiceFactory.class);

	private static CodeTableService ourService;

	private CodeTableServiceFactory() {
	}

	public static synchronized CodeTableService getService() throws LoadServiceException {
		if (ourService == null) {
			ourService = (CodeTableService) createServiceImpl(CodeTableService.class.getName());
			log.info("CodeTableService has been initialized");
		}
		return ourService;
	}
}
