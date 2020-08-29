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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateVO implements Serializable {
	
	private static final long serialVersionUID = -1929510492542630141L;
	
	private String templateId = null; // ID of Template
	private String templateName = null; // Name of Template
	private List<String> blockNames = null; // Name of each Signature Block

	public TemplateVO() {
	}

	public TemplateVO(String name) {
		templateName = name;
	}

	public String getTemplateId() {
		return templateId;
	}
	
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public String getTemplateName() {
		return (templateName != null) ? templateName : "";
	}

	public void setTemplateName(String name) {
		templateName = name;
	}

	public int getSignatureBlockCount() {
		return (blockNames != null) ? blockNames.size() : 0;
	}

	public String[] getSignatureBlockNames() {
		String[] result = null;
		if (blockNames != null && blockNames.size() > 0) {
			result = blockNames.toArray(new String[0]);
		}
		return result;
	}

	public void setSignatureBlockNames(String[] names) {
		if (names != null) {
			blockNames = Arrays.asList(names);
		} else {
			blockNames = new ArrayList<String>();
		}
	}

	public void addSignatureBlockName(String name) {
		if (blockNames == null) {
			blockNames = new ArrayList<String>();
		}
		blockNames.add(name);
	}
	
	@Override
	public String toString() {
		return getTemplateName();
	}
}
