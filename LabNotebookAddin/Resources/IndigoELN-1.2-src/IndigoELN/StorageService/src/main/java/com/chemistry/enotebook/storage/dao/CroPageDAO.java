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

import com.chemistry.enotebook.domain.CROChemistInfo;
import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.JDBCRuntimeException;
import com.chemistry.enotebook.storage.jdbc.insert.CROPageInsert;
import com.chemistry.enotebook.storage.jdbc.select.CROPageSelect;
import com.chemistry.enotebook.storage.jdbc.select.CRORequestInfoSelect;
import com.chemistry.enotebook.storage.jdbc.select.NotebookPageSummarySelect;
import com.chemistry.enotebook.storage.query.RemoveQueryGenerator;
import com.chemistry.enotebook.storage.query.SearchQueryGenerator;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CroPageDAO extends StorageDAO {

	private static final Log log = LogFactory.getLog(CroPageDAO.class);

	/*
	 * Inserts CRO Info into CEN_CRO_PAGEINFO table
	 *
	 * @return boolean true if insert was success.
	 */
	public void insertCROPageInfo(CROPageInfo cro) throws DAOException {

		String chemistID = "UNKNOWN";
		String chemistDisplay = "Unknown, Data";
		CROChemistInfo chmistinfo = cro.getCroChemistInfo();
		if (chmistinfo != null) {
			if (chmistinfo.getCroChemistID() != null && !(chmistinfo.getCroChemistID().equals(""))) {
				chemistID = chmistinfo.getCroChemistID();
			}

			if (chmistinfo.getCroChemistDisplayName() != null && !(chmistinfo.getCroChemistDisplayName().equals(""))) {
				chemistDisplay = chmistinfo.getCroChemistDisplayName();
			}
		}
		log.debug("Before inserting CROPageSubmissionInfo---:");

		CROPageInsert insert = new CROPageInsert(this.getDataSource());

		String xmlData = CeNConstants.XML_VERSION_TAG + "<CRO></CRO>";
		if (cro.getAdditionalXMLInfo() != null && !(cro.getAdditionalXMLInfo().trim().equals(""))) {
			xmlData = CommonUtils.replaceSpecialCharsinXML(cro.getAdditionalXMLInfo());
		}
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookInsertDAO.insertCROPage ");
		try {
			insert.update(new Object[] { cro.getKey(), // PAGE_KEY
					cro.getCroID(), // VENDOR_ID
					cro.getCroDisplayName(), // VENDOR_DISPLAY_NAME
					chemistID, // VENDOR_CHEMIST_ID
					chemistDisplay, // VENDOR_CHEMIST_DISPLAY_NAME
					cro.getCroAplicationSourceName(), // VENDOR_APPLICATION_SOURCE
					cro.getRequestId(), // REQUEST_ID
					xmlData.getBytes() // XML_METADATA
					});

			log.debug(" CROPageSubmissionInfo with page_key" + cro.getKey() + " Inserted SUCESSFULLY");

		} catch (Exception insertError) {
			throw new JDBCRuntimeException(insertError);
		} finally {
			stopwatch.stop();
		}
	}

	/**
	 * @return ArrayList containing key=vendorId and value=vendorDisplayName
	 */
	public ArrayList<CROPageInfo> getAllCros() throws DAOException {
		String selectSql = SelectQueryGenerator.getQueryForAllCROs();
		ArrayList<CROPageInfo> resultList = null;
		Map<String, Object> element = null;
		CROPageInfo croPage = null;
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			List<Map<String, Object>> result = jt.queryForList(selectSql);
			resultList = new ArrayList<CROPageInfo>(result.size());
			for (int i = 0; i < result.size(); i++) {
				element = result.get(i);
				croPage = new CROPageInfo("");
				croPage.setCroID((String) element.values().toArray()[0]);
				croPage.setCroDisplayName((String) element.values().toArray()[1]);
				resultList.add(croPage);
			}
		} catch (Exception error) {
			log.error("Failure fetching CRO List.", error);
			throw new DAOException("Failure fetching CRO List.", error);
		}
		return resultList;
	}

	/**
	 * @param croId
	 * @return ArrayList containing ChemistId and ChemistDisplayName
	 */
	public ArrayList<CROChemistInfo> getAllChemistsForCro(String croId) throws DAOException {
		String selectSql = SelectQueryGenerator.getQueryForAllChemistWithCROId(croId);
		ArrayList<CROChemistInfo> resultList = null;
		Map<String, Object> element = null;
		CROChemistInfo croChemist = null;
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			List<Map<String, Object>> result = jt.queryForList(selectSql);
			resultList = new ArrayList<CROChemistInfo>(result.size());
			for (int i = 0; i < result.size(); i++) {
				element = result.get(i);
				croChemist = new CROChemistInfo();
				croChemist.setCroChemistID((String) element.values().toArray()[0]);
				croChemist.setCroChemistDisplayName((String) element.values().toArray()[1]);
				resultList.add(croChemist);
			}
		} catch (Exception error) {
			log.error("Failed getting chemists working for CRO: " + croId, error);
			throw new DAOException("Failed getting chemists working for CRO: " + croId, error);
		}
		return resultList;
	}

	/**
	 * @param chemistId
	 * @return ArrayList containing Objects with nbkRef+CROSubmissionPage info.
	 */
	public ArrayList getAllPagesForChemist(String chemistId) throws DAOException {
		String selectSql = SelectQueryGenerator.getNotebookPageSummaryForChemistIdQuery(chemistId);
		ArrayList resultList = null;
		try {
			NotebookPageSummarySelect select = new NotebookPageSummarySelect(this.getDataSource(), selectSql);
			resultList = new ArrayList(select.execute());

		} catch (Exception error) {
			log.error("Failure while finding all pages for chemist: " + chemistId, error);
			throw new DAOException("Failure while finding all pages for chemist: " + chemistId, error);
		}
		return resultList;
	}

	public ArrayList getAllRequestIdsForChemist(String chemistId) throws DAOException {
		String selectSql = SelectQueryGenerator.getRequestIdForChemistQuery(chemistId);
		ArrayList resultList = null;
		try {
			CRORequestInfoSelect select = new CRORequestInfoSelect(this.getDataSource(), selectSql);
			resultList = new ArrayList(select.execute());
		} catch (Exception error) {
			log.error("Failed look up of request ids for chemist: " + chemistId, error);
			throw new DAOException("Failed look up of request ids for chemist: " + chemistId, error);
		}
		return resultList;
	}

	public ArrayList getAllPagesForRequestId(String requestId) throws DAOException {
		String selectSql = SelectQueryGenerator.getNotebookPageSummaryForRequestIdQuery(requestId);
		ArrayList resultList = null;
		try {
			NotebookPageSummarySelect select = new NotebookPageSummarySelect(this.getDataSource(), selectSql);
			resultList = new ArrayList(select.execute());

		} catch (Exception error) {
			log.error("Failed look up of pages for request id: " + requestId, error);
			throw new DAOException("Failed look up of pages for request id: " + requestId, error);
		}
		return resultList;
	}

	public List<NotebookPageModel> getAllPagesForNotebook(String nbkNo) throws DAOException {
		String selectSql = SelectQueryGenerator.getNotebookPageSummaryForNBKQuery(nbkNo);
		List<NotebookPageModel> resultList = null;
		try {
			NotebookPageSummarySelect select = new NotebookPageSummarySelect(this.getDataSource(), selectSql);
			resultList = new ArrayList<NotebookPageModel>(select.execute());
			for (NotebookPageModel page : resultList) {
				CROPageInfo croInfo = getCroPageForNBK(page.getNbRef());
				page.setCroInfo(croInfo);
			}
		} catch (Exception error) {
			log.error("Failed finding all pages for notebook: " + nbkNo, error);
			throw new DAOException("Failed finding all pages for notebook: " + nbkNo, error);
		}
		return resultList;
	}

	/**
	 * @param cro
	 * @return List of CROSubmissionPageInfo objects
	 */
	public List searchForCROPages(CROPageInfo cro) throws DAOException {
		String searchSql = SearchQueryGenerator.getCROSearchQuery(cro);
		ArrayList resultList = null;
		try {
			CROPageSelect select = new CROPageSelect(this.getDataSource(), searchSql);
			List result = select.execute();
			resultList = new ArrayList(result);
		} catch (Exception error) {
			log.error("Problem searching for CROPages based on CROPageInfo " + 
			          (cro != null ? "Cro Id: " + cro.getCroID() : "CRO object was null."), error);
			throw new DAOException("Problem searching for CROPages based on CROPageInfo " + 
			 			           (cro != null ? "Cro Id: " + cro.getCroID() : "CRO object was null."), error);
		}
		return resultList;
	}

	/**
	 * @param requestId
	 * @return boolean status of requestid availability
	 */
	public boolean isCroRequestIdAvailable(String requestId) throws DAOException {
		try {
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			String query = "SELECT COUNT(*) FROM CEN_CRO_PAGEINFO WHERE REQUEST_ID='" + requestId + "'";
			int result = jt.queryForInt(query);
			if (result > 0)
				return true;
			else
				return false;
		} catch (Exception error) {
			log.error("Failed to determine whether request id: " + requestId + " is available.", error);
			throw new DAOException("Failed to determine whether request id: " + requestId + " is available.", error);
		}

	}

	/**
	 * @param nbkref
	 * @return
	 * @throws DAOException
	 */
	public CROPageInfo getCroPageForNBK(NotebookRef nbkref) throws DAOException {
		String searchSql = SelectQueryGenerator.getCROForNBKQuery(nbkref);
		ArrayList resultList = null;
		try {
			CROPageSelect select = new CROPageSelect(this.getDataSource(), searchSql);
			List result = select.execute();
			resultList = new ArrayList(result);
		} catch (Exception error) {
			log.error(CommonUtils.getStackTrace(error));
			throw new DAOException(error);
		}
		if (resultList != null && resultList.size() > 0)
			return (CROPageInfo) resultList.get(0);
		else
			return null;
	}

	/**
	 * @param requestId
	 * @return
	 * @throws DAOException
	 */
	public Date getCROModifiedDate(String requestId) throws DAOException {
		if (requestId == null)
			return null;
		Date modifiedDate = null;
		String selectSql = SelectQueryGenerator.getQueryForCROModifiedDate(requestId);
		try {
			JdbcTemplate jt = new JdbcTemplate();
			jt.setDataSource(this.getDataSource());
			List result = jt.queryForList(selectSql);
			if (result.size() > 0) {
				Timestamp date = new Timestamp(System.currentTimeMillis());
				modifiedDate = new Date(date.getTime());
			}
		} catch (Exception error) {
			log.error("Failure finding CRO Modified date for request id: " + requestId, error);
			throw new DAOException("Failure finding CRO Modified date for request id: " + requestId, error);
		}
		return modifiedDate;
	}

	/**
	 * @param chemistId
	 * @return ArrayList Containing the
	 * @throws DAOException
	 */
	public List getAllNotebooksForChemistId(String chemistId) throws DAOException {
		String selectSql = SelectQueryGenerator.getQueryForAllNotebooksWithChemistId(chemistId);
		ArrayList resultList = null;
		Map element = null;
		// CROChemistInfo croChemist = null;
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			List result = jt.queryForList(selectSql);
			resultList = new ArrayList(result.size());
			if (result.size() == 0)
				return resultList;
			for (int i = 0; i < result.size(); i++) {
				element = (Map) result.get(i);
				resultList.add((String) element.values().toArray()[0]);
			}
		} catch (Exception error) {
			log.error("Failure fetching notebooks for chemist id:" + chemistId, error);
			throw new DAOException("Failure fetching notebooks for chemist id:" + chemistId, error);
		}
		return resultList;
	}

	public void deletePages(String requestId) throws DAOException {
		try {
			String removeSql = RemoveQueryGenerator.getRemoveCROQuery(requestId);
			SqlUpdate su = getSqlUpdate(removeSql);
			su.compile();
			su.update();
			log.debug("Completed Remove CRO with requestId :" + requestId);
		} catch (Exception removeError) {
			log.error("Failed to remove pages for request id: " + requestId, removeError);
			throw new JDBCRuntimeException("Failed to remove pages for request id: " + requestId, removeError);
		}
	}

}
