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

import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePaneAdapter;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePaneEvent;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePanes;
import com.chemistry.enotebook.client.gui.page.ParallelNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundManagementMonomerContainer;
import com.chemistry.enotebook.client.gui.page.experiment.ContainerTypeExPTree;
import com.chemistry.enotebook.client.gui.page.experiment.ParallelCeNMonomerReactantMapCollapsiblePane;
import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelStoichCollapsiblePane;
import com.chemistry.enotebook.client.gui.page.reaction.ReactionSchemaContainer;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionStepModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReactionStepJTabPaneFactory {
	private static final int EXPERIMENT_TAB = 0;

	private NotebookPageModel pageModel;
	private ParallelNotebookPageGUI pNBGUI;
	private ContainerTypeTreeModel containerTypeTreeModel;// = new
	
//	private ProcedureKeyTypedHandler procKeyTypeHandler;
//	private static final int STOICH = 1;
//	private static final int MONOMER_MAP = 0;
//	private static final int SUMMARY_STEP = 0;
//	ContainerTypeTreeModel();

	private List<CompoundManagementMonomerContainer> compoundManagementPanels = new ArrayList<CompoundManagementMonomerContainer>(5);
	private PlateCreationHandler mPlateCreationHandler;
	int selectedIndex = 0;

	public ReactionStepJTabPaneFactory(PlateCreationHandler plateCreationHandler, NotebookPageModel pageModel) {
		this.pageModel = pageModel;
		mPlateCreationHandler = plateCreationHandler;
		containerTypeTreeModel = new ContainerTypeTreeModel(pageModel);
//		List rxnSteps =
//		ServiceController.getStorageService().getAllReactionsStepsDetails(pageModel.getNbRef(),
//		NotebookUser.getInstance().getSessionIdentifier());
	}

	public JTabbedPane createIntermediateStepsJTabbedPane(int tabPlacementPolicy, int whichTab) {
		final JTabbedPane rSPane = new JTabbedPane(tabPlacementPolicy);
		int stepSize = pageModel.getReactionSteps().size();

		if (stepSize == 1) {
			String title = "REACTION SUMMARY";
			String title2 = "STEP 1";
			//String title2 = "EXPERIMENT DETAILES";
			if (whichTab == EXPERIMENT_TAB) {// experiment tab
				rSPane.addTab(title, getExpTab(0, true, false));
				rSPane.addTab(title2, getExpTab(0, false, true));
			} else { //if(pageModel.isParallelExperiment() ){// intermediate tab
				rSPane.addTab(title, getTabForStep(0, whichTab));
			}
		} else {
			for (int numberSteps = 0; numberSteps < stepSize; numberSteps++) {
				String title = (numberSteps == 0) ? "SUMMARY REACTION" : "STEP " + numberSteps;
				if (whichTab == EXPERIMENT_TAB) {// experiment tab
					if (numberSteps == 0) {
						rSPane.addTab(title, getExpTab(numberSteps, true, false));
					} else {
						rSPane.addTab(title, getExpTab(numberSteps, false, true));
					}
				} else {// intermediate tab
					rSPane.addTab(title, getTabForStep(numberSteps, whichTab));
				}
			}
		}
		if (rSPane.getTabCount() > 1) {
			rSPane.setSelectedIndex(1);  // vb BZ 14231 Make fist (or only) step selected
			// Below is an attempt to make the background of the selected tab white.  It
			// works but removes the outline.
//			UIManager.put("TabbedPane.selected", new Color(255, 255, 255));		
//			SwingUtilities.updateComponentTreeUI(rSPane);
//			rSPane.setUI(new javax.swing.plaf.DesktopPaneUI() {
//				protected void paintContentBorder(Graphics g,int tabPlacement, int selectedIndex){}
//				}
//			); 
		}

		rSPane.setAutoscrolls(false);
		
		rSPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JScrollPane jScrollPane1 = (JScrollPane)rSPane.getSelectedComponent();
				Dimension size = jScrollPane1.getComponent(0).getPreferredSize();
				size.setSize(size.getWidth(), size.getHeight() + 10);
				for (int i = 0; i < rSPane.getTabCount(); i++) {
					rSPane.getComponent(i).setPreferredSize(size);
				}
				jTabbedPaneReactionSchemaStateChanged(evt);
			}
		});
		return rSPane;
	}

	private JComponent getExpTab(int tabIndex, boolean maporstoich, boolean containerplates) {
		final CollapsiblePanes collapsiblePanes = new CollapsiblePanes();
		final JScrollPane jScrollPane1 = new JScrollPane(collapsiblePanes);
		
		CollapsiblePaneAdapter clpListener = new CollapsiblePaneAdapter() {
			public void paneExpanded(CollapsiblePaneEvent arg0) {
				Dimension size = collapsiblePanes.getPreferredSize();
				size.setSize(size.getWidth(), size.getHeight() + 10);
				jScrollPane1.setPreferredSize(size);
			}
			
			public void paneCollapsed(CollapsiblePaneEvent event) {
				
			}
			
			public void paneCollapsing(CollapsiblePaneEvent event) {
				CollapsiblePane cp = (CollapsiblePane)event.getSource();
				Dimension cpSize = cp.getSize();
				final Dimension size = collapsiblePanes.getSize();
				size.setSize(size.getWidth(), size.getHeight() - cpSize.getHeight());
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						jScrollPane1.setPreferredSize(size);
						}
				});
			}
		};
		
		ParallelStoichCollapsiblePane stoichPane = null;
		// if experiment tab include stoich table else display only reaction
		// schema
		/*
		 * ReactionStepModel summaryStep = ServiceController .getStorageService().getSummaryReaction( pageModel.getNbRef(),
		 * NotebookUser.getInstance().getSessionIdentifier());
		 */
		// JPanel rxnPanel = new JPanel(new BorderLayout());
	
		ReactionStepModel rxnStep = (ReactionStepModel) pageModel.getReactionStep(tabIndex);
		// JPanel reactionPane = new ReactionSchemaViewer(rxnStep).getJPanelReactionSchema();
		//1
		ReactionSchemaContainer mReactionSchemaContainer = new ReactionSchemaContainer(false, pageModel.isEditable());
		mReactionSchemaContainer.setReactionStep(rxnStep);
		// JPanel summaryReactionPane = new ReactionSchemaViewer(summaryStep)
		// .getJPanelReactionSchema();
		// rxnPanes.add(reactionPane);
		
		CollapsiblePane rxnSchemeCollpsiblePane = new CollapsiblePane("Reaction Scheme");
		rxnSchemeCollpsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		rxnSchemeCollpsiblePane.setSteps(1);
		rxnSchemeCollpsiblePane.setStepDelay(0);
		rxnSchemeCollpsiblePane.setContentPane(mReactionSchemaContainer);
		rxnSchemeCollpsiblePane.addCollapsiblePaneListener(clpListener);
		collapsiblePanes.add(rxnSchemeCollpsiblePane);		
		
		//2
		if (maporstoich) {// summary rxn, show monomer list
			ParallelCeNMonomerReactantMapCollapsiblePane monomerReactMapPane = new ParallelCeNMonomerReactantMapCollapsiblePane(rxnStep,pageModel);
			CollapsiblePane monomerReactWorkingPane = monomerReactMapPane.geMonomerReactantPane();
			monomerReactMapPane.geMonomerReactantPane().addCollapsiblePaneListener(clpListener);
			collapsiblePanes.add(monomerReactWorkingPane);
		} // else if (tabIndex == STOICH) {
		else {// STOICH
			stoichPane = new ParallelStoichCollapsiblePane(rxnStep, pNBGUI,tabIndex, pageModel);
			stoichPane.getCollPane().addCollapsiblePaneListener(clpListener);
			collapsiblePanes.add(stoichPane.getCollPane());
		}

		//3
		if (containerplates) {
			// to add material map
			// mPlateCreationHandler.addPlateCreatedEventListener(containerTypeTreeModel);

			ContainerTypeExPTree containerTypeTree = new ContainerTypeExPTree(containerTypeTreeModel, rxnStep, pageModel.isEditable());
			CompoundManagementMonomerContainer compoundManagementMonomer = new CompoundManagementMonomerContainer(mPlateCreationHandler, rxnStep, tabIndex, containerTypeTree, pageModel);
			if (stoichPane != null)
				stoichPane.addStoichGUIListener(compoundManagementMonomer);
			compoundManagementPanels.add(compoundManagementMonomer);
			//String title = (tabIndex == 0) ? "SUMMARY REACTION" : "STEP " + tabIndex;
			//CollapsiblePane mapReactantCollPane = new CollapsiblePane(title + " Materials");
			CollapsiblePane mapReactantCollPane = new CollapsiblePane("Materials Stoichiometry" + " (Step " + tabIndex +")");
			// containerTypeTree = compoundManagementMonomer.getContainerTypeExPTree();
			// containerTypeTree.addPlateCreatedEventListener(
			// parallelCeNController.getParallelCeNBatchInfoControllerInstance());
			mapReactantCollPane.setStyle(CollapsiblePane.TREE_STYLE);
			// mapReactantCollPane.setBackground(BACKGROUND_COLOR);
			mapReactantCollPane.setSteps(1);
			mapReactantCollPane.setStepDelay(0);
			mapReactantCollPane.setContentPane(compoundManagementMonomer);
			mapReactantCollPane.addCollapsiblePaneListener(clpListener);			
			collapsiblePanes.add(mapReactantCollPane, BorderLayout.SOUTH);
		}
		//collapsiblePanes.addExpansion();
		//collapsiblePanes.validate();
		
		//return collapsiblePanes;
		return jScrollPane1;
	}

	private JComponent getTabForStep(int tabIndex, int whichTab) {
		// JPanel reactionPane = new ReactionSchemaViewer()
		// .getJPanelReactionSchema();
		// JPanel mainP = new JPanel(new BorderLayout());
		// if experiment tab include stoich table else display only reaction
		// schema
		// return reactionPane;

		//ReactionSchemaContainer mReactionSchemaContainer = new ReactionSchemaContainer(false, pageModel.isEditable());
		ReactionSchemaContainer mReactionSchemaContainer = new ReactionSchemaContainer(false, false);// All other tabs (except Exp tab) is not editable.
		mReactionSchemaContainer.setReactionStep((ReactionStepModel) pageModel.getReactionStep(tabIndex));
		JScrollPane jScrollPane1 = new JScrollPane(mReactionSchemaContainer);
		jScrollPane1.setPreferredSize(new Dimension(450, 300));
		return jScrollPane1;
		
//		return mReactionSchemaContainer;
		/*
		 * if (whichTab == EXPERIMENT_TAB) {
		 * 
		 * ReactionStepModel summaryStep = ServiceController .getStorageService().getSummaryReaction( pageModel.getNbRef(),
		 * NotebookUser.getInstance().getSessionIdentifier());
		 * 
		 * //ReactionStepModel summaryStep = pageModel.getReactionSteps(); JPanel summaryReactionPane = new
		 * ReactionSchemaViewer(summaryStep) .getJPanelReactionSchema(); ParallelCeNMonomerReactantMapCollapsiblePane
		 * monomerReactMapPane = new ParallelCeNMonomerReactantMapCollapsiblePane( summaryStep); CollapsiblePane
		 * monomerReactWorkingPane = monomerReactMapPane .geMonomerReactantPane(); ParallelStoichCollapsiblePane stoichPane = new
		 * ParallelStoichCollapsiblePane( (ReactionStepModel) pageModel.getReactionSteps().get( tabIndex), pNBGUI); if (tabIndex ==
		 * MONOMER_MAP) { mainP.add(summaryReactionPane, BorderLayout.NORTH); mainP.add(monomerReactWorkingPane.getContentPane(),
		 * BorderLayout.CENTER); } else if (tabIndex == STOICH) { mainP.add(summaryReactionPane, BorderLayout.NORTH);
		 * mainP.add(stoichPane.getCollPane().getContentPane(), BorderLayout.CENTER); } // for procedure_cont stepRProcedureMap =
		 * new HashMap(); procKeyTypeHandler = new ProcedureKeyTypedHandler(stepRProcedureMap); ProcedureContainer Proc_cont = new
		 * ProcedureContainer(tabIndex); CollapsiblePane proc_cont_Coll_Pane = new CollapsiblePane("Step" + tabIndex + " Reaction &
		 * Workup Procedure"); proc_cont_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE); proc_cont_Coll_Pane
		 * .setBackground(ParallelNotebookPageGUI.BACKGROUND_COLOR); proc_cont_Coll_Pane.setContentPane(Proc_cont);
		 * proc_cont_Coll_Pane.setSteps(1); proc_cont_Coll_Pane.setStepDelay(0); // add procedure listener only if not key typed in
		 * it if (tabIndex == 0 && !Proc_cont.getKeyTyped()) { stepRProcedureMap.put(new Integer(tabIndex), Proc_cont);
		 * Proc_cont.addPropertyChangeListener(procKeyTypeHandler); } mainP.add(proc_cont_Coll_Pane, BorderLayout.SOUTH); return
		 * mainP; // } else { //mainP.add(reactionPane, BorderLayout.CENTER); return reactionPane; }
		 */
	}

	protected void jTabbedPaneReactionSchemaStateChanged(ChangeEvent evt) {
		
		JTabbedPane pane = (JTabbedPane) evt.getSource();
		selectedIndex = pane.getSelectedIndex();
		
		// Get current tab
		// containerTypeTree.setSelectedPlateCreateInterface((CompoundManagementMonomerContainer)compoundManagementPanels.get(selectedIndex));
		for (int c = 0; c < pane.getTabCount(); c++) {
			pane.setBackgroundAt(c, Color.LIGHT_GRAY);
		}
		if (selectedIndex >= 0)
			pane.setBackgroundAt(selectedIndex, Color.WHITE);
		else {
			if (pane.getTabCount() > 0)
				pane.setBackgroundAt(0, Color.WHITE);
		}
	}

