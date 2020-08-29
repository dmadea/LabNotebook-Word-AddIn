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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail;

import com.chemistry.enotebook.analyticalservice.classes.AnalyticalServiceMetaData;
import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.analytical.DateComboBox;
import com.chemistry.enotebook.client.gui.page.analytical.FileTypesDialog;
import com.chemistry.enotebook.client.gui.page.analytical.InstrumentTypesDialog;
import com.chemistry.enotebook.client.gui.page.analytical.SitesDialog;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalUtility;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.CeNJob;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class AdvancedLinkJDialog extends CeNDialog {

	private static final long serialVersionUID = -375981092111007108L;
	
	private JButton jButtonCancel;
	private JButton jButtonSearch;
//	private JTextField jTextFieldFileNames;
//	private JLabel jLabelFileNames;
	private JTextField jTextFieldExpMethods;
	private JLabel jLabelExpMethods;
	private JButton jButtonFileTypes;
	private static JTextField jTextFieldFileTypes;
	private JLabel jLabelFileTypes;
	private JTextField jTextFieldInsNames;
	private JLabel jLabelInsNames;
	private JTextField jTextFieldUserIds;
	private JLabel jLabelUserIds;
	private JButton jButtonInsTypes;
	private static JTextField jTextFieldInsTypes;
	private JLabel jLabelInstrumentTypes;
	private JTextField jTextFieldGroupIds;
	private JLabel jLabelGroupID;
	private JLabel jLabelTo;
	private JLabel jLabelFrom;
	private JComboBox jComboBoxDateTo;
	private JComboBox jComboBoxDateFrom;
	private JComboBox jComboBoxSearchOptions;
	private JLabel jLabelSearchOptions;
	private JLabel jLabelExpDates;
	private JButton jButtonSites;
	private static JTextField jTextFieldExpSites;
	private JLabel jLabelExperimentSites;
	private JTextField jTextFieldSampleRefs;
	private JLabel jLabelSampleRef;
	//private AnalyticalModel analyticalModel;
	private PAnalyticalUtility analyticalModel;
	private AnalyticalDetailTableView tableViewer;
	private Hashtable<String, String> ht_SitesDescCode = new Hashtable<String, String>();
	private AnalyticalServiceMetaData analyticalServiceServiceMetaData;
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private String queryText = null;

	public AdvancedLinkJDialog(JFrame owner) throws Exception {
		super(owner);
		setModal(true);
		try {
			init();
			initGUI();
		} catch (Exception error) {
			throw error;
		}
	}

	/**
	 * initialize the AnalyticalService Client and get the Sites, FileTypes and instrument Types
	 * 
	 * @throws CeNAnalyticalServiceAccessException
	 * @throws PropertyException
	 * @throws RemoteException
	 * 
	 */
	public void init() throws Exception {
		AnalyticalServiceDelegate analyticalServiceDelegate;
		try {
			analyticalServiceDelegate = new AnalyticalServiceDelegate();
			analyticalServiceServiceMetaData = analyticalServiceDelegate.retrieveAnalyticalServiceMetaData();
			fillHashtable(analyticalServiceServiceMetaData.getSiteDescriptionsToSiteCodeMap());
		} catch (Exception e) {
			processException(e);
			throw e;
		}
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jLabelSampleRef = new JLabel();
			jTextFieldSampleRefs = new JTextField();
			jLabelExperimentSites = new JLabel();
			jTextFieldExpSites = new JTextField();
			jButtonSites = new JButton();
			jLabelExpDates = new JLabel();
			jComboBoxDateFrom = new DateComboBox();
			jComboBoxDateTo = new DateComboBox();
			jLabelFrom = new JLabel();
			jLabelTo = new JLabel();
			jLabelGroupID = new JLabel();
			jTextFieldGroupIds = new JTextField();
			jLabelInstrumentTypes = new JLabel();
			jTextFieldInsTypes = new JTextField();
			jButtonInsTypes = new JButton();
			jLabelUserIds = new JLabel();
			jTextFieldUserIds = new JTextField();
			jLabelInsNames = new JLabel();
			jTextFieldInsNames = new JTextField();
			jLabelFileTypes = new JLabel();
			jTextFieldFileTypes = new JTextField();
			jButtonFileTypes = new JButton();
			jLabelExpMethods = new JLabel();
			jTextFieldExpMethods = new JTextField();
//			jLabelFileNames = new JLabel();
//			jTextFieldFileNames = new JTextField();
			jButtonSearch = new JButton();
			jButtonCancel = new JButton();
			jComboBoxSearchOptions = new JComboBox();
			jLabelSearchOptions = new JLabel();
			setTitle("Advanced Link");
			this.getContentPane().setLayout(null);
			this.setSize(new java.awt.Dimension(830, 300));
			setLocation(getParent().getX() + (getParent().getWidth() - getWidth()) / 2, getParent().getY()
					+ (getParent().getHeight() / 2));
			final JDialog owner = this;
			jLabelSampleRef.setText("Sample References:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelSampleRef);
			jLabelSampleRef.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelSampleRef.setBounds(new java.awt.Rectangle(20, 20, 130, 20));
			this.getContentPane().add(jLabelSampleRef);
			jTextFieldSampleRefs.setToolTipText("expects a comma separated list of sample references");
			jTextFieldSampleRefs.setPreferredSize(new java.awt.Dimension(230, 20));
			jTextFieldSampleRefs.setBounds(new java.awt.Rectangle(165, 20, 230, 20));
			this.getContentPane().add(jTextFieldSampleRefs);
			jLabelSearchOptions.setText("Search Options:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelSearchOptions);
			jLabelSearchOptions.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelSearchOptions.setBounds(new java.awt.Rectangle(415, 20, 100, 20));
			this.getContentPane().add(jLabelSearchOptions);
			jComboBoxSearchOptions.setPreferredSize(new java.awt.Dimension(100, 20));
			jComboBoxSearchOptions.setBounds(new java.awt.Rectangle(525, 20, 100, 20));
			this.getContentPane().add(jComboBoxSearchOptions);
			jLabelExpDates.setText("Experiment Dates:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelExpDates);
			jLabelExpDates.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelExpDates.setBounds(new java.awt.Rectangle(20, 65, 130, 20));
			this.getContentPane().add(jLabelExpDates);
			jComboBoxDateFrom.setPreferredSize(new java.awt.Dimension(160, 20));
			jComboBoxDateFrom.setBounds(new java.awt.Rectangle(165, 55, 160, 20));
			this.getContentPane().add(jComboBoxDateFrom);
			jComboBoxDateTo.setPreferredSize(new java.awt.Dimension(160, 20));
			jComboBoxDateTo.setBounds(new java.awt.Rectangle(165, 85, 160, 20));
			this.getContentPane().add(jComboBoxDateTo);
			jLabelFrom.setText("From");
			jLabelFrom.setPreferredSize(new java.awt.Dimension(35, 20));
			jLabelFrom.setBounds(new java.awt.Rectangle(330, 55, 34, 20));
			this.getContentPane().add(jLabelFrom);
			jLabelTo.setText("To");
			jLabelTo.setPreferredSize(new java.awt.Dimension(35, 20));
			jLabelTo.setBounds(new java.awt.Rectangle(330, 85, 35, 20));
			this.getContentPane().add(jLabelTo);
			jLabelExperimentSites.setText("Data Sources:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelExperimentSites);
			jLabelExperimentSites.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelExperimentSites.setBounds(new java.awt.Rectangle(415, 55, 100, 20));
			this.getContentPane().add(jLabelExperimentSites);
			jTextFieldExpSites.setEnabled(false);
			jTextFieldExpSites.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldExpSites.setBounds(new java.awt.Rectangle(525, 55, 200, 20));
			this.getContentPane().add(jTextFieldExpSites);
			jButtonSites.setText("Sources");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonSites);
			jButtonSites.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					try {
						SitesDialog sd = new SitesDialog(owner, analyticalServiceServiceMetaData.getSiteDescriptionsToSiteCodeMap().keySet().iterator());
						sd.setVisible(true);
						if (sd.isDialogClosed()) {
							setSitesText(sd.getSitesText());
						}
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
				}
			});
			jButtonSites.setPreferredSize(new java.awt.Dimension(90, 20));
			jButtonSites.setBounds(new java.awt.Rectangle(730, 55, 90, 20));
			this.getContentPane().add(jButtonSites);
			jLabelGroupID.setText("Group Ids:");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelGroupID);
			jLabelGroupID.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelGroupID.setBounds(new java.awt.Rectangle(415, 90, 100, 20));
			this.getContentPane().add(jLabelGroupID);
			jTextFieldGroupIds.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldGroupIds.setBounds(new java.awt.Rectangle(525, 90, 200, 20));
			this.getContentPane().add(jTextFieldGroupIds);
			jLabelInstrumentTypes.setText("Instrument Type(s):");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelInstrumentTypes);
			jLabelInstrumentTypes.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelInstrumentTypes.setBounds(new java.awt.Rectangle(20, 125, 130, 20));
			this.getContentPane().add(jLabelInstrumentTypes);
			jTextFieldInsTypes.setEnabled(false);
			jTextFieldInsTypes.setPreferredSize(new java.awt.Dimension(152, 20));
			jTextFieldInsTypes.setBounds(new java.awt.Rectangle(165, 125, 152, 20));
			this.getContentPane().add(jTextFieldInsTypes);
			jButtonInsTypes.setText("Types");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonInsTypes);
			jButtonInsTypes.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					try {
						List<String> selInstrTypes = null;
						try {
							selInstrTypes = Arrays.asList(StringUtils.split(jTextFieldInsTypes.getText(), ','));
						} catch (Exception unusableException) {
							// Just ignore
						}
						InstrumentTypesDialog sd = new InstrumentTypesDialog(owner, analyticalServiceServiceMetaData.getInstrumentTypesSupported(), selInstrTypes);
						sd.setVisible(true);
						if (sd.isDialogClosed()) {
							setInstrumentsText(sd.getInsTypesText());
						}
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
				}
			});
			jButtonInsTypes.setPreferredSize(new java.awt.Dimension(70, 20));
			jButtonInsTypes.setBounds(new java.awt.Rectangle(321, 125, 70, 20));
			this.getContentPane().add(jButtonInsTypes);
			jLabelUserIds.setText("User ID(s):");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelUserIds);
			jLabelUserIds.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelUserIds.setBounds(new java.awt.Rectangle(415, 125, 100, 20));
			this.getContentPane().add(jLabelUserIds);
			jTextFieldUserIds.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldUserIds.setBounds(new java.awt.Rectangle(525, 125, 200, 20));
			this.getContentPane().add(jTextFieldUserIds);
			jLabelInsNames.setText("Instrument Name(s):");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelInsNames);
			jLabelInsNames.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelInsNames.setBounds(new java.awt.Rectangle(20, 160, 130, 20));
			this.getContentPane().add(jLabelInsNames);
			jTextFieldInsNames.setPreferredSize(new java.awt.Dimension(230, 20));
			jTextFieldInsNames.setBounds(new java.awt.Rectangle(165, 160, 230, 20));
			this.getContentPane().add(jTextFieldInsNames);
			jLabelFileTypes.setText("File Type(s):");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelFileTypes);
			jLabelFileTypes.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelFileTypes.setBounds(new java.awt.Rectangle(415, 160, 100, 20));
			this.getContentPane().add(jLabelFileTypes);
			jTextFieldFileTypes.setEnabled(false);
			jTextFieldFileTypes.setPreferredSize(new java.awt.Dimension(200, 20));
			jTextFieldFileTypes.setBounds(new java.awt.Rectangle(525, 160, 200, 20));
			this.getContentPane().add(jTextFieldFileTypes);
			jButtonFileTypes.setText("Types");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonFileTypes);
			jButtonFileTypes.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					try {
						FileTypesDialog sd = new FileTypesDialog(owner, analyticalServiceServiceMetaData.getFileTypesSupported());
						sd.setVisible(true);
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
				}
			});
			jButtonFileTypes.setPreferredSize(new java.awt.Dimension(70, 20));
			jButtonFileTypes.setBounds(new java.awt.Rectangle(730, 160, 70, 20));
			this.getContentPane().add(jButtonFileTypes);
			jLabelExpMethods.setText("Experiment Method(s):");
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelExpMethods);
			jLabelExpMethods.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelExpMethods.setBounds(new java.awt.Rectangle(20, 195, 130, 20));
			this.getContentPane().add(jLabelExpMethods);
			jTextFieldExpMethods.setPreferredSize(new java.awt.Dimension(230, 20));
			jTextFieldExpMethods.setBounds(new java.awt.Rectangle(165, 195, 230, 20));
			this.getContentPane().add(jTextFieldExpMethods);
