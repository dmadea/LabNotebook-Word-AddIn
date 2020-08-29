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
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.client.gui.common.utils.CeNTableCellAlignedComponentRenderer;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * 
 * 
 */
public class PCeNBatchInfoTableView extends PCeNTableView { //implements ProductBatchDetailsContainerListener {

	private static final long serialVersionUID = -3923062621467462135L;
	
	//private BatchSelectionListener batchSelectionListener = null;
	private BatchSelectionListener[] batchSelectionListeners = null;

	// vb 6/28 added methods to invoke batchSelctionChaged in the BatchEditPanel when the user clicks on a new row
	public PCeNBatchInfoTableView(PCeNTableModel model, int rowHeight,PCeNTableModelConnector connector, NotebookPageModel pageModel) {
		super(model, rowHeight, connector,null, pageModel);
		/**This event is redundant. But, right now this is the only way to capture the selected row of table when a combo box on a cell is clicked. 
		 * Note:  
		 * Please observe the issue in the BatchEditPanel when a table cell combo box is clicked for row selection. 
		 * It does not refresh even though the table row selected changed.*/
//		SelectionListener listener = new SelectionListener(this);
//		this.getSelectionModel().addListSelectionListener(listener);
	}

    public Dimension getPreferredScrollableViewportSize() {
        final Dimension d = getPreferredSize();
        return new Dimension(d.width, d.height + 20);
    }

	// vb change 6/13 public void setProductBatchDetailsContainerListener(ProductBatchDetailsContainerListener
	// mmProductBatchDetailsContainerListener){
	public void setProductBatchDetailsContainerListener(BatchSelectionListener batchSelectionListener) {
		this.batchSelectionListeners = new BatchSelectionListener[1];
		batchSelectionListeners[0] = batchSelectionListener;
	}
	
	public void setProductBatchDetailsContainerListenerList(BatchSelectionListener[] listeners) {
		this.batchSelectionListeners = listeners;
	}
	
	public BatchSelectionListener[] getProductBatchDetailsContainerListenerList() {
		return this.batchSelectionListeners;
	}
	
	// vb 11/20
	public BatchModel getSelectedBatch() {
		PCeNTableModel model = (PCeNTableModel) getModel();
		PCeNProductTableModelConnector connector = (PCeNProductTableModelConnector) model.getConnector();
		if (this.getSelectedRow() > -1)
			return (BatchModel) connector.getProductBatchModel(this.getSelectedRow());
		else
			return null;
	}
	
	public void valueChanged() {
		if (batchSelectionListeners != null && batchSelectionListeners.length > 0) {
			int a = this.getSelectedRow();
			if (a == -1) {
				return;
			}
			if (getModel() instanceof PCeNTableModel) {
				PCeNTableModel model = (PCeNTableModel) getModel();
				PCeNTableModelConnector connector = model.getConnector();
				if (connector instanceof PCeNPlateTableRowSelectionChangeHandler) {
					PCeNPlateTableRowSelectionChangeHandler utility = (PCeNPlateTableRowSelectionChangeHandler) connector;
					Object eventObject = ((utility.getPlateWell(a)== null) ? utility.getBatchModel(a) : (Object)utility.getPlateWell(a));  
					BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, eventObject);
					for (int i=0; i<batchSelectionListeners.length; i++)
						batchSelectionListeners[i].batchSelectionChanged(batchSelectionEvent);
				} else if(connector instanceof PCeNProductTableRowSelectionChangeHandler) {
					PCeNProductTableRowSelectionChangeHandler utility = (PCeNProductTableRowSelectionChangeHandler) connector;
					BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, utility.getBatchModel(a));
					for (int i=0; i<batchSelectionListeners.length; i++)
						batchSelectionListeners[i].batchSelectionChanged(batchSelectionEvent);
					
					if(this.getColumnName(this.getSelectedColumn()).equals(PCeNTableView.SELECT_OPTION)) {
						ProductBatchModel productBatchModel = (ProductBatchModel)((PCeNTableModelConnector)connector).getBatchModel(a);
						productBatchModel.setSelected(!productBatchModel.isSelected());
						this.repaint();
					}
				}
			}
			//This is added to resolve a bug (ticket #741) First row in tables cannot be selected until after clicking another row
			if (a == 0)
			{
				this.clearSelection();
				this.setRowSelectionInterval(0, 0);
			}
		}
	}

	public void setRenderers(String name) {
		super.setRenderers(name);		
		if (name.equalsIgnoreCase(BARCODE)) {
			TableColumn col = getColumn(name);
			col.setCellEditor(new PCeNTableViewCellEditor(new JTextField()));
			col.setCellRenderer(new CeNTableCellAlignedComponentRenderer(new JLabel()));
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		super.setValueAt(value, row, column);
		this.valueChanged();
	}
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		
		ProductBatchModel rowBatch = (ProductBatchModel) ((PCeNTableModel) getModel()).getStoicElementAt(row);
		String columnName = getColumnName(column);
			
		if (StringUtils.equals(columnName, PCeNTableView.NBK_BATCH_NUM)) {
			if (rowBatch != null && rowBatch.isChloracnegen()) {
				c.setBackground(Color.red);
			} else {
				c.setBackground(Color.white);
			}
		}		
						
		return c;
	}
}
