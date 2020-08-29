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
/**
 * 
 */
package com.chemistry.enotebook.compoundregistration.classes;

import java.io.Serializable;

/**
 * 
 *
 */
public class RegistrationVialDetailsVO implements Serializable {
	static final long serialVersionUID = -1444474239732091558L;

	private String siteCode;
	private String localBarCode;
	private float amountAvailable;
	private String amountUnitCode;

	/**
	 * @return Returns the amountAvailable.
	 */
	public float getAmountAvailable() {
		return amountAvailable;
	}

	/**
	 * @param amountAvailable
	 *            The amountAvailable to set.
	 */
	public void setAmountAvailable(float amountAvailable) {
		this.amountAvailable = amountAvailable;
	}

	/**
	 * @return Returns the amountUnitCode.
	 */
	public String getAmountUnitCode() {
		return amountUnitCode;
	}

	/**
	 * @param amountUnitCode
	 *            The amountUnitCode to set.
	 */
	public void setAmountUnitCode(String amountUnitCode) {
		this.amountUnitCode = amountUnitCode;
	}

	/**
	 * @return Returns the localBarCode.
	 */
	public String getLocalBarCode() {
		return localBarCode;
	}

	/**
	 * @param localBarCode
	 *            The localBarCode to set.
	 */
	public void setLocalBarCode(String localBarCode) {
		this.localBarCode = localBarCode;
	}

	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode
	 *            The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
}
