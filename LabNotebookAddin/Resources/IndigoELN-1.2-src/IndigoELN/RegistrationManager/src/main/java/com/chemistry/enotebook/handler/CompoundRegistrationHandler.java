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
package com.chemistry.enotebook.handler;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.SDFileGeneratorUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class CompoundRegistrationHandler 
	extends AbstractHandler 
{
	private static final Log log = LogFactory.getLog(CompoundRegistrationHandler.class);

	/**
	 * 
	 */
	public CompoundRegistrationHandler() { }

	/**
	 * Submits product plates for registration.
	 * 
	 * @param pPlates
	 * @param userID
	 * @param compoundManagementEmployeeID
	 * @param pageHeader
	 * @return String jobid returned from CompoundRegistration
	 * @throws Exception
	 */
	public HashMap<String, String> submitPlatesForRegistration(ProductPlate[] pPlates, SessionIdentifier session, 
												String compoundManagementEmployeeID, NotebookPageModel pageModel) 
		throws Exception 
	{
		HashMap<String, String> map = new HashMap<String, String>(pPlates.length);

		try {
			for (ProductPlate plate : pPlates) {
				RegistrationServiceDelegate delegate = new RegistrationServiceDelegate();
				long jobId = delegate.submitBatchesForRegistration(Arrays.asList(plate.getAllBatchesInThePlate()));
				
				if (jobId != -1) {
					perpareAndSubmitPlatetJob(plate, pageModel.getPageHeader().getKey(), Long.toString(jobId), session);
					map.put(plate.getKey(), Long.toString(jobId));
				}
			}
		} catch (RuntimeException e) {
			log.error(e);
		}
		return map;
	}

	/**
	 * Returns SDFile as String for ProductPlate
	 * 
	 * @param pPlate
	 *            ProductPlate for with SDfile to be generated
	 * @param userID
	 * @param compoundManagementEmployeeID
	 * @param pageHeader
	 * @return String SDFileUnit
	 * @throws Exception
	 */
	public SDFileInfo getSDFileForPlate(ProductPlate pPlate, String userID, String compoundManagementEmployeeID, 
										NotebookPageModel pageModel)
		throws Exception 
	{
		SDFileInfo sdUnit = null;

		try {
			log.debug("Preparing the SDF Unit");
			SDFileGeneratorUtil sdfUtil = new SDFileGeneratorUtil();
			log.debug("Created SDF Unit");
			sdUnit = sdfUtil.buildSDUnitsForBatchesInPlate(pPlate, userID, compoundManagementEmployeeID, pageModel, true);
			log.debug("Completed buildSDUnitsForBatchesInPlate");
		} catch (RuntimeException e) {
			log.info("Handled Error. Preparing the SDF for batches in a plate.");
			log.error(e);
		}
		return sdUnit;
	}

	public HashMap<String, String> submitBatchesForRegistration(ProductBatchModel[] pBatches, String userID, String compoundManagementEmployeeID, NotebookPageModel pageModel, SessionIdentifier session) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>(pBatches.length);
		RegistrationServiceDelegate delegate = new RegistrationServiceDelegate();
		
		long jobId = delegate.submitBatchesForRegistration(Arrays.asList(pBatches));
		
		if (jobId != -1) {
			prepareAndSubmitBatchJob(pBatches, pageModel.getPageHeader().getKey(), Long.toString(jobId), session);
			for (ProductBatchModel batch : pBatches)
				map.put(batch.getKey(), Long.toString(jobId));
		}
		
		return map;
	}

	/**
	 * Generates SDFile for a given ProductBatch.
	 * 
	 * @param pBatch
	 *            ProductBatch for with SDfile to be generated
	 * @param userID
	 * @param compoundManagementEmployeeID
	 * @param pageHeader
	 * @return String SDFFile Unit.
	 * @throws Exception
	 */
	public String getSDFileForBatch(ProductBatchModel pBatch, String userID, String compoundManagementEmployeeID,
									NotebookPageModel pageModel) 
		throws Exception 
	{
		SDFileGeneratorUtil sdfUtil = new SDFileGeneratorUtil();
		if (pBatch.getRegInfo().getRegistrationStatus().equals(CeNConstants.COMPOUND_REGISTRATION_REGISTERED)) {
			return null;
		}
		String sdUnit = sdfUtil.buildSDUnitForABatch(pBatch, compoundManagementEmployeeID, pageModel, true, true);
		return sdUnit;
	}

	/**
	 * @param plate
	 * @param pageKey
	 * @param jobId
	 */
	private void prepareAndSubmitBatchJob(ProductBatchModel[] batchModels, String pageKey, String jobId, 
											SessionIdentifier session) 
	{
		JobModel[] jobs = new JobModel[1];
		jobs[0] = new JobModel(jobId, "", pageKey, session == null ? "" : session.getUserProfile().getUrl());
		jobs[0].setCompoundRegistrationStatus(CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
		BatchRegInfoModel[] batchJobs = new BatchRegInfoModel[batchModels.length];
		for (int i = 0; i < batchModels.length; i++) {
			batchJobs[i] = batchModels[i].getRegInfo();
			batchJobs[i].setRegistrationStatus(CeNConstants.COMPOUND_REGISTRATION_NOT_REGISTERED);
			batchJobs[i].setSubmissionStatus(CeNConstants.COMPOUND_REGISTRATION_SUBMITTED);
			batchJobs[i].setStatus(CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			batchJobs[i].setJobId(jobId);
		}

		try {
			getStorageEJBLocal().insertRegistrationJobs(jobs);
			getStorageEJBLocal().updateBatchJobs(batchJobs, null);
		} catch (Exception error) {
			error.printStackTrace();
			log.error("Handled Error:Unable to insert JOBS using Storage Service");
			log.error(error);
		}
	}

	/**
	 * @param plate
	 * @param pageKey
	 * @param jobId
	 */
	private void perpareAndSubmitPlatetJob(ProductPlate plate, String pageKey, String jobId, SessionIdentifier session) 
	{
		ProductBatchModel[] batchModels = plate.getAllBatchesInThePlate();
		JobModel[] plateJobs = new JobModel[1];

		plateJobs[0] = new JobModel(jobId, plate.getKey(), pageKey, session.getUserProfile().getUrl());
		plateJobs[0].setCompoundRegistrationStatus(CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
		BatchRegInfoModel[] batchJobs = new BatchRegInfoModel[batchModels.length];
		for (int i = 0; i < batchModels.length; i++) {
			batchJobs[i] = batchModels[i].getRegInfo();
			batchJobs[i].setRegistrationStatus(CeNConstants.COMPOUND_REGISTRATION_NOT_REGISTERED);
			batchJobs[i].setSubmissionStatus(CeNConstants.COMPOUND_REGISTRATION_SUBMITTED);
			batchJobs[i].setStatus(CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			batchJobs[i].setJobId(jobId);
		}

		try {
			getStorageEJBLocal().insertRegistrationJobs(plateJobs);
			getStorageEJBLocal().updateBatchJobs(batchJobs, session);
		} catch (Exception error) {
			error.printStackTrace();
			log.error("Handled Error.Unable to insert JOBS using Storage Service.");
			log.error(error);
		}
	}

//	/**
//	 * @return RegistrationServiceLocal
//	 */
//	private RegistrationServiceLocal getRegistrationServiceEJBLocal() {
//		try {
//			log.debug("Creating RegistrationServiceLocalHome ..");
//			RegistrationServiceLocalHome registrationLocalHome = (RegistrationServiceLocalHome) ServiceLocator.locateEJBLocalHome(
//					RegistrationServiceLocalHome.JNDI_NAME, RegistrationServiceLocalHome.class);
//			log.debug("located RegistrationServiceLocalHome");
//			return registrationLocalHome.create();
//		} catch (NamingException n) {
//			log.error("Error getting RegistrationServiceLocalHome: " + n.getMessage());
//			log.error(CommonUtils.getStackTrace(n));
//			n.printStackTrace();
//			return null;
//		} catch (Exception f) {
//			log.error("Error creating RegistrationServiceLocalHome reference: " + f.getMessage());
//			log.error(CommonUtils.getStackTrace(f));
//			f.printStackTrace();
//			return null;
//		}
//	}

	/**
	 * Updates CompoundRegistration details for given batches
	 * @param batches batches to update
	 * @return List of updated batches
	 */
	private List<ProductBatchModel> updateBatchesCompoundRegistrationDetails(ProductBatchModel[] batches) {
		log.info("updateBatchesCompoundRegistrationDetails(ProductBatchModel[]) start");
		
		String sqlQueryJob = "SELECT a.status JOB_STATUS, b.status COMPOUND_STATUS, b.batch_number, b.job_id FROM COMPOUND_REGISTRATION_JOB_CTRL a, COMPOUND_REGISTRATION_COMPOUND_JOB_CTRL b WHERE a.JOB_ID = b.JOB_ID and b.batch_number in (";
		String sqlQueryInfo = "select b.GLOBAL_NUMBER, a.GLOBAL_COMPOUND_NUMBER, a.BATCH_NUMBER, a.BATCH_TRACKING_NO, a.LOAD_DATE, a.NOTEBOOK_REF from COMPOUND_REGISTRATION_COMPOUND_BATCH a, COMPOUND_REGISTRATION_COMPOUND_PARENT b, COMPOUND_REGISTRATION_AMOUNT c where a.BATCH_TRACKING_NO = c.BATCH_TRACKING_NO and a.GLOBAL_SEQ_NUMBER = b.GLOBAL_SEQ_NUMBER and a.NOTEBOOK_REF in (";
		
		List<ProductBatchModel> result = new Vector<ProductBatchModel>();
		
		if (batches != null) {
			List<String> batchNumbers = new Vector<String>();
			String[] jobIDsForBatches = new String[batches.length];
			for (ProductBatchModel batch : batches) {
				if (batch != null) {
					String batchNumber = batch.getBatchNumberAsString();
					sqlQueryJob += ("'" + batchNumber + "',");
					sqlQueryInfo += ("'" + batchNumber + "',");
					batchNumbers.add(batchNumber);
				}
			}
			sqlQueryJob = sqlQueryJob.substring(0, sqlQueryJob.lastIndexOf(","));
			sqlQueryInfo = sqlQueryInfo.substring(0, sqlQueryInfo.lastIndexOf(","));
			sqlQueryJob += ")";
			sqlQueryInfo += ")";
			try {
				Connection con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI("COMPOUND_REGISTRATION_DS_JNDI"))).getConnection();
				PreparedStatement st = con.prepareStatement(sqlQueryJob);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					String batchNumber = rs.getString("BATCH_NUMBER");
					String jobId = rs.getString("JOB_ID");
					String jobStatus = rs.getString("JOB_STATUS");
					String compoundStatus = rs.getString("COMPOUND_STATUS");
					int batchIndex = batchNumbers.indexOf(batchNumber);
					if (!StringUtils.isEmpty(jobIDsForBatches[batchIndex])) {
						int oldJobID = Integer.parseInt(jobIDsForBatches[batchIndex]);
						int newJobID = Integer.parseInt(jobId);
						if (newJobID < oldJobID) {
							continue;
						}
					}
					batches[batchIndex].getRegInfo().setCompoundRegistrationStatus(jobStatus + " - " + compoundStatus);
					batches[batchIndex].getRegInfo().setErrorMsg("");
					jobIDsForBatches[batchIndex] = jobId;
					if (!result.contains(batches[batchIndex])) {
						result.add(batches[batchIndex]);
					}
				}
				
				st = con.prepareStatement(sqlQueryInfo);
				rs = st.executeQuery();
				while (rs.next()) {
					String batchNumber = rs.getString("NOTEBOOK_REF");
					int batchIndex = batchNumbers.indexOf(batchNumber);
					batches[batchIndex].setParentBatchNumber(rs.getString("GLOBAL_NUMBER"));
					batches[batchIndex].getCompound().setRegNumber(rs.getString("GLOBAL_COMPOUND_NUMBER"));
					batches[batchIndex].setConversationalBatchNumber(rs.getString("GLOBAL_COMPOUND_NUMBER"));
					batches[batchIndex].getRegInfo().setConversationalBatchNumber(rs.getString("GLOBAL_COMPOUND_NUMBER"));
					batches[batchIndex].getRegInfo().setBatchTrackingId(Long.valueOf(rs.getString("BATCH_TRACKING_NO")).longValue());
					
					String loadDate = rs.getString("LOAD_DATE");
					batches[batchIndex].getRegInfo().setRegistrationDate(CommonUtils.parseRegDateToTimestamp(loadDate));
					
					if (!result.contains(batches[batchIndex])) {
						result.add(batches[batchIndex]);
					}
				}
			} catch (Exception e) {
				log.error("Error updating batches: ", e);
			}
		}
		
		log.info("updateBatchesCompoundRegistrationDetails(ProductBatchModel[]) end");
		return result;
	}
		
	/**
	 * Updates CompoundRegistration details for given batches
	 * @param batches batches to update
	 * @return List of updated batches
	 */
	public ProductBatchModel[] getRegistrationInformation(ProductBatchModel[] batches, SessionIdentifier sessionID) 
	{
		log.info("getRegistrationInformation(ProductBatchModel[], SessionIdentifier) start");
		List<ProductBatchModel> result = updateBatchesCompoundRegistrationDetails(batches);				
		log.info("getRegistrationInformation(ProductBatchModel[], SessionIdentifier) end");
		return result.toArray(new ProductBatchModel[0]);
	}
}
