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
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellEditor;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCeNTableModelConnectorCommon {
	
	private static final Log log = LogFactory.getLog(PCeNTableModelConnectorCommon.class);
	
	private NotebookPageModel pageModel = null;

	public PCeNTableModelConnectorCommon(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}

	public PCeNTableModelConnectorCommon() {
	}

	public boolean updateColumn(List<? extends BatchModel> batches, String columnName, Object newValue) {
		boolean update = false;
		for (BatchModel batch : batches) {
			PlateWell<? extends BatchModel> genericWell = null;
			if (batch instanceof ProductBatchModel) {
				ProductBatchModel pBatch = (ProductBatchModel) batch;
				PlateWell<ProductBatchModel> well = null;
				if (!CommonUtils.getProductBatchModelEditableFlag(pBatch, pageModel)) {
					if (!(columnName.equals(PCeNTableView.AMT_IN_WELL_WT) || 
					      columnName.equals(PCeNTableView.AMT_IN_VIAL_WT) ||
					      columnName.equals(PCeNTableView.AMT_IN_WELL_VOL) || 
					      columnName.equals(PCeNTableView.AMT_IN_WELL_MOLES) || 
					      columnName.equals(PCeNTableView.WELL_SOLVENT) ||
					      columnName.equals(PCeNTableView.SELECT_OPTION))) {
						continue;
					}
				}
				if (columnName.equals(PCeNTableView.SELECT_OPTION)) {
					if (newValue.equals("true") || newValue.equals("false")) {						
						pBatch.setSelected(Boolean.valueOf(newValue.toString()).booleanValue());						
						update = true;
					}
					continue;
				}
				if (pageModel != null) {
					ProductPlate productPlate = pageModel.getAllProductBatchesAndPlatesMap(false).get(pBatch);
					if (productPlate != null) {
						List<PlateWell<ProductBatchModel>> wellsList = productPlate.getPlateWellsforBatch(pBatch);
						if (wellsList != null && wellsList.size() > 0) {
							well = wellsList.get(0);
						}
					}
				}
				
				if (columnName.equals(PCeNTableView.ANALYTICAL_PURITY)) {
					if (! (newValue instanceof ArrayList<?>)) {
						throw new IllegalArgumentException("Purity must be a list");
					}
					ArrayList<PurityModel> newPurityModelList = (ArrayList<PurityModel>) newValue;
					if (newPurityModelList.size() > 0) {
						pBatch.setAnalyticalPurityList(newPurityModelList);
						update = true;
					}
				} else if (columnName.equals(PCeNTableView.MELTING_POINT)) {
					if (! (newValue instanceof TemperatureRangeModel)) {
						throw new IllegalArgumentException("Melting point must be a valid temperature ranges.");
					}
					TemperatureRangeModel newTempRangeModel = (TemperatureRangeModel) newValue;
					if (newTempRangeModel != null) {
						pBatch.setMeltPointRange(newTempRangeModel);
						update = true;
					}
				} else if (columnName.equals(PCeNTableView.EXT_SUPPLIER)) {
					if (newValue == null) {
						pBatch.setVendorInfo(null);
						update = true;
					} else {
						if (! (newValue instanceof ExternalSupplierModel)) {
							throw new IllegalArgumentException("External Supplier must be a item");
						}
						ExternalSupplierModel externalSupplierModel = (ExternalSupplierModel)newValue;
						pBatch.setVendorInfo(externalSupplierModel);
						update = true;
					}
				} else if (columnName.equals(PCeNTableView.TOTAL_WEIGHT)) {
					if (! (newValue instanceof PAmountComponent) ) {
						throw new IllegalArgumentException("Total weight must be an amount");
					}
					AmountModel totalWeightAmountModel = ((PAmountComponent)newValue).getAmount();
					BatchAttributeComponentUtility.setTotalAmountMadeWeight(pBatch, totalWeightAmountModel, well);
					update = true;
				} else if (columnName.equals(PCeNTableView.TOTAL_VOLUME)) {
					if (! (newValue instanceof PAmountComponent) ) {
						throw new IllegalArgumentException("Total volume must be an amount");
					}
					AmountModel totalVolumeAmountModel = ((PAmountComponent)newValue).getAmount();
					BatchAttributeComponentUtility.setTotalAmountMadeVolume(pBatch, totalVolumeAmountModel, well);
					update = true;
				} else if (columnName.equals(PCeNTableView.STATUS)) {
					if (! (newValue instanceof String) ) {
						throw new IllegalArgumentException("Status must be a string");
					}
					if (BatchAttributeComponentUtility.setProductBatchStatus(pBatch, (String) newValue)) {
						update = true;
					}
				} else if (columnName.equals(PCeNTableView.COMPOUND_STATE)) {
					if (! (newValue instanceof String) ) {
						throw new IllegalArgumentException("Compound state must be a String");
					}
					BatchAttributeComponentUtility.setCompoundState(pBatch, (String) newValue);
					update = true;
				} 
				genericWell = well;
			} else if (batch instanceof MonomerBatchModel) {
				if (pageModel != null && pageModel.getBatchPlateWellsMap() != null) {
					HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> map  = pageModel.getBatchPlateWellsMap();
					ArrayList<PlateWell<? extends BatchModel>> wellsList = map.get(batch);
					if (wellsList != null && wellsList.size() > 0) {
						genericWell = wellsList.get(0);
					}
					MonomerBatchModel mBatch = (MonomerBatchModel) batch;
					if (columnName.equals(PCeNTableView.STATUS)) {
						if (! (newValue instanceof String) ) {
							throw new IllegalArgumentException("Status must be a string");
						}
						if (batch instanceof MonomerBatchModel) {
							BatchAttributeComponentUtility.propagateMonomerBatchStatus(mBatch, newValue.toString(), pageModel);
							mBatch.setStatus(newValue.toString());
							update = true;
						}
					} else if (columnName.equals(PCeNTableView.DELIVERED_WEIGHT)) {
						if (! (newValue instanceof PAmountComponent) ) {
							throw new IllegalArgumentException("Delivered weight must be an amount");
						}
						if (BatchAttributeComponentUtility.setDeliveredWeight(mBatch, ((PAmountComponent)newValue).getAmount())) {
							update = true;
						}
					}  else if (columnName.equals(PCeNTableView.DENSITY)) {
						if (! (newValue instanceof PAmountComponent) ) {
							throw new IllegalArgumentException("Density must be an amount");
						}
						if (BatchAttributeComponentUtility.setDensity(mBatch, ((PAmountComponent)newValue).getAmount())) {
							update = true;
						}
					} else if (columnName.equals(PCeNTableView.DELIVERED_MOLES)) {
						if (! (newValue instanceof PAmountComponent) ) {
							throw new IllegalArgumentException("Delivered moles must be an amount");
						}
						if (BatchAttributeComponentUtility.setDeliveredMoles(mBatch, ((PAmountComponent)newValue).getAmount())) {
							update = true;
						}
					} 
				}
			}
			if (columnName.equals(PCeNTableView.SALT_CODE)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Salt code must be a String");
				}
				BatchAttributeComponentUtility.setSaltCode(batch, newValue.toString());
				update = true;
				if (SaltFormModel.isParentCode(batch.getSaltForm().getCode())) {
					batch.setSaltEquivs(0.0);
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.SALT_EQ)) {
				if (SaltFormModel.isParentCode(batch.getSaltForm().getCode())) {
					batch.setSaltEquivs(0.0);
					update = true;
				} else if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Salt Equivs must be an amount");
				} else {
					try {
						BatchAttributeComponentUtility.setSaltEquiv(batch, ((PAmountComponent) newValue).getAmount());
						update = true;
					} catch (IllegalArgumentException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(e);
						log.error("Problem setting salt equivalents for batch to: " + newValue.toString(), e);
					} catch (CodeTableCacheException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(e);
						log.error("Problem setting salt code for batch to: " + newValue.toString(), e);
					}
				}
			} else if (columnName.equals(PCeNTableView.PURITY)) {
				if(newValue instanceof PAmountCellEditor) {
					if (BatchAttributeComponentUtility.setPurity(batch, ((PAmountComponent) newValue).getAmount())) {
						update = true;
					}
				}
			} else if (columnName.equals(PCeNTableView.EXTRA_NEEDED)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Extra needed must be an amount");
				}
				AmountModel amountModel = ((PAmountComponent)newValue).getAmount();
				((MonomerBatchModel) batch).setExtraNeeded(amountModel);
				update = true;
			} else if (columnName.equals(PCeNTableView.TIMES_USED)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Times used must be a string");
				}
				((MonomerBatchModel) batch).setNoOfTimesUsed(Integer.parseInt((String)newValue));
				update = true;
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_WT) || columnName.equals(PCeNTableView.AMT_IN_VIAL_WT)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Well weight must be an amount");
				}
				AmountModel wellWeightAmountModel = ((PAmountComponent)newValue).getAmount();
				BatchAttributeComponentUtility.setAmountInWellOrTubeWeight(genericWell, wellWeightAmountModel, batch);
				update = true;
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_VOL)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Well volume must be an amount");
				}
				AmountModel wellVolumeAmountModel = ((PAmountComponent)newValue).getAmount();
				BatchAttributeComponentUtility.setAmountInWellOrTubeVolume(genericWell, wellVolumeAmountModel, batch);
				update = true;
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_MOLES)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Well moles must be an amount");
				}
				if (BatchAttributeComponentUtility.setAmountInWellOrTubeMoles(genericWell, ((PAmountComponent)newValue).getAmount(), batch)) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.WELL_SOLVENT)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Well solvent must be a string");
				}
				String wellSolventCode = BatchAttributeComponentUtility.getWellSolventCodeFromDecr(newValue.toString());
				genericWell.setSolventCode(wellSolventCode);
				update = true;
			} else if (columnName.equals(PCeNTableView.PARENT_MW)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Parent molecular weight must be an amount");
				}
				if (BatchAttributeComponentUtility.setParentMolWeight(batch, ((PAmountComponent) newValue).getAmount())) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.BATCH_MW)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Extra needed must be an amount");
				}
				if (BatchAttributeComponentUtility.setMolWeight(batch, ((PAmountComponent)newValue).getAmount())) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.RXN_WEIGHT)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("RxN weight must be an amount");
				}
				if (BatchAttributeComponentUtility.setRxNWeight(batch, ((PAmountComponent)newValue).getAmount(), null)) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.RXN_VOLUME)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("RxN volume must be an amount");
				}
				if (BatchAttributeComponentUtility.setRxNVolume(batch, ((PAmountComponent)newValue).getAmount())) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.RXN_MOLES)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("RxN moles must be an amount");
				}
				if (BatchAttributeComponentUtility.setRxNMoles(batch, ((PAmountComponent)newValue).getAmount())) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.RXN_EQ)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("RxN eq must be an amount");
				}
				if (BatchAttributeComponentUtility.setRxNEQ(batch, ((PAmountComponent)newValue).getAmount())) {
					update = true;
				}
			}
			else if (columnName.equals(PCeNTableView.LIMITING)) {
				if (newValue.equals("true") || newValue.equals("false")) {
					batch.setLimiting(Boolean.valueOf(newValue.toString()).booleanValue());
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.COMMENTS)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Comments must be a string");
				}
				batch.setComments((String) newValue);
				update = true;
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_MOLARITY)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Well Molarity must be an amount");
				}
				AmountModel wellMolarityAmountModel = ((PAmountComponent)newValue).getAmount();
				BatchAttributeComponentUtility.setAmountInWellOrTubeMolarity(genericWell, wellMolarityAmountModel);
			} else if (columnName.equals(PCeNTableView.TOTAL_MOLES)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Total moles must be an amount");
				}
				if (BatchAttributeComponentUtility.setTotalMoles(batch, ((PAmountComponent)newValue).getAmount(), genericWell)) {
					update = true;
				}
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_MOLES)) {
				if (! (newValue instanceof PAmountComponent) ) {
					throw new IllegalArgumentException("Well moles must be an amount");
				}
				AmountModel wellMolarityAmountModel = ((PAmountComponent)newValue).getAmount();
				BatchAttributeComponentUtility.setAmountInWellOrTubeMoles(genericWell, wellMolarityAmountModel, batch);
				update = true;
			} else if (columnName.equals(PCeNTableView.EXPTAB_HAZARD_COMMENTS)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Comments must be a string");
				}
				batch.setHazardComments((String) newValue);
				update = true;
			} else if (columnName.equals(PCeNTableView.HAZARD_COMMENTS)) {
				if (! (newValue instanceof ProductBatchModel) ) {
					throw new IllegalArgumentException("Hazard comments must be a string");
				}
				ProductBatchModel associatedBatch = (ProductBatchModel)newValue;
				
				ArrayList<String> hazardCodesList = new ArrayList<String>(associatedBatch.getRegInfo().getCompoundRegistrationHazardCodes());
				((ProductBatchModel)batch).getRegInfo().setCompoundRegistrationHazardCodes(hazardCodesList);
				
				String hazardComments = associatedBatch.getHazardComments();
				((ProductBatchModel)batch).setHazardComments(hazardComments);
				
				String parentHazardComments = associatedBatch.getCompound().getHazardComments(); 
				((ProductBatchModel)batch).getCompound().setHazardComments(parentHazardComments);
				update = true;
			} else if (columnName.equals(PCeNTableView.HANDLING_PRECAUTIONS)) {
				if (! (newValue instanceof ProductBatchModel) ) {
					throw new IllegalArgumentException("Handling precautions must be a string");
				}
				ProductBatchModel associatedBatch = (ProductBatchModel)newValue;
				
				ArrayList<String> handlingCodesList = new ArrayList<String>(associatedBatch.getRegInfo().getCompoundRegistrationHandlingCodes());
				((ProductBatchModel)batch).getRegInfo().setCompoundRegistrationHandlingCodes(handlingCodesList);
				
				String handlingPrecations = associatedBatch.getHandlingComments();
				((ProductBatchModel)batch).setHandlingComments(handlingPrecations);
				
				update = true;
			} else if (columnName.equals(PCeNTableView.STORAGE_INSTRUCTIONS)) {
				if (! (newValue instanceof ProductBatchModel) ) {
					throw new IllegalArgumentException("Storage instructions must be a string");
				}
				ProductBatchModel associatedBatch = (ProductBatchModel)newValue;
				
				ArrayList<String> storageCodesList = new ArrayList<String>(associatedBatch.getRegInfo().getCompoundRegistrationStorageCodes());
				((ProductBatchModel)batch).getRegInfo().setCompoundRegistrationStorageCodes(storageCodesList);
				
				String storageComments = associatedBatch.getStorageComments();
				((ProductBatchModel)batch).setStorageComments(storageComments);
				
				update = true;			
			} else if (columnName.equals(PCeNTableView.SOURCE)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Compound source must be a String");
				}
				BatchAttributeComponentUtility.setCompoundSource(batch, (String) newValue);
				update = true;
			} else if (columnName.equals(PCeNTableView.SOURCE_DETAILS)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Compound source detail must be a String");
				}
				BatchAttributeComponentUtility.setCompoundSourceDetails(batch, newValue);
				update = true;
			} else if (columnName.equals(PCeNTableView.COMPOUND_PROTECTION)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Compound protection must be a String");
				}
				((ProductBatchModel)batch).setProtectionCode(BatchAttributeComponentUtility.getCode(newValue.toString()));
				update = true;
			} else if (columnName.equals(PCeNTableView.STEREOISOMER)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Compound state must be a String");
				}
				batch.getCompound().setStereoisomerCode((String) newValue);
				update = true;
			} else if (columnName.equals(PCeNTableView.STRUCTURE_COMMENTS)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Structure comments must be a String");
				}
				batch.getCompound().setStructureComments((String) newValue);
				update = true;
			} else if (columnName.equals(PCeNTableView.PRECURSORS)) {
				if (! (newValue instanceof String) ) {
					throw new IllegalArgumentException("Precursors must be a String");
				}
				if(batch instanceof ProductBatchModel) {
					ArrayList<String> precursors = pageModel.processSingletonPrecursorsUpdate((String)newValue, ((ProductBatchModel)batch));
					((ProductBatchModel)batch).setReactantBatchKeys(precursors);
				}
				update = true;
			}
			batch.setModelChanged(true);
		}
		return update;
	}
}
