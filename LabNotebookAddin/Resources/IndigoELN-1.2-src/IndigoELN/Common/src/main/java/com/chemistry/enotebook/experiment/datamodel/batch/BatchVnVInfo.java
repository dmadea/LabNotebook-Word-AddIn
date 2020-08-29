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
 * Created on May 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchVnVInfo extends ObservableObject implements DeepClone, DeepCopy {

	private static final long serialVersionUID = 4534762088948938184L;
	
	private String status;
	private String molData;
	private String errorMsg;
	private ArrayList suggestedSICList;

	public BatchVnVInfo() {
		status = "";
		molData = "";
		errorMsg = "";
		suggestedSICList = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchVnVInfo target = new BatchVnVInfo();
		target.deepCopy(this);
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepCopy#deepCopy(java.lang.Object)
	 */
	public void deepCopy(Object source) {
		if (source != null) {
			BatchVnVInfo sourceInstance = (BatchVnVInfo) source;
			status = sourceInstance.status;
			molData = sourceInstance.molData;
			errorMsg = sourceInstance.errorMsg;
			suggestedSICList = sourceInstance.suggestedSICList;
			removeAllObservablesFromList(suggestedSICList);
			addAllObservablesToList(sourceInstance.suggestedSICList, suggestedSICList);
		}

	}

	/**
	 * @return Returns the errorMsg.
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            The errorMsg to set.
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return Returns the molData.
	 */
	public String getMolData() {
		return molData;
	}

	/**
	 * @param molData
	 *            The molData to set.
	 */
	public void setMolData(String molData) {
		if (this.molData.length() == 0) {
			this.molData = "\n" + molData + "\n";
		} else if (molData.startsWith("\n")) {
			this.molData = molData;
		} else
			this.molData = "\n" + molData + "\n";

	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Returns the suggestedSICList.
	 */
	public ArrayList getSuggestedSICList() {
		return suggestedSICList;
	}

	/**
	 * @param suggestedSICList
	 *            The suggestedSICList to set.
	 */
	public void setSuggestedSICList(ArrayList sugSICList) {
		removeAllObservablesFromList(suggestedSICList);
		addAllObservablesToList(sugSICList, suggestedSICList);
		setModified(true);
	}
}
