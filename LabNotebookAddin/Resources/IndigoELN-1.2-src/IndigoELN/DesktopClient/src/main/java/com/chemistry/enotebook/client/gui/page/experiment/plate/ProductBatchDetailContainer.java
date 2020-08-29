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
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.utils.Disposable;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.utils.*;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.virtuan.plateVisualizer.WellProperty;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductBatchDetailContainer extends JPanel implements BatchSelectionListener, Disposable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4767701362969413044L;
	private NotebookPageModel notebookPageModel = null;
	private ProductPlate productPlate;
	private ProductBatchModel productBatchModel = null;
	private ArrayList wellProperties = new ArrayList();
	private static final String SEQUENCE = "Sequence";
	private static final String PRODUCT_PLATE = "Plate";
	private static final String WELL = PCeNTableView.WELL_POSITION;
	private static final String NOTEBOOK_BATCH_NUM = PCeNTableView.NBK_BATCH_NUM;
	private static final String NOTEBOOK_BATCH_NUM_EDITABLE = "Notebook Batch # Editable";
	private static final String STATUS = PCeNTableView.STATUS; //"Status";
	private static final String CONVERSATIONAL_BATCH_NUM = "Conversational Batch #";
	private static final String VIRTUAL_COMPOUND_NUM = PCeNTableView.VIRTUAL_COMP_NUM; //"Virtual Compound (VC)#";
	private static final String STEREOISOMER = PCeNTableView.STEREOISOMER; //"Stereoisomer";
	private static final String PRECURSOR_REACTANT_IDS = PCeNTableView.PRECURSORS; //"Precursor/Reactant IDs";
	private static final String CALCULATED_BATCH_MW = PCeNTableView.MOL_WEIGHT; //"Calculated Batch MW";
	private static final String CALCULATED_BATCH_MF = PCeNTableView.MOL_FORMULA; //"Calculated Batch MF";
	private static final String M_MOLES = PCeNTableView.THEORETICAL_MMOLES; //"mMoles";
	private static final String THEORETICAL_AMOUNTS = PCeNTableView.THEORETICAL_WEIGHT; //"Theo. Amounts";
	private static final String CONTAINED_WEIGHT = "Contained Weight";
	private static final String SALT_CODE_AND_NAME = PCeNTableView.SALT_CODE; //"Salt Code & Name";
	private static final String SALT_EQUIVALENT = PCeNTableView.SALT_EQ; //"Salt Equivalent";
	private static final String AMOUNT_IN_WELL = "Amount in Well";
	private static final String HEALTH_HAZARDS = PCeNTableView.EXPTAB_HAZARD_COMMENTS; //"Health Hazards";
	

	public static final Font BOLD_LABEL_FONT = new java.awt.Font("Arial", Font.BOLD, 12);
	public static final Font PLAIN_LABEL_FONT = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font EDIT_FONT_SMALL = new java.awt.Font("Arial", Font.PLAIN, 11);
	public static final Font EDIT_FONT_LARGE = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font COMBO_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final Font BUTTON_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final int NUM_ROWS = 17;
	
	// Flags
	private boolean isEditable = true;
	private boolean isLoading = false;
	private boolean isChanging = false;
	
	// GUI components

	// Plate and well 
	//private CeNLabel jLabelProductPlate;
	//private JLabel jLabelProductPlateName;
	private CeNLabel jLabelWell;
	private JLabel jLabelWellName;
	//private CeNLabel jLabelWellSequence;
	private JLabel jLabelWellSequenceValue;
	// Noteook batch number
	private CeNLabel jLabelNbkBatchNum;
	private JLabel jLabelNbkBatchNumNbkPageValue;
	private JTextField jTextFieldNbkBatchNumSeqValue;
	// Status
	private CeNLabel jLabelStatus;
	private CeNComboBox jComboBoxStatus;
	// Conversion batch number
	private CeNLabel jLabelConvBatchNum;
	private JLabel jLabelConvBatchNumValue;
	// Virtual compound number
	private CeNLabel jLabelVirtualCompNum;
	private JLabel jLabelVirtualCompNumValue;
	// Stereoisomer
	private CeNLabel jLabelStereoisomer;
	private CeNComboBox jComboBoxStereoisomer;
	// Calculated batch MW
	private CeNLabel jLabelCalcBatchMW;
	private JLabel jLabelCalcBatchMWValue;
	//private JTextField jTextFieldCalcBatchMWValue;
	// Calculated batch MF
	private CeNLabel jLabelCalcBatchMF;
	private JLabel jLabelCalcBatchMFValue;
	// Salt code
	private CeNLabel jLabelSaltCode;
	private CeNComboBox jComboBoxSaltCode;
	// Salt equivalent
	private CeNLabel jLabelSaltEquivalents;
	private PAmountComponent saltEquivalentsView;
	//private AmountModel saltEquivalentsModel;
	// Total amount made
	//private CeNLabel jLabelTotAmountMade;
	//private JTextField jTextFieldTotAmountMade;
	//private JComboBox jComboBoxTotAmountMadeUnits;
	// Theoretical amounts
	private CeNLabel jLabelTheoreticalAmounts;
	private JLabel jLabelTheoreticalAmountsWeight;
	// mMoles
	private CeNLabel jLabelMMoles;
	//private PAmountComponent mMolesView; // vb 7/27
	private JLabel jLabelTheoreticalMolesValue;
	//private AmountModel mMolesModel; 
	// Amount in well
	private CeNLabel jLabelAmountInWell;
	private PAmountComponent amountInWellView; // vb 7/27
	private AmountModel amountInWellModel; // vb 7/27
