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
package com.chemistry.enotebook.experiment.datamodel.attachments;

/*
 * Created on 10-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AttachmentCache extends ObservableObject implements Observer, DeepCopy, DeepClone {
	
	private static final long serialVersionUID = -8957423545349203000L;
	
	private Map attachmentList = null;
	private Map deletedList = Collections.synchronizedMap(new LinkedHashMap());

	public AttachmentCache() {
		attachmentList = Collections.synchronizedMap(new LinkedHashMap());
	}

	public void dispose() throws Throwable {
		removeAll();
		attachmentList = null;
		// deleted list will not have observers.
		if (deletedList != null)
			deletedList.clear();
		deletedList = null;
	}

	public boolean hasAttachment(String attachmentKey) {
		// checks to see if analysis exists in map
		return attachmentList.containsKey(attachmentKey);
	}

	public boolean hasAttachment(Attachment attachment) {
		// checks to see if attachment exists in map
		return hasAttachment(attachment.getKey());
	}

	public Attachment createAttachment(String name, String description, String fileName) {
		Attachment doc = new Attachment();
		doc.setDocumentName(name);
		doc.setDocumentDescription(description);
		if (fileName.substring(0, 7).equals("http://")) {
			doc.setType(".html");
			doc.setAttachmentIcon();
			doc.setDateModified((new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")).format(new Date()));
			doc.setOriginalFileName(fileName);
		} else {
			try {
				File attachedFile = new File(fileName);
				if (attachedFile.exists()) {
					doc.setIcon(Attachment.getIcon(attachedFile));
					// doc.saveFileIcon((ImageIcon) jfc.getIcon(attachedFile));
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(attachedFile));
					int bytes = (int) attachedFile.length();
					byte[] buffer = new byte[bytes];
					int readBytes = bis.read(buffer);
					doc.setContents(buffer);
					doc.setDateModified((new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss"))
							.format(new Date(attachedFile.lastModified())));
					doc.setSize(Integer.toString((int) attachedFile.length() / 1000) + " KB");
					doc.setOriginalFileName(attachedFile.getPath());
					doc.setType(attachedFile.getName().substring(attachedFile.getName().lastIndexOf(".")));
				}
			} catch (Exception e) {
				// We don't do this if the file doesn't exist
			}
		}
		attachmentList.put(doc.getKey(), doc);
		doc.addObserver(this);
		setChanged();
		notifyObservers(this);
		return doc;
	}

	public void addAttachment(Attachment attachment) {
		attachment.addObserver(this);
		attachment.setAttachmentIcon();
		attachmentList.put(attachment.getKey(), attachment);
	}

	/**
	 * 
	 * @param guid
	 * @return null if guid is not found, Attachment if found
	 */
	public Attachment getAttachment(String attachmentKey) {
		return (Attachment) attachmentList.get(attachmentKey);
	}

	/*
	 * We were lacking a method that returns a LinkedHashMap with the list of the current attachments in cash
	 * 
	 * @returns LinkedHashMap
	 */

	public Map getAttachmentMap() {
		return attachmentList;
	}

	public Attachment getAttachment(int rownum) {
		Attachment obj = null;
		Iterator attachmentIterator = attachmentList.keySet().iterator();
		int count = 0;
		while (count <= rownum && attachmentIterator.hasNext()) {
			obj = (Attachment) attachmentList.get(attachmentIterator.next());
			if (!obj.isDeleted())
				count++;
		}
		if (obj != null)
			return obj;
		else
			return null;
	}

	/**
	 * 
	 * 
	 * @param batch -
	 *            Attachment object to remove
	 * @return boolean - true if successful, false otherwise.
	 */
	public boolean deleteAttachment(Attachment doc) {
		boolean result = false;
		Attachment deleted = null;
		deleted = (Attachment) attachmentList.remove(doc.getKey());
		if (deleted != null) {
			deleted.setDeletedFlag(true);
			deleted.setModified(true);
			deleted.deleteObserver(this);
			deletedList.put(deleted.getKey(), deleted);
			result = true;
		}
		return result;
	}

	public Iterator iterator() {
		return attachmentList.keySet().iterator();
	}

	public HashMap getMapCopy() {
		return (HashMap) (new HashMap(attachmentList)).clone();
	}

	public Map getDeletedEntries() {
		return deletedList;
	}

	public void update(Observable arg0, Object arg1) {
		setChanged();
		notifyObservers(arg1);
	}

	public int size() {
		return attachmentList.size();
	}

	private void removeAll() {

		if (attachmentList != null) {
			// Need to remove observers from each of the attachments before closing.
			try {
				for (Iterator i = attachmentList.keySet().iterator(); i.hasNext();) {
					Attachment att;
					try {
						synchronized (attachmentList) {
							att = (Attachment) attachmentList.get(i.next());
							att.dispose();
						}
					} catch (ClassCastException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				attachmentList.clear();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	// 
	// DeepCopy Interface
	//
	public void deepCopy(Object resource) {
		if (resource instanceof AttachmentCache) {
			AttachmentCache srcCache = (AttachmentCache) resource;
			removeAll();
			for (Iterator i = srcCache.iterator(); i.hasNext();) {
				addAttachment((Attachment) ((Attachment) srcCache.getAttachment((String) i.next())).deepClone());
			}
		}
	}

	public Object deepClone() {
		AttachmentCache newCache = new AttachmentCache();
		newCache.deepCopy(this);
		return newCache;
	}
}
