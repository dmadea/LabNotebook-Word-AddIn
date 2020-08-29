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
package com.chemistry.enotebook.client.gui.page.experiment.table;

//import java.awt.BorderLayout;

import com.chemistry.enotebook.client.gui.common.utils.AmountCellEditor;
import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import java.awt.*;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.util.EventObject;
//import java.util.Vector;
//import javax.swing.CellEditor;
//import javax.swing.event.ChangeEvent;
//import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
//import com.chemistry.enotebook.experiment.datamodel.common.Amount;
//import com.chemistry.enotebook.utils.CodeTableUtils;

/**
 * 
 * 
 */
public class ParallelCeNTableViewCellEditor extends DefaultCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8276925890690615598L;
	private JTextField ta;
	//private JCheckBox check = new JCheckBox();
	private JComboBox combo = new JComboBox();


	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	JPanel chkBoxPanel = new JPanel();
	JPanel comboPanel = new JPanel();
	StringCellEditor strEditor;
	AmountCellEditor amtEditor;

	public ParallelCeNTableViewCellEditor() {
		super(new JTextField());
		ta = (JTextField) super.getComponent();
		strEditor = new StringCellEditor();
		amtEditor = new AmountCellEditor();
		//BorderLayout border =new BorderLayout();
		//saltCodeComboPanel.setLayout(border);
	
		
		super.setClickCountToStart(2);
	}

	// Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
		if (value == null)
			value = "";
		//String coluName = table.getColumnName(column);
		// if (collection.getAnnotations().contains(coluName)) {
	
		
		 if (value instanceof String) {
			
			
			JTextField cellComp = (JTextField) strEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			cellComp.setText((String) value);
			if (isSelected && !cellComp.hasFocus()) {
				cellComp.setForeground(selectedForeground);
				cellComp.setBackground(selectedBackground);
			} else if (cellComp.hasFocus()) {
				cellComp.setForeground(Color.BLACK);
				cellComp.setBackground(Color.WHITE);
			}
			return cellComp;
		} else if (value instanceof AmountModel) {
			//return new JPanel().add(amtEditor.getTableCellEditorComponent(table, value, isSelected, row, column));
			
			AmountModel mAmountModel =  (AmountModel)value;
			double va = mAmountModel.GetValueInStdUnitsAsDouble();
			String v = ""+va;
			JTextField cellComp = (JTextField) strEditor.getTableCellEditorComponent(table, v, isSelected, row, column);
			cellComp.setText(v);
			
			if (isSelected && !cellComp.hasFocus()) {
				cellComp.setForeground(selectedForeground);
				cellComp.setBackground(selectedBackground);
			} else if (cellComp.hasFocus()) {
				cellComp.setForeground(Color.BLACK);
				cellComp.setBackground(Color.WHITE);
			}
			return cellComp;
		
		} else if (value instanceof Boolean) {
			Boolean checked = (Boolean) value;
			chkBoxPanel.setForeground(foreground);
			chkBoxPanel.setBackground(background);
			JCheckBox check = new JCheckBox();
			check.setSelected(checked.booleanValue());
			//DefaultCellEditor mDefaultCellEditor = new DefaultCellEditor(check);
			chkBoxPanel.add(check);
			return chkBoxPanel;
		} else if (value instanceof RackFlagType) {
			RackFlagType mRackFlagType = (RackFlagType) value;
			((DefaultComboBoxModel) combo.getModel()).addElement("TEST ONE");
			((DefaultComboBoxModel) combo.getModel()).addElement("TEST TWO");
			// mRackFlagType.getRackPlateFlagValues()
			combo.setSelectedIndex(mRackFlagType.getSelected());
			comboPanel.add(combo);
			comboPanel.setForeground(foreground);
			comboPanel.setBackground(background);
			return comboPanel;
		} else
			return null;
		// }
	}

	// public Object getCellEditorValue() {
	// return ta.getText();
	// }
	private class StringCellEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4870946860272396255L;

		private StringCellEditor() {
			super(ta);
			ta.removeActionListener(delegate);
			delegate = new EditorDelegate() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 6190350642544419039L;

				public void setValue(Object value) {
					ta.setText((String) value);
				}

				public Object getCellEditorValue() {
					return ta.getText();
				}
			};
			ta.addActionListener(delegate);
			ta.setBorder(null);
			// ta.addKeyListener(l);
		}
	}
	


}
