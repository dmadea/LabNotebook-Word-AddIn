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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.AmountEditListener;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedListener;
import com.chemistry.enotebook.client.gui.page.batch.solvents.EditResidualSolventsDialog;
import com.chemistry.enotebook.client.gui.page.batch.solvents.EditSolubilityInSolventsDialog;
import com.chemistry.enotebook.client.gui.page.conception.ConceptionCompoundInfoContainer;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundCreateInterface;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNUtilObject;
import com.chemistry.enotebook.client.gui.page.experiment.table.StoicConstants;
import com.chemistry.enotebook.client.gui.page.reagents.ProgressBarDialog;
import com.chemistry.enotebook.client.gui.page.regis_submis.JDialogHealthHazInfo;
import com.chemistry.enotebook.client.gui.page.regis_submis.PCeNStructureVnVContainer;
import com.chemistry.enotebook.client.gui.page.regis_submis.SingletonRegSubHandler;
import com.chemistry.enotebook.client.gui.page.regis_submis.StructureVnvDialog;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.delegate.VnvDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.exceptions.VnvException;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.formatter.UtilsDispatcher;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.vcr.auxiliary.StatusInfo;
import com.chemistry.enotebook.vcr.auxiliary.VcrStatus;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BatchEditPanel extends javax.swing.JPanel implements BatchSelectionListener, AmountEditListener, PlateSelectionChangedListener {
	
	private static final long serialVersionUID = -1567903706617099434L;

	private static final Log log = LogFactory.getLog(BatchEditPanel.class);
	
	public static final Font BOLD_LABEL_FONT = new java.awt.Font("Arial", Font.BOLD, 12);
	public static final Font PLAIN_LABEL_FONT = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font EDIT_FONT_SMALL = new java.awt.Font("Arial", Font.PLAIN, 11);
	public static final Font EDIT_FONT_LARGE = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font COMBO_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final Font BUTTON_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	private static final int NUM_ROWS_PARALLEL = 39;
	private static final int NUM_ROWS_NONPARALLEL = 15;
	//private static final int BATCH_WT_FIXED_FIGS = 6;
	
	private ProductBatchModel productBatchModel;  
	
	// vb 12/6
	ChemistryPanel chime = new ChemistryPanel();
	
	private VnvDelegate vnvDelegate = null;
	// vb
	//private HashMap productBatchAttributes = new HashMap();
	//private int currentWellPosition = 0;
	private PlateWell<ProductBatchModel> well = null;
	private NotebookPageModel pageModel = null;
	private BatchAttributeComponentUtility componentUtility = null;
	
	// Attribute name constants
	private static final String PRODUCT_PLATE = "Product Plate Barcode";
	private static final String WELL = "Well";
	private static final String NOTEBOOK_BATCH_NUM_EDITABLE = "Notebook Batch # Editable";
	private static final String STATUS = "Status";
	private static final String CONVERSATIONAL_BATCH_NUM = "Conversational Batch #";
	private static final String VIRTUAL_COMPOUND_NUM = "Virtual Compound Id:";
	private static final String STEREOISOMER = "Stereoisomer Code";
	private static final String STRUCTURE_COMMENTS = "Structure Comments";

	public static final String COMPOUND_SOURCE = "<html><font color=\"red\">* </font><font size='3'>Source:</font></html>";
	public static final String SOURCE_DETAIL = "<html><font color=\"red\">* </font><font size='3'>Source Detail:</font></html>";

	private static final String BATCH_OWNER = "Batch Owner";
	private static final String SYNTHESIZED_BY = "Synthesized By";
	private static final String CALCULATED= "Calculated ";//
	private static final String MW = " MW";
	private static final String MF = " MF";
	private static final String THERAPEUTIC_AREA = "Therapeutic Area";
	private static final String COMPOUND_STATE = "Compound State";
	private static final String PROJECT = "Project";
	private static final String TOTAL_WEIGHT = PCeNTableView.TOTAL_WEIGHT;
	private static final String TOTAL_VOLUME = PCeNTableView.TOTAL_VOLUME;
	private static final String AMOUNT_IN_WELL = "Amount in Well/Tube";
	private static final String THEORETICAL_AMOUNTS = "Theoretical Amounts";
	public static final String TUBE_BARCODE = "Tube Barcode (if any)";
	private static final String VIAL_BARCODE = "Vial/Tube Barcode";
	private static final String EXTERNAL_SUPPLIER = "External Supplier";
	private static final String PURITY = "Purity";
	private static final String RESIDUAL_SOLVENTS = "Residual Solvents";
	private static final String SOLUBILITY_IN_SOLVENTS = "Solubility in Solvents";
	public static final String WELL_SOLVENT = "Well Solvent (if any)";
	private static final String COMPOUND_PROTECTION = "Compound Protection";
	private static final String HEALTH_HAZARDS = "Health Hazards";
	private static final String HANDLING_PRECAUTIONS = "Handling Precautions";
	private static final String STORAGE_INSTRUCTIONS = "Storage Instructions";
	private static final String SALT_CODE_AND_NAME = "Salt Code & Name";
	private static final String SALT_EQUIVALENT = "Salt Equivalent";
	private static final String BATCH_COMMENTS = "Batch Comments";
	private static final String PRECURSOR_REACTANT_IDS = "Precursor/Reactant IDs";
	private static final String CONCEPTION = "CONCEPTION";
	private static final String COMPOUND = "Compound";
	private static final String BATCH = "Batch";
	private static final String AMOUNT_IN_WELL_TUBE_WEIGHT = "Amount in Well/Tube Weight";
	private static final String AMOUNT_IN_WELL_TUBE_VOLUME = "Amount in Well/Tube Volume";
	private static final String AMOUNT_IN_WELL_TUBE_MOLES = "Amount in Well/Tube Moles";
	private static final String AMOUNT_IN_VIAL_WEIGHT = "Amount in Vial Weight";
	private static final String PRODUCT = "Product";
	private static final String MELTING_POINT = "Melting Point";
	private static final String TOTAL_MOLES = "Total Moles";
	private static final String MOLES = "Moles";
	private String BATCH_OR_COMPOUND = "";

    public static final String INTERMEDIATE_BUTTON_LABEL = "Intermediate";
    public static final String TEST_COMPOUND_BUTTON_LABEL = "Test Compound";

	// Flags
	private boolean isBatchEditable = false;
	private boolean isLoading = false;
	private boolean isChanging = false;
	
	// GUI components
	private JLabel jLabelEditBatchHeader = new JLabel();
	
	// Plate and well 
	private JLabel jLabelProductPlate;
	private JLabel jLabelProductPlateName;
	private JLabel jLabelWell;
	private JLabel jLabelWellName;

	// Notebook batch number
	private JLabel jLabelNbkBatchNum;
	private JLabel jLabelNbkBatchNumNbkPageValue;
	private JTextField jTextFieldNbkBatchNumSeqValue;
	//Product
	JCheckBox jCheckBoxProduct;
	// Status
	private JLabel jLabelStatus;
	private CeNComboBox jComboBoxStatus;
	// Conversational batch number
	// Note:  this is a text field so it can be copied but it is never enabled.
	private JLabel jLabelConvBatchNum;
	public JTextField jTextFieldConvBatchNumValue;
	// Virtual compound number
	private JLabel jLabelVirtualCompNum;
	private JTextField jTextFieldVirtualCompNumValue;
	// Stereoisomer
	private JLabel jLabelStereoisomer;
	public CeNComboBox jComboBoxStereoisomer;
	// VnV button
	private JButton jButtonVnV;
	private JButton jButtonVCReg;
	//Structure comments
	private JLabel jLabelStructureComments;
	private JTextArea jTextAreaStructureCommentsValue;
	private JScrollPane jScrollPaneStructureComments = null;
	// Compound source
	private JLabel jLabelCompoundSource;
	private CeNComboBox jComboBoxCompoundSource;
	// Source detail
	private JLabel jLabelSourceDetail;
	private CeNComboBox jComboBoxSourceDetail;
	// Batch owner
	private JLabel jLabelBatchOwnerValue;
	// Synthesized by
	private JLabel jLabelSynthesizedByValue;
	// Calculated batch MW
	private JLabel jLabelCalcBatchMW;
	private JLabel jLabelCalcBatchMWValue;
	// Calculated batch MF
	private JLabel jLabelCalcBatchMF;
	private JLabel jLabelCalcBatchMFValue;
	// Therapeutic area
	private JLabel jLabelTherapeuticAreaValue;
	// Project
	private JLabel jLabelProjectValue;
	// Compound state
	private JLabel jLabelCompoundState;
	private CeNComboBox jComboBoxCompoundState;
	// Site
	//private JLabel jLabelSite;
	//private JLabel jLabelSiteValue;
	// Salt code
	private JLabel jLabelSaltCode;
	private CeNComboBox jComboBoxSaltCode;
	// Salt equivalent
	private JLabel jLabelSaltEquivalents;
	private PAmountComponent saltEquivalentsView;
	//private AmountModel saltEquivalentsModel;
	// Total amount made
	private PAmountComponent totalAmountMadeWtView;
	private PAmountComponent totalAmountMadeVolView; 
	private PAmountComponent totalAmountMadeMolesView;
	// Amount in well
	private JLabel jLabelAmountInWell;
	private PAmountComponent amountInWellWtView;
	private PAmountComponent amountInWellVolView;
	private PAmountComponent amountInWellMolesView;
	// Amount in vial
	private PAmountComponent amountInVialWtView;
	// Batch comments
	private JLabel jLabelBatchComments;
	private JTextArea jTextAreaBatchComments;
	private JScrollPane jScrollPaneBatchComments;
	////private JPanel jPanelBatchComments;
	// Theoretical amounts
	private JLabel jLabelTheoreticalAmounts;
	private JLabel jLabelTheoreticalAmountsWeight;
	private JLabel jLabelTheoreticalAmountsMoles;
	private JLabel jLabelTheoreticalAmountsWeightValue;
	private JLabel jLabelTheoreticalAmountsMolesValue;
	private JLabel jLabelPercentYieldValue;
	// Amount remaining
	private PAmountComponent amountRemainingWtView;
	private PAmountComponent amountRemainingVolView;
	// Tube barcode
	private JLabel jLabelTubeBarcode;
	private JTextField jTextFieldTubeBarcode;
	// Vial barcode for Singleton Exp
	private JTextField jTextFieldVialBarcode;
	// Precursor/Reactant ids
	private JLabel jLabelPrecursors;
	private JTextField jTextFieldPrecursorsValues;
	// External supplier
	private JLabel jLabelExternalSupplier;
	private JTextArea jTextAreaExternalSupplier;
	private JScrollPane jScrollPaneExternalSupplier;
	private JPanel jPanelExternalSupplier;
	private JButton jButtonExternalSupplierEdit;
	// Purity
	private JLabel jLabelPurity;
	private JTextArea jTextAreaPurity;
	private JScrollPane jScrollPanePurity;
	private JPanel jPanelPurity;
	private JButton jButtonPurityEdit;
	// Residual solvents
	private JLabel jLabelResidualSolvents;
	private JTextField jTextFieldResidualSolvents;
	private JButton jButtonResidualSolventsEdit;
	// Solubility in solvents
	private JLabel jLabelSolubilityInSolvents;
	private JTextField jTextFieldSolubilityInSolvents;
	//private JTextField jTextFieldTubeVialBarcode;
	private JButton jButtonSolubilityInSolventsEdit;
	// Well solvent
	private JLabel jLabelWellSolvent;
	private CeNComboBox jComboBoxWellSolvent;
	// Compound protection
	private JLabel jLabelCompoundProtection;
	private CeNComboBox jComboBoxCompoundProtection;
	// Health hazards
	private JLabel jLabelHealthHazards;
	private JTextArea jTextAreaHealthHazards;
	private JScrollPane jScrollPaneHealthHazards;
	private JButton jButtonHealthHazardsEdit;
	// Handling precautions
	private JLabel jLabelHandlingPrecautions;
	private JTextArea jTextAreaHandlingPrecautions;
	private JScrollPane jScrollPaneHandlingPrecautions;
	// Storage instructions
	private JLabel jLabelStorageInstructions;
	private JTextArea jTextAreaStorageInstructions;
	private JScrollPane jScrollPaneStorageInstructions;
	
	private ProgressBarDialog progressBarDialog = null;
	private CompoundCreationHandler compoundCreationHandler = null;
	//private HashMap batchesAndPlatesMap;
	private ProductPlate productPlate;
	private boolean isPseudoProductPlate = false;
	//Melting Point
	private JLabel jLabelMeltingPoint;
	private JTextArea jTextAreaMeltingPnt;
	private JButton jButtonMeltingPntEdit;
	private JScrollPane jScrollMeltingPnt;
	
	private Object lastEventObject; //To refresh panel when Tab selection changes.
	
	private NotebookPageGuiInterface parentWindow;
	private TubeVialContainerTableView jTableTubeVialContainers;
	private TubeVialContainerTableConnector tubeVialTableConnector ;
	private TubeVialContainerTablePopupMenuManager tubeVialTablePopupManager;
	private CompoundCreateInterface compoundCreateInterface = null;
    private JRadioButton intermediateButton;
    private JRadioButton testCompoundButton;
    private JRadioButton invisibleButton;

    private RegistrationServiceDelegate adapter;

/*	private Tube tube;
	private Vial vial;
*/	
	//Singleton and Conceptual Pages, Batches add and Update informations are stored.
	//HashMap batchContainerMap = new HashMap();// Stores the batch and the container.
		
	/**
	 * Create a new BatchEditPanel -- This should
	 * only happen once when an experiment loads.
	 */
//	public BatchEditPanel(boolean isParallel) 
//	{
//		this.isParallel = isParallel;
//		this.componentUtility = BatchAttributeComponentUtility.getInstance();
//		this.initGui();
//		this.populate();
//		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
//		CellConstraints cc = new CellConstraints();
//		this.add(this.getPanel(), cc.xy(2, 1));
//	}

/*	public BatchEditPanel(boolean isParallel,CompoundCreationHandler mCompoundCreationHandler) 
    {
		this.isParallel = isParallel;
		this.componentUtility =BatchAttributeComponentUtility.getInstance();
		this.initGui();
		this.populate();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.compoundCreationHandler = mCompoundCreationHandler;
	}*/
	
    private BatchEditPanel(NotebookPageModel pageModelPojo) {
		this.pageModel = pageModelPojo;
		this.componentUtility = BatchAttributeComponentUtility.getInstance();
		this.initGui();
		this.populate();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		//batchesAndPlatesMap = pageModel.getAllProductBatchesAndPlatesMap(false);
	}

    private BatchEditPanel(NotebookPageModel pageModelPojo, CompoundCreationHandler compoundCreationHandler) {
		this(pageModelPojo);
		this.compoundCreationHandler = compoundCreationHandler;
	}

	public BatchEditPanel(NotebookPageModel pageModelPojo,CompoundCreationHandler mcompoundCreationHandler,NotebookPageGuiInterface mparentWindow) 
	{
		this(pageModelPojo ,mcompoundCreationHandler);
		this.parentWindow = mparentWindow;
	}
	
	/**
	 * Batch selection has changed.  Recreate the panel and refresh the parent's panel.
	 * @param event contains the PlateWell object
	 */
	public void batchSelectionChanged(final BatchSelectionEvent event) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				internalBatchSelectionChanged(event);
			}
		});
	}
	
	private void internalBatchSelectionChanged(BatchSelectionEvent event) 
	{		
		if (event != null) {
			lastEventObject = event.getSubObject();
		}

		if (lastEventObject == null) {
			productBatchModel = null;
		}

		if (lastEventObject instanceof CompoundContainer) {
			this.well = (PlateWell<ProductBatchModel>) lastEventObject;
			this.productBatchModel = (ProductBatchModel) well.getBatch();
			productPlate = (ProductPlate) well.getPlate();
			if (productPlate instanceof PseudoProductPlate)
				isPseudoProductPlate = true;
			else
				isPseudoProductPlate = false;
		} else if (lastEventObject instanceof ProductBatchModel) {
			this.productBatchModel = (ProductBatchModel) lastEventObject;
			if (pageModel.isParallelExperiment()) {
				this.productPlate = pageModel.getAllProductBatchesAndPlatesMap(false).get(productBatchModel);
				if (productPlate instanceof PseudoProductPlate)
					isPseudoProductPlate = true;
				else
					isPseudoProductPlate = false;
			}
			// The batchesAndPlatesMap is reactionstep specific.
			// But, in the Products view single BatchEditPanel caters the need
			// to display all products stepwise unlike registrationBatchDetailsPanel.java
			// As, it is not required for Beta, this functionality is put on hold.
			// To do.. Consolidate all Batches and it's plates into a single map
			// and use that map to get the well, tube and vial information.
			well = null;
			if (productPlate != null) {
				List<PlateWell<ProductBatchModel>> wellsList = productPlate.getPlateWellsforBatch(productBatchModel);
				if (wellsList.size() > 0)
					well = wellsList.get(0); // For this release assume only one
															// well exists for each Batch.
			}
			
			//Update the ContainerTable with new Prod batch
			//if(tubeVialTableConnector !=null) {
			//	tubeVialTableConnector.setProductBatchModel(this.productBatchModel);
			//}
		} else if (lastEventObject instanceof ParallelCeNUtilObject) {
			ParallelCeNUtilObject utilObj = (ParallelCeNUtilObject) lastEventObject;
			this.productBatchModel = (ProductBatchModel) utilObj.getBatch();
		}

		this.populate();
		this.removeAll();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Initialize the panel widgets.
	 *
	 */
	private void initGui() 
	{
		if (pageModel != null && pageModel.getPageType().equals(CONCEPTION))
			BATCH_OR_COMPOUND = COMPOUND;
		else
			BATCH_OR_COMPOUND = BATCH;
		jLabelEditBatchHeader = new JLabel("Enter " + BATCH_OR_COMPOUND + " Data & Edit " + BATCH_OR_COMPOUND + " Structure(s):");
		jLabelEditBatchHeader.setFont(BOLD_LABEL_FONT);
		
		if (pageModel != null && pageModel.isParallelExperiment()) {
			jLabelProductPlate = makeCeNLabel(PRODUCT_PLATE, PLAIN_LABEL_FONT);
			jLabelProductPlateName = makeLabel("", PLAIN_LABEL_FONT);

			jLabelWell = makeCeNLabel(WELL, PLAIN_LABEL_FONT);
			jLabelWellName = makeLabel("", PLAIN_LABEL_FONT);
		
			// Tube barcode
			jLabelTubeBarcode = makeCeNLabel(TUBE_BARCODE, PLAIN_LABEL_FONT);
			
			// Vial barcode
//			jLabelVialBarcode = makeCeNLabel(VIAL_BARCODE, PLAIN_LABEL_FONT);
		}
		if (pageModel != null && pageModel.isConceptionExperiment()) {
			jLabelNbkBatchNum = makeCeNLabel("Notebook ID #:", PLAIN_LABEL_FONT);
		} else {
			jLabelNbkBatchNum = makeCeNLabel("Notebook Batch #:", PLAIN_LABEL_FONT);
		}
		jLabelNbkBatchNumNbkPageValue = makeLabel("", PLAIN_LABEL_FONT);
		
		jTextFieldNbkBatchNumSeqValue = makeTextField("", 6, NOTEBOOK_BATCH_NUM_EDITABLE);
		jTextFieldNbkBatchNumSeqValue.setDocument(new JTextFieldFilter(JTextFieldFilter.ALPHA_NUMERIC, 6));
		jTextFieldNbkBatchNumSeqValue.getDocument().addDocumentListener(new BatchDocumentListener());
		jTextFieldNbkBatchNumSeqValue.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				//updateProperty(BatchEditPanel.NOTEBOOK_BATCH_NUM_EDITABLE);
			}
		});
		jTextFieldNbkBatchNumSeqValue.addFocusListener(new FocusListener() {
		    public void focusGained(FocusEvent e){}
		    public void focusLost(FocusEvent e) {
				updateProperty(BatchEditPanel.NOTEBOOK_BATCH_NUM_EDITABLE);		    	
		    }
		});
		
		jCheckBoxProduct = makeCeNCheckBox(PRODUCT);
		
		jLabelStatus = makeCeNLabel(STATUS, PLAIN_LABEL_FONT);
		jComboBoxStatus = new CeNComboBox();
		//jComboBoxStatus.setPopupWidth(175);
		jComboBoxStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(BatchEditPanel.STATUS);
			}
		});	
		
		jLabelConvBatchNum = makeCeNLabel(CONVERSATIONAL_BATCH_NUM, PLAIN_LABEL_FONT);
		jTextFieldConvBatchNumValue = makeTextField("", 15, CONVERSATIONAL_BATCH_NUM);
		
		jLabelVirtualCompNum = makeCeNLabel(VIRTUAL_COMPOUND_NUM, PLAIN_LABEL_FONT);
		jTextFieldVirtualCompNumValue = makeTextField("", 0, VIRTUAL_COMPOUND_NUM);
				
		jButtonVCReg = new JButton("VC Reg"); 
		jButtonVCReg.setEnabled(false);
		jButtonVCReg.setMargin(new Insets(2, 2, 2, 2));
		jButtonVCReg.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					handleVCReg();
				}
			}
		);
		
		jLabelStructureComments = makeCeNLabel(STRUCTURE_COMMENTS, PLAIN_LABEL_FONT);
		jTextAreaStructureCommentsValue = makeTextArea(STRUCTURE_COMMENTS, true);
		jTextAreaStructureCommentsValue.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				updateProperty(BatchEditPanel.STRUCTURE_COMMENTS);
			}
		});
		jScrollPaneStructureComments = makeScrollPane(jTextAreaStructureCommentsValue);
		//jTextAreaStructureCommentsValue.setLineWrap(true);
		
		jLabelStereoisomer = makeCeNLabel(STEREOISOMER, PLAIN_LABEL_FONT);
		jComboBoxStereoisomer = new CeNComboBox(); 
		jComboBoxStereoisomer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading) {
					updateProperty(BatchEditPanel.STEREOISOMER);
					enableSaveButton();
				}
			}
		});
		
		jButtonVnV = new JButton("VnV");
		jButtonVnV.setToolTipText("Validate and Verify then perform Uniqueness Check");
		jButtonVnV.setEnabled(false);
		jButtonVnV.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					//handleVnv(); // This logic is retired and 1.1 VnV panel is introduced
					displayVnVContainer();
				}
			}
		);
		
		// Compound source and source detail
		jLabelCompoundSource = makeCeNLabel(COMPOUND_SOURCE, PLAIN_LABEL_FONT);
		jLabelSourceDetail = makeCeNLabel(SOURCE_DETAIL, PLAIN_LABEL_FONT);
		jComboBoxCompoundSource = new CeNComboBox();
		jComboBoxCompoundSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(BatchEditPanel.COMPOUND_SOURCE);				
			}
		});
		jComboBoxSourceDetail = new CeNComboBox();
		jComboBoxSourceDetail.setEditable(false);
		this.insertBlankItem(jComboBoxSourceDetail);
		jComboBoxSourceDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(BatchEditPanel.SOURCE_DETAIL);				
			}
		});
		
		// Batch owner and synthesized by
