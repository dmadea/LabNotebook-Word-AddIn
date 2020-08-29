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
 * Created on 29-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.experiment.datamodel.analytical.AbstractAnalysis;
import com.chemistry.enotebook.experiment.datamodel.analytical.Analysis;
import com.chemistry.enotebook.experiment.datamodel.analytical.AnalysisCache;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AnalysisHandler extends NotebookHandler {
	private static final String ROOT_NODE = "Analysis_Properties";
	private static final String ROOT_XPATH = "/Analysis_Properties";

	private static AnalysisHandler _instance = null;

	private AnalysisHandler() {
	}

	public static AnalysisHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collissions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new AnalysisHandler();
	}

	public synchronized boolean processAnalysis(RowSet rs, NotebookPage nbPage, boolean blnLoading) throws SQLException, Exception {
		boolean result = false;
		setAnalysisPropertyMappings();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		if (rs != null) {
			rs.beforeFirst();
			while (rs.next()) {
				// get the key for this record
				String analysisKey = rs.getString("ANALYSIS_KEY");
				// see if this analysis is already in the analysisCache
				AnalysisCache analyses = nbPage.getAnalysisCache();
				AbstractAnalysis analysis;
				if (analyses.hasAnalysis(analysisKey)) {
					analysis = analyses.getAnalysis(analysisKey);
				} else {
					analysis = new Analysis();
					analysis.setKey(analysisKey);
					// add this analysis to the analysisCache
					analyses.put(analysis);
				}
				try {
					XMLDataHandler xmlHelper = new XMLDataHandler(rs.getString(XML_METADATA));

					String analysisFileID = xmlHelper.getAttributeValue(ROOT_XPATH, 0, "cyberLabFileId");
					analysis.setCyberLabFileId(analysisFileID);

					// set the flag to indicate analysis is being loaded from the
					// database to enable modified behaviour in object set methods
					analysis.setLoading(blnLoading);
					analysis.setExistsInDB(true);
					analysis.setCachedLocally(false);

					// Populate Analysis properties from table fields
					getRowsetData(rs, analysis, blnLoading);

					// now populate from XML_DATA
					getDataFromXML(analysis, xmlHelper, null, ROOT_XPATH, blnLoading);

					/*
					 * String batchKey = newAnalysis.getBatchKey(); //get the batch if there is a batch key if (batchKey != null && !
					 * batchKey.trim().equals("")) { AbstractBatch batch = nbPage.getBatchCache().getBatch(batchKey); }
					 */

					// compound object has finished loading
					analysis.setLoading(false);
				} catch (Exception e) {
					throw new Exception("Unable to read the structures from DB", e);
				}
			}
			result = true;
		}
		return result;
	}

	public synchronized boolean updateAnalysis(RowSet rs, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setAnalysisPropertyMappings();
		ArrayList dbFields = null;
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		ArrayList currentAnalyses = new ArrayList();
		// //System.out.println("Update Analysis in Progress");

		try {
			rs.beforeFirst();
			while (rs.next()) {
				String strAnalysisKey = rs.getString("ANALYSIS_KEY");
				if (strAnalysisKey != null) {
					// get the matching Analysis
					if (nbPage.getAnalysisCache().hasAnalysis(strAnalysisKey)) {
						AbstractAnalysis analysisEntry = (AbstractAnalysis) nbPage.getAnalysisCache().get(strAnalysisKey);
						currentAnalyses.add(strAnalysisKey);
						if (!nbPage.getModifedObjects().contains(analysisEntry)) continue;		// If not modified, ignore

						if (analysisEntry.isModified() && !analysisEntry.isDeleted()) {
							dbFields = updateRowsetData(rs, analysisEntry, false);
							// create the XML part of the database record
							XMLDataHandler xmlHelper = new XMLDataHandler("");
							xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

							// add the analysis type attribute
							xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", analysisEntry.getInstrumentType().toString());
							createXMLFromModel(analysisEntry, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
							rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
							rs.updateRow();
						} else if (analysisEntry.isDeleted()) {
							rs.deleteRow();
						}
					} else {
						// this must have been deleted so remove from rowset
						rs.deleteRow();
					}
				} else {
					// this must be deleted so remove from rowset
					rs.deleteRow();
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to update the analysis entry in DB", e);
		}
		try {
			// check for new analyses
			Iterator analysisIterator = nbPage.getAnalysisCache().getMapCopy().keySet().iterator();
			while (analysisIterator.hasNext()) {
				String strAnalysisKey = (String) analysisIterator.next();

				// look for this analysis in the list of analyses already dealt with
				if (!currentAnalyses.contains(strAnalysisKey)) {
					AbstractAnalysis analysisEntry = (AbstractAnalysis) nbPage.getAnalysisCache().get(strAnalysisKey);
					addNewAnalysis(rs, analysisEntry, nbPage.getKey());
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to update the analysis entry in DB", e);
		}
		return result;
	}

	public boolean addNewAnalysis(RowSet rs, AbstractAnalysis analysisEntry, String pageKey) throws Exception {
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		setAnalysisPropertyMappings();
		try {
			rs.beforeFirst();

			// insert new record
			rs.moveToInsertRow();
			ArrayList dbFields = updateRowsetData(rs, analysisEntry, true);
			rs.next();

			// create the XML part of the database record
			XMLDataHandler xmlHelper = new XMLDataHandler("");
			xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

			// add the analysis type attribute
			xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", analysisEntry.getInstrumentType().toString());
			createXMLFromModel(analysisEntry, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
			rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());

			// add the PAGE_KEY
			rs.updateString("PAGE_KEY", pageKey);
			rs.updateRow();
		} catch (Exception e) {
			throw new Exception("Unable to create the analysis in DB\n" + e.getMessage(), e);
		}

		return true;
	}

	private void setAnalysisPropertyMappings() {
		if (propertyMappings.isEmpty()) {
			propertyMappings.put("ANALYSIS_KEY", "key");
			propertyMappings.put("BATCH_KEY", "batchKey");
		}

	}
}
