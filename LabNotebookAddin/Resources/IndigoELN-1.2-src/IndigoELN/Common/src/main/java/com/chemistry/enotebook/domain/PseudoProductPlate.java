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

public class PseudoProductPlate extends ProductPlate implements Comparable<ProductPlate> {

	public static final long serialVersionUID = 3609440484123506971L;

	ArrayList<ProductBatchModel> batches = new ArrayList<ProductBatchModel>();

	public PseudoProductPlate(List<ProductBatchModel> vbatches) {
		batches.addAll(vbatches);
		this.setPlateNumber(CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO);
		this.setPlateType(CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO);
		this.setPlateBarCode("NO_BARCODE");
		Container fakeContainer = new Container("FAKE_CONTAINER_KEY_FOR_PSEUDOPLATE");
		this.setContainer(fakeContainer);
		if(vbatches == null || vbatches.size() == 0)
		{
			wells = new PlateWell[0];	
		}else
		{
//			This should happen at server side. Not here.
			wells = new PlateWell[vbatches.size()];	
		}
        if(vbatches == null) return;
		
		int index = 0;
		for (ProductBatchModel batch : batches) {
			//At this point the Well Type is not selected. Eventually it can be VIAL.
			PlateWell<ProductBatchModel> well = new PlateWell<ProductBatchModel>(batch, null);
			well.setPlate(this);
			wells[index] = well;
			index++;
		}
	}

	public PseudoProductPlate(String vKey)
	{
		this.key = vKey;
		this.setLoadedFromDB(false);
		this.setPlateType(CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO);
		
	}
	public PseudoProductPlate() {//Only used for versioning. 
		this.key = GUIDUtil.generateGUID(this);
		this.setLoadedFromDB(false);
		this.setPlateType(CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO);
	}
	public void addNewBatch(ProductBatchModel batch)
	{
		if (batch instanceof ProductBatchModel)
		{
			batches.add(batch);
			moveBatchesToContainers(batch);
		}
	}
	
	public PlateWell<ProductBatchModel> moveBatchesToContainers(ProductBatchModel batch)
	{
		PlateWell<ProductBatchModel> well = new PlateWell<ProductBatchModel>(batch, CeNConstants.CONTAINER_TYPE_VIAL);
		well.setPlate(this);
		PlateWell<ProductBatchModel> [] oldWells = this.getWells();
		PlateWell<ProductBatchModel> [] newWells = null;
		if (oldWells == null)
		{
			newWells = new PlateWell[1];
			newWells[0] = well;
		}
		else
		{
			newWells = new PlateWell[oldWells.length + 1];
			System.arraycopy( oldWells, 0, newWells, 0, oldWells.length );
			newWells[oldWells.length] = well;
		}
		this.setWells(newWells);
		newWells = null;
		oldWells = null;
		return well;
	}
	
//	public void moveBatchesToContainers(ArrayList productBatchModelList)
//	{
//		int increaseSize = productBatchModelList.size(); 
//		PlateWell [] oldWells = this.getWells();
//		PlateWell [] newWells = null;
//		PlateWell well = null;
//		ProductBatchModel batch = null;
//
//		if (oldWells == null)
//		{
//			newWells = new PlateWell[increaseSize];
//			for(int i=0; i<increaseSize; i++)
//			{
//				batch = (ProductBatchModel)productBatchModelList.get(i);
//				well = new PlateWell(batch, CeNConstants.CONTAINER_TYPE_VIAL);
//				newWells[i] = well;
//			}
//		}
//		else
//		{
//			newWells = new PlateWell[oldWells.length + increaseSize];
//			System.arraycopy( oldWells, 0, newWells, 0, oldWells.length );
//			for(int i=0; i<increaseSize; i++)
//			{
//				batch = (ProductBatchModel)productBatchModelList.get(i);
//				well = new PlateWell(batch, CeNConstants.CONTAINER_TYPE_VIAL);
//				newWells[oldWells.length + i] = well;
//			}
//		}
//		this.setWells(newWells);
//		oldWells = null;
//		newWells = null;
//	}	

	public void removeBatch(ProductBatchModel batch) {
		PlateWell<ProductBatchModel>[] oldWells = getWells();
		if (oldWells != null && oldWells.length > 0) {
			PlateWell<ProductBatchModel>[] newWells = new PlateWell[oldWells.length - 1];
			boolean isVialCreated = false;
			int count = 0;
			for (PlateWell<ProductBatchModel> oldWell : oldWells) {
				if (oldWell != null) {
					BatchModel tempBatch = oldWell.getBatch();
					if (tempBatch != batch) {
						newWells[count] = oldWell;
						count += 1;
					} else {
						isVialCreated = true;
					}
				}
			}
			if (isVialCreated) {
				setWells(newWells);
			}
			newWells = null;
		}
		batches.remove(batch);
		batch = null;
		oldWells = null;
	}

	public List<ProductBatchModel> getListOfProductBatches() {
		return batches;
	}

