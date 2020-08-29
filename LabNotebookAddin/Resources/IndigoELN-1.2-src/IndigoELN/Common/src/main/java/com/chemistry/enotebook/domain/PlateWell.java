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

import com.chemistry.enotebook.experiment.utils.GUIDUtil;

/**
 * Created by IntelliJ IDEA. User: ITO01 Date: Jul 18, 2006 Time: 1:46:22 PM To change this template use File | Settings | File
 * Templates.
 */
//Though the class overrides all unnecessary methods, it is better to leave it as it is to avoid changes in many classes/projects.
public class PlateWell<E extends BatchModel> extends CompoundContainer<E> {

	public static final long serialVersionUID = 7526472295622776147L;

	protected PlateWell(){
		this.key = GUIDUtil.generateGUID(this);
		
	}
	
	public PlateWell(AbstractPlate mplate)
	{
		this();
		plate = mplate;
	}
	
	private  PlateWell(String key){
		this();
		this.key=key;
	}
	
	public PlateWell(String key,AbstractPlate mplate){
		this(key);
		plate = mplate;
	}
	
	/**
	 * @return the wellType
	 */
	public String getWellType() {
		return this.containerType;
	}

	/**
	 * @param wellType
	 *            the wellType to set
	 */
	public void setWellType(String wellType) {
		this.containerType = wellType;
		this.modelChanged = true;
	}

	public PlateWell(E batch) {
		this();
		this.batch = batch;
	}
	
	//This constructor should only be called when a new batch is created as we 
	//do not want to set the modelChanged attribute in all other cases except adding the new batch.
	public PlateWell(E batch, String wellType) {
		this();
		this.batch = batch;
		if (wellType != null)
			setWellType(wellType);
	}
	
	public E getBatch() {
		return batch;
	}

	public void setBatch(E batch) {
		this.batch = batch;
		this.modelChanged = true;
	}

	public int getWellPosition() {
		return this.position;
	}

	public void setWellPosition(int wellPosition) {
		this.position = wellPosition;
		this.modelChanged = true;
	}

	public String getWellNumber() {
		return this.number;
	}

	public void setWellNumber(String wellNumber) {
		this.number = wellNumber;
		this.modelChanged = true;
	}

	
	/**
	 * @return the molarity
	 */
	public AmountModel getMolarity() {
		return containedMolarity;
	}

	/**
	 * @param molarity
	 *            the molarity to set
	 */
	public void setMolarity(AmountModel vmolarity) {
		this.setContainedMolarity(vmolarity);
		
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();

		return xmlbuff.toString();
	}
	
	public PlateWell<E> deepClone()
	{
		//Plate well key should be the same
		PlateWell<E>  well = new PlateWell<E>(this.getKey());
		well.deepCopy(this);
		return well;
		
	}

	public AbstractPlate getPlate() {
		return plate;
	}

	public void setPlate(AbstractPlate plate) {
		this.plate = plate;
	}

	public void deepCopy(PlateWell<E> src) {
		setWellPosition(src.position); 
		setWellNumber(src.number);
		setBatch(src.batch) ;
		setContainedWeightAmount(src.containedWeightAmount) ;
		setContainedVolumeAmount(src.containedVolumeAmount) ;
		setMolarity(src.containedMolarity);
		setWellType(src.containerType); 
		setSolventCode(src.solventCode);
		setBarCode(src.barCode);
		setCompoundManagementContainerGUID(src.compoundManagementContainerGUID);
		setLoadedFromDB(src.isLoadedFromDB());
		setModelChanged(src.modelChanged);
		setPurificationServiceParameter(src.getPurificationServiceParameter());
		super.deepCopy(src);
	}


	

	
}
