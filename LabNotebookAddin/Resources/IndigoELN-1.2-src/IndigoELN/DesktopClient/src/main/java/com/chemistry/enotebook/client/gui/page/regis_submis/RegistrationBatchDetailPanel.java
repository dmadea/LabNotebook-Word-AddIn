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
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.client.gui.page.batch.*;
import com.chemistry.enotebook.client.gui.page.experiment.table.PAmountComponent;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNUtilObject;
import com.chemistry.enotebook.client.gui.page.experiment.table.StoicConstants;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.batch.BatchResidualSolventModel;
import com.chemistry.enotebook.domain.batch.BatchSolubilitySolventModel;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.ProductBatch;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import com.chemistry.viewer.ChemistryViewer;
import com.common.chemistry.codetable.CodeTableCache;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Iterator;
import java.util.List;

public class RegistrationBatchDetailPanel extends javax.swing.JPanel implements BatchSelectionListener, AmountEditListener {

	private static final long serialVersionUID = 5894431536268959767L;

	private static final Log log = LogFactory.getLog(RegistrationBatchDetailPanel.class);

	public static final Font BOLD_LABEL_FONT = new java.awt.Font("Arial", Font.BOLD, 12);
	public static final Font PLAIN_LABEL_FONT = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font EDIT_FONT_SMALL = new java.awt.Font("Arial", Font.PLAIN, 11);
	public static final Font EDIT_FONT_LARGE = new java.awt.Font("Arial", Font.PLAIN, 12);
	public static final Font COMBO_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	public static final Font BUTTON_FONT = new java.awt.Font("Arial", Font.BOLD, 11);
	private static final int NUM_ROWS = 33; // 23;
	//private static final int BATCH_WT_FIXED_FIGS = 6;

	// for business implement
	private NotebookPage nbPage;
	private ProductBatchModel productBatchModel;  // vb
	//private HashMap productBatchAttributes = new HashMap();
	//private int currentWellPosition = 0;
	private PlateWell well = null;
	private NotebookPageModel pageModel = null;
	private BatchAttributeComponentUtility componentUtility = null;

	// Attribute name constants
	private static final String PRODUCT_PLATE = "Plate Barcode";
	private static final String WELL = "Well";
	//private static final String SEQUENCE = "Sequence";
	private static final String NOTEBOOK_BATCH_NUM = "Notebook Batch #";
	private static final String STATUS = "Status";
	private static final String CONVERSATIONAL_BATCH_NUM = "Conversational Batch #";
	private static final String VIRTUAL_COMPOUND_NUM = PCeNTableView.VIRTUAL_COMP_NUM;
	private static final String STEREOISOMER = "Stereoisomer";
	private static final String COMPOUND_SOURCE = "Compound Source";
	private static final String SOURCE_DETAIL = "Source Detail";
	private static final String BATCH_OWNER = "Batch Owner";
	private static final String SYNTHESIZED_BY = "Synthesized By";
	private static final String CALCULATED_BATCH_MW = "Calculated Batch MW";
	private static final String CALCULATED_PARENT_MW = "Calculated Parent MW";
	private static final String CALCULATED_BATCH_MF = "Calculated Batch MF";
	private static final String BATCH_COMMENTS = "Batch Comments";
	private static final String AMOUNT_REMAINING = "Amount Remaining";
	private static final String TOTAL_AMOUNT_MADE = "Total Amount Made";
	private static final String TOTAL_VOLUME_MADE = "Total Volume Made";
	private static final String AMOUNT_IN_WELL_WT = "Amount in Well (Weight)";
	private static final String AMOUNT_IN_WELL_VOL = "Amount in Well (Volume)";
	private static final String AMOUNT_IN_TUBE_WT = "Amount in Tube (Weight)";
	private static final String AMOUNT_IN_TUBE_VOL = "Amount in Tube (Volume)";
	private static final String AMOUNT_IN_VIAL = "Amount in Vial";
	private static final String WELL_MOLARITY = "Well Molrity";
	private static final String TUBE_MOLARITY = "Tube Molrity";
	private static final String TUBE_BARCODE = "Tube Barcode";
	private static final String VIAL_BARCODE = "Vial Barcode";
	private static final String EXTERNAL_SUPPLIER = "External Supplier";
	private static final String PURITY = "Purity";
	private static final String RESIDUAL_SOLVENTS = "Residual Solvents";
	private static final String SOLUBILITY_IN_SOLVENTS = "Solubility in Solvents";
	private static final String PRECURSOR_REACTANT_IDS = "Precursor/Reactant IDs";
	private static final String SALT_CODE_AND_NAME = "Salt Code & Name";
	private static final String SALT_EQUIVALENT = "Salt Equivalent";
    private static final String COMPOUND_IS = "Compound Is";
	//private static final String SUBMISSION_PARAMETERS = "Submission Parameters";
	private static final String REGISTERED = "Registered";
	//private static final String SUBMITTED_TO_PURIFICATION = "Submitted to Purification";
	private static final String SUBMITTED_TO_QQC = "Submitted to Quantitative Quality Control";
	//private static final String SUBMITTED_TO_SCREEN_PANELS = "Submitted to Screen Panels";

	// Flags
	private boolean isEditable = true;
	private boolean isLoading = false;
	private boolean isChanging = false;
	private boolean isParallel = true;

	// Plate and well
	private JLabel jLabelProductPlate;
	private JLabel jLabelProductPlateName;
	private JLabel jLabelWell;
	private JLabel jLabelWellName;
	//private JLabel jLabelWellSequence;
	//private JLabel jLabelWellSequenceValue;
	// Noteook batch number
	private JLabel jLabelNbkBatchNum;
	private JTextField jLabelNbkBatchNumValue;
	// Status
	private JLabel jLabelStatus;
	private JLabel jLabelStatusValue;
	// Conversion batch number
	private JLabel jLabelConvBatchNum;
	private JTextField jLabelConvBatchNumValue;
	// Virtual compound number
	private JLabel jLabelVirtualCompNum;
	private JLabel jLabelVirtualCompNumValue;
	// Stereoisomer
	private JLabel jLabelStereoisomer;
	private JLabel jLabelStereoisomerValue;
	// Compound source
	private JLabel jLabelCompoundSource;
	private JLabel jLabelCompoundSourceValue;
	// Souce detail
	private JLabel jLabelSourceDetail;
	private JLabel jLabelSourceDetailValue;
	// Batch owner
	private JLabel jLabelBatchOwner;
	private JLabel jLabelBatchOwnerValue;
	// Sythesized by
	private JLabel jLabelSynthesizedBy;
	private JLabel jLabelSynthesizedByValue;
	// Calculated batch MW
	private JLabel jLabelCalcBatchMW;
	private JLabel jLabelCalcBatchMWValue;
	// Calculated parent MW
	private JLabel jLabelCalcParentMW;
	private JLabel jLabelCalcParentMWValue;
	// Calculated batch MF
	private JLabel jLabelCalcBatchMF;
	private JLabel jLabelCalcBatchMFValue;
	// Salt code
	private JLabel jLabelSaltCode;
	private JLabel jLabelSaltCodeValue;
	// Salt equivalent
	private JLabel jLabelSaltEquivalents;
	private JLabel jLabelSaltEquivalentsValue;

	// Total amount made
	private JLabel jLabelTotVolume;
	private JLabel jLabelTotWeight;
	private PAmountComponent totalAmountMadeWtView;
	private PAmountComponent totalAmountMadeVolView;
	private PAmountComponent totalAmountMadeMolesView;
	// Amount remaining
	private PAmountComponent amountRemainingWtView;
	private PAmountComponent amountRemainingVolView;
	// Amount in well
	private JLabel jLabelAmountInWell;
	private PAmountComponent amountInWellWtView;
	private PAmountComponent amountInWellVolView;
	private PAmountComponent amountInWellMolesView;
	// Amount in vial
	private PAmountComponent amountInVialWtView;

