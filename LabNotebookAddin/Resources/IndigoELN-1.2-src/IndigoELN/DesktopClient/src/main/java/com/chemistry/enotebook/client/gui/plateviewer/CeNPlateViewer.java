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
package com.chemistry.enotebook.client.gui.plateviewer;

import com.virtuan.plateVisualizer.PlateModel;
import com.virtuan.plateVisualizer.PlateMood;
import com.virtuan.plateVisualizer.StaticPlateRenderer;
import com.virtuan.plateVisualizer.WellMood;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

// Is this used???????????????????
public class CeNPlateViewer extends StaticPlateRenderer implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5485937720944020296L;

	public CeNPlateViewer(int numberOfRows, int numberOfColumns, PlateMood plateMood, WellMood initialWellMood,
			ArrayList initialWellProperties, boolean displayWellProperties, String propertiesFrameTitle, PlateModel plateModel) {
		super(numberOfRows, numberOfColumns, plateMood, initialWellMood, initialWellProperties, displayWellProperties,
				propertiesFrameTitle, plateModel, null, null);  // added null param vb 5/30  vb 7/11
	}

	/**
	 * method to create the pop up menu to select the various standard plate types
	 * 
	 * @param panel
	 */
	protected void setStandardPopUpMenuItems() {
		getPopupMenu().removeAll();
		JMenuItem menuCmd1 = new JMenuItem("COMMAND1");
		menuCmd1.addActionListener(this);
		JMenuItem menuCmd2 = new JMenuItem("COMMAND2");
		menuCmd2.addActionListener(this);
		getPopupMenu().add(menuCmd1);
		getPopupMenu().add(menuCmd2);
	}

	/**
	 * list Selection event receiver.
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
	}

	transient Vector menuActionListeners;

	public synchronized void addMenuActionListener(MenuActionListener l) {
		menuActionListeners.addElement(l);
	}

	public synchronized void removeMenuActionListener(MenuActionListener l) {
		menuActionListeners.removeElement(l);
	}

	protected void fireMenuActionPerformed(MenuActionEvent e) {
		if (menuActionListeners != null) {
			Vector listeners = menuActionListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((MenuActionListener) listeners.elementAt(i)).menuActionPerformed(e);
			}
		}
	}
}
