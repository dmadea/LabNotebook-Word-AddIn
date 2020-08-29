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

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * 
 */
public class ParallelCeNStoichTableToolBar extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6619428088118023337L;
	//private JLabel label = new JLabel("Reactants, Reagents, Solvents");
	private JButton myReagentBtn = new JButton("My Reagent List");
	private JButton addToMyReagentBtn = new JButton("Add to My Reagent List");
	private JButton searchDBBtn = new JButton("Search DBs");
	private JButton msdsSearchBtn = new JButton("MSDS Search");
	private JButton upBtn = new JButton(CenIconFactory.getImageIcon(CenIconFactory.General.UP_ARROW));
	private JButton downBtn = new JButton(CenIconFactory.getImageIcon(CenIconFactory.General.DOWN_ARROW));
	private JLabel label2 = new JLabel("Intended Product Label");
	private JTextField intendedProductLabel = new JTextField("      ");
		
	private ParallelStoichCollapsiblePane stoichPane;
	
	private FlowLayout flow = new FlowLayout();

	public ParallelCeNStoichTableToolBar(ParallelStoichCollapsiblePane mStoicPane) {
	
		//this.setLayout(new BorderLayout());
		flow.setAlignment(FlowLayout.LEFT);
		this.setLayout(flow);
		this.stoichPane = mStoicPane;
		init();
	}

	private boolean isEditable() {
		return stoichPane.getPageModel().isEditable();
	}
	
	private void init() {
		//this.add(label);
		
		myReagentBtn.setEnabled(isEditable());
		addToMyReagentBtn.setEnabled(isEditable());
		searchDBBtn.setEnabled(isEditable());
		upBtn.setEnabled(isEditable());
		downBtn.setEnabled(isEditable());
		
		upBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, upBtn);
		this.add(upBtn);	
		downBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, downBtn);
		this.add(downBtn);
		myReagentBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, myReagentBtn);
		this.add(myReagentBtn);
		addToMyReagentBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, addToMyReagentBtn);
		this.add(addToMyReagentBtn);
		searchDBBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, searchDBBtn);
		this.add(searchDBBtn);
		msdsSearchBtn.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, msdsSearchBtn);
		this.add(msdsSearchBtn);
		//Commented for now.This feature will be re-enabled later
		//this.add(label2);	
		//this.add(intendedProductLabel);	
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
		
	} // end init
	//Launch MYReagents
	private void myReagentBtnActionPerformed(){
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_MYREAGENTS);
	}
	
	//Launch MSDS search
	private void msdsSearchBtnActionPerformed(){
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_MSDSSEARCH);
	}
	//Search DB launch
	private void searchDBBtnActionPerformed(){
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_DBSEARCH);
	}
	//Add to MY Reagents
	private void addToMyReagentBtnActionPerformed(){
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_ADDTOMYREAGENTS);
	}
	
	
	private void upBtnActionPerformed()
	{
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_UPARROW);	
	}
	
	private void downBtnActionPerformed()
	{
		stoichPane.stoicToolBarButtonClickAction(CeNGUIConstants.STOIC_TOOLBAR_DOWNARROW);
	}

	public JButton getAddToMyReagentBtn() {
		return addToMyReagentBtn;
	}

	public JButton getDownBtn() {
		return downBtn;
	}

	public JButton getMsdsSearchBtn() {
		return msdsSearchBtn;
	}

	public JButton getSearchDBBtn() {
		return searchDBBtn;
	}

	public JButton getUpBtn() {
		return upBtn;
	}

	public JButton getMyReagentBtn() {
		return myReagentBtn;
	}

	
}
