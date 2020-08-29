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
package com.chemistry.enotebook.client.gui.page.experiment;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.Gui;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePaneEvent;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePaneListener;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNLabel;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.client.gui.page.batch.ParallelCeNMonomerPlateBuilder;
import com.chemistry.enotebook.client.gui.page.batch.ParallelCeNProductPlateBuilder;
import com.chemistry.enotebook.client.gui.page.batch.PlateCreationHandler;
import com.chemistry.enotebook.client.gui.page.batch.events.*;
import com.chemistry.enotebook.client.gui.page.experiment.plate.*;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.experiment.table.MonomerPlateTableViewPanel;
import com.chemistry.enotebook.client.gui.page.experiment.table.PCeNMonomer_TableView;
import com.chemistry.enotebook.client.gui.page.experiment.table.ProductPlateTableViewPanel;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNCompoundManagementProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNMonomerPlatesTableModelConnector;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNMonomerReactantsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.experiment.table.connector.PCeNProductPlatesTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.*;
import com.chemistry.enotebook.client.integration.compoundmanagementorderingapi.CompoundManagementOrderingHandler;
import com.chemistry.enotebook.client.integration.compoundmanagementorderingapi.OrderDeliverable;
import com.chemistry.enotebook.client.integration.spreadsheet.SpreadsheetProcessor;
import com.chemistry.enotebook.client.print.gui.PrintExperimentSetup;
import com.chemistry.enotebook.client.print.gui.PrintRequest;
import com.chemistry.enotebook.client.utils.Disposable;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.domain.container.ContainerType;
import com.chemistry.enotebook.experiment.common.codetables.SaltCodeCache;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.service.container.ContainerService;
import com.chemistry.enotebook.storage.StorageException;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.virtuan.plateVisualizer.StaticPlateRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class CompoundManagementMonomerContainer extends JPanel implements ItemListener, PlateCreateInterface, PlatesCreatedEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5188701452612498977L;
	private static final Log log = LogFactory.getLog(CompoundManagementMonomerContainer.class);
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private StorageDelegate storageDelegate = null;

	private NotebookPageModel mNotebookPageModel;
	private ParallelCeNProductPlateBuilder mParallelCeNProductPlateBuilder = new ParallelCeNProductPlateBuilder();
	private ParallelCeNMonomerPlateBuilder parallelCeNMonomerPlateBuilder = new ParallelCeNMonomerPlateBuilder();
	private CeNMonomerPlaterBuilder mCeNMonomerPlaterBuilder; // vb 7/12 = new CeNMonomerPlaterBuilder();
	private CeNProductPlateBuilder mCeNProductPlateBuilder; // vb 7/12 = new CeNProductPlateBuilder();
	private PlateCreationHandler mPlateCreationHandler;// = new
	private BorderLayout borderLayout = new BorderLayout();
	private JPanel topPanel = new JPanel();
	private JSplitPane split;
	private JScrollPane rightSpliScroll = new JScrollPane();
	// private FlowLayout topFlowLayout = new FlowLayout();
	String[] types = { "Monomers", "Products" };
	private CeNComboBox typeComboBox = new CeNComboBox(types);
	String[] views;
	String[] monomerViews = { "Tables - Separate Reactants", "Tables - Separate Plates", "Plates" };
	String[] productViews = { "Tables - Separate Products", "Tables - Separate Plates", "Plates" };
	CeNComboBox viewOptionsComboBox = null;// new CeNComboBox(views);
	JLabel asLabel = new JLabel();
	JPanel viewControlPanel = new JPanel();
	JPanel loadOrdersPanel = new JPanel(); // vb 5/13
	JPanel createPlatesPanel = new JPanel(); // vb 5/13
	Border border1 = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(109, 109, 110),
			new Color(156, 156, 158));
	Border controlBorder = BorderFactory.createEtchedBorder(Color.white, new Color(156, 156, 158));

	JButton loadCompoundManagementOrderButton = new JButton("Read Other Order");
	JButton loadFileOrderButton = new JButton("Read from Excel Spreadsheet");
	JButton loadOrdersFromSynthesisPlanButton = new JButton("Synthesis Plan");
	JButton exportExcelButton = new JButton("Export to Excel");
	JButton exportOtherWorklistButton = new JButton("Export Other Worklist");
	JLabel fillerLabel = new JLabel("   ");
	JButton createPlateFromSynthesisPlanButton = new JButton("Synthesis Plan"); // vb 5/13
	JButton createPlateFromMonomerPlateButton = new JButton("MonomerPlate"); // vb 5/13
	JButton createPlateFromProductPlateButton = new JButton("ProductPlate"); // vb 5/13
	JButton openSynthesisPlateButton = new JButton("Synthesis Plate");

	JButton showHideSplitterButton = new JButton("Show Containers");
	JButton searchContainerTypesButton = new JButton("Search Container Types");
	JButton defineCustomerContainterButton = new JButton("Define Custom Container");

	// Container details panel
	private CeNLabel cotainerNameLabel = new CeNLabel("Container Name");
	private CeNLabel cotainerCodeLabel = new CeNLabel("Container Code");
	private JLabel containerNameValue = new JLabel();
	private JLabel containerCodeValue = new JLabel();
	private CeNLabel xPositions = new CeNLabel("X Positions");
	private JLabel xPositionsValue = new JLabel();
	private CeNLabel yPositions = new CeNLabel("Y Positions");
	private JLabel yPositionsValue = new JLabel();
	private CeNLabel fillOrder = new CeNLabel("Fill Order");
	private JLabel fillOrderValue = new JLabel();
	private CeNLabel skipWellPositions = new CeNLabel("Skipped Well Positions");
	private JLabel skipWellValue = new JLabel();
	JPanel containerDetailsPanel = null;

	// load Compound Management Orders
	private JPanel loadCompoundManagementPanel = new JPanel();
	// private CeNComboBox compoundManagementOrderComboBox = null;// = new JComboBox();
	private JTextField compoundManagementOrderTextField = new JTextField();
	private String[] amounttoUsedOptions = { "Delivered", "Ordered" };
	CeNComboBox amountUsedCombo = new CeNComboBox(amounttoUsedOptions);// This is accessed in ContainerDialog class.
	private String[] sortMonomer = { CeNConstants.SORT_BY_ORDER_SEQ, CeNConstants.SORT_BY_BATCH_NUMBER, 
									 CeNConstants.SORT_BY_COMPOUND_NUMBER, CeNConstants.SORT_BY_MW };
	CeNComboBox sortMonomerCombo = new CeNComboBox(sortMonomer);// This is accessed in ContainerDialog class.
	private FormLayout loadCompoundManagementOrderFormLayout = new FormLayout("left:max(pref;70dlu), 60dlu", "5dlu,pref, 5dlu,pref, 5dlu,pref");
	private CellConstraints mCellConstraints = new CellConstraints();
	private JLabel compoundManagementOrderLabel = new JLabel("Compound Management Order: ");
	private JLabel sortMonomerLabel = new JLabel("Sort Monomers by: ");
	private JLabel amountUseLabel = new JLabel("Amounts to Use: ");
	JLabel viewLabel = new JLabel();
	// load Excel Orders
	private JPanel loadExcelPanel = new JPanel();
	private JTextField exceltf = new JTextField(40);
	private FormLayout loadExcelOrderFormLayout = new FormLayout("left:max(pref;70dlu),5dlu,100dlu,5dlu,50dlu",
			"5dlu,pref, 5dlu,pref, 5dlu,pref");
	private JLabel ctLabel = new JLabel("Container Type: ");
	private JLabel excelLabel = new JLabel("Spreadsheet: ");
	private JButton browseBtn = new JButton("Browse");
	// private CompoundManagementOrderingServiceImpl compoundManagementOrderingService;

	private PCeNTableModelConnector connector;
	private JTabbedPane monomerTableViewTabs = new JTabbedPane(// table view of
			// monomer lists
			SwingConstants.TOP);
	private JTabbedPane monomerPlatePLateViewTabs = new JTabbedPane(// plateviewofplates
			SwingConstants.TOP);
	private JTabbedPane monomerPlateTableViewTabs = new JTabbedPane(// tableviewofplates
			SwingConstants.TOP);
	private JTabbedPane productPlatePlateViewTabs = new JTabbedPane(// plateviewofplates
			SwingConstants.TOP);
	private JTabbedPane productPlateTableViewTabs = new JTabbedPane(// tableviewofplates
			SwingConstants.TOP);
	// private JScrollPane productTableViewScroll = new JScrollPane();
	private JPanel productTablePanel;

	private PCeNTableView productTableView;
	private PCeNTableViewToolBar productTableViewToolBar;
	private SpinnerPanel mSpinnerPanel = new SpinnerPanel();
	private String[] directions = { "X", "Y" };
	private JPanel customContainerPanel = new JPanel();
	private JRadioButton xDirectionRadioButton = new JRadioButton(directions[0]);
	private JRadioButton yDirectionRadioButton = new JRadioButton(directions[1]);
	private ButtonGroup group = new ButtonGroup();
	private ContainerAddedEventListener mContainerAddedEventListener;
	private VerticalFlowLayout mVerticalFlowLayout = new VerticalFlowLayout();
	private ContainerTypeExPTree containerTypeExPTree;
	private JPanel leftpanel = new JPanel(mVerticalFlowLayout);
	private ReactionStepModel stepModel;
	private int stepIndex = 0;

	private JPopupMenu monomerTableViewTabPopupMenu = new JPopupMenu();
	private JMenuItem deleteMonomerPlate1 = new JMenuItem("Remove Monomer Plate");

	private JPopupMenu monomerPlateViewTabPopupMenu = new JPopupMenu();
	private JMenuItem deleteMonomerPlate2 = new JMenuItem("Remove Monomer Plate");

	private JPopupMenu productTableViewTabPopupMenu = new JPopupMenu();
	private JMenuItem deleteProductPlate1 = new JMenuItem("Remove Product Plate");

	private JPopupMenu productPlateViewTabPopupMenu = new JPopupMenu();
	private JMenuItem deleteProductPlate2 = new JMenuItem("Remove Product Plate");

	private FormLayout fillMonomerPalteFormLayout = new FormLayout("left:max(pref;70dlu),5dlu,100dlu,5dlu",
			"5dlu,pref, 5dlu,pref, 5dlu,pref");
	private JPanel fillMonomerPalteDialogPanel = new JPanel();

	private JLabel monomeListLabel = new JLabel("Reactant List: ");
	private JLabel sortMonomerLabel2 = new JLabel("Sort Monomers By: ");
	private JLabel containerLabel = new JLabel("Container From My List: ");
	private ContainerService mContainerService = null;

	private Container selectedContainer = null;

	private String[] validContainerTypes = { "PLATE", "RACK", "VIAL", "TOTE" };

	/*
	 * Create Product Plates
	 */
	private JRadioButton stepWiseRadio = new JRadioButton("All");
	private JLabel synthesisPlanProductsLabel = new JLabel("<html><b>Use These Synthesis Plan Products (Current Step):</b></html>");
	private JRadioButton stepWiseSubIndexedRadio = new JRadioButton("Range inclusive:");
	private JLabel sortProductLabel = new JLabel("Sort Products by: ");
	private JLabel containerLabel4ProductPlate = new JLabel("Container From My List: ");
	private ButtonGroup dataTypeGroup = new ButtonGroup();
	private Container mContainerType;
	private JButton creatPlatesBtn = new JButton("Create Plates");
	private JButton cancelBtn = new JButton("Cancel");
	private FormLayout createProductPlateDialoglayout1 = new FormLayout(
			"left:max(70dlu;pref), 5dlu, fill:50dlu, 5dlu, center:15dlu, 5dlu,fill:50dlu ",// columns
			"5dlu,pref, 10dlu, pref, pref, 5dlu"); // rows
	private FormLayout createProductPlateDialoglayout2 = new FormLayout(
			"left:max(70dlu;pref), 5dlu, fill:50dlu, 5dlu, center:15dlu, 5dlu,fill:50dlu ",// columns
			"5dlu, pref, 5dlu, pref,5dlu, pref, 5dlu"); // rows
	private JPanel fillProductPalteDialogPanel = new JPanel();
	private String[] sortProduct = { CeNConstants.SORT_BY_BATCH_NUMBER, CeNConstants.SORT_BY_MW };
	private CeNComboBox sortProductCombo = new CeNComboBox(sortProduct);
	private JLabel useProductsLable = new JLabel("Repeat Product(s): ");
	private JLabel noOfTimesLable = new JLabel("Time(s)");
	// private JLabel timessLable = new JLabel(" times ");

	private SpinnerNumberModel productTimesUsedModel = new SpinnerNumberModel(1, 1, 10, 1);
	private JSpinner productTimesUsedJSpinner = new JSpinner(productTimesUsedModel);

	private Vector containerList;
	boolean isLoading = true;

	private List disposeList = new ArrayList(); // vb 3/6 list of components to invoke dispose on
	private Map plateViewPanelMap = new HashMap();

	// for storing preferred size instead of multiple calculating it
	private Dimension preferredSize = null;
	
	private static final String ASDI_ORDER_LOAD_NO_CONTAINER = "No Containers";

	public CompoundManagementMonomerContainer(PlateCreationHandler plateCreationHandler, ReactionStepModel stepe, int stepIndex,
			ContainerTypeExPTree containerTypeExPTree, NotebookPageModel notebookPageModel) {
		try {
			this.stepIndex = stepIndex;
			mNotebookPageModel = notebookPageModel;
			stepModel = stepe;
			NotebookUser user = MasterController.getUser();
			storageDelegate = ServiceController.getStorageDelegate(user.getSessionIdentifier());
			mContainerService = ServiceController.getContainerService(user.getSessionIdentifier());
			mPlateCreationHandler = plateCreationHandler;
			this.containerTypeExPTree = containerTypeExPTree;
			containerTypeExPTree.setSelectedPlateCreateInterface(this);
			mContainerAddedEventListener = containerTypeExPTree;
			mCeNProductPlateBuilder = new CeNProductPlateBuilder(notebookPageModel);
			mCeNMonomerPlaterBuilder = new CeNMonomerPlaterBuilder(notebookPageModel);
			mPlateCreationHandler.addPlateCreatedEventListener(this);
			init();
		} catch (Exception ex) {
			log.error("Constructor failure.", ex);
		}
	}

	public void dispose() {
		this.mPlateCreationHandler.dispose();
		for (Iterator it = disposeList.iterator(); it.hasNext();) {
			Disposable disp = (Disposable) it.next();
			if (disp != null)
				disp.dispose();
		}
	}

	private void init() throws Exception {
		group.add(xDirectionRadioButton);
		group.add(yDirectionRadioButton);
		xDirectionRadioButton.setActionCommand(directions[0]);
		yDirectionRadioButton.setActionCommand(directions[1]);
		// xDirectionRadioButton.setSelected(true);//Default selection
		customContainerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		customContainerPanel.add(new JLabel("Fill Orders by: "));
		customContainerPanel.add(xDirectionRadioButton);
		customContainerPanel.add(yDirectionRadioButton);

		this.setLayout(borderLayout);
		this.setBackground(new Color(189, 236, 214));

		FormLayout topPanelLayout = new FormLayout("2dlu, 95dlu, 2dlu, left:pref, 2dlu, 95dlu, pref:grow, 2dlu, right:pref, 2dlu",
				"12dlu, 0dlu, 13dlu, 2dlu");
		final CellConstraints cc = new CellConstraints();

		topPanel.setLayout(topPanelLayout);
		loadOrdersPanel.setBorder(BorderFactory.createEtchedBorder(Color.white, new Color(156, 156, 158)));

		FormLayout loadOrdersPanelLayout = new FormLayout("2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu", "10dlu, 0dlu, 13dlu, 2dlu");
		loadOrdersPanel.setLayout(loadOrdersPanelLayout);
		loadOrdersPanel.add(new JLabel("Create plate from:"), cc.xyw(2, 1, 6));
		createPlatesPanel.setBorder(BorderFactory.createEtchedBorder(Color.white, new Color(156, 156, 158)));
		createPlatesPanel.setLayout(loadOrdersPanelLayout);
		createPlatesPanel.add(new JLabel("Create plate from:"), cc.xyw(2, 1, 6));

		loadCompoundManagementOrderButton.setToolTipText("Load Orders from Other");
		loadCompoundManagementOrderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// loadCompoundManagementOrderOptions();
				createMonomerPlateFromOtherOrder(selectedContainer);
			}
		});
		loadOrdersPanel.add(loadCompoundManagementOrderButton, cc.xy(2, 3));

		loadFileOrderButton.setToolTipText("Load Orders from Excel Spreadsheet");
		loadFileOrderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadOrdersFromFile();
			}
		});
		loadOrdersPanel.add(loadFileOrderButton, cc.xy(4, 3));

		loadOrdersFromSynthesisPlanButton.setToolTipText("Fill Plates from Synthesis Plan");
		loadOrdersFromSynthesisPlanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createMonomerPlateFromSynthesisPlan(selectedContainer);
			}
		});
		loadOrdersPanel.add(loadOrdersFromSynthesisPlanButton, cc.xy(6, 3));

		createPlateFromMonomerPlateButton.setToolTipText("Create a plate from a Monomer plate");
		createPlateFromMonomerPlateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlateCreateFromAbstractPlateDialog plateCreateByPlate = new PlateCreateFromAbstractPlateDialog(MasterController
						.getGUIComponent(), true, true, selectedContainer);
				plateCreateByPlate.setVisible(true);
			}
		});
		this.createPlatesPanel.add(this.createPlateFromMonomerPlateButton, cc.xy(2, 3));

		createPlateFromProductPlateButton.setToolTipText("Create a plate from a Product plate");
		createPlateFromProductPlateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				RegistrationPlateCreateDialog plateCreateByProduct = new RegistrationPlateCreateDialog(MasterController
						.getGUIComponent(), selectedContainer);
			}
		});
		this.createPlatesPanel.add(this.createPlateFromProductPlateButton, cc.xy(4, 3));

		createPlateFromSynthesisPlanButton.setToolTipText("Create a plate from Synthesis Plan design");
		createPlateFromSynthesisPlanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createProductPlateFromSynthesisPlan(selectedContainer);
			}
		});
		this.createPlatesPanel.add(this.createPlateFromSynthesisPlanButton, cc.xy(6, 3));

		showHideSplitterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHideSplitterButton_actionPerformed();
			}
		});

		exportExcelButton.setToolTipText("Export to Excel");
		exportExcelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportExcelButton();
			}
		});

		exportOtherWorklistButton.setToolTipText("Export Other Worklist");
		exportOtherWorklistButton.setEnabled(false);
		exportOtherWorklistButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportOtherWorklist();
			}
		});
		// todo

		exportOtherWorklistButton.setEnabled(false);

		searchContainerTypesButton.setToolTipText("Search Container Types");
		searchContainerTypesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchContainerTypes();
			}
		});
		defineCustomerContainterButton.setToolTipText("Define Customer Container");
		defineCustomerContainterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				defineCustomerContainter();
			}
		});
		browseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseBton_actionPerformed();
			}
		});

		asLabel.setText(" as: ");

		if (typeComboBox.getSelectedItem() == "Monomers")
			views = monomerViews;
		else
			views = productViews;
		viewOptionsComboBox = new CeNComboBox(views);
		typeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == typeComboBox && !isLoading) {
					int selectedIndex = viewOptionsComboBox.getSelectedIndex();
					removeListeners(viewOptionsComboBox);
					if (typeComboBox.getSelectedItem() == "Monomers") {
						views = monomerViews;
						topPanel.remove(createPlatesPanel);
						topPanel.add(loadOrdersPanel, cc.xywh(4, 1, 1, 3));
						topPanel.remove(exportOtherWorklistButton);
						topPanel.add(fillerLabel, cc.xy(2, 1));
						// loadFileOrderButton.setEnabled(true);
					} else {
						views = productViews;
						topPanel.remove(loadOrdersPanel);
						topPanel.add(createPlatesPanel, cc.xywh(4, 1, 1, 3));
						topPanel.remove(fillerLabel);
						topPanel.add(exportOtherWorklistButton, cc.xy(2, 1));
						// loadFileOrderButton.setEnabled(false);
					}
					viewOptionsComboBox.removeAllItems();
					for (int i = 0; i < views.length; i++)
						viewOptionsComboBox.addItem(views[i]);
					viewOptionsComboBox.setSelectedIndex(selectedIndex);
					addListeners(viewOptionsComboBox);
				}
			}
		});

		typeComboBox.addItemListener(this);
		viewOptionsComboBox.addItemListener(this);
		viewControlPanel.setBorder(controlBorder);
		// leftControlPanle.setBorder(controlBorder); vb 7/25
		// midControlPanle.setBorder(controlBorder);

		viewLabel.setToolTipText(null);
		viewLabel.setText("View: ");
		this.add(topPanel, java.awt.BorderLayout.NORTH);

		topPanel.add(fillerLabel, cc.xy(2, 1));
		topPanel.add(showHideSplitterButton, cc.xy(2, 3));
		topPanel.add(loadOrdersPanel, cc.xywh(4, 1, 1, 3));

		topPanel.add(openSynthesisPlateButton, cc.xy(6, 3));
		openSynthesisPlateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object frame = MasterController.getGUIComponent().getActiveDesktopWindow();
				NotebookPageModel model = ((NotebookPageGuiInterface) frame).getPageModel();
				Gui gui = MasterController.getGuiController().getGUIComponent();
				if (model != null) {
					ArrayList<PrintRequest> list = new ArrayList<PrintRequest>();
					NotebookRef nbRef = model.getNbRef();
					list.add(new PrintRequest( model.getSiteCode(), nbRef, model.getVersion(), model.getPageType() ));
					PrintExperimentSetup.displayPrintExperimentsDialog(gui, list, true);
				}
			}
		});

		// topPanel.add(leftControlPanle, cc.xy(4, 1));
		// leftControlPanle.add(fillPlatefromLabel); vb 7/25
		// leftControlPanle.add(compoundManagementOrderComboBox);
		// leftControlPanle.add(showHideSplitterButton);
		// //leftControlPanle.add(loadCompoundManagementOrderButton);
		// //leftControlPanle.add(loadFileOrderButton);
		// //leftControlPanle.add(loadSynthesisPlanButton);
		// leftControlPanle.add(searchContainerTypesButton);
		// leftControlPanle.add(defineCustomerContainterButton);
		// topPanel.add(midControlPanle);
		// midControlPanle.add(loadFileOrderButton);
		// midControlPanle.add(createCustomPlateButton);
		topPanel.add(viewControlPanel, cc.xywh(9, 1, 1, 3));
		viewControlPanel.setLayout(new FlowLayout());
		viewControlPanel.add(viewLabel);
		viewControlPanel.add(typeComboBox);
		viewControlPanel.add(asLabel);
		viewControlPanel.add(viewOptionsComboBox);
		// MasterController.getUser().this.add(plateViewPanel, java.awt.BorderLayout.CENTER);
		// scroll.getViewport().add(monomerPlateTabPane, java.awt.BorderLayout.CENTER);
		// NotebookRef nbkRef = mNotebookPageModel.getNbRef();
		/*
		 * step = ServiceController.getStorageService().getSummaryReaction(nbkRef, MasterController.getUser().getSessionIdentifier());
		 */
		// mParallelCeNMonomerBatchTableViewController = new ParallelCeNMonomerBatchTableViewController(step);
		ArrayList monomerLists = stepModel.getMonomers();
		PCeNMonomerReactantsTableModelConnector[] monomerReactantsTableModelConnector = new PCeNMonomerReactantsTableModelConnector[monomerLists
				.size()];
		StoichDataChangesListener stoichDataChangesListener = new StoichDataChangesListener(stepModel,
				CeNConstants.PAGE_TYPE_PARALLEL);

		for (int i = 0; i < monomerLists.size(); i++) {
			monomerReactantsTableModelConnector[i] = new PCeNMonomerReactantsTableModelConnector((BatchesList) monomerLists.get(i),
					stepModel, this.mNotebookPageModel);
			monomerReactantsTableModelConnector[i].addStoichDataChangesListener(stoichDataChangesListener);
		}
		for (int i = 0; i < monomerLists.size(); i++) {
			connector = monomerReactantsTableModelConnector[i];
			PCeNTableModel model = new PCeNTableModel(connector);
			PCeNTableView tableView = new PCeNMonomer_TableView(model, 70, connector);
			PCeNTableViewToolBar monomerTableViewToolBar = new PCeNTableViewToolBar(tableView);
			JPanel mPanel = new JPanel(new BorderLayout());
			JScrollPane sPane = new JScrollPane(tableView);
			mPanel.add(monomerTableViewToolBar, BorderLayout.NORTH);
			mPanel.add(sPane, BorderLayout.CENTER);
			monomerTableViewTabs.addTab(ResourceKit.getABCDRoot(i), mPanel);
		}
		monomerTableViewTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane pane = (JTabbedPane) evt.getSource();
				int selectedIndex = pane.getSelectedIndex();
				for (int i = 0; i < pane.getTabCount(); i++) {
					pane.setBackgroundAt(i, Color.LIGHT_GRAY);
				}
				pane.setBackgroundAt(selectedIndex, Color.WHITE);
			}
		});
		// set up monomer plates in the plate viewers
		// modified vb 6/1
		List monomerPlates = stepModel.getMonomerPlates();
		for (int i = 0; i < monomerPlates.size(); i++) {
			MonomerPlate mPlate = (MonomerPlate) monomerPlates.get(i);
			MonomerBatchDetailContainer batchDetail = new MonomerBatchDetailContainer(mPlate, mNotebookPageModel, stepModel);
			this.disposeList.add(batchDetail);
			StaticPlateRenderer plateview = (StaticPlateRenderer) mCeNMonomerPlaterBuilder.buildCeNMonomerPlateViewer(mPlate,
					batchDetail);

			this.disposeList.add(plateview);
			ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel("");
			MonomerPlatePlateViewPanel monomerPlatePlateViewPanel = new MonomerPlatePlateViewPanel(mPlate, plateview, batchDetail, // vb
					// 6/28
					// batchDetailPanel,
					reactionsViewPanel);
			monomerPlatePLateViewTabs.add(mPlate.getPlateBarCode(), monomerPlatePlateViewPanel);

			// monomer table views for plates
			monomerPlateTableViewTabs.add(mPlate.getPlateBarCode(), createMonomePlateTableViewPanel(mPlate));
		}

		List productPlates = stepModel.getProductPlates();
		addProductPlatesTabs(productPlates);
		// set up product batches table views
		connector = new PCeNCompoundManagementProductsTableModelConnector(((BatchesList) stepModel.getProducts().get(0)).getBatchModels(),
				mNotebookPageModel); // vb 11/9 added page model);
		// List abstractBatches = tableController.getProductBatches();
		// String[] headerNames2 = mParallelCeNProductBatchTableViewController
		// .getHeaderNames();
		PCeNTableModel model = new PCeNTableModel(connector);
		productTableView = new PCeNTableView(model, 70, connector, null);
		productTableViewToolBar = new PCeNTableViewToolBar(productTableView);
		// productToolBarPanel = new JPanel();
		// productToolBarPanel.setLayout(new BorderLayout());
		// productToolBarPanel.add(productTableViewToolBar,
		// BorderLayout.CENTER);
		// productTableViewScroll.getViewport().add(productTableView);

		productTablePanel = createProductTableView(productTableView, productTableViewToolBar);
		// productTableViewScroll.getViewport().setBackground(
		// new Color(189, 236, 214));
		JScrollPane treescroller = new JScrollPane(containerTypeExPTree);
		leftpanel.add(searchContainerTypesButton);
		leftpanel.add(defineCustomerContainterButton);
		leftpanel.add(treescroller);

		containerDetailsPanel = new JPanel();
		FormLayout containerDetailsLayout = new FormLayout("pref, 5dlu, pref, 5dlu, pref, 5dlu, pref",// columns
				"pref, pref, pref, pref, pref, pref, pref"); // rows
		containerDetailsPanel.setLayout(containerDetailsLayout);
		containerDetailsPanel.add(cotainerCodeLabel, mCellConstraints.xy(1, 1));
		containerDetailsPanel.add(containerCodeValue, mCellConstraints.xy(3, 1));
		containerDetailsPanel.add(cotainerNameLabel, mCellConstraints.xyw(1, 2, 6));
		containerDetailsPanel.add(containerNameValue, mCellConstraints.xyw(1, 3, 6));
		containerDetailsPanel.add(xPositions, mCellConstraints.xy(1, 4));
		containerDetailsPanel.add(xPositionsValue, mCellConstraints.xy(3, 4));
		containerDetailsPanel.add(yPositions, mCellConstraints.xy(5, 4));
		containerDetailsPanel.add(yPositionsValue, mCellConstraints.xy(7, 4));
		containerDetailsPanel.add(fillOrder, mCellConstraints.xyw(1, 5, 2));
		containerDetailsPanel.add(fillOrderValue, mCellConstraints.xyw(3, 5, 2));
		containerDetailsPanel.add(skipWellPositions, mCellConstraints.xyw(1, 6, 6));
		containerDetailsPanel.add(skipWellValue, mCellConstraints.xyw(1, 7, 6));
		containerDetailsPanel.setVisible(false);

		leftpanel.add(containerDetailsPanel);

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftpanel, rightSpliScroll);
		split.setOneTouchExpandable(true);
		split.setResizeWeight(0.2);
		split.setDividerLocation(0);
		this.add(split, java.awt.BorderLayout.CENTER);
		// show monomer list table views in pane
		typeComboBox.setSelectedIndex(0);
		viewOptionsComboBox.setSelectedIndex(0);

		monomerTableViewTabPopupMenu.add(deleteMonomerPlate1);

		// vb 5/17
		deleteMonomerPlate1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteMonomerPlate();
			}
		});

		monomerPlateTableViewTabs.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					monomerTableViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseReleased
		});

		monomerPlateViewTabPopupMenu.add(deleteMonomerPlate2);

		deleteMonomerPlate2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equalsIgnoreCase("Remove Monomer Plate"))
					deleteMonomerPlate();
			}
		});

		productPlateTableViewTabs.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					productTableViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseReleased
		});

		productTableViewTabPopupMenu.add(deleteProductPlate1);
		productPlateViewTabPopupMenu.add(deleteProductPlate2);
		productPlateViewTabPopupMenu.add(new ShowColorLegendDialogMenuItem());
		monomerPlateViewTabPopupMenu.add(new ShowColorLegendDialogMenuItem());

		// vb 6/14
		deleteProductPlate1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteProductPlate();
			}
		});

		deleteProductPlate2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if (e.getActionCommand().equalsIgnoreCase("Delete Product Plate"))
				deleteProductPlate();
			}
		});

		monomerPlatePLateViewTabs.addChangeListener(new ChangeListener() {
			// This method is called whenever the selected tab changes
			public void stateChanged(ChangeEvent evt) {
				if (monomerPlatePLateViewTabs.getSelectedComponent() != null)
					((MonomerPlatePlateViewPanel) monomerPlatePLateViewTabs.getSelectedComponent()).refresh();
			}
		});

		monomerPlatePLateViewTabs.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					monomerPlateViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseReleased

			public void mouseClicked(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					monomerPlateViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseClciked
		});

		productPlatePlateViewTabs.addChangeListener(new ChangeListener() {
			// This method is called whenever the selected tab changes
			public void stateChanged(ChangeEvent evt) {
				JTabbedPane pane = (JTabbedPane) evt.getSource();
				// Get current tab
				Object obj = pane.getSelectedComponent();
				if (obj instanceof ProductPlatePlateViewPanel) {
					((ProductPlatePlateViewPanel) obj).refresh();
				}
			}
		});

		productPlatePlateViewTabs.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					productPlateViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseReleased

			public void mouseClicked(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					productPlateViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			} // end mouseClicked

		});

		/*
		 * private JTabbedPane monomerPlatePLateViewTabs = new JTabbedPane(// plateviewofplates SwingConstants.TOP); private
		 * JTabbedPane monomerPlateTableViewTabs = new JTabbedPane(// tableviewofplates SwingConstants.TOP); private JTabbedPane
		 * productPlatePlateViewTabs = new JTabbedPane(// plateviewofplates SwingConstants.TOP); private JTabbedPane
		 * productPlateTableViewTabs = new JTabbedPane(// tableviewofplates
		 */
		dataTypeGroup.add(stepWiseRadio);
		dataTypeGroup.add(stepWiseSubIndexedRadio);
		stepWiseRadio.setSelected(true);
		/*
		 * Font f = synthesisPlanProductsLabel.getFont(); int style = f.getStyle(); style+=Font.BOLD; f.deriveFont(style);
		 * synthesisPlanProductsLabel.setFont(f);
		 */

		isLoading = false;
		showViews();
		if (!mNotebookPageModel.isEditable())
			setButtonsEditable(false);
	}

	public Dimension getPreferredSize() {
		if (preferredSize == null) {
			preferredSize = super.getPreferredSize(); 
		}
		return preferredSize; 
	}
	
	private void setButtonsEditable(boolean editable) {
		if (!mNotebookPageModel.isEditable())
			editable = false;
		loadCompoundManagementOrderButton.setEnabled(editable);
		loadFileOrderButton.setEnabled(editable);
		loadOrdersFromSynthesisPlanButton.setEnabled(editable);
		exportExcelButton.setEnabled(editable);
		exportOtherWorklistButton.setEnabled(editable);
		createPlateFromSynthesisPlanButton.setEnabled(editable);
		createPlateFromMonomerPlateButton.setEnabled(editable);
		createPlateFromProductPlateButton.setEnabled(editable);

		// showHideSplitterButton.setEnabled(editable);
		searchContainerTypesButton.setEnabled(editable);
		defineCustomerContainterButton.setEnabled(editable);
	}

	protected void exportOtherWorklist() {
		// TODO Auto-generated method stub

	}

	protected void removeListeners(CeNComboBox viewOptionsComboBox2) {
		viewOptionsComboBox2.removeItemListener(this);
	}

	protected void addListeners(CeNComboBox viewOptionsComboBox2) {
		viewOptionsComboBox2.addItemListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chemistry.enotebook.client.gui.page.experiment.PlateCreateInterface#setSelectedContainer(com.
	 * chemistry.enotebook.domain.container.Container)
	 */
	public void setSelectedContainer(Container mContainer) {
		this.selectedContainer = mContainer;
		populateContainerDetailsPanel(selectedContainer);
	}

	private void populateContainerDetailsPanel(Container selectedContainer) {
		if (selectedContainer != null) {
			containerCodeValue.setText(selectedContainer.isUserDefined() ? "" : selectedContainer.getContainerCode());
			containerNameValue.setText(selectedContainer.getContainerName() + " (" + selectedContainer.getContainerType() + ")");
			xPositionsValue.setText(selectedContainer.getXPositions() + "");
			yPositionsValue.setText(selectedContainer.getYPositions() + "");
			fillOrderValue.setText(selectedContainer.getMajorAxis() == null ? "X" : selectedContainer.getMajorAxis());
			if (selectedContainer.getSkippedWellPositions() == null
					|| selectedContainer.getSkippedWellPositions().size() == 0
					|| (selectedContainer.getSkippedWellPositions().size() == 1 && selectedContainer.getSkippedWellPositions().get(
							0) == null))

				skipWellValue.setText("No Skip wells");
			else
				skipWellValue.setText(selectedContainer.getSkippedWellPositions().toString());
			containerDetailsPanel.setVisible(true);
		} else {
			containerNameValue.setText("");
			xPositionsValue.setText("");
			yPositionsValue.setText("");
			fillOrderValue.setText("");
			skipWellValue.setText("");
			containerDetailsPanel.setVisible(false);
		}
		skipWellValue.setToolTipText(skipWellValue.getText());
	}

	public void searchContainerTypes() {
		SearchContainerDialog mSearchContainerDialog = new SearchContainerDialog(MasterController.getGUIComponent(),
				mContainerAddedEventListener);
		mSearchContainerDialog.setSize(450, 200);
		mSearchContainerDialog.setLocationRelativeTo(MasterController.getGUIComponent());
		mSearchContainerDialog.setVisible(true);
	}

	public void defineCustomerContainter() {
		Object[] message = new Object[7];
		int index = 0;
		message[index++] = "Enter Name for Container: ";
		JTextField nametf = new JTextField();
		message[index++] = nametf;
		message[index++] = "Rows:      Cols: ";
		message[index++] = mSpinnerPanel.getPanel();
		// message[index++] = "Fill Orders by ";
		message[index++] = customContainerPanel;
		message[index++] = "Enter Well Positions to skip: ";
		JTextField skipPosTextField = new JTextField();
		message[index++] = skipPosTextField;
		// message[index++] = "Enter Site Code ";
		// JTextField sitetf = new JTextField();
		// message[index++] = sitetf;
		// ContainerTypeTreeModel mContainerTypeTreeModel =
		// getMContainerTypeTreeModel();
		// Options
		String[] options = { "Add to List", "Cancel" };
		int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the
				// dialog blocks
				message, // the dialog message array
				"Define your Container", // the title of the dialog
				// window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.INFORMATION_MESSAGE, // message type
				null, // optional icon, use null to use the default icon
				options, // options string array, will be made into
				// buttons
				options[0] // option that should be made into a default
				// button
				);
		switch (result) {
			case 0: // add result
				String containerName = nametf.getText();
				if (containerName == null || containerName.equals("")) {
					JOptionPane.showOptionDialog(MasterController.getGUIComponent(), "Container Name must be a valid string.",
							"Invalid container name", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
					return;
				}
				/*
				 * if (isContainerNameExisting(containerName)) { JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
				 * "Container must have a unique name to define.", "Invalid container name", JOptionPane.DEFAULT_OPTION ,
				 * JOptionPane.ERROR_MESSAGE, null, null, null); return; }
				 */
				if (group.getSelection() == null) {
					JOptionPane.showOptionDialog(MasterController.getGUIComponent(), "Container must have a valid fill order.",
							"Invalid container name", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
					return;
				}
				Container mContainerType = Container.prepareNewUSerContainer(containerName, ContainerType.PLATE, MasterController
						.getUser().getNTUserID(), mSpinnerPanel.getColNumber(), mSpinnerPanel.getRowNumber(), (String) group
						.getSelection().getActionCommand());

				String[] wellPosArray = skipPosTextField.getText().split("[ \t]*,[ \t]*");
				ArrayList<String> skippedWellPositions = new ArrayList<String>(Arrays.asList(wellPosArray));
				while (skippedWellPositions.contains("")) {
					skippedWellPositions.remove("");
				}
				if (skippedWellPositions.size() > 0) {
					mContainerType.setSkippedWellPositions(skippedWellPositions);
				}

				ContainerAddedEvent event = new ContainerAddedEvent(this, mContainerType);
				mContainerAddedEventListener.newContainerAdded(event);
				break;
			case 1: // cancel
				break;
		}
	}

	/*
	 * private boolean isContainerNameExisting(String containerName) { ArrayList mContainers = new ArrayList(); boolean isExisting =
	 * false; try { mContainers = mContainerService.getUserContainers(MasterController.getUser().getNTUserID()); } catch (Exception
	 * ex) { ex.printStackTrace(); return isExisting; } Container temp = null; for (int i=0; i< mContainers.size(); i++) { temp =
	 * (Container) mContainers.get(i); if (temp.getContainerName().equals(containerName)) { isExisting = true; break; } } return
	 * isExisting; }
	 */
	/**
	 * Use the container from user selected
	 */
	public void loadOrderNoContainerFromFile(Container selectedContainer) {
		ArrayList mContainers = new ArrayList();
		try {
			mContainers = (ArrayList) mContainerService.getUserContainers(MasterController.getUser().getNTUserID());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Vector containers = new Vector(mContainers);
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(containers);
		CeNComboBox myContainerListCombo = new CeNComboBox(comboBoxModel);
		setSelectedContainer(selectedContainer);
		myContainerListCombo.getModel().setSelectedItem(selectedContainer);
		this.loadOrdersFromFile(myContainerListCombo);
	}

	private CeNComboBox getUserContainerListCombo() throws Exception {
		// Fill the containers map using key as key
		Map containersMap = new HashMap();
		ArrayList containerList = (ArrayList) mContainerService.getUserContainers(MasterController.getUser().getNTUserID());
		for (Iterator it = containerList.iterator(); it.hasNext();) {
			Container cont = (Container) it.next();
			containersMap.put(cont.getKey(), cont);
		}
		Vector containersVec = new Vector(containersMap.values());
		Collections.sort(containersVec, new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 == null || o2 == null)
					return 0;
				if (o1 instanceof Container && o2 instanceof Container) {
					return ((Container) o1).getContainerName().compareTo(((Container) o2).getContainerName());
				}
				return 0;
			}
		});
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(containersVec);
		CeNComboBox containerListCombo = new CeNComboBox(comboBoxModel);
		// containerListCombo.enablePopUpCombo(250);//CeNComboBox takes care of it.

		containerListCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				CeNComboBox tempComboBox = (CeNComboBox) e.getSource();
				if (tempComboBox.getSelectedItem() != null && !tempComboBox.getSelectedItem().equals("")
						&& !tempComboBox.getSelectedItem().equals("No Container")) {
					Container tempContainer = (Container) tempComboBox.getSelectedItem();
					tempComboBox.setToolTipText(tempContainer.getContainerName() + " (" + tempContainer.getYPositions() + ", "
							+ tempContainer.getXPositions() + ")");
				}
			}
		});
		// Set the selected item in the containers list. If there is no selected item,
		// insert an empty string at index zero to indicate no container is selected.
		// (Hack alert: the code that calls this method depends on knowing this is an empty string.)
		if (selectedContainer != null && containersMap.containsKey(selectedContainer.getKey())) {
			containerListCombo.getModel().setSelectedItem(selectedContainer);
			containerListCombo.setToolTipText(selectedContainer.getContainerName() + " (" + selectedContainer.getYPositions()
					+ ", " + selectedContainer.getXPositions() + ")");
		} else {
			containerListCombo.insertItemAt("", 0);
			containerListCombo.setSelectedIndex(0);
		}

		return containerListCombo;
	}

	/**
	 * Assuming there is a container info in the file So no being ued yes
	 */
	public void loadOrdersFromFile() {
		loadExcelPanel.setLayout(loadExcelOrderFormLayout);
		CeNComboBox containerListCombo = new CeNComboBox();
		try {
			containerListCombo = this.getUserContainerListComboForASDI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance()
					.logExceptionMsg(MasterController.getGUIComponent(), "Unable to load user's container(s).", e);
		}
		this.loadOrdersFromFile(containerListCombo);
	}

	/**
	 * Assuming there is a container info in the file So no being ued yes
	 */
	private void loadOrdersFromFile(CeNComboBox containerListCombo) {	
		
		ArrayList<BatchesList<MonomerBatchModel>> monomerLists = stepModel.getMonomers();
		JComboBox monoListCombo = new JComboBox();
		for (int i = 0; i < monomerLists.size(); i++) {
			monoListCombo.addItem(ResourceKit.getABCDRoot(i));
		}
		
		JPanel labelPanel = new JPanel(new GridLayout(4, 1, 6, 6));
		JPanel editPanel = new JPanel(new GridLayout(4, 1, 6, 6));
		
		loadExcelPanel = new JPanel(new BorderLayout());
		labelPanel.add(excelLabel);
		JPanel browsePanel = new JPanel(new BorderLayout(6, 6));
		browsePanel.add(exceltf, BorderLayout.CENTER);
		browsePanel.add(browseBtn, BorderLayout.EAST);
		editPanel.add(browsePanel);
		labelPanel.add(monomeListLabel);
		editPanel.add(monoListCombo);
		labelPanel.add(sortMonomerLabel);
		editPanel.add(sortMonomerCombo);
		labelPanel.add(ctLabel);
		editPanel.add(containerListCombo);
		
		loadExcelPanel.add(labelPanel, BorderLayout.WEST);
		loadExcelPanel.add(editPanel, BorderLayout.CENTER);
		
		Object[] message = new Object[] { loadExcelPanel };
		// Options
		String[] options = { "Fill", "Cancel" };
		int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the dialog blocks
				message, // the dialog message array
				"Fill Monomer Plates from Excel", // the title of the dialog window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.PLAIN_MESSAGE, // message type
				null, // optional icon, use null to use the default icon
				options, // options string array, will be made into buttons
				options[0] // option that should be made into a default button
				);
		
		switch (result) {
			case 0: // "Add selected to",
				try {
					Object obj = containerListCombo.getSelectedItem();
					if (obj instanceof String && ((String) obj).equals("")) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please select a Container.",
								"Container Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					Container selectedContainerType = (Container) containerListCombo.getSelectedItem();
					if (exceltf == null || exceltf.getText().length() == 0) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please select a file.", "File Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					ArrayList<MonomerBatchModel> loadedMonomers = SpreadsheetProcessor.loadMonomersFromFile(exceltf.getText());
										
					List monomerBatches = loadedMonomers;
					BatchesList bList = (BatchesList) stepModel.getMonomers().get(monoListCombo.getSelectedIndex()); 
					List synthesisPlanMonomerBatchList = bList.getBatchModels();
					
					// This method copies the matching ASDI mon batches delivered weight to Synthesis Plan Mon batches.
					ArrayList<MonomerBatchModel> equivalentSynthesisPlanBatchesList = getEqSynthesisPlanBatchesWithDelivAmt(monomerBatches, synthesisPlanMonomerBatchList);
					if (equivalentSynthesisPlanBatchesList.size() == 0) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
								"There are no batches in the Excel file that match your designed monomers. Please correct and try again.");
						return;
					}
					if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_COMPOUND_NUMBER)) {
						Collections.sort(equivalentSynthesisPlanBatchesList, new CompoundNumberComparator());
					} else if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_MW)) {
						Collections.sort(equivalentSynthesisPlanBatchesList, new BatchMWComparator());
					} else if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_SYNTHESIS_PLAN_SEQ)) {
						// Do nothing. Tubes are not sorted prior to copyDeliveredAmtsToSynthesisPlan so results should be in design order 
					} else if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_ORDER_SEQ)) {
						// Do nothing. Tubes are sorted prior to copyDeliveredAmtsToSynthesisPlan so results should be sorted 
					}

					// if user has chosen "No Containers" just copy delivered amount into Synthesis Plan batches
					if (selectedContainerType.getContainerCode().equals(ASDI_ORDER_LOAD_NO_CONTAINER)) {
						this.mNotebookPageModel.setModelChanged(true);
						this.enableSaveButton();
					} else {
						List<MonomerPlate> monomerPlatesList = this.createMonomerPlates(equivalentSynthesisPlanBatchesList, selectedContainerType, "", CeNConstants.PLATE_CREATED_FROM_FILE);// always using step 0
						MonomerPlate[] monomerPlates = (MonomerPlate[]) monomerPlatesList.toArray(new MonomerPlate[monomerPlatesList.size()]);
						// This method sets amounts to PlateWell and SynthesisPlan batches
						copyDeliveredAmtsToSynthesisPlan(monomerPlates, synthesisPlanMonomerBatchList, true, "Delivered");
					}
					// System.out.println("mASDIOrderFileProcessor.getNumberOfOrders()"+mASDIOrderFileProcessor.getNumberOfOrders());
					
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "File import successfully completed.\n Filled " + equivalentSynthesisPlanBatchesList.size() + " of " + loadedMonomers.size() + " monomers.");
					enableSaveButton();
				} catch (Exception ex) {
					ex.printStackTrace();
					ceh.logExceptionMsg(this, "Error processing and filling order from selected Excel file.", ex);
					// JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					// 			"Issue processing and filling order from selected ASDI file.", "ASDIOrderFileProcessor Error",
					// 			JOptionPane.ERROR_MESSAGE);
				}
				break;
			case 1: // cancel
				break;
		}
	}

	private ArrayList<MonomerBatchModel> getEqSynthesisPlanBatchesWithDelivAmt(List<MonomerBatchModel> asdiMonomerBatches, List<MonomerBatchModel> synthesisPlanMonomerBatchList) {
		ArrayList<MonomerBatchModel> eqSynthesisPlanMonomerBatchesList = new ArrayList<MonomerBatchModel>();
		
		for (MonomerBatchModel asdiMonomerBatchModel : asdiMonomerBatches) {
			for (int i = 0; i < synthesisPlanMonomerBatchList.size(); i++) {
				MonomerBatchModel synthesisPlanMonomerBatchModel = (MonomerBatchModel) synthesisPlanMonomerBatchList.get(i);
				if (CommonUtils.isMatch(synthesisPlanMonomerBatchModel.getCompoundId(), asdiMonomerBatchModel.getCompoundId())) {
					eqSynthesisPlanMonomerBatchesList.add(synthesisPlanMonomerBatchModel);
					synthesisPlanMonomerBatchModel.deepCopy(asdiMonomerBatchModel);
					synthesisPlanMonomerBatchModel.setDeliveredMonomerID(asdiMonomerBatchModel.getMonomerId());
					synthesisPlanMonomerBatchModel.setMolWgt(asdiMonomerBatchModel.getMolWgt());
					synthesisPlanMonomerBatchModel.setBarCode(asdiMonomerBatchModel.getBarCode());
				}
			}
		}
		
		return eqSynthesisPlanMonomerBatchesList;
	}

	private void browseBton_actionPerformed() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		FileFilter filter = new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String extension = getExtension(f);
				if (extension != null)
					return (extension.equals("xls") || extension.equals("xlsx"));
				return false;
			}

			public String getDescription() {
				return "Microsoft Office Excel files";
			}

			private String getExtension(File f) {
				String ext = null;
				String s = f.getName();
				int i = s.lastIndexOf('.');
				if (i > 0 && i < s.length() - 1) {
					ext = s.substring(i + 1).toLowerCase();
				}
				return ext;
			}
		};
		chooser.setFileFilter(filter);
		int dlgResult = chooser.showOpenDialog(this);
		if (JFileChooser.APPROVE_OPTION == dlgResult) {
			String fileName = chooser.getSelectedFile().getAbsolutePath();
			exceltf.setText(fileName);
		}
	}

	private void showHideSplitterButton_actionPerformed() {
		if (showHideSplitterButton.getText().indexOf("Hide") > -1) {
			showHideSplitterButton.setText("Show Containers");
			split.setDividerLocation(0);

			// this.remove(split);
			// this.add(scroll, java.awt.BorderLayout.CENTER);
		} else if (showHideSplitterButton.getText().indexOf("Show") > -1) {
			showHideSplitterButton.setText("Hide Containers");
			split.setDividerLocation(200);
			// split.setDividerLocation(split.getLastDividerLocation());
			// this.remove(scroll);
			// split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftpanel, scroll);
			// split.setResizeWeight(0.2);
			// this.add(split, java.awt.BorderLayout.CENTER);
		}
	}

	private void enableSaveButton() {
		MasterController.getGUIComponent().enableSaveButtons();
		// this.revalidate();
	}

	public void createMonomerPlateFromSynthesisPlan(Container selectedContainerType) 
	{
		fillMonomerPalteDialogPanel = new JPanel(); // vb 5/18 If we don't new it, the selected container will not refresh.
		fillMonomerPalteDialogPanel.setLayout(fillMonomerPalteFormLayout);

		ArrayList monomerLists = stepModel.getMonomers();
		JComboBox monoListCombo = new JComboBox();
		for (int i = 0; i < monomerLists.size(); i++) {
			monoListCombo.addItem(ResourceKit.getABCDRoot(i));
		}

		CeNComboBox containerListCombo = new CeNComboBox();
		try {
			containerListCombo = this.getUserContainerListCombo();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to load user's container(s).", e);
		}

		fillMonomerPalteDialogPanel.add(monomeListLabel, mCellConstraints.xy(1, 2));
		fillMonomerPalteDialogPanel.add(monoListCombo, mCellConstraints.xy(3, 2));

		fillMonomerPalteDialogPanel.add(sortMonomerLabel2, mCellConstraints.xy(1, 4));
		fillMonomerPalteDialogPanel.add(sortMonomerCombo, mCellConstraints.xy(3, 4));
		fillMonomerPalteDialogPanel.add(containerLabel, mCellConstraints.xy(1, 6));
		fillMonomerPalteDialogPanel.add(containerListCombo, mCellConstraints.xy(3, 6));

		Object[] message = new Object[1];
		int index = 0;
		message[index++] = fillMonomerPalteDialogPanel;
		// Options
		String[] options = { "Fill", "Cancel" };
		int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the
				// dialog blocks
				message, // the dialog message array
				"Fill Monomer Plates from Synthesis Plan Monomer List", // the title of the dialog window
				JOptionPane.DEFAULT_OPTION, // option type
				JOptionPane.PLAIN_MESSAGE, // message type
				null, // optional icon, use null to use the default icon
				options, // options string array, will be made into buttons
				options[0] // option that should be made into a default button
				);
		switch (result) {
			case 0: // 
				try {
					Object obj = containerListCombo.getSelectedItem();
					if (obj instanceof String && ((String) obj).equals("")) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please select a container.",
								"Container Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// vbtodo add other sort criteria
					BatchesList batchesList = (BatchesList) monomerLists.get(monoListCombo.getSelectedIndex());
					List monomerBatches = batchesList.getBatchModels();
					if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_COMPOUND_NUMBER)) {
						Collections.sort(monomerBatches, new BatchNumberComparator());
					} else if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_MW)) {
						Collections.sort(monomerBatches, new BatchMWComparator());
					} else if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_BATCH_NUMBER)) {
						// assignSynthesisPlanBatchNumer(monomerBatches);
						Collections.sort(monomerBatches, new NbkBatchNumberComparator());
//					} else if (((String) sortMonomerCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_ORDER_SEQ)) {
//						Collections.sort(monomerBatches, new OrderSequenceComparator());
					}

					this.createMonomerPlates(batchesList.getBatchModels(), (Container) containerListCombo.getSelectedItem(), "",
							CeNConstants.PLATE_CREATED_FROM_SYNTHESIS_PLAN);// always using step 0
					/**
					 * Assuming there is a container info in the file
					 */
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case 1: // cancel
				break;
		}
	}

	/**
	 * Create one or more product plates using the container parameter. CPP1
	 */
	public void createProductPlate(Container selectedContainerType) {
		PlateCreateDialog plateCreateDialog = new PlateCreateDialog(MasterController.getGUIComponent(), false);
		plateCreateDialog.setVisible(true);
		/*
		 * JPanel topPanel = new JPanel(); fillProductPalteDialogPanel = new JPanel(); 
		 * // vb 5/18 If we don't new it, the selected container will not 
		 * refresh. fillProductPalteDialogPanel.setLayout(createProductPlateDialoglayout1);
		 * 
		 * //set up the container combo 
		 * CeNComboBox myContainerListCombo = new CeNComboBox(); 
		 * try { 
		 * 		myContainerListCombo = this.getUserContainerListCombo(); 
		 * } catch (Exception e) {
		 * 		CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "Unable to retrieve user's containers", e); 
		 * }
		 * 
		 * int row = 2;
		 * 
		 * fillProductPalteDialogPanel.add(containerLabel4ProductPlate, mCellConstraints.xy(1, row));
		 * fillProductPalteDialogPanel.add(myContainerListCombo, mCellConstraints.xyw(3, row,5)); row += 2;
		 * fillProductPalteDialogPanel.add(sortProductLabel, mCellConstraints.xy(1, row));
		 * fillProductPalteDialogPanel.add(sortProductCombo, mCellConstraints.xyw(3, row,5)); row += 2;
		 * 
		 * JPanel innerPanel = new JPanel(); innerPanel.setLayout(createProductPlateDialoglayout2); row = 2;
		 * innerPanel.add(stepWiseRadio, mCellConstraints.xy(1, row)); 
		 * // result.add(stepCombo1, cc.xyw(3, row, 5)); row += 2;
		 * innerPanel.add(stepWiseSubIndexedRadio, mCellConstraints.xy(1, row));
		 * 
		 * innerPanel.add(lowertf, mCellConstraints.xy(3, row)); 
		 * innerPanel.add(toLable, mCellConstraints.xy(5, row));
		 * innerPanel.add(highertf, mCellConstraints.xy(7, row)); row += 2; 
		 * innerPanel.add(useProductsLable, mCellConstraints.xy(1, row)); 
		 * innerPanel.add(productTimesUsedJSpinner, mCellConstraints.xy(3, row));
		 * //fillProductPalteDialogPanel.add(timessLable, mCellConstraints.xy(5, row));
		 * 
		 * CollapsiblePane collapsiblePanel = new CollapsiblePane("Advanced Options"); 
		 * collapsiblePanel.setBorder(null);
		 * collapsiblePanel.setStyle(CollapsiblePane.TREE_STYLE); 
		 * collapsiblePanel.setBackground(CeNConstants.BACKGROUND_COLOR);
		 * collapsiblePanel.setSteps(1); collapsiblePanel.setStepDelay(0); 
		 * collapsiblePanel.setOpaque(false);
		 * collapsiblePanel.getContentPane().setLayout(new BorderLayout()); 
		 * collapsiblePanel.getContentPane().add(innerPanel); 
		 * try { collapsiblePanel.setCollapsed(false); } catch (PropertyVetoException e) { e.printStackTrace(); }
		 * collapsiblePanel.addCollapsiblePaneListener(new CollapsiblePaneHelper(topPanel, collapsiblePanel));
		 * topPanel.setLayout(new BorderLayout()); 
		 * topPanel.add(this.fillProductPalteDialogPanel, BorderLayout.PAGE_START);
		 * topPanel.add(collapsiblePanel, BorderLayout.PAGE_END); 
		 * //validate();
		 * 
		 * Object[] message = new Object[1]; 
		 * int index = 0; 
		 * message[index++] = topPanel; 
		 * // Options String[] options = { "Fill", "Cancel" }; 
		 * int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(), // the parent that the dialog blocks 
		 * 			message,  // the dialog message array 
		 * 			"Fill Product Plates from Synthesis Plan Product Lists", // the title of the dialog window 
		 * 			JOptionPane.DEFAULT_OPTION, // option type 
		 * 			JOptionPane.PLAIN_MESSAGE, // message type 
		 * 			null, // optional icon, use null to use the default icon 
		 * 			options, // options string array, will be made into buttons 
		 * 			options[1] // option that should be made into a default // button ); 
		 * switch (result) { case 0: // if (log.isInfoEnabled()) {
		 * log.info("Starting product plates build"); } long startTime = System.currentTimeMillis(); try { if
		 * (myContainerListCombo.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
		 * "Please select a container.", "Container Error", JOptionPane.ERROR_MESSAGE); return; } String errorMessage =
		 * isValidProductContainer((Container) myContainerListCombo.getSelectedItem(), validContainerTypes); if (errorMessage !=
		 * null ) { JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
		 * JOptionPane.ERROR_MESSAGE); return; } BatchesList batchesList = (BatchesList) stepModel.getProducts().get(0); if
		 * (!batchesList.getPosition().equals("P1"))//Constant should be moved to CeNConstants later. batchesList = (BatchesList)
		 * stepModel.getProducts().get(1); //There will only be two batchesLists, one for Products (P1),other one for User added
		 * Batches (PUA).
		 * 
		 * List productBatches = batchesList.getBatchModels(); // vbtodo add other sort criteria if (((String)
		 * sortProductCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_BATCH_NUMBER)) { Collections.sort(productBatches, new
		 * NbkBatchNumberComparator()); } else if (((String) sortProductCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_MW)) {
		 * Collections.sort(productBatches, new BatchMWComparator()); } if (stepWiseRadio.isSelected()) {
		 * createProductPlates(productBatches, (Container) myContainerListCombo.getSelectedItem()); } else if
		 * (stepWiseSubIndexedRadio.isSelected()) {
		 * 
		 * int from; int to; try { from = Integer.parseInt(lowertf.getText()); to = Integer.parseInt(highertf.getText()); if (from <
		 * 0 || to < 1 || from > productBatches.size() || to > productBatches.size() || from >= to) throw new RuntimeException(); }
		 * catch (RuntimeException e) { JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
		 * "Invalid sub set in current Rxn step is entered.", "Invalid numbers", JOptionPane.ERROR_MESSAGE); return; }
		 * createProductPlates(productBatches.subList(from, to), (Container) myContainerListCombo.getSelectedItem());
		 * 
		 * }
		 *//**
		 * Assuming there is a container info in the file
		 */
		/*
		 * } catch (Exception ex) { ex.printStackTrace(); } long endTime = System.currentTimeMillis(); if (log.isInfoEnabled()) {
		 * log.info("Total time to build all plates: " + (endTime - startTime) + " ms"); } break; case 1: // cancel break; }
		 */
	}

	private void exportExcelButton() {

	}

	class PlateCreateDialog extends CeNDialog implements CollapsiblePaneListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2812155971969274916L;
		PanelBuilder builder = null;
		javax.swing.JButton fillBtn;
		javax.swing.JButton cancelBtn;
		CollapsiblePane collapsiblePanel = null;
		CeNComboBox myContainerListCombo = new CeNComboBox();
		private JLabel toLable = new JLabel(" to ");
		private JTextField lowertf = new JTextField();
		private JTextField highertf = new JTextField();

		/** Creates new form Test2 */
		public PlateCreateDialog(JFrame parent, boolean modal) {
			super(parent, modal);
			initComponents();
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			Dimension labelSize = getPreferredSize();
			setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

			// this.show();

			this.setTitle("Fill Product Plates from Synthesis Plan Product List");
		}

		/**
		 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content
		 * of this method is always regenerated by the Form Editor.
		 */
		// GEN-BEGIN:initComponents
		// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
		private void initComponents() {
			fillBtn = new JButton();
			cancelBtn = new JButton();
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // vb todo dispose
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(this.getPanel1(), BorderLayout.PAGE_START);
			this.getContentPane().add(this.getInnerPanel(), BorderLayout.CENTER);
			this.getContentPane().add(this.getPanel2(), BorderLayout.PAGE_END);
			validate();
			pack();
			fillBtn.setText("  Fill  ");
			fillBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					createPeoductPlates();
				}
			});

			cancelBtn.setText("Cancel");
			cancelBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelBtnActionPerformed(evt);
				}
			});

			// ActionListener containerActionListener = new ActionListener() {
			// public void actionPerformed(ActionEvent actionEvent) {
			// if (log.isInfoEnabled()) {
			// log.info("Starting product plates build");
			// }
			// long startTime = System.currentTimeMillis();
			// try {
			// if (myContainerListCombo.getSelectedIndex() == 0) {
			// JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please select a container.", "Container Error",
			// JOptionPane.ERROR_MESSAGE);
			// return;
			// }
			// String errorMessage = isValidProductContainer((Container) myContainerListCombo.getSelectedItem(),
			// validContainerTypes);
			// if (errorMessage != null ) {
			// JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
			// JOptionPane.ERROR_MESSAGE);
			// return;
			// }
			// BatchesList batchesList = (BatchesList) stepModel.getProducts().get(0);
			// if (!batchesList.getPosition().equals("P1"))//Constant should be moved to CeNConstants later.
			// batchesList = (BatchesList) stepModel.getProducts().get(1); //There will only be two batchesLists, one for Products
			// (P1),other one for User added Batches (PUA).
			//								
			// List productBatches = batchesList.getBatchModels();
			// // vbtodo add other sort criteria
			// if (((String) sortProductCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_BATCH_NUMBER)) {
			// Collections.sort(productBatches, new NbkBatchNumberComparator());
			// } else if (((String) sortProductCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_MW)) {
			// Collections.sort(productBatches, new BatchMWComparator());
			// }
			// if (stepWiseRadio.isSelected()) {
			// createProductPlates(productBatches, (Container) myContainerListCombo.getSelectedItem());
			// } else if (stepWiseSubIndexedRadio.isSelected()) {
			//								
			// int from;
			// int to;
			// try {
			// from = Integer.parseInt(lowertf.getText());
			// to = Integer.parseInt(highertf.getText());
			// if (from < 0 || to < 1 || from > productBatches.size() || to > productBatches.size() || from >= to)
			// throw new RuntimeException();
			// } catch (RuntimeException e) {
			// JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Invalid sub set in current Rxn step is entered.",
			// "Invalid numbers", JOptionPane.ERROR_MESSAGE);
			// return;
			// }
			// createProductPlates(productBatches.subList(from, to), (Container) myContainerListCombo.getSelectedItem());
			//								
			// }
			// //*Assuming there is a container info in the file*/
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
			// long endTime = System.currentTimeMillis();
			// if (log.isInfoEnabled()) {
			// log.info("Total time to build all plates: " + (endTime - startTime) + " ms");
			// }
			// }
			// };
			pack();
		}// </editor-fold>//GEN-END:initComponents

		protected void createPeoductPlates() {
			if (log.isInfoEnabled()) {
				log.info("Starting product plates build");
			}
			long startTime = System.currentTimeMillis();
			try {
				if (myContainerListCombo.getSelectedIndex() == 0) {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Please select a container.",
							"Container Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String errorMessage = isValidProductContainer((Container) myContainerListCombo.getSelectedItem(),
						validContainerTypes);
				if (errorMessage != null) {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				BatchesList batchesList = (BatchesList) stepModel.getProducts().get(0);
				if (!batchesList.getPosition().equals(CeNConstants.PRODUCTS_DESIGN_DSP))
					batchesList = (BatchesList) stepModel.getProducts().get(1); // There will only be two batchesLists, one for
																			    // Products (P1),other one for User added Batches (PUA).

				List productBatches = batchesList.getBatchModels();
				// vbtodo add other sort criteria
				if (((String) sortProductCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_BATCH_NUMBER)) {
					Collections.sort(productBatches, new NbkBatchNumberComparator());
				} else if (((String) sortProductCombo.getSelectedItem()).equals(CeNConstants.SORT_BY_MW)) {
					Collections.sort(productBatches, new BatchMWComparator());
				}
				if (stepWiseRadio.isSelected()) {
					createProductPlates(productBatches, (Container) myContainerListCombo.getSelectedItem());
				} else if (stepWiseSubIndexedRadio.isSelected()) {
					int from;
					int to;
					try {
						from = Integer.parseInt(lowertf.getText());
						to = Integer.parseInt(highertf.getText());
						if (from < 1 || to < 1 || from > productBatches.size() || to > productBatches.size() || from > to)
							throw new RuntimeException();
					} catch (RuntimeException e) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
								"Invalid subset in current Rxn step is entered.", "Invalid numbers", JOptionPane.ERROR_MESSAGE);
						return;
					}
					createProductPlates(productBatches.subList(from - 1, to), (Container) myContainerListCombo.getSelectedItem());
				}
				/**
				 * Assuming there is a container info in the file
				 */
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			long endTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info("Total time to build all plates: " + (endTime - startTime) + " ms");
			}
			this.dispose();
		}

		private Component getPanel1() {
			FormLayout createProductPlateDialoglayout1 = new FormLayout(
					"20dlu, left:max(70dlu;pref), 5dlu, fill:50dlu, 5dlu, center:15dlu, 5dlu,fill:50dlu ",// columns
					"5dlu,pref, 10dlu, pref, pref, 5dlu"); // rows
			fillProductPalteDialogPanel = new JPanel(); // vb 5/18 If we don't new it, the selected container will not refresh.
			fillProductPalteDialogPanel.setLayout(createProductPlateDialoglayout1);
			// set up the container combo
			try {
				myContainerListCombo = getUserContainerListCombo();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
						"Unable to retrieve user's container(s)", e);
			}

			int row = 2;

			fillProductPalteDialogPanel.add(containerLabel4ProductPlate, mCellConstraints.xy(2, row));
			fillProductPalteDialogPanel.add(myContainerListCombo, mCellConstraints.xyw(4, row, 5));
			row += 2;
			fillProductPalteDialogPanel.add(sortProductLabel, mCellConstraints.xy(2, row));
			fillProductPalteDialogPanel.add(sortProductCombo, mCellConstraints.xyw(4, row, 5));
			return fillProductPalteDialogPanel;
		}

		private Component getPanel2() {
			FormLayout formLayout = new FormLayout("75dlu, right:40dlu, 15dlu, left:40dlu, 10dlu", "10dlu,15dlu,10dlu");
			builder = new PanelBuilder(formLayout);
			CellConstraints cc1 = new CellConstraints();
			builder.add(fillBtn, cc1.xy(2, 2));
			builder.add(cancelBtn, cc1.xy(4, 2));
			return builder.getPanel();
		}

		private Component getInnerPanel() {
			JPanel innerPanel = new JPanel();
			FormLayout createProductPlateDialoglayout2 = new FormLayout(
					"20dlu, left:max(70dlu;pref), 5dlu, fill:50dlu, 5dlu, center:15dlu, 5dlu,fill:50dlu, 20dlu ",// columns
					"5dlu, pref, 5dlu, pref, 5dlu, pref,5dlu, pref, 5dlu");
			innerPanel.setLayout(createProductPlateDialoglayout2);
			int row = 2;
			innerPanel.add(synthesisPlanProductsLabel, mCellConstraints.xyw(2, row, 7));

			row += 2;
			innerPanel.add(stepWiseRadio, mCellConstraints.xy(2, row));
			// result.add(stepCombo1, cc.xyw(3, row, 5));
			row += 2;
			innerPanel.add(stepWiseSubIndexedRadio, mCellConstraints.xy(2, row));

			innerPanel.add(lowertf, mCellConstraints.xy(4, row));
			innerPanel.add(toLable, mCellConstraints.xy(6, row));
			innerPanel.add(highertf, mCellConstraints.xy(8, row));
			row += 2;
			innerPanel.add(useProductsLable, mCellConstraints.xy(2, row));
			innerPanel.add(productTimesUsedJSpinner, mCellConstraints.xy(4, row));
			innerPanel.add(noOfTimesLable, mCellConstraints.xyw(6, row, 3));
			// fillProductPalteDialogPanel.add(timessLable, mCellConstraints.xy(5, row));

			collapsiblePanel = new CollapsiblePane("Protocol Development");
			collapsiblePanel.setBorder(null);
			collapsiblePanel.setStyle(CollapsiblePane.TREE_STYLE);
			collapsiblePanel.setBackground(CeNConstants.BACKGROUND_COLOR);
			collapsiblePanel.setSteps(1);
			collapsiblePanel.setStepDelay(0);
			collapsiblePanel.setOpaque(false);
			collapsiblePanel.getContentPane().setLayout(new BorderLayout());
			collapsiblePanel.getContentPane().add(innerPanel);
			try {
				collapsiblePanel.setCollapsed(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			collapsiblePanel.addCollapsiblePaneListener(this);

			if (stepWiseRadio.isSelected())
				enableRangeTextBoxes(false);

			stepWiseSubIndexedRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					enableRangeTextBoxes(true);
				}
			});
			stepWiseRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					enableRangeTextBoxes(false);
				}
			});
			return collapsiblePanel;
		}

		private void enableRangeTextBoxes(boolean enable) {
			lowertf.setEditable(enable);
			highertf.setEditable(enable);
		}

		// GEN-FIRST:event_cancelBtnActionPerformed
		private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {
			this.dispose();
		}// GEN-LAST:event_cancelBtnActionPerformed

		// GEN-FIRST:event_fillBtnActionPerformed
		private void fillBtnActionPerformed(java.awt.event.ActionEvent evt) {
			this.dispose();
		}// GEN-LAST:event_fillBtnActionPerformed

		public void paneCollapsed(CollapsiblePaneEvent arg0) {
			this.remove(collapsiblePanel);
			getContentPane().add(collapsiblePanel, BorderLayout.CENTER);
			validate();
			pack();
		}

		public void paneCollapsing(CollapsiblePaneEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void paneExpanded(CollapsiblePaneEvent arg0) {
			this.remove(collapsiblePanel);
			getContentPane().add(collapsiblePanel, BorderLayout.CENTER);
			validate();
			pack();
		}

		public void paneExpanding(CollapsiblePaneEvent arg0) {
		}

		public void componentResized(ComponentEvent paramComponentEvent) {
			// TODO Auto-generated method stub
			
		}

		public void componentMoved(ComponentEvent paramComponentEvent) {
			// TODO Auto-generated method stub
			
		}

		public void componentShown(ComponentEvent paramComponentEvent) {
			// TODO Auto-generated method stub
			
		}

		public void componentHidden(ComponentEvent paramComponentEvent) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void loadCompoundManagementOrders(final String selectedOderId, String selectedOption, final boolean addContainerFlag,
			final Container selectedContainer, final String sortMonomer, final String amtToUse) 
	{
		final String selectedOptionString = selectedOption;
		final String progressStatus = "Creating Plates from Other Order ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);

		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				OrderDeliverable mOrderDeliverable = null;
				try {
					CompoundManagementOrderingHandler handler = new CompoundManagementOrderingHandler();
					mOrderDeliverable = handler.findOrderDeliverableByOrderId(selectedOderId, 
							selectedOptionString.equals("Select a Container from My Containers:"),
							sortMonomer.equals(CeNConstants.SORT_BY_ORDER_SEQ));
				} catch (Exception e) {
					ceh.logErrorMsgWithoutDisplay(e.getMessage(), "Error importing Other Order(" + selectedOderId + ") into CeN");
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Error importing Other Order into CeN");
					return null;
				}
				if (mOrderDeliverable == null) {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "No data found for Order ID "
											+ selectedOderId + ".", "Plate creation error", JOptionPane.ERROR_MESSAGE);
				} else {
					log.info("OrderDeliverable.hasPlate()=" + mOrderDeliverable.hasPlate() + ", OrderDeliverable.hasTubes()=" + mOrderDeliverable.hasTubes());
//					if (selectedOptionString.equals("Use Other Containers") && !mOrderDeliverable.hasPlate()) {
//						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This Order does not contain any plates.");
//						return null;
//					}
					
					List synthesisPlanMonomerBatchList = stepModel.getAllMonomerBatchModelsInThisStep();
					List<MonomerBatchModel> otherMonomerBatchesList = new ArrayList<MonomerBatchModel>();
					
					MonomerPlate[] monomerPlates = mOrderDeliverable.getMonomerPlates();
					if (mOrderDeliverable.hasPlate()) {
						for (MonomerPlate monomerPlate : monomerPlates) {
							otherMonomerBatchesList.addAll(monomerPlate.getListOfMonomerBatches());
						}
					} else if (mOrderDeliverable.hasTubes()) {
						otherMonomerBatchesList.addAll(mOrderDeliverable.getMonomerBatches());
					} else {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This Order does not contain any Plates, Vials or Tubes.");
						return null;
					}
						
					if (selectedOptionString.equals("Use Other Containers")) {
						// Disabled for testing. Please enable it if you see it disabled. Jags_todo...
						/*
						 * if (!checkBatchesExistencyInSynthesisPlan(monomerPlates, synthesisPlanMonomerBatchList)) {
						 * 		JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						 * 				"One or more Batches in this order do not exist in this Synthesis Plan design.", "Invalid Other order items",
						 * 				JOptionPane.ERROR_MESSAGE); 
						 * 		return null; 
						 * }
						 */
						ArrayList<MonomerBatchModel> equivalentSynthesisPlanBatchesList = getEquivalentSynthesisPlanBatchesList(otherMonomerBatchesList, synthesisPlanMonomerBatchList, false);
						if (equivalentSynthesisPlanBatchesList == null || equivalentSynthesisPlanBatchesList.size() == 0) {
							JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
									"There are no batches in the Other order that match your designed monomers. Please correct and try again.");
							return null;
						}

						ParallelCeNMonomerPlateBuilder monomerPlateBuilder = new ParallelCeNMonomerPlateBuilder();
						monomerPlateBuilder.setPlateSequenceNum(stepModel.getMonomerPlates());
						monomerPlateBuilder.assignPlateNumbers(monomerPlates);

						copyDeliveredAmtsToSynthesisPlan(monomerPlates, (ArrayList<MonomerBatchModel>) equivalentSynthesisPlanBatchesList, true, amtToUse);
						replaceOtherBatchWithSynthesisPlan(monomerPlates, equivalentSynthesisPlanBatchesList);
						MonomerBatchPlateCreatedEvent mPlateCreatedEvent = new MonomerBatchPlateCreatedEvent(this, Arrays.asList(monomerPlates), stepModel);
						fireNewMonomerPlateCreated(mPlateCreatedEvent);
						displayMonomerPlates(Arrays.asList(monomerPlates));

						if (addContainerFlag) {
							Container selectedCompoundManagementContainer = monomerPlates[0].getContainer();
							ContainerAddedEvent event = new ContainerAddedEvent(this, null);
							selectedCompoundManagementContainer.setCreatorId(MasterController.getUser().getNTUserID());
							event.setContainer(selectedCompoundManagementContainer);
							mContainerAddedEventListener.newContainerAdded(event);
							// addPlatesToStepModel();
						}
					} else if (selectedOptionString.equals("Select a Container from My Containers:")) {
						ArrayList<MonomerBatchModel> equivalentSynthesisPlanBatchesList = getEquivalentSynthesisPlanBatchesList(otherMonomerBatchesList, synthesisPlanMonomerBatchList, sortMonomer.equals(CeNConstants.SORT_BY_SYNTHESIS_PLAN_SEQ));
						if (equivalentSynthesisPlanBatchesList == null || equivalentSynthesisPlanBatchesList.size() == 0) {
							JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
									"There are no batches in the Other order that match your designed monomers. Please correct and try again.");
							return null;
						}
						
						copyDeliveredAmtsToSynthesisPlan(mOrderDeliverable.getMonomerTubes(), (ArrayList<MonomerBatchModel>)equivalentSynthesisPlanBatchesList, false, amtToUse);
						if (sortMonomer.equals(CeNConstants.SORT_BY_COMPOUND_NUMBER)) {
							Collections.sort(equivalentSynthesisPlanBatchesList, new CompoundNumberComparator());
						} else if (sortMonomer.equals(CeNConstants.SORT_BY_MW)) {
							Collections.sort(equivalentSynthesisPlanBatchesList, new BatchMWComparator());
						} else if (sortMonomer.equals(CeNConstants.SORT_BY_SYNTHESIS_PLAN_SEQ)) {
							// Do nothing. Tubes are not sorted prior to copyDeliveredAmtsToSynthesisPlan so results should be in design order 
						} else if (sortMonomer.equals(CeNConstants.SORT_BY_ORDER_SEQ)) {
							// Do nothing. Tubes are sorted prior to copyDeliveredAmtsToSynthesisPlan so results should be sorted 
						}
						
						createMonomerPlates(equivalentSynthesisPlanBatchesList, selectedContainer, "", CeNConstants.PLATE_CREATED_FROM_OTHER, otherMonomerBatchesList);// always using step 0

						/*
						 * Rack monomerRacks = mOrderDeliverable.getRack(); 
						 * MonomerBatchPlateCreatedEvent mPlateCreatedEvent = new MonomerBatchPlateCreatedEvent(this, Arrays.asList(monomerPlates), stepModel);
						 * fireNewMonomerPlateCreated(mPlateCreatedEvent);
						 * // displayMonomerPlates(Arrays.asList(monomerPlates));
						 */
					} else if (selectedOptionString.equals("No Containers")) {
						//This method will set the concordance match ID in the Matrack's MonomerBatchModel
						ArrayList<MonomerBatchModel> equivalentSynthesisPlanBatchesList = getEquivalentSynthesisPlanBatchesList(otherMonomerBatchesList, synthesisPlanMonomerBatchList, false);
						copyDeliveredAmtsToSynthesisPlan(monomerPlates, (ArrayList<MonomerBatchModel>) synthesisPlanMonomerBatchList, false, amtToUse);
						viewOptionsComboBox.setSelectedIndex(0);
						// refreshTable();
					}
					if (amtToUse.equals("Delivered"))
						updateAllListLevelFlags();

					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Other order successfully imported.");
					enableSaveButton();
				}
				return null;
			}

			public void finished() {
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
			}
		};
		worker.start();
	}

	private void replaceOtherBatchWithSynthesisPlan(MonomerPlate[] monomerPlates, ArrayList<MonomerBatchModel> equivalentSynthesisPlanBatchesList) {
		for (MonomerPlate monomerPlate : monomerPlates) {
			PlateWell[] plateWells = monomerPlate.getWells();
			for (PlateWell otherPlateWell : plateWells) {
				String otherMonomerID = "";
				MonomerBatchModel otherMonomerBatchModel = (MonomerBatchModel) otherPlateWell.getBatch();
				//if (otherMonomerBatchModel.getCompoundId().indexOf("-", 4) > -1)
				//	otherMonomerID = otherMonomerBatchModel.getCompoundId().substring(0,
				//			otherMonomerBatchModel.getCompoundId().indexOf("-", 4));
				//else
				//	otherMonomerID = otherMonomerBatchModel.getCompoundId();
				otherMonomerID = otherMonomerBatchModel.getConcordedCompoundID();
				for (MonomerBatchModel synthesisPlanBatch : equivalentSynthesisPlanBatchesList) {
					if (otherMonomerID.equals(synthesisPlanBatch.getCompoundId()))
						otherPlateWell.setBatch(synthesisPlanBatch);
				}
			}
		}
	}

	// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// TODO The next method need to check how make Concordance
	// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	private Map<String, String> getConcordance(String[] compoundNumbers) {
		Map<String, String> result = new HashMap<String, String>();

		for (int i = 0; i < compoundNumbers.length; i++) {
			String value = compoundNumbers[i];

			String[] list = new String[] { value };

			for (int j = 0; j < list.length; j++) {
				String key = list[j];
				result.put(key, value);
			}
		}
		return result;
	}
	
	//This method matches the monomer IDs between the Monomers Delivered and Monomers in Synthesis Plan design and returns the 
	//monomer batches that match in both the lists.
	private ArrayList<MonomerBatchModel> getEquivalentSynthesisPlanBatchesList(List<MonomerBatchModel> otherMonomerBatchesList, List<MonomerBatchModel> synthesisPlanMonomerBatchList, boolean orderBySynthesisPlan) {
		ArrayList<MonomerBatchModel> eqSynthesisPlanMonomerBatchesList = new ArrayList<MonomerBatchModel>();

		//This array would have monomerIDs by the Synthesis Plan order
		String[] design_monomerIDs = CommonUtils.getCompoundIDsFromTheList(synthesisPlanMonomerBatchList);
		try
		{
			// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// TODO The next line need to check how make Concordance
			// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            Map map = getConcordance(design_monomerIDs);
            
            //find the matching Concorded compound from Other Order and set the matching Synthesis Plan parent comp link.
            // IF there is no concordance and IDs can be same.
            int matrackListSize = otherMonomerBatchesList.size();
            for (int i = 0; i < matrackListSize; i++) {
  
            	  MonomerBatchModel otherMonomerBatch = otherMonomerBatchesList.get(i);
            	  String key = otherMonomerBatch.getCompoundId();
                  if(map.containsKey(key))
                  {
                  otherMonomerBatch.setConcordedCompoundID((String)map.get(key));
                  }else
                  {
                	  log.debug("Delivered Other monomer id:"+key+ " has no direct matching compounds found in concordance-desing monomer map");
                	  //check the match with CNF. 0093355-0000 is retuned as 00933550000. So only CNF can match this.
                	  Set keyset = map.keySet();
                	  Iterator iter = keyset.iterator();
                	  while(iter.hasNext())
                	  {
                		 String concordKey = (String)iter.next();
                		 boolean ismatch = CommonUtils.isMatch(key, concordKey);
                		 if(ismatch)
                		 {
                			 otherMonomerBatch.setConcordedCompoundID((String)map.get(concordKey)); 
                			 log.debug("Delivered Other monomer id:"+key+ " has matched with concordanceid :"+concordKey);
                			 break;
                		 }
                	  }
                	  
                  }
                 
            }
		}catch(Exception e)
		{
		log.error(e);
		}
		if (orderBySynthesisPlan) {
			for (int i = 0; i < synthesisPlanMonomerBatchList.size(); i++) {
				MonomerBatchModel synthesisPlanMonomerBatchModel = (MonomerBatchModel) synthesisPlanMonomerBatchList.get(i);
				
				for (MonomerBatchModel otherMonomerBatchModel : otherMonomerBatchesList) {
					if (CommonUtils.isMatch(synthesisPlanMonomerBatchModel.getCompoundId(), otherMonomerBatchModel.getConcordedCompoundID())) {
						eqSynthesisPlanMonomerBatchesList.add(synthesisPlanMonomerBatchModel);
						break;
					}
				}
			}
		} else {
			for (MonomerBatchModel otherMonomerBatchModel : otherMonomerBatchesList) {
				for (int i = 0; i < synthesisPlanMonomerBatchList.size(); i++) {
					MonomerBatchModel synthesisPlanMonomerBatchModel = (MonomerBatchModel) synthesisPlanMonomerBatchList.get(i);
	
					if (CommonUtils.isMatch(synthesisPlanMonomerBatchModel.getCompoundId(), otherMonomerBatchModel.getConcordedCompoundID())) {
						eqSynthesisPlanMonomerBatchesList.add(synthesisPlanMonomerBatchModel);
						break;
					}
				}
			}
		}
		
		return eqSynthesisPlanMonomerBatchesList;
	}

	private boolean checkBatchesExistencyInSynthesisPlan(MonomerPlate[] monomerPlates, List<MonomerBatchModel> synthesisPlanMonomerBatchList) {
		for (MonomerPlate monomerPlate : monomerPlates) {
			String[] compoundIDs = monomerPlate.getBatchIDs();
			for (String compoundID : compoundIDs) {
				return isBatchExistInSynthesisPlan(compoundID.split("-"), synthesisPlanMonomerBatchList);
			}
		}
		return true;
	}

	private boolean isBatchExistInSynthesisPlan(String[] otherMonomerId, List<MonomerBatchModel> monomerBatchModelList) {
		String monomerId = otherMonomerId[0] + "-" + otherMonomerId[1];
		for (int i = 0; i < monomerBatchModelList.size(); i++) {
			MonomerBatchModel synthesisPlanMonomerBatch = (MonomerBatchModel) monomerBatchModelList.get(i);
			if (synthesisPlanMonomerBatch.getMonomerId().equals(monomerId)) {
				if (otherMonomerId.length > 2) {
					/** For now ignore MonomerBatch number as we do not populate it for Synthesis Plan monomer batches. ***/
					/*
					 * if (asdiMonomerId.length > 3) { if (synthesisPlanMonomerBatch.getSaltForm().getCode().equals(asdiMonomerId[2]) &&
					 * synthesisPlanMonomerBatch.getBatchNumber().getBatchNumber().equals(asdiMonomerId[4])) { return
					 * synthesisPlanMonomerBatch.getBatchNumber(); } } else
					 */{
						if (synthesisPlanMonomerBatch.getSaltForm().getCode().equals(otherMonomerId[2])) {
							return true;
						} else {
							try {
								SaltCodeCache scc = SaltCodeCache.getCache();
								synthesisPlanMonomerBatch.setSaltForm(new SaltFormModel(otherMonomerId[2],
								                                               scc.getDescriptionGivenCode(otherMonomerId[2]),
								                                               scc.getMolFormulaGivenCode(otherMonomerId[2]),
								                                               scc.getMolWtGivenCode(otherMonomerId[2])));
								// If the salt code has been set back to the parent code, set the salt equiv to null
								if (SaltFormModel.isParentCode(otherMonomerId[2])) {
									synthesisPlanMonomerBatch.setSaltEquivs(0.0);
								} else {
									JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
											"One of the compound has Salt Code with out Salt EQ. Please correct it and try again.");
									return false;
								}

								return true;
							} catch (Exception e) {
								CeNErrorHandler.getInstance().logExceptionMsg(null, e);
								JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
										"CodeTableService is not available. Please try again later.");
								e.printStackTrace();
							}
						}
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public void copyDeliveredAmtsToSynthesisPlan(MonomerPlate[] monomerPlates, List<MonomerBatchModel> synthesisPlanMonomerBatchList,
			boolean moveDeliveredAmtsToWellFlag, String amtToUse) {
		for (MonomerPlate monomerPlate : monomerPlates) {
			ArrayList<MonomerBatchModel> monomerBatchesList = (ArrayList<MonomerBatchModel>) monomerPlate.getListOfMonomerBatches();
			for (MonomerBatchModel otherMonomerBatchModel : monomerBatchesList) {
				for (MonomerBatchModel synthesisPlanMonomerBatchModel : synthesisPlanMonomerBatchList) {
					if (CommonUtils.isMatch(synthesisPlanMonomerBatchModel.getCompoundId(), otherMonomerBatchModel.getConcordedCompoundID())) {
						CommonUtils.copyCompoundManagementMonomerSaltInfoToSynthesisPlanMonomer(otherMonomerBatchModel,synthesisPlanMonomerBatchModel);
						if (moveDeliveredAmtsToWellFlag) {
							// For RC1 there is only one well is expected for one batch.
							List<PlateWell> plateWells = monomerPlate.getPlateWellsforBatch(otherMonomerBatchModel);
							plateWells.get(0).setContainedWeightAmount(otherMonomerBatchModel.getDeliveredWeight());
							plateWells.get(0).setContainedVolumeAmount(otherMonomerBatchModel.getDeliveredVolume());
							// Replace the batch with corresponding Synthesis Plan batch so all properties will be available. Deliv wt and vol
							// should not be ccopied as they go into well.
							plateWells.get(0).setBatch(synthesisPlanMonomerBatchModel);
						}
						synthesisPlanMonomerBatchModel.setDeliveredMonomerID(otherMonomerBatchModel.getConcordedCompoundID());
						synthesisPlanMonomerBatchModel.setDeliveredWeight(otherMonomerBatchModel.getDeliveredWeight());
						synthesisPlanMonomerBatchModel.setDeliveredVolume(otherMonomerBatchModel.getDeliveredVolume());
						if (amtToUse.equals("Delivered")) {
							synthesisPlanMonomerBatchModel.setStoicWeightAmount(otherMonomerBatchModel.getDeliveredWeight());
							synthesisPlanMonomerBatchModel.setStoicVolumeAmount(otherMonomerBatchModel.getDeliveredVolume());
						}
					}
				}
			}
		}
	}

	private void copyDeliveredAmtsToSynthesisPlan(ArrayList<PlateWell<MonomerBatchModel>> monomerTubes, ArrayList<MonomerBatchModel> synthesisPlanMonomerBatchList,
			boolean moveDeliveredAmtsToWellFlag, String amtToUse) {
		for (PlateWell<MonomerBatchModel> otherMonomerTube : monomerTubes) {
			MonomerBatchModel tubeBatch = otherMonomerTube.getBatch();
			
			for (MonomerBatchModel synthesisPlanMonomerBatchModel : synthesisPlanMonomerBatchList) {
				if (CommonUtils.isMatch(synthesisPlanMonomerBatchModel.getCompoundId(), tubeBatch.getConcordedCompoundID())) {
					CommonUtils.copyCompoundManagementMonomerSaltInfoToSynthesisPlanMonomer(otherMonomerTube.getBatch(),synthesisPlanMonomerBatchModel);
					if (moveDeliveredAmtsToWellFlag) {
						otherMonomerTube.setContainedWeightAmount(tubeBatch.getDeliveredWeight());
						otherMonomerTube.setContainedVolumeAmount(tubeBatch.getDeliveredVolume());
						// Replace the batch with corresponding Synthesis Plan batch so all properties will be available. Deliv wt and vol
						// should not be ccopied as they go into well.
						otherMonomerTube.setBatch(synthesisPlanMonomerBatchModel);
					}
					//Set Other batch id to synthesisPlan batch's devlired monomer id
					synthesisPlanMonomerBatchModel.setDeliveredMonomerID(tubeBatch.getCompoundId());
					synthesisPlanMonomerBatchModel.setDeliveredWeight(tubeBatch.getDeliveredWeight());
					synthesisPlanMonomerBatchModel.setDeliveredVolume(tubeBatch.getDeliveredVolume());
					if (amtToUse.equals("Delivered")) {
						synthesisPlanMonomerBatchModel.setStoicWeightAmount(tubeBatch.getDeliveredWeight());
						synthesisPlanMonomerBatchModel.setStoicVolumeAmount(tubeBatch.getDeliveredVolume());
					}
				}
			}
		}
	}
	private void updateAllListLevelFlags() {
		ArrayList monomerLists = stepModel.getMonomers();
		// Since we do not know what monomer lists from a plate, loop is run to check all Mononer list in the current step.
		for (int i = 0; i < monomerLists.size(); i++) {
			((BatchesList) monomerLists.get(i)).updateAllListLevelFlags();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		showViews();
	}

	private void showViews() {
		String type = (String) typeComboBox.getSelectedItem();
		String view = (String) viewOptionsComboBox.getSelectedItem();

		if (type.equals(types[0])) {// monomer views
			if (view.equals(views[0])) {// table view

				// sets ligth green background
				// scroll.setBackground(new Color(189, 236, 214));
				rightSpliScroll.getViewport().add(monomerTableViewTabs, BorderLayout.CENTER);
			} else if (view.equals(views[1])) {
				rightSpliScroll.getViewport().removeAll();
				rightSpliScroll.getViewport().add(this.monomerPlateTableViewTabs, BorderLayout.CENTER);
				// rightSpliScroll.getViewport().add(mBatchDetailViewContainer, BorderLayout.EAST);

			} else if (view.equals(views[2])) {// Rack view structure
				rightSpliScroll.getViewport().removeAll();
				// loadCompoundManagementOrderButton_actionPerformed(null);
				// rightSpliScroll.getViewport().add(monomerPlatePlateViewSplit, BorderLayout.CENTER);
				rightSpliScroll.getViewport().add(monomerPlatePLateViewTabs, BorderLayout.CENTER);
				if (monomerPlatePLateViewTabs.getSelectedComponent() != null)
					((MonomerPlatePlateViewPanel) monomerPlatePLateViewTabs.getSelectedComponent()).refresh();

			} else if (view.equals(views[3])) {// Rxn view of prodcut plates
				JOptionPane.showMessageDialog(this, "This feature is not yet implemented");
				// rightSpliScroll.getViewport().add(monomerPlatePlateViewSplit2, BorderLayout.CENTER);
			}
		} else if (type.equals(types[1])) {// Products
			if (view.equals(views[0])) {// table view
				rightSpliScroll.getViewport().removeAll();
				// JPanel panel = new JPanel(new BorderLayout());
				// scroll.getViewport().add(productToolBarPanel,BorderLayout.NORTH);
				// scroll.getViewport().add(productTableViewScroll,BorderLayout.CENTER);
				// panel.add(productTableViewToolBar, BorderLayout.NORTH);
				// panel.add(productTableViewScroll, BorderLayout.CENTER);
				rightSpliScroll.getViewport().add(productTablePanel);
			} else if (view.equals(views[2])) {// Rack view of product plates
				rightSpliScroll.getViewport().removeAll();
				// loadCompoundManagementOrderButton_actionPerformed(null);
				for (Iterator it = this.plateViewPanelMap.keySet().iterator(); it.hasNext();) {
					String plateNo = (String) it.next();
					if (this.plateViewPanelMap.containsKey(plateNo)) {
						ProductPlatePlateViewPanel panel = (ProductPlatePlateViewPanel) this.plateViewPanelMap.get(plateNo);
						panel.refresh();
					}
				}
				rightSpliScroll.getViewport().add(productPlatePlateViewTabs, BorderLayout.CENTER);
			} else if (view.equals(views[1])) {// Table view of product plates
				rightSpliScroll.getViewport().removeAll();
				rightSpliScroll.getViewport().add(productPlateTableViewTabs, BorderLayout.CENTER);
			} else if (view.equals(views[3])) {// Rxn view of product plates
				JOptionPane.showMessageDialog(this, "This feature is not yet implemented");
			}
			//
		}

	}

	public ContainerTypeExPTree getContainerTypeExPTree() {
		return containerTypeExPTree;
	}

	public void addPlateCreatedEventListener(PlatesCreatedEventListener l) {
		mPlateCreationHandler.addPlateCreatedEventListener(l);
	}

	public void removePlateCreatedEventListener(PlatesCreatedEventListener l) {
		mPlateCreationHandler.removePlateCreatedEventListener(l);
	}

	protected void fireNewProductPlateCreated(ProductBatchPlateCreatedEvent event) {
		mPlateCreationHandler.fireNewProductPlateCreated(event);
		// ReactionStepModel rxnStep = (ReactionStepModel) mNotebookPageModel.getReactionSteps().get(0);
		// System.out.println("Product PLate Size"+rxnStep.getProductPlates().size());
	}

	protected void fireNewMonomerPlateCreated(MonomerBatchPlateCreatedEvent event) {
		mPlateCreationHandler.fireNewMonomerPlateCreated(event);
		// ReactionStepModel rxnStep = (ReactionStepModel) mNotebookPageModel.getReactionSteps().get(0);
		// System.out.println("Monomer PLate Size"+rxnStep.getMonomerPlates().size());
	}

	protected void fireProdcutPlateDeleted(ProductBatchPlateCreatedEvent event) {
		mPlateCreationHandler.fireProdcutPlateDeleted(event);
	}

	protected void fireMonomerPlateDeleted(MonomerBatchPlateCreatedEvent event) {
		mPlateCreationHandler.fireMonomerPlateDeleted(event);
	}

	// called by CreatPlateOptionDialog
	public void createProductPlates(List productBatches, Container selectedContainerType) {

		if (isPlated(productBatches)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"Only non-plated batches can be plated. Please remove plated batches and try again.", "Plate creation failed",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String errorMessage = isValidProductContainer(selectedContainerType, validContainerTypes);
		if (errorMessage != null) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		List newPlates = mParallelCeNProductPlateBuilder.getPlates(productBatches, selectedContainerType, mNotebookPageModel);

		stepModel.addProductPlates(newPlates);

		// System.out.println("CompoundManagementMonomerContainer::createProductPlates: Step"+stepIndex+" newPlates.size() "+newPlates.size());
		// set the step number in ProductPlate.
		for (int i = 0; i < newPlates.size(); i++) {
			ProductPlate plate = (ProductPlate) newPlates.get(i);
			plate.setStepNumber(stepIndex + "");
		}

		// Collections.sort(newPlates, new PlateNumberComparator());//sequence the plates before it is loaded to tabs.

		addProductPlatesTabs(newPlates);
		this.mNotebookPageModel.setModelChanged(true); // vb

		typeComboBox.setSelectedIndex(1);// products
		viewOptionsComboBox.setSelectedIndex(1);// Plate view of plates
		showViews();
		mNotebookPageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
		new ParallelExpModelUtils(mNotebookPageModel).setOrRefreshGuiPseudoProductPlate();
		// //System.out.println("newPlates size " + newPlates.size());
		if (newPlates.size() > 0) {
			ProductBatchPlateCreatedEvent mPlateCreatedEvent = new ProductBatchPlateCreatedEvent(this, newPlates);
			this.fireNewProductPlateCreated(mPlateCreatedEvent);
		}
		this.enableSaveButton();
	}

	private void addProductPlatesTabs(List newPlates) {
		for (int i = 0; i < newPlates.size(); i++) {
			long startTime = System.currentTimeMillis();
			ProductPlate productPlate = (ProductPlate) newPlates.get(i);
			// table views of plates
			PCeNProductPlatesTableModelConnector plateTableViewConnector = new PCeNProductPlatesTableModelConnector(productPlate,
					mNotebookPageModel); // vb 11/9 added page model
			PCeNTableModel model = new PCeNTableModel(plateTableViewConnector);
			PCeNPlateTableView plateProductView = new PCeNPlateTableView(model, 70, plateTableViewConnector);
			// register itself as a candidate for ProducBatchDetaisContainerListener
			// ParallelNotebookPageGUI.getPBDetailContainerListeners().add(plateProductView);
			// add this table to list of candidates for listSelectionListeners
			
			// TODO implement loop to check the number of listeners dynamicaly
			JScrollPane scrollTableViewPane = new JScrollPane(plateProductView);
			PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(plateProductView);
			JPanel plateTabTableViewPanel = new ProductPlateTableViewPanel(productPlate);
			plateTabTableViewPanel.setLayout(new BorderLayout());
			plateTabTableViewPanel.setBackground(Color.LIGHT_GRAY);
			plateTabTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
			plateTabTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);
			// mStaticPlateRenderers.add(mStaticPlateRenderer);
			productPlateTableViewTabs.addTab(productPlate.getLotNo(), plateTabTableViewPanel);
			// Add plate view and batch detail view
			// vb 10/31 select tab just created
			productPlateTableViewTabs.setSelectedIndex(productPlateTableViewTabs.getTabCount() - 1);

			ProductBatchDetailContainer batchDetail = new ProductBatchDetailContainer(productPlate, mNotebookPageModel);
			// vb 6/28

			ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel(productPlate, mNotebookPageModel);
			BatchSelectionListener[] listeners = new BatchSelectionListener[2];
			listeners[0] = batchDetail; // vb 6/21
			listeners[1] = reactionsViewPanel;
			plateProductView.setProductBatchDetailsContainerListeners(listeners);
			StaticPlateRenderer plateview = (StaticPlateRenderer) mCeNProductPlateBuilder.buildCeNproductPlateViewer(productPlate,
					listeners);
			// plateview.setProductPlate(mPlate);
			// JPanel batchDetailPanel = new JPanel();
			// batchDetailPanel.add(batchDetail);
			// ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel(productPlate, mNotebookPageModel);
			JScrollPane scrollBatchDetailViewPane = new JScrollPane();
			scrollBatchDetailViewPane.getViewport().add(batchDetail);
			scrollBatchDetailViewPane.setComponentPopupMenu(productPlateViewTabPopupMenu);

			ProductPlatePlateViewPanel productPlatePlateViewPanel = new ProductPlatePlateViewPanel(productPlate, plateview,
					scrollBatchDetailViewPane, // batchDetailPanel,
					batchDetail, reactionsViewPanel);

			this.plateViewPanelMap.put(productPlate.getLotNo(), productPlatePlateViewPanel);
			// ProductPlatePlateViewPanel productPlatePlateViewPanel = new ProductPlatePlateViewPanel(productPlate, plateview,
			// batchDetailPanel);
			productPlatePlateViewTabs.addTab(productPlate.getLotNo(), productPlatePlateViewPanel);
			long endTime = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info("addProductPlates elapsed time: " + (endTime - startTime) + " ms");
			}
		}
	}

	private boolean isPlated(List productBatches) {
		for (int i = 0; i < productBatches.size(); i++) {
			if (!mNotebookPageModel.getNonPlatedBatches().contains(productBatches.get(i)))
				return true;
		}
		return false;
	}

	// called by loadOrderNoContainerFromFile in the class
	private List<MonomerPlate> createMonomerPlates(List monomerBatches, Container selectedContainerType, String name,
			String plateCreationSource) {
		String containerType = selectedContainerType.getContainerType();
		if (containerType == null || containerType.length() == 0 || !isValidMonomerContainer(selectedContainerType)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"The monomer plate must be a rack or plate container.", "Container Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		parallelCeNMonomerPlateBuilder.setPlateSequenceNum(stepModel.getMonomerPlates());
		List newPlates = parallelCeNMonomerPlateBuilder.getPlates(monomerBatches, selectedContainerType, name, mNotebookPageModel);
		// vb 5/24 fill in the source of the plate
		for (Iterator it = newPlates.iterator(); it.hasNext();) {
			AbstractPlate plate = (AbstractPlate) it.next();
			plate.setPlateCreationSource(plateCreationSource);
		}
		// System.out.println("newPlates size " + newPlates.size());
		if (newPlates.size() > 0) {
			MonomerBatchPlateCreatedEvent mPlateCreatedEvent = new MonomerBatchPlateCreatedEvent(this, newPlates, stepModel);
			this.fireNewMonomerPlateCreated(mPlateCreatedEvent);
		}
		// enableSaveButton();
		ExperimentPageUtils mExperimentPageUtils = new ExperimentPageUtils();
		mExperimentPageUtils.refreshMonomerBatchToPlateWellsMapping(mNotebookPageModel);
		this.mNotebookPageModel.setModelChanged(true); // vb 5/21
		displayMonomerPlates(newPlates);
		this.enableSaveButton();
		return newPlates;
	}

	private void createMonomerPlates(List monomerBatches, Container selectedContainerType, String name, String plateCreationSource,
			List<MonomerBatchModel> otherMonomerBatchesList) {
		String containerType = null;
		if (selectedContainerType != null)
			containerType = selectedContainerType.getContainerType();
		if (containerType == null || containerType.length() == 0 || !isValidMonomerContainer(selectedContainerType)) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
					"The Monomer Plate must have a valid Rack or Plate Container.", "Container Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		parallelCeNMonomerPlateBuilder.setPlateSequenceNum(stepModel.getMonomerPlates());
		List newPlates = parallelCeNMonomerPlateBuilder.getPlates(monomerBatches, selectedContainerType, name, mNotebookPageModel);
		// vb 5/24 fill in the source of the plate
		for (Iterator it = newPlates.iterator(); it.hasNext();) {
			MonomerPlate plate = (MonomerPlate) it.next();
			setDeliveredWellsAmtsFromMtatrack(plate, otherMonomerBatchesList);
			plate.setPlateCreationSource(plateCreationSource);
		}
		// System.out.println("newPlates size " + newPlates.size());
		if (newPlates.size() > 0) {
			MonomerBatchPlateCreatedEvent mPlateCreatedEvent = new MonomerBatchPlateCreatedEvent(this, newPlates, stepModel);
			this.fireNewMonomerPlateCreated(mPlateCreatedEvent);
		}
		// enableSaveButton();
		ExperimentPageUtils mExperimentPageUtils = new ExperimentPageUtils();
		mExperimentPageUtils.refreshMonomerBatchToPlateWellsMapping(mNotebookPageModel);
		this.mNotebookPageModel.setModelChanged(true); // vb 5/21
		displayMonomerPlates(newPlates);
		this.enableSaveButton();
	}

	private void setDeliveredWellsAmtsFromMtatrack(MonomerPlate plate, List<MonomerBatchModel> otherMonomerBatchesList) {
		PlateWell[] plateWells = plate.getWells();
		for (MonomerBatchModel otherMonomerBatchModel : otherMonomerBatchesList) {
			String otherMonomerID = null;
			int saltIdx =  otherMonomerBatchModel.getCompoundId().indexOf("-", 4);
			if (saltIdx >= 0)
				otherMonomerID = otherMonomerBatchModel.getCompoundId().substring(0, otherMonomerBatchModel.getCompoundId().indexOf("-", 4));
			else
				otherMonomerID = otherMonomerBatchModel.getCompoundId();
			for (PlateWell plateWell : plateWells) {
				if (plateWell == null)
					continue;
				MonomerBatchModel synthesisPlanMonomerBatchModel = (MonomerBatchModel) plateWell.getBatch();
				// Check for skipped wells.Skipped wells will not have a batch.
				if (synthesisPlanMonomerBatchModel != null && synthesisPlanMonomerBatchModel.getCompoundId().equals(otherMonomerID)) {
					plateWell.setContainedWeightAmount(otherMonomerBatchModel.getDeliveredWeight());
					plateWell.setContainedVolumeAmount(otherMonomerBatchModel.getDeliveredVolume());
				}
			}
		}
	}

	private void displayMonomerPlates(List newMonomerPlates) {
		// ArrayList monomerPlates = step.getMonomerPlates();
		// to add tabs in table plate viewers
		/*
		 * for (int i = 0; i < newMonomerPlates.size(); i++) { 
		 * 		MonomerPlate mPlate = (MonomerPlate) newMonomerPlates.get(i); 
		 * 		JPanel plateview = mCeNMonomerPlaterBuilder.buildCeNMonomerPlateViewer(mPlate);
		 * 		monomerPlatePLateViewTabs.add(mPlate.getPlateBarCode(), plateview);
		 * 		monomerPlateTableViewTabs.add(mPlate.getPlateBarCode(), createMonomePlateTableViewPanel(mPlate)); 
		 * }
		 */
		createAddMonomerPlateDisplays(newMonomerPlates);		
	}

	// vb 6/1
	private void createAddMonomerPlateDisplays(List monomerPlates) {
		// ArrayList monomerPlates = step.getMonomerPlates();
		// to add tabs in table plate viewers
		for (int i = 0; i < monomerPlates.size(); i++) {
			MonomerPlate mPlate = (MonomerPlate) monomerPlates.get(i);
			MonomerBatchDetailContainer batchDetail = new MonomerBatchDetailContainer(mPlate, mNotebookPageModel, stepModel);
			StaticPlateRenderer plateview = (StaticPlateRenderer) mCeNMonomerPlaterBuilder.buildCeNMonomerPlateViewer(mPlate,
					batchDetail);
			this.disposeList.add(plateview);
			// plateview.setMonomerPlate(mPlate);
			// JPanel batchDetailPanel = new JPanel();
			// vb 7/16
			ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel("");
			MonomerPlatePlateViewPanel monomerPlatePlateViewPanel = new MonomerPlatePlateViewPanel(mPlate, plateview, batchDetail, // vb
					// 6/28
					// batchDetailPanel,
					reactionsViewPanel);

			// batchDetailPanel.add(batchDetail);
			// ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel("");
			// MonomerPlatePlateViewPanel monomerPlatePlateViewPanel = new MonomerPlatePlateViewPanel(mPlate,
			// 					plateview, batchDetailPanel, reactionsViewPanel);
			// monomerPlatePlateViewPanel.add(plateview);
			// monomerPlatePlateViewPanel.add(this.getPlateDetailsPanel(batchDetail));
			monomerPlatePLateViewTabs.add(mPlate.getPlateBarCode(), monomerPlatePlateViewPanel);
			monomerPlateTableViewTabs.add(mPlate.getPlateBarCode(), createMonomePlateTableViewPanel(mPlate));
			// vb 10/31 set current tab to the one that was just created
			monomerPlateTableViewTabs.setSelectedIndex(monomerPlateTableViewTabs.getTabCount() - 1);
		}

		if (typeComboBox.getSelectedIndex() != 0) {		// Viewing Monomers?
			typeComboBox.setSelectedIndex(0);	
			viewOptionsComboBox.setSelectedIndex(1);	// Switch to Plate Reactants view
		} else if (viewOptionsComboBox.getSelectedIndex() == 0)	// Viewing Separate Reactants?
			viewOptionsComboBox.setSelectedIndex(1);	// Switch to Plate Reactants view, i.e. if viewing plates already, leave us there.
		showViews();
	}

	// vb 6/1 this needs more testing and work
	public void deleteMonomerPlate() {
		int confirmation = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(), "Do you want to remove this plate?",
				"Remove Plate", JOptionPane.YES_NO_OPTION);
		if (confirmation == JOptionPane.NO_OPTION)
			return;
		MonomerPlateContainer monomerPlateContainer = null;
		JTabbedPane tempPane = ((JTabbedPane) rightSpliScroll.getViewport().getComponent(0));
		Object obj = tempPane.getSelectedComponent();
		if (obj instanceof MonomerPlateContainer) {
			monomerPlateContainer = (MonomerPlateContainer) obj;
		}
		if (monomerPlateContainer != null) {
			MonomerPlate monomerPlate = monomerPlateContainer.getMonomerPlate();
			ArrayList monomerPlatesList = new ArrayList();
			monomerPlatesList.add(monomerPlate);
			if (deleteMonomerPlate(monomerPlate)) {
				MonomerBatchPlateCreatedEvent mPlateCreatedEvent = new MonomerBatchPlateCreatedEvent(this, monomerPlatesList,
						stepModel);
				fireMonomerPlateDeleted(mPlateCreatedEvent);
				int index = tempPane.indexOfTab(monomerPlate.getPlateBarCode());

				monomerPlateTableViewTabs.remove(index);
				monomerPlatePLateViewTabs.remove(index);
				// fireMonomerPlateDeleted(monomerPlate);
			}
		}
	}

	private boolean deleteMonomerPlate(MonomerPlate monomerPlate) {
		String[] plateKeys = { monomerPlate.getKey() };
		PlateWell[] plateWells = monomerPlate.getWells();
		ArrayList<String> plateWellKeys = new ArrayList<String>();
		if (plateWells != null) {
			for (PlateWell well : plateWells) {
				plateWellKeys.add(well.getKey());
			}
		}
		try {
			if (monomerPlate.isLoadedFromDB()) {
				storageDelegate.deletePlateWells(plateWellKeys.toArray(new String[plateWells.length]), MasterController.getUser().getSessionIdentifier());
				storageDelegate.deletePlates(plateKeys, MasterController.getUser().getSessionIdentifier());
			}
			// Where is the ReactionStepModel that has the plate list?
			this.stepModel.deleteMonomerPlateFromList(monomerPlate);
			resetBatchDeliveredAmts(monomerPlate);
			return true;
		} catch (StorageException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void resetBatchDeliveredAmts(MonomerPlate monomerPlate) {
		List<MonomerBatchModel> monomerBatchesList = monomerPlate.getListOfMonomerBatches();
		for (MonomerBatchModel monomerBatchModel : monomerBatchesList) {
			monomerBatchModel.setDeliveredWeight(new AmountModel(UnitType.MASS));
			monomerBatchModel.setDeliveredVolume(new AmountModel(UnitType.VOLUME));
		}
	}

	// vb 6/1 this needs more testing and work
	public void deleteProductPlate() {
		ProductPlateContainer productPlateContainer = null;
		JTabbedPane tempPane = ((JTabbedPane) rightSpliScroll.getViewport().getComponent(0));
		Object obj = tempPane.getSelectedComponent();
		// Code required to avoid Delete Product plate Popup.
		if (obj instanceof ProductPlateContainer) {
			productPlateContainer = (ProductPlateContainer) obj;
		}
		if (productPlateContainer != null) {
			productPlateContainer = (ProductPlateContainer) obj;
			ProductPlate productPlate = productPlateContainer.getProductPlate();
			if (!productPlate.isEditable()) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"Registered plates can not be deleted from the system.", "Plate Delete failed.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int confirmation = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(),
					"The delete operation will save all changes. Do you want to remove this plate and save changes?",
					"Remove Plate", JOptionPane.YES_NO_OPTION);
			if (confirmation == JOptionPane.NO_OPTION)
				return;

			ArrayList deleteProductPlatesList = new ArrayList();
			deleteProductPlatesList.add(productPlate);
			if (deleteProductPlate(productPlate)) {
				ProductBatchPlateCreatedEvent mPlateCreatedEvent = new ProductBatchPlateCreatedEvent(this, deleteProductPlatesList);
				fireProdcutPlateDeleted(mPlateCreatedEvent);
				productPlate = null;
			}
		}
		mNotebookPageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
		new ParallelExpModelUtils(mNotebookPageModel).setOrRefreshGuiPseudoProductPlate();
	}

	private boolean deleteProductPlate(ProductPlate productPlate) {
		String[] plateKeys = { productPlate.getKey() };
		PlateWell[] plateWells = productPlate.getWells();
		ArrayList<String> plateWellKeys = new ArrayList<String>();
		if (plateWells != null) {
			for (PlateWell well : plateWells) {
				plateWellKeys.add(well.getKey());
			}
		}
		try {
			if (productPlate.isLoadedFromDB())
				storageDelegate.deletePlateWells(plateWellKeys.toArray(new String[plateWells.length]), MasterController.getUser().getSessionIdentifier());
				storageDelegate.deletePlates(plateKeys, MasterController.getUser().getSessionIdentifier());
			if (productPlate.getPlateType().equals(CeNConstants.PLATE_TYPE_REGISTRATION))
				mNotebookPageModel.deleteRegistrationPlate(productPlate);
			else
				this.stepModel.deleteProductPlateFromList(productPlate);
			return true;
		} catch (StorageException e) {
			e.printStackTrace();
			return false;
		}
	}

	private MonomerPlateTableViewPanel createMonomePlateTableViewPanel(MonomerPlate mPlate) {
		MonomerPlateTableViewPanel plateTabTableViewPanel = new MonomerPlateTableViewPanel(new BorderLayout());
		plateTabTableViewPanel.setMonomerPlate(mPlate);
		// ParallelCeNMonomerPlateTableViewControllerUtility plateTableViewConnector = new
		// ParallelCeNMonomerPlateTableViewControllerUtility(
		// mPlate);
		PCeNMonomerPlatesTableModelConnector plateTableViewConnector = new PCeNMonomerPlatesTableModelConnector(stepModel, mPlate,
				this.mNotebookPageModel);
		PCeNTableModel model = new PCeNTableModel(plateTableViewConnector);
		PCeNTableView plateProductView = new PCeNMonomer_TableView(model, 70, plateTableViewConnector);
		JScrollPane scrollTableViewPane = new JScrollPane(plateProductView);
		// plateProductView.getSelectionModel().addListSelectionListener(pBatchDetailsContainerListener);
		PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(plateProductView);
		plateTabTableViewPanel.setBackground(Color.LIGHT_GRAY);
		plateTabTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
		plateTabTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);
		return plateTabTableViewPanel;
	}

	private JPanel createProductTableView(PCeNTableView mParallelCeNTableView, PCeNTableViewToolBar productTableViewToolBar) {
		JPanel productTableViewPanel = new JPanel(new BorderLayout());
		JScrollPane scrollTableViewPane = new JScrollPane(mParallelCeNTableView);
		productTableViewPanel.setBackground(Color.LIGHT_GRAY);
		productTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
		productTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);
		return productTableViewPanel;
	}

	public void createMonomerPlateFromOtherOrder(Container selectedContainer) {
		if (loadCompoundManagementPanel.getLayout() != loadCompoundManagementOrderFormLayout) {
			loadCompoundManagementPanel.setLayout(loadCompoundManagementOrderFormLayout);
			loadCompoundManagementPanel.add(compoundManagementOrderLabel, mCellConstraints.xy(1, 2));
			loadCompoundManagementPanel.add(compoundManagementOrderTextField, mCellConstraints.xy(2, 2));
			loadCompoundManagementPanel.add(amountUseLabel, mCellConstraints.xy(1, 4));
			loadCompoundManagementPanel.add(amountUsedCombo, mCellConstraints.xy(2, 4));
			loadCompoundManagementPanel.add(sortMonomerLabel, mCellConstraints.xy(1, 6));
			loadCompoundManagementPanel.add(sortMonomerCombo, mCellConstraints.xy(2, 6));
		}
		CeNComboBox containerListCombo = new CeNComboBox();
		try {
			containerListCombo = getUserContainerListCombo();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
					"Unable to load user's container list", e);
		}
		ContainerDialog containerPanel = new ContainerDialog(this, true, containerListCombo);
		containerPanel.setVisible(true);
	}


	public void setSelectedContainerList(Vector siblingList) {
		containerList = siblingList;
	}

	public void createProductPlateFromSynthesisPlan(Container container) {
		this.createProductPlate(container);
	}

	public void createProductPlateFromMonomerPlate(Container container, String plateBarcode) {
		String errorMessage = isValidProductContainer(container, validContainerTypes);
		if (errorMessage != null) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		List monomerPlates = stepModel.getMonomerPlates();
		Iterator it = monomerPlates.iterator();
		while (it.hasNext()) {
			MonomerPlate monomerPlate = (MonomerPlate) it.next();
			if (monomerPlate.getPlateBarCode().equals(plateBarcode)) {
				List newPlates = mParallelCeNProductPlateBuilder.createPlatesFromMonomerPlate(this.mNotebookPageModel, stepModel,
						monomerPlate);
				stepModel.addProductPlates(newPlates);
				Iterator pit = newPlates.iterator();
				while (pit.hasNext()) {
					ProductPlate newPlate = (ProductPlate) pit.next();

					PCeNProductPlatesTableModelConnector plateTableViewConnector = new PCeNProductPlatesTableModelConnector(
							newPlate, mNotebookPageModel); // vb 11/9 added page model
					PCeNTableModel model = new PCeNTableModel(plateTableViewConnector);
					PCeNPlateTableView plateProductView = new PCeNPlateTableView(model, 70, plateTableViewConnector);
					JScrollPane scrollTableViewPane = new JScrollPane(plateProductView);
					PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(plateProductView);
					JPanel plateTabTableViewPanel = new ProductPlateTableViewPanel(newPlate);
					plateTabTableViewPanel.setLayout(new BorderLayout());
					plateTabTableViewPanel.setBackground(Color.LIGHT_GRAY);
					plateTabTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
					plateTabTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);
					// mStaticPlateRenderers.add(mStaticPlateRenderer);
					productPlateTableViewTabs.addTab(newPlate.getLotNo(), plateTabTableViewPanel);
					// Add plate view and batch detail view
					// vb 10/31 select tab just created
					productPlateTableViewTabs.setSelectedIndex(productPlateTableViewTabs.getTabCount() - 1);
					// Add plate view and batch detail view
					ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel(newPlate, mNotebookPageModel);
					ProductBatchDetailContainer batchDetail = new ProductBatchDetailContainer(newPlate, mNotebookPageModel);
					BatchSelectionListener[] listeners = new BatchSelectionListener[2];
					listeners[0] = batchDetail; // vb 6/21
					listeners[1] = reactionsViewPanel;
					plateProductView.setProductBatchDetailsContainerListeners(listeners);
					StaticPlateRenderer plateview = (StaticPlateRenderer) mCeNProductPlateBuilder.buildCeNproductPlateViewer(
							newPlate, listeners);
					this.disposeList.add(plateview);
					JScrollPane scrollProductBatchDetailViewPane = new JScrollPane();
					scrollProductBatchDetailViewPane.getViewport().add(batchDetail);
					// ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel(productPlate, mNotebookPageModel);
					ProductPlatePlateViewPanel productPlatePlateViewPanel = new ProductPlatePlateViewPanel(newPlate, plateview,
							scrollProductBatchDetailViewPane, // vb 6/28 batchDetailPanel,
							batchDetail, reactionsViewPanel);
					this.plateViewPanelMap.put(newPlate.getLotNo(), productPlatePlateViewPanel);
					productPlatePlateViewTabs.addTab(newPlate.getLotNo(), productPlatePlateViewPanel);
				}
				typeComboBox.setSelectedIndex(1);
				viewOptionsComboBox.setSelectedIndex(1);// Plate view of plates
				showViews();
				if (newPlates.size() > 0) {
					ProductBatchPlateCreatedEvent mPlateCreatedEvent = new ProductBatchPlateCreatedEvent(this, newPlates);
					this.fireNewProductPlateCreated(mPlateCreatedEvent);
				}
				this.enableSaveButton();
				break; // Currently there should only be one monomer plate
			}
		}
	}

	/**
	 * Restricts containers to the hardcoded list above (currently "Plate" and "Rack").
	 * 
	 * @param container
	 * @param types
	 * @return
	 */
	private String isValidProductContainer(Container container, String[] types) {
		String type = container.getContainerType();
		// Temporary because previously user created plates were types "PLATE", "RACK", etc.
		// Now they are "USER_CREATED_PLATE", etc.
		// if (type.equals("PLATE") || type.equals("RACK") || type.equals("VIAL") || type.equals("TOTE"))
		if (container == null)
			return "Please select a container.";
		if (type == null || type.length() == 0)
			return "The product plate container must be a rack, plate or tote container.";
		/*
		 * if (type.equals("RACK") || type.equals("VIAL") || type.equals("TOTE")) return
		 * "The product plate container must be a rack, plate or tote container.";
		 */if (container.getSize() == 0)
			return "The size of the container must be greater than 0.";
		for (int i = 0; i < types.length; i++) {
			if (type.toUpperCase().indexOf(types[i].toUpperCase()) > -1)
				return null;
		}
		return "The product plate container must be a rack, plate or tote container.";
	}

	/**
	 * Allows the container to be Compound Management or user created
	 * 
	 * @param container
	 * @return
	 */
	private boolean isValidMonomerContainer(Container container) {
		String type = container.getContainerType().toLowerCase();
		if (type == null || type.length() == 0)
			return false;
		return (type.indexOf("plate") >= 0 || type.indexOf("rack") >= 0);
	}

	public void monomerPlatesRemoved(MonomerBatchPlateCreatedEvent event) {
		// TODO Auto-generated method stub

	}

	public void newMonomerPlatesCreated(MonomerBatchPlateCreatedEvent event) {
		// TODO Auto-generated method stub

	}

	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		// TODO Auto-generated method stub

	}

	public void newRegisteredPlatesCreated(RegisteredPlateCreatedEvent event) {
		// TODO Auto-generated method stub

	}

	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event) {
		List<ProductPlate> list = event.getPlates();
		for (int i = 0; i < list.size(); i++) {
			ProductPlate productPlate = (ProductPlate) list.get(i);
			if (productPlate.getPlateType().equals(CeNConstants.PLATE_TYPE_REGISTRATION))
				return;
			if (typeComboBox.getSelectedItem().equals(types[1])) // Products
			{
				Component obj = (Component) plateViewPanelMap.get(productPlate.getLotNo());
				if (viewOptionsComboBox.getSelectedIndex() == 1 || viewOptionsComboBox.getSelectedIndex() == 2)// Plate table view
				{
					JTabbedPane tempPane = ((JTabbedPane) rightSpliScroll.getViewport().getComponent(0));
					int index = tempPane.indexOfTab(productPlate.getLotNo());
					if (index > -1) {
						productPlateTableViewTabs.remove(index);
						productPlatePlateViewTabs.remove(index);
						productPlateTableViewTabs.repaint();
						productPlatePlateViewTabs.repaint();
					}
				}
				/*
				 * else if (viewOptionsComboBox.getSelectedIndex() == 2) //Plate plate view productPlatePlateViewTabs.remove(obj);//
				 */}

			if (this.plateViewPanelMap.containsKey(productPlate.getLotNo()))
				this.plateViewPanelMap.remove(productPlate.getLotNo());

		}
	}

	public void registeredPlatesRemoved(RegisteredPlateCreatedEvent event) {
		// TODO Auto-generated method stub

	}

	public void fireStoichDataChanged() {
		if (monomerPlatePLateViewTabs.getSelectedComponent() != null) {
			((MonomerPlatePlateViewPanel) monomerPlatePLateViewTabs.getSelectedComponent()).refresh();
		}
		if (mNotebookPageModel != null) {
			mNotebookPageModel.setModelChanged(true);
		}
	}

	enum ReformatType {
		SEQUENCE, TRANSFER_ALL, OMIT_DISCONTINUED, RELATIVE_POSITION
	};

	class RegistrationPlateCreateDialog extends CeNDialog {
		private javax.swing.JButton createBtn;
		private javax.swing.JButton cancelBtn;
		private JLabel containerLabel4ProductPlate;
		private CeNComboBox containerListCombo;
		private JLabel platesLabel;
		private JLabel compoundPositionLabel;
		private JList plateList;
		private String[] validContainerTypes = { "PLATE", "RACK", "VIAL", "TOTE" };
		private ContainerService mContainerService = null;
		private JRadioButton sequenceOrderRadioBtn = new JRadioButton("Sequence Order of Source Plate(s)");
		private JRadioButton transferAllRadioBtn = new JRadioButton("Transfer All Compounds");
		private JRadioButton omitDiscontinuedRadioBtn = new JRadioButton("Omit Discountinued Compounds");
		private JRadioButton relativePositionRadioBtn = new JRadioButton("Same Relative Positions as Source Plate(s)");
		private ButtonGroup mainGroup = new ButtonGroup();
		private ButtonGroup subGroup = new ButtonGroup();
		private ArrayList containerList;
		private Container container;

		/**
		 * Creates new form Test2
		 * 
		 * @param selectedContainer
		 * @param gui
		 * @param monomerPlateFlag
		 */
		public RegistrationPlateCreateDialog(Gui gui, Container selectedContainer) {
			super(gui, true);
			if (selectedContainer != null && selectedContainer.isUserDefined()) {
				JOptionPane.showMessageDialog(gui,
						"Selected Container must be a Compound Management container to create Product Plate from Product Plate.");
				dispose();
				return;
			} else
				this.container = selectedContainer;
			NotebookUser user = MasterController.getUser();
			try {
				mContainerService = ServiceController.getContainerService(user.getSessionIdentifier());
				containerList = (ArrayList) mContainerService.getUserContainers(user.getNTUserID());
			} catch (Exception e) {
				log.error("Failed to load user containers from server.", e);
			}
			initComponents();
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			Dimension labelSize = getPreferredSize();
			setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
			this.setTitle("Create Product Plate from Product Plate");
			setVisible(true);
		}

		private void initComponents() {
			containerLabel4ProductPlate = new JLabel("New Plate Container: ");
			platesLabel = new JLabel("Product Plates:");
			containerListCombo = new CeNComboBox();
			plateList = new JList();
			compoundPositionLabel = new JLabel("Compound Positions on New Plate:");
			Font f = new Font(compoundPositionLabel.getFont().getName(), Font.BOLD, compoundPositionLabel.getFont().getSize());
			compoundPositionLabel.setFont(f);
			List plates = null;

			createBtn = new JButton("Create");
			cancelBtn = new JButton("Cancel");
			createBtn.setEnabled(false);

			plates = mNotebookPageModel.getAllProductPlatesAndRegPlates();
			ArrayList selectPlateIndexList = new ArrayList();
			Iterator it = plates.iterator();
			DefaultListModel tempMod = new DefaultListModel();
			while (it.hasNext()) {
				ProductPlate plate = (ProductPlate) it.next();
				String plateDisplayName = plate.getLotNo();
				tempMod.add(tempMod.getSize(), plateDisplayName);
			}
			plateList.setModel(tempMod);
			plateList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					enableCreateBtn();
				}
			});
			ArrayList<ProductPlate> lastStepProductPlates = new ArrayList<ProductPlate>();
			lastStepProductPlates.addAll(mNotebookPageModel.getReactionStep(mNotebookPageModel.getReactionSteps().size() - 1)
					.getProductPlates());
			for (int i = 0; i < lastStepProductPlates.size(); i++) {
				ProductPlate tempPlate = (ProductPlate) lastStepProductPlates.get(i);
				selectPlateIndexList.add(new Integer(plates.indexOf(tempPlate)));
			}

			int selectedIndeces[] = new int[selectPlateIndexList.size()];
			for (int i = 0; i < selectPlateIndexList.size(); i++) {
				selectedIndeces[i] = Integer.valueOf(selectPlateIndexList.get(i).toString()).intValue();
			}
			plateList.setSelectedIndices(selectedIndeces);

			mainGroup.add(sequenceOrderRadioBtn);
			subGroup.add(transferAllRadioBtn);
			subGroup.add(omitDiscontinuedRadioBtn);
			mainGroup.add(relativePositionRadioBtn);

			sequenceOrderRadioBtn.setActionCommand(ReformatType.SEQUENCE.name());
			omitDiscontinuedRadioBtn.setActionCommand(ReformatType.OMIT_DISCONTINUED.name());
			transferAllRadioBtn.setActionCommand(ReformatType.TRANSFER_ALL.name());
			relativePositionRadioBtn.setActionCommand(ReformatType.RELATIVE_POSITION.name());

			transferAllRadioBtn.setSelected(true);
			transferAllRadioBtn.setEnabled(false);
			omitDiscontinuedRadioBtn.setEnabled(false);

			sequenceOrderRadioBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					enableCreateBtn();
					transferAllRadioBtn.setEnabled(true);
					omitDiscontinuedRadioBtn.setEnabled(true);
				}
			});

			omitDiscontinuedRadioBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					enableCreateBtn();
				}
			});

			transferAllRadioBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					enableCreateBtn();
				}
			});

			relativePositionRadioBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					enableCreateBtn();
					transferAllRadioBtn.setEnabled(false);
					omitDiscontinuedRadioBtn.setEnabled(false);
				}
			});

			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // vb todo dispose
			validate();

			FormLayout createProductPlateDialoglayout = new FormLayout("15dlu, 20dlu, 60dlu, 5dlu, 120dlu, 15dlu",// columns
					"5dlu,pref, 10dlu, pref, 10dlu, pref, 5dlu, pref, 1dlu, pref, 1dlu, pref, 5dlu, pref,5dlu, pref,10dlu, pref"); // rows
			CellConstraints cellConstraints = new CellConstraints();
			getContentPane().setLayout(createProductPlateDialoglayout);
			// set up the container combo
			try {
				containerListCombo = getUserContainerListCombo();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
						"Unable to retrieve user's containers", e);
			}
			if (this.container != null)
				containerListCombo.getModel().setSelectedItem(container);

			containerListCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					enableCreateBtn();
				}
			});
			int row = 2;
			this.getContentPane().add(containerLabel4ProductPlate, cellConstraints.xyw(2, row, 2));
			this.getContentPane().add(containerListCombo, cellConstraints.xy(5, row));

			row += 2;
			this.getContentPane().add(platesLabel, cellConstraints.xyw(2, row, 2));
			JScrollPane jScrollPanePlatesList = new JScrollPane();
			jScrollPanePlatesList.setPreferredSize(new java.awt.Dimension(363, 59));
			jScrollPanePlatesList.setBounds(new java.awt.Rectangle(12, 28, 363, 59));
			jScrollPanePlatesList.add(plateList);
			jScrollPanePlatesList.setViewportView(plateList);

			this.getContentPane().add(jScrollPanePlatesList, cellConstraints.xy(5, row));

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(createBtn);
			buttonPanel.add(cancelBtn);

			row += 2;
			this.getContentPane().add(compoundPositionLabel, cellConstraints.xyw(2, row, 4));

			row += 2;
			this.getContentPane().add(sequenceOrderRadioBtn, cellConstraints.xyw(2, row, 4));

			row += 2;
			this.getContentPane().add(omitDiscontinuedRadioBtn, cellConstraints.xyw(3, row, 3));

			row += 2;
			this.getContentPane().add(transferAllRadioBtn, cellConstraints.xyw(3, row, 3));

			row += 2;
			this.getContentPane().add(relativePositionRadioBtn, cellConstraints.xyw(2, row, 4));

			row += 2;
			this.getContentPane().add(buttonPanel, cellConstraints.xyw(1, row, 5));

			pack();
			createBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					Container container = (Container) containerListCombo.getSelectedItem();
					String errorMessage = isValidProductContainer((Container) containerListCombo.getSelectedItem(),
							validContainerTypes);
					if (errorMessage != null) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					String creationType = "";
					if (mainGroup.getSelection().getActionCommand().equals(ReformatType.SEQUENCE.name()))
						creationType = subGroup.getSelection().getActionCommand();
					else
						creationType = mainGroup.getSelection().getActionCommand();

					if (createRegisterationPlateFromProductPlate(container, plateList.getSelectedValues(), creationType))
						dispose();
				}
			});

			cancelBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelBtnActionPerformed(evt);
				}
			});
		}

		protected boolean createRegisterationPlateFromProductPlate(Container container, Object[] plateLotNo,
				String reformatTypeString) {
			ArrayList sourcePlateWellsList = (ArrayList) getPlateWells(plateLotNo);
			ReformatType reformatType = ReformatType.valueOf(reformatTypeString);
			removeTraillingWells(sourcePlateWellsList);// remove last plate trailing empty wells.
			if (!reformatType.name().equals(ReformatType.RELATIVE_POSITION.name()))
				sourcePlateWellsList = getQualifiedWellsToMove(sourcePlateWellsList, reformatType.name().equals(
						ReformatType.OMIT_DISCONTINUED.name()));
			ParallelCeNProductPlateBuilder mParallelCeNProductPlateBuilder = new ParallelCeNProductPlateBuilder();
			List newPlates = null;
			switch (reformatType) {
				case OMIT_DISCONTINUED:
				case TRANSFER_ALL:
					newPlates = mParallelCeNProductPlateBuilder.getCompressedPlatesForWells(sourcePlateWellsList, container,
							mNotebookPageModel);
					break;
				case RELATIVE_POSITION: {
					String errorMsg = mParallelCeNProductPlateBuilder.validateRelativePlatesSkippWells(sourcePlateWellsList,
							container, mNotebookPageModel);
					if (!errorMsg.equals("")) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMsg);
						return false;
					}
					newPlates = mParallelCeNProductPlateBuilder.getRelativePlatesForWells(sourcePlateWellsList, container,
							mNotebookPageModel);
					break;
				}
			}
			// sourcePlateWellsList = null;
			// ProductPlate newTempPlate;
			mNotebookPageModel.addRegisteredPlate(newPlates);
			// stepModel.addProductPlates(newPlates);
			/*
			 * Iterator pit = newPlates.iterator(); while (pit.hasNext()) { ProductPlate newPlate = (ProductPlate) pit.next();
			 * 
			 * PCeNProductPlatesTableModelConnector plateTableViewConnector = new PCeNProductPlatesTableModelConnector( newPlate,
			 * mNotebookPageModel); // vb 11/9 added page model PCeNTableModel model = new PCeNTableModel(plateTableViewConnector);
			 * PCeNPlateTableView plateProductView = new PCeNPlateTableView(model, 70, plateTableViewConnector); JScrollPane
			 * scrollTableViewPane = new JScrollPane(plateProductView); PCeNTableViewToolBar productTableViewToolBar = new
			 * PCeNTableViewToolBar(plateProductView); JPanel plateTabTableViewPanel = new ProductPlateTableViewPanel(newPlate);
			 * plateTabTableViewPanel.setLayout(new BorderLayout()); plateTabTableViewPanel.setBackground(Color.LIGHT_GRAY);
			 * plateTabTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
			 * plateTabTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER); //
			 * mStaticPlateRenderers.add(mStaticPlateRenderer); productPlateTableViewTabs.addTab(newPlate.getLotNo(),
			 * plateTabTableViewPanel); // Add plate view and batch detail view // vb 10/31 select tab just created
			 * productPlateTableViewTabs.setSelectedIndex(productPlateTableViewTabs.getTabCount() - 1); // Add plate view and batch
			 * detail view ReactionsViewPanel reactionsViewPanel = new ReactionsViewPanel(newPlate, mNotebookPageModel);
			 * ProductBatchDetailContainer batchDetail = new ProductBatchDetailContainer(newPlate, mNotebookPageModel);
			 * BatchSelectionListener[] listeners = new BatchSelectionListener[2]; listeners[0] = batchDetail; // vb 6/21
			 * listeners[1] = reactionsViewPanel; plateProductView.setProductBatchDetailsContainerListeners(listeners);
			 * StaticPlateRenderer plateview = (StaticPlateRenderer) mCeNProductPlateBuilder.buildCeNproductPlateViewer(newPlate,
			 * listeners); disposeList.add(plateview); JScrollPane scrollProductBatchDetailViewPane = new JScrollPane();
			 * scrollProductBatchDetailViewPane.getViewport().add(batchDetail); //ReactionsViewPanel reactionsViewPanel = new
			 * ReactionsViewPanel(productPlate, mNotebookPageModel); ProductPlatePlateViewPanel productPlatePlateViewPanel = new
			 * ProductPlatePlateViewPanel(newPlate, plateview, scrollProductBatchDetailViewPane, // vb 6/28 batchDetailPanel,
			 * batchDetail, reactionsViewPanel); plateViewPanelMap.put(newPlate.getLotNo(), productPlatePlateViewPanel);
			 * productPlatePlateViewTabs.addTab(newPlate.getLotNo(), productPlatePlateViewPanel); }
			 * typeComboBox.setSelectedIndex(1); viewOptionsComboBox.setSelectedIndex(1);// Plate view of plates showViews();
			 */
			if (newPlates.size() > 0) {
				ProductBatchPlateCreatedEvent mPlateCreatedEvent = new ProductBatchPlateCreatedEvent(this, newPlates);
				fireNewProductPlateCreated(mPlateCreatedEvent);
			}
			enableSaveButton();
			return true;
		}

		private ArrayList getQualifiedWellsToMove(ArrayList initialSourcePlateWellsList, boolean omitDiscontinuedFlag) {
			ArrayList finalSourcePlateWellsList = new ArrayList();
			if (initialSourcePlateWellsList.size() > 0) {
				// Apply business conditions.
				for (int j = 0; j < initialSourcePlateWellsList.size(); j++) {
					PlateWell tempWell = (PlateWell) initialSourcePlateWellsList.get(j);
					if (tempWell.getBatch() == null)
						continue;
					if ((((ProductBatchModel) tempWell.getBatch()).getContinueStatus() == CeNConstants.BATCH_STATUS_DISCONTINUE)) {
						if (!omitDiscontinuedFlag) {
							PlateWell copyTempWell = (PlateWell) tempWell.deepClone();
							copyTempWell.setPlate(tempWell.getPlate());
							finalSourcePlateWellsList.add(copyTempWell);
						}
					} else
						finalSourcePlateWellsList.add(tempWell);
				}
			}
			return finalSourcePlateWellsList;
		}

		private void removeTraillingWells(ArrayList initialSourcePlateWellsList) {
			if (((PlateWell) (initialSourcePlateWellsList.get(initialSourcePlateWellsList.size() - 1))).getBatch() == null) {
				for (int i = initialSourcePlateWellsList.size() - 1; i >= 0; i--) {
					if (((PlateWell) initialSourcePlateWellsList.get(i)).getBatch() == null) {
						initialSourcePlateWellsList.remove(i);
					} else
						break;
				}
			}
		}

		private ArrayList getPlateWells(Object[] plateLotNo) {
			List productPlates = mNotebookPageModel.getAllProductPlatesAndRegPlates();
			Iterator it = productPlates.iterator();
			ArrayList initialSourcePlateWellsList = new ArrayList();
			while (it.hasNext()) {
				ProductPlate productPlate = (ProductPlate) it.next();
				for (int i = 0; i < plateLotNo.length; i++) {
					String plateRefNo = productPlate.getLotNo();
					if (plateRefNo.equals(plateLotNo[i].toString())) {
						PlateWell[] plateWells = productPlate.getWells();
						initialSourcePlateWellsList.addAll(Arrays.asList(plateWells));
					}
				}
			}
			return initialSourcePlateWellsList;
		}

		protected void enableCreateBtn() {
			if (plateList.getSelectedIndex() > -1 && containerListCombo.getSelectedItem() != null
					&& mainGroup.getSelection() != null)
				createBtn.setEnabled(mNotebookPageModel.isEditable());
			else
				createBtn.setEnabled(false);
		}

		private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {
			this.dispose();
		}

		private CeNComboBox getUserContainerListCombo() {
			// Fill the containers map using key as key
			Map containersMap = new HashMap();
			for (Iterator it = containerList.iterator(); it.hasNext();) {
				Container cont = (Container) it.next();
				if (!cont.isUserDefined())
					containersMap.put(cont.getKey(), cont);
			}
			Vector containersVec = new Vector(containersMap.values());
			Collections.sort(containersVec, new Comparator() {
				public int compare(Object o1, Object o2) {
					if (o1 == null || o2 == null)
						return 0;
					if (o1 instanceof Container && o2 instanceof Container) {
						return ((Container) o1).getContainerName().compareTo(((Container) o2).getContainerName());
					}
					return 0;
				}
			});
			DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(containersVec);
			CeNComboBox containerListCombo = new CeNComboBox(comboBoxModel);
			// containerListCombo.enablePopUpCombo(250);//CeNComboBox takes care of it.

			containerListCombo.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					CeNComboBox tempComboBox = (CeNComboBox) e.getSource();
					if (tempComboBox.getSelectedItem() != null && !tempComboBox.getSelectedItem().equals("")) {
						Container tempContainer = (Container) tempComboBox.getSelectedItem();
						tempComboBox.setToolTipText(tempContainer.getContainerName() + " (" + tempContainer.getYPositions() + ", "
								+ tempContainer.getXPositions() + ")");
					}
				}
			});
			containerListCombo.setSelectedIndex(-1);
			return containerListCombo;
		}

		/**
		 * Restricts containers to the hardcoded list above (currently "Plate" and "Rack").
		 * 
		 * @param container
		 * @param types
		 * @return
		 */
		private String isValidProductContainer(Container container, String[] types) {
			String type = container.getContainerType();
			// Temporary because previously user created plates were types "PLATE", "RACK", etc.
			// Now they are "USER_CREATED_PLATE", etc.
			// if (type.equals("PLATE") || type.equals("RACK") || type.equals("VIAL") || type.equals("TOTE"))
			if (container == null)
				return "Please select a container.";
			if (type == null || type.length() == 0)
				return "The product plate container must be a rack, plate or tote container.";
			/*
			 * if (type.equals("RACK") || type.equals("VIAL") || type.equals("TOTE")) return
			 * "The product plate container must be a rack, plate or tote container.";
			 */if (container.getSize() == 0)
				return "The size of the container must be greater than 0.";
			for (int i = 0; i < types.length; i++) {
				if (type.toUpperCase().indexOf(types[i].toUpperCase()) > -1)
					return null;
			}
			return "The product plate container must be a rack, plate or tote container.";
		}
	}

	class PlateCreateFromAbstractPlateDialog extends CeNDialog {
		private PanelBuilder builder = null;
		private javax.swing.JButton fillBtn;
		private javax.swing.JButton cancelBtn;
		private JLabel containerLabel4ProductPlate;
		private CeNComboBox containerListCombo;
		private JLabel platesLabel;
		private JList plateList;
		private String plateString = null;
		private boolean monomerPlateFlag = false;
		private Container cotainer;

		/**
		 * Creates new form Test2
		 * 
		 * @param monomerPlateFlag
		 */
		public PlateCreateFromAbstractPlateDialog(JFrame parent, boolean monomerPlateFlag, boolean modal,
				Container selectedContainerType) {
			super(parent, modal);
			this.monomerPlateFlag = monomerPlateFlag;
			this.cotainer = selectedContainerType;
			if (monomerPlateFlag)
				plateString = "Monomer";
			else
				plateString = "Product";
			initComponents();
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			Dimension labelSize = getPreferredSize();
			setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

			// this.show();

			this.setTitle("Fill Product Plates from " + plateString + " Plate");
		}

		private void initComponents() {
			containerLabel4ProductPlate = new JLabel("Container From My List: ");
			platesLabel = new JLabel(plateString + " Plates");
			containerListCombo = new CeNComboBox();
			plateList = new JList();
			List plates = null;
			if (monomerPlateFlag) {
				plates = mNotebookPageModel.getAllMonomerPlates();
				plateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			} else
				plates = mNotebookPageModel.getAllProductPlatesAndRegPlates();

			Iterator it = plates.iterator();
			DefaultListModel tempMod = new DefaultListModel();
			while (it.hasNext()) {
				AbstractPlate plate = (AbstractPlate) it.next();
				tempMod.add(tempMod.getSize(), (plate.getPlateBarCode().equals("") ? ((ProductPlate) plate).getLotNo() : plate
						.getPlateBarCode()));
			}
			plateList.setModel(tempMod);
			plateList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					enableFillBtn();
				}
			});
			fillBtn = new JButton("  Fill  ");
			cancelBtn = new JButton("Cancel");
			fillBtn.setEnabled(false);

			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // vb todo dispose
			validate();

			FormLayout createProductPlateDialoglayout = new FormLayout("5dlu, pref, 10dlu, 100dlu, 5dlu, ",// columns
					"5dlu,pref, 10dlu, pref, 10dlu, pref, 5dlu"); // rows
			CellConstraints cellConstraints = new CellConstraints();
			this.getContentPane().setLayout(createProductPlateDialoglayout);
			// set up the container combo
			try {
				containerListCombo = getUserContainerListCombo();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
						"Unable to retrieve user's container(s)", e);
			}
			if (cotainer != null)
				containerListCombo.setSelectedItem(cotainer.getContainerName());
			containerListCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					enableFillBtn();
				}
			});
			int row = 2;
			this.getContentPane().add(containerLabel4ProductPlate, cellConstraints.xy(2, row));
			this.getContentPane().add(containerListCombo, cellConstraints.xy(4, row));

			row += 2;
			this.getContentPane().add(platesLabel, cellConstraints.xy(2, row));
			JScrollPane jScrollPanePlatesList = new JScrollPane();
			jScrollPanePlatesList.setPreferredSize(new java.awt.Dimension(363, 59));
			jScrollPanePlatesList.setBounds(new java.awt.Rectangle(12, 28, 363, 59));
			jScrollPanePlatesList.add(plateList);
			jScrollPanePlatesList.setViewportView(plateList);

			this.getContentPane().add(jScrollPanePlatesList, cellConstraints.xy(4, row));

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(fillBtn);
			buttonPanel.add(cancelBtn);

			row += 2;
			this.getContentPane().add(buttonPanel, cellConstraints.xyw(2, row, 3));
			pack();
			fillBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					Container container = (Container) containerListCombo.getSelectedItem();
					String errorMessage = isValidProductContainer((Container) containerListCombo.getSelectedItem(),
							validContainerTypes);
					if (errorMessage != null) {
						JOptionPane.showMessageDialog(MasterController.getGUIComponent(), errorMessage, "Container Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (monomerPlateFlag)
						createProductPlateFromMonomerPlate(container, plateList.getSelectedValue().toString());
					// else
					// createRegistrationPlateFromProductPlate(container, plateList.getSelectedValues());
					dispose();
				}
			});

			cancelBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelBtnActionPerformed(evt);
				}
			});
		}

		protected void enableFillBtn() {
			if (plateList.getSelectedIndex() > -1 && !containerListCombo.getSelectedItem().equals(""))
				fillBtn.setEnabled(mNotebookPageModel.isEditable());
			else
				fillBtn.setEnabled(false);
		}

		private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {
			this.dispose();
		}
	}

	private CeNComboBox getUserContainerListComboForASDI() throws Exception {
		// Fill the containers map using key as key
		Map containersMap = new HashMap();
		ArrayList containerList = (ArrayList) mContainerService.getUserContainers(MasterController.getUser().getNTUserID());
		for (Iterator it = containerList.iterator(); it.hasNext();) {
			Container cont = (Container) it.next();
			containersMap.put(cont.getKey(), cont);
		}
		Vector containersVec = new Vector(containersMap.values());
		Collections.sort(containersVec, new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 == null || o2 == null)
					return 0;
				if (o1 instanceof Container && o2 instanceof Container) {
					return ((Container) o1).getContainerName().compareTo(((Container) o2).getContainerName());
				}
				return 0;
			}
		});
		
		Container c = new Container(ASDI_ORDER_LOAD_NO_CONTAINER);
		c.setContainerCode(ASDI_ORDER_LOAD_NO_CONTAINER);
		c.setContainerName(ASDI_ORDER_LOAD_NO_CONTAINER);
		containersMap.put(c.getKey(), c);
		containersVec.add(0, c);
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(containersVec);
		CeNComboBox containerListCombo = new CeNComboBox(comboBoxModel);
		// containerListCombo.enablePopUpCombo(250);//CeNComboBox takes care of it.

		containerListCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CeNComboBox tempComboBox = (CeNComboBox) e.getSource();
				if (tempComboBox.getSelectedItem() != null && !tempComboBox.getSelectedItem().equals("")
						&& !tempComboBox.getSelectedItem().equals(ASDI_ORDER_LOAD_NO_CONTAINER)) {
					Container tempContainer = (Container) tempComboBox.getSelectedItem();
					tempComboBox.setToolTipText(tempContainer.getContainerName() + " (" + tempContainer.getYPositions() + ", "
							+ tempContainer.getXPositions() + ")");
				}
			}
		});
		
		// Set the selected item in the containers list. If there is no selected item,
		// insert an empty string at index zero to indicate no container is selected.
		// (Hack alert: the code that calls this method depends on knowing this is an empty string.)
		if (selectedContainer != null && containersMap.containsKey(selectedContainer.getKey())) {
			containerListCombo.getModel().setSelectedItem(selectedContainer);
			containerListCombo.setToolTipText(selectedContainer.getContainerName() + " (" + selectedContainer.getYPositions()
					+ ", " + selectedContainer.getXPositions() + ")");
		}
		// else {
		// 		containerListCombo.insertItemAt("", 0);
		// 		containerListCombo.setSelectedIndex(0);
		// }

		return containerListCombo;
	}
	
	
}

