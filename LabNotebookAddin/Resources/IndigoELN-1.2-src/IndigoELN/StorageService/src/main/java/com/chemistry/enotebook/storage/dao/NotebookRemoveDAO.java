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

import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.util.List;

public class NotebookRemoveDAO extends StorageDAO {
	
	private static final Log log = LogFactory.getLog(NotebookRemoveDAO.class);
	
	/*
	 * Removes experiment given the page key.
	 */
		
	public void removeExperiment(String pageKey) throws DAOException{
		try{
			String removeSql = RemoveQueryGenerator.getRemoveNotebookQuery(pageKey);
			SqlUpdate su = getSqlUpdate(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove Notebook with pageKey :"+pageKey);
		} catch (Exception error){
			log.error(CommonUtils.getStackTrace(error));
			throw new JDBCRuntimeException(error);
		}
	}

	public void removeBatches(List deletableBatches) throws DAOException{
		try{
			BatchModel mBatch = null;
			int size = deletableBatches.size();
			for(int i=0;i<size;i++){
				mBatch = (BatchModel)deletableBatches.get(i);
				
				//If PUA ProductBatch also delete the PlateWell. DSP ProductBatches will never be deleted.
				// For satisfying key constraints we need to delete PlateWell first
				if(mBatch instanceof ProductBatchModel)
				{
					String removeSql3 = RemoveQueryGenerator.getRemovePlateWellForPseudoPlate(mBatch.getKey());
					SqlUpdate su3 = getSqlUpdate(removeSql3);
					su3.update();
					log.debug("PlateWell deleted for ProductBatch batchkey:"+mBatch.getKey());
				}
				//Delete from CEN_STRUCTURES first. There is no FK and CASCADE Delete setting on batch delete
				String removeSql5 = 
					RemoveQueryGenerator.getRemoveStructureQuery(mBatch.getCompound().getKey());
				SqlUpdate su = getSqlUpdate(removeSql5);
				su.update();
				log.debug("Completed Remove Structure with key :"+mBatch.getCompound().getKey());
				//Delete ProductBatch from CEN_BATCHES
				String removeSql = 
					RemoveQueryGenerator.getRemoveBatchQuery(mBatch.getKey());
				su = getSqlUpdate(removeSql);
				su.update();
				log.debug("Completed Remove Batch with batchType :"+mBatch.getBatchType());
				
				//This is generic Batch Delete.
				//After last PUA,STE batch delte check if the list needs to be deleted.
				if(i == (size-1))
				{
					//Check if batches exist for this list
					JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
					int result = jt.queryForInt(SelectQueryGenerator.getBatchesCountInAListQuery(mBatch.getListKey()));
					if(result == 0)
					{
						//No batches linked to this list.Delete the List key as well.
						String removeSql2 = 
							RemoveQueryGenerator.getRemoveStepBatchListsQuery(mBatch.getListKey());
						su = getSqlUpdate(removeSql2);
						su.update();
						log.debug("List entry deleted in CEN_STEP_BATCH_LISTS for list key:"+mBatch.getListKey());
						String removeSql4 = 
							RemoveQueryGenerator.getRemoveListQuery(mBatch.getListKey());
						su = getSqlUpdate(removeSql4);
						su.update();
						log.debug("List entry deleted in CEN_LIST for list key:"+mBatch.getListKey());
					}else
					{
						log.debug("List entry not delted since batch count in it is:"+result+ " for list key:"+mBatch.getListKey());
						
					}
				}
				
			}
		} catch (Exception error){
			log.error(CommonUtils.getStackTrace(error));
			throw new JDBCRuntimeException(error);
		}
	}
}