/*	private class ProcedureKeyTypedHandler implements PropertyChangeListener, FocusListener {
		private HashMap map;
		PropertyChangeEvent evt = null;
		private ProcedureKeyTypedHandler(Map rMap) {
			map = (HashMap) rMap;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			this.evt =evt;
			Integer stepNum = (Integer) evt.getOldValue();
			Integer summaryStep = new Integer(0);
			if (stepNum.equals(summaryStep)) {
				ProcedureContainer cont = null;
				for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
					Integer key = (Integer) itr.next();
					if (!key.equals(summaryStep)) 
					{
						cont = (ProcedureContainer) map.get(key);
						cont.setReadOnly(true);
					}
				} // end for
			} else {
				ProcedureContainer contSummary = (ProcedureContainer) map.get(summaryStep);
				contSummary.setReadOnly(true);
			} // end else						
		}
		public void focusGained(FocusEvent arg0) {
			ELJBean edit = (ELJBean)arg0.getSource();
			JPanel jPanelProcedure = (JPanel)edit.getParent();
			JPanel jPaneMain = (JPanel)jPanelProcedure.getParent();
			ProcedureContainer proc_container = (ProcedureContainer)jPaneMain.getParent();
			if (proc_container.isReadOnly())
			{
				String message = "";
				if (selectedIndex == 0)
					message = "Clear all Step details to enter Summary details.";
				else
					message = "Clear all Summary details to enter Step details.";
				proc_container.transferFocusBackward();
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), message);
			}
		}

		public void focusLost(FocusEvent arg0) 
		{
			boolean deletedAllText = true;
			for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
				Integer key = (Integer) itr.next();
				ProcedureContainer cont = (ProcedureContainer) map.get(key);
				JPanel jPaneMain = (JPanel)cont.getComponent(0);
				JPanel jPanelProcedure = (JPanel)jPaneMain.getComponent(0);
				ELJBean edit = (ELJBean) jPanelProcedure.getComponent(0);
				String text = edit.getDocument();

				if (text.indexOf("<p>") > -1 )
					if (text.indexOf("<p>&nbsp;</p>") == -1)
						deletedAllText = false; 
			}
			if (deletedAllText)
			{
				for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
					Integer key = (Integer) itr.next();
					ProcedureContainer cont = (ProcedureContainer) map.get(key);
					cont.setReadOnly(false);
				}
				return;
			}
		}				
	}*/

	public JTabbedPane createPlateStepsJTabbedPane(int tabPlacementPolicy) {
		JTabbedPane rSPane = new JTabbedPane(tabPlacementPolicy);
		for (int numberSteps = 0; numberSteps < pageModel.getReactionSteps().size(); numberSteps++) {
			// String title = (numberSteps == 0) ? "SUMMARY REACTION" : "STEP " + numberSteps;
			String title = (numberSteps == 0) ? "SUMMARY REACTION" : "STEP " + numberSteps;
			rSPane.addTab(title, new JPanel(new BorderLayout()));
		}
		rSPane.setAutoscrolls(false);
		rSPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				jTabbedPaneReactionSchemaStateChanged(evt);
			}
		});
		return rSPane;
	}

	public List<CompoundManagementMonomerContainer> getCompoundManagementPanels() {
		return compoundManagementPanels;
	}
	
}
