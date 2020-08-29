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

import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnBatchIDClient;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnLoader;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.searchquery.SearchQueryParams;
import com.chemistry.enotebook.util.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 
 *
 */
public class AnalyzeRxnBatchIDLoader implements Runnable, AnalyzeRxnLoader {

	private static final Log log = LogFactory.getLog(AnalyzeRxnBatchIDLoader.class);

	private final List<BatchModel> _Batches = new ArrayList<BatchModel>();
	
	// else
	private volatile boolean _finishedFlag = false;
	private volatile boolean _cancelFlag = false;
	private final BatchModel _batch; // Batch containing structure searched.
	
	// list of batch ids to update
	private final AnalyzeRxnBatchIDClient _model; // call back object
	private final String[] _dbList; // used to search cls
	
	// used to tell when something went wrong.
	private final List<Exception> _errors = Collections.synchronizedList(new ArrayList<Exception>());

	public AnalyzeRxnBatchIDLoader(AnalyzeRxnBatchIDClient model, BatchModel rb, String[] dbList) {
		_model = model;
		_batch = rb;
		_dbList = dbList;
	}

	public void run() {
		ParentCompoundModel cmp = _batch.getCompound();
		if (!_cancelFlag && cmp != null && cmp.getNativeSketch() != null && cmp.getNativeSketch().length > 0) {
			log.debug("initialization query params...");
			// Search CeN
			byte[] molFile = null;
			SearchQueryParams queryParam = new SearchQueryParams();
			try {
				ChemistryDelegate chemDel = new ChemistryDelegate();
				molFile = chemDel.convertChemistry(cmp.getNativeSketch(), "", "MDL Molfile");
			} catch (ChemUtilAccessException e) {
				_errors.add(new StructureLookupException("Failed to access Chemistry System.\n" + ExceptionUtils.getStackTrace(e),
						e));
				log.error("Failed to initialize Chemistry System.", e);
			}
			if (molFile != null && molFile.length > 0 && cmp.getMolWgt() > 0) {
				// Search CeN
				queryParam.setQueryChemistry(molFile);
				queryParam.setStructureSearchType("Exact");
				if (!_cancelFlag && _dbList != null && _dbList.length > 0) {
					try {
						String srchOperator = "Substructure";
						// String srchOptionValues = "TAU/MET/CHA/MAS";
						String srchOptionValues = "";

						ArrayList<String> dblist = new ArrayList<String>();
						for (int i = 0; i < _dbList.length; i++) {
							String s = _dbList[i];
							String[] sArray = s.split("/");
							if (sArray.length > 1) {
								dblist.add(sArray[sArray.length - 1]);
							}
						}
						
						List<Compound> compounds = new CompoundMgmtServiceDelegate().searchByStructure(dblist, new String(molFile), srchOperator, srchOptionValues);

						_Batches.addAll(CompoundAndBatchInfoUpdater.processSearchReturn(compounds));

						log.debug("found " + _Batches.size() + " compounds");
					} catch (Exception e2) {
						if (e2.getMessage().indexOf("contains query features") > 0) {
							_errors.add(new StructureLookupException(e2.getLocalizedMessage()));
							log.error(e2.getLocalizedMessage(), e2);
						} else {
							_errors.add(new StructureLookupException(
								"Failed searching corporate resources: Could not find structure.\n"
										+ ExceptionUtils.getStackTrace(e2), e2));
							log.error("Failed searching corporate resources: Could not find structure.", e2);
						}
					}
				}
			}
		}
		
		if (!_cancelFlag) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					_model.syncBatchesFromSearch(_batch, _Batches);
				}
			});
		}
	}

	/**
	 * Will prevent return of data. Attempts to gracefully finish.
	 */
	public void cancelSearch() {
		_cancelFlag = true;
	}

	public boolean isFinished() {
		return _cancelFlag || _finishedFlag;
	}

	public void setFinishedFlag(boolean flg) {
		_finishedFlag = flg;
	}
}
