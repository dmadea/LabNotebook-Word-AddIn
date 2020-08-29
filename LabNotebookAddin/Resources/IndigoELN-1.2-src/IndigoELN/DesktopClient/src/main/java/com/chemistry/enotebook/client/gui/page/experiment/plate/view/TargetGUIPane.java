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
package com.chemistry.enotebook.client.gui.page.experiment.plate.view;

import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.AppEvent;
import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.TabbedGUIPaneController;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TargetGUIPane extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 281350899844979468L;
	
	private TabbedGUIPaneController controller;

	public TargetGUIPane() {
		setLayout(new GridLayout(4, 0));

		Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
		setBorder(padding);

		JRadioButton tableButton = new JRadioButton("Monomer / Product Tables");
		JRadioButton monomerPlateButton = new JRadioButton("Monomer Plates");
		JRadioButton productPlateButton = new JRadioButton("Product Plates");
		JRadioButton spreadsheetButton = new JRadioButton("Spreadsheet");

		ButtonGroup group = new ButtonGroup();

		group.add(tableButton);
		group.add(monomerPlateButton);
		group.add(productPlateButton);
		group.add(spreadsheetButton);

		buttonClickAction(tableButton);
		buttonClickAction(monomerPlateButton);
		buttonClickAction(productPlateButton);
		buttonClickAction(spreadsheetButton);

		add(tableButton);
		add(monomerPlateButton);
		add(productPlateButton);
		add(spreadsheetButton);
	}

	private void buttonClickAction(JRadioButton button) {
		button.setActionCommand(ActionCommandInterface.CLICK);
		button.addActionListener(this);
	}

	public void setController(TabbedGUIPaneController ctlr) {
		controller = ctlr;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ActionCommandInterface.CLICK)) {

			AppEvent ape = new AppEvent(AppEvent.TARGET_EVENT);
			ape.setMessage(((JRadioButton) ae.getSource()).getText());
			controller.handleAppEvent(ape);
		}

	}
}
