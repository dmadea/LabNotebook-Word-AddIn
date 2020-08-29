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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * 
 */
public class PcenPlateSummaryTableViewCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7023396750790710941L;

	private JTextField ta;
	
	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	
	private JPanel comboPanel = new JPanel();
	private StringCellEditor strEditor;
	//private AmountCellEditor amtEditor;
	private String[] rackPlateFlagValues = new String[] { "Intermediate", "Product", "By-Product" };
	private String[] selectFlagValues = new String[] { "Select", "Not" };
	private JComboBox combo = new JComboBox(rackPlateFlagValues);
	//private JComboBox combosel = new JComboBox(selectFlagValues);
	//DefaultCellEditor commboDefaultCellEditor = new DefaultCellEditor(combo);
	//DefaultCellEditor checkDefaultCellEditor = new DefaultCellEditor(check);
	private Component editingComponent;

	//private PlateDetailsViewer plateDetailsViewer;
	public PcenPlateSummaryTableViewCellEditor() {
		super(new JTextField());
		ta = (JTextField) super.getComponent();
		//this.plateDetailsViewer = plateDetailsViewer;
		strEditor = new StringCellEditor();
		//amtEditor = new AmountCellEditor();
		setup();
		//super.setClickCountToStart(2);
	}

	private void setup() {
		/*
		chkBoxPanel.setForeground(foreground);
		chkBoxPanel.setBackground(background);
		chkBoxPanel.setBorder(null);
		chkBoxPanel.add(check);
		*/

		comboPanel.add(combo);
		comboPanel.setForeground(foreground);
		comboPanel.setBackground(background);
		//check.addItemListener(this);
	}

	// Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
		//("getTableCellEditorComponent       " + value + value.getClass().getName());
		if (value == null)
			value = "";
		String coluName = table.getColumnName(column);
		// if (collection.getAnnotations().contains(coluName)) {
		if (coluName.equalsIgnoreCase("Product Plate Flag") && value instanceof String) {
			String currentValue = (String) value;
			//commboDefaultCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			//("currentValue "+currentValue);

			//if(value!="")
			//	int index = combo.

			if (isSelected) {
				combo.setForeground(selectedForeground);
				combo.setBackground(selectedBackground);
			} else {
				combo.setForeground(foreground);
				combo.setBackground(background);
			}

			editingComponent = combo;
			return combo;

		} else if (value instanceof String) {

			JTextField cellComp = (JTextField) strEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			cellComp.setText((String) value);
			if (isSelected && !cellComp.hasFocus()) {
				cellComp.setForeground(selectedForeground);
				cellComp.setBackground(selectedBackground);
			} else if (cellComp.hasFocus()) {
				cellComp.setForeground(Color.BLACK);
				cellComp.setBackground(Color.WHITE);
			}
			editingComponent = cellComp;
			return cellComp;
			//} else if (value instanceof Amount) {
			//	return new JPanel().add(amtEditor.getTableCellEditorComponent(table, value, isSelected, row, column));
			//} else if (value instanceof Boolean) {
		} else if (coluName.equalsIgnoreCase("Select to View")) {
			
			
			/*combosel.setSelectedIndex(1);
			Boolean checked = (Boolean) value;
			if(checked.booleanValue()){
				combosel.setSelectedIndex(0);
			}
			if (isSelected) {
				combosel.setForeground(selectedForeground);
				combosel.setBackground(selectedBackground);
			} else {
				combosel.setForeground(foreground);
				combosel.setBackground(background);
			}

			editingComponent = combosel;
			return combosel;
			*/
			
			/*
			if (isSelected) {
				chkBoxPanel.setForeground(selectedForeground);
				chkBoxPanel.setBackground(selectedBackground);
			} else {
				chkBoxPanel.setForeground(foreground);
				chkBoxPanel.setBackground(background);
			}
			chkBoxPanel.removeAll();
			chkBoxPanel.add(chkBox0);
			editingComponent = chkBox0;
			return chkBoxPanel;
			*/
			
			//return checkDefaultCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			
			//
			//check.setSelected(true);
		
			Boolean checked = (Boolean) value;
			JCheckBox check = new JCheckBox();
			JPanel chkBoxPanel = new JPanel();
			chkBoxPanel.add(check);
			if (isSelected) {
				chkBoxPanel.setForeground(selectedForeground);
				chkBoxPanel.setBackground(selectedBackground);
			} else {
				chkBoxPanel.setForeground(foreground);
				chkBoxPanel.setBackground(background);
			}
			check.setSelected(checked.booleanValue());

			editingComponent = check;
			return chkBoxPanel;
			
			
			
		} else {

			JTextField tf = new JTextField("Unknown");
			editingComponent = tf;
			return tf;
		}

	}

	public Object getCellEditorValue() {

		if (editingComponent instanceof JTextField) {
			return ((JTextField) editingComponent).getText();
			
		} else if (editingComponent instanceof JCheckBox) {
			return new Boolean(((JCheckBox) editingComponent).isSelected());

		} else if (editingComponent instanceof JComboBox) {
			return ((JComboBox) editingComponent).getSelectedItem();
		}
		return "uuu";

	}

	private class StringCellEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8199322260813971202L;

		private StringCellEditor() {
			super(ta);
			ta.removeActionListener(delegate);
			delegate = new EditorDelegate() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -8711635659217442146L;

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