	private JLabel jLabelTotAmountMade;
	//private PAmountComponent totAmountMadeView;
	// Total volume made
	private JLabel jLabelTotVolumeMade;
	//private PAmountComponent totVolumeMadeView;
	// Amount in well
	private JLabel jLabelAmountInWellWt;
	private JLabel jLabelAmountInWellVol;
	private PAmountComponent amountInWellViewMass;
	private PAmountComponent amountInWellViewVolume;
	private AmountModel amountInWellModelMass;
	private AmountModel amountInWellModelVolume;
	// Amount in Tube
/*	private JLabel jLabelAmountInTubeWt;
	private JLabel jLabelAmountInTubeVol;
*/	//private PAmountComponent amountInTubeViewMass;
	//private PAmountComponent amountInTubeViewVolume;
	private AmountModel amountInTubeModelMass;
	private AmountModel amountInTubeModelVolume;
	// Amount in Vial
	private JLabel jLabelAmountInVial;
	private PAmountComponent amountInVialViewMass;
	private PAmountComponent amountInVialViewVolume;
	private AmountModel amountInVialModelMass;
	private AmountModel amountInVialModelVolume;
	//Well & Tube Molarities
	private JLabel jLabelWellMolarity;
	//private PAmountComponent wellMolarityComponent;
	private JLabel jLabelTubeMolarity;
	private PAmountComponent tubeMolarityComponent;
	// Tube barcode
  private JLabel jLabelTubeBarcode;
  private JLabel jLabelTubeBarcodeValue;
	// Vial barcode
	private JLabel jLabelVialBarcode;
	private JTextField jTextFieldVialBarcodeValue;
	// Precursor/Reactant ids
	private JLabel jLabelPrecursors;
	private JLabel jLabelPrecursorsValues;
	// Purity
	private JLabel jLabelPurity;
	private JLabel jLabelPurityValue;
	// Residual solvents
	private JLabel jLabelResidualSolvents;
	private JLabel jLabelResidualSolventsValue;
	// Solubility in solvents
	private JLabel jLabelSolubilityInSolvents;
	private JLabel jLabelSolubilityInSolventsValue;
  //Well solvent
  private JLabel jLabelWellSolvent;
  private JLabel jLabelWellSolventValue;
	// Submission parameters
/*	private JLabel jLabelSubmissionParameters;
	private JButton jButtonSubmissionParameters;
*/	// Amount remaining
	private JLabel jLabelAmountRemaining;
	private JLabel jLabelAmountRemainingValue;

    private JLabel jLabelCompoundIs;
    private JLabel jLabelCompoundIsValue;

	// Batch comments
	private JLabel jLabelBatchComments;
	private JTextField vialBarcode;
	private JTextArea jTextAreaBatchCommentsValue;
	private JScrollPane jScrollPaneBatchComments;
	private ProductPlate productPlate;

	private boolean isPseudoProductPlate = false;

//	private ChemistryPanel chime = new ChemistryPanel(); // vb 7/21 make this an instance var
	
	private final ChemistryViewer chemistryViewer = new ChemistryViewer(MasterController.getGUIComponent().getTitle(), "");
	private final JPanel chemistryViewerPanel = new JPanel(new BorderLayout());
	
	private Object lastEventObj;
	//private HashMap batchesAndPlatesMap;
/*	private Tube tube;
	private Vial vial;
		// vb 12/6

	/**
	 * Create a new BatchEditPanel -- This should
	 * only happen once when an experiment loads.
	 */
	public RegistrationBatchDetailPanel(NotebookPageModel pageModel, boolean isParallel) {
		this.pageModel = pageModel;
		this.isParallel = isParallel;
		this.componentUtility = BatchAttributeComponentUtility.getInstance();
		this.initGui();
		this.populate();
		this.setLayout(new FormLayout("left:5dlu, pref", "pref"));
		CellConstraints cc = new CellConstraints();
		this.add(this.getPanel(), cc.xy(2, 1));
	}

