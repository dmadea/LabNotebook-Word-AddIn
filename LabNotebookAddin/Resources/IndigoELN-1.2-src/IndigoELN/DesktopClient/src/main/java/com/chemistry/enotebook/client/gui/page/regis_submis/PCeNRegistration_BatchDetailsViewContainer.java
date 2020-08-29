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
import com.chemistry.enotebook.client.controller.scheduler.TimerStatusHandler;
import com.chemistry.enotebook.client.gui.CeNCheckBoxIcon;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.*;
import com.chemistry.enotebook.client.gui.page.experiment.plate.CeNProductPlateBuilder;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.PCeNRegistrationBatchInfoTableView;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationAbstractTableModelConnector;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.regis_submis.table.connector.PCeNRegistrationSingleProductPlateTableViewConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewToolBar;
import com.chemistry.enotebook.delegate.RegistrationManagerDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.virtuan.plateVisualizer.StaticPlateRenderer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PCeNRegistration_BatchDetailsViewContainer implements
		PlatesCreatedEventListener, ItemListener,
		ActionListener,
		CompoundCreatedEventListener, BatchSelectionListener {
	
	public static final Log log = LogFactory.getLog(PCeNRegistration_BatchDetailsViewContainer.class);

	private CollapsiblePane viewBatch_Pane;
//	private JTabbedPane plateTableViewTab;
	private JTabbedPane plateRackViewTab;
	private JTabbedPane productTableViewTab;
	// private JScrollPane plateRackViewScrollPane;
	//private ArrayList nonPlatedBatches = new ArrayList();
	
	private BatchSelectionListener batchSelectionListener;

	private NotebookPageModel pageModel;
	private CeNProductPlateBuilder mCeNProductPlateBuilder = new CeNProductPlateBuilder(); // vb

//	public static final String PLATES_VIEW = "Plates";
//	public static final String SEPARATE_PLATES_VIEW = "Tables - Separate Plates";
//	public static final String SEPARATE_PRODUCTS_VIEW = "Tables - Separate Products";
//	public static final String[] VIEWOPTIONS = { SEPARATE_PRODUCTS_VIEW, SEPARATE_PLATES_VIEW, PLATES_VIEW };

	// public static final String SELECT_COMPOUND_AGGREGATION_PANEL = "Select Screen Panels";
	public static final String SELECT_COMPOUND_AGGREGATION_PANEL = "Submittal Sets";
	// public static final String PURIFICATION_PARAMETER = "Set Purification
	// Parameters";
	public static final String PURIFICATION_PARAMETER = "Purification Parameters";
	// public static final String QUALITY_CONTROL_PARAMETER = "Set Quantitative
	// Quality Control Parameters";
	//public static final String QUALITY_CONTROL_PARAMETER = "Quantitative Quality Control Parameters";
	public static final String QUALITY_CONTROL_PARAMETER = "QQC Parameters";
	public static final String GENERATE_BARCODE = "Generate Plate Barcode";
	public static final String REFORMAT_PLATE = "Reformat Plate";
	//public static final String CREATE_SD_FILE = "Create SD Files for Selected Compounds";
	public static final String CREATE_SD_FILE = "Create SD Files";

	public static final String[] ACTION_OPTIONS = { PURIFICATION_PARAMETER,
			QUALITY_CONTROL_PARAMETER, SELECT_COMPOUND_AGGREGATION_PANEL, REFORMAT_PLATE,
			CREATE_SD_FILE };

	// GENERATE_BARCODE, REFORMAT_PLATE, CREATE_SD_FILE, };

	public static final String TABLES_VIEW = "Tables";
	public static final String PLATES_VIEW = "Plates";

	// public static final String[] SUBMIT_OPTIONS = { "Register", "Register
	// Plate", "Submit to Purification", "Submit to Quantitative Quality
	// Control",
	// "Submit to Screening", "All of the Above", };
/*	public static final String[] SUBMIT_OPTIONS = { "Register",
			"Register Plate", "Purification", "Quantitative Quality Control",
			"Screening", "All of the Above", };
*/
//	private ProductBatchFilter productBatchFilter = null;

//	private CeNComboBox viewOptionsCombo = new CeNComboBox(VIEWOPTIONS);
	private CeNComboBox actionComboBox = new CeNComboBox(ACTION_OPTIONS);
	//private CeNComboBox submitToComboBox = new CeNComboBox(SUBMIT_OPTIONS);
	private JButton submitBtn = new JButton("Submit");
	private JButton retrieveRegInfoBtn = new JButton("Retrieve Reg Info");
	private Map<String, PCeNRegistration_BatchDetailsPlateBatchViewPanel> plateViews = new LinkedHashMap<String, PCeNRegistration_BatchDetailsPlateBatchViewPanel>();  // maintain tab plate order
//	private Hashtable plateTableViews = new Hashtable();
	private Map<String, JPanel> productViews = new LinkedHashMap<String, JPanel>(); // maintain product tab ordering
	private ArrayList<ProductPlate> selectedPlates = new ArrayList<ProductPlate>();
	
	// workflow checkboxes 
	private JCheckBox registerCheckBox = new JCheckBox("Register Compound(s)");
	private JCheckBox registerPlateCheckBox = new JCheckBox("Register Container(s)");
	private JCheckBox purificationCheckBox = new JCheckBox("Purification");
	private JCheckBox quantitativeQCCheckBox = new JCheckBox("Quantitative Quality Control");
	private JCheckBox screeningCheckBox = new JCheckBox("Screening");

	// button group
	private JButton purificationParmsBtn = new JButton("Purification Parms");
	private JButton qqcParametersBtn = new JButton("QQC Parameters");
	private JButton screenPanelsBtn = new JButton("Submittal Sets");
	private JButton createSDFilesBtn = new JButton("Create SD Files");
	
	private boolean isSubmitting = false;

	//private PCeNTableView productTableView = null;
	//private PCeNTableView nonPlatedBatchesTableView = null;
	private PCeNRegistrationBatchInfoTableView lastStepProductTableView;  // need this for adding compounds
	
	private List<PCeNRegistrationBatchInfoTableView> tableList = new ArrayList<PCeNRegistrationBatchInfoTableView>();  // keep a list of the tables so we can unset selected row when a we switch to a different table
	
	private JLabel promptLabel = new JLabel("");
	private boolean isLoading = true;
	private static LinkedHashMap<ProductBatchModel, String> errorMap = new LinkedHashMap<ProductBatchModel, String>();;
	//private RegistrationBatchDetailPanel batchDetailPanel;  //This class should not have to have a ref to the class, just the listener
	//private JCheckBox[] workFlowCheckBoxes = { registerCheckBox, registerPlateCheckBox, purificationCheckBox, quantitativeQCCheckBox, screeningCheckBox };

	private String selectedView = TABLES_VIEW;

	private JRadioButton tablesRadioButton = null;
	private JRadioButton platesRadioButton = null;
	private List<JCheckBox> checkBoxList = null;

	/** Checkboxes for tabs */
	private HashMap<String, CeNCheckBoxIcon> checkBoxIconMap = new HashMap<String, CeNCheckBoxIcon>();
	
	public PCeNRegistration_BatchDetailsViewContainer(final NotebookPageModel pageModel, 
	                                                  final BatchSelectionListener batchSelectionListener) {
		this.batchSelectionListener = batchSelectionListener;
//		this.productBatchFilter = new ProductBatchFilter(pageModel);
		// this.tableBatchSelectionChangedListener = listener;
		this.pageModel = pageModel;

		registerCheckBox.setEnabled(pageModel.isEditable());
		registerPlateCheckBox.setEnabled(pageModel.isEditable());
		purificationCheckBox.setEnabled(pageModel.isEditable());
		quantitativeQCCheckBox.setEnabled(pageModel.isEditable());
		screeningCheckBox.setEnabled(pageModel.isEditable());
		
		purificationParmsBtn.setEnabled(pageModel.isEditable());
		qqcParametersBtn.setEnabled(pageModel.isEditable());
		screenPanelsBtn.setEnabled(pageModel.isEditable());
		
		submitBtn.setEnabled(pageModel.isEditable());
		retrieveRegInfoBtn.setEnabled(pageModel.isEditable());
		
		createCollapsiblePane();
//		plateTableViewTab = new JTabbedPane(JTabbedPane.TOP);
		plateRackViewTab = new JTabbedPane(JTabbedPane.TOP);
		productTableViewTab = new JTabbedPane(JTabbedPane.TOP);
		//productTablePanel = new JPanel();
		// viewOptionsCombo.setPreferredSize(new Dimension(150, 20));
//		viewOptionsCombo.addItemListener(this);
		viewBatch_Pane.getContentPane().add(createToolBarElements(),
				BorderLayout.NORTH);
		this.addPlatesToTabs(pageModel.getAllProductPlatesAndRegPlates());
		//this.refreshNonPlatedBatches();
		addProductsToTabs(pageModel);
		updatePlates();

		actionComboBox.addItemListener(this);
		//submitToComboBox.addItemListener(this);

		submitBtn.addActionListener(this);
		retrieveRegInfoBtn.addActionListener(new Retriever());
		
//		viewOptionsCombo.setSelectedIndex(0);//Change the enableSubmitAndRetrieveBtn(false) if it is changed.
		//enableSubmitAndRetrieveBtn(false);//For beta release this will only be enabled in the Plates view. Remove it after Beta and implement the functionality.	
		
		// vb 1/22
		this.purificationParmsBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				performPurificationParameter();
			}
		});
		this.screenPanelsBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				CompoundAggregationScreenPanelSelect dlg = null;

                List<ProductBatchModel> submittalBatches = getSelectedBatches();

				if (submittalBatches.size() == 0) {
					showNoBatchesSelectedErrorMessage("for Submittal Sets");
					return;
				}				
				try {
					dlg = new CompoundAggregationScreenPanelSelect(MasterController.getGUIComponent(), pageModel);
					dlg.initialize(pageModel.getProjectCode(), submittalBatches);
				} catch (RuntimeException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Unexpected error. Please try later.", "Screen Panel failed", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dlg.setVisible(true);
			}
		});
		this.qqcParametersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createRegistrationSDFile();
			}
		});
		this.createSDFilesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createRegistrationSDFile();
			}
		});
		
		productTableViewTab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));
			}
		});

//		plateTableViewTab.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent evt) {
//				batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));
//			}
//		});

		plateRackViewTab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));
			}
		});
		// Disable some components that don't work yet
		
		quantitativeQCCheckBox.setEnabled(false);
		qqcParametersBtn.setEnabled(false);
		retrieveRegInfoBtn.setEnabled(false);
		purificationParmsBtn.setEnabled(false);
		/////RegistrationBatchDetailPanel batchDetailPanelForProductsTabTableView = new RegistrationBatchDetailPanel(this.pageModel, true);
		
		switchView(selectedView);
		
		if (!pageModel.isEditable())
			setButtonsEditable(false);		
		isLoading = false;
	}

