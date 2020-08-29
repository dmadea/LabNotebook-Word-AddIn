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
package com.chemistry.enotebook.compoundregistration.clean;

import com.chemistry.enotebook.compoundregistration.CompoundRegistrationService;
import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.search.Compound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundRegistrationServiceCleanImpl implements CompoundRegistrationService {

    private static final String PASSED = "PASSED";

    private static final Map<Long, List<Compound>> cache = new HashMap<Long, List<Compound>>();

    private static final Object lock = new Object();

    private static long currentJobId = 1;

	@Override
	public boolean checkHealth() {
		return true;
	}

	@Override
	public String getTokenHash() throws RegistrationRuntimeException {
		return Long.toString(System.currentTimeMillis());
	}
	
	@Override
	public long submitForRegistration(String tokenHash, List<Compound> compound) throws RegistrationRuntimeException {
        synchronized (lock) {
            if (currentJobId == Long.MAX_VALUE) {
                currentJobId = 1;
            }

            for (Compound c : compound) {
                c.setConversationalBatchNo("STR-" + System.currentTimeMillis());
                c.setRegistrationStatus(PASSED);
            }

            cache.put(currentJobId, compound);

            return currentJobId++;
        }
	}

	@Override
	public String getRegisterJobStatus(String tokenHash, long jobId) throws RegistrationRuntimeException {
		return PASSED;
	}

	@Override
	public List<Compound> getRegisteredCompounds(String tokenHash, long jobId) throws RegistrationRuntimeException {
		synchronized (lock) {
            return cache.remove(jobId);
        }
	}
}
