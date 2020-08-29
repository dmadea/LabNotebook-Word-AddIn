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
 * SaltForm.java
 * 
 * Created on Nov 23, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.common;

import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @date Nov 23, 2004
 * 
 * Purpose: simple data holding object to free datamodel from having knowledge of a service to provide molwgt values for a
 * particular code.
 */
public class SaltForm extends GenericCode {
	
	private static final long serialVersionUID = -1851196528602080298L;
	
	private static final Log log = LogFactory.getLog(SaltForm.class);
	private static final int HASH_PRIME = 38177;
	private static final String parent = "00";

	private String formula = "";
	private double molWgt = 0.0;

	// Constructor
	public SaltForm() {
		this(parent);
	}

	public SaltForm(String code) {
		super();
		this.code = code;
		updateValuesBasedOnCode();
	}

	/**
	 * @param code
	 * @param formula
	 * @param molWgt
	 */
	public SaltForm(String code, String description, String formula, double molWgt) {
		super(code, description);
		if (formula != null)
			this.formula = formula;
		this.molWgt = molWgt;
	}

	private void updateValuesBasedOnCode() {
		try {
			SaltCodeCache scc = SaltCodeCache.getCache();
			setDescription(scc.getDescriptionGivenCode(getCode()));
			setFormula(scc.getMolFormulaGivenCode(getCode()));
			setMolWgt(scc.getMolWtGivenCode(getCode()));
		} catch (Throwable e) {
			log.warn("Failed to fetch salt metadata from SaltCodeCache.", e);
		}
	}

	//
	// Getters/Setters
	//
	public void setCode(String newCode) {
		if (!code.equals(newCode)) {
			if (isParentCode(newCode) == false) {
				code = newCode;
				updateValuesBasedOnCode();
			} else {
				code = parent;
				description = "";
				formula = "";
				molWgt = 0.0;
			}
			setModified(true);
		}
	}

	public String getFormula() {
		return (formula != null) ? formula : "";
	}

	public void setFormula(String newFormula) {
		if (!formula.equals(newFormula)) {
			formula = newFormula;
			setModified(true);
		}
	}

	public double getMolWgt() {
		return molWgt;
	}

	public void setMolWgt(double newWgt) {
		if (!CeNNumberUtils.doubleEquals(molWgt, newWgt)) {
			molWgt = newWgt;
			setModified(true);
		}
	}

	public boolean isParentForm() {
		return (StringUtils.isBlank(getCode()) || getCode().equals(parent));
	}

	/**
	 * Compares testCode with getParentSaltCode()
	 * @param testCode
	 * @return
	 */
	public static boolean isParentCode(String testCode) {
		return (StringUtils.isBlank(testCode) || testCode.equals(parent));
	}
	
	/**
	 * 
	 * @return String = "00" or current incarnation of parent salt code
	 */
	public static String getParentSaltCode() { return parent; }

	// 
	// Implements
	//

	public void deepCopy(Object resource) {
		super.deepCopy(resource);
		if (resource instanceof SaltForm) {
			SaltForm salt = (SaltForm) resource;
			formula = salt.formula;
			molWgt = salt.molWgt;
		}
	}

	public Object deepClone() {
		return new SaltForm(code, description, formula, molWgt);
	}

	//
	// Overrides
	//

	public boolean equals(Object value) {
		boolean result = false;
		if (value instanceof SaltForm) {
			SaltForm sf = (SaltForm) value;
			result = this.hashCode() == sf.hashCode();
		}
		return result;
	}

	public int hashCode() {
		return HASH_PRIME + HASH_PRIME * (super.hashCode() * (new Double(molWgt)).hashCode());
	}
}
