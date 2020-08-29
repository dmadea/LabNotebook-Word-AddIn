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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.chemistry.enotebook.client.gui.chlorac.ChloracnegenBatchStructure;
import com.chemistry.enotebook.client.gui.chlorac.ChloracnegenPredictor;
import com.chemistry.enotebook.client.gui.chlorac.ChloracnegenResultsViewContainer;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.ContainerFrame;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.LimitingReagentChangedEvent;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.PCeNStoichCalculator;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.reaction.ReactionSchemeModelChangeEvent;
import com.chemistry.enotebook.client.gui.page.reagents.PCeNAddToMyReagentsHelper;
import com.chemistry.enotebook.client.gui.page.reagents.ReagentAdditionListener;
import com.chemistry.enotebook.client.gui.page.reagents.ReagentsFrame;
import com.chemistry.enotebook.client.gui.page.stoichiometry.MSDSSearchLauncher;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnSearchReturnClient;
import com.chemistry.enotebook.client.gui.page.stoichiometry.search.PCeNStructureLookupDialog;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewPopupMenuManager;
import com.chemistry.enotebook.client.utils.ReactionStepModelUtils;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.businessmodel.stoichiometry.ComparatorStoicAdditionOrder;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchTypeException;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.ReactionProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.*;
import com.chemistry.enotebook.utils.SwingWorker;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

//This is not a collapsible pane itself , but eventually will be. Currently this panel is added to Collapsible panel.

