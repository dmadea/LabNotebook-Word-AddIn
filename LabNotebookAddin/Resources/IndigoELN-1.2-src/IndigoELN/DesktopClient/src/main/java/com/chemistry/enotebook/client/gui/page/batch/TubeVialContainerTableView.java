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

import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellEditor;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountCellRenderer;
import com.chemistry.enotebook.client.gui.page.experiment.table.StoicConstants;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewPopupMenuManager;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.CodeTableUtils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;

public class TubeVialContainerTableView extends PCeNTableView {

	private static final long serialVersionUID = -4342970017567101779L;
	
	private ProductBatchModel productBatchModel;
	
	public TubeVialContainerTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector mvconnector, PCeNTableViewPopupMenuManager tableViewPopupMenuManager,ProductBatchModel mBatchModel) {
		super(model,rowHeight,mvconnector,tableViewPopupMenuManager);
		
		this.productBatchModel = mBatchModel;
		this.mTableViewPopupMenuManager = tableViewPopupMenuManager;
		
		if (this.mTableViewPopupMenuManager != null) {
			this.mTableViewPopupMenuManager.addMouseListener(this);
		}
		
		this.setModel(model);
		
		this.ROW_HEIGHT = rowHeight;

		this.setAutoResizeMode(AUTO_RESIZE_OFF);
		this.setAutoCreateColumnsFromModel(true);

		this.setRowHeight(ROW_HEIGHT);
		
		this.setColumnSelectionAllowed(false);
		this.setCellSelectionEnabled(false);
		this.setRowSelectionAllowed(true);

		this.setTableViewCellRenderers();

		((TubeVialContainerTableConnector) model.getConnector()).setTable(this);  // TODO need to implement fireTableDataChanged() in connector
	}
	
	/**
	 * Enable popup menu in the "empty" areas of the Vials/Tubes JTable.
	 */
	public boolean getScrollableTracksViewportHeight() {
		// fetch the table's parent
		Container viewport = getParent();

		// if the parent is not a viewport, calling this isn't useful
		if (!(viewport instanceof JViewport)) {
			return false;
		}

		// return true if the table's preferred height is smaller
		// than the viewport height, else false
		return getPreferredSize().height < viewport.getHeight();
	}

	
	protected void setTableViewCellRenderers() {
		TubeVialContainerTableViewCellRenderer cellRenderer = new TubeVialContainerTableViewCellRenderer();
		TubeVialContainerTableViewCellEditor cellEditor = new TubeVialContainerTableViewCellEditor();
		
		int colCount = getColumnCount();
		TableCellEditor containerTypeCellEditor;
		JComboBox jComboContainerType = new CeNComboBox();

		for (int i = 0; i < colCount; i++) {
			String name = getColumnName(i);
			TableColumn col = getColumn(name);
			// Set all amount column renderers
			if (name.equals(PCeNTableView.WT_IN_TUBE_VIAL) || name.equals(PCeNTableView.VOL_IN_TUBE_VIAL) || name.equals(PCeNTableView.MOLES_IN_TUBE_VIAL)) {
				col.setCellEditor(new PAmountCellEditor());
				col.setCellRenderer(new PAmountCellRenderer());
			} else if (name.equalsIgnoreCase(PCeNTableView.SOLVENT)) {
				col.setCellEditor(solventCellEditor);
				col.setCellRenderer(cellRenderer);
			} else if (name.equalsIgnoreCase(PCeNTableView.CONTAINER_TYPE)) {
				CodeTableUtils.fillComboBoxWithContainerTypes(jComboContainerType);
				jComboContainerType.setSelectedIndex(-1);
				containerTypeCellEditor = new DefaultCellEditor(jComboContainerType);
				col.setCellEditor(containerTypeCellEditor);
				col.setCellRenderer(cellRenderer);
			} else {
				col.setCellEditor(cellEditor);
				col.setCellRenderer(cellRenderer);
			}
		}// end for
	}

	public static void main(String[] args){
		JFrame frame = new JFrame();
		TubeVialContainerTablePopupMenuManager tubeVialTablePopupManager = new TubeVialContainerTablePopupMenuManager(); 
		JPanel vialTubeTablePanel = new JPanel();
		vialTubeTablePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		JLabel jLabelContainerHeader = new JLabel("Vials/Tubes Containing this Batch");
		jLabelContainerHeader.setFont(new java.awt.Font("Arial", Font.BOLD, 10));
		vialTubeTablePanel.setLayout(new BorderLayout());

		vialTubeTablePanel.add(jLabelContainerHeader, BorderLayout.NORTH);
		JScrollPane jScrollPane = new JScrollPane();
		TubeVialContainerTableConnector tubeVialTableConnector = new TubeVialContainerTableConnector(null,null);
		TubeVialContainerTableView jTableTubeVialContainers = new TubeVialContainerTableView(new TubeVialContainerTableModel(tubeVialTableConnector),StoicConstants.TABLE_ROW_HEIGHT,tubeVialTableConnector,tubeVialTablePopupManager,null);
		jScrollPane.add(jTableTubeVialContainers);
		jScrollPane.setViewportView(jTableTubeVialContainers);
		vialTubeTablePanel.add(jScrollPane, BorderLayout.CENTER);
		frame.add(vialTubeTablePanel);
		frame.pack();
		frame.setVisible(true);
	}
}
