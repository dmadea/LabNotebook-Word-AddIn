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
package com.chemistry.enotebook.domain.container;

public class CompoundManagementBarcodePrefixInfo implements java.io.Serializable, Comparable {
   
	public static final long serialVersionUID = 7526472295645376147L;
	
	// GLB etc
	private String prefix;
	// GTN etc
	private String siteCode;
	//PLATE, VIAL , TUBE etc
	private String type;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
/*	public String toString(){
		return "Prefix :"+this.prefix +
			"Type :"+this.type+
			"Site :"+this.siteCode;
	}*/
	public String toString(){
		return this.prefix;
	}
    public int compareTo(Object o) 
    {           
    	CompoundManagementBarcodePrefixInfo obj = (CompoundManagementBarcodePrefixInfo) o;             
    	return this.prefix.compareTo(obj.getPrefix()); 
    }   	
}
