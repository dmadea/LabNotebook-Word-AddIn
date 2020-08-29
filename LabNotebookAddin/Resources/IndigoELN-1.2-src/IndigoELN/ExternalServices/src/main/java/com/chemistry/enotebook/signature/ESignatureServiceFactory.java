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
package com.chemistry.enotebook.signature;

import com.chemistry.enotebook.AbstractServiceFactory;
import com.chemistry.enotebook.LoadServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ESignatureServiceFactory extends AbstractServiceFactory {
	private static final Log log = LogFactory.getLog(ESignatureServiceFactory.class);
	private ESignatureServiceFactory() {}
	
	public static synchronized ESignatureService getService() throws LoadServiceException {
		if (ourService == null) {
			try {
				ourService = (ESignatureService) createServiceImpl(ESignatureService.class.getName());
				log.info("ESignatureService has been initialized");
			} catch (Exception e) {
				log.error("Error initialize ESignatureService:", e);
			}
		}
		return ourService;
	}

	
	private static ESignatureService ourService;
}
