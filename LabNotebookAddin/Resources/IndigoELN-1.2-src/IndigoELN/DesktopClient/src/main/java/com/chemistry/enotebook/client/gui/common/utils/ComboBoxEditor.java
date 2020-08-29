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
/*
 * Created on Jun 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * 
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
public class ComboBoxEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -7937639066861873391L;
	
	JComponent component = new JComboBox();

	public ComboBoxEditor() {
	}

	public ComboBoxEditor(String[] boxValues) {
		DefaultComboBoxModel model = new DefaultComboBoxModel(boxValues);
		((JComboBox) component).setModel(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		// 'value' is value contained in the cell located at (rowIndex,
		// vColIndex)
		// TODO Auto-generated method stub
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		// Fired when needs a value
		return ((JComboBox) component).getSelectedItem().toString();
	}

	public void setValues(String[] boxValues) {
		DefaultComboBoxModel model = new DefaultComboBoxModel(boxValues);
		((JComboBox) component).setModel(model);
	}
}
