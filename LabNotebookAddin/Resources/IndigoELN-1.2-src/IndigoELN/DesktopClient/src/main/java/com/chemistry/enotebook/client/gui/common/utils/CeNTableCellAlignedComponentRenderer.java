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

import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CeNTableCellAlignedComponentRenderer extends CeNTableCellAlignedComponent implements
		TableCellRenderer {

	private static final long serialVersionUID = -1986266032365915988L;

		public CeNTableCellAlignedComponentRenderer(JComponent component) {
			super(component);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (!table.isCellEditable(row, column))
			{
				component.setBackground((DefaultPreferences.getNonEditableRowBackgroundColor()));
				this.setBackground((DefaultPreferences.getNonEditableRowBackgroundColor()));
				setOpaque(true); 
			}
			else
			{
				component.setBackground((DefaultPreferences.getEditableRowBackgroundColor()));
				setOpaque(false);
			}
			

			if (component instanceof JComboBox)
			{
				((JComboBox)super.getCellInnerComponent()).removeAllItems();
				((JComboBox)super.getCellInnerComponent()).addItem(value);
				return this;
			}
			else if (component instanceof JTextArea)
			{
				JTextArea textArea = ((JTextArea)super.getCellInnerComponent());
				textArea.setText(value == null ? "" : value.toString());
				return textArea;
			}
			else if (component instanceof JLabel)
			{
				JLabel jLabel  = ((JLabel)super.getCellInnerComponent());
				jLabel.setText(value == null ? "" : value.toString());
				jLabel.setVerticalAlignment(SwingConstants.TOP);
				jLabel.setOpaque(true);
				return jLabel;
			}

			else
				return null;
		}
}
