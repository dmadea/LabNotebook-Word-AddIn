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

import com.chemistry.enotebook.client.gui.common.utils.AmountCellEditor;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import java.awt.*;

public class TubeVialContainerTableViewCellEditor extends DefaultCellEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7528167307964398923L;
	private JTextField ta;
	private JCheckBox check = new JCheckBox();
	private JComboBox combo = new JComboBox();


	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	JPanel chkBoxPanel = new JPanel();
	JPanel comboPanel = new JPanel();
	StringCellEditor strEditor;
	AmountCellEditor amtEditor;
	
	private Component editingComponent;

	public TubeVialContainerTableViewCellEditor() {
		super(new JTextField());
		ta = (JTextField) super.getComponent();
		strEditor = new StringCellEditor();
		amtEditor = new AmountCellEditor();
		//BorderLayout border =new BorderLayout();
		//saltCodeComboPanel.setLayout(border);
	
		
		//super.setClickCountToStart(2);
	}

	// Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
		if (value == null)
			value = "";
				
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
			editingComponent = cellComp;
			return cellComp;
		}  else if (value instanceof Boolean) {
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
			
		}else
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
		private static final long serialVersionUID = -8877597203650870821L;

		private StringCellEditor() {
			super(ta);
			ta.removeActionListener(delegate);
			delegate = new EditorDelegate() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1813018680619063152L;

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

}