class SpinnerPanel {
	int rows = 2;
	int cols = 3;
	int min = 1;
	int max = 20;
	int step = 1;
	SpinnerNumberModel rowModel = new SpinnerNumberModel(rows, min, max, step);
	SpinnerNumberModel colModel = new SpinnerNumberModel(cols, min, max, step);
	JSpinner numRows = new JSpinner(rowModel);
	JSpinner numCols = new JSpinner(colModel);
	FlowLayout flow = new FlowLayout();
	JPanel panel = new JPanel();

	public SpinnerPanel() {
		flow.setAlignment(FlowLayout.LEFT);
		panel.setLayout(flow);
		// panel.setAlignmentX(FlowLayout.LEFT);
		panel.add(numRows);
		panel.add(numCols);
		// panel.add(combo);
	}

	public JPanel getPanel() {
		return panel;
	}

	public int getRowNumber() {
		return new Integer(numRows.getValue().toString()).intValue();
	}

	public int getColNumber() {
		return new Integer(numCols.getValue().toString()).intValue();
	}
	
	
}

/*
 * class CollapsiblePaneHelper implements CollapsiblePaneListener {
 * 
 * private JPanel contentPane; private CollapsiblePane collapsiblePanel;
 * 
 * CollapsiblePaneHelper(JPanel contentPane, CollapsiblePane collapsiblePanel) { this.contentPane = contentPane;
 * this.collapsiblePanel = collapsiblePanel; }
 * 
 * public void paneCollapsed(CollapsiblePaneEvent arg0) { contentPane.remove(collapsiblePanel); contentPane.add(collapsiblePanel,
 * BorderLayout.CENTER); contentPane.validate(); //contentPane.pack(); }
 * 
 * public void paneCollapsing(Col * Revision 1.250  2009/03/26 20:26:24  USER2
 * Moved  isMatch () to CommonUtils class.
 *
lapsiblePaneEvent arg0) { // TODO Auto-generated method stub
 * 
 * }
 * 
 * public void paneExpanded(CollapsiblePaneEvent arg0) { contentPane.remove(collapsiblePanel); contentPane.add(collapsiblePanel,
 * BorderLayout.CENTER); contentPane.validate(); //contentPane.pack(); }
 * 
 * public void paneExpanding(CollapsiblePaneEvent arg0) { }
 * 
 * 
 * }
 */
