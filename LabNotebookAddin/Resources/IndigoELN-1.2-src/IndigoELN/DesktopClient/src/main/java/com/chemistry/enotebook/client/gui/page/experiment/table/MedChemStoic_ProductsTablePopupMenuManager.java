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

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class MedChemStoic_ProductsTablePopupMenuManager extends PCeNTableViewPopupMenuManager {

	// Reference table
	private PCeNTableView mTable;
	private MedChemStoic_ProductsTableControllerUtility connector;
	// popup Menus on products table in stoich
	private JPopupMenu jPopupMenuStoicElementOptions;
	private JMenuItem jMenuItemReagentStoicToScheme;
	private JMenuItem jMenuItemReagentAnalyzeReactionScheme;
	private MedChemStoichCollapsiblePane stoicPane;

	public MedChemStoic_ProductsTablePopupMenuManager() {
		init();
	}

	public MedChemStoic_ProductsTablePopupMenuManager(MedChemStoichCollapsiblePane mStoicPane) {
		this.stoicPane = mStoicPane;
		init();
	}

	private void init() {

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
		
		jPopupMenuStoicElementOptions.add(jMenuItemReagentAnalyzeReactionScheme);
		jPopupMenuStoicElementOptions.add(jMenuItemReagentStoicToScheme);
		
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
			connector = (MedChemStoic_ProductsTableControllerUtility) mTable.getConnector();
		}
		mTable.addMouseListener(this);
	}

	public void mouseReleased(MouseEvent evt) {

		if (evt.isPopupTrigger()) {
			preparePopupMenuAndOptions(evt);
			jPopupMenuStoicElementOptions.show(evt.getComponent(), evt.getX(), evt.getY());
		}
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
	
	private StoicModelInterface getStoicElementAt(MouseEvent evt) {
		Point p = evt.getPoint();
		int row = mTable.rowAtPoint(p);
		PCeNTableModel rtm = (PCeNTableModel) mTable.getModel();
		if (row >= 0 && row < mTable.getRowCount()) {
			return rtm.getStoicElementAt(row);
		} else {
			return null;
		}
	}

	public void preparePopupMenuAndOptions(MouseEvent evt) {
		// Determine what should be available.
		boolean isEnabled = false;
		StoicModelInterface se = getStoicElementAt(evt);
		// If the experiment is not editable
		if (!isEditable()) {
			isEnabled = isEditable();

					
			jMenuItemReagentAnalyzeReactionScheme.setEnabled(isEnabled);
			jMenuItemReagentStoicToScheme.setEnabled(isEnabled);
		
		}else
		{
		// If Editable and the click is on the stoich table
		
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