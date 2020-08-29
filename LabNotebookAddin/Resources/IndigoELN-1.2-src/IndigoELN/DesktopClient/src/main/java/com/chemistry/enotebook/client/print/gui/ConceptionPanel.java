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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConceptionPanel extends JPanel implements ActionListener {
		
	private static final long serialVersionUID = -4453259394360025817L;
	
	private ConceptionPrintOptions printOptions;
	private JCheckBox conceptDetailsCB;
	private JCheckBox reactionTargetCB;
	private JCheckBox utilityInventionNotesCB;
	private JCheckBox preferredCompoundsCB;
	private JCheckBox ipAttachmentsCB;
	private JCheckBox signatureFooterCB;
	private JDialog parent;
	
	public ConceptionPanel(JDialog parent, ConceptionPrintOptions printOptions) {
		this.parent = parent;
		this.add(getIncludePanel());
		this.printOptions = printOptions;
		this.updateDisplay();
	}
	
	private JPanel getIncludePanel() {
		JPanel includePanel = new JPanel();
		includePanel.setPreferredSize(new Dimension(500, 250));
		includePanel.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Include: "));
		
		FormLayout layout = new FormLayout("10dlu, pref, 20dlu, pref, 10dlu", "10dlu, pref, 10dlu, pref, 10dlu, pref, 10dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		
		conceptDetailsCB = new JCheckBox("Concept Details");
		reactionTargetCB = new JCheckBox("Reaction/Target");
		utilityInventionNotesCB = new JCheckBox("Utility, Invention Notes");
		preferredCompoundsCB = new JCheckBox("PreferredCompounds");
		ipAttachmentsCB = new JCheckBox("IP Related Attachments");
		signatureFooterCB = new JCheckBox("Signature & Witness Footer");
		
		builder.add(conceptDetailsCB, cc.xy(2, 2));
		builder.add(reactionTargetCB, cc.xy(2, 4));
		builder.add(utilityInventionNotesCB, cc.xy(2, 6));
		builder.add(preferredCompoundsCB, cc.xy(4, 2));
		builder.add(ipAttachmentsCB, cc.xy(4, 4));
		builder.add( signatureFooterCB, cc.xy(4, 6));
		includePanel.add(builder.getPanel(), BorderLayout.CENTER);
		
		conceptDetailsCB.addActionListener(this);
		reactionTargetCB.addActionListener(this);
		utilityInventionNotesCB.addActionListener(this);
		preferredCompoundsCB.addActionListener(this);
		ipAttachmentsCB.addActionListener(this);
		signatureFooterCB.addActionListener(this);
		
		return includePanel;
	}
	
	public void updateDisplay() {
		this.conceptDetailsCB.setSelected(printOptions.includeConceptDetails);
		this.reactionTargetCB.setSelected(printOptions.includeReactionTarget);
		this.utilityInventionNotesCB.setSelected(printOptions.includeUtilityInventionNotes);
		this.preferredCompoundsCB.setSelected(printOptions.includePreferredCompounds);
		this.ipAttachmentsCB.setSelected(printOptions.includeIpRelatedAttachments);
		this.signatureFooterCB.setSelected(printOptions.includeSignatureFooter);
	}

	public void updatePrintOptions() {
		printOptions.includeConceptDetails = this.conceptDetailsCB.isSelected();
		printOptions.includeReactionTarget = this.reactionTargetCB.isSelected();
		printOptions.includeUtilityInventionNotes = this.utilityInventionNotesCB.isSelected();
		printOptions.includePreferredCompounds = this.preferredCompoundsCB.isSelected();
		printOptions.includeIpRelatedAttachments = this.ipAttachmentsCB.isSelected();
		printOptions.includeSignatureFooter = this.signatureFooterCB.isSelected();
	}
	
	public ConceptionPrintOptions getPrintOptions() {
		return printOptions;
	}

	public void setPrintOptions(ConceptionPrintOptions printOptions) {
		this.printOptions = printOptions;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JCheckBox) {
			if (parent instanceof PrintExperimentSetup) {
				((PrintExperimentSetup) parent).enableSaveButton();
			}
		}
	}
}
