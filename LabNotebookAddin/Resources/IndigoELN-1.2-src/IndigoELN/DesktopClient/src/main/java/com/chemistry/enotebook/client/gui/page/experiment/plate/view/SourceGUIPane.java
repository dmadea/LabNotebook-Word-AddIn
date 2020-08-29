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

public class SourceGUIPane extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3549338110431057414L;
	
	SourceGUISynthesisPlanMonomersSubPane synthesisPlanMonomers;
	private TabbedGUIPaneController controller;

	public SourceGUIPane() {
		setLayout(new GridLayout(0, 2, 10, 10));

		Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
		setBorder(padding);

		ButtonGroup group = new ButtonGroup();

		synthesisPlanMonomers = new SourceGUISynthesisPlanMonomersSubPane();
		synthesisPlanMonomers.init();
		synthesisPlanMonomers.setButtonGroup(group);

		add(synthesisPlanMonomers);

		JRadioButton monomerPlateButton = new JRadioButton("Monomer Plate");
		JRadioButton otherOrderButton = new JRadioButton("Other Order");
		JRadioButton monomerSpreadsheetButton = new JRadioButton("Monomer Spreadsheet");
		JRadioButton synthesisPlanProductsButton = new JRadioButton("Synthesis Plan Products");
		JRadioButton productPlateButton = new JRadioButton("Product Plate");
		JRadioButton productSpreadsheetButton = new JRadioButton("Product Spreadsheet");


		group.add(monomerPlateButton);
		group.add(otherOrderButton);
		group.add(monomerSpreadsheetButton);
		group.add(synthesisPlanProductsButton);
		group.add(productPlateButton);
		group.add(productSpreadsheetButton);

		// buttonClickAction(synthesisPlanMonomersButton);
		buttonClickAction(monomerPlateButton);
		buttonClickAction(otherOrderButton);
		buttonClickAction(monomerSpreadsheetButton);
		buttonClickAction(synthesisPlanProductsButton);
		buttonClickAction(productPlateButton);
		buttonClickAction(productSpreadsheetButton);

		add(monomerPlateButton);
		add(otherOrderButton);
		add(monomerSpreadsheetButton);
		add(synthesisPlanProductsButton);
		add(productPlateButton);
		add(productSpreadsheetButton);

	}

	private void buttonClickAction(JRadioButton button) {
		button.setActionCommand(ActionCommandInterface.CLICK);
		button.addActionListener(this);
	}

	public void setController(TabbedGUIPaneController ctlr) {
		controller = ctlr;
		synthesisPlanMonomers.setController(controller);
		
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ActionCommandInterface.CLICK)) {

			AppEvent ape = new AppEvent(AppEvent.SOURCE_EVENT);
			ape.setMessage(((JRadioButton) ae.getSource()).getText());
			controller.handleAppEvent(ape);
		}

	}
}
