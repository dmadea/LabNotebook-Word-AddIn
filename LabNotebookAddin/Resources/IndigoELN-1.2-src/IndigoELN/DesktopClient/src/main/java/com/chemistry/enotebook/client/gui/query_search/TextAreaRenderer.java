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
import java.awt.*;

public class TextAreaRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = 6612937341905248791L;

	public TextAreaRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(JTable jTable, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		setText((String) obj);
		return this;
	}
}
