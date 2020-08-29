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
/*
 * Created on Mar 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.storage.dao;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.session.security.HttpUserProfile;
import com.chemistry.enotebook.storage.DAOException;
import com.chemistry.enotebook.storage.NotebookInvalidException;
import com.chemistry.enotebook.storage.query.SelectQueryGenerator;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ParallelExpDuperDeDuper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.naming.NamingException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * 
 * 
 */
public class NotebookDAO extends StorageDAO implements NotebookManager {
	// private static NotebookDAO notebookDao = null;
	private static final Log log = LogFactory.getLog(NotebookDAO.class);

	/**
	 * Inserts NotebookPageModel using NotebookInsertDAO, This method all Database operations are in one transaction defined in
	 * storage-dao.xml as <prop key="insertNotebookPage*"> PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED </prop>
	 * 
	 * @param pageModel
	 * @throws DAOException
	 */
	public void insertNotebookPage(NotebookPageModel pageModel, HttpUserProfile userProfile) throws DAOException {
		DAOFactory factory = getDaoFactory();
		String nbkNumber = "";
		String pageNumber = "";
		try {
			nbkNumber = pageModel.getNbRef().getNbNumber();
			pageNumber = pageModel.getNbRef().getNbPage();
			NotebookInsertDAO insertDAO = factory.getNotebookInsertDAO();
			insertDAO.setUserProfile(userProfile);
			insertDAO.insertNotebookPage(pageModel);
		} catch (DAOException insertError) {
			log.error(CommonUtils.getStackTrace(insertError));
			throw insertError;
		} finally {
			try {
				// Remove the temp experiment number from CEN_TEMP_NEXT_EXPERIMENT table
				removeNextExperimentFromTempTable(nbkNumber, pageNumber);
			} catch (Exception e) {
				log.info("Problem deleting the unused experiment page number from CEN_TEMP_NEXT_EXPERIMENT table.This page number("
						+ pageNumber + ") will be skipped for notebook:." + nbkNumber);
				log.error(CommonUtils.getStackTrace(e));
			}
			releaseDaoFactory(factory);
		}
	}

	/**
	 * Update NotebookPageModel using NotebookUpdateDAO, This method all Database operations are in one transaction defined in
	 * storage-dao.xml as <prop key="updateNotebookPage*"> PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED </prop>
	 * 
	 * @param pageModel
	 * @throws DAOException
	 */
	public void updateNotebookPage(NotebookPageModel pageModel, HttpUserProfile userProfile) throws DAOException {

		DAOFactory factory = getDaoFactory();
		try {
			NotebookUpdateDAO updateDao = factory.getNotebookUpdateDAO();
			updateDao.updateNotebookPageHeader(pageModel.getPageHeader());

			ProcedureImageDAO procedureImageDAO = factory.getProcedureImageDAO();
			procedureImageDAO.updateNotebookProcedureImages(pageModel.getPageHeader());
				
			AnalysisCacheModel analysisCache = pageModel.getAnalysisCache();
			AnalysisDAO analysisDAO = this.getDaoFactory().getAnalysisDAO();
			analysisDAO.updateAnalysisList(analysisCache.getAnalyticalList(), pageModel.getKey());

			AttachmentCacheModel attachmentCache = pageModel.getAttachmentCache();
			AttachmentDAO attachmentDAO = this.getDaoFactory().getAttachmentDAO();
			attachmentDAO.updateAttachmentList(attachmentCache.getAttachmentList(), pageModel.getKey());

			// update PseudoPlate first before updating the batches since this can cause FK issue with ..
			// .. User added batch delete if PlateWell referencing it is not deleted first.
			
			PlateDAO plateDao = factory.getPlateDAO();
			if (pageModel.isParallelExperiment()) {
				plateDao.updatePseudoProductPlate(pageModel.getPseudoProductPlate(false), pageModel.getKey(), "UPDATE_DELETE");
				plateDao.updatePlate(pageModel.getPseudoProductPlate(false)); // update comments
			}
			if (pageModel.getReactionSteps().size() > 0) {
				// update ReactionSteps
				updateDao.updateReactionSteps(pageModel.getKey(), pageModel.getReactionSteps());
				if (pageModel.isParallelExperiment()) {
					// Do PlateWell inserts after batches insert to satisfy FK2
					plateDao.updatePseudoProductPlate(pageModel.getPseudoProductPlate(false), pageModel.getKey(), "INSERT");
				}
			}
			if (pageModel.isParallelExperiment()) {
				// update RegistredPlates
				updateDao.updateRegisteredPlates(pageModel.getKey(), pageModel.getRegisteredPlates());
			}
			releaseDaoFactory(factory);
		} catch (DAOException updateError) {
			log.error(CommonUtils.getStackTrace(updateError));
			throw updateError;
		} finally {
			releaseDaoFactory(factory);
		}

	}

