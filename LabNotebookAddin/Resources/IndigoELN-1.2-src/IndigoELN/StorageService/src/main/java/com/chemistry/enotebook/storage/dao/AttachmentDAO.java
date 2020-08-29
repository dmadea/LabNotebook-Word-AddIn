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

import com.chemistry.enotebook.domain.AttachmentModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.AttachmentInsert;
import com.chemistry.enotebook.storage.jdbc.select.AbstractSelect;
import com.chemistry.enotebook.storage.jdbc.select.AttachmentSelect;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.storage.query.UpdateQueryGenerator;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(AttachmentDAO.class);

	public void insertAttachmentList(List<AttachmentModel> attachmentList, String pageKey) throws DAOException {
		try {
			for (AttachmentModel model : attachmentList) {
				insertAttachment(model, pageKey);
			}
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(insertError);
		}
	}

	public void insertAttachment(AttachmentModel attachmentModel, String pageKey) throws DAOException {

		AttachmentInsert insert = new AttachmentInsert(this.getDataSource());
		try {
			log.debug("Insert Attachment :" + attachmentModel.getKey());
			insert.update(new Object[] { attachmentModel.getKey(),// ATTACHEMENT_KEY
					pageKey,// PAGE_KEY
					CommonUtils.replaceSpecialCharsinXML(attachmentModel.toXML()), // XML_METADATA
					attachmentModel.getContents(), // BLOB_DATA
					attachmentModel.getDateModified(),
					attachmentModel.getDocumentDescription(),
					attachmentModel.getDocumentName(),
					attachmentModel.isIpRelated()==true?"Y":"N",
					attachmentModel.getOriginalFileName(),
					new Integer(attachmentModel.getSize()),
					attachmentModel.getType()
					});
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(insertError);
		}
	}

	public void updateAttachmentList(List<AttachmentModel> attachmentList, String pageKey) throws DAOException {
		try {
			for (AttachmentModel model : attachmentList) {
				if (model.isLoadedFromDB()) {
					if (model.isSetToDelete()) {
						deleteAttachment(model.getKey());
					} else {
						updateAttachment(model);
					}
				} else {
					if (!isAttachementInDB(model.getKey())) {
						insertAttachment(model, pageKey);
					}
				}
			}
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(insertError);
		}
	}

	public void updateAttachment(AttachmentModel attachmentModel) throws DAOException {
		try {
			log.debug("Update Attachment :" + attachmentModel.getKey());
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			SqlUpdate su = new SqlUpdate();
			if (attachmentModel.getContents() == null) {
				su.setTypes(new int[] { Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.NUMERIC,Types.VARCHAR, });
			} else {
				su.setTypes(new int[] { Types.VARCHAR, Types.BINARY,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.NUMERIC,Types.VARCHAR, });
			}
			su.setJdbcTemplate(jt);
			String updateSql = UpdateQueryGenerator.getAttachmentUpdateQuery(attachmentModel);
			su.setSql(updateSql);
			su.compile();
			Object[] obj = attachmentModel.getContents() != null ?
					new Object[] {
					CommonUtils.replaceSpecialCharsinXML(attachmentModel.toXML()),
					attachmentModel.getContents(),
					attachmentModel.getDateModified(),
					attachmentModel.getDocumentDescription(),
					attachmentModel.getDocumentName(),
					attachmentModel.isIpRelated()==true?"Y":"N",
					attachmentModel.getOriginalFileName(),
					new Integer(attachmentModel.getSize()),
					attachmentModel.getType()} :
					new Object[] {
					CommonUtils.replaceSpecialCharsinXML(attachmentModel.toXML()),
					attachmentModel.getDateModified(),
					attachmentModel.getDocumentDescription(),
					attachmentModel.getDocumentName(),
					attachmentModel.isIpRelated()==true?"Y":"N",
					attachmentModel.getOriginalFileName(),
					new Integer(attachmentModel.getSize()),
					attachmentModel.getType()};
			
			su.update(obj);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new JDBCRuntimeException(error);
		}
	}

	public List<AttachmentModel> loadAttachment(String pageKey) throws DAOException {
		String selectSql = SelectQueryGenerator.getQueryForAttachment(pageKey);
		List<AttachmentModel> resultList = null;
		try {
			AttachmentSelect select = new AttachmentSelect(this.getDataSource(), selectSql);
			List<AttachmentModel> result = select.execute();
			resultList = new ArrayList<AttachmentModel>(result);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		return resultList;
	}
	
  public AttachmentModel loadAttachmentAndContents(String attachmentKey) throws DAOException {
    String selectSql = SelectQueryGenerator.getQueryForAttachmentAndContents(attachmentKey);
    List<AttachmentModel> resultList = null;
    
    try {
      AttachmentSelect select = new AttachmentSelect(this.getDataSource(), selectSql);
      List<AttachmentModel> result = select.execute();
      resultList = new ArrayList<AttachmentModel>(result);
      if (resultList == null || resultList.size() != 1) {
        return null;
      }
    } catch (Exception error) {
      log.error(CommonUtils.getStackTrace(error));
      throw new DAOException(error);
    }
    
    return (AttachmentModel) resultList.get(0);
  }	

	public void deleteAttachmentList(List<AttachmentModel> attachmentList) throws DAOException {
		try {
			for (int i = 0; i < attachmentList.size(); i++) {
				AttachmentModel model = (AttachmentModel) attachmentList.get(i);
				deleteAttachment(model.getKey());
			}
		} catch (Exception insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw new JDBCRuntimeException(insertError);
		}
	}

	public void deleteAttachment(String attachmentKey) throws DAOException {
		try {
			log.debug("Delete Attachment :" + attachmentKey);
			String removeSql = RemoveQueryGenerator.getRemoveAttachmentQuery(attachmentKey);
			SqlUpdate su = getSqlUpdate(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove Attachment with key :" + attachmentKey);
		} catch (Exception removeError) {
			log.error(CommonUtils.getStackTrace(removeError));
			throw new JDBCRuntimeException(removeError);
		}
	}

	public boolean isAttachementInDB(String attachmentKey) {
		String selectSql = " select attachement_key from cen_attachements where attachement_key='" + attachmentKey + "' ";
		AbstractSelect select = new AbstractSelect(this.getDataSource(), selectSql) {
			public Object mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
				return new AttachmentModel(resultSet.getString("ATTACHEMENT_KEY"));//ATTACHEMENT_KEY
			}
		};
		List<Object> result = select.execute();
		if (result == null || result.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
