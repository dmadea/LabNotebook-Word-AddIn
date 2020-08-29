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
 * Created on 13-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchCache;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class StructureHandler extends NotebookHandler {
	private static final String ROOT_NODE = "Structure_Properties";
	private static final String ROOT_XPATH = "/Structure_Properties";

	private static StructureHandler _instance = null;

	private StructureHandler() {
	}

	public static StructureHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collissions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new StructureHandler();
	}

	public synchronized boolean processStructures(RowSet rs, Map nbBatchList, boolean blnLoading) throws SQLException, Exception {
		boolean result = false;
		setStructurePropertyMappings();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		if (rs != null && nbBatchList != null) {
			rs.beforeFirst();
			while (rs.next()) {
				try {
					XMLDataHandler xmlHelper = new XMLDataHandler(rs.getString(XML_METADATA));

					// get the key for this record
					String structKey = rs.getString("STRUCT_KEY");

					Compound productStructure = null;

					// add this compound to the relevant batch
					// need to look through the batches to find the right one.
					Iterator batchListIterator = nbBatchList.keySet().iterator();

					// Find the batch to which this structure is related
					while (batchListIterator.hasNext()) {
						AbstractBatch batch = (AbstractBatch) nbBatchList.get((String) batchListIterator.next());
						if (batch.getCompound().getKey().equals(structKey)) {
							// We're loading something into the batch so set the loading flag
							batch.setLoading(blnLoading);

							productStructure = batch.getCompound();

							// batch object has finished loading
							batch.setLoading(false);

							break;
						}
					}

					if (productStructure != null) {
						// set the flag to indicate structure is being loaded from the
						// database to enable modified behaviour in object set methods
						productStructure.setLoading(blnLoading);
						productStructure.setExistsInDB(true);
						productStructure.setCachedLocally(false);

						// Populate Compound properties from table fields
						getRowsetData(rs, productStructure, blnLoading);

						// now populate from XML_DATA
						getDataFromXML(productStructure, xmlHelper, null, ROOT_XPATH, blnLoading);

						// compound object has finished loading
						productStructure.setLoading(false);
					}
				} catch (Exception e) {
					throw new Exception("Unable to read the structures from DB", e);
				}
			}
			result = true;
		}
		return result;
	}

	public synchronized boolean updateStructures(RowSet rs, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setStructurePropertyMappings();
		ArrayList dbFields = null;
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		ArrayList currentStructures = new ArrayList();
		BatchCache bc = nbPage.getBatchCache();

		try {
			rs.beforeFirst();
			while (rs.next()) {
				String strStructureKey = rs.getString("STRUCT_KEY");
				if (strStructureKey != null) {
					// get the batch for this structure
					// need to look through the batches to find the right one.
					Iterator batchListIterator = bc.getMapCopy().keySet().iterator();
					AbstractBatch batch = null;
					boolean batchFound = false;
					while (batchListIterator.hasNext()) {
						batch = (AbstractBatch) bc.getMap().get((String) batchListIterator.next());
						if (batch.getCompound().getKey() != null && batch.getCompound().getKey().equals(strStructureKey)) {
							batchFound = true;
							break;
						}
					}

					if (batchFound) {
						Compound cmpd = batch.getCompound();
						currentStructures.add(strStructureKey);
						if (!nbPage.getModifedObjects().contains(cmpd)) continue;		// If not modified, ignore

						if (cmpd.isModified() && !cmpd.isDeleted()) {
							if (cmpd.isCreatedByNotebook()) {
								// remove the exclusions for this structure
								propertyExclusions.clear();
							} else {
								// certain properties can be ignored so add to exclusions
								setPropertyExclusions();
							}
							dbFields = updateRowsetData(rs, cmpd, false);

							// create the XML part of the database record
							XMLDataHandler xmlHelper = new XMLDataHandler("");
							xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

							createXMLFromModel(cmpd, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
							rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
							rs.updateRow();
						} else if (cmpd.isDeleted()) {
							rs.deleteRow();
						}
					} else {
						// this must have changed from a product so remove from rowset
						rs.deleteRow();
					}
				} else {
					// this must be deleted so remove from rowset
					rs.deleteRow();
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to update the structure in DB", e);
		}

		try {
			// check for new structures
			Iterator batchIterator = bc.getMapCopy().keySet().iterator();
			while (batchIterator.hasNext()) {
				AbstractBatch batch = (AbstractBatch) bc.getMap().get((String) batchIterator.next());

				// look for this structure in the list of structures already dealt with
				if (!currentStructures.contains(batch.getCompound().getKey())) {
					if (batch.getCompound().isCreatedByNotebook()) {
						// remove the exclusions for this structure
						propertyExclusions.clear();
					} else {
						// certain properties can be ignored so add to exclusions
						setPropertyExclusions();
					}

					addNewStructure(rs, batch.getCompound(), nbPage.getKey());
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to create the structure in DB", e);
		}

		return result;
	}

	public boolean addNewStructure(RowSet rsStructures, Compound cmpd, String pageKey) throws Exception {
		// //System.out.println("add NewStructure in Progress");
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		setStructurePropertyMappings();
		try {
			rsStructures.beforeFirst();

			// insert new record
			rsStructures.moveToInsertRow();
			ArrayList dbFields = updateRowsetData(rsStructures, cmpd, true);
			rsStructures.next();

			// create the XML part of the database record
			XMLDataHandler xmlHelper = new XMLDataHandler("");
			xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);
			createXMLFromModel(cmpd, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
			rsStructures.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());

			// add PAGE_KEY
			rsStructures.updateString("PAGE_KEY", pageKey);
			rsStructures.updateRow();
		} catch (Exception e) {
			throw new Exception("Unable to add structure record", e);
		}
		return true;
	}

	private void setStructurePropertyMappings() {
		if (propertyMappings.isEmpty()) {
			propertyMappings.put("STRUCT_KEY", "key");
			propertyMappings.put("STRUCT_IMAGE", "viewSketch");
			propertyMappings.put("STRUCT_SKETCH", "searchSketch");
			propertyMappings.put("NATIVE_STRUCT_SKETCH", "nativeSketch");
			propertyMappings.put("CHEMICAL_NAME", "chemicalName");
			propertyMappings.put("REGISTRATION_NUMBER", "regNumber");
		}
	}

	private void setPropertyExclusions() {
		propertyExclusions.add("nativeSketch");
		propertyExclusions.add("searchSketch");
		propertyExclusions.add("viewSketch");
//		propertyExclusions.add("nativeSketchFormat");
		// propertyExclusions.add("molWgt");
		// propertyExclusions.add("molFormula");
	}
}