	/**
	 * Batch selection has changed.  Recreate the panel and refresh the parent's panel.
	 * @param event contains the PlateWell object
	 */
	public void batchSelectionChanged(BatchSelectionEvent event) {
		lastEventObj = event.getSubObject();
		if (lastEventObj instanceof CompoundContainer) {
			this.well = (PlateWell) lastEventObj;
			this.productBatchModel = (ProductBatchModel) well.getBatch();
			productPlate = (ProductPlate) well.getPlate();
			if (productPlate instanceof PseudoProductPlate)
				isPseudoProductPlate = true;
			else
				isPseudoProductPlate = false;

/*			else
				this.tube = (Tube) obj; //Vial will be assigned in the next step.
			this.productBatchModel = (ProductBatchModel) compoundContainer.getBatch();
			if (productPlate == null)

				if (this.well.getPlate() != null) {
					productPlate = (ProductPlate) this.well.getPlate();
					String plateBarCode = this.well.getPlate().getPlateBarCode();
					if (plateBarCode != null)  // vb 8/22 well create earlier than this date won't have a plate reference
						this.jLabelProductPlateName.setText(plateBarCode);
				} else
					this.jLabelProductPlateName.setText("");

/*			if (productPlate instanceof PseudoProductPlate)
			{
				vial = ((PseudoProductPlate)productPlate).getVialForBatch(productBatchModel);
				// This is a non-plated batch. No more wells are needed here.
				this.well = null;
			}
			else
			{
				tube = null;
				vial = null;
			}
*/		} else if (lastEventObj instanceof ProductBatchModel) {
			this.productBatchModel = (ProductBatchModel) lastEventObj;
			if (isParallel)
			{
				//this.productPlate = (ProductPlate) pageModel.getAllProductBatchesAndPlatesMap(false).get(productBatchModel);
				this.productPlate = null; // For Products view it is not required to show the plate details as there may be multiple plates for a batch exists.
				if (productPlate instanceof PseudoProductPlate)
					isPseudoProductPlate = true;
				else
					isPseudoProductPlate = false;
			}else
			{
				isPseudoProductPlate = true;
			}
			well = null;
			if (productPlate != null)
			{
				List wellsList = productPlate.getPlateWellsforBatch(productBatchModel);
				if (wellsList.size() > 0)
					well = (PlateWell) wellsList.get(0);
			}
		} else if (lastEventObj instanceof ParallelCeNUtilObject) {
			ParallelCeNUtilObject utilObj = (ParallelCeNUtilObject) lastEventObj;
			this.productBatchModel = (ProductBatchModel) utilObj.getBatch();
		} else {
			if (lastEventObj == null)
			{
				productBatchModel = null;
				well = null;
				productPlate = null;
				populate();	// To clear the values of fields and set them disabled.
			}
			return;
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
	private void initGui() {

		chemistryViewerPanel.setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new Color(0, 0, 0)));
		chemistryViewerPanel.add(chemistryViewer, BorderLayout.CENTER);
		chemistryViewer.setEditorType(MasterController.getGuiController().getDrawingTool());
		chemistryViewer.setReadOnly(true);
		
		if (isParallel) {
			jLabelProductPlate = makeCeNLabel(PRODUCT_PLATE);
			jLabelProductPlateName = makeLabel("", PLAIN_LABEL_FONT);

			jLabelWell = makeCeNLabel(WELL);
			jLabelWellName = makeLabel("", PLAIN_LABEL_FONT);

			//jLabelWellSequence = makeCeNLabel(SEQUENCE, PLAIN_LABEL_FONT);
			//jLabelWellSequenceValue = makeLabel("", PLAIN_LABEL_FONT);

			jLabelTubeBarcode = makeCeNLabel(TUBE_BARCODE);
			jLabelVialBarcode = makeCeNLabel(VIAL_BARCODE);
			jTextFieldVialBarcodeValue = makeTextField("", 12, VIAL_BARCODE);

			//wellMolarityComponent = makePAmountComponent();
			//tubeMolarityComponent = makePAmountComponent();
		} else { // Singleton

		}

		jLabelNbkBatchNum = makeCeNLabel(NOTEBOOK_BATCH_NUM);
		jLabelNbkBatchNumValue = makeTextFieldLabel("", PLAIN_LABEL_FONT, 12);

		jLabelStatus = makeCeNLabel(STATUS);
		jLabelStatusValue = makeLabel("", PLAIN_LABEL_FONT);

		jLabelConvBatchNum = makeCeNLabel(CONVERSATIONAL_BATCH_NUM);
		jLabelConvBatchNumValue = makeTextFieldLabel("", PLAIN_LABEL_FONT, 12);

		jLabelVirtualCompNum = makeCeNLabel(VIRTUAL_COMPOUND_NUM);
		jLabelVirtualCompNumValue = makeLabel("", PLAIN_LABEL_FONT);

		jLabelStereoisomer = makeCeNLabel(STEREOISOMER);
		jLabelStereoisomerValue = makeLabel("", PLAIN_LABEL_FONT);

		// Compound source and source detail
		jLabelCompoundSource = makeCeNLabel(COMPOUND_SOURCE);
		jLabelCompoundSourceValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelSourceDetail = makeCeNLabel(SOURCE_DETAIL);
		jLabelSourceDetailValue = makeLabel("", PLAIN_LABEL_FONT);

		// Batch owner and synthesized by
		jLabelBatchOwner = makeCeNLabel(BATCH_OWNER);
		jLabelBatchOwnerValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelSynthesizedBy = makeCeNLabel(SYNTHESIZED_BY);
		jLabelSynthesizedByValue = makeLabel("", PLAIN_LABEL_FONT);

		// Calculated batch MW and MF
		jLabelCalcBatchMW = makeCeNLabel(CALCULATED_BATCH_MW);
		jLabelCalcBatchMWValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelCalcParentMW = makeCeNLabel(CALCULATED_PARENT_MW);
		jLabelCalcParentMWValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelCalcBatchMF = makeCeNLabel(CALCULATED_BATCH_MF);
		jLabelCalcBatchMFValue = makeLabel("", PLAIN_LABEL_FONT);

		// Salt code and equivalents
		jLabelSaltCode = makeCeNLabel(SALT_CODE_AND_NAME);
		jLabelSaltCodeValue = makeLabel("", PLAIN_LABEL_FONT);
		jLabelSaltEquivalents = makeCeNLabel(SALT_EQUIVALENT);
		jLabelSaltEquivalentsValue = makeLabel("", PLAIN_LABEL_FONT);

		// Total amounts
		totalAmountMadeWtView = new PAmountComponent();
		this.totalAmountMadeWtView.setEditable(false);
		totalAmountMadeVolView = new PAmountComponent();
		this.totalAmountMadeVolView.setEditable(false);
		totalAmountMadeMolesView = new PAmountComponent(new AmountModel(UnitType.MOLES));
		this.totalAmountMadeMolesView.setEditable(false);
		// Remaining amounts
		this.amountRemainingWtView = new PAmountComponent();
		this.amountRemainingWtView.setEditable(false);
		this.amountRemainingVolView = new PAmountComponent();
		this.amountRemainingVolView.setEditable(false);
		// Amount in well
		amountInWellWtView = new PAmountComponent();
		amountInWellVolView = new PAmountComponent();
		amountInWellMolesView = new PAmountComponent(new AmountModel(UnitType.MOLES));
		// Amount in vial
		amountInVialWtView = new PAmountComponent();

		// Total amount made
		jLabelTotAmountMade = makeCeNLabel(TOTAL_AMOUNT_MADE);
//		totAmountMadeView = this.makePAmountComponent();
//		totAmountMadeView.setEditable(false);
		// Total amount made
		jLabelTotVolumeMade = makeCeNLabel(TOTAL_VOLUME_MADE);
//		totVolumeMadeView = this.makePAmountComponent();
//		totVolumeMadeView.setEditable(false);
		// Amount in well
		jLabelAmountInWellWt = makeCeNLabel(AMOUNT_IN_WELL_WT);
		jLabelAmountInWellVol = makeCeNLabel(AMOUNT_IN_WELL_VOL);
		amountInWellViewMass = this.makePAmountComponent();
		amountInWellViewVolume = this.makePAmountComponent();
		// Amount in Vial
		jLabelAmountInVial = makeCeNLabel(AMOUNT_IN_VIAL);
		jLabelWellMolarity = makeCeNLabel(WELL_MOLARITY);
		jLabelTubeMolarity = makeCeNLabel(TUBE_MOLARITY);
		amountInVialViewMass = this.makePAmountComponent();
		// Amount remaining
		jLabelAmountRemaining = makeCeNLabel(AMOUNT_REMAINING);
		jLabelAmountRemainingValue = makeLabel("", PLAIN_LABEL_FONT);

		// Precursor/Reactant ids
		jLabelPrecursors = makeCeNLabel(PRECURSOR_REACTANT_IDS);
		jLabelPrecursorsValues = makeLabel("", PLAIN_LABEL_FONT);
		// Purity
		jLabelPurity = makeCeNLabel(PURITY);
		jLabelPurityValue = makeLabel("", PLAIN_LABEL_FONT);

        jLabelCompoundIs = makeCeNLabel(COMPOUND_IS);
        jLabelCompoundIsValue = makeLabel("", PLAIN_LABEL_FONT);

		// Residual solvents
		jLabelResidualSolvents = makeCeNLabel(RESIDUAL_SOLVENTS);
		jLabelResidualSolventsValue = makeLabel("", PLAIN_LABEL_FONT);
		// Solubility in solvents
		jLabelSolubilityInSolvents = makeCeNLabel(SOLUBILITY_IN_SOLVENTS);
		jLabelSolubilityInSolventsValue = makeLabel("", PLAIN_LABEL_FONT);
		// Steroisomer
		jLabelStereoisomer = makeCeNLabel(STEREOISOMER);
		jLabelStereoisomerValue = makeLabel("", PLAIN_LABEL_FONT);
		// Submission parameters
		//jLabelSubmissionParameters = makeCeNLabel(SUBMISSION_PARAMETERS, PLAIN_LABEL_FONT);
		//jButtonSubmissionParameters = new JButton("Purification");
/*		jButtonSubmissionParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// todo...
				// load the submission parameters dialog
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Functionality is under Development.");
			}
		});
*/		// Batch comments
		jLabelBatchComments = makeCeNLabel(BATCH_COMMENTS);
		jTextAreaBatchCommentsValue = makeTextArea(BATCH_COMMENTS, true);
		jTextAreaBatchCommentsValue.setEditable(false);
		jScrollPaneBatchComments = this.makeScrollPane(jTextAreaBatchCommentsValue);
		vialBarcode = new JTextField();
		vialBarcode.setEditable(false);

    //well solvent
    jLabelWellSolvent = makeCeNLabel(BatchEditPanel.WELL_SOLVENT);
    jLabelWellSolventValue = makeLabel("", PLAIN_LABEL_FONT);
    //Tube barcode
    jLabelTubeBarcode = makeCeNLabel(BatchEditPanel.TUBE_BARCODE);
    jLabelTubeBarcodeValue = makeLabel("", PLAIN_LABEL_FONT);
	}

