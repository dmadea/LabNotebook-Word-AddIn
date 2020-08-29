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
package com.chemistry.enotebook.client.gui.page.regis_submis;

/**
 * Class to manage and contain the summary view of the plates in the registration and 
 * submission tab.
 */

import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.*;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.PseudoProductPlate;
import com.chemistry.enotebook.utils.ProductBatchFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PCeNRegistration_SummaryViewContainer extends JPanel implements PlatesCreatedEventListener, CompoundCreatedEventListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7203753120853918391L;

	public static final Log log = LogFactory.getLog(PCeNRegistration_SummaryViewContainer.class);
	
	private PCeNRegistration_SummaryTableModel tableModel;
	private NotebookPageModel pageModel;
	private PCeNRegistration_SummaryViewControllerUtility controllerUtil;
	private PCeNRegistration_SummaryTableView summaryTableView;
	private ProductBatchFilter productBatchFilter = null;  
	private ProductBatchPlateCreatedEvent event;

	/**
	 * Create the summary view panel and its components.
	 * @param pageModel the NotebookPageModel
	 */
	public PCeNRegistration_SummaryViewContainer(NotebookPageModel pageModel) {
		this.setLayout(new BorderLayout());
		this.pageModel = pageModel;
		this.productBatchFilter = new ProductBatchFilter(pageModel);
		controllerUtil = new PCeNRegistration_SummaryViewControllerUtility(pageModel);
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();			
		controllerUtil.replacePseudoProductPlate(pseudoPlate);
		tableModel = new PCeNRegistration_SummaryTableModel(controllerUtil);
		summaryTableView = new PCeNRegistration_SummaryTableView(tableModel, 20, controllerUtil);
		JScrollPane sPane = new JScrollPane(summaryTableView);
		summaryTableView.setPreferredScrollableViewportSize(new Dimension(CeNConstants.TABLE_VIEW_WIDTH,
				CeNConstants.TABLE_VIEW_ROW_HIGHT * 3));
		summaryTableView.hideViewColumn();
		this.add(sPane, BorderLayout.CENTER);
	}
	
	public PCeNRegistration_SummaryTableView getSummaryTableview() {
		return this.summaryTableView;
	}

	/**
	 * Handle the new product plates created event.  The pseudo plates have to be 
	 * updated as well as the product plates.
	 * 
	 * @param event contains the list of new plates
	 */
	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event) {
		long startTime = System.currentTimeMillis();
		List plates = event.getPlates();
		PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView
				.getModel();
		PCeNRegistration_SummaryViewControllerUtility summaryConnector = (PCeNRegistration_SummaryViewControllerUtility) summaryModel
				.getConnector();
		// vb 7/25 refresh the pseudo plates (in this case the non-plated batches)
		//NotebookPageGuiInterface mNotebookPageGuiInterface = (NotebookPageGuiInterface)MasterController.getGUIComponent().getActiveDesktopWindow();
		PseudoProductPlate pseudoPlate = pageModel.getGuiPseudoProductPlate();
		//ArrayList pseudoPlates = new ArrayList();
		//pseudoPlates.add(pseudoPlate);
		summaryConnector.replacePseudoProductPlate(pseudoPlate);
		summaryConnector.addProductPlates(plates);
		summaryTableView.setRowHeight(20);
		this.event = event;
		long endTime = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("newProductPlatesCreated execution time: " + (endTime - startTime) + " ms");
		}
	}

	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event) {
		controllerUtil.removeProductPlates(event.getPlates());
		tableModel.fireTableDataChanged();		
	}

	public void newMonomerPlatesCreated(MonomerBatchPlateCreatedEvent event) {
	}

	public void monomerPlatesRemoved(MonomerBatchPlateCreatedEvent event) {
	}

	public void newRegisteredPlatesCreated(RegisteredPlateCreatedEvent event) {

	}

	public void registeredPlatesRemoved(RegisteredPlateCreatedEvent event) {

	}

	/**
	 * Sets the compound detail view as a listener when the plate selection is changed so 
	 * it can update the plates in its view.
	 * 
	 * @param plateSelectionChangedListener  the compound detail view
	 */
	public void setPlateSelectionChangedListener(PlateSelectionChangedListener plateSelectionChangedListener) {
		PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
		PCeNRegistration_SummaryViewControllerUtility summaryConnector = (PCeNRegistration_SummaryViewControllerUtility) summaryModel
				.getConnector();
		summaryConnector.addPlateSelectionChangedListener(plateSelectionChangedListener);
	}

	public void compoundRemoved(CompoundCreationEvent event) {
		PCeNTableModel summaryModel = (PCeNTableModel) summaryTableView.getModel();
		PCeNRegistration_SummaryViewControllerUtility summaryConnector = (PCeNRegistration_SummaryViewControllerUtility) summaryModel
				.getConnector();
		summaryConnector.removeBatchFromPseudoPlate((ProductBatchModel)event.getBatch());
		summaryModel.fireTableDataChanged();
		summaryTableView.setRowHeight(30);	
	}

	public void compoundUpdated(CompoundCreationEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void newCompoundCreated(CompoundCreationEvent event) {
/*		ParallelCeNTableModel summaryModel = (ParallelCeNTableModel) summaryTableView.getModel();
		PCeNRegistration_SummaryViewControllerUtility summaryConnector = (PCeNRegistration_SummaryViewControllerUtility) summaryModel
				.getConnector();
		summaryConnector.addBatchToPseudoPlate((ProductBatchModel)event.getBatch());
		summaryModel.fireTableDataChanged();
		summaryTableView.setRowHeight(40);*/		
	}
}
