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
package com.chemistry.enotebook.client.gui.page.experiment.plate.controller;

import com.chemistry.enotebook.client.gui.page.experiment.plate.model.CreatePlateModel;
import com.chemistry.enotebook.client.gui.page.experiment.plate.model.TabbedGUIPaneModel;
import com.chemistry.enotebook.client.gui.page.experiment.plate.view.CreatePlateDialog;
import com.chemistry.enotebook.client.gui.page.experiment.plate.view.TabbedGUIPane;

import javax.swing.*;

public class CreatePlateDialogController {
	private TabbedGUIPaneController childController_;

	private CreatePlateDialog view;
	private CreatePlateModel model;

	public void init() {
		view.init();
		createChildTriad();
		childController_.init();
		view.setVisible(true);
	}

	public void setChildController(TabbedGUIPaneController childCtlr) {

		childController_ = childCtlr;

	}

	public void createChildTriad() {
		TabbedGUIPane tabbedPane = new TabbedGUIPane();
		TabbedGUIPaneModel tabbedModel = new TabbedGUIPaneModel();
		TabbedGUIPaneController tabbedController = new TabbedGUIPaneController();

		tabbedController.setView(tabbedPane);
		tabbedController.setModel(tabbedModel);

		tabbedController.setParentController(this);
		setChildController(tabbedController);
		view.setPane(tabbedPane);

	}

	public void setView(CreatePlateDialog view) {
		this.view = view;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		CreatePlateDialogController cpd = factory(frame);
	}


	public static CreatePlateDialogController factory(JFrame parentFrame) {
		CreatePlateDialog view = new CreatePlateDialog(parentFrame, true);
		CreatePlateModel model = new CreatePlateModel();
		CreatePlateDialogController controller = new CreatePlateDialogController();
		controller.setView(view);
		controller.setModel(model);

		controller.init();
		return controller;
	}

	public void handleAppEvent(AppEvent ae) {
		if (ae.isSourceEvent()) {
			view.getSummaryPane().setSourceText(ae.getMessage());
		} else if (ae.isSortEvent()) {
			view.getSummaryPane().setSortText(ae.getMessage());

		} else if (ae.isExcludeEvent()) {
			view.getSummaryPane().setExcludeText(ae.getMessage());
		} else if (ae.isTargetEvent()) {
			view.getSummaryPane().setTargetText(ae.getMessage());
		} else {
			// this.parentCtlr_.
		}
	}

	public void setModel(CreatePlateModel model) {
		this.model = model;
	}
}