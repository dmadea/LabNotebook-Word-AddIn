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
package com.chemistry.enotebook.domain;


public class CROChemistInfo extends CeNAbstractModel{

	private static final long serialVersionUID = -4686314098695932368L;
	
	private String croChemistID="";
	private String croChemistDisplayName="";
	/**
	 * @return the croChemistDisplayName
	 */
	public String getCroChemistDisplayName() {
		return croChemistDisplayName;
	}
	/**
	 * @param croChemistDisplayName the croChemistDisplayName to set
	 */
	public void setCroChemistDisplayName(String croChemistDisplayName) {
		this.croChemistDisplayName = croChemistDisplayName;
	}
	/**
	 * @return the croChemistID
	 */
	public String getCroChemistID() {
		return croChemistID;
	}
	/**
	 * @param croChemistID the croChemistID to set
	 */
	public void setCroChemistID(String croChemistID) {
		this.croChemistID = croChemistID;
	}
	
	
	public String toXML(){
		return 	" CRO CHEMIST ID="+this.getCroChemistID()+
		"; CRO CHEMIST DISPLAY NAME="+this.getCroChemistDisplayName();
		
	}
	
	public String toString(){
		return croChemistDisplayName;
	}
	
}
