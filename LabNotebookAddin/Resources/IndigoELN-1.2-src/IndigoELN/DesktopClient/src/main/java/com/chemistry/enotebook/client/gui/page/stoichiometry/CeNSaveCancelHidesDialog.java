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
package com.chemistry.enotebook.client.gui.page.stoichiometry;

import com.chemistry.enotebook.client.gui.CeNInternalFrame;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * 
 *
 */
public class CeNSaveCancelHidesDialog extends CeNInternalFrame {

	private static final long serialVersionUID = -1109664068942618429L;
	
	//	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private static int CANCELLED = -1;
	private static int SAVE = 1;
	private int status = CANCELLED;
	private Object selected = null;
	protected JButton jButtonCancel;
	protected JButton jButtonSave;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog() throws HeadlessException {
		super();
		init();
	}

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Dialog arg0) throws HeadlessException {
		// super(arg0);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Dialog arg0, boolean arg1) throws HeadlessException {
		// super(arg0, arg1);
		init();
	}

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Frame arg0) throws HeadlessException {
		// super(arg0);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Frame arg0, boolean arg1) throws HeadlessException {
		// super(arg0, arg1);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Dialog arg0, String arg1) throws HeadlessException {
		// super(arg0, arg1);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Dialog arg0, String arg1, boolean arg2) throws HeadlessException {
		// super(arg0, arg1, arg2);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Frame arg0, String arg1) throws HeadlessException {
		// super(arg0, arg1);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Frame arg0, String arg1, boolean arg2) throws HeadlessException {
		// super(arg0, arg1, arg2);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @throws java.awt.HeadlessException
	 */
	public CeNSaveCancelHidesDialog(Dialog arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) throws HeadlessException {
		// super(arg0, arg1, arg2, arg3);
		init();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public CeNSaveCancelHidesDialog(Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		// super(arg0, arg1, arg2, arg3);
		init();
	}

	private void init() {
		// Setup the default buttons for the dialog
		JPanel saveCancelPanel = new JPanel();
		saveCancelPanel.setPreferredSize(new java.awt.Dimension(649, 35));
		this.getContentPane().add(saveCancelPanel);
		jButtonSave = new JButton("Save");
		CeNGUIUtils.styleComponentText(Font.BOLD, jButtonSave);
		jButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSaveActionPerformed(evt);
			}
		});
		saveCancelPanel.add(jButtonSave);
		JPanel jPanelFiller = new JPanel();
		jPanelFiller.setPreferredSize(new java.awt.Dimension(51, 10));
		saveCancelPanel.add(jPanelFiller);
		jButtonCancel = new JButton("Cancel");
		CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);
		jButtonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonCancelActionPerformed(evt);
			}
		});
		saveCancelPanel.add(jButtonCancel);
		getContentPane().add(saveCancelPanel, BorderLayout.SOUTH);
	}

	/**
	 * Use to tell when the user cancelled the action or intended to save the results.
	 * 
	 * @return SAVE when success, or CANCELLED for pressing Cancel button
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Hides the dialog so the results may be transferred back to the calling function.
	 * 
	 * Calling Entity needs to call dispose() on this object when finished.
	 * 
	 * @param evt =
	 *            not used.
	 */
	protected void jButtonSaveActionPerformed(ActionEvent evt) {
		// Take currently selected batch and use copy to get the values back
		status = SAVE;
		setVisible(false);
		// Calling entity needs to dispose of this object
	}

	/**
	 * Hides the dialog so the results may be transferred back to the calling function.
	 * 
	 * Calling Entity needs to call dispose() on this object when finished.
	 * 
	 * @param evt =
	 *            not used.
	 */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		// No change to current batch.
		status = CANCELLED;
		setVisible(false);
		// Calling entity needs to dispose of this object
	}

	public Object getSelectedObject() {
		return selected;
	}

	public void setSelectedObject(Object obj) {
		selected = obj;
	}
}
