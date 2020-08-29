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

public class ExcludeGUIPane extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 5668287579192185298L;
	
	private TabbedGUIPaneController controller;

	public ExcludeGUIPane() {
		
	}

	public void init() {
		setLayout(new GridLayout(0, 1));
		
		Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
		setBorder(padding);
		
		JRadioButton noneButton = new JRadioButton("No Compounds");
		JRadioButton discontinuedButton = new JRadioButton("Discontinued Compounds");
		JRadioButton includeRangeButton = new JRadioButton("(Protocol development) Include only these products (inclusive");

		buttonClickAction(noneButton);
		buttonClickAction(discontinuedButton);
		buttonClickAction(includeRangeButton);

		ButtonGroup group = new ButtonGroup();
		group.add(noneButton);
		group.add(discontinuedButton);
		group.add(includeRangeButton);

		add(noneButton);
		add(discontinuedButton);
		add(includeRangeButton);
		add(new JLabel());

		noneButton.setSelected(true);
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

			AppEvent ape = new AppEvent(AppEvent.EXCLUDE_EVENT);
			ape.setMessage(((JRadioButton) ae.getSource()).getText());
			controller.handleAppEvent(ape);
		}

	}

}
