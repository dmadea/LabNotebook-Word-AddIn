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
package com.chemistry.enotebook.utils.jtable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * 
 * 
 */
public class SortHeaderRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 8498527594799724273L;
	
	public static Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);
	public static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);
	public static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);

	public SortHeaderRenderer() {
		setHorizontalTextPosition(LEFT);
		setHorizontalAlignment(CENTER);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int col) {
		int index = -1;
		boolean ascending = true;
		if (table instanceof JSortTable) {
			SortableTableModel model = (SortableTableModel) table.getModel();
			index = model.getSortedColumnIndex();
			ascending = model.isSortedColumnAscending();
		}
		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				setForeground(header.getForeground());
				setBackground(header.getBackground());
				setFont(header.getFont());
			}
		}
		Icon icon = ascending ? ASCENDING : DECENDING;
		setIcon(col == index ? icon : NONSORTED);
		setText((value == null) ? "" : value.toString());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		return this;
	}
}