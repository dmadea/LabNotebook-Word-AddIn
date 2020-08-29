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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNUtilObject;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.domain.StoicModelInterface;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PCeNRegistration_ErrorViewControllerUtility implements PCeNTableModelConnector {
	private String[] headerNames;
	private ProductBatchModel batchModel;
	//private PlateWell well;
	private ArrayList  batches, listOfBatchesWithMolStrings;
	private ArrayList errorMessagesList;
	private boolean structureHidden = false;
	private boolean batchSelectionMode = false; 
	
	/**
	 * Initialize the controller for the plate model and view
	 * @param plate
	 */
	public PCeNRegistration_ErrorViewControllerUtility(LinkedHashMap errorMap, boolean batchSelectionMode) {
		batches = new ArrayList(errorMap.keySet());
		errorMessagesList = new ArrayList(errorMap.values());

		if (!isStructureHidden()) {
			listOfBatchesWithMolStrings = new ArrayList(100);
			// associates batch with a structure
			for (int i = 0; i < batches.size(); i++) {
				batchModel = (ProductBatchModel) batches.get(i);
				String dspStructures = batchModel.getCompound().getStringSketchAsString();
				ParallelCeNUtilObject abstractBatchWithMolString = new ParallelCeNUtilObject(batchModel, new MolString(Decoder
						.decodeString(dspStructures), i), this, i);
				listOfBatchesWithMolStrings.add(abstractBatchWithMolString);
			}// end for
		} 
		batchModel = (ProductBatchModel) batches.get(0);
		this.batchSelectionMode = batchSelectionMode;
		createHeaderNames();
	}

	/**
	 * Initialize the header names for the batch details table
	 *
	 */
	private void createHeaderNames() {
		int index = 0;

		if (!structureHidden) {
			headerNames = new String[10];
			headerNames[index++] = "Structure";
		} else {
			headerNames = new String[9];
		}
		headerNames[index++] = "Select";
		headerNames[index++] = "<html><font size=4>Notebook <br>Batch #<font></html>";
		headerNames[index++] = "<html><font size=3 color=red>Error Msg<font></html>";		
		headerNames[index++] = "Register";
		headerNames[index++] = "Register Plate";
		headerNames[index++] = "Purification";
		headerNames[index++] = "Quantitative Quality Control";
		headerNames[index++] = "Screening";
		// NOTE:  the regular expression to filter out the html won't work if you don't use a blank in front of the <br>
		headerNames[index++] = "Status";
	}

	/**
	 * @return array of header name strings
	 */
	public String[] getHeaderNames() {
		return headerNames;
	}
	
	/**
	 * Called by the table model to the object in the cell defined by the row and column index
	 */
	public Object getValue(int rowIndex, int colIndex) {
		ProductBatchModel batch = (ProductBatchModel) batches.get(rowIndex);
		ParallelCeNUtilObject mParallelCeNUtilObject = (ParallelCeNUtilObject) listOfBatchesWithMolStrings.get(rowIndex);
		String headerName = headerNames[colIndex];
		headerName = headerName.replaceAll("<(.|\n)*?>", "");
		if (headerName.equals("Select")) {
			return new Boolean(batch.isSelected());
			//return new Boolean(true);// Default select all check Boxes. Later on modify the logic 
		}  else if (headerName.equals("Register")) {
			return batch.getRegInfo().getCompoundRegistrationStatus();
		}  else if (headerName.equals("Register Plate")) {
			return batch.getRegInfo().getCompoundManagementStatus();
		} else if (headerName.equals("Purification")) {
			return batch.getRegInfo().getPurificationServiceStatus();
		}    else if (headerName.equals("Screening")) {
			return batch.getRegInfo().getCompoundAggregationStatus();
		} else if (headerName.equals("Quantitative Quality Control")) {
			return ""; //batch.getRegInfo().get;
		}  else if (headerNames[colIndex].equals("Structure")) {
			return mParallelCeNUtilObject.getMolString();
		} else if (headerName.equals("Notebook Batch #")) {
			return batch.getBatchNumber().getBatchNumber();
		} else if (headerName.equals("Error Msg")) {
				return errorMessagesList.get(rowIndex).toString();
		} else if (headerName.equals("Status")) {
			return ProductBatchStatusMapper.getInstance().getCombinedProductBatchStatus(batch);
		} 
		return "";


	}

	/**
	 * todo:  make other columns editable.
	 */
	public boolean isCellEditable(int rowIndex, int colIndex) {
		if (headerNames[colIndex].equalsIgnoreCase("Select"))
			if (!batchSelectionMode)
				return true;
		return false;
	}

	/**
	 * Set the selected flag of the product batch from the checkbox entry.
	 * @param rowIndex
	 */
	public void setSelectValue(int rowIndex) {
		if (!batchSelectionMode)
		{
			ProductBatchModel batch = (ProductBatchModel) ((ParallelCeNUtilObject) listOfBatchesWithMolStrings.get(rowIndex)).getBatch();
			batch.setSelected(!batch.isSelected());
		}
		// todo:  need a list of selected batches cached somewhere
	}

	/**
	 * 
	 */
	public void setValue(Object value, int rowIndex, int colIndex) {

	}

	private void enableSaveButton() {
		MasterController.getInstance().getGUIComponent().enableSaveButtons();
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
		this.batches = (ArrayList) abstractBatches;
	}

	/**
	 * @return the listOfBatchesWithMolStrings
	 */
	public List getListOfBatchesWithMolStrings() {
		return listOfBatchesWithMolStrings;
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

	public void updateColumn(String cloumnname, Object newValue) {
		if (newValue.equals("true") || newValue.equals("false"))
		{
			ProductBatchModel batch = null;
			for (int i=0; i < batches.size(); i++)
			{
				batch = (ProductBatchModel) batches.get(i);
				if (cloumnname.equals("Select")) {
					batch.setSelected(Boolean.valueOf(newValue.toString()).booleanValue());
				}
			}
		}
	}
	public void sortBatches(int colIndex, boolean ascending) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeProductBatchModel(ProductBatchModel productBatchModel){
		int index = batches.indexOf(productBatchModel);
		reArrangeMolString(index);
		if(productBatchModel.isLoadedFromDB())
			productBatchModel.setToDelete(true);
		batches.remove(productBatchModel);
		if (listOfBatchesWithMolStrings != null)
			listOfBatchesWithMolStrings.remove(index);
	}
	
	private void reArrangeMolString(int index) {
		for (int i=index; i<listOfBatchesWithMolStrings.size(); i++)
			((ParallelCeNUtilObject)listOfBatchesWithMolStrings.get(i)).getMolString().setIndex(i-1);
	}

	public StoicModelInterface getBatchModel(int selectedRowIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isMoreDeletableRows(ReactionStepModel reactionStepModel) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isColumnEditable(String columnName) {
		if (columnName.equalsIgnoreCase("Select"))
			if (!batchSelectionMode)
				return true;
		return false;
	}

	public void addStoichDataChangesListener(
			StoichDataChangesListener stoichDataChangesListener)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public TablePreferenceDelegate getTablePreferenceDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	public NotebookPageModel getPageModel() {
		// TODO Auto-generated method stub
		return null;
	}	
}