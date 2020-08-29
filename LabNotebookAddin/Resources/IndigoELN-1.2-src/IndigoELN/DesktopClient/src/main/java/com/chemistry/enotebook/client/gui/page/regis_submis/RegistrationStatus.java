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
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import java.util.Date;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class RegistrationStatus {
	public RegistrationStatus() {
		notebookRef = new String();
		compoundNumber = new String();
		batchNumber = new String();
		compoundParent = new String();
		registrationDate = new Date();
	}

	private String notebookRef;
	private String compoundNumber;
	private String batchNumber;
	private String compoundParent;
	private Date registrationDate;

	/**
	 * @return Returns the batchNumber.
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * @param batchNumber
	 *            The batchNumber to set.
	 */
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * @return Returns the compoundNumber.
	 */
	public String getCompoundNumber() {
		return compoundNumber;
	}

	/**
	 * @param compoundNumber
	 *            The compoundNumber to set.
	 */
	public void setCompoundNumber(String compoundNumber) {
		this.compoundNumber = compoundNumber;
	}

	/**
	 * @return Returns the compoundParent.
	 */
	public String getCompoundParent() {
		return compoundParent;
	}

	/**
	 * @param compoundParent
	 *            The compoundParent to set.
	 */
	public void setCompoundParent(String compoundParent) {
		this.compoundParent = compoundParent;
	}

	/**
	 * @return Returns the notebookRef.
	 */
	public String getNotebookRef() {
		return notebookRef;
	}

	/**
	 * @param notebookRef
	 *            The notebookRef to set.
	 */
	public void setNotebookRef(String notebookRef) {
		this.notebookRef = notebookRef;
	}

	/**
	 * @return Returns the registrationDate.
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate
	 *            The registrationDate to set.
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
}
