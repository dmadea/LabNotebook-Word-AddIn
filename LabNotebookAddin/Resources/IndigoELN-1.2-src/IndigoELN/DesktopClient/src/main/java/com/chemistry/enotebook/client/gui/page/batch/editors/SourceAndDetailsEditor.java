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

import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SourceAndDetailsEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8271447020653227470L;
	protected static final String EDIT = "edit";
	JLabel labelText = new JLabel("Hello");
	BatchAttributeComponentUtility batchUtility = null;
	JTextArea textArea = new JTextArea();
	private CeNComboBox comboBox;
	
	public SourceAndDetailsEditor(CeNComboBox comboBox) {
	   	this.comboBox = comboBox;
		batchUtility = BatchAttributeComponentUtility.getInstance();
        //Set up the editor (from the table's point of view),
        //which is a combo box.
        comboBox = new CeNComboBox();
        comboBox.setActionCommand(EDIT);
        comboBox.addActionListener(this);
   	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		comboBox.addItem(new JLabel("one"));
		comboBox.addItem(new JLabel("two"));
		comboBox.addItem(new JLabel("three"));
		return comboBox;
	}

	public Object getCellEditorValue() {
		return comboBox.getSelectedItem();
	}

	public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
        	if (e.getSource() instanceof JButton) {
        		JButton button = (JButton)e.getSource();
         	}
            //Make the renderer reappear.
            fireEditingStopped();

        } else { 
            // do nothing
        }
	}
	  

}
