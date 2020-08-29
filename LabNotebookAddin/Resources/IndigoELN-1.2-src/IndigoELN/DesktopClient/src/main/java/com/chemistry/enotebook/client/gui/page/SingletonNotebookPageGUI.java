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
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PAnalyticalSampleRefContainer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PCeNAnalytical_BatchDetailsViewContainer;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.PCeNAnalytical_SummaryViewContainer;
import com.chemistry.enotebook.client.gui.page.attachements.AttachmentsContainer;
import com.chemistry.enotebook.client.gui.page.batch.*;
import com.chemistry.enotebook.client.gui.page.conception.GeneralSynthesisPathwayContainer;
import com.chemistry.enotebook.client.gui.page.conception.SPIDContainer;
import com.chemistry.enotebook.client.gui.page.conception.SummaryReactionContainer;
import com.chemistry.enotebook.client.gui.page.experiment.table.MedChemStoichCollapsiblePane;
import com.chemistry.enotebook.client.gui.page.reaction.*;
import com.chemistry.enotebook.client.gui.page.regis_submis.*;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchTypeException;
import com.chemistry.enotebook.experiment.datamodel.reaction.ReactionScheme;
import com.chemistry.viewer.ChemistryViewer;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonNotebookPageGUI extends CeNInternalFrame implements NotebookPageGuiInterface,
																		  ReactionSchemeModelChangeListener,
																		  ReactionSchemaStructureChangeListener 
{
	private static final long serialVersionUID = 2787150238422678205L;
	
	private static final Log log = LogFactory.getLog(SingletonNotebookPageGUI.class);
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();

	// public static final Color BACKGROUND_COLOR = new Color(122, 194, 174);
	public static final String CONCEPTION_RECORD_TAB = "Conception Record";
	public static final String EXPERIMENTS_TAB = "Experiments";
	public static final String BATCHINFO_TAB = "Batches";
	public static final String QCANALYTICAL_TAB = "Analytical";
	public static final String REGISTRATION_TAB = "Registration & Submission";
	public static final String ATTACHMENTS_TAB = "Attachments";
	// batch info tab
	public static final String REACTION_PANE = "Reaction Scheme";
	public static final String BATCHSUMMARY_PANE = "Product Nbk Batch Summary";
	public static final String BATCHDETAILS_PANE = "Product Batch Details";
	public static final String SAMPLE_REFS_PANE = "Analytical Sample References";

	private CollapsiblePanes panesConceptionRecord = new CollapsiblePanes();
	private CollapsiblePanes panesStoich = new CollapsiblePanes();
	private CollapsiblePanes panesBatches = new CollapsiblePanes();
	private CollapsiblePanes panesAnalQC = new CollapsiblePanes();
	private CollapsiblePanes panesRegistration = new CollapsiblePanes();
	private CollapsiblePanes panesAttach = new CollapsiblePanes();
	// private JPanel jPanelAnalSub = new JPanel();
	private JTabbedPane mainTabbedPane = new JTabbedPane();
	private JScrollPane jScrollPaneConceptionRecord = new JScrollPane();
	private JScrollPane jScrollPaneExperiment = new JScrollPane();
	private JScrollPane jScrollPaneBatches = new JScrollPane();
	private JScrollPane jScrollPaneAnalQC = new JScrollPane();
	private JScrollPane jScrollPaneRegistration = new JScrollPane();
	private NotebookPageModel pageModel;
	private GeneralSynthesisPathwayContainer generalSysnthesisPathwayConainer;
	private SPIDContainer spidContainer;
	private SummaryReactionContainer summaryReactionContainer;
	private ChemistryViewer synthesisTargetStructuresContainer;
	private CollapsiblePane batchEditCollapsiblePane = new CollapsiblePane(BATCHDETAILS_PANE);
	// contents of experiment panes
	private ReactionDetailsContainer ReactionDetails_cont;
	private ReactionSchemaContainer reactionSchemaContainer_exp;
	private ProcedureContainer Proc_cont;
	// contents of Product batch panes
	private ReactionSchemaContainer reactionSchemeContainer_batch;
	BatchEditPanel batchEditPanel = null;
	
	private ReactionSchemaContainer reactionSchemaRegistration_cont;
	private SingletonRegistration_SummaryViewContainer singletonRegistration_SummaryViewContainer;
	//this will be retired
	private StructureVnVContainer regVnV_cont;
	private CompoundRegInfoContainer compReg_cont;
	private HazHandlingStorageContainer hazHandling_cont;

	// contents of submission panes
	private SampleSubSumContainer sampleSubSum_cont;
	private PlateCreationHandler mPlateCreationHandler = new PlateCreationHandler();
	private ReactionStepJTabPaneFactory rSchemaGUI;
	// contents of attachment pane
	private AttachmentsContainer attachements_cont;
	// Conception Record tab panes
	private CollapsiblePane spidLinkCollPane;
	private CollapsiblePane synthesisTargetCollPane;
	private CollapsiblePane synthesisPathwayCollPane;
	private CollapsiblePane summaryReactionSchemeCollPane;
	// experiment tab panes
	private CollapsiblePane reactionDetails_Coll_Pane;
	private CollapsiblePane reactionSchema_Coll_Pane;
	private CollapsiblePane stoic_cont_Coll_Pane;
	private CollapsiblePane proc_cont_Coll_Pane;
	// Product Batch Tab panes
	private CollapsiblePane reactionSchemaProdBatch_Pane = new CollapsiblePane(REACTION_PANE);

	// Analytical Tab panes
	private com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane reactionSchemaAnalytical_Pane = new com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane("Reaction Scheme");
	private com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane analyticalSummary_Pane = new com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane("Analytical Data Summary");
	private PCeNAnalytical_SummaryViewContainer paralleAnalyticalSummaryViewer;
	private ReactionSchemaContainer reactionSchemaAnalytical_cont;
	private PCeNAnalytical_BatchDetailsViewContainer analyticalPlateDetailsViewer;
	private com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane analyticalSampleRef_Pane = new com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane(SAMPLE_REFS_PANE);
	private PAnalyticalSampleRefContainer analyticalSampleRefContainer; 

	// Registration Tab panes
	private CollapsiblePane reactionSchemaRegistration_Pane = new CollapsiblePane("Reaction Scheme");
	private CollapsiblePane regBatchDetailsCollapsiblePane = new CollapsiblePane("Batch Details");
	//private StructureVnVContainer regVnV_cont; // = new StructureVnVContainer();
	//private CompoundRegInfoContainer compReg_cont; // = new CompoundRegInfoContainer();
	//private HazHandlingStorageContainer hazHandling_cont; // = new HazHandlingStorageContainer();
	private PCeNRegistration_BatchDetailsViewContainer compoundRegistrationPlateDetailsViewer;
	// vb 2/12
	private RegistrationBatchDetailPanel regBatchDetailsPanel;
	// GUI for plate viewer
	// private PlateDetailsViewer plateViewerGUI;
	// private RegSubHandler regSubHandler;
	private boolean closing;
	private MedChemStoichCollapsiblePane mPCeNExperiment_StoicTableContainer;
	private CollapsiblePane summaryCollapsiblePane = new CollapsiblePane("Batch Racks/Plates Summary");
	private PCeNBatch_BatchDetailsViewContainer batchDetailsViewContainer; 
	private CompoundCreationHandler mCompoundCreationHandler = new CompoundCreationHandler();
	private PCeNBatch_SummaryViewContainer summaryBatchRackCont;
	private ArrayList<NotebookPageChangedListener> pageChangedListenerList = new ArrayList<NotebookPageChangedListener>();
	private Map<String, BatchContainer> batchContainerCache = new HashMap<String, BatchContainer>();

	
	
	public SingletonNotebookPageGUI(NotebookPageModel mNotebookPageModel) {
		
		this.pageModel = mNotebookPageModel;
		rSchemaGUI = new ReactionStepJTabPaneFactory(mPlateCreationHandler, this.pageModel);
		batchEditPanel = new BatchEditPanel(pageModel, mCompoundCreationHandler,this);
		regBatchDetailsPanel = new RegistrationBatchDetailPanel(this.pageModel, this.pageModel.isParallelExperiment());
		log.debug("Before initGUI() pageModel modified:"+this.pageModel.isModelChanged());
		boolean pageModelChanged = this.pageModel.isModelChanged();
		initGUI();
		log.debug("After initGUI() pageModel modified:"+this.pageModel.isModelChanged());
		this.pageModel.setModelChanged(pageModelChanged);
		log.debug("After restoring original flag pageModel modified:"+this.pageModel.isModelChanged());
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();

			String title = pageModel.getNotebookRefWithoutVersion() + "- Singleton Experiment" ;
			if (pageModel.getVersion() > 1 || pageModel.getLatestVersion() == "N") title += " v" + pageModel.getVersion();
			this.setTitle(title);
			this.setResizable(true);
			this.setSelected(true);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setClosable(true);
			this.setIconifiable(true);
			this.setMaximizable(true);
			this.setVisible(true);
			this.setPreferredSize(new java.awt.Dimension(681, 318));
			this.setBounds(new java.awt.Rectangle(0, 0, 681, 318));
			mainTabbedPane.setAutoscrolls(false);
			this.getContentPane().add(mainTabbedPane);
			mainTabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					jTabbedPaneMainStateChanged(evt);
					// below added by vb 12/17
					int i = mainTabbedPane.getSelectedIndex(); 
					if (i == 1) {  // BATCHINFO_TAB - make it work the same as Parallel
						batchEditPanel.batchSelectionChanged(null);        
						batchEditPanel.revalidate();
					}
					if (StringUtils.equals(mainTabbedPane.getTitleAt(i), REGISTRATION_TAB)) {
						if (compoundRegistrationPlateDetailsViewer != null) {
							compoundRegistrationPlateDetailsViewer.setBatchesSelected();
						}
					}
				}
			});
			jScrollPaneExperiment.setVisible(false);
			jScrollPaneExperiment.setBorder(new EmptyBorder(new Insets(0, 0, 0,	0)));
			jScrollPaneExperiment.add(panesStoich);
			jScrollPaneExperiment.setViewportView(panesStoich);
			mainTabbedPane.add(jScrollPaneExperiment);
			mainTabbedPane.setTitleAt(0, EXPERIMENTS_TAB);
			panesStoich.setBorder(null);
			jScrollPaneBatches.add(panesBatches);
			jScrollPaneBatches.setViewportView(panesBatches);
			mainTabbedPane.add(jScrollPaneBatches);
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
		jScrollPaneConceptionRecord.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPaneExperiment.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPaneRegistration.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPaneBatches.getVerticalScrollBar().setUnitIncrement(50);
		jScrollPaneAnalQC.getVerticalScrollBar().setUnitIncrement(50);
		// for Subject Title SPID link
		spidContainer = new SPIDContainer();
		spidLinkCollPane = new CollapsiblePane("Subject Title and SPID Link");
		spidLinkCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		spidLinkCollPane.setBackground(CeNConstants.BACKGROUND_COLOR);
		spidLinkCollPane.setSteps(1);
		spidLinkCollPane.setStepDelay(0);
		spidLinkCollPane.setContentPane(spidContainer);
		panesConceptionRecord.add(spidLinkCollPane);
		synthesisTargetStructuresContainer = new ChemistryViewer(MasterController.getGUIComponent().getTitle(), "Structure");
		synthesisTargetCollPane = new CollapsiblePane("Synthesis Target Structures & Description");
		synthesisTargetCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		synthesisTargetCollPane.setBackground(CeNConstants.BACKGROUND_COLOR);
		synthesisTargetCollPane.setSteps(1);
		synthesisTargetCollPane.setStepDelay(0);
		synthesisTargetCollPane.setContentPane(synthesisTargetStructuresContainer);
		panesConceptionRecord.add(synthesisTargetCollPane);
		generalSysnthesisPathwayConainer = new GeneralSynthesisPathwayContainer();
		synthesisPathwayCollPane = new CollapsiblePane("General Synthesis Pathway & Description");
		synthesisPathwayCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		synthesisPathwayCollPane.setBackground(CeNConstants.BACKGROUND_COLOR);
		synthesisPathwayCollPane.setSteps(1);
		synthesisPathwayCollPane.setStepDelay(0);
		synthesisPathwayCollPane.setContentPane(generalSysnthesisPathwayConainer);
		panesConceptionRecord.add(synthesisPathwayCollPane);
		summaryReactionContainer = new SummaryReactionContainer();
		summaryReactionSchemeCollPane = new CollapsiblePane("Summary Reaction Scheme & Reactant List");
		summaryReactionSchemeCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		summaryReactionSchemeCollPane.setBackground(CeNConstants.BACKGROUND_COLOR);
		summaryReactionSchemeCollPane.setSteps(1);
		summaryReactionSchemeCollPane.setStepDelay(0);
		summaryReactionSchemeCollPane.setContentPane(summaryReactionContainer);
		panesConceptionRecord.add(summaryReactionSchemeCollPane);
		panesConceptionRecord.addExpansion();
		// for ReactionDetails_cont
		// setup and show collapse pane for reaction_details
		ReactionDetails_cont = new ReactionDetailsContainer(pageModel);
		reactionDetails_Coll_Pane = new CollapsiblePane("Reaction Details");
		reactionDetails_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionDetails_Coll_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionDetails_Coll_Pane.setSteps(1);
		reactionDetails_Coll_Pane.setStepDelay(0);
		reactionDetails_Coll_Pane.setContentPane(ReactionDetails_cont);
		panesStoich.add(reactionDetails_Coll_Pane);
		// for ReactionSchemaContainer in EXP tab also pass the PageGUI reference.
		reactionSchemaContainer_exp = new ReactionSchemaContainer(false,pageModel.isEditable(),this);

		reactionSchemaContainer_exp.setReactionStep(pageModel.getSummaryReactionStep());
		reactionSchema_Coll_Pane = new CollapsiblePane("Reaction Scheme");
		reactionSchema_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchema_Coll_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchema_Coll_Pane.setSteps(1);
		reactionSchema_Coll_Pane.setStepDelay(0);
		reactionSchema_Coll_Pane.setContentPane(reactionSchemaContainer_exp);
		panesStoich.add(reactionSchema_Coll_Pane);
		// for stoic_cont
		stoic_cont_Coll_Pane = new CollapsiblePane("Stoichiometry Table");

		mPCeNExperiment_StoicTableContainer = new MedChemStoichCollapsiblePane(this.pageModel,this);
		stoic_cont_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		stoic_cont_Coll_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		stoic_cont_Coll_Pane.setSteps(1);
		stoic_cont_Coll_Pane.setStepDelay(0);
		 stoic_cont_Coll_Pane.setContentPane(mPCeNExperiment_StoicTableContainer);

		/** TODO Bo Yang 3/14/07
		 Stoic_cont = new StoichiometryTabContainer(this);
		 
		 stoic_cont_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		 stoic_cont_Coll_Pane.setBackground(BACKGROUND_COLOR);
		 stoic_cont_Coll_Pane.setSteps(1);
		 stoic_cont_Coll_Pane.setStepDelay(0);
		 stoic_cont_Coll_Pane.setContentPane(Stoic_cont);
		 */
		panesStoich.add(stoic_cont_Coll_Pane);
		// for Map Reactant Monomer List
		// setup and show collapse pane for reaction_details
		//This panel is not required.
		/*
		mapReactantContainer = new MapReactantContainer();
		mapReactantCollPane = new CollapsiblePane("Materials");
		mapReactantCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		mapReactantCollPane.setBackground(CeNConstants.BACKGROUND_COLOR);
		mapReactantCollPane.setSteps(1);
		mapReactantCollPane.setStepDelay(0);
		mapReactantCollPane.setContentPane(mapReactantContainer);
		panesStoich.add(mapReactantCollPane);
		*/
		// for procedure_cont
		Proc_cont = new ProcedureContainer(pageModel, mCompoundCreationHandler,false);
		proc_cont_Coll_Pane = new CollapsiblePane("Reaction & Workup Procedure");
		proc_cont_Coll_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		proc_cont_Coll_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		proc_cont_Coll_Pane.setContentPane(Proc_cont);
		proc_cont_Coll_Pane.setSteps(1);
		proc_cont_Coll_Pane.setStepDelay(0);
		panesStoich.add(proc_cont_Coll_Pane);
		// for Stepwise Experiments
		/*stepwiseExperimentContainer = new StepwiseExperimentsContainer();
		//stepwiseExperimentsCollPane = new CollapsiblePane("Stepwise Experiments (remove)");
		
		
		stepwiseExperimentsCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		stepwiseExperimentsCollPane.setBackground(BACKGROUND_COLOR);
		stepwiseExperimentsCollPane.setSteps(1);
		stepwiseExperimentsCollPane.setStepDelay(0);
		stepwiseExperimentsCollPane.setContentPane(stepwiseExperimentContainer);
		panesStoich.add(stepwiseExperimentsCollPane);
		
		 */
		// for View & Load
		// setup and show collapse pane for reaction_details
		/*
	
		viewLoadContainer = new ViewLoadContainer();
		//viewLoadCollPane = new CollapsiblePane("View & Load (remove)");
		viewLoadCollPane.setStyle(CollapsiblePane.TREE_STYLE);
		viewLoadCollPane.setBackground(BACKGROUND_COLOR);
		viewLoadCollPane.setSteps(1);
		viewLoadCollPane.setStepDelay(0);
		viewLoadCollPane.setContentPane(viewLoadContainer);
		panesStoich.add(viewLoadCollPane);
		 */
		panesStoich.addExpansion();

