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
package com.chemistry.enotebook.registration.ejb;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.compoundregistration.CompoundRegistrationServiceFactory;
import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationJobStatus;
import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationStatus;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.registration.*;
import com.chemistry.enotebook.registration.dao.BiologyScreenDAO;
import com.chemistry.enotebook.registration.dao.RegistrationDAO;
import com.chemistry.enotebook.registration.interfaces.RegistrationServiceRemote;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.vcr.VCRServiceFactory;
import com.chemistry.enotebook.vcr.auxiliary.StatusInfo;
import com.chemistry.enotebook.vnv.VnvServiceFactory;
import com.chemistry.enotebook.vnv.classes.UniquenessCheckVO;
import com.chemistry.enotebook.vnv.exceptions.VnvRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistrationServiceEJB implements RegistrationServiceRemote {

	private static final Log log = LogFactory.getLog(RegistrationServiceEJB.class);

	@Override
	public String executeVnV(String molfile, String stereoisomerCode) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		if (!isServiceAvailable()) {
			throw new RegistrationSvcUnavailableException();
		}
		
		try {
			return VnvServiceFactory.getService().executeVnV(molfile, stereoisomerCode);
		} catch (Exception e) {
			throw new RegistrationSvcUnavailableException(e.getMessage(), e);
		}
	}

	@Override
	public CompoundRegistrationJobStatus[] getRegisterJobStatus(String[] jobids) throws RegistrationSvcUnavailableException {
		if (!isServiceAvailable()) {
			throw new RegistrationSvcUnavailableException();
		}

		try {			
			List<CompoundRegistrationJobStatus> result = new ArrayList<CompoundRegistrationJobStatus>();
			
			for (String jobId : jobids) {
				String jobStatus = CompoundRegistrationServiceFactory.getServices().getRegisterJobStatus(getCompoundRegistrationAccessToken(), Long.parseLong(jobId));
				
				CompoundRegistrationJobStatus status = new CompoundRegistrationJobStatus();
				
				status.setJobId(jobId);
				status.setStatus(jobStatus);
				status.setDetails("");
				
				result.add(status);
			}
			
			return result.toArray(new CompoundRegistrationJobStatus[0]);
		} catch (Exception e) {
			throw new RegistrationSvcUnavailableException(e);
		}
	}

	@Override
	public boolean checkCompoundRegistrationHealth() throws Exception {
		if (!isServiceAvailable()) {
			return false;
		}

		try {
			return CompoundRegistrationServiceFactory.getServices().checkHealth();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getRegistrationNoFromBatch(String batchNo) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		try {
			return new RegistrationDAO().getRegistrationNoFromBatch(batchNo);
		} catch (Exception e) {
			throw new RegistrationSvcUnavailableException(e.getMessage(), e);
		}
	}

	@Override
	public List<ScreenInfoRequest> getInfoForScreens(List<ScreenInfoRequest> screenInfoRequests) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		try {
			return new BiologyScreenDAO().processScreenInfoRequests(new ArrayList<ScreenInfoRequest>(screenInfoRequests));
		} catch (Exception e) {
			throw new RegistrationSvcUnavailableException(e.getMessage(), e);
		}
	}

	@Override
	public List<ScreenSearchVO> searchForScreens(ScreenSearchParams params) throws RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		try {
			return new BiologyScreenDAO().performScreenSearch(params);
		} catch (Exception e) {
			throw new RegistrationSvcUnavailableException(e.getMessage(), e);
		}
	}

	@Override
	public UniquenessCheckVO checkUniqueness(String molfile, String stereoisomerCode, boolean includeLegacy) throws VnvRuntimeException, RegistrationTokenInvalidException, RegistrationSvcUnavailableException, LoadServiceException {
		if (!isServiceAvailable()) {
			throw new RegistrationSvcUnavailableException();
		}

		return VnvServiceFactory.getService().checkUniqueness(molfile, stereoisomerCode, includeLegacy);
	}

	@Override
	public boolean checkUniquenessHealth() throws Exception {
		try {
			return VnvServiceFactory.getService().checkHealth();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public BarcodeValidationVO validateBarcode(BarcodeValidationVO barcodeVO) throws RegistrationRuntimeException, RegistrationTokenInvalidException, RegistrationSvcUnavailableException {
		try {
			return new RegistrationDAO().validateBarcode(barcodeVO);
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}

	@Override
	public CompoundRegistrationStatus[] getRegisterCompoundStatus(String jobid, String plateid, int offsets[], boolean includeStruct) throws  RegistrationSvcUnavailableException {
		try {
			List<CompoundRegistrationStatus> result = new ArrayList<CompoundRegistrationStatus>();
			
			String token = getCompoundRegistrationAccessToken();
			
			List<Compound> compounds = CompoundRegistrationServiceFactory.getServices().getRegisteredCompounds(token, Long.parseLong(jobid));
			
			for (Compound compound : compounds) {
				CompoundRegistrationStatus status = new CompoundRegistrationStatus();
				
				status.setInternalBatchNumber(compound.getBatchNo());
				status.setBatchNumber(compound.getConversationalBatchNo());
				status.setGlobalNumber(compound.getCompoundNo());
				status.setJobid(jobid);
				status.setStatus(compound.getRegistrationStatus());
				status.setDetailElement("");
				status.setOffset("0");
				status.setStructureData(compound.getStructure());
				
				result.add(status);
			}
			
			return result.toArray(new CompoundRegistrationStatus[0]);			
		} catch (Exception e) {
			throw new RegistrationSvcUnavailableException(e);
		}
	}

	@Override
	public Map<String, RegistrationDetailsVO> getRegistrationInformation(String[] NBBatchNos) throws RegistrationRuntimeException, RegistrationSvcUnavailableException, RegistrationTokenInvalidException {
		try {
			return new RegistrationDAO().getRegistrationInformation(NBBatchNos);
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}
	
	@Override
	public StatusInfo registerVirtualCompound(String molfile, boolean isDryrun, String loaderId) throws RegistrationRuntimeException {
		try {
			return VCRServiceFactory.getService().registerCompound(molfile, isDryrun, loaderId);
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}

	@Override
	public long submitBatchesForRegistration(List<ProductBatchModel> batches) throws RegistrationRuntimeException {
		try {
			return CompoundRegistrationServiceFactory.getServices().submitForRegistration(getCompoundRegistrationAccessToken(), createCompoundsForRegistration(batches));
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}
	
	private List<Compound> createCompoundsForRegistration(List<ProductBatchModel> batches) {
		List<Compound> result = new ArrayList<Compound>();
		
		for (ProductBatchModel batch : batches) {
			Compound compound = new Compound();
			
			compound.setStructure(batch.getCompound().getStringSketchAsString());
			compound.setBatchNo(batch.getBatchNumberAsString());
			compound.setCasNo(batch.getCompound().getCASNumber());
			compound.setSaltCode(batch.getSaltForm().getCode());
			compound.setSaltEquivs(batch.getSaltEquivs());
			compound.setComment(batch.getComments());
			compound.setHazardComment(batch.getHazardComments());
			compound.setStereoisomerCode(batch.getCompound().getStereoisomerCode());
			compound.setStorageComment(batch.getStorageComments());
			
			result.add(compound);
		}
		
		return result;
	}
	
	private String getCompoundRegistrationAccessToken() throws RegistrationRuntimeException {
		try {
			return CompoundRegistrationServiceFactory.getServices().getTokenHash();
		} catch (Exception e) {
			throw new RegistrationRuntimeException(e);
		}
	}
	
	private boolean isServiceAvailable() {
		try {
			String val = CeNSystemXmlProperties.getCeNProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_AVAIL, null);
			return (new Boolean(val)).booleanValue();
		} catch (Exception e) {
			log.error("", e);
		}

		return true;
	}
}
