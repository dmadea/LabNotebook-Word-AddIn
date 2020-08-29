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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Offers the user checkboxes to select options for printing experiments.
 * 
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
public class PrintExperimentSetupDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = -1005090384622198690L;
	
	private JPanel jPanelAttach;
	private JPanel jPanelReg;
	private JPanel jPanelSignatureFooter;
	private JRadioButton jRadioButtonStoicNoStructures;
	private JRadioButton jRadioButtonStoicWithStructures;
	private ButtonGroup buttonGroupStoicStructures;
	private JPanel jPanelExperimentDetailsContents;
	private JPanel jPanelQc;
	private JPanel jPanelBatch;
	private ButtonGroup buttonGroupAttachments;
	private JRadioButton jRadioButtonNoAttachments;
	private JRadioButton jRadioButtonAllAttachments;
	private JLabel jLabelIncAttachments;
	private JCheckBox jCheckBoxHazHandlingDetails;
	private JCheckBox jCheckBoxSubDetails;
	private JCheckBox jCheckBoxRegDetails;
	private JCheckBox jCheckBoxRegSubSum;
	private ButtonGroup buttonGroupInstrumentTypes;
	private JLabel jLabelIncRegSub;
	private JButton jButtonSelectTypes;
	private JTextField jTextFieldInstrumentTypes;
	private JRadioButton jRadioButtonTypes;
	private JRadioButton jRadioButtonAllInstrumentTypes;
	private JLabel jLabelFor;
	private JCheckBox jCheckBoxAnalyticalServiceDataFiles;
	private JCheckBox jCheckBoxAccesibiltySum;
	private JLabel jLabelIncQCAnalData;
	private JCheckBox jCheckBoxBatchDataStrucDetails;
	private JLabel jLabelIncBatchStruc;
	private JCheckBox jCheckBoxStoichiometry;
	private JCheckBox jCheckBoxReactionProcedure;
	private JCheckBox jCheckBoxReactionSchema;
	private JCheckBox jCheckBoxExpSubject;
	private JLabel jLabelIncExpDetails;
	private JPanel jPanelMainCenter;
	private JPanel jPanelFiller2;
	private JPanel jPanelFiller;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JPanel jPanelMainSouth;
	private JLabel jLabelSignatureFooter;
	private JCheckBox jCheckBoxSignatureFooter;
	
	private PrintOptions printOptions;
	
	
	public PrintExperimentSetupDialog(PrintOptions printOptions) {
		this(null, printOptions);
	}
	
	/**
	 * @param arg0
	 * @throws java.awt.HeadlessException
	 */
	public PrintExperimentSetupDialog(Frame arg0, PrintOptions printOptions) 
	throws HeadlessException 
	{
		super(arg0);
		this.printOptions = printOptions;
		initGUI();
	}
	
	/**
	 * Initializes the GUI.
	 * Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI(){
		try {
			preInitGUI();
			buttonGroupInstrumentTypes = new ButtonGroup();
			
			buttonGroupAttachments = new ButtonGroup();
			
			buttonGroupStoicStructures = new ButtonGroup();
			jPanelMainSouth = new JPanel();
			jPanelFiller = new JPanel();
			jButtonOk = new JButton();
			jPanelFiller2 = new JPanel();
			jButtonCancel = new JButton();
			jPanelMainCenter = new JPanel();
			jLabelIncExpDetails = new JLabel();
			jCheckBoxExpSubject = new JCheckBox();
			jCheckBoxReactionSchema = new JCheckBox();
			jCheckBoxStoichiometry = new JCheckBox();
			jCheckBoxReactionProcedure = new JCheckBox();
			jLabelIncBatchStruc = new JLabel();
			jLabelIncQCAnalData = new JLabel();
			jCheckBoxAccesibiltySum = new JCheckBox();
			jCheckBoxAnalyticalServiceDataFiles = new JCheckBox();
			jLabelFor = new JLabel();
			jRadioButtonAllInstrumentTypes = new JRadioButton();
			jRadioButtonTypes = new JRadioButton();
			jTextFieldInstrumentTypes = new JTextField();
			jButtonSelectTypes = new JButton();
			jLabelIncRegSub = new JLabel();
			jCheckBoxRegSubSum = new JCheckBox();
			jCheckBoxRegDetails = new JCheckBox();
			jCheckBoxSubDetails = new JCheckBox();
			jCheckBoxHazHandlingDetails = new JCheckBox();
			jLabelIncAttachments = new JLabel();
			jPanelBatch = new JPanel();
			FlowLayout jPanelBatchLayout = new FlowLayout();
			jPanelBatchLayout.setAlignment(FlowLayout.LEFT);
			jPanelBatchLayout.setHgap(20);
			jPanelBatch.setLayout(jPanelBatchLayout);
			jPanelQc = new JPanel();
			jPanelReg = new JPanel();
			jPanelAttach = new JPanel();
			jRadioButtonAllAttachments = new JRadioButton();
			jRadioButtonNoAttachments = new JRadioButton();
			jLabelSignatureFooter = new JLabel();
			jCheckBoxSignatureFooter = new JCheckBox();
			
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("Setup Print Contents");
			this.setResizable(false);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(632,440));
			
			jPanelMainSouth.setPreferredSize(new java.awt.Dimension(625,35));
			this.getContentPane().add(jPanelMainSouth, BorderLayout.SOUTH);
			
			jPanelFiller.setVisible(true);
			jPanelFiller.setPreferredSize(new java.awt.Dimension(245,10));
			jPanelMainSouth.add(jPanelFiller);
			
			jButtonOk.setText("OK");
			jButtonOk.setBorderPainted(true);
			jButtonOk.setFont(new java.awt.Font("sansserif",1,11));
			jButtonOk.setPreferredSize(new java.awt.Dimension(67,25));
			jPanelMainSouth.add(jButtonOk);
			jButtonOk.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
			
			jPanelFiller2.setVisible(true);
			jPanelMainSouth.add(jPanelFiller2);
			
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif",1,11));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(71,25));
			jPanelMainSouth.add(jButtonCancel);
			jButtonCancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			
			jPanelMainCenter.setLayout(null);
			jPanelMainCenter.setPreferredSize(new java.awt.Dimension(623, 315));
			this.getContentPane().add(jPanelMainCenter, BorderLayout.CENTER);
			
			jLabelIncExpDetails.setText("Include Experiment Details with these contents:");
			jLabelIncExpDetails.setForeground(new java.awt.Color(0,0,255));
			jLabelIncExpDetails.setOpaque(true);
			jLabelIncExpDetails.setBounds(27, 3, 279, 20);
			jPanelMainCenter.add(jLabelIncExpDetails);
			jLabelIncExpDetails.setFont(new java.awt.Font("Tahoma",1,11));
			
			jPanelExperimentDetailsContents = new JPanel();
			jPanelMainCenter.add(jPanelExperimentDetailsContents);
			jPanelExperimentDetailsContents.setBounds(14, 14, 602, 56);
			jPanelExperimentDetailsContents.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			jPanelExperimentDetailsContents.setLayout(null);
			
			jCheckBoxExpSubject.setText("Experiment Subject / Title");
			jCheckBoxExpSubject.setBounds(23, 7, 147, 21);
			jPanelExperimentDetailsContents.add(jCheckBoxExpSubject);
			
			jCheckBoxReactionSchema.setText("Reaction Scheme");
			jCheckBoxReactionSchema.setBounds(192, 7, 105, 21);
			jPanelExperimentDetailsContents.add(jCheckBoxReactionSchema);
			
			jCheckBoxStoichiometry.setText("Stoichiometry Table");
			jCheckBoxStoichiometry.setBounds(318, 7, 119, 21);
			jPanelExperimentDetailsContents.add(jCheckBoxStoichiometry);
			
			jRadioButtonStoicWithStructures = new JRadioButton();
			jRadioButtonStoicNoStructures = new JRadioButton();

			jPanelExperimentDetailsContents.add(jRadioButtonStoicWithStructures);
			jRadioButtonStoicWithStructures.setSelected(true);
			jRadioButtonStoicWithStructures.setText("With Structures");
			jRadioButtonStoicWithStructures.setBounds(448, 7, 140, 21);
			buttonGroupStoicStructures.add(jRadioButtonStoicWithStructures);

			jPanelExperimentDetailsContents.add(jRadioButtonStoicNoStructures);
			jRadioButtonStoicNoStructures.setSelected(false);
			jRadioButtonStoicNoStructures.setText("Without Structures");
			jRadioButtonStoicNoStructures.setBounds(448, 28, 126, 21);
			buttonGroupStoicStructures.add(jRadioButtonStoicNoStructures);
						
			if (jCheckBoxStoichiometry.isSelected()){
				jRadioButtonStoicWithStructures.setEnabled(true);
				jRadioButtonStoicNoStructures.setEnabled(true);
			}
			else
			{
				jRadioButtonStoicWithStructures.setEnabled(false);
				jRadioButtonStoicNoStructures.setEnabled(false);
			}
			
			jCheckBoxStoichiometry.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (jCheckBoxStoichiometry.isSelected()){
						jRadioButtonStoicWithStructures.setEnabled(true);
						jRadioButtonStoicNoStructures.setEnabled(true);
					}
					else
					{
						jRadioButtonStoicWithStructures.setEnabled(false);
						jRadioButtonStoicNoStructures.setEnabled(false);
					}
				}
			});
			
			jCheckBoxReactionProcedure.setText("Reaction & Workup Procedure");
			jCheckBoxReactionProcedure.setBounds(23, 28, 182, 21);
			jPanelExperimentDetailsContents.add(jCheckBoxReactionProcedure);
			
			jLabelIncBatchStruc.setText("Include Batch Data/Structures :");
			jLabelIncBatchStruc.setForeground(new java.awt.Color(0,0,255));
			jLabelIncBatchStruc.setOpaque(true);
			jLabelIncBatchStruc.setBounds(28, 72, 189, 21);
			jPanelMainCenter.add(jLabelIncBatchStruc);
			jLabelIncBatchStruc.setFont(new java.awt.Font("Tahoma",1,11));
			
			jLabelIncQCAnalData.setText("Include Analytical Data with these contents:");
			jLabelIncQCAnalData.setForeground(new java.awt.Color(0,0,255));
			jLabelIncQCAnalData.setOpaque(true);
			jLabelIncQCAnalData.setBounds(28, 121, 273, 21);
			jPanelMainCenter.add(jLabelIncQCAnalData);
			jLabelIncQCAnalData.setFont(new java.awt.Font("Tahoma",1,11));
			
			jCheckBoxAccesibiltySum.setText("Analytical Summary");
			jCheckBoxAccesibiltySum.setBounds(37, 141, 140, 20);
			jPanelMainCenter.add(jCheckBoxAccesibiltySum);
			
			jCheckBoxAnalyticalServiceDataFiles.setText("AnalyticalService Data Files");
			jCheckBoxAnalyticalServiceDataFiles.setBounds(194, 141, 111, 20);
			jPanelMainCenter.add(jCheckBoxAnalyticalServiceDataFiles);
			
			jLabelFor.setText("For");
			jLabelFor.setBounds(309, 141, 19, 20);
			jPanelMainCenter.add(jLabelFor);
			
			jRadioButtonAllInstrumentTypes.setText("All Instrument Types");
			jRadioButtonAllInstrumentTypes.setSelected(true);
			jRadioButtonAllInstrumentTypes.setBounds(339, 141, 124, 20);
			buttonGroupInstrumentTypes.add(jRadioButtonAllInstrumentTypes);
			jPanelMainCenter.add(jRadioButtonAllInstrumentTypes);
			
			jRadioButtonTypes.setText("Types:");
			jRadioButtonAllInstrumentTypes.setSelected(false);
			jRadioButtonTypes.setBounds(339, 164, 60, 20);
			buttonGroupInstrumentTypes.add(jRadioButtonTypes);
			jPanelMainCenter.add(jRadioButtonTypes);
			
			jButtonSelectTypes.setText("Select Types");
			jButtonSelectTypes.setBounds(504, 140, 97, 20);
			jPanelMainCenter.add(jButtonSelectTypes);
			jButtonSelectTypes.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonSelectTypesActionPerformed(evt);
				}
			});
			
			jCheckBoxAnalyticalServiceDataFiles.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (jCheckBoxAnalyticalServiceDataFiles.isSelected()){
						jRadioButtonAllInstrumentTypes.setEnabled(true);
						jRadioButtonTypes.setEnabled(true);
						jButtonSelectTypes.setEnabled(true);
					}
					else{
						jRadioButtonAllInstrumentTypes.setEnabled(false);
						jRadioButtonTypes.setEnabled(false);
						jButtonSelectTypes.setEnabled(false);
					}
				}
			});
			
			if (jCheckBoxAnalyticalServiceDataFiles.isSelected()){
				jRadioButtonAllInstrumentTypes.setEnabled(true);
				jRadioButtonTypes.setEnabled(true);
				jButtonSelectTypes.setEnabled(true);
			}
			else{
				jRadioButtonAllInstrumentTypes.setEnabled(false);
				jRadioButtonTypes.setEnabled(false);
				jButtonSelectTypes.setEnabled(false);
			}
			
			jTextFieldInstrumentTypes.setEnabled(false);
			jTextFieldInstrumentTypes.setText("-none-");
			jTextFieldInstrumentTypes.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jTextFieldInstrumentTypes.setBounds(404, 164, 203, 20);
			jPanelMainCenter.add(jTextFieldInstrumentTypes);
			
			jLabelIncRegSub.setText("Include Registration/Submission with these contents:");
			jLabelIncRegSub.setForeground(new java.awt.Color(0,0,255));
			jLabelIncRegSub.setOpaque(true);
			jLabelIncRegSub.setBounds(27, 198, 310, 20);
			jPanelMainCenter.add(jLabelIncRegSub);
			jLabelIncRegSub.setFont(new java.awt.Font("Tahoma",1,11));
			
			jCheckBoxRegSubSum.setText("Registration/Submission Summary");
			jCheckBoxRegSubSum.setBounds(35, 221, 182, 21);
			jPanelMainCenter.add(jCheckBoxRegSubSum);
			
//			jCheckBoxRegDetails.setText("Registration Details");
//			jCheckBoxRegDetails.setBounds(235, 221, 119, 21);
//			jPanelMainCenter.add(jCheckBoxRegDetails);
//
//			jCheckBoxSubDetails.setText("Sub. Details");
//			jCheckBoxSubDetails.setBounds(365, 221, 84, 21);
//			jPanelMainCenter.add(jCheckBoxSubDetails);
//			
//			jCheckBoxHazHandlingDetails.setText("Hazards/Handling/Storage");
//			jCheckBoxHazHandlingDetails.setVerticalTextPosition(SwingConstants.TOP);
//			jCheckBoxHazHandlingDetails.setBounds(461, 221, 147, 21);
//			jPanelMainCenter.add(jCheckBoxHazHandlingDetails);

			jLabelIncAttachments.setText("Include Attachments with these contents:");
			jLabelIncAttachments.setForeground(new java.awt.Color(0,0,255));
			jLabelIncAttachments.setOpaque(true);
			jLabelIncAttachments.setBounds(28, 262, 245, 21);
			jPanelMainCenter.add(jLabelIncAttachments);
			jLabelIncAttachments.setFont(new java.awt.Font("Tahoma",1,11));
			
			jLabelSignatureFooter.setText("Include Signature Footer:");
			jLabelSignatureFooter.setForeground(new java.awt.Color(0,0,255));
			jLabelSignatureFooter.setOpaque(true);
			jLabelSignatureFooter.setBounds(28, 321, 154, 21);
			jPanelMainCenter.add(jLabelSignatureFooter);
			jLabelSignatureFooter.setFont(new java.awt.Font("Tahoma",1,11));
			
			jCheckBoxSignatureFooter.setText("Signature & Witness Footer");
			jCheckBoxSignatureFooter.setVerticalTextPosition(SwingConstants.TOP);
			jCheckBoxSignatureFooter.setBounds(35, 341, 182, 21);
			jCheckBoxSignatureFooter.setSelected(true);
			jPanelMainCenter.add(jCheckBoxSignatureFooter);
			
			jPanelBatch.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelBatch.setBounds(15, 84, 601, 35);
			jPanelMainCenter.add(jPanelBatch);
			
			jCheckBoxBatchDataStrucDetails = new JCheckBox();
			jPanelBatch.add(jCheckBoxBatchDataStrucDetails);
			jCheckBoxBatchDataStrucDetails.setText("Batch Data/Structure Details");
			jCheckBoxBatchDataStrucDetails.setBounds(37, 90, 400, 20);
			jCheckBoxBatchDataStrucDetails.setPreferredSize(new java.awt.Dimension(183, 20));
			jCheckBoxBatchDataStrucDetails.setSize(300, 23);
			
			jPanelQc.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelQc.setBounds(15, 132, 601, 66);
			jPanelMainCenter.add(jPanelQc);
			
			jPanelReg.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelReg.setBounds(14, 210, 602, 49);
			jPanelMainCenter.add(jPanelReg);
			
			AnchorLayout jPanelAttachLayout = new AnchorLayout();
			jPanelAttach.setLayout(jPanelAttachLayout);
			jPanelAttach.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelAttach.setBounds(14, 273, 602, 42);
			jPanelMainCenter.add(jPanelAttach);
			
			jPanelSignatureFooter = new JPanel();
			jPanelSignatureFooter.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jPanelMainCenter.add(jPanelSignatureFooter);
			jPanelSignatureFooter.setBounds(14, 329, 602, 42);
			
			jRadioButtonAllAttachments.setText("All IP Related Attachments");
			jRadioButtonAllAttachments.setSelected(false);
			jRadioButtonAllAttachments.setVisible(true);
			jRadioButtonAllAttachments.setPreferredSize(new java.awt.Dimension(151, 26));
			buttonGroupAttachments.add(jRadioButtonAllAttachments);
			jPanelAttach.add(jRadioButtonAllAttachments, new AnchorConstraint(273, 288, 892, 37, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			
			jRadioButtonNoAttachments.setText("None");
			jRadioButtonNoAttachments.setSelected(true);
			jRadioButtonNoAttachments.setPreferredSize(new java.awt.Dimension(60, 26));
			buttonGroupAttachments.add(jRadioButtonNoAttachments);
			jPanelAttach.add(jRadioButtonNoAttachments, new AnchorConstraint(273, 464, 892, 364, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
					PrintExperimentSetupDialog.this.setVisible(false);
					PrintExperimentSetupDialog.this.dispose();
				}
				public void windowClosed(WindowEvent e){
					PrintExperimentSetupDialog.this.removeAll();
					PrintExperimentSetupDialog.this.setVisible(false);
				}
			});	
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}
	
	/** Add your pre-init code in here 	*/
	public void preInitGUI()
	{
	}
	
	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
		updateDisplay();
		
		//disableAnalytical();
		//disableAttachments();
	}
	
	/** Auto-generated main method */
	public static void main(String[] args)
	{
		showGUI();
	}
	
	/**
	 * This static method creates a new instance of this class and shows
	 * it inside a new JFrame, (unless it is already a JFrame).
	 *
	 * It is a convenience method for showing the GUI, but it can be
	 * copied and used as a basis for your own code.	*
	 * It is auto-generated code - the body of this method will be
	 * re-generated after any changes are made to the GUI.
	 * However, if you delete this method it will not be re-created.	*/
	public static void showGUI(){
		try {
			PrintExperimentSetupDialog inst = new PrintExperimentSetupDialog(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed(ActionEvent evt)
	{
		this.dispose();
		
		try {
			updatePrintOptions();
			printOptions.saveOptions();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
	
	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt)
	{
		this.dispose();
	}
	
	/** Auto-generated event handler method */
	protected void jButtonSelectTypesActionPerformed(ActionEvent evt) {
		updatePrintOptions();
		PrintSelectInstTypesDialog dlg = new PrintSelectInstTypesDialog((Frame)MasterController.getGuiController().getGUIComponent(), printOptions);
		Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
		Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
		dlg.setLocation(loc.x + (dim.width - dlg.getSize().width)/2, loc.y + (dim.height - dlg.getSize().height)/2);
		PrintOptions tPrintOptions = dlg.getSelectedInstrumentTypes();
		if (tPrintOptions != null) {
			printOptions = tPrintOptions;
			updateDisplay();
		}
		dlg=null;
	}
	
	private void updateDisplay()
	{
		jCheckBoxExpSubject.setSelected(printOptions.subject);
		jCheckBoxReactionSchema.setSelected(printOptions.rxnSchema);
		jCheckBoxStoichiometry.setSelected(printOptions.stoichTable);
		if (jCheckBoxStoichiometry.isSelected()){
			jRadioButtonStoicWithStructures.setEnabled(true);
			jRadioButtonStoicNoStructures.setEnabled(true);
		}
		else
		{
			jRadioButtonStoicWithStructures.setEnabled(false);
			jRadioButtonStoicNoStructures.setEnabled(false);
		}
		jRadioButtonStoicWithStructures.setSelected(printOptions.stoicWStructures);
		jRadioButtonStoicNoStructures.setSelected(printOptions.stoicNoStructures);

		jCheckBoxReactionProcedure.setSelected(printOptions.rxnProcedure);
		
		jCheckBoxBatchDataStrucDetails.setSelected(printOptions.batchStructData);
		
		jCheckBoxAccesibiltySum.setSelected(printOptions.qcSummary);
		jCheckBoxAnalyticalServiceDataFiles.setSelected(printOptions.qcFiles);
		if (jCheckBoxAnalyticalServiceDataFiles.isSelected()){
			jRadioButtonAllInstrumentTypes.setEnabled(true);
			jRadioButtonTypes.setEnabled(true);
			jButtonSelectTypes.setEnabled(true);
		}
		else{
			jRadioButtonAllInstrumentTypes.setEnabled(false);
			jRadioButtonTypes.setEnabled(false);
			jButtonSelectTypes.setEnabled(false);
		}
		jRadioButtonAllInstrumentTypes.setSelected(printOptions.qcAllInstruments);
		jRadioButtonTypes.setSelected(printOptions.qcSelectInstruments);
		
		jTextFieldInstrumentTypes.setText(printOptions.qcInstrumentsDesc);
		
		jCheckBoxRegSubSum.setSelected(printOptions.regSummary);
		jCheckBoxRegDetails.setSelected(printOptions.regDetails);
		jCheckBoxSubDetails.setSelected(printOptions.regSubmissionDetails);
		jCheckBoxHazHandlingDetails.setSelected(printOptions.regHazards);
		
		jRadioButtonAllAttachments.setSelected(printOptions.attachAll);
		jRadioButtonNoAttachments.setSelected(printOptions.attachNone);
		
		jCheckBoxSignatureFooter.setSelected(printOptions.signatureFooter);
	}
	
	private void updatePrintOptions()
	{
		printOptions.subject = (jCheckBoxExpSubject.isSelected());
		printOptions.rxnSchema = jCheckBoxReactionSchema.isSelected();
		printOptions.stoichTable = jCheckBoxStoichiometry.isSelected();
		printOptions.stoicWStructures = jRadioButtonStoicWithStructures.isSelected();
		printOptions.stoicNoStructures = jRadioButtonStoicNoStructures.isSelected();
		printOptions.rxnProcedure = jCheckBoxReactionProcedure.isSelected();
		
		printOptions.batchStructData = jCheckBoxBatchDataStrucDetails.isSelected();
		
		printOptions.qcSummary = jCheckBoxAccesibiltySum.isSelected();
		printOptions.qcFiles = jCheckBoxAnalyticalServiceDataFiles.isSelected();
		printOptions.qcAllInstruments = jRadioButtonAllInstrumentTypes.isSelected();
		printOptions.qcSelectInstruments = jRadioButtonTypes.isSelected();
		printOptions.qcInstrumentsDesc = jTextFieldInstrumentTypes.getText();
		
		printOptions.regSummary = jCheckBoxRegSubSum.isSelected();
		printOptions.regDetails = jCheckBoxRegDetails.isSelected();
		printOptions.regSubmissionDetails = jCheckBoxSubDetails.isSelected();
		printOptions.regHazards = jCheckBoxHazHandlingDetails.isSelected();
		
		printOptions.attachAll = jRadioButtonAllAttachments.isSelected();
		printOptions.attachNone = jRadioButtonNoAttachments.isSelected();
		
		printOptions.signatureFooter = jCheckBoxSignatureFooter.isSelected();
		
	}
	
//	private void disableAnalytical() {
//	jCheckBoxAccesibiltySum.setEnabled(false);
//	jCheckBoxAnalyticalServiceDataFiles.setEnabled(false);
//	jRadioButtonAllInstrumentTypes.setEnabled(false);
//	jRadioButtonTypes.setEnabled(false);
//	jTextFieldInstrumentTypes.setEnabled(false);
//	jButtonSelectTypes.setEnabled(false);
//	}
//	
//	private void disableAttachments() {
//	jRadioButtonAllAttachments.setEnabled(false);
//	jRadioButtonNoAttachments.setEnabled(false);
//	}
	
	public void dispose() {
		super.dispose();
	}
}
