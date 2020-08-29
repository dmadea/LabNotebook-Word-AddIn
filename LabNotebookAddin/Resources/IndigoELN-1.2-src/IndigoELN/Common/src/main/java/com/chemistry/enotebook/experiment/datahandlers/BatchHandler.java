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

import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchCache;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchFactory;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @date Aug 16, 2004
 */
public class BatchHandler extends NotebookHandler {

	private static final String ROOT_NODE = "Batch_Properties";
	private static final String ROOT_XPATH = "/Batch_Properties";

	private static BatchHandler _instance = null;

	private BatchHandler() {
		setBatchPropertyMappings();
	}

	public static BatchHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collissions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new BatchHandler();
	}

	public synchronized boolean processBatches(RowSet rs, NotebookPage nbPage, boolean blnLoading) throws SQLException, Exception {
		boolean result = false;
		setBatchPropertyMappings();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		if (rs != null && nbPage != null) {
			BatchCache bc = nbPage.getBatchCache();
			rs.beforeFirst();
			while (rs.next()) {
				try {
					XMLDataHandler xmlHelper = new XMLDataHandler(rs.getString(XML_METADATA));

					String batchType = xmlHelper.getAttributeValue("/Batch_Properties", 0, "Type");
					AbstractBatch newBatch = BatchFactory.getBatch(BatchTypeFactory.getBatchType(batchType), true);
					// get the key for this record
					String batchKey = rs.getString("BATCH_KEY");
					newBatch.setKey(batchKey);
					if (bc.hasBatch(newBatch)) {
						newBatch = bc.getBatch(batchKey);
					} else {
						bc.put(newBatch);
					}
					// AbstractBatch newBatch = bc.createBatch(BatchTypeFactory.getBatchType(batchType));

					// set the flag to indicate batch is being loaded from the
					// database to enable modified behaviour in object set methods
					newBatch.setLoading(true);
					newBatch.setExistsInDB(true);
					newBatch.setCachedLocally(false);

					// Populate batch properties from table fields
					getRowsetData(rs, newBatch, blnLoading);

					// now populate from XML_DATA
					getDataFromXML(newBatch, xmlHelper, null, ROOT_XPATH, blnLoading);

					// batch object has finished loading
					newBatch.setLoading(false);

				} catch (Exception e) {
					throw new Exception("Unable to read the batches from DB", e);
				}
			}

			// Run through batchCache again replacing ProductBatch/IntendedBatches with actual intended batches.
			// As I understand the load process referenced objects are instantiated with an
			// zero parameter constructor then the property stored (in this case the key) will
			// be set via the public getter/setter methods. Hence no referenced batch should
			// exist in the batch cache and so will be empty except where it references the proper
			// batch's key.
			// Removed due to redesign of technique in updating Theo amounts: ajk via conv. with Jeremy Edwards Feb. 2005
			// List li = bc.getBatches(BatchType.ACTUAL_PRODUCT);
			// for (int i = 0; i < li.size(); i++) {
			// ProductBatch apb = (ProductBatch)li.get(i);
			// if (apb.getIntendedBatch() != null) {
			// String batchKey = apb.getIntendedBatch().getKey();
			//			        
			// // take the key and match it to an object in the batch cache.
			// // Invariant: batchKey will not be an emtpy string nor will it be
			// // null when the intended batch object is not null.
			// // Invariant: batchKey will be to a ProductBatch.
			// // Set IntendedBatch = null if no corresponding batch exists in batchCache
			// // or set IntendedBatch to the proper object if it does exist, discarding
			// // the previous (empty) batch of the same key.
			// apb.setLoading(true);
			// apb.setIntendedBatch((ProductBatch)bc.getBatch(batchKey));
			// apb.setLoading(false);
			// }
			// }

			result = true;
		}
		return result;
	}

	public synchronized boolean updateBatches(RowSet rsBatches, RowSet rsStructures, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setBatchPropertyMappings();
		ArrayList dbFields = null;
		ArrayList currentBatches = new ArrayList();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		// //System.out.println("Update Batches in Progress");
		try {
			rsBatches.beforeFirst();
			while (rsBatches.next()) {
				String strBatchKey = rsBatches.getString("BATCH_KEY");
				if (strBatchKey != null && nbPage.getBatchCache() != null) {
					// look for this batch in the BatchList
					BatchCache bc = nbPage.getBatchCache();
					if (bc.getMap().containsKey(strBatchKey)) {
						AbstractBatch batchEntry = (AbstractBatch) bc.getMap().get(strBatchKey);
						currentBatches.add(strBatchKey);
						if (!nbPage.getModifedObjects().contains(batchEntry)) continue;		// If not modified, ignore

						// Need to process this only if modified
						if (batchEntry.isModified() && !batchEntry.isDeleted()) {
							dbFields = updateRowsetData(rsBatches, batchEntry, false);

							// create the XML part of the database record
							XMLDataHandler xmlHelper = new XMLDataHandler("");
							// --xmlHelper.createNewElement(metaDataRoot, "");
							xmlHelper.createNewXMLElement(metaDataRoot, "", false);

							// add the batch type attribute
							xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", batchEntry.getType().toString());
							createXMLFromModel(batchEntry, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
							rsBatches.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
							// --rsBatches.updateString("XML_METADATA", xmlHelper.getXMLString());
							rsBatches.updateRow();
						} else if (batchEntry.isDeleted()) {
							rsBatches.deleteRow();
						}
					} else {
						// this must be deleted so remove from rowset
						rsBatches.deleteRow();
					}
				} else {
					// this must be deleted so remove from rowset
					rsBatches.deleteRow();
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to update the Batch in DB", e);
		}

		try {
			// check for new batches
			BatchCache bc = nbPage.getBatchCache();
			Iterator batchIterator = bc.getMapCopy().keySet().iterator();
			while (batchIterator.hasNext()) {
				String strBatchKey = (String) batchIterator.next();

				// look for this batch number in the list of batches already dealt with
				if (!currentBatches.contains(strBatchKey)) {
					AbstractBatch batchEntry = (AbstractBatch) bc.getMap().get(strBatchKey);
					rsBatches.beforeFirst();

					// insert new record
					rsBatches.moveToInsertRow();
					dbFields = updateRowsetData(rsBatches, batchEntry, true);
					rsBatches.next();

					// create the XML part of the database record
					XMLDataHandler xmlHelper = new XMLDataHandler("");
					xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

					// add the batch type attribute
					xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", batchEntry.getType().toString());
					createXMLFromModel(batchEntry, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
					rsBatches.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());

					// add the PAGE_KEY
					rsBatches.updateString("PAGE_KEY", nbPage.getKey());
					rsBatches.updateRow();

					// //add the structure for this new batch
					// if (batchEntry.getCompound().isCreatedByNotebook()) {
					// StructureHandler.addNewStructure(rsStructures, batchEntry.getCompound(),nbPage.getKey());
					// setBatchPropertyMappings();
					// }
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to update/insert the batch record", e);
		}

		return result;
	}

	private void setBatchPropertyMappings() {

		if (propertyMappings.isEmpty()) {
			propertyMappings.put("BATCH_KEY", "key");
			propertyMappings.put("STRUCT_KEY", "compound/key");
			propertyMappings.put("BATCH_NUMBER", "batchNumber/batchNumber");
			propertyMappings.put("Intended_Batch_Key", "intendedBatch/key");
			propertyMappings.put("Parent_Batch_Num", "parentBatchNumber");
			propertyMappings.put("Conversational_Batch_Num", "conversationalBatchNumber");

			propertyMappings.put("intendedBatch/key", "");
		}
	}
}
