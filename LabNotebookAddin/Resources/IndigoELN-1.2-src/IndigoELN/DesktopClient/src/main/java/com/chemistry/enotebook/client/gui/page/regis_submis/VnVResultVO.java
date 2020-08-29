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
 * Created on Oct 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis;

import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class VnVResultVO {
	private String resultStructureString = new String();
	private String errorMessage = new String();
	private String validSIC = new String();
	private String molFormula = new String();
	private double molWeight = 0.0;
	public static String DEFAULT_MESSAGE = "Your batch structure passed VnV.";
	private ArrayList<String> sicList = new ArrayList<String>();

	/**
	 * @return Returns the errorMessage.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            The errorMessage to set.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return Returns the molFormula.
	 */
	public String getMolFormula() {
		return molFormula;
	}

	/**
	 * @param molFormula
	 *            The molFormula to set.
	 */
	public void setMolFormula(String molFormula) {
		this.molFormula = molFormula;
	}

	/**
	 * @return Returns the molWeight.
	 */
	public double getMolWeight() {
		return molWeight;
	}

	/**
	 * @param molWeight
	 *            The molWeight to set.
	 */
	public void setMolWeight(double molWeight) {
		this.molWeight = molWeight;
	}

	/**
	 * @return Returns the resultStructureString.
	 */
	public String getResultStructureString() {
		return resultStructureString;
	}

	/**
	 * @param resultStructureString
	 *            The resultStructureString to set.
	 */
	public void setResultStructureString(String resultStructureString) {
		this.resultStructureString = resultStructureString;
	}

	/**
	 * @return Returns the validSIC.
	 */
	public String getValidSIC() {
		return validSIC;
	}

	/**
	 * @param validSIC
	 *            The validSIC to set.
	 */
	public void setValidSIC(String validSIC) {
		this.validSIC = validSIC;
	}

	/**
	 * @return Returns the sicList.
	 */
	public ArrayList<String> getSicList() {
		return sicList;
	}

	/**
	 * @param sicList
	 *            The sicList to set.
	 */
	public void setSicList(ArrayList<String> sicList) {
		this.sicList = sicList;
	}
}
