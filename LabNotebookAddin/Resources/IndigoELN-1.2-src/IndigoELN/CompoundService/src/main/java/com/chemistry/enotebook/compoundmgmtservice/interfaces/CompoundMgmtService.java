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

package com.chemistry.enotebook.compoundmgmtservice.interfaces;

import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementCompoundBatch;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementContainer;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementOrderDetail;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate;
import com.chemistry.enotebook.compoundmanagement.exceptions.CompoundManagementServiceException;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundMgmtServiceException;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.search.Compound;

import java.util.List;

public interface CompoundMgmtService {

	public boolean checkHealth() throws CompoundMgmtServiceException;

	public List<Compound> searchByStructure(List<String> dbList, String structure, String searchOperator, String searchOption) throws CompoundNotFoundException;

	public List<String> getAvailableDBList() throws CompoundMgmtServiceException;

	public List<String> getStructureByCompoundNo(String compoundNumber) throws CompoundNotFoundException;

	public List<String> getStructureByCasNo(String casNo) throws CompoundNotFoundException;

	public List<String> getStructureByBatchNo(String batchNo) throws CompoundNotFoundException;

	public List<Compound> getCompoundInfoByCompoundNo(String compoundNo) throws CompoundNotFoundException;

	public List<Compound> getCompoundInfoByCasNo(String casNo) throws CompoundNotFoundException;

	public List<Compound> getCompoundInfoByBatchNo(String batchNo) throws CompoundNotFoundException;
	
	public CompoundManagementContainer getContainer(String containerTypeCode) throws CompoundManagementServiceException;

	public List<CompoundManagementOrderDetail> findOrders(String orderId) throws CompoundManagementServiceException;

	public CompoundManagementPlate getPlate(String plateBarcode) throws CompoundManagementServiceException;

	public CompoundManagementCompoundBatch getCompoundBatch(String barcode) throws CompoundManagementServiceException;
}