	public void updateSelectivity() {
		PlateWell<ProductBatchModel>[] wells = getWells();
		for (int i = 0; i < wells.length; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel batch = well.getBatch();
			if (batch != null)
				switch (batch.getSelectivityStatus()) {
					case CeNConstants.BATCH_STATUS_PASS:
						// numPassed++;
						break;
					case CeNConstants.BATCH_STATUS_FAIL:
						// numFailed++;
						break;
					case CeNConstants.BATCH_STATUS_SUSPECT:
						// numSuspect++;
						break;
				}
		}
	}

	public String toXML() {
		StringBuffer xmlbuff = new StringBuffer();

		return xmlbuff.toString();
	}

	public int getNumFailed() {
		int numFailed = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return numFailed;
		else
			return super.getNumFailed();
	}

	public int getNumPassed() {
		int numPassed = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return numPassed;
		else
			return super.getNumPassed();
	}

	public int getNotMade() {
		return 0;
	}

	public int getRegisteredBatchesCount() {
		int numPassed = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return numPassed; 
		else
			return super.getRegisteredBatchesCount();
	}

	public int getNumSuspect() {
		int numSuspect = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return numSuspect;
		else
			return super.getNumSuspect();
	}

	public String getWellPosition(BatchModel batch) {
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return "";
		else
			return super.getWellPosition(batch);
	}
	
	public AmountModel getAmountforBatch(BatchModel batch) {

		AmountModel mAmountModel = new AmountModel(UnitType.MASS);
		mAmountModel.setValue(0.0);
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return mAmountModel;
		else
			return super.getAmountforBatch(batch);
	}

	public List<PlateWell<ProductBatchModel>> getPlateWellsforBatch(ProductBatchModel batch) {
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null)
			return new ArrayList<PlateWell<ProductBatchModel>>();
		else
			return super.getPlateWellsforBatch(batch);
	}

	public ProductBatchModel[] getAllBatchesInThePlate() {
		return batches.toArray(new ProductBatchModel[] {});
	}
	
	public String getLotNo() {
		//return getPlateNumber();
		return CeNConstants.PSEUDO_PLATE_LABEL;
	}

	public ArrayList<ProductBatchModel> getBatches() {
		return batches;
	}

	public void setBatches(ArrayList<ProductBatchModel> batches) {
		this.batches = batches;
	}

	public PlateWell<ProductBatchModel>[] getWells() {
		return wells;
	}

	public void setWells(PlateWell<ProductBatchModel>[] wells) {
		this.wells = wells;
	}

	/**
	 * @return the plateBarCode
	 */
	public String getPlateBarCode() {
		return "";
	}
	
	public int compareTo(ProductPlate arg0) {
		return -1;
	}

	public Object deepClone() {
		// Plate key should be the same
		PseudoProductPlate plate = new PseudoProductPlate(this.getKey());
		plate.deepCopy(this);
		return plate;
	}
		
	public void deepCopy(PseudoProductPlate src) {
		if (src.key.equals(this.key))//This is called from deepClone().
		{
			setWells(src.getAllModifedWellsClone());
			setBatches(src.getBatches());
		}
		else
		{
			setWells(src.getAllWellsCopy());
			setBatches(getBatches(getWells()));
		}
		
		setContainer(src.getContainer());
		setPlateNumber(src.getPlateNumber());
		setRegisteredDate(src.getRegisteredDate());
		setPlateType(src.getPlateType());
		setPlateComments(src.getPlateComments());
		setPlateBarCode(src.getPlateBarCode());
		setLoadedFromDB(src.isLoadedFromDB());
		setModelChanged(src.modelChanged);
		setCompoundManagementRegistrationSubmissionStatus(src.getCompoundManagementRegistrationSubmissionStatus());
	}
	private ArrayList<ProductBatchModel> getBatches(PlateWell<ProductBatchModel>[] wells) {
		ArrayList<ProductBatchModel> batchesList = new ArrayList<ProductBatchModel>();
		if (wells != null) {
			for (int i=0; i<wells.length && 
			     wells[i].getBatch() != null && 
			     wells[i].getBatch() instanceof ProductBatchModel; i++) 
			{
				batchesList.add((ProductBatchModel)wells[i].getBatch());
			}
		}
		return batchesList;
	}
	
	//Use it only for versioning
	public List<PlateWell<ProductBatchModel>> getPlateWellsforBatchByBatchNum(ProductBatchModel batchModel) {
		List<PlateWell<ProductBatchModel>> list = new ArrayList<PlateWell<ProductBatchModel>>();
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells == null || batchModel == null) 
			return list;
		
		int wellsSize = wells.length;
		// traverse plate wells and look for matching batch
		for (int i = 0; i < wellsSize; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel wellBatch = well.getBatch();
			if(wellBatch == null) continue;
			if (batchModel.getBatchNumber().getBatchNumber().equals(wellBatch.getBatchNumber().getBatchNumber())) {
				well.setBatch(batchModel);
				list.add(well);
			}
		}
		return list;
	}

}