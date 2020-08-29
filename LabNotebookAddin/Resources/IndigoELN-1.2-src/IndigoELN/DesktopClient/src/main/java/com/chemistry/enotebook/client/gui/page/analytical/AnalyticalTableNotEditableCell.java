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
package com.chemistry.enotebook.client.gui.page.analytical;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

public class AnalyticalTableNotEditableCell implements TableCellEditor {
	private String label;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		label = (value == null) ? "" : value.toString();
		return null;
	}

	public void cancelCellEditing() {
	}

	public boolean stopCellEditing() {
		return true;
	}

	public Object getCellEditorValue() {
		return label;
	}

	public boolean isCellEditable(EventObject arg0) {
		return false;
	}

	public boolean shouldSelectCell(EventObject arg0) {
		return true;
	}

	public void addCellEditorListener(CellEditorListener arg0) {
	}

	public void removeCellEditorListener(CellEditorListener arg0) {
	}
}