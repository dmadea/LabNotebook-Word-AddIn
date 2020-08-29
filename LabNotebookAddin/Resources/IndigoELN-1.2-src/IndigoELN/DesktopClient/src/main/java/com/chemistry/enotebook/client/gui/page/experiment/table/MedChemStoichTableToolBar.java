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


import com.chemistry.enotebook.client.gui.CeNGUIConstants;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MedChemStoichTableToolBar extends JToolBar{
	
	private static final long serialVersionUID = -4528723276744185220L;
	
	private JLabel label = new JLabel("Reactants, Reagents, Solvents ");
	private JButton myReagentBtn = new JButton(StoicConstants.MENU_MY_REAGENTS);
	private JButton addToMyReagentBtn = new JButton(StoicConstants.MENU_ADD_TO_MY_REAGENTS);
	private JButton searchDBBtn = new JButton(StoicConstants.MENU_SEARCH_DBS);
	private JButton msdsSearchBtn = new JButton(StoicConstants.MENU_MSDS_SEARCH);
	private JButton upBtn = new JButton(CenIconFactory.getImageIcon(CenIconFactory.General.UP_ARROW));
	private JButton downBtn = new JButton(CenIconFactory.getImageIcon(CenIconFactory.General.DOWN_ARROW));
	private JButton createRxnBtn = new JButton(StoicConstants.MENU_CREATE_RXN);
	private JButton analyzeRxnBtn = new JButton(StoicConstants.MENU_ANALYZE_RXN);
		
	private MedChemStoichCollapsiblePane stoichPane = null;
	private PCeNStoicTableModelConnector connector = null;
	
	public MedChemStoichTableToolBar(MedChemStoichCollapsiblePane mStoicPane, PCeNStoicTableModelConnector connector) {
		this.stoichPane = mStoicPane;
		this.connector = connector;
		init();
	}

	private void init() {
		
		myReagentBtn.setEnabled(isEditable());
		addToMyReagentBtn.setEnabled(isEditable());
		searchDBBtn.setEnabled(isEditable());
		upBtn.setEnabled(isEditable());
		downBtn.setEnabled(isEditable());
		createRxnBtn.setEnabled(isEditable());
		analyzeRxnBtn.setEnabled(isEditable());
		
		double toolBarSize[][] = {
				{ 180, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP,
						CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN,
						CeNGUIUtils.HORIZ_GAP}, { StoicConstants.TITLE_HEIGHT, 2, StoicConstants.TITLE_HEIGHT, 2 } };
		TableLayout jToolBarLayout = new TableLayout(toolBarSize);
		this.setLayout(jToolBarLayout);
		this.setBorderPainted(false);
		this.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
		CeNGUIUtils.styleComponentText(Font.BOLD, label);
		this.add(label, "0,0");
		upBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, upBtn);
		this.add(upBtn,"2,0");	
		downBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, downBtn);
		this.add(downBtn, "4,0");
		// Function buttons Row 1
		myReagentBtn.setBorder(new TitledBorder(null, "", 
		                                        TitledBorder.LEADING, TitledBorder.TOP, 
		                                        new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, myReagentBtn);
		this.add(myReagentBtn, "6,0");
		searchDBBtn.setBorder(new TitledBorder(null, "",
		                                       TitledBorder.LEADING, TitledBorder.TOP,
		                                       new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, searchDBBtn);
		this.add(searchDBBtn, "8,0");
		analyzeRxnBtn.setBorder(new TitledBorder(null, "", 
		                                         TitledBorder.LEADING, TitledBorder.TOP, 
		                                         new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, analyzeRxnBtn);
		this.add(analyzeRxnBtn, "10,0");	
		createRxnBtn.setBorder(new TitledBorder(null, "", 
		                                        TitledBorder.LEADING, TitledBorder.TOP, 
		                                        new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, createRxnBtn);
		this.add(createRxnBtn, "10,2");	
//		this.add(createRxnBtn, "12,0");	

		// Function buttons Row 2
		addToMyReagentBtn.setBorder(new TitledBorder(null, "", 
		                                             TitledBorder.LEADING, TitledBorder.TOP, 
		                                             new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, addToMyReagentBtn);
//		this.add(addToMyReagentBtn, "16,0");
		this.add(addToMyReagentBtn, "6,2");
		msdsSearchBtn.setBorder(new TitledBorder(null, "", 
		                                         TitledBorder.LEADING, TitledBorder.TOP, 
		                                         new java.awt.Font("MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, msdsSearchBtn);
		this.add(msdsSearchBtn, "8,2");

		// Adding Handlers
		myReagentBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				myReagentBtnActionPerformed();
			}
		});
		msdsSearchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				msdsSearchBtnActionPerformed();
			}
		});
		searchDBBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchDBBtnActionPerformed();
			}
		});
		addToMyReagentBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addToMyReagentBtnActionPerformed();
			}
		});	
		
		upBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				upBtnActionPerformed();
			}
		});	
		
		downBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downBtnActionPerformed();
			}
		});	
		
		createRxnBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				createRxnBtnActionPerformed();
			}
		});	
		
		analyzeRxnBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				analyzeRxnBtnActionPerformed();
			}
		});	
		
		setFloatable(false);
		
		updateAnalyzeAndCreateButtonsEnabledStatus();		
		updateArrowButtonsEnabledStatus();
	} // end init
	
	/**
	 * Update 'enabled' status of 'Analyze Rxn' and 'Create Rxn' buttons 
	 */
	public void updateAnalyzeAndCreateButtonsEnabledStatus() {
		if (connector != null && connector.getPageModel() != null && connector.getPageModel().isEditable()) {
			if (connector.isReactionSchemeEmpty() && !connector.isStoicMonomerGridEmpty()) {
				analyzeRxnBtn.setEnabled(false);
				createRxnBtn.setEnabled(true);
			} else if (!connector.isReactionSchemeEmpty() && connector.isStoicMonomerGridEmpty()) {
				analyzeRxnBtn.setEnabled(true);
				createRxnBtn.setEnabled(false);
			} else if (connector.isReactionSchemeEmpty() && connector.isStoicMonomerGridEmpty()) {
				analyzeRxnBtn.setEnabled(false);
				createRxnBtn.setEnabled(false);
			} else {
				analyzeRxnBtn.setEnabled(true);
				createRxnBtn.setEnabled(true);
			}
		}
	}

	/**
	 * Update 'enabled' status of 'Up' and 'Down' buttons
	 */
	public void updateArrowButtonsEnabledStatus() {
		if (connector != null && connector.getPageModel() != null && connector.getPageModel().isEditable()) {
			if (!connector.isStoicMonomerGridEmpty()) {
				PCeNTableView monomersTable = stoichPane.getMonomersTable();
				if (monomersTable != null) {
					int selectedRow = monomersTable.getSelectedRow();
					int rowCount = monomersTable.getRowCount();
					if (selectedRow > -1 && rowCount > 1) {
						if (selectedRow == 0) {
							upBtn.setEnabled(false);
							downBtn.setEnabled(true);
						} else if (selectedRow == rowCount - 1) {
							upBtn.setEnabled(true);
							downBtn.setEnabled(false);
						} else if (selectedRow > 0 && selectedRow < rowCount - 1){
							upBtn.setEnabled(true);
							downBtn.setEnabled(true);
						} else {
							upBtn.setEnabled(false);
							downBtn.setEnabled(false);
						}
					} else {
						upBtn.setEnabled(false);
						downBtn.setEnabled(false);
					}
				}
			} else {
				upBtn.setEnabled(false);
				downBtn.setEnabled(false);
			}
		}
	}
	
	//Launch MYReagents
	private void myReagentBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_MYREAGENTS);
	}
	
	//Launch MSDS search
	private void msdsSearchBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_MSDSSEARCH);
	}
	
	//Search DB launch
	private void searchDBBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_DBSEARCH);
	}
	
	//Add to MY Reagents
	private void addToMyReagentBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_ADDTOMYREAGENTS);
	}
	
	
	private void upBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_UPARROW);	
	}
	
	private void downBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_DOWNARROW);
	}
	
	private void createRxnBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_CREATERXN);
	}
	
	private void analyzeRxnBtnActionPerformed() {
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_ANALYZERXN);
	}
	
	private boolean isEditable() {
		return stoichPane.getPageModel().isEditable();
	}
}
