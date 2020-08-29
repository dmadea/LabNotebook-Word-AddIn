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

import com.chemistry.enotebook.domain.NotebookPageHeaderModel;
import com.chemistry.enotebook.domain.ProcedureImage;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.ProcedureImageInsert;
import com.chemistry.enotebook.storage.jdbc.select.AbstractSelect;
import com.chemistry.enotebook.storage.jdbc.select.ProcedureImageSelect;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.object.SqlUpdate;

import java.util.List;

public class ProcedureImageDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(ProcedureImageDAO.class);
	
	private AbstractSelect selector = null;
	
	public void updateNotebookProcedureImages(NotebookPageHeaderModel pageHeaderModel) throws DAOException {
		// update procedure images
		List<ProcedureImage> procedureImages = pageHeaderModel.getProcedureImages();
		
		if (procedureImages == null) {
			return;
		}
		
		for (ProcedureImage procedureImage : procedureImages) { 
			if (procedureImage == null) {
				continue;
			}
			
			if (StringUtils.equals(procedureImage.getStoreState(), ProcedureImage.NEW)) {
				log.debug("Insert procedure image");
				ProcedureImageInsert insert = new ProcedureImageInsert(this.getDataSource());
				try {
					insert.update(new Object[] { 
							procedureImage.getKey(),// IMAGE_KEY
							pageHeaderModel.getKey(),// PAGE_KEY
							procedureImage.getImageType(), // IMAGE_TYPE
							procedureImage.getImageData(),// IMAGE_DATA
							});
				} catch (Exception insertError) {
					log.error(CommonUtils.getStackTrace(insertError));
					throw new JDBCRuntimeException(insertError);
				}
				continue;
			}
			if (StringUtils.equals(procedureImage.getStoreState(), ProcedureImage.DELETED)) {
				log.debug("Delete procedure image");
				try {
					String removeSql = RemoveQueryGenerator.getRemoveProcedureImageQuery(procedureImage.getKey());
					SqlUpdate su = getSqlUpdate(removeSql);
					su.compile();
					su.update();
					log.debug("Completed Remove Procedure Image with key :" + procedureImage.getKey());
				} catch (Exception removeError) {
					log.error(CommonUtils.getStackTrace(removeError));
					throw new JDBCRuntimeException(removeError);
				}
			}
		}
	}

	public List<ProcedureImage> loadNotebookProcedureImages(String pageKey) throws DAOException {
		List<ProcedureImage> toReturn = null;		
		
		try {
			log.debug("STARTED Loading all procedure images for pageKey :"+pageKey);
			String sqlQuery = SelectQueryGenerator.getProcedureImagesQuery(pageKey);
			this.selector = new ProcedureImageSelect(super.getDataSource(), sqlQuery);
			log.debug("Executing Select for AllReactionSteps without details.");
			toReturn = this.selector.execute();
			
			log.debug("FINISHED Loading all procedure images "+ toReturn);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return toReturn;
	}
}