//		Batches Tab
		reactionSchemaProdBatch_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaProdBatch_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchemaProdBatch_Pane.setSteps(1);
		reactionSchemaProdBatch_Pane.setStepDelay(0);
		ReactionSchemaContainer reactionSchemaContainer_bat = new ReactionSchemaContainer(false, false, this);
		reactionSchemaContainer_bat.setReactionStep(pageModel.getSummaryReactionStep());
		reactionSchemaProdBatch_Pane.setContentPane(reactionSchemaContainer_bat);
		panesBatches.add(reactionSchemaProdBatch_Pane);
	
		// batch summary viewer
		summaryBatchRackCont = new PCeNBatch_SummaryViewContainer(pageModel);
		summaryCollapsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		summaryCollapsiblePane.setBackground(CeNConstants.BACKGROUND_COLOR);
		summaryCollapsiblePane.setSteps(1);
		summaryCollapsiblePane.setStepDelay(0);
		summaryCollapsiblePane.setContentPane(summaryBatchRackCont);
		//panesBatches.add(summaryCollapsiblePane);
		mPlateCreationHandler.addPlateCreatedEventListener(summaryBatchRackCont);
		mCompoundCreationHandler.addCompoundCreatedEventListener(summaryBatchRackCont);
      
		// batch details viewer
		batchDetailsViewContainer = new PCeNBatch_BatchDetailsViewContainer(pageModel, 
		                                                                    this.batchEditPanel,
		                                                                    mCompoundCreationHandler,
		                                                                    mPlateCreationHandler, 
		                                                                    summaryBatchRackCont, 
		                                                                    BATCHSUMMARY_PANE);
		panesBatches.add(batchDetailsViewContainer.getPlateViewer());
		mCompoundCreationHandler.addCompoundCreatedEventListener(batchDetailsViewContainer);
