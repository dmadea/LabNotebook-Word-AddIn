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
package com.chemistry.enotebook.publisher.classes;

import java.io.Serializable;

/**
 * Model class to publish structure info
 */
public class PublishEntityInfo implements Serializable {

	private static final long serialVersionUID = 7526472245322776147L;

	public static final String PUBLISHER_KEY_SPERATOR = "_";

	// (PK will be in NBKRef Ver(8-4-2)+KeySep(2)+key(upto 256) format
	private String key = "";
	// Mol format/encode chime,chime formats of the structure
	private String stringStructure = "";
	// REACTION or BATCH flag
	private String structureType = null;

	// Monomer ID ,VCR Id ,VRXN ID etc
	private String clsId = "";
	// In case of structure capture the SIC
	private String sicCode = "";
	private byte[] nativeStructure = null;
	private boolean isInsert;
	private boolean isDelete = false;

	public PublishEntityInfo() {

	}

	public PublishEntityInfo(String key, String stringstructure,
			String structureType) {
		this();
		this.key = key;
		this.stringStructure = stringstructure;
		this.structureType = structureType;
	}

	public PublishEntityInfo(String key, byte[] nativestructure,
			String structureType) {
		this();
		this.key = key;
		this.nativeStructure = nativestructure;
		this.structureType = structureType;
	}

	public PublishEntityInfo(String key, byte[] natstructure,
			String structureType, String molStructure, String id) {
		this(key, natstructure, structureType);
		this.stringStructure = molStructure;
		this.clsId = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String cusKey) {
		this.key = cusKey;
	}

	public String getStructureType() {
		return structureType;
	}

	public void setStructureType(String structureType) {
		this.structureType = structureType;
	}

	public String getNotebookRef() {
		int i = key.indexOf(PUBLISHER_KEY_SPERATOR);
		if (i > 0) {
			return key.substring(0, i);
		}
		return "";
	}

	public String getClsId() {
		return clsId;
	}

	public void setClsId(String clsId) {
		this.clsId = clsId;
	}

	public String getSicCode() {
		return sicCode;
	}

	public void setSicCode(String sicCode) {
		this.sicCode = sicCode;
	}

	public boolean isInsert() {
		return isInsert;
	}

	public void setInsert(boolean isInsert) {
		this.isInsert = isInsert;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getStringStructure() {
		return stringStructure;
	}

	public void setStringStructure(String cusStringStructure) {
		this.stringStructure = cusStringStructure;
	}

	public byte[] getNativeStructure() {
		return nativeStructure;
	}

	public void setNativeStructure(byte[] nativeStructure) {
		this.nativeStructure = nativeStructure;
	}

}
