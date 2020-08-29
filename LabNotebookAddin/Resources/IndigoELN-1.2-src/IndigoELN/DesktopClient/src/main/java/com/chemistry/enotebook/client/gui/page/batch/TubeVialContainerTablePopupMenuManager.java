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
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewPopupMenuManager;
import com.chemistry.enotebook.domain.BatchSubmissionContainerInfoModel;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class TubeVialContainerTablePopupMenuManager extends PCeNTableViewPopupMenuManager {

	// Reference table
	private PCeNTableView mTable;
	private TubeVialContainerTableConnector connector;
	// popup Menus on products table in stoich
	private JPopupMenu jPopupMenuOptions;
	private JMenuItem jMenuItemAppendRow;
	private JMenuItem jMenuItemDeleteRow;
	private JMenuItem jMenuItemClearRow;
	private JMenuItem jMenuItemEditCell;
	private Point popupPoint;

	private BatchEditPanel batchPanel;

	public TubeVialContainerTablePopupMenuManager() {
		init();
	}

	public TubeVialContainerTablePopupMenuManager(BatchEditPanel mbatchPanel) {
		this();
		this.batchPanel = mbatchPanel;
	}

	private void init() {
		jMenuItemAppendRow = new JMenuItem("Append row");
		jMenuItemAppendRow.setEnabled(true);
		jMenuItemAppendRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addContainerRow();
			}
		});
		
		jMenuItemDeleteRow = new JMenuItem("Delete selected row");
		jMenuItemDeleteRow.setEnabled(true);
		jMenuItemDeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteContainerRow();
			}
		});
		
		jMenuItemClearRow = new JMenuItem("Clear selected row");
		jMenuItemClearRow.setEnabled(true);
		jMenuItemClearRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clearContainerRow();
			}
		});
		
		jMenuItemEditCell = new JMenuItem("Edit cell");
		jMenuItemEditCell.setEnabled(true);
		jMenuItemEditCell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				editContainerCell();
			}
		});
		
		jPopupMenuOptions = new JPopupMenu();

		jPopupMenuOptions.add(jMenuItemEditCell);
		jPopupMenuOptions.add(jMenuItemAppendRow);
		jPopupMenuOptions.add(jMenuItemDeleteRow);
		jPopupMenuOptions.add(jMenuItemClearRow);

		jPopupMenuOptions.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});
	}

	public void addMouseListener(JTable table) {
		if (table instanceof PCeNTableView) {
			mTable = (PCeNTableView) table;
			connector = (TubeVialContainerTableConnector) mTable.getConnector();
		}
		
		mTable.addMouseListener(this);
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
		if (e.isPopupTrigger()) {
			this.popupPoint = e.getPoint();
			preparePopupMenuAndOptions(e);
			jPopupMenuOptions.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	private boolean isEditable() {
		return true;
	}

	private BatchSubmissionContainerInfoModel getContainerAt(MouseEvent evt) {
		Point p = evt.getPoint();
		int row = mTable.rowAtPoint(p);
		if (row >= 0 && row < mTable.getRowCount()) {
			return null;
		} else {
			return null;
		}
	}

	public void preparePopupMenuAndOptions(MouseEvent evt) {
		// Determine what should be available.
		boolean isEnabled = false;
		BatchSubmissionContainerInfoModel se = getContainerAt(evt);
		// If the experiment is not editable
		if (!isEditable()) {
			isEnabled = isEditable();

			jMenuItemAppendRow.setEnabled(isEnabled);
			jMenuItemDeleteRow.setEnabled(isEnabled);
			jMenuItemClearRow.setEnabled(isEnabled);
			this.jMenuItemEditCell.setEnabled(isEnabled);
		} else {
			// If Editable
			int selectedRow = mTable.getSelectedRow();

			jMenuItemAppendRow.setEnabled(true);
			
			if (selectedRow != -1) {
				jMenuItemDeleteRow.setEnabled(true);
				jMenuItemClearRow.setEnabled(true);
			} else {
				jMenuItemDeleteRow.setEnabled(false);
				jMenuItemClearRow.setEnabled(false);
			}
			
			Point p = evt.getPoint();
			
			int col = mTable.columnAtPoint(p);
			int row = mTable.rowAtPoint(p);

			// check to see if cell is editable
			if (col != -1 && row != -1 && !mTable.getColumnModel().getColumn(col).getHeaderValue().equals(PCeNTableView.MOLES_IN_TUBE_VIAL) && !mTable.getColumnModel().getColumn(col).getHeaderValue().equals(PCeNTableView.CONTAINER_LOCATION)) {
				this.jMenuItemEditCell.setEnabled(true);
			} else {
				this.jMenuItemEditCell.setEnabled(false);
			}

		}// else editable()
	}

	public PCeNTableModelConnector getTableViewControllerUtility() {
		PCeNTableModel tm = (PCeNTableModel) mTable.getModel();
		// return (PCeNStoich_MonomersTableViewControllerUtility)
		// tm.getConnector();
		return tm.getConnector();
	}

	private void addContainerRow() {
		batchPanel.addVialTubeContainerToBatch();
		connector.fireTableDataChanged();
	}

	private void deleteContainerRow() {
		int row = mTable.getSelectedRow();

		if (row >= 0) {
			batchPanel.deleteVialTubeContainerInBatch(row);
			connector.fireTableDataChanged();
		} else {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No row has been selected to delete.");
		}
	}

	private void clearContainerRow() {
		int row = mTable.getSelectedRow();

		if (row >= 0) {
			batchPanel.clearVialTubeContainerInBatch(row);
			connector.fireTableDataChanged();
		} else {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"No row has been selected to clear.");
		}
	}

	// don't forget about hidden columns
	private void editContainerCell() {
		mTable.editCellAt(mTable.rowAtPoint(popupPoint), mTable.columnAtPoint(popupPoint));
		mTable.findComponentAt(popupPoint).requestFocusInWindow();
	}
}
