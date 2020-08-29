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
package com.chemistry.enotebook.client.gui.page.experiment.plate;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.utils.Disposable;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.CeNFormatter;
import com.chemistry.enotebook.utils.MonomerBatchStatusMapper;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class to display the monomer batch details.  Consists of two panels, the list of 
 * details and a structure diagram.  It invoked by the plate visualizer API when the 
 * well mouse over event occurs.  It is also used in the plate visualizer.  When a new
 * plate structure tab is created, a MonomerBatchDetailContainer is created and 
 * passed to the plate renderer in the form of a BatchSelectionListener.  When the user
 * clicks on a well, the plate renderer invokes the batchSelectionChanged method in 
 * the listener.  It updates the monomer batch panel in the plate structure view.
 * 
 * 
 *
 */

public class MonomerBatchDetailContainer extends JPanel implements BatchSelectionListener, AmountEditListener, Disposable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2536475425461453978L;

	public static final Log log = LogFactory.getLog(MonomerBatchDetailContainer.class);

	private MonomerPlate monomerPlate;
	private MonomerBatchModel monomerBatchModel = null;
	private NotebookPageModel notebookPageModel = null;
	private PlateWell well = null;
	private BatchAttributeComponentUtility componentUtility = null;
	
	// These are only used in this class, so no point in moving them into the CeNConstants
	private static String PLATE = "Plate";
	private static String WELL = "Well";
	private static String SEQUENCE = "Sequence";
	private static String NOTEBOOK_BATCH_NUM = "Nbk Batch Number";
	private static String STATUS = "Status";
	private static String DESIGN_MONOMER = "Design Monomer";
	private static String DELIVERED_MONOMER = "Delivered Monomer";
	private static String MOLECULAR_FORMULA = "Molecular Formula";
	private static String PARENT_MW = "Parent MW";
	private static String BATCH_MW = "Batch MW";
	private static String DELIVERED_WEIGHT = "Delivered Weight";
	private static String REACTION_WEIGHT = "Reaction Weight";
	private static String DELIVERED_MOLES = "Delivered Moles";
	private static String REACTION_MOLES = "Reaction Moles";

	public static final Font BOLD_LABEL_FONT = new java.awt.Font("Arial", Font.BOLD, 12);
	public static final Font PLAIN_LABEL_FONT = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font EDIT_FONT_SMALL = new java.awt.Font("Arial", Font.PLAIN, 11);
	public static final Font EDIT_FONT_LARGE = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font COMBO_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final Font BUTTON_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final int NUM_ROWS = 22;
	
	// Flags
	private boolean isEditable = true;
	private boolean isLoading = false;
	private boolean isChanging = false;
	
	// Plate and well 
	private CeNLabel jLabelWell;
	private JLabel jLabelWellName;
	private CeNLabel jLabelWellSequence;
	private JLabel jLabelWellSequenceValue;
	// Noteook batch number
	private CeNLabel jLabelNbkBatchNum;
	private JLabel jLabelNbkBatchNumNbkPageValue;
	private JLabel jTextFieldNbkBatchNumSeqValue;
	// Status
	private CeNLabel jLabelStatus;
	private CeNComboBox jComboBoxStatus;
	// Design and delivered monomers
	private CeNLabel jLabelDesignMonomer;
	private JLabel jLabelDesignMonomerValue;
	private CeNLabel jLabelDeliveredMonomer;
	private JLabel jLabelDeliveredMonomerValue;
	//Molecular Formula
	private CeNLabel jLabelMolFormula;
	private JLabel jLabelMolFormulaValue;
	
	// Calculated parent MW
	private CeNLabel jLabelParentMW;
	private PAmountComponent parentMWView; 
	//private AmountModel parentMWModel; 
	// Calculated batch MW
	private CeNLabel jLabelBatchMW;
	private PAmountComponent batchMWView; 
	//private AmountModel batchMWModel; 
	// Delivered weight
	private CeNLabel jLabelDeliveredWeight;
	private PAmountComponent deliveredWeightView; 
	//private AmountModel deliveredWeightModel; 
	// Reaction weight
	private CeNLabel jLabelReactionWeight;
	private PAmountComponent reactionWeightView; 
	//private AmountModel reactionWeightModel; 
	// Delivered weight
	private CeNLabel jLabelDeliveredMoles;
	private PAmountComponent deliveredMolesView; 
	//private AmountModel deliveredVolumeModel; 
	//Reaction moles
	private CeNLabel jLabelRxNMoles;
	private PAmountComponent reactionMolesView;
	
	private Object lastObject;

	private ReactionStepModel stepModel;

	/**
	 * Constructor used to build the MonomerPlatePlateViewPanel.  This
	 * constructor is invoked when a new instance is created for a new
	 * plate tab view.
	 * @param monomerPlate
	 * @param stepModel 
	 */
	public MonomerBatchDetailContainer(MonomerPlate monomerPlate, NotebookPageModel notebookPageModel, ReactionStepModel stepModel) {
		this.monomerPlate = monomerPlate;
		this.notebookPageModel = notebookPageModel;
		this.stepModel = stepModel;
		// If the plate has not yet been build, the structures will not have been added to the compounds
		if (this.monomerPlate.getWells().length > 0) {
			PlateWell[] wells = this.monomerPlate.getWells();
			if (wells[0].getBatch().getCompound().getMolfile() == null)
				this.getStructuresfromCLS4MonomerPlate(this.monomerPlate);
		}
		this.selectFirstWell();
		this.componentUtility = BatchAttributeComponentUtility.getInstance();
		this.initGui();
		this.populate();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.isLoading = false;
	}
	
	/**
	 * Constructor used by plate visualization API when the mouse over well event occurs.
	 * @param well
	 * @param plateBarcode
	 */
	public MonomerBatchDetailContainer(PlateWell well, String plateBarcode) {
		// In this case, no monomer plate is defined so create one and set its barcode.
		monomerPlate = new MonomerPlate();
		monomerPlate.setPlateBarCode(plateBarcode);
		//this.setWellProperties(well);
		//this.buildPanel();
	}
	
	/**
	 * Batch selection has changed.  Recreate the panel and refresh the parent's panel.
	 * @param event contains the PlateWell object
	 */
	public void batchSelectionChanged(BatchSelectionEvent event) {
		if (event != null)
			lastObject = event.getSubObject();
		
		if (lastObject instanceof PlateWell) {			
			this.well = (PlateWell) lastObject;
			this.monomerBatchModel = (MonomerBatchModel) well.getBatch();
			this.enableComponents(true);
			this.populate();
		} else { // this is an empty well
			this.well = null;
			this.monomerBatchModel = null;
			this.clear();
		}
		
		this.removeAll();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.revalidate();
		this.repaint();
	}

	private void initGui() {
		//jLabelProductPlate = makeLabel(PRODUCT_PLATE, PLAIN_LABEL_FONT);
		//jLabelProductPlateName = makeLabel("", PLAIN_LABEL_FONT);

		jLabelWell = makeCeNLabel(WELL, PLAIN_LABEL_FONT);
		jLabelWellName = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelWellSequence = makeCeNLabel(SEQUENCE, PLAIN_LABEL_FONT);
		jLabelWellSequenceValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelNbkBatchNum = makeCeNLabel(NOTEBOOK_BATCH_NUM, PLAIN_LABEL_FONT);
		jLabelNbkBatchNumNbkPageValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelStatus = makeCeNLabel(STATUS, PLAIN_LABEL_FONT);
		jComboBoxStatus = new CeNComboBox();
		jComboBoxStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(STATUS);
			}
		});	
		
		// Design monomer
		jLabelDesignMonomer = makeCeNLabel(DESIGN_MONOMER, PLAIN_LABEL_FONT);
		jLabelDesignMonomerValue = makePlainLabel("", PLAIN_LABEL_FONT);

		// Delivered monomer
		jLabelDeliveredMonomer = makeCeNLabel(DELIVERED_MONOMER, PLAIN_LABEL_FONT);
		jLabelDeliveredMonomerValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelMolFormula = makeCeNLabel(MOLECULAR_FORMULA, PLAIN_LABEL_FONT);
		jLabelMolFormulaValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		// Parent MW	
		jLabelParentMW = makeCeNLabel(PARENT_MW, PLAIN_LABEL_FONT);
		//parentMWModel = new AmountModel(UnitType.SCALAR); // vb 11/14 was mass
		parentMWView = makeAmountComponent();
		// Batch MW 
		jLabelBatchMW = makeCeNLabel(BATCH_MW, PLAIN_LABEL_FONT);
		//batchMWModel = new AmountModel(UnitType.SCALAR);  // vb 11/14 was mass
		batchMWView = makeAmountComponent();
		// Delivered weight
		jLabelDeliveredWeight = makeCeNLabel(DELIVERED_WEIGHT, PLAIN_LABEL_FONT);
		//deliveredWeightModel = new AmountModel(UnitType.MASS);
		deliveredWeightView = makeAmountComponent();
		
		// Reaction weight
		jLabelReactionWeight = makeCeNLabel(REACTION_WEIGHT, PLAIN_LABEL_FONT);
		//reactionWeightModel = new AmountModel(UnitType.MASS);
		reactionWeightView = makeAmountComponent();
		// Delivered volume
		jLabelDeliveredMoles = makeCeNLabel(DELIVERED_MOLES, PLAIN_LABEL_FONT);
		//deliveredVolumeModel = new AmountModel(UnitType.VOLUME);
		deliveredMolesView = makeAmountComponent();

		jLabelRxNMoles = makeCeNLabel(REACTION_MOLES, PLAIN_LABEL_FONT);
		reactionMolesView = makeAmountComponent();
	    MonomerBatchStatusMapper.getInstance().fillComboBox(this.jComboBoxStatus);
	}

	/**
	 * Build the panel from its components.
	 *
	 */
	public void populate() {
//		if (isLoading)
//			return;
		isLoading = true;
		if (this.monomerBatchModel == null) {
			isEditable = false;
			isLoading = false;
			this.enableComponents(false);
		} else {
			isEditable = true;
			this.jLabelWellName.setText(this.well.getWellNumber());
			this.jLabelWellSequenceValue.setText("" + this.well.getWellPosition());
			jLabelNbkBatchNumNbkPageValue.setText(this.monomerBatchModel.getBatchNumber().getBatchNumber());
		
			// NOTE:  for now, we are just saving the combined statuses for monomer batches
			// to the "status" field, so we don't need the MonomerBatchStatusModel.  
			String status = monomerBatchModel.getStatus();
			if (status == null || status.length() == 0)
				this.jComboBoxStatus.setSelectedIndex(0);
			else 
				this.jComboBoxStatus.setSelectedItem(status);
		    
			jLabelDesignMonomerValue.setText(this.monomerBatchModel.getMonomerId());
			
			/**Need to find out whether it comes from ASDI file/From CompoundManagement/Monomer(Matrack) order**/
			jLabelDeliveredMonomerValue.setText(this.monomerBatchModel.getMonomerId());
			
			jLabelMolFormulaValue.setText(this.monomerBatchModel.getMolecularFormula());
			jLabelMolFormulaValue.setToolTipText(this.monomerBatchModel.getMolecularFormula());
			parentMWView.setValue(CeNFormatter.formatMolecularWeight(monomerBatchModel.getCompound().getMolWgt()));
			//parentMWView.setValue(BatchAttributeComponentUtility.getMolWgt(monomerBatchModel));			
			//batchMWModel.setValue(.getValue());
			batchMWView.setValue(CeNFormatter.formatMolecularWeight(monomerBatchModel.getMolecularWeightAmount().GetValueInStdUnitsAsDouble()));
			//monomerBatchModel.getMolecularFormula()***************************************
			//deliveredWeightModel.setValue();
			deliveredWeightView.setValue(monomerBatchModel.getDeliveredWeight());
			//reactionWeightModel.setValue(monomerBatchModel.getRxnEquivsAmount().getValue());
			reactionWeightView.setValue(monomerBatchModel.getStoicWeightAmount());
			//deliveredVolumeModel.setValue(monomerBatchModel.getDeliveredVolume().getValue());
/*			if (monomerBatchModel.getDeliveredVolume().getValue().toLowerCase().indexOf("n") >= 0)
				System.out.println("delivered volume is NaN"); // vb 11/9  we need to do something about this!
			else 
*/			deliveredMolesView.setValue(new AmountModel(UnitType.MOLES, monomerBatchModel.getDeliveredWeight().GetValueInStdUnitsAsDouble()/monomerBatchModel.getMolWgt()));
			reactionMolesView.setValue(monomerBatchModel.getMoleAmount());
			// Enable the components 
			this.enableComponents(true);
		}
		isLoading = false;
	}
	
	public void clear() {
		isEditable = false;
		this.jLabelWellName.setText("");
		this.jLabelWellName.setEnabled(false);
		this.jLabelWellSequenceValue.setText("");
		this.jLabelWellSequenceValue.setEnabled(false);
		this.jLabelNbkBatchNumNbkPageValue.setText("");
		this.jLabelNbkBatchNumNbkPageValue.setEnabled(false);
		this.jComboBoxStatus.setSelectedIndex(0);
		this.jComboBoxStatus.setEnabled(false);
		jLabelDesignMonomerValue.setText("");
		jLabelDesignMonomerValue.setEnabled(false);
		jLabelDeliveredMonomerValue.setText("");
		jLabelDeliveredMonomerValue.setEnabled(false);
		jLabelMolFormulaValue.setText("");
		jLabelMolFormulaValue.setToolTipText("");
		jLabelMolFormulaValue.setEnabled(false);
		this.parentMWView.setValue(new AmountModel(UnitType.SCALAR));	
		//this.parentMWView.setEditable(false);
		this.batchMWView.setValue(new AmountModel(UnitType.SCALAR));	
		//this.batchMWView.setEditable(false);
		deliveredWeightView.setValue(new AmountModel(UnitType.MASS));
		deliveredWeightView.setEditable(false);
		reactionWeightView.setValue(new AmountModel(UnitType.MASS));	
		//reactionWeightView.setEditable(false);	
		deliveredMolesView.setValue(new AmountModel(UnitType.MOLES));
		deliveredMolesView.setEditable(false);
		reactionMolesView.setValue(new AmountModel(UnitType.MOLES));
		//this.enableComponents(false);  this makes the amount components slits
	}
	
	/**
	 * To do -- which fields are editable?
	 * @param enable
	 */
	private void enableComponents(boolean enable) {
		isEditable = enable;
		this.jLabelWellName.setEnabled(enable);
		this.jLabelDesignMonomerValue.setEnabled(enable);
		this.jLabelMolFormulaValue.setEnabled(enable);
		this.jLabelDeliveredMonomerValue.setEnabled(enable);
		this.jComboBoxStatus.setEnabled(enable);
		
		this.parentMWView.setEditable(enable);
		this.parentMWView.setEnabled(enable);
		
		this.batchMWView.setEditable(enable);
		this.batchMWView.setEnabled(enable);
		
		this.reactionWeightView.setEditable(enable);
		this.reactionWeightView.setEnabled(enable);
		
		this.reactionMolesView.setEditable(enable);
		this.reactionMolesView.setEnabled(enable);
	
		this.deliveredWeightView.setEditable(enable);
		this.deliveredWeightView.setEnabled(enable);
		
		this.deliveredMolesView.setEditable(enable);
		this.deliveredMolesView.setEnabled(enable);
		
		
		
	}

	/**
	 * Create the part of the view consisting of the list of poroperties.
	 * @return
	 */
	public JPanel getPanel(){
		StringBuffer rowFormatBuff = new StringBuffer();
		for (int i=0; i<NUM_ROWS-1; i++) {
			rowFormatBuff.append("pref, ");
		}
		rowFormatBuff.append("pref");
		FormLayout layout = new FormLayout(
				"pref, 7dlu, 63dlu, 30dlu",
				   rowFormatBuff.toString());						   
		// Set row groups (all rows are in group)
		int[][] rowGroups = new int[1][NUM_ROWS];
		for (int i=0; i<NUM_ROWS; i++) {
			rowGroups[0][i] = i+1;
		}
		layout.setRowGroups(rowGroups);
		
		//PanelBuilder builder = new PanelBuilder(layout, new FormDebugPanel());
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		int row = 1;
		int col1 = 1;
		int col2 = 3;
			
		CellConstraints cc = new CellConstraints();
		//builder.add(jLabelProductPlate,                 cc.xy (col1,   row));
		//builder.add(jLabelProductPlateName,             cc.xy (col2,   row)); 	  row++;
		builder.add(jLabelWell,                 		cc.xy (col1,   row));
		builder.add(jLabelWellName,             		cc.xy (col2,   row));     row++;
		//builder.add(jLabelWellSequence,             	cc.xy (col1,   row));
		//builder.add(jLabelWellSequenceValue,        	cc.xy (col2,   row));     row++;
		//builder.add(jLabelNbkBatchNum,                  cc.xy (col1,   row)); 
		//builder.add(jLabelNbkBatchNumNbkPageValue,      cc.xy (col2,   row));     row++;
		//builder.add(jTextFieldNbkBatchNumSeqValue,      cc.xy (col3,   row));     row++;
		builder.add(jLabelStatus,                       cc.xy  (col1,   row));
		builder.add(jComboBoxStatus,    				cc.xyw  (col2,   row, 2));      row++;
		builder.add(jLabelDesignMonomer,                cc.xy  (col1,   row));
		builder.add(jLabelDesignMonomerValue,           cc.xyw  (col2,   row, 2));     row++;
		builder.add(jLabelDeliveredMonomer,             cc.xy  (col1,   row));
		builder.add(jLabelDeliveredMonomerValue,        cc.xyw  (col2,   row, 2));     row++;
		builder.add(jLabelMolFormula,             		cc.xy  (col1,   row));
		builder.add(jLabelMolFormulaValue,        		cc.xyw  (col2,   row, 2));     row++;
		builder.add(jLabelParentMW,                     cc.xy  (col1,   row));
		builder.add(parentMWView,                       cc.xy  (col2,   row));     row++;
		builder.add(jLabelBatchMW,                      cc.xy  (col1,   row));
		builder.add(batchMWView,                        cc.xy  (col2,   row));     row++;
		builder.add(jLabelDeliveredWeight,              cc.xy  (col1,   row));
		builder.add(deliveredWeightView,                cc.xy  (col2,   row));     row++;
		builder.add(jLabelReactionWeight,               cc.xy  (col1,   row));
		builder.add(reactionWeightView,                 cc.xy  (col2,   row));     row++;		
		builder.add(jLabelDeliveredMoles,              cc.xy  (col1,   row));
		builder.add(deliveredMolesView,                cc.xy  (col2,   row));     row++;
		builder.add(jLabelRxNMoles,             		cc.xy  (col1,   row));
		builder.add(reactionMolesView,        			cc.xy  (col2,   row));     row++; row++;
		builder.add(this.getChimePanel(),               cc.xywh(col1,   row, 4, 6));
		return builder.getPanel();
	}
	
	private void updateProperty(String property) {
		if (isLoading || !isEditable || this.monomerBatchModel == null)
			return;
		isLoading = true;
		if (property != null && property.length() > 0) {
			if (property.equals(PARENT_MW)) {
				if (BatchAttributeComponentUtility.setParentMolWeight(monomerBatchModel, parentMWView.getAmount()))
					enableSaveButton();
			} else if (property.equals(BATCH_MW)) {
				if (BatchAttributeComponentUtility.setMolWeight(monomerBatchModel, batchMWView.getAmount()))
					enableSaveButton();
	    	} else 
	    		if (property.equals(DELIVERED_WEIGHT)) {
	    			if (BatchAttributeComponentUtility.setDeliveredWeight(monomerBatchModel, deliveredWeightView.getAmount()))
	    				enableSaveButton();
	    	} else if (property.equals(REACTION_WEIGHT)) {
				if (BatchAttributeComponentUtility.setRxNWeight(monomerBatchModel, reactionWeightView.getAmount(), well))
				{
					updateAllListLevelFlags();
					enableSaveButton();
				}
	    	} else if (property.equals(REACTION_MOLES)) {
				if (BatchAttributeComponentUtility.setRxNMoles(monomerBatchModel, reactionMolesView.getAmount()))
				{
					updateAllListLevelFlags();	    		
					enableSaveButton();
				}
	    	} else if (property.equals(DELIVERED_MOLES)) {
	    		if (BatchAttributeComponentUtility.setDeliveredMoles(monomerBatchModel, deliveredMolesView.getAmount()))
	    			enableSaveButton();
	    	} else if (property.equals(STATUS)) {
	    		// vb 7/17 put fix in here
	    		BatchAttributeComponentUtility.propagateMonomerBatchStatus((MonomerBatchModel) monomerBatchModel, (String)jComboBoxStatus.getSelectedItem(), notebookPageModel);
				this.monomerBatchModel.setStatus((String) jComboBoxStatus.getSelectedItem());
				enableSaveButton();
	    	} 
			populate();
		}
		isLoading = false;
	}
	
	private void enableSaveButton() {
		if (this.monomerBatchModel != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			this.monomerBatchModel.setModelChanged(true);
		}
	}
	
	private void updateAllListLevelFlags()
	{
		ArrayList monomerLists = stepModel.getMonomers();
		//Since we do not know what monomer lists form a plate, loop is run to check all Mononer list in the current step.
		for (int i = 0; i < monomerLists.size(); i++) {
			((BatchesList) monomerLists.get(i)).updateAllListLevelFlags();
		}
	}
	
	/**
	 * If this is a new plate view, set the detail well to the first well in the plate.
	 *
	 */
	private void selectFirstWell() {
		if (this.monomerPlate != null) {
			PlateWell[] wells = this.monomerPlate.getWells();
			if (wells != null && wells.length > 0) {
				this.monomerBatchModel = (MonomerBatchModel) wells[0].getBatch();
				this.well = wells[0];
			}
		}
	}

	private CeNLabel makeCeNLabel(String text, Font font) {
		CeNLabel label = new CeNLabel(text);
		//label.setFont(font);
		return label;
	}

	private JLabel makePlainLabel(String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		return label;
	}
	
