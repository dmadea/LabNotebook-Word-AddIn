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
package com.chemistry.enotebook.client.gui.page.batch.editors;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.batch.MeltingPointJDialog;
import com.chemistry.enotebook.client.gui.page.table.PCeNBatchInfoTableView;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.CommonUtils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class MeltingPointDialogEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108103806392644734L;
	private JButton button;
	protected static final String EDIT = "Edit";
    private JLabel labelText = new JLabel("Hello");
	private NotebookPageModel pageModel;
	
	public MeltingPointDialogEditor(NotebookPageModel pageModel) {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setOpaque(false);
        this.pageModel = pageModel;
	}

	public Component getTableCellEditorComponent(JTable arg0, Object arg1,
			boolean arg2, int arg3, int arg4) {
		 return button;
	}

	public Object getCellEditorValue() {
		return null;
	}

	public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
        	if (e.getSource() instanceof JButton) {
        		JButton button = (JButton)e.getSource();
        		if (button.getParent() instanceof PCeNBatchInfoTableView) {
        			PCeNBatchInfoTableView table = (PCeNBatchInfoTableView) button.getParent();
        			ProductBatchModel productBatchModel = (ProductBatchModel) table.getSelectedBatch();
        			if (productBatchModel != null) {
        	    		 JLabel jLabelC = new JLabel();
        	    		 Font PLAIN_LABEL_FONT = new java.awt.Font(CommonUtils.getStandardTableCellFont(), Font.PLAIN, 12);
        	    		 jLabelC.setFont(PLAIN_LABEL_FONT);
        	    		 MeltingPointJDialog.showGUI(MasterController.getGUIComponent(), productBatchModel, new JTextArea(), jLabelC, pageModel);
        			}
        		}
        	}
            //Make the renderer reappear.
            fireEditingStopped();

        } else { 
            // do nothing
        }
	}

	/**
	 * Use only when you have the mouse event that is in the coordinate space of this component.
	 * 
	 * This method must be overridden to be able to deal with a mouse event properly Otherwise the comboBox will require a
	 * clickToStart number of clicks to allow a selection.
	 * 
	 * @param anEvent -
	 *            mouse event if you want to process click count to start.
	 * @return - true or false
	 */
	public boolean isCellEditable(EventObject anEvent) {
		boolean result = true;
		if (anEvent instanceof MouseEvent && result) {
			// Mouse in txt field: apply standard clickCount to start
			result = result && (((MouseEvent) anEvent).getClickCount() >= clickCountToStart);
		}
		return result;
	}
	private int clickCountToStart = 2;	

}
