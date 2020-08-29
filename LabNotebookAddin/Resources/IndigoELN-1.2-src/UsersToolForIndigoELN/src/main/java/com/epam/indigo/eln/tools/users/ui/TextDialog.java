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

package com.epam.indigo.eln.tools.users.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class TextDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -183325639968255070L;

	public TextDialog(Frame owner, String text) {
		JTextArea area = new JTextArea(text);
		JScrollPane pane = new JScrollPane(area);
		JButton closeButton = new JButton(UIConstants.CLOSE_WINDOW);
		
		Font textFieldFont = UIManager.getFont("TextField.font");
		
		if (textFieldFont != null)
			area.setFont(textFieldFont);
		
		area.setEditable(false);
		area.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		
		closeButton.addActionListener(this);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(pane, BorderLayout.CENTER);
		getContentPane().add(closeButton, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		setResizable(false);
		setSize(600, 400);
		setModal(true);
		setLocationRelativeTo(owner);
		
		closeButton.requestFocus();
	}

	public void actionPerformed(ActionEvent e) {
		dispose();
	}
}
