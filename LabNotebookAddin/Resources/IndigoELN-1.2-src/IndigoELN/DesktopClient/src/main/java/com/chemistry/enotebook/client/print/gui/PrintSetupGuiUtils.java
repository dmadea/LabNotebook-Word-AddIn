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
package com.chemistry.enotebook.client.print.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class PrintSetupGuiUtils {
	
	private PrintSetupGuiUtils() {
	}

	public static TitledBorder getEtchedTitledBorder(String title) {
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder tborder = BorderFactory.createTitledBorder(loweredetched, title);
		return tborder;
	}

	public static JCheckBox getCheckBox(String label, boolean checked, boolean enabled) {
		JCheckBox checkBox = new JCheckBox(label);
		checkBox.setSelected(checked);
		checkBox.setEnabled(enabled);
		return checkBox;
	}

	public static JRadioButton getRadioButton(String label, boolean selected, boolean enabled) {
		JRadioButton radioButton = new JRadioButton(label);
		radioButton.setSelected(selected);
		radioButton.setEnabled(enabled);
		return radioButton;
	}

	public static JTextField getTextField(String text, int cols, boolean editable) {
		JTextField textField = new JTextField(cols);
		if (text != null) {
			textField.setText(text);
		}
		textField.setEditable(editable);
		return textField;
	}

	public static JButton getButton(String label, boolean enabled) {
		JButton button = new JButton(label);
		button.setEnabled(enabled);
		return button;
	}
}
