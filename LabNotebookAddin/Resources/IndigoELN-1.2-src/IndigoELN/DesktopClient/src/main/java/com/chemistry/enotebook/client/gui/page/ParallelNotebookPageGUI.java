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
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePanes;
import com.chemistry.enotebook.client.gui.common.ekit.ProcedureContainer;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalSampleRefContainer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PCeNAnalytical_BatchDetailsViewContainer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PCeNAnalytical_SummaryViewContainer;
import com.chemistry.enotebook.client.gui.page.attachements.AttachmentsContainer;
import com.chemistry.enotebook.client.gui.page.batch.*;
import com.chemistry.enotebook.client.gui.page.experiment.CompoundManagementMonomerContainer;
import com.chemistry.enotebook.client.gui.page.reaction.ReactionDetailsContainer;
import com.chemistry.enotebook.client.gui.page.reaction.ReactionSchemaContainer;
import com.chemistry.enotebook.client.gui.page.regis_submis.*;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.NbkBatchNumberComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ParallelNotebookPageGUI extends CeNInternalFrame implements NotebookPageGuiInterface {
	
	private static final long serialVersionUID = -2274271897073658480L;
	
	public static final String CONCEPTION_RECORD_TAB = "Conception Record";
	public static final String EXPERIMENTS_TAB = "Experiments";
	public static final String BATCHINFO_TAB = "Batches";
	public static final String QCANALYTICAL_TAB = "Analytical";
	public static final String REGISTRATION_TAB = "Registration & Submission";
	public static final String ATTACHMENTS_TAB = "Attachments";
	public static final int EXPERIMENTS_TAB_MODE = 0;
	// batch info tab
	public static final String REACTION_PANE = "Reaction Scheme";
	public static final String BATCHDETAILS_PANE = "Product Batch Details";
	public static final String PLATEVIEWER_PANE = "Product";
	public static final String SAMPLE_REFS_PANE = "Analytical Sample References";
	public static final String COMPOUND_REG_DETAILS = "Compound Registration Details";  // vb 8/6

	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private JTabbedPane mainTabbedPane = new JTabbedPane();
	private JScrollPane tabExperimentPanel = new JScrollPane();
	private CollapsiblePanes panesExperiment = new CollapsiblePanes();

	private CollapsiblePanes panesBatches = new CollapsiblePanes();
	private CollapsiblePanes panesAnalQC = new CollapsiblePanes();
	private CollapsiblePanes panesRegistration = new CollapsiblePanes();
	private CollapsiblePanes panesAttach = new CollapsiblePanes();

	private JScrollPane tabBatchInfoPanel = new JScrollPane();
	private JScrollPane jScrollPaneAnalQC = new JScrollPane();
	private JScrollPane jScrollPaneRegistration = new JScrollPane();

	private ReactionDetailsContainer reactionDetails;

	private ProcedureContainer Proc_cont;
	
	// contents of submission panes
	private SampleSubSumContainer sampleSubSum_cont = new SampleSubSumContainer();
	
	// contents of attachment pane
	private AttachmentsContainer attachements_cont;

	// experiment tab panes
	private CollapsiblePane reactionDetails_Coll_Pane = new CollapsiblePane("Reaction Details");
	private CollapsiblePane reactionSchema_Coll_Pane = new CollapsiblePane("Reaction Scheme");
	private CollapsiblePane proc_cont_Coll_Pane = new CollapsiblePane("Procedure");

	// Product Batch Tab panes
	private CollapsiblePane reactionSchemaProdBatch_Pane = new CollapsiblePane(REACTION_PANE);
	private CollapsiblePane batchEditCollapsiblePane = new CollapsiblePane(BATCHDETAILS_PANE);
	private CollapsiblePane summaryCollapsiblePane = new CollapsiblePane("Batch Racks/Plates Summary");
	private PCeNBatch_BatchDetailsViewContainer batchDetailsViewContainer; 
	private CompoundCreationHandler mCompoundCreationHandler = new CompoundCreationHandler();	
	private BatchEditPanel batchEditPanel;
	private PCeNBatch_SummaryViewContainer summaryBatchRackCont;

	// Analytical Tab panes
	private CollapsiblePane reactionSchemaAnalytical_Pane = new CollapsiblePane("Reaction Scheme");
	private ReactionSchemaContainer ReactionSchemaAnalytical_cont;
	private CollapsiblePane analyticalSummary_Pane = new CollapsiblePane("Analytical Data Summary");
	private PCeNAnalytical_SummaryViewContainer paralleAnalyticalSummaryViewer;
	private PCeNAnalytical_BatchDetailsViewContainer analyticalPlateDetailsViewer;
	private CollapsiblePane analyticalSampleRef_Pane = new CollapsiblePane(SAMPLE_REFS_PANE);  
	private PAnalyticalSampleRefContainer analyticalSampleRefContainer; 

	// Registration Tab panes
	private CollapsiblePane reactionSchemaRegistration_Pane = new CollapsiblePane("Reaction Scheme");
	private CollapsiblePane summaryCompoundRegistrantsPane = new CollapsiblePane("Compound Registration and Submission Summary");
	private CollapsiblePane regBatchDetailsCollapsiblePane = new CollapsiblePane("Batch Details");
	private PCeNRegistration_SummaryViewContainer paralleRegistrationlSummaryViewer;
	private Reg_subsumContainer regSubSum_cont = new Reg_subsumContainer();
	//private StructureVnVContainer regVnV_cont; // = new StructureVnVContainer();
	//private CompoundRegInfoContainer compReg_cont; // = new CompoundRegInfoContainer();
	//private HazHandlingStorageContainer hazHandling_cont; // = new HazHandlingStorageContainer();
	private PCeNRegistration_BatchDetailsViewContainer compoundRegistrationPlateDetailsViewer;
	// vb 2/12
	private RegistrationBatchDetailPanel regBatchDetailsPanel;
	
	// Attachments
	private CollapsiblePane attachment_Pane = new CollapsiblePane("Attachment");
	
	private boolean closing;
	//private boolean pageEditable;
	private NotebookPageModel pageModel;
	private PlateCreationHandler mPlateCreationHandler = new PlateCreationHandler();
	private ReactionStepJTabPaneFactory rSchemaGUI;
	private Map<String, BatchContainer> batchContainerCache = new HashMap<String, BatchContainer>();
	
	public static final Log log = LogFactory.getLog(ParallelNotebookPageGUI.class);

	public ParallelNotebookPageGUI(NotebookPageModel mpageModel) {
		this.pageModel = mpageModel;
		log.debug("Before initGUI() pageModel modified:"+this.pageModel.isModelChanged());
		boolean pageModelChanged = this.pageModel.isModelChanged();
		long startTime = System.currentTimeMillis();
		/////// vb 7/17 this should be moved to the storage delegate post load processing
		// Create a map of monomer batches to their products so statuses can be propagated
		new ExperimentPageUtils().setPrecusorMap(pageModel);
		///////////////////////////////////////////
		//regVnV_cont = new StructureVnVContainer();
		if (log.isInfoEnabled()) 
			log.info("StructureVnVContainer constructor : " + (System.currentTimeMillis() - startTime ) + " ms");
		startTime = System.currentTimeMillis();
		//compReg_cont = new CompoundRegInfoContainer();
		if (log.isInfoEnabled()) 
			log.info("CompoundRegInfoContainer constructor : " + (System.currentTimeMillis() - startTime ) + " ms");		
		startTime = System.currentTimeMillis();
		//hazHandling_cont = new HazHandlingStorageContainer(pageModel);
		if (log.isInfoEnabled()) 
			log.info("HazHandlingStorageContainer constructor : " + (System.currentTimeMillis() - startTime ) + " ms");				
		
		// vb 3/13 sort product batch lists at start so it doesn't have to be done for every table
		startTime = System.currentTimeMillis();
		for (ReactionStepModel step : pageModel.getReactionSteps()) {
			List<BatchesList<ProductBatchModel>>  batchesLists = step.getProductBatchLists();
			for (BatchesList<ProductBatchModel> batchesList : batchesLists) {
				List<ProductBatchModel> batches = batchesList.getBatchModels();
				Collections.sort(batches, new NbkBatchNumberComparator<ProductBatchModel>());
			}
		}
		if (log.isInfoEnabled()) 
			log.info("Get Reaction steps and Sort :" + (System.currentTimeMillis() - startTime) + " ms");	
		startTime = System.currentTimeMillis();
		rSchemaGUI = new ReactionStepJTabPaneFactory(mPlateCreationHandler, this.pageModel);
		batchEditPanel = new BatchEditPanel(pageModel, mCompoundCreationHandler,this); // set isParallel flag
		regBatchDetailsPanel = new RegistrationBatchDetailPanel(this.pageModel, true);
		if (log.isInfoEnabled()) 
			log.info("Populate Batch edit panels and RSchema :" + (System.currentTimeMillis() - startTime ) + " ms");
		initGUI();
		log.debug("After initGUI() pageModel modified:"+this.pageModel.isModelChanged());
		this.pageModel.setModelChanged(pageModelChanged);
		log.debug("After restoring original flag pageModel modified:"+this.pageModel.isModelChanged());
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	private void initGUI() {
		try {
			String title = pageModel.getNotebookRefWithoutVersion() + "- Parallel Experiment";
			if (pageModel.getVersion() > 1 || pageModel.getLatestVersion() == "N") title += " v" + pageModel.getVersion();
			this.setTitle(title);
			this.setSelected(true);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setClosable(true);
			// this.setResizable(true);
			// this.setIconifiable(true);
			// this.setMaximizable(true);
			this.setVisible(true);
			this.setPreferredSize(new java.awt.Dimension(681, 318));
			this.setBounds(new java.awt.Rectangle(0, 0, 703, 618));
			// this.setMaximumSize(super.getDesktopPane().getSize());
			try {
				setMaximum(true);
			} catch (Exception e) {
			}
			mainTabbedPane.setAutoscrolls(false);
			this.getContentPane().add(mainTabbedPane);
			mainTabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					// Get current tab
					for (int c = 0; c <= mainTabbedPane.getTabCount() - 1; c++) {
						mainTabbedPane.setBackgroundAt(c, Color.LIGHT_GRAY);
					}
					mainTabbedPane.setBackgroundAt(mainTabbedPane.getSelectedIndex(), Color.WHITE);
					if (StringUtils.equals(mainTabbedPane.getTitleAt(mainTabbedPane.getSelectedIndex()), REGISTRATION_TAB)) {
						if (compoundRegistrationPlateDetailsViewer != null) {
							compoundRegistrationPlateDetailsViewer.setBatchesSelected();
						}
					}
				}
			});
			tabExperimentPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			tabExperimentPanel.add(panesExperiment);
			tabExperimentPanel.setViewportView(panesExperiment);
			mainTabbedPane.add(tabExperimentPanel);
			mainTabbedPane.setTitleAt(0, EXPERIMENTS_TAB);
			panesExperiment.setBorder(null);
			tabBatchInfoPanel.add(panesBatches);
			tabBatchInfoPanel.setViewportView(panesBatches);
			mainTabbedPane.add(tabBatchInfoPanel);
			mainTabbedPane.setTitleAt(1, BATCHINFO_TAB);
			panesBatches.setBorder(null);
			jScrollPaneAnalQC.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
			jScrollPaneAnalQC.add(panesAnalQC);
			jScrollPaneAnalQC.setViewportView(panesAnalQC);
			mainTabbedPane.add(jScrollPaneAnalQC);
			mainTabbedPane.setTitleAt(2, QCANALYTICAL_TAB);
			panesAnalQC.setBorder(null);
			jScrollPaneRegistration.setVisible(false);
			jScrollPaneRegistration.add(panesRegistration);
			jScrollPaneRegistration.setViewportView(panesRegistration);
			mainTabbedPane.add(jScrollPaneRegistration);
			mainTabbedPane.setTitleAt(3, REGISTRATION_TAB);
			panesRegistration.setBorder(null);
			panesAttach.setPreferredSize(new java.awt.Dimension(520, 251));
			panesAttach.setBorder(null);
			mainTabbedPane.add(panesAttach);
			mainTabbedPane.setTitleAt(4, ATTACHMENTS_TAB);
			postInitGUI();
		} catch (Exception e) {
//			e.printStackTrace();  // for development
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/**
	 * Add your post-init code in here
	 */
	public void postInitGUI() {
		if (log.isInfoEnabled()) 
			log.info("GUI construction starting...");			
		long startTime = System.currentTimeMillis();
		super.postInitGUI();
		long b = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Internal Frame Construction :" + (b-startTime) + " ms");
		setFrameIcon(CenIconFactory.getImageIcon(CenIconFactory.General.APPLICATION_ICON));
		// setup scrollbars with larger increment
		// jScrollPaneConceptionRecord.getVerticalScrollBar().setUnitIncrement(50);
		tabExperimentPanel.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPaneRegistration.getVerticalScrollBar().setUnitIncrement(50);
		tabBatchInfoPanel.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPaneAnalQC.getVerticalScrollBar().setUnitIncrement(50);
		// for ReactionDetails_cont setup and show collapse pane for
		// reaction_details
		reactionDetails = new ReactionDetailsContainer(pageModel);
		reactionDetails_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionDetails_Coll_Pane.setSteps(1);
		reactionDetails_Coll_Pane.setStepDelay(0);
		reactionDetails_Coll_Pane.getContentPane().setLayout(new BorderLayout());
		reactionDetails_Coll_Pane.getContentPane().add(reactionDetails, BorderLayout.CENTER);
		panesExperiment.add(reactionDetails_Coll_Pane);
		// for ReactionSchemaContainer
		reactionSchema_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchema_Coll_Pane.setSteps(1);
		reactionSchema_Coll_Pane.setStepDelay(0);
		reactionSchema_Coll_Pane.getContentPane().setLayout(new BorderLayout());
		// adds an Intermediate Steps TabbedPane
		reactionSchema_Coll_Pane.getContentPane().add(
				rSchemaGUI.createIntermediateStepsJTabbedPane(SwingConstants.TOP, EXPERIMENTS_TAB_MODE), BorderLayout.CENTER);  //////////////////////
		panesExperiment.add(reactionSchema_Coll_Pane);
		//panesExperiment.addExpansion();

		// for procedure_cont
		Proc_cont = new ProcedureContainer(pageModel, mCompoundCreationHandler,true);
		proc_cont_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		proc_cont_Coll_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		proc_cont_Coll_Pane.setContentPane(Proc_cont);
		proc_cont_Coll_Pane.setSteps(1);
		proc_cont_Coll_Pane.setStepDelay(0);
		panesExperiment.add(proc_cont_Coll_Pane);	
		panesExperiment.addExpansion();
		long c = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Exp Tab Construction : " + (c-b) + " ms");

		//Batches Tab
		reactionSchemaProdBatch_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaProdBatch_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchemaProdBatch_Pane.setSteps(1);
		reactionSchemaProdBatch_Pane.setStepDelay(0);
		reactionSchemaProdBatch_Pane.getContentPane().setLayout(new BorderLayout());
		// adds an Intermediate Steps TabbedPane
		reactionSchemaProdBatch_Pane.getContentPane().add(rSchemaGUI.createIntermediateStepsJTabbedPane(SwingConstants.TOP, -1),
				BorderLayout.CENTER);
		panesBatches.add(reactionSchemaProdBatch_Pane);
	
		// batch summary viewer
		summaryBatchRackCont = new PCeNBatch_SummaryViewContainer(pageModel);
		summaryCollapsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		summaryCollapsiblePane.setBackground(CeNConstants.BACKGROUND_COLOR);
		summaryCollapsiblePane.setSteps(1);
		summaryCollapsiblePane.setStepDelay(0);
		summaryCollapsiblePane.setContentPane(summaryBatchRackCont);
		panesBatches.add(summaryCollapsiblePane);
		mPlateCreationHandler.addPlateCreatedEventListener(summaryBatchRackCont);
		mCompoundCreationHandler.addCompoundCreatedEventListener(summaryBatchRackCont);

		// batch details viewer
		batchDetailsViewContainer = new PCeNBatch_BatchDetailsViewContainer(pageModel,
		                                                                    this.batchEditPanel, 
		                                                                    mCompoundCreationHandler,
		                                                                    mPlateCreationHandler, 
		                                                                    summaryBatchRackCont, 
		                                                                    ParallelNotebookPageGUI.PLATEVIEWER_PANE); 
		panesBatches.add(batchDetailsViewContainer.getPlateViewer());
		mCompoundCreationHandler.addCompoundCreatedEventListener(batchDetailsViewContainer);
//		summaryBatchRackCont.setPlateSelectionChangedListener(batchDetailsViewContainer);
		mPlateCreationHandler.addPlateCreatedEventListener(batchDetailsViewContainer);
		
		
		// batch detail edit panel
		JScrollPane scrollBatchEditPane = new JScrollPane(batchEditPanel);
		scrollBatchEditPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		batchEditCollapsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		batchEditCollapsiblePane.setBackground(CeNConstants.BACKGROUND_COLOR);
		batchEditCollapsiblePane.setSteps(1);
		batchEditCollapsiblePane.setStepDelay(0);
		batchEditCollapsiblePane.setContentPane(scrollBatchEditPane);
		panesBatches.add(batchEditCollapsiblePane);
		panesBatches.addExpansion();
		
		// add batch detail edit panel to the plate selection changed listeners
		summaryBatchRackCont.setPlateSelectionChangedListener(batchEditPanel);
		long d = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Batch TabConstruction : " + (d-c) + " ms");

////Analytical Tab
		//reaction Schema
		reactionSchemaAnalytical_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaAnalytical_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchemaAnalytical_Pane.setSteps(1);
		reactionSchemaAnalytical_Pane.setStepDelay(0);
		reactionSchemaAnalytical_Pane.getContentPane().setLayout(new BorderLayout());
		// adds an Intermediate Steps TabbedPane
		reactionSchemaAnalytical_Pane.getContentPane().add(rSchemaGUI.createIntermediateStepsJTabbedPane(SwingConstants.TOP, -1));
		reactionSchemaAnalytical_Pane.setContentPaneHeight(300);
		// rSchemaGUI = new ReactionSchemaGUIBuilder(pageModel, analitPanel,
		// -1);
		panesAnalQC.add(reactionSchemaAnalytical_Pane, BorderLayout.CENTER);
		// for analytical summary pane
		//panesAnalQC.add(jPanelAnalSub);
		
		//sumary viewer
		paralleAnalyticalSummaryViewer = new PCeNAnalytical_SummaryViewContainer(pageModel);
		analyticalSummary_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		analyticalSummary_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		analyticalSummary_Pane.setSteps(1);
		analyticalSummary_Pane.setStepDelay(0);
		analyticalSummary_Pane.setContentPane(paralleAnalyticalSummaryViewer);
		panesAnalQC.add(analyticalSummary_Pane);
		analyticalSampleRefContainer = new PAnalyticalSampleRefContainer(pageModel);

		analyticalPlateDetailsViewer = new PCeNAnalytical_BatchDetailsViewContainer(pageModel, 
										   analyticalSampleRefContainer);  
		// vb 6/4 The detail container has to listen for the global analytical events that occur at the summary level
		// (advancedLinkAll and linkAll).
		paralleAnalyticalSummaryViewer.addDownstreamAnalyticalChangeListener(analyticalPlateDetailsViewer);
		
		mPlateCreationHandler.addPlateCreatedEventListener(this.analyticalPlateDetailsViewer);
		panesAnalQC.add(analyticalPlateDetailsViewer.getPlateViewer());

//		paralleAnalyticalSummaryViewer.setPlateSelectionChangedListener(analyticalPlateDetailsViewer);
		mCompoundCreationHandler.addCompoundCreatedEventListener(paralleAnalyticalSummaryViewer);		
		mCompoundCreationHandler.addCompoundCreatedEventListener(analyticalPlateDetailsViewer);
		
		analyticalSampleRef_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		analyticalSampleRef_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		analyticalSampleRef_Pane.setSteps(1);
		analyticalSampleRef_Pane.setStepDelay(0);
		analyticalSampleRef_Pane.setContentPane(analyticalSampleRefContainer);
		panesAnalQC.add(analyticalSampleRef_Pane);
		
		mPlateCreationHandler.addPlateCreatedEventListener(paralleAnalyticalSummaryViewer);
		panesAnalQC.addExpansion();
		long e = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Analytical TabConstruction : " + (e-d) + " ms");

        // Registration Tab
		// Reaction Schema
		reactionSchemaRegistration_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaRegistration_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchemaRegistration_Pane.setSteps(1);
		reactionSchemaRegistration_Pane.setStepDelay(0);
		reactionSchemaRegistration_Pane.getContentPane().setLayout(new BorderLayout());
		// adds an Intermediate Steps TabbedPane
		reactionSchemaRegistration_Pane.getContentPane().add(rSchemaGUI.createIntermediateStepsJTabbedPane(SwingConstants.TOP, -1));
		reactionSchemaRegistration_Pane.setContentPaneHeight(300);
		panesRegistration.add(reactionSchemaRegistration_Pane, BorderLayout.CENTER);

		// summary viewer
		paralleRegistrationlSummaryViewer = new PCeNRegistration_SummaryViewContainer(pageModel);
		summaryCompoundRegistrantsPane.setStyle(CollapsiblePane.TREE_STYLE);
		summaryCompoundRegistrantsPane.setBackground(CeNConstants.BACKGROUND_COLOR);
		summaryCompoundRegistrantsPane.setSteps(1);
		summaryCompoundRegistrantsPane.setStepDelay(0);
		summaryCompoundRegistrantsPane.setContentPane(paralleRegistrationlSummaryViewer);
		panesRegistration.add(summaryCompoundRegistrantsPane); // vb 8/3 analyticalSummary_Pane);
		mCompoundCreationHandler.addCompoundCreatedEventListener(paralleRegistrationlSummaryViewer);
		// details viewer
		compoundRegistrationPlateDetailsViewer = new PCeNRegistration_BatchDetailsViewContainer(pageModel, this.regBatchDetailsPanel);  
		mPlateCreationHandler.addPlateCreatedEventListener(paralleRegistrationlSummaryViewer);
		mPlateCreationHandler.addPlateCreatedEventListener(compoundRegistrationPlateDetailsViewer);
		panesRegistration.add(compoundRegistrationPlateDetailsViewer.getPlateViewer());
		
		// batch detail edit panel
		regBatchDetailsCollapsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		regBatchDetailsCollapsiblePane.setBackground(CeNConstants.BACKGROUND_COLOR);
		regBatchDetailsCollapsiblePane.setSteps(1);
		regBatchDetailsCollapsiblePane.setStepDelay(0);
		regBatchDetailsCollapsiblePane.setContentPane(this.regBatchDetailsPanel);
		panesRegistration.add(regBatchDetailsCollapsiblePane);
		panesRegistration.addExpansion();



//		paralleRegistrationlSummaryViewer.setPlateSelectionChangedListener(compoundRegistrationPlateDetailsViewer);
		panesRegistration.addExpansion();
		mCompoundCreationHandler.addCompoundCreatedEventListener(compoundRegistrationPlateDetailsViewer);
		
		long f = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Registration TabConstruction : " + (f-e) + " ms");
	
////	Attachment Tab	
		
		 attachements_cont = new AttachmentsContainer(pageModel);
		 attachment_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		 attachment_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		 attachment_Pane.setSteps(1);
		 attachment_Pane.setStepDelay(0);
		 attachment_Pane.getContentPane().setLayout(new BorderLayout());
		
		 attachment_Pane.getContentPane().add(attachements_cont);
		 attachment_Pane.setContentPaneHeight(300);
		panesAttach.add(attachment_Pane, BorderLayout.CENTER);

		long g = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Attachments Tab :" + (g-f) + " ms");
		// pass reference to child components
		// regSubSum_cont.setParentDialog(this);
		// regVnV_cont.setParentDialog(this);
		// compReg_cont.setParentDialog(this);
		// hazHandling_cont.setParentDialog(this);
		// sampleSubSum_cont.setParentDialog(this);
		try {
			reactionSchemaAnalytical_Pane.setCollapsed(true);
			reactionSchemaRegistration_Pane.setCollapsed(true);
			// this.setSize(getMaximumSize());
		} catch (Exception ex) {
			ceh.logExceptionMsg(this, ex);
		}
		// Batches Update
		mainTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				// vb 2/6 clear selected areas when you switch to another tab
				summaryBatchRackCont.getSummaryTableView().setSelectedAreas(new Vector<Rectangle>());  // vb 2/6
				paralleAnalyticalSummaryViewer.getSummaryTableView().setSelectedAreas(new Vector<Rectangle>());
				//paralleRegistrationlSummaryViewer.getSummaryTableview().setSelectedAreas(new Vector());
				
				int i = mainTabbedPane.getSelectedIndex();
				if (i == 0) // Reaction_Details_TAB
					reactionDetails.setEditableFlag();
				if (i == 1) // BATCHINFO_TAB
				{
					batchEditPanel.batchSelectionChanged(null);				
					batchEditPanel.revalidate();
				}
				else if (i == 2) {  // QCANALYTICAL_TAB
					// viewBatchRackContainer.refresh();
					//paralleAnalyticalSummaryViewer.refresh();
				} else if (i == 3) { // REGISTRATION_TAB
					regSubSum_cont.refresh(); // This only works for singleton
					//PCeNRegistration_SummaryTableView summaryTableView = paralleRegistrationlSummaryViewer.getSummaryTableview();
					//List selectedPlates = summaryTableView.getSelectedPlates();
					//compoundRegistrationPlateDetailsViewer.updateSelectedPlatesProducts(selectedPlates);
				}
				// else if (i == 0)
				// Proc_cont.refresh();
			}
		});
		this.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				if (!MasterController.getGUIComponent().checkPendingChangesInPage())
					return;
				
				closing = true;
				if (shouldClose())
					ParallelNotebookPageGUI.this.internalFrameClosing();
				else if (ParallelNotebookPageGUI.this.isEnabled())
					closing = false;
			}

			public void internalFrameClosed(InternalFrameEvent e) {
				MasterController.getGUIComponent().refreshIcons();
			}
		});
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("GUI construction : " + (endTime - startTime) + " ms");
		}
		
		long h = System.currentTimeMillis();
		if (log.isInfoEnabled()) 
			log.info("Rest Misc. :" + (h-g) + " ms");		
	}

	/**
	 * TODO implement all the logic to clean and save subObjects after close.
	 */
	public boolean shouldClose() {
		return true;
	}

	/**
	 * Auto-generated main method
	 */
