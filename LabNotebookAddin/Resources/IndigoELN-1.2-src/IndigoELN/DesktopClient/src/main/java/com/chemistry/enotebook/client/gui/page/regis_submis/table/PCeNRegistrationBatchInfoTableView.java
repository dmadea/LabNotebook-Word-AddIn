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
package com.chemistry.enotebook.client.gui.page.regis_submis.table;

import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.regis_submis.PCeNRegistration_SummaryTableViewCellEditor;
import com.chemistry.enotebook.client.gui.page.regis_submis.PCeNRegistration_SummaryTableViewCellRender;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationAbstractTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.*;
import com.chemistry.enotebook.domain.ProductBatchModel;

import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;

//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableViewHeaderRenderer;

public class PCeNRegistrationBatchInfoTableView  extends PCeNTableView {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7459032779603617859L;
	private PCeNRegistration_SummaryTableViewCellEditor mPcenRegistrationSummaryTableViewCellEditor;
	private PCeNRegistration_SummaryTableViewCellRender mPcenRegistrationSummaryTableViewCellRender = new PCeNRegistration_SummaryTableViewCellRender();
	private BatchSelectionListener[] batchSelectionListeners = null;
	private int selectedRow = -1;
		
	/**
	 * Constructor
	 * @param model the table model
	 * @param rowHeight initial height of the rows
	 * @param connector the controller link between the model and the table view
	 * @param ceNRegistration_BatchDetailsViewContainer 
	 */
	public PCeNRegistrationBatchInfoTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connector ) {
		super(model, rowHeight, connector, null, connector.getPageModel());
		mPcenRegistrationSummaryTableViewCellEditor = new PCeNRegistration_SummaryTableViewCellEditor((null));
		setTableViewCellRenderers();
//		this.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent evt) {
//				if (evt.getClickCount() == 1) {
//					checkboxclicked(evt);
//				}
//			} // end mouseClicked
//		});
	}

    public Dimension getPreferredScrollableViewportSize() {
        final Dimension d = getPreferredSize();
        return new Dimension(d.width, d.height + 20);
    }    
	
	public void setProductBatchDetailsContainerListenerList(BatchSelectionListener[] listeners) {
		this.batchSelectionListeners = listeners;
	}

	/**
	 * Handle the event when a row is selected by clicking on the checkbox.
	 * @param evt
	 */
//	private void checkboxclicked(MouseEvent evt) {
//		String columnName = getColumnName(this.getSelectedColumn());
//		if (columnName.equalsIgnoreCase("Select")) {
//			ProductBatchModel batchModel = (ProductBatchModel)connector.getAbstractBatches().get(this.getSelectedRow());
//			connector.setSelectValue(this.getSelectedRow());
//			this.repaint();
//		}
//	}

	/**
	 * Set non-default table cell renderer to the ParallelCeNTableViewHeaderRenderer.
	 */
	protected void setTableViewCellRenderers() {
		PCeNTableViewHeaderRenderer headerRenderer = new PCeNTableViewHeaderRenderer();
		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++) {
			if (getColumnName(i).equals(PCeNTableView.SELECT))
			{
				TableColumn col = getColumn(getColumnName(i));
				col.setCellRenderer(mPcenRegistrationSummaryTableViewCellRender);
				col.setHeaderRenderer(headerRenderer);
				col.setCellEditor(mPcenRegistrationSummaryTableViewCellEditor);
			}
			else
				super.setRenderers(getColumnName(i));
		}// end for
	}
	
	public void valueChanged() {
		String columnName = getColumnName(this.getSelectedColumn());
		if (columnName.equalsIgnoreCase(PCeNTableView.SELECT)) {
			ProductBatchModel batchModel = (ProductBatchModel)connector.getAbstractBatches().get(this.getSelectedRow());
			connector.setSelectValue(this.getSelectedRow());
			this.repaint();
		}
		
		if (batchSelectionListeners != null && batchSelectionListeners.length > 0) {
			int a = this.getSelectedRow();
			selectedRow = a;

			if (getModel() instanceof PCeNTableModel) {

				PCeNTableModel model = (PCeNTableModel) getModel();
				PCeNTableModelConnector connector = model.getConnector();
				if (connector instanceof PCeNPlateTableRowSelectionChangeHandler) {
					PCeNPlateTableRowSelectionChangeHandler utility = (PCeNPlateTableRowSelectionChangeHandler) connector;
					Object eventObject = ((utility.getPlateWell(a)== null) ? utility.getBatchModel(a) : (Object)utility.getPlateWell(a));  
					BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, eventObject);
					for (int i=0; i<batchSelectionListeners.length; i++)
						batchSelectionListeners[i].batchSelectionChanged(batchSelectionEvent);

				}else if(connector instanceof PCeNProductTableRowSelectionChangeHandler){
					PCeNProductTableRowSelectionChangeHandler utility = (PCeNProductTableRowSelectionChangeHandler) connector;
					BatchSelectionEvent batchSelectionEvent = new BatchSelectionEvent(this, utility.getBatchModel(a));
					for (int i=0; i<batchSelectionListeners.length; i++)
						batchSelectionListeners[i].batchSelectionChanged(batchSelectionEvent);
				}
			}
		}
	}

	public void setValueAt(Object value, int row, int column) {
		super.setValueAt(value, row, column);
		this.selectedRow = row;
		this.valueChanged();
	}
	
	public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);
        if (realColumnIndex >= 3 && realColumnIndex <= 7) { 
        	PCeNRegistrationAbstractTableModelConnector abstractConnector = (PCeNRegistrationAbstractTableModelConnector)connector;
            tip = abstractConnector.getToolTip(rowIndex, realColumnIndex);
        } else { //another column
            //You can omit this part if you know you don't 
            //have any renderers that supply their own tool 
            //tips.
            tip = super.getToolTipText(e);
        }
        return tip;
    }
}
