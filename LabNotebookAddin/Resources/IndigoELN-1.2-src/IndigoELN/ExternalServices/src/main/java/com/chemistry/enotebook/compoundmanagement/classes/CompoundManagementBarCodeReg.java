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
package com.chemistry.enotebook.compoundmanagement.classes;

import java.io.Serializable;

public class CompoundManagementBarCodeReg implements Serializable {

	private static final long serialVersionUID = -3784018109181447185L;

	private String type;
	private String prefix;
	private String siteCode;

	public CompoundManagementBarCodeReg(String type, String prefix,
			String siteCode) {
		this.type = type;
		this.prefix = prefix;
		this.siteCode = siteCode;
	}

	public CompoundManagementBarCodeReg() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
}
