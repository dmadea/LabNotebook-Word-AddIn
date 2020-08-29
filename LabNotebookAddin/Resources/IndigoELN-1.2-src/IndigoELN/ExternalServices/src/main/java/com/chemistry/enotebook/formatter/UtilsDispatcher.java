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
package com.chemistry.enotebook.formatter;

import com.chemistry.enotebook.AbstractServiceFactory;
import com.chemistry.enotebook.LoadServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UtilsDispatcher extends AbstractServiceFactory {
	private static final Log log = LogFactory.getLog(UtilsDispatcher.class);
	private static Formatter formatter;
	
	public static synchronized Formatter getFormatter() throws LoadServiceException {
		if (formatter == null) {
            formatter = (Formatter) createServiceImpl(Formatter.class.getName());
			log.info("Formatter has been initialized");
		}
		return formatter;
	}
}
