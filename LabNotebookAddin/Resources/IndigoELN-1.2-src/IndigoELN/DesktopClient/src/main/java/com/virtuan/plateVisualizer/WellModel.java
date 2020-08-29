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
package com.virtuan.plateVisualizer;

import com.chemistry.enotebook.domain.PlateWell;

import java.awt.*;
import java.util.HashMap;

/**
 * Title: Well Model Description: class to model a physical well
 */
public class WellModel {
	private WellMood _wellMood;
	private HashMap _properties;
	private HashMap _inalienableProperties;
	private PlateWell userWellObject;



	private WellMood emptyMood = new WellMood("CeN_Plate_EMPTY", new BasicStroke(2.5f), Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
			6, WellMood.SHAPE_CIRCULAR, 0, 1, null);

	/**
	 * parameterized constructor
	 * 
	 * @param wellMood
	 */
	public WellModel(WellMood initMood) {
		//userWellObject = userWell;

		_wellMood = initMood;
		_properties = new HashMap();
		_inalienableProperties = new HashMap();
	}



	/**
	 * method to add a property to the well
	 * 
	 * @param wellProperty
	 */
	public void addProperty(WellProperty wellProperty) {
		_properties.put(wellProperty.getName(), wellProperty);
	}

	/**
	 * method to add an inalienable property to the well
	 * 
	 * @param wellProperty
	 */
	public void addInalienableProperty(WellProperty wellProperty) {
		_inalienableProperties.put(wellProperty.getName(), wellProperty);
	}

	/**
	 * method to return a property to the well
	 * 
	 * @param name
	 * @return WellProperty that matches the name
	 */
	public WellProperty getProperty(String name) {
		return (WellProperty) _properties.get(name);
	}

	/**
	 * method to return an inanlienable property to the well
	 * 
	 * @param name
	 * @return WellProperty that matches the name
	 */
	public WellProperty getInalienableProperty(String name) {
		return (WellProperty) _inalienableProperties.get(name);
	}

	/**
	 * method to remove a property of the well
	 * 
	 * @param name
	 */
	public void removeProperty(String name) {
		_properties.remove(name);
	}

	/**
	 * method to remove an inalienable property to the well
	 * 
	 * @param name
	 */
	public void removeInalienableProperty(String name) {
		_inalienableProperties.remove(name);
	}

	/**
	 * method to remove all properties from a well
	 * 
	 * @param name
	 */
	public void removeAllProperties() {
		_properties.clear();
	}

	/**
	 * method to return a property to the well
	 * 
	 * @param name
	 * @return WellProperty that matches the name
	 */
	public WellProperty getProperty(int index) {
		Object[] a = _properties.values().toArray();
		return (WellProperty) a[index];
	}

	/**
	 * method to return an inalienable property to the well
	 * 
	 * @param name
	 * @return WellProperty that matches the name
	 */
	public WellProperty getInalienableProperty(int index) {
		Object[] a = _inalienableProperties.values().toArray();
		return (WellProperty) a[index];
	}

	/**
	 * method to return the number of properties assigned to the well
	 * 
	 * @return int value of the number of properties
	 */
	public int getNumberOfProperties() {
		return _properties.size();
	}

	/**
	 * method to return the number of inalienable properties assigned to the well
	 * 
	 * @return int value of the number of properties
	 */
	public int getNumberOfInalienableProperties() {
		return _inalienableProperties.size();
	}

	public void setWellMood(WellMood wellMood) {
		_wellMood = wellMood;
	}

	/**
	 * method to return the well mood
	 * 
	 * @return WellMood for the well
	 */
	public WellMood getWellMood() {
		return _wellMood;
	}

	public PlateWell getUserWellObject() {
		return userWellObject;
	}

	public void setUserWellObject(PlateWell userWellObject) {
		
		this.userWellObject = userWellObject;
		_wellMood.setUserWellObject(userWellObject);
		/*
		
		BatchModel batchModel = userWellObject.getBatch();
		
		
		int status = 0;
		
		if (batchModel instanceof ProductBatchModel) {
			ProductBatchModel pBatch = (ProductBatchModel) batchModel;

			//pBatch.getSelectivityStatus()== CeNConstants.BATCH_STATUS_PASS
			//pBatch.getContinueStatus()==CeNConstants.BATCH_STATUS_CONTINUE

			if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_PASS) {
				status =0;
			} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_FAIL) {
				status = 1;
			} else if (pBatch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_SUSPECT) {
				status = 5;
			}

		} else if (batchModel instanceof MonomerBatchModel) {
			MonomerBatchModel mBatch = (MonomerBatchModel) batchModel;

		}
		updateWellMood(status);
		
		 */
	}

}
