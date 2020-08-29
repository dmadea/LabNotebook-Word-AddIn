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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.chlorac.SDFileChloracnegenTester;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.utils.PanelFormatter;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalUtility;
import com.chemistry.enotebook.client.gui.page.batch.events.*;
import com.chemistry.enotebook.client.gui.page.batch.table.PCeNProductsTableModelConnector;
import com.chemistry.enotebook.client.gui.page.batch.table.PCeNSingleProductPlateTableModelConnector;
import com.chemistry.enotebook.client.gui.page.batch.table.ProductTablePopupMenuManager;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundCreateInterface;
import com.chemistry.enotebook.client.gui.page.table.*;
import com.chemistry.enotebook.client.utils.ReactionStepModelUtils;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.*;
import com.virtuan.plateVisualizer.InteractivePlateRenderer;
import com.virtuan.plateVisualizer.StaticPlateRenderer;
import com.virtuan.plateVisualizer.WellModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class PCeNBatch_BatchDetailsViewContainer implements PlatesCreatedEventListener, ItemListener, CompoundCreateInterface,
		CompoundCreatedEventListener, BatchSelectionListener, ActionListener {

	private static final Log log = LogFactory.getLog(PCeNBatch_BatchDetailsViewContainer.class);

	public static final String TABLES_VIEW = "Tables";
	public static final String PLATES_VIEW = "Plates";
	
	private CollapsiblePane viewBatch_Pane;

	private ProductBatchRackViewPanel mProductBatchRackViewPanel;
	private BatchSelectionListener batchSelectionListener = null;
	private JTabbedPane productTableTabbedPane;
	// private JTabbedPane plateTableViewTab;
	private JTabbedPane plateRackViewTab;
	private NotebookPageModel pageModel;
	private List<PCeNTableView> tableList = new ArrayList<PCeNTableView>(); // keep a list of the tables so we can unset selected row when a we switch to a
												// different table

	private String selectedView = TABLES_VIEW;

	private JButton vnvButton = new JButton("VnV");
	private JButton addNewCompoundBtn = new JButton("Add New Batch");
	private JButton syncIntendedProductsBtn = new JButton("Sync with Intended Products");
	private JButton deleteCompoundBtn = new JButton("Delete Batch(es)");
	private JButton importSDFileBtn = new JButton("Import SD File");

	private Map<String, JPanel> productViews = new LinkedHashMap<String, JPanel>();  // maintain product tab ordering
	private Map<String, JPanel> plateRackViews = new LinkedHashMap<String, JPanel>();  // maintain tab plate order

	private CompoundCreationHandler compoundCreationHandler = null;
	private PCeNBatchInfoTableView currentTableView;

	private PCeNBatchInfoTableView lastStepProductTableView; // need this for adding compounds
	private PCeNProductsTableModelConnector lastStepProductConnector;
	private ProductTablePopupMenuManager parallelProductTablePopupMenuManagerForProductTable = new ProductTablePopupMenuManager(this);
	private ReactionStepModel lastStep = null;

	private ArrayList<ProductPlate> selectedPlates = new ArrayList<ProductPlate>();

	private boolean isLoading = true;

	private JPopupMenu productTableViewTabPopupMenu = new JPopupMenu();
	private JMenuItem deleteProductPlate1 = new JMenuItem("Remove Product Plate");
	private JPopupMenu productPlateViewTabPopupMenu = new JPopupMenu();
	private JMenuItem deleteProductPlate2 = new JMenuItem("Remove Product Plate");

	private PlateCreationHandler plateCreationHandler;

	private PCeNBatch_SummaryViewContainer summaryBatchRackCont;
	private JRadioButton platesRadioButton;
	private JRadioButton tablesRadioButton;
	
	// private ParallelNotebookPageGUI mParallelNotebookPageGUI;
	public PCeNBatch_BatchDetailsViewContainer(NotebookPageModel pageModel, 
	                                           final BatchSelectionListener batchSelectionListener,
	                                           CompoundCreationHandler compoundCreationHandler, 
	                                           PlateCreationHandler plateCreationHandler,
	                                           PCeNBatch_SummaryViewContainer summaryBatchRackCont,
	                                           String paneTitle) 
	{
		// public PlateViewerGUIRenderer(NotebookPageModel pageModel ) {
		this.pageModel = pageModel;
		this.batchSelectionListener = batchSelectionListener;
		this.compoundCreationHandler = compoundCreationHandler;
		this.plateCreationHandler = plateCreationHandler;
		this.summaryBatchRackCont = summaryBatchRackCont;
		// mParallelNotebookPageGUI = pNBGUI;
		mProductBatchRackViewPanel = new ProductBatchRackViewPanel(pageModel); // vb 7/12 added pageModel
		createCollapsiblePane(paneTitle);
		// plateTableViewTab = new JTabbedPane(SwingConstants.TOP);
		plateRackViewTab = new JTabbedPane(SwingConstants.TOP);
		productTableTabbedPane = new JTabbedPane(SwingConstants.TOP);
		
		vnvButton.setEnabled(MasterController.isVnvEnabled());
		vnvButton.setToolTipText("Auto assign stereoisomer codes without Uniquess check");
		vnvButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doVnv();
			}
		});
		
		addNewCompoundBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				createCompound();
			}
		});
		deleteCompoundBtn.setEnabled(true);
		deleteCompoundBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteCompound();
			}
		});

		syncIntendedProductsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				syncIntendedProducts();
			}
		});

		importSDFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(MasterController.getGUIComponent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					importSDFile(file);
				}
			}
		});

		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BorderLayout());

		JPanel batchBtnPanel = new JPanel();
		if (this.pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
			batchBtnPanel.add(syncIntendedProductsBtn);
		}
		if (!pageModel.isParallelExperiment()) {
			batchBtnPanel.add(vnvButton);
		}
		batchBtnPanel.add(addNewCompoundBtn);
		batchBtnPanel.add(deleteCompoundBtn);
		if (this.pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
			importSDFileBtn.setToolTipText(importSDFileBtn.getText());
			batchBtnPanel.add(importSDFileBtn);
		}
		JLabel reqForRegLabel = new JLabel();
		reqForRegLabel.setText("<html><font color=\"red\">* </font><font size='2'>Required for registration</font></html>");

		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
		JPanel plateBtnPanel = new JPanel(flowLayout);
		plateBtnPanel.add(new JLabel("<html><SPAN style='font-size:10.0pt;font-family:Sansserif'><font color=\"red\">* </font>" +
									 "Required for registration</SPAN></html>"));
		// Temp.adjustment. Once layout is finalized, re-layout the components.
		// plateBtnPanel.add(reformatPlateBtn);

		toolBarPanel.add(plateBtnPanel, BorderLayout.LINE_START);
		toolBarPanel.add(batchBtnPanel, BorderLayout.CENTER);
		if (this.pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
			toolBarPanel.add(createViewOptionsPanel(), BorderLayout.LINE_END);
		}

		viewBatch_Pane.getContentPane().add(toolBarPanel, BorderLayout.PAGE_START);

		// reformatPlateBtn.setToolTipText("Create a registration plate from a product plate");

		int noOfSteps = pageModel.getReactionSteps().size();
		lastStep = pageModel.getReactionStep(noOfSteps - 1);

		// Create products and plates tabs
		addPlatesToTabs(pageModel.getAllProductPlatesAndRegPlates());
		if(this.pageModel.isParallelExperiment())
		{
			//createNonPlatedBatchesViewTab();
		}
		addProductsToTabs(pageModel);
		updateProducts();
		
		switchView(selectedView);
		
		if (!pageModel.isEditable())
			setButtonsEditable(false);

		plateRackViewTab.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				processPlateRackViewPopupEvent(evt);
			} // end mouseReleased

			private void processPlateRackViewPopupEvent(MouseEvent evt) {
				if (evt.isPopupTrigger()) {// show popup
					productPlateViewTabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}

			public void mouseClicked(MouseEvent evt) {
				processPlateRackViewPopupEvent(evt);
			}

		});

		productTableViewTabPopupMenu.add(deleteProductPlate1);
		productPlateViewTabPopupMenu.add(deleteProductPlate2);
		productPlateViewTabPopupMenu.add(new ShowColorLegendDialogMenuItem());

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

