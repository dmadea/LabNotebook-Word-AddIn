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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.ParallelNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundManagementMonomerContainer;
import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.page.reagents.PCeNAddToMyReagentsHelper;
import com.chemistry.enotebook.client.gui.page.reagents.ReagentAdditionListener;
import com.chemistry.enotebook.client.gui.page.reagents.ReagentsFrame;
import com.chemistry.enotebook.client.gui.page.stoichiometry.MSDSSearchLauncher;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.CeN11To12ConversionUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 
 * 
 */
public class ParallelStoichCollapsiblePane implements StoicToolBarButtonClickListener,ReagentAdditionListener{
	
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private CollapsiblePane collPane;
	private PCeNStoich_MonomersTableViewControllerUtility mParallelCeNMonomerStoichTableViewControllerUtility;
	private PCeNStoich_MonomersTableModel mParallelCeNTableModel;
	private PCeNStoich_MonomersTableView mParallelStoichTableView;
	private PCeNStoich_MonomersTablePopupMenuManager mStoichTablePopupMenuManager;
	private ReactionStepModel intermediateStep;
	private ParallelCeNStoichTableToolBar mParallelCeNStoichTableToolBar ;
    private int index ;
	
    private NotebookPageModel pageModel = null;
	
	public ParallelStoichCollapsiblePane(ReactionStepModel intermediateStepe, ParallelNotebookPageGUI pNBookPageGui,int tabIndex, NotebookPageModel pageModel) {
		this.pageModel = pageModel;
		this.intermediateStep = intermediateStepe;
		this.index = tabIndex;
		mStoichTablePopupMenuManager = new PCeNStoich_MonomersTablePopupMenuManager(this);
		mParallelCeNMonomerStoichTableViewControllerUtility = new PCeNStoich_MonomersTableViewControllerUtility(intermediateStep, pageModel);
		//add stoich changes listener.
		try
		{
		mParallelCeNMonomerStoichTableViewControllerUtility.addStoichDataChangesListener(new StoichDataChangesListener(this.intermediateStep,CeNConstants.PAGE_TYPE_PARALLEL));
		}catch(Exception e)
		{
			//initialized with proper listerner.so don't expect exception.
		}
		mParallelCeNTableModel = new PCeNStoich_MonomersTableModel(mParallelCeNMonomerStoichTableViewControllerUtility);
		mParallelCeNMonomerStoichTableViewControllerUtility.setParallelStoicMonTableModel(mParallelCeNTableModel);
		initGUI();
		refresh();
	}
	
	public NotebookPageModel getPageModel() {
		return pageModel;
	}
	