//	private JTextField jTextFieldAmountInWell;
//	private JComboBox jComboBoxAmountInWellUnits;
	// Precursor/Reactant ids
	private CeNLabel jLabelPrecursors;
	private JLabel jLabelPrecursorsValues;
	// Health hazards
	private CeNLabel jLabelHealthHazards;
	private JTextArea jTextAreaHealthHazards;
	
	//private NotebookPageModel pageModel = null;
	private PlateWell well = null;
	private BatchAttributeComponentUtility componentUtility = null;
	//private ProductBatchModel batchModel = null;

	/**
	 * Constructor used to build the ProductPlatePlateViewPanel.  This
	 * constructor is invoked when a new instance is created for a new
	 * plate tab view.
	 * @param productPlate
	 */
	public ProductBatchDetailContainer(ProductPlate productPlate, NotebookPageModel notebookPageModel) {
		this.isLoading = true;  // vb 7/16
		this.productPlate = productPlate;
		// If the plate has not yet been build, the structures will not have been added to the compounds
		if (this.productPlate.getWells().length > 0) {
			PlateWell[] wells = this.productPlate.getWells();
			if (wells[0].getBatch().getCompound().getMolfile() == null)
				this.getStructuresfromCLS4MonomerPlate(this.productPlate);
		}
		this.notebookPageModel = notebookPageModel;
		this.selectFirstWell();
		this.componentUtility = BatchAttributeComponentUtility.getInstance();
		this.initGui();
		this.populate();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.isLoading = false; // vb 7/16
	}
	
	public void dispose() {
		if (this.wellProperties != null)
			for (Iterator it = wellProperties.iterator(); it.hasNext();) {
				Object obj = it.next();
				obj = null;
			}
	}

	/**
	 * Constructor used by plate visualization API when the mouse over well event occurs.
	 * @param well
	 * @param plateBarcode REMOVE THIS
	 */
	public ProductBatchDetailContainer(PlateWell well, String plateBarcode) {
		// In this case, no product plate is defined so create one and set its barcode.
		productPlate = new ProductPlate();
		productPlate.setPlateBarCode(plateBarcode);
		//this.setWellProperties(well);
		//this.buildPanel();
	}
	
	private void initGui() {
		//jLabelProductPlate = makeLabel(PRODUCT_PLATE, PLAIN_LABEL_FONT);
		//jLabelProductPlateName = makeLabel("", PLAIN_LABEL_FONT);

		jLabelWell = makeCeNLabel(WELL, PLAIN_LABEL_FONT);
		jLabelWellName = makePlainLabel("", PLAIN_LABEL_FONT);
		
		//jLabelWellSequence = makeCeNLabel(SEQUENCE, PLAIN_LABEL_FONT);
		jLabelWellSequenceValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelNbkBatchNum = makeCeNLabel(NOTEBOOK_BATCH_NUM, PLAIN_LABEL_FONT);
		jLabelNbkBatchNumNbkPageValue = makePlainLabel("", PLAIN_LABEL_FONT);
		jTextFieldNbkBatchNumSeqValue = makeTextField("", 6, NOTEBOOK_BATCH_NUM_EDITABLE);
		
		jLabelStatus = makeCeNLabel(STATUS, PLAIN_LABEL_FONT);
		jComboBoxStatus = new CeNComboBox();
		jComboBoxStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(STATUS);
			}
		});	
		
		jLabelConvBatchNum = makeCeNLabel(CONVERSATIONAL_BATCH_NUM, PLAIN_LABEL_FONT);
		jLabelConvBatchNumValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelVirtualCompNum = makeCeNLabel(VIRTUAL_COMPOUND_NUM, PLAIN_LABEL_FONT);
		jLabelVirtualCompNumValue = makePlainLabel("", PLAIN_LABEL_FONT);
		
		jLabelStereoisomer = makeCeNLabel(STEREOISOMER, PLAIN_LABEL_FONT);
		jComboBoxStereoisomer = new CeNComboBox(); 

		// Precursor/Reactant ids
		jLabelPrecursors = makeCeNLabel(PRECURSOR_REACTANT_IDS, PLAIN_LABEL_FONT);
		jLabelPrecursorsValues = makePlainLabel("", PLAIN_LABEL_FONT);
		
		// Calculated batch MW and MF		
		jLabelCalcBatchMW = makeCeNLabel(CALCULATED_BATCH_MW, PLAIN_LABEL_FONT);
		jLabelCalcBatchMWValue = makePlainLabel("", PLAIN_LABEL_FONT);
		jLabelCalcBatchMF = makeCeNLabel(CALCULATED_BATCH_MF, PLAIN_LABEL_FONT);
		jLabelCalcBatchMFValue = makePlainLabel("", PLAIN_LABEL_FONT);
		

		// Theoretical amounts
		jLabelTheoreticalAmounts = makeCeNLabel(THEORETICAL_AMOUNTS, PLAIN_LABEL_FONT);
		jLabelTheoreticalAmountsWeight = makePlainLabel("", PLAIN_LABEL_FONT);
		
		// mMoles
		jLabelMMoles = makeCeNLabel(M_MOLES, PLAIN_LABEL_FONT);
		jLabelTheoreticalMolesValue = makePlainLabel("", PLAIN_LABEL_FONT);
		//mMolesModel = new AmountModel(UnitType.SCALAR);
