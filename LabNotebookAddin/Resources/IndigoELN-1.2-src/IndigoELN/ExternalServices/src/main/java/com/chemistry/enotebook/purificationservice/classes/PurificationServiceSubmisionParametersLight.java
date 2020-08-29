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
package com.chemistry.enotebook.purificationservice.classes;

import java.io.Serializable;

public class PurificationServiceSubmisionParametersLight implements Serializable {

	private static final long serialVersionUID = -5862317876987925356L;

	private String key;
	private String modifiers[];// need atleast one value.What are the standard
								// codes ?
	private boolean modelChanged;
	private double phValue; // double,Calculated based on other data
	private String archivePlate = "";// Standard Codes PurificationService
	private double archiveVolume = -1;// UM //This has been referred in the
										// AP#ParameterDialog.java. So,
										// initializing to '0' needs corrections
										// there.
	private String sampleWorkUp = "";// Standard Codes PurificationService
	private boolean isInorganicByProductSaltPresent = false;
	private String saltType = "";
	private boolean separateTheIsomers;
	private String destinationLab = "";// Standard Codes from
										// PurificationService
	private double reactionScale;
	private String reactionScaleUnit;

	private double analyticalPlateConc;
	private String analyticalPlateConcUnit;
	private String comment = "";

	private String wellKey = ""; // Well reference

	public PurificationServiceSubmisionParametersLight(String vKey) {
		this.key = vKey;
	}

	public PurificationServiceSubmisionParametersLight(String vKey, String vWellKey) {
		this.key = vKey;
		this.wellKey = vWellKey;
	}

	public String getKey() {
		return key;
	}
	
	public String getArchivePlate() {
		return archivePlate;
	}

	public void setArchivePlate(String archivePlate) {
		this.archivePlate = archivePlate;
	}

	public double getArchiveVolume() {
		return archiveVolume;
	}

	public void setArchiveVolume(double archiveVolume) {
		this.archiveVolume = archiveVolume;
	}

	public String getDestinationLab() {
		return destinationLab;
	}

	public void setDestinationLab(String destinationLab) {
		this.destinationLab = destinationLab;
	}

	public boolean isInorganicByProductSaltPresent() {
		return isInorganicByProductSaltPresent;
	}

	public void setInorganicByProductSaltPresent(
			boolean isInorganicByProductSaltPresent) {
		this.isInorganicByProductSaltPresent = isInorganicByProductSaltPresent;
	}

	public String[] getModifiers() {
		return modifiers;
	}

	public void setModifiers(String[] modifiers) {
		this.modifiers = modifiers;
	}

	public double getPhValue() {
		return phValue;
	}

	public void setPhValue(double phValue) {
		this.phValue = phValue;
	}

	public String getSampleWorkUp() {
		return sampleWorkUp;
	}

	public void setSampleWorkUp(String sampleWorkUp) {
		this.sampleWorkUp = sampleWorkUp;
	}

	public boolean isSeperateTheIsomers() {
		return separateTheIsomers;
	}

	public void setSeperateTheIsomers(boolean seperateTheIsomers) {
		this.separateTheIsomers = seperateTheIsomers;
	}

	public double getReactionScaleAmount() {
		return reactionScale;
	}

	public String getReactionScaleAmountUnit() {
		return reactionScaleUnit;
	}

	/**
	 * @param scaleAmount
	 *            the scaleAmount to set
	 */
	public void setRecationScaleAmount(double reactionScale,
			String reactionScaleUnit) {

		if ("MOLES".equals(reactionScaleUnit)) {
			//boolean unitChange = false;
			// Check to see if it is a unit change
			if (!reactionScaleUnit.equals(this.reactionScaleUnit)) {
				this.reactionScale = reactionScale;
				this.reactionScaleUnit = reactionScaleUnit;
				setModelChanged(true);
			}
		} else {
			reactionScale = 0;
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setModelChanged(boolean hasChanged) {
		this.modelChanged = hasChanged;
	}

	public boolean setModelChanged() {
		return modelChanged;
	}

	public String getSaltType() {
		return saltType;
	}

	public void setSaltType(String saltType) {
		this.saltType = saltType;
	}

	// For Volume
	public double getAnalyticalPlateConcVolume() {
		return this.analyticalPlateConc;
	}

	public String getAnalyticalPlateConcVolumeUnit() {
		return this.analyticalPlateConcUnit;
	}

	public void setAnalyticalPlateConcVolume(double analyticalConc,
			String analyticalConcUnit) {
		if ("VOLUME".equals(analyticalConcUnit)) {
			// if only units changed
			if (analyticalPlateConc == analyticalConc
					&& !analyticalConcUnit.equals(this.analyticalPlateConcUnit)) {
				this.analyticalPlateConcUnit = analyticalConcUnit;
			}

			if (this.analyticalPlateConc != analyticalConc) {
				this.analyticalPlateConc = analyticalConc;
				setModelChanged(true);
			}
		} else {
			analyticalPlateConc = 0;
		}
	}

	public String getWellKey() {
		return wellKey;
	}

	public void setWellKey(String wellKey) {
		this.wellKey = wellKey;
	}

	public String toString() {
		String str = "";
		str = str + "modifiers :" + getValuesAsMultilineString(modifiers);
		str = str + "archivePlate :" + archivePlate;
		str = str + "archiveVolume :" + archiveVolume;
		str = str + "sampleWorkUp :" + sampleWorkUp;
		str = str + "isInorganicByProductSaltPresent :"
				+ isInorganicByProductSaltPresent;
		str = str + "saltType :" + saltType;
		str = str + "separateTheIsomers :" + separateTheIsomers;
		str = str + "destinationLab :" + destinationLab;
		str = str + "analyticalPlateConc :" + analyticalPlateConc;
		return str;
	}

	private static String getValuesAsMultilineString(String[] vals) {
		String val = "";
		if (vals == null || vals.length == 0)
			return val;

		int size = vals.length;
		for (int i = 0; i < size; i++) {
			val = val + vals[i] + "\n";
		}
		return val;
	}
}
