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


import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SaltFormModel extends GenericCodeModel {

	private static final long serialVersionUID = 8174943220379293210L;

	private static final Log log = LogFactory.getLog(SaltFormModel.class);
	
	private static final String parent = "00";
	private static final String unknown = "99";
	private String formula = "";
	private double molWgt = 0.0;

	// Constructor
	public SaltFormModel() {
		this(parent);
	}

	public SaltFormModel(String code) {
		super(code);
		updateValuesBasedOnCode();
	}

	/**
	 * @param code
	 * @param formula
	 * @param molWgt
	 */
	public SaltFormModel(String code, String description, String formula, double molWgt) {
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
		if (newCode == null)
			newCode = "";
		if (genericCode.equals(newCode) == false) {
			if (isParentCode(newCode) == false) {
				//setCode(newCode);
				genericCode = newCode;
				updateValuesBasedOnCode();
			} else {
			    //setCode("00");  // vb 11/16 to avoid stack overflow
				genericCode = parent; 
				setDescription("");
				formula = "";
				molWgt = 0.0;
			}
		}
	}

	public String getFormula() {
		return (formula != null) ? formula : "";
	}

	public void setFormula(String newFormula) {
		if (formula.equals(newFormula) == false) {
			formula = newFormula;
		}
	}

	public double getMolWgt() {
		return molWgt;
	}

	public void setMolWgt(double newWgt) {
		if (CeNNumberUtils.doubleEquals(molWgt, newWgt) == false) {
			molWgt = newWgt;
		}
	}

	/**
	 * See isParentCode()
	 * @return
	 */
	public boolean isParentForm() {
		return isParentCode(getCode().trim());
	}

	public static String getParentCode() { return parent; }
	public static String getUnknownCode() { return unknown; }
	/**
	 * Extended this to treat the Parent Salt code and Unknown Salt form the same
	 * @param testCode
	 * @return
	 */
	public static boolean isParentCode(String testCode) {
		return (StringUtils.isBlank(testCode) || testCode.trim().equalsIgnoreCase(parent) || testCode.trim().equalsIgnoreCase(unknown));
	}

	public static boolean isUnknownCode(String testCode) {
		return (StringUtils.isBlank(testCode) || testCode.trim().equalsIgnoreCase(unknown));
	}
	
	public boolean equals(Object value) {
		boolean result = false;
		if (value instanceof SaltFormModel) {
			SaltFormModel sf = (SaltFormModel) value;
			result = this.getCode().equals(sf.getCode());
		}
		return result;
	}
	
	public void deepCopy(Object value) {
		if (value instanceof SaltFormModel) {
			SaltFormModel sf = (SaltFormModel) value;
			this.setCode(sf.getCode());
			this.setDescription(sf.getDescription());
			this.formula = sf.getFormula();
			this.molWgt = sf.getMolWgt();
		}
	}
	
}
