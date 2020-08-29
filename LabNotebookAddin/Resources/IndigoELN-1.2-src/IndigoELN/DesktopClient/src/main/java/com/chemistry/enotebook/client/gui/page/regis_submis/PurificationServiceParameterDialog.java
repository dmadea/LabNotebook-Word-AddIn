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


import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.purificationservice.PurificationServiceSubmisionParameters;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PurificationServiceParameterDialog extends CeNDialog implements AmountEditListener{

	private static final long serialVersionUID = -8087323621778395469L;

	private static final Log log = LogFactory.getLog(PurificationServiceParameterDialog.class);

	// control panel for buttons
	private JPanel controllPanel = new JPanel();
	private JLabel phValueLabel;
	
	private CeNComboBox destinationLabCombo = new CeNComboBox();
	private CeNComboBox archivePlateCombo = new CeNComboBox();
	private CeNComboBox sampleWorkupCombo = new CeNComboBox();
	private JTextField saltTypetf = new JTextField("");
	
	private JCheckBox byproductSaltPresentJCheckbox = new JCheckBox();
	private JCheckBox seperateIsomersJCheckbox = new JCheckBox();
	
	
	private JCheckBox neutralAllowedJCheckbox = new JCheckBox("Neutral Allowed");
	private JCheckBox tFAAllowedJCheckbox = new JCheckBox("TFA Allowed");
	private JCheckBox basicAllowedJCheckbox = new JCheckBox("Basic Allowed");
	private JCheckBox formicAcidAllowedJCheckbox = new JCheckBox("Formic Acid Allowed");
	
	private PAmountComponent reactionScale;
	private PAmountComponent analyticalPlateConc;
	private PAmountComponent archiveVolumeAmountComp;
	private JTextArea commentTextArea = new JTextArea();
	private JScrollPane commentScrollPane = new JScrollPane(commentTextArea);
	
	private static final String REACTION_SCALE = "Reaction Scale";
	private static final String ANALYTICAL_PLATE_CONC = "Analytical Plate Concentration";
	
	private boolean isLoading = false;
	private NotebookPageModel pageModel = null;
	private List<PlateWell<ProductBatchModel>> plateWells;
	JCheckBox[] modifierCheckBoxes = {neutralAllowedJCheckbox, tFAAllowedJCheckbox, basicAllowedJCheckbox, formicAcidAllowedJCheckbox};
	
	private boolean readOnly = false;

	public PurificationServiceParameterDialog(JFrame parent, NotebookPageModel pageModel, List<PlateWell<ProductBatchModel>> plateWells, boolean readOnly) {
		super(parent, "Set Purification Parameters", true);
		this.pageModel = pageModel;
		this.plateWells = plateWells;
		this.readOnly = readOnly;
		try {
			jbInit(readOnly);
		} catch (Exception e) {
			log.error("Failed initializing PurificationService Parameter Dialog", e);
		}
		setResizable(false);
		setDefaultCloseOperation(PurificationServiceParameterDialog.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
	}

	private void jbInit(boolean readOnly) throws Exception {
		JButton setButton = new JButton("Set");
		JButton cancelButton = new JButton("Cancel");
		
		JLabel destinationLabLabel = new JLabel("Destination Labs:");
		JLabel reactionScaleLabel = new JLabel("Rxn Scale:");
		JLabel archiveVolumeLabel = new JLabel("Archive Volume(UL):");
		JLabel sampleWorkupLabel = new JLabel("Sample Workup:");
		JLabel byproductSaltPresentLabel = new JLabel("Inorganic By-product Salt Present:");
		JLabel saltTypeLabel = new JLabel("Salt Type:");
		JLabel separateIsomersLabel = new JLabel("Separate Isomers:");
		JLabel archivePlateLabel = new JLabel("Archive Choice:");
		JLabel analyticalPlateConcLabel = new JLabel("Analytical Plate Concentration(mg/mL):");
		phValueLabel = new JLabel("pH");
		
		isLoading = true;
		reactionScale = this.makeAmountComponent(null);
		reactionScale.setEditable(false);
		analyticalPlateConc = this.makeAmountComponent(new AmountModel(UnitType.SCALAR));
		archiveVolumeAmountComp = this.makeAmountComponent(new AmountModel(UnitType.VOLUME, 0.400));
		Unit2 un = new Unit2(UnitType.VOLUME, "UL");
		archiveVolumeAmountComp.getAmount().setUnit(un);
		archiveVolumeAmountComp.setUnitEditable(false);
		archiveVolumeAmountComp.updateDisplay();
						
    	for (int i=0; i<modifierCheckBoxes.length; i++) {
    		modifierCheckBoxes[i].addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent actionEvent) {
    		        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
    		        ButtonModel buttonModel = abstractButton.getModel();
    		        boolean selected = buttonModel.isSelected();
    		        if (selected == false)
    		        {
    		        	boolean isOneSelected = false;
    		        	for (int i=0; i<modifierCheckBoxes.length && isOneSelected == false; i++)
    		        	{
   		        			if (abstractButton != modifierCheckBoxes[i] && modifierCheckBoxes[i].isSelected())
   		        				isOneSelected = true;
    		        	}
    		        	if (isOneSelected == false && isAnyOtherFieldPopulated())
    		        	{
    		        		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
    		        		                              "There must be at least one modifier checked.",
    		        		                              "Modifier selection error", 
    		        		                              JOptionPane.ERROR_MESSAGE);
    		        		abstractButton.setSelected(true);	
    		        	}
    		        }
    		        recalculatepHValue();
    			}
    	    });
    	}
		byproductSaltPresentJCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AbstractButton abstractButton = (AbstractButton) ae.getSource();
				ButtonModel buttonModel = abstractButton.getModel();
				boolean selected = buttonModel.isSelected();
				if (selected) {
					saltTypetf.setEnabled(true);
				} else {
					saltTypetf.setText("NONE");
					saltTypetf.setEnabled(false);
				}
			}
		});

		//initialize components
		RegistrationManagerDelegate delegate = new RegistrationManagerDelegate();
		List<String> labsForPurification = delegate.getLabsForPurification(pageModel.getSiteCode());
		CodeTableUtils.fillComboBox(destinationLabCombo, null, labsForPurification, false, true);
		List<String> archivePlateChoice = delegate.getPurificationArchivePlateChoice();
		CodeTableUtils.fillComboBox(archivePlateCombo, null, archivePlateChoice, false, true);
		List<String> sampleWorkup = delegate.getPurificationSampleWorkup();
    	CodeTableUtils.fillComboBox(sampleWorkupCombo, null, sampleWorkup, false, true);
    	basicAllowedJCheckbox.setSelected(true);
    	
    	if (plateWells != null && plateWells.size() > 0 && plateWells.get(0) != null && plateWells.get(0).getBatch() != null) {
    		ProductBatchModel batch = plateWells.get(0).getBatch();
    		if (batch.getRegInfo() != null && batch.getRegInfo().getSubmitContainerList() != null) {
    			PurificationServiceSubmisionParameters purificationServiceSubmissionParameters = null;
    			
    			if (batch.getRegInfo().getSubmitContainerListSize() > 0 && batch.getRegInfo().getSubmitContainerListRow(0) != null) {
    				purificationServiceSubmissionParameters = batch.getRegInfo().getSubmitContainerListRow(0).getPurificationServiceParameters();
    			} else {
    				purificationServiceSubmissionParameters = plateWells.get(0).getPurificationServiceParameter();
    			}
    			
        		if (purificationServiceSubmissionParameters != null) {
        			String selectedDestinationLab = purificationServiceSubmissionParameters.getDestinationLab();
        			if (StringUtils.isNotBlank(selectedDestinationLab)) {
        				destinationLabCombo.setSelectedItem(selectedDestinationLab);
        			} else {
        				destinationLabCombo.setSelectedIndex(0);
        			}
        			
        			if (StringUtils.isNotBlank(purificationServiceSubmissionParameters.getArchivePlate())) {
        				archivePlateCombo.setSelectedItem(purificationServiceSubmissionParameters.getArchivePlate());
        			} else {
        				archivePlateCombo.setSelectedIndex(0);
        			}
        			
    	    		if (purificationServiceSubmissionParameters.getArchiveVolume() != -1) {
    	    			archiveVolumeAmountComp.getAmount().SetValueInStdUnits(purificationServiceSubmissionParameters.getArchiveVolume()/1000);
    	    		}
    	    		archiveVolumeAmountComp.updateDisplay();
    	    		
    	    		reactionScale.setValue(purificationServiceSubmissionParameters.getReactionScaleAmount());
    	    		
        			if (StringUtils.isNotBlank(purificationServiceSubmissionParameters.getSampleWorkUp())) {
        				sampleWorkupCombo.setSelectedItem(purificationServiceSubmissionParameters.getSampleWorkUp());
        			} else {
        				sampleWorkupCombo.setSelectedIndex(0);
        			}

    				saltTypetf.setText(purificationServiceSubmissionParameters.getSaltType());
    				byproductSaltPresentJCheckbox.setSelected(purificationServiceSubmissionParameters.isInorganicByProductSaltPresent());
    				seperateIsomersJCheckbox.setSelected(purificationServiceSubmissionParameters.isSeperateTheIsomers());
    				analyticalPlateConc.setValue(purificationServiceSubmissionParameters.getAnalyticalPlateConcVolume());
    				
    				String modifiers[] = purificationServiceSubmissionParameters.getModifiers();
    				
    				if (modifierCheckBoxes != null && modifiers != null) {
    					for (int i = 0; i < modifierCheckBoxes.length; ++i) {
    						modifierCheckBoxes[i].setSelected(false);
    						for (int j = 0; j < modifiers.length; ++j) {
    							if (modifierCheckBoxes[i].getText().equals(modifiers[j])) {
    								modifierCheckBoxes[i].setSelected(true);
    							}
    						}
    					}
    				}
    				commentTextArea.setText(purificationServiceSubmissionParameters.getComment());
    				recalculatepHValue();
        		} else {
        			destinationLabCombo.setSelectedIndex(0);
        			archivePlateCombo.setSelectedIndex(0);
        			sampleWorkupCombo.setSelectedIndex(0);
        		}
        		
        		saltTypetf.setEnabled(byproductSaltPresentJCheckbox.isSelected());
    		}
    	}
    	
		// center
		getContentPane().setLayout(new BorderLayout());
	
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("125dlu, 1dlu, 80dlu, 15dlu", ""));
		builder.setDefaultDialogBorder();
		
		builder.append(destinationLabLabel);
		builder.append(destinationLabCombo);
		builder.nextLine();

		builder.append(reactionScaleLabel);
		builder.append(reactionScale);
		builder.nextLine();

		builder.append(archivePlateLabel);
		builder.append(archivePlateCombo);
		builder.nextLine();
		
		builder.append(archiveVolumeLabel);
		builder.append(archiveVolumeAmountComp);
		builder.nextLine();

		builder.append(sampleWorkupLabel);
		builder.append(sampleWorkupCombo);
		builder.nextLine();
		
		builder.append(byproductSaltPresentLabel);
		builder.append(byproductSaltPresentJCheckbox);
		builder.nextLine();
		
		builder.append(saltTypeLabel);
		builder.append(saltTypetf);
		builder.nextLine();

		builder.append(separateIsomersLabel);
		builder.append(seperateIsomersJCheckbox);
		builder.nextLine();		

		if(pageModel.isParallelExperiment()) {
			builder.append(analyticalPlateConcLabel);
			builder.append(analyticalPlateConc);
			builder.nextLine();
		}		
		builder.appendSeparator("Modifier");
	
		builder.append(neutralAllowedJCheckbox);
		builder.append(tFAAllowedJCheckbox);
		builder.nextLine();
		builder.append(basicAllowedJCheckbox);
		builder.append(formicAcidAllowedJCheckbox);
		builder.nextLine();
		
		builder.append(phValueLabel);
		
		commentScrollPane.setPreferredSize(new Dimension(300, 50));
		commentTextArea.setLineWrap(true);
		
		builder.nextLine();
		builder.appendSeparator("Comment");
		builder.append(commentScrollPane, 3);
		
		//2
		getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
		getContentPane().add(controllPanel, BorderLayout.SOUTH);
		
		//3
		controllPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		controllPanel.add(setButton);
		controllPanel.add(cancelButton);
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		setButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setButton_ActionPerformed();
			}
		});
				
		if (readOnly) {
			destinationLabCombo.setEnabled(false);
			reactionScale.setEditable(false);
			archivePlateCombo.setEnabled(false);
			archiveVolumeAmountComp.setEditable(false);
			sampleWorkupCombo.setEnabled(false);
			byproductSaltPresentJCheckbox.setEnabled(false);
			saltTypetf.setEditable(false);
			seperateIsomersJCheckbox.setEnabled(false);
			analyticalPlateConc.setEditable(false);
			neutralAllowedJCheckbox.setEnabled(false);
			tFAAllowedJCheckbox.setEnabled(false);
			basicAllowedJCheckbox.setEnabled(false);
			formicAcidAllowedJCheckbox.setEnabled(false);
			commentTextArea.setEditable(false);
			commentTextArea.setBackground(saltTypetf.getBackground());
		}
		isLoading = false;
		if(pageModel != null && pageModel.isParallelExperiment()) {
			reactionScale.setValue(pageModel.getPageHeader().getScale());
		} else {
			List<StoicModelInterface> limitingReagents = pageModel.getLimitingReagentsInAllSteps();
			if(limitingReagents != null && !limitingReagents.isEmpty() && limitingReagents.get(0) != null) {
				// Singleton Experiments should have only one step
				StoicModelInterface limitingReagent = limitingReagents.get(0);
				reactionScale.setValue(limitingReagent.getStoicMoleAmount());
			} else {
				// we don't have a scale.  Should indicate an issue
				reactionScale.setValue(new AmountModel(UnitType.MOLES, -1));
			}
		}

	}
	
	private PAmountComponent makeAmountComponent(AmountModel amountModel) {
		PAmountComponent amountComponent = null;
		if (amountModel == null) {
			amountComponent = new PAmountComponent();
		} else {
			amountComponent = new PAmountComponent(amountModel);
		}
		amountComponent.setEditable(true);
		amountComponent.setValueSetTextColor(null);
		amountComponent.addAmountEditListener(this);
		amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());
		return amountComponent;
	}

	protected boolean isAnyOtherFieldPopulated() {
		String destinationLab = destinationLabCombo.getSelectedItem().toString();
		if (destinationLab.indexOf("None") == -1)
			return true;
		
		String archivePlate =  	archivePlateCombo.getSelectedItem().toString();
		if (archivePlate.indexOf("None") == -1)
			return true;
		
/*		double archiveVolume = Double.valueOf(archiveVolumetf.getText().trim()).doubleValue();
		if (archiveVolume > 0.0)
			return true;*/

		
		AmountModel reactionScaleAmount = reactionScale.getAmount();
		if (reactionScaleAmount.GetValueInStdUnitsAsDouble() > 0.0)
			return true;
		else
			reactionScaleAmount.SetValueInStdUnits(0.0d);
		
	 	String sampleWorkUp = sampleWorkupCombo.getSelectedItem().toString();
		if (sampleWorkUp.indexOf("None") == -1)
			return true;		
		return false;
	}

