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
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BatchResidualSolventModel extends CeNAbstractModel {

	/**
	 * should not share public version of parent class serialVersionUID
	 */
	private static final long serialVersionUID = 8526569614701290268L;


	private static final Log log = LogFactory.getLog(BatchResidualSolventModel.class);
	

	private String codeAndName;
	private double eqOfSolvent;
	private String comments;
//	Flag to identify the order of addition
	private int additionOrder = 0;
	
	public BatchResidualSolventModel() {
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

		if (eqOfSolvent > 0 && StringUtils.isNotBlank(codeAndName)) {
			try {
				String val = CodeTableCache.getCache().getResidualSolventMolWgt(codeAndName);
				if (StringUtils.isNotBlank(val)) {
					result = eqOfSolvent * new Double(val).doubleValue();
				}
			} catch (CodeTableCacheException e) {
				log.error("Failed to retrieve residual solvent molecular weight.", e);
			}
		}

		return result;
	}

	public String getResidualMolFormula() {
		String result = getResidualDescription();
		if (eqOfSolvent > 0 && eqOfSolvent != 1 && StringUtils.isNotBlank(result)) {
			result = eqOfSolvent + "(" + result + ")";
		}
		return result;
	}

	public String getResidualDescription() {
		String result = "";

		if (StringUtils.isNotBlank(codeAndName)) {
			try {
				result = CodeTableCache.getCache().getResidualSolventDescription(codeAndName);
			} catch (CodeTableCacheException e) {
				log.warn("Failed to retrieve residual solvent description for " + codeAndName + " will return: " + codeAndName, e);
			}
			if (StringUtils.isNotBlank(result))
				result = codeAndName;
			else
				log.error("Error getting Description for Residual Solvent '" + codeAndName + "'");
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
			BatchResidualSolventModel sourceInstance = (BatchResidualSolventModel) source;
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
		BatchResidualSolventModel target = new BatchResidualSolventModel();
		target.codeAndName = this.codeAndName;
		target.eqOfSolvent = this.eqOfSolvent;
		target.comments = this.comments;
		return target;
	}
	
	
	public String toXML()
	{
		StringBuffer xmlbuff = new StringBuffer();
		xmlbuff.append("<Code_And_Name>"+ this.codeAndName+"</Code_And_Name>"); 
		xmlbuff.append("<Comments>"+ this.comments +"</Comments>"); 
		xmlbuff.append("<Eq_Of_Solvent>"+ this.eqOfSolvent +"</Eq_Of_Solvent>");
		return xmlbuff.toString();
	}
	
	public int getAdditionOrder() {
		return additionOrder;
	}

	public void setAdditionOrder(int additionOrder) {
		this.additionOrder = additionOrder;
	}
	
}
