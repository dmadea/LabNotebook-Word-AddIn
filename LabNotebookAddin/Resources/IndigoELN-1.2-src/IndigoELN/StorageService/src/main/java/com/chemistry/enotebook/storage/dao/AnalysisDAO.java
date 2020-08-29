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

import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.jdbc.insert.AnalysisInsert;
import com.chemistry.enotebook.storage.jdbc.select.AnalysisSelect;
import com.chemistry.enotebook.storage.jdbc.update.AnalysisUpdate;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Should we fail all analysis insert/updates? or should we fail only those with a problem?
 * @param analysisList
 * @param pageKey
 * @throws DAOException
 */
public class AnalysisDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(AnalysisDAO.class);
	
	private static final String removeSql = "DELETE FROM CEN_ANALYSIS WHERE ANALYSIS_KEY=?";

	public void insertAnalysisList(List<AnalysisModel> analysisList, String pageKey) throws DAOException {
		for (AnalysisModel model : analysisList) {
			insertAnalysis(model, pageKey);
		}
	}

	/**
	 * TODO: Refactor to ask if the model exists before performing an insert/update.  
	 *       Then be specific on the action taken, therefore the error involved.
	 *       Here returning the insert error may lose the real issue which could be in the update...
	 * @param analysisModel
	 * @param pageKey
	 * @throws DAOException
	 */
	public void insertAnalysis(AnalysisModel analysisModel, String pageKey) throws DAOException {
		String method = "insertAnalysis()";
		log.debug(method+".enter");
		AnalysisInsert insert = new AnalysisInsert(this.getDataSource());
		try {
			insert.update(new Object[] { 
					analysisModel.getKey(),// ANALYSIS_KEY
					pageKey,// PAGE_KEY
					CommonUtils.replaceSpecialCharsinXML(analysisModel.toXML()), // XML_METADATA
					analysisModel.getFile(),// BLOB_DATA
					analysisModel.getCenSampleRef(),// CEN_SAMPLE_REF
					analysisModel.getAnalyticalServiceSampleRef(), // ANALYTICAL_SERVICE_SAMPLE_REF
					analysisModel.getAnnotation(),
					analysisModel.getComments(),
					analysisModel.getSiteCode(),
					analysisModel.getCyberLabDomainId(),
					analysisModel.getCyberLabFileId(),
					analysisModel.getCyberLabFolderId(),
					analysisModel.getCyberLabLCDFId(),
					analysisModel.getCyberLabUserId(),
					analysisModel.getDomain(),
					analysisModel.getServer(),
					analysisModel.getUrl(),
					analysisModel.getUserId(),
					new Long(analysisModel.getVersion()),
					analysisModel.getInstrument(),
					analysisModel.getInstrumentType(),
					analysisModel.getFileName(),
					new Double(analysisModel.getFileSize()),
					analysisModel.getFileType(),
					analysisModel.getExperimentTime(),
					analysisModel.getExperiment(),
					analysisModel.getGroupId(),
					analysisModel.isIPRelated()==true?"Y":"N",
					analysisModel.isLinked()==true?"Y":"N"
		
					});
		} catch (Throwable insertError) {
			log.error("Analysis Insert error. Probably because the analysis row already exists. Attempting to update analysis for pageKey: " + pageKey);
			try {
				updateAnalysis(analysisModel);
				log.info("Analysis update success for pageKey: " + pageKey);
			} catch (Throwable e) {
				log.error("Failed updating anlysis after insert attempt for pageKey: " + pageKey, e);
				throw new DAOException("Failed insert/update of anlysis after insert attempt for pageKey: " + pageKey, insertError);
			}			
		}
		log.debug(method+".exit");
	}

	public void updateAnalysisList(List<AnalysisModel> analysisList, String pageKey) throws DAOException {
		for (AnalysisModel model : analysisList) {
			if (model.isSetToDelete()) {
				deleteAnalysis(model.getKey());
			} else {
				if (model.isLoadedFromDB()) {
					updateAnalysis(model);
				} else {
					insertAnalysis(model, pageKey);
				}
			}
		}
	}

	public void updateAnalysis(AnalysisModel analysisModel) throws DAOException {
		String method = "updateAnalysis()";
		try {
			log.debug(method+".enter");
			SqlUpdate su = new AnalysisUpdate(this.getDataSource());
			su.update(new Object[] {
					CommonUtils.replaceSpecialCharsInText(CommonUtils.replaceSpecialCharsInText(analysisModel.toXML())), // XML_METADATA
					analysisModel.getFile(), // BLOB_DATA
					analysisModel.getCenSampleRef(), // CEN_SAMPLE_REF
					analysisModel.getAnalyticalServiceSampleRef(), // ANALYTICAL_SERVICE_SAMPLE_REF
					analysisModel.getAnnotation(),
					analysisModel.getComments(),
					analysisModel.getSiteCode(),
					analysisModel.getCyberLabDomainId(),
					analysisModel.getCyberLabFileId(),
					analysisModel.getCyberLabFolderId(),
					analysisModel.getCyberLabLCDFId(),
					analysisModel.getCyberLabUserId(),
					analysisModel.getDomain(),
					analysisModel.getServer(),
					analysisModel.getUrl(),
					analysisModel.getUserId(),
					new Long(analysisModel.getVersion()),
					analysisModel.getInstrument(),
					analysisModel.getInstrumentType(),
					analysisModel.getFileName(),
					new Double(analysisModel.getFileSize()),
					analysisModel.getFileType(),
					analysisModel.getExperimentTime(),
					analysisModel.getExperiment(),
					analysisModel.getGroupId(),
					analysisModel.isIPRelated()==true?"Y":"N",
					analysisModel.isLinked()==true?"Y":"N",
					analysisModel.getKey() // ANALYSIS_KEY
					});
		} catch (Exception e) {
			String errorMsg = "Update of analysis failed for analytical key: " + (analysisModel == null ? "<null model>" : analysisModel.getKey()) + 
	  				  ", sampleRef: " + (analysisModel == null ? "<null model>" : analysisModel.getCenSampleRef());
						log.error(errorMsg, e);
						throw new DAOException(errorMsg, e);
		}
		log.debug(method+".exit");
	}

	public List<AnalysisModel> loadAnalysis(String pageKey) throws DAOException {
		String method = "loadAnalysis()";
		log.debug(method+".enter");
		String selectSql = SelectQueryGenerator.getQueryForAnalysis(pageKey);
		List<AnalysisModel> resultList = null;
		try {
			AnalysisSelect select = new AnalysisSelect(this.getDataSource(), selectSql);
			List<AnalysisModel> result = select.execute();
			resultList = new ArrayList<AnalysisModel>(result);
		} catch (Exception error) {
			log.error("Problem loading analysis for page key: " + pageKey, error);
			throw new DAOException("Problem loading analysis for page key: " + pageKey, error);
		}
		log.debug(method+".exit");
		return resultList;

	}
	
	public void deleteAnalysis(String analysisKey) throws DAOException {
		String method = "deleteAnalysis()";
		log.debug(method+".enter");
		try {
			String removeSql = RemoveQueryGenerator.getRemoveAnalysisQuery(analysisKey);
			SqlUpdate su = getSqlUpdate(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove Analysis with key :" + analysisKey);
		} catch (Exception removeError) {
			log.error("Failed removal of analysis object with key " + analysisKey, removeError);
			throw new DAOException("Failed removal of analysis object with key " + analysisKey, removeError);
		}
		log.debug(method+".exit");
	}
	
	public void deleteAnalysisWithBatching(final List<AnalysisModel> analysisList) throws DAOException {
		String method = "deleteAnalysisWithBatching()";
		log.debug(method+".enter");
		JdbcTemplate t = new JdbcTemplate();
		t.setDataSource(super.getDataSource());

		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				AnalysisModel model = (AnalysisModel) analysisList.get(i);
				preparedStatement.setString(1, model.getKey());
			}

			public int getBatchSize() {
				return analysisList.size();
			}

		};

		// execute the batch insert
		int[] rows = t.batchUpdate(AnalysisDAO.removeSql, setter);
		log.debug("Total Analysis records deleted :"+rows.length);
		log.debug(method+".exit");
	}

}
