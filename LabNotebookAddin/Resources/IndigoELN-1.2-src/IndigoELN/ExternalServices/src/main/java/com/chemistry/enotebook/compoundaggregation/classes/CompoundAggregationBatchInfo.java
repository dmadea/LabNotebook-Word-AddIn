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
package com.chemistry.enotebook.compoundaggregation.classes;

import java.io.Serializable;

public class CompoundAggregationBatchInfo implements Serializable {
	
	private static final long serialVersionUID = 8274975544416878762L;
	
	private long[] screenPanelIds;
	private long batchTrackingId;

	public CompoundAggregationBatchInfo(long[] screenPanelIds, long batchTrackingId) {
		this.screenPanelIds = screenPanelIds;
		this.batchTrackingId = batchTrackingId;
	}

	public long[] getScreenPanelIds() {
		return screenPanelIds;
	}

	public void setScreenPanelIds(long[] screenPanelIds) {
		this.screenPanelIds = screenPanelIds;
	}

	public long getBatchTrackingId() {
		return batchTrackingId;
	}

	public void setBatchTrackingId(long batchTrackingId) {
		this.batchTrackingId = batchTrackingId;
	}
}
