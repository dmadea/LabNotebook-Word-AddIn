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
/**
 * 
 */
package com.chemistry.enotebook.domain;

import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 */
public class ProductPlate extends AbstractPlate<ProductBatchModel> implements Comparable<ProductPlate>{


	private static final long serialVersionUID = 8431572731172757511L;

	private static final Log log = LogFactory.getLog(ProductPlate.class);
	
	private String stepNumber;
	private String rackPlateFlag;
	
	//batch tab
	private boolean select;
	
	//analytical tab
	private boolean select_analytical;
	
	//registration tab
	private boolean select_reg;
	
	// CompoundManagement Reg Info
	private String compoundManagementRegistrationSubmissionStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String compoundManagementRegistrationSubmissionMessage;
	// PurificationService Info
	private String purificationSubmissionStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String purificationSubmissionMessage;
	//CompoundAggregation info
	private String screenPanelsSubmissionStatus = CeNConstants.REGINFO_NOT_SUBMITTED;
	private String screenPanelSubmissionMessage;
	
	private String lotNo;
    private String projectTrackingCode; //for CompoundManagement container registration.For CeN it is at Page level
	// private int numPassed = 0, numFailed = 0, numSuspect = 0;

	public ProductPlate(String key) {
		this.key = key;
		this.setLoadedFromDB(true);
		this.setPlateType(CeNConstants.PLATE_TYPE_PRODUCT);
	}

