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
package com.ggasoftware.indigo.eln.services.search;

import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.search.SearchService;
import com.chemistry.enotebook.search.exceptions.SearchServiceException;
import com.epam.indigo.crs.classes.CompoundRegistrationStatus;
import com.epam.indigo.crs.classes.FullCompoundInfo;
import com.epam.indigo.crs.services.search.BingoSearch;
import com.ggasoftware.indigo.eln.services.crs.CrsConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchServiceImpl implements SearchService {
	
	private static final String SUBSTRUCTURE = "SUBSTRUCTURE";
	private static final String SIMILARITY = "SIMILARITY";
	private static final String SMARTS = "SMARTS";
	private static final String EXACT = "EXACT";
	
	private static final String DEFAULT_SIMILARITY = "tanimoto";

    private BingoSearch search;
	
	public SearchServiceImpl() {
        try {
            search = CrsConnection.getSearchService();
        } catch (SearchServiceException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
	
	@Override
	public boolean checkHealth() throws SearchServiceException {
		return (search != null);
	}

	@Override
	public List<String> getAvailableDBList() throws SearchServiceException {
		return Arrays.asList("IndigoELN");
	}

	@Override
	public synchronized List<Compound> searchByStructure(List<String> dbList, String structure, String searchOperator, String searchOption) throws SearchServiceException {
		List<Compound> result = new ArrayList<Compound>();
		List<Integer> searchResult = null;
		
		try {
			if (SUBSTRUCTURE.equalsIgnoreCase(searchOperator))
				searchResult = search.searchSub(structure, searchOption);
		
			if (SIMILARITY.equalsIgnoreCase(searchOperator))
				searchResult = search.searchSim(structure, DEFAULT_SIMILARITY, Double.parseDouble(searchOption) / 100, (double) 1);
		
			if (SMARTS.equalsIgnoreCase(searchOperator))
				searchResult = search.searchSmarts(structure);
			
			if (EXACT.equalsIgnoreCase(searchOperator)) {
				if (searchOption.equals(""))
					searchOption = "ALL";
				searchResult = search.searchExact(structure, searchOption);
			}
		} catch (Exception e) {
			throw new SearchServiceException(e);
		}
		
		if (searchResult != null)
			for (Integer i : searchResult)
				try {
					result.add(populateCompound(search.getCompoundById(i)));
				} catch (Exception e) {
					// Just skip this compound
				}
		
		return result;
	}

	@Override
	public synchronized List<String> getStructureByCompoundNo(String compoundNumber) throws SearchServiceException {
		List<String> structures = new ArrayList<String>();
		
		List<Compound> compounds = getCompoundInfoByCompoundNo(compoundNumber);
		for (Compound compound : compounds)
			structures.add(compound.getStructure());
		
		return structures;
	}

	@Override
	public synchronized List<String> getStructureByCasNo(String casNo) throws SearchServiceException {
		List<String> structures = new ArrayList<String>();
		
		List<Compound> compounds = getCompoundInfoByCasNo(casNo);
		for (Compound compound : compounds)
			structures.add(compound.getStructure());
		
		return structures;
	}

	@Override
	public synchronized List<String> getStructureByBatchNo(String batchNo) throws SearchServiceException {
		List<String> structures = new ArrayList<String>();
		
		List<Compound> compounds = getCompoundInfoByBatchNo(batchNo);
		for (Compound compound : compounds)
			structures.add(compound.getStructure());
		
		return structures;

	}

	@Override
	public synchronized List<Compound> getCompoundInfoByCompoundNo(String compoundNo) throws SearchServiceException {
		List<Compound> result = new ArrayList<Compound>();
		try {	
			List<FullCompoundInfo> list = search.getCompoundByNumber(compoundNo);
			for (FullCompoundInfo info : list)
				result.add(populateCompound(info));
		} catch (Exception e) {
			throw new SearchServiceException(e);
		}
		return result;
	}

	@Override
	public synchronized List<Compound> getCompoundInfoByCasNo(String casNo) throws SearchServiceException {
		List<Compound> result = new ArrayList<Compound>();
		try {	
			List<FullCompoundInfo> list = search.getCompoundByCasNumber(casNo);
			for (FullCompoundInfo info : list)
				result.add(populateCompound(info));
		} catch (Exception e) {
			throw new SearchServiceException(e);
		}
		return result;
	}

	@Override
	public synchronized List<Compound> getCompoundInfoByBatchNo(String batchNo) throws SearchServiceException {
		List<Compound> result = new ArrayList<Compound>();
		try {
			result.add(populateCompound(search.getCompoundByBatchNumber(batchNo)));
		} catch (Exception e) {
			throw new SearchServiceException(e);
		}
		return result;
	}

	public static Compound populateCompound(FullCompoundInfo searchResult) {
		if (searchResult == null)
			return null;
		
		Compound compound = new Compound();
		
		compound.setStructure(searchResult.getData());
		compound.setCompoundNo(searchResult.getCompoundNumber());
		compound.setConversationalBatchNo(searchResult.getConversationalBatchNumber());
		compound.setBatchNo(searchResult.getBatchNumber());
		compound.setCasNo(searchResult.getCasNumber());
		compound.setSaltCode(searchResult.getSaltCode());
		compound.setSaltEquivs(searchResult.getSaltEquivalents());
		compound.setComment(searchResult.getComments());
		compound.setHazardComment(searchResult.getHazardComments());
		compound.setStereoisomerCode(searchResult.getStereoIsomerCode());
		compound.setStorageComment(searchResult.getStorageComments());
		
		if (searchResult.getRegistrationStatus() == CompoundRegistrationStatus.SUCCESSFUL) {
			compound.setRegistrationStatus("PASSED");
		} else {
			compound.setRegistrationStatus("FAILED");
		}
		
		return compound;
	}
}