//		jLabelBatchOwner = makeCeNLabel(BATCH_OWNER, PLAIN_LABEL_FONT);
		jLabelBatchOwnerValue = makeLabel("", PLAIN_LABEL_FONT);
//		jLabelSynthesizedBy = makeCeNLabel(SYNTHESIZED_BY, PLAIN_LABEL_FONT);
		jLabelSynthesizedByValue = makeLabel("", PLAIN_LABEL_FONT);
		
		// Calculated batch MW and MF		
		jLabelCalcBatchMW = makeCeNLabel(CALCULATED + BATCH_OR_COMPOUND + MW, PLAIN_LABEL_FONT);
		jLabelCalcBatchMWValue = makeLabel("", PLAIN_LABEL_FONT);
//		jLabelTherapeuticArea = makeCeNLabel(THERAPEUTIC_AREA, PLAIN_LABEL_FONT);
		jLabelTherapeuticAreaValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelCalcBatchMF = makeCeNLabel(CALCULATED + BATCH_OR_COMPOUND + MF, PLAIN_LABEL_FONT);
		jLabelCalcBatchMFValue = makeLabel("", PLAIN_LABEL_FONT);
		
		// Compound state
		jLabelCompoundState = makeCeNLabel(COMPOUND_STATE, PLAIN_LABEL_FONT);
		jComboBoxCompoundState = new CeNComboBox();
		jComboBoxCompoundState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading)
					updateProperty(BatchEditPanel.COMPOUND_STATE);
			}
		});		
		// Project
//		jLabelProject = makeCeNLabel(PROJECT, PLAIN_LABEL_FONT);
		jLabelProjectValue = makeLabel("", PLAIN_LABEL_FONT);
		
		// Salt code and equivalents
		jLabelSaltCode = makeCeNLabel(SALT_CODE_AND_NAME, PLAIN_LABEL_FONT);
		jComboBoxSaltCode = new CeNComboBox();
		jComboBoxSaltCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading) 
					updateProperty(BatchEditPanel.SALT_CODE_AND_NAME);
			}
		});
		jLabelSaltEquivalents = makeCeNLabel(SALT_EQUIVALENT, PLAIN_LABEL_FONT);
		//saltEquivalentsModel = new AmountModel(UnitType.SCALAR);
		saltEquivalentsView = this.makeAmountComponent(new AmountModel(UnitType.SCALAR));
		/*
		//workaround
		saltEquivalentsView.getComponent(0).addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				updateProperty(BatchEditPanel.SALT_EQUIVALENT);
			}
		});
		*/

		
		// Total amounts
		totalAmountMadeWtView = this.makeAmountComponent(new AmountModel(UnitType.MASS));
		totalAmountMadeVolView = this.makeAmountComponent(new AmountModel(UnitType.VOLUME));
		totalAmountMadeMolesView = this.makeAmountComponent(new AmountModel(UnitType.MOLES));
		totalAmountMadeMolesView.setEditable(false);
		
		// Remaining amounts
		this.amountRemainingWtView = this.makeAmountComponent(new AmountModel(UnitType.MASS));
		this.amountRemainingWtView.setEditable(false);
		this.amountRemainingVolView = this.makeAmountComponent(new AmountModel(UnitType.VOLUME));
		this.amountRemainingVolView.setEditable(false);
		
		// Amount in well
		amountInWellWtView = this.makeAmountComponent(new AmountModel(UnitType.MASS));
		amountInWellWtView.addFocusListener(new AmountFocusListener(AMOUNT_IN_WELL_TUBE_WEIGHT));
		amountInWellVolView = this.makeAmountComponent(new AmountModel(UnitType.VOLUME));
		amountInWellVolView.addFocusListener(new AmountFocusListener(AMOUNT_IN_WELL_TUBE_VOLUME));
		amountInWellMolesView = this.makeAmountComponent(new AmountModel(UnitType.MOLES));
		amountInWellMolesView.setEditable(false);
		amountInWellMolesView.addFocusListener(new AmountFocusListener(AMOUNT_IN_WELL_TUBE_MOLES));
		
		// Amount in vial
		amountInVialWtView = this.makeAmountComponent(new AmountModel(UnitType.MASS));
				
//		jLabelTotWeight = makeCeNLabel(TOTAL_WEIGHT, PLAIN_LABEL_FONT);
//		jLabelTotVolume = makeCeNLabel(TOTAL_VOLUME, PLAIN_LABEL_FONT);
	
		
		// Batch comments
		if (pageModel != null && pageModel.isConceptionExperiment()) {
			jLabelBatchComments = makeCeNLabel("Comments", PLAIN_LABEL_FONT);
		} else {
			jLabelBatchComments = makeCeNLabel(BATCH_COMMENTS, PLAIN_LABEL_FONT);
		}
		jTextAreaBatchComments = makeTextArea(BATCH_COMMENTS, true);
		jTextAreaBatchComments.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				updateProperty(BatchEditPanel.BATCH_COMMENTS);
			}
		});
		
		jScrollPaneBatchComments = this.makeScrollPane(jTextAreaBatchComments);
		
		// Theoretical amounts
		jLabelTheoreticalAmounts = makeCeNLabel(THEORETICAL_AMOUNTS, PLAIN_LABEL_FONT);
		jLabelTheoreticalAmountsWeight = makeCeNLabel("Weight", PLAIN_LABEL_FONT);
		jLabelTheoreticalAmountsMoles = makeCeNLabel("mMoles", PLAIN_LABEL_FONT);
		jLabelTheoreticalAmountsWeightValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelTheoreticalAmountsMolesValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelPercentYieldValue = makeLabel("", PLAIN_LABEL_FONT);
		
		// Amount in well
		jLabelAmountInWell = makeCeNLabel(AMOUNT_IN_WELL, PLAIN_LABEL_FONT);
		//amountInWellView = this.makeAmountComponent();
		
		// Precursor/Reactant ids
		jLabelPrecursors = makeCeNLabel(PRECURSOR_REACTANT_IDS, PLAIN_LABEL_FONT);
		jTextFieldPrecursorsValues = makeTextField("", 0, PRECURSOR_REACTANT_IDS);
		
		// External supplier
		jLabelExternalSupplier = makeCeNLabel(EXTERNAL_SUPPLIER, PLAIN_LABEL_FONT);
		jTextAreaExternalSupplier = makeTextArea(EXTERNAL_SUPPLIER, true);
		jScrollPaneExternalSupplier = new JScrollPane(jTextAreaExternalSupplier);
		jScrollPaneExternalSupplier.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
		jPanelExternalSupplier = new JPanel();
		jPanelExternalSupplier.setLayout(null);
		jPanelExternalSupplier.setPreferredSize(new java.awt.Dimension(300, 414));
		jPanelExternalSupplier.add(jScrollPaneExternalSupplier);
		jButtonExternalSupplierEdit = new JButton("Edit");
		jButtonExternalSupplierEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showExternalSupplierDialog(e);
				enableSaveButton();
			}			
		});
		
		// Purity
		jLabelPurity = makeCeNLabel(PURITY, PLAIN_LABEL_FONT);
		jTextAreaPurity = makeTextArea(PURITY, true);
		jScrollPanePurity = new JScrollPane(jTextAreaPurity);
		jScrollPanePurity.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
		jPanelPurity = new JPanel();
		jPanelPurity.setLayout(null);
		jPanelPurity.setPreferredSize(new java.awt.Dimension(300, 414));
		jPanelPurity.add(jScrollPanePurity);		
		jButtonPurityEdit = new JButton("Edit");
		jButtonPurityEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPurityInfoDialog(e);
			}
		});

        intermediateButton = makeCeNRadioButton(INTERMEDIATE_BUTTON_LABEL);
        testCompoundButton = makeCeNRadioButton(TEST_COMPOUND_BUTTON_LABEL);
        invisibleButton = new JRadioButton();
		
		//Melting Point
		jLabelMeltingPoint = makeCeNLabel(MELTING_POINT, PLAIN_LABEL_FONT);
		jTextAreaMeltingPnt = makeTextArea(MELTING_POINT, true);
		jScrollMeltingPnt = new JScrollPane(jTextAreaMeltingPnt);
		jScrollMeltingPnt.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
		JPanel jPanelMeltingPnt = new JPanel();
		jPanelMeltingPnt.setLayout(null);
		jPanelMeltingPnt.setPreferredSize(new java.awt.Dimension(300, 414));
		jPanelMeltingPnt.add(jScrollMeltingPnt);		
		jButtonMeltingPntEdit = new JButton("Edit");
		jButtonMeltingPntEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMeltingPntDialog(e);
			}
		});
		
		// Residual solvents
		jLabelResidualSolvents = makeCeNLabel(RESIDUAL_SOLVENTS, PLAIN_LABEL_FONT);
		jTextFieldResidualSolvents = makeTextField("", 25, RESIDUAL_SOLVENTS);
		jTextFieldResidualSolvents.setEditable(false);
		jButtonResidualSolventsEdit = new JButton("Edit");
		jButtonResidualSolventsEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showResidualSolventsInfoDialog(e);
			}
		});
		// Solubility in solvents
		jLabelSolubilityInSolvents = makeCeNLabel(SOLUBILITY_IN_SOLVENTS, PLAIN_LABEL_FONT);
		jTextFieldSolubilityInSolvents = makeTextField("", 25, SOLUBILITY_IN_SOLVENTS);
		jTextFieldSolubilityInSolvents.setEditable(false);
		jButtonSolubilityInSolventsEdit = new JButton("Edit");
		jButtonSolubilityInSolventsEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSolubilityInSolventsInfoDialog(e);
			}
		});
		//well solvent
		jLabelWellSolvent = makeCeNLabel(WELL_SOLVENT, PLAIN_LABEL_FONT);
		jComboBoxWellSolvent = new CeNComboBox();
		jComboBoxWellSolvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading) 
					updateProperty(BatchEditPanel.WELL_SOLVENT);
			}			
		});		
		// Compound protection
		jLabelCompoundProtection = makeCeNLabel(COMPOUND_PROTECTION, PLAIN_LABEL_FONT);
		jComboBoxCompoundProtection = new CeNComboBox();
		jComboBoxCompoundProtection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isLoading) 
					updateProperty(BatchEditPanel.COMPOUND_PROTECTION);
			}			
		});
		// Health hazards
		jLabelHealthHazards = makeCeNLabel(HEALTH_HAZARDS, PLAIN_LABEL_FONT);
		jTextAreaHealthHazards = makeTextArea(HEALTH_HAZARDS, true);
		jScrollPaneHealthHazards = this.makeScrollPane(jTextAreaHealthHazards);
		jButtonHealthHazardsEdit = new JButton("Edit");
		jButtonHealthHazardsEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHealthHazardsInfoDialog(e);
			}
		});		
		// Handling precautions
		jLabelHandlingPrecautions = makeCeNLabel(HANDLING_PRECAUTIONS, PLAIN_LABEL_FONT);
		jTextAreaHandlingPrecautions = this.makeTextArea(HANDLING_PRECAUTIONS, true);
		//new JTextArea(); //makeTextArea("", 3, 50, HANDLING_PRECAUTIONS, true);
		jScrollPaneHandlingPrecautions = this.makeScrollPane(jTextAreaHandlingPrecautions);		
		// Storage instructions
		jLabelStorageInstructions = makeCeNLabel(STORAGE_INSTRUCTIONS, PLAIN_LABEL_FONT);
		jTextAreaStorageInstructions = makeTextArea(STORAGE_INSTRUCTIONS, true);
		jScrollPaneStorageInstructions = this.makeScrollPane(jTextAreaStorageInstructions);
		
		// Populate code table dropdowns -- do these ever need to be updated?
		//Tube Barcode
		jTextFieldTubeBarcode = makeTextField("", 12, TUBE_BARCODE);
		jTextFieldTubeBarcode.setDocument(new JTextFieldLimit(12));		
		//Vial Barcode
		jTextFieldVialBarcode = makeTextField("", 12, VIAL_BARCODE);
		jTextFieldVialBarcode.setDocument(new JTextFieldLimit(12));
