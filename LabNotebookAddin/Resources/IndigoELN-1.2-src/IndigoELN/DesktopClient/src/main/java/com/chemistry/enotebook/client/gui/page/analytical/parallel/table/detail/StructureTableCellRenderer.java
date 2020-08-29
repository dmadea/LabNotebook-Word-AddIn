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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail;

import com.chemistry.enotebook.client.gui.page.experiment.table.ChimeRenderingPanel;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class StructureTableCellRenderer implements TableCellRenderer {

	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);
	private Font tableCellFont = DefaultPreferences.getStandardTableCellFont();

	private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private JLabel processingLabel = new JLabel("Processing ... ");
	private JLabel naLabel = new JLabel(""); // vb 11/21 Eliminate any N/A's
	protected boolean m_selected;
	
	public StructureTableCellRenderer() {
		naLabel.setFont(tableCellFont);
		naLabel.setHorizontalAlignment(SwingConstants.LEFT);
		naLabel.setVerticalAlignment(SwingConstants.TOP);
		naLabel.setOpaque(false);
		naLabel.setOpaque(true);
		naLabel.setBorder(noFocusBorder);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		String coluName = table.getColumnName(column);
		if (value instanceof MolString) {
			ChimeRenderingPanel chimeRenderingPanel = new ChimeRenderingPanel();
			MolString molString = (MolString) value;
			String molData = molString.getMolString();
			chimeRenderingPanel.setRendererData(molData);
			if (isSelected) {
				chimeRenderingPanel.setForeground(selectedForeground);
				chimeRenderingPanel.setBackground(selectedBackground);
			} else {
				chimeRenderingPanel.setForeground(foreground);
				chimeRenderingPanel.setBackground(background);
			}
			
			//this numbering is not required unless sorting is needed on this column.
			//This is temporally disabled. To remove permanently remove, remove Index from MolString class.
/*			int a = molString.getIndex() + 1;
			if (a != 0)
				chimeRenderingPanel.setLabel(a + "");
			// deal with selection colors
			else
				chimeRenderingPanel.setImage(null);
*/			chimeRenderingPanel.setBorder(noFocusBorder);
			// chimeRenderingPanel.setPropertiesToolTip((Hashtable)(collection.getProperties().elementAt(realIndex)));
			return chimeRenderingPanel;
		} else {
			return new JLabel();
		}
	}

}
