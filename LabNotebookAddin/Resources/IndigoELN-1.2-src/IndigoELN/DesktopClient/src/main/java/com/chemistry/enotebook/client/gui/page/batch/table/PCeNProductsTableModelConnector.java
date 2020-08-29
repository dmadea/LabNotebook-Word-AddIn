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
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.regis_submis.RegSubHandler;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.*;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PCeNProductsTableModelConnector extends PCeNAbstractTableModelConnector implements PCeNProductTableRowSelectionChangeHandler, PCeNProductTableModelConnector {

	private static final Log log = LogFactory.getLog(PCeNProductsTableModelConnector.class);

	private List<ProductBatchModel> batches; //, listOfBatchesWithMolStrings;
	//private boolean structureHidden = false;
	private NotebookPageModel pageModel;
	//private boolean plateView = false;
	private Map<String, MolString> batchesToMolStringsMap;
	//private HashMap batchesAndPlatesMap;
	protected String[] headerNames;
	protected boolean structureHidden = false;
	// column prefs
	private List<TableColumnInfo> columnPreferences;
	private TablePreferenceDelegate preferenceDelegate;
	private static final String tableName = "Batch_Products_Table";
	private static final String[] columnNames = { 
		PCeNTableView.STRUCTURE,
		PCeNTableView.NBK_BATCH_NUM,
		PCeNTableView.SELECT_OPTION,
		PCeNTableView.TOTAL_WEIGHT,
		PCeNTableView.TOTAL_VOLUME,
		PCeNTableView.TOTAL_MOLES,
/*		PCeNTableView.AMT_REMAINING_WEIGHT,
		PCeNTableView.AMT_REMAINING_VOLUME,
*/		PCeNTableView.THEORETICAL_WEIGHT,
		PCeNTableView.THEORETICAL_MMOLES,
		PCeNTableView.PERCENT_YIELD,
		PCeNTableView.COMPOUND_STATE,
		PCeNTableView.SALT_CODE,
		PCeNTableView.SALT_EQ,
		PCeNTableView.ANALYTICAL_PURITY,
		PCeNTableView.MELTING_POINT,
		PCeNTableView.MOL_WEIGHT,
		PCeNTableView.MOL_FORMULA,
		PCeNTableView.CONVERSATIONAL_BATCH,
		PCeNTableView.VIRTUAL_COMP_NUM,
		PCeNTableView.STEREOISOMER,
		PCeNTableView.STATUS,
		PCeNTableView.SOURCE,
		PCeNTableView.SOURCE_DETAILS,
		PCeNTableView.EXT_SUPPLIER,
		PCeNTableView.COMMENTS,
		PCeNTableView.PRECURSORS,
		PCeNTableView.HAZARD_COMMENTS,
		PCeNTableView.COMPOUND_PROTECTION,
		PCeNTableView.STRUCTURE_COMMENTS		
	};

	private PCeNTableView table;
	
	public PCeNProductsTableModelConnector(List<ProductBatchModel> batches, NotebookPageModel pageModel) {
		//if (batches == null || batches.size() == 0)
			//throw new IllegalArgumentException("ParallelCeNProductBatchTableViewControllerUtility received NULL argument list");
		super(pageModel);
		this.batches = batches;
		this.pageModel = pageModel;
		this.batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, pageModel);
		//this.batchesAndPlatesMap = pageModel.getAllProductBatchesAndPlatesMap(false);
		createHeaderNames();
		// col prefs
		try {
			preferenceDelegate = new TablePreferenceDelegate(tableName, this.pageModel, columnNames);
			columnPreferences = preferenceDelegate.getColumnPreferences();
		} catch (UserPreferenceException e) {
			log.error("Unable to set table preferences : " + e.getMessage());
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to set table preferences.", e);
		}
	}

	
	protected void createHeaderNames() {
		headerNames = columnNames;
	}
	
	public String[] getHeaderNames() {
		return headerNames;
	}
	
	public void setTable(PCeNTableView table) {
		this.table = table;
	}
	
	public boolean isCellEditable(int rowIndex, int colIndex) {
		if (StringUtils.equals(headerNames[colIndex], PCeNTableView.SELECT_OPTION)) {
			return true;
		}
		
	    if (headerNames[colIndex].equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM)
	    		|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.STATUS) 
				|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.COMMENTS)
				|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.TOTAL_MOLES)
				|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.TOTAL_WEIGHT)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.TOTAL_VOLUME)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.COMPOUND_STATE)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.SALT_CODE)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.SALT_EQ)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.ANALYTICAL_PURITY)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.MELTING_POINT)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.BARCODE)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.STATUS)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.SOURCE)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.SOURCE_DETAILS)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.COMMENTS)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.COMPOUND_PROTECTION)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.EXT_SUPPLIER)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.HAZARD_COMMENTS)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.STRUCTURE_COMMENTS)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.STEREOISOMER)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.THEORETICAL_WEIGHT)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.THEORETICAL_MMOLES)
		    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.PRECURSORS)
	    		)
	    {
	    	ProductBatchModel batch = (ProductBatchModel) batches.get(rowIndex);
			if (CommonUtils.getProductBatchModelEditableFlag(batch, pageModel))
				return true;
			else
				return false;
	    }
			else
				return false;
	}