	/**
	 * method populates the batchEditPanel Pass null object to blank the values.
	 *
	 */
	public void populate() {
		try {
			if (isLoading)
				return;
			isLoading = true;
			if (this.productBatchModel == null) {
				isEditable = false;
				this.clearComponents();
				this.disableAllComponents();
				this.isLoading = false;
			} else {
				isEditable = true;
				if (isParallel) {
					if (!isPseudoProductPlate)
					{
						if (this.well != null)
						{
							productPlate = (ProductPlate) this.well.getPlate();
							String plateBarCode = this.well.getPlate().getPlateBarCode();
							this.jLabelProductPlateName.setText(plateBarCode);
						}
					}
					else
					{
						this.jLabelProductPlateName.setText("");
					}
					if (this.well != null)
						this.jLabelWellName.setText(this.well.getWellNumber());
				}

				jLabelNbkBatchNumValue.setText(this.productBatchModel.getBatchNumber().getBatchNumber());
				jLabelVirtualCompNumValue.setText(this.productBatchModel.getCompound().getVirtualCompoundId());
				jLabelVirtualCompNumValue.setToolTipText(jLabelVirtualCompNumValue.getText());

				//batch owner
				String name = MasterController.getGuiController().getUsersFullName(productBatchModel.getOwner());
				jLabelBatchOwnerValue.setText(name);

				//synthesized by
				name = MasterController.getGuiController().getUsersFullName(productBatchModel.getSynthesizedBy());
				jLabelSynthesizedByValue.setText(name);

				//jLabelBatchOwnerValue.setText(this.pageModel.getBatchOwner());
				//jLabelSynthesizedByValue.setText(this.pageModel.getBatchCreator());

				// If there is a structure show molecular weight otherwise don't
				if (this.productBatchModel.getCompound().getStringSketchAsString().length() > 0) {
					jLabelCalcBatchMWValue.setText(PCeNRegistrationProductsTableModelConnector.formatWeight(productBatchModel.getMolecularWeightAmount().doubleValue()));
					this.jLabelCalcBatchMW.setText(CALCULATED_BATCH_MW);
				} else {
					this.jLabelCalcBatchMW.setText("");
					this.jLabelCalcBatchMWValue.setText("");
				}
				this.jLabelCalcParentMWValue.setText(PCeNRegistrationProductsTableModelConnector.formatWeight(
                        productBatchModel.getCompound().getMolWgt()));

				//for Beta release disable all the Volume components
				if (lastEventObj instanceof CompoundContainer && well != null)
				{
					if (!isPseudoProductPlate)
					{
						this.amountInWellWtView.setValue(well.getContainedWeightAmount());
						//wellMolarityComponent.setValue(well.getMolarity());
						//amountInWellModelVolume.setValue(well.getContainedAmountInVolume().GetValueInStdUnitsAsDouble() / well.getMolarity().GetValueInStdUnitsAsDouble());
						this.amountInWellVolView.setValue(well.getContainedVolumeAmount());
						////this.jLabelTotAmountMadeValue.setText("" + this.productBatchModel.getTotalWeight().getValue());
						//There are chances that Plate may be replaced by Rack.
						this.jTextFieldVialBarcodeValue.setEnabled(false);
						amountInWellViewVolume.setEnabled(false);

					  //well solvent
		        this.jLabelWellSolventValue.setText(BatchAttributeComponentUtility.getWellSolventDecrFromCode(well.getSolventCode()));
			      //Tube barcode
		        this.jLabelTubeBarcodeValue.setText(well.getBarCode());
					}
					else
					{
						this.amountInVialWtView.setValue(well.getContainedWeightAmount());
						////this.jLabelTotAmountMadeValue.setText("" + this.productBatchModel.getWeightAmount().getValue());
						jTextFieldVialBarcodeValue.setEnabled(true);
						jTextFieldVialBarcodeValue.setText(well.getBarCode());
					}
				}
				// Salt code and equivalent
				if (productBatchModel.getSaltForm() == null || productBatchModel.getSaltForm().getCode() == null)
					this.jLabelSaltCodeValue.setText("");
				else
					this.jLabelSaltCodeValue.setText(this.componentUtility.getSaltDescFromCode(productBatchModel.getSaltForm().getCode()));
				this.jLabelSaltCodeValue.setToolTipText(this.jLabelSaltCodeValue.getText());
				this.jLabelSaltEquivalentsValue.setText("" + productBatchModel.getSaltEquivs());
				// Stereoisomer
				if (productBatchModel.getCompound().getStereoisomerCode().equalsIgnoreCase("null"))
					this.jLabelStereoisomerValue.setText("");
				else
					this.jLabelStereoisomerValue.setText(this.componentUtility.getStereoisomerDescriptionFromCode(productBatchModel.getCompound().getStereoisomerCode()));
				this.jLabelStereoisomerValue.setToolTipText(this.jLabelStereoisomerValue.getText());
				// Get the selectivity and continue status
				this.jLabelStatusValue.setText(ProductBatchStatusMapper.getInstance().getCombinedProductBatchStatus(this.productBatchModel));

				// Set the conversational batch number
				jLabelConvBatchNumValue.setText(productBatchModel.getRegInfo().getConversationalBatchNumber());

				// Precursors
				StringBuffer buff = new StringBuffer();
				if (isParallel) {
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
				} else {
					List<ReactionStepModel> steps = pageModel.getReactionSteps();
					for (ReactionStepModel step : steps) {
						BatchesList stoicBatches = step.getStoicBatchesList();
						List<BatchModel> batches = stoicBatches.getBatchModels();
						for (BatchModel batch : batches) {
							if (batch.getBatchType() != null && batch.getCompound() != null && batch.getBatchType().equals(BatchType.REACTANT)) {
								String regNumer = batch.getCompound().getRegNumber();
								//TODO workaround for CENSTR
								if (StringUtils.isNotBlank(regNumer) && !regNumer.startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
									buff.append("  ").append(regNumer);
								}
							}
						}
					}
				}
				this.jLabelPrecursorsValues.setText(buff.toString());
				this.jLabelPrecursorsValues.setToolTipText(buff.toString());

				// Get the purity list
				List purityModels = this.productBatchModel.getAnalyticalPurityList();
				for (Iterator it = purityModels.iterator(); it.hasNext();) {
					PurityModel purityModel = (PurityModel) it.next();
					if (purityModel.isRepresentativePurity()) {
						this.jLabelPurityValue.setText(purityModel.toString());
					}
				}
				this.jLabelPurityValue.setToolTipText(PurityModel.toToolTipString(purityModels));

				String intermediateOrTest = productBatchModel.getRegInfo().getIntermediateOrTest();
                this.jLabelCompoundIsValue.setText(StringUtils.equals(intermediateOrTest, "N") ? BatchEditPanel.TEST_COMPOUND_BUTTON_LABEL : BatchEditPanel.INTERMEDIATE_BUTTON_LABEL);

				this.jTextAreaBatchCommentsValue.setText(this.processString(productBatchModel.getComments()));
				this.jTextAreaBatchCommentsValue.setCaretPosition(0);
				this.jLabelCalcBatchMFValue.setText(productBatchModel.getMolecularFormula());
				// Compound source and detail
				//this.jLabelCompoundSourceValue.setText(productBatchModel.getRegInfo().getCompoundSource());
				this.jLabelCompoundSourceValue.setText(CodeTableCache.getCache().getSourceDescription(productBatchModel.getRegInfo().getCompoundSource()));
				this.jLabelSourceDetailValue.setText(CodeTableCache.getCache().getSourceDetailDescription(productBatchModel.getRegInfo().getCompoundSourceDetail()));
				// residual solvents
				List<BatchResidualSolventModel> residualSolventList = this.productBatchModel.getRegInfo().getResidualSolventList();
				if (residualSolventList.size() != 0) {
					String residualString = new String();
					for (int i = 0; i < residualSolventList.size(); i++) {
						BatchResidualSolventModel residualSolventVO = (BatchResidualSolventModel) residualSolventList.get(i);
						if (residualString.length() > 0)
							residualString += ", ";
						residualString += residualSolventVO.getEqOfSolvent() + " mols of " + residualSolventVO.getResidualDescription();
					}
					jLabelResidualSolventsValue.setText(residualString);
					jLabelResidualSolventsValue.setToolTipText(jLabelResidualSolventsValue.getText());
				} else {
					jLabelResidualSolventsValue.setText("-none-");
					jLabelResidualSolventsValue.setToolTipText(null);
				}
				// solubility in solvents
				List<BatchSolubilitySolventModel> solubilitySolventList = this.productBatchModel.getRegInfo().getSolubilitySolventList();
				if (solubilitySolventList.size() != 0) {
					String solubilityString = new String();
					for (int i = 0; i < solubilitySolventList.size(); i++) {
						BatchSolubilitySolventModel solubilitySolventVO = (BatchSolubilitySolventModel) solubilitySolventList.get(i);
						if (solubilitySolventVO.isQuantitative()) {
							if (solubilityString.length() > 0)
								solubilityString += ", ";
							solubilityString += solubilitySolventVO.getOperator() + " " + solubilitySolventVO.getSolubilityValue() + " "
									+ solubilitySolventVO.getSolubilityUnit() + " in " + solubilitySolventVO.getCodeAndName();
						} else {
							if (solubilityString.length() > 0)
								solubilityString += ", ";
							solubilityString += solubilitySolventVO.getQualiString() + " in " + solubilitySolventVO.getCodeAndName();
						}
					}
					jLabelSolubilityInSolventsValue.setText(solubilityString);
					jLabelSolubilityInSolventsValue.setToolTipText(jLabelSolubilityInSolventsValue.getText());
				} else {
					jLabelSolubilityInSolventsValue.setText("-none-");
					jLabelSolubilityInSolventsValue.setToolTipText(null);
				}
				// total amount weight
				this.totalAmountMadeWtView.setValue(BatchAttributeComponentUtility.getTotalAmountMadeWeight(productBatchModel));
				this.totalAmountMadeWtView.setEditable(false);
				// total amount volume
				this.totalAmountMadeVolView.setValue(BatchAttributeComponentUtility.getTotalAmountMadeVolume(productBatchModel));
				this.totalAmountMadeVolView.setEditable(false);
				// Total molarity
				this.totalAmountMadeMolesView.setValue(BatchAttributeComponentUtility.getTotalMoles(productBatchModel));
				this.totalAmountMadeMolesView.setEditable(false);
				// Amount remaining weight
				this.amountRemainingWtView.setValue(BatchAttributeComponentUtility.getWeightRemaining(productPlate, productBatchModel));
				this.amountRemainingWtView.setEditable(false);
				// Amount remaining volume
				this.amountRemainingVolView.setValue(BatchAttributeComponentUtility.getVolumeRemaining(productPlate, productBatchModel));
				// Amount in well/Vial weight
				if (isPseudoProductPlate)
					// Amount in vial
					this.amountInVialWtView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeWeight(well));
				else
					// Amount in well weight
					this.amountInWellWtView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeWeight(well));
				this.amountRemainingVolView.setEditable(false);
				this.amountInVialWtView.setEditable(false);
/*				if (well != null) {
					this.amountInWellWtView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeWeight(well));
					this.amountInWellWtView.setEditable(false);
				}
*/				// Amount in well volume
				if (well != null) {
					this.amountInWellVolView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeVolume(well));
					this.amountInWellVolView.setEditable(false);
				}
				// Amount in well molarity
				if (well != null) {
					this.amountInWellMolesView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeMoles(well, productBatchModel));
					this.amountInWellMolesView.setEditable(false);

