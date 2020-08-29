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
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.regis_submis.JDialogHealthHazInfo;
import com.chemistry.enotebook.client.gui.page.table.PCeNBatchInfoTableView;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class HazardsDialogEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener {
	  
    /**
	 * 
	 */
	private static final long serialVersionUID = 2149508125862021007L;
	JButton button;
    protected static final String EDIT = "Edit";
    JLabel labelText = new JLabel("Hello");
    BatchAttributeComponentUtility batchUtility = null;
	private NotebookPageModel pageModel;

    public HazardsDialogEditor(NotebookPageModel pageModel) {
    	batchUtility = BatchAttributeComponentUtility.getInstance();
    	this.pageModel = pageModel;
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
        			ProductBatchModel associatedBatch = null;
        			if (productBatchModel != null) {
    					JDialogHealthHazInfo jDialogHealthHazInfo = new JDialogHealthHazInfo(MasterController.getGUIComponent(), pageModel);
    					Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
    					Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
    					jDialogHealthHazInfo.setLocation(loc.x + (dim.width - jDialogHealthHazInfo.getSize().width) / 2, loc.y
    							+ (dim.height - jDialogHealthHazInfo.getSize().height) / 2);
    					jDialogHealthHazInfo.setHandlingList(batchUtility.getHandlingList());
    					jDialogHealthHazInfo.setHazardList(batchUtility.getHazardsList());
    					jDialogHealthHazInfo.setStorageList(batchUtility.getStorageList());
    					jDialogHealthHazInfo.setHandlingMap(batchUtility.getHandlingMap());
    					jDialogHealthHazInfo.setHazardMap(batchUtility.getHazardsMap());
    					jDialogHealthHazInfo.setStorageMap(batchUtility.getStorageMap());
    					//jDialogHealthHazInfo.setPageModel(pageModel);
    					try {
    						// Convert from BatchModel to AbstractBatch
    						associatedBatch = productBatchModel;
/*    						associatedBatch.setRegInfo(productBatchModel.getRegInfo());
    						associatedBatch.setHazardComments(productBatchModel.getHazardComments());
    						associatedBatch.setHandlingComments(productBatchModel.getHandlingComments());
    						associatedBatch.setStorageComments(productBatchModel.getStorageComments());
*/    					} catch (NumberFormatException e1) {
    						CeNErrorHandler.getInstance().logExceptionMsg(e1);
    					} catch (Exception e1) {
    						CeNErrorHandler.getInstance().logExceptionMsg(e1);
    					}
    					jDialogHealthHazInfo.setSelectedBatch(associatedBatch);
    					jDialogHealthHazInfo.getHealthHazInfo();
/*    					if (jDialogHealthHazInfo.getHealthHazInfo()) {
    						//updateDisplay(); // update
    						try {
    							// Convert from AbstractBatch to BatchModel
    							productBatchModel.setRegInfo(associatedBatch.getRegInfo());
    							productBatchModel.setHazardComments(associatedBatch.getHazardComments());
    							productBatchModel.setHandlingComments(associatedBatch.getHandlingComments());
    							productBatchModel.setStorageComments(associatedBatch.getStorageComments());
    						} catch (NumberFormatException e1) {
    							CeNErrorHandler.getInstance().logExceptionMsg(e1);
    						} catch (Exception e1) {
    							CeNErrorHandler.getInstance().logExceptionMsg(e1);
    						}
    					}
*/    					jDialogHealthHazInfo = null;
        			}
        		}
        	}
            //Make the renderer reappear.
            fireEditingStopped();

        } else { 
            // do nothing
        }
    }
    
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return labelText; // should not appear
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
