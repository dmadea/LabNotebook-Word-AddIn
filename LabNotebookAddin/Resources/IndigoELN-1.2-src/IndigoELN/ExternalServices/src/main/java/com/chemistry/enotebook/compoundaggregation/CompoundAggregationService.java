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
package com.chemistry.enotebook.compoundaggregation;

import com.chemistry.enotebook.compoundaggregation.classes.CompoundAggregationBatchInfo;
import com.chemistry.enotebook.compoundaggregation.classes.CompoundAggregationResult;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanel;
import com.chemistry.enotebook.compoundaggregation.classes.ScreenPanelSelector;
import com.chemistry.enotebook.compoundaggregation.exceptions.CompoundAggregationServiceException;

/**
 * Interface for external compound aggregation service
 */
public interface CompoundAggregationService {

	/**
	 * Check health for given site
	 * 
	 * @param siteCode
	 *            site code to check health
	 * @return true if service available, false if service unavailable
	 */
	public boolean isAvailable(String siteCode);

	/**
	 * Get Screen Panel names for given IDs
	 * 
	 * @param screenPanelIDs
	 *            Screen Panel IDs
	 * @return Screen Panel names
	 * @throws CompoundAggregationServiceException
	 */
	public String[] getScreenPanelsNames(long[] screenPanelIDs)
			throws CompoundAggregationServiceException;

	/**
	 * Submit batches to compound aggregation service
	 * 
	 * @param pBatches
	 *            batches to submit
	 * @param compoundManagementEmployeeID
	 *            user id in compound management service
	 * @param siteCode
	 *            site code where to do aggregation
	 * @param projectCode
	 *            project code where to do aggregation
	 * @return Result of compound aggregation
	 * @throws CompoundAggregationServiceException
	 */
	public CompoundAggregationResult submitBatchesToCompoundAggregation(
			CompoundAggregationBatchInfo[] pBatches,
			String compoundManagementEmployeeID, String siteCode,
			String projectCode) throws CompoundAggregationServiceException;

	/**
	 * Get GUI dialog from service to show in Indigo ELN
	 * 
	 * @return GUI dialog
	 * @throws CompoundAggregationServiceException
	 */
	public ScreenPanelSelector getScreenPanelDialog()
			throws CompoundAggregationServiceException;

	/**
	 * Retrieve Screen Panel from service
	 * 
	 * @param screenKey
	 *            Screen Panel ID
	 * @return Screen Panel with given ID
	 * @throws CompoundAggregationServiceException
	 */
	public ScreenPanel getScreenPanel(long screenKey)
			throws CompoundAggregationServiceException;
}
