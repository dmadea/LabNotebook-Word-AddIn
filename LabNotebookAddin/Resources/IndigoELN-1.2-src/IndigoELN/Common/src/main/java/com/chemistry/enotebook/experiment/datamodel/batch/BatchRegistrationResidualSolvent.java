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
 * Created on Nov 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

/**
 * 
 * @deprecated - use com.chemistry.enotebook.domain.batch.BatchResidualSolventModel instead
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchRegistrationResidualSolvent extends ObservableObject implements DeepClone, DeepCopy {
	
	private static final long serialVersionUID = -5161996073849483453L;
	
	private String codeAndName;
	private double eqOfSolvent;
	private String comments;

	public BatchRegistrationResidualSolvent() {
		codeAndName = new String();
		eqOfSolvent = 0.0;
		comments = new String();
	}

	/**
	 * @return Returns the codeAndName.
	 */
	public String getCodeAndName() {
		return codeAndName;
	}

	/**
	 * @param codeAndName
	 *            The codeAndName to set.
	 */
	public void setCodeAndName(String codeAndName) {
		if (!this.codeAndName.equals(codeAndName)) {
			this.codeAndName = codeAndName;
			setModified(true);
		}
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            The comments to set.
	 */
	public void setComments(String comments) {
		if (!this.comments.equals(comments)) {
			this.comments = comments;
			setModified(true);
		}
	}

	/**
	 * @return Returns the eqOfSolvent.
	 */
	public double getEqOfSolvent() {
		return eqOfSolvent;
	}

	/**
	 * @param eqOfSolvent
	 *            The eqOfSolvent to set.
	 */
	public void setEqOfSolvent(double eqOfSolvent) {
		if (this.eqOfSolvent != eqOfSolvent) {
			this.eqOfSolvent = eqOfSolvent;
			setModified(true);
		}
	}

	public double getResidualMolWgt() {
		double result = 0.0;

		if (eqOfSolvent > 0 && codeAndName != null && codeAndName.length() > 0) {
			try {
				String val = CodeTableCache.getCache().getResidualSolventMolWgt(codeAndName);
				if (val != null && val.length() > 0)
					result = eqOfSolvent * new Double(val).doubleValue();
				else {
				}
				// //System.out.println("Error getting MolWgt for Residual Solvent '" + codeAndName + "'");
			} catch (CodeTableCacheException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public String getResidualMolFormula() {
		String result = "";

		if (eqOfSolvent > 0 && codeAndName != null && codeAndName.length() > 0) {
			try {
				result = CodeTableCache.getCache().getResidualSolventDescription(codeAndName);
			} catch (CodeTableCacheException e) {
				e.printStackTrace();
			}

			if (result != null && result.length() > 0)
				if (eqOfSolvent != 1)
					result = eqOfSolvent + "(" + result + ")";
				else
					result = codeAndName;
			else {
				// System.out.println("Error getting Description for Residual Solvent '" + codeAndName + "'");
				result = "";
			}
		}

		return result;
	}

	public String getResidualDescription() {
		String result = "";

		if (codeAndName != null && codeAndName.length() > 0) {
			try {
				result = CodeTableCache.getCache().getResidualSolventDescription(codeAndName);
			} catch (CodeTableCacheException e) {
				e.printStackTrace();
			}
			if (result != null && result.length() > 0)
				result = codeAndName;
			else
				System.out.println("Error getting Description for Residual Solvent '" + codeAndName + "'");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepCopy#deepCopy(java.lang.Object)
	 */
	public void deepCopy(Object source) {
		if (source != null) {
			BatchRegistrationResidualSolvent sourceInstance = (BatchRegistrationResidualSolvent) source;
			codeAndName = sourceInstance.codeAndName;
			eqOfSolvent = sourceInstance.eqOfSolvent;
			comments = sourceInstance.comments;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchRegistrationResidualSolvent target = new BatchRegistrationResidualSolvent();
		target.codeAndName = this.codeAndName;
		target.eqOfSolvent = this.eqOfSolvent;
		target.comments = this.comments;
		return target;
	}
}