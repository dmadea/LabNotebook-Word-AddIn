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
 * Created on Mar 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.registration;

import java.io.Serializable;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SampleForTestVO 
	implements Serializable
{
	static final long serialVersionUID = -81486083079939404L;	

	private String batchTrackingId;
	private String protocolId;
	private String scientistCode;
	private String barCode;
	private double amountInMg;
	private boolean isContainer;

	
	public SampleForTestVO(){
		batchTrackingId = "";
		protocolId = "";
		scientistCode = "";
		barCode = "";
		amountInMg = 0.0;
		isContainer = false;
	}
	
	/**
	 * @return Returns the batchTrackingId.
	 */
	public String getBatchTrackingId() {
		return batchTrackingId;
	}
	/**
	 * @param batchTrackingId The batchTrackingId to set.
	 */
	public void setBatchTrackingId(String batchTrackingId) {
		this.batchTrackingId = batchTrackingId;
	}
	/**
	 * @return Returns the protocolId.
	 */
	public String getProtocolId() {
		return protocolId;
	}
	/**
	 * @param protocolId The protocolId to set.
	 */
	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}
	/**
	 * @return Returns the scientistCode.
	 */
	public String getScientistCode() {
		return scientistCode;
	}
	/**
	 * @param scientistCode The scientistCode to set.
	 */
	public void setScientistCode(String scientistCode) {
		this.scientistCode = scientistCode;
	}
	/**
	 * @return Returns the amountInMg.
	 */
	public double getAmountInMg() {
		return amountInMg;
	}
	/**
	 * @param amountInMg The amountInMg to set.
	 */
	public void setAmountInMg(double amountInMg) {
		this.amountInMg = amountInMg;
	}
	/**
	 * @return Returns the barCode.
	 */
	public String getBarCode() {
		return barCode;
	}
	/**
	 * @param barCode The barCode to set.
	 */
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	/**
	 * @return Returns the forMM.
	 */
	public boolean isContainer() {
		return isContainer;
	}
	/**
	 * @param forMM The forMM to set.
	 */
	public void setContainer(boolean forMM) {
		this.isContainer = forMM;
	}
}
