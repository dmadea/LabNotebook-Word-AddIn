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
/*
 * Created on Oct 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.delegate;


import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.hazard.exceptions.HazardException;
import com.chemistry.enotebook.reagent.exceptions.ReagentInvalidTokenException;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.reagent.interfaces.ReagentMgmtService;
import com.chemistry.enotebook.reagent.valueobject.UserInfoVO;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

public class ReagentMgmtServiceDelegate implements ReagentMgmtService {

    private ReagentMgmtService service;

    public ReagentMgmtServiceDelegate() {
        service = ServiceLocator.getInstance().locateService("ReagentService", ReagentMgmtService.class);
    }

    @Override
    public byte[] getMyReagentList(java.lang.String UserID) throws ReagentInvalidTokenException, ReagentMgmtException {
        return service.getMyReagentList(UserID);
    }

    @Override
    public void UpdateMyReagentList(UserInfoVO myInfo, java.lang.String MyReagentList) throws ReagentInvalidTokenException, ReagentMgmtException {
    	service.UpdateMyReagentList(myInfo, MyReagentList);
    }

    @Override
    public void UpdateMyReagentList(String userName, byte[] MyReagentList) throws ReagentInvalidTokenException, ReagentMgmtException {
    	service.UpdateMyReagentList(userName, MyReagentList);
    }

    @Override
    public String getDBInfo(String SiteID) throws ReagentInvalidTokenException, ReagentMgmtException {
        return service.getDBInfo(SiteID);
    }

    @Override
    public void updateReagentDBXML(String SiteCode, String reagentDBXML) throws ReagentMgmtException {
    	service.updateReagentDBXML(SiteCode, reagentDBXML);
    }

    @Override
    public String getStructureByCompoundNo(String compoundNumber) throws ReagentInvalidTokenException, CompoundNotFoundException, ReagentMgmtException {
    	return service.getStructureByCompoundNo(compoundNumber);
    }

    @Override
    public String getHazardInfo(String id, String idtype, String lang) throws HazardException {
    	return service.getHazardInfo(id, idtype, lang);
    }

    @Override
	public boolean getHazardInfoHealth() throws HazardException {
    	return service.getHazardInfoHealth();
	}

	@Override
	public byte[] doReagentsSearch(String searchParamsXML, ReagentCallBackInterface cbInterface) throws ReagentMgmtException, ReagentInvalidTokenException {
		return service.doReagentsSearch(searchParamsXML, cbInterface);
	}
}
