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

public class PlateJobSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(PlateJobSelect.class);

	public PlateJobSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}
	
public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		JobModel job = new JobModel();
		
		job.setJobID(rs.getString("JOB_ID"));
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
		job.setModificationDate(rs.getTimestamp("MODIFIED_DATE"));
		job.setPageKey(rs.getString("PAGE_KEY"));
		HttpUserMessage userMessage = new HttpUserMessage();
		//PAGE_KEY is not inserted for Plate jobs hence cannot join CEN_PAGES table
		//userMessage.setNotebook(rs.getString("NOTEBOOK"));
		//userMessage.setPage(rs.getString("EXPERIMENT"));
		//userMessage.setVersion(rs.getInt("VERSION"));
		job.setUserMessage(userMessage);
		job.setLoadedFromDB(true);
		job.setModelChanged(false);
		return job;
	}

}