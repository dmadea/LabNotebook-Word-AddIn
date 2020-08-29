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
package com.chemistry.enotebook.hazard;

import com.chemistry.enotebook.AbstractServiceFactory;


public class HazardServiceFactory extends AbstractServiceFactory {
	private HazardServiceFactory() {}
	
	public static synchronized HazardService getService() throws Exception {
		if (ourService == null) {
			ourService = (HazardService) createServiceImpl(HazardService.class.getName());
		}
		return ourService;
	}
	
	private static HazardService ourService;	
}
