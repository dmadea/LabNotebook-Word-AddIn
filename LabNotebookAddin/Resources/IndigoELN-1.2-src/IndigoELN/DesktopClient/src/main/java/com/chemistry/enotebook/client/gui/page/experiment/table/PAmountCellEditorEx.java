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

import javax.swing.*;
import java.awt.*;

public class PAmountCellEditorEx extends PAmountCellEditor {

	private static final long serialVersionUID = -5654847464550681851L;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		valueView.setEditable(false);
		valueView.setEnabled(false);

		txtUnitView.setEditable(false);
		txtUnitView.setEnabled(false);
		
		comboUnitView.setEditable(false);
		comboUnitView.setEnabled(editableFlg);
		
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
	
	/*
	public void setValueEditable(boolean val) {
		if (valueView != null) {
			valueView.setEditable(false);
			valueView.setEnabled(false);
		}
	}

	public void setEditable(boolean val) {
		super.setEditable(val);
	}
	
	public void setUnitEditable(boolean val) {
		super.setUnitEditable(val);
	}
	
	public void setEnabled(boolean enableFlag) {
		super.setEditable(enableFlag);
	}
	*/
	protected void setColors() {
		super.setColors();
	}
}
