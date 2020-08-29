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
package com.chemistry.enotebook.client.gui.esig;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
@SuppressWarnings("serial")
public class SignatureTemplateDialog 
	extends javax.swing.JDialog 
{
	private JLabel jLabelMsg;
	private JComboBox jComboBoxTemplates;
	private JButton jButtonTip;
	private JButton jButtonCancel;
	private JButton jButtonOK;
	private boolean cancelled = false;
	private SignatureHandler sigHandler = null;

	public SignatureTemplateDialog(JFrame frame, SignatureHandler sigHandler) {
		super(frame);
		this.sigHandler = sigHandler;
		initGUI();
		setLocation(getParent().getX() + ((getParent().getWidth() - getWidth()) / 2), getParent().getY()
				+ ((getParent().getHeight() - getHeight()) / 2));
	}

	private void initGUI() {
		try {
			{
				jLabelMsg = new JLabel();
				getContentPane().add(jLabelMsg);
				jLabelMsg.setText("Please select the Template to use for this Submission:");
				jLabelMsg.setBounds(7, 7, 329, 28);
				jLabelMsg.setFont(new java.awt.Font("Arial", 1, 12));
			}
			{
				ComboBoxModel jComboBoxTemplatesModel = new DefaultComboBoxModel();
				jComboBoxTemplates = new JComboBox();
				getContentPane().add(jComboBoxTemplates);
				jComboBoxTemplates.setModel(jComboBoxTemplatesModel);
				jComboBoxTemplates.setBounds(28, 35, 308, 21);
			}
			{
				jButtonOK = new JButton();
				getContentPane().add(jButtonOK);
				jButtonOK.setText("OK");
				jButtonOK.setBounds(84, 70, 63, 28);
				jButtonOK.setFont(new java.awt.Font("Arial", 1, 11));
				jButtonOK.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						cancelled = false;
						setVisible(false); 
					}
				});
			}
			{
				jButtonCancel = new JButton();
				getContentPane().add(jButtonCancel);
				jButtonCancel.setText("Cancel");
				jButtonCancel.setBounds(182, 70, 77, 28);
				jButtonCancel.setFont(new java.awt.Font("Arial", 1, 11));
				jButtonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						cancelled = true;
						setVisible(false);
					}
				});
			}
			{
				jButtonTip = new JButton();
				getContentPane().add(jButtonTip);
				jButtonTip.setText("<html><span style='font-size:12.0pt;font-family:Times New Roman'><b>Note:</b>  A Signature Template defines the list of collegues who must sign/witness this submission.  There must be at least one template defined in Signature Service that contains two signature blocks where one is named 'Author' to continue.  If the desired Template is not listed please click  <font color='blue'><u>here</u></font>  to go to Signature Service to create/define a Signature Template.<html>");
				jButtonTip.setBounds(7, 112, 336, 140);
				jButtonTip.setVerticalAlignment(SwingConstants.TOP);
				jButtonTip.setVerticalTextPosition(SwingConstants.TOP);
				jButtonTip.setFont(new java.awt.Font("Times New Roman", 0, 12));
				jButtonTip.setBorderPainted(false);
				jButtonTip.setFocusable(false);
				jButtonTip.setFocusTraversalKeysEnabled(false);
				jButtonTip.setFocusPainted(false);
				jButtonTip.setRequestFocusEnabled(false);
			}
			jButtonTip.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					sigHandler.launchSAFESign();
				}
			});
			getContentPane().setLayout(null);
			{
				this.setTitle("Signature Template Selection Dialog");
				this.setFocusCycleRoot(false);
				this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				this.setModal(true);
				this.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						cancelled = true;
						setVisible(false);
					}
				});
			}
			this.setSize(358, 280);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), e);
		}
	}

	public TemplateVO getSelectedTemplate() {
		if (cancelled) {
			return null;
		} else {
			TemplateVO value = (TemplateVO) jComboBoxTemplates.getSelectedItem();
			
			try {
				MasterController.getUser().setPreference(NotebookUser.PREF_ESIG_LAST_TEMPLATE, value.getTemplateId());
			} catch (UserPreferenceException e) {
			}
			
			return value;
		}
	}

	public void setAvailableTemplates(TemplateVO[] list) {
		String defaultTemplate = "";
		
		try { 
			defaultTemplate = MasterController.getUser().getPreference(NotebookUser.PREF_ESIG_LAST_TEMPLATE); 
		} catch (Exception e) {	
			// do nothing
		}
		
		jComboBoxTemplates.removeAllItems();
		
		if (list != null) {
			for (TemplateVO vo : list) {
				jComboBoxTemplates.addItem(vo);
			}
			
			if (StringUtils.isNotBlank(defaultTemplate)) {
				for (TemplateVO vo : list) {
					if (StringUtils.equals(vo.getTemplateId(), defaultTemplate)) {
						jComboBoxTemplates.setSelectedItem(vo);
						break;
					}
				}
			}
		}
	}
}
