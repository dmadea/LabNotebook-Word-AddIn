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
package com.chemistry.enotebook.client.gui.page.analytical.parallel;


import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalChangeListener;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalDataSummaryToolbar;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.summary.PCeNAnalytical_SummaryTableView;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.summary.PCeNAnalytical_SummaryViewControllerUtility;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationHandler;
import com.chemistry.enotebook.client.gui.page.batch.events.*;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.PseudoProductPlate;
import com.chemistry.enotebook.utils.DefaultPreferences;
import com.chemistry.enotebook.utils.ProductBatchFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PCeNAnalytical_SummaryViewContainer extends JPanel implements PlatesCreatedEventListener, CompoundCreatedEventListener, AnalyticalChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2049516229945927986L;

	public static final Log log = LogFactory.getLog(PCeNAnalytical_SummaryViewContainer.class);
	
	private PCeNTableModel model;
	// private ParallelCeNBatchInfoController batchInfoController;
	private NotebookPageModel pageModel;
	private PCeNAnalytical_SummaryViewControllerUtility controllerUtil;
	//private PCeNTableModelConnector controllerUtil;
	// private JTabbedPane plateTabPane;
	// private JTabbedPane rackTabPane;
	// private PlateViewerGUIRenderer plateGUIRenderer;
	private PCeNAnalytical_SummaryTableView summaryTableView;
	private ProductBatchFilter productBatchFilter = null;  // vb 7/25
	private AnalyticalChangeListener analyticalChangeListener;
	private AnalyticalDataSummaryToolbar toolbar ;

	public PCeNAnalytical_SummaryViewContainer(NotebookPageModel pageModel) {
		this.setLayout(new BorderLayout());
		this.pageModel = pageModel;
		this.productBatchFilter = new ProductBatchFilter(pageModel);
		// batchInfoController = new ParallelCeNBatchInfoController(pageModel);
		// create plate summary table
		controllerUtil = new PCeNAnalytical_SummaryViewControllerUtility(pageModel.getAllProductPlatesAndRegPlates(), pageModel);
		// vb 7/20
		////////// vb 7/25 ArrayList batchModels = (ArrayList) this.pageModel.getAllProductBatchModelsInThisPage();
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();	
		controllerUtil.replacePseudoProductPlate(pseudoPlate);		
		model = new PCeNTableModel(controllerUtil);
		summaryTableView = new PCeNAnalytical_SummaryTableView(model, DefaultPreferences.SHORT_ROW_HEIGHT, controllerUtil);
		JScrollPane sPane = new JScrollPane(summaryTableView);
		summaryTableView.setPreferredScrollableViewportSize(new Dimension(CeNConstants.TABLE_VIEW_WIDTH,
				CeNConstants.TABLE_VIEW_ROW_HIGHT * 3));
		summaryTableView.hideViewColumn();
		// batchInfoController.setSummaryTableView(summaryTableView);
		// vb 7/17 tool bar above table view
		toolbar = new AnalyticalDataSummaryToolbar(pageModel, this);
		this.add(toolbar.getPanel(), BorderLayout.NORTH);
		if(this.pageModel.isParallelExperiment())
		{
		this.add(sPane, BorderLayout.CENTER);
		}
		enableComponents();
	}

	private void enableComponents() {
		boolean editable = pageModel.isEditable();
		toolbar.refresh();
	}

	public PCeNAnalytical_SummaryViewContainer(NotebookPageModel pageModelPojo,
			CompoundCreationHandler compoundCreationHandler) {

	}

	/**
	 * @return the model
	 */
	public PCeNTableModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(PCeNTableModel model) {
		this.model = model;
	}
	
	public PCeNAnalytical_SummaryTableView getSummaryTableView() {
		return this.summaryTableView;
	}
	
	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		long startTime = System.currentTimeMillis();
		List plates = event.getPlates();
		// add newly created plates to list with existing plates for a Reaction
		// Step
		// ((ReactionStepModel) pageModel.getReactionSteps().get(event.getStepIndex())).addProductPlates(plates);
		// update sumary table
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();	
		controllerUtil.replacePseudoProductPlate(pseudoPlate);		
		controllerUtil.addProductPlates(plates);
		summaryTableView.setRowHeight(20);
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("newProductPlatesCreated execution time: " + (endTime - startTime) + " ms");
		}
	}

	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event) {
		controllerUtil.removeProductPlates(event.getPlates());
		model.fireTableDataChanged();
	}

	public void newMonomerPlatesCreated(MonomerBatchPlateCreatedEvent event) {
	}

	public void monomerPlatesRemoved(MonomerBatchPlateCreatedEvent event) {
	}

	public void newRegisteredPlatesCreated(RegisteredPlateCreatedEvent event) {

	}

	public void registeredPlatesRemoved(RegisteredPlateCreatedEvent event) {

	}

	public void setPlateSelectionChangedListener(PlateSelectionChangedListener plateSelectionChangedListener) {
		PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
		PCeNAnalytical_SummaryViewControllerUtility summaryConnector = (PCeNAnalytical_SummaryViewControllerUtility) summaryModel
				.getConnector();
		summaryConnector.addPlateSelectionChangedListener(plateSelectionChangedListener);
	}
	/**
	 * @return the plateGUIRenderer
	 */
	// public PlateViewerGUIRenderer getPlateGUIRenderer() {
	// return plateGUIRenderer;
	// }
	/**
	 * @param plateGUIRenderer
	 *            the plateGUIRenderer to set
	 */
	// public void setPlateGUIRenderer(PlateViewerGUIRenderer plateGUIRenderer) {
	// this.plateGUIRenderer = plateGUIRenderer;
	// }
	/**
	 * @return the summaryTableView
	 */
	// public PcenPlateSummaryTableView getSummaryTableView() {
	// return summaryTableView;
	// }
	/**
	 * @param summaryTableView
	 *            the summaryTableView to set
	 */
	// public void setSummaryTableView(PcenPlateSummaryTableView summaryTableView) {
	// this.summaryTableView = summaryTableView;
	// }

	public void compoundRemoved(CompoundCreationEvent event) {
		PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
		//PCeNAnalytical_SummaryViewControllerUtility summaryConnector = (PCeNAnalytical_SummaryViewControllerUtility) summaryModel
				//.getConnector();
		//summaryConnector.removeBatchFromPseudoPlate((ProductBatchModel)event.getBatch());
		summaryModel.fireTableDataChanged();
		summaryTableView.setRowHeight(30);	
	}

	public void compoundUpdated(CompoundCreationEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void newCompoundCreated(CompoundCreationEvent event) {
	PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
/*	PCeNAnalytical_SummaryViewControllerUtility summaryConnector = (PCeNAnalytical_SummaryViewControllerUtility) summaryModel
			.getConnector();
	summaryConnector.addBatchToPseudoPlate((ProductBatchModel)event.getBatch());
*/	summaryModel.fireTableDataChanged();
	summaryTableView.setRowHeight(20);	
	}

	public void addDownstreamAnalyticalChangeListener(AnalyticalChangeListener listener) {
		this.analyticalChangeListener = listener;
	}
	
	public void updateAnalyses() {
		if (this.analyticalChangeListener != null)
			this.analyticalChangeListener.updateAnalyses();
//		System.out.println();
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		if (pageModel != pageModel2)
			this.pageModel = pageModel2; 
		enableComponents();
	}
}