//	public static void main(String[] args) {
//		showGUI();
//	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * <p/> It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
//	public static void showGUI() {
//		try {
//			/*
//			 * pageModel = ServiceController.getStorageService() .createParallelExperiment("SPID","", new
//			 * SessionIdentifier("","","",true)); javax.swing.JFrame frame = new javax.swing.JFrame(); ParallelNotebookPageGUI inst =
//			 * new ParallelNotebookPageGUI( pageModel); javax.swing.JDesktopPane jdp = new javax.swing.JDesktopPane();
//			 * jdp.add(inst); jdp.setPreferredSize(inst.getPreferredSize()); frame.setContentPane(jdp);
//			 * frame.getContentPane().setSize(inst.getSize()); frame
//			 * .setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); frame.pack(); frame.setVisible(true);
//			 */
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Auto-generated event handler method
	 */
	protected void jTabbedPaneMainStateChanged(ChangeEvent evt) {
		JTabbedPane pane = (JTabbedPane) evt.getSource();
		// Get current tab
		for (int c = 0; c <= pane.getTabCount() - 1; c++) {
			pane.setBackgroundAt(c, Color.LIGHT_GRAY);
		}
		pane.setBackgroundAt(pane.getSelectedIndex(), Color.WHITE);
	}

	/**
	 * method refreshes the current page with its model TODO fill this method
	 */
	public void refreshPage() {
		if (pageModel != null) {
			String str = pageModel.getNotebookRefWithoutVersion() + "-Parallel Experiment";
			setTitle(str);
			setName(str);
		}

		reactionDetails.refreshPageModel(pageModel);
		//ReactionSchema_cont.setPageModel(pageModel);//It is not editable in Parallel
/*		if (Stoic_cont.getPageModel() != pageModel)
		    // Takes a hit on the lookup of compound info.
		    Stoic_cont.setPageModel(pageModel, ReactionSchema_cont);
		else
		    // Simply refreshes the view based on model
		    Stoic_cont.refresh();*/
		
		Proc_cont.refreshPageModel(pageModel);
		
		summaryBatchRackCont.refreshPageModel(pageModel);
		batchDetailsViewContainer.refreshPageModel(pageModel);
		batchEditPanel.refreshPageModel(pageModel);
		
		paralleAnalyticalSummaryViewer.refreshPageModel(pageModel);
		analyticalSampleRefContainer.refreshPageModel(pageModel);
		
		compoundRegistrationPlateDetailsViewer.refreshPageModel(pageModel);
		
		attachements_cont.refreshPageModel(pageModel);
//		Stoic_cont.refresh();
		/*ReactionSchemaAnalytical_cont.setPageModel(pageModel);
		AnalyticalSummary_cont.setPageModel(pageModel);
		
		ReactionSchemaRegistration_cont.setPageModel(pageModel);
		RegSubSum_cont.setPageModel(pageModel);
		
		Attachements_cont.setPageModel(pageModel);*/

/*		ReactionDetails_cont.setPageModel(pageModel);
		ReactionSchema_cont.setPageModel(pageModel);
		if (Stoic_cont.getPageModel() != pageModel)
		    // Takes a hit on the lookup of compound info.
		    Stoic_cont.setPageModel(pageModel, ReactionSchema_cont);
		else
		    // Simply refreshes the view based on model
		    Stoic_cont.refresh();
		
		Proc_cont.setPageModel(pageModel);
		
		ReactionSchemaProdBatch_cont.setPageModel(pageModel);
		BatchInfo_cont.setPageModel(pageModel, ReactionSchema_cont);
		
//		Stoic_cont.refresh();
		ReactionSchemaAnalytical_cont.setPageModel(pageModel);
		AnalyticalSummary_cont.setPageModel(pageModel);
		
		ReactionSchemaRegistration_cont.setPageModel(pageModel);
		RegSubSum_cont.setPageModel(pageModel);
		
		Attachements_cont.setPageModel(pageModel);
*/	}

