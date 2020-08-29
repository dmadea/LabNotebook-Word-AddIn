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
package com.chemistry.enotebook.compoundaggregation.clean;

import com.chemistry.enotebook.compoundaggregation.CompoundAggregationService;
import com.chemistry.enotebook.compoundaggregation.classes.CompoundAggregationBatchInfo;
import com.chemistry.enotebook.compoundaggregation.classes.CompoundAggregationResult;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanel;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanelSelector;
import com.chemistry.enotebook.compoundaggregation.exceptions.CompoundAggregationServiceException;

import javax.swing.*;


public class CompoundAggregationServiceCleanImpl implements CompoundAggregationService {
	public String[] getScreenPanelsNames(long[] screenPanelIDs) {
		return new String[0];
	}

	public CompoundAggregationResult submitBatchesToCompoundAggregation(CompoundAggregationBatchInfo[] pBatches, String compoundManagementEmployeeID, String siteCode, String projectCode) {
		return new CompoundAggregationResult("", true);
	}

	public ScreenPanel getScreenPanel(long screenKey) throws CompoundAggregationServiceException {
		return new ScreenPanel(null);
	}

	public ScreenPanelSelector getScreenPanelDialog()
			throws CompoundAggregationServiceException {
		return new ScreenPanelSelectorFakeImpl();
	}
	
	private class ScreenPanelSelectorFakeImpl extends ScreenPanelSelector {
		private static final long serialVersionUID = -4217014796811262586L;

		public ScreenPanelSelectorFakeImpl() {
			this.add(new JLabel("Screen panel selector."));
		}
		public ScreenPanel[] getSelectedPanels() {
			return new ScreenPanel[0];
		}
		public void initialize(String projectCode, ScreenPanel[] screenPanels) {
		}
	}

	public boolean isAvailable(String siteCode) {
		return true;
	};
}
