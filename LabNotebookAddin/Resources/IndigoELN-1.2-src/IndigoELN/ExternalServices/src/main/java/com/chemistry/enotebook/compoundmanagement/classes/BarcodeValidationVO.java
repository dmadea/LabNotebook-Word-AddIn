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
 * Created on Apr 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.compoundmanagement.classes;

import java.io.Serializable;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BarcodeValidationVO 
	implements Serializable 
{
	static final long serialVersionUID = 6602067872567987958L;	
	
	public BarcodeValidationVO(){
		barcode = "";
		barcodeStatus = "";
		siteCode = "";
	}
	
	private String barcode;
	
	private String barcodeStatus;
	
	private String siteCode;
	
	public static final String VALID_STRING = "Valid barcode";
	
	public static final String NOT_VALID_STRING = "Invalid barcode";
	
	public static final String VALID_BUT_IN_USE_STRING = "Valid barcode but in use";

	/**
	 * @return Returns the barcode.
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * @param barcode The barcode to set.
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	/**
	 * @return Returns the barcodeStatus.
	 */
	public String getBarcodeStatus() {
		return barcodeStatus;
	}
	/**
	 * @param barcodeStatus The barcodeStatus to set.
	 */
	public void setBarcodeStatus(String barcodeStatus) {
		this.barcodeStatus = barcodeStatus;
	}
	/**
	 * @return Returns the siteCode.
	 */
	public String getSiteCode() {
		return siteCode;
	}
	/**
	 * @param siteCode The siteCode to set.
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
}
