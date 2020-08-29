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
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.utils.GUIDUtil;

import javax.swing.*;

public class AttachmentModel extends CeNAbstractModel {

	public static final long serialVersionUID = 7592425443234776147L;

	private String type = "";
	private String documentName = "";
	private String documentDescription = "";
	private String dateModified = "";
	private int size = -1;
	private String originalFileName = "";
	private byte[] contents = null;
	private boolean ipRelated = false;
	private transient Icon icon = null;

	public AttachmentModel(String key) {
		this.key = key;
		setModelChanged(true);
	}

	public AttachmentModel() {
		this.key = GUIDUtil.generateGUID(this);
		setModelChanged(true);
	}

	public String toXML() {

		StringBuffer xmlBuffer = new StringBuffer();
		xmlBuffer.append(CeNConstants.XML_VERSION_TAG);
		xmlBuffer.append("<Attachement_Properties>");
		xmlBuffer.append("<Meta_Data>");
		xmlBuffer.append("</Meta_Data>");
		xmlBuffer.append("</Attachement_Properties>");

		return xmlBuffer.toString();

	}

	/**
	 * @return the contents
	 */
	public byte[] getContents() {
		return contents;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(byte[] contents) {
		if (contents == null) {
			this.contents = new byte[0];
		} else {
			this.contents = contents;
		}
		this.setSize(this.contents.length);
		this.modelChanged = true;
	}

	/**
	 * @return the dateModified
	 */
	public String getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified
	 *            the dateModified to set
	 */
	public void setDateModified(String dateModified) {
		if (!this.dateModified.equals(dateModified)) {
			this.dateModified = dateModified;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the documentDescription
	 */
	public String getDocumentDescription() {
		return documentDescription;
	}

	/**
	 * @param documentDescription
	 *            the documentDescription to set
	 */
	public void setDocumentDescription(String documentDescription) {
		if(documentDescription == null) return;
		if (!this.documentDescription.equals(documentDescription)) {
			this.documentDescription = documentDescription;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the documentName
	 */
	public String getDocumentName() {

		return documentName;
	}

	/**
	 * @param documentName
	 *            the documentName to set
	 */
	public void setDocumentName(String documentName) {
		if(documentName == null) return;
		if (!this.documentName.equals(documentName)) {
			this.documentName = documentName;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the ipRelated
	 */
	public boolean isIpRelated() {
		return ipRelated;
	}

	/**
	 * @param ipRelated
	 *            the ipRelated to set
	 */
	public void setIpRelated(boolean ipRelated) {
		if (!this.ipRelated == ipRelated) {
			this.ipRelated = ipRelated;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the originalFileName
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}

	/**
	 * @param originalFileName
	 *            the originalFileName to set
	 */
	public void setOriginalFileName(String originalFileName) {
		if(originalFileName == null) return;
		if (!this.originalFileName.equals(originalFileName)) {
			this.originalFileName = originalFileName;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		if(this.contents != null)
			return this.contents.length;
		else if (size >= 0) {  // contents will be empty initially for lazy loading
		  return size;
		}
		else return 0;
		//return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		if (this.size != size) {
			this.size = size;
			this.modelChanged = true;
		}
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		if(type == null) return;
		if (!this.type.equals(type)) {
			this.type = type;
			this.modelChanged = true;
		}
	}

	public void setTempKey(String key) {
		this.key = key;
	}

	/**
	 * @return the icon
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	
	public Object deepClone()
	{
		//key should be the same
		AttachmentModel clone = new AttachmentModel(this.key);
		clone.deepCopy(this);
		return clone;		
	}
	
	public String toString() {
		return "Name: " + getDocumentName() + " Key: " + getKey() + " Contents: " + (getContents() != null ? "Yes" : "No");
	}

	public void deepCopy(AttachmentModel src) {
		setType(src.getType());
		setDocumentName(src.getDocumentName()); 
		setDocumentDescription(src.getDocumentDescription());
		setDateModified(src.getDateModified());
		setSize(src.getSize());
		setOriginalFileName(src.getOriginalFileName());
		setContents(src.getContents());
		setIpRelated(src.isIpRelated());
		setLoadedFromDB(src.isLoadedFromDB());
		setModelChanged(src.isModelChanged());
		setToDelete(src.isSetToDelete());
	}
}