//	private ArrayList getAllItems(CeNComboBox destinationLabCombo) {
//		ArrayList list = new ArrayList();
//		for (int i=0; i<destinationLabCombo.getItemCount(); i++)
//		{
//			list.add(destinationLabCombo.getItemAt(i));
//		}
//		return list;
//	}

	protected void recalculatepHValue() {
		String pHValue = "";
		if (neutralAllowedJCheckbox.isSelected()) {
			if (formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= All";
			}
			else if (formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "<= 7";
			}
			else if (formicAcidAllowedJCheckbox.isSelected() &&
			!tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "<= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "<= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			!tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = ">= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			!tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= All";
			}
			else if (formicAcidAllowedJCheckbox.isSelected() &&
			!tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= All";
			}
		}
		else if (!neutralAllowedJCheckbox.isSelected())
		{
			if (formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= All";
			}
			else if (formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "<= 7";
			}
			else if (formicAcidAllowedJCheckbox.isSelected() &&
			!tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "<= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			!basicAllowedJCheckbox.isSelected())
			{
				pHValue = "<= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			!tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = ">= 7";
			}
			else if (!formicAcidAllowedJCheckbox.isSelected() &&
			tFAAllowedJCheckbox.isSelected() &&
			basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= All";
			}
			else if (formicAcidAllowedJCheckbox.isSelected() &&
					!tFAAllowedJCheckbox.isSelected() &&
					basicAllowedJCheckbox.isSelected())
			{
				pHValue = "= All";
			}
		}
		phValueLabel.setText("pH" + pHValue);		
	}

	private void setButton_ActionPerformed() {
		if (readOnly) {
			dispose();
			return;
		}		
		
		if (commentTextArea.getText().length() > 256) {
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Comment field must be 256 symbols max.");
			return;
		}
		
		ArrayList<String> modifiersList = new ArrayList<String>();
		
		for (int i = 0; i < modifierCheckBoxes.length; i++) {
			if (modifierCheckBoxes[i].isSelected())
				modifiersList.add(modifierCheckBoxes[i].getText());
		}
		
		if (modifiersList.size() == 0 && isAnyOtherFieldPopulated()) {
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "There must be at least one modifier checked.", "Modifier selection error", JOptionPane.ERROR_MESSAGE);
			return;
		}
				
		for (PlateWell<ProductBatchModel> tube : plateWells) {
			if (tube != null && tube.getBatch() != null && tube.getBatch().getRegInfo() != null) {
				List<BatchSubmissionContainerInfoModel> contList = tube.getBatch().getRegInfo().getSubmitContainerList();
				
				if (contList != null) {
					for (BatchSubmissionContainerInfoModel infoModel : contList) {
						if (infoModel != null) {
							infoModel.setPurificationServiceParameters(updateParameters(infoModel.getPurificationServiceParameters()));
							infoModel.setPurificationServiceParamatersSetByUser(true);
							infoModel.setModelChanged(true);
						}
					}
				}
			}	
			
			if (tube != null) {
				tube.setPurificationServiceParameter(updateParameters(tube.getPurificationServiceParameter()));
			}
			
			tube.setModelChanged(true);
		}
		pageModel.setModelChanged(true);
		
		dispose();
	}

	private PurificationServiceSubmisionParameters updateParameters(PurificationServiceSubmisionParameters params) {
		PurificationServiceSubmisionParameters purificationServiceParameter;
		
		if (params == null) {
			purificationServiceParameter = new PurificationServiceSubmisionParameters();
		} else {
			purificationServiceParameter = params;
		}
		
		String destinationLab = destinationLabCombo.getSelectedItem().toString();
		if (destinationLab.indexOf("None") > -1)
			destinationLab = "";
		
		String archivePlate =  	archivePlateCombo.getSelectedItem().toString();
		if (archivePlate.indexOf("None") > -1)
			archivePlate = "";
	
		double archiveVolume = Double.valueOf(archiveVolumeAmountComp.getAmount().getValue()).doubleValue() ;
		if (archiveVolume <= 0.0)
			archiveVolume = 0.0d;
		
		AmountModel reactionScaleAmount = reactionScale.getAmount();
		if (reactionScaleAmount.GetValueInStdUnitsAsDouble() <= 0.0)
			reactionScaleAmount.SetValueInStdUnits(0.0d);
		
	 	String sampleWorkUp = sampleWorkupCombo.getSelectedItem().toString();
		if (sampleWorkUp.indexOf("None") > -1)
			sampleWorkUp = "";
	 	
		String saltType = saltTypetf.getText().trim(); 
		boolean isSaltPresent = byproductSaltPresentJCheckbox.isSelected();
		boolean separateTheIsomers = seperateIsomersJCheckbox.isSelected();
		
		AmountModel analytialPlateConcAmount = analyticalPlateConc.getAmount();
		if (analytialPlateConcAmount.GetValueInStdUnitsAsDouble() <= 0.0d)
			analytialPlateConcAmount.SetValueInStdUnits(0.0d);

		ArrayList<String> modifiersList = new ArrayList<String>();
		for (int i = 0; i < modifierCheckBoxes.length; i++) {
			if (modifierCheckBoxes[i].isSelected())
				modifiersList.add(modifierCheckBoxes[i].getText());
		}

		String[] modifiers = (String[]) modifiersList.toArray(new String[modifiersList.size()]);
				
		String labCode = destinationLab;
		purificationServiceParameter.setDestinationLab(labCode);
		purificationServiceParameter.setArchivePlate(archivePlate);
		purificationServiceParameter.setArchiveVolume(archiveVolume);
		purificationServiceParameter.setRecationScaleAmount(reactionScaleAmount);
		purificationServiceParameter.setSampleWorkUp(sampleWorkUp);
		if (isSaltPresent) {
			purificationServiceParameter.setSaltType(saltType);
		} else {
			purificationServiceParameter.setSaltType("");
		}
		purificationServiceParameter.setInorganicByProductSaltPresent(isSaltPresent);
		purificationServiceParameter.setSeperateTheIsomers(separateTheIsomers);
		purificationServiceParameter.setAnalyticalPlateConcVolume(analytialPlateConcAmount);
		purificationServiceParameter.setModifiers(modifiers);
		purificationServiceParameter.setComment(commentTextArea.getText());
		purificationServiceParameter.setModelChanged(true);
		
		return purificationServiceParameter;
	}

	public void editingCanceled(ChangeEvent e) {
		
	}

	public void editingStopped(ChangeEvent e) {
		Object o = e.getSource();
		if (o instanceof PAmountComponent) {
			if (o == reactionScale)
				this.updateProperty(REACTION_SCALE);
			else
				this.updateProperty(ANALYTICAL_PLATE_CONC);  
		}
	}

	private void updateProperty(String property) {
		if (isLoading )
			return;
		isLoading = true;
		if (property.equals(REACTION_SCALE)) {
				//enableSaveButton();
		} else if (property.equals(ANALYTICAL_PLATE_CONC)) {
				//enableSaveButton();
		}
		isLoading = false;
	}
}
