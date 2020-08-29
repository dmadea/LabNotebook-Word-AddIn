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
package com.chemistry.enotebook.client.gui.preferences;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.CompoundMgmtDataSourceSetupPanel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.utils.CeNDialog;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import org.jdom.JDOMException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If
 * Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a
 * license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class JDialogPrefs extends CeNDialog {
	
	private static final long serialVersionUID = 4986926528657870922L;
	
	private CompoundMgmtDataSourceSetupPanel structureDBListPanel;
	public static final int GENERAL_PREFS_TAB = 0;
	public static final int REGISTRATION_PREFS_TAB = 1;
	public static final int STOICH_PREFS_TAB = 2;
	public static final int VNV_PREFS_TAB = 3;
	public static final int STRUCT_SEARCH_PREFS_TAB = 4;
	public static final int MAX_TAB = 5;
	private JTextField jTextFieldTime;
	private JLabel jLabel5;
	private JLabel jLabel4;
	private JPanel jPanel4;
	private JPanel jPanelSouth;
	private JPanel jPanelNorth;
	private JPanel jPanelEast;
	private JPanel jPanelWest;
	private JPanel jPanelCentre;
	private JCheckBox jCheckBoxRequireParentMW;
	private JCheckBox jCheckBoxCheckParentMF;
	private JCheckBox jCheckBoxAutoVnV;
	private ButtonGroup buttonGroupDefaultNav;
	private ButtonGroup buttonGroupStrucEditor;
	private JRadioButton jRadioButtonAllSites;
	private JRadioButton jRadioButtonMyBookmarks;
	private JLabel jLabel3;
	private JPanel jPanel9;
	private JRadioButton jRadioButtonMDLISIS;
	private JRadioButton jRadioButtonKetcher;
	private JLabel jLabel2;
	private JPanel jPanel6;
	private ButtonGroup buttonGroupDefaultDisplay;
	private JLabel jLabel1;
	private JRadioButton jRadioButtonMostRecentExp;
	private JRadioButton jRadioButtonWelcomeMess;
	private JPanel jPanel3;
	private JCheckBox jCheckBoxSysHealthCheck;
	private JPanel jPanelStructureVnVPrefs;
	private JLabel jLabel8;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JPanel jPanel7;
	private JPanel jPanel5;
	private JPanel jPanelStoichPrefs;
	private JPanel jPanelPageTemplatePrefs;
	private JPanel jPanelLogInSetupPrefs;
	private JTabbedPane jTabbedPaneMain;
	private JPanel jPanel2;
	private JPanel jPanel1;
	private JButton jButtonCancel;
	private JCheckBox jCheckBoxMem;
	private JCheckBox jCheckBoxOfl;	
	private JCheckBox jCheckBoxeSig;
	private JCheckBox jCheckBoxeSigLaunchUrl;
	private JButton jButtonOk;

	private TaAndProjCodePrefsPanel taAndProjCodePrefsPanel;

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public JDialogPrefs(Frame arg0, int initialTab) throws HeadlessException {
		super(arg0);
		initGUI();
		if (initialTab >= 0 && initialTab < MAX_TAB)
			jTabbedPaneMain.setSelectedIndex(initialTab);
	}

	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public JDialogPrefs(Frame arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	public JDialogPrefs() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			buttonGroupDefaultDisplay = new ButtonGroup();
			buttonGroupStrucEditor = new ButtonGroup();
			buttonGroupDefaultNav = new ButtonGroup();
			jPanel1 = new JPanel();
			jButtonOk = new JButton();
			jButtonCancel = new JButton();
			jPanel2 = new JPanel();
			jTabbedPaneMain = new JTabbedPane();
			jPanelPageTemplatePrefs = new JPanel();
			jPanelStoichPrefs = new JPanel(); // new StoicPrefPanel();
			jPanelStructureVnVPrefs = new JPanel();
			jPanelCentre = new JPanel();
			jCheckBoxRequireParentMW = new JCheckBox();
			jCheckBoxCheckParentMF = new JCheckBox();
			jCheckBoxAutoVnV = new JCheckBox();
			jPanelWest = new JPanel();
			jPanelEast = new JPanel();
			jPanelNorth = new JPanel();
			jPanelSouth = new JPanel();
			structureDBListPanel = new CompoundMgmtDataSourceSetupPanel();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setTitle("Preferences");
			this.setModal(true);
			this.setFocusCycleRoot(true);
			this.setFocusableWindowState(true);
			this.setSize(576, 374);
			this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
			jButtonOk.setText("OK");
			jButtonOk.setFont(new java.awt.Font("sansserif", 1, 12));
			jButtonOk.setPreferredSize(new java.awt.Dimension(101, 26));
			jPanel1.add(jButtonOk);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 12));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(98, 26));
			jPanel1.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			BorderLayout jPanel2Layout = new BorderLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setHgap(0);
			jPanel2Layout.setVgap(0);
			this.getContentPane().add(jPanel2, BorderLayout.CENTER);
			jTabbedPaneMain.setTabLayoutPolicy(0);
			jTabbedPaneMain.setTabPlacement(1);
			jTabbedPaneMain.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanel2.add(jTabbedPaneMain, BorderLayout.CENTER);
			{
				jPanelLogInSetupPrefs = new JPanel();
				jTabbedPaneMain.addTab("Setup Preferences", null, jPanelLogInSetupPrefs, null);
				AnchorLayout jPanelLogInSetupPrefsLayout = new AnchorLayout();
				jPanelLogInSetupPrefs.setLayout(jPanelLogInSetupPrefsLayout);
				jPanelLogInSetupPrefs.setPreferredSize(new java.awt.Dimension(650, 269));
				jPanelLogInSetupPrefs.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
				{
					jPanel7 = new JPanel();
					jPanel7.setLayout(null);
					jPanelLogInSetupPrefs.add(jPanel7, new AnchorConstraint(689, 970, 959, 523, AnchorConstraint.ANCHOR_REL,
							AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
					jPanel7.setPreferredSize(new java.awt.Dimension(252, 77));
					jPanel7.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					{
						jCheckBoxMem = new JCheckBox();
						jPanel7.add(jCheckBoxMem);
						jCheckBoxMem.setText("View Application Memory Statistics");
						jCheckBoxMem.setBounds(7, 7, 220, 21);
						
						jCheckBoxOfl = new JCheckBox();
						jPanel7.add(jCheckBoxOfl);
						jCheckBoxOfl.setText("Enable Off-line Report");
						jCheckBoxOfl.setBounds(7, 30, 196, 21);
					}
				}
				{
					jPanel5 = new JPanel();
					jPanel5.setLayout(null);
					jPanelLogInSetupPrefs.add(jPanel5, new AnchorConstraint(689, 466, 959, 18, AnchorConstraint.ANCHOR_REL,
							AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
					jPanel5.setPreferredSize(new java.awt.Dimension(252, 77));
					jPanel5.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					{
						jLabel7 = new JLabel();
						jPanel5.add(jLabel7);
						jLabel7.setText("  Electronic Signatures:");
						jLabel7.setFont(new java.awt.Font("sansserif", 1, 11));
						jLabel7.setPreferredSize(new java.awt.Dimension(224, 15));
						jLabel7.setBounds(2, 2, 126, 15);
						{
							jLabel8 = new JLabel();
							jLabel7.add(jLabel8);
							jLabel8.setText("  After log-in my default navigation tree is:");
							jLabel8.setFont(new java.awt.Font("sansserif", 1, 11));
							jLabel8.setPreferredSize(new java.awt.Dimension(224, 15));
						}
					}
					{
						jCheckBoxeSig = new JCheckBox();
						jPanel5.add(jCheckBoxeSig);
						jCheckBoxeSig.setText("Use eSig on Experiment Completion");
						jCheckBoxeSig.setBounds(7, 21, 196, 21);
						jCheckBoxeSigLaunchUrl = new JCheckBox();
						jPanel5.add(jCheckBoxeSigLaunchUrl);
						jCheckBoxeSigLaunchUrl.setText("Launch Author eSig Window After Submission");
						jCheckBoxeSigLaunchUrl.setBounds(7, 42, 236, 21);
					}
				}
				{
					jPanel3 = new JPanel();
					jPanelLogInSetupPrefs.add(jPanel3, new AnchorConstraint(78, 466, 349, 18, AnchorConstraint.ANCHOR_REL,
							AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
					jPanel3.setLayout(null);
					jPanel3.setVisible(true);
					jPanel3.setPreferredSize(new java.awt.Dimension(252, 77));
					jPanel3.setAlignmentY(0.5f);
					jPanel3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
					{
						jLabel1 = new JLabel();
						jPanel3.add(jLabel1);
						jLabel1.setText(" After log-in my default display screen is:");
						jLabel1.setFont(new java.awt.Font("sansserif", 1, 11));
						jLabel1.setPreferredSize(new java.awt.Dimension(67, 15));
						jLabel1.setBounds(2, 2, 226, 15);
					}
					{
						jRadioButtonWelcomeMess = new JRadioButton();
						jPanel3.add(jRadioButtonWelcomeMess);
						jRadioButtonWelcomeMess.setEnabled(true);
						jRadioButtonWelcomeMess.setText("Welcome Message");
						jRadioButtonWelcomeMess.setFont(new java.awt.Font("sansserif", 0, 12));
						jRadioButtonWelcomeMess.setFocusable(false);
						buttonGroupDefaultDisplay.add(jRadioButtonWelcomeMess);
						jRadioButtonWelcomeMess.setBounds(7, 17, 131, 25);
					}
					{
						jRadioButtonMostRecentExp = new JRadioButton();
						jPanel3.add(jRadioButtonMostRecentExp);
						jRadioButtonMostRecentExp.setText("Last Experiment Modified");
						jRadioButtonMostRecentExp.setFont(new java.awt.Font("sansserif", 0, 12));
						jRadioButtonMostRecentExp.setFocusable(false);
						buttonGroupDefaultDisplay.add(jRadioButtonMostRecentExp);
						jRadioButtonMostRecentExp.setBounds(7, 42, 163, 25);
					}
				}
				{
					jCheckBoxSysHealthCheck = new JCheckBox();
					jPanelLogInSetupPrefs.add(jCheckBoxSysHealthCheck, new AnchorConstraint(79, 434, 161, 62, 1, 1, 1, 1));
					jCheckBoxSysHealthCheck.setText("Run System Health Check at log-in");
					jCheckBoxSysHealthCheck.setSelected(false);
					jCheckBoxSysHealthCheck.setVisible(false);
					jCheckBoxSysHealthCheck.setFont(new java.awt.Font("sansserif", 0, 12));
					jCheckBoxSysHealthCheck.setPreferredSize(new java.awt.Dimension(242, 22));
					jCheckBoxSysHealthCheck.setBounds(new java.awt.Rectangle(40, 21, 242, 22));
					jCheckBoxSysHealthCheck.setFocusable(false);
				}
				{
					jPanel6 = new JPanel();
					jPanelLogInSetupPrefs.add(jPanel6, new AnchorConstraint(75, 970, 345, 523, AnchorConstraint.ANCHOR_REL,
							AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
					jPanel6.setLayout(null);
					jPanel6.setVisible(true);
					jPanel6.setPreferredSize(new java.awt.Dimension(252, 77));
					jPanel6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
					{
						jLabel2 = new JLabel();
						jPanel6.add(jLabel2);
						jLabel2.setText(" My default Structure / Reaction Editor is:");
						jLabel2.setFont(new java.awt.Font("sansserif", 1, 11));
						jLabel2.setPreferredSize(new java.awt.Dimension(67, 15));
						jLabel2.setBounds(2, 2, 222, 15);
					}
					{
						jRadioButtonKetcher = new JRadioButton();
						jPanel6.add(jRadioButtonKetcher);
						jRadioButtonKetcher.setEnabled(true);
						jRadioButtonKetcher.setText("Ketcher");
						jRadioButtonKetcher.setFont(new java.awt.Font("sansserif", 0, 12));
						jRadioButtonKetcher.setFocusable(false);
						buttonGroupStrucEditor.add(jRadioButtonKetcher);
						jRadioButtonKetcher.setBounds(7, 17, 109, 25);
					}
					{
						jRadioButtonMDLISIS = new JRadioButton();
						jPanel6.add(jRadioButtonMDLISIS);
						jRadioButtonMDLISIS.setText("MDL ISIS/Draw");
						jRadioButtonMDLISIS.setFont(new java.awt.Font("sansserif", 0, 12));
						jRadioButtonMDLISIS.setFocusable(false);
						buttonGroupStrucEditor.add(jRadioButtonMDLISIS);
						jRadioButtonMDLISIS.setBounds(7, 42, 107, 25);
					}
				}
				{
					jPanel9 = new JPanel();
					jPanelLogInSetupPrefs.add(jPanel9, new AnchorConstraint(394, 970, 664, 523, AnchorConstraint.ANCHOR_REL,
							AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
					jPanel9.setLayout(null);
					jPanel9.setVisible(true);
					jPanel9.setPreferredSize(new java.awt.Dimension(252, 77));
					jPanel9.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
					{
						jLabel3 = new JLabel();
						jPanel9.add(jLabel3);
						jLabel3.setText("  Auto-Recover Information:");
						jLabel3.setFont(new java.awt.Font("sansserif", 1, 11));
						jLabel3.setPreferredSize(new java.awt.Dimension(250, 15));
						jLabel3.setBounds(new java.awt.Rectangle(7, 2, 200, 15));
					}
					{
						jLabel5 = new JLabel();
						jPanel9.add(jLabel5);
						jLabel5.setLayout(null);
						jLabel5.setText("Minute Intervals");
						jLabel5.setVisible(true);
						jLabel5.setPreferredSize(new java.awt.Dimension(100, 15));
						jLabel5.setLocation(new java.awt.Point(50, 0));
						jLabel5.setBounds(new java.awt.Rectangle(48, 25, 100, 15));
					}
					{
						jTextFieldTime = new JTextField();
						jPanel9.add(jTextFieldTime);
						jTextFieldTime.setMargin(new Insets(1, 4, 1, 1));
						jTextFieldTime.setPreferredSize(new java.awt.Dimension(27, 20));
						jTextFieldTime.setBounds(new java.awt.Rectangle(16, 23, 27, 20));
					}
				}
				{
					jPanel4 = new JPanel();
					jPanelLogInSetupPrefs.add(jPanel4, new AnchorConstraint(391, 466, 661, 18, AnchorConstraint.ANCHOR_REL,
							AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
					jPanel4.setLayout(null);
					jPanel4.setVisible(true);
					jPanel4.setPreferredSize(new java.awt.Dimension(252, 77));
					jPanel4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
					{
						jLabel4 = new JLabel();
						jPanel4.add(jLabel4);
						jLabel4.setText("  After log-in my default navigation tree is:");
						jLabel4.setFont(new java.awt.Font("sansserif", 1, 11));
						jLabel4.setPreferredSize(new java.awt.Dimension(224, 15));
						jLabel4.setBounds(2, 2, 230, 15);
						{
							jLabel6 = new JLabel();
							jLabel4.add(jLabel6);
							jLabel6.setText("  After log-in my default navigation tree is:");
							jLabel6.setFont(new java.awt.Font("sansserif", 1, 11));
							jLabel6.setPreferredSize(new java.awt.Dimension(224, 15));
						}
					}
					{
						jRadioButtonMyBookmarks = new JRadioButton();
						jPanel4.add(jRadioButtonMyBookmarks);
						jRadioButtonMyBookmarks.setText("My Bookmarks");
						jRadioButtonMyBookmarks.setIconTextGap(4);
						jRadioButtonMyBookmarks.setFont(new java.awt.Font("sansserif", 0, 12));
						jRadioButtonMyBookmarks.setFocusable(false);
						buttonGroupDefaultNav.add(jRadioButtonMyBookmarks);
						jRadioButtonMyBookmarks.setBounds(7, 17, 105, 25);
					}
					{
						jRadioButtonAllSites = new JRadioButton();
						jPanel4.add(jRadioButtonAllSites);
						jRadioButtonAllSites.setText("All Sites (ROOT)");
						jRadioButtonAllSites.setSelected(false);
						jRadioButtonAllSites.setFont(new java.awt.Font("sansserif", 0, 12));
						jRadioButtonAllSites.setFocusable(false);
						buttonGroupDefaultNav.add(jRadioButtonAllSites);
						jRadioButtonAllSites.setBounds(7, 42, 153, 25);
					}
				}
			}
			jTabbedPaneMain.setTitleAt(0, "Log-in Setup Preferences");
			jRadioButtonMostRecentExp.setSelected(true);
			jTextFieldTime.setText("5");
			jRadioButtonMyBookmarks.setSelected(true);
			jPanelPageTemplatePrefs.setPreferredSize(new java.awt.Dimension(650, 285));
			jTabbedPaneMain.addTab("Page Template Submission Screens", null, jPanelPageTemplatePrefs, null);
			jPanelStoichPrefs.setPreferredSize(new java.awt.Dimension(650, 260));
			jTabbedPaneMain.addTab("Stoichiometry Table Setups", null, jPanelStoichPrefs, null);
			BorderLayout jPanelStructureVnVPrefsLayout = new BorderLayout();
			jPanelStructureVnVPrefs.setLayout(jPanelStructureVnVPrefsLayout);
			jPanelStructureVnVPrefsLayout.setHgap(0);
			jPanelStructureVnVPrefsLayout.setVgap(0);
			jPanelStructureVnVPrefs.setPreferredSize(new java.awt.Dimension(650, 285));
			jTabbedPaneMain.addTab("Structure VnV Default Setups", null, jPanelStructureVnVPrefs, null);
			BoxLayout jPanelCentreLayout = new BoxLayout(jPanelCentre, 1);
			jPanelCentre.setLayout(jPanelCentreLayout);
			jPanelStructureVnVPrefs.add(jPanelCentre, BorderLayout.CENTER);
			jCheckBoxRequireParentMW.setText("Require Check Parent MW");
			jCheckBoxRequireParentMW.setVisible(true);
			jPanelCentre.add(jCheckBoxRequireParentMW);
			jCheckBoxCheckParentMF.setText("Require Check Parent MF");
			jCheckBoxCheckParentMF.setVisible(true);
			jPanelCentre.add(jCheckBoxCheckParentMF);
			jCheckBoxAutoVnV.setText("Run the Structure VnV automatically");
			jCheckBoxAutoVnV.setVisible(true);
			jPanelCentre.add(jCheckBoxAutoVnV);
			jPanelWest.setPreferredSize(new java.awt.Dimension(135, 265));
			jPanelStructureVnVPrefs.add(jPanelWest, BorderLayout.WEST);
			jPanelEast.setPreferredSize(new java.awt.Dimension(300, 110));
			jPanelStructureVnVPrefs.add(jPanelEast, BorderLayout.EAST);
			jPanelNorth.setPreferredSize(new java.awt.Dimension(650, 36));
			jPanelStructureVnVPrefs.add(jPanelNorth, BorderLayout.NORTH);
			jPanelSouth.setPreferredSize(new java.awt.Dimension(650, 143));
			jPanelStructureVnVPrefs.add(jPanelSouth, BorderLayout.SOUTH);
			if (structureDBListPanel.isConnected()) {
				structureDBListPanel.setOpaque(false);
				jTabbedPaneMain.addTab("Structure Databases", null, structureDBListPanel, null);
			} else
				jTabbedPaneMain.add(new JLabel("Compound Structure Service is unavailable."));
			
			// TA&ProjectCode panel
			
			taAndProjCodePrefsPanel = new TaAndProjCodePrefsPanel();			
			jTabbedPaneMain.addTab("Therapeutic Area & Project Code", taAndProjCodePrefsPanel);
			
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		pack();
		jButtonOk.requestFocusInWindow();
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/**
	 * Add your post-init code in here
	 * 
	 * @throws JDOMException
	 */
	public void postInitGUI() throws JDOMException {
		jTextFieldTime.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
		jTabbedPaneMain.removeTabAt(1);
		jTabbedPaneMain.removeTabAt(2);
		jTabbedPaneMain.removeTabAt(1);
		
		// Temporary disabled "Structure Databases"
		jTabbedPaneMain.setEnabledAt(1, false);
		
		NotebookUser user = MasterController.getUser();
		// TODO: defaults should come from site or global preferences
		String str;
		try {
			str = user.getPreference(NotebookUser.PREF_HEALTH_CHECK);
			jCheckBoxSysHealthCheck.setSelected(str.toUpperCase().equals("YES")); // Default
			// = no
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = user.getPreference(NotebookUser.PREF_SB_OUTLOOK_BAR);
			if (str.toUpperCase().equals(NotebookUser.MY_BOOKMARKS))
				jRadioButtonMyBookmarks.setSelected(true);
			else
				// if (str.toUpperCase().equals("ALL_SITES")) // Default
				jRadioButtonAllSites.setSelected(true);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = user.getPreference(NotebookUser.PREF_DEFAULT_START);
			if (str.toUpperCase().equals(NotebookUser.LAST_EXPERIMENT))
				jRadioButtonMostRecentExp.setSelected(true);
			else
				// if (str.toUpperCase().equals(NotebookUser.WELCOME)) //
				// Default
				jRadioButtonWelcomeMess.setSelected(true);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = user.getPreference(NotebookUser.PREF_DEFAULT_EDITOR);
			if (str.toUpperCase().equals(NotebookUser.ISIS_DRAW)) {
				jRadioButtonKetcher.setSelected(false);
				jRadioButtonMDLISIS.setSelected(true);
			} else {
				jRadioButtonMDLISIS.setSelected(false);
				jRadioButtonKetcher.setSelected(true);
			}
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		try {
			str = user.getPreference(NotebookUser.PREF_AUTOSAVE_INTERVAL);
			if (str == null || str.length() == 0)
				str = "5";
			jTextFieldTime.setText(str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			jCheckBoxeSig.setSelected(user.isEsigEnabled());
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}

		try {
			jCheckBoxeSigLaunchUrl.setSelected(user.isEsigLaunchUrl());   
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		
		try {
			str = user.getPreference(NotebookUser.PREF_ENABLE_OFFLINE_REPORT);
			if (str == null || str.length() == 0)
				str = "NO"; // Default = no
			jCheckBoxOfl.setSelected(str.toUpperCase().equals("YES"));
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			List l_selectedDBs = user.getPreferencesList(NotebookUser.PREF_STRUCTURE_DATABASES, NotebookUser.PREF_STRUCTURE_DATABASES_CHILD);
			String s = user.getPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES);
			boolean cenSelected = Boolean.parseBoolean(s);
			
			Iterator iter = l_selectedDBs.iterator();
			// all CSS2 databases have "DB" in their ID, if we find one that doesn't
			// it must be from CSS1 and therefore invalid
			boolean needToClear = false;
			while (iter.hasNext()) {
				if (((String) iter.next()).indexOf("DB") == -1) {
					needToClear = true;
					break;
				}
			}
			if (needToClear == true) {
				l_selectedDBs.clear();
			}
			structureDBListPanel.setDefaultSelectedDBList(l_selectedDBs, cenSelected);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		
		// Fill TA and ProjCode
		
		try {
			String useTA = user.getPreference(NotebookUser.PREF_UseTA);
			String TA = user.getPreference(NotebookUser.PREF_TA);
			String PC = user.getPreference(NotebookUser.PREF_PC);
			
			taAndProjCodePrefsPanel.setUseTA(Boolean.parseBoolean(useTA));
			taAndProjCodePrefsPanel.setTaCode(TA);
			taAndProjCodePrefsPanel.setProjectCode(PC);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		
		Thread.yield();
	}

	protected void processWindowEvent(WindowEvent evt) {
		super.processWindowEvent(evt);
		if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
			this.setVisible(false);
		}
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
			JDialogPrefs inst = new JDialogPrefs();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt) {
		NotebookUser user = MasterController.getUser();
		String str;
		try {
			str = jCheckBoxSysHealthCheck.isSelected() ? "YES" : "NO";
			user.setPreference(NotebookUser.PREF_HEALTH_CHECK, str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jRadioButtonMyBookmarks.isSelected() ? "MY_BOOKMARKS" : "ALL_SITES";
			user.setPreference(NotebookUser.PREF_SB_OUTLOOK_BAR, str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jRadioButtonWelcomeMess.isSelected() ? "WELCOME" : "LAST_EXPERIMENT";
			user.setPreference(NotebookUser.PREF_DEFAULT_START, str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jRadioButtonKetcher.isSelected() ? NotebookUser.KETCHER : NotebookUser.ISIS_DRAW;
			user.setPreference(NotebookUser.PREF_DEFAULT_EDITOR, str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		try {
			str = jTextFieldTime.getText();
			if (new Integer(str).intValue() == 0)
				str = "1";
			user.setPreference(NotebookUser.PREF_AUTOSAVE_INTERVAL, str);
			MasterController.getGuiController().reStartAutoSaveTimer();
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jCheckBoxeSig.isSelected() ? "YES" : "NO";
			user.setPreference(NotebookUser.PREF_ESIG_USE, str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jCheckBoxeSigLaunchUrl.isSelected() ? "YES" : "NO";
			user.setPreference(NotebookUser.PREF_ESIG_LAUNCH_URL, str);
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jCheckBoxMem.isSelected() ? "YES" : "NO";
			user.setPreference(NotebookUser.PREF_VIEW_MEM_STATS, str);
			MasterController.getGUIComponent().updateMemStatsView(jCheckBoxMem.isSelected());
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			str = jCheckBoxOfl.isSelected() ? "YES" : "NO";
			user.setPreference(NotebookUser.PREF_ENABLE_OFFLINE_REPORT, str);
			MasterController.getGUIComponent().updateMemStatsView(jCheckBoxMem.isSelected());
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		try {
			ArrayList al_selectedDBs = structureDBListPanel.getSelectedDBList();
			for (int i = 0; i < al_selectedDBs.size(); i++) {
				String db = (String) al_selectedDBs.get(i);
				if (db.indexOf(" (") > 0) {
					db = db.substring(0, db.indexOf(" ("));
					al_selectedDBs.remove(i);
					al_selectedDBs.add(i, db);
				}
			}
			user.setPreference(NotebookUser.PREF_STRUCTURE_DATABASES, NotebookUser.PREF_STRUCTURE_DATABASES_CHILD, al_selectedDBs);
			if (structureDBListPanel.getCENSelected()) {
				user.setPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES, "true");
			} else {
				user.setPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES, "false");				
			}
		} catch (UserPreferenceException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		
		// Update TA and ProjCode
		try {
			user.setPreference(NotebookUser.PREF_UseTA, Boolean.toString(taAndProjCodePrefsPanel.getUseTA()));
			user.setPreference(NotebookUser.PREF_TA, taAndProjCodePrefsPanel.getTaCode());
			user.setPreference(NotebookUser.PREF_PC, taAndProjCodePrefsPanel.getProjectCode());
			if (taAndProjCodePrefsPanel.getUseTA()) {
				user.setPreference(NotebookUser.PREF_LastTA, taAndProjCodePrefsPanel.getTaCode());
				user.setPreference(NotebookUser.PREF_LastProject, taAndProjCodePrefsPanel.getProjectCode());
			}
		} catch (UserPreferenceException e) {			
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
		
		try {
			user.updateUserPrefs();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		
		this.setVisible(false);
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		this.setVisible(false);
	}

	/** Auto-generated method */
	public CompoundMgmtDataSourceSetupPanel getStructureDBListPanel() {
		return structureDBListPanel;
	}
}
