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
/*
 * Created on Nov 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.controller.MasterController;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.beans.PropertyVetoException;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class CeNInternalFrame extends JInternalFrame {

	private static final long serialVersionUID = -3494739111351458203L;

	public CeNInternalFrame() {
		super("", true, true, true, true);
	}

	public CeNInternalFrame(String name) {
		super(name);
	}

	public void postInitGUI() {
		final CeNInternalFrame gui = this;
		this.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameOpened(InternalFrameEvent e) {
				// System.out.println("Opened");
				try {
					((CeNInternalFrame) e.getSource()).setMaximum(true);
				} catch (Exception e2) {
				}
				// MasterController.getGUIComponent().refreshIcons();
				((CeNInternalFrame) e.getSource()).moveToFront();
			}

			public void internalFrameActivated(InternalFrameEvent e) {
				MasterController.getGUIComponent().refreshIcons();
				gui.internalFrameActivated();
			}

			public void internalFrameDeiconified(InternalFrameEvent e) {
				// System.out.println("Deiconified");
				MasterController.getGUIComponent().refreshIcons();
			}

			public void internalFrameIconified(InternalFrameEvent e) {
				// System.out.println("Iconified");
				MasterController.getGUIComponent().refreshIcons();
			}
		});
		// try and set current window to selected
		try {
			this.setSelected(true);
		} catch (PropertyVetoException e) {
		}
	}

	public boolean shouldClose() {
		return true;
	}

	public void internalFrameActivated() {
	}

	public void internalFrameClosing() {
	}
}
