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
/*
 * Created on Aug 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.valueobject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class StructureParamsVO 
	implements Serializable
{
	static final long serialVersionUID = 8028995095941131003L;	
	
	private String srchOperator;
	private String srchOptionValue;
	private String dbType;
	private String molDefn;
	private boolean returnAsXML = true;
	private ArrayList fldListArrayList;
	private ArrayList dbsArrayList;
	
	
	public StructureParamsVO(){
		srchOperator = "";
		srchOptionValue = "";
		dbType = "";
		molDefn = "";
		returnAsXML = true;
		fldListArrayList = new ArrayList();
		dbsArrayList = new ArrayList();
	}
	
	public String getSrchOperator(){
		return this.srchOperator;
	}
	
	public String getSrchOptionValue(){
		return this.srchOptionValue;
	}
	
	public String getDbType(){
		return this.dbType;
	}
	
	public String getMolDefn(){
		return this.molDefn;
	}
	
	public ArrayList getFldListArrayList(){
		return this.fldListArrayList;
	}
	
	public ArrayList getDbsArrayList(){
		return this.dbsArrayList;
	}
	
	public String[] getFldList(){
		
		return (String[])(this.fldListArrayList.toArray(new String[fldListArrayList.size()]));
	}
	
	public String[] getDbsList(){
		return (String[])(this.dbsArrayList.toArray(new String[dbsArrayList.size()]));
	}
	
	
	
	public void setSrchOperator( String sOperator){
		this.srchOperator = sOperator;
	}
	
	public void setSrchOptionValue(String sOptionValue){
		this.srchOptionValue = sOptionValue;
	}
	
	public void setDbType( String dType){
		this.dbType = dType;
	}
	
	public void setMolDefn(String molFile){
		this.molDefn = molFile;
	}
	
	public void setFldListArrayList(ArrayList fldArrayList){
		this.fldListArrayList = fldArrayList;
	}
	
	public void setDbsArrayList(ArrayList dArrayList){
		this.dbsArrayList = dArrayList;
	}

}
