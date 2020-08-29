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
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegHandlingCodeCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegHazardCodeCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegStorageCodeCache;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.CeNDialog;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class JDialogHealthHazInfo extends CeNDialog {

	private static final long serialVersionUID = 6119740450644502565L;

	private static final String MORE_BATCH_HAZARD = "see Hazard Comments";
	private static final String MORE_PARENT_BATCH_HAZARD = "Parent Hazard Comments";
	private static final String OTHER_STRING = "Other";
	private JLabel jLabel1;
	private JPanel jPanel2;
	private JPanel jPanelHandlingComments;
	private JLabel jLabelHandlingComment;
	private JTextField jTextAreaOtherStorage;
	private JTextField jTextAreaOtherHandling;
	private JPanel jPanelStorage;
	private JPanel jPanelHandling;
	private JPanel jPanelStorageInfoCont;
	private JPanel jPanelHandlingPrecationsCont;
	private JLabel jLabelStorageInformation;
	private JLabel jLabelHandlingPrecautions;
	private JTextField AddMoreBatchHazardComments;
	private JLabel jLabelAddMoreBatchHazComments;
	private JTextField AddMoreParentHazComments;
	private JLabel jLabelAddMoreParentHazCom;
	private JPanel jPanelAddMoreBatchCom;
	private JPanel jPanelAddMoreParentCom;
	private JPanel jPanelExistingHazCom;
	private JPanel jPanelComments;
	private JLabel jLabelHealthHaz;
	private JPanel jPanelHealthHazCont;
	private JPanel jPanelMainEast;
	private JPanel jPanelMainWest;
	private JPanel jPanelfiller;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JPanel jPanel1;
	private JPanel jPanelMain;
	private ArrayList hazardList = new ArrayList();
	private ArrayList handlingList = new ArrayList();
	private ArrayList storageList = new ArrayList();
	private ArrayList hazardCheckBoxList = new ArrayList();
	private ArrayList handlingCheckBoxList = new ArrayList();
	private ArrayList storageCheckBoxList = new ArrayList();
	private String hazardString = new String();
	private String handlingString = new String();
	private String storageString = new String();
	private String parentHazardString = new String();
	private ArrayList hazardCodeList = new ArrayList();
	private ArrayList handlingCodeList = new ArrayList();
	private ArrayList storageCodeList = new ArrayList();
	private ProductBatchModel selectedBatch = new ProductBatchModel();
	private HashMap hazardMap = new HashMap();
	private HashMap handlingMap = new HashMap();
	private HashMap storageMap = new HashMap();
	private boolean canceled = false;
	private NotebookPageModel pageModel;

	public JDialogHealthHazInfo(JFrame owner, NotebookPageModel pageModel) {
		super(owner);
		this.pageModel = pageModel;
		this.initFakeGui(); // this.initGUI();
	}
	
	private void initFakeGui() {		
		AddMoreParentHazComments = new JTextField();
		AddMoreBatchHazardComments = new JTextField();
		jTextAreaOtherHandling = new JTextField();
		jTextAreaOtherStorage = new JTextField();
				
		JPanel panel = new JPanel(new GridLayout(12, 1));
		
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		panel.add(createBoldLabel("Parent Hazard Comments:"));
		panel.add(AddMoreParentHazComments);
		panel.add(new JLabel());
		panel.add(createBoldLabel("Batch Hazard Comments:"));
		panel.add(AddMoreBatchHazardComments);
		panel.add(new JLabel());
		panel.add(createBoldLabel("Handling Comments:"));
		panel.add(jTextAreaOtherHandling);
		panel.add(new JLabel());
		panel.add(createBoldLabel("Storage Comments:"));
		panel.add(jTextAreaOtherStorage);
				
		jButtonOk = new JButton("OK");
		jButtonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonOkActionPerformed();
			}
		});
		
		jButtonCancel = new JButton("Cancel");
		jButtonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonCancelActionPerformed();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(jButtonOk);
		buttonPanel.add(jButtonCancel);
				
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.NORTH);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		this.getRootPane().setDefaultButton(jButtonOk);
		
		this.setPreferredSize(new Dimension(600, 350));
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle("Edit Health Hazard Information");
		this.setModal(true);
		this.setLocationRelativeTo(this.getOwner());
	}

	private JLabel createBoldLabel(String text) {
		JLabel label = new JLabel(text);
		CeNGUIUtils.styleComponentText(Font.BOLD, label);
		return label;		
	}
	
	public void initGUI() {
		try {
			jPanelMain = new JPanel();
			jPanelMainWest = new JPanel();
			jPanelHealthHazCont = new JPanel();
			jLabelHealthHaz = new JLabel();
			jPanelComments = new JPanel();
			jPanelExistingHazCom = new JPanel();
			jPanelAddMoreParentCom = new JPanel();
			jLabelAddMoreParentHazCom = new JLabel();
			AddMoreParentHazComments = new JTextField();
			jPanelAddMoreBatchCom = new JPanel();
			jLabelAddMoreBatchHazComments = new JLabel();
			AddMoreBatchHazardComments = new JTextField();
			jPanelMainEast = new JPanel();
			jPanelHandling = new JPanel();
			jLabelHandlingPrecautions = new JLabel();
			jPanelHandlingPrecationsCont = new JPanel();
			jPanelHandlingComments = new JPanel();
			jTextAreaOtherHandling = new JTextField();
			jLabelHandlingComment = new JLabel();
			jPanelStorage = new JPanel();
			jLabelStorageInformation = new JLabel();
			jPanelStorageInfoCont = new JPanel();
			jPanel2 = new JPanel();
			jTextAreaOtherStorage = new JTextField();
			jLabel1 = new JLabel();
			jPanel1 = new JPanel();
			jButtonOk = new JButton();
			jPanelfiller = new JPanel();
			jButtonCancel = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);

			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setTitle("Edit Health Hazard Information");
			this.setModal(true);
			this.setName("Edit Health Hazard Information");
			this.setSize(new java.awt.Dimension(807, 627));
			this.setLocationRelativeTo(this.getOwner());

			BorderLayout jPanelMainLayout = new BorderLayout();
			jPanelMain.setLayout(jPanelMainLayout);
			jPanelMainLayout.setHgap(0);
			jPanelMainLayout.setVgap(0);
			this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
			BorderLayout jPanelMainWestLayout = new BorderLayout();
			jPanelMainWest.setLayout(jPanelMainWestLayout);
			jPanelMainWestLayout.setHgap(0);
			jPanelMainWestLayout.setVgap(0);
			jPanelMainWest.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			jPanelMain.add(jPanelMainWest, BorderLayout.WEST);
			BoxLayout jPanelHealthHazContLayout = new BoxLayout(
					jPanelHealthHazCont, 0);
			jPanelHealthHazCont.setLayout(jPanelHealthHazContLayout);
			jPanelMainWest.add(jPanelHealthHazCont, BorderLayout.CENTER);
			jLabelHealthHaz
					.setText("Health Hazard (Select all those applicable)");
			jLabelHealthHaz.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelMainWest.add(jLabelHealthHaz, BorderLayout.NORTH);
			BorderLayout jPanelCommentsLayout = new BorderLayout();
			jPanelComments.setLayout(jPanelCommentsLayout);
			jPanelCommentsLayout.setHgap(0);
			jPanelCommentsLayout.setVgap(0);
			jPanelMainWest.add(jPanelComments, BorderLayout.SOUTH);
			BoxLayout jPanelExistingHazComLayout = new BoxLayout(
					jPanelExistingHazCom, 1);
			jPanelExistingHazCom.setLayout(jPanelExistingHazComLayout);
			jPanelComments.add(jPanelExistingHazCom, BorderLayout.NORTH);
			BoxLayout jPanelAddMoreParentComLayout = new BoxLayout(
					jPanelAddMoreParentCom, 1);
			jPanelAddMoreParentCom.setLayout(jPanelAddMoreParentComLayout);
			jPanelComments.add(jPanelAddMoreParentCom, BorderLayout.CENTER);
			jLabelAddMoreParentHazCom
					.setText("Add More Parent Hazard Comments:");
			jLabelAddMoreParentHazCom.setFont(new java.awt.Font("sansserif", 1,
					11));
			jPanelAddMoreParentCom.add(jLabelAddMoreParentHazCom);
			jPanelAddMoreParentCom.add(AddMoreParentHazComments);
			BoxLayout jPanelAddMoreBatchComLayout = new BoxLayout(
					jPanelAddMoreBatchCom, 1);
			jPanelAddMoreBatchCom.setLayout(jPanelAddMoreBatchComLayout);
			jPanelComments.add(jPanelAddMoreBatchCom, BorderLayout.SOUTH);
			jLabelAddMoreBatchHazComments
					.setText("Add More Batch Hazard Comments:");
			jLabelAddMoreBatchHazComments.setFont(new java.awt.Font(
					"sansserif", 1, 11));
			jLabelAddMoreBatchHazComments
					.setPreferredSize(new java.awt.Dimension(172, 15));
			jPanelAddMoreBatchCom.add(jLabelAddMoreBatchHazComments);
			jPanelAddMoreBatchCom.add(AddMoreBatchHazardComments);
			BorderLayout jPanelMainEastLayout = new BorderLayout();
			jPanelMainEast.setLayout(jPanelMainEastLayout);
			jPanelMainEastLayout.setHgap(0);
			jPanelMainEastLayout.setVgap(0);
			jPanelMainEast.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			jPanelMain.add(jPanelMainEast, BorderLayout.EAST);
			BorderLayout jPanelHandlingLayout = new BorderLayout();
			jPanelHandling.setLayout(jPanelHandlingLayout);
			jPanelHandlingLayout.setHgap(0);
			jPanelHandlingLayout.setVgap(0);
			jPanelHandling.setVisible(true);
			jPanelMainEast.add(jPanelHandling, BorderLayout.NORTH);
			jLabelHandlingPrecautions
					.setText("Handling Precautions (select all those applicable)");
			jLabelHandlingPrecautions
					.setHorizontalAlignment(SwingConstants.LEADING);
			jLabelHandlingPrecautions
					.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabelHandlingPrecautions.setVisible(true);
			jLabelHandlingPrecautions.setFont(new java.awt.Font("sansserif", 1,
					11));
			jPanelHandling.add(jLabelHandlingPrecautions, BorderLayout.NORTH);
			BoxLayout jPanelHandlingPrecationsContLayout = new BoxLayout(
					jPanelHandlingPrecationsCont, 0);
			jPanelHandlingPrecationsCont
					.setLayout(jPanelHandlingPrecationsContLayout);
			jPanelHandlingPrecationsCont.setVisible(true);
			jPanelHandlingPrecationsCont.setBorder(new EmptyBorder(new Insets(
					0, 0, 0, 0)));
			jPanelHandling.add(jPanelHandlingPrecationsCont,
					BorderLayout.CENTER);
			BorderLayout jPanelHandlingCommentsLayout = new BorderLayout();
			jPanelHandlingComments.setLayout(jPanelHandlingCommentsLayout);
			jPanelHandlingCommentsLayout.setHgap(0);
			jPanelHandlingCommentsLayout.setVgap(0);
			jPanelHandling.add(jPanelHandlingComments, BorderLayout.SOUTH);
			jTextAreaOtherHandling.setBorder(new EtchedBorder(
					BevelBorder.LOWERED, null, null));
			jPanelHandlingComments.add(jTextAreaOtherHandling,
					BorderLayout.CENTER);
			FlowLayout jLabelHandlingCommentLayout = new FlowLayout();
			jLabelHandlingComment.setLayout(jLabelHandlingCommentLayout);
			jLabelHandlingCommentLayout.setAlignment(FlowLayout.CENTER);
			jLabelHandlingCommentLayout.setHgap(5);
			jLabelHandlingCommentLayout.setVgap(5);
			jLabelHandlingComment.setText("Handling Comments");
			jLabelHandlingComment
					.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelHandlingComments.add(jLabelHandlingComment,
					BorderLayout.NORTH);
			BorderLayout jPanelStorageLayout = new BorderLayout();
			jPanelStorage.setLayout(jPanelStorageLayout);
			jPanelStorageLayout.setHgap(0);
			jPanelStorageLayout.setVgap(0);
			jPanelMainEast.add(jPanelStorage, BorderLayout.CENTER);
			BorderLayout jLabelStorageInformationLayout = new BorderLayout();
			jLabelStorageInformation.setLayout(jLabelStorageInformationLayout);
			jLabelStorageInformationLayout.setHgap(0);
			jLabelStorageInformationLayout.setVgap(0);
			jLabelStorageInformation
					.setText("Storage Information (select all those applicable)");
			jLabelStorageInformation.setVisible(true);
			jLabelStorageInformation.setFont(new java.awt.Font("sansserif", 1,
					11));
			jPanelStorage.add(jLabelStorageInformation, BorderLayout.NORTH);
			BorderLayout jPanelStorageInfoContLayout = new BorderLayout();
			jPanelStorageInfoCont.setLayout(jPanelStorageInfoContLayout);
			jPanelStorageInfoContLayout.setHgap(0);
			jPanelStorageInfoContLayout.setVgap(0);
			jPanelStorageInfoCont.setBorder(new EmptyBorder(new Insets(0, 0, 0,
					0)));
			jPanelStorage.add(jPanelStorageInfoCont, BorderLayout.CENTER);
			BorderLayout jPanel2Layout = new BorderLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setHgap(0);
			jPanel2Layout.setVgap(0);
			jPanel2.setPreferredSize(new java.awt.Dimension(269, 34));
			jPanelStorage.add(jPanel2, BorderLayout.SOUTH);
			jTextAreaOtherStorage.setBorder(new EtchedBorder(
					BevelBorder.LOWERED, null, null));
			jPanel2.add(jTextAreaOtherStorage, BorderLayout.SOUTH);
			FlowLayout jLabel1Layout = new FlowLayout();
			jLabel1.setLayout(jLabel1Layout);
			jLabel1Layout.setAlignment(FlowLayout.CENTER);
			jLabel1Layout.setHgap(5);
			jLabel1Layout.setVgap(5);
			jLabel1.setText("Storage Comments");
			jLabel1.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanel2.add(jLabel1, BorderLayout.NORTH);
			getContentPane().add(jPanel1, BorderLayout.SOUTH);
			jButtonOk.setText("Ok");
			jButtonOk.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonOk.setPreferredSize(new java.awt.Dimension(51, 27));
			jPanel1.add(jButtonOk);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed();
				}
			});
			jPanelfiller.setVisible(true);
			jPanelfiller.setPreferredSize(new java.awt.Dimension(69, 10));
			jPanel1.add(jPanelfiller);
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanel1.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed();
				}
			});
			resetMainPanelLayout();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void defaultApplyAction() {
		jButtonOkActionPerformed();
	}

	protected void defaultCancelAction() {
		jButtonCancelActionPerformed();
	}

	private void resetMainPanelLayout() {
		double border = 4;
		double size[][] = { { border, 0.48, 0.04, 0.48, border }, // Columns
				{ TableLayout.FILL } }; // Rows
		jPanelMain.setLayout(new TableLayout(size));
		// JScrollPane healthHazInfoScrollPane = new JScrollPane(jPanelMain);
		getContentPane().add(jPanelMain, BorderLayout.CENTER);
		jPanelMain.add(jPanelMainWest, "1,0");
		jPanelMain.add(jPanelMainEast, "3,0");
	}

	public void populateOptions() {
		constructHazardPanel();
		constructHandlingPanel();
		constructStoragePanel();
	}

	private void constructHazardPanel() {
		if (!StringUtils.isBlank(getSelectedBatch().getHazardComments()) && AddMoreBatchHazardComments != null) {
			AddMoreBatchHazardComments.setText(getSelectedBatch().getHazardComments().trim());
		}
		if (!StringUtils.isBlank(getSelectedBatch().getCompound().getHazardComments()) && AddMoreParentHazComments != null) {
			AddMoreParentHazComments.setText(getSelectedBatch().getCompound().getHazardComments().trim());
		}
		if (jPanelHealthHazCont == null) {
			return;
		}
		try {
			int hazardRowsize = Math.round((getHazardList().size() / 2f));
			double[] hazardRow = new double[hazardRowsize];
			for (int i = 0; i < hazardRowsize; i++) {
				hazardRow[i] = 20;
			}
			double border = 4;
			double size[][] = { { border, 0.48, 0.04, 0.48, border }, // Columns
					hazardRow }; // Rows
			jPanelHealthHazCont.setLayout(new TableLayout(size));
			for (int i = 0; i < hazardRowsize; i++) {
				JCheckBox jCheckBox1 = new JCheckBox();
				jCheckBox1.setPreferredSize(new Dimension(25, 45));
				RegHazardCodeCache regHazardCodeCache1 = (RegHazardCodeCache) (getHazardList()
						.get(i * 2));
				jCheckBox1.setText(regHazardCodeCache1.getDescription().trim());
				jCheckBox1.setToolTipText(jCheckBox1.getText());
				jPanelHealthHazCont.add(jCheckBox1, "1, " + i);
				hazardCheckBoxList.add(jCheckBox1);
				JCheckBox jCheckBox2 = new JCheckBox();
				jCheckBox2.setPreferredSize(new Dimension(25, 45));
				RegHazardCodeCache regHazardCodeCache2;
				if (getHazardList().size() > (i * 2 + 1)) {
					regHazardCodeCache2 = (RegHazardCodeCache) (getHazardList()
							.get(i * 2 + 1));
					jCheckBox2.setText(regHazardCodeCache2.getDescription()
							.trim());
					jCheckBox2.setToolTipText(jCheckBox2.getText());
					jPanelHealthHazCont.add(jCheckBox2, "3, " + i);
					hazardCheckBoxList.add(jCheckBox2);
				}
				if (getSelectedBatch().getRegInfo()
						.getCompoundRegistrationHazardCodes() != null
						&& getSelectedBatch().getRegInfo()
								.getCompoundRegistrationHazardCodes().size() > 0) {
					for (int j = 0; j < getSelectedBatch().getRegInfo()
							.getCompoundRegistrationHazardCodes().size(); j++) {
						if (jCheckBox1
								.getText()
								.equals(((String) getHazardMap()
										.get(getSelectedBatch()
												.getRegInfo()
												.getCompoundRegistrationHazardCodes()
												.get(j))).trim())) {
							jCheckBox1.setSelected(true);
						}
						if (jCheckBox2
								.getText()
								.equals(((String) getHazardMap()
										.get(getSelectedBatch()
												.getRegInfo()
												.getCompoundRegistrationHazardCodes()
												.get(j))).trim())) {
							jCheckBox2.setSelected(true);
						}
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	private void constructHandlingPanel() {
		if (!StringUtils.isBlank(getSelectedBatch().getHandlingComments()) && jTextAreaOtherHandling != null) {
			jTextAreaOtherHandling.setText(getSelectedBatch().getHandlingComments());
			jTextAreaOtherHandling.setCaretPosition(0);
		}
		if (jPanelHandlingPrecationsCont == null) {
			return;
		}
		try {
			int handlingRowsize = Math.round((getHandlingList().size() / 2f));
			double[] handlingRow = new double[handlingRowsize];
			for (int i = 0; i < handlingRowsize; i++) {
				handlingRow[i] = 20;
			}
			double border = 4;
			double size[][] = { { border, 0.48, 0.04, 0.48, border }, // Columns
					handlingRow }; // Rows
			jPanelHandlingPrecationsCont.setLayout(new TableLayout(size));
			for (int i = 0; i < handlingRowsize; i++) {
				JCheckBox jCheckBox1 = new JCheckBox();
				jCheckBox1.setPreferredSize(new Dimension(25, 45));
				RegHandlingCodeCache regHandlingCodeCache1 = (RegHandlingCodeCache) getHandlingList()
						.get(i * 2);
				jCheckBox1.setText(regHandlingCodeCache1.getDescription()
						.trim());
				jCheckBox1.setToolTipText(jCheckBox1.getText());
				jPanelHandlingPrecationsCont.add(jCheckBox1, "1, " + i);
				handlingCheckBoxList.add(jCheckBox1);
				JCheckBox jCheckBox2 = new JCheckBox();
				jCheckBox2.setPreferredSize(new Dimension(25, 45));
				RegHandlingCodeCache regHandlingCodeCache2;
				if (getHandlingList().size() > (i * 2 + 1)) {
					regHandlingCodeCache2 = (RegHandlingCodeCache) getHandlingList()
							.get(i * 2 + 1);
					jCheckBox2.setText(regHandlingCodeCache2.getDescription()
							.trim());
					jCheckBox2.setToolTipText(jCheckBox2.getText());
					jPanelHandlingPrecationsCont.add(jCheckBox2, "3, " + i);
					handlingCheckBoxList.add(jCheckBox2);
				}
				if (getSelectedBatch().getRegInfo()
						.getCompoundRegistrationHandlingCodes() != null
						&& getSelectedBatch().getRegInfo()
								.getCompoundRegistrationHandlingCodes().size() > 0) {
					for (int j = 0; j < getSelectedBatch().getRegInfo()
							.getCompoundRegistrationHandlingCodes().size(); j++) {
						if (jCheckBox1
								.getText()
								.equals(((String) getHandlingMap()
										.get(getSelectedBatch()
												.getRegInfo()
												.getCompoundRegistrationHandlingCodes()
												.get(j))).trim())) {
							jCheckBox1.setSelected(true);
						}
						if (jCheckBox2
								.getText()
								.equals(((String) getHandlingMap()
										.get(getSelectedBatch()
												.getRegInfo()
												.getCompoundRegistrationHandlingCodes()
												.get(j))).trim())) {
							jCheckBox2.setSelected(true);
						}
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	private void constructStoragePanel() {
		if (!StringUtils.isBlank(getSelectedBatch().getStorageComments()) && jTextAreaOtherStorage != null) {
			jTextAreaOtherStorage.setText(getSelectedBatch().getStorageComments().trim());
			jTextAreaOtherStorage.setCaretPosition(0);
		}
		if (jPanelStorageInfoCont == null) {
			return;
		}
		try {
			int storageRowsize = Math.round((getStorageList().size() / 2f));
			double[] storageRow = new double[storageRowsize];
			for (int i = 0; i < storageRowsize; i++) {
				storageRow[i] = 20;
			}
			double border = 4;
			double size[][] = { { border, 0.48, 0.04, 0.48, border }, // Columns
					storageRow }; // Rows
			jPanelStorageInfoCont.setLayout(new TableLayout(size));
			for (int i = 0; i < storageRowsize; i++) {
				JCheckBox jCheckBox1 = new JCheckBox();
				jCheckBox1.setPreferredSize(new Dimension(25, 45));
				RegStorageCodeCache regStorageCodeCache1 = (RegStorageCodeCache) getStorageList()
						.get(i * 2);
				jCheckBox1
						.setText(regStorageCodeCache1.getDescription().trim());
				jCheckBox1.setToolTipText(jCheckBox1.getText());
				jPanelStorageInfoCont.add(jCheckBox1, "1, " + i);
				storageCheckBoxList.add(jCheckBox1);
				JCheckBox jCheckBox2 = new JCheckBox();
				jCheckBox2.setPreferredSize(new Dimension(25, 45));
				RegStorageCodeCache regStorageCodeCache2;
				if (getStorageList().size() > (i * 2 + 1)) {
					regStorageCodeCache2 = (RegStorageCodeCache) getStorageList()
							.get(i * 2 + 1);
					jCheckBox2.setText(regStorageCodeCache2.getDescription()
							.trim());
					jCheckBox2.setToolTipText(jCheckBox2.getText());
					jPanelStorageInfoCont.add(jCheckBox2, "3, " + i);
					storageCheckBoxList.add(jCheckBox2);
				}
				if (getSelectedBatch().getRegInfo()
						.getCompoundRegistrationStorageCodes() != null
						&& getSelectedBatch().getRegInfo()
								.getCompoundRegistrationStorageCodes().size() > 0) {
					for (int j = 0; j < getSelectedBatch().getRegInfo()
							.getCompoundRegistrationStorageCodes().size(); j++) {
						if (jCheckBox1
								.getText()
								.equals(((String) getStorageMap()
										.get(getSelectedBatch()
												.getRegInfo()
												.getCompoundRegistrationStorageCodes()
												.get(j))).trim())) {
							jCheckBox1.setSelected(true);
						} else if (jCheckBox2
								.getText()
								.equals(((String) getStorageMap()
										.get(getSelectedBatch()
												.getRegInfo()
												.getCompoundRegistrationStorageCodes()
												.get(j))).trim())) {
							jCheckBox2.setSelected(true);
						}
					}
				}
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	public boolean validateHealthHazardInfo() {
		boolean hasError = false;
		String testString = "";
		JCheckBox itemJCheckBox = new JCheckBox();
		getSelectedBatch().getRegInfo().getCompoundRegistrationStorageCodes()
				.clear();
		for (int i = 0; i < storageCheckBoxList.size(); i++) {
			itemJCheckBox = (JCheckBox) storageCheckBoxList.get(i);
			testString = itemJCheckBox.getText().trim();
			if (itemJCheckBox.isSelected()) {
				if (testString.toUpperCase()
						.indexOf(OTHER_STRING.toUpperCase()) >= 0) {
					if (jTextAreaOtherStorage.getText().trim() == null
							|| jTextAreaOtherStorage.getText().trim().length() == 0) {
						hasError = true;
						JOptionPane.showMessageDialog(this,
								"Other storage comments is required.");
						break;
					}
				}
			}
		}
		if (!hasError) {
			getSelectedBatch().getRegInfo()
					.getCompoundRegistrationHandlingCodes().clear();
			for (int i = 0; i < handlingCheckBoxList.size(); i++) {
				itemJCheckBox = (JCheckBox) handlingCheckBoxList.get(i);
				testString = itemJCheckBox.getText().trim();
				if (itemJCheckBox.isSelected()) {
					if (testString.toUpperCase().indexOf(
							OTHER_STRING.toUpperCase()) >= 0) {
						if (jTextAreaOtherHandling.getText().trim() == null
								|| jTextAreaOtherHandling.getText().trim()
										.length() == 0) {
							hasError = true;
							JOptionPane.showMessageDialog(this,
									"Other handling comments is required.");
							break;
						}
					}
				}
			}
		}
		if (!hasError) {
			getSelectedBatch().getRegInfo()
					.getCompoundRegistrationHazardCodes().clear();
			for (int i = 0; i < hazardCheckBoxList.size(); i++) {
				itemJCheckBox = (JCheckBox) hazardCheckBoxList.get(i);
				testString = itemJCheckBox.getText().trim();
				if (itemJCheckBox.isSelected()) {
					if (testString.toUpperCase().indexOf(
							MORE_BATCH_HAZARD.toUpperCase()) >= 0) {
						if (AddMoreBatchHazardComments.getText().trim() == null
								|| AddMoreBatchHazardComments.getText().trim()
										.length() == 0) {
							hasError = true;
							JOptionPane.showMessageDialog(this,
									"Batch hazard comments is required.");
							break;
						}
					} else if (testString.toUpperCase().indexOf(
							MORE_PARENT_BATCH_HAZARD.toUpperCase()) >= 0) {
						if (AddMoreParentHazComments.getText().trim() == null
								|| AddMoreParentHazComments.getText().trim()
										.length() == 0) {
							hasError = true;
							JOptionPane.showMessageDialog(this,
									"Parent hazard comments is required.");
							break;
						}
					}
				}
			}
		}
		return hasError;
	}

	private String getHazardCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getHazardCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null,
					"Look up code failed.", e);
		}
		return codeString;
	}

	private String getHandlingCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getHandlingCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null,
					"Look up code failed.", e);
		}
		return codeString;
	}

	private String getStorageCodeFromDecr(String decr) {
		String codeString = "";
		try {
			codeString = CodeTableCache.getCache().getStorageCode(decr);
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null,
					"Look up code failed.", e);
		}
		return codeString;
	}

	/**
	 * 
	 */
	private void updateStorageInfo() {
		JCheckBox itemJCheckBox = new JCheckBox();
		getSelectedBatch().getRegInfo().getCompoundRegistrationStorageCodes()
				.clear();
		for (int i = 0; i < storageCheckBoxList.size(); i++) {
			itemJCheckBox = (JCheckBox) storageCheckBoxList.get(i);
			if (itemJCheckBox.isSelected()) {
				getSelectedBatch()
						.getRegInfo()
						.getCompoundRegistrationStorageCodes()
						.add(getStorageCodeFromDecr(itemJCheckBox.getText()
								.trim()));
			}
		}
		getSelectedBatch().setStorageComments(
				jTextAreaOtherStorage.getText().trim());
	}

	/**
	 * 
	 */
	private void updateHandlingInfo() {
		JCheckBox itemJCheckBox = new JCheckBox();
		getSelectedBatch().getRegInfo().getCompoundRegistrationHandlingCodes()
				.clear();
		for (int i = 0; i < handlingCheckBoxList.size(); i++) {
			itemJCheckBox = (JCheckBox) handlingCheckBoxList.get(i);
			if (itemJCheckBox.isSelected()) {
				getSelectedBatch()
						.getRegInfo()
						.getCompoundRegistrationHandlingCodes()
						.add(getHandlingCodeFromDecr(itemJCheckBox.getText()
								.trim()));
			}
		}
		getSelectedBatch().setHandlingComments(
				jTextAreaOtherHandling.getText().trim());
	}

	/**
	 * 
	 */
	private void updateHazardInfo() {
		JCheckBox itemJCheckBox = new JCheckBox();
		getSelectedBatch().getRegInfo().getCompoundRegistrationHazardCodes()
				.clear();
		for (int i = 0; i < hazardCheckBoxList.size(); i++) {
			itemJCheckBox = (JCheckBox) hazardCheckBoxList.get(i);
			if (itemJCheckBox.isSelected()) {
				getSelectedBatch()
						.getRegInfo()
						.getCompoundRegistrationHazardCodes()
						.add(getHazardCodeFromDecr(itemJCheckBox.getText()
								.trim()));
			}
		}
		getSelectedBatch().setHazardComments(
				AddMoreBatchHazardComments.getText());
		getSelectedBatch().getCompound().setHazardComments(
				AddMoreParentHazComments.getText());
		getSelectedBatch().setModelChanged(true);
		pageModel.setModelChanged(true);
	}

	/**
	 * @return Returns the handlingString.
	 */
	public String getHandlingString() {
		return handlingString;
	}

	/**
	 * @param handlingString
	 *            The handlingString to set.
	 */
	public void setHandlingString(String handlingString) {
		this.handlingString = handlingString;
	}

	/**
	 * @return Returns the hazardString.
	 */
	public String getHazardString() {
		return hazardString;
	}

	/**
	 * @param hazardString
	 *            The hazardString to set.
	 */
	public void setHazardString(String hazardString) {
		this.hazardString = hazardString;
	}

	/**
	 * @return Returns the storageString.
	 */
	public String getStorageString() {
		return storageString;
	}

	/**
	 * @param storageString
	 *            The storageString to set.
	 */
	public void setStorageString(String storageString) {
		this.storageString = storageString;
	}

	/**
	 * @return Returns the handlingList.
	 */
	public ArrayList getHandlingList() {
		return handlingList;
	}

	/**
	 * @param handlingList
	 *            The handlingList to set.
	 */
	public void setHandlingList(ArrayList handlingList) {
		this.handlingList = handlingList;
	}

	/**
	 * @return Returns the hazardList.
	 */
	public ArrayList getHazardList() {
		return hazardList;
	}

	/**
	 * @param hazardList
	 *            The hazardList to set.
	 */
	public void setHazardList(ArrayList hazardList) {
		this.hazardList = hazardList;
	}

	/**
	 * @return Returns the storageList.
	 */
	public ArrayList getStorageList() {
		return storageList;
	}

	/**
	 * @param storageList
	 *            The storageList to set.
	 */
	public void setStorageList(ArrayList storageList) {
		this.storageList = storageList;
	}

	/**
	 * @return Returns the handlingCodeList.
	 */
	public ArrayList getHandlingCodeList() {
		return handlingCodeList;
	}

	/**
	 * @param handlingCodeList
	 *            The handlingCodeList to set.
	 */
	public void setHandlingCodeList(ArrayList handlingCodeList) {
		this.handlingCodeList = handlingCodeList;
	}

	/**
	 * @return Returns the hazardCodeList.
	 */
	public ArrayList getHazardCodeList() {
		return hazardCodeList;
	}

	/**
	 * @param hazardCodeList
	 *            The hazardCodeList to set.
	 */
	public void setHazardCodeList(ArrayList hazardCodeList) {
		this.hazardCodeList = hazardCodeList;
	}

	/**
	 * @return Returns the parentHazardString.
	 */
	public String getParentHazardString() {
		return parentHazardString;
	}

	/**
	 * @param parentHazardString
	 *            The parentHazardString to set.
	 */
	public void setParentHazardString(String parentHazardString) {
		this.parentHazardString = parentHazardString;
	}

	/**
	 * @return Returns the storageCodeList.
	 */
	public ArrayList getStorageCodeList() {
		return storageCodeList;
	}

	/**
	 * @param storageCodeList
	 *            The storageCodeList to set.
	 */
	public void setStorageCodeList(ArrayList storageCodeList) {
		this.storageCodeList = storageCodeList;
	}

	/**
	 * @return Returns the selectedBatch.
	 */
	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * @param selectedBatch
	 *            The selectedBatch to set.
	 */
	public void setSelectedBatch(ProductBatchModel selectedBatch) {
		this.selectedBatch = selectedBatch;
	}

	/**
	 * @return Returns the handlingMap.
	 */
	public HashMap getHandlingMap() {
		return handlingMap;
	}

	/**
	 * @param handlingMap
	 *            The handlingMap to set.
	 */
	public void setHandlingMap(HashMap handlingMap) {
		this.handlingMap = handlingMap;
	}

	/**
	 * @return Returns the hazardMap.
	 */
	public HashMap getHazardMap() {
		return hazardMap;
	}

	/**
	 * @param hazardMap
	 *            The hazardMap to set.
	 */
	public void setHazardMap(HashMap hazardMap) {
		this.hazardMap = hazardMap;
	}

	/**
	 * @return Returns the storageMap.
	 */
	public HashMap getStorageMap() {
		return storageMap;
	}

	/**
	 * @param storageMap
	 *            The storageMap to set.
	 */
	public void setStorageMap(HashMap storageMap) {
		this.storageMap = storageMap;
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed() {
		if (!validateHealthHazardInfo()) {
			updateHazardInfo();
			updateHandlingInfo();
			updateStorageInfo();
			// close edit dialog
			setVisible(false);
			canceled = false;
			this.dispose();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed() {
		setVisible(false);
		this.dispose();
	}

	public boolean getHealthHazInfo() {
		canceled = true;
		populateOptions();
		setModal(true);
		setVisible(true);
		return !canceled;
	}
}
