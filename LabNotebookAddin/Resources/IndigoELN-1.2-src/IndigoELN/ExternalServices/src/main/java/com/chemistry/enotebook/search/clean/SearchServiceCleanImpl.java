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

package com.chemistry.enotebook.search.clean;

import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.search.SearchService;
import com.chemistry.enotebook.search.exceptions.SearchServiceException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;
import java.util.List;

public class SearchServiceCleanImpl implements SearchService {

	@Override
	public boolean checkHealth() throws SearchServiceException {
		return true;
	}

	@Override
	public List<String> getAvailableDBList() throws SearchServiceException {
		Element root = new Element("Databases");
		Document doc = new Document(root);

		XMLOutputter xmlout = new XMLOutputter();

		Element e = new Element("Database");
		e.setAttribute("displayName", "Reactions");
		e.setAttribute("count", new Long(0).toString());
		root.addContent(e);

		List<String> dbs = new ArrayList<String>();
		dbs.add(xmlout.outputString(doc));
		
		return dbs;
	}	
	
	@Override
	public List<Compound> searchByStructure(List<String> dbList, String structure, String searchOperator, String searchOption) throws SearchServiceException {
		List<Compound> compounds = new ArrayList<Compound>();
		return compounds;
	}

	@Override
	public List<String> getStructureByCompoundNo(String compoundNumber) throws SearchServiceException {
		List<String> structures = new ArrayList<String>();
		return structures;
	}

	@Override
	public List<String> getStructureByCasNo(String casNo) throws SearchServiceException {
		return getStructureByCompoundNo(casNo);
	}


	@Override
	public List<String> getStructureByBatchNo(String batchNo) throws SearchServiceException {
		return getStructureByCompoundNo(batchNo);
	}
	
	@Override
	public List<Compound> getCompoundInfoByCompoundNo(String compoundNo) throws SearchServiceException {
		List<Compound> compounds = new ArrayList<Compound>();
		return compounds;
	}

	@Override
	public List<Compound> getCompoundInfoByCasNo(String casNo) throws SearchServiceException {
		return getCompoundInfoByCompoundNo(casNo);
	}

	@Override
	public List<Compound> getCompoundInfoByBatchNo(String batchNo) throws SearchServiceException {
		return getCompoundInfoByCompoundNo(batchNo);
	}
}
