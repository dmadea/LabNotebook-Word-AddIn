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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.errorhandler.MsgBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.utils.CeNDialog;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo is being used commercially
 * (ie, by a for-profit company or business) then you should purchase a license
 * - please visit www.cloudgarden.com for details.
 */
public class AboutDialog extends CeNDialog {

	private static final long serialVersionUID = 7459275413264192788L;

	private JLabel jLabelCeNpiccy;
	private JEditorPane jEditorPane1;
	private JButton jButtonOk;
	private JButton jButtonTeam;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JLabel jLabelVersion;
	private JPanel jPanel2;
	private JPanel jPanel1;
	private JLabel jLabelSystem;
	private JLabel jLabelSysLbl;

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public AboutDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	public AboutDialog() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will
	 * disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			
			jPanel1 = new JPanel() {

				private static final long serialVersionUID = -5456408240551364416L;
				
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Image logo = CenIconFactory.getCompanyLogo();
					g.drawImage(logo, this.getWidth() - logo.getWidth(this) - 5, 5, this);
				}				
			};
			
			jLabelCeNpiccy = new JLabel();
			jEditorPane1 = new JEditorPane();
			jLabelVersion = new JLabel();
			jLabel6 = new JLabel();
			jLabel7 = new JLabel();
			jPanel2 = new JPanel();
			jButtonTeam = new JButton();
			jButtonOk = new JButton();
			jLabelSystem = new JLabel();
			jLabelSysLbl = new JLabel();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("About " + CeNConstants.PROGRAM_NAME);
			this.setResizable(false);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(542, 236));
			AnchorLayout jPanel1Layout = new AnchorLayout();
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setVisible(true);
			jPanel1.setBackground(new java.awt.Color(122, 194, 174));
			jPanel1.setPreferredSize(new java.awt.Dimension(435, 174));
			jPanel1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
					null, null));
			this.getContentPane().add(jPanel1, BorderLayout.CENTER);
			jLabelCeNpiccy.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelCeNpiccy.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelCeNpiccy.setIconTextGap(0);
			jLabelCeNpiccy.setVerticalAlignment(SwingConstants.CENTER);
			jLabelCeNpiccy.setVerticalTextPosition(SwingConstants.CENTER);
			jLabelCeNpiccy.setVisible(true);
			jLabelCeNpiccy.setBackground(new java.awt.Color(255, 255, 255));
			jLabelCeNpiccy.setPreferredSize(new java.awt.Dimension(117, 131));
			jLabelCeNpiccy.setBorder(new TitledBorder(null, "",
					TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
							"MS Sans Serif", 0, 11),
					new java.awt.Color(0, 0, 0)));
			jPanel1.add(jLabelCeNpiccy, new AnchorConstraint(83, 255, 836, 35,
					1, 1, 1, 1));
			jEditorPane1
					.setText("Provided to you by " + CeNConstants.PROGRAM_NAME + " team.");
			jEditorPane1.setEditable(false);
			jEditorPane1.setVisible(true);
			jEditorPane1.setBackground(new java.awt.Color(122, 194, 174));
			jEditorPane1.setPreferredSize(new java.awt.Dimension(276, 51));
			jEditorPane1.setFocusable(false);
			jPanel1.add(jEditorPane1, new AnchorConstraint(440, 966, 630, 302,
					1, 1, 1, 1));
			jLabelVersion.setText("-not set-");
			jLabelVersion.setVisible(true);
			jLabelVersion.setFont(new java.awt.Font("Arial", 0, 12));
			jLabelVersion.setPreferredSize(new java.awt.Dimension(213, 18));
			jPanel1.add(jLabelVersion, new AnchorConstraint(204, 989, 307, 470,
					1, 1, 1, 1));
			jLabel6.setText(CeNConstants.PROGRAM_NAME);
			jLabel6.setVisible(true);
			jLabel6.setFont(new java.awt.Font("Arial Black", 0, 14));
			jLabel6.setPreferredSize(new java.awt.Dimension(287, 19));
			jPanel1.add(jLabel6, new AnchorConstraint(75, 1003, 151, 295, 1, 1,
					1, 1));
			jLabel7.setText("Version:");
			jLabel7.setVisible(true);
			jLabel7.setFont(new java.awt.Font("Arial", 0, 12));
			jLabel7.setPreferredSize(new java.awt.Dimension(57, 18));
			jPanel1.add(jLabel7, new AnchorConstraint(205, 497, 306, 336, 1, 1,
					1, 1));
			jLabelSysLbl.setText("System:");
			jLabelSysLbl.setVisible(true);
			jLabelSysLbl.setFont(new java.awt.Font("Arial", 0, 12));
			jLabelSysLbl.setPreferredSize(new java.awt.Dimension(57, 18));
			jPanel1.add(jLabelSysLbl, new AnchorConstraint(285, 497, 386, 336,
					1, 1, 1, 1));
			jLabelSystem.setText("-not set-");
			jLabelSystem.setVisible(true);
			jLabelSystem.setFont(new java.awt.Font("Arial", 0, 12));
			jLabelSystem.setPreferredSize(new java.awt.Dimension(213, 18));
			jPanel1.add(jLabelSystem, new AnchorConstraint(284, 989, 387, 470,
					1, 1, 1, 1));
			FlowLayout jPanel2Layout = new FlowLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setAlignment(FlowLayout.CENTER);
			jPanel2Layout.setHgap(50);
			jPanel2Layout.setVgap(6);
			jPanel2.setPreferredSize(new java.awt.Dimension(435, 35));
			jPanel2.setSize(new java.awt.Dimension(435, 35));
			this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
			jButtonTeam.setText("The Team");
			jButtonTeam.setVisible(false);
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonTeam);
			jButtonTeam.setPreferredSize(new java.awt.Dimension(90, 25));
			jButtonTeam.setSize(new java.awt.Dimension(90, 25));
			jPanel2.add(jButtonTeam);
			jButtonTeam.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonTeamActionPerformed(evt);
				}
			});
			jButtonOk.setText("Ok");
			jButtonOk.setVisible(true);
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonOk);
			jButtonOk.setPreferredSize(new java.awt.Dimension(63, 25));
			jButtonOk.setSize(new java.awt.Dimension(63, 25));
			jPanel2.add(jButtonOk);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
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
		try {
			jLabelSystem.setText(CeNSystemProperties.getRunMode());
		} catch (Exception e) { /* Ignored */
		}
		jLabelVersion.setText(MasterController.getVersionInfoAsString());
		jLabelCeNpiccy.setIcon(CenIconFactory
				.getImageIcon(CenIconFactory.General.ABOUT));
		jButtonOk.setDefaultCapable(true);
		this.getRootPane().setDefaultButton(jButtonOk);
		this.addWindowListener(new ResetButWinListener(jButtonOk, this));
		this.setDefaultCloseOperation(AboutDialog.DISPOSE_ON_CLOSE);
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * This static method creates a new instance of this class and shows it
	 * inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and
	 * used as a basis for your own code. * It is auto-generated code - the body
	 * of this method will be re-generated after any changes are made to the
	 * GUI. However, if you delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			AboutDialog inst = new AboutDialog();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt) {
		this.setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void jButtonTeamActionPerformed(ActionEvent evt) {
		MsgBox.showMsgBox(this, "CeN Team", "CeN Team");
	}
}
