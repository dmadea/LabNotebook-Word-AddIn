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
package com.chemistry.enotebook.session.security;

import java.io.Serializable;

public class HttpUserMessage implements Serializable, Cloneable {

	private static final long serialVersionUID = -2124241507346678947L;
	
	public static final String BATCH = "Batch";
	public static final String ERROR_BATCH = "Error Batch";
	public static final String PLATE = "Plate";
	public static final String ERROR = "Error";
	
	private String action = "";
	private String message = "";
	private String notebook = "";
	private String page = "";
	private int version = 1;
	
	// Optional parameters
	
	private String jobid = "";
	private String key = "";
	private String type = "";
	private String status = "";
	private String statusMessage = "";
	
	public HttpUserMessage clone() {
		HttpUserMessage newMsg = new HttpUserMessage();
		
		newMsg.setAction(new String(getAction()));
		newMsg.setMessage(new String(getMessage()));
		newMsg.setNotebook(new String(getNotebook()));
		newMsg.setPage(new String(getPage()));
		newMsg.setVersion(getVersion());
		newMsg.setJobid(new String(getJobid()));
		newMsg.setKey(new String(getKey()));
		newMsg.setType(new String(getType()));
		newMsg.setStatus(new String(getStatus()));
		newMsg.setStatusMessage(new String(getStatusMessage()));
		
		return newMsg;
	}
	
	/**
	 * Returns registration status
	 * @return registration status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Set registration status
	 * @param status registration status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Returns registration status message
	 * @return registration status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * Set registration status message
	 * @param statusMessage registration status message
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/**
	 * Returns batch or plate key
	 * @return batch or plate key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Set batch or plate key
	 * @param key batch or plate key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Returns type - batch or plate
	 * @return type - batch or plate
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Set type - batch or plate
	 * @param type type - batch or plate
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Returns action
	 * @return action
	 */
	public String getAction() {
		return action;
	}
		
	/**
	 * Set action
	 * @param action action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Returns message for action
	 * @return message for action
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Set message for action
	 * @param message message for action
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Returns notebook reference
	 * @return notebook reference
	 */
	public String getNotebook() {
		return notebook;
	}
	
	/**
	 * Set notebook reference
	 * @param notebook notebook reference
	 */
	public void setNotebook(String notebook) {
		this.notebook = notebook;
	}
	
	/**
	 * Returns notebook page reference (page number)
	 * @return notebook page reference (page number)
	 */
	public String getPage() {
		return page;
	}
	
	/**
	 * Set notebook page reference
	 * @param page notebook page reference
	 */
	public void setPage(String page) {
		this.page = page;
	}
	
	/**
	 * Returns version of notebook page
	 * @return version of notebook page
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Set version of notebook page
	 * @param version version of notebook page
	 */
	public void setVersion(int version) {
		this.version = version;
	}
		
	/**
	 * Returns registration job ID
	 * @return registration job ID
	 */
	public String getJobid() {
		return jobid;
	}

	/**
	 * Set registration job ID
	 * @param jobId registration job ID
	 */
	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String toString(){
		return "NotebookRef = " + this.notebook + "-" + this.page + "_" + this.version + "\n" +
				"Action	= " + this.action + "\n" +
				"Message = " + this.message;
	}
}
