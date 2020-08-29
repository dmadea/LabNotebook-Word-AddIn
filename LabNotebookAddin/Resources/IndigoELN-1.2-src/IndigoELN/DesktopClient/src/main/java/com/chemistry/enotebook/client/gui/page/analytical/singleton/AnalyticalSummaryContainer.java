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
/*
 * Created on Dec 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.analytical.singleton;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNCollapsiblePane;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalSampleRefContainer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalUtility;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalChangeListener;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalDetailTableView;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalDetailTableViewToolBar;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.PAnalyticalAdvancedLinkDialog;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.CompoundCreatedEventListener;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.utils.CeNJobProgressHandler;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * 
 *
 */
public class AnalyticalSummaryContainer extends javax.swing.JPanel 
	implements CompoundCreatedEventListener, AnalyticalChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2745360017259700919L;
	private static final Log log = LogFactory.getLog(AnalyticalSummaryContainer.class);
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
    //  All Buttons
	private JButton jButtonQuickLinkAll;
	private JButton jButtonAdvancedLink;
	//ToolBars
	private JToolBar jToolBarLinks;
	//Table
	private AnalyticalDetailTableView detailTableView;
	private PAnalyticalUtility analyticalUtil; 
	private NotebookPageModel nbPage;
	private PAnalyticalSampleRefContainer analyticalSampleRefContainer; // = new PAnalyticalSampleRefContainer();

	/**
	 * Default Constructor
	 *
	 */
	public AnalyticalSummaryContainer() {
		initGUI();
	}
	/**
	 * 
	 * @param nbPage
	 */
	public AnalyticalSummaryContainer(NotebookPageModel nbPage) {
		this.nbPage = nbPage;
		analyticalSampleRefContainer = new PAnalyticalSampleRefContainer(nbPage);
		initGUI();
		analyticalUtil = new PAnalyticalUtility(nbPage, this);
	}
	
	/**
	 * All fields are set to blank
	 *
	 */
	public void initGUI(){
		try {
			this.setLayout(new BorderLayout());
			jToolBarLinks = new JToolBar();
			jButtonQuickLinkAll = new JButton();
			jButtonAdvancedLink = new JButton();
			detailTableView = new AnalyticalDetailTableView(nbPage, nbPage.getAllProductBatchModelsInThisPage(), analyticalSampleRefContainer);

			// Add buttons to toolbar
			JPanel buttonPanel = new JPanel();
			jButtonQuickLinkAll.setText("Quick Link All");
			jButtonQuickLinkAll.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("MS Sans Serif",0,11), new java.awt.Color(0,0,0)));
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonQuickLinkAll);
			jButtonQuickLinkAll.setVisible(true);
			jButtonQuickLinkAll.setToolTipText("Search for Analytical matching this Nbk-Exp");
			//jToolBarLinks.add(jButtonQuickLinkAll, "4,0");
			
			jButtonQuickLinkAll.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonQuickLinkAllReleased(evt);
				}
			});
			buttonPanel.add(jButtonQuickLinkAll);
			jButtonAdvancedLink.setText("Advanced Link");
			jButtonAdvancedLink.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("MS Sans Serif",0,11), new java.awt.Color(0,0,0)));
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonAdvancedLink);
			jButtonAdvancedLink.setVisible(true);
			//jToolBarLinks.add(jButtonAdvancedLink, "6,0");
			jButtonAdvancedLink.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonAdvancedLinkAllReleased(evt);
				}
			});
			buttonPanel.add(jButtonAdvancedLink);
			AnalyticalDetailTableViewToolBar detailTableViewToolBar = new AnalyticalDetailTableViewToolBar(detailTableView, buttonPanel);
			/*
			 * AnalyticalSampleRef panel is intialized in here itself, it should be displayed in tandem with 
			 * the Summary Data
			 */
			//AnalyticalSampleRef_cont = new AnalyticalSampleRefContainer();
			CeNCollapsiblePane analyticalSampleRef_Pane = new CeNCollapsiblePane("Analytical Sample References");
			analyticalSampleRef_Pane.setStyle(CollapsiblePane.TREE_STYLE);
			analyticalSampleRef_Pane.setBackground(new Color(122, 194, 174));
			analyticalSampleRef_Pane.setContentPane(analyticalSampleRefContainer);
			//add(AnalyticalSampleRef_Pane, BorderLayout.SOUTH);

			//this.setPreferredSize(new java.awt.Dimension(550, 350));
			JScrollPane scrollTableViewPane = new JScrollPane(detailTableView);
			setBackground(Color.LIGHT_GRAY);
			add(detailTableViewToolBar, BorderLayout.NORTH);
			add(scrollTableViewPane, BorderLayout.CENTER);
			add(analyticalSampleRef_Pane, BorderLayout.SOUTH);
			scrollTableViewPane.setPreferredSize(new java.awt.Dimension(500, 150));	
		} catch (Exception e) {
			ceh.logExceptionMsg(this, "Error occurred while initializing the Batch tables", e);
		}
	}
	/**
	 * 
	 * @param evt
	 */
	protected void jButtonQuickLinkAllReleased(ActionEvent evt) {
		final String progressStatus = "Performing Quick Link All ...";
		CeNJobProgressHandler.getInstance().addItem(progressStatus);
		SwingWorker quikLinkAll = new SwingWorker() {
			boolean success;
			public Object construct(){
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				//jTableAnalyticalViewer.setAnalyticalDataModel(analyticalModel);
				ArrayList sites = new ArrayList();
				sites.add(nbPage.getSiteCode());
				success = analyticalUtil.performQuickLinkAll(nbPage.getNotebookRefWithoutVersion(), sites);
				return null;
			}
			public void finished(){
				CeNJobProgressHandler.getInstance().removeItem(progressStatus);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				if (success)
					detailTableView.reload();
				else 
					JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), 
							"Unable to find any Quick Link results", "Quick Link", JOptionPane.INFORMATION_MESSAGE);;

			}
		};
		quikLinkAll.start();		
	}

	
	/**
	 * 
	 * @param evt
	 */
	protected void jButtonAdvancedLinkAllReleased(ActionEvent evt) {
//		System.out.println();
//		jTableAnalyticalViewer.setAnalyticalDataModel(analyticalModel);
		try {
			PAnalyticalAdvancedLinkDialog aJDialog = new PAnalyticalAdvancedLinkDialog(MasterController.getGUIComponent());
			aJDialog.setAnalyticalModel(analyticalUtil);
//			aJDialog.setTableViewer(jTableAnalyticalViewer);
			aJDialog.setVisible(true);
		} catch (Exception error) {
			log.debug("Advanced Link button All Released button handler failed.", error);
		}
	}
	
	public void refresh(){
		//analyticalModel.refreshAnalyticalData();
		detailTableView.reload();
	}
	public void compoundRemoved(CompoundCreationEvent event) {
		detailTableView.updateCompounds();
	}
	public void compoundUpdated(CompoundCreationEvent event) {
		detailTableView.updateCompounds();
	}
	public void newCompoundCreated(CompoundCreationEvent event) {
		detailTableView.updateCompounds();
	}
	public void updateAnalyses() {
		// supposed to indicate whether or not analysis objects changed.
	}
}
