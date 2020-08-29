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

import com.chemistry.enotebook.client.gui.page.experiment.plate.model.TabbedGUIPaneModel;
import com.chemistry.enotebook.client.gui.page.experiment.plate.view.TabbedGUIPane;

public class TabbedGUIPaneController {
	TabbedGUIPane view;
	TabbedGUIPaneModel model;
	private CreatePlateDialogController parentController;

	public void setView(TabbedGUIPane tabbedPane) {
		view = tabbedPane;
		
	}

	public void setModel(TabbedGUIPaneModel tabbedModel) {
		model = tabbedModel;
		
	}

	public void setParentController(CreatePlateDialogController c) {
		parentController = c;
		
	}

	public void init() {
    	
        view.setController(this);
        view.init();
            
        model.setView(view);
        model.init();
		
	}
	
	 public void handleAppEvent(AppEvent ae)
	    {
		 parentController.handleAppEvent(ae);
	    }

	public void getMonomerList() {
//		return model.getMonomerList();
		
	}



}
