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
package com.chemistry.enotebook.report.beans.experiment;

import com.chemistry.enotebook.report.utils.TextUtils;

public class ExperimentProcedure {
	
	String procedureText = new String();

	public String getProcedureText() {
		return procedureText;
	}
    
	public void setProcedureText(String procedureText) {
		this.procedureText = procedureText;
	}
	
	public String toXml() {
		// Substitute html codes for xml compatibility
		procedureText = TextUtils.substituteHtmlCodes(procedureText);
		StringBuffer buff = new StringBuffer("<procedureList>");
		buff.append("<procedure>");
		TextUtils.fillBufferWithClassMethods(buff, this, true);
		buff.append("</procedure>");
		buff.append("</procedureList>");
		return buff.toString();
	}

}