/*	
	public boolean isColumnEditable(String columnName) {
		if 	(columnName.equalsIgnoreCase(PCeNTableView.THEORETICAL_WEIGHT)
				|| columnName.equalsIgnoreCase(PCeNTableView.THEORETICAL_MMOLES)
				|| columnName.equalsIgnoreCase(PCeNTableView.MOL_WEIGHT)
				|| columnName.equalsIgnoreCase(PCeNTableView.MOL_FORMULA)
				|| columnName.equalsIgnoreCase(PCeNTableView.CONVERSATIONAL_BATCH)
				|| columnName.equalsIgnoreCase(PCeNTableView.VIRTUAL_COMP_NUM)
				|| columnName.equalsIgnoreCase(PCeNTableView.STRUCTURE)
				|| columnName.equalsIgnoreCase(PCeNTableView.STEREOISOMER)
				|| columnName.equalsIgnoreCase(PCeNTableView.BARCODE)
				|| columnName.equalsIgnoreCase(PCeNTableView.TA)
				|| columnName.equalsIgnoreCase(PCeNTableView.PROJECT)
				|| columnName.equalsIgnoreCase(PCeNTableView.STATUS)  // vbtodo fix!
				|| columnName.equalsIgnoreCase(PCeNTableView.PRECURSORS)
				|| columnName.equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM)
				|| columnName.equalsIgnoreCase(PCeNTableView.HAZARD_COMMENTS)
				|| columnName.equalsIgnoreCase(PCeNTableView.WELL_POSITION)
				|| columnName.equalsIgnoreCase(PCeNTableView.PLATE)
				|| columnName.equalsIgnoreCase(PCeNTableView.PERCENT_YIELD)
				|| columnName.equalsIgnoreCase(PCeNTableView.EXTERNAL_SUPPLIER)
				|| columnName.equalsIgnoreCase(PCeNTableView.EXT_SUPPLIER)             // vbtodo fix!
				|| columnName.equalsIgnoreCase(PCeNTableView.AMT_REMAINING_WEIGHT)             
				|| columnName.equalsIgnoreCase(PCeNTableView.AMT_REMAINING_VOLUME)
		)
			return false;
		else
			return true;		
	}
*/

	public Object getValue(int rowIndex, int colIndex) {
		ProductBatchModel batch = (ProductBatchModel) batches.get(rowIndex);
		if (! (batch instanceof ProductBatchModel))
			BatchAttributeComponentUtility.setDefaultTheoreticalAmounts(batch, pageModel);
		
		//This part of code is only needed when the well, tube and vial info. are required to display in the jTable.
/*		PlateWell well = null;
		Tube tube = null;
		Vial vial = null;
		
		AbstractPlate productPlate = (AbstractPlate) batchesAndPlatesMap.get(batch);
		if (productPlate instanceof PseudoProductPlate)
		{
			tube = ((PseudoProductPlate)productPlate).getTubeForBatch(batch);//(Tube) allTubes.get(rowIndex);
			vial = ((PseudoProductPlate)productPlate).getVialForBatch(batch);//(Vial) allVials.get(rowIndex);
		}
		else
		{
			ArrayList wellsList = (ArrayList) ((ProductPlate)productPlate).getPlateWellsforBatch(batch);
			well = (PlateWell) wellsList.get(0);//Assuming only one well for each batch for BETA.
		}
*/		
		if (headerNames[colIndex].equals(PCeNTableView.STRUCTURE)) {
			return batchesToMolStringsMap.get(batch.getKey());
		} else if (headerNames[colIndex].equals(PCeNTableView.NBK_BATCH_NUM)) {
			return BatchAttributeComponentUtility.getNotebookBatchNumber(batch);
		} else if (StringUtils.equals(headerNames[colIndex], PCeNTableView.SELECT_OPTION)) {
			return new Boolean(batch.isSelected());
		}  else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_VOLUME)) {
			return BatchAttributeComponentUtility.getTotalAmountMadeVolume(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_WEIGHT)) {
			return BatchAttributeComponentUtility.getTotalAmountMadeWeight(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.MOL_WEIGHT)) {
			double d = batch.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
			return PCeNRegistrationProductsTableModelConnector.formatWeight(d);
			//return "" + batch.getMolecularWeightAmount().getValue();
		} else if (headerNames[colIndex].equals(PCeNTableView.MOL_FORMULA)) {
			return batch.getMolecularFormula();
		} else if (headerNames[colIndex].equals(PCeNTableView.THEORETICAL_WEIGHT)) {
			return batch.getTheoreticalWeightAmount();
		} else if (headerNames[colIndex].equals(PCeNTableView.THEORETICAL_MMOLES)) {
			return batch.getTheoreticalMoleAmount();
		} else if (headerNames[colIndex].equals(PCeNTableView.PERCENT_YIELD)) {
			if (batch.getTheoreticalYieldPercentAmount().GetValueInStdUnitsAsDouble() < 0)
				return new AmountModel(UnitType.SCALAR);
			else 
				return batch.getTheoreticalYieldPercentAmount().deepClone();
			//return (batch.getTheoreticalYieldPercentAmount().GetValueInStdUnitsAsDouble() < 0 ? "" : batch.getTheoreticalYieldPercentAmount().GetValueInStdUnitsAsDouble() + "");
		} else if (headerNames[colIndex].equals(PCeNTableView.COMPOUND_STATE)) {
			return (batch.getCompoundState() == null ? "" : batch.getCompoundState());
		} else if (headerNames[colIndex].equals(PCeNTableView.SALT_CODE)) {
			return batch.getSaltForm().getCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.SALT_EQ)) {
			AmountModel amt = new AmountModel(UnitType.SCALAR);
			amt.setValue(batch.getSaltEquivs());
			return amt;
		} else if (headerNames[colIndex].equals(PCeNTableView.ANALYTICAL_PURITY)) {
			List<PurityModel> purityModels = batch.getAnalyticalPurityList();
			String purityStr = BatchAttributeComponentUtility.getPuritiesListAsHTMLString(purityModels);
/*			for (Iterator it = purityModels.iterator(); it.hasNext();) {
				PurityModel model = (PurityModel) it.next();
				//Enforcement of Purity selection is made in the Panel itself. So, this check is not required.
				//if (model.isRepresentativePurity()) {
				purityStr = model.toString();
				//}
			}
*/			return purityStr;
		}  else if (headerNames[colIndex].equals(PCeNTableView.MELTING_POINT)) {
			return batch.getMeltPointRange();//.toString();
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_MOLES)) {	
			return BatchAttributeComponentUtility.getTotalMoles(batch);
		}/* else if (headerNames[colIndex].equals(PCeNTableView.AMT_REMAINING_WEIGHT)) {	
			return BatchAttributeComponentUtility.getWeightRemaining(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.AMT_REMAINING_VOLUME)) {	
			return BatchAttributeComponentUtility.getVolumeRemaining(batch);
		} */else if (headerNames[colIndex].equals(PCeNTableView.EXT_SUPPLIER)) {
			// External supplier
			if (batch.getVendorInfo() != null) {
				String externalSupplierCode = batch.getVendorInfo().getCode();
				String externalSupplierDescription = batch.getVendorInfo().getDescription();
				String externalSupplierCatalogReg = batch.getVendorInfo().getSupplierCatalogRef();
				
				if (externalSupplierCode != null && externalSupplierCode.length() > 0) {
					StringBuffer buff = new StringBuffer(externalSupplierCode);
					
					if (externalSupplierDescription != null && externalSupplierDescription.length() > 0) {
						buff.append(" - ").append(externalSupplierDescription);
					}
					if (externalSupplierCatalogReg != null && externalSupplierCatalogReg.length() > 0) {
            buff.append(" (").append(externalSupplierCatalogReg).append(")");
          }
					
          return buff.toString();
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
			return batch.getCompound().getStereoisomerCode().equals("null") ? "" : batch.getCompound().getStereoisomerCode() ;
		} else if (headerNames[colIndex].equals(PCeNTableView.BARCODE)) {
			return batch.getBarCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.STATUS)) {
			return ProductBatchStatusMapper.getInstance().getStatusString(batch.getSelectivityStatus(), batch.getContinueStatus());
		} else if (headerNames[colIndex].equals(PCeNTableView.SOURCE)) {
			return BatchAttributeComponentUtility.getCompoundSourceDisplayValue(batch.getRegInfo().getCompoundSource());
			//return batch.getRegInfo().getCompoundSource();
		} else if (headerNames[colIndex].equals(PCeNTableView.SOURCE_DETAILS)) {
//			return batch.getRegInfo().getCompoundSourceDetail();
			return BatchAttributeComponentUtility.getCompoundSourceDetailsDisplayValue(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.COMMENTS)) {
			return batch.getComments();
		} else if (headerNames[colIndex].equals(PCeNTableView.TA)) {
			return pageModel.getTaCode();
		} else if (headerNames[colIndex].equals(PCeNTableView.PROJECT)) {
			return pageModel.getProjectCode();
		}  else if (headerNames[colIndex].equals(PCeNTableView.HAZARD_COMMENTS)) {
			StringBuffer buff = new StringBuffer();
			String hazards = RegSubHandler.getProductBatchModelHazardString(batch);
			String handling = RegSubHandler.getProductBatchModelHandlingString(batch);
			String storage = RegSubHandler.getProductBatchModelStorageString(batch);
			if (hazards.length() > 0)
				buff.append(hazards).append("; ");
			if (handling.length() > 0)
				buff.append(handling).append("; ");
			if (storage.length() > 0)
				buff.append(storage);
			return buff.toString();
		} else if (headerNames[colIndex].equals(PCeNTableView.COMPOUND_PROTECTION)) {
			return BatchAttributeComponentUtility.getProtectionCode(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.PRECURSORS)) {
			return pageModel.getSingletonPrecursorsString(batch);
		} else if (headerNames[colIndex].equals(PCeNTableView.STRUCTURE_COMMENTS)) {
			return batch.getCompound().getStructureComments();
		}
		return "";
	}


	public void setValue(Object value, int rowIndex, int colIndex) {
		if (value == null || value.toString().equals(""))
		{
			if (!(headerNames[colIndex].equals(PCeNTableView.SOURCE) //Null allowed columns.
					||headerNames[colIndex].equals(PCeNTableView.COMPOUND_STATE)))
				return;
		}
		//System.out.println(this.getClass().getName() + ".setValue of cell (" + rowIndex + "," + colIndex + ") to " + strval);
		ProductBatchModel batch = batches.get(rowIndex);
		boolean modelChanged = false;
		if (headerNames[colIndex].equals(PCeNTableView.NBK_BATCH_NUM)) {
			try {
				if (BatchAttributeComponentUtility.setNotebookBatchNumber(batch, (String) value, pageModel)) 
					modelChanged = true;
			} catch (InvalidBatchNumberException e) {
				JOptionPane.showMessageDialog(
						null,
						e.getMessage(),
						"Data Exception", JOptionPane.ERROR_MESSAGE);
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_WEIGHT)) {
			if(value instanceof AmountModel)
			{
				PlateWell<ProductBatchModel> well = getPlateWell(batch);
				if(BatchAttributeComponentUtility.setTotalAmountMadeWeight(batch, (AmountModel) value, well)) {
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_VOLUME)) {
			if(value instanceof AmountModel)
			{
				PlateWell<ProductBatchModel> well = getPlateWell(batch);
				if (BatchAttributeComponentUtility.setTotalAmountMadeVolume(batch, (AmountModel) value, well))
					modelChanged = true;
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.PARENT_MW)) {
			if(value instanceof AmountModel)
			{
				if (BatchAttributeComponentUtility.setParentMolWeight(batch, (AmountModel) value))
					modelChanged = true;
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.BATCH_MW)) {
			if(value instanceof AmountModel)
			{
				if (BatchAttributeComponentUtility.setMolWeight(batch, (AmountModel) value)) {
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals(PCeNTableView.RXN_WEIGHT)) {
			if(value instanceof AmountModel)
			{
				AmountModel newVal = (AmountModel)value;
				if(!batch.getStoicWeightAmount().equals(newVal));
				{
					batch.getStoicWeightAmount().setValue(newVal.GetValueInStdUnitsAsDouble());
					modelChanged = true;
				}
			}
		} else if (headerNames[colIndex].equals("Density")) {
			if (batch.getStoicReactionRole().equals(BatchType.SOLVENT.toString()))
			{
				//Density Amount doesn't make sense for SOLVENTS. Do nothing
			} 
			else
			{
				if(value instanceof AmountModel)
				{
					AmountModel newVal = (AmountModel)value;
					if(!batch.getStoicDensityAmount().equals(newVal));
					{
						batch.getStoicDensityAmount().deepCopy(newVal);
						modelChanged = true;
						//fireDensityChanged(batch);
					}
				}
			}
		}
		else if (headerNames[colIndex].equals(PCeNTableView.SALT_CODE)) 
		{
			try {
				if (BatchAttributeComponentUtility.setSaltCode(batch, (String) value)) {
					modelChanged = true;
					batch.setModelChanged(true);
					batch.recalcAmounts();
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		} 
		else if (headerNames[colIndex].equals(PCeNTableView.TOTAL_MOLES))
		{
			if(value instanceof AmountModel)
			{
				PlateWell<ProductBatchModel> well = getPlateWell(batch);
				AmountModel newVal = (AmountModel)value;
				if(!batch.getStoicMolarAmount().equals(newVal));
				{
					if (BatchAttributeComponentUtility.setTotalMoles(batch, newVal, well))
						modelChanged = true;
				}
			}

		}
		else if (headerNames[colIndex].equals(PCeNTableView.SOURCE)) {
			
			if(((ProductBatchModel)batch).getRegInfo().getCompoundSource() != null)  
			{
				String code = "";
				try {
					code = CodeTableCache.getCache().getSourceCode((String) value);
				} catch (CodeTableCacheException e) {
					e.printStackTrace();
				}
				if (((ProductBatchModel)batch).getRegInfo().getCompoundSource().equals(code))
					return;
			}
					
			if (BatchAttributeComponentUtility.setCompoundSource(batch, value))
					modelChanged = true;

		}
		else if (headerNames[colIndex].equals(PCeNTableView.SOURCE_DETAILS)) {
			
			if(((ProductBatchModel)batch).getRegInfo().getCompoundSourceDetail() != null && ((ProductBatchModel)batch).getRegInfo().getCompoundSourceDetail().equals(value))
				return;
			if (BatchAttributeComponentUtility.setCompoundSourceDetails(batch, value))
				modelChanged = true;
		}
		else if (headerNames[colIndex].equals("Purity"))
		{
			if(value instanceof AmountModel)
			{
				AmountModel newVal = (AmountModel)value;
				if(!batch.getStoicPurityAmount().equals(newVal));
				{
					batch.setStoicPurityAmount(newVal);
					modelChanged = true;
					//firePurityChanged(batch);
				}
			}
		}
		else if (headerNames[colIndex].equals(PCeNTableView.SALT_EQ))
		{
			try {
				if (BatchAttributeComponentUtility.setSaltEquiv(batch, (AmountModel)value)) {
					modelChanged = true;
					batch.setModelChanged(true);
					batch.recalcAmounts();
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}

		}  
/*		else if (headerNames[colIndex].equals(PCeNTableView.HAZARD_COMMENTS))
		{
			//Applicable only for Non-List type row
			batch.setHazardComments(value.toString());
			modelChanged = true;
		}*/
		else if (headerNames[colIndex].equals(PCeNTableView.STATUS)) 
		{
			if (BatchAttributeComponentUtility.setProductBatchStatus(batch, (String) value))
				modelChanged = true;
		}
		else if (headerNames[colIndex].equals(PCeNTableView.COMPOUND_STATE)) {
			//batch.setCompoundState(value.toString());
			BatchAttributeComponentUtility.setCompoundState(batch, value.toString());
			modelChanged = true;
		}
		else if (headerNames[colIndex].equals(PCeNTableView.COMMENTS)) 
		{
			batch.setComments(value.toString());
			modelChanged = true;
		}
		else if (headerNames[colIndex].equals(PCeNTableView.COMPOUND_PROTECTION)) {
			batch.setProtectionCode(BatchAttributeComponentUtility.getCode(value.toString()));
			modelChanged = true;
		}
		else if (headerNames[colIndex].equals(PCeNTableView.THEORETICAL_MMOLES)) {
			if(value instanceof AmountModel)
			{
				AmountModel newVal = (AmountModel)value;
				if(!batch.getTheoreticalMoleAmount().equals(newVal));
				{
					batch.setTheoreticalMoleAmount(newVal);
					Unit2 unit = newVal.getUnit().deepClone();
					batch.getTotalMolarAmount().setUnit(unit);
					modelChanged = true;
				}
			}
		}
		else if (headerNames[colIndex].equals(PCeNTableView.THEORETICAL_WEIGHT)) {
			if(value instanceof AmountModel)
			{
				AmountModel newVal = (AmountModel)value;
				if(!batch.getTheoreticalWeightAmount().equals(newVal));
				{
					batch.setTheoreticalWeightAmount(newVal);
					Unit2 unit = newVal.getUnit().deepClone();
					batch.getTotalWeight().setUnit(unit);
					modelChanged = true;
				}
			}
		}
		else if (headerNames[colIndex].equals(PCeNTableView.STRUCTURE_COMMENTS)) {
			batch.getCompound().setStructureComments(value.toString());
			modelChanged = true;
		}
		// Do the following if anything changed
		if (modelChanged) {
			refreshTable();
			enableSaveButton();
		}
	}
	
	private void refreshTable() {
		if (table != null) {
			int row = table.getSelectedRow();		
			((PCeNTableModel) table.getModel()).fireTableDataChanged();
			if (row != -1) {
				table.setRowSelectionInterval(row, row);
			}
			
		}
	}

	private PlateWell<ProductBatchModel> getPlateWell(BatchModel batch) {
		if (pageModel != null) {
			Map<ProductBatchModel, ProductPlate> batchesAndPlatesMap = pageModel.getAllProductBatchesAndPlatesMap(false);
			if (batchesAndPlatesMap != null) {
				ProductPlate productPlate = batchesAndPlatesMap.get(batch); 
				if (productPlate != null) {
					List<PlateWell<ProductBatchModel>> wellsList = productPlate.getPlateWellsforBatch((ProductBatchModel) batch);
					if (wellsList != null && wellsList.size() > 0) {
						PlateWell<ProductBatchModel> well = wellsList.get(0);
						return well;
					}
				}
			}
		}
		return null;
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
	public List<ProductBatchModel> getAbstractBatches() {
		return batches;
	}

	/**
	 * @param abstractBatches
	 *            the abstractBatches to set
	 */
	public void setAbstractBatches(List<ProductBatchModel> abstractBatches) {
		this.batches = abstractBatches;
	}

	/**
	 * @return the listOfBatchesWithMolStrings
	 */
	public List<ProductBatchModel> getListOfBatchesWithMolStrings() {
		return batches;
	}

	/**
	 * @param listOfBatchesWithMolStrings
	 *            the listOfBatchesWithMolStrings to set
	 */
	public void setListOfBatchesWithMolStrings(List<ProductBatchModel> listOfBatchesWithMolStrings) {
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

	public List<StoicModelInterface> getStoicElementListInTransactionOrder() {
		List<StoicModelInterface> stoichList = new ArrayList<StoicModelInterface>();
		stoichList.addAll(batches);
		return stoichList;
	}

	private boolean confirmUpdateColumn(List<ProductBatchModel> problemBatches) {
		return ProblemBatchesDialog.confirmUpdateColumn(MasterController.getGUIComponent(), problemBatches);
	}
	
	public void updateColumn(String columnName, Object newValue) {
		List<ProductBatchModel> workingBatchesList = this.batches;
		if (newValue instanceof PAmountComponent)
		{
			ArrayList<ProductBatchModel> failedBatches = new ArrayList<ProductBatchModel>();
			ArrayList<ProductBatchModel> passedBatches = new ArrayList<ProductBatchModel>();
			ProductBatchModel tempBatch = null;
			double newValueInDouble = ((PAmountComponent) newValue).getAmount().GetValueInStdUnitsAsDouble();
/*			if (columnName.equals(PCeNTableView.AMT_IN_WELL_WT))
			{
				for (int j=0; j < batches.size(); j++)
				{
					tempBatch = (ProductBatchModel)batches.get(j);
					if (tempBatch.getTotalWeight().GetValueInStdUnitsAsDouble() < newValueInDouble)
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0)
				{
					if (confirmUpdateColumn())
						workingBatchesList = passedBatches;
					else
						return;
				}
			}
			else if (columnName.equals(PCeNTableView.AMT_IN_WELL_VOL))
			{
				for (int j=0; j < batches.size(); j++)
				{
					tempBatch = (ProductBatchModel)batches.get(j);
					if (tempBatch.getTotalVolume().GetValueInStdUnitsAsDouble() < newValueInDouble)
						failedBatches.add(tempBatch);
					else
						passedBatches.add(tempBatch);
				}
				if (failedBatches.size() > 0)
				{
					if (confirmUpdateColumn())
						workingBatchesList = passedBatches;
					else
						return;
				}
			}
			else */
			if (columnName.equals(PCeNTableView.TOTAL_WEIGHT))
			{
				for (int j=0; j < batches.size(); j++)
				{
					tempBatch = (ProductBatchModel)batches.get(j);
					PlateWell<ProductBatchModel> tempWell = getPlateWell(tempBatch);
					if (tempWell == null || tempWell.getContainedWeightAmount() == null) {
						failedBatches.add(tempBatch);
					} else {
						if (newValueInDouble < tempWell.getContainedWeightAmount().GetValueInStdUnitsAsDouble()) {
							failedBatches.add(tempBatch);
						} else {
							passedBatches.add(tempBatch);
						}
					}
				}
				if (failedBatches.size() > 0)
				{
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			}
			else if (columnName.equals(PCeNTableView.TOTAL_VOLUME))
			{
				for (int j=0; j < batches.size(); j++)
				{
					tempBatch = batches.get(j);
					PlateWell<ProductBatchModel> tempWell = getPlateWell(tempBatch);
 
					if (tempWell == null || tempWell.getContainedWeightAmount() == null) {
						failedBatches.add(tempBatch);
					} else {
						if (newValueInDouble < tempWell.getContainedVolumeAmount().GetValueInStdUnitsAsDouble()) {
							failedBatches.add(tempBatch);
						} else {
							passedBatches.add(tempBatch);
						}
					}
				}
				if (failedBatches.size() > 0)
				{
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			}
			else if (columnName.equals(PCeNTableView.TOTAL_MOLES))
			{
				for (int j=0; j < batches.size(); j++)
				{
					tempBatch = batches.get(j);
					PlateWell<ProductBatchModel> tempWell = getPlateWell(tempBatch);
					if (tempWell == null || tempWell.getContainedWeightAmount() == null) {
						failedBatches.add(tempBatch);
					} else {
						double newTotalAmtMadeWeight = newValueInDouble * tempBatch.getMolWgt(); 
						if (newTotalAmtMadeWeight < tempWell.getContainedWeightAmount().GetValueInStdUnitsAsDouble()) {
							failedBatches.add(tempBatch);
						} else {
							passedBatches.add(tempBatch);
						}
					}
				}
				if (failedBatches.size() > 0)
				{
					if (confirmUpdateColumn(failedBatches))
						workingBatchesList = passedBatches;
					else
						return;
				}
			}			
		}
		if (new PCeNTableModelConnectorCommon(pageModel).updateColumn(workingBatchesList, columnName, newValue))
			enableSaveButton();
//		ProductBatchModel batch = null;
//		//for (int i=0; i < allWells.size(); i++) vb 11/26 won't work for non plated batches
//		for (int i=0; i < batches.size(); i++)
//		{
//			batch = (ProductBatchModel) batches.get(i); //(ProductBatchModel) well.getBatch();
//			if (columnName.equals(PCeNTableView.SALT_CODE)) {
//				if (! (newValue instanceof String) )
//					throw new IllegalArgumentException("Salt code must be a String");
//				batch.getSaltForm().setCode(newValue.toString());
//				if (batch.getSaltForm().getCode().indexOf("00") >= 0)
//					batch.setSaltEquivs(0.0);
//			} else if (columnName.equals(PCeNTableView.SALT_EQ)) {
//				if (batch.getSaltForm().getCode().indexOf("00") >= 0)
//					batch.setSaltEquivs(0.0);
//				else if (! (newValue instanceof PAmountCellEditor) )
//					throw new IllegalArgumentException("Salt Equivs must be an amount");
//				else 
//					batch.setSaltEquivs(((PAmountCellEditor) newValue).getValue());
//			} else if (columnName.equals(PCeNTableView.ANALYTICAL_PURITY)) {
//				if (! (newValue instanceof ArrayList))
//					throw new IllegalArgumentException("Purity must be a list");
//				ArrayList newPurityModelList = (ArrayList) newValue;
//				if (newPurityModelList.size() > 0) {
//					batch.setAnalyticalPurityList(newPurityModelList);
//				}
//			}
//
//		}
//		enableSaveButton();
	}

	public void setSelectValue(int selectedRow) {
		// TODO Auto-generated method stub

	}

	public void sortBatches(int colIndex, boolean ascending) {
		// TODO Auto-generated method stub

	}
	
	public StoicModelInterface getProductBatchModel(int selectedRowIndex) {
		return batches.get(selectedRowIndex);
	}


	public boolean isMoreDeletableRows(ReactionStepModel reactionStepModel) {
		// TODO Auto-generated method stub
		return false;
	}


	public void removeProductBatchModel(ProductBatchModel productBatchModel) {
		if (this.batchesToMolStringsMap.containsKey(productBatchModel.getKey()))
			this.batchesToMolStringsMap.remove(productBatchModel.getKey());
		if(productBatchModel.isLoadedFromDB())
			productBatchModel.setToDelete(true);
		batches.remove(productBatchModel);
	}


	public void updateProductBatchModel(BatchModel batchModel) {
		// TODO Auto-generated method stub
		
	}


	public void addProductBatchModel(ProductBatchModel batchModel) {
		this.batches.add(batchModel);
		this.batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, pageModel);
	}


	public void removeProductBatchModel(BatchModel batchModel) {
		this.batches.remove(batchModel);
		
	}

	public ProductBatchModel getBatchModel(int index) {
		if (batches != null && batches.size() < index + 1) {
			throw new ArrayIndexOutOfBoundsException("getBatchModel index > number of batchs");
		}
		return batches.get(index);
	}


	public void addProductBatchModel(ProductBatchModel batch,
			PseudoProductPlate pseudoPlate) {
		// TODO Auto-generated method stub
		
	}


	public void addProductBatchModel(ProductBatchModel batch,
			ProductPlate pseudoPlate) {
		// TODO Auto-generated method stub
		
	}

	// vb 12/6
	public void updateProductBatchModel(ProductBatchModel batch) {
		if (this.batches.contains(batch)) {
			this.batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, pageModel);
		}
	}


	public void addStoichDataChangesListener(
			StoichDataChangesListener stoichDataChangesListener) {
		// TODO Auto-generated method stub
		
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