//		this.componentUtility.fillCompoundSourceAndDetailComboBoxes(this.productBatchModel, 
//																  this.jComboBoxCompoundSource, 
//																  this.jComboBoxSourceDetail,
//																  true, true);
		////this.componentUtility.fillExternalSupplierComboBox(this.jComboBoxExternalSupplier);
		////this.insertBlankItem(jComboBoxExternalSupplier);
		CodeTableUtils.fillComboBoxWithSaltCodes(jComboBoxSaltCode);
		CodeTableUtils.fillComboBoxWithCompoundStates(this.jComboBoxCompoundState);
		this.insertBlankItem(jComboBoxCompoundState);
	    ProductBatchStatusMapper.getInstance().fillComboBox(this.jComboBoxStatus);
	}
	
	protected void showMeltingPntDialog(ActionEvent e) 
	{
		JLabel jLabelC = new JLabel();
		jLabelC.setFont(PLAIN_LABEL_FONT);
		MeltingPointJDialog.showGUI(MasterController.getGUIComponent(), this.productBatchModel, jTextAreaMeltingPnt, jLabelC, pageModel);
	}

	protected void refreshToolTips() 
	{
		if (jComboBoxCompoundSource.getSelectedItem() != null)
			jComboBoxCompoundSource.setToolTipText(jComboBoxCompoundSource.getSelectedItem().toString());
		else
			jComboBoxCompoundSource.setToolTipText("");
		
		if (jComboBoxSourceDetail.getSelectedItem() != null)
			jComboBoxSourceDetail.setToolTipText(jComboBoxSourceDetail.getSelectedItem().toString());
		else
			jComboBoxSourceDetail.setToolTipText("");
		
		if (jComboBoxCompoundState.getSelectedItem() != null)
			jComboBoxCompoundState.setToolTipText(jComboBoxCompoundState.getSelectedItem().toString());
		else
			jComboBoxCompoundState.setToolTipText("");

		if (jComboBoxWellSolvent.getSelectedItem() != null)
			jComboBoxWellSolvent.setToolTipText(jComboBoxWellSolvent.getSelectedItem().toString());
		else
			jComboBoxWellSolvent.setToolTipText("");

        final Object stereoIsomerCode = jComboBoxStereoisomer.getSelectedItem();
        if (stereoIsomerCode != null) {
            jComboBoxStereoisomer.setToolTipText(stereoIsomerCode.toString());
        }
        if(jComboBoxSaltCode.getSelectedItem() != null)
        	jComboBoxSaltCode.setToolTipText(jComboBoxSaltCode.getSelectedItem().toString());
        else
        	jComboBoxSaltCode.setToolTipText("");
		jLabelCalcBatchMFValue.setToolTipText(jLabelCalcBatchMFValue.getText());
		jTextFieldPrecursorsValues.setToolTipText(jTextFieldPrecursorsValues.getText());
		jTextFieldVirtualCompNumValue.setToolTipText(jTextFieldVirtualCompNumValue.getText());
		jComboBoxStatus.setToolTipText(jComboBoxStatus.getSelectedItem().toString());
		jTextFieldSolubilityInSolvents.setToolTipText(jTextFieldSolubilityInSolvents.getText());
		jTextAreaStructureCommentsValue.setToolTipText(jTextAreaStructureCommentsValue.getText());
		jTextAreaPurity.setToolTipText(jTextAreaPurity.getText());
		jTextAreaMeltingPnt.setToolTipText(jTextAreaMeltingPnt.getText());
		jTextFieldResidualSolvents.setToolTipText(jTextFieldResidualSolvents.getText());
		jTextAreaExternalSupplier.setToolTipText(jTextAreaExternalSupplier.getText());
		jTextAreaBatchComments.setToolTipText(jTextAreaBatchComments.getText());
		jTextAreaHealthHazards.setToolTipText(jTextAreaHealthHazards.getText());
		jTextAreaHandlingPrecautions.setToolTipText(jTextAreaHandlingPrecautions.getText());
		jTextAreaStorageInstructions.setToolTipText(jTextAreaStorageInstructions.getText());
	}

	/**
	 * method populates the batchEditPanel Pass null object to blank the values.
	 * 
	 */
	public void populate() 
	{
		try {
			//if (isLoading)  //why doesn't this work anymore????
			// return;

			ProductPlate plate = null;
			isLoading = true;
			if (this.productBatchModel == null) {
				this.enableComponents(false);
				this.clearComponents();
				this.isLoading = false;
			} else {
				resetEditableFlag();
				this.enableComponents(isBatchEditable);
				if (pageModel.isParallelExperiment()) {
					plate = (ProductPlate) this.well.getPlate();
//					if (plate == null) return;
					
					String plateBarCode = this.well.getPlate().getPlateBarCode();
					if (lastEventObject instanceof CompoundContainer) {  //Plates view
						if (plateBarCode != null)  // vb 8/22 well create earlier than this date won't have a plate reference
							this.jLabelProductPlateName.setText(plateBarCode);  
						if (!isPseudoProductPlate) {
								this.amountInWellWtView.setEditable(plate.isEditable());
								this.amountInWellVolView.setEditable(plate.isEditable());
								CodeTableUtils.fillComboBoxWithSolvents(jComboBoxWellSolvent);
								jComboBoxWellSolvent.setEnabled(plate.isEditable());
								jComboBoxWellSolvent.setSelectedItem(BatchAttributeComponentUtility.getWellSolventDecrFromCode(well.getSolventCode()));
								amountInVialWtView.setEditable(false);

								this.amountInWellWtView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeWeight(well));
								this.amountInVialWtView.setValue(new AmountModel(UnitType.MASS, 0.0d));
								//well solvent
						} else {
							this.jLabelProductPlateName.setText("");
							this.amountInWellWtView.setEditable(false);
							this.amountInWellVolView.setEditable(false);
							jComboBoxWellSolvent.setEnabled(false);
							amountInVialWtView.setEditable(isBatchEditable);
							this.amountInVialWtView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeWeight(well));
							this.amountInWellWtView.setValue(new AmountModel(UnitType.MASS, 0.0d));
						}
	
						this.jLabelWellName.setText(this.well.getWellNumber());
						// Amount in well volume
						this.amountInWellVolView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeVolume(well));
						// Amount in well moles
						this.amountInWellMolesView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeMoles(well, productBatchModel));
//						// Amount remaining weight
//						this.amountRemainingWtView.setValue(BatchAttributeComponentUtility.getWeightRemaining(plate, productBatchModel));
//						// Amount remaining volume
//						this.amountRemainingVolView.setValue(BatchAttributeComponentUtility.getVolumeRemaining(plate, productBatchModel));
						
						if (!(plate instanceof PseudoProductPlate)) {
							jTextFieldTubeBarcode.setText(well.getBarCode());
							jTextFieldTubeBarcode.setEditable(true);
							jTextFieldVialBarcode.setText("");
							jTextFieldVialBarcode.setEditable(false);	
						} else {
							jTextFieldTubeBarcode.setText("");
							jTextFieldTubeBarcode.setEditable(false);
							jTextFieldVialBarcode.setText(well.getBarCode());
							jTextFieldVialBarcode.setEditable(true);
							amountInVialWtView.setEditable(true);
						}
					} else { //Products view
						this.jLabelProductPlateName.setText("");  
						this.amountInWellWtView.setEditable(false);
						this.amountInWellVolView.setEditable(false);
						jComboBoxWellSolvent.setEnabled(false);
						amountInVialWtView.setEditable(false);
						jTextFieldTubeBarcode.setEditable(false);
						jTextFieldVialBarcode.setEditable(false);
						this.jLabelWellName.setText("");
					}
					
					if (this.productBatchModel.isUserAdded())
						jCheckBoxProduct.setEnabled(true);
					else
						jCheckBoxProduct.setEnabled(false);
				}

				String nbkBatchNumber = this.productBatchModel.getBatchNumber().getBatchNumber();
				String conststr = nbkBatchNumber.substring(0, nbkBatchNumber.lastIndexOf("-") + 1);
				jLabelNbkBatchNumNbkPageValue.setText(conststr);
				String editstr = nbkBatchNumber.substring(nbkBatchNumber.lastIndexOf("-") + 1);
				jTextFieldNbkBatchNumSeqValue.setText(editstr);
				jCheckBoxProduct.setSelected(productBatchModel.isProductFlag());
				if (productBatchModel.isProductFlag())
					showTheoAmountComponents(true);
				else
					showTheoAmountComponents(false);
				jTextFieldConvBatchNumValue.setText(this.productBatchModel.getRegInfo().getConversationalBatchNumber());
				jTextFieldVirtualCompNumValue.setText(this.productBatchModel.getCompound().getVirtualCompoundId());
				jLabelBatchOwnerValue.setText(this.pageModel.getBatchOwner());
				jLabelSynthesizedByValue.setText(this.pageModel.getBatchCreator());
				
				// If there is a structure show molecular weight otherwise don't
				if (this.productBatchModel.getCompound().getStringSketchAsString().length() > 0) {
					double d = productBatchModel.getMolecularWeightAmount().GetValueInStdUnitsAsDouble();
					jLabelCalcBatchMWValue.setText(PCeNRegistrationProductsTableModelConnector.formatWeight(d));
					//jLabelCalcBatchMWValue.setText("" + productBatchModel.getMolecularWeightAmount().doubleValue());
					this.jLabelCalcBatchMW.setText(CALCULATED + BATCH_OR_COMPOUND + MW);
				} else {
					//this.jLabelCalcBatchMW.setText("");
					this.jLabelCalcBatchMWValue.setText("");
				}
				jLabelTherapeuticAreaValue.setText(CodeTableCache.getCache().getTAsDescription(pageModel.getTaCode()));
				jLabelCalcBatchMFValue.setText(productBatchModel.getMolecularFormula());
				jLabelProjectValue.setText(CodeTableCache.getCache().getProjectsDescription(pageModel.getProjectCode()));
				// Total amount made
				totalAmountMadeWtView.setValue(BatchAttributeComponentUtility.getTotalAmountMadeWeight(productBatchModel));
				// Total volume made
				this.totalAmountMadeVolView.setValue(BatchAttributeComponentUtility.getTotalAmountMadeVolume(productBatchModel));
				// Total moles
				this.totalAmountMadeMolesView.setValue(BatchAttributeComponentUtility.getTotalMoles(productBatchModel));
        // Amount remaining weight
        this.amountRemainingWtView.setValue(BatchAttributeComponentUtility.getWeightRemaining(plate, productBatchModel));
        // Amount remaining volume
        this.amountRemainingVolView.setValue(BatchAttributeComponentUtility.getVolumeRemaining(plate, productBatchModel));
        				
				this.componentUtility.updateCompoundStatesComboBox(this.productBatchModel, this.jComboBoxCompoundState);
				// Compound protection
				this.componentUtility.fillProtectionCodesComboBox(jComboBoxCompoundProtection); // vb 11/23, productBatchModel);
				// Salt code and equivalent
				this.componentUtility.fillSaltCodesComboBox(this.jComboBoxSaltCode, productBatchModel);
				this.updateSaltEquivs();
				////this.jTextFieldSaltEquivalents.setText(CeNNumberUtils.getBigDecimal(productBatchModel.getSaltEquivs(), 2).toString());
				// Stereoisomer 
				if (MasterController.isVnvEnabled())
					BatchAttributeComponentUtility.updateReadOnlyStereoisomerComboBox(jComboBoxStereoisomer, productBatchModel);
				else {
					CodeTableUtils.fillComboBoxWithIsomers(jComboBoxStereoisomer);
					String sic = productBatchModel.getCompound().getStereoisomerCode();
					for (int i = 0; i < jComboBoxStereoisomer.getItemCount(); ++i) {
						String line = jComboBoxStereoisomer.getItemAt(i).toString();
						if (sic != null && !sic.trim().equals("") && line.startsWith(sic)) {
							jComboBoxStereoisomer.setSelectedIndex(i);
							break;
						}
					}
				}
				//Structure comments
				this.jTextAreaStructureCommentsValue.setText(processString(productBatchModel.getCompound().getStructureComments()));
				
				//jComboBoxStereoisomer.setToolTipText("");
				//Disable and Enable VNV button and vcreg button
				validateAndEnableVnVButton();
				validateAndEnableVCRegButton();
				// Get the selectivity and continue status
				int selectivityStatus = this.productBatchModel.getSelectivityStatus();
				int continueStatus = this.productBatchModel.getContinueStatus();
				ProductBatchStatusMapper.getInstance().setStatus(this.jComboBoxStatus, selectivityStatus, continueStatus);
				// Compound source and detail are coordinated
				this.componentUtility.fillCompoundSourceAndDetailComboBoxes(productBatchModel, 
																			this.jComboBoxCompoundSource, 
																			this.jComboBoxSourceDetail, 
																			isBatchEditable, isBatchEditable);
				// External supplier
				if (this.productBatchModel.getVendorInfo() != null) {
					ExternalSupplierModel vendorInfo = this.productBatchModel.getVendorInfo();
					List<Properties> suppliers = this.componentUtility.getExternalSuppliersList();

					// supplier name not saved to db, so just get name from cached supplier list
					if (suppliers != null) {
						for (int i = 0; i < suppliers.size(); i++) {
							Properties alInner = suppliers.get(i);

							if (alInner != null &&
								alInner.size() > 1) {
								String code = (String) alInner.get(CodeTableCache.VENDOR__SUPPLIER_CODE);// Code
								String name = (String) alInner.get(CodeTableCache.VENDOR__SUPPLIER_DESC);

								if (code != null &&
									name != null && code.equals(vendorInfo.getCode())) {
									vendorInfo.setSupplierName(name);
									break;
								}
							}
						}
					}
					jTextAreaExternalSupplier.setText(vendorInfo.toString());
				} else
					this.jTextAreaExternalSupplier.setText("");
				
				// Precursors
				StringBuffer buff = new StringBuffer();
				if (pageModel.isParallelExperiment()) {
					List<String> monomerBatchKeys = productBatchModel.getReactantBatchKeys();
					ExperimentPageUtils pageUtils = new ExperimentPageUtils();
					for (String monomerKey : monomerBatchKeys) {
						MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, pageModel);
						String regNumer = monomerBatch.getCompound().getRegNumber();
						//TODO workaround for CENSTR
						if (StringUtils.isNotBlank(regNumer) && !regNumer.startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
							buff.append("  ").append(regNumer);
						}
					}
					if (pageModel.isEditable() && productBatchModel.isEditable()) {
						this.jTextFieldPrecursorsValues.setEnabled(productBatchModel.isUserAdded());
					}
					this.jTextFieldPrecursorsValues.setText(buff.toString());
				} else {
					if (pageModel.isEditable() && productBatchModel.isEditable()) {
						jTextFieldPrecursorsValues.setEnabled(true);
					}
					String regNumer = pageModel.getSingletonPrecursorsString(productBatchModel);
					//TODO workaround for CENSTR
					if (StringUtils.isNotBlank(regNumer) && !regNumer.trim().startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
						this.jTextFieldPrecursorsValues.setText(regNumer);
					} else {
						this.jTextFieldPrecursorsValues.setText("");
					}
				}
				jComboBoxCompoundProtection.setSelectedItem(BatchAttributeComponentUtility.getCompoundProtectionCodeAndDesc(productBatchModel.getProtectionCode()));
				
				// Get the purity list
				this.jTextAreaPurity.setText("");
				//this.jTextAreaPurity.setToolTipText("");
				List<PurityModel> purityModels = this.productBatchModel.getAnalyticalPurityList();
				this.jTextAreaPurity.setText(BatchAttributeComponentUtility.getPuritiesListAsString(purityModels));
				this.jTextAreaPurity.setCaretPosition(0);
				//this.jTextAreaPurity.setToolTipText(PurityModel.toToolTipString(purityModels));
				
				//Melting point.
				this.jTextAreaMeltingPnt.setText(this.productBatchModel.getMeltPointRange().toString());
				jTextAreaMeltingPnt.setCaretPosition(0);
				
				// Comment text areas
				this.jTextAreaBatchComments.setText(this.processString(productBatchModel.getComments()));
				updateDisplayOfHealthHazards();
				
				// residual solvents
				List<BatchResidualSolventModel> solventsList = this.productBatchModel.getRegInfo().getResidualSolventList();
				if (solventsList.size() != 0) {
					String residualString = CommonUtils.getResidualSolventsList(productBatchModel);
					jTextFieldResidualSolvents.setText(residualString);
				} else {
					jTextFieldResidualSolvents.setText("-none-");
					//jTextFieldResidualSolvents.setToolTipText(null);
				}
				// solubility in solvents
				List<BatchSolubilitySolventModel> solubilityList = this.productBatchModel.getRegInfo().getSolubilitySolventList();
				if (solubilityList.size() != 0) {
					String solubilityString = CommonUtils.getSolubilitySolventList(productBatchModel);
					jTextFieldSolubilityInSolvents.setText(solubilityString);
					//jTextFieldSolubilityInSolvents.setToolTipText(jTextFieldSolubilityInSolvents.getText());
				} else {
					jTextFieldSolubilityInSolvents.setText("-none-");
					jTextFieldSolubilityInSolvents.setToolTipText(null);
				}
				//if (productBatchModel.getTheoreticalWeightAmount().GetValueInStdUnitsAsDouble() > 0.0)
				if (productBatchModel.isProductFlag()) {
					this.jLabelTheoreticalAmountsWeightValue.setText(productBatchModel.getTheoreticalWeightAmount().GetValueForDisplay() + " mg");
					this.jLabelTheoreticalAmountsMolesValue.setText(productBatchModel.getTheoreticalMoleAmount().GetValueForDisplay());
				} else  {
					this.jLabelTheoreticalAmountsWeightValue.setText("");
					this.jLabelTheoreticalAmountsMolesValue.setText("");
				}
				AmountModel percentYieldAmount = productBatchModel.getTheoreticalYieldPercentAmount();
				if (percentYieldAmount != null) {
					if (percentYieldAmount.isValueDefault()) {
						this.jLabelPercentYieldValue.setText("");
					} else { 
						this.jLabelPercentYieldValue.setText(percentYieldAmount.GetValueForDisplay());
					}
				} else {
					this.jLabelPercentYieldValue.setText("");
				}
                 String currentValue = StringUtils.equals(productBatchModel.getRegInfo().getIntermediateOrTest(), "N") ? TEST_COMPOUND_BUTTON_LABEL : INTERMEDIATE_BUTTON_LABEL;
                 boolean isTestCompound = TEST_COMPOUND_BUTTON_LABEL.equals(currentValue);
                 testCompoundButton.setSelected(isTestCompound);
                 boolean isIntermediateCompound = INTERMEDIATE_BUTTON_LABEL.equals(currentValue);
                 intermediateButton.setSelected(isIntermediateCompound);
                 if (!isIntermediateCompound && !isTestCompound)
                     invisibleButton.setSelected(true);
			}
		} catch (CodeTableCacheException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			isLoading = false;
		} finally {
			isLoading = false;
		}
		refreshToolTips();
	}
	
	private void updateSaltEquivs() {
		componentUtility.updateSaltEquivs(productBatchModel, jComboBoxSaltCode, saltEquivalentsView);
		String code = BatchAttributeComponentUtility.getCode((String) jComboBoxSaltCode.getSelectedItem());
		if (code.equals("00")) {
			saltEquivalentsView.setEditable(false);
		} else {
			saltEquivalentsView.setEditable(CommonUtils.getProductBatchModelEditableFlag(productBatchModel, pageModel));
		}
	}

	private void validateAndEnableVnVButton() {
		if (productBatchModel == null  || StringUtils.isBlank(productBatchModel.getCompound().getMolFormula()) || (!isBatchEditable) || !MasterController.isVnvEnabled())
			jButtonVnV.setEnabled(false);
		else
			jButtonVnV.setEnabled(true);
	}
	
	/**
	 * <p>
	 * Conception records should allow VCRegistration without a valid VnV, 
	 * but Parallel experiments should prevent VCRegistration when the productBatchModel in question
	 * doesn't have a valid VnV.
	 * </p>
	 * <p>
	 * validation will succeed if the product batch exists and is editablebatch must exist
	 */
	private void validateAndEnableVCRegButton()
	{
		if (productBatchModel == null || (!isBatchEditable) || 
				!StringUtils.isEmpty(productBatchModel.getCompound().getVirtualCompoundId()) || 
				!BatchVnVInfoModel.VNV_PASS.equalsIgnoreCase(productBatchModel.getRegInfo().getBatchVnVInfo().getStatus()))
		{
			jButtonVCReg.setEnabled(false);
		} else {
			jButtonVCReg.setEnabled(true);
		}
	}
	
	private void showTheoAmountComponents(boolean visible) 
	{
		this.jLabelTheoreticalAmounts.setVisible(visible);
		this.jLabelTheoreticalAmounts.setVisible(visible);
		this.jLabelTheoreticalAmountsWeight.setVisible(visible);
		this.jLabelTheoreticalAmountsWeightValue.setVisible(visible);
		this.jLabelTheoreticalAmountsMoles.setVisible(visible);
		this.jLabelTheoreticalAmountsMolesValue.setVisible(visible);
		// updating visibility of the theoretical values in the table
		this.productBatchModel.getTheoreticalMoleAmount().setCanBeDisplayed(visible);
		this.productBatchModel.getTheoreticalWeightAmount().setCanBeDisplayed(visible);
	}

	private void enableComponents(boolean enable) {
		
		this.jTextFieldVirtualCompNumValue.setEnabled(true);
		
		if (pageModel.isConceptionExperiment()) {
			this.jTextFieldVirtualCompNumValue.setEditable(enable);
		} else {
			this.jTextFieldVirtualCompNumValue.setEditable(false);
		}
		
		if (enable) {			
			this.jTextFieldVirtualCompNumValue.setForeground(Color.BLACK);
		} else {
			this.jTextFieldVirtualCompNumValue.setForeground(Color.GRAY);
		}
		
		this.jTextFieldPrecursorsValues.setEnabled(enable);
		this.jTextFieldNbkBatchNumSeqValue.setEnabled(enable);
		this.jCheckBoxProduct.setEnabled(enable);
		this.jComboBoxStatus.setEnabled(false);
		this.jComboBoxStereoisomer.setEnabled(enable && !MasterController.isVnvEnabled());
		this.jTextAreaStructureCommentsValue.setEnabled(enable);
		this.jComboBoxCompoundSource.setEnabled(enable);
		this.jComboBoxSourceDetail.setEnabled(enable);
		// Make conversational batch number copyable but not editable
		this.jTextFieldConvBatchNumValue.setEnabled(true);
		if (enable) {			
			this.jTextFieldConvBatchNumValue.setForeground(Color.BLACK);
		} else {
			this.jTextFieldConvBatchNumValue.setForeground(Color.GRAY);
		}
		this.jTextFieldConvBatchNumValue.setEditable(false);
		this.jTextAreaExternalSupplier.setEnabled(false);
		////this.jComboBoxTotAmountMadeUnits.setEnabled(enable);
		//this.amountInWellView.setEditable(false);
		this.jComboBoxCompoundState.setEnabled(enable);
		this.jComboBoxSaltCode.setEnabled(enable);
		this.jComboBoxCompoundProtection.setEnabled(enable);
/*		this.jTextAreaHandlingPrecautions.setEnabled(false);
		this.jTextAreaHealthHazards.setEnabled(false);
		this.jTextAreaPurity.setEnabled(false);
		this.jTextAreaStorageInstructions.setEnabled(false);*/
		this.jTextAreaBatchComments.setEnabled(enable);
		this.jButtonExternalSupplierEdit.setEnabled(enable);
		this.jButtonHealthHazardsEdit.setEnabled(enable);
		this.jButtonResidualSolventsEdit.setEnabled(enable);
		this.jButtonSolubilityInSolventsEdit.setEnabled(enable);
		this.jButtonPurityEdit.setEnabled(enable);
		this.jButtonMeltingPntEdit.setEnabled(enable);
		this.saltEquivalentsView.setEditable(enable);
		//this.totalAmountMadeMolesView.setEditable(enable);
		
		//It is overridden by plate editablity later.
		this.amountInWellWtView.setEditable(enable);
		this.amountInWellVolView.setEditable(enable);
		jComboBoxWellSolvent.setEnabled(enable);

		this.amountInVialWtView.setEditable(enable);
		//this.totalAmountMadeMolesView.setEditable(enable);
		this.totalAmountMadeVolView.setEditable(enable);
		this.totalAmountMadeWtView.setEditable(enable);
		this.jTextFieldTubeBarcode.setEnabled(enable);
		this.jTextFieldVialBarcode.setEnabled(enable);
        testCompoundButton.setEnabled(enable);
        intermediateButton.setEnabled(enable);
		
		if (jTableTubeVialContainers != null) {
		  jTableTubeVialContainers.setEnabled(productBatchModel != null && pageModel.isEditable() && productBatchModel.getRegInfo().isCompoundManagementNotSubmitted());
	}
	}

	private void clearComponents() 
	{
		this.getChimePanel(); // this should set it to empty if the batch is null
		if (pageModel.isParallelExperiment()) {
			this.productBatchModel = null;
			this.jLabelProductPlateName.setText("");
			this.well = null;
			jLabelWellName.setText("");
		}
		jLabelCalcBatchMWValue.setText("");
		jLabelCalcBatchMFValue.setText("");
		jLabelNbkBatchNumNbkPageValue.setText("");

		this.jComboBoxStatus.setSelectedIndex(0);
		this.jComboBoxStereoisomer.removeAllItems();
		this.jTextAreaStructureCommentsValue.setText("");
		this.jComboBoxCompoundSource.setSelectedIndex(-1);
		this.jComboBoxSourceDetail.setSelectedIndex(-1);
		this.jTextFieldNbkBatchNumSeqValue.setText("");
		this.jCheckBoxProduct.setSelected(false);
		this.jTextFieldConvBatchNumValue.setText("");
		this.jTextFieldConvBatchNumValue.setEditable(false);
		this.jTextFieldVirtualCompNumValue.setText("");
		
		this.jTextAreaExternalSupplier.setText("");
		////this.jComboBoxTotAmountMadeUnits.setEnabled(enable);
//		this.totalAmountMadeView.setEnabled(enable);
		this.jComboBoxCompoundState.setSelectedIndex(0);
		this.jComboBoxSaltCode.setSelectedIndex(-1);
		this.jComboBoxWellSolvent.setSelectedIndex(-1);
		this.jComboBoxCompoundProtection.setSelectedIndex(-1);
		this.jTextAreaHandlingPrecautions.setEnabled(false);
		this.jTextAreaHealthHazards.setEnabled(false);
		this.jTextAreaPurity.setEnabled(false);
		this.jTextAreaMeltingPnt.setEnabled(false);
		this.jTextAreaStorageInstructions.setEnabled(false);
		//if (this.isParallel)
			//this.amountInWellView.setEnabled(enable);
		this.jTextAreaBatchComments.setText("");
		this.jTextFieldPrecursorsValues.setText("");
		this.amountInVialWtView.setValue(new AmountModel(UnitType.MASS));
		this.amountInWellMolesView.setValue(new AmountModel(UnitType.MOLES));
		this.amountInWellVolView.setValue(new AmountModel(UnitType.VOLUME));
		this.amountInWellWtView.setValue(new AmountModel(UnitType.MASS));
		this.amountRemainingVolView.setValue(new AmountModel(UnitType.VOLUME));
		this.amountRemainingWtView.setValue(new AmountModel(UnitType.MASS));
		this.totalAmountMadeMolesView.setValue(new AmountModel(UnitType.MOLES));
		this.totalAmountMadeVolView.setValue(new AmountModel(UnitType.VOLUME));
		this.totalAmountMadeWtView.setValue(new AmountModel(UnitType.MASS));
		totalAmountMadeWtView.refresh();
		this.jLabelTheoreticalAmountsMolesValue.setText("");
		this.jLabelTheoreticalAmountsWeightValue.setText("");
		this.jLabelPercentYieldValue.setText("");

         invisibleButton.setSelected(true);

		if (tubeVialTableConnector != null) {
		  tubeVialTableConnector.setProductBatchModel(null);
	  }
		

		//this.getChimePanel(),               cc.xywh(col5,   4,   5,  10)

		//this.getCompo
		//this.saltEquivalentsView.setEnabled(enable);
		//this.jButtonExternalSupplierEdit.setEnabled(enable);
		//this.jButtonHandlingPrecautionsEdit.setEnabled(enable);
/*		this.jButtonHealthHazardsEdit.setEnabled(enable);
		this.jButtonResidualSolventsEdit.setEnabled(enable);
		this.jButtonSolubilityInSolventsEdit.setEnabled(enable);
		this.jButtonStorageInstructionsEdit.setEnabled(enable);
		this.jButtonPurityEdit.setEnabled(enable);
*/		////this.jTextFieldTotAmountMade.setEnabled(enable);		
	}	
	
	/**
	 * Builds the panel using the Form Layout.
	 * @return
	 */
	private JPanel getPanel() 
	{
		if (pageModel.isParallelExperiment() || pageModel.isSingletonExperiment()) {
			// Set row format
			StringBuffer rowFormatBuff = new StringBuffer();
			for (int i=0; i < NUM_ROWS_PARALLEL - 1; i++)
				rowFormatBuff.append("pref, ");
			rowFormatBuff.append("pref");
			FormLayout layout = new FormLayout("left:pref, 7dlu, " +  // 1, 2 (col 1 and space)
											   "63dlu, pref, " +      // 3, 4 (col 2 and col 3)
											   "40dlu, 24dlu, " +     // 5, 6 (col 4 and space)
											   "50dlu, 50dlu, 50dlu, 50dlu, 40dlu, 45dlu", // 7, 8, 9, 19 (space, col5 and col6)
											   rowFormatBuff.toString());						   
			// Set row groups (all rows are in group)
			int[][] rowGroups = new int[1][NUM_ROWS_PARALLEL];
			for (int i=0; i<NUM_ROWS_PARALLEL; i++)
				rowGroups[0][i] = i+1;
			layout.setRowGroups(rowGroups);
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			int row = 1;
			int col1 = 1;
			int col2 = 3;
			int col3 = 4;
			int col4 = 6;
			int col5 = 7;
			int col6 = 10;

			builder.add(jLabelEditBatchHeader,              	cc.xyw (col1,   row,   7));     row++;
			builder.add(this.getEditableChimePanel(),  		cc.xywh(col5,   2,   5,  9)); 
			if (pageModel.isParallelExperiment()) {
				builder.add(jLabelProductPlate,                 cc.xy  (col1,   row));
				builder.add(jLabelProductPlateName,             cc.xyw (col2,   row,   2));    	row++;
				builder.add(jLabelWell,                 		cc.xy  (col1,   row));
				builder.add(jLabelWellName,             		cc.xyw (col2,   row,   2));     row++;
			} else {
/*				builder.add(jLabelTubeBarcode,              	cc.xy  (col1,   row));
				builder.add(jTextFieldTubeBarcode,         		cc.xyw (col2,   row,   2));     row++;
				builder.add(jLabelVialBarcode,             		cc.xy  (col1,   row));
				builder.add(jTextFieldVialBarcode,        		cc.xyw (col2,   row,   2));     row++;				
*/			}

			builder.add(jLabelNbkBatchNum,                  	cc.xy  (col1,   row)); 
			builder.add(jLabelNbkBatchNumNbkPageValue,      	cc.xyw (col2,   row,   1));
			builder.add(jTextFieldNbkBatchNumSeqValue,      	cc.xyw (col3,   row,   1));

			builder.add(jCheckBoxProduct,      					cc.xyw (5,   row,   1));    	row++;
			builder.add(jLabelStatus,                       	cc.xy  (col1,   row));
			builder.add(jComboBoxStatus,    					cc.xyw (col2,   row,  2));      row++;
			builder.add(jLabelConvBatchNum,                 	cc.xy  (col1,   row));
			builder.add(jTextFieldConvBatchNumValue,        	cc.xyw (col2,   row,   2));     row++;		
			builder.add(jLabelVirtualCompNum,               	cc.xy  (col1,   row));
			builder.add(jTextFieldVirtualCompNumValue,          	cc.xyw (col2,   row,   2));     row++;		
			
			builder.add(jLabelStereoisomer,                 	cc.xyw  (7,  11, 2));
			builder.add(jComboBoxStereoisomer,              	cc.xyw (9,   11,   2));     
			builder.add(jButtonVnV,                         	cc.xyw (11,  11,   1));         //row++;
			
			// Next column/ on top of Chime panel
			builder.add(jLabelStructureComments,                cc.xyw(col5,   12, 2));     
			builder.add(jScrollPaneStructureComments,         	cc.xywh(9,   12, 3, 2));
			
			// row Compound Source & Source Detail
			builder.add(jLabelCompoundSource,               	cc.xy  (col1,   row));
			builder.add(jComboBoxCompoundSource,				cc.xyw (col2,   row,  2));      row++;
			builder.add(jLabelSourceDetail,                 	cc.xy  (col1,   row));
			builder.add(jComboBoxSourceDetail,		    		cc.xyw (col2,   row,  2));      row++;

			// row Batch Mol.Wgt & Fmla
			builder.add(jLabelCalcBatchMW,                   	cc.xy  (col1,   row));
			builder.add(jLabelCalcBatchMWValue,		    		cc.xyw (col2,   row,  2));      row++;
			builder.add(jLabelCalcBatchMF,                  	cc.xy  (col1,   row));
			builder.add(jLabelCalcBatchMFValue,	    	    	cc.xyw (col2,   row,  2));      row++;

			// row Salt and Salt Equiv.
			builder.add(jLabelSaltCode,                     	cc.xy  (col1,   row));          
			builder.add(jComboBoxSaltCode,                  	cc.xyw (col2,  row, 2));        row++;
			builder.add(jLabelSaltEquivalents,              	cc.xy  (col1,   row));          
			builder.add(saltEquivalentsView,                	cc.xyw (col2,   row, 2));          			


			// Theo panel
			row = 14;
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(testCompoundButton);
            buttonGroup.add(intermediateButton);
            buttonGroup.add(invisibleButton);

            builder.add(intermediateButton,    			cc.xy(col2, row));
            builder.add(testCompoundButton,    			cc.xy(col3, row));
            builder.add(makeTheoAmountPanel(cc),    			cc.xywh(col5, row, 5, 1));		row++;
			
			builder.add(this.makeCeNLabel("Percent Yield", PLAIN_LABEL_FONT),    cc.xywh(col5, row, 2, 1));
			builder.add(this.jLabelPercentYieldValue,                            cc.xy(col5 + 2, row ));

			builder.add(jLabelCompoundState,                	cc.xy  (col1,   row));          
			builder.add(jComboBoxCompoundState,             	cc.xyw (col2,   row, 2));       row++;
			
			builder.add(jLabelBatchComments,                	cc.xy  (col1,   row));   
			builder.add(jScrollPaneBatchComments,           	cc.xywh(col2,   row,  2,  3));  row+=3;
			
			builder.add(jLabelExternalSupplier,             	cc.xy  (col1,   row));
			builder.add(jScrollPaneExternalSupplier,        	cc.xywh (col2,   row,   2, 3));  
			builder.add(jButtonExternalSupplierEdit,        	cc.xy   (5,   row));            row+=3;

			// row purity	
			builder.add(jLabelPurity,                       	cc.xy   (col1,   row));      
			builder.add(jScrollPanePurity,                  	cc.xywh (col2,   row,  2,  3)); 
			builder.add(jButtonPurityEdit,                  	cc.xy   (5,   row));            row+=3;
			
			//Melting point
			builder.add(jLabelMeltingPoint,                 	cc.xy   (col1,   row));      
			builder.add(jScrollMeltingPnt,                  	cc.xywh (col2,   row,  2,  3)); 
			builder.add(jButtonMeltingPntEdit,              	cc.xy   (5,   row));            row+=3;

			// row  residual solvents	
			builder.add(jLabelResidualSolvents,             	cc.xy  (col1,   row));
			builder.add(jTextFieldResidualSolvents,	        	cc.xyw (col2,   row,  2));      
			builder.add(jButtonResidualSolventsEdit,        	cc.xy  (5,   row));            	row++;

			// row solubility in solvents
			builder.add(jLabelSolubilityInSolvents,         	cc.xy  (col1,   row));          
			builder.add(jTextFieldSolubilityInSolvents,     	cc.xyw (col2,   row,  2));      
			builder.add(jButtonSolubilityInSolventsEdit,    	cc.xy   (5,   row));            row++;

			// row precursors
			builder.add(jLabelPrecursors,                   	cc.xy  (col1,   row));          
			builder.add(jTextFieldPrecursorsValues,             cc.xyw (col2,   row, 5));       row++;
			
			// row Compound Protection
			builder.add(jLabelCompoundProtection,           	cc.xy  (col1,   row));
			builder.add(jComboBoxCompoundProtection,        	cc.xyw (col2,   row,  6));      row++;

			row++;
			builder.add(jButtonHealthHazardsEdit,           	cc.xy  (col1,     row)); 
			builder.add(new JLabel("Edit Health Hazards, Handling Precautions and Storage Instructions"),  	cc.xyw  (col2,     row, 8));	row++;
			// row Hazards
			builder.add(jLabelHealthHazards,                	cc.xy  (col1,   row));
			builder.add(jScrollPaneHealthHazards,           	cc.xywh(col2,   row,  8, 2));   
			builder.add(new JLabel(""),           				cc.xy  (11,     row));          
			row+=2;

			// row Handling Precautions
			builder.add(jLabelHandlingPrecautions,          	cc.xy  (col1,   row));
			builder.add(jScrollPaneHandlingPrecautions,     	cc.xywh(col2,   row,  8, 2));   
			builder.add(new JLabel(""),					     	cc.xy  (11,     row));          
			row+=2; 
			
			// row Storage Instructions
			builder.add(jLabelStorageInstructions,         		cc.xy  (col1,   row));
			builder.add(jScrollPaneStorageInstructions,     	cc.xywh(col2,   row,  8, 2));
			builder.add(new JLabel(""),					     	cc.xy  (11,     row));          
			row+=2;
			
//			builder.add(jLabelTotWeight,                    	cc.xy  (col1,   row));
//			builder.add(totalWeightView,    	            	cc.xyw (col2,   row,  2));     	row++;
//			builder.add(jLabelTotVolume,                    	cc.xy  (col1,   row));
//			builder.add(totalVolumeView,    	            	cc.xyw (col2,   row,  2));     	row++;			

//			builder.add(jLabelTheoreticalAmounts,          		cc.xy  (col1,   row));
//			builder.add(jLabelTheoreticalAmountsWeight,    		cc.xyw (col2,   row,  1));     	row++;

//			builder.add(jLabelAmountInWell,                		cc.xy  (col1,   row));
//			builder.add(amountInWellView,  	                	cc.xyw (col2,   row,  2));  
//			builder.add(jLabelTheoreticalAmountsMoles,    		cc.xyw (col3,   row,  1));  	
//			builder.add(jLabelCompoundState,                	cc.xy  (col5,   row));          
//			builder.add(jComboBoxCompoundState,             	cc.xyw (col6,   row, 2));       row++;

			// Amount values
			row = 16;
			
			// Total amount made and amount remaining
			builder.add(makeAmountRemainingPanel(cc), 			cc.xywh(col5, row, 5, 5));      row+=6;
			
			if ((this.productPlate == null) || (this.productPlate instanceof PseudoProductPlate) ||
			    (!(lastEventObject instanceof CompoundContainer))) {  // not Plates view  
			  
				// Vial amount panel - visible only for non-plated batches
				builder.add(makeVialAmountPanel(cc),cc.xywh(col5, row, 6, 7)); 					row+=7;
			} else {
				// Well tube amounts panel - visible only for plated batches
				builder.add(makeWellTubeAmountPanel(cc), cc.xywh(col5, row, 5, 6));           row+=6;// cc.xywh(col5, row, 5, 4));      		row+=4;
			}	
			return builder.getPanel();
		} else {  // Conception Page
			StringBuffer rowFormatBuff = new StringBuffer();
			for (int i=0; i < NUM_ROWS_NONPARALLEL - 1; i++)
				rowFormatBuff.append("pref, ");
			rowFormatBuff.append("pref");
			FormLayout layout = new FormLayout("left:pref, 7dlu, " +  	// 1, 2 (col 1 and space)
											   "63dlu, pref, " +  		// 3, 4 (col 2 and col 3)
											   "40dlu, 24dlu, " + 		// 5, 6 (col 4 and space)
											   "50dlu, 50dlu, 50dlu, 50dlu, 40dlu, 45dlu", // 7, 8, 9, 19 (space, col5 and col6)
											   rowFormatBuff.toString());						   
			// Set row groups (all rows are in group)
			int[][] rowGroups = new int[1][NUM_ROWS_NONPARALLEL];
			for (int i=0; i < NUM_ROWS_NONPARALLEL; i++)
				rowGroups[0][i] = i+1;
			layout.setRowGroups(rowGroups);
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			int row = 1;
			int col1 = 1;
			int col2 = 3;
			int col3 = 4;
			int col4 = 6;
			int col5 = 7;
			int col6 = 10;

			builder.add(jLabelEditBatchHeader,              	cc.xyw (col1,   row,   7));     row++;
			
			builder.add(jLabelNbkBatchNum,                  	cc.xy  (col1,   row)); 
			builder.add(jLabelNbkBatchNumNbkPageValue,      	cc.xyw (col2,   row,   1)); 
			builder.add(jTextFieldNbkBatchNumSeqValue,      	cc.xyw (col3,   row,   1));     row+=2;
			
			builder.add(jLabelVirtualCompNum,               	cc.xy  (col1,   row));
			builder.add(jTextFieldVirtualCompNumValue,        		cc.xyw (col2,   row,   2));
			builder.add(jButtonVCReg,                       	cc.xy  (5,  	row));     		row+=2;

			builder.add(this.getEditableChimePanel(),  			cc.xywh(col5,   2,   5,  9)); 
			
			builder.add(jLabelCalcBatchMW,                 		cc.xy  (col1,   row));
			builder.add(jLabelCalcBatchMWValue,    				cc.xyw (col2,   row,  2));      row++;
			
			builder.add(jLabelCalcBatchMF,                  	cc.xy  (col1,   row));
			builder.add(jLabelCalcBatchMFValue,        			cc.xyw (col2,   row,   2));     row+=2;
			
			builder.add(jLabelBatchComments,                	cc.xy  (col1,   row));   
			builder.add(jScrollPaneBatchComments,           	cc.xywh(col2,   row,  2,  4));  row+=4;

			builder.add(jLabelStereoisomer,                 	cc.xyw  (7,  11, 2));
			builder.add(jComboBoxStereoisomer,              	cc.xyw (9,   11,   2));     
			builder.add(jButtonVnV,                         	cc.xyw (11,  11,   1));

			builder.add(jLabelStructureComments,                cc.xyw(col5,   12, 2));     
			builder.add(jScrollPaneStructureComments,         	cc.xywh(9,   12, 3, 2));
			
			return builder.getPanel();			
		}		
	}

	private JPanel makeAmountRemainingPanel(CellConstraints cc) 
	{
		JPanel amountRemainingPanel = new JPanel();
		amountRemainingPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		FormLayout layout0 = new FormLayout("left:1dlu, 80dlu, 10dlu, 60dlu, 10dlu, 60dlu", "1dlu, pref, 3dlu, pref, 3dlu, pref, 7dlu");
		PanelBuilder builder0 = new PanelBuilder(layout0);
		//builder0.add(this.makeCeNLabel("Total Amount Made", PLAIN_LABEL_FONT),    cc.xy(2, 2));
		builder0.add(this.makeLabelWithAsterisk("Total Amount Made"),         		cc.xy(2, 2));
		builder0.add(this.totalAmountMadeWtView,              					    cc.xy(4, 2));
		builder0.add(this.totalAmountMadeVolView,                                   cc.xy(6, 2));
		builder0.add(this.makeCeNLabel(TOTAL_MOLES, PLAIN_LABEL_FONT),              cc.xy(2, 4));
		builder0.add(this.totalAmountMadeMolesView,                                 cc.xy(6, 4));
		builder0.add(this.makeCeNLabel("Amount Remaining", PLAIN_LABEL_FONT),       cc.xy(2, 6));
		builder0.add(this.amountRemainingWtView,                                    cc.xy(4, 6));
		builder0.add(this.amountRemainingVolView,                                   cc.xy(6, 6));
		amountRemainingPanel.add(builder0.getPanel());
		return amountRemainingPanel;
	}

	private JPanel makeTheoAmountPanel(CellConstraints cc) 
	{
		JPanel theoAmountPanel = new JPanel();
		FormLayout layout3 = new FormLayout("left:pref, 8dlu, pref, 3dlu, pref, 8dlu, pref, 3dlu, pref", "pref");
		PanelBuilder builder3 = new PanelBuilder(layout3);
		//PanelBuilder builder3 = new PanelBuilder(layout3, new FormDebugPanel());
		builder3.add(jLabelTheoreticalAmounts,      					 cc.xy(1, 1));
		builder3.add(jLabelTheoreticalAmountsWeight,      				 cc.xy(3, 1));
		builder3.add(this.jLabelTheoreticalAmountsWeightValue,           cc.xy(5, 1));
		builder3.add(jLabelTheoreticalAmountsMoles,      				 cc.xy(7, 1));
		builder3.add(this.jLabelTheoreticalAmountsMolesValue,            cc.xy(9, 1));		
		theoAmountPanel.setLayout(new FormLayout("left:pref", "pref"));
		theoAmountPanel.add(builder3.getPanel(), 						 cc.xy(1, 1));
		return theoAmountPanel;
	}
	
   /*
    //added different impl to create a table
	private JPanel makeVialAmountPanel(CellConstraints cc) 
	{
		JPanel vialAmountPanel = new JPanel();
		vialAmountPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		FormLayout layout2 = new FormLayout("left:1dlu, 80dlu, 10dlu, 60dlu, 10dlu, 60dlu", "2dlu, pref, 3dlu, pref, 2dlu");
		PanelBuilder builder2 = new PanelBuilder(layout2);
		builder2.add(this.makeCeNLabel("Amount in Vial/Tube", PLAIN_LABEL_FONT),    cc.xy(2, 2));
		builder2.add(this.amountInVialWtView,      									cc.xy(4, 2));
		if (isParallel) {
			builder2.add(jLabelVialBarcode,        									cc.xy(2, 4));
			builder2.add(jTextFieldVialBarcode,    									cc.xyw(4, 4, 3));
		}
		vialAmountPanel.add(builder2.getPanel());
		amountInVialWtView.setEditable(isBatchEditable);		
		jTextFieldVialBarcode.setEditable(isBatchEditable);		
		return vialAmountPanel;
	}
  */
	
	private JPanel makeWellTubeAmountPanel(CellConstraints cc) 
	{
		JPanel wellTubeAmountPanel = new JPanel();
		wellTubeAmountPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		FormLayout layout1 = new FormLayout("left:1dlu, 80dlu, 10dlu, 60dlu, 10dlu, 60dlu", "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu");
		PanelBuilder builder1 = new PanelBuilder(layout1);
		builder1.add(jLabelAmountInWell,                cc.xy(2, 2));
		builder1.add(this.amountInWellWtView,              cc.xy(4, 2));
		builder1.add(amountInWellVolView,              cc.xy(6, 2)); 
		builder1.add(this.makeCeNLabel(MOLES, PLAIN_LABEL_FONT),                    cc.xy(2, 4));
		builder1.add(amountInWellMolesView,                  cc.xy(6, 4));
		//well solvent
		builder1.add(jLabelWellSolvent, cc.xy(2, 6));
		builder1.add(jComboBoxWellSolvent, cc.xyw(4 , 6, 3));
			builder1.add(jLabelTubeBarcode, cc.xy(2, 8));
			builder1.add(jTextFieldTubeBarcode, cc.xyw(4 , 8, 3));						
		wellTubeAmountPanel.add(builder1.getPanel(),         cc.xy(1, 1));
		return wellTubeAmountPanel;
	}

	private JPanel makeVialAmountPanel(CellConstraints cc) 
	{
		boolean editable = productBatchModel != null && pageModel.isEditable() && productBatchModel.isEditable();
		
		JPanel vialTubeTablePanel = new JPanel();
		vialTubeTablePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		tubeVialTablePopupManager = editable ? new TubeVialContainerTablePopupMenuManager(this) : null;
		JLabel jLabelContainerHeader = makeCeNLabel("Vials/Tubes Containing this Batch", PLAIN_LABEL_FONT);
		
		vialTubeTablePanel.setLayout(new BorderLayout());
		vialTubeTablePanel.add(jLabelContainerHeader, BorderLayout.NORTH);
		JScrollPane jScrollPane = new JScrollPane();
		tubeVialTableConnector = new TubeVialContainerTableConnector(this.pageModel,this.productBatchModel);
		jTableTubeVialContainers = new TubeVialContainerTableView(new TubeVialContainerTableModel(tubeVialTableConnector),StoicConstants.TABLE_ROW_HEIGHT,tubeVialTableConnector,tubeVialTablePopupManager,this.productBatchModel);
		jScrollPane.add(jTableTubeVialContainers);
		jScrollPane.setViewportView(jTableTubeVialContainers);
		
		vialTubeTablePanel.add(jScrollPane, BorderLayout.CENTER);
		tubeVialTableConnector.addAmountEditListener(this);

		tubeVialTableConnector.setReadOnly(!editable);
		
		return vialTubeTablePanel;
	}
		
    private Component getEditableChimePanel() {    	
    	BatchContainer batchContainer = null;
    	
    	if (parentWindow == null || productBatchModel == null) {
    		batchContainer = new BatchContainer(false, pageModel);
    		batchContainer.setReadOnly(true);
    	} else {
    		Map<String, BatchContainer> cache = parentWindow.getBatchContainerCache();
    	
    		batchContainer = cache.get(productBatchModel.getKey());
    		
    		if (batchContainer == null) {
    			batchContainer = new BatchContainer(false, pageModel, productBatchModel, this, compoundCreationHandler);
    			cache.put(productBatchModel.getKey(), batchContainer);
    		} else {
    			batchContainer.refreshChemitry();
    		}
    		batchContainer.setReadOnly(!isBatchEditable);
    	}
    	
    	return batchContainer;
	}

	/**
     * Create a label object.
     * @param text
     * @param font
     * @return
     */	
	private JLabel makeLabel(String text, Font font) 
	{
		JLabel label = new JLabel(text);
		label.setFont(font);
		return label;
	}

	/**
     * Create a CeNlabel object. ":" will be appended in the end. default font will be set.
     * @param text
     * @param font //Will be ignored.
     * @return
     */	
	private CeNLabel makeCeNLabel(String text, Font font) 
	{
		CeNLabel label = new CeNLabel(text);
		//label.setFont(font);
		return label;
	}
	
	// vb 10/21
	public JLabel makeLabelWithAsterisk(String caption) 
	{
		StringBuffer buff = new StringBuffer();
		buff.append("<html><SPAN style='font-size:11.1pt;font-weight:bold;font-family:Sansserif'><font color=\"FF0000\">* </font>");
		buff.append(caption);
		buff.append(":");
		buff.append("</SPAN></html>");
		return new JLabel(buff.toString());
	}
	
	private JCheckBox makeCeNCheckBox(String text) 
	{
		JCheckBox jCheckBox = new JCheckBox(text);
		jCheckBox.addChangeListener(new PropertyItemListener(text));
		return jCheckBox;
	}
	
    private JRadioButton makeCeNRadioButton(String text)
    {
        JRadioButton radio = new JRadioButton(text);
        radio.addChangeListener(new PropertyItemListener(text));
        return radio;
    }

	/**
	 * Create a tex field object and set listeners.
	 * @param text
	 * @param size
	 * @param property
	 * @return
	 */
	private JTextField makeTextField(String text, int size, String property) 
	{
		JTextField textField = new JTextField(text, size);
		textField.setFont(EDIT_FONT_SMALL);
		textField.getDocument().addDocumentListener(new BatchDocumentListener());
		textField.addFocusListener(new PropertyFocusListener(property));
		textField.addKeyListener(new TextFieldKeyListener(property));
		return textField;
	}

	private PAmountComponent makeAmountComponent(AmountModel amountModel) 
	{
		PAmountComponent amountComponent = new PAmountComponent(amountModel);
		amountComponent.setEditable(true);
		//amountComponent.setValueSetTextColor(null);
		amountComponent.addAmountEditListener(this);
		amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());	
		return amountComponent;
	}

	/**
	 * Create a text area object and set its listeners.
	 */
	private JTextArea makeTextArea(String property, boolean inScrollPane) 
	{
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setFont(EDIT_FONT_LARGE);
		if (! inScrollPane)
			textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, Color.lightGray, null, Color.black));
		textArea.getDocument().addDocumentListener(new BatchDocumentListener());
		textArea.addFocusListener(new PropertyFocusListener(property));
		return textArea;
	}

	private JScrollPane makeScrollPane(JTextArea textarea) 
	{
		JScrollPane sp = new JScrollPane(textarea);
		sp.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
		sp.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//sp.setViewportBorder(new EmptyBorder(new Insets(0,0,0,0)));
		return sp;
	}

	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * @param pageModel the pageModel to set
	 */
	public void refreshPageModel(NotebookPageModel pageModel) {
		if (this.pageModel != pageModel) {
			this.pageModel = pageModel;
		}
		enableComponents(pageModel.isEditable());
		populate();
		getChimePanel();
		getEditableChimePanel();
	}
	
	
	private void insertBlankItem(JComboBox comboBox) 
	{
		comboBox.insertItemAt("", 0);
	}
	
	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	public JPanel getChimePanel() {
		String stringSketch = "";
		if (productBatchModel != null) {
			stringSketch = productBatchModel.getCompound().getStringSketchAsString();
			stringSketch = Decoder.decodeString(stringSketch);
		} else {
			chime.setEnabled(false);
		}
		chime.setMolfileData(stringSketch);
		return chime;
	}
	
	private void enableSaveButton() 
	{
		if (this.productBatchModel != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			this.productBatchModel.setModelChanged(true);
			this.pageModel.setModelChanged(true);
			// The singleton page model is set to uneditable.  Why?
			// Temporary fix...
			this.pageModel.setEditable(true);
			// vb 2/5 This takes FOREVER because it completely updates the registration tables!
			///////////	if (compoundCreationHandler != null)
			//////////		compoundCreationHandler.fireCompoundUpdated(new CompoundCreatedEvent(this, productBatchModel, null));
		}
	}
	
   //This Method syncs the UI comp to BatchModel
	private void updateProperty(String property)
	{
		if (isLoading || this.productBatchModel == null)
			return;
		
		if (!isBatchEditable) {
			if (!(property.equals(AMOUNT_IN_WELL_TUBE_WEIGHT) || property.equals(AMOUNT_IN_WELL_TUBE_VOLUME)
				|| property.equals(AMOUNT_IN_WELL_TUBE_MOLES) || property.equals(AMOUNT_IN_VIAL_WEIGHT)))
				return;
		}
		
		isLoading = true;
		if (property != null && property.length() > 0) {
			try {
				if (property.equals(NOTEBOOK_BATCH_NUM_EDITABLE)) {
/*					String nbkBatchNumber = this.productBatchModel.getBatchNumber().getBatchNumber();
					nbkBatchNumber = nbkBatchNumber.substring(0, nbkBatchNumber.lastIndexOf("-") + 1) + this.jTextFieldNbkBatchNumSeqValue.getText().toUpperCase();
					if (ResourceKit.iaValidBatchNumber(nbkBatchNumber)) {
						BatchNumber batchNumber = new BatchNumber();
						batchNumber.setBatchNumber(nbkBatchNumber);
						this.productBatchModel.setBatchNumber(batchNumber);
						enableSaveButton();
					} else {
						JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Invalid batch number format. Please correct it to format ####C#");
						nbkBatchNumber = this.productBatchModel.getBatchNumber().getBatchNumber();
						String editstr = nbkBatchNumber.substring(nbkBatchNumber.lastIndexOf("-") + 1);
						jTextFieldNbkBatchNumSeqValue.setText(editstr);
					}
*/				
					String value = this.jTextFieldNbkBatchNumSeqValue.getText().toUpperCase();
					try {
						if (BatchAttributeComponentUtility.setNotebookBatchNumber(productBatchModel, value, pageModel)) 
							enableSaveButton();
					} catch (InvalidBatchNumberException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Data Exception", JOptionPane.ERROR_MESSAGE);
						String nbkBatchNumber = this.productBatchModel.getBatchNumber().getBatchNumber();
						String editstr = nbkBatchNumber.substring(nbkBatchNumber.lastIndexOf("-") + 1);
						jTextFieldNbkBatchNumSeqValue.setText(editstr);
						jTextFieldNbkBatchNumSeqValue.requestFocus();
					}
				} else if (property.equals(PRODUCT)) {
					this.productBatchModel.setProductFlag(jCheckBoxProduct.isSelected());
					this.populate();
					enableSaveButton();
				} else if (property.equals(STORAGE_INSTRUCTIONS)) {
					if(this.productBatchModel.getStorageComments() == null || !this.productBatchModel.getStorageComments().equalsIgnoreCase(jTextAreaStorageInstructions.getText())) {
						this.productBatchModel.setStorageComments(jTextAreaStorageInstructions.getText());
						enableSaveButton();
					}
				} else if (property.equals(AMOUNT_IN_WELL_TUBE_WEIGHT)) {
					if (this.well != null) {
					  AmountModel molarity = well.getMolarity();
					  String value = molarity.getValue();
					  
					  molarity.setValue("0"); //when weight changes, don't allow volume to automatically change, since in this case both values are entered manually, so molarity should not be taken into consideration
						BatchAttributeComponentUtility.setAmountInWellOrTubeWeight(well, this.amountInWellWtView.getAmount(), productBatchModel);
						molarity.setValue(value);
						this.populate();
						enableSaveButton();
					}
				} else if (property.equals(AMOUNT_IN_WELL_TUBE_VOLUME)) {
					if (this.well != null) {
						//if (amountInWellVolView.getAmount().GetValueInStdUnits() > )
						BatchAttributeComponentUtility.setAmountInWellOrTubeVolume(well, this.amountInWellVolView.getAmount(), productBatchModel);
						this.populate();
						enableSaveButton();
					}
				} else if (property.equals(AMOUNT_IN_WELL_TUBE_MOLES)) {
					if (this.well != null) {
						BatchAttributeComponentUtility.setAmountInWellOrTubeMoles(well, this.amountInWellMolesView.getAmount(), productBatchModel);
						this.populate();
						enableSaveButton();
					}
				} else if (property.equals(TOTAL_MOLES)) {
					//if (this.well != null) {
						BatchAttributeComponentUtility.setTotalMoles(productBatchModel, this.totalAmountMadeMolesView.getAmount(), well);
						this.populate();
						enableSaveButton();
					//}
				} else if (property.equals(AMOUNT_IN_VIAL_WEIGHT)) {
/*					if (this.well == null)
							well = ((PseudoProductPlate)productPlate).moveBatchesToContainers(productBatchModel);
*/
						BatchAttributeComponentUtility.setAmountInWellOrTubeWeight(well, this.amountInVialWtView.getAmount(), productBatchModel);
						this.populate();
						enableSaveButton();
				} else if (property.equals(TOTAL_WEIGHT)) {
					if (BatchAttributeComponentUtility.setTotalAmountMadeWeight(this.productBatchModel, this.totalAmountMadeWtView.getAmount(), well)) {
						this.populate();
						enableSaveButton();
					}
				} else if (property.equals(TOTAL_VOLUME)) {
					if (BatchAttributeComponentUtility.setTotalAmountMadeVolume(this.productBatchModel, totalAmountMadeVolView.getAmount(), well)) {
						this.populate();
						enableSaveButton();
					}
				} else if (property.equals(STEREOISOMER)) {
					if (MasterController.isVnvEnabled())
						BatchAttributeComponentUtility.updateReadOnlyStereoisomerComboBox(jComboBoxStereoisomer, productBatchModel);
					else {
						String sic = jComboBoxStereoisomer.getSelectedItem().toString();
						sic = sic.substring(0, sic.indexOf('-')).trim();
						productBatchModel.getRegInfo().getBatchVnVInfo().setAssignedStereoIsomerCode(sic);
						productBatchModel.getCompound().setStereoisomerCode(sic);
					}
                    enableSaveButton();
                    validateAndEnableVnVButton();
                } else if (property.equals(STRUCTURE_COMMENTS)) {
					String structureComments = (String) jTextAreaStructureCommentsValue.getText();
					if(this.productBatchModel.getCompound().getStructureComments() ==null || !this.productBatchModel.getCompound().getStructureComments().equalsIgnoreCase(structureComments)) {
						this.productBatchModel.getCompound().setStructureComments(structureComments);
						enableSaveButton();
					}
				} 
				else if (property.equals(STATUS)) {
					String selectedItem = (String) jComboBoxStatus.getSelectedItem();
					int selectivityStatus = ProductBatchStatusMapper.getInstance().getSelectivityStatus(selectedItem);
					if (selectivityStatus >= 0) 
						this.productBatchModel.setSelectivityStatus(selectivityStatus);
					int continueStatus = ProductBatchStatusMapper.getInstance().getContinueStatus(selectedItem);
					if (continueStatus >= 0)
						this.productBatchModel.setContinueStatus(continueStatus);
					enableSaveButton();
				} else if (property.equals(COMPOUND_SOURCE)) {
					try {
						String source = CodeTableCache.getCache().getSourceCode((String) jComboBoxCompoundSource.getSelectedItem());
						if(productBatchModel.getRegInfo().getCompoundSource() == null || !productBatchModel.getRegInfo().getCompoundSource().equals(source)) {
							productBatchModel.getRegInfo().setCompoundSource(source);
							// If source changed, detail changed, so refresh it.
							this.comboSourceItemStateChanged();
							enableSaveButton();
						}
					} catch (CodeTableCacheException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (property.equals(SOURCE_DETAIL)) {
					try {
						String val = CodeTableCache.getCache().getSourceDetailCode((String) jComboBoxSourceDetail.getSelectedItem());
						if(productBatchModel.getRegInfo().getCompoundSourceDetail() == null || !productBatchModel.getRegInfo().getCompoundSourceDetail().equalsIgnoreCase(val)) {
							productBatchModel.getRegInfo().setCompoundSourceDetail(val);
							enableSaveButton();
						}
						//MasterController.getUser().setPreference(NotebookUser.PREF_LastSourceDetail, val);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (property.equals(COMPOUND_PROTECTION)) {
					productBatchModel.setProtectionCode(BatchAttributeComponentUtility.getCode((String) this.jComboBoxCompoundProtection.getSelectedItem()));
					enableSaveButton();
				} else if (property.equals(WELL_SOLVENT)) {
					if (this.well != null) {
						String selSolvCode = BatchAttributeComponentUtility.getWellSolventCodeFromDecr(jComboBoxWellSolvent.getSelectedItem().toString());
						if(well.getSolventCode() == null || !well.getSolventCode().equals(selSolvCode)) {
							well.setSolventCode(selSolvCode);
							enableSaveButton();
						}
					}
				} else if (property.equals(EXTERNAL_SUPPLIER)) {
					// Save done in dialog handler?
					enableSaveButton();
				} else if (property.equals(PURITY)) {
					// todo
					enableSaveButton();
				} else if (property.equals(COMPOUND_STATE)) {
					// Manipulate the code from the dropdownlist and update the code in the batch object
					String selectedCompoundState = "";
					if (jComboBoxCompoundState.getSelectedItem() != null)
						selectedCompoundState = jComboBoxCompoundState.getSelectedItem().toString();
					int index1 = selectedCompoundState.indexOf(" -");
					if (index1 > 0) {
						String compoundCode = selectedCompoundState.substring(0, index1);
						productBatchModel.setCompoundState(compoundCode);
					} else {
						productBatchModel.setCompoundState("");
					}
					enableSaveButton();
				} else if (property.equals(BATCH_COMMENTS)) {
					if(productBatchModel.getComments() == null || !productBatchModel.getComments().equals(this.jTextAreaBatchComments.getText())) {
					  if (this.jTextAreaBatchComments.getText().equals("")) {
					    productBatchModel.getRegInfo().setComments("");
					  }
					  else {
						  productBatchModel.setComments(this.jTextAreaBatchComments.getText());
					  }
						enableSaveButton();
					}
				} else if (property.equals(TUBE_BARCODE)) {
					if(well.getBarCode() == null ||!well.getBarCode().equals(jTextFieldTubeBarcode.getText())) {
						well.setBarCode(jTextFieldTubeBarcode.getText());
						enableSaveButton();
					}
				} else if (property.equals(VIAL_BARCODE)) {
					if(well.getBarCode() == null || !well.getBarCode().equals(jTextFieldVialBarcode.getText())) {
						well.setBarCode(jTextFieldVialBarcode.getText());
						enableSaveButton();
					}
				} else if (property.equals(HEALTH_HAZARDS)) {
					if(productBatchModel.getHazardComments() ==null ||!productBatchModel.getHazardComments().equals(this.jTextAreaHealthHazards.getText())) {
						productBatchModel.setHazardComments(this.jTextAreaHealthHazards.getText());
						enableSaveButton();
					}
				} else if (property.equals(HANDLING_PRECAUTIONS)) {
					if(productBatchModel.getHandlingComments() ==null ||!productBatchModel.getHandlingComments().equals(this.jTextAreaHandlingPrecautions.getText())) {
						productBatchModel.setHandlingComments(this.jTextAreaHandlingPrecautions.getText());
						enableSaveButton();
					}
				} else if (property.equals(STORAGE_INSTRUCTIONS)) {
					if(productBatchModel.getStorageComments() == null ||!productBatchModel.getStorageComments().equals(this.jTextAreaStorageInstructions.getText())) {
						productBatchModel.setStorageComments(this.jTextAreaStorageInstructions.getText());
						enableSaveButton();
					}
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
				    		enableSaveButton();
				    		updateSaltEquivs();
				    		this.populate();
						} catch (Exception e) {
							CeNErrorHandler.getInstance().logExceptionMsg(e);
						}
					}	    		
				} else if (property.equals(SALT_EQUIVALENT)) {
					try {
						double amount = saltEquivalentsView.getValue();
						if (BatchAttributeComponentUtility.setSaltEquiv(productBatchModel, new AmountModel(UnitType.SCALAR, amount))) {
							productBatchModel.setModelChanged(true);
						}
						productBatchModel.recalcAmounts();
						updateSaltEquivs();
						enableSaveButton();
						populate();
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
				} else if (property.equals(PRECURSOR_REACTANT_IDS)) {
					ArrayList<String> precursors = pageModel.processSingletonPrecursorsUpdate(jTextFieldPrecursorsValues.getText(), productBatchModel);
					productBatchModel.setReactantBatchKeys(precursors);
					populate();
					enableSaveButton();
				} else if (TEST_COMPOUND_BUTTON_LABEL.equals(property) || INTERMEDIATE_BUTTON_LABEL.equals(property)) {
                    String value = null;
                    if (testCompoundButton.isSelected())
                        value = "N";
                    if (intermediateButton.isSelected())
                        value = "U";
                    productBatchModel.getRegInfo().setIntermediateOrTest(value);
                    productBatchModel.setModified(true);
                    enableSaveButton();
                } else if (StringUtils.equals(property, VIRTUAL_COMPOUND_NUM)) {
                	String vcId = jTextFieldVirtualCompNumValue.getText();
                	UtilsDispatcher.getFormatter().formatCompoundNumber(vcId);
                	jTextFieldVirtualCompNumValue.setText(vcId);
                	productBatchModel.getCompound().setVirtualCompoundId(vcId);
                	productBatchModel.setModified(true);
                	enableSaveButton();
                }
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(e);
			} 
		}
		refreshToolTips();
		updateBatchTable();
		compoundCreationHandler.fireCompoundUpdated(new CompoundCreationEvent(this, productBatchModel, null));
		isLoading = false;
	}
	
	public void updateBatchTable() {
		PCeNTableView table = parentWindow.getCurrentBatchDetailsTableView();
		if (table != null) {
			int row = table.getSelectedRow();		
			((PCeNTableModel) table.getModel()).fireTableDataChanged();
			if (row != -1) {
				table.setRowSelectionInterval(row, row);
			}
		}
	}
		
	private void validatePropertyPreConditions(String property)	{
		if (isLoading || !isBatchEditable || this.productBatchModel == null)
			return;
		isLoading = true;
		if (property != null && property.length() > 0) {
			if (property.equals(AMOUNT_IN_WELL_TUBE_WEIGHT) || property.equals(AMOUNT_IN_WELL_TUBE_VOLUME)
					|| property.equals(AMOUNT_IN_WELL_TUBE_MOLES)) {
				if ((productBatchModel.getTotalWeight() == null || productBatchModel.getTotalWeight().GetValueInStdUnitsAsDouble() <= 0.0d)
					&& (productBatchModel.getTotalVolume() == null || productBatchModel.getTotalVolume().GetValueInStdUnitsAsDouble() <= 0.0d)) {
					totalAmountMadeWtView.requestFocus();
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Total amount made must be entered.");
				}
			} 
		}
		isLoading = false;
	}
	
	private String processString(String instr) 
	{
		if (instr == null)
			return "";
		else if (instr.equalsIgnoreCase("null"))
			return "";
		else return instr;
	}

	protected void comboSourceItemStateChanged() 
	{
		this.componentUtility.updateComboSourceDetail(this.jComboBoxCompoundSource, this.jComboBoxSourceDetail, true);
	}
	
	/**
	 *  Enable the save button when a text field or text area is changed.
	 */
	public class BatchDocumentListener implements DocumentListener 
	{
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
	
	class PropertyItemListener implements ChangeListener  {
		String property = "";
		public PropertyItemListener(String property) {
			this.property = property;
		}

		public void stateChanged(ChangeEvent arg0) {
			if (property != null && property.length() > 0)
				updateProperty(property);
		}
	}
	
	class PropertyFocusListener extends FocusAdapter {
		String property = "";
		public PropertyFocusListener(String property) {
			this.property = property;
		}

		public void focusGained(FocusEvent evt) {
			if (property != null && property.length() > 0)
				validatePropertyPreConditions(property);
			return;
		}

		public void focusLost(FocusEvent evt) {
			if (isChanging) {
				if (property != null && property.length() > 0)
					updateProperty(property);
			}
			isChanging = false;
		}
	}

	class AmountFocusListener extends FocusAdapter {
		String property = "";
		public AmountFocusListener(String property) {
			this.property = property;
		}

		public void focusGained(FocusEvent evt) {
			if (property != null && property.length() > 0)
				validatePropertyPreConditions(property);
			return;
		}
	}
	
	/**
	 * Make text field able to apply value by ENTER or TAB
	 */
	public class TextFieldKeyListener extends KeyAdapter {

		private String property;
		
		public TextFieldKeyListener(String property) {
			this.property = property;
		}

		public void keyTyped(KeyEvent e) {
			if (StringUtils.isNotBlank(property)) {
				char key = e.getKeyChar();
				if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_TAB) {
					if (isChanging) {
						updateProperty(property);
					}
					isChanging = false;
				}
			}
		}		
	}
	
	/**
	 * @param evt
	 */
	protected void showPurityInfoDialog(ActionEvent evt) {
		try {
			if (this.productBatchModel != null) {
				// Populate fields in the ProductBatch which is derived from the ProductBatchModel
				List<Properties> al_purities = CodeTableCache.getCache().getPurityDeterminedBy();
				// Show dialog
				PurityInfoJDialog.showGUI(MasterController.getGUIComponent(), this.productBatchModel, al_purities, jTextAreaPurity, pageModel);
				// Get updates and set the ProductBatchModel purity list from the ProductBatch purity list.
				List<PurityModel> newPurityList = this.productBatchModel.getAnalyticalPurityList();
				if (this.purityModelListChanged(newPurityList))
					this.updateProperty(PURITY);
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	protected void showExternalSupplierDialog(ActionEvent evt) {
		if (this.productBatchModel != null) {
			if (this.productBatchModel.getVendorInfo() == null) {
				productBatchModel.setVendorInfo(new ExternalSupplierModel());
			}
			ExtSuplInfoJDialog.showGUI(MasterController.getGUIComponent(), productBatchModel, this.componentUtility.getExternalSuppliersList(), this.jTextAreaExternalSupplier, pageModel);
			this.updateProperty(EXTERNAL_SUPPLIER);
		}
	}
	
	private void showHealthHazardsInfoDialog(ActionEvent e) {
		if (this.productBatchModel != null) {
			if (this.productBatchModel == null) {
				// no batch selected
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Please select a batch first.");
			} else {
				JDialogHealthHazInfo jDialogHealthHazInfo = new JDialogHealthHazInfo(MasterController.getGUIComponent(), pageModel);
				Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
				Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
				jDialogHealthHazInfo.setLocation(loc.x + (dim.width - jDialogHealthHazInfo.getSize().width) / 2, loc.y
						+ (dim.height - jDialogHealthHazInfo.getSize().height) / 2);
				jDialogHealthHazInfo.setHandlingList(this.componentUtility.getHandlingList());
				jDialogHealthHazInfo.setHazardList(this.componentUtility.getHazardsList());
				jDialogHealthHazInfo.setStorageList(this.componentUtility.getStorageList());
				jDialogHealthHazInfo.setHandlingMap(this.componentUtility.getHandlingMap());
				jDialogHealthHazInfo.setHazardMap(this.componentUtility.getHazardsMap());
				jDialogHealthHazInfo.setStorageMap(this.componentUtility.getStorageMap());
/*				try {
					// Convert from BatchModel to AbstractBatch
					this.associatedBatch = (ProductBatch) CeN11To12ConversionUtils.convertBatchModelToAbstractBatch(this.productBatchModel);
					this.associatedBatch.setRegInfo(CeN11To12ConversionUtils.convertRegInfoModelToRegInfo(this.productBatchModel.getRegInfo()));
					this.associatedBatch.setHazardComments(this.productBatchModel.getHazardComments());
					this.associatedBatch.setHandlingComments(this.productBatchModel.getHandlingComments());
					this.associatedBatch.setStorageComment(this.productBatchModel.getStorageComments());
				} catch (NumberFormatException e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(e1);
				} catch (Exception e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(e1);
				}*/
	
				jDialogHealthHazInfo.setSelectedBatch(productBatchModel);
				if (jDialogHealthHazInfo.getHealthHazInfo()) {
					updateDisplayOfHealthHazards(); // update
					enableSaveButton();
/*					try {
						// Convert from AbstractBatch to BatchModel
						this.productBatchModel.setRegInfo(CeN11To12ConversionUtils.convertRegInfoToRegInfoModel(this.associatedBatch.getRegInfo()));
						this.productBatchModel.setHazardComments(this.associatedBatch.getHazardComments());
						this.productBatchModel.setHandlingComments(this.associatedBatch.getHandlingComments());
						this.productBatchModel.setStorageComments(this.associatedBatch.getStorageComment());
					} catch (NumberFormatException e1) {
						CeNErrorHandler.getInstance().logExceptionMsg(e1);
					} catch (Exception e1) {
						CeNErrorHandler.getInstance().logExceptionMsg(e1);
					}
*/				}
				jDialogHealthHazInfo = null;
			}
		} else
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The batch is currently not editable.");
	}

	public void updateDisplayOfHealthHazards() {
		String hazardString = SingletonRegSubHandler.getBatchHazardString(productBatchModel);
		
		if (StringUtils.isNotBlank(hazardString)) {
			updateTextAndToolTip(jTextAreaHealthHazards, hazardString);
		} else {
			updateTextAndToolTip(jTextAreaHealthHazards, "No Hazards currently identified");
		}
		
		String handlingString = SingletonRegSubHandler.getBatchHandlingString(productBatchModel);
		
		if (StringUtils.isNotBlank(handlingString)) {
			updateTextAndToolTip(jTextAreaHandlingPrecautions, handlingString);
		} else {
			updateTextAndToolTip(jTextAreaHandlingPrecautions, "No Handling precautions");
		}
		
		String storageString = SingletonRegSubHandler.getBatchStorageString(productBatchModel);
		
		if (StringUtils.isNotBlank(storageString)) {
			updateTextAndToolTip(jTextAreaStorageInstructions, storageString);
		} else {
			updateTextAndToolTip(jTextAreaStorageInstructions, "No special storage required");
		}
	}
	
	private void updateTextAndToolTip(JTextComponent comp, String text) {
		if (comp != null && text != null) {
			comp.setText(text);
			comp.setToolTipText(text);
			comp.setCaretPosition(0);
		}
	}
	
	/**
	 * Process the list of product batch model type for VCRegistration
	 * 
	 * @param batchesToVCReg
	 */
	public void handleVCRegMultiple(List<ProductBatchModel> batchesToVCReg) {
		String registering = "Registering VC compounds...";

		// Ensure there is something to process
        if (checkEmptiness(batchesToVCReg)) return;

        // Do not enforce VnV testing for Conception Experiments.
		if( pageModel.isConceptionExperiment() == false) {
			// For any other experiment we are saying we have a valid VnV'd structure and an appropriate SIC
			for(ProductBatchModel batchModel : batchesToVCReg) {
				if (batchModel != null && !batchModel.isVnVPassedAndStereoisomerCodeValid()) {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
					                              "VnV and Uniquness check has to be performed for all selected compounds.\n" +
					                              "Note: a Stereoisomer Code of HSREG would also be considered an invalid state.",
					                              MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);				
					return;
				}
			}
		}

		if (compoundCreateInterface != null && compoundCreateInterface instanceof ConceptionCompoundInfoContainer) {
			((ConceptionCompoundInfoContainer) compoundCreateInterface).setRegisterCompoundButtonEnabled(false);
		}		

		int numberOfSuccessfulCompounds = 0;
		if (initializeVCRegAdapter()) {
			String progressStatus = registering;
			CeNJobProgressHandler.getInstance().addItem(progressStatus);
			for (ProductBatchModel batchModel : batchesToVCReg) {
				if (batchModel != null && batchModel.getRegInfo() != null && batchModel.getRegInfo().isVnVValid()) {
					String virtualCompoundId = batchModel.getCompound().getVirtualCompoundId();
					if(StringUtils.isBlank(virtualCompoundId)) {
						virtualCompoundId = "";
					}
					if(vcRegisterBatch(batchModel, false)) {
						numberOfSuccessfulCompounds++;
					}
				}
			}
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		}
		registerVCReport(numberOfSuccessfulCompounds, batchesToVCReg.size());
		
		if (compoundCreateInterface != null && compoundCreateInterface instanceof ConceptionCompoundInfoContainer) {
			((ConceptionCompoundInfoContainer) compoundCreateInterface).setRegisterCompoundButtonEnabled(true);
		}
	}

	/** 
	 * works on currently selected productBatchModel
	 */
	public void handleVCReg() { 
		if(initializeVCRegAdapter()) {
			vcRegisterBatch(productBatchModel, true);
		}
	}

	private boolean vcRegisterBatch(ProductBatchModel productBatchModel, boolean messageDialogAllowed) {
		if (productBatchModel != null) {
			boolean successful = true;
			try {
				byte[] sketch = productBatchModel.getCompound().getNativeSketch();
				ChemistryDelegate chemDel = new ChemistryDelegate();
				byte[] molFile = chemDel.convertChemistry(sketch, "", "MDL Molfile");
				StatusInfo info = adapter.registerVirtualCompound(new String(molFile), false, MasterController.getUser().getNTUserID());
				switch (info.getVcrStatus().getValue()) {
				case VcrStatus._FAILED:
				case VcrStatus._FAIL_ETL:
				case VcrStatus._FAIL_GCD:
					if (info.getMatched() == null || info.getMatched().length < 1) {
						log.debug("handleVCReg() - status: " + info.getVcrStatus().getValue());
						String errorMessage = "";
						for (String messageStr : info.getMessage()) {
							errorMessage += (messageStr + "\n");
						}
						log.debug("handleVCReg() - error message: " + errorMessage);
						if(messageDialogAllowed) {
							JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Registration failed:\n" + errorMessage, MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);
						}
						successful = false;
						break;
					} else {
						updateCompound(productBatchModel, info, messageDialogAllowed);
					}
					break;
					case VcrStatus._PASSED:
					case VcrStatus._PASS_ETL:
					case VcrStatus._PASS_GCD:
					case VcrStatus._PASS_LOAD:
					case VcrStatus._PASS_UC:
					case VcrStatus._PASS_VALIDATION:
					case VcrStatus._PASS_VGSD:
					case VcrStatus._PASS_VNV:
						updateCompound(productBatchModel, info, messageDialogAllowed);
						break;
				default:
					break;
				}
			} catch (Exception e) {
				log.error("Error while performing VCR: ", e);
				if(messageDialogAllowed)JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Error while performing VCR:\n" + e.getMessage(), MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);
				successful = false;
			}
			pageModel.getSummaryReactionStep().setModelChanged(true);
			populate();
			enableSaveButton();
			return successful;
		}
		return false;
	}

	private void registerVCReport(int idsOfSuccessfulCompounds, int totalNumberOfCompounds) {
		StringBuilder message = new StringBuilder();
		message.append(idsOfSuccessfulCompounds).append(" compound").append(idsOfSuccessfulCompounds == 1 ? "" : "s")
		   .append(" from ").append(totalNumberOfCompounds)
		   .append(" ").append(idsOfSuccessfulCompounds == 1 ? "was" : "were").append(" registered successfully.");
		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), message, MasterController.getGUIComponent().getTitle(), JOptionPane.INFORMATION_MESSAGE);
	}

    public void handleVnVMultiple(List<ProductBatchModel> batches) {
        if (checkEmptiness(batches))
            return;

        compoundCreateInterface.setVnvButtonEnabled(false);
        String progressStatus = "Performing VnV...";
        CeNJobProgressHandler.getInstance().addItem(progressStatus);
        for (ProductBatchModel batch : batches) {
            final String compoundRegistrationStatus = batch.getRegInfo().getCompoundRegistrationStatus();
            final ParentCompoundModel compound = batch.getCompound();
            final byte[] sketch = compound.getNativeSketch();
            if (CeNConstants.REGINFO_SUBMISION_PENDING.equals(compoundRegistrationStatus) ||
                    CeNConstants.REGINFO_SUBMISION_PASS.equals(compoundRegistrationStatus) ||
                    sketch == null) {
                continue;
            }

            BatchVnVInfoModel batchVnvInfo = null;
            try {
            	String vnvSDFile = StructureVnvDialog.getVnVSdFile(sketch);
                if (StringUtils.isNotBlank(vnvSDFile)) {
                    VnvDelegate vnvDelegate = new VnvDelegate();
                    batchVnvInfo = vnvDelegate.performVnV(vnvSDFile, "HSREG");
                    if (batchVnvInfo != null) {
                        if (batchVnvInfo.isPassed()) {
                        	StructureVnvDialog.updateBatchWithVnV(batch, batchVnvInfo);
                                compound.setStereoisomerCode(batchVnvInfo.getAssignedStereoIsomerCode());
                        }
                    }
                }
            } catch (Throwable e) {
                log.error("Error performing VnV", e);
            }
        }
        CeNJobProgressHandler.getInstance().removeItem(progressStatus);

        compoundCreateInterface.setVnvButtonEnabled(true);
    }

    private boolean checkEmptiness(List<ProductBatchModel> batches) {
        // Ensure there is something to process
        if(batches == null || batches.isEmpty()) {
            JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
                                          "No selected compounds.",
                                          MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private void updateCompound(ProductBatchModel model, StatusInfo info, boolean messageDialogAllowed) {
		if (StringUtils.isBlank(info.getCompoundId())) {
			model.getCompound().setVirtualCompoundId(info.getMatched()[0]);
		} else {
			model.getCompound().setVirtualCompoundId(info.getCompoundId());
		}
		model.getCompound().setMolWgt(Double.parseDouble(info.getMolweight()));
		model.getCompound().setMolFormula(info.getMolFormula());
		if(messageDialogAllowed) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Registration successful.", MasterController.getGUIComponent().getTitle(), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void handleVnv() {
		final BatchEditPanel editPanel = this;
		if (this.productBatchModel != null) {
			if (this.vnvDelegate == null) {
				try {
					this.vnvDelegate = new VnvDelegate();
				} catch(VnvException error) {
					JOptionPane.showMessageDialog(this, error.getMessage(), 
							"VnV Service Initialization Error", JOptionPane.ERROR_MESSAGE);
					return;
				} 
			}
			
			if (this.progressBarDialog == null)
				this.progressBarDialog = new ProgressBarDialog(MasterController.getGUIComponent());
				
			final SwingWorker worker = new SwingWorker() {
				BatchVnVInfoModel batchVnvInfo = null;
				
				public Object construct() {
					try {
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								progressBarDialog.setTitle("Performing VnV Check, Please Wait ...");
							}
						});
						batchVnvInfo = vnvDelegate.performVnV(productBatchModel,
						                                      MasterController.getUser().getCompoundManagementEmployeeId(), 
						                                      pageModel);
					} catch(VnvException error) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
								error.getMessage(), "VnV Execution Error", JOptionPane.ERROR_MESSAGE);
					}
					return batchVnvInfo;
				}
				
				public void finished() {
					progressBarDialog.setVisible(false);
					if (batchVnvInfo != null) {
						BatchVnVResultViewer  vnvResultViewer = 
							new BatchVnVResultViewer(MasterController.getGUIComponent(),batchVnvInfo);
						vnvResultViewer.setLocation(200, 200);
						vnvResultViewer.setVisible(true);
						if (batchVnvInfo.isPassed() && vnvResultViewer.OK_PRESSED) {
							NewNoveltyCheckUI noveltyUI = new NewNoveltyCheckUI(progressBarDialog);
							noveltyUI.SubmitForUniquenessCheck(productBatchModel,batchVnvInfo,editPanel);
							//progressBarDialog.setVisible(true);
//							System.out.println(batchVnvInfo.getAssignedStereoIsomerCode());
//							int count= jComboBoxStereoisomer.getItemCount();
//							for(int i=0;i<count;i++){
//								String isomerCode = (String)jComboBoxStereoisomer.getItemAt(i);
//								if(isomerCode.startsWith(batchVnvInfo.getAssignedStereoIsomerCode())){
//									jComboBoxStereoisomer.setSelectedIndex(i);
//									break;
//								}
//							}
						}
					}
				}
			};
			worker.start();
			progressBarDialog.setVisible(true);
		}
	}
	
	private boolean initializeVCRegAdapter() {
		log.debug("VCRegistration Adapter Start initialization...");
		boolean success = true;
		try {
			adapter = new RegistrationServiceDelegate();
		} catch(Exception error) {
			success = false;
			log.error("Failed to initialize VCRegistration Adapter", error);
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
			                              error.getMessage(), 
			                              "VC Registration Adapter Initialization Error", 
			                              JOptionPane.ERROR_MESSAGE);
		}
		log.debug("VCRegistration Adapter initialized");
		return success;
	}
	
	protected void showSolubilityInSolventsInfoDialog(ActionEvent e) 
	{
		if (this.productBatchModel != null) {
			if (this.productBatchModel == null) {
				// no batch selected
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Please select a batch first.");
			} else {
				EditSolubilityInSolventsDialog solubilityInSolventsDialog = new EditSolubilityInSolventsDialog(MasterController.getGUIComponent(), this.productBatchModel, pageModel);
				//solubilityInSolventsDialog.setSelectedBatch();
				Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
				Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
				solubilityInSolventsDialog.setLocation(loc.x + (dim.width - solubilityInSolventsDialog.getSize().width) / 2, loc.y
						+ (dim.height - solubilityInSolventsDialog.getSize().height) / 2);
				solubilityInSolventsDialog.setVisible(true);
				
				solubilityInSolventsDialog = null;
				this.populate();
			}
		} else
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The batch is currently not editable.");
	}

	protected void showResidualSolventsInfoDialog(ActionEvent e) 
	{
		if (this.productBatchModel != null) {
			if (this.productBatchModel == null) {
				// no batch selected
				JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "Please select a batch first.");
			} else {
				EditResidualSolventsDialog residualSolventsDialog = new EditResidualSolventsDialog(MasterController.getGUIComponent(), this.productBatchModel, pageModel);
				residualSolventsDialog.setVisible(true);
				residualSolventsDialog = null;
				this.populate();
			}
		} else
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The batch is currently not editable.");
	}
	
	private boolean purityModelListChanged(List newPurityModelList) {
		return true;
	}

	public void editingCanceled(ChangeEvent e) {
	}

	public void editingStopped(ChangeEvent e) {
		Object o = e.getSource();
		if (o instanceof PAmountComponent) {
			if (o == amountInWellWtView)
				this.updateProperty(AMOUNT_IN_WELL_TUBE_WEIGHT);  
			else if (o == amountInWellVolView)
				this.updateProperty(AMOUNT_IN_WELL_TUBE_VOLUME);  
			else if (o == amountInWellMolesView)
				this.updateProperty(AMOUNT_IN_WELL_TUBE_MOLES);
			else if (o == amountInVialWtView)
				this.updateProperty(AMOUNT_IN_VIAL_WEIGHT);  
			else if (o == totalAmountMadeMolesView)
				this.updateProperty(TOTAL_MOLES);  
			else if (o == totalAmountMadeWtView) 
				this.updateProperty(TOTAL_WEIGHT);
			else if (o == totalAmountMadeVolView )
				this.updateProperty(TOTAL_VOLUME);
			else if (o == this.saltEquivalentsView)
				this.updateProperty(SALT_EQUIVALENT);
		}
		else if (o == tubeVialTableConnector) {
		  this.populate();
	}
	}

