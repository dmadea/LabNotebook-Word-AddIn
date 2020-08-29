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
package com.chemistry.enotebook.client.gui.preferences.stoic;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class StoicPrefPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = -3007513537591265496L;
	
	private OtherPrefPanel otherPrefPanel1;
	private ColumnDisplayPrefPanel columnDisplayPrefPanel;

	public StoicPrefPanel() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			columnDisplayPrefPanel = new ColumnDisplayPrefPanel();
			otherPrefPanel1 = new OtherPrefPanel();
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(800, 369));
			columnDisplayPrefPanel.setPreferredSize(new java.awt.Dimension(786, 369));
			this.add(columnDisplayPrefPanel, BorderLayout.CENTER);
			otherPrefPanel1.setPreferredSize(new java.awt.Dimension(255, 369));
			columnDisplayPrefPanel.add(otherPrefPanel1, BorderLayout.EAST);
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
			javax.swing.JFrame frame = new javax.swing.JFrame();
			StoicPrefPanel inst = new StoicPrefPanel();
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButton1ActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
	}

	/** Auto-generated event handler method */
	protected void jButton2ActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
	}
}
