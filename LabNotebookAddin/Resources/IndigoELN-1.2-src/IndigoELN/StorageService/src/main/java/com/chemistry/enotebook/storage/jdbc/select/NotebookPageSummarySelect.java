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
/**
 * 
 */
package com.chemistry.enotebook.storage.jdbc.select;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * 
 */
public class NotebookPageSummarySelect extends NotebookPageSelect {
	private static final Log log = LogFactory.getLog(NotebookPageSummarySelect.class);

	public NotebookPageSummarySelect(DataSource dataSource, String sqlQuery) {
		super(dataSource, sqlQuery);
		// declareParameter(new SqlParameter(Types.VARCHAR));
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		// System.out.println("SearchResult Map ---:"+rowNum);

		NotebookRef pageRef = new NotebookRef();
		try {
			pageRef.setNbNumber(rs.getString("NOTEBOOK"));
			pageRef.setNbPage(rs.getString("EXPERIMENT"));
			pageRef.setVersion(rs.getInt("PAGE_VERSION"));
		} catch (Exception err) {
			log.error("Failed mapping row: " + rowNum, err);
			return new NotebookPageModel();
		}

		NotebookPageModel page = new NotebookPageModel(pageRef,rs.getString("PAGE_KEY"));
		page.setNbRef(pageRef);
		
		page.setProjectCode(rs.getString("PROJECT_CODE"));
		page.setTaCode(rs.getString("TA_CODE"));
		page.setPageType(rs.getString("LOOK_N_FEEL"));
		page.setSiteCode(rs.getString("SITE_CODE"));
		page.setUserName(rs.getString("USERNAME"));
		page.setCreationDateAsTimestamp(rs.getTimestamp("CREATION_DATE"));
		page.setSubject(rs.getString("SUBJECT"));
		page.setLiteratureRef(rs.getString("LITERATURE_REF"));
		page.setStatus(rs.getString("PAGE_STATUS"));

		ReactionSchemeModel scheme = new ReactionSchemeModel();
		
		try {
			scheme.setStringSketch(rs.getBytes("RXN_SKETCH"));
			scheme.setNativeSketch(rs.getBytes("NATIVE_RXN_SKETCH"));			
		} catch (Exception e) {
			log.error("Possible problem with character encoding.", e);
		}
		
		// ReactionStepModel[] step = new ReactionStepModel[1];
		ReactionStepModel step = new ReactionStepModel();
		step.setRxnScheme(scheme);
		ArrayList<ReactionStepModel> stepList = new ArrayList<ReactionStepModel>();
		stepList.add(step);
		page.setReactionSteps(stepList);
		
		page.setLoadedFromDB(true);
		page.setModelChanged(false);
		return page;
	}
}
