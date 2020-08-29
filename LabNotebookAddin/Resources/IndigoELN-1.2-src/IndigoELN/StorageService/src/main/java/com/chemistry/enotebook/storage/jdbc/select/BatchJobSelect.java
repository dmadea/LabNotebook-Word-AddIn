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
package com.chemistry.enotebook.storage.jdbc.select;

import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 *
 * Loads the BatchRegInfoModel from CEN_REGISTERED_BATCHES for handling
 * CompoundRegistration Registration 
 */
public class BatchJobSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(CROPageSelect.class);

	public BatchJobSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		BatchRegInfoModel regBatchInfo = new BatchRegInfoModel(rs.getString("REG_BATCH_KEY"));
		regBatchInfo.setRegistrationStatus(rs.getString("REGISTRATION_STATUS"));
		regBatchInfo.setSubmissionStatus(rs.getString("SUBMISSION_STATUS"));
		regBatchInfo.setStatus(rs.getString("STATUS"));
		regBatchInfo.setJobId(rs.getString("JOB_ID"));
		regBatchInfo.setOffset(CommonUtils.toInteger(rs.getString("COMPOUND_REGISTRATION_OFFSET")));
		regBatchInfo.setStatus(rs.getString("COMPOUND_MANAGEMENT_STATUS"));
		regBatchInfo.setStatus(rs.getString("COMPOUND_MGMT_STATUS_MESSAGE"));
		regBatchInfo.setStatus(rs.getString("PURIFICATION_SERVICE_STATUS"));
		regBatchInfo.setStatus(rs.getString("PUR_SERVICE_STATUS_MSG"));
		regBatchInfo.setStatus(rs.getString("COMPOUND_AGGREGATION_STATUS"));
		regBatchInfo.setStatus(rs.getString("CMPD_AGGREGATION_STATUS_MSG"));
		regBatchInfo.setBatchTrackingId(CommonUtils.toInteger(rs.getString("BATCH_TRACKING_ID")));
		regBatchInfo.setCompoundRegistrationStatusMessage(rs.getString("COMPOUND_REG_STATUS_MESSAGE"));

		regBatchInfo.setLoadedFromDB(true);
		regBatchInfo.setModelChanged(false);
		return regBatchInfo;
	}
}
