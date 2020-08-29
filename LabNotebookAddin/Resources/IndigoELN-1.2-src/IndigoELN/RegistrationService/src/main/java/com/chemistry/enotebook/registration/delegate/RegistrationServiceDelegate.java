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
package com.chemistry.enotebook.registration.delegate;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationJobStatus;
import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationStatus;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.registration.*;
import com.chemistry.enotebook.registration.interfaces.RegistrationServiceRemote;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.vcr.auxiliary.StatusInfo;
import com.chemistry.enotebook.vnv.classes.UniquenessCheckVO;
import com.chemistry.enotebook.vnv.exceptions.VnvRuntimeException;

import java.util.List;
import java.util.Map;

public class RegistrationServiceDelegate implements RegistrationServiceRemote {

	private RegistrationServiceRemote service;

	public RegistrationServiceDelegate() {
		service = ServiceLocator.getInstance().locateService("RegistrationService", RegistrationServiceRemote.class);
	}
	
	public RegistrationDetailsVO getRegistrationInformation(String batchNo) throws RegistrationDelegateException, RegistrationSvcUnavailableException {
		String NBBatchNos[] = new String[1];
		NBBatchNos[0] = batchNo;

		try {
			Map<String, RegistrationDetailsVO> m = getRegistrationInformation(NBBatchNos);
			return (RegistrationDetailsVO) m.get(batchNo);
		} catch (Exception e) {
			throw new RegistrationDelegateException(e);
		}
	}

	@Override
	public String executeVnV(String molfile, String stereoisomerCode) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		return service.executeVnV(molfile, stereoisomerCode);
	}

	@Override
	public CompoundRegistrationJobStatus[] getRegisterJobStatus(String[] jobids) throws RegistrationSvcUnavailableException {
		return service.getRegisterJobStatus(jobids);
	}

	@Override
	public boolean checkCompoundRegistrationHealth() throws Exception {
		return service.checkCompoundRegistrationHealth();
	}

	@Override
	public String getRegistrationNoFromBatch(String batchNo) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		return service.getRegistrationNoFromBatch(batchNo);
	}

	@Override
	public List<ScreenInfoRequest> getInfoForScreens(List<ScreenInfoRequest> screenInfoRequests) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		return service.getInfoForScreens(screenInfoRequests);
	}

	@Override
	public List<ScreenSearchVO> searchForScreens(ScreenSearchParams params) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		return service.searchForScreens(params);
	}

	@Override
	public UniquenessCheckVO checkUniqueness(String molfile, String stereoisomerCode, boolean includeLegacy) throws VnvRuntimeException, RegistrationTokenInvalidException, RegistrationSvcUnavailableException, LoadServiceException {
		return service.checkUniqueness(molfile, stereoisomerCode, includeLegacy);
	}

	@Override
	public boolean checkUniquenessHealth() throws Exception {
		return service.checkUniquenessHealth();
	}

	@Override
	public BarcodeValidationVO validateBarcode(BarcodeValidationVO barcodeVO) throws RegistrationRuntimeException, RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		return service.validateBarcode(barcodeVO);
	}

	@Override
	public Map<String, RegistrationDetailsVO> getRegistrationInformation( String[] NBBatchNos) throws RegistrationRuntimeException, RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		return service.getRegistrationInformation(NBBatchNos);
	}

	@Override
	public CompoundRegistrationStatus[] getRegisterCompoundStatus(String jobid, String plateid, int[] offsets, boolean includeStruct) throws RegistrationSvcUnavailableException {
		return service.getRegisterCompoundStatus(jobid, plateid, offsets, includeStruct);
	}

	@Override
	public StatusInfo registerVirtualCompound(String molfile, boolean isDryrun, String loaderId) throws RegistrationRuntimeException {
		return service.registerVirtualCompound(molfile, isDryrun, loaderId);
	}

	@Override
	public long submitBatchesForRegistration(List<ProductBatchModel> batches) throws RegistrationRuntimeException {
		return service.submitBatchesForRegistration(batches);
	}
}
