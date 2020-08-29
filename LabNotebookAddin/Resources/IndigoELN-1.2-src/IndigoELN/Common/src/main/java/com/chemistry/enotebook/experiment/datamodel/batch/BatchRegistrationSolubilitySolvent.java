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

/**
 * 
 * @deprecated - use com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel instead 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchRegistrationSolubilitySolvent extends ObservableObject implements DeepClone, DeepCopy {
	
	private static final long serialVersionUID = 1157662982553669842L;
	
	private String codeAndName;
	private String operator;
	private double solubilityValue;
	private String solubilityUnit;
	private String qualiString;
	private String comments;
	private boolean qualitative;
	private boolean quantitative;
	private double solubilityUpperValue;

	public BatchRegistrationSolubilitySolvent() {
		codeAndName = "";
		operator = "";
		solubilityValue = 0.0;
		solubilityUpperValue = 0.0;
		solubilityUnit = "";
		qualiString = "";
		comments = "";
		qualitative = false;
		quantitative = false;
	}

	/**
	 * @return Returns the Qualitative.
	 */
	public boolean isQualitative() {
		return qualitative;
	}

	/**
	 * @return Returns the Qualitative.
	 */
	public boolean getQualitative() {
		return qualitative;
	}

	/**
	 * @param isQualitative
	 *            The Qualitative to set.
	 */
	public void setQualitative(boolean isQualit) {
		if (qualitative != isQualit) {
			qualitative = isQualit;
			setModified(true);
		}
	}

	/**
	 * @return Returns the Quantitative.
	 */
	public boolean isQuantitative() {
		return quantitative;
	}

	/**
	 * @return Returns the Quantitative.
	 */
	public boolean getQuantitative() {
		return quantitative;
	}

	/**
	 * @param isQuantitative
	 *            The Quantitative to set.
	 */
	public void setQuantitative(boolean isQuanti) {
		if (quantitative != isQuanti) {
			quantitative = isQuanti;
			setModified(true);
		}
	}

	/**
	 * @return Returns the operator.
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            The operator to set.
	 */
	public void setOperator(String operator) {
		if (!this.operator.equals(operator)) {
			this.operator = operator;
			setModified(true);
		}
	}

	/**
	 * @return Returns the solubilityUnit.
	 */
	public String getSolubilityUnit() {
		return solubilityUnit;
	}

	/**
	 * @param solubilityUnit
	 *            The solubilityUnit to set.
	 */
	public void setSolubilityUnit(String solubilityUnit) {
		if (!this.solubilityUnit.equals(solubilityUnit)) {
			this.solubilityUnit = solubilityUnit;
			setModified(true);
		}
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
	 * @return Returns the solubilityValue.
	 */
	public double getSolubilityValue() {
		return solubilityValue;
	}

	/**
	 * @param solubilityValue
	 *            The solubilityValue to set.
	 */
	public void setSolubilityValue(double solubilityValue) {
		if (this.solubilityValue != solubilityValue) {
			this.solubilityValue = solubilityValue;
			setModified(true);
		}
	}

	/**
	 * @return Returns the solubilityUpperValue.
	 */
	public double getSolubilityUpperValue() {
		return solubilityUpperValue;
	}
	/**
	 * @param solubilityValue The solubilityUpperValue to set.
	 */
	public void setSolubilityUpperValue(double solubilityUpperValue) {
		if (this.solubilityUpperValue != solubilityUpperValue) {
			this.solubilityUpperValue = solubilityUpperValue;
			setModified(true);
		}
	}

	/**
	 * @return Returns the qualiString.
	 */
	public String getQualiString() {
		return qualiString;
	}

	/**
	 * @param qualiString
	 *            The qualiString to set.
	 */
	public void setQualiString(String qualiString) {
		if (!this.qualiString.equals(qualiString)) {
			this.qualiString = qualiString;
			setModified(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepCopy#deepCopy(java.lang.Object)
	 */
	public void deepCopy(Object source) {
		if (source != null) {
			BatchRegistrationSolubilitySolvent sourceInstance = (BatchRegistrationSolubilitySolvent) source;
			codeAndName = sourceInstance.codeAndName;
			operator = sourceInstance.operator;
			comments = sourceInstance.comments;
			solubilityValue = sourceInstance.solubilityValue;
	    	solubilityUpperValue = sourceInstance.solubilityUpperValue;

			solubilityUnit = sourceInstance.solubilityUnit;
			qualiString = sourceInstance.qualiString;
			qualitative = sourceInstance.qualitative;
			quantitative = sourceInstance.quantitative;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.experiment.common.interfaces.DeepClone#deepClone()
	 */
	public Object deepClone() {
		BatchRegistrationSolubilitySolvent target = new BatchRegistrationSolubilitySolvent();
		target.codeAndName = this.codeAndName;
		target.operator = this.operator;
		target.comments = this.comments;
		target.solubilityValue = this.solubilityValue;
    	target.solubilityUpperValue = this.solubilityUpperValue;
		target.solubilityUnit = this.solubilityUnit;
		target.qualiString = this.qualiString;
		target.codeAndName = this.codeAndName;
		target.qualitative = this.qualitative;
		target.quantitative = this.quantitative;
		return target;
	}
}
