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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class CeNDialog extends JDialog {

	private static final long serialVersionUID = -4376722938158320684L;

	public CeNDialog() {
		this((Frame) null, false);
	}

	public CeNDialog(Frame owner) {
		this(owner, false);
	}

	public CeNDialog(Frame owner, boolean modal) {
		this(owner, null, modal);
	}

	public CeNDialog(Frame owner, String title) {
		this(owner, title, false);
	}

	public CeNDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public CeNDialog(Dialog owner) {
		this(owner, false);
	}

	public CeNDialog(Dialog owner, boolean modal) {
		this(owner, null, modal);
	}

	public CeNDialog(Dialog owner, String title) {
		this(owner, title, false);
	}

	public CeNDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				defaultCancelAction();
			}
		};
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		
		rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				defaultApplyAction();
			}
		};
		
		stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		
		rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		/*
		 * KeyListener keyListener = new KeyListener() { public void
		 * keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_SPACE)
		 * e.consume(); else System.out.println(e.getKeyCode()); } public void
		 * keyReleased(KeyEvent e) {} public void keyTyped(KeyEvent e) {} };
		 * Component[] components = this.getComponents(); for(int i = 0; i <
		 * components.length; i++) { Component[] comps =
		 * ((Container)components[i]).getComponents(); for(int j = 0; j <
		 * comps.length; j++) if(comps[j] instanceof JButton) { JButton button =
		 * (JButton)comps[j]; button.addKeyListener(keyListener); } }
		 */
		
		return rootPane;
	}

	/**
	 * The method is invoked when ENTER is pressed
	 */
	protected void defaultApplyAction() {
	};

	/**
	 * The method is invoked when ESCAPE is pressed
	 */
	protected void defaultCancelAction() {
		this.dispose();
	};
}
