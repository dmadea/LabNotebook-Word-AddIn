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

import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CeNStatusComboBoxRenderer extends CeNTableCellAlignedComponent implements TableCellRenderer {
	
	private static final long serialVersionUID = 1647932877579074677L;
	
	private JComboBox comboBox = null;
		
	public CeNStatusComboBoxRenderer(JComponent component) {
		super(component);
		comboBox = (JComboBox)component;
	}

	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
	{
		//comboBox.setBorder(null);
		comboBox.removeAllItems();
		comboBox.addItem( value );
		if (!table.isCellEditable(row, column))
		{
			this.setBackground((DefaultPreferences.getNonEditableRowBackgroundColor()));
			setOpaque(true);
		}
		else
			setOpaque(false);
		
		if (value instanceof String) {
			String valstr = (String) value;
			// Monomer batch statuses
			if (valstr.equals(CeNConstants.SOLUBLE_CONTINUE))
				comboBox.setBackground(CeNGUIConstants.LIGHT_GREEN);
			else if (valstr.equals(CeNConstants.INSOLUBLE_CONTINUE))
				//comboBox.setBackground(new Color(180, 180, 250));
				comboBox.setBackground(CeNGUIConstants.LIGHT_BLUE);
			else if (valstr.equals(CeNConstants.INSOLUBLE_DISCONTINUE))
				//comboBox.setBackground(new Color(250, 70, 70));
				comboBox.setBackground(CeNGUIConstants.ORANGE);
			else if (valstr.equals(CeNConstants.UNAVAILABLE_DISCONTINUE))
				//comboBox.setBackground(new Color(250, 250, 70));
				comboBox.setBackground(CeNGUIConstants.LIGHT_RED);
			// Product batch statuses
			else if (valstr.equals(CeNConstants.PASS_CONTINUE))
				comboBox.setBackground(CeNGUIConstants.LIGHT_GREEN);
			else if (valstr.equals(CeNConstants.PASS_REGISTERED_CONTINUE))
				comboBox.setBackground(new Color(0, 250, 0));
			else if (valstr.equals(CeNConstants.SUSPECT_CONTINUE))
				comboBox.setBackground(CeNGUIConstants.LIGHT_GREEN);
			else if (valstr.equals(CeNConstants.SUSPECT_DISCONTINUE))
				//comboBox.setBackground(new Color(250, 250, 100));	
				comboBox.setBackground(CeNGUIConstants.YELLOW);
			else if (valstr.equals(CeNConstants.FAIL_CONTINUE))
				//comboBox.setBackground(new Color(250, 150, 50));
				comboBox.setBackground(CeNGUIConstants.ORANGE);
			else if (valstr.equals(CeNConstants.FAIL_DISCONTINUE))
				//comboBox.setBackground(new Color(250, 70, 70));	
				comboBox.setBackground(CeNGUIConstants.LIGHT_RED);
			else if (valstr.equals(CeNConstants.NOT_MADE_DISCONTINUE))
				//comboBox.setBackground(new Color(200, 200, 200));
				comboBox.setBackground(CeNGUIConstants.WHITE );
			else
				comboBox.setBackground(Color.white);
		} else {
			comboBox.setBackground(Color.white);
		}
		return super.getCellComponent();
	}

	public JComboBox getAssociatedComboBox()
	{
		return comboBox;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(100, 20);
	}


}
