/****************************************************************************
 * Copyright (C) 2009-2012 EPAM Systems
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

package com.epam.indigo.eln.tools.users;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.epam.indigo.eln.tools.users.db.DBService;
import com.epam.indigo.eln.tools.users.ui.ConsoleUI;
import com.epam.indigo.eln.tools.users.ui.UsersToolWindow;

public class UsersTool {

	public static void main(String[] args) {
		if (GraphicsEnvironment.isHeadless())
			doConsole();
		else
			doGraphics();
	}

	private static void doConsole() {
		ConsoleUI.doConsole();
	}

	private static void doGraphics() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			DBService dbService = new DBService();

			UsersToolWindow window = new UsersToolWindow(dbService);
			window.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}
}
