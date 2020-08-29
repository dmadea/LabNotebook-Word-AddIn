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
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParallelPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 478751277810059632L;

	private static final Log log = LogFactory.getLog(ParallelPanel.class);

	private ParallelPrintOptions printOptions;
	private List<String> instrumentTypes = new ArrayList<String>();
	private JDialog parent;
	private JCheckBox expSubjectTitleCB;
	private JCheckBox reactionSchemeCB;
	private JCheckBox summaryReactionCB;
	private StepChooserPanel reactionStepChooserPanel;
	private JCheckBox stoicTableCB; 
	private StepChooserPanel stoicTableStepChooserPanel;
	private JCheckBox monomerBatchesCB;
	private StepChooserPanel monomerBatchesStepChooserPanel;
	private JCheckBox procedureCB;
	private JCheckBox productBatchDetailsCB;
	private StepChooserPanel productBatchStepChooserPanel;
	private JCheckBox analyticalSummaryCB;
	private JCheckBox analyticalServiceFilesCB;
	private JRadioButton allInstrTypes;
	private JRadioButton selectInstrTypes;
	private JButton selectTypesButton;
	private JTextField typesText;
	private JCheckBox regSummaryCB;
//	private JCheckBox regDetailsCB;
//	private JCheckBox submissionDetailsCB;
//	private JCheckBox hazardsCB;
	private JRadioButton allAttachments; 
	private JRadioButton noAttachments; 
	private JCheckBox signatureFooterCB;
	
	public ParallelPanel(JDialog parent, List<String> types, ParallelPrintOptions printOptions) {
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
		summaryReactionCB.addActionListener(this);
		reactionStepChooserPanel.allStepsRB.addActionListener(this);
		reactionStepChooserPanel.finalStepRB.addActionListener(this);
		reactionStepChooserPanel.stepsRB.addActionListener(this);
		stoicTableCB.addActionListener(this);
		stoicTableStepChooserPanel.allStepsRB.addActionListener(this);
		stoicTableStepChooserPanel.finalStepRB.addActionListener(this);
		stoicTableStepChooserPanel.stepsRB.addActionListener(this);	
		monomerBatchesCB.addActionListener(this);
		monomerBatchesStepChooserPanel.allStepsRB.addActionListener(this);
		monomerBatchesStepChooserPanel.finalStepRB.addActionListener(this);
		monomerBatchesStepChooserPanel.stepsRB.addActionListener(this);	
		productBatchDetailsCB.addActionListener(this);
		productBatchStepChooserPanel.allStepsRB.addActionListener(this);
		productBatchStepChooserPanel.finalStepRB.addActionListener(this);
		productBatchStepChooserPanel.stepsRB.addActionListener(this);
		procedureCB.addActionListener(this);
		analyticalSummaryCB.addActionListener(this);
		analyticalServiceFilesCB.addActionListener(this);
		allInstrTypes.addActionListener(this);
		selectInstrTypes.addActionListener(this);
		regSummaryCB.addActionListener(this);
//		regDetailsCB.addActionListener(this);
//		submissionDetailsCB.addActionListener(this);
//		hazardsCB.addActionListener(this);
		signatureFooterCB.addActionListener(this);
		allAttachments.addActionListener(this);
	}
	
	private JPanel getIncludeExpContentsPanel() {
		JPanel includeExpContentsPanel = new JPanel();
		includeExpContentsPanel.setPreferredSize(new Dimension(730, 160));
		includeExpContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Experiment Contents: "));
		FormLayout layout = new FormLayout("left:3dlu, pref, 3dlu, 80dlu, 80dlu, 40dlu, 120dlu, 3dlu", "pref, 30dlu, 15dlu, 15dlu, 3dlu");
		//layout.setColumnGroups(new int[][] {{2,4}});
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel()); 
		CellConstraints cc = new CellConstraints();
		expSubjectTitleCB = PrintSetupGuiUtils.getCheckBox("Experiment Subject/Title", false, true);
		reactionSchemeCB = PrintSetupGuiUtils.getCheckBox("Reaction Scheme", false, true);
		summaryReactionCB = PrintSetupGuiUtils.getCheckBox("Summary Reaction", false, true);
		
		JPanel reactionPanel = new JPanel();
		reactionPanel.setBorder(new EtchedBorder());
		reactionPanel.getInsets().set(0,0,0,0);
		FormLayout rxnPanelLayout = new FormLayout("left:pref, pref", "15dlu, 15dlu");
		PanelBuilder rxnPanelBuilder = new PanelBuilder(rxnPanelLayout); //, new FormDebugPanel());
		rxnPanelBuilder.add(summaryReactionCB, cc.xy(1, 1));
		reactionStepChooserPanel = new StepChooserPanel(this.printOptions.includeReactionSchemeAllSteps,
														this.printOptions.includeReactionSchemeFinalStep,
														this.printOptions.includeReactionSchemeSelectSteps,
														this.printOptions.reactionSchemeSelectedStepsDesc);
		
		if (this.printOptions.includeReactionSchemes) {
			reactionStepChooserPanel.enable(true);
		} else {
			reactionStepChooserPanel.enable(false);
		}
		
		summaryReactionCB.setEnabled(false);
		rxnPanelBuilder.add(reactionStepChooserPanel, cc.xy(1, 2));
		reactionPanel.add(rxnPanelBuilder.getPanel());

		reactionSchemeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					reactionStepChooserPanel.enable(true);
					summaryReactionCB.setEnabled(true);
				} else {
					reactionStepChooserPanel.enable(false);
					summaryReactionCB.setEnabled(false);
				}
			}
		});
		
		stoicTableCB = PrintSetupGuiUtils.getCheckBox("Stoichiometry", false, true);    
		stoicTableStepChooserPanel = new StepChooserPanel(this.printOptions.includeStoicTableAllSteps,
														  this.printOptions.includeStoicTableFinalStep,
														  this.printOptions.includeStoicTableSelectSteps,
														  this.printOptions.stoicTableSelectedStepsDesc);
		stoicTableStepChooserPanel.enable(false);
		
		stoicTableCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					stoicTableStepChooserPanel.enable(true);
				} else {
					stoicTableStepChooserPanel.enable(false);
				}
			}
		});
		
		monomerBatchesCB = PrintSetupGuiUtils.getCheckBox("Monomer Plates", false, true); 
		monomerBatchesStepChooserPanel = new StepChooserPanel(this.printOptions.includeMonomerBatchesAllSteps,
															  this.printOptions.includeMonomerBatchesFinalStep,
															  this.printOptions.includeMonomerBatchesSelectSteps,
															  this.printOptions.monomerBatchesSelectedStepsDesc);
		monomerBatchesStepChooserPanel.enable(false);
		 monomerBatchesCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					monomerBatchesStepChooserPanel.enable(true);
				} else {
					monomerBatchesStepChooserPanel.enable(false);
				}
			}
		});

		procedureCB = PrintSetupGuiUtils.getCheckBox("Reaction & Workup Procedure", false, true); 

		builder.add(expSubjectTitleCB, cc.xy(2, 1));
		builder.add(reactionSchemeCB, cc.xy(4, 1));
		builder.add(reactionPanel, cc.xywh(5, 1, 3, 2));
		builder.add(stoicTableCB, cc.xy(2, 3));
		builder.add(stoicTableStepChooserPanel, cc.xywh(4, 3, 3, 1));
		builder.add(monomerBatchesCB, cc.xy(2, 4));
		builder.add(monomerBatchesStepChooserPanel, cc.xywh(4, 4, 3, 1));
		builder.add(procedureCB, cc.xy(7, 4));

		JPanel builderPanel = builder.getPanel();
		includeExpContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeExpContentsPanel.add(builderPanel);
		return includeExpContentsPanel;
	}
	
	private JPanel getIncludeBatchContentsPanel() {
		JPanel includeBatchContentsPanel = new JPanel();
		includeBatchContentsPanel.setPreferredSize(new Dimension(730, 70));
		includeBatchContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Batch Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, pref, pref, 3dlu", "pref");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		productBatchDetailsCB = PrintSetupGuiUtils.getCheckBox("ProductBatches", false, true);
		builder.add(productBatchDetailsCB, cc.xy(2, 1));
		
		productBatchStepChooserPanel = new StepChooserPanel(this.printOptions.includeProductBatchesAllSteps,
															this.printOptions.includeProductBatchesFinalStep,
															this.printOptions.includeProductBatchesSelectSteps,
															this.printOptions.productBatchesSelectedStepsDesc);
		productBatchStepChooserPanel.enable(false);
		productBatchDetailsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					productBatchStepChooserPanel.enable(true);
				} else {
					productBatchStepChooserPanel.enable(false);
				}
			}
		});
		
		builder.add(productBatchStepChooserPanel, cc.xy(3, 1));
		
		JPanel builderPanel = builder.getPanel();
		includeBatchContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeBatchContentsPanel.add(builderPanel);
		return includeBatchContentsPanel;
	}
	
	private JPanel getIncludeAnalyticalContentsPanel() {
		JPanel includeAnalyticalContentsPanel = new JPanel();
		includeAnalyticalContentsPanel.setPreferredSize(new Dimension(730, 100));
		includeAnalyticalContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Analytical Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref:grow, 3dlu, pref:grow, 3dlu, pref:grow, 3dlu, pref:grow, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		layout.setColumnGroups(new int[][] {{2,4,6,8}});
		CellConstraints cc = new CellConstraints();
		analyticalSummaryCB = PrintSetupGuiUtils.getCheckBox("Analytical Summary", false, true);
		analyticalServiceFilesCB = PrintSetupGuiUtils.getCheckBox("AnalyticalService Data Files", false, true);
		analyticalServiceFilesCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					allInstrTypes.setEnabled(true);
					selectInstrTypes.setEnabled(true);
				} else {
					allInstrTypes.setEnabled(false);
					selectInstrTypes.setEnabled(false);
				}
			}
		});
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
	
	private JPanel getIncludeFooterPanel() {
		JPanel includeFooterContentsPanel = new JPanel();
		includeFooterContentsPanel.setPreferredSize(new Dimension(730, 70));
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
	
	private JPanel getIncludeRegAndSubContentsPanel() {
		JPanel includeRegContentsPanel = new JPanel();
		includeRegContentsPanel.setPreferredSize(new Dimension(730, 70));
		includeRegContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Registration and Submission Contents: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu", "3dlu, pref, 3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		layout.setColumnGroups(new int[][] {{2,4,6,8}});
		CellConstraints cc = new CellConstraints();
		this.regSummaryCB = PrintSetupGuiUtils.getCheckBox("Reg./Sub. Summary", false, true);
//		this.regDetailsCB = PrintSetupGuiUtils.getCheckBox("Registration Details", false, PrintSetupGuiUtils.EDIT_FONT_SMALL, true);
//		this.submissionDetailsCB = PrintSetupGuiUtils.getCheckBox("Sub. Details", false, PrintSetupGuiUtils.EDIT_FONT_SMALL, true);
//		this.hazardsCB = PrintSetupGuiUtils.getCheckBox("Hazards/Handling/Storage", false, PrintSetupGuiUtils.EDIT_FONT_SMALL, true);	
		builder.add(regSummaryCB, cc.xy(2, 2));
//		builder.add(regDetailsCB, cc.xy(4, 2));
//		builder.add(submissionDetailsCB, cc.xy(6, 2));
//		builder.add(hazardsCB, cc.xy(8, 2));
		JPanel builderPanel = builder.getPanel();
		includeRegContentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		includeRegContentsPanel.add(builderPanel);
		return includeRegContentsPanel;		
	}
	
	private JPanel getIncludeAttachmentsPanel() {
		JPanel includeAttachmentsContentsPanel = new JPanel();
		includeAttachmentsContentsPanel.setPreferredSize(new Dimension(730, 70));
		includeAttachmentsContentsPanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include Attachments: "));
		FormLayout layout = new FormLayout("left:20dlu, left:pref, 3dlu, pref, 3dlu", "3dlu, pref, 3dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		this.allAttachments = PrintSetupGuiUtils.getRadioButton("All IP Related Attachments", true, true); 
		this.noAttachments = PrintSetupGuiUtils.getRadioButton("None", false, true); 
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
		this.expSubjectTitleCB.setSelected(printOptions.includeDetails);
		this.reactionSchemeCB.setSelected(printOptions.includeReactionSchemes);
		this.summaryReactionCB.setSelected(printOptions.includeReactionSchemeSummary);
		if (this.reactionSchemeCB.isSelected()) {
			reactionStepChooserPanel.enable(true);
			summaryReactionCB.setEnabled(true);
		} else {
			reactionStepChooserPanel.enable(false);
			summaryReactionCB.setEnabled(false);
		}
		this.reactionStepChooserPanel.allStepsRB.setSelected(printOptions.includeReactionSchemeAllSteps);
		this.reactionStepChooserPanel.finalStepRB.setSelected(printOptions.includeReactionSchemeFinalStep);
		this.reactionStepChooserPanel.stepsRB.setSelected(printOptions.includeReactionSchemeSelectSteps);
		this.reactionStepChooserPanel.stepNumbersTF.setText(printOptions.reactionSchemeSelectedStepsDesc);
		this.stoicTableCB.setSelected(printOptions.includeStoicTable);
		this.stoicTableStepChooserPanel.allStepsRB.setSelected(printOptions.includeStoicTableAllSteps);
		this.stoicTableStepChooserPanel.finalStepRB.setSelected(printOptions.includeStoicTableFinalStep);
		this.stoicTableStepChooserPanel.stepsRB.setSelected(printOptions.includeStoicTableSelectSteps);
		this.stoicTableStepChooserPanel.stepNumbersTF.setText(printOptions.stoicTableSelectedStepsDesc);	
		if (this.stoicTableCB.isSelected()) {
			this.stoicTableStepChooserPanel.enable(true);
		} else {
			this.stoicTableStepChooserPanel.enable(false);
		}
		this.monomerBatchesCB.setSelected(printOptions.includeMonomerBatches);
		this.monomerBatchesStepChooserPanel.allStepsRB.setSelected(printOptions.includeMonomerBatchesAllSteps);
		this.monomerBatchesStepChooserPanel.finalStepRB.setSelected(printOptions.includeMonomerBatchesFinalStep);
		this.monomerBatchesStepChooserPanel.stepsRB.setSelected(printOptions.includeMonomerBatchesSelectSteps);
		this.monomerBatchesStepChooserPanel.stepNumbersTF.setText(printOptions.monomerBatchesSelectedStepsDesc);	
		if (this.monomerBatchesCB.isSelected()) {
			this.monomerBatchesStepChooserPanel.enable(true);
		} else {
			this.monomerBatchesStepChooserPanel.enable(false);
		}
		this.productBatchDetailsCB.setSelected(printOptions.includeProductBatches);
		this.productBatchStepChooserPanel.allStepsRB.setSelected(printOptions.includeProductBatchesAllSteps);
		this.productBatchStepChooserPanel.finalStepRB.setSelected(printOptions.includeProductBatchesFinalStep);
		this.productBatchStepChooserPanel.stepsRB.setSelected(printOptions.includeProductBatchesFinalStep);
		this.productBatchStepChooserPanel.stepNumbersTF.setText(printOptions.productBatchesSelectedStepsDesc);
		if (this.productBatchDetailsCB.isSelected()) {
			this.productBatchStepChooserPanel.enable(true);
		} else {
			this.productBatchStepChooserPanel.enable(false);
		}
		this.procedureCB.setSelected(printOptions.includeProcedure);
		this.analyticalSummaryCB.setSelected(printOptions.includeAnalyticalSummary);
		this.analyticalServiceFilesCB.setSelected(printOptions.includeAnalyticalFiles);
		if (this.analyticalServiceFilesCB.isSelected()) {
			this.allInstrTypes.setEnabled(true);
			this.selectInstrTypes.setEnabled(true);
		} else {
			this.allInstrTypes.setEnabled(false);
			this.selectInstrTypes.setEnabled(false);			
		}
		this.allInstrTypes.setSelected(printOptions.includeAllAnalyticalServiceInstruments);
		if (this.allInstrTypes.isSelected()) {
			this.selectTypesButton.setEnabled(false);
		} else {
			this.selectTypesButton.setEnabled(true);
		}
		this.selectInstrTypes.setSelected(printOptions.includeSelectedAnalyticalServiceInstruments);
		this.regSummaryCB.setSelected(printOptions.includeRegSummary);
//		this.regDetailsCB.setSelected(printOptions.includeRegDetails);
//		this.submissionDetailsCB.setSelected(printOptions.includeRegSubmissionDetails);
//		this.hazardsCB.setSelected(printOptions.includeRegHazards);
		this.signatureFooterCB.setSelected(printOptions.includeSignatureFooter);
	}

	public void updatePrintOptions() {
		printOptions.includeDetails = this.expSubjectTitleCB.isSelected();
		printOptions.includeReactionSchemes = this.reactionSchemeCB.isSelected();
		printOptions.includeReactionSchemeSummary = this.summaryReactionCB.isSelected();
		printOptions.includeReactionSchemeAllSteps = this.reactionStepChooserPanel.allStepsRB.isSelected();
		printOptions.includeReactionSchemeFinalStep = this.reactionStepChooserPanel.finalStepRB.isSelected();
		printOptions.includeReactionSchemeSelectSteps = this.reactionStepChooserPanel.stepsRB.isSelected();
		printOptions.reactionSchemeSelectedStepsDesc = this.reactionStepChooserPanel.stepNumbersTF.getText();
		printOptions.reactionSchemeSelectedStepsList = this.reactionStepChooserPanel.getSelectedSteps();
		printOptions.includeStoicTable = this.stoicTableCB.isSelected();
		printOptions.includeStoicTableAllSteps = this.stoicTableStepChooserPanel.allStepsRB.isSelected();
		printOptions.includeStoicTableFinalStep = this.stoicTableStepChooserPanel.finalStepRB.isSelected();
		printOptions.includeStoicTableSelectSteps = this.stoicTableStepChooserPanel.stepsRB.isSelected();
		printOptions.stoicTableSelectedStepsDesc = this.stoicTableStepChooserPanel.stepNumbersTF.getText();	
		printOptions.includeMonomerBatches = this.monomerBatchesCB.isSelected();
		printOptions.includeMonomerBatchesAllSteps = this.monomerBatchesStepChooserPanel.allStepsRB.isSelected();
		printOptions.includeMonomerBatchesFinalStep = this.monomerBatchesStepChooserPanel.finalStepRB.isSelected();
		printOptions.includeMonomerBatchesSelectSteps = this.monomerBatchesStepChooserPanel.stepsRB.isSelected();
		printOptions.monomerBatchesSelectedStepsDesc = this.monomerBatchesStepChooserPanel.stepNumbersTF.getText();		
		printOptions.includeProductBatches = this.productBatchDetailsCB.isSelected();
		printOptions.includeProductBatchesAllSteps = this.productBatchStepChooserPanel.allStepsRB.isSelected();
		printOptions.includeProductBatchesFinalStep = this.productBatchStepChooserPanel.finalStepRB.isSelected();
		printOptions.includeProductBatchesSelectSteps = this.productBatchStepChooserPanel.stepsRB.isSelected();
		printOptions.productBatchesSelectedStepsDesc = this.productBatchStepChooserPanel.stepNumbersTF.getText();
		printOptions.includeProcedure = this.procedureCB.isSelected();
		printOptions.includeAnalyticalSummary = this.analyticalSummaryCB.isSelected();
		printOptions.includeAnalyticalFiles = this.analyticalServiceFilesCB.isSelected();
		printOptions.includeAllAnalyticalServiceInstruments = this.allInstrTypes.isSelected();
		printOptions.includeSelectedAnalyticalServiceInstruments = this.selectInstrTypes.isSelected();
		printOptions.analyticalInstrumentsDesc = this.typesText.getText();
		printOptions.includeRegSummary = this.regSummaryCB.isSelected();
//		printOptions.includeRegDetails = this.regDetailsCB.isSelected();
//		printOptions.includeRegSubmissionDetails = this.submissionDetailsCB.isSelected();
//		printOptions.includeRegHazards = this.hazardsCB.isSelected();
		printOptions.includeSignatureFooter = this.signatureFooterCB.isSelected();
		printOptions.attachAll = this.allAttachments.isSelected();
	}
	
	public ParallelPrintOptions getPrintOptions() {
		return printOptions;
	}

	public void setPrintOptions(ParallelPrintOptions printOptions) {
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