//	private JPanel createViewOptionsPanel() {
//		JPanel viewOptionsPanel = new JPanel(new GridLayout(1, 2, 6, 6));
//		tablesRadioButton = new JRadioButton(TABLES_VIEW);
//		platesRadioButton = new JRadioButton(PLATES_VIEW);
//		ButtonGroup group = new ButtonGroup();
//		group.add(tablesRadioButton);
//		group.add(platesRadioButton);
//		viewOptionsPanel.add(tablesRadioButton);
//		viewOptionsPanel.add(platesRadioButton);
//		viewOptionsPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1), new EmptyBorder(6, 6, 6, 6)));
//		tablesRadioButton.setSelected(true);
//		tablesRadioButton.addActionListener(this);
//		platesRadioButton.addActionListener(this);
//		return viewOptionsPanel;
//	}
	
	protected void createRegistrationSDFile() {

		final SwingWorker worker = new CreateSdFileWorker() {
			public Object construct() {
				String sdFile = null;
				try {
					ArrayList<ProductBatchModel> batchesList = new ArrayList<ProductBatchModel>();  
//					ArrayList<Serializable> productPlatesAndBatchesList = getProductPlatesAndBatchesList(false);
					ArrayList<ProductPlate> productPlatesList = getSelectedProductPlates();
					ArrayList<ProductBatchModel> nonPlatedBatchesList = getSelectedNonPlatedBatches(false);

					for (ProductPlate productPlate : productPlatesList)
					{
						ProductBatchModel[] batchModels = productPlate.getAllBatchesInThePlate();
						for (ProductBatchModel batchModel : batchModels)
							batchesList.add(batchModel);
					}
					
					if (nonPlatedBatchesList.isEmpty() == false) {
						batchesList.addAll(nonPlatedBatchesList);
					}
					
					if (batchesList.size() == 0) {
						showNoBatchesSelectedErrorMessage("to create SD file");
						return null;
					}
					 String ntid = MasterController.getUser().getNTUserID();
					 String compoundManagementEmployeeID = MasterController.getUser().getPreference(NotebookUser.PREF_EmployeeID);
					 sdFile = new RegistrationHandler(pageModel).buildSDFile(batchesList,ntid, compoundManagementEmployeeID);
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e);
				}
				return sdFile;
			}
			
		};
		worker.start();
	}

	
	private void setButtonsEditable(boolean editable) {
		editable = pageModel.isEditable();
		submitBtn.setEnabled(editable);
		retrieveRegInfoBtn.setEnabled(editable);
		purificationParmsBtn.setEnabled(editable);
		qqcParametersBtn.setEnabled(editable);
		screenPanelsBtn.setEnabled(editable);
		screeningCheckBox.setEnabled(editable);
		createSDFilesBtn.setEnabled(true);//SDFile creation should be avialable to all users on all experiments
	}

	private void enableSubmitAndRetrieveBtn(boolean enable) {
		enable = pageModel.isEditable() && enable;
		
		submitBtn.setEnabled(enable);
		retrieveRegInfoBtn.setEnabled(enable);
	}

	private CeNCheckBoxIcon getCheckBoxForTab(String key) {
		CeNCheckBoxIcon icon = null;
		if (checkBoxIconMap.containsKey(key)) {
			icon = checkBoxIconMap.get(key);
		} else {
			icon = new CeNCheckBoxIcon(key);
			icon.addActionListener(this);
			checkBoxIconMap.put(key, icon);
		}
		return icon;
	}
	
	public void updatePlates() {
		plateRackViewTab.removeAll();
//		plateTableViewTab.removeAll();
		productTableViewTab.removeAll();
		// vb 2/6 addProductsToTabs(pageModel);  DON'T DO THIS...IT CREATES A DUPLICATE SET OF VIEWS AND MODELS!
		// We will NEVER create a new products tab only a new plate tab.  To add a new batch to the products or non-plated
		// batches table, it is NOT necessary to create an entirely new view and model.  
		updateProducts();
		
		int count = 0;

		for (String key : plateViews.keySet()) {
			if (key.equals(CeNConstants.PSEUDO_PLATE_LABEL)) {
				continue;
			}
			PCeNRegistration_BatchDetailsPlateBatchViewPanel plateViewPanel = plateViews.get(key);
			plateRackViewTab.insertTab(key, null, plateViewPanel, null, count);
			count++;

		}
		
		
		switchView(selectedView);
	}

	private void updateProducts() {
		productTableViewTab.removeAll();
		
		for (Iterator<String> it = this.productViews.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			this.productTableViewTab.addTab(key, getCheckBoxForTab(key), this.productViews.get(key)); 

		}
		switchView(selectedView);
	}
	
	/*
	private void refreshNonPlatedBatches() {
		if(this.pageModel.isParallelExperiment())
		{
		this.nonPlatedBatches.clear();
		this.nonPlatedBatches.addAll(pageModel.getNonPlatedBatches());
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();
		ArrayList pseudoPlates = new ArrayList();
		pseudoPlates.add(pseudoPlate);
		this.removeProductPlatesFromTabs(pseudoPlates);
		this.addPlatesToTabs(pseudoPlates);
		}
	}
	*/

	private JPanel createToolBarElements() {
		
		FormLayout layout = new FormLayout("left:2dlu, 86dlu, 2dlu, 1dlu, 5dlu, pref, 8dlu, 19dlu, 2dlu, left:pref:grow", 
										   "5dlu, 20dlu, 20dlu, 15dlu, 5dlu");
		
		PanelBuilder builder = new PanelBuilder(layout);
		// For debug
		//PanelBuilder builder = new PanelBuilder(layout, new FormDebugPanel());
		
		CellConstraints ccouter = new CellConstraints();

		builder.add(purificationParmsBtn, ccouter.xy(2, 2));
		builder.add(qqcParametersBtn, ccouter.xy(2, 3));
		builder.add(screenPanelsBtn, ccouter.xy(2, 4));

		FormLayout submitCheckboxLayout = new FormLayout("5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu", 
		"1dlu, pref, 1dlu, pref, 1dlu, pref, 1dlu");

		PanelBuilder submitBuilder = new PanelBuilder(submitCheckboxLayout);
		// For debug
		//PanelBuilder submitBuilder = new PanelBuilder(submitCheckboxLayout, new FormDebugPanel());

		CellConstraints ccinner = new CellConstraints();
		submitBuilder.add(this.registerCheckBox, ccinner.xy(2, 2));
		submitBuilder.add(this.registerPlateCheckBox, ccinner.xy(6, 2));
		submitBuilder.add(this.purificationCheckBox, ccinner.xy(2, 4));
		submitBuilder.add(this.quantitativeQCCheckBox, ccinner.xy(6, 4));
		submitBuilder.add(this.screeningCheckBox, ccinner.xy(2, 6));
		
		registerCheckBox.setToolTipText("Compound(s) and Vial(s)");
		registerPlateCheckBox.setToolTipText("Plate(s) and Tube(s)");
		
//		In order of necessity:
//			1)	Register Compounds
//			2)	Register Plates (containers)
//			3)	Purification
//			4)	Screening
		
		checkBoxList = new ArrayList<JCheckBox>();
		checkBoxList.add(registerCheckBox);      // Register Compounds
		checkBoxList.add(registerPlateCheckBox); // Register Plates (containers)
		checkBoxList.add(purificationCheckBox);  // Purification
		checkBoxList.add(screeningCheckBox);     // Screening
				    	
	    for (final JCheckBox box : checkBoxList) {
	    	box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					checkBatchesPassed(box);
				}
			});
	    }
	    
		// Temporary
		promptLabel = new JLabel("To enable, select plate table view.");
		submitBuilder.add(promptLabel, ccinner.xywh(4, 6, 4, 1));
		//submitBuilder.add(submitBtn, ccinner.xy(10, 4));
		JPanel submitCheckboxPanel = submitBuilder.getPanel();
		submitCheckboxPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		JPanel submitPanel = new JPanel();
		submitPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		submitPanel.add(submitCheckboxPanel, BorderLayout.PAGE_START);
		
		JPanel submitButtonPanel = new JPanel();
		submitButtonPanel.setLayout(new GridLayout(3, 1));
		submitButtonPanel.add(submitBtn);
		submitButtonPanel.add(retrieveRegInfoBtn);
		submitButtonPanel.add(createSDFilesBtn);
		submitPanel.add(submitButtonPanel, BorderLayout.LINE_END);
		
		builder.add(submitPanel, ccouter.xywh(6, 2, 1, 3));
		
		return builder.getPanel();
	}

	private void checkBatchesPassed(JCheckBox checkBox) {
		if (checkBox.isSelected() == false) {
			return;
		}
		List<ProductBatchModel> selectedBatches = getSelectedBatches();
		boolean allRegisteredCompoundRegistration = true;
		boolean allRegisteredCompoundManagement = true;
		if (selectedBatches == null || selectedBatches.size() < 1) {
			allRegisteredCompoundRegistration = false;
			allRegisteredCompoundManagement = false;
		} else {
			for (ProductBatchModel batch : selectedBatches) {
				if (batch != null && batch.getRegInfo() != null) {
					if (StringUtils.equals(batch.getRegInfo().getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMISION_PASS) == false) {
						allRegisteredCompoundRegistration = false;
					}
					if (StringUtils.equals(batch.getRegInfo().getCompoundManagementStatus(), CeNConstants.REGINFO_SUBMISION_PASS) == false) {
						allRegisteredCompoundManagement = false;
					}
				}
			}
		}
		if (checkBox == registerPlateCheckBox) {
			registerCheckBox.setSelected(!allRegisteredCompoundRegistration);
		}
		if (checkBox == purificationCheckBox || checkBox == screeningCheckBox) {
			registerCheckBox.setSelected(!allRegisteredCompoundRegistration);
			registerPlateCheckBox.setSelected(!allRegisteredCompoundManagement);
		}
	}

	private List<ProductBatchModel> getSelectedBatches() {
		List<ProductBatchModel> batches = pageModel.getAllProductBatchModelsInThisPage();
		List<ProductBatchModel> selectedBatches = new Vector<ProductBatchModel>();
		for (ProductBatchModel batch : batches) {
			if (batch != null && batch.isSelectedForRegistration()) {
				selectedBatches.add(batch);
			}
		}
		return selectedBatches;
	}

//	protected void selectAllBatchesOfPlate() {
		//Use this method if user want to select all wells of the plate to submit to CompoundManagement.
		
/*		List selectedPlates = this.getSelectedPlates();
		for (Iterator it = selectedPlates.iterator(); it.hasNext();)
			if (! this.isAllBatchesSelected((ProductPlate) it.next())) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
							"Partial selection of wells in the plate can not be submitted for CompoundManagement in this software release. \n"
							+ "Please select all wells in the plate to submit the plate to CompoundManagement.");
				return;
			}
	} */

	// for plate viewer
	private void createCollapsiblePane() {
		viewBatch_Pane = new CollapsiblePane("Product Batch");
		viewBatch_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		viewBatch_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		viewBatch_Pane.setSteps(1);
		viewBatch_Pane.setStepDelay(0);
		viewBatch_Pane.getContentPane().setLayout(new BorderLayout());
	}// end method

	public CollapsiblePane getPlateViewer() {
		return viewBatch_Pane;
	}

	/**
	 * @return the plateRackViewTab
	 */
	public JTabbedPane getPlateRackViewTab() {
		return plateRackViewTab;
	}

	/**
	 * @return the plateTableViewTab
	 */
//	public JTabbedPane getPlateTableViewTab() {
//		return plateTableViewTab;
//	}

	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		long startTime = System.currentTimeMillis();
		List<ProductPlate> plates = event.getPlates();
		addPlatesToTabs(plates);
		//this.refreshNonPlatedBatches();
		// updateSelectedPlatesProducts(this.selectedPlates);
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("newProductPlatesCreated execution time: " + (endTime - startTime) + " ms");
		}
		updatePlates();
		updateProducts();
	}

//	public void addBatchSelectionListener(BatchSelectionListener batchSelectionListener) {
//		this.batchSelectionListener = batchSelectionListener;
//	}

