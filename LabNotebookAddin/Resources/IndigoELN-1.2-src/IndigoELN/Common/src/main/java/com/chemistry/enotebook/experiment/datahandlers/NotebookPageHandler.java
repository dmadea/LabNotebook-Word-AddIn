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
 * NotebookPageHandler.java
 * 
 * Created on Aug 16, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 
 * @date Aug 16, 2004
 */
public class NotebookPageHandler extends NotebookHandler {
	private static final String ROOT_NODE = "Page_Properties";
	private static final String ROOT_XPATH = "/Page_Properties";

	private static NotebookPageHandler _instance = null;

	private NotebookPageHandler() {
	}

	public static NotebookPageHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collissions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new NotebookPageHandler();
	}

	public synchronized boolean processNotebookPage(RowSet rs, NotebookPage nbPage, boolean blnLoading) throws Exception {
		boolean result = false;
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		setNotebookPagePropertyMappings();

		if (rs != null) {
			rs.beforeFirst();
			rs.next();

			// process rows then XMLdata
			try {
				nbPage.setLoading(blnLoading);
				nbPage.setExistsInDB(true);
				nbPage.setCachedLocally(false);

				// Populate reactionscheme properties from table fields
				getRowsetData(rs, nbPage, blnLoading);

				// now populate from XML_DATA
				String strXML = rs.getString(XML_METADATA);
				XMLDataHandler xmlHelper = new XMLDataHandler(strXML);
				getDataFromXML(nbPage, xmlHelper, null, ROOT_XPATH, blnLoading);

				// //System.out.println("NotebookPage--:"+nbPage);
				// keep page object loading flag set
				nbPage.setLoading(blnLoading);
			} catch (Exception e) {
				throw new Exception("Unable to read the notebook page from DB", e);
			}

			result = true;
		}
		return result;
	}

	public synchronized boolean updateNotebookPage(RowSet rs, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setNotebookPagePropertyMappings();
		ArrayList dbFields = null;
		metaDataRoot = ROOT_NODE + "/Meta_Data/";

		// System.out.println("Update Analysis in Progress");

		try {
			if (rs != null) {
				boolean blnInsert = false;

				rs.beforeFirst();
				if (rs.isLast()) {
					// new record inserted
					rs.moveToInsertRow();
					blnInsert = true;
				} else
					rs.next();

				dbFields = updateRowsetData(rs, nbPage, blnInsert);
				if (blnInsert)
					rs.next();

				// create the XML part of the database record
				XMLDataHandler xmlHelper = new XMLDataHandler("");
				xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);
				// --xmlHelper.createNewElement(ROOT_NODE + "/Meta_Data", "");
				createXMLFromModel(nbPage, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
				rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
				rs.updateRow();
			} else
				result = false;
		} catch (Exception e) {
			throw new Exception("Unable to update the notebook page", e);
		}
		return result;
	}

	private void setNotebookPagePropertyMappings() {
		if (propertyMappings.isEmpty()) {
			propertyMappings.put("PAGE_KEY", "key");
			propertyMappings.put("NOTEBOOK", "notebookRef/nbNumber");
			propertyMappings.put("EXPERIMENT", "notebookRef/nbPage");
			propertyMappings.put("USERNAME", "userNTID");
			propertyMappings.put("LOOK_N_FEEL", "laf");
			propertyMappings.put("PAGE_STATUS", "status");
			propertyMappings.put("OWNER_USERNAME", "owner");
			propertyMappings.put("MODIFIED_DATE", "modificationDate");
			propertyMappings.put("SUBJECT", "subject");
			// not final
			propertyMappings.put("COMMENTS", "synthesisPlan/comments");
			propertyMappings.put("DESCRIPTION", "synthesisPlan/description");
			propertyMappings.put("DESIGN_USERS", "synthesisPlan/designUsers");
			propertyMappings.put("SCREEN_PANELS", "synthesisPlan/screenPanels");
			propertyMappings.put("SCALE", "synthesisPlan/scale");
			propertyMappings.put("PROTOTYPE_LEAD_IDS", "synthesisPlan/prototypeLeadIds");
			propertyMappings.put("DESIGN_SITE", "synthesisPlan/designSite");
			propertyMappings.put("DESIGN_CREATION_DATE", "synthesisPlan/designCreationDate");
			propertyMappings.put("SUMMARY_PLAN_ID", "synthesisPlan/summaryPlanId");
			propertyMappings.put("VRXN_ID", "synthesisPlan/vrxnId");
			propertyMappings.put("DESIGN_SUBMITTER", "synthesisPlan/designSubmitter");
			propertyMappings.put("INTERMEDIATE_PLAN_IDS", "synthesisPlan/intermediatePlanIds");

		}
	}

	public synchronized boolean processNotebookPage(NotebookPageModel model, NotebookPage nbPage, boolean blnLoading)
			throws Exception {
		boolean result = false;

		if (model != null) {

			// process rows then XMLdata
			try {
				nbPage.setLoading(blnLoading);
				nbPage.setExistsInDB(true);
				nbPage.setCachedLocally(false);

				// Populate NotebookPage(page info,Reaction Step,Reaction Scheme ) from data in model
				// Ignoring the reflection way of populating data for performance gain
				nbPage.setNotebookRef(new NotebookRef(model.getNbRef().toString()));
				nbPage.setVersion(model.getVersion());
				nbPage.setLatestVersion(model.getLatestVersion());
				nbPage.setUserNTID(model.getUserName());
				nbPage.setSiteCode(model.getSiteCode());
				nbPage.setOwner(model.getBatchOwner());
				nbPage.setLaf(model.getPageType());
				nbPage.setStatus(model.getStatus());
				// nbPage.setPageType(model.getPageType());
				nbPage.setCenVersion(model.getCenVersion());
				nbPage.setSubject(model.getSubject());
				nbPage.setLiteratureRef(model.getLiteratureRef());
				nbPage.setTaCode(model.getTaCode());
				nbPage.setProjectCode(model.getProjectCode());
				nbPage.setCreationDate(model.getCreationDate());
				nbPage.setCompletionDate(model.getCompletionDate());
				nbPage.setModificationDate(model.getModificationDate());
				nbPage.setContinuedFromRxn(model.getContinuedFromRxn());
				nbPage.setContinuedToRxn(model.getContinuedToRxn());
				nbPage.setProjectAlias(model.getProjectAlias());
				nbPage.setProtocolID(model.getProtocolID());
				// nbPage.setSeriesID(model.getSeriesID());
				// nbPage.setBatchOwner(model.getBatchOwner());
				// nbPage.setBatchCreator(model.getBatchCreator());
				// nbPage.setConceptorNames(model.getConceptorNames());
				// nbPage.setConceptionKeyWords(model.getConceptionKeyWords());
				nbPage.setUssiKey(model.getUssiKey());
				nbPage.setArchiveDate(model.getArchiveDate());
				nbPage.setSignatureUrl(model.getSignatureUrl());
				// nbPage.setChemistUserID(model.getChemistUserID());

				// System.out.println("NotebookPage--loaded:"+nbPage.getNotebookRefAsString());
				// keep page object loading flag set
				nbPage.setLoading(blnLoading);
			} catch (Exception e) {
				throw new Exception("Unable to read the notebook page from DB", e);
			}

			result = true;
		}
		return result;
	}

}
