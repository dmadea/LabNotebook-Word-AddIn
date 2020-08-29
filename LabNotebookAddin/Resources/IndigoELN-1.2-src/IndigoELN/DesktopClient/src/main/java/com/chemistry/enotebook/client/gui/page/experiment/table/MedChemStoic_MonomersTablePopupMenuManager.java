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
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewPopupMenuManager;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.StoicModelInterface;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class MedChemStoic_MonomersTablePopupMenuManager extends PCeNTableViewPopupMenuManager {

	// Reference table
	private PCeNTableView mTable;
	private PCeNStoicTableModelConnector connector;
	// popup Menus on monomer table in stoich
	private JPopupMenu jPopupMenuStoicElementOptions;
	private JMenuItem jMenuItemStoicElementMSDSSearch;
	private JMenuItem jMenuItemStoicElementAddFromMyList;
	private JMenuItem jMenuItemStoicElementAddFromSearch;
	private JMenuItem jMenuItemStoicElementAddToMyList;
	private JMenuItem jMenuItemStoicElementAppendRow;
	private JMenuItem jMenuItemStoicElementAddRowAfter;
	private JMenuItem jMenuItemStoicElementAddRowBefore;
	private JMenuItem jMenuItemStoicElementClear;
	private JMenuItem jMenuItemStoicElementDelete;
	private JMenuItem jMenuItemReagentStoicToScheme;
	private JMenuItem jMenuItemReagentAnalyzeReactionScheme;
	private MedChemStoichCollapsiblePane stoicPane;

	public MedChemStoic_MonomersTablePopupMenuManager() {
		init();
	}

	public MedChemStoic_MonomersTablePopupMenuManager(MedChemStoichCollapsiblePane mStoicPane) {
		this.stoicPane = mStoicPane;
		init();
	}

	private void init() {

		jMenuItemStoicElementAddFromMyList = new JMenuItem();
		jMenuItemStoicElementAddFromSearch = new JMenuItem();
		jMenuItemStoicElementAddToMyList = new JMenuItem();
		jMenuItemStoicElementAppendRow = new JMenuItem();
		jMenuItemStoicElementAddRowBefore = new JMenuItem();
		jMenuItemStoicElementAddRowAfter = new JMenuItem();
		jMenuItemStoicElementClear = new JMenuItem();
		jMenuItemStoicElementDelete = new JMenuItem();
		jMenuItemStoicElementAddFromMyList.setText(StoicConstants.MENU_MY_REAGENTS);
		jMenuItemStoicElementAddFromMyList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				editAddFromMyReagents();
			}
		});
		jMenuItemStoicElementAddFromSearch.setText(StoicConstants.MENU_SEARCH_DBS);
		jMenuItemStoicElementAddFromSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				searchAddFromMyReagents();
			}
		});
		jMenuItemStoicElementAddToMyList.setText(StoicConstants.MENU_ADD_TO_MY_REAGENTS);
		jMenuItemStoicElementAddToMyList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// add a new reagent to My Reagent List
				addToMyList();
			}
		});
		jMenuItemStoicElementAppendRow.setText(StoicConstants.MENU_APPEND_ROW);
		jMenuItemStoicElementAppendRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				appendRow();
			}
		});
		jMenuItemStoicElementAddRowBefore.setText(StoicConstants.MENU_ADD_ROW_BEFORE);
		jMenuItemStoicElementAddRowBefore.setEnabled(false);
		jMenuItemStoicElementAddRowBefore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				addRowAbove();
			}
		});
		jMenuItemStoicElementAddRowAfter.setText(StoicConstants.MENU_ADD_ROW_AFTER);
		jMenuItemStoicElementAddRowAfter.setEnabled(false);
		jMenuItemStoicElementAddRowAfter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				addRowBelow();
			}
		});
		jMenuItemStoicElementClear.setText(StoicConstants.MENU_CLEAR_ROW);
		jMenuItemStoicElementClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				clearReagent();
			}
		});
		jMenuItemStoicElementDelete.setText(StoicConstants.MENU_DEL_ROW);
		jMenuItemStoicElementDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// create a new reactant
				deleteSelectedReagent();
			}
		});
		jMenuItemStoicElementMSDSSearch = new JMenuItem(StoicConstants.MENU_MSDS_SEARCH);
		jMenuItemStoicElementMSDSSearch.setEnabled(true);
		jMenuItemStoicElementMSDSSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchMSDS();
			}
		});
		jMenuItemReagentAnalyzeReactionScheme = new JMenuItem(StoicConstants.MENU_ANALYZE_RXN);
		jMenuItemReagentAnalyzeReactionScheme.setEnabled(true);
		jMenuItemReagentAnalyzeReactionScheme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				analyzeReaction();
			}
		});
		jMenuItemReagentStoicToScheme = new JMenuItem(StoicConstants.MENU_CREATE_RXN);
		jMenuItemReagentStoicToScheme.setEnabled(true);
		jMenuItemReagentStoicToScheme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				stoicToScheme();
			}
		});

		jPopupMenuStoicElementOptions = new JPopupMenu();
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementAddFromMyList);
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementAddFromSearch);
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementAddToMyList);
		jPopupMenuStoicElementOptions.addSeparator();
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementAppendRow);
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementAddRowBefore);
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementAddRowAfter);
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementClear);
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementDelete);
		jPopupMenuStoicElementOptions.addSeparator();
		jPopupMenuStoicElementOptions.add(jMenuItemReagentAnalyzeReactionScheme);
		jPopupMenuStoicElementOptions.add(jMenuItemReagentStoicToScheme);
		jPopupMenuStoicElementOptions.addSeparator();
		jPopupMenuStoicElementOptions.add(jMenuItemStoicElementMSDSSearch);
		jPopupMenuStoicElementOptions.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

			}
		});
	}

	public void addMouseListener(JTable table) {
		if (table instanceof PCeNTableView) {
			mTable = (PCeNTableView) table;
			connector = (PCeNStoich_MonomersTableViewControllerUtility) mTable.getConnector();
		}
		mTable.addMouseListener(this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		showPopupMenu(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		showPopupMenu(e);
	}

	private void showPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			preparePopupMenuAndOptions(e);
			jPopupMenuStoicElementOptions.show(e.getComponent(), e.getX(), e.getY());
		}
		stoicPane.updateArrowButtonsEnabledStatus();
	}

	private void editAddFromMyReagents() {
		stoicPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_MYREAGENTS);
	}

	private void searchAddFromMyReagents() {
		stoicPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_DBSEARCH);
	}

	private void addToMyList() {
		stoicPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_ADDTOMYREAGENTS);
	}

	private void appendRow() {
		int cuurentRows = mTable.getRowCount();
		addStoicElement(cuurentRows);
		mTable.updateUI();
		stoicPane.refresh();
	}

	private void addRowAbove() {
		int row = mTable.getSelectedRow();
		if (row >= 0) {
			addStoicElement(mTable.getSelectedRow());
		}
		mTable.updateUI();
		stoicPane.refresh();
	}

	private void addRowBelow() {
		int row = mTable.getSelectedRow();
		if (row != mTable.getRowCount())
			row = row + 1;
		addStoicElement(row);
		mTable.updateUI();
		stoicPane.refresh();
	}

	private void clearReagent() {
		int row = mTable.getSelectedRow();
		PCeNTableModel rtm = (PCeNTableModel) mTable.getModel();
		StoicModelInterface model = rtm.getStoicElementAt(row);
		if (model != null) {
			model.clearStoicData();
		}
		mTable.updateUI();
		MasterController.getGUIComponent().enableSaveButtons();
	}

	private void deleteSelectedReagent() {
		int row = mTable.getSelectedRow();
		PCeNTableModel rtm = (PCeNTableModel) mTable.getModel();
		StoicModelInterface model = rtm.getStoicElementAt(row);
		connector.removeBatch(model);
		mTable.updateUI();
		stoicPane.refresh();
	}

	private void searchMSDS() {
		stoicPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_MSDSSEARCH);
	}

	private void analyzeReaction() {
		stoicPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_ANALYZERXN);
	}

	private void stoicToScheme() {
		stoicPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_CREATERXN);
	}

	private boolean isEditable() {
		NotebookPageGuiInterface mNotebookPageGuiInterface = (NotebookPageGuiInterface) MasterController.getGUIComponent().getActiveDesktopWindow();		
		if (mNotebookPageGuiInterface != null) {		
			NotebookPageModel pageModel = mNotebookPageGuiInterface.getPageModel();	
			if (pageModel != null) {	
				return pageModel.isEditable();
			}	
		}	
		return true;
	}

	protected void addStoicElement(int position) {

		// try {
		if (position >= 0 && position < mTable.getRowCount() && mTable.getRowCount() > 0) {
			connector.addReagentBatchAt(BatchType.REACTANT, position);
		} else if (position == mTable.getRowCount()) {
			connector.addReagentBatchAt(BatchType.REACTANT, position);
		} else { // append
			position = mTable.getRowCount();
			connector.addReagentBatchAfter(BatchType.REACTANT, position);
		}
		// } //catch (InvalidBatchTypeException e) {
		// CeNErrorHandler.getInstance().logExceptionMsg(this, "Could not create batch.", e);
		// }

		if (position >= 0 && position < mTable.getRowCount() && mTable.getRowCount() > 0)
			mTable.setRowSelectionInterval(position, position);
	}

	private StoicModelInterface getStoicElementAt(MouseEvent evt) {
		Point p = evt.getPoint();
		int row = mTable.rowAtPoint(p);
		PCeNTableModel rtm = (PCeNTableModel) mTable.getModel();
		if (row > -1 && row < mTable.getRowCount()) {
			mTable.setRowSelectionInterval(row, row);
			return rtm.getStoicElementAt(row);
		}
		return null;
	}

	public void preparePopupMenuAndOptions(MouseEvent evt) {
		// Determine what should be available.
		boolean isEnabled = false;
		StoicModelInterface se = getStoicElementAt(evt);
		// If the experiment is not editable
		if (!isEditable()) {
			isEnabled = isEditable();

			jMenuItemStoicElementAddFromMyList.setEnabled(isEnabled);
			jMenuItemStoicElementAddFromSearch.setEnabled(isEnabled);
			jMenuItemStoicElementAddToMyList.setEnabled(isEnabled);

			jMenuItemStoicElementAppendRow.setEnabled(isEnabled);
			jMenuItemStoicElementAddRowBefore.setEnabled(isEnabled);
			jMenuItemStoicElementAddRowAfter.setEnabled(isEnabled);
			jMenuItemStoicElementClear.setEnabled(isEnabled);
			jMenuItemStoicElementDelete.setEnabled(isEnabled);
			
			jMenuItemReagentAnalyzeReactionScheme.setEnabled(isEnabled);
			jMenuItemReagentStoicToScheme.setEnabled(isEnabled);
			
			jMenuItemStoicElementMSDSSearch.setEnabled(true);
		}else
		{
		// If Editable and the click is on the stoich table
		if (se != null) {
			isEnabled = true;
			jMenuItemStoicElementAddFromMyList.setEnabled(isEnabled);
			jMenuItemStoicElementAddFromSearch.setEnabled(isEnabled);
			jMenuItemStoicElementAddToMyList.setEnabled(isEnabled);

			jMenuItemStoicElementAppendRow.setEnabled(isEnabled);
			jMenuItemStoicElementAddRowBefore.setEnabled(isEnabled);
			jMenuItemStoicElementAddRowAfter.setEnabled(isEnabled);
			jMenuItemStoicElementClear.setEnabled(isEnabled);
			jMenuItemStoicElementDelete.setEnabled(isEnabled);
			
				
			jMenuItemStoicElementMSDSSearch.setEnabled(true);
			

		}
		// If editable and the click is on the stoic panel
		else if (se == null) {
			jMenuItemStoicElementAddFromMyList.setEnabled(true);
			jMenuItemStoicElementAddFromSearch.setEnabled(true);
			jMenuItemStoicElementAddToMyList.setEnabled(false);

			jMenuItemStoicElementAppendRow.setEnabled(true);
			jMenuItemStoicElementAddRowBefore.setEnabled(false);
			jMenuItemStoicElementAddRowAfter.setEnabled(false);
			jMenuItemStoicElementClear.setEnabled(false);
			jMenuItemStoicElementDelete.setEnabled(false);
			
	
			jMenuItemStoicElementMSDSSearch.setEnabled(true);
		}
		
		if( connector.isReactionSchemeEmpty() && !connector.isStoicMonomerGridEmpty())
		{
			jMenuItemReagentAnalyzeReactionScheme.setEnabled(false);
			jMenuItemReagentStoicToScheme.setEnabled(true);	
			
		}else if(!connector.isReactionSchemeEmpty()&& connector.isStoicMonomerGridEmpty())
		{   
			jMenuItemReagentAnalyzeReactionScheme.setEnabled(true);
			jMenuItemReagentStoicToScheme.setEnabled(false);	
			
		}else if (connector.isReactionSchemeEmpty() && connector.isStoicMonomerGridEmpty())
		{
			jMenuItemReagentAnalyzeReactionScheme.setEnabled(false);
			jMenuItemReagentStoicToScheme.setEnabled(false);	
			
		}else
		{
			jMenuItemReagentAnalyzeReactionScheme.setEnabled(true);
			jMenuItemReagentStoicToScheme.setEnabled(true);	
		}
		}//else editable()
	}

	public PCeNTableModelConnector getTableViewControllerUtility()

	{
		PCeNTableModel tm = (PCeNTableModel) mTable.getModel();
		//return (PCeNStoich_MonomersTableViewControllerUtility) tm.getConnector();
		return tm.getConnector();
	}
}