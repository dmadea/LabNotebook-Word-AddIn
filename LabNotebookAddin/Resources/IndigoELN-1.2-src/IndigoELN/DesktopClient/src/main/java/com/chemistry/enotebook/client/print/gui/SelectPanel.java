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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SelectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1703027376389739086L;
	
	private JTextArea jTextAreaNBKExp = new JTextArea();
	private JCheckBox experimentCB;
	private JCheckBox synthesisPlatesCB;
	private JCheckBox overnightReactionCardsCB;
	private StepChooserPanel synPlateStepChooserPanel;
	private StepChooserPanel reactionCardStepChooserPanel;
	private SynthesisPlatePrintOptions platePrintOptions;

	private JDialog parent;
	
	public SelectPanel(JDialog parent, SynthesisPlatePrintOptions platePrintOptions) {
		this.parent = parent;
		this.platePrintOptions = platePrintOptions;
		FormLayout layout = new FormLayout("10dlu, left:pref:grow, pref, 10dlu", "pref, pref");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
//		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(getTopPanel(),  cc.xy(2, 1));
		builder.add(getSelectPanel(),  cc.xy(2, 2));
//		builder.add(this.getControlPanel(), cc.xywh(2, 3, 2, 1));
		add(builder.getPanel(), BorderLayout.EAST);

		updateDisplay();
		
		experimentCB.addActionListener(this);
		overnightReactionCardsCB.addActionListener(this);
		synthesisPlatesCB.addActionListener(this);
		synPlateStepChooserPanel.allStepsRB.addActionListener(this);
		synPlateStepChooserPanel.finalStepRB.addActionListener(this);
		synPlateStepChooserPanel.stepsRB.addActionListener(this);
	}

	private JPanel getTopPanel() {
		JPanel jPanelMainCenter = new JPanel();
		jPanelMainCenter.setLayout(null);
		jPanelMainCenter.setVisible(true);
		jPanelMainCenter.setPreferredSize(new Dimension(700, 100));
		this.add(jPanelMainCenter, BorderLayout.CENTER);
		//experimentsToPrintPanel.setSize(522, 287);

		jPanelMainCenter.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Print Range and Contents: "));

		jTextAreaNBKExp.setText("-none-");
		jTextAreaNBKExp.setEditable(false);
		jTextAreaNBKExp.setVisible(true);
		jTextAreaNBKExp.setPreferredSize(new Dimension(450, 55));
		jTextAreaNBKExp.setBorder(new LineBorder(new Color(0, 0, 0), 1, false));
		jTextAreaNBKExp.setBounds(new Rectangle(30, 30, 450, 55));
		jTextAreaNBKExp.setFocusable(false);
		jPanelMainCenter.add(jTextAreaNBKExp);
		return jPanelMainCenter;
	}

	private JPanel getSelectPanel() {
		JPanel jPanelSelect = new JPanel();
		jPanelSelect.setBorder(PrintSetupGuiUtils.getEtchedTitledBorder("Printout Types: "));

		FormLayout layout = new FormLayout("10dlu, pref, 10dlu, pref, 10dlu", "10dlu, pref, 10dlu, pref, 10dlu, pref, 10dlu");
		PanelBuilder builder = new PanelBuilder(layout); //, new FormDebugPanel());
		//PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		experimentCB = new JCheckBox("Experiment");
		experimentCB.setSelected(true);		
		synthesisPlatesCB = new JCheckBox("Synthesis Plates");
		overnightReactionCardsCB = new JCheckBox("Overnight Reaction Cards");
		// temporary!!!
		//synthesisPlatesCB.setEnabled(false);
		overnightReactionCardsCB.setEnabled(false);
		
		builder.add(experimentCB, cc.xy(2, 2));
		builder.add(synthesisPlatesCB, cc.xy(2, 4));
		builder.add(overnightReactionCardsCB, cc.xy(2, 6));
		
		// Synthesis plate step definition
		synPlateStepChooserPanel = new StepChooserPanel(true, false, false, "");
		synPlateStepChooserPanel.enable(false);
		synthesisPlatesCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					synPlateStepChooserPanel.enable(true);
				} else {
					synPlateStepChooserPanel.enable(false);
				}
			}
		});
		builder.add(synPlateStepChooserPanel, cc.xy(4, 4));
		
		// Overnight reaction card step definition
		reactionCardStepChooserPanel = new StepChooserPanel(true, false, false, "");
		reactionCardStepChooserPanel.enable(false);
		overnightReactionCardsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) {
					reactionCardStepChooserPanel.enable(true);
				} else {
					reactionCardStepChooserPanel.enable(false);
				}
			}
		});
		builder.add(reactionCardStepChooserPanel, cc.xy(4, 6));
		jPanelSelect.add(builder.getPanel(), BorderLayout.EAST);    		
		return jPanelSelect;
	}

	public void setSynthesisPlatesCB() {
		synthesisPlatesCB.setSelected(true);
		synPlateStepChooserPanel.enable(true);
		updatePrintOptions();
	}
	
	public void setExperimentsToPrint(List<PrintRequest> experiments) {
		StringBuffer buff = new StringBuffer();
		for (PrintRequest request : experiments) {
			buff.append(request.getNbRef().getNotebookRef());
			buff.append("\n");
		}
		jTextAreaNBKExp.setText(buff.toString());
	}

	public boolean printExperiment() {
		if (this.experimentCB.isSelected()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean printSynthesisPlates() {
		if (this.synthesisPlatesCB.isSelected()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void updatePrintOptions() {
		platePrintOptions.includeExperiment = this.experimentCB.isSelected();
		platePrintOptions.includeSynthesisPlates = this.synthesisPlatesCB.isSelected();
		platePrintOptions.includeSynthesisPlatesAllSteps = this.synPlateStepChooserPanel.allStepsRB.isSelected();
		platePrintOptions.includeSynthesisPlatesFinalStep = this.synPlateStepChooserPanel.finalStepRB.isSelected();
		platePrintOptions.includeSynthesisPlatesSelectSteps = this.synPlateStepChooserPanel.stepsRB.isSelected();
		platePrintOptions.synthesisPlatesSelectedStepsDesc = this.synPlateStepChooserPanel.getStepNumbersTF().getText();
	}
	
	public void updateDisplay() {
		experimentCB.setSelected(platePrintOptions.includeExperiment);
		synthesisPlatesCB.setSelected(platePrintOptions.includeSynthesisPlates);
		if (synthesisPlatesCB.isSelected()) {
			synPlateStepChooserPanel.allStepsRB.setEnabled(true);
			synPlateStepChooserPanel.finalStepRB.setEnabled(true);
			synPlateStepChooserPanel.stepsRB.setEnabled(true);
		} else {
			synPlateStepChooserPanel.allStepsRB.setEnabled(false);
			synPlateStepChooserPanel.finalStepRB.setEnabled(false);
			synPlateStepChooserPanel.stepsRB.setEnabled(false);
		}
		synPlateStepChooserPanel.allStepsRB.setSelected(platePrintOptions.includeSynthesisPlatesAllSteps);
		synPlateStepChooserPanel.finalStepRB.setSelected(platePrintOptions.includeSynthesisPlatesFinalStep);
		synPlateStepChooserPanel.stepsRB.setSelected(platePrintOptions.includeSynthesisPlatesSelectSteps);
		if (synPlateStepChooserPanel.stepsRB.isSelected()) {
			synPlateStepChooserPanel.getStepNumbersTF().setEnabled(true);
		} else {
			synPlateStepChooserPanel.getStepNumbersTF().setEnabled(false);
		}
		synPlateStepChooserPanel.getStepNumbersTF().setText(platePrintOptions.synthesisPlatesSelectedStepsDesc);
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
