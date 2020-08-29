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
package com.chemistry.enotebook.compoundregistration;

import com.chemistry.enotebook.AbstractServiceFactory;
import com.chemistry.enotebook.LoadServiceException;

public class CompoundRegistrationServiceFactory extends AbstractServiceFactory {
	private CompoundRegistrationServiceFactory() {}
	
	public static synchronized CompoundRegistrationService getServices() throws LoadServiceException {
		if (ourService == null) {
            ourService = (CompoundRegistrationService) createServiceImpl(CompoundRegistrationService.class.getName());
		}
		return ourService;
	}
	
	private static CompoundRegistrationService ourService;
}
