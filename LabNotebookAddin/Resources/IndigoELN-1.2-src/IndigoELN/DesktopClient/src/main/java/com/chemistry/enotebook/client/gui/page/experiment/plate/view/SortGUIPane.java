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

public class SortGUIPane extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -8286596820784703532L;
	
	private TabbedGUIPaneController controller;
	private JRadioButton noneButton;
	private JRadioButton synthesisPlanSequenceButton;
	private JRadioButton compoundIdButton;
	private JRadioButton molecularWtButton;
	private JRadioButton sourcePlateSeqButton;
	private JRadioButton relativePlatePositionButton;

	public SortGUIPane() {
		setLayout(new GridLayout(6, 0));
		
		Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
		setBorder(padding);
		
		noneButton = new JRadioButton("None (Retain current order)");
		synthesisPlanSequenceButton = new JRadioButton("Synthesis Plan Sequence");
		compoundIdButton = new JRadioButton("Compound ID");
		molecularWtButton = new JRadioButton("Molecular Weight");
		sourcePlateSeqButton = new JRadioButton("Sequence on Source Plate");
		relativePlatePositionButton = new JRadioButton("Same Relative Position as Source Plate");

		ButtonGroup group = new ButtonGroup();
		group.add(noneButton);
		group.add(synthesisPlanSequenceButton);
		group.add(compoundIdButton);
		group.add(molecularWtButton);
		group.add(sourcePlateSeqButton);
		group.add(relativePlatePositionButton);

		buttonClickAction(noneButton);
		buttonClickAction(synthesisPlanSequenceButton);
		buttonClickAction(compoundIdButton);
		buttonClickAction(molecularWtButton);
		buttonClickAction(sourcePlateSeqButton);
		buttonClickAction(relativePlatePositionButton);

		add(noneButton);
		add(synthesisPlanSequenceButton);
		add(compoundIdButton);
		add(molecularWtButton);
		add(sourcePlateSeqButton);
		add(relativePlatePositionButton);
		

	}
	
	public void reset(){
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

			AppEvent ape = new AppEvent(AppEvent.SORT_EVENT);
			ape.setMessage(((JRadioButton) ae.getSource()).getText());
			controller.handleAppEvent(ape);
		}

	}

}
