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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ScreenSubmittalSet {
    private static final Log log = LogFactory.getLog(ScreenSubmittalSet.class);
    
	private String screenCode = "";
	private String screenName = "";
	private String site = "";
	private String recipient = "";
	private String compoundAggregationScreenPanelKey = "";
	private List<String> batches = new ArrayList<String>();
	
	public String getScreenCode() {
		return screenCode;
	}
	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getCompoundAggregationScreenPanelKey() {
		return compoundAggregationScreenPanelKey;
	}
	public void setCompoundAggregationScreenPanelKey(String compoundAggregationScreenPanelKey) {
		this.compoundAggregationScreenPanelKey = compoundAggregationScreenPanelKey;
	}
	public void addBatch(String batchId) {
		this.batches.add(batchId);
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<screenSubmittalSet>");
		try {
			Class c = Class.forName(this.getClass().getName());
			Object me = this;
			Method[] allMethods = c.getDeclaredMethods();
			for (int i=0; i<allMethods.length; i++) {
				Method method = allMethods[i];
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					String fieldName = methodName.substring(3);
					if (fieldName.equalsIgnoreCase("batches")
							|| fieldName.equalsIgnoreCase("purificationServiceSubmissions"))
						continue;
					buff.append("<").append(fieldName).append(">");
					try {
						buff.append((method.invoke(me, (Object[])null)).toString());
					} catch (RuntimeException e) {
						buff.append("");
					}
					buff.append("</").append(fieldName).append(">");
				}
			}
			buff.append("<batches>");
			int count = 0;
			for (String batchId : batches) {
				if (count > 0)
					buff.append(", ");
				buff.append(batchId);
				if (++count > 10) {
					buff.append("\n");
					count = 0;
				}
			}
			buff.append("</batches>");
		} catch (Exception e) {
			log.error("Failed to translate batches to screen submital set XML", e);
		}
		buff.append("</screenSubmittalSet>");
		log.debug(buff.toString());
		return buff.toString();
		
	}
	
	public String toString() {
		return this.toXml();
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ScreenSubmittalSet sset = (ScreenSubmittalSet) obj;
		return this.toString().equals(sset.toString());
	}
	
	public int hashCode() {
		return this.toString().hashCode();
	}

}
