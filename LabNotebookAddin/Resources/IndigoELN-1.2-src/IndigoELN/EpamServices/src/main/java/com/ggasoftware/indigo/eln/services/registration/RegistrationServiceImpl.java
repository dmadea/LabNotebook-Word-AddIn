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
package com.ggasoftware.indigo.eln.services.registration;

import com.chemistry.enotebook.compoundregistration.CompoundRegistrationService;
import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.search.Compound;
import com.epam.indigo.crs.classes.CompoundInfo;
import com.epam.indigo.crs.classes.CompoundRegistrationStatus;
import com.epam.indigo.crs.classes.FullCompoundInfo;
import com.epam.indigo.crs.services.registration.BingoRegistration;
import com.epam.indigo.crs.services.search.BingoSearch;
import com.ggasoftware.indigo.eln.services.crs.CrsConnection;
import com.ggasoftware.indigo.eln.services.search.SearchServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RegistrationServiceImpl implements CompoundRegistrationService {

	private static final String PROPERTIES_FILE = "registration.properties";
	
	private static final String PROPERTIES_USERNAME = "USERNAME";
	private static final String PROPERTIES_PASSWORD = "PASSWORD";

    private BingoRegistration registration;

	private String username;
	private String password;
	private String token;
	
	public RegistrationServiceImpl() {
        try {
            registration = CrsConnection.getRegistrationService();
        } catch (RegistrationRuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

	@Override
	public boolean checkHealth() {
		return (registration != null);
	}

	@Override
	public synchronized String getTokenHash() throws RegistrationRuntimeException {
        InputStream is = null;

		try {
			if (token == null) {
				if (username == null || password == null) {
                    is = RegistrationServiceImpl.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);

					Properties props = new Properties();
					props.load(is);
				
					username = props.getProperty(PROPERTIES_USERNAME);
					password = props.getProperty(PROPERTIES_PASSWORD);
				}
				
				token = registration.getTokenHash(username, password);
			}
			return token;
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		} finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
                // ignored
            }
        }
    }
	
	@Override
	public long submitForRegistration(String tokenHash, List<Compound> compounds) throws RegistrationRuntimeException {
		List<CompoundInfo> list = new ArrayList<CompoundInfo>();
		
		for (Compound compound : compounds) {
			CompoundInfo info = new CompoundInfo();
			
			info.setBatchNumber(compound.getBatchNo());
			info.setCasNumber(compound.getCasNo());
			info.setComments(compound.getComment());
			info.setData(compound.getStructure());
			info.setHazardComments(compound.getHazardComment());
			info.setSaltCode(compound.getSaltCode());
			info.setSaltEquivalents(compound.getSaltEquivs());
			info.setStereoIsomerCode(compound.getStereoisomerCode());
			info.setStorageComments(compound.getStorageComment());
			
			list.add(info);
		}
		
		try {
			if (list.size() > 0)
				return registration.submitListForRegistration(tokenHash, list);
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
		
		return -1;
	}

	@Override
	public String getRegisterJobStatus(String tokenHash, long jobId) throws RegistrationRuntimeException {
		try {
			CompoundRegistrationStatus status = registration.checkRegistrationStatus(tokenHash, jobId);
			
			switch (status) {
			case SUCCESSFUL:
				return "PASSED";
			case WRONG_TOKEN_DURING_REGISTRATION:
				return "FAILED";
			case WRONG_TOKEN_DURING_CHECK:
				return "FAILED";
			default:
				return status.toString();
			}
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}

	@Override
	public List<Compound> getRegisteredCompounds(String tokenHash, long jobId) throws RegistrationRuntimeException {
		try {
			BingoSearch search = CrsConnection.getSearchService();

			List<FullCompoundInfo> compounds = search.getCompoundByJobId(Long.toString(jobId));
			
			List<Compound> result = new ArrayList<Compound>();
			
			for (FullCompoundInfo compound : compounds)
				result.add(SearchServiceImpl.populateCompound(compound));
			
			return result;
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}
}
