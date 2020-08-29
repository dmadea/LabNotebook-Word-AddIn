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
import com.chemistry.enotebook.client.gui.page.batch.PurityInfoJDialog;
import com.chemistry.enotebook.client.gui.page.table.PCeNBatchInfoTableView;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Properties;

public class PurityDialogEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener {
	  
    /**
	 * 
	 */
	private static final long serialVersionUID = -8485624202654704441L;
	JButton button;
    protected static final String EDIT = "Edit";
    JLabel labelText = new JLabel("Hello");
	private NotebookPageModel pageModel;

    public PurityDialogEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setOpaque(false);
    }

    public PurityDialogEditor(NotebookPageModel pageModel) {
    	this();
    	this.pageModel = pageModel;
	}

	/**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
        	if (e.getSource() instanceof JButton) {
        		JButton button = (JButton)e.getSource();
        		if (button.getParent() instanceof PCeNBatchInfoTableView) {
        			PCeNBatchInfoTableView table = (PCeNBatchInfoTableView) button.getParent();
        			ProductBatchModel productBatchModel = (ProductBatchModel) table.getSelectedBatch();
        			if (productBatchModel != null) {
        				// Populate fields in the ProductBatch which is derived from the ProductBatchModel
        				List<Properties> al_purities = new ArrayList<Properties>();
						try {
							al_purities = CodeTableCache.getCache().getPurityDeterminedBy();
						} catch (CodeTableCacheException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
        				// Show dialog
        				PurityInfoJDialog.showGUI(MasterController.getGUIComponent(), productBatchModel, al_purities, new JTextArea(), pageModel);
        			}
        		}
        	}
            //Make the renderer reappear.
            fireEditingStopped();

        } else { 
            // do nothing
        }
    }
    
	private boolean purityModelListChanged(List newPurityModelList) {
		
		return true;
	}

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return labelText;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
         return button;
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
