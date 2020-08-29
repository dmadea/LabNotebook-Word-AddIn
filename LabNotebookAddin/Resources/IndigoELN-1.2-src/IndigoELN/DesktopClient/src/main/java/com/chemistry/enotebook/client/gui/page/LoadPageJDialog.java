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
package com.chemistry.enotebook.client.gui.page;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/
public class LoadPageJDialog extends javax.swing.JDialog {
	
	private static final long serialVersionUID = -1169191275489889064L;
	
	private JLabel jLabelMsg;
//	private JLabel jLabelSite;
	private JLabel jLabelNbRef;
	private JTextField jTextFieldNbRef;
//	private JComboBox jComboBoxSites;
	private JButton jButtonCancel;
	private JPanel jPanelSpacer;
	private JButton jButtonOK;
	private JPanel jPanelButtons;
	private JPanel jPanelInputs;
	private Frame owner;
	
	private boolean canceled = true;
	
	
	public LoadPageJDialog(Frame parent) {
		super(parent);
		this.owner = parent;
		setTitle("Open an Existing Experiment");
		initGUI();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
			jPanelInputs = new JPanel();
			jLabelMsg = new JLabel();
//			jLabelSite = new JLabel();
			jLabelNbRef = new JLabel();
			jTextFieldNbRef = new JTextField();
//			jComboBoxSites = new JComboBox();
			jPanelButtons = new JPanel();
			jButtonOK = new JButton();
			jPanelSpacer = new JPanel();
			jButtonCancel = new JButton();
	
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//			this.setSize(new java.awt.Dimension(345,126));
			this.setSize(new java.awt.Dimension(300, 126));
	
			AnchorLayout jPanelInputsLayout = new AnchorLayout();
			jPanelInputs.setLayout(jPanelInputsLayout);
			jPanelInputs.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			this.getContentPane().add(jPanelInputs, BorderLayout.CENTER);
	
			jLabelMsg.setText("Enter a Notebook Reference (########-####[v#]):");
			jLabelMsg.setVisible(true);
			jLabelMsg.setPreferredSize(new java.awt.Dimension(318,20));
			jLabelMsg.setBounds(new java.awt.Rectangle(6,6,318,20));
			jPanelInputs.add(jLabelMsg, new AnchorConstraint(106,960, 434, 19, 1, 1, 1, 1));
	
//			jLabelSite.setText("Site:");
//			jLabelSite.setVisible(true);
//			jLabelSite.setFont(new java.awt.Font("Dialog",1,12));
//			jLabelSite.setPreferredSize(new java.awt.Dimension(30,26));
//			jLabelSite.setBounds(new java.awt.Rectangle(8,27,30,26));
//			jPanelInputs.add(jLabelSite, new AnchorConstraint(450,113, 877, 25, 1, 1, 1, 1));
	
			jLabelNbRef.setText("Notebook Ref:");
			jLabelNbRef.setVisible(true);
			jLabelNbRef.setFont(new java.awt.Font("Dialog",1,12));
			jLabelNbRef.setPreferredSize(new java.awt.Dimension(88,26));
//			jLabelNbRef.setBounds(new java.awt.Rectangle(146,27,88,26));
//			jPanelInputs.add(jLabelNbRef, new AnchorConstraint(450,693, 877, 433, 1, 1, 1, 1));
			jPanelInputs.add(jLabelNbRef, new AnchorConstraint(450,693, 877, 25, 1, 1, 1, 1));
	
			jTextFieldNbRef.setVisible(true);
			jTextFieldNbRef.setPreferredSize(new java.awt.Dimension(99,19));
			jTextFieldNbRef.setFocusCycleRoot(true);
//			jTextFieldNbRef.setBounds(new java.awt.Rectangle(230,31,99,19));
//			jPanelInputs.add(jTextFieldNbRef, new AnchorConstraint(516,974, 827, 681, 1, 1, 1, 1));
			jPanelInputs.add(jTextFieldNbRef, new AnchorConstraint(516,874, 827, 380, 1, 1, 1, 1));
			jTextFieldNbRef.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jTextFieldNbRefActionPerformed(evt);
				}
			});
	
//			jComboBoxSites.setVisible(true);
//			jComboBoxSites.setPreferredSize(new java.awt.Dimension(88,18));
//			jComboBoxSites.setBounds(new java.awt.Rectangle(42,32,88,18));
//			jComboBoxSites.setFocusable(false);
//			jPanelInputs.add(jComboBoxSites, new AnchorConstraint(532,386, 827, 125, 1, 1, 1, 1));
	
			jPanelButtons.setBackground(new java.awt.Color(204,204,204));
			jPanelButtons.setPreferredSize(new java.awt.Dimension(338,38));
			this.getContentPane().add(jPanelButtons, BorderLayout.SOUTH);
	
			jButtonOK.setText("OK");
			jButtonOK.setFont(new java.awt.Font("Dialog",1,12));
			jButtonOK.setPreferredSize(new java.awt.Dimension(51,26));
			jPanelButtons.add(jButtonOK);
			jButtonOK.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOKActionPerformed(evt);
				}
			});
	
			jPanelButtons.add(jPanelSpacer);
	
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("Dialog",1,12));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(73,26));
			jPanelButtons.add(jButtonCancel);
			jButtonCancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
	
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI(){
//		CodeTableUtils.fillComboBoxWithSites(jComboBoxSites);
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	* This static method creates a new instance of this class and shows
	* it inside a new JFrame, (unless it is already a JFrame).
	*
	* It is a convenience method for showing the GUI, but it can be
	* copied and used as a basis for your own code.	*
	* It is auto-generated code - the body of this method will be
	* re-generated after any changes are made to the GUI.
	* However, if you delete this method it will not be re-created.	*/
	public static void showGUI(){
		try {
			LoadPageJDialog inst = new LoadPageJDialog(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	public String getSiteCode() { 
//		String site = (String)jComboBoxSites.getSelectedItem();
//		if (site != null) {
//			try {
//				return CodeTableCache.getCache().getSiteCode(site);
//			} catch (Exception e) { return null; }
//		} else
//			return null;
//	}
	public String getNotebookRef() { return jTextFieldNbRef.getText().trim(); }
	
	public boolean getPageToLoad() {
		canceled = true;
					
		jTextFieldNbRef.setText("");
		jTextFieldNbRef.requestFocus();
		
		setModal(true);
		setLocationRelativeTo(owner);
		setVisible(true);
		
		return !canceled && 
//			   getSiteCode() != null &&
			   getNotebookRef().length() > 0;
	}

	/** Auto-generated event handler method */
	protected void jButtonOKActionPerformed(ActionEvent evt){
		jTextFieldNbRefActionPerformed(evt);
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt){
		setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jTextFieldNbRefActionPerformed(ActionEvent evt){
		if (getNotebookRef().length() > 0) {
			canceled = false;
			setVisible(false);
		}
	}
}