//		mMolesView = new PAmountComponent();
//		mMolesView.setEditable(false);
//		mMolesView.setValueSetTextColor(null);
//		mMolesView.setBorder(BorderFactory.createLoweredBevelBorder());
//		mMolesView.addAmountEditListener(new AmountEditListener() {
//			public void editingCanceled(ChangeEvent e) {
//			}
//			public void editingStopped(ChangeEvent e) {
//				updateProperty(M_MOLES);
//			}
//		});
		
		// Salt code and equivalents
		jLabelSaltCode = makeCeNLabel(SALT_CODE_AND_NAME, PLAIN_LABEL_FONT);
		jComboBoxSaltCode = new CeNComboBox();
		jComboBoxSaltCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading) 
					updateProperty(ProductBatchDetailContainer.SALT_CODE_AND_NAME);
			}
		});
		jLabelSaltEquivalents = makeCeNLabel(SALT_EQUIVALENT, PLAIN_LABEL_FONT);
		//saltEquivalentsModel = new AmountModel(UnitType.SCALAR);
		saltEquivalentsView = new PAmountComponent();
		saltEquivalentsView.setEditable(true);
		saltEquivalentsView.setValueSetTextColor(null);
		saltEquivalentsView.addAmountEditListener(new AmountEditListener() {
			public void editingCanceled(ChangeEvent e) {
			}
			public void editingStopped(ChangeEvent e) {
				updateProperty(SALT_EQUIVALENT);
			}
		});
		