// Not currently covered by AnalyticalService API.jar.			
//			jLabelFileNames.setText("File Name(s):");
//			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelFileNames);
//			jLabelFileNames.setPreferredSize(new java.awt.Dimension(100, 20));
//			jLabelFileNames.setBounds(new java.awt.Rectangle(415, 195, 100, 20));
//			this.getContentPane().add(jLabelFileNames);
//			jTextFieldFileNames.setPreferredSize(new java.awt.Dimension(200, 20));
//			jTextFieldFileNames.setBounds(new java.awt.Rectangle(525, 195, 200, 20));
//			this.getContentPane().add(jTextFieldFileNames);
			jButtonSearch.setText("Search");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonSearch);
			jButtonSearch.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jButtonSearchMouseClicked(evt);
				}
			});
			jButtonSearch.setPreferredSize(new java.awt.Dimension(80, 20));
			jButtonSearch.setBounds(new java.awt.Rectangle(308, 230, 82, 20));
			this.getContentPane().add(jButtonSearch);
			jButtonCancel.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);
			jButtonCancel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jButtonCancelMouseClicked(evt);
				}
			});
			jButtonCancel.setPreferredSize(new java.awt.Dimension(80, 20));
			jButtonCancel.setBounds(new java.awt.Rectangle(418, 230, 79, 20));
			this.getContentPane().add(jButtonCancel);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					AdvancedLinkJDialog.this.setVisible(false);
					AdvancedLinkJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					AdvancedLinkJDialog.this.removeAll();
					AdvancedLinkJDialog.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			processException(e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		try {
			setSitesText(CodeTableCache.getCache().getSiteDescription(MasterController.getUser().getSiteCode()));
			jComboBoxSearchOptions.addItem("Equals");
			jComboBoxSearchOptions.addItem("Starts With");
			jComboBoxSearchOptions.addItem("Ends With");
			jComboBoxSearchOptions.addItem("Contains");
		} catch (Exception e) {
		}
	}

	public void dispose() {
		this.analyticalModel = null;
		this.tableViewer = null;
		super.dispose();
	}

	private void jButtonCancelMouseClicked(MouseEvent evt) {
		this.setVisible(false);
		this.dispose();
	}

	private void jButtonSearchMouseClicked(MouseEvent evt) {
		// if (jTextFieldSampleRefs.getText().length()<1) {
		// JOptionPane.showMessageDialog(this, "Please Enter SampleReferences.",
		// "Input Error", JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		if (jTextFieldExpSites.getText().length() < 1) {
			JOptionPane.showMessageDialog(this, "Please select at least one site.", "Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		queryText = getQueryText();
		if (StringUtils.isBlank(queryText)) {
			JOptionPane.showMessageDialog(	this,
											"Please select at least one search attribute.",
											"Input Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		setVisible(false);
		String msg;
		if (jTextFieldSampleRefs == null || StringUtils.isBlank(jTextFieldSampleRefs.getText())) {
			msg = "Searching AnalyticalService ...";
		} else {
			msg = "Searching AnalyticalService for \'" + jTextFieldSampleRefs.getText() + "\' ...";
		}
		Object result = CeNJob.getCeNJob().execute(this, msg, "performAdvancedLinkSearch", null, null);
		// return value of the method performAdvancedLinkSearch
		if (result != null && result instanceof Boolean) {
			boolean success = ((Boolean) result).booleanValue();
			if (success) {
				tableViewer.reload();
			} else {
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(),
						"Unable to find any Advanced Link results.", "Advanced Link", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		dispose();
	}

	public static void setSitesText(String arg0) {
		jTextFieldExpSites.setText(arg0);
	}

	public static void setInstrumentsText(String arg0) {
		jTextFieldInsTypes.setText(arg0);
	}

	public static void setFileTypesText(String arg0) {
		jTextFieldFileTypes.setText(arg0.trim());
	}

	public String buildQueryText() {
		StringBuffer sb_query = new StringBuffer("");
		// Sample References
		StringBuffer tempBuff = new StringBuffer("");
		String selectedSearchOption = jComboBoxSearchOptions.getSelectedItem().toString();
		String s_SampleRefs[] = jTextFieldSampleRefs.getText().split(",");
		for (int i = 0; i < s_SampleRefs.length; i++) {
			s_SampleRefs[i] = s_SampleRefs[i].trim();
			if (s_SampleRefs[i].length() > 0) {
				if (selectedSearchOption.equals("Contains")) {
					tempBuff.append("[Sample Reference] Contains \"").append(s_SampleRefs[i]).append("\"");
				} else if (selectedSearchOption.equals("Starts With")) {
					tempBuff.append("[Sample Reference] Contains \"").append(s_SampleRefs[i]).append("%\"");
				} else if (selectedSearchOption.equals("Ends With")) {
					tempBuff.append("[Sample Reference] Contains \"%").append(s_SampleRefs[i]).append("\"");
				} else {
					tempBuff.append("[Sample Reference] = \"").append(s_SampleRefs[i]).append("\"");
				}
				if (i < s_SampleRefs.length - 1)
					tempBuff.append(" OR ");
			}
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// Experiment Sites
		/*
		 * String s_ExperimentSites[] = jTextFieldExpSites.getText().split(","); for (int i=0; i < s_ExperimentSites.length; i++) {
		 * s_ExperimentSites[i] = s_ExperimentSites[i].trim(); if (s_ExperimentSites[i].length()<1) continue;
		 * sb_query.append("[Site]=\"").append(s_ExperimentSites[i]).append("\""); if (i < s_ExperimentSites.length-1
		 * )sb_query.append(" OR "); else sb_query.append(" AND "); }
		 */
		// experiment dates
		String s_date = parseDate();
		if (s_date.length() > 0) {
			tempBuff.append(s_date);
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// Group Ids
		String s_GroupIds[] = jTextFieldGroupIds.getText().split(",");
		for (int i = 0; i < s_GroupIds.length; i++) {
			s_GroupIds[i] = s_GroupIds[i].trim();
			if (s_GroupIds[i].length() < 1)
				continue;
			tempBuff.append("[Group ID]=\"").append(s_GroupIds[i]).append("\"");
			if (i < s_GroupIds.length - 1)
				tempBuff.append(" OR ");
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// InstrumentTypes
		String s_InsTypes[] = jTextFieldInsTypes.getText().split(",");
		for (int i = 0; i < s_InsTypes.length; i++) {
			s_InsTypes[i] = s_InsTypes[i].trim();
			if (s_InsTypes[i].length() < 1)
				continue;
			tempBuff.append("[Instrument Type]=\"").append(s_InsTypes[i]).append("\"");
			if (i < s_InsTypes.length - 1)
				tempBuff.append(" OR ");
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// User IDs
		String s_UserIds[] = jTextFieldUserIds.getText().split(",");
		for (int i = 0; i < s_UserIds.length; i++) {
			s_UserIds[i] = s_UserIds[i].trim();
			if (s_UserIds[i].length() < 1)
				continue;
			tempBuff.append("[User ID]=\"").append(s_UserIds[i]).append("\"");
			if (i < s_UserIds.length - 1)
				tempBuff.append(" OR ");
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// Instrument Names
		String s_InsNames[] = jTextFieldInsNames.getText().split(",");
		for (int i = 0; i < s_InsNames.length; i++) {
			s_InsNames[i] = s_InsNames[i].trim();
			if (s_InsNames[i].length() < 1)
				continue;
			tempBuff.append("[Instrument]=\"").append(s_InsNames[i]).append("\"");
			if (i < s_InsNames.length - 1)
				tempBuff.append(" OR ");
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// Data File Types
		String s_FileTypes[] = jTextFieldFileTypes.getText().split(",");
		for (int i = 0; i < s_FileTypes.length; i++) {
			s_FileTypes[i] = s_FileTypes[i].trim();
			if (s_FileTypes[i].length() < 1)
				continue;
			tempBuff.append("[Data File Type]=\"").append(s_FileTypes[i]).append("\"");
			if (i < s_FileTypes.length - 1)
				tempBuff.append(" OR ");
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
		// Experiment Methods
		String s_ExpMethods[] = jTextFieldExpMethods.getText().split(",");
		for (int i = 0; i < s_ExpMethods.length; i++) {
			s_ExpMethods[i] = s_ExpMethods[i].trim();
			if (s_ExpMethods[i].length() < 1)
				continue;
			tempBuff.append("[Experiment Name]=\"").append(s_ExpMethods[i]).append("\"");
			if (i < s_ExpMethods.length - 1)
				tempBuff.append(" OR ");
		}
		if (tempBuff.length() > 0) {
			sb_query.append("(").append(tempBuff).append(") AND ");
			tempBuff.setLength(0);
		}
//		// File Names
//		String s_FileNames[] = jTextFieldFileNames.getText().split(",");
//		for (int i = 0; i < s_FileNames.length; i++) {
//			s_FileNames[i] = s_FileNames[i].trim();
//			if (s_FileNames[i].length() < 1)
//				continue;
//			tempBuff.append("[File Name]=\"").append(s_FileNames[i]).append("\"");
//			if (i < s_FileNames.length - 1)
//				tempBuff.append(" OR ");
//		}
//		if (tempBuff.length() > 0) {
//			sb_query.append("(").append(tempBuff).append(") AND ");
//			tempBuff.setLength(0);
//		}
		String returnQueryString = sb_query.toString();
		if (returnQueryString.endsWith(" AND ")) {
			returnQueryString = returnQueryString.substring(0, returnQueryString.length() - 5);
		}
		return returnQueryString;
	}

	public String getQueryText() {
		return buildQueryText();
	}

	public PAnalyticalUtility getAnalyticalModel() {
		return analyticalModel;
	}

	public void setAnalyticalModel(PAnalyticalUtility analyticalModel) {
		this.analyticalModel = analyticalModel;
	}

	public void setTableViewer(AnalyticalDetailTableView tableViewer) {
		this.tableViewer = tableViewer;
	}

	public ArrayList<String> getSites() {
		Set<String> sites = new TreeSet<String>();
		// Experiment Sites
		String s_ExperimentSites[] = jTextFieldExpSites.getText().split(",");
		for (int i = 0; i < s_ExperimentSites.length; i++) {
			String siteCode = (String) ht_SitesDescCode.get(s_ExperimentSites[i].trim());
			if (siteCode != null) {
				sites.add(ht_SitesDescCode.get(s_ExperimentSites[i].trim()));
			}
		}
		return new ArrayList<String>(sites);
	}

	public String parseDate() {
		String fromDate = "";
		String toDate = "";
		if (jComboBoxDateFrom.getSelectedItem() != null) {
			fromDate = jComboBoxDateFrom.getSelectedItem().toString();
		}
		if (jComboBoxDateTo.getSelectedItem() != null) {
			toDate = jComboBoxDateTo.getSelectedItem().toString();
		}
		String from[] = fromDate.split(" ");
		String to[] = toDate.split(" ");
		if (from.length == 3) {
			fromDate = "[Experiment Time] >= #" + from[2] + "-" + from[0] + "-" + from[1] + " 00:00:00#";
		}
		if (to.length == 3) {
			if (fromDate.equals("")) {
				toDate = " [Experiment Time] <= #" + to[2] + "-" + to[0] + "-" + to[1] + " 23:59:59#";
			} else {
				toDate = " AND [Experiment Time] <= #" + to[2] + "-" + to[0] + "-" + to[1] + " 23:59:59#";
			}
		}
		return fromDate + toDate;
	}

	private void fillHashtable(Map<String, String> siteDescriptionsToSiteCodeMap) {
		for (Map.Entry<String, String> entrySet : siteDescriptionsToSiteCodeMap.entrySet()) {
			ht_SitesDescCode.put(entrySet.getKey(), entrySet.getValue()); // Desc and Code
		}
	}

	private void processException(Exception e) {
		ceh.logExceptionMsg(this, "Error occurred while performing Advanced Link", e);
	}

	public boolean performAdvancedLinkSearch() {
		return analyticalModel.performAdvancedLinkAll(queryText, getSites());
	}
}
