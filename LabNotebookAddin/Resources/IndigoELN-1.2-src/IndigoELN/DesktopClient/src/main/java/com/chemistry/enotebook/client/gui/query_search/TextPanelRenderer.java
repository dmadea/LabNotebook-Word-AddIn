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
package com.chemistry.enotebook.client.gui.query_search;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class TextPanelRenderer extends JTextPane implements TableCellRenderer {

	private static final long serialVersionUID = 2390058296158044591L;

	public TextPanelRenderer() {
		SimpleAttributeSet bSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(bSet, StyleConstants.ALIGN_CENTER);
		StyledDocument doc = getStyledDocument();
		doc.setParagraphAttributes(0, 104, bSet, false);
	}

	public Component getTableCellRendererComponent(JTable jTable, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		setText((String) obj);
		return this;
	}
}