public class MedChemStoichCollapsiblePane extends JPanel implements StoicToolBarButtonClickListener, ReagentAdditionListener,
		AnalyzeRxnSearchReturnClient {

	private static final long serialVersionUID = -5982485713343830944L;
	
	private static final Log log = LogFactory.getLog(MedChemStoichCollapsiblePane.class);
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();

	private PCeNTableModel monomerTableModel; //runtime impl will be PCeNStoich_MonomersTableModel
	private PCeNStoich_MonomersTableView monomerTableView;
	private PCeNStoich_MonomersTableViewControllerUtility monomerTableConnector;
	private ReactionStepModel rxnStepModel;
	private MedChemStoic_ProductsTableModel productTableModel;
	private MedChemStoic_ProductsTableView productTableView;
	private MedChemStoic_ProductsTableControllerUtility productTableConnector;
	private MedChemStoichTableToolBar mMedChemStoichTableToolBar;
	private PCeNTableViewPopupMenuManager  mStoichTablePopupMenuManager; //runtime impl will be MedChemStoic_MonomersTablePopupMenuManager
	private MedChemStoic_ProductsTablePopupMenuManager mStoichTableProdPopupMenuManager;
	private ChemistryDelegate chemDelegate = null;
	private SwingWorker getChemDelegateProcess = null;
	private SwingWorker calcDelegateProcess = null;
	private SingletonNotebookPageGUI notebookGUI;
	// AnalyzeRxn part
	private final List<MonomerBatchModel> newReactantList = new ArrayList<MonomerBatchModel>();
	private NotebookPageModel pageModel;
	// flag whether to launch chloracnegen test at all or not to (DB prop)
//	flag indicating whether chloracnegen test needs to be done for only un tested compounds
    // or run the test every time for all.(DB prop)
    private boolean testOnlyUnTestedBatches = true; 
    // Flag whether to show non chloracnegens in the viewer (DB prop)
    private boolean showNonChloracnegens = false;
    //flag wether to launch chloracnegen test at all or not to (DB prop)
    private boolean runChloracnegenTest = true;
    //url for more info (db prop)
	private StoichDataChangesListener stoicChangesListener;
	
	private PCeNStoichCalculator calculator;
	
	
	public MedChemStoichCollapsiblePane(NotebookPageModel mpageModel, SingletonNotebookPageGUI mnotebookGUI) {

		//set properties
		try
		{
			runChloracnegenTest = Boolean.valueOf(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ALERT_ENABLED)).booleanValue();
			showNonChloracnegens = Boolean.valueOf(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ALERT_SHOW_NONCHLORACNEGENS)).booleanValue();
			testOnlyUnTestedBatches = Boolean.valueOf(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ALERT_TEST_ONLYUNTESTEDBATCHES)).booleanValue();
		} catch (Exception e) {
			log.debug("Error retrieving and initialzing chloracnegen flags", e);
		}
		this.pageModel = mpageModel;
		this.rxnStepModel = mpageModel.getSummaryReactionStep();
		// Need this reference to call structure changed event.
		this.notebookGUI = mnotebookGUI;
		mStoichTablePopupMenuManager = new MedChemStoic_MonomersTablePopupMenuManager(this);
		mStoichTableProdPopupMenuManager = new MedChemStoic_ProductsTablePopupMenuManager(this);
		monomerTableConnector = new PCeNStoich_MonomersTableViewControllerUtility(rxnStepModel, mpageModel);
		calculator = new PCeNStoichCalculator(rxnStepModel,CeNConstants.PAGE_TYPE_MED_CHEM);
		try {
			stoicChangesListener = new StoichDataChangesListener(this.rxnStepModel,CeNConstants.PAGE_TYPE_MED_CHEM);
			monomerTableConnector.addStoichDataChangesListener(stoicChangesListener);
		} catch (Exception e) {
			// initialized with proper listerner.so don't expect exception.
			log.error("Unexpected error on creation of Stoichiometry Change listener or monomer Table listener.", e);
		}
		monomerTableModel = new PCeNStoich_MonomersTableModel(monomerTableConnector);
		monomerTableView = new PCeNStoich_MonomersTableView(this, 
		                                                    monomerTableModel,
		                                                    StoicConstants.TABLE_ROW_HEIGHT,
		                                                    monomerTableConnector, 
		                                                    mStoichTablePopupMenuManager,
		                                                    rxnStepModel, 
		                                                    pageModel);

		monomerTableConnector.setTableView(monomerTableView);
		
		productTableConnector = new MedChemStoic_ProductsTableControllerUtility(rxnStepModel, pageModel);

		productTableModel = new MedChemStoic_ProductsTableModel(productTableConnector);
		guiInit();
		refresh();
	}

	public NotebookPageModel getPageModel() {
		return pageModel;
	}
	
	private void guiInit() {
		double stoichSize[][] = { { CeNGUIUtils.FILL }, { CeNGUIUtils.PREF, CeNGUIUtils.PREF, 20, CeNGUIUtils.PREF } };
		TableLayout stoichLayout = new TableLayout(stoichSize);
		setLayout(stoichLayout);
		setBorder(new MatteBorder(new Insets(1, 1, 1, 1), new java.awt.Color(0, 0, 0)));
		mMedChemStoichTableToolBar = new MedChemStoichTableToolBar(this, monomerTableConnector);
		this.add(mMedChemStoichTableToolBar, "0,0");
		monomerTableView.setPreferredScrollableViewportSize(new Dimension(StoicConstants.STOICH_EMPTY_WIDTH,
		                                                                  StoicConstants.REAGENTS_EMPTY_HEIGHT));
		JScrollPane sPane = new JScrollPane(monomerTableView);
		this.add(sPane, "0,1");
		// Add Mouse listener for monomers table scroll pane.
		sPane.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				jTableStoicElementMouseReleased(evt);
			}
		});

		JLabel productsTableHeaderLabel = new JLabel("Intended Reaction Products");
		CeNGUIUtils.styleComponentText(Font.BOLD, productsTableHeaderLabel);
		productsTableHeaderLabel.setVisible(true);
		this.add(productsTableHeaderLabel, "0,2");

		productTableView = new MedChemStoic_ProductsTableView(productTableModel, 
		                                                      StoicConstants.TABLE_ROW_HEIGHT,
		                                                      productTableConnector, 
		                                                      mStoichTableProdPopupMenuManager,
		                                                      rxnStepModel);
		JScrollPane sPane2 = new JScrollPane(productTableView);
		productTableView.setPreferredScrollableViewportSize(new Dimension(StoicConstants.STOICH_EMPTY_WIDTH,
		                                                                  StoicConstants.PRODUCTS_EMPTY_HEIGHT));
		
		monomerTableConnector.setProductTableView(productTableView);
		productTableConnector.setProductTableView(productTableView);
		
		this.add(sPane2, "0,3");
		// Add Mouse listener for products table scroll pane.
		sPane2.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				jTableProductsMouseReleased(evt);
			}
		});
		getChemDelegateProcess = new SwingWorker() {
			// TODO: There is a delay when initializing the thread.
			public Object construct() {
				// initialize ChemistryDelegate
				if (chemDelegate == null)
					chemDelegate = new ChemistryDelegate();
				return chemDelegate;
			}

			public void finished() {
				if (chemDelegate == null)
					CeNErrorHandler.showMsgOptionPane(getRootPane().getGlassPane(), "Chemistry Service Delegate Init Failure",
							"Failed to initialize the chemistry delegate.\n" + "No chemistry information will be avialable.",
							JOptionPane.ERROR_MESSAGE);  //CeNErrorHandler.LOG_TO_FILE);
			}
		};
		getChemDelegateProcess.start();
		initChemDelegate(); // If it doesn't work here, we won't have an object.
	}

	//
	// Implements ReagentAdditionListener
	//
	public void addReagentsFromList(List<MonomerBatchModel> reagentsToAdd) {

		if (reagentsToAdd == null || reagentsToAdd.size() == 0)
			return;
		// Set the batchModels to changed status when we first add
		CeN11To12ConversionUtils.setModelChangedForMonBatchModels(reagentsToAdd);
		// because these are reagents, we can always add to this list.
		rxnStepModel.addMonomerBatchListForStoic(reagentsToAdd);
		this.refreshStoichTable();
		refresh();
		// enable save button
		enableSaveButton();
	}

	private void launchMSDSSearch(String casNumber) {
		String userSiteCode = MasterController.getUser().getSiteCode();
		String preferredLang = "EN";
		if (userSiteCode.equals("NGY")) {
			preferredLang = "JA";
		}
		MSDSSearchLauncher.launchMSDSSearch(casNumber, preferredLang);
		return;
	}

	// This method refreshes the Stoich table.Needs to be called after add/delete etc
	public void refreshStoichTable() {
		calculator.recalculateStoich();
		monomerTableView.updateUI();
		updateArrowButtonsEnabledStatus();
	}

	public void stoicToolBarButtonClickAction(String buttonName) {
		mMedChemStoichTableToolBar.requestFocus();
		int row = monomerTableView.getSelectedRow();
		PCeNTableModel rtm = (PCeNTableModel) monomerTableView.getModel();
		if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_UPARROW)) {

			if (row >= 1 && row < monomerTableView.getRowCount()) {
				if (monomerTableView.getModel() instanceof PCeNTableModel) {
					rxnStepModel.swapBatchPositionOrder(rtm.getStoicElementAt(row), rtm.getStoicElementAt(row - 1));
					row--;
					monomerTableView.setRowSelectionInterval(row, row);
					enableSaveButton();
				}
			}

		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_DOWNARROW)) {

			if (row >= 0 && row < monomerTableView.getRowCount() - 1)
				if (monomerTableView.getModel() instanceof PCeNTableModel) {

					rxnStepModel.swapBatchPositionOrder(rtm.getStoicElementAt(row), rtm.getStoicElementAt(row + 1));
					row++;
					monomerTableView.setRowSelectionInterval(row, row);
					enableSaveButton();
				}
		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_MYREAGENTS)) {
			ReagentsFrame.viewReagentsFrame(ReagentsFrame.MY_REAGENTS_TAB, this, pageModel.getNotebookRefAsString());
		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_ADDTOMYREAGENTS)) {

			PCeNAddToMyReagentsHelper handler = new PCeNAddToMyReagentsHelper();
			StoicModelInterface model = rtm.getStoicElementAt(row);
			try {
				handler.addToMyReagentsList(model);
			} catch (Exception ex) {
				CeNErrorHandler.getInstance().logExceptionWithoutDisplay(ex, ex.getMessage());
			}
		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_DBSEARCH)) {
			ReagentsFrame.viewReagentsFrame(ReagentsFrame.SEARCH_REAGENTS_TAB, this, pageModel.getNotebookRefAsString());
		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_MSDSSEARCH)) {
			String searchString = null;
			// If a row is chosen
			if (row != -1) {
				StoicModelInterface model = rtm.getStoicElementAt(row);
				// If that is not a List and MonomerBatchmodel
				if (model instanceof MonomerBatchModel) {
					searchString = (String) rtm.getValueAt(row, MedChemStoic_MonomersTableViewControllerUtility.CAS_NUMBER);
					if (StringUtils.isBlank(searchString)) {
						MonomerBatchModel monmodel = (MonomerBatchModel) model;
						searchString = monmodel.getMonomerId();
					}
				}
			} else {
				searchString = null;
			}

			launchMSDSSearch(searchString);
		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_ANALYZERXN)) {
			log.debug("Analyze Reaction called");
			analyzeReaction();
		} else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_CREATERXN)) {
			log.debug("Create Reaction called");
			stoicToScheme();
		}
		refreshStoichTable();
	}

	// Pass-through for Options menu activation in Reactants Grid
	protected void jTableStoicElementMouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			monomerTableView.mouseReleased(evt);
		}
	}

	// Pass-through for Options menu activation in products Grid
	protected void jTableProductsMouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			productTableView.mouseReleased(evt);
		}
	}

	protected void analyzeReaction() {

		if (chemDelegate == null)
			initChemDelegate();
		if (chemDelegate == null && rxnStepModel != null) {
			// Give a message to the user that the connection to the
			// Reaction Window
			// is not available at this time.
			ceh.logErrorMsg(this, "Cannot analyze reaction at this time.\nChemistry service is not available.",
					"ChemistryDelegate failed initialization");
		} else {
			ReactionProperties rp = new ReactionProperties();
			rp.ReturnedSketchFormat = (MasterController.getGuiController().getDrawingTool() == Compound.CHEMDRAW) ? ReactionProperties.CHEM_DRAW_SKETCH
					: ReactionProperties.ISIS_DRAW_SKETCH;
			rp.Reaction = rxnStepModel.getRxnScheme().getNativeSketch();
			rp.isolateFragments = false; // Bug 24509 - May want to make
			// this a gui option.
			try {
				CeNJobProgressHandler.getInstance().addItem("Analyzing Rxn...");
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				rp = chemDelegate.extractReactionComponents(rp);
				if (rxnStepModel != null && rp != null) {
					clearSearchArrays();
					// update reactants
					List<byte[]> rxnReactantComponents = new ArrayList<byte[]>(rp.Reactants);
					List<MonomerBatchModel> curReactantBatches = rxnStepModel.getStoicBatchesList().getBatchModels();
					// create batches from the structures - retain order info
					newReactantList.addAll(ReactionStepModelUtils.createReactantBatchesFromStructures(rxnReactantComponents,
					                                                                                  pageModel));
					// search for batches not found in curReactantList
					List<MonomerBatchModel> batchesToSearch = ReactionStepModelUtils.getUnmatchedReactantBatches(newReactantList, 
					                                                                                             curReactantBatches,
					                                                                                             pageModel);
					if (batchesToSearch.size() > 0) {
						// pop-up the Search Dialog
						ContainerFrame cf = new ContainerFrame();
						cf.setTitle("Reactant Structure Search");
						cf.setSize(new java.awt.Dimension(800, 480));
						CeNJobProgressHandler.getInstance().removeItem("Analyzing Rxn...");
						CeNJobProgressHandler.getInstance().addItem("Found Structures to search...");
						PCeNStructureLookupDialog searchDialog = new PCeNStructureLookupDialog(cf, pageModel, this, batchesToSearch);
						searchDialog.addInternalFrameListener(new InternalFrameAdapter() {
							public void internalFrameClosed(InternalFrameEvent e) {
								MedChemStoichCollapsiblePane.this.refresh();
							}
						});
						cf.addInternalFrame(searchDialog);
						MasterController.getGUIComponent().setEnabled(false);
						searchDialog.setVisible(true);
						cf.setLocationRelativeTo(null);
						cf.setVisible(true);
						// searchDialog = null;
					} else {
						CeNJobProgressHandler.getInstance().addItem("Updating reactant order...");
						// make sure order of reactants is correct.
						ReactionStepModelUtils.updateReactantOrderBasedOnList(newReactantList, 
						                                                      pageModel.getSummaryReactionStep());
						CeNJobProgressHandler.getInstance().removeItem("Updating reactant order...");
						CeNErrorHandler.showMsgOptionPane(this, "Finished", "Stoichiometry is synchronized",
						                                  JOptionPane.INFORMATION_MESSAGE);
						MasterController.getGUIComponent().setEnabled(true);
					}
				} else {
					ceh.logErrorMsg(this,
									"Failed to analyze reaction.\nSome ChemDraw reactions do not analyze due to the way molecules are grouped.\nPlease check the reaction and try again.",
									"Analyze Reaction Failure");
					MasterController.getGUIComponent().setEnabled(true);
				}
			} catch (ChemUtilInitException e) {
				ceh.logExceptionMsg(this, "Could not synchronize stoich with reaction.  No Chemistry Service available", e);
				MasterController.getGUIComponent().setEnabled(true);
			} catch (ChemUtilAccessException e) {
				ceh.logExceptionMsg(this,
						"Could not synchronize stoich with reaction.  There was an error processing reactant structures.", e);
				MasterController.getGUIComponent().setEnabled(true);
			} catch (Throwable t) {
				ceh.logExceptionMsg(this, "Could not synchronize stoich with reaction. ", t);
				MasterController.getGUIComponent().setEnabled(true);
			} finally {
				CeNJobProgressHandler.getInstance().removeItem("Analyzing Rxn...");
				CeNJobProgressHandler.getInstance().removeItem("Found Structures to search...");
				CeNJobProgressHandler.getInstance().removeItem("Updating reactant order...");
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		refresh();
	}

	protected boolean initChemDelegate() {
		boolean result = false;
		if (chemDelegate == null) {
			// the swing worker should have filled in the chemDelegate as
			// it's result.
			// if it is null we need to get a chemDelegate.
			getChemDelegateProcess.get();
			result = false;
		} else {
			result = true; // ChemDelegate was already initialized.
		}
		return result;
	}

	public void refresh() {

		int row = monomerTableView.getSelectedRow();
		if (monomerTableView.getModel() != null && monomerTableView.getModel() instanceof MedChemStoic_MonomersTableModel)
			((MedChemStoic_MonomersTableModel) monomerTableView.getModel()).refresh();
		Dimension rtpd = monomerTableView.getPreferredScrollableViewportSize();
		int rowHeightReag = (monomerTableView.getRowCount() * monomerTableView.getRowHeight()); // account for header
		if (rowHeightReag < StoicConstants.REAGENTS_EMPTY_HEIGHT)
			rowHeightReag = StoicConstants.REAGENTS_EMPTY_HEIGHT;
		monomerTableView.setPreferredScrollableViewportSize(new Dimension(rtpd.width, rowHeightReag));
		if (row >= 0 && row < monomerTableView.getRowCount())
			monomerTableView.setRowSelectionInterval(row, row);
		row = productTableView.getSelectedRow();
		if (productTableView.getModel() != null && productTableView.getModel() instanceof MedChemStoic_ProductsTableModel)
			((MedChemStoic_ProductsTableModel) productTableView.getModel()).refresh();
		Dimension jtpd = productTableView.getPreferredScrollableViewportSize();
		int rowHeightProd = (productTableView.getRowCount() * productTableView.getRowHeight()); // account for header
		if (rowHeightProd < StoicConstants.PRODUCTS_EMPTY_HEIGHT)
			rowHeightProd = StoicConstants.PRODUCTS_EMPTY_HEIGHT;
		productTableView.setPreferredScrollableViewportSize(new Dimension(jtpd.width, rowHeightProd));
		if (row >= 0 && row < productTableView.getRowCount())
			productTableView.setRowSelectionInterval(row, row);
		/*
		 * refreshButtons(); revalidate();
		 */
		
		updateAnalyzeAndCreateButtonsEnabledStatus();
		updateArrowButtonsEnabledStatus();
	}

	/*
	 * public void refreshButtons() { boolean isEnabled = isEditable(); jButtonAddFromMyList.setEnabled(isEnabled);
	 * jButtonSearchReagents.setEnabled(true); int row = jTableReactants.getSelectedRow(); jButtonMoveNext.setEnabled(isEnabled &&
	 * row >= 0 && row < jTableReactants.getRowCount() - 1); jButtonMovePrev.setEnabled(isEnabled && row > 0 && row <
	 * jTableReactants.getRowCount()); jButtonCreateRxn.setEnabled(isEnabled &&
	 * stoichModel.getBatches(BatchType.REACTANT_ORDINAL).size() > 0); if (rxnViewer == null) { jButtonAnalyzeRxn.setEnabled(false); }
	 * else { jButtonAnalyzeRxn .setEnabled(isEnabled && rxnViewer.getNativeSketch() != null && rxnViewer.getNativeSketch().length >
	 * 0); }
	 *  }
	 */
	private void clearSearchArrays() {
		newReactantList.clear();
	}

	// This called back from PCeNStructureLookupDialog
	public void doUpdateResults(boolean cancelFlag) {
		if (cancelFlag) {
			clearSearchArrays();
		} else {
			try {
				CeNJobProgressHandler.getInstance().addItem("Updating stoichiometry...");
				// List contains updated reactant batches.
				ReactionStepModelUtils.syncReactants(newReactantList, this.rxnStepModel);
				// Update Limiting column
				List<StoicModelInterface> batches = rxnStepModel.getStoicElementListInTransactionOrder();
				StoicModelInterface batch = batches.get(0);
				if (batch != null) {
					batch.setStoicLimiting(true);
					LimitingReagentChangedEvent lrce = new LimitingReagentChangedEvent(this, batch);
					stoicChangesListener.stoicElementLimitingReagentStatusChanged(lrce);
				}
				// update products
				syncProductsWithReactionScheme(false);
				// determine new stoich. this event obj is not really used so passing null for now.
				// We should be really firing a event
				this.stoicChangesListener.stoicAnalyzeReaction(null);
			} catch (ChemUtilInitException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(this,
						"Error initializing chemistry serivce.\nCould not update stoich with rxn selections.", e);
			} catch (ChemUtilAccessException e) {
				CeNErrorHandler.getInstance().logExceptionMsg(this,
						"Error accessing chemistry serivce.\nCould not update stoich with rxn selections.", e);
			} finally {
				CeNJobProgressHandler.getInstance().removeItem("Updating stoichiometry...");
				refreshStoichTable();
			}
		}
	}

	public void syncProductsWithReactionScheme() {
		if (!ReactionStepModelUtils.isReactionSchemeDrawingEmpty(rxnStepModel.getRxnScheme().getNativeSketch())) {
			syncProductsWithReactionScheme(true);
		} else {
			// If Rxn Scheme is empty or user 'Cut' Rxn scheme
			List<ProductBatchModel> batches = rxnStepModel.getAllIntendedProductBatchModelsInThisStep();
			for (ProductBatchModel batch : batches) {
				rxnStepModel.removeProductBatch(batch);
			}
			BatchesList<ProductBatchModel> syncList = pageModel.getNewSyncIntendedBatchesList();
			List<ProductBatchModel> newList = new ArrayList<ProductBatchModel>();
			for (ProductBatchModel batch : syncList.getBatchModels()) {
				newList.add(batch);
			}
			for (ProductBatchModel batch : newList) {
				notebookGUI.getBatchDetailsViewContainer().internalDeleteCompound(batch);
			}
			refresh();
		}
	}

	protected void syncProductsWithReactionScheme(boolean refreshView) {
		MasterController.getGUIComponent().setEnabled(true);
		try {
			CeNJobProgressHandler.getInstance().addItem("Synchronizing Products...");
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (chemDelegate == null)
				initChemDelegate();
			if (chemDelegate == null && rxnStepModel != null) {
				// Give a message to the user that the connection to the
				// Reaction Window
				// is not available at this time.
				ceh.logErrorMsg(this, "Cannot analyze reaction at this time.\nChemistry service is not available.",
						"ChemistryDelegate failed initialization");
			} else {
				ReactionProperties rp = new ReactionProperties();
				rp.ReturnedSketchFormat = (MasterController.getGuiController().getDrawingTool() == Compound.CHEMDRAW) ? ReactionProperties.CHEM_DRAW_SKETCH
						: ReactionProperties.ISIS_DRAW_SKETCH;
				rp.Reaction = rxnStepModel.getRxnScheme().getNativeSketch();
				rp.isolateFragments = false; // Bug 24509 - May want to make
				// this a gui option.
				try {
					rp = chemDelegate.extractReactionComponents(rp);
				} catch (ChemUtilAccessException e) {
					ceh.logExceptionMsg(this, "Could not synchronize stoich products with reaction.", e);
				}
				syncProductsWithReactionScheme(rp, refreshView);
			}
		} finally {
			CeNJobProgressHandler.getInstance().removeItem("Synchronizing Products...");
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Components should have been extracted prior to calling this funciton.
	 * 
	 * @param rp -
	 *            filled in with extraced product components.
	 * @param refreshView -
	 *            should we call refresh?
	 */
	protected void syncProductsWithReactionScheme(ReactionProperties rp, boolean refreshView) {
		List<byte[]> rxnProductsList = null;
		try {

			if (rxnStepModel != null &&
				rp != null) {
				// update products
				rxnProductsList = new ArrayList<byte[]>(rp.Products);
				syncIntendedProducts(rxnProductsList, MasterController.getGuiController().getDrawingTool());
			}
		} catch (Exception e) {
			ceh.logExceptionMsg(MasterController.getGuiComponent(), "Products part of reaction contains not valid molecules", e);
			MasterController.getGUIComponent().setEnabled(true);
		}
		if (refreshView)
			refresh();
		
		// recalculate stoichiometry table to have it up to date
		calculator.recalculateStoich();
		
		if (runChloracnegenTest) {
			// launch the cct checker thread if at least there is one of the
			// reactants or products
			if ((rxnProductsList != null && rxnProductsList.isEmpty() == false) || 
			    (rxnStepModel != null && 
			     rxnStepModel.getReactantBatches() != null && 
			     rxnStepModel.getReactantBatches().isEmpty() == false)) 
			{
				// use worker thread to run this
				calcDelegateProcess = new SwingWorker() {
					public Object construct() {
						// launchChloracnegenCheckerForProdcuts(reactionProductsList);
						launchChloracnegenCheckerForAllBatches(rxnStepModel.getProductBatches(), rxnStepModel.getReactantBatches());
						return null;
					}

					public void finished() {
						log.debug("calcDelegateProcess Swing worker finished");
					}
				};
				calcDelegateProcess.start();
				log.debug("StoictabContainer.syncProductsWithReactionScheme().done chlorac test launch");
			}
		}
		 
	}

	//
	// Bring up reagent search dialog with my list shown
	// 
	protected void stoicToScheme() {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			CeNJobProgressHandler.getInstance().addItem("Creating Rxn Scheme from Stoichiometry...");
			if (chemDelegate == null)
				initChemDelegate();
			if (chemDelegate == null && rxnStepModel.getRxnScheme() != null) {
				// Give a message to the user that the connection to the
				// Reaction Window
				// is not available at this time.
				ceh.logErrorMsg(this, "Cannot synchronize to Reaction window at this time.",
						"ChemistryDelegate failed initialization");
			} else {
				Vector<byte[]> upLoadReactants = new Vector<byte[]>();
				// Reactants need to be added in their proper order
				List<MonomerBatchModel> monomerList = rxnStepModel.getReactantBatches(); 	
				Collections.sort(monomerList, new ComparatorStoicAdditionOrder());
				for (MonomerBatchModel ab : monomerList) {
					if (ab.getBatchType().equals(BatchType.REACTANT)) {
						
						if (ab.getCompound() != null) {
							byte[] sketch = ab.getCompound().getNativeSketch();
							if (!ArrayUtils.isEmpty(sketch))
								upLoadReactants.add(sketch);
							// WriteSketch("C:\\reactants" + ++j + ".skc",
							// ab.getCompound().getNativeSketch());
						}
					}
				}
				Vector<byte[]> upLoadProducts = new Vector<byte[]>();
				List<ProductBatchModel> prodList = rxnStepModel.getProductBatches(); 	
				//Products need to be added in their proper order
				Collections.sort(prodList, new ComparatorStoicAdditionOrder());
				for (ProductBatchModel ab : prodList) {
					if (ab.getBatchType().equals(BatchType.INTENDED_PRODUCT)) {
						if (ab.getCompound() != null && 
						    ab.getCompound().getNativeSketch() != null &&
						    ab.getCompound().getNativeSketch().length > 0) 
						{
							byte[] sketch = ab.getCompound().getNativeSketch();
							if (!ArrayUtils.isEmpty(sketch))
								upLoadProducts.add(sketch);
						}
					}
				}
				if (upLoadReactants.size() > 0 || upLoadProducts.size() > 0) {
					int editor = MasterController.getGuiController().getDrawingTool();
					ReactionProperties rp = new ReactionProperties();
					rp.ReturnedSketchFormat = (editor == Compound.CHEMDRAW) ? ReactionProperties.CHEM_DRAW_SKETCH
							: ReactionProperties.ISIS_DRAW_SKETCH;
					rp.Reactants = upLoadReactants;
					rp.Products = upLoadProducts;
					try {
						ReactionProperties rp2 = chemDelegate.combineReactionComponents(rp);
						// WriteSketch("C:\\reaction.skc", rp2.Reaction);
						if (rp2 != null && rp2.Reaction != null && rp2.Reaction.length > 0) {
							//WriteSketch("C:\\reaction before.skc", rxnStepModel.getRxnScheme().getNativeSketch());
							rxnStepModel.getRxnScheme().setNativeSketch(rp2.Reaction);
							rxnStepModel.getRxnScheme().setNativeSketchFormat(rp.ReturnedSketchFormat);
							//WriteSketch("C:\\reaction after.skc", rxnStepModel.getRxnScheme().getNativeSketch());
							notebookGUI.reactionSchemeModelChanged(new ReactionSchemeModelChangeEvent(this));

						} else {
							ceh.logInformationMsg(this, "Could not create reaction to place in ReactionViewer.");
						}
					} catch (ChemUtilAccessException e) {
						ceh.logExceptionMsg(this, "StoichiometryTabContainer: Could not upload reactants from stoich grid.", e);
					}
				}
			}
		} finally {
			CeNJobProgressHandler.getInstance().removeItem("Creating Rxn Scheme from Stoichiometry...");
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

//	private void WriteSketch(String filename, byte[] sketch) {
//		try {
//			FileOutputStream skcOutStrm = new FileOutputStream(filename);
//			skcOutStrm.write(sketch);
//			skcOutStrm.flush();
//			skcOutStrm.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void syncIntendedProducts(List<byte[]> rxnProducts, int drawingTool) 
		throws ChemUtilInitException, ChemUtilAccessException, InvalidBatchTypeException 
	{
//		ChemistryDelegate chemDelegate = null;
//		if (chemDelegate == null)
			chemDelegate = new ChemistryDelegate();

		// Sync Intended products with compounds
		List<ProductBatchModel> unMatchedBatches = this.rxnStepModel.getAllIntendedProductBatchModelsInThisStep();
		List<Structure> unMatchedStructures = new ArrayList<Structure>();

		// Make sure there are no duplicate matches.
		int addOrder = 0;
		// Get the structure to compare...
		for (byte[] nativeStruct : rxnProducts) {

			// See if there is a matching batch.
			ProductBatchModel matchedBatch = isStructureInBatchList(nativeStruct, unMatchedBatches);

			// Set the addition order to match reaction addition order
			// or add to a list indicating a match is needed.
			if (matchedBatch != null) {
				unMatchedBatches.remove(matchedBatch);
				matchedBatch.setIntendedBatchAdditionOrder(addOrder);
				// WriteISISSketch("C:\\structureMatched_" + addOrder + ".cdx", nativeStruct);
			} else {
				unMatchedStructures.add(new Structure(addOrder, nativeStruct));
				// WriteISISSketch("C:\\structureUnMatched_" + addOrder + ".cdx", nativeStruct);
			}
			addOrder++;
		}

		// Should end up here with a list of matched and unmatched batches and unmatched structures.
		// Reconcile unmatched Structs with unmatched batches or create new ones.
		for (Structure nativeStruct : unMatchedStructures) {
			// Is there an existing batch to match up?
			ListIterator<ProductBatchModel> li = unMatchedBatches.listIterator();
			ProductBatchModel pb = null;
			if (li.hasNext()) {
				pb = li.next();
				unMatchedBatches.remove(pb);

				pb.setMolecularFormula("");
				
				StructureLoadAndConversionUtil.loadSketch(nativeStruct.structure, drawingTool, true, "MDL Molfile", pb.getCompound());

				// Do not overwrite user entry.
				if (pb.getMolecularWeightAmount().isCalculated())
					pb.setMolWgtCalculated(pb.getMolWgtCalculated());
				pb.setIntendedBatchAdditionOrder(nativeStruct.addOrder);
				// convert nativestruc into mol struc
				// byte[] molFile = chemDelegate.convertChemistry(pb.getCompound().getNativeSketch(), "", "MDL Molfile");
				// System.out.println("Intended product mol struc:"+new String(molFile));
				// reset chlorac properties
				pb.setChloracnegenFlag(false);
				pb.setTestedForChloracnegen(false);

				// WriteISISSketch("C:\\structureForced_" + nativeStruct.addOrder + ".cdx", nativeStruct.structure);
			} else {
				// Create a new batch to match with structure.
				pb = ReactionStepModelUtils.createIntendedProduct(nativeStruct.addOrder);
				StructureLoadAndConversionUtil.loadSketch(nativeStruct.structure, drawingTool, true, "MDL Molfile", pb.getCompound());
				this.rxnStepModel.addProductBatch(pb);

				// because MW is the default value and is calculated the call to loadSketch will properly update MW.
				// WriteISISSketch("C:\\structureCreated_" + nativeStruct.addOrder + ".cdx", nativeStruct.structure);
			}
		}

		// Remove unmatched batches from Reaction
		for (Iterator<ProductBatchModel> i = unMatchedBatches.iterator(); i.hasNext();) {
			this.rxnStepModel.removeProductBatch((ProductBatchModel) i.next());
			//USER2 reCalcStoic()
		}
		
		ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
		utils.sortProductBatchListsByBatchNumber();
		//USER2 recalculateProductsBasedOnStoich();
	}

	public ProductBatchModel isStructureInBatchList(byte[] testStructure, List<ProductBatchModel> batches) 
		throws ChemUtilInitException, ChemUtilAccessException 
	{
		ProductBatchModel result = null;
		for (ProductBatchModel tstBatch : batches) {
			// if(i>=rxnProducts.size())break;//if stoich has more than Reaction Scheme
			if (new ChemistryDelegate().areMoleculesEqual(testStructure, tstBatch.getCompound().getNativeSketch())) {
				result = tstBatch;
			}
		}
		return result;
	}
	
	class Structure {
		byte[] structure;
		int addOrder;
		
		Structure(int order, byte[] struct) {
			structure = struct;
			addOrder = order;
		}
	}
    
	public void addSolventsFromList(List<MonomerBatchModel> reagentsToAdd)
	{
		//no impl required
		refresh();
	}
	
	private void enableSaveButton() {
		if (this.rxnStepModel != null && this.pageModel != null) {
			this.pageModel.setModelChanged(true);
			MasterController.getGUIComponent().enableSaveButtons();
			
		}
	}
	
//  this method launches chloracnegen test for all batches in stoic tables
    public void launchChloracnegenCheckerForAllBatches(List<ProductBatchModel> productBatches,
                                                       List<MonomerBatchModel> reagentBatches)
    {
    	log.debug("StoictabContainer.launchChloracnegenCheckerForAllBatches().enter");
    	
    	final String progressStatus = "Running Chloracnegen Prediction ...";
    	CeNJobProgressHandler.getInstance().addItem(progressStatus);
    	
    	try {
    		ArrayList<ChloracnegenBatchStructure> chloracBatchList = new ArrayList<ChloracnegenBatchStructure>();
    		HashMap<String, String> testedStoicBatchesMap = new HashMap<String, String>();
    		//get all products	   
    		for (ProductBatchModel abBatch : productBatches) {
    			//check if this has been tested for chlorac and also overriding flag
    			if(testOnlyUnTestedBatches && abBatch.isTestedForChloracnegen()) {
    				continue;
				}
    			// Get the native structure 
    			byte[] nativeStruct = abBatch.getCompound().getNativeSketch();
    			//convert nativestruc into mol struc
    			byte[] molFile = chemDelegate.convertChemistry(nativeStruct, "", "MDL Molfile");
    			    			
    			//System.out.println("Batch mol struc:"+new String(molFile));
    			//if any of the strucs is null don't add to the list
    			if (nativeStruct == null || molFile == null) {
    				continue; 
				}
    			
    			//check if this is empty molecule.If empty ignore this struc ,donot send it for chlorac test
    			if (chemDelegate.isChemistryEmpty(nativeStruct)) {
    				continue; 
				}
    			
    			ChloracnegenBatchStructure cbatch = new ChloracnegenBatchStructure(nativeStruct, molFile, abBatch.getBatchType());
    			cbatch.setBatchKey(abBatch.getKey());
    			cbatch.setCompoundName("P" + abBatch.getIntendedBatchAdditionOrder());
    			cbatch.setNBKBatchNumber(abBatch.getBatchNumberAsString());
    			cbatch.setCasNumber(abBatch.getCompound().getCASNumber());
    			cbatch.setMolecularFormula(abBatch.getMolecularFormula());
    			chloracBatchList.add(cbatch);
    		}
    		
    		//get all reactant batches
    		for (MonomerBatchModel abBatch : reagentBatches) {
    			//check if this has been tested for chlorac and also overidding flag
    			if(testOnlyUnTestedBatches && abBatch.isTestedForChloracnegen()) {
    				continue;
				}
    			// Get the native structure 
    			byte[] nativeStruct = abBatch.getCompound().getNativeSketch();
    			//convert nativestruc into mol struc
    			byte[] molFile = chemDelegate.convertChemistry(nativeStruct, "", "MDL Molfile");
    			//if any of the strucs is null don't add to the list
    			if(nativeStruct == null || molFile == null) {
    				continue; 
    			}
    			
    			//check if this is empty molecule.If empty ignore this struc ,donot send it for chlorac test
    			if(chemDelegate.isChemistryEmpty(molFile)) {
    				continue;
				}
    			
    			ChloracnegenBatchStructure cbatch = new ChloracnegenBatchStructure(nativeStruct,molFile,abBatch.getBatchType());
    			cbatch.setBatchKey(abBatch.getKey());
    			cbatch.setCompoundID(abBatch.getCompoundId());
    			cbatch.setNBKBatchNumber(abBatch.getBatchNumberAsString());
    			cbatch.setCasNumber(abBatch.getCompound().getCASNumber());
    			cbatch.setMolecularFormula(abBatch.getMolecularFormula());
    			chloracBatchList.add(cbatch);
    		}
    		
    		//run only if there are batches to check and NB Page is editable(cur user's page)
    		if (!(chloracBatchList != null && chloracBatchList.size() > 0 && this.pageModel.isEditable())) {
    			log.debug("StoictabContainer.launchChloracnegenCheckerForAllBatches().NBPage is uneditable or" +
    								"no structures to test");	
    			return;	
    		}
    		
    		for (ChloracnegenBatchStructure cbatch : chloracBatchList) {	
    			testedStoicBatchesMap.put(cbatch.getBatchKey(), cbatch.getChloracnegenType());
    		}
    		
    		//filter the structures that needs to be viewed
    		ArrayList<ChloracnegenBatchStructure> filteredViewableList = new ArrayList<ChloracnegenBatchStructure>();
    		
    		if (!showNonChloracnegens) { 	//if donot show all
     			for (ChloracnegenBatchStructure cbatch : chloracBatchList) {
     	    		com.chemistry.enotebook.chloracnegen.classes.Structure structure = ChloracnegenPredictor.getInstance().checkChloracnegen(new String(cbatch.getMolString()));
    				if (structure.isChloracnegenicStructure()) {
        				filteredViewableList.add(cbatch);
    				}
    			}
    		} else {
    			filteredViewableList = chloracBatchList;
    		}
    		//now alert the user with a JDialog
    		if (filteredViewableList.size() > 0) {
    			alertAboutChloracnegens(filteredViewableList);
    		}
    		//update the Monomer model
    		updateStoichBatchesWithChloracnegenInfo(testedStoicBatchesMap);   
    		//update the Product model
    		updateIntendedProductsWithChloracnegenInfo(testedStoicBatchesMap);    		
    	} catch(Exception e) {   		
    		log.error("Failed to check for Chloracnegen flag for all batches.", e);
    	} finally {
    		CeNJobProgressHandler.getInstance().removeItem(progressStatus);
    		log.debug("StoictabContainer.launchChloracnegenCheckerForAllBatches().finally clause");
    	}
    	
    	log.debug("StoictabContainer.launchChloracnegenCheckerForAllBatches().exit");   
    }
    
    //name value pairs of struc/result or ArrayList of ChloracBatchStructures
    private void alertAboutChloracnegens(ArrayList<ChloracnegenBatchStructure> strucList) {
    	if (strucList != null && strucList.size() > 0) {
    		ChloracnegenResultsViewContainer ui = new ChloracnegenResultsViewContainer(MasterController.getGUIComponent());
    		ui.addStructures(strucList);
    		ui.showGUI();
    	}
    }
    
    //make call to StoicModel to update the chloracnegen flag for the products
    private void updateIntendedProductsWithChloracnegenInfo(HashMap<String, String> testedRxnProductsMap) {
    	if (rxnStepModel!= null && rxnStepModel.getProductBatches() != null ) {
    		this.rxnStepModel.updateIntendedProductsWithChloracnegenInfo(testedRxnProductsMap);
    		refresh();
    	}
    }
      
    // Updates Chloracnegen info for Reagents batches
    private void updateStoichBatchesWithChloracnegenInfo(HashMap<String, String> testedStoicBatchesMap) {
    	if (rxnStepModel!= null && rxnStepModel.getReagentBatches() != null) {
    		this.rxnStepModel.updateStoicBatchesWithChloracnegenInfo(testedStoicBatchesMap);
    		refresh();
    	}
    }
    
    /**
	 * Update 'enabled' status of 'Analyze Rxn' and 'Create Rxn' buttons 
	 */
	public void updateAnalyzeAndCreateButtonsEnabledStatus() {
		mMedChemStoichTableToolBar.updateAnalyzeAndCreateButtonsEnabledStatus();
	}
	
	public void updateArrowButtonsEnabledStatus() {
		mMedChemStoichTableToolBar.updateArrowButtonsEnabledStatus();
	}
	
	public PCeNTableView getMonomersTable() {
		return monomerTableView;
	}
}
