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
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis.uc;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class UcStructAreaRenderer extends JPanel implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4716607127952577161L;
	private JRadioButton radioButton = new JRadioButton();
	private JPanel radioPanel = new JPanel();
	private JPanel isomerStructPanel = new JPanel();
	private JLabel isomerDescr = new JLabel();
	private JLabel noStruct = new JLabel("No Structure");
	private ChemistryPanel chimeStruct = new ChemistryPanel();

	public UcStructAreaRenderer() {
		setBackground(Color.WHITE);
		isomerDescr.setOpaque(false);
		noStruct.setOpaque(false);
		noStruct.setHorizontalAlignment(JLabel.CENTER);
		radioButton.setOpaque(false);
		radioPanel.setLayout(new BorderLayout());
		radioPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		radioPanel.add(radioButton, BorderLayout.CENTER);
		isomerStructPanel.setBackground(Color.WHITE);
		isomerStructPanel.setLayout(new BorderLayout());
		isomerStructPanel.add(isomerDescr, BorderLayout.NORTH);
		setLayout(new BorderLayout());
		add(radioPanel, BorderLayout.WEST);
		add(isomerStructPanel, BorderLayout.CENTER);
	}

	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if (value != null) {
			UcCompoundInfo ucInfo = (UcCompoundInfo) value;
			isomerDescr.setText("  " + ucInfo.getIsomerDescr());
			if (isSelected && ucInfo.isLegacy()) {
				jTable.clearSelection();
				isSelected = false;
			}
			radioButton.setVisible(!ucInfo.isLegacy());
			radioButton.setSelected(isSelected);
			if (ucInfo.isExact())
				radioPanel.setBackground(Color.YELLOW);
			else
				radioPanel.setBackground(Color.WHITE);
			if (ucInfo.getMolStruct() == null || ucInfo.getMolStruct().length() == 0) {
				isomerStructPanel.remove(chimeStruct);
				isomerStructPanel.add(noStruct, BorderLayout.CENTER);
			} else {
				isomerStructPanel.remove(noStruct);
				isomerStructPanel.add(chimeStruct, BorderLayout.CENTER);
				chimeStruct.setMolfileData(ucInfo.getMolStruct());
			}
		}
		return this;
	}
}
