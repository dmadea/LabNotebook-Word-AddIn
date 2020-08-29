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

import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class StepChooserPanel extends JPanel {

	private static final long serialVersionUID = -773673088383752780L;
	
	JRadioButton stepsRB;
	JRadioButton allStepsRB;
	JRadioButton finalStepRB;
	JTextField stepNumbersTF;
	List<String> selectedSteps = new ArrayList<String>();
	boolean allStepsEnabled = true;
	boolean finalStepEnabled = false;
	boolean stepsEnabled = false;
	String selectedStepDesc = "";
	
	public StepChooserPanel(boolean allStepsEnabled, boolean finalStepEnabled, boolean stepsEnabled, String selectedStepsDesc) {
		this.allStepsEnabled = allStepsEnabled;
		this.finalStepEnabled = finalStepEnabled;
		this.stepsEnabled = stepsEnabled;
		this.selectedStepDesc = selectedStepsDesc;
		this.initGUI();
	}
	
	private void initGUI() {
		//this.getInsets().set(0,0,0,0);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		allStepsRB = PrintSetupGuiUtils.getRadioButton("All Steps", true, true); 
		stepsRB = PrintSetupGuiUtils.getRadioButton("Steps", false, true); 
		finalStepRB = PrintSetupGuiUtils.getRadioButton("Final Step", false, true); 
		stepNumbersTF = new JTextField(8);
		stepNumbersTF.setDocument(new JTextFieldFilter(JTextFieldFilter.COMMA_SEPARATED_NUMBERS));
		if (stepsEnabled) stepNumbersTF.setEnabled(true);
		else stepNumbersTF.setEnabled(false);
		stepNumbersTF.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				selectedStepsEdited();
			}
		});

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(allStepsRB);
		buttonGroup.add(stepsRB);
		buttonGroup.add(finalStepRB);
		
		allStepsRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepNumbersTF.setEnabled(false);
				allStepsEnabled = true;
				stepsEnabled = false;
				finalStepEnabled = false;
			}
		});
		stepsRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepNumbersTF.setEnabled(true);
				stepsEnabled = true;
				allStepsEnabled = false;
				finalStepEnabled = false;
			}
		});
		finalStepRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepNumbersTF.setEnabled(false);
				finalStepEnabled = true;
				allStepsEnabled = false;
				stepsEnabled = false;
			}
		});
		
		this.add(allStepsRB);
		this.add(stepsRB);
		this.add(stepNumbersTF);
		this.add(finalStepRB);
		
	}
	
	public void enable(boolean enable) {
		this.allStepsRB.setEnabled(enable);
		this.stepsRB.setEnabled(enable);
		this.finalStepRB.setEnabled(enable);
		if (enable) {
			if (this.stepsEnabled)
				this.stepNumbersTF.setEnabled(true);
			else 
				this.stepNumbersTF.setEnabled(false);
		} else
			this.stepNumbersTF.setEnabled(false);
	}

//	public JRadioButton getStepsRB() {
//		return stepsRB;
//	}
//
//	public void setStepsRB(JRadioButton stepsRB) {
//		this.stepsRB = stepsRB;
//	}
//
//	public JRadioButton getAllStepsRB() {
//		return allStepsRB;
//	}
//
//	public void setAllStepsRB(JRadioButton allStepsRB) {
//		this.allStepsRB = allStepsRB;
//	}
//
//	public JRadioButton getFinalStepRB() {
//		return finalStepRB;
//	}
//
//	public void setFinalStepRB(JRadioButton finalStepRB) {
//		this.finalStepRB = finalStepRB;
//	}

	public JTextField getStepNumbersTF() {
		return stepNumbersTF;
	}

	public List<String> getSelectedSteps() {
		return selectedSteps;
	}

	public void setStepNumbersTF(JTextField stepNumbersTF) {
		this.stepNumbersTF = stepNumbersTF;
	}
	
	private void selectedStepsEdited() {
		String stepsText = this.stepNumbersTF.getText();
		if (stepsText != null) {
			selectedSteps.clear();
			if (stepsText.length() == 0)
				selectedSteps.add("0");
			else {
				String[] steps = stepsText.split(",");
				if (steps != null) {
					for (String step : steps)
						selectedSteps.add(step);
				}
			}
		}
	}
}