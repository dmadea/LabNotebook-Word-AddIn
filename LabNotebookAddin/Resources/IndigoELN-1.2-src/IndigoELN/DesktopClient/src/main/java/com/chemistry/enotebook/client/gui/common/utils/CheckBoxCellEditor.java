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
 * Created on 27-May-2004
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
public class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -8254209385907555110L;
	
	// this is the componet that will handle the editing of the cell value
	JComponent component = new JCheckBox();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		// value is the value in the cell located at (rowIndex, vColIndex)
		if (isSelected) {
			// cell (and perhaps other cells) are selected
		}
		// configure the componet with the specified value
		if (value.toString().matches("true")) {
			((JCheckBox) component).setSelected(true);
		} else {
			((JCheckBox) component).setSelected(false);
		}
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	// returns new value to be storeed in cell
	public Object getCellEditorValue() {
		String retValue = "";
		if (((JCheckBox) component).isSelected()) {
			retValue = "true";
		} else {
			retValue = "false";
		}
		return retValue;
	}
}
