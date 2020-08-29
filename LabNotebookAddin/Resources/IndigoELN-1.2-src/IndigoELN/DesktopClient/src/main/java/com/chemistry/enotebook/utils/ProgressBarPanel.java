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

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ProgressBarPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = -7706358061521209612L;
	/*
	 * private JLabel jLabel3; private JButton jButton2; private JLabel jLabel2; private JButton jButton1; private JLabel jLabel1;
	 */
	private JPanel jPanel1;
	private JScrollPane jScrollPane1;
	private static ProgressBarPanel progressPanel;

	private ProgressBarPanel() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jScrollPane1 = new JScrollPane();
			jPanel1 = new JPanel();
			/*
			 * jLabel1 = new JLabel(); jButton1 = new JButton(); jLabel2 = new JLabel(); jButton2 = new JButton(); jLabel3 = new
			 * JLabel();
			 */
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			// this.setPreferredSize(new java.awt.Dimension(503,135));
			// jScrollPane1.setPreferredSize(new
			// java.awt.Dimension(502,120));
			this.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.add(jPanel1);
			jScrollPane1.setViewportView(jPanel1);
			/*
			 * jLabel1.setText("jLabel1"); jPanel1.add(jLabel1);
			 * 
			 * jButton1.setText("jButton1"); jPanel1.add(jButton1);
			 * 
			 * jLabel2.setText("jLabel2"); jPanel1.add(jLabel2);
			 * 
			 * jButton2.setText("jButton2"); jPanel1.add(jButton2);
			 * 
			 * jLabel3.setText("jLabel3"); jPanel1.add(jLabel3);
			 */
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

//	/** Auto-generated main method */
//	public static void main(String[] args) {
//		showGUI();
//	}
//
//	/**
//	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
//	 * 
//	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
//	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
//	 * delete this method it will not be re-created.
//	 */
//	public static void showGUI() {
//		try {
//			javax.swing.JFrame frame = new javax.swing.JFrame();
//			ProgressBarPanel inst = new ProgressBarPanel();
//			frame.setContentPane(inst);
//			frame.getContentPane().setSize(inst.getSize());
//			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//			frame.pack();
//			frame.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void showGUI(ProgressBarPanel inst) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.setUndecorated(true);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addItem(JComponent item) {
		jPanel1.add(item);
		jPanel1.repaint();
	}

	public void removeItem(JComponent item) {
		jPanel1.remove(item);
		jPanel1.repaint();
	}

	public void setLayout(GridLayout progressLayout) {
		jPanel1.setLayout(progressLayout);
	}

	public void setPanelSize(Dimension size) {
		this.setPreferredSize(size);
		jScrollPane1.setPreferredSize(size);
	}

	public static ProgressBarPanel getInstance() {
		if (progressPanel == null) {
			progressPanel = new ProgressBarPanel();
		} else {
			progressPanel = null;
		}
		return progressPanel;
	}
}
