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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.experiment.WellColorLegendDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowColorLegendDialogMenuItem extends JMenuItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6812406565946180306L;

	public ShowColorLegendDialogMenuItem() {
		super("Color Legend");
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showColorLegendDialog();
			}
		});
	}

	private void showColorLegendDialog() {
		WellColorLegendDialog mWellColorLegendDialog = new WellColorLegendDialog(MasterController.getGUIComponent());
		Dimension frmSize = mWellColorLegendDialog.getSize();
		Dimension dlgSize = new Dimension(550, 300);
		mWellColorLegendDialog.setSize(dlgSize);
		mWellColorLegendDialog.setLocation(200, 200);
		mWellColorLegendDialog.setVisible(true);
	}
}
