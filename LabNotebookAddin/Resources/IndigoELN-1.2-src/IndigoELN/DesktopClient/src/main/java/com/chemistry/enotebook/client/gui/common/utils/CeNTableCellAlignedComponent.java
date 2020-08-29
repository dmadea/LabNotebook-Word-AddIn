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

import info.clearthought.layout.TableLayout;

import javax.swing.*;

/**
 * 
 * 
 *
 */
public class CeNTableCellAlignedComponent extends JPanel {

	private static final long serialVersionUID = -5683548545191586288L;
	
	protected JComponent component = null; // double value is
	protected javax.swing.border.EmptyBorder border = new javax.swing.border.EmptyBorder(0, 0, 0, 0);
	protected int clickCountToStart = 2;
	protected boolean comboClickOverride = false;

	protected double dataSizeBrief[][] = { {0.98}, {17, TableLayout.FILL} };

	public CeNTableCellAlignedComponent(JComponent component) {
		super();
		this.component = component;
		init();
	}

	private void init() {
		//comboBox = new JComboBox();
		this.setLayout(new TableLayout(dataSizeBrief));
		setBorder(border);
		if (!(component instanceof JTextField))
			component.setBorder(border);
		this.add(component, "0, 0");
		//setOpaque(false); 
		revalidate();
	}
	
	public JComponent getCellInnerComponent()
	{
		return component;
	}
	
	
	public JComponent getCellComponent()
	{
		return this;
	}	
}
