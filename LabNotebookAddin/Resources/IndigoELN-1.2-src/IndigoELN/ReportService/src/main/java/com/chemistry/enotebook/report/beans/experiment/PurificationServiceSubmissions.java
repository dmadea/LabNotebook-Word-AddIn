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

import java.util.ArrayList;
import java.util.List;

public class PurificationServiceSubmissions {
	
	private List<String> submittedBatches = new ArrayList<String>();
	
	public void addSubmittedBatch(String batchId) {
		submittedBatches.add(batchId);
	}

	public String getSubmittedBatchesAsString() {
		StringBuffer buff = new StringBuffer();
		StringBuffer line = new StringBuffer("");
		for (String s : submittedBatches) {
			if (buff.length() > 0) {
				buff.append(", ");
				line.append(", ");
			}
			buff.append(s);
			line.append(s);
			if (line.length() > 50) {
				buff.append("\n");
				line = new StringBuffer("");
			}
		}
		return buff.toString();
	}

	public String toXml() {
		StringBuffer buff = new StringBuffer("<purificationServiceSubmissions>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</purificationServiceSubmissions>");
		return buff.toString();
	}


}
