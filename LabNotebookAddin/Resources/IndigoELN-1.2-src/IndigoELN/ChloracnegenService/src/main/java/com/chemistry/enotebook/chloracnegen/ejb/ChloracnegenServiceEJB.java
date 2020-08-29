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
package com.chemistry.enotebook.chloracnegen.ejb;

import com.chemistry.enotebook.chloracnegen.ChloracnegenServiceFactory;
import com.chemistry.enotebook.chloracnegen.classes.Structure;
import com.chemistry.enotebook.chloracnegen.delegate.ChloracnegenDelegateException;
import com.chemistry.enotebook.chloracnegen.interfaces.ChloracnegenService;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChloracnegenServiceEJB implements ChloracnegenService {

    private static final Log log = LogFactory.getLog(ChloracnegenServiceEJB.class);

    @Override
    public Structure checkChloracnegen(Structure struct, SessionIdentifier sessionID) throws ChloracnegenDelegateException {
        log.debug("checkChloracnegen().entering");
        
		String userId = sessionID.getUserID();

        try {
        	struct = ChloracnegenServiceFactory.getCalculationService().checkChloracnegen(struct, userId);
        } catch (Exception e) {
            log.error("checkChloracnegen()", e);
            throw new ChloracnegenDelegateException("", e);
        }
        
        log.debug("checkChloracnegen().exiting");
        return struct;
    }
}