//		summaryBatchRackCont.setPlateSelectionChangedListener(batchDetailsViewContainer);
		mPlateCreationHandler.addPlateCreatedEventListener(batchDetailsViewContainer);
		
		// batch detail edit panel
		batchEditCollapsiblePane.setStyle(CollapsiblePane.TREE_STYLE);
		batchEditCollapsiblePane.setBackground(CeNConstants.BACKGROUND_COLOR);
		batchEditCollapsiblePane.setSteps(1);
		batchEditCollapsiblePane.setStepDelay(0);
		batchEditCollapsiblePane.setContentPane(batchEditPanel);
		panesBatches.add(batchEditCollapsiblePane);
		panesBatches.addExpansion();
		
		batchEditPanel.setCompoundCreateInterface(batchDetailsViewContainer);
		
		// add batch detail edit panel to the plate selection changed listeners
		summaryBatchRackCont.setPlateSelectionChangedListener(batchEditPanel);
		//long d = System.currentTimeMillis();
		//if (log.isInfoEnabled()) 
		//	log.info("Batch TabConstruction : " + (d-c) + " ms");


		// // Analytical Tab

		reactionSchemaAnalytical_cont = new ReactionSchemaContainer(false, false, this);
		reactionSchemaAnalytical_cont.setReactionStep(pageModel.getSummaryReactionStep());
		reactionSchemaAnalytical_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaAnalytical_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchemaAnalytical_Pane.setSteps(1);
		reactionSchemaAnalytical_Pane.setStepDelay(0);
		reactionSchemaAnalytical_Pane.getContentPane().setLayout(new BorderLayout());
		// add reaction schema container instead of Intermediate Steps TabbedPane
		reactionSchemaAnalytical_Pane.getContentPane().add(reactionSchemaAnalytical_cont);
		// rSchemaGUI = new ReactionSchemaGUIBuilder(pageModel, analitPanel, -1);
		panesAnalQC.add(reactionSchemaAnalytical_Pane);
		//summary viewer ( to show two buttons)
		paralleAnalyticalSummaryViewer = new PCeNAnalytical_SummaryViewContainer(pageModel);
		analyticalSummary_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		analyticalSummary_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		analyticalSummary_Pane.setSteps(1);
		analyticalSummary_Pane.setStepDelay(0);
		analyticalSummary_Pane.setContentPane(paralleAnalyticalSummaryViewer);
		panesAnalQC.add(analyticalSummary_Pane);
				
		// for analytical summary pane
		analyticalSampleRefContainer = new PAnalyticalSampleRefContainer(pageModel);

		analyticalPlateDetailsViewer = new PCeNAnalytical_BatchDetailsViewContainer(pageModel, analyticalSampleRefContainer);
		
		paralleAnalyticalSummaryViewer.addDownstreamAnalyticalChangeListener(analyticalPlateDetailsViewer);
		// vb 6/4 The detail container has to listen for the global analytical events that occur at the summary level (advancedLinkAll and linkAll).
		mPlateCreationHandler.addPlateCreatedEventListener(this.analyticalPlateDetailsViewer);
		panesAnalQC.add(analyticalPlateDetailsViewer.getPlateViewer());
		mCompoundCreationHandler.addCompoundCreatedEventListener(analyticalPlateDetailsViewer);
		
		analyticalSampleRef_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		analyticalSampleRef_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		analyticalSampleRef_Pane.setSteps(1);
		analyticalSampleRef_Pane.setStepDelay(0);
		analyticalSampleRef_Pane.setContentPane(analyticalSampleRefContainer);
		panesAnalQC.add(analyticalSampleRef_Pane);
		
		panesAnalQC.addExpansion();

		// Setup and show collapse pane for reaction schema on Registration TAB
		reactionSchemaRegistration_Pane.setStyle(CollapsiblePane.TREE_STYLE);
		reactionSchemaRegistration_Pane.setBackground(CeNConstants.BACKGROUND_COLOR);
		reactionSchemaRegistration_Pane.setSteps(1);
		reactionSchemaRegistration_Pane.setStepDelay(0);
		ReactionSchemaContainer reactionSchemaContainer_reg = new ReactionSchemaContainer(false, false, this);
		reactionSchemaContainer_reg.setReactionStep(pageModel.getSummaryReactionStep());
		reactionSchemaRegistration_Pane.setContentPane(reactionSchemaContainer_reg);

		panesRegistration.add(reactionSchemaRegistration_Pane);
		
		//summary viewer - Not required for Singleton exp details viewer
		compoundRegistrationPlateDetailsViewer = new PCeNRegistration_BatchDetailsViewContainer(pageModel, this.regBatchDetailsPanel);  
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

		mCompoundCreationHandler.addCompoundCreatedEventListener(compoundRegistrationPlateDetailsViewer);
		
		// setup and show pane for Attachments
		attachements_cont = new AttachmentsContainer(pageModel);
		panesAttach.add(attachements_cont);
		// pass reference to child components
		
		try {
			reactionSchemaAnalytical_Pane.setCollapsed(true);
			reactionSchemaRegistration_Pane.setCollapsed(true);
		} catch (Exception e) {
			ceh.logExceptionMsg(this, e);
		}
		// Batches Update
		mainTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int i = mainTabbedPane.getSelectedIndex();