	/**
	 * Loads NotebookPageModel using NotebookLoadDAO,
	 * 
	 * @param NotebookRef
	 * @throws DAOException
	 */
	public NotebookPageModel loadNotebookPage(NotebookRef notebookRef) throws DAOException {
		NotebookPageModel pageModel = null;
		DAOFactory factory = getDaoFactory();
		try {
			NotebookLoadDAO loadDAO = factory.getNotebookLoadDAO();
			pageModel = loadDAO.loadNotebookPage(notebookRef);

			// Sort the batches in steps for parallel exp
			if (pageModel != null && pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
				ParallelExpDuperDeDuper deduper = new ParallelExpDuperDeDuper();
				deduper.sortMonomerBatchesInSteps(pageModel);
			}
		} catch (DAOException error) {
			throw error;
		} finally {
			releaseDaoFactory(factory);
		}
		return pageModel;
	}

	/**
	 * deleteExperiment using NotebookUpdateDAO, udpates the page status
	 * 
	 * @param pageKey
	 * @param modifiedDate
	 * @throws DAOException
	 */
	public void deleteExperiment(String pageKey, Timestamp modifiedDate) throws DAOException {
		DAOFactory factory = getDaoFactory();
		try {
			NotebookUpdateDAO updateDAO = factory.getNotebookUpdateDAO();
			updateDAO.updateNotebookPageStatus(pageKey, CeNConstants.PAGE_STATUS_DELETED, modifiedDate);

		} catch (DAOException error) {
			throw error;
		} finally {
			releaseDaoFactory(factory);
		}
	}

	public void deletePlateWells(String[] plateWellKeys, HttpUserProfile userProfile) throws DAOException {
		DAOFactory factory = getDaoFactory();
		try {
			PlateDAO plateDAO = factory.getPlateDAO();
			if (plateWellKeys != null && plateWellKeys.length > 0) {
				for (int i = 0; i < plateWellKeys.length; ++i) {
					plateDAO.removePlateWell(plateWellKeys[i]);
				}
				log.info("Total plate wells deleted:" + plateWellKeys.length);
			} else {
				log.info("No plate wells to delete");
			}
		} catch (DAOException deleteError) {
			log.error(CommonUtils.getStackTrace(deleteError));
			throw deleteError;
		} finally {
			releaseDaoFactory(factory);
		}
	}
	
	/**
	 * Delete Plates using PlateDAO, This method all Database operations are in one transaction defined in storage-dao.xml as <prop
	 * key="deletePlates*"> PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED </prop>
	 * 
	 * @param plateKeys
	 * @throws DAOException
	 */
	public void deletePlates(String[] plateKeys, HttpUserProfile userProfile) throws DAOException {
		DAOFactory factory = getDaoFactory();
		try {
			PlateDAO plateDAO = factory.getPlateDAO();

			if (plateKeys != null && plateKeys.length > 0) {
				int size = plateKeys.length;
				for (int i = 0; i < size; i++) {
					plateDAO.removePlate(plateKeys[i]);
				}
				log.info("Total plates deleted:" + size);
			} else {
				log.info("No plates to delete");
			}
		} catch (DAOException deleteError) {
			log.error(CommonUtils.getStackTrace(deleteError));
			throw deleteError;
		} finally {
			releaseDaoFactory(factory);
		}
	}

	/**
	 * @param siteCode
	 * @param userID
	 * @param notebook
	 * @param creator
	 * @throws DAOException
	 * 
	 */
	public void createNotebook(String siteCode, String userID, String notebook, String creator) throws DAOException {

		DAOFactory factory = getDaoFactory();
		try {
			NotebookInsertDAO insertDAO = factory.getNotebookInsertDAO();
			insertDAO.createNewNotebook(siteCode, userID, notebook, creator);
		} catch (DAOException createError) {
			log.error(CommonUtils.getStackTrace(createError));
			throw createError;
		} finally {
			releaseDaoFactory(factory);
		}
	}

