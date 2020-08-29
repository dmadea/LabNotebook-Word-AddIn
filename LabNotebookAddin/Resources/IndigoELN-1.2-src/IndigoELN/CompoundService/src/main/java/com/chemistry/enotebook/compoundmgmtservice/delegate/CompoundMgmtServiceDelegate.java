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
package com.chemistry.enotebook.compoundmgmtservice.delegate;

import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementCompoundBatch;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementContainer;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementOrderDetail;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundMgmtServiceException;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.compoundmgmtservice.interfaces.CompoundMgmtService;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.servicelocator.ServiceLocator;

import java.util.List;

public class CompoundMgmtServiceDelegate implements CompoundMgmtService {
	
	private CompoundMgmtService service;

	public CompoundMgmtServiceDelegate() {
		service = ServiceLocator.getInstance().locateService("CompoundService", CompoundMgmtService.class);
	}

	@Override
	public boolean checkHealth() throws CompoundMgmtServiceException {
		return service.checkHealth();
	}

	@Override
	public List<Compound> searchByStructure(List<String> dbList, String structure, String searchOperator, String searchOption) throws CompoundNotFoundException {
		return service.searchByStructure(dbList, structure, searchOperator, searchOption);
	}

	@Override
	public List<String> getAvailableDBList() throws CompoundMgmtServiceException {
		return service.getAvailableDBList();
	}

	@Override
	public List<String> getStructureByCompoundNo(String compoundNumber) throws CompoundNotFoundException {
		return service.getStructureByCompoundNo(compoundNumber);
	}

	@Override
	public List<String> getStructureByCasNo(String casNo) throws CompoundNotFoundException {
		return service.getStructureByCasNo(casNo);
	}

	@Override
	public List<String> getStructureByBatchNo(String batchNo) throws CompoundNotFoundException {
		return service.getStructureByBatchNo(batchNo);
	}

	@Override
	public List<Compound> getCompoundInfoByCompoundNo(String compoundNo) throws CompoundNotFoundException {
		return service.getCompoundInfoByCompoundNo(compoundNo);
	}

	@Override
	public List<Compound> getCompoundInfoByCasNo(String casNo) throws CompoundNotFoundException {
		return service.getCompoundInfoByCasNo(casNo);
	}

	@Override
	public List<Compound> getCompoundInfoByBatchNo(String batchNo) throws CompoundNotFoundException {
		return service.getCompoundInfoByBatchNo(batchNo);
	}
	
	@Override
	public CompoundManagementContainer getContainer(String containerTypeCode) throws CompoundManagementServiceException {
		return service.getContainer(containerTypeCode);
	}

	@Override
	public List<CompoundManagementOrderDetail> findOrders(String orderId) throws CompoundManagementServiceException {
		return service.findOrders(orderId);
	}

	@Override
	public CompoundManagementPlate getPlate(String plateBarcode) throws CompoundManagementServiceException {
		return service.getPlate(plateBarcode);
	}

	@Override
	public CompoundManagementCompoundBatch getCompoundBatch(String barcode) throws CompoundManagementServiceException {
		return service.getCompoundBatch(barcode);
	}
}
