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
 * Created on May 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage;

import com.chemistry.enotebook.experiment.utils.xml.JDomUtils;
import org.jdom.Document;
import org.jdom.JDOMException;

import java.io.Serializable;

/**
 * 
 * 
 * Purpose: Used to pull back information based on a single CeN BatchNumber. Use BatchNumberSearchContext to bring back batch level
 * responses to handle search criteria.
 * 
 * Use: Fill in searchBatchNumber. There should be an index on the table containing the batches and the batcnNumber should be
 * unique. If it isn't all batch references will be returned.
 */
public class BatchNumberSearchContext implements StorageContextInterface, Serializable {
	
	private static final long serialVersionUID = 8701003300179774910L;

	/**
	 * Creates an empty context. Need to call setBatchNumber(String) which accepts any entry. So be careful. Results returned in
	 * StorageVO format.
	 * 
	 */
	public BatchNumberSearchContext() {
	}

	/**
	 * Expects a properly formatted CeN batchnumber. Send the context to the Storage delegate for processing. Results are in the
	 * form of StorageVO in getResults();
	 * 
	 * @param searchBatchNumber -
	 *            properly formatted CeNBatchNumber
	 * @throws StorageException
	 */
	public BatchNumberSearchContext(String searchBatchNumber) throws StorageException {
		setBatchNumber(searchBatchNumber);
	}

	private String _searchBatchNumber;
	private StorageVO _results = null;

	/**
	 * Returns the search criteria and the results in XML format <BatchNumberSearchCriteria> <SearchCriteria> <BatchNumber/>
	 * </SearchCriteria> <SearchResults> <Row> WARNING: Currently not implemented and will only return the SearchCriteria section
	 * </Row> </SearchResults>
	 */
	public String getXml() {
		StringBuffer xml = new StringBuffer();

		xml.append("<BatchNumberSearchContext>\n" + "\t<SearchCriteria>" + "\t\t<BatchNumber>" + _searchBatchNumber
				+ "</BatchNumber>" + "\t</SearchCriteria>\n" + "\t<SearchResults>\n" + "\t</SearchResults>\n"
				+ "</BatchNumberSearchContext>\n");
		return xml.toString();
	}

	// TODO: change setXML to throw an exception.
	/**
	 * Use only to set the search criteria: BatchNumber
	 */
	public void setXml(String xml) throws StorageException {
		// //System.out.println("BatchNumberSearchContext::setXml: Not supported yet.");
		Document xmlReturn = null;
		try {
			xmlReturn = JDomUtils.getDocFromString(xml);
		} catch (JDOMException e) {
			throw new StorageException("setXML(): Sent in a possible bad or null XML document: " + xml, e);
		} catch (Exception e) {
			throw new StorageException("Failed to setXML().\n" + e.toString(), e);
		}

		// fill in compound information
		if (xmlReturn != null && xmlReturn.hasRootElement()) {
			setBatchNumber(JDomUtils.getText(xmlReturn, "BatchNumber"));
		}
	}

	public String getBatchNumber() {
		return _searchBatchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		_searchBatchNumber = batchNumber;
	}

	public StorageVO getResults() {
		return _results;
	}

	public void setResults(StorageVO vo) {
		_results = vo;
	}
}
