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
import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.CreatePlateDialogController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SummaryGUIPane extends javax.swing.JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 152541962750101605L;

	private CreatePlateDialogController controller;

	private SubPane sourcePane;
	private SubPane sortPane;
	private SubPane excludePane;
	private SubPane targetPane;
	private Button goButton;
	private Button cancelButton;

	public SummaryGUIPane(CreatePlateDialogController controller) {
		{
			this.controller = controller;

		}
	}

	public void init() {

		setLayout(new GridLayout(1, 2));

		sourcePane = new SubPane("Source:  ");
		sortPane = new SubPane("Sort:  ");
		excludePane = new SubPane("Exclude:  ");
		targetPane = new SubPane("Target:  ");

		goButton = new Button("Go");
		cancelButton = new Button("Cancel");

		cancelButton.setMaximumSize(new Dimension(80, 40));
		goButton.setMaximumSize(new Dimension(80, 40));

		Box leftBox = Box.createVerticalBox();
		Box rightBox = Box.createVerticalBox();

		add(leftBox);
		add(rightBox);
		setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		leftBox.add(sourcePane);
		leftBox.add(sortPane);
		leftBox.add(excludePane);
		leftBox.add(targetPane);

//		Dimension fixed_height = new Dimension(0, 10);
//		Dimension infinite_height = new Dimension(0, Short.MAX_VALUE);
//		Box.Filler filler = new Box.Filler(fixed_height, fixed_height, infinite_height);

		Box buttonBox = Box.createHorizontalBox();

		rightBox.add(Box.createGlue());
		rightBox.add(buttonBox);

		buttonBox.add(goButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancelButton);

		cancelButton.setActionCommand(ActionCommandInterface.CANCEL);
		cancelButton.addActionListener(this);

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ActionCommandInterface.CANCEL)) {

			AppEvent ape = new AppEvent(AppEvent.NAV_EVENT);
			ape.setMessage(ae.getActionCommand());
			controller.handleAppEvent(ape);
		}

	}

	public void setSourceText(String text) {
		sourcePane.display.setText(text);
	}

	public void setSortText(String text) {
		sortPane.display.setText(text);
	}

	public void setExcludeText(String text) {
		excludePane.display.setText(text);
	}

	public void setTargetText(String text) {
		targetPane.display.setText(text);
	}

}

class SubPane extends JPanel {
	
	private static final long serialVersionUID = -9184590964565435061L;
	
	JLabel display = new JLabel("");

	public SubPane(String label) {

		setLayout(new FlowLayout(FlowLayout.LEADING));

		add(new JLabel(label));
		add(display);
	}
}
