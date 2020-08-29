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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

public class PanelFormatter {

	public static JPanel formatPlatePanel(JPanel innerPanel) {
		JPanel panel = new JPanel();
		FormLayout layout = new FormLayout("100dlu, pref:grow, 100dlu", "30dlu, pref:grow, 30dlu");
		panel.setLayout(layout);
		CellConstraints cc = new CellConstraints();
		panel.add(innerPanel, cc.xy(2, 2));
		return panel;
	}
}
