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
package com.chemistry.enotebook.client.gui.page.regis_submis;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

public class RegistrationErrorHandlerDialog extends CeNDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110140501935252503L;
	private LinkedHashMap errorMap = null;
	private PCeNRegistration_ErrorViewControllerUtility controllerUtil;
	private PCeNRegistration_ErrorTableModel tableModel;
	private PCeNRegistration_ErrorTableView errorTableView;
	private boolean batchSelectionMode = false;
	private boolean selectFlag;
	
	public RegistrationErrorHandlerDialog(LinkedHashMap errorMap, boolean batchSelectionMode, boolean selectFlag) {
		this.errorMap = errorMap;
		this.batchSelectionMode = batchSelectionMode;
		this.selectFlag = selectFlag;
		setModal(true);
		initGUI();
	}

	/**
	 * Initializes the GUI.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			if (selectFlag)
				this.setTitle("Errors in Selection");
			else
				this.setTitle("Errors in Submission");
			this.getContentPane().setLayout(new BorderLayout());
			JPanel labelPanel = new JPanel();
			JLabel errorMessageLabel1 = new JLabel();
			JLabel errorMessageLabel2 = new JLabel();
			if (selectFlag)
			{
				errorMessageLabel1.setText("The following could not be selected (see Error Msg)."); 
				errorMessageLabel2.setText("To correct & reselect the batches with errors, click Cancel and correct errors.");
			}
			else
			{
				errorMessageLabel1.setText("The following could not be submitted (see Error Msg). To resubmit the others, click Continue."); 
				errorMessageLabel2.setText("To correct & resubmit the batches with errors, go to Batches tab and correct errors.");
			}
			Font f = new Font("Arial",Font.PLAIN,14); 
			errorMessageLabel1.setFont(f);
			errorMessageLabel1.setForeground(Color.BLACK);
			errorMessageLabel2.setFont(f);
			errorMessageLabel2.setForeground(Color.BLACK);
			labelPanel.setLayout(new GridLayout(0,1));
			labelPanel.add(new JLabel("")); // spacer
			labelPanel.add(errorMessageLabel1);
			labelPanel.add(errorMessageLabel2);
			labelPanel.add(new JLabel("")); // spacer
			this.getContentPane().add(labelPanel, BorderLayout.PAGE_START);
			controllerUtil = new PCeNRegistration_ErrorViewControllerUtility(errorMap, batchSelectionMode);
			tableModel = new PCeNRegistration_ErrorTableModel(controllerUtil);
			errorTableView = new PCeNRegistration_ErrorTableView(tableModel, 70, controllerUtil);
			JScrollPane sPane = new JScrollPane(errorTableView);
			errorTableView.setPreferredScrollableViewportSize(new Dimension(CeNConstants.TABLE_VIEW_WIDTH,
					CeNConstants.TABLE_VIEW_ROW_HIGHT * 3));
			this.getContentPane().add(sPane, BorderLayout.CENTER);
			
			JPanel buttonPanel = new JPanel();
			JButton continueButton = new JButton("Continue");
			continueButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					dispose();
				}});
			JButton cancelButton = new JButton("Cancel");
			buttonPanel.add(continueButton);
			if (batchSelectionMode)
				continueButton.setEnabled(false);
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					errorMap = null;
					dispose();
				}});
			buttonPanel.add(cancelButton);
			this.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
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
		this.setSize(960, 300);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = this.getPreferredSize();
		this.setLocation(((screenSize.width - 350) - dialogSize.width) / 2, (screenSize.height - dialogSize.height) / 2);
		this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
				errorMap = null;
				dispose();               
            }
        });

	}
	/**
	 * @param args
	 */
/*	public static void main(String[] args) {
		LinkedHashMap hashMap = new LinkedHashMap();
		hashMap.put(new ProductBatchModel(), "Error Message");
		RegistrationErrorHandlerDialog handler = new RegistrationErrorHandlerDialog(hashMap, batchSelectionMode);
		handler.show();
	}*/
	
	public void dispose() {
		super.dispose();
/*		this.setVisible(false);
		this.controllerUtil = null;
		this.tableModel = null;
		this.errorTableView = null;
		this.removeAll();
		//super.dispose();
		*/
	}

	public LinkedHashMap getErrorMap() {
		return errorMap;
	}	
}