//	/**
//	 * @return Returns the regSubSum_cont.
//	 */
//	public Reg_subsumContainer getRegSubSum_cont() {
//		return regSubSum_cont;
//	}
//
//	/**
//	 * @param regSubSum_cont
//	 *            The regSubSum_cont to set.
//	 */
//	public void setRegSubSum_cont(Reg_subsumContainer regSubSum_cont) {
//		this.regSubSum_cont = regSubSum_cont;
//	}

	/**
	 * @return Returns the regVnV_cont.
	 */
/*	public StructureVnVContainer getRegVnV_cont() {
		return regVnV_cont;
	}

	*//**
	 * @param regVnV_cont
	 *            The regVnV_cont to set.
	 *//*
	public void setRegVnV_cont(StructureVnVContainer regVnV_cont) {
		this.regVnV_cont = regVnV_cont;
	}*/

	/**
	 * @return Returns the compReg_cont.
	 */
/*	public CompoundRegInfoContainer getCompReg_cont() {
		return compReg_cont;
	}

	*//**
	 * @param compReg_cont
	 *            The compReg_cont to set.
	 *//*
	public void setCompReg_cont(CompoundRegInfoContainer compReg_cont) {
		this.compReg_cont = compReg_cont;
	}*/

	/**
	 * @return Returns the hazHandling_cont.
	 */