//				if (i == 1) {
//				// productBatchTableView.refresh();
//				} else if (i == 2) {
//					// analyticalSummary_cont.refresh();
//				} else if (i == 3) {
//					//singletonRegistration_SummaryViewContainer.refresh();
//				} else 
					if (i == 0) {
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
					SingletonNotebookPageGUI.this.internalFrameClosing();
				else if (SingletonNotebookPageGUI.this.isEnabled())
					closing = false;
			}

			public void internalFrameClosed(InternalFrameEvent e) {
				MasterController.getGUIComponent().refreshIcons();
			}
		});
		
		//enable menu appropriately. This is handled by refreshIcons()
		//if(this.pageModel != null && this.pageModel.isModelChanged())
		//{
		//MasterController.getInstance().getGUIComponent().enableSaveButtons();
		//}
	}

	/*
	 * TODO Bo Yang 3/14/07 public boolean shouldClose() { boolean result =
	 * true; if (this.pageModel.isChanging()) {
	 * JOptionPane.showMessageDialog(this, "Notebook page " +
	 * this.pageModel.getNotebookRefAsString() + " is undergoing state change,
	 * \n Please wait for a moment and try again", "Page State Change",
	 * JOptionPane.PLAIN_MESSAGE); return false; } if (pageModel != null &&
	 * pageModel.isSaving() && this.isEnabled()) { int selection =
	 * JOptionPane.showConfirmDialog(this, "The Experiment Page " +
	 * pageModel.getNotebookRefAsString() + " is still saving. Do you wish to
	 * close anyway ?", "Background Save in Progress",
	 * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); if (selection ==
	 * JOptionPane.NO_OPTION) result = false; } else if (pageModel != null &&
	 * pageModel.isPageEditable() && pageModel.isModelChanged() &&
	 * this.isEnabled()) { int selection = JOptionPane.showConfirmDialog(this,
	 * "Save changes to " + pageModel.getNotebookRefAsString() + "?", "Confirm
	 * Save", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE); if
	 * (selection == JOptionPane.CANCEL_OPTION) { result = false; } else if
	 * (selection == JOptionPane.YES_OPTION) { // Check the Storage Service
	 * backend is working properly boolean serviceOK =
	 * HealthCheckHandler.checkServiceHealth(HealthCheckHandler.STORAGE_SERVICE);
	 * if (!serviceOK) { if (JOptionPane.showConfirmDialog(this, "The Storage
	 * Service is not currently available, you will be unable to save experiment " +
	 * pageModel.getNotebookRefAsString() + " to the database.\n" + " Do you
	 * wish to Save your latest changes to the local disk ?", "Storage Service
	 * Unavailable", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) ==
	 * 0) try {
	 * MasterController.getGuiController().getPageCache().saveNotebookPage(pageModel,
	 * true); result = true; } catch (NotebookDelegateException e) {
	 * JOptionPane.showMessageDialog(this, "Unable to AutoSave this
	 * experiment.", "AutoSave Error", JOptionPane.ERROR_MESSAGE); result =
	 * true; } } else { result = saveExperiment(); } } else { try {
	 * MasterController.getGuiController().getPageCache().deleteAutoSaveFiles(pageModel.getNotebookRef()); }
	 * catch (NotebookDelegateException e) { ceh.logExceptionMsg(null, e); }
	 * result = true; } } else if (pageModel.isSaving()) result = false; return
	 * result; }
	 */

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

	/** Auto-generated main method */