	public void initGUI() {
		try {
			mParallelCeNStoichTableToolBar = new ParallelCeNStoichTableToolBar(this);			
					
			mParallelStoichTableView = new PCeNStoich_MonomersTableView(null, mParallelCeNTableModel,StoicConstants.TABLE_ROW_HEIGHT,mParallelCeNMonomerStoichTableViewControllerUtility,mStoichTablePopupMenuManager, intermediateStep, pageModel);
			((PCeNStoich_MonomersTableViewControllerUtility)mParallelCeNMonomerStoichTableViewControllerUtility).setTableView(mParallelStoichTableView);
			mParallelStoichTableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					boolean isAdjusting = e.getValueIsAdjusting();
					// Find out which indexes are selected.
					int minIndex = lsm.getMinSelectionIndex();
					int maxIndex = lsm.getMaxSelectionIndex();
					if (minIndex >= 0 && !isAdjusting) {
						// get the batch info from the table model and
						// display the details and the structure
						refreshButtons();
					}
				}
			});
			mParallelStoichTableView.setPreferredScrollableViewportSize(new Dimension(StoicConstants.STOICH_EMPTY_WIDTH, StoicConstants.STOICELEMENTS_EMPTY_HEIGHT));
			JScrollPane stoicElementTableScrollPane = new JScrollPane(mParallelStoichTableView);
			
			
			
			stoicElementTableScrollPane.setBackground(mParallelStoichTableView.getBackground());
			// jScrollPaneReactants.setPreferredSize();
			stoicElementTableScrollPane.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					jTableStoicElementMouseReleased(evt);
				}
			});
			
			collPane = new CollapsiblePane("Stoichiometry - Reactants, Reagents, Solvents"+ " (Step "+this.index+")");
			collPane.setStyle(CollapsiblePane.TREE_STYLE);
			collPane.setSteps(1);
			collPane.setStepDelay(0);
			
			
			JPanel toolPanel = new JPanel(new BorderLayout());
			toolPanel.add(mParallelCeNStoichTableToolBar, BorderLayout.CENTER);
			JPanel mPanel = new JPanel(new BorderLayout());
			mPanel.add(toolPanel, BorderLayout.NORTH);
			mPanel.add(stoicElementTableScrollPane, BorderLayout.CENTER);
			collPane.setContentPane(mPanel);
			
		} catch (Exception e) {
			ceh.logExceptionMsg(null, "Error occurred while trying to initialize the stoichiometry tables", e);
		}
	}
	
	private boolean isEditable() {
		return pageModel.isEditable();
	}

	/**
	 * @return Returns the collPane.
	 */
	public CollapsiblePane getCollPane() {
		return collPane;
	}
	
	//This method refreshes the Stoic table.Needs to be called after add/delete etc 
	public void refreshStoichTable() {

		//mParallelCeNMonomerStoichTableViewControllerUtility.updateStoicModelInterfaceList(intermediateStep.getStoicModelList());
		mParallelStoichTableView.updateUI();
	}
	
	public void refresh() {
		int row = mParallelStoichTableView.getSelectedRow();
		
		if (mParallelStoichTableView.getModel() != null &&mParallelStoichTableView.getModel() instanceof MedChemStoic_MonomersTableModel) {
			((MedChemStoic_MonomersTableModel) mParallelStoichTableView.getModel()).refresh();
		}
		
		Dimension rtpd = mParallelStoichTableView.getPreferredScrollableViewportSize();
		
		int rowHeightReag = (mParallelStoichTableView.getRowCount() * mParallelStoichTableView.getRowHeight()); // account for header
		
		if (rowHeightReag < StoicConstants.REAGENTS_EMPTY_HEIGHT) {
			rowHeightReag = StoicConstants.REAGENTS_EMPTY_HEIGHT;
		}
		
		mParallelStoichTableView.setPreferredScrollableViewportSize(new Dimension(rtpd.width, rowHeightReag));
		
		if (row >= 0 && row < mParallelStoichTableView.getRowCount()) {
			mParallelStoichTableView.setRowSelectionInterval(row, row);
		}
	}
	
	public void stoicToolBarButtonClickAction(String buttonName)
	{
		int row = mParallelStoichTableView.getSelectedRow();
		PCeNTableModel rtm = (PCeNTableModel) mParallelStoichTableView.getModel();
		if(buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_UPARROW))
		{
			
			if (row >= 1 && row < mParallelStoichTableView.getRowCount()) {
				if (mParallelStoichTableView.getModel() instanceof PCeNTableModel) {
					intermediateStep.swapBatchPositionOrder(rtm.getStoicElementAt(row), rtm.getStoicElementAt(row - 1));
					row--;
					mParallelStoichTableView.setRowSelectionInterval(row, row);
					enableSaveButton();
				}
			}
			
		} else if(buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_DOWNARROW)) {
			
			if (row >= 0 && row < mParallelStoichTableView.getRowCount() - 1)
				if (mParallelStoichTableView.getModel() instanceof PCeNTableModel) {
					
					intermediateStep.swapBatchPositionOrder(rtm.getStoicElementAt(row), rtm.getStoicElementAt(row + 1));
					row++;
					mParallelStoichTableView.setRowSelectionInterval(row, row);
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
		}else if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_DBSEARCH))
		{
			ReagentsFrame.viewReagentsFrame(ReagentsFrame.SEARCH_REAGENTS_TAB, this, pageModel.getNotebookRefAsString());
		}else  if (buttonName.equals(CeNGUIConstants.STOIC_TOOLBAR_MSDSSEARCH))
		{
			String searchString = null;
			//If a row is choosen
			if(row != -1)
			{
				StoicModelInterface model = rtm.getStoicElementAt(row);	
				//If that is not a List and MonomerBatchmodel
				if(model instanceof MonomerBatchModel)
				{
			     searchString = (String) rtm.getValueAt(row, PCeNStoich_MonomersTableViewControllerUtility.CAS_NUMBER);
			     if (searchString == null || searchString.equals("")) {
			    	 MonomerBatchModel monmodel = (MonomerBatchModel)model;
			    	 searchString = monmodel.getMonomerId();
			    	 
			     }
				}
			}else
			{
				searchString = null;
			}
			
			launchMSDSSearch(searchString);
		}
		refreshStoichTable();
	}
	
	
	
