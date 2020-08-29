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

import com.chemistry.enotebook.client.controller.MasterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class CeNProgressJDialog extends CeNDialog {
	
	private static final long serialVersionUID = 4680327555491457738L;
	
	// private JPanel jPanel2;
	private JButton jButton1;
	private JScrollPane jScrollPane1;
	private JPanel jPanel1;

	public CeNProgressJDialog() {
		super(MasterController.getGUIComponent());
		// setModal(true);
		setTitle("All CeN progresses");
		setLocation(getParent().getX() + (getParent().getWidth() - getWidth()) / 2, getParent().getY()
				+ (getParent().getHeight() / 2));
		// setUndecorated(true);
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanel1 = new JPanel();
			jButton1 = new JButton();
			jScrollPane1 = new JScrollPane();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setSize(new java.awt.Dimension(452, 262));
			jPanel1.setPreferredSize(new java.awt.Dimension(445, 31));
			this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
			jButton1.setText("Close");
			jButton1.setPreferredSize(new java.awt.Dimension(81, 26));
			jPanel1.add(jButton1);
			jButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});
			jScrollPane1.setPreferredSize(new java.awt.Dimension(445, 220));
			this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI(JPanel panel) {
		try {
			CeNProgressJDialog inst = new CeNProgressJDialog();
			inst.setPanel(panel);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButton1ActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
		// removeAll();
		dispose();
	}

	public void setPanel(JPanel panel) {
		jScrollPane1.add(panel);
		jScrollPane1.setViewportView(panel);
	}
}
