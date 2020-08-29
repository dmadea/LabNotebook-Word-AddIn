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
import com.chemistry.enotebook.client.gui.page.batch.PCeNBatch_BatchDetailsViewContainer;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundCreateInterface;
import com.chemistry.enotebook.client.gui.page.table.*;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ReactionStepModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ProductTablePopupMenuManager extends PCeNTableViewPopupMenuManager {

	//Reference table 
	private PCeNTableView mTable;
	private PCeNProductTableModelConnector connector;

	private JPopupMenu batchOptionsPopupMenu = new JPopupMenu();
	private JMenuItem addBatchMenuItem = new JMenuItem("Add Batch");
	private JMenuItem deleteBatchMenuItem = new JMenuItem("Delete Batch"); 
	private JMenuItem duplicateBatchMenuItem = new JMenuItem("Duplicate Batch");
	private JMenuItem syncProductsMenuItem = new JMenuItem("Sync with Intended Products");
	private ReactionStepModel reactionStepModel;
	private ProductBatchModel selectedProductBatchModel = null;
	private CompoundCreateInterface batchInfoContainer = null;
		
	public ProductTablePopupMenuManager() {
		init();
	}

	public ProductTablePopupMenuManager(CompoundCreateInterface batchInfoContainer) {
		this.batchInfoContainer = batchInfoContainer;
		init();
	}

	private void init() {
		addBatchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBatch();
			}
		});		
		duplicateBatchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				duplicateBatch();
			}
		});
		deleteBatchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteBatch();
			}
		});
		syncProductsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				syncIntendedProducts();
			}
		});
		batchOptionsPopupMenu.add(addBatchMenuItem);
		batchOptionsPopupMenu.add(duplicateBatchMenuItem);
		batchOptionsPopupMenu.add(deleteBatchMenuItem);
		if (batchInfoContainer instanceof PCeNBatch_BatchDetailsViewContainer) {
			batchOptionsPopupMenu.add(syncProductsMenuItem);
		}
	}

	protected void syncIntendedProducts() {
		if (batchInfoContainer != null) {
			batchInfoContainer.syncIntendedProducts();
		}
	}

	protected void duplicateBatch() {
		if (selectedProductBatchModel == null)
		{
			mTable.stopEditing();
			int selectedRowIndex = mTable.getSelectedRow();
			if (selectedRowIndex > -1) {
				selectedProductBatchModel = (ProductBatchModel) connector.getProductBatchModel(selectedRowIndex);
			}
		}
		if(selectedProductBatchModel != null && reactionStepModel != null){
			batchInfoContainer.duplicateCompound(selectedProductBatchModel);
			selectedProductBatchModel.setSelected(false);
		}
	}

	protected void addBatch() {
		batchInfoContainer.createCompound();
	}

	public void addMouseListener(JTable table, ReactionStepModel reactionStepModele) {
		if (table instanceof PCeNTableView) {
			mTable = (PCeNTableView) table;
			if (mTable == null) return;  // vbtodo fiox
				connector = (PCeNProductTableModelConnector) mTable.getConnector();
		}
		if (mTable == null) return;
		mTable.addMouseListener(this);
		reactionStepModel = reactionStepModele; 
	}

	@Override
	public void mousePressed(MouseEvent e) {
		showPopupMenu(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		showPopupMenu(e);
	}
	
	private void showPopupMenu(MouseEvent e) {
		if (!connector.getPageModel().isEditable()) {
			return;
		}
		
		selectedProductBatchModel = getProductBatchAt(e);
		
		if (e.isPopupTrigger()) {
			if(selectedProductBatchModel != null) {
				if (selectedProductBatchModel != null && !selectedProductBatchModel.isEditable()) {
					deleteBatchMenuItem.setEnabled(false);
				} else {
					deleteBatchMenuItem.setEnabled(true);
				}
				
				batchOptionsPopupMenu.show(e.getComponent(), e.getX(), e.getY());				
			}
		}
	}

	/*

	 private void deleteSelectedReagent() {
	 int row = mTable.getSelectedRow();
	 ParallelCeNTableModel rtm = (ParallelCeNTableModel) mTable.getModel();
	 StoicModelInterface model =  rtm.getStoicElementAt(row);
	 connector.removeBatch(model);
	 mTable.updateUI();
	 }
	 */
	private boolean isEditable() {
		return true;
	}

	private ProductBatchModel getProductBatchAt(MouseEvent evt) {
		Point p = evt.getPoint();
		int row = mTable.rowAtPoint(p);
		PCeNTableModel rtm = (PCeNTableModel) mTable.getModel();
		if (row > -1 && row < mTable.getRowCount()) {
			mTable.setRowSelectionInterval(row, row);
			PCeNTableModelConnector tableViewMVConnector = rtm.getConnector();
			return (ProductBatchModel) tableViewMVConnector.getAbstractBatches().get(row);
		}
		return null;
	}
	
	public void deleteBatch(){  
		if(selectedProductBatchModel != null && reactionStepModel != null){		
			batchInfoContainer.deleteCompound(selectedProductBatchModel);
		}
	}
	
	private void enableSaveButton() {
		MasterController.getGUIComponent().enableSaveButtons();
	}
	
	public PCeNTableModelConnector getTableViewControllerUtility() {
		PCeNTableModel tm = (PCeNTableModel) mTable.getModel();
		return tm.getConnector();
	}
	
	public void resetDeleteButton() {
		if (selectedProductBatchModel == null) {
			if (mTable == null) {
				return;
			}
			int selectedRowIndex = mTable.getSelectedRow();
			if (selectedRowIndex > -1) {
				selectedProductBatchModel = (ProductBatchModel)connector.getProductBatchModel(selectedRowIndex);
			}
		}
	}
	
	public void updateSyncWithIntendedProductsActionState(boolean state) {
		syncProductsMenuItem.setEnabled(state);
	}
}
