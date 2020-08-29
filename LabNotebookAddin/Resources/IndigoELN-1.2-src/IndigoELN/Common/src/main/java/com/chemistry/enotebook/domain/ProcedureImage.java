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

public class ProcedureImage implements java.io.Serializable {

	private static final long serialVersionUID = -5083817185628268883L;
	
	public static final String STORED = "STORED";
	public static final String NEW = "NEW";
	public static final String DELETED = "DELETED";

	private String key = null;
	private String imageType = null;
	private byte[] imageData = null;
	private String storeState = null;
	private transient String localPath = null;

	public ProcedureImage(String key, String imageType) {
		this(key, null, imageType, STORED);
	}

	public ProcedureImage(String key, String localPath, String imageType, String storeState) {
		this.key = key;
		this.localPath = localPath;
		this.imageType = imageType;
		this.storeState = storeState;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}

	public String getStoreState() {
		return storeState;
	}
}
