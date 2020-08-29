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

import com.chemistry.enotebook.client.gui.page.batch.events.*;
import com.chemistry.enotebook.client.gui.page.batch.table.PCeNBatch_SummaryTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.domain.PseudoProductPlate;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class PCeNBatch_SummaryViewContainer extends JPanel implements PlatesCreatedEventListener, CompoundCreatedEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3693288220081406389L;
	private PCeNTableModel model;
	private NotebookPageModel pageModel;
	private PCeNBatch_SummaryViewControllerUtility controllerUtil;
	private PCeNBatch_SummaryTableView summaryTableView;
	  
	

	public PCeNBatch_SummaryViewContainer(NotebookPageModel pageModel) {
		this.setLayout(new BorderLayout());
		this.pageModel = pageModel;
		// batchInfoController = new ParallelCeNBatchInfoController(pageModel);
		// create plate summary table
		controllerUtil = new PCeNBatch_SummaryViewControllerUtility(pageModel.getAllProductPlatesAndRegPlates(), pageModel);
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();	
		controllerUtil.replacePseudoProductPlate(pseudoPlate);		
		model = new PCeNTableModel(controllerUtil);
		summaryTableView = new PCeNBatch_SummaryTableView(model, DefaultPreferences.SHORT_ROW_HEIGHT, controllerUtil);
		JScrollPane sPane = new JScrollPane(summaryTableView);
		summaryTableView.setPreferredScrollableViewportSize(new Dimension(CeNConstants.TABLE_VIEW_WIDTH,
				CeNConstants.TABLE_VIEW_ROW_HIGHT * 3));
		// Remove(Hide) view column
		summaryTableView.hideViewColumn();
		// batchInfoController.setSummaryTableView(summaryTableView);
		this.add(sPane, BorderLayout.CENTER);
	}

	public void dispose() {
		this.controllerUtil.dispose();
		this.summaryTableView.dispose();
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

	public PCeNBatch_SummaryTableView getSummaryTableView() {
		return this.summaryTableView;
	}
	
	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		List<ProductPlate> plates = event.getPlates();
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();	
		controllerUtil.replacePseudoProductPlate(pseudoPlate);
		controllerUtil.addProductPlates(plates);
		summaryTableView.setRowHeight(20);
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
		PCeNBatch_SummaryViewControllerUtility summaryConnector = (PCeNBatch_SummaryViewControllerUtility) summaryModel
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
		//PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
		//PCeNBatch_SummaryViewControllerUtility summaryConnector = (PCeNBatch_SummaryViewControllerUtility) summaryModel
				//.getConnector();
		controllerUtil.replacePseudoProductPlate(pageModel.getGuiPseudoProductPlate());
		model.fireTableDataChanged();
		summaryTableView.setRowHeight(30);
	}

	/**
	 * No op 
	 */
	public void compoundUpdated(CompoundCreationEvent event) {
	}

	public void newCompoundCreated(CompoundCreationEvent event) {
//		PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
//		PCeNBatch_SummaryViewControllerUtility summaryConnector = (PCeNBatch_SummaryViewControllerUtility) summaryModel
//				.getConnector();
//		summaryConnector.addBatchToPseudoPlate((ProductBatchModel)event.getBatch());
//		summaryModel.fireTableDataChanged();
//		summaryTableView.setRowHeight(20);
		if (isNonPlatedBatchesSelected(controllerUtil.getSelectedPlates()) || this.pageModel.isSingletonExperiment() || this.pageModel.isConceptionExperiment()) {
			controllerUtil.replacePseudoProductPlate(pageModel.getGuiPseudoProductPlate());
			model.fireTableDataChanged();
		} else if (event.getSelectedViewItem() != null) {
			if (event.getSelectedViewItem().equals(PCeNBatch_BatchDetailsViewContainer.TABLES_VIEW)	|| event.getSelectedViewItem().equals(PCeNBatch_BatchDetailsViewContainer.PLATES_VIEW)) {
				controllerUtil.setSelectValue(controllerUtil.getProductPlates().size() - 1);
				model.fireTableDataChanged();
			}
		}
	}

	private boolean isNonPlatedBatchesSelected(List<ProductPlate> selectedPlates) {
		for (ProductPlate plate : selectedPlates)
		{
			if (plate instanceof PseudoProductPlate)
				return true;
		}
		return false;
	}

	public void selectGivenPlate(String plateLotNo) {
		int plateRowNo = -1;
		List<ProductPlate> productPlateList = pageModel.getAllProductPlatesAndRegPlates();
		ProductPlate productPlate = null;
		for (int i=0; i<productPlateList.size(); i++)
		{
			productPlate = productPlateList.get(i);
			if (productPlate.getLotNo().equals(plateLotNo))
			{
				plateRowNo = i;
				break;
			}
		}
		if (!productPlate.isSelect()) {
			controllerUtil.setSelectValue(plateRowNo);
		}
		summaryTableView.setRowSelectionInterval(plateRowNo - 1, plateRowNo);
		summaryTableView.scrollRectToVisible(new Rectangle(10 * 10,summaryTableView.getRowHeight()*(plateRowNo),5,summaryTableView.getRowHeight()));		
		
/*		productTableTabbedPane.setSelectedIndex(step);
		PCeNBatchInfoTableView tempTtableView = null;
		if (lastStep.getStepNumber() == step)
			tempTtableView = lastStepProductTableView;
			//lastStepProductTableView.scrollRectToVisible(new Rectangle(10 * 10,lastStepProductTableView.getRowHeight()*(batchRowNo),5,lastStepProductTableView.getRowHeight()));
		else
		{
			JPanel productPanel = (JPanel)productViews.get("P" + step);
			JScrollPane scrollPane = ((JScrollPane) productPanel.getComponent(1));
			tempTtableView = (PCeNBatchInfoTableView) scrollPane.getViewport().getView();
		}
		tempTtableView.scrollRectToVisible(new Rectangle(10 * 10,tempTtableView.getRowHeight()*(batchRowNo),5,tempTtableView.getRowHeight()));
		
        if (batchRowNo > 0)
        	tempTtableView.setRowSelectionInterval(batchRowNo - 1, batchRowNo);
	}
*/
	}

	public void setSelectedPlateIndex(int i) {
		controllerUtil.setSelectValue(0);
	}

	public void refreshPageModel(NotebookPageModel pageModel2) {
		if (pageModel != pageModel2)
			this.pageModel = pageModel2; 
		refresh();
	}

	private void refresh() {
		//Table rows get disabled based on pageModel. Do not have to do anything here.		
	}
}
