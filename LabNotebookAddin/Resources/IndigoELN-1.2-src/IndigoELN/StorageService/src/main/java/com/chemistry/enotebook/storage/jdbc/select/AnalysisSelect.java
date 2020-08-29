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

import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.utils.CommonUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalysisSelect extends AbstractSelect {

	public AnalysisSelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

		AnalysisModel analysis = new AnalysisModel(rs.getString("ANALYSIS_KEY"));//ANALYSIS_KEY
		
		analysis.setAnnotation(rs.getString("ANNOTATION"));//ANNOTATION
		analysis.setFile(rs.getBytes("BLOB_DATA"));
		analysis.setCenSampleRef(rs.getString("CEN_SAMPLE_REF"));//CEN_SAMPLE_REF
		analysis.setComments(rs.getString("COMMENTS"));//COMMENTS
		analysis.setCyberLabDomainId(rs.getString("CYBER_LAB_DOMAIN_ID"));//CYBER_LAB_DOMAIN_ID
		analysis.setCyberLabFileId(rs.getString("CYBER_LAB_FILE_ID"));//CYBER_LAB_FILE_ID
		analysis.setCyberLabFolderId(rs.getString("CYBER_LAB_FOLDER_ID"));//CYBER_LAB_FOLDER_ID
		analysis.setCyberLabLCDFId(rs.getString("CYBER_LAB_LCDF_ID"));//CYBER_LAB_LCDFID
		analysis.setCyberLabUserId(rs.getString("CYBER_LAB_USER_ID"));//CYBER_LAB_USER_ID
		analysis.setDomain(rs.getString("DOMAIN"));//DOMAIN
		analysis.setExperiment(rs.getString("EXPERIMENT"));//EXPERIMENT
		analysis.setExperimentTime(rs.getString("EXPERIMENT_TIME"));//EXPERIMENT_TIME
		analysis.setFileName(rs.getString("FILE_NAME"));//FILE_NAME
		analysis.setFileSize(CommonUtils.toLong(rs.getString("FILE_SIZE")));//FILE_SIZE
		analysis.setFileType(rs.getString("FILE_TYPE"));//FILE_TYPE
		analysis.setAnalyticalServiceSampleRef(rs.getString("ANALYTICAL_SERVICE_SAMPLE_REF"));//ANALYTICAL_SERVICE_SAMPLE_REF
		analysis.setGroupId(rs.getString("GROUP_ID"));//GROUP_ID
		analysis.setInstrument(rs.getString("INSTRUMENT"));//INSTRUMENT
		analysis.setInstrumentType(rs.getString("INSTRUMENT_TYPE"));//INSTRUMENT_TYPE
		analysis.setServer(rs.getString("SERVER"));//SERVER
		analysis.setSite(rs.getString("SITE_CODE"));//SITE
		analysis.setSiteCode(rs.getString("SITE_CODE"));//SITE_CODE
		analysis.setUrl(rs.getString("URL"));//URL
		analysis.setUserId(rs.getString("USER_ID"));//USER_ID
		analysis.setVersion(CommonUtils.toLong(rs.getString("ANALYTICAL_VERSION")));//VERSION
		analysis.setIPRelated(CommonUtils.toBooleanFromChar(rs.getString("IP_RELATED")));//IP_RELATED
		analysis.setLinked(CommonUtils.toBooleanFromChar(rs.getString("IS_LINKED")));
		analysis.setLoadedFromDB(true);
		analysis.setModelChanged(false);
		
		return analysis;
	}
}