//	public static void main(String[] args) {
//		showGUI();
//	}

	/**
	 * This static method creates a new instance of this class and shows it
	 * inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and
	 * used as a basis for your own code. * It is auto-generated code - the body
	 * of this method will be re-generated after any changes are made to the
	 * GUI. However, if you delete this method it will not be re-created.
	 */
//	public static void showGUI() {
//		try {
//			javax.swing.JFrame frame = new javax.swing.JFrame();
//			NotebookPageGUI inst = new NotebookPageGUI();
//			javax.swing.JDesktopPane jdp = new javax.swing.JDesktopPane();
//			jdp.add(inst);
//			jdp.setPreferredSize(inst.getPreferredSize());
//			frame.setContentPane(jdp);
//			frame.getContentPane().setSize(inst.getSize());
//			frame
//					.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//			frame.pack();
//			frame.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/** Auto-generated event handler method */
	protected void jTabbedPaneMainStateChanged(ChangeEvent evt) {
		JTabbedPane pane = (JTabbedPane) evt.getSource();
		// Get current tab
		for (int c = 0; c <= pane.getTabCount() - 1; c++) {
			pane.setBackgroundAt(c, Color.LIGHT_GRAY);
		}
		pane.setBackgroundAt(pane.getSelectedIndex(), Color.WHITE);
	}

	/**
	 * @return pageModel The pageModel to set/get.
	 */
	public NotebookPageModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(NotebookPageModel pageModelpojo) {
		this.pageModel = pageModelpojo;

		// System.out.println("dfdfsdfdfg"+pageModelPojo.getSummaryReactionStep().getRxnScheme());
		/*
		 * if(pageModel instanceof NotebookPage) { this.pageModel =
		 * (NotebookPage)pageModel; if (this.pageModel != null) {
		 * this.pageModel.addObserver(this); this.refreshPage(); } }
		 */
	}

	/**
	 * method refreshes the current page with its model TODO fill this method
	 */
	/**
	 * TODO Bo Yang 3/14/07 public void refreshPage() { if (pageModelPojo !=
	 * null) { String str = pageModelPojo.getNbRef().getNbRef(); if
	 * (pageModelPojo.getVersion() > 1 || pageModelPojo.getLatestVersion() ==
	 * "N") str += " v" + pageModelPojo.getVersion(); setTitle(str);
	 * setName(str); } ReactionDetails_cont.setPageModel(pageModelPojo);
	 * ReactionSchema_cont.setPageModel(pageModelPojo); if
	 * (Stoic_cont.getPageModel() != pageModelPojo) // Takes a hit on the lookup
	 * of compound info. Stoic_cont.setPageModel(pageModelPojo,
	 * ReactionSchema_cont); else // Simply refreshes the view based on model
	 * Stoic_cont.refresh(); Proc_cont.setPageModel(pageModelPojo);
	 * ReactionSchemaProdBatch_cont.setPageModel(pageModelPojo);
	 * BatchInfo_cont.setPageModel(pageModelPojo, ReactionSchema_cont); //
	 * Stoic_cont.refresh();
	 * ReactionSchemaAnalytical_cont.setPageModel(pageModelPojo);
	 * AnalyticalSummary_cont.setPageModel(pageModelPojo);
	 * reactionSchemaRegistration_cont.setPageModel(pageModelPojo);
	 * regSubSum_cont.setPageModel(pageModelPojo);
	 * attachements_cont.setPageModel(pageModelPojo); }
	 */
	/**
	 * TODO Bo Yang 3/14/07 public void update(Observable watchedObj, Object
	 * obj) { // arg represents whatever was passed to the notifyObservers()
	 * method. if (watchedObj instanceof NotebookPage) { pageModel =
	 * (NotebookPage) watchedObj; if (pageModel.isModelChanged())
	 * MasterController.getGUIComponent().refreshIcons(); if (obj instanceof
	 * NotebookPage) fireDetailsChanged(); if (obj instanceof ReactionScheme)
	 * fireReactionChanged((ReactionScheme) obj); } }
	 */
	
	public void refreshPage() {
		Proc_cont.refreshPageModel(pageModel);
		
		summaryBatchRackCont.refreshPageModel(pageModel);
		batchDetailsViewContainer.refreshPageModel(pageModel);
		batchEditPanel.refreshPageModel(pageModel);
		
		paralleAnalyticalSummaryViewer.refreshPageModel(pageModel);
		analyticalSampleRefContainer.refreshPageModel(pageModel);
		
		compoundRegistrationPlateDetailsViewer.refreshPageModel(pageModel);
		
		attachements_cont.refreshPageModel(pageModel);
		
		regBatchDetailsPanel.refreshPageModel(pageModel);
		
	}
	
	/**
	 * @return Returns the regSubSum_cont.
	 */
	public SingletonRegistration_SummaryViewContainer getRegSubSum_cont() {
		return singletonRegistration_SummaryViewContainer;
	}

	/**
	 * @param regSubSum_cont
	 *            The regSubSum_cont to set.
	 */
	public void setRegSubSum_cont(SingletonRegistration_SummaryViewContainer regSubSum_cont) {
		this.singletonRegistration_SummaryViewContainer = regSubSum_cont;
	}

	/**
	 * @return Returns the regVnV_cont.
	 */
	public StructureVnVContainer getRegVnV_cont() {
		return regVnV_cont;
	}

	/**
	 * @param regVnV_cont
	 *            The regVnV_cont to set.
	 */
	public void setRegVnV_cont(StructureVnVContainer regVnV_cont) {
		this.regVnV_cont = regVnV_cont;
	}

	/**
	 * @return Returns the compReg_cont.
	 */
	public CompoundRegInfoContainer getCompReg_cont() {
		return compReg_cont;
	}

	/**
	 * @param compReg_cont
	 *            The compReg_cont to set.
	 */
	public void setCompReg_cont(CompoundRegInfoContainer compReg_cont) {
		this.compReg_cont = compReg_cont;
	}

	/**
	 * @return Returns the hazHandling_cont.
	 */
	public HazHandlingStorageContainer getHazHandling_cont() {
		return hazHandling_cont;
	}

	/**
	 * @param hazHandling_cont
	 *            The hazHandling_cont to set.
	 */
	public void setHazHandling_cont(HazHandlingStorageContainer hazHandling_cont) {
		this.hazHandling_cont = hazHandling_cont;
	}

	public ReactionSchemaContainer getReactionSchemaProdBatch_cont() {
		return reactionSchemeContainer_batch;
	}

	public ReactionSchemaContainer getReactionSchemaAnalytical_cont() {
		return reactionSchemaAnalytical_cont;
	}

	public ReactionSchemaContainer getReactionSchemaRegistration_cont() {
		return reactionSchemaRegistration_cont;
	}

	/**
	 * @param arg0
	 */
	public void setSelectedIndex(int arg0) {
		mainTabbedPane.setSelectedIndex(arg0);
	}

	public void fireReactionChanged(ReactionScheme scheme) {
		NotebookPageChangedEvent event = new NotebookPageChangedEvent(this,
				scheme);
		for (int i = pageChangedListenerList.size() - 1; i >= 0; i--) {
			pageChangedListenerList.get(i).reactionChanged(event);
		}
	}

	public void fireDetailsChanged() {
		NotebookPageChangedEvent event = new NotebookPageChangedEvent(this,
		                                                              getPageModel());
		for (int i = pageChangedListenerList.size() - 1; i >= 0; i--) {
			pageChangedListenerList.get(i).detailsChanged(event);
		}
	}

	public void addNotebookPageChangedListener(NotebookPageChangedListener listener) {
		if (listener != null && !pageChangedListenerList.contains(listener)) {
			pageChangedListenerList.add(listener);
		}
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
		ActionMap actionMap = ((ActionMap) UIManager.getLookAndFeelDefaults().get("InternalFrame.actionMap"));
		if (actionMap != null)
			actionMap.remove("showSystemMenu");
		// BatchInfo_cont.dispose();
		reactionSchemaContainer_exp.dispose();
		if(reactionSchemeContainer_batch != null ) {
			reactionSchemeContainer_batch.dispose();
			reactionSchemeContainer_batch = null;
		}
		if(reactionSchemaAnalytical_cont != null ) {
			reactionSchemaAnalytical_cont.dispose();
			reactionSchemaAnalytical_cont = null;
		}
		if(reactionSchemaRegistration_cont != null ) {
			reactionSchemaRegistration_cont.dispose();
			reactionSchemaRegistration_cont = null;
		}
		if(pageChangedListenerList != null) {
			pageChangedListenerList.clear();
			pageChangedListenerList = null;
		}
		if (batchContainerCache != null) {
			batchContainerCache.clear();
			batchContainerCache = null;
		}
	}

	/**
	 * @return Returns the reactionSchema_cont.
	 */
	// public ReactionSchemaContainer getReactionSchema_cont() {
	// return reactionSchemaContainer;
	// }
	/**
	 * @return Returns the proc_cont.
	 */
	public ProcedureContainer getProc_cont() {
		return Proc_cont;
	}

	public void setClosing(boolean close) {
		this.closing = close;
	}

	public boolean isClosing() {
		return this.closing;
	}

	public Component getComponent() {
		return this;
	}

	/**
	 * @return the pageEditable
	 */
	public boolean isPageEditable() {
		return pageModel.isEditable();
	}
	
	//This is called when Rxn is created from "Create Rxn" menu selection and Rxn is set to Model.
	//This notifies the RxnScemaContainers to load the CANVAS with Rxn from ReactionScheme to reflect latest
	public void reactionSchemeModelChanged(ReactionSchemeModelChangeEvent structureevent)
	/**** this method needs perfect testing. reason is few lines are commented that gets implicitly called by reactionSchemeModelChangedOnExpTab()***/
	{
        //System.out.println("reactionSchemeModelChanged: ");
		//Notify all ReactionScheme Containers in each tab 
		if(reactionSchemaContainer_exp != null ) {
			reactionSchemaContainer_exp.reactionSchemeModelChanged(structureevent); //This method implicitly calls reactionSchemeModelChangedOnExpTab() to get the data.
		}
/*		if(reactionSchemeContainer_batch != null )
		reactionSchemeContainer_batch.reactionSchemeModelChanged(structureevent);
		if(reactionSchemaAnalytical_cont != null )
		reactionSchemaAnalytical_cont.reactionSchemeModelChanged(structureevent);
		if(reactionSchemaRegistration_cont != null )
		reactionSchemaRegistration_cont.reactionSchemeModelChanged(structureevent);
*/	}
	
	//This is called when structure is edited in a ReactionSchemaContainer.This is similar
	//to structureChanged() method provided by Canvas.
	public void reactionSchemaStructureChanged(ReactionSchemaStructureChangeEvent structureevent)
	{
		//To avoid the call during Exp load
		if(mPCeNExperiment_StoicTableContainer != null)
		{
			mPCeNExperiment_StoicTableContainer.syncProductsWithReactionScheme();
			mPCeNExperiment_StoicTableContainer.updateAnalyzeAndCreateButtonsEnabledStatus();
			batchDetailsViewContainer.updateSyncWithIntendedProductsActionState();
		}
	}

	public void reactionSchemeModelChangedOnExpTab(ReactionSchemeModelChangeEvent structureevent)
	{
		//To avoid the call during Exp load
		if (mPCeNExperiment_StoicTableContainer != null) {
			// Notify all ReactionScheme Containers in each tab, But not to the Exp tab.
			if (reactionSchemeContainer_batch != null)
				reactionSchemeContainer_batch.reactionSchemeModelChanged(structureevent);
			if (reactionSchemaAnalytical_cont != null)
				reactionSchemaAnalytical_cont.reactionSchemeModelChanged(structureevent);
			if (reactionSchemaRegistration_cont != null)
				reactionSchemaRegistration_cont.reactionSchemeModelChanged(structureevent);
		}
	}

	
	public boolean isUserAddedBatch(ProductBatchModel productBatchModel) {
		BatchType userAddedBatchType = null;
		try {
			userAddedBatchType = BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL);
		} catch (InvalidBatchTypeException e) {
			log.error("Problem creating an actual batch", e);
		}
		BatchType batchType = productBatchModel.getBatchType();
		if (batchType.compareTo(userAddedBatchType) == 0)
			return true;
		else
			return false;
	}

	public PCeNBatch_BatchDetailsViewContainer getBatchDetailsViewContainer() {
		return batchDetailsViewContainer;
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
}