//	private void removePlatesFromTabs(List<ProductPlate> plates) {
//		for (ProductPlate productPlate : plates) {
//			productViews.remove(productPlate.getLotNo());
//		}
//	}

	/**
	 * Add plates to view -- executed once per plate
	 * 
	 * @param plates
	 */
	private void addPlatesToTabs(List<ProductPlate> plates) {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < plates.size(); i++) {
			//Object plateObj = plates.get(i);
			ProductPlate productPlate = plates.get(i);
			
			// Create table view and add it to the tabbed pane
			PCeNRegistrationSingleProductPlateTableViewConnector plateTableViewConnector = new PCeNRegistrationSingleProductPlateTableViewConnector(productPlate, pageModel, errorMap);
			PCeNTableModel model = new PCeNTableModel(plateTableViewConnector);
			PCeNRegistrationBatchInfoTableView plateTableView = new PCeNRegistrationBatchInfoTableView(model, 90, plateTableViewConnector);
			//batchDetailPanel.setProductPlate(productPlate);
			// Set the listeners
			BatchSelectionListener[] batchSelectionListeners = new BatchSelectionListener[2];
			batchSelectionListeners[0] = batchSelectionListener;
			batchSelectionListeners[1] = this;
			plateTableView.setProductBatchDetailsContainerListenerList(batchSelectionListeners);
			this.tableList.add(plateTableView);
			//plateTableView.setProductBatchDetailsContainerListener(batchDetailPanel);
			
			// Create the toolbar
			JScrollPane scrollTableViewPane = new JScrollPane(plateTableView);
			PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(plateTableView);
            scrollTableViewPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollTableViewPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			JPanel plateTabTableViewPanel = new JPanel();
			plateTabTableViewPanel.setLayout(new BorderLayout());
			plateTabTableViewPanel.setBackground(Color.LIGHT_GRAY);
			plateTabTableViewPanel.add(productTableViewToolBar,	BorderLayout.NORTH);
			plateTabTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);

			PCeNRegistration_BatchDetailsPlateBatchViewPanel plateTabTableViewBatchDetailsPanel = new PCeNRegistration_BatchDetailsPlateBatchViewPanel(null,
					plateTabTableViewPanel, null);
			//plateTableViews.put(productPlate.getLotNo(), plateTabTableViewPanel); 
			productViews.put(productPlate.getLotNo(), plateTabTableViewBatchDetailsPanel);
			
			if (!(productPlate instanceof PseudoProductPlate)) {
				// Create plate view and add it to the tabbed pane
				////////// NO!! only need one of these!! RegistrationBatchDetailPanel batchDetailPanel = new RegistrationBatchDetailPanel(this.pageModel, true);
				////////// batchDetailPanel.setProductPlate(productPlate);
				////////// BatchSelectionListener[] listeners = new BatchSelectionListener[2];
				////////// listeners[0] = batchDetailPanel;
				// listeners[1] = this.tableBatchSelectionChangedListener;
				StaticPlateRenderer plateView = (StaticPlateRenderer) mCeNProductPlateBuilder
						.buildCeNproductPlateViewer(productPlate, batchSelectionListeners);
				PCeNRegistration_BatchDetailsPlateBatchViewPanel plateBatchViewPanel = new PCeNRegistration_BatchDetailsPlateBatchViewPanel(
						productPlate, plateView, null);
				//this.plateRackViewTab.putClientProperty(productPlate.getLotNo(), plateBatchViewPanel);
				this.plateViews.put(productPlate.getLotNo(), plateBatchViewPanel);
			} else {
				//nonPlatedBatchesTableView = plateTableView;
			}
		}
		if (log.isInfoEnabled()) {
			long endTime = System.currentTimeMillis();
			log.info("addPlatesToTabs " + plates.size() + " plates elapsed time: " + (endTime - startTime) + " ms");
		}
		updateProducts();
	}

	private void addProductsToTabs(NotebookPageModel pageModel) {
		List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
		if (reactionSteps == null || reactionSteps.size() < 1) {
			// log no products error
			return;
		}
		Collections.sort(reactionSteps, new ReactionStepSorter());  // vb 3/20 if we are creating experiment from Synthesis Plan, steps will not be sorted
		if (reactionSteps.size() == 1) {
			ReactionStepModel reactionStep = (ReactionStepModel) reactionSteps.get(0);
			this.createProductTab(reactionStep, 1, true);
		} else {
			for (int i=1; i<reactionSteps.size(); i++) {
				boolean isLastStep = i+1 == reactionSteps.size();
				ReactionStepModel reactionStep = reactionSteps.get(i);
				this.createProductTab(reactionStep, i, isLastStep);
			}
		}
	}
		
		
	private void createProductTab(ReactionStepModel reactionStep, int stepNumber, boolean isLastStep)
	{
		List<ProductBatchModel> batches = reactionStep.getAllActualProductBatchModelsInThisStep();
		Collections.sort(batches, new NbkBatchNumberComparator<ProductBatchModel>()); // new Comparator() {
		PCeNRegistrationProductsTableModelConnector connector = new PCeNRegistrationProductsTableModelConnector(batches, pageModel, errorMap);
		PCeNTableModel model = new PCeNTableModel(connector);
		final PCeNRegistrationBatchInfoTableView productTableView = new PCeNRegistrationBatchInfoTableView(model, PCeNTableView.tableViewRowHeight, connector);
		// Set the listeners
		BatchSelectionListener[] batchSelectionListeners = new BatchSelectionListener[2];
		batchSelectionListeners[0] = batchSelectionListener;
		batchSelectionListeners[1] = this;
		productTableView.setProductBatchDetailsContainerListenerList(batchSelectionListeners);
		this.tableList.add(productTableView);

		final JScrollPane scrollTableViewPane = new JScrollPane(productTableView);
		PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(productTableView);
		JPanel productTableViewPanel = new JPanel();
		productTableViewPanel.setLayout(new BorderLayout());
		productTableViewPanel.setBackground(Color.LIGHT_GRAY);
		productTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
		productTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);

		productTableView.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                if(productTableView.getHeight() > PCeNTableView.elevenRowsHeight) {
                	scrollTableViewPane.setPreferredSize(new Dimension(scrollTableViewPane.getViewport().getWidth(), PCeNTableView.elevenRowsHeight + PCeNTableView.heightDifference));
                	scrollTableViewPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                } else {
                	scrollTableViewPane.setPreferredSize(null);
                	scrollTableViewPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                }
            }
        });

		PCeNRegistration_BatchDetailsPlateBatchViewPanel plateTabTableViewBatchDetailsPanel = new PCeNRegistration_BatchDetailsPlateBatchViewPanel(	null,
																																					productTableViewPanel,
																																					null);
		productTableViewPanel = plateTabTableViewBatchDetailsPanel;

		if (isLastStep) {
			this.lastStepProductTableView = productTableView;

			Map<String, JPanel> temp = new LinkedHashMap<String, JPanel>();
			temp.put("Product", productTableViewPanel);
			
			for (Iterator<String> it = this.productViews.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					temp.put(key, productViews.get(key));
			}
			productViews = temp;
		}
		else 
			productViews.put("P" + stepNumber, productTableViewPanel);
		
