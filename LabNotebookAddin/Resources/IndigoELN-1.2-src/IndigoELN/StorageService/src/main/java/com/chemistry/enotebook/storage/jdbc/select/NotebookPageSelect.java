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
package com.chemistry.enotebook.storage.jdbc.select;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class NotebookPageSelect extends AbstractSelect {
	private static final Log log = LogFactory.getLog(NotebookPageSelect.class);

	public NotebookPageSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z");

		NotebookRef pageRef = new NotebookRef();
		int ver = 1;
		try {
			pageRef.setNbNumber(rs.getString("NOTEBOOK"));
			pageRef.setNbPage(rs.getString("EXPERIMENT"));
			ver = rs.getInt("PAGE_VERSION");
			pageRef.setVersion(ver);
		} catch (Exception err) {
			err.printStackTrace();
			return new NotebookPageModel();
		}

		String xmlMetadata = rs.getString("XML_METADATA");
		
		String continuedFromRxn = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Continued_From_Rxn");
		String continuedToRxn = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Continued_To_Rxn");
		String projectAlias = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Project_Alias");
		String conceptionKeywords = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/ConceptionKeyWords");
		String conceptorNames = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/ConceptorNames");
		String plateLotNumber = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Plate_Lot_Number");
		String ussiKey = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Ussi_Key");
		String signatureUrl = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Signature_Url");
		String completionDate = CeNXMLParser.getXmlProperty(xmlMetadata, "/Page_Properties/Meta_Data/Completion_Date");
		
		NotebookPageModel page = new NotebookPageModel(pageRef,rs.getString("PAGE_KEY"));
		page.setNbRef(pageRef);
		page.setStatus(rs.getString("PAGE_STATUS"));
		page.setCenVersion(ver+"");
		page.setSiteCode(rs.getString("SITE_CODE"));
		page.setUserName(rs.getString("USERNAME"));
		page.setProjectCode(rs.getString("PROJECT_CODE"));
		page.setTaCode(rs.getString("TA_CODE"));
		page.setPageType(rs.getString("LOOK_N_FEEL"));
		page.setCreationDateAsTimestamp(rs.getTimestamp("CREATION_DATE"));
		page.setSubject(rs.getString("SUBJECT"));
		page.setSeriesID(rs.getString("SERIES_ID"));
		page.setProtocolID(rs.getString("PROTOCOL_ID"));
		page.setLiteratureRef(rs.getString("LITERATURE_REF"));
		page.setBatchOwner(rs.getString("BATCH_OWNER"));
		page.setBatchCreator(rs.getString("BATCH_CREATOR"));
		page.setDesignSubmitter(rs.getString("DESIGN_SUBMITTER"));
		page.setProjectAlias(projectAlias);
		if(StringUtils.isNotBlank(completionDate)) {
			try {
				page.setCompletionDateAsTimestamp(new Timestamp(df.parse(completionDate).getTime()));
			} catch (Throwable e) {
				log.debug("Problem retrieving completion date: " + completionDate + " for pageRef: " + pageRef + " no completion date was set.", e);
				// we want to give an effort to update completion date.  If we fail.  Don't fill it in.
			}
		} // else completion date is null - expected behavior
		page.setContinuedFromRxn(continuedFromRxn);
		page.setContinuedToRxn(continuedToRxn);
		page.setConceptionKeyWords(conceptionKeywords);
		page.setConceptorNames(conceptorNames);
		page.setPlateLotNum(CommonUtils.toInteger(plateLotNumber));
		page.setProcedure(rs.getString("PROCEDURE"));
		page.setLatestVersion(rs.getString("LATEST_VERSION"));
		page.setUssiKey(ussiKey);
		page.setSignatureUrl(signatureUrl);
		page.getPageHeader().setVersion(ver);
		page.setLoadedFromDB(true);
		page.setModelChanged(false);
		return page;
	}
}
