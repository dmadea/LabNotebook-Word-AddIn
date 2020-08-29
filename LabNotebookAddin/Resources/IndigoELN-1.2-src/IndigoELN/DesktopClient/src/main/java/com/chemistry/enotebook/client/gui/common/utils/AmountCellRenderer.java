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
package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * 
 * 
 *
 */
public class AmountCellRenderer extends AmountComponent implements TableCellRenderer {

	private static final long serialVersionUID = 4919513845193859170L;

	/*
	 * The default renderer for a Cell in a JTable is a JLabel. The trick here is to ...
	 */
	public AmountCellRenderer() {
		super();
	}

	// Implementation of TableCellRenderer interface
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if (!(value == null || table == null)) {
			if (debug) // System.out.println("AmountCellRenderer.getTableCellRendererComponent():
				// calling setValue()");
				setEditable(true);
			currentAmt.deepCopy(value);
			updateDisplay();
		}
		return this;
	}
}