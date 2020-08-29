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

import javax.swing.*;
import java.awt.*;

public class DefaultPreferences {

	public final static int SHORT_ROW_HEIGHT = 20;
	public final static int TALL_ROW_HEIGHT = 70;

	public static Font getStandardTableCellFont() {
		return new Font("Tahoma", Font.PLAIN, 11);
	}

	public static Font getStandardTableHeaderFont() {
		return new Font("Tahoma", Font.BOLD, 11);
	}

	public static Color getNonEditableRowBackgroundColor() {
		// return new Color(255, 255, 245);
		return new Color(220, 220, 210);
	}

	public static Color getSelectedRowForegroundColor() {
		// return new Color(255, 255, 245);
		return new Color(50, 50, 100); // vb 11/2
	}

	public static Color getSelectedRowBackgroundColor() {
		// return new Color(100, 100, 150); vb 11/2
		return new Color(255, 255, 245);
	}

	public static Color getRowForegroundColor() {
		return new Color(30, 64, 124);
		// return new Color(255, 255, 245);
	}

	public static Color getEditableRowBackgroundColor() {
		return new Color(255, 255, 245);
	}

	public static Color getTableHeaderBackgroundColor() {
		return new Color(100, 120, 150);
	}

	public static Color getTableHeaderForegroundColor() {
		return new Color(255, 255, 245);
	}
	
	public static Color getDisabledElementBackgroundColor() {
		return new Color(212, 208, 200);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(panel);
		frame.setBounds(20, 30, 400, 400);
		frame.setVisible(true);

	}

	public static Color getErrorMsgRowForegroundColor() {
		return Color.RED;
	}
}
