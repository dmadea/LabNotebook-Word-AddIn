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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellEditor;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.UnitCache2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TubeVialContainerTableConnector implements PCeNTableModelConnector {

	public static final int WT_IN_TUBE_VIAL_IDX = 0;
	public static final int MOLES_IN_TUBE_IDX = 1;
	public static final int VOL_IN_TUBE_IDX = 2;
	public static final int BARCODE_TUBE_VIAL_IDX = 3;
	public static final int SOLVENT_IDX = 4;
	public static final int CONTAINER_TYPE_IDX = 5;
	public static final int CONTAINER_LOCATION_IDX = 6;
	private static final int MAX_COLS = 7;
	
	protected boolean readOnly = false;
	
	private NotebookPageModel pageModel;
	private ProductBatchModel batchModel;
	
	private final String[] headerNames = new String[] {
			PCeNTableView.WT_IN_TUBE_VIAL, PCeNTableView.MOLES_IN_TUBE_VIAL,
			PCeNTableView.VOL_IN_TUBE_VIAL, PCeNTableView.BARCODE_TUBE_VIAL,
			PCeNTableView.SOLVENT, PCeNTableView.CONTAINER_TYPE,
			PCeNTableView.CONTAINER_LOCATION };
	
	private JTable table; // TODO get rid of this reference - use listener
	
	protected List<AmountEditListener> editlisteners = new ArrayList<AmountEditListener>();

	public TubeVialContainerTableConnector(NotebookPageModel mpagemodel, ProductBatchModel mbatchModel) {
		this.pageModel = mpagemodel;
		this.batchModel = mbatchModel;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean flag) {
		readOnly = flag;
	}

	public int getRowCount() {
		if (this.batchModel == null || this.batchModel.getRegInfo() == null) {
			// No batch selected in batches tab
			return 0;
		}
		
		return batchModel.getRegInfo().getSubmitContainerListSize();
	}

	public int getColumnCount() {
		return MAX_COLS;
	}

	public String[] getHeaderNames() {
		return new String[] { PCeNTableView.WT_IN_TUBE_VIAL,
				PCeNTableView.MOLES_IN_TUBE_VIAL,
				PCeNTableView.VOL_IN_TUBE_VIAL,
				PCeNTableView.BARCODE_TUBE_VIAL, PCeNTableView.SOLVENT,
				PCeNTableView.CONTAINER_TYPE, PCeNTableView.CONTAINER_LOCATION };
	}

	public String getColumnName(int column) {
		switch (column) {
		case WT_IN_TUBE_VIAL_IDX:
			return PCeNTableView.WT_IN_TUBE_VIAL;
		case MOLES_IN_TUBE_IDX:
			return PCeNTableView.MOLES_IN_TUBE_VIAL;
		case VOL_IN_TUBE_IDX:
			return PCeNTableView.VOL_IN_TUBE_VIAL;
		case BARCODE_TUBE_VIAL_IDX:
			return PCeNTableView.BARCODE_TUBE_VIAL;
		case SOLVENT_IDX:
			return PCeNTableView.SOLVENT;
		case CONTAINER_TYPE_IDX:
			return PCeNTableView.CONTAINER_TYPE;
		case CONTAINER_LOCATION_IDX:
			return PCeNTableView.CONTAINER_LOCATION;
		}
		return "";
	}

	public boolean isCellEditable(int nRow, int nCol) {
		String name = headerNames[nCol];
		if (name.equals(PCeNTableView.CONTAINER_LOCATION)
				|| name.equals(PCeNTableView.MOLES_IN_TUBE_VIAL)
				|| isReadOnly()) {
			return false;
		}

		return (batchModel != null && pageModel.isEditable() && batchModel.getRegInfo().isCompoundManagementNotSubmitted());
	}

	public boolean isColumnEditable(String columnName) {
		if (columnName.equals(PCeNTableView.CONTAINER_LOCATION)
				|| columnName.equals(PCeNTableView.MOLES_IN_TUBE_VIAL)
				|| isReadOnly()) {
			return false;
		}

		return (batchModel != null && pageModel.isEditable() && batchModel.getRegInfo().isCompoundManagementNotSubmitted());
	}

	public Object getValue(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount()) {
			return "";
		}
		
		BatchSubmissionContainerInfoModel si = batchModel.getRegInfo().getSubmitContainerListRow(nRow);
		
		switch (nCol) {
		case WT_IN_TUBE_VIAL_IDX:
			AmountModel val = new AmountModel();
			val.setUnit(UnitCache2.getInstance().getUnit(si.getAmountUnit()));
			val.setValue(si.getAmountValue());
			return val;
			
		case MOLES_IN_TUBE_IDX:
			AmountModel val2 = new AmountModel();
			val2.setUnit(UnitCache2.getInstance().getUnit(si.getMoleUnit()));
			val2.setValue(si.getMoleValue());
			return val2;
			
		case VOL_IN_TUBE_IDX:
			AmountModel val3 = new AmountModel();
			val3.setUnit(UnitCache2.getInstance().getUnit(si.getVolumeUnit()));
			val3.setValue(si.getVolumeValue());
			return val3;
			
		case BARCODE_TUBE_VIAL_IDX:
			return si.getBarCode() == null ? "" : si.getBarCode();
			
		case SOLVENT_IDX:
			String desc = "";
			
			try {
				desc = CodeTableCache.getCache().getSolventDescription(si.getSolvent() == null ? "" : si.getSolvent());
			} catch (CodeTableCacheException e) {
				e.printStackTrace();
			}
			
			return desc == null ? "" : desc;
			
		case CONTAINER_TYPE_IDX:
			return si.getContainerType() == null ? "" : si.getContainerType();
			
		case CONTAINER_LOCATION_IDX:
			return si.getContainerType() == null ? "" : si.getContainerLocation();
		}
		
		return "";
	}

	public void setValue(Object aValue, int rowIndex, int columnIndex) {
		BatchSubmissionContainerInfoModel si = batchModel.getRegInfo().getSubmitContainerListRow(rowIndex);

		if (si == null || aValue == null) {
			return;
		}
		
		switch (columnIndex) {
		case WT_IN_TUBE_VIAL_IDX:
			AmountModel val = (AmountModel) aValue; // TODO use sig figs in
													// standalone containers
													// table

			si.setAmountUnit(val.getUnit().getCode());
			si.setAmountValue(val.doubleValue());
			AmountModel val2 = this.getContainerMoles(val);

			si.setMoleUnit(val2.getUnit().getCode());
			si.setMoleValue(val2.doubleValue());

			fireEditingStopped(); // update Amount Remaining
			break;

		case MOLES_IN_TUBE_IDX:
			// AmountModel val2 = (AmountModel)aValue;
			// si.setMoleUnit(val2.getUnit().getCode());
			// si.setMoleValue(val2.doubleValue());
			break;

		case VOL_IN_TUBE_IDX:
			AmountModel val3 = (AmountModel) aValue;
			si.setVolumeUnit(val3.getUnit().getCode());
			si.setVolumeValue(val3.doubleValue());
			fireEditingStopped();
			break;

		case BARCODE_TUBE_VIAL_IDX:
			si.setBarCode((String) aValue);
			break;
			
		case SOLVENT_IDX:
			String solvDesc = (String) aValue;
			String code = "None";
			
			try {
				code = CodeTableCache.getCache().getSolventCode(solvDesc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			si.setSolvent(code);
			break;
			
		case CONTAINER_TYPE_IDX:
			si.setContainerType(((ContainerTypeLocationModel) aValue).getType());
			si.setContainerLocation(((ContainerTypeLocationModel) aValue).getLocation());
			fireTableDataChanged();
			break;
			
		case CONTAINER_LOCATION_IDX:
			break;
		}
		
		this.batchModel.getRegInfo().setModified(true);
		this.batchModel.setModelChanged(true);
		this.pageModel.setModelChanged(true);
		
		MasterController.getGUIComponent().refreshIcons(); // ensure that the
															// Save menu item
															// gets enabled

		fireTableDataChanged();
	}

	public void resetModelData(List<BatchSubmissionContainerInfoModel> mmList) {
		readOnly = false;

		for (BatchSubmissionContainerInfoModel batchCont : mmList) {
			this.batchModel.getRegInfo().addSubmitContainer(batchCont);
		}

		fireTableDataChanged();
	}

	public void addModelData() {
		try {
			BatchSubmissionContainerInfoModel sd = new BatchSubmissionContainerInfoModel();
			this.batchModel.getRegInfo().addSubmitContainer(sd);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void deleteModelData(int index) {
		try {
			this.batchModel.getRegInfo().removeSubmitContainer(index);
			fireTableDataChanged();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public void fireTableDataChanged() {
		if (table != null) {
			table.invalidate();
			table.repaint(); // TODO need to implement using an event instead,
								// but this is quick and in a hurry
		}
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public boolean isStructureHidden() {
		return true;
	}

	public List<?> getAbstractBatches() {
		return null;
	}

	public String getTableHeaderTooltip(String headerName) {
		return "";
	}

	public List<StoicModelInterface> getStoicElementListInTransactionOrder() {
		return null;
	}

	public boolean isSortable(int colIndex) {
		return false;
	}

	public void updateColumn(String columnName, Object newValue) {
		boolean update = false;
		
		List<BatchSubmissionContainerInfoModel> list = this.batchModel.getRegInfo().getSubmitContainerList();
		Iterator<BatchSubmissionContainerInfoModel> iter = list.iterator();

		while (iter.hasNext()) {
			BatchSubmissionContainerInfoModel bsci = iter.next();

			if (columnName.equals(PCeNTableView.WT_IN_TUBE_VIAL)) {
				if (!(newValue instanceof PAmountCellEditor)) {
					throw new IllegalArgumentException("Weight in tube must be an amount");
				}
				
				PAmountCellEditor amtEditor = (PAmountCellEditor) newValue;
				AmountModel val = amtEditor.getAmount();

				bsci.setAmountUnit(val.getUnit().getCode());
				bsci.setAmountValue(val.doubleValue());
				AmountModel val2 = this.getContainerMoles(val);

				bsci.setMoleUnit(val2.getUnit().getCode());
				bsci.setMoleValue(val2.doubleValue());
				
				update = true;
			} else if (columnName.equals(PCeNTableView.VOL_IN_TUBE_VIAL)) {
				if (!(newValue instanceof PAmountCellEditor)) {
					throw new IllegalArgumentException("Volume in tube must be an amount");
				}
				
				PAmountCellEditor amtEditor = (PAmountCellEditor) newValue;
				AmountModel val = amtEditor.getAmount();

				bsci.setVolumeUnit(val.getUnit().getCode());
				bsci.setVolumeValue(val.doubleValue());
				
				update = true;
			} else if (columnName.equals(PCeNTableView.BARCODE_TUBE_VIAL)) {
				if (!(newValue instanceof String)) {
					throw new IllegalArgumentException("Barcode must be a String");
				}
				
				bsci.setBarCode((String) newValue);
				
				update = true;
			} else if (columnName.equals(PCeNTableView.SOLVENT)) {
				if (!(newValue instanceof String)) {
					throw new IllegalArgumentException("Solvent description must be a String");
				}
				
				String solvDesc = (String) newValue;
				String code = "None";
				
				try {
					code = CodeTableCache.getCache().getSolventCode(solvDesc);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				bsci.setSolvent(code);
				
				update = true;
			} else if (columnName.equals(PCeNTableView.CONTAINER_TYPE)) {
				bsci.setContainerType(((ContainerTypeLocationModel) newValue).getType());
				bsci.setContainerLocation(((ContainerTypeLocationModel) newValue).getLocation());
				
				update = true;
			}
		}

		if (update) {
			this.batchModel.getRegInfo().setModified(true);
			this.batchModel.setModelChanged(true);
			this.pageModel.setModelChanged(true);
			MasterController.getGUIComponent().refreshIcons(); // ensure that
																// the Save menu
																// item gets
																// enabled

			fireEditingStopped(); // update Amount Remaining
			fireTableDataChanged();
		}
	}

	public void setSelectValue(int selectedRow) {

	}

	public void sortBatches(int colIndex, boolean ascending) {

	}

	public void addStoichDataChangesListener(StoichDataChangesListener stoichDataChangesListener) throws Exception {

	}

	public StoicModelInterface getBatchModel(int selectedRow) {
		return null;
	}

	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		return null;
	}

	public TablePreferenceDelegate getTablePreferenceDelegate() {
		return null;
	}

	public NotebookPageModel getPageModel() {
		return this.pageModel;
	}

	public void setProductBatchModel(ProductBatchModel model) {
		this.batchModel = model;
		fireTableDataChanged();
	}

	public AmountModel getContainerMoles(AmountModel containerAmt) {
		if (batchModel == null || containerAmt == null) {
			return null;
		}
		
		double totalMoles = batchModel.getTotalWeight().GetValueInStdUnitsAsDouble() / batchModel.getMolWgt();
		double containerMoles = totalMoles * (containerAmt.GetValueInStdUnitsAsDouble() / batchModel.getTotalWeight().GetValueInStdUnitsAsDouble());
		
		AmountModel molesAmount = new AmountModel(UnitType.MOLES,containerMoles);
		molesAmount.setUnit(batchModel.getTheoreticalMoleAmount().getUnit());

		return molesAmount;
	}

	//
	// Editing Functions
	//
	
	public void addAmountEditListener(AmountEditListener ael) {
		if (!editlisteners.contains(ael)) {
			editlisteners.add(ael);
		}
	}

	public void removeAmountEditListener(AmountEditListener ael) {
		if (editlisteners.size() > 0) {
			editlisteners.remove(ael);
		}
	}

	protected void fireEditingCanceled() {
		ChangeEvent ce = new ChangeEvent(this);

		for (int i = editlisteners.size() - 1; i >= 0; i--) {
			((AmountEditListener) editlisteners.get(i)).editingCanceled(ce);
		}
	}

	protected void fireEditingStopped() {
		ChangeEvent ce = new ChangeEvent(this);

		for (int i = editlisteners.size() - 1; i >= 0; i--) {
			((AmountEditListener) editlisteners.get(i)).editingStopped(ce);
		}
	}
}
