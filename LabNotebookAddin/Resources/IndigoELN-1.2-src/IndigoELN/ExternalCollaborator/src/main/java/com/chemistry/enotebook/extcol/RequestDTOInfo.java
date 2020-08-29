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
package com.chemistry.enotebook.extcol;

public class RequestDTOInfo implements java.io.Serializable{
	public static final long serialVersionUID = 7592425443234776147L;
	
	public RequestDTOInfo()
	{
		
	}
	
	public RequestDTOInfo(String requestorID,long issueDate,long lastUpdateDate,long closeDate)
	{
		this.requestorID = requestorID;
		this.issueDate = issueDate;
		this.lastUpdateDate = lastUpdateDate;
		this.closeDate = closeDate;
		
	}

	private String requestorID = "";
	private long issueDate = 0;
	private long lastUpdateDate = 0;
	private long closeDate = 0;
	public long getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(long closeDate) {
		this.closeDate = closeDate;
	}
	public long getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(long issueDate) {
		this.issueDate = issueDate;
	}
	public long getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getRequestorID() {
		return requestorID;
	}
	public void setRequestorID(String requestorID) {
		this.requestorID = requestorID;
	}
	
	
	public String toString()
	{
		return "RequestorID:"+this.requestorID+" issueDate:"+this.issueDate + " lastUpdateDate:"+this.lastUpdateDate + " closeDate:"+this.closeDate;
	}
	
	
}
