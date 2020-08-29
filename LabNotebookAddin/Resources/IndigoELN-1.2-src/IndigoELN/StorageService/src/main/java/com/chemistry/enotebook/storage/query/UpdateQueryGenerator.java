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
package com.chemistry.enotebook.storage.query;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class UpdateQueryGenerator {
	private static final Log log = LogFactory.getLog(UpdateQueryGenerator.class);

	private UpdateQueryGenerator() {
	}
		
	public static String getNotebookPageModifiedDateUpdateQuery(NotebookPageHeaderModel pageHdr) {

		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_PAGES SET ");
		updateSql.append(" MODIFIED_DATE=?");
		updateSql.append(" WHERE PAGE_KEY='" + pageHdr.getKey() + "'");
		log.debug(updateSql.toString());
		return updateSql.toString();
	}


	/*
	 * This method will be used to update the Notebook Page status possible status states are OPEN,COMPLETE,DELETED
	 */
	public static String getNotebookPageStatusUpdateQuery(String pageKey, String status) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_PAGES SET ");
		updateSql.append("PAGE_STATUS='" + status + "',");
		updateSql.append(" MODIFIED_DATE=?");

		updateSql.append(" WHERE PAGE_KEY='" + pageKey + "'");
		log.debug(updateSql.toString());
		// System.out.println(updateSql);
		return updateSql.toString();
	}

	public static String getPlateUpdateQuery(Object plate) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_PLATE SET ");
		AbstractPlate aPlate = (AbstractPlate) plate;
		ArrayList updateParams = new ArrayList();

		if (/*CommonUtils.isNotNull(aPlate.getPlateComments()) */
		    (aPlate.getPlateComments() != null)) { // this is needed instead of isNotNull() otherwise comments cannot be "blanked" out (part of artf59009)
			updateParams.add(" COMMENTS='" + aPlate.getPlateComments().trim() + "'");
		}
		if (CommonUtils.isNotNull(aPlate.getPlateBarCode()))
			updateParams.add(" PLATE_BAR_CODE='" + aPlate.getPlateBarCode() + "'");

		if (CommonUtils.isNotNull(aPlate.getPlateType()))
			updateParams.add(" CEN_PLATE_TYPE='" + aPlate.getPlateType() + "'");

		if (CommonUtils.isNotNull(aPlate.getPlateNumber()))
			updateParams.add(" PLATE_NUMBER='" + aPlate.getPlateNumber() + "'");

		if (plate instanceof MonomerPlate) {
			MonomerPlate mPlate = (MonomerPlate) plate;
			if (CommonUtils.isNotNull(mPlate.getParentPlateKey()))
				updateParams.add(" PARENT_PLATE_KEY='" + mPlate.getParentPlateKey() + "'");
		} else if (plate instanceof ProductPlate) {
			ProductPlate pPlate = (ProductPlate) plate;
			if (CommonUtils.isNotNull(pPlate.getRegisteredDate()))
				updateParams.add(" REGISTERED_DATE='" + pPlate.getRegisteredDate() + "'");

		}
		updateSql.append(getCommaDelimitedString(updateParams));
		updateSql.append(" WHERE PLATE_KEY='" + aPlate.getKey() + "'");
		log.debug(updateSql.toString());
		return updateSql.toString();
	}

	public static String getPlateWellUpdateQuery(PlateWell well) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_PLATE_WELL SET ");
		ArrayList updateParams = new ArrayList();

		String batchKey = "";
		if (CommonUtils.isNotNull(well.getBatch()))
			batchKey = well.getBatch().getKey();
		updateParams.add(" BATCH_KEY='" + batchKey + "'");

		if (CommonUtils.isNotNull(well.getSolventCode()))
			updateParams.add(" SOLVENT_CODE='" + well.getSolventCode() + "'");

		if (CommonUtils.isNotNull(well.getBarCode()))
			updateParams.add(" BARCODE='" + well.getBarCode() + "'");
		updateSql.append(getCommaDelimitedString(updateParams));
		updateSql.append(" WHERE WELL_KEY='" + well.getKey() + "'");
		log.debug(updateSql.toString());
		// System.out.println(updateSql);
		return updateSql.toString();
	}

	public static String getAttachmentUpdateQuery(AttachmentModel attachmentModel) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_ATTACHEMENTS SET ");// cen_attachements
		ArrayList updateParams = new ArrayList();
		updateParams.add(" XML_METADATA=? ");
		//DTODO workaround  do not update content in table if it is null in AttachmentModel. It could be null in the AttachmentModel as a result of "lazy loading"		
		if (attachmentModel.getContents() != null) {
			updateParams.add(" BLOB_DATA=? ");
		}
		updateParams.add(" DATE_MODIFIED=? ");
		updateParams.add(" DOCUMENT_DESCRIPTION=? ");
		updateParams.add(" DOCUMENT_NAME=? ");
		updateParams.add(" IP_RELATED=? ");
		updateParams.add(" ORIGINAL_FILE_NAME=? ");
		updateParams.add(" DOCUMENT_SIZE=? ");
		updateParams.add(" DOCUMENT_TYPE=? ");
		updateSql.append(getCommaDelimitedString(updateParams));
		updateSql.append(" WHERE ATTACHEMENT_KEY='" + attachmentModel.getKey() + "'");
		log.debug(updateSql.toString());
		return updateSql.toString();
	}

	public static String getUpdateJobQuery(JobModel job) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_REG_JOBS SET ");// cen_attachements
		
		List<String> updateParams = new ArrayList<String>();
		updateParams.add(" COMPOUND_REGISTRATION_STATUS='" + job.getCompoundRegistrationStatus() + "'");
		updateParams.add(" COMPOUND_REG_STATUS_MESSAGE='" + job.getCompoundRegistrationStatusMessage() + "'");
		updateParams.add(" COMPOUND_MANAGEMENT_STATUS='" + job.getCompoundManagementStatus() + "'");
		updateParams.add(" COMPOUND_MGMT_STATUS_MESSAGE='" + job.getCompoundManagementStatusMessage() + "'");
		updateParams.add(" PURIFICATION_SERVICE_STATUS='" + job.getPurificationServiceStatus() + "'");
		updateParams.add(" PUR_SERVICE_STATUS_MSG='" + job.getPurificationServiceStatusMessage() + "'");
		updateParams.add(" COMPOUND_AGGREGATION_STATUS='" + job.getCompoundAggregationStatus() + "'");
		updateParams.add(" CMPD_AGGREGATION_STATUS_MSG='" + job.getCompoundAggregationStatusMessage() + "'");
		updateParams.add(" CALLBACK_URL='" + job.getCallbackUrl() + "'");
		updateParams.add(" COMPOUND_REGISTRATION_JOB_ID='" + job.getCompoundRegistrationJobId() + "'");
		updateParams.add(" WORKFLOW='" + job.getWorkflowsString() + "'");
		updateParams.add(" STATUS='" + job.getJobStatus() + "'");		
		
		updateSql.append(getCommaDelimitedString(updateParams));
		
		if (job.getJobID() == null)
			updateSql.append(" WHERE PLATE_KEY='" + job.getPlateKey() + "'");
		else
			updateSql.append(" WHERE JOB_ID='" + job.getJobID() + "'");
		
		log.debug(updateSql.toString());
		return updateSql.toString();

	}

	public static String getUpdateJobStatusQuery(String jobId, String status) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_REG_JOBS SET ");// cen_attachements
		
		List<String> updateParams = new ArrayList<String>();
		updateParams.add(" STATUS='" + status + "'");
		
		updateSql.append(getCommaDelimitedString(updateParams));
		updateSql.append(" WHERE PLATE_KEY='" + jobId + "'");
		
		log.debug(updateSql.toString());
		return updateSql.toString();

	}
	
	private static String getCommaDelimitedString(List<String> list) {
		StringBuffer buffer = new StringBuffer();
		if (list.size() == 0)
			return "";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				buffer.append(" , ");
			buffer.append((String) list.get(i));
		}
		return buffer.toString();
	}
	
	public static String getPurificationServiceParamUpdateQuery(PurificationServiceSubmisionParameters purificationServiceParam) {
		StringBuffer updateSql = new StringBuffer();
		
		updateSql.append("UPDATE CEN_PURIFICATION_SERVICE SET ");
		
		List<String> updateParams = new ArrayList<String>();
		
    	updateParams.add(" MODIFIERS='" + CommonUtils.getAsPipeSeperateValues(purificationServiceParam.getModifiers()) + "'");
		updateParams.add(" ARCHIVE_PLATE='" + purificationServiceParam.getArchivePlate() + "'");
		updateParams.add(" SAMPLE_WORKUP='" + purificationServiceParam.getSampleWorkUp() + "'");
    	updateParams.add(" DESTINATION_LAB='" + purificationServiceParam.getDestinationLab() + "'");
		updateParams.add(" INORGANIC_BYPRODUCT_SALT ='" + (purificationServiceParam.isInorganicByProductSaltPresent()== true?"Y":"N") + "'");
		updateParams.add(" SEPERATE_ISOMERS='" + (purificationServiceParam.isSeperateTheIsomers()== true?"Y":"N") + "'");
		updateSql.append(getCommaDelimitedString(updateParams));
		updateSql.append(" WHERE PURIFICATION_SERVICE_KEY='" + purificationServiceParam.getKey() + "'");
		
		log.debug(updateSql.toString());
		
		return updateSql.toString();
	}
	
	public static String getPlateWellUpdatePrepStmtSQL() {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_PLATE_WELL SET ");
		updateSql.append("BATCH_KEY=?,SOLVENT_CODE=?,BARCODE=?,");
		updateSql.append("WEIGHT_VALUE=?,WEIGHT_UNIT_CODE=?,WEIGHT_IS_CALC=?,WEIGHT_SIG_DIGITS=?,WEIGHT_SIG_DIGITS_SET=?,WEIGHT_USER_PREF_FIGS=?,");
		updateSql.append("VOLUME_VALUE=?,VOLUME_UNIT_CODE=?,VOLUME_IS_CALC=?,VOLUME_SIG_DIGITS=?,VOLUME_SIG_DIGITS_SET=?,VOLUME_USER_PREF_FIGS=?,");
		updateSql.append("MOLARITY_VALUE=?,MOLARITY_UNIT_CODE=?,MOLARITY_IS_CALC=?,MOLARITY_SIG_DIGITS=?,MOLARITY_SIG_DIGITS_SET=?,MOLARITY_USER_PREF_FIGS=?");
		updateSql.append(" WHERE WELL_KEY=?");
		log.debug(updateSql.toString());
		return updateSql.toString();
	}

	public static String getPurificationServiceParamUpdatePrepStmtSQL() {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_PURIFICATION_SERVICE SET ");
		updateSql.append(" MODIFIERS=?,");
		updateSql.append(" PH_VALUE=?,");
		updateSql.append(" ARCHIVE_PLATE=?,");
		updateSql.append(" ARCHIVE_VOLUME=?,");
		updateSql.append(" SAMPLE_WORKUP=?,");
		updateSql.append(" DESTINATION_LAB=?,");
		updateSql.append(" INORGANIC_BYPRODUCT_SALT =?,");
		updateSql.append(" SEPERATE_ISOMERS=?");
		updateSql.append(" WHERE PURIFICATION_SERVICE_KEY=?");
		log.debug(updateSql.toString());
		return updateSql.toString();
	}
	
	public static String getUpdateListsQuery() {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_LISTS SET ");
		updateSql.append("LIST_NAME=? ");
		updateSql.append("WHERE LIST_KEY=?");
		log.debug(updateSql.toString());
		return updateSql.toString();
	}
}
