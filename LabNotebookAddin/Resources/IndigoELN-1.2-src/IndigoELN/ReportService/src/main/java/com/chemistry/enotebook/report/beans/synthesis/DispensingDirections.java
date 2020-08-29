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

public class DispensingDirections {

	private String plateKey = "";
	private String plateName = "";
	private String plateWell = "";
	private String moleAmount = "";
	private String weightAmount = "";
	private String volumeAmount = "";
	private String totalWeight = "";
	private String totalVolume = "";
	
	public void dispose() {
		
	}

	public String getPlateKey() {
		return plateKey;
	}
	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}
	public String getPlateName() {
		return this.truncatePlateName(plateName);
	}
	public void setPlateName(String plateName) {
		this.plateName = plateName;
	}
	public String getPlateWell() {
		return plateWell;
	}
	public void setPlateWell(String plateWell) {
		this.plateWell = plateWell;
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
	 * Remove the user id, notebook number and experiment number
	 * from plate name for brevity.  
	 * @param plateName
	 * @return
	 */
	private String truncatePlateName(String plateName) {
		if (plateName.length() <= 18)
			return plateName;
		else {
			int lastHyphenIdx = plateName.lastIndexOf("-");
			if (lastHyphenIdx > 0) {
				String plateNumberPart = plateName.substring(plateName.lastIndexOf("-") + 1);
				if (plateNumberPart != null && plateNumberPart.length() >= 3) {
					return plateNumberPart;
				}
			}
		}
		return plateName;
	}
	
	public String toXml() {
		StringBuffer buff = new StringBuffer("<dispensingDirections>");
		TextUtils.fillBufferWithClassMethods(buff, this);
		buff.append("</dispensingDirections>");
		//System.out.println(buff.toString());
		return buff.toString();
	}

	public String getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(String totalWeight) {
		this.totalWeight = totalWeight;
	}

	public String getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(String totalVolume) {
		this.totalVolume = totalVolume;
	}
}