	/**
	 * @param siteCode
	 * @param notebook
	 * @param experiment
	 * @param version
	 * @throws DAOException
	 * 
	 * @deprecated This method is deprecated, instead use <code>updateNotebookPageStatus(pageKey,status)</code> defined in
	 *             NotebookUpdateDAO
	 */
	public void deleteExperiment(String siteCode, String notebook, String experiment, int version) throws DAOException {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = this.getCeNConnection();

			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM cen_pages WHERE site_code = '" + siteCode + "' AND notebook = '" + notebook
					+ "' AND experiment = '" + experiment + "' AND page_version = " + version);
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, null);
		}
	}

	public String[] getUsersFullName(String[] userIDs) throws DAOException {
		String s_UserQuery = "SELECT fullname FROM CEN_USERS WHERE username = ?";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String[] result = new String[userIDs.length];

		try {
			conn = this.getCeNConnection();

			stmt = conn.prepareStatement(s_UserQuery.toString());

			for (int i = 0; i < userIDs.length; i++) {
				stmt.setString(1, userIDs[i].toUpperCase());

				rs = stmt.executeQuery();
				if (rs.next()) {
					if (rs.getString("fullname") != null) {
						result[i] = rs.getString("fullname");
					}
				}

				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
				}
				rs = null;
			}
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, rs);
		}

		return result;
	}

	public String getNotebookPageCompleteStatus(String siteCode, String nbRefStr,
			int version) throws DAOException, InvalidNotebookRefException {
		NotebookRef nbRef = new NotebookRef(nbRefStr);
		String query = "select page_status from cen_pages WHERE site_code = '"
				+ siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber()
				+ "' AND" + " experiment = '" + nbRef.getNbPage() + "' AND"
				+ " page_version = " + version;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = this.getCeNConnection();
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			resultSet.next();
			if (resultSet.getRow() > 0) {
				return resultSet.getString("PAGE_STATUS");
			} else {
				return "";
			}
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, null);
		}
	}
	
	public void updateNotebookPageStatus(String siteCode, String nbRefStr, int version, String status) throws DAOException, InvalidNotebookRefException {
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return;
		}
		if ((status == null) || (status.length() == 0)) {
			return;
		}
		if (version <= 0) {
			return;
		}

		NotebookRef nbRef = new NotebookRef(nbRefStr);

		SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z");
		String completion_date = df.format(new Date());
		
		String s_Query = "UPDATE cen_pages" + "  SET page_status = '" + status + "'";
		s_Query += ", xml_metadata = ?";
		s_Query += " WHERE site_code = '" + siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber() + "' AND" + " experiment = '" + nbRef.getNbPage() + "' AND"
				+ " page_version = " + version;
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = this.getCeNConnection();
			stmt = conn.createStatement();
			
			String xmlMetadata = "";
			
			ResultSet rs = stmt.executeQuery("select xml_metadata from cen_pages WHERE site_code = '" + siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber() + "' AND" + " experiment = '" + nbRef.getNbPage() + "' AND" + " page_version = " + version);
			if (rs.next())
				xmlMetadata = rs.getString("XML_METADATA");
				
			xmlMetadata = CeNXMLParser.updateXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Completion_Date", completion_date);
			
			PreparedStatement pstmt = conn.prepareStatement(s_Query);
			pstmt.setString(1, xmlMetadata);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, null);
		}
	}

	public void updateNotebookPageStatus(String siteCode, String nbRefStr, int version, String status, int ussiKey)
			throws DAOException, InvalidNotebookRefException {
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return;
		}
		if ((status == null) || (status.length() == 0)) {
			return;
		}
		if (version <= 0) {
			return;
		}

		NotebookRef nbRef = new NotebookRef(nbRefStr);

		String s_Query = "UPDATE cen_pages" + "  SET page_status = '" + status + "'";

		String ussiVal = "";
		if ((ussiKey != 0) && (ussiKey != -1)) {
			ussiVal = "" + ussiKey;
			s_Query = s_Query + ", xml_metadata = ? ";
		}

		s_Query = s_Query + " WHERE site_code = '" + siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber() + "' AND"
				+ " experiment = '" + nbRef.getNbPage() + "' AND" + " page_version = " + version;

		Connection conn = null;
		Statement stmt = null;

		try {
			conn = this.getCeNConnection();
			stmt = conn.createStatement();

			PreparedStatement pstmt = conn.prepareStatement(s_Query);
				
			if ((ussiKey != 0) && (ussiKey != -1)) {
				String xmlMetadata = "";
				
				ResultSet rs = stmt.executeQuery("select xml_metadata from cen_pages WHERE site_code = '" + siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber() + "' AND" + " experiment = '" + nbRef.getNbPage() + "' AND" + " page_version = " + version);
				if (rs.next())
					xmlMetadata = rs.getString("XML_METADATA");
				xmlMetadata = CeNXMLParser.updateXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Ussi_Key", ussiVal);
				pstmt.setString(1, xmlMetadata);
			}
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, null);
		}
	}

	public byte[] getExperimentPDF(String siteCode, String nbRefStr, int version) throws DAOException, InvalidNotebookRefException {
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return null;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return null;
		}
		if (version <= 0) {
			return null;
		}

		NotebookRef nbRef = new NotebookRef(nbRefStr);

		String s_Query = "SELECT pdf_document " + " FROM cen_pages " + " WHERE site_code = '" + siteCode + "' AND "
				+ " notebook = '" + nbRef.getNbNumber() + "' AND" + "  experiment = '" + nbRef.getNbPage() + "' AND"
				+ " page_version = " + version;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getCeNConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(s_Query);

			if (rs.next()) {
				return rs.getBytes(1);
			} else {
				return null;
			}
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, rs);
		}
	}

	public void storeExperimentPDF(String siteCode, String nbRefStr, int version, byte[] pdf) throws DAOException,
			InvalidNotebookRefException {

		log.debug("NotbookDAO.storeExperimentPDF().enter");
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return;
		}
		if (version <= 0) {
			return;
		}
		if (pdf == null) {
			pdf = new byte[0];
		}

		NotebookRef nbRef = new NotebookRef(nbRefStr);

		String s_Query = "UPDATE cen_pages" + "  SET pdf_document = ?" + "  WHERE site_code = '" + siteCode + "' AND "
				+ " notebook = '" + nbRef.getNbNumber() + "' AND" + "  experiment = '" + nbRef.getNbPage() + "' AND"
				+ " page_version = " + version;
		log.debug(s_Query);
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = this.getCeNConnection();
			stmt = conn.prepareStatement(s_Query);
			stmt.setBytes(1, pdf);
			stmt.executeUpdate();
			log.debug("NotbookDAO.storeExperimentPDF().Sql executed. PDF size:" + pdf.length + " for NB page"
					+ nbRef.getNotebookRef());
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, null);
		}
	}

	private Connection getCeNConnection() throws NamingException, SQLException, PropertyException {
		return this.getDataSource().getConnection();
	}

	/*
	 * Asuming Notebook no is unique in CeN, this method is replaced earlier method which accepted site_code as 2nd argument.
	 */
	public int getNextExperimentForNotebook(String notebook) throws DAOException, NotebookInvalidException {
		int result = -1;
		int nbkResult = -1;
		// Check if notebook exists and also if there is duplicate notebook
		JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
		nbkResult = jt.queryForInt(SelectQueryGenerator.getValidationQueryForNotebook(notebook));

		if (nbkResult == 0) {
			throw new NotebookInvalidException("Notebook '" + notebook + "' not found");
		} else if (nbkResult > 1) {
			throw new NotebookInvalidException("Duplicate Notebook found for '" + notebook + "'");
		}

		result = jt.queryForInt(SelectQueryGenerator.getMaxExpNumberQuery(notebook));

		result = result + 1;

		if (result > NotebookPageUtil.NB_MAX_PAGE_NUMBER) {
			throw new NotebookInvalidException("Notebook '" + notebook + "' is full, "
					+ "New Experiments cannot be created for this Notebook");
		}
		// Logic to see if this page number is already reserverd in temp table and currently being processed for insert
		// If found the number in temp table increment the number untill we get a number not in use
		while (!isNextExperimentAvailable(notebook, result + "")) {
			result++;
			if (result > NotebookPageUtil.NB_MAX_PAGE_NUMBER) {
				throw new NotebookInvalidException("Notebook '" + notebook + "' is full, "
						+ "New Experiments cannot be created for this Notebook");
			}
		}
		try {
			registerNextExperimentInTempTable(notebook, result + "");
		} catch (DAOException error) {
			log.error(error);
			log.error("UNIQUE KEY Exception, Trying getNextExperimentForNotebook AGAIN");
			result = getNextExperimentForNotebook(notebook);
		}

		log.debug("Registered temp NewExperiment :" + notebook + "-" + result);
		return result;
	}

	private boolean isNextExperimentAvailable(String notebook, String experiment) throws DAOException {
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			int count = jt.queryForInt(SelectQueryGenerator.getCheckForNextExperimentAvailabilityQuery(notebook, experiment));
			if (count == 0)
				return true;
			else
				return false;
		} catch (Exception error) {
			throw new DAOException(error);
		}
	}

	private void registerNextExperimentInTempTable(String notebook, String experiment) throws DAOException {
		try {
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			jt.update("insert into CEN_TEMP_NEXT_EXPERIMENT(NOTEBOOK,NEXT_EXPERIMENT) VALUES('" + notebook + "','" + experiment + "')");
		} catch (Exception error) {
			// error.printStackTrace();
			throw new DAOException(error);
		}
	}

	private void removeNextExperimentFromTempTable(String notebook, String experiment) throws DAOException {
		try {
			int expNo = Integer.parseInt(experiment);
			JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
			jt.update("delete from CEN_TEMP_NEXT_EXPERIMENT where NOTEBOOK='" + notebook + "' AND NEXT_EXPERIMENT='" + expNo + "'");
		} catch (Exception error) {
			throw new DAOException(error);
		}
	}

	/**
	 * Loads NotebookPageModel using NotebookLoadDAO,
	 * 
	 * @param NotebookRef
	 * @throws DAOException
	 */
	public ProductBatchModel loadProductBatchModel(String batchNumber) throws DAOException {
		ProductBatchModel prodBatchModel = null;
		DAOFactory factory = getDaoFactory();
		try {
			NotebookLoadDAO loadDAO = factory.getNotebookLoadDAO();
			prodBatchModel = loadDAO.loadProductBatchModel(batchNumber);

		} catch (DAOException error) {
			throw error;
		} finally {
			releaseDaoFactory(factory);
		}
		return prodBatchModel;
	}

	public int checkCompliance(String username, int numComplianceDays) throws DAOException {
		int count = 0;

		if (username == null || username.length() == 0)
			return 0;
		JdbcTemplate jt = new JdbcTemplate(this.getDataSource());
		count = jt.queryForInt(SelectQueryGenerator.getcheckComplianceQuery(username, numComplianceDays));
		return count;
	}

	public void updateNotebookPage(String siteCode, String nbRefStr, int version, String status, int ussiKey, byte[] pdfContent,
			String completionDateTSStr) throws DAOException, InvalidNotebookRefException {
		log.debug("updateNotebookPage().enter");
		if ((siteCode == null) || (siteCode.length() == 0)) {
			return;
		}
		if ((nbRefStr == null) || (nbRefStr.length() == 0)) {
			return;
		}
		if ((status == null) || (status.length() == 0)) {
			return;
		}
		if (version <= 0) {
			return;
		}

		NotebookRef nbRef = new NotebookRef(nbRefStr);

		String s_Query = "UPDATE cen_pages" + " SET pdf_document = ?,page_status = '" + status + "'";

		String ussiVal = "";
		if ((ussiKey != 0) && (ussiKey != -1)) {
			ussiVal = "" + ussiKey;
		}
		s_Query = s_Query + ", xml_metadata = ? ";

		s_Query = s_Query + " WHERE site_code = '" + siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber() + "' AND"
				+ " experiment = '" + nbRef.getNbPage() + "' AND" + " page_version = " + version;

		Connection conn = null;
		PreparedStatement stmt = null;
		log.debug(s_Query);
		try {
			conn = this.getCeNConnection();
			stmt = conn.prepareStatement(s_Query);
			
			String xmlMetadata = "";
			
			Statement stmt1 = conn.createStatement();
			ResultSet rs = stmt1.executeQuery("select xml_metadata from cen_pages WHERE site_code = '" + siteCode + "' AND " + " notebook = '" + nbRef.getNbNumber() + "' AND" + " experiment = '" + nbRef.getNbPage() + "' AND" + " page_version = " + version);
			if (rs.next())
				xmlMetadata = rs.getString("XML_METADATA");
			stmt1.close();	
			
			xmlMetadata = CeNXMLParser.updateXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Ussi_Key", ussiVal);
			xmlMetadata = CeNXMLParser.updateXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Completion_Date", completionDateTSStr);
						
			Blob b = conn.createBlob();
			b.setBytes(1, pdfContent);
			
			stmt.setBlob(1, b);
			stmt.setString(2, xmlMetadata);
			stmt.executeUpdate();
			
			b.free();
			
			log.debug("NotbookDAO.storeExperimentPDF().Sql executed. PDF size:" + pdfContent.length + " for NB page"
					+ nbRef.getNotebookRef());
			log.debug("updateNotebookPage().exit");
		} catch (Exception error) {
			log.error(error.getStackTrace());
			throw new DAOException(error);
		} finally {
			this.cleanUp(conn, stmt, null);
		}
	}
}