	public ProductPlate() {
		this.key = GUIDUtil.generateGUID(this);
		this.setLoadedFromDB(false);
		this.setPlateType(CeNConstants.PLATE_TYPE_PRODUCT);
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

	public boolean isSelect_analytical() {
		return select_analytical;
	}

	public void setSelect_analytical(boolean select_analytical) {
		this.select_analytical = select_analytical;
		this.modelChanged = true;
	}

	public boolean isSelect_reg() {
		return select_reg;
	}

	public void setSelect_reg(boolean select_reg) {
		this.select_reg = select_reg;
		this.modelChanged = true;
	}

	/**
	 * @param select
	 *            the select to set
	 */
	public void setSelect(boolean select) {
		this.select = select;
		this.modelChanged = true;
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

	/**
	 * This method retrieve a list with ProductBatches. The plate is traversed from Well zero to the total number of wells and
	 * product batch is extracted from each well. A reference for each batch is pushed in the local list which is returned.
	 * 
	 * @return list with product batches
	 */

	public List<ProductBatchModel> getListOfProductBatches() {
		PlateWell<ProductBatchModel>[] wells = getWells();
		if (wells != null)
		{
			int wellsSize = wells.length;
		
			List<ProductBatchModel> productBatches = new ArrayList<ProductBatchModel>(wellsSize);
	
			// traverse plate wells and extract batch references from each
			for (PlateWell<ProductBatchModel> well : wells) {
				if (well.getBatch() != null)
				{
					ProductBatchModel batch = well.getBatch();
					productBatches.add(batch);
				}
			}
			return productBatches;
		}
		else
			return null;
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
		for (int i = 0; i < wells.length; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel batch = (ProductBatchModel) well.getBatch();
			if (batch != null && (batch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_FAIL)) {
				numFailed++;
			}
		}
		return numFailed;
	}

	public int getNumPassed() {
		int numPassed = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		for (int i = 0; i < wells.length; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel batch = (ProductBatchModel) well.getBatch();
			if (batch == null) {
				continue;
			}
			int batchStatus = batch.getSelectivityStatus();
			if ((batchStatus == CeNConstants.BATCH_STATUS_PASS) || (batchStatus == CeNConstants.BATCH_STATUS_PASSED_REGISTERED)) {
				numPassed++;
			}
		}
		return numPassed;
	}

	public int getNotMade() {
		int numNotMade = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		for (int i = 0; i < wells.length; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel batch = (ProductBatchModel) well.getBatch();
			if (batch != null && (batch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_NOT_MADE)) {
				numNotMade++;
			}
		}
		return numNotMade;
	}

	public int getRegisteredBatchesCount() {
		int numRegistered = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		for (int i = 0; i < wells.length; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel batch = well.getBatch();
			if (batch != null && (CeNConstants.REGINFO_REGISTERED.equals(batch.getRegInfo().getStatus()))) {
				numRegistered++;
			}
		}
		return numRegistered;
	}

	public int getNumSuspect() {
		int numSuspect = 0;
		PlateWell<ProductBatchModel>[] wells = getWells();
		for (int i = 0; i < wells.length; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel batch = well.getBatch();
			if (batch != null && (batch.getSelectivityStatus() == CeNConstants.BATCH_STATUS_SUSPECT)) {
				numSuspect++;
			}
		}
		return numSuspect;
	}

	public Object deepClone() {
		// Plate key should be the same
		ProductPlate plate = new ProductPlate(this.getKey());
		plate.deepCopy(this);
		return plate;
	}

	// overriding super class impl
	public void setWells(PlateWell<ProductBatchModel>[] wells) {
		super.setWells(wells);
	}

	public String getWellPosition(BatchModel batch) {
		PlateWell<ProductBatchModel>[] wells = getWells();
		int wellsSize = wells.length;
		// traverse plate wells and look for matching batch
		for (int i = 0; i < wellsSize; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel wellBatch = well.getBatch();
			// ?? object reference comparison ?
			if (batch == wellBatch) {
				return well.getWellNumber();
			}
		}
		return "";
	}

	public AmountModel getAmountforBatch(BatchModel batch) {

		AmountModel mAmountModel = new AmountModel(UnitType.MASS);
		mAmountModel.setValue(0.0);
		PlateWell<ProductBatchModel>[] wells = getWells();
		int wellsSize = wells.length;
		// traverse plate wells and look for matching batch
		for (int i = 0; i < wellsSize; i++) {
			PlateWell<ProductBatchModel> well = wells[i];
			ProductBatchModel wellBatch = well.getBatch();
			if (batch == wellBatch) {
				return well.getContainedWeightAmount();
			}
		}
		return mAmountModel;
	}

	public List<PlateWell<ProductBatchModel>> getPlateWellsforBatch(ProductBatchModel batch) {
		List<PlateWell<ProductBatchModel>> list = new ArrayList<PlateWell<ProductBatchModel>>();
		if (batch != null) {
			PlateWell<ProductBatchModel>[] wells = getWells(); 
			// traverse plate wells and look for matching batch
			for (PlateWell<ProductBatchModel> well : wells) {
				ProductBatchModel wellBatch = well.getBatch();
				if (wellBatch != null) {
					if (batch.getKey().equals(wellBatch.getKey())) {
						list.add(well);
					}
				}
			}
		}
		return list;
	}

/*	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean plate_Registered) {
		this.registered = plate_Registered;
		this.modelChanged = true;
	}*/
	
	/**
	 * @return true if compoundManagementRegistrationSubmissionStatus == CeNConstants.REGINFO_PASSED
	 */
	public boolean isCompoundManagementRegistered() {
		return StringUtils.equals(compoundManagementRegistrationSubmissionStatus, CeNConstants.REGINFO_PASSED) ||
			   StringUtils.equals(compoundManagementRegistrationSubmissionStatus, CeNConstants.REGINFO_SUBMISION_PASS);
	}
	
	public String getCompoundManagementRegistrationSubmissionStatus() {
		if (compoundManagementRegistrationSubmissionStatus == null) 
			return CeNConstants.REGINFO_NOT_SUBMITTED;
		return compoundManagementRegistrationSubmissionStatus;
	}

	public void setCompoundManagementRegistrationSubmissionStatus(String regComments) {
		if (regComments!= null)
		{
			this.compoundManagementRegistrationSubmissionStatus = regComments;
			this.modelChanged = true;
		}
/*		PlateWell[] plateWells = getWells();
		for (int i=0; i< plateWells.length; i++)
		{
			ProductBatchModel batchModel = (ProductBatchModel) plateWells[i].getBatch();
			batchModel.getRegInfo().setCompoundManagementStatus(regComments);
		}
*/		
	}



	public ProductBatchModel[] getAllBatchesInThePlate() {
		try {
			PlateWell<ProductBatchModel>[] wells = this.getWells();
			ArrayList<ProductBatchModel> batches = new ArrayList<ProductBatchModel>();

			for (int i = 0; i < wells.length; i++) {
				// there can be some empty wells in the plate
				ProductBatchModel model = wells[i].getBatch();
				if (model != null) {
					batches.add(model);
				}
			}
			ProductBatchModel[] pBatches = new ProductBatchModel[batches.size()];
			for (int i = 0; i < batches.size(); i++) {
				pBatches[i] = batches.get(i);
			}
			return pBatches;// (ProductBatchModel[])batches.toArray(new ProductBatchModel[]{});
		} catch (Exception err) {
			log.error("Failed to get batches in the plate.", err);
		}
		return null;
	}

	public String getLotNo() {
		String[] plateNumberSplit = getPlateNumber().split("-");
		lotNo = plateNumberSplit[2];
		return lotNo;
	}

	public String getPurificationSubmissionStatus() {
		if (purificationSubmissionStatus == null)
			return CeNConstants.REGINFO_NOT_SUBMITTED;
		return purificationSubmissionStatus;
	}
	
	/**
	 * 
	 * @return true if purificationSubmissionStatus == CeNConstants.REGINFO_SUBMITTED
	 */
	public boolean isPurificationServiceSubmitted() {
		return purificationSubmissionStatus.contains(CeNConstants.REGINFO_SUBMITTED);
	}
	/**
	 * 
	 * @return true if purificationSubmissionStatus == CeNConstants.REGINFO_SUBMISION_PASS
	 */
	public boolean isPurificationServiceSubmittedSuccessfully() {
		return purificationSubmissionStatus.contains(CeNConstants.REGINFO_SUBMISION_PASS );
	}

	public void setPurificationSubmissionStatus(String purifiedDate) {
		this.purificationSubmissionStatus = purifiedDate;
	}

	public String getScreenPanelsSubmissionStatus() {
		if (screenPanelsSubmissionStatus == null)
			return CeNConstants.REGINFO_NOT_SUBMITTED;
		return screenPanelsSubmissionStatus;
	}

	public void setScreenPanelsSubmissionStatus(String submissionToScreenPanelsDate) {
		this.screenPanelsSubmissionStatus = submissionToScreenPanelsDate;
	}

	public String getPurificationSubmissionMessage() {
		return purificationSubmissionMessage;
	}

	public void setPurificationSubmissionMessage(String purificationComments) {
		this.purificationSubmissionMessage = purificationComments;
	}

	public String getScreenPanelSubmissionMessage() {
		return screenPanelSubmissionMessage;
	}

	public void setScreenPanelSubmissionMessage(String screenPanelComments) {
		this.screenPanelSubmissionMessage = screenPanelComments;
	}

	public String getCompoundManagementRegistrationSubmissionMessage() {
		return compoundManagementRegistrationSubmissionMessage;
	}

	public void setCompoundManagementRegistrationSubmissionMessage(String compoundManagementRegistrationSubmissionMessage) {
		this.compoundManagementRegistrationSubmissionMessage = compoundManagementRegistrationSubmissionMessage;
	}

	public boolean isEditable() {
		if (compoundManagementRegistrationSubmissionStatus != null && compoundManagementRegistrationSubmissionStatus.equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED))
			return false;
		else
			return true;
	}

	public int compareTo(ProductPlate arg0) {
		ProductPlate tempPlate = arg0;
		if (tempPlate.getLotNo().equals(CeNConstants.PLATE_TYPE_PRODUCT_PSEUDO))
			return 1;
		return this.getLotNo().compareTo(tempPlate.getLotNo());
	}

	public void deepCopy(ProductPlate src) {
		if (src.key.equals(this.key))//This is called from deepClone().
			setWells(src.getAllModifedWellsClone());
		else
			setWells(src.getAllWellsCopy());
		setContainer(src.getContainer());
		setPlateNumber(src.getPlateNumber());
		setRegisteredDate(src.getRegisteredDate());
		setPlateType(src.getPlateType());
		setPlateComments(src.getPlateComments());
		setSelect(src.select);
		setStepNumber(src.stepNumber);
		setRackPlateFlag(src.rackPlateFlag);
		setPlateBarCode(src.getPlateBarCode());
		setProjectTrackingCode(src.getProjectTrackingCode());
		setLoadedFromDB(src.isLoadedFromDB());
		setModelChanged(src.modelChanged);
		setCompoundManagementRegistrationSubmissionStatus(src.getCompoundManagementRegistrationSubmissionStatus());
	}

	public String getProjectTrackingCode() {
		return projectTrackingCode;
	}

	public void setProjectTrackingCode(String projectTrackingCode) {
		this.projectTrackingCode = projectTrackingCode;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}
	
	
}
