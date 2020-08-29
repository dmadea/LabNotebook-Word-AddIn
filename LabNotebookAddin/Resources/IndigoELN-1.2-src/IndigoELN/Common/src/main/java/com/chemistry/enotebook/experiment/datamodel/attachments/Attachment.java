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
package com.chemistry.enotebook.experiment.datamodel.attachments;

import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Attachment extends PersistableObject implements Observer, DeepCopy, DeepClone {
	
	private static final long serialVersionUID = 6332972017585487983L;
	
	private String type;
	private String documentName;
	private String documentDescription;
	private String dateModified;
	private String size;
	private String originalFileName;
	private byte[] contents;
	private boolean ipRelated;
	private transient Icon icon;

	/*
	 * Constructors
	 */
	public Attachment() {
		this(null, null, null, null);
	}

	public Attachment(String name, String description, String modifiedDate, String type) {
		super();
		this.documentName = name;
		this.documentDescription = description;
		this.dateModified = modifiedDate;
		this.type = type;

	    if (type != null && type.compareToIgnoreCase("PDF") > 0) ipRelated = true;
	}

	public void dispose() throws Throwable {
		super.dispose();
		icon = null;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String docName) {
		this.documentName = docName;
		setModified(true);
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String fileName) {
		this.originalFileName = fileName;
	}

	public String getDateModified() {
		return dateModified;
	}

	public void setDateModified(String date) {
		this.dateModified = date;
	}

	public String getDocumentDescription() {
		return documentDescription;
	}

	public void setDocumentDescription(String desc) {
		documentDescription = desc;
		setModified(true);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;

		if (type != null && type.toUpperCase().indexOf("PDF") > 0) ipRelated = true;
	}

	public byte[] getContents() {
		return contents;
	}

	public void setContents(byte[] fileBytes) {
		this.contents = fileBytes;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String fileSize) {
		this.size = fileSize;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public boolean isIpRelated() {
		return ipRelated;
	}

	public void setIpRelated(boolean IPFlag) {
		this.ipRelated = IPFlag;
		this.setModified(true);
	}

	/**
	 * This is an override of the ObservableObject. Don't use this to update the values in the object as it is meant only to
	 * indicate that the isModified() flag was set on either this object or one it contains.
	 */
	public void update(Observable observed) {
		super.update(observed);
	}

	public File createTemporaryFile() {
		try {
			if (getOriginalFileName() != null && getType() != null) {
				File tempFile = File.createTempFile("CeNAttach", getType());
				tempFile.deleteOnExit();
				if (tempFile.exists()) {
					FileWriter fout = new FileWriter(tempFile);
					FileOutputStream fos = new FileOutputStream(tempFile);
					fos.write(getContents());
					fos.close();
					fout.close();
				}
				return tempFile;
			} else
				return null;
		} catch (IOException ioe) {
			// log error;
			return null;
		}
	}

	public void setAttachmentIcon() {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("icon", getType());

			// get the icon
			Icon fileIcon = null;
			boolean blnFoundIcon = false;
			while (!blnFoundIcon) {
				File iconFile = new File(tempFile.getCanonicalPath());
				fileIcon = getIcon(iconFile);
				// //System.out.println("File Path : "+tempFile.getCanonicalPath());
				if (fileIcon instanceof ImageIcon)
					blnFoundIcon = true;
			}
			setIcon(fileIcon);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

	public void launchFile(File retrievedFile, String strUrl) throws AttachmentLaunchException {
		if (retrievedFile != null || strUrl != null) {
			try {
				Desktop.getDesktop().browse(retrievedFile.toURI());
			} catch (Exception e) {
				throw new AttachmentLaunchException(e);
			}
		}
	}

	// 
	// DeepCopy Interface
	//

	public void deepCopy(Object resource) {
		if (resource instanceof Attachment) {
			Attachment src = (Attachment) resource;
			type = src.type;
			documentName = src.documentName;
			documentDescription = src.documentDescription;
			dateModified = src.dateModified;
			size = src.size;
			originalFileName = src.originalFileName;
			contents = (byte[]) src.contents.clone();
			ipRelated = src.ipRelated;
			icon = src.icon;
		}
	}

	public Object deepClone() {
		Attachment newAttachment = new Attachment();
		newAttachment.deepCopy(this);
		return newAttachment;
	}

	/*
	 * Gets the O.S file system view using FileSystemView class. and searches for the corresponding icon for specified file Obj.
	 */

	public static Icon getIcon(File fileName) {
		FileSystemView view = FileSystemView.getFileSystemView();
		return view.getSystemIcon(fileName);
	}
}
