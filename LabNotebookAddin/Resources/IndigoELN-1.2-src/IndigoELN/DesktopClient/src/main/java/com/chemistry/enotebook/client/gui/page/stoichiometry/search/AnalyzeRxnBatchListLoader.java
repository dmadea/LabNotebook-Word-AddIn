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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 
 *
 * 
 * Artifact submitted to the ThreadPool for loading.
 */
public class AnalyzeRxnBatchListLoader implements Runnable {
	private final BatchModel _batchMatched; // Reference to the batch whose
	// ids are being loaded
	private volatile List<BatchModel> _searchReturn = Collections.synchronizedList(new ArrayList<BatchModel>()); // List of batches loaded
	private final List<String> _idsToLookUp; // List of ids that needed to be
	// searched
	private final AnalyzeRxnBatchUpdateClient _model; // call back object

	// followup after completion of
	// callback
	/**
	 * 
	 */
	public AnalyzeRxnBatchListLoader(AnalyzeRxnBatchUpdateClient model, 
	                                 final BatchModel batchMatched,
	                                 final List<String> idsToLookUp) 
	{
		_idsToLookUp = idsToLookUp; // copy of the batch to operate on.
		// Since it is strings things are duped
		// automatically.
		_batchMatched = batchMatched;
		_model = model; // callback 1
	}

	public void run() {
		// Get the currently displayed page's stoich model
		_model.syncBatchesFromSearch(_batchMatched, _searchReturn);
	}

	public List<BatchModel> getReagentBatches() {
		return _searchReturn;
	}

	public List<String> getIDsSearched() {
		return _idsToLookUp;
	}

	public boolean isLoadingData() {
		return true;
	}
}
