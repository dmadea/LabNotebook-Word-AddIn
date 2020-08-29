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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class OtherPrefPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 1749931870959633417L;
	
	private JRadioButton jRadioButton4;
	private JRadioButton jRadioButton3;
	private JLabel jLabel4;
	private JButton jButtonOtherPref2;
	private JButton jButtonOtherPref1;
	private JList jList1;
	private JLabel jLabel3;
	private JRadioButton jRadioButton2;
	private JRadioButton jRadioButton1;
	private JLabel jLabel2;
	private JPanel jPanelOtherPref;
	private JScrollPane jScrollPaneOtherPref;

	public OtherPrefPanel() {
		initGUI();
	}

	/** Auto-generated event handler method */
	protected void jButtonOtherPref1ActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
	}

	/** Auto-generated event handler method */
	protected void jButtonOtherPref2ActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jScrollPaneOtherPref = new JScrollPane();
			jPanelOtherPref = new JPanel();
			jLabel2 = new JLabel();
			jRadioButton1 = new JRadioButton();
			jRadioButton2 = new JRadioButton();
			jLabel3 = new JLabel();
			jList1 = new JList();
			jButtonOtherPref1 = new JButton();
			jButtonOtherPref2 = new JButton();
			jLabel4 = new JLabel();
			jRadioButton3 = new JRadioButton();
			jRadioButton4 = new JRadioButton();
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(250, 301));
			jScrollPaneOtherPref.setPreferredSize(new java.awt.Dimension(265, 301));
			this.add(jScrollPaneOtherPref, BorderLayout.CENTER);
			jPanelOtherPref.setLayout(null);
			jPanelOtherPref.setPreferredSize(new java.awt.Dimension(244, 298));
			jScrollPaneOtherPref.add(jPanelOtherPref);
			jScrollPaneOtherPref.setViewportView(jPanelOtherPref);
			jLabel2.setText("2. Calculate Weight & Volume");
			jLabel2.setPreferredSize(new java.awt.Dimension(192, 20));
			jLabel2.setBounds(new java.awt.Rectangle(34, 18, 192, 20));
			jPanelOtherPref.add(jLabel2);
			jRadioButton1.setText("Manual");
			jRadioButton1.setPreferredSize(new java.awt.Dimension(94, 20));
			jRadioButton1.setBounds(new java.awt.Rectangle(52, 40, 94, 20));
			jPanelOtherPref.add(jRadioButton1);
			jRadioButton2.setText("Automatic");
			jRadioButton2.setPreferredSize(new java.awt.Dimension(103, 20));
			jRadioButton2.setBounds(new java.awt.Rectangle(54, 61, 103, 20));
			jPanelOtherPref.add(jRadioButton2);
			jLabel3.setText("3. Chemical Name/Alias Filling Order");
			jLabel3.setPreferredSize(new java.awt.Dimension(210, 20));
			jLabel3.setBounds(new java.awt.Rectangle(30, 104, 210, 20));
			jPanelOtherPref.add(jLabel3);
			jList1.setPreferredSize(new java.awt.Dimension(115, 59));
			jList1.setBounds(new java.awt.Rectangle(51, 127, 115, 59));
			jPanelOtherPref.add(jList1);
			jButtonOtherPref1.setText("Up");
			jButtonOtherPref1.setPreferredSize(new java.awt.Dimension(60, 20));
			jButtonOtherPref1.setBounds(new java.awt.Rectangle(176, 133, 60, 20));
			jPanelOtherPref.add(jButtonOtherPref1);
			jButtonOtherPref1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOtherPref1ActionPerformed(evt);
				}
			});
			jButtonOtherPref2.setText("Dn");
			jButtonOtherPref2.setPreferredSize(new java.awt.Dimension(60, 20));
			jButtonOtherPref2.setBounds(new java.awt.Rectangle(178, 159, 60, 20));
			jPanelOtherPref.add(jButtonOtherPref2);
			jButtonOtherPref2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOtherPref2ActionPerformed(evt);
				}
			});
			jLabel4.setText("4. Table Display Orientation");
			jLabel4.setPreferredSize(new java.awt.Dimension(189, 20));
			jLabel4.setBounds(new java.awt.Rectangle(36, 222, 189, 20));
			jPanelOtherPref.add(jLabel4);
			jRadioButton3.setText("Landscape");
			jRadioButton3.setPreferredSize(new java.awt.Dimension(97, 20));
			jRadioButton3.setBounds(new java.awt.Rectangle(70, 246, 97, 20));
			jPanelOtherPref.add(jRadioButton3);
			jRadioButton4.setText("Portrait");
			jRadioButton4.setPreferredSize(new java.awt.Dimension(91, 20));
			jRadioButton4.setBounds(new java.awt.Rectangle(72, 268, 91, 20));
			jPanelOtherPref.add(jRadioButton4);
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
			OtherPrefPanel inst = new OtherPrefPanel();
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
