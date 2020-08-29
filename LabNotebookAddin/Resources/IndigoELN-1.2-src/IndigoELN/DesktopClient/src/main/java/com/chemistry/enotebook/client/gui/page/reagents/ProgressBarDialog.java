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
 * Created on Sep 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ProgressBarDialog extends JDialog {
	
	private static final long serialVersionUID = 9005737069758352386L;
	
	private JTextPane jTextPane1;
	private JProgressBar jProgressBar1;
	private JPanel jPanel1;
	private JButton cancelButton;
	private ActionListener actionListener;

	public ProgressBarDialog() throws HeadlessException {
		initGUI();
	}

	/**
	 * @param Frame
	 * @throws java.awt.HeadlessException
	 */
	public ProgressBarDialog(Frame parent) throws HeadlessException {
		super(parent);
		setModal(true);
		// setTitle("Processing, please wait ...");
		initGUI();
	}

	public ProgressBarDialog(Frame parent, String message) throws HeadlessException {
		this(parent);
		// setTitle("Searching, please wait....");
		initGUI();
		jTextPane1.setText(message);
		jTextPane1.setVisible(true);
	}

	/**
	 * @param Frame
	 * @param ActionListener
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public ProgressBarDialog(Frame parent, ActionListener listener) throws HeadlessException {
		this(parent);
		this.actionListener = listener;
		// setTitle("Processing, please wait ...");
		initGUI();
	}

	protected void processWindowEvent(WindowEvent e) {
		// this.setTitle("Searching is undergoing, please wait...");
	}

	/**
	 * Initializes the GUI.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanel1 = new JPanel();
			jTextPane1 = new JTextPane();
			jProgressBar1 = new JProgressBar();
			if (actionListener != null) {
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(actionListener);
			}
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			setSize(new java.awt.Dimension(317, 91));
			// this.setEnabled(false);
			BorderLayout jPanel1Layout = new BorderLayout();
			jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout.setHgap(0);
			jPanel1Layout.setVgap(0);
			jPanel1.setPreferredSize(new java.awt.Dimension(354, 72));
			getContentPane().add(jPanel1, BorderLayout.CENTER);
			if (actionListener == null) {
				jTextPane1.setText("");
				jTextPane1.setEnabled(false);
				jTextPane1.setVisible(false);
				jPanel1.add(jTextPane1, BorderLayout.SOUTH);
			} else
				jPanel1.add(cancelButton, BorderLayout.SOUTH);
			jPanel1.add(jProgressBar1, BorderLayout.CENTER);
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		jProgressBar1.setIndeterminate(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			ProgressBarDialog inst = new ProgressBarDialog(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		this.setVisible(false);
		this.jTextPane1.setVisible(false);
		this.jPanel1.setVisible(false);
		if (actionListener != null)
			this.cancelButton.removeActionListener(this.actionListener);
		this.jPanel1.removeAll();
		this.removeAll();
		super.dispose();
	}
}
