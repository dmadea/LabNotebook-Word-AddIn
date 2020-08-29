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
package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.controller.GuiController;
import com.chemistry.enotebook.client.gui.page.stoichiometry.CeNSaveCancelHidesDialog;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnBatchIDClient;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnBatchUpdateClient;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.AnalyzeRxnSearchReturnClient;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.BatchLookupDisplayClient;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import info.clearthought.layout.TableLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class PCeNStructureLookupDialog extends CeNSaveCancelHidesDialog implements AnalyzeRxnBatchIDClient,
AnalyzeRxnBatchUpdateClient, BatchLookupDisplayClient {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9057082880222874308L;
	private static final Log log = LogFactory.getLog(PCeNStructureLookupDialog.class);
	private final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private final int RESULTS = 0;
	private final int DB_SELECTION = 1;
	private final String BTN_SEARCHING_STR = "Stop Searching";
	private final String BTN_START_SEARCH_STR = "Search";
	private final String TABLE_INFO_SEARCH_STR = "Searching ...";
	private final int DEFAULT_POOL_SIZE = 5;
	private final String DEFAULT_POOL_NAME = "Analyze Rxn Pool";
	private final String POOL_CLEAN_MSG = "Analyze Rxn: Resetting search pool ...";
	private final String SEARCHING_MSG = "Analyze Rxn: Looking up structures ...";
	private JTabbedPane jTabbedPaneMain;
	private JScrollPane jScrollPaneSearchCriteriaView;
	private JPanel jPanelSearchMain;
	private JPanel structPanel;
	//First tab's table where search results summary is displayed.
	private StructureLookupCriteriaViewer jTableSearchCriteria;
	private CompoundMgmtDataSourceSetupPanel jPanelDBSetup;
	private BatchLookupDisplayPanel jPanelSearchResults;
	private ChemistryPanel structViewer;
	private JButton jButtonRestart;
	// reactantList will be a list of batches that are to be added to the stoich grid
	// Initially reactantList will be the passed with batches to be searched.
	// If no match the entered batch is returned.
	//List of MonomerBatchModel
	private List<MonomerBatchModel> inputReactantList = new ArrayList<MonomerBatchModel>();
	private final List<MonomerBatchModel> newReactantList = new ArrayList<MonomerBatchModel>();
	private final List<MonomerBatchModel> reactantsToSearchList = new ArrayList<MonomerBatchModel>();
	private BatchModel currentBatch;
	private Map<String, List<BatchModel>> hm_searchResults = new HashMap<String, List<BatchModel>>();
	private HashMap<String, BatchModel> returnMap = new HashMap<String, BatchModel>();
	private HashMap<String, byte[]> moleFileMap = new HashMap<String, byte[]>(); // used to prevent
	// having to go back to server to display content.
	private NotebookPageModel pageModel;
	private String[] selectedDbs;
	private String searchingPopupMessage = null;
	// private ThreadPool analyzeThreads = null;
	private boolean searchCancelledFlag = false;
	private JFrame parent;
	private AnalyzeRxnSearchReturnClient callBack;

	/**
	 * 
	 * @param owner -
	 *            frame which owns this dialog
	 * @param stoichModel
	 *            to update with changes
	 * @param batchesInOrderOfRxn -
	 *            list of batches holding structures in order of occurrence in reactionStep
	 * @param currentReactantList -
	 *            list of reactant batches to prevent multiple requests to build/alter same.
	 */
	public PCeNStructureLookupDialog(JFrame owner, NotebookPageModel mpageModel, AnalyzeRxnSearchReturnClient mcallBack,
			List<MonomerBatchModel> orderedBatchesToSearch) {
		super(owner);
		this.parent = owner;
		this.callBack = mcallBack;
		super.jButtonSave.setText("Update and Exit to Stoich");
		this.pageModel = mpageModel;
		this.inputReactantList = orderedBatchesToSearch;
		reactantsToSearchList.addAll(orderedBatchesToSearch);
		if (owner != null) {
			Point p = owner.getLocation();
			setLocation(p.x + (owner.getWidth() - getWidth()) / 2, p.y + (owner.getHeight() - getHeight()) / 2);
		}
		log.debug("lookup dialog initialization");
		initGUI();
		this.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				PCeNStructureLookupDialog.this.setVisible(false);
				PCeNStructureLookupDialog.this.dispose();
			}

			public void internalFrameClosed(InternalFrameEvent e) {
				PCeNStructureLookupDialog.this.removeAll();
				PCeNStructureLookupDialog.this.setVisible(false);
				if (MasterController.getGUIComponent() != null) {
					MasterController.getGUIComponent().setEnabled(true);
					MasterController.getGUIComponent().toFront();
				}
			}
		});
		jButtonSearchPerformed();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
			this.setBorder(null);
			// Create tabs to organize results from setup
			jTabbedPaneMain = new JTabbedPane();
			jTabbedPaneMain.setAutoscrolls(false);
			getContentPane().add(jTabbedPaneMain, BorderLayout.CENTER);
			jTabbedPaneMain.setVisible(true);
			// Setup SearchMain tab
			jPanelSearchMain = new JPanel();
			jPanelSearchMain.setVisible(true);
			jTabbedPaneMain.add(jPanelSearchMain, 0);
			jTabbedPaneMain.setTitleAt(0, "Search Results");
			// TableLayout for SearchMain = two (four with borders) columns
			// three rows
			// SearchCriteria side will have a title, table, title, and
			// Chemistry Viewer.
			double searchResultLayout[][] = {
					{ CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.PREF, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.FILL, CeNGUIUtils.HORIZ_GAP },
					{ CeNGUIUtils.VERT_GAP, 32, CeNGUIUtils.FILL, CeNGUIUtils.VERT_GAP, 25, CeNGUIUtils.PREF, CeNGUIUtils.VERT_GAP } };
			TableLayout layoutSearchMain = new TableLayout(searchResultLayout);
			jPanelSearchMain.setLayout(layoutSearchMain);
			// Setup the Search Criteria
			JPanel jPanelTemp = new JPanel();
			JLabel searchCritLabel = new JLabel();
			CeNGUIUtils.styleComponentText(Font.BOLD, searchCritLabel);
			searchCritLabel.setText("   Search Criteria");
			searchCritLabel.setPreferredSize(new java.awt.Dimension(125, 25));
			jPanelTemp.add(searchCritLabel);
			jButtonRestart = new JButton();
			jButtonRestart.setText(BTN_START_SEARCH_STR);
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonRestart);
			jButtonRestart.setPreferredSize(new java.awt.Dimension(125, 20));
			jButtonRestart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jButtonSearchPerformed();
				}
			});
			jPanelTemp.add(jButtonRestart);
			jPanelSearchMain.add(jPanelTemp, "1,1");
			jScrollPaneSearchCriteriaView = new JScrollPane();
			jTableSearchCriteria = new StructureLookupCriteriaViewer(new JPopupMenu());
			// Table Sizing
			jTableSearchCriteria.setPreferredScrollableViewportSize(new Dimension(320, 220));
			// TableModel
			StructureLookupCriteriaTableModel sctm = new StructureLookupCriteriaTableModel(reactantsToSearchList);
			jTableSearchCriteria.setModel(sctm);
			ListSelectionModel lsm = jTableSearchCriteria.getSelectionModel();
			jTableSearchCriteria.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			lsm.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					// Find out which indexes are selected.
					int minIndex = lsm.getMinSelectionIndex();
					if (minIndex >= 0) {
						// get the batch info from the table model and
						// display the details and the structure
						setSelectedBatch();
						updateDisplay();
					}
				}
			});
			jScrollPaneSearchCriteriaView.add(jTableSearchCriteria);
			jScrollPaneSearchCriteriaView.setViewportView(jTableSearchCriteria);
			jPanelSearchMain.add(jScrollPaneSearchCriteriaView, "1,2");
			// Need to tell scroll pane to check the table it added.
			sctm.fireTableStructureChanged();
			// Setup the structure viewer
			JLabel structLabel = new JLabel("   Structure Searched");
			CeNGUIUtils.styleComponentText(Font.BOLD, structLabel);
			jPanelSearchMain.add(structLabel, "1,4");
			structPanel = new JPanel();
			structViewer = new ChemistryPanel();
			BorderLayout fl = new BorderLayout();
			fl.setHgap(0);
			fl.setVgap(0);
			structPanel.setLayout(fl);
			structPanel.add(structViewer, BorderLayout.CENTER);
			structPanel.setPreferredSize(new Dimension(310, 150));
			jPanelSearchMain.add(structPanel, "1,5");
			// Setup Search Results side
			JLabel matchLabel = new JLabel("   Matches");
			CeNGUIUtils.styleComponentText(Font.BOLD, matchLabel);
			jPanelSearchMain.add(matchLabel, "3,1");
			jPanelSearchResults = new BatchLookupDisplayPanel();
			jPanelSearchResults.addSelectionChangedListener(this);
			jPanelSearchMain.add(jPanelSearchResults, "3,2,3,5");
			// Setup DB Setup Tabbed Pane
			NotebookUser user = MasterController.getUser();
			List<String> defaultDBList = user.getPreferencesList(NotebookUser.PREF_STRUCTURE_DATABASES,
			                                                     NotebookUser.PREF_STRUCTURE_DATABASES_CHILD);
			String s = user.getPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES);
			boolean cenSelected = Boolean.valueOf(s).booleanValue();
			
			defaultDBList = removeReactionsDbsFromDBList(defaultDBList);
			jPanelDBSetup = new CompoundMgmtDataSourceSetupPanel();
			jPanelDBSetup.setDefaultSelectedDBList(defaultDBList, cenSelected);
			jTabbedPaneMain.add(jPanelDBSetup, 1);
			jTabbedPaneMain.setTitleAt(1, "Database Setup");
			
			// Temporary disabled "Database Setup"
			jTabbedPaneMain.setEnabledAt(1, false);
			
			if (defaultDBList == null || defaultDBList.size() < 1) {
				selectedDbs = NotebookUser.DEFAULT_STRUCTURE_DATABASES;
			} else {
				selectedDbs = (String[]) defaultDBList.toArray(new String[defaultDBList.size()]);
			}
			updateSearchingToolTip(defaultDBList);
			jTabbedPaneMain.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					int i = jTabbedPaneMain.getSelectedIndex();
					if (i == 1) {
						jButtonSave.setText("Update Preferences and Return to Search");
						jButtonCancel.setText("Do not Update Preferences and Return to Search");
					} else if (i == 0) {
						jButtonSave.setText("Update and Exit to Stoich");
						jButtonCancel.setText("Cancel");
					}
				}
			});
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}
	
	private List<String> removeReactionsDbsFromDBList(List<String> defaultDBList) {
		if(defaultDBList == null) return null;
		String cenRxnDbName = "CENRXN";
		
		List<String> dbList = new ArrayList<String>();
		for (String row : defaultDBList) {
			if(row.toUpperCase().contains(cenRxnDbName)) {
				String[] sArray = row.split("/");
				StringBuilder sb = new StringBuilder();
				for(String dbName : sArray) {
					if(!dbName.toUpperCase().contains(cenRxnDbName)) {
						sb.append(dbName).append("/");
					}
				}
				row = sb.toString();
			}
			dbList.add(row);
		}
		return dbList;
	} 

	protected void updateSearchingToolTip(List<String> dbsToSearch) {
		searchingPopupMessage = null;
		selectedDbs = new String[dbsToSearch.size()];
		if (dbsToSearch != null && dbsToSearch.size() > 0) {
			searchingPopupMessage = "<html>Searching ...<br>";
			for (int i = 0; i < dbsToSearch.size(); i++) {
				String db = dbsToSearch.get(i);
				if (db.indexOf(" (") > 0) {
					db = db.substring(0, db.indexOf(" ("));
				}
				selectedDbs[i] = db;
				searchingPopupMessage = searchingPopupMessage + selectedDbs[i] + "<br>";
			}
			searchingPopupMessage = searchingPopupMessage + "</html>";
		}
	}

	/**
	 * 
	 */
	protected void hideToolTip() {
		jTableSearchCriteria.setToolTipText(null);
		jButtonRestart.setToolTipText(null);
	}

	/**
	 * 
	 */
	private void showToolTip() {
		jTableSearchCriteria.setToolTipText(searchingPopupMessage);
		jButtonRestart.setToolTipText(searchingPopupMessage);
	}

	/**
	 * 
	 */
	protected void jButtonSearchPerformed() {
		updateSearchingToolTip(jPanelDBSetup.getSelectedDBList());
		showToolTip();
		if (jButtonRestart.getText().equals(BTN_START_SEARCH_STR)) {
			hm_searchResults = Collections.synchronizedMap(new HashMap());
			returnMap = new HashMap<String, BatchModel>();
			if (searchingPopupMessage == null) {
				ceh.showMsgDialog(this.parent, "Input Error", "Please select a Database.");
				return;
			}
			showToolTip();
			initiateReactantSearch(reactantsToSearchList, selectedDbs);
		} else {
			jButtonRestart.setText("Canceling ...");
			updateStatus(null, false);
			cancelSearch();
			jButtonRestart.setText(BTN_START_SEARCH_STR);
		}
		updateDisplay();
	}

	/**
	 * Hides the dialog so the results may be transferred back to the calling function.
	 * 
	 * Calling entity needs to call dispose() for this object
	 * 
	 * @param evt =
	 *            not used.
	 */
	protected void jButtonSaveActionPerformed(ActionEvent evt) {
		if (jTabbedPaneMain.getSelectedIndex() == 1) {
			// User is saving their db list preferences
			jButtonSave.setText("Update and Exit to Stoich");
			jButtonCancel.setText("Cancel");
			jTabbedPaneMain.setSelectedIndex(0);
			ArrayList<String> al_selected = jPanelDBSetup.getSelectedDBList();
			NotebookUser user = MasterController.getUser();
			try {
				user.setPreference(NotebookUser.PREF_STRUCTURE_DATABASES, NotebookUser.PREF_STRUCTURE_DATABASES_CHILD, al_selected);
				if (jPanelDBSetup.getCENSelected()) {
					user.setPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES, "true");
				} else {
					user.setPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES, "false");				
				}
				user.updateUserPrefs();
			} catch (UserPreferenceException e) {
				processException(e);
			}
		} else {
			if (MasterController.getGUIComponent() != null) {
				MasterController.getGUIComponent().setEnabled(true);
			}
			CompoundAndBatchInfoUpdater batchInfoUpdater = new CompoundAndBatchInfoUpdater();
			for (int i = 0; i < reactantsToSearchList.size(); i++) {
				MonomerBatchModel returnBatch = reactantsToSearchList.get(i); // return original if nothing selected
				BatchModel rbSearchResult = returnMap.get(returnBatch.getKey());
				if (rbSearchResult != null) {
					batchInfoUpdater.updateBatch(returnBatch, rbSearchResult, true);
				}
				// Mark this batch as CreatedByNotebook or no details will be
				// saved or be found next time it's loaded
				returnBatch.getCompound().setCreatedByNotebook(true);
				
				//Update the actual MonomerBatchModel with the returnBatch(ReagentBatch)
				MonomerBatchModel stoicMonomerBatchModel = getMatchingMonomerBatch(returnBatch.getKey());
				batchInfoUpdater.updateBatch(stoicMonomerBatchModel, returnBatch, true);
			}
			callBack.doUpdateResults(false);
			pageModel.setModelChanged(true);
			MasterController.getGUIComponent().enableSaveButtons();
			dispose();
		}
	}

	/**
	 * Hides the dialog so the results may be transferred back to the calling function.
	 * 
	 * Calling entity needs to call dispose() for this object
	 * 
	 * @param evt =
	 *            not used.
	 */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		if (jTabbedPaneMain.getSelectedIndex() == 1) {
			jButtonSave.setText("Update and Exit to Stoich");
			jButtonCancel.setText("Cancel");
			
			// Setup default values back
			jPanelDBSetup.getSelectedDBList().clear();
			NotebookUser user = MasterController.getUser();
			List<String> defaultDBList;
			try {
				defaultDBList = user.getPreferencesList(NotebookUser.PREF_STRUCTURE_DATABASES, NotebookUser.PREF_STRUCTURE_DATABASES_CHILD);
				String s = user.getPreference(NotebookUser.PREF_STRUCTURE_DATABASES + "/" + NotebookUser.PREF_CEN_STRUCTURE_DATABASES);
				boolean cenSelected = Boolean.valueOf(s).booleanValue();

				defaultDBList = removeReactionsDbsFromDBList(defaultDBList);
				jPanelDBSetup.setDefaultSelectedDBList(defaultDBList, cenSelected);
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);			
			}
			
			jTabbedPaneMain.setSelectedIndex(0);
		} else {
			super.jButtonCancelActionPerformed(evt);
			cancelSearch();
			callBack.doUpdateResults(true);
			this.dispose();
		}
	}

	public void setSelectedBatch() {
		if (jTableSearchCriteria.getRowCount() > 0 && jTableSearchCriteria.getSelectedRow() >= 0
				&& jTableSearchCriteria.getSelectedRow() < reactantsToSearchList.size()) {
			currentBatch = reactantsToSearchList.get(jTableSearchCriteria.getSelectedRow());
			jPanelSearchResults.setModel(new ReagentLookupTableModel(hm_searchResults.get(currentBatch.getKey())));
			if (currentBatch != null && returnMap.keySet().contains(currentBatch.getKey())
					&& returnMap.get(currentBatch.getKey()) != null) 
			{
				jPanelSearchResults.setSelectedBatch(returnMap.get(currentBatch.getKey()));
			}
		}
	}

	public BatchModel getSearchResultsSelectedBatch() {
		return jPanelSearchResults.getSelectedBatch();
	}

	private void updateDisplay() {
		if (currentBatch != null) {
			try {
				if (currentBatch.getCompound() == null) {
					structViewer.setMolfileData(null);
				} else {
					byte[] molFile = moleFileMap.get(currentBatch.getKey());
					if (molFile == null) {
						ChemistryDelegate chemDel = new ChemistryDelegate();
						molFile = chemDel.convertChemistry(currentBatch.getCompound().getNativeSketch(), "", "MDL Molfile");
						moleFileMap.put(currentBatch.getKey(), molFile);
					}
					if (structViewer != null)
						structViewer.setMolfileData(new String(molFile));
				}
			} catch (Exception e) {
				processException(e);
				clearBatchSearchResults();
			}
		}
		jPanelSearchResults.updateDisplay();
	}

