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
 * Created on 27-Feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.chlorac;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.Serializable;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer, Serializable {

	private static final long serialVersionUID = 7216459680099210472L;
	
	protected static Border noFocusBorder;
	private Color unselectedForeground;
	private Color unselectedBackground;

	public MultiLineCellRenderer() {
		super();
		noFocusBorder = new EmptyBorder(1, 2, 1, 2);
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
		setBorder(noFocusBorder);
	}

	public void setForeground(Color c) {
		super.setForeground(c);
		unselectedForeground = c;
	}

	public void setBackground(Color c) {
		super.setBackground(c);
		unselectedBackground = c;
	}

	public void updateUI() {
		super.updateUI();
		setForeground(null);
		setBackground(null);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
		}
		setFont(table.getFont());
		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				super.setForeground(UIManager.getColor("Table.focusCellForeground"));
				super.setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(noFocusBorder);
		}
		setValue(value);
		return this;
	}

	protected void setValue(Object value) {
		setText((value == null) ? "" : value.toString());
	}

	public static class UIResource extends MultiLineCellRenderer implements javax.swing.plaf.UIResource {
		private static final long serialVersionUID = 6079680242130903413L;
	}
}
