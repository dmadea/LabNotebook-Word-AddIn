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
package com.chemistry.enotebook.client.gui.page.batch.table;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.regis_submis.RegSubHandler;
import com.chemistry.enotebook.client.gui.page.table.PCeNPlateTableRowSelectionChangeHandler;
import com.chemistry.enotebook.client.gui.page.table.PCeNProductTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnectorCommon;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PCeNSingleProductPlateTableModelConnector extends
		PCeNAbstractTableModelConnector implements
		PCeNPlateTableRowSelectionChangeHandler, PCeNProductTableModelConnector {
	public static final Log log = LogFactory
			.getLog(PCeNSingleProductPlateTableModelConnector.class);
	// private PlateWell well;
	private List<PlateWell<ProductBatchModel>> allWells;
	private List<ProductBatchModel> batches; // , listOfBatchesWithMolStrings;
	// private boolean structureHidden = false;
	// private ProductBatch workingBatch;
	// private HashMap productProperties;
	private ProductPlate productPlate;
	private NotebookPageModel pageModel = null;
	private LinkedHashMap batchesToMolStringsMap = new LinkedHashMap();
	// private List allProductPlates = new ArrayList();
	protected String[] headerNames;
	protected boolean structureHidden = false;
	private boolean isPseudoPlate = false;
	private boolean inTable = false;
	private HashMap<PlateWell<ProductBatchModel>, ProductPlate> WellsAndPlatesMap = null;
	// private HashMap<BatchModel, PlateWell> batchesAndWellsMap = null;
	// column prefs
	private List columnPreferences;
	private TablePreferenceDelegate preferenceDelegate;
	private String tableName = "Product_Plates_Table";
	private String[] columnNamesForPlate = { PCeNTableView.STRUCTURE,
			PCeNTableView.NBK_BATCH_NUM, PCeNTableView.WELL_POSITION,
			PCeNTableView.TOTAL_WEIGHT, PCeNTableView.TOTAL_VOLUME,
			PCeNTableView.TOTAL_MOLES, PCeNTableView.AMT_IN_WELL_WT,
			PCeNTableView.AMT_IN_WELL_VOL, PCeNTableView.AMT_IN_WELL_MOLARITY,
			PCeNTableView.AMT_IN_WELL_MOLES, PCeNTableView.WELL_SOLVENT,
			PCeNTableView.AMT_REMAINING_WEIGHT,
			PCeNTableView.AMT_REMAINING_VOLUME,
			PCeNTableView.THEORETICAL_WEIGHT, PCeNTableView.THEORETICAL_MMOLES,
			PCeNTableView.PERCENT_YIELD, PCeNTableView.COMPOUND_STATE,
			PCeNTableView.SALT_CODE, PCeNTableView.SALT_EQ,
			PCeNTableView.ANALYTICAL_PURITY, PCeNTableView.MELTING_POINT,
			PCeNTableView.MOL_WEIGHT, PCeNTableView.MOL_FORMULA,
			PCeNTableView.CONVERSATIONAL_BATCH, PCeNTableView.VIRTUAL_COMP_NUM,
			PCeNTableView.STEREOISOMER, PCeNTableView.BARCODE,
			PCeNTableView.STATUS, PCeNTableView.SOURCE,
			PCeNTableView.SOURCE_DETAILS, PCeNTableView.EXT_SUPPLIER,
			PCeNTableView.COMMENTS, PCeNTableView.PRECURSORS,
			PCeNTableView.HAZARD_COMMENTS, PCeNTableView.COMPOUND_PROTECTION,
			PCeNTableView.STRUCTURE_COMMENTS, };
	private String[] columnNamesForPseudoPlate = { PCeNTableView.STRUCTURE,
			PCeNTableView.NBK_BATCH_NUM, PCeNTableView.TOTAL_WEIGHT,
			PCeNTableView.TOTAL_VOLUME, PCeNTableView.TOTAL_MOLES,
			PCeNTableView.AMT_IN_VIAL_WT, PCeNTableView.AMT_REMAINING_WEIGHT,
			PCeNTableView.AMT_REMAINING_VOLUME,
			PCeNTableView.THEORETICAL_WEIGHT, PCeNTableView.THEORETICAL_MMOLES,
			PCeNTableView.PERCENT_YIELD, PCeNTableView.COMPOUND_STATE,
			PCeNTableView.SALT_CODE, PCeNTableView.SALT_EQ,
			PCeNTableView.ANALYTICAL_PURITY, PCeNTableView.MELTING_POINT,
			PCeNTableView.MOL_WEIGHT, PCeNTableView.MOL_FORMULA,
			PCeNTableView.CONVERSATIONAL_BATCH, PCeNTableView.VIRTUAL_COMP_NUM,
			PCeNTableView.STEREOISOMER, PCeNTableView.BARCODE,
			PCeNTableView.STATUS, PCeNTableView.SOURCE,
			PCeNTableView.SOURCE_DETAILS, PCeNTableView.EXT_SUPPLIER,
			PCeNTableView.COMMENTS, PCeNTableView.PRECURSORS,
			PCeNTableView.HAZARD_COMMENTS, PCeNTableView.COMPOUND_PROTECTION,
			PCeNTableView.STRUCTURE_COMMENTS };

	public PCeNSingleProductPlateTableModelConnector(ProductPlate plate,
	                                                 NotebookPageModel pageModel) {
		// batches = batchesList.getBatchModels();
		super(pageModel);
		productPlate = plate;
		// allProductPlates.add(plate);
		this.pageModel = pageModel;
		updateLocalCaches();
		if (batches == null)
			throw new IllegalArgumentException(
					"ParallelCeNProductBatchTableViewControllerUtility received NULL argument list");
		if (batches.size() > 0) { // vb 7/15 this throws an exception if all
									// batches are assigned to plates
			// Don't assume there is a batch
//			batchModel = batches.get(0);
			// not used workingBatch = new ProductBatch(batchModel);
		}
		// not used workingBatch = new ProductBatch(batchModel);
		// productProperties = workingBatch.getBatchProperties();
		createHeaderNames();
		// col prefs
		try {
			if (this.isPseudoPlate)
				headerNames = columnNamesForPseudoPlate;
			else
				headerNames = columnNamesForPlate;
			if (this.isPseudoPlate)
				tableName = tableName + "_Pseudo_Plate";
			else
				tableName = tableName + "_Product_Plate";
			preferenceDelegate = new TablePreferenceDelegate(tableName,
					this.pageModel, headerNames);
			columnPreferences = preferenceDelegate.getColumnPreferences();
		} catch (UserPreferenceException e) {
			log.error("Unable to set table preferences : " + e.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(
					MasterController.getGUIComponent(),
					"Unable to set table preferences.", e);
		}
	}

	private void updateLocalCaches() {
		allWells = new ArrayList<PlateWell<ProductBatchModel>>();
		batches = new ArrayList<ProductBatchModel>();
		WellsAndPlatesMap = new HashMap();
		//batchesAndWellsMap = new HashMap<BatchModel, PlateWell>();
		if (productPlate != null){
		PlateWell<ProductBatchModel> plateWells[] = productPlate.getWells();
		
		if (plateWells != null)
			for (int m = 0; m < plateWells.length; m++) {
				if (plateWells[m].getBatch() != null)
				{
					allWells.add(plateWells[m]);
					batches.add(plateWells[m].getBatch());
					//batchesAndWellsMap.put(plateWells[m].getBatch(), plateWells[m]);
				}
				WellsAndPlatesMap.put(productPlate.getWells()[m], productPlate);
			}	
/*		if (batches != null)
			Collections.sort(batches, new NbkBatchNumberComparator());
*/		//Add wells based on the order of batches.
/*		for (int i=0; i<batches.size(); i++)
		{
			allWells.add(batchesAndWellsMap.get(batches.get(i)));
		}*/
		
		
		batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, pageModel);
		if (productPlate instanceof PseudoProductPlate)
			isPseudoPlate = true;
		else
			isPseudoPlate = false;
		}
	}

	public void updateProductPlate(ProductPlate productPlate) {
		this.productPlate = productPlate;
		this.updateLocalCaches();
	}

	private List setDefaultColumnPrefs() {
		List columnPrefs = new ArrayList();
		for (int i = 0; i < headerNames.length; i++) {
			String name = headerNames[i];
			TableColumnInfo colInfo = new TableColumnInfo();
			colInfo.setColumnIndex(i);
			colInfo.setModelIndex(i);
			colInfo.setPreferredWidth(70); // ignore???
			if (name.equals(PCeNTableView.SALT_CODE)
					|| name.equals(PCeNTableView.SALT_EQ)
					|| name.equals(PCeNTableView.TIMES_USED)
					|| name.equals(PCeNTableView.PLATE)
					|| name.equals(PCeNTableView.WELL_POSITION)
					|| name.equals(PCeNTableView.LIMITING)
					|| name.equals(PCeNTableView.PARENT_MW)
					|| name.equals(PCeNTableView.DELIVERED_VOLUME)
					|| name.equals(PCeNTableView.RXN_VOLUME)
					|| name.equals(PCeNTableView.RXN_EQ)
					|| name.equals(PCeNTableView.DEAD_VOLUME)
					|| name.equals(PCeNTableView.DENSITY)
					|| name.equals(PCeNTableView.MOLARITY)
					|| name.equals(PCeNTableView.PURITY)
					|| name.equals(PCeNTableView.EXPTAB_HAZARD_COMMENTS)
					|| name.equals(PCeNTableView.COMMENTS))
				colInfo.setVisible(false);
			else
				colInfo.setVisible(true);
			columnPrefs.add(colInfo);
		}
		return columnPrefs;
	}

	protected void createHeaderNames() {
		int index = 0;
		if (!structureHidden) {
			if (isPseudoPlate)
				headerNames = new String[31];
			else
				headerNames = new String[36];
			headerNames[index] = PCeNTableView.STRUCTURE;
			index++;
		} else if (isPseudoPlate)
			headerNames = new String[29];
		else
			headerNames = new String[35];

		headerNames[index] = PCeNTableView.NBK_BATCH_NUM;
		index++;
		if (!isPseudoPlate) {
			headerNames[index] = PCeNTableView.WELL_POSITION;
			index++;
			// headerNames[index] = PCeNTableView.PLATE; vb 7/15
			// index++;
		}
		headerNames[index] = PCeNTableView.TOTAL_WEIGHT;
		index++;
		headerNames[index] = PCeNTableView.TOTAL_VOLUME;
		index++;
		headerNames[index] = PCeNTableView.TOTAL_MOLES;
		index++;
		if (!isPseudoPlate) {
			headerNames[index] = PCeNTableView.AMT_IN_WELL_WT;
			index++;
			headerNames[index] = PCeNTableView.AMT_IN_WELL_VOL;
			index++;
			headerNames[index] = PCeNTableView.AMT_IN_WELL_MOLARITY;
			index++;
			headerNames[index] = PCeNTableView.AMT_IN_WELL_MOLES;
			index++;
			headerNames[index] = PCeNTableView.WELL_SOLVENT;
			index++;
		} else {
			headerNames[index] = PCeNTableView.AMT_IN_VIAL_WT;
			index++;
		}
		headerNames[index] = PCeNTableView.AMT_REMAINING_WEIGHT;
		index++;
		headerNames[index] = PCeNTableView.AMT_REMAINING_VOLUME;
		index++;
		headerNames[index] = PCeNTableView.THEORETICAL_WEIGHT;
		index++;
		headerNames[index] = PCeNTableView.THEORETICAL_MMOLES;
		index++;
		headerNames[index] = PCeNTableView.PERCENT_YIELD;
		index++;
		headerNames[index] = PCeNTableView.COMPOUND_STATE;
		index++;
		headerNames[index] = PCeNTableView.SALT_CODE;
		index++;
		headerNames[index] = PCeNTableView.SALT_EQ;
		index++;
		headerNames[index] = PCeNTableView.ANALYTICAL_PURITY;
		index++;
		headerNames[index] = PCeNTableView.MELTING_POINT;
		index++;
		headerNames[index] = PCeNTableView.MOL_WEIGHT;
		index++;
		headerNames[index] = PCeNTableView.MOL_FORMULA;
		index++;
		headerNames[index] = PCeNTableView.CONVERSATIONAL_BATCH;
		index++;
		headerNames[index] = PCeNTableView.VIRTUAL_COMP_NUM;
		index++;
		headerNames[index] = PCeNTableView.STEREOISOMER;
		index++;
		headerNames[index] = PCeNTableView.BARCODE;
		index++;
		headerNames[index] = PCeNTableView.STATUS;
		index++;
		headerNames[index] = PCeNTableView.SOURCE;
		index++;
		headerNames[index] = PCeNTableView.SOURCE_DETAILS;
		index++;
		headerNames[index] = PCeNTableView.EXT_SUPPLIER;
		index++;
		headerNames[index] = PCeNTableView.COMMENTS;
		index++;
		headerNames[index] = PCeNTableView.PRECURSORS;
		index++;
		headerNames[index] = PCeNTableView.HAZARD_COMMENTS;
		index++;
		headerNames[index] = PCeNTableView.COMPOUND_PROTECTION;
		index++;
		headerNames[index] = PCeNTableView.STRUCTURE_COMMENTS;
		index++;
	}

	public String[] getHeaderNames() {
		return headerNames;
	}

	public boolean isCellEditable(int rowIndex, int colIndex) {
		if (headerNames[colIndex].equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM)
				|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.STATUS)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.COMMENTS)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.AMT_IN_WELL_WT)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.AMT_IN_VIAL_WT)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.AMT_IN_WELL_VOL)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.AMT_IN_WELL_MOLARITY)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.WELL_SOLVENT)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.AMT_IN_WELL_MOLES)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.TOTAL_MOLES)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.TOTAL_WEIGHT)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.TOTAL_VOLUME)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.COMPOUND_STATE)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.SALT_CODE)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.SALT_EQ)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.ANALYTICAL_PURITY)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.MELTING_POINT)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.BARCODE)
				|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.SOURCE)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.SOURCE_DETAILS)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.COMMENTS)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.COMPOUND_PROTECTION)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.EXT_SUPPLIER)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.HAZARD_COMMENTS)
				|| headerNames[colIndex]
						.equalsIgnoreCase(PCeNTableView.STRUCTURE_COMMENTS)) {
			PlateWell well = null;
			ProductBatchModel batch = null;
			if (allWells.size() > rowIndex) {
				well = (PlateWell) allWells.get(rowIndex);
				batch = (ProductBatchModel) well.getBatch();
			} else
				batch = (ProductBatchModel) batches.get(rowIndex);

			if (headerNames[colIndex].equals(PCeNTableView.SALT_EQ)) {
				if (batch.getSaltForm().getCode().indexOf("00") >= 0) {
					return false;
				}
			} else if (headerNames[colIndex]
					.equals(PCeNTableView.AMT_IN_WELL_WT)
					|| headerNames[colIndex]
							.equals(PCeNTableView.AMT_IN_WELL_VOL)
					|| headerNames[colIndex]
							.equals(PCeNTableView.AMT_IN_WELL_MOLARITY)
					|| headerNames[colIndex].equals(PCeNTableView.WELL_SOLVENT)) {
				if (well != null) {
					return ((ProductPlate) WellsAndPlatesMap.get(well))
							.isEditable();
				}
			}

			if (CommonUtils.getProductBatchModelEditableFlag(batch, pageModel))
				return true;
			else
				return false;
		} else
			return false;
	}

	public PlateWell<ProductBatchModel> getPlateWell(int rowIndex) {
		return (PlateWell<ProductBatchModel>) allWells.get(rowIndex);
	}

	public Object getValue(int rowIndex, int colIndex) {
		PlateWell<ProductBatchModel> well = null;
		ProductBatchModel batch = null;
		if (!isPseudoPlate) {
			if (allWells.size() > rowIndex) {
				well = allWells.get(rowIndex);
				//In ParallelExpModeUtils.linkPlateWellBatchToCorrespondingBatch() ProdBatchModel with null key is added
				//  to handle the save issues for empty/skipped well. Refere to the comments there.
				if (well.getBatch() == null || well.getBatch().getKey() == null)
				{
					return "";
				}
				batch = well.getBatch();
			}
		} else {
			batch = (ProductBatchModel) this.batches.get(rowIndex);
			// System.out.println("batch:" + batch.getBatchNumber());
			productPlate = (ProductPlate) pageModel
					.getAllProductBatchesAndPlatesMap(false).get(batch);
			List wellsList = productPlate.getPlateWellsforBatch(batch);
			if (wellsList.size() > 0)
				well = (PlateWell) wellsList.get(0);
		}
		if (batch == null)
			return "";
		if (!(batch instanceof ProductBatchModel))
			BatchAttributeComponentUtility.setDefaultTheoreticalAmounts(batch,
					pageModel);
		if (headerNames[colIndex].equals(PCeNTableView.STRUCTURE)) {
			return batchesToMolStringsMap.get(batch.getKey());
		} else if (headerNames[colIndex].equals(PCeNTableView.NBK_BATCH_NUM)) {
			return BatchAttributeComponentUtility.getNotebookBatchNumber(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_WEIGHT)) {
			return BatchAttributeComponentUtility
					.getTotalAmountMadeWeight(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_VOLUME)) {
			return BatchAttributeComponentUtility
					.getTotalAmountMadeVolume(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_MADE)) {
			AmountModel amodel = null;

			if (!isPseudoPlate) {
				List wells = ((ProductPlate) productPlate)
						.getPlateWellsforBatch(batch);
				if (wells.size() == 1) {
					amodel = BatchAttributeComponentUtility
							.getAmountInWellOrTubeWeight((PlateWell) wells
									.get(0));
				}/*
				 * else { double totalAmount = 0.0; for (int i = 0; i <
				 * wells.size(); i++) { PlateWell w = ((PlateWell)
				 * wells.get(i)); totalAmount +=
				 * w.getContainedWeightAmount().GetValueInStdUnitsAsDouble(); }
				 * }
				 */
			}
			return amodel;
		} else if (headerNames[colIndex].equals(PCeNTableView.WELL_POSITION)) {
			return well.getWellNumber();
		} else if (headerNames[colIndex].equals(PCeNTableView.PLATE)) {
			return productPlate.getPlateBarCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.MOL_WEIGHT)) {
			double d = batch.getMolecularWeightAmount()
					.GetValueInStdUnitsAsDouble();
			NumberFormat nf = NumberFormat.getInstance();
			nf
					.setMaximumFractionDigits(CeNGUIConstants.BATCH_WT_DECIMAL_FIXED_FIGS);
			return nf.format(d);
			// return "" + batch.getMolecularWeightAmount().getValue();
		} else if (headerNames[colIndex].equals(PCeNTableView.MOL_FORMULA)) {
			return batch.getMolecularFormula();
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.THEORETICAL_WEIGHT)) {
			return batch.getTheoreticalWeightAmount();
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.THEORETICAL_MMOLES)) {
			return batch.getTheoreticalMoleAmount();
		} else if (headerNames[colIndex].equals(PCeNTableView.PERCENT_YIELD)) {
			if (batch.getTheoreticalYieldPercentAmount()
					.GetValueInStdUnitsAsDouble() < 0)
				return new AmountModel(UnitType.SCALAR);
			else
				return batch.getTheoreticalYieldPercentAmount().deepClone();
			// return
			// (batch.getTheoreticalYieldPercentAmount().GetValueInStdUnitsAsDouble()
			// < 0 ? "" :
			// batch.getTheoreticalYieldPercentAmount().GetValueInStdUnitsAsDouble()
			// + "");
		} else if (headerNames[colIndex].equals(PCeNTableView.COMPOUND_STATE)) {
			return (batch.getCompoundState() == null ? "" : batch
					.getCompoundState());
		} else if (headerNames[colIndex].equals(PCeNTableView.SALT_CODE)) {
			return batch.getSaltForm().getCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.SALT_EQ)) {
			AmountModel amt = new AmountModel(UnitType.SCALAR);
			amt.setValue(batch.getSaltEquivs());
			return amt;
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.ANALYTICAL_PURITY)) {
			List purityModels = batch.getAnalyticalPurityList();
			String purityStr = BatchAttributeComponentUtility
					.getPuritiesListAsHTMLString(purityModels);
			/*
			 * for (Iterator it = purityModels.iterator(); it.hasNext();) {
			 * PurityModel model = (PurityModel) it.next();
			 */// Enforcement of Purity selection is made in the Panel itself.
				// So, this check is not required.
			// if (model.isRepresentativePurity()) {
			// purityStr = model.toString();
			// }
			// }
			return purityStr;
		} else if (headerNames[colIndex].equals(PCeNTableView.MELTING_POINT)) {
			return batch.getMeltPointRange().toString();
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_MOLES)) {
			return BatchAttributeComponentUtility.getTotalMoles(batch);
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.AMT_REMAINING_WEIGHT)) {
			return BatchAttributeComponentUtility.getWeightRemaining(
					productPlate, batch);
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.AMT_REMAINING_VOLUME)) {
			return BatchAttributeComponentUtility.getVolumeRemaining(
					productPlate, batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_IN_WELL_WT)) {
			return BatchAttributeComponentUtility
					.getAmountInWellOrTubeWeight(well);
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_IN_WELL_VOL)) {
			return BatchAttributeComponentUtility
					.getAmountInWellOrTubeVolume(well);
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.AMT_IN_WELL_MOLARITY)) {
			return BatchAttributeComponentUtility.getAmountInWellMolarity(well);
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.AMT_IN_WELL_MOLES)) {
			return BatchAttributeComponentUtility.getAmountInWellOrTubeMoles(
					well, batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_IN_VIAL_WT)) {
			return BatchAttributeComponentUtility
					.getAmountInWellOrTubeWeight(well);
		} else if (headerNames[colIndex].equals(PCeNTableView.WELL_SOLVENT)) {
			return BatchAttributeComponentUtility
					.getWellSolventDecrFromCode(well.getSolventCode());
		} else if (headerNames[colIndex].equals(PCeNTableView.EXT_SUPPLIER)) {
			// External supplier
			if (batch.getVendorInfo() != null) {
				String externalSupplierCode = batch.getVendorInfo().getCode();
				String externalSupplierDescription = batch.getVendorInfo()
						.getDescription();
				if (externalSupplierCode != null
						&& externalSupplierCode.length() > 0) {
					StringBuffer buff = new StringBuffer(externalSupplierCode);
					if (externalSupplierDescription != null
							&& externalSupplierCode.length() > 0) {
						buff.append(" - ").append(externalSupplierDescription);
						return buff.toString();
					}
				} else
					return "";
			} else
				return "";
		} else if (headerNames[colIndex].equals(PCeNTableView.CONVERSATIONAL_BATCH)) {
			String val = batch.getRegInfo().getConversationalBatchNumber();
			return (val == null ? "" : val);
		} else if (headerNames[colIndex].equals(PCeNTableView.VIRTUAL_COMP_NUM)) {
			String val = batch.getCompound().getVirtualCompoundId();
			return (val == null ? "" : val);
		} else if (headerNames[colIndex].equals(PCeNTableView.STEREOISOMER)) {
			return batch.getCompound().getStereoisomerCode().equals("null") ? ""
					: batch.getCompound().getStereoisomerCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.BARCODE)) {
				return batch.getBarCode();
				/*
				if (productPlate != null) {
					List wellsList = productPlate
							.getPlateWellsforBatch(batch);
					if (wellsList.size() > 0)
						well = (PlateWell) wellsList.get(0); // For this release
																// assume only one
																// well exists for
																// each Batch.
					return well.getBarCode();
				}
				return "";				
				*/
		} else if (headerNames[colIndex].equals(PCeNTableView.STATUS)) {
			return ProductBatchStatusMapper.getInstance().getStatusString(
					batch.getSelectivityStatus(), batch.getContinueStatus());
		} else if (headerNames[colIndex].equals(PCeNTableView.SOURCE)) {
			// return batch.getRegInfo().getCompoundSource();
			return BatchAttributeComponentUtility
					.getCompoundSourceDisplayValue(batch.getRegInfo()
							.getCompoundSource());
		} else if (headerNames[colIndex].equals(PCeNTableView.SOURCE_DETAILS)) {
			return BatchAttributeComponentUtility
					.getCompoundSourceDetailsDisplayValue(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.COMMENTS)) {
			return batch.getComments();
		} else if (headerNames[colIndex].equals(PCeNTableView.TA)) {
			return pageModel.getTaCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.PROJECT)) {
			return pageModel.getProjectCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.HAZARD_COMMENTS)) {
			StringBuffer buff = new StringBuffer();
			String hazards = RegSubHandler
					.getProductBatchModelHazardString((ProductBatchModel) batch);
			String handling = RegSubHandler
					.getProductBatchModelHandlingString((ProductBatchModel) batch);
			String storage = RegSubHandler
					.getProductBatchModelStorageString((ProductBatchModel) batch);
			if (hazards.length() > 0)
				buff.append(hazards).append("; ");
			if (handling.length() > 0)
				buff.append(handling).append("; ");
			if (storage.length() > 0)
				buff.append(storage);
			return buff.toString();
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.COMPOUND_PROTECTION)) {
			return BatchAttributeComponentUtility.getProtectionCode(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.PRECURSORS)) {
			return pageModel.getSingletonPrecursorsString(batch);
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.STRUCTURE_COMMENTS)) {
			return batch.getCompound().getStructureComments();
		}
		return batch.getBatchProperties().get(headerNames[colIndex]);
	}

	/*
	 * private ProductPlate getPlate(ProductBatchModel batch) { return
	 * this.productPlate; }
	 */
	public void setValue(Object value, int rowIndex, int colIndex) {
		if (inTable)
			return;
		if (value == null || value.toString().equals("")) {
			if (!headerNames[colIndex].equals(PCeNTableView.SOURCE) // Null
																	// allowed
																	// columns.
					|| !headerNames[colIndex]
							.equals(PCeNTableView.COMPOUND_STATE)
					|| !headerNames[colIndex]
							.equals(PCeNTableView.WELL_SOLVENT))
				return;
		}

		inTable = true;
		PlateWell well = null;
		ProductBatchModel pbatch = null;
		if (!isPseudoPlate) {
			if (allWells.size() > rowIndex) {
				well = (PlateWell) allWells.get(rowIndex);
				pbatch = (ProductBatchModel) well.getBatch();
			}
		} else {
			pbatch = (ProductBatchModel) batches.get(rowIndex);
			/*
			 * HashMap batchAndWellMap = pageModel.getBatchPlateWellsMap();
			 * ArrayList wellsList = (ArrayList) batchAndWellMap.get(pbatch);
			 * well = (PlateWell) wellsList.get(0);
			 */productPlate = (ProductPlate) pageModel
					.getAllProductBatchesAndPlatesMap(false).get(pbatch);
			List wellsList = productPlate.getPlateWellsforBatch(pbatch);
			if (wellsList.size() > 0)
				well = (PlateWell) wellsList.get(0);

		}

		boolean modelChanged = false;
		if (headerNames[colIndex].equals(PCeNTableView.NBK_BATCH_NUM)) {
			try {
				if (BatchAttributeComponentUtility.setNotebookBatchNumber(
						pbatch, (value + ""), pageModel))
					pbatch.setModified(true);
				enableSaveButton();
			} catch (InvalidBatchNumberException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Data Exception", JOptionPane.ERROR_MESSAGE);
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_WEIGHT)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setTotalAmountMadeWeight(
						pbatch, (AmountModel) value, well))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_VOLUME)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setTotalAmountMadeVolume(
						pbatch, (AmountModel) value, well))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_MOLES)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setTotalMoles(pbatch,
						(AmountModel) value, well))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_IN_WELL_WT)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setAmountInWellOrTubeWeight(
						well, (AmountModel) value, pbatch))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_IN_VIAL_WT)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setAmountInWellOrTubeWeight(
						well, (AmountModel) value, pbatch))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_IN_WELL_VOL)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setAmountInWellOrTubeVolume(
						well, (AmountModel) value, pbatch))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.AMT_IN_WELL_MOLARITY)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility
						.setAmountInWellOrTubeMolarity(well,
								(AmountModel) value))
					;
				{
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.AMT_IN_WELL_MOLES)) {
			if (value instanceof AmountModel) {
				if (BatchAttributeComponentUtility.setAmountInWellOrTubeMoles(
						well, (AmountModel) value, pbatch)) {
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.WELL_SOLVENT)) {
			if (value instanceof String) {
				well.setSolventCode(BatchAttributeComponentUtility
						.getWellSolventCodeFromDecr(value.toString()));
				modelChanged = true;
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.WELL_POSITION)) {
			well.setWellNumber(value.toString());
			modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.PLATE)) {
			productPlate.setPlateBarCode(value.toString());
			modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.COMPOUND_STATE)) {
			// pbatch.setCompoundState(value.toString());
			BatchAttributeComponentUtility.setCompoundState(pbatch, value
					.toString());
			modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.SALT_CODE)) {
			try {
				BatchAttributeComponentUtility.setSaltCode(pbatch, (String) value);
				modelChanged = true;
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.SALT_EQ)) {
			try {
				if (BatchAttributeComponentUtility.setSaltEquiv(pbatch, (AmountModel) value)) {
					modelChanged = true;
					// fireSaltEquiChanged(batch);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}

		}
		/*
		 * else if (headerNames[colIndex].equals(PCeNTableView.EXT_SUPPLIER)) {
		 * pbatch.getVendorInfo().setCode(value.toString()); modelChanged =
		 * true; }
		 */
		else if (headerNames[colIndex].equals(PCeNTableView.STATUS)) {

			if (BatchAttributeComponentUtility.setProductBatchStatus(pbatch, (String) value))
				modelChanged = true;
			/*
			 * int sStatus =
			 * ProductBatchStatusMapper.getInstance().getSelectivityStatus
			 * (status); int cStatus =
			 * ProductBatchStatusMapper.getInstance().getContinueStatus(status);
			 * pbatch.setSelectivityStatus(sStatus);
			 * pbatch.setContinueStatus(cStatus);
			 */modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.SOURCE)) {

			if (((ProductBatchModel) pbatch).getRegInfo().getCompoundSource() != null) {
				String code = "";
				try {
					code = CodeTableCache.getCache().getSourceCode(
							(String) value);
				} catch (CodeTableCacheException e) {
					e.printStackTrace();
				}
				if (((ProductBatchModel) pbatch).getRegInfo()
						.getCompoundSource().equals(code))
					return;
			}
			if (BatchAttributeComponentUtility.setCompoundSource(pbatch, value))
				modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.SOURCE_DETAILS)) {

			if (((ProductBatchModel) pbatch).getRegInfo()
					.getCompoundSourceDetail() != null
					&& ((ProductBatchModel) pbatch).getRegInfo()
							.getCompoundSourceDetail().equals(value))
				return;
			if (BatchAttributeComponentUtility.setCompoundSourceDetails(pbatch,
					value))
				modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.COMMENTS)) {
			pbatch.setComments(value.toString());
			modelChanged = true;
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.COMPOUND_PROTECTION)) {
			pbatch.setProtectionCode(BatchAttributeComponentUtility
					.getCode(value.toString()));
			modelChanged = true;
		} else if (headerNames[colIndex]
				.equals(PCeNTableView.STRUCTURE_COMMENTS)) {
			pbatch.getCompound().setStructureComments(value.toString());
			modelChanged = true;
		} else if (headerNames[colIndex].equals(PCeNTableView.BARCODE)) {
			pbatch.setBarCode(value.toString());
		    modelChanged = true;
		}
		// Do the following if anything changed
		if (modelChanged) {
			this.enableSaveButton();

		}
		inTable = false;
	}

	private void enableSaveButton() {
		pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
	}

	public boolean isStructureHidden() {
		return structureHidden;
	}

	/**
	 * @return the abstractBatches
	 */
	public List getAbstractBatches() {
		return batches;
	}

	/**
	 * @param abstractBatches
	 *            the abstractBatches to set
	 */
	public void setAbstractBatches(List abstractBatches) {
		this.batches = abstractBatches;
	}

	/**
	 * @return the listOfBatchesWithMolStrings
	 */
	public List getListOfBatchesWithMolStrings() {
		return batches;
	}

	/**
	 * @param listOfBatchesWithMolStrings
	 *            the listOfBatchesWithMolStrings to set
	 */
	public void setListOfBatchesWithMolStrings(List listOfBatchesWithMolStrings) {
		this.batches = listOfBatchesWithMolStrings;
	}

	public String getTableHeaderTooltip(String headerName) {
		return null;
	}

	public int getRowCount() {
		if (!isStructureHidden())
			return getListOfBatchesWithMolStrings().size();
		else {
			if (getAbstractBatches() == null)
				return 0;
			return getAbstractBatches().size();
		}

	}

	public boolean isSortable(int col) {
		return true;
	}

	// This method is not applicable here
	public List getStoicElementListInTransactionOrder() {
		return null;
	}

	private boolean confirmUpdateColumn(ArrayList<ProductBatchModel> failedBatches) {
		return ProblemBatchesDialog.confirmUpdateColumn(MasterController.getGUIComponent(), failedBatches);
	}
	
	public void updateColumn(String columnName, Object newValue) {
		List<ProductBatchModel> workingBatchesList = this.batches;
		if (newValue instanceof PAmountComponent) {
			ArrayList<ProductBatchModel> failedBatches = new ArrayList<ProductBatchModel>();
			ArrayList<ProductBatchModel> passedBatches = new ArrayList<ProductBatchModel>();
			ProductBatchModel tempBatch = null;
			double newValueInDouble = ((PAmountComponent) newValue).getAmount()
					.GetValueInStdUnitsAsDouble();
			if (columnName.equals(PCeNTableView.AMT_IN_WELL_WT)
					|| columnName.equals(PCeNTableView.AMT_IN_VIAL_WT)) {
				for (int j = 0; j < batches.size(); j++) {
					tempBatch = batches.get(j);
					if (tempBatch.getTotalWeight().GetValueInStdUnitsAsDouble() < newValueInDouble)
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0) {
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_MOLES)) {
				for (int j = 0; j < batches.size(); j++) {
					tempBatch = batches.get(j);
					double newWellWeight = newValueInDouble
							* tempBatch.getMolWgt();
					if (tempBatch.getTotalWeight().GetValueInStdUnitsAsDouble() < newWellWeight)
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0) {
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			} else if (columnName.equals(PCeNTableView.AMT_IN_WELL_VOL)) {
				for (int j = 0; j < batches.size(); j++) {
					tempBatch = batches.get(j);
					if (tempBatch.getTotalVolume().GetValueInStdUnitsAsDouble() < newValueInDouble)
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0) {
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			} else if (columnName.equals(PCeNTableView.TOTAL_WEIGHT)) {
				for (int j = 0; j < batches.size(); j++) {
					tempBatch = batches.get(j);
					PlateWell<ProductBatchModel> tempWell = allWells.get(j);
					if (newValueInDouble < tempWell.getContainedWeightAmount()
							.GetValueInStdUnitsAsDouble())
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0) {
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			} else if (columnName.equals(PCeNTableView.TOTAL_VOLUME)) {
				for (int j = 0; j < batches.size(); j++) {
					tempBatch = batches.get(j);
					PlateWell<ProductBatchModel> tempWell = allWells.get(j);
					if (newValueInDouble < tempWell.getContainedVolumeAmount()
							.GetValueInStdUnitsAsDouble())
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0) {
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			} else if (columnName.equals(PCeNTableView.TOTAL_MOLES)) {
				for (int j = 0; j < batches.size(); j++) {
					tempBatch = batches.get(j);
					PlateWell<ProductBatchModel> tempWell = allWells.get(j);
					double newTotalAmtMadeWeight = newValueInDouble
							* tempBatch.getMolWgt();
					if (newTotalAmtMadeWeight < tempWell
							.getContainedWeightAmount()
							.GetValueInStdUnitsAsDouble())
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0) {
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			}
		}
		if (new PCeNTableModelConnectorCommon(pageModel).updateColumn(
				workingBatchesList, columnName, newValue))
			enableSaveButton();
		// ProductBatchModel batch = null;
		// //for (int i=0; i < allWells.size(); i++) vb 11/26 won't work for non
		// plated batches
		// for (int i=0; i < batches.size(); i++)
		// {
		// batch = (ProductBatchModel) batches.get(i); //(ProductBatchModel)
		// well.getBatch();
		// if (columnName.equals(PCeNTableView.SALT_CODE)) {
		// if (! (newValue instanceof String) )
		// throw new IllegalArgumentException("Salt code must be a String");
		// batch.getSaltForm().setCode(newValue.toString());
		// if (batch.getSaltForm().getCode().indexOf("00") >= 0)
		// batch.setSaltEquivs(0.0);
		// } else if (columnName.equals(PCeNTableView.SALT_EQ)) {
		// if (batch.getSaltForm().getCode().indexOf("00") >= 0)
		// batch.setSaltEquivs(0.0);
		// else if (! (newValue instanceof PAmountCellEditor) )
		// throw new IllegalArgumentException("Salt Equivs must be an amount");
		// else
		// batch.setSaltEquivs(((PAmountCellEditor) newValue).getValue());
		// } else if (columnName.equals(PCeNTableView.ANALYTICAL_PURITY)) {
		// if (! (newValue instanceof ArrayList))
		// throw new IllegalArgumentException("Purity must be a list");
		// ArrayList newPurityModelList = (ArrayList) newValue;
		// if (newPurityModelList.size() > 0) {
		// batch.setAnalyticalPurityList(newPurityModelList);
		// }
		// } else if (columnName.equals(PCeNTableView.SOURCE)) {
		// if (! (newValue instanceof String) )
		// throw new
		// IllegalArgumentException("Compound source must be a String");
		// batch.getRegInfo().setCompoundSource((String) newValue);
		// } else if (columnName.equals(PCeNTableView.SOURCE_DETAILS)) {
		// if (! (newValue instanceof String) )
		// throw new
		// IllegalArgumentException("Compound source details must be a String");
		// batch.getRegInfo().setCompoundSourceDetail((String) newValue);
		// }
		//
		// }
		// enableSaveButton();
	}

	public void setSelectValue(int selectedRow) {
		// TODO Auto-generated method stub

	}

	public void sortBatches(int colIndex, boolean ascending) {
		// TODO Auto-generated method stub

	}

	public Object getPlateWell(ProductBatchModel newBatch) {
		List wells = productPlate.getPlateWellsforBatch(newBatch);

		if (wells.size() >= 1) {
			return (PlateWell) wells.get(0);
		}
		return null;
	}

	public void removeProductBatchModel(ProductBatchModel productBatchModel) {
		// int index = batches.indexOf(productBatchModel);
		// reArrangeMolString(index);
		if (this.batchesToMolStringsMap.containsKey(productBatchModel.getKey()))
			this.batchesToMolStringsMap.remove(productBatchModel);
		if (productBatchModel.isLoadedFromDB())
			productBatchModel.setToDelete(true);
		batches.remove(productBatchModel);
	}

	public boolean isMoreDeletableRows(ReactionStepModel reactionStepModel) {
		List products = reactionStepModel.getProducts();
		BatchesList batchesList = null;
		for (int i = 0; i < products.size(); i++) {
			batchesList = (BatchesList) products.get(i);
			if (batchesList.getPosition().equals(
					CeNConstants.PRODUCTS_USER_ADDED))
				return true;
		}
		return false;
	}

	public StoicModelInterface getProductBatchModel(int selectedRowIndex) {
		return (ProductBatchModel) batches.get(selectedRowIndex);
	}

	public void addProductBatchModel(ProductBatchModel batch,
			ProductPlate productplate) {
		this.productPlate = productplate;
		updateLocalCaches();
	}

	public void updateProductBatchModel(BatchModel batchModel2) {
		updateLocalCaches();
	}

	public void addProductBatchModel(ProductBatchModel batchModel) {
		this.updateProductBatchModel(batchModel);

	}

	// vb 12/6
	public void updateProductBatchModel(ProductBatchModel batch) {
		if (this.batches.contains(batch)) {
			this.batchesToMolStringsMap.clear();
			this.batchesToMolStringsMap = BatchAttributeComponentUtility
					.getCachedProductBatchesToMolstringsMap(batches, pageModel);
		}
	}

	public void removeProductBatchModel(BatchModel batchModel) {
		// TODO Auto-generated method stub

	}

	public void addStoichDataChangesListener(
			StoichDataChangesListener stoichDataChangesListener) {
		// TODO Auto-generated method stub

	}

	public ProductBatchModel getBatchModel(int index) {
		return batches.get(index);
	}

	public ProductPlate getProductPlate() {
		return productPlate;
	}

	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		if (this.columnPreferences.size() > modelIndex)
			return (TableColumnInfo) this.columnPreferences.get(modelIndex);
		else
			return null;
	}

	public TablePreferenceDelegate getTablePreferenceDelegate() {
		return this.preferenceDelegate;
	}

}
