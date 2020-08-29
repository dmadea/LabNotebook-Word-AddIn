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
import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class NewPageDialog extends CeNDialog {
	
	private static final long serialVersionUID = 98704938092818519L;
	
	private JButton jButtonNo;
	private JButton jButtonYes;
	private JLabel jLabel2;
	private JLabel jLabelnewpaenumber;
	private JLabel jLabel1;
	private JPanel jPanel1;
	private int result;
	private String _newPage;
	public static final int YES = 1;
	public static final int NO = 0;

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public NewPageDialog(Frame arg0, String newPage) throws HeadlessException {
		super(arg0);
		_newPage = newPage;
		initGUI();
		// TODO Auto-generated constructor stub
	}

	public NewPageDialog() {
		initGUI();
	}

	public NewPageDialog(String newPage) {
		_newPage = newPage;
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanel1 = new JPanel();
			jLabel1 = new JLabel();
			jLabelnewpaenumber = new JLabel();
			jLabel2 = new JLabel();
			jButtonYes = new JButton();
			jButtonNo = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("New Notebook Page ?");
			this.setUndecorated(false);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(325, 180));
			jPanel1.setLayout(null);
			jPanel1.setPreferredSize(new java.awt.Dimension(312, 153));
			this.getContentPane().add(jPanel1, BorderLayout.CENTER);
			jLabel1.setText("You are about to create a new page with this number:");
			jLabel1.setPreferredSize(new java.awt.Dimension(268, 20));
			jLabel1.setBounds(new java.awt.Rectangle(25, 20, 268, 20));
			jPanel1.add(jLabel1);
			jLabelnewpaenumber.setText("-none-");
			jLabelnewpaenumber.setPreferredSize(new java.awt.Dimension(259, 20));
			jLabelnewpaenumber.setBounds(new java.awt.Rectangle(25, 42, 259, 20));
			jPanel1.add(jLabelnewpaenumber);
			jLabel2.setText("Are you sure ?");
			jLabel2.setPreferredSize(new java.awt.Dimension(110, 20));
			jLabel2.setBounds(new java.awt.Rectangle(25, 73, 110, 20));
			jPanel1.add(jLabel2);
			jButtonYes.setText("Yes");
			jButtonYes.setPreferredSize(new java.awt.Dimension(60, 20));
			jButtonYes.setBounds(new java.awt.Rectangle(48, 110, 60, 20));
			jPanel1.add(jButtonYes);
			jButtonYes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonYesActionPerformed(evt);
				}
			});
			jButtonNo.setText("No");
			jButtonNo.setPreferredSize(new java.awt.Dimension(60, 20));
			jButtonNo.setBounds(new java.awt.Rectangle(190, 110, 60, 20));
			jPanel1.add(jButtonNo);
			jButtonNo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonNoActionPerformed(evt);
				}
			});
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
		jLabelnewpaenumber.setText(_newPage);
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
			NewPageDialog inst = new NewPageDialog();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the result.
	 */
	public int getResult() {
		return result;
	}

	/** Auto-generated event handler method */
	protected void jButtonYesActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
		result = YES;
		setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jButtonNoActionPerformed(ActionEvent evt) {
		// TODO add your handler code here
		result = NO;
		setVisible(false);
	}
}
