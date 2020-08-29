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
package com.chemistry.enotebook.client.gui.page;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.CeNInternalFrame;
import com.chemistry.enotebook.client.gui.GlassPane;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePanes;
import com.chemistry.enotebook.client.gui.common.ekit.ProcedureContainer;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.attachements.AttachmentsContainer;
import com.chemistry.enotebook.client.gui.page.batch.BatchContainer;
import com.chemistry.enotebook.client.gui.page.batch.BatchEditPanel;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationHandler;
import com.chemistry.enotebook.client.gui.page.conception.ConceptionCompoundInfoContainer;
import com.chemistry.enotebook.client.gui.page.conception.ConceptionDetailsContainer;
import com.chemistry.enotebook.client.gui.page.reaction.ReactionSchemaContainer;
import com.chemistry.enotebook.client.gui.page.reaction.ReactionSchemeModelChangeEvent;
import com.chemistry.enotebook.client.gui.page.regis_submis.SampleSubSumContainer;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ReactionSchemeModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo is being used commercially
 * (ie, by a for-profit company or business) then you should purchase a license -
 * please visit www.cloudgarden.com for details.
 */
public class ConceptionNotebookPageGUI extends CeNInternalFrame implements NotebookPageGuiInterface {
	
	private static final long serialVersionUID = -8743376460555686605L;

	public static final Log log = LogFactory.getLog(ConceptionNotebookPageGUI.class);

	public static final Color BACKGROUND_COLOR = new Color(122, 194, 174);
	public static final String CONCEPTION_TAB = "Concept";
	public static final String PREFERRED_COMPOUND_TAB = "Preferred Compounds";
	public static final String ATTACHMENTS_TAB = "Attachments";
	// private static final String BATCHSUMMARY_PANE = "Batches";
	private static final String BATCHDETAILS_PANE = "Details";
	private static final String SUMMARY_PANE = "Summary";
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();

	private CollapsiblePanes panesConceptionExp = new CollapsiblePanes();
	private CollapsiblePanes panesPreferredSpecies = new CollapsiblePanes();
	private CollapsiblePanes panesAttach = new CollapsiblePanes();
	private JTabbedPane mainTabbedPane = new JTabbedPane();
	private JScrollPane jScrollPaneConception = new JScrollPane();
	private JScrollPane jScrollPanePreferredSpecies = new JScrollPane();
	
	// contents of experiment panes
	private ConceptionDetailsContainer conceptionDetailContainer;
	private ReactionSchemaContainer reactionSchemaContainer_exp;
	private ReactionSchemaContainer reactionSchemaContainer_bat;
	private ProcedureContainer Proc_cont;

	// contents of Product batch panes
	//private ReactionSchemaContainer ReactionSchemaProdBatch_cont;
	//private PreferredCompoundContainer preferredCompoundContainer;
	//private SigletonBatchInfoContainer conceptBatchInfoContainer;
	private ConceptionCompoundInfoContainer conceptionCompoundInfoContainer;
	private CompoundCreationHandler mCompoundCreationHandler = new CompoundCreationHandler();
	private SampleSubSumContainer sampleSubSum_cont;
	
	// contents of attachment pane
	private AttachmentsContainer attachements_cont;
	
	// experiment tab panes
	private CollapsiblePane reactionDetails_Coll_Pane;
	private CollapsiblePane reactionSchemaRegistration_Pane;
	private CollapsiblePane proc_cont_Coll_Pane;
	
	// Product Batch Tab panes
	// private CollapsiblePane reactionSchemaProdBatch_Pane;
	private CollapsiblePane reactionSchemaBatch_Pane;
	private CollapsiblePane reactionProdBatchSum_Pane;
	private CollapsiblePane reactionProdBatchDetail_Pane;
	private boolean closing;
	private boolean pageEditable;
	private NotebookPageModel pageModel;
	private BatchEditPanel batchEditPanel = null;
	
