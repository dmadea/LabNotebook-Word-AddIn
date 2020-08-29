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

import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.CreatePlateDialogController;

import javax.swing.*;
import java.awt.*;

public class CreatePlateDialog extends JDialog {
	
	private static final long serialVersionUID = 122388482947394303L;
	
	private TabbedGUIPane tabbedPane_;
	private SummaryGUIPane summaryPane_;

	public CreatePlateDialog(JFrame frame, boolean b) {
		super(frame, b);
	}

	public TabbedGUIPane getTabbedPane() {
		return tabbedPane_;
	}

	public SummaryGUIPane getSummaryPane() {
		return summaryPane_;
	}

	private CreatePlateDialogController controller;

	public void setController(CreatePlateDialogController ctlr) {
		controller = ctlr;
	}

	public void init() {
		setSize(new Dimension(640, 480));
		summaryPane_ = new SummaryGUIPane(controller);
		summaryPane_.init();
		// setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void gridLayoutInit() {

		setLayout(new GridLayout(0, 1));
		add(tabbedPane_);
		add(summaryPane_);
		validate();
	}

	public void setPane(TabbedGUIPane tabbedPane) {
		tabbedPane_ = tabbedPane;
		gridLayoutInit();

	}

}
