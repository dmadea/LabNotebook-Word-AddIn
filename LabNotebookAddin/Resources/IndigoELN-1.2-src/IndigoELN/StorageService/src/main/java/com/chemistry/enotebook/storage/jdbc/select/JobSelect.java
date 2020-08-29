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

import com.chemistry.enotebook.domain.JobModel;
import com.chemistry.enotebook.session.security.HttpUserMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JobSelect extends AbstractSelect {

	private static final Log log = LogFactory.getLog(JobSelect.class);

	public JobSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		log.debug("JobSelect(DataSource, String) called");
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		log.debug("mapRow(ResultSet, int) start");
		JobModel job = new JobModel();
		job.setJobID(rs.getString("JOB_ID"));
		job.setPageKey(rs.getString("PAGE_KEY"));
		job.setPlateKey(rs.getString("PLATE_KEY"));
		job.setCompoundRegistrationStatus(rs.getString("COMPOUND_REGISTRATION_STATUS"));
		job.setCompoundRegistrationStatusMessage(rs.getString("COMPOUND_REG_STATUS_MESSAGE"));
		job.setCompoundManagementStatus(rs.getString("COMPOUND_MANAGEMENT_STATUS"));
		job.setCompoundManagementStatusMessage(rs.getString("COMPOUND_MGMT_STATUS_MESSAGE"));
		job.setPurificationServiceStatus(rs.getString("PURIFICATION_SERVICE_STATUS"));
		job.setPurificationServiceStatusMessage(rs.getString("PUR_SERVICE_STATUS_MSG"));
		job.setCompoundAggregationStatus(rs.getString("COMPOUND_AGGREGATION_STATUS"));
		job.setCompoundAggregationStatusMessage(rs.getString("CMPD_AGGREGATION_STATUS_MSG"));
		job.setCallbackUrl(rs.getString("CALLBACK_URL"));
		HttpUserMessage userMessage = new HttpUserMessage();
		userMessage.setNotebook(rs.getString("NOTEBOOK"));
		userMessage.setPage(rs.getString("EXPERIMENT"));
		userMessage.setVersion(rs.getInt("PAGE_VERSION"));
		job.setUserMessage(userMessage);
		job.setModificationDate(rs.getTimestamp("MODIFIED_DATE"));
		job.setCompoundRegistrationJobId(rs.getString("COMPOUND_REGISTRATION_JOB_ID"));
		job.setBatchKeysString(rs.getString("BATCH_KEYS"));
		job.setPlateKeysString(rs.getString("PLATE_KEYS"));
		job.setWorkflowsString(rs.getString("WORKFLOW"));
		job.setJobStatus(rs.getString("STATUS"));
		job.setLoadedFromDB(true);
		job.setModelChanged(false);
		log.debug("mapRow(ResultSet, int) end");
		return job;
	}
}
