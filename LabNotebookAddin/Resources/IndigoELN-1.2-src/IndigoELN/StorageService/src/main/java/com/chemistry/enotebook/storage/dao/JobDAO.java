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
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.JobModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.JobInsert;
import com.chemistry.enotebook.storage.jdbc.select.BatchJobSelect;
import com.chemistry.enotebook.storage.jdbc.select.JobSelect;
import com.chemistry.enotebook.storage.jdbc.select.PlateJobSelect;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.storage.query.UpdateQueryGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * Class will be handling the Database interactions required for CeNRegistrationManager module.
 */
public class JobDAO extends StorageDAO {
	private static final Log log = LogFactory.getLog(JobDAO.class);
	
	private String insertRegistrationJob(final JobModel job) {
		log.debug("insertRegistrationJob(JobModel) start");
		//JobInsert insertJob = new JobInsert(this.getDataSource());

		try {
			KeyHolder holder = new GeneratedKeyHolder();
			JdbcTemplate template = new JdbcTemplate(getDataSource());
			
			template.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(JobInsert.getInsertJobQuery(), new String[] { "job_id" });
					
					ps.setString(1, job.getPlateKey()); // PLATE_KEY
					ps.setString(2, job.getCompoundRegistrationStatus()); // COMPOUND_REGISTRATION_STATUS
					ps.setString(3, job.getCompoundRegistrationStatusMessage()); // COMPOUND_REG_STATUS_MESSAGE
					ps.setString(4, job.getCompoundManagementStatus()); // COMPOUND_MANAGEMENT_STATUS
					ps.setString(5, job.getCompoundManagementStatusMessage()); // COMPOUND_MGMT_STATUS_MESSAGE
					ps.setString(6, job.getPurificationServiceStatus()); // PURIFICATION_SERVICE_STATUS
					ps.setString(7, job.getPurificationServiceStatusMessage()); // PUR_SERVICE_STATUS_MSG
					ps.setString(8, job.getCompoundAggregationStatus()); // COMPOUND_AGGREGATION_STATUS
					ps.setString(9, job.getCompoundRegistrationStatusMessage());// CMPD_AGGREGATION_STATUS_MSG
					ps.setString(10, job.getCallbackUrl()); // CALLBACK_URL
					ps.setString(11, job.getPageKey()); // PAGE_KEY
					ps.setString(12, job.getCompoundRegistrationJobId()); // COMPOUND_REGISTRATION_JOB_ID
					ps.setString(13, job.getBatchKeysString()); // BATCH_KEYS
					ps.setString(14, job.getPlateKeysString()); // PLATE_KEYS
					ps.setString(15, job.getWorkflowsString()); // WORKFLOW
					ps.setString(16, job.getJobStatus());
					
					return ps;
				}
			}, holder);
			
			return holder.getKey().toString();
		} catch (Exception e) {
			throw new JDBCRuntimeException(e);
		}
	}
	
	public String[] insertRegistrationJobs(JobModel[] jobs) {
		log.debug("insertRegistrationJobs(JobModel[]) start");
		try {
			List<String> jobIds = new ArrayList<String>();
			if (jobs != null) {
				for (JobModel model : jobs) {
					if (model != null) {
						jobIds.add(insertRegistrationJob(model));
					}
				}
			}
			log.debug("insertRegistrationJobs(JobModel[]) end");
			return jobIds.toArray(new String[0]);
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
	}

	private void updateRegistrationJob(JobModel job) {
		log.debug("updateRegistrationJob(JobModel) start");
		String updateSql = null;
		SqlUpdate updateJob = new SqlUpdate();
		updateJob.setDataSource(this.getDataSource());
		log.debug("Insert Query: " + updateSql);
		try {
			updateSql = UpdateQueryGenerator.getUpdateJobQuery(job);
			updateJob.setSql(updateSql);
			updateJob.update();
			log.debug("Job updated with jobId = " + job.getJobID());
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
		log.debug("updateRegistrationJob(JobModel) end");
	}

	public void updateRegistrationJobs(JobModel[] jobs) {
		log.debug("updateRegistrationJobs(JobModel[]) start");
		try {
			for (int i = 0; i < jobs.length; ++i) {
				updateRegistrationJob(jobs[i]);
			}
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
		log.debug("updateRegistrationJobs(JobModel[]) end");
	}

	public void updateBatchJobs(BatchRegInfoModel[] jobs) {
		log.debug("updateBatchJobs(BatchRegInfoModel[]) start");
		NotebookUpdateDAO updateDAO = null;
		try {
			updateDAO = getDaoFactory().getNotebookUpdateDAO();
			for (int i = 0; i < jobs.length; ++i) {
				BatchRegInfoModel batchRegInfo = (BatchRegInfoModel) jobs[i];
				updateDAO.updateRegisteredBatchRegistrationInfo(batchRegInfo);
			}
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
		log.debug("updateBatchJobs(BatchRegInfoModel[]) end");
	}

	public JobModel getRegistrationJob(String jobId) {
		try {
			JobSelect select = new JobSelect(getDataSource(), SelectQueryGenerator.getRegistrationJobQuery(jobId));
			List<?> list = select.execute();
			
			if (list.size() > 0) {
				return (JobModel) list.get(0);
			}
			
			return null;
		} catch (Exception e) {
			throw new JDBCRuntimeException(e);
		}
	}
	
	public List<JobModel> getAllRegistrationJobs(String userId, String status) throws DAOException {
		try {
			JobSelect select = new JobSelect(getDataSource(), SelectQueryGenerator.getAllRegistrationJobsQuery(userId, status));
			return select.execute();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	public ArrayList getAllRegistrationJobs() throws DAOException {
		return getAllRegistrationJobs(false);
	}
	
	public ArrayList getAllRegistrationJobs(boolean forUpdate) throws DAOException {
		log.debug("getAllRegistrationJobs() start");
		String selectSql = SelectQueryGenerator.getPendingRegistrationJobsQuery(forUpdate);
		ArrayList resultList = null;
		try {
			JobSelect select = new JobSelect(this.getDataSource(), selectSql);
			resultList = new ArrayList(select.execute());
		} catch (Exception e) {
			log.error("Error getting all registration jobs:", e);
			throw new DAOException(e);
		}
		log.debug("getAllRegistrationJobs() end");
		return resultList;
	}

	public void updateRegistrationJobsStatus(String jobId, String status) throws DAOException {
		log.debug("updateRegistrationJobsStatus start");
		String updateSql = null;
		SqlUpdate updateJob = new SqlUpdate();
		updateJob.setDataSource(this.getDataSource());
		log.debug("Insert Query: " + updateSql);
		try {
			updateSql = UpdateQueryGenerator.getUpdateJobStatusQuery(jobId, status);
			updateJob.setSql(updateSql);
			updateJob.update();
			log.debug("Job updated with jobId = " + jobId);
		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		}
		log.debug("updateRegistrationJob(JobModel) end");
	}
	
	public ArrayList getAllBatchJobs(String jobId) throws DAOException {
		log.debug("getAllBatchJobs(String) start");
		String selectSql = SelectQueryGenerator.getAllBatchJobsQuery(jobId);
		ArrayList resultList = null;
		try {
			log.debug(" Getting All Batch Jobs for jobId :" + jobId);
			BatchJobSelect select = new BatchJobSelect(this.getDataSource(), selectSql);
			resultList = new ArrayList(select.execute());
			log.debug("retrieved RegBatchObject list :" + resultList.size());
		} catch (Exception e) {
			log.error("Error getting all batch jobs:", e);
			throw new DAOException(e);
		}
		log.debug("getAllBatchJobs(String) end");
		return resultList;
	}

	public List getAllPendingCompoundRegistrationJobs() throws DAOException {
		log.debug("getAllPendingCompoundRegistrationJobs() start");
		String selectSql = SelectQueryGenerator.getAllPendingCompoundRegistrationJobsQuery();
		ArrayList resultList = null;
		try {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
			log.debug("Query: " + selectSql);
			List list = jdbcTemplate.queryForList(selectSql, String.class);
			resultList = new ArrayList(list);
			log.debug("List of JobIds:" + resultList);
		} catch (Exception e) {
			log.error("Error getting all CompoundRegistration jobs:", e);
			throw new DAOException(e);
		}
		log.debug("getAllPendingCompoundRegistrationJobs() end");
		return resultList;
	}

	public List getAllCompoundRegistrationJobOffsets(String jobId) throws DAOException {
		log.debug("getAllCompoundRegistrationJobOffsets(String) start");
		String selectSql = SelectQueryGenerator.getAllCompoundRegistrationJobOffsetQuery(jobId);
		ArrayList resultList = null;
		try {
			RowMapper rm = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					int n = rs.getInt("COMPOUND_REGISTRATION_OFFSET");
					log.debug("COMPOUND_REGISTRATION_OFFSET =" + n);
					Integer num = new Integer(n);
					log.debug("OFFSET as Integer -:" + num);
					return num;
				}
			};
			JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
			List list = jdbcTemplate.query(selectSql, rm);
			log.debug("List of offsets:" + list);
			resultList = new ArrayList(list);
			log.debug("List of offsets:" + resultList);
		} catch (Exception e) {
			log.error("Error getting all CompoundRegistration job joffsets:", e);
			throw new DAOException(e);
		}
		log.debug("getAllCompoundRegistrationJobOffsets(String) end");
		return resultList;
	}

	public JobModel getCeNPlateJob(String plateKey) throws DAOException {
		log.debug("getCeNPlateJob(String) start");
		String selectSql = SelectQueryGenerator.getPlateJobQuery(plateKey);
		ArrayList resultList = null;
		try {
			log.debug(" Getting Plate Job for plate key :" + plateKey);
			PlateJobSelect select = new PlateJobSelect(this.getDataSource(), selectSql);
			resultList = new ArrayList(select.execute());
			log.debug("retrieved JobModel list :" + resultList.size());
		} catch (Exception e) {
			log.error("Error getting plate job:", e);
			throw new DAOException(e);
		}
		log.debug("getCeNPlateJob(String) end");
		if (resultList != null && resultList.size() > 0) {
			return (JobModel) resultList.get(0);
		} else {
			return null;
		}
	}
}