/*	public void setBatchesAndPlatesMap(HashMap batchesAndPlatesMap) {
		this.batchesAndPlatesMap = batchesAndPlatesMap;
	}*/

	public void plateSelectionChanged(PlateSelectionChangedEvent event) {
		this.productBatchModel = null;
		this.populate();
	}
	
	protected boolean resetEditableFlag() {
		try{
			isBatchEditable = CommonUtils.getProductBatchModelEditableFlag(productBatchModel, pageModel);
		} catch(Exception e){
			log.error(e);
			isBatchEditable = false;
		}
		return isBatchEditable;
	}
	
	public boolean isBatchEditable() {
	  return isBatchEditable;
	}
	
	public void dispose() {
		try {
			amountComponentDispose(this.amountInWellMolesView);
			amountComponentDispose(this.amountInVialWtView);
			amountComponentDispose(this.amountInWellVolView);
			amountComponentDispose(this.amountInWellWtView);
			amountComponentDispose(this.amountRemainingVolView);
			amountComponentDispose(this.saltEquivalentsView);
			amountComponentDispose(this.totalAmountMadeMolesView);
			amountComponentDispose(this.totalAmountMadeVolView);
			amountComponentDispose(this.totalAmountMadeWtView);
//			if (this.batchesAndPlatesMap != null) {
//				this.batchesAndPlatesMap.clear();
//				this.batchesAndPlatesMap = null;
//			}	
		} catch (Exception e) {
			log.error("Error disposing BatchEditPanel: ", e);
		}
	}	
	
	private void amountComponentDispose(PAmountComponent ac) {
		if (ac != null) ac.dispose();
	}

	class JTextFieldLimit extends PlainDocument {
	    
		private static final long serialVersionUID = 2046043962603053091L;
		
		private int limit;
	    // optional uppercase conversion
	    private boolean toUppercase = false;
	    
	    JTextFieldLimit(int limit) {
	        super();
	        this.limit = limit;
	    }
	    
	    JTextFieldLimit(int limit, boolean upper) {
	        super();
	        this.limit = limit;
	        toUppercase = upper;
	    }
	    
	    public void insertString(int offset, String  str, AttributeSet attr)
	            throws BadLocationException {
	        if (str == null) return;
	        
	        if ((getLength() + str.length()) <= limit) {
	            if (toUppercase) str = str.toUpperCase();
	            super.insertString(offset, str, attr);
	        }
	    }
	}
	
	private void displayVnVContainer() {
		PCeNStructureVnVContainer.VnVPassedListener vnVPassedListener = new PCeNStructureVnVContainer.VnVPassedListener() {
			public void vnVPassed() {
				validateAndEnableVCRegButton();
			}
		};

		PCeNStructureVnVContainer compoundVnVContainer = new PCeNStructureVnVContainer(vnVPassedListener, MasterController.getGUIComponent(), true);
		
		compoundVnVContainer.setSelectedBatch(this.productBatchModel);
		compoundVnVContainer.setParentDialog(this.parentWindow);
		
        compoundVnVContainer.setCallback(new Runnable() {
            public void run() {
                updateProperty(STEREOISOMER);
                jTextAreaStructureCommentsValue.setText(processString(productBatchModel.getCompound().getStructureComments()));
            }
        });
        
        compoundVnVContainer.updateBatchDisplay();
		compoundVnVContainer.setSize(800, 530);
        compoundVnVContainer.setResizable(false);
        compoundVnVContainer.setLocationRelativeTo(MasterController.getGUIComponent());
        compoundVnVContainer.setVisible(true);
		
//		PCeNStructureVnVContainer compoundVnVContainer = new PCeNStructureVnVContainer(vnVPassedListener, MasterController.getGUIComponent(), true);
//		
//		compoundVnVContainer.setSelectedBatch(this.productBatchModel);
//		compoundVnVContainer.setParentDialog(this.parentWindow);
//		
	}
	
	public void addVialTubeContainerToBatch()
	{
		BatchSubmissionContainerInfoModel contModel =new BatchSubmissionContainerInfoModel();
		this.productBatchModel.getRegInfo().addSubmitContainer(contModel);
	}
	
	public void deleteVialTubeContainerInBatch(int row) {
	  if (row >= 0 && row < productBatchModel.getRegInfo().getSubmitContainerListSize()) {
	    this.productBatchModel.getRegInfo().removeSubmitContainer(row);
	    this.productBatchModel.setModelChanged(true);
	    this.pageModel.setModelChanged(true);
	  }
	}
	
    public void clearVialTubeContainerInBatch(int row) {
        productBatchModel.getRegInfo().clearSubmitContainerListRow(row);
    }

	public void setCompoundCreateInterface(CompoundCreateInterface compoundCreateInterface) {
		this.compoundCreateInterface = compoundCreateInterface;
	}
	
	public void clearVnV() {
		if (productBatchModel != null && productBatchModel.getRegInfo() != null) {
			this.productBatchModel.getRegInfo().setBatchVnVInfo(new BatchVnVInfoModel());
			if (productBatchModel.getCompound() != null) {
				productBatchModel.getCompound().setStereoisomerCode("");
				productBatchModel.getCompound().setStructureComments("");
				productBatchModel.getCompound().setVirtualCompoundId("");
			}
		}
		enableSaveButton();
	}
}