	private List<NotebookPageChangedListener> pageChangedListenerList = new Vector<NotebookPageChangedListener>();
	
	private Map<String, BatchContainer> batchContainerCache = new HashMap<String, BatchContainer>();

	public ConceptionNotebookPageGUI(NotebookPageModel mNotebookPageModel) {
		this.pageModel = mNotebookPageModel;
		log.debug("Before initGUI() pageModel modified:"+this.pageModel.isModelChanged());
		boolean pageModelChanged = this.pageModel.isModelChanged();

		batchEditPanel = new BatchEditPanel(pageModel, mCompoundCreationHandler, this);// set isParallel flag
		initGUI();
		log.debug("After initGUI() pageModel modified:"+this.pageModel.isModelChanged());
		this.pageModel.setModelChanged(pageModelChanged);
		log.debug("After restoring original flag pageModel modified:"+this.pageModel.isModelChanged());
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will
	 * disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();

			String title = pageModel.getNotebookRefWithoutVersion() + "- Conception Record";
			if (pageModel.getVersion() > 1 || pageModel.getLatestVersion() == "N") title += " v" + pageModel.getVersion();
			this.setTitle(title);
			this.setResizable(true);
			this.setSelected(true);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setClosable(true);
			this.setIconifiable(true);
			this.setMaximizable(true);
			this.setVisible(true);
			this.setPreferredSize(new Dimension(681, 318));
			// this.setBounds(new Rectangle(0, 0, 681, 318));
			mainTabbedPane.setAutoscrolls(false);
			this.getContentPane().add(mainTabbedPane);
			mainTabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					jTabbedPaneMainStateChanged(evt);
					mainTabbedPane.invalidate();
				}
			});
			jScrollPaneConception.setVisible(false);
			jScrollPaneConception.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			jScrollPaneConception.add(panesConceptionExp);
			jScrollPaneConception.setViewportView(panesConceptionExp);
			mainTabbedPane.add(jScrollPaneConception);
			mainTabbedPane.setTitleAt(0, ConceptionNotebookPageGUI.CONCEPTION_TAB);
			panesConceptionExp.setBorder(null);
			jScrollPanePreferredSpecies.add(panesPreferredSpecies);
			jScrollPanePreferredSpecies.setViewportView(panesPreferredSpecies);
			mainTabbedPane.add(jScrollPanePreferredSpecies);
			mainTabbedPane.setTitleAt(1, ConceptionNotebookPageGUI.PREFERRED_COMPOUND_TAB);
			panesPreferredSpecies.setBorder(null);
			//panesPreferredSpecies.setPreferredSize(new Dimension(520, 225));

			panesAttach.setPreferredSize(new Dimension(520, 251));
			panesAttach.setBorder(null);
			mainTabbedPane.add(panesAttach);
			mainTabbedPane.setTitleAt(2, ConceptionNotebookPageGUI.ATTACHMENTS_TAB);
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		super.postInitGUI();
		
		setFrameIcon(CenIconFactory.getImageIcon(CenIconFactory.General.APPLICATION_ICON));
		// setup scrollbars with larger increment
		jScrollPaneConception.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPanePreferredSpecies.getVerticalScrollBar().setUnitIncrement(50);
		// for ReactionDetails_cont
		// setup and show collapse pane for reaction_details
		conceptionDetailContainer = new ConceptionDetailsContainer(pageModel);
		reactionDetails_Coll_Pane = new CollapsiblePane("Concept Details");
		reactionDetails_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionDetails_Coll_Pane.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
		reactionDetails_Coll_Pane.setSteps(1);
		reactionDetails_Coll_Pane.setStepDelay(0);
		reactionDetails_Coll_Pane.setContentPane(conceptionDetailContainer);
		panesConceptionExp.add(reactionDetails_Coll_Pane);
		// setup and show collapse pane for reaction schema on analytical
		reactionSchemaContainer_exp = new ReactionSchemaContainer(false, pageModel.isEditable(), this);
		reactionSchemaContainer_exp.setReactionStep(pageModel.getSummaryReactionStep());
		// reactionSchemaRegistration_cont = new ReactionSchemaContainer(false, true);
		reactionSchemaRegistration_Pane = new CollapsiblePane("Reaction Concept / Generic Target");
		reactionSchemaRegistration_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaRegistration_Pane.setBackground(BACKGROUND_COLOR);
		reactionSchemaRegistration_Pane.setSteps(1);
		reactionSchemaRegistration_Pane.setStepDelay(0);
		reactionSchemaRegistration_Pane.setContentPane(reactionSchemaContainer_exp);
		panesConceptionExp.add(reactionSchemaRegistration_Pane);
		// for procedure_cont
		Proc_cont = new ProcedureContainer(pageModel, mCompoundCreationHandler,false);
		proc_cont_Coll_Pane = new CollapsiblePane("Utility, Definitions, Invention Notes & Synthetic Routes");
		proc_cont_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		proc_cont_Coll_Pane.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
		proc_cont_Coll_Pane.setContentPane(Proc_cont);
		proc_cont_Coll_Pane.setSteps(1);
		proc_cont_Coll_Pane.setStepDelay(0);
		panesConceptionExp.add(proc_cont_Coll_Pane);
		panesConceptionExp.addExpansion();
		// Product batches for Reaction schema on product batches
		//preferredCompoundContainer = new PreferredCompoundContainer();
		// reactionSchemaProdBatch_Pane = new CollapsiblePane("Preferred Compound Summary");
		// reactionSchemaProdBatch_Pane = new CollapsiblePane("");
		// reactionSchemaProdBatch_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		// reactionSchemaProdBatch_Pane.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
		// reactionSchemaProdBatch_Pane.setSteps(1);
		// reactionSchemaProdBatch_Pane.setStepDelay(0);
		// reactionSchemaProdBatch_Pane.setContentPane(preferredCompoundContainer);
		// panesPreferredSpecies.add(reactionSchemaProdBatch_Pane);

		reactionSchemaContainer_bat = new ReactionSchemaContainer(false, false, this);
		reactionSchemaContainer_bat.setReactionStep(pageModel.getSummaryReactionStep());
		// reactionSchemaRegistration_cont = new ReactionSchemaContainer(false, true);
		reactionSchemaBatch_Pane = new CollapsiblePane("Reaction Concept / Generic Target");
		reactionSchemaBatch_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaBatch_Pane.setBackground(BACKGROUND_COLOR);
		reactionSchemaBatch_Pane.setSteps(1);
		reactionSchemaBatch_Pane.setStepDelay(0);
		reactionSchemaBatch_Pane.setContentPane(reactionSchemaContainer_bat);
		panesPreferredSpecies.add(reactionSchemaBatch_Pane);
		try {
			reactionSchemaBatch_Pane.setCollapsed(true);
		} catch (Exception e1) {
			ceh.logExceptionMsg(this, e1);
		}
		
		conceptionCompoundInfoContainer = new ConceptionCompoundInfoContainer (
				pageModel, batchEditPanel, mCompoundCreationHandler);
		reactionProdBatchSum_Pane = new CollapsiblePane(SUMMARY_PANE);
		reactionProdBatchSum_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionProdBatchSum_Pane.setBackground(ConceptionNotebookPageGUI.BACKGROUND_COLOR);
		reactionProdBatchSum_Pane.setSteps(1);
		reactionProdBatchSum_Pane.setStepDelay(0);
		reactionProdBatchSum_Pane.setContentPane(conceptionCompoundInfoContainer);
		batchEditPanel.setCompoundCreateInterface(conceptionCompoundInfoContainer);
		panesPreferredSpecies.add(reactionProdBatchSum_Pane);
		mCompoundCreationHandler.addCompoundCreatedEventListener(conceptionCompoundInfoContainer);
		// panesPreferredSpecies.addExpansion();

		reactionProdBatchDetail_Pane = new CollapsiblePane(BATCHDETAILS_PANE);
		reactionProdBatchDetail_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionProdBatchDetail_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionProdBatchDetail_Pane.setSteps(1);
		reactionProdBatchDetail_Pane.setStepDelay(0);
		reactionProdBatchDetail_Pane.setContentPane(batchEditPanel);
		panesPreferredSpecies.add(reactionProdBatchDetail_Pane);
		panesPreferredSpecies.addExpansion();

		// setup and show pane for Attachments
		attachements_cont = new AttachmentsContainer(pageModel);
		panesAttach.add(attachements_cont);

		// TODO fix setParentDialog to be notebookpageguiinterface or component pass reference to child components
		// regSubSum_cont.setParentDialog(this);
		// regVnV_cont.setParentDialog(this);
		// compReg_cont.setParentDialog(this);
		// hazHandling_cont.setParentDialog(this);
		// sampleSubSum_cont.setParentDialog(this);
		// try {
		// 	  reactionSchemaAnalytical_Pane.setCollapsed(true);
		//    reactionSchemaRegistration_Pane.setCollapsed(true);
		// } catch (PropertyVetoException e) {
		//    ConceptionNotebookPageGUI.ceh.logExceptionMsg(this, e);
		// }
		// Batches Update
		mainTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int i = mainTabbedPane.getSelectedIndex();
				if (i == 1) {
					//preferredCompoundContainer.refresh();
				} else if (i == 2) {
					// AnalyticalSummary_cont.refresh();
				} else if (i == 3) {
					// regSubSum_cont.refresh();
				} else if (i == 0) {
					if (Proc_cont != null)
						Proc_cont.refresh();
				}
			}
		});
		this.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				if (!MasterController.getGUIComponent().checkPendingChangesInPage())
					return;

				closing = true;
				if (shouldClose())
					ConceptionNotebookPageGUI.this.internalFrameClosing();
				else if (ConceptionNotebookPageGUI.this.isEnabled())
					closing = false;
			}

			public void internalFrameClosed(InternalFrameEvent e) {
				MasterController.getGUIComponent().refreshIcons();
			}
		});
	}

	public boolean shouldClose() {
		boolean result = true;
		/*
		 * //later if (this.pageModel.isChanging()) {
		 * JOptionPane.showMessageDialog(this, "Notebook page " +
		 * this.pageModel.getNotebookRefAsString() + " is undergoing state
		 * change, \n Please wait for a moment and try again", "Page State
		 * Change", JOptionPane.PLAIN_MESSAGE); return false; } if (pageModel !=
		 * null && pageModel.isSaving() && this.isEnabled()) { int selection =
		 * JOptionPane.showConfirmDialog(this, "The Experiment Page " +
		 * pageModel.getNotebookRefAsString() + " is still saving. Do you wish
		 * to close anyway ?", "Background Save in Progress",
		 * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); if
		 * (selection == JOptionPane.NO_OPTION) result = false; } else if
		 * (pageModel != null && pageModel.isPageEditable() &&
		 * pageModel.isModelChanged() && this.isEnabled()) { int selection =
		 * JOptionPane.showConfirmDialog(this, "Save changes to " +
		 * pageModel.getNotebookRefAsString() + "?", "Confirm Save",
		 * JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); if
		 * (selection == JOptionPane.CANCEL_OPTION) { result = false; } else if
		 * (selection == JOptionPane.YES_OPTION) { // Check the Storage Service
		 * backend is working properly boolean serviceOK =
		 * HealthCheckHandler.checkServiceHealth(HealthCheckHandler.STORAGE_SERVICE);
		 * if (!serviceOK) { if (JOptionPane.showConfirmDialog(this, "The
		 * Storage Service is not currently available, you will be unable to
		 * save experiment " + pageModel.getNotebookRefAsString() + " to the
		 * database.\n" + " Do you wish to Save your latest changes to the local
		 * disk ?", "Storage Service Unavailable", JOptionPane.YES_NO_OPTION,
		 * JOptionPane.WARNING_MESSAGE) == 0) try {
		 * MasterController.getGuiController().getPageCache().saveNotebookPage(pageModel,
		 * true); result = true; } catch (NotebookDelegateException e) {
		 * JOptionPane.showMessageDialog(this, "Unable to AutoSave this
		 * experiment.", "AutoSave Error", JOptionPane.ERROR_MESSAGE); result =
		 * true; } } else { result = saveExperiment(); } } else { try {
		 * MasterController.getGuiController().getPageCache().deleteAutoSaveFiles(pageModel.getNotebookRef()); }
		 * catch (NotebookDelegateException e) {
		 * ConceptionNotebookPageGUI.ceh.logExceptionMsg(null, e); } result =
		 * true; } } else if (pageModel.isSaving()) result = false;
		 */
		return result;
	}

	public boolean saveExperiment() {
		boolean result;
		if (!MasterController.getGuiController().saveExperiment(pageModel))
			result = false;
		else {
			this.setEnabled(false);
			GlassPane glassPane = new GlassPane();
			glassPane.setGlassPane(this);
			glassPane.setHandleMouseEvents(true);
			glassPane.setDrawing(true);
			glassPane.setOpaque(false);
			glassPane.requestFocus();
			result = true;// was false, changed to true to test.s
		}
		return result;
	}

	/** Auto-generated event handler method */
	protected void jTabbedPaneMainStateChanged(ChangeEvent evt) {
		JTabbedPane pane = (JTabbedPane) evt.getSource();
		// Get current tab
		for (int c = 0; c <= pane.getTabCount() - 1; c++) {
			pane.setBackgroundAt(c, Color.LIGHT_GRAY);
		}
		pane.setBackgroundAt(pane.getSelectedIndex(), Color.WHITE);
		pane.invalidate();
		pane.revalidate();
		pane.repaint();
	}

	/**
	 * @return pageModel The pageModel to set/get.
	 */
	public NotebookPageModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(NotebookPageModel pageModelpojo) {
	  if(pageModelpojo instanceof NotebookPageModel) 
	  { 
		  this.pageModel = (NotebookPageModel)pageModelpojo; 
		  if (this.pageModel != null) {
		  //this.pageModel.addObserver(this); 
		  this.refreshPage(); 
		  } 
	  }
	}

	public Component getComponent() {
		return this;
	}

	/**
	 * method refreshes the current page with its model TODO fill this method
	 */
	/**
	 * TODO Bo Yang 3/14/07 public void refreshPage() { if (pageModel != null) {
	 * String str = pageModel.getNotebookRefAsString(); if
	 * (pageModel.getVersion() > 1 || pageModel.getLatestVersion() == "N") str += "
	 * v" + pageModel.getVersion(); setTitle(str); setName(str); }
	 * conceptionDetailContainer.setPageModel(pageModel);
	 * ReactionSchema_cont.setPageModel(pageModel); if
	 * (Stoic_cont.getPageModel() != pageModel) // Takes a hit on the lookup of
	 * compound info. Stoic_cont.setPageModel(pageModel, ReactionSchema_cont);
	 * else // Simply refreshes the view based on model Stoic_cont.refresh();
	 * Proc_cont.setPageModel(pageModel);
	 * ReactionSchemaProdBatch_cont.setPageModel(pageModel);
	 * preferredCompoundContainer.setPageModel(pageModel, ReactionSchema_cont); //
	 * Stoic_cont.refresh(); //
	 * ReactionSchemaAnalytical_cont.setPageModel(pageModel); //
	 * AnalyticalSummary_cont.setPageModel(pageModel); // //
	 * reactionSchemaRegistration_cont.setPageModel(pageModel); //
	 * regSubSum_cont.setPageModel(pageModel);
	 * attachements_cont.setPageModel(pageModel); }
	 */
	
	/** TODO Bo Yang 3/14/07
	public void refreshPage() {
		if (pageModel != null) {
			String str = pageModel.getNotebookRefAsString();
			if (pageModel.getVersion() > 1 || pageModel.getLatestVersion() == "N")
				str += " v" + pageModel.getVersion();
			setTitle(str);
			setName(str);
		}
		conceptionDetailContainer.setPageModel(pageModel);
		ReactionSchema_cont.setPageModel(pageModel);
		if (Stoic_cont.getPageModel() != pageModel)
			// Takes a hit on the lookup of compound info.
			Stoic_cont.setPageModel(pageModel, ReactionSchema_cont);
		else
			// Simply refreshes the view based on model
			Stoic_cont.refresh();
		Proc_cont.setPageModel(pageModel);
		ReactionSchemaProdBatch_cont.setPageModel(pageModel);
		preferredCompoundContainer.setPageModel(pageModel, ReactionSchema_cont);
		// Stoic_cont.refresh();
		// ReactionSchemaAnalytical_cont.setPageModel(pageModel);
		// AnalyticalSummary_cont.setPageModel(pageModel);
		//
		// reactionSchemaRegistration_cont.setPageModel(pageModel);
		// regSubSum_cont.setPageModel(pageModel);
		attachements_cont.setPageModel(pageModel);
	}*/

	/** TODO Bo Yang 3/14/07
	public void update(Observable watchedObj, Object obj) {
		// arg represents whatever was passed to the notifyObservers() method.
		if (watchedObj instanceof NotebookPage) {
			pageModel = (NotebookPage) watchedObj;
			if (pageModel.isModelChanged())
				MasterController.getGUIComponent().refreshIcons();
			if (obj instanceof NotebookPage)
				fireDetailsChanged();
			if (obj instanceof ReactionScheme)
				fireReactionChanged((ReactionScheme) obj);
		}
	}*/

	/**
	 * @param arg0
	 */
	public void setSelectedIndex(int arg0) {
		mainTabbedPane.setSelectedIndex(arg0);
	}

	public void fireReactionChanged(ReactionSchemeModel scheme) {
		NotebookPageChangedEvent event = new NotebookPageChangedEvent(this, scheme);
		for (int i = pageChangedListenerList.size() - 1; i >= 0; i--)
			((NotebookPageChangedListener) pageChangedListenerList.get(i)).reactionChanged(event);
		
		if(reactionSchemaContainer_bat != null )
			reactionSchemaContainer_bat.reactionSchemeModelChanged(new ReactionSchemeModelChangeEvent(scheme));
	}

	public void fireDetailsChanged() {
		NotebookPageChangedEvent event = new NotebookPageChangedEvent(this, getPageModel());
		for (int i = pageChangedListenerList.size() - 1; i >= 0; i--)
			((NotebookPageChangedListener) pageChangedListenerList.get(i)).detailsChanged(event);
	}

	public void addNotebookPageChangedListener(NotebookPageChangedListener listener) {
		if (listener != null && !pageChangedListenerList.contains(listener))
			pageChangedListenerList.add(listener);
	}

	public void removeNotebookPageChangedListener(NotebookPageChangedListener listener) {
		pageChangedListenerList.remove(listener);
	}

	public void internalFrameActivated() {
		// BACKGROUND_COLOR navigate sb to this experiment
		if (pageModel != null) {
			MasterController.getGuiController().speedBarLookupNavigateTo(
					pageModel.getSiteCode(), pageModel.getBatchCreator(),
					pageModel.getNbRef(), pageModel.getVersion());
		}
	}

	public void internalFrameClosing() {
		MasterController.getGuiController().removeNotebookPageFromGUI(this);
	}

	/**
	 * @return Returns the sampleSubSum_cont.
	 */
	public SampleSubSumContainer getSampleSubSum_cont() {
		return sampleSubSum_cont;
	}

	/**
	 * @param sampleSubSum_cont
	 *            The sampleSubSum_cont to set.
	 */
	public void setSampleSubSum_cont(SampleSubSumContainer sampleSubSum_cont) {
		this.sampleSubSum_cont = sampleSubSum_cont;
	}

	public void dispose() {
		super.dispose();
		/*
		 * //later preferredCompoundContainer.dispose(); ((ActionMap)
		 * UIManager.getLookAndFeelDefaults().get("InternalFrame.actionMap")).remove("showSystemMenu");
		 * ReactionSchema_cont.dispose();
		 * ReactionSchemaProdBatch_cont.dispose();
		 */
		// ReactionSchemaAnalytical_cont.dispose();
		// reactionSchemaRegistration_cont.dispose();
		if (batchContainerCache != null) {
			batchContainerCache.clear();
			batchContainerCache = null;
		}
	}

	/**
	 * @return Returns the reactionSchema_cont.
	 */
	public ReactionSchemaContainer getReactionSchema_cont() {
		return reactionSchemaContainer_exp;
	}

	/**
	 * @return Returns the proc_cont.
	 */
	public ProcedureContainer getProc_cont() {
		return Proc_cont;
	}

	// public AnalyticalSummaryContainer getAnalyticalSummary_cont() {
	// return AnalyticalSummary_cont;
	// }
	public void setClosing(boolean close) {
		this.closing = close;
	}

	public boolean isClosing() {
		return this.closing;
	}

	/**
	 * @return the pageEditable
	 */
	public boolean isPageEditable() {
		return pageEditable;
	}

	/**
	 * @param pageEditable
	 *            the pageEditable to set
	 */
	public void setPageEditable(boolean pageEditable) {
		this.pageEditable = pageEditable;
	}

	public void refreshPage() {
		// contents of experiment panes
		conceptionDetailContainer.setPageModel(pageModel);
		reactionSchemaContainer_exp.refreshPageModel(pageModel, false);
		reactionSchemaContainer_bat.refreshPageModel(pageModel, true);
		// refresh for reaction viewer
		reactionSchemaContainer_exp.setReactionStep(pageModel.getSummaryReactionStep());
		reactionSchemaContainer_bat.setReactionStep(pageModel.getSummaryReactionStep());
	
		
		Proc_cont.refreshPageModel(pageModel);
	
		// contents of Product batch panes
		conceptionCompoundInfoContainer.switchToProductsFromPageModel(pageModel);
		conceptionCompoundInfoContainer.refreshPageModel(pageModel);

		//private PreferredCompoundContainer preferredCompoundContainer;
		//private SigletonBatchInfoContainer conceptBatchInfoContainer;
		batchEditPanel.refreshPageModel(pageModel);
		attachements_cont.refreshPageModel(pageModel);
	}
	
	public ConceptionCompoundInfoContainer getConceptionCompoundInfoContainer() {
		return conceptionCompoundInfoContainer;
	}

/*	public boolean isUserAddedBatch(ProductBatchModel productBatchModel) {
		BatchType userAddedBatchType = null;
		try {
			userAddedBatchType = BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL);
		} catch (InvalidBatchTypeException e) {
			e.printStackTrace();
		}
		BatchType batchType = productBatchModel.getBatchType();
		if (batchType.compareTo(userAddedBatchType) == 0)
			return true;
		else
			return false;
	}*/
	
	public void showTab(int i) {
		mainTabbedPane.setSelectedIndex(i);		
	}

	public ConceptionDetailsContainer getConceptionDetailContainer() {
		return conceptionDetailContainer;
	}
	

	public Map<String, BatchContainer> getBatchContainerCache() {
		return batchContainerCache;
	}

	public PCeNTableView getCurrentBatchDetailsTableView() {
		return conceptionCompoundInfoContainer.getProductTableView();
	}
}