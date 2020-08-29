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

import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.searchquery.SearchQueryParams;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryGenerator {
	
	private static final Log log = LogFactory.getLog(SearchQueryGenerator.class);
	
	private static final String CONTAINS = "Contains";	
	private static final String STARTS_WITH = "Starts With";
	private static final String EQUALS = "Equals";	
	private static final String DOES_NOT_CONTAINS = "Does Not Contain";
	
	private static final String PAGES_TABLE = "cen_pages p";
	private static final String REACTION_TABLE = "cen_reaction_schemes r";
	private static final String BATCHES_TABLE = "cen_batches b";
	private static final String STRUCTURES_TABLE = "cen_structures s";
	private static final String RXN_STEP_TABLE = "cen_reaction_steps n";
	private static final String CRO_INFO_TABLE = "cen_cro_pageinfo cri";
	//private static final String BATCHES_AMOUNT_TABLE = "CEN_BATCH_AMOUNTS ba";	
	
	private static final String SELECT_CLAUSE = "SELECT p.PAGE_KEY, p.NOTEBOOK, p.EXPERIMENT, p.PAGE_VERSION, p.PROJECT_CODE, p.TA_CODE, "+
	" p.SITE_CODE, p.USERNAME, p.CREATION_DATE, p.SUBJECT, p.LITERATURE_REF, " +
	" p.LOOK_N_FEEL, p.PAGE_STATUS, r.RXN_SKETCH, r.NATIVE_RXN_SKETCH  FROM ";

	private static final String WHERE_CLAUSE = " WHERE p.PAGE_KEY = r.PAGE_KEY ";//AND r.RXN_SCHEME_KEY = n.RXN_SCHEME_KEY  AND n.SEQ_NUM=0 ";

	private static final String NBK_REF_SELECT = "select p.nbk_ref_version from cen_pages p where p.page_key in ";
	
	// For bingo search
	public static final String SUBSTRUCTURE = "SUBSTRUCTURE";
	public static final String SIMILARITY = "SIMILARITY";
	public static final String EXACT = "EXACT";	
	
	private SearchQueryGenerator() {
	}
	
	private static String getBingoQuery(boolean isReaction, String field, String searchOperator, String searchOption, String dbName) throws Exception {
		String bingoQuery = "";

		if (EXACT.equalsIgnoreCase(searchOperator))
			bingoQuery = BingoQueryGenerator.getExactQuery(isReaction, field, dbName);
		
		if (SUBSTRUCTURE.equalsIgnoreCase(searchOperator))
			bingoQuery = BingoQueryGenerator.getSubstructureQuery(isReaction, field, dbName);
		
		if (SIMILARITY.equalsIgnoreCase(searchOperator)) {
			if (isReaction)
				throw new Exception("Can not search reactions using Similarity in Bingo!");
			bingoQuery = BingoQueryGenerator.getSimilarityQuery(field, Double.parseDouble(searchOption) / 100, Double.valueOf(1), dbName);
		}
		
		return bingoQuery;
	}
	
	public static String getSearchByReactionQuery(String searchOperator, String searchOption, String dbName) throws Exception {
		return NBK_REF_SELECT + "(" + "select r.page_key from cen_reaction_schemes r where " + getBingoQuery(true, "r.native_rxn_sketch", searchOperator, searchOption, dbName) + ")";
	}
	
	public static String getSearchByStructureQuery(String searchOperator, String searchOption, String dbName) throws Exception {
		return NBK_REF_SELECT + "(" + "select b.page_key from cen_batches b where b.batch_type='" + CeNConstants.BATCH_TYPE_ACTUAL + "' and b.struct_key in (select s.struct_key from cen_structures s where " + getBingoQuery(false, "s.native_struct_sketch", searchOperator, searchOption, dbName) + "))";
	}
	
	public static String getNotebookSearchQuery(SearchQueryParams params, 
			String cssSearchType, String nbkRefPages) {
		StringBuffer sqlQueryBuffer = new StringBuffer();

		boolean includeCenStructures = false;

		boolean includeCenProducts = false;

		boolean includeCenBatches = false;

		boolean includeCenReactions = false;

		sqlQueryBuffer.append(SELECT_CLAUSE);

	
		StringBuffer conditionBuffer = new StringBuffer();

		
		log.debug ("CSS SearchType -:"+cssSearchType);
		if (cssSearchType != null && !cssSearchType.equals("")) {
			if (cssSearchType.equalsIgnoreCase(CeNConstants.CEN_CSS_SEARCH_TYPE_PRODUCTS)) {
				includeCenProducts = true;
			} else if (cssSearchType.equalsIgnoreCase(CeNConstants.CEN_CSS_SEARCH_TYPE_REACTIONS)) {
				includeCenReactions = true;
			}
		}

//		conditionBuffer.append(" AND ROWNUM < 1002 ");

		// versioning --CEN_PAGES
		conditionBuffer.append(" AND (p.LATEST_VERSION = 'Y' OR p.USERNAME ='" + params.getOwner() + "')");

		log.debug("CSS Page Status ----:"+params.getPageStatus());
		// page status --CEN_PAGES
		if (params.getPageStatus().size() == 1) {
			conditionBuffer.append(" AND p.PAGE_STATUS = '" + (String) params.getPageStatus().get(0) + "'");
		} else {
			conditionBuffer.append(" AND p.PAGE_STATUS IN ( " + CommonUtils.getConcatenatedString(params.getPageStatus()) + " ) ");
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		if (StringUtils.isNotBlank(params.getSpid())) {
			conditionBuffer.append(" AND p.SPID like '%" + params.getSpid() +"%' ");			
		}
		
		log.debug("CSS Page Types ----:"+params.getPageTypes());
		// PAGE TYPE --CEN_PAGES
		if (params.getPageTypes().size() == 1) {
			conditionBuffer.append(" AND p.LOOK_N_FEEL = '" + (String) (params.getPageTypes().get(0)) + "'");
		} else if (params.getPageTypes().size() > 1) {
			conditionBuffer.append(" AND p.LOOK_N_FEEL IN ( " + CommonUtils.getConcatenatedString(params.getPageTypes()) + ") ");
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS User List ----:"+params.getUserNameList());
		// search domain --CEN_PAGES
		if (!params.isSearchAllSite()) {
			if (params.isSearchAllChemistsOnTheSite()) {
				// will handle this later
			} else {
				List<String> userList = params.getUserNameList();
				if (userList.size() == 1) {
					conditionBuffer.append(
							//" AND p.SITE_CODE = '" + (String) (params.getSiteCodeList().get(0))+"'"
							" AND p.USERNAME = '" + (String) userList.get(0) + "' ");
				} else if (userList.size() == 0) {
					//conditionBuffer.append(" AND p.SITE_CODE = '" + (String) (params.getSiteCodeList().get(0)) + "'");
				} else {
					conditionBuffer.append(//" AND p.SITE_CODE = '" + (String) (params.getSiteCodeList().get(0))+"'"
							 " AND p.USERNAME IN ( " + CommonUtils.getConcatenatedString(params.getUserNameList()) + ") ");
				}
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		conditionBuffer.append(" AND coalesce(r.REACTION_TYPE, 'INTENDED') = 'INTENDED' ");

		log.debug("CSS Reaction Procedure ----:"+params.getReactionProcedure());
		if (params.getReactionProcedure() != null && !params.getReactionProcedure().equals("")) {
			/*
			String field = "p.PROCEDURE";
			String val = params.getReactionProcedure().trim().toUpperCase();
			String operator = params.getReactionProcedureOperator();
			
			if (EQUALS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") = '" + val + "' ");
			} else if (CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '%" + val + "%' ");
			} else if (DOES_NOT_CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") NOT LIKE '%" + val + "%' ");				
			} else if (STARTS_WITH.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '" + val + "%' ");
			}
			*/
			//TODO only "contains"
			conditionBuffer.append(" AND  CTXSYS.CONTAINS( p.PROCEDURE, '" + params.getReactionProcedure() + "' ) <> 0");
		}

		log.debug("CSS Condition Buffer ----:"+conditionBuffer);
		
		log.debug("CSS TA Code list ----:"+params.getTACodeList());
		// fields
		// TA code --page
		if (params.getTACodeList() != null && params.getTACodeList().size() > 0) {

			if (params.getTACodeList().size() == 1) {
				conditionBuffer.append(" AND p.TA_CODE =  '" + params.getTACodeList().get(0) + "' ");
			} else {
				conditionBuffer.append(" AND ( ");
				for (int i = 0; i < params.getTACodeList().size(); i++) {
					if (i < params.getTACodeList().size() - 1) {
						conditionBuffer.append(" p.TA_CODE) =  '" + params.getTACodeList().get(i) + "' OR ");
					} else {
						conditionBuffer.append(" p.TA_CODE) =  '" + params.getTACodeList().get(i) + "' ");
					}
				}
				conditionBuffer.append(" ) ");
			}

		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS Project Code list ----:"+params.getProjectCodeList());
		// project code --page
		if (params.getProjectCodeList() != null && params.getProjectCodeList().size() > 0) {

			if (params.getProjectCodeList().size() == 1) {
				conditionBuffer.append(" AND p.PROJECT_CODE = '" + params.getProjectCodeList().get(0) + "' ");
			} else {
				conditionBuffer.append(" AND ( ");
				for (int i = 0; i < params.getProjectCodeList().size(); i++) {
					if (i < params.getTACodeList().size() - 1) {
						conditionBuffer.append(" p.PROJECT_CODE = '" + params.getProjectCodeList().get(i) + "' OR ");
					} else {
						conditionBuffer.append(" p.PROJECT_CODE = '" + params.getProjectCodeList().get(i) + "' ");
					}
				}
				conditionBuffer.append(" ) ");
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS Subject Title ----:"+params.getSubjectTitle());
		// subject title --page
		if (params.getSubjectTitle() != null && params.getSubjectTitle().trim().length() > 0) {
			String field = "p.SUBJECT";
			String val = params.getSubjectTitle().trim().toUpperCase();
			String operator = params.getSubjectTitleOperator();
			
			if (EQUALS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") = '" + val + "' ");
			} else if (CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '%" + val + "%' ");
			} else if (DOES_NOT_CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") NOT LIKE '%" + val + "%' ");				
			} else if (STARTS_WITH.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '" + val + "%' ");
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS Literature Ref ----:"+params.getLitReference());
		// literature reference --page
		if (params.getLitReference() != null && params.getLitReference().trim().length() > 0) {
			String field = "p.LITERATURE_REF";
			String val = params.getLitReference().trim().toUpperCase();
			String operator = params.getLitReferenceOperator();
			
			if (EQUALS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") = '" + val + "' ");
			} else if (CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '%" + val + "%' ");
			} else if (DOES_NOT_CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") NOT LIKE '%" + val + "%' ");				
			} else if (STARTS_WITH.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '" + val + "%' ");
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS Page Creation Date ----:"+params.getPageCreationDate());
		// page creation date --page
		if (params.getPageCreationDate() != null && params.getPageCreationDate().trim().length() > 0) {
			String dateOperator = params.getDateOperator();
			conditionBuffer.append(" AND round(p.CREATION_DATE, 'DD') " + dateOperator + " to_date('" + params.getPageCreationDate() + "', 'MM DD YYYY')  ");
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS Purity Operator ----:"+params.getPurityOperator());
		// purity --batch
		if (params.getPurityOperator() != null && params.getPurityOperator().trim().length() > 0) {
			String purityOperator = params.getPurityOperator();
			
			log.debug("CSS Purity Lower ----:"+params.getProductPurityLowerValue());
			String lowerValue = params.getProductPurityLowerValue();
			if (purityOperator.equals("Between")) {
				log.debug("CSS Purity Upper ----:"+params.getProductPurityUpperValue());
				String upperValue = params.getProductPurityUpperValue();
				conditionBuffer.append(" and p.PAGE_KEY in (SELECT distinct (page_key) from cen_batch_amounts where PURITY_VALUE >= " + lowerValue + " AND " + " PURITY_VALUE <= " + upperValue + ") ");
			} else {// >=,<=, =
				conditionBuffer.append(" and p.PAGE_KEY in (SELECT distinct (page_key) from cen_batch_amounts where PURITY_VALUE >= " + lowerValue + ") ");
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS THEORITICAL_YIELD_OPERATOR ----:"+params.getYieldOperator());
		// reaction yield --BATCH/Theoretical_Yield_Percent
		if (params.getYieldOperator() != null && params.getYieldOperator().trim().length() > 0) {
			includeCenBatches = true;
			String yieldOperator = params.getYieldOperator();
			log.debug("CSS THEORITICAL_YIELD_PERCENT Lower ----:"+params.getReactionYieldLowerValue());

			String lowerValue = params.getReactionYieldLowerValue();
			if (yieldOperator.equals("Between")) {
				log.debug("CSS THEORITICAL_YIELD_PERCENT Upper ----:"+params.getReactionYieldUpperValue());
				String upperValue = params.getReactionYieldUpperValue();
				conditionBuffer.append(" AND (b.THEORITICAL_YIELD_PERCENT >= " + lowerValue + " AND "
						+ " b.THEORITICAL_YIELD_PERCENT <= " + upperValue + " )");
			} else {// >=,<=, =
				conditionBuffer.append(" AND ROUND(b.THEORITICAL_YIELD_PERCENT) " + yieldOperator + " " + lowerValue);
			}

		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS CompoundId ----:"+params.getCompoundID());
		// compound id --COMPOUND -- CEN_STRUCTURES
		if (params.getCompoundID() != null && params.getCompoundID().trim().length() > 0) {
			includeCenStructures = true;
			
			String field = "s.REGISTRATION_NUMBER";
			String val = params.getCompoundID().trim().toUpperCase();
			String operator = params.getCompoundIDOperator();
			
			if (EQUALS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") = '" + val + "' ");
			} else if (CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '%" + val + "%' ");
			} else if (DOES_NOT_CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") NOT LIKE '%" + val + "%' ");				
			} else if (STARTS_WITH.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '" + val + "%' ");
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS Chemical Name ----:"+params.getChemicalName());
		// chemical name --COMPOUND -- CEN_STRUCTURES
		if (params.getChemicalName() != null && params.getChemicalName().trim().length() > 0) {
			includeCenStructures = true;
			
			String field = "s.CHEMICAL_NAME";
			String val = params.getChemicalName().trim().toUpperCase();
			String operator = params.getChemicalNameOperator();
			
			if (EQUALS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") = '" + val + "' ");
			} else if (CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '%" + val + "%' ");
			} else if (DOES_NOT_CONTAINS.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") NOT LIKE '%" + val + "%' ");				
			} else if (STARTS_WITH.equals(operator)) {
				conditionBuffer.append(" AND UPPER(" + field + ") LIKE '" + val + "%' ");
			}
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		String reactionOrderString = " ORDER BY p.SITE_CODE, p.USERNAME, p.NOTEBOOK, p.EXPERIMENT ";

		sqlQueryBuffer.append(PAGES_TABLE + ", " + REACTION_TABLE + " "); //", " + RXN_STEP_TABLE + " ");// + ", " + CRO_INFO_TABLE);
						
		log.debug("CSS includeCenBatches ----:"+includeCenBatches);
		if (includeCenBatches) {
			sqlQueryBuffer.append(" RIGHT OUTER JOIN " + BATCHES_TABLE + " ON r.PAGE_KEY = b.PAGE_KEY ");
			conditionBuffer.append(" AND p.PAGE_KEY = b.PAGE_KEY AND  ");
		}
		
		if (StringUtils.isNotBlank(params.getRequestId())) {
			sqlQueryBuffer.append(", " + CRO_INFO_TABLE);
		}
		
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);
		log.debug("CSS includeCenStructures ----:"+includeCenStructures);
		if (includeCenStructures) {
			sqlQueryBuffer.append(", " + STRUCTURES_TABLE);
			conditionBuffer.append(" AND p.PAGE_KEY = s.PAGE_KEY AND  r.PAGE_KEY = s.PAGE_KEY");
		}
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);

		log.debug("CSS includeCenProducts ----:"+includeCenProducts);
		log.debug("CSS includeCenReactions ----:"+includeCenReactions);

		if(includeCenProducts || includeCenReactions){
			if(StringUtils.isNotBlank(nbkRefPages)){
				//added this statement since CSS search returns PagekKeys instead of page
				conditionBuffer.append(" AND p.NBK_REF_VERSION IN ("+nbkRefPages+")");
			} 
		} else	if (includeCenStructures) {
			if (includeCenBatches) {
				conditionBuffer.append(" AND s.STRUCT_KEY = b.STRUCT_KEY ");
			}
		} else if (includeCenBatches) {
			// add to whereClause if anything required.
		}
		
		// Request ID:
		if (StringUtils.isNotBlank(params.getRequestId())) {
			conditionBuffer.append(" and cri.request_id = " + params.getRequestId() + " ");
			conditionBuffer.append(" and cri.page_key = p.page_key ");
		}
		
		log.debug("CSS Condition Buffer ----:"+conditionBuffer);
        
		sqlQueryBuffer.append(WHERE_CLAUSE);
		sqlQueryBuffer.append(conditionBuffer);
		sqlQueryBuffer.append(reactionOrderString);

		// System.out.println("The final query string is: " + sqlQueryBuffer.toString());
		log.debug(sqlQueryBuffer.toString());
		return sqlQueryBuffer.toString();
	}
	
	public static String getCROSearchQuery(CROPageInfo croPage) {
		StringBuffer sqlQueryBuffer = new StringBuffer();
		List conditionList = new ArrayList(7);
		sqlQueryBuffer.append("SELECT * FROM CEN_CRO_PAGEINFO ");
		
		if(CommonUtils.isNotNull(croPage.getCroID()))
			conditionList.add(" VENDOR_ID LIKE '%"+
					croPage.getCroID()+"%'");
		
		if(CommonUtils.isNotNull(croPage.getCroDisplayName()))
			conditionList.add(" VENDOR_DISPLAY_NAME LIKE '%"+
					croPage.getCroDisplayName()+"%'");
		
		if(CommonUtils.isNotNull(croPage.getCroChemistInfo().getCroChemistID()))
			conditionList.add(" VENDOR_CHEMIST_ID LIKE '%"+
					croPage.getCroChemistInfo().getCroChemistID()+"%'");
		
		if(CommonUtils.isNotNull(croPage.getCroChemistInfo().getCroChemistDisplayName()))
			conditionList.add(" VENDOR_CHEMIST_DISPLAY_NAME LIKE '%"+
					croPage.getCroChemistInfo().getCroChemistDisplayName()+"%'");

		if(CommonUtils.isNotNull(croPage.getCroAplicationSourceName()))
			conditionList.add(" VENDOR_APPLICATION_SOURCE LIKE '%"+
					croPage.getCroAplicationSourceName()+"%'");
		
		if(CommonUtils.isNotNull(croPage.getRequestId()))
			conditionList.add(" REQUEST_ID LIKE '%"+
					croPage.getRequestId()+"%'");
		
		
		//For adding WHERE clause
		if(conditionList.size() > 0)
			sqlQueryBuffer.append(" WHERE "+conditionList.get(0));
		//i starting with 1, instead of 0,for appending AND condition
		for(int i=1; i<conditionList.size(); i++){
			sqlQueryBuffer.append(" AND "+conditionList.get(i));
		}
		log.debug(sqlQueryBuffer.toString());
		return sqlQueryBuffer.toString();
	}
}
