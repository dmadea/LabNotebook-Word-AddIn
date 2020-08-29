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


import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelectQueryGenerator {
	
	private static final Log log = LogFactory.getLog(SelectQueryGenerator.class);
	
	private static final String MONOMER_BATCHES_QUERY = 	" SELECT "+
	"pc.STRUCT_KEY, pc.PAGE_KEY, pc.STRUCT_SKETCH,pc.NATIVE_STRUCT_SKETCH, pc.STRUCT_IMAGE,"+
	"pc.CHEMICAL_NAME, pc.MOLECULAR_WEIGHT, pc.VIRTUAL_COMPOUND_ID,"+
	"pc.REGISTRATION_NUMBER,pc.CAS_NUMBER,pc.STRUCT_SKTH_FRMT ,pc.NATIVE_STRUCT_SKTH_FRMT,"+
	"pc.STRUCT_IMAGE_FRMT,pc.USER_HAZARD_COMMENTS ,pc.STRUCT_COMMENTS,pc.STEREOISOMER_CODE,"+
	"pc.COMPOUND_NAME,pc.BOILING_PT_VALUE ,pc.BOILING_PT_UNIT_CODE ,pc.MELTING_PT_VALUE,"+
	"pc.MELTING_PT_UNIT_CODE ,pc.CREATED_BY_NOTEBOOK ,pc.EXACT_MASS ,pc.COMPOUND_PARENT_ID, "+
	"batch.BATCH_KEY,  batch.PAGE_KEY, batch.BATCH_NUMBER, batch.STRUCT_KEY,"+
	"batch.STEP_KEY, batch.BATCH_TYPE,batch.MOLECULAR_FORMULA, batch.SALT_CODE,"+
	"batch.SALT_EQUIVS, batch.LIST_KEY,batch.BATCH_MW_VALUE ,batch.BATCH_MW_UNIT_CODE,"+
	"batch.BATCH_MW_IS_CALC ,batch.BATCH_MW_SIG_DIGITS,batch.BATCH_MW_SIG_DIGITS_SET,"+
	"batch.BATCH_MW_USER_PREF_FIGS,batch.IS_LIMITING ,batch.AUTO_CALC,"+
	"batch.SYNTHSZD_BY ,batch.ADDED_SOLV_BATCH_KEY,batch.NO_OF_TIMES_USED ,"+
	"batch.INTD_ADDITION_ORDER ,batch.CHLORACNEGEN_TYPE ,batch.IS_CHLORACNEGEN ,batch.TESTED_FOR_CHLORACNEGEN,"+
	"batch.xml_metadata as batch_xml_metadata," +
	"l.step_key as STEP_KEY_FROM_BATCHES_LIST,l.POSITION"+
	"  FROM   cen_batches batch, cen_structures pc, cen_step_batch_lists l"+
	" WHERE  batch.struct_key = pc.struct_key"+
	" AND batch.list_key = l.list_key AND l.POSITION NOT LIKE 'P%' ";
	
	private static final String PRODUCT_BATCHES_QUERY = " SELECT "+
	"pc.STRUCT_KEY, pc.PAGE_KEY, pc.STRUCT_SKETCH,pc.NATIVE_STRUCT_SKETCH, pc.STRUCT_IMAGE,"+
	"pc.CHEMICAL_NAME, pc.MOLECULAR_WEIGHT, pc.VIRTUAL_COMPOUND_ID,"+
	"pc.REGISTRATION_NUMBER,pc.CAS_NUMBER,pc.STRUCT_SKTH_FRMT ,pc.NATIVE_STRUCT_SKTH_FRMT,"+
	"pc.STRUCT_IMAGE_FRMT,pc.USER_HAZARD_COMMENTS ,pc.STRUCT_COMMENTS,pc.STEREOISOMER_CODE,"+
	"pc.COMPOUND_NAME,pc.BOILING_PT_VALUE ,pc.BOILING_PT_UNIT_CODE ,pc.MELTING_PT_VALUE,"+
	"pc.MELTING_PT_UNIT_CODE ,pc.CREATED_BY_NOTEBOOK ,pc.EXACT_MASS ,pc.COMPOUND_PARENT_ID, "+
	"batch.BATCH_KEY,  batch.PAGE_KEY, batch.BATCH_NUMBER, batch.STRUCT_KEY,"+
	"batch.STEP_KEY, batch.BATCH_TYPE,batch.MOLECULAR_FORMULA, batch.SALT_CODE,"+
	"batch.SALT_EQUIVS, batch.LIST_KEY,batch.BATCH_MW_VALUE ,batch.BATCH_MW_UNIT_CODE,"+
	"batch.BATCH_MW_IS_CALC ,batch.BATCH_MW_SIG_DIGITS,batch.BATCH_MW_SIG_DIGITS_SET,"+
	"batch.BATCH_MW_USER_PREF_FIGS,batch.IS_LIMITING ,batch.AUTO_CALC,"+
	"batch.SYNTHSZD_BY ,batch.ADDED_SOLV_BATCH_KEY,batch.NO_OF_TIMES_USED ,"+
	"batch.INTD_ADDITION_ORDER ,batch.CHLORACNEGEN_TYPE ,batch.IS_CHLORACNEGEN ,batch.TESTED_FOR_CHLORACNEGEN,"+
	"batch.xml_metadata as batch_xml_metadata," + 
	"l.step_key as STEP_KEY_FROM_BATCHES_LIST,l.POSITION,"+
	"r.reg_batch_key, r.page_key, r.batch_key,"+
	" r.conversational_batch_number r_conversational_batch_number,"+
	"r.parent_batch_number r_parent_batch_number, r.batch_tracking_id,"+
	"r.job_id, r.registration_status, r.submission_status, r.status,"+
	"r.compound_aggregation_status, r.cmpd_aggregation_status_msg, r.purification_service_status,"+
	"r.pur_service_status_msg, r.compound_registration_offset, r.compound_management_status,"+
	"r.compound_mgmt_status_message, r.compound_reg_status_message,"+
	"r.SOURCE_CODE,r.SOURCE_DETAIL_CODE,r.registration_date,r.MELT_POINT_VAL_UPPER,r.MELT_POINT_VAL_LOWER,r.MELT_POINT_COMMENTS,"+
	"r.SUPPLIER_CODE,r.SUPPLIER_REGISTRY_NUMBER,r.COMPOUND_STATE,r.SELECTIVITY_STATUS,r.CONTINUE_STATUS,r.PROTECTION_CODE,"+
	"r.hit_id,r.vnv_status,r.BATCH_STORAGE_COMMENT,r.BATCH_HAZARD_COMMENT,r.BATCH_HANDLING_COMMENT,r.BATCH_OWNER,r.PRODUCT_FLAG,r.BATCH_COMMENTS,r.INTERMEDIATE_OR_TEST,"+
	"r.xml_metadata as reg_xml_metadata"+
	"  FROM   cen_batches batch,cen_registered_batches r, cen_structures pc, cen_step_batch_lists l"+
	" WHERE  batch.batch_key = r.batch_key "+
	" AND batch.struct_key = pc.struct_key "+
	" AND batch.list_key = l.list_key ";
		
	private static final String STOIC_BATCH_SEARCH_QUERY = " SELECT "+
	"pc.STRUCT_KEY, pc.PAGE_KEY, pc.STRUCT_SKETCH,pc.NATIVE_STRUCT_SKETCH, pc.STRUCT_IMAGE,"+
	"pc.CHEMICAL_NAME, pc.MOLECULAR_WEIGHT, pc.VIRTUAL_COMPOUND_ID,"+
	"pc.REGISTRATION_NUMBER,pc.CAS_NUMBER,pc.STRUCT_SKTH_FRMT ,pc.NATIVE_STRUCT_SKTH_FRMT,"+
	"pc.STRUCT_IMAGE_FRMT,pc.USER_HAZARD_COMMENTS ,pc.STRUCT_COMMENTS,pc.STEREOISOMER_CODE,"+
	"pc.COMPOUND_NAME,pc.BOILING_PT_VALUE ,pc.BOILING_PT_UNIT_CODE ,pc.MELTING_PT_VALUE,"+
	"pc.MELTING_PT_UNIT_CODE ,pc.CREATED_BY_NOTEBOOK ,pc.EXACT_MASS ,pc.COMPOUND_PARENT_ID, "+
	"batch.BATCH_KEY,  batch.PAGE_KEY, batch.BATCH_NUMBER, batch.STRUCT_KEY,"+
	"batch.STEP_KEY, batch.BATCH_TYPE,batch.MOLECULAR_FORMULA, batch.SALT_CODE,"+
	"batch.SALT_EQUIVS, batch.LIST_KEY,batch.BATCH_MW_VALUE ,batch.BATCH_MW_UNIT_CODE,"+
	"batch.BATCH_MW_IS_CALC ,batch.BATCH_MW_SIG_DIGITS,batch.BATCH_MW_SIG_DIGITS_SET,"+
	"batch.BATCH_MW_USER_PREF_FIGS,batch.IS_LIMITING ,batch.AUTO_CALC,"+
	"batch.SYNTHSZD_BY ,batch.ADDED_SOLV_BATCH_KEY,"+
	"batch.INTD_ADDITION_ORDER ,batch.CHLORACNEGEN_TYPE ,batch.IS_CHLORACNEGEN ,batch.TESTED_FOR_CHLORACNEGEN,"+
	"batch.xml_metadata as batch_xml_metadata, "+
	"r.reg_batch_key, r.page_key, r.batch_key,"+
	" r.conversational_batch_number r_conversational_batch_number,"+
	"r.parent_batch_number r_parent_batch_number, r.batch_tracking_id,"+
	"r.SOURCE_CODE,r.SOURCE_DETAIL_CODE,r.registration_date,r.MELT_POINT_VAL_UPPER,r.MELT_POINT_VAL_LOWER,r.MELT_POINT_COMMENTS,"+
	"r.SUPPLIER_CODE,r.SUPPLIER_REGISTRY_NUMBER,r.COMPOUND_STATE,r.SELECTIVITY_STATUS,r.CONTINUE_STATUS,r.PROTECTION_CODE,"+
	"r.hit_id,r.vnv_status,r.BATCH_STORAGE_COMMENT,r.BATCH_HAZARD_COMMENT,r.BATCH_HANDLING_COMMENT,r.BATCH_OWNER,r.PRODUCT_FLAG,r.BATCH_COMMENTS,"+
	"r.xml_metadata as reg_xml_metadata"+
	"  FROM   cen_batches batch,cen_registered_batches r, cen_structures pc "+
	" WHERE  batch.batch_key = r.batch_key "+
	" AND batch.struct_key = pc.struct_key ";
		
	private SelectQueryGenerator() {
	}
		
	public static String getNotebookHeaderQuery(NotebookRef nbref) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(" SELECT ");
		queryBuffer.append("NOTEBOOK,EXPERIMENT,PAGE_KEY,PAGE_STATUS,PAGE_VERSION,SITE_CODE,"); 
		queryBuffer.append("USERNAME,PROJECT_CODE,TA_CODE,LOOK_N_FEEL,CREATION_DATE" );
		queryBuffer.append(",SUBJECT,SERIES_ID,PROTOCOL_ID,LITERATURE_REF,BATCH_OWNER," );
		queryBuffer.append("BATCH_CREATOR,DESIGN_SUBMITTER,LATEST_VERSION,XML_METADATA,");
		queryBuffer.append("PROCEDURE");
		queryBuffer.append(" FROM CEN_PAGES WHERE NOTEBOOK='" + nbref.getNbNumber()+"' AND" );
		queryBuffer.append(" EXPERIMENT='"+nbref.getNbPage() +"' AND ");
		queryBuffer.append(" (PAGE_STATUS NOT LIKE '"+CeNConstants.PAGE_STATUS_DELETED+"' or PAGE_STATUS is NULL)");
		if(nbref.getVersion() > 0)
		{
			queryBuffer.append(" AND PAGE_VERSION =" + nbref.getVersion());
		}
		queryBuffer.append(" order by page_version desc ");
		

		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	public static String getNbkQueryForUser(String user) {
		String selectQry = " SELECT * FROM CEN_PAGES WHERE USERNAME='" + user + "' ORDER BY EXPERIMENT";
		log.debug(selectQry);
		return selectQry;
	}

	public static String getAllReactionStepsQuery(String pageKey) {

		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(" SELECT ");
		queryBuffer.append("step.step_key, step.page_key, step.seq_num, step.rxn_scheme_key,");
		queryBuffer.append("step.xml_metadata,");
		queryBuffer.append("scheme.reaction_type, scheme.rxn_sketch, scheme.native_rxn_sketch,");
		queryBuffer.append(" scheme.sketch_image, scheme.vrxn_id, scheme.protocol_id,");
		queryBuffer.append("scheme.SYNTH_ROUTE_REF,scheme.reaction_id,scheme.NATIVE_RXN_SKTH_FRMT,scheme.RXN_SKTH_FRMT");
		queryBuffer.append(" FROM cen_reaction_steps step, cen_reaction_schemes scheme");
		queryBuffer.append(" WHERE step.rxn_scheme_key = scheme.rxn_scheme_key ");
		queryBuffer.append(" AND step.PAGE_KEY = '"+pageKey+"'");
		queryBuffer.append(" ORDER BY step.seq_num ASC");
	
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();

	}

	public static String getProcedureImagesQuery(String pageKey) {
		String selectQry = " SELECT * FROM CEN_PROCEDURE_IMAGES WHERE PAGE_KEY='" + pageKey + "'";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getAllRegisteredBatchesForPageQuery(String pageKey) {
		StringBuffer queryBuffer = new StringBuffer(); 
		queryBuffer.append(" SELECT r.PROTECTION_CODE,");
		queryBuffer.append("r.reg_batch_key, r.page_key, r.batch_key,");
		queryBuffer.append(" r.conversational_batch_number r_conversational_batch_number,");
		queryBuffer.append("r.parent_batch_number r_parent_batch_number, r.batch_tracking_id,");
		queryBuffer.append("r.job_id, r.registration_status, r.submission_status, r.status,");
		queryBuffer.append("r.compound_aggregation_status, r.cmpd_aggregation_status_msg, r.purification_service_status,");
		queryBuffer.append("r.pur_service_status_msg, r.compound_registration_offset, r.compound_management_status,");
		queryBuffer.append("r.compound_mgmt_status_message, r.compound_reg_status_message,");
		queryBuffer.append("r.melt_point_val_lower, r.melt_point_val_upper, r.melt_point_comments,");
		queryBuffer.append("r.SOURCE_CODE,r.SOURCE_DETAIL_CODE,r.registration_date,");
		queryBuffer.append("r.SUPPLIER_CODE,r.SUPPLIER_REGISTRY_NUMBER,r.COMPOUND_STATE,r.SELECTIVITY_STATUS,r.CONTINUE_STATUS,");
		queryBuffer.append("r.BATCH_STORAGE_COMMENT, r.BATCH_HAZARD_COMMENT, r.PRODUCT_FLAG,r.BATCH_HANDLING_COMMENT,r.BATCH_OWNER,r.BATCH_COMMENTS,r.INTERMEDIATE_OR_TEST,");
		queryBuffer.append("r.hit_id,r.vnv_status,");
		queryBuffer.append("r.xml_metadata as reg_xml_metadata");
		queryBuffer.append(" FROM   cen_registered_batches r");
		queryBuffer.append(" WHERE PAGE_KEY = '"+pageKey+"'");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}


	public static String getBatchesListQueryForStep(String stepKey){
		String selectQry = " SELECT * FROM CEN_STEP_BATCH_LISTS WHERE " + 
		" STEP_KEY='" + stepKey+"'";
		
		log.debug(selectQry);
		return selectQry;
	}
	

	public static String getSummaryProductBatchesQuery( String stepKey) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(PRODUCT_BATCHES_QUERY);
		queryBuffer.append(" AND batch.STEP_KEY='"+stepKey+"'AND l.STEP_KEY='"+stepKey+"' ");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	public static String getSummaryMonomerBatchesQuery(String stepKey) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(MONOMER_BATCHES_QUERY);
		queryBuffer.append(" AND batch.STEP_KEY='"+stepKey+"'AND l.STEP_KEY='"+stepKey+"' ");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	public static String getIntermediateProductBatchesQuery( String listKey) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(PRODUCT_BATCHES_QUERY);
		queryBuffer.append(" AND batch.LIST_KEY='"+listKey+"'"); 
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}
	
	public static String getIntermediateMonomerBatchesQuery(String listKey) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(MONOMER_BATCHES_QUERY);
		queryBuffer.append(" AND batch.LIST_KEY='"+listKey+"'"); 
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	public static String getQueryForDSP(String pageKey) {
		String selectQry = " SELECT SPID,NOTEBOOK,EXPERIMENT, "
				+ " SERIES_ID, PROTOCOL_ID, OWNER_USERNAME, TA_CODE, PROJECT_CODE, "
				+ " XML_METADATA, "
				+ " DESIGN_SUBMITTER"
				+ " FROM CEN_PAGES WHERE PAGE_KEY ='"+pageKey+"'";

		log.debug(selectQry);
		return selectQry;
	}

	public static String getQueryForContainers( boolean isUserDefined) {
		String userDefinedFlag = "N";
		if(isUserDefined)
			userDefinedFlag = "Y";
		String selectQry = " SELECT * FROM CEN_CONTAINER WHERE " +
						" IS_USER_DEFINED='"+userDefinedFlag+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getQueryForAllContainers() {
		String selectQry = " SELECT * FROM CEN_CONTAINER ";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getQueryForUserContainers(String userId){
		String selectQry = " SELECT * FROM CEN_CONTAINER WHERE CREATOR_ID='"+userId+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getQueryForOneContainer(String containerCode){
		String selectQry = " SELECT * FROM CEN_CONTAINER WHERE CONTAINER_CODE='"+containerCode+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getQueryForOneContainerWithKey(String containerKey){
		String selectQry = " SELECT * FROM CEN_CONTAINER WHERE CONTAINER_KEY='"+containerKey+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}

	public static String getValidationQueryForNotebook(String notebook){
		/*
		 * Nvl(expr1 , expr2)
		 * If expr1 is null, NVL returns expr2. If expr1 is not null, NVL returns expr1. 
		 * The arguments expr1 and expr2 can have any datatype. 
		 * If their datatypes are different, Oracle converts expr2 to the datatype of expr1 before comparing them.
		 */
		String selectQry = "SELECT coalesce(COUNT(*), 0) FROM CEN_NOTEBOOKS WHERE " +
			"notebook = '" + notebook + "'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}

	public static String getCheckForNextExperimentAvailabilityQuery(String notebook,String experiment){
		/*
		 * Nvl(expr1 , expr2)
		 * If expr1 is null, NVL returns expr2. If expr1 is not null, NVL returns expr1. 
		 * The arguments expr1 and expr2 can have any datatype. 
		 * If their datatypes are different, Oracle converts expr2 to the datatype of expr1 before comparing them.
		 */
		String selectQry = "SELECT coalesce(COUNT(*), 0) FROM CEN_TEMP_NEXT_EXPERIMENT WHERE " +
			"notebook = '" + notebook + "' and  next_experiment='"+experiment+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getMaxExpNumberQuery(String notebook){
		String selectQry = "SELECT coalesce(MAX(experiment), '0') FROM CEN_PAGES WHERE " +
		"notebook = '" + notebook + "'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
		
	public static String getQueryForMonomerPlates(String pageKey, String stepKey){
		String selectQry = " SELECT * FROM CEN_PLATE p, CEN_CONTAINER c WHERE " +
				"p.PAGE_KEY='"+pageKey+"' AND p.STEP_KEY='"+ stepKey+"'" +
				" AND p.CEN_PLATE_TYPE ='"+CeNConstants.PLATE_TYPE_MONOMER+"'" +
				" AND c.CONTAINER_KEY = p.CONTAINER_KEY";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getQueryForProductPlates(String pageKey, String stepKey){
		String selectQry = " SELECT * FROM CEN_PLATE p, CEN_CONTAINER c WHERE " +
				"p.PAGE_KEY='"+pageKey+"' AND p.STEP_KEY='"+ stepKey+"'" +
				" AND p.CEN_PLATE_TYPE ='"+CeNConstants.PLATE_TYPE_PRODUCT+"'"+
				" AND c.CONTAINER_KEY = p.CONTAINER_KEY";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getQueryForRegisteredPlates(String pageKey){
		String selectQry = " SELECT * FROM CEN_PLATE p, CEN_CONTAINER c WHERE " +
				"PAGE_KEY='"+pageKey+"' " +
				" AND CEN_PLATE_TYPE ='"+CeNConstants.PLATE_TYPE_REGISTRATION+"'"+
				" AND c.CONTAINER_KEY = p.CONTAINER_KEY";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getQueryForAllCROs(){
		String selectQry = " SELECT DISTINCT VENDOR_ID,VENDOR_DISPLAY_NAME FROM CEN_CRO_PAGEINFO ";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getQueryForAllChemistWithCROId(String croId){
		String selectQry = " SELECT DISTINCT VENDOR_CHEMIST_ID,VENDOR_CHEMIST_DISPLAY_NAME FROM CEN_CRO_PAGEINFO WHERE VENDOR_ID='"+croId+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}

	public static String getQueryForAllPagesForChemist(String chemistId){
		String selectQry = " SELECT * FROM CEN_CRO_PAGEINFO WHERE VENDOR_CHEMIST_ID='"+chemistId+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getNotebookPageSummaryForRequestIdQuery(String requestId){
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT  p.PAGE_KEY, p.NOTEBOOK, p.EXPERIMENT, p.PAGE_VERSION , p.PROJECT_CODE, p.TA_CODE, ");
		queryBuffer.append("p.SITE_CODE, p.USERNAME, p.CREATION_DATE, p.SUBJECT,p.LITERATURE_REF, " );
		queryBuffer.append("p.LOOK_N_FEEL, p.PAGE_STATUS, r.RXN_SKETCH, r.NATIVE_RXN_SKETCH  FROM ");
		queryBuffer.append(" CEN_PAGES p ,CEN_REACTION_SCHEMES r ,CEN_REACTION_STEPS s WHERE ");
		queryBuffer.append(" p.PAGE_KEY = r.PAGE_KEY AND r.RXN_SCHEME_KEY = s.RXN_SCHEME_KEY AND s.SEQ_NUM = 0 AND  ");
		queryBuffer.append("p.PAGE_KEY IN (SELECT DISTINCT c.PAGE_KEY FROM CEN_CRO_PAGEINFO c WHERE REQUEST_ID='"+requestId+"')");
		queryBuffer.append("ORDER BY p.EXPERIMENT");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}
	
	
	public static String getNotebookPageSummaryForNBKQuery(String nbkNo){
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT  p.PAGE_KEY, p.NOTEBOOK, p.EXPERIMENT, p.PAGE_VERSION, p.PROJECT_CODE, p.TA_CODE, ");
		queryBuffer.append("p.SITE_CODE, p.USERNAME, p.CREATION_DATE, p.SUBJECT,p.LITERATURE_REF, " );
		queryBuffer.append("p.LOOK_N_FEEL, p.PAGE_STATUS, r.RXN_SKETCH, r.NATIVE_RXN_SKETCH  FROM ");
		queryBuffer.append(" CEN_PAGES p ,CEN_REACTION_SCHEMES r ,CEN_REACTION_STEPS s WHERE ");
		queryBuffer.append(" p.NOTEBOOK='"+nbkNo+"' AND p.PAGE_KEY = r.PAGE_KEY AND r.RXN_SCHEME_KEY = s.RXN_SCHEME_KEY AND s.SEQ_NUM = 0 ");
		queryBuffer.append("ORDER BY p.EXPERIMENT");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	public static String getNotebookPageSummaryForChemistIdQuery(String chemistId){
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT  p.PAGE_KEY, p.NOTEBOOK, p.EXPERIMENT, p.PAGE_VERSION, p.PROJECT_CODE, p.TA_CODE, ");
		queryBuffer.append("p.SITE_CODE, p.USERNAME, p.CREATION_DATE, p.SUBJECT,p.LITERATURE_REF, " );
		queryBuffer.append("p.LOOK_N_FEEL, p.PAGE_STATUS, r.RXN_SKETCH, r.NATIVE_RXN_SKETCH  FROM ");
		queryBuffer.append(" CEN_PAGES p ,CEN_REACTION_SCHEMES r ,CEN_REACTION_STEPS s WHERE ");
		queryBuffer.append(" p.PAGE_KEY = r.PAGE_KEY AND r.RXN_SCHEME_KEY = s.RXN_SCHEME_KEY AND s.SEQ_NUM = 0 AND  ");
		queryBuffer.append("p.PAGE_KEY IN (SELECT DISTINCT c.PAGE_KEY FROM CEN_CRO_PAGEINFO c WHERE VENDOR_CHEMIST_ID='"+chemistId+"')");
		queryBuffer.append("ORDER BY p.EXPERIMENT");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}
	
	public static String getAmountsForPageQuery(String pageKey){
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT * FROM CEN_BATCH_AMOUNTS WHERE PAGE_KEY ='"+pageKey+"'");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	
	public static String getRequestIdForChemistQuery(String chemistId){
		String selectQry = " SELECT DISTINCT(REQUEST_ID) FROM CEN_CRO_PAGEINFO WHERE VENDOR_CHEMIST_ID='"+chemistId+"'";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getCROForNBKQuery(NotebookRef nbref){
		String selectQry = " SELECT * FROM CEN_CRO_PAGEINFO " +
				"WHERE PAGE_KEY=(SELECT PAGE_KEY FROM CEN_PAGES WHERE " +
				"NOTEBOOK='"+nbref.getNbNumber()+"' AND " +
				"EXPERIMENT='"+nbref.getNbPage()+"' AND " +
				"PAGE_VERSION="+nbref.getVersion()+" )";
		log.debug(selectQry);
	//	System.out.println(selectQry);
		return selectQry;
	}
		
	public static String getQueryForAllNotebooksWithChemistId(String chemistId){
		String selectQry = " SELECT DISTINCT NOTEBOOK FROM CEN_PAGES WHERE PAGE_KEY "
			+"IN (SELECT PAGE_KEY FROM CEN_CRO_PAGEINFO WHERE VENDOR_CHEMIST_ID='"+chemistId+"')";
		log.debug(selectQry);
		//	System.out.println(selectQry);
		return selectQry;
	}
	
	public static String getQueryForCROModifiedDate(String requestId){
		String selectQry = "SELECT DISTINCT MODIFIED_DATE FROM CEN_PAGES WHERE PAGE_KEY IN ("+
		"SELECT PAGE_KEY FROM CEN_CRO_PAGEINFO WHERE REQUEST_ID='"+requestId+"')";
		log.debug(selectQry);
		return selectQry;
	}
		
	public static String getQueryForAnalysis(String pageKey){
		String selectQry = "SELECT * FROM CEN_ANALYSIS WHERE PAGE_KEY ='"+pageKey+"'";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getQueryForAttachment(String pageKey){
//		String selectQry = "SELECT * FROM CEN_ATTACHEMENTS WHERE PAGE_KEY ='"+pageKey+"'";
	  String selectQry = // since implementing lazy loading, do not select "BLOB_DATA"
	    "SELECT ATTACHEMENT_KEY,DATE_MODIFIED,DOCUMENT_DESCRIPTION,DOCUMENT_NAME,IP_RELATED," +
	    "  ORIGINAL_FILE_NAME,DOCUMENT_SIZE,DOCUMENT_TYPE FROM CEN_ATTACHEMENTS WHERE PAGE_KEY ='"+pageKey+"'";
		log.debug(selectQry);
		
		return selectQry;
	}
	
  public static String getQueryForAttachmentAndContents(String attachmentKey){
    String selectQry = // since implementing lazy loading, now select the "BLOB_DATA" contents
      "SELECT * FROM CEN_ATTACHEMENTS WHERE ATTACHEMENT_KEY ='"+attachmentKey+"'";
    log.debug(selectQry);
    
    return selectQry;
  }	
	
	public static String getRegistrationJobQuery(String jobId) {
		String selectQry = "SELECT j.*, p.notebook, p.experiment, p.page_version FROM cen_reg_jobs j, cen_pages p WHERE j.job_id = " + jobId + " AND j.page_key = p.page_key";
		return selectQry;
	}
  
  public static String getAllRegistrationJobsQuery(String userId, String status) {
	  String selectQry = "SELECT j.*,p.NOTEBOOK,p.EXPERIMENT,p.PAGE_VERSION " +
				"FROM CEN_REG_JOBS j, CEN_PAGES p WHERE j.STATUS='" + status + "' " +
				"AND j.PAGE_KEY=p.PAGE_KEY AND p.USERNAME='" + userId + "'";
	  
	  return selectQry;
  }
  
	public static String getPendingRegistrationJobsQuery(boolean forUpdate){
		String selectQry = "SELECT j.*,p.NOTEBOOK,p.EXPERIMENT,p.PAGE_VERSION " +
				"FROM CEN_REG_JOBS j, CEN_PAGES p WHERE j.STATUS='" + CeNConstants.JOB_OPEN + "' " +
				"AND j.PAGE_KEY=p.PAGE_KEY";
		if (forUpdate) {
			selectQry = selectQry + " FOR UPDATE";
		}
		log.debug(selectQry);
		return selectQry;
	}	
	
	public static String getAllBatchJobsQuery(String jobId){
		String selectQry = "SELECT * FROM CEN_REGISTERED_BATCHES WHERE JOB_ID='"+jobId+"'";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getAllPendingCompoundRegistrationJobsQuery(){
		String selectQry = "SELECT JOB_ID FROM CEN_REG_JOBS" +
				" WHERE COMPOUND_REGISTRATION_STATUS='"+CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING+"'" ;
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getAllCompoundRegistrationJobOffsetQuery(String jobId){
		String selectQry = "SELECT COMPOUND_REGISTRATION_OFFSET FROM CEN_REGISTERED_BATCHES" +
		" WHERE JOB_ID='"+jobId+"'";
		System.out.println(selectQry);
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getAllRegisteredBatchesForJobidQuery(String jobid) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(" SELECT ");
		queryBuffer.append("r.reg_batch_key, r.page_key, r.batch_key, b.batch_number,");
		queryBuffer.append(" r.conversational_batch_number r_conversational_batch_number,");
		queryBuffer.append("r.parent_batch_number r_parent_batch_number, r.batch_tracking_id,");
		queryBuffer.append("r.job_id, r.registration_status, r.submission_status, r.status,");
		queryBuffer.append("r.compound_aggregation_status, r.cmpd_aggregation_status_msg, r.purification_service_status,");
		queryBuffer.append("r.pur_service_status_msg, r.compound_registration_offset, r.compound_management_status,");
		queryBuffer.append("r.melt_point_val_lower, r.melt_point_val_upper, r.melt_point_comments,");
		queryBuffer.append("r.compound_mgmt_status_message, r.compound_reg_status_message,");
		queryBuffer.append("r.SOURCE_CODE,r.SOURCE_DETAIL_CODE,r.registration_date,");
		queryBuffer.append("r.SUPPLIER_CODE,r.SUPPLIER_REGISTRY_NUMBER,r.COMPOUND_STATE,r.SELECTIVITY_STATUS,r.CONTINUE_STATUS,");
		queryBuffer.append("r.hit_id,r.vnv_status,r.PROTECTION_CODE,r.BATCH_STORAGE_COMMENT,r.BATCH_HAZARD_COMMENT,r.BATCH_HANDLING_COMMENT,r.PRODUCT_FLAG,r.BATCH_OWNER,r.BATCH_COMMENTS,");
		queryBuffer.append("r.xml_metadata as reg_xml_metadata");
		queryBuffer.append(" FROM   cen_registered_batches r, cen_batches b");
		queryBuffer.append(" WHERE JOB_ID='"+jobid+"' and b.batch_key=r.batch_key");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}
	
	public static String getPlateJobQuery(String platekey){
		String selectQry = "SELECT * FROM CEN_REG_JOBS" +
				" WHERE PLATE_KEY='"+platekey+"'" ;
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getQueryForProductPlatesWithSubmissionStatus(String pageKey, String stepKey) {
		String selectQry = " SELECT p.*, c.*, j.*, j.PLATE_KEY JOB_TABLE_PLATEKEY FROM CEN_CONTAINER c, CEN_PLATE p LEFT OUTER JOIN CEN_REG_JOBS j ON p.PLATE_KEY = j.PLATE_KEY WHERE p.CEN_PLATE_TYPE = 'PRODUCT' AND c.CONTAINER_KEY = p.CONTAINER_KEY AND p.PAGE_KEY='" + pageKey + "' AND p.STEP_KEY='" + stepKey + "'";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getQueryForPseudoProductPlateInExperimentPage(String pageKey){
		String selectQry = " SELECT p.*,c.* " +
		" FROM CEN_PLATE p, CEN_CONTAINER c  "+
		"WHERE "+ 
		" p.CEN_PLATE_TYPE ='"+CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO+"'"+
		" AND c.CONTAINER_KEY = p.CONTAINER_KEY "+
		" AND p.PAGE_KEY='"+pageKey+"'";
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getBatchesCountInAListQuery(String listKey) {
		String selectQry = " SELECT coalesce(count(*),0) FROM CEN_BATCHES " +
				" WHERE LIST_KEY='"+listKey+"'"; 
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getStepBatchListsCountQuery(String listKey) {
		String selectQry = " SELECT coalesce(count(*),0) FROM CEN_STEP_BATCH_LISTS " +
				" WHERE LIST_KEY='"+listKey+"'"; 
		log.debug(selectQry);
		return selectQry;
	}
	
	public static String getStoicProductBatchQuery( String batchNumber) {

		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append(STOIC_BATCH_SEARCH_QUERY);
		queryBuffer.append("AND batch.BATCH_TYPE='ACTUAL' AND batch.BATCH_NUMBER='"+batchNumber+"' ORDER BY batch.LAST_MODIFIED DESC");
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}

	public static String getcheckComplianceQuery(String username, int numComplianceDays) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT count(*) num_recs");
		queryBuffer.append(" FROM cen_pages");
		queryBuffer.append(" WHERE username = '" + username + "' AND "); 
		queryBuffer.append(" not page_status in ('COMPLETE', 'ARCHIVED') AND ");
		queryBuffer.append(" latest_version = 'Y' AND ");
		queryBuffer.append(" modified_date < CURRENT_DATE - " + numComplianceDays);
		log.debug(queryBuffer.toString());
		return queryBuffer.toString();
	}
}
