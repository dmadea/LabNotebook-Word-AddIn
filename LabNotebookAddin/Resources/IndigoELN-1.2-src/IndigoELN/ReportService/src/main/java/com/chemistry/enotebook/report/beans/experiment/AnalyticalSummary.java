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

import java.util.HashMap;
import java.util.Map;

public class AnalyticalSummary {
	
	private String notebookBatchNumber = "";
	private String comments = "";
	private Map<String, String> instrumentTypeCounts = new HashMap<String, String>();
	private String myname = "AnalyticalSummary";
	
	public String getNotebookBatchNumber() {
		return notebookBatchNumber;
	}
	public void setNotebookBatchNumber(String notebookBatchNumber) {
		this.notebookBatchNumber = notebookBatchNumber;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Map<String, String> getInstrumentTypeCounts() {
		return instrumentTypeCounts;
	}
	public void setInstrumentTypeCounts(Map<String, String> instrumentTypeCounts) {
		this.instrumentTypeCounts = instrumentTypeCounts;
	}
	public void addInstrumentType(String instrumentType) {
		String count = "0";
		if (this.instrumentTypeCounts.containsKey(instrumentType)) {
			count = this.instrumentTypeCounts.get(instrumentType);
		}
		int i = Integer.parseInt(count);
		count = "" + ++i;
		this.instrumentTypeCounts.put(instrumentType, count);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<" + myname + ">");
		buff.append("<notebookBatchNumber>").append(this.getNotebookBatchNumber()).append("</notebookBatchNumber>");
		buff.append("<comments>").append(this.getComments()).append("</comments>");
		buff.append("<instrumentTypeCounts>");
        int lcms = 0;
        int nmr = 0;
        int other = 0;
		for (String instrumentType : instrumentTypeCounts.keySet()) {
			String count = instrumentTypeCounts.get(instrumentType);
            int v = Integer.parseInt(count);
            if ("LC-MS".equalsIgnoreCase(instrumentType)) {
                lcms += v;
            } else if ("NMR".equalsIgnoreCase(instrumentType)) {
                nmr += v;
            } else {
                other += v;
            }

			buff.append("<instrument>");
			buff.append("<notebookBatchNumber>").append(this.getNotebookBatchNumber()).append("</notebookBatchNumber>");
			buff.append("<instrType>");
			buff.append(instrumentType);
			buff.append("</instrType>");
			buff.append("<count>");
			buff.append(count);
			buff.append("</count>");
			buff.append("</instrument>");
		}
		buff.append("</instrumentTypeCounts>");
        buff.append("<lcms>" + lcms + "</lcms>");
        buff.append("<nmr>" + nmr + "</nmr>");
        buff.append("<other>" + other + "</other>");
		buff.append("</" + myname + ">");
		return buff.toString();
	}
}
