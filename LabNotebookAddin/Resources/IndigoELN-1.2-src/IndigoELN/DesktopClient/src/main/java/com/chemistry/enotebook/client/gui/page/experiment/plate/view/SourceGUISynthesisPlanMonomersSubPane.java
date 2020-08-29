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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.experiment.plate.view;

import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.AppEvent;
import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.TabbedGUIPaneController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * 
 */
public class SourceGUISynthesisPlanMonomersSubPane extends JPanel implements ActionListener {

	private static final long serialVersionUID = 164222956027619567L;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	JRadioButton synthesisPlanMonomersButton;
	JList plateList;
	private TabbedGUIPaneController controller;

	public void init() {
		setLayout(new FlowLayout(FlowLayout.LEADING));
		synthesisPlanMonomersButton = new JRadioButton("Synthesis Plan Monomers");
		synthesisPlanMonomersButton.setActionCommand(ActionCommandInterface.CLICK);
		synthesisPlanMonomersButton.addActionListener(this);

		add(synthesisPlanMonomersButton);

		DefaultListModel listModel = new DefaultListModel();

		plateList = new JList(listModel);
		
//		controller.getMonomerList();
		
		listModel.addElement("A");
		listModel.addElement("B");
		listModel.addElement("C");
		
		plateList.setEnabled(false);
		plateList.setBackground(this.getBackground());

		plateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		plateList.setVisibleRowCount(-1);

		JScrollPane listScroller = new JScrollPane(plateList);
		listScroller.setPreferredSize(new Dimension(50, 50));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);
		add(listScroller);
		
	}

	public void setController(TabbedGUIPaneController ctlr) {
		controller = ctlr;
	}

	public void setButtonGroup(ButtonGroup group) {
		group.add(synthesisPlanMonomersButton);

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ActionCommandInterface.CLICK)) {

			AppEvent ape = new AppEvent(AppEvent.SOURCE_EVENT);
			ape.setMessage(((JRadioButton) ae.getSource()).getText());
			controller.handleAppEvent(ape);
		}

	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		SourceGUISynthesisPlanMonomersSubPane subpane = new SourceGUISynthesisPlanMonomersSubPane();
//		TabbedGUIPaneModel tabbedModel = new TabbedGUIPaneModel();
		
		frame.add(subpane);
		subpane.setController(new TabbedGUIPaneController());
		subpane.init();
		frame.setVisible(true);
		
	}
}
