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
package com.chemistry.enotebook.signature.classes;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SigDocumentVO implements Serializable {

	private static final long serialVersionUID = -2591063856278973535L;

	private byte[] document; // Byte array containing document being submitted

	private String email; // email Address for Owner of document being submitted
	private String ntUserId; // NT UserID for Owner of document being submitted
	private String documentName; // Name of document being submitted
	private int size; // Size of file being submitted in bytes
	private Date createDate; // Date document was created
	private TemplateVO template; // Template to use for Document submission

	private String key; // key of document in USSI (Filled in after submission)
	private URL url; // Launch Url (Filled in after submission)

	private Map<String, String> attributes; // Additional attributes for
											// document being submitted

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getNtUserId() {
		return ntUserId;
	}

	public void setNtUserId(String ntUserId) {
		this.ntUserId = ntUserId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TemplateVO getTemplate() {
		return template;
	}

	public void setTemplate(TemplateVO template) {
		this.template = template;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(String name, String value) {
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put(name, value);
	}
}
