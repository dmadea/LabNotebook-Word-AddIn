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
 * Created on Oct 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.registration.dao;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.compoundmanagement.CompoundManagementService;
import com.chemistry.enotebook.compoundmanagement.CompoundManagementServiceFactory;
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.compoundregistration.classes.RegistrationDetailsVO;
import com.chemistry.enotebook.compoundregistration.exceptions.RegistrationRuntimeException;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.search.SearchServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationDAO {
	
	private static final Log LOG = LogFactory.getLog(RegistrationDAO.class);

	public String getRegistrationNoFromBatch(String batchNo) throws RegistrationRuntimeException, LoadServiceException, CompoundManagementServiceException {
		CompoundManagementService containerService = CompoundManagementServiceFactory.getService();
		return containerService.getRegistrationNoFromBatch(batchNo);
	}

	public Map<String, RegistrationDetailsVO> getRegistrationInformation(String[] batchNos) throws RegistrationRuntimeException, LoadServiceException {
		Map<String, RegistrationDetailsVO> result = new HashMap<String, RegistrationDetailsVO>();

		for (String batchNo : batchNos) {
			try {
				List<Compound> compounds = SearchServiceFactory.getService().getCompoundInfoByBatchNo(batchNo);
				for (Compound compound : compounds) {
					RegistrationDetailsVO vo = new RegistrationDetailsVO();
					
					vo.setGlobalNumber(compound.getCompoundNo());
					vo.setGlobalCompoundNumber(compound.getConversationalBatchNo());
					vo.setBatchNumber(compound.getBatchNo());
					vo.setBatchComment(compound.getComment());
					vo.setStructureComment(compound.getComment());
					vo.setStereoisomerCode(compound.getStereoisomerCode());
					vo.setSaltCode(compound.getSaltCode());
					vo.setSaltMole(compound.getSaltEquivs());
					
					result.put(batchNo, vo);
				}
			} catch (Exception e) {
				LOG.debug("Error getting Registration Information for BatchNo " + batchNo + ". Skip this Batch.", e);
			}
		}

		return result;
	}

	public BarcodeValidationVO validateBarcode(BarcodeValidationVO barcodeValidationVO) throws RegistrationRuntimeException, LoadServiceException {
		CompoundManagementService containerService = CompoundManagementServiceFactory.getService();
		return containerService.validateBarcode(barcodeValidationVO);
	}
}