//			BatchSelectionListener[] batchSelectionListeners = new BatchSelectionListener[1];
//			batchSelectionListeners[0] = batchSelectionListener;  
	}
		
	private void removeProductPlatesFromTabs(List<ProductPlate> plates) {
		for (ProductPlate productPlate : plates) {
			JPanel panel = productViews.get(productPlate.getLotNo());
			productTableViewTab.remove(panel);
			productViews.remove(productPlate.getLotNo());
			checkBoxIconMap.remove(productPlate.getLotNo());
			panel = plateViews.get(productPlate.getLotNo());
			plateRackViewTab.remove(panel);
			plateViews.remove(productPlate.getLotNo());
		}
	}
	
	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event) {
		removeProductPlatesFromTabs(event.getPlates());
		//refreshNonPlatedBatches();
		switchView(selectedView);
	}

	public void newMonomerPlatesCreated(MonomerBatchPlateCreatedEvent event) {
	}

	public void monomerPlatesRemoved(MonomerBatchPlateCreatedEvent event) {
	}

	public void newRegisteredPlatesCreated(RegisteredPlateCreatedEvent event) {
	}

	public void registeredPlatesRemoved(RegisteredPlateCreatedEvent event) {
	}

	/*
	 * public void setSelectionListener(ListSelectionListener
	 * listSelectionListener){ this.pBatchDetailsContainerListener =
	 * listSelectionListener; }
	 */

	public void itemStateChanged(ItemEvent e) {

		if (e.getStateChange() == ItemEvent.SELECTED) {
			String viewer = (String) e.getItem();
			switchView(viewer);
		}
	}
	
	private void enableSubmitPanel(boolean enable) {
		//this.submitBtn.setEnabled(enable);
		//this.registerCheckBox.setEnabled(enable);
		this.registerPlateCheckBox.setEnabled(enable);
		this.purificationCheckBox.setEnabled(enable);
		//this.screeningCheckBox.setEnabled(enable);
		// If we are disabled, display the prompt
		if (enable || !pageModel.isEditable()) 
			promptLabel.setText("");
		else
			promptLabel.setText("To enable, select \"Tables\" view");
	}

	private void switchView(String comboText) {
		if (!isLoading)
		{
			for (Iterator<PCeNRegistrationBatchInfoTableView> it = this.tableList.iterator(); it.hasNext();) {
				PCeNTableView table = it.next();
				table.setSelectedAreas(null);  // vb 11/29
			}
			this.batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));
		}

		if (comboText.equals(PLATES_VIEW)) {// PLATE_VIEW, PLATE_TABLE_VIEW,
			// TABLE_VIEW
			if (productTableViewTab != null )
				viewBatch_Pane.getContentPane().remove(productTableViewTab);
			
//			if (plateTableViewTab != null)
//				viewBatch_Pane.getContentPane().remove(plateTableViewTab);
			viewBatch_Pane.getContentPane().add(plateRackViewTab,
					BorderLayout.CENTER);
			// For now only allow submission from plate table view
			enableSubmitPanel(false);
			enableSubmitAndRetrieveBtn(false);
			if (selectedPlates.isEmpty() == false)
			{
				screenPanelsBtn.setEnabled(pageModel.isEditable());
				screeningCheckBox.setEnabled(pageModel.isEditable());				
				purificationParmsBtn.setEnabled(pageModel.isEditable());
			}
			else
			{
				screenPanelsBtn.setEnabled(false);
				screeningCheckBox.setEnabled(false);
				purificationParmsBtn.setEnabled(false);
			}
			//			if (!isSubmitting
//					&& getNonPseudoProductPlates(selectedPlates).size() > 0)
//			{
//				//retrieveRegInfoBtn.setEnabled(true);
//				submitBtn.setEnabled(true);
//			}
//			else
//				submitBtn.setEnabled(false);
//		} else if (comboText.equals(TABLES_VIEW)) {
//			// add them to the collapsibile pane
//			enableSubmitPanel(true);
//			enableSubmitAndRetrieveBtn(true);			
//			if (productTableViewTab != null)
//				viewBatch_Pane.getContentPane().remove(productTableViewTab);
//			if (plateRackViewTab != null)
//				viewBatch_Pane.getContentPane().remove(plateRackViewTab);
//			viewBatch_Pane.getContentPane().add(productTableViewTab, BorderLayout.CENTER);
//			if (selectedPlates.size() > 0)
//			{
//				purificationParmsBtn.setEnabled(pageModel.isEditable());
//				screenPanelsBtn.setEnabled(pageModel.isEditable());
//				if (!isSubmitting)
//					//retrieveRegInfoBtn.setEnabled(true);
//					enableSubmitAndRetrieveBtn(pageModel.isEditable());
//			}
//			else
//			{
//				screenPanelsBtn.setEnabled(false);
//				purificationParmsBtn.setEnabled(false);
//				enableSubmitAndRetrieveBtn(false);
//			}
//			plateTableViewTab.repaint();
		} else if (comboText.equals(TABLES_VIEW)) {
			// // showTableViewInReactionStepTab();
			if (plateRackViewTab != null)
				viewBatch_Pane.getContentPane().remove(plateRackViewTab);
//			if (plateTableViewTab != null)
//				viewBatch_Pane.getContentPane().remove(plateTableViewTab);
			viewBatch_Pane.getContentPane().add(productTableViewTab, BorderLayout.CENTER);
			productTableViewTab.repaint();
			// For now only allow submission from plate table view
			enableSubmitPanel(pageModel.isEditable());
			//enableSubmitAndRetrieveBtn(false);	//For beta release this will only be enabled in the Plates view. Remove it after Beta and implement the functionality.
			enableSubmitAndRetrieveBtn(pageModel.isEditable()); //After Beta release.			
			purificationParmsBtn.setEnabled(pageModel.isEditable());
			screenPanelsBtn.setEnabled(pageModel.isEditable());
			screeningCheckBox.setEnabled(pageModel.isEditable());

			//if (!isSubmitting && selectedPlates.size() > 0)
//			if (!isSubmitting)
//			{
//				//retrieveRegInfoBtn.setEnabled(true);
//				submitBtn.setEnabled(true);
//			}
//			else
//				submitBtn.setEnabled(false);
		} /*else if (comboText.equals(PURIFICATION_PARAMETER)) {
			performPurificationParameter();
		} else if (comboText.equals(QUALITY_CONTROL_PARAMETER)) {
			System.out.println("QUALITY_CONTROL_PARAMETER selected");
		}*//*
			 * else if(comboText.equals(GENERATE_BARCODE)){
			 * System.out.println("GENERATE_BARCODE selected"); }
			 */
		else if (comboText.equals(REFORMAT_PLATE)) {
			System.out.println("REFORMAT_PLATE selected");
		} else if (comboText.equals(CREATE_SD_FILE)) {
			System.out.println("CREATE_SD_FILE selected");
		}/* else if (comboText.equals(SELECT_COMPOUND_AGGREGATION_PANEL)) {
			CompoundAggregationScreenPanelSelect dlg = new CompoundAggregationScreenPanelSelect(
					MasterController.getGUIComponent());
			dlg.initialize(MasterController.getMCompoundAggregationManager(), pageModel
					.getProjectCode());
			dlg.setVisible(true);
//		} else if (comboText.equals(SUBMIT_OPTIONS[0])) { // Register
//			System.out.println("Register");
//		} else if (comboText.equals(SUBMIT_OPTIONS[1])) { // Submit to
//			// Purification
//			// System.out.println("Submit to Purification");
//		} else if (comboText.equals(SUBMIT_OPTIONS[2])) { // Submit to
//			// Quantitative
//			// Quality Control
//			// System.out.println("Submit to Quantitative Quality Control");
//		} else if (comboText.equals(SUBMIT_OPTIONS[3])) { // Submit to
//			// Screening
//			// System.out.println("Submit to Screening");
//		} else if (comboText.equals(SUBMIT_OPTIONS[4])) { // All of the Above
//			// System.out.println("All of the Above");
		}*/
	}// end method

	/*	// Rename vars so they make sense.  Confusing!!!
	private ArrayList getPseudoProductPlate(ArrayList selectedList2) {
		ArrayList nonPseudoProductPlatesList = new ArrayList();
		ProductPlate productPlate = null;
		for (int i = 0; i < selectedList2.size(); i++) {
			productPlate = (ProductPlate) selectedList2.get(i);
			if (productPlate instanceof PseudoProductPlate) {
				nonPseudoProductPlatesList.add(productPlate);
				break;
			}
		}
		return nonPseudoProductPlatesList;
	}
*/
	private void performPurificationParameter() {
		ArrayList<ProductPlate> productPlatesList = getSelectedProductPlates();
		List<PlateWell<ProductBatchModel>> tubesList = getSelectedTubes(pageModel.getGuiPseudoProductPlate());
		if (productPlatesList.size() == 0 && tubesList.size() == 0) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No plates or batches with tube containers are selected for purification.");
			return;
		}
		boolean readOnly = hasPlatePurificationServiceSubmissions(productPlatesList) || hasTubePurificationServiceSubmissions(tubesList);
		if (readOnly)
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "As the plate is already Purified the parameters can not be changed.");
		}
		// PurificationService Submission information is held at the well level.
		// gather all tubes for update via the dialog
		tubesList.addAll(getSelectedTubes(productPlatesList));
		PurificationServiceParameterDialog mPurificationServiceParameterDialog = new PurificationServiceParameterDialog(MasterController.getGUIComponent(), pageModel, tubesList, readOnly);
		mPurificationServiceParameterDialog.setVisible(true);
	}
	
	private boolean hasPlatePurificationServiceSubmissions(List<ProductPlate> plates) {
		if(plates != null) {
			for(ProductPlate plate : plates) {
				if(plate.isPurificationServiceSubmittedSuccessfully()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasTubePurificationServiceSubmissions(List<PlateWell<ProductBatchModel>> tubes) {
		if(tubes != null) {
			for(PlateWell<ProductBatchModel> tube : tubes) {
				if(tube.getBatch() != null && tube.getBatch().getRegInfo() != null && 
				   tube.getBatch().getRegInfo().isPurificationServiceSubmittedSuccessfully()) 
				{
					return true;
				}
			}
		}
		return false;
	}
//	private String createBarCodesMessageString(ArrayList<ProductPlate> combinedList) {
//		StringBuffer barCodes = new StringBuffer();
//
//		for (int index = 0; index < combinedList.size(); index++) {
//			if (index != 0) {
//				barCodes.append( ", " );
//			}
//			barCodes.append(combinedList.get(index).getPlateBarCode());
//		}
//		return barCodes.toString();
//	}
	
	/**
	 * Assume for BETA that this will only be called from the plate table view, so get the 
	 * selected plates from that view.
	 * @return the plates checked by the user.  How do we know this?  That's in the summary table.
	 */
	private List<ProductPlate> getSelectedPlates() {
		List<ProductPlate> selectedPlates = new ArrayList<ProductPlate>();
		PCeNTableModel tableModel = getCurrentTableViewTableModel();
	    PCeNTableModelConnector	connector = tableModel.getConnector();	
	    
	    if (connector instanceof PCeNRegistrationProductsTableModelConnector) { // non-plated batches (really want all batches, since a batch that is plated, can also have standalone tubes)
	      PseudoProductPlate ppp = new PseudoProductPlate(((PCeNRegistrationProductsTableModelConnector) connector).getAbstractBatches());
	      selectedPlates.add(ppp);
	    }
	    else {		
	  		connector = tableModel.getConnector();
	  	
	  		ProductPlate productPlate = ((PCeNRegistrationSingleProductPlateTableViewConnector) connector).getProductPlate();
	  		selectedPlates.add(productPlate);
	    }

		return selectedPlates;
	}
	
	/** 
	 * 
	 * @return ArrayList of product plates where all batches in the plate have been selected or an empty list
	 */
	private ArrayList<ProductPlate> getSelectedProductPlates() {
		ArrayList<ProductPlate> productPlateList = new ArrayList<ProductPlate>();
		if (TABLES_VIEW.equals(selectedView)) {
			for (ProductPlate productPlate : pageModel.getAllProductPlates()) {
				if (isAllBatchesSelected(productPlate)) {
					productPlateList.add(productPlate);
				} 
			}
		}
		return productPlateList;
	}
	
	private ArrayList<PlateWell<ProductBatchModel>> getSelectedNonPlatedProductTubesList() {
		ArrayList<PlateWell<ProductBatchModel>> nonPlatedTubesList = new ArrayList<PlateWell<ProductBatchModel>>();
		nonPlatedTubesList.addAll(getSelectedTubes(pageModel.getGuiPseudoProductPlate()));
		
		return nonPlatedTubesList;
	}
	
	/**
	 * There exists some business logic in this method that shouldn't be performed here.
	 * 
	 * @param screenPanelsFlag
	 * @return
	 */
	private ArrayList<ProductBatchModel> getSelectedNonPlatedBatches(boolean screenPanelsFlag) {
		ArrayList<ProductBatchModel> nonPlatedBatchesList = new ArrayList<ProductBatchModel>();
		//
		// the following logic enforces logic without knowing what the user selected when screenPanelsFlag == true
		//
		if (PLATES_VIEW.equals(selectedView)) {// PLATE_VIEW, PLATE_TABLE_VIEW, TABLE_VIEW
			PCeNRegistration_BatchDetailsPlateBatchViewPanel plateBatchViewPanel = 
				(PCeNRegistration_BatchDetailsPlateBatchViewPanel) plateRackViewTab.getSelectedComponent();
			ProductPlate productPlate = plateBatchViewPanel.getProductPlate();
			ProductBatchModel batchModel = getSelectedBatch(productPlate);
			if (screenPanelsFlag) // For submittal sets, batches need not be selected.  
			{
				batchModel = productPlate.getAllBatchesInThePlate()[0];
			}
			if (batchModel == null) {
				showNoBatchesSelectedErrorMessage("to Register");
				return null;
			} else {
				RegistrationValidator validator = new RegistrationValidator(pageModel, new ArrayList<String>());
				if (validator.validateBatchRegInfo(batchModel)) {
					//This flow is not known. When a single well is submitted for Registration, it should not go as a tube registration.
					//Accordingly change the nonPlatedBatchesList/platedBatchesList later. 					
					nonPlatedBatchesList.addAll(getSelectedBatches(productPlate));
				} else {
					return null;
				}
			}
		} else if (TABLES_VIEW.equals(selectedView)) {
			if (screenPanelsFlag) {
				PCeNTableModel tableModel = getCurrentTableViewTableModel();
				nonPlatedBatchesList.addAll(getSelectedBatches(tableModel.getConnector().getAbstractBatches()));
			} else {
				ArrayList<ProductPlate> tempSelectedPlates = new ArrayList<ProductPlate>();
				tempSelectedPlates.addAll(pageModel.getAllProductPlates());
				tempSelectedPlates.add(pageModel.getGuiPseudoProductPlate());
				for (ProductPlate productPlate : tempSelectedPlates) {
					if (productPlate instanceof PseudoProductPlate) {
						if (isAnySelectedBatch(productPlate))
							nonPlatedBatchesList.addAll(getSelectedBatches(productPlate));
					} else {
						if (isAnySelectedBatch(productPlate))
							nonPlatedBatchesList.addAll(getSelectedBatches(productPlate));
					}
				}
			}
		}
		
		return nonPlatedBatchesList;
	}

//	/**
//	 * 
//	 * @param screenPanelsFlag
//	 * @return an ArrayList whose members are [0] ArrayList<ProductPlate>, [1] ArrayList<ProductPlate> or ArrayList<ProductBatchModel> 
//	 */
//	private ArrayList<Serializable> getProductPlatesAndBatchesList(boolean screenPanelsFlag) {
//		ArrayList<Serializable> productPlatesAndBatchesList = new ArrayList<Serializable>();
//		ArrayList<Object> nonPlatedBatchesList = new ArrayList<Object>();
//		ArrayList<ProductPlate> productPlatesList = new ArrayList<ProductPlate>();
//		String comboText = selectedView;
//		if (comboText.equals(PLATES_VIEW)) {// PLATE_VIEW, PLATE_TABLE_VIEW, TABLE_VIEW
//
//			PCeNRegistration_BatchDetailsPlateBatchViewPanel plateBatchViewPanel = 
//				(PCeNRegistration_BatchDetailsPlateBatchViewPanel) plateRackViewTab.getSelectedComponent();
//			ProductPlate productPlate = plateBatchViewPanel.getProductPlate();
//			ProductBatchModel batchModel = getSelectedBatch(productPlate);
//			if (screenPanelsFlag) // For submittal sets, batches need not be selected.  
//			{
//				batchModel = productPlate.getAllBatchesInThePlate()[0];
//			}
//			if (batchModel == null) {
//				showNoBatchesSelectedErrorMessage("to Register");
//				return null;
//			} else {
//				RegistrationValidator validator = new RegistrationValidator(pageModel, new ArrayList<String>());
//				if (validator.validateBatchRegInfo(batchModel)) {
//					//This flow is not known. When a single well is submitted for Registration, it should not go as a tube registration.
//					//Accordingly change the nonPlatedBatchesList/platedBatchesList later. 					
//					nonPlatedBatchesList.add(productPlate);
//				} else {
//					return null;
//				}
//			}
//		} else if (comboText.equals(TABLES_VIEW)) {
//			PCeNTableModel tableModel = getCurrentTableViewTableModel();
//			if (screenPanelsFlag)
//				nonPlatedBatchesList.add(tableModel.getConnector().getAbstractBatches());
//			else
//			{
//				//selectedPlates.addAll(pageModel.getAllProductPlates());
//				ArrayList<ProductPlate> tempSelectedPlates = new ArrayList<ProductPlate>();
//				tempSelectedPlates.addAll(pageModel.getAllProductPlates());
//				tempSelectedPlates.add(pageModel.getGuiPseudoProductPlate());
//				ProductPlate productPlate = null; 
//				for (int i = 0; i < tempSelectedPlates.size(); i++) {
//					
//					productPlate = tempSelectedPlates.get(i);
//										
//					if (productPlate instanceof PseudoProductPlate) {
//						if (isAnySelectedBatch(productPlate))
//							nonPlatedBatchesList.add(productPlate);
//					} else if (isAllBatchesSelected(productPlate)) {
//						productPlatesList.add(productPlate);
//					} else {
//						if (isAnySelectedBatch(productPlate))
//							nonPlatedBatchesList.add(productPlate);
//					}
//				}
//			}
//		}
//		productPlatesAndBatchesList.add(productPlatesList);
//		productPlatesAndBatchesList.add(nonPlatedBatchesList);
//
//		return productPlatesAndBatchesList;
//	}

	/**
	 * If the model from the current view is of type PCeNTableModel
	 */
	private void refreshCurrentTableViewTableModel() {
		PCeNTableModel model = getCurrentTableViewTableModel();
		if(model != null) {
			model.fireTableDataChanged();
		}
	}
	/**
	 * 
	 * @return calls getModel() on the current view's scrollPane 
	 */
	private PCeNTableModel getCurrentTableViewTableModel() { 
		JPanel panel = (JPanel)((JPanel) productTableViewTab.getSelectedComponent()).getComponent(0);
		JScrollPane scrollPane = ((JScrollPane) panel.getComponent(1));
		PCeNRegistrationBatchInfoTableView tempTableView = (PCeNRegistrationBatchInfoTableView) scrollPane.getViewport().getView();
		return (PCeNTableModel) tempTableView.getModel();
	}
	
	private PCeNTableModel getTableModelForTab(String title) {
		for (int i = 0; i < productTableViewTab.getTabCount(); ++i) {
			String tabTitle = productTableViewTab.getTitleAt(i);
			if (StringUtils.equals(title, tabTitle)) {
				JPanel panel = (JPanel) ((JPanel)productTableViewTab.getComponent(i)).getComponent(0);
				JScrollPane scrollPane = ((JScrollPane) panel.getComponent(1));
				PCeNRegistrationBatchInfoTableView tempTableView = (PCeNRegistrationBatchInfoTableView) scrollPane.getViewport().getView();
				return (PCeNTableModel) tempTableView.getModel();
			}
		}
		return null;
	}
	
	private PCeNRegistrationBatchInfoTableView getCurrentTableView() { 
		JPanel panel = (JPanel)((JPanel) productTableViewTab.getSelectedComponent()).getComponent(0);
		JScrollPane scrollPane = ((JScrollPane) panel.getComponent(1));
		PCeNRegistrationBatchInfoTableView tempTableView = (PCeNRegistrationBatchInfoTableView) scrollPane.getViewport().getView();
		return tempTableView;
	}
	
	/**
	 * 
	 * @param productPlate
	 * @return true if any batch in the plate is selected
	 */
	private ProductBatchModel getSelectedBatch(ProductPlate productPlate) {
		List<ProductBatchModel> batchList = productPlate.getListOfProductBatches();
		for (ProductBatchModel batchModel : batchList) {
			if (batchModel.isSelectedForRegistration())
				return batchModel;
		}
		return null;
	}
	
	/**
	 * 
	 * @param productPlate
	 * @return List of batches where isSelected() == true
	 */
	private List<ProductBatchModel> getSelectedBatches(ProductPlate productPlate) {
		List<ProductBatchModel> selectedBatches = new ArrayList<ProductBatchModel>();
		List<ProductBatchModel> batchList = productPlate.getListOfProductBatches();
		for (ProductBatchModel batchModel : batchList) {
			if (batchModel.isSelectedForRegistration())
				selectedBatches.add(batchModel);
		}
		return selectedBatches;
	}
	
	/** 
	 * 
	 * @param productPlate
	 * @return a list of PlateWell only if contained batch isSelected() == true
	 */
	private List<PlateWell<ProductBatchModel>> getSelectedTubes(ProductPlate productPlate) {
		List<PlateWell<ProductBatchModel>> selectedTubes = new ArrayList<PlateWell<ProductBatchModel>>();
		if (productPlate != null) {
			for (PlateWell<ProductBatchModel> well : productPlate.getWells()) {
				if (well.getBatch() != null && well.getBatch().isSelectedForRegistration())
					selectedTubes.add(well);
			}
		}
		return selectedTubes;
	}
	
	/**
	 * 
	 * @param plates
	 * @return empty list or a list with tubes that have batches with isSelected() == true.
	 */
	private List<PlateWell<ProductBatchModel>> getSelectedTubes(List<ProductPlate> plates) {
		List<PlateWell<ProductBatchModel>> tubes = new ArrayList<PlateWell<ProductBatchModel>>();
		if (plates != null) {
			for(ProductPlate plate : plates) {
				tubes.addAll(getSelectedTubes(plate));
			}
		}
		return tubes;
	}
	/**
	 * 
	 * @param productPlate
	 * @return true if we find a single selected batch in the plate
	 */
	private boolean isAnySelectedBatch(ProductPlate productPlate) {
		List<ProductBatchModel> batchList = productPlate.getListOfProductBatches();
		for (ProductBatchModel batchModel : batchList) {
			if (batchModel.isSelectedForRegistration())
				return true;
		}
		return false;
	}

	private boolean isAllBatchesSelected(ProductPlate productPlate) {
		if (productPlate != null) {
			List<ProductBatchModel> batchList = productPlate.getListOfProductBatches();
			if (batchList != null) {
				for (ProductBatchModel batchModel : batchList) {
					if (!batchModel.isSelectedForRegistration()) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private void getRegistrationStatusForBatches() {
		try {
			List<ProductBatchModel> selectedBatches = pageModel.getAllProductBatchModelsInThisPage();
			
			if (selectedBatches != null && selectedBatches.size() == 0) {
				showNoBatchesSelectedErrorMessage(" to retrieve Registration information");
				return;
			}
			
			ProductBatchModel[] batchArray = selectedBatches.toArray(new ProductBatchModel[0]);	
			
			RegistrationHandler regHandler = new RegistrationHandler(pageModel);
			setCursor(Cursor.WAIT_CURSOR);
			String serverMessage = regHandler.getRegistrationStatusForBatches(batchArray);
			setCursor(Cursor.DEFAULT_CURSOR);

			pageModel.setModelChanged(true);
			
			NotebookPageGuiInterface page = MasterController.getGuiController().getLoadedPageGui(pageModel.getSiteCode(), pageModel.getNbRef(), pageModel.getVersion());
			if (page != null) {
				page.refreshPage();			
			}
						
			MasterController.getGuiComponent().repaint();
			
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), serverMessage, MasterController.getGUIComponent().getTitle(), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "submitBatchesForRegistration unexpected exception");
		}
	}
	
	protected void submitPlatesAndTubesForRegistration() {
		errorMap.clear();
		// Check for employee id
		String employeeID = MasterController.getUser().getCompoundManagementEmployeeId();
		if (StringUtils.isNotBlank(employeeID)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"Only Employees with Employee ID can register!");
			return;
		}
		//List selectedBatches = this.getSelectedBatchesForRegistration();
		
//		// Declare the potential lists and arrays
		ArrayList<ProductPlate> productPlatesList = getSelectedProductPlates();
		List<PlateWell<ProductBatchModel>> nonPlatedTubesList = getSelectedNonPlatedProductTubesList(); 
		List<ProductBatchModel> nonPlatedBatchesList = new ArrayList<ProductBatchModel>();
		RegistrationHandler regHandler = null;

		// TODO: verify we have something to process
		if (productPlatesList.isEmpty() == false)
		{	
			if (isAnyNonStandardPlates(productPlatesList))
			{
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
				                              "The selected plate is a Non-Standard type. Standard plates can only be registered.");
				return;
			}
		}
		if (nonPlatedTubesList.isEmpty() == false) {
			for(PlateWell<ProductBatchModel> tube : nonPlatedTubesList) {
				if(nonPlatedBatchesList.contains(tube.getBatch()) == false) {
					nonPlatedBatchesList.add(tube.getBatch());
				}
			}
		}
		
		if (productPlatesList.size() == 0
				&& nonPlatedBatchesList.size() == 0)
		{
			showNoBatchesSelectedErrorMessage("to Register");
			return;
		}

		regHandler = new RegistrationHandler(pageModel);

		ProductBatchModel[] nonPlatedBatches = nonPlatedBatchesList.toArray(new ProductBatchModel[]{});
		ProductPlate[] productPlates = productPlatesList.toArray(new ProductPlate[]{});
		
		//LinkedHashMap errorMap = new LinkedHashMap();
		if (productPlatesList.isEmpty() == false) {
			regHandler.validatePlatesForRegistration(productPlates, errorMap);
		} else if (nonPlatedBatchesList.isEmpty() == false) {
			regHandler.validateBatchesForRegistration(nonPlatedBatches, errorMap);
		}
		if (errorMap.isEmpty() == false) {
			this.displayErrorTable(errorMap, isPlateRackView(), false);
			return;
		}

/*		int result = JOptionPane.showConfirmDialog(MasterController
				.getGUIComponent(),
				"Selected Item(s) will be submitted for registration",
				"Plates and Batches Registration Confirmation",
				JOptionPane.YES_NO_OPTION);
		if (result != JOptionPane.YES_OPTION)
			return;
*/
		//RegistrationManagerDelegate regManager = ServiceController.getRegistrationManagerDelegate();
		Map<String, String> productPlatesMap = new HashMap();
		Map<String, String> nonPlatedBatchesMap = new HashMap();
		String serverErrorMessage = "";

		if (productPlatesList.isEmpty() == false) {
			try {
				setCursor(Cursor.WAIT_CURSOR);
				productPlatesMap = regHandler.submitPlatesForRegistration(productPlates, employeeID);
				setBatchStatus(productPlatesList, "REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			} catch (Exception e) {
				serverErrorMessage = "Submit to Registration of Product Plates failed due to : \n";
				serverErrorMessage += "GCR system is unable to process requests now." + "\n";
				log.error(serverErrorMessage, e);
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "submitBatchesForRegistration unexpected exception");
			}
		}
		if (nonPlatedBatchesList.isEmpty() == false) {
			try {
				setCursor(Cursor.WAIT_CURSOR);
				nonPlatedBatchesMap = regHandler.submitBatchesForRegistration(nonPlatedBatches, employeeID);
				setBatchStatus(nonPlatedBatches, "REGISTRATION",
						CeNConstants.REGINFO_SUBMITTED,
						CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			} catch (Exception e) {
				serverErrorMessage += "Submit to Registration of Batches failed due to : \n";
				serverErrorMessage += "Problem accessing/submitting batches to CompoundRegistration service.\n";
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(e, "submitBatchesForRegistration unexpected exception");
			}
		}

		setCursor(Cursor.DEFAULT_CURSOR);
		if (StringUtils.isNotBlank(serverErrorMessage) || productPlatesMap == null
				|| nonPlatedBatchesMap == null)
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"Submit to Registration Failed.\n " + serverErrorMessage);
		else
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"Submit to Registration completed");
		refreshCurrentTableViewTableModel();		
	}

	/**
	 * Indicate whether we have any user defined plates in the plate list
	 * @param productPlates
	 * @return
	 */
	private boolean isAnyNonStandardPlates(List<ProductPlate> productPlates) {
		if (productPlates != null) {
			for (ProductPlate plate : productPlates) {
				Container container = plate.getContainer();
				if (container != null && container.isUserDefined()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns Product Batches selected for a given set of product plates
	 * @param productPlatesList
	 * @return
	 */
	private ArrayList<ProductBatchModel> getSelectedBatches(List<ProductPlate> productPlatesList) {
		ArrayList<ProductBatchModel> selectedBatches = new ArrayList<ProductBatchModel>();
		for (ProductPlate plate : productPlatesList) {
			List<ProductBatchModel> batchesList = plate.getListOfProductBatches();
			for (ProductBatchModel batchModel : batchesList) {
				if (batchModel.isSelectedForRegistration())
					selectedBatches.add(batchModel);
			}
		}
		return selectedBatches;
	}
	
	protected void submitPlatesandTubesForCompoundManagementRegistration()
	{
	  errorMap.clear();
	  
		List<ProductPlate> selectedPlates = this.getSelectedPlates();
		ProductPlate[] productPlates = selectedPlates.toArray(new ProductPlate[] {});
		if(productPlates == null || productPlates.length == 0)
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
										"Please select plate or Tubes to be submmited to CompoundManagement.");
			return;
		}
		int platesSize = productPlates.length ;
		for (int i = 0; i< platesSize; i ++)
		{
			if(productPlates[i] instanceof PseudoProductPlate)
			{
				 // no need to check if all wells are selected.This is for tube submission.
				 this.submitTubesForCompoundManagementRegistration(productPlates[i]);	
				 break;
			}
		}
		
		//Now look for full Plate registration
		ArrayList<ProductPlate> fullPlateRegList = new ArrayList<ProductPlate>();
		for (int i = 0; i< platesSize; i ++)
		{
			if(productPlates[i] instanceof PseudoProductPlate == false)
			{
				fullPlateRegList.add(productPlates[i]);
			}
		}
		
		if(fullPlateRegList.isEmpty() == false)
		{
			submitPlatesForCompoundManagementRegistration(fullPlateRegList.toArray(new ProductPlate[]{}));
		}
	}
	
	protected void submitPlatesForCompoundManagementRegistration(ProductPlate[] productPlates ) {
		errorMap.clear();
		
		int platesSize = productPlates.length ;
		for (int i = 0; i< platesSize; i ++)
		{
						
			if (! this.isAllBatchesSelected(productPlates[i])) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
							"Partial selection of wells in the plate can not be submitted for CompoundManagement in this software release. \n"
							+ "Please select all wells in the plate to submit the plate to CompoundManagement.");
				return;
			}
		}
		
		RegistrationHandler regHandler = new RegistrationHandler(pageModel);
		
		if (! regHandler.isAllProductPlatesBarCoded(productPlates)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"Please generate Bar Codes for all Plates before CompoundManagement Submission");
			return;
		}
		
		if (! regHandler.isAllCompoundManagementContainers(productPlates)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"The selection includes Non CompoundManagement Container Plates that can not be Submitted for Plate Registration!");
			return;
		}
		
		regHandler.validatePlatesForCompoundManagementRegistration(productPlates, errorMap);
		if (!errorMap.isEmpty()) {
			this.displayErrorTable(errorMap, isPlateRackView(), false);
			return;
		} 
		// Plates validated so proceed with CompoundManagement submission
		setCursor(Cursor.WAIT_CURSOR);
		String serverErrorMessage = regHandler.submitPlatesForCompoundManagementRegistration(productPlates,this.pageModel.getProjectCode());
		setCursor(Cursor.DEFAULT_CURSOR);		
		if (!serverErrorMessage.equals(""))
		{
			JOptionPane pane = getNarrowOptionPane(50);
			pane.setMessage("Submit to container registration Failed.\n " + serverErrorMessage);
			pane.setMessageType(JOptionPane.ERROR_MESSAGE);
	        JDialog dialog = pane.createDialog(MasterController.getGUIComponent(), "Registration Status");
	        dialog.setVisible(true);
		}
		else
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
					"Submit to container registration completed.");
			refreshCurrentTableViewTableModel();
		}
	}

	// TODO -- There is no PENDING STATUS.  The result comes back immediately.
	protected void submitPlatesAndBatchesForScreening() {
		errorMap.clear();
//		ArrayList<Serializable> productPlatesAndBatchesList = null;
		ArrayList<ProductPlate> productPlatesList = getSelectedProductPlates();
		ArrayList<ProductBatchModel> nonPlatedBatchesList = getSelectedNonPlatedBatches(false);
		ProductPlate[] productPlates = null;
		ProductBatchModel[] nonPlatedBatches = null;

		if (productPlatesList.isEmpty() == false)
			productPlates = (ProductPlate[]) productPlatesList.toArray(new ProductPlate[] {});

		if (nonPlatedBatchesList.isEmpty() == false) {
			nonPlatedBatches = (ProductBatchModel[]) nonPlatedBatchesList.toArray(new ProductBatchModel[] {});
		}

		if (productPlatesList.size() == 0 && nonPlatedBatchesList.size() == 0) {
			showNoBatchesSelectedErrorMessage(" to Screen");
			return;
		}

		if (productPlatesList.isEmpty() == false) {
			RegistrationHandler regHandler = new RegistrationHandler(pageModel);
			regHandler.validatePlatesForScreening(productPlates, errorMap);
			if (errorMap.isEmpty()) {
				// Batches validated so proceed with CompoundRegistration submission
				//JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This is an entire plate submission and the results can be viewed in the Submission Summary section.");
				setCursor(Cursor.WAIT_CURSOR);
				String serverErrorMessage = regHandler.submitPlatesForScreening(productPlates);
				setCursor(Cursor.DEFAULT_CURSOR);
				if (!serverErrorMessage.equals(""))
				{
					JOptionPane pane = getNarrowOptionPane(50);
					pane.setMessage("Submit to screening failed.\n " + serverErrorMessage);
					pane.setMessageType(JOptionPane.ERROR_MESSAGE);
	                JDialog dialog = pane.createDialog(MasterController.getGUIComponent(), "Screening Status");
	                dialog.setVisible(true);
				}
				else
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
							"Submit to screening completed.");
			} 
		}
		if (nonPlatedBatchesList.isEmpty() == false) {
			RegistrationHandler regHandler = new RegistrationHandler(pageModel);
			regHandler.validateBatchesForScreening(nonPlatedBatches, errorMap);
			if (errorMap.isEmpty()) {
				// Batches validated so proceed with CompoundRegistration submission
				setCursor(Cursor.WAIT_CURSOR);
				String serverErrorMessage = regHandler.submitBatchesForScreening(nonPlatedBatches);
				setCursor(Cursor.DEFAULT_CURSOR);
				if (StringUtils.isNotBlank(serverErrorMessage)) {
					JOptionPane pane = getNarrowOptionPane(50);
					pane.setMessage("Submit to screening failed.\n " + serverErrorMessage);
					pane.setMessageType(JOptionPane.ERROR_MESSAGE);
	                JDialog dialog = pane.createDialog(MasterController.getGUIComponent(), "Screening Status");
	                dialog.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Submit to screening completed");
				}				
			} 
		} 
		if (errorMap.isEmpty() == false) {
			this.displayErrorTable(errorMap, isPlateRackView(), false);
		} 
		refreshCurrentTableViewTableModel();
	}

	private boolean isPlateRackView() {
		boolean isPlateRackView = false;
		if (selectedView.equals(PLATES_VIEW))
			isPlateRackView = true;
		return isPlateRackView;
	}

	private void showNoBatchesSelectedErrorMessage(String process) {
		JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
				"No Plates/Batches are selected " + process + "!");
	}

	// TODO -- There is no PENDING STATUS.  The result comes back immediately.
	protected synchronized void submitPlatesAndWellsForPurification() {
		errorMap.clear();
		ArrayList<ProductPlate> productPlatesList = getSelectedProductPlates();
		ArrayList<PlateWell<ProductBatchModel>> nonPlatedTubesList = getSelectedNonPlatedProductTubesList();
		ProductPlate[] productPlates = null;


		if (productPlatesList.isEmpty() == false)
			productPlates = productPlatesList.toArray(new ProductPlate[] {});

		if (productPlatesList.size() == 0 && nonPlatedTubesList.size() == 0) {
			showNoBatchesSelectedErrorMessage("to Purify");
			return;
		}

		if (productPlatesList.isEmpty() == false) {
			RegistrationHandler regHandler = new RegistrationHandler(pageModel);
			regHandler.validatePlatesForPurification(productPlates, errorMap);
			if (errorMap.isEmpty() == false) {
				this.displayErrorTable(errorMap, isPlateRackView(), false);
				return;
			} 
			// Batches validated so proceed with Purification submission
			//JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This is an entire plate submission and the results can be viewed in the Submission Summary section.");
			setCursor(Cursor.WAIT_CURSOR);
			String serverErrorMessage = regHandler.submitPlatesForPurification(productPlates);
			setCursor(Cursor.DEFAULT_CURSOR);
			if (StringUtils.isNotBlank(serverErrorMessage))
			{
				JOptionPane pane = getNarrowOptionPane(50);
				pane.setMessage("Submit to purification failed.\n " + serverErrorMessage);
				pane.setMessageType(JOptionPane.ERROR_MESSAGE);
                JDialog dialog = pane.createDialog(MasterController.getGUIComponent(), "Purification Status");
                dialog.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
											  "Submit to purification completed.");
			}
		}
		if (nonPlatedTubesList.isEmpty() == false) {
			RegistrationHandler regHandler = new RegistrationHandler(pageModel);
			regHandler.validateTubesForPurification(nonPlatedTubesList, errorMap);
			if (!errorMap.isEmpty()) {
				this.displayErrorTable(errorMap, isPlateRackView(), false);
				return;
			} 
			// Batches validated so proceed with CompoundRegistration submission
			setCursor(Cursor.WAIT_CURSOR);
			String serverErrorMessage = regHandler.submitTubesForPurification(nonPlatedTubesList.toArray(new PlateWell[]{}));
			setCursor(Cursor.DEFAULT_CURSOR);
			if (StringUtils.isNotBlank(serverErrorMessage))
			{
				JOptionPane pane = getNarrowOptionPane(50);
				pane.setMessage("Submit to purification failed.\n " + serverErrorMessage);
				pane.setMessageType(JOptionPane.ERROR_MESSAGE);
                JDialog dialog = pane.createDialog(MasterController.getGUIComponent(), "Purification Status");
                dialog.setVisible(true);
			}							
			else
			{
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Submit to purification completed. Please check the Batch Plates and Tables section for the results.");
			}
		}
		refreshCurrentTableViewTableModel();
	}

	private void setBatchStatus(List<ProductPlate> plates, String purpose, String reginfoSubmitted, String compoundRegistrationJobStatusPending) {
		if (plates != null) {
			for (ProductPlate plate : plates) {
				if (plate != null) {
					ProductBatchModel[] batches = plate.getAllBatchesInThePlate();
					setBatchStatus(batches, purpose, reginfoSubmitted, compoundRegistrationJobStatusPending);
				}
			}
		}
	}

	private void setBatchStatus(ProductBatchModel[] batches, String purpose, String submissionStatus, String status) {
		if (batches != null) {
			for (ProductBatchModel batch : batches) {
				if (batch != null && batch.getRegInfo() != null) {
					batch.setSelectedForRegistration(false);
					BatchRegInfoModel regInfo = batch.getRegInfo();
					if (StringUtils.equals(purpose, "REGISTRATION")) {
						regInfo.setRegistrationStatus(submissionStatus + " - " + status);
						regInfo.setSubmissionStatus(submissionStatus);
						regInfo.setStatus(status);
					}
					if (StringUtils.equals(purpose, "PLATE REGISTRATION")) {
						regInfo.setCompoundManagementStatus(submissionStatus + " - " + status);
					}
					if (StringUtils.equals(purpose, "PURIFICATION")) {
						regInfo.setPurificationServiceStatus(submissionStatus + " - " + status);
					}
					if (StringUtils.equals(purpose, "SCREENING")) {
						regInfo.setCompoundAggregationStatus(submissionStatus + " - " + status);
					}
				}
			}
			refreshCurrentTableViewTableModel();
		}
	}

	/**
	 * Perhaps used in parallel interface but disabled for the moment 
	 * @param batchPlates
	 * @return Array of all PlateWell objects for any selected batches in the plate
	 */
	private PlateWell<ProductBatchModel>[] getPlateWells(List<ProductPlate> batchPlates) {
		ArrayList<PlateWell<ProductBatchModel>> PlateWellsList = new ArrayList<PlateWell<ProductBatchModel>>();
		for (ProductPlate productPlate : batchPlates) {
			List<ProductBatchModel> batchesList = productPlate.getListOfProductBatches();
			for (ProductBatchModel batchModel : batchesList) {
				if (batchModel.isSelectedForRegistration())
					PlateWellsList.addAll(productPlate.getPlateWellsforBatch(batchModel));
			}
		}
		return PlateWellsList.toArray(new PlateWell[]{});
	}

