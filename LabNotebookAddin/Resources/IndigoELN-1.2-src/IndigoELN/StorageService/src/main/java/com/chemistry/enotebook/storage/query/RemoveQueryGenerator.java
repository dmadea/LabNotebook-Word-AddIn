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

public class RemoveQueryGenerator {
	
	private static final Log log = LogFactory.getLog(RemoveQueryGenerator.class);
	
	private RemoveQueryGenerator() {
	}
		
	public static String getRemoveContainerQuery(String key){
		String removeSql = "DELETE FROM CEN_CONTAINER " +
				"WHERE CONTAINER_KEY='"+key+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemoveNotebookQuery(String pageKey){
		String removeSql = "DELETE FROM CEN_PAGES " +
		"WHERE PAGE_KEY='"+pageKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemoveBatchQuery(String batchKey){
		String removeSql = "DELETE FROM CEN_BATCHES " +
		"WHERE BATCH_KEY='"+batchKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemovePlateQuery(String pageKey){
		String removeSql = "DELETE FROM CEN_PLATE " +
		"WHERE PLATE_KEY='"+pageKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemoveCROQuery(String requestId){
		String removeSql = "DELETE FROM CEN_CRO_PAGEINFO " +
		"WHERE REQUEST_ID='"+requestId+"'";
		log.debug(removeSql);
		return removeSql;
	}

	public static String getRemoveAnalysisQuery(String analysisKey){
		String removeSql = "DELETE FROM CEN_ANALYSIS " +
		"WHERE ANALYSIS_KEY='"+analysisKey+"'";
		log.debug(removeSql);
		return removeSql;
	}

	public static String getRemoveProcedureImageQuery(String imageKey){
		String removeSql = "DELETE FROM CEN_PROCEDURE_IMAGES " +
		"WHERE IMAGE_KEY='"+imageKey+"'";
		log.debug(removeSql);
		return removeSql;
	}

	public static String getRemoveAttachmentQuery(String attachmentKey){
		String removeSql = "DELETE FROM CEN_ATTACHEMENTS " +
		"WHERE ATTACHEMENT_KEY='"+attachmentKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemovePlateWellQuery(String wellKey){
		String removeSql = "DELETE FROM CEN_PLATE_WELL " +
		"WHERE WELL_KEY='"+wellKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemoveStepBatchListsQuery(String listKey){
		String removeSql = "DELETE FROM CEN_STEP_BATCH_LISTS " +
		"WHERE LIST_KEY='"+listKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemovePlateWellForPseudoPlate(String batchKey){
		String removeSql = "DELETE FROM CEN_PLATE_WELL " +
		"WHERE BATCH_KEY='"+batchKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemoveListQuery(String listKey){
		String removeSql = "DELETE FROM CEN_LISTS " +
		"WHERE LIST_KEY='"+listKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
	
	public static String getRemoveStructureQuery(String structKey){
		String removeSql = "DELETE FROM CEN_STRUCTURES " +
		"WHERE STRUCT_KEY='"+structKey+"'";
		log.debug(removeSql);
		return removeSql;
	}
}
