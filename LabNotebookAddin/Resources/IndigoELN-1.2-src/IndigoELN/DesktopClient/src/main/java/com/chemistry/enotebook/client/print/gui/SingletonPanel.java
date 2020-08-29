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
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.analytical.InstrumentTypesDialog;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingletonPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8725199423608750334L;

	private static final Log log = LogFactory.getLog(SingletonPanel.class);
	
	private SingletonPrintOptions printOptions;
	private List<String> instrumentTypes = new ArrayList<String>();
	private JDialog parent;
	private JCheckBox expSubjectTitleCB;
	private JCheckBox reactionSchemeCB;
	private JCheckBox stoicTableCB;           
	private JCheckBox procedureCB;
	private JCheckBox batchDetailsCB;
	private JCheckBox analyticalSummaryCB;
	private JCheckBox analyticalServiceFilesCB;
	private JRadioButton allInstrTypes;
	private JRadioButton selectInstrTypes;
	private JButton selectTypesButton;
	private JTextField typesText;
	private JCheckBox regSummaryCB;
	private JCheckBox regDetailsCB;
	private JCheckBox submissionDetailsCB;
	private JCheckBox hazardsCB;
	private JRadioButton allAttachments; 
	private JRadioButton noAttachments; 
	private JCheckBox signatureFooterCB;

	public SingletonPanel(JDialog parent, List<String> types, SingletonPrintOptions printOptions) {
		this.parent = parent;
		this.instrumentTypes = types;
		this.printOptions = printOptions;
		this.initGui();
		this.updateDisplay();
	}
	
	private void initGui() {
		FormLayout layout = new FormLayout("left:10dlu, left:pref:grow, 10dlu", "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu");
		
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		
		builder.add(getIncludeExpContentsPanel(),  cc.xy(2, 2));
		builder.add(getIncludeBatchContentsPanel(),  cc.xy(2, 4));
		builder.add(getIncludeAnalyticalContentsPanel(),  cc.xy(2, 6));
		builder.add(getIncludeRegAndSubContentsPanel(),  cc.xy(2, 8));
		builder.add(getIncludeAttachmentsPanel(),  cc.xy(2, 10));
		builder.add(getIncludeFooterPanel(),  cc.xy(2, 12));
		
		add(builder.getPanel(), BorderLayout.CENTER);	
		
		expSubjectTitleCB.addActionListener(this);
		reactionSchemeCB.addActionListener(this);
		stoicTableCB.addActionListener(this);
		procedureCB.addActionListener(this);
		batchDetailsCB.addActionListener(this);
		analyticalSummaryCB.addActionListener(this);
		analyticalServiceFilesCB.addActionListener(this);
		allInstrTypes.addActionListener(this);
		selectInstrTypes.addActionListener(this);
		regSummaryCB.addActionListener(this);
		regDetailsCB.addActionListener(this);
		submissionDetailsCB.addActionListener(this);
		hazardsCB.addActionListener(this);
		signatureFooterCB.addActionListener(this);
		allAttachments.addActionListener(this);
		noAttachments.addActionListener(this);
	}
	
	private JPanel getIncludeAnalyticalContentsPanel() {
		JPanel includeAnalyticalContentsPanel = new JPanel();
		includeAnalyticalContentsPanel.setPreferredSize(new java.awt.Dimension(730, 100));
		includeAnalyticalContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Analytical Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref:grow, 3dlu, pref:grow, 3dlu, pref:grow, 3dlu, pref:grow, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		layout.setColumnGroups(new int[][] {{2,4,6,8}});
		CellConstraints cc = new CellConstraints();
		analyticalSummaryCB = PrintSetupGuiUtils.getCheckBox("Analytical Summary", false, true);
		analyticalServiceFilesCB = PrintSetupGuiUtils.getCheckBox("AnalyticalService Data Files", false, true);
		allInstrTypes = PrintSetupGuiUtils.getRadioButton("All Instrument Types", true, true); 
		selectInstrTypes = PrintSetupGuiUtils.getRadioButton("Types", false, true); 
		allInstrTypes.addActionListener(new AnalyticalInstrActionListener());
		selectInstrTypes.addActionListener(new AnalyticalInstrActionListener());
		ButtonGroup structureButtonGroup = new ButtonGroup();
		structureButtonGroup.add(allInstrTypes);
		structureButtonGroup.add(selectInstrTypes);
		typesText = PrintSetupGuiUtils.getTextField("", 20, false);
		
		selectTypesButton = PrintSetupGuiUtils.getButton("Select Types", true);
		selectTypesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<String> selInstrTypes = null;
					try {
						selInstrTypes = Arrays.asList(StringUtils.split(typesText.getText(), ','));
					} catch (Exception unusableException) {
						// Just ignore
					}
					InstrumentTypesDialog sd = new InstrumentTypesDialog(parent, instrumentTypes, selInstrTypes);
					sd.setVisible(true);
					if (sd.isDialogClosed()) {
						typesText.setText(sd.getInsTypesText());
						if (parent instanceof PrintExperimentSetup) {
							((PrintExperimentSetup) parent).enableSaveButton();
						}
					}
				} catch (Exception ex) {
					CeNErrorHandler.getInstance().logExceptionMsg(parent, ex);
					log.error("", ex);
				}
			}
		});
		
		builder.add(analyticalSummaryCB, cc.xy(2, 2));
		builder.add(analyticalServiceFilesCB, cc.xy(4, 2));
		builder.add(allInstrTypes, cc.xy(6, 2));
		builder.add(selectInstrTypes, cc.xy(6, 4));
		builder.add(selectTypesButton, cc.xy(8, 2));
		builder.add(typesText, cc.xy(8, 4));
		JPanel builderPanel = builder.getPanel();
		includeAnalyticalContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeAnalyticalContentsPanel.add(builderPanel);
		return includeAnalyticalContentsPanel;
	}
	
	private JPanel getIncludeBatchContentsPanel() {
		JPanel includeBatchContentsPanel = new JPanel();
		includeBatchContentsPanel.setPreferredSize(new java.awt.Dimension(730, 70));
		includeBatchContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Batch Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref:grow, 3dlu", "3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		batchDetailsCB = PrintSetupGuiUtils.getCheckBox("Batch Data/Structure Details", false, true);
		builder.add(batchDetailsCB, cc.xy(2, 2));
		JPanel builderPanel = builder.getPanel();
		includeBatchContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeBatchContentsPanel.add(builderPanel);
		return includeBatchContentsPanel;
	}

	private JPanel getIncludeFooterPanel() {
		JPanel includeFooterContentsPanel = new JPanel();
		includeFooterContentsPanel.setPreferredSize(new java.awt.Dimension(730, 70));
		includeFooterContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Signature Footer: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref:grow, 3dlu", "3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		signatureFooterCB = PrintSetupGuiUtils.getCheckBox("Signature and Witness Footer", false, true);
		builder.add(signatureFooterCB, cc.xy(2, 2));
		JPanel builderPanel = builder.getPanel();
		includeFooterContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeFooterContentsPanel.add(builderPanel);
		return includeFooterContentsPanel;
	}
	
	private JPanel getIncludeExpContentsPanel() {
		JPanel includeExpContentsPanel = new JPanel();
		includeExpContentsPanel.setPreferredSize(new java.awt.Dimension(730, 100));
		includeExpContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Experiment Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu");
		layout.setColumnGroups(new int[][] {{2,4,6,8}});
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		expSubjectTitleCB = PrintSetupGuiUtils.getCheckBox("Experiment Subject/Title", false, true);
		reactionSchemeCB = PrintSetupGuiUtils.getCheckBox("Reaction Scheme", false, true);
		stoicTableCB = PrintSetupGuiUtils.getCheckBox("Stoichiometry Table", false, true);                 
		procedureCB = PrintSetupGuiUtils.getCheckBox("Reaction & Workup Procedure", false, true); 
		builder.add(expSubjectTitleCB, cc.xy(2, 2));
		builder.add(reactionSchemeCB, cc.xy(4, 2));
		builder.add(stoicTableCB, cc.xy(6, 2));
		builder.add(procedureCB, cc.xy(2, 4));
		JPanel builderPanel = builder.getPanel();
		includeExpContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeExpContentsPanel.add(builderPanel);
		return includeExpContentsPanel;
	}
	
	private JPanel getIncludeRegAndSubContentsPanel() {
		JPanel includeRegContentsPanel = new JPanel();
		includeRegContentsPanel.setPreferredSize(new java.awt.Dimension(730, 70));
		includeRegContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Registration and Submission Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		layout.setColumnGroups(new int[][] {{2,4,6,8}});
		CellConstraints cc = new CellConstraints();
		regSummaryCB = PrintSetupGuiUtils.getCheckBox("Reg./Sub. Summary", false, true);
		regDetailsCB = PrintSetupGuiUtils.getCheckBox("Registration Details", false, true);
		submissionDetailsCB = PrintSetupGuiUtils.getCheckBox("Sub. Details", false, true);
		hazardsCB = PrintSetupGuiUtils.getCheckBox("Hazards/Handling/Storage", false, true);	
		builder.add(regSummaryCB, cc.xy(2, 2));
		builder.add(regDetailsCB, cc.xy(4, 2));
		builder.add(submissionDetailsCB, cc.xy(6, 2));
		builder.add(hazardsCB, cc.xy(8, 2));
		JPanel builderPanel = builder.getPanel();
		includeRegContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeRegContentsPanel.add(builderPanel);
		return includeRegContentsPanel;		
	}
	
	private JPanel getIncludeAttachmentsPanel() {
		JPanel includeAttachmentsContentsPanel = new JPanel();
		includeAttachmentsContentsPanel.setPreferredSize(new java.awt.Dimension(730, 70));
		includeAttachmentsContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Attachments: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref, 3dlu, pref, 3dlu", "3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		allAttachments = PrintSetupGuiUtils.getRadioButton("All IP Related Attachments", true, true); 
		noAttachments = PrintSetupGuiUtils.getRadioButton("None", false, true); 
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(allAttachments);
		buttonGroup.add(noAttachments);
		builder.add(allAttachments, cc.xy(2, 2));
		builder.add(noAttachments, cc.xy(4, 2));
		JPanel builderPanel = builder.getPanel();
		includeAttachmentsContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeAttachmentsContentsPanel.add(builderPanel);
		return includeAttachmentsContentsPanel;
	}
	
	public void updateDisplay() {
		this.expSubjectTitleCB.setSelected(printOptions.includeSubjectTitle);
		this.reactionSchemeCB.setSelected(printOptions.includeReactionScheme);
		this.stoicTableCB.setSelected(printOptions.includeStoichTable);
		this.procedureCB.setSelected(printOptions.includeProcedure);
		this.batchDetailsCB.setSelected(printOptions.includeBatchDetails);
		this.analyticalSummaryCB.setSelected(printOptions.includeAnalyticalSummary);
		this.analyticalServiceFilesCB.setSelected(printOptions.includeAnalyticalFiles);
		this.allInstrTypes.setSelected(printOptions.includeAnalyticalAllInstruments);
		this.selectInstrTypes.setSelected(printOptions.includeAnalyticalSelectInstruments);
		if (this.allInstrTypes.isSelected()) {
			this.selectTypesButton.setEnabled(false);
		} else {
			this.selectTypesButton.setEnabled(true);
		}
		this.regSummaryCB.setSelected(printOptions.includeRegSummary);
		this.regDetailsCB.setSelected(printOptions.includeRegDetails);
		this.submissionDetailsCB.setSelected(printOptions.includeRegSubmissionDetails);
		this.hazardsCB.setSelected(printOptions.includeRegHazards);
		this.signatureFooterCB.setSelected(printOptions.includeSignatureFooter);
        this.allAttachments.setSelected(printOptions.attachAll);
        this.noAttachments.setSelected(printOptions.attachNone);
        this.typesText.setText(printOptions.analyticalInstrumentsDesc);
	}

	public void updatePrintOptions() {
		printOptions.includeSubjectTitle = this.expSubjectTitleCB.isSelected();
		printOptions.includeReactionScheme = this.reactionSchemeCB.isSelected();
		printOptions.includeStoichTable = this.stoicTableCB.isSelected();
		printOptions.includeProcedure = this.procedureCB.isSelected();
		printOptions.includeBatchDetails = this.batchDetailsCB.isSelected();
		printOptions.includeAnalyticalSummary = this.analyticalSummaryCB.isSelected();
		printOptions.includeAnalyticalFiles = this.analyticalServiceFilesCB.isSelected();
		printOptions.includeAnalyticalAllInstruments = this.allInstrTypes.isSelected();
		printOptions.includeAnalyticalSelectInstruments = this.selectInstrTypes.isSelected();
		printOptions.analyticalInstrumentsDesc = this.typesText.getText();
		printOptions.includeRegSummary = this.regSummaryCB.isSelected();
		printOptions.includeRegDetails = this.regDetailsCB.isSelected();
		printOptions.includeRegSubmissionDetails = this.submissionDetailsCB.isSelected();
		printOptions.includeRegHazards = this.hazardsCB.isSelected();
		printOptions.includeSignatureFooter = this.signatureFooterCB.isSelected();
        printOptions.attachAll = this.allAttachments.isSelected();
        printOptions.attachNone = this.noAttachments.isSelected();
	}

	public SingletonPrintOptions getPrintOptions() {
		return printOptions;
	}

	public void setPrintOptions(SingletonPrintOptions printOptions) {
		this.printOptions = printOptions;
	}
	
	class AnalyticalInstrActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().indexOf("All") >= 0) {
				selectTypesButton.setEnabled(false);
				typesText.setText("");
			} else
				selectTypesButton.setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JCheckBox || source instanceof JRadioButton) {
			if (parent instanceof PrintExperimentSetup) {
				((PrintExperimentSetup) parent).enableSaveButton();
			}
		}
	}
}
