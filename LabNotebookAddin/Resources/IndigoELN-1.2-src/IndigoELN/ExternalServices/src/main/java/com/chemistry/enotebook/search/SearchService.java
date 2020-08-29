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

package com.chemistry.enotebook.search;

import com.chemistry.enotebook.search.exceptions.SearchServiceException;

import java.util.List;

/**
 * Interface for external Search Service
 */
public interface SearchService {

	/**
	 * Check external service health
	 * 
	 * @return true if service running fine
	 * @throws SearchServiceException
	 */
	public boolean checkHealth() throws SearchServiceException;

	/**
	 * Get list of available DBs from external service
	 * 
	 * @return List of available DBs
	 * @throws SearchServiceException
	 */
	public List<String> getAvailableDBList() throws SearchServiceException;

	/**
	 * Search compounds in external DBs by structure
	 * 
	 * @param dbs
	 *            List of DBs where to search
	 * @param structure
	 *            Structure to search
	 * @param searchOperator
	 *            How to search - Substructure, Similarity or Exact
	 * @param searchOption
	 *            Using for Similarity, %
	 * @return List of compounds. Every compound should have CompoundNo or
	 *         BatchNo. BatchNo should be in "notebook-page-batch" or
	 *         "notebook-page-RXN" (for reactions) format.
	 * @throws SearchServiceException
	 */
	public List<Compound> searchByStructure(List<String> dbList,
			String structure, String searchOperator, String searchOption)
			throws SearchServiceException;

	/**
	 * Search structure using compound number
	 * 
	 * @param compoundNumber
	 *            compound number
	 * @return Structures with given compound number
	 * @throws SearchServiceException
	 */
	public List<String> getStructureByCompoundNo(String compoundNumber)
			throws SearchServiceException;

	/**
	 * Search structure using cas number
	 * 
	 * @param casNo
	 *            CAS Number
	 * @return Structures with given CAS Number
	 * @throws SearchServiceException
	 */
	public List<String> getStructureByCasNo(String casNo)
			throws SearchServiceException;

	/**
	 * Search structure using batch number
	 * 
	 * @param batchNo
	 *            batch number
	 * @return Structures with given batch number
	 * @throws SearchServiceException
	 */
	public List<String> getStructureByBatchNo(String batchNo)
			throws SearchServiceException;

	/**
	 * Search compound using compound number
	 * 
	 * @param compoundNo
	 *            compound number
	 * @return Compounds with given compound number
	 * @throws SearchServiceException
	 */
	public List<Compound> getCompoundInfoByCompoundNo(String compoundNo)
			throws SearchServiceException;

	/**
	 * Search compound using cas number
	 * 
	 * @param casNo
	 *            CAS Number
	 * @return Compounds with given CAS Number
	 * @throws SearchServiceException
	 */
	public List<Compound> getCompoundInfoByCasNo(String casNo)
			throws SearchServiceException;

	/**
	 * Search compound using batch number
	 * 
	 * @param batchNo
	 *            batch number
	 * @return Compounds with given batch number
	 * @throws SearchServiceException
	 */
	public List<Compound> getCompoundInfoByBatchNo(String batchNo)
			throws SearchServiceException;
}
