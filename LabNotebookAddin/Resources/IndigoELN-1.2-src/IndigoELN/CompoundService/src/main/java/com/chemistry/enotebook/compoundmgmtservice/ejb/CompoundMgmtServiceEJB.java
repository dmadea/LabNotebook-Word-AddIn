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
package com.chemistry.enotebook.compoundmgmtservice.ejb;

import com.chemistry.enotebook.compoundmanagement.CompoundManagementServiceFactory;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementCompoundBatch;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementContainer;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementOrderDetail;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundMgmtServiceException;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.compoundmgmtservice.interfaces.CompoundMgmtService;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.search.SearchServiceFactory;

import java.util.List;

public class CompoundMgmtServiceEJB implements CompoundMgmtService {
	
	@Override
	public boolean checkHealth() throws CompoundMgmtServiceException {
		try {
			return SearchServiceFactory.getService().checkHealth();
		} catch (Exception e) {
			throw new CompoundMgmtServiceException(e);
		}
	}

	@Override
	public List<Compound> searchByStructure(List<String> dbList, String structure, String searchOperator, String searchOption) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().searchByStructure(dbList, structure, searchOperator, searchOption);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}

	@Override
	public List<String> getAvailableDBList() throws CompoundMgmtServiceException {
		try {
			return SearchServiceFactory.getService().getAvailableDBList();
		} catch (Exception e) {
			throw new CompoundMgmtServiceException(e);
		}
	}

	@Override
	public List<String> getStructureByCompoundNo(String compoundNumber) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().getStructureByCompoundNo(compoundNumber);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}

	@Override
	public List<String> getStructureByCasNo(String casNo) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().getStructureByCasNo(casNo);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}

	@Override
	public List<String> getStructureByBatchNo(String batchNo) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().getStructureByBatchNo(batchNo);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}

	@Override
	public List<Compound> getCompoundInfoByCompoundNo(String compoundNo) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().getCompoundInfoByCompoundNo(compoundNo);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}

	@Override
	public List<Compound> getCompoundInfoByCasNo(String casNo) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().getCompoundInfoByCasNo(casNo);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}

	@Override
	public List<Compound> getCompoundInfoByBatchNo(String batchNo) throws CompoundNotFoundException {
		try {
			return SearchServiceFactory.getService().getCompoundInfoByBatchNo(batchNo);
		} catch (Exception e) {
			throw new CompoundNotFoundException(e);
		}
	}
	
	@Override
	public CompoundManagementContainer getContainer(String containerTypeCode) throws CompoundManagementServiceException {
		try {
			return CompoundManagementServiceFactory.getService().getContainer(containerTypeCode);
		} catch (Exception e) {
			throw new CompoundManagementServiceException(e);
		}
	}

	@Override
	public List<CompoundManagementOrderDetail> findOrders(String orderId) throws CompoundManagementServiceException {
		try {
			return CompoundManagementServiceFactory.getService().findOrders(orderId);
		} catch (Exception e) {
			throw new CompoundManagementServiceException(e);
		}
	}

	@Override
	public CompoundManagementPlate getPlate(String plateBarcode) throws CompoundManagementServiceException {
		try {
			return CompoundManagementServiceFactory.getService().getPlate(plateBarcode);
		} catch (Exception e) {
			throw new CompoundManagementServiceException(e);
		}
	}

	@Override
	public CompoundManagementCompoundBatch getCompoundBatch(String barcode) throws CompoundManagementServiceException {
		try {
			return CompoundManagementServiceFactory.getService().getCompoundBatch(barcode);
		} catch (Exception e) {
			throw new CompoundManagementServiceException(e);
		}
	}
}
