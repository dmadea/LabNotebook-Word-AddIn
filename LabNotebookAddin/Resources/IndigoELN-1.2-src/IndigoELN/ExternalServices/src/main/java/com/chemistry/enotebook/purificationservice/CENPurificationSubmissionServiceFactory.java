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
package com.chemistry.enotebook.purificationservice;

import com.chemistry.enotebook.AbstractServiceFactory;
import com.chemistry.enotebook.LoadServiceException;

public class CENPurificationSubmissionServiceFactory extends AbstractServiceFactory {
    private CENPurificationSubmissionServiceFactory() {
    }

    public static synchronized CeNPurificationServiceSubmissionService getPurificationServiceSubmissionService() throws LoadServiceException {
        if (ourService == null) {
            ourService = (CeNPurificationServiceSubmissionService) createServiceImpl(CeNPurificationServiceSubmissionService.class.getName());
        }
        return ourService;
    }

    private static CeNPurificationServiceSubmissionService ourService;

}
