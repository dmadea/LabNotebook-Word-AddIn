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
 * Created on Jan 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.analytical;

import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchNumberCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = -6434418608801994627L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value instanceof BatchNumber) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(Color.RED);
			} else {
				cell.setBackground(Color.WHITE);
				cell.setForeground(Color.RED);
			}
		} else {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				cell.setBackground(table.getBackground());
				cell.setForeground(table.getForeground());
			}
		}
		cell.setFont(cell.getFont().deriveFont(Font.BOLD));
		setHorizontalAlignment(SwingConstants.CENTER);
		return cell;
	}
}
