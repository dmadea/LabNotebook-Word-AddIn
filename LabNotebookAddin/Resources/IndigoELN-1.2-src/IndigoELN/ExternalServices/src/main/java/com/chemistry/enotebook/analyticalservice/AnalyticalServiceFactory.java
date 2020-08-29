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
package com.chemistry.enotebook.analyticalservice;

import com.chemistry.enotebook.AbstractServiceFactory;
import com.chemistry.enotebook.LoadServiceException;

public class AnalyticalServiceFactory extends AbstractServiceFactory {
	private AnalyticalServiceFactory() {}
	
	public static synchronized AnalyticalService getService() throws LoadServiceException {
		if (service == null) {
            service = (AnalyticalService) createServiceImpl(AnalyticalService.class.getName());
		}
		return service;
	}
	
	private static AnalyticalService service; 
}
