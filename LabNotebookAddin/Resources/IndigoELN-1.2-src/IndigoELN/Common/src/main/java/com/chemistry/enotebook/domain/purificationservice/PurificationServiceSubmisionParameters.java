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
package com.chemistry.enotebook.domain.purificationservice;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.BatchUtils;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.purificationservice.classes.PurificationServiceSubmisionParametersLight;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;

public class PurificationServiceSubmisionParameters extends CeNAbstractModel{
	
	private static final long serialVersionUID = -5860766738447229069L;
	
	private String modifiers[]  ;//need atleast one value.What are the standard codes ?
	private double phValue     ; //double,Calculated based on other data
	private String archivePlate = "" ;//Standard Codes PurificationService
	private double archiveVolume = -1 ;//UM	//This has been referred in the AP#ParameterDialog.java. So, initializing to '0' needs corrections there.
	private String sampleWorkUp  = "" ;//Standard Codes PurificationService
	private boolean isInorganicByProductSaltPresent = false;
	private String saltType = "";
	private boolean separateTheIsomers ;
	private String destinationLab  = "" ;//Standard Codes from PurificationService
	private AmountModel reactionScale = new AmountModel(UnitType.MOLES);     
	private AmountModel analyticalPlateConc = new AmountModel(UnitType.SCALAR);
    private String wellKey =""; //Well reference
    private String comment = "";
    
    public PurificationServiceSubmisionParameters()
    {
    	// PURIFICATION_SERVICE_Key in CeN_PURIFICATION_SERVICE table
		this.key = GUIDUtil.generateGUID(this);
    }
    
