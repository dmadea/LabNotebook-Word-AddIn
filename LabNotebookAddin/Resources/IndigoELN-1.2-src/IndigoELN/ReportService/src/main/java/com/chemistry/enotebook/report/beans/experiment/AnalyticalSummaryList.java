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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnalyticalSummaryList {
	
	private String myname = "AnalyticalSummaryList";
	private List<AnalyticalSummary> alist = new ArrayList<AnalyticalSummary>();
	public String getMyname() {
		return myname;
	}
	public void setMyname(String myname) {
		this.myname = myname;
	}
	public List<AnalyticalSummary> getList() {
		return alist;
	}
	public void setList(List<AnalyticalSummary> alist) {
		this.alist = alist;
	}
	public void add(AnalyticalSummary asummary) {
		alist.add(asummary);
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<" + myname + ">");
		buff.append("<analyticalSummaries>");
		for (Iterator<AnalyticalSummary> it = alist.iterator(); it.hasNext();) {
			buff.append(it.next().toXml());
		}
		buff.append("</analyticalSummaries>");
		buff.append("</" + myname + ">");
		return buff.toString();
	}
	

}
