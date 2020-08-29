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
 * Created on 10-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.experiment.datamodel.attachments.Attachment;
import com.chemistry.enotebook.experiment.datamodel.attachments.AttachmentCache;
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
public class AttachmentHandler extends NotebookHandler {
	private static final String ROOT_NODE = "Attachment_Properties";
	private static final String ROOT_XPATH = "/Attachment_Properties";

	private ArrayList currentAttachments = new ArrayList();
	private static AttachmentHandler _instance = null;

	private AttachmentHandler() {
	}

	public static AttachmentHandler getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// Form of double-checked locking to prevent collissions
	private synchronized static void createInstance() {
		if (_instance == null)
			_instance = new AttachmentHandler();
	}

	public synchronized boolean processAttachment(RowSet rs, NotebookPage nbPage, boolean blnLoading) throws SQLException,
			Exception {
		boolean result = false;
		setAttachmentPropertyMappings();
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		if (rs != null) {
			rs.beforeFirst();
			while (rs.next()) {
				// get the key for this record
				String attachKey = rs.getString("ATTACHEMENT_KEY");
				// see if this attachment is already in the attachmentCache
				AttachmentCache attachments = nbPage.getAttachmentCache();
				Attachment attachment;
				if (attachments.hasAttachment(attachKey)) {
					attachment = attachments.getAttachment(attachKey);
				} else {
					attachment = new Attachment();
					attachment.setKey(attachKey);

				}
				try {
					XMLDataHandler xmlHelper = new XMLDataHandler(rs.getString(XML_METADATA));

					/*
					 * Might need to get an attachment type from the metadata
					 * 
					 * String attachmentType = xmlHelper.getAttributeValue(ROOT_XPATH, 0, "Type");
					 */

					// set the flag to indicate analysis is being loaded from the
					// database to enable modified behaviour in object set methods
					attachment.setLoading(blnLoading);
					attachment.setExistsInDB(true);
					attachment.setCachedLocally(false);

					// Populate Analysis properties from table fields
					getRowsetData(rs, attachment, blnLoading);

					// now populate from XML_DATA
					getDataFromXML(attachment, xmlHelper, null, ROOT_XPATH, blnLoading);

					// compound object has finished loading
					attachment.setLoading(false);
					// add this attachment to the attachmentCache
					attachments.addAttachment(attachment);
				} catch (Exception e) {
					throw new Exception("Unable to read the attachments from DB", e);
				}
			}
			result = true;
		}
		return result;
	}

	public synchronized boolean updateAttachment(RowSet rs, NotebookPage nbPage) throws Exception {
		boolean result = true;
		setAttachmentPropertyMappings();
		ArrayList dbFields = null;
		metaDataRoot = ROOT_NODE + "/Meta_Data/";

		// //System.out.println("Update Attachment in Progress");

		try {
			rs.beforeFirst();
			while (rs.next()) {
				String strAttachmentKey = rs.getString("ATTACHEMENT_KEY");
				if (strAttachmentKey != null) {
					// get the matching Analysis
					if (nbPage.getAttachmentCache().hasAttachment(strAttachmentKey)) {
						Attachment attachmentEntry = (Attachment) nbPage.getAttachmentCache().getAttachment(strAttachmentKey);
						currentAttachments.add(strAttachmentKey);
						if (!nbPage.getModifedObjects().contains(attachmentEntry)) continue;		// If not modified, ignore

						if (attachmentEntry.isModified() && !attachmentEntry.isDeleted()) {
							dbFields = updateRowsetData(rs, attachmentEntry, false);
							// create the XML part of the database record
							XMLDataHandler xmlHelper = new XMLDataHandler("");
							xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

							// add the analysis type attribute
							xmlHelper.createNewXMLAttribute(ROOT_NODE, "Type", attachmentEntry.getType().toString());
							createXMLFromModel(attachmentEntry, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
							rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());
							rs.updateRow();
						} else if (attachmentEntry.isDeleted()) {
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
//		try {
//			// check for new attachments
//
//		} catch (Exception e) {
//			throw new Exception("Unable to update the analysis entry in DB", e);
//		}
		return result;
	}

	public synchronized boolean addNewAttachments(NotebookPage nbPage, RowSet rs) throws Exception {
		metaDataRoot = ROOT_NODE + "/Meta_Data/";
		setAttachmentPropertyMappings();
		try {
			Iterator attachmentIterator = nbPage.getAttachmentCache().getMapCopy().keySet().iterator();

			while (attachmentIterator.hasNext()) {
				String strAttachmentKey = (String) attachmentIterator.next();

				// look for this analysis in the list of analyses already dealt with
				if (!currentAttachments.contains(strAttachmentKey)) {
					Attachment attachmentEntry = (Attachment) nbPage.getAttachmentCache().getAttachment(strAttachmentKey);
					// insert new record
					rs.beforeFirst();
					rs.moveToInsertRow();
					ArrayList dbFields = updateRowsetData(rs, attachmentEntry, true);
					rs.next();

					// create the XML part of the database record
					XMLDataHandler xmlHelper = new XMLDataHandler("");
					xmlHelper.createNewXMLElement(ROOT_NODE + "/Meta_Data", "", false);

					// add the attachment type attribute
					// xmlHelper.createNewAttribute(ROOT_NODE, "Type", attachmentEntry.getType().toString());
					createXMLFromModel(attachmentEntry, xmlHelper, ROOT_NODE + "/Meta_Data/", null, dbFields);
					rs.updateString("XML_METADATA", xmlHelper.getXMLFormattedString());

					// add the PAGE_KEY
					rs.updateString("PAGE_KEY", nbPage.getKey());
					rs.updateRow();
				}
			}

		} catch (Exception e) {
			throw new Exception("Unable to create the attachment in DB", e);
		}

		return true;
	}

	private void setAttachmentPropertyMappings() {
		if (propertyMappings.isEmpty()) {
			propertyMappings.put("ATTACHEMENT_KEY", "key");
			propertyMappings.put("BLOB_DATA", "contents");
		}

	}
}
