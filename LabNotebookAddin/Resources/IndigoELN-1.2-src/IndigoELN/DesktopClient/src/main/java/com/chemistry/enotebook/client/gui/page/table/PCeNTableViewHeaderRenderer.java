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
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PCeNTableViewHeaderRenderer implements TableCellRenderer {
	private Color foreground = DefaultPreferences.getTableHeaderForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getTableHeaderBackgroundColor(); //new Color(255, 255, 245);

	protected boolean m_selected;
	private JLabel m_label;
	
	public PCeNTableViewHeaderRenderer() {
		super();
		m_label = new JLabel();
		m_label.setOpaque(true);
		m_label.setFont(DefaultPreferences.getStandardTableHeaderFont());
		m_label.setHorizontalAlignment(SwingConstants.CENTER);
		m_label.setHorizontalTextPosition(SwingConstants.LEFT);
		m_label.setForeground(foreground);
		m_label.setBackground(background);
		LookAndFeel.installBorder(m_label, "TableHeader.cellBorder");
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {

		String s = (String) value;
		m_label.setText(s);
		m_label.setToolTipText((String) value);
		return m_label; 
	}
}
