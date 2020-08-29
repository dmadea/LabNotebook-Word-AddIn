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
package com.chemistry.enotebook.chloracnegen.delegate;

import com.chemistry.enotebook.chloracnegen.classes.Structure;
import com.chemistry.enotebook.chloracnegen.interfaces.ChloracnegenService;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.security.SessionIdentifier;

public class ChloracnegenServiceDelegate implements ChloracnegenService {

    private ChloracnegenService calcServiceRemote;

    public ChloracnegenServiceDelegate() {
    	calcServiceRemote = ServiceLocator.getInstance().locateService("ChloracnegenService", ChloracnegenService.class);
    }

    @Override
    public Structure checkChloracnegen(Structure struct, SessionIdentifier sessionId) throws ChloracnegenDelegateException {
        try {
            struct = calcServiceRemote.checkChloracnegen(struct, sessionId);
        } catch (Exception e) {
            throw new ChloracnegenDelegateException("Error running CalculationService synchronously", e);
        }
        return struct;
    }
}
