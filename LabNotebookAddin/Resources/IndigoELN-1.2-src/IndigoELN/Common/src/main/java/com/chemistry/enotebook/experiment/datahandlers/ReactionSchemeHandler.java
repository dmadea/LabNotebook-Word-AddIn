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
 * Created on 10-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.reaction.*;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ReactionSchemeHandler extends NotebookHandler {
	private static final String ROOT_NODE = "Reaction_Properties";
	private static final String ROOT_XPATH = "/Reaction_Properties";

	private static ReactionSchemeHandler _instance = null;

	private ReactionSchemeHandler() {
	}

	public static ReactionSchemeHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collisions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new ReactionSchemeHandler();
	}

	public synchronized boolean processReactionSchemes(RowSet rs, NotebookPage nbPage, boolean blnLoading) throws SQLException,
			Exception {
		boolean result = false;
		setReactionSchemePropertyMappings();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		if (rs != null && nbPage != null) {
			rs.beforeFirst();
			while (rs.next()) {
				ReactionScheme newReactionScheme = null;
				try {
					XMLDataHandler xmlHelper = new XMLDataHandler(rs.getString(XML_METADATA));

					String reactionType = rs.getString("REACTION_TYPE");
					newReactionScheme = ReactionSchemeFactory.getReactionScheme(reactionType);
					// get the key for this record
					String rsKey = rs.getString("RXN_SCHEME_KEY");
					newReactionScheme.setKey(rsKey);
					// Find RectionStep for this Reaction
					ReactionStep step = null;
					ReactionCache reactionCache = nbPage.getReactionCache();
					Iterator it = reactionCache.iterator();
					synchronized (reactionCache) {
						while (it.hasNext()) {
							List steps = ((Reaction) (reactionCache.get((String) it.next()))).getRxnSteps();
							for (Iterator it2 = steps.iterator(); it2.hasNext();) {
								step = (ReactionStep) it2.next();
								if (step.getReactionScheme().getKey().equals(rsKey)) {
									step.setLoading(blnLoading);
									// does scheme already exist for this step
									if (step.getReactionScheme() == null) {
										step.setReactionScheme(newReactionScheme);
									} else {
										newReactionScheme = step.getReactionScheme();
									}
									step.setLoading(false);
									break;
								}
							}
						}
					}

					// set the flag to indicate reaction scheme is being loaded from the
					// database to enable modified behaviour in object set methods
					newReactionScheme.setLoading(blnLoading);
					newReactionScheme.setExistsInDB(true);
					newReactionScheme.setCachedLocally(false);

					// Populate ReactionScheme properties from table fields
					getRowsetData(rs, newReactionScheme, blnLoading);

					// now populate from XML_DATA
					getDataFromXML(newReactionScheme, xmlHelper, null, ROOT_XPATH, blnLoading);

					// reaction scheme object has finished loading
					newReactionScheme.setLoading(false);
				} catch (Exception e) {
					throw new Exception("Unable to read the reaction schemes from DB", e);
				}
			}
			result = true;
		}
		return result;
	}

	public synchronized boolean updateReactionSchemes(RowSet rs, ReactionCache rxnCache, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setReactionSchemePropertyMappings();
		ArrayList dbFields = null;
		ArrayList currentSchemes = new ArrayList();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		// System.out.println("Update ReactionSchemes in Progress");

		try {
			rs.beforeFirst();
			while (rs.next()) {
				String strSchemeKey = rs.getString("RXN_SCHEME_KEY");
				if (strSchemeKey != null) {
					// look for this scheme in the ReactionCache
					if (rxnCache.hasRxnScheme(strSchemeKey)) {
						// get the reaction scheme for this reaction
						ReactionScheme rxnScheme = (ReactionScheme) rxnCache.getRxnSchemeWithKey(strSchemeKey);
						currentSchemes.add(strSchemeKey);
						if (!nbPage.getModifedObjects().contains(rxnScheme)) continue;		// If not modified, ignore

						if (rxnScheme.isModified() && !rxnScheme.isDeleted()) {
							dbFields = updateRowsetData(rs, rxnScheme, false);

							// create the XML part of the database record
							XMLDataHandler xmlHelper = new XMLDataHandler("");
							xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

							// add the reaction scheme type attribute
							xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", rxnScheme.getReactionType().toString());
							createXMLFromModel(rxnScheme, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
							rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
							rs.updateRow();
						} else if (rxnScheme.isDeleted()) {
							rs.deleteRow();
						}
					} else {
						// this must be deleted so remove from rowset
						rs.deleteRow();
					}
				} else {
					// this must be deleted so remove from rowset
					rs.deleteRow();
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to update the reaction schemes in DB", e);
		}

		try {
			// check for new reaction schemes
			Iterator rxnIterator = rxnCache.getMapCopy().keySet().iterator();
			while (rxnIterator.hasNext()) {
				String strReactionKey = (String) rxnIterator.next();

				List steps = ((Reaction) rxnCache.getReactions().get(strReactionKey)).getRxnSteps();
				for (Iterator stepIterator = steps.iterator(); stepIterator.hasNext();) {
					ReactionStep step = (ReactionStep) stepIterator.next();
					ReactionScheme rxnScheme = step.getReactionScheme();

					// look for this reaction in the list of reactions already dealt with
					if (!currentSchemes.contains(rxnScheme.getKey())) {
						rs.beforeFirst();

						// insert new record
						rs.moveToInsertRow();
						dbFields = updateRowsetData(rs, rxnScheme, true);
						rs.next();

						// create the XML part of the database record
						XMLDataHandler xmlHelper = new XMLDataHandler("");
						xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

						// add the reaction scheme type attribute
						xmlHelper.createNewAttribute(ROOT_NODE, "Type", rxnScheme.getReactionType().toString());
						createXMLFromModel(rxnScheme, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
						rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());

						// add the page key
						rs.updateString("PAGE_KEY", nbPage.getKey());
						rs.updateRow();
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to create the reaction scheme in DB", e);
		}

		return result;
	}

	private void setReactionSchemePropertyMappings() {

		if (propertyMappings.isEmpty()) {
			propertyMappings.put("RXN_SCHEME_KEY", "key");
			propertyMappings.put("RXN_SKETCH", "searchSketch");
			propertyMappings.put("NATIVE_RXN_SKETCH", "nativeSketch");
			propertyMappings.put("SKETCH_IMAGE", "viewSketch");
			propertyMappings.put("REACTION_TYPE", "reactionType");
		}

	}
}
