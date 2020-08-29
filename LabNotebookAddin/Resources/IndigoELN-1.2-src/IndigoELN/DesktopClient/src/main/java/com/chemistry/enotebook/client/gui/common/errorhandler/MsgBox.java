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
 * Created on Sep 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui.common.errorhandler;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.ResetButWinListener;
import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class MsgBox extends CeNDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3856720552028107372L;
	private static Component anchor = null;
	private JTextArea jTextAreaMsg;
	private JScrollPane jScrollPane1;
	private JButton jButtonOK;
	private JPanel jPanel1;

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public MsgBox(Frame arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public MsgBox(Dialog arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	public MsgBox() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanel1 = new JPanel();
			jButtonOK = new JButton();
			jScrollPane1 = new JScrollPane();
			jTextAreaMsg = new JTextArea();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setFocusableWindowState(true);
			this.setSize(new java.awt.Dimension(607, 327));
			this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
			jButtonOK.setText("OK");
			jButtonOK.setPreferredSize(new java.awt.Dimension(51, 26));
			jPanel1.add(jButtonOK);
			jButtonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOKActionPerformed(evt);
				}
			});
			jScrollPane1.setAutoscrolls(true);
			this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
			jTextAreaMsg.setEditable(false);
			jScrollPane1.add(jTextAreaMsg);
			jScrollPane1.setViewportView(jTextAreaMsg);
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	public void clearGUI() {
		setTitle("");
		jTextAreaMsg.setText("");
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		Dimension dim = getToolkit().getScreenSize();
		setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);
		this.getRootPane().setDefaultButton(jButtonOK);
		this.addWindowListener(new ResetButWinListener(jButtonOK, this));
	}

	/** Auto-generated event handler method */
	protected void jButtonOKActionPerformed(ActionEvent evt) {
		setVisible(false);
	}

	public static void showMsgBox(Component parent, String title, String msg) {
		try {
			MsgBox inst = null;
			if (parent instanceof Frame) {
				inst = new MsgBox((Frame) parent);
			} else if (parent instanceof Dialog) {
				inst = new MsgBox((Dialog) parent);
			} else {
				inst = new MsgBox(MasterController.getGUIComponent());
			}
			inst.setTitle(title);
			inst.jTextAreaMsg.setText(msg);
			inst.jTextAreaMsg.setCaretPosition(0);
			inst.setModal(true);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace(); // Not much logging to server since this
			// is where it would be done
		}
	}
}