//	 Pass-through for Options menu activation in Reactants Grid
	protected void jTableStoicElementMouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			mParallelStoichTableView.mouseReleased(evt);
		}
	}
	
	//
	// Implements ReagentAdditionListener
	//
	public void addReagentsFromList(List reagentsToAdd) {
		
		if(reagentsToAdd == null || reagentsToAdd.size() == 0) return;
		//convert the models and add to Step
		List models = CeN11To12ConversionUtils.getReagentsListConverted(reagentsToAdd);
		//Set the batchModels to changed status when we first add
		CeN11To12ConversionUtils.setModelChangedForMonBatchModels(models);
		//because these are reagents , we can always add to this list.
		intermediateStep.addMonomerBatchListForStoic(models);
		this.refreshStoichTable();
		//todo fireevent 
		refresh();
		enableSaveButton();
	}
	
	private void launchMSDSSearch(String casNumber)
	{
		String userSiteCode = MasterController.getUser().getSiteCode();
		String preferredLang = "EN";
		if (userSiteCode.equals("NGY")) {
			preferredLang = "JA";
		}
	   MSDSSearchLauncher.launchMSDSSearch(casNumber, preferredLang);
	   return;
	}
	
	//When a solvent is deleted from stoic this method removes the batchkey reference in a Reagent
	//if this solvent is added to a Reagent from Stoic
	public void unlinkSolventFromReactant(String solventBatchKey)
	{
		
		if(solventBatchKey == null)
		{
		return ;	
		}else
		{
			List list = intermediateStep.getStoicElementListInTransactionOrder();
			for(int i=0; i < list.size() ; i ++)
			{
				StoicModelInterface stoicM = (StoicModelInterface)list.get(i);
				String solventBatchKeys = stoicM.getStoicSolventsAdded();
				if(solventBatchKeys == null || solventBatchKeys.equals("")) continue ;
					if(solventBatchKeys.equals(solventBatchKey))
					{
						stoicM.setStoicSolventsAdded("");
						//Aslo remove this solvent batch object as well
						StoicModelInterface solvStoicM = this.intermediateStep.getSolventBatch(solventBatchKey);
						if(solvStoicM != null)
						{
							this.intermediateStep.deleteStoicElement(solvStoicM);
							
						}
						System.out.println("ParallelStoichCollapsiblePane.unlinkSolventFromReactant()--> found a match for solvent key");
					}
			    
			}
			enableSaveButton();
		}
		
		
	}
	
	public void refreshButtons() {
		boolean isEnabled = isEditable();
		mParallelCeNStoichTableToolBar.getMyReagentBtn().setEnabled(isEnabled);
		mParallelCeNStoichTableToolBar.getAddToMyReagentBtn().setEnabled(false);
		mParallelCeNStoichTableToolBar.getSearchDBBtn().setEnabled(true);
		int row = mParallelStoichTableView.getSelectedRow();
		mParallelCeNStoichTableToolBar.getDownBtn().setEnabled(isEnabled && row >= 0 && row < mParallelStoichTableView.getRowCount() - 1);
		mParallelCeNStoichTableToolBar.getUpBtn().setEnabled(isEnabled && row > 0 && row < mParallelStoichTableView.getRowCount());
		
		List list = intermediateStep.getStoicElementListInTransactionOrder();
		if(row <= list.size() && row >= 0)
		{
		StoicModelInterface se = (StoicModelInterface)list.get(row);
		 //If the row is a list type	
		 if(se instanceof BatchesList)
		 {
			   BatchesList listModel = (BatchesList)se; 
			   // If List of size 1 
			    if(listModel.getListSize() == 1)
			    {
			    	mParallelCeNStoichTableToolBar.getAddToMyReagentBtn().setEnabled(true);
			    }else
			    {
			    	mParallelCeNStoichTableToolBar.getAddToMyReagentBtn().setEnabled(false);	
    	
			    }
			    
			   
				
		 }
		 //if the row is a simple batch type
		 else
		 {
			 mParallelCeNStoichTableToolBar.getAddToMyReagentBtn().setEnabled(true);    			 
		 }
		}
		
	}
	
	public void addSolventsFromList(List reagentsToAdd)
	{
		refresh();
	}

	public void addStoichGUIListener(CompoundManagementMonomerContainer monomerContainer) {
		mParallelCeNMonomerStoichTableViewControllerUtility.addStoichGUIListener(monomerContainer);
		
	}
	
	private void enableSaveButton() {
		if (this.intermediateStep != null ) {
			this.intermediateStep.setModelChanged(true);
			MasterController.getGUIComponent().enableSaveButtons();
		}
	}
	
}