//	private JTextField makeTextField(String text, int size, String property) {
//		JTextField textField = new JTextField(text, size);
//		textField.setFont(EDIT_FONT_SMALL);
//		//textField.getDocument().addDocumentListener(new BatchDocumentListener());
//		//textField.addFocusListener(new PropertyFocusListener(property));
//		return textField;
//	}

	private PAmountComponent makeAmountComponent() {
		PAmountComponent amountComponent = new PAmountComponent();
		amountComponent.setEditable(true);
		amountComponent.setValueSetTextColor(null);
		amountComponent.addAmountEditListener(this);
		amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());	
		return amountComponent;		
	}
	
	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	public JPanel getChimePanel() {
		if (well == null)
			return new JPanel();
		Object molstr = this.notebookPageModel.getMonomerBatchMolStringMap().get(monomerBatchModel.getKey());
		ChemistryPanel chime = new ChemistryPanel();
		//String molfile = monomerBatchModel.getCompound().getMolfile();
		if (molstr != null)
			chime.setMolfileData(molstr.toString());
		else {
			//log.error("molfile is null for " + monomerBatchModel.getCompoundId());
			return new JPanel();
		}
		return chime;
	}

	public void getStructuresfromCLS4MonomerPlate(MonomerPlate monomerPlate) {
//		try {
//			String structures[] = CLSLookup2.getInstance().getStructuresNoBatching(monomerPlate.getBatchIDs());
//			PlateWell[] wells = monomerPlate.getWells();
//			if (structures.length == wells.length) {
//				for (int i = 0; i < wells.length; i++) {
//					// //System.out.println(structures[i]);
//					wells[i].getBatch().getCompound().setMolfile(structures[i]);
//				}
//			}
//		} catch (Exception ex) {
//			CeNErrorHandler.showMsgOptionPane(this.getParent(), "CLS Lookup Error", "CLS lookup failed", JOptionPane.ERROR_MESSAGE);
//			log.error("CLS lookup failed");
//		}

		PlateWell[] wells = monomerPlate.getWells();
		ArrayList batches = new ArrayList();
		for (int i=0; i<wells.length; i++)
			batches.add(wells[i].getBatch());
		LinkedHashMap map = BatchAttributeComponentUtility.getCachedMonomerBatchesToMolstringsMap(batches, this.notebookPageModel);
		for (int i=0; i<wells.length; i++) {
			BatchModel batch = wells[i].getBatch();
			String key = batch.getKey();
			MolString molstr = (MolString) map.get(key);
			wells[i].getBatch().getCompound().setMolfile(molstr.getMolString());
		}
	}

	/**
	 * Get a panel representing an empty well.
	 * @param plateBarcode
	 * @param wellPosition
	 * @return
	 */
	public JPanel getEmptyWellPanel(String plateBarcode, String wellPosition) {
		FormLayout layout = new FormLayout(
				"right:max(40dlu;pref), 5dlu, pref, 7dlu",
				""); 
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append("Plate");
		builder.append(plateBarcode);
		builder.nextLine();
		builder.append("Well");
		builder.append(wellPosition);
		return builder.getPanel();
	}
	

	public void editingCanceled(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void editingStopped(ChangeEvent e) {
		Object o = e.getSource();
		if (o instanceof PAmountComponent) {
			if (o == this.parentMWView)
				this.updateProperty(PARENT_MW);
			else if (o == this.batchMWView)
				this.updateProperty(BATCH_MW);
			else 
			if (o == this.deliveredWeightView)
				this.updateProperty(DELIVERED_WEIGHT);
			else if (o == this.reactionWeightView) 
				this.updateProperty(REACTION_WEIGHT);
			else if (o == this.reactionMolesView) 
				this.updateProperty(REACTION_MOLES);
			else if (o == this.deliveredMolesView)
				this.updateProperty(DELIVERED_MOLES);
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Use this method to "dispose" of the resources.
	 */
//	public void setVisible(boolean flag) {
//		super.setVisible(flag);
//		if (! flag) {
//			if (this.monomerPlate != null && this.monomerPlate.getWells() != null) {
//				PlateWell[] wells = PlateWell[] wells = this.monomerPlate.getWells();
//				for (int i=0; i<wells.length; i++)
//					MonomerBatchModel batch = (MonomerBatchModel) 
//					if (wells[0].getBatch().getCompound().getMolfile() == null)
//						this.getStructuresfromCLS4MonomerPlate(this.monomerPlate);
//				}
//			}
//		}
//	}



}

