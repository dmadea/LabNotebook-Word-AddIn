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
package com.chemistry.enotebook.publisher;

import com.chemistry.enotebook.AbstractServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublisherFactory extends AbstractServiceFactory {
	private static final Log log = LogFactory.getLog(PublisherFactory.class);
	
	private PublisherFactory() {}
		
	public static synchronized PublisherService getService() {
		if (ourService == null) {
			try {
				ourService = (PublisherService) createServiceImpl(PublisherService.class.getName());
				log.info("PublisherService has been initialized");
			} catch (Exception e) {
				log.error("Error initialize PublisherService:", e);
			}
		}
		return ourService;
	}

	private static PublisherService ourService;
}
