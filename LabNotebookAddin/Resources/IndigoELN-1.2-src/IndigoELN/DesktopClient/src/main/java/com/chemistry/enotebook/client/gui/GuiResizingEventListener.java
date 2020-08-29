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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import javax.swing.*;
import java.awt.*;

public class GuiResizingEventListener {
	private Dimension size = new Dimension();

	public GuiResizingEventListener(Dimension defaults) {
		NotebookUser user = MasterController.getUser();
		try {
			String widthStr, heightStr;
			int width = 0, height = 0;
			// Get preferred width, use default if necessary
			widthStr = user.getPreference(NotebookUser.PREF_GUI_WIDTH);
			if (widthStr != null && widthStr.length() > 0)
				width = new Integer(widthStr).intValue();
			if (width == 0)
				width = defaults.width;
			// Get preferred height, use default if necessary
			heightStr = user.getPreference(NotebookUser.PREF_GUI_HEIGHT);
			if (heightStr != null && heightStr.length() > 0)
				height = new Integer(heightStr).intValue();
			if (height == 0)
				height = defaults.height;
			// Make sure we don't go over the maximum window size
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (width > screenSize.width)
				width = defaults.width;
			if (height > screenSize.height)
				height = defaults.height;
			size.setSize(width, height);
		} catch (UserPreferenceException e1) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
		}
	}

	public Dimension getPreferredGuiSize() {
		return size;
	}

	public void setUserPreferredGuiSize(JFrame frame) {
		NotebookUser user = MasterController.getUser();
		// we check if either the width or the height are below minimum
		if ((frame.getExtendedState() == JFrame.NORMAL)) {
			int width = frame.getWidth();
			int height = frame.getHeight();
			String widthAsStr = new String(width + "");
			String heightAsStr = new String(height + "");
			if (width > 0 && widthAsStr.length() > 0 && height > 0 && heightAsStr.length() > 0) {
				try {
					user.setPreference(NotebookUser.PREF_GUI_WIDTH, widthAsStr);
					user.setPreference(NotebookUser.PREF_GUI_HEIGHT, heightAsStr);
					user.updateUserPrefs();
				} catch (UserPreferenceException e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
				}
			}
		}
	}
}