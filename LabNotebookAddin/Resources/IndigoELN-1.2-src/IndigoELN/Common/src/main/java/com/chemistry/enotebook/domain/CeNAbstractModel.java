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

public abstract class CeNAbstractModel implements java.io.Serializable {

	// GUID generated key ( Also Key in Database if highlevel object)
	// This key value is important for an object that represents a CeN table record
	// Ex: NotebookPageModel,ReactionStepModel,ReactionSchemeModel,BatchModel,ParentCompoundModel
	protected String key = "";

	// If you dont explicitly define a serialVersionUID, the language requires that the VM generate one,
	// using some function of all field and method names in the class.Some compilers add synthetic methods
	// that can cause different values for same class compiled with different compilers.
	// So every class should have explicit definition of serialVersionUID
	public static final long serialVersionUID = 7526472295622776147L; 
	// Classes used as it is as they are pure POJOs in 1.1
	// BatchNumber
	// BatchType
	// UnitType
	// NotebookRef
	// ReactionType

	// Every POJO should implement this method to release memory
	/*
	 * public void dispose() throws Throwable{ finalize(); }
	 */
	
	//Boolean flag to identify if a model has changed.Model itself is responsible 
	//for changing the flag when data of interest has changed and that needs to be persisited in DB
	public boolean modelChanged = false;
	
    //Flag to indicate if the object is loaded from DB or new obj in memory
	private boolean isLoadedFromDB;
	
	private boolean isSetToDelete = false;
	
	//Flag indicates if the model is in the process of loading.
	private boolean isLoadingFromDB = false;
	
	//Flag to inidcate if Cloning of object is taking place.
	//this is used to deep clone without triggering calc which would otherwise in setXXXAmount()-> recalcAmounts()
	private boolean isBeingCloned = false;

	protected void setKey(String newkey) {
		this.key = newkey;
	}

	public String getKey() {
		return key;
	}

	/**
	 * Boolean flag to identify if a model has changed.  Model itself is responsible 
	 * for changing the flag when data of interest has changed and that needs to be persisited in DB
	 * 
	 * @return
	 */
	public boolean isModelChanged() {
		return modelChanged;
	}

	/**
	 * Set the Boolean flag to identify if a model has changed.  Model itself is responsible 
	 * for changing the flag when data of interest has changed and that needs to be persisited in DB
	 * @param hasChanged
	 */
	public void setModelChanged(boolean hasChanged) {
		this.modelChanged = hasChanged;
	}
	
	
	public boolean isSetToDelete() {
		return isSetToDelete;
	}

	public void setToDelete(boolean delete) {
		this.isSetToDelete = delete;
		this.modelChanged = true;
	}
	
	// This method returns XML data as string. This xml data is stored in CeN database.
	public abstract String toXML();
	
	public void resetModelChanged()
	{
		modelChanged = !modelChanged;
	}
	
	public boolean isLoadedFromDB() {
		return isLoadedFromDB;
	}

	public void setLoadedFromDB(boolean isLoadedFromDB) {
		this.isLoadedFromDB = isLoadedFromDB;
		//if not loaded from DB then always model needs to be persisted.
		//So we can set modified flag as true
		if(!isLoadedFromDB)
		{
			modelChanged = true;	
		}
	}
	
	public void resetLoadedFromDB()
	{
		isLoadedFromDB = !isLoadedFromDB;
	}
	
	//This was added for 1.1 compatabiity
	public void setModified(boolean bol)
	{
		setModelChanged(bol);
	}

	/**
	 * @return the isLoadingFromDB
	 */
	public boolean isLoadingFromDB() {
		return isLoadingFromDB;
	}

	/**
	 * @param isLoadingFromDB the isLoadingFromDB to set
	 */
	public void setLoadingFromDB(boolean isLoadingFromDB) {
		this.isLoadingFromDB = isLoadingFromDB;
	}

	public boolean isBeingCloned() {
		return isBeingCloned;
	}

	public void setBeingCloned(boolean isCloned) {
		this.isBeingCloned = isCloned;
	}
	
}

