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

import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.ReagentBatchUpdateClient;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.search.Compound;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 *
 */
public class ReagentCompoundLoader extends SwingWorker {
	private StoichReagentBatchUpdater updater;
	private ArrayList<Exception> errors = new ArrayList<Exception>();

	public ReagentCompoundLoader(ReagentBatchUpdateClient model, BatchModel rb, boolean bDisplayMsgDlg, boolean bUpdateModel,
			boolean bForceLookup, boolean bOverWriteFlag) {
		this(model, rb, bDisplayMsgDlg, bUpdateModel, bForceLookup, bOverWriteFlag, true);
	}

	public ReagentCompoundLoader(ReagentBatchUpdateClient model, BatchModel rb, boolean bDisplayMsgDlg, boolean bUpdateModel,
			boolean bForceLookup, boolean bOverWriteFlag, boolean bUseStopOnInterrupt) {
		super(bUseStopOnInterrupt);
		// Don't operate on the same batch as might be connected to the AWT Event Thread
		BatchModel rbSearch = rb.deepClone();
		// sync later in finished section
		rbSearch.setLoadedFromDB(rb.isLoadedFromDB());
		updater = new StoichReagentBatchUpdater(model, rbSearch, bDisplayMsgDlg, bUpdateModel, bForceLookup, bOverWriteFlag);
	}

	public boolean isOverWritingData() {
		return updater.isOverWritingData();
	}

	public void setOverWriteingFlag(boolean flag) {
		updater.setOverWritingFlag(flag);
	}

	public Object construct() {
		BatchModel reagent = updater.getReagentBatch();
		// if this compound was not created by the notebook then lookup
		// the required details via CompoundAndBatchInfoUpdater
		if (reagent != null) {
			reagent.setLoadingFromDB(true);
			ParentCompoundModel cmpd = reagent.getCompound();
			cmpd.setLoadingFromDB(reagent.isLoadingFromDB());
			if (updater.isLookupNeeded() || (CeNNumberUtils.doubleEquals(cmpd.getMolWgt(), 0.0) && !cmpd.isCreatedByNotebook())) {
				if (StringUtils.isNotBlank(cmpd.getRegNumber())) {
					(new CompoundAndBatchInfoUpdater()).getCompoundInfoFromValue(reagent,
					                                                             cmpd.getRegNumber(), 
					                                                             updater.isMsgDisplayed(), 
					                                                             updater.isOverWritingData());
				} else if (StringUtils.isNotBlank(cmpd.getCASNumber())) {
					(new CompoundAndBatchInfoUpdater()).getCompoundInfoFromCasNumber(reagent,
					                                                                 cmpd.getCASNumber(), 
					                                                                 updater.isMsgDisplayed(), 
					                                                                 updater.isOverWritingData());
				} else if (StringUtils.isNotBlank(reagent.getBatchNumberAsString())) {
					(new CompoundAndBatchInfoUpdater()).getCompoundInfoFromBatchNumber(reagent, 
					                                                                   reagent.getBatchNumberAsString(),
					                                                                   updater.isMsgDisplayed(), 
					                                                                   updater.isOverWritingData());
				}
			}
			// if there is only a structure we need to check if anything is yet registered
			// just check PF database as it will only be newly registered PF structure
			else if (StringUtils.isBlank(reagent.getBatchNumberAsString()) && StringUtils.isBlank(cmpd.getRegNumber())) 
			{
				if (cmpd.getNativeSketch() != null && cmpd.getNativeSketch().length > 0) {
					try {
                        String [] dbList = new String [] {"DB_2"};
						String srchOperator = "=";
						String srchOptionValues = "";
						byte[] molFile = new ChemistryDelegate().convertChemistry(cmpd.getNativeSketch(), "", "MDL Molfile");
												
						List<Compound> compoundIDs = new CompoundMgmtServiceDelegate().searchByStructure(Arrays.asList(dbList), new String(molFile), srchOperator, srchOptionValues);
						if (compoundIDs.size() > 0) {
							// a registered structure has been found so update the regnumber
							cmpd.setRegNumber(compoundIDs.get(0).getCompoundNo());
						}
					} catch (ChemUtilAccessException e) {
						errors.add(new StructureLookupException("An error has occurred while accessing Chemistry System", e));
					} catch (Exception e) {
						errors.add(new StructureLookupException("An error has occurred while searching for structures", e));
					}
				}
			}
			// reset loading flag
			reagent.setLoadingFromDB(false);
			cmpd.setLoadingFromDB(false);
		}
		return updater;
	}

	public void finished() {
		SwingUtilities.invokeLater(updater);
	}
}
