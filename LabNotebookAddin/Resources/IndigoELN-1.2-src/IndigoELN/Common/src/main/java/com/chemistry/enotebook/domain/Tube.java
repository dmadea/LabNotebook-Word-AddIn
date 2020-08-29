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

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;

public class Tube extends CompoundContainer {

	private static final long serialVersionUID = -2431881495283136266L;
	private String tubeGUID ="";  // GUID from CompoundManagement
	private String barcode =""; //2D barcode


	private Tube(){
		this.key = GUIDUtil.generateGUID(this);
		containedWeightAmount = new AmountModel(UnitType.MASS);
		containedVolumeAmount = new AmountModel(UnitType.VOLUME);
		containedMolarity = new AmountModel(UnitType.MOLAR);
	}
	
	public Tube(ProductPlate mplate)
	{
		this();
		plate = mplate;
	}
	
	private  Tube(String key){
		this();
		this.key=key;
	}
	
	public Tube(String key,ProductPlate mplate){
		this(key);
		plate = mplate;
	}
	
	public Tube(BatchModel batch) {
		this();
		this.batch = batch;
	}

/*	public BatchModel getBatch() {
		return batch;
	}

	public void setBatch(BatchModel batch) {
		this.batch = batch;
	}
*/
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();

		return xmlbuff.toString();
	}

	public String getCompoundManagementContainerGUID() {
		return tubeGUID;
	}

	public void setCompoundManagementContainerGUID(String tubeGUID) {
		this.tubeGUID = tubeGUID;
	}
	
	public Object deepClone()
	{
		//tube key should be the same
		Tube tube = new Tube(this.getKey());
		tube.setPosition(this.position); 
		tube.setNumber(this.number);
		tube.setBatch(this.batch) ;
		tube.setContainedWeightAmount(this.containedWeightAmount) ;
		tube.setContainedVolumeAmount(this.containedVolumeAmount) ;
		tube.setContainedMolarity(this.containedMolarity);
		tube.setSolventCode(this.solventCode);
		return tube;
	}
	
	public String getContainerType() {
		return "TUBE";
	}

}
