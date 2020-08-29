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
package com.chemistry.enotebook.domain.batch;

import com.chemistry.enotebook.domain.CeNAbstractModel;

public class BatchSolubilitySolventModel extends CeNAbstractModel {
	
	public static final long serialVersionUID = 7526472295622776147L;
	
	private String codeAndName;
	private String operator;
	private double solubilityValue;
	private String solubilityUnit;
	private String qualiString;
	private String comments;
	private boolean qualitative;
	private boolean quantitative;
//	Flag to identify the order of addition
	private int additionOrder = 0;

	public BatchSolubilitySolventModel() {
		codeAndName = "";
		operator = "";
		solubilityValue = 0.0;
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
			BatchSolubilitySolventModel sourceInstance = (BatchSolubilitySolventModel) source;
			codeAndName = sourceInstance.codeAndName;
			operator = sourceInstance.operator;
			comments = sourceInstance.comments;
			solubilityValue = sourceInstance.solubilityValue;

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
		BatchSolubilitySolventModel target = new BatchSolubilitySolventModel();
		target.codeAndName = this.codeAndName;
		target.operator = this.operator;
		target.comments = this.comments;
		target.solubilityValue = this.solubilityValue;
		target.solubilityUnit = this.solubilityUnit;
		target.qualiString = this.qualiString;
		target.codeAndName = this.codeAndName;
		target.qualitative = this.qualitative;
		target.quantitative = this.quantitative;
		return target;
	}
	
	
	public String toXML()
	{
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<Code_And_Name>"+ this.codeAndName+"</Code_And_Name>"); 
		xmlbuff.append("<Comments>"+ this.comments +"</Comments>"); 
		xmlbuff.append("<Operator>"+ this.operator +"</Operator>"); 
		xmlbuff.append("<Quali_String>"+ this.qualiString +"</Quali_String>"); 
		xmlbuff.append("<Qualitative>"+ this.qualitative+"</Qualitative>");  
		xmlbuff.append("<Quantitative>"+ this.quantitative +"</Quantitative>");  
		xmlbuff.append("<Solubility_Unit>"+ this.solubilityUnit + "</Solubility_Unit>");  
		xmlbuff.append("<Solubility_Upper_Value>" + this.solubilityValue+ "</Solubility_Upper_Value>");  
		xmlbuff.append("<Solubility_Value>"+ this.solubilityValue +"</Solubility_Value>");  
		return xmlbuff.toString();
	}

	public int getAdditionOrder() {
		return additionOrder;
	}

	public void setAdditionOrder(int additionOrder) {
		this.additionOrder = additionOrder;
	}
	
	
}