//	public void plateSelectionChanged(PlateSelectionChangedEvent event) {
//		enableSubmitAndRetrieveBtn(false); // force it to be on for now
////		this.selectedPlates.clear();
////		this.selectedPlates.addAll(event.getAllSelectedPlates());
////		// System.out.println(event.getAllPlates().size());
//		updateSelectedPlatesProducts(event.getAllSelectedPlates());
//	}

	/** Find CheckBoxIcon position in tab list */
	/*
	private int getCheckBoxPosition(CeNCheckBoxIcon icon) {
		int position = -1;
		for (int i = 0; i < productTableViewTab.getTabCount(); ++i) {
			if (icon.getCommand().equals(productTableViewTab.getTitleAt(i))) {
				position = i;
				break;
			}
		}
		return position;
	}
*/	
	/** Updates all checkboxes in tabs depending on checkboxes in batches */
	public void updateCheckBoxes() {
		if (productTableViewTab != null) {
			for (CeNCheckBoxIcon icon : checkBoxIconMap.values()) {
				PCeNTableModel tableModel = getTableModelForTab(icon.getCommand());
				PCeNTableModelConnector connector = tableModel.getConnector();
				if (connector.getRowCount() > 0) {
					boolean allSelected = true;
					for (int i = 0; i < connector.getRowCount(); ++i) {
						ProductBatchModel model = (ProductBatchModel) connector.getBatchModel(i);
						if (!model.isSelectedForRegistration()) {
							allSelected = false;
							break;
						}
					}
					icon.setSelected(allSelected);
				} else {
					icon.setSelected(false);
				}
			}
			productTableViewTab.repaint();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
				
		String command = e.getActionCommand();
		if (command.equals(TABLES_VIEW) || command.equals(PLATES_VIEW)) {
			selectedView = command;
			switchView(selectedView);
			updateCheckBoxes();
			return;
		}

		Object source = e.getSource();
		if (source instanceof CeNCheckBoxIcon) {
			CeNCheckBoxIcon icon = (CeNCheckBoxIcon) source;
//			String tabTitle = icon.getCommand();

			PCeNTableModel tableModel = getCurrentTableViewTableModel();
			PCeNRegistrationAbstractTableModelConnector connector = (PCeNRegistrationAbstractTableModelConnector) tableModel.getConnector();
			
			connector.updateColumn(PCeNTableView.SELECT, String.valueOf(icon.isSelected()).toLowerCase());
			
			updateCheckBoxes();
			
			return;
		}		
			
		final String progressStatus = "Saving page and submitting...";			
		SwingWorker workflowWorker = new SwingWorker() {
			public Object construct() {
				log.info("Submit to registration start");
				enableSubmitAndRetrieveBtn(false);
				CeNJobProgressHandler.getInstance().addItem(progressStatus);
				
				isSubmitting = true;
				
				if (checkBoxList != null) {
					boolean someSelected = false;
					for (JCheckBox box : checkBoxList) {
						if (box.isSelected()) {
							someSelected = true;
							break;
						}
					}
					if (someSelected == false) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please select the workflow to submit.");
						return null;
					}
				}
					
				ArrayList<String> workflowList = new ArrayList<String>();
										
				if (registerCheckBox.isSelected()) {
					workflowList.add(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION);
					registerCheckBox.setSelected(false);
				} 
				if (registerPlateCheckBox.isSelected()) {
					workflowList.add(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT);
					registerPlateCheckBox.setSelected(false);
				} 
				if (purificationCheckBox.isSelected()) {
					workflowList.add(CeNConstants.WORKFLOW_PURIFICATION_SERVICE);
					purificationCheckBox.setSelected(false);
				} 
				if (quantitativeQCCheckBox.isSelected()) {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This functionality is Under way!");
					quantitativeQCCheckBox.setSelected(false);
				} 
				if (screeningCheckBox.isSelected()) {
					workflowList.add(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION);
					screeningCheckBox.setSelected(false);
				}
							
				PCeNTableModel tableModel = getCurrentTableViewTableModel();
				PCeNRegistrationAbstractTableModelConnector connector = (PCeNRegistrationAbstractTableModelConnector) tableModel.getConnector();
								
				if (connector instanceof PCeNRegistrationProductsTableModelConnector) {
					((PCeNRegistrationProductsTableModelConnector)connector).setPendingStatusesWhileSaving(true);
					tableModel.fireTableDataChanged();
				}
				
				if (MasterController.getGUIComponent().isSavePageButtonEnabled()) {
					MasterController.getGUIComponent().jButtonSavePageActionPerformed(null);
					if (MasterController.getGUIComponent().isSavePageButtonEnabled()) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
						                              "Selected items will not be submitted for registration as save failed. Please try again later.", 
						                              "Unexpected error", JOptionPane.ERROR_MESSAGE);
						return null;
					}
				}		
				
				try{					
					sendAllToRegistration(workflowList);
				} catch (Throwable e) {
					log.error("Error submitting batches and plates to registration:", e);
					enableSubmitAndRetrieveBtn(pageModel.isEditable());
					isSubmitting = false;
				}
				
				if (connector instanceof PCeNRegistrationProductsTableModelConnector) {
					((PCeNRegistrationProductsTableModelConnector)connector).setPendingStatusesWhileSaving(false);
					tableModel.fireTableDataChanged();
				}
				
				return null;
			}
			public void finished() {
				enableSubmitAndRetrieveBtn(pageModel.isEditable());
				isSubmitting = false;
				updateCheckBoxes();
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);	
				log.info("Submit to registration end");				
			}
		};
		
		workflowWorker.start();
	}

	private void sendAllToRegistration(List<String> workflowList) {
		log.info("sendAllToRegistration(List) start");
		log.info("Workflows to submit: " + workflowList.toString());
		
		errorMap.clear();
		// Ensure user has a valid CompoundManagement employee ID.
		String employeeID = MasterController.getUser().getCompoundManagementEmployeeId();
		if (StringUtils.isBlank(employeeID)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Only Employees with Employee ID can register!");
			return;
		}
		
		// Ensure we have items to submit
		//This method return Plates and Pseudo-Plates (in case of CompoundManagement Tube and Vial registration)
		List<ProductPlate> productPlatesList = getSelectedProductPlates();
		List<ProductBatchModel> nonPlatedBatchesList = getSelectedNonPlatedBatches(false);
		List<PlateWell<ProductBatchModel>> nonPlatedTubesList = getSelectedNonPlatedProductTubesList();
		
		if (productPlatesList.isEmpty() == false && isAnyNonStandardPlates(productPlatesList))
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
			                              "The selected plate is a Non-Standard type. Standard plates can only be registered.");
			return;
		}
		ProductBatchModel[] nonPlatedBatches = null;
		// why are we checking intended batches if there are no actual batches?
		if (nonPlatedBatchesList.isEmpty()) {
			BatchesList<ProductBatchModel> batchesList = pageModel.getExistingSyncIntendedBatches();
			if (batchesList != null) {
				List<ProductBatchModel> intendedBatches = batchesList.getBatchModels();
				if (intendedBatches != null) {
					nonPlatedBatchesList.addAll(intendedBatches);
				}
			}
		} else {
			nonPlatedBatches = nonPlatedBatchesList.toArray(new ProductBatchModel[] {});
		} 
		
		if (nonPlatedBatchesList.isEmpty() && productPlatesList.isEmpty()) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
			                              "Please select batches " + 
			                              (pageModel.isParallelExperiment() ? "and plates " : "") + 
			                              "for registration.");
			return;
		}
				
		log.info("Check batches to see if they are ready for registration start");
		
		RegistrationHandler regHandler = new RegistrationHandler(pageModel, workflowList);
		ProductPlate[] productPlates = productPlatesList.toArray(new ProductPlate[]{});
		// Check that batches are ready for CompoundRegistration...
		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
			if (productPlatesList.isEmpty() == false) {
				regHandler.validatePlatesForRegistration(productPlates, errorMap);
			} 
			if (ArrayUtils.isEmpty(nonPlatedBatches) == false) {
				regHandler.validateBatchesForRegistration(nonPlatedBatches, errorMap);
			}
		}
		
		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
			if (productPlatesList.isEmpty() == false) {
				regHandler.validatePlatesForCompoundManagementRegistration(productPlates, errorMap);
			}
			if (nonPlatedTubesList.isEmpty() == false) {
				regHandler.validateTubesForCompoundManagementRegistration(nonPlatedTubesList, errorMap);
			}
		}
		
		if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
			if (productPlatesList.isEmpty() == false) {
				regHandler.validatePlatesForPurification(productPlates, errorMap);
			}
			if (nonPlatedTubesList.isEmpty() == false) {
				regHandler.validateTubesForPurification(nonPlatedTubesList, errorMap);
			}
		}

		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
			if (productPlatesList.isEmpty() == false) {
				regHandler.validatePlatesForScreening(productPlates, errorMap);
			}
			if (ArrayUtils.isEmpty(nonPlatedBatches) == false) {
				regHandler.validateBatchesForScreening(nonPlatedBatches, errorMap);
			}
		}
		
		if (errorMap.isEmpty() == false) {
			this.displayErrorTable(errorMap, isPlateRackView(), false);
			return;
		}
		
		log.info("Check batches to ready for registration end");
		
		// Submit
		
		List<ProductBatchModel> allBatches = new Vector<ProductBatchModel>();
		
		String[] batchKeys = new String[0];
		if (nonPlatedBatches != null) {
			batchKeys = new String[nonPlatedBatches.length];
			for (int i = 0; i < nonPlatedBatches.length; ++i) {
				if (nonPlatedBatches[i] != null) {
					batchKeys[i] = nonPlatedBatches[i].getKey();
					allBatches.add(nonPlatedBatches[i]);
				}
			}
		}
		
		String[] plateKeys = new String[0];
		if (productPlates != null) {
			plateKeys = new String[productPlates.length];
			for (int i = 0; i < productPlates.length; ++i) {
				if (productPlates[i] != null) {
					plateKeys[i] = productPlates[i].getKey();
					ProductBatchModel[] plateBatches = productPlates[i].getAllBatchesInThePlate();
					if (plateBatches != null) {
						allBatches.addAll(Arrays.asList(plateBatches));
					}
				}
			}
		}
				
		String[] workflows = workflowList.toArray(new String[workflowList.size()]);
		log.info("Submitting " + batchKeys.length + " non plated batches and " + plateKeys.length + " plates");
		try {
			setBatchStatuses(workflowList, nonPlatedBatches, productPlatesList);
			
			log.info("Submit batches and plates for registration start");
			String jobId = regHandler.submitBatchesAndPlatesForRegistration(batchKeys, plateKeys, workflows, pageModel, MasterController.getUser().getSessionIdentifier());
			log.info("Submit batches and plates for registration end");
			
			TimerStatusHandler.getInstance().addRegistrationTask(pageModel.getNotebookRefAsString(), jobId);
			
			refreshCurrentTableViewTableModel();
		} catch (Exception e) {
			log.error("Error submitting batches and plates:", e);
		}
		log.info("sendAllToRegistration(List) end");
	}

	private void setBatchStatuses(List<String> workflowList, ProductBatchModel[] nonPlatedBatchesAndOtherBatches, List<ProductPlate> productPlates) {
		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_REGISTRATION)) {
			setBatchStatus(productPlates, "REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			setBatchStatus(nonPlatedBatchesAndOtherBatches, "REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
		} 
		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_MANAGEMENT)) {
			setBatchStatus(productPlates, "PLATE REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			setBatchStatus(nonPlatedBatchesAndOtherBatches, "PLATE REGISTRATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);			
		}
		if (workflowList.contains(CeNConstants.WORKFLOW_PURIFICATION_SERVICE)) {
			setBatchStatus(productPlates, "PURIFICATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			setBatchStatus(nonPlatedBatchesAndOtherBatches, "PURIFICATION", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);			
		}
		if (workflowList.contains(CeNConstants.WORKFLOW_COMPOUND_AGGREGATION)) {
			setBatchStatus(productPlates, "SCREENING", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);
			setBatchStatus(nonPlatedBatchesAndOtherBatches, "SCREENING", CeNConstants.REGINFO_SUBMITTED, CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING);			
		}
	}

	protected void setCursor(int cursor) {
		if (viewBatch_Pane.getCursor().getType() != cursor)
			viewBatch_Pane.setCursor(Cursor.getPredefinedCursor(cursor));
	}

	class Retriever implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			SwingWorker submitWorker = new SwingWorker() {
				public Object construct() {
					retrieveRegInfoBtn.setEnabled(false);
//					pullRegistrationDetails();
					getRegistrationStatusForBatches();
					return null;
				}

				public void finished() {
					retrieveRegInfoBtn.setEnabled(pageModel.isEditable());
				}
			};
			submitWorker.start();
		}
	}
	
	
	protected void pullRegistrationDetails() {
		int count = 0;
		// the following two collections will not be null
		ArrayList<ProductPlate> productPlatesList = getSelectedProductPlates();
		ArrayList<ProductBatchModel> nonPlatedBatchesList = getSelectedNonPlatedBatches(false);
		
		ProductPlate[] productPlates = null;
		ProductBatchModel[] nonPlatedBatchesAndOtherBatches = null;
		List<ProductBatchModel> allProductPlateBatchModels = new ArrayList<ProductBatchModel>();
		
		if (productPlatesList.isEmpty() == false)
		{	
			if (isAnyNonStandardPlates(productPlatesList))
			{
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The selected plate is a Non-Standard type plate. Only standard plates can be registered and informations can be retrieved.");
				return;
			}
			List<ProductBatchModel> tempBatchModelList = new ArrayList<ProductBatchModel>();;
			for (ProductPlate plate : productPlatesList)
			{
				tempBatchModelList.clear();
				tempBatchModelList.addAll(Arrays.asList(plate.getAllBatchesInThePlate()));
				for(ProductBatchModel batch : tempBatchModelList) {
					if(allProductPlateBatchModels.contains(batch) == false) {
						allProductPlateBatchModels.add(batch);
					}
				}
			}
		}
		
		if (nonPlatedBatchesList.isEmpty() == false) {
			nonPlatedBatchesAndOtherBatches = (ProductBatchModel[]) nonPlatedBatchesList.toArray(new ProductBatchModel[] {});
		}

		if (productPlatesList.size() == 0 && nonPlatedBatchesList.size() == 0) {
			showNoBatchesSelectedErrorMessage(" to retrieve Registeration information");
			return;
		}
		// Need a single collection with all product batch models 
		List<ProductBatchModel> combinedProductBatchModels = new ArrayList<ProductBatchModel>();
		combinedProductBatchModels.addAll(allProductPlateBatchModels);
		combinedProductBatchModels.addAll(nonPlatedBatchesList);
		// Pull those that have passed into a final list
		ArrayList<ProductBatchModel> finalProductBatchModelsList = new ArrayList<ProductBatchModel>();
		for (ProductBatchModel regBatch : combinedProductBatchModels)
		{
			if (regBatch.getRegInfo().getCompoundRegistrationStatus() == null ||
			    regBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS) == false) 
			{
				finalProductBatchModelsList.add(regBatch);
			}
		}
		ProductBatchModel[] finalProductBatchModels = finalProductBatchModelsList.toArray(new ProductBatchModel[finalProductBatchModelsList.size()]);
		
		ProductBatchModel[] result = null;
		try {
			NotebookUser user = MasterController.getUser();
			RegistrationManagerDelegate regManager = ServiceController.getRegistrationManagerDelegate(user.getSessionIdentifier(),
			                                                                                          user.getCompoundManagementEmployeeId());
			result = regManager.getRegistrationInformation(finalProductBatchModels);
		} catch (Exception e1) {
			log.error("Failed to register final product batch models!", e1);
		}
		if (result == null)
			return;
		//Update the result set.
		for (int i=0; i<result.length && result[i] == finalProductBatchModels[i]; i++)
		{
			finalProductBatchModels[i].setParentBatchNumber(result[i].getParentBatchNumber());
			finalProductBatchModels[i].getCompound().setRegNumber(result[i].getCompound().getRegNumber());
			finalProductBatchModels[i].setConversationalBatchNumber(result[i].getConversationalBatchNumber());
			finalProductBatchModels[i].getRegInfo().setBatchTrackingId(result[i].getRegInfo().getBatchTrackingId());
			try {
				finalProductBatchModels[i].getRegInfo().setRegistrationDate(result[i].getRegInfo().getRegistrationDate());
			} catch (Exception e) {  //ignored
				log.debug("ignored: Failed to set registrationDate for ProductBatchModel: " + finalProductBatchModels[i].getBatchNumberAsString(), e);
			}
			finalProductBatchModels[i].getRegInfo().setCompoundRegistrationStatus(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED);
			finalProductBatchModels[i].getRegInfo().setErrorMsg("");
		}
		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "" + count + " Record(s) updated.");
	}
	
	public void compoundRemoved(CompoundCreationEvent event) {
		if (tablesRadioButton == null || tablesRadioButton.isSelected())
		{
			PCeNRegistrationProductsTableModelConnector controller = (PCeNRegistrationProductsTableModelConnector)lastStepProductTableView.getConnector(); 
			controller.removeProductBatchModel((ProductBatchModel)event.getBatch());
			((PCeNTableModel)lastStepProductTableView.getModel()).fireTableDataChanged();
		}
		else
		{
//			PCeNProductTableModelConnector controller = (PCeNProductTableModelConnector)nonPlatedBatchesTableView.getConnector(); 
//			 controller.removeProductBatchModel((ProductBatchModel)event.getBatch());
//			 ((PCeNTableModel)nonPlatedBatchesTableView.getModel()).fireTableDataChanged();
		}
	}

	public void compoundUpdated(CompoundCreationEvent event) {
		int selectedRow = getCurrentTableView().getSelectedRow();
		BatchModel updatedBatch = event.getBatch();
		StoicModelInterface selectedBatch = getCurrentTableViewTableModel().getConnector().getBatchModel(selectedRow);
		
		updatePlates();
		
		if (updatedBatch != null && updatedBatch == selectedBatch) {
			batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, updatedBatch));
		} else {
			batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, selectedBatch));
		}
		
		if (selectedRow > -1) {
			getCurrentTableView().setRowSelectionInterval(selectedRow, selectedRow);
		}
	}

	public void newCompoundCreated(CompoundCreationEvent event) {
		//this.refreshNonPlatedBatches();
		this.refreshProductBatchesAfterCompoundCreated(event);
		//updateSelectedPlatesProducts(selectedPlates);
	}
	
	private void refreshProductBatchesAfterCompoundCreated(CompoundCreationEvent event) {
		ProductBatchModel batch = (ProductBatchModel) event.getBatch();
		
		if (lastStepProductTableView == null ) {
		  return;
		}
		PCeNTableModel productsModel = (PCeNTableModel) lastStepProductTableView.getModel();
		PCeNRegistrationProductsTableModelConnector lastStepProductConnector = (PCeNRegistrationProductsTableModelConnector)lastStepProductTableView.getConnector();
		lastStepProductConnector.addProductBatchModel(batch);
		productsModel.fireTableDataChanged();
	}
	
	private void displayErrorTable(LinkedHashMap<ProductBatchModel, String> errorMap, boolean isPlateRackView, boolean selectFlag) {
		for (PCeNRegistrationBatchInfoTableView tableView : tableList) {
			((PCeNTableModel)tableView.getModel()).fireTableDataChanged();
		}

		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "There are errors in submission. Please check the Pre-Conditions column to correct it.", "Submission failed", JOptionPane.ERROR_MESSAGE);
	}

	public void batchSelectionChanged(BatchSelectionEvent event) {
		updateCheckBoxes();
	}
	
	public static void removeBatchFrommErrorMap(ProductBatchModel batchModel)
	{
		errorMap.remove(batchModel);
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		if (pageModel != pageModel2)
			this.pageModel = pageModel2;
		if (!pageModel.isEditable())
			setButtonsEditable(false);		
	}
	

	protected void submitTubesForCompoundManagementRegistration(ProductPlate pseudoProdPlate) {
		
		//get the selected PlateWells(batches)
		PlateWell allWells[] = pseudoProdPlate.getWells();
		ArrayList<PlateWell<ProductBatchModel>> selectedWellList = new ArrayList<PlateWell<ProductBatchModel>>();
		ProductBatchModel batchModel = null;
		for (int j = 0; j < allWells.length; j++) {
			if(allWells[j] == null || allWells[j].getBatch() == null) continue;
			batchModel = (ProductBatchModel) allWells[j].getBatch();
			if (batchModel.isSelectedForRegistration())
			{
				selectedWellList.add(allWells[j]);
			}
		}
		
		if (selectedWellList.size() == 0) {
		  log.info("No standalone tubes selected for CompoundManagement registration");
		  return;
		}
	
		PlateWell<ProductBatchModel>[] selectedWells = selectedWellList.toArray(new PlateWell[]{});
		
		//now validate PlateWell for tube related data
		
		//submit tubes for CompoundManagement registration
		
		//update the appropriate batch status
		
        RegistrationHandler regHandler = new RegistrationHandler(pageModel);
		
//		if (! regHandler.isAllTubesBarCoded(selectedWells)) {
    if (! regHandler.isAllTubesBarCodedForCompoundManagementSubmission(selectedWells)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"Please enter Bar Codes for all Tubes before CompoundManagement Submission");
			return;
		}
    
    // filter Vials (submitted via CompoundRegistration) prior to submission to CompoundManagement (on server)
		
