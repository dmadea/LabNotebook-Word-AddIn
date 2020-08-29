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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InsertQueryGenerator {

	private static final Log log = LogFactory.getLog(InsertQueryGenerator.class);

	private InsertQueryGenerator() {
	}
	
	public static String getInsertMonomerPlateQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PLATE(");
		sqlQuery.append("PLATE_KEY, CEN_PLATE_TYPE, PAGE_KEY,  " + "CONTAINER_KEY, PLATE_NUMBER,PLATE_BAR_CODE,"
				+ "STEP_KEY,PARENT_PLATE_KEY");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

	public static String getInsertProductPlateQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PLATE(");

		sqlQuery.append("PLATE_KEY, CEN_PLATE_TYPE, PAGE_KEY, " + "CONTAINER_KEY, PLATE_NUMBER," + "PLATE_BAR_CODE,STEP_KEY");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

	public static String getInsertRegisteredPlateQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PLATE(");
		sqlQuery.append("PLATE_KEY, CEN_PLATE_TYPE, PAGE_KEY, " + "CONTAINER_KEY, PLATE_NUMBER,PLATE_BAR_CODE, REGISTERED_DATE");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

	public static String getInsertPlateWellQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PLATE_WELL(");
		sqlQuery.append("WELL_KEY, PLATE_KEY, WELL_POSITION, WELL_TYPE, BATCH_KEY,  " + " SOLVENT_CODE,BARCODE");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}



	public static String getInsertStepBatchListQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_STEP_BATCH_LISTS(");
		sqlQuery.append("STEP_KEY,LIST_KEY, POSITION,  PAGE_KEY");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

	public static String getInsertListsQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_LISTS(");
		sqlQuery.append(" LIST_KEY, LIST_NAME");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

	public static String getInsertContainerQuery() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_CONTAINER(");
		sqlQuery.append("CONTAINER_KEY, CONTAINER_CODE, CREATOR_ID, CONTAINER_NAME,");
		sqlQuery.append("IS_USER_DEFINED, X_POSITIONS, Y_POSITIONS, MAJOR_AXIS,");
		sqlQuery.append("CONTAINER_TYPE,SKIP_WELL_POSITIONS");
		sqlQuery.append(" ");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}

	public static String getInsertNewNotebookQuery() {

		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_NOTEBOOKS(SITE_CODE, USERNAME,");
		sqlQuery.append(" NOTEBOOK, STATUS, XML_METADATA)");
		sqlQuery.append(" VALUES (?, ?, ?, ?, ?)");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}


	public static String getInsertPlateWellPrepStmtSQL() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PLATE_WELL(");
		sqlQuery.append("WELL_KEY, PLATE_KEY, WELL_POSITION, WELL_TYPE, BATCH_KEY,SOLVENT_CODE,BARCODE,WELL_NUMBER,");
		sqlQuery.append("WEIGHT_VALUE,WEIGHT_UNIT_CODE,WEIGHT_IS_CALC,WEIGHT_SIG_DIGITS,WEIGHT_SIG_DIGITS_SET,WEIGHT_USER_PREF_FIGS,");
		sqlQuery.append("VOLUME_VALUE,VOLUME_UNIT_CODE,VOLUME_IS_CALC,VOLUME_SIG_DIGITS,VOLUME_SIG_DIGITS_SET,VOLUME_USER_PREF_FIGS,");
		sqlQuery.append("MOLARITY_VALUE,MOLARITY_UNIT_CODE,MOLARITY_IS_CALC,MOLARITY_SIG_DIGITS,MOLARITY_SIG_DIGITS_SET,MOLARITY_USER_PREF_FIGS");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,?,");
		sqlQuery.append("?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}
	
	
	public static String getInsertPurificationServiceParamPrepStmtSQL() {
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append("INSERT INTO CEN_PURIFICATION_SERVICE(");
		sqlQuery.append("PURIFICATION_SERVICE_KEY, MODIFIERS, PH_VALUE, ARCHIVE_PLATE, ARCHIVE_VOLUME,  "
				+ " SAMPLE_WORKUP,INORGANIC_BYPRODUCT_SALT,SEPERATE_ISOMERS,DESTINATION_LAB,WELL_KEY");
		sqlQuery.append(") values (");
		sqlQuery.append("?,?,?,?,?,?,?,?,?,?");
		sqlQuery.append(")");
		log.debug(sqlQuery.toString());
		return sqlQuery.toString();
	}
}
