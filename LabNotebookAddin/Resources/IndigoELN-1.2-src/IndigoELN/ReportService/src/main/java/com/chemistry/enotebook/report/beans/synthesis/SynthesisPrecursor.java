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
package com.chemistry.enotebook.report.beans.synthesis;

import com.chemistry.enotebook.report.utils.TextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SynthesisPrecursor {
	private static final Log log = LogFactory.getLog(SynthesisPrecursor.class);
	
	private String batchId = "";
	private String moleAmount = "";
	private String weightAmount = "";
	private String volumeAmount = "";
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getMoleAmount() {
		return moleAmount;
	}
	public void setMoleAmount(String moleAmount) {
		this.moleAmount = moleAmount;
	}
	public String getWeightAmount() {
		return weightAmount;
	}
	public void setWeightAmount(String weightAmount) {
		this.weightAmount = weightAmount;
	}
	public String getVolumeAmount() {
		return volumeAmount;
	}
	public void setVolumeAmount(String volumeAmount) {
		this.volumeAmount = volumeAmount;
	}
	
	/**
	 * TODO: This should migrate to a utility as it is a fairly general mechanism for producing an object's state as xml
	 * @return
	 */
	public String toXml() {
		StringBuffer buff = new StringBuffer("<precursor>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</precursor>");
		log.debug(buff.toString());
		return buff.toString();
	}
}