//		jTextFieldSaltEquivalents = makeTextField("", 15, SALT_EQUIVALENT);
//		jTextFieldSaltEquivalents.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
//		jTextFieldSaltEquivalents.getDocument().addDocumentListener(new BatchDocumentListener());		
		// Amount in well
		amountInWellModel = new AmountModel(UnitType.MASS);
		jLabelAmountInWell = makeCeNLabel(AMOUNT_IN_WELL, PLAIN_LABEL_FONT);
		amountInWellView = new PAmountComponent();
		amountInWellView.setEditable(true);
		amountInWellView.setValueSetTextColor(null);
		amountInWellView.addAmountEditListener(new AmountEditListener() {
			public void editingCanceled(ChangeEvent e) {
			}
			public void editingStopped(ChangeEvent e) {
				updateProperty(AMOUNT_IN_WELL);
			}
		});
		amountInWellView.setBorder(BorderFactory.createLoweredBevelBorder());		
		//jLabelAmountInWell = makeLabel(AMOUNT_IN_WELL, PLAIN_LABEL_FONT);
		//jTextFieldAmountInWell = makeTextField("", 15, AMOUNT_IN_WELL);
		//String[] wellAmountUnits = {"mg", "mL"};
		//jComboBoxAmountInWellUnits = makeComboBox(wellAmountUnits);
		
		// Health hazards
		jLabelHealthHazards = makeCeNLabel(HEALTH_HAZARDS, PLAIN_LABEL_FONT);
		jTextAreaHealthHazards = makeTextArea("", 3, 20, HEALTH_HAZARDS);
		
		// Populate code table dropdowns -- do these ever need to be updated?
		this.componentUtility.fillStereoisomerComboBox(jComboBoxStereoisomer);
		this.jComboBoxStereoisomer.insertItemAt("", 0);
		this.jComboBoxStereoisomer.setEnabled(false);
		CodeTableUtils.fillComboBoxWithSaltCodes(jComboBoxSaltCode);
	    ProductBatchStatusMapper.getInstance().fillComboBox(this.jComboBoxStatus);
	    CodeTableUtils.fillComboBoxWithSaltCodes(jComboBoxSaltCode);
	}
	
	/**
	 * Batch selection has changed.  Recreate the panel and refresh the parent's panel.
	 * @param event contains the PlateWell object
	 */
	public void batchSelectionChanged(BatchSelectionEvent event) {
		Object obj = event.getSubObject();
		if (obj instanceof PlateWell) {			
			this.well = (PlateWell) obj;
			this.productBatchModel = (ProductBatchModel) well.getBatch();
			//this.setWellProperties(well);
			this.populate();
		} else { // this is an empty well
			this.well = null;
			this.clear();
		}
		this.removeAll();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.revalidate();
		this.repaint();
	}
	
	public void refresh() {
		this.populate();
		this.revalidate();
		//this.repaint();
	}
	
	
	/**
	 * If this is a new plate view, set the detail well to the first well in the plate.
	 *
	 */
	private void selectFirstWell() {
		if (this.productPlate != null) {
			PlateWell[] wells = this.productPlate.getWells();
			if (wells != null && wells.length > 0) {
				this.well = wells[0];
				this.productBatchModel = (ProductBatchModel) wells[0].getBatch();
			}
		}
	}

	/**
	 * Build the panel from its components.
	 *
	 */
	public void populate() {
//		if (isLoading)
//			return;
		if (this.well == null)
			return;
		isLoading = true;
		if (this.productBatchModel == null) {
			isEditable = false;
			this.enableComponents(false);
		} else {
			isEditable = true;
			this.jLabelWellName.setText(this.well.getWellNumber());
			this.jLabelWellSequenceValue.setText("" + this.well.getWellPosition());
			String nbkBatchNumber = this.productBatchModel.getBatchNumber().getBatchNumber();
			String conststr = nbkBatchNumber.substring(0, nbkBatchNumber.lastIndexOf("-") + 1);
			jLabelNbkBatchNumNbkPageValue.setText(conststr);
			String editstr = nbkBatchNumber.substring(nbkBatchNumber.lastIndexOf("-") + 1);
			jTextFieldNbkBatchNumSeqValue.setText(editstr);
			jTextFieldNbkBatchNumSeqValue.setEditable(false);
			jTextFieldNbkBatchNumSeqValue.setEnabled(false);
			jLabelConvBatchNumValue.setText(this.productBatchModel.getRegInfo().getConversationalBatchNumber());
			jLabelVirtualCompNumValue.setText(this.productBatchModel.getCompound().getVirtualCompoundId());
			//jLabelCalcBatchMWValue.setText(productBatchModel.getMolecularWeightAmount().getValue());
			double d = productBatchModel.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
			jLabelCalcBatchMWValue.setText(PCeNRegistrationProductsTableModelConnector.formatWeight(d));
			jLabelCalcBatchMFValue.setText(productBatchModel.getMolecularFormula());
			this.jLabelTheoreticalMolesValue.setText(productBatchModel.getTheoreticalMoleAmount().GetValueForDisplay() + " mMole");
			//mMolesView.setValue(BatchAttributeComponentUtility.getTheoreticalMoleAmount(productBatchModel));
			this.jLabelTheoreticalAmountsWeight.setText(productBatchModel.getTheoreticalWeightAmount().GetValueForDisplay() + " mg");
			//jTextFieldAmountInWell.setText("" + well.getContainedAmount().getValue());
			this.componentUtility.updateStereoisomerComboBox(productBatchModel, jComboBoxStereoisomer);
			// Get the selectivity and continue status
			int selectivityStatus = this.productBatchModel.getSelectivityStatus();
			int continueStatus = this.productBatchModel.getContinueStatus();
			ProductBatchStatusMapper.getInstance().setStatus(this.jComboBoxStatus, selectivityStatus, continueStatus);
			// Salt code and equivalent
			this.componentUtility.fillSaltCodesComboBox(this.jComboBoxSaltCode, productBatchModel);
			this.componentUtility.updateSaltEquivs(productBatchModel, jComboBoxSaltCode, saltEquivalentsView);
			// Amount in well
			amountInWellModel.setValue(well.getContainedWeightAmount().getValue());
			amountInWellView.setValue(CeN11To12ConversionUtils.convertAmountModelToAmount(amountInWellModel));
			//jTextFieldAmountInWell.setText("" + well.getContainedAmount().getValue());
			// Get the precursors
			List monomerBatchKeys = productBatchModel.getReactantBatchKeys();
			ExperimentPageUtils pageUtils = new ExperimentPageUtils();
			StringBuffer buff = new StringBuffer();
			for (Iterator it = monomerBatchKeys.iterator(); it.hasNext();) {
				String monomerKey = (String) it.next();
				MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, notebookPageModel);
				if (monomerBatch != null && monomerBatch.getCompound() != null) {
					buff.append("  ").append(monomerBatch.getCompound().getRegNumber());
				}
			}
			this.jLabelPrecursorsValues.setText(buff.toString());
			// Hazards
			this.jTextAreaHealthHazards.setText(productBatchModel.getHazardComments() != null ? productBatchModel.getHazardComments() : "");
			jTextAreaHealthHazards.setEnabled(false);
			// Enable the components 
			this.enableComponents(true);
		}
		isLoading = false;
	}

	
	public void clear() {
		this.jLabelWellName.setText("");
		this.jLabelWellSequenceValue.setText("");
		jLabelNbkBatchNumNbkPageValue.setText("");
		jTextFieldNbkBatchNumSeqValue.setText("");
		jTextFieldNbkBatchNumSeqValue.setEditable(false);
		jTextFieldNbkBatchNumSeqValue.setEnabled(false);
		jLabelConvBatchNumValue.setText("");
		jLabelVirtualCompNumValue.setText("");
		jLabelCalcBatchMWValue.setText("");
		jLabelCalcBatchMFValue.setText("");
		this.jLabelTheoreticalMolesValue.setText("");
		this.jLabelTheoreticalAmountsWeight.setText("");
		this.componentUtility.updateStereoisomerComboBox(productBatchModel, jComboBoxStereoisomer);
		this.jComboBoxStatus.setSelectedIndex(0);
		this.jComboBoxStatus.setEnabled(false);
		this.jComboBoxSaltCode.setSelectedIndex(0);
		this.jComboBoxSaltCode.setEnabled(false);
		this.saltEquivalentsView.setValue(new AmountModel(UnitType.SCALAR));
		amountInWellView.setValue(new AmountModel(UnitType.SCALAR));
		this.jLabelPrecursorsValues.setText("");
		this.jTextAreaHealthHazards.setText("");
		jTextAreaHealthHazards.setEditable(false);
		jTextAreaHealthHazards.setEnabled(false);
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
				"pref, 7dlu, 63dlu, pref",
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
		int col3 = 4;
		
		CellConstraints cc = new CellConstraints();
		//builder.add(jLabelProductPlate,                 cc.xy (col1,   row));
		//builder.add(jLabelProductPlateName,             cc.xy (col2,   row)); 	  row++;
		builder.add(jLabelWell,                 		cc.xy (col1,   row));
		builder.add(jLabelWellName,             		cc.xy (col2,   row));     row++;
		//builder.add(jLabelWellSequence,             	cc.xy (col1,   row));
		//builder.add(jLabelWellSequenceValue,        	cc.xy (col2,   row));     row++;
		builder.add(jLabelNbkBatchNum,                  cc.xy (col1,   row)); 
		builder.add(jLabelNbkBatchNumNbkPageValue,      cc.xy (col2,   row));     
		builder.add(jTextFieldNbkBatchNumSeqValue,      cc.xy (col3,   row));     row++;
		builder.add(jLabelStatus,                       cc.xy  (col1,   row));
		builder.add(jComboBoxStatus,    				cc.xyw (col2,   row,  2));      row++;
		//builder.add(jLabelConvBatchNum,                 cc.xy  (col1,   row));
		//builder.add(jLabelConvBatchNumValue,            cc.xyw (col2,   row,   2));     row++;		
		builder.add(jLabelVirtualCompNum,               cc.xy  (col1,   row));
		builder.add(jLabelVirtualCompNumValue,          cc.xyw (col2,   row,   2));     row++;		
		builder.add(jLabelStereoisomer,                 cc.xy  (col1,   row));
		builder.add(jComboBoxStereoisomer,              cc.xyw (col2,   row,   2));     row++;
		builder.add(jLabelPrecursors,                   cc.xy  (col1,   row));
		builder.add(jLabelPrecursorsValues,             cc.xyw  (col2,   row, 2));          row++;
		builder.add(jLabelCalcBatchMF,                  cc.xy  (col1,   row));
		builder.add(jLabelCalcBatchMFValue,             cc.xyw  (col2,   row, 2));         row++;
		builder.add(jLabelCalcBatchMW,                  cc.xy  (col1,   row));
		builder.add(jLabelCalcBatchMWValue,             cc.xyw  (col2,   row, 2));         row++;
		
		builder.add(jLabelMMoles,                       cc.xy  (col1,   row));
		builder.add(this.jLabelTheoreticalMolesValue,   cc.xyw  (col2,   row, 2));         row++;
		
		builder.add(jLabelTheoreticalAmounts,           cc.xy  (col1,   row));
		builder.add(jLabelTheoreticalAmountsWeight,    	cc.xyw (col2,   row,  1));  	row++;
		//builder.add(jLabelTheoreticalAmountsMoles,    	cc.xyw (col3,   row,  1));  	
		builder.add(jLabelSaltCode,                     cc.xy  (col1,   row));          
		builder.add(jComboBoxSaltCode,                  cc.xyw  (col2,   row, 2));          row++;
		builder.add(jLabelSaltEquivalents,              cc.xy  (col1,   row));          
		builder.add(saltEquivalentsView,                cc.xyw  (col2,   row, 2));          row++;
		//builder.add(jLabelAmountInWell,                 cc.xy  (col1,   row));
		//builder.add(amountInWellView,           	    cc.xyw (col2,   row,  2));         row++;
		//builder.add(jTextFieldAmountInWell,     	    cc.xyw (col2,   row,  1));  
		//builder.add(jComboBoxAmountInWellUnits,         cc.xyw (col3,   row,  1));          row++; 
		builder.add(jLabelHealthHazards,                cc.xy  (col1,   row));
		builder.add(jTextAreaHealthHazards,             cc.xywh(col2,   row,  2, 3));   row++; row++; row++;


		
		return builder.getPanel();

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
	
	private JTextField makeTextField(String text, int size, String property) {
		JTextField textField = new JTextField(text, size);
		textField.setFont(EDIT_FONT_SMALL);
		//textField.getDocument().addDocumentListener(new BatchDocumentListener());
		//textField.addFocusListener(new PropertyFocusListener(property));
		return textField;
	}

	/**
     * This will go away when amount widget is developed.
     * @param items
     * @return
     */
	private JComboBox makeComboBox(String[] items) {
		JComboBox comboBox = new JComboBox();
		comboBox.setFont(COMBO_FONT);
		//JComboBoxModel model = new JComboBoxModel();
		ComboBoxModel model = new DefaultComboBoxModel(items);
		comboBox.setModel(model);
		return comboBox;
	}
	
	/**
	 * Create a text area object and set its listeners.
	 * @param text
	 * @param rows
	 * @param cols
	 * @param property
	 * @return
	 */
	private JTextArea makeTextArea(String text, int rows, int cols, String property) {
		JTextArea textArea = new JTextArea(text, rows, cols);
		textArea.setFont(EDIT_FONT_LARGE);
		textArea.setLineWrap(true);
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, Color.lightGray, null, Color.black));
		return textArea;
	}

	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	public JPanel getChimePanel() {
		ChemistryPanel chime = new ChemistryPanel();
		String stringSketch = this.productBatchModel.getCompound().getStringSketchAsString();
		stringSketch = Decoder.decodeString(stringSketch);
		chime.setMolfileData(stringSketch);
		return chime;
	}

	public void getStructuresfromCLS4MonomerPlate(ProductPlate monomerPlate) {
		try {
			List<String> strs = new ArrayList<String>();
			for (String compoundNo : monomerPlate.getBatchIDs())
				strs.add(new CompoundMgmtServiceDelegate().getStructureByCompoundNo(compoundNo).get(0));
			String structures[] = strs.toArray(new String[0]);
			PlateWell[] wells = productPlate.getWells();
			if (structures.length == wells.length) {
				for (int i = 0; i < wells.length; i++) {
					// //System.out.println(structures[i]);
					wells[i].getBatch().getCompound().setMolfile(structures[i]);
				}
			}
		} catch (Exception ex) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, "Error occurred while doing the CLS Lookup", ex);
		}
	}
	
	/**
	 * To do -- which fields are editable?
	 * @param enable
	 */
	private void enableComponents(boolean enable) {
		this.jComboBoxStatus.setEnabled(enable);
		this.saltEquivalentsView.setEnabled(enable);
		//this.jTextFieldCalcBatchMFValue.setEnabled(enable);
		//this.jTextFieldCalcBatchMWValue.setEnabled(enable);
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
	
	/**
	 * If the user changes the delivered weight, update the product plate well with 
	 * the new value.
	 * @param textField
	 */
	private void handleBatchPropertyEdit(JTextField textField) {
		String enteredAmount = textField.getText();
		double amount = 0.0;
		try {
			amount = Double.valueOf(enteredAmount).doubleValue();
		} catch (NumberFormatException nfe) {};
		AmountModel amountModel = new AmountModel(UnitType.MASS, amount);
		if (textField.getName().equals(CONTAINED_WEIGHT)) {
			System.out.println(textField.getName());
			String wellNumber = null;
			for (Iterator it = wellProperties.iterator(); it.hasNext();) {
				WellProperty prop = (WellProperty) it.next();
				if (prop.getName().equals(WELL)) {
					wellNumber = prop.getValue();
					break;
				}
			}
			if (wellNumber != null) {
				List wells =  productPlate.getPlateWellsforBatch(this.productBatchModel);
				if (wells.size() == 1) {
					PlateWell well = (PlateWell) wells.get(0);
					well.setContainedWeightAmount(amountModel);
					MasterController.getGUIComponent().enableSaveButtons();
					if (this.notebookPageModel != null)
						this.notebookPageModel.setModelChanged(true);
				}
			}
		}		
	}
	
	private void updateProperty(String property) {
		if (isLoading || !isEditable || this.productBatchModel == null)
			return;
		isLoading = true;
		if (property != null && property.length() > 0) {
			if (property.equals(AMOUNT_IN_WELL)) {
				double amount = amountInWellView.getValue();
				this.well.setContainedWeightAmount(new AmountModel(UnitType.MASS, amount));
				enableSaveButton();
			} else if (property.equals(STATUS)) {
				String selectedItem = (String) jComboBoxStatus.getSelectedItem();
				int selectivityStatus = ProductBatchStatusMapper.getInstance().getSelectivityStatus(selectedItem);
				if (selectivityStatus >= 0) 
					this.productBatchModel.setSelectivityStatus(selectivityStatus);
				int continueStatus = ProductBatchStatusMapper.getInstance().getContinueStatus(selectedItem);
				if (continueStatus >= 0)
					this.productBatchModel.setContinueStatus(continueStatus);
				enableSaveButton();
	    	//}  else if (property.equals(M_MOLES)) {
	    	//	BatchAttributeComponentUtility.setTheoreticalMoleAmount(this.productBatchModel, mMolesView.getAmount());
			//	enableSaveButton();
	    	} else if (property.equals(SALT_CODE_AND_NAME)) {
				// Manipulate the code from the dropdownlist and update the code in the batch object
				String selectedSaltCd = "";
				if (this.jComboBoxSaltCode.getSelectedItem() != null)
					selectedSaltCd = jComboBoxSaltCode.getSelectedItem().toString();
				int index2 = selectedSaltCd.indexOf(" -");
				if (index2 > 0) {
					try {
						String saltCode = selectedSaltCd.substring(0, index2);
						if (BatchAttributeComponentUtility.setSaltCode(productBatchModel, saltCode))
								productBatchModel.setModelChanged(true);
			    		////enableSaveButton();
			    		this.componentUtility.updateSaltEquivs(productBatchModel, jComboBoxSaltCode, saltEquivalentsView);
			    		this.populate();
						////String saltCode = selectedSaltCd.substring(0, index2);
						////SaltFormModel saltFormModel = new SaltFormModel(saltCode);
						//// What else should be set here?????
						////productBatchModel.setSaltForm(saltFormModel);
			    		enableSaveButton();
			    		////this.componentUtility.updateSaltEquivs(productBatchModel, jComboBoxSaltCode, saltEquivalentsView);
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(e);
					}
				}	
				enableSaveButton();
	    	} else if (property.equals(SALT_EQUIVALENT)) {
				try {
					double amount = this.saltEquivalentsView.getValue();
					if (BatchAttributeComponentUtility.setSaltEquiv(productBatchModel, new AmountModel(UnitType.SCALAR, amount))) {
						enableSaveButton();
						this.populate();
						//fireSaltEquiChanged(batch);
					}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
	    	}
		}
		isLoading = false;
	}
	
//	/**
//	 * @param pageModel the pageModel to set
//	 */
//	public void setPageModel(NotebookPageModel pageModel) {
//		this.pageModel = pageModel;
//	}
	
	private void enableSaveButton() {
		if (this.productBatchModel != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			this.productBatchModel.setModelChanged(true);
		}
	}
	
	/**
	 *  Enable the save button when a text field or text area is changed.
	 */
	class BatchDocumentListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
			isChanging = true;
		}
		public void insertUpdate(DocumentEvent e) {
			isChanging = true;
		}
		public void removeUpdate(DocumentEvent e) {
			isChanging = true;
		}
		
	}

//	private AmountComponent makeAmountComponent() {
//		AmountComponent amountComponent = new AmountComponent();
//		amountComponent.setEditable(true);
//		amountComponent.setValueSetTextColor(null);
//		amountComponent.addAmountEditListener(this);
//		amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());	
//		return amountComponent;
//	}
	

}
