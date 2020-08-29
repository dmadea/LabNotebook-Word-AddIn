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


public class GenericCodeModel extends CeNAbstractModel {

	private static final long serialVersionUID = -3999094756405895275L;
	
	protected String genericCode = "";
	protected String genericDescription = "";

	public GenericCodeModel(String code, String desc) {
		this.genericCode = code;
		this.genericDescription = desc;
	}

	public GenericCodeModel(String code) {
		this(code, "");
	}

	public GenericCodeModel() {
		this("", "");
	}

	public String getCode() {
		return genericCode;
	}

	public void setCode(String code) {
		if (genericCode == null)
			genericCode = "";
		else
			this.genericCode = code;
	}

	public String getDescription() {
		return genericDescription;
	}

	public void setDescription(String description) {
		if (description == null)
			this.genericDescription = "";
		else
			this.genericDescription = description;
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<Code>");
		xmlbuff.append(this.getCode());
		xmlbuff.append("</Code>");
		xmlbuff.append("<Description>");
		xmlbuff.append(this.getDescription());
		xmlbuff.append("</Description>");

		return xmlbuff.toString();
	}
}
