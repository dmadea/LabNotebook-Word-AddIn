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
import com.chemistry.enotebook.domain.BatchModel;

/**
 * 
 * 
 *
 */
public class StoichReagentBatchUpdater implements Runnable {
	private BatchModel _rb; // Copy of the batch on which we are operating
	private ReagentBatchUpdateClient _model; // call back object
	private boolean _updateModel = false; // flag indicating whether or
	// not to update the stoich.
	private boolean _lookupNeeded = false; // Force lookup if needed flag.
	// Force when true
	private boolean _overWriteFlag = false; // defaults to update mode -
	// load overwrites data.
	private boolean _displayMsgDlg = false; // Show the CompoundIDLookup

	// dialog to allow user
	// intervention
	/**
	 * 
	 */
	public StoichReagentBatchUpdater(ReagentBatchUpdateClient model, BatchModel rb, boolean displayMsgDlg, boolean updateModel,
			boolean lookupForced, boolean overWriteFlag) {
		super();
		_rb = rb; // copy of the batch to operate on.
		_model = model; // call back.
		_updateModel = updateModel; //
		_lookupNeeded = lookupForced;
		_overWriteFlag = overWriteFlag;
		_displayMsgDlg = displayMsgDlg;
	}

	public void run() {
		// Get the currently displayed page's stoich model
		_model.syncBatchFromSearch(_rb, _updateModel, _overWriteFlag);
	}

	public BatchModel getReagentBatch() {
		return _rb;
	}

	public boolean isLookupNeeded() {
		return _lookupNeeded;
	}

	public void setLookupNeeded(boolean lookup) {
		_lookupNeeded = lookup;
	}

	public boolean isOverWritingData() {
		return _overWriteFlag;
	}

	public void setOverWritingFlag(boolean overWritingFlg) {
		_overWriteFlag = overWritingFlg;
	}

	public boolean isMsgDisplayed() {
		return _displayMsgDlg;
	}

	public void setMsgDisplayFlag(boolean flag) {
		_displayMsgDlg = flag;
	}
}
