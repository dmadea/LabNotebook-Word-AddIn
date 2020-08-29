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
 * NotebookRef.java
 * 
 * Created on Aug 6, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.page;

import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;

/**
 * 
 * @date Aug 6, 2004
 */
public class NotebookRef extends CeNAbstractModel implements DeepCopy, DeepClone {

	private static final long serialVersionUID = -5111324893100506258L;

	// Need to override equals and hashCode to get these to be
	// consistent
	private static final int HASH_PRIME = 100003;

	// NotebookRef = nbNumber + "-" + nbPage
	private String _nbNumber = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
	private String _nbPage = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
	private int _nbVersion = 1;
	
	public NotebookRef() {
		// Create an invalid NotebookRef
		_nbNumber = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
		_nbPage = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
	}

	public NotebookRef(String nbRef) throws InvalidNotebookRefException {
		setNotebookRef(nbRef);
	}

	public NotebookRef(String notebook, String experiment) throws InvalidNotebookRefException {
		setNotebookRef(notebook + "-" + experiment);
	}

	public NotebookRef(String notebook, String experiment, String pageKey) throws InvalidNotebookRefException {
		setNotebookRef(notebook + "-" + experiment);
		this.key = pageKey;
	}

	public String getNotebookRef() {
		return getNbRef();
	}

	public void setNotebookRef(String nbRef) throws InvalidNotebookRefException {
		if (nbRef != null && NotebookPageUtil.isValidNotebookRef(nbRef)) {
			String nbRefPadded = NotebookPageUtil.formatNotebookRef(nbRef);
			_nbNumber = NotebookPageUtil.getNotebookNumberFromNotebookRef(nbRef);
			_nbPage = NotebookPageUtil.getNotebookPageFromNotebookRef(nbRef);
			_nbVersion = NotebookPageUtil.getNotebookPageVersionFromNotebookRef(nbRef);
		} else {
			throw new InvalidNotebookRefException("Notebook Ref entered is invalid: '" + nbRef + "'");
		}
	}

	/**
	 * @return Returns a valid Notebook Reference if valid otherwise returns INVALID_NUMBER
	 */
	public String getNbRef() {
		// Returns INVALID_NUMBER if any part of the NotebookRef is
		// invalid. Otherwise it returns the NotebookRef
		String result = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
		if (_nbNumber.equals(result) || _nbPage.equals(result))
			return result;
		return _nbNumber + "-" + _nbPage;
	}

	/**
	 * @return Returns the nbNumber if valid otherwise returns INVALID_NUMBER
	 */
	public String getNbNumber() {
		// Returns INVALID_NUMBER if number is invalid
		// Otherwise it returns the NotebookRef
		String result = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
		if (_nbNumber.equals(result))
			return result;
		return _nbNumber;

	}

	/**
	 * @param nbNumber
	 *            The nbNumber to set.
	 */
	public void setNbNumber(String nbNumber) throws InvalidNotebookRefException {
		if (NotebookPageUtil.isValidNotebookNumber(nbNumber))
			this._nbNumber = NotebookPageUtil.formatNotebookNumber(nbNumber);
		else
			throw new InvalidNotebookRefException("Notebook Number does not meet validation criteria. Entered value was: "
					+ nbNumber);
	}

	/**
	 * @return Returns the nbPage if valid otherwise returns INVALID_NUMBER
	 */
	public String getNbPage() {
		// Returns INVALID_NUMBER if page is invalid.
		// Otherwise it returns the NotebookRef
		String result = Integer.toString(NotebookPageUtil.INVALID_NUMBER);
		if (_nbPage.equals(result))
			return result;
		return _nbPage;
	}

	/**
	 * @param nbPage
	 *            The nbPage to set.
	 */
	public void setNbPage(String nbPage) throws InvalidNotebookRefException {
		if (NotebookPageUtil.isValidNotebookPage(nbPage))
			this._nbPage = NotebookPageUtil.formatNotebookPage(nbPage);
		else
			throw new InvalidNotebookRefException("Notebook Page does not meet validation criteria. Entered value was: " + nbPage);
	}

	//
	// Override Methods toString, equals and hashCode
	//
	public String toString() {
		return getNbRef();
	}

	public void deepCopy(Object resource) {
		if (resource instanceof NotebookRef) {
			NotebookRef srcRef = (NotebookRef) resource;
			_nbNumber = srcRef._nbNumber;
			_nbPage = srcRef._nbPage;
			_nbVersion = srcRef._nbVersion;			
//			setLoadedFromDB(srcRef.isLoadedFromDB());
		}
	}

	public Object deepClone() {
		NotebookRef result = new NotebookRef();
		result.deepCopy(this);
		return result;
	}

	public boolean equals(Object rhs) {
		boolean result = false;
		if (rhs.getClass() == NotebookRef.class) {
			NotebookRef testRef = (NotebookRef) rhs;
			result = (testRef.getNbNumber() == this.getNbNumber() && testRef.getNbPage() == this.getNbPage());
		}
		return result;
	}

	public int hashCode() {
		return (HASH_PRIME * (_nbNumber.hashCode() + "-".hashCode() + _nbPage.hashCode()));
	}

	/**
	 * @return the _nbVersion
	 */
	public int getVersion() {
		return _nbVersion;
	}

	/**
	 * @param version
	 *            the _nbVersion to set
	 */
	public void setVersion(int version) {
		_nbVersion = version;
	}

	/*
	 * returns String representation of the fully qualified name of the notebook
	 * number. The values would be same as that stored in database column "NBK_REF_VERSION",  
	 * in table CEN_PAGES. 
	 * *DO - NOT - CHANGE - THE - FORMAT*
	 */
	public String getCompleteNbkNumber(){
		return this.getNbNumber()+"-"+this.getNbPage()+"-"+this.getVersion();
	}
	
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();

		return xmlbuff.toString();
	}
}
