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
import com.chemistry.enotebook.experiment.datamodel.reaction.Reaction;
import com.chemistry.enotebook.experiment.datamodel.reaction.ReactionCache;
import com.chemistry.enotebook.experiment.datamodel.reaction.ReactionStep;

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
public class ReactionStepHandler extends NotebookHandler {
	private static final String ROOT_NODE = "Step_Properties";
	private static final String ROOT_XPATH = "/Step_Properties";

	private static ReactionStepHandler _instance = null;

	private ReactionStepHandler() {
	}

	public static ReactionStepHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collissions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new ReactionStepHandler();
	}

	public synchronized boolean processReactionSteps(RowSet rs, NotebookPage nbPage, boolean blnLoading) throws SQLException,
			Exception {
		ArrayList reactionStepList = new ArrayList();

		boolean result = false;
		setReactionStepPropertyMappings();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		if (rs != null && nbPage != null) {
			rs.beforeFirst();

			while (rs.next()) {
				ReactionStep newReactionStep = null;
				// get the key for this record
				String stepKey = rs.getString("STEP_KEY");
				try {
					XMLDataHandler xmlHelper = new XMLDataHandler(rs.getString(XML_METADATA));

					// add the reaction steps to the correct reaction
					Reaction reaction = null;
					ReactionCache reactionCache = nbPage.getReactionCache();
					reactionCache.setLoading(blnLoading);
					if (reactionCache.hasRxn(stepKey)) {
						reaction = reactionCache.getReaction(stepKey);
						newReactionStep = reaction.getRxnStepWithKey(stepKey);
					} else {
						reactionCache.setLoading(blnLoading);
						reaction = reactionCache.createReaction(stepKey);
					}
					if (newReactionStep == null) {
						newReactionStep = new ReactionStep();
						newReactionStep.setLoading(blnLoading);
						newReactionStep.setKey(stepKey);
						reaction.setLoading(blnLoading);
						reaction.addReactionStep(newReactionStep);
					}
					// set the flag to indicate reaction step is being loaded from the
					// database to enable modified behaviour in object set methods
					newReactionStep.setExistsInDB(true);
					newReactionStep.setCachedLocally(false);

					// Populate ReactionScheme properties from table fields
					getRowsetData(rs, newReactionStep, blnLoading);

					// now populate from XML_DATA
					getDataFromXML(newReactionStep, xmlHelper, null, ROOT_XPATH, blnLoading);

					reactionCache.setLoading(false);

					reaction.setLoading(false);

					// reaction step object has finished loading
					newReactionStep.setLoading(false);
				} catch (Exception e) {
					throw new Exception("Unable to read the reaction steps from DB", e);
				}
			}
			result = true;
		}
		return result;
	}

	public synchronized boolean updateReactionSteps(RowSet rs, ReactionCache rxnCache, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setReactionStepPropertyMappings();
		ArrayList dbFields = null;
		ArrayList currentSteps = new ArrayList();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";

		// //System.out.println("Update ReactionSteps in Progress");
		try {
			rs.beforeFirst();
			while (rs.next()) {
				String strStepKey = rs.getString("STEP_KEY");
				if (strStepKey != null) {
					// look for this step in the reaction
					ReactionStep rxnStep = (ReactionStep) rxnCache.getRxnStepWithKey(strStepKey);
					if (rxnStep != null) {
						// add step key to the arraylist of deal with steps
						currentSteps.add(strStepKey);
						if (!nbPage.getModifedObjects().contains(rxnStep)) continue;		// If not modified, ignore

						if (rxnStep.isModified() && !rxnStep.isDeleted()) {
							dbFields = updateRowsetData(rs, rxnStep, false);

							// create the XML part of the database record
							XMLDataHandler xmlHelper = new XMLDataHandler("");
							xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

							// add the batch type attribute
							xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", rxnStep.getReactionType().toString());
							createXMLFromModel(rxnStep, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
							rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
							rs.updateRow();
						} else if (rxnStep.isDeleted()) {
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
			throw new Exception("Unable to update Reaction Steps", e);
		}

		try {
			addNewSteps(rs, rxnCache, currentSteps, nbPage);
		} catch (Exception e) {
			throw new Exception("Unable to add new Reaction Steps", e);
		}

		return result;
	}

	private void addNewSteps(RowSet rs, ReactionCache rxnCache, ArrayList currentSteps, NotebookPage nbPage) throws Exception {
		ArrayList dbFields = null;
		metaDataRoot = ROOT_NODE + "/Meta_Data/";

		// System.out.println("add NewSteps in Progress");
		// need to go through each reaction looking for new steps
		Iterator reactionIterator = rxnCache.getReactions().keySet().iterator();
		while (reactionIterator.hasNext()) {
			String strRxnKey = (String) reactionIterator.next();
			Reaction rxn = (Reaction) rxnCache.getReactions().get(strRxnKey);

			// deal with each step in the reaction
			List steps = rxn.getRxnStepsCopy();
			for (int i = 0; i < steps.size(); i++) {
				ReactionStep newStep = (ReactionStep) steps.get(i);
				String strStepKey = (String) newStep.getKey();

				// check for new reaction/step combination in the list of steps already dealt with
				if (!currentSteps.contains(strStepKey)) {
					rs.beforeFirst();

					// insert new record
					rs.moveToInsertRow();
					dbFields = updateRowsetData(rs, newStep, true);
					rs.next();

					// create the XML part of the database record
					XMLDataHandler xmlHelper = new XMLDataHandler("");
					xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);
					createXMLFromModel(newStep, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
					rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());

					// add the page key
					rs.updateString("PAGE_KEY", nbPage.getKey());
					rs.updateRow();
				}
			}
		}
	}

	private void setReactionStepPropertyMappings() {

		if (propertyMappings.isEmpty()) {
			propertyMappings.put("STEP_KEY", "key");
			propertyMappings.put("RXN_SCHEME_KEY", "reactionScheme/key");
			propertyMappings.put("ReactionType", "reactionScheme/reactionType"); // INTENDED or ACTUAL reaction types.
			// How do I get the key values from each of the transactionStep objects in the transactions ArrayList?
			// propertyMappings.put("TransactionStep", "transactions/")
			propertyMappings.put("SEQ_NUM", "stepNumber");
		}

	}
}
