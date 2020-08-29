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
 * Created on Jan 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.analytical;

import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonCellRenderer extends JButton implements TableCellRenderer {
	
	private static final long serialVersionUID = -7029389630303536659L;

	public ButtonCellRenderer() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(Color.WHITE);
			setForeground(Color.green);
		}
		CeNGUIUtils.styleComponentText(Font.BOLD, this);
		setText((value == null) ? "" : value.toString());
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getToolTipText()
	 */
	public String getToolTipText() {
		if (this.getText().equals("Q"))
			return "Search for Analytical matching this Nbk Batch #";
		else if (this.getText().equals("P"))
			return "Search for PurificationService QC Data for this Nbk Batch #";
		else
			return null;
	}
}