//		try {
//			userAddedBatchType = BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL);
//		} catch (InvalidBatchTypeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		productTableTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JPanel productPanel = (JPanel) productTableTabbedPane.getSelectedComponent();
				if (productPanel == null) {
					productPanel = (JPanel) productTableTabbedPane.getComponent(0);
				}
				JScrollPane scrollPane = ((JScrollPane) productPanel.getComponent(1));
				PCeNBatchInfoTableView tempTtableView = (PCeNBatchInfoTableView) scrollPane.getViewport().getView();
				ProductBatchModel batchModel = (ProductBatchModel) tempTtableView.getSelectedBatch();
				batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, batchModel));
			}
		});

		plateRackViewTab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				int selectedIndex = ((JTabbedPane) evt.getSource()).getSelectedIndex();
				InteractivePlateRenderer ipRenderer = null;
				if (((JTabbedPane) evt.getSource()).getSelectedComponent() != null)
					ipRenderer = (InteractivePlateRenderer) ((JPanel) ((JTabbedPane) evt.getSource()).getSelectedComponent())
							.getComponent(0);
				PlateWell<?> well = null;
				WellModel wellModel = null;
				if (ipRenderer != null)
					wellModel = ipRenderer.getRightMouseSeletedWell();
				if (wellModel != null)
					well = wellModel.getUserWellObject();
				ProductBatchModel batchModel = null;
				if (well == null) {
					if (selectedPlates != null && selectedPlates.size() > 0 && selectedIndex > -1)
						batchModel = getBatchInFirstWell((ProductPlate) selectedPlates.get(selectedIndex));
				} else
					batchModel = (ProductBatchModel) well.getBatch();
				if (batchModel != null)
					batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, batchModel));
			}
		});
		isLoading = false;
		// initialize pseudo product plate
		new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
		updateSyncWithIntendedProductsActionState();
		updateVnvButtonState();
	}

	private void doVnv() {
		final String progressStatus = "Performing VnV for batches...";
		try {
			CeNJobProgressHandler.getInstance().addItem(progressStatus);
			Thread t = new Thread(new Runnable() {
				public void run() {
					if (batchSelectionListener != null && batchSelectionListener instanceof BatchEditPanel) {
						List<ProductBatchModel> batches = getSelectedBatches();
						((BatchEditPanel) batchSelectionListener).handleVnVMultiple(batches);
						lastStepProductTableView.validate();
						lastStepProductTableView.repaint();
					}

				}
			});
			t.start();
		} finally {
			CeNJobProgressHandler.getInstance().removeItem(progressStatus);
		}
	}

	private List<ProductBatchModel> getSelectedBatches() {
		List<ProductBatchModel> batches = lastStepProductTableView.getConnector().getAbstractBatches();
		List<ProductBatchModel> selectedBatches = new ArrayList<ProductBatchModel>();
		
		for (ProductBatchModel batch : batches) {
			if (batch.isSelected() && batch.isEditable()) {
				selectedBatches.add(batch);
			}
		}
		
		return selectedBatches;
	}

	private JPanel createViewOptionsPanel() {
		JPanel viewOptionsPanel = new JPanel(new GridLayout(1, 2, 6, 6));
		tablesRadioButton = new JRadioButton(TABLES_VIEW);
		platesRadioButton = new JRadioButton(PLATES_VIEW);
		ButtonGroup group = new ButtonGroup();
		group.add(tablesRadioButton);
		group.add(platesRadioButton);
		viewOptionsPanel.add(tablesRadioButton);
		viewOptionsPanel.add(platesRadioButton);
		viewOptionsPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		tablesRadioButton.setSelected(true);
		tablesRadioButton.addActionListener(this);
		platesRadioButton.addActionListener(this);
		return viewOptionsPanel;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(TABLES_VIEW) || command.equals(PLATES_VIEW)) {
			selectedView = command;
			switchView(selectedView);
		}
	}

	private void setButtonsEditable(boolean editable) {
		if (!pageModel.isEditable()) {
			editable = false;
		}
		syncIntendedProductsBtn.setEnabled(editable);
		vnvButton.setEnabled(editable && MasterController.isVnvEnabled());
		addNewCompoundBtn.setEnabled(editable);
		deleteCompoundBtn.setEnabled(editable);
		importSDFileBtn.setEnabled(editable);
		
		updateSyncWithIntendedProductsActionState();
		updateVnvButtonState();
	}

	protected void deleteProductPlate() {
		JTabbedPane tempPane = ((JTabbedPane) viewBatch_Pane.getContentPane().getComponent(1));
		Component obj = tempPane.getSelectedComponent();
		// Code required to avoid Delete Product plate Popup.
		PCeNSingleProductPlateTableModelConnector connector = null;
		ProductPlate productPlate = null;

		if (selectedView.equals(TABLES_VIEW)) {
			JPanel tempPanel = (JPanel) obj;
			JViewport viewport = ((JViewport) ((JScrollPane) tempPanel.getComponent(1)).getComponent(0));
			PCeNBatchInfoTableView tableView = (PCeNBatchInfoTableView) viewport.getComponent(0);
			connector = (PCeNSingleProductPlateTableModelConnector) tableView.getConnector();
			productPlate = connector.getProductPlate();
		} else if (selectedView.equals(PLATES_VIEW)) {
			int index = tempPane.getSelectedIndex();
			String titleKey = tempPane.getTitleAt(index);
			JPanel tempPanel = (JPanel) productViews.get(titleKey);
			JViewport viewport = ((JViewport) ((JScrollPane) tempPanel.getComponent(1)).getComponent(0));
			PCeNBatchInfoTableView tableView = (PCeNBatchInfoTableView) viewport.getComponent(0);
			connector = (PCeNSingleProductPlateTableModelConnector) tableView.getConnector();
			productPlate = connector.getProductPlate();
		}

		if (connector != null) {
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

			ArrayList<ProductPlate> deleteProductPlatesList = new ArrayList<ProductPlate>();
			deleteProductPlatesList.add(productPlate);
			if (deleteProductPlate(productPlate)) {
				ProductBatchPlateCreatedEvent mPlateCreatedEvent = new ProductBatchPlateCreatedEvent(this, deleteProductPlatesList);
				fireProdcutPlateDeleted(mPlateCreatedEvent);
				productPlate = null;
			}
		}
		tempPane.remove(obj);
		pageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
		new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
	}

	private boolean deleteProductPlate(ProductPlate productPlate) {
		String[] plateKeys = { productPlate.getKey() };
		try {
			if (productPlate.isLoadedFromDB()) {
				StorageDelegate storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
				storageDelegate.deletePlates(plateKeys,MasterController.getUser().getSessionIdentifier());
			}

			if (!pageModel.deleteRegistrationPlate(productPlate)) {
				List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
				for (int i = 0; i < reactionSteps.size(); i++) {
					ReactionStepModel stepModel = reactionSteps.get(i);
					stepModel.deleteProductPlateFromList(productPlate);
				}
			}
			return true;
		} catch (Exception e) {
			log.error("Failed product plate deletion.", e);
			return false;
		}
	}

	protected void fireProdcutPlateDeleted(ProductBatchPlateCreatedEvent event) {
		plateCreationHandler.fireProdcutPlateDeleted(event);
	}

	private void updateProducts() {
	  for (String key : productViews.keySet()) {
			this.productTableTabbedPane.addTab(key, this.productViews.get(key));
		}
		switchView(selectedView);
	}

	private void removePlatesFromTabs(List<ProductPlate> plates) {
		for (int i = 0; i < plates.size(); i++) {
			ProductPlate productPlate = plates.get(i);
			selectedPlates.remove(productPlate);
			int index = productTableTabbedPane.indexOfTab(productPlate.getLotNo());
			if (index > -1 
			        && (productTableTabbedPane.getTabCount() > index)
					&& productTableTabbedPane.getTitleAt(index).equals(productPlate.getLotNo())) {
				productTableTabbedPane.remove(index);
			}
			productViews.remove(productPlate.getLotNo());

			if (index > -1 
				    && (plateRackViewTab.getTabCount() > index)
					&& plateRackViewTab.getTitleAt(index).equals(productPlate.getLotNo())) {
				plateRackViewTab.remove(index);
			}
			plateRackViews.remove(productPlate.getLotNo());

		}
	}

	// for plate viewer
	private void createCollapsiblePane(String paneTitle) {
		viewBatch_Pane = new CollapsiblePane(paneTitle);
		viewBatch_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		viewBatch_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		viewBatch_Pane.setSteps(1);
		viewBatch_Pane.setStepDelay(0);
		viewBatch_Pane.getContentPane().setLayout(new BorderLayout());
	}// end method

	public CollapsiblePane getPlateViewer() {
		return viewBatch_Pane;
	}
	
	public PCeNBatchInfoTableView getCurrentTableView() {
		JPanel productPanel = (JPanel) productTableTabbedPane.getSelectedComponent();
		if (productPanel == null) {
			productPanel = (JPanel) productTableTabbedPane.getComponent(0);
		}
		JScrollPane scrollPane = ((JScrollPane) productPanel.getComponent(1));
		return (PCeNBatchInfoTableView) scrollPane.getViewport().getView();
	}

	/**
	 * @return the plateRackViewTab
	 */
	public JTabbedPane getPlateRackViewTab() {
		return plateRackViewTab;
	}

	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		List<ProductPlate> plates = event.getPlates();
		addPlatesToTabs(plates);
		//this.refreshNonPlatedBatches();
		updateProducts();
	}

	private void addProductsToTabs(NotebookPageModel pageModel) {
		List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
		if (reactionSteps == null || reactionSteps.size() < 1) {
			// log no products error
			return;
		}
		Collections.sort(reactionSteps, new ReactionStepSorter()); // vb 3/20 if we are creating experiment from Synthesis Plan steps will not
																	// be sorted
		if (reactionSteps.size() == 1) {
			ReactionStepModel reactionStep = reactionSteps.get(0);
			this.createProductTab(reactionStep, 1, true);
		} else {
			for (int i = 1; i < reactionSteps.size(); i++) {
				boolean isLastStep = i + 1 == reactionSteps.size();
				ReactionStepModel reactionStep = reactionSteps.get(i);
				this.createProductTab(reactionStep, i, isLastStep);
			}
		}
	}

	private void createProductTab(ReactionStepModel reactionStep, int stepNumber, boolean isLastStep) {

		List<ProductBatchModel> batches = reactionStep.getAllActualProductBatchModelsInThisStep();
		// Sort Batches to ensure compareTo(BatchModel) order
		if(batches != null && batches.isEmpty() == false) {
			Collections.sort(batches);
		}

		PCeNProductsTableModelConnector connector = new PCeNProductsTableModelConnector(batches, pageModel);
		PCeNTableModel model = new PCeNTableModel(connector);
		final PCeNBatchInfoTableView plateTableViewTable = new PCeNBatchInfoTableView(model, PCeNTableView.tableViewRowHeight, connector, pageModel);
		connector.setTable(plateTableViewTable);
		this.tableList.add(plateTableViewTable);
		// If this is the last step, save the tableView for adding compounds
		if (isLastStep) {
			this.lastStepProductTableView = plateTableViewTable;
			this.lastStepProductConnector = connector;
			parallelProductTablePopupMenuManagerForProductTable.addMouseListener(lastStepProductTableView, lastStep);
		}
		final JScrollPane scrollTableViewPane = new JScrollPane(plateTableViewTable);
        scrollTableViewPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        plateTableViewTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                if(plateTableViewTable.getHeight() > PCeNTableView.elevenRowsHeight) {
                	scrollTableViewPane.setPreferredSize(new Dimension(scrollTableViewPane.getViewport().getWidth(), PCeNTableView.elevenRowsHeight + PCeNTableView.heightDifference));
                	scrollTableViewPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                } else {
                	scrollTableViewPane.setPreferredSize(null);
                	scrollTableViewPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                }
            }
        });
		BatchSelectionListener[] listeners = new BatchSelectionListener[2];
		listeners[0] = batchSelectionListener;
		listeners[1] = this;
		plateTableViewTable.setProductBatchDetailsContainerListenerList(listeners);
		PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(plateTableViewTable);
		JPanel productTableViewPanel = new JPanel();
		productTableViewPanel.setLayout(new BorderLayout());
		productTableViewPanel.setBackground(Color.LIGHT_GRAY);
		productTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
		productTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);
		if (isLastStep) {
			Map<String, JPanel> temp = new LinkedHashMap<String, JPanel>();
			
			temp.put("Product", productTableViewPanel);
			for (String key : productViews.keySet()) {
				temp.put(key, productViews.get(key));
			}
			productViews = temp;
		} else
			productViews.put("P" + stepNumber, productTableViewPanel);
	}

	private void addPlatesToTabs(List<ProductPlate> plates) {
		for (int i = 0; i < plates.size(); i++) {
			ProductPlate productPlate = plates.get(i);
			// Create table view and add it to the tabbed pane
			PCeNSingleProductPlateTableModelConnector plateTableViewConnector = new PCeNSingleProductPlateTableModelConnector(productPlate, pageModel);
			PCeNTableModel model = new PCeNTableModel(plateTableViewConnector);
			PCeNBatchInfoTableView plateTableViewTable = new PCeNBatchInfoTableView(model, 70, plateTableViewConnector, pageModel);
			this.tableList.add(plateTableViewTable);
			JScrollPane scrollTableViewPane = new JScrollPane(plateTableViewTable);
			currentTableView = plateTableViewTable;

			// Set the listeners
			BatchSelectionListener[] batchSelectionListeners = new BatchSelectionListener[2];
			batchSelectionListeners[0] = batchSelectionListener;
			batchSelectionListeners[1] = this;
			plateTableViewTable.setProductBatchDetailsContainerListenerList(batchSelectionListeners);

			// Create the plate toolbar
			PCeNTableViewToolBar productTableViewToolBar = new PCeNTableViewToolBar(plateTableViewTable);
			JPanel plateTabTableViewPanel = new JPanel();
			plateTabTableViewPanel.setLayout(new BorderLayout());
			plateTabTableViewPanel.setBackground(Color.LIGHT_GRAY);
			plateTabTableViewPanel.add(productTableViewToolBar, BorderLayout.NORTH);
			plateTabTableViewPanel.add(scrollTableViewPane, BorderLayout.CENTER);
			productViews.put(productPlate.getLotNo(), plateTabTableViewPanel);

			// Create and save the plate view
			StaticPlateRenderer mStaticPlateRenderer = mProductBatchRackViewPanel.buildCeNproductPlateViewer(productPlate,
			                                                                                                 batchSelectionListeners);
			plateRackViews.put(productPlate.getLotNo(), PanelFormatter.formatPlatePanel(mStaticPlateRenderer));
		}
	}

	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event) {
		removePlatesFromTabs(event.getPlates());
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

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			String viewer = (String) e.getItem();
			switchView(viewer);
		}
	}

	private void switchView(String comboText) {
		if (!isLoading) {
			// set the batch panel back to empty
			for (PCeNTableView table : tableList) {
				table.setSelectedAreas(null); // vb 11/29
			}
			this.batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));
		}

		if (selectedView.equals(TABLES_VIEW)) {
			if (plateRackViewTab != null) {
				viewBatch_Pane.getContentPane().remove(plateRackViewTab);
			}
			viewBatch_Pane.getContentPane().add(this.productTableTabbedPane, BorderLayout.CENTER);
			productTableTabbedPane.repaint();
		} else if (selectedView.equals(PLATES_VIEW)) {
			if (plateRackViewTab != null || productTableTabbedPane != null) {
				viewBatch_Pane.getContentPane().remove(productTableTabbedPane);
			}
			viewBatch_Pane.getContentPane().add(refreshRackView(), BorderLayout.CENTER);
			this.batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, getBatchInFirstWell(null)));
		}
	}// end method

	private ProductBatchModel getBatchInFirstWell(ProductPlate productPlate) {
		if (productPlate == null) {
			if (selectedPlates != null && selectedPlates.size() > 0)
				productPlate = (ProductPlate) selectedPlates.get(0);
		}
		if (productPlate != null) {
			PlateWell<ProductBatchModel> wells[] = productPlate.getWells();
			if (wells[0] != null)
				return wells[0].getBatch();
			else
				return null;
		} else
			return null;
	}

	private Component refreshRackView() {
		plateRackViewTab.removeAll();
		int count = 0;

		for (String key : plateRackViews.keySet()) {
			if (key.equals(CeNConstants.PSEUDO_PLATE_LABEL)) {
				continue;
			}
			JPanel plateBatchViewPanel = plateRackViews.get(key);
			plateRackViewTab.insertTab(key, null, plateBatchViewPanel, null, count);
			count++;
		}

		return plateRackViewTab;
	}

	// This method is only used when a new batch is added, the added batch
	public void batchSelectionChanged(BatchModel batchModel) {
		this.batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, batchModel));
	}

	public void createCompound() {
		lastStepProductTableView.stopEditing();
		ProductBatchModel newBatch = new ProductBatchModel();
		createCompound(newBatch, false);
		updateVnvButtonState();
	}
	
	public void createCompound(ProductBatchModel newBatch, boolean fromSDFile) {
		try {
			BatchNumber batchNumber = null;
			if (pageModel.isParallelExperiment()) {
				batchNumber = new BatchNumber(pageModel.getNextBatchNumberForProductBatch());
			} else {
				batchNumber = pageModel.getNextBatchNumberForSingletonProductBatch();
			}
			newBatch.setBatchNumber(batchNumber);
			newBatch.setBatchType(BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL));
			newBatch.setUserAdded(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Batch number creation failed.!");
			log.debug("Batch number creation failed for user added actual batch", ex);
			return;
		}
		String owner = null;
		try {
			owner = MasterController.getUser().getPreference(NotebookUser.PREF_LastOwner);
		} catch (Exception e) {
		}
		if (owner == null || owner.length() == 0)
			newBatch.setOwner(pageModel.getBatchOwner());
		else
			newBatch.setOwner(owner);

		newBatch.setSynthesizedBy(pageModel.getPageHeader().getUserName());
		newBatch.getCompound().setCreatedByNotebook(true);
		
		if (!fromSDFile) {
			// Remove registration information
			newBatch.getCompound().setVirtualCompoundId("");
			newBatch.getRegInfo().resetRegistrationInfo();
		}
			
		newBatch.setLoadedFromDB(false);
		// Ask Sumant and set the Stoich attributes. Jags_todo...
		/* newBatch.setPrecursors(stoichModel.getPreCursorsForReaction()); */
		newBatch.setProductFlag(true);
		StoicModelInterface stoichModel = ReactionStepModelUtils.findLimitingReagent(lastStep);
		if (stoichModel != null) {
			newBatch.setTheoreticalMoleAmountFromStoicAmount(stoichModel.getStoicMoleAmount());
		}

		pageModel.getUserAddedBatchesList().addBatch(newBatch);
		
		lastStep.setModelChanged(true);
		pageModel.getPseudoProductPlate(false).addNewBatch(newBatch);
		pageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
		pageModel.setModelChanged(true); // To enable the save buttons
		new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
		if (compoundCreationHandler != null) {
			CompoundCreationEvent event = new CompoundCreationEvent(this, newBatch, lastStep, selectedView);
			compoundCreationHandler.fireNewCompoundCreated(event);
		}
		if (selectedView.equals(TABLES_VIEW)) // Products View
		{
			//productTableTabbedPane.setSelectedIndex(productTableTabbedPane.getTabCount() - 1);

			// In consideration of the fact that we can add new batch only to product list (not to plate)
			// after adding new batch we switch tabbed pane to product list
			productTableTabbedPane.setSelectedIndex(0);
			lastStepProductTableView.scrollRectToVisible(new Rectangle(10 * 10, lastStepProductTableView.getRowHeight()
					* (lastStepProductTableView.getRowCount()), 5, lastStepProductTableView.getRowHeight()));
		} else {
			if (selectedView.equals(PLATES_VIEW)) {
				selectedView = TABLES_VIEW;
				tablesRadioButton.setSelected(true);
				switchView(selectedView);
			}
			// if (viewOptionsCombo.getSelectedIndex() == 2) //Plate Rack View
			// viewOptionsCombo.setSelectedIndex(1);
			if (productTableTabbedPane.getTabCount() == 0)
				summaryBatchRackCont.setSelectedPlateIndex(0);
			productTableTabbedPane.setSelectedIndex(0);
			//currentTableView = nonPlatedBatchesPlateTableView;
			currentTableView.scrollRectToVisible(new Rectangle(10 * 10, currentTableView.getRowHeight()
					* (currentTableView.getRowCount()), 5, currentTableView.getRowHeight()));
		}
		if(this.pageModel.isParallelExperiment() && currentTableView != null) {
			currentTableView.highlightLastRow(); // this is Non-Plated tab
		}
		lastStepProductTableView.highlightLastRow();
		batchSelectionChanged(newBatch);
		MasterController.getGUIComponent().enableSaveButtons();
	}

	public void fireCompoundRemoved(CompoundCreationEvent event) {
		// This task is called executed here as the compound can be deleted from ProductTablePopupMenuManager and we can not have
		// this code right there.
		MasterController.getGUIComponent().enableSaveButtons();
		pageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
		new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
		BatchAttributeComponentUtility.removeBatchFromCachedProductBatchesToMolstringsMap(event.getBatch(), pageModel);
		if (compoundCreationHandler != null)
			compoundCreationHandler.fireCompoundDeleted(event);
	}
	
	private boolean deleteConfirmation(int selectedBatches) {
		String batchWord = "batch" + (selectedBatches > 1 ? "es" : "");
		int result = JOptionPane.showConfirmDialog(MasterController.getGuiComponent(), 
        		"Do you want to delete " + (selectedBatches > 1 ? "these " + selectedBatches + " " : "this ") + batchWord + "?", 
        		"Delete " + batchWord,
        		JOptionPane.YES_NO_OPTION,
        		JOptionPane.WARNING_MESSAGE);
		return result == JOptionPane.YES_OPTION;
	}
	
	public void deleteCompound() {
		List<ProductBatchModel> selected = getSelectedBatches();
		if(selected == null || selected.isEmpty()) {
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Please select editable batches to delete!", MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (deleteConfirmation(selected.size())) {
			for(ProductBatchModel batchModel : selected) {
				internalDeleteCompound(batchModel);
			}
		}
	}
		
	public void deleteCompound(ProductBatchModel batch) {
		if (!batch.isEditable()) {
			JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Can't delete this batch!", MasterController.getGUIComponent().getTitle(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (deleteConfirmation(1)) {
			internalDeleteCompound(batch);
		}
	}
	
	public void internalDeleteCompound(ProductBatchModel batch) {
		if (batch != null) {
			if (batch.isEditable()) {
				final String batchNumber = batch.getBatchNumberAsString();
						
				PAnalyticalUtility analyticalUtility = new PAnalyticalUtility(pageModel);
				analyticalUtility.unlinkAll(batchNumber);
			
				List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
				ReactionStepModel reactionStepModel = (ReactionStepModel) reactionSteps.get(reactionSteps.size() - 1);
				List<BatchesList<ProductBatchModel>> products = reactionStepModel.getProducts();
				BatchesList<ProductBatchModel> batchesList;
			
				if (CeNConstants.PRODUCTS_SYNC_INTENDED.equals(batch.getPosition())) {
					batchesList = pageModel.getNewSyncIntendedBatchesList();
				} else {
					batchesList = pageModel.getUserAddedBatchesList();
				}
			
				if (!batch.isLoadedFromDB()) {
					batchesList.removeBatch(batch);
				} else {
					batch.markToBeDeleted(true);
				}

				if (batchesList.getBatchModels().size() == 0 && batchesList.isLoadedFromDB() == false) {
					products.remove(batchesList);
				}
			
				lastStepProductTableView.stopEditing();
				PCeNProductTableModelConnector controller = (PCeNProductTableModelConnector) lastStepProductTableView.getConnector();
				controller.removeProductBatchModel(batch); // PCeNProductsTableModelController
				PCeNTableModel model = (PCeNTableModel) lastStepProductTableView.getModel();
				model.fireTableDataChanged();

				List<PlateWell<ProductBatchModel>> wellsList = pageModel.getPseudoProductPlate(false).getPlateWellsforBatch(batch);
				if (wellsList != null && wellsList.size() > 0) {
					PlateWell<ProductBatchModel> well = wellsList.get(0);
					well.setToDelete(true);
				}

				batch.setToDelete(true);
				batch.setModelChanged(true);
				pageModel.setModelChanged(true);
				pageModel.getPseudoProductPlate(false).removeBatch(batch);
						
				batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));

				CompoundCreationEvent event = new CompoundCreationEvent(this, batch, lastStep, selectedView);
				fireCompoundRemoved(event);
			} else {
				if (CeNConstants.PRODUCTS_SYNC_INTENDED.equals(batch.getPosition())) {
					batch.setPosition(CeNConstants.BATCH_TYPE_ACTUAL);
				
					pageModel.getNewSyncIntendedBatchesList().removeBatch(batch);
				
					pageModel.getUserAddedBatchesList().addBatch(batch);
					
					lastStep.setModelChanged(true);
					pageModel.getPseudoProductPlate(false).addNewBatch(batch);
					pageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
					pageModel.setModelChanged(true); // To enable the save buttons
					new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
				}
			}
		}

		updateSyncWithIntendedProductsActionState();
		updateVnvButtonState();
	}
	
	public void compoundRemoved(CompoundCreationEvent parentEvent) {
		List<PlateWell<ProductBatchModel>> wellsList = pageModel.getPseudoProductPlate(false).getPlateWellsforBatch((ProductBatchModel)parentEvent.getBatch());
		// The reason is the batch can be deleted from ProductTablePopUpMenu class and that does not have access to the wells list.
		// If it is called from Delete Compound button(this class), it is not required to execute once again.
		if (wellsList.size() != 0) {
			// PlateWell well = (PlateWell) wellsList.get(0);
			ProductBatchModel batch = (ProductBatchModel) parentEvent.getBatch();
			PlateWell<ProductBatchModel> well = wellsList.get(0);
			well.setToDelete(true);

			batch.setToDelete(true);
			batch.setModelChanged(true);
			pageModel.setModelChanged(true);
			if (!batch.isLoadedFromDB())
				pageModel.getPseudoProductPlate(false).removeBatch((ProductBatchModel) batch);
		}
		if (this.pageModel.isParallelExperiment())
		{
		//this.nonPlatedBatchesConnector.removeProductBatchModel((ProductBatchModel) parentEvent.getBatch());

			if (currentTableView != null) {
				PCeNTableModel model1 = (PCeNTableModel) currentTableView.getModel();
				model1.fireTableDataChanged();
			}
		} else {
			this.lastStepProductConnector.removeProductBatchModel(parentEvent.getBatch());
			PCeNTableModel model1 = (PCeNTableModel) this.lastStepProductTableView.getModel();
			model1.fireTableDataChanged();
		}
	}

	public void batchSelectionChanged(BatchSelectionEvent event) {
		if (event == null) {
			return;
		}

		Object obj = event.getSubObject();
		ProductBatchModel batch = null;
		if (obj instanceof PlateWell<?>) {
			PlateWell<ProductBatchModel> well = (PlateWell<ProductBatchModel>) obj;
			batch = (ProductBatchModel) well.getBatch();
		} else if (obj instanceof ProductBatchModel) {
			batch = (ProductBatchModel) obj;
		}
	}

	public void compoundUpdated(CompoundCreationEvent event) {
	  if (currentTableView == null || currentTableView.getModel() == null) {
	    return;
	  }
		((PCeNTableModel) currentTableView.getModel()).fireTableDataChanged();
	}

	public void newCompoundCreated(CompoundCreationEvent event) {
		//this.refreshNonPlatedBatches();
		this.refreshProductBatchesAfterCompoundCreated(event);
		// updateSelectedPlatesProducts(selectedPlates);
		selectedView = TABLES_VIEW;
		if (tablesRadioButton != null) {
			tablesRadioButton.setSelected(true);
		}
		switchView(selectedView);
	}

	private void refreshProductBatchesAfterCompoundCreated(CompoundCreationEvent event) {
		ProductBatchModel batch = (ProductBatchModel) event.getBatch();
		this.lastStepProductConnector.addProductBatchModel(batch);
		((PCeNTableModel) lastStepProductTableView.getModel()).fireTableDataChanged();
	}

	public void dispose() {
		if (this.plateRackViews != null) {
			this.plateRackViews.clear();
			this.plateRackViews = null;
		}
		if (this.productViews != null) {
			this.productViews.clear();
			this.productViews = null;
		}
		if (this.selectedPlates != null) {
			this.selectedPlates.clear();
			this.selectedPlates = null;
		}
		if (this.tableList != null) {
			this.tableList.clear();
			this.tableList = null;
		}
	}

	public void selectBatch(String batchNumber) {
		if (batchNumber == null) {
			return;
		}
		selectedView = TABLES_VIEW;
		if (tablesRadioButton != null) {
			tablesRadioButton.setSelected(true);
		}
		switchView(selectedView);
		List<ReactionStepModel> reactionSteps = pageModel.getReactionSteps();
		int step = -1;
		int batchRowNo = -1;
		int position = 0;
		for (int i = 0; i < reactionSteps.size(); i++) {
			ReactionStepModel reactionStep = reactionSteps.get(i);
			List<ProductBatchModel> productBatchModelList = reactionStep.getAllProductBatchModelsInThisStep();
			for (int j = 0; j < productBatchModelList.size(); j++) {
				ProductBatchModel batchModel = productBatchModelList.get(j);
				String batchNumberString = batchModel.getBatchNumber().getBatchNumber();
				if (batchNumberString.equals(batchNumber)) {
					step = i;
					batchRowNo = position;
					break;
				}
				if(StringUtils.isNotBlank(batchNumberString)) {
					position++;					
				}
			}
		}
		productTableTabbedPane.setSelectedIndex(step);
		PCeNBatchInfoTableView tempTtableView = null;
		if (lastStep.getStepNumber() == step) {
			tempTtableView = lastStepProductTableView;
		} else {
			JPanel productPanel = (JPanel) productViews.get("P" + step);
			JScrollPane scrollPane = ((JScrollPane) productPanel.getComponent(1));
			tempTtableView = (PCeNBatchInfoTableView) scrollPane.getViewport().getView();
		}
		tempTtableView.scrollRectToVisible(new Rectangle(10 * 10, tempTtableView.getRowHeight() * (batchRowNo), 
		                                                 5, tempTtableView.getRowHeight()));

		String lastNum = batchNumber.substring(batchNumber.lastIndexOf('-')+1);
		PCeNTableModel model = (PCeNTableModel)tempTtableView.getModel();		
		for (int i = 0; i < tempTtableView.getModel().getColumnCount(); i++) {
			if (PCeNTableView.NBK_BATCH_NUM.equals(tempTtableView.getModel().getColumnName(i))) {
				for (int j = 0; j < tempTtableView.getRowCount(); j++) {				
					if (model.getValueAt(j, i).equals(lastNum)) {
						tempTtableView.setRowSelectionInterval(j, j);
					}
				}				
			}			
		}
		tempTtableView.valueChanged();
		/*
		if (batchRowNo > 0)
			tempTtableView.setRowSelectionInterval(batchRowNo - 1, batchRowNo);
		else
			tempTtableView.setRowSelectionInterval(batchRowNo, batchRowNo);
		*/
	}

	public void selectPlatesView() {
		selectedView = TABLES_VIEW;
		tablesRadioButton.setSelected(true);
		switchView(selectedView);
	}

	protected void fireNewProductPlateCreated(ProductBatchPlateCreatedEvent event) {
		plateCreationHandler.fireNewProductPlateCreated(event);
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		if (pageModel != pageModel2) {
			this.pageModel = pageModel2;
		}
		setButtonsEditable(pageModel.isEditable());
	}

	public void syncIntendedProducts() {
		lastStepProductTableView.stopEditing();
		// clear the list with sync intended products
		BatchesList<ProductBatchModel> batchesList = pageModel.getNewSyncIntendedBatchesList();
		List<ProductBatchModel> oldProductBatchesList = batchesList.getBatchModels();
		
		for (Iterator<ProductBatchModel> batchesIt = oldProductBatchesList.iterator(); batchesIt.hasNext(); ) {
			ProductBatchModel batchModel = batchesIt.next();
			
			if (batchModel.isEditable()) {
				this.batchSelectionListener.batchSelectionChanged(new BatchSelectionEvent(this, null));
				CompoundCreationEvent event = new CompoundCreationEvent(this, batchModel, lastStep, selectedView);
				fireCompoundRemoved(event);

				if (!batchModel.isLoadedFromDB()) {
					batchesIt.remove();
				} else {
					batchModel.markToBeDeleted(true);
				}
			}
		}
		
		ArrayList<ProductBatchModel> productBatchesList = getIntendedProducts(pageModel.getSingletonBatchs());
		if (productBatchesList.size() != 0) {
			String firstBatchNumber = null;
			for (ProductBatchModel productBatchModel : productBatchesList) {
				if (productBatchModel == null) {
					continue;
				}
				ProductBatchModel newBatch = BatchAttributeComponentUtility.syncBatch(productBatchModel, pageModel.getSummaryReactionStep());
				newBatch.setBatchType(BatchType.getBatchType("ACTUAL"));
				newBatch.setParentKey(productBatchModel.getKey());
				newBatch.setPosition(CeNConstants.PRODUCTS_SYNC_INTENDED);
				batchesList = pageModel.getNewSyncIntendedBatchesList();
				batchesList.addBatch(newBatch);
				initializeAndDisplay(newBatch);
				
				pageModel.getPseudoProductPlate(false).addNewBatch(newBatch);
				pageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
				pageModel.setModelChanged(true); // To enable the save buttons
				
				if (firstBatchNumber == null) {
					firstBatchNumber = newBatch.getBatchNumberAsString();
				}
				// newBatch.setParentBatchNumber(productBatchModel.getBatchNumber().getBatchNumber());// May be required.
			}
			selectBatch(firstBatchNumber);
			syncIntendedProductsBtn.setEnabled(false);
		} else {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "There are no intended products exist to sync.");
		}
		
		parallelProductTablePopupMenuManagerForProductTable.updateSyncWithIntendedProductsActionState(false);
		pageModel.getPseudoProductPlate(true);
		new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
	}

	public void updateSyncWithIntendedProductsActionState() {
		ArrayList<ProductBatchModel> productBatchesList = getIntendedProducts(pageModel.getSingletonBatchs());
		BatchesList<ProductBatchModel> batchesList = pageModel.getNewSyncIntendedBatchesList();
		
		int productBatchModelListSize = 0;
		List<ProductBatchModel> batchModelList = batchesList.getBatchModels();
		for (BatchModel batchModel : batchModelList) {
			if (!batchModel.isToBeDeleted()) {
				productBatchModelListSize++;
			}
		}
		
		boolean state = productBatchModelListSize != productBatchesList.size();
		if (!pageModel.isEditable()) {
			syncIntendedProductsBtn.setEnabled(false);
		} else {
			syncIntendedProductsBtn.setEnabled(state);
		}
		parallelProductTablePopupMenuManagerForProductTable.updateSyncWithIntendedProductsActionState(state);
	}
	
	private void updateVnvButtonState() {
		if (lastStepProductTableView != null) {
			if (lastStepProductTableView.getRowCount() >= 20) {
				vnvButton.setEnabled(pageModel.isEditable() && MasterController.isVnvEnabled());
			} else {
				vnvButton.setEnabled(false);
			}
		}
	}
	
	private ArrayList<ProductBatchModel> getIntendedProducts(List<ProductBatchModel> singletonBatches) {
		ArrayList<ProductBatchModel> productbatchModelList = new ArrayList<ProductBatchModel>();
		for (ProductBatchModel batchModel : singletonBatches) {
			if (batchModel.getBatchType().equals(BatchType.INTENDED_PRODUCT)) {
				productbatchModelList.add(batchModel);
			}
		}
		return productbatchModelList;
	}

	private void initializeAndDisplay(ProductBatchModel newBatch) {
		try {
			BatchNumber batchNumber = pageModel.getNextBatchNumberForSingletonProductBatch();
			newBatch.setBatchNumber(batchNumber);
			newBatch.setBatchType(BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL));
			newBatch.setUserAdded(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Batch number creation failed.!");
			log.error("Error creating batch number for newly added user batch.", ex);
			return;
		}
		ReactionStepModel step = pageModel.getSummaryReactionStep();
		String owner = null;
		try {
			owner = MasterController.getUser().getPreference(NotebookUser.PREF_LastOwner);
		} catch (Exception e) {
		}
		if (owner == null || owner.length() == 0)
			newBatch.setOwner(pageModel.getBatchOwner());
		else
			newBatch.setOwner(owner);

		newBatch.setSynthesizedBy(pageModel.getPageHeader().getUserName());
		newBatch.getCompound().setCreatedByNotebook(true);
		// Ask Sumant and set the Stoich attributes. Jags_todo...
		/* newBatch.setPrecursors(stoichModel.getPreCursorsForReaction()); */
		newBatch.setProductFlag(true);
		StoicModelInterface stoichModel = ReactionStepModelUtils.findLimitingReagent(step);
		if (stoichModel != null) {
			newBatch.getTheoreticalMoleAmount().setCalculated(true);
			//newBatch.setTheoreticalMoleAmount(stoichModel.getStoicMoleAmount());
			//TODO is it correct???
			newBatch.setTheoreticalMoleAmount(newBatch.getMoleAmount());
		}
		// parallelCeNTableModel.fireTableDataChanged();
		if (this.compoundCreationHandler != null) {
			CompoundCreationEvent event = new CompoundCreationEvent(this, newBatch, lastStep, selectedView);
			compoundCreationHandler.fireNewCompoundCreated(event);
		}
		pageModel.getSummaryReactionStep().setModelChanged(true);
		
		MasterController.getGUIComponent().enableSaveButtons();
	}

	protected void importSDFile(File file) {
		FileInputStream inputStream = null;
		ProductBatchModel[] batchModels = null;

		try {
			inputStream = new FileInputStream(file);
			SDFileGeneratorUtil fileGenerator = new SDFileGeneratorUtil();
			batchModels = fileGenerator.getProductBatchModelsFromSDFile(inputStream);
			// Do all necessary conversions
			for (ProductBatchModel batchModel : batchModels) {
				StructureLoadAndConversionUtil.loadSketch(batchModel.getCompound().getStringSketch(), 0, batchModel.getCompound()); // Default (0) is SDfile.
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
							"Invalid file selected. Please check the file and fix errors.");
			ex.printStackTrace();
			return;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		SDFileChloracnegenTester.launchChloracnegenCheckerForBatches(batchModels);
		
		for (ProductBatchModel batchModel : batchModels) {
			createCompound(batchModel, true);
		}
		
		updateVnvButtonState();
	}
	
	public void duplicateCompound(ProductBatchModel batch) {
		ProductBatchModel newBatch = new ProductBatchModel();
		newBatch.deepCopy(batch);  // RegInfo copied in deepCopy()
		newBatch.setPosition(CeNConstants.PRODUCTS_USER_ADDED);
		createCompound(newBatch, false);
	}

	public void setVnvButtonEnabled(boolean b) {
		vnvButton.setEnabled(b && MasterController.isVnvEnabled());
	}
}