//	private void updateBatchSearchResults(BatchModel ab) {
//		List<BatchModel> al_results = hm_searchResults.get(ab.getKey());
//		ReagentLookupTableModel resultsTableModel = new ReagentLookupTableModel(al_results);
//		jPanelSearchResults.setModel(resultsTableModel);
//		// set selected if a batch was selected previously - held in returnMap
////		BatchModel selected = returnMap.get(ab.getKey());
//		jPanelSearchResults.setSelectedBatch(ab);
//		jButtonRestart.setText(BTN_START_SEARCH_STR);
//		hideToolTip();
//		updateStatus(ab, false);
//	}

	private void clearBatchSearchResults() {
		jPanelSearchResults.setModel(new ReagentLookupTableModel(new ArrayList<BatchModel>()));
	}

//	private void setResultsTableModel(TableModel tm) {
//		if (tm instanceof ReagentLookupTableModel) {
//			jPanelSearchResults.setModel((ReagentLookupTableModel) tm);
//		}
//	}

	public void updateStatus(BatchModel searchedBatch, boolean isSearching, List<Exception> errorsFound) {
		updateStatus(searchedBatch, isSearching);
		if (errorsFound != null && errorsFound.size() > 0) {
			int row = reactantsToSearchList.indexOf(searchedBatch);
			StructureLookupCriteriaTableModel sctm = (StructureLookupCriteriaTableModel) jTableSearchCriteria.getModel();
			sctm.setValueAt("Error(s) Occured", row, StructureLookupCriteriaColumnModel.INFO);
			StringBuffer errorMsg = new StringBuffer();
			for (int i = 0; i < errorsFound.size(); i++) {
				errorMsg.append("\n" + errorsFound.get(i));
			}
			sctm.setErrorMsg(row, errorMsg.toString());
			// only when the search is finished, show/log errors if any
			if (reactantsToSearchList.indexOf(searchedBatch) == reactantsToSearchList.size() - 1) {
				CeNJobProgressHandler.getInstance().removeItem(SEARCHING_MSG);
				ceh.logExceptionMsg(this, 
				                    "Error occurred while performing Reactant Structure Search", 
				                    new Exception(errorMsg.toString()));
			}
		}
	}

	public void updateStatus(BatchModel searchedBatch, boolean isSearching) {
		StructureLookupCriteriaTableModel sctm = (StructureLookupCriteriaTableModel) jTableSearchCriteria.getModel();
		int newCursor = Cursor.DEFAULT_CURSOR;
		if (isSearching) {
			for (int i = 0; i < reactantsToSearchList.size(); i++) {
				sctm.setValueAt("Searching ...", i, StructureLookupCriteriaColumnModel.INFO);
				sctm.setErrorMsg(i, null);
			}
			newCursor = Cursor.WAIT_CURSOR;
			jButtonRestart.setText(BTN_SEARCHING_STR);
			CeNJobProgressHandler.getInstance().addItem(SEARCHING_MSG);
		} else if (searchedBatch != null) {
			int row = reactantsToSearchList.indexOf(searchedBatch);
			List<BatchModel> al = hm_searchResults.get(searchedBatch.getKey());
			if (al != null) {
				sctm.setValueAt(al.size() + " Result(s) found", row, StructureLookupCriteriaColumnModel.INFO);
			} else {
				sctm.setValueAt("No Results found", row, StructureLookupCriteriaColumnModel.INFO);
			}
		} else {
			for (int i = 0; i < reactantsToSearchList.size(); i++) {
				List<BatchModel> al = hm_searchResults.get(reactantsToSearchList.get(i).getKey());
				if (al != null) {
					sctm.setValueAt(al.size() + " Result(s) found", i, StructureLookupCriteriaColumnModel.INFO);
				} else {
					sctm.setValueAt("No Results", i, StructureLookupCriteriaColumnModel.INFO);
				}
			}
		}
		if (!isSearching) {
			boolean searchInProgress = false;
			for (int i = 0; i < reactantsToSearchList.size() && !searchInProgress; i++) {
				searchInProgress = TABLE_INFO_SEARCH_STR.equals(sctm.getValueAt(i, StructureLookupCriteriaColumnModel.INFO));
			}
			if (!searchInProgress) {
				jButtonRestart.setText(BTN_START_SEARCH_STR);
				CeNJobProgressHandler.getInstance().removeItem(SEARCHING_MSG);
				hideToolTip();
				cleanUpPool(searchedBatch == null);
			}
		}
		sctm.fireTableDataChanged();
		// Reset the Row Selection if lost
		if (jTableSearchCriteria.getSelectedRow() == -1) {
			int i = reactantsToSearchList.indexOf(currentBatch);
			if (i != -1 && i < jTableSearchCriteria.getRowCount()) {
				jTableSearchCriteria.setRowSelectionInterval(i, i);
			}
		}
		setCursor(Cursor.getPredefinedCursor(newCursor));
	}

	/**
	 * Implementation of BatchLookupDisplayClient
	 * 
	 * @param selectedBatch
	 */
	public void syncBatchSelected(BatchModel selectedBatch) {
		if (currentBatch != null) {
			List<BatchModel> results = hm_searchResults.get(currentBatch.getKey());
			if (results.contains(selectedBatch))
				returnMap.put(currentBatch.getKey(), selectedBatch);
		}
	}

	private void processException(Exception e) {
		ceh.logExceptionMsg(this, "Error occurred while performing Reactant Structure Search", e);
	}

	/**
	 * Call only from within the AWTEvent thread. No concurrency protection here.
	 */
	public void syncIDListFromSearch(BatchModel batchMatched, List<String> lBatchIDs, List<Exception> errorsFound) {
		log.debug("syncIDListFromSearch");
		if (batchMatched != null && hm_searchResults != null) {
			if (errorsFound != null && errorsFound.size() > 0)
				updateStatus(batchMatched, lBatchIDs.size() > 0, errorsFound);
			else if (lBatchIDs.size() == 0)
				updateStatus(batchMatched, false);
			if (lBatchIDs.size() > 0) {
				try {
					// Spin off Loader to load batches from ids given:
					GuiController.threadPool.execute(new AnalyzeRxnBatchLoader(this,
					                                                           GuiController.threadPool, 
					                                                           batchMatched,
					                                                           lBatchIDs));
				} catch (InterruptedException e) {
					log.warn("Search interrupted.");
				}
			}
		}
	}

	/**
	 * Call only from within the AWTEvent thread
	 */
	public void syncBatchesFromSearch(BatchModel batchMatched, List<BatchModel> searchReturn) {
		// make sure cancel wasn't pressed prior to update.
		if (batchMatched != null && hm_searchResults != null) {
			// locate batch matched
			if (searchReturn != null) {
				// Sort batches by whether or not they come from CeN
				Collections.sort(searchReturn);
				hm_searchResults.put(batchMatched.getKey(), searchReturn);
			}
			updateStatus(batchMatched, false);
			updateDisplay();
		}
	}

	/**
	 * Spins off a swing worker to find IDs associated with each batch structure supplied
	 * 
	 * @param l_reactantBatchesToFind -
	 *            list of ReagentBatch object with compound structures to search
	 * @param dbList -
	 *            list of dbs to search (CLS only)
	 */
	public void initiateReactantSearch(List<MonomerBatchModel> l_reactantBatchesToFind, String[] dbList) {
		searchCancelledFlag = false;
		try {
			log.debug("initiateReactantSearch");
			updateStatus(null, true);
			// perform a CeN & CLS lookup for each structure returning a
			// list of ids per structure to be loaded.
			for (Iterator<MonomerBatchModel> i = l_reactantBatchesToFind.iterator(); i.hasNext();) {
				GuiController.threadPool.execute(new AnalyzeRxnBatchIDLoader(this, i.next(), dbList));
			}
		} catch (Exception e) {
			processException(e);
		}
	}

	public void dispose() {
		structViewer.setVisible(false);
		structPanel.remove((Component) structViewer);
		structViewer = null;
		structPanel.removeAll();
		jPanelSearchMain.remove((Component) structPanel);
		structPanel = null;
		//this.stoichModel = new StoichiometryModel(null);
		ChangeListener[] cla = jTabbedPaneMain.getChangeListeners();
		for (int i = 0; i < cla.length; i++) {
			jTabbedPaneMain.removeChangeListener(cla[i]);
		}
		PCeNStructureLookupDialog.this.setVisible(false);
		if (parent != null) {
			parent.setVisible(false);
			parent.dispose();
			parent = null;
		}
		inputReactantList.clear();
		reactantsToSearchList.clear();
		newReactantList.clear();
		// Remove objects in arrays and maps
//		hm_searchResults = new HashMap();
		hm_searchResults = null;
//		returnMap = new HashMap();
		returnMap = null;
		CeNJobProgressHandler.getInstance().removeItem(SEARCHING_MSG);
		cleanUpPool(true);
		super.dispose();
	}

	private void cleanUpPool(boolean threadDestroyFlag) {
		// if (analyzeThreads != null) {
		// if (threadDestroyFlag) {
		// SwingWorker sw = new SwingWorker() {
		// public Object construct() {
		// try {
		// if (analyzeThreads.groupExists(analyzeThreads.getThreadGroupName()))
		// analyzeThreads.destroyThreadGroup(analyzeThreads.getThreadGroupName());
		// } catch (ThreadPoolException e) {
		// processException(e);
		// } finally {
		// analyzeThreads = null;
		// }
		// return null;
		// }
		// };
		// sw.start();
		// } else {
		// try {
		// CeNJobProgressHandler.getInstance().addItem(POOL_CLEAN_MSG);
		// if (analyzeThreads.groupExists(analyzeThreads.getThreadGroupName()))
		// analyzeThreads.destroyThreadGroup(analyzeThreads.getThreadGroupName());
		// } catch (ThreadPoolException e) {
		// processException(e);
		// } finally {
		// CeNJobProgressHandler.getInstance().removeItem(POOL_CLEAN_MSG);
		// analyzeThreads = null;
		// }
		// }
		// }
	}

	private void cancelSearch() {
		searchCancelledFlag = true;
		cleanUpPool(true);
	}
	
	
	private MonomerBatchModel getMatchingMonomerBatch(String key)
	{
		if(this.inputReactantList != null && this.inputReactantList.size() > 0)
		{
			int size = this.inputReactantList.size();
			for(int i = 0 ; i < size ; i ++)
			{
				MonomerBatchModel model = 	(MonomerBatchModel)inputReactantList.get(i);
				if(model.getKey().equals(key))
				{
					return model;
				}
			}
		}
		return null;
	}

}