    public PurificationServiceSubmisionParameters(String vKey)
    {
    	this.key = vKey;
    }
    public PurificationServiceSubmisionParameters(String vKey ,String vWellKey)
    {
    	this.key = vKey;
    	this.wellKey = vWellKey;
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



	public void setInorganicByProductSaltPresent(boolean isInorganicByProductSaltPresent) {
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

	public AmountModel getReactionScaleAmount() {
		return reactionScale;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param scaleAmount
	 *            the scaleAmount to set
	 */
	public void setRecationScaleAmount(AmountModel scale) {
		if (scale != null) {
			if (scale.getUnitType().getOrdinal() == UnitType.MOLES.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!reactionScale.equals(scale)) {
					unitChange = BatchUtils.isUnitOnlyChanged(reactionScale, scale);
					reactionScale.deepCopy(scale);
					if (!unitChange) {
						//updateCalcFlags(reactionScale);
						setModified(true);
					}
				}
			}
		} else {
			reactionScale.setValue("0");
		}
	}

	private String formatXMLTag(String tagName, boolean value) {
		return formatXMLTag(tagName, "" + value);
	}
	
	private String formatXMLTag(String tagName, double value) {
		return formatXMLTag(tagName, "" + value);
	}
	
	private String formatXMLTag(String tagName, String value) {
		StringBuffer tagStr = new StringBuffer("");
	  
		tagStr.append("<" + tagName + ">");

		if (StringUtils.isNotBlank(value)) {
			tagStr.append(value);  // ensure not to include "null" as the value
		}
		
		tagStr.append("</" + tagName + ">");
	  
		return tagStr.toString();
	}
	
	public String toXML() {
		StringBuffer xmlStr = new StringBuffer();
		
		xmlStr.append(formatXMLTag("purification_service_reaction_scale_amount", reactionScale != null ? reactionScale.GetValueInStdUnitsAsDouble() : 0));
		xmlStr.append(formatXMLTag("purification_service_archive_plate", archivePlate));
		xmlStr.append(formatXMLTag("purification_service_archive_volume", archiveVolume));
		xmlStr.append(formatXMLTag("purification_service_sample_workup", sampleWorkUp));
		xmlStr.append(formatXMLTag("purification_service_analytical_plate_conc", analyticalPlateConc != null ? analyticalPlateConc.GetValueInStdUnitsAsDouble() : 0));
		xmlStr.append(formatXMLTag("purification_service_destination_lab", destinationLab));
		xmlStr.append(formatXMLTag("purification_service_salt_type", saltType));
		xmlStr.append(formatXMLTag("purification_service_inorganic", isInorganicByProductSaltPresent));
		xmlStr.append(formatXMLTag("purification_service_separate_the_isomers", separateTheIsomers));
		xmlStr.append(formatXMLTag("purification_service_modifiers", StringUtils.join(modifiers, '|')));
		xmlStr.append(formatXMLTag("purification_service_comment", comment));
		
		return xmlStr.toString();
	}

	public String getSaltType() {
		return saltType;
	}

	public void setSaltType(String saltType) {
		this.saltType = saltType;
	}
	
	// For Volume
	public AmountModel getAnalyticalPlateConcVolume() {
		return this.analyticalPlateConc;
	}

	public void setAnalyticalPlateConcVolume(AmountModel analyticalConc) {
		if (analyticalConc != null) {
			if (analyticalConc.getUnitType().getOrdinal() == UnitType.VOLUME.getOrdinal()) {
				boolean unitChange = false;
				// Check to see if it is a unit change
				if (!analyticalPlateConc.equals(analyticalConc)) {
					unitChange = BatchUtils.isUnitOnlyChanged(analyticalPlateConc, analyticalConc);
					analyticalPlateConc.deepCopy(analyticalConc);
					if (!unitChange) {
						setModified(true);
					}
				}
			}
		} else {
			analyticalPlateConc.setValue("0");
		}
	}

	public String getWellKey() {
		return wellKey;
	}

	public void setWellKey(String wellKey) {
		this.wellKey = wellKey;
	}
	
	
	public String toString()
	{
		String str= "";
		str = str+ "modifiers :"+CommonUtils.getValuesAsMultilineString(modifiers);
		str = str+ "archivePlate :"+archivePlate;
		str = str+ "archiveVolume :"+archiveVolume;
		str = str+ "sampleWorkUp :"+sampleWorkUp;
		str = str+ "isInorganicByProductSaltPresent :"+isInorganicByProductSaltPresent;
		str = str+ "saltType :"+saltType;
		str = str+ "separateTheIsomers :"+separateTheIsomers;
		str = str+ "destinationLab :"+destinationLab;
		str = str+ "analyticalPlateConc :" + analyticalPlateConc;
		return str;
	}
	
	public PurificationServiceSubmisionParametersLight convertToPurificationServiceSubmisionParametersLight() {
		PurificationServiceSubmisionParametersLight purificationServiceSubmisionParametersLight = new PurificationServiceSubmisionParametersLight(this.getKey());
		
		purificationServiceSubmisionParametersLight.setModifiers(this.getModifiers());
		purificationServiceSubmisionParametersLight.setPhValue(this.getPhValue());
		purificationServiceSubmisionParametersLight.setArchivePlate(this.getArchivePlate());
		purificationServiceSubmisionParametersLight.setArchiveVolume(this.getArchiveVolume());
		purificationServiceSubmisionParametersLight.setSampleWorkUp(this.getSampleWorkUp());
		purificationServiceSubmisionParametersLight.setInorganicByProductSaltPresent(this.isInorganicByProductSaltPresent);
		purificationServiceSubmisionParametersLight.setSaltType(this.getSaltType());
		purificationServiceSubmisionParametersLight.setSeperateTheIsomers(this.isSeperateTheIsomers());
		purificationServiceSubmisionParametersLight.setDestinationLab(this.getDestinationLab());
		purificationServiceSubmisionParametersLight.setWellKey(this.getWellKey());
		
		AmountModel reactionScale = this.getReactionScaleAmount();
		purificationServiceSubmisionParametersLight.setRecationScaleAmount(reactionScale.GetValueInStdUnitsAsDouble(), reactionScale.getUnit().getDisplayValue());
		
		AmountModel analyticalConc = this.getAnalyticalPlateConcVolume();
		purificationServiceSubmisionParametersLight.setAnalyticalPlateConcVolume(analyticalConc.GetValueInStdUnitsAsDouble(), analyticalConc.getUnit().getDisplayValue());
		
		return purificationServiceSubmisionParametersLight;
	}
	
}
