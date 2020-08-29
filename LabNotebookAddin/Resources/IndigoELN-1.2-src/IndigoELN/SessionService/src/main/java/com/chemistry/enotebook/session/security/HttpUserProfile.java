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


public class HttpUserProfile implements java.io.Serializable {
	
	public static final long serialVersionUID = 949327842571264L;
	private String hostName = "";
	private long portNumber = 65432;
	private String userId = "";
	private HttpUserMessage httpMessage = null;
	private String url = "";
	
	public HttpUserProfile(String hostName, long portNumber, String userId){
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.userId = userId;
		httpMessage = new HttpUserMessage();
	}
	
	public HttpUserProfile(String hostName, String userId){
		this.hostName = hostName;
		this.userId = userId;
		httpMessage = new HttpUserMessage();
	}
	public HttpUserProfile(String url){
		this.url= url;
		httpMessage = new HttpUserMessage();
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		if(url.equals("")){
			this.url = "http://"+this.hostName+":"+this.portNumber;
		}
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the action
	 */
	public HttpUserMessage getHttpMessage() {
		return httpMessage;
	}
	/**
	 * @param action the action to set
	 */
	public void setHttpMessage(HttpUserMessage msg) {
		this.httpMessage = msg;
	}
	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}
	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	/**
	 * @return the portNumber
	 */
	public long getPortNumber() {
		return portNumber;
	}
	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(long portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public String toString(){
		return "Host:"+ hostName+" Port:"+portNumber+ " user:"+userId; 
		
	}
	
	public String toXML(){
		return "";
	}
}
