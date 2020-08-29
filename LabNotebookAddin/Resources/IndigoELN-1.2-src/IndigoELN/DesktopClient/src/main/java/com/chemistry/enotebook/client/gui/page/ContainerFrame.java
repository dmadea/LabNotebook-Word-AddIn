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

package com.chemistry.enotebook.client.gui.page;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNInternalFrame;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

public class ContainerFrame extends JFrame {
	
	private static final long serialVersionUID = -3556091355740111594L;
	
	JDesktopPane desktop = null;

	public ContainerFrame() {
		desktop = new JDesktopPane();
		this.setContentPane(desktop);
		initGui();
	}

	public void initGui() {
		if (MasterController.getGUIComponent() != null)
			this.setIconImage(MasterController.getGUIComponent().getIconImage());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (ContainerFrame.this.getInternalFrame() != null)
					if (((CeNInternalFrame) (ContainerFrame.this.getInternalFrame())).shouldClose())
						ContainerFrame.this.getInternalFrame().dispose();
					else
						return; // Cancel close operation
				ContainerFrame.this.dispose();
			}

			public void windowClosed(WindowEvent evt) {
				setVisible(false);
			}
		});
		this.getRootPane().setGlassPane(CeNGUIUtils.createGlassPane());
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		getRootPane().getGlassPane().setVisible(!b);
	}
	
	public void addInternalFrame(JInternalFrame intFrame) {
		desktop.add(intFrame);
		intFrame.setVisible(true);
		try {
			intFrame.setSelected(true);
			intFrame.setMaximum(true);
		} catch (PropertyVetoException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public JInternalFrame getInternalFrame() {
		for (int i = 0; i < this.getComponents().length; i++) {
			try {
				Class<?> test = desktop.getComponent(i).getClass();
				while (test != Object.class) {
					if (test.getName().equals(CeNInternalFrame.class.getName()))
						return (JInternalFrame) desktop.getComponent(i);
					else
						test = test.getSuperclass();
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
}