//    if (! regHandler.isAllCompoundManagementTubeContainers(selectedWells)) {
		if (! regHandler.isAllCompoundManagementTubeContainersForCompoundManagementSubmission(selectedWells)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"The selection includes non-CompoundManagement container tubes that can not be submitted for container registration. Please select valid container type.");
			return;
		}
		
		regHandler.validateTubesForCompoundManagementRegistration(selectedWellList, errorMap);
		if (!errorMap.isEmpty()) {
			this.displayErrorTable(errorMap, isPlateRackView(), false);
			return;
		} 
		// Plates validated so proceed with CompoundManagement submission
		setCursor(Cursor.WAIT_CURSOR);
		String serverErrorMessage = regHandler.submitTubesForCompoundManagementRegistration(selectedWells, pageModel.getSiteCode());
		setCursor(Cursor.DEFAULT_CURSOR);		
		if (!serverErrorMessage.equals(""))
		{
			JOptionPane pane = getNarrowOptionPane(50);
			pane.setMessage("Submit to container registration Failed.\n " + serverErrorMessage);
			pane.setMessageType(JOptionPane.ERROR_MESSAGE);
	        JDialog dialog = pane.createDialog(MasterController.getGUIComponent(), "Registration Status");
	        dialog.setVisible(true);
		}
		else
		{
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), 
					"Submit to container registration completed.");
			refreshCurrentTableViewTableModel();
		}
	}
	
	/**
	 * Automatic set selection for batches that ready for registration (asynchronous)
	 * @param pageModel current page model
	 * @param submitButton submit button to disable it while selecting
	 */
	public void setBatchesSelected() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				setBatchesSelectedThread();
			}
		});
		thread.start();
	}

	private synchronized void setBatchesSelectedThread() {
		if (submitBtn != null) {
			submitBtn.setEnabled(false);
		}
		List<ProductBatchModel> batches = pageModel.getAllProductBatchModelsInThisPage();
//		RegistrationValidator validator = new RegistrationValidator(pageModel, new Vector<String>());
		for (ProductBatchModel batch : batches) {
			if (batch != null && batch.getRegInfo() != null) {
				if (validateBatchForCompoundRegistration(batch)) {
					batch.setSelectedForRegistration(true);
				} else {
					batch.setSelectedForRegistration(false);
				}
				refreshProductsView();
			}
		}
		if (submitBtn != null) {
			submitBtn.setEnabled(pageModel.isEditable());
		}
		updateCheckBoxes();
	}

	private boolean validateBatchForCompoundRegistration(ProductBatchModel batch) {
		if (batch != null && batch.getRegInfo() != null) {
			RegistrationValidator validator = new RegistrationValidator(pageModel, new Vector<String>());
			return validator.validateBatchRegInfo(batch, new LinkedHashMap<ProductBatchModel, String>()) && 
						!StringUtils.equals(batch.getRegInfo().getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED) &&
						!StringUtils.equals(batch.getRegInfo().getCompoundRegistrationStatus(), CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING) &&
						!StringUtils.equals(ProductBatchStatusMapper.getInstance().getCombinedProductBatchStatus(batch), CeNConstants.FAIL_CONTINUE) && 
						!StringUtils.equals(ProductBatchStatusMapper.getInstance().getCombinedProductBatchStatus(batch), CeNConstants.FAIL_DISCONTINUE);
		}
		return false;
	}
	
	private void refreshProductsView() {
		if (productViews != null) {
			for (JPanel productPanel : productViews.values()) {
				if (productPanel != null) {
					productPanel.repaint();
				}
			}
		}
	}

	private JOptionPane getNarrowOptionPane(int maxCharactersPerLineCount) {
		// Our inner class definition
		class NarrowOptionPane extends JOptionPane {
			private static final long serialVersionUID = -3550013548270706358L;
			int maxCharactersPerLineCount;

			NarrowOptionPane(int maxCharactersPerLineCount) {
				this.maxCharactersPerLineCount = maxCharactersPerLineCount;
			}

			public int getMaxCharactersPerLineCount() {
				return maxCharactersPerLineCount;
			}
		}

		return new NarrowOptionPane(maxCharactersPerLineCount);
	}
}
