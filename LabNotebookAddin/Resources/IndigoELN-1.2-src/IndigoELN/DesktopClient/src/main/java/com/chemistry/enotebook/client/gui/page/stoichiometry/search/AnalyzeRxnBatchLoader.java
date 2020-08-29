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

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnBatchUpdateClient;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnLoader;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 *
 */
public class AnalyzeRxnBatchLoader implements Runnable, AnalyzeRxnBatchUpdateClient, AnalyzeRxnLoader {
	private static final Log log = LogFactory.getLog(AnalyzeRxnBatchLoader.class);
	private final AnalyzeRxnBatchListLoader _updater;
	private final ArrayList<String> _cenBatchIDs = new ArrayList<String>();
	private int taskCounter = 0;
	private volatile boolean cancelFlag = false;
	private volatile boolean finishedFlag = false;
	private PooledExecutor batchLoaderPool;

	public AnalyzeRxnBatchLoader(AnalyzeRxnBatchUpdateClient model, PooledExecutor pool, final BatchModel batchMatched,
	                             final List<String> idsToLookUp) 
	{
		// Don't operate on the same batch as might be connected to the AWT
		// Event Thread
		batchLoaderPool = pool;
		_updater = new AnalyzeRxnBatchListLoader(model, batchMatched, idsToLookUp);
	}

	public void run() {
		// Start ThreadPool
		if (batchLoaderPool != null) {
			for (Iterator<String> i = _updater.getIDsSearched().iterator(); i.hasNext() && !cancelFlag;) {
				// Determine type of id and place in the proper place
				MonomerBatchModel rb = new MonomerBatchModel();
				rb.setBatchType(BatchType.REACTANT);
				String batchID = i.next();
				if (NotebookPageUtil.isValidCeNBatchNumber(batchID)){
					_cenBatchIDs.add(batchID);
				}
				AnalyzeRxnBatchLoaderHelper batchLoaderTask = new AnalyzeRxnBatchLoaderHelper(this, rb, batchID);
				++taskCounter;
				try {
					_updater.getReagentBatches().add(rb);
					batchLoaderPool.execute(batchLoaderTask);
				} catch (InterruptedException e) {
					log.warn("Loading of Batch ids was interrupted.");
				}
			}
		} else {
			CeNErrorHandler.getInstance().logErrorMsgWithoutDisplay("No execution pool available.", "Failed to Load batch data.");
			cancelFlag = true;
		}
	}

	public void syncBatchesFromSearch(BatchModel batchSearched, List<BatchModel> searchReturn) {
		List<BatchModel> batches = _updater.getReagentBatches();
		--taskCounter;
		if (batches.contains(batchSearched)) {
			batches.remove(batchSearched);
		}
		if (searchReturn != null)
			batches.addAll(searchReturn);
		if (!cancelFlag && taskCounter <= 0) {
			removeDuplicateBatches(batches);
			SwingUtilities.invokeLater(_updater);
		}
	}

	/**
	 * 
	 * param batches list to search for duplicate conversational batch numbers.
	 */
	private void removeDuplicateBatches(List<BatchModel> batches) {
		List<BatchModel> duplicatesToRemove = new ArrayList<BatchModel>();
		// There is a problem where a CeN batch will load and the corresponding
		// registered batch will load.
		// The request was to remove the CeN batch iff conv. batch numbers matched.
		// CeN Batches are the first in the list hence we can produce a list of each id.
		// if the list length > 1 then we remove the 0th entry from the return list.
		boolean found = false;
		BatchModel firstOccurance = null;
		for (Iterator<String> i = _cenBatchIDs.iterator(); i.hasNext();) {
			String batchID = i.next();
			for (Iterator<BatchModel> batch = batches.iterator(); !found && batch.hasNext();) {
				BatchModel rb = batch.next();
				if (batchID.equals(rb.getBatchNumberAsString()) && StringUtils.isNotBlank(rb.getConversationalBatchNumber())) {
					if (firstOccurance == null) {
						firstOccurance = rb;
					} else {
						duplicatesToRemove.add(firstOccurance);
						found = true;
					}
				}
			}
			firstOccurance = null;
			found = false;
		}
		for (Iterator<BatchModel> i = duplicatesToRemove.iterator(); i.hasNext();)
			batches.remove(i.next());
	}

	public void cancelSearch() {
		cancelFlag = true;
	}

	public boolean isFinished() {
		return cancelFlag || finishedFlag;
	}

	public void setFinishedFlag(boolean flg) {
		finishedFlag = flg;
	}
}
