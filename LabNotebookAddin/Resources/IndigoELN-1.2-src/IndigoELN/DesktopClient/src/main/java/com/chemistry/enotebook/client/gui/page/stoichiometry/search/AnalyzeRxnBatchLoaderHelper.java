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
package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnBatchUpdateClient;
import com.chemistry.enotebook.domain.BatchModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

/**
 * 
 * 
 *
 */
public class AnalyzeRxnBatchLoaderHelper implements Runnable {
	private static final Log log = LogFactory.getLog(AnalyzeRxnBatchLoaderHelper.class);
	private BatchModel updatedBatch = new BatchModel();
	private String idToLookUp = null;
	private ArrayList<BatchModel> searchReturn = new ArrayList<BatchModel>();
	private AnalyzeRxnBatchUpdateClient model;

	public AnalyzeRxnBatchLoaderHelper(AnalyzeRxnBatchUpdateClient client, BatchModel batchToLoad, String id) {
		// Don't operate on the same batch as might be connected to the AWT
		// Event Thread
		idToLookUp = id;
		updatedBatch = batchToLoad.deepClone();
		// equals to work.
		model = client;
	}

	public void run() {
		// if this compound was not created by the notebook then lookup
		// the required details via CompoundAndBatchInfoUpdater
		if (updatedBatch != null && idToLookUp != null) {
			searchReturn.addAll((new CompoundAndBatchInfoUpdater()).getBatchesFromID(updatedBatch, idToLookUp, true));
			log.debug(searchReturn.size() + " values found for id " + idToLookUp);
			// reset loading flag
			model.syncBatchesFromSearch(updatedBatch, searchReturn);
		}
	}
}