/*	public HazHandlingStorageContainer getHazHandling_cont() {
		return hazHandling_cont;
	}

	*//**
	 * @param hazHandling_cont
	 *            The hazHandling_cont to set.
	 *//*
	public void setHazHandling_cont(HazHandlingStorageContainer hazHandling_cont) {
		this.hazHandling_cont = hazHandling_cont;
	}*/

	/**
	 * @return Returns the batchInfo_cont.
	 */
/*	public BatchInfoTabContainer getBatchInfo_cont() {
		return BatchInfo_cont;
	}*/
	
	
	public PCeNBatch_BatchDetailsViewContainer getBatchDetailsViewContainer() {
		return batchDetailsViewContainer;	
	}

	// public ReactionSchemaContainer getReactionSchemaProdBatch_cont() {
	// return ReactionSchemaProdBatch_cont;
	// }
	public ReactionSchemaContainer getReactionSchemaAnalytical_cont() {
		return ReactionSchemaAnalytical_cont;
	}

	// public ReactionSchemaContainer getReactionSchemaRegistration_cont() {
	// return reactionSchemaRegistration_cont;
	// }
	/**
	 * @param arg0
	 */
	public void setSelectedIndex(int arg0) {
		mainTabbedPane.setSelectedIndex(arg0);
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

	/**
	 * @return Returns the proc_cont.
	 */
	public ProcedureContainer getProc_cont() {
		return Proc_cont;
	}

	/**
	 * @return Returns the AnalyticalSummary_cont.
	 */
	//public AnalyticalSummaryContainer getAnalyticalSummary_cont() {
	//	return paralleAnalyticalSummaryViewer;
	//}

	public void setClosing(boolean close) {
		this.closing = close;
	}

	public boolean isClosing() {
		return this.closing;
	}

	// NotebookPageGuiInterface implementation
	public void dispose() {
		super.dispose();  // vbtodo close calls here?
		((ActionMap) UIManager.getLookAndFeelDefaults().get("InternalFrame.actionMap")).remove("showSystemMenu");
		List<CompoundManagementMonomerContainer> expContainers = rSchemaGUI.getCompoundManagementPanels();
		for (Iterator<CompoundManagementMonomerContainer> it = expContainers.iterator(); it.hasNext();)
			((CompoundManagementMonomerContainer) it.next()).dispose();
		this.batchEditPanel.dispose();
		this.batchDetailsViewContainer.dispose();
		this.summaryBatchRackCont.dispose();
		this.regBatchDetailsPanel = null;
		// ReactionSchemaProdBatch_cont.dispose();
		// ReactionSchemaAnalytical_cont.dispose();
		// reactionSchemaRegistration_cont.dispose();
		if (batchContainerCache != null) {
			batchContainerCache.clear();
			batchContainerCache = null;
		}
	}

	public NotebookPageModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(NotebookPageModel pageModel) {
		if (pageModel instanceof NotebookPageModel) {
			this.pageModel = pageModel;
		}
	}

	public Component getComponent() {
		return this;
	}
/*
	public static List getListSelectionListeners() {
		return listSelectionListeners;
	}

	public static List getPBDetailContainerListeners() {
		return pBDetailContainerListeners;
	}
*/
	/**
	 * @return the pageEditable
	 */
	public boolean isPageEditable() {
		return pageModel.isEditable();
	}

	public  PCeNBatch_SummaryViewContainer getPlateSummaryViewContainer() {
		return summaryBatchRackCont;
	}

	public void showTab(int i) {
		mainTabbedPane.setSelectedIndex(i);
		
	}
	
	public Map<String, BatchContainer> getBatchContainerCache() {
		return batchContainerCache ;
	}

	public PCeNTableView getCurrentBatchDetailsTableView() {
		return getBatchDetailsViewContainer().getCurrentTableView();
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
		{
			ArrayList batchesListList = lastStep.getProducts();
			for (int i=0; i< batchesListList.size();i++)
			{
				BatchesList tempBatchesList = (BatchesList)batchesListList.get(i);  
				if (tempBatchesList.getPosition().equals("PUA"))// User Added Batches. UAB
				{
					ArrayList batchModels = tempBatchesList.getBatchModels();
					if (batchModels.contains(productBatchModel))
						return true;
					else
						break;
				}
			}		
		}
		return false;
	}*/
}
