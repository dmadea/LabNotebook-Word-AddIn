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
package com.chemistry.enotebook.client.gui.page.analytical;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
public class JDialogViewerQuery 
	extends javax.swing.JDialog 
{
	private static final long serialVersionUID = 5965421362464326725L;

	public static final String ACD_LABS = "ACD_LABS";
	public static final String MESTRE_NOVA = "MESTRE_NOVA";
	
	private JPanel jPanelButtons;
	private JPanel jPanelMain;
	private JLabel jLabelMsg2;
	private JButton jButtonACDLabs;
	private JButton jButtonMestreNova;
	private JLabel jLabelMsg;

	private String viewerChoice = null;
	private String viewerType = null;
	
	public JDialogViewerQuery(JFrame frame, String viewerType) {
		super(frame);
		setModal(true);
		this.viewerType = viewerType;
		initGUI();
	}

	private void initGUI() {
		try {
			setTitle(viewerType + " Viewer Selection");
			{
				jPanelButtons = new JPanel();
				getContentPane().add(jPanelButtons, BorderLayout.SOUTH);
				jPanelButtons.setPreferredSize(new java.awt.Dimension(408, 36));
				{
					jButtonMestreNova = new JButton();
					jPanelButtons.add(jButtonMestreNova);
					jButtonMestreNova.setText("MestRe Nova");
					jButtonMestreNova.setFocusable(false);
					jButtonMestreNova.setFont(new java.awt.Font("sansserif", 1, 11));
					jButtonMestreNova.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonMestreNovaActionPerformed(evt);
						}
					});
				}
				{
					jButtonACDLabs = new JButton();
					jPanelButtons.add(jButtonACDLabs);
					jButtonACDLabs.setText("ACD Labs");
					jButtonACDLabs.setFocusable(false);
					jButtonACDLabs.setFont(new java.awt.Font("sansserif", 1, 11));
					jButtonACDLabs.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButtonACDLabsActionPerformed(evt);
						}
					});
				}
			}
			{
				jPanelMain = new JPanel();
				getContentPane().add(jPanelMain, BorderLayout.CENTER);
				{
					jLabelMsg = new JLabel();
					jPanelMain.add(jLabelMsg);
					jLabelMsg.setText("MestReNova software was found on your machine.");
					jLabelMsg.setPreferredSize(new java.awt.Dimension(295, 21));
					jLabelMsg.setFont(new java.awt.Font("sansserif", 1, 11));
				}
				{
					jLabelMsg2 = new JLabel();
					jPanelMain.add(jLabelMsg2);
					jLabelMsg2.setText("Would you like to view this " + viewerType + " using MestReNova or ACD Labs?");
					jLabelMsg2.setFont(new java.awt.Font("sansserif", 1, 11));
				}
			}
			this.setSize(397, 122);
			setLocation(getParent().getX()+(getParent().getWidth()-getWidth())/2, getParent().getY() + (getParent().getHeight() / 2));
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	public String getViewerChoice()
	{
		this.setVisible(true);
		return viewerChoice;
	}
	
	private void jButtonMestreNovaActionPerformed(ActionEvent evt) {
		viewerChoice = MESTRE_NOVA;
		this.setVisible(false);
	}
	
	private void jButtonACDLabsActionPerformed(ActionEvent evt) {
		viewerChoice = ACD_LABS;
		this.setVisible(false);
	}
}
