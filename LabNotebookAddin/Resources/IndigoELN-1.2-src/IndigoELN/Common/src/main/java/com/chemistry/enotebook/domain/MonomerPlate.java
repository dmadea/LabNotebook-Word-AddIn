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

import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: ITO01 Date: Jul 18, 2006 Time: 1:29:30 PM To change this template use File | Settings | File
 * Templates.
 */
public class MonomerPlate extends AbstractPlate<MonomerBatchModel> {


	private static final long serialVersionUID = -629504078333739227L;

	private boolean select;
	private String stepNumber;
	private String rackPlateFlag;
	private String parentPlateKey;

	public MonomerPlate(String key){
		this.key = key;
		this.setLoadedFromDB(true);
		this.setPlateType(CeNConstants.PLATE_TYPE_MONOMER);
		this.setContainer(new Container());
	}
	public MonomerPlate(){
		this.key = GUIDUtil.generateGUID(this);
		this.setLoadedFromDB(false);
		this.setPlateType(CeNConstants.PLATE_TYPE_MONOMER);
		this.setContainer(new Container());
	}
	
	
	/**
	 * @return the rackPlateFlag
	 */
	public String getRackPlateFlag() {
		return rackPlateFlag;
	}

	/**
	 * @param rackPlateFlag
	 *            the rackPlateFlag to set
	 */
	public void setRackPlateFlag(String rackPlateFlag) {
		this.rackPlateFlag = rackPlateFlag;
		this.modelChanged = true;
	}

	/**
	 * @return the select
	 */
	public boolean isSelect() {
		return select;
	}

	/**
	 * @param select
	 *            the select to set
	 */
	public void setSelect(boolean select) {
		this.modelChanged = true;
		this.select = select;
	}

	/**
	 * @return the stepNumber
	 */
	public String getStepNumber() {
		return stepNumber;
	}

	/**
	 * @param stepNumber
	 *            the stepNumber to set
	 */
	public void setStepNumber(String stepNumber) {
		this.stepNumber = stepNumber;
		this.modelChanged = true;
	}
 
	public List<MonomerBatchModel> getListOfMonomerBatches() {
		PlateWell[] wells = getWells();
		int wellsSize = wells.length;
		List<MonomerBatchModel> monomerBatches = new ArrayList<MonomerBatchModel>(wellsSize);

		// traverse plate wells and extract batch references from each
		for (int i = 0; i < wellsSize; i++) {
			PlateWell well = wells[i];
			MonomerBatchModel batch = (MonomerBatchModel) well.getBatch();
			if (batch != null)
				monomerBatches.add(batch);
		}

		return monomerBatches;
	}
   
	
	public String getParentPlateKey() {
		return parentPlateKey;
	}
	public void setParentPlateKey(String parentPlateKey) {
		this.parentPlateKey = parentPlateKey;
		this.modelChanged = true;
	}
	
	public String getWellPosition(BatchModel batch){
		PlateWell[] wells = getWells();
		int wellsSize = wells.length;
		// traverse plate wells and look for matching batch
		for (int i = 0; i < wellsSize; i++) {
			PlateWell well = wells[i];
			MonomerBatchModel wellBatch = (MonomerBatchModel) well.getBatch();
			//?? object reference comparison ?
			if (batch == wellBatch) {
				return well.getWellNumber();
			}
		}
		return "";
	}
	public AmountModel getAmountforBatch(BatchModel batch){
		
		AmountModel mAmountModel = new AmountModel(UnitType.MASS);
		mAmountModel.setValue(0.0);
		PlateWell[] wells = getWells();
		int wellsSize = wells.length;
		// traverse plate wells and look for matching batch
		for (int i = 0; i < wellsSize; i++) {
			PlateWell well = wells[i];
			MonomerBatchModel wellBatch = (MonomerBatchModel) well.getBatch();
			if (batch == wellBatch) {
				return well.getContainedWeightAmount();
			}
		}
		return mAmountModel;
	}
	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();

		return xmlbuff.toString();
	}
	
	
	public Object deepClone()
	{
		//Plate  key should be the same
		MonomerPlate  plate = new MonomerPlate(this.getKey());
		plate.deepCopy(this);
		return plate;
	}
	
	public List<PlateWell> getPlateWellsforBatch(MonomerBatchModel batch){
		
		List<PlateWell> list = new ArrayList<PlateWell>();
		PlateWell[] wells = getWells();
		int wellsSize = wells.length;
		// traverse plate wells and look for matching batch
		for (int i = 0; i < wellsSize; i++) {
			PlateWell well = wells[i];
			MonomerBatchModel wellBatch = (MonomerBatchModel) well.getBatch();
			if (batch == wellBatch) {
				list.add(well);
			}
		}
		return list;
	}
	public void deepCopy(MonomerPlate src) {
		if (src.key.equals(this.key))//This is called from deepClone().
			setWells(src.getAllModifedWellsClone());
		else
			setWells(src.getAllWellsCopy());
		setContainer(src.getContainer());
		setPlateNumber(src.getPlateNumber());
        setRegisteredDate(src.getRegisteredDate());
        setPlateType(src.getPlateType());
        setPlateComments(src.getPlateComments()) ;
        setSelect(src.select);
		setStepNumber(src.stepNumber);
		setRackPlateFlag(src.rackPlateFlag);
		setParentPlateKey(src.parentPlateKey);
		setPlateBarCode(src.getPlateBarCode());
		setLoadedFromDB(src.isLoadedFromDB());
		setModelChanged(src.modelChanged);
	}
}