					//Vial barcode
					vialBarcode.setText(well.getBarCode());
				}
				// Amount in vial
				//this.amountInVialWtView.setValue(BatchAttributeComponentUtility.getAmountInWellOrTubeWeight(well));


				this.enableComponents(true);
			}

		} catch (Exception e) {
			log.error("Failed to populate product batch details for batch: " + productBatchModel.getBatchNumberAsString());
			isLoading = false;
		} finally {
			isLoading = false;
		}
	}

	private void clearComponents() {
		this.getChimePanel(); // this should set it to empty if the batch is null
		if (isParallel)
		{
			this.productBatchModel = null;
			this.well = null;
			jLabelProductPlateName.setText("");
			jLabelWellName.setText("");
		}
		jLabelNbkBatchNumValue.setText("");
		jLabelStatusValue.setText("");
		jLabelConvBatchNumValue.setText("");
		jLabelVirtualCompNumValue.setText("");
		jTextAreaBatchCommentsValue.setText("");
		jLabelStereoisomerValue.setText("");
		jLabelCompoundSourceValue.setText("");
		jLabelSourceDetailValue.setText("");
		jLabelBatchOwnerValue.setText("");
		jLabelSynthesizedByValue.setText("");

		jLabelCalcBatchMWValue.setText("");
		jLabelCalcBatchMFValue.setText("");
		jLabelCalcParentMWValue.setText("");
		jLabelSaltCodeValue.setText("");
		jLabelSaltEquivalentsValue.setText("");
		jLabelResidualSolventsValue.setText("");
		jLabelSolubilityInSolventsValue.setText("");
		jLabelPrecursorsValues.setText("");
		jLabelPurityValue.setText("");
        jLabelCompoundIsValue.setText("");
		amountInVialWtView.setValue(new PAmountComponent());
		amountInWellMolesView.setValue(new AmountModel(UnitType.MOLES));
		amountInWellVolView.setValue(new PAmountComponent());
		amountInWellWtView.setValue(new PAmountComponent());
		amountRemainingVolView.setValue(new PAmountComponent());
		amountRemainingWtView.setValue(new PAmountComponent());
		totalAmountMadeMolesView.setValue(new AmountModel(UnitType.MOLES));
		totalAmountMadeVolView.setValue(new PAmountComponent());
		totalAmountMadeWtView.setValue(new PAmountComponent());
		vialBarcode.setText("");
	}

	private void disableAllComponents() {
		this.amountInWellWtView.setEditable(false);
		this.amountInWellVolView.setEditable(false);
		this.amountInWellMolesView.setEditable(false);
		this.amountInVialWtView.setEditable(false);
		//this.jTextFieldVialBarcodeValue.setEditable(false);
	}

	private void enableComponents(boolean enable) {
		enable = false;  // vb 2/4 for now
		this.totalAmountMadeWtView.setEditable(enable);
		this.totalAmountMadeVolView.setEditable(enable);
		this.totalAmountMadeMolesView.setEditable(enable);
		this.amountRemainingVolView.setEditable(enable);
		this.amountRemainingWtView.setEditable(enable);
		this.amountInWellWtView.setEditable(enable);
		this.amountInWellVolView.setEditable(enable);
		this.amountInWellMolesView.setEditable(enable);
		this.amountInVialWtView.setEditable(false);
		this.amountInVialWtView.setEditable(enable);
		//this.jTextFieldVialBarcodeValue.setEditable(enable);

		if (this.productPlate instanceof PseudoProductPlate)
		{
			this.amountInWellWtView.setEditable(enable);
			//this.wellMolarityComponent.setEditable(!enable);
			this.amountInVialViewMass.setEditable(enable);
			//this.tubeMolarityComponent.setEditable(enable);

			this.jTextFieldVialBarcodeValue.setEditable(enable);
		}
		else
		{
			this.amountInVialViewMass.setEditable(enable);
			//this.tubeMolarityComponent.setEditable(!enable);
			this.amountInWellViewMass.setEditable(enable);
			this.amountInWellViewVolume.setEditable(enable);
			//this.wellMolarityComponent.setEditable(enable);
		}
	}

	/**
	 * Builds the panel using the Form Layout.
	 * @return
	 */
	private JPanel getPanel() {
		// Set row format
		StringBuffer rowFormatBuff = new StringBuffer();
		for (int i=0; i<NUM_ROWS-1; i++) {
			rowFormatBuff.append("pref, ");
		}
		rowFormatBuff.append("pref");
		FormLayout layout = new FormLayout("left:pref, 10dlu, " +  // 1, 2 (col 1 and space)
										   "70dlu, 70dlu, 20dlu, " +  // 3, 4 (col 2 and space)
										   "120dlu, 10dlu, " + // 5, 6 (col 3 and space)
										   "120dlu, 5dlu, 45dlu", // 7, 8 (col 4 and space)
										   rowFormatBuff.toString());
		// Set row groups (all rows are in group)
		int[][] rowGroups = new int[1][NUM_ROWS];
		for (int i=0; i<NUM_ROWS; i++) {
			rowGroups[0][i] = i+1;
		}
		layout.setRowGroups(rowGroups);

		int row = 2;
		int col1 = 1;
		int col2 = 3;
		int col3 = 6;
		int col4 = 8;

		PanelBuilder builder = new PanelBuilder(layout);
		// For debug
		//PanelBuilder builder = new PanelBuilder(layout, new FormDebugPanel());
		CellConstraints cc = new CellConstraints();
		if (isParallel)
		{
		builder.add(jLabelProductPlate,                 cc.xy  (col1,   row));
		builder.add(jLabelProductPlateName,             cc.xyw  (col2,   row, 2));
		}
		builder.add(this.getChimePanel(),               cc.xywh(col3, row, 3, 8));  row++;
		if (isParallel)
		{
		builder.add(jLabelWell,                 		cc.xy  (col1,   row));
		builder.add(jLabelWellName,             		cc.xy (col2,   row));     row++;
		}
		////builder.add(jLabelTubeBarcode,                  cc.xy(col1, row));
		////builder.add(this.jTextFieldTubeBarcodeValue,    cc.xy (col2, row));       row++;

		////builder.add(jLabelVialBarcode,                  cc.xy(col1, row));
		////builder.add(this.jTextFieldVialBarcodeValue,    cc.xy (col2, row));       row++;

		//builder.add(jLabelWellSequence,             	cc.xy  (col1,   row));
		//builder.add(jLabelWellSequenceValue,        	cc.xy (col2,   row));      row++;

		builder.add(jLabelNbkBatchNum,                  cc.xy  (col1,   row));
		builder.add(jLabelNbkBatchNumValue,             cc.xyw  (col2,   row, 2));     row++;

		builder.add(jLabelStatus,                       cc.xy  (col1,   row));
		builder.add(jLabelStatusValue,                  cc.xyw  (col2,   row, 2));     row++;

		builder.add(jLabelConvBatchNum,                 cc.xy  (col1,   row));
		builder.add(jLabelConvBatchNumValue,            cc.xyw  (col2,   row, 2));     row++;

		builder.add(jLabelVirtualCompNum,               cc.xy  (col1,   row));
		builder.add(jLabelVirtualCompNumValue,          cc.xyw  (col2,   row, 2));     row++;

		builder.add(jLabelBatchComments,     			cc.xy  (col1, row));
		builder.add(jScrollPaneBatchComments,           cc.xywh(col2, row, 2, 3));   row++; row++; row++;  row++;

/*		builder.add(jLabelSubmissionParameters,         cc.xy(col1, row));
		builder.add(jButtonSubmissionParameters,        cc.xy(col2, row));       row++;
*/
		builder.add(jLabelStereoisomer,                  cc.xy(col1, row));
		builder.add(jLabelStereoisomerValue,             cc.xyw(col2, row, 2));      row++;

		builder.add(jLabelCompoundSource,               cc.xy (col1,   row));
		builder.add(jLabelCompoundSourceValue,          cc.xyw (col2,   row, 2));    row++;
		builder.add(jLabelSourceDetail,                 cc.xy  (col1,   row));
		builder.add(jLabelSourceDetailValue,            cc.xyw  (col2,   row, 2));   row++;

		builder.add(jLabelBatchOwner,                   cc.xy  (col1,   row));
		builder.add(jLabelBatchOwnerValue,		    	cc.xyw (col2,   row, 2));    row++;

		builder.add(jLabelSynthesizedBy,                cc.xy  (col1,   row));
		builder.add(jLabelSynthesizedByValue,	    	cc.xyw (col2,   row, 2));     row++;

		////builder.add(jLabelTotAmountMade,                cc.xy  (col1,   row));
		////builder.add(totAmountMadeView, 	      			cc.xyw (col2,   row, 1));      row++;

		////builder.add(jLabelTotVolumeMade,                cc.xy  (col1,   row));
		////builder.add(totVolumeMadeView, 	      			cc.xyw (col2,   row, 1));      row++;

//		builder.add(jLabelAmountRemaining,             cc.xy(col1, row));
//		builder.add(jLabelAmountRemainingValue,        cc.xyw(col2, row, 2));         row++;

		builder.add(this.jLabelCalcBatchMW,             cc.xy(col1, row));
		builder.add(this.jLabelCalcBatchMWValue,        cc.xy(col2, row));       row++;

		builder.add(this.jLabelCalcParentMW,             cc.xy(col1, row));
		builder.add(this.jLabelCalcParentMWValue,        cc.xy(col2, row));       row++;

		builder.add(this.jLabelSaltCode,                 cc.xy(col1, row));
		builder.add(this.jLabelSaltCodeValue,            cc.xyw(col2, row, 2));       row++;

		builder.add(this.jLabelSaltEquivalents,         cc.xy(col1, row));
		builder.add(this.jLabelSaltEquivalentsValue,    cc.xy(col2, row));       row++;

		builder.add(jLabelCalcBatchMF,                  cc.xy  (col1,   row));
		builder.add(jLabelCalcBatchMFValue,             cc.xy (col2,   row));          row++;

		builder.add(jLabelPrecursors,                   cc.xy  (col1,   row));
		builder.add(jLabelPrecursorsValues,             cc.xyw (col2,   row, 2));          row++;

		builder.add(this.jLabelPurity,                  cc.xy(col1, row));
		builder.add(this.jLabelPurityValue,             cc.xy(col2, row));		    row++;
		builder.add(this.jLabelCompoundIs,                  cc.xy(col1, row));
		builder.add(this.jLabelCompoundIsValue,             cc.xy(col2, row));      row++;

		// Second column
		row = 11;

		// Total amount made and amount remaining
		JPanel amountRemainingPanel = new JPanel();
		amountRemainingPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		FormLayout layout0 = new FormLayout("left:1dlu, 80dlu, 10dlu, 60dlu, 10dlu, 60dlu", "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu");
		PanelBuilder builder0 = new PanelBuilder(layout0);
		builder0.add(this.makeCeNLabel("Total Amount Made"),         cc.xy(2, 2));
		builder0.add(this.totalAmountMadeWtView,              							   cc.xy(4, 2));
		builder0.add(this.totalAmountMadeVolView,                                             cc.xy(6, 2));
		builder0.add(this.makeCeNLabel("Total Moles"),                  cc.xy(2, 4));
		builder0.add(this.totalAmountMadeMolesView,                                 cc.xy(6, 4));
		builder0.add(this.makeCeNLabel("Amount Remaining"),       cc.xy(2, 6));
		builder0.add(this.amountRemainingWtView,                                    cc.xy(4, 6));
		builder0.add(this.amountRemainingVolView,                                   cc.xy(6, 6));
		amountRemainingPanel.add(builder0.getPanel());
		builder.add(amountRemainingPanel,                  cc.xywh(col3, row, 4, 3));      row += 4;

    if ((this.productPlate == null) || (this.productPlate instanceof PseudoProductPlate) ||
        (!(lastEventObj instanceof CompoundContainer))) {  // not Plates view

      JPanel vialTubeTablePanel = new JPanel();
      vialTubeTablePanel.setBorder(BorderFactory.createLoweredBevelBorder());

      JLabel jLabelContainerHeader = makeCeNLabel("Vials/Tubes Containing this Batch");

      vialTubeTablePanel.setLayout(new BorderLayout());
      vialTubeTablePanel.add(jLabelContainerHeader, BorderLayout.NORTH);
      JScrollPane jScrollPane = new JScrollPane();
      TubeVialContainerTableConnector tubeVialTableConnector = new TubeVialContainerTableConnector(this.pageModel,this.productBatchModel);
      tubeVialTableConnector.setReadOnly(true); // read only on registration page
      TubeVialContainerTableView jTableTubeVialContainers = new TubeVialContainerTableView(new TubeVialContainerTableModel(tubeVialTableConnector),StoicConstants.TABLE_ROW_HEIGHT,tubeVialTableConnector,null,this.productBatchModel);
      jScrollPane.add(jTableTubeVialContainers);
      jScrollPane.setViewportView(jTableTubeVialContainers);

      vialTubeTablePanel.add(jScrollPane, BorderLayout.CENTER);

      jTableTubeVialContainers.setEnabled(Boolean.FALSE);

      builder.add(vialTubeTablePanel,cc.xywh(col3, row, 5, 6));          row+=6;
    } else {
  		// Well tube amounts
  		JPanel wellTubeAmountPanel = new JPanel();
      FormLayout layout1 = new FormLayout("left:1dlu, 80dlu, 10dlu, 60dlu, 10dlu, 60dlu", "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu");
      PanelBuilder builder1 = new PanelBuilder(layout1);

  		wellTubeAmountPanel.setBorder(BorderFactory.createLoweredBevelBorder());
      builder1.add(this.makeCeNLabel("Amount in Well/Tube"),                cc.xy(2, 2));
      builder1.add(this.amountInWellWtView,              cc.xy(4, 2));
      builder1.add(amountInWellVolView,              cc.xy(6, 2));
      builder1.add(this.makeCeNLabel("Moles"),                    cc.xy(2, 4));
      builder1.add(amountInWellMolesView,                  cc.xy(6, 4));

      builder1.add(jLabelWellSolvent, cc.xy(2, 6));
      builder1.add(jLabelWellSolventValue, cc.xyw(4 , 6, 3));
      builder1.add(jLabelTubeBarcode, cc.xy(2, 8));
      builder1.add(jLabelTubeBarcodeValue, cc.xyw(4 , 8, 3));
      wellTubeAmountPanel.add(builder1.getPanel(),         cc.xy(1, 1));

      builder.add(wellTubeAmountPanel, cc.xywh(col3, row, 4, 4));           row+=5;
		}
		builder.add(jLabelResidualSolvents,             cc.xywh(col3, row, 2, 1));
		builder.add(jLabelResidualSolventsValue,        cc.xywh(col4, row, 1, 1));    	row++;

		builder.add(jLabelSolubilityInSolvents,         cc.xywh(col3, row, 2, 1));
		builder.add(jLabelSolubilityInSolventsValue,    cc.xywh(col4, row, 1, 1));		row++;

//		builder.add(jLabelAmountInWellWt,                 cc.xy(col3, row));
//		builder.add(amountInWellViewMass,                   cc.xy(col4, row));           row++;
//
//		builder.add(jLabelAmountInWellVol,                 cc.xy(col3, row));
//		builder.add(amountInWellViewVolume,     cc.xy(col4, row));           row++;
//
//		builder.add(jLabelWellMolarity,                   cc.xy(col3, row));
//		builder.add(wellMolarityComponent,     cc.xy(col4, row));           row++;
//
//		builder.add(jLabelAmountInTubeWt,                 cc.xy(col3, row));
//		builder.add(amountInTubeViewMass,                   cc.xy(col4, row));           row++;
//
//		builder.add(jLabelAmountInTubeVol,                 cc.xy(col3, row));
//		builder.add(amountInTubeViewVolume,     cc.xy(col4, row));           	row++;
//
//		builder.add(jLabelTubeMolarity,                 cc.xy(col3, row));
//		builder.add(tubeMolarityComponent,     cc.xy(col4, row));           row++;
//
//		builder.add(jLabelAmountInVial,                 cc.xy(col3, row));
//		builder.add(amountInVialViewMass,                   cc.xy(col4, row));           row++;

//		builder.add(this.jLabelPurity,                  cc.xy(col3, row));
//		builder.add(this.jLabelPurityValue,             cc.xy(col4, row));       row++;
//
//		builder.add(this.jLabelSaltCode,                 cc.xy(col3, row));
//		builder.add(this.jLabelSaltCodeValue,            cc.xy(col4, row));       row++;
//
//		builder.add(this.jLabelSaltEquivalents,         cc.xy(col3, row));
//		builder.add(this.jLabelSaltEquivalentsValue,    cc.xy(col4, row));       row++;
//
//		builder.add(jLabelResidualSolvents,             cc.xy  (col3,   row));
//		builder.add(jLabelResidualSolventsValue,        cc.xy  (col4,   row));    row++;
//
//		builder.add(jLabelSolubilityInSolvents,         cc.xy  (col3,   row));
//		builder.add(jLabelSolubilityInSolventsValue,    cc.xy  (col4,   row));		row++;
//
//		builder.add(jLabelCalcBatchMF,                  cc.xy  (col3,   row));
//		builder.add(jLabelCalcBatchMFValue,             cc.xy (col4,   row));          row++;
//
//		builder.add(jLabelPrecursors,                   cc.xy  (col3,   row));
//		builder.add(jLabelPrecursorsValues,             cc.xy (col4,   row));          row++;

		return builder.getPanel();
	}

    /**
     * Create a label object.
     * @param text
     * @param font
     * @return
     */
	private JLabel makeLabel(String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		return label;
	}

    private JLabel makeCeNLabel(String text) {
        return new CeNLabel(text);
    }


	/**
	 * Create a tex field object and set listeners.
	 * @param text
	 * @param size
	 * @param property
	 * @return
	 */
	private JTextField makeTextField(String text, int size, String property) {
		JTextField textField = new JTextField(text, size);
		textField.setDocument(new JTextFieldLimit(12));
		textField.setFont(EDIT_FONT_SMALL);
		textField.getDocument().addDocumentListener(new BatchDocumentListener());
		textField.addFocusListener(new PropertyFocusListener(property));
		return textField;
	}
	
	private JTextField makeTextFieldLabel(String text, Font font, int size) {
		JTextField textField = new JTextField("", size);
		textField.setFont(font);
		textField.setFocusable(true);
		textField.setEnabled(true);
		textField.setEditable(false);
		textField.setBorder(null);
		return textField;
	}

	private PAmountComponent makePAmountComponent() {
		PAmountComponent amountComponent = new PAmountComponent();
		amountComponent.setEditable(true);
		amountComponent.setValueSetTextColor(null);
		amountComponent.addAmountEditListener(this);
		amountComponent.setBorder(BorderFactory.createLoweredBevelBorder());
		return amountComponent;
	}

	/**
	 * Create a text area object and set its listeners.
	 * @param text
	 * @param rows
	 * @param cols
	 * @param property
	 * @return
	 */
	private JTextArea makeTextArea(String property, boolean inScrollPane) {
		JTextArea textArea = new JTextArea();
		textArea.setFont(EDIT_FONT_LARGE);
		if (! inScrollPane)
			textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, Color.lightGray, null, Color.black));
		textArea.getDocument().addDocumentListener(new BatchDocumentListener());
		textArea.addFocusListener(new PropertyFocusListener(property));
		return textArea;
	}

	private JScrollPane makeScrollPane(JTextArea textarea) {
		JScrollPane sp = new JScrollPane(textarea);
		sp.setBounds(new java.awt.Rectangle(0, 350, 295, 60));
		sp.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		//sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//sp.setViewportBorder(new EmptyBorder(new Insets(0,0,0,0)));
		return sp;
	}

	/**
	 * todo
	 *
	 */
	public void dispose() {
//		MasterController.getGuiController().removeStructureEditorTypeChangeListener(this);
//		nbPage = null;
//		associatedBatch = null;
//		batchModel = null;
//		structQueryCanvas = null;
//		if (saltCodes != null)
//			saltCodes.clear();
//		saltCodes = null;
//		if (editListeners != null)
//			editListeners.clear();
//		editListeners = null;
//		if (amtCompBatchWt != null)
//			amtCompBatchWt.dispose();
//		amtCompBatchWt = null;
//		if (amtCompTheoWt != null)
//			amtCompTheoWt.dispose();
//		amtCompTheoWt = null;
//		if (amtCompTheoMoles != null)
//			amtCompTheoMoles.dispose();
//		amtCompTheoMoles = null;
//		nativeSketchBeforeEdit = null;
	}

	protected boolean isEditing() {
		//	return (jTextFieldLotNo.hasFocus() || amtCompBatchWt.hasFocus());
		return true;
	}

	protected void stopEditing() {
//	if (!isLoading) {
//		if (jTextFieldLotNo.hasFocus()) {
//			updateBatchObject(RegistrationBatchDetailPanel.LOT);
//			fireEditingStopped();
//		}
//		if (amtCompBatchWt.hasFocus()) {
//			amtCompBatchWt.stopEditing();
//		}
//		this.requestFocus();
	}

	public void addBatchEditListener(BatchEditListener o) {
//		if (!editListeners.contains(o))
//			editListeners.add(o);
	}

	/**
	 *
	 * @return
	 */
	public NotebookPage getNotebookPage() {
		return nbPage;
	}

	/**
	 *
	 * @param nbPage
	 */
	public void setNotebookPage(NotebookPage nbPageObj) {
		nbPage = nbPageObj;
		isEditable = (nbPage != null && nbPage.isPageEditable());
		// commented out to set external chemistry editor to read only by
		// default
		// structQueryCanvas.setReadOnly(!isEditable);
	}


	public boolean isLoading() {
		return isLoading;
	}

	public void setAssociatedBatch(ProductBatch batch) {
		// Literally is the same object
//		if (batch != associatedBatch) {
//			if (associatedBatch != null)
//				associatedBatch.deleteObserver(this);
//			associatedBatch = batch;
//			if (associatedBatch != null)
//				associatedBatch.addObserver(this);
//		}
//		populate();
	}


	/**
	 * @param pageModel the pageModel to set
	 */
	public void setPageModel(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}


	private void insertBlankItem(JComboBox comboBox) {
		comboBox.insertItemAt("", 0);
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
	}
	
	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	public JPanel getChimePanel() {
		String stringSketch = "";
		
		if (productBatchModel != null) {
			stringSketch = this.productBatchModel.getCompound().getStringSketchAsString();
			stringSketch = Decoder.decodeString(stringSketch);
		}
			
		chemistryViewer.setChemistry(stringSketch.getBytes());	
			
		return chemistryViewerPanel;
		
	}

	private void enableSaveButton() {
		if (this.productBatchModel != null) {
			MasterController.getGUIComponent().enableSaveButtons();
			this.productBatchModel.setModelChanged(true);
			this.pageModel.setModelChanged(true);
			// The singleton page model is set to uneditable.  Why?
			// Temporary fix...
			this.pageModel.setEditable(true);
		}
	}

	private void updateProperty(String property) {
		if (isLoading || !isEditable || this.productBatchModel == null)
			return;
		isLoading = true;
		if (property != null && property.length() > 0) {
			if (property.equals(AMOUNT_IN_WELL_WT) ) {
//				double totalAmtMade = well.getBatch().getTotalWeight().GetValueInStdUnitsAsDouble();
//				if (totalAmtMade <= 0 )
//				{
//					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please enter 'Total Amount Made' before entering the amount in well.");
//					amountInWellViewMass.setValue(well.getContainedAmount());
//				}
//				else {
//					double amount = amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble();
//					if (amount > totalAmtMade)
//					{
//						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Amount in well can not exceed total Amount made.");
//						amountInWellViewMass.setValue(well.getContainedAmount());
//					}
//					else
//					{
//						well.setContainedAmount(amountInWellViewMass.getAmount());
//						if (amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble() > 0 && amountInWellViewVolume.getAmount().GetValueInStdUnitsAsDouble() > 0)
//						{
//							double molarity = amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble() / amountInWellViewVolume.getAmount().GetValueInStdUnitsAsDouble();
//							well.getMolarity().SetValueInStdUnits(molarity);
//							wellMolarityComponent.setValue(well.getMolarity());
//						}
//						enableSaveButton();
//					}
//				}
			}
			if (property.equals(AMOUNT_IN_WELL_VOL)) {
//				well.setContainedAmountInVolume(amountInWellViewVolume.getAmount());
//				if (amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble() > 0 && amountInWellViewVolume.getAmount().GetValueInStdUnitsAsDouble() > 0)
//				{
//					double molarity = amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble() / amountInWellViewVolume.getAmount().GetValueInStdUnitsAsDouble();
//					well.getMolarity().SetValueInStdUnits(molarity);
//					wellMolarityComponent.setValue(well.getMolarity());
//				}
//				enableSaveButton();
//			} else if (property.equals(WELL_MOLARITY))
//			{
//				well.setMolarity(wellMolarityComponent.getAmount());
//				if (amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble() > 0)
//				{
//					double volume = amountInWellViewMass.getAmount().GetValueInStdUnitsAsDouble() / wellMolarityComponent.getAmount().GetValueInStdUnitsAsDouble();
//					well.getContainedAmountInVolume().SetValueInStdUnits(volume);
//					amountInWellViewVolume.setValue(well.getContainedAmountInVolume());
//				}
			}
			if (property.equals(AMOUNT_IN_TUBE_WT) ) {
//				double totalAmtMade = tube.getBatch().getWeightAmount().GetValueInStdUnitsAsDouble();
//				if (totalAmtMade <= 0 )
//				{
//					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please enter 'Total Amount Made' before entering the amount in tube.");
//					amountInTubeViewMass.setValue(tube.getContainedAmount());
//				}
//				else {
//					double amountInTube = amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble();
//					double amountInVial = amountInVialViewMass.getAmount().GetValueInStdUnitsAsDouble();
//					double amount = amountInVial + amountInTube;
//
//					if (amount > totalAmtMade)
//					{
//						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The sum of amount in tube and amount in vial can not exceed total amount made.");
//						amountInTubeViewMass.setValue(tube.getContainedAmount());
//					}
//					else
//					{
//						tube.setContainedAmount(amountInTubeViewMass.getAmount());
//						if (amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble() > 0 && amountInTubeViewVolume.getAmount().GetValueInStdUnitsAsDouble() > 0)
//						{
//							double molarity = amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble() / amountInTubeViewVolume.getAmount().GetValueInStdUnitsAsDouble();
//							tube.getMolarity().SetValueInStdUnits(molarity);
//							tubeMolarityComponent.setValue(tube.getMolarity());
//						}
//						enableSaveButton();
//					}
//				}
			}
			if (property.equals(AMOUNT_IN_TUBE_VOL)) {
//				tube.setContainedAmountInVolume(amountInTubeViewVolume.getAmount());
//				if (amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble() > 0 && amountInTubeViewVolume.getAmount().GetValueInStdUnitsAsDouble() > 0)
//				{
//					double molarity = amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble() / amountInTubeViewVolume.getAmount().GetValueInStdUnitsAsDouble();
//					tube.getMolarity().SetValueInStdUnits(molarity);
//					tubeMolarityComponent.setValue(tube.getMolarity());
//				}
//				enableSaveButton();
//			} else if (property.equals(TUBE_MOLARITY))
//			{
//				tube.setMolarity(tubeMolarityComponent.getAmount());
//				if (amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble() > 0)
//				{
//					double volume = amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble() / tube.getMolarity().GetValueInStdUnitsAsDouble();
//					tube.getContainedAmountInVolume().SetValueInStdUnits(volume);
//					amountInTubeViewVolume.setValue(tube.getContainedAmountInVolume());
//				}
			}
			else if (property.equals(AMOUNT_IN_VIAL)) {
//				double totalAmtMade = vial.getBatch().getTotalWeight().GetValueInStdUnitsAsDouble();
//				if (totalAmtMade <= 0 )
//				{
//					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please enter 'Total Amount Made' before entering the amount in vial.");
//					amountInVialViewMass.setValue(vial.getContainedAmount());
//				}
//				else
//				{
//					double amountInVial = amountInVialViewMass.getAmount().GetValueInStdUnitsAsDouble();
//					double amountInTube = amountInTubeViewMass.getAmount().GetValueInStdUnitsAsDouble();
//					double amount = amountInVial + amountInTube;
//
//					if (amount > totalAmtMade)
//					{
//						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The sum of amount in tube and amount in vial can not exceed total amount made.");
//						amountInVialViewMass.setValue(vial.getContainedAmount());
//					}
//					else
//					{
//						vial.setContainedAmount(amountInVialViewMass.getAmount());
//						enableSaveButton();
//					}
//				}
			} else if (property.equals(VIAL_BARCODE)) {
				//Vial barcode is nor necerssary. Remove this after confirmation. Jags_todo...
				//this.vial.setBarcode(jTextFieldVialBarcodeValue.getText());
				enableSaveButton();
			}
		}
		// todo......... handle the mass or volume entry
		isLoading = false;
	}

	private String processString(String instr) {
		if (instr == null)
			return "";
		else if (instr.equalsIgnoreCase("null"))
			return "";
		else return instr;
	}

	protected void comboSourceItemStateChanged() {
		//this.componentUtility.updateComboSourceDetail(this.jComboBoxCompoundSource, this.jComboBoxSourceDetail);
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

	class PropertyFocusListener extends FocusAdapter {
		String property = "";
		public PropertyFocusListener(String property) {
			this.property = property;
		}
		public void focusLost(FocusEvent evt) {
			System.out.println("focusLost property = " + property);
			if (isChanging) {
				if (property != null && property.length() > 0)
					updateProperty(property);
				//enableSaveButton();
				isChanging = false;
			}
		}
	}

	protected void showSolubilityInSolventsInfoDialog(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	protected void showResidualSolventsInfoDialog(ActionEvent e) {
		// TODO Auto-generated method stub

	}
	private boolean purityModelListChanged(List newPurityModelList) {

		return true;
	}

	public void editingCanceled(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

	public void editingStopped(ChangeEvent e) {
/*		Object o = e.getSource();
		if (o instanceof PAmountComponent) {
			if (o == amountInWellViewMass)
				this.updateProperty(AMOUNT_IN_WELL_WT);
			else if (o == amountInWellViewVolume)
				this.updateProperty(AMOUNT_IN_WELL_VOL);
			else if (o == amountInTubeViewMass)
				this.updateProperty(AMOUNT_IN_TUBE_WT);
			else if (o == amountInTubeViewVolume)
				this.updateProperty(AMOUNT_IN_TUBE_VOL);
			else if (o == amountInVialViewMass)
				this.updateProperty(AMOUNT_IN_VIAL);
			else if (o == wellMolarityComponent)
				this.updateProperty(WELL_MOLARITY);
			else if (o == tubeMolarityComponent)
				this.updateProperty(TUBE_MOLARITY);
		} else if (o == jTextFieldVialBarcodeValue)
			this.updateProperty(VIAL_BARCODE);	*/
	}

/*	public void setBatchesAndPlatesMap(HashMap batchesAndPlatesMap) {
		this.batchesAndPlatesMap = batchesAndPlatesMap;
	}*/

	public void setProductPlate(ProductPlate productPlate) {
		this.productPlate = productPlate;
	}

}


class JTextFieldLimit extends PlainDocument {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6121857822281528042L;
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

    public void insertString
            (int offset, String  str, AttributeSet attr)
            throws BadLocationException {
        if (str == null) return;

        if ((getLength() + str.length()) <= limit) {
            if (toUppercase) str = str.toUpperCase();
            super.insertString(offset, str, attr);
        }
    